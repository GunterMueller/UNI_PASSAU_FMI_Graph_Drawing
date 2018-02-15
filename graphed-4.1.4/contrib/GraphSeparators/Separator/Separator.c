/******************************************************************************/
/*                                                                            */
/*    Separator.c                                                             */
/*                                                                            */
/******************************************************************************/
/*  Functions to support the separator-algorithms.                            */
/*  Generally needed functions to manage the attribute structures.            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  16.02.1994                                                    */
/*  Modified :  26.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              2.3         Select flags for IncreaseDegree.                  */
/*              2.1         Support for ANSI Prototypes.                      */
/*              2.0         Restructuring and addition of AlgorithmInfo.      */
/*              1.2         Attributes for contracted nodes and edges added.  */
/*              1.1         The original attributes of the graph are saved.   */
/*              1.0         First Revision                                    */
/******************************************************************************/

#include "Separator.h"
#include "Interface.h"
#include <sgraph/random.h>
#include <sgraph/graphed_structures.h>


/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef ANSI_HEADERS_OFF
Local void  InitialDValueOfNode  (/*currNode*/);
Local void  ComputeDValue        (/*currNode, currEdge*/);
Local void  UpdateDValues        (/*Snode  pairNode*/);
Local void  ComputeNewDValue     (/*pairNode, currNode, currEdge*/);
#endif

#ifdef ANSI_HEADERS_ON
Local void  InitialDValueOfNode  (Snode  currNode);
Local void  ComputeDValue        (Snode  currNode, Sedge  currEdge);
Local void  UpdateDValues        (Snode  pairNode);
Local void  ComputeNewDValue     (Snode  pairNode, 
                                  Snode  currNode, 
                                  Sedge  currEdge);
#endif

/******************************************************************************/
/*  Miscellaneous functions                                                   */
/******************************************************************************/

/******************************************************************************/
/*  InitAttributes                                                            */
/*----------------------------------------------------------------------------*/
/*  Initializes the graph, node and edge attributes of the specified graph.   */
/*  If validAttributes is FALSE, existing attributes are saved, the needed    */
/*  structures are then created and filled with default values. Else only the */
/*  default values for the marker fields (needed in the Separator Algorithms) */
/*  are set - e.g. the weight of nodes and edges remain untouched.            */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph        The graph to initialize                  */
/*                  validAttributes  Initialize only the marker fields        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InitAttributes  (Sgraph currGraph, bool validAttributes)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode        currNode      = NULL;
  Sedge        currEdge      = NULL;  
  GraphInfoPtr currGraphAttr = NULL;
  NodeInfoPtr  currNodeAttr  = NULL;
  EdgeInfoPtr  currEdgeAttr  = NULL;

  /****************************************************************************/
  /*  Initialize the Attributes structure of the graph                        */
  /****************************************************************************/

  if (!validAttributes)
  {
    currGraphAttr             = (GraphInfoPtr) malloc (sizeof (GraphInfo));
    currGraphAttr->attrs      = currGraph->attrs;
    currGraphAttr->numOfNodes = 0; 
    set_graphattrs (currGraph, make_attr (ATTR_DATA, (char *)currGraphAttr));
  }  /* endif */

  /****************************************************************************/
  /*  Initialize the Attributes structures of the nodes and edges             */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    if (!validAttributes)
    {
      currNodeAttr = (NodeInfoPtr) malloc (sizeof (NodeInfo));
      currNodeAttr->attrs = make_attr (ATTR_DATA, currNode->attrs.value.data);
      set_nodeattrs (currNode, make_attr(ATTR_DATA, (char *) currNodeAttr));
      NODE_REF (currNode) = NULL;
    }  /* endif */

    IS_NODE_VISITED (currNode)   = FALSE;
    PART_OF_NODE_SET (currNode)  = NodeNone;
    VALID_NODE_SET (currNode)    = NodeNone;
    D_VALUE (currNode)           = 0.0;
    IS_DUMMY (currNode)          = FALSE;
    SET_ENTRY (currNode)         = empty_slist;
    IS_CONTRACTED (currNode)     = FALSE;
    CONT_INFO(currNode)          = NULL;
    FLAGS(currNode)              = NUM_GRAPH_NODES(currGraph)++;

    for_sourcelist (currNode, currEdge)
    {
      if (currGraph->directed || unique_edge (currEdge))
      {
        if (!validAttributes)
        {
          currEdgeAttr = (EdgeInfoPtr) malloc (sizeof (EdgeInfo));
          currEdgeAttr->attrs = currEdge->attrs;
          set_edgeattrs (currEdge, make_attr(ATTR_DATA, (char *) currEdgeAttr));
          EDGE_WEIGHT(currEdge) = 1.0;
        }  /* endif */

        IS_EDGE_VISITED (currEdge)   = FALSE;
        PART_OF_EDGE_SET (currEdge)  = EdgeNone;
        VALID_EDGE_SET (currEdge)    = EdgeNone;
        SOURCE_NUMBER (currEdge)     = -1;
        TARGET_NUMBER (currEdge)     = -1;
        MULTI_LIST (currEdge)        = empty_slist;
      }  /* endif */
    } end_for_sourcelist (currNode, currEdge);
  } end_for_all_nodes (currGraph, currNode);
}  /* End of InitAttributes */

