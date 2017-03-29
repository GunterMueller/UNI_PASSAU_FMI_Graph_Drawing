/******************************************************************************/
/*                                                                            */
/*    Fiduccia-Mattheyses.c                                                   */
/*                                                                            */
/******************************************************************************/
/*  Heuristic to compute an alpha-edge separator according to Fiduccia and    */
/*  Mattheyses.                                                               */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  16.06.1994                                                    */
/*  Modified :  23.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "Fiduccia-Mattheyses.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local bool           ImprovePartition        (/*currPassInfo, maxSize*/);
Local bool           GetNodeToMove           (/*..*/);
Local void           MoveNodeToMove          (/*..*/);
Local void           LockNodeToMove          (/*..*/);
Local void           DecreaseMaxGain         (/*currBucket*/);
Local void           UpdateBuckets           (/*currPassInfo, currNode*/);
Local void           UpdateBucketForNode     (/*..*/);
Local BucketCellPtr  GetCell                 (/*currDValue, currBucket*/);
Local Slist          ComputeMaxPrefix        (/*moveLog*/);
Local void           PerformNodeMoves        (/*..*/);
Local PassInfoPtr    CreatePassInfo          (/*currGraph*/);
Local void           ComputeMaxDegreeWeight  (/*..*/);
Local void           CreateBuckets           (/*currBucket, maxIndex*/);
Local void           InitialPassInfo         (/*newPassInfo, currGraph*/);
Local void           MoveIntoBucketCell      (/*..*/);
Local void           RemovePassInfo          (/*currPassInfo*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local bool           ImprovePartition        (PassInfoPtr  currPassInfo, 
                                              int          maxSize);
Local bool           GetNodeToMove           (PassInfoPtr   currPassInfo, 
                                              int           maxSize, 
                                              Slist        *moveNode, 
                                              NodeSet      *moveSet);
Local void           MoveNodeToMove          (PassInfoPtr   currPassInfo, 
                                              Slist         moveNode, 
                                              NodeSet       moveSet, 
                                              Slist        *moveLog);
Local void           LockNodeToMove          (PassInfoPtr  currPassInfo, 
                                              Slist        lockNode, 
                                              NodeSet      lockSet);
Local void           DecreaseMaxGain         (BucketPtr  currBucket);
Local void           UpdateBuckets           (PassInfoPtr  currPassInfo, 
                                              Snode        currNode);
Local void           UpdateBucketForNode     (Snode      currNode, 
                                              Sedge      currEdge, 
                                              Snode      nextNode, 
                                              BucketPtr  nextBucket);
Local BucketCellPtr  GetCell                 (int        currDValue, 
                                              BucketPtr  currBucket);
Local Slist          ComputeMaxPrefix        (Slist  moveLog);
Local void           PerformNodeMoves        (PassInfoPtr  currPassInfo, 
                                              Slist        moveLog, 
                                              Slist        maxGain);
Local PassInfoPtr    CreatePassInfo          (Sgraph  currGraph);
Local void           ComputeMaxDegreeWeight  (Sgraph   currGraph, 
                                              int     *maxDegree, 
                                              int     *maxWeight);
Local void           CreateBuckets           (BucketPtr  currBucket, 
                                              int        maxIndex);
Local void           InitialPassInfo         (PassInfoPtr  newPassInfo, 
                                              Sgraph       currGraph);
Local void           MoveIntoBucketCell      (BucketPtr  currBucket, 
                                              int        currDValue, 
                                              Snode      currNode);
Local void           RemovePassInfo          (PassInfoPtr  currPassInfo);
#endif

/******************************************************************************/
/*  Implement the heuristic                                                   */
/******************************************************************************/

/******************************************************************************/
/*    FiducciaMattheyses                                                      */
/*----------------------------------------------------------------------------*/
/*  Computes an alpha edge-separator according to the Fiduccia - Mattheyses   */
/*  heuristic.                                                                */
/*  The separators computed by this procedure are bisections. To compute      */
/*  separators for alpha > 0.5, the graph is partitioned into two node sets   */
/*  fulfilling the alpha condition. Then the smaller set is filled up with    */
/*  "dummy nodes" having no edges adjacent to them, which are removed after   */
/*  the (locally) optimal separator was computed.                             */
/*  The actual computation of the separator is the repetition of two steps:   */
/*  1) For each node compute the difference between external and internal     */
/*     cost in the current partition and initialize the data structure.       */
/*  2) Improve the current partion.                                           */
/*  One improve step is:                                                      */
/*  1) Compute a sequence of node movements yielding a positive gain using    */
/*     a efficient data structure.                                            */
/*  2) If moving those nodes would yield a positive gain, do it!              */
/*  Calling this function with validSep == TRUE and valid alpha-separator     */
/*  data in the node sets allows the optimization of a separator by the       */
/*  Fiduccia-Mattheyses heuristic or the randomization of this heuristic.     */
/*  The attributes of the elements of the specified graph must be initalized  */
/*  before calling this function and they MUST be removed afterwards.         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo    Data to execute algorithm (graph, alpha...).   */
/*                  validSep   TRUE, if A, B and S are a valid Separator      */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  FiducciaMattheyses  (GSAlgInfoPtr algInfo, bool validSep)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  PassInfoPtr  currPassInfo = CreatePassInfo (GRAPH (algInfo));
  int          balancedSize = NUM_GRAPH_NODES (GRAPH (algInfo)) % 2 ?
                                NUM_GRAPH_NODES (GRAPH (algInfo)) / 2      :
                                (NUM_GRAPH_NODES (GRAPH (algInfo)) + 1) / 2;
  int          bigSize      = 0;
  int          smallSize    = 0;
  
  /****************************************************************************/
  /*  Compute an initial node set partition if none was given                 */
  /****************************************************************************/

  if (!validSep)
  {
    RandomInitialPartition (GRAPH (algInfo), &A_SET (algInfo), &B_SET (algInfo), 
                            balancedSize);
  }  /* endif */
  
  ComputeNodeSetSizes (NUM_GRAPH_NODES (GRAPH (algInfo)), 
                       &smallSize, 
                       &bigSize,
                       ALPHA (algInfo));

  /****************************************************************************/
  /*  Handle the special case of graphs up to 2 nodes.                        */
  /****************************************************************************/
  
  if ((smallSize > 1) || (bigSize > 1))
  {
    /**************************************************************************/
    /*  Improve the node set partition by moving nodes while improving        */
    /**************************************************************************/
    
    currPassInfo->LockedA = A_SET (algInfo);
    currPassInfo->LockedB = B_SET (algInfo);
    
    do
    {
      InitialPassInfo (currPassInfo, GRAPH (algInfo));
      
      /* Stepping to insert here ... */
  
#ifdef ANIMATION_ON
      AnimateAlgorithm (algInfo);
#endif
  
    }  while (ImprovePartition (currPassInfo, bigSize));  /* enddo */

    /**************************************************************************/
    /*  Get the partition from the last pass                                  */
    /**************************************************************************/
    
      A_SET (algInfo) = currPassInfo->LockedA;
      B_SET (algInfo) = currPassInfo->LockedB;
  
  }  /* endif */
  
  /****************************************************************************/
  /*  Compute the separator edges                                             */
  /****************************************************************************/
  
  ComputeSeparatorEdges (GRAPH (algInfo), &SEP_SET (algInfo));
  
  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  RemovePassInfo (currPassInfo);
  
}  /* End of FiducciaMattheyses */

