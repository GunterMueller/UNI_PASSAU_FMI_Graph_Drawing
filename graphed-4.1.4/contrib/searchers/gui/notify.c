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
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/notice.h>
#include <xview/notify.h>


/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <sgraph/std.h>
#include <graphed/user_event_functions.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/window.h>
#include <search/control.h>


/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/

typedef void (*voidproc)();

/************************************************************************/
/* we expect that GraphEd exits, otherwise we catch the quit signal and */
/* set the 'graphed_killed'-flag on FALSE				*/
/************************************************************************/
static bool 	graphed_killed = TRUE;
extern Frame    base_frame;	/* from GraphEd */


/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/


/*******************************************************************************
*									       *
*			local functions of the same sort		       *
*									       *
********************************************************************************

names	    :	WNbusyBaseFrame
		WNbusyFrames

arguments   :	
   	type		name		description(-I/-O)
1.	bool		what		show timeout or not-I

return()    :	void

description :	show the timeout cursor on particular frame(s)

use	    :	while an time intensive algorithm proceeds

restrictions:	

bugs	    :	none reported

*******************************************************************************/

/*******************************************************************************

name        :	WNbusyBaseFrame

description :	show/remove timeout cursor of the GraphEd 'base_frame'

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
static void	WNbusyBaseFrame(int what)
{
	xv_set( base_frame, FRAME_BUSY, what, NULL );

	return;
}
	
/*******************************************************************************

name        :	WNbusyFrames

description :	set/remove timeout cursor on both frames, search and GraphEd

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
static void     WNbusyFrames(int what)
{
	xv_set( frame, FRAME_BUSY, what, NULL );
	xv_set( base_frame, FRAME_BUSY, what, NULL );

	return;
}
/******************************************************************************/



/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/




/*******************************************************************************
*									       *
*			global functions of the same sort		       *
*									       *
********************************************************************************

names	    :	WNnotifyClear
		WNnotifySearch
		WNnotifyMethod
		WNnotifyStrategy
		WNnotifyHelp
		WNnotifyAlgorithm
		WNnotifyAnimation
		WNnotifyManual
		WNnotifyBackstep
		WNnotifyRewind
		WNnotifyStop
		WNnotifyPlay
		WNnotifyForward
		WNnotifyStep
		WNnotifyReset
		WNnotifyQuit
		WNnotifyStatistics
		WNnotifyInterval

arguments   :	
   	type		name		description(-I/-O)
1.	Panel_item	item		event occured in ...-I
2.	int		value		value of the 'item'-I

return()    :	void
		exception: int WNnotifyQuit

description :	*all* the notifier procedures, most of the procedures only
		extract the 'value' and cascade to the control level,
		some functions manipulate directly the panel items

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/

/*******************************************************************************

name        :	WNnotifyClear

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyClear(Panel_item item, int value)
{

	COnotifyClear();

	return;
}


/*******************************************************************************

name        :	WNnotifySearch

description :	algorithm will need a little time, so display a timeout
		cursor on the screen

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifySearch(Panel_item item, int value)
{

/*************************************************************************/
/* show the timeout cursor, ==> symbolize that the search is in progress */
/*************************************************************************/
	WNbusyFrames( TRUE );

/******************************/
/* start the search algorithm */
/******************************/
	COnotifySearch();

/**********************************************/
/* the search is done, window is again active */
/**********************************************/
	WNbusyFrames( FALSE );

	return;
}

