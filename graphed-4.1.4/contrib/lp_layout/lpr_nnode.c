#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_test.h"

#include "lpr_eedge.h"
#include "lpr_ggraph.h"

#include "lpr_nnode.h"

/************************************************************************************************/
/*												*/
/*	File mit den grundlegenden Funktionen auf lpr_Node und lpr_Nodelist			*/
/*												*/
/************************************************************************************************/

/************************************************************************************************/
/******					lpr_Node					   ******/
/************************************************************************************************/

/*************************************************************************************************
function:	create_lpr_node

	Erzeugt Speicherplatz fuer lpr_Node l

Output:	Zeiger auf l
*************************************************************************************************/

lpr_Node	create_lpr_node(void)
{
	lpr_Node	new = (lpr_Node)mymalloc(sizeof( struct lpr_node));

	new->node_type			= lpr_NORMAL_NODE;
	new->GRAPH_iso			= NULL;
	new->PROD_iso			= NULL;
	new->array_of_iso_node_pointers = NULL;
	new->applied_production		= NULL;
	new->is_terminal_node		= TRUE;
	new->graph			= NULL;
	new->label			= NULL;
	new->source_edges		= NULL;
	new->target_edges		= NULL;
	new->later_source_edges		= NULL;
	new->later_target_edges		= NULL;
	new->left			= NULL;
	new->right			= NULL;
	new->up				= NULL;
	new->down			= NULL;
	new->bleft			= NULL;
	new->bright			= NULL;
	new->bup			= NULL;
	new->bdown			= NULL;
	new->flag 			= FALSE;
	new->big			= FALSE;
	new->leaf			= TRUE;
	new->father			= NULL;

	return( new );
}

/*************************************************************************************************
function:	lpr_node_SET_LATER_SOURCE_EDGES
Input:	lpr_Node node, lpr_Edgelist edges

	setzt node->later_source_edges
*************************************************************************************************/

void	lpr_node_SET_LATER_SOURCE_EDGES(lpr_Node node, lpr_Edgelist edges)
{
	node->later_source_edges = edges;
}

/*************************************************************************************************
function:	lpr_node_GET_LATER_SOURCE_EDGES
Input:	lpr_Node node

	holt node->later_source_edges
*************************************************************************************************/

lpr_Edgelist	lpr_node_GET_LATER_SOURCE_EDGES(lpr_Node node)
{
	return( node->later_source_edges );
}

/*************************************************************************************************
function:	lpr_node_SET_LATER_TARGET_EDGES
Input:	lpr_Node node, lpr_Edgelist edges

	setzt node->later_target_edges
*************************************************************************************************/

void	lpr_node_SET_LATER_TARGET_EDGES(lpr_Node node, lpr_Edgelist edges)
{
	node->later_target_edges = edges;
}

/*************************************************************************************************
function:	lpr_node_GET_LATER_TARGET_EDGES
Input:	lpr_Node node

	holt node->later_target_edges
*************************************************************************************************/

lpr_Edgelist	lpr_node_GET_LATER_TARGET_EDGES(lpr_Node node)
{
	return( node->later_target_edges );
}

/*************************************************************************************************
function:	lpr_node_SET_TARGET_EDGES
Input:	lpr_Node node, lpr_Edgelist edges

	Haengt edges an node->target_edges
*************************************************************************************************/

void	lpr_node_SET_TARGET_EDGES(lpr_Node node, lpr_Edgelist edges)
{
	node->target_edges	= edges;
}

/*************************************************************************************************
function:	lpr_node_GET_TARGET_EDGES
Input:	lpr_Node node

	Holt node->target_edges
*************************************************************************************************/

lpr_Edgelist	lpr_node_GET_TARGET_EDGES(lpr_Node node)
{
	return( node->target_edges );
}

/*************************************************************************************************
function:	lpr_node_SET_SOURCE_EDGES
Input:	lpr_Node node, lpr_Edgelist edges

	Haengt edges an node->source_edges
*************************************************************************************************/

void	lpr_node_SET_SOURCE_EDGES(lpr_Node node, lpr_Edgelist edges)
{
	node->source_edges	= edges;
}