/******************************************************************************/
/*    ImprovePartition                                                        */
/*----------------------------------------------------------------------------*/
/*  Improves the given partition of the graph.                                */
/*  This is one pass in the Fiduccia-Mattheyses heuristic:                    */
/*  Computing a sequence of node exchanges which improves the given partition */
/*  and then exchanging them.                                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currPassInfo   Initialized pass info structure.           */
/*                  maxSize        Maximum size of the bigger node set.       */
/*  Return value :  TRUE, if improving the partition was successful           */
/******************************************************************************/
Local bool  ImprovePartition  (PassInfoPtr currPassInfo, int maxSize)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Slist    moveLog  = empty_slist;
  Slist    moveNode = empty_slist;
  NodeSet  moveSet  = NodeNone;
  Slist    maxGain  = empty_slist;
  bool     success  = TRUE;
  
  /****************************************************************************/
  /*  Compute the list (log) of tentative node moves.                         */
  /****************************************************************************/
  
  while ((currPassInfo->BucketA->MaxGain != NULL) || 
         (currPassInfo->BucketB->MaxGain != NULL))
  {
    if (GetNodeToMove  (currPassInfo, maxSize, &moveNode, &moveSet))
    {
      MoveNodeToMove (currPassInfo, moveNode, moveSet, &moveLog);
    }
    else
    {
      LockNodeToMove (currPassInfo, moveNode, moveSet);
    }  /*endif */
  }  /* endwhile */

  /****************************************************************************/
  /*  Compute the prefix of moves in the log yielding the highest gain.       */
  /****************************************************************************/
  
  maxGain = ComputeMaxPrefix (moveLog);
  success = (maxGain != empty_slist);

  /****************************************************************************/
  /*  Perform the neccessary moves and free log space.                        */
  /****************************************************************************/
  
  PerformNodeMoves (currPassInfo, moveLog, maxGain);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (moveLog);
  return success;
  
}  /* End of ImprovePartition */

