#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_edgeline.h"

#include "lpr_nnode.h"
#include "lpr_hhierarchie.h"
#include "lpr_eedge.h"
#include "lpr_ggraph.h"

/************************************************************************************************/
/*												*/
/*	File mit den grundlegenden Funktionen auf lpr_edge und lpr_edgelist			*/
/*												*/
/************************************************************************************************/



/************************************************************************************************/
/******					lpr_Edge					   ******/
/************************************************************************************************/


/*************************************************************************************************
function:	create_lpr_edge

	Allockiert Speicherplatz fuer Datenstruktur lpr_edge und schickt Zeiger p darauf zurueck

Output:	p
*************************************************************************************************/

lpr_Edge	create_lpr_edge(void)
{
	lpr_Edge	new = (lpr_Edge)mymalloc(sizeof( struct lpr_edge));

	new->edge_type			= lpr_RHS_EDGE;
	new->source			= NULL;
	new->target			= NULL;
	new->source_label		= NULL;
	new->target_label		= NULL;
	new->GRAPH_iso			= NULL;
	new->PROD_iso			= NULL;
	new->array_of_iso_edge_pointers = NULL;
	new->generated_edges		= NULL;
	new->following_edges		= NULL;
	new->label			= NULL;
	new->last 			= NULL;
	new->pred 			= NULL;
	new->EH                	        = NULL;
	new->EH_number         		= 0;
	new->bridge_numbers     	= NULL;
	new->pes_array			= NULL;
	new->ta_td			= NULL;
	new->S_list			= NULL;
	new->S2_list 		= NULL;
	new->start_value			= -1;
	new->end_value			= -1;
	return( new );
}

/*************************************************************************************************
function:	lpr_edge_SET_LABEL
Input:	lpr_Edge edge, char* label

	Traegt label in edge->label ein
*************************************************************************************************/

void	lpr_edge_SET_LABEL(lpr_Edge edge, char *label)
{
	edge->label = label;
}

/*************************************************************************************************
function:	lpr_edge_GET_LABEL
Input:	lpr_Edge edge

	Holt edge->label
*************************************************************************************************/

char*	lpr_edge_GET_LABEL(lpr_Edge edge)
{
	return( edge->label );
}

/*************************************************************************************************
function:	lpr_edge_SET_SOURCE_LABEL
Input:	lpr_Edge edge, char* label

	Traegt label in edge->source_label ein
*************************************************************************************************/

void	lpr_edge_SET_SOURCE_LABEL(lpr_Edge edge, char *label)
{
	edge->source_label = label;
}

/*************************************************************************************************
function:	lpr_edge_GET_SOURCE_LABEL
Input:	lpr_Edge edge

	Holt edge->source_label
*************************************************************************************************/

char*	lpr_edge_GET_SOURCE_LABEL(lpr_Edge edge)
{
	return( edge->source_label );
}

/*************************************************************************************************
function:	lpr_edge_SET_TARGET_LABEL
Input:	lpr_Edge edge, char* label

	Traegt label in edge->target_label ein
*************************************************************************************************/

void	lpr_edge_SET_TARGET_LABEL(lpr_Edge edge, char *label)
{
	edge->target_label = label;
}

/*************************************************************************************************
function:	lpr_edge_GET_TARGET_LABEL
Input:	lpr_Edge edge

	Holt edge->target_label
*************************************************************************************************/

char*	lpr_edge_GET_TARGET_LABEL(lpr_Edge edge)
{
	return( edge->target_label );
}

/*************************************************************************************************
function:	lpr_edge_SET_GENERATED_EDGES
Input:	lpr_Edge edge, lpr_hierarchie hierarchie

	Traegt  in edge->generated_edges ein
*************************************************************************************************/

void	lpr_edge_SET_GENERATED_EDGES(lpr_Edge edge, lpr_Hierarchie hierarchie)
{
	edge->generated_edges = hierarchie;
}

/*************************************************************************************************
function:	lpr_edge_GET_GENERATED_EDGES
Input:	lpr_Edge edge

	Holt edge->generated_edges
*************************************************************************************************/

lpr_Hierarchie	lpr_edge_GET_GENERATED_EDGES(lpr_Edge edge)
{
	return( edge->generated_edges );
}

/*************************************************************************************************
function:	lpr_edge_SET_FOLLOWING_EDGES
Input:	lpr_Edge edge, lpr_hierarchie hierarchie

	Traegt  in edge->following_edges ein
*************************************************************************************************/

void	lpr_edge_SET_FOLLOWING_EDGES(lpr_Edge edge, lpr_Hierarchie hierarchie)
{
	edge->following_edges = hierarchie;
}

/*************************************************************************************************
function:	lpr_edge_GET_FOLLOWING_EDGES
Input:	lpr_Edge edge

	Holt edge->following_edges
*************************************************************************************************/

lpr_Hierarchie	lpr_edge_GET_FOLLOWING_EDGES(lpr_Edge edge)
{
	return( edge->following_edges );
}

/*************************************************************************************************
function:	lpr_edge_SET_PROD_ISO
Input:	lpr_Edge edge, Edge prod_edge

	Traegt  in edge->PROD_iso ein
*************************************************************************************************/

void	lpr_edge_SET_PROD_ISO(lpr_Edge edge, Edge prod_edge)
{
	edge->PROD_iso = prod_edge;
}

