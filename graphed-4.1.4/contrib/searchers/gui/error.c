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


The error messages numbers are decoded in this file, and printed or saved 
for further use, i.e. in higher layer of the program the error may be
recovered and printed to the user.



********************************************************************************


Functions of %M% :

ErrorData


*******************************************************************************/


/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

/******************************************************************/
/* included: the functions error, message,... of graphed are used */
/******************************************************************/
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
#include <search/err.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/

/***********************/
/* error message array */
/***********************/

static struct 
{ 
	int error_number;
	char error_text[81];
}  error_record[] =
{

/*************** MESSAGES ***************************************************/

/************************************************/
/* USER: insert your new MESSAGE message here   */
/* DO NOT USE STRINGS LONGER THAN 80 CHARACTERS */
/************************************************/

/*************** WARNINGS ***************************************************/

{ WARNING_EMPTY_GRAPH, 
		"Cannot search an empty graph"},
{ WARNING_DIGRAPH,  
		"Cannot search a digraph"},
{ WARNING_NOT_CONNECTED_GRAPH, 
		"Only connected graphs"},


/************************************************/
/* USER: insert your new WARNING message here   */
/* DO NOT USE STRINGS LONGER THAN 80 CHARACTERS */
/************************************************/

/*************** ERRORS *****************************************************/

{ ERROR_NOT_A_TREE, 
		"Graph is not a tree" },
{ ERROR_NOT_A_COMPLETE_GRAPH, 
		"Graph is not complete" },
{ ERROR_NOT_A_GRID, 
		"Graph is not grid" },
{ ERROR_NOT_A_KNM, 
		"Graph is not a Kuratowski graph (Kn,m)" },
{ ERROR_MOVETO_IN_NS_METHOD, 
		"node search, moving over edges not allowed"},
{ ERROR_MOVETO_FROM_WRONG_SOURCENODE,
		"moving impossible from not cleared node"},
{ ERROR_SETON_NULL_NODE, 
		"function SetOn with no node called (nil)" },
{ ERROR_MOVETO_NULL_NODE, 
		"function MoveTo with no node(s) called (nil)" },

/************************************************/
/* USER: insert your new ERROR message here     */
/* DO NOT USE STRINGS LONGER THAN 80 CHARACTERS */
/************************************************/

/*************** FATAL ERRORS ***********************************************/

/**************************************************/
/* USER: insert your new FATAL ERROR message here */
/* DO NOT USE STRINGS LONGER THAN 80 CHARACTERS   */
/**************************************************/

/*************** INTERNAL ERRORS ********************************************/

{ IERROR_SWITCH_NO_CASE, 
		"no case in switch -- no default" },
{ IERROR_MAX_SEARCHERS_LT_ZERO, 
		"negative value of maximum searchers" }, 
{ IERROR_SEARCHERS_LT_ZERO, 
		"negative value of searchers" },
{ IERROR_STEPS_LT_ZERO, 
		"negative steps aren't possible" },
{ IERROR_FREE_NULL_PTR, 
		"null pointer cannot be free'd" },
{ IERROR_NULL_PTR, 
		"null pointer access" },
{ IERROR_NO_MEM, 
		"not enough memory" },
{ IERROR_END_OF_TAPE, 
		"recording error" },
{ IERROR_DECREMENT_ZERO, 
		"cannot decrement pos. value" },


/* USER: insert your new INTERNAL ERROR message here */
/* DO NOT USE STRINGS LONGER THAN 80 CHARACTERS */

/*************** DEBUG MESSAGES *********************************************/

/****************************************************/
/* USER: insert your new DEBUG MESSAGE message here */
/* DO NOT USE STRINGS LONGER THAN 80 CHARACTERS     */
/****************************************************/

/***************************************************************************/

/* ERROR_OK :this must be the last entry !!! */
{ERROR_OK, 
"no error - but printing this message is an error in the error-function !"}
};

/**********************************/
/* end of the error message array */
/**********************************/



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

name        :	ErrorData

arguments   :	
   	type		name		description(-I/-O)
1.	int		errnumber	the error which occured -I
2.	DataAdmin	what		what to do with errnumber -I

return()    :	int 

description :	processes the error i.e. printing, saves the error for
		a later use, an error can only be recovered in some
		functions not in the function in which it has occured 
		e.g. an algorithm cannot compute the rest of the search 
		caused by an nil-pointer, the algorithm is aborted and
		returns to the user interface to wait for another command

use	    :	only with the macros described in err.h

those are:
GetError()			returns the current errornumber
SetError(ERROR)			sets the new errornumber
SetErrorAndReturnVoid(ERROR)	same as: SetError(ERROR); return;
SetErrorAndReturn(ERROR,DATA)	same as: SetError(ERROR); return(DATA);
ResetError()			error recovered and reset to no error
PrintError()			prints error message, only if an error occured
				the errornumber is reset to no error
