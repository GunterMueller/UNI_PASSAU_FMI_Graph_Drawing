/******************************************************************************/
/*                                                                            */
/*    Interface.c                                                             */
/*                                                                            */
/******************************************************************************/
/*  Standard routines for calling the algorithms to compute graph-separators. */
/*  System independend through conditional compilation.                       */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  12.03.1994                                                    */
/*  Modified :  13.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              2.1         Fast labeling.                                    */
/*              2.0         Openwindows interface.                            */
/*              1.1         Support for NeXT and SUN-version.                 */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include  "Interface.h"
Local void ClearLabels (Sgraph  currGraph); /* Changed to local MH 26/10/93 */

/******************************************************************************/
/*  Compute a graph separator from a sgraph...                                */
/******************************************************************************/

/******************************************************************************/
/*    ComputeSeparator                                                        */
/*----------------------------------------------------------------------------*/
/*  Prepares the graph by creating the proper attributes and then calls the   */
/*  selected algorithm. Removes the attributes after return.                  */
/*  The labels of the graph are set according to the result.                  */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo      Data needed by the separator algorithms      */
/*                  algSelect    Which algorithm is to be called              */
/*                  compaction   Use the comapction heuristic                 */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  ComputeSeparator  (GSAlgInfoPtr algInfo, GSAlgSelect algSelect, bool compaction)
{
  /****************************************************************************/
  /*  Is alpha invalid or currGraph empty ? Then do nothing                   */
  /****************************************************************************/

  if ((ALPHA (algInfo) < 0.5) || (ALPHA (algInfo) >= 1.0) || 
      (GRAPH (algInfo) == empty_sgraph))
  {
    if (GRAPH (algInfo) == empty_sgraph)
    {
      message ("\n\tGraph is empty !\n");
    }
    else if ((ALPHA (algInfo) < 0.5) || (ALPHA (algInfo) >= 1.0))
    {
      message ("\n\tWrong alpha entered !\n");
    }  /* endif */
    
    return;
  }  /* endif */
  
  InitAttributes (GRAPH (algInfo), FALSE);

  if (!GraphIsConsistent (algInfo, algSelect, compaction))
  {
    message ("\n\t\tGraph contains multiedges or self loops,");
    message ("\n\tor\tit is not connected (Plaisted),");
    message ("\n\tor\talpha = 1/2 + Fiduccia-Mattheyses");
    message ("\n\tor\tCompaction with invalid algorithm !\n");
    return;
  }  /* endif */
  
  ClearLabels (GRAPH (algInfo));

  /****************************************************************************/
  /*  Compute the separator                                                   */
  /****************************************************************************/

  if (compaction)
  {
    Compaction (algInfo, algSelect);
  }
  else
  {
    InvokeSeparatorAlgorithm (algInfo, algSelect);
  }  /*endif */

  /****************************************************************************/
  /*  Label the nodes and edges according to the result                       */
  /****************************************************************************/

  if (compaction)
  {
    SlowClearAndSetLabels (GRAPH (algInfo));
  }
  else
  {
    ClearAndSetLabels (GRAPH (algInfo));
  }  /* endif */
  
  RemoveAttributes (GRAPH (algInfo));

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/

  free_slist (A_SET (algInfo));
  free_slist (B_SET (algInfo));
  free_slist (SEP_SET (algInfo));
  
  A_SET (algInfo)   = empty_slist;
  B_SET (algInfo)   = empty_slist;
  SEP_SET (algInfo) = empty_slist;

}  /* End of ComputeSeparator */

/******************************************************************************/
/*    InvokeSeparatorAlgorithm                                                */
/*----------------------------------------------------------------------------*/
/*  Invokes the specified separator algorithm.                                */
/*  Attributes MUST be set already.                                           */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo      Data needed by the separator algorithms      */
/*                  algSelect    Which algorithm is to be called              */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InvokeSeparatorAlgorithm  (GSAlgInfoPtr algInfo, GSAlgSelect algSelect)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sgraph  PlaistedGraph = empty_sgraph;

  /****************************************************************************/
  /*  Compute the separator                                                   */
  /****************************************************************************/

  switch (algSelect)
  {
    case BRUTE_FORCE        :  
      BruteForce (algInfo);         
      break;
    case NAIVE              :  
      Naive (algInfo, FALSE); 
      break;
    case GREEDY             :  
      Greedy (algInfo, FALSE); 
      break;
    case KERNIGHAN_LIN      :  
      KernighanLin (algInfo, FALSE);     
      break;
    case KL_EXCHANGE        :  
      KLExchange  (algInfo, FALSE);            
      break;
    case FIDUCCIA_MATTHEYSES:  
      FiducciaMattheyses (algInfo, FALSE);     
      break;
    case PLAISTED_A         :  
      PlaistedGraph = InitPlaistedGraph (GRAPH (algInfo));
      PlaistedA (PlaistedGraph, 
                  &A_SET (algInfo), &B_SET (algInfo), &SEP_SET (algInfo));
      RemovePlaistedGraph (PlaistedGraph);
      break;
    case PLAISTED_STAR      :  
      PlaistedGraph = InitPlaistedGraph (GRAPH (algInfo));
      PlaistedAStar (PlaistedGraph, 
                     &A_SET (algInfo), &B_SET (algInfo), &SEP_SET (algInfo));
      RemovePlaistedGraph (PlaistedGraph);
      break;
    default                :  break;
  }  /* endswitch */
}  /* End of InvokeSeparatorAlgorithm */