/******************************************************************************/
/*    GetNodeToMove                                                           */
/*----------------------------------------------------------------------------*/
/*  Compute the node set entry which is next to be moved.                     */
/*  If both buckets contain movable nodes, the one with the higher gain is    */
/*  choosen, in case of a tie, improve the balance - choose the node in the   */
/*  bigger node set.                                                          */
/*  If one node set is full, but both buckets contain movable node, move one  */
/*  node from the full side to the less filled side.                          */
/*  If one bucket is empty, choose a node from the other bucket. If the node  */
/*  set of the empty bucket is full, tell the calling function to lock the    */
/*  choosen node in its bucket.                                               */
/*  At least on bucket MUST contain an unlocked node.                         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currPassInfo   Current state of the pass.                 */
/*                  maxSize        Maximum size of a node set.                */
/*                  moveNode       Node set entry to move.                    */
/*                  moveSet        Node set of the node to move.              */
/*  Return value :  FALSE, if moveNode should be locked in moveSet.           */
/******************************************************************************/
Local bool  GetNodeToMove  (PassInfoPtr currPassInfo, int maxSize, Slist *moveNode, NodeSet *moveSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  BucketCellPtr  maxA = currPassInfo->BucketA->MaxGain;
  BucketCellPtr  maxB = currPassInfo->BucketB->MaxGain;
  
  /****************************************************************************/
  /*  Compute the node to move according to the current state and balance     */
  /****************************************************************************/

  if ((maxA != NULL) && (maxB != NULL))
  {
    if ((currPassInfo->ASize != maxSize) && (currPassInfo->BSize != maxSize))
    {
      if (D_VALUE (GET_NODESET_ENTRY (maxA->UnlockedNodes)) > 
          D_VALUE (GET_NODESET_ENTRY (maxB->UnlockedNodes)))
      {
        *moveNode = maxA->UnlockedNodes;
        *moveSet  = LeftSet;
      }
      else if (D_VALUE (GET_NODESET_ENTRY (maxB->UnlockedNodes)) > 
               D_VALUE (GET_NODESET_ENTRY (maxA->UnlockedNodes)))
      {
        *moveNode = maxB->UnlockedNodes;
        *moveSet  = RightSet;
      }
      else if (currPassInfo->ASize > currPassInfo->BSize)
      {
        *moveNode = maxA->UnlockedNodes;
        *moveSet  = LeftSet;
      }
      else
      {
        *moveNode = maxB->UnlockedNodes;
        *moveSet  = RightSet;
      }  /* endif */
    }
    else if (currPassInfo->ASize == maxSize)
    {
      *moveNode = maxA->UnlockedNodes;
      *moveSet  = LeftSet;
    }
    else
    {
      *moveNode = maxB->UnlockedNodes;
      *moveSet  = RightSet;
    }  /* endif */
    
    return TRUE;
  } 
  else if (maxA != NULL)
  {
    *moveNode = maxA->UnlockedNodes;
    *moveSet  = LeftSet;
    return (currPassInfo->BSize < maxSize);
  } 
  else
  {
    *moveNode = maxB->UnlockedNodes;
    *moveSet  = RightSet;
    return (currPassInfo->ASize < maxSize);
  }  /* endif */
  
}  /* End of GetNodeToMove */

/******************************************************************************/
/*    MoveNodeToMove                                                          */
/*----------------------------------------------------------------------------*/
/*  Locks the specified node, removes it from its bucket cell and update the  */
/*  D-values of all adjacent nodes while maintaining the bucket structure.    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currPassInfo   Current state of the pass.                 */
/*                  moveNode       Node set entry to move.                    */
/*                  moveSet        Node set of the node to move.              */
/*                  moveLog        Store the move into the log.               */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  MoveNodeToMove  (PassInfoPtr currPassInfo, Slist moveNode, NodeSet moveSet, Slist *moveLog)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode            currNode    = GET_NODESET_ENTRY (moveNode);
  MoveLogEntryPtr  newLogEntry = (MoveLogEntryPtr) malloc(sizeof(MoveLogEntry));
  BucketPtr        currBucket  = moveSet == LeftSet ? currPassInfo->BucketA :
                                                      currPassInfo->BucketB;
  
  /****************************************************************************/
  /*  Enter the move into the log.                                            */
  /****************************************************************************/
  
  newLogEntry->MovedNode = currNode;
  newLogEntry->OldSet    = moveSet;
  newLogEntry->Gain      = (int) D_VALUE (currNode);
  *moveLog = add_immediately_to_slist (*moveLog, CREATE_LOG_ENTRY(newLogEntry));

  /****************************************************************************/
  /*  Increment the target size.                                              */
  /****************************************************************************/
  
  if (moveSet == LeftSet)
  {
    currPassInfo->BSize++;
    currPassInfo->ASize--;
  }
  else
  {
    currPassInfo->ASize++;
    currPassInfo->BSize--;
  }  /* endif */
  
  /****************************************************************************/
  /*  Remove the node to move from its bucket and mark it locked              */
  /****************************************************************************/
  
  IS_NODE_VISITED (currNode)         = TRUE;
  PART_OF_NODE_SET (currNode)        = moveSet == LeftSet ? RightSet : LeftSet;
  currBucket->MaxGain->UnlockedNodes = 
    subtract_immediately_from_slist (currBucket->MaxGain->UnlockedNodes, 
                                     moveNode);

  /****************************************************************************/
  /*  Update the MaxGain entry if neccessary                                  */
  /****************************************************************************/
  
  if (currBucket->MaxGain->UnlockedNodes == empty_slist)
  {
    DecreaseMaxGain (currBucket);
  }  /* endif */

  /****************************************************************************/
  /*  Update the D-values of all adjacent nodes and the bucket structure.     */
  /****************************************************************************/
  
  UpdateBuckets (currPassInfo, currNode);

}  /* End of MoveNodeToMove */

