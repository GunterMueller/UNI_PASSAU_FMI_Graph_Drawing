/******************************************************************************/
/*                                                                            */
/*    PlaistedStar.c                                                          */
/*                                                                            */
/******************************************************************************/
/*  Compute an a-balanced Edgeseparator using the Plaisted-Heuristic.         */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  27.08.1994                                                    */
/*  Modified :  27.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "Plaisted.h"
#include <AttrsStack.h>

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

struct  _GraphComponent
{
  int      Size;
  Slist    Nodes;
  NodeSet  Side;
};
typedef  struct _GraphComponent   GraphComponent;
typedef  GraphComponent          *GraphComponentPtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  CREATE_GC_ENTRY(c)  (make_attr(ATTR_DATA, (char *)(c)))
#define  GET_GC_ENTRY(c)     (attr_data_of_type((c), GraphComponentPtr))

#define  C_SIZE(c)           (attr_data_of_type((c), GraphComponentPtr)->Size)
#define  C_NODES(c)          (attr_data_of_type((c), GraphComponentPtr)->Nodes)
#define  C_SIDE(c)           (attr_data_of_type((c), GraphComponentPtr)->Side)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local Slist              HandleMultipleComponents    (/*...*/);
Local GraphComponentPtr  NextSmallestComponent       (/*...*/);
Local void               AssignNodeSets              (/*...*/);
Local Sgraph             ComputeInducedSubGraph      (/*...*/);
Local void               CreateInducedNodes          (/*...*/);
Local void               CreateInducedEdges          (/*...*/);
Local void               RemoveSubGraph              (/*...*/);
Local Slist              ComputeConnectedComponents  (/*...*/);
Local GraphComponentPtr  ComputeComponent            (/*...*/);
Local void               RemoveComponentList         (/*...*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local Slist              HandleMultipleComponents    (Slist  *currComponents, 
                                                      Slist  *smallSet, 
                                                      int    *smallSize,
                                                      int     minSize);
Local GraphComponentPtr  NextSmallestComponent       (Slist  *currComponents);
Local void               AssignNodeSets              (Slist   smallSet, 
                                                      Slist  *NodeSet_A, 
                                                      Slist  *NodeSet_B);
Local Sgraph             ComputeInducedSubGraph      (Slist  currNodeSet);
Local void               CreateInducedNodes          (Slist   currNodeSet, 
                                                      Sgraph  newGraph);
Local void               CreateInducedEdges          (Sgraph  newGraph);
Local void               RemoveSubGraph              (Sgraph  currGraph);
Local Slist              ComputeConnectedComponents  (Sgraph  currGraph);
Local GraphComponentPtr  ComputeComponent            (Snode  startNode, 
                                                      float  cNumber);
Local void               RemoveComponentList         (Slist  currComponents);
#endif

/******************************************************************************/
/*  Implementation of the Plaisted* heuristic.                                */
/******************************************************************************/

/******************************************************************************/
/*    PlaistedAStar                                                           */
/*----------------------------------------------------------------------------*/
/*  Computes a 1/3-2/3 balanced separator using the Plaisted heuritic.        */
/*  This is done by successively computing a sequence of graphs and their     */
/*  separators and collecting the smaller sides of the cuts in one node set.  */
/*  When this set is big enough the algorithm terminates.                     */
/*  During the course of the algorithm, the graph can become disconnected, so */
/*  special handling of components is needed.                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   A plaisted graph (each edge going in both d.) */
/*                  NodeSet_A   Left side of the cut.                         */
/*                  NodeSet_B   Right side of the cut.                        */
/*                  SepSet      List of edges disconnecting the graph.        */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  PlaistedAStar  (Sgraph currGraph, Slist *NodeSet_A, Slist *NodeSet_B, Slist *SepSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sgraph  subGraph       = currGraph;
  Sgraph  oldGraph       = empty_sgraph;
  Slist   smallSet       = empty_slist;
  int     smallSize      = 0;
  Slist   currComponents = empty_slist;
  Slist   subSet_A       = empty_slist;
  Slist   subSet_B       = empty_slist;
  Slist   subSep         = empty_slist;
  int     ASize          = 0;
  int     BSize          = 0;
  int     minSize        = (int) ceil ((double) 1/3*NUM_NODES (currGraph));
  Slist   bigSet         = empty_slist;
  
  /****************************************************************************/
  /*  Compute the sequence of graphs with its partitions.                     */
  /****************************************************************************/
  
  do
  {
    currComponents = ComputeConnectedComponents (subGraph);
    
    if (size_of_slist (currComponents) > 1)
    {
      bigSet = HandleMultipleComponents (&currComponents, 
                                         &smallSet, 
                                         &smallSize,
                                         minSize);
                                         
      if (smallSize >= minSize)
      {
        free_slist (bigSet);
        break;
      }  /* endif */
      
      subGraph  = ComputeInducedSubGraph (bigSet);
      free_slist (bigSet);
    }
    else
    {
      RemoveComponentList (currComponents);
      currComponents = empty_slist;
    }  /* endif */
    
    PlaistedA (subGraph, &subSet_A, &subSet_B, &subSep);
    
    ASize    = size_of_slist (subSet_A);
    BSize    = size_of_slist (subSet_B);
    oldGraph = subGraph;
    
    if (ASize < BSize)
    {
      smallSet  = add_slists (smallSet, subSet_A);
      smallSize = smallSize + ASize;
      subGraph  = ComputeInducedSubGraph (subSet_B);
    }
    else
    {
      smallSet  = add_slists (smallSet, subSet_B);
      smallSize = smallSize + BSize;
      subGraph  = ComputeInducedSubGraph (subSet_A);
    }  /*endif */
    
    if (oldGraph != currGraph)
    {
      RemoveSubGraph (oldGraph);
    }  /* endif */
    
    free_slist (subSet_A);
    free_slist (subSet_B);
    free_slist (subSep);
    subSet_A = empty_slist;
    subSet_B = empty_slist;
    subSep   = empty_slist;
      
  }  while (smallSize < minSize);

  /****************************************************************************/
  /* Compute the partition of the nodes in the original graph.                */
  /****************************************************************************/
  
  AssignNodeSets (smallSet, NodeSet_A, NodeSet_B);
  ComputeSeparatorEdges (ORG_GRAPH (currGraph), SepSet);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  if (subGraph != currGraph)
  {
    RemoveSubGraph (subGraph);
  }  /* endif */
  
}  /* End of PlaistedAStar */

/******************************************************************************/
/*    HandleMultipleComponents                                                */
/*----------------------------------------------------------------------------*/
/*  If the graph to partition by the Plaisted heuristic is disconnected (in   */
/*  Plaisted* during execution) the components must be handeled to either     */
/*  make the small side big enough or only one connected component must be    */
/*  further partitioned.                                                      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currComponents   Set of connected components of the graph */
/*                  smallSet         Set of nodes in the smaller node set.    */
/*                  smallSize        Size of the smaller node set.            */
/*                  minSize          Do not grow smallSet beyond this size.   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local Slist HandleMultipleComponents  (Slist *currComponents, 
                                       Slist *smallSet, 
                                       int *smallSize,
                                       int minSize)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist              bigSet        = empty_slist;
  int                noComponents  = size_of_slist (*currComponents);
  GraphComponentPtr  nextComponent = NULL;
  Slist              currEntry     = empty_slist;
  
  /****************************************************************************/
  /*  Add the smallest components to the small side until it is big enough.   */
  /****************************************************************************/
  
  while ((noComponents > 1) && (*smallSize < minSize))
  {
    nextComponent = NextSmallestComponent (currComponents);
    
    for_slist (nextComponent->Nodes, currEntry)
    {
      if (ORG_NODE (currEntry) != empty_snode)
      {
        *smallSet = add_immediately_to_slist (*smallSet, 
          CREATE_NODESET_ENTRY (ORG_NODE (GET_NODESET_ENTRY(currEntry))));
      }  /* endif */
    }  end_for_slist (nextComponent->Nodes, currEntry);
    
    *smallSize = *smallSize + nextComponent->Size;
    noComponents--;
  }  /* endwhile */
  
  /****************************************************************************/
  /*  Clean up the list of components.                                        */
  /****************************************************************************/
  
  bigSet  = copy_slist (C_NODES (*currComponents));
  RemoveComponentList (*currComponents);
  *currComponents = empty_slist;

  /****************************************************************************/
  /*  Return the remaining biggest set (if smallsize < minsize)               */
  /****************************************************************************/
  
  return bigSet;
  
}  /* End of HandleMultipleComponents */

