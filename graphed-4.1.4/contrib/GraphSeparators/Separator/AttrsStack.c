/******************************************************************************/
/*                                                                            */
/*    AttrsStack.c                                                            */
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

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "AttrsStack.h"

/******************************************************************************/
/*  Implementation                                                            */
/******************************************************************************/

/******************************************************************************/
/*    InitAStack                                                              */
/*----------------------------------------------------------------------------*/
/*  Creates a new AStack (allocates memory) and initializes it.               */
/*  This function MUST be called before using an AStack!                      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  -----                                                     */
/*  Return value :  Pointer to the new created AStack structure.              */
/******************************************************************************/
Global AStackPtr  InitAStack  (void)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  AStackPtr  NewAStack = (AStackPtr) malloc (sizeof (AStack));
  
  /****************************************************************************/
  /*  Insert default values                                                   */
  /****************************************************************************/

  NewAStack->Top        = NULL;
  NewAStack->NoElements = 0;

  return  NewAStack;

}  /* End of InitAStack */

/******************************************************************************/
/*    EmptyAStack                                                             */
/*----------------------------------------------------------------------------*/
/*  Frees the memory allocated for the members of AStack.                     */
/*  The memory allocated for the Attributes entry is NOT freed!               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAStack   Stack to remove.                             */
/******************************************************************************/
Global void  EmptyAStack  (AStackPtr currAStack)
{
  /****************************************************************************/
  /*  Remove the remaining elementsof AStack.                                 */
  /****************************************************************************/

  while (ASTACK_NO_ELEMENTS (currAStack) != 0)
  {
    PopAStack (currAStack);
  }  /* endwhile */
    
}  /* End of EmptyAStack */

/******************************************************************************/
/*    RemoveAStack                                                            */
/*----------------------------------------------------------------------------*/
/*  Frees the memory allocated for AStack an all its members.                 */
/*  The memory allocated for the Attributes entry is NOT freed!               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAStack   Stack to remove.                             */
/******************************************************************************/
Global void  RemoveAStack  (AStackPtr currAStack)
{
  /****************************************************************************/
  /*  Remove the remaining elements of the AStack.                            */
  /****************************************************************************/

  EmptyAStack (currAStack);
  free (currAStack);
    
}  /* End of RemoveAStack */

/******************************************************************************/
/*    PushAStack                                                              */
/*----------------------------------------------------------------------------*/
/*  Puts the specified attribute as top element onto the stack.               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAStack     AStack to put NewAttribute on.             */
/*                  NewAttribute   Attribute to be put into the stack.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  PushAStack  (AStackPtr currAStack, Attributes NewAttribute)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  AStackEntryPtr  NewAStackEntry = (AStackEntryPtr)malloc(sizeof(AStackEntry));
  
  /****************************************************************************/
  /*  Put the attribute into the new entry and make it the new tail.          */
  /****************************************************************************/

  NewAStackEntry->attrs  = NewAttribute;

  if (!ASTACK_IS_EMPTY (currAStack))
  {
    NewAStackEntry->Next   = currAStack->Top;
    currAStack->Top        = NewAStackEntry;
  }
  else
  {
    currAStack->Top = NewAStackEntry;
  }  /* endif */

  ASTACK_NO_ELEMENTS(currAStack)++;
  
}  /* End of PushAStack */

/******************************************************************************/
/*    PopAStack                                                               */
/*----------------------------------------------------------------------------*/
/*  Removes the top element of the stack and returns its attributes.          */
/*  The stack MUST contain at least one element.                              */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currAStack    AStack with at least one element.           */
/*  Return value :  Attribute of the top element on the stack.                */
/******************************************************************************/
Global Attributes  PopAStack  (AStackPtr currAStack)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  AStackEntryPtr  TopAStackEntry = currAStack->Top;
  Attributes      TopAttrs;

  /****************************************************************************/
  /*  Remove the top entry of the stack and return its attributes             */
  /****************************************************************************/

  TopAttrs = TopAStackEntry->attrs;
  currAStack->Top = TopAStackEntry->Next;
  free (TopAStackEntry);
  ASTACK_NO_ELEMENTS(currAStack)--;

  return TopAttrs;
  
}  /* End of PopAStack */

/******************************************************************************/
/*  End of  AttrsStack.c                                                      */
/******************************************************************************/