/******************************************************************************/
/*  Set the labels according to the result of the computation                 */
/******************************************************************************/

/******************************************************************************/
/*    ClearLabels                                                             */
/*----------------------------------------------------------------------------*/
/*  Removes all labels from the specified graph (nodes and edges) to repre-   */
/*  sent a separator later with label values.                                 */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   A valid Sgraph                                */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void ClearLabels (Sgraph currGraph)  /* Changed to local MH 26/10/93 */
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
#ifdef ANIMATION_ON
  Slist          allNodes  = make_slist_of_sgraph (currGraph);
  Graphed_group  nodeGroup = create_graphed_group_from_slist (allNodes);
#endif

  /****************************************************************************/
  /*  Is alpha invalid or currGraph empty ? Then do nothing                   */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    for_sourcelist (currNode, currEdge)
    {
      if (currEdge->label != NULL)
      {
        free (currEdge->label);
      }  /* endif */ 

      set_edgelabel (currEdge, NO_LABEL);
#ifdef ANIMATION_ON
      if (graphed_edge (currEdge) != NULL)
      {
        edge_set (graphed_edge (currEdge), ONLY_SET, EDGE_LABEL, "", 0);
      }  /* endif */
#endif
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (currGraph, currNode);
  
#ifdef ANIMATION_ON
  group_set (nodeGroup, RESTORE_IT, 0);
  free_group (nodeGroup);
  free_slist (allNodes);
#endif
}

/******************************************************************************/
/*    SetLabels                                                               */
/*----------------------------------------------------------------------------*/
/*  Sets the labels of all nodes and the edges of an edge separator to the    */
/*  defined values.                                                           */
/*  Edges not in the separator may have bogus labels if not cleared.          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  SeparatorSet   Set of edges in the separator.             */
/*                  LeftSet        One node set of the node partition.        */
/*                  RightSet       The other node set of the node partition.  */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void SetLabels (Slist SeparatorSet, Slist LeftNodeSet, Slist RightNodeSet)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Slist  currEntry = empty_slist;
#ifdef ANIMATION_ON
  Graphed_group  nodeLeft  = create_graphed_group_from_slist (LeftNodeSet);
  Graphed_group  nodeRight = create_graphed_group_from_slist (RightNodeSet);
#endif

  /****************************************************************************/
  /*  Label the nodes and edges according to the result                       */
  /****************************************************************************/

  for_slist (SeparatorSet, currEntry)    
  {
    if (GET_SEPARATOR_EDGE(currEntry)->label != NULL)
    {
      free (GET_SEPARATOR_EDGE(currEntry)->label);
    }  /* endif */ 

    set_edgelabel (GET_SEPARATOR_EDGE(currEntry), SEPARATOR_LABEL);

#ifdef ANIMATION_ON
    if (graphed_edge (GET_SEPARATOR_EDGE(currEntry)) != NULL)
    {
      edge_set (graphed_edge (GET_SEPARATOR_EDGE(currEntry)), ONLY_SET,
                              EDGE_LABEL, "S", 0);
    }  /* endif */
#endif
  }  end_for_slist (SeparatorSet, currEntry);
  
  for_slist (LeftNodeSet, currEntry)
  {
    if (GET_NODESET_ENTRY(currEntry)->label != NULL)
    {
      free (GET_NODESET_ENTRY(currEntry)->label);
    }  /* endif */ 

    set_nodelabel (GET_NODESET_ENTRY(currEntry), LEFT_LABEL);

#ifdef ANIMATION_ON
    if (graphed_node (GET_NODESET_ENTRY(currEntry)) != NULL)
    {
      node_set (graphed_node (GET_NODESET_ENTRY(currEntry)),  ONLY_SET,
                              NODE_LABEL, "A", 0);
    }  /* endif */
#endif
  }  end_for_slist (LeftNodeSet, currEntry);

  for_slist (RightNodeSet, currEntry)
  {
    if (GET_NODESET_ENTRY(currEntry)->label != NULL)
    {
      free (GET_NODESET_ENTRY(currEntry)->label);
    }  /* endif */ 

    set_nodelabel (GET_NODESET_ENTRY(currEntry), RIGHT_LABEL);

#ifdef ANIMATION_ON
    if (graphed_node (GET_NODESET_ENTRY(currEntry)) != NULL)
    {
      node_set (graphed_node (GET_NODESET_ENTRY(currEntry)),  ONLY_SET,
                              NODE_LABEL, "B", 0);
    }  /* endif */
#endif
  }  end_for_slist (RightNodeSet, currEntry);
  
