#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_6_pass.h"

/******************************************************************************
function:	T-point
Input:	Pointer to int x, y, int x_orig, y_orig, type

	(x,y) is the o-orientation of (x,y) where o is the orientation with 
	type type and origin (x_orig, y_orig)
******************************************************************************/
			
void	T_point	(int *x, int *y, int x_orig, int y_orig, int type)
{
	int	x_old = *x;
	int	y_old = *y;

	switch ( type )
	{
		case 1 :
			break;

		case 2 :
			*x = y_orig - y_old + x_orig;
			*y = x_old - x_orig + y_orig;
			break;

		case 3 :
			*x = x_orig - x_old + x_orig;
			*y = y_orig - y_old + y_orig;
			break;

		case 4 :
			*x = y_old - y_orig + x_orig;
			*y = x_orig - x_old + y_orig;
			break;

		case 5 :
			*x = 2 * x_orig - x_old;
			break;

		case 6 :
			*y = 2 * y_orig - y_old;
			break;

		case 7 :
			*x = x_orig - y_orig + y_old;
			*y = x_old - x_orig + y_orig;
			break;

		case 8 :
			*x = y_orig - y_old + x_orig;
			*y = y_orig - x_old + x_orig;
			break;
	}

}

/******************************************************************************
function:	T_rectangle
Input:	Pointer to int x1, y1, x2, y2
	int x_orig, y_orig, type

	The rectangle(x1, y1, x2, y2) is the o-orientation of the rectangle
	(x1, y1, x2, y2) where o is the orientation with type type 
	and origin (x_orig, y_orig)
******************************************************************************/

void	T_rectangle	(int *x1, int *y1, int *x2, int *y2, int x_orig, int y_orig, int type)
{
	int	h;

	T_point(x1, y1, x_orig, y_orig, type );
	T_point(x2, y2, x_orig, y_orig, type );

	if ( *x2 < *x1 )
	{
		h = *x1; 
		*x1 = *x2;
		*x2 = h;
	}
	if ( *y2 < *y1 )
	{
		h = *y1;
		*y1 = *y2;
		*y2 = h;
	}
}

/******************************************************************************
function:	pass_6
Input:	tree_ref father

	Compute the
		* Enlarging			Kap. 4.7.1
		* Orienting			Kap. 4.7.2
		* Translating			Kap. 4.7.3
	and the attributes
		* layout			Kap. 4.7.3.2
		* entry				Kap. 4.7.3.2
	in a top down traversal
******************************************************************************/

