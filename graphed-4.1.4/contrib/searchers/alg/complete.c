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
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>


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



/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/

/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/



void    CompleteInitAndTest(Sgraph sgraph, Method method)
{
	Snode	node;
	Sedge	edge;
	int	deg;

	int	number_of_nodes = COnodes();


	for_all_nodes( sgraph, node )
	{	
		deg = 0;
		for_sourcelist( node, edge )
		{
			deg++;
		} end_for_sourcelist( node, edge )

		if ( (deg+1) != number_of_nodes )
		{
			SetErrorAndReturnVoid( ERROR_NOT_A_COMPLETE_GRAPH );
		}
	} end_for_all_nodes( sgraph, node );
}


void    CompleteSearch(Sgraph sgraph, Method method)
{
	Snode	node, adjnode, node2;
	int	searchers;
	int	number_of_nodes = COnodes();

	if ( COanimate() )
	{
		if( method != METHOD_EDGE_SEARCH )
		{
			for_all_nodes( sgraph, node )
			{
				SetOn( node );	/* checks for the best way to move */
			} end_for_all_nodes( sgraph, node );
		}
		else
		{
			node = first_node_in_graph( sgraph );
			SetOn( node );
			for_all_adjacent_nodes( node, adjnode )
			{
				MoveTo( node, adjnode );
				for_all_adjacent_set_nodes( adjnode, node2 )
				{
					MoveTo( adjnode, node2 );
				} end_for_all_adjacent_set_nodes( adjnode, node2 )

			} end_for_all_adjacent_nodes( node, adjnode )

		}
	}
	else
	{
		switch ( method )
		{
		case METHOD_NODE_SEARCH:
			searchers = number_of_nodes;
			break;
		case METHOD_EDGE_SEARCH:
			if ( number_of_nodes == 2 || number_of_nodes == 3 )
				searchers = number_of_nodes -1;
			else
				searchers = number_of_nodes;
			break;
		case METHOD_MIXED_SEARCH:
			if ( number_of_nodes == 1 ) 
				searchers = 1;
			else
				searchers = number_of_nodes -1;
			break;
		default:
			SetErrorAndReturnVoid( IERROR_SWITCH_NO_CASE );
			break;
		}
		COsetHiddenMaxSearchers( searchers );
		COsetMaxSearchers( searchers );
	}
}



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