#ifdef ANIMATION_ON
  group_set (nodeLeft, RESTORE_IT, 0);
  group_set (nodeRight, RESTORE_IT, 0);
  free_group (nodeLeft);
  free_group (nodeRight);
#endif
}

/******************************************************************************/
/*    ClearAndSetLabels                                                       */
/*----------------------------------------------------------------------------*/
/*  Sets the labels of all nodes and the edges of an edge separator to the    */
/*  defined values.                                                           */
/*  Edges not in the separator are assigned an empty label.                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Clear and set all labels of this graph.       */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void ClearAndSetLabels (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode    currNode = NULL;
  Sedge    currEdge = NULL;
  EdgeSet  currSet  = EdgeNone;
#ifdef ANIMATION_ON
  Graphed_node   gnode = NULL;
  Graphed_edge   gedge = NULL;
  Slist          allNodes  = make_slist_of_sgraph (currGraph);
  Graphed_group  nodeGroup = create_graphed_group_from_slist (allNodes);
#endif

  /****************************************************************************/
  /*  Label the nodes and edges according to the result                       */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    if (currNode->label != NULL)
    {
      free (currNode->label);
    }  /* endif */ 
    
    set_nodelabel (currNode, strsave (PART_OF_NODE_SET(currNode) == LeftSet ? 
                               LEFT_LABEL : RIGHT_LABEL));

#ifdef ANIMATION_ON
    gnode = graphed_node (currNode);
    if (gnode != NULL)
    {
      node_set (gnode, ONLY_SET,
                NODE_LABEL, PART_OF_NODE_SET(currNode) == LeftSet ? "A" : "B",
                0);
    }  /* endif */
#endif

    for_sourcelist (currNode, currEdge)
    {
      if (currEdge->label != NULL)
      {
        free (currEdge->label);
      }  /* endif */ 

      currSet = (PART_OF_NODE_SET(currNode) != 
                 PART_OF_NODE_SET(NEXT_NODE(currNode, currEdge))) ?
                   Separator_Set : EdgeNone;
      set_edgelabel (currEdge, currSet == Separator_Set ?
                                 SEPARATOR_LABEL : NO_LABEL);
#ifdef ANIMATION_ON
      gedge = graphed_edge (currEdge);
      if (gedge != NULL)
      {
        edge_set (gedge, ONLY_SET,
                  EDGE_LABEL, (currSet == Separator_Set) ? "S" : "");
      }  /* endif */
#endif
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (currGraph, currNode);
  
#ifdef ANIMATION_ON
  group_set (nodeGroup, RESTORE_IT, 0);
  free_group (nodeGroup);
  free_slist (allNodes);
#endif
}

/******************************************************************************/
/*    SlowClearAndSetLabels                                                   */
/*----------------------------------------------------------------------------*/
/*  Sets the labels of all nodes and the edges of an edge separator to the    */
/*  defined values.                                                           */
/*  Edges not in the separator are assigned an empty label.                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currGraph   Clear and set all labels of this graph.       */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void SlowClearAndSetLabels (Sgraph currGraph)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode    currNode = NULL;
  Sedge    currEdge = NULL;
  EdgeSet  currSet  = EdgeNone;
#ifdef ANIMATION_ON
  Graphed_node  gnode = NULL;
  Graphed_edge  gedge = NULL;
#endif

  /****************************************************************************/
  /*  Label the nodes and edges according to the result                       */
  /****************************************************************************/

  for_all_nodes (currGraph, currNode)
  {
    if (currNode->label != NULL)
    {
      free (currNode->label);
    }  /* endif */ 
    
    set_nodelabel (currNode, strsave (PART_OF_NODE_SET(currNode) == LeftSet ? 
                               LEFT_LABEL : RIGHT_LABEL));

#ifdef ANIMATION_ON
    gnode = graphed_node (currNode);
    if (gnode != NULL)
    {
      node_set (gnode, 
                NODE_LABEL, PART_OF_NODE_SET(currNode) == LeftSet ? "A" : "B",
                0);
    }  /* endif */
#endif

    for_sourcelist (currNode, currEdge)
    {
      if (currEdge->label != NULL)
      {
        free (currEdge->label);
      }  /* endif */ 

      currSet = (PART_OF_NODE_SET(currNode) != 
                 PART_OF_NODE_SET(NEXT_NODE(currNode, currEdge))) ?
                   Separator_Set : EdgeNone;
      set_edgelabel (currEdge, currSet == Separator_Set ?
                                 SEPARATOR_LABEL : NO_LABEL);
#ifdef ANIMATION_ON
      gedge = graphed_edge (currEdge);
      if (gedge != NULL)
      {
        edge_set (gedge, EDGE_LABEL, (currSet == Separator_Set) ? "S" : "");
      }  /* endif */
#endif
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (currGraph, currNode);
}  /* End of SlowClearAndSetLabels */

/******************************************************************************/
/*  End of  Interface.c                                                       */
/******************************************************************************/
