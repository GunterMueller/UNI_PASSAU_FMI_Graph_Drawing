/******************************************************************************/
/*                                                                            */
/*    IncreaseDegree.c                                                        */
/*                                                                            */
/******************************************************************************/
/*  Increase the degree of a graph by contracting certain pairs of nodes to   */
/*  a single node. A graph with a high average degree used as input for a     */
/*  separator algorithm, often yields a better separator, after the con-      */
/*  tracted nodes have been expanded again.                                   */
/*  The pairs of nodes to contract are computed using a standard matching     */
/*  algorithm.                                                                */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  03.05.1994                                                    */
/*  Modified :  18.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.2         Compaction does not generate multi-edges any more */
/*              1.1         Save the node positions                           */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "IncreaseDegree.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local void   InitEdgeAttrs           (/*currGraph*/);
Local void   InitNodeFlags           (/*currGraph*/);
Local void   RestoreEdgeAttrs        (/*currGraph*/);
Local Slist  ComputeMatchedEdges     (/*currGraph*/);
Local void   ContractMatchedEdges    (/*currGraph, matchedEdges*/);
Local void   MoveEdgesToContNode     (/*currNode, contNode*/);
Local bool   EdgeAlreadyExists       (/*sourceNode, targetNode, multiEdge*/);
Local Slist  ComputeContractedNodes  (/*currGraph*/);
Local void   RestoreContractedNode   (/*contNode, algInfo*/);
Local void   RestoreMovedEdges       (/*contNode, firstNode, secondNode*/);
Local Snode  ComputeEndNode          (/*...*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local void   InitEdgeAttrs           (Sgraph  currGraph);
Local void   InitNodeFlags           (Sgraph  currGraph);
Local void   RestoreEdgeAttrs        (Sgraph  currGraph);
Local Slist  ComputeMatchedEdges     (Sgraph  currGraph);
Local void   ContractMatchedEdges    (Sgraph  currGraph, Slist   matchedEdges);
Local void   MoveEdgesToContNode     (Snode  currNode, Snode  contNode);
Local bool   EdgeAlreadyExists       (Snode   sourceNode, 
                                      Snode   targetNode, 
                                      Sedge  *multiEdge);
Local Slist  ComputeContractedNodes  (Sgraph  currGraph);
Local void   RestoreContractedNode   (Snode         contNode, 
                                      GSAlgInfoPtr  algInfo);
Local void   RestoreMovedEdges       (Snode  contNode, 
                                      Snode  firstNode, 
                                      Snode  secondNode);
Local Snode  ComputeEndNode          (int    sourceNumber, 
                                      int    targetNumber, 
                                      Snode  firstNode, 
                                      Snode  secondNode);
#endif

/******************************************************************************/
/*  Contracting, Expanding and updating of the node sets                      */
/******************************************************************************/

/******************************************************************************/
/*    IncreaseDegree                                                          */
/*----------------------------------------------------------------------------*/
/*  Increases the average degree of the input graph by computing a maximal    */
/*  matching of the graph and then contracting all pairs of nodes connected   */
/*  by a matched edge to one node.                                            */
/*  The attributes of the contracted nodes are saved in the new nodes - which */
/*  are marked as new - to enable the restoring of the old graph.             */
/*  MUST NOT BE CALLED TWICE ON A GRAPH !!                                    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph    Nonempty graph to increase average degree.   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  IncreaseDegree  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  matchedEdges = empty_slist;
  
  /****************************************************************************/
  /*  Put the edge attributes required for the matching algorithm in the graph*/
  /****************************************************************************/
  
  InitEdgeAttrs (currGraph);  /* Also initializes the edge weight to 1.0      */

  /****************************************************************************/
  /*  Compute a maximum matching and contract all matched edges               */
  /****************************************************************************/
  
/*  SgraphMaxWeightMatching (currGraph);*/
  DumbMatching (currGraph);
  matchedEdges = ComputeMatchedEdges (currGraph);
  InitNodeFlags (currGraph);
  RestoreEdgeAttrs (currGraph);
  ContractMatchedEdges (currGraph, matchedEdges);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (matchedEdges);
  
}  /* End of IncreaseDegree */

