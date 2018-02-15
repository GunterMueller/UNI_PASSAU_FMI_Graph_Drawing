/************************************************************************************************/
/*																*/
/*						FILE: lpr_hhierarchie.c							*/
/*																*/
/*		Hier befindet sich alles, was zum Berechnen von Egde-Hierarchie-Sets			*/
/*		und Bridge-Hierarchie-Sets n"otig ist.								*/
/*																*/
/************************************************************************************************/


#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpr_eedge.h"

#include "lpr_hhierarchie.h"


/*************************************************************************************************
function:	create_lpr_hierarchie

	Erzeugt Speicherplatz fuer Datenstruktur lpr_Hierarchie 

Output:	Zeiger auf Speicher
*************************************************************************************************/

lpr_Hierarchie	create_lpr_hierarchie(void)
{
	lpr_Hierarchie	new = (lpr_Hierarchie)mymalloc( sizeof(struct lpr_hierarchie) );

	new->edges	= NULL;

	return( new );
}

/*************************************************************************************************
function:	create_lpr_hierarchie_with_edges
Input:	Edgelist edges

	Erzeugt Speicherplatz fuer Datenstruktur lpr_Hierarchie; legt Zeiger edges an

Output:	Zeiger auf Speicher
*************************************************************************************************/

lpr_Hierarchie	create_lpr_hierarchie_with_edges(lpr_Edgelist edges)
{
	lpr_Hierarchie	new = create_lpr_hierarchie();

	new->edges	= edges;

	return( new );
}

/*************************************************************************************************
function:	lpr_hierarchie_SET_EDGES
Input:	lpr_Hierarchie hierarchie, lpr_Edgelist edges

	Haengt edges an
*************************************************************************************************/

void	lpr_hierarchie_SET_EDGES(lpr_Hierarchie hierarchie, lpr_Edgelist edges)
{
	hierarchie->edges	= edges;
}

/*************************************************************************************************
function:	lpr_hierarchie_GET_EDGES
Input:	lpr_Hierarchie hierarchie

Output:	hierarchie->edges
*************************************************************************************************/

lpr_Edgelist	lpr_hierarchie_GET_EDGES(lpr_Hierarchie hierarchie)
{
	return( hierarchie->edges );
}

/*************************************************************************************************
function:	lpr_hierarchie_add_edgelist
Input:	lpr_Hierarchie hierarchie, lpr_Edgelist edges

	Haengt edges hinten an hierarchie->edges an
*************************************************************************************************/

void	lpr_hierarchie_add_edgelist_to_edges(lpr_Hierarchie hierarchie, lpr_Edgelist edges)
{
	lpr_hierarchie_SET_EDGES( hierarchie, add_edgelist_to_lpr_edgelist(lpr_hierarchie_GET_EDGES(hierarchie), edges) );
}

/*************************************************************************************************
function:	lpr_hierarchie_add_edge
Input:	lpr_Hierarchie hierarchie, lpr_Edge edge

	Haengt edge hinten an hierarchie->edges an
*************************************************************************************************/

void	lpr_hierarchie_add_edge_to_edges(lpr_Hierarchie hierarchie, lpr_Edge edge)
{
	lpr_hierarchie_SET_EDGES( hierarchie, add_edge_to_lpr_edgelist(lpr_hierarchie_GET_EDGES(hierarchie), edge) );
}

/*************************************************************************************************
function:	free_lpr_hierarchie
Input:	lpr_Hierarchie hierarchie

	Loescht Speicherplatz von hierarchie und die Kantenlisten ( ohne Kanten )
*************************************************************************************************/

void	free_lpr_hierarchie(lpr_Hierarchie hierarchie)
{
	if (hierarchie != NULL)
	{
		free_lpr_edgelist( lpr_hierarchie_GET_EDGES(hierarchie) );
		free_lpr_edgelist( lpr_hierarchie_GET_EDGES(hierarchie) );
		free( hierarchie );
	}
}


