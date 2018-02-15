#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_test.h"
#include "lpm_iso_test.h"
#include "lp_general_functions.h"
#include <string.h>

/************************************************************************/
/*									*/
/*	modul: lp_test.c						*/
/*	includes:  functions for					*/
/*		   *testing on layout production properties		*/
/*									*/
/************************************************************************/

/************************************************************************/
/*									*/
/* remember:	normaly all functions have to be computed for		*/
/*			* in_embeddings					*/
/*			* out_embeddings				*/
/*			* elements of right hand side			*/
/*		In graphed they are all together represented in one 	*/
/*		graph. Because of that some functions works also if	*/
/*		they are computed only for the graph.			*/
/*									*/
/************************************************************************/
 
typedef enum
{
	IS_SOURCE_EDGE,
	IS_TARGET_EDGE
}
	edge_type;

/*************************************************************************
function:	lp_test_edge_relabeling
Input:	Graph prod

	Testet, ob Kantenumbenennungen existieren

Output:	TRUE wenn keine existieren
	FALSE sonst
*************************************************************************/

int	lp_test_edge_relabeling(Graph prod)
{
	Edge		e;
	Node		n;
	for_nodes( prod, n )
	{
		for_edge_sourcelist( n, e )
		{
			if( e->label.text )
			{
				if( strstr(e->label.text, ">") )
				{
					return( FALSE );
				}
			}
		}
		end_for_edge_sourcelist( n, e );
	}
	end_for_nodes( prod, n );

	return( TRUE );
}

/*************************************************************************
function:	edge_relabeling_graph_grammar

	Testet ob GG edges relabeled
*************************************************************************/

int	edge_relabeling_graph_grammar(void)
{
	int	buffer;
	Graph	graph;

	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) 
	{
		for_all_graphs( buffer, graph )
		{
			if( graph->is_production )
			{
				if( !lp_test_edge_relabeling(graph) )
				{
					return( FALSE );
				}
			}
		}
		end_for_all_graphs (buffer, graph);
	}

	return( TRUE );
}

	
/*************************************************************************
function:	lp_test_nodes_non_touching
Input:	Graph	prod

	Testet, ob die Knoten sich beruehren

Output:	FALSE, wenn sie sich beruehren
	TRUE sonst
*************************************************************************/

int	lp_test_nodes_non_touching(Graph prod)
{
	Group	cur_group, g, 
		cur_group2, g2;

	cur_group	= prod->gra.gra.nce1.right_side;
	cur_group2	= prod->gra.gra.nce1.right_side;

	for_group (cur_group, g )
	{
		for_group (cur_group2, g2 )
		{
			/****** ACHTUNG: Es reich einen Halbraum zu untersuchen, da jeder Knoten mit jedem 2 mal verglichen wird ******/
			if( (g->node->x <= g2->node->x) && (g->node->y > g2->node->y) )
			{
				/*** g links unter g2 (Umgedreht wegen Bildschirm)***/
				if( !((node_top(g->node) - node_top(g2->node) - node_height(g2->node)) > 0) )	/** Hoehe **/
				{
					if( !((node_left(g2->node) - node_left(g->node) - node_width(g->node)) > 0) )
					{ 
						compile_production_error_list = 
						new_picklist( NODE_PICKED, g->node );
						return( FALSE );
					}
				}
			}
			if( (g->node->x <= g2->node->x) && (g->node->y <= g2->node->y) && (g->node != g2->node) )
			{
				/*** g links oberhalb g2 (Umgedreht wegen Bildschirm)***/
				if( !((node_top(g2->node) - node_top(g->node) - node_height(g->node)) > 0) )	/** Hoehe **/
				{
					if( !((node_left(g2->node) - node_left(g->node) - node_width(g->node)) > 0) )
					{ 
						compile_production_error_list = 
						new_picklist( NODE_PICKED, g->node );
						return( FALSE );
					}
				}
			}

    	 	}
		end_for_group (cur_group2, g2);
     	}
	end_for_group (cur_group, g);

	return( TRUE );
}

/*************************************************************************
function: 	lp_test_non_overlapping_edges
Input:	Graph prod

	Testet, ob Kanten uebereinanderliegen

Output:	TRUE wenn nicht,
	FALSE sonst
*************************************************************************/