/******************************************************************************/
/*    LockNodeToMove                                                          */
/*----------------------------------------------------------------------------*/
/*  Locks the specified node, removes it from its bucket cell and puts it     */
/*  into the locked list of its current bucket without changing the D-values. */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currPassInfo   Current state of the pass.                 */
/*                  lockNode       Node set entry to lock.                    */
/*                  lockSet        Node set of the node to lock.              */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  LockNodeToMove  (PassInfoPtr currPassInfo, Slist lockNode, NodeSet lockSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode      currNode    = GET_NODESET_ENTRY (lockNode);
  BucketPtr  currBucket  = lockSet == LeftSet ? currPassInfo->BucketA :
                                                currPassInfo->BucketB;
  
  /****************************************************************************/
  /*  Remove the node to move from its bucket and mark it locked              */
  /****************************************************************************/
  
  IS_NODE_VISITED (currNode)         = TRUE;
  currBucket->MaxGain->UnlockedNodes = 
    subtract_immediately_from_slist (currBucket->MaxGain->UnlockedNodes, 
                                     lockNode);

  /****************************************************************************/
  /*  Update the MaxGain entry if neccessary                                  */
  /****************************************************************************/
  
  if (currBucket->MaxGain->UnlockedNodes == empty_slist)
  {
    DecreaseMaxGain (currBucket);
  }  /* endif */

  /****************************************************************************/
  /*  Move the node into the lock list of its bucket.                         */
  /****************************************************************************/
  
  if (lockSet == LeftSet)
  {
    currPassInfo->LockedA = add_immediately_to_slist (currPassInfo->LockedA,
                              CREATE_NODESET_ENTRY (currNode));
    SET_ENTRY (currNode)  = currPassInfo->LockedA->pre;
    
    currPassInfo->ASize++;
    currPassInfo->BSize--;
  }
  else
  {
    currPassInfo->LockedB = add_immediately_to_slist (currPassInfo->LockedB,
                              CREATE_NODESET_ENTRY (currNode));
    SET_ENTRY (currNode)  = currPassInfo->LockedB->pre;
    
    currPassInfo->BSize++;
    currPassInfo->ASize--;
  }  /* endif */

}  /* End of LockNodeToMove */

/******************************************************************************/
/*    DecreaseMaxGain                                                         */
/*----------------------------------------------------------------------------*/
/*  The last entry in the MaxGain has been removed: The next-highest gain is  */
/*  lower than the current - search downwards.                                */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currBucket   Bucket with a too high MaxGain entry.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  DecreaseMaxGain  (BucketPtr currBucket)
{
  /****************************************************************************/
  /*  Search the next  non empty bucket cell with smaller D-value.            */
  /****************************************************************************/
  
  currBucket->MaxGain = currBucket->MaxGain->SuccCell;
  
  while (currBucket->MaxGain != NULL)
  {
    if (currBucket->MaxGain->UnlockedNodes != empty_slist)
    {
      return;
    }  /* endif */

    currBucket->MaxGain = currBucket->MaxGain->SuccCell;
    
  }  /* endwhile */
  
}  /* End of DecreaseMaxGain */

/******************************************************************************/
/*    UpdateBuckets                                                           */
/*----------------------------------------------------------------------------*/
/*  Update the bucket structure of all nodes adjacent to currNode, which      */
/*  havn't been visited - locked - yet.                                       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currPassInfo   Current stat information.                  */
/*                  currNode       Node which has been moved.                 */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  UpdateBuckets  (PassInfoPtr currPassInfo, Snode currNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge          currEdge   = NULL;
  Snode          nextNode   = NULL;
  BucketPtr      nextBucket = NULL;
  
  /****************************************************************************/
  /*  Test all adjacent nodes if they need to be updated                      */
  /****************************************************************************/
  
  for_sourcelist (currNode, currEdge)
  {
    nextNode   = NEXT_NODE (currNode, currEdge);
    
    if (!IS_NODE_VISITED (nextNode))
    {
      nextBucket = PART_OF_NODE_SET (nextNode) == LeftSet ? 
                     currPassInfo->BucketA : currPassInfo->BucketB;
      
      UpdateBucketForNode  (currNode, currEdge, nextNode, nextBucket);
      
    }  /* endif */
  }  end_for_sourcelist (currNode, currEdge);
  
  /* Handle the directed case                                                 */
  if (currNode->graph->directed)
  {
    for_targetlist (currNode, currEdge)
    {
      nextNode   = NEXT_NODE (currNode, currEdge);
      
      if (!IS_NODE_VISITED (nextNode))
      {
        nextBucket = PART_OF_NODE_SET (nextNode) == LeftSet ? 
                        currPassInfo->BucketA : currPassInfo->BucketB;
        
        UpdateBucketForNode  (currNode, currEdge, nextNode, nextBucket);
        
      }  /* endif */
    }  end_for_targetlist (currNode, currEdge);
  }  /* endif */
  
}  /* End of UpdateBuckets */