/************************************************************
Function    : free_lpr_Bridge_hierarchie
Input       : lpr_Edge edge;
Output      : void
Description : Gibt den Speicher, der von den bridge_numbers
	        der "ubergebenen Kante belegt wird, wieder frei.
*************************************************************/ 
void free_lpr_Bridge_hierarchie(lpr_Edge edge)
{
	lpr_Bridge_hierarchie cur_bridge_hierarchy = edge->bridge_numbers, last;

	
	if (edge->bridge_numbers != NULL)
	{
		edge->bridge_numbers->pre->suc = NULL;				/* Breche Zyklus auf.	*/
		while (cur_bridge_hierarchy != NULL)				/* und l"osche Liste	*/
		{
			last = cur_bridge_hierarchy;
			cur_bridge_hierarchy = cur_bridge_hierarchy->suc;
			free(last);
		}
		edge->bridge_numbers = NULL;	
	}
}


/*********************************************************************************************
Function	: delete_all_hierarchies
Input		: lpr_Node root
Output	: void
Description : L"oscht die Edge- und Bridge-Hierarchies des gesamten lpr_Graphen.
*********************************************************************************************/
void delete_all_hierarchies(lpr_Node root)
{

	lpr_Graph lpr_graph;
	lpr_Nodelist cur_nodelist;
	lpr_Edgelist cur_embeddinglist, cur_edgelist, help_edgelist;

	lpr_graph = root->applied_production;
	FOR_LPR_IN_CONN_REL(lpr_graph->IN_embeddings, cur_embeddinglist)						/* L"osche EH und BH der einlaufenden Einbettungs-	*/
		free_lpr_Bridge_hierarchie(cur_embeddinglist->edge);							/* regeln und aller davon erzeugten Kanten.		*/
		free_lpr_hierarchie(cur_embeddinglist->edge->EH);
		if (cur_embeddinglist->edge->generated_edges != NULL)
		{
			FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist)
				free_lpr_Bridge_hierarchie(cur_edgelist->edge);
				free_lpr_hierarchie(cur_edgelist->edge->EH);
			END_FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist);
		}
	END_FOR_LPR_IN_CONN_REL(lpr_graph->IN_embeddings, cur_embeddinglist);

	FOR_LPR_OUT_CONN_REL(lpr_graph->OUT_embeddings, cur_embeddinglist)					/* L"osche EH und BH der auslaufenden Einbettungs-	*/
		free_lpr_Bridge_hierarchie(cur_embeddinglist->edge);							/* regeln und aller davon erzeugten Kanten.		*/
		free_lpr_hierarchie(cur_embeddinglist->edge->EH);
		if (cur_embeddinglist->edge->generated_edges != NULL)
		{
			FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist)
				free_lpr_Bridge_hierarchie(cur_edgelist->edge);
				free_lpr_hierarchie(cur_edgelist->edge->EH);
			END_FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist);
		}
	END_FOR_LPR_OUT_CONN_REL(lpr_graph->OUT_embeddings, cur_embeddinglist);

	FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist)    								/* L"osche EH und BH der RHS_EDGES und der darauf	*/
       	{                                                      						/* folgenden Kanten.						*/      		FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist)
			free_lpr_Bridge_hierarchie(cur_edgelist->edge);
			free_lpr_hierarchie(cur_edgelist->edge->EH);
			if (cur_edgelist->edge->following_edges != NULL)
			{
				FOR_LPR_EDGELIST(cur_edgelist->edge->following_edges->edges, help_edgelist)
					free_lpr_Bridge_hierarchie(help_edgelist->edge);
					free_lpr_hierarchie(help_edgelist->edge->EH);
				END_FOR_LPR_EDGELIST(cur_edgelist->edge->following_edges->edges, help_edgelist);
			}
		END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);

		FOR_LPR_RHS_EDGES(cur_nodelist->node->target_edges, cur_edgelist)
			free_lpr_Bridge_hierarchie(cur_edgelist->edge);
			free_lpr_hierarchie(cur_edgelist->edge->EH);
			if (cur_edgelist->edge->following_edges != NULL)
			{
				FOR_LPR_EDGELIST(cur_edgelist->edge->following_edges->edges, help_edgelist)
					free_lpr_Bridge_hierarchie(help_edgelist->edge);	
					free_lpr_hierarchie(help_edgelist->edge->EH);
				END_FOR_LPR_EDGELIST(cur_edgelist->edge->following_edges->edges, help_edgelist);
			}
		END_FOR_LPR_RHS_EDGES(cur_nodelist->node->target_edges, cur_edgelist);
		if (cur_nodelist->node->applied_production != NULL)								/* Laufe Top-Down durch den lpr_Graphen.	*/
			delete_all_hierarchies( cur_nodelist->node);        
	}                            							
	END_FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist);
}



