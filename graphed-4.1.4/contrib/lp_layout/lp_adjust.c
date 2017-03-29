#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"

/********************************************************************************/
/********************************************************************************/
typedef	struct pos_rec
{
	int		pos;
	int		count;
	struct pos_rec	*next;
}
	*pos_ref;

/********************************************************************************
function:	add_pos_ref
Input:	pos_ref list, int p

	Create an new pos_ref n with pos p and append list to n

Output:	n
********************************************************************************/

pos_ref	add_pos_ref	(pos_ref list, int p)
{
	pos_ref	cur = list;
	pos_ref new_pos;

	while	( cur )
	{
		if ( cur->pos == p )
		{
			cur->count++;
			return( list );
		}
		else
		{
			cur = cur->next;
		}
	}
	new_pos = (pos_ref) mymalloc( sizeof( struct pos_rec ) );
	new_pos->pos = p;
	new_pos->count = 1;
	new_pos->next = list;
	return( new_pos );
}
	
/*******************************************************************************
function:	free_pos_ref
Input:	pos_ref list;

	Free memory space of list
*******************************************************************************/

void	free_pos_ref	(pos_ref list)
{
	pos_ref	cur = list;
	pos_ref del;

	while ( cur )
	{
		del = cur;
		cur = cur->next;
		free( del );
	}
}

/*******************************************************************************
function:	write_pos_ref
Input:	pos_ref	list

	Write list to stdio
*******************************************************************************/

/* void	write_pos_ref	(list)
pos_ref	list;
{
	pos_ref	cur = list;

	while ( cur )
	{
		printf("pos %d count %d ****",cur->pos,cur->count);
		cur = cur->next;
	}
	printf("\n");
}*/

/********************************************************************************
function:	best_pos
Input:	pos_ref list;

	Find pos_ref r whith is this pos_ref with highest count
********************************************************************************/

pos_ref	best_pos	(pos_ref list)
{
	pos_ref	result = NULL;
	pos_ref	cur = list;
	int	m = 0;

	while	( cur )
	{
		if ( cur->count > m )
		{
			result = cur;
			m = cur->count;
		}
		cur = cur->next;
	}
	return( result );
}

/*******************************************************************************
function:	move_vertical_line
Input:	lp_Edgeline line, int pos;

	Change the x-coord. of the first two points of line to pos
*******************************************************************************/

void	move_vertical_line	(lp_Edgeline line, int pos)
{
	if ( vertical_lp_edgeline_segment( line, line->suc ) )
	{
			line->x = pos;
			line->suc->x = pos;
	}
}

/*******************************************************************************
function:	move_vertical_line_b
Input:	lp_Edgeline line, int pos

	Change the x-coord of the last two points of line to pos
*******************************************************************************/

void	move_vertical_line_b	(lp_Edgeline line, int pos)
{
	if ( vertical_lp_edgeline_segment( line->pre, line->pre->pre ) )
	{
		line->pre->x = pos;
		line->pre->pre->x = pos;
	}
}

/*******************************************************************************
function:	move_horizontal_line
Input:	lp_Edgeline line, int pos;

	Change the y-coord of the first two points of line to pos
*******************************************************************************/

void	move_horizontal_line	(lp_Edgeline line, int pos)
{
	if ( horizontal_lp_edgeline_segment( line, line->suc ) )
	{
		line->y = pos;
		line->suc->y = pos;
	}
}

/*******************************************************************************
function:	move_horizontal_line_b
Input:	lp_Edgeline line, int pos

	Change the y-coord of the last two points of line to pos
*******************************************************************************/

void	move_horizontal_line_b	(lp_Edgeline line, int pos)
{
	if ( horizontal_lp_edgeline_segment( line->pre, line->pre->pre ) )
	{
		line->pre->y = pos;
		line->pre->pre->y = pos;
	}
}

/********************************************************************************
function:	adjust_level_x
Input:	tree_top_sort_ref first_top, int pos;

	Move for every tree_top_sort_ref, which stands for a node in the graph, 
	the x-coord of all source- and targetedges of the node to pos if they 
	are no edges from a lower hierarchy-level to a higher
********************************************************************************/

void			adjust_level_x	(tree_top_sort_ref first_top, int pos)
{
	tree_top_sort_ref	first = first_top;
	Edge			e;

	while ( ( first != NULL ) && ( first->first_x  == first_top ) )
	{
		if ( first->type == LP_NODE )
		{
			if ( first->ref.node->leaf )
			{
				Node	node = first->ref.node->graph_iso;

				for_edge_sourcelist( node, e )
				{
					if(e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if (  e->lp_edge.history->element->hierarchy_level > 
						                   e->lp_edge.history->suc->element->hierarchy_level )
							move_vertical_line( e->lp_edge.lp_line, pos );
					}

					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_vertical_line( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if (  e->lp_edge.history->element->hierarchy_level > 
						                   e->lp_edge.history->suc->element->hierarchy_level )
							move_vertical_line_b( e->lp_edge.lp_line, pos );
					}


				}
				end_for_edge_sourcelist( node, e );
				for_edge_targetlist( node, e )
				{
					if(e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
							move_vertical_line_b( e->lp_edge.lp_line, pos );
					}

					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_vertical_line_b( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( ( e->lp_edge.history != e->lp_edge.history->suc ) &&
						     ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level ) )
							move_vertical_line( e->lp_edge.lp_line, pos );
					}
				}
				end_for_edge_targetlist( node, e );
			}
		}
		first = first->next_x;
	}
}