/******************************************************************************/
/*    UpdateBucketForNode                                                     */
/*----------------------------------------------------------------------------*/
/*  Updates the D-value for the node adajcent to the moved node and puts it   */
/*  into a possible new bucket cell. MaxGain of the bucket of the adjacent    */
/*  node can change in two ways:                                              */
/*  1) The old cell of nextNode becomes empty: Decrease MaxGain to the next   */
/*     non-empty cell.                                                        */
/*  2) The new cell has a higher D-Value: newCell is the new MaxGain.         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode     Node involved in the move operation.         */
/*                  currEdge     Edge between currNode and nextNode.          */
/*                  nextNode     Node adjacent to currNode needing update.    */
/*                  nextBucket   Bucket, where nextNode is in.                */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  UpdateBucketForNode  (Snode currNode, Sedge currEdge, Snode nextNode, BucketPtr nextBucket)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  BucketCellPtr  newCell    = NULL;
  BucketCellPtr  oldCell    = GetCell ((int) D_VALUE (nextNode), nextBucket);

  /****************************************************************************/
  /*  Update the D-value of the node adjacent to the moved node               */
  /****************************************************************************/

  if (PART_OF_NODE_SET (currNode) == PART_OF_NODE_SET (nextNode))
  {
    D_VALUE (nextNode) = D_VALUE (nextNode) - 2.0 * EDGE_WEIGHT (currEdge);
  }
  else
  {
    D_VALUE (nextNode) = D_VALUE (nextNode) + 2.0 * EDGE_WEIGHT (currEdge);
  }  /* endif */
  
  /****************************************************************************/
  /*  Move the next node to its new bucket cell.                              */
  /****************************************************************************/

  oldCell->UnlockedNodes = 
    subtract_immediately_from_slist (oldCell->UnlockedNodes,
                                     SET_ENTRY (nextNode));
  newCell                = GetCell ((int) D_VALUE (nextNode), nextBucket);
  newCell->UnlockedNodes = add_immediately_to_slist (newCell->UnlockedNodes,
                              CREATE_NODESET_ENTRY (nextNode));
  SET_ENTRY (nextNode)   = newCell->UnlockedNodes->pre;
                              
  /****************************************************************************/
  /*  Adjust the MaxGain pointer if neccessary.                               */
  /****************************************************************************/

  if (newCell->CellDValue > nextBucket->MaxGain->CellDValue)
  {
    nextBucket->MaxGain = newCell;
  }
  else if (nextBucket->MaxGain->UnlockedNodes == empty_slist)
  {
    DecreaseMaxGain (nextBucket);
  }  /* endif*/
  
}  /* End of UpdateBucketForNode */

/******************************************************************************/
/*    GetCell                                                                 */
/*----------------------------------------------------------------------------*/
/*  Compute the cell in the specified bucket, which has the requested D-value.*/
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currDValue   Get the cell with this D-Value.              */
/*                  currBucket   Search this bucket for the cell.             */
/*  Return value :  Cell in the bucket with currDValue.                       */
/******************************************************************************/
Local BucketCellPtr  GetCell  (int currDValue, BucketPtr currBucket)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  bool           found    = FALSE;
  bool           searchUp = (currDValue < 0);
  BucketCellPtr  currCell = searchUp ? currBucket->LastCell : 
                                       currBucket->FirstCell;
  
  /****************************************************************************/
  /*  Return the searched cell                                                */
  /****************************************************************************/
  
  while (!found)
  {
    if (currCell->CellDValue == currDValue)
    {
      found = TRUE;
    }
    else
    {
      currCell = searchUp ? currCell->PrevCell : currCell->SuccCell;
    }  /* endif */
  }  /* endwhile */
  
  return currCell;

}  /* End of GetCell */

