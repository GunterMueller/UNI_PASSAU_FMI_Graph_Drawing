/******************************************************************************/
/*                                                                            */
/*    BruteForce.c                                                            */
/*                                                                            */
/******************************************************************************/
/*  Implementation of the naive edge-separator algorithm.                     */
/*  This NP-Algorithm computes a minimal separator exactly by testing all     */
/*  possible edge combinations, if they fullfill the separator condition.     */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  13.02.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.2         Name change                                       */
/*              1.1         New interface (AlgorithmInfo)                     */
/*              1.0         First Revision                                    */
/******************************************************************************/

#include "BruteForce.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local  bool   ComputeNaiveSeparator  (/*...*/);
Local  int    ComputeEdgeList        (/*...*/);
Local  Slist  AddToSolution          (/*currSolution, currEdge*/);
Local  void   RemoveFromSolution     (/*currSolution, currEntry*/);
Local  bool   IsAlphaSeparator       (/*...*/);
Local  bool   ExpandFromSeparator    (/*...*/);
Local  bool   ExpandNodes            (/*...*/);
Local  bool   TestAlphaCondition     (/*...*/);
Local  int    CountComponentNodes    (/*...*/);
Local  bool   AssignComponents       (/*...*/);
Local  void   CreateComponent        (/*...*/);
Local  void   FreeComponents         (/*...*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local int    ComputeEdgeList        (Sgraph  currGraph, Slist  *EdgeList);
Local Slist  AddToSolution          (Slist  *currSolution, Sedge  currEdge);
Local void   RemoveFromSolution     (Slist  *currSolution, Slist  currEntry);
Local bool   ComputeNaiveSeparator  (Slist   EdgeList,
                                     float   alpha,
                                     int     currDepth,
                                     Slist  *Separator,
                                     Slist  *currSolution,
                                     Slist   currEntry,
                                     int    *maxDepth);
Local bool   IsAlphaSeparator       (Sgraph  currGraph, 
                                     Slist   Separator, 
                                     float   alpha);
Local bool   ExpandFromSeparator    (Slist  Separator);
Local bool   ExpandNodes            (Snode  startNode, NodeSet  currSet);
Local bool   TestAlphaCondition     (Sgraph  currGraph, float  alpha);
Local int    CountComponentNodes    (Snode  startNode);
Local bool   AssignComponents       (int    noGraphNodes,
                                     int    cardLeftSet,
                                     int    cardRightSet,
                                     Slist  currEntry,
                                     int    noComponents,
                                     float  alpha);
Local void   CreateComponent        (Snode          currNode, 
                                     int            currSize, 
                                     ComponentPtr  *newComponent);
Local void   FreeComponents         (Slist  currComponents);
#endif

/******************************************************************************/
/*  Functions to compute the optimal alpha-edge separator the hard way        */
/******************************************************************************/

/******************************************************************************/
/*     BruteForce                                                             */
/*----------------------------------------------------------------------------*/
/*  Computes an alpha edge separator of currGraph the naive way, that is by   */
/*  testing possible edge sets if they separate the graph while the alpha     */
/*  condition holds.                                                          */
/*  After testing if alpha is valid and if the graph is not empty, the edges  */
/*  of the graph are computed and used as input for the (recursive) procedure */
/*  which computes the separator. The worst case for the size of an edge      */
/*  separator is |E|/2 - a clique with |E| edges. Thus only edge sets up to   */
/*  this size are tested.                                                     */
/*  When ComputenaiveSeparator returns, the separator edges are in Separator  */
/*  and all nodes (including disconnected components of the graph) are        */
/*  assigned to a node set. This assignment is then used to compute the node  */
/*  set parameters.                                                           */
/*  If validAttrs is TRUE, all nodes and edges of the graph MUST contain the  */
/*  neccessary attribute structure. If the value is FALSE, they are created.  */
/*  In either case they can (must) be destroyed after calling this algorithm. */
/*  The algorithm can handle directed and undirected graphs, which need not   */
/*  be connected.                                                             */
/*  The attributes of currGraph MUST be initialized and the node sets empty ! */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo   Data to execute algorithm (graph, alpha...).    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  BruteForce  (GSAlgInfoPtr algInfo)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode      = NULL;
  Slist  EdgeList      = empty_slist;
  Slist  currSolution  = empty_slist;
  int    EdgeCount     = 0;
  int    maxDepth      = 0;
  int    maxASet       = 0;
  int    maxBSet       = 0;
  Slist  currEntry     = empty_slist;
  Slist  NewEntry      = empty_slist;

  /****************************************************************************/
  /*  Test if the graph is disconnected (and the alpha condition holds)       */
  /****************************************************************************/
  
  EdgeCount  = ComputeEdgeList (GRAPH (algInfo), &EdgeList);

  if (EdgeCount == 0)
  {
    ComputeNodeSetSizes (NUM_GRAPH_NODES (GRAPH (algInfo)), 
                         &maxASet, &maxBSet, ALPHA (algInfo));
    InitialPartition(GRAPH(algInfo), &A_SET(algInfo), &B_SET(algInfo), maxASet);
  }
  else  if (!IsAlphaSeparator (GRAPH (algInfo), empty_slist, ALPHA (algInfo)))
  { /*  There are edges in the graph which might be in the separator          */
    /* Compute the separator (Worst case is CLIQUE: Separatorsize is |E|/2)   */
    maxDepth  = (int) floor (pow ((double) NUM_GRAPH_NODES (GRAPH (algInfo)),
                                  (double) 2.0) / (double) 4.0) + 1;
    maxDepth  = (maxDepth < EdgeCount) ? maxDepth : EdgeCount;

    for_slist (EdgeList, currEntry)
    {
      NewEntry = AddToSolution (&currSolution, GET_EDGELIST_ENTRY(currEntry));
      ComputeNaiveSeparator (EdgeList, 
                             ALPHA (algInfo), 
                             1, 
                             &SEP_SET (algInfo), 
                             &currSolution,
                             currEntry->suc,
                             &maxDepth);
      RemoveFromSolution (&currSolution, NewEntry);
    }  end_for_slist (EdgeList, currEntry);
  }  /* endif */

  /****************************************************************************/
  /*  Compute the node sets                                                   */
  /****************************************************************************/

  for_all_nodes (GRAPH (algInfo), currNode)
  {
    IS_NODE_VISITED(currNode) = FALSE;
    if (PART_OF_NODE_SET(currNode) == LeftSet)
    {
      A_SET (algInfo) = add_immediately_to_slist (A_SET (algInfo), 
                                      CREATE_NODESET_ENTRY (currNode));
    }
    else /* currNode is in the right set */
    {
      B_SET (algInfo) = add_immediately_to_slist (B_SET (algInfo), 
                                      CREATE_NODESET_ENTRY (currNode));
    } /* endif */
  } end_for_all_nodes (GRAPH (algInfo), currNode);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  free_slist (EdgeList);

}  /* End of BruteForce */