/********************************************************************************
function:	adjust_level_x_y
Input:	tree_top_sort_ref first_top, int pos;

	Move for every tree_top_sort_ref, which stands for a node in the graph, 
	the x-coord of all source- and targetedges of the node to pos if they 
	are no edges from a lower hierarchy-level to a higher.
	In this case x- and y-coord are changed because of the used orientation 
	type( given from calling routine)
********************************************************************************/

void			adjust_level_x_y	(tree_top_sort_ref first_top, int pos)
{
	tree_top_sort_ref	first = first_top;
	Edge		e;

	while ( ( first != NULL ) && ( first->first_x == first_top ) )
	{
		if ( first->type == LP_NODE )
		{
			if ( first->ref.node->leaf )
			{
				Node	node = first->ref.node->graph_iso;
						
				for_edge_sourcelist( node, e )
				{
					if(e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level > 
						       e->lp_edge.history->suc->element->hierarchy_level )
							move_horizontal_line( e->lp_edge.lp_line, pos );
					}

					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_horizontal_line( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level > 
						       e->lp_edge.history->suc->element->hierarchy_level )
							move_horizontal_line_b( e->lp_edge.lp_line, pos );
					}
				}
				end_for_edge_sourcelist( node, e );
				for_edge_targetlist( node, e )
				{
					if(e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
							move_horizontal_line_b( e->lp_edge.lp_line, pos );
					}

					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_horizontal_line_b( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
							move_horizontal_line( e->lp_edge.lp_line, pos );
					}
				}
				end_for_edge_targetlist( node, e );
			}
		}
		first = first->next_x;
	}
}

/********************************************************************************
function:	adjust_level_y
Input:	tree_top_sort_ref first_top, int pos;

	Move for every tree_top_sort_ref, which stands for a node in the graph, 
	the y-coord of all source- and targetedges of the node to pos if they 
	are no edges from a lower hierarchy-level to a higher
********************************************************************************/

void			adjust_level_y	(tree_top_sort_ref first_top, int pos)
{
	tree_top_sort_ref	first = first_top;
	Edge			e;

	while ( (first != NULL ) && ( first->first_y == first_top ) )
	{
		if ( first->type == LP_NODE )
		{
			if ( first->ref.node->leaf )
			{
				Node	node = first->ref.node->graph_iso;
				
				for_edge_sourcelist( node, e )
				{
					if (e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level > 
						       e->lp_edge.history->suc->element->hierarchy_level )
							move_horizontal_line( e->lp_edge.lp_line, pos );
					}

					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_horizontal_line( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level > 
						       e->lp_edge.history->suc->element->hierarchy_level ) 
							move_horizontal_line_b( e->lp_edge.lp_line, pos );
					}
				}
				end_for_edge_sourcelist( node, e );
				for_edge_targetlist( node, e )
				{
					if(e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level ) 
							move_horizontal_line_b( e->lp_edge.lp_line, pos );
					}

					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_horizontal_line_b( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
							move_horizontal_line( e->lp_edge.lp_line, pos );
					}
				}
				end_for_edge_targetlist( node, e );
			}
		}
		first = first->next_y;
	}
}

/********************************************************************************
function:	adjust_level_x_y
Input:	tree_top_sort_ref first_top, int pos;

	Move for every tree_top_sort_ref, which stands for a node in the graph, 
	the y-coord of all source- and targetedges of the node to pos if they 
	are no edges from a lower hierarchy-level to a higher.
	In this case x- and y-coord are changed because of the used orientation 
	type( given from calling routine)
********************************************************************************/

void			adjust_level_y_x	(tree_top_sort_ref first_top, int pos)
{
	tree_top_sort_ref	first = first_top;
	Edge			e;
	Node			node;

	while ( ( first != NULL ) && ( first->first_y == first_top ) )
	{
		if ( first->type == LP_NODE )
		{
			if ( first->ref.node->leaf )
			{
				node = first->ref.node->graph_iso;

				for_edge_sourcelist( node, e )
				{
					if(e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level > 
						       e->lp_edge.history->suc->element->hierarchy_level ) 
							move_vertical_line( e->lp_edge.lp_line, pos );
					}
						
					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_vertical_line( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level >
						       e->lp_edge.history->suc->element->hierarchy_level ) 
						{
							move_vertical_line_b( e->lp_edge.lp_line, pos );
						}
					}
				}
				end_for_edge_sourcelist( node, e );
				for_edge_targetlist( node, e )
				{
					if (e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level ) 
						{
							move_vertical_line_b( e->lp_edge.lp_line, pos );
						}
					}

					if ( e->lp_edge.history == e->lp_edge.history->suc ) move_vertical_line_b( e->lp_edge.lp_line, pos );

					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level > 
						       e->lp_edge.history->pre->pre->element->hierarchy_level ) 
						{
							move_vertical_line( e->lp_edge.lp_line, pos );
						}
					}
				}
				end_for_edge_targetlist( node, e );
			}
		}
		first = first->next_y;
	}
}