/******************************************************************************/
/*    NextSmallestComponent                                                   */
/*----------------------------------------------------------------------------*/
/*  Removes the currently biggest component entry from the specified list and */
/*  returns the size of the component.                                        */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currComponents   Set of connected components of the graph */
/*  Return value :  The smallest component currently in the set.              */
/******************************************************************************/
Local GraphComponentPtr  NextSmallestComponent  (Slist *currComponents)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist              currEntry      = empty_slist;
  Slist              smallEntry     = *currComponents;
  GraphComponentPtr  smallComponent = NULL;
  
  /****************************************************************************/
  /*  Search the biggest component.                                           */
  /****************************************************************************/
  
  for_slist  (*currComponents, currEntry)
  {
    if (C_SIZE (currEntry) < C_SIZE (smallEntry))
    {
      smallEntry = currEntry;
    }  /* end */
  }  end_for_slist  (*currComponents, currEntry);

  /****************************************************************************/
  /*  Remove the found entry from the list.                                   */
  /****************************************************************************/
  
  smallComponent  = GET_GC_ENTRY (smallEntry);
  *currComponents = subtract_immediately_from_slist(*currComponents,smallEntry);
  
  /****************************************************************************/
  /*  return the smallest component.                                          */
  /****************************************************************************/
  
  return  smallComponent;
  
}  /* End of NextSmallestComponent */