/******************************************************************************/
/*    ComputeEdgeList                                                         */
/*----------------------------------------------------------------------------*/
/*  Scans the graph for its edges and puts each ONLY ONCE into the EdgeList.  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Graph to separate                             */
/*                  EdgeList    List of edges to be computed in the graph.    */
/*  Return value :  Number of elements in EdgeList                            */
/******************************************************************************/
Local int  ComputeEdgeList  (Sgraph currGraph, Slist *EdgeList)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode  = NULL;
  Sedge  currEdge  = NULL;
  int    EdgeCount = 0;

  /****************************************************************************/
  /*  Since we're computing edge separators, compute all edges                */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if (currGraph->directed || unique_edge (currEdge))
      {
        EdgeCount++;
        *EdgeList = add_immediately_to_slist (*EdgeList, 
                      make_attr (ATTR_DATA, (char *)currEdge));
      }  /* endif */
    } end_for_sourcelist (currNode, currEdge);
  } end_for_all_nodes (currGraph, currNode);
  
  /****************************************************************************/
  /*  Return the number of edges in the graph                                 */
  /****************************************************************************/

  return  EdgeCount;
  
}  /* End of ComputeEdgeList */

/******************************************************************************/
/*    AddToSolution                                                           */
/*----------------------------------------------------------------------------*/
/*  Puts the specified edge into the candidate list.                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currSolution   Current list of candidate edges.           */
/*                  currEdge       Edge to add to the candidate list.         */
/*  Return value :  Slist entry created for currEdge.                         */
/******************************************************************************/
Local Slist  AddToSolution  (Slist *currSolution, Sedge currEdge)
{
  /****************************************************************************/
  /*  Put the edge into the candidate list.                                   */
  /****************************************************************************/

  *currSolution = add_immediately_to_slist (*currSolution,
                                            CREATE_SEPARATOR_ENTRY(currEdge));
  VALID_EDGE_SET(currEdge) = Separator_Set;
  
  /****************************************************************************/
  /*  Return the pointer to the slist entry created for the edge.             */
  /****************************************************************************/

  return  (*currSolution)->pre;
  
}  /* End of AddToSolution */

