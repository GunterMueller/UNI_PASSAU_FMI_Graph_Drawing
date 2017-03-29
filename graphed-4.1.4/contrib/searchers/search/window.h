
/* do not include this file twice */
#ifndef	_INCLUDE_WINDOW
#define _INCLUDE_WINDOW

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




	File	:	window.h

	Date	:	93/02/17	(05:04:17)

	Version	:	1.2

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Inteface (graphic)	:	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	

Modul   :	


********************************************************************************


Description of window.h :



*******************************************************************************/


/******************************************************************************
*									      *
*			standard includes				      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			gui includes    	 			      *
*									      *
*******************************************************************************/

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>


/******************************************************************************
*									      *
*			GraphEd & Sgraph includes			      *
*									      *
*******************************************************************************/

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include <graphed/user_header.h>


/******************************************************************************
*									      *
*			local includes		 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			defines 		 			      *
*									      *
*******************************************************************************/

#define INTERVAL_MAX		60
#define INTERVAL_MIN		 0

/******************************************************************************
*									      *
*			macros  		 			      *
*									      *
*******************************************************************************/

/* two macros for saving and loading the panel-items of the search-window,
   used for a later manipulation of the panel-items */

#define WNgetItem( WHICH_ITEM )\
                WNitem( (Panel_item)NULL, WHICH_ITEM, DATA_GET )

#define WNsetItem( ITEM, WHICH_ITEM )\
                (void)WNitem( ITEM, WHICH_ITEM, DATA_SET )




#define WNreportSearchers()\
	WNreportOneItem( WNgetItem( WNITEM_SEARCHERS ), COsearchers() )
#define WNreportMaxSearchers()\
	WNreportOneItem( WNgetItem( WNITEM_MAX_SEARCHERS ), COmaxSearchers() )
#define WNreportSteps()\
	WNreportOneItem( WNgetItem( WNITEM_STEPS ), COsteps() )
#define WNreportMaxSteps()\
	WNreportOneItem( WNgetItem( WNITEM_MAX_STEPS ), COmaxSteps() )
#define WNreportNodes()\
	WNreportOneItem( WNgetItem( WNITEM_NODES ), COnodes() )
#define WNreportNodesSet()\
	WNreportOneItem( WNgetItem( WNITEM_NODES_SET ), COnodesSet() )
#define WNreportNodesNotSet()\
	WNreportOneItem( WNgetItem( WNITEM_NODES_NOT_SET ), COnodesNotSet() )
#define WNreportNodesUnset()\
	WNreportOneItem( WNgetItem( WNITEM_NODES_UNSET ), COnodesUnset() )
#define WNreportEdges()\
	WNreportOneItem( WNgetItem( WNITEM_EDGES ), COedges() )
#define WNreportEdgesClear()\
	WNreportOneItem( WNgetItem( WNITEM_EDGES_CLEAR ), COedgesClear() )
#define WNreportEdgesNotClear()\
	WNreportOneItem( WNgetItem( WNITEM_EDGES_NOT_CLEAR ), COedgesNotClear() )





/******************************************************************************
*									      *
*			structs			 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			types			 			      *
*									      *
*******************************************************************************/


typedef enum {
		WNITEM_FILENAME,
		WNITEM_SEARCH,
		WNITEM_METHOD,
		WNITEM_STRATEGY,
		WNITEM_ALGORITHM,
		WNITEM_ANIMATION,
		WNITEM_PLAY_STOP,
		WNITEM_PLAY_STOP_TEXT,
		WNITEM_STATISTICS,
		WNITEM_INFO,
		WNITEM_MAX_SEARCHERS,
		WNITEM_SEARCHERS,
		WNITEM_MANUAL,
		WNITEM_REWIND,
		WNITEM_FORWARD,

		/* statistics */

		WNITEM_STEPS,
		WNITEM_STEPS_TEXT,
		WNITEM_MAX_STEPS,
		WNITEM_MAX_STEPS_TEXT,
		WNITEM_NODES,
		WNITEM_NODES_TEXT,
		WNITEM_NODES_SET,
		WNITEM_NODES_SET_TEXT,
		WNITEM_NODES_UNSET,
		WNITEM_NODES_UNSET_TEXT,
		WNITEM_NODES_NOT_SET,
		WNITEM_NODES_NOT_SET_TEXT,
		WNITEM_EDGES,
		WNITEM_EDGES_TEXT,
		WNITEM_EDGES_CLEAR,
		WNITEM_EDGES_CLEAR_TEXT,
		WNITEM_EDGES_NOT_CLEAR,
		WNITEM_EDGES_NOT_CLEAR_TEXT,


		/* last entry in list !! */
		WNITEM_MAX_ITEMS
} SearchItem;


/******************************************************************************
*									      *
*			global variables				      *
*									      *
*******************************************************************************/

extern Frame	frame;

/******************************************************************************
*									      *
*			functions					      *
*									      *
*******************************************************************************/

extern GraphEd_Menu_Proc	WNsearchWindow;
extern Panel_item	WNitem(Panel_item item, SearchItem which_item, DataAdmin what);
extern void	WNcreateStatistics(Panel panel);
extern void	WNdestroyStatistics(void);
extern void	WNmenuAlgorithms(void);
extern void	WNshowFilename(void);
extern void	WNsetAnimation(void);
extern void	WNsetManual(void);
extern void	WNsetMethodBusy(int data);
extern void	WNsetAnimationBusy(int data);
extern void	WNsetAlgorithmsBusy(int data);
extern void	WNsetRewindBusy(int data);
extern void	WNsetForwardBusy(int data);
extern void	WNsetStop(void);
extern void	WNsetPlay(void);
extern void	WNsetSearchBusy(int data);
extern void	WNsetMethod(void);
extern void	WNsetStrategy(void);



/* file: notify.c */

extern void	WNnotifyClear(Panel_item item, int value);
extern void	WNnotifySearch(Panel_item item, int value);
extern void	WNnotifyHelp(Panel_item item, int value);
extern void	WNnotifyMethod(Panel_item item, int value);
extern void	WNnotifyStrategy(Panel_item item, int value);
extern void	WNnotifyAlgorithm(Panel_item item, int value);
extern void	WNnotifyManual(Panel_item item, int value);
extern void	WNnotifyAnimation(Panel_item item, int value);
extern void	WNnotifyBackstep(Panel_item item, int value);
extern void	WNnotifyRewind(Panel_item item, int value);
extern void	WNnotifyPlay(Panel_item item, int value);
extern void	WNnotifyStop(Panel_item item, int value);
extern void	WNnotifyForward(Panel_item item, int value);
extern void	WNnotifyStep(Panel_item item, int value);
extern void	WNnotifyInterval(Panel_item item, int value);
extern int 	WNnotifyQuit(Panel_item item, int value);
extern void	WNnotifyStatistics(Panel_item item, int value);
extern void	WNnotifyReset(Panel_item item, int value);
extern User_event_functions_result	WNnotifyManualEvent(UEV_info info, Event *event);
extern void	WNnotifyManualMove(Sgraph_proc_info info, Sgraph_event_proc_info uev_info, Event *event);


extern Notify_value	WNnotifyDestroyFrame(Notify_client client, Destroy_status status);
extern void		WNnotifyMenuQuit(void);





extern void	WNreport(void);
extern void	WNreportOneItem(Panel_item item, unsigned int number);
extern void	WNshowInfo(char *string);



extern void	WNstopAnimation(void);


#endif /* do not include file twice */
/******************************************************************************
*		       [EOF] end of header-file window.h 
******************************************************************************/
