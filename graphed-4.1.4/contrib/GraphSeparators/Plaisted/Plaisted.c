/******************************************************************************/
/*                                                                            */
/*    Plaisted.c                                                              */
/*                                                                            */
/*  Heuristic to compute edge separators proposed by Plaisted.                */
/*  Randomly distributes all nodes into two sets and converts the graph into  */
/*  a parametrized network flow problem. The parameter is the edge capacity,  */
/*  which is then minimized. The saturated edges at this capacity are the     */
/*  wanted separator edges.                                                   */
/*  The separator yields no balanced separator though.                        */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  15.04.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "Plaisted.h"
#include "sgraph/random.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local NodeSet   RandomPlaistedPartition  (/*...*/);
Local void      AdjustDummyCapacities    (/*dummyNode, dummySet, nodeSet*/);
Local void      SetNewCapacity           (/*currGraph, newCapacity*/);
Local float     MaxEdgeFlow              (/*currGraph*/);
Local void      CreateSourceNode         (/*currGraph, LeftSet*/);
Local void      CreateTargetNode         (/*currGraph, RightSet*/);
Local void      RemoveSourceTarget       (/*currGraph*/);
Local void      EpsilonMinCut            (/*...*/);
Local void      AssignOrgNodes           (/*SepSet, NodeSet_A, NodeSet_B*/);
Local void      ScanIntoNodeSet          (/*startNode, currSet, thisSet*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local NodeSet  RandomPlaistedPartition  (Sgraph   currGraph, 
                                         Slist   *LeftNodeSet, 
                                         Slist   *RightNodeSet, 
                                         int      maxSize);
Local void  AdjustDummyCapacities       (Snode    dummyNode, 
                                         NodeSet  dummySet, 
                                         Slist    nodeSet);
Local void   SetNewCapacity             (Sgraph  currGraph, float newCapacity);
Local float  MaxEdgeFlow                (Sgraph  currGraph);
Local void   CreateSourceNode           (Sgraph  currGraph, 
                                         Slist   LeftNodeSet);
Local void   CreateTargetNode           (Sgraph  currGraph, 
                                         Slist   RightNodeSet);
Local void   RemoveSourceTarget         (Sgraph  currGraph);
Local void   EpsilonMinCut              (Sgraph   currGraph, 
                                         Slist   *NodeSet_A, 
                                         Slist   *NodeSet_B, 
                                         Slist   *SepSet);
Local void   AssignOrgNodes             (Slist   SepSet, 
                                         Slist  *NodeSet_A, 
                                         Slist  *NodeSet_B);
Local void   ScanIntoNodeSet            (Snode     startNode, 
                                         NodeSet   currSet, 
                                         Slist    *thisSet);
#endif

/******************************************************************************/
/*  Implementation of the plaisted algorithms.                                */
/******************************************************************************/

/******************************************************************************/
/*    PlaistedAmax                                                            */
/*----------------------------------------------------------------------------*/
/*  Implementation of algorithm A_max of the Plaisted paper.                  */
/*  The input graph must have the proper attributes for nodes and edges (of   */
/*  the max-flow implementation) and for the graph. The graph must be direc-  */
/*  ted and if two nodes of the original graph are connected by at least one  */
/*  edge, there is one edge from the first to the second and from the second  */
/*  to the first node. The number of nodes must be even and each edge of the  */
/*  original graph has the same capacity (initially |V|/2).                   */
/*  This algorithm calls the PlaistedA algorithm a number of times to maxi-   */
/*  mize the probability for a good output cut.                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   A plaisted graph (each edge going in both d.) */
/*                  numTrials   Number of times to run algorithm A.           */
/*                  NodeSet_A   Left side of the cut.                         */
/*                  NodeSet_B   Right side of the cut.                        */
/*                  SepSet      List of edges disconnecting the graph.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void   PlaistedAmax (Sgraph currGraph, int numTrials, Slist *NodeSet_A, Slist *NodeSet_B, Slist *SepSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currLeftSet  = empty_slist;
  Slist  currRightSet = empty_slist;
  Slist  currSepSet   = empty_slist;
  int    minSize      = 0;
  int    currSize     = 0;
  int    currTrial    = 1;
  
  /****************************************************************************/
  /*  Try the Plaisted algorithm several times and store the best try.        */
  /****************************************************************************/
  
  PlaistedA (currGraph, &currLeftSet, &currRightSet, &currSepSet);
  minSize    = size_of_slist (currSepSet);
  *NodeSet_A = copy_slist (currLeftSet);
  *NodeSet_B = copy_slist (currRightSet);
  *SepSet    = copy_slist (currSepSet);
  
  do
  {
    free_slist (currLeftSet); 
    free_slist (currRightSet);
    free_slist (currSepSet);
    currLeftSet  = empty_slist;
    currRightSet = empty_slist;
    currSepSet   = empty_slist;
    
    PlaistedA (currGraph, &currLeftSet, &currRightSet, &currSepSet);
    currSize = size_of_slist (currSepSet);
    
    if (currSize < minSize)
    {
      free_slist (*NodeSet_A);
      free_slist (*NodeSet_B);
      free_slist (*SepSet);
      minSize    = currSize;
      *NodeSet_A = copy_slist (currLeftSet);
      *NodeSet_B = copy_slist (currRightSet);
      *SepSet    = copy_slist (currSepSet);
    }  /* endif */
    
    currTrial++;
  }  while (currTrial < numTrials);
  
  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (currLeftSet);
  free_slist (currRightSet);
  free_slist (currSepSet);
    
}  /* End of PlaistedAmax */