/******************************************************************************/
/*    RemoveFromSolution                                                      */
/*----------------------------------------------------------------------------*/
/*  Removes the specified edge from the candidate list.                       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currSolution   Current list of candidate edges.           */
/*                  currEdge       Edge to add to the candidate list.         */
/*  Return value :  Slist entry created for currEdge.                         */
/******************************************************************************/
Local void  RemoveFromSolution  (Slist *currSolution, Slist currEntry)
{
  /****************************************************************************/
  /*  Remove the entry from the list and reset the edge set flag              */
  /****************************************************************************/

  VALID_EDGE_SET(GET_EDGELIST_ENTRY(currEntry)) = EdgeNone;
  *currSolution = subtract_immediately_from_slist(*currSolution, currEntry);
 
}  /* End of RemoveFromSolution */

/******************************************************************************/
/*     ComputeNaiveSeparator                                                  */
/*----------------------------------------------------------------------------*/
/*  This recursive function tries out ALL possible edge combinations (without */
/*  repetitions) starting with small cardinality until a separator is found.  */
/*  In the recursion, three cases can occur:                                  */
/*  1) The current edge set is a alpha separator:                             */
/*    - Then the separator is updated.                                        */
/*    - The maximum search level is set to one level BEFORE the current so-   */
/*      lution was found (only sets of lesser cardinality should be tried).   */
/*    - The calling recursion level is told not to continue search. The level */
/*      below that should continue.                                           */
/*  2) The maximum search level is reached and no new separator:              */
/*    - Try again with a new candidate of the same size until all candidates  */
/*      are exhausted (done at the calling recursion level).                  */
/*  3) None of the above: Continue search                                     */
/*    - All NEW (not already in currSolution) edges are put into the test set */
/*      and tested in the recursive call.                                     */
/*    - If the recursive call returns FALSE, the actual set was successful    */
/*      and only possible separators of lower cardinality are of interest.    */
/*      So continue one level higher.                                         */
/*    - The return value was TRUE, which means that the search should be      */
/*      continued. So remove the new edge from currSolution and try the next. */
/*    - When all edges are tested, try again one level higher.                */
/*  To make sure, that no edge set is tested twice, currEntry is used to      */
/*  add only edges to the current edge set, which come after the last added   */
/*  edge of EdgeList.                                                         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  EdgeList       The edges of the graph to separate         */
/*                  Separator      Edges in the separator                     */
/*                  alpha          alpha-sparators are computed               */
/*                  currDepth      Number of edges in the Edge set to test    */
/*                  Separator      Edges in the currently optimal separator   */
/*                  currSolution   Edge set to be tested                      */
/*                  currEntry      Edge to be added next                      */
/*                  maxDepth       Edge sets up to this size need testing     */
/*  Return value :  Continue search                                           */
/******************************************************************************/
Local bool  ComputeNaiveSeparator  (Slist EdgeList,
                                    float alpha,
                                    int currDepth,
                                    Slist *Separator,
                                    Slist *currSolution,
                                    Slist currEntry,
                                    int *maxDepth)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Slist      currEdge  = currEntry;
  Slist      NewEntry  = empty_slist;
  bool       Continue  = TRUE;
  
  /****************************************************************************/
  /*  First try the current edge set then try recursively adding edges        */
  /****************************************************************************/
