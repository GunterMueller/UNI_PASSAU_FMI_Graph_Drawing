/******************************************************************************/
/*                                                                            */
/*    KL-Exchange.c                                                           */
/*                                                                            */
/******************************************************************************/
/*  Enhancement of the Kernighan Lin heuristic:                               */
/*  Pure Kernighan-Lin often fails to find rather big (approximately n/2)     */
/*  sets of nodes to exchange and therefor often fails to find a global       */
/*  optimum.                                                                  */
/*  This enhancement takes a (locally optimal) node set partition and com-    */
/*  putes a bisection of each of the node sets. The resulting subsets are     */
/*  then exchanged in both ways (A_1 + B_1, A_2 + B_2 ; A_1 + B_2, A_2 + B_1) */
/*  and the resulting node set partition is used as input to the separator    */
/*  algorithm. The best resulting (optimized) partition is the output.        */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  15.05.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.2         Name change.                                      */
/*              1.1         New interface (AlgorithmInfo)                     */
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
Local void  BisectNodeSet        (/*currNodeSet, Set_1, Set_2*/);
Local void  ComputeSubGraph      (/*currNodeSet, subGraph*/);
Local void  MergeNodeSets        (/*Set_1, Set_2, mergedSet, newSet*/);
Local void  BalanceNodeSets      (/*Slist  *leftSet, Slist  *rightSet*/);
Local void  ComputeNewPartition  (/*algInfo, NewSet_A, NewSet_B, optimumSize*/);
Local void  UpdateNodeMarkers    (/*currSet, inSet*/);
Local void  UpdateEdgeMarkers    (/*currGraph*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local void  BisectNodeSet        (Slist   currNodeSet, 
                                  Slist  *Set_1, 
                                  Slist  *Set_2);
Local void  ComputeSubGraph      (Slist   currNodeSet, 
                                  Sgraph  subGraph);
Local void  MergeNodeSets        (Slist     Set_1, 
                                  Slist     Set_2, 
                                  Slist    *mergedSet, 
                                  NodeSet   newSet);
Local void  BalanceNodeSets      (Slist  *leftSet, Slist  *rightSet);
Local void  ComputeNewPartition  (GSAlgInfoPtr   algInfo, 
                                  Slist         *NewSet_A, 
                                  Slist         *NewSet_B, 
                                  int           *optimumSize);
Local void  UpdateNodeMarkers    (Slist  currSet, NodeSet  inSet);
Local void  UpdateEdgeMarkers    (Sgraph  currGraph);
#endif

/******************************************************************************/
/*  Implementation of the exchange heuristic.                                 */
/******************************************************************************/

/******************************************************************************/
/*    KLExchange                                                              */
/*----------------------------------------------------------------------------*/
/*  Implements the the algorithm explained in the header of this file.        */
/*  Since KL originally can only compute bisections, the dummy nodes neces-   */
/*  sary for alpha > 0.5 are added before calling KL for the first locally    */
/*  optimal partition. After the exchange operation, the graph still contains */
/*  the correct number of dummys to compute an alpha edge separator.          */
/*  To be able to compute the exchange sets, it may be necessary to add       */
/*  (temporary) a dummy node to make the number of nodes in the node set even.*/
/*  The node sets and the edge set must be empty and the graph nonempty.      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo    Data to execute algorithm (graph, alpha...).   */
/*                  validSep   TRUE, if A, B and S are a valid Separator      */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  KLExchange  (GSAlgInfoPtr algInfo, bool validSep)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  int     maxASet      = 0;
  int     maxBSet      = 0;
  int     optimumSize  = 0;
  Slist   A_1          = empty_slist;
  Slist   A_2          = empty_slist;
  Slist   B_1          = empty_slist;
  Slist   B_2          = empty_slist;
  Slist   NewSet_A     = empty_slist;
  Slist   NewSet_B     = empty_slist;
  
  /****************************************************************************/
  /*  Prepare a proper input separator for the KL-algorithm.                  */
  /****************************************************************************/

  if (!validSep)
  {
    ComputeNodeSetSizes (NUM_GRAPH_NODES(GRAPH (algInfo)), 
                          &maxASet, &maxBSet, ALPHA (algInfo));
    InsertDummys (GRAPH (algInfo), maxBSet - maxASet);
    RandomInitialPartition (GRAPH (algInfo), &A_SET (algInfo), &B_SET (algInfo), 
                            NUM_GRAPH_NODES (GRAPH (algInfo))/2);
  }  /* endif */
                          
  /****************************************************************************/
  /*  Compute the first locally optimal node set partition                    */
  /****************************************************************************/
  
  KernighanLin (algInfo, TRUE);
  optimumSize = size_of_slist (SEP_SET (algInfo));

  if ((maxASet > 1) && (maxBSet > 1))
  {
    /**************************************************************************/
    /*  Partition both node sets in two subsets to exchange them.             */
    /**************************************************************************/
    
    BisectNodeSet (A_SET (algInfo), &A_1, &A_2);
    BisectNodeSet (B_SET (algInfo), &B_1, &B_2);
  
    /**************************************************************************/
    /*  Test if A_1 + B_1 and A_2 + B_2 leads to a better separator.          */
    /**************************************************************************/
    
    MergeNodeSets (A_1, B_1, &NewSet_A, LeftSet);
    MergeNodeSets (A_2, B_2, &NewSet_B, RightSet);
    BalanceNodeSets (&NewSet_A, &NewSet_B);
    ComputeNewPartition (algInfo, &NewSet_A, &NewSet_B, &optimumSize);
  
    /**************************************************************************/
    /*  Do the same thing with A_1 + B_2 and A_2 + B_1                        */
    /**************************************************************************/
    
    MergeNodeSets (A_1, B_2, &NewSet_A, LeftSet);
    MergeNodeSets (A_2, B_1, &NewSet_B, RightSet);
    BalanceNodeSets (&NewSet_A, &NewSet_B);
    ComputeNewPartition (algInfo, &NewSet_A, &NewSet_B, &optimumSize);
  
    /**************************************************************************/
    /*  Update the attribute markers to reflect the optimal partition         */
    /**************************************************************************/
    
    UpdateNodeMarkers (A_SET (algInfo), LeftSet);
    UpdateNodeMarkers (B_SET (algInfo), RightSet);
    UpdateEdgeMarkers (GRAPH (algInfo));

  } /* endif */
  
  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  if (!validSep)
  {
    RemoveDummysFromNodeSet (&A_SET (algInfo));
    RemoveDummysFromNodeSet (&B_SET (algInfo));
  }  /* endif */
  
}  /* End of KLExchange */

