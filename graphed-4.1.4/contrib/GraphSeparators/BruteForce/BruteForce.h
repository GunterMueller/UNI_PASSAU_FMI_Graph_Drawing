/******************************************************************************/
/*                                                                            */
/*    BruteForce.h                                                            */
/*                                                                            */
/******************************************************************************/
/*  Header-file for the naive edge-separator algorithm.                       */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  13.02.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.2         Name change                                       */
/*              1.1         New interface (AlgorithmInfo)                     */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef BRUTE_FORCE_INC
#define BRUTE_FORCE_INC

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <Separator.h>

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

struct  _Component
{
  int    Size;        /* Size of the component of the graph                   */
  Snode  startNode;   /* Node in the component - used for expansion           */
};
typedef  struct _Component  Component;
typedef  Component         *ComponentPtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  CREATE_COMPONENT_ENTRY(c)  (make_attr(ATTR_DATA, (char *)(c)))
#define  GET_COMPONENT_ENTRY(c)     (attr_data_of_type((c), ComponentPtr))

#define  COMPONENT_SIZE(c)    (attr_data_of_type((c), ComponentPtr)->Size)
#define  COMPONENT_START(c)   (attr_data_of_type((c), ComponentPtr)->startNode)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global void  BruteForce  (/*algInfo*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global void  BruteForce  (GSAlgInfoPtr  algInfo);
#endif

#endif

/******************************************************************************/
/*  End of  BruteForce.h                                                      */
/******************************************************************************/
