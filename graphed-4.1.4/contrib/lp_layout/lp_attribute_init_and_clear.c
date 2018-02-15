#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_3_pass.h"
#include "lp_4_pass.h"
#include "lp_history.h"
#include "lp_edgeline.h"
#include "lp_attribute_init_and_clear.h"

/************************************************************************/
/*									*/
/*	modul lp_attribute_init_and_clear.c				*/
/*									*/
/************************************************************************/

/*************************************************************************
function:	init_attributes_of_history_edge_ref
Input:	history_edge_ref ref

	Init the attributes of ref
*************************************************************************/

void	init_attributes_of_history_edge_ref(history_edge_ref ref)
{
	ref->edge_split_nr = 0;
}

/*************************************************************************
function:	init_attributes_of_tree_node_ref
Input:	tree_node_ref ref

	Init the attributes of ref
*************************************************************************/
 
void	init_attributes_of_tree_node_ref(tree_node_ref ref)
{
	int i;
	
	for(i=0;i<9;i++) ref->orientation_type_costs[i] = 0;
	ref->orientation_type = 1;
	for(i=0;i<4;i++) ref->max_ord[i] = 0;
	for(i=0;i<4;i++) ref->tsncr[i] = NULL;
	for(i=0;i<4;i++) ref->tncr[i] = NULL;
	for(i=0;i<4;i++) ref->track_quantity[i] = 0;

	ref->W = 0;
	ref->H = 0;
	ref->W1 = 0;
	ref->H1 = 0;
	ref->w = 0;
	ref->h = 0;
	ref->x1 = 0;
	ref->y1 = 0;
	ref->x2 = 0;
	ref->y2 = 0;
}	

/*************************************************************************
function:	init_attributes_of_tree_edge_ref
Input:	tree_edge_ref ref

	Init the attributes of ref
*************************************************************************/

void	init_attributes_of_tree_edge_ref(tree_edge_ref ref)
{
	int i, j;

	ref->split_nr = 0;
	for(i=0;i<4;i++) ref->split[i] = 0;
	ref->source_dir = 0;
	ref->target_dir = 0;
	for(i=0;i<4;i++) ref->directions[i] = NULL;
	for(i=0;i<4;i++) ref->sides[i] = NULL;
	ref->lp_ord = 0;
	for(i=0;i<4;i++) ref->clock_wise[i] = 0;
	for(i=0;i<4;i++)
	{
		for(j=0;j<4;j++)
		{
			ref->interval[i][j].left=0;
			ref->interval[i][j].right=0;
		}
	}
	for(i=0;i<4;i++) 
	{
		ref->track_interval[i].left = 0;
		ref->track_interval[i].right = 0;
	}
	for(i=0;i<4;i++) ref->track_number[i] = 0;
	ref->line = NULL;
}	

/*************************************************************************
function:	clear_attributes_of_tree_top_sort_ref
Input:	tree_top_sort_ref ref

	Clear the attributes of ref
*************************************************************************/

void	clear_attributes_of_tree_top_sort_ref(tree_top_sort_ref ref)
{
	tree_top_sort_ref	cur = ref;

	while ( cur != NULL )
	{
		cur->mL = 0;
		cur->mLS = 0;
		cur->mB = 0;
		cur->mBS = 0;
	
		cur = cur->next_x;
	}
}

/*************************************************************************
function:	clear_attributes_of_tree_node_ref
Input:	tree_node_ref ref

	Clear the attributes of ref
*************************************************************************/

void	clear_attributes_of_tree_node_ref(tree_node_ref ref)
{
	int	i;

	for(i=0; i < 4; i++) free_conn_ref(ref->tsncr[i]);
	for(i=0; i < 4; i++) free_level_ref(ref->tncr[i]);
	clear_attributes_of_tree_top_sort_ref(ref->first_x);
	init_attributes_of_tree_node_ref( ref );
}

/*************************************************************************
function:	clear_attributes_of_tree_lp_edgeline
Input:	tree_lp_edgeline_ref ref

	Clear the attributes of ref
*************************************************************************/

void 	clear_attributes_of_tree_lp_edgeline(tree_lp_edgeline_ref ref)
{
	tree_lp_edgeline_ref	cur;

	if ( ref != NULL )
	{
		cur = ref;
		do
		{
			cur->x = 0;
			cur->y = 0;
			cur = cur->suc;
		}
		while ( cur != ref );
	}
}

/*************************************************************************
function:	clear_attributes_of_tree_edge_ref
Input:	tree_edge_ref ref

	Clear the attributes of ref
*************************************************************************/

void	clear_attributes_of_tree_edge_ref(tree_edge_ref ref)
{
	int	i;

	clear_attributes_of_tree_lp_edgeline( ref->tree_line );
	for(i=0; i < 4; i++) free_dir_ref(ref->directions[i]);
	for(i=0; i < 4; i++) free_side_ref(ref->sides[i]);
	free_lp_edgeline( ref->line );
	init_attributes_of_tree_edge_ref( ref );
}

/*************************************************************************
function:	clear_attributes_of_history_edge_ref
Input:	history_edge_ref ref

	Clear the attributes of ref
*************************************************************************/

void 	clear_attributes_of_history_edge_ref(history_edge_ref ref)
{
	init_attributes_of_history_edge_ref( ref );
}

/*************************************************************************
function:	clear_attributes
Input:	tree_ref father

	Clear the attributes of father and all sons
*************************************************************************/

void		clear_attributes	(tree_ref father)
{
	history_edge_ref	cur_edge, first_edge;
	tree_ref		cur;

	clear_attributes_of_tree_node_ref( father->tree_rec.node );

	cur = father->tree_rec.node->first_son;

	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
			clear_attributes( cur );
		}
		else
		{
			clear_attributes_of_tree_edge_ref( cur->tree_rec.history_elem );
			first_edge = cur->tree_rec.history_elem->out_edges;
			if ( first_edge != NULL )
			{
				cur_edge = first_edge;
				do
				{
					clear_attributes_of_history_edge_ref( cur_edge );
					cur_edge = cur_edge->out_suc;
				}
				while ( cur_edge != first_edge );
			}

		}
		cur = cur->next_brother;
	}
}

/*************************************************************************
function:	clear_lp_edgelines
Input:	Graph graph

	Free all lp_edgelines of graph
*************************************************************************/

void	clear_lp_edgelines	(Graph graph)
{
	Node	node;
	Edge	edge;

	for_nodes( graph, node )
	{
		for_edge_sourcelist( node, edge )
		{
			free_lp_edgeline( edge->lp_edge.lp_line );
			edge->lp_edge.lp_line = NULL;
		}
		end_for_edge_sourcelist( node, edge );
	}
	end_for_nodes( graph, node );
}

/*************************************************************************
function:	clear_edge_historys
Input:	Graph graph

	Loescht edge_historys aller Kanten

Output:	---
*************************************************************************/

void	clear_edge_historys(Graph graph)
{
	Node	node;
	Edge	edge;

	for_nodes( graph, node )
	{
		for_edge_sourcelist( node, edge )
		{
			free_history( edge->lp_edge.history );
			edge->lp_edge.history = NULL;
		}
		end_for_edge_sourcelist( node, edge );
	}
	end_for_nodes( graph, node );
}