/******************************************************************************/
/*    AssignNodeSets                                                          */
/*----------------------------------------------------------------------------*/
/*  Assigns all nodes of the original graph to the node set computed by the   */
/*  Plaisted* heuristic.                                                      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  smallSet    Set of nodes representing the smaller side.   */
/*                  NodeSet_A   Left side of the partition.                   */
/*                  NodeSet_B   Right side of the partition.                  */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  AssignNodeSets  (Slist smallSet, Slist *NodeSet_A, Slist *NodeSet_B)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  Snode  currNode  = empty_snode;
  
  /****************************************************************************/
  /*  Initialize the set attribute flags of the original graph.               */
  /****************************************************************************/
  
  for_all_nodes (GET_NODESET_ENTRY (smallSet)->graph, currNode)
  {
    PART_OF_NODE_SET (currNode) = NodeNone;
  }  end_for_all_nodes (GET_NODESET_ENTRY (smallSet)->graph, currNode);
  
  /****************************************************************************/
  /*  Put all nodes of the small set into the left set.                       */
  /****************************************************************************/
  
  for_slist (smallSet, currEntry)
  {
    PART_OF_NODE_SET (GET_NODESET_ENTRY (currEntry)) = LeftSet;
  }  end_for_slist (smallSet, currEntry);
  
  *NodeSet_A = smallSet;

  /****************************************************************************/
  /*  Put all other nodes into the right set.                                 */
  /****************************************************************************/
  
  for_all_nodes (GET_NODESET_ENTRY (smallSet)->graph, currNode)
  {
    if (PART_OF_NODE_SET (currNode) != LeftSet)
    {
      PART_OF_NODE_SET (currNode) = RightSet;
      *NodeSet_B = add_immediately_to_slist (*NodeSet_B, 
                                             CREATE_NODESET_ENTRY (currNode));
    }  /* endif */
  }  end_for_all_nodes (GET_NODESET_ENTRY (smallSet)->graph, currNode);
  
}  /* End of AssignNodeSets */

