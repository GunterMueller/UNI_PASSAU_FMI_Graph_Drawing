
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

#include "mystd.h"


/******************************************************************************
*									      *
*			local includes		 			      *
*									      *
*******************************************************************************/

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


typedef struct {
	int	mark;
	int	separator_mark;
	int	temp_mark;
	Snode	isocopy,
		isoorg;
} SeparatorNode;



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


#endif /* do not include file twice */
/******************************************************************************
*		       [EOF] end of header-file %M% 
******************************************************************************/