/*****************************************************************************************
Function	: lpr_hierarchy_add_hierarchy
Input		: lpr_Hierarchie h1, h2
Output	: lpr_Hierarchie
Description	: Funktion, die an die "ubergebene Hierarchie bzw an dessen Edgeliste die
		  Edgelisten der zweiten Hierarchie anh"angt"ubergebene einelementige Egdelist
		  anh"angt. (wird z.B. f"ur compute_hsn_edges ben"otigt.)
*****************************************************************************************/
lpr_Hierarchie lpr_hierarchy_add_hierarchy(lpr_Hierarchie h1, lpr_Hierarchie h2)
{
	lpr_Edgelist last_of_h1, last_of_h2;

	if ( h2 == NULL)
		return h1;
	else
		if (h1 == NULL)
			return h2;
		else
		{
			last_of_h1 = h1->edges->pre;
			last_of_h2 = h2->edges->pre;

			last_of_h1->suc = h2->edges;
			h2->edges->pre  = last_of_h1;
			last_of_h2->suc = h1->edges;
			h1->edges->pre  = last_of_h2;
		
			return h1;
		}
}



/*****************************************************************************************
Function	: lpr_hierarchie_add_edgelist_to_hierarchie_edgelist
Input		: lpr_Hierarchie hierarchy, lpr_Edgelist   new_edgelist
Output	: void
Description	: Funktion, die an die "ubergebene Hierarchie bzw an dessen Edgeliste die
		  "ubergebene einelementige Egdelist anh"angt. (wird f"ur lpr_hierarchy_copy
		  _hierarchy ben"otigt)
****************************************************************************************/
void lpr_hierarchie_add_edgelist_to_hierarchie_edgelist(lpr_Hierarchie hierarchy, lpr_Edgelist new_edgelist)
{
	lpr_Edgelist last_of_hierarchy;

	if (hierarchy->edges == NULL)                      		/* Falls die Hierarchieliste noch leer ist setzte sie gleich der 	*/
		hierarchy->edges = new_edgelist;	   			/* "ubergebenen Edgelist 					    		*/
	else
	{						   				/* sonst hinten anh"angen, indem pre und suc jeweils entsprechend	*/
		last_of_hierarchy = hierarchy->edges->pre; 		/* ge"andert werden.						    		*/

		hierarchy->edges->pre->suc = new_edgelist;
		hierarchy->edges->pre      = new_edgelist;
		new_edgelist->suc          = hierarchy->edges;
		new_edgelist->pre	   = last_of_hierarchy;
	}
}
		



/****************************************************************************************
Function	: lpr_hierarchy_copy_hierarchy
Input		: lpr_Hierarchie original
Output	: lpr_Hierarchie
Description	: Funktion, die die "ubergebene Hierarchie kopiert und das Ergebnis
		  zur"uckliefert. (wird f"ur compute_hsn_edges ben"otigt)
****************************************************************************************/
lpr_Hierarchie lpr_hierarchy_copy_hierarchy(lpr_Hierarchie original)
{
	lpr_Hierarchie copy;
	lpr_Edgelist   cur_edgelist, new_edgelist;

	if ( original == NULL)    						 		/* Falls das Original NULL ist, hat es eh keinen Sinn. */
		copy = NULL;      						 		/* also NULL zur"uck.                                  */
	else
	{
		copy = create_lpr_hierarchie();					 	/* sonst erzeuge neue Hierarchie                       */
		FOR_LPR_EDGELIST( original->edges, cur_edgelist )                	/* und neue Edgelisten entsprechend denen im Original  */
		{
			new_edgelist = create_lpr_edgelist_with_edge(cur_edgelist->edge);
			lpr_hierarchie_add_edgelist_to_hierarchie_edgelist(copy, new_edgelist);
		}
		END_FOR_LPR_EDGELIST( original->edges, cur_edgelist );
	}
	return copy;
}

			
		