/*************************************************************************************************
function:	lpr_node_GET_SOURCE_EDGES
Input:	lpr_Node node

	Holt node->source_edges
*************************************************************************************************/

lpr_Edgelist	lpr_node_GET_SOURCE_EDGES(lpr_Node node)
{
	return( node->source_edges );
}

/*************************************************************************************************
function:	lpr_node_SET_LABEL
Input:	lpr_Node node, char* label

	Haengt label an node->label
*************************************************************************************************/

void	lpr_node_SET_LABEL(lpr_Node node, char *label)
{
	node->label	= label;
}

/*************************************************************************************************
function:	lpr_node_GET_LABEL
Input:	lpr_Node node

	Holt node->label
*************************************************************************************************/

char*	lpr_node_GET_LABEL(lpr_Node node)
{
	return( node->label );
}

/*************************************************************************************************
function:	lpr_node_SET_GRAPH
Input:	lpr_Node node, lpr_Graph graph

	Haengt graph an node->graph
*************************************************************************************************/

void	lpr_node_SET_GRAPH(lpr_Node node, lpr_Graph graph)
{
	node->graph	= graph;
}

/*************************************************************************************************
function:	lpr_node_GET_GRAPH
Input:	lpr_Node node

	Holt node->graph
*************************************************************************************************/

lpr_Graph	lpr_node_GET_GRAPH(lpr_Node node)
{
	return( node->graph );
}

/*************************************************************************************************
function:	lpr_node_SET_IS_TERMINAL
Input:	lpr_Node node

	Setzt  node->is_terminal_node
*************************************************************************************************/

void	lpr_node_SET_IS_TERMINAL(lpr_Node node)
{
	node->is_terminal_node	= TRUE;
}


/*************************************************************************************************
function:	lpr_node_SET_IS_NON_TERMINAL
Input:	lpr_Node node

	Setzt  node->is_terminal_node zurueck
*************************************************************************************************/

void	lpr_node_SET_IS_NON_TERMINAL(lpr_Node node)
{
	node->is_terminal_node	= FALSE;
}

/*************************************************************************************************
function:	lpr_node_IS_TERMINAL
Input:	lpr_Node node

Output:	TRUE iff Knoten ist terminal markiert
	FALSE sonst
*************************************************************************************************/

int	lpr_node_IS_TERMINAL(lpr_Node node)
{
	return( node->is_terminal_node );
}

/*************************************************************************************************
function:	lpr_node_SET_APPLIED_PRODUCTION
Input:	lpr_Node node, lpr_Graph graph

	Haengt graph an node->applied_production
*************************************************************************************************/

void	lpr_node_SET_APPLIED_PRODUCTION(lpr_Node node, lpr_Graph graph)
{
	node->applied_production	= graph;
}

/*************************************************************************************************
function:	lpr_node_GET_APPLIED_PRODUCTION
Input:	lpr_Node node, lpr_Graph graph

	Holt node->applied_production
*************************************************************************************************/

lpr_Graph	lpr_node_GET_APPLIED_PRODUCTION(lpr_Node node)
{
	return( node->applied_production );
}

/*************************************************************************************************
function:	lpr_node_SET_PROD_ISO
Input:	lpr_Node node, Node prod_node

	Haengt prod_node an node->PROD_iso
*************************************************************************************************/

void	lpr_node_SET_PROD_ISO(lpr_Node node, Node prod_node)
{
	node->PROD_iso	= prod_node;
}

/*************************************************************************************************
function:	lpr_node_GET_PROD_ISO
Input:	lpr_Node node

	Holt node->PROD_iso
*************************************************************************************************/

Node	lpr_node_GET_PROD_ISO(lpr_Node node)
{
	return( node->PROD_iso );
}

/*************************************************************************************************
function:	lpr_node_SET_GRAPH_ISO
Input:	lpr_Node node, Node graph_node

	Haengt graph_node an node->GRAPH_iso
*************************************************************************************************/

void	lpr_node_SET_GRAPH_ISO(lpr_Node node, Node graph_node)
{
	node->GRAPH_iso	= graph_node;
}

