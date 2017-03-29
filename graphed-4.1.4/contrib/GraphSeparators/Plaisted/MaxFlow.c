/******************************************************************************/
/*                                                                            */
/*    MaxFlow.c                                                               */
/*                                                                            */
/******************************************************************************/
/*                                                                            */
/*  Implementation of the Max-Flow / Min-Cut algorithm of Ford and Fulkerson. */
/*                                                                            */
/*  The algorithms work on networks. These are directed graphs with exactly   */
/*  one source and one sink. The input networks MUST have the neccessary      */
/*  Attributes.                                                               */
/*                                                                            */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  16.04.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "MaxFlow.h"

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Local void  InitFlow      (/*NetWork*/);
Local bool  ScanFromNode  (/*currNode, ScanSet, Target*/);
Local bool  LabelBackward (/*currNode, ScanSet, Target*/);
Local bool  LabelForward  (/*currNode, ScanSet, Target*/);
Local void  AugmentFlow   (/*Target*/);
#endif

#ifdef  ANSI_HEADERS_ON
Local void  InitFlow      (Sgraph  NetWork);
Local bool  ScanFromNode  (Snode  currNode, AQueuePtr  ScanSet, Snode  Target);
Local bool  LabelForward  (Snode  currNode, AQueuePtr  ScanSet, Snode  Target);
Local bool  LabelBackward (Snode  currNode, AQueuePtr  ScanSet, Snode  Target);
Local void  AugmentFlow   (Snode  Target);
#endif

/******************************************************************************/
/*  The Ford-Fulkerson Max-Flow and the Min-Cut algorithm.                    */
/******************************************************************************/

/******************************************************************************/
/*    FordFulkerson                                                           */
/*----------------------------------------------------------------------------*/
/*  Implementation of the Ford-Fulkerson labeling algorithm to compute the    */
/*  maximum flow through a network.                                           */
/*  The implementation is according to Papadimitriou-Steiglitz with the       */
/*  shortest-augmenting-path improvement. Estimated running time: O(|V|*|E|)  */
/*                                                                            */
/*  When the algorithm is called, the nodes and arcs must have the proper     */
/*  attributes (including correct capacities and such).                       */
/*  The algorithm returns the maximum flow. The nodes which are labled (where */
/*  the From-Attribute != NULL) are the nodes on the "left" side of the min-  */
/*  imum cut, whereas the unlabled nodes are on the right side of the cut.    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  NetWork   Directed graph with the proper Attributes.      */
/*                  Source    Source node of the network (NO incoming arcs).  */
/*                  Target    Target node of the network (NO outgoing arcs).  */
/*  Return value :  Amount of the maximum flow through the NetWork.           */
/******************************************************************************/
Global float  FordFulkerson  (Sgraph NetWork, Snode Source, Snode Target)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  float       currFlow     = 0.0;
  AQueuePtr   ScanSet      = InitAQueue ();
  bool        TargetLabled = FALSE;
  Sedge       sourceEdge   = NULL;
  float       upperBound   = 0.0;
  Attributes  ScanAttribute;
  
  /****************************************************************************/
  /*  Init the network.                                                       */
  /****************************************************************************/

  InitFlow (NetWork);

  for_sourcelist (Source, sourceEdge)
  {
    upperBound += CAPACITY (sourceEdge);
  }  end_for_sourcelist (Source, sourceEdge);


  /****************************************************************************/
  /*  Search for augmenting paths as long as they exist.                      */
  /****************************************************************************/

  do
  {
    /* Initialize for the search of the new augmenting path                   */
    InitLabels (NetWork);
    EnAQueue (ScanSet, CREATE_SCANSET_ENTRY (Source));
    EXTRA_FLOW (Source) = upperBound;
    IS_LABLED (Source)  = TRUE;
    TargetLabled        = FALSE;
    
    /* Scan the network until Target is reached or the ScanSet is empty.      */
    /* (An empty ScanSet means, that the maximum flow has been found)         */
    while (!AQUEUE_IS_EMPTY (ScanSet) && !TargetLabled)
    {
      ScanAttribute = DeAQueue (ScanSet);
      TargetLabled  = ScanFromNode ((Snode)ScanAttribute.value.data, ScanSet, Target);
      if (TargetLabled)
      {
        AugmentFlow (Target);
        currFlow = currFlow + EXTRA_FLOW (Target);
        EmptyAQueue (ScanSet);
      } /* endif */
    } /* endwhile */
  }  while (TargetLabled);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  RemoveAQueue (ScanSet);
  return currFlow;
  
}  /* End of FordFulkerson */

