#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include <X11/Xos.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>

#include "lp_subframe.h"

#include "lp_edgeline.h"
#include "lp_3_pass.h"
#include "lp_7_pass.h"
#include "lp_routing.h"
#include "lp_adjust.h"

/****************************************************************************
function:	projection
Input:	int x, y, side, x1, x2, y1, y2
	Pointer to int *px, *py

	Compute the projection (px, py) of the point (x, y) to the side side of the
	rectangle(x1, y1, x2, y2)
****************************************************************************/

void 	projection(int x, int y, int side, int x1, int y1, int x2, int y2, int *px, int *py)
{
	switch( side )
	{
		case L_side:	*px = x1; *py = y;  break;
		case R_side:	*px = x2; *py = y;  break;
		case D_side:	*px = x;  *py = y1; break;
		case U_side:	*px = x;  *py = y2; break;
	}
}

/****************************************************************************
function:	corner
Input:	int side1, side2, x1, y1, x2, y2
	Pointer to int cx, cy

	Compute the  side1, side2 - corner (cx, cy) of the rectangle(x1, y1, x2, y2)
****************************************************************************/

void	corner(int side1, int side2, int x1, int y1, int x2, int y2, int *cx, int *cy)
{
	switch( side1 )
	{
		case L_side:

			*cx = x1;
			switch( side2 )
			{
				case D_side: *cy = y1; break;
				case U_side: *cy = y2; break;
			}
			break;

		case R_side:

			*cx = x2;
			switch( side2 )
			{
				case D_side: *cy = y1; break;
				case U_side: *cy = y2; break;
			}
			break;

		case D_side:

			*cy = y1;
			switch( side2 )
			{
				case L_side: *cx = x1; break;
				case R_side: *cx = x2; break;
			}
			break;

		case U_side:

			*cy = y2;
			switch( side2 )
			{
				case L_side: *cx = x1; break;
				case R_side: *cx = x2; break;
			}
			break;

	}
}

/****************************************************************************
function:	pass_7
Input:	Graph graph

	Compute * the coord. of all nodes of the graph and set according to 
		  Def. 4.8.4 the graphed nodes
		* the edge routing and set the graphed edges
****************************************************************************/

