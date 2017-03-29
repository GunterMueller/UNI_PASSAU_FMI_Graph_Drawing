/******************************************************************************/
/*                                                                            */
/*    Kernighan-Lin.c                                                         */
/*                                                                            */
/******************************************************************************/
/*  Implementation of the Kernighan-Lin graph-spearator heuristic.            */
/*  Functions to compute alpha edge separators.                               */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  14.03.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.4         Name change.                                      */
/*              1.3         Resolved naming collisions.                       */
/*              1.2         Interface changed (algInfo).                      */
/*              1.1         Randomized initial node set assignment.           */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "Kernighan-Lin.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local  bool  ImprovePartition           ();
Local  void  CreateNewPair              ();
Local  void  UpdateRemainingDValues     ();
Local  void  ComputeRemainingNewDValue  ();
Local  bool  ComputeMaximumGain         ();
Local  void  ExchangeXY                 ();
#endif

#ifdef  ANSI_HEADERS_ON
Local bool  ImprovePartition           (Slist  *NodeSet_A, Slist  *NodeSet_B);
Local void  CreateNewPair              (ExchangePairPtr  *newPair,
                                        Snode             A_max,
                                        Snode             B_max);
Local void  UpdateRemainingDValues     (Snode  pairNode);
Local void  ComputeRemainingNewDValue  (Snode  pairNode, 
                                        Snode  currNode, 
                                        Sedge  currEdge);
Local bool  ComputeMaximumGain         (Slist  *XYPairs);
Local void  ExchangeXY                 (Slist  *NodeSet_A, 
                                        Slist  *NodeSet_B, 
                                        Slist   XYPairs);
#endif

/******************************************************************************/
/*  Implementation of the edge separator algorithm                            */
/******************************************************************************/

/******************************************************************************/
/*    KernighanLin                                                            */
/*----------------------------------------------------------------------------*/
/*  Computes an alpha edge-separator according to the Kernighan-Lin           */
/*  Heuristic.                                                                */
/*  The separators computed by this procedure are bisections. To compute      */
/*  separators for alpha > 0.5, the graph is partitioned into two node sets   */
/*  fulfilling the alpha condition. Then the smaller set is filled up with    */
/*  "dummy nodes" having no edges adjacent to them, which are removed after   */
/*  the (locally) optimal separator was computed.                             */
/*  The actual computation of the separator is the repetition of two steps:   */
/*  1) For each node compute the difference between external and internal     */
/*     cost in the current partition.                                         */
/*  2) Improve the current partion.                                           */
/*  One improve step is:                                                      */
/*  1) Compute one subset of each node set to be exchanged according to a     */
/*     suitable fast selection heuristic.                                     */
/*  2) If exchanging those subsets would yield a positive gain, do it!        */
/*  Calling this function with validSep == TRUE and valid alpha-separator     */
/*  data in the node sets allows the optimization of a separator by the       */
/*  Kernighan-Lin heuristic or the randomization of this heuristic. Then,     */
/*  however, if alpha > 0.5, the dummy nodes MUST be already in the graph and */
/*  will still be there after completion of this function. Dummy nodes have   */
/*  the Dummy-flag in the node attributes set, whereas normal nodes haven't.  */
/*  Both node sets must the be of equal size.                                 */
/*  The attributes of the elements of the specified graph must be initalized  */
/*  before calling this function and they MUST be removed afterwards.         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo    Data to execute algorithm (graph, alpha...).   */
/*                  validSep   TRUE, if A, B and S are a valid Separator      */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  KernighanLin  (GSAlgInfoPtr algInfo, bool validSep)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  int  maxASet   = 0;
  int  maxBSet   = 0;
  
  /****************************************************************************/
  /*  Compute an initial node set partition if none was given                 */
  /****************************************************************************/

  if (!validSep)
  {
    ComputeNodeSetSizes (NUM_GRAPH_NODES (GRAPH (algInfo)), 
                         &maxASet, &maxBSet, ALPHA (algInfo));
    InsertDummys (GRAPH (algInfo), maxBSet - maxASet);
    RandomInitialPartition (GRAPH (algInfo), &A_SET (algInfo), &B_SET (algInfo), 
                            NUM_GRAPH_NODES (GRAPH (algInfo))/2);
  }  /* endif */

  /****************************************************************************/
  /*  Improve the node set partition by exchanging subsets while improving    */
  /****************************************************************************/
  
  do
  {
    InitialDValues (GRAPH (algInfo));
    /* Stepping to insert here ... */

#ifdef ANIMATION_ON
    AnimateAlgorithm (algInfo);
#endif

  }  while (ImprovePartition (&A_SET (algInfo), &B_SET (algInfo)));

  /****************************************************************************/
  /*  Compute the separator edges and remove dummy nodes if neccessary        */
  /****************************************************************************/
  
  ComputeSeparatorEdges (GRAPH (algInfo), &SEP_SET (algInfo));
  
  if (!validSep)
  {
    RemoveDummysFromNodeSet (&A_SET (algInfo));
    RemoveDummysFromNodeSet (&B_SET (algInfo));
  }  /* endif */
  
}  /* End of KernighanLin */

