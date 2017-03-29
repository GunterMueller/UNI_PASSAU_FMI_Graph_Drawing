#ifndef _INCLUDE_ERR
#define _INCLUDE_ERRH 		/* do not include twice */

/*******************************************************************
SEARCH STRATEGIES ON GRAPHS

File    : err.h
of      : 93/01/08  19:10:58
Version : 1.1 
Author  : Schweikardt Andreas
RCS     : $RCSfile: graph.h $$Date: 92/01/18 11:11:31 $$Revision: 1.2 $

Layer   : 
Modul   : error functions

OBJECTS : 

PORTABILITY 
  language  : C
  OS        : 
  GUI       :
  other     :

**************************************************************************/

/***************** defines ***********************************************/

#define ERROR_OK        	0	/* do not chnage this value ! */

/* for marking the different error types */
#define WHICH_ERROR     	0xe000
#define IS_WARNING      	0x4000
#define IS_ERROR        	0x8000
#define IS_FATALERROR       	0xe000
#define IS_INTERNALERROR    	0xc000
#define IS_MESSAGE      	0x0000
#define IS_DEBUGMESSAGE     	0x2000

/***************** macros ************************************************/

/* these macros are used for standardized acces to the error function,
   please do not use any other function */
#define GetError()		ErrorData( ERROR_OK, DATA_GET )
#define SetError( DATA )	ErrorData( DATA, DATA_SET )
#define SetErrorAndReturnVoid(DATA)	{ErrorData( DATA, DATA_SET);return; }
#define SetErrorAndReturn(DATA,RETDATA)	{ErrorData( DATA, DATA_SET);return(RETDATA); }
#define ResetError()		ErrorData( ERROR_OK, DATA_SET )
#define PrintError()		ErrorData( ERROR_OK, DATA_PROCESS )
#define SetAndPrintError(DATA)	ErrorData( DATA, DATA_PROCESS )
#define PrintErrorAndReturn(RETDATA)	{ErrorData( ERROR_OK, DATA_PROCESS );return(RETDATA); }		
#define PrintErrorAndReturnVoid()	{ErrorData( ERROR_OK, DATA_PROCESS );return; }		
#define IfError   		if ( GetError() != ERROR_OK ) 
#define OnErrorReturn       	if ( GetError() != ERROR_OK ) return
#define IfErrorReturn     	if ( GetError() != ERROR_OK ) return

/* macros for defining the type of the error,
   Attention ! messages and fatalerror are treated in same way !! */

#define     WARNING( A )        ( (A) | IS_WARNING )
#define     ERROR( A )      	( (A) | IS_ERROR )
#define     MESSAGE( A )        ( (A) | IS_MESSAGE )
#define     FATALERROR( A )     ( (A) | IS_FATALERROR )
#define     INTERNALERROR( A )  ( (A) | IS_INTERNALERROR )
#define     DEBUGMESSAGE( A )   ( (A) | IS_DEBUGMESSAGE ) 

/***************** external functions ************************************/

/* the one and only error function, use only with the above macros ! */
extern int	ErrorData(int errnumber, DataAdmin what);

#endif /* _INCL_ERRH */

/******************************************************************************
          [EOF] end of file err.h 
******************************************************************************/
