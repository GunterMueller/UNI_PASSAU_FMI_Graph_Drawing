#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_routing.h"

/***********************************************************************************
***********************************************************************************/
typedef enum
{
	IN_EDGE,
	OUT_EDGE
}	edge_type;

/***********************************************************************************
function:	line_is_coming_from_up
Input:	lp_Edgeline line, edge_type kind

Output:	TRUE iff. line is coming from the up side of the node for IN_EDGES or
		  line is going out at the down side of the node for OUT_EDGES
	FALSE otherwise
***********************************************************************************/

int	line_is_coming_from_up(lp_Edgeline line, edge_type kind)
{
	if( kind == IN_EDGE )
		return( ((line->pre->x == line->pre->pre->x) &&
		         (line->pre->pre->y > line->pre->y)) );
	if( kind == OUT_EDGE )
		return( ((line->x == line->suc->x) &&
			 (line->y < line->suc->y)) );
	return 0; /* should not be reached */
}

/***********************************************************************************
function:	line_is_coming_from_down
Input:	lp_Edgeline line, edge_type kind

Output:	TRUE iff. line is coming from the down side of the node for IN_EDGES or
		  line is going out at the down side of the node for OUT_EDGES
	FALSE otherwise
***********************************************************************************/

int line_is_coming_from_down(lp_Edgeline line, edge_type kind)
{
	if( kind == IN_EDGE )
		return( ((line->pre->x == line->pre->pre->x) &&
			 (line->pre->pre->y < line->pre->y)) );
	if( kind == OUT_EDGE )
		return( ((line->x == line->suc->x) &&
			 (line->y > line->suc->y)) );
	return 0; /* should not be reached */
}

/***********************************************************************************
function:	line_is_coming_from_left
Input:	lp_Edgeline line, edge_type kind

Output:	TRUE iff. line is coming from the left side of the node for IN_EDGES or
		  line is going out at the left side of the node for OUT_EDGES
	FALSE otherwise
***********************************************************************************/

int line_is_coming_from_left(lp_Edgeline line, edge_type kind)
{
	if( kind == IN_EDGE )
		return( ((line->pre->y == line->pre->pre->y) &&
			 (line->pre->pre->x < line->pre->x)) );
	if( kind == OUT_EDGE )
		return( ((line->y == line->suc->y) &&
			 (line->x > line->suc->x)) );
	return 0; /* should not be reached */
}

/***********************************************************************************
function:	line_is_coming_from_right
Input:	lp_Edgeline line, edge_type kind

Output:	TRUE iff. line is coming from the right side of the node for IN_EDGES or
		  line is going out at the right side of the node for OUT_EDGES
	FALSE otherwise
***********************************************************************************/

int line_is_coming_from_right(lp_Edgeline line, edge_type kind)
{
	if( kind == IN_EDGE )
		return( ((line->pre->y == line->pre->pre->y) &&
			 (line->pre->pre->x > line->pre->x)) );
	if( kind == OUT_EDGE )
		return( ((line->y == line->suc->y) &&
			 (line->x < line->suc->x)) );
	return 0; /* should not be reached */
}

/***********************************************************************************
function:	min_nodes
Input:	Graph	graph

	Minimize every node- width and height whenever it is possible
***********************************************************************************/

