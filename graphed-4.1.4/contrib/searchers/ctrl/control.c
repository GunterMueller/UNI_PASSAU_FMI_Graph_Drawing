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
	Other			:	


********************************************************************************


Layer   : 	Control

Modul   :	Control


********************************************************************************


Description of %M% :

This is the control level of the search module, all input goes through
this level. But a picture is more worth than a thousand words:

		+-------------------------------------+
		|				      |
		| graphical user interface 	      |
		|				      |
		+-------------------------------------+
			|	     A		A
			|useraction  |reaction  |
			V	     |		|
		+-----------------------+	| results
		|			|	|	
		| control.c (this file) |	|
		|			|	|
		+-----------------------+-------------+
		|				      |
		| control layer			      |
		|				      |
		+-------------------------------------+
			|		A
			|search		|report
			V		|
		+-------------------------------------+
		|				      |
		| basic algorithms and graph handling |
		|				      |
		+-------------------------------------+



********************************************************************************


Functions of %M% :

COnotifyInfo		a short information about the used algorithm
COnotifyInterval	processes the time between two steps
COnotifyPlay		shows a recorded search
COnotifyAnimation	notifies the toggle
COnotifyStrategy	changes the algorithm class
COnotifyAlgorithm	chooses the right algorithm
COnotifyMethod		chooses the right method
COnotifyManual		turn on or off manual control
COnotifyStop		stop the animation of a search (opposite to ..Play)
COnotifySearch		starts the algorithm
COnotifyRecorded	notifies if anything is recorded
COnotifyClear		clears up the graph and all recorded things
COnotifyBackStep	steps back in the animation
COnotifyRewind		fast back stepping
COnotifyRewindStep	one fast step back
COnotifyForward		opposite to ..Rewind
COnotifyForwardStep	..
COnotifyStep		..
COnotifyLastFewSteps	cannot stop an animation evrywhere
COnotifyReset		to the beginning of the animation, no deletion


*******************************************************************************/

/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <sgraph/std.h>

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

extern void    WNrewindAnimation(void);
extern void    WNshowAnimation(void);
extern void    WNforwardAnimation(void);

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

name        :	COnotifyInfo

arguments   :	
   	type		name		description(-I/-O)
1.	char*		string		string to write-I	

return()    :	void

description :	a short description of the algorithm and its result

use	    :	only for algorithm info

restrictions:	string must be allocated

bugs	    :	none reported