/******************************************************************************/
/*  RemoveAttributes                                                          */
/*----------------------------------------------------------------------------*/
/*  Removes ALL (including nodes and edges) Attribute structures from the     */
/*  specified graph.                                                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph        The graph to clean up                    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  RemoveAttributes  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode       currNode = NULL;
  Sedge       currEdge = NULL;  
  Attributes  orgAttribute;

  /****************************************************************************/
  /*  Remove the Attributes structure of the graph                            */
  /****************************************************************************/

  if (attr_data(currGraph) != NULL)
  {
    orgAttribute = make_attr (ATTR_DATA, ORG_GRAPH_ATTR (currGraph));
    free (attr_data_of_type (currGraph, GraphInfoPtr));
  }  /* endif */
  set_graphattrs (currGraph, orgAttribute);

  /****************************************************************************/
  /*  Remove the Attributes structures of the nodes and edges                 */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    if (attr_data(currNode) != NULL)
    {
      orgAttribute = make_attr (ATTR_DATA, ORG_NODE_ATTR (currNode));
      free (attr_data_of_type (currNode, NodeInfoPtr));
    }  /* endif */
    set_nodeattrs (currNode, orgAttribute);

    for_sourcelist (currNode, currEdge)
    {
      if (currGraph->directed || unique_edge (currEdge))
      {
        if (attr_data(currEdge) != NULL)
        {
          orgAttribute = make_attr (ATTR_DATA, ORG_NODE_ATTR (currEdge));
          free (attr_data_of_type (currEdge, EdgeInfoPtr));
        }  /* endif */
        set_edgeattrs (currEdge, orgAttribute);
      }  /* endif */
    } end_for_sourcelist (currNode, currEdge);
  } end_for_all_nodes (currGraph, currNode);
}  /* End of RemoveAttributes */

/******************************************************************************/
/*  InitNodeMarkers                                                           */
/*----------------------------------------------------------------------------*/
/*  Sets the marker entries of all nodes of the graph to None (ValidSet) or   */
/*  FALSE (Visited).                                                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph        The graph which contains the nodes       */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InitNodeMarkers  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode currNode = NULL;

  /****************************************************************************/
  /*  Initialize all node markers                                             */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    IS_NODE_VISITED(currNode)   = FALSE;
    IS_DUMMY(currNode)          = FALSE;
    VALID_NODE_SET(currNode)    = NodeNone;
  } end_for_all_nodes (currGraph, currNode);
}  /* End of InitNodeMarkers */

/******************************************************************************/
/*  InitEdgeMarkers                                                           */
/*----------------------------------------------------------------------------*/
/*  Sets the marker entries of all edges of the graph to their default values.*/
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph        The graph which contains the edges       */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InitEdgeMarkers  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode currNode = NULL;
  Sedge currEdge = NULL;

  /****************************************************************************/
  /*  Initialize all node markers                                             */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      IS_EDGE_VISITED (currEdge) = FALSE;
      VALID_EDGE_SET(currEdge)   = EdgeNone;
    }  end_for_sourcelist (currNode, currEdge);
  } end_for_all_nodes (currGraph, currNode);
}  /* End of InitEdgeMarkers */