/*
  printf("\tLevel: %d,\tMax = %d,\tSize = %d,\tOpt = %d\n", 
         currDepth, *maxDepth, 
         size_of_slist (*currSolution), size_of_slist (*Separator));
*/
  if (IsAlphaSeparator (GET_SEP_SNODE(EdgeList)->graph, *currSolution, alpha)) 
  {                                /* 1. Update the Separator                 */
    free_slist (*Separator);
    *Separator = copy_slist (*currSolution);
    *maxDepth = currDepth - 1;
    /* Do not continue creating separator candidates of the current size      */
    return FALSE;
  }
  else if (currDepth == *maxDepth) /* 2. No further recursion needed          */
  {
    return TRUE;  /* Try another of this size, until all possibilities tested */
  }
  else  /* 3. Continue recursion (EdgeList is never empty, else the graph is  */
  {     /*    completely disconected and thus a separtor of size 0 was found) */
    while (currEdge != EdgeList)
    {   
      /* Current edge cannot be in currSolution, so add it                    */
      NewEntry = AddToSolution (currSolution, GET_EDGELIST_ENTRY(currEdge));
      /* Recursive call:                                                      */
      Continue = ComputeNaiveSeparator (EdgeList, 
                                        alpha, 
                                        currDepth + 1,  
                                        Separator, 
                                        currSolution,
                                        currEdge->suc,
                                        maxDepth);
      /* Clean up                                                             */
      RemoveFromSolution (currSolution, NewEntry);
      currEdge = currEdge->suc;
      if (!Continue) /* Separator found on this level, continue one higher    */
      {
        return TRUE;
      }  /* endif */
    }
    /* No luck on this level, so try one level higher                         */
    return TRUE;
  }  /*endif */
}  /* End of ComputeNaiveSeparator */

/******************************************************************************/
/*  Is a set of edges an alpha-separator ?                                    */
/******************************************************************************/

/******************************************************************************/
/*     IsAlphaSeparator                                                       */
/*----------------------------------------------------------------------------*/
/*  Tests if the specified edge set is an alpha edge separator by assigning   */
/*  each node reachable (in a dfs-manner) from the endnodes of the separator  */
/*  edges consistently to a node set. The return value is FALSE, if this      */
/*  assignment fails.                                                         */
/*  After the assignment the alpha condition is tested.                       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Separator      Edges in the possible separator            */
/*                  alpha          alpha-sparators are computed               */
/*  Return value :  True if an alpha-separator has been found                 */
/******************************************************************************/
Local bool  IsAlphaSeparator  (Sgraph currGraph, Slist Separator, float alpha)
{ 
  /****************************************************************************/
  /*  Initialize the markers, then expand all endnodes of separator edges     */
  /****************************************************************************/
  
  InitNodeMarkers (currGraph);
  if (!ExpandFromSeparator (Separator))
  {
    return FALSE;
  }  /* endif */
    
  /****************************************************************************/
  /*  Edge set separates the graph - test if Separator is an alpha-Separator  */
  /****************************************************************************/
  
  return TestAlphaCondition (currGraph, alpha);
  
}  /* End of IsAlphaSeparator */

