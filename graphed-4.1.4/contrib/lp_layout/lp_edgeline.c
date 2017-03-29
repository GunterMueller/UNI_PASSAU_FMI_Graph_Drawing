#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

/****************************************************************/
/*								*/
/*	LGG Edgelines						*/
/*								*/
/****************************************************************/

/***************** FUNCTIONS ************************************/
/****************************************************************
int		vertical_lp_edgeline_segment	(lp_Edgeline, lp_Edgeline)
int		horizontal_lp_edgeline_segment	(lp_Edgeline, lp_Edgeline)
lp_Edgeline	new_lp_edgeline	(int, int)
lp_Edgeline 	add_to_lp_edgeline	(lp_Edgeline, int, int)
lp_Edgeline	delete_from_lp_edgeline	(lp_Edgeline, lp_Edgeline)
void		write_lp_edgeline	(lp_Edgeline)
lp_Edgeline	concat_lp_edgelines	(lp_Edgeline, lp_Edgeline)
lp_Edgeline	copy_lp_edgeline	(lp_Edgeline)
void		free_lp_edgeline	(lp_Edgeline)
Edgeline	cp_lp_edgeline_to_edgeline	(lp_Edgeline)
lp_Edgeline	cp_edgeline_to_lp_edgeline	(lp_Edgeline)
*****************************************************************/

/****************************************************************
function:	vertical_lp_edgeline_segment
Input:	lp_edgeline first, second

Output:	TRUE iff [first, second] is vertical
****************************************************************/

int	vertical_lp_edgeline_segment	(lp_Edgeline first, lp_Edgeline second)
{
	int	result;

	result = ( first->x == second->x );
	return( result );
}

/****************************************************************
function:	horizontal_lp_edgeline_segment
Input:	lp_edgeline first, second

Output:	TRUE iff [first, second] is horizontal
****************************************************************/

int	horizontal_lp_edgeline_segment	(lp_Edgeline first, lp_Edgeline second)
{
	int	result;

	result = ( first->y == second->y );
	return( result );
}

/****************************************************************
function:	new_lp_edgeline
Input:	int x, y

	Create a new lp_edgeline structure n with coord. (x, y)

Output:	Pointer to n
****************************************************************/

lp_Edgeline	new_lp_edgeline	(int x, int y)
{
	lp_Edgeline	result = 
			(lp_Edgeline) mymalloc( sizeof( struct lp_edgeline ) );

	result->x = x;
	result->y = y;
	result->iso = NULL;
	result->suc = result;
	result->pre = result;
	return( result );
}

/***************************************************************
function:	add_to_lp_edgeline
Input:	lp_Edgeline line, int x, y

	Create a new lp_Edgeline with coord. (x, y) and put it
	at the end of line

Output:	line
***************************************************************/

lp_Edgeline 	add_to_lp_edgeline	(lp_Edgeline line, int x, int y)
{
	lp_Edgeline	new = new_lp_edgeline( x, y );
	lp_Edgeline	result;

	if ( line == NULL )
	{
		result = new;
	}
	else
	{
		line->pre->suc = new;
		new->pre = line->pre;
		new->suc = line;
		line->pre = new;
		result = line;
	}
	return( result );
}

/***************************************************************
function:	delete_from_lp_edgeline
Input:	lp_Edgeline line, point

	Delete edge point of line

Output:	line
***************************************************************/

lp_Edgeline	delete_from_lp_edgeline	(lp_Edgeline line, lp_Edgeline point)
{
	lp_Edgeline	result = NULL;

	if ( point->suc == point )
	{ 
		result = NULL;
		free( point );
	}
	else
	{
		if ( line == point ) result = line->suc;
		else	result = line;

		point->pre->suc = point->suc;
		point->suc->pre = point->pre;
		free( point );
	}
	return( result );
}

/*****************************************************************
function:	write_lp_edgeline
Input:	lp_Edgeline line

	Print all edges of line on stdio
*****************************************************************/

void	write_lp_edgeline	(lp_Edgeline line)
{
	lp_Edgeline	cur;

	for_lp_edgeline( line, cur )
	{
		printf("%d %d ***** ", cur->x, cur->y);
	}
	end_for_lp_edgeline( line, cur );
	printf("\n");
}

/*****************************************************************
function:	concat_lp_edgelines
Input:	lp_Edgeline first, second

	Put second at the end of first

Output:	first
*****************************************************************/