/******************************************************************************/
/*  Compute subgraphs from node sets.                                         */
/******************************************************************************/

/******************************************************************************/
/*    ComputeInducedSubGraph                                                  */
/*----------------------------------------------------------------------------*/
/*  From the specified set of nodes of a graph, compute the subgraph, which   */
/*  is induced by the node set.                                               */
/*  The flow attributes are used for the subgraph creation.                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNodeSet   Set of nodes inducing a subgraph.           */
/*  Return value :  The induced subgraph.                                     */
/******************************************************************************/
Local Sgraph  ComputeInducedSubGraph  (Slist currNodeSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  PGraphInfoPtr  subInfo   = (PGraphInfoPtr) malloc (sizeof (PGraphInfo));
  Sgraph         subGraph  = make_graph (make_attr(ATTR_DATA, (char *)subInfo));
  
  /****************************************************************************/
  /*  Initialize the attributes for the subgraph.                             */
  /****************************************************************************/
  
  subGraph->directed       = TRUE;
  ORG_GRAPH (subGraph)     = GET_NODESET_ENTRY (currNodeSet)->graph;
  SOURCE (subGraph)        = empty_snode;
  TARGET (subGraph)        = empty_snode;
  NUM_NODES (subGraph)     = size_of_slist (currNodeSet);
  DUMMY_NODE (subGraph)    = empty_snode;
  MAX_CAPACITY (subGraph)  = 0.0;
  
  /****************************************************************************/
  /*  Initialize the attributes for the original graph.                       */
  /****************************************************************************/
  
  InitLabels (NODE_REF(GET_NODESET_ENTRY (currNodeSet))->graph);
  
  /****************************************************************************/
  /*  Create all the nodes of the induced subgraph.                           */
  /****************************************************************************/
  
  CreateInducedNodes  (currNodeSet, subGraph);

  /****************************************************************************/
  /*  Create all the edges in the subgraph.                                   */
  /****************************************************************************/
  
  CreateInducedEdges (subGraph);

  /****************************************************************************/
  /*  Create a dummy node if the number of node is odd.                       */
  /****************************************************************************/
  
  if (NUM_NODES (subGraph) % 2 != 0)
  {
    AddDummyNode (subGraph);
  }  /* endif */

  /****************************************************************************/
  /*  Return the induced subgraph.                                            */
  /****************************************************************************/
  
  return  subGraph;
  
}  /* End of ComputeInducedSubGraph */

/******************************************************************************/
/*    CreateInducedNodes                                                      */
/*----------------------------------------------------------------------------*/
/*  Creates all nodes of the induced subgraph and sets the pointers to        */
/*  enable creating the edges in the subgraph.                                */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNodeSet   Create these nodes in the new graph.        */
/*                  newGraph      The induced subgraph.                       */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CreateInducedNodes  (Slist currNodeSet, Sgraph newGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist          currEntry = empty_slist;
  Snode          newNode   = empty_snode;
  FFNodeInfoPtr  nodeAttr  = NULL;
  
  /****************************************************************************/
  /*  Create all the nodes of the induced subgraph.                           */
  /****************************************************************************/
  
  for_slist (currNodeSet, currEntry)
  {
    nodeAttr = (FFNodeInfoPtr) malloc (sizeof (FFNodeInfo));
    newNode  = make_node (newGraph, make_attr (ATTR_DATA, (char *) nodeAttr));
    
    ORG_NODE (newNode)    = GET_NODESET_ENTRY (currEntry);
    EXTRA_FLOW (newNode)  = 0.0;
    BACK_LABLED (newNode) = FALSE;
    FROM_ARC (newNode)    = empty_sedge;
    FROM_NODE (newNode)   = NODE_REF (GET_NODESET_ENTRY (currEntry));
    IS_LABLED (newNode)   = FALSE;
    
    IS_LABLED (FROM_NODE (newNode))           = TRUE;
    NODE_REF (GET_NODESET_ENTRY (currEntry))  = newNode;
  }  end_for_slist (currNodeSet, currEntry);

}  /* End of CreateInducedNodes */

