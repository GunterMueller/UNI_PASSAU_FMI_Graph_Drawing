#ifdef UNIX5
#ifndef lint
static char version_id[] = "%Z% File : %M% %E%  %U%  Version %I% Copyright (C) 1992/93 Schweikardt Andreas";
#endif
#endif 

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
	User Interface (graphic):	
	Other			:	GraphEd & Sgraph


********************************************************************************


Layer   : 	

Modul   :	


********************************************************************************


Description of %M% :




********************************************************************************


Functions of %M% :



*******************************************************************************/


/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph_interface.h>

/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/control.h>
#include <search/move.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	MVmarkEdgeClear

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	void

description :	marks an edge as decontaminated in the internal structure

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static void	MVmarkEdgeClear(Sedge edge)
{
	EdgeState	estate = (EdgeState)attr_data( edge );
	NodeState	nstate;

/*************************/
/* edge is decontaminted */
/*************************/
	estate->state = EDGE_CLEAR;

/***********************************************************************/
/* both target and source node have now an lower degree of edges still */
/* contaminated incident to them				       */
/***********************************************************************/
	nstate = (NodeState)attr_data( edge->tnode );
	nstate->not_clear--;

/***************************************************************/
/* if edge is a self-loop, the loops of the node decreases too */
/***************************************************************/
	if ( estate->self_loop )
	{
		nstate->self_loops--;
	}

	nstate = (NodeState)attr_data( edge->snode );

/****************************************************************************/
/* if 'snode' and 'tnode' are the same (selfloop) do not decrement it twice */
/****************************************************************************/
	if ( !(estate->self_loop) )
	{
		nstate->not_clear--;
	}

/************************************/
/* record the step only if animated */
/************************************/
	if ( COanimation() )
		MVrecordAction( ACTION_CLEAR, (char *)edge, NULL );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	MVmarkEdgeClearingPlusOne

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		to move over-I
2.	Snode		node		source node-I

return()    :	void

description :	moves a searcher over 'edge' beginning at 'node', the edge
		isn't marked as decontaminated. this procedures marks
		that an extra searcher is moved over the edge

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
static void	MVmarkEdgeClearingPlusOne(Sedge edge, Snode node)
{
/************************************/
/* record the step only if animated */
/************************************/
	if ( COanimation() )
		MVrecordAction( ACTION_CLEARING_PLUS_ONE, (char *)edge, (char *)node );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		to move over-I
2.	Snode		node		source node-I

return()    :	void

description :	moves a searcher over 'edge' beginning at 'node', the edge
		isn't marked as decontaminated. No extra searcher is required

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
static void	MVmarkEdgeClearing(Sedge edge, Snode node)
{
/************************************/
/* record the step only if animated */
/************************************/
	if ( COanimation() )
		MVrecordAction( ACTION_CLEARING, (char *)edge, (char *)node );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	MVmarkNodeSet

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	set a searcher on a node

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static void	MVmarkNodeSet(Snode node)
{
	NodeState	nstate = (NodeState)attr_data( node );

	nstate->state = NODE_SET;

/***************************************************************************/
/* one searcher more, but increment it only internal, do not display it in */
/* the report window (on the screen) this will happen during animation     */
/***************************************************************************/
	COincHiddenSearchers();

/************************************/
/* record the step only if animated */
/************************************/
	if ( COanimation() )
		MVrecordAction( ACTION_NODE_SET, (char *)node, NULL );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	MVmarkNodeUnset

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	searcher isn't necessary anymore

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static void	MVmarkNodeUnset(Snode node)
{
	NodeState	nstate = (NodeState)attr_data( node );

	nstate->state = NODE_UNSET;

/***************************************************************************/
/* one searcher more, but decrement it only internal, do not display it in */
/* the report window (on the screen) this will happen during animation     */
/***************************************************************************/
	COdecHiddenSearchers();


/************************************/
/* record the step only if animated */
/************************************/
	if ( COanimation() )
		MVrecordAction( ACTION_NODE_UNSET, (char *)node, NULL );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	MVclearAllEdgesBetween

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node1		-I
2.	Snode		node2		-I

return()    :	void

description :	decontaminates all multiple edges between two nodes containing
		a searcher

use	    :	only for mixed and node saerch methods

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static void	MVclearAllEdgesBetween(Snode node1, Snode node2)
{
	Sedge	edge;
	Snode	node;

/*****************************************************************/
/* cannot clear the edges if one node doesn't contain a searcher */
/*****************************************************************/
	if ( StateNode( node1 ) != NODE_SET && StateNode( node2 ) != NODE_SET )
		return;

/**********************************************************************/
/* get the smaller degree node for a faster traversing the sourcelist */
/**********************************************************************/
	node = ( Degree( node1 ) < Degree( node2 ) ? node1 : node2 );

        for_sourcelist( node, edge )
        {
	/******************************************************************/
	/* decontaminate the edge only if it is incident to both node and */
	/* still contaminted 						  */
	/******************************************************************/
                if (    StateEdge( edge ) == EDGE_NOT_CLEAR
                     && EdgeIsBetween( edge, node1, node2 ) )
                        MVmarkEdgeClear( edge );
        } end_for_sourcelist( node, edge );
 
	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	MVtestAllEdgesClear

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	checks if there exists still a contaminated edge, if there
		is none the searcher on the 'node' isn't uesful anymore

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static void	MVtestAllEdgesClear(Snode node)
{
	NodeState	nstate = (NodeState)attr_data( node );

/***************************************************************************/
/* if it doesn't contain a searcher, no edges can be decontaminated around */
/***************************************************************************/
	if ( nstate->state == NODE_SET && nstate->not_clear == 0 )
	{
		MVmarkNodeUnset( node );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	MVtestOnlyOneEdgeNotClear

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	checks a node if it has only one edge stil contaminated

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static bool	MVtestOnlyOneEdgeNotClear(Snode node)
{
        Sedge   edge;
        bool    one = FALSE;
 
	NodeState	nstate = (NodeState)attr_data( node );

/**************************************************************************/
/* if more than one adjacent is still contaminted then there cannot exist */
/* only one edge that is contamineted 					  */
/**************************************************************************/
	if ( nstate->not_clear == 1 )
	{
		for_sourcelist( node, edge )
		{
			if ( StateEdge( edge ) == EDGE_NOT_CLEAR )
			{
				if ( one )
					return( FALSE );
				else
                                one = TRUE;
			}
		} end_for_sourcelist( node, edge );
	} 
        return( one );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			 local function					       *
*									       *
********************************************************************************

name        :	MVtestOnlyOneAdjacentNodeNotSet

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	Snode

description :	gets the only adjacent node that never have contained a searcher
		if there is no such node 'empty_node' is returned

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static Snode	MVtestOnlyOneAdjacentNodeNotSet(Snode node)
{
	int	count = 0;	
	Snode	this_node,
		neighbour;

/*******************************************************/
/* check all adjacent nodes never contained a searcher */
/*******************************************************/
	for_all_adjacent_not_set_nodes( node, neighbour )
	{
		count++;
		this_node = neighbour;

	/**********************************/
	/* there are more than one node ? */
	/**********************************/
		if ( count == 2 )
		{
			return( empty_node );
		}
	} end_for_all_adjacent_not_set_nodes( node, neighbour );

/****************************************/
/* did we have found only one such node */
/****************************************/
	if ( count == 1 )
	{
		return( this_node );
	}
	else
	{
		return( empty_node );
	}
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	MVmoveTo

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		edge to move over-I

return()    :	void

description :	moves a searcher in the best way ! over the 'edge'

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static void	MVmoveTo(Sedge edge)
{

        Method  method = COmethod();
        Snode   neighbour,      /* used for adjacency checks */
		snode,
		tnode;
  
	snode = sedge_real_source( edge );
	tnode = sedge_real_target( edge );
/********************************/
/* Wrong use of this function ? */
/********************************/
	if ( method == METHOD_NODE_SEARCH )
        {
                SetErrorAndReturnVoid( ERROR_MOVETO_IN_NS_METHOD );
        }

/***********************************************/
/* moving starts always a node with a searcher */
/***********************************************/
        if ( StateNode( snode ) != NODE_SET && StateNode( tnode ) != NODE_SET )
        {
                SetErrorAndReturnVoid( ERROR_MOVETO_FROM_WRONG_SOURCENODE );
        }
 

/*******************************************************************/
/* check if 'snode' has only on uncleared edge, so we save a	   */
/* searcher if we move this searcher over the edge.		   */
/* if more than one not cleared incident edges exists, so the	   */
/* searcher on 'snode' must be doubled to have a new searcher that */
/* can move on 'edge'.						   */
/* Selfloops must be ignored, doubling is always required          */
/*******************************************************************/
        if (    MVtestOnlyOneEdgeNotClear( snode )
             && snode != tnode          /* ignore selfloops */
             && StateNode( snode ) == NODE_SET ) /* moving only from a set node */
        {
	/*******************/
        /* save a searcher */
	/*******************/
                MVmarkNodeUnset( snode );
		MVmarkEdgeClearing( edge, snode );
                MVmarkEdgeClear( edge );
        }

/**************************************************/
/* maybe we save searcher if we move from tnode ? */
/**************************************************/
        else if (    MVtestOnlyOneEdgeNotClear( tnode )
            && snode != tnode
            && StateNode( tnode ) == NODE_SET )
        {
                /* use 'neighbour' as a helpnode for swapping */
                neighbour = tnode;
                tnode = snode;
                snode = neighbour;
                /* save a searcher */
                MVmarkNodeUnset( snode );
		MVmarkEdgeClearing( edge, snode );
                MVmarkEdgeClear( edge );
        }

/***************************************************************/
/* we have to double a searcher and move one of it over 'edge' */
/***************************************************************/
        else
        {
	/**************************************************************/
	/* the source node must carry a searcher, if not then 'tnode' */
	/* carries a searcher (is checked above) 		     */
	/**************************************************************/
                if ( StateNode( snode ) != NODE_SET )
                {
               
		/**********************************************/
		/* use 'neighbour' as a helpnode for swapping */
		/**********************************************/
                        neighbour = tnode;
                        tnode = snode;
                        snode = neighbour;
                }
                COincHiddenSearchers();
		MVmarkEdgeClearingPlusOne( edge, snode );
                COdecHiddenSearchers();
                MVmarkEdgeClear( edge );
        }


        if ( StateNode( tnode ) == NODE_NOT_SET )
        {
	/***********************************************************/
	/* selfloops are implicit excluded,			   */
	/* 'snode' is set, 'tnode' is not set ==> 'tnode'!='snode' */
	/***********************************************************/
                MVmarkNodeSet( tnode );
 
                if ( method == METHOD_MIXED_SEARCH )
                {
		/*****************************************************/
		/* then clear all other edges between adjacent nodes */
		/*****************************************************/
                        for_all_adjacent_set_nodes( tnode, neighbour )
                        {
                                MVclearAllEdgesBetween( tnode, neighbour );
			/*********************************************/
			/* maybe the searcher on the adjacent is not */
			/* necessary any more 			     */
			/*********************************************/
                                MVtestAllEdgesClear( neighbour );
                        } end_for_all_adjacent_set_nodes( tnode, neighbour )
        
                }
        }
        else
        {
	/*********************************************************/
	/* 'tnode' carries already a searcher, the searcher that */
	/* moved on the edge can be deleted 			 */
	/* decrementation already done 				 */
	/*********************************************************/
        }


/********************************************/
/* maybe 'tnode's edges are all cleared now */
/********************************************/
        MVtestAllEdgesClear( tnode );

/************************************************************************/
/* 'snode' is not checked to carry an unnecessary searcher because	*/
/* (1) if only one incident not-cleared edge has exist the searcher	*/
/*     is already deleted (better: moved on the edge)			*/
/* (2) if mixed search method is choosen there exist two possibilities  */
/*     of deleting the searcher on 'snode':				*/
/*     a)  'tnode' carries no searcher:  all edges between adjacent set */
/*         nodes are cleared and the neigbours are tested for		*/
/*         unnecessary searchers especially 'snode' is a neighbour of	*/
/*         'tnode', so 'snode'is tested !				*/
/*     b) 'tnode' carries a searcher: all edges are already cleared	*/
/*        between both nodes, no uncleared edge exists ==> no move	*/
/*        is necessary ==> no changes of the states of nodes & edges	*/
/*     c) 'tnode'has carried a searcher: see b)				*/
/************************************************************************/

/***************************************************/
/* we have reached now a valid congiguration again */
/***************************************************/
	if ( COanimation() )
	{
		MVrecordMark();
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	CheckNode

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	checks if we can do some moves without incrementing the number
		of searchers

use	    :	

restrictions:	

bugs	    :	no nullpointer check, do not check if it has an internal struct

*******************************************************************************/
static void	CheckNode(Snode node)
{
	Snode	neighbour;
	Sedge	edge;
	Method	method = COmethod();


	if ( method == METHOD_EDGE_SEARCH )
	{
	/***********************************************************/
	/* clear all edges between two nodes containing a searcher */
	/***********************************************************/
		for_all_adjacent_set_nodes( node, neighbour )
		{
		/**********************************/
		/* do not forget multiple edges ! */
		/**********************************/
			while ( (edge = GetEdgeNotClearBetween( node, neighbour )) != empty_sedge )
			{
			/****************************/
			/* check this neighbour too */
			/****************************/
				MoveToCheck( node, neighbour );
			}
		} end_for_all_adjacent_set_nodes( node, neighbour )
	}


/****************************************/
/* can we move our searcher to next one */
/****************************************/
	if ( (neighbour = MVtestOnlyOneAdjacentNodeNotSet( node ) ) != empty_snode )
	{
		SetOnCheck( neighbour );
	}

	return;
}
/******************************************************************************/



/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SetOn

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	SetOn(Snode node)
{
	Snode	neighbour;

	Method 	method = COmethod();

        if ( node == empty_node )
	{
                SetErrorAndReturnVoid( ERROR_SETON_NULL_NODE );
	}
        if ( StateNode( node ) != NODE_NOT_SET )
	{
	/********************************************************************/
	/* do not set a searcher on a node already contains a searcher	    */
	/* and due to the recontamination is not possible is this program   */
	/* do not set a searcher on node that has been contained a searcher */
	/********************************************************************/
                return;
	}

/*********************************************************************/
/* the real placement of a searcher on a node, 'node' does not carry */
/* a searcher 							     */
/* if the method is mixed or node search, there might be a good move */
/* from node with a searcher and only one (the last) incident	     */
/* and not cleared edge, if it is so, then make a MoveTo from	     */
/* this node or vice versa 					     */
/*********************************************************************/
	if ( method != METHOD_NODE_SEARCH  )
        {
                for_all_adjacent_set_nodes( node, neighbour )
                {
		/*************************************************************/
		/* if the node has only one incident not cleared edge we can */
		/* save a searcher if we move the searcher on this edge      */
		/*************************************************************/
                        if ( MVtestOnlyOneEdgeNotClear( neighbour ) )
                        {
                                MoveTo( neighbour, node );
                                return; /* break *function*  */
                        }
                } end_for_all_adjacent_set_nodes( node, neighbour );
        }
	if ( StateNode( node ) == NODE_NOT_SET )
	{
	/**********************************************************************/
	/* mark the node as set, i.e. carrying a searcher, we know that 'node'*/
	/* does not carry a searcher 					      */
	/**********************************************************************/
		MVmarkNodeSet( node );
	}

/********************************************************/
/* mixed and node search are not different (SetOn only) */
/********************************************************/
        if ( method != METHOD_EDGE_SEARCH )
        {
	/*****************************************************************/
	/* mark all edges between all adjacent nodes carrying a searcher */
	/* as cleared 					 	         */
	/*****************************************************************/
                for_all_adjacent_set_nodes( node, neighbour )
                {
		/*****************************/
                /* mark the edges as cleared */
		/*****************************/
                        MVclearAllEdgesBetween( node, neighbour );

		/*********************************************************/	
		/* if all incident edges of 'neighbour' are cleared then */
		/* the searcher on this node isn't necessary any more    */
		/*********************************************************/	
			if ( node != neighbour )
			{
                        	MVtestAllEdgesClear( neighbour );
			}
                } end_for_all_adjacent_set_nodes( node, neighbour );
 
        }

/******************************************************/
/* Check if the searcher on 'node' is still necessary */
/******************************************************/
        MVtestAllEdgesClear( node );

/*******************************************/
/* we have now again a valid configuration */
/*******************************************/
	if ( COanimation() )
	{
		MVrecordMark();
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	Move

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	void

description :	move over an edge 

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	Move(Sedge edge)
{
	if ( edge != empty_edge )
	{
		MVmoveTo( edge );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MoveTo

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		snode		-I
2.	Snode		tnode		-I

return()    :	void

description :	move from one to the other (loops are ok) over an arbitrary
		but contaminated edge

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	MoveTo(Snode snode, Snode tnode)
{
	Sedge	edge;

/***********************************/
/* check if the moving is possible */
/***********************************/
	if ( snode == (Snode)NULL || tnode == (Snode)NULL )
	{
                SetErrorAndReturnVoid( ERROR_SETON_NULL_NODE );
	}
	if (    StateNode( snode ) == NODE_NOT_SET 
	     && StateNode( tnode ) == NODE_NOT_SET )
	{
                return; /* ignore this */

	}	

/*****************************************/
/* get an incident and contaminated edge */
/*****************************************/
	edge = GetEdgeNotClearBetween( snode, tnode );

	if ( edge != empty_edge )
	{
		MVmoveTo( edge );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MoveToCheck

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		snode		-I
2.	Snode		tnode		-I

return()    :	void

description :	same as MoveToCheck, but with a local optimization of the
		moving

use	    :	good for heuristics

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	MoveToCheck(Snode snode, Snode tnode)
{
	Snode	neighbour;
	Sedge	edge;
		
/***********************************/
/* check if the moving is possible */
/***********************************/
	if ( snode == (Snode)NULL || tnode == (Snode)NULL )
	{
                SetErrorAndReturnVoid( ERROR_SETON_NULL_NODE );
	}
	if (    StateNode( snode ) == NODE_NOT_SET 
	     && StateNode( tnode ) == NODE_NOT_SET )
	{
                return; /* ignore this move */
	}

/*****************************************/
/* get an incident and contaminated edge */
/*****************************************/
	edge = GetEdgeNotClearBetween( snode, tnode );

	if ( edge == (Sedge)NULL )
	{
		return; /* not  adjacent or no contaminated edge */
	}

/*****************/
/* do the moving */
/*****************/
	MVmoveTo( edge );

/*****************************************************/
/* can we do a good move without an extra searcher ? */
/*****************************************************/
	CheckNode( tnode );
	for_all_adjacent_set_nodes( tnode, neighbour )
	{
		CheckNode( neighbour );
	} end_for_all_adjacent_set_nodes( tnode, neighbour )

/******************************************************************************/
/* can we do a good move without an extra searcher ? (this time with 'snode') */
/******************************************************************************/
	/****************************************************************
	CheckNode( snode ); already done 'snode' is adjacent to 'tnode'
	****************************************************************/

	for_all_adjacent_set_nodes( snode, neighbour )
	{
		CheckNode( neighbour );
	} end_for_all_adjacent_set_nodes( snode, neighbour )

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MoveCheck

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	void

description :	same as 'Move' but with a local optimization

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	MoveCheck(Sedge edge)
{
	Snode	neighbour;

	if ( edge == NULL )
	{
		return;
	}

	MVmoveTo( edge );

/*****************************************************/
/* can we do a good move without an extra searcher ? */
/*****************************************************/
	CheckNode( edge->tnode );
	for_all_adjacent_set_nodes( edge->tnode, neighbour )
	{
		CheckNode( neighbour );
	} end_for_all_adjacent_set_nodes( edge->tnode, neighbour )


	for_all_adjacent_set_nodes( edge->snode, neighbour )
	{
		CheckNode( neighbour );
	} end_for_all_adjacent_set_nodes( edge->snode, neighbour )

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SetOnCheck

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	same as 'SetOn' but with a local optimization

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	SetOnCheck(Snode node)
{
	Snode	neighbour;

        if ( node == (Snode)NULL )
	{
                SetErrorAndReturnVoid( ERROR_SETON_NULL_NODE );
        }
	if ( StateNode( node ) != NODE_NOT_SET )
	{
                return;
	}

	SetOn( node );

	CheckNode( node );
	for_all_adjacent_set_nodes( node, neighbour )
	{
		CheckNode( neighbour );
	} end_for_all_adjacent_set_nodes( node, neighbour )

	return;
}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
