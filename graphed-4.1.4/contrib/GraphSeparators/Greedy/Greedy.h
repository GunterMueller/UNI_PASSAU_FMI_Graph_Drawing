/******************************************************************************/
/*                                                                            */
/*    Greedy.h                                                                */
/*                                                                            */
/******************************************************************************/
/*                                                                            */
/*  Implementation of a greedy startegy to find an alpha edge separator.      */
/*                                                                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  25.04.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              2.1         Name change.                                      */
/*              2.0         Divided into Hill-Climbing and Greedy.            */
/*              1.2         Interface changed (algInfo).                      */
/*              1.1         Randomized initial node set assignment.           */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  GREEDY_INC
#define  GREEDY_INC

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <Separator.h>

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global void  Naive   (/*algInfo, validSep*/);
Global void  Greedy  (/*algInfo, validSep*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global void  Naive   (GSAlgInfoPtr  algInfo, bool  validSep);
Global void  Greedy  (GSAlgInfoPtr  algInfo, bool  validSep);
#endif

#endif

/******************************************************************************/
/*  End of Greedy.h                                                           */
/******************************************************************************/
