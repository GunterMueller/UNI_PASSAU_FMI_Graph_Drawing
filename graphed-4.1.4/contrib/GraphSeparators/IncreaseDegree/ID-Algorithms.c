/******************************************************************************/
/*                                                                            */
/*    ID-Algorithms.c                                                         */
/*                                                                            */
/******************************************************************************/
/*  Provides the compaction heuristic for the various separator algorithms.   */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  28.06.1994                                                    */
/*  Modified :  28.08.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.1         Bug fixes                                         */
/*              1.0         First Revision                                    */
/******************************************************************************/

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "IncreaseDegree.h"
#include <Greedy.h>
#include <Kernighan-Lin.h>
#include <Fiduccia-Mattheyses.h>

/******************************************************************************/
/*  Implementation                                                            */
/******************************************************************************/

/******************************************************************************/
/*    Compaction                                                              */
/*----------------------------------------------------------------------------*/
/*  Uses the compaction heuristic on the various separator heursistics, when  */
/*  possible.                                                                 */
/*  1) First contract a maximum number of nodes using a matching              */
/*  2) Compute a separator of the contracted graph                            */
/*  3) Expand the contracted nodes and optimize the still valid Separator.    */
/*----------------------------------------------------------------------------*/
/*  Parameters   :  algInfo     Data to execute algorithm (graph, alpha...).  */
/*                  algSelect   Which separator algorithm is to use.          */
/*  Return value :  -----                                                     */
/******************************************************************************/
Global void  Compaction  (GSAlgInfoPtr algInfo, GSAlgSelect algSelect)
{
  /****************************************************************************/
  /*  Variable declaration                                                    */
  /****************************************************************************/

  int   maxASet = 0;
  int   maxBSet = 0;

#ifdef  ANIMATION_ON
  bool  oldAnimate = algInfo->animate;
#endif
  
  /****************************************************************************/
  /*  Disable animation (fast labeling does not like new nodes in graphed).   */
  /****************************************************************************/

#ifdef  ANIMATION_ON
  algInfo->animate = FALSE;
#endif
  
  /****************************************************************************/
  /*  Increase the average degree of the graph by contracting certain nodes   */
  /****************************************************************************/
  
  IncreaseDegree (GRAPH (algInfo));

  /****************************************************************************/
  /*  Prepare a proper input separator for the KL-algorithm and call it       */
  /****************************************************************************/

  if (algSelect != FIDUCCIA_MATTHEYSES)
  {
    ComputeNodeSetSizes (NUM_GRAPH_NODES(GRAPH (algInfo)), 
                          &maxASet, &maxBSet, ALPHA (algInfo));
    InsertDummys (GRAPH (algInfo), maxBSet - maxASet);
  }  /* endif */
  
  RandomInitialPartition (GRAPH (algInfo), &A_SET (algInfo), &B_SET (algInfo), 
                          NUM_GRAPH_NODES (GRAPH (algInfo))/2);
          
  switch (algSelect)
  { 
    case NAIVE               :  Naive              (algInfo, TRUE); break;
    case GREEDY              :  Greedy             (algInfo, TRUE); break;
    case KERNIGHAN_LIN       :  KernighanLin       (algInfo, TRUE); break;
    case KL_EXCHANGE         :  KLExchange         (algInfo, TRUE); break;
    case FIDUCCIA_MATTHEYSES :  FiducciaMattheyses (algInfo, TRUE); break;
    default                  :  break;
  }  /* endswitch */

  /****************************************************************************/
  /*  Expand all contracted nodes (Separator remains valid) and update dummys */
  /****************************************************************************/
  
  ExpandContractedNodes (algInfo);

  if (algSelect != FIDUCCIA_MATTHEYSES)
  {
    RemoveDummysFromNodeSet (&A_SET (algInfo));
    RemoveDummysFromNodeSet (&B_SET (algInfo));
  }  /* endif */
  
  AdjustPartition (algInfo, algSelect);
  
  /****************************************************************************/
  /*  Optimize the still valid separator.                                     */
  /****************************************************************************/
  
  switch (algSelect)
  { 
    case NAIVE               :  Naive              (algInfo, TRUE); break;
    case GREEDY              :  Greedy             (algInfo, TRUE); break;
    case KERNIGHAN_LIN       :  KernighanLin       (algInfo, TRUE); break;
    case KL_EXCHANGE         :  KLExchange         (algInfo, TRUE); break;
    case FIDUCCIA_MATTHEYSES :  FiducciaMattheyses (algInfo, TRUE); break;
    default                  :  break;
  }  /* endswitch */

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/
  
  if (algSelect != FIDUCCIA_MATTHEYSES)
  {
    RemoveDummysFromNodeSet (&A_SET (algInfo));
    RemoveDummysFromNodeSet (&B_SET (algInfo));
  }  /* endif */
  
  /****************************************************************************/
  /*  Disable animation (fast labeling does not like new nodes in graphed).   */
  /****************************************************************************/

#ifdef  ANIMATION_ON
  algInfo->animate = oldAnimate;
#endif
  
}  /* End of Compaction */

/******************************************************************************/
/*  End of  ID-Algorithms.c                                                   */
/******************************************************************************/
