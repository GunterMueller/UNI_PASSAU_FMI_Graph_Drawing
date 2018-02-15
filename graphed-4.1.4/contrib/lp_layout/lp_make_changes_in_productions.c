#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_test.h"
#include "lp_make_changes_in_productions.h"

/************************************************************************/
/*									*/
/*	modul: lp_make_changes_in_productions.c				*/
/*	includes:  functions for					*/
/*		   * makeing the necessary changes in productions	*/
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
 


/************************************************************************/
/*                                                                      */
/*	Copy edgeline to lp_edgeline					*/
/*									*/
/************************************************************************/

/*************************************************************************
function: 	Graphcopy_edgeline_to_lp_edgeline
input:	Production p

	copy every edgeline in the p to a lp_edgeline in the p
*************************************************************************/

void	Graphcopy_edgeline_to_lp_edgeline(Graph p)
{
	Edge 	e;
	Node	n;

	for_nodes (p, n)
	{
		for_edge_sourcelist(n, e)
		{
			e->lp_edge.lp_line = cp_edgeline_to_lp_edgeline(e->line);
		}
		end_for_edge_sourcelist(n, e);
	}
	end_for_nodes(p, n);
}

/************************************************************************/
/*									*/
/*	remove unnecessary nodes from every lp_edgeline			*/
/*									*/
/************************************************************************/

/************************************************************************
function: 	node_to_delete
input:	lp_edgeline-point p and the starting point of this lp_edgeline.
	Let q be the successor of p, r be the successor of q in the lp_edgeline

output:	TRUE if (p,q) and (q,r) are included in the discrete line going through
	(p,r) and r is not the starting point of the lp_edgeline
	False otherwise

Garantie: l->suc != el_head is given by the executing routine
************************************************************************/

int 	node_to_delete (lp_Edgeline l, lp_Edgeline el_head)
{
	if ( (((l->x == l->suc->suc->x) && (l->x == l->suc->x)) || ((l->y == l->suc->suc->y) && (l->y == l->suc->y))) &&
             (l->suc->suc != el_head) 
	   )
	{ 
		return( TRUE );
	}
	return( FALSE );
}

/***********************************************************************
function: 	compact
input:	Production p

	deletes unnecessary lp_edgeline-points in each edge of the production
***********************************************************************/

void	compact(Graph p)
{
	lp_Edgeline 	l,del;
	Edge		e;
	Node		n;

	for_nodes (p, n)
	{
		for_edge_sourcelist(n, e);
		{
			if ((e->lp_edge.lp_line != NULL)				/*** Existiert lp_line 			***/
			 && (e->lp_edge.lp_line != e->lp_edge.lp_line->suc->suc))	/*** Hat sie mehr als 2 Elemente 	***/
			{
				l = e->lp_edge.lp_line;
				do
				{
					if( node_to_delete(l, e->lp_edge.lp_line) )
					{
						del = l->suc;
						l->suc->suc->pre = l;
						l->suc = l->suc->suc;
						free(del);
					}
					else l = l->suc;


				}
				while(l != e->lp_edge.lp_line->pre->pre);
			}
		}
		end_for_edge_sourcelist(n, e);
	}
	end_for_nodes(p, n);
}

/************************************************************************/
/*									*/
/*	making edges start and end at middle of node			*/
/*									*/
/************************************************************************/

/************************************************************************
function: 	convert_start_and_end_point_of_edge
input:	Production p, grid unit

	Takes start- and end-points of every edge and makes it start and 
	end in the middle of the node
************************************************************************/

void 	convert_start_and_end_point_of_edge(Graph p, int grid)
{
	Node	n;
	Edge	e;

	for_nodes(p,n)
	{
		for_edge_sourcelist(n,e)
		{
			if(vertical_lp_edgeline_segment(e->lp_edge.lp_line,e->lp_edge.lp_line->suc) )
			{
				if (e->lp_edge.lp_line->y < e->lp_edge.lp_line->suc->y)
					e->lp_edge.lp_line->y = (e->lp_edge.lp_line->y - grid/2);
				else e->lp_edge.lp_line->y = (e->lp_edge.lp_line->y + grid/2);
			}
			else
			{
				if (e->lp_edge.lp_line->x < e->lp_edge.lp_line->suc->x)
					e->lp_edge.lp_line->x = (e->lp_edge.lp_line->x - grid/2);
				else e->lp_edge.lp_line->x = (e->lp_edge.lp_line->x + grid/2);
			}
			if(vertical_lp_edgeline_segment(e->lp_edge.lp_line->pre,e->lp_edge.lp_line->pre->pre) )
			{
				if (e->lp_edge.lp_line->pre->y < e->lp_edge.lp_line->pre->pre->y)
					e->lp_edge.lp_line->pre->y = (e->lp_edge.lp_line->pre->y - grid/2);
				else e->lp_edge.lp_line->pre->y = (e->lp_edge.lp_line->pre->y + grid/2);
			}
			else
			{
				if (e->lp_edge.lp_line->pre->x < e->lp_edge.lp_line->pre->pre->x)
					e->lp_edge.lp_line->pre->x = (e->lp_edge.lp_line->pre->x - grid/2);
				else e->lp_edge.lp_line->pre->x = (e->lp_edge.lp_line->pre->x + grid/2);
			}
		}
		end_for_edge_sourcelist(n,e);
	}
	end_for_nodes(p,n);
}