/******************************************************************************/
/*    PlaistedA                                                               */
/*----------------------------------------------------------------------------*/
/*  Implementation of algorithm A of the Plaisted paper.                      */
/*  The input graph must have the proper attributes for nodes and edges (of   */
/*  the max-flow implementation) and for the graph. The graph must be direc-  */
/*  ted and if two nodes of the original graph are connected by at least one  */
/*  edge, there is one edge from the first to the second and from the second  */
/*  to the first node. The number of nodes must be even and each edge of the  */
/*  original graph has the same capacity (initially |V|/2).                   */
/*  The nodes are partitioned randomly into two equal sized node sets and two */
/*  new nodes - a source and a target node - are created and the source is    */
/*  connected to one node set and the target to the other (by edges of capa-  */
/*  city one. This creates a network used as input for a max flow algorithm.  */
/*  By iterated calls to max-flow, the capacity of the original edges is      */
/*  minimized. The saturated edges of the network at max-flow with minimal    */
/*  capacities disconnect the original graph.                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   A plaisted graph (each edge going in both d.) */
/*                  NodeSet_A   Left side of the cut.                         */
/*                  NodeSet_B   Right side of the cut.                        */
/*                  SepSet      List of edges disconnecting the graph.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void   PlaistedA  (Sgraph currGraph, Slist *NodeSet_A, Slist *NodeSet_B, Slist *SepSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  int      step         = 0;
  int      maxIteration = (int) ceil (log ((double) NUM_NODES (currGraph)));
  int      currFlow     = 0;
  NodeSet  dummySet     = NodeNone;
  bool     raised       = FALSE;
  
  /****************************************************************************/
  /*  Prepare the graph to conform to the network conditions.                 */
  /****************************************************************************/
  
  dummySet = RandomPlaistedPartition (currGraph, NodeSet_A, NodeSet_B, 
                                      NUM_NODES (currGraph)/2);
  if (dummySet == LeftSet)
  {
      AdjustDummyCapacities (DUMMY_NODE (currGraph), LeftSet, *NodeSet_B);
  }
  else  if (dummySet == RightSet)
  {
      AdjustDummyCapacities (DUMMY_NODE (currGraph), RightSet, *NodeSet_A);
  }  /* endif */
  
  CreateSourceNode (currGraph, *NodeSet_A);
  CreateTargetNode (currGraph, *NodeSet_B);
  free_slist (*NodeSet_A);
  free_slist (*NodeSet_B);
  *NodeSet_A = empty_slist;
  *NodeSet_B = empty_slist;
  SetNewCapacity (currGraph, ((float) NUM_NODES (currGraph)) / 2.0);

  /****************************************************************************/
  /*  Compute the capacity at which the min cut disconnects the graph         */
  /****************************************************************************/
  
  while ((step < maxIteration) || raised)
  {
    currFlow = FordFulkerson (currGraph, SOURCE(currGraph), TARGET(currGraph));
    
    if  (currFlow == ((float) NUM_NODES (currGraph)) / 2.0)
    {
      SetNewCapacity (currGraph, MaxEdgeFlow (currGraph) / 2.0);
      raised = FALSE;
    }
    else  /* Capacity of the edges was too low */
    {
      SetNewCapacity (currGraph, 1.5 * ((float) MAX_CAPACITY(currGraph)));
      raised = TRUE;
    }  /* endif */
    
    step++;
  }  /* endwhile */

  /****************************************************************************/
  /*  Compute the min cut using the hopefully good capacity for the edges.    */
  /****************************************************************************/
  
  FordFulkerson (currGraph, SOURCE(currGraph), TARGET(currGraph));
  RemoveSourceTarget (currGraph);
  EpsilonMinCut (currGraph, NodeSet_A, NodeSet_B, SepSet);

}  /* End of PlaistedA */