/******************************************************************************/
/*  Handling of subsets                                                       */
/******************************************************************************/

/******************************************************************************/
/*    BisectNodeSet                                                           */
/*----------------------------------------------------------------------------*/
/*  Computes a bisection of the subgraph induced by the specified node set.   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNodeSet   Compute a bisection using only these nodes. */
/*                  Set_1         First part of the bisection.                */
/*                  Set_2         Second part of the bisection.               */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  BisectNodeSet  (Slist currNodeSet, Slist *Set_1, Slist *Set_2)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  GraphInfoPtr  subInfoAttr   = (GraphInfoPtr) malloc (sizeof (GraphInfo));
  Slist         currElem      = empty_slist;
  GSAlgInfoPtr  subGraphInfo  = (GSAlgInfoPtr) malloc (sizeof (GSAlgInfo));

  /****************************************************************************/
  /*  Compute the subgraph induced by the set of nodes                        */
  /****************************************************************************/
  
  GRAPH (subGraphInfo)   = make_graph (make_attr(ATTR_DATA,(char*)subInfoAttr));
  A_SET (subGraphInfo)   = empty_slist;
  B_SET (subGraphInfo)   = empty_slist;
  SEP_SET (subGraphInfo) = empty_slist;
  ALPHA (subGraphInfo)   = 0.5;
  
  NUM_GRAPH_NODES (GRAPH (subGraphInfo)) = 0;
  
  /****************************************************************************/
  /*  Compute the subgraph induced by the set of nodes                        */
  /****************************************************************************/
  
  ComputeSubGraph (currNodeSet, GRAPH (subGraphInfo));

  /****************************************************************************/
  /*  Compute a bisection of the subgraph                                     */
  /****************************************************************************/

  KernighanLin (subGraphInfo, FALSE);

  /****************************************************************************/
  /*  Compute the node sets corresponding to the original graph               */
  /****************************************************************************/
  
  for_slist (A_SET (subGraphInfo), currElem)
  {
    *Set_1 = add_immediately_to_slist (*Set_1, 
               CREATE_NODESET_ENTRY (NODE_REF (GET_NODESET_ENTRY (currElem))));
    SET_ENTRY (NODE_REF (GET_NODESET_ENTRY (currElem))) = (*Set_1)->pre;
  }  end_for_slist (A_SET (subGraphInfo), currElem);

  for_slist (B_SET (subGraphInfo), currElem)
  {
    *Set_2 = add_immediately_to_slist (*Set_2, 
               CREATE_NODESET_ENTRY (NODE_REF (GET_NODESET_ENTRY (currElem))));
    SET_ENTRY (NODE_REF (GET_NODESET_ENTRY (currElem))) = (*Set_2)->pre;
  }  end_for_slist (B_SET (subGraphInfo), currElem);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (A_SET (subGraphInfo));
  free_slist (B_SET (subGraphInfo));
  free_slist (SEP_SET (subGraphInfo));
  
  RemoveAttributes (GRAPH (subGraphInfo));
  remove_graph (GRAPH (subGraphInfo));
  
}  /* End of BisectNodeSet */