/***************************************************************************************
Function	: compute_hsn_edges
Input		: lpr_Edgelist workedgelist
Output	: void
Description : Funktion, die die eigentliche Arbeit zur Berechnung der EH einer
		  Kante macht. Sie berechnet die EH der "ubergebenen Kante und deren EH#
***************************************************************************************/
void compute_hsn_edges(lpr_Edgelist workedgelist)
{
	lpr_Edge workedge = workedgelist->edge;
	lpr_Edgelist cur_edgelist, new_edgelist;
   	lpr_Hierarchie help_hierarchy = NULL;
	int help_counter;

	if ( (workedge->source->applied_production == NULL) && (workedge->target->applied_production == NULL) )
											 				/* Sind source und target beide terminal? */ 
  	{					                                         			/* falls ja,                              */
		new_edgelist = create_lpr_edgelist();					 		/* dann kopiere Kante in neue Edgelist    */
		new_edgelist->edge = workedge;

		workedge->EH = create_lpr_hierarchie_with_edges(new_edgelist);		 	/* dann ist EH(workedge) = {workedge}     */
		workedge->EH_number = 1;						 			/* und EH#(workedge) ist 1                */
	}
	else										 				/* falls nein,                            */
	{
		if (workedge->following_edges != NULL)
		{
			help_hierarchy = NULL;
			help_counter = 0;
			FOR_LPR_EDGELIST(workedge->following_edges->edges, cur_edgelist) 		/* durchlaufe alle Kanten, die aus dieser */
			{								 				/* entstanden sind.                       */
				compute_hsn_edges( cur_edgelist );                       		/* und berechne rekursiv deren EH         */
				help_hierarchy = lpr_hierarchy_add_hierarchy(help_hierarchy,lpr_hierarchy_copy_hierarchy(cur_edgelist->edge->EH));
				help_counter+=cur_edgelist->edge->EH_number;             		/* f"uge diese EHs an die EH von workedge */
			}								 				/* an und z"ahle EH#(workedge) hoch       */
			END_FOR_LPR_EDGELIST(workedge->following_edges->edges, cur_edgelist);
		
			workedge->EH = help_hierarchy;
			workedge->EH_number = help_counter;
		}
		else
		{
			workedge->EH = NULL;
			workedge->EH_number = 0;
		}			
	}
}



/******************************************************************************************
Function	: compute_hierarchy_sets_and_numbers_of_RHS_edges
Input		: lpr_Node root
Output	: void
Description	: Funktion zum berechnen von EH(e) f"ur alle RHS_edges der bisher angewandten
		  Produktionen.
******************************************************************************************/
void compute_hierarchy_sets_and_numbers_of_RHS_edges(lpr_Node root)
{
	lpr_Nodelist cur_nodelist;
	lpr_Edgelist cur_edgelist;
	lpr_Graph    lpr_graph;

	lpr_graph = root->applied_production;                                           	/* Holen der angewandten Produktion */
	FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist)                                	/* Durchlaufe alle Knoten der Prod. */
	{                                                                               	/* um an die Kanten ranzukommen     */
		FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);      	/* Durchlaufe die Kanten            */
		{
			compute_hsn_edges(cur_edgelist);                     	        		/* berechne EH und EH# von RHS_edge */
		}
		END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);
		if (cur_nodelist->node->applied_production != NULL)					/* Gibts eine Produktion?           */
			compute_hierarchy_sets_and_numbers_of_RHS_edges(cur_nodelist->node);	/* rekursiv weiter zur n"achsten	*/
	}                            										/* Produktion                       */
	END_FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist);		
}


