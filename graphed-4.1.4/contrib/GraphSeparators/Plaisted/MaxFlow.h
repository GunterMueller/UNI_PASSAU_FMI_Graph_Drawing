/******************************************************************************/
/*                                                                            */
/*    MaxFlow.h                                                               */
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

#ifndef MAX_FLOW
#define MAX_FLOW

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>

#include <AttrsQueue.h>

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

struct _FFNodeInfo
{
  /* Flags and variables used by FordFulkerson:                               */
  bool   IsLabled;    /* Node was labled.                                     */
  Snode  FromNode;    /* Node from where this node was labeled.               */
  Sedge  FromArc;     /* Arc over which the label was propagated.             */
  bool   BackLabled;  /* Labeling this node was done accross a backward edge. */
  float  ExtraFlow;   /* Amount of extra flow from Source to this node.       */
  
  /* References to the elements of the original graph.                        */
  Snode       orgNode;
  Attributes  attrs;
};
typedef  struct _FFNodeInfo   FFNodeInfo;
typedef  FFNodeInfo          *FFNodeInfoPtr;

struct _FFEdgeInfo
{
  /* Flags and variables used by MaxFlow:                                     */
  float  capacity;    /* Maximum amount of flow allowed for this edge.        */
  float  flow;        /* Amount of flow actually going through this edge.     */
  
  /* References to the elements of the original graph.                        */
  Sedge       orgEdge;
  Attributes  attrs;
};
typedef  struct _FFEdgeInfo   FFEdgeInfo;
typedef  FFEdgeInfo          *FFEdgeInfoPtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define IS_LABLED(n)     (attr_data_of_type ((n), FFNodeInfoPtr)->IsLabled)
#define FROM_NODE(n)     (attr_data_of_type ((n), FFNodeInfoPtr)->FromNode)
#define FROM_ARC(n)      (attr_data_of_type ((n), FFNodeInfoPtr)->FromArc)
#define BACK_LABLED(n)   (attr_data_of_type ((n), FFNodeInfoPtr)->BackLabled)
#define EXTRA_FLOW(n)    (attr_data_of_type ((n), FFNodeInfoPtr)->ExtraFlow)
#define ORG_NODE(n)      (attr_data_of_type ((n), FFNodeInfoPtr)->orgNode)

#define CAPACITY(e)    (attr_data_of_type ((e), FFEdgeInfoPtr)->capacity)
#define FLOW(e)        (attr_data_of_type ((e), FFEdgeInfoPtr)->flow)
#define ORG_EDGE(e)    (attr_data_of_type ((e), FFEdgeInfoPtr)->orgEdge)

#define CREATE_SCANSET_ENTRY(n)    (make_attr(ATTR_DATA, (char *)(n)))

#define  MAKE_NODESET_ENTRY(n)    (make_attr(ATTR_DATA, (char *)(n)))
#define  MAKE_SEPARC_ENTRY(e)     (make_attr(ATTR_DATA, (char *)(e)))

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global float  FordFulkerson  (/*NetWork, Source, Target*/);
Global void   MinCut         ();
Global void  InitLabels      (/*NetWork*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global float  FordFulkerson  (Sgraph  NetWork, Snode  Source, Snode  Target);
Global void   MinCut         (Sgraph   NetWork, 
                              Snode    Source, 
                              Snode    Target, 
                              Slist   *NodeSetA, 
                              Slist   *NodeSetB, 
                              Slist   *SepArcs);
Global void   InitLabels     (Sgraph  NetWork);
#endif

#endif

/******************************************************************************/
/*  End of  MaxFlow.h                                                         */
/******************************************************************************/