int	lp_test_non_overlapping_edges(Graph prod)
{
	Node		node, node2;
	Edge		edge, edge2;
	lp_Edgeline	cur, cur2;
	int		min_cur, max_cur, min_cur2, max_cur2;



	for_nodes( prod, node )
	{
		for_edge_sourcelist( node, edge )
		{
			for_nodes( prod, node2 )
			{
				for_edge_sourcelist( node2, edge2 )
				{
					for_lp_edgeline( edge->lp_edge.lp_line , cur )
					{
						for_lp_edgeline( edge2->lp_edge.lp_line , cur2 )
						{
							if( cur != cur2 )
							{
								if( cur->suc != edge->lp_edge.lp_line && cur2->suc != edge2->lp_edge.lp_line )
								{
									if( vertical_lp_edgeline_segment(cur, cur->suc)		&&
									    vertical_lp_edgeline_segment(cur2, cur2->suc)	)
									{
										if( cur->x == cur2->x )
										{
											if( cur->y > cur->suc->y )
											{
												min_cur = cur->suc->y;
												max_cur = cur->y;
											}
											else
											{
												min_cur = cur->y;
												max_cur = cur->suc->y;
											}

											if( cur2->y > cur2->suc->y )
											{
												min_cur2 = cur2->suc->y;
												max_cur2 = cur2->y;
											}
											else
											{
												min_cur2 = cur2->y;
												max_cur2 = cur2->suc->y;
											}
											if( !((max_cur < min_cur2) || (max_cur2 < min_cur)) )
											{
												if( ((max_cur == min_cur2) &&
												     ((node->y == max_cur) || (edge->target->y == max_cur))) 
												||  ((max_cur2 == min_cur) &&
												     ((node->y == min_cur) || (edge->target->y == min_cur)))
												  ) 
												{
													/*OK*/
												}
												else
												{
													compile_production_error_list = 
													new_picklist( EDGE_PICKED, edge );
													return( FALSE );
												}
											}
										}

									}
									if( horizontal_lp_edgeline_segment(cur, cur->suc)	&&
									    horizontal_lp_edgeline_segment(cur2, cur2->suc)	)
									{
										if( cur->y == cur2->y )
										{
											if( cur->x > cur->suc->x )
											{
												min_cur = cur->suc->x;
												max_cur = cur->x;
											}
											else
											{
												min_cur = cur->x;
												max_cur = cur->suc->x;
											}

											if( cur2->x > cur2->suc->x )
											{
												min_cur2 = cur2->suc->x;
												max_cur2 = cur2->x;
											}
											else
											{
												min_cur2 = cur2->x;
												max_cur2 = cur2->suc->x;
											}
											if( !((max_cur < min_cur2) || (max_cur2 < min_cur)) )
											{
												if( ((max_cur == min_cur2) &&
												     ((node->x == max_cur) || (edge->target->x == max_cur))) 
												||  ((max_cur2 == min_cur) &&
												     ((node->x == min_cur) || (edge->target->x == min_cur)))
												  ) 
												{
													/*OK*/
												}
												else
												{
													compile_production_error_list = 
													new_picklist( EDGE_PICKED, edge );
													return( FALSE );
												}
											}
										}
									}
								}
							}
						}
						end_for_lp_edgeline( edge2->lp_edge.lp_line, cur2 );
					}
					end_for_lp_edgeline( edge->lp_edge.lp_line, cur );
				}
				end_for_edge_sourcelist( node2, edge2 );
    	 		}
			end_for_nodes( prod, node2 );
		}
		end_for_edge_sourcelist( node, edge );
     	}
	end_for_nodes( prod, node );

	return( TRUE );
}
	
/*************************************************************************
function: 	lp_test_node_inside_border
Input:	Node node, Node n, int dist

	Stellt fest, ob Knoten node innerhalb von der Produktion  
	liegt mit mindest-Abstand dist.

Output:	TRUE, wenn der Abstand fuer jeden Punkt gegeben ist
*************************************************************************/

int	lp_test_node_inside_border(Node node, Node n, int dist)
{
	int	result;

	result = ( (node_left(n)			<= node_left(node) - dist)			&&
		   (node_left(n) + node_width(n)	>= node_left(node) + node_width(node) + dist)	&&
		   (node_top(n) 			<= node_top(node)  - dist)			&&
		   (node_top(n)  + node_height(n)	>= node_top(node)  + node_height(node) + dist)	);


	if( result )
	{
		return( TRUE );
	}
	return( FALSE );
}

/*************************************************************************
function: 	lp_test_edge_inside_border
Input:	Edge edge, Node n, int dist

	Stellt fest, ob jeder einzelne Punkt der Polylinie von edge
	innerhalb von der Produktion liegt mit mindest-Abstand dist
Output:	TRUE, wenn der Abstand fuer jeden Punkt gegeben ist
*************************************************************************/

int	lp_test_edge_inside_border(Edge edge, Node n, int dist)
{
	lp_Edgeline	cur;
	int		x, y;
	int		result;

	for_lp_edgeline( edge->lp_edge.lp_line , cur )
	{
		x = cur->x;
		y = cur->y;

		result = ( !( (node_left(n) <= x - dist)	&& 	(x + dist <= (node_left(n) + node_width(n)) ) &&
		       	      (node_top(n)  <= y - dist)	&&	(y + dist <= (node_top(n) + node_height(n)) ) 	) );

		if ( result )
		{
			return( FALSE );
		}
	}
	end_for_lp_edgeline( edge->lp_edge.lp_line, cur );

	return( TRUE );
}

/*************************************************************************
function: 	lp_test_on_frame
Input:	Graph	prod

	Diese Funktion ueberprueft, ob vom Rand jedes Knoten und jedem 
	Kantenpunkt noch mindestens grid / 2 Abstand zum Rand der
	Produktion ist.

Output:	---
************************************************************************/