/******************************************************************************************
Function	: compute_hierarchy_sets_and_numbers_of_embeddings
Input		: lpr_Node root
Output	: void
Description	: Funktion zum berechnen von EH(e) f"ur alle Einbettungsregeln der bisher
		  angewandten Produktionen.
******************************************************************************************/
void compute_hierarchy_sets_and_numbers_of_embeddings(lpr_Node root)
{
	lpr_Nodelist   cur_nodelist;
	lpr_Edgelist   cur_edgelist, cur_embeddinglist;
	lpr_Graph      lpr_graph;
	lpr_Hierarchie help_hierarchy = NULL;
	int            help_counter;

	lpr_graph = root->applied_production;
	FOR_LPR_IN_CONN_REL(lpr_graph->IN_embeddings, cur_embeddinglist)					/* Durchlaufe die IN_CONN_RELs		*/
	{															/* Init. Z"ahler					*/
		help_counter = 0;												/* und  Hierarchie.				*/
		help_hierarchy = NULL;
		if (cur_embeddinglist->edge->generated_edges != NULL)						/* Falls aus dieser Regel Kanten entstan-	*/
		{														/* sind, durchlaufe diese und h"ange eine	*/
			FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist)
			{													/* Kopie der EH von diesen an die 	 	*/
				help_hierarchy = lpr_hierarchy_add_hierarchy(help_hierarchy,lpr_hierarchy_copy_hierarchy(cur_edgelist->edge->EH));
				help_counter+=cur_edgelist->edge->EH_number;             			/* Hierarchie an.					*/
			}
			END_FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist);
			cur_embeddinglist->edge->EH        = help_hierarchy;					/* Setze die EH dieser Regel gleich der 	*/				
			cur_embeddinglist->edge->EH_number = help_counter;					/* berechneten Hierarchie, ebenso die EH#	*/
		}
	}
	END_FOR_LPR_IN_CONN_REL(lpr_graph->IN_embeddings, cur_embeddinglist);
	
	FOR_LPR_OUT_CONN_REL(lpr_graph->OUT_embeddings, cur_embeddinglist)				/* F"ur auslaufende Regeln analog.		*/
	{
		help_counter = 0;
		help_hierarchy = NULL;
		if (cur_embeddinglist->edge->generated_edges != NULL)
		{
			FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist)
			{
				help_hierarchy = lpr_hierarchy_add_hierarchy(help_hierarchy,lpr_hierarchy_copy_hierarchy(cur_edgelist->edge->EH));
				help_counter+=cur_edgelist->edge->EH_number;             
			}
			END_FOR_LPR_EDGELIST(cur_embeddinglist->edge->generated_edges->edges, cur_edgelist);
			cur_embeddinglist->edge->EH        = help_hierarchy;
			cur_embeddinglist->edge->EH_number = help_counter;
		}
	}
	END_FOR_LPR_OUT_CONN_REL(lpr_graph->OUT_embeddings, cur_embeddinglist);

                                           
	FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist)                                		/* Top-Down_Durchlauf				 */
	{                                                                               
		if (cur_nodelist->node->applied_production != NULL)			
			compute_hierarchy_sets_and_numbers_of_embeddings(cur_nodelist->node);         
	}                            							
	END_FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist);		
}


/*****************************************************************************************
Function   : create_lpr_Bridge_hierarchie
Input      : void
Output     : lpr_Bridge_hierarchie
Description: Erzeugt eine leere lpr_Bridge_hierarchie und gibt den Zeiger darauf zur"uck.
*****************************************************************************************/
lpr_Bridge_hierarchie create_lpr_Bridge_hierarchie(void)
{
	lpr_Bridge_hierarchie	new = (lpr_Bridge_hierarchie)mymalloc( sizeof(struct lpr_bridge_hierarchie) );

	new->number     	= 0;
	new->bridge_edge	= NULL;
	new->suc		= new;
	new->pre		= new;
	return( new );
}



/****************************************************************
Function   : get_bridge_hierarchy
Input      : lpr_Edge edge1, edge2
Output     : lpr_Bridge_hierarchie
Description: Testet, ob in den bridge_numbers von edge1 die Kante
	       edge2 auftaucht. Falls ja, wird ein Zeiger auf diese
	       lpr_Bridge_hierarchie zur"uckgegeben, sonst NULL.
****************************************************************/

