/******************************************************************************/
/*                                                                            */
/*    Separator.h                                                             */
/*                                                                            */
/******************************************************************************/
/*  Header-file to support the separator-algorithms.                          */
/*  The generally needed headers are included, attribute data-structures and  */
/*  makros and functions to access them are defined.                          */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  16.02.1994                                                    */
/*  Modified :  26.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              2.3         Select flags for IncreaseDegree.                  */
/*              2.2         D-values for greedy moved in.                     */
/*              2.1         Support for ANSI Prototypes.                      */
/*              2.0         Restructuring and addition of AlgorithmInfo.      */
/*              1.2         Attributes for contracted nodes and edges added.  */
/*              1.1         The original attributes of the graph are saved.   */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef SEPARATOR_GLOBAL
#define SEPARATOR_GLOBAL
 
/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include <math.h>
#include <stdlib.h>
#include <time.h>

#ifndef SGRAPH_INCLUDED
#define SGRAPH_INCLUDED
#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#endif

#ifdef  DEBUG_ON
#include <GS-Debug.h>
#endif

/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

enum  _NodeSet    /* Where is the node ?                                      */
{
  NodeNone,       /* The node currently belongs nowhere                       */
  LeftSet,        /* The node is part of the "left" subset of the nodes       */
  RightSet,       /* The node is part of the "right" subset of the nodes      */
  SeparatorNode   /* Node-separators only: The node is part of the separator  */
};
typedef  enum _NodeSet  NodeSet;

enum  _EdgeSet    /* Where is the edge ?                                      */
{
  EdgeNone,       /* The edge currently belongs nowhere                       */
  A_Set,          /* This an edge between two nodes of the left node set      */
  B_Set,          /* This an edge between two nodes of the right node set     */
  Separator_Set   /* The edge is part of the separator (edge separators) or   */
};                /* is adjacent to a node in the separator (node separators) */
typedef  enum _EdgeSet  EdgeSet;

struct  _ContInfo          /* Data of two contracted nodes.                   */
{
  char *  firstAttr;       /* Attributes of the first of the two nodes.       */
  char *  edgeAttr;        /* Attributes of the edge in between.              */
  char *  secondAttr;      /* Attributes of the second of the two nodes.      */
  int     firstX;          /* X location of the first node.                   */
  int     firstY;          /* Y location of the first node.                   */
  int     secondX;         /* X location of the second node.                  */
  int     secondY;         /* Y location of the second node.                  */
};
typedef  struct _ContInfo   ContInfo;
typedef  ContInfo          *ContInfoPtr;

struct  _EdgeInfo             /* Attribute structure for Sedges               */
{
  Attributes  attrs;          /* Original value of attrs field of the edge    */
  float       Weight;         /* Weight of the edge                           */
  EdgeSet     PartOfSet;      /* Where is the edge in ?   (Finally)           */
  EdgeSet     ValidSet;       /* Where is the edge in ? (Last successful try) */
  bool        Visited;        /* Marker for the search algorithms             */
  int         SourceNumber;   /* Org. end node of a recently contracted pair. */
  int         TargetNumber;   /* Org. end node of a recently contracted pair. */
  Slist       MultiList;      /* Attributes of a Multi-Edge.                  */
};
typedef  struct _EdgeInfo   EdgeInfo;
typedef  EdgeInfo          *EdgeInfoPtr;

struct  _NodeInfo           /* Attribute structure for Snodes                 */
{
  Attributes   attrs;       /* Original value of the attrs field of the node. */
  Snode        refNode;     /* Reference pointer (used to copy graphs).       */
  NodeSet      PartOfSet;   /* Where is the node in ?  (Finally)              */
  NodeSet      ValidSet;    /* Where is the node in ?  (Last successful try)  */
  bool         Visited;     /* Marker for the search algorithms.              */
  float        DValue;      /* == External cost - Internal cost.              */
  bool         Dummy;       /* Flag (used to identify dummies in K-L).        */
  Slist        SetEntry;    /* Pointer to corresponding node set entry (K-L). */
  bool         Contracted;  /* This node is actually two nodes. (Incr.Degree) */
  ContInfoPtr  cInfo;       /* Combined data on two contracted nodes.         */
  int          flags;       /* Used by the MaxFlow-MaxMatching Algorithm.      */
};
typedef  struct _NodeInfo   NodeInfo;
typedef  NodeInfo          *NodeInfoPtr;

struct  _GraphInfo         /* Attribute structure for Sgraphs                 */
{
  Attributes  attrs;       /* Original value of the attrs field of the graph. */
  int         numOfNodes;  /* Number of nodes in the graph.                   */
};
typedef  struct _GraphInfo  GraphInfo;
typedef  GraphInfo         *GraphInfoPtr;

