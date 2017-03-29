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

static int      mindeg, maxdeg = 0;
static Snode    startnode;



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

void	GridInitAndTest(Sgraph graph, Method method)
{

        Snode   node;
        int     deg;
        int     number_mindeg_nodes = 0;
        
        startnode = first_node_in_graph( graph );
        mindeg = DegreeNode( startnode );
 
        for_all_nodes( graph, node )
        {
                deg = DegreeNode( node );
                if ( mindeg > deg )
                {
                        mindeg = deg;
                        startnode = node;
                        number_mindeg_nodes = 0;
                }
                if ( mindeg == deg ) number_mindeg_nodes++;
 
                if ( maxdeg < deg ) maxdeg = deg;
 
        } end_for_all_nodes( graph, node );
 
        if ( number_mindeg_nodes != (1<<mindeg) )
                SetErrorAndReturnVoid( ERROR_NOT_A_GRID );


}

void	GridSearch(Sgraph graph, Method method)
{
        Snode   node,
		set_neighbour,
                sdeg_node;

        Slist   setlist = (Slist)NULL,
                elt,
                newlist = (Slist)NULL;
 
        int     local_min;
        
        SetOn( startnode );
 
        newlist = add_to_slist( newlist, make_attr( ATTR_DATA, (char *)startnode ) );
        do
        { 
                setlist = newlist;
                newlist = (Slist)NULL;
                do
                { 
                        local_min = maxdeg+1;
                        for_slist( setlist, elt )
                        {
                                node = (Snode)attr_data( elt );
                                if ( DegreeEdgeNotClear( node ) < local_min )           
                                {
                                        sdeg_node = node;
                                        local_min = DegreeEdgeNotClear( node );         
                                }
                        } end_for_slist( setlist, elt )
 
                        setlist = (Slist)subtract_from_slist( setlist, make_attr( ATTR_DATA, (char *)sdeg_node ));
 
                        if ( method == METHOD_EDGE_SEARCH )
                        {
                                for_all_adjacent_set_nodes( sdeg_node, node )           
                                {
                                        MoveTo( sdeg_node, node );
					for_all_adjacent_set_nodes( node, set_neighbour )
					{
						MoveTo( node, set_neighbour );
					} end_for_all_adjacent_set_nodes( node, set_neighbour )

                                } end_for_all_adjacent_set_nodes( sdeg_node, node )     
                        }
                        for_all_adjacent_not_set_nodes( sdeg_node, node
)               
                        {
                                if ( method != METHOD_EDGE_SEARCH )
                                {
                                        SetOn( node );
                                }
                                else
                                {
                                        MoveTo( sdeg_node, node );
					for_all_adjacent_set_nodes( node, set_neighbour )
					{
						MoveTo( node, set_neighbour );
					} end_for_all_adjacent_set_nodes( node, set_neighbour )
                                }
                                newlist = add_to_slist( newlist, make_attr( ATTR_DATA, (char *)node ));
                        } end_for_all_adjacent_not_set_nodes( sdeg_node, node );        
                } while( setlist != (Slist)NULL );
        } while ( newlist != (Slist)NULL );
 

}


/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