/******************************************************************************/
/*    RandomPlaistedPartition                                                 */
/*----------------------------------------------------------------------------*/
/*  Randomly assigns the nodes of the specified graph to the two node sets.   */
/*  The bigger set is of at most maxSize, which therefor MUST be greater than */
/*  half the number of nodes. Also the node sets MUST be empty when calling.  */
/*  The diffrence to a similar function implemented in Separator.c is, that   */
/*  currGraph is a plaisted graph, which has other node attributes.           */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph     Partition the nodes of this plaisted graph. */
/*                  LeftNodeSet   First node set.                             */
/*                  RightNodeSet  Second node set.                            */
/*                  maxSize       Maximum size of the bigger of the node sets */
/*  Return value :  Node set to which the dummy node (if existent) belongs    */
/******************************************************************************/
Local NodeSet  RandomPlaistedPartition  (Sgraph currGraph, 
                                         Slist *LeftNodeSet, 
                                         Slist *RightNodeSet, 
                                         int maxSize)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode    currNode  = NULL;
  int      LeftSize  = 0;
  int      RightSize = 0;
  NodeSet  dummySet  = NodeNone;
  
  /****************************************************************************/
  /*  Provide a random seed for the random number generator using system time */
  /****************************************************************************/

  srandom ((int) time (NULL));

  /****************************************************************************/
  /*  Randomly assign all nodes to node sets smaller than maxSize.            */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    if ((LeftSize < maxSize) && (RightSize < maxSize))
    {
      if (random ()&01)
      {
        *LeftNodeSet = add_immediately_to_slist (*LeftNodeSet,
                         CREATE_NODESET_ENTRY (currNode));
        LeftSize++;
        dummySet = (currNode == DUMMY_NODE (currGraph)) ? LeftSet : NodeNone;
      }
      else
      {
        *RightNodeSet = add_immediately_to_slist (*RightNodeSet, 
                          CREATE_NODESET_ENTRY (currNode));
        RightSize++;
        dummySet = (currNode == DUMMY_NODE (currGraph)) ? RightSet : NodeNone;
      }  /* endif */
    }
    else if (LeftSize < maxSize)
    {
      *LeftNodeSet = add_immediately_to_slist (*LeftNodeSet,
                        CREATE_NODESET_ENTRY (currNode));
      LeftSize++;
      dummySet = (currNode == DUMMY_NODE (currGraph)) ? LeftSet : NodeNone;
    }
    else
    {
      *RightNodeSet = add_immediately_to_slist (*RightNodeSet, 
                        CREATE_NODESET_ENTRY (currNode));
      RightSize++;
      dummySet = (currNode == DUMMY_NODE (currGraph)) ? RightSet : NodeNone;
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);
  
  return  dummySet;

}  /* End of RandomPlaistedPartition */

