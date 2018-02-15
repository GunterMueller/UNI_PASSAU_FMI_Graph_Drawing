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

	provides some useful functions about nodes and edges 
	e.g. different degree functions, about the search state




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
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	StateNode

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	SearchState

description :	gets current search state of a node i.e. does 'node'
		contain a searcher, still contaminated or is the searcher
		removed

use	    :	

restrictions:	'node' must be initialized !

bugs	    :	no null pointer check, no initialized check of node

*******************************************************************************/
SearchState	StateNode(Snode node)
{
	NodeState	get = (NodeState)attr_data( node );

	return( get->state );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	StateEdge

arguments   :	
   	type		name		description(-I/-O)
1.	Sedge		edge		-I

return()    :	SearchState

description :	gets the current search state of an edge i.e. is the edge
		still contaminated or is it decontaminated

use	    :	

restrictions:	'edge' must be initialized !

bugs	    :	no null pointer check, no initialized check of edge

*******************************************************************************/
SearchState	StateEdge(Sedge edge)
{
	EdgeState	get = (EdgeState)attr_data( edge );

	return( get->state );
}
/******************************************************************************/


unsigned int	DegreeEdge(Snode node)
{
	Sedge	edge;
	unsigned int	count = 0;

	for_sourcelist( node, edge )
	{
		count++;
	} end_for_sourcelist( node, edge )

	return( count );
}


/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	Degree

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	unsigned int

description :	returns the degree of node = number of edges including the
		multiple edges but without selfloops

use	    :	

restrictions:	'node' must be initialized !

bugs	    :	no null pointer check, no initialized check of node

*******************************************************************************/
unsigned int	Degree(Snode node)
{
	NodeState	nstate = (NodeState)attr_data( node );

	return( nstate->degree );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	DegreeEdgeNotClear

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	unsigned int

description :	gets the number edges incident to 'node' and are still
		contaminated (including multiple edges but no selfloops)

use	    :	

restrictions:	'node' must be initialized !

bugs	    :	no null pointer check, no initialized check of node

*******************************************************************************/
unsigned int	DegreeEdgeNotClear(Snode node)
{
	NodeState	nstate = (NodeState)attr_data( node );

	return( nstate->not_clear );
}	
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	DegreeNode

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	unsigned int

description :	returns the number of adjacent nodes

use	    :	

restrictions:	

bugs	    :	no null pointer check

*******************************************************************************/
unsigned int	DegreeNode(Snode node)
{
	Snode           neighbour;
        int             counter = 0;
	Slist		list,
			elt;

	list = make_slist_of_sourcelist( node );

	for_slist( list, elt )
	{
		neighbour = (Snode)attr_data( elt );	
                if ( node != neighbour ) counter++;
	} end_for_slist( list, elt )
        /*************************************************
        for_all_adjacent_nodes( node, neighbour )
        {
                if ( node != neighbour ) counter++;
        } end_for_all_adjacent_nodes( node, neighbour );
	*************************************************/
	free_slist( list );

        return( counter );

}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	DegreeNodeNotSet

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	unsigned int

description :	returns the number of still contaminated adjacent nodes of
		'node'

use	    :	

restrictions:	'node' must be initialized !

bugs	    :	no null pointer check, no initialized check of node

*******************************************************************************/
unsigned int 	DegreeNodeNotSet(Snode node)
{
        Snode           neighbour;
        int             counter = 0;
         
        for_all_adjacent_not_set_nodes( node, neighbour )
        {
                if ( node != neighbour ) counter++;
        } end_for_all_adjacent_not_set_nodes( node, neighbour );

        return( counter );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	DegreeNodeSet

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	unsigned int

description :	returns the number of adjacent nodes which are containing
		a searcher

use	    :	

restrictions:	'node' must be initialized !

bugs	    :	no null pointer check, no initialized check of node

*******************************************************************************/
unsigned int 	DegreeNodeSet(Snode node)
{
        Snode           neighbour;
        int             counter = 0;
         
        for_all_adjacent_set_nodes( node, neighbour )
        {
                if ( node != neighbour ) counter++;
        } end_for_all_adjacent_set_nodes( node, neighbour );

        return( counter );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	DegreeNodeUnset

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node		-I

return()    :	unsigned int

description :	returns the number of decontaminated adjavent nodes that have
		contained a searcher (the searcher was removed)

use	    :	

restrictions:	'node' must be initialized !

bugs	    :	no null pointer check, no initialized check of node

*******************************************************************************/
unsigned int	DegreeNodeUnset(Snode node)
{
        Snode           neighbour;
        int             counter = 0;
         
        for_all_adjacent_unset_nodes( node, neighbour )
        {
                if ( node != neighbour ) counter++;
        } end_for_all_adjacent_unset_nodes( node, neighbour );

        return( counter );
}
/******************************************************************************/



/*******************************************************************************
*									       *
*			global function					       *
*									       *
********************************************************************************

name        :	GetEdgeNotClearBetween

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		node1		-I
2.	Snode		node2		-I

return()    :	Sedge

description :	gets an arbitrary, but still contaminated between the
		two nodes 

use	    :	

restrictions:	'nodeX' must be initialized !

bugs	    :	no null pointer check, no initialized check of node

*******************************************************************************/
Sedge	GetEdgeNotClearBetween(Snode node1, Snode node2)
{

	Sedge   edge;
        Snode   node;

/**********************************************************************/
/* we check the degree of both nodes, for fastening the traversing of */
/* the sourcelist the lower degree node will be choosen 	      */
/**********************************************************************/
        node = ( Degree( node1 ) < Degree( node2 ) ? node1 : node2 );

        for_sourcelist( node, edge )
        {
	/***********************************************************/
	/* the edge must be contaminated and between the two nodes */
	/***********************************************************/
                if (    StateEdge( edge ) == EDGE_NOT_CLEAR
                     && EdgeIsBetween( edge, node1, node2 ) )
                        return( edge );
        } end_for_sourcelist( node, edge );

/******************************************************/
/* none contaminated edge between both has been found */
/******************************************************/
	return( empty_edge );
}
/******************************************************************************/

Sedge	GetEdgeBetween(Snode node1, Snode node2)
{

	Sedge   edge;
        Snode   node;

        for_sourcelist( node, edge )
        {
                if ( EdgeIsBetween( edge, node1, node2 ) )
                        return( edge );
        } end_for_sourcelist( node, edge );

	return( empty_edge );
}


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
