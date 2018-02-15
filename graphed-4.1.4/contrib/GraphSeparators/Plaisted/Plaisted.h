/******************************************************************************/
/*                                                                            */
/*    Plaisted.h                                                              */
/*                                                                            */
/******************************************************************************/
/*  Heuristic to compute edge separators proposed by Plaisted.                */
/*  Randomly distributes all nodes into two sets and converts the graph into  */
/*  a parametrized network flow problem. The parameter is the edge capacity,  */
/*  which is then minimized. The saturated edges at this capacity are the     */
/*  wanted separator edges.                                                   */
/*  The separator yields no balanced separator though.                        */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  15.04.1994                                                    */
/*  Modified :  28.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.1         Plaisted* added.                                  */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  PLAISTED
#define  PLAISTED

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <Separator.h>
#include "MaxFlow.h"

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

struct _PGraphInfo
{
  Sgraph  orgGraph;    /* This graph is the undirected copy of orgGraph.      */
  Snode   Source;      /* Source node, when used as network.                  */
  Snode   Target;      /* Target node, when used as network.                  */
  Snode   Dummy;       /* Added node if orgGraph has an odd number of nodes.  */
  int     numNodes;    /* Number of nodes in this graph.                      */
  float   capacity;    /* Capacity of the edges in the graph.                 */
};
typedef  struct _PGraphInfo   PGraphInfo;
typedef  PGraphInfo          *PGraphInfoPtr;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  ORG_GRAPH(g)     (attr_data_of_type ((g), PGraphInfoPtr)->orgGraph)
#define  SOURCE(g)        (attr_data_of_type ((g), PGraphInfoPtr)->Source)
#define  TARGET(g)        (attr_data_of_type ((g), PGraphInfoPtr)->Target)
#define  NUM_NODES(g)     (attr_data_of_type ((g), PGraphInfoPtr)->numNodes)
#define  DUMMY_NODE(g)    (attr_data_of_type ((g), PGraphInfoPtr)->Dummy)
#define  MAX_CAPACITY(g)  (attr_data_of_type ((g), PGraphInfoPtr)->capacity)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef  ANSI_HEADERS_OFF
Global Sgraph  InitPlaistedGraph    (/*currGraph*/);
Global void    RemovePlaistedGraph  (/*currGraph*/);
Global void    PlaistedA            (/*...*/);
Global void    PlaistedAmax         (/*...*/);
Global void    PlaistedAStar        (/*...*/);
Global Sedge   CopyOrgEdge          (/*orgE., sourceN., targetN., artificial*/);
Global void    AddDummyNode         (/*currGraph*/);
#endif

#ifdef  ANSI_HEADERS_ON
Global Sgraph  InitPlaistedGraph    (Sgraph  currGraph);
Global void    RemovePlaistedGraph  (Sgraph  currGraph);
Global void    PlaistedA            (Sgraph   currGraph, 
                                     Slist   *NodeSet_A, 
                                     Slist   *NodeSet_B, 
                                     Slist   *SepSet);
Global void    PlaistedAmax         (Sgraph   currGraph, 
                                     int      numTrials, 
                                     Slist   *NodeSet_A, 
                                     Slist   *NodeSet_B, 
                                     Slist   *SepSet);
Global void    PlaistedAStar        (Sgraph   currGraph, 
                                     Slist   *NodeSet_A, 
                                     Slist   *NodeSet_B, 
                                     Slist   *SepSet);
Global Sedge   CopyOrgEdge          (Sedge  orgEdge, 
                                     Snode  sourceNode, 
                                     Snode  targetNode, 
                                     bool   artificial);
Global void    AddDummyNode         (Sgraph  currGraph);
#endif

#endif

/******************************************************************************/
/*  End of  Plaisted.h                                                        */
/******************************************************************************/