/******************************************************************************/
/*    ImprovePartition                                                        */
/*----------------------------------------------------------------------------*/
/*  Improves the given partition of the graph.                                */
/*  This is one step in the Kernighan-Lin heuristic:                          */
/*  Computing one subset of each node set which improves the given partition  */
/*  and then exchanging them.                                                 */
/*  The specified node sets MUST be of equal size!                            */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  NodeSet_A   First subset of the graphs nodes              */
/*                  NodeSet_B   Second subset of the graphs nodes             */
/*  Return value :  TRUE, if improving the partition was successful           */
/******************************************************************************/
Local bool  ImprovePartition  (Slist *NodeSet_A,
                               Slist *NodeSet_B)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist            A_Work     = copy_slist (*NodeSet_A);
  Slist            B_Work     = copy_slist (*NodeSet_B);
  Slist            A_max      = empty_slist;
  Slist            B_max      = empty_slist;
  Slist            XYPairs    = empty_slist;  /* Hopefully slist keeps order! */
  ExchangePairPtr  newPair    = NULL; 
  bool             success    = TRUE;
  
  /****************************************************************************/
  /*  Compute the ordered list of all exchange pairs                          */
  /****************************************************************************/
  
  while (A_Work != empty_slist) /* Both lists are of equal size !!            */
  {
    /* Heuristic for computing the pair yielding the heighest gain when ex-   */
    /* changing. Gain additionally depends on the edge-weight between the two */
    /* nodes, which is not considered here.                                   */
    ComputeMaxElem (A_Work, &A_max);
    ComputeMaxElem (B_Work, &B_max);
    CreateNewPair(&newPair, GET_NODESET_ENTRY(A_max), GET_NODESET_ENTRY(B_max));
    XYPairs = add_immediately_to_slist (XYPairs, CREATE_EP_ENTRY (newPair));
    /* Remove the computed pair from the work-copies of the node sets.        */
    A_Work = subtract_immediately_from_slist (A_Work, A_max);
    B_Work = subtract_immediately_from_slist (B_Work, B_max);
    /* Simulate exchanging the pair by computing the effect on the D-values   */
    /* of the remaining nodes (Nodes not yet marked as visited).              */
    UpdateRemainingDValues (newPair->A_Max);
    UpdateRemainingDValues (newPair->B_Max);
  }  /*endwhile */

  /****************************************************************************/
  /*  Compute the first pairs with maximum gain and perform exchange on them  */
  /****************************************************************************/
  
  success = ComputeMaximumGain (&XYPairs);
  if (success)
  {
    ExchangeXY (NodeSet_A, NodeSet_B, XYPairs); /* Also free space of pairs.  */
  }  /* endif */
  
  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/

  free_slist (XYPairs);
  return success;
}  /* End of ImprovePartition */

