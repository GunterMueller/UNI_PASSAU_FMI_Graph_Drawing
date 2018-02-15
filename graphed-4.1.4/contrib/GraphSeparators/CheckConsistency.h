/******************************************************************************/
/*                                                                            */
/*    CheckConsistency.h                                                      */
/*                                                                            */
/******************************************************************************/
/*                                                                            */
/*  Contains the functions for the neccessary consistncy checks of algorithm  */
/*  and parameter combination as well as conditions on the graph.             */
/*                                                                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  31.08.1994                                                    */
/*  Modified :  31.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  CHECK_CONSISTENCY
#define  CHECK_CONSISTENCY

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <Separator.h>

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global bool  GraphIsConsistent  ();
#endif
  
#ifdef  ANSI_HEADERS_ON
Global bool  GraphIsConsistent  (GSAlgInfoPtr   algInfo,
                                 GSAlgSelect    algSelect,
                                 bool           compaction);
#endif

#endif
  
/******************************************************************************/
/*  End of  CheckConsistency.h                                                */
/******************************************************************************/