/******************************************************************************/
/*    MinCut                                                                  */
/*----------------------------------------------------------------------------*/
/*  Computes the minimum cut of the specified network.                        */
/*  This is done by calling the max-flow algorithm of Ford-Fulkerson. The     */
/*  labels on the nodes provided by this algorithm are then used to determine */
/*  to which node set a node belongs.                                         */
/*  The sets MUST be empty when this algorithm is called.                     */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  NetWork    Input network to compute the min-cut on.       */
/*                  Source     Source node of the network (NO incoming arcs). */
/*                  Target     Target node of the network (NO outgoing arcs). */
/*                  NodeSetA   The left side of the cut (with source).        */
/*                  NodeSetB   The right side of the cut (with target).       */
/*                  SepArcs    Arcs of the cut.                               */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  MinCut  (Sgraph NetWork, Snode Source, Snode Target, Slist *NodeSetA, Slist *NodeSetB, Slist *SepArcs)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Compute the max-flow and with it the node labels.                       */
  /****************************************************************************/
  
  FordFulkerson  (NetWork, Source, Target);
  
  /****************************************************************************/
  /*  Fill the node and arc sets                                              */
  /****************************************************************************/
  
  for_all_nodes (NetWork, currNode)
  {
    /* Assign the nodes to their node sets.                                   */
    if (IS_LABLED (currNode))
    {
      *NodeSetA = add_immediately_to_slist (*NodeSetA, 
                                            MAKE_NODESET_ENTRY (currNode));
    }
    else
    {
      *NodeSetB = add_immediately_to_slist (*NodeSetB, 
                                            MAKE_NODESET_ENTRY (currNode));
    }  /* endif */
    
    /* Search the sourcelist for separating arcs                              */
    for_sourcelist (currNode, currEdge)
    {
      if (IS_LABLED (currNode) != IS_LABLED (currEdge->tnode))
      {
        *SepArcs = add_immediately_to_slist (*SepArcs, 
                                             MAKE_SEPARC_ENTRY (currEdge));
      }  /* endif */
    }  end_for_sourcelist (currNode, currEdge);
    
    /* Search the targetlist for separating arcs                              */
    for_targetlist (currNode, currEdge)
    {
      if (IS_LABLED (currNode) != IS_LABLED (currEdge->snode))
      {
        *SepArcs = add_immediately_to_slist (*SepArcs, 
                                             MAKE_SEPARC_ENTRY (currEdge));
      }  /* endif */
    }  end_for_targetlist (currNode, currEdge);
    
  }  end_for_all_nodes (NetWork, currNode);

}  /* End of MinCut */

/******************************************************************************/
/*  Initialization of the network                                             */
/******************************************************************************/

/******************************************************************************/
/*    InitFlow                                                                */
/*----------------------------------------------------------------------------*/
/*  Resets the flow value of each arc in the network.                         */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  NetWork   Graph where the Attributes need initializing.   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  InitFlow  (Sgraph NetWork)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode = NULL;
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Reset the flow value.                                                   */
  /****************************************************************************/
  
  for_all_nodes (NetWork, currNode)
  {
    /* A network is directed !                                                */
    for_sourcelist (currNode, currEdge)
    {
      FLOW (currEdge) = 0.0;
    }  end_for_sourcelist (currNode, currEdge);
  }  end_for_all_nodes (NetWork, currNode);

}  /* End of InitFlow */

/******************************************************************************/
/*    InitLabels                                                              */
/*----------------------------------------------------------------------------*/
/*  Resets the labels of the nodes to their default values.                   */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  NetWork   Graph where the Attributes need initializing.   */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  InitLabels  (Sgraph NetWork)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode = NULL;
  
  /****************************************************************************/
  /*  Reset the labels.                                                       */
  /****************************************************************************/
  
  for_all_nodes (NetWork, currNode)
  {
    IS_LABLED (currNode)   = FALSE;
    FROM_NODE (currNode)   = NULL;
    FROM_ARC (currNode)    = NULL;
    BACK_LABLED (currNode) = FALSE;
    EXTRA_FLOW (currNode)  = 0.0;
  }  end_for_all_nodes (NetWork, currNode);

}  /* End of InitLabels */

/******************************************************************************/
/*  The labeling procedure                                                    */
/******************************************************************************/

/******************************************************************************/
/*    ScanFromNode                                                            */
/*----------------------------------------------------------------------------*/
/*  Calls the labeling procedures for the forward and the backward case.      */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode   Propagate labels to adjacent nodes.            */
/*                  ScanSet    Enqueue unlabled nodes.                        */
/*                  Target     Sink of the network.                           */
/*  Return value :  TRUE if the sink of the network has been found.           */
/******************************************************************************/
Local bool  ScanFromNode (Snode currNode, AQueuePtr ScanSet, Snode Target)
{
  /****************************************************************************/
  /*  Propagate labels forward and backward.                                  */
  /****************************************************************************/
  
  if (LabelForward  (currNode, ScanSet, Target))
  {
    return TRUE;
  } /* endif */
  
  return  LabelBackward (currNode, ScanSet, Target);

}  /* End of ScanFromNode */

