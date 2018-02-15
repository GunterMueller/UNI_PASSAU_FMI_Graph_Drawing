#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lp_general_functions.h"
#include "lpp_redraw.h"
#include "lp_draw_tree.h"
#include "lp_edgeline.h"

/***************************************************************************************************
function:	lp_scale_down
Input:	Graph graph

	Zeichnet graph halb so gross
***************************************************************************************************/

void	lp_scale_down	(Graph graph)
{
	Node	cur_node;
	Edge	cur_edge;
	lp_Edgeline	cur_edgeline;

	for_nodes( graph, cur_node )
	{
		for_edge_sourcelist( cur_node, cur_edge )
		{
			cur_edge->lp_edge.old_lp_line = cur_edge->lp_edge.lp_line;
			cur_edge->lp_edge.lp_line = cp_edgeline_to_lp_edgeline( cur_edge->line );

			for_lp_edgeline (cur_edge->lp_edge.lp_line, cur_edgeline) 
			{
				cur_edgeline->x /= 2;
				cur_edgeline->y /= 2;
			} 
			end_for_lp_edgeline (cur_edge->lp_edge.lp_line, cur_edgeline);

			for_lp_edgeline (cur_edge->lp_edge.old_lp_line, cur_edgeline) 
			{
				cur_edgeline->x /= 2;
				cur_edgeline->y /= 2;
			} 
			end_for_lp_edgeline (cur_edge->lp_edge.old_lp_line, cur_edgeline);
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( graph, cur_node );

	for_nodes( graph, cur_node )
	{
		node_set( cur_node, ONLY_SET, 
			/* NODE_NEI, NO_NODE_EDGE_INTERFACE, */
			NODE_POSITION, cur_node->x / 2, cur_node->y / 2, 
			NODE_SIZE, cur_node->box.r_width / 2, cur_node->box.r_height / 2, 0 );
	}
	end_for_nodes( graph, cur_node );

	for_nodes( graph, cur_node )
	{
		for_edge_sourcelist( cur_node, cur_edge )
		{

			edge_set( cur_edge, ONLY_SET, EDGE_LINE, cp_lp_edgeline_to_edgeline(cur_edge->lp_edge.lp_line), 0 );

			free_lp_edgeline( cur_edge->lp_edge.lp_line );

			cur_edge->lp_edge.lp_line = cur_edge->lp_edge.old_lp_line;
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( graph, cur_node );


	restore_graph( graph );
	graph->lp_graph.current_size = graph->lp_graph.current_size / 2;
	graph->lp_graph.changed = FALSE;
}
	

/***************************************************************************************************
function:	lp_scale_down_proc

	Sucht selektierten graphen und zeichnet ihn halb so gross
***************************************************************************************************/
			
void	lp_scale_down_proc	(void)
{
	Graph	graph = empty_graph;

	graph = compute_graph();

	if( graph )
	{
		lp_scale_down( graph );
	}
}


/***************************************************************************************************
function:	lp_scale_up
Input:	Graph graph

	Zeichnet graph doppelt so gross
***************************************************************************************************/

void	lp_scale_up	(Graph graph)
{
	Node	cur_node;
	Edge	cur_edge;
	lp_Edgeline	cur_edgeline;

	for_nodes( graph, cur_node )
	{
		for_edge_sourcelist( cur_node, cur_edge )
		{
			cur_edge->lp_edge.old_lp_line = cur_edge->lp_edge.lp_line;
			cur_edge->lp_edge.lp_line = cp_edgeline_to_lp_edgeline( cur_edge->line );

			for_lp_edgeline (cur_edge->lp_edge.lp_line, cur_edgeline) 
			{
				cur_edgeline->x *= 2;
				cur_edgeline->y *= 2;
			} 
			end_for_lp_edgeline (cur_edge->lp_edge.lp_line, cur_edgeline);

			for_lp_edgeline (cur_edge->lp_edge.old_lp_line, cur_edgeline) 
			{
				cur_edgeline->x *= 2;
				cur_edgeline->y *= 2;
			} 
			end_for_lp_edgeline (cur_edge->lp_edge.old_lp_line, cur_edgeline);
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( graph, cur_node );

	for_nodes( graph, cur_node )
	{
		node_set( cur_node, ONLY_SET, 
			/* NODE_NEI, NO_NODE_EDGE_INTERFACE, */
			NODE_POSITION, cur_node->x * 2, cur_node->y * 2, 
			NODE_SIZE, cur_node->box.r_width * 2, cur_node->box.r_height * 2, 0 );
	}
	end_for_nodes( graph, cur_node );

	for_nodes( graph, cur_node )
	{
		for_edge_sourcelist( cur_node, cur_edge )
		{

			edge_set( cur_edge, ONLY_SET, EDGE_LINE, cp_lp_edgeline_to_edgeline(cur_edge->lp_edge.lp_line), 0 );

			free_lp_edgeline( cur_edge->lp_edge.lp_line );
			cur_edge->lp_edge.lp_line = cur_edge->lp_edge.old_lp_line;
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( graph, cur_node );


	restore_graph( graph );
	graph->lp_graph.current_size = graph->lp_graph.current_size * 2;
	graph->lp_graph.changed = FALSE;
}
	

/***************************************************************************************************
function:	lp_scale_up_proc

	Sucht selektierten graphen und zeichnet ihn doppelt so gross
***************************************************************************************************/
			
void	lp_scale_up_proc	(void)
{
	Graph	graph = empty_graph;

	graph = compute_graph();
	if( graph )
	{
		lp_scale_up( graph );
	}
}





