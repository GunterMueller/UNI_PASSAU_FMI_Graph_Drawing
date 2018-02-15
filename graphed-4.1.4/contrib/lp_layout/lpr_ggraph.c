#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpr_eedge.h"
#include "lpr_nnode.h"

#include "lpr_ggraph.h"


/************************************************************************************************
function:	create_lpr_graph

	Erzeugt Speicher fuer lpr_Graph

Output:	Zeiger auf diesen Speicher
************************************************************************************************/

lpr_Graph	create_lpr_graph(void)
{
	lpr_Graph	new = (lpr_Graph)mymalloc(sizeof(struct lpr_graph));

	new->nodes			= NULL;
	new->IN_embeddings		= NULL;
	new->OUT_embeddings		= NULL;
	new->PROD_iso			= NULL;
	new->array_of_iso_prod_pointers = NULL;
	new->number_of_iso_prods        = 1;
	new->GRAPH_iso			= NULL;
	new->cost_c              	= NULL;
	new->cost_c0_array            = NULL;
	new->cost_c_stern_array		= NULL;
	new->cost_C_stern_array		= NULL;
	new->cost_C_2stern_array	= 0;
	new->set_stern_array		= NULL;
	new->set_2stern_array		= NULL;
	new->optimal_layout		= NULL;
	new->in_con_seq[0]		= NULL;
	new->in_con_seq[1]		= NULL;
	new->in_con_seq[2]		= NULL;
	new->in_con_seq[3]		= NULL;
	new->out_con_seq[0]		= NULL;
	new->out_con_seq[1]		= NULL;
	new->out_con_seq[2]		= NULL;
	new->out_con_seq[3]		= NULL;
	new->ts_array[0]		= NULL;
	new->ts_array[1]		= NULL;
	new->ts_array[2]		= NULL;
	new->ts_array[3]		= NULL;
	new->tn[0]			= 0;
	new->tn[1]			= 0;
	new->tn[2]			= 0;
	new->tn[3]			= 0;
	new->track_segments[0]	= NULL;
	new->track_segments[1]	= NULL;
	new->track_segments[2]	= NULL;
	new->track_segments[3]	= NULL;
	return( new );
}

/************************************************************************************************
function:	lpr_graph_SET_NODES
Input:	lpr_Graph graph, lpr_Nodelist nodes

	Setzt graph->nodes;
************************************************************************************************/

void	lpr_graph_SET_NODES(lpr_Graph graph, lpr_Nodelist nodes)
{
	graph->nodes = nodes;
}

/************************************************************************************************
function:	lpr_graph_GET_NODES
Input:	lpr_Graph graph

	Holt graph->nodes;
************************************************************************************************/

lpr_Nodelist	lpr_graph_GET_NODES(lpr_Graph graph)
{
	return( graph->nodes );
}

/************************************************************************************************
function:	lpr_graph_SET_PROD_ISO
Input:	lpr_Graph graph, Graph prod

	Setzt graph->Prod_iso;
************************************************************************************************/

void	lpr_graph_SET_PROD_ISO(lpr_Graph graph, Graph prod)
{
	graph->PROD_iso = prod;
}

/************************************************************************************************
function:	lpr_graph_GET_PROD_ISO
Input:	lpr_Graph graph

	Holt graph->Prod_iso;
************************************************************************************************/

Graph	lpr_graph_GET_PROD_ISO(lpr_Graph graph)
{
	return( graph->PROD_iso );
}

/************************************************************************************************
function:	lpr_graph_SET_GRAPH_ISO
Input:	lpr_Graph graph, Graph graphed_graph

	Setzt graph->Prod_iso;
************************************************************************************************/

void	lpr_graph_SET_GRAPH_ISO(lpr_Graph graph, Graph graphed_graph)
{
	graph->PROD_iso = graphed_graph;
}

/************************************************************************************************
function:	lpr_graph_GET_GRAPH_ISO
Input:	lpr_Graph graph

	Holt graph->GRAPH_iso;
************************************************************************************************/

Graph	lpr_graph_GET_GRAPH_ISO(lpr_Graph graph)
{
	return( graph->GRAPH_iso );
}

/************************************************************************************************
function:	lpr_graph_SET_IN_EMBEDDINGS
Input:	lpr_Graph graph, lpr_Edgelist edges

	Setzt graph->IN_embeddings;
************************************************************************************************/

void	lpr_graph_SET_IN_EMBEDDINGS(lpr_Graph graph, lpr_Edgelist edges)
{
	graph->IN_embeddings = edges;
}

/************************************************************************************************
function:	lpr_graph_GET_IN_EMBEDDINGS
Input:	lpr_Graph graph

	Holt graph->IN_embeddings;
************************************************************************************************/

