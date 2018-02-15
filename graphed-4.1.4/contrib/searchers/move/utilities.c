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
#include <sgraph/graphed.h>
#include <sgraph/algorithms.h>
 

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
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	EdgeFree

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		attributes of edge to free-I

return()    :	void

description :	frees the allocated attribute field of an edge

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    EdgeFree(Sedge edge)
{
        EdgeState       edgestate = (EdgeState)attr_data( edge );

        if ( edgestate == 0 )
        {
                SetErrorAndReturnVoid( IERROR_FREE_NULL_PTR );
        }
        free( edgestate );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	NodeFree

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		attributes of node to free-I

return()    :	void

description :	frees the allocated attribute field of a node 

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void    NodeFree(Snode node)
{
        NodeState       nodestate = (NodeState)attr_data( node );

        if ( nodestate == 0 )
        {
                SetErrorAndReturnVoid( IERROR_FREE_NULL_PTR );
        }
        free( nodestate );

	set_nodeattrs( node, make_attr( ATTR_DATA, NULL ) );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COtestSearchability

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph		graph		graph to test for search-I

return()    :	void

description :	test a graph for various conditions to do a search on it

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COtestSearchability(Sgraph graph)
{
/**************************/
/* Test on nonempty graph */
/**************************/
	if ( graph == empty_sgraph )
		 SetError( WARNING_EMPTY_GRAPH );
	else if ( graph->nodes == empty_node )
		SetError( WARNING_EMPTY_GRAPH );

/*****************************/
/* test on nondirected graph */ 
/*****************************/
	else if ( graph->directed ) 
		SetError( WARNING_DIGRAPH ); 

/**********************************************/
/* for easier searching only connected graphs */ 
/**********************************************/
	else if ( !(test_sgraph_connected( graph )) ) 
		SetError( WARNING_NOT_CONNECTED_GRAPH  );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COinitSearchStructure

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph		graph		init internal search structure-I
2.	bool		original	original graph ?-I

return()    :	void

description :	initializes an internal search structure, for a faster
		searching we compute some attributes of the nodes only
		once and do only make update after every move

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COinitSearchStructure(Sgraph graph, int original)
{
	Snode	node;
	Sedge	edge;

/************************/
/* count various things */
/************************/
	unsigned int	number_of_edges = 0;
	unsigned int	number_of_nodes = 0;
	unsigned int	number_of_self_loops = 0;
	unsigned int	degree;

	NodeState	nstate;
	EdgeState	estate;

/***********************/
/* ignore empty graphs */
/***********************/
	if ( graph == empty_sgraph )
		 return;
	else if ( graph->nodes == empty_node )
		 return;

/************************************************************************/
/* count the various items, append the structure to every node and edge */
/************************************************************************/
	for_all_nodes( graph, node )
	{
		degree = 0;
		number_of_self_loops = 0;
	/**********************************************************/
	/* intialize the edges, compute the degree for every node */
	/**********************************************************/
		for_sourcelist( node, edge )
		{
		/*******************************/
		/* we count all incident edges */
		/*******************************/
			degree++;

                        if ( unique_edge( edge ) )
			{
			/***********************************************/
			/* but intialize and count the edges only once */
			/***********************************************/
                        	number_of_edges++;

			/**************************************/
			/* count the self-loops of every node */
			/**************************************/
				if ( edge->snode == edge->tnode )
					number_of_self_loops++;
		
			/*********************************/
			/* create the new edge-structure */
			/*********************************/
				estate = (EdgeState)malloc( sizeof( StructNodeState) );
				if ( estate == (EdgeState)NULL )
				{
					SetErrorAndReturnVoid( IERROR_NO_MEM );
				}

				estate->state = EDGE_NOT_CLEAR;
				/* ->multiple edge not used */
				estate->attrs = make_attr( ATTR_FLAGS, 0 );
				estate->self_loop = ( edge->tnode == edge->snode );	

			/**********************/
			/* append to the edge */
			/**********************/
				set_edgeattrs( edge, make_attr( ATTR_DATA, estate ) );

			}
		} end_for_sourcelist( node, edge );

	/***************************************************/
	/* only the degree without selfloops is considered */
	/***************************************************/
		degree = degree - number_of_self_loops;

	/*********************************/
	/* create the new node-structure */
	/*********************************/
		nstate = (NodeState)malloc( sizeof( StructNodeState ) );
		if ( nstate == (NodeState)NULL )
		{
			SetErrorAndReturnVoid( IERROR_NO_MEM );
		}
		nstate->state = NODE_NOT_SET;
		nstate->degree = degree;         
		nstate->not_clear = degree;         
		nstate->self_loops = number_of_self_loops; 
		nstate->attrs = make_attr( ATTR_FLAGS, 0 );

	/*************************/
	/* append it to the node */
	/*************************/
		set_nodeattrs( node, make_attr( ATTR_DATA, nstate ) );

	/***********************************/
	/* give every node a unique number */
	/***********************************/
                node->nr = number_of_nodes;

	/******************/
	/* and count them */
	/******************/
                number_of_nodes++;
	} end_for_all_nodes( graph, node );

	if ( original )
	{
	/*********************************************************/
	/* set (and display) the new information about the graph */
	/*********************************************************/
		COsetNodes( number_of_nodes );
		COsetNodesNotSet( number_of_nodes );
		COsetEdgesNotClear( number_of_edges );
		COsetEdges( number_of_edges );
		COsetMaxSteps( 2*number_of_nodes+number_of_edges );
	}

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COfreeSearchStructure

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph		graph		-I

return()    :	void

description :	free all the attributes of the edges and nodes of the graph
		previously allocated by the procedure 'COinitSearchStructure'

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COfreeSearchStructure(Sgraph graph)
{
        Snode   node;
        Sedge   edge;
         

        for_all_nodes( graph, node )
        {
                for_sourcelist( node, edge )
                {
		/*****************************/
		/* free every edge only once */
		/*****************************/
			if ( unique_edge( edge ) )
			{
				EdgeFree( edge );
				IfErrorReturn;
			}
                } end_for_sourcelist( node, edge );

                NodeFree( node );
                IfErrorReturn;
         
        } end_for_all_nodes( graph, node );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	COinitGraphAttributes

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph		graph		-I

return()    :	void

description :	initializes all edges and nodes as contaminated, does also
		a normalization on the graph, i.e. all nodes and edges
		are have the same type (if animated)

use	    :	only for animation and manual search

restrictions:	

bugs	    :	none reported

*******************************************************************************/
void	COinitGraphAttributes(Sgraph graph)
{
	Snode	node;
	Sedge	edge;
	Slist	list = empty_slist;

	Graphed_edge	gedge;
	Graphed_group	group;

/**********************************************************************/
/* count the nodes and the edges of the graph, used for some internal */
/* purposes							      */
/**********************************************************************/
	unsigned int	number_of_edges = 0;
	unsigned int	number_of_nodes = 0;

/************************************/
/* cannot initialize an empty graph */
/************************************/
	if ( graph == empty_sgraph )
		 return;
	else if ( graph->nodes == empty_node )
		 return;

/*****************************/
/* intialize the whole graph */
/*****************************/
	for_all_nodes( graph, node )
	{
		set_nodeattrs( node, make_attr( ATTR_FLAGS, NODE_NOT_SET ) );
		number_of_nodes++;

	/************************/
	/* initialize all edges */
	/************************/
		for_sourcelist( node, edge )
		{
		/************************/
		/* every edge only once */
		/************************/
			if ( unique_edge( edge ) )
			{
			/***************************/
			/* mark it as contaminated */
			/***************************/
				set_edgeattrs( edge, make_attr( ATTR_FLAGS, EDGE_NOT_CLEAR ) );
		
			/***********************/
			/* make a nice drawing */
			/***********************/
				gedge = graphed_edge( edge );
				if ( edge_get( gedge, EDGE_TYPE ) != 0 )
				{
					edge_set( gedge,
						ONLY_SET,
						EDGE_TYPE,		0,
						EDGE_LABEL_VISIBILITY,	FALSE,
						NULL );
				}

			/***********************************/
			/* don't forget to count all edges */
			/***********************************/
				number_of_edges++;
			}
		} end_for_sourcelist( node, edge );

	/*******************************************************/
	/* insert node into list, a do the whole stuff at once */
	/*******************************************************/
		list = add_to_slist( list, make_attr( ATTR_DATA, node ) );

	} end_for_all_nodes( graph, node );

/*************************************************/
/* make all the settings at once (for the nodes) */
/*************************************************/
	group = create_graphed_group_from_slist( list );
	free_slist( list );
	group_set( group, 
		ONLY_SET,
		NODE_TYPE,		1,
		NODE_LABEL_VISIBILITY,	FALSE,
		NULL );
	free_group( group );

/*************************************/
/* and now show the normalized graph */
/*************************************/
	COrepaint();

/***********************************************************************/
/* set (and display) all the new information about the graph we gained */
/***********************************************************************/
	COsetNodes( number_of_nodes );
	COsetNodesNotSet( number_of_nodes );
	COsetEdgesNotClear( number_of_edges );
	COsetEdges( number_of_edges );
	COsetMaxSteps( 2*number_of_nodes+number_of_edges );

	return;
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	MakeCopyOfGraph

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph		graph		graph to copy-I

return()    :	Sgraph

description :	copies a graph

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/
Sgraph	MakeCopyOfGraph(Sgraph graph)
{
	Sgraph	ngraph;
	Snode	node,
		nnode;
	Sedge	edge;

/******************************/
/* do not copy an empty graph */
/******************************/
	if ( graph == empty_sgraph )
		 return( empty_sgraph );
	if ( graph->nodes == empty_node )
		 return( empty_sgraph );

/*********************************/
/* create a new undirected graph */
/*********************************/
	ngraph = make_graph( make_attr( ATTR_FLAGS, 0 ) );

	ngraph->directed = FALSE;

/***************************/
/* now duplicate all nodes */
/***************************/
	for_all_nodes( graph, node )
	{
		nnode = make_node( ngraph, make_attr( ATTR_DATA, (char *)node ) );
	/*************************************/
	/* the copied node has to his origin */
	/*************************************/
		nnode->iso = node;
		node->iso = nnode;	/* only for copying the edges */
	} end_for_all_nodes( graph, node );

/****************************************/
/* duplicate the edges of the old graph */
/****************************************/
	for_all_nodes( graph, node )
	{
		for_sourcelist( node, edge )
		{
			if ( unique_edge( edge ) )
			{
				make_edge( edge->tnode->iso, edge->snode->iso, make_attr( ATTR_DATA, (char *)edge ) );
			}
		} end_for_sourcelist( node, edge );
	} end_for_all_nodes( graph, node );

	return( ngraph );
}
/******************************************************************************/



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
