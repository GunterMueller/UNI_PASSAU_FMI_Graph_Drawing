#ifdef UNIX5
#	ifndef lint
static char version_id[] = "@(#) File : window.c 93/03/12  03:35:13  Version 1.3 Copyright (C) 1992/93 Schweikardt Andreas";
#	endif
#endif 

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




	File	:	window.c

	Date	:	93/03/12	(03:35:13)

	Version	:	1.3

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Interface (graphic):	Xview (X11)
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	GUI

Modul   :	window


********************************************************************************


Description of window.c :





********************************************************************************


Functions of window.c :



*******************************************************************************/


/******************************************************************************
*                                                                             *
*			standard includes				      *
*                                                                             *
*******************************************************************************/

#include <stdio.h>


/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>
#include <xview/notice.h>
#include <xview/svrimage.h>
#include <xview/icon.h>


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
#include <search/error.h>

extern void  COresetAlgorithms(void);
extern Frame	base_frame;	/* from GraphEd */


/******************************************************************************
*                                                                             *
*			local defines 		 			      *
*                                                                             *
*******************************************************************************/

/*****************************/
/* constants for window size */
/*****************************/

#define WNFRAME_SIZE_X		450
#define WNFRAME_SIZE_Y		650
#define WNFRAME_SEPARATOR	360

/**********************************************************************/
/* the gaps between frame border and the beginning of the panel_items */
/**********************************************************************/

#define WNCOL_GAP		15	
#define WNROW_GAP		 5


/******************************************************************************
*                                                                             *
*			global variables				      *
*                                                                             *
*******************************************************************************/


/******************************************************************/
/* the frame of the search window, need sometimes access to frame */
/* e.g quitting the window 				  	  */
/******************************************************************/

Frame	frame = (Frame)NULL;


/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/

/****************************************************/
/* including the pictures for the recording-buttons */
/****************************************************/


static unsigned short   	step_bits[] = {
#	include <search/fwd.icon>	/* step forward, symbol > */
};
static unsigned short   	backstep_bits[] = {
#	include <search/rwd.icon>	/* step backwards, symbol < */
};
static unsigned short   	rewind_bits[] = {
#	include <search/frwd.icon>	/* rewind, << */
};
static unsigned short   	play_bits[] = {
#	include <search/play.icon>	/* play, a bigger > */
};
static unsigned short   	stop_bits[] = {
#	include <search/stop.icon>	/* stop, a filled circle */
};
static unsigned short   	forward_bits[] = {
#	include <search/ffwd.icon>	/* forward, >> */
};
static unsigned short	reset_bits[] = {	/* reset, no symbol */
#	include <search/reset.icon>
};


/************************************/
/* include the picture for the icon */
/************************************/

static unsigned short	icon_bits[] = {
#	include <search/sog.icon>	/* iconfy it, and you will see this
					   beautiful art :-) */
};


/**************************************************************/
/* save both images for the button, we use toggling play/stop */
/**************************************************************/

static Server_image	play_image,
			stop_image;


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

name        :	WNcreateControl

arguments   :	
   	type		name		description(-I/-O)
1.	Panel		panel		panel to fill with items-I

return()    :	void

description :	creates the buttons, sliders, choices of the upper part
		of the search window, most of the panel items are  saved
		for a further manipulation of the items, e.g. make them
		inactive or set the right value.

use	    :	creating window

restrictions:	call only once, cannot create window twice

bugs	    :	none reported

