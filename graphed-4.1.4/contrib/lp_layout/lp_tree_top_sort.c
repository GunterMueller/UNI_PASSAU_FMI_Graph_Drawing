#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_tree_top_sort.h"

/************************************************************************************
function	new_tree_lp_edgeline_ref

	Create a new tree_lp_edgeline_ref n

Output: Pointer to n
************************************************************************************/

tree_lp_edgeline_ref	new_tree_lp_edgeline_ref	(void)
{
	tree_lp_edgeline_ref	new =
	(tree_lp_edgeline_ref) mymalloc( sizeof( struct tree_lp_edgeline ) );

	new->x 		= 0;
	new->y		= 0;
	new->pre 	= new;
	new->suc 	= new;
	new->prod_iso 	= NULL;
	new->tree_iso	= NULL;

	return( new );
}

/*****************************************************************************
function add_tree_lp_edgeline
Input:	tree_lp_edgeline_ref list, new

	Add new at the end of list

Output:	list
*****************************************************************************/

tree_lp_edgeline_ref	add_tree_lp_edgeline(tree_lp_edgeline_ref list, tree_lp_edgeline_ref new)
{
	if ( list == NULL )	return( new );

	new->pre = list->pre;
	new->suc = list;
	list->pre->suc = new;
	list->pre = new;
	return( list );
}

/****************************************************************************
function	copy_lp_edgeline_to_tree
Input:	lp_Edgeline cur

	Create a tree_lp_edgeline_ref n with corresponding lp_edgeline cur

Output:	Pointer to n
****************************************************************************/

tree_lp_edgeline_ref	copy_lp_edgeline_to_tree	(lp_Edgeline cur)
{
	tree_lp_edgeline_ref	new = new_tree_lp_edgeline_ref();

	new->prod_iso = cur;
	cur->iso = new;

	return( new );
}

/****************************************************************************
function:	copy_lp_edgeline_list_to_tree
Input:	lp_Edgeline line

	Create a tree_lp_edgeline_ref n with all points of line

Output:	Pointer to n
****************************************************************************/

tree_lp_edgeline_ref	copy_lp_edgeline_list_to_tree	(lp_Edgeline line)
{
	lp_Edgeline	cur;
	tree_lp_edgeline_ref	result = NULL;
	
	for_lp_edgeline( line, cur )
	{
		result = add_tree_lp_edgeline( result, copy_lp_edgeline_to_tree( cur ) );
	}
	end_for_lp_edgeline( line, cur );

	return( result );
}

/*****************************************************************************
function	copy_lp_edgelines_to_tree
Input:	Graph p

	Copy every lp_edgeline in p to a corresponding  tree_lp_edgeline_ref
	in the derivation net
*****************************************************************************/

void	copy_lp_edgelines_to_tree	(Graph p)
{
	Group 		g;
	int		i, number_of_in_embeddings;
	Edge		e;
	Group		cur_group;

	number_of_in_embeddings  = size_of_embedding (p->gra.gra.nce1.embed_in);

	cur_group = p->gra.gra.nce1.right_side;
	/* executed also for outembedding  */
	for_group (cur_group, g )
	{
		for_edge_sourcelist( g->node, e )
		{
			e->lp_edge.tree_iso->tree_rec.history_elem->tree_line = 
			copy_lp_edgeline_list_to_tree( e->lp_edge.lp_line );
		}
		end_for_edge_sourcelist( g->node, e );
	}
	end_for_group( cur_group, g );
	
	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = p->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, e )
			{
				e->lp_edge.tree_iso->tree_rec.history_elem->tree_line = 
				copy_lp_edgeline_list_to_tree( e->lp_edge.lp_line );
			}
			end_for_edge_sourcelist( g->node, e );
	      	}
		end_for_group (cur_group, g);
	}
}

/*******************************************************************************
function	new_tree_top_sort_ref

	Create a new tree_top_sort_ref n
Output:	Pointer to n
*******************************************************************************/

tree_top_sort_ref	new_tree_top_sort_ref(void)
{
	tree_top_sort_ref	new =
	(tree_top_sort_ref) mymalloc( sizeof( struct tree_top_sort_rec ) );

	new->next_x 	= NULL;
	new->next_y 	= NULL;
	new->first_x 	= new;
	new->first_y 	= new;
	new->iso	= NULL;
	new->type 	= LP_NODE;
	new->ref.node	= NULL;
	
	/********************************/
	/*	Initialize		*/
	/********************************/

	new->mL = 0;
	new->mLS = 0;
	new->mB = 0;
	new->mBS = 0;

	return( new );
}

/******************************************************************************
function	copy_top_to_tree
Input:	top_sort_ref new

	create a tree_top_sort_ref n whitch is corresponding to new

Output:	Pointer to n
******************************************************************************/

tree_top_sort_ref	copy_top_to_tree(top_sort_ref new)
{
	tree_top_sort_ref	new_top = new_tree_top_sort_ref();

	new_top->iso 	= new;
	new->iso 	= new_top;

	new_top->type	= new->type;
	if ( new->type == LP_NODE )
	{
		new_top->ref.node = new->ref.node->lp_node.tree_iso->tree_rec.node;
	}
	else
	{
		new_top->ref.tree_lp_edgeline = new->ref.lp_edgeline->iso;
		new->ref.lp_edgeline->iso->tree_iso = new_top;
	}
	return( new_top );
}
	
/****************************************************************************************
function	copy_topological_sorting_to_tree
Input:	tree_ref father

	Copy the topological sorting of the production whitch was applied in father
	to the derivation net
****************************************************************************************/
void	copy_topological_sorting_to_tree(tree_ref father)
{
	top_sort_ref		cur_top = father->tree_rec.node->used_prod->gra.gra.nce1.lp_nce1_gragra.first_x;
	tree_top_sort_ref	x_list = NULL;
	tree_top_sort_ref	x_last = NULL;
	tree_top_sort_ref	cur_tree_top;
	tree_top_sort_ref	new_top;

	while ( cur_top != NULL )
	{
		new_top = copy_top_to_tree( cur_top );
	
		if ( x_list == NULL ) 
		{
			x_list = new_top;
			x_last = new_top;
		}
		else
		{
			x_last->next_x = new_top;
			x_last = new_top;
		}
		cur_top = cur_top->next_x;
	}

	cur_tree_top = x_list;
	while ( cur_tree_top != NULL )
	{
		cur_tree_top->first_x = cur_tree_top->iso->first_x->iso;
		cur_tree_top->first_y = cur_tree_top->iso->first_y->iso;
		if ( cur_tree_top->iso->next_y == NULL )
		{
			cur_tree_top->next_y = NULL;
		}
		else
		{
			cur_tree_top->next_y = cur_tree_top->iso->next_y->iso;
		}
		cur_tree_top = cur_tree_top->next_x;
	}
	father->tree_rec.node->first_x = x_list;
	father->tree_rec.node->first_y = father->tree_rec.node->used_prod->gra.gra.nce1.lp_nce1_gragra.first_y->iso;
}
