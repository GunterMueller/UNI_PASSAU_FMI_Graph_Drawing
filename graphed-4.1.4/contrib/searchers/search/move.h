/* do not include this file twice */
#ifndef _INCLUDE_MOVE
#define _INCLUDE_MOVE

/*******************************************************************************
*									       *
*									       *
*			SEARCH STRATEGIES ON GRAPHS			       *
*									       *
*									       *
*	Copyright (C) 1992/93 Andreas Schweikardt			       *
********************************************************************************




	File	:	%M%

	Date	:	%E%	(%U%)

	Version	:	%I%

	Author	:	Schweikardt, Andreas



Portability

	Language		:	C
	Operating System	:	Sun-OS (UNIX)
	User Inteface (graphic)	:	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	

Modul   :	


********************************************************************************


Description of %M% :



*******************************************************************************/


/******************************************************************************
*									      *
*			standard includes				      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			gui includes    	 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			GraphEd & Sgraph includes			      *
*									      *
*******************************************************************************/

#include <sgraph/std.h>


/******************************************************************************
*									      *
*			local includes		 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			defines 		 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			macros  		 			      *
*									      *
*******************************************************************************/


/* a macro ( similar to Sgraph macros ) to traverse all adjacent
   nodes of the node 'MAINNODE' 
   'NODE'and 'MAINNODE' are of Sgraph's type 'Snode'
*/
#define for_all_adjacent_nodes( MAINNODE, NODE )    \
{\
	Sedge	_edge;\
	for_sourcelist( MAINNODE, _edge )\
	{\
		NODE = (_edge->snode==(MAINNODE))?(_edge->tnode):(_edge->snode);\
		{

#define end_for_all_adjacent_nodes( MAINNODE, NODE )    \
		}\
	} end_for_sourcelist( MAINNODE, _edge )\
}


/* traverse all adjacent nodes of 'MAINNODE' which have a searcher
   placed on it
*/

#define for_all_adjacent_nodes_cond( MAINNODE, NODE, FUNC, EQU )\
{\
	Sedge	_edge;\
	for_sourcelist( MAINNODE, _edge )\
	{\
		NODE = (_edge->snode==(MAINNODE))?(_edge->tnode):(_edge->snode);\
		if ( FUNC( NODE ) == EQU )\
		{
	
#define end_for_all_adjacent_nodes_cond( MAINNODE, NODE, FUNC, EQU )\
		}\
	} end_for_sourcelist( MAINNODE, _edge )\
}

#define for_all_adjacent_set_nodes( MAINNODE, NODE )\
{\
	Sedge	_edge;\
	for_sourcelist( MAINNODE, _edge )\
	{\
		NODE = (_edge->snode==(MAINNODE))?(_edge->tnode):(_edge->snode);\
		if ( StateNode( NODE ) == NODE_SET )\
		{

#define end_for_all_adjacent_set_nodes( MAINNODE, NODE )\
		}\
	} end_for_sourcelist( MAINNODE, _edge )\
}
#define for_all_adjacent_unset_nodes( MAINNODE, NODE )\
{\
	Sedge	_edge;\
	for_sourcelist( MAINNODE, _edge )\
	{\
		NODE = (_edge->snode==(MAINNODE))?(_edge->tnode):(_edge->snode);\
		if ( StateNode( NODE ) == NODE_UNSET )\
		{

#define end_for_all_adjacent_unset_nodes( MAINNODE, NODE )\
		}\
	} end_for_sourcelist( MAINNODE, _edge )\
}

/* traverse all adjacent nodes of 'MAINNODE' which never have a 
   searcher placed on it
*/

#define for_all_adjacent_not_set_nodes( MAINNODE, NODE )    \
{\
	Sedge	_edge;\
	for_sourcelist( MAINNODE, _edge )\
	{\
		NODE = (_edge->snode==(MAINNODE))?(_edge->tnode):(_edge->snode);\
		if ( StateNode( NODE ) == NODE_NOT_SET )\
		{



#define end_for_all_adjacent_not_set_nodes( MAINNODE, NODE )    \
		}\
	} end_for_sourcelist( MAINNODE, _edge )\
}

/* traverse all nodes of the graph 'SGRAPH' which never have a searcher 
   placed on it */

#define for_all_not_set_nodes( SGRAPH, NODE )   \
{\
	for_all_nodes( SGRAPH, NODE )\
	{\
		if ( StateNode( NODE ) == NODE_NOT_SET )\
		{
#define end_for_all_not_set_nodes( SGRAPH, NODE )   \
		}\
	} end_for_all_nodes( SGRAPH, NODE );\
}

/* traverse all nodes of the graph 'SGRAPH' which have a searcher 
   placed on it */