*******************************************************************************/
static void	WNcreateControl(Panel panel)
{
	Panel_item	item;
	Server_image	image;

/****************************************/
/* set the default gaps to panel border */
/****************************************/
	xv_set( panel,
		WIN_COLUMN_GAP,		WNCOL_GAP,
		WIN_ROW_GAP,		WNROW_GAP,
		NULL );

/*************************************************************************/
/* the start button, pressing this button starts the algorithm displayed */
/*************************************************************************/
	item = xv_create( panel,	PANEL_BUTTON,
		 PANEL_LABEL_STRING,    "Search",
		 PANEL_NOTIFY_PROC,     WNnotifySearch,
		 XV_X,                  xv_col( panel, 1 ),
		 XV_Y,                  xv_row( panel, 1 ),
		 XV_HELP_DATA,          "search:search",
		 NULL );
	/* store the value for inactiviting this button */
	WNsetItem( item, WNITEM_SEARCH );

/**************************************************************************/
/* the clear button, deletes any recorded search and refreshes the screen */
/**************************************************************************/
	xv_create( panel,		PANEL_BUTTON,
 		PANEL_LABEL_STRING,     "Clear",
		PANEL_NOTIFY_PROC,      WNnotifyClear,
		XV_X,                   xv_col( panel, 5 ),
		XV_Y,                   xv_row( panel, 1 ),
		XV_HELP_DATA,           "search:clear",
		NULL );

/**********************************************************************/
/* the help button, press it and you'll get the best introduction ... */
/**********************************************************************/
	xv_create( panel,               PANEL_BUTTON,
		PANEL_LABEL_STRING,     "Help",
		PANEL_NOTIFY_PROC,      WNnotifyHelp,
		XV_X,                   xv_col( panel, 16 ),
		XV_Y,                   xv_row( panel, 1 ),
		XV_HELP_DATA,           "search:help",
		NULL);

/*************************************************************************/
/* this panel item handles three different menus, for every strategy the */
/* appropriate menu is created, also the help will change                */
/*************************************************************************/
  	item = xv_create( panel,        PANEL_CHOICE_STACK,
		PANEL_LABEL_STRING,     "algorithm:",
		PANEL_CHOICE_STRINGS,	" ",	
			/* the function WNmenuAlgorithms will show it */
					NULL,
		PANEL_NOTIFY_PROC,	WNnotifyAlgorithm,		  
		PANEL_VALUE,		0,
		XV_X,			xv_col( panel, 1 ),
		XV_Y,			xv_row( panel, 3 ),
		XV_HELP_DATA,		"search:optimum",
		NULL );
	/* store for inactiviting and set the menu */
	WNsetItem( item, WNITEM_ALGORITHM );
	WNmenuAlgorithms(); /* show the right menu */

/*********************************************************************/
/* the choice of the method, as you know, the literature knows three */
/* different methods for searching (in our meaning) a graph          */
/*********************************************************************/
        item = xv_create( panel,        PANEL_CHECK_BOX,
		PANEL_LABEL_STRING,     "method:",
		PANEL_NOTIFY_PROC,      WNnotifyMethod,
		XV_X,                   xv_col( panel, 1 ),
		XV_Y,                   xv_row( panel, 5 ),
		PANEL_LAYOUT,           PANEL_VERTICAL,
		PANEL_CHOOSE_ONE,       TRUE,
		PANEL_CHOICE_STRINGS,   "node search",
					"edge search",
					"mixed search",
					NULL,
		PANEL_VALUE,            COmethod(),
		XV_HELP_DATA,           "search:method",
		NULL );
	/* might also be inactivated */
	WNsetItem( item, WNITEM_METHOD );

/************************************************************************/
/* we have so many different algorithms, so I decided to divide them in */
/* three groups:							*/
/* - optimum: all algorithm do comput the exact(!) result 		*/
/* - heuristic: they are quit good, but we cannot say how good		*/
/* - approximation: they are quite bad, but we can say how bad ;-)	*/
/************************************************************************/
        item = xv_create( panel,        PANEL_CHECK_BOX,
		PANEL_LABEL_STRING,     "strategy:",
		PANEL_NOTIFY_PROC,      WNnotifyStrategy,
		XV_X,                   xv_col( panel, 7 ),
		XV_Y,                   xv_row( panel, 5 ),
		PANEL_LAYOUT,           PANEL_VERTICAL,
		PANEL_CHOOSE_ONE,       TRUE,
		PANEL_CHOICE_STRINGS,   "optimum",
					"heuristic",  
					"approximation",
					NULL,
		PANEL_VALUE,            COstrategy(),
		XV_HELP_DATA,           "search:strategy",
		NULL );
	/* inactiviting of the strategy is sometimes necessary */
	WNsetItem( item, WNITEM_STRATEGY );

/******************************************************************/
/* will see only the number of searchers or also their movement ? */
/******************************************************************/
	item = xv_create( panel,        PANEL_CHECK_BOX,
		PANEL_LABEL_STRING,     "animation ",
		PANEL_NOTIFY_PROC,      WNnotifyAnimation,
		XV_X,                   xv_col( panel, 13 ),
		XV_Y,                   xv_row( panel, 6 ),
		PANEL_VALUE,            COanimate(),
		XV_HELP_DATA,           "search:animation",
		NULL );
	/* inacti... */
	WNsetItem( item, WNITEM_ANIMATION );

/**************************************************************************/
/* are the algorithms boring and have a bad result, and you think you can */
/* do a better job ... do it						  */
/**************************************************************************/
	item = xv_create( panel,        PANEL_CHECK_BOX,
		PANEL_LABEL_STRING,     "manual mode",
		PANEL_NOTIFY_PROC,      WNnotifyManual,
		XV_X,                   xv_col( panel, 13 ),
		XV_Y,                   xv_row( panel, 7 ),
		PANEL_VALUE,            COmanual (),
		XV_HELP_DATA,           "search:manual",
		NULL );
	/* can't remember why I stored this :-( */
	WNsetItem( item, WNITEM_MANUAL );

/****************************************************************************/
/* the following buttons have no text, but an image, so we have a different */
/* layout, see datails in the code 					    */
/****************************************************************************/
#define	RECIMAGE	10
#define RECTEXT		11
#define RECROW_GAP	 4
#define	RECCOL_GAP	xv_col( panel,1)/2	

/************************************************************************/
/* it is always the same procedure, creating the image, then button and */
/* below the button the text 						*/
/* only the first button is described here				*/
/************************************************************************/


/********************************************/
/* create the internal image for the button */
/********************************************/
        image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
                SERVER_IMAGE_BITS,      backstep_bits,
                NULL );