int	lp_test_on_frame(Graph prod)
{
	int	grid 		= 32;		/*** Hier noch richtigen Wert einsetzen ***/
	Group	cur_group, g;
	Edge	edge;
	Node	LHS_node 	= prod->gra.gra.nce1.left_side->node;

	cur_group = prod->gra.gra.nce1.right_side;

	for_group (cur_group, g )
	{
		if( !lp_test_node_inside_border(g->node, LHS_node, grid/2) )
		{
			compile_production_error_list = 
			new_picklist( NODE_PICKED, g->node );
			return( FALSE );
		}
		for_edge_sourcelist( g->node, edge )
		{
			if( edge_is_no_out_embedding(edge, prod) && !lp_test_edge_inside_border(edge, LHS_node, grid/2) )
			{
				compile_production_error_list = 
				new_picklist( EDGE_PICKED, edge );
				return( FALSE );
			}
		}
		end_for_edge_sourcelist( g->node, edge );
     	}
	end_for_group (cur_group, g);

	return( TRUE );
}

/************************************************************************/
/*									*/
/*	Testing rectangular edgelines.		 			*/
/*									*/
/************************************************************************/
/************************************************************************
function: 	rectangular_edgeline_segment
input:	discrete lp_Edgeline points p,q

output:	TRUE iff. (p,q) is a discrete line segment,
	FALSE otherwise
************************************************************************/

int	rectangular_edgeline_segment	(lp_Edgeline first, lp_Edgeline second)
{
	return( ( first->x == second->x ) || ( first->y == second->y ) );
}

/************************************************************************
function: 	rectangular edgeline
input: 	sequence of discrete edgeline points (p1...pn).

output:	TRUE, iff. (p1...pn) is a discrete polyline, 
	FALSE otherwise.
************************************************************************/

int	rectangular_edgeline	(lp_Edgeline line)
{
	lp_Edgeline 	cur;

	for_lp_edgeline( line, cur )
	{
		if ( cur->suc != line )
			if ( !rectangular_edgeline_segment( cur, cur->suc) )
				return( FALSE );
	}
	end_for_lp_edgeline( line, cur );
	return( TRUE );
}

/****************************************************************
function:	 rectangular_edgelines
input:	Production

output:	FALSE iff. there is a non-discrete polyline in the edgelines
	of the production, 
	TRUE otherwise
****************************************************************/ 

int 	rectangular_edgelines	(Graph p)
{
	Node		n;
	Edge		e;

	for_nodes (p, n )
	{
		for_edge_sourcelist( n, e )
		{
			if (!rectangular_edgeline( e->lp_edge.lp_line ) )
			{
				compile_production_error_list = 
				new_picklist( EDGE_PICKED, e );
				return( FALSE );
			}
		}
		end_for_edge_sourcelist( n, e );
	}
	end_for_nodes( p, n );
	
	return( TRUE );
}

/************************************************************************
function:	write_production_lp_edgelines
Input:	Graph p

	write all lp_edgelines of p on stdio 		
************************************************************************/

void	write_production_lp_edgelines 	(Graph p)
{
	Edge		e;
	Node		n;
	for_nodes(p, n )
	{
		for_edge_sourcelist( n, e )
		{
			write_lp_edgeline(e->lp_edge.lp_line);
		}
		end_for_edge_sourcelist( n, e );
	}
	end_for_nodes( p, n );
	
	printf("**** end****\n");
}

/************************************************************************/
/*									*/
/*	Testing unit nodes						*/
/*									*/
/************************************************************************/

/************************************************************************
function: 	unit_lp_edgeline
input:	lp_edgeline point lp, grid unit

output:	FALSE iff. l is not on the grid 
	TRUE otherwise
************************************************************************/

int 		unit_lp_edgeline	(lp_Edgeline lp, int unit)
{
	lp_Edgeline	l = lp ->suc;
	
	do
	{
		if ( ((l->x % unit != 0) || (l->y % unit != 0)) 
		   && (l != l->suc->suc) ) 
		return(FALSE);
		l = l->suc;
	}
	while(l->suc != lp);
	return(TRUE);
}

/************************************************************************
function: 	unit_layout_production
input:	Production

output:	FALSE, iff. there is a node with a non unit size in the production
	TRUE otherwise
	plus auf grid gesetzt plus kanten
************************************************************************/

int	unit_layout_production(Graph p, int unit)
{
	Group		cur_group, g;
	Node		cur_node;
	Edge		e;

	cur_group = p->gra.gra.nce1.right_side;

	for_group (cur_group, g )
	{
		if ( (g->node->box.r_width != unit) 	||
		     (g->node->box.r_height != unit) 	||
		     (g->node->x % unit != 0)		||
		     (g->node->y % unit != 0)		)
		{
			compile_production_error_list = 
			new_picklist( NODE_PICKED, g->node );
			return( FALSE );
		}
     	}
	end_for_group (cur_group, g);

	for_nodes( p, cur_node )
	{
		for_edge_sourcelist( cur_node, e)
		{
			if (!grid_lp_edgeline(e->lp_edge.lp_line, 32) )
			{
				compile_production_error_list = 
				new_picklist( EDGE_PICKED, e );
				return( FALSE );
			}
		}
		end_for_edge_sourcelist( cur_node, e);	
	}
	end_for_nodes( p, cur_node )
	return( TRUE );
}
	
/************************************************************************
function: 	non_term_unit
input:	Production

output:	FALSE, iff. there is a non term node with a non unit size in the production
	TRUE otherwise
************************************************************************/

