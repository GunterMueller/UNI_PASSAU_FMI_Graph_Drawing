/******************************************************************************/
/*											      	*/
/*				     FILE: lpr_plr_system.c				   	*/
/*											      	*/
/*	Hier sind die Funktionen f"ur das Erstellen der PLRS-Graphen 	     	*/
/* 	zu einem Produktionenlayout zusammengefasst. Die Hauptfunktion		*/
/* 	bildet compute_plr_system am Ende des Files.					*/
/******************************************************************************/

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "user_header.h"

#include "lpr_nnode.h"
#include "lpr_eedge.h"
#include "lpr_ggraph.h"
#include "lpr_lrp.h"
#include "lp_edgeline.h"



/******************************************************************************/
/*											      	*/
/* 	Zun"achst einige Funktionen zur Manipulation von plrs_nodes			*/
/*													*/
/******************************************************************************/

/*******************************************************************************
Function	: create_plrs_node
Input		: void 
Output	: plrs_Node
Description : Reserviert Speicher f"ur ein Objekt vom Typ plrs_node setzt die
		  Eintr"age und gibt einen Zeiger auf das Objekt zur"uck.
*******************************************************************************/
plrs_Node	create_plrs_node(void)
{
	plrs_Node	new = (plrs_Node) mymalloc( sizeof( struct plrs_node ) );

	new->value 		= -1;	
	new->h_value 	= -1;	
	new->side		= -1;
	new->is_x		= -1;
	new->in_edges 	= NULL;
	new->out_edges 	= NULL;
	memset(new->info,0,100);
	new->iso 		= NULL;
	new->pre 		= new;	
	new->suc 		= new;
	new->px		= 0;
	new->py		= 0;
	new->new_node 	= 0;
	new->visited	= 0;
	new->graphed_node = NULL;

	return( new );
}


/*******************************************************************************
Function	: add_plrs_node
Input		: plrs_Node	list, element
Output	: plrs_Node
Description : F"ugt an eine evtl. bestehende Liste von plrs_nodes einen neuen
		  an und gibt die erweiterte Liste wieder zur"uck. Funktioniert auch
		  f"ur 2 Listen.
*******************************************************************************/
plrs_Node	add_plrs_node	(plrs_Node list, plrs_Node element)
{
	plrs_Node last;

	if ( list == NULL )	return( element );
	else
	{
		last = list->pre;

		last->suc = element;
		list->pre = element->pre;
		element->pre->suc = list;
		element->pre = last;

		return( list );
	}
}








/******************************************************************************/
/*											      	*/
/* 	Jetzt einige Funktionen zur Manipulation von plrs_edges			*/
/*													*/
/******************************************************************************/

/*******************************************************************************
Function	: create_plrs_edge
Input		: plrs_Node	source, target ,int length
 
Output	: plrs_Edge
Description : Reserviert Speicher f"ur ein Objekt vom Typ plrs_edge setzt die
		  Eintr"age und gibt einen Zeiger auf das Objekt zur"uck.
*******************************************************************************/
plrs_Edge	create_plrs_edge(plrs_Node source, int length, plrs_Node target)
{
	plrs_Edge new = (plrs_Edge) mymalloc( sizeof( struct plrs_edge ) );

	new->source 	= source;
	new->target 	= target;
	new->length 	= length;
	new->source_pre 	= new;
	new->source_suc 	= new;
	new->target_pre	= new;	
	new->target_suc 	= new;
	new->iso 		= NULL;
	new->visited 	= 0;

	return( new );
}

/*******************************************************************************
Function	: add_plrs_edge
Input		: plrs_Edge element
Output	: void
Description : F"ugt die Kante element in die in_ bzw. out_edges seiner Quelle
		  bzw. seines Ziels ein.
*******************************************************************************/
void		add_plrs_edge	(plrs_Edge element)
{
	plrs_Edge	first;

	if ( element->source->is_x != element->target->is_x )
		printf("\r\n Fehler ");
	if ( element->source->out_edges == NULL )			/* Bearbeite die out_edges von Source	*/
	{									/* Falls leer, dann mache Element zur	*/
		element->source->out_edges = element;		/* neuen Liste.					*/
	}
	else
	{
		first = element->source->out_edges;			/* sonst h"ange element an die bestehen-	*/
		element->source_pre = first->source_pre;		/* de Liste an.					*/
		element->source_suc = first;
		first->source_pre->source_suc = element;
		first->source_pre = element;
	}

	if ( element->target->in_edges == NULL )			/* Ebenso f"ur die in_edges von target	*/ 
	{
		element->target->in_edges = element;
	}
	else
	{
		first = element->target->in_edges;
		element->target_pre = first->target_pre;
		element->target_suc = first;
		first->target_pre->target_suc = element;
		first->target_pre = element;
	}
}

/**************************************************************************
Function	: plrs_edge_exists
Input		: plrs_Node node1, int length, plrs_Node node2
Output	: int
Description : Gibt 1 zur"uck, falls eine Kante mit entsprechender L"ange 
		  zwischen node1 und node2 existiert, sonst 0
**************************************************************************/
int plrs_edge_exists(plrs_Node node1, int length, plrs_Node node2)
{
	plrs_Edge 	cur_plrs_edge;
	int 		found = 0;

	FOR_PLRS_EDGE_SOURCE( node1, cur_plrs_edge )					/* Vergleiche alle ausl. Kanten von node1 mit den Parametern. */		
		if (( cur_plrs_edge->target == node2 ) && ( cur_plrs_edge->length == length ))
		{
			found = 1;
			break;
		}
	END_FOR_PLRS_EDGE_SOURCE( node1, cur_plrs_edge );

	return found;
}