/******************************************************************************/
/*    CreateInducedEdges                                                      */
/*----------------------------------------------------------------------------*/
/*  Creates all the neccessary edges in the specified graph using the         */
/*  references in the node attributes.                                        */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  newGraph   Induced subgraph containing all the nodes.     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CreateInducedEdges  (Sgraph newGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode          newNode   = empty_snode;
  Sedge          currEdge  = empty_sedge;
  Sedge          newEdge   = empty_sedge;
  FFEdgeInfoPtr  edgeAttr  = NULL;
  
  /****************************************************************************/
  /*  Create all the edges in the subgraph.                                   */
  /****************************************************************************/
  
  for_all_nodes (newGraph, newNode)
  {
    for_sourcelist (FROM_NODE (newNode), currEdge)
    {
      if (IS_LABLED (currEdge->tnode))
      {
        edgeAttr = (FFEdgeInfoPtr) malloc (sizeof (FFEdgeInfo));
        newEdge  = make_edge (newNode, NODE_REF (ORG_NODE (currEdge->tnode)),
                              make_attr (ATTR_DATA, (char *) edgeAttr));
        
        ORG_EDGE (newEdge) = ORG_EDGE (currEdge);
        CAPACITY (newEdge) = 1.0;
        FLOW (newEdge)     = 0.0;
      }  /* endif */
    }  end_for_sourcelist (FROM_NODE (newNode), currEdge);
  }  end_for_all_nodes (newGraph, newNode);

}  /* End of CreateInducedEdges */

/******************************************************************************/
/*    RemoveSubGraph                                                          */
/*----------------------------------------------------------------------------*/
/*  Removes the specified graph including all attributes.                     */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph    Subgraph to remove.                          */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  RemoveSubGraph  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Remove all attributes of nodes and edges of the graph                   */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      free (attr_data_of_type (currEdge, FFEdgeInfoPtr));
    }  end_for_sourcelist (currNode, currEdge);
    
    free (attr_data_of_type (currNode, FFNodeInfoPtr));
    
  }  end_for_all_nodes (currGraph, currNode);

  /****************************************************************************/
  /*  Remove the graph itself                                                 */
  /****************************************************************************/
  
  free (attr_data_of_type (currGraph, PGraphInfoPtr));
  remove_graph (currGraph);

}  /* End of RemoveSubGraph */

/******************************************************************************/
/*  Search for all connected components of a graph.                           */
/******************************************************************************/

/******************************************************************************/
/*    ComputeConnectedComponents                                              */
/*----------------------------------------------------------------------------*/
/*  Computes the list of all connected components of the specified graph.     */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   The graph which is being decomposed.          */
/*  Return value :  The list of connected components.                         */
/******************************************************************************/
Local Slist  ComputeConnectedComponents  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  Components = empty_slist;
  Snode  currNode   = empty_snode;
  float  cNumber    = 1.0;
  
  /****************************************************************************/
  /*  Clean the marker flags in the attributes.                               */
  /****************************************************************************/

  InitLabels (currGraph);

  /****************************************************************************/
  /*  Test each node, if it is already in an component.                       */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    if (!IS_LABLED (currNode))
    {
      Components = add_immediately_to_slist (Components,
        CREATE_GC_ENTRY (ComputeComponent (currNode, cNumber)));
      cNumber = cNumber + 1.0;
    } /* endif */
  }  end_for_all_nodes (currGraph, currNode);
  
  /****************************************************************************/
  /*  Return the list of all encountered components.                          */
  /****************************************************************************/
  
  return Components;

}  /* End of ComputeConnectedComponents */