/******************************************************************************/
/*    InitialPartition                                                        */
/*----------------------------------------------------------------------------*/
/*  Computes an initial partition of the nodes of the specified graph into    */
/*  two sets such that the first set is of size maxASet.                      */
/*  The NodeSets MUST be initially empty!                                     */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   The graph to partition                        */
/*                  NodeSet_A   First subset of the graphs nodes              */
/*                  NodeSet_B   Second subset of the graphs nodes             */
/*                  maxASet     Maximum number of nodes for NodeSet_A         */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InitialPartition  (Sgraph currGraph, Slist *NodeSet_A, Slist *NodeSet_B, int maxASet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode  = NULL;
  int    counter   = 0;
  
  /****************************************************************************/
  /*  Distribute the nodes into the two node sets by assigning them in turn   */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    if (counter < maxASet)
    {
      PART_OF_NODE_SET (currNode) = LeftSet;
      VALID_NODE_SET (currNode)   = LeftSet;
      *NodeSet_A = add_immediately_to_slist (*NodeSet_A, 
                                             CREATE_NODESET_ENTRY(currNode));
      SET_ENTRY(currNode) = (*NodeSet_A)->pre; /* As in sgraph manual !       */
    }
    else  /* Its the other node sets turn */
    {
      PART_OF_NODE_SET (currNode) = RightSet;
      VALID_NODE_SET (currNode)   = RightSet;
      *NodeSet_B = add_immediately_to_slist (*NodeSet_B, 
                                             CREATE_NODESET_ENTRY(currNode));
      SET_ENTRY(currNode) = (*NodeSet_B)->pre; /* As in sgraph manual !       */
    }  /* endif */
    counter++;
  } end_for_all_nodes (currGraph, currNode);
}  /* End of InitialPartition */

/******************************************************************************/
/*    RandomInitialPartition                                                  */
/*----------------------------------------------------------------------------*/
/*  Randomly assigns the nodes of the specified graph to the two node sets.   */
/*  The bigger set is of at most maxSize, which therefor MUST be greater than */
/*  half the number of nodes. Also the node sets MUST be empty when calling.  */
/*                                                                            */
/*  To do: Use a better random number generator than the standard C one.      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph     Partition the nodes of this graph.          */
/*                  LeftNodeSet   First node set.                             */
/*                  RightNodeSet  Second node set.                            */
/*                  maxSize       Maximum size of the bigger of the node sets */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  RandomInitialPartition  (Sgraph currGraph, 
                                      Slist *LeftNodeSet, 
                                      Slist *RightNodeSet, 
                                      int maxSize)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode  = NULL;
  int    LeftSize  = 0;
  int    RightSize = 0;
  
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
        PART_OF_NODE_SET (currNode) = LeftSet;
        VALID_NODE_SET (currNode)   = LeftSet;
        SET_ENTRY(currNode)         = (*LeftNodeSet)->pre;
        LeftSize++;
      }
      else
      {
        *RightNodeSet = add_immediately_to_slist (*RightNodeSet, 
                          CREATE_NODESET_ENTRY (currNode));
        PART_OF_NODE_SET (currNode) = RightSet;
        VALID_NODE_SET (currNode)   = RightSet;
        SET_ENTRY(currNode)         = (*RightNodeSet)->pre;
        RightSize++;
      }  /* endif */
    }
    else if (LeftSize < maxSize)
    {
      *LeftNodeSet = add_immediately_to_slist (*LeftNodeSet,
                        CREATE_NODESET_ENTRY (currNode));
      PART_OF_NODE_SET (currNode) = LeftSet;
      VALID_NODE_SET (currNode)   = LeftSet;
      SET_ENTRY(currNode)         = (*LeftNodeSet)->pre;
      LeftSize++;
    }
    else
    {
      *RightNodeSet = add_immediately_to_slist (*RightNodeSet, 
                        CREATE_NODESET_ENTRY (currNode));
      PART_OF_NODE_SET (currNode) = RightSet;
      VALID_NODE_SET (currNode)   = RightSet;
      SET_ENTRY(currNode)         = (*RightNodeSet)->pre;
      RightSize++;
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);

}  /* End of RandomInitialPartition */