void	min_nodes	(Graph graph)
{
	Node		n;
	Edge		e;
	tree_node_ref	tree_node;
	lp_Edgeline	line;
	int		min_x, min_y, max_x, max_y;
	int		change_y, change_x;
	for_nodes( graph, n )
	{
	

		tree_node = n->lp_node.tree_iso->tree_rec.node;
		min_x = 100000;
		min_y = 100000;
		max_x = 0;
		max_y = 0;
		change_y = FALSE;
		change_x = FALSE;

		for_edge_sourcelist( n, e)
		{
			line = e->lp_edge.lp_line;
			if ( (line_is_coming_from_up(line, OUT_EDGE)) || 
			     (line_is_coming_from_down(line, OUT_EDGE)) )
			{
				if ( line->x < min_x ) min_x = line->x;
				if ( line->x > max_x ) max_x = line->x;
				change_x = TRUE;
			}
			/* koennte auch else setzen */
			if ( (line_is_coming_from_left(line, OUT_EDGE)) || 
			     (line_is_coming_from_right(line, OUT_EDGE)) )
			{
				if ( line->y < min_y ) min_y = line->y;
				if ( line->y > max_y ) max_y = line->y;
				change_y = TRUE;
			}
		}
		end_for_edge_sourcelist( n, e );

		for_edge_targetlist( n, e)
		{
			line = e->lp_edge.lp_line->pre;
			if ( (line_is_coming_from_up(line, IN_EDGE)) || 
			     (line_is_coming_from_down(line, IN_EDGE)) )
			{
				if ( line->x < min_x ) min_x = line->x;
				if ( line->x > max_x ) max_x = line->x;
				change_x = TRUE;
			}
			/* koennte auch else setzen */
			if ( (line_is_coming_from_left(line, IN_EDGE)) || 
			     (line_is_coming_from_right(line, IN_EDGE)) )
			{
				if ( line->y < min_y ) min_y = line->y;
				if ( line->y > max_y ) max_y = line->y;
				change_y = TRUE;
			}
		}
		end_for_edge_targetlist( n, e );

		if( change_x )
		{
			tree_node->x1 = min_x;
			tree_node->x2 = max_x;
		}
		if( change_y )
		{
			tree_node->y1 = min_y;
			tree_node->y2 = max_y;
		}
	}
	end_for_nodes( graph, n );
}


/****************************************************************************************/

void	min_nodes_add_bends	(Graph graph)
{
	Node		n;
	Edge		e;
	tree_node_ref	tree_node;
	lp_Edgeline	line;
	int		min_x, min_y, max_x, max_y;

	for_nodes( graph, n )
	{
		int	edge_exists = 0;
	

		tree_node = n->lp_node.tree_iso->tree_rec.node;
		min_x = 100000;
		min_y = 100000;
		max_x = 0;
		max_y = 0;

		for_edge_sourcelist( n, e)
		{
			line = e->lp_edge.lp_line;
			if ( line->x < min_x ) min_x = line->x;
			if ( line->x > max_x ) max_x = line->x;
			if ( line->y < min_y ) min_y = line->y;
			if ( line->y > max_y ) max_y = line->y;
			edge_exists = 1;
		}
		end_for_edge_sourcelist( n, e );

		for_edge_targetlist( n, e)
		{
			line = e->lp_edge.lp_line->pre;
			if ( line->x < min_x ) min_x = line->x;
			if ( line->x > max_x ) max_x = line->x;
			if ( line->y < min_y ) min_y = line->y;
			if ( line->y > max_y ) max_y = line->y;
			edge_exists = 1;
		}
		end_for_edge_targetlist( n, e );

		if ( edge_exists )
		{
			if ( min_x == max_x )
			{
				if ( min_y != max_y )
				{
					for_edge_sourcelist( n , e )
					{
						line = e->lp_edge.lp_line;
						if ( line->y != min_y )
							e->lp_edge.lp_line = concat_lp_edgelines( new_lp_edgeline( min_x, min_y ), e->lp_edge.lp_line );
					}
					end_for_edge_sourcelist( n, e );
					
					for_edge_targetlist( n , e )
					{
						line = e->lp_edge.lp_line->pre;
						if ( line->y != min_y )
							e->lp_edge.lp_line = add_to_lp_edgeline( e->lp_edge.lp_line, min_x, min_y );
					}
					end_for_edge_targetlist( n, e );
				}
				tree_node->x1 = min_x;
				tree_node->x2 = max_x;
				tree_node->y1 = min_y;
				tree_node->y2 = min_y;
			}
			else
			{
				if ( max_y == min_y )
				{
					if ( min_x != max_x )
					{
						for_edge_sourcelist( n , e )
						{
							line = e->lp_edge.lp_line;
							if ( line->x != min_x )
								e->lp_edge.lp_line = concat_lp_edgelines( new_lp_edgeline(min_x, min_y), e->lp_edge.lp_line );
						}
						end_for_edge_sourcelist( n, e );
	
						for_edge_targetlist( n , e )
						{
							line = e->lp_edge.lp_line->pre;
							if ( line->x != min_x )
								e->lp_edge.lp_line = add_to_lp_edgeline( e->lp_edge.lp_line, min_x, min_y );
						}
						end_for_edge_targetlist( n, e );
					}
					tree_node->x1 = min_x;
					tree_node->x2 = min_x;
					tree_node->y1 = min_y;
					tree_node->y2 = max_y;
				}
				else
				{
					tree_node->x1 = min_x;
					tree_node->x2 = max_x;
					tree_node->y1 = min_y;
					tree_node->y2 = max_y;
				}
			}
		}
	}
	end_for_nodes( graph, n );
}