/******************************************************************************/
/*    AdjustDummyCapacities                                                   */
/*----------------------------------------------------------------------------*/
/*  This function sets the capacities of all edges, which should not be in a  */
/*  separator, to zero, to prevent them from carrying any flow.               */
/*  If the dummy node is in the set of target nodes, the edges are those      */
/*  coming from the set of source nodes going into the dummy node.            */
/*  If the dummy node is in the set of source nodes, the edges are those      */
/*  coming from the dummy node going into the nodes of the set of target      */
/*  nodes.                                                                    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  dummyNode   The dummy node of the current graph.          */
/*                  dummySet    Node set to which the dummy node belongs.     */
/*                  nodeSet     Adjust edge capacities to/from these nodes    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  AdjustDummyCapacities  (Snode dummyNode, NodeSet dummySet, Slist nodeSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  Sedge  currEdge  = NULL;
  
  /****************************************************************************/
  /*  Zero the capacities of the proper edges between dummy and the node set  */
  /****************************************************************************/
  
  for_slist (nodeSet, currEntry)
  {
    if (dummySet == LeftSet)
    {
      /* dummyNode is a Source: Search for edges coming from dummyNode        */
      for_targetlist (GET_NODESET_ENTRY (currEntry), currEdge)
      {
        if (currEdge->snode == dummyNode)
        {
          CAPACITY (currEdge) = 0.0;
        }  /* endif */
      }  end_for_targetlist (GET_NODESET_ENTRY (currEntry), currEdge);
    }
    else  /* dummyNode is in the right set ! */
    {
      /* dummyNode is a Target: Search for edges going into dummyNode         */
      for_sourcelist (GET_NODESET_ENTRY (currEntry), currEdge)
      {
        if (currEdge->tnode == dummyNode)
        {
          CAPACITY (currEdge) = 0.0;
        }  /* endif */
      }  end_for_sourcelist (GET_NODESET_ENTRY (currEntry), currEdge);
    }  /* endif */
  }  end_for_slist (nodeSet, currEntry);

}  /* End of AdjustDummyCapacities */

/******************************************************************************/
/*    SetNewCapacity                                                          */
/*----------------------------------------------------------------------------*/
/*  Sets the capacity attribute of each edge of the specified graph to the    */
/*  new Value.                                                                */
/*  This excludes the edges to and from the source and target node and all    */
/*  edges with zero capacity, which are edges to be excluded from being cut.  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph     Set the capacity attribute of its edges.    */
/*                  newCapacity   New capacity to be set.                     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  SetNewCapacity (Sgraph currGraph, float newCapacity)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = NULL;
  Sedge  currEdge = NULL;

  /****************************************************************************/
  /*  Set all capacities to their new values.                                 */
  /****************************************************************************/

  MAX_CAPACITY (currGraph) = newCapacity;

  for_all_nodes (currGraph, currNode)
  {
    if (currNode != SOURCE (currGraph))
    {
      for_sourcelist (currNode, currEdge)
      {
        if ((currEdge->tnode != TARGET (currGraph)) &&
            (CAPACITY (currEdge) > 0.0))
        {
          CAPACITY (currEdge) = newCapacity;
        }  /* endif */
      }  end_for_sourcelist (currNode, currEdge);
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);

}  /* End of SetNewCapacity */

