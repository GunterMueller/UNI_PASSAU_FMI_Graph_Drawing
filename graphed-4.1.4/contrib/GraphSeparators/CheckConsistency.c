/******************************************************************************/
/*                                                                            */
/*    CheckConsistency.c                                                      */
/*                                                                            */
/******************************************************************************/
/*                                                                            */
/*  Contains the functions for the neccessary consistncy checks of algorithm  */
/*  and parameter combination as well as conditions on the graph.             */
/*                                                                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  31.08.1994                                                    */
/*  Modified :  31.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "CheckConsistency.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local bool  GraphIsConnected  ();
Local void  LabelComponent    ();
Local bool  ThisEdgeExists    ();
#endif
  
#ifdef  ANSI_HEADERS_ON
Local bool  GraphIsConnected  (Sgraph  currGraph);
Local void  LabelComponent    (Snode  currNode);
Local bool  ThisEdgeExists    (Sedge  currEdge);
#endif
  
/******************************************************************************/
/*  Consitency checks.                                                        */
/******************************************************************************/

/******************************************************************************/
/*    GraphIsConsistent                                                       */
/*----------------------------------------------------------------------------*/
/*  Checks the specified data for consistency, that is                        */
/*  -  In every case: The graph must not conatain self loops or multiedges.   */
/*  -  If the separator algorithm to be invoked is FiducciaMattheyses, then   */
/*     alpha must be bigger than 0.5.                                         */
/*  -  If the separator algorithm to be invoked is Plaisted, then             */
/*     the graph must be connected.                                           */
/*  -  If compaction is to be used, then the only heuristics other than       */
/*     Brute-Force or Plaisted are allowed.                                   */
/*  If any of these conditions is violated, FALSE is returned, else TRUE.     */
/*  The specified graph must be nonempty and must conain valid attributes.    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo      Data needed by the separator algorithms      */
/*                  algSelect    Which algorithm is to be called              */
/*                  compaction   Use the comapction heuristic                 */
/*  Return value :  TRUE, if the data is consistent.                          */
/******************************************************************************/
Global bool  GraphIsConsistent  (GSAlgInfoPtr algInfo, GSAlgSelect algSelect, bool compaction)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = empty_snode;
  Sedge  currEdge = empty_sedge;
  
  /****************************************************************************/
  /*  Test the "use not algorithm x with y" conditions.                       */
  /****************************************************************************/
  
  if (((algSelect == FIDUCCIA_MATTHEYSES) && (ALPHA (algInfo) == 0.5)) ||
      (compaction && ((algSelect == BRUTE_FORCE) || 
                      (algSelect == PLAISTED_A)  ||
                      (algSelect == PLAISTED_STAR))))
  {
    return  FALSE;
  }  /* endif */

  /****************************************************************************/
  /*  Test if Plaisted is to be used for graph connectedness.                 */
  /****************************************************************************/
  
  if ((algSelect == PLAISTED_A) && !GraphIsConnected (GRAPH (algInfo)))
  {
    return  FALSE;
  }  /* endif */

  /****************************************************************************/
  /*  Last but not least self loops and multiedges.                           */
  /****************************************************************************/
  
  for_all_nodes (GRAPH (algInfo), currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if (currEdge->snode == currEdge->tnode)
      {
        return FALSE;
      }
      else if (GRAPH (algInfo)->directed || unique_edge (currEdge))
      {
        if (ThisEdgeExists (currEdge))
        {
          return FALSE;
        }  /* endif */
      }  /* endif */
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (GRAPH (algInfo), currNode);

  /****************************************************************************/
  /*  Nothing bad happend                                                     */
  /****************************************************************************/
  
  return TRUE;
  
}  /* End of GraphIsConsistent */

/******************************************************************************/
/*    GraphIsConnected                                                        */
/*----------------------------------------------------------------------------*/
/*  Tests if the specified graph is connected.                                */
/*  The graph must contain valid attributes.                                  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Graph to test.                                */
/*  Return value :  TRUE, if the graph is connected.                          */
/******************************************************************************/
Local bool  GraphIsConnected  (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = empty_snode;
  
  /****************************************************************************/
  /*  Label the component containing the first node.                          */
  /****************************************************************************/
  
  InitNodeMarkers (currGraph);
  LabelComponent (currGraph->nodes);

  /****************************************************************************/
  /*  Check if the labeled compononent is the whole graph.                    */
  /****************************************************************************/
  
  for_all_nodes (currGraph, currNode)
  {
    if (!IS_NODE_VISITED (currNode))
    {
      return  FALSE;
    }  /* endif */
  }  end_for_all_nodes (currGraph, currNode);

  /****************************************************************************/
  /*  Nothing bad happend                                                     */
  /****************************************************************************/
  
  return TRUE;
  
}  /* End of GraphIsConnected */

/******************************************************************************/
/*    LabelComponent                                                          */
/*----------------------------------------------------------------------------*/
/*  Sets recursivly the labels of the component starting with the specified   */
/*  node using the visited flag.                                              */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode  Label from this node onwards.                   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  LabelComponent  (Snode currNode)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge = empty_sedge;
  Snode  nextNode = empty_snode;
  
  /****************************************************************************/
  /*  Label the current node.                                                 */
  /****************************************************************************/
  
  IS_NODE_VISITED (currNode) = TRUE;

  /****************************************************************************/
  /*  Label across inzidentt edges to adjacent nodes.                         */
  /****************************************************************************/
  
  for_sourcelist (currNode, currEdge)
  {
    nextNode = NEXT_NODE (currNode, currEdge);
    
    if (!IS_NODE_VISITED (nextNode))
    {
      LabelComponent (nextNode);
    }  /* endif */
  }  end_for_sourcelist (currNode, currEdge);
  
  if (currNode->graph->directed)
  {
    for_targetlist (currNode, currEdge)
    {
      nextNode = NEXT_NODE (currNode, currEdge);
      
      if (!IS_NODE_VISITED (nextNode))
      {
        LabelComponent (nextNode);
      }  /* endif */
    }  end_for_targetlist (currNode, currEdge);
  }  /* endif */

}  /* End of LabelComponent */

/******************************************************************************/
/*    ThisEdgeExists                                                          */
/*----------------------------------------------------------------------------*/
/*  Searches the sourcelist of the sourcenode of the edge for an edge with    */
/*  the same end-nodes, that is diffrent from the given edge.                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currEdge   Does this edge exist twice ?                   */
/*  Return value :  TRUE, if such an edge exists.                             */
/******************************************************************************/
Local bool  ThisEdgeExists  (Sedge currEdge)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  testEdge = empty_sedge;
  
  /****************************************************************************/
  /*  Search the edge.                                                        */
  /****************************************************************************/
  
  for_sourcelist (currEdge->snode, testEdge)
  {
    if ((currEdge != testEdge) && (currEdge->tnode == testEdge->tnode))
    {
      return TRUE;
    }  /* endif */
  }  end_for_sourcelist (currEdge->snode, testEdge);

  /****************************************************************************/
  /* No edge found.                                                           */
  /****************************************************************************/
  
  return FALSE;

}  /* End of ThisEdgeExists */

/******************************************************************************/
/*  End of  CheckConsistency.c                                                */
/******************************************************************************/