/*************************************************************************************
function:	adjust_tree
Input:	tree_ref tree

	Moves as many edges as possible in a way, that afterwards the edges of every node
	end in one coord of the node
*************************************************************************************/

void		adjust_tree	(tree_ref tree)
{
	pos_ref			list = (pos_ref) NULL;
	tree_top_sort_ref	level_first;
	pos_ref			pos;
	Edge			e;
	Node			node;
	int			alpha = tree->tree_rec.node->orientation_type;
	tree_top_sort_ref	first;
	tree_ref		cur;

	cur = tree->tree_rec.node->first_son;
	while ( cur )
	{
		if ( cur->tree_rec_type ==  TREE_NODE )
		{
			if ( !cur->tree_rec.node->leaf )
			{
				adjust_tree( cur );
			}
		}
		cur = cur->next_brother;
	}

	list = (pos_ref) NULL;

	first = tree->tree_rec.node->first_x;
	level_first = first;
	while ( first )
	{
		if ( first->first_x != level_first )
		{
			pos = best_pos( list );
			if ( pos )
			{
				switch( alpha )
				{
					case 1 :
					case 3 :
					case 5 :
					case 6 :
						adjust_level_x( level_first , pos->pos );
						break;
					default :
						adjust_level_x_y( level_first , pos->pos );
						break;
				}
			}
			level_first = first;
			free_pos_ref( list );
			list = NULL;
		}

		if ( first->type == LP_NODE )
		{
			if ( first->ref.node->leaf )
			{
				node = first->ref.node->graph_iso;
				for_edge_sourcelist( node, e )
				{
					if(e->lp_edge.history->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level < 
						       e->lp_edge.history->suc->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->x );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->y );
									break;
							}
						}
					}
					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->element->hierarchy_level < 
						       e->lp_edge.history->suc->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->x );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->y );
									break;
							}
						}
					}
				}
				end_for_edge_sourcelist( node, e );
				for_edge_targetlist( node, e )
				{
					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level < 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->x );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->y );
									break;
							}
						}
					}
					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL)
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level < 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->x );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->y );
									break;
							}
						}
					}
				}
				end_for_edge_targetlist( node, e );
			}
		}
		first = first->next_x;
	}

	pos = best_pos( list );
	if ( pos )
	{
		switch( alpha )
		{
			case 1 :
			case 3 :
			case 5 :
			case 6 :
				adjust_level_x( level_first , pos->pos );
				break;
			default :
				adjust_level_x_y( level_first , pos->pos );
				break;
		}
	}
	free_pos_ref( list );


	first = tree->tree_rec.node->first_y;
	list = (pos_ref) NULL;
	level_first = first;

	while ( first )
	{
		if ( first->first_y != level_first )
		{
			pos = best_pos( list );
			if ( pos )
			{
				switch( alpha )
				{
					case 1 :
					case 3 :
					case 5 :
					case 6 :
						adjust_level_y( level_first , pos->pos );
						break;
					default :
						adjust_level_y_x( level_first , pos->pos );
						break;
				}
			}
			level_first = first;
			free_pos_ref( list );
			list = NULL;
		}

		if ( first->type == LP_NODE )
		{
			if ( first->ref.node->leaf )
			{
				node = first->ref.node->graph_iso;
				for_edge_sourcelist( node, e )
				{
					if(e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL) 			
					{
						if ( e->lp_edge.history->element->hierarchy_level < 
						     e->lp_edge.history->suc->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->y );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->x );
									break;
							}
						}
					}
					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL ) 
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level < 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->y );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->x );
									break;
							}
						}
					}
				}
				end_for_edge_sourcelist( node, e );
				for_edge_targetlist( node, e )
				{
					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == IN_CONN_REL)  
					{
						if ( e->lp_edge.history->pre->element->hierarchy_level < 
						       e->lp_edge.history->pre->pre->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->y );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->x );
									break;
							}
						}
					}
					if( e->lp_edge.history->suc->element->tree_rec.history_elem->type == OUT_CONN_REL ) 
					{
						if ( e->lp_edge.history->element->hierarchy_level <
						       e->lp_edge.history->suc->element->hierarchy_level )
						{
							switch ( alpha )
							{
								case 1 :
								case 3 :
								case 5 :
								case 6 :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->y );
									break;
								default :
									list = add_pos_ref( list, e->lp_edge.lp_line->pre->x );
									break;
							}
						}
					}
				}
				end_for_edge_targetlist( node, e );
			}
		}
		first = first->next_y;
	}

	pos = best_pos( list );
	if ( pos )
	{
		/* write_pos_ref( list ); */
		switch( alpha )
		{
			case 1 :
			case 3 :
			case 5 :
			case 6 :
				adjust_level_y( level_first , pos->pos );
				break;
			default :
				adjust_level_y_x( level_first , pos->pos );
				break;
		}
	}
	free_pos_ref( list );
	list = NULL;
}


