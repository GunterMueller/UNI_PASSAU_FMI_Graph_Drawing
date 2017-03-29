#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_graph.h"
#include "lp_edgeline.h"
#include "lp_3_pass.h"
#include "lp_4_pass.h"

/********************************************************************************
function:	new_conn_ref
Input:	tree_edge_ref conn_rel

	Create a new connection record n with connection relation conn_rel

Output:	Pointer to n
********************************************************************************/

conn_ref	new_conn_ref	(tree_edge_ref conn_rel)
{
	conn_ref	new = (conn_ref) mymalloc( sizeof( struct conn_rec ) );

	new->conn_rel = conn_rel;
	new->pre = new;
	new->suc = new;
	return( new );
}

/********************************************************************************
function:	add_conn_ref
Input:	conn_ref liste, new

	Put new at the end of liste

Output: liste
********************************************************************************/

conn_ref	add_conn_ref	(conn_ref liste, conn_ref new)
{
	if ( liste == NULL ) return( new );

	new->pre = liste->pre;
	new->suc = liste;
	liste->pre->suc = new;
	liste->pre = new;
	return(liste);
}

/********************************************************************************
function:	copy_conn_ref
Input:	conn_ref ref

Output:	Pointer to a copy of ref
********************************************************************************/

conn_ref	copy_conn_ref	(conn_ref ref)
{
	return( new_conn_ref( ref->conn_rel ) );
}

/********************************************************************************
function:	free_conn_ref
Input:	conn_ref ref

	free memory space of ref
********************************************************************************/

void		free_conn_ref	(conn_ref ref)
{
	conn_ref	cur, del;

	if ( ref != NULL )
	{
		cur = ref;
		do
		{
			del = cur;
			cur = cur->suc;
			free( del );
		}
		while ( cur != ref );
	}
}

/********************************************************************************
function:	new_level_ref
Input:	conn_ref conn_rels, int level_number

	Create a new level record n with connection relation conn_rels and
	level number level_number

Output:	Pointer to n
********************************************************************************/

level_ref	new_level_ref	(conn_ref conn_rels, int level_number)
{
	level_ref	new = (level_ref) mymalloc( sizeof( struct level_rec ));

	new->conn_rels = conn_rels;
	new->level_number = level_number;
	new->pre = new;
	new->suc = new;
	return( new );
}

/********************************************************************************
function:	add_level_ref
Input:	level_ref liste, new

	Put new at the end of liste

Output: liste
********************************************************************************/

level_ref	add_level_ref	(level_ref list, level_ref new)
{
	if ( list == NULL ) return( new );

	new->pre = list->pre;
	new->suc = list;
	list->pre->suc = new;
	list->pre = new;
	return( list );
}

/********************************************************************************
function:	free_level_ref
Input:	level_ref ref

	free memory space of ref
********************************************************************************/

void	free_level_ref	(level_ref ref)
{
	level_ref	cur, del;

	if ( ref != NULL )
	{
		cur = ref;
		do
		{
			del = cur;
			free_conn_ref( del->conn_rels );
			cur = cur->suc;
			free( del );
		}
		while ( cur != ref );
	}
}

/********************************************************************************
function: no_intersection
Input:	conn_ref ref1, ref2

Output:	TRUE iff. the track intervals of ref1 and ref2 do not intersect
	FALSE otherwise
********************************************************************************/

int	no_intersection	(conn_ref ref1, conn_ref ref2, int side)
{
	return ( ( ref1->conn_rel->track_interval[side].right <= ref2->conn_rel->track_interval[side].left ) || 
		 ( ref2->conn_rel->track_interval[side].right <= ref1->conn_rel->track_interval[side].left ) );
}

/********************************************************************************
function:	add_conn_rel_to_level_list
Input:	conn_ref rel, level_ref list, int side

	add rel to the first track in list which serves the track demand of rel
	or create a new track t and add rel to t

Output:	Update of list
********************************************************************************/

level_ref 	add_conn_rel_to_level_list(conn_ref rel, level_ref list, int side)
{
	level_ref cur_level;
	

	if ( list == NULL ) return( new_level_ref( copy_conn_ref( rel ), 0 ) );

	cur_level = list;
	do
	{
		if ( no_intersection( cur_level->conn_rels, rel , side) )
		{
			cur_level->conn_rels = add_conn_ref( cur_level->conn_rels, copy_conn_ref( rel ));
			return( list );
		}
		cur_level = cur_level->suc;
	}
	while ( ( cur_level != list ) );

	return ( add_level_ref( list, new_level_ref( copy_conn_ref( rel ), list->pre->level_number + 1 ) ) );
}

/********************************************************************************
function: 	add_interval
Input: Pointer to int left, right, interval_rec interval

	iff. interval.left < left: 	change left to interval.left
	iff. interval.right > right: 	change right to interval.right
********************************************************************************/