/******************************************************************************/
/*    ComputeMaxPrefix                                                        */
/*----------------------------------------------------------------------------*/
/*  Computes the prefix of the specified ordered list of node moves yielding  */
/*  the highest gain when performed.                                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  moveLog   Ordered list of possible move operations.       */
/*  Return value :  Last entry of the log prefix yielding the highest gain.   */
/******************************************************************************/
Local Slist  ComputeMaxPrefix  (Slist moveLog)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  Slist  maxEntry  = empty_slist;
  int    currGain  = 0;
  int    maxGain   = 0;
  
  /****************************************************************************/
  /*  Compute the last entry of the maximum prefix.                           */
  /****************************************************************************/
  
  for_slist (moveLog, currEntry)
  {
    currGain += GET_GAIN (currEntry);
    
    if (currGain > maxGain)
    {
      maxEntry = currEntry;
    }  /* endif */
  }  end_for_slist (moveLog, currEntry);

  /****************************************************************************/
  /*  Return the last entry of the maximum prefix.                            */
  /****************************************************************************/
  
  return  maxEntry;
  
}  /* End of ComputeMaxPrefix */

/******************************************************************************/
/*    PerformNodeMoves                                                        */
/*----------------------------------------------------------------------------*/
/*  Actually perform the node moves of the prefix - the PartOf attribute must */
/*  be already set. All moves after the prefix are undone - the PartOf flag   */
/*  is reset and the node is put into its old node set.                       */
/*  The space of the log (not the log itself) ist freed.                      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currPassInfo   Current state of the pass.                 */
/*                  moveLog        Ordered list of possible move operations.  */
/*                  maxGain        Last entry of the maximum prefix.          */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  PerformNodeMoves  (PassInfoPtr currPassInfo, Slist moveLog, Slist maxGain)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = moveLog;
  
  /****************************************************************************/
  /*  Perform node moves if neccessary, and free the space in the log.        */
  /****************************************************************************/
  
  if (maxGain != empty_slist)
  {
    do
    {
      if (GET_OLDSET (currEntry) == LeftSet)
      {
        currPassInfo->LockedB = add_immediately_to_slist (currPassInfo->LockedB,
                                  CREATE_NODESET_ENTRY (GET_NODE (currEntry)));
      }
      else
      {
        currPassInfo->LockedA = add_immediately_to_slist (currPassInfo->LockedA,
                                  CREATE_NODESET_ENTRY (GET_NODE (currEntry)));
      }  /* endif */
      
      free (attr_data_of_type (currEntry, MoveLogEntryPtr));
    
      currEntry = currEntry->suc;
    }  while (currEntry != maxGain->suc);  /* enddo */
  }  /* endif */

  /****************************************************************************/
  /*  Undo the uneccessary moves, also free log space.                        */
  /****************************************************************************/
  
  do
  {
    if (GET_OLDSET (currEntry) == LeftSet)
    {
      currPassInfo->LockedA = add_immediately_to_slist (currPassInfo->LockedA,
                                CREATE_NODESET_ENTRY (GET_NODE (currEntry)));
      SET_ENTRY (GET_NODE (currEntry))        = currPassInfo->LockedA->pre;
      PART_OF_NODE_SET (GET_NODE (currEntry)) = LeftSet;
    }
    else
    {
      currPassInfo->LockedB = add_immediately_to_slist (currPassInfo->LockedB,
                                CREATE_NODESET_ENTRY (GET_NODE (currEntry)));
      SET_ENTRY (GET_NODE (currEntry))        = currPassInfo->LockedB->pre;
      PART_OF_NODE_SET (GET_NODE (currEntry)) = RightSet;
    }  /* endif */
    
    free (attr_data_of_type (currEntry, MoveLogEntryPtr));
    
    currEntry = currEntry->suc;
  }  while (currEntry != moveLog);  /* enddo*/

}  /* End of PerformNodeMoves */

/******************************************************************************/
/*  Maintain the Bucket structure.                                            */
/******************************************************************************/

/******************************************************************************/
/*    CreatePassInfo                                                          */
/*----------------------------------------------------------------------------*/
/*  Creates the PassInfo structure according to the cost and degree present   */
/*  in the specified graph. The neccessary number of BuckteCells ist created  */
/*  and initialized.                                                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph    Use the degree and cost info of this graph.  */
/*  Return value :  The new PassInfo structure.                               */
/******************************************************************************/
Local PassInfoPtr  CreatePassInfo  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  PassInfoPtr  newPassInfo = (PassInfoPtr) malloc (sizeof (PassInfo));
  int          maxDegree   = 0;
  int          maxWeight   = 0;
  
  /****************************************************************************/
  /*  Init the elements of the PassInfo structure.                            */
  /****************************************************************************/
  
  newPassInfo->BucketA = (BucketPtr) malloc (sizeof (Bucket));
  newPassInfo->BucketB = (BucketPtr) malloc (sizeof (Bucket));
  newPassInfo->ASize   = 0;
  newPassInfo->BSize   = 0;
  newPassInfo->LockedA = empty_slist;
  newPassInfo->LockedB = empty_slist;

  /****************************************************************************/
  /*  Init the elements of the Buckets.                                       */
  /****************************************************************************/
  
  newPassInfo->BucketA->FirstCell = NULL;
  newPassInfo->BucketB->FirstCell = NULL;
  newPassInfo->BucketA->LastCell  = NULL;
  newPassInfo->BucketB->LastCell  = NULL;
  newPassInfo->BucketA->MaxGain   = NULL;
  newPassInfo->BucketB->MaxGain   = NULL;

  /****************************************************************************/
  /*  2 * maxDegree * maxWeight + 1cells are needed in each bucket.           */
  /****************************************************************************/
  
  ComputeMaxDegreeWeight (currGraph, &maxDegree, &maxWeight);

  /****************************************************************************/
  /*  Create the neccessary number of bucket cells.                           */
  /****************************************************************************/
  
  CreateBuckets (newPassInfo->BucketA, maxDegree * maxWeight);
  CreateBuckets (newPassInfo->BucketB, maxDegree * maxWeight);

  /****************************************************************************/
  /*  Return the new structure                                                */
  /****************************************************************************/
  
  return  newPassInfo;
  
}  /* End of CreatePassInfo */