lpr_Edgelist	lpr_graph_GET_IN_EMBEDDINGS(lpr_Graph graph)
{
	return( graph->IN_embeddings );
}

/************************************************************************************************
function:	lpr_graph_SET_OUT_EMBEDDINGS
Input:	lpr_Graph graph, lpr_Edgelist edges

	Setzt graph->OUT_embeddings;
************************************************************************************************/

void	lpr_graph_SET_OUT_EMBEDDINGS(lpr_Graph graph, lpr_Edgelist edges)
{
	graph->OUT_embeddings = edges;
}

/************************************************************************************************
function:	lpr_graph_GET_OUT_EMBEDDINGS
Input:	lpr_Graph graph

	Holt graph->OUT_embeddings;
************************************************************************************************/

lpr_Edgelist	lpr_graph_GET_OUT_EMBEDDINGS(lpr_Graph graph)
{
	return( graph->OUT_embeddings );
}

/************************************************************************************************
function:	create_lpr_graph_with_nodes
Input:	lpr_Nodelist nodes

	Erzeugt Speicher fuer lpr_Graph und setzt Zeiger auf Nodes

Output:	Zeiger auf neuen Speicher
************************************************************************************************/

lpr_Graph	create_lpr_graph_with_nodes(lpr_Nodelist nodes)
{
	lpr_Graph	new = create_lpr_graph();

	lpr_graph_SET_NODES( new, nodes );

	return( new );
}

/************************************************************************************************
function:	free_lpr_graph
Input:	lpr_Graph graph

	Loescht Speicherplatz von GANZEM Graph (+darunterliegendes)
************************************************************************************************/

void	free_lpr_graph(lpr_Graph graph)
{
	free_lpr_nodelist_with_node_and_lower_part( lpr_graph_GET_NODES(graph) );
	free_lpr_edgelist_with_edge_and_in_source( lpr_graph_GET_IN_EMBEDDINGS(graph) );
	free_lpr_edgelist_with_edge_and_in_target( lpr_graph_GET_OUT_EMBEDDINGS(graph) );

	free( graph );
}

/************************************************************************************************/
/*												*/
/*	Funktion, um einen Graphed - graphen in unsere Datenstruktur zu kopieren		*/
/*												*/
/************************************************************************************************/

/*************************************************************************************************
function:	lpr_graph_create_copy_of_graphed_prod
Input:	Graph graphed_graph, lpr_Node

	Erzeugt eine Kopie von graphed_prod als lpr_Graph g

Output:	g
*************************************************************************************************/

lpr_Graph	lpr_graph_create_copy_of_graphed_prod(Graph graphed_prod, lpr_Node derivated_node)
     		             
        	               		/*   Wird benoetigt fuer vollst. Erzeugung der Kanten   */
{
	lpr_Graph	copy	= create_lpr_graph();

	lpr_graph_SET_NODES	( copy, lpr_node_create_copy_of_prod(graphed_prod, copy, derivated_node) );
	lpr_graph_SET_PROD_ISO	( copy, graphed_prod );

	return( copy );
}


/*************************************************************************************************
function   :    set_array_of_prod_iso_pointers      
Input      :    lpr_Graph lpr_graph;
Output     :    void
Description:    Holt sich "uber lpr_graph->PROD_iso alle zu dieser Produktion isomorphen,
		    erzeugt daraus ein Array und setzt lpr_graph->array_of_prod_iso_pointers auf
		    dieses Array. Dadurch wird der Zugriff auf verschiedene Produktionslayouts
		    effizienter. 
*************************************************************************************************/

void set_array_of_iso_prod_pointers(lpr_Graph lpr_graph)
{
	int      count = 0;                                        	/* Zum Z"ahlen isomorpher Layouts    */
	Graph    cur_graph;					   		/* Zum Durchlauf durch die Layouts   */
	Graph    *array;					   			/* Das entstehende Array             */

	for_graph_multi_suc( lpr_graph->PROD_iso, cur_graph )      	/* Z"ahle zuerst, wieviele isomorphe */ 
		count++;					   			/* Layouts es gibt, um die Gr"osse   */
	end_for_graph_multi_suc( lpr_graph->PROD_iso, cur_graph ); 	/* des Arrays berechnen zu k"onnen   */
	lpr_graph->number_of_iso_prods = count;    		   	/* Anzahl speichern                  */

	array = (Graph *) mymalloc(sizeof(Graph *) * count);       	/* Fordere ausreichend Speicher an   */
	count = 0;
	for_graph_multi_suc( lpr_graph->PROD_iso, cur_graph )      	/* und trage Layouts ins Array ein   */
		array[count++] = cur_graph;		
	end_for_graph_multi_suc( lpr_graph->PROD_iso, cur_graph );

	lpr_graph->array_of_iso_prod_pointers = array;             	/* Speichere das Array in lpr_graph  */
}