/**********************************/
/* create the button with the "<" */
/**********************************/
        xv_create( panel,               PANEL_BUTTON,
                PANEL_LABEL_IMAGE,      image,
                PANEL_NOTIFY_PROC,      WNnotifyBackstep,
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, RECIMAGE ),
		XV_HELP_DATA,		"search:backstep",
                NULL );

/******************************************/
/* create now the text below the "<" sign */
/******************************************/
        xv_create( panel,               PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "step",
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, RECTEXT )+RECROW_GAP,
		XV_HELP_DATA,		"search:backstep",
                NULL );

/*************************/
/* see above description */
/*************************/
       	image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
                SERVER_IMAGE_BITS, rewind_bits,
                NULL );
        item = xv_create( panel,               PANEL_BUTTON,
                PANEL_LABEL_IMAGE,      image,
                PANEL_NOTIFY_PROC,      WNnotifyRewind,
                XV_X,                   xv_col( panel, 3 ) + RECCOL_GAP,
                XV_Y,                   xv_row( panel, RECIMAGE ),
		XV_HELP_DATA,		"search:rewind",
                NULL );
	/* store for showing the rewinding process */
	WNsetItem( item, WNITEM_REWIND );

        xv_create( panel,               PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "rewind",
                XV_X,                   xv_col( panel, 3 )-10 + RECCOL_GAP,
                XV_Y,                   xv_row( panel, RECTEXT ) + RECROW_GAP,
		XV_HELP_DATA,		"search:rewind",
                NULL );


	/* save both images */
	play_image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
                SERVER_IMAGE_BITS, 	play_bits,
                NULL );
        stop_image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
                SERVER_IMAGE_BITS, stop_bits,
                NULL );
/*************************/
/* see above description */
/*************************/
        item = xv_create( panel,        PANEL_BUTTON,
                PANEL_LABEL_IMAGE,      play_image,
                PANEL_NOTIFY_PROC,      WNnotifyPlay,
                XV_X,                   xv_col( panel, 5 ) + RECCOL_GAP*2,
                XV_Y,                   xv_row( panel, RECIMAGE ),
		XV_HELP_DATA,		"search:play",
                NULL );
        WNsetItem( item, WNITEM_PLAY_STOP );

        item = xv_create( panel,        PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "play",
                XV_X,                   xv_col( panel, 5 ) + RECCOL_GAP*2,
                XV_Y,                   xv_row( panel, RECTEXT ) + RECROW_GAP,
		XV_HELP_DATA,		"search:play",
                NULL );
	/* the text has also to be toggled */
        WNsetItem( item, WNITEM_PLAY_STOP_TEXT );


/*************************/
/* see above description */
/*************************/
      	image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
                SERVER_IMAGE_BITS, forward_bits,
                NULL );
        item = xv_create( panel,               PANEL_BUTTON,
                PANEL_LABEL_IMAGE,      image,
                PANEL_NOTIFY_PROC,      WNnotifyForward,
                XV_X,                   xv_col( panel, 7 ) + RECCOL_GAP*3,
                XV_Y,                   xv_row( panel, RECIMAGE ),
		XV_HELP_DATA,		"search:forward",	
                NULL );
	WNsetItem( item, WNITEM_FORWARD );

        xv_create( panel,               PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "forward",
                XV_X,                   xv_col( panel, 7 )-10 + RECCOL_GAP*3,
                XV_Y,                   xv_row( panel, RECTEXT ) + RECROW_GAP,
		XV_HELP_DATA,		"search:forward",	
                NULL );

/*************************/
/* see above description */
/*************************/
        image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
                SERVER_IMAGE_BITS, step_bits,
                NULL );
        xv_create( panel,               PANEL_BUTTON,
                PANEL_LABEL_IMAGE,      image,
                PANEL_NOTIFY_PROC,      WNnotifyStep,
                XV_X,                   xv_col( panel, 9 ) + RECCOL_GAP*4,
                XV_Y,                   xv_row( panel, RECIMAGE ),
		XV_HELP_DATA,		"search:stepping",
                NULL );

        xv_create( panel,               PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "step",
                XV_X,                   xv_col( panel, 9 ) + RECCOL_GAP*4,
                XV_Y,                   xv_row( panel, RECTEXT ) + RECROW_GAP,
		XV_HELP_DATA,		"search:stepping",
                NULL );