/*************************************************************************************************
function:	lpr_node_GET_GRAPH_ISO
Input:	lpr_Node node

	holt node->GRAPH_iso
*************************************************************************************************/

Node	lpr_node_GET_GRAPH_ISO(lpr_Node node)
{
	return( node->GRAPH_iso	);
}

/*************************************************************************************************
function:	lpr_node_SET_NODETYPE
Input:	lpr_Node node, lpr_NODETYPE type

	Haengt type an node->node_type
*************************************************************************************************/

void	lpr_node_SET_NODETYPE(lpr_Node node, lpr_NODETYPE type)
{
	node->node_type	= type;
}

/*************************************************************************************************
function:	lpr_node_GET_NODETYPE
Input:	lpr_Node node

	Holt node->node_type
*************************************************************************************************/

lpr_NODETYPE	lpr_node_GET_NODETYPE(lpr_Node node)
{
	return( node->node_type	);
}

/*************************************************************************************************
function:	create_lpr_node_with_edgelists
Input:	lpr_Edgelist source_edges, target_edges

	Erzeugt lpr_Node n und setzt source_edges und target_edges

Output:	n
*************************************************************************************************/

lpr_Node	create_lpr_node_with_edgelists(lpr_Edgelist source_edges, lpr_Edgelist target_edges)
{
	lpr_Node	new = create_lpr_node();

	lpr_node_SET_TARGET_EDGES( new, target_edges );
	lpr_node_SET_SOURCE_EDGES( new, source_edges );

	return( new );
}

/*************************************************************************************************
function:	free_lpr_node
Input:	lpr_Node	node

	Loescht Speicherplatz von node (Mit Kanten die daran haengen(auch vom anderen Ende aus) )
*************************************************************************************************/

void	free_lpr_node(lpr_Node node)
{
	free_lpr_edgelist_with_edge_and_in_source( node->target_edges );
	free_lpr_edgelist_with_edge_and_in_target( node->source_edges );
	free_lpr_edgelist_with_edge_and_in_source( node->later_target_edges );
	free_lpr_edgelist_with_edge_and_in_target( node->later_source_edges );

	free( node );
}

/*************************************************************************************************
function:	free_lpr_node_with_lower_part
Input:	lpr_Node	node

	Loescht Speicherplatz von node (Mit Kanten die daran haengen(auch vom anderen Ende aus) )
	und darunterliegenden Graphen
*************************************************************************************************/

void	free_lpr_node_with_lower_part(lpr_Node node)
{
	free_lpr_edgelist_with_edge_and_in_source( node->target_edges );
	free_lpr_edgelist_with_edge_and_in_target( node->source_edges );
	free_lpr_edgelist_with_edge_and_in_source( node->later_target_edges );
	free_lpr_edgelist_with_edge_and_in_target( node->later_source_edges );

	if( lpr_node_GET_APPLIED_PRODUCTION(node) )
	{
		free_lpr_graph( lpr_node_GET_APPLIED_PRODUCTION(node) );
	}

	free( node );
}

/************************************************************************************************/
/******					lpr_Nodelist					   ******/
/************************************************************************************************/

/*************************************************************************************************
function:	create_lpr_nodelist

	Erzeugt Speicherplatz s fuer lpr_Nodelist

Output:	Zeiger auf s
*************************************************************************************************/

lpr_Nodelist	create_lpr_nodelist(void)
{
	lpr_Nodelist new = (lpr_Nodelist)mymalloc(sizeof(struct lpr_nodelist));

	new->node	= NULL;
	new->pre	= new;
	new->suc	= new;

	return( new );
}

/*************************************************************************************************
function:	lpr_nodelist_SET_NODE
Input:	lpr_Nodelist list, lpr_Node node

	Setzt list->node;
*************************************************************************************************/

void	lpr_nodelist_SET_NODE(lpr_Nodelist list, lpr_Node node)
{
	list->node = node;
}

/*************************************************************************************************
function:	lpr_nodelist_GET_NODE
Input:	lpr_Nodelist list

	Holt list->node;
*************************************************************************************************/

lpr_Node	lpr_nodelist_GET_NODE(lpr_Nodelist list)
{
	return( list->node );
}

