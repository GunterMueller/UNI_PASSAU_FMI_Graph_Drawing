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

Modul   :	data administration


********************************************************************************


Description of %M% :
	administrates all neccessary data for the control level, saving
	processing and retrieving.

	Most functions are called only(!) via macros.


********************************************************************************


Functions of %M% :



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
#include <search/window.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*			local defines 		 			      *
*                                                                             *
*******************************************************************************/

#define INIT_METHOD		METHOD_NODE_SEARCH
#define INIT_STRATEGY		OPTIMUM
#define INIT_MANUAL		FALSE
#define INIT_ANIMATION		TRUE
#define INIT_STATISTICS		FALSE
#define INIT_RECORDED		FALSE
#define INIT_INTERVAL		17

/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/

static Sgraph	graph;


/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/

/******************************************************************************
*                                                                             *
*	two functions in this file, but there used only once (yes it is       *
*	neccessary) by an other function. That why they are placed right      *
*	near to this function.						      *
*                                                                             *
******************************************************************************/


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

name        :	COdataMethod

arguments   :	
   	type		name		description(-I/-O)
1.	Method		data		method to save-I
2.	DataAdmin	what		set or get method-I

return()    :	Method

description :	stores the method

use	    :	calls only via the macros:
		COmethod()	to retrieve the saved method
		COsetMethod()	to set the new method

restrictions:	do not change while searching !

bugs	    :	'data' is not checked for the right range

