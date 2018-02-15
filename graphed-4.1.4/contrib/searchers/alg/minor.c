#ifdef UNIX5
#ifndef lint
static char version_id[] = "%Z% File : %M% %E%  %U%  Version %I% Copyright (C) 1992/93 Schweikardt Andreas";
#endif
#endif 

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




	File	:	%M%

	Date	:	%E%	(%U%)

	Version	:	%I%

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Interface (graphic):	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	

Modul   :	


********************************************************************************


Description of %M% :




********************************************************************************


Functions of %M% :



*******************************************************************************/


/******************************************************************************
*                                                                             *
*			standard includes				      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/algorithm.h>
#include <search/control.h>
#include <search/move.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*			local defines 		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local macros  		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local types		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local functions					      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			global variables				      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/


static int	low_deg;
static int	N;
/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/

static Snode	MinorGetLowestDegreeNode(Sgraph graph)
{
	int	mindeg = N,
		deg;
	Snode	node,
		return_node = empty_snode;
	Slist	list = empty_slist,
		elt;
	bool	has_degree_one = FALSE;

	for_all_nodes( graph, node )
	{
		deg = DegreeNode( node );	
		if ( deg == 1 )
		{
			list = add_to_slist( list, make_attr( ATTR_DATA, node ) );
			has_degree_one = TRUE;
		}
		else if ( deg < mindeg )
		{
			mindeg = deg;
			return_node = node;
		}
	} end_for_all_nodes( graph, node );

	if ( mindeg > low_deg && !has_degree_one) low_deg = mindeg;

	if ( list != empty_slist )
	{
		for_slist( list, elt )
		{
			node = (Snode)attr_data( elt );
			remove_node( node );
			N--;
		} end_for_slist( list, elt )

		free_slist( list );

		return( empty_snode );
	}
	return( return_node );
}

static void	MinorContraction(Sgraph graph, Snode node)
{
	Snode	adj_node,
		the_other_node = node;
	int	mindeg = N,
		deg;

	for_all_adjacent_nodes( node, adj_node )
	{
		if ( node == adj_node ) continue;
		deg = DegreeNode( adj_node );
		if ( deg < mindeg )
		{
			mindeg = deg;
			the_other_node = adj_node;
		}
	} end_for_all_adjacent_nodes( node, adj_node );


	for_all_adjacent_nodes( the_other_node, adj_node )
	{
		if ( node == adj_node ) continue;
		make_edge( node, adj_node, make_attr( ATTR_DATA, NULL ) );
	} end_for_all_adjacent_nodes( the_other_node, adj_node )

	remove_node( the_other_node );
	N--;
	
	return;
}

/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/

void	MinorSearch(Sgraph graph, Method method)
{
	Sgraph	local;

	Snode	node;

	low_deg = 1;
	N = COnodes();

	local = MakeCopyOfGraph( graph );

	do
	{

		node = MinorGetLowestDegreeNode( local );		
		if ( node != empty_snode )
			MinorContraction( local, node );
	} while ( N > 1 );

	if ( method != METHOD_MIXED_SEARCH )
	{
		if ( low_deg >= 1 && low_deg && method == METHOD_EDGE_SEARCH )
		{ }
		else
		{
			low_deg++;
		}
	}

	COsetHiddenMaxSearchers( low_deg );
	COsetMaxSearchers( low_deg );

	remove_graph( local );

	return;
}

/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