void 	pass_6	(tree_ref father)
{
	int			x1, y1, x2, y2;
	int			x, y;
	tree_top_sort_ref	cur_top, level_first;
	int			n_x, n_y;
	int			trans_x, trans_y;
	int			x_orig, y_orig;
	int			type;
	tree_ref		cur;

	int			int_x1, int_y1, int_x2, int_y2;
	int			box_x1, box_y1, box_x2, box_y2;
	int			cur_sum_x, cur_sum_y;
	lp_Edgeline		result;
	tree_lp_edgeline_ref	first_tree_lp_edgeline, cur_tree_lp_edgeline;
	tree_node_ref		cur_node;
	tree_edge_ref		cur_edge;

	/************************/
	/*	ENLARGING 	*/
	/************************/

	cur_top = father->tree_rec.node->first_x;

	cur_sum_x = 0;
	cur_sum_y = 0;

	while ( cur_top != NULL )
	{
		
		if ( cur_top->mLS > cur_sum_x ) cur_sum_x = cur_top->mLS;
		if ( cur_top->mBS > cur_sum_y ) cur_sum_y = cur_top->mBS;

		if ( cur_top->type == LP_NODE )
		{
			n_x = (int)((double)cur_top->iso->ref.node->x * SCALING );
			n_y = (int)((double)cur_top->iso->ref.node->y * SCALING );

			trans_x = cur_top->first_x->mLS;
			trans_y = cur_top->first_y->mBS;

			cur_top->ref.node->x1 = n_x + trans_x;
			cur_top->ref.node->y1 = n_y + trans_y;

			cur_top->ref.node->x2 = cur_top->ref.node->x1 +
				cur_top->ref.node->W1;

			cur_top->ref.node->y2 = cur_top->ref.node->y1 +
				cur_top->ref.node->H1;
		}
		else
		{
			trans_x = cur_top->first_x->mLS;
			trans_y = cur_top->first_y->mBS;

			cur_top->ref.tree_lp_edgeline->x = 
				(int)((double)cur_top->iso->ref.lp_edgeline->x * SCALING) + trans_x;

			cur_top->ref.tree_lp_edgeline->y =
				(int)((double)cur_top->iso->ref.lp_edgeline->y * SCALING) + trans_y;
		}
		cur_top = cur_top->next_x;
	}

	cur_top = father->tree_rec.node->first_x;

	cur_sum_x = 0;

	level_first = cur_top;

	while ( cur_top != NULL )
	{
		if ( level_first != cur_top->first_x )
		{
			cur_sum_x = cur_sum_x + level_first->mL;
			level_first = cur_top;
		}
		cur_top = cur_top->next_x;
	}
	cur_sum_x = cur_sum_x + level_first->mL;

	
	cur_top = father->tree_rec.node->first_y;

	cur_sum_y = 0;

	level_first = cur_top;

	while ( cur_top != NULL )
	{
		if ( level_first != cur_top->first_y )
		{
			cur_sum_y = cur_sum_y + level_first->mB;
			level_first = cur_top;
		}
		cur_top = cur_top->next_y;
	}
	cur_sum_y = cur_sum_y + level_first->mB;

	
	box_x1 = (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->x * SCALING) - 
	 	 (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_width / 2 * SCALING) ;

	box_y1 = (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->y * SCALING) -
		 (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_height / 2 * SCALING) ;


	box_x2 = (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->x * SCALING) +
		 (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_width / 2  * SCALING) + cur_sum_x;

	box_y2 = (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->y * SCALING) +
		 (int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_height / 2  * SCALING) + cur_sum_y;


	int_x1 = box_x1 + current_size;
	int_y1 = box_y1 + current_size;
	int_x2 = box_x2 - current_size;
	int_y2 = box_y2 - current_size;

	/************************/
	/*	ORIENTING 	*/
	/************************/

	x_orig = box_x1;
	y_orig = box_y1;

	type = father->tree_rec.node->orientation_type;

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
			cur_node = cur->tree_rec.node;
			x1 = cur_node->x1;
			y1 = cur_node->y1;
			x2 = cur_node->x2;
			y2 = cur_node->y2;

			T_rectangle(&x1, &y1, &x2, &y2, x_orig, y_orig, type);

			cur_node->x1 = x1;
			cur_node->y1 = y1;
			cur_node->x2 = x2;
			cur_node->y2 = y2;
		}
		else
		{
			cur_edge = cur->tree_rec.history_elem;

			first_tree_lp_edgeline = cur_edge->tree_line;

			if ( first_tree_lp_edgeline != NULL )
			{
				cur_tree_lp_edgeline = first_tree_lp_edgeline;
				do
				{
					x = cur_tree_lp_edgeline->x;
					y = cur_tree_lp_edgeline->y;

					T_point(&x, &y, x_orig, y_orig, type );

					cur_tree_lp_edgeline->x = x;
					cur_tree_lp_edgeline->y = y;
		
					cur_tree_lp_edgeline = cur_tree_lp_edgeline->suc;
				}
				while ( cur_tree_lp_edgeline != first_tree_lp_edgeline );
			}
		}
		cur = cur->next_brother;
	}

	T_rectangle(&box_x1, &box_y1, &box_x2, &box_y2, x_orig, y_orig, type );
	
	int_x1 = box_x1 + current_size;
	int_y1 = box_y1 + current_size;
	int_x2 = box_x2 - current_size;
	int_y2 = box_y2 - current_size;

	/************************/
	/*	TRANSLATING 	*/
	/************************/

	trans_x = father->tree_rec.node->x1 + father->tree_rec.node->track_quantity[L_side] * current_size - int_x1;
	trans_y = father->tree_rec.node->y1 + father->tree_rec.node->track_quantity[D_side] * current_size - int_y1;


	cur_top = father->tree_rec.node->first_x;

	while ( cur_top != NULL )
	{
		if ( cur_top->type == LP_NODE )
		{

			cur_top->ref.node->x1 = 
			cur_top->ref.node->x1 + trans_x;
			cur_top->ref.node->y1 = 
			cur_top->ref.node->y1 + trans_y;
			cur_top->ref.node->x2 = 
			cur_top->ref.node->x2 + trans_x;
			cur_top->ref.node->y2 = 
			cur_top->ref.node->y2 + trans_y;
		}
		else
		{
			cur_top->ref.tree_lp_edgeline->x = 
			cur_top->ref.tree_lp_edgeline->x + trans_x;
			cur_top->ref.tree_lp_edgeline->y = 
			cur_top->ref.tree_lp_edgeline->y + trans_y;
		}
		cur_top = cur_top->next_x;
	}

	/****************************************/
	/*	ATTRIBUTES layout, entry 	*/
	/****************************************/

	cur = father->tree_rec.node->first_son;

	while ( cur != NULL )
	{
		if (cur->tree_rec_type == HISTORY_ELEM )
		{
			result = NULL;
			first_tree_lp_edgeline =
			cur->tree_rec.history_elem->tree_line;
			if ( first_tree_lp_edgeline != NULL )
			{
				cur_tree_lp_edgeline = first_tree_lp_edgeline;
				do
				{
					result = add_to_lp_edgeline(result, 
						cur_tree_lp_edgeline->x, cur_tree_lp_edgeline->y );

					cur_tree_lp_edgeline = cur_tree_lp_edgeline->suc;
				}
				while( cur_tree_lp_edgeline != first_tree_lp_edgeline );
			}
			cur->tree_rec.history_elem->line = result;
		}
		cur = cur->next_brother;
	}

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) ) pass_6( cur );
		cur = cur->next_brother;
	}
}