/******************************************************************************/
/*    ExpandContractedNodes                                                   */
/*----------------------------------------------------------------------------*/
/*  Restore the original graph by reexpanding all contracted nodes of the     */
/*  graph.                                                                    */
/*  The node sets are updated.                                                */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo   Graph containing nodes which are contracted     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  ExpandContractedNodes  (GSAlgInfoPtr algInfo)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  contractedNodes = ComputeContractedNodes (GRAPH (algInfo));
  Slist  currEntry       = empty_slist;
  
  /****************************************************************************/
  /*  Restore each pair of contracted nodes including all adjacent edges      */
  /****************************************************************************/

  for_slist (contractedNodes, currEntry)
  {
    RestoreContractedNode (GET_NODESET_ENTRY (currEntry), algInfo);
    
    if (PART_OF_NODE_SET (GET_NODESET_ENTRY (currEntry)) == LeftSet)
    {
      algInfo->NodeSet_A = subtract_immediately_from_slist (algInfo->NodeSet_A,
                            SET_ENTRY (GET_NODESET_ENTRY (currEntry)));
    }
    else
    {
      algInfo->NodeSet_B = subtract_immediately_from_slist (algInfo->NodeSet_B,
                            SET_ENTRY (GET_NODESET_ENTRY (currEntry)));
    }  /* endif */
    
    free  (CONT_INFO (GET_NODESET_ENTRY (currEntry)));
    free (attr_data_of_type (GET_NODESET_ENTRY (currEntry), NodeInfoPtr));
    remove_node (GET_NODESET_ENTRY (currEntry));
    NUM_GRAPH_NODES (GRAPH (algInfo))++;
  }  end_for_slist (contractedNodes, currEntry);
  
  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (contractedNodes);
  
}  /* End of ExpandContractedNodes */

/******************************************************************************/
/*    AdjustPartition                                                         */
/*----------------------------------------------------------------------------*/
/*  Rebalances the given partition.                                           */
/*  After Contraction->Separation->Expansion, the node set partition may be   */
/*  unbalanced. The balance is re-established by moving the appropriate       */
/*  number of nodes into the bigger node set.                                 */
/*  The node sets are then made equal in size by adding the apropriate number */
/*  of dummy nodes to the smaller node set.                                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo     Graph containing nodes which are contracted   */
/*                  algSelect   Which separator algorithm is used.            */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  AdjustPartition  (GSAlgInfoPtr algInfo, GSAlgSelect algSelect)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  int    ASize     = size_of_slist (A_SET (algInfo));
  int    BSize     = size_of_slist (B_SET (algInfo));
  bool   leftIsBig = (ASize > BSize);
  int    bigSize   = 0;
  int    smallSize = 0;
  int    moveNodes = 0;
  Slist  fromSet   = empty_slist;
  Slist  toSet     = empty_slist;
  Snode  currNode  = NULL;
  
  /****************************************************************************/
  /*  Determine, from which node set nodes have to be moved to the other set  */
  /****************************************************************************/
  
  ComputeNodeSetSizes (NUM_GRAPH_NODES(GRAPH (algInfo)), 
                       &smallSize, &bigSize, ALPHA (algInfo));
                       
  if (leftIsBig)
  {
    moveNodes = ASize - bigSize;
    fromSet   = A_SET (algInfo);
    toSet     = B_SET (algInfo);
  }
  else
  {
    moveNodes = BSize - bigSize;
    fromSet   = B_SET (algInfo);
    toSet     = A_SET (algInfo);
  }  /* endif */
  
  /****************************************************************************/
  /*  Move the correct number of nodes and keep track of the set sizes.       */
  /****************************************************************************/
  
  while (moveNodes > 0)
  {
    currNode = GET_NODESET_ENTRY (fromSet);
    fromSet  = subtract_immediately_from_slist (fromSet, fromSet);
    toSet    = add_immediately_to_slist (toSet, CREATE_NODESET_ENTRY(currNode));
    SET_ENTRY (currNode)        = toSet->pre;
    PART_OF_NODE_SET (currNode) = leftIsBig ? RightSet : LeftSet;
    
    if (leftIsBig)
    {
      ASize--;
      BSize++;
    }
    else
    {
      ASize++;
      BSize--;
    }  /* endif */
    
    moveNodes--;
  }  /* endwhile */

  /****************************************************************************/
  /*  Reset the set information and balance the node sets with dummies.       */
  /****************************************************************************/

  if (leftIsBig)
  {
    A_SET (algInfo) = fromSet;
    B_SET (algInfo) = toSet;
    
    if (algSelect != FIDUCCIA_MATTHEYSES)
    {
      InsertDummysInNodeSet (algInfo, ASize-BSize, RightSet);
    } /* endif */
  }
  else
  {
    B_SET (algInfo) = fromSet;
    A_SET (algInfo) = toSet;

    if (algSelect != FIDUCCIA_MATTHEYSES)
    {
      InsertDummysInNodeSet (algInfo, BSize-ASize, LeftSet);
    } /* endif */
  }  /* endif */
  
}  /* End of AdjustPartition */