lpr_Bridge_hierarchie get_bridge_hierarchy(lpr_Edge edge1, lpr_Edge edge2)
{
	lpr_Bridge_hierarchie cur_bridge_hierarchy, back = NULL;
	
	if (edge1->bridge_numbers != NULL)                      				/* Falls bridge_numbers f"ur edge1 existieren  	*/
	{                      										/* suche nach einer lpr_Bridge_hierarchie, die 	*/
		FOR_LPR_BRIDGE_HIERARCHIE(edge1->bridge_numbers, cur_bridge_hierarchy) 	/* edge2 enth"alt      		                 	*/
			if (cur_bridge_hierarchy->bridge_edge == edge2)
			{
				back = cur_bridge_hierarchy; 
				break;									/* Gefunden, dann merke in back und Schluss	*/
			}  								                 		
		END_FOR_LPR_BRIDGE_HIERARCHIE(edge1->bridge_numbers, cur_bridge_hierarchy);
	}	
	return back;
}
		


/********************************************************************
Function    : lpr_hierarchy_append_to_bridge_hierarchy
Input       : lpr_Edge edge1,edge2; int number;
Output      : void
Description : Erzeugt eine lpr_Bridge_hierarchie aus edge2 und number
	        und h"angt diese an die bridge_numbers von edge1 an.
*********************************************************************/ 
void lpr_hierarchy_append_to_bridge_hierarchy(lpr_Edge edge1, lpr_Edge edge2, int number)
{
	lpr_Bridge_hierarchie new_bridge_hierarchy, last;

	new_bridge_hierarchy = create_lpr_Bridge_hierarchie();          	/* Erzeuge eine neue lpr_Bridge_hierarchie */
	new_bridge_hierarchy->number = number;					/* und setze deren Eintr"age               */
	new_bridge_hierarchy->bridge_edge = edge2;

	if (edge1->bridge_numbers != NULL)                              	/* Falls schon was da ist, h"ange die neue */
	{ 											/* Hierarchie hinten an.                   */
		last = edge1->bridge_numbers->pre;

		last->suc = new_bridge_hierarchy;
		new_bridge_hierarchy->pre = last;
		edge1->bridge_numbers->pre = new_bridge_hierarchy;
		new_bridge_hierarchy->suc = edge1->bridge_numbers;
	}
	else
		edge1->bridge_numbers =  new_bridge_hierarchy;
}
		
		





	


