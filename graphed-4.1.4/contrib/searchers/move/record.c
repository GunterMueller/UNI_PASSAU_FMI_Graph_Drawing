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

#include <malloc.h>

/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>


/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/control.h>
#include <search/move.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

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

typedef struct
{
        unsigned int    max;	/* current maximum of searchers */
	Snode		snode;	/* the source node if required by moving */
        Action  action;		/* what to do */
        bool    mark;		/* a valid configuration */
        union
        {
                Sedge   edge;
                Snode   node;
        } action_on;		/* do the action on what */
} RecordTape;
 

/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/

/************************************/
/* beginning of the recorded search */
/************************************/
static RecordTape	*tape = (RecordTape *)NULL;

/**********************************************************/
/* pointer to the current position of the animated search */
/**********************************************************/
static RecordTape  	*head = (RecordTape *)NULL;



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

name        :	MVrecordAction

arguments   :	
   	type		name		description(-I/-O)
1.	Action		action		what to record-I
2.	char*		data		it is a node or edge !-I
3.	char*		data2		a node-I
4.	

return()    :	void

description :

use	    :	

restrictions:	

bugs	    :	'data' and 'data2' aren't checked to be nodes or any edge

*******************************************************************************/
void	MVrecordAction(Action action, char *data, char *data2)
      	       
     	      
     	       /* sourcenode if ActionClearing */
{
	RecordTape	*last_entry;

/********************************************/
/* check if anything was recorded until now */
/********************************************/
	if ( tape == (RecordTape *)NULL )
	{
	/******************************************************************/
	/* create the recording tape, intialize the beginning of the tape */
	/******************************************************************/
		tape = (RecordTape *)calloc( COmaxSteps()+2, sizeof( RecordTape) );
		head = tape;
		head->max = 0;
		head->action = ACTION_START_OF_TAPE;
		head->mark = TRUE;

	/****************************************************************/
	/* put the head to the next position and and mark it as the end */
	/****************************************************************/
		head++;
		head->max = 0;
		head->action = ACTION_END_OF_TAPE;
		head->mark = TRUE;

	/**************************************************************/
	/* let the whole program know that we have recorded something */
	/**************************************************************/
		COnotifyRecorded();
	}

/*****************************************************************/
/* remove the marker if we are at the end of the recorded search */
/*****************************************************************/
	if ( head->action != ACTION_END_OF_TAPE )
	{
		head->mark = FALSE;
	}	

/********************/
/* store the action */
/********************/
	head->action = action;
	head->max = COhiddenMaxSearchers();	
	if ( action == ACTION_CLEARING ||
	     action == ACTION_CLEARING_PLUS_ONE ||
	     action == ACTION_CLEAR )
	{
	/***************************************************************/
	/* do not save ACTION_CLEAR if a CLEARING has been done before */
	/* an ACTION_CLEAR is implicit done by CLEARING		       */
	/***************************************************************/
		last_entry = head-1;
		if ( ( last_entry->action == ACTION_CLEARING ||
		       last_entry->action == ACTION_CLEARING_PLUS_ONE )
		    && action == ACTION_CLEAR )
			return;	/* ignore it */
		
		head->action_on.edge = (Sedge)data;
		head->snode = (Snode)data2;
	}
	else
	{
		head->action_on.node = (Snode)data;
	}

/**********************************************/
/* move the head to next position of the tape */
/**********************************************/
	head++;

	return;	
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordMark

arguments   :	

return()    :	void

description :	sets a marker that the steps until marker are a valid
		search configuration

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	MVrecordMark(void)
{
/********************************************/
/* check if anything was recorded until now */
/********************************************/
	if ( tape == (RecordTape *)NULL )
	{
	/******************************************************************/
	/* create the recording tape, intialize the beginning of the tape */
	/******************************************************************/
		tape = (RecordTape *)calloc( COmaxSteps()+2, sizeof( RecordTape) );
		head = tape;
		head->max = 0;
		head->action = ACTION_START_OF_TAPE;
		head->mark = TRUE;

	/****************************************************************/
	/* put the head to the next position and and mark it as the end */
	/****************************************************************/
		head++;
		head->max = 0;
		head->action = ACTION_END_OF_TAPE;
		head->mark = TRUE;

	/**************************************************************/
	/* let the whole program know that we have recorded something */
	/**************************************************************/
		COnotifyRecorded();
	}
	else
	{
	/********************************************************/
	/* set the marker, and say that the recording ends here */
	/********************************************************/
		head->mark = TRUE;
		head->action = ACTION_END_OF_TAPE;
		head->max = COhiddenMaxSearchers();
	}	

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordClear

arguments   :	

return()    :	void

description :	removes the recorded search

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	MVrecordClear(void)
{
	if ( tape != (RecordTape *) NULL )
	{
		free( tape );
		tape = head = (RecordTape *)NULL;
	}

/*************************************************/
/* let the remainding program know what happened */
/*************************************************/
	COsetRecorded( FALSE );

	return;
}
/******************************************************************************/

void	MVrecordResetTape(void)
{
/*******************************************************************/
/* if no recorded search is available or no animation will be done */
/* reset makes no sense						   */
/*******************************************************************/
	if ( tape == (RecordTape *)NULL && !COanimation() )
		return;	/* ignore this */

/****************************************************/
/* position to the beginning of the recorded search */
/****************************************************/
	head = tape+1;
	
	return;
}
/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordReset

arguments   :	

return()    :	void

description :	like a tape recorder, set the head position to the beginning of
		the tape

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	MVrecordReset(void)
{
	MVrecordResetTape();

/*************************************************************/
/* initialize the graph  all nodes and edges as contaminated */
/*************************************************************/
	COinitGraphAttributes( COgetGraph() );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordEndOfTape

arguments   :	

return()    :	bool

description :	have we reached the end of the recorded search ?

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool	MVrecordEndOfTape(void)
{
	if ( head == (RecordTape *)NULL )
		return( TRUE );	/* ignore this */

	if ( head->action == ACTION_END_OF_TAPE )
	{
		return( TRUE );
	}
	else
	{
		return( FALSE );
	}
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordStartOfTape

arguments   :	

return()    :	bool

description :	are we now at the beginning of the recorded search ?

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool	MVrecordStartOfTape(void)
{
	if ( head == tape ) /* better than head->action ... includes NULL */ 
	{
		return( TRUE );
	}
	else
	{
		return( FALSE );
	}
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordStep

arguments   :	

return()    :	void

description :	proceed a single step of the recorded search

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	MVrecordStep(void)
{
	if ( head == (RecordTape *)NULL )
		return;	/* ignore an empty tape */

/************************************************************************/
/* move the head to the next postion if we are only at the start marker */
/************************************************************************/
	if ( head->action == ACTION_START_OF_TAPE )
	{
		head++;
	}

/****************************************/
/* but have we reached the end of all ? */
/****************************************/
	if ( head->action == ACTION_END_OF_TAPE )
	{
		return;	
	}	

/**********************************/
/* ... and what shall we do now ? */
/**********************************/
	switch( head->action )
	{
	case ACTION_NODE_SET:
		SimpleMarkNodeSet( head->action_on.node );
		break;

	case ACTION_NODE_UNSET:
		SimpleMarkNodeUnset( head->action_on.node );
		break;

	case ACTION_CLEAR:
		SimpleMarkEdgeClear( head->action_on.edge );
		break;

	case ACTION_CLEARING:
		if ( !COfastPlay() )
			SimpleMarkEdgeClearing( head->action_on.edge, head->snode ); 
		else
			SimpleMarkEdgeClear( head->action_on.edge );
		break;

	case ACTION_CLEARING_PLUS_ONE:
		if ( !COfastPlay() )
			SimpleMarkEdgeClearingPlusOne( head->action_on.edge, head->snode ); 
		else
			SimpleMarkEdgeClear( head->action_on.edge );
		break;
	default:
		break;
	}

/*********************/
/* move to next step */
/*********************/
	head++;

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordToNextStep

arguments   :	

return()    :	bool

description :	do the following step until we have found a marker

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool	MVrecordToNextStep(void)
{

	if ( head == (RecordTape *)NULL )
		return( FALSE ); /* ignore empty tape */

	if ( head->action == ACTION_END_OF_TAPE )
		return( FALSE ); /* all done */

/**************************************************************************/
/* ah, did we find a marker, the search configuration is in a valid state */
/**************************************************************************/
	if ( head->mark )
		return( FALSE );

/*********************************************************************/
/* No :-( we hadn't found a marker, we have to proceed the next step */
/*********************************************************************/
	MVrecordStep();

	return( TRUE );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordBackstep

arguments   :	

return()    :	void

description :	proceeds on step backwards

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void 	MVrecordBackstep(void)
{
	RecordTape	*last;

	if ( head == tape )
	{
	/***************************************************************/
	/* we are already at the beginning (or both pointers are NULL) */
	/***************************************************************/
		return;
	}

/******************/
/* move backwards */
/******************/
	head--;

	if ( head == tape )
	{
	/***********************************/
	/* but now we are at the beginning */
	/***********************************/

		COsetMaxSearchers( head->max );
		return;
	}

/****************************/
/* do the opposite action ! */
/****************************/
	switch( head->action )
	{
	case ACTION_NODE_SET:
		SimpleMarkNodeNotSet( head->action_on.node );
		break;

	case ACTION_NODE_UNSET:
		SimpleMarkNodeSet( head->action_on.node );
		break;

	case ACTION_CLEAR:
	case ACTION_CLEARING:
	case ACTION_CLEARING_PLUS_ONE:
		SimpleMarkEdgeNotClear( head->action_on.edge );
		break;

	default:
		break;
	}

/*********************************************************************/
/* set now the maximum searchers that has been in this configuration */
/*********************************************************************/
	last = head-1;
	COsetMaxSearchers( last->max );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MVrecordToNextBackstep

arguments   :	

return()    :	bool

description :	do the previous step until we find a marker for a valid
		configuration

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
bool	MVrecordToNextBackstep(void)
{

	if ( head == (RecordTape *)NULL )
		return( FALSE );	/* ignore this */

	if ( head->mark )
		return( FALSE );	/* marker found ! */

/***********************************/
/* proceed a single step backwards */
/***********************************/
	MVrecordBackstep();

/******************************************************************************/
/* are we at the beginning of the recorded search ? ==> cannot step backwards */
/******************************************************************************/
	if ( head == tape )
		return( FALSE );

/*****************************************************************/
/* is there a marker for valid configuration ? ==> stop now here */
/*****************************************************************/
	if ( head->mark )
		return( FALSE );
	
	return( TRUE );
}
/******************************************************************************/



Snode	MVrecordGetNextSetNode(void)
{

	Snode	node;
	if ( head == (RecordTape *)NULL )
		return( empty_node );

	while ( head->action != ACTION_END_OF_TAPE )
	{
		if ( head->action == ACTION_NODE_SET )
		{
			node = head->action_on.node;
			head++;
			return( node );
		}
		head++;

	}

	return( empty_node );

}
	
	

/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