/******************************************************************************/
/*  New edge attribute handling                                               */
/******************************************************************************/

/******************************************************************************/
/*    InitEdgeAttrs                                                           */
/*----------------------------------------------------------------------------*/
/*  Creates for each edge in the graph the attribute structure needed for the */
/*  matching algorithm and saves the old attribute structure to enable the    */
/*  restoring of it later.                                                    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Nonempty graph to add new type of edge attrs. */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  InitEdgeAttrs  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode               currNode    = NULL;
  Sedge               currEdge    = NULL;
  Pair_of_edgevalues  newEdgeAttr = NULL;
  
  /****************************************************************************/
  /*  Create new attributes for each edge and save the old.                   */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if (currGraph->directed || unique_edge (currEdge))
      {
        newEdgeAttr = (Pair_of_edgevalues) malloc 
                      (sizeof (struct pair_of_edgevalues));
        newEdgeAttr->attrs = make_attr (ATTR_DATA, currEdge->attrs.value.data);
        set_edgeattrs (currEdge, MAKE_FLOWMATCH_ATTR (newEdgeAttr));
        MATCH_WEIGHT (currEdge) = 1.0;  /* Initialization !                   */
        FLOW_FLOW (currEdge)    = 0.0;
      }  /* endif */
    } end_for_sourcelist (currNode, currEdge);
  } end_for_all_nodes (currGraph, currNode);

}  /* End of InitEdgeAttrs */

/******************************************************************************/
/*    InitNodeFlags                                                           */
/*----------------------------------------------------------------------------*/
/*  Each node in the graph is given a distinct number starting with zero in   */
/*  the flags.                                                                */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Nonempty graph to add new type of edge attrs. */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  InitNodeFlags  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode   = NULL;
  int    currNumber = 0;
  
  /****************************************************************************/
  /*  Put a distinct node number into the flags field (matching destroyt it)  */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    FLAGS (currNode) = currNumber++;
  } end_for_all_nodes (currGraph, currNode);

}  /* End of InitNodeFlags */