SetAndPrintError(ERROR)		same as: SetError(ERROR); PrintError();
PrintErrorAndReturnVoid()	same as: PrintError(); return;
PrintErrorAndReturn(DATA)	same as: PrintError(); return(DATA);
IfError				like if with condition error has occured
IfErrorReturn			if error has occured then return
OnErrorReturn			same as: IfErrorReturn

restrictions:	

bugs	    :	when the error was printed, the errnumber is reset to
		zero, PrintError-Family always means that you have
		recovered the error. But getting the error-number with
		GetError() and setting with SetError() after the
		PrintError you can recover later on in your program.
		When recovering you have to reset (ResetError)


*******************************************************************************/
int  		ErrorData(int errnumber, DataAdmin what)
{
/****************************/
/* initialize with no error */
/****************************/
	static int	the_error = ERROR_OK;

	
	if ( what == DATA_SET )
	{
	/****************************************************************/
	/* test if there was already another error, print the old error */
	/* and then set the new one 					*/
	/****************************************************************/
		if ( the_error != ERROR_OK )
		{
		/*************************/
		/* print the "old" error */
		/*************************/
			ErrorData( ERROR_OK, DATA_PROCESS );
		}
	/***********************/
	/* set the "new" error */
	/***********************/
		the_error = errnumber; 
	}
	else if ( what == DATA_PROCESS )	/* print the_error */
	{
		int 	index = 0;	/* for searching in the array */
		char	*string;	/* the formatted output */

	/************************************************************/
	/* test if there was already another error and a new on has */
	/* occured, print the old one and process the new one       */	
	/************************************************************/
		if ( errnumber != ERROR_OK && the_error != ERROR_OK )
		{
		/*************************/
		/* print the "old" error */
		/*************************/
			ErrorData( ERROR_OK, DATA_PROCESS );
		}

	/***************************************************************/
	/* was not only called to print, 'errnumber' is to be printed, */
	/* i.e. an error occured and will be printed promptly,	       */ 
	/* usually an error is set and later printed, because	       */
	/* the function returns to the control-loop 		       */ 
	/***************************************************************/
		if ( errnumber != ERROR_OK )
			the_error = errnumber;

	/***********************************************************/
	/* if no error has occured then return without any message */
	/***********************************************************/
		if ( the_error == ERROR_OK ) 
			return( ERROR_OK );	


		do 
		{
		/*******************************************************/
		/* search for this error, it isn't sorted or something */
		/* else, but we know that after an error response time */
		/* to the user is not so important as for algorithms   */
		/* e.g. 0.04 seconds more doesn't matter 	       */
		/*******************************************************/
			if ( error_record[index].error_number == the_error )
			{
			/*************************/	
			/* decode the error type */
			/*************************/	
				switch( error_record[index].error_number & WHICH_ERROR )
				{
				case IS_MESSAGE :
					message( "#%d: %s\n", the_error & ~WHICH_ERROR, error_record[index].error_text );	
					break;
				case IS_WARNING :
					warning( "#%d: %s\n", the_error & ~WHICH_ERROR, error_record[index].error_text );	
					break;
				case IS_ERROR :
					error( "#%d: %s\n", the_error & ~WHICH_ERROR, error_record[index].error_text );	
					break;
				case IS_FATALERROR :
					fatal_error( "#%d: %s\n", the_error & ~WHICH_ERROR, error_record[index].error_text );	
					break;
				case IS_INTERNALERROR :
					error( "(INTERNAL)#%d: %s\n", the_error & ~WHICH_ERROR, error_record[index].error_text );	
					break;
				case IS_DEBUGMESSAGE :
					message( "(DEBUG)#%d: %s\n", the_error & ~WHICH_ERROR, error_record[index].error_text );	
					break;
				default:
					error("(INTERNAL)#%d(%d): error occured, but no specified type of message, check message numbers > 0x2000\n%s", the_error & ~(WHICH_ERROR), the_error, string );
					break;
				}
			/************************************/
			/* error was processed ==> reset it */
			/************************************/
				the_error = ERROR_OK;
			}
		}
		while( 	   error_record[index++].error_number != ERROR_OK 
			&& the_error != ERROR_OK );
	/************************************************************/
	/* searches until the ERROR_OK occures, we know that is the */
	/* last entry in the error array (see declaration of	    */
	/* the array). If the_error is set to ERROR_OK then we know */
	/* that the error message was found and printed, searching  */
	/* the rest of the array is not useful 			    */
	/************************************************************/
	
		if ( the_error != ERROR_OK ) 
		{
		/*******************************************/
		/* error wasn't reset ==> no message found */	
		/*******************************************/
			error("(INTERNAL)#%d(%d): no specified message for this error number", the_error & ~(WHICH_ERROR), the_error );
		/*************************************/
		/* reset the error, it was processed */
		/*************************************/
			the_error = ERROR_OK;
		}

		/*******************************/
		/* for editing the graph again */
		/*******************************/
		unlock_user_interface();
	}	
	else if ( what == DATA_RESET ) the_error = ERROR_OK;

/******************************************/
/* always returns the current errornumber */
/******************************************/
	return( the_error );
}


/******************************************************************************
          [EOF] end of file %M% 
******************************************************************************/
