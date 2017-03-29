/******************************************************************************/
/*                                                                            */
/*    AttrsStack.h                                                            */
/*                                                                            */
/******************************************************************************/
/*                                                                            */
/*  Implementation of stacks using Sgraph attributes as entries.              */
/*                                                                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  19.04.1994                                                    */
/*  Modified :  ??.??.????                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  ATTRS_STACK
#define  ATTRS_STACK

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#ifndef SGRAPH_INLCUDED
#include <sgraph/std.h>
#endif

#ifdef  NEXT_VERSION
#include <stdlib.h>
#endif

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

struct _AStackEntry
{
  Attributes            attrs;  /* Sgraph Attributes union.                   */ 
  struct _AStackEntry  *Next;   /* Next element on the stack.                 */
};
typedef  struct _AStackEntry   AStackEntry;
typedef  AStackEntry          *AStackEntryPtr;

struct _AStack
{
  AStackEntryPtr  Top;         /* Top element of the stack.                   */
  int             NoElements;  /* Number of elements currently on the stack.  */
};
typedef  struct _AStack   AStack;
typedef  AStack          *AStackPtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  ASTACK_IS_EMPTY(q)       ((q)->NoElements == 0)
#define  ASTACK_NO_ELEMENTS(q)    ((q)->NoElements)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global AStackPtr   InitAStack    ();
Global void        EmptyAStack   (/*currAStack*/);
Global void        RemoveAStack  (/*currAStack*/);
Global void        PushAStack    (/*currAStack, NewAttribute*/);
Global Attributes  PopAStack     (/*currAStack*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global AStackPtr   InitAStack    (void);
Global void        EmptyAStack   (AStackPtr  currAStack);
Global void        RemoveAStack  (AStackPtr  currAStack);
Global void        PushAStack    (AStackPtr   currAStack, 
                                  Attributes  NewAttribute);
Global Attributes  PopAStack     (AStackPtr  currAStack);
#endif

#endif

/******************************************************************************/
/*  End of  AttrsStack.h                                                      */
/******************************************************************************/