/******************************************************************************/
/*    LabelForward                                                            */
/*----------------------------------------------------------------------------*/
/*  Propagates labels across forward arcs to all unlabled nodes which are     */
/*  adjacent to currNode. The newly labled nodes are enqueued in the ScanSet. */
/*  The From label enables AugmentFlow later to find the augmenting path      */
/*  backwards, whereas ExtraFlow yields the amount of extra flow from the     */
/*  Source to the newly labled node.                                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode   Propagate labels to target nodes.              */
/*                  ScanSet    Enqueue unlabled nodes.                        */
/*                  Target     Sink of the network.                           */
/*  Return value :  TRUE if the sink of the network has been found.           */
/******************************************************************************/
Local bool  LabelForward (Snode currNode, AQueuePtr ScanSet, Snode Target)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Propagate labels forward.                                               */
  /****************************************************************************/
  
  for_sourcelist (currNode, currEdge)
  {
    /* Label forward across unsaturated arcs to unlabled nodes.               */
    if ((FLOW (currEdge) < CAPACITY (currEdge)) && !IS_LABLED (currEdge->tnode))
    {
      IS_LABLED (currEdge->tnode)   = TRUE;
      FROM_NODE (currEdge->tnode)   = currNode;
      FROM_ARC  (currEdge->tnode)   = currEdge;
      BACK_LABLED (currEdge->tnode) = FALSE;
      EXTRA_FLOW (currEdge->tnode)  = minimum (EXTRA_FLOW (currNode), 
                                        CAPACITY (currEdge) - FLOW (currEdge));

      if (currEdge->tnode == Target)
      {
        return TRUE;
      }
      else
      {
        EnAQueue (ScanSet, CREATE_SCANSET_ENTRY (currEdge->tnode));
      } /* endif */
    } /* endif */
  }  end_for_sourcelist (currNode, currEdge);
  
  return FALSE;
  
}  /* End of LabelForward */

/******************************************************************************/
/*    LabelBackward                                                           */
/*----------------------------------------------------------------------------*/
/*  Propagates labels across backward arcs to all unlabled nodes which are    */
/*  adjacent to currNode. The newly labled nodes are enqueued in the ScanSet. */
/*  The From label enables AugmentFlow later to find the augmenting path      */
/*  backwards, whereas ExtraFlow yields the amount of extra flow from the     */
/*  Source to the newly labled node.                                          */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  currNode   Propagate labels to source nodes.              */
/*                  ScanSet    Enqueue unlabled nodes.                        */
/*                  Target     Sink of the network.                           */
/*  Return value :  TRUE if the sink of the network has been found.           */
/******************************************************************************/
Local bool  LabelBackward (Snode currNode, AQueuePtr ScanSet, Snode Target)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/
  
  Sedge  currEdge = NULL;
  
  /****************************************************************************/
  /*  Propagate labels backward.                                              */
  /****************************************************************************/
  
  for_targetlist (currNode, currEdge)
  {
    /* Label backward across nonempty arcs to unlabled nodes.                 */
    if ((FLOW (currEdge) > 0.0) && !IS_LABLED (currEdge->snode))
    {
      IS_LABLED (currEdge->snode)   = TRUE;
      FROM_NODE (currEdge->snode)   = currNode;
      FROM_ARC  (currEdge->snode)   = currEdge;
      BACK_LABLED (currEdge->snode) = TRUE;
      EXTRA_FLOW (currEdge->snode)  = minimum (EXTRA_FLOW (currNode), 
                                               FLOW (currEdge));
      if (currEdge->snode == Target)
      {
        return TRUE;
      }
      else
      {
        EnAQueue (ScanSet, CREATE_SCANSET_ENTRY (currEdge->snode));
      } /* endif */
    } /* endif */
  }  end_for_targetlist (currNode, currEdge);
  
  return FALSE;
  
}  /* End of LabelBackward */

/******************************************************************************/
/*  Augment the flow along an augmenting path.                                */
/******************************************************************************/

/******************************************************************************/
/*    AugmentFlow                                                             */
/*----------------------------------------------------------------------------*/
/*  Reconstructs the augmenting path through the node labels and augments the */
/*  flow of the arcs on the path accordingly.                                 */
/*  The FromNode field of the source node MUST be NULL.                       */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  Target  Go backwards along the From labels starting here. */
/*  Return value :  -----                                                     */
/******************************************************************************/
Local void  AugmentFlow  (Snode Target)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  Snode  currNode  = Target;
  float  extraFlow = EXTRA_FLOW (Target);
  
  /****************************************************************************/
  /*  Follow the labels backward along the augmenting path to the source node */
  /****************************************************************************/

  while (FROM_NODE (currNode) != NULL)
  {
    /* Augment the flow according to the backward or forward direction.       */
    if (!BACK_LABLED (currNode))
    {
      FLOW (FROM_ARC (currNode)) += extraFlow;
    }
    else
    {
      FLOW (FROM_ARC (currNode)) -= extraFlow;
    }  /*endif */
    
    currNode = FROM_NODE (currNode);
  }  /* endwhile */

}  /* End of AugmentFlow */

/******************************************************************************/
/*  End of  MaxFlow.c                                                         */
/******************************************************************************/