/******************************************************************************/
/*    ComputeNodeSetSizes                                                     */
/*----------------------------------------------------------------------------*/
/*  Computes the sizes of the node sets if the alpha condition is fulfilled.  */
/*  maxBSize is ALWAYS greater than maxASize!                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  graphSize   Number of nodes in the graph                  */
/*                  maxASize    Size of the smaller set                       */
/*                  maxBSize    Size of the bigger set                        */
/*                  alpha       max(|A|,|B|) <= alpha*(|A|+|B|), 0.5<=alpha<1 */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  ComputeNodeSetSizes  (int graphSize, int *maxASize, int *maxBSize, float alpha)
{
    *maxBSize = (graphSize > 1) ? (int) floor((double)alpha*(double)graphSize) :
                                  graphSize;
    *maxASize = graphSize - *maxBSize;
}  /* End of ComputeNodeSetSizes */

/******************************************************************************/
/*    InsertDummys                                                            */
/*----------------------------------------------------------------------------*/
/*  Creates the specified number of dummy nodes for the graph.                */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   The graph to partition                        */
/*                  noAddNodes  Number of dummy nodes to add to nodeSet.      */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InsertDummys  (Sgraph currGraph, int noAddNodes)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode        newNode = NULL;
  int          counter = 0;
  NodeInfoPtr  newAttr = NULL;
  
  /****************************************************************************/
  /*  Add dummy nodes to the graph                                            */
  /****************************************************************************/
  
  for (counter = 0; counter < noAddNodes; counter++)
  {
    /* Create an attribute structure for the new dummy node                   */
    newAttr = (NodeInfoPtr) malloc (sizeof (NodeInfo));
    newNode = make_node (currGraph, make_attr (ATTR_DATA, (char *) newAttr));
    NODE_REF(newNode)          = NULL;
    IS_NODE_VISITED(newNode)   = FALSE;
    PART_OF_NODE_SET(newNode)  = NodeNone;
    VALID_NODE_SET(newNode)    = NodeNone;
    D_VALUE(newNode)           = 0.0;
    IS_DUMMY(newNode)          = TRUE;
    SET_ENTRY(newNode)         = NULL;
    IS_CONTRACTED(newNode)     = FALSE;
    CONT_INFO(newNode)         = NULL;
    FLAGS(newNode)             = 0;
  }  /* endfor */ 
  
  NUM_GRAPH_NODES (currGraph) += noAddNodes;
  
}  /* End of InsertDummys */

/******************************************************************************/
/*    InsertDummysInNodeSet                                                   */
/*----------------------------------------------------------------------------*/
/*  Creates the specified number of dummy nodes for the the node set.         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo     Graph and a valid node partition.             */
/*                  noAddNodes  Number of dummy nodes to add to nodeSet.      */
/*                  inSet       Add the dummies to this set.                  */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InsertDummysInNodeSet  (GSAlgInfoPtr algInfo, int noAddNodes, NodeSet inSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Slist        toSet   = inSet == LeftSet ? A_SET (algInfo) : B_SET (algInfo);
  Snode        newNode = NULL;
  int          counter = 0;
  NodeInfoPtr  newAttr = NULL;
  
  /****************************************************************************/
  /*  Add dummy nodes to the graph                                            */
  /****************************************************************************/
  
  for (counter = 0; counter < noAddNodes; counter++)
  {
    /* Create an attribute structure for the new dummy node                   */
    newAttr                    = (NodeInfoPtr) malloc (sizeof (NodeInfo));
    newNode                    = make_node (GRAPH (algInfo), 
                                   make_attr (ATTR_DATA, (char *) newAttr));
    NODE_REF(newNode)          = NULL;
    IS_NODE_VISITED(newNode)   = FALSE;
    D_VALUE(newNode)           = 0.0;
    IS_DUMMY(newNode)          = TRUE;
    IS_CONTRACTED(newNode)     = FALSE;
    CONT_INFO(newNode)         = NULL;
    FLAGS(newNode)             = 0;
    
    /* Put the new dummy in its node set.                                     */
    toSet = add_immediately_to_slist (toSet, CREATE_NODESET_ENTRY (newNode));
    SET_ENTRY(newNode)        = toSet->pre;
    PART_OF_NODE_SET(newNode) = inSet;
    VALID_NODE_SET(newNode)   = inSet;
  }  /* endfor */ 
  
  /****************************************************************************/
  /*  Update the partition information and the graph size.                    */
  /****************************************************************************/
  
  if (inSet == LeftSet)
  {
    A_SET (algInfo) = toSet;
  }
  else
  {
    B_SET (algInfo) = toSet;
  }  /* endif */

  NUM_GRAPH_NODES (GRAPH (algInfo)) += noAddNodes;
  
}  /* End of InsertDummysInNodeSet */