/******************************************************************************/
/*    ComputeMaxDegreeWeight                                                  */
/*----------------------------------------------------------------------------*/
/*  Compute the maximum degree of the graph and the cost of the most          */
/*  expensive edge in the graph.                                              */
/*  The graph most contain valid attribute fields.                            */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Degree and cost are in this graph (and attrs) */
/*                  maxDegree   Maximum degree found at a node of the graph.  */
/*                  maxWeight   Maximum cost found at a edge of the graph.    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ComputeMaxDegreeWeight  (Sgraph currGraph, int *maxDegree, int *maxWeight)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode   = NULL;
  Sedge  currEdge   = NULL;
  int    currDegree = 0;
  
  /****************************************************************************/
  /*  Do it ...                                                               */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    currDegree = 0;
    
    for_sourcelist (currNode, currEdge)
    {
      currDegree++;
      *maxWeight = EDGE_WEIGHT (currEdge) > *maxWeight ? 
                     EDGE_WEIGHT (currEdge) : *maxWeight;
    }  end_for_sourcelist (currNode, currEdge);
    
    for_targetlist (currNode, currEdge)
    {
      currDegree++;
      *maxWeight = EDGE_WEIGHT (currEdge) > *maxWeight ? 
                     EDGE_WEIGHT (currEdge) : *maxWeight;
    }  end_for_targetlist (currNode, currEdge);
    
    *maxDegree = currDegree > *maxDegree ? currDegree : *maxDegree;
    
  }  end_for_all_nodes (currGraph, currNode);

}  /* End of ComputeMaxDegreeWeight */

/******************************************************************************/
/*    CreateBuckets                                                           */
/*----------------------------------------------------------------------------*/
/*  Creates the neccessary number of cells for the specified bucket and       */
/*  initializes their entries. The cells are indexed from -maxIndex to        */
/*  maxIndex.                                                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currBucket   Bucket to fill with cells.                   */
/*                  maxIndex     2*maxIndex + 1 cells are needed              */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CreateBuckets  (BucketPtr currBucket, int maxIndex)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  BucketCellPtr  newCell   = NULL;
  BucketCellPtr  oldCell   = NULL;
  int            currIndex = (-1 * maxIndex); 
  
  /****************************************************************************/
  /*  Create the neccessary number of cells starting from the end.            */
  /****************************************************************************/
  
  while (currIndex <= maxIndex)
  {
    newCell                = (BucketCellPtr) malloc (sizeof (BucketCell));
    newCell->CellDValue    = currIndex;
    newCell->UnlockedNodes = empty_slist;
    newCell->PrevCell      = NULL;
    newCell->SuccCell      = NULL;
    
    if (currBucket->LastCell == NULL)
    {
      currBucket->LastCell = newCell;
    }
    else
    {
      oldCell->PrevCell = newCell;
      newCell->SuccCell = oldCell;
    }  /* endif */
    
    oldCell = newCell;
    currIndex++;
  }  /* endwhile */
  
  currBucket->FirstCell = newCell;

}  /* End of CreateBuckets */