*******************************************************************************/
void	COnotifyInfo(char *string)
{
	WNshowInfo( string );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyInterval

arguments   :	
   	type		name		description(-I/-O)
1.	unsigned int	value		-I

return()    :	void

description :	saves the value, and checks if any animation is shown,
		if so, the timer is updated to the time interval      

use	    :	when time interval has changed, only via GUI

restrictions:	value only between INTERVAL_MAX and ..MIN

bugs	    :	none reported

*******************************************************************************/
void	COnotifyInterval(unsigned int value)
{
	COsetInterval( value );
	if ( COplay() && !COfastPlay() )
	{
	/****************************************************/
	/* set the new time interval to the animated search */
	/****************************************************/
		COnotifyPlay();
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyAnimation

arguments   :	
   	type		name		description(-I/-O)
1.	bool        	value		on or off-I

return()    :	void

description :	saves if animation is turned on or off

use	    :	only via GUI

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyAnimation(int value)
{
	COsetAnimation( value );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyStrategy

arguments   :	
   	type		name		description(-I/-O)
1.	Strategy    	value		strategy class number-I

return()    :	void

description :	saves strategy class and displays the corresponding algorithms
		of the class

use	    :	only via GUI

restrictions:	value only 0, 1, or 2

bugs	    :	none reported

*******************************************************************************/
void		COnotifyStrategy(Strategy value)
{

	COsetStrategy( value );

/********************************************/
/* update the algorithms, show the new menu */
/********************************************/
	WNmenuAlgorithms( );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyAlgorithm

arguments   :	
   	type		name		description(-I/-O)
1.	unsigned int	value		number of algorithm-I

return()    :	void

description :	saves the number of the current active algorithm

use	    :	only via GUI

restrictions:	algorithm must exist

bugs	    :	none reported

*******************************************************************************/
void		COnotifyAlgorithm(unsigned int value)
{
	COsetAlgorithm( value );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyMethod

arguments   :	
   	type		name		description(-I/-O)
1.	Method		value		-I

return()    :	void

description :	saves the current method

use	    :	only via GUI

restrictions:	mixed, node or edge search

bugs	    :	none reported

*******************************************************************************/
void	COnotifyMethod(Method value)
{
	COsetMethod( value );
	
	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyManual

arguments   :	
   	type		name		description(-I/-O)
1.	bool		value		on or off-I

return()    :	void

description :	turns manual control on or off, makes all buttons used for
		algorithm search inactive

use	    :	only via GUI

restrictions:	

bugs	    :	unlock_user_interface() is necessary for clicking

*******************************************************************************/
void	COnotifyManual(int value)
{

	WNshowFilename();
	COsetManual( value );

/*************************************************/
/* if manual mode is set, animation is turned on */
/*************************************************/
	if ( value )
	{
		unlock_user_interface();

	/*******************************************************/
	/* stop any active animation, the user will do it know */
	/*******************************************************/
		COnotifyStop();


	/*****************************************************/
	/* set all information to zero, but only if the user */
	/* do not want to complete a search 		     */
	/*****************************************************/
		if ( !COrecorded() )
			COreset();
	
	/*********************************************/
	/* make algorithm control buttons not usable */
	/*********************************************/
		WNsetAnimationBusy( TRUE );
		WNsetSearchBusy( TRUE );
		WNsetAlgorithmsBusy( TRUE  );


	/******************/
	/* allow clicking */
	/******************/
		call_sgraph_proc( COstartManualControl, NULL );

		
		IfError
		{
			WNsetAnimationBusy( FALSE );
			WNsetSearchBusy( FALSE );
			WNsetAlgorithmsBusy( FALSE );
			COsetManual( FALSE );
			WNsetManual( );
			PrintError();
			return;
		}

	/**************************/
	/* short info to the user */
	/**************************/
		COnotifyInfo( "Manually controlled search" );

	}
	else /* manual mode turned off */
	{
	/*****************************************************************/
	/* if nothing was recorded, the algorithm control is made usable */
	/* again 							 */
	/*****************************************************************/
		if ( !COrecorded() )
		{
		/**********************************************************/
		/* nothing recorded == nothing to show, any algorithm can */
		/* be applied again					  */
		/**********************************************************/
			WNsetAnimationBusy( FALSE );
			WNsetSearchBusy( FALSE );
			WNsetAlgorithmsBusy( FALSE );
		}		
		else if ( COplay() )
		{
		/**************************/
		/* stop any animation now */
		/**************************/
			COnotifyStop();
		}

	/*******************************/
	/* clicking is forbidden again */
	/*******************************/
		call_sgraph_proc( COstopManualControl, NULL );
		IfError
		{
			PrintError();
			return;
		}

	/***************/
	/* remove info */
	/***************/
		COnotifyInfo( "" );

	/**********************************************/
	/* if anything was recorded, disallow editing */
	/**********************************************/
		if ( COrecorded() ) 
			lock_user_interface();
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifySearch

arguments   :	

return()    :	void

description :	starts the current algorithm

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifySearch(void)
{

/*****************************************************************/
/* we have to made some changes to the graph, ie. initialization */
/*****************************************************************/
	unlock_user_interface();

	
	WNshowFilename();

/*******************/
/* remove old info */
/*******************/
	COnotifyInfo( "" );

/***********************************/
/* set the appropriate information */
/***********************************/
	COsetSearchers( 0 );
	COsetMaxSearchers( 0 );
	COsetHiddenSearchers( 0 );
	COsetHiddenMaxSearchers( 0 );
	COsetEdgesNotClear( COedges() );
	COsetEdgesClear( 0 );
	COsetNodesNotSet( COnodes() );
	COsetNodesUnset( 0 );
	COsetNodesSet( 0 );
	COsetSteps( 0 );

/*********************************/
/* avoid flickering of the graph */
/*********************************/
	dispatch_user_action( UNSELECT );

/*****************************/
/* Search button is inactive */
/*****************************/
	WNsetSearchBusy( TRUE );

/******************************************/
/* call the Search routine with the graph */
/******************************************/
	call_sgraph_proc( COsearch, NULL );

/*********************************************************/
/* if animation is on, then lock the graph for all edits */
/*********************************************************/
	if ( COanimation() )
		lock_user_interface();
	else 
	{
	/*****************************/
	/* ready for the next search */
	/*****************************/
		WNsetSearchBusy( FALSE );
		WNsetAnimationBusy( FALSE );
		WNsetAlgorithmsBusy( FALSE );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyRecorded

arguments   :	

return()    :	void

description :	notifies if a search was recorded, also manual controlled
		search

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyRecorded(void)
{
	COsetRecorded( TRUE );

/***************************************************************/
/* Method cannot be changed anymore, useful for manual control */
/***************************************************************/
	WNsetMethodBusy( TRUE );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyClear

arguments   :	

return()    :	void

description :

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyClear(void)
{
	unlock_user_interface();
	WNshowFilename();

/***************************************/
/* stop and delete any recorded search */
/***************************************/
	if ( COrecorded() )
	{
		COnotifyStop();
		COsetStop();
		COsetRecorded( FALSE );
		COsetBackstep( FALSE );
		MVrecordReset();
		MVrecordClear();
	}
	else
	{
	/************************************************/
	/* repaint and initailize graph (if neccessary) */
	/************************************************/
		COinitGraphAttributes( COgetGraph() );
	}

/*****************************************************************************/
/* if manual mode isn't turned on, show all algorithms control buttons again */
/*****************************************************************************/
	if ( !COmanual() )
	{
		WNsetSearchBusy( FALSE );
		WNsetAlgorithmsBusy( FALSE );	
		WNsetAnimationBusy( FALSE );
		COnotifyInfo( "" );
	}

/********************************/
/* reset the report information */
/********************************/
	COsetSearchers( 0 );
	COsetMaxSearchers( 0 );
	COsetHiddenSearchers( 0 );
	COsetHiddenMaxSearchers( 0 );
	COsetEdgesNotClear( COedges() );
	COsetEdgesClear( 0 );
	COsetNodesNotSet( COnodes() );
	COsetNodesUnset( 0 );
	COsetNodesSet( 0 );
	COsetSteps( 0 );

/*****************************************/
/* yeah, the method can be changed again */
/*****************************************/
	WNsetMethodBusy( FALSE );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyBackstep

arguments   :	

return()    :	void

description :	do one step backwards in the animation of the search

use	    :	always when the user will do a step backwards

restrictions:	do not call via timer

bugs	    :	none reported

*******************************************************************************/
void	COnotifyBackstep(void)
{

/***************************************************************************/
/* we can make only a backstep if something was recorded and we are not at */
/* the beginning of the recorded search (will normal animation stepping is */
/* also forbidden)							   */
/***************************************************************************/
	if ( COrecorded() && !COplay() && !COrecStartOfTape() )
	{
		COsetBackstep( TRUE );
		COsetPlay();
		MVrecordBackstep();
		COnotifyLastFewSteps();
		COsetBackstep( FALSE );

		COsetStop();
	}
	else
		bell();
	/***************************************************************
		nothing was recorded ==> cannot step
		but if something is recorded and is showed right now
			=> do not break the show, use stop button 
	***************************************************************/

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyRewind

arguments   :	

return()    :	void

description :	starts the rewinding of the animation

use	    :	

restrictions:	only to start, do not use for consecutive steps
		(see COnotifyRewindStep)

bugs	    :	none reported

*******************************************************************************/
void 	COnotifyRewind(void)
{

/*****************************************************************************/
/* is there anything recorded and aren't we at the beginning of the search ? */
/*****************************************************************************/
	if ( COrecorded() && !COrecStartOfTape() )
        {
		lock_user_interface();
                WNsetStop();
                COsetFastPlay();
                COsetBackstep( TRUE );
                WNsetRewindBusy( TRUE );
		WNrewindAnimation();
        }
	else
		bell();
	/*****************************************************
		if nothing is recorded, then we cannot rewind
		   ==> nothing happens
	*****************************************************/

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyRewindStep

arguments   :	

return()    :	void

description :	do one step backwards in the animation 

use	    :	call only via a timer

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyRewindStep(void)
{
	MVrecordBackstep();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyStop

arguments   :	

return()    :	void

description :	stop the animation

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyStop(void)
{
	if ( COplay() )
	{
		if ( COmanual() )
		{
		/************************/
		/* allow clicking again */
		/************************/
			unlock_user_interface();
		}

	/******************/
	/* stop the timer */
	/******************/
		WNstopAnimation();
		WNsetPlay();
		COsetStop();
		WNsetRewindBusy( FALSE );
		/* WNsetForwardBusy( FALSE ); done already */
		COsetBackstep( FALSE );
	}
	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyPlay

arguments   :	

return()    :	void

description :	start the animation

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyPlay(void)
{

/******************************************************/
/* anything recorded ? and not at the of the search ? */
/******************************************************/
        if ( COrecorded() && !COrecEndOfTape() )
        {
	/********************/
	/* disallow editing */
	/********************/
		lock_user_interface();

                WNsetStop();
                COsetBackstep( FALSE );
                COsetPlay();

	/*****************/
	/* turn timer on */
	/*****************/
		WNshowAnimation();
        }      
	else
		bell();
	/***************************************************
		play makes no sense if nothing was recorded 
	***************************************************/

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyForward

arguments   :	

return()    :	void

description :	a fast show of the animation

use	    :	only start the fast animation, do not use for consecutive steps
		(see COnotifyForwardStep)

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyForward(void)
{

/********************************************************/
/* anything recorded ? and not at the end of the search */
/********************************************************/
        if ( COrecorded() && !COrecEndOfTape() )
        {
		lock_user_interface();
                COsetFastPlay();
                WNsetStop();
                COsetBackstep( FALSE );
                WNsetForwardBusy( TRUE );
                /* WNsetRewindBusy( FALSE ); done already */

	/****************************************************/
	/* start the fast timer for the "fastest" animation */
	/****************************************************/
		WNforwardAnimation();
        }          
	else
		bell();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyForwardStep

arguments   :	

return()    :	void

description :	make on fast step forward

use	    :	call only via timer

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyForwardStep(void)
{
	MVrecordStep();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyStep

arguments   :	

return()    :	void

description :	only one step forward

use	    :	

restrictions:	do not call via timer

bugs	    :	none reported

*******************************************************************************/
void	COnotifyStep(void)
{

/****************************************************************************/
/* we do not make any step if nothing was recorded, or we are at the end of */
/* the search or while animation of the search 				    */
/****************************************************************************/
	if ( COrecorded() && !COplay() && !COrecEndOfTape() )
	{
	/******************************/
	/* the search is animated now */
	/******************************/
		COsetPlay();

                COsetBackstep( FALSE );

	/********************/
	/* proceed ONE step */
	/********************/
		MVrecordStep();

		COnotifyLastFewSteps();
		COsetStop();
	}
	else
		bell();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyLastFewSteps

arguments   :	

return()    :	void

description :	if user presses the stop button, the search cannot stop
		in every configuration, e.g. a searcher is moving over
		an edge (he moves then to the target node) or if in node
		search, all edges (between two set nodes ) must be cleared 
		before stopping

use	    :	after every single step or if the stop button was pressed

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyLastFewSteps(void)
{
	if ( COplay() )
	{
	/*************************************************************/
	/* only if the search is animated now do the steps until the */
	/* configuration is again in normal form 		     */
	/*************************************************************/
		if ( CObackstep() )
			do {} while( MVrecordToNextBackstep() );
		else
			do {} while( MVrecordToNextStep() );
	}
	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COnotifyReset

arguments   :	

return()    :	void

description :	sets the recorded search to the beginning, but don't delete
		it

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COnotifyReset(void)
{
	if ( COrecorded() )
	{
	/**********************/
	/* stop the animation */
	/**********************/
		COnotifyStop();
		COsetStop();
		WNsetPlay();
		WNsetForwardBusy( FALSE );
		WNsetRewindBusy( FALSE );

	/**********************************************/
	/* set the recorded search to beginning again */
	/**********************************************/
		MVrecordReset();

	/****************************************/
	/* check if clicking is allowed again ? */
	/****************************************/
		if ( COmanual() )
			unlock_user_interface();
		else
			lock_user_interface();

	/****************************/
	/* reset report information */
	/****************************/
	   	COsetMaxSearchers( 0 );
	   	COsetSearchers( 0 );
	   	COsetHiddenMaxSearchers( 0 );
	   	COsetHiddenSearchers( 0 );
                COsetEdgesNotClear( COedges() );
                COsetEdgesClear( 0 );
                COsetNodesNotSet( COnodes() );
                COsetNodesUnset( 0 );
                COsetNodesSet( 0 );
                COsetSteps( 0 );

	}
	else
		bell();

	return;
}
/******************************************************************************/


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