void	minimize_edge_routings	(Graph graph)
{
	Node		n;
	Edge		e;
	tree_node_ref	tree_node, target_tree_node;
	int		x1, y1, x2, y2;
	int		target_x1, target_y1, target_x2, target_y2;
	lp_Edgeline	line;

	for_nodes(graph, n )
	{
		tree_node = n->lp_node.tree_iso->tree_rec.node;
		x1 = tree_node->x1;
		y1 = tree_node->y1;
		x2 = tree_node->x2;
		y2 = tree_node->y2;

		for_edge_sourcelist(n, e)
		{
			line = e->lp_edge.lp_line;
			if ( line->suc->suc != line )
			{
				if ( horizontal_lp_edgeline_segment( line, line->suc ) )
				{
					if ( lp_dir( line->suc, line->suc->suc ) == U_dir )
					{
						line->y = y2;
						line->suc->y = y2;
					}
					else
					{
						line->y = y1;
						line->suc->y = y1;
					}
				}
				else
				{
					if ( lp_dir( line->suc, line->suc->suc ) == R_dir )
					{
						line->x = x2;
						line->suc->x = x2;
					}
					else
					{
						line->x = x1;
						line->suc->x = x1;
					}
				}
				target_tree_node = e->target->lp_node.tree_iso->tree_rec.node;
				target_x1 = target_tree_node->x1;
				target_y1 = target_tree_node->y1;
				target_x2 = target_tree_node->x2;
				target_y2 = target_tree_node->y2;

				if ( horizontal_lp_edgeline_segment( line->pre, line->pre->pre ) )
				{
					if ( lp_dir( line->pre->pre->pre, line->pre->pre ) == U_dir )
					{
						line->pre->y = target_y1;
						line->pre->pre->y = target_y1;
					}
					else
					{
						line->pre->y = target_y2;
						line->pre->pre->y = target_y2;
					}
				}
				else
				{
					if ( lp_dir( line->pre->pre->pre, line->pre->pre ) == R_dir )
					{
						line->pre->x = target_x1;
						line->pre->pre->x = target_x1;
					}
					else
					{
						line->pre->x = target_x2;
						line->pre->pre->x = target_x2;
					}
				}
			}
		}
		end_for_edge_sourcelist(n, e);
	}
	end_for_nodes(graph, n);
}