/*************************/
/* see above description */
/*************************/
        image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
		XV_HEIGHT,		16,
		XV_WIDTH,		32,
                SERVER_IMAGE_BITS, reset_bits,
                NULL );
        xv_create( panel,               PANEL_BUTTON,
                PANEL_LABEL_IMAGE,      image,
                PANEL_NOTIFY_PROC,      WNnotifyReset,
                XV_X,                   xv_col( panel, 14 ),
                XV_Y,                   xv_row( panel, RECIMAGE ),
		XV_HELP_DATA,		"search:reset",
                NULL );
/***************************************/
/* this were all the recording buttons */	
/***************************************/
	
/*******************************************************************/
/* for different step interval, this slider is a useful instrument */
/*******************************************************************/
	xv_create( panel,		PANEL_SLIDER,
                PANEL_LABEL_STRING,     "interval :",
                PANEL_NOTIFY_PROC,      WNnotifyInterval,
                PANEL_MIN_VALUE,        INTERVAL_MIN,
                PANEL_MAX_VALUE,        INTERVAL_MAX,
		PANEL_SLIDER_WIDTH,	270,
		PANEL_TICKS,		11,
		PANEL_SHOW_RANGE,	FALSE,
		PANEL_SHOW_VALUE,	FALSE,
                PANEL_VALUE,            COinterval(),
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 13 ),
                XV_HELP_DATA,           "search:interval",
                NULL );


	return; /* now you see a well-designed window on the screen */
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	WNcreateReport

arguments   :	
   	type		name		description(-I/-O)
1.	Panel		panel		panel to fill with items-I

return()    :	void

description :	creates the lower panel of the frame for the report
		of the search, the output, information about the
		search

use	    :	creating window

restrictions:	same as WNcreateControl

bugs	    :	none reported

*******************************************************************************/
static void	WNcreateReport(Panel panel)
{
	Panel_item	item;

/****************************************/
/* set the default gaps to panel border */
/****************************************/
	xv_set( panel,
		WIN_COLUMN_GAP,		WNCOL_GAP,
		WIN_ROW_GAP,		WNROW_GAP,
		NULL );

/*********************/
/* name of the panel */
/*********************/
        xv_create( panel,               PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "Search Report :",
                PANEL_LABEL_BOLD,       TRUE,
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 1 ),
                XV_HELP_DATA,           "search:report",
                NULL );

/*********************************************/
/* show the name of the graph - the filename */
/*********************************************/
	item = xv_create( panel,	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
		XV_X,			xv_col( panel, 8 ),
		XV_Y,			xv_row( panel, 1 ),
		XV_HELP_DATA,		"search:filename",
		NULL );
	WNsetItem( item, WNITEM_FILENAME );
	/* show the name */
	WNshowFilename();

/******************************************************/
/* some useful information about the search algorithm */
/******************************************************/
	item = xv_create( panel,	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
		XV_X,			xv_col( panel, 1 ),
		XV_Y,			xv_row( panel, 2 ),
		XV_HELP_DATA,		"search:info",
		NULL );
	WNsetItem( item, WNITEM_INFO );

/**************************************************************************/
/* the next two items shows the maximum number of searchers used, this is */
/* main information about the search					  */ 
/**************************************************************************/
	xv_create( panel,               PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "maximum  :",
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 3 ),    
                XV_HELP_DATA,           "search:maximum",
                NULL );
        item = xv_create( panel,        PANEL_MESSAGE,
                PANEL_LABEL_STRING,     " ",
			/* very important information ==> bold */
                PANEL_LABEL_BOLD,       TRUE,
                XV_X,                   xv_col( panel, 5 ),
                XV_Y,                   xv_row( panel, 3 ),    
                XV_HELP_DATA,           "search:maximum",
                NULL );
        WNsetItem( item, WNITEM_MAX_SEARCHERS ); /* saving the panel */

/************************************************************************/
/* the next two items shows the number of the current searchers used in */
/* the animation of the search algorithm 				*/
/************************************************************************/
        xv_create( panel,               PANEL_MESSAGE,
                PANEL_LABEL_STRING,     "searchers :",
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 4 ),    
                XV_HELP_DATA,           "search:current",
                NULL );
        item = xv_create( panel,        PANEL_MESSAGE,
                PANEL_LABEL_STRING,     " ",
                PANEL_LABEL_BOLD,       TRUE,
                XV_X,                   xv_col( panel, 5 ),
                XV_Y,                   xv_row( panel, 4 ),     
                XV_HELP_DATA,           "search:current",
                NULL );
	/* store the item, we have to manipulate the value during the search */
        WNsetItem( item, WNITEM_SEARCHERS );

