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


Layer   : 	control

Modul   :	control of search algorithms


********************************************************************************


Description of %M% :	
	
	controls the correct calls of the different algorithm procedures




********************************************************************************


Functions of %M% :

COsearch		supervise the search



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
#include <search/algorithm.h>
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

name        :	COsearch

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph_proc_info graphinfo	-I

return()    :	void

description :	supervises the search algorithm

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void			COsearch(Sgraph_proc_info graphinfo)
{

	Sgraph		graph,
			save_original_graph;
	Strategy	strategy;
	Method		method;
	unsigned int	algorithm;

	VoidFunction	proc;


	WNsetSearchBusy( TRUE );
	COreset();
	COsaveGraphinfo( graphinfo );


	graph = graphinfo->sgraph;

/********************************************/
/* test if graph is non-empty and connected */
/********************************************/
	COtestSearchability( graph );

	IfError
	{
		WNsetSearchBusy( FALSE );
		WNsetAlgorithmsBusy( FALSE );
		WNsetAnimationBusy( FALSE );
		PrintError();
		unlock_user_interface();
		return;
	}

/******************************************/
/* the current settings for the algorithm */
/******************************************/
	strategy = COstrategy();
	method = COmethod();
	algorithm = COalgorithm();

/*****************************************/
/* disable the algorithm setting buttons */
/*****************************************/
	if ( COanimation() )
	{
		WNsetAlgorithmsBusy( TRUE );
		WNsetAnimationBusy( TRUE );
		WNsetMethodBusy( TRUE );
	}

/************************************/
/* initialize the sgraph attributes */
/************************************/
	if ( method == METHOD_NODE_SEARCH )
	{
		save_original_graph = graph;
		graph = SimplifyGraph( graph, 0 );
		COinitSearchStructure( graph, FALSE );
	}
	else
		COinitSearchStructure( graph, TRUE );
/*****************************************************************************/
/* does the algorithm needs any testing for a special graph class or does it */
/* needs any initializing of the graph structure ? 			     */
/*****************************************************************************/
	proc = COalgorithmInitAndTest( algorithm );
	if ( proc != NULL )
		proc( graph, method );
	IfError
	{

	/*************************************/
	/* undo all the settings done before */
	/*************************************/
		PrintError();
		proc = COalgorithmFree( algorithm );
		if ( proc != NULL )
			proc( graph, method );

		PrintError(); /* may be an error while freeing the structure */
		COfreeSearchStructure( graph );
		PrintError(); /* may be an error while freeing the structure */

		WNsetSearchBusy( FALSE );
		WNsetAlgorithmsBusy( FALSE );
		WNsetAnimationBusy( FALSE );
		WNsetMethodBusy( FALSE );
		unlock_user_interface();

		return;
	}

	proc = COalgorithmSearch( algorithm );
	if ( proc != NULL )
		proc( graph, method );
	else
		return;
	IfError
	{
	/*************************************/
	/* undo all the settings done before */
	/*************************************/
		PrintError();
		proc = COalgorithmFree( algorithm );
		if ( proc != NULL )
			proc( graph, method );

		PrintError(); /* may be an error while freeing the structure */
		COfreeSearchStructure( graph );
		PrintError(); /* may be an error while freeing the structure */

		WNsetSearchBusy( FALSE );
		WNsetAlgorithmsBusy( FALSE );
		WNsetAnimationBusy( FALSE );
		WNsetMethodBusy( FALSE );
		unlock_user_interface();

		return;
	}
	proc = COalgorithmFree( algorithm );
	if ( proc != NULL )
		proc( graph, method );

	IfError
	{
	/*************************************/
	/* undo all the settings done before */
	/*************************************/
		PrintError(); /* may be an error while freeing the structure */
		COfreeSearchStructure( graph );
		PrintError(); /* may be an error while freeing the structure */

		WNsetSearchBusy( FALSE );
		WNsetAlgorithmsBusy( FALSE );
		WNsetAnimationBusy( FALSE );
		WNsetMethodBusy( FALSE );
		unlock_user_interface();

		return;
	}
	COfreeSearchStructure( graph );
	if ( method == METHOD_NODE_SEARCH )
	{
		if ( COanimation() )
		{
			COinitSearchStructure( save_original_graph, TRUE );
			ComputeSimplifiedSearch( graph );
			COfreeSearchStructure( save_original_graph );
		}
		remove_graph( graph );
		graph = save_original_graph;
	}
	
	IfError
	{
	/*************************************/
	/* undo all the settings done before */
	/*************************************/
		PrintError(); /* may be an error while freeing the structure */

		WNsetSearchBusy( FALSE );
		WNsetAlgorithmsBusy( FALSE );
		WNsetAnimationBusy( FALSE );
		WNsetMethodBusy( FALSE );
		unlock_user_interface();

		return;
	}	

	if ( COanimation() )
	{
	/******************************************************/
	/* the search was recorded ==> at the end of the tape */
	/******************************************************/
		COnotifyReset();

	/*********************************/
	/* show the search on the screen */
	/*********************************/
		COnotifyPlay();
	}
	else
	{
	/*******************/
	/* show the result */
	/*******************/
		COsetMaxSearchers( COhiddenMaxSearchers() );
	}


/***********************************************/
/* print all the information about this search */
/***********************************************/
	WNreport();

/********************************************************/
/* unlock for a short time, otherwise GraphEd complains */
/********************************************************/
	unlock_user_interface();

	return;
}
/******************************************************************************/


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