#define for_all_set_nodes( SGRAPH, NODE )   \
{\
	for_all_nodes( SGRAPH, NODE )\
	{\
		if ( StateNode( NODE ) == NODE_SET )\
		{
#define end_for_all_set_nodes( SGRAPH, NODE )   \
		}\
	} end_for_all_nodes( SGRAPH, NODE );\
}
#define for_all_unset_nodes( SGRAPH, NODE )   \
{\
	for_all_nodes( SGRAPH, NODE )\
	{\
		if ( StateNode( NODE ) == NODE_UNSET )\
		{
#define end_for_all_unset_nodes( SGRAPH, NODE )   \
		}\
	} end_for_all_nodes( SGRAPH, NODE );\
}





#define EdgeIsBetween( EDGE, NODE1, NODE2 )\
	(    ( (EDGE)->tnode == (NODE1) && (EDGE)->snode == (NODE2) )\
	  || ( (EDGE)->tnode == (NODE2) && (EDGE)->snode == (NODE1) ) )\

#define set_nodestateattrs( NSTATE, ATTRS )\
	NSTATE->attrs = ATTRS
#define set_edgestateattrs( ESTATE, ATTRS )\
	ESTATE->attrs = ATTRS



/******************************************************************************
*									      *
*			structs			 			      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			types			 			      *
*									      *
*******************************************************************************/

typedef struct
{
	SearchState	state;
	unsigned int	degree;
	unsigned int	not_clear;
	unsigned int	self_loops;
	Attributes	attrs;
} StructNodeState, *NodeState;

typedef struct
{
	SearchState	state;
	unsigned int	multiple_edge;
	bool		self_loop;
	Attributes	attrs;
} StructEdgeState, *EdgeState;


typedef enum
{
	ACTION_NODE_SET,
	ACTION_NODE_UNSET,
	ACTION_CLEAR,
	ACTION_CLEARING,
	ACTION_CLEARING_PLUS_ONE,
	ACTION_END_OF_TAPE,
	ACTION_START_OF_TAPE
} Action;


/******************************************************************************
*									      *
*			global variables				      *
*									      *
*******************************************************************************/

/******************************************************************************
*									      *
*			functions					      *
*									      *
*******************************************************************************/


extern void	AnimateNodeNotSet(Snode node);
extern void	AnimateNodeSet(Snode node);
extern void	AnimateNodeUnset(Snode node);
extern void	AnimateEdgeClear(Sedge edge);
extern void	AnimateEdgeNotClear(Sedge edge);
extern void	AnimateEdgeClearing(Sedge edge, Snode source_nod);

extern void	ManualSetOn(Snode node);
extern void	ManualMove(Sedge edge);
extern void	SetOn(Snode node);
extern void	Move(Sedge edge);
extern void	MoveTo(Snode snode, Snode tnode);
extern void	SetOnCheck(Snode node);
extern void	MoveCheck(Sedge edge);
extern void	MoveToCheck(Snode snode, Snode tnode);

extern void      MVrecordMark(void);

extern SearchState	StateNode(Snode node);
extern SearchState	StateEdge(Sedge edge);

extern unsigned int	Degree(Snode node);
extern unsigned int	DegreeNode(Snode node);
extern unsigned int	DegreeNodeNotSet(Snode node);
extern unsigned int	DegreeNodeSet(Snode node);
extern unsigned int	DegreeNodeUnset(Snode node);
extern unsigned int	DegreeEdge(Snode node);
extern unsigned int	DegreeEdgeNotClear(Snode node);
extern Sedge		GetEdgeNotClearBetween(Snode node1, Snode node2);
extern Sedge		GetEdgeBetween(Snode node1, Snode node2);


extern void		MVrecordAction(Action action, char *data, char *data2);
extern void		MVrecordStep(void);
extern bool		MVrecordToNextStep(void);
extern void		MVrecordBackstep(void);
extern bool		MVrecordToNextBackstep(void);
extern void		MVrecordReset(void);
extern void		MVrecordClear(void);
extern bool		MVrecordEndOfTape(void);
extern bool		MVrecordStartOfTape(void);

extern void		SimpleMarkNodeSet(Snode node);
extern void		SimpleMarkNodeNotSet(Snode node);
extern void		SimpleMarkNodeUnset(Snode node);
extern void		SimpleMarkEdgeClear(Sedge edge);
extern void		SimpleMarkEdgeNotClear(Sedge edge);
extern void		SimpleMarkEdgeClearing(Sedge edge, Snode node);


extern void		EdgeFree(Sedge edge);
extern void		NodeFree(Snode node);


extern Sgraph           SimplifyGraph(Sgraph graph, Method method); 
extern Snode            MVrecordGetNextSetNode(void); 
extern void		MVrecordResetTape(void);

extern void		ComputeSimplifiedSearch(Sgraph graph);
extern void		SimpleMarkEdgeClearingPlusOne(Sedge edge, Snode node);


#endif /* do not include file twice */
/******************************************************************************
*		       [EOF] end of header-file %M% 
******************************************************************************/
