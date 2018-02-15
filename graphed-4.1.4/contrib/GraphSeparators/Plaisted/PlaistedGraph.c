/******************************************************************************/
/*                                                                            */
/*    PlaistedGraph.c                                                         */
/*                                                                            */
/******************************************************************************/
/*  Supporting functions for the Plaisted heuristic.                          */
/*  The Plaisted heuristic works on a undirected and connected graph, which   */
/*  is converted into a undirected flow problem later.                        */
/*  The undirected flow problem is constructed from a connected directed or   */
/*  undirected input graph. The undirected flow problem has each edge in both */
/*  directions in the undirected version. In the directed version, it is made */
/*  sure, that each arc exists in both directions.                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  03.05.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "Plaisted.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local void    CopyPlaistedEdges   (/*newGraph*/);
Local bool    BackwardEdgeExists  (/*nextOrgNode, orgNode*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local void  CopyPlaistedEdges   (Sgraph  newGraph);
Local bool  BackwardEdgeExists  (Snode  nextOrgNode, Snode  orgNode);
#endif

/******************************************************************************/
/*  Initializing the graph                                                    */
/******************************************************************************/

/******************************************************************************/
/*    InitPlaistedGraph                                                       */
/*----------------------------------------------------------------------------*/
/*  Copies the specified graph such that:                                     */
/*  - The number of nodes is even. If the insertion of a dummy node is nec-   */
/*    cessary, it will be marked and refernced to by the Plaisted graph       */
/*    attribute. The dummy node is adjacent to every other node.              */
/*  - If an edge exists between two nodes of the input graph, there will be   */
/*    two edges between the two nodes in the output graph - one for each      */
/*    direction. The inserted one will be marked as artificial.               */
/*  The output graph is directed and has a PGraphInfo structure as graph      */
/*  attribute and the FF*Info structures as node and edge attributes.         */
/*  The input graph MUST NOT contain multi-edges.                             */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph    Input graph to copy.                         */
/*  Return value :  A directed copy of the input graph with the proper attrs. */
/******************************************************************************/
Global Sgraph  InitPlaistedGraph  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  PGraphInfoPtr  newGAttr = (PGraphInfoPtr) malloc (sizeof (PGraphInfo));
  Sgraph         newGraph = make_graph (make_attr(ATTR_DATA, (char *)newGAttr));
  Snode          currNode = NULL;
  Snode          newNode  = NULL;
  FFNodeInfoPtr  newNAttr = NULL;
  
  /****************************************************************************/
  /*  Create the new graph and make it directed.                              */
  /****************************************************************************/

  newGraph->directed    = TRUE;
  ORG_GRAPH (newGraph)  = currGraph;
  SOURCE (newGraph)     = NULL;
  TARGET (newGraph)     = NULL;
  DUMMY_NODE (newGraph) = NULL;

  /****************************************************************************/
  /*  Copy all nodes.                                                         */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    newNAttr             = (FFNodeInfoPtr) malloc (sizeof (FFNodeInfo));
    newNode              = make_node (newGraph, make_attr (ATTR_DATA, 
                                                           (char *) newNAttr));
    IS_LABLED(newNode)   = FALSE;
    FROM_NODE(newNode)   = NULL;
    FROM_ARC(newNode)    = NULL;
    BACK_LABLED(newNode) = FALSE;
    EXTRA_FLOW(newNode)  = 0.0;
    ORG_NODE (newNode)   = currNode;
    NODE_REF (currNode)  = newNode;
  }  end_for_all_nodes (currGraph, currNode);

  NUM_NODES (newGraph) = NUM_GRAPH_NODES (currGraph);
  
  /****************************************************************************/
  /*  Now copy each edge twice if neccessary.                                 */
  /****************************************************************************/
  
  CopyPlaistedEdges (newGraph);

  /****************************************************************************/
  /*  Create a dummy node if the number of node is odd.                       */
  /****************************************************************************/
  
  if (NUM_GRAPH_NODES (currGraph) % 2 != 0)
  {
    AddDummyNode (newGraph);
  }  /* endif */

  /****************************************************************************/
  /*  Return the new graph                                                    */
  /****************************************************************************/
  
  return newGraph;
  
}  /* End of InitPlaistedGraph */

/******************************************************************************/
/*    CopyPlaistedEdges                                                       */
/*----------------------------------------------------------------------------*/
/*  Copies the edges of orgGraph into newGraph. newGraph contains all nodes   */
/*  of orgGraph, which MUST NOT contain multi-edges.                          */
/*  If if the edge (u,v) is in orgGraph, (u,v) and (v,u) will be in newGraph. */
/*  Therefor (u,v) will be copied twice if neccessary.                        */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  newGraph   Copy of orgGraph containing only the nodes.    */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  CopyPlaistedEdges  (Sgraph newGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  newNode     = NULL;
  Snode  nextNewNode = NULL;
  Snode  nextOrgNode = NULL;
  Sedge  currOrgEdge = NULL;
  
  /****************************************************************************/
  /*  Now copy each edge twice if neccessary.                                 */
  /****************************************************************************/
  
  for_all_nodes (newGraph, newNode)
  {
    for_sourcelist (ORG_NODE (newNode), currOrgEdge)
    {
      if (ORG_NODE (newNode)->graph->directed || unique_edge (currOrgEdge))
      {
        PART_OF_EDGE_SET (currOrgEdge) = EdgeNone;
        
        nextOrgNode = NEXT_NODE (ORG_NODE (newNode), currOrgEdge);
        nextNewNode = NODE_REF (nextOrgNode);
        
        CopyOrgEdge (currOrgEdge, newNode, nextNewNode, FALSE);
        
        if (!BackwardEdgeExists (nextOrgNode, ORG_NODE (newNode)))
        {
          CopyOrgEdge (currOrgEdge, nextNewNode, newNode, TRUE);
        }  /* endif */
      }  /* endif */
    }  end_for_sourcelist (ORG_NODE (newNode), currOrgEdge);
  }  end_for_all_nodes (newGraph, newNode);
  
}  /* End of CopyPlaistedEdges */