struct  _GSAlgInfo
{
  Sgraph  Graph;          /* The Graph to compute the separator of.           */
  Slist   NodeSet_A;      /* One node set of the vertice partition.           */
  Slist   NodeSet_B;      /* The second node set of the vertice partition.    */
  Slist   Separator;      /* The set of edges/nodes separating the graph.     */
  float   Alpha;          /* 0.5 =< alpha < 1. The alpha condition...         */

#ifdef  ANIMATION_ON
  bool  animate;          /* Update GraphEd without giving control to luser.  */
#endif
};
typedef  struct _GSAlgInfo   GSAlgInfo;
typedef  GSAlgInfo          *GSAlgInfoPtr;

enum  _GSAlgSelect        /* Enumeration of the various separator algorithms. */
{
  BRUTE_FORCE,            /* The Brute-Force algorithm.                       */
  NAIVE,                  /* The Naive algorithm.                             */
  GREEDY,                 /* The Greedy algorithm.                            */
  KERNIGHAN_LIN,          /* The Kernighan-Lin heuristic.                     */
  KL_EXCHANGE,            /* Extension to Kernighan-Lin.                      */
  FIDUCCIA_MATTHEYSES,    /* The Fiduccia-Mattheyses heuristic.               */
  PLAISTED_A,             /* The Plaisted algorithm.                          */
  PLAISTED_STAR           /* The Plaisted algorithm.                          */
};
typedef  enum _GSAlgSelect  GSAlgSelect;

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#define  CREATE_NODESET_ENTRY(n)    (make_attr(ATTR_DATA, (char *)(n)))
#define  CREATE_EDGESET_ENTRY(e)    (make_attr(ATTR_DATA, (char *)(e)))
#define  CREATE_SEPARATOR_ENTRY(e)  (make_attr(ATTR_DATA, (char *)(e)))
#define  EMPTY_ATTR                 (make_attr(ATTR_DATA, NULL))
#define  CREATE_MULTI_ENTRY(m)      (make_attr(ATTR_DATA, (char *)(m)))
#define  GET_MULTI_ENTRY(m)         (attr_data_of_type((m), EdgeInfoPtr))
#define  GET_NODESET_ENTRY(e)       (attr_data_of_type((e), Snode))
#define  GET_EDGELIST_ENTRY(e)      (attr_data_of_type((e), Sedge))
#define  GET_SEPARATOR_EDGE(e)      (attr_data_of_type((e), Sedge))
#define  GET_SEP_SNODE(e)           (attr_data_of_type((e), Sedge)->snode)
#define  GET_SEP_TNODE(e)           (attr_data_of_type((e), Sedge)->tnode)

#define  ORG_NODE_ATTR(n)     (attr_data_of_type((n),NodeInfoPtr)->attrs.value.data)
#define  IS_NODE_VISITED(n)   (attr_data_of_type((n),NodeInfoPtr)->Visited)
#define  PART_OF_NODE_SET(n)  (attr_data_of_type((n),NodeInfoPtr)->PartOfSet)
#define  VALID_NODE_SET(n)    (attr_data_of_type((n),NodeInfoPtr)->ValidSet)
#define  NODE_REF(n)          (attr_data_of_type((n),NodeInfoPtr)->refNode)
#define  D_VALUE(n)           (attr_data_of_type((n),NodeInfoPtr)->DValue)
#define  IS_DUMMY(n)          (attr_data_of_type((n),NodeInfoPtr)->Dummy)
#define  SET_ENTRY(n)         (attr_data_of_type((n),NodeInfoPtr)->SetEntry)
#define  IS_CONTRACTED(n)     (attr_data_of_type((n),NodeInfoPtr)->Contracted)
#define  FLAGS(n)             (attr_data_of_type((n),NodeInfoPtr)->flags)

#define  ORG_EDGE_ATTR(n)     (attr_data_of_type((n),EdgeInfoPtr)->attrs.value.data)
#define  IS_EDGE_VISITED(e)   (attr_data_of_type((e),EdgeInfoPtr)->Visited)
#define  PART_OF_EDGE_SET(e)  (attr_data_of_type((e),EdgeInfoPtr)->PartOfSet)
#define  VALID_EDGE_SET(e)    (attr_data_of_type((e),EdgeInfoPtr)->ValidSet)
#define  EDGE_WEIGHT(e)       (attr_data_of_type((e),EdgeInfoPtr)->Weight)
#define  SOURCE_NUMBER(e)     (attr_data_of_type((e),EdgeInfoPtr)->SourceNumber)
#define  TARGET_NUMBER(e)     (attr_data_of_type((e),EdgeInfoPtr)->TargetNumber)
#define  MULTI_LIST(e)        (attr_data_of_type((e),EdgeInfoPtr)->MultiList)

#define  ORG_GRAPH_ATTR(n)    (attr_data_of_type((n),GraphInfoPtr)->attrs.value.data)
#define  NUM_GRAPH_NODES(g)   (attr_data_of_type((g),GraphInfoPtr)->numOfNodes)

