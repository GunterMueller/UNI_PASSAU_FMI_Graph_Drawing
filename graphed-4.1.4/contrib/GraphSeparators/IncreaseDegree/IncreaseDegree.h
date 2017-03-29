/******************************************************************************/
/*                                                                            */
/*    IncreaseDegree.h                                                        */
/*                                                                            */
/******************************************************************************/
/*  Increase the degree of a graph by contracting certain pairs of nodes to   */
/*  a single node. A graph with a high average degree used as input for a     */
/*  separator algorithm, often yields a better separator, after the con-      */
/*  tracted nodes have been expanded again.                                   */
/*  The pairs of nodes to contract are computed using a standard matching     */
/*  algorithm.                                                                */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  03.05.1994                                                    */
/*  Modified :  26.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.2         Correct edge restauration                         */
/*              1.1         Save the node positions                           */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  INCREASE_DEGREE
#define  INCREASE_DEGREE

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include  <Separator.h>
#include  <FlowMatch.h>

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  MATCH_WEIGHT(e)   (attr_data_of_type ((e), Pair_of_edgevalues)->float1)
#define  FLOW_CAPACITY(e)  (attr_data_of_type ((e), Pair_of_edgevalues)->float1)
#define  FLOW_FLOW(e)      (attr_data_of_type ((e), Pair_of_edgevalues)->float2)
#define  SEP_ATTRS(e)  (attr_data_of_type ((e), Pair_of_edgevalues)->attrs.value.data)

#define  MAKE_FLOWMATCH_ATTR(e)  (make_attr(ATTR_DATA, (char *) (e)))

#define  IS_MATCHED(e)     (FLOW_FLOW(e) == 1.0)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global void  IncreaseDegree         (/*currGraph*/);
Global void  ExpandContractedNodes  (/*GSAlgInfoPtr  algInfo*/);
Global void  AdjustPartition        (/*algInfo*/);
Global void  Compaction             (/*algInfo, algSelect*/);
Global void  DumbMatching           (/*currGraph*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global void  IncreaseDegree         (Sgraph  currGraph);
Global void  ExpandContractedNodes  (GSAlgInfoPtr  algInfo);
Global void  AdjustPartition        (GSAlgInfoPtr  algInfo, 
                                     GSAlgSelect   algSelect);
Global void  Compaction             (GSAlgInfoPtr  algInfo, 
                                     GSAlgSelect   algSelect);
Global void  DumbMatching           (Sgraph  currGraph);
#endif

#endif

/******************************************************************************/
/*  End of  IncreaseDegree.h                                                  */
/******************************************************************************/
