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
*			standard includes				      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
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
#include <search/move.h>
#include <search/control.h>
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

name        :	SearchState

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	SearchState

description :	gets the current search state of the 'node'

use	    :	

restrictions:

bugs	    :	'node' is not checked to be a null pointer, and not checked
		if it is initialized with a SearchState value

*******************************************************************************/
static SearchState	SimpleStateNode(Snode node)
{
	return( (SearchState)attr_flags( node ) );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	SimpleStateEdge

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	SearchState

description :	gets the current search state of an edge, cleared or not
		cleared by a searcher

use	    :	

restrictions:	

bugs	    :	'edge' is not checked to be a null pointer, and it isn't
		checked for a SearchState value

*******************************************************************************/
static SearchState	SimpleStateEdge(Sedge edge)
{
	return( (SearchState)attr_flags( edge ) );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	SimpleTestAllEdgesClear

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	bool

description :	checks if all edges of 'node' ar cleared resp. decontaminated

use	    :	

restrictions:	

bugs	    :	'node' isn't checked to be a nullpointer

*******************************************************************************/
static bool	SimpleTestAllEdgesClear(Snode node)
{
	Sedge	edge;

/*****************************************************************/
/* traverse all incident edges and check if they are all cleared */
/*****************************************************************/
	for_sourcelist( node, edge )
	{
		if ( SimpleStateEdge( edge ) == EDGE_NOT_CLEAR )
		{
		/*********************************************************/
		/* one edge isn't cleared yet ==> all edges aren't clear */
		/*********************************************************/
			return( FALSE );
		}
	} end_for_sourcelist( node, edge );

/*********************************************************************/
/* no still contaminated edge found ==> all edges are decontaminated */
/*********************************************************************/
	return( TRUE );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	SimpleTestOnlyOneEdgeNotClear

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	bool

description :	checks if 'node' has only one edge that is still contaminated

use	    :	

restrictions:	

bugs	    :	'node' isn't checked to be a null pointer

*******************************************************************************/
static bool	SimpleTestOnlyOneEdgeNotClear(Snode node)
{
	Sedge	edge;
	bool	one = FALSE;

/****************************************************************************/
/* traverse all incident edges, and search for two edges still contaminated */ 
/****************************************************************************/
	for_sourcelist( node, edge )
	{
	/************************/
	/* still contaminated ? */
	/************************/
		if ( SimpleStateEdge( edge ) == EDGE_NOT_CLEAR )
		{
			if ( one )
			{
			/********************************************/
			/* found the second edge still contaminated */
			/********************************************/
				return( FALSE );
			}
			else
			{
			/***********************/
			/* found the first one */
			/***********************/
				one = TRUE;
			}
		}
	} end_for_sourcelist( node, edge );

/*************************************************************************/
/* if I have found one and only edge still contaminated ==> 'one' = TRUE */
/* else 'one' is FALSE							 */
/*************************************************************************/
	return( one );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	SimpleGetOneEdgeNotClearBetween

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node1		-I
2.	Snode		node2		-I

return()    :	Sedge

description :	get an arbitratry and still contaminated edge between bot nodes

use	    :	

restrictions:	

bugs	    :	'nodeX' isn't checked to be a null pointer, they aren't also
		checked to be adjacent (==> return null edge )

*******************************************************************************/
static Sedge	SimpleGetOneEdgeNotClearBetween(Snode node1, Snode node2)
{
	Sedge	edge;

/***************************************************************************/
/* traverse all incident edges of 'node1' and check these edge to be still */
/* contaminated and also incident to 'node2'				   */
/***************************************************************************/
	for_sourcelist( node1, edge )
	{
		if (    SimpleStateEdge( edge ) == EDGE_NOT_CLEAR 
		     && EdgeIsBetween( edge, node1, node2 ) )
			return( edge );
	} end_for_sourcelist( node1, edge );

	return( empty_edge );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	SimpleGetOneEdgeNotClear

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I
2.	
3.	
4.	

return()    :	Sedge

description :	get an arbitrary still contaminated edge, doen't matter which
		other node is incident to this edge !

use	    :	

restrictions:	

bugs	    :	node isn't checked to be a null pointer

*******************************************************************************/
static Sedge	SimpleGetOneEdgeNotClear(Snode node)
{
	Sedge	edge;


/****************************************************************************/
/* traverse all incident edges and return the first contaminated edge found */
/****************************************************************************/
	for_sourcelist( node, edge )
	{
		if ( SimpleStateEdge( edge ) == EDGE_NOT_CLEAR )
			return( edge );
	} end_for_sourcelist( node, edge );

	return( empty_edge );
}
/******************************************************************************/


 
/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	SimpleClearAllEdgesBetween

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node1		-I
2.	Snode		node2		-I

return()    :	void

description :	decontaminates all edges (one or more ie. multiple edges)
		especially for the node and the mixed search methods

use	    :	

restrictions:	

bugs	    :	'nodeX' aren't checked if they are adjacent, they aren't
		checked for null pointer

*******************************************************************************/
static void	SimpleClearAllEdgesBetween(Snode node1, Snode node2)
{
	Sedge	edge;

/**************************************************************************/
/* traverse all incident edges of 'node1', check for adjacency to 'node2' */
/**************************************************************************/
	for_sourcelist( node1, edge )
	{
		if (    SimpleStateEdge( edge ) == EDGE_NOT_CLEAR 
		     && EdgeIsBetween( edge, node1, node2 ) )
		{
			SimpleMarkEdgeClear( edge );
		}
	} end_for_sourcelist( node1, edge );

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

name        :	SimpleMarkEdgeClear

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	void

description :	this procedures marks the edge 'edge' as cleared resp.
		decontaminated and does the animation

use	    :	while a manual move or a recording step

restrictions:	

bugs	    :	edge isn't checked to be a null pointer

*******************************************************************************/
void	SimpleMarkEdgeClear(Sedge edge)
{
/***********************************/
/* mark the edge as decontaminated */
/***********************************/
	set_edgeattrs( edge, make_attr( ATTR_FLAGS, EDGE_CLEAR ) );

/*************************************************/
/* show the edge as decontaminated on the screen */
/*************************************************/
	AnimateEdgeClear( edge );

/*************************************************************/
/* change the information according to the new configuration */
/*************************************************************/
	COincEdgesClear();
	COdecEdgesNotClear();
	COincSteps();

/************************************************************************/
/* if it is not a recording step ==> done by a manual action, record it */
/************************************************************************/
	if ( !COplay() )
	{
        	MVrecordAction( ACTION_CLEAR, (char *)edge, NULL );
	}

	return;
}
/******************************************************************************/


/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SimpleMarkEdgeClearingPlusOne

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		move along-I	
2.	Snode		node		sourcenode-I

return()    :	void

description :	move an extra searcher along 'edge', starting at 'node',
		(explicit: an extra searcher !)

use	    :	

restrictions:	

bugs	    :	'edge' and 'node' aren't checked to be incident, 
		no null pointer checking

*******************************************************************************/
void	SimpleMarkEdgeClearingPlusOne(Sedge edge, Snode node)
{
/***********************************/
/* mark the edge as decontaminated */
/***********************************/
	set_edgeattrs( edge, make_attr( ATTR_FLAGS, EDGE_CLEAR ) );

/************************/
/* the extra searcher ! */
/************************/
	COincSearchers();
	COincHiddenSearchers();

/*************************************************************/
/* change the information according to the new configuration */
/*************************************************************/
	COincSteps();
	COincEdgesClear();
	COdecEdgesNotClear();

/******************************************************/
/* show that is edge is decontaminated at this moment */
/******************************************************/
	AnimateEdgeClearing( edge, node );

/************************************************************************/
/* if it is not a recording step ==> done by a manual action, record it */
/************************************************************************/
	if ( !COplay() )
	{
		MVrecordAction( ACTION_CLEARING, (char *)edge, (char *)node );
	}

/***********************************/
/* remove the extra searcher again */
/***********************************/
	COdecSearchers();
	COdecHiddenSearchers();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SimpleMarkEdgeClearing

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		move along-I	
2.	Snode		node		sourcenode-I

return()    :	void

description :	move a searcher from 'node' along 'edge'

use	    :	

restrictions:	

bugs	    :	'edge' and 'node' aren't checked to be incident, 
		no null pointer checking

*******************************************************************************/
void	SimpleMarkEdgeClearing(Sedge edge, Snode node)
{
/***********************************/
/* mark the edge as decontaminated */
/***********************************/
	set_edgeattrs( edge, make_attr( ATTR_FLAGS, EDGE_CLEAR ) );

/*************************************************************/
/* change the information according to the new configuration */
/*************************************************************/
	COincSteps();
	COincEdgesClear();
	COdecEdgesNotClear();

/******************************************************/
/* show that is edge is decontaminated at this moment */
/******************************************************/
	AnimateEdgeClearing( edge, node );

/************************************************************************/
/* if it is not a recording step ==> done by a manual action, record it */
/************************************************************************/
	if ( !COplay() )
	{
		MVrecordAction( ACTION_CLEARING, (char *)edge, (char *)node );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SimpleMarkEdgeNotClear

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	void

description :	opposite function to SimpleMarkEdgeClear

use	    :	only while rewinding

restrictions:	

bugs	    :	no null pointer checking

*******************************************************************************/
void	SimpleMarkEdgeNotClear(Sedge edge)
{

/***********************************/
/* mark edge again as contaminated */
/***********************************/
	set_edgeattrs( edge, make_attr( ATTR_FLAGS, EDGE_NOT_CLEAR ) );

/*****************************/
/* and show it on the screen */
/*****************************/
	AnimateEdgeNotClear( edge );

/*************************************************************/
/* change the information according to the new configuration */
/*************************************************************/
	COincEdgesNotClear();
	COdecEdgesClear();
	COdecSteps();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SimpleMarkNodeNotSet

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	removes a searcher from a node

use	    :	only while rewinding 

restrictions:	

bugs	    :	no null pointer checking

*******************************************************************************/
void	SimpleMarkNodeNotSet(Snode node)
{
/***************************************/
/* mark the node again as contaminated */
/***************************************/
	set_nodeattrs( node, make_attr( ATTR_FLAGS, NODE_NOT_SET ) );

/***************/
/* and show it */
/***************/
	AnimateNodeNotSet( node );

/*************************************************************/
/* change the information according to the new configuration */
/*************************************************************/
	COincNodesNotSet();
	COdecNodesSet();
	COdecSteps();
	COdecSearchers();
	COdecHiddenSearchers();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SimpleMarkNodeSet

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	sets a searcher on a node

use	    :	both, forward and rewind

restrictions:	

bugs	    :	no null pointer check

*******************************************************************************/
void	SimpleMarkNodeSet(Snode node)
{
/*******************************************/
/* the node 'node' contains now a searcher */
/*******************************************/
	set_nodeattrs( node, make_attr( ATTR_FLAGS, NODE_SET ) );

/****************************/
/* set a pebble on the node */
/****************************/
	AnimateNodeSet( node );

/*************************************************************/
/* change the information according to the new configuration */
/*************************************************************/
	COincNodesSet();
	if ( CObackstep() )
	{
	/***************************************************/
	/* we proceed a step form 'NodeUnset' to 'NodeSet' */
	/***************************************************/
		COdecSteps();
		COdecNodesUnset();
	}
	else
	{
	/****************************************************/
	/* we proceed a step form 'NodeNotSet' to 'NodeSet' */
	/****************************************************/
		COincSteps();
		COdecNodesNotSet();
	}	
	COincSearchers();
	COincHiddenSearchers();

/************************************************************************/
/* if it is not a recording step ==> done by a manual action, record it */
/************************************************************************/
	if ( !COplay() )
	{	
		MVrecordAction( ACTION_NODE_SET, (char *)node, NULL );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	SimpleMarkNodeUnset

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	the searcher on the 'node' isn't necessary anymore, so
		remove it

use	    :	

restrictions:	

bugs	    :	no null pointer check

*******************************************************************************/
void	SimpleMarkNodeUnset(Snode node)
{
/*************************************/
/* remove the searcher from the node */
/*************************************/
	set_nodeattrs( node, make_attr( ATTR_FLAGS, NODE_UNSET ) );

/*************************************************/
/* remove the pebble from the node on the screen */
/*************************************************/
	AnimateNodeUnset( node );

/*************************************************************/
/* change the information according to the new configuration */
/*************************************************************/
	COincNodesUnset();
	COdecNodesSet();
	COincSteps();
	COdecSearchers();
	COdecHiddenSearchers();

/************************************************************************/
/* if it is not a recording step ==> done by a manual action, record it */
/************************************************************************/
	if ( !COplay() )
	{
		MVrecordAction( ACTION_NODE_UNSET, (char *)node, NULL );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	ManualSetOn

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	void

description :	a node was clicked at, now set a searcher on it, but
		checks for the best way to do this i.e. save a searcher
		if possible

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	ManualSetOn(Snode node)
{
	Snode	neighbour;
	Method	method = COmethod();

/**********************************************************************/
/* do nothing if node is an 'empty_node' or 'node' already contains a */
/* searcher resp. is decontaminated 				      */
/**********************************************************************/
	if ( node == empty_node )
		SetErrorAndReturnVoid( ERROR_SETON_NULL_NODE );
	if ( SimpleStateNode( node ) != NODE_NOT_SET )
		return;

/*********************************************************************/
/* if the method is mixed or node search, there might be a good move */
/* from node with a searcher and only one (the last) incident	     */
/* and not cleared edge, if it is so, then make a MoveTo from	     */
/* this node or vice versa 					     */
/*********************************************************************/
	if ( method != METHOD_NODE_SEARCH )
	{
	/*********************************************************************/
	/* Is there an adjacent node with a searcher on it and with a single */
	/* edge  still contaminated, if yes move along this edge 	     */
	/*********************************************************************/
		for_all_adjacent_nodes_cond( node, neighbour, SimpleStateNode, NODE_SET )
		{
			if ( SimpleTestOnlyOneEdgeNotClear( neighbour ) )
			{
			/******************************************/
			/* found a single edge still contaminated */
			/******************************************/
				ManualMove( SimpleGetOneEdgeNotClear( neighbour /* , node  */ ) );
			/***********************/
			/* we saved a searcher */
			/***********************/
				return;
			}
		} end_for_all_adjacent_nodes_cond( node, neighbour, SimpleStateNode, NODE_SET );

	/********************************************************************/
	/* if there is a node containing a searcher adjacent to 'node' then */
	/* move a searcher from this adjacent node along a decontaminated   */
	/* edge to our 'node' 						    */
	/********************************************************************/
		for_all_adjacent_nodes_cond( node, neighbour, SimpleStateNode, NODE_SET )
		{
		/******************************************/
		/* get an arbitrary edge (contaminated !) */
		/******************************************/
			ManualMove( SimpleGetOneEdgeNotClearBetween( neighbour, node ) );
			if ( method == METHOD_EDGE_SEARCH )
			{
			/***************************************************/
			/* all done, if method is edge search we cannot do */
			/* more 					   */
			/***************************************************/
				return;
			}
			else
			{
			/****************************************************/
			/* we have to check if there exists selfloops, 	    */
			/* muliple edges or other nodes with a searcher and */ 
			/* contaminted edges between them 		    */
			/****************************************************/
				break;
			}
		} end_for_all_adjacent_nodes_cond( node, neighbour, SimpleStateNode, NODE_SET );
	}

/*********************************************************/
/* if node doesn't contain now a searcher, set one on it */
/*********************************************************/
	if ( SimpleStateNode( node ) == NODE_NOT_SET )
		SimpleMarkNodeSet( node );

/*****************************************************************************/
/* if the method is edge search the edges between two nodes cont. a searcher */
/* aren' t decontaminated, but both other methods do so			     */
/*****************************************************************************/
	if ( method != METHOD_EDGE_SEARCH )
	{
	/*******************************************************************/
	/* decontaminate all edges between two nodes containing a searcher */
	/*******************************************************************/
		for_all_adjacent_nodes_cond( node, neighbour, SimpleStateNode, NODE_SET )
		{
			SimpleClearAllEdgesBetween( node, neighbour );

		/***********************************************************/
		/* if all edges of the node 'neighbour' are decontaminated */
		/* the searcher on this node isn't neccessary any more,	   */
		/* we can remove it, and we do removing 		   */
		/* (but not 'node' we do this explicitly at end of proc.)  */
		/***********************************************************/
			if ( SimpleTestAllEdgesClear( neighbour )
			     && node != neighbour )
			{
				SimpleMarkNodeUnset( neighbour );
			}
		} end_for_all_adjacent_nodes_cond( node, neighbour, SimpleStateNode, NODE_SET );
	}

/********************************************************************/
/* is 'node' still neccessary ie. still contaminated edges around ? */
/********************************************************************/
	if ( SimpleTestAllEdgesClear( node ) && SimpleStateNode( node ) == NODE_SET )
	{
		SimpleMarkNodeUnset( node );
	}

/***************************************************************************/
/* write to the record that this is the end of many (maybe a single) steps */
/* that at this moment the search configuration is in a valid state	   */
/***************************************************************************/
	MVrecordMark();

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	ManualMove

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	void

description :	edge was clicked and searcher is moved along this edge

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	ManualMove(Sedge edge)
{

/*************************************************************************/
/* we have to determine the 'real' GraphEd source and target of the edge */
/*************************************************************************/
	Snode	tnode = sedge_real_target( edge ),
		snode = sedge_real_source( edge );
	Snode	neighbour;

	Method	method = COmethod();

/*************************************************************************/
/* do nothing if 'edge' is empty or already decontaminated or if both    */
/* adjacent nodes are not containing a searcher (from where the searcher */
/* should move ?)							 */
/*************************************************************************/
	if ( edge == empty_edge )
		return;
	if ( SimpleStateEdge( edge ) == EDGE_CLEAR ) 
		return;
	if (    SimpleStateNode( snode ) != NODE_SET 
	     && SimpleStateNode( tnode ) != NODE_SET )
		return;
	
/***********************************************************************/
/* is it possible to use the searcher on 'snode' (saving a searcher ?) */
/***********************************************************************/
	if (    SimpleTestOnlyOneEdgeNotClear( snode ) 
	     && snode != tnode
	     && SimpleStateNode( snode ) == NODE_SET )
	{
	/****************************************************/
	/* Yes, we save a searcher, remove and then move it */
	/****************************************************/
		SimpleMarkNodeUnset( snode );
		SimpleMarkEdgeClearing( edge, snode );

		/**********************************************
		unneccessary: done by SimpleMarkeEdgeClearing
		SimpleMarkEdgeClear( edge );
		**********************************************/
		/**********************************************
		will be done later in proc.
		if ( SimpleStateNode( tnode ) == NODE_NOT_SET )
			SimpleMarkNodeSet( tnode );
		**********************************************/	
	}

/***********************************************************************/
/* is it possible to use the searcher on 'tnode' (saving a searcher ?) */
/*					  ^^^^^			       */
/***********************************************************************/
	else if (    SimpleTestOnlyOneEdgeNotClear( tnode ) 
	     	  && snode != tnode
	     	  && SimpleStateNode( tnode ) == NODE_SET )
	{
	/*************************************************/
	/* swap tnode and snode, for the latter checking */
	/*************************************************/
		neighbour = tnode;
		tnode = snode;
		snode = neighbour;
	
	/****************************************************/
	/* Yes, we save a searcher, remove and then move it */
	/****************************************************/
		SimpleMarkNodeUnset( snode );
		SimpleMarkEdgeClearing( edge, snode );

		/**********************************************
		unneccessary: done by SimpleMarkeEdgeClearing
		SimpleMarkEdgeClear( edge );
		**********************************************/
		/**********************************************
		will be done later in proc.
		if ( SimpleStateNode( tnode ) == NODE_NOT_SET )
			SimpleMarkNodeSet( tnode );
		**********************************************/	
	}

/****************************************************************************/
/* we cannot save a searcher, use an extra searcher to decontaminate 'edge' */
/****************************************************************************/
	else
	{
	/***************************************************/
	/* make 'snode' that node that contains a searcher */
	/***************************************************/
		if( SimpleStateNode( snode ) != NODE_SET )
		{
		/**********************************************/
                /* use 'neighbour' as a helpnode for swapping */
		/**********************************************/
                        neighbour = tnode;
                        tnode = snode;
                        snode = neighbour;

		}
	/****************************************************/
	/* use an extra searcher for decontaminating 'edge' */
	/****************************************************/
		SimpleMarkEdgeClearingPlusOne( edge, snode );

		/**********************************************
		unneccessary: done by SimpleMarkeEdgeClearing
		SimpleMarkEdgeClear( edge );
		**********************************************/
		/****************************************************
		will be done later in proc
		if ( SimpleStateNode( snode ) == NODE_NOT_SET )
			SimpleMarkNodeSet( snode );
		else if ( SimpleStateNode( tnode ) == NODE_NOT_SET )
			SimpleMarkNodeSet( tnode );
		****************************************************/
	}

/******************************************************************************/
/* we know that 'snode' contains a searcher, but does this 'tnode' make too ? */
/******************************************************************************/
	if ( SimpleStateNode( tnode ) == NODE_NOT_SET )
	{
	/******************************************/
	/* no it doesn't ==> set a searcher on it */
	/******************************************/
		SimpleMarkNodeSet( tnode );

	/*********************************************************************/
	/* mixed search (node search isn't possible here) decontaminates all */
	/* edges between two nodes containing a searcher, so we do here too  */
	/*********************************************************************/
		if ( method == METHOD_MIXED_SEARCH )
		{
			for_all_adjacent_nodes_cond( tnode, neighbour, SimpleStateNode, NODE_SET )
			{
				SimpleClearAllEdgesBetween( tnode, neighbour );
				if ( SimpleTestAllEdgesClear( neighbour ) )
					SimpleMarkNodeUnset( neighbour );

			} end_for_all_adjacent_nodes_cond( tnode, neighbour, SimpleStateNode, NODE_SET );

		}
	}

/********************************************************/
/* and what about 'tnode', can we remove the searcher ? */
/********************************************************/
	if (    SimpleTestAllEdgesClear( tnode )  
	     && SimpleStateNode( tnode ) != NODE_UNSET )
	{
		SimpleMarkNodeUnset( tnode );
	}

/**************************************************************
unneccessary:
	if ( SimpleTestAllEdgesClear( snode )
	     && SimpleStateNode( snode ) != NODE_UNSET )
		SimpleMarkNodeUnset( snode );
**************************************************************/

/***************************************************************************/
/* write to the record that this is the end of many (maybe a single) steps */
/* that at this moment the search configuration is in a valid state	   */
/***************************************************************************/
	MVrecordMark();

	return;
}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