/******************************************************************************/
/*    ComputeSubGraph                                                         */
/*----------------------------------------------------------------------------*/
/*  Compute the graph induced by the nodes in the specified node set. That    */
/*  is, all nodes in the node set are also in the new graph and all edges of  */
/*  the original graph with both end nodes in the node set are also in the    */
/*  new graph.                                                                */
/*  The refNode attribute is set to the node of the original graph, for which */
/*  the new node was created.                                                 */
/*  The subGraph must already exist - with its proper attribute structure.    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNodeSet   Nodes inducing a graph.                     */
/*                  subGraph      Induced by the nodes in currNodeSet.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ComputeSubGraph  (Slist currNodeSet, Sgraph subGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist        currEntry = empty_slist;
  Sedge        currEdge  = NULL;
  Snode        subNode   = NULL;
  NodeInfoPtr  nodeInfo  = NULL;
  Sedge        subEdge   = NULL;
  EdgeInfoPtr  edgeInfo  = NULL;
  
  /****************************************************************************/
  /*  Create all nodes of the subgraph with their attributes and init. them   */
  /****************************************************************************/
  
  for_slist (currNodeSet, currEntry)
  {
    nodeInfo = (NodeInfoPtr) malloc (sizeof (NodeInfo));
    subNode  = make_node (subGraph, make_attr (ATTR_DATA, (char *) nodeInfo));
    NODE_REF (GET_NODESET_ENTRY (currEntry)) = subNode;
    NODE_REF (subNode)          = GET_NODESET_ENTRY (currEntry);
    IS_NODE_VISITED (subNode)   = FALSE;
    PART_OF_NODE_SET (subNode)  = NodeNone;
    VALID_NODE_SET (subNode)    = NodeNone;
    D_VALUE (subNode)           = 0.0;
    IS_DUMMY (subNode)          = FALSE;
    SET_ENTRY (subNode)         = empty_slist;
    IS_CONTRACTED (subNode)     = FALSE;
    CONT_INFO(subNode)          = NULL;
    NUM_GRAPH_NODES(subGraph)++;
  }  end_for_slist (currNodeSet, currEntry);

  /****************************************************************************/
  /*  Copy all edges with endnodes in the subgraph to the subgraph            */
  /****************************************************************************/
  
  for_slist (currNodeSet, currEntry)
  {
    for_sourcelist (GET_NODESET_ENTRY (currEntry), currEdge)
    {
      if ((currEdge->snode->graph->directed || unique_edge (currEdge)) &&
          (PART_OF_NODE_SET (currEdge->snode) == 
           PART_OF_NODE_SET (currEdge->tnode)))
      {
        edgeInfo = (EdgeInfoPtr) malloc (sizeof (EdgeInfo));
        subEdge  = make_edge (NODE_REF (currEdge->snode), 
                              NODE_REF (currEdge->tnode),
                              make_attr (ATTR_DATA, (char *) edgeInfo));
        EDGE_WEIGHT(subEdge)        = EDGE_WEIGHT (currEdge);
        IS_EDGE_VISITED (subEdge)   = FALSE;
        PART_OF_EDGE_SET (subEdge)  = EdgeNone;
        VALID_EDGE_SET (subEdge)    = EdgeNone;
        SOURCE_NUMBER (subEdge)     = -1;
        TARGET_NUMBER (subEdge)     = -1;
        MULTI_LIST (subEdge)        = empty_slist;
      }  /* endif */
    }  end_for_sourcelist (GET_NODESET_ENTRY (currEntry), currEdge);
  }  end_for_slist (currNodeSet, currEntry);

}  /* End of ComputeSubGraph */