/*************************************************************************************************
function:	lpr_edge_GET_PROD_ISO
Input:	lpr_Edge edge

	Holt edge->PROD_iso
*************************************************************************************************/

Edge	lpr_edge_GET_PROD_ISO(lpr_Edge edge)
{
	return( edge->PROD_iso );
}

/*************************************************************************************************
function:	lpr_edge_SET_GRAPH_ISO
Input:	lpr_Edge edge, Edge graph_edge

	Traegt  in edge->graph_iso ein
*************************************************************************************************/

void	lpr_edge_SET_GRAPH_ISO(lpr_Edge edge, Edge graph_edge)
{
	edge->GRAPH_iso = graph_edge;
}

/*************************************************************************************************
function:	lpr_edge_GET_GRAPH_ISO
Input:	lpr_Edge edge

	Holt edge->graph_iso
*************************************************************************************************/

Edge	lpr_edge_GET_GRAPH_ISO(lpr_Edge edge)
{
	return( edge->GRAPH_iso );
}

/*************************************************************************************************
function:	lpr_edge_SET_SOURCE
Input:	lpr_Edge edge, lpr_Node node

	Traegt  in edge->source ein
*************************************************************************************************/

void	lpr_edge_SET_SOURCE(lpr_Edge edge, lpr_Node node)
{
	edge->source = node;
}

/*************************************************************************************************
function:	lpr_edge_GET_SOURCE
Input:	lpr_Edge edge

	Holt edge->source
*************************************************************************************************/

lpr_Node	lpr_edge_GET_SOURCE(lpr_Edge edge)
{
	return( edge->source );
}

/*************************************************************************************************
function:	lpr_edge_SET_TARGET
Input:	lpr_Edge edge, lpr_Node node

	Traegt  in edge->target ein
*************************************************************************************************/

void	lpr_edge_SET_TARGET(lpr_Edge edge, lpr_Node node)
{
	edge->target = node;
}

/*************************************************************************************************
function:	lpr_edge_GET_TARGET
Input:	lpr_Edge edge

	Holt edge->target
*************************************************************************************************/

lpr_Node	lpr_edge_GET_TARGET(lpr_Edge edge)
{
	return( edge->target );
}

/*************************************************************************************************
function:	lpr_edge_SET_EDGETYPE
Input:	lpr_Edge edge,  lpr_EDGETYPE type

	Traegt  in edge->edge_type ein
*************************************************************************************************/

void	lpr_edge_SET_EDGETYPE(lpr_Edge edge, lpr_EDGETYPE type)
{
	edge->edge_type = type;
}

/*************************************************************************************************
function:	lpr_edge_GET_EDGETYPE
Input:	lpr_Edge edge

	Holt edge->edge_type
*************************************************************************************************/

lpr_EDGETYPE	lpr_edge_GET_EDGETYPE(lpr_Edge edge)
{
	return( edge->edge_type );
}

/*************************************************************************************************
function:	create_lpr_edge_with_source_and_target
Input:	lpr_Node source, target
	Allockiert Speicherplatz fuer Datenstruktur lpr_edge und schickt Zeiger p darauf zurueck.
	Legt Zeiger source und target an

Output:	p
*************************************************************************************************/

lpr_Edge	create_lpr_edge_with_source_and_target(lpr_Node source, lpr_Node target)
{
	lpr_Edge	new = create_lpr_edge();

	lpr_edge_SET_SOURCE( new, source );
	lpr_edge_SET_TARGET( new, target );

	return( new );
}

/*************************************************************************************************
function:	free_lpr_edge
Input:	lpr_Edge edge

	Loescht Speicherplatz

*************************************************************************************************/

void	free_lpr_edge(lpr_Edge edge)
{
	free_lpr_hierarchie( edge->generated_edges );
	free_lpr_hierarchie( edge->following_edges );
	free( edge );
}


/************************************************************************************************/
/******					lpr_Edgelist					   ******/
/************************************************************************************************/


/*************************************************************************************************
function:	create_lpr_edgelist

	Erzeugt Speicherplatz fuer lpr_Edgelist

Output:	Zeiger auf Speicher
*************************************************************************************************/

lpr_Edgelist	create_lpr_edgelist(void)
{
	lpr_Edgelist	new = (lpr_Edgelist)mymalloc(sizeof( struct lpr_edgelist));

	new->edge	= NULL;
	new->pre	= new;
	new->suc	= new;

	return( new );
}

/*************************************************************************************************
function:	lpr_edgelist_SET_EDGE
Input:	lpr_Edgelist list, lpr_Edge edge

	Traegt list->edge ein
*************************************************************************************************/

void	lpr_edgelist_SET_EDGE(lpr_Edgelist list, lpr_Edge edge)
{
	list->edge = edge;
}

/*************************************************************************************************
function:	lpr_edgelist_GET_EDGE
Input:	lpr_Edgelist list

	Holt list->edge 
*************************************************************************************************/

lpr_Edge	lpr_edgelist_GET_EDGE(lpr_Edgelist list)
{
	return( list->edge );
}

/*************************************************************************************************
function   :        create_lpr_iso_edge      
Input      :        void 
Output     :        lpr_Iso_edge
Description:        Erzeugt eine lpr_iso_edge
*************************************************************************************************/