/*****************************************************************************************
Function	: compute_bridge_hierarchy_set_numbers
Input		: lpr_Node  root, lpr_Graph father
Output	: void
Description : Berechnet die BH# f"ur den gesamten lpr-Graphen.			
*****************************************************************************************/
void compute_bridge_hierarchy_set_numbers(lpr_Node root, lpr_Graph father)
{
	lpr_Nodelist cur_nodelist;
	lpr_Edgelist cur_embeddinglist1, cur_embeddinglist2, cur_edgelist;
	lpr_Edge     bridge_edge;
	lpr_Graph    lpr_graph;
	lpr_Bridge_hierarchie cur_bridge_hierarchy;

	
	lpr_graph = root->applied_production;
	FOR_LPR_IN_CONN_REL(lpr_graph->IN_embeddings, cur_embeddinglist1)									/* Durchlaufe die IN_CONN_RELs	*/
	{
		if (father != NULL)
		{
			FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2)							/* Durchlaufe die IN_CONN_RELs der	*/
				lpr_hierarchy_append_to_bridge_hierarchy(cur_embeddinglist2->edge,cur_embeddinglist1->edge,0);	/* father-Prod. und h"ange neue BH	*/
			END_FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2);						/* an cur_embeddinglist2->edge an.	*/

			FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2)							/* Ebenso f"ur die auslaufenden Re-	*/
				lpr_hierarchy_append_to_bridge_hierarchy(cur_embeddinglist2->edge,cur_embeddinglist1->edge,0);	/* geln.					*/
			END_FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2);
		
			FOR_LPR_NODELIST(father->nodes, cur_nodelist)										/* und f"ur die Kanten der rechten	*/
				FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist)						/* Seite.					*/
					if ( cur_edgelist->edge->edge_type == lpr_RHS_EDGE ) 
						lpr_hierarchy_append_to_bridge_hierarchy(cur_edgelist->edge,cur_embeddinglist1->edge,0);
				END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);
			END_FOR_LPR_NODELIST(father->nodes, cur_nodelist);
		}

		if (cur_embeddinglist1->edge->generated_edges != NULL)									/* Falls aus dieser Kante neue ent-	*/
		{																		/* standen sind, so h"ange auch f"ur*/
			FOR_LPR_EDGELIST(cur_embeddinglist1->edge->generated_edges->edges, cur_edgelist)				/* diese eine neue BH an, wobei hier*/
				bridge_edge = cur_edgelist->edge->pred->last;									/* aber als Nummer die EH# "uberge-	*/
				if ((cur_bridge_hierarchy = get_bridge_hierarchy(bridge_edge,cur_embeddinglist1->edge)) == NULL)/* ben wird.				*/
					lpr_hierarchy_append_to_bridge_hierarchy(bridge_edge,cur_embeddinglist1->edge,
										 cur_edgelist->edge->EH_number);
				else
					cur_bridge_hierarchy->number+=cur_edgelist->edge->EH_number;
			END_FOR_LPR_EDGELIST(cur_embeddinglist1->edge->generated_edges->edges, cur_edgelist);
		}								
	}
	END_FOR_LPR_IN_CONN_REL(lpr_graph->IN_embeddings, cur_embeddinglist1);


	FOR_LPR_OUT_CONN_REL(lpr_graph->OUT_embeddings, cur_embeddinglist1)								/* Analog f"ur die auslaufenden 	*/
	{																			/* Regeln.					*/
		if (father != NULL)
		{
			FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2)
				lpr_hierarchy_append_to_bridge_hierarchy(cur_embeddinglist2->edge,cur_embeddinglist1->edge,0);
			END_FOR_LPR_IN_CONN_REL(father->IN_embeddings, cur_embeddinglist2);

			FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2)
				lpr_hierarchy_append_to_bridge_hierarchy(cur_embeddinglist2->edge,cur_embeddinglist1->edge,0);
			END_FOR_LPR_OUT_CONN_REL(father->OUT_embeddings, cur_embeddinglist2);
		
			FOR_LPR_NODELIST(father->nodes, cur_nodelist)
				FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist)
					lpr_hierarchy_append_to_bridge_hierarchy(cur_edgelist->edge,cur_embeddinglist1->edge,0);
				END_FOR_LPR_RHS_EDGES(cur_nodelist->node->source_edges, cur_edgelist);	
			END_FOR_LPR_NODELIST(father->nodes, cur_nodelist);
		}

		if (cur_embeddinglist1->edge->generated_edges != NULL)
		{
			FOR_LPR_EDGELIST(cur_embeddinglist1->edge->generated_edges->edges, cur_edgelist)
				bridge_edge = cur_edgelist->edge->pred->last;
				if ((cur_bridge_hierarchy = get_bridge_hierarchy(bridge_edge,cur_embeddinglist1->edge)) == NULL)
					lpr_hierarchy_append_to_bridge_hierarchy(bridge_edge,cur_embeddinglist1->edge,0);
				else
					cur_bridge_hierarchy->number+=cur_edgelist->edge->EH_number;
			END_FOR_LPR_EDGELIST(cur_embeddinglist1->edge->generated_edges->edges, cur_edgelist);
		}								
	}
	END_FOR_LPR_OUT_CONN_REL(lpr_graph->OUT_embeddings, cur_embeddinglist1);

	FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist) 											/* Top-Down-Durchlauf			*/
     	{                                                                                
		if (cur_nodelist->node->applied_production != NULL)			
			compute_bridge_hierarchy_set_numbers(cur_nodelist->node, lpr_graph);        
	}                            							
	END_FOR_LPR_NODELIST(lpr_graph->nodes, cur_nodelist);		

	
}