void 	pass_7	(Graph graph)
{
	Node			n;
	Edge			e;
	history_ref		cur_history, first_history;
	int			x1, y1, x2, y2;
	tree_node_ref		father;
	tree_edge_ref		conn_rel;
	int			dir;
	int			cur_length;
	int			xr1, yr1;
	int			xpsmin1, ypsmin1;
	int			cx, cy;
	int			px, py;
	lp_Edgeline		entry;
	lp_Edgeline		last;
	int			side;
	Edgeline		graphed_line;
	int			x_pos, y_pos;
	int			x_size, y_size;
	lp_Edgeline		cur_lp_edgeline;

	for_nodes(graph, n)
	{
		for_edge_sourcelist(n, e)
		{
			first_history = e->lp_edge.history;

			e->lp_edge.lp_line = copy_lp_edgeline(first_history->element->tree_rec.history_elem->line );
			cur_history = first_history->suc;

			while ( cur_history != first_history )
			{
				father = cur_history->element->father->tree_rec.node;
				conn_rel = cur_history->element->tree_rec.history_elem;

				x1 = father->x1;
				y1 = father->y1;
				x2 = father->x2;
				y2 = father->y2;

				x1 = x1 + conn_rel->track_number[L_side] * current_size;
				y1 = y1 + conn_rel->track_number[D_side] * current_size;
				x2 = x2 - conn_rel->track_number[R_side] * current_size;
				y2 = y2 - conn_rel->track_number[U_side] * current_size;

				if( (cur_history->element->tree_rec.history_elem->type == IN_CONN_REL) ||
				    (cur_history->element->tree_rec.history_elem->type == RHS_EDGE))
				{
					dir = lp_dir( e->lp_edge.lp_line->pre->pre, e->lp_edge.lp_line->pre );

					cur_length = side_ref_length( conn_rel->sides[dir] );

					entry = copy_lp_edgeline( conn_rel->line );

					xr1 = entry->x;
					yr1 = entry->y;

					xpsmin1 = e->lp_edge.lp_line->pre->pre->x;
					ypsmin1 = e->lp_edge.lp_line->pre->pre->y;

					e->lp_edge.lp_line = delete_from_lp_edgeline(e->lp_edge.lp_line, e->lp_edge.lp_line->pre );
				
					entry = delete_from_lp_edgeline( entry, entry );

					last = e->lp_edge.lp_line->pre;

					switch (cur_length)
					{
						case 0:
		
							if ( ( dir == U_dir ) || ( dir == D_dir ) )
							{	last->x = xr1;		last->y = ypsmin1;	}
							else
							{	last->x = xpsmin1;	last->y = yr1;		}
							break;

						case 1:
							side = conn_rel->sides[dir]->side;

							if ( ( dir == U_dir ) || ( dir == D_dir ) )
							{
								if ( side == L_side )
								{	last->x = x1;	last->y = ypsmin1;	}	
								else
								{	last->x = x2;	last->y = ypsmin1;	}	
							}
							else
							{
								if ( side == D_side )
								{	last->x = xpsmin1;	last->y = y1;	}	
								else
								{	last->x = xpsmin1;	last->y = y2;	}
							}	

							projection(xr1, yr1, side, x1, y1, x2, y2, &px, &py );

							e->lp_edge.lp_line = add_to_lp_edgeline( e->lp_edge.lp_line, px, py );
							break;
			
						case 2:

							side = conn_rel->sides[dir]->side;
							if ( ( dir == U_dir ) || ( dir == D_dir ) )
							{
								if ( side == L_side )
								{	last->x = x1;	last->y = ypsmin1;	}	
								else
								{	last->x = x2;	last->y = ypsmin1;	}	
							}
							else
							{
								if ( side == D_side )
								{	last->x = xpsmin1;	last->y = y1;	}	
								else
								{	last->x = xpsmin1;	last->y = y2;	}
							}					
							corner( side, conn_rel->sides[dir]->suc->side, x1, y1, x2, y2, &cx, &cy );

							e->lp_edge.lp_line = add_to_lp_edgeline( e->lp_edge.lp_line, cx, cy );
						
							projection(xr1, yr1, conn_rel->sides[dir]->suc->side, x1, y1, x2, y2, &px, &py );

							e->lp_edge.lp_line = add_to_lp_edgeline( e->lp_edge.lp_line, px, py );
							break;
					}
					e->lp_edge.lp_line = concat_lp_edgelines( e->lp_edge.lp_line, entry );	
					/* write_lp_edgeline(e->lp_edge.lp_line); */
				}

				if(cur_history->element->tree_rec.history_elem->type == OUT_CONN_REL)
				{
					dir = lp_dir( e->lp_edge.lp_line, e->lp_edge.lp_line->suc );

					cur_length = side_ref_length( conn_rel->sides[dir] );
					/* write_lp_edgeline(e->lp_line); */

					entry = copy_lp_edgeline( conn_rel->line );
					/* write_lp_edgeline(entry); */

					xr1 = entry->pre->x;
					yr1 = entry->pre->y;

					xpsmin1 = e->lp_edge.lp_line->suc->x;
					ypsmin1 = e->lp_edge.lp_line->suc->y;

					e->lp_edge.lp_line = delete_from_lp_edgeline(e->lp_edge.lp_line, e->lp_edge.lp_line );
					/* write_lp_edgeline(e->lp_line); */

					entry = delete_from_lp_edgeline( entry, entry->pre );

					last = e->lp_edge.lp_line;

					switch (cur_length)
					{
						case 0:
		
							if ( ( dir == U_dir ) || ( dir == D_dir ) )
							{	last->x = xr1;		last->y = ypsmin1;	}
							else
							{	last->x = xpsmin1;	last->y = yr1;		}
							break;

						case 1:
							side = conn_rel->sides[dir]->side;

							if ( ( dir == U_dir ) || ( dir == D_dir ) )
							{
								if ( side == L_side )
								{	last->x = x1;	last->y = ypsmin1;	}	
								else
								{	last->x = x2;	last->y = ypsmin1;	}	
							}
							else
							{
								if ( side == D_side )
								{	last->x = xpsmin1;	last->y = y1;	}	
								else
								{	last->x = xpsmin1;	last->y = y2;	}
							}	

							projection(xr1, yr1, side, x1, y1, x2, y2, &px, &py );

							entry = add_to_lp_edgeline( entry, px, py );
							break;
			
						case 2:

							side = conn_rel->sides[dir]->side;
							if ( ( dir == U_dir ) || ( dir == D_dir ) )
							{
								if ( side == L_side )
								{	last->x = x1;	last->y = ypsmin1;	}	
								else
								{	last->x = x2;	last->y = ypsmin1;	}	
							}
							else
							{
								if ( side == D_side )
								{	last->x = xpsmin1;	last->y = y1;	}	
								else
								{	last->x = xpsmin1;	last->y = y2;	}
							}

							projection(xr1, yr1, conn_rel->sides[dir]->suc->side, x1, y1, x2, y2, &px, &py );
							entry = add_to_lp_edgeline( entry, px, py );

							corner( side, conn_rel->sides[dir]->suc->side, x1, y1, x2, y2, &cx, &cy );
							entry = add_to_lp_edgeline( entry, cx, cy );
							break;
					}
					e->lp_edge.lp_line = concat_lp_edgelines( entry, e->lp_edge.lp_line );	
				}
				cur_history = cur_history->suc;
			}
		}
		end_for_edge_sourcelist(n, e);
	}
	end_for_nodes(graph, n);

	if( LP_WIN.min_nodes )
	{
		adjust_tree( graph->lp_graph.derivation_net );
		min_nodes( graph );
	} 

	for_nodes(graph, n )
	{
		x_pos	= ( n->lp_node.tree_iso->tree_rec.node->x1 + n->lp_node.tree_iso->tree_rec.node->x2 ) / 2;
		y_pos	= ( n->lp_node.tree_iso->tree_rec.node->y1 + n->lp_node.tree_iso->tree_rec.node->y2 ) / 2;
		
		x_pos  += (int)(current_size / 2);
		y_pos  += (int)(current_size / 2);
		x_size	= n->lp_node.tree_iso->tree_rec.node->x2 - n->lp_node.tree_iso->tree_rec.node->x1 + current_size;
		y_size	= n->lp_node.tree_iso->tree_rec.node->y2 - n->lp_node.tree_iso->tree_rec.node->y1 + current_size;

		node_set(n, ONLY_SET, NODE_POSITION, x_pos, y_pos, 0);
		node_set(n, ONLY_SET, NODE_SIZE, x_size, y_size, 0);
		node_set(n, ONLY_SET, NODE_NEI, NO_NODE_EDGE_INTERFACE, 0 );
	}
	end_for_nodes(graph, n);

	for_nodes(graph, n )
	{
		for_edge_sourcelist(n, e)
		{
			for_lp_edgeline( e->lp_edge.lp_line, cur_lp_edgeline )
			{
				cur_lp_edgeline->x = (int)((double)cur_lp_edgeline->x 
							   + (int)((double)current_size / (double)2));
				cur_lp_edgeline->y = (int)((double)cur_lp_edgeline->y 
							   + (int)((double)current_size / (double)2));
			}
			end_for_lp_edgeline( e->lp_edge.lp_line, cur_lp_edgeline );
			clip_lp_edgeline(e->lp_edge.lp_line->suc, e->lp_edge.lp_line, e->source);
			clip_lp_edgeline(e->lp_edge.lp_line->pre->pre, e->lp_edge.lp_line->pre, e->target);
			graphed_line = cp_lp_edgeline_to_edgeline( e->lp_edge.lp_line );
			edge_set(e, ONLY_SET, EDGE_LINE, graphed_line, 0 );
		}
		end_for_edge_sourcelist(n, e);
	}
	end_for_nodes(graph, n);

}