/*******************************************************************************

name        :	WNnotifyMethod

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyMethod(Panel_item item, int value)
{
	COnotifyMethod( (Method)value );

	return;
}

/*******************************************************************************

name        :	WNnotifyStrategy

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyStrategy(Panel_item item, int value)
{
	COnotifyStrategy( (Strategy)value );

	return;
}

/*******************************************************************************

name        :	WNnotifyHelp

description :

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyHelp(Panel_item item, int value)
{
	message("\n\
********************\n\
* use the HELP-key *\n\
********************\n" );
return;
}

/*******************************************************************************

name        :	WNnotifyAlgorithm

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyAlgorithm(Panel_item item, int value)
{
	COnotifyAlgorithm( (unsigned int)value );

	return;
}


/*******************************************************************************

name        :	WNnotifyAnimation

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyAnimation(Panel_item item, int value)
{
	COnotifyAnimation( (bool)value );

	return;
}

/*******************************************************************************

name        :	WNnotifyManual

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyManual(Panel_item item, int value)
{
	COnotifyManual( (bool)value );

	return;
}

/*******************************************************************************

name        :	WNnotifyBackstep

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyBackstep(Panel_item item, int value)
{
	COnotifyBackstep();

	return;
}


/*******************************************************************************

name        :	WNnotifyRewind

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyRewind(Panel_item item, int value)
{
	COnotifyRewind();

	return;
}


/*******************************************************************************

name        :	WNnotifyStop

description :	only cascading

use	    :	you have to registrate that the stop/play button are the same
		panel item but have *different* notify procedures

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyStop(Panel_item item, int value)
{
	COnotifyStop();

	return;
}

/*******************************************************************************

name        :	WNnotifyPlay

description :	only cascading

use	    :	you have to registrate that the stop/play button are the same
		panel item but have *different* notify procedures

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyPlay(Panel_item item, int value)
{
	COnotifyPlay();

	return;
}


/*******************************************************************************

name        :	WNnotifyForward

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyForward(Panel_item item, int value)
{
	COnotifyForward();

	return;
}


/*******************************************************************************

name        :	WNnotifyStep

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyStep(Panel_item item, int value)
{
	COnotifyStep();

	return;
}

/*******************************************************************************

name        :	WNnotifyReset

description :	only cascading

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyReset(Panel_item item, int value)
{
	COnotifyReset();

	return;
}

/*******************************************************************************

name        :	WNnotifyQuit

description :	notifying the Quit-button

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
int		WNnotifyQuit(Panel_item item, int value)
{
	WNnotifyMenuQuit(); /* just the same */

	return( XV_OK );
}

/*******************************************************************************

name        :	WNnotifyStatistics

description :	notifies the toggle button more.../...enough, creates resp.
		removes the panel items and changes the label of the pressed
		button

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyStatistics(Panel_item item, int value)
{
	if ( COtoggleStatistics() )
	{
	/*****************************************************************/
	/* display the MORE information about the specific graph and the */
	/* search done on it, change the label of the button		 */
	/*****************************************************************/
		xv_set( item,
			PANEL_LABEL_STRING, 	"...enough",
			XV_HELP_DATA,		"search:statisticsenough",
			NULL );

		WNcreateStatistics( (Panel)xv_get( item, XV_OWNER ) );
	}
	else
	{
	/*****************************************************************/
	/* remove the MORE information, change the label back to more... */
	/*****************************************************************/
		xv_set( item, 
			PANEL_LABEL_STRING, 	"more...   ", 
			XV_HELP_DATA,		"search:statisticsmore",
			NULL );

                WNdestroyStatistics( );
        }

	return;
}


/*******************************************************************************

name        :	WNnotifyInterval

description :	only cascading	

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		WNnotifyInterval(Panel_item item, int value)
{
	COnotifyInterval( value );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNnotifyManualEvent

arguments   :	
   	type		name		description(-I/-O)
1.	char*		info		see Xview resp. GraphEd-I
2.	Event*		event		Xevent-I

return()    :	User_event_functions_result

description :	notify procedure for double-clickinh events on a graph

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
User_event_functions_result	WNnotifyManualEvent(UEV_info info, Event *event)
{
/*********************************************************/
/* fetch the graph for proceeding the double-click event */
/*********************************************************/
	call_sgraph_event_proc( (voidproc)WNnotifyManualMove, info, event, NULL );

	return( UEV_CONSUMED );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNnotifyManualMove

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph_proc_info info		-I
2.	Sgraph_event_proc_info		-I	
3.	Event		event		Xevent-I

return()    :	char*

