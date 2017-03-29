#ifdef UNIX5
#ifndef lint
static char version_id[] = "@(#) File : timer.c 93/03/12  03:35:35  Version 1.3 Copyright (C) 1992/93 Schweikardt Andreas";
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




	File	:	timer.c

	Date	:	93/03/12	(03:35:35)

	Version	:	1.3

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Interface (graphic):	Xview
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	gui

Modul   :	timer


********************************************************************************


Description of timer.c :	this file includes all necessary functions to manage
	the interrupts for the animation. The user *can* push the stop-
	button to stop the animation.



********************************************************************************


Functions of timer.c :



*******************************************************************************/


/******************************************************************************
*                                                                             *
*			standard includes				      *
*                                                                             *
*******************************************************************************/

#include <sys/time.h>


/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/notify.h>


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

extern Frame		base_frame;

/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/


/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	WNtimer

arguments   :	
   	type		name		description(-I/-O)
1.	*void()		function	function to call-I
2.	bool		fast		fast calls or interval calls-I

return()    :	void

description :	turns a timer on and off, 'fast' described the minimum possible
		interval (if true) otherwise the time is ffetched from the
		interval routine to compute the user defined interval

		if funtion is NULL the timer is turned off

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
static void	WNtimer(void (*function) (), int fast)
{
	unsigned int	seconds,
			microseconds;

	struct itimerval	timer;

/*********************************************************************/
/* check for a rewind or forward animation, this will be the fastest */
/* animation the timer will do					     */
/*********************************************************************/
	if ( function != NULL && fast )	
	{
		/* fast forward and rewind functions */
		microseconds = 10000;
		seconds = 0;
	}
	else if ( function != NULL ) 
	{
		/* fetch the user defined interval between two calls */
		COwait( &seconds, &microseconds );
	}

/*****************************/
/* intialize the timer value */
/*****************************/
        timer.it_value.tv_usec = microseconds;
        timer.it_interval.tv_usec = microseconds;
        timer.it_value.tv_sec = seconds;
        timer.it_interval.tv_sec = seconds;

/*******************/
/* start the timer */
/******************/
	notify_set_itimer_func( (Notify_client)base_frame, 
		(Notify_func)function, /* if NULL then stop */
		ITIMER_REAL, &timer, /* REAL seems to be REAListic */
		NULL );

	return;
}



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	WNshowBackStep

arguments   :	none	

return()    :	void

description :	decides depending on the current position to show a recorded
		step (backwards) or to stop the timer

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
static void	WNshowBackstep(void)
{
	if ( COrecStartOfTape() )
	{
	/***************************************************************/
	/* a single step backwards, but the recorded tape is a already */
	/* at the beginning, so stop the timer and return 	       */
	/***************************************************************/
		COnotifyStop();
	}
	else
	{
	/***************************/
	/* show one step backwards */
	/***************************/
		COnotifyRewindStep();
	}
		
	return;
}



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	WNshowStep

arguments   :	none

return()    :	void

description :	does the same as WNshowBackstep (see function above)

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
static void	WNshowStep(void)
{
	if ( COrecEndOfTape() )
	{
	/*************************************************************/
	/* a single step forward, but the recorded tape is a already */
	/* at the end of the tape, so stop the timer and return      */
	/*************************************************************/
		COnotifyStop();
	}
	else
	{
	/*************************/
	/* show one step forward */
	/*************************/
		COnotifyForwardStep();
	}

	return;
}
	


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

name        :	WNfastTimer

arguments   :	
   	type		name		description(-I/-O)
1.	*void()		func		forward/rewind-I

return()    :	void

description :	start the rewind/forward functions as fast as the machine 
		will do

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    WNfastTimer(void (*func) ())
{
        WNtimer( func, TRUE );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNplayTimer

arguments   :	

return()    :	void

description :	start the normal animation of the search 

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    WNplayTimer(void)
{
        WNtimer( WNshowStep, FALSE );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNresetTimer

arguments   :	

return()    :	void

description :	stop the timer immediately

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    WNresetTimer(void)
{
	notify_set_itimer_func( base_frame, NOTIFY_FUNC_NULL, ITIMER_REAL, NULL, NULL );

	return;
}
/******************************************************************************/
 


/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNshowAnimation

arguments   :	

return()    :	void

description :	= WNplayTimer ( cascading function for a better division of 
		the modules )

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    WNshowAnimation(void)
{
        WNplayTimer();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNstopAnimation

arguments   :	

return()    :	void

description :	stops the timer and do animation steps until the graph is again
		in a normal form.
		normal form: example if method is node search a two nodes
			are containing searchers then ALL edges between these
			two nodes must be free'd.
			So we do the (last few steps) until all these edges
			are free'd.

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    WNstopAnimation(void)
{
        WNresetTimer();
	COnotifyLastFewSteps();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNforwardAnimation

arguments   :	

return()    :	void

description :	start the fast forwrding of the animation

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    WNforwardAnimation(void)
{
        WNfastTimer( WNshowStep );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNrewindAnimation

arguments   :	

return()    :	void

description :	starts the fast rewinding of the animation

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    WNrewindAnimation(void)
{
        WNfastTimer( WNshowBackstep );

	return;
}
/******************************************************************************/
 


/******************************************************************************
*		       [EOF] end of file timer.c 
******************************************************************************/