void	level_edge_routings	(Graph graph)
{
	Node		n;
	Edge		e;
	tree_node_ref	tree_node, target_tree_node;
	lp_Edgeline	line;
	int		min_x, min_y, max_x, max_y;
	int		target_x1, target_y1, target_x2, target_y2;
	int		sum_x, sum_y;

	for_nodes(graph, n )
	{
		tree_node = n->lp_node.tree_iso->tree_rec.node;
		min_x = tree_node->x1;
		min_y = tree_node->y1;
		max_x = tree_node->x2;
		max_y = tree_node->y2;

		for_edge_sourcelist(n, e)
		{
			line = e->lp_edge.lp_line;
			target_tree_node = e->target->lp_node.tree_iso->tree_rec.node;

			if ( line->suc->suc == line )
			{
				if ( horizontal_lp_edgeline_segment( line, line->suc ) )
				{
					target_y1 = target_tree_node->y1;
					target_y2 = target_tree_node->y2;

					if ( target_y1 > min_y ) min_y = target_y1;
					if ( target_y2 < max_y ) max_y = target_y2;
				}
				else
				{
					target_x1 = target_tree_node->x1;
					target_x2 = target_tree_node->x2;

					if ( target_x1 > min_x ) min_x = target_x1;
					if ( target_x2 < max_x ) max_x = target_x2;
				}
			}
		}
		end_for_edge_sourcelist(n, e);

		for_edge_targetlist(n, e)
		{
			line = e->lp_edge.lp_line;
			target_tree_node = e->source->lp_node.tree_iso->tree_rec.node;

			if ( line->suc->suc == line )
			{
				if ( horizontal_lp_edgeline_segment( line, line->suc ) )
				{
					target_y1 = target_tree_node->y1;
					target_y2 = target_tree_node->y2;

					if ( target_y1 > min_y ) min_y = target_y1;
					if ( target_y2 < max_y ) max_y = target_y2;
				}
				else
				{
					target_x1 = target_tree_node->x1;
					target_x2 = target_tree_node->x2;

					if ( target_x1 > min_x ) min_x = target_x1;
					if ( target_x2 < max_x ) max_x = target_x2;
				}
			}	
		}
		end_for_edge_targetlist(n, e);

		tree_node->x1 = min_x;
		tree_node->y1 = min_y;
		tree_node->x2 = max_x;
		tree_node->y2 = max_y;

		sum_x = tree_node->x1 + tree_node->x2;
		if ( ( sum_x % 64 ) == 0 ) sum_x = sum_x / 2;
		else sum_x = sum_x / 2 + 16;
		sum_y = tree_node->y1 + tree_node->y2;
		if ( ( sum_y % 64 ) == 0 ) sum_y = sum_y / 2;
		else sum_y = sum_y / 2 + 16;

		for_edge_sourcelist(n, e)
		{
			if ( e->lp_edge.history == e->lp_edge.history->suc )
			{
				line = e->lp_edge.lp_line;
				target_tree_node = e->target->lp_node.tree_iso->tree_rec.node;

				if ( horizontal_lp_edgeline_segment( line, line->suc ) )
				{
					line->y = sum_y;
					line->suc->y = sum_y;
				}
				else
				{
					line->x = sum_x;
					line->suc->x = sum_x;
				}
			}
		}
		end_for_edge_sourcelist(n, e);

		for_edge_targetlist(n, e)
		{
			if ( e->lp_edge.history == e->lp_edge.history->suc )
			{
				line = e->lp_edge.lp_line;
				if ( horizontal_lp_edgeline_segment( line->pre->pre, line->pre ) )
				{
					line->pre->y = sum_y;
					line->pre->pre->y = sum_y;
				}
				else
				{
					line->pre->x = sum_x;
					line->pre->pre->x = sum_x;
				}
			}	
		}
		end_for_edge_targetlist(n, e);		
	}
	end_for_nodes(graph, n);

}