/******************************************************************************/
/*    ExpandFromSeparator                                                     */
/*----------------------------------------------------------------------------*/
/*  Tries to assign each node reachable from an endnode of a separator-edge   */
/*  consistently to a node set.                                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Separator   Current separator to expand from              */
/*  Return value :  FALSE, if expanding leaded to inconsistencies             */
/******************************************************************************/
Local bool  ExpandFromSeparator  (Slist Separator)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Slist    currEntry = empty_slist;
  NodeSet  SourceSet = NodeNone;
  NodeSet  TargetSet = NodeNone;
  
  /****************************************************************************/
  /*  Initialize the markers, then expand all endnodes of separator edges     */
  /****************************************************************************/
  
  for_slist (Separator, currEntry)
  {
    /* Get the node sets of the end nodes of the current separator edge       */
    SourceSet = VALID_NODE_SET(GET_SEP_SNODE(currEntry));
    TargetSet = VALID_NODE_SET(GET_SEP_TNODE(currEntry));

    /* Test if the values are inconsistent or decide the value to expand with */
    if ((SourceSet != NodeNone) && (TargetSet != NodeNone) && 
        (SourceSet == TargetSet))
    {
      return FALSE;  /* Node sets are inconsistent                            */
    }
    else if ((SourceSet == NodeNone) && (TargetSet == NodeNone))
    {
      SourceSet = LeftSet;   /* Initial random assignment - happens only at   */
      TargetSet = RightSet;  /* the beginning or when a new component begins  */
    }
    else if ((SourceSet != NodeNone) && (TargetSet == NodeNone))
    {
      TargetSet = (SourceSet == RightSet) ? LeftSet : RightSet;
    }
    else if ((TargetSet != NodeNone) && (SourceSet == NodeNone))
    {
      SourceSet = (TargetSet == RightSet) ? LeftSet : RightSet;
    }
    else  /* Both nodes visited and consistent */
    {
      continue;
    }  /* endif */
    
    /* Expanding of end node(s) is necessary, test if expanding is consistent */
    if (!ExpandNodes (GET_SEP_SNODE(currEntry), SourceSet))
    {
      return FALSE;  /* Node sets are inconsistent                            */
    }
    else  /* Still consistent after first try */
    {
      if (!ExpandNodes (GET_SEP_TNODE(currEntry), TargetSet))
      {
        return FALSE;  /* Node sets are inconsistent                          */
      }  /* endif */
    }  /* endif */
  }  end_for_slist (Separator, currEntry);
  
  /****************************************************************************/
  /*  Nothing bad happend                                                     */
  /****************************************************************************/
  
  return  TRUE;

}  /* End of ExpandFromSeparator */

/******************************************************************************/
/*     ExpandNodes                                                            */
/*----------------------------------------------------------------------------*/
/*  Recursivly assigns nodes - beginning with startNode - to currSet          */
/*  dfs-like.                                                                 */
/*  The recursion terminates unsuccessfully, if set assignment consistency is */
/*  violated or successfully, if no more nodes are expandable.                */
/*  Expansion does not cross separator edges, therefore the EdgeSet values of */
/*  separator edges MUST be Separator_Set.                                    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  startNode  Node to expand                                 */
/*                  currSet    Set where the expandable nodes are assigned to */
/*  Return value :  FALSE if expanding leads to inconsistent set assignments  */
/******************************************************************************/
Local bool  ExpandNodes  (Snode startNode, NodeSet currSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Sedge  currEdge = NULL;
  Snode  nextNode = NULL;
  bool   success  = TRUE;
  
  /****************************************************************************/
  /*  First test consistency, then termination or continue expanding          */
  /****************************************************************************/

  if (IS_NODE_VISITED(startNode) && (VALID_NODE_SET(startNode) != currSet))
  {
    return FALSE;  /* The other node set was reached by a non separatore edge */
  }
  else if (IS_NODE_VISITED(startNode) && (VALID_NODE_SET(startNode) == currSet))
  {
    return TRUE;  /* The current node was already expanded - consistently     */
  }
  else  /* Expand the current node */
  {
    /* Mark the current node as visited and put it into the current node set  */
    IS_NODE_VISITED(startNode) = TRUE;
    VALID_NODE_SET(startNode)  = currSet;
    
    /* Expand all adjacent node (undirected) or all starting nodes (directed) */
    for_sourcelist (startNode, currEdge)
    {
      nextNode = NEXT_NODE(startNode, currEdge);
      success = (VALID_EDGE_SET(currEdge) != Separator_Set) ?
                  ExpandNodes (nextNode, currSet) : 
                  (VALID_NODE_SET(nextNode) != currSet);
    }  end_for_sourcelist (startNode, currEdge);

    /* If everything is still consistent, handle the directed case            */
    if (success && (startNode->graph->directed))
    {
      for_targetlist (startNode, currEdge)
      {
        nextNode = NEXT_NODE(startNode, currEdge);
        success = (VALID_EDGE_SET(currEdge) != Separator_Set) ?
                    ExpandNodes (nextNode, currSet) : 
                    (VALID_NODE_SET(nextNode) != currSet);
      }  end_for_targetlist (startNode, currEdge);
    }  /* endif */

    return success;
  }  /* endif */
}  /* End of ExpandNodes */