/******************************************************************************/
/*    MaxEdgeFlow                                                             */
/*----------------------------------------------------------------------------*/
/*  Computes the maximum value of flow through an edge of the specified graph.*/
/*  This excludes the edges to and from the source and target node.           */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Test the flow attribute of its edges.         */
/*  Return value :  Return the maximum value of the flow through an edge.     */
/******************************************************************************/
Local float  MaxEdgeFlow  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  float  maxValue = 0.0;
  
  /****************************************************************************/
  /*  Search the edge having the maximum flow value.                          */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    if (currNode != SOURCE (currGraph))
    {
      for_sourcelist (currNode, currEdge)
      {
        if ((FLOW (currEdge) > maxValue) &&
            (currEdge->tnode != TARGET (currGraph)))
        {
          maxValue = FLOW (currEdge);
        }  /* endif */
      }  end_for_sourcelist (currNode, currEdge);
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);

  /****************************************************************************/
  /*  Return the found value.                                                 */
  /****************************************************************************/
  
  return  maxValue;
  
}  /* End of MaxEdgeFlow */

/******************************************************************************/
/*    CreateSourceNode                                                        */
/*----------------------------------------------------------------------------*/
/*  Creates a source node for the specified graph which is adjacent to all    */
/*  nodes in the specified node set by an edge of capacity 1.                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph     Create the source node for this graph.      */
/*                  LeftNodeSet   Connect the source node with these nodes.   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CreateSourceNode  (Sgraph currGraph, Slist LeftNodeSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  FFNodeInfoPtr  newNAttr = (FFNodeInfoPtr) malloc (sizeof (FFNodeInfo));
  Snode  sourceNode       = make_node (currGraph, 
                                       make_attr (ATTR_DATA, (char *)newNAttr));
  Slist  currEntry        = empty_slist;
  
  /****************************************************************************/
  /*  Create the source node.                                                 */
  /****************************************************************************/

  ORG_NODE (sourceNode) = NULL;
  SOURCE (currGraph)    = sourceNode;

  /****************************************************************************/
  /*  Create edges between all nodes in the node set and the source node.     */
  /****************************************************************************/
  
  for_slist (LeftNodeSet, currEntry)
  {
    CopyOrgEdge(NULL, sourceNode, GET_NODESET_ENTRY(currEntry), TRUE);
  }  end_for_slist (LeftNodeSet, currEntry);
  
}  /* End of CreateSourceNode */

/******************************************************************************/
/*    CreateTargetNode                                                        */
/*----------------------------------------------------------------------------*/
/*  Creates a target node for the specified graph which is adjacent to all    */
/*  nodes in the specified node set by an edge of capacity 1.                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph       Create the target node for this graph.    */
/*                  RightNodeSet    Connect the target node with these nodes. */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CreateTargetNode  (Sgraph currGraph, Slist RightNodeSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  FFNodeInfoPtr  newNAttr = (FFNodeInfoPtr) malloc (sizeof (FFNodeInfo));
  Snode  targetNode       = make_node (currGraph, 
                                       make_attr (ATTR_DATA, (char *)newNAttr));
  Slist  currEntry        = empty_slist;
  
  /****************************************************************************/
  /*  Create the target node.                                                 */
  /****************************************************************************/

  ORG_NODE (targetNode) = NULL;
  TARGET (currGraph)    = targetNode;

  /****************************************************************************/
  /*  Create edges between all nodes in the node set and the target node.     */
  /****************************************************************************/
  
  for_slist (RightNodeSet, currEntry)
  {
    CopyOrgEdge(NULL, GET_NODESET_ENTRY(currEntry), targetNode, TRUE);
  }  end_for_slist (RightNodeSet, currEntry);
  
}  /* End of CreateTargetNode */