/******************************************************************************/
/*    BackwardEdgeExists                                                      */
/*----------------------------------------------------------------------------*/
/*  Returns TRUE if the original graph was directed and an edge exists from   */
/*  nextOrgNode to orgNode.                                                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  nextOrgNode   Target node in the original graph.          */
/*                  orgNode       Source node in the original graph.          */
/*  Return value :  TRUE, if an edge exists from nextOrgNode to orgNode.      */
/******************************************************************************/
Local bool  BackwardEdgeExists  (Snode nextOrgNode, Snode orgNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  nextOrgEdge = NULL;
  
  /****************************************************************************/
  /*  Search for the possibly existing backward edge                          */
  /****************************************************************************/

  if (orgNode->graph->directed)
  {
    for_sourcelist (nextOrgNode, nextOrgEdge)
    {
      if (NEXT_NODE (nextOrgNode, nextOrgEdge) == orgNode)
      {
        return TRUE;
      }  /* endif */
    }  end_for_sourcelist (nextOrgNode, nextOrgEdge);
  }

  /****************************************************************************/
  /*  NO MULTI-EDGES!                                                         */
  /****************************************************************************/
  
  return  FALSE;
  
}  /* End of BackwardEdgeExists */

/******************************************************************************/
/*    CopyOrgEdge                                                             */
/*----------------------------------------------------------------------------*/
/*  Copies the specified edge into the new graph.                             */
/*  Source and target node are nodes in the new graph.                        */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  orgEdge      Edge to be copied into the new graph.        */
/*                  sourceNode   Source node of the new edge.                 */
/*                  targetNode   Target node of the new edge.                 */
/*                  artificial   Edge is an artificial copy of orgEdge.       */
/*  Return value :  The new edge in the new graph.                            */
/******************************************************************************/
Global Sedge  CopyOrgEdge  (Sedge orgEdge, Snode sourceNode, Snode targetNode, bool artificial)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  FFEdgeInfoPtr  newAttr = (FFEdgeInfoPtr) malloc (sizeof (FFEdgeInfo));
  Sedge          newEdge = make_edge (sourceNode, targetNode, 
                                      make_attr (ATTR_DATA, (char *) newAttr));
  
  /****************************************************************************/
  /*  Set the attribute fields                                                */
  /****************************************************************************/
  
  ORG_EDGE (newEdge) = orgEdge;
  CAPACITY (newEdge) = 1.0;
  FLOW (newEdge)     = 0.0;

  return newEdge;

}  /* End of CopyOrgEdge */

/******************************************************************************/
/*    AddDummyNode                                                            */
/*----------------------------------------------------------------------------*/
/*  Adds a dummy node to the specified graph and connects it with all other   */
/*  nodes in the graph.                                                       */
/*  The specified graph is a Plaisted graph, which means, that all created    */
/*  edges exist twice - one for each direction.                               */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Plaisted graph with an odd number of nodes.   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  AddDummyNode  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  FFNodeInfoPtr  newNAttr  = (FFNodeInfoPtr) malloc (sizeof (FFNodeInfo));
  Snode          dummyNode = make_node (currGraph, 
                                        make_attr(ATTR_DATA, (char *)newNAttr));
  Snode          currNode  = NULL;
  
  /****************************************************************************/
  /*  Create the dummy node.                                                  */
  /****************************************************************************/

  ORG_NODE (dummyNode)   = NULL;
  DUMMY_NODE (currGraph) = dummyNode;
  NUM_NODES (currGraph)++;

  /****************************************************************************/
  /*  Create edges between all nodes and the dummy node.                      */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    if (currNode != dummyNode)
    {
      CopyOrgEdge (NULL, dummyNode, currNode, TRUE);
      CopyOrgEdge (NULL, currNode, dummyNode, TRUE);
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);

}  /* End of AddDummyNode */

/******************************************************************************/
/*  Remove the graph                                                          */
/******************************************************************************/

/******************************************************************************/
/*    RemovePlaistedGraph                                                     */
/*----------------------------------------------------------------------------*/
/*  Removes the plaisted copy of the original graph including all attributes. */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph    Plaisted graph to remove                     */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  RemovePlaistedGraph  (Sgraph currGraph)
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
    
    if (ORG_NODE (currNode))
    {
      NODE_REF (ORG_NODE (currNode)) = NULL;
    }  /* endif */
    
    free (attr_data_of_type (currNode, FFNodeInfoPtr));
    
  }  end_for_all_nodes (currGraph, currNode);

  /****************************************************************************/
  /*  Remove the graph itself                                                 */
  /****************************************************************************/
  
  free (attr_data_of_type (currGraph, PGraphInfoPtr));
  remove_graph (currGraph);

}  /* End of RemovePlaistedGraph */

/******************************************************************************/
/*  End of  PlaistedGraph.c                                                   */
/******************************************************************************/