int	non_term_unit(Graph p, int unit)
{
	Group		cur_group, g;

	cur_group = p->gra.gra.nce1.right_side;

	for_group (cur_group, g )
	{
		if( node_is_nonterminal(g->node) )
		{
			if ( (g->node->box.r_width != unit) 	||
			     (g->node->box.r_height != unit) 	)
			{
				compile_production_error_list = 
				new_picklist( NODE_PICKED, g->node );
				return( FALSE );
			}
		}
     	}
	end_for_group (cur_group, g);

	return( TRUE );
}

/************************************************************************/
/*									*/
/*	Testing grid graph						*/
/*									*/
/************************************************************************/

/************************************************************************
function: 	grid_lp_edgeline
input:	lp_edgeline point lp, grid unit

output:	FALSE iff. l is not on the grid 
	TRUE otherwise
************************************************************************/

int 		grid_lp_edgeline	(lp_Edgeline lp, int unit)
{
	lp_Edgeline	l = lp ->suc;
	
	do
	{
		if ( ((l->x % unit != 0) || (l->y % unit != 0)) 
		   && (l != l->suc->suc) ) 
		return(FALSE);
		l = l->suc;
	}
	while(l->suc != lp);
	return(TRUE);
}

/************************************************************************
function: 	grid_layout_production_node_lp_edgeline
input:	Production p, grid unit

output:	FALSE iff. there is a lp_edgeline containing a non grid point 
	      or a node with its center not on grid
	TRUE otherwise
************************************************************************/

int	grid_layout_production_node_lp_edgeline	(Graph p, int unit, Node left_hand_side)
{
	Group	cur_group, g;
	Edge	e;

	cur_group = p->gra.gra.nce1.right_side;
	for_group (cur_group, g )
	{
		if ( ( ( g->node->x - (( g->node->box.r_width ) / 2 ) ) % unit != 0 ) ||
		     ( ( g->node->y - (( g->node->box.r_height ) / 2 ) ) % unit != 0 ) ||
		     ( ( g->node->x + (( g->node->box.r_width ) / 2 ) ) % unit != 0 ) ||
		     ( ( g->node->y + (( g->node->box.r_height ) / 2 ) ) % unit != 0 ) )
		{
			compile_production_error_list = 
			new_picklist( NODE_PICKED,g->node );
			return( FALSE );
		}
		
		for_edge_sourcelist( g->node, e)
		{
			if (!grid_lp_edgeline(e->lp_edge.lp_line, 16) )
			{
				compile_production_error_list = 
				new_picklist( EDGE_PICKED, e );
				return( FALSE );
			}
		}
		end_for_edge_sourcelist( g->node, e);	

		for_edge_targetlist( g->node, e)
		{
			if (!grid_lp_edgeline(e->lp_edge.lp_line, 16) )
			{
				compile_production_error_list = 
				new_picklist( EDGE_PICKED, e );
				return( FALSE );
			}
		}
		end_for_edge_targetlist( g->node, e);
	}
	end_for_group( cur_group, g);	

	return( TRUE );
}

/************************************************************************
function: 	grid_point
input: 	coord (x,y) and grid unit

output:	TRUE iff. coord is a grid point, 
	FALSE otherwise
************************************************************************/

int	grid_point	(int x, int y, int unit)
{
	return( ( ( x % unit ) == 0 ) && ( ( y % unit ) == 0 ) );
}

/************************************************************************
function: 	grid_layout_production
input:	Production p, grid unit

output:	FALSE iff. * a point of a lp_edgeline 
		   * a center of a node
		   * the box of the left hand side 
		   of the production is no grid rectangle,
	TRUE otherwise
************************************************************************/

int	grid_layout_production	(Graph p, int unit)
{
	Node	left_hand_side_node = p->gra.gra.nce1.left_side->node;
	Node	cur_node, target_node;
	Edge	e;


	if ( !grid_point(node_top(left_hand_side_node), node_left(left_hand_side_node), 16) || 
	     !grid_point(node_width(left_hand_side_node), node_height(left_hand_side_node), 16) )
	{
		compile_production_error_list = new_picklist( NODE_PICKED, left_hand_side_node );
		return( FALSE );
	}

	if (!grid_layout_production_node_lp_edgeline(p, 16, left_hand_side_node) )
	{
		return( FALSE );
	}

	/****** Kanten duerfen nicht am Eck anfangen oder aufhoeren ******/
	for_nodes( p, cur_node )
	{
		for_edge_sourcelist( cur_node, e)
		{
			if( horizontal_lp_edgeline_segment(e->lp_edge.lp_line, e->lp_edge.lp_line->suc) )
			{
				if( (e->lp_edge.lp_line->y == node_top(cur_node))					||
				    (e->lp_edge.lp_line->y == node_top(cur_node) + node_height(cur_node))		)
				{
					compile_production_error_list = 
					new_picklist( EDGE_PICKED, e );
					return( FALSE );
				}
			}
			else
			{
				if( (e->lp_edge.lp_line->x == node_left(cur_node))					||
				    (e->lp_edge.lp_line->x == node_left(cur_node) + node_width(cur_node))		)
				{
					compile_production_error_list = 
					new_picklist( EDGE_PICKED, e );
					return( FALSE );
				}
			}

			target_node = e->target;

			if( horizontal_lp_edgeline_segment(e->lp_edge.lp_line->pre, e->lp_edge.lp_line->pre->pre) )
			{
				if( (e->lp_edge.lp_line->pre->y == node_top(target_node))				||
				    (e->lp_edge.lp_line->pre->y == node_top(target_node) + node_height(target_node))	)
				{
					compile_production_error_list = 
					new_picklist( EDGE_PICKED, e );
					return( FALSE );
				}
			}
			else
			{
				if( (e->lp_edge.lp_line->pre->x == node_left(target_node))					||
				    (e->lp_edge.lp_line->pre->x == node_left(target_node) + node_width(target_node))		)
				{
					compile_production_error_list = 
					new_picklist( EDGE_PICKED, e );
					return( FALSE );
				}
			}

			
		}
		end_for_edge_sourcelist( cur_node, e);	
		
	}
	end_for_nodes( p, cur_node )
	
	return( TRUE );
}
		     
