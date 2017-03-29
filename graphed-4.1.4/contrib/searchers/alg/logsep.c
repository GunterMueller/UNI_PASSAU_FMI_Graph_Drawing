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


extern int	MarkNodes(Sgraph graph, int with);

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

typedef int (*sortfunc)(const void *, const void *);

typedef struct {
	int			number_of_nodes;
	Sgraph			graph;
} GraphList;


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



static void	MarkComponent(Snode node, int nr)
{
	Snode		neighbour;
	SeparatorNode	*iso;

	for_all_adjacent_nodes( node, neighbour )
	{
		iso = (SeparatorNode *)attr_data( neighbour );
		if ( iso->mark == 0 )
		{
			iso->mark = nr;
			MarkComponent( neighbour, nr );
		}
	} end_for_all_adjacent_nodes( node, neighbour )
		
	return;
}


static int 	GLCompare(GraphList *item1, GraphList *item2)
{
	return( item1->number_of_nodes - item2->number_of_nodes );
}

static GraphList	*GetComponentsSorted(Sgraph graph, Slist separator)
{
	
	Sgraph	ngraph;
	GraphList	*gl,
		*gl_item;
	Slist	elt;
	Snode	node,
		neighbour,
		newnode;
	Sedge	edge;
	SeparatorNode	*iso,
		*tnode_iso,
		*snode_iso;
	int	component_nr = 1;
	int	index;

	MarkNodes( graph, 0 );
	for_slist( separator, elt )	
	{
		node = (Snode)attr_data( elt );
		iso = (SeparatorNode *)attr_data( node );
		iso->mark = component_nr;
	} end_for_slist( separator, elt );
	
	for_slist( separator, elt )	
	{
		node = (Snode)attr_data( elt );
		for_all_adjacent_nodes( node, neighbour )
		{
			iso = (SeparatorNode *)attr_data( neighbour );
			if ( iso->mark == 0 )
			{
				component_nr++;
				iso->mark = component_nr;
				MarkComponent( neighbour, component_nr );
			}
			
		} end_for_all_adjacent_nodes( node, neighbour )
	} end_for_slist( separator, elt );

	gl = (GraphList *)calloc( component_nr, sizeof( GraphList ) );

	for ( index = 2; index <= component_nr; index++ )
	{
		ngraph = make_graph( make_attr( ATTR_FLAGS, 0 ) );
		ngraph->directed = FALSE;

		gl_item = gl+index-2;
		gl_item->graph = ngraph;
		gl_item->number_of_nodes = 0;
	}
	gl_item = gl+component_nr-1;
	gl_item->graph = (Sgraph)NULL;
	gl_item->number_of_nodes = 0;

	if ( component_nr == 1 )
		return( gl );
	for_all_nodes( graph, node )	
	{
		iso = (SeparatorNode *)attr_data( node );
		if ( iso->mark == 1 ) continue;

		gl_item = gl+iso->mark-2;
		gl_item->number_of_nodes++;
		newnode = make_node( gl_item->graph, make_attr( ATTR_DATA, NULL ) );
                iso->isocopy = newnode;
		newnode->iso = node->iso;
                iso = (SeparatorNode *)malloc( sizeof( SeparatorNode ) );
                iso->isoorg = node;
                iso->mark = FALSE;
                iso->isocopy = (Snode)FALSE;
                set_nodeattrs( newnode, make_attr( ATTR_DATA, iso ) );
 
	} end_for_all_nodes( graph, node );
	
	for_all_nodes( graph, node )
	{
               for_sourcelist( node, edge )
                {
                        if ( unique_edge( edge ) )
                        {
                                tnode_iso = (SeparatorNode *)attr_data( edge->tnode
);
                                snode_iso = (SeparatorNode *)attr_data( edge->snode
);
 
 
				if ( snode_iso->mark == tnode_iso->mark && snode_iso->mark > 1 ) 
                                	make_edge( snode_iso->isocopy, tnode_iso->isocopy, make_attr( ATTR_DATA, (char *)edge ) );
                        }
                } end_for_sourcelist( node, edge );
 
	} end_for_all_nodes( graph, node );


	qsort( gl, component_nr-1, sizeof( GraphList ), (sortfunc)GLCompare );


	return( gl );
}


static void	MinComponentSeparator(Sgraph graph)
{
        Slist   initial, elt;
        Snode   node;
	Sgraph	component;
        SeparatorNode  *new;
	int	count = 0;
	Sedge	edge;

	GraphList	*graphlist = (GraphList *)NULL,
			*gl_item;;

        for_all_nodes( graph, node )
        {
		count++;
                new = (SeparatorNode *)malloc( sizeof( SeparatorNode ) );
                new->mark = FALSE;
                new->isoorg = node->iso;
                new->isocopy = (Snode)FALSE;
                set_nodeattrs( node, make_attr( ATTR_DATA, new ) );
        } end_for_all_nodes( graph, node );
	if ( count <= 3 )
	{
		for_all_nodes( graph, node )
		{
			SetOnCheck( node->iso );
			if ( COmethod() == METHOD_EDGE_SEARCH )
			{
				for_sourcelist( node->iso, edge )
				{
					if ( StateEdge( edge ) == EDGE_NOT_CLEAR 
					     && StateNode( edge->tnode ) == NODE_SET 
					     && StateNode( edge->snode ) == NODE_SET )
					{
						MoveCheck( edge );	
					}
				} end_for_sourcelist( node->iso, edge )

			}
		} end_for_all_nodes( graph, node )
		return;
	}

        initial = Separator( graph );


        for_slist( initial, elt )
        {
                node = (Snode)attr_data( elt );
                SetOnCheck( node->iso );
		if ( COmethod() == METHOD_EDGE_SEARCH )
		{
			for_sourcelist( node->iso, edge )
			{
				if ( StateEdge( edge ) == EDGE_NOT_CLEAR 
				     && StateNode( edge->tnode ) == NODE_SET 
				     && StateNode( edge->snode ) == NODE_SET )
				{
					MoveCheck( edge );	
				}
			} end_for_sourcelist( node->iso, edge )

		}
        } end_for_slist( initial, elt );

	graphlist = GetComponentsSorted( graph, initial );

	if ( graphlist != (GraphList *)NULL ) 
	{
		gl_item = graphlist;
		while ( gl_item->number_of_nodes != 0 )
		{
			component = gl_item->graph;
			if ( component == empty_graph || component->nodes == empty_node )
			{}
			else
			{
				MinComponentSeparator( component );	
			}	
			for_all_nodes( component, node )
			{
                		free( (SeparatorNode *)attr_data( node ) );
			} end_for_all_nodes( component, node )
			remove_graph( component );
			gl_item++;
		} 
	}
        for_all_nodes( graph, node )
        {
                free( (SeparatorNode *)attr_data( node ) );
        } end_for_all_nodes( graph, node );
	free( graphlist );
	remove_graph( component );

}


/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/

void    MinComponentLogSepSearch(Sgraph graph, Method method)
{
	Sgraph  local;

	local = MakeCopyOfGraph( graph );
	MinComponentSeparator( local );

	COnotifyInfo( "Result is not the optimum" );

	remove_graph( local );
	return;
}



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
