#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_test.h"
#include "lpm_iso_test.h"

/**********************************************************************
struct	label_rec

	
**********************************************************************/

typedef	struct label_rec	
	{
		char			*source_label;
		char			*target_label;
		char			*edge_label;
		int			nr;
		struct label_rec	*next;
	}
	* label_list;

/*****************************************************************************
function:	append_to_isomorph graphs
Input:	Graph p1, p2

	Append p2 at the end of graphs beginning with p1
*****************************************************************************/

void	append_to_isomorph_graphs(Graph p1, Graph p2)
{
	Node	cur_node;
	Edge	cur_edge;

	/* append the graphs */
	p2->lp_graph.multi_pre 				= p1->lp_graph.multi_pre;
	p2->lp_graph.multi_suc 				= p1;
	p1->lp_graph.multi_pre->lp_graph.multi_suc	= p2;
	p1->lp_graph.multi_pre				= p2;

	/* append all nodes and all edges */
	for_nodes( p1, cur_node )
	{
		cur_node->lp_node.multi_iso->lp_node.multi_pre 			= cur_node->lp_node.multi_pre;
		cur_node->lp_node.multi_iso->lp_node.multi_suc	 		= cur_node;
		cur_node->lp_node.multi_pre->lp_node.multi_suc 			= cur_node->lp_node.multi_iso;
		cur_node->lp_node.multi_pre			= cur_node->lp_node.multi_iso;
		cur_node->lp_node.multi_iso->lp_node.multi_iso	= NULL;
		cur_node->lp_node.multi_iso           	 	= NULL;
		for_edge_sourcelist( cur_node, cur_edge )
		{
			cur_edge->lp_edge.multi_iso->lp_edge.multi_pre = cur_edge->lp_edge.multi_pre;
			cur_edge->lp_edge.multi_iso->lp_edge.multi_suc = cur_edge;
			cur_edge->lp_edge.multi_pre->lp_edge.multi_suc = cur_edge->lp_edge.multi_iso;
			cur_edge->lp_edge.multi_pre	      	 	= cur_edge->lp_edge.multi_iso;
			cur_edge->lp_edge.multi_iso->lp_edge.multi_iso  = NULL;
			cur_edge->lp_edge.multi_iso    		        = NULL;
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( p1, cur_node );
}
/*****************************************************
function:	reset_iso
Input:	Graph	p

	For all nodes and edges of p:
	element->multi_iso = NULL
*****************************************************/

void	reset_iso(Graph p)
{
	Node	cur_node;
	Edge	cur_edge;


	p->lp_graph.multi_pre = p;
	p->lp_graph.multi_suc = p;

	for_nodes( p, cur_node)
	{
		cur_node->lp_node.multi_iso = NULL;
		cur_node->lp_node.multi_pre = cur_node;
		cur_node->lp_node.multi_suc = cur_node;
		for_edge_sourcelist( cur_node, cur_edge)
		{
			cur_edge->lp_edge.multi_iso = NULL;
			cur_edge->lp_edge.multi_pre = cur_edge;
			cur_edge->lp_edge.multi_suc = cur_edge;
		}
		end_for_edge_sourcelist( cur_node, cur_edge);
	}
	end_for_nodes( p, cur_node);
}



void	reset_iso_in_all_productions(void)
{
	Graph	graph;
	int	buffer;
	

	for( buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++ ) 
	{
		for_all_graphs( buffer, graph )
		{
			if ( graph->is_production )
			{
				reset_iso( graph );
			}
		} 
		end_for_all_graphs( buffer, graph );
	}
}


/****************************************************
function:	found_corresponding_source_edge
Input:	Edge e, Node n

Output: TRUE iff. there is a corresponding edge to e in n
*****************************************************/

int	found_corresponding_source_edge(Edge e, Node n)
{
	Edge	cur_edge;
	Node	lhs_node_1 = n->graph->gra.gra.nce1.left_side->node;
	Node	lhs_node_2 = e->target->graph->gra.gra.nce1.left_side->node;

	for_edge_sourcelist( n, cur_edge)
	{								/* not necessary to compare source 			*/
									/* 1. edge_label					*/
									/* 2. target node_label					*/
									/* 3. both are outembeddings or RHS- edges		*/
									/* 4. target nodes are connected by a iso-pointer	*/
		if( my_strcmp( e->label.text, cur_edge->label.text) &&
		    my_strcmp( e->target->label.text, cur_edge->target->label.text) &&
		    (cur_edge->target == e->target->lp_node.multi_iso) &&
		    ( (inside(e->target->x, e->target->y, lhs_node_2) && inside(cur_edge->target->x, cur_edge->target->y, lhs_node_1)) || 
		      (!inside(e->target->x, e->target->y, lhs_node_2) && !inside(cur_edge->target->x, cur_edge->target->y, lhs_node_2)) 
		    ) &&
		    (e->target->lp_node.multi_iso == cur_edge->target)
		  )
		{
			cur_edge->lp_edge.multi_iso = e;
			e->lp_edge.multi_iso = cur_edge;
			return( TRUE );
		}
	}
	end_for_edge_sourcelist( n, cur_edge);

	return( FALSE );
}

/*****************************************************
function:	found_corresponding_target_edge
Input:	Edge e, Node n

Output: TRUE iff. there is a corresponding edge to e in n
*****************************************************/

int	found_corresponding_target_edge(Edge e, Node n)
{
	Edge	cur_edge;
	Node	lhs_node_1 = n->graph->gra.gra.nce1.left_side->node;
	Node	lhs_node_2 = e->target->graph->gra.gra.nce1.left_side->node;

	for_edge_targetlist( n, cur_edge)
	{								/* not necessary to compare target 			*/
									/* 1. edge_label					*/
									/* 3. both are inembeddings or RHS- edges		*/
									/* 4. source nodes are connected by a iso-pointer	*/
		if( my_strcmp( e->label.text, cur_edge->label.text) &&
		    (cur_edge->source == e->source->lp_node.multi_iso) &&
		    ( (inside(e->source->x, e->source->y, lhs_node_2) && inside(cur_edge->source->x, cur_edge->source->y, lhs_node_1)) || 
		      (!inside(e->source->x, e->source->y, lhs_node_2) && !inside(cur_edge->source->x, cur_edge->source->y, lhs_node_2)) 
		    ) 
		  )
		{
			cur_edge->lp_edge.multi_iso = e;
			e->lp_edge.multi_iso = cur_edge;
			return( TRUE );
		}
	}
	end_for_edge_targetlist( n, cur_edge);

	return( FALSE );
}

/*****************************************************
function:	isomorphism_test
Input:	Graph	p1, p2

Output:	TRUE iff. p1 is isomorph to p2 by given iso-pointers
	FALSE otherwise
*****************************************************/

int	isomorphism_test(Graph p1, Graph p2)
{
	Edge		cur_edge;
	Node		cur_node;
	for_nodes( p1, cur_node )
	{
			for_edge_sourcelist( cur_node, cur_edge)
			{
				if(!found_corresponding_source_edge( cur_edge, cur_node->lp_node.multi_iso))
				{
					return( FALSE );
				}
			}
			end_for_edge_sourcelist( cur_node, cur_edge);

			for_edge_targetlist( cur_node, cur_edge)
			{
				if(!found_corresponding_target_edge( cur_edge, cur_node->lp_node.multi_iso))
				{
					return( FALSE );
				}
			}
			end_for_edge_targetlist( cur_node, cur_edge );
	}
	end_for_nodes( p1, cur_node );
	return( TRUE );
}


/*****************************************************
function:	test_permutations
Input:	Nodelist l1, l2, Graph p1, p2

	get all possible injections from l1 in l2 and 
	test them to be isomorph

Output:	TRUE iff. p1 is isomorph to p2
	FALSE otherwise
*****************************************************/

int	test_permutations(Graph p1, Graph p2, Node first_node, Node n1)
{
	Node	n2;
	int	result;

	for_nodes( p2, n2 )
	{	
		if( n2->lp_node.multi_iso == NULL )
		{
			if( my_strcmp( n1->label.text , n2->label.text ) )
			{
				n2->lp_node.multi_iso = n1;
				n1->lp_node.multi_iso = n2;
				if( n1->suc == first_node )
				{
					result = isomorphism_test( p1, p2 );
					if( result == TRUE ) return( TRUE );
					n2->lp_node.multi_iso = NULL;
					n1->lp_node.multi_iso = NULL;
				}
				else
				{
					result = test_permutations( p1, p2, first_node, n1->suc );
					if( result == TRUE ) return( TRUE );
					n2->lp_node.multi_iso = NULL;
					n1->lp_node.multi_iso = NULL;
				}
			}
		}
	}
	end_for_nodes( p2, n2 );
	return( FALSE );
}

/**********************************************************************
function	equal_label_list_edge
Input:	label_list cur, Edge edge

	Compare cur with edge ( source-, target-, edgelabel )

Output:	TRUE 	iff cur is equal to edge
	FALSE	otherwise
**********************************************************************/

int		equal_label_list_edge	(label_list cur, Edge edge)
{
	return( ( my_strcmp( cur->source_label, edge->source->label.text ) &&
		  my_strcmp( cur->target_label, edge->target->label.text ) &&
		  my_strcmp( cur->edge_label, edge->label.text ) ) );
}

/**********************************************************************
function	search_edge_in_label_list
Input:	label_list list, Edge edge

	Find in list the corresponding element e to edge

Output:	iff. e exists: Pointer to e
	otherwise: NULL
**********************************************************************/

label_list	search_edge_in_label_list(label_list list, Edge edge)
{
	label_list	cur = list;

	while ( cur )
	{
		if ( equal_label_list_edge( cur, edge ) ) 
		{
			return( cur );
		}
		else cur = cur->next;
	}
	return( NULL );
}

/**********************************************************************
function	append_to_label_list
Input:	label_list list, Edge edge

	create label_list n from edge and append list as successor
	to n

Output:	Pointer to n
**********************************************************************/

label_list	append_to_label_list	(label_list list, Edge edge)
{
	label_list	new = (label_list) mymalloc( sizeof( struct label_rec  ) );

	new->source_label = edge->source->label.text;
	new->target_label = edge->target->label.text;
	new->edge_label = edge->label.text;
	new->nr = 1;

	new->next = list;
	return( new );
}

/**********************************************************************
function	add_to_label_list
Input:	label_list list, Edge edge

	iff. there is no equal element to edge in list: append edge to list
	otherwise: in the corresponding element: nr = nr + 1

Output:	changed label_list
**********************************************************************/

label_list	add_to_label_list	(label_list list, Edge edge)
{
	label_list	found = NULL;

	found = search_edge_in_label_list(list, edge);

	if ( found == NULL )
	{
		list = append_to_label_list( list, edge );
	}
	else
	{
		found->nr = found->nr + 1;
	}
	return( list );
}

/**********************************************************************
function	make_label_list
Input:	Graph g

	Create a label_list n of all edges in g

Output:	Pointer to n
**********************************************************************/

label_list	make_label_list(Graph g)
{
	Node		node;
	Edge		edge;
	label_list	result = NULL;

	for_nodes( g, node )
	{
		for_edge_sourcelist( node, edge )
		{
			result = add_to_label_list( result, edge );
		}
		end_for_edge_sourcelist( node, edge );
	}
	end_for_nodes( g, node );
	return( result );
}

/**********************************************************************
function	free_label_list
Input:	label_list list

	free memory space of list
**********************************************************************/

void		free_label_list	(label_list list)
{
	label_list	cur, del;

	cur = list;
	while ( cur )
	{
		del = cur;
		cur = cur->next;
		free( del );
	}
}

/**********************************************************************
function	equal_label_list
Input:	label_list l1, l2

Output:	True 	iff. l1 is equal to l2 ( source-, target-, edgelabel )
	FALSE 	otherwise
**********************************************************************/

int		equal_label_list(label_list l1, label_list l2)
{	
	return( ( my_strcmp( l1->source_label, l2->source_label ) &&
		  my_strcmp( l1->target_label, l2->target_label ) &&
		  my_strcmp( l1->edge_label, l2->edge_label ) &&
		  ( l1->nr == l2->nr ) ) );
}

/**********************************************************************
function:	search_in_label_list
Input:	label_list list, element

Output:	TRUE 	iff. there is a corresponding element to element in list
	FALSE	otherwise
**********************************************************************/

int	search_in_label_list(label_list list, label_list element)
{
	label_list	cur = list;

	while ( cur )
	{
		if ( equal_label_list( cur, element ) ) 
		{
			return( TRUE );
		}
		else cur = cur->next;
	}
	return( FALSE );
}

/**********************************************************************
function	compare_label_lists
Input:	label_list list1, list2

Output:	TRUE	iff. for every element in list1 exists an equal element
		in list2
	FALSE	otherwise
**********************************************************************/

int		compare_label_lists	(label_list list1, label_list list2)
{
	label_list	cur = list1;

	while ( cur )
	{
		if ( !search_in_label_list( list2, cur ) ) return( FALSE );
		cur = cur->next;
	}
	return( TRUE );
}

/*****************************************************
function:	isomorph_graphs
Input:	Graph	p1, p2

	this function also sets all iso_pointers between both
	graphs, if they are isomorph
Output:	TRUE iff. p1 is isomorph to p2
	FALSE otherwise
*****************************************************/

int	isomorph_graphs(Graph p1, Graph p2)
{
	int		l1_length = 0;
	int		l2_length = 0;
	int		e1 = 0;
	int		e2 = 0;
	Edge		cur_edge;
	Node		cur_node;
	label_list	label_list_of_g, label_list_of_h;

	if (!my_strcmp( p1->gra.gra.nce1.left_side->node->label.text, p2->gra.gra.nce1.left_side->node->label.text ) )
		return( FALSE );

	/* create nodelist of p1 and p2 */

	for_nodes( p1, cur_node )
	{
		l1_length++;
		for_edge_sourcelist( cur_node, cur_edge )
		{
			e1++;
		}
		end_for_edge_sourcelist( cur_node, cur_edge );

		for_edge_targetlist( cur_node, cur_edge )
		{
			e1++;
		}
		end_for_edge_targetlist( cur_node, cur_edge );
	}
	end_for_nodes( p1, cur_node );

	for_nodes( p2, cur_node )
	{
		l2_length++;
		for_edge_sourcelist( cur_node, cur_edge )
		{
			e2++;
		}
		end_for_edge_sourcelist( cur_node, cur_edge );

		for_edge_targetlist( cur_node, cur_edge )
		{
			e2++;
		}
		end_for_edge_targetlist( cur_node, cur_edge );
	}
	end_for_nodes( p2, cur_node );

	if( (l1_length != l2_length) || (e1 != e2) )
		return( FALSE );

	label_list_of_g = make_label_list( p1 );
	label_list_of_h = make_label_list( p2 );
	
	if (!compare_label_lists( label_list_of_g, label_list_of_h ) )
	{ 
		free_label_list( label_list_of_g );
		free_label_list( label_list_of_h );
		return( FALSE );
	}
	else
	{
		free_label_list( label_list_of_g );
		free_label_list( label_list_of_h );
	}
	
	/* call recursive function to permute nodes */
	return( test_permutations( p1, p2, p1->firstnode, p1->firstnode) );
}