void	level_edge_routings_hierarchical	(tree_ref tree)
{
	Node		n;
	Edge		e;
	tree_node_ref	tree_node, target_tree_node;
	lp_Edgeline	line;
	int		min_x, min_y, max_x, max_y;
	int		target_x1, target_y1, target_x2, target_y2;
	int		sum_x, sum_y;
	tree_ref	cur = tree->tree_rec.node->first_son;

	while( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) &&
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) )
			level_edge_routings_hierarchical( cur );
		cur = cur->next_brother;
	}
	
	cur = tree->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
	             ( cur->tree_rec.node->graph_iso != NULL ) &&
		     ( cur->tree_rec.node->leaf ) )
		{
		tree_node = cur->tree_rec.node;
		min_x = tree_node->x1;
		min_y = tree_node->y1;
		max_x = tree_node->x2;
		max_y = tree_node->y2;
		n = cur->tree_rec.node->graph_iso;
		for_edge_sourcelist(n, e)
		{
			line = e->lp_edge.lp_line;
			target_tree_node = e->target->lp_node.tree_iso->tree_rec.node;

			if ( line->suc->suc == line )
			{
				if ( horizontal_lp_edgeline_segment( line, line->suc ) )
				{
					target_y1 = target_tree_node->y1;
					target_y2 = target_tree_node->y2;

					if ( target_y1 > min_y ) min_y = target_y1;
					if ( target_y2 < max_y ) max_y = target_y2;
				}
				else
				{
					target_x1 = target_tree_node->x1;
					target_x2 = target_tree_node->x2;

					if ( target_x1 > min_x ) min_x = target_x1;
					if ( target_x2 < max_x ) max_x = target_x2;
				}
			}
		}
		end_for_edge_sourcelist(n, e);

		for_edge_targetlist(n, e)
		{
			line = e->lp_edge.lp_line;
			target_tree_node = e->source->lp_node.tree_iso->tree_rec.node;

			if ( line->suc->suc == line )
			{
				if ( horizontal_lp_edgeline_segment( line, line->suc ) )
				{
					target_y1 = target_tree_node->y1;
					target_y2 = target_tree_node->y2;

					if ( target_y1 > min_y ) min_y = target_y1;
					if ( target_y2 < max_y ) max_y = target_y2;
				}
				else
				{
					target_x1 = target_tree_node->x1;
					target_x2 = target_tree_node->x2;

					if ( target_x1 > min_x ) min_x = target_x1;
					if ( target_x2 < max_x ) max_x = target_x2;
				}
			}	
		}
		end_for_edge_targetlist(n, e);

		tree_node->x1 = min_x;
		tree_node->y1 = min_y;
		tree_node->x2 = max_x;
		tree_node->y2 = max_y;

		sum_x = tree_node->x1 + tree_node->x2;
		if ( ( sum_x % 64 ) == 0 ) sum_x = sum_x / 2;
		else sum_x = sum_x / 2 + 16;
		sum_y = tree_node->y1 + tree_node->y2;
		if ( ( sum_y % 64 ) == 0 ) sum_y = sum_y / 2;
		else sum_y = sum_y / 2 + 16;

		for_edge_sourcelist(n, e)
		{
			if ( e->lp_edge.history == e->lp_edge.history->suc )
			{
				line = e->lp_edge.lp_line;
				target_tree_node = e->target->lp_node.tree_iso->tree_rec.node;

				if ( horizontal_lp_edgeline_segment( line, line->suc ) )
				{
					line->y = sum_y;
					line->suc->y = sum_y;
				}
				else
				{
					line->x = sum_x;
					line->suc->x = sum_x;
				}
			}
		}
		end_for_edge_sourcelist(n, e);

		for_edge_targetlist(n, e)
		{
			if ( e->lp_edge.history == e->lp_edge.history->suc )
			{
				line = e->lp_edge.lp_line;
				if ( horizontal_lp_edgeline_segment( line->pre->pre, line->pre ) )
				{
					line->pre->y = sum_y;
					line->pre->pre->y = sum_y;
				}
				else
				{
					line->pre->x = sum_x;
					line->pre->pre->x = sum_x;
				}
			}	
		}
		end_for_edge_targetlist(n, e);		
		}
		cur = cur->next_brother;
	}
}


void	level_routings_hierarchical	(Graph graph)
{
	level_edge_routings_hierarchical( graph->lp_graph.derivation_net );
}