/******************************************************************************/
/*    InitialPassInfo                                                         */
/*----------------------------------------------------------------------------*/
/*  After computing the D-values of the current partition, this function uses */
/*  them to initialize the buckets: All nodes in the locked lists are moved   */
/*  into the correct buckets.                                                 */
/*  The unlocked lists must contain a valid partition and the buckets must    */
/*  contain the correct number of cells.                                      */
/*  The current partition must also be reflected through the PartOfSet attr.  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  newPassInfo   Structure containing only locked nodes.     */
/*                  currGraph     Graph containing the locked nodes.          */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  InitialPassInfo  (PassInfoPtr newPassInfo, Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = NULL;
  
  /****************************************************************************/
  /*  Initialize the D-values according to the current partition.             */
  /****************************************************************************/
  
  InitialDValues (currGraph);

  newPassInfo->ASize = 0;
  newPassInfo->BSize = 0;

  /****************************************************************************/
  /*  Put all nodes of the left set into the buckets of the left set.         */
  /****************************************************************************/
  
  for_slist (newPassInfo->LockedA, currEntry)
  {
    MoveIntoBucketCell (newPassInfo->BucketA, 
                        (int) D_VALUE (GET_NODESET_ENTRY (currEntry)),
                        GET_NODESET_ENTRY (currEntry));
    newPassInfo->ASize++;
  }  end_for_slist (newPassInfo->LockedA, currEntry);

  /****************************************************************************/
  /*  Do the same thing for the right set.                                    */
  /****************************************************************************/

  for_slist (newPassInfo->LockedB, currEntry)
  {
    MoveIntoBucketCell (newPassInfo->BucketB, 
                        (int) D_VALUE (GET_NODESET_ENTRY (currEntry)),
                        GET_NODESET_ENTRY (currEntry));
    newPassInfo->BSize++;
  }  end_for_slist (newPassInfo->LockedB, currEntry);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (newPassInfo->LockedA);
  free_slist (newPassInfo->LockedB);
  newPassInfo->LockedA = empty_slist;
  newPassInfo->LockedB = empty_slist;
  
}  /* End of InitialPassInfo */

/******************************************************************************/
/*    MoveIntoBucketCell                                                      */
/*----------------------------------------------------------------------------*/
/*  Searches the BucketCell with the same D-value as the specified one and    */
/*  inserts the specified node into the cell.                                 */
/*  If the cell was previously empty, the MaxGain entry of the specified      */
/*  Bucket is updated to the cell.                                            */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currBucket   Bucket to move into.                         */
/*                  currDValue   D-value of the node to move into the bucket. */
/*                  currNode     Node to move into the bucket.                */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  MoveIntoBucketCell (BucketPtr currBucket, int currDValue, Snode currNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  bool           found    = FALSE;
  bool           searchUp = (currDValue < 0);
  BucketCellPtr  currCell = searchUp ? currBucket->LastCell : 
                                       currBucket->FirstCell;
  
  /****************************************************************************/
  /*  Search the correct bucket (it must exist)                               */
  /****************************************************************************/
  
  do
  {
    if (currDValue == currCell->CellDValue)
    {
      found = TRUE;
    }
    else
    {
      currCell = searchUp ? currCell->PrevCell : currCell->SuccCell;
    }  /* endif */
  }  while (!found);  /* endwhile */

  /****************************************************************************/
  /*  Put the node into the bucket.                                           */
  /****************************************************************************/
  
  currCell->UnlockedNodes    = add_immediately_to_slist(currCell->UnlockedNodes,
                                 CREATE_NODESET_ENTRY (currNode));
  SET_ENTRY (currNode)       = currCell->UnlockedNodes->pre;
  IS_NODE_VISITED (currNode) = FALSE;
  
  /****************************************************************************/
  /*  Update the MaxGain information if neccessary.                           */
  /****************************************************************************/
  
  if ((currBucket->MaxGain == NULL) ||
      (currBucket->MaxGain->CellDValue < currDValue))
  {
    currBucket->MaxGain = currCell;
  }  /* endif */
  
}  /* End of MoveIntoBucketCell */

/******************************************************************************/
/*    RemovePassInfo                                                          */
/*----------------------------------------------------------------------------*/
/*  Frees the memory used by the specified pass info structure. Only the      */
/*  LockedA/B lists remain intact, since they represent the current node      */
/*  partition.                                                                */
/*  The Buckets must exist.                                                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currPassInfo   Pass information structure to remove       */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  RemovePassInfo  (PassInfoPtr currPassInfo)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  BucketCellPtr  currCell = currPassInfo->BucketA->FirstCell;
  BucketCellPtr  nextCell = NULL;
  
  /****************************************************************************/
  /*  Remove the cells of bucket A                                            */
  /****************************************************************************/
  
  while (currCell != NULL)
  {
    nextCell = currCell->SuccCell;
    
    free_slist (currCell->UnlockedNodes);
    free (currCell);
    
    currCell = nextCell;
  }  /* endwhile */
  
  free (currPassInfo->BucketA);

  /****************************************************************************/
  /*  Remove the cells of bucket B                                            */
  /****************************************************************************/
  
  currCell = currPassInfo->BucketB->FirstCell;
  
  while (currCell != NULL)
  {
    nextCell = currCell->SuccCell;
    
    free_slist (currCell->UnlockedNodes);
    free (currCell);
    
    currCell = nextCell;
  }  /* endwhile */
  
  free (currPassInfo->BucketB);

  /****************************************************************************/
  /*  Now remove the pass info itself                                         */
  /****************************************************************************/
  
  free (currPassInfo);

}  /* End of RemovePassInfo */

/******************************************************************************/
/*  End of  Fiduccia-Mattheyses.c                                             */
/******************************************************************************/