/*************************************************************************************************
function:	create_lpr_nodelist_with_node
Input:	lpr_Node node
	Erzeugt Speicherplatz s fuer lpr_Nodelist

Output:	Zeiger auf s
*************************************************************************************************/

lpr_Nodelist	create_lpr_nodelist_with_node(lpr_Node node)
{
	lpr_Nodelist new = create_lpr_nodelist();

	new->node	= node;

	return( new );
}

/*************************************************************************************************
function:	add_nodelist_to_lpr_nodelist
Input:	lpr_Nodelist old_list, new

	Haengt new hinten an old_list an

Output:	Zeiger auf 1. Listenelement
*************************************************************************************************/

lpr_Nodelist	add_nodelist_to_lpr_nodelist(lpr_Nodelist old_list, lpr_Nodelist new)
{
	if( old_list == NULL )
	{
		return( new );
	}

	new->suc		= old_list;
	new->pre		= old_list->pre;
	old_list->pre->suc	= new;
	old_list->pre		= new;

	return( old_list );
}

/*************************************************************************************************
function:	add_node_to_lpr_nodelist
Input:	lpr_Nodelist old_list, lpr_Node new

	Haengt new hinten an old_list an

Output:	Zeiger auf 1. Listenelement
*************************************************************************************************/

lpr_Nodelist	add_node_to_lpr_nodelist(lpr_Nodelist old_list, lpr_Node new)
{
	return( add_nodelist_to_lpr_nodelist(old_list, create_lpr_nodelist_with_node(new)) );
}

/*************************************************************************************************
function:	free_lpr_nodelist
Input:	lpr_Nodelist list

	loescht Speicherplatz von list (ohne ->node)
*************************************************************************************************/

void	free_lpr_nodelist(lpr_Nodelist list)
{
	lpr_Nodelist	to_delete;

	if( list )
	{
		list->pre->suc = NULL;

		while( list )
		{
			to_delete = list;
			list = list->suc;

			free( to_delete );
		}
	}
}

/*************************************************************************************************
function:	free_lpr_nodelist_with_node
Input:	lpr_Nodelist list

	loescht Speicherplatz von list (mit ->node)
*************************************************************************************************/

void	free_lpr_nodelist_with_node(lpr_Nodelist list)
{
	lpr_Nodelist	to_delete;

	if( list )
	{
		list->pre->suc = NULL;

		while( list )
		{
			to_delete = list;
			list = list->suc;

			free_lpr_node( lpr_nodelist_GET_NODE(to_delete) );
			free( to_delete );
		}
	}
}

/*************************************************************************************************
function:	free_lpr_nodelist_with_node_and_lower_part
Input:	lpr_Nodelist list

	loescht Speicherplatz von list (mit ->node und node->applied_production)
*************************************************************************************************/

void	free_lpr_nodelist_with_node_and_lower_part(lpr_Nodelist list)
{
	lpr_Nodelist	to_delete;

	if( list )
	{
		list->pre->suc = NULL;

		while( list )
		{
			to_delete = list;
			list = list->suc;

			free_lpr_node_with_lower_part( lpr_nodelist_GET_NODE(to_delete) );
			free( to_delete );
		}
	}
}


/*************************************************************************************************
Function   : lpr_node_SET_ARRAY_OF_ISO_NODE_POINTERS
Input      : lpr_node node
Output     : void
Description: Holt sich "uber node->PROD_iso alle zu diesem Knoten isomorphen, erzeugt daraus
             ein Array und setzt node->array_of_iso_node_pointers auf dieses Array. Dadurch
	     wird der Zugriff auf verschiedene Knotenlayouts eines Knoten effizienter. 
*************************************************************************************************/