lpr_Iso_edge create_lpr_iso_edge(void)
{
	lpr_Iso_edge	new = (lpr_Iso_edge)mymalloc(sizeof( struct lpr_iso_edge));

	new->edge	= NULL;
	new->bends	= 0;

	return( new );
}	

/*************************************************************************************************
function   :        lpr_edge_SET_ARRAY_OF_ISO_EDGE_POINTERS      
Input      :        lpr_Edge edge
Output     :        void
Description:        Holt sich "uber edge->PROD_iso alle zu dieser Kante isomorphen, erzeugt daraus
                    ein Array und setzt edge->array_of_iso_edge_pointers auf dieses Array. Da-
		    durch wird der Zugriff auf verschiedene Kantenlayouts einer Kante effizienter. 
*************************************************************************************************/

void lpr_edge_SET_ARRAY_OF_ISO_EDGE_POINTERS(lpr_Edge edge)
{
	int      	count = 0;                                        /* Zum Z"ahlen isomorpher Layouts    */
	Edge     	cur_edge;					  /* Zum Durchlauf durch die Layouts   */
	lpr_Iso_edge    *array;					 	  /* Das entstehende Array             */
	lpr_Iso_edge 	iso_edge;

	for_edge_multi_suc( edge->PROD_iso, cur_edge )      		  /* Z"ahle zuerst, wieviele isomorphe */ 
		count++;					   	  /* Layouts es gibt, um die Gr"osse   */
	end_for_edge_multi_suc( edge->PROD_iso, cur_edge ); 		  /* des Arrays berechnen zu k"onnen   */

	array = (lpr_Iso_edge *) mymalloc(sizeof(lpr_Iso_edge *) * count);/* Fordere ausreichend Speicher an   */
	count = 0;
	for_edge_multi_suc( edge->PROD_iso, cur_edge )                    /* und trage Layouts ins Array ein   */
		iso_edge        = create_lpr_iso_edge();		  /* wobei zun"achst eine iso_edge-    */
		iso_edge->edge  = cur_edge;				  /* Struktur erzeugt und besetzt wird */
		iso_edge->bends = lp_edgeline_length(cur_edge->lp_edge.lp_line)-2;
		array[count++]  = iso_edge;		
	end_for_edge_multi_suc( edge->PROD_iso, cur_edge );

	edge->array_of_iso_edge_pointers = array;             		  /* Speichere das Array in lpr_graph  */
}

/*************************************************************************************************
function:	create_lpr_edgelist_with_edge
Input:	lpr_Edge edge
	Erzeugt Speicherplatz fuer lpr_Edgelist. Legt Zeiger edge an

Output:	Zeiger auf Speicher
*************************************************************************************************/

lpr_Edgelist	create_lpr_edgelist_with_edge(lpr_Edge edge)
{
	lpr_Edgelist	new = create_lpr_edgelist();

	lpr_edgelist_SET_EDGE( new, edge);

	return( new );
}

/*************************************************************************************************
function:	add_edgelist_to_lpr_edgelist
Input:	lpr_Edgelist old_list, new

	Haengt new hinten an old_list an (Funktioniert auch fuer zwei Listen)

Output:	Zeiger auf 1. Listenelement
*************************************************************************************************/

lpr_Edgelist	add_edgelist_to_lpr_edgelist(lpr_Edgelist old_list, lpr_Edgelist new)
{
	lpr_Edgelist	last;

	if( old_list )
	{
		last = old_list->pre;
	}

	if( old_list == NULL )
	{
		return( new );
	}

	if ( new == NULL)
		return old_list;

	new->pre->suc		= old_list;
	last->suc		= new;
	old_list->pre		= new->pre;
	new->pre		= last;

	return( old_list );
}

/*************************************************************************************************
function:	add_edge_to_lpr_edgelist
Input:	lpr_Edgelist old_list, lpr_Edge new

	Haengt new hinten an old_list an. (Vorher Speicheranforderung fuer lpr_Edgelist )

Output:	Zeiger auf 1. Listenelement
*************************************************************************************************/

lpr_Edgelist	add_edge_to_lpr_edgelist(lpr_Edgelist old_list, lpr_Edge new)
{
	return( add_edgelist_to_lpr_edgelist(old_list, create_lpr_edgelist_with_edge(new)) );
}


/**************************************************************************************************
Function	: copy_lpr_edgelist
Input		: lpr_Edgelist
Output		: lpr_Edgelist
Description	: Erzeugt eine Kopie und gibt einen Zeiger darauf zur"uck. Falls das Original leer
		  ist, wird ebenfalls NULL zur"uckgegeben.
**************************************************************************************************/
lpr_Edgelist copy_lpr_edgelist(lpr_Edgelist original)
{
	lpr_Edgelist cur_edgelist, copy = NULL, new_edgelist;

	FOR_LPR_EDGELIST(original, cur_edgelist)
		new_edgelist 	= create_lpr_edgelist_with_edge(cur_edgelist->edge);
		copy	 	= add_edgelist_to_lpr_edgelist(copy, new_edgelist);
	END_FOR_LPR_EDGELIST(original, cur_edgelist);

	return copy;
}

