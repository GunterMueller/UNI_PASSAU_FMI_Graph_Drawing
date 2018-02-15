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


Layer   : 	Control

Modul   :	manual control


********************************************************************************


Description of %M% :	
	starts, initialize and stops manually controlled search.


********************************************************************************


Functions of %M% :

COstartManualControl	initializes the manually controlled search
COstopManualControl	stops ...
COmanualSetOn		sets a searcher a node
COmanualMove		moves a searcher along an edge



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
#include <search/window.h>
#include <search/control.h>
#include <search/move.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COstartManualControl

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph_proc_info graphinfo	-I

return()    :	void

description :	initializes a manually controlled search

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COstartManualControl(Sgraph_proc_info graphinfo)
{
	Sgraph	graph = graphinfo->sgraph;

	COsaveGraphinfo( graphinfo );

/*****************************************************************************/
/* if nothing was recorded then initialize (and test connectivity) the graph */
/* else the graph is already initialized (caused by the recording!)	     */
/*****************************************************************************/
	if ( !COrecorded() )
	{

		COtestSearchability( graph );
		IfErrorReturn;
		COinitGraphAttributes( graph );
	}

	set_user_event_func( SGRAPH_UEV_DOUBLE_CLICK, WNnotifyManualEvent );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COstopManualControl

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph_proc_info graphinfo	-I

return()    :	void

description :	stops the manually controlled search, ie. removes the clicking
		function.

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COstopManualControl(Sgraph_proc_info graphinfo)
{

	remove_user_event_func( SGRAPH_UEV_DOUBLE_CLICK, WNnotifyManualEvent );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COmanualSetOn

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		set a searcher on 'node'-I

return()    :	void

description :	sets a searcher on the node 'node'

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COmanualSetOn(Snode node)
{
	dispatch_user_action( UNSELECT );
	ManualSetOn( node );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COmanualMove

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		move along 'edge'-I

return()    :	void

description :	moving a searcher along the edge 'edge'

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COmanualMove(Sedge edge)
{
	dispatch_user_action( UNSELECT );
	ManualMove( edge );
}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
