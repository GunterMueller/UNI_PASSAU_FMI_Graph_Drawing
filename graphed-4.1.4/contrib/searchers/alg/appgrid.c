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

#include <math.h>

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

static int      mindeg, maxdeg = 0;
static Snode    startnode;



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

void	AppGridInitAndTest(Sgraph graph, Method method)
{

        Snode   node;
        int     deg;
        int     number_mindeg_nodes = 0;
        
        startnode = first_node_in_graph( graph );
        mindeg = DegreeNode( startnode );
 
        for_all_nodes( graph, node )
        {
                deg = DegreeNode( node );
                if ( mindeg > deg )
                {
                        mindeg = deg;
                        startnode = node;
                        number_mindeg_nodes = 0;
                }
                if ( mindeg == deg ) number_mindeg_nodes++;
 
                if ( maxdeg < deg ) maxdeg = deg;
 
        } end_for_all_nodes( graph, node );
 
        if ( number_mindeg_nodes != (1<<mindeg) )
                SetErrorAndReturnVoid( ERROR_NOT_A_GRID );


}

void	AppGridSearch(Sgraph graph, Method method)
{
	unsigned int	dim = mindeg;
	unsigned int	searchers;

	searchers = (unsigned int)pow( (float)COnodes(), (float)(dim-1)/(float)dim );

	searchers++;

	if ( method != METHOD_MIXED_SEARCH )
		searchers++;
	
	
	COsetHiddenMaxSearchers( searchers );
	COsetMaxSearchers( searchers );

	COnotifyInfo( "Only an estimation ! (No animation)" );

}


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