/******************************************************************************/
/*     TestAlphaCondition                                                     */
/*----------------------------------------------------------------------------*/
/*  Tests if the current node set assignment violates the alpha-condition:    */
/*    max(|A|,|B|) <= alpha*(|A|+|B|), 0.5<=alpha<1                           */
/*  The problem is, that node expansion starting with end nodes of the        */
/*  separator edges may not cover all nodes. This is the case if the graph is */
/*  not connected. For all components of the graph, a valid assignment to the */
/*  node sets have to be computed if possible.                                */
/*  If the alpha condition holds, the node markers are saved to maintain the  */
/*  last found separator and the components info is nuked.                    */
/*  The DUMMY-flag of the node attributes is used to search a component.      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph  Graph where the alpha condition should hold    */
/*                  alpha      max(|A|,|B|) <= alpha*(|A|+|B|), 0.5<=alpha<1  */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local bool  TestAlphaCondition  (Sgraph currGraph, float alpha)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  int        cardLeftSet      = 0;
  int        cardRightSet     = 0;
  Snode      currNode         = NULL;
  Sedge      currEdge         = NULL;
  Component  *newComponent    = NULL;
  Slist      Components       = empty_slist;
  bool       success          = FALSE;
  int        componentCounter = 0;
  
  /****************************************************************************/
  /*  Count the nodes in the node sets and the nodes in unvisited components  */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    if (VALID_NODE_SET(currNode) == LeftSet)
    {
      cardLeftSet++;
    }
    else if (VALID_NODE_SET(currNode) == RightSet)
    {
      cardRightSet++;
    }
    else  /* Node in unvisited component found */
    {
      /* Test if currNode is in an already found component                    */
      if (!IS_DUMMY(currNode))
      {
        componentCounter++;
        CreateComponent (currNode, 
                         CountComponentNodes (currNode), 
                         &newComponent);
        Components = add_immediately_to_slist (Components,
                       CREATE_COMPONENT_ENTRY(newComponent));
      } /* endif */
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);

  /****************************************************************************/
  /*  Try to assign the components so, that the alpha condition holds         */
  /****************************************************************************/

  success = AssignComponents (NUM_GRAPH_NODES (currGraph), 
                              cardLeftSet, 
                              cardRightSet, 
                              Components, 
                              componentCounter,
                              alpha);
  /* Save the nodeset markers of a valid separator                            */
  if (success)
  {
    for_all_nodes (currGraph, currNode)
    {
      PART_OF_NODE_SET(currNode) = VALID_NODE_SET(currNode);
      for_sourcelist (currNode, currEdge)
      {
        PART_OF_EDGE_SET(currEdge) = VALID_EDGE_SET(currEdge);
      }  end_for_sourcelist (currNode, currEdge);
    }  end_for_all_nodes (currGraph, currNode);
  }  /* endif */
  FreeComponents (Components);
  return success;
}  /* End of TestAlphaCondition */

/******************************************************************************/
/*  Components handling                                                       */
/******************************************************************************/

/******************************************************************************/
/*     CountComponentNodes                                                    */
/*----------------------------------------------------------------------------*/
/*  Counts the nodes of a not yet visited component of the graph in the same  */
/*  style as ExpandNode starting with startNode recursively.                  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  startNode  Node in the unvisited component of the graph   */
/*  Return value :  Number of nodes in the component                          */
/******************************************************************************/
Local int  CountComponentNodes  (Snode startNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Sedge  currEdge = NULL;
  Snode  nextNode = NULL;
  int    counter  = 1;
  
  /****************************************************************************/
  /*  Recursively count the nodes in the current component                    */
  /****************************************************************************/

  IS_DUMMY (startNode) = TRUE;        /* Mark the node as visited             */
  for_sourcelist (startNode, currEdge)
  {
    nextNode = NEXT_NODE (startNode, currEdge);
    if (!IS_DUMMY (nextNode))
    {
      counter = counter + CountComponentNodes (nextNode);
    }  /* endif */
  }  end_for_sourcelist (startNode, currEdge);

  /* Handle the directed case, that is consider the targetlist                */
  if (startNode->graph->directed)
  {
    for_targetlist (startNode, currEdge)
    {
      nextNode = NEXT_NODE (startNode, currEdge);
      if (!IS_DUMMY (nextNode))
      {
        counter = counter + CountComponentNodes (nextNode);
      }  /* endif */
    }  end_for_targetlist (startNode, currEdge);
  }  /* endif */

  return counter; 
}  /* End of CountComponentNodes */