/**************************************************************************/
/* quit the window, both control *and* report are removed from the screen */
/**************************************************************************/
        xv_create( panel,               PANEL_BUTTON,
                PANEL_LABEL_STRING,     "Quit",
                PANEL_NOTIFY_PROC,      WNnotifyQuit,
                XV_X,                   xv_col( panel, 16 ),       
                XV_Y,                   xv_row( panel, 10 )-5,
                XV_HELP_DATA,           "search:quit",
                NULL );

/***************************************************************************/
/* if the user wants more than only maximum and current searchers, here he */
/* got it, *more* information about the search is displayed		   */
/***************************************************************************/
        item = xv_create( panel, 	PANEL_BUTTON,
                PANEL_LABEL_STRING,     "more...   ",
                PANEL_NOTIFY_PROC,      WNnotifyStatistics,
                XV_X,                   xv_col( panel, 8 ),
                XV_Y,                   xv_row( panel, 3 ),
                XV_HELP_DATA,           "search:statisticsmore",
                NULL );
	/* store this item, toggles to '... enough', see below */
        WNsetItem( item, WNITEM_STATISTICS );

/****************************************************/
/* display all the information required by the user */
/****************************************************/
	WNreport();

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
*			global function					       *
*									       *
********************************************************************************

name        :	WNcreateStatistics

arguments   :	
   	type		name		description(-I/-O)
1.	Panel		panel		create the items in this panel-I

return()    :	void

description :	creates only panel items in the Report panel, lower panel
		in the window

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNcreateStatistics(Panel panel)
{

	Panel_item	item;

/****************************************************************************/
/* while developing the program, I used different constants, it seems to be */
/* the best using this one						    */
/****************************************************************************/
#define WNREPORT	13

/************************************************************************/
/* the following panel items are always a group of two, the name of the */
/* diplayed information and the information itself			*/
/************************************************************************/

/**********************************************************************/
/* display the number of nodes that have contained a searcher and the */
/* searcher has been removed 					      */
/**********************************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Nodes free'd:",
                XV_X,                   xv_col( panel, 8 ),
                XV_Y,                   xv_row( panel, 5 ),    
                XV_HELP_DATA,           "search:nodesunset",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_NODES_UNSET_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, WNREPORT ),
                XV_Y,                   xv_row( panel, 5 ),    
                XV_HELP_DATA,           "search:nodesunset",
                NULL );
	WNsetItem( item, WNITEM_NODES_UNSET );

/*****************************************************************/
/* display the number of nodes never having contained a searcher */
/*****************************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Nodes not free'd:",
                XV_X,                   xv_col( panel, 8 ),
                XV_Y,                   xv_row( panel, 6 ),    
                XV_HELP_DATA,           "search:nodesnotset",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_NODES_NOT_SET_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, WNREPORT ),
                XV_Y,                   xv_row( panel, 6 ),    
                XV_HELP_DATA,           "search:nodesnotset",
                NULL );
	
	WNsetItem( item, WNITEM_NODES_NOT_SET );

/***********************************************************************/
/* display the number of nodes that contains a searcher at this moment */
/***********************************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Nodes searched:",
                XV_X,                   xv_col( panel, 8 ),
                XV_Y,                   xv_row( panel, 7 ),    
                XV_HELP_DATA,           "search:nodesset",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_NODES_SET_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, WNREPORT ),
                XV_Y,                   xv_row( panel, 7 ),    
                XV_HELP_DATA,           "search:nodesset",
                NULL );
	
	WNsetItem( item, WNITEM_NODES_SET );

/**********************************************/
/* display the number of edges decontaminated */
/**********************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Edges cleared:",
                XV_X,                   xv_col( panel, 8 ),
                XV_Y,                   xv_row( panel, 8 ),    
                XV_HELP_DATA,           "search:edgescleared",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_EDGES_CLEAR_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, WNREPORT ),
                XV_Y,                   xv_row( panel, 8 ),    
                XV_HELP_DATA,           "search:edgescleared",
                NULL );
	WNsetItem( item, WNITEM_EDGES_CLEAR );

/**************************************************/
/* display the number of edges still contaminated */
/**************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Edges not cleared:",
                XV_X,                   xv_col( panel, 8 ),
                XV_Y,                   xv_row( panel, 9 ),    
                XV_HELP_DATA,           "search:edgesnotcleared",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_EDGES_NOT_CLEAR_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, WNREPORT ),
                XV_Y,                   xv_row( panel, 9 ),    
                XV_HELP_DATA,           "search:edgesnotcleared",
                NULL );
	WNsetItem( item, WNITEM_EDGES_NOT_CLEAR );

/**************************************************/
/* display the number of edges of the whole graph */
/**************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Edges:",
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 5 ),    
                XV_HELP_DATA,           "search:edges",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_EDGES_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, 5 ),
                XV_Y,                   xv_row( panel, 5 ),    
                XV_HELP_DATA,           "search:edges",
                NULL );
	WNsetItem( item, WNITEM_EDGES );

/**************************************************/
/* display the number of nodes of the whole graph */
/**************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Nodes:",
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 6 ),    
                XV_HELP_DATA,           "search:nodes",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_NODES_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, 5 ),
                XV_Y,                   xv_row( panel, 6 ),    
                XV_HELP_DATA,           "search:nodes",
                NULL );
	WNsetItem( item, WNITEM_NODES );

/***********************************************************/
/* display the number of steps done in the animated search */
/***********************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Steps:",
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 8 ),    
                XV_HELP_DATA,           "search:steps",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_STEPS_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, 5 ),
                XV_Y,                   xv_row( panel, 8 ),    
                XV_HELP_DATA,           "search:steps",
                NULL );
	WNsetItem( item, WNITEM_STEPS );

/*****************************************************************/
/* display the number of maximum steps to search the whole graph */
/*****************************************************************/
	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"max. steps:",
                XV_X,                   xv_col( panel, 1 ),
                XV_Y,                   xv_row( panel, 9 ),    
                XV_HELP_DATA,           "search:maxsteps",
                NULL );
	/* store it, for remove use only */
	WNsetItem( item, WNITEM_MAX_STEPS_TEXT );

	item = xv_create( panel, 	PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"",
                XV_X,                   xv_col( panel, 5 ),
                XV_Y,                   xv_row( panel, 9 ),    
                XV_HELP_DATA,           "search:maxsteps",
                NULL );
	WNsetItem( item, WNITEM_MAX_STEPS );