/******************************************************************************/
/*    RemoveSourceTarget                                                      */
/*----------------------------------------------------------------------------*/
/*  Remove the source and the target node of the specified plaisted network.  */
/*  (Including all adjacent edges and all attribute structs of course).       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   A plaisted network                            */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void   RemoveSourceTarget  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge  = NULL;
  
  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  for_sourcelist (SOURCE (currGraph), currEdge)
  {
    free (attr_data_of_type (currEdge, FFEdgeInfoPtr));
  }  end_for_sourcelist (SOURCE (currGraph), currEdge);
  
  for_targetlist (TARGET (currGraph), currEdge)
  {
    free (attr_data_of_type (currEdge, FFEdgeInfoPtr));
  }  end_for_targetlist (TARGET (currGraph), currEdge);
  
  free (attr_data_of_type (SOURCE (currGraph), FFNodeInfoPtr));
  remove_node (SOURCE (currGraph));
  SOURCE (currGraph) = NULL;
  
  free (attr_data_of_type (TARGET (currGraph), FFNodeInfoPtr));
  remove_node (TARGET (currGraph));
  TARGET (currGraph) = NULL;
  
}  /* End of RemoveSourceTarget */

/******************************************************************************/
/*    EpsilonMinCut                                                           */
/*----------------------------------------------------------------------------*/
/*  Computes the node set partition and the set of separating edges resulting */
/*  from the min cut of the flow problem given by currGraph.                  */
/*  The flow problem has a max flow of value |V| / 2 (where |V| is the number */
/*  of nodes in the graph). A result of the Plaisted paper is, that with the  */
/*  edge capacities minimized, the saturated edges of currGraph, without the  */
/*  edges from the source node and the edges to the target node, disconnect   */
/*  the graph. According to this observation, the node sets and the sepa-     */
/*  rating edge set is computed. To separate, all flow along the edges of the */
/*  edge set must go in one direction.                                        */
/*  The output sets contain only elements of the original graph!              */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   A plaisted graph (each edge going in both d.) */
/*                  NodeSet_A   Left side of the cut.                         */
/*                  NodeSet_B   Right side of the cut.                        */
/*                  SepSet      List of edges disconnecting the graph.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  EpsilonMinCut  (Sgraph currGraph, Slist *NodeSet_A, Slist *NodeSet_B, Slist *SepSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Compute the original edges separating the graph and initialize it.      */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if (ORG_EDGE (currEdge) != NULL)
      {
        if (FLOW (currEdge) >= CAPACITY (currEdge))  /* Numeric problems ? */
        {
          *SepSet = add_immediately_to_slist (*SepSet, 
                      CREATE_SEPARATOR_ENTRY (ORG_EDGE (currEdge)));
          PART_OF_EDGE_SET (ORG_EDGE (currEdge)) = Separator_Set;
        }  /* endif */
      }  /* endif */
    }  end_for_sourcelist (currNode, currEdge);
    
    if (ORG_NODE (currNode) != NULL)
    {
      IS_NODE_VISITED (ORG_NODE (currNode))  = FALSE;
      PART_OF_NODE_SET (ORG_NODE (currNode)) = NodeNone;
    }  /* endif */
    
  }  end_for_all_nodes (currGraph, currNode);

  /****************************************************************************/
  /*  Assign all nodes of the original graph to their node sets.              */
  /****************************************************************************/
  
  AssignOrgNodes (*SepSet, NodeSet_A, NodeSet_B);

}  /* End of EpsilonMinCut */