/******************************************************************************/
/*    RemoveDummys                                                            */
/*----------------------------------------------------------------------------*/
/*  Removes the nodes with the dummy attribute set from the graph.            */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph       Graph possibly containing dummy nodes.    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  RemoveDummys  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = NULL;
  
  /****************************************************************************/
  /*  Remove all nodes with the dummy attribute set from the graph.           */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    if (IS_DUMMY (currNode))
    {
      free (attr_data_of_type(currNode, NodeInfoPtr));
      remove_node (currNode);
      NUM_GRAPH_NODES (currGraph)--;
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);
  
}  /* End of RemoveDummys */

/******************************************************************************/
/*    RemoveDummysFromNodeSet                                                 */
/*----------------------------------------------------------------------------*/
/*  Removes the nodes with the dummy attribute set from the given node set.   */
/*  Also removes the nodes from the graph.                                    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currSet        Slist possibly containing dummy nodes.     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  RemoveDummysFromNodeSet  (Slist *currSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  Slist  start     = empty_slist;
  Slist  next      = empty_slist;
  
  /****************************************************************************/
  /*  Search the first non dummy node.                                        */
  /****************************************************************************/

  for_slist (*currSet, currEntry)
  {
    if (!IS_DUMMY (GET_NODESET_ENTRY (currEntry)))
    {
      start = currEntry;
      break;
    }  /* endif */
  }  end_for_slist (*currSet, currEntry);

  /****************************************************************************/
  /*  Remove nodes with the dummy attribute set from the node set and graph.  */
  /****************************************************************************/

  if (start != empty_slist)
  {
    currEntry = start;
  
    do
    {
      next = currEntry->suc;
  
      if (IS_DUMMY (GET_NODESET_ENTRY (currEntry)))
      {
        NUM_GRAPH_NODES (GET_NODESET_ENTRY (currEntry)->graph)--;
        free (attr_data_of_type (GET_NODESET_ENTRY (currEntry), NodeInfoPtr));
        remove_node (GET_NODESET_ENTRY (currEntry));
        currEntry->suc->pre = currEntry->pre;
        currEntry->pre->suc = currEntry->suc;
        next = currEntry->suc;
        free (currEntry);
      }  /* endif */
  
      currEntry = next;
    }  while (currEntry != start);
  
    *currSet = start;
  }
  else  /* There are only dummy-nodes in the list */
  {
    for_slist (*currSet, currEntry);
    {
        NUM_GRAPH_NODES (GET_NODESET_ENTRY (currEntry)->graph)--;
        free (attr_data_of_type (GET_NODESET_ENTRY (currEntry), NodeInfoPtr));
        remove_node (GET_NODESET_ENTRY (currEntry));      
    }  end_for_slist (*currSet, currEntry);
    
    free_slist (*currSet);
    *currSet = empty_slist;
  }  /* endif */
  
}  /* End of RemoveDummysFromNodeSet */

