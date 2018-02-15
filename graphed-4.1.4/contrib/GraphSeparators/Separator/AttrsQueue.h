/******************************************************************************/
/*                                                                            */
/*    AttrsQueue.h                                                            */
/*                                                                            */
/******************************************************************************/
/*                                                                            */
/*  Implementation of queues using Sgraph attributes as entries.              */
/*                                                                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  19.04.1994                                                    */
/*  Modified :  ??.??.????                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  ATTRS_QUEUE
#define  ATTRS_QUEUE

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

struct _AQueueEntry
{
  Attributes            attrs;  /* Sgraph Attributes union.                   */ 
  struct _AQueueEntry  *Next;   /* Next element in the queue.                 */
};
typedef  struct _AQueueEntry   AQueueEntry;
typedef  AQueueEntry          *AQueueEntryPtr;

struct _AQueue
{
  AQueueEntryPtr  First;       /* First element of the queue.                 */
  AQueueEntryPtr  Last;        /* Last element of the queue.                  */
  int             NoElements;  /* Number of elements currently in the queue.  */
};
typedef  struct _AQueue   AQueue;
typedef  AQueue          *AQueuePtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  AQUEUE_IS_EMPTY(q)       ((q)->NoElements == 0)
#define  AQUEUE_NO_ELEMENTS(q)    ((q)->NoElements)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global AQueuePtr   InitAQueue    ();
Global void        EmptyAQueue   (/*currAQueue*/);
Global void        RemoveAQueue  (/*currAQueue*/);
Global void        EnAQueue      (/*currAQueue, NewAttribute*/);
Global Attributes  DeAQueue      (/*currAQueue*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global AQueuePtr   InitAQueue    (void);
Global void        EmptyAQueue   (AQueuePtr  currAQueue);
Global void        RemoveAQueue  (AQueuePtr  currAQueue);
Global void        EnAQueue      (AQueuePtr   currAQueue, 
                                  Attributes  NewAttribute);
Global Attributes  DeAQueue      (AQueuePtr  currAQueue);
#endif

#endif

/******************************************************************************/
/*  End of  AttrsQueue.h                                                      */
/******************************************************************************/