/******************************************************************************/
/*    MergeNodeSets                                                           */
/*----------------------------------------------------------------------------*/
/*  Put all elements of the specified two node sets into one single set and   */
/*  set the attribute values for set membership to the specified value.       */
/*  The new set MUST initially be empty, whereas the input sets stay the same.*/
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Set_1       First set for the merging.                    */
/*                  Set_2       Second set for the merging.                   */
/*                  mergedSet   Result of Set_1 + Set_2 (+ == union).         */
/*                  newSet      Put all nodes in mergedSet into this set.     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  MergeNodeSets (Slist Set_1, Slist Set_2, Slist *mergedSet, NodeSet newSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  
  /****************************************************************************/
  /*  Put the elements of the two lists into one list and set the set attrs   */
  /****************************************************************************/
  
  for_slist (Set_1, currEntry)
  {
    PART_OF_NODE_SET (GET_NODESET_ENTRY (currEntry)) = newSet;
    VALID_NODE_SET (GET_NODESET_ENTRY (currEntry))   = newSet;
    *mergedSet = add_immediately_to_slist (*mergedSet,
                   CREATE_NODESET_ENTRY (GET_NODESET_ENTRY (currEntry)));
    SET_ENTRY (GET_NODESET_ENTRY (currEntry)) = (*mergedSet)->pre;
  }  end_for_slist (Set_1, currEntry);

  for_slist (Set_2, currEntry)
  {
    PART_OF_NODE_SET (GET_NODESET_ENTRY (currEntry)) = newSet;
    VALID_NODE_SET (GET_NODESET_ENTRY (currEntry))   = newSet;
    *mergedSet = add_immediately_to_slist (*mergedSet,
                   CREATE_NODESET_ENTRY (GET_NODESET_ENTRY (currEntry)));
    SET_ENTRY (GET_NODESET_ENTRY (currEntry)) = (*mergedSet)->pre;
  }  end_for_slist (Set_2, currEntry);

}  /* End of MergeNodeSets */

/******************************************************************************/
/*    BalanceNodeSets                                                         */
/*----------------------------------------------------------------------------*/
/*  Rebalances the specified node sets after the merge step, to make sure     */
/*  the Kernighan-Lin heuristic is able to work on them as input.             */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  leftSet    First subset of the nodes.                     */
/*                  rightSet   Second subset of the nodes.                    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  BalanceNodeSets  (Slist *leftSet, Slist *rightSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  int    leftSize  = size_of_slist (*leftSet);
  int    rightSize = size_of_slist (*rightSet);
  bool   leftIsBig = (leftSize > rightSize);
  int    moveNodes = 0;
  Slist  fromSet   = empty_slist;
  Slist  toSet     = empty_slist;
  Snode  currNode  = NULL;
  
  /****************************************************************************/
  /*  Determine, from which node set nodes have to be moved to the other set  */
  /****************************************************************************/
  
  if (leftIsBig)
  {
    moveNodes = (leftSize - rightSize) / 2;
    fromSet   = *leftSet;
    toSet     = *rightSet;
  }
  else
  {
    moveNodes = (rightSize - leftSize) / 2;
    fromSet   = *rightSet;
    toSet     = *leftSet;
  }  /* endif */
  
  /****************************************************************************/
  /*  Move the correct number of nodes.                                       */
  /****************************************************************************/
  
  while (moveNodes > 0)
  {
    currNode = GET_NODESET_ENTRY (fromSet);
    fromSet  = subtract_immediately_from_slist (fromSet, fromSet);
    toSet    = add_immediately_to_slist (toSet, CREATE_NODESET_ENTRY(currNode));
    SET_ENTRY (currNode)        = toSet->pre;
    PART_OF_NODE_SET (currNode) = leftIsBig ? RightSet : LeftSet;
    VALID_NODE_SET (currNode)   = PART_OF_NODE_SET (currNode);
    
    moveNodes--;
  }  /* endwhile */

  /****************************************************************************/
  /*  Reset the set information.                                              */
  /****************************************************************************/

  if (leftIsBig)
  {
    *leftSet = fromSet;
    *rightSet = toSet;
  }
  else
  {
    *rightSet = fromSet;
    *leftSet = toSet;
  }  /* endif */
  
}  /* End of BalanceNodeSets */

/******************************************************************************/
/*  Compute separators using subsets.                                         */
/******************************************************************************/