/******************************************************************************/
/*    InitialDValues                                                          */
/*----------------------------------------------------------------------------*/
/*  Computes the D-values of the initial partition of the graph established   */
/*  by the PART_OF_SET attribute, which MUST be set for EVERY node.           */
/*  The D-value of a node is the difference between external an internal      */
/*  cost of the node, where the external cost is the sum of the weights of    */
/*  edges going into the other node set and the internal cost is the sum of   */
/*  the weights of edges staying in the node set of the current node.         */
/*  The cost of an edge is given by the weight attribute of a sedge, which    */
/*  MUST be set to 1.0 for unweighted graphs.                                 */
/*  As a sideeffect, the edges are assigned to their sets by setting the      */
/*  attributes to A_Set, B_Set or SeparatorSet.                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   The initially partitioned graph               */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InitialDValues  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode  = NULL;
  
  /****************************************************************************/
  /*  Update the D-value of all nodes.                                        */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    InitialDValueOfNode (currNode);
  } end_for_all_nodes (currGraph, currNode);
}  /* End of InitialDValues */

/******************************************************************************/
/*    InitialDValueOfNode                                                     */
/*----------------------------------------------------------------------------*/
/*  Computes the D-values of the specified node according to the assignment   */
/*  by the PART_OF_SET attribute, which MUST be set for EVERY node.           */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode   Node to compute the d-value of.                */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  InitialDValueOfNode  (Snode currNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Sedge  currEdge  = NULL;
  
  /****************************************************************************/
  /*  Update the D-value of the node by considering all adjacent edges        */
  /****************************************************************************/
  
  IS_NODE_VISITED (currNode) = FALSE;
  D_VALUE (currNode)         = 0.0;
  
  for_sourcelist (currNode, currEdge)
  {
    ComputeDValue (currNode, currEdge);
  }  end_for_sourcelist (currNode, currEdge);
  
  if (currNode->graph->directed)
  {
    for_targetlist (currNode, currEdge)
    {
      ComputeDValue (currNode, currEdge);
    }  end_for_targetlist (currNode, currEdge);
  }  /* endif */
    
}  /* End of InitialDValueOfNode */

/******************************************************************************/
/*    ComputeDValue                                                           */
/*----------------------------------------------------------------------------*/
/*  Computes the D-values of the specified node according to the edge.        */
/*  The D-value of a node is the difference between external an internal      */
/*  cost of the node, where the external cost is the sum of the weights of    */
/*  edges going into the other node set and the internal cost is the sum of   */
/*  the weights of edges staying in the node set of the current node.         */
/*  The cost of an edge is given by the weight attribute of a sedge, which    */
/*  MUST be set to 1.0 for unweighted graphs.                                 */
/*  As a sideeffect, the edge is assigned to its set by setting the           */
/*  attribute to A_Set, B_Set or SeparatorSet.                                */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode    Update its D-value.                           */
/*                  currEdge    Determines the increase or decrease of D.     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ComputeDValue  (Snode currNode,
                            Sedge currEdge)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  nextNode  = NEXT_NODE (currNode, currEdge);
                                                     
  /****************************************************************************/
  /*  Update the D-value according to the Set-value of the next node          */
  /****************************************************************************/

  if (PART_OF_NODE_SET (currNode) == PART_OF_NODE_SET (nextNode))
  {
    D_VALUE (currNode) = D_VALUE (currNode) - EDGE_WEIGHT (currEdge);
    PART_OF_EDGE_SET (currEdge) = 
      (PART_OF_NODE_SET (currNode) == LeftSet) ? A_Set : B_Set;
  }
  else  /* external ! */
  {
    D_VALUE (currNode) = D_VALUE (currNode) + EDGE_WEIGHT (currEdge);
    PART_OF_EDGE_SET (currEdge) = Separator_Set;
  }  /* endif */

}  /* End of ComputeDValue */