/************************************************************************/
/*									*/
/*	intersection-test						*/
/*									*/
/************************************************************************/

/************************************************************************
function: 	edge_is_no_out_embedding
Input:	Edge e

Output:	TRUE iff. edge is no out_embedding
	FALSE otherwise
************************************************************************/

int	edge_is_no_out_embedding(Edge edge, Graph p)
{
	Node	left_hand_side_node = p->gra.gra.nce1.left_side->node;

	if(inside(edge->target->x, edge->target->y, left_hand_side_node) )
		return( TRUE );
	return( FALSE );
}

/************************************************************************
function: 	inside
input:	int x, y, box of the left side of a production

output:	TRUE iff. the point (x, y) is included in the box around production
	FALSE otherwise
************************************************************************/

int	inside(int x, int y, Node n)
{
	if ( (node_left(n) < x) && ( x < (node_left(n) + node_width(n)) ) &&
	     (node_top(n) < y) && ( y < (node_top(n) + node_height(n)) ) 
	   )
		return( TRUE );
	return( FALSE );
}

/************************************************************************
function: 	line_segment_node_cut
input:	lp_Edgeline-point l, node m

output: TRUE iff. the lp_edgeline-segment [l, l->suc] is going 
	     through the node center
	FALSE otherwise
************************************************************************/

int 	line_segment_node_cut(lp_Edgeline l, Node m)
{
	if   ( (  vertical_lp_edgeline_segment(l, l->suc) && (l->x == m->x) && 
   	       ( ((l->y < m->y) && (l->suc->y > m->y)) || ((l->y > m->y) &&(l->suc->y < m->y)) ))
  	  ||   (  horizontal_lp_edgeline_segment(l, l->suc) &&  (l->y == m->y) &&
   	       (((l->x < m->x) && (l->suc->x > m->x)) || ((l->x > m->x) &&(l->suc->x < m->x)))))
		return(TRUE);
	return(FALSE);
}

/************************************************************************
function: 	schnitt_edge_node
input:	Production
output: FALSE iff. there is a cut between a node of the production
	      and an Edge of the production inside the production
	TRUE otherwise
************************************************************************/

int 	schnitt_edge_node(Graph p)
{
	Node		m;
	Node		left_hand_side_node = p->gra.gra.nce1.left_side->node;
	Edge 		e;
	lp_Edgeline 	l;
	Group		g,gr, cur_group, cur_group2;
	int		i, number_of_in_embeddings;

	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);
	cur_group = p->gra.gra.nce1.right_side;
	for_group (cur_group, g )
	{
		for_edge_sourcelist( g->node, e)
		{
			if ( !((e->line->x == e->line->suc->x) && (e->line->y == e->line->suc->y)) )
			{	/*** Ansonsten degenerated, also NICHT melden ***/

				for_lp_edgeline(e->lp_edge.lp_line,l)
				{
					/* if l is an out_embedding only test inside box */
					/* Korrektur TH 15.09.91 */
			     		if ( l->suc != e->lp_edge.lp_line )
					{
						if(inside(l->x, l->y, left_hand_side_node) )
						{
							for_group(cur_group,gr)
							{
								m = gr->node;
								if (line_segment_node_cut(l,m) == TRUE)
								{
									compile_production_error_list = 
									new_picklist( EDGE_PICKED, e );
				  					return(FALSE);
								}
							}
							end_for_group(cur_group,gr);
						}
					}
				}
				end_for_lp_edgeline(e->lp_edge.lp_line,l);
			}
		}
		end_for_edge_sourcelist( g->node, e);	
	}
	end_for_group (cur_group, g);

	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group2 = p->gra.gra.nce1.right_side;
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, e)
			{
				for_lp_edgeline(e->lp_edge.lp_line,l)
				{
					/* Korrektur TH 15.09.91 */
				        if ( l->suc != e->lp_edge.lp_line )
					{
					if(inside(l->x, l->y, left_hand_side_node) )
					{
						for_group (cur_group2, gr)
						{
							m = gr->node;
							if (line_segment_node_cut(l,m) == TRUE)
							{
								compile_production_error_list = 
								new_picklist( EDGE_PICKED, e );
					  			return(FALSE);
							}
						}
						end_for_group(cur_group2, gr);
					}
					}
				}
				end_for_lp_edgeline(e->lp_edge.lp_line,l);
			}
			end_for_edge_sourcelist( g->node, e);			
	      	}
		end_for_group (cur_group, g);
	}

	return(TRUE);
}

/************************************************************************/
/*									*/
/*	test lp_edgelines getting out of production			*/
/*									*/
/************************************************************************/