/******************************************************/
/* ... and retrieve all the information on the screen */
/******************************************************/
	WNreport();
	
	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNdestroyStatistics

arguments   :	

return()    :	void

description :	the opposite function to WNcreateStatistics, removes
		only(!) the panel items

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNdestroyStatistics(void)
{
/* no comment */
	xv_destroy( WNgetItem( WNITEM_STEPS ) );
	xv_destroy( WNgetItem( WNITEM_STEPS_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_MAX_STEPS ) );
	xv_destroy( WNgetItem( WNITEM_MAX_STEPS_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_NODES ) );
	xv_destroy( WNgetItem( WNITEM_NODES_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_NODES_SET ) );
	xv_destroy( WNgetItem( WNITEM_NODES_SET_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_NODES_NOT_SET ) );
	xv_destroy( WNgetItem( WNITEM_NODES_NOT_SET_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_NODES_UNSET ) );
	xv_destroy( WNgetItem( WNITEM_NODES_UNSET_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_EDGES ) );
	xv_destroy( WNgetItem( WNITEM_EDGES_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_EDGES_CLEAR ) );
	xv_destroy( WNgetItem( WNITEM_EDGES_CLEAR_TEXT ) );
	xv_destroy( WNgetItem( WNITEM_EDGES_NOT_CLEAR ) );
	xv_destroy( WNgetItem( WNITEM_EDGES_NOT_CLEAR_TEXT ) );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNmenuAlgorithms

arguments   :	

return()    :	void

description :	retrieves all the algorithm names of the current startegy

use	    :	whenever the strategy has changed

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNmenuAlgorithms(void)
{

	char	*name;
	int	index = 0;

/***********************/
/* remove the old menu */
/***********************/
	xv_set( WNgetItem( WNITEM_ALGORITHM ),
		PANEL_CHOICE_STRINGS,	
				" ",
				NULL,
		NULL );

/*************************************************/
/* reset the list with all algorithm information */
/*************************************************/
	COresetAlgorithms();


/********************************************************/
/* fetch the names of the algorithms in the right order */
/********************************************************/
	while ( ( name = COnextAlgorithm() ) != (char *)NULL )
	{
		/* set it at right place */
		xv_set( WNgetItem( WNITEM_ALGORITHM ),
			PANEL_CHOICE_STRING,	index,	name,
			NULL );
		index++;
	}

/**************************************************/
/* replace the old help-text by the new help-text */
/**************************************************/
	switch( COstrategy() )
	{
	case OPTIMUM:
		xv_set( WNgetItem( WNITEM_ALGORITHM ),
			PANEL_VALUE,		COalgorithm(),
			XV_HELP_DATA, 		"search:optimum",
			NULL );
		break;
	case HEURISTIC:
		xv_set( WNgetItem( WNITEM_ALGORITHM ),
			PANEL_VALUE,		COalgorithm(),
			XV_HELP_DATA,		"search:heuristic",
			NULL );
		break;
	case APPROXIMATION:
		xv_set( WNgetItem( WNITEM_ALGORITHM ),
			PANEL_VALUE,		COalgorithm(),
			XV_HELP_DATA, 		"search:approximation",
			NULL );
		break;
	default:
		SetErrorAndReturnVoid( IERROR_SWITCH_NO_CASE );
		break;
	}

	return;

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global functions of the same sort		       *
*									       *
********************************************************************************

names	    :	WNsetAnimation
		WNsetStrategy
		WNsetManual
		WNsetMethod
		WNsetPlay
		WNsetStop

arguments   :	

return()    :	void

description :	changes of flags must also displayed by the buttons, if 
		anything changed internal, this must be displayed on the
		screen 

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/

/*******************************************************************************

name        :	WNsetAnimation

description :	sets or removes the check in the animation item

use	    :	if animation flag has changed

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetAnimation(void)
{
	xv_set( WNgetItem( WNITEM_ANIMATION ),
		PANEL_VALUE, 	COanimation(),
		NULL );

	return;
}

/*******************************************************************************

name        :	WNsetStrategy

description :	if strategy has changed, set also the appropiate menu of
		algorithms

use	    :	if strategy has changed

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetStrategy(void)
{
	xv_set( WNgetItem( WNITEM_STRATEGY ),
		PANEL_VALUE,	COstrategy(),
		NULL );
	/* change also the algorithms */
	WNmenuAlgorithms();

	return;
}

/*******************************************************************************

name        :	WNsetManual

description :	sets or removes the check (see animation)

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetManual(void)
{
	xv_set( WNgetItem( WNITEM_MANUAL ),
		PANEL_VALUE,	COmanual(),
		NULL );

	return;
}

/*******************************************************************************

name        :	WNsetPlay

description :	toggle the button *and* text to play, and change th
		notify procedure to play again

use	    :	if [stop]-button was pressed

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetPlay(void)
{
	xv_set( WNgetItem( WNITEM_PLAY_STOP ),
                PANEL_LABEL_IMAGE,      play_image,
		PANEL_NOTIFY_PROC,	WNnotifyPlay,
		NULL );
	/* not only the image also the text ! */
	xv_set( WNgetItem( WNITEM_PLAY_STOP_TEXT ),
		PANEL_LABEL_STRING,	"play",
		NULL );
	
	return;
}