/******************************************************************************/
/*    ComputeComponent                                                        */
/*----------------------------------------------------------------------------*/
/*  Computes the list of all nodes in a connected component of a graph.       */
/*  The visited flag is set for all nodes in the component and it is assumed, */
/*  that the flag is not set for all nodes in the component.                  */
/*  Each node is assigned the number of the component.                        */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  startNode   The first node of a component.                */
/*                  cNumber     Number of the current component.              */
/*  Return value :  The structure representing the component.                 */
/******************************************************************************/
Local GraphComponentPtr  ComputeComponent  (Snode startNode, float cNumber)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  GraphComponentPtr  newComponent = (GraphComponentPtr) malloc 
                                    (sizeof (GraphComponent));
  Snode              currNode     = startNode;
  Snode              nextNode     = empty_snode;
  Sedge              currEdge     = empty_sedge;
  AStackPtr          FoundNodes   = InitAStack ();
  Attributes         nodeAttr;
  
  /****************************************************************************/
  /*  Initialize the component structure.                                     */
  /****************************************************************************/
  
  newComponent->Size  = 1;
  newComponent->Nodes = empty_slist;
  newComponent->Nodes = add_immediately_to_slist (newComponent->Nodes,
                             CREATE_NODESET_ENTRY (startNode));
  newComponent->Side  = NodeNone;
  
  PushAStack (FoundNodes, CREATE_NODESET_ENTRY (startNode));

  /****************************************************************************/
  /*  Search dfs-like the component.                                          */
  /****************************************************************************/
  
  do
  {
    nodeAttr = PopAStack (FoundNodes);
    currNode = (Snode) nodeAttr.value.data;
    
    for_sourcelist (currNode, currEdge)
    {
      nextNode = NEXT_NODE (currNode, currEdge);
      
      if (!IS_LABLED (nextNode))
      {
        IS_LABLED (nextNode)   = TRUE;
        EXTRA_FLOW (nextNode)  = cNumber;
        newComponent->Nodes = add_immediately_to_slist (
                                   newComponent->Nodes,
                                   CREATE_NODESET_ENTRY (nextNode));
        PushAStack (FoundNodes, CREATE_NODESET_ENTRY (nextNode));
        newComponent->Size++;
      }  /* endif */
    }  end_for_sourcelist (currNode, currEdge);
    
    for_targetlist (currNode, currEdge)
    {
      nextNode = NEXT_NODE (currNode, currEdge);
      
      if (!IS_LABLED (nextNode))
      {
        IS_LABLED (nextNode)   = TRUE;
        newComponent->Nodes = add_immediately_to_slist (
                                   newComponent->Nodes,
                                   CREATE_NODESET_ENTRY (nextNode));
        PushAStack (FoundNodes, CREATE_NODESET_ENTRY (nextNode));
        newComponent->Size++;
      }  /* endif */
    }  end_for_targetlist (currNode, currEdge);
    
  }  while (!ASTACK_IS_EMPTY(FoundNodes));

  /****************************************************************************/
  /*  Clean up and return the component.                                      */
  /****************************************************************************/
  
  RemoveAStack (FoundNodes);
  return newComponent;

}  /* End of ComputeComponent */

/******************************************************************************/
/*    RemoveComponentList                                                     */
/*----------------------------------------------------------------------------*/
/*  Removes the specified list of conected components including the entries.  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currComponents   List of the current components to remove.*/
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  RemoveComponentList  (Slist currComponents)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist  currEntry = empty_slist;
  
  /****************************************************************************/
  /*  Free the memory allocated for the component entries.                    */
  /****************************************************************************/
  
  for_slist (currComponents, currEntry)
  {
    free_slist (C_NODES (currEntry));
    free (attr_data_of_type (currEntry, GraphComponentPtr));
  }  end_for_slist (currComponents, currEntry);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (currComponents);
  
}  /* End of RemoveComponentList */

/******************************************************************************/
/*  End of  PlaistedStar.c                                                    */
/******************************************************************************/