/************************************************************************
function: 	getting_out
input:	box of the left side of the production n, Edge e

output:	TRUE iff. Edge has a point inside box and a following outside
	FALSE otherwise
************************************************************************/

int 	getting_out(Node n, Edge e)
{
	lp_Edgeline	l;
	int		marke = 0;
	for_lp_edgeline(e->lp_edge.lp_line,l)
	{
		if (!inside(l->x, l->y, n) && (marke == 1) )
		{
			return( TRUE );
		}
		else if (inside(l->x, l->y, n) ) marke = 1;
	}
	end_for_lp_edgeline(e->lp_edge.lp_line,l);
	return( FALSE );
}

/************************************************************************
function:	going_through_for_segment
Input:	Node n, lp_edgeline l

Output:	TRUE, iff. segment between l and l->suc is going through n
	FALSE otherwise
************************************************************************/

int	going_through_for_segment(Node n, lp_Edgeline l)
{
	if ( ( ( vertical_lp_edgeline_segment(l, l->suc) ) && 
	       ( l->y < node_top(n) ) && 
	       ( (node_top(n) + node_height(n)) < l->suc->y ) &&
	       ( l->x > node_left(n) ) &&
	       ( l->x < (node_left(n) + node_width(n)) )
	     ) ||
	     ( ( horizontal_lp_edgeline_segment(l, l->suc) ) && 
	       ( l->x < node_left(n) ) &&
	       ( (node_left(n) + node_width(n)) < l->suc->x ) &&
	       ( l->y > node_top(n) ) &&
	       ( l->y < (node_top(n) + node_height(n)) )
	     )
	   )
		return( TRUE );
	return( FALSE );
}
/************************************************************************
function: going through
input:	box of the left side of the production, Edge
output:	TRUE iff. a segment of edge is going through box, FALSE otherwise
************************************************************************/

int	going_through(Node n, Edge e)
{
	lp_Edgeline	l;
	for_lp_edgeline(e->lp_edge.lp_line,l)
	{
		if(l->suc != e->lp_edge.lp_line)
		{
			if (going_through_for_segment(n, l) )
				return( TRUE );
		}
	}
	end_for_lp_edgeline(e->lp_edge.lp_line, l);
	return( FALSE );
}
/************************************************************************
function: 	edgelines_in_production_for_out_embeddings
Input:	Graph p, Node node

Output:	TRUE iff. edgelines of all out_embeddings doesn't get in the 
	     left_hand_side again when they have left it
	FALSE otherwise
************************************************************************/

int	edgelines_in_production_for_out_embeddings(Graph p, Node left_hand_side_node)
{
	int		i, number_of_out_embeddings;
	Group		g, cur_group;
	Edge		e;
	lp_Edgeline	l;

	number_of_out_embeddings = size_of_embedding (p->gra.gra.nce1.embed_out);

	for( i=0; i<number_of_out_embeddings; i++)
	{
		cur_group = p->gra.gra.nce1.embed_out[i].embed;
		for_group(cur_group, g)
		{
			for_edge_targetlist(g->node, e)
			{
				l = e->lp_edge.lp_line;
				while( inside(l->x, l->y, left_hand_side_node) )
					l = l->suc;
				while(l->suc != e->lp_edge.lp_line)
				{
					if( going_through_for_segment(left_hand_side_node, l) ||
					    inside(l->x, l->y, left_hand_side_node) )
					{
						compile_production_error_list = 
						new_picklist( EDGE_PICKED, e );
						return( FALSE );
					}
					l = l->suc;
				}
				if( inside(l->x, l->y, left_hand_side_node) )
				{
					compile_production_error_list = 
					new_picklist( EDGE_PICKED, e );
			  		return(FALSE);
				}
			}
			end_for_edge_targetlist(g->node, e);
		}
		end_for_group(cur_group, g)
	}

	return( TRUE );
}

/************************************************************************
function: 	edgelines_in_production
input:	Production

output: FALSE iff. there is a Edge that intersects or gets out
	      of a production-box
	TRUE otherwise
************************************************************************/

int 	edgelines_in_production(Graph p)
{
	Node	left_hand_side_node = p->gra.gra.nce1.left_side->node;
	Edge 		e;
	Group		g, cur_group;
	int		i, number_of_in_embeddings;

	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);
	cur_group = p->gra.gra.nce1.right_side;

	for_group (cur_group, g )
	{
		for_edge_sourcelist( g->node, e)
		{
			if( edge_is_no_out_embedding(e, p) )
			{
				if (getting_out(left_hand_side_node, e))
				{
					compile_production_error_list = 
					new_picklist( EDGE_PICKED, e );
			  		return(FALSE);
				}
			}
		}
		end_for_edge_sourcelist( g->node, e);	
	}
	end_for_group (cur_group, g);

	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, e)
			{
				if ( (getting_out(left_hand_side_node, e)) ||
				     (going_through(left_hand_side_node, e)) )
				{
					compile_production_error_list = 
					new_picklist( EDGE_PICKED, e );
			  		return(FALSE);
				}
			}
			end_for_edge_sourcelist( g->node, e);			
	      	}
		end_for_group (cur_group, g);
	}

	if(!edgelines_in_production_for_out_embeddings(p, left_hand_side_node) )
		return( FALSE );
	return(TRUE);
}
/************************************************************************/
/*									*/
/*	testing degenerated Edges					*/
/*									*/
/************************************************************************/

