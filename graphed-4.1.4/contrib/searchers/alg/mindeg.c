

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





void    SmallestDegreeSearch(Sgraph sgraph, Method method)
{
	Snode   node, sdeg_node, node2;
	int sdeg, deg, maxdeg  = 0;

	/* Initialize the minimum degree 'sdeg' */
	sdeg_node = first_node_in_graph( sgraph );
	sdeg = Degree( sdeg_node );

	/* ... and find the node with smallest degree */
	for_all_nodes( sgraph, node )
	{
		if ( method != METHOD_EDGE_SEARCH )
			deg = DegreeNode( node );
		else
			deg = Degree( node );
		if ( deg  < sdeg )
		{
			sdeg_node = node;
			sdeg = deg;
		}
		if ( deg > maxdeg ) maxdeg = deg;
	} end_for_all_nodes( sgraph, node );
	/* set a searcher on it */
	SetOnCheck( sdeg_node );
	


	while( sdeg < maxdeg+1 )
	{
		sdeg = maxdeg+1;
		for_all_set_nodes( sgraph, node )
		{
			if ( 	( DegreeNodeNotSet( node ) < sdeg && 
				   method != METHOD_EDGE_SEARCH )  
			     || ( DegreeEdgeNotClear( node ) < sdeg &&
				   method == METHOD_EDGE_SEARCH )  
			   ) 
			{
				sdeg_node = node;
				if ( method != METHOD_EDGE_SEARCH )
					sdeg = DegreeNodeNotSet( sdeg_node );
				else
					sdeg = DegreeEdgeNotClear( sdeg_node );
			}
		} end_for_all_set_nodes( sgraph, node );

		if ( method != METHOD_EDGE_SEARCH )
		{
			for_all_adjacent_not_set_nodes( sdeg_node, node )
			{
				SetOnCheck( node );
			} end_for_all_adjacent_not_set_nodes( sdeg_node, node );
		}
		else
		{
			for_all_adjacent_not_set_nodes( sdeg_node, node )
			{
				MoveToCheck( sdeg_node, node );
			} end_for_all_adjacent_not_set_nodes( sdeg_node, node );
			 
			for_all_set_nodes( sgraph, node )
			{
				for_all_adjacent_set_nodes( node, node2 )
				{
					
					MoveToCheck( node, node2 );
				} end_for_all_adjacent_set_nodes( node, node2 );
			} end_for_all_set_nodes( sgraph, node );
			
		}
	}

	COnotifyInfo( "Result is not the optimum" );

}
/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