#define  CONT_INFO(n)    (attr_data_of_type((n),NodeInfoPtr)->cInfo)
#define  FIRST_INFO(n)   (attr_data_of_type((n),NodeInfoPtr)->cInfo->firstAttr)
#define  EDGE_INFO(n)    (attr_data_of_type((n),NodeInfoPtr)->cInfo->edgeAttr)
#define  SECOND_INFO(n)  (attr_data_of_type((n),NodeInfoPtr)->cInfo->secondAttr)
#define  FIRST_X(n)      (attr_data_of_type((n),NodeInfoPtr)->cInfo->firstX)
#define  FIRST_Y(n)      (attr_data_of_type((n),NodeInfoPtr)->cInfo->firstY)
#define  SECOND_X(n)     (attr_data_of_type((n),NodeInfoPtr)->cInfo->secondX)
#define  SECOND_Y(n)     (attr_data_of_type((n),NodeInfoPtr)->cInfo->secondY)

#define  GRAPH(a)        ((a)->Graph)
#define  A_SET(a)        ((a)->NodeSet_A)
#define  B_SET(a)        ((a)->NodeSet_B)
#define  SEP_SET(a)      ((a)->Separator)
#define  ALPHA(a)        ((a)->Alpha)

#ifdef ANIMATION_ON
#define  ANIMATE(a)      ((a)->animate)
#endif

#define  NEXT_NODE(n,e)       (((e)->snode == (n)) ? (e)->tnode : (e)->snode)

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef ANSI_HEADERS_OFF
Global void   InitAttributes         (/*currGraph, validAttributes*/);
Global void   RemoveAttributes       (/*currGraph*/);
Global void   InitNodeMarkers        (/*currGraph*/);
Global void   InitEdgeMarkers        (/*currGraph*/);
Global void   InitialPartition       (/*...*/);
Global void   RandomInitialPartition (/*...*/);
Global void   ComputeNodeSetSizes    (/*graphSize, maxASize, maxBSize, alpha*/);
Global void   InsertDummys           (/*currGraph, noAddNodes*/);
Global void   InsertDummysInNodeSet  (/*algInfo, noAddNodes, inSet*/);
Global void   RemoveDummys           (/*currGraph*/);
Global void   RemoveDummysFromNodeSet(/*currSet*/);
Global void   InitialDValues         (/*currGraph*/);
Global void   ExchangeOnePair        (/*A_Entry,B_Entry,NodeSet_A,NodeSet_B*/);
Global void   ComputeMaxElem         (/*currNodeSet, D_max*/);
Global float  PairEdgeWeight         (/*ANode, BNode*/);
Global void   ComputeSeparatorEdges  (/*currGraph, Separator*/);
Global void   AnimateAlgorithm       (/*GSAlgInfoPtr  algInfo*/);
#endif

#ifdef ANSI_HEADERS_ON
Global void   InitAttributes           (Sgraph  currGraph, 
                                        bool    validAttributes);
Global void   RemoveAttributes         (Sgraph  currGraph);
Global void   InitNodeMarkers          (Sgraph  currGraph);
Global void   InitEdgeMarkers          (Sgraph  currGraph);
Global void   InitialPartition         (Sgraph  currGraph, 
                                        Slist  *NodeSet_A, 
                                        Slist  *NodeSet_B, 
                                        int     maxASet);
Global void   RandomInitialPartition   (Sgraph  currGraph, 
                                        Slist  *LeftNodeSet, 
                                        Slist  *RightNodeSet, 
                                        int     maxSize);
Global void   ComputeNodeSetSizes      (int    graphSize, 
                                        int   *maxASize, 
                                        int   *maxBSize, 
                                        float  alpha);
Global void   InsertDummys             (Sgraph  currGraph, int  noAddNodes);
Global void   InsertDummysInNodeSet    (GSAlgInfoPtr  algInfo, 
                                        int           noAddNodes, 
                                        NodeSet       inSet);
Global void   RemoveDummys             (Sgraph  currGraph);
Global void   RemoveDummysFromNodeSet  (Slist  *currSet);
Global void   InitialDValues           (Sgraph  currGraph);
Global void   ExchangeOnePair          (Slist   A_Entry, 
                                        Slist   B_Entry, 
                                        Slist  *NodeSet_A, 
                                        Slist  *NodeSet_B);
Global void   ComputeMaxElem           (Slist  currNodeSet, Slist  *D_max);
Global float  PairEdgeWeight           (Snode  ANode, Snode  BNode);
Global void   ComputeSeparatorEdges    (Sgraph  currGraph,   Slist  *Separator);
Global void   AnimateAlgorithm         (GSAlgInfoPtr  algInfo);
#endif

#endif

/******************************************************************************/
/*  End of  Separator.h                                                       */
/******************************************************************************/