void	add_interval	(int *left, int *right, interval_rec interval)
{
	if ( ( interval.left != 0 ) || ( interval.right != 0 ) )
	{
		if ( ( *left == 0 ) && ( *right == 0 ) )
		{
			*left = interval.left;
			*right = interval.right;
		}
		else
		{
			if ( interval.left <  *left ) *left = interval.left;
			if ( interval.right >  *right ) *right = interval.right;
		}
	}
}

/********************************************************************************
function:	pass_4
Input:	tree_ref father

	Compute the attributes
		* maximal ordering number		Kap. 4.5.2
		* ordering number			Kap. 4.5.4
		* clockwise				Kap. 4.5.8
		* interval				Kap. 4.5.14
		* track interval			Kap. 4.5.16
		* tsncr					Kap. 4.5.20
		* tncr					Kap. 4.5.24
		* track quantity			Kap. 4.5.25
		* track number				Kap. 4.5.26
	in a bottom-up traversal
********************************************************************************/

void 	pass_4	(tree_ref father)
{
	tree_ref		cur = father->tree_rec.node->first_son;
	int			dir;
	tree_edge_ref		cur_tree_edge;
	int			side;
	int			cur_length;
	int			cur_side;
	int			left, right;
	conn_ref		cur_conn_list;
	conn_ref		first_conn_rel;
	conn_ref		cur_conn_rel;
	level_ref		cur_list;
	level_ref		first_level;
	level_ref		cur_level;

	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     (cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) ) pass_4( cur );
		cur = cur->next_brother;
	}

	/**************************************/
	/* Attribute: maximal ordering number */
	/**************************************/

	for( side = 0; side <= 3; side++ )
	{
		father->tree_rec.node->max_ord[T_side(side, father->tree_rec.node->orientation_type)] =
		father->tree_rec.node->used_prod->gra.gra.nce1.lp_nce1_gragra.max_ord[side];
	}

	/*******************************/
	/* Attribute: ordering number */
	/******************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur_tree_edge = cur->tree_rec.history_elem;
			if ( (cur_tree_edge->type == IN_CONN_REL) ||
			     (cur_tree_edge->type == OUT_CONN_REL) )
			{
				if ( father->tree_rec.node->orientation_type <= 4 )
				{
					cur_tree_edge->lp_ord = cur_tree_edge->prod_iso->lp_edge.lp_ord;
				}
				else
				{
					cur_tree_edge->lp_ord = 
						father->tree_rec.node->used_prod->gra.gra.nce1.lp_nce1_gragra.max_ord[cur_tree_edge->prod_iso->lp_edge.side]
						- cur_tree_edge->prod_iso->lp_edge.lp_ord + 1;
				}
			}
		}
		cur = cur->next_brother;
	}

	/*************************/
	/* Attribute: clock wise */
	/*************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur_tree_edge = cur->tree_rec.history_elem;
			if( cur_tree_edge->type == IN_CONN_REL) 
			{
				for( dir = 0; dir <= 3; dir++ )
				{
					cur_length = dir_ref_length( cur_tree_edge->directions[dir] );
					if ( cur_length > 0 )
					{
						if ( cur_length == 1 )	
						{
							cur_tree_edge->clock_wise[dir] = 0;
						}
						else
						{
							cur_tree_edge->clock_wise[dir] =
							( cur_tree_edge->directions[dir]->dir == 
							  T_dir( cur_tree_edge->directions[dir]->suc->dir, 2 ) 
							);
						}
					}
				}
			}
			if( cur_tree_edge->type == OUT_CONN_REL) 
			{
				for( dir = 0; dir <= 3; dir++ )
				{
					cur_length = dir_ref_length( cur_tree_edge->directions[dir] );
					if ( cur_length > 0 )
					{
						if ( cur_length == 1 )	
						{
							cur_tree_edge->clock_wise[dir] = 0;
						}
						else
						{
							cur_tree_edge->clock_wise[dir] =
							( !( cur_tree_edge->directions[dir]->dir == 
							  T_dir( cur_tree_edge->directions[dir]->suc->dir, 2 ) )
							);
						}
					}
				}
			}
		}
		cur = cur->next_brother;
	}

	/***********************/
	/* Attribute: interval */
	/***********************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur_tree_edge = cur->tree_rec.history_elem;
			if ( (cur_tree_edge->type == IN_CONN_REL) ||
			     (cur_tree_edge->type == OUT_CONN_REL) )
			{
				for( dir = 0; dir <= 3; dir++ )
				{
					if (cur_tree_edge->sides[dir] != NULL )
					{
						cur_length = side_ref_length( cur_tree_edge->sides[dir] );
						if ( cur_length > 0 )
						{
							if ( cur_length == 1 )
							{
								cur_side = cur_tree_edge->sides[dir]->side;
								if ( cur_tree_edge->clock_wise[dir] )
								{
									cur_tree_edge->interval[dir][cur_side].left = 0;
									cur_tree_edge->interval[dir][cur_side].right = cur_tree_edge->lp_ord;
								}
								else
								{
									cur_tree_edge->interval[dir][cur_side].left  = cur_tree_edge->lp_ord;
									cur_tree_edge->interval[dir][cur_side].right = 
										father->tree_rec.node->max_ord[cur_side] + 1;
								}
							}
							else
							{	
								cur_side = cur_tree_edge->sides[dir]->side;
								cur_tree_edge->interval[dir][cur_side].left = 0;
								cur_tree_edge->interval[dir][cur_side].right = 
								father->tree_rec.node->max_ord[cur_side] + 1;
							
								cur_side = cur_tree_edge->sides[dir]->suc->side;

								if ( cur_tree_edge->clock_wise[dir] )
								{
									cur_tree_edge->interval[dir][cur_side].left = 0;
									cur_tree_edge->interval[dir][cur_side].right = cur_tree_edge->lp_ord;
								}
								else
								{
									cur_tree_edge->interval[dir][cur_side].left  = cur_tree_edge->lp_ord;
									cur_tree_edge->interval[dir][cur_side].right = 
										father->tree_rec.node->max_ord[cur_side] + 1;
								}
							}
						}
					}
				}
			}
		}
		cur = cur->next_brother;
	}

	/*****************************/
	/* Attribute: track_interval */
	/*****************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur_tree_edge = cur->tree_rec.history_elem;
			if ( (cur_tree_edge->type == IN_CONN_REL) ||
			     (cur_tree_edge->type == OUT_CONN_REL) )
			{
				for( side = 0; side <= 3; side++ )
				{
					left = 0;
					right = 0;

					for( dir = 0; dir <= 3; dir++ )
					{
						add_interval( &left, &right, cur_tree_edge->interval[dir][side] );
					}
					cur_tree_edge->track_interval[side].left = left;
					cur_tree_edge->track_interval[side].right = right;
				}
			}
		}
		cur = cur->next_brother;
	}
	
	/********************/
	/* Attribute: tsncr */
	/********************/

	for ( side = 0; side <= 3; side++ )
	{
		cur_conn_list = NULL;
		cur = father->tree_rec.node->first_son;
		while ( cur != NULL )
		{
			if ( cur->tree_rec_type == HISTORY_ELEM )
			{
				cur_tree_edge = cur->tree_rec.history_elem;
				if ( (cur_tree_edge->type == IN_CONN_REL) ||
				     (cur_tree_edge->type == OUT_CONN_REL) )
				{
					if ( ( cur_tree_edge->track_interval[side].left != 0 ) ||
					     ( cur_tree_edge->track_interval[side].right != 0 ) )
						cur_conn_list = add_conn_ref( cur_conn_list, 
									new_conn_ref( cur_tree_edge ) );
				}
			}
			cur = cur->next_brother;
		}
		father->tree_rec.node->tsncr[side] = cur_conn_list;
	}

	/*******************/
	/* Attribute: tncr */
	/*******************/

	for( side = 0; side < 4; side++)
	{
		cur_list = NULL;
		first_conn_rel = father->tree_rec.node->tsncr[side];
		if ( first_conn_rel != NULL )
		{
			cur_conn_rel = first_conn_rel;
			do
			{	
				cur_list = add_conn_rel_to_level_list( cur_conn_rel, cur_list, side );
				cur_conn_rel = cur_conn_rel->suc;
			}
			while ( cur_conn_rel != first_conn_rel );
		}
		father->tree_rec.node->tncr[side] = cur_list;
	}
	
	/*****************************/
	/* Attribute: track quantity */
	/*****************************/

	for( side = 0; side < 4; side++)
	{
		if ( father->tree_rec.node->tncr[side] == NULL ) 
			father->tree_rec.node->track_quantity[side] = 0;
		else 	father->tree_rec.node->track_quantity[side] = 
				father->tree_rec.node->tncr[side]->pre->level_number + 1;
	}

	/***************************/
	/* Attribute: track number */
	/***************************/

	for( side = 0; side < 4; side++)
	{
		first_level = father->tree_rec.node->tncr[side];
		if ( first_level != NULL )
		{
			cur_level = first_level;
			do
			{
				first_conn_rel = cur_level->conn_rels;
				if ( first_conn_rel != NULL )
				{
					cur_conn_rel = first_conn_rel;
					do
					{
						cur_conn_rel->conn_rel->track_number[side] = cur_level->level_number;
						cur_conn_rel = cur_conn_rel->suc;
					}
					while ( cur_conn_rel != first_conn_rel );
				}
				cur_level = cur_level->suc;
			}
			while ( cur_level != first_level );
		}
	}	
}