/************************************************************************/
/*									*/
/*	making embeddings start or end at box				*/
/*									*/
/************************************************************************/
				
/************************************************************************
function: 	delete_segment
input:	lp_Edgeline-point

	delete lp_Edgeline-point and change pointers

output:	lp_Edgeline-point->suc

Note:	Not necessary to care for less than 3 points, because that can't be
	(given from calling routine)
************************************************************************/

lp_Edgeline	delete_segment(lp_Edgeline l)
{
	lp_Edgeline	to_delete;

	to_delete = l;
	l = l->suc;
	to_delete->pre->suc = l;
	l->pre = to_delete->pre;
	free(to_delete);
	return(l);
}

/************************************************************************
function: 	make_segment_start_at_box
input:	lp_Edgeline-point, box of the left side of the production

	change the coord. of point, so that it starts at box
************************************************************************/

void	make_segment_start_at_box(lp_Edgeline l, Node n)
{
		/***  lp_line is coming from left side of box  ***/
	if ( (l->x < node_left(n) ) && horizontal_lp_edgeline_segment(l, l->suc) )
		l->x = node_left(n);

		/***  lp_line is coming from top of box  ***/
	if ( (l->y < node_top(n) ) && vertical_lp_edgeline_segment(l, l->suc) )
		l->y = node_top(n);

		/***  lp_line is coming from right side of box  ***/
	if ( (l->x > (node_left(n) + node_width(n)) ) && horizontal_lp_edgeline_segment(l, l->suc) )
		 l->x = (node_left(n) + node_width(n));

		/***  lp_line is coming from bottom of box  ***/
	if ( (l->y > (node_top(n) + node_height(n)) ) && vertical_lp_edgeline_segment(l, l->suc) )
		l->y = (node_top(n) + node_height(n));
}

/************************************************************************
function: 	make_segment_start_at_box_for_out
input:	lp_Edgeline-point l, box of the left side of the production

	change the coord. of point, so that it starts at box
************************************************************************/

void	make_segment_start_at_box_for_out(lp_Edgeline l, Node n)
{
		/***  lp_line is coming from left side of box  ***/
	if ( (l->x < node_left(n) ) && horizontal_lp_edgeline_segment(l, l->pre) )
		l->x = node_left(n);

		/***  lp_line is coming from top of box  ***/
	if ( (l->y < node_top(n) ) && vertical_lp_edgeline_segment(l, l->pre) )
		l->y = node_top(n);

		/***  lp_line is coming from right side of box  ***/
	if ( (l->x > (node_left(n) + node_width(n)) ) && horizontal_lp_edgeline_segment(l, l->pre) )
		 l->x = (node_left(n) + node_width(n));

		/***  lp_line is coming from bottom of box  ***/
	if ( (l->y > (node_top(n) + node_height(n)) ) && vertical_lp_edgeline_segment(l, l->pre) )
		l->y = (node_top(n) + node_height(n));
}

/************************************************************************
function: 	change end_point_of_all_out_embeddings
Input:	Graph p

	Makes all out_embeddings end at production box
************************************************************************/

void	change_end_point_of_all_out_embeddings(Graph p)
{
	Node		left_hand_side_node = p->gra.gra.nce1.left_side->node;
	Edge 		e;
	lp_Edgeline 	l;
	Group		g, cur_group;
	int		i, number_of_out_embeddings;

	number_of_out_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_out);

	for( i=0; i<number_of_out_embeddings; i++)
	{
		cur_group = p->gra.gra.nce1.embed_out[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_targetlist( g->node, e)
			{
				l = e->lp_edge.lp_line;
				while(inside(l->x, l->y, left_hand_side_node) )
					l = l->suc;

				make_segment_start_at_box_for_out(l, left_hand_side_node);

				l = l->suc;
				while (!inside(l->x, l->y, left_hand_side_node ) )
					l = delete_segment(l);   /*** after that l is l->suc ***/

				e->lp_edge.lp_line = l;
			}
			end_for_edge_targetlist( g->node, e);	
	      	}
		end_for_group (cur_group, g);
	}
}
		
/************************************************************************
function: 	change_start_point_of_all_embeddings
input:  Graph

	make every embedding start or end at the box around production
************************************************************************/

void 	change_start_point_of_all_embeddings(Graph p)
{
	Node		left_hand_side_node = p->gra.gra.nce1.left_side->node;
	Edge 		e;
	lp_Edgeline 	l;
	Group		g, cur_group;
	int		i, number_of_in_embeddings;
	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);

	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, e)
			{
				l = e->lp_edge.lp_line;
				
				do	/*** delete until box starts ***/
				{
					if (!inside(l->suc->x, l->suc->y, left_hand_side_node ) )          
						l = delete_segment(l);   /*** after that l is l->suc ***/
				}
				while (!inside(l->suc->x, l->suc->y, left_hand_side_node ) );

 				make_segment_start_at_box(l, left_hand_side_node);
				e->lp_edge.lp_line = l;
			}
			end_for_edge_sourcelist( g->node, e);	
	      	}
		end_for_group (cur_group, g);
	}

	change_end_point_of_all_out_embeddings(p);
}