/******************************************************************************/
/*     AssignComponents                                                       */
/*----------------------------------------------------------------------------*/
/*  Tries recursively all combinations fo components assignments to node sets */
/*  until a valid one is found or all possibilities are exhausted.            */
/*  When a valid assignment is found, the nodes in the components are expan-  */
/*  ded to their node sets.                                                   */
/*  When first called, currEntry MUST be the first component!                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  noGraphNodes   The number of nodes in the current graph   */
/*                  cardLeftSet    Current cardinality of the first set       */
/*                  cardLeftSet    Current cardinality of the second set      */
/*                  currEntry      Current component entry to add to the set  */
/*                  noComponents   Number components to assign                */
/*                  alpha          max(|A|,|B|) <= alpha*(|A|+|B|)            */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local bool  AssignComponents  (int noGraphNodes,
                               int cardLeftSet,
                               int cardRightSet,
                               Slist currEntry,
                               int noComponents,
                               float alpha)
{
  /****************************************************************************/
  /*  Assign each component recursively to a set and test the alpha condition */
  /****************************************************************************/

  if (noComponents == 0)
  {
    return ((float)maximum(cardLeftSet, cardRightSet) <= 
            alpha * (float)noGraphNodes);
  }
  else
  {
    /* Try to assign the current component to the left node set               */
    if (AssignComponents (noGraphNodes, 
                          cardLeftSet + COMPONENT_SIZE(currEntry), 
                          cardRightSet,
                          currEntry->suc,
                          noComponents - 1,
                          alpha))
    {
      /* Valid assignment found, put the component nodes into the left set    */
      ExpandNodes (COMPONENT_START(currEntry), LeftSet);
      return  TRUE;
    }  /* endif */
    /* Try to assign the current component to the right node set              */
    if (AssignComponents (noGraphNodes, 
                          cardLeftSet, 
                          cardRightSet + COMPONENT_SIZE(currEntry),
                          currEntry->suc,
                          noComponents - 1,
                          alpha))
    {
      /* Valid assignment found, put the component nodes into the right set   */
      ExpandNodes (COMPONENT_START(currEntry), RightSet);
      return TRUE;
    }  /* endif */
  }  /* endif */
  /* No luck                                                                  */
  return  FALSE;
}  /* End of AssignComponents */

/******************************************************************************/
/*  Components creation and removal                                           */
/******************************************************************************/

/******************************************************************************/
/*     CreateComponent                                                        */
/*----------------------------------------------------------------------------*/
/*  Allocates memory for a new component info structure and fills it with the */
/*  specified values.                                                         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode      Starting node of the new component          */
/*                  currSize      Size of the new component                   */
/*                  newComponent  Pointer to the newly created component      */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CreateComponent  (Snode currNode, int currSize, ComponentPtr *newComponent)
{
  *newComponent              = (ComponentPtr) malloc (sizeof(Component));
  (*newComponent)->Size      = currSize;
  (*newComponent)->startNode = currNode;

}  /* End of CreateComponent */

/******************************************************************************/
/*     FreeComponents                                                         */
/*----------------------------------------------------------------------------*/
/*  Frees for each component the info structure and then the list of compo-   */
/*  nents.                                                                    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currComponents  List of components info structs to remove */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  FreeComponents  (Slist currComponents)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Slist  currEntry = empty_slist;
  
  /****************************************************************************/
  /*  Remove each component struct                                            */
  /****************************************************************************/

  for_slist (currComponents, currEntry)
  {
    free (GET_COMPONENT_ENTRY(currEntry));
  }  end_for_slist (currComponents, currEntry);

  /****************************************************************************/
  /*  Clean up component list                                                 */
  /****************************************************************************/
  
  free_slist (currComponents);
  
}  /* End of FreeComponents */

/******************************************************************************/
/*  End of  BruteForce.c                                                      */
/******************************************************************************/