/*******************************************************************************

name        :	WNsetStop

description :	reverse to WNsetPlay

use	    : 	if [play]-button was pressed	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetStop(void)
{
	xv_set( WNgetItem( WNITEM_PLAY_STOP ),
                PANEL_LABEL_IMAGE,      stop_image,
		PANEL_NOTIFY_PROC,	WNnotifyStop,
		NULL );
	xv_set( WNgetItem( WNITEM_PLAY_STOP_TEXT ),
		PANEL_LABEL_STRING,	"stop",
		NULL );

	return;
}

/*******************************************************************************

name        :	WNsetMethod

description :   show the choosen method on the screen	

use	    :	internal change of the method

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetMethod(void)
{
	xv_set( WNgetItem( WNITEM_METHOD ),
		PANEL_VALUE,	COmethod(),
		NULL );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global functions of the same sort		       *
*									       *
********************************************************************************

names	    :	WNsetAnimationBusy
		WNsetAlgorithmsBusy
		WNsetMethodBusy
		WNsetRewindBusy
		WNsetForwardBusy
		WNsetSearchBusy

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		inactive on or off-I

return()    :	void

description :	sets a specific button(s) inactive or active again 

use	    :	always if a button could or shouldn't be pressed

restrictions:	

bugs	    :	none reported

*******************************************************************************/

