#ifndef _INCL_ERRORH
#define _INCL_ERRORH 		/* do not include twice */

/*******************************************************************
SEARCH STRATEGIES ON GRAPHS 

File    : error.h
of      : 93/02/17  05:04:01
Version : 1.2 
Author  : Schweikardt Andreas
RCS     : $RCSfile: graph.h $$Date: 92/01/18 11:11:31 $$Revision: 1.2 $

Layer   : 
Modul   : error

OBJECTS	    :

PORTABILITY 
  language  : C
  OS        : SUN-OS
  GUI       :
  other     :

BUGS :	only number upto 4000 allowed for each error type

**************************************************************************/

#include    <search/err.h>

/* you have six types of errormessages */
/* MESSAGE
   WARNING
   ERROR
   FATALERROR
   and
   DEBUGMESSAGE
   INTERNALERROR
   for debugging or some bugs not detected until now
*/


#define NO_ERROR	ERROR_OK
#define ERROR_NONE	ERROR_OK

/********** MESSAGES *************************************************/

/* USER: insert your message number here, use macro MESSAGE to define
   the type */

/********** WARNINGS *************************************************/

/* graph test warnings, no search possible	#100-149 */
#define WARNING_EMPTY_GRAPH     		WARNING( 100 )
#define WARNING_DIGRAPH         		WARNING( 101 )
#define WARNING_NOT_CONNECTED_GRAPH 		WARNING( 102 )


/* manual mode warnings 			#150-199 */

/* user makes some wrong input 			#200-299 */



/* USER: insert your warning number here, use macro WARNING to define
   the type */

/********** ERRORS ***************************************************/

/* tests for trees 				#100-149*/
#define ERROR_NOT_A_TREE        		ERROR( 100 )
#define ERROR_NOT_A_COMPLETE_GRAPH		ERROR( 101 )
#define ERROR_NOT_A_GRID			ERROR( 102 )
#define ERROR_NOT_A_KNM				ERROR( 103 )

/* wrong movements 				#150-199 */
#define ERROR_MOVETO_IN_NS_METHOD   		ERROR( 150 )
#define ERROR_MOVETO_FROM_WRONG_SOURCENODE  	ERROR( 151 )
#define ERROR_MOVETO_NULL_NODE			ERROR( 152 )
#define ERROR_SETON_NULL_NODE			ERROR( 153 )

/* USER: insert your error number here, use macro ERROR to define
   the type */

/********** FATAL ERORS **********************************************/


/* USER: insert your fatal error number here, use macro FATALERROR to 
  define the type */

/********** DEBUG MESSAGES *******************************************/

/********** INTERNAL ERRORS ******************************************/


/* status information negative 			#100-149 */
#define IERROR_MAX_SEARCHERS_LT_ZERO 		INTERNALERROR( 100 )
#define IERROR_SEARCHERS_LT_ZERO 		INTERNALERROR( 101 )
#define IERROR_STEPS_LT_ZERO 			INTERNALERROR( 102 )
#define IERROR_DECREMENT_ZERO			INTERNALERROR( 103 )

/* internal program control 			#200-299 */
/* default in switch, but only defined values required */
#define IERROR_SWITCH_NO_CASE 			INTERNALERROR( 200 )
/* empty list or pointer, but they have to be defined */
#define IERROR_NULL_PTR				INTERNALERROR( 201 )
#define IERROR_FREE_NULL_PTR			INTERNALERROR( 202 )
#define IERROR_NO_MEM				INTERNALERROR( 203 )

/* tape control					#300-349 */
#define IERROR_END_OF_TAPE			INTERNALERROR( 300 )




#endif /* _INCL_ERRORH */

/******************************************************************************
          [EOF] end of file error.h 
******************************************************************************/