*******************************************************************************/
Method		COdataMethod(Method data, DataAdmin what)
{

	static Method	store = INIT_METHOD;

	if ( what == DATA_SET )	
	{
		store = data;
	}

	return( store );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataStrategy

arguments   :	
   	type		name		description(-I/-O)
1.	Strategy	data		strategy to save-I
2.	DataAdmin	what		set or get-I

return()    :	Strategy

description :	stores or returns the current strategy class

use	    :	calls only via the macros:
		COstrategy()
		COsetStrategy()

restrictions:	do not change while searching !

bugs	    :	'data' is checked for the right range

*******************************************************************************/
Strategy	COdataStrategy(Strategy data, DataAdmin what)
{

	static Strategy	store = INIT_STRATEGY;

	if ( what == DATA_SET )	
	{
		store = data;
	}

	return( store );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataManual

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		on or off manual mode-I
2.	DataAdmin	what		set or get-I

return()    :	bool

description :	saves and returns the current state of manual mode

use	    :	calls only via the macros:
		COmanual()
		COsetManual()

restrictions:	do not change while searching !

bugs	    :	none reported

*******************************************************************************/
bool		COdataManual(int data, DataAdmin what)
{

	static bool	store = INIT_MANUAL;

	if ( what == DATA_SET )	
	{
		store = data;
	}

	return( store );

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataAnimation

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		on or off animation mode-I
2.	DataAdmin	what		set or get-I

return()    :	bool

description :	saves and returns the current state of animation mode depending
		on the current algorithm, some algorithm cannot animate the
		search, so animation is not possible ==> returns FALSE

use	    :	calls only via the macros:
		COanimation()
		COanimate() (same as COanimation)
		COsetAnimation()

restrictions:	do not change while searching !

bugs	    :	none reported

*******************************************************************************/
bool		COdataAnimation(int data, DataAdmin what)
{

	static bool	store = INIT_ANIMATION;

	if ( what == DATA_SET )	
	{
		store = data;
	}
	if ( COmanual() )
	{
	/************************************************************/
	/* in manual mode the user have to see what he/she is doing */
	/************************************************************/
		return( TRUE );
	}
	else if ( store == TRUE )
	{	
	/************************************/
	/* can the algorithm do animation ? */
	/************************************/
		return( COalgorithmAnimation( COalgorithm() ) );
	}

	return( store );

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataStatistics

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		statistics diplayed or not-I
2.	DataAdmin	what		set,get or toggling-I

return()    :	bool

description :	saving if the more-button statistics are displayed or not,
		toggling the value and retrieving the data

use	    :	calls only via the macros:
		COstatistics()
		COsetStatistics()
		COtoggleStatistics()

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool		COdataStatistics(int data, DataAdmin what)
{
	
	static bool	store = INIT_STATISTICS;

	if ( what == DATA_SET )	
	{
		store = data;
	}
	else if ( what == DATA_PROCESS )
	{
		/* toggle the value */
		store = !store;
	}


	return( store );

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataRecorded

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		anything recorded ?-I
2.	DataAdmin	what		set or get-I

return()    :	bool

description :	if anything was recorded the returned value is true else false

use	    :	calls only via the macros:
		COrecorded()
		COsetRecorded()

restrictions:	do not change the recorded value, only the record module is
		allowed to do it - so use only COrecorded()

bugs	    :	COsetRecorded( FALSE ) does not delete the recorded search
		it saves only the state of recording

*******************************************************************************/
bool		COdataRecorded(int data, DataAdmin what)
{
	static bool	store = INIT_RECORDED;

	if ( what == DATA_SET )	
	{
		store = data;
	}
	else if ( what == DATA_PROCESS )
	{
		store = !store;
	}

	return( store );

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataBackstep

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		anything recorded ?-I
2.	DataAdmin	what		set or get-I

return()    :	bool

description :	direction of the animation, backwards or forward

use	    :	calls only via the macros:
		CObackstep()
		COsetBackstep()

restrictions:	do not call during animation

bugs	    :	setting data is not checked whether is anything recorded,
		also is not checked for animation

*******************************************************************************/
bool		COdataBackstep(int data, DataAdmin what)
{
	static bool	store = FALSE;

	if ( what == DATA_SET )	
	{
		store = data;
	}
	else if ( what == DATA_PROCESS )
	{
		store = !store;
	}

	return( store );

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataFastPlay

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		fast forward or rewind ?-I
2.	DataAdmin	what		set or get-I

return()    :	bool

description :	saves the current state of (fast!) playing, rewind or forward,
		if FastPlay is True then automatically Play is also
		set true

use	    :	calls only via the macros:
		COfastPlay()
		COsetFastPlay()

restrictions:	

bugs	    :	setting data is not checked whether is anything recorded,

*******************************************************************************/
bool		COdataFastPlay(int data, DataAdmin what)
{
	static bool	store = FALSE;

	if ( what == DATA_SET )	
	{
		store = data;
		if ( store == TRUE && !COplay() )
		{
			COsetPlay();
		}
		else if ( COplay() )
		{
			COsetPlay();
		}
	}
	else if ( what == DATA_PROCESS )
	{
		store = !store;
	}

	return( store );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataPlay

arguments   :	
   	type		name		description(-I/-O)
1.	bool		data		fast forward or rewind ?-I
2.	DataAdmin	what		set or get-I

return()    :	bool

description :	saves the state of playing, if on or off,
		if play is turned off, FastPlay is also turned off

use	    :	calls only via the macros:
		COplay()
		COsetPlay()

restrictions:	

bugs	    :	setting data is not checked whether is anything recorded.

*******************************************************************************/
bool		COdataPlay(int data, DataAdmin what)
{
	static bool	store = FALSE;

	if ( what == DATA_SET )	
	{
		store = data;
		if ( store == FALSE && COfastPlay() )
		{
			COdataFastPlay( FALSE, DATA_SET );
		}
	}
	else if ( what == DATA_PROCESS )
	{
		store = !store;
	}

	return( store );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataAlgorithm

arguments   :	
   	type		name		description(-I/-O)
1.	unsigned int	data		number current active algorithm-I
2.	DataAdmin	what		set/get-I	

return()    :	unsigned int

description :	saves resp. retrieves the number of the current active
		algorithm depending on the algorithm class

use	    :	calls only via the macros:
		COalgorithm()
		COsetAlgorithm()

restrictions:	

bugs	    :	'data' isn't checked for a possible value

*******************************************************************************/
unsigned int	COdataAlgorithm(unsigned int data, DataAdmin what)
{

/***************************************************************/
/* stored in an array, 0 = optimum, 1 = heuristic, 2 = approx. */
/***************************************************************/
	static unsigned int	store[ STRATEGY_MAX ] = { 0, 0, 0 };

	if ( what == DATA_SET )	
	{
		store[ (int)COstrategy() ] = data;
	}

	return( store[ (int)COstrategy() ] );

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataInterval

arguments   :	
   	type		name		description(-I/-O)
1.	
2.	DataAdmin	what		set/get-I

return()    :	unsigned int

description :	saves the time gap between to steps

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
unsigned int	COdataInterval(unsigned int data, DataAdmin what)
{

	static unsigned int	store = INIT_INTERVAL;

	if ( what == DATA_SET )	
	{
		store = data;
	}

	return( store );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COreport

arguments   :	
   	type		name		description(-I/-O)
1.	unsigned int	data		different meaning-I
2.	ReportData	which_data	which meaning :-) -I
3.	DataAdmin	what		set/get or process, increment...-I

return()    :	unsigned int

description :	administration of all the different statistic data, that
		is in the lower part of the search window,
		e.g. #searchers, maximum, #nodes, #steps, and many more

		searchers	current number of searchers
		maxSearchers	maximum number of searchers used already
		hiddenSearchers keep track of the # of searchers (don't show)
				useful for non animated algorithm and other
		hiddenMaxSearchers	...
		steps		current number of steps done in animation
		maxSteps	steps to do
		nodes		#nodes
		nodesNotSet	#nodes never contained a searcher
		nodesSet	#nodes containing a searcher
		nodesUnset	#nodes that contained a searcher (moved away)
		edges		#edges
		edgesNotClear	#edges contaminated
		edgesClear	#edges *de*contaminated

use	    :	use the macros:
		COsearchers()
		COincSearchers()
		COdecSearchers()
		COsetSearchers()
		(all set get dec and inc !!)
			maxSearchers()
			hiddenSearchers()
			hiddenMaxSearchers()
			steps()
			maxSteps()
			nodes()
			nodesUnset()
			nodesNotSet()
			nodesSet()
			edges()
			edgesClear()
			edgesNotClear()
		COreset()

restrictions:	nodes and edges don't have to be changed while searching

bugs	    :	none reported

*******************************************************************************/
unsigned int	COreport(unsigned int data, ReportData which_data, DataAdmin what)
{

	static unsigned int	data_array[ COMAX_REPORT ] =
		{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

	unsigned int		return_data = 0;


	switch( what )
	{
	case DATA_SET:
		data_array[ which_data ] = return_data = data;
		break;

	case DATA_GET:
		return_data = data_array[ which_data ];
		break;

	case DATA_INC:
		data_array[ which_data ]++;

		if ( which_data == COSEARCHERS )
		{
		/**************************************************/
		/* maybe the maximum have to be incremented too ? */
		/**************************************************/
			if ( data_array[ which_data ] > data_array[ COMAX_SEARCHERS ] )
			{
				data_array[ COMAX_SEARCHERS ] = data_array[ which_data ];
			/*************************/
			/* show it on the screen */
			/*************************/
				WNreportMaxSearchers();
			}
		}
		else if ( which_data == COHIDDEN_SEARCHERS )
		{
		/******************************/
		/* same as "normal" searchers */
		/******************************/
			if ( data_array[ which_data ] > data_array[ COMAX_HIDDEN_SEARCHERS ] )
			{
				data_array[ COMAX_HIDDEN_SEARCHERS ] = data_array[ which_data ];
			/*******************************************/
			/* nothing to report (that's why "hidden") */
			/*******************************************/
			}
		}
		break;

	case DATA_DEC:
		if ( data_array[ which_data ] == 0 )
		{
		/**********************************/
		/* no negative values are allowed */
		/**********************************/
			SetErrorAndReturn( IERROR_DECREMENT_ZERO, 0 );
		}
		else
		{
			data_array[ which_data ]--;
		}
		break;

	case DATA_PROCESS:
		/*****************/
		/* nothing to do */
		/*****************/
		break;

	case DATA_RESET:
		{
			int	index;

		/********************************/
		/* set all values to zero again */
		/********************************/
			for( index = 0; index < COMAX_REPORT; index++ )
				data_array[ index ] = 0;
		}

		break;

	default:
	/**************/
	/* no default */
	/**************/
		break;

	}
	if ( what == DATA_RESET )
	{
	/******************************************/
	/* show all values, they are all zero now */
	/******************************************/
		WNreport();
	}
	else if ( what != DATA_GET )
	{
	/***************************************************/
	/* show all the changed things, except "hidden..." */
	/***************************************************/

	/*******************************************************/
	/* "Searchers" and "MaxSearchers" are always displayed */
	/*******************************************************/
		if ( which_data == COSEARCHERS )
		{
			WNreportSearchers();
		}
		else if ( which_data == COMAX_SEARCHERS )
		{
			WNreportMaxSearchers();
		}

	/*************************************/
	/* shall we show more (statistics) ? */
	/*************************************/
		if ( COstatistics() )
		{
			switch( which_data )
			{
			case COSTEPS:
				WNreportSteps();
				break;

			case COMAX_STEPS:
				WNreportMaxSteps();
				break;

			case CONODES:
				WNreportNodes();
				break;

			case CONODES_SET:
				WNreportNodesSet();
				break;

			case CONODES_NOT_SET:
				WNreportNodesNotSet();
				break;

			case CONODES_UNSET:
				WNreportNodesUnset();
				break;

			case COEDGES:
				WNreportEdges();
				break;

			case COEDGES_CLEAR:
				WNreportEdgesClear();
				break;

			case COEDGES_NOT_CLEAR:
				WNreportEdgesNotClear();
				break;

			default:
				break;

			}
		}

	/***********************/
	/* end of the show :-) */
	/***********************/
	}

	return( return_data );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COrecEndOfTape

arguments   :	

return()    :	bool

description :	Animation arrived at the end of the recorded search ?

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool	COrecEndOfTape(void)
{
	return( MVrecordEndOfTape() );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COrecStartOfTape

arguments   :	

return()    :	bool

description :	Animation (Rewind) arrived at the beginning of the search

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool	COrecStartOfTape(void)
{
	return( MVrecordStartOfTape() );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	COloadGraph

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph_proc_info graphinfo	-I

return()    :	void

description :	gets the current graph 

sideeffects :	sets the module variable 'graph'

use	    :	only via call_sgraph_proc

restrictions:	

bugs	    :	cannot detect if buffer has changed

*******************************************************************************/
static void		COloadGraph(Sgraph_proc_info graphinfo)
{

/**********************************************/
/* 'graph' is global and used in 'COgetGraph' */
/**********************************************/
	graph = graphinfo->sgraph;

	if ( graphinfo->buffer != COgetBuffer() )
	{
	/*
		graph = (Sgraph)NULL;
	*/
		/* !!!! Fehler wac gew. */
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COgetGraph

arguments   :	

return()    :	Sgraph

description :	gets the current working graph

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
Sgraph		COgetGraph(void)
{
	bool	locked = test_user_interface_locked();

/***************************************/
/* unlock, otherwise GraphEd complains */
/***************************************/
	unlock_user_interface();

/****************/
/* call GraphEd */
/****************/
	call_sgraph_proc( COloadGraph, NULL );
/**********************************/
/* module variable 'graph' is set */
/**********************************/

/********************************************/
/* return to the old "user_interface" state */
/********************************************/
	if ( locked )
	{
		lock_user_interface();
	}

	return( graph );
}
/******************************************************************************/


/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	COdataPaintGraph

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph_proc_info graphinfo	-I

return()    :	void

description :	calls the repaint procedure of GraphEd

use	    :	only via call_sgraph_proc

restrictions:	

bugs	    :	cannot detect if the current working graph has changed

*******************************************************************************/
static void	COdataPaintGraph(Sgraph_proc_info graphinfo)
{
	if ( graphinfo->buffer == COgetBuffer() )
        {
		graphinfo->no_structure_changes = FALSE;
                graphinfo->no_changes = FALSE;
                graphinfo->repaint = TRUE;
                force_repainting();
	}
	else
	{
		/* !!!! wac gewechslet */
	}
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataGraphinfo

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph_proc_info data		extract the buffer number-I
2.	DataAdmin			repaint or buffer ?-I

return()    :	int

description :	forced a repainting of the graph or saves/returns the
		number of the buffer (of the searched graph)

use	    :	call only via macros
		COrepaint()
		COgetBuffer()
		COsaveGraphinfo()

restrictions:	

bugs	    :	none reported

*******************************************************************************/
int              COdataGraphinfo(Sgraph_proc_info data, DataAdmin what)
{
	bool			locked = test_user_interface_locked();
	static int		buffer = -1;


	if ( what == DATA_SET )
		buffer = data->buffer;
	else if ( what == DATA_PROCESS )
	{
		
		unlock_user_interface();
		call_sgraph_proc( COdataPaintGraph, NULL );
		if ( locked )
			lock_user_interface();
	}

	return( buffer );
}
/******************************************************************************/
		


/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataSetNodeType

arguments   :	
   	type		name		description(-I/-O)
1.	int		data		node type-I
2.	DataAdmin	what		set/get-I

return()    :	int 

description :	sets resp. gets the used node type to show a node with
		a searcher on it

use	    :	calls only via macros
		COsetSetNodeType()
		COgetSetNodeType()

restrictions:	

bugs	    :	none reported

*******************************************************************************/
int		COdataSetNodeType(int data, DataAdmin what)
{
	static int	store = 0;

	if ( what == DATA_SET )
	{
		if ( data == -1 )
			/* the default node type couldn't be loaded */
			store = 0;
		else
			store = data;
	}

	return( store );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COdataUnsetNodeType

arguments   :	
   	type		name		description(-I/-O)
1.	int		data		node type-I
2.	DataAdmin	what		set/get-I

return()    :	int 

description :	sets resp. gets the used node type to show a node which has
		had a searcher on it

use	    :	calls only via macros
		COsetUnsetNodeType()
		COgetUnsetNodeType()

restrictions:	

bugs	    :	none reported

*******************************************************************************/
int		COdataUnsetNodeType(int data, DataAdmin what)
{
	static int	store;

	if ( what == DATA_SET )
	{
		if ( data == -1 )
			/* the default node type couldn't be loaded */
			store = 2;
		else
			store = data;
	}
	return( store );
}
/******************************************************************************/
 


/*******************************************************************************
*									       *
*                       global function                                        *
*									       *
********************************************************************************

name        :   COdataMoveNodeType
 
arguments   :  
        type            name            description(-I/-O)
1.      int             data            node type-I
2.      DataAdmin       what            set/get-I
 
return()    :   int
 
description :   sets resp. gets the used node type to show a searcher moving
		along an edgeline
 
use         :   calls only via macros
                COsetMoveNodeType()
                COgetMoveNodeType()
 
restrictions:    
 
bugs        :   none reported
 
*******************************************************************************/
int             COdataMoveNodeType(int data, DataAdmin what)
{
        static int      store;
 
        if ( what == DATA_SET )
        {
                if ( data == -1 )
                        /* the default node type couldn't be loaded */
                        store = 1;
                else     
                        store = data;
        }
        return( store );
}
/******************************************************************************/ 
 
 

/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COwait

arguments   :	
   	type		name		description(-I/-O)
1.	*unsigned int	seconds		seconds of interval-O
2.	*unsigned int	microseconds	microseconds of interval-O

return()    :	void

description :	computes of the number saved for the time gap between
		to steps the real time

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void		COwait(unsigned int *seconds, unsigned int *microseconds)
{
	unsigned int	time = COinterval();

	if ( time == 0 )
	{
		*seconds = 0;
		*microseconds = 10000;
	}
	else
	{
		*seconds = time / 50;
		*microseconds = ( time % 50 ) *20000;
	}	
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COinitProgram

arguments   :	

return()    :	void

description :	initailizes the data

use	    :	

restrictions:	only before startup

bugs	    :	none reported

*******************************************************************************/
void    COinitProgram(void)
{
        COreset();

        COsetMoveNodeType( add_nodetype( "searchers/move.icon" ) );
        COsetSetNodeType( add_nodetype( "searchers/search.icon" ) );
        COsetUnsetNodeType( add_nodetype( "searchers/nosearch.icon" ) );
 
        COsetManual( FALSE );
        WNsetManual();
 
        COsetAnimation( TRUE );
        WNsetAnimation();
 
        COsetMethod( METHOD_NODE_SEARCH );
        WNsetMethod();
 
        COsetStrategy( HEURISTIC );
        COsetAlgorithm( 0 );
 
        COsetStrategy( APPROXIMATION );
        COsetAlgorithm( 0 );
 
        COsetStrategy( OPTIMUM );
        COsetAlgorithm( 0 );
        WNsetStrategy();
 
        COsetStop();
        WNsetPlay();
 
}
/******************************************************************************/
 


/******************************************************************************
*		       [EOF] end of file %M% 
a*****************************************************************************/