/******************************************************************************/
/*    RestoreEdgeAttrs                                                        */
/*----------------------------------------------------------------------------*/
/*  Restores the original edge attributes for each edge in the graph.         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Nonempty graph to add new type of edge attrs. */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  RestoreEdgeAttrs  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode       currNode    = NULL;
  Sedge       currEdge    = NULL;
  Attributes  oldEdgeAttr;
  
  /****************************************************************************/
  /*  Restore the old edge attributes and free the space allocated for the new*/
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if (currGraph->directed || unique_edge (currEdge))
      {
        oldEdgeAttr = make_attr (ATTR_DATA, SEP_ATTRS (currEdge));
        free (attr_data_of_type (currEdge, Pair_of_edgevalues));
        set_edgeattrs (currEdge, oldEdgeAttr);
        
        SOURCE_NUMBER (currEdge) = FLAGS (currEdge->snode);
        TARGET_NUMBER (currEdge) = FLAGS (currEdge->tnode);
      }  /* endif */
    } end_for_sourcelist (currNode, currEdge);
  } end_for_all_nodes (currGraph, currNode);

}  /* End of RestoreEdgeAttrs */

/******************************************************************************/
/*  Handling of the matched edges                                             */
/******************************************************************************/

/******************************************************************************/
/*    ComputeMatchedEdges                                                     */
/*----------------------------------------------------------------------------*/
/*  Computes all edges of the specified graph, that are in the matching.      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Graph containing matched edges.               */
/*  Return value :  A Slist containing all edges in the matching.             */
/******************************************************************************/
Local Slist  ComputeMatchedEdges  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  matchedEdges = empty_slist;
  Snode  currNode     = NULL;
  Sedge  currEdge     = NULL;
  
  /****************************************************************************/
  /*  Search for the edges in the matching                                    */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if ((currGraph->directed || unique_edge (currEdge)) &&
          IS_MATCHED (currEdge))
      {
        matchedEdges = add_immediately_to_slist (matchedEdges, 
                         CREATE_EDGESET_ENTRY (currEdge));
      }  /* endif */
    } end_for_sourcelist (currNode, currEdge);
  } end_for_all_nodes (currGraph, currNode);
  
  /****************************************************************************/
  /*  Return the list of edges in the matching                                */
  /****************************************************************************/
  
  return  matchedEdges;
  
}  /* End of ComputeMatchedEdges */

/******************************************************************************/
/*    ContractMatchedEdges                                                    */
/*----------------------------------------------------------------------------*/
/*  Contracts nodes which are connected by edges in a maximal matching of the */
/*  graph.                                                                    */
/*  To enable restoring of the original graph, these attribute values are     */
/*  set for each pair of nodes to contract:                                   */
/*  - The attribute of the snode of the matched edge is stored in firstAttr   */
/*    of the new node; the secondAttr corresponds to tnode.                   */
/*  - edgeAttr contains the attribute of the matched edge.                    */
/*  - For all other adjacent edges: TNode == first if the target node of this */
/*    edge is the snode of the matched edge; SNode == first if the source     */
/*    node of this edge is the snode of the matched edge; anlogous for SNode, */
/*    TNode == second.                                                        */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph      Graph with nodes to contract.              */
/*                  matchedEdges   All matched edges if the graph.            */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ContractMatchedEdges  (Sgraph currGraph, Slist matchedEdges)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist        currEntry = empty_slist;
  Snode        contNode  = NULL;
  NodeInfoPtr  contAttr  = NULL;
  Snode        sNode     = NULL;
  Snode        tNode     = NULL;
  ContInfoPtr  contInfo  = NULL;
  Sedge        currEdge  = NULL;
  
  /****************************************************************************/
  /*  Update the graph according to the current matching.                     */
  /****************************************************************************/
  
  for_slist (matchedEdges, currEntry)
  {
    currEdge = GET_EDGELIST_ENTRY (currEntry);
    
    if (!currEdge->snode->graph->directed)
    {
      currEdge = get_unique_edge_handle (currEdge);
    }  /* endif */
    
    sNode    = currEdge->snode;
    tNode    = currEdge->tnode;

    contAttr = (NodeInfoPtr) malloc (sizeof (NodeInfo));
    contNode = make_node (currGraph, make_attr (ATTR_DATA, contAttr));
    
    NODE_REF (contNode)         = NULL;
    PART_OF_NODE_SET (contNode) = NodeNone;
    VALID_NODE_SET (contNode)   = NodeNone;
    IS_NODE_VISITED (contNode)  = FALSE;
    D_VALUE (contNode)          = 0.0;
    IS_DUMMY (contNode)         = FALSE;
    SET_ENTRY (contNode)        = empty_slist;
    FLAGS(contNode)             = -1;

    contInfo = (ContInfoPtr) malloc (sizeof (ContInfo));
    CONT_INFO (contNode)     = contInfo;
    IS_CONTRACTED (contNode) = TRUE;
    FIRST_INFO (contNode)    = sNode->attrs.value.data;
    SECOND_INFO (contNode)   = tNode->attrs.value.data;
    EDGE_INFO (contNode)     = currEdge->attrs.value.data;
    FIRST_X (contNode)       = sNode->x;
    FIRST_Y (contNode)       = sNode->y;
    SECOND_X (contNode)      = tNode->x;
    SECOND_Y (contNode)      = tNode->y;
  
    remove_edge (currEdge);
  
    MoveEdgesToContNode  (sNode, contNode);
    MoveEdgesToContNode  (tNode, contNode);
    
    remove_node (sNode);
    remove_node (tNode);
    NUM_GRAPH_NODES (currGraph)--;
  }  end_for_slist (matchedEdges, currEntry);

}  /* End of ContractMatchedEdges */