lp_Edgeline	concat_lp_edgelines	(lp_Edgeline first, lp_Edgeline second)
{
	lp_Edgeline	result = (lp_Edgeline)NULL;
	lp_Edgeline	last_of_first;

	if ( first == NULL ) 
	{
		result = second;
	}
	else
	{
		if ( second == NULL )
		{
			result = first;
		}
		else
		{
			last_of_first = first->pre;
			first->pre->suc = second;
			first->pre = second->pre;
			second->pre->suc = first;
			second->pre = last_of_first;
			result = first;
		}
	}		
	return result;
}

/*****************************************************************
function:	copy_lp_edgeline
Input:	lp_Edgeline line

	create a copy c of line

Output:	c
*****************************************************************/

lp_Edgeline	copy_lp_edgeline	(lp_Edgeline line)
{
	lp_Edgeline	result = NULL;
	lp_Edgeline	cur;

	for_lp_edgeline( line, cur )
	{
		result = add_to_lp_edgeline( result, cur->x, cur->y );
	}
	end_for_lp_edgeline( line, cur );
	return( result );
}

/*****************************************************************
function:	free_lp_edgeline
Input:	lp_Edgeline line

	Free memory space of line
*****************************************************************/

void	free_lp_edgeline	(lp_Edgeline line)
{
	lp_Edgeline	cur = line;
	lp_Edgeline	del;

	if ( cur != NULL )
	{
		do
		{
			del = cur;
			cur = cur->suc;
			free( del );
		}
		while ( cur != line );
	}
}

/****************************************************************
function:	cp_lp_edgeline_to_edgeline
Input:	lp_Edgeline line

	Create a edgeline e whitch is the copy of line

Output:	e
****************************************************************/

Edgeline	cp_lp_edgeline_to_edgeline	(lp_Edgeline line)
{
	lp_Edgeline	cur;
	Edgeline	result = NULL;
	Edgeline	real_result = NULL;

	if ( line != NULL )
	{
		cur = line;
		result = new_edgeline(cur->x, cur->y);
		real_result = result;
		cur = cur->suc;
		while ( cur != line )
		{
			result = add_to_edgeline(result, cur->x, cur->y);
			cur = cur->suc;
		}
	}
	return( real_result );
}

/****************************************************************
function:	cp_edgeline_to_lp_edgeline
Input:	Edgeline line

	Create a lp_Edgeline l whitch is the copy of line

Output:	l
****************************************************************/

lp_Edgeline	cp_edgeline_to_lp_edgeline	(Edgeline line)
{
	Edgeline	cur;
	lp_Edgeline	result = NULL;
	
	for_edgeline( line, cur )
	{
		result = add_to_lp_edgeline( result, cur->x, cur->y );
	}
	end_for_edgeline( line, cur );
	return( result );
}

/*****************************************************************
function:	neughbor_dirs
Input:	int dir1, dir2

Output:	TRUE iff. dir1 is neighbor direction to dir2
*****************************************************************/

int	neighbor_dirs(int dir1, int dir2)
{
	return( ( ( dir1 + dir2 ) % 2 ) == 1 );
}

/*****************************************************************
function:	Xi
Input:	int dir1, dir2

Output:	0 iff. dir1 = dir2
	1 iff. dir1 and dir2 are neighbor directions
	2 iff. dir1 and dir2 are opposite directions
*****************************************************************/

int	Xi	(int dir1, int dir2)
{
	if ( dir1 == dir2 ) return( 0 );
	if ( neighbor_dirs(dir1, dir2) ) return( 1 );
	return( 2 );
} 

/*****************************************************************
function	lp_dir
Input:	lp_Edgeline first, second

Output:	Direction of [first, second]
*****************************************************************/

int	lp_dir		(lp_Edgeline first, lp_Edgeline second)
{
	if ( first->x == second->x )
	{
		if ( first->y < second->y ) return(U_dir);
		return(D_dir);
	}
	else
	{
		if (first->x < second->x ) return(R_dir);
		return(L_dir);
	}
}

/*****************************************************************
function:	first_dir
Input:	Edge edge

Output:	Direction of first segment of edge
*****************************************************************/

int	first_dir	(Edge edge)
{
	return( lp_dir( edge->lp_edge.lp_line, edge->lp_edge.lp_line->suc ) );
}

/*****************************************************************
function:	last_dir
Input:	Edge edge

Output:	Direction of last segment of edge
*****************************************************************/