/******************************************************************************/
/*    ExchangeOnePair                                                         */
/*----------------------------------------------------------------------------*/
/*  Performs the exchange operation with the two specified set entries,       */
/*  including update of the D-values.                                         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  A_Entry     Entry in the left set to exchange.            */
/*                  B_Entry     Entry in the right set to exchange.           */
/*                  NodeSet_A   First subset of the graphs nodes              */
/*                  NodeSet_B   Second subset of the graphs nodes             */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  ExchangeOnePair  (Slist A_Entry, Slist B_Entry, Slist *NodeSet_A, Slist *NodeSet_B)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  A_node = GET_NODESET_ENTRY(A_Entry);
  Snode  B_node = GET_NODESET_ENTRY(B_Entry);
  
  /****************************************************************************/
  /*  Recompute the D-value for the two nodes and all adjacent nodes.         */
  /****************************************************************************/

  UpdateDValues (A_node);
  UpdateDValues (B_node);

  /****************************************************************************/
  /* Remove the pair nodes from their node sets                               */
  /****************************************************************************/

  *NodeSet_A = subtract_immediately_from_slist (*NodeSet_A, A_Entry);
  *NodeSet_B = subtract_immediately_from_slist (*NodeSet_B, B_Entry);

  /****************************************************************************/
  /* Put the former element of the right set into the left set                */
  /****************************************************************************/
  *NodeSet_A = add_immediately_to_slist (*NodeSet_A, 
                  CREATE_NODESET_ENTRY(B_node));
  SET_ENTRY (B_node)        = (*NodeSet_A)->pre;
  PART_OF_NODE_SET (B_node) = LeftSet;

  /****************************************************************************/
  /* Put the former element of the left set into the right set                */
  /****************************************************************************/

  *NodeSet_B = add_immediately_to_slist (*NodeSet_B, 
                  CREATE_NODESET_ENTRY(A_node));
  SET_ENTRY (A_node)        = (*NodeSet_B)->pre;
  PART_OF_NODE_SET (A_node) = RightSet;

  /****************************************************************************/
  /*  Re-initialize the D-values of the exchanged nodes.                      */
  /****************************************************************************/

  InitialDValueOfNode (A_node);
  InitialDValueOfNode (B_node);

}  /* End of ExchangeOnePair */

/******************************************************************************/
/*    UpdateDValues                                                           */
/*----------------------------------------------------------------------------*/
/*  Recomputes the D_values for nodes which are adjacent to the specified     */
/*  node, simulating the exchange operation using this node.                  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  pairNode   Node changing node set disturbing D-values     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  UpdateDValues  (Snode pairNode)
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
    ComputeNewDValue (pairNode, NEXT_NODE (pairNode, currEdge), currEdge);
  }  end_for_sourcelist (pairNode, currEdge);
  
  /* Handle the directed case                                                 */
  if (pairNode->graph->directed)
  {
    for_targetlist (pairNode, currEdge)
    {
      ComputeNewDValue (pairNode, NEXT_NODE (pairNode, currEdge), currEdge);
    }  end_for_targetlist (pairNode, currEdge);
  }  /* endif */
}  /* End of UpdateDValues */

/******************************************************************************/
/*    ComputeNewDValue                                                        */
/*----------------------------------------------------------------------------*/
/*  Simulate moving pairNode to the other node set by updating the D-value of */
/*  currNode and of the pairNode accordingly.                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  pairNode   Simulate moving this node into the other set   */
/*                  currNode   Node possibly needing update of its D-value    */
/*                  currEdge   Edge between pairNode and currNode             */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  ComputeNewDValue  (Snode pairNode, Snode currNode, Sedge currEdge)
{
  /****************************************************************************/
  /*  Update the D-value of currNode                                          */
  /****************************************************************************/

  if (PART_OF_NODE_SET (pairNode) == PART_OF_NODE_SET (currNode))
  {
    D_VALUE (currNode) = D_VALUE (currNode) + 2.0 * EDGE_WEIGHT (currEdge);
    D_VALUE (pairNode) = D_VALUE (pairNode) + 2.0 * EDGE_WEIGHT (currEdge);
    PART_OF_EDGE_SET (currEdge) = Separator_Set;
  }
  else  /* Simulate moving currNode into the same set as nextNode             */
  {
    D_VALUE (currNode) = D_VALUE (currNode) - 2.0 * EDGE_WEIGHT (currEdge);
    D_VALUE (pairNode) = D_VALUE (pairNode) - 2.0 * EDGE_WEIGHT (currEdge);
    PART_OF_EDGE_SET (currEdge) = 
      (PART_OF_NODE_SET (currNode) == LeftSet) ? A_Set : B_Set;
  }  /*endif */
}  /* End of ComputeNewDValue */