/************************************************************************
function: 	degenerated_edges
input:	Production

output: TRUE iff. there is a edge which is only a point (degenerated)
	FALSE otherwise
NOTE: 	NOT necessary for nodes, because in graphed you can`t put 
	one node on another
ACHTUNG: Diese Funktion MUSS auf der ORGINAL_GRAPHED Linie ausgefuehrt
	 werden, denn wir verschieben einen Punkt
************************************************************************/

int	degenerated_edges	(Graph p)
{
	Node	n;
	Edge	e;

	for_nodes(p,n)
	{
		for_edge_sourcelist(n,e)
		{
			if ((e->line->x == e->line->suc->x) &&
			    (e->line->y == e->line->suc->y))
			{
				compile_production_error_list = 
				new_picklist( EDGE_PICKED, e );
		  		return( TRUE );
			}
		}
		end_for_edge_sourcelist(n,e);
	}
	end_for_nodes(p,n);
	return( FALSE );
}
/************************************************************************
function: 	number_of_in_embedding_edges_is_incorrect
input:  Graph

Output:	TRUE iff. two in_embedding edges starts at one node
	FALSE otherwise	
************************************************************************/

int	number_of_in_embedding_edges_is_incorrect(Graph p)
{
	Group		cur_group,g;
	int		i, number_of_in_embeddings, number_of_out_embeddings;

	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);

	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			if (!(  (g->node->sourcelist == NULL) 
			    || (g->node->sourcelist->sourcesuc == g->node->sourcelist) ))
			{
				compile_production_error_list = 
				new_picklist( NODE_PICKED, g->node );
				return( TRUE );
			}
	      	}
		end_for_group (cur_group, g);
	}

	number_of_out_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_out);

	for( i=0; i<number_of_out_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_out[i].embed;
	      	for_group (cur_group, g) 
		{
			if (!(  (g->node->targetlist == NULL) 
			    || (g->node->targetlist->targetsuc == g->node->targetlist) ))
			{
				compile_production_error_list = 
				new_picklist( NODE_PICKED, g->node );
				return( TRUE );
			}
	      	}
		end_for_group (cur_group, g);
	}
	return( FALSE );
}

/************************************************************************
function: test_for_allowed_first_production
input:  Node node

Output:	FALSE iff. no derivation net exists and node is not the only one
	      node of a graph
	      (the graph grammar has to be started with a single node
	TRUE otherwise 	
************************************************************************/

int	test_for_allowed_first_production(Node node)
{
	if( (node->graph->lp_graph.derivation_net == NULL) &&
	    (node->suc != node) )
	{
		compile_production_error_list = 
		new_picklist( NODE_PICKED, node );
		return( FALSE );
	}
	return( TRUE );
}

/***********************************************************************
function:	my_strcmp
Input:	char	*string1, string2

Output:	TRUE iff. strcmp(string1, string2) = TRUE or string1 = string2 = NULL
	FALSE otherwise
***********************************************************************/

int	my_strcmp(char *string1, char *string2)
{
	if( (string1 != NULL) && (string2 != NULL) &&
	    (strcmp(string1, string2) == 0) )
		return( TRUE );
	if( (string1 == NULL) && (string2 == NULL) )
		return( TRUE );
	return( FALSE );
}

/***********************************************************************
function:	all_embeddings_are_different
Input:	Graph	p

	Compare all embeddings

Output:	TRUE iff. all embeddings are different
	FALSE otherwise
***********************************************************************/

int	all_embeddings_are_different(Graph p)
{
	int	i, t, number_of_in_embeddings, number_of_out_embeddings;
	Group	g, cur_group;
	Group	g2, cur_group2;
	Edge 	e, to_compare;
	

	number_of_out_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_out);
	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);

	/* durchlaufe alle out_embeddings */
	for( i=0; i<number_of_out_embeddings; i++)
	{
		cur_group = p->gra.gra.nce1.embed_out[i].embed;
	      	for_group( cur_group, g ) 
		{
			for_edge_targetlist( g->node, to_compare)
			{

				/***** innere Schleife *****/
				for( t=0; t<number_of_out_embeddings; t++)
				{
					cur_group2 = p->gra.gra.nce1.embed_out[t].embed;
	      				for_group( cur_group2, g2 ) 
					{
						for_edge_targetlist( g2->node, e )
						{
							if( (e != to_compare) &&
							    (my_strcmp(e->label.text, to_compare->label.text) ) &&
							    (my_strcmp(e->target->label.text, to_compare->target->label.text) ) &&
							    (e->source == to_compare->source) )
							{
								compile_production_error_list = 
								new_picklist( EDGE_PICKED, to_compare );
								return( FALSE );
							}
						}
						end_for_edge_targetlist( g2->node, e )
					}
					end_for_group( cur_group2, g2 )
				}
			}
			end_for_edge_targetlist( g->node, to_compare )
		}
		end_for_group( cur_group, g )
	}

	/* durchlaufe alle in_embeddings */
	for( i=0; i<number_of_in_embeddings; i++)
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group( cur_group, g ) 
		{
			for_edge_sourcelist( g->node, to_compare)
			{

				/***** innere Schleife *****/
				for( t=0; t<number_of_in_embeddings; t++)
				{
					cur_group2 = p->gra.gra.nce1.embed_in[t].embed;
	      				for_group( cur_group2, g2 ) 
					{
						for_edge_sourcelist( g2->node, e )
						{
							if( (e != to_compare) &&
							    (my_strcmp(e->label.text, to_compare->label.text) ) &&
							    (e->target == to_compare->target) &&
							    (my_strcmp(e->source->label.text, to_compare->source->label.text) ) )
							{
								compile_production_error_list = 
								new_picklist( EDGE_PICKED, to_compare );
								return( FALSE );
							}
						}
						end_for_edge_sourcelist( g2->node, e )
					}
					end_for_group( cur_group2, g2 )
				}
			}
			end_for_edge_sourcelist( g->node, to_compare )
		}
		end_for_group( cur_group, g )
	}
	return( TRUE );
}

