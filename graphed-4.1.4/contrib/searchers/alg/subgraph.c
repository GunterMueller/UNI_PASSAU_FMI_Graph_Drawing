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
#include <sgraph/graphed.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/algorithm.h>
#include <search/control.h>
#include <search/move.h>
#include <search/error.h>


/******************************************************************************
*                                                                             *
*			local defines 		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local macros  		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local types		 			      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local functions					      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			global variables				      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			local variables					      *
*                                                                             *
*******************************************************************************/



/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/

/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/


void	SubgraphSearch(Sgraph graph, Method method)
{

	Snode	node,
		nnode,
		neighbour;
	Sgraph	local;
	Slist	list = empty_slist,
		elt,
		next = empty_slist;

/*********************************/
/* create a new undirected graph */
/*********************************/
        local = make_graph( make_attr( ATTR_FLAGS, 0 ) );
 
        local->directed = FALSE;
 
/***************************/
/* now duplicate all nodes */
/***************************/
        for_all_nodes( graph, node )
        {
                nnode = make_node( local, make_attr( ATTR_DATA, (char *)node )
);
        /*************************************/
        /* the copied node has to his origin */
        /*************************************/
                nnode->iso = node;
                node->iso = nnode;      /* only for copying the edges */

		set_nodeattrs( nnode, make_attr( ATTR_FLAGS, FALSE ) );
        } end_for_all_nodes( graph, node );

	node = first_node_in_graph( graph );
	list = add_to_slist( list, make_attr( ATTR_DATA, node ) );
	set_nodeattrs( node->iso, make_attr( ATTR_FLAGS, TRUE ) );

	do
	{
		for_slist( list, elt )
		{
			node = (Snode)attr_data( elt );
			for_all_adjacent_nodes( node, neighbour )
			{
				if ( !attr_flags( neighbour->iso ) )
				{
					next = add_to_slist( next, make_attr( ATTR_DATA, neighbour ) );
					set_nodeattrs( neighbour->iso, make_attr( TRUE ) );
					make_edge( node->iso, neighbour->iso, make_attr( ATTR_FLAGS, 0 ) );
				}
			} end_for_all_adjacent_nodes( node, neighbour )
		} end_for_slist( list, elt )
		free_slist( list );
		list = next;
		next = empty_slist;
	} while ( list != empty_slist );	

/*
a		ache bfs baue baum
rufe baumalg mit method auf

remove baum
*/
	COinitSearchStructure( local ,0 /* ? */);
	TreeInitAndTest( local, method );
	TreeSearch( local, method );
	TreeFree( local, method );

	COfreeSearchStructure( local );

	COnotifyInfo( "Lower bound !" );
	return;	
}


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