/*******************************************************************************

name        :	WNsetAnimationBusy	 

description :	sets the animation toggle inactive/active, while animation
		or manual mode it makes no sense to allow turning the
		animation off

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetAnimationBusy(int data)
{
	xv_set( WNgetItem( WNITEM_ANIMATION ),
		PANEL_INACTIVE,		data,
		NULL );
	return;
}

/*******************************************************************************

name        :	WNsetAlgorithmsBusy

description :	sets both strategy AND algorithms active/inactive, these
		depend on each other

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetAlgorithmsBusy(int data)
{
	xv_set( WNgetItem( WNITEM_STRATEGY ),
		PANEL_INACTIVE,	data,
		NULL );
	xv_set( WNgetItem( WNITEM_ALGORITHM ),	
		PANEL_INACTIVE,	data,
		NULL );

	return;
}


/*******************************************************************************

name        :	WNsetMethodBusy

description :	sets the choice of the method inactive/active, 

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetMethodBusy(int data)
{
	xv_set( WNgetItem( WNITEM_METHOD ),
		PANEL_INACTIVE,	data,
		NULL );

	return;
}


/*******************************************************************************

name        :	WNsetRewindBusy

description :	sets the rewind-button active/inactive AND the opposite
		forward-button as active

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetRewindBusy(int data)
{
	xv_set( WNgetItem( WNITEM_REWIND ),
		PANEL_INACTIVE, 	data,
		NULL );
	xv_set( WNgetItem( WNITEM_FORWARD ),
		PANEL_INACTIVE, 	FALSE,
		NULL );

	return;
}

/*******************************************************************************

name        :	WNsetForwardBusy

description :	sets the forward-button active/inactive AND the opposite
		rewind-button as active

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetForwardBusy(int data)
{
	xv_set( WNgetItem( WNITEM_FORWARD ),
		PANEL_INACTIVE, 	data,
		NULL );
	xv_set( WNgetItem( WNITEM_REWIND ),
		PANEL_INACTIVE, 	FALSE,
		NULL );

	return;
}

/*******************************************************************************

name        :	WNsetSearchBusy

description :	activate/inactivate the search button 

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	WNsetSearchBusy(int data)
{
	xv_set( WNgetItem( WNITEM_SEARCH ),
		PANEL_INACTIVE,		data,
		NULL );

	return;
}



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	

arguments   :	
   	type		name		description(-I/-O)
1.	Panel_item	item		panel item to store-I
2.	SearchItem	which_item	which panel item to store-I
3.	DataAdmin	what		what to do, set/get-I

return()    :	Panel_item

description :	stores all the used panel items in both panels, report
		and control

use	    :	only via macros WNsetItem, WNgetItem

restrictions:	

bugs	    :	none reported

*******************************************************************************/
Panel_item	WNitem(Panel_item item, SearchItem which_item, DataAdmin what)
{
	static Panel_item	items[ WNITEM_MAX_ITEMS ];

	if ( what == DATA_SET )
	{
		items[ which_item ] = item;
	}

	return( items[ which_item ] );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	WNsearchWindow

arguments   :	
   	type		name		description(-I/-O)
1.	Menu		menu
2.	Menu_item	menu_item

return()    :	void

description :	creates the search window, checks if it has been already
		created or iconified

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void WNsearchWindow(Menu menu, Menu_item menu_item)
{
	Panel		panel;

	Server_image	icon_image;
	Icon		icon;

/********************************************/
/* checking if the window is already opened */
/********************************************/
	if ( frame != (Frame)NULL )
	/* 'frame' (global) is initialized with NULL and if is window is
	    destroyed then fram is reset to NULL, so we now if it is
	    already created or not */
	{
		/* the search window was already created */

		
		xv_set( frame,
			XV_SHOW,	TRUE,	/* expose in the foreground */
			FRAME_CLOSED,	FALSE,	/* de-iconize */
			NULL );
		/* do not continue the creating function */
	}

/****************************/
/* create the search window */
/****************************/
	frame = (Frame)xv_create( base_frame, FRAME,
		FRAME_LABEL,		"Search Strategies",
		FRAME_DONE_PROC,	WNnotifyMenuQuit,	
		XV_WIDTH,		WNFRAME_SIZE_X,
		XV_HEIGHT,		WNFRAME_SIZE_Y,
		FRAME_SHOW_RESIZE_CORNER, 	FALSE,
		NULL );

/*********************************************************************/
/* fetch the quit functions (button, window menu and GraphEd itself) */
/*********************************************************************/
	notify_interpose_destroy_func( frame, WNnotifyDestroyFrame );

/************************************************************************/
/* create the very beautiful icon, self describing and ... so beautiful */
/************************************************************************/
	icon_image = (Server_image)xv_create( XV_NULL, SERVER_IMAGE,
		XV_WIDTH,		64,
		XV_HEIGHT,		64,
		SERVER_IMAGE_BITS,	icon_bits,
		NULL );
	icon = (Icon)xv_create( frame, ICON,
		ICON_IMAGE,		icon_image,
		XV_X,			100,
		XV_Y,			100,
		NULL );
	xv_set( frame, FRAME_ICON, icon, NULL );

/********************************/
/* create the control panel ... */
/********************************/
	panel = (Panel)xv_create( frame, PANEL,
		XV_X,			0,
		XV_Y,			0,
		XV_WIDTH,		WNFRAME_SIZE_X,
		XV_HEIGHT,		WNFRAME_SEPARATOR,
		XV_HELP_DATA,		"search:searching",
		NULL );
/********************************/
/* ... and fill it with buttons */
/********************************/
	WNcreateControl( panel );

/********************************/
/* create the report  panel ... */
/********************************/
	panel = (Panel)xv_create( frame, PANEL,
		XV_X,			0,
		XV_Y,			WNFRAME_SEPARATOR+1,
		XV_WIDTH,		WNFRAME_SIZE_X,
		XV_HEIGHT,		WNFRAME_SIZE_Y - WNFRAME_SEPARATOR,
		XV_HELP_DATA,		"search:searching",
		NULL );
/**************************************/
/* .. and fill it with the info texts */
/**************************************/
	WNcreateReport( panel );

/******************************************************************/
/* initialize all the values (animation, method, interval, .... ) */
/******************************************************************/
	COinitProgram();


/*******************************************/
/* display the search window on the screen */
/*******************************************/
	xv_set( frame, XV_SHOW, TRUE, NULL );
}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file window.c 
******************************************************************************/