/******************************************************************************/
/*    MoveEdgesToContNode                                                     */
/*----------------------------------------------------------------------------*/
/*  Removes all edges adjacent to currNode and reinserts them with contNode   */
/*  as replacment for currNode as an end node.                                */
/*  The matched edge between the two nodes to contract MUST be removed.       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode    One of the end nodes of a matched edge        */
/*                  contNode    The contracted node out of the two end nodes  */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  MoveEdgesToContNode  (Snode currNode, Snode contNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge  = currNode->slist;
  Sedge  multiEdge = NULL;
  Slist  currEntry = empty_slist;
  
  /****************************************************************************/
  /*  Move all edges of the source list of the current node                   */
  /****************************************************************************/
  
  while (currEdge != NULL)
  {
    if (EdgeAlreadyExists (contNode, currEdge->tnode, &multiEdge))
    {
      MULTI_LIST (multiEdge) = add_immediately_to_slist (MULTI_LIST (multiEdge),
        CREATE_MULTI_ENTRY ((EdgeInfoPtr) attr_data (currEdge)));

      for_slist (MULTI_LIST (currEdge), currEntry)
      {
        MULTI_LIST (multiEdge) = add_immediately_to_slist (
          MULTI_LIST (multiEdge),
          CREATE_MULTI_ENTRY (GET_MULTI_ENTRY (currEntry)));
      }  end_for_slist (MULTI_LIST (currEdge), currEntry);

      free_slist (MULTI_LIST (currEdge));
      MULTI_LIST (currEdge) = empty_slist;
    }
    else
    {
      make_edge (contNode, currEdge->tnode, 
                  make_attr (ATTR_DATA, (EdgeInfoPtr) attr_data (currEdge)));
    }  /* endif */

    /* Get the next valid edge of currNode                                    */
    remove_edge (currEdge);
    currEdge = currNode->slist; 
  }  /* endwhile */

  /****************************************************************************/
  /*  Do the same thing for the target list (empty for undirected graphs).    */
  /****************************************************************************/
  
  currEdge = currNode->tlist;

  while (currEdge != NULL)
  {
    if (EdgeAlreadyExists (currEdge->snode, contNode, &multiEdge))
    {
      MULTI_LIST (multiEdge) = add_immediately_to_slist (MULTI_LIST (multiEdge),
        CREATE_MULTI_ENTRY ((EdgeInfoPtr) attr_data (currEdge)));

      for_slist (MULTI_LIST (currEdge), currEntry)
      {
        MULTI_LIST (multiEdge) = add_immediately_to_slist (
          MULTI_LIST (multiEdge),
          CREATE_MULTI_ENTRY (GET_MULTI_ENTRY (currEntry)));
      }  end_for_slist (MULTI_LIST (currEdge), currEntry);

      free_slist (MULTI_LIST (currEdge));
      MULTI_LIST (currEdge) = empty_slist;
    }
    else
    {
      make_edge (currEdge->snode, contNode,
                  make_attr (ATTR_DATA, (EdgeInfoPtr) attr_data (currEdge)));
    }  /* endif */

    /* Get the next valid edge of currNode                                    */
    remove_edge (currEdge);
    currEdge = currNode->tlist; 
  }  /* endwhile */

}  /* End of MoveEdgesToContNode */

