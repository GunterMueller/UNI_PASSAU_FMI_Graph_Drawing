#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_graph.h"
#include "lp_6_pass.h"

#include "lp_5_pass.h"

/**********************************************************************************
function:	orientation_type_1356
Input:	int type

Output:	TRUE iff. type is orientation type is 1, 3, 5 or 6
	FALSE otherwise
**********************************************************************************/

int 	orientation_type_1356(int type)
{
	return( ( type == 1 ) || ( type == 3 ) || ( type == 5 ) || ( type == 6 ) );
}

/**********************************************************************************
function:	pass_5
Input:	tree_ref father

	Compute the attributes
		* W, H				Kap. 4.6.1
		* mL, mLS, mB, mBS		Kap. 4.6.2
		* W1, H1			Kap. 4.6.3
		* w, h				Kap. 4.6.7
	in a bottom up traversal
**********************************************************************************/

void 	pass_5	(tree_ref father)
{
	tree_ref		cur = father->tree_rec.node->first_son;
	tree_node_ref		cur_net_node;
	tree_top_sort_ref	cur_top;
	int			max_width = 0;
	int			x_cur_sum = 0;
	int			y_cur_sum = 0;
	int			max_height = 0;
	tree_top_sort_ref	level_first;
	

	/********************************/
	/*	Attributes	W, H 	*/
	/********************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) ) pass_5( cur );
		cur = cur->next_brother;
	}

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
			cur_net_node = cur->tree_rec.node;
			if ( cur_net_node->first_son == NULL )
			{
				cur_net_node->W = (int)((double)cur_net_node->prod_iso->box.r_width * SCALING) - current_size;
				cur_net_node->H = (int)((double)cur_net_node->prod_iso->box.r_height * SCALING) - current_size;
			}
			else
			{
				if ( orientation_type_1356( father->tree_rec.node->orientation_type ) )
				{
					cur_net_node->W = cur_net_node->w;
					cur_net_node->H = cur_net_node->h;
				}	
				else
				{
					cur_net_node->W = cur_net_node->h;
					cur_net_node->H = cur_net_node->w;
				}
			}
		}
		cur = cur->next_brother;
	}

	/************************************************/
	/*	Attributes	mL, mLS, mB, mBS 	*/
	/************************************************/

	cur_top = father->tree_rec.node->first_x;

	max_width = 0;
	x_cur_sum = 0;

	level_first = cur_top;

	while ( cur_top != NULL )
	{
		if ( cur_top->first_x == level_first )
		{
			if ( cur_top->type == LP_NODE )
			{
				if ( cur_top->ref.node->W > max_width )
					max_width = cur_top->ref.node->W;
			}
		}
		else
		{
			level_first->mL = max_width;
			level_first->mLS = x_cur_sum;
			x_cur_sum = x_cur_sum + max_width;
			level_first = cur_top;
			max_width = 0;
			if ( cur_top->type == LP_NODE )
			{
				if ( cur_top->ref.node->W > max_width )
					max_width = cur_top->ref.node->W;
			}
		}
		cur_top = cur_top->next_x;
	}
	level_first->mL = max_width;
	level_first->mLS = x_cur_sum;
	x_cur_sum = x_cur_sum + max_width;

	cur_top = father->tree_rec.node->first_y;

	max_height = 0;
	y_cur_sum = 0;

	level_first = cur_top;

	while ( cur_top != NULL )
	{
		if ( cur_top->first_y == level_first )
		{
			if ( cur_top->type == LP_NODE )
			{
				if ( cur_top->ref.node->H > max_height )
					max_height = cur_top->ref.node->H;
			}
		}
		else
		{
			level_first->mB = max_height;
			level_first->mBS = y_cur_sum;
			y_cur_sum = y_cur_sum + max_height;
			level_first = cur_top;
			max_height = 0;
			if ( cur_top->type == LP_NODE )
			{
				if ( cur_top->ref.node->H > max_height )
					max_height = cur_top->ref.node->H;
			}
		}
		cur_top = cur_top->next_y;
	}
	level_first->mB = max_height;
	level_first->mBS = y_cur_sum;
	y_cur_sum = y_cur_sum + max_height;

	/********************************/
	/*	Attributes	W1, H1 	*/
	/********************************/

	cur_top = father->tree_rec.node->first_x;


	while ( cur_top != NULL )
	{
		if ( cur_top->type == LP_NODE )
		{
			if ( cur_top->ref.node->first_son == NULL )
			{
				cur_top->ref.node->W1 =
				cur_top->first_x->mL;
				cur_top->ref.node->H1 =
				cur_top->first_y->mB;
			}
			else
			{
				cur_top->ref.node->W1 =
				cur_top->ref.node->W;

				cur_top->ref.node->H1 =
				cur_top->ref.node->H;
			}
		}
		cur_top = cur_top->next_x;
	}
	
	/********************************/
	/*	Attributes	w, h 	*/
	/********************************/

	if ( orientation_type_1356( father->tree_rec.node->orientation_type  ) )
	{
		father->tree_rec.node->w =

		(int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_width * SCALING) - (current_size * 2) +
		x_cur_sum +
		father->tree_rec.node->track_quantity[L_side] * current_size +
		father->tree_rec.node->track_quantity[R_side] * current_size;

		father->tree_rec.node->h =

		(int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_height * SCALING)  - (current_size * 2) +
		y_cur_sum +
		father->tree_rec.node->track_quantity[U_side] * current_size +
		father->tree_rec.node->track_quantity[D_side] * current_size;

	}
	else
	{
		father->tree_rec.node->w =

		(int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_height * SCALING)  - (current_size * 2) +
		y_cur_sum +
		father->tree_rec.node->track_quantity[L_side] * current_size +
		father->tree_rec.node->track_quantity[R_side] * current_size;

		father->tree_rec.node->h =

		(int)((double)father->tree_rec.node->used_prod->gra.gra.nce1.left_side->node->box.r_width * SCALING)  - (current_size * 2) +
		x_cur_sum +
		father->tree_rec.node->track_quantity[U_side] * current_size +
		father->tree_rec.node->track_quantity[D_side] * current_size;
	}
}
