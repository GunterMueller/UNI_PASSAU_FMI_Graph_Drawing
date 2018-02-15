#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_3_pass.h"

/****************************************************************************
function:	new_dir_ref
Input:	int dir

	Create a new direction record n with direction dir

Output:	Pointer to n
****************************************************************************/

dir_ref	new_dir_ref	(int dir)
{
	dir_ref		new = (dir_ref) mymalloc( sizeof( struct dir_rec ) );
	
	new->pre = new;
	new->suc = new;
	new->dir = dir;
	return( new );
}

/****************************************************************************
function:	add_dir_ref
Input:	dir_ref liste, new

	Put new at the end of liste

Output:	liste
****************************************************************************/

dir_ref	add_dir_ref	(dir_ref liste, dir_ref new)
{
	if ( liste == NULL ) return( new );
	
	new->pre = liste->pre;
	new->suc = liste;
	liste->pre->suc = new;
	liste->pre = new;
	return( liste );
}

/****************************************************************************
function:	dir_ref_length
Input:	dir_ref liste

Output:	length of liste
****************************************************************************/

int	dir_ref_length	(dir_ref liste)
{
	int 	result = 0;
	dir_ref	cur;

	if ( liste != NULL )
	{
		cur = liste;
		do
		{
			result++;
			cur = cur->suc;
		}	
		while ( cur != liste );
	}
	return( result );
}

/****************************************************************************
function:	free_dir_ref
Input:	dir_ref ref

	free memory space of ref
****************************************************************************/

void	free_dir_ref	(dir_ref ref)
{
	dir_ref	cur, del;

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

/****************************************************************************
function:	new_side_ref
Input:	int side

	Create a new side record n with side side

Output: Pointer to n
****************************************************************************/

side_ref	new_side_ref	(int side)
{
	side_ref		new = (side_ref) mymalloc( sizeof( struct side_rec ) );
	
	new->pre = new;
	new->suc = new;
	new->side = side;
	return( new );
}

/****************************************************************************
function:	add_side_ref
Input:	side_ref liste, new

	Put new at the end of liste

Output:	liste
****************************************************************************/

side_ref	add_side_ref	(side_ref liste, side_ref new)
{
	if ( liste == NULL ) return( new );
	
	new->pre = liste->pre;
	new->suc = liste;
	liste->pre->suc = new;
	liste->pre = new;
	return( liste );
}

/****************************************************************************
function:	side_ref_length
Input:	side_ref liste

Output:	length of liste
****************************************************************************/

int	side_ref_length	(side_ref liste)
{
	int 	result = 0;
	side_ref	cur;

	if ( liste != NULL )
	{
		cur = liste;
		do
		{		
			result++;
			cur = cur->suc;
		}
		while ( liste != cur );
	}
	return( result );
}

/****************************************************************************
function:	free_side_ref
Input:	side_ref ref

	free memory space of ref
****************************************************************************/

void	free_side_ref	(side_ref ref)
{
	side_ref	cur, del;

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

/****************************************************************************
function:	pass_3
Input:	tree_ref father

	Compute the attributes
		* direction sequence			Kap. 4.4.1
		* side sequence				Kap. 4.4.5
	in a bottom-up traversal
****************************************************************************/

void 	pass_3	(tree_ref father)
{
	tree_ref		cur = father->tree_rec.node->first_son;
	int			dir;
	tree_edge_ref		cur_tree_edge;
	int			cur_dir;
	dir_ref			dir_list;
	int			act_length;
	side_ref		cur_side_list;

	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) ) pass_3( cur );
		cur = cur->next_brother;
	}

	/*********************************/
	/* Attribute: direction sequence */
	/*********************************/

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
					if ( cur_tree_edge->split[dir] == 0 )
						cur_tree_edge->directions[dir] = NULL;
					else
					{
						if(cur_tree_edge->type == OUT_CONN_REL)
						{
							cur_dir = cur_tree_edge->target_dir;
							if ( dir == cur_dir )
							{
								cur_tree_edge->directions[dir] = new_dir_ref(dir);
							}
							else
							{
								if (neighbor_dirs(dir, cur_dir) )
								{
									dir_list = new_dir_ref( cur_dir );
									dir_list = add_dir_ref( dir_list, new_dir_ref( dir ) );
									cur_tree_edge->directions[dir] = dir_list;
								}
								else
								{
									dir_list = new_dir_ref(cur_dir);
									dir_list = add_dir_ref( dir_list, new_dir_ref( ( dir +3 ) % 4 ) );
									dir_list = add_dir_ref( dir_list, new_dir_ref( dir ) );
									cur_tree_edge->directions[dir] = dir_list;
								}
							}
						}
						if(cur_tree_edge->type == IN_CONN_REL)
						{
							cur_dir = cur_tree_edge->source_dir;
							if ( dir == cur_dir )
							{
								cur_tree_edge->directions[dir] = new_dir_ref(dir);
							}
							else
							{
								if (neighbor_dirs(dir, cur_dir) )
								{
									dir_list = new_dir_ref( dir );
									dir_list = add_dir_ref( dir_list, new_dir_ref( cur_dir ) );
									cur_tree_edge->directions[dir] = dir_list;
								}
								else
								{
									dir_list = new_dir_ref(dir);
									dir_list = add_dir_ref( dir_list, new_dir_ref( ( dir + 1 ) % 4 ) );
									dir_list = add_dir_ref( dir_list, new_dir_ref( cur_dir ) );
									cur_tree_edge->directions[dir] = dir_list;
								}
							}
						}

					}
				}
			}
		}
		cur = cur->next_brother;
	}

	/****************************/
	/* Attribute: side sequence */
	/****************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur_tree_edge = cur->tree_rec.history_elem;
			if ( cur_tree_edge->type == IN_CONN_REL) 
			{
				for( dir = 0; dir <= 3; dir++ )
				{
					act_length = dir_ref_length( cur_tree_edge->directions[dir]);
					if ( act_length <= 1 )
					{			
						cur_tree_edge->sides[dir] = NULL;
					}
					else
					{
						if ( act_length == 2 )
						{
							cur_tree_edge->sides[dir] = 
									new_side_ref( opposite_side( cur_tree_edge->directions[dir]->suc->dir ) );
						}
						else
						{
							cur_side_list = new_side_ref( opposite_side( cur_tree_edge->directions[dir]->suc->dir ) );
							cur_side_list = add_side_ref( cur_side_list, 
										new_side_ref(opposite_side(cur_tree_edge->directions[dir]->suc->suc->dir)) );
							cur_tree_edge->sides[dir] = cur_side_list;
						}
					}
				}
			}
			if ( cur_tree_edge->type == OUT_CONN_REL) 
			{
				for( dir = 0; dir <= 3; dir++ )
				{
					act_length = dir_ref_length( cur_tree_edge->directions[dir]);
					if ( act_length <= 1 )
					{			
						cur_tree_edge->sides[dir] = NULL;
					}
					else
					{
						if ( act_length == 2 )
						{
							cur_tree_edge->sides[dir] = new_side_ref(cur_tree_edge->directions[dir]->dir);
						}
						else
						{
							cur_side_list = new_side_ref(cur_tree_edge->directions[dir]->suc->dir);
							cur_side_list = add_side_ref( cur_side_list, new_side_ref(cur_tree_edge->directions[dir]->dir) );
							cur_tree_edge->sides[dir] = cur_side_list;
						}
					}
				}
			}
		}
		cur = cur->next_brother;
	}
}