/**************************************************************************************************
Function	: remove_edgelist_from_lpr_edgelist 
Input		: lpr_Edgelist list, lpr_Edgelist elem
Output		: lpr_Edgelist
Description	: L"oscht elem aus der Liste und gibt den daf"ur reservierten Speicher wieder frei.
		  Zur"uckgegeben wird der u.U. ge"anderte Anfang der Liste.
**************************************************************************************************/
lpr_Edgelist remove_edgelist_from_lpr_edgelist(lpr_Edgelist list, lpr_Edgelist elem)
{
	lpr_Edgelist start = list, cur_edgelist;

	FOR_LPR_EDGELIST( list, cur_edgelist )
		if (cur_edgelist->edge == elem->edge)
			break;
	END_FOR_LPR_EDGELIST( list, cur_edgelist );

	if (cur_edgelist == list) 		/* Falls elem das erste in der Liste ist gib seinen Nachfolger 	*/
		start = list->suc;	/* als neuen Listenstart zur"uck.				*/
	if (list->suc == list)		/* Falls die Liste nur aus einem Element besteht gib NULL zur"uck */
		start = NULL;

	cur_edgelist->pre->suc = cur_edgelist->suc;	/* Stelle Verzeigerung von Vorg"anger und Nachfolger neu her 	*/
	cur_edgelist->suc->pre = cur_edgelist->pre;
		
	free(cur_edgelist);
	return start;
}
	

		
/*************************************************************************************************
function:	free_lpr_edgelist
Input:	lpr_Edgelist list

	Loescht Speicherplatz von lpr_Edgelist Elementen ( ohne ->edge )
*************************************************************************************************/