/******************************************************************************/
/*    CreateNewPair                                                           */
/*----------------------------------------------------------------------------*/
/*  Creates a new entry for the exchange pair list with the specified two     */
/*  node set entries.                                                         */
/*  The gain of an exchange operation with these two nodes is also computed.  */
/*  This requires the computatation of the edge (if it exists) between the    */
/*  nodes and therefore the sorcelist of one node (two in the directed case)  */
/*  has to be scanned.                                                        */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  newPair   New exchange pair list entry to create          */
/*                  A_max     Maximum element in the first set of nodes       */
/*                  B_max     Maximum element in the second set of nodes      */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CreateNewPair  (ExchangePairPtr *newPair, Snode A_max, Snode B_max)
{
  /****************************************************************************/
  /*  Create a new exchange pair list entry and fill it with values           */
  /****************************************************************************/

  *newPair          = (ExchangePairPtr) malloc (sizeof (ExchangePair));
  (*newPair)->A_Max = A_max;
  (*newPair)->B_Max = B_max;
  (*newPair)->Gain  = D_VALUE (A_max) + D_VALUE (B_max) 
                      - 2.0 * PairEdgeWeight (A_max, B_max);
  
  /****************************************************************************/
  /*  Lock the two nodes of the pair to be exchanged                          */
  /****************************************************************************/

  IS_NODE_VISITED (A_max) = TRUE;
  IS_NODE_VISITED (B_max) = TRUE;
  
}  /* End of CreateNewPair */

/******************************************************************************/
/*    UpdateRemainingDValues                                                  */
/*----------------------------------------------------------------------------*/
/*  Recomputes the D_values for nodes which are adjacent to the specified     */
/*  node, simulating the possible exchange operation using this node.         */
/*  Only adjacent nodes, which are still in a *_Work set need to be           */
/*  considered.                                                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  pairNode   Node changing node set disturbing D-values     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  UpdateRemainingDValues  (Snode pairNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Test all adjacent nodes if they need to be updated                      */
  /****************************************************************************/
  
  for_sourcelist (pairNode, currEdge)
  {
    ComputeRemainingNewDValue (pairNode, 
                               NEXT_NODE (pairNode, currEdge), 
                               currEdge);
  }  end_for_sourcelist (pairNode, currEdge);
  
  /* Handle the directed case                                                 */
  if (pairNode->graph->directed)
  {
    for_targetlist (pairNode, currEdge)
    {
      ComputeRemainingNewDValue (pairNode, 
                                 NEXT_NODE (pairNode, currEdge), 
                                 currEdge);
    }  end_for_targetlist (pairNode, currEdge);
  }  /* endif */
}  /* End of UpdateRemainingDValues */

/******************************************************************************/
/*    ComputeRemainingNewDValue                                               */
/*----------------------------------------------------------------------------*/
/*  Simulate moving pairNode to the other node set by updating the D-value of */
/*  currNode accordingly.                                                     */
/*  This is done only if currNode is still in a *_Work set.                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  pairNode   Simulate moving this node into the other set   */
/*                  currNode   Node possibly needing update of its D-value    */
/*                  currEdge   Edge between pairNode and currNode             */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ComputeRemainingNewDValue  (Snode pairNode, Snode currNode, Sedge currEdge)
{
  /****************************************************************************/
  /*  Update the D-value of currNode if neccessary                            */
  /****************************************************************************/

  if (!IS_NODE_VISITED (currNode)) /* Node still in a working set             */
  {
    if (PART_OF_NODE_SET (pairNode) == PART_OF_NODE_SET (currNode))
    {
      D_VALUE (currNode) = D_VALUE (currNode) + 2.0 * EDGE_WEIGHT (currEdge);
    }
    else  /* Simulate moving currNode into the same set as nextNode           */
    {
      D_VALUE (currNode) = D_VALUE (currNode) - 2.0 * EDGE_WEIGHT (currEdge);
    }  /*endif */
  }  /* endif */
}  /* End of ComputeRemainingNewDValue */

