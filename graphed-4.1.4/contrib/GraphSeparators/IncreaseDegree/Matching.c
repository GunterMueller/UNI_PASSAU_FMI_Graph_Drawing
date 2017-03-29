/******************************************************************************/
/*                                                                            */
/*    Matching.c                                                              */
/*                                                                            */
/******************************************************************************/
/*  A heuristic to compute a matching (a rather dumb one ...).                */
/*  Uses the Attribute fild needed for the real matching algorithm.           */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  21.08.1994                                                    */
/*  Modified :  21.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "IncreaseDegree.h"

/******************************************************************************/
/*  Compute a matching.                                                       */
/******************************************************************************/

/******************************************************************************/
/*    DumbMatching                                                            */
/*----------------------------------------------------------------------------*/
/*  Computes a matching on the given graph according to a very dumb heuristic.*/
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Graph to compute a matching on.               */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  DumbMatching (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Slist  EdgeList  = empty_slist;
  Slist  currEntry = empty_slist;
  Snode  currNode  = NULL;
  Sedge  currEdge  = NULL;
  
  /****************************************************************************/
  /*  Compute the list of edges and initialize the visited flag.              */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    IS_NODE_VISITED (currNode) = FALSE;

    for_sourcelist (currNode, currEdge)
    {
      if (currGraph->directed || unique_edge (currEdge))
      {
        EdgeList = add_immediately_to_slist (EdgeList,
                   CREATE_EDGESET_ENTRY (currEdge));
      }  /* endif */
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (currGraph, currNode);

  /****************************************************************************/
  /*  Match all edges with endnodes not yet in a matched edge.                */
  /****************************************************************************/

  for_slist (EdgeList, currEntry)
  {
    currEdge = GET_EDGELIST_ENTRY (currEntry);
    if ((!IS_NODE_VISITED (currEdge->snode)) && 
        (!IS_NODE_VISITED (currEdge->tnode)))
    {
      FLOW_FLOW (currEdge) = 1.0;
      IS_NODE_VISITED (currEdge->snode) = TRUE;
      IS_NODE_VISITED (currEdge->tnode) = TRUE;
    }  /* endif */
  }  end_for_slist (EdgeList, currEntry);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/

  free_slist (EdgeList);
  
}  /* End of DumbMatching */

/******************************************************************************/
/*  End of Matching.c                                                         */
/******************************************************************************/