description :	checks for a double-click on a node or edge and proceeds
		the move of a seracher along an edge or sets a searcher
		a node

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void WNnotifyManualMove(Sgraph_proc_info info, Sgraph_event_proc_info uev_info, Event *event)
{


        if (    uev_info->type == SGRAPH_UEV_DOUBLE_CLICK
             && uev_info->state == SGRAPH_UEV_START )
        {
		/* only a double-click, no dragging */

                /* double-click on a node ? */
                if ( info->selected == SGRAPH_SELECTED_SNODE )
                {

                        /* set or move a searcher on/to this node */
                        COmanualSetOn( info->selection.snode );

                        /* no default action, like double-click on a node
                           opens the node edit window */
                        uev_info->do_default_action = FALSE;

                }
                else if ( info->selected == SGRAPH_SELECTED_SEDGE )
                {
			if ( COmethod() != METHOD_NODE_SEARCH )
	                        COmanualMove( info->selection.sedge );

                        /* no default action, like double-click on a node
                           opens the node edit window */
                        uev_info->do_default_action = FALSE;

                }


        }
        /* if any other event then do the GraphEd-default action */
        else uev_info->do_default_action = FALSE;

        return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNnotifyDestroyFrame

arguments   :	
   	type		name		description(-I/-O)
1.	Notify_client	client		quit event in window-I
2.	Destroy_status	status		which level-I

return()    :	void

description :	proceeds a controlled quit of the search window

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
Notify_value	WNnotifyDestroyFrame(Notify_client client, Destroy_status status)
{

/****************************************/
/* which level of the controlled quit ? */
/****************************************/
	switch( status )
	{
	case DESTROY_CLEANUP:
		/* set variable `frame` as zero, then we now if
		frame == 0 no window is on the screen */

		if ( graphed_killed == FALSE )
		{
			/* only the base frame */
			WNbusyBaseFrame( FALSE );
			if ( COplay() )
			{
				/* stop any animation */
				COnotifyStop();
			}
			if ( COrecorded() ) 
			{
				/* remove the recorded search, if any */
				COnotifyClear();
			}
			if ( COmanual() )
			{
				/* turn it off */
				COnotifyManual( FALSE );
			}

			/************************************/
			/* reset all values of report panel */
			/************************************/
			COreset();

			/*************************************/
			/* manual off, and display on window */	
			/*************************************/
			COsetManual( FALSE );
			WNsetManual();

			/**********************************/
			/* activate all panel items again */
			/**********************************/
			WNsetAnimationBusy( FALSE ); 
			WNsetMethodBusy( FALSE ); 
			WNsetAlgorithmsBusy( FALSE ); 

			/************************************************/
			/* set the default values for panel items again */
			/************************************************/
			COsetAnimation( TRUE );
			COsetMethod( METHOD_NODE_SEARCH );
			COsetStrategy( HEURISTIC );
			COsetAlgorithm( 0 );
		 
			COsetStrategy( APPROXIMATION );
			COsetAlgorithm( 0 );
		 
			COsetStrategy( OPTIMUM );
			COsetAlgorithm( 0 );

			/************************************/
			/* and display the default settings */
			/************************************/
			WNsetAnimation();
			WNsetMethod();
			WNsetStrategy();
		
			/******************************************/
			/* store that no animation is in progress */
			/******************************************/
			COsetStop();
			WNsetPlay();

			/*****************************/
			/* make graph editable again */
			/*****************************/
			unlock_user_interface();

			/************************************************/
			/* if the MORE statistics are shown, remve them */
			/************************************************/
			if ( COstatistics() )
			{
				WNdestroyStatistics(/*WNgetItem( WNITEM_STATISTICS ), 0 */);
				COsetStatistics( FALSE );
			}
		}
		else
		{
			/*****************************************************/
			/* graphed is killed, so all data structures will be */
			/* free'd by the OS itself 			     */
			/*****************************************************/
		}

		/***************************************/
		/* store that search window is removed */
		/**************************************/
		frame = (Frame)NULL;

		/*****************************************/
		/* and GraphEd is expected to be removed */
		/*****************************************/
		graphed_killed = TRUE;

		return( notify_next_destroy_func( client, status ) );
		break;
	case DESTROY_CHECKING:
		break;
	case DESTROY_SAVE_YOURSELF:
	case DESTROY_PROCESS_DEATH:
		break;
	default:
		break;
	}

	return( NOTIFY_DONE );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNnotifyMenuQuit

arguments   :	

return()    :	void

description :	recognizes that the exit event is ONLY for the search window

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNnotifyMenuQuit(void)
{

/*********************************************/
/* this is only an exit of the search window */
/*********************************************/
	graphed_killed = FALSE;

/************************************************************************/
/* remove the window, resetting all values and stopping, if neccessary, */
/* animation of a search 						*/
/************************************************************************/
	xv_destroy_safe( frame );

	return;
}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