/******************************************************************************/
/*    EdgeAlreadyExists                                                       */
/*----------------------------------------------------------------------------*/
/*  Searches the sourcelist of the first specified node for an edge with      */
/*  the other node as end-node.                                               */
/*  If such an edge is found, it is returned.                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  sourceNode   Node to scan the source list.                */
/*                  targetNode   Other end node of a possible edge.           */
/*                  multiEdge    Contains the exisiting edge, or is empty.    */
/*  Return value :  TRUE, if an edge exists between the two nodes.            */
/******************************************************************************/
Local bool  EdgeAlreadyExists  (Snode sourceNode, Snode targetNode, Sedge *multiEdge)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Search the edge.                                                        */
  /****************************************************************************/
  
  for_sourcelist (sourceNode, currEdge)
  {
    if (currEdge->tnode == targetNode)
    {
      *multiEdge = currEdge;
      return TRUE;
    }  /* endif */
  }  end_for_sourcelist (sourceNode, currEdge);

  /****************************************************************************/
  /* No edge found.                                                           */
  /****************************************************************************/
  
  *multiEdge = NULL;
  return FALSE;

}  /* End of EdgeAlreadyExists */

/******************************************************************************/
/*  Restoring of contracted nodes                                             */
/******************************************************************************/

/******************************************************************************/
/*    ComputeContractedNodes                                                  */
/*----------------------------------------------------------------------------*/
/*  Computes all nodes of the specified graph, that have the contracted flag  */
/*  set.                                                                      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Graph containing contracted nodes.            */
/*  Return value :  A Slist containing all contracted nodes.                  */
/******************************************************************************/
Local Slist  ComputeContractedNodes  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  contractedNodes = empty_slist;
  Snode  currNode        = NULL;
  
  /****************************************************************************/
  /*  Search for the contracted nodes                                         */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    if (IS_CONTRACTED (currNode))
    {
      contractedNodes = add_immediately_to_slist (contractedNodes, 
                          CREATE_NODESET_ENTRY (currNode));
    }  /* endif */
  } end_for_all_nodes (currGraph, currNode);
  
  /****************************************************************************/
  /*  Return the list of contracted nodes                                     */
  /****************************************************************************/
  
  return contractedNodes;
  
}  /* End of ComputeContractedNodes */