/******************************************************************************/
/*    ComputeNewPartition                                                     */
/*----------------------------------------------------------------------------*/
/*  Computes a new separator using the new node sets as inpute for the KL     */
/*  edge separator heuristic and determines if the new separator is better    */
/*  than the specified actual one.                                            */
/*  If this is the case, the actual separator is replaced by the new one and  */
/*  the size of the optimum is updated.                                       */
/*  The node sets of the new partition are always empty after completion of   */
/*  this function.                                                            */
/*  The node markers of all nodes in the graph must be set according to their */
/*  membership in the specified new node sets.                                */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo       The graph to separate                       */
/*                  NewSet_A      One set of the new node set partition       */
/*                  NewSet_B      The other set of the new node set partition */
/*                  optimumSize   Size of the actual optimum separator.       */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ComputeNewPartition  (GSAlgInfoPtr algInfo, Slist *NewSet_A, Slist *NewSet_B, int *optimumSize)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  int           newSize     = 0;
  GSAlgInfoPtr  newAlgInfo  = (GSAlgInfoPtr) malloc (sizeof (GSAlgInfo));
  
  /****************************************************************************/
  /*  Compute the separator set according to the current node marker settings */
  /****************************************************************************/

 
  GRAPH (newAlgInfo)   = GRAPH (algInfo);
  A_SET (newAlgInfo)   = *NewSet_A;
  B_SET (newAlgInfo)   = *NewSet_B;
  SEP_SET (newAlgInfo) = empty_slist;
  ALPHA (newAlgInfo)   = ALPHA (algInfo);
  
#ifdef  ANIMATION_ON
  ANIMATE (newAlgInfo) = ANIMATE (algInfo);
#endif
  
  ComputeSeparatorEdges (GRAPH (algInfo), &SEP_SET(newAlgInfo));
  
  /****************************************************************************/
  /*  Compute a new separator with the new node sets                          */
  /****************************************************************************/

  KernighanLin (newAlgInfo, TRUE);
  newSize = size_of_slist (SEP_SET(newAlgInfo));
  
  /****************************************************************************/
  /*  Replace the actual optimum by the new separator if the new one is better*/
  /****************************************************************************/

  if (newSize < *optimumSize)
  {
    free_slist (A_SET (algInfo));
    free_slist (B_SET (algInfo));
    free_slist (SEP_SET (algInfo));
    
    A_SET (algInfo)   = A_SET (newAlgInfo);
    B_SET (algInfo)   = B_SET (newAlgInfo);
    SEP_SET (algInfo) = SEP_SET(newAlgInfo);
    
    *optimumSize = newSize;
  }
  else  /* The old one was better */
  {
    free_slist (A_SET (newAlgInfo));
    free_slist (B_SET (newAlgInfo));
    free_slist (SEP_SET(newAlgInfo));
  }  /* endif */

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  *NewSet_A  = empty_slist;
  *NewSet_B  = empty_slist;
  free (newAlgInfo);

}  /* End of ComputeNewPartition */

/******************************************************************************/
/*  Cleaning up the attribute values                                          */
/******************************************************************************/

/******************************************************************************/
/*    UpdateNodeMarkers                                                       */
/*----------------------------------------------------------------------------*/
/*  Set the values of the node set membership flags of the node attributes of */
/*  the specified node set to the specified value.                           */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currSet   Node set to update.                             */
/*                  inSet     Put all nodes into this set.                    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  UpdateNodeMarkers  (Slist currSet, NodeSet inSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  
  /****************************************************************************/
  /*  Set the attribute values of all nodes in the node set                   */
  /****************************************************************************/
  
  for_slist (currSet, currEntry)
  {
    PART_OF_NODE_SET (GET_NODESET_ENTRY (currEntry)) = inSet;
    VALID_NODE_SET (GET_NODESET_ENTRY (currEntry))   = inSet;
    SET_ENTRY (GET_NODESET_ENTRY (currEntry))        = currEntry;
  }  end_for_slist (currSet, currEntry);

}  /* End of UpdateNodeMarkers */

/******************************************************************************/
/*    UpdateEdgeMarkers                                                       */
/*----------------------------------------------------------------------------*/
/*  Update the edge markers according to the values of the node markers.      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Graph, where only the node markers are correct*/
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  UpdateEdgeMarkers  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Compute the correct attribute values for all edges of the graph         */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if (currGraph->directed || unique_edge (currEdge))
      {
        if (PART_OF_NODE_SET (currEdge->snode) == 
            PART_OF_NODE_SET (currEdge->tnode))
        {
          PART_OF_EDGE_SET (currEdge) = 
            (PART_OF_NODE_SET (currEdge->snode) == LeftSet) ? A_Set : B_Set;
        }
        else
        {
          PART_OF_EDGE_SET (currEdge) = Separator_Set;
        }  /* endif */
        VALID_EDGE_SET (currEdge) = PART_OF_EDGE_SET (currEdge);
      }  /* endif */
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (currGraph, currNode);

}  /* End of UpdateEdgeMarkers */

/******************************************************************************/
/*  End of  KL-Exchange.c                                                     */
/******************************************************************************/