/******************************************************************************/
/*    AssignOrgNodes                                                          */
/*----------------------------------------------------------------------------*/
/*  Recursivly assign all nodes of the original graph to the node sets, which */
/*  are induced be the set of separating edges.                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  SepSet      List of edges disconnecting the graph.        */
/*                  NodeSet_A   Left side of the cut.                         */
/*                  NodeSet_B   Right side of the cut.                        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  AssignOrgNodes  (Slist SepSet, Slist *NodeSet_A, Slist *NodeSet_B)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist    currEntry = empty_slist;
  NodeSet  SourceSet = NodeNone;
  NodeSet  TargetSet = NodeNone;
  
  /****************************************************************************/
  /*  Assign all nodes of the original graph to their node sets.              */
  /****************************************************************************/
  
  for_slist (SepSet, currEntry)
  {
    SourceSet = VALID_NODE_SET(GET_SEP_SNODE(currEntry));
    TargetSet = VALID_NODE_SET(GET_SEP_TNODE(currEntry));

    if ((SourceSet == NodeNone) && (TargetSet == NodeNone))
    {
      ScanIntoNodeSet (GET_SEP_SNODE(currEntry), LeftSet, NodeSet_A);
      ScanIntoNodeSet (GET_SEP_TNODE(currEntry), RightSet, NodeSet_B);
    }
    else if ((SourceSet != NodeNone) && (TargetSet == NodeNone))
    {
      if (SourceSet == RightSet)
      {
        ScanIntoNodeSet (GET_SEP_TNODE(currEntry), LeftSet, NodeSet_A);
      }
      else
      {
        ScanIntoNodeSet (GET_SEP_TNODE(currEntry), RightSet, NodeSet_B);
      }  /* endif */
    }
    else if ((TargetSet != NodeNone) && (SourceSet == NodeNone))
    {
      if (TargetSet == RightSet)
      {
        ScanIntoNodeSet (GET_SEP_SNODE(currEntry), LeftSet, NodeSet_A);
      }
      else
      {
        ScanIntoNodeSet (GET_SEP_SNODE(currEntry), RightSet, NodeSet_B);
      }  /* endif */
    }  /* endif */
  }  end_for_slist (SepSet, currEntry);

}  /* End of AssignOrgNodes */

/******************************************************************************/
/*     ScanIntoNodeSet                                                        */
/*----------------------------------------------------------------------------*/
/*  Recursivly assigns nodes - beginning with startNode - to currSet          */
/*  dfs-like.                                                                 */
/*  Expansion does not cross separator edges, therefore the EdgeSet values of */
/*  separator edges MUST be Separator_Set.                                    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  startNode  Node to expand                                 */
/*                  currSet    Set where the expandable nodes are assigned to */
/*                  thisSet    The node set to put the nodes in.              */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ScanIntoNodeSet  (Snode startNode, NodeSet currSet, Slist *thisSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Expand from the specified node if neccessary                            */
  /****************************************************************************/

  if (IS_NODE_VISITED(startNode))
  {
    return;       /* The current node was already expanded                    */
  }
  else  /* Expand the current node */
  {
    /* Mark the current node as visited and put it into the current node set  */
    IS_NODE_VISITED(startNode)  = TRUE;
    PART_OF_NODE_SET(startNode) = currSet;
    *thisSet                    = add_immediately_to_slist (*thisSet, 
                                    CREATE_NODESET_ENTRY (startNode));
    
    /* Expand all adjacent node (undirected) or all starting nodes (directed) */
    for_sourcelist (startNode, currEdge)
    {
      if (PART_OF_EDGE_SET(currEdge) != Separator_Set)
      {
        ScanIntoNodeSet (NEXT_NODE (startNode, currEdge), currSet, thisSet);
      } /* endif */
    }  end_for_sourcelist (startNode, currEdge);

    /* Handle the directed case                                               */
    if (startNode->graph->directed)
    {
      for_targetlist (startNode, currEdge)
      {
        if (PART_OF_EDGE_SET(currEdge) != Separator_Set)
        {
          ScanIntoNodeSet (NEXT_NODE (startNode, currEdge), currSet, thisSet);
        } /* endif */
      }  end_for_targetlist (startNode, currEdge);
    }  /* endif */
  }  /* endif */
}  /* End of ScanIntoNodeSet */

/******************************************************************************/
/*  End of  Plaisted.c                                                        */
/******************************************************************************/