/******************************************************************************/
/*    RestoreContractedNode                                                   */
/*----------------------------------------------------------------------------*/
/*  Expands the specified contracted node to the original two nodes, the edge */
/*  between the two nodes and divides the edges adjacent to the contracted    */
/*  node between the two original nodes such that the structure of the orig-  */
/*  inal graph is restored (locally).                                         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  contNode   Node consisting of two original nodes.         */
/*                  algInfo    Graph and node partition.                      */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  RestoreContractedNode  (Snode contNode, GSAlgInfoPtr algInfo)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  firstNode  = NULL;
  Snode  secondNode = NULL;
  
  /****************************************************************************/
  /*  Create the two original nodes                                           */
  /****************************************************************************/
  
  firstNode      = make_node (contNode->graph, 
                              make_attr (ATTR_DATA, FIRST_INFO (contNode)));
  firstNode->x   = FIRST_X (contNode);
  firstNode->y   = FIRST_Y (contNode);
  secondNode     = make_node (contNode->graph, 
                              make_attr (ATTR_DATA, SECOND_INFO (contNode)));
  secondNode->x  = SECOND_X (contNode);
  secondNode->y  = SECOND_Y (contNode);
  
  PART_OF_NODE_SET (firstNode)  = PART_OF_NODE_SET (contNode);
  PART_OF_NODE_SET (secondNode) = PART_OF_NODE_SET (contNode);

  /****************************************************************************/
  /*  Put firstNode and SecondNode into their node sets                       */
  /****************************************************************************/
  
  if (PART_OF_NODE_SET (contNode) == LeftSet)
  {
    algInfo->NodeSet_A     = add_immediately_to_slist (algInfo->NodeSet_A,
                               CREATE_NODESET_ENTRY (firstNode));
    SET_ENTRY (firstNode)  = algInfo->NodeSet_A->pre;
    algInfo->NodeSet_A     = add_immediately_to_slist (algInfo->NodeSet_A,
                               CREATE_NODESET_ENTRY (secondNode));
    SET_ENTRY (secondNode) = algInfo->NodeSet_A->pre;
  }
  else
  {
    algInfo->NodeSet_B     = add_immediately_to_slist (algInfo->NodeSet_B,
                               CREATE_NODESET_ENTRY (firstNode));
    SET_ENTRY (firstNode)  = algInfo->NodeSet_B->pre;
    algInfo->NodeSet_B     = add_immediately_to_slist (algInfo->NodeSet_B,
                               CREATE_NODESET_ENTRY (secondNode));
    SET_ENTRY (secondNode) = algInfo->NodeSet_B->pre;
  }  /* endif */
  
  /****************************************************************************/
  /*  Reconstruct the adjacency lists.                                        */
  /****************************************************************************/
  
  RestoreMovedEdges (contNode, firstNode, secondNode);
  
  make_edge (firstNode, secondNode, make_attr (ATTR_DATA, EDGE_INFO(contNode)));

}  /* End of RestoreContractedNode */