/**************************************************************************
Function	: inherit_all_plrs_edges
Input		: plrs_Node node1, plrs_Node node2
Output	: void 
Description : Vererbt alle ein- und ausl. Kanten von node1 an node2 und 
		  umgekehrt.
**************************************************************************/
void inherit_all_plrs_edges(plrs_Node node1, plrs_Node node2)
{
	plrs_Edge cur_plrs_edge, plrs_edge;

	FOR_PLRS_EDGE_TARGET(node1, cur_plrs_edge)
		if ( plrs_edge_exists( cur_plrs_edge->source, cur_plrs_edge->length, node2 )  == 0 )
		{ 
			plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, node2 );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_TARGET(node1, cur_plrs_edge);
							
	FOR_PLRS_EDGE_SOURCE(node1, cur_plrs_edge)  
		if ( plrs_edge_exists( node2, cur_plrs_edge->length, cur_plrs_edge->target )  == 0 )
		{ 
			plrs_edge = create_plrs_edge( node2, cur_plrs_edge->length, cur_plrs_edge->target );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_SOURCE(node1, cur_plrs_edge);
	
	FOR_PLRS_EDGE_TARGET(node2, cur_plrs_edge) 
		if ( plrs_edge_exists( cur_plrs_edge->source, cur_plrs_edge->length, node1 )  == 0 )
		{ 
			plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, node1 );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_TARGET(node2, cur_plrs_edge);
							
	FOR_PLRS_EDGE_SOURCE(node2, cur_plrs_edge) 
		if ( plrs_edge_exists( node1, cur_plrs_edge->length, cur_plrs_edge->target )  == 0 )
		{ 
			plrs_edge = create_plrs_edge( node1, cur_plrs_edge->length, cur_plrs_edge->target );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_SOURCE(node2, cur_plrs_edge);
}

/************************************************************************
Function	: inherit_all_target_edges
Input		: plrs_Node node1, node2
Output	: void 
Description : Vererbt alle einlaufenden Kanten von node1 an node2 und um-
		  gekehrt.
************************************************************************/
void inherit_all_target_edges(plrs_Node node1, plrs_Node node2)
{
	plrs_Edge cur_plrs_edge, plrs_edge;

	FOR_PLRS_EDGE_TARGET(node1, cur_plrs_edge)
		if ( plrs_edge_exists( cur_plrs_edge->source, cur_plrs_edge->length, node2 )  == 0 )
		{
 			plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, node2 );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_TARGET(node1, cur_plrs_edge);
	FOR_PLRS_EDGE_TARGET(node2, cur_plrs_edge) 
		if ( plrs_edge_exists( cur_plrs_edge->source, cur_plrs_edge->length, node1 )  == 0 )
		{ 
			plrs_edge = create_plrs_edge( cur_plrs_edge->source, cur_plrs_edge->length, node1 );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_TARGET(node2, cur_plrs_edge);
}


/************************************************************************
Function	: inherit_all_source_edges
Input		: plrs_Node node1, node2
Output	: void 
Description : Vererbt alle auslaufenden Kanten von node1 an node2 und um-
		  gekehrt.
************************************************************************/
void inherit_all_source_edges(plrs_Node node1, plrs_Node node2)
{
	plrs_Edge cur_plrs_edge, plrs_edge;

	FOR_PLRS_EDGE_SOURCE(node1, cur_plrs_edge)  
		if ( plrs_edge_exists( node2, cur_plrs_edge->length, cur_plrs_edge->target )  == 0 )
		{ 
			plrs_edge = create_plrs_edge( node2, cur_plrs_edge->length, cur_plrs_edge->target );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_SOURCE(node1, cur_plrs_edge);
	FOR_PLRS_EDGE_SOURCE(node2, cur_plrs_edge) 
		if ( plrs_edge_exists( node1, cur_plrs_edge->length, cur_plrs_edge->target )  == 0 )
		{ 
			plrs_edge = create_plrs_edge( node1, cur_plrs_edge->length, cur_plrs_edge->target );
			add_plrs_edge( plrs_edge );
		}
	END_FOR_PLRS_EDGE_SOURCE(node2, cur_plrs_edge);
}

/**************************************************************************
Function	: free_plrs_edges_of_one_node
Input		: plrs_Node plrs_node
Output	: void 
Description : Gibt die in- und out_edges eines Knoten frei. Dabei wird eine
		  zu l"oschende Kante auch aus der entsprechenden Kantenliste 
		  des Knoten, mit dem plrs_node "uber diese Kante verbunden ist,
		  gel"oscht.
**************************************************************************/	
void free_plrs_edges_of_one_node(plrs_Node plrs_node)
{
	plrs_Edge cur_edge, to_delete;

	if ( plrs_node->out_edges != NULL )
		plrs_node->out_edges->source_pre->source_suc = NULL;		/* Sichere Terminierung der Liste der ausl. Kanten	*/
	cur_edge = plrs_node->out_edges;						/* Durchlaufe alle ausl. Kanten.				*/
	while ( cur_edge != NULL )
	{
		if ( cur_edge == cur_edge->target_suc ) cur_edge->target->in_edges = NULL;
		else
		{
			if ( cur_edge == cur_edge->target->in_edges ) cur_edge->target->in_edges = cur_edge->target_suc;
			cur_edge->target_pre->target_suc = cur_edge->target_suc;	/* L"osche sie aus zugeh. Listen einl. Kanten	*/
			cur_edge->target_suc->target_pre = cur_edge->target_pre;
		}
		to_delete = cur_edge;							/* Gib den reservierten Speicher wieder frei.		*/
		cur_edge  = cur_edge->source_suc;					
		free( to_delete );
	}

	if ( plrs_node->in_edges != NULL )
		plrs_node->in_edges->target_pre->target_suc = NULL;		/* Ebenso f"ur die einlaufenden Kanten.			*/
	cur_edge = plrs_node->in_edges;
	while ( cur_edge != NULL )
	{
		if (cur_edge->source_suc == cur_edge) cur_edge->source->out_edges = NULL;
		else
		{
			if ( cur_edge == cur_edge->source->out_edges ) cur_edge->source->out_edges = cur_edge->source_suc;
			cur_edge->source_pre->source_suc = cur_edge->source_suc;
			cur_edge->source_suc->source_pre = cur_edge->source_pre;
		}
		to_delete = cur_edge;
		cur_edge  = cur_edge->target_suc;
		free( to_delete );
	}
	plrs_node->in_edges = NULL;
	plrs_node->out_edges = NULL;
}	
		


/*******************************************************************************
Function	: remove_plrs_node_from_graph
Input		: plrs_Node	node, plr_System system
Output	: void
Description : L"oscht einen Knoten aus dem entsprechenden plrs-Graphen und gibt
		  dabei auch alle zugeh"origen Kanten frei.
*******************************************************************************/
void remove_plrs_node_from_graph (plrs_Node node, plr_System system)
{
	if ( system->x_graph == node )					/* Falls n"otig korrigiere die Anfangs-	*/
	{										/* zeiger in den plrs-Graphen.		*/
		system->x_graph = node->suc;
		if ( node == node->suc ) system->x_graph = NULL;
	}
	if ( system->y_graph == node )
	{
		system->y_graph = node->suc;
		if ( node == node->suc ) system->y_graph = NULL;
	}

	free_plrs_edges_of_one_node( node );				/* L"osche alle inzidenten Kanten des	*/
	node->pre->suc = node->suc;						/* Knotens, nimm ihn aus der Knotenliste	*/
	node->suc->pre = node->pre;						/* und gib seinen Speicher frei.		*/
	free( node );	
}


void 	print_in_edges(plrs_Edge list)
{
	plrs_Edge	cur_edge;

	printf("list check \n");
	if ( list != NULL )
	{
		cur_edge = list;
		do
		{
			printf("%p ",(void *)cur_edge);
			cur_edge = cur_edge->target_suc;
		}
		while( cur_edge != list );
	}
	printf("\n");
	if ( list != NULL )
	{
		cur_edge = list->target_pre;
		do
		{
			printf("%p ",(void *)cur_edge);
			cur_edge = cur_edge->target_pre;
		}
		while( cur_edge != list->target_pre );
	}
			
	printf("\n");
}
		
/**************************************************************************
Function	: free_plrs_in_edges
Input		: plrs_Edge list
Output	: void 
Description : Gibt die Kantenliste list frei. Achtung! : Um alle Kanten
		  in einem plrs_Graphen zu l"oschen reicht es, die Knoten zu
		  durchlaufen und deren In_edges zu l"oschen, da diese immer
		  in der Rolle einer in_edge und einer out_edge auftauchen.
		  Dazu ist diese Funktion geeignet. Sie sollte aber nie zum
		  L"oschen der Kanten eines einzelnen Knoten verwendet werden,
		  da die Kanten aus den out_edges der anderen Knoten nicht ge-
		  l"oscht werden.
**************************************************************************/	
void free_plrs_in_edges(plrs_Edge list)
{
	plrs_Edge	to_delete, cur_edge;

	if (list != NULL)						/* wie "ublich		*/
	{
/*		print_in_edges( list ); */
		list->target_pre->target_suc = NULL;
		cur_edge = list;
		while ( cur_edge != NULL )
		{
			to_delete = cur_edge;
			cur_edge = cur_edge->target_suc;
			free( to_delete );
		}
	}
}

/**************************************************************************
Function	: free_plrs_nodes_with_edges
Input		: plrs_Node list
Output	: void 
Description : Wird dazu benutzt einen vollst"andigen plrs-Graphen zu l"o-
		  schen. Deshalb reicht es auch, nur die einlaufenden Kanten
		  eines Knoten zu l"oschen. 
**************************************************************************/	
void free_plrs_nodes_with_edges(plrs_Node list)
{
	plrs_Node	to_delete, cur_node;

	if (list != NULL)
	{
		list->pre->suc = NULL; 					/* Sichere Terminierung	*/
		cur_node = list;
		while( cur_node != NULL)
		{
			to_delete = cur_node;
			cur_node = cur_node->suc;

			free_plrs_in_edges(to_delete->in_edges);	/* L"osche die Kanten	*/
			free( to_delete );				/* und den Knoten.	*/
		}
	}
}




/******************************************************************************/
/*											      	*/
/* 	Jetzt noch einige Funktionen zur Manipulation von Dependency_listen	*/
/*	und plrs_Nodelisten	                                                											*/
/*											      	*/
/******************************************************************************/

/*******************************************************************************
Function	: create_dep_list_with_node
Input		: plrs_Node plrs_node
Output	: Dependency_list
Description : Reserviert Speicher f"ur ein Objekt vom Typ dependency_list, setzt
		  die Eintr"age und gibt einen Zeiger auf das Objekt zur"uck. 
*******************************************************************************/
Dependency_list create_dep_list_with_node(plrs_Node plrs_node)
{
	Dependency_list new = (Dependency_list) mymalloc( sizeof( struct dependency_list ) );

	new->node 	= plrs_node;
	new->pre 	= new;	
	new->suc 	= new;

	return( new );
}	


/*******************************************************************************
Function	: add_dep_to_dep_list
Input		: Dependency_list	list, element
Output	: Dependency_list
Description : F"ugt an eine evtl. bestehende Liste vom Typ Dependency_list, einen neuen
		  Eintrag an und gibt die erweiterte Liste wieder zur"uck. Diese Funktion
		  kann auch zum Verketten von 2 Listen benutzt werden.
*******************************************************************************/
Dependency_list add_dep_to_dep_list(Dependency_list list, Dependency_list element)
{
	Dependency_list last;

	if ( element == NULL ) return ( list );
	if ( list == NULL )	return( element );
	else
	{
		last = list->pre;

		last->suc = element;
		list->pre = element->pre;
		element->pre->suc = list;
		element->pre = last;

		return( list );
	}
}

/**************************************************************************************************
Function	: copy_dep_list
Input		: Dependency_list
Output	: Dependency_list
Description	: Erzeugt eine Kopie und gibt einen Zeiger darauf zur"uck. Falls das Original leer
		  ist, wird ebenfalls NULL zur"uckgegeben.
**************************************************************************************************/
Dependency_list copy_dep_list(Dependency_list original)
{
	Dependency_list cur_dep_list, copy = NULL, new_dep_list;

	FOR_DEP_LIST(original, cur_dep_list)
		new_dep_list = create_dep_list_with_node(cur_dep_list->node);
		copy	 	 = add_dep_to_dep_list(copy, new_dep_list);
	END_FOR_DEP_LIST(original, cur_dep_list)
	return copy;
}

/**************************************************************************
Function	: free_dep_list
Input		: Dependency_list list
Output	: void 
Description : Gibt eine Liste vom Typ Dependency_list ohne die Knoten frei
**************************************************************************/	
void free_dep_list(Dependency_list list)
{
	Dependency_list	to_delete;

	if (list != NULL)
	{
		list->pre->suc = NULL; /*Sichere Terminierung*/
		while( list )
		{
			to_delete = list;
			list = list->suc;

			free( to_delete );
		}
	}
}

/**************************************************************************
Function	: free_last_of_dep_list
Input		: Dependency_list list
Output	: Dependency_list 
Description : Gibt das letzte Element einer Liste vom Typ Dependency_list
		  ohne den Knoten frei und gibt die resultierende Liste zur"uck.
**************************************************************************/	
Dependency_list free_last_of_dep_list(Dependency_list list)
{
	Dependency_list last = list->pre;		/* Merke das letzte Element der Liste	*/

	if ( last == list )				/* Falls Liste einelementig, l"osche sie	*/
	{							/* und gib NULL zur"uck.			*/
		free( list );
		return NULL;
	}
	else							/* sonst setzte Zeiger neu und gib Liste 	*/
	{							/* zur"uck.						*/
		last->pre->suc = last->suc;
		list->pre	   = last->pre;
		free(last);
		return list;
	}
}



/**************************************************************************
Function	: free_first_of_dep_list
Input		: Dependency_list list
Output	: Dependency_list 
Description : Gibt das erste Element einer Liste vom Typ Dependency_list
		  ohne den Knoten frei und gibt die resultierende Liste zur"uck.
**************************************************************************/	
Dependency_list free_first_of_dep_list(Dependency_list list)
{
	Dependency_list result;	

	if ( list->suc == list )				/* Falls Liste einelementig, l"osche sie	*/
	{								/* und gib NULL zur"uck.			*/
		free( list );
		return NULL;
	}
	else								/* sonst setzte Zeiger neu und gib Liste 	*/
	{								/* zur"uck.						*/
		result 		= list->suc;
		list->pre->suc 	= list->suc;
		list->suc->pre 	= list->pre;
		free( list );
		return result;
	}
}





/******************************************************************************/
/*													*/
/*	Einige Funktionen zum Umgang mit plrs_nodelist's. Diese sind im		*/
/* 	"ubrigen physikalisch identisch mit der Struktur dependency-list.		*/
/*	Sie erf"ullen jedoch andere Aufgaben. Deshalb zwecks "Ubersicht-		*/
/*	lichkeit, das gleiche noch einmal.							*/
/*													*/
/******************************************************************************/

/*******************************************************************************
Function	: create_plrs_nodelist_with_node
Input		: plrs_Node plrs_node
Output	: plrs_Nodelist
Description : Reserviert Speicher f"ur ein Objekt vom Typ plrs_Nodelist, setzt
		  die Eintr"age und gibt einen Zeiger auf das Objekt zur"uck. 
*******************************************************************************/
plrs_Nodelist create_plrs_nodelist_with_node(plrs_Node plrs_node)
{
	plrs_Nodelist new = (plrs_Nodelist) mymalloc( sizeof( struct plrs_nodelist ) );

	new->node 	= plrs_node;
	new->pre 	= new;	
	new->suc 	= new;

	return( new );
}	


/*******************************************************************************
Function	: add_plrs_nodelist_to_plrs_nodelist
Input		: plrs_Nodelist	list, element
Output	: plrs_Nodelist
Description : F"ugt an eine evtl. bestehende Liste vom Typ plrs_Nodelist, einen neuen
		  Eintrag an und gibt die erweiterte Liste wieder zur"uck. Funktioniert
		  auch f"ur 2 Listen.
*******************************************************************************/
plrs_Nodelist add_plrs_nodelist_to_plrs_nodelist(plrs_Nodelist list, plrs_Nodelist element)
{
	plrs_Nodelist last;

	if ( element == NULL ) return list;
	if ( list == NULL )	return( element );
	else
	{
		last = list->pre;

		last->suc = element;
		list->pre = element->pre;
		element->pre->suc = list;
		element->pre = last;

		return( list );
	}
}



/**************************************************************************
Function	: free_plrs_nodelist
Input		: Dependency_list list
Output	: void 
Description : Gibt eine Liste vom Typ Dependency_list ohne die Knoten frei
**************************************************************************/	
void free_plrs_nodelist(plrs_Nodelist list)
{
	plrs_Nodelist	to_delete;

	if (list != NULL)
	{
		list->pre->suc = NULL; /*Sichere Terminierung*/
		while( list )
		{
			to_delete = list;
			list = list->suc;

			free( to_delete );
		}
	}
}




/****************************************************************************
Function	: free_plr_system
Input		: lpr_Node node
Output	: void
Description : Gibt alles, was mit dem PLR-System einer Produktion zu tun hat
 		  wieder frei. Ausgenommen sind die x- und y-Graphen. 
****************************************************************************/
void free_plr_system(lpr_Node node)
{
	lpr_Graph 		prod = node->applied_production;
	lpr_Nodelist	cur_nodelist;
	lpr_Edgelist 	cur_edgelist, in_cons, out_cons, cons;


	node->bleft 	= NULL;										/* Setze die b-Zeiger des Knotens, der	*/
	node->bright 	= NULL;										/* abgeleitet wurde zur"uck.			*/
	node->bup 		= NULL;
	node->bdown 	= NULL;

	free( prod->track_segments[0]);
	prod->track_segments[0] = NULL;
	free( prod->track_segments[1]);
	prod->track_segments[1] = NULL;
	free( prod->track_segments[2]);
	prod->track_segments[2] = NULL;
	free( prod->track_segments[3]);
	prod->track_segments[3] = NULL;

	FOR_LPR_NODELIST(prod->nodes, cur_nodelist)							/* L"osche alle entstandenen S-Listen	*/
		FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )		/* und die Verweise von Knoten der		*/
			free_dep_list(cur_edgelist->edge->S_list);					/* rechten Seite in die PLRS-Graphen	*/
			cur_edgelist->edge->S_list = NULL;
			free_dep_list(cur_edgelist->edge->S2_list);					/* rechten Seite in die PLRS-Graphen	*/
			cur_edgelist->edge->S2_list = NULL;	
		END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );
		cur_nodelist->node->left 	= NULL;
		cur_nodelist->node->right 	= NULL;
		cur_nodelist->node->up 		= NULL;
		cur_nodelist->node->down 	= NULL;
	END_FOR_LPR_NODELIST(prod->nodes, cur_nodelist);


	in_cons 	= copy_lpr_edgelist( prod->IN_embeddings );					/* L"osche die S-Listen der Einbettungs-	*/
	out_cons	= copy_lpr_edgelist( prod->OUT_embeddings );					/* regeln.						*/
	cons		= add_edgelist_to_lpr_edgelist( in_cons, out_cons );
	FOR_LPR_EDGELIST(cons, cur_edgelist)
		free_dep_list(cur_edgelist->edge->S_list);
		cur_edgelist->edge->S_list = NULL;
		free_dep_list(cur_edgelist->edge->S2_list);
		cur_edgelist->edge->S2_list = NULL;
	END_FOR_LPR_EDGELIST(cons, cur_edgelist);
	free_lpr_edgelist( cons );

	FOR_LPR_NODELIST ( prod->nodes, cur_nodelist)
		if ( cur_nodelist->node->applied_production != NULL )
			free_plr_system(cur_nodelist->node);
	END_FOR_LPR_NODELIST ( prod->nodes, cur_nodelist);
}





/******************************************************************************************/
/*															*/
/*			Schliesslich noch eine Funktion zum Erzeugen eines PLRS			*/
/*															*/
/******************************************************************************************/

/*******************************************************************************************
Function	: create_plr_system
Input		: void
Output	: plr_System
Description : Erzeugt eine plr-System und initialisiert alle Zeiger mit NULL
*******************************************************************************************/					
plr_System create_plr_system(void)
{
	plr_System new = (plr_System) mymalloc( sizeof( struct plr_system ) );

	new->x_graph = NULL;
	new->y_graph = NULL;

	return( new );
}

int no_node_between(plrs_Node node1, plrs_Node node2)
{
	plrs_Node 	cur_node;
	
	FOR_PLRS_NODES( node1, cur_node )
		if ( ( cur_node != node1 ) && ( cur_node != node2 ) )
			if ( ( cur_node->value > node1->value ) && ( cur_node->value < node2->value ) )
				return 0;
	END_FOR_PLRS_NODES( node1, cur_node );
	return 1;
}

/******************************************************************************************/
/*															*/
/* 					Die Hauptfunktion	dieses Abschnitts					*/
/*															*/
/******************************************************************************************/

/******************************************************************************************
Function	: compute_plr_system
Input		: lpr_Node node, int lpr_grid
Output	: plr_System
Description : Wie schon gesagt, werden hier die PLRS-Graphen und die S-Listen f"ur eine
		  Produktion erzeugt. 
******************************************************************************************/
plr_System compute_plr_system(lpr_Node node, int lpr_grid)
{
	plrs_Node		x_nodes = NULL, y_nodes = NULL;	
	plrs_Node 	 	plrs_node, cur_plrs_node, cur_plrs_node1, cur_plrs_node2;
	plrs_Edge		plrs_edge;
	Node			node_layout;
	lpr_Nodelist	cur_nodelist;
	lpr_Edgelist	cur_edgelist, in_cons, out_cons, cons;
	int			switcher, dir;
	lpr_Iso_edge	iso_edge;
	lp_Edgeline		cur_lp_line, first, last_;
	Dependency_list	new_dep_entry;
	lpr_Graph 		prod = node->applied_production;
	plr_System		prod_system;
	

	FOR_LPR_NODELIST( prod->nodes, cur_nodelist )						/* Erzeuge zun"achst f"ur jede Seite eines Knotens 	*/
		node_layout = get_optimal_node_of_lpr_node( prod, cur_nodelist->node);		/* einen Knoten im plrs-Graphen.				*/

		plrs_node = create_plrs_node();								/* Als Beispiel die linke Seite				*/
		plrs_node->side = L_side;									/* Trage die Seite des Knotens ein.				*/
		plrs_node->is_x = 1;										/* Vermerke, dass dies ein X-Knoten ist.			*/
		strcpy(plrs_node->info, "N:0" );
		plrs_node->value = node_layout->x - ( node_layout->box.r_width / 2 );		/* Berechne die Koord.(Graphed-Koord. sind zentriert)	*/
		x_nodes = add_plrs_node( x_nodes, plrs_node );						/* In den plrs-Graphen einh"angen.				*/
		cur_nodelist->node->left = plrs_node;							/* Stelle die Verzeigerung vom lpr-Graphen zum plrs-	*/
															/* Graphen her.							*/
		plrs_node = create_plrs_node();								/* Analog f"ur alle anderen Seiten.				*/
		plrs_node->side = R_side;
		plrs_node->is_x = 1;				
		plrs_node->value = node_layout->x + ( node_layout->box.r_width / 2 );				
		strcpy(plrs_node->info, "N:2" );
		x_nodes = add_plrs_node( x_nodes, plrs_node );
		cur_nodelist->node->right = plrs_node;


		plrs_node = create_plrs_node();				
		plrs_node->side = D_side;				
		plrs_node->is_x = 0;
		plrs_node->value = node_layout->y - ( node_layout->box.r_height / 2 );				
		strcpy(plrs_node->info, "N:3" );
		y_nodes = add_plrs_node( y_nodes, plrs_node );
		cur_nodelist->node->down = plrs_node;

		plrs_node = create_plrs_node();
		plrs_node->side = U_side;
		plrs_node->is_x = 0;				
		plrs_node->value = node_layout->y + ( node_layout->box.r_height / 2 );				
		strcpy(plrs_node->info, "N:1" );
		y_nodes = add_plrs_node( y_nodes, plrs_node );
		cur_nodelist->node->up = plrs_node;
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );


	FOR_LPR_NODELIST( prod->nodes, cur_nodelist )								/* Nun wird ein Knoten f"ur die Anfangs- und Endpunk-	*/										
		FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist )			/* te und alle Segmente einer RHS-Edge erzeugt.		*/
			if ( (  cur_edgelist->edge->EH != NULL) && ( cur_edgelist->edge->edge_type == lpr_RHS_EDGE ) )
			{

				iso_edge = get_optimal_edge_of_lpr_edge( prod, cur_edgelist->edge );
				dir 	 = first_dir(iso_edge->edge);							/* Bestimme die Richtung des ersten Segments		*/
				first  = iso_edge->edge->lp_edge.lp_line;							/* und merke die erste und letzte lp-edgeline		*/
				last_   = iso_edge->edge->lp_edge.lp_line->pre;

				if ((dir == U_dir) || ( dir == D_dir ))						/* Entscheide in Abh. von dir ob der erste Knoten im	*/
				{												/* X- oder Y-Graphen landet.					*/
					cur_edgelist->edge->start_value = first->x;
					switcher = 1;									/* Initialisiere switcher mit 1, damit das erste	*/
				}												/* Segment im x-Graphen landet.				*/
				else												/* Im andern Fall analog.					*/
				{
					cur_edgelist->edge->start_value = first->y;
					switcher = 0;
				}


				for_lp_edgeline(first, cur_lp_line)							/* Erzeuge jetzt die Segment-Knoten				*/
				{
					if (cur_lp_line == last_) break;						

					plrs_node = create_plrs_node();						/* Knoten erzeugen und wieder in die Dependency-Liste	*/
					strcpy(plrs_node->info, "E:S");
					new_dep_entry = create_dep_list_with_node( plrs_node ); 		/* eintragen.							*/
					cur_edgelist->edge->S_list = add_dep_to_dep_list( cur_edgelist->edge->S_list, new_dep_entry );
					if ( switcher == 0 )								/* Wohin soll der Knoten ?					*/
					{											/* falls in den Y-Graphen, dann wie zuvor.		*/
						plrs_node->is_x = 0;				
						plrs_node->value = cur_lp_line->y;				
						y_nodes = add_plrs_node( y_nodes, plrs_node );
					}
					else											/* und umgekehrt.							*/
					{
						plrs_node->is_x = 1;				
						plrs_node->value = cur_lp_line->x;				
						x_nodes = add_plrs_node( x_nodes, plrs_node );
					}
					switcher = 1 - switcher;							/* Drehe switcher um, damit das n"achste Segment im 	*/
				}												/* anderen Graphen landet.					*/
				end_for_lp_edgeline(first, cur_lp_line);

				dir 	 = last_dir(iso_edge->edge);							/* Erzeuge einen Knoten f"ur den Endpunkt der Kante	*/
				if ((dir == U_dir) || ( dir == D_dir ))								
					cur_edgelist->edge->end_value = last_->x;			
				else
					cur_edgelist->edge->end_value = last_->y;			

			}
		END_FOR_LPR_EDGELIST( cur_nodelist->node->source_edges, cur_edgelist );
	END_FOR_LPR_NODELIST( prod->nodes, cur_nodelist );



	in_cons 	= copy_lpr_edgelist( prod->IN_embeddings );					/* Das gleiche geschieht jetzt noch mit den 	*/
	out_cons	= copy_lpr_edgelist( prod->OUT_embeddings );					/* Einbettungsregeln. Verkette sie tempor"ar	*/
	cons	= add_edgelist_to_lpr_edgelist( in_cons, out_cons );					/* um eine Schleife zu sparen				*/

	FOR_LPR_EDGELIST( cons, cur_edgelist )								/* und los gehts.						*/
		if ( cur_edgelist->edge->EH != NULL )
		{
			iso_edge = get_optimal_edge_of_lpr_edge( prod, cur_edgelist->edge );
			dir 	 = first_dir(iso_edge->edge);
			first  = iso_edge->edge->lp_edge.lp_line;
			last_   = iso_edge->edge->lp_edge.lp_line->pre;


			if ( cur_edgelist->edge->edge_type == lpr_OUT_CONN_REL)				/* Achtung! Nur Out-Conn-Rels haben einen An-	*/
			{													/* fangspunkt.						*/
				if ((dir == U_dir) || ( dir == D_dir ))								
				{
					cur_edgelist->edge->start_value = first->x;
					switcher = 0;
				}
				else
				{
					cur_edgelist->edge->start_value = first->y;
					switcher = 1;
				}
				switcher = 1 - switcher;
			}
			else													/* In beiden F"allen muss aber switcher gesetzt	*/
			{													/* werden.							*/
				if ((dir == L_dir) || ( dir == R_dir ))								
					switcher = 0;
				else
					switcher = 1;
			}
	
			for_lp_edgeline(first, cur_lp_line)
			{
				if (cur_lp_line == last_) break;
				plrs_node = create_plrs_node();						
				strcpy(plrs_node->info, "C:S");
				new_dep_entry = create_dep_list_with_node( plrs_node ); 
				cur_edgelist->edge->S_list = add_dep_to_dep_list( cur_edgelist->edge->S_list, new_dep_entry );
				if ( switcher == 0 )
				{
					plrs_node->is_x = 0;				
					plrs_node->value = cur_lp_line->y;				
					y_nodes = add_plrs_node( y_nodes, plrs_node );
				}
				else
				{
					plrs_node->is_x = 1;				
					plrs_node->value = cur_lp_line->x;				
					x_nodes = add_plrs_node( x_nodes, plrs_node );
				}
				switcher = 1 -switcher;
			}
			end_for_lp_edgeline(first, cur_lp_line);

			dir = last_dir(iso_edge->edge);
			if ( cur_edgelist->edge->edge_type == lpr_IN_CONN_REL)				/* Achtung! Nur IN-CONN_RELs haben einen End-	*/
			{													/* punkt.							*/
				if ((dir == U_dir) || ( dir == D_dir ))								
					cur_edgelist->edge->end_value = last_->x;
				else
					cur_edgelist->edge->end_value = last_->y;
			}
		}	
	END_FOR_LPR_EDGELIST( cons, cur_edgelist );
	free_lpr_edgelist( cons );										/* Diese Liste wird nicht mehr gebraucht.		*/

															/* Erzeuge jetzt noch Knoten f"ur die R"ander	*/
															/* der Produktion.					*/
	node_layout = get_optimal_node_of_lpr_node( prod, node->applied_production->nodes->node)->graph->gra.gra.nce1.left_side->node;
		
	plrs_node = create_plrs_node();									/* Wie zuvor						*/
	strcpy(plrs_node->info, "B:0");
	plrs_node->side = L_side;				
	plrs_node->is_x = 1;
	plrs_node->value = node_layout->x - ( node_layout->box.r_width / 2 );			
	x_nodes = add_plrs_node( x_nodes, plrs_node );
	node->bleft = plrs_node;										/* Setze hier aber die b-Zeiger im lpr-Graphen.	*/

	plrs_node = create_plrs_node();
	strcpy(plrs_node->info, "B:2");
	plrs_node->side = R_side;
	plrs_node->is_x = 1;				
	plrs_node->value = node_layout->x + ( node_layout->box.r_width / 2 );				
	x_nodes = add_plrs_node( x_nodes, plrs_node );
	node->bright = plrs_node;


	plrs_node = create_plrs_node();				
	strcpy(plrs_node->info, "B:3");
	plrs_node->side = D_side;				
	plrs_node->is_x = 0;
	plrs_node->value = node_layout->y - ( node_layout->box.r_height / 2 );				
	y_nodes = add_plrs_node( y_nodes, plrs_node );
	node->bdown = plrs_node;

	plrs_node = create_plrs_node();
	strcpy(plrs_node->info, "B:1");
	plrs_node->side = U_side;
	plrs_node->is_x = 0;				
	plrs_node->value = node_layout->y + ( node_layout->box.r_height / 2 );				
	y_nodes = add_plrs_node( y_nodes, plrs_node );
	node->bup = plrs_node;
				

	/********************************** Erzeuge jetzt alle Kanten in den PLRS-Graphen **************************************/

															/* Zun"achst eine Kante vom li. zum re. Rand 	*/
	if ( prod->nodes == NULL )										/* Wird nur bei l"oschender Prod. gebraucht.	*/
	{
		plrs_edge = create_plrs_edge( node->bleft, node_layout->box.r_width - 2*lpr_grid, node->bright );	/* der Produktion		*/
		add_plrs_edge( plrs_edge );									/* und von unten nach oben				*/
		plrs_edge = create_plrs_edge( node->bdown, node_layout->box.r_height - 2*lpr_grid, node->bup );
		add_plrs_edge( plrs_edge );
	}

	FOR_PLRS_NODES(x_nodes, cur_plrs_node)								/* Dann vom linken Rand zu allen "ubrigen Knoten*/
		if (( cur_plrs_node != node->bleft ) && ( cur_plrs_node != node->bright ))
		{
			if ( no_node_between( node->bleft, cur_plrs_node) )
			{
				plrs_edge = create_plrs_edge( node->bleft, cur_plrs_node->value - node->bleft->value - lpr_grid, cur_plrs_node );
				add_plrs_edge( plrs_edge );
			}
		}
	END_FOR_PLRS_NODES(x_nodes, cur_plrs_node);
		
	FOR_PLRS_NODES(y_nodes, cur_plrs_node)								/* und vom unteren zu allen dar"uberliegenden.	*/
		if (( cur_plrs_node != node->bdown ) && ( cur_plrs_node != node->bup ))
		{
			if ( no_node_between( node->bdown, cur_plrs_node) )
			{
				plrs_edge = create_plrs_edge( node->bdown, cur_plrs_node->value - node->bdown->value - lpr_grid , cur_plrs_node );
				add_plrs_edge( plrs_edge );
			}
		}
	END_FOR_PLRS_NODES(y_nodes, cur_plrs_node);

	FOR_PLRS_NODES(x_nodes, cur_plrs_node)								/* Jetzt von allen X-Knoten zum rechten Rand	*/
		if (( cur_plrs_node != node->bleft ) && ( cur_plrs_node != node->bright ))
		{
			if ( no_node_between( cur_plrs_node, node->bright) )
			{
				plrs_edge = create_plrs_edge( cur_plrs_node, node->bright->value - cur_plrs_node->value  - lpr_grid,  node->bright);
				add_plrs_edge( plrs_edge );
			}
		}
	END_FOR_PLRS_NODES(x_nodes, cur_plrs_node);
	FOR_PLRS_NODES(y_nodes, cur_plrs_node)								/* und von allen Y-Knoten zum oberen Rand		*/
		if (( cur_plrs_node != node->bdown ) && ( cur_plrs_node != node->bup ))
		{
			if ( no_node_between( cur_plrs_node, node->bup) )
			{
				plrs_edge = create_plrs_edge( cur_plrs_node, node->bup->value - cur_plrs_node->value  - lpr_grid,  node->bup);
				add_plrs_edge( plrs_edge );
			}
		}
	END_FOR_PLRS_NODES(y_nodes, cur_plrs_node);


	FOR_PLRS_NODES(x_nodes, cur_plrs_node1)								/* Nun untereinander, falls die Koord.-Differenz*/
		if (( cur_plrs_node1 != node->bleft ) && ( cur_plrs_node1 != node->bright ))	/* positiv.							*/
		{
			FOR_PLRS_NODES(x_nodes, cur_plrs_node2)
				if (( cur_plrs_node2 != node->bleft ) && ( cur_plrs_node2 != node->bright ))
				{
					if ( cur_plrs_node2->value > cur_plrs_node1->value )
					{
						if (  no_node_between( cur_plrs_node1,cur_plrs_node2 ) )
						{ 
							plrs_edge = create_plrs_edge( cur_plrs_node1, cur_plrs_node2->value - cur_plrs_node1->value, cur_plrs_node2);
							add_plrs_edge( plrs_edge );
						} 
					}
				}
			END_FOR_PLRS_NODES(x_nodes, cur_plrs_node2);
		}
	END_FOR_PLRS_NODES(x_nodes, cur_plrs_node1);
	FOR_PLRS_NODES(y_nodes, cur_plrs_node1)								/* Genauso f"ur die Y-Knoten				*/
		if (( cur_plrs_node1 != node->bdown ) && ( cur_plrs_node1 != node->bup ))
		{
			FOR_PLRS_NODES(y_nodes, cur_plrs_node2)
				if (( cur_plrs_node2 != node->bdown ) && ( cur_plrs_node2 != node->bup ))
				{
					if ( cur_plrs_node2->value > cur_plrs_node1->value )
					{
						if (  no_node_between( cur_plrs_node1,cur_plrs_node2 ) )
						{ 
							plrs_edge = create_plrs_edge( cur_plrs_node1, cur_plrs_node2->value - cur_plrs_node1->value, cur_plrs_node2);
							add_plrs_edge( plrs_edge );
						} 
					}
				}
			END_FOR_PLRS_NODES(y_nodes, cur_plrs_node2);
		}
	END_FOR_PLRS_NODES(y_nodes, cur_plrs_node1);
	





	prod_system = create_plr_system();									/* Erzeuge schliesslich ein plr_System, um das	*/
	prod_system->x_graph = x_nodes;									/* ganze abzulegen und zur"uckzugeben.		*/
	prod_system->y_graph = y_nodes;

	return prod_system;
}		
		


					
			
			

 












