/******************************************************************************/
/*                                                                            */
/*    Greedy.c                                                                */
/*                                                                            */
/******************************************************************************/
/*  Implementation of a greedy startegy to find an alpha edge separator.      */
/*  This in fact is some sort of hill-climbing, since always the approximatly */
/*  "steepest descent" is choosen.                                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  25.04.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.3         Name change.                                      */
/*              1.2         Interface changed (algInfo).                      */
/*              1.1         Randomized initial node set assignment.           */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "Greedy.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local bool  ImprovePartition  (/*NodeSet_A, NodeSet_B*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local bool  ImprovePartition  (Slist  *NodeSet_A, Slist  *NodeSet_B);
#endif

/******************************************************************************/
/*  Implementation of the greedy heuristic.                                   */
/******************************************************************************/

/******************************************************************************/
/*    Greedy                                                                  */
/*----------------------------------------------------------------------------*/
/*  Computes an alpha edge-separator according to a Hill-Climbing heuristic.  */
/*  The separators computed by this procedure are bisections. To compute      */
/*  separators for alpha > 0.5, the graph is partitioned into two node sets   */
/*  fulfilling the alpha condition. Then the smaller set is filled up with    */
/*  "dummy nodes" having no edges adjacent to them, which are removed after   */
/*  the (locally) optimal separator was computed.                             */
/*  The actual computation of the separator is the repetition of two steps:   */
/*  1) For each node compute the difference between external and internal     */
/*     cost in the current partition.                                         */
/*  2) Improve the current partion while improving is possible.               */
/*  One improve step is:                                                      */
/*  1) Compute a pair of nodes yielding positve gain.                         */
/*  2) Exchange this pair and update the partition.                           */
/*  Calling this function with validSep == TRUE and valid alpha-separator     */
/*  data in the node sets allows the optimization of a separator by the       */
/*  Hill-Climbing heuristic or the randomization of this heuristic. Then,     */
/*  however, if alpha > 0.5, the dummy nodes MUST be already in the graph and */
/*  will still be there after completion of this function. Dummy nodes have   */
/*  the Dummy-flag in the node attributes set, whereas normal nodes haven't.  */
/*  The smaller set is initially always NodeSet_A.                            */
/*  The attributes of the elements of the specified graph must be initalized  */
/*  before calling this function and they MUST be removed afterwards.         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo     Data to execute algorithm (graph, alpha...).  */
/*                  validSep    TRUE, if A, B and S are a valid Separator     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  Greedy  (GSAlgInfoPtr algInfo, bool validSep)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  int    maxASet  = 0;
  int    maxBSet  = 0;
  
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
  /*  Improve the node set partition by exchanging node pairs while improving */
  /****************************************************************************/
  
  InitialDValues (GRAPH (algInfo)); 

  while (ImprovePartition (&A_SET (algInfo), &B_SET (algInfo)))
  {
    /* Stepping to insert here ... */

#ifdef ANIMATION_ON
    AnimateAlgorithm (algInfo);
#endif

  }  /* endwhile */

  /****************************************************************************/
  /*  Compute the separator edges and remove dummy nodes if neccessary        */
  /****************************************************************************/
  
  ComputeSeparatorEdges (GRAPH (algInfo), &SEP_SET (algInfo));
  
  if (!validSep)
  {
    RemoveDummysFromNodeSet (&A_SET (algInfo));
    RemoveDummysFromNodeSet (&B_SET (algInfo));
  }  /* endif */
  
}  /* End of Greedy */

/******************************************************************************/
/*    ImprovePartition                                                        */
/*----------------------------------------------------------------------------*/
/*  Improves the given partition of the graph by exchanging one pair of nodes */
/*  yielding a positive gain.                                                 */
/*  The D-values of the graph are also updated.                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  NodeSet_A   First subset of the graphs nodes              */
/*                  NodeSet_B   Second subset of the graphs nodes             */
/*  Return value :  TRUE, if improving the partition was successful           */
/******************************************************************************/
Local bool  ImprovePartition  (Slist *NodeSet_A, Slist *NodeSet_B)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  A_max   = empty_slist;
  Slist  B_max   = empty_slist;
  
  /****************************************************************************/
  /*  Compute the pair of nodes yielding the highest gain.                    */
  /****************************************************************************/
  
  /* Heuristic for computing the pair yielding the heighest gain when ex-     */
  /* changing. Gain additionally depends on the edge-weight between the two   */
  /* nodes, which is not considered here.                                     */
  ComputeMaxElem (*NodeSet_A, &A_max);
  ComputeMaxElem (*NodeSet_B, &B_max);
  
  /****************************************************************************/
  /*  Perform an exchange of the nodes, if this yields a positive gain.       */
  /****************************************************************************/
  
  if (D_VALUE (GET_NODESET_ENTRY (A_max)) + 
      D_VALUE (GET_NODESET_ENTRY (B_max)) - 
      2.0 * PairEdgeWeight (GET_NODESET_ENTRY(A_max), GET_NODESET_ENTRY(B_max)) 
      > 0.0)
  {
    /* Simulate exchanging the pair by computing the effect on the D-values   */
    /* of the pair-nodes and all adjacent nodes.                              */
    ExchangeOnePair (A_max, B_max, NodeSet_A, NodeSet_B);

    return TRUE;
  }
  else  /* No positive gain possible */ 
  {
    return FALSE;
  }  /* endif */
  
}  /* End of ImprovePartition */

/******************************************************************************/
/*  End of Greedy.c                                                           */
/******************************************************************************/