/******************************************************************************/
/*    RestoreMovedEdges                                                       */
/*----------------------------------------------------------------------------*/
/*  Distributes the edges adjacent to contNode to their original end nodes    */
/*  according to the *Number entries in the edge attributes.                  */
/*  The edges are NOT removed from the source/target lists of contNode.       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  contNode     Contracted node to be restored.              */
/*                  firstNode    Source node of the contracted node.          */
/*                  secondNode   Target node of the contracted node.          */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  RestoreMovedEdges  (Snode contNode, Snode firstNode, Snode secondNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge  = NULL;
  Sedge  newEdge   = NULL;
  Snode  endNode   = NULL;
  Slist  multiList = empty_slist;
  Slist  currEntry = empty_slist;
  Sedge  multiEdge = NULL;
  
  /****************************************************************************/
  /*  Distribute the source edges of contNode                                 */
  /****************************************************************************/
  
  for_sourcelist (contNode, currEdge)
  {
    endNode   = ComputeEndNode (SOURCE_NUMBER (currEdge), 
                                TARGET_NUMBER (currEdge), 
                                firstNode, 
                                secondNode);
    multiList             = MULTI_LIST (currEdge);
    MULTI_LIST (currEdge) = empty_slist;
                              
    if (EdgeAlreadyExists (endNode, currEdge->tnode, &multiEdge))
    {
      MULTI_LIST (multiEdge) = add_immediately_to_slist (
        MULTI_LIST (multiEdge),
        CREATE_MULTI_ENTRY ((EdgeInfoPtr) attr_data (currEdge)));
    }
    else
    {
      newEdge = make_edge (endNode, currEdge->tnode,
                  make_attr (ATTR_DATA, (EdgeInfoPtr) attr_data (currEdge)));
      MULTI_LIST (newEdge) = empty_slist;
    }  /* endif */
    
    for_slist (multiList, currEntry)
    {
      endNode = ComputeEndNode (GET_MULTI_ENTRY (currEntry)->SourceNumber, 
                                GET_MULTI_ENTRY (currEntry)->TargetNumber, 
                                firstNode, 
                                secondNode);
      
      if (EdgeAlreadyExists (endNode, currEdge->tnode, &multiEdge))
      {
        MULTI_LIST (multiEdge) = add_immediately_to_slist (
          MULTI_LIST (multiEdge),
          CREATE_MULTI_ENTRY (GET_MULTI_ENTRY (currEntry)));
      }
      else
      {
        make_edge (endNode, currEdge->tnode,
                    make_attr (ATTR_DATA, GET_MULTI_ENTRY (currEntry)));
      }  /* endif */
    }  end_for_slist (multiList, currEntry);
    
    free_slist (multiList);
  }  end_for_sourcelist (contNode, currEdge);
  
  /****************************************************************************/
  /*  Distribute the target edges of contNode                                 */
  /****************************************************************************/
  
  if (contNode->graph->directed)
  {
    for_targetlist (contNode, currEdge)
    {
      endNode               = ComputeEndNode (SOURCE_NUMBER (currEdge), 
                                              TARGET_NUMBER (currEdge), 
                                              firstNode, 
                                              secondNode);
      multiList             = MULTI_LIST (currEdge);
      MULTI_LIST (currEdge) = empty_slist;
                                
      if (EdgeAlreadyExists (currEdge->snode, endNode, &multiEdge))
      {
        MULTI_LIST (multiEdge) = add_immediately_to_slist (
          MULTI_LIST (multiEdge),
          CREATE_MULTI_ENTRY ((EdgeInfoPtr) attr_data (currEdge)));
      }
      else
      {
        newEdge = make_edge (currEdge->snode, endNode, 
                    make_attr (ATTR_DATA, (EdgeInfoPtr) attr_data (currEdge)));
        MULTI_LIST (newEdge) = empty_slist;
      }  /* endif */
    
      for_slist (multiList, currEntry)
      {
        endNode = ComputeEndNode (GET_MULTI_ENTRY (currEntry)->SourceNumber, 
                                  GET_MULTI_ENTRY (currEntry)->TargetNumber, 
                                  firstNode, 
                                  secondNode);
        
        if (EdgeAlreadyExists (currEdge->snode, endNode, &multiEdge))
        {
          MULTI_LIST (multiEdge) = add_immediately_to_slist (
            MULTI_LIST (multiEdge),
            CREATE_MULTI_ENTRY (GET_MULTI_ENTRY (currEntry)));
        }
        else
        {
          make_edge (currEdge->snode, endNode, 
                      make_attr (ATTR_DATA, GET_MULTI_ENTRY (currEntry)));
        }  /* endif */
      }  end_for_slist (multiList, currEntry);
      
      free_slist (multiList);
    }  end_for_targetlist (contNode, currEdge);
  }  /* endif */
  
}  /* End of RestoreMovedEdges */

/******************************************************************************/
/*    ComputeEndNode                                                          */
/*----------------------------------------------------------------------------*/
/*  Decides to which original node the current edge was adjacent.             */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  sourceNumber   Number of the source node of the edge.     */
/*                  targetNumber   Number of the target node of the edge.     */
/*                  firstNode    Source node of the contracted node.          */
/*                  secondNode   Target node of the contracted node.          */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local Snode  ComputeEndNode  (int sourceNumber, int targetNumber, Snode firstNode, Snode secondNode)
{
  /****************************************************************************/
  /*  Return the correct end node.                                            */
  /****************************************************************************/
  
  if ((sourceNumber == FLAGS (firstNode)) ||
      (targetNumber == FLAGS (firstNode)))
  {
    return firstNode;
  }
  else
  {
    return secondNode;
  }  /* endif */

}  /* End of ComputeEndNode */

/******************************************************************************/
/*  End of  IncreaseDegree.c                                                  */
/******************************************************************************/