void lpr_node_SET_ARRAY_OF_ISO_NODE_POINTERS(lpr_Node node)
{
	int      count = 0;                                        /* Zum Z"ahlen isomorpher Layouts    */
	Node     cur_node;					   /* Zum Durchlauf durch die Layouts   */
	Node     *array;					   /* Das entstehende Array             */

	for_node_multi_suc( node->PROD_iso, cur_node )             /* Z"ahle zuerst, wieviele isomorphe */ 
		count++;					   /* Layouts es gibt, um die Gr"osse   */
	end_for_node_multi_suc( node->PROD_iso, cur_node );        /* des Arrays berechnen zu k"onnen   */

	array = (Node *) mymalloc(sizeof(Node *) * count);         /* Fordere ausreichend Speicher an   */
	count = 0;
	for_node_multi_suc( node->PROD_iso, cur_node )             /* und trage Layouts ins Array ein   */
		array[count++] = cur_node;		
	end_for_node_multi_suc( node->PROD_iso, cur_node );

	node->array_of_iso_node_pointers = array;                  /* Speichere das Array in node       */
}

/************************************************************************************************/
/*												*/
/* Funktionen um eine Kopie von Knoten von Graped - prod zu erzeugen				*/
/*												*/
/************************************************************************************************/

/*************************************************************************************************
function:	lpr_node_create_copy_of_prod_nodes
Input:	Graph graphed_prod, lpr_Graph lpr_graph

	Erzeugt eine Kopie von den Knoten von graphed_prod. Geht davon aus, dass von Knoten der 
	Produktion graphed-Kopierzeiger auf entsprechung im Graphen zeigen (fuer ->GRAPH_iso);

Output:	Zeiger auf Kopie
*************************************************************************************************/

lpr_Nodelist	lpr_node_create_copy_of_prod_nodes(Graph graphed_prod, lpr_Graph lpr_graph, lpr_Node father)
{
	Node		LHS_node	= graphed_prod->gra.gra.nce1.left_side->node;
	lpr_Nodelist	result		= NULL;
	Group		cur_group, g;
	lpr_Node	new_node;

	cur_group = graphed_prod->gra.gra.nce1.right_side;

	for_group (cur_group, g )
	{
		if( g->node != LHS_node )
		{
			new_node = create_lpr_node();

			lpr_node_SET_LABEL	( new_node, g->node->label.text		);
			lpr_node_SET_GRAPH	( new_node, lpr_graph			);
			lpr_node_SET_IS_TERMINAL( new_node				);
			lpr_node_SET_PROD_ISO	( new_node, g->node			);
			lpr_node_SET_ARRAY_OF_ISO_NODE_POINTERS(new_node            	);
			lpr_node_SET_GRAPH_ISO	( new_node, g->node->iso		);
			lpr_node_SET_NODETYPE	( new_node, lpr_NORMAL_NODE		);

			new_node->father = father;

			/****** Kopierzeiger setzen							******/
			g->node->lp_node.copy_iso	= new_node;	/* von Prod aus */

			/****** Setze Zeiger vom Graph - Knoten zum lpr_Knoten				******/
			g->node->iso->lp_node.corresponding_lpr_node = new_node;

			result = add_node_to_lpr_nodelist( result, new_node );
		}
	}
	end_for_group( cur_group, g );

	return( result );
}

/*************************************************************************************************
function:	lpr_node_create_copy_of_prod
Input:	Graph graphed_prod, lpr_Graph lpr_graph, lpr_Node derivated_node

	Erzeugt eine Kopie von den Knoten von graphed_prod. Geht davon aus, dass von Knoten der 
	Produktion graphed-Kopierzeiger auf entsprechung im Graphen zeigen (fuer ->GRAPH_iso);

Output:	Zeiger auf Kopie
*************************************************************************************************/

lpr_Nodelist	lpr_node_create_copy_of_prod(Graph graphed_prod, lpr_Graph lpr_graph, lpr_Node derivated_node)
{
	lpr_Nodelist	result;

	/****** 1. Kopie der Knoten der Produktion erzeugen						******/
	/****** 2. Kopie der Kanten der Produktion erzeugen						******/
	/****** 3. Kanten erzeugen, die dadurch entstehen, dass abgeleiteter Knoten auch Kanten hatte	******/

	result = lpr_node_create_copy_of_prod_nodes( graphed_prod, lpr_graph, derivated_node );

	lpr_edge_create_copy_of_prod_edges	( graphed_prod, lpr_graph 			);

	/***************/
	lpr_edge_create_embedding_edges		( lpr_graph, derivated_node			);
	/***************/
	return( result );
}