int	last_dir	(Edge edge)
{
	return( lp_dir( edge->lp_edge.lp_line->pre->pre, edge->lp_edge.lp_line->pre ) );
}

/*****************************************************************
function:	T_dir
Input:	int dir, t

Output:	Resulting direction by applying orientation t on dir
*****************************************************************/

int	T_dir	(int dir, int t)
{
	int	result = 0;

	switch( t )
	{
		case 5:
			switch( dir )
			{
				case L_dir:	result = R_dir;	break;
				case U_dir:	result = U_dir;	break;
				case R_dir:	result = L_dir;	break;
				case D_dir:	result = D_dir;	break;
			}
			break;
		case 6:
			switch( dir )
			{
				case L_dir:	result = L_dir;	break;
				case U_dir:	result = D_dir;	break;
				case R_dir:	result = R_dir;	break;
				case D_dir:	result = U_dir;	break;
			}
			break;
		case 7:
			switch( dir )
			{
				case L_dir:	result = D_dir;	break;
				case U_dir:	result = R_dir;	break;
				case R_dir:	result = U_dir;	break;
				case D_dir:	result = L_dir;	break;
			}
			break;
		case 8:
			switch( dir )
			{
				case L_dir:	result = U_dir;	break;
				case U_dir:	result = L_dir;	break;
				case R_dir:	result = D_dir;	break;
				case D_dir:	result = R_dir;	break;
			}
			break;
				
		default:
			result = ( dir + 9 - t ) % 4;
			break;
	}	
	return( result );
}

/****************************************************************
function:	T_side
Input:	int side, t;

Output:	Resulting side by applying orientation t to side
****************************************************************/

int	T_side	(int side, int t)
{
	return( T_dir(side, t) );
}

/***************************************************************
function:	opposite_side
Input:	int side

Output:	Opposite side of side
***************************************************************/

int opposite_side	(int side)
{
	return( ( side + 2 ) % 4 );
}


int opposite_dir	(int dir)
{
	return( ( dir + 2 ) % 4 );
}

/***************************************************************
function:	clip_lp_edgeline
Input:	lp_Edgeline outside, inside, Node node

	Cut [inside, outside] at the border of node b;
	the resulting segment is [b, outside]
***************************************************************/

void	clip_lp_edgeline(lp_Edgeline outside, lp_Edgeline inside, Node node)
{
	int	x1, y1, x2, y2;
	
	x1 = node->x - node->box.r_width / 2;
	x2 = node->x + node->box.r_width / 2;
	y1 = node->y - node->box.r_height / 2;
	y2 = node->y + node->box.r_height / 2;

	if ( outside->x == inside->x )
	{
		if ( outside->y >= y2 ) inside->y = y2;
		else inside->y = y1;
	}
	else
	{
		if ( outside->x >= x2 ) inside->x = x2;
		else inside->x = x1;
	}
}

/***************************************************************
function:	write_tree_lp_edgelines
Input:	tree_ref tree

	Print all lp_edgelines of the derivation net beginning with
	tree on stdio
***************************************************************/

void	write_tree_lp_edgelines	(tree_ref tree)
{
	tree_ref	cur = tree->tree_rec.node->first_son;

	while	( cur != NULL )
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
			write_tree_lp_edgelines( cur );
		}
		else
		{
			printf("TREE LP EDGELINE ****************\n");
			write_lp_edgeline( cur->tree_rec.history_elem->line );
		}
		cur = cur->next_brother;
	}
}

/****************************************************************
function:	lp_edgeline_length
Input:	lp_Edgeline line

Output:	Number of bends + 2 in line
****************************************************************/

int		lp_edgeline_length	(lp_Edgeline line)
{	
	int		count = 0;
	lp_Edgeline	cur;

	for_lp_edgeline( line, cur )
	{
		count++;
	}
	end_for_lp_edgeline( line, cur );
	return( count );
}

/****************************************************************
function:	bend_number_of_unique_graph
Input:	Graph graph

Output:	Sum of bends of all lp_Edgelines of graph
****************************************************************/

int	bend_number_of_unique_graph	(Graph graph)
{
	Node	n;
	Edge	e;
	int	count = 0;
	int	edge_number = 0;

	for_nodes( graph, n )
	{
		for_edge_sourcelist( n, e )
		{
			edge_number++;
			count = count + lp_edgeline_length( e->lp_edge.lp_line ) - 2;
		}
		end_for_edge_sourcelist( n, e )
	}
	end_for_nodes( graph, n );
	return( count );
}
	