/******************************************************************************/
/*    ComputeMaxElem                                                          */
/*----------------------------------------------------------------------------*/
/*  Computes the entries in the specified node set with the biggest D-value.  */
/*  D-value.                                                                  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNodeSet   Node set with D-values in the attributes    */
/*                  D_max         Maximum D-value in the node set             */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  ComputeMaxElem (Slist currNodeSet, Slist *D_max)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  
  /****************************************************************************/
  /*  Initialize the D-value then test the entries of the node set            */
  /****************************************************************************/
  
  *D_max = currNodeSet;

  for_slist (currNodeSet, currEntry)
  {
    if (D_VALUE (GET_NODESET_ENTRY (currEntry)) > 
        D_VALUE (GET_NODESET_ENTRY (*D_max)))
    {
      *D_max = currEntry;
    }  /* endif */    
  }  end_for_slist (currNodeSet, currEntry);
}  /* End of ComputeMaxElem */

/******************************************************************************/
/*    PairEdgeWeight                                                          */
/*----------------------------------------------------------------------------*/
/*  Computes the sum of the edge-weights between the specified nodes.         */
/*  The graph should NOT contain self-loops.                                  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  ANode   First Node of the node pair to test               */
/*                  ANode   Second Node of the node pair to test              */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global float  PairEdgeWeight (Snode ANode, Snode BNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  pairEdge        = NULL;
  float  pairEdgeWeights = 0.0;
  
  /****************************************************************************/
  /*  Now compute the edge (if exists) between the two nodes                  */
  /****************************************************************************/

  for_sourcelist (ANode, pairEdge)
  {
    if (NEXT_NODE (ANode, pairEdge) == BNode)
    {
      pairEdgeWeights +=  EDGE_WEIGHT (pairEdge);
    }  /* endif */
  }  end_for_sourcelist (ANode, pairEdge);
  
  /* Handle the directed case                                                 */
  if (ANode->graph->directed)
  {
    for_sourcelist (BNode, pairEdge)
    {
      if (NEXT_NODE (BNode, pairEdge) == ANode)
      {
        pairEdgeWeights +=  EDGE_WEIGHT (pairEdge);
      }  /* endif */
    }  end_for_sourcelist (BNode, pairEdge);
  }  /* endif */
  
  /****************************************************************************/
  /*  Return the sum of the weights of all nedges between the two nodes       */
  /****************************************************************************/
  
  return pairEdgeWeights;

}  /* End of PairEdgeWeight */

/******************************************************************************/
/*    ComputeSeparatorEdges                                                   */
/*----------------------------------------------------------------------------*/
/*  Computes the set of edges which disconnect the graph according to the     */
/*  PartOfSet node attribute, which MUST be correctly set.                    */
/*  The Separator set MUST be empty!                                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph  Graph where the node attributes are correct.   */
/*                  Separator  Set of edges to be computed.                   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  ComputeSeparatorEdges  (Sgraph currGraph, Slist *Separator)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Remove a possibly non-empty set.                                        */
  /****************************************************************************/
  
  free_slist (*Separator);
  *Separator = empty_slist;

  /****************************************************************************/
  /*  Compute the set of edges forming the separator set.                     */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if ((currGraph->directed || unique_edge (currEdge)) &&
          (PART_OF_NODE_SET (currNode) != 
           PART_OF_NODE_SET (NEXT_NODE (currNode, currEdge))))
      {
        *Separator = add_immediately_to_slist (*Separator,
                       CREATE_SEPARATOR_ENTRY(currEdge));
      }  /* endif */
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (currGraph, currNode);
  
}  /* End of ComputeSeparatorEdges */

/******************************************************************************/
/*    AnimateAlgorithm                                                        */
/*----------------------------------------------------------------------------*/
/*  SHows the current state of the algorithm (node partition) on Graphed      */
/*  using labels, if the animate flag is set.                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo   Current graph and node partition.               */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  AnimateAlgorithm  (GSAlgInfoPtr algInfo)
{
  /****************************************************************************/
  /*  Show the current partition.                                             */
  /****************************************************************************/

#ifdef ANIMATION_ON

  if (algInfo->animate)
  {
    ClearAndSetLabels (GRAPH (algInfo));
    force_repainting ();
  }  /* endif */
  
#endif

}  /* End of AnimateAlgorithm */

/******************************************************************************/
/*  End of  Separator.c                                                       */
/******************************************************************************/
