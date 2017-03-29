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

#define KNM_NODES_NOTDEF        0
#define KNM_NODES_M             1
#define KNM_NODES_N             2


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

static int      deg_n = 0, deg_m = 0;
 


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

void	KnmInitAndTest(Sgraph graph, Method method)
{

        Snode   	node, adjnode;
        NodeState       nstate, n2state;
	int	count;

        for_all_nodes( graph, node )
        {
                nstate = (NodeState)attr_data( node );
                set_nodestateattrs( nstate, make_attr( ATTR_FLAGS, KNM_NODES_NOTDEF ) );
                set_nodeattrs( node, make_attr( ATTR_DATA, (char *)nstate ) );
        } end_for_all_nodes( graph, node );
 
        node = first_node_in_graph( graph );
 
        nstate = (NodeState)attr_data( node );
        set_nodestateattrs( nstate, make_attr( ATTR_FLAGS, KNM_NODES_N ) );     
        set_nodeattrs( node, make_attr( ATTR_DATA, (char *)nstate ) );
 
        for_all_adjacent_nodes( node, adjnode )
        {
                nstate = (NodeState)attr_data( adjnode );
                set_nodestateattrs( nstate, make_attr( ATTR_FLAGS, KNM_NODES_M ) );
                set_nodeattrs( adjnode, make_attr( ATTR_DATA, (char *)nstate ) );
        } end_for_all_adjacent_nodes( node, adjnode );
 
        for_all_nodes( graph, node )
        {
                nstate = (NodeState)attr_data( node );
                if ( attr_flags( nstate ) == KNM_NODES_NOTDEF )
                {
                        set_nodestateattrs( nstate, make_attr( ATTR_FLAGS, KNM_NODES_N ) );
                        set_nodeattrs( node, make_attr( ATTR_DATA, (char *)nstate ) );
                }
        } end_for_all_nodes( graph, node );



        deg_n = 0; deg_m = 0;

        for_all_nodes( graph, node )
        {
                nstate = (NodeState)attr_data( node );
                if ( attr_flags( nstate ) == KNM_NODES_N )
                        deg_m++;
                else if ( attr_flags( nstate ) == KNM_NODES_M )
                        deg_n++;
                else SetErrorAndReturnVoid( ERROR_NOT_A_KNM );

        } end_for_all_nodes( graph, node )
        
        for_all_nodes( graph, node )
        {
		count = 0;
                nstate = (NodeState)attr_data( node );
                for_all_adjacent_nodes( node, adjnode )
                {
                        n2state = (NodeState)attr_data( adjnode );
                        if ( attr_flags( nstate ) == attr_flags( n2state ) )
                                SetErrorAndReturnVoid( ERROR_NOT_A_KNM );
			count++; 
                } end_for_all_adjacent_nodes( node, adjnode );

		if ( attr_flags( nstate ) == KNM_NODES_M )
		{
			if ( count != deg_m )
                                SetErrorAndReturnVoid( ERROR_NOT_A_KNM );
		}
       		else
		{
			if ( count != deg_n )
                                SetErrorAndReturnVoid( ERROR_NOT_A_KNM );
		}

        } end_for_all_nodes( graph, node );

}

void	KnmSearch(Sgraph graph, Method method)
{

        int     searchers = 0;
        int     mindeg = minimum( deg_n, deg_m );
 
        Snode   node, startnode, adjnode;
        NodeState       nstate;
 
        if ( COanimate() )
        {
                for_all_nodes( graph, node )
                {
                        nstate = (NodeState)attr_data( node );
                        if ( deg_n == mindeg  &&
                             attr_flags( nstate ) == KNM_NODES_N )
                        {
                                startnode = node;
                                break;
                        }
                        else if ( deg_m == mindeg &&
                             attr_flags( nstate ) == KNM_NODES_M )
                        {
                                startnode = node;
                                break;
                        }
                                 
                } end_for_all_nodes( graph, node );
                
                SetOn( startnode );
 
                if ( method != METHOD_EDGE_SEARCH )
                {
                        for_all_adjacent_nodes( startnode, node )
                        {
                                SetOn( node );
                        } end_for_all_adjacent_nodes( startnode, node );

 
                        for_all_not_set_nodes( graph, node )
                        {
                                SetOn( node );
                        } end_for_all_not_set_nodes( graph, node )
                }
                else
                {
                        for_all_adjacent_nodes( startnode, node )
                        {
                                MoveTo( startnode, node );
                        } end_for_all_adjacent_nodes( startnode, node )
                        /* take last node `node` */
                        startnode = node;
 
                        for_all_adjacent_not_set_nodes( startnode, node
)
                        {
                                for_all_adjacent_nodes( node, adjnode );                                {
                                        MoveTo( node, adjnode );
                                } end_for_all_adjacent_nodes( node, adjnode );
                        } end_for_all_adjacent_nodes( startnode, node )
                }
        }
        else
        {
                searchers = mindeg + 1;
                if ( method == METHOD_EDGE_SEARCH )
                        searchers++;
                if ( mindeg == 2 &&
                     method == METHOD_EDGE_SEARCH )
                        searchers--;
                if ( mindeg == 1 )
		{
                        searchers = 2;
                        if ( deg_n == deg_m ) /* both are 1 */
                                searchers = 1;
		}
                COsetMaxSearchers( searchers );
                COsetHiddenMaxSearchers( searchers );
        }

}


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
