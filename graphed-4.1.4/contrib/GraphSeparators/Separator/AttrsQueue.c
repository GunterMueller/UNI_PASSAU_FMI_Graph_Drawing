/******************************************************************************/
/*                                                                            */
/*    AttrsQueue.c                                                            */
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

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "AttrsQueue.h"

/******************************************************************************/
/*  Implementation                                                            */
/******************************************************************************/

/******************************************************************************/
/*    InitAQueue                                                              */
/*----------------------------------------------------------------------------*/
/*  Creates a new AQueue (allocates memory) and initializes it.               */
/*  This function MUST be called before using an AQueue!                      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  -----                                                     */
/*  Return value :  Pointer to the new created AQueue structure.              */
/******************************************************************************/
Global AQueuePtr  InitAQueue  (void)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  AQueuePtr  NewAQueue = (AQueuePtr) malloc (sizeof (AQueue));
  
  /****************************************************************************/
  /*  Insert default values                                                   */
  /****************************************************************************/

  NewAQueue->First      = NULL;
  NewAQueue->Last       = NULL;
  NewAQueue->NoElements = 0;

  return  NewAQueue;

}  /* End of InitAQueue */

/******************************************************************************/
/*    EmptyAQueue                                                             */
/*----------------------------------------------------------------------------*/
/*  Frees the memory allocated for the members of AQueue .                    */
/*  The memory allocated for the Attributes entry is NOT freed!               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAQueue   Queue to remove.                             */
/******************************************************************************/
Global void  EmptyAQueue  (AQueuePtr currAQueue)
{
  /****************************************************************************/
  /*  Remove the remaining elements and then the AQueue itself.               */
  /****************************************************************************/

  while (AQUEUE_NO_ELEMENTS (currAQueue) != 0)
  {
    DeAQueue (currAQueue);
  }  /* endwhile */

}  /* End of EmptyAQueue */

/******************************************************************************/
/*    RemoveAQueue                                                            */
/*----------------------------------------------------------------------------*/
/*  Frees the memory allocated for AQueue an all its members.                 */
/*  The memory allocated for the Attributes entry is NOT freed!               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAQueue   Queue to remove.                             */
/******************************************************************************/
Global void  RemoveAQueue  (AQueuePtr currAQueue)
{
  /****************************************************************************/
  /*  Remove the remaining elements and then the AQueue itself.               */
  /****************************************************************************/

  EmptyAQueue (currAQueue);
  free (currAQueue);
    
}  /* End of RemoveAQueue */

/******************************************************************************/
/*    EnAQueue                                                                */
/*----------------------------------------------------------------------------*/
/*  Puts the specified attribute as last element into the queue.              */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAQueue     AQueue to put NewAttribute on.             */
/*                  NewAttribute   Attribute to be put into the queue.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  EnAQueue  (AQueuePtr currAQueue, Attributes NewAttribute)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  AQueueEntryPtr  NewAQueueEntry = (AQueueEntryPtr)malloc(sizeof(AQueueEntry));
  
  /****************************************************************************/
  /*  Put the attribute into the new entry and make it the new tail.          */
  /****************************************************************************/

  NewAQueueEntry->attrs  = NewAttribute;

  if (!AQUEUE_IS_EMPTY (currAQueue))
  {
    currAQueue->Last->Next = NewAQueueEntry;
    currAQueue->Last       = NewAQueueEntry;
  }
  else
  {
    currAQueue->First = NewAQueueEntry;
    currAQueue->Last  = NewAQueueEntry;
  }  /* endif */

  AQUEUE_NO_ELEMENTS(currAQueue)++;
  
}  /* End of EnAQueue */

/******************************************************************************/
/*    DeAQueue                                                                */
/*----------------------------------------------------------------------------*/
/*  Removes the first element of the queue and returns its attributes.        */
/*  The queue MUST contain at least one element.                              */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAQueue    AQueue with at least one element.           */
/*  Return value :  Attribute of the first element of the queue.              */
/******************************************************************************/
Global Attributes  DeAQueue  (AQueuePtr currAQueue)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  AQueueEntryPtr  FirstAQueueEntry = currAQueue->First;
  Attributes      FirstAttrs;
  
  /****************************************************************************/
  /*  Remove the first entry of the queue and return its attributes           */
  /****************************************************************************/

  FirstAttrs = FirstAQueueEntry->attrs;
  currAQueue->First = FirstAQueueEntry->Next;
  free (FirstAQueueEntry);
  AQUEUE_NO_ELEMENTS(currAQueue)--;
  
  if (AQUEUE_IS_EMPTY(currAQueue))
  {
    currAQueue->First      = NULL;
    currAQueue->Last       = NULL;
    currAQueue->NoElements = 0;
  }  /* endif */

  return FirstAttrs;
  
}  /* End of DeAQueue */

/******************************************************************************/
/*  End of  AttrsQueue.c                                                      */
/******************************************************************************/