/******************************************************************************/
/*    ComputeMaximumGain                                                      */
/*----------------------------------------------------------------------------*/
/*  Computes from the specified list of exchange pairs the first k elements   */
/*  yielding the maximum gain, when exchanging the pairs.                     */
/*  The space for the remaining pairs is removed from the list.               */
/*  If no positive gain is possible, the space of all pairs is removed.       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  XYPairs   List of candidate node pairs for exchange       */
/*  Return value :  TRUE, if a positive gain is possible.                     */
/******************************************************************************/
Local bool  ComputeMaximumGain  (Slist *XYPairs)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Slist  nextEntry = empty_slist;  
  Slist  currEntry = empty_slist;
  float  currGain  = 0.0;
  Slist  maxEntry  = empty_slist;
  float  maxGain   = 0.0;
  
  /****************************************************************************/
  /*  Compute the last element of the pairs which should be exchanged         */
  /****************************************************************************/
  
  for_slist (*XYPairs, currEntry)
  {
    currGain = currGain + GAIN(currEntry);
    if (currGain > maxGain)
    {
      maxGain  = currGain;
      maxEntry = currEntry;
    } /* endif */
  }  end_for_slist (*XYPairs, currEntry);

  /****************************************************************************/
  /*  Remove the rest of the list of exchange pairs or else all pairs         */
  /****************************************************************************/
  
  if (maxEntry != empty_slist)
  {
    currEntry = maxEntry->suc;
    while (currEntry != *XYPairs)
    {
      nextEntry = currEntry->suc;
      free (GET_EP_ENTRY(currEntry));
      subtract_immediately_from_slist (*XYPairs, currEntry);
      currEntry = nextEntry;
    }  /* endwhile */
    
    return TRUE;
  }
  else  /* Remove the space allocated for all pairs, since no gain possible   */
  {
    for_slist (*XYPairs, currEntry)
    {
      free (GET_EP_ENTRY(currEntry));
    }  end_for_slist (*XYPairs, currEntry);
    
    return FALSE;
  }  /* endif */
}  /* End of ComputeMaximumGain */

/******************************************************************************/
/*    ExchangeXY                                                              */
/*----------------------------------------------------------------------------*/
/*  Exchnages the specified node pairs and updates the node sets accordingly. */
/*  The space allocated for the entries of the list of pairs is freed.        */
/*  The markers in the node attributes (DValue, Visited) maybe dirty after    */
/*  completion of this function call.                                         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  NodeSet_A   First set of the partition to be updated      */
/*                  NodeSet_B   Second set of the partition to be updated     */
/*                  XYPairs     List of node pairs to be exchanged            */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ExchangeXY  (Slist *NodeSet_A, 
                         Slist *NodeSet_B, 
                         Slist XYPairs)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  
  /****************************************************************************/
  /*  Exchange the pairs of nodes and free the space needed to store them     */
  /****************************************************************************/
  
  for_slist (XYPairs, currEntry)
  {
    /* Remove the pair nodes from their node sets                             */
    *NodeSet_A = subtract_immediately_from_slist (*NodeSet_A, 
                   SET_ENTRY (A_NODE (currEntry)));
    *NodeSet_B = subtract_immediately_from_slist (*NodeSet_B, 
                   SET_ENTRY (B_NODE (currEntry)));

    /* Put the former element of the right set into the left set              */
    *NodeSet_A = add_immediately_to_slist (*NodeSet_A, 
                   CREATE_NODESET_ENTRY(B_NODE (currEntry)));
    SET_ENTRY (B_NODE (currEntry))        = (*NodeSet_A)->pre;
    PART_OF_NODE_SET (B_NODE (currEntry)) = LeftSet;

    /* Put the former element of the left set into the right set              */
    *NodeSet_B = add_immediately_to_slist (*NodeSet_B, 
                   CREATE_NODESET_ENTRY(A_NODE (currEntry)));
    SET_ENTRY (A_NODE (currEntry))        = (*NodeSet_B)->pre;
    PART_OF_NODE_SET (A_NODE (currEntry)) = RightSet;
    
    free (GET_EP_ENTRY(currEntry));
  }  end_for_slist (XYPairs, currEntry);
}  /* End of ExchangeXY */

/******************************************************************************/
/*  End of  Kernighan-Lin.c                                                   */
/******************************************************************************/