/**************************************************************************
function:	create_production_isomorphism
Input:	Graph	production

	compare the current production with the first with the same name in the 
	list of graphs and append it by pointers multi_pre and multi_suc
**************************************************************************/

void	create_production_isomorphism(Graph production)
{
	Graph	graph;
	int	buffer;
	

	for( buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++ ) 
	{
		for_all_graphs( buffer, graph )
		{
			if ( graph->is_production )
			{
				if( production != graph )
				{
					if( isomorph_graphs(graph, production) )
					{
						append_to_isomorph_graphs( graph, production );
						goto exit;
					}
				}
				else goto exit;
			}
		} 
		end_for_all_graphs( buffer, graph );
	} 
exit:	;
}


void	recompute_all_production_isomorphisms(void)
{
	Graph	graph;
	int	buffer;
	

	for( buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++ ) 
	{
		for_all_graphs( buffer, graph )
		{
			if ( graph->is_production )
			{
				create_production_isomorphism( graph );
			}
		} 
		end_for_all_graphs( buffer, graph );
	} 
}

/************************************************************************
************************************************************************/
int	target_flexible_in_node(Edge edge, edge_type edgetype)
{
	return( TRUE );
}

/************************************************************************
************************************************************************/
int	target_flexible_in_edge(Edge edge, edge_type edgetype)
{
	return( 1 );
}

/************************************************************************
function	target_flexible
Input:	Graph	prod

Output: TRUE	iff. every non_terminal in prod is target-flexible
	FALSE 	otherwise
************************************************************************/

int	target_flexible(Graph prod)
{
	Node	cur_node;
	Edge	cur_edge;
	int	result;

	for_nodes( prod, cur_node )
	{
		if( node_is_nonterminal(cur_node) )
		{
			for_edge_sourcelist( cur_node, cur_edge )
			{
				if( lp_edgeline_length( cur_edge->lp_edge.lp_line ) == 3 )
				{
					result = target_flexible_in_node( cur_edge, IS_SOURCE_EDGE );
					if( !result )
					{
						compile_production_error_list = 
						new_picklist( NODE_PICKED, cur_node );
						return( FALSE );
					}
				}
				else
				{
					result = target_flexible_in_edge( cur_edge, IS_SOURCE_EDGE );
					if( !result )
					{
						compile_production_error_list = 
						new_picklist( NODE_PICKED, cur_node );
						return( FALSE );
					}
				}
			}
			end_for_edge_sourcelist( cur_node, cur_edge );
		}
	}
	end_for_nodes( prod, cur_node );
	return( TRUE );
}


int	test_grammar_changed(int graph_creation_time)
{
	int	buffer;
	Graph	graph;

	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) 
	{
		for_all_graphs (buffer, graph)
		{
			if( graph->is_production )
			{
			 	if ( ( graph->change_time > graph_creation_time ) ||
				     ( graph_state.lp_graph_state.production_deleted ) )
				{
					MsgBox("Grammar has been changed. No hierarchcal manipulation possible", CMD_OK);
					return( TRUE );
				}
			}
		}
		end_for_all_graphs (buffer, graph);
	}
	return( FALSE );
}

int	is_ENCE_1_GRAMMAR(Graph graph)
{
	if ( graph->gra.type != ENCE_1 )
	{
		compile_production_error_list = new_picklist( NODE_PICKED, graph->gra.gra.nce1.left_side->node );
	}
	return ( graph->gra.type == ENCE_1 );
}


int	lp_pre_test(Node node)
{
	if ( graph_state.lp_graph_state.LGG_Algorithm == GRAPHED_STANDARD )
	{
		node->graph->lp_graph.hierarchical_graph = FALSE;
		node->graph->lp_graph.creation_time	= (int)ticks();
	}
	else
	{
		if (node->graph->lp_graph.derivation_net == NULL )
		{
			node->graph->lp_graph.creation_time	= (int)ticks();
			reset_iso_in_all_productions();		
			recompute_all_production_isomorphisms();	
			lp_set_production_deleted( FALSE );
		}
		if ( test_grammar_changed( node->graph->lp_graph.creation_time ) )
		{
			node->graph->lp_graph.hierarchical_graph = FALSE;
			return( FALSE );
		}
		if( !lp_lgg_derivation_tests(node->graph) ) 
		{
			return( FALSE );
		}
	}
	return( TRUE );
}