void	free_lpr_edgelist(lpr_Edgelist list)
{
	lpr_Edgelist	to_delete;

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

/*************************************************************************************************
function:	free_lpr_edgelist_with_edge
Input:	lpr_Edgelist list

	Loescht Speicherplatz von lpr_Edgelist Elementen und von zugehoerigen lpr_Edge
*************************************************************************************************/

void	free_lpr_edgelist_with_edge(lpr_Edgelist list)
{
	lpr_Edgelist	to_delete;

	list->pre->suc = NULL;
	while( list )
	{
		to_delete = list;
		list = list->suc;

		free_lpr_edge( to_delete->edge );
		free( to_delete );
	}
}

/*************************************************************************************************
function:	free_lpr_edgelist_with_edge_and_in_source
Input:	lpr_Edgelist list

	Loescht Speicherplatz von lpr_Edgelist Elementen und von zugehoerigen lpr_Edge
*************************************************************************************************/

void	free_lpr_edgelist_with_edge_and_in_source(lpr_Edgelist list)
{
	lpr_Edgelist	to_delete;

	if( list )
	{
		list->pre->suc = NULL;

		while( list )
		{
			to_delete = list;
			list = list->suc;

			clear_edgelist_in_source( to_delete->edge );
			free_lpr_edge( to_delete->edge );
			free( to_delete );
		}
	}
}

/*************************************************************************************************
function:	free_lpr_edgelist_with_edge_and_in_target
Input:	lpr_Edgelist list

	Loescht Speicherplatz von lpr_Edgelist Elementen und von zugehoerigen lpr_Edge
*************************************************************************************************/

void	free_lpr_edgelist_with_edge_and_in_target(lpr_Edgelist list)
{
	lpr_Edgelist	to_delete;

	if( list )
	{
		list->pre->suc = NULL;

		while( list )
		{
			to_delete = list;
			list = list->suc;

			clear_edgelist_in_target( to_delete->edge );
			free_lpr_edge( to_delete->edge );
			free( to_delete );
		}
	}
}
	
/*************************************************************************************************
function:	clear_edgelist_in_source
Input:	lpr_Edge edge

	Loescht das Element lpr_Edgelist, das auf Edge zeigt in Kantenliste von Sourceknoten
*************************************************************************************************/

void	clear_edgelist_in_source(lpr_Edge edge)
{
	lpr_Edgelist	first_edge;
	lpr_Edgelist	cur_edge;

	if( lpr_edge_GET_SOURCE(edge) )
	{
		/***Einmal fuer normal Kanten, ...***/
		first_edge	= lpr_node_GET_SOURCE_EDGES(lpr_edge_GET_SOURCE(edge) );

		if( first_edge )
		{
			/****** 1. Kante muss geloescht werden						   ******/
			if( lpr_edgelist_GET_EDGE(first_edge) == edge )
			{
				/******	Es existiert nur eine Kante					   ******/
				if( lpr_edgelist_GET_EDGE(first_edge->suc) == edge )
				{
					lpr_node_SET_SOURCE_EDGES( lpr_edge_GET_SOURCE(edge), NULL );
					free( first_edge );
				}

				/****** Zeiger auf erste Kante muss umgebogen werden			   ******/
				else
				{
					first_edge->suc->pre	= first_edge->pre;
					first_edge->pre->suc	= first_edge->suc;

					lpr_node_SET_SOURCE_EDGES( lpr_edge_GET_SOURCE(edge), first_edge->suc );
					free( first_edge );
				}
			}
			else
			{
				FOR_LPR_EDGELIST( first_edge, cur_edge )
				{
					if( cur_edge->edge == edge )
					{
						cur_edge->suc->pre = cur_edge->pre;
						cur_edge->pre->suc = cur_edge->suc;

						free( cur_edge );
					}
				}
				END_FOR_LPR_EDGELIST( first_edge, cur_edge );
			}
		}
		/***Einmal fuer die spaeter dazugekommenen.***/
		first_edge	= edge->source->later_source_edges;

		if( first_edge )
		{
			/****** 1. Kante muss geloescht werden						   ******/
			if( lpr_edgelist_GET_EDGE(first_edge) == edge )
			{
				/******	Es existiert nur eine Kante					   ******/
				if( lpr_edgelist_GET_EDGE(first_edge->suc) == edge )
				{
					lpr_node_SET_SOURCE_EDGES( lpr_edge_GET_SOURCE(edge), NULL );
					free( first_edge );
				}

				/****** Zeiger auf erste Kante muss umgebogen werden			   ******/
				else
				{
					first_edge->suc->pre	= first_edge->pre;
					first_edge->pre->suc	= first_edge->suc;

					lpr_node_SET_SOURCE_EDGES( lpr_edge_GET_SOURCE(edge), first_edge->suc );
					free( first_edge );
				}
			}
			else
			{
				FOR_LPR_EDGELIST( first_edge, cur_edge )
				{
					if( cur_edge->edge == edge )
					{
						cur_edge->suc->pre = cur_edge->pre;
						cur_edge->pre->suc = cur_edge->suc;

						free( cur_edge );
					}
				}
				END_FOR_LPR_EDGELIST( first_edge, cur_edge );
			}
		}
	}
}

/*************************************************************************************************
function:	clear_edgelist_in_target
Input:	lpr_Edge edge

	Loescht das Element lpr_Edgelist, das auf Edge zeigt in Kantenliste von targetknoten
	( Kommentare siehe clear_edgelist_in_source )

*************************************************************************************************/

void	clear_edgelist_in_target(lpr_Edge edge)
{
	lpr_Edgelist	first_edge;
	lpr_Edgelist	cur_edge;

	if( edge->target )
	{
		/***Einmal fuer normal Kanten, ...***/
		first_edge	= edge->target->target_edges;

		if( first_edge )
		{
			if( lpr_edgelist_GET_EDGE(first_edge) == edge )
			{
				/******	Es existiert nur eine Kante					   ******/
				if( lpr_edgelist_GET_EDGE(first_edge->suc) == edge )
				{
					lpr_node_SET_TARGET_EDGES( lpr_edge_GET_SOURCE(edge), NULL );
					free( first_edge );
				}
				/****** Zeiger auf erste Kante muss umgebogen werden			   ******/
				else
				{
					first_edge->suc->pre	= first_edge->pre;
					first_edge->pre->suc	= first_edge->suc;

					lpr_node_SET_TARGET_EDGES( lpr_edge_GET_SOURCE(edge), first_edge->suc );
					free( first_edge );
				}
			}
			else
			{
				FOR_LPR_EDGELIST( first_edge, cur_edge )
				{
					if( cur_edge->edge == edge )
					{
						cur_edge->suc->pre = cur_edge->pre;
						cur_edge->pre->suc = cur_edge->suc;

						free( cur_edge );
					}
				}
				END_FOR_LPR_EDGELIST( first_edge, cur_edge );
			}
		}
		/***Einmal fuer die spaeter dazugekommenen.***/
		first_edge	= edge->target->later_target_edges;

		if( first_edge )
		{
			if( lpr_edgelist_GET_EDGE(first_edge) == edge )
			{
				/******	Es existiert nur eine Kante					   ******/
				if( lpr_edgelist_GET_EDGE(first_edge->suc) == edge )
				{
					lpr_node_SET_TARGET_EDGES( lpr_edge_GET_SOURCE(edge), NULL );
					free( first_edge );
				}
				/****** Zeiger auf erste Kante muss umgebogen werden			   ******/
				else
				{
					first_edge->suc->pre	= first_edge->pre;
					first_edge->pre->suc	= first_edge->suc;

					lpr_node_SET_TARGET_EDGES( lpr_edge_GET_SOURCE(edge), first_edge->suc );
					free( first_edge );
				}
			}
			else
			{
				FOR_LPR_EDGELIST( first_edge, cur_edge )
				{
					if( cur_edge->edge == edge )
					{
						cur_edge->suc->pre = cur_edge->pre;
						cur_edge->pre->suc = cur_edge->suc;

						free( cur_edge );
					}
				}
				END_FOR_LPR_EDGELIST( first_edge, cur_edge );
			}
		}
	}
}

/*************************************************************************************************
function:	lpr_edge_create_copy_of_prod_edges
Input:	Graph graphed_prod, lpr_Graph lpr_graph

	Erzeugt eine Kopie von den Knoten von graphed_prod. Geht davon aus, dass von Knoten der 
	Produktion graphed-Kopierzeiger auf entsprechung im Graphen zeigen (fuer ->GRAPH_iso);
*************************************************************************************************/

void	lpr_edge_create_copy_of_prod_edges(Graph graphed_prod, lpr_Graph lpr_graph)
{
	Node		cur_node;
	Edge		cur_edge;
	lpr_Edge	new_edge;
	Node		LHS_node	= graphed_prod->gra.gra.nce1.left_side->node;

	for_nodes( graphed_prod, cur_node )
	{
		for_edge_sourcelist( cur_node, cur_edge )
		{
			new_edge	= create_lpr_edge();

			lpr_edge_SET_LABEL		( new_edge, cur_edge->label.text		);
			lpr_edge_SET_PROD_ISO		( new_edge, cur_edge				);
			lpr_edge_SET_ARRAY_OF_ISO_EDGE_POINTERS( new_edge                               );
			lpr_edge_SET_SOURCE_LABEL	( new_edge, cur_node->label.text		);
			lpr_edge_SET_TARGET_LABEL	( new_edge, cur_edge->target->label.text	);		
			lpr_edge_SET_GRAPH_ISO		( new_edge, cur_edge->lp_edge.iso		);

			if( !inside(cur_node->x, cur_node->y, LHS_node) )
			{
				/****** IN_Embedding rule						******/

				/*** Source existiert nicht ***/
				lpr_edge_SET_TARGET	( new_edge, cur_edge->target->lp_node.copy_iso	);
				lpr_edge_SET_EDGETYPE	( new_edge, lpr_IN_CONN_REL		);

				lpr_graph_SET_IN_EMBEDDINGS( lpr_graph,
							     add_edge_to_lpr_edgelist(lpr_graph_GET_IN_EMBEDDINGS(lpr_graph), new_edge) );
			}

			if( !inside(cur_edge->target->x, cur_edge->target->y, LHS_node) )
			{
				/****** OUT_Embedding rule						******/

				/*** Target existiert nicht ***/
				lpr_edge_SET_SOURCE	( new_edge, cur_edge->source->lp_node.copy_iso	);
				lpr_edge_SET_EDGETYPE	( new_edge, lpr_OUT_CONN_REL );

				lpr_graph_SET_OUT_EMBEDDINGS( lpr_graph,
							      add_edge_to_lpr_edgelist(lpr_graph_GET_OUT_EMBEDDINGS(lpr_graph), new_edge) );
			}

			if( inside(cur_node->x, cur_node->y, LHS_node) &&
			    inside(cur_edge->target->x, cur_edge->target->y, LHS_node) )
			{
				/****** RHS_Edge							******/

				lpr_edge_SET_SOURCE	( new_edge, cur_edge->source->lp_node.copy_iso	);
				lpr_edge_SET_TARGET	( new_edge, cur_edge->target->lp_node.copy_iso	);
				lpr_edge_SET_EDGETYPE	( new_edge, lpr_RHS_EDGE		);
				new_edge->last = new_edge; /* last von new_edge ist nach Def. new_edge */
			}

			/****** an Knoten anhaengen							******/

			if( lpr_edge_GET_TARGET(new_edge) )
			{
				lpr_node_SET_TARGET_EDGES( lpr_edge_GET_TARGET(new_edge), 
						   add_edge_to_lpr_edgelist(lpr_node_GET_TARGET_EDGES(lpr_edge_GET_TARGET(new_edge)),
									    new_edge) );
			}

			if( lpr_edge_GET_SOURCE(new_edge) )
			{
				lpr_node_SET_SOURCE_EDGES( lpr_edge_GET_SOURCE(new_edge),
						   add_edge_to_lpr_edgelist(lpr_node_GET_SOURCE_EDGES(lpr_edge_GET_SOURCE(new_edge)),
									    new_edge));
			}
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( graphed_prod, cur_node );
}

Edge	find_graphed_edge(Node source, Node target)
{
	Edge cur_edge;
	
	for_edge_sourcelist( source, cur_edge )
	{
		if ( cur_edge->target == target ) return cur_edge;
	}
	end_for_edge_sourcelist( source, cur_edge );
	return NULL;
}


/*************************************************************************************************
function:	append_edges_for_out_embedding
Input:	lpr_edge edge, lpr_Graph production

	Erzeugt alle Knoten, die dadurch entstehen, dass edge in abgeleiteten Knoten hinein-
	gelaufen ist
*************************************************************************************************/
void	append_edges_for_out_embedding(lpr_Edge edge, lpr_Graph production)
{
	lpr_Edgelist	cur_edgelist;
	lpr_Edge	cur_edge,		/****** Macht Code schneller und kuerzer		******/
			new_edge;
	Edge		new_graphed_edge;

	FOR_LPR_EDGELIST( lpr_graph_GET_OUT_EMBEDDINGS(production), cur_edgelist )
	{
		cur_edge = lpr_edgelist_GET_EDGE( cur_edgelist );

		/****** Kanten vergleichen, ob Regel passt						******/
		if( my_strcmp( lpr_edge_GET_LABEL(cur_edge), lpr_edge_GET_LABEL(edge) )		&&
		    my_strcmp( lpr_edge_GET_TARGET_LABEL(cur_edge), lpr_edge_GET_TARGET_LABEL(edge) ) )
		{
			/****** Kante erzeugen								******/
			new_edge = create_lpr_edge_with_source_and_target
								( lpr_edge_GET_SOURCE(cur_edge),
								  lpr_edge_GET_TARGET(edge) );

			new_graphed_edge = find_graphed_edge( cur_edge->source->PROD_iso->iso, edge->GRAPH_iso->target );
			/*	make_new_edge_from_embedding( cur_edge->PROD_iso, ENCE_1, edge->GRAPH_iso, 1, cur_edge->source->PROD_iso ); */


			lpr_edge_SET_LABEL		( new_edge, lpr_edge_GET_LABEL(cur_edge) 			);
			lpr_edge_SET_SOURCE_LABEL	( new_edge, lpr_node_GET_LABEL(lpr_edge_GET_SOURCE(new_edge))	);
			lpr_edge_SET_TARGET_LABEL	( new_edge, lpr_node_GET_LABEL(lpr_edge_GET_TARGET(new_edge))	);
			lpr_edge_SET_EDGETYPE		( new_edge, lpr_RHS_EDGE					);
			lpr_edge_SET_PROD_ISO		( new_edge, lpr_edge_GET_PROD_ISO(cur_edge)			);			
			lpr_edge_SET_ARRAY_OF_ISO_EDGE_POINTERS( new_edge                 		                );
			lpr_edge_SET_GRAPH_ISO		( new_edge, new_graphed_edge			);

			lpr_node_SET_LATER_SOURCE_EDGES	( lpr_edge_GET_SOURCE(new_edge), 
							  add_edge_to_lpr_edgelist( lpr_node_GET_LATER_SOURCE_EDGES(lpr_edge_GET_SOURCE(new_edge)),
										    new_edge) );
			lpr_node_SET_LATER_TARGET_EDGES	( lpr_edge_GET_TARGET(new_edge),
							  add_edge_to_lpr_edgelist( lpr_node_GET_LATER_TARGET_EDGES(lpr_edge_GET_TARGET(new_edge)),
										    new_edge) );

			new_edge->pred = edge;           /* Setzt den Vorg"anger von new_edge */
			new_edge->last = cur_edge;       /* Setzt das letzte Element von history(new_edge) */

			/****** Hierarchie OUT_embedding nach Kante kann erzeugt werden	(Theorie: L(e) )******/
			if( lpr_edge_GET_GENERATED_EDGES(cur_edge) )
			{
				lpr_hierarchie_SET_EDGES( lpr_edge_GET_GENERATED_EDGES(cur_edge),
							  add_edge_to_lpr_edgelist( lpr_hierarchie_GET_EDGES(lpr_edge_GET_GENERATED_EDGES(cur_edge)),
										    new_edge ) );
			}
			else
			{
				lpr_edge_SET_GENERATED_EDGES( cur_edge, create_lpr_hierarchie_with_edges(add_edge_to_lpr_edgelist(NULL, new_edge)) );
			}

			/****** Hierarchie Kante nach Kante kann erzeugt werden	(Theorie: H(e) )******/
			if( lpr_edge_GET_FOLLOWING_EDGES(edge) )
			{
				lpr_hierarchie_SET_EDGES( lpr_edge_GET_FOLLOWING_EDGES(edge),
							  add_edge_to_lpr_edgelist( lpr_hierarchie_GET_EDGES(lpr_edge_GET_FOLLOWING_EDGES(edge)),
										    new_edge ) );
			}
			else
			{
				lpr_edge_SET_FOLLOWING_EDGES( edge, create_lpr_hierarchie_with_edges(add_edge_to_lpr_edgelist(NULL, new_edge)) );
			}
		}
	}
	END_FOR_LPR_EDGELIST( lpr_graph_GET_OUT_EMBEDDINGS(production), cur_edgelist );
}


/*************************************************************************************************
function:	append_edges_for_in_embedding
Input:	lpr_edge edge, lpr_Graph production

	Erzeugt alle Knoten, die dadurch entstehen, dass edge aus abgeleiteten Knoten heraus-
	gelaufen ist
*************************************************************************************************/

void	append_edges_for_in_embedding(lpr_Edge edge, lpr_Graph production)
{
	lpr_Edgelist	cur_edgelist;
	lpr_Edge	cur_edge,		/****** Macht Code schneller und kuerzer		******/
			new_edge;
	Edge		new_graphed_edge;

	FOR_LPR_EDGELIST( lpr_graph_GET_IN_EMBEDDINGS(production), cur_edgelist )
	{
		cur_edge = lpr_edgelist_GET_EDGE( cur_edgelist );

		/****** Kanten vergleichen, ob Regel passt					******/
		if( my_strcmp( lpr_edge_GET_LABEL(cur_edge), lpr_edge_GET_LABEL(edge)  )		&&
		    my_strcmp( lpr_edge_GET_SOURCE_LABEL(cur_edge), lpr_edge_GET_SOURCE_LABEL(edge) ) )
		{
			/****** Kante erzeugen							******/
			new_edge = create_lpr_edge_with_source_and_target ( lpr_edge_GET_SOURCE(edge), lpr_edge_GET_TARGET(cur_edge) );

			new_graphed_edge = find_graphed_edge( edge->GRAPH_iso->source, cur_edge->target->PROD_iso->iso );
			/*	make_new_edge_from_embedding( cur_edge->PROD_iso, ENCE_1, edge->GRAPH_iso, 0, cur_edge->target->PROD_iso ); */

			lpr_edge_SET_LABEL		( new_edge, lpr_edge_GET_LABEL(cur_edge) 			);
			lpr_edge_SET_SOURCE_LABEL	( new_edge, lpr_node_GET_LABEL(lpr_edge_GET_SOURCE(new_edge))	);
			lpr_edge_SET_TARGET_LABEL	( new_edge, lpr_node_GET_LABEL(lpr_edge_GET_TARGET(new_edge))	);
			lpr_edge_SET_EDGETYPE		( new_edge, lpr_RHS_EDGE					);
			lpr_edge_SET_PROD_ISO		( new_edge, lpr_edge_GET_PROD_ISO(cur_edge)			);
			lpr_edge_SET_ARRAY_OF_ISO_EDGE_POINTERS( new_edge                 		                );
			lpr_edge_SET_GRAPH_ISO		( new_edge, new_graphed_edge			);

			lpr_node_SET_LATER_SOURCE_EDGES	( lpr_edge_GET_SOURCE(new_edge), 
							  add_edge_to_lpr_edgelist( lpr_node_GET_LATER_SOURCE_EDGES(lpr_edge_GET_SOURCE(new_edge)),
										    new_edge) );
			lpr_node_SET_LATER_TARGET_EDGES	( lpr_edge_GET_TARGET(new_edge),
							  add_edge_to_lpr_edgelist( lpr_node_GET_LATER_TARGET_EDGES(lpr_edge_GET_TARGET(new_edge)),
										    new_edge) );

			new_edge->pred = edge;           /* Setzt den Vorg"anger von new_edge */
			new_edge->last = cur_edge;       /* Setzt das letzte Element von history(new_edge) */

			/****** Hierarchie IN_embedding nach Kante kann erzeugt werden	(Theorie: L(e) )******/
			if( lpr_edge_GET_GENERATED_EDGES(cur_edge) )
			{
				lpr_hierarchie_SET_EDGES( lpr_edge_GET_GENERATED_EDGES(cur_edge),
							  add_edge_to_lpr_edgelist( lpr_hierarchie_GET_EDGES(lpr_edge_GET_GENERATED_EDGES(cur_edge)),
										    new_edge ) );
			}
			else
			{
				lpr_edge_SET_GENERATED_EDGES( cur_edge, create_lpr_hierarchie_with_edges(add_edge_to_lpr_edgelist(NULL, new_edge)) );
			}

			/****** Hierarchie Kante nach Kante kann erzeugt werden	(Theorie: H(e) )******/
			if( lpr_edge_GET_FOLLOWING_EDGES(edge) )
			{
				lpr_hierarchie_SET_EDGES( lpr_edge_GET_FOLLOWING_EDGES(edge),
							  add_edge_to_lpr_edgelist( lpr_hierarchie_GET_EDGES(lpr_edge_GET_FOLLOWING_EDGES(edge)),
										    new_edge ) );
			}
			else
			{
				lpr_edge_SET_FOLLOWING_EDGES( edge, create_lpr_hierarchie_with_edges(add_edge_to_lpr_edgelist(NULL, new_edge)) );
			}
		}
	}
	END_FOR_LPR_EDGELIST( lpr_graph_GET_IN_EMBEDDINGS(production), cur_edgelist );
}

/*************************************************************************************************
function:	lpr_edge_create_embedding_edges		
Input:	lpr_Graph copy_of_production, lpr_Node derivated_node

	Erzeugt die Kanten, die sich dadurch ergeben, dass auch abgeleiteter Knoten Kanten 
	besitzen konnte. Es werden also fuer jede dieser Kanten soviele Kopien erzeugt ( und an
	den entsprechenden Knoten angehaengt ) wie durch die Embedding-Regeln notwendig
*************************************************************************************************/

void	lpr_edge_create_embedding_edges(lpr_Graph copy_of_production, lpr_Node derivated_node)
{
	lpr_Edgelist	cur_edgelist;


	FOR_LPR_RHS_EDGES( lpr_node_GET_SOURCE_EDGES(derivated_node), cur_edgelist )
	{
		append_edges_for_out_embedding( lpr_edgelist_GET_EDGE(cur_edgelist), copy_of_production );
	}
	END_FOR_LPR_RHS_EDGES( lpr_node_GET_SOURCE_EDGES(derivated_node), cur_edgelist );

	FOR_LPR_RHS_EDGES( lpr_node_GET_LATER_SOURCE_EDGES(derivated_node), cur_edgelist )
	{
		append_edges_for_out_embedding( lpr_edgelist_GET_EDGE(cur_edgelist), copy_of_production );
	}
	END_FOR_LPR_RHS_EDGES( lpr_node_GET_LATER_SOURCE_EDGES(derivated_node), cur_edgelist );


	FOR_LPR_RHS_EDGES( lpr_node_GET_TARGET_EDGES(derivated_node), cur_edgelist )
	{
		append_edges_for_in_embedding( lpr_edgelist_GET_EDGE(cur_edgelist), copy_of_production );
	}
	END_FOR_LPR_RHS_EDGES( lpr_node_GET_TARGET_EDGES(derivated_node), cur_edgelist );

	FOR_LPR_RHS_EDGES( lpr_node_GET_LATER_TARGET_EDGES(derivated_node), cur_edgelist )
	{
		append_edges_for_in_embedding( lpr_edgelist_GET_EDGE(cur_edgelist), copy_of_production );
	}
	END_FOR_LPR_RHS_EDGES( lpr_node_GET_LATER_TARGET_EDGES(derivated_node), cur_edgelist );
}








