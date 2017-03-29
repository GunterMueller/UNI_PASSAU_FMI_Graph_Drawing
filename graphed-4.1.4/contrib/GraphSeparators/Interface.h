/******************************************************************************/
/*                                                                            */
/*    Interface.h                                                             */
/*                                                                            */
/******************************************************************************/
/*  Standard routines for calling the algorithms th compute graph-separators. */
/*  System independend through conditional compilation.                       */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  12.03.1994                                                    */
/*  Modified :  13.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              2.1         Fast labeling.                                    */
/*              2.0         Openwindows interface.                            */
/*              1.1         Support for NeXT and SUN-version.                 */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  INTERFACE_MISC
#define  INTERFACE_MISC

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <Separator.h>
#include <BruteForce.h>
#include <Plaisted.h>
#include <Kernighan-Lin.h>
#include <Fiduccia-Mattheyses.h>
#include <Greedy.h>
#include <IncreaseDegree.h>
#include <CheckConsistency.h>

#include <string.h>

#ifdef SUN_VERSION
#include <sgraph/graphed.h>
#else
/*
#include <SgraphInterface.h>
*/
#endif

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define SEPARATOR_LABEL  strsave("S")
#define LEFT_LABEL       strsave("A")
#define RIGHT_LABEL      strsave("B")
#define NO_LABEL         strsave("")

#ifdef  NEXT_VERSION
#define  message(m)  printf(m)
#endif

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef ANSI_HEADERS_OFF
Global void  ComputeSeparator          (/*algInfo, algSelect, compaction*/);
Global void  InvokeSeparatorAlgorithm  (/*algInfo, algSelect*/);
Global void  SetLabels                 (/* ... */);
Global void  ClearAndSetLabels         (/*currGraph*/);
Global void  SlowClearAndSetLabels     (/*Sgraph  currGraph*/);
#endif

#ifdef ANSI_HEADERS_ON
Global void  ComputeSeparator          (GSAlgInfoPtr  algInfo, 
                                        GSAlgSelect   algSelect,
                                        bool          compaction);
Global void  InvokeSeparatorAlgorithm  (GSAlgInfoPtr  algInfo, 
                                        GSAlgSelect   algSelect);
/*
Global void  ClearLabels               (Sgraph  currGraph);
now local */
Global void  SetLabels                 (Slist  SeparatorSet, 
                                        Slist  LeftNodeSet, 
                                        Slist  RightNodeSet);
Global void  ClearAndSetLabels         (Sgraph  currGraph);
Global void  SlowClearAndSetLabels     (Sgraph  currGraph);
#endif

#endif

/******************************************************************************/
/*  End of  Interface.h                                                       */
/******************************************************************************/
