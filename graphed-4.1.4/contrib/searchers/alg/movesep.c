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

#define DIRECTION_NONE		0
#define DIRECTION_REVERSE	-1
#define DIRECTION_FORWARD	1

/******************************************************************************
*                                                                             *
*			local macros  		 			      *
*                                                                             *
*******************************************************************************/

#define ReverseDirection( DIR )		((((DIR)+1)*(-1))+1)

/******************************************************************************
*                                                                             *
*			local types		 			      *
*                                                                             *
*******************************************************************************/

typedef int (*sortfunc)(const void *, const void *);

typedef struct {
	int	mark,
		size,
		separator_mark;
	Sgraph	graph;
} Component;


/******************************************************************************
*                                                                             *
*			local functions					      *
*                                                                             *
*******************************************************************************/

static void	Separate(Sgraph graph, Slist separator, int depth, int direction);

/******************************************************************************
*                                                                             *
*			global variables				      *
*                                                                             *
*******************************************************************************/

/******************************************************************************
*                                                                             *
*			global functions				      *
*                                                                             *
*******************************************************************************/

extern int MarkNodes(Sgraph graph, int with);

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
static int	DepthCompare(Component *item1, Component *item2)
{
	return( item1->separator_mark - item2->separator_mark );
}
static int	SizeCompareReverse(Component *item1, Component *item2)
{
	return( item2->size - item1->size );
}
static int	SizeCompare(Component *item1, Component *item2)
{
	return( item1->size - item2->size );
}

static void	SetOnSlist(Slist list)
{
	Slist	elt;
	Snode	node;
	Sedge	edge;

	for_slist( list, elt )
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
	} end_for_slist( list, elt )
}

static void    MarkComponent(Snode node, int nr)
{
        Snode           neighbour;
        SeparatorNode   *iso;

        for_all_adjacent_nodes( node, neighbour )
        {
                iso = (SeparatorNode *)attr_data( neighbour );
                if ( iso->mark  == -1 )
                {
                        iso->mark = nr;
                        MarkComponent( neighbour, nr );
                }
        } end_for_all_adjacent_nodes( node, neighbour )
                 
        return;
}
 
static	Component	*SortComponentsOfSeparator(Component *list, int direction)
{
	Component	*sep_list,
			*item;
	int		index = 0;

	item = list;
	while ( item->graph != empty_graph )
	{
		index++;
		item++;
	}

	if ( index == 0 )
		return( list );
	/* sort for separators */

	qsort( list, index, sizeof( Component ), (sortfunc)DepthCompare );	

	/* sort for size */

	sep_list = list;
	do 
	{
		item = sep_list;
		index = 0;
		do
		{
			index++;
			item++;	
		} while ( item->separator_mark == sep_list->separator_mark );
		if ( direction != DIRECTION_REVERSE )
			qsort( sep_list, index, sizeof( Component ), (sortfunc)SizeCompareReverse );
		else
			qsort( sep_list, index, sizeof( Component ), (sortfunc)SizeCompare );
		sep_list = item;
		
		
	} while ( item->graph != (Sgraph)NULL );

	return( list );

}

static Component	*SortComponentsSize(Component *list, int direction)
{
	Component	*item;
	int		index = 0;

	item = list;
	while ( item->graph != empty_graph )
	{
		index++;
		item++;
	}

	if ( direction == DIRECTION_REVERSE )
		qsort( list, index, sizeof( Component ), (sortfunc)SizeCompareReverse );
	else
		qsort( list, index, sizeof( Component ), (sortfunc)SizeCompare );

	return( list );
}

static	Component	*GetComponentsOfSeparator(Sgraph graph, Slist separator)
{
	Component	*comp_list,
			*comp_item;

	int		component_nr = 0,
			index,
			N = COnodes();
	Slist		elt;
	Sgraph		ngraph;
	Snode		node,
			newnode,
			neighbour;
	SeparatorNode	*tnode_iso,
			*snode_iso;
	Sedge		edge;
	SeparatorNode	*iso,
			*niso;




	MarkNodes( graph, -1 );
        for_slist( separator, elt )
        {
                node = (Snode)attr_data( elt );
                iso = (SeparatorNode *)attr_data( node );
                iso->mark = 0;
        } end_for_slist( separator, elt );
        
        for_slist( separator, elt )
        {
                node = (Snode)attr_data( elt );
                for_all_adjacent_nodes( node, neighbour )
                {
                        iso = (SeparatorNode *)attr_data( neighbour );
                        if ( iso->mark == -1 )
                        {
                                component_nr++;
                                iso->mark = component_nr;
                                MarkComponent( neighbour, component_nr );
                        }

                } end_for_all_adjacent_nodes( node, neighbour )
        } end_for_slist( separator, elt );

 
        comp_list = (Component *)calloc( component_nr+1, sizeof( Component ) );

        for ( index = 1; index <= component_nr; index++ )
        {
                ngraph = make_graph( make_attr( ATTR_FLAGS, 0 ) );
                ngraph->directed = FALSE;
 
        	comp_item = comp_list+index-1;
		comp_item->graph = ngraph;
		comp_item->size = 0;
		comp_item->mark = index;
		comp_item->separator_mark = N;
        }
	comp_item = comp_list+component_nr;
	comp_item->graph = (Sgraph)NULL;
	comp_item->size = 0;
	comp_item->mark = 0;
	comp_item->separator_mark = N+1;

	if ( component_nr == 0 )
	{
		return( comp_list );
	}


        for_all_nodes( graph, node )
        {
                iso = (SeparatorNode *)attr_data( node );
                if ( iso->mark == 0 ) continue;
 
                comp_item = comp_list+iso->mark-1;
                comp_item->size++;
                newnode = make_node( comp_item->graph, make_attr( ATTR_DATA, NULL
) );             
                iso->isocopy = newnode;
                newnode->iso = node->iso;
                niso = (SeparatorNode *)malloc( sizeof( SeparatorNode ) );
                niso->isoorg = node;
                niso->mark = iso->mark;
                niso->isocopy = (Snode)newnode;
		niso->separator_mark = iso->separator_mark;
		if ( niso->separator_mark < comp_item->separator_mark )
		{
			comp_item->separator_mark = niso->separator_mark;
		} 
                set_nodeattrs( newnode, make_attr( ATTR_DATA, niso ) );
 
        } end_for_all_nodes( graph, node );
         
        for_all_nodes( graph, node )
        {
               for_sourcelist( node, edge )
                {
                        if ( unique_edge( edge ) )
                        {
                                tnode_iso = (SeparatorNode *)attr_data( edge->tnode );
                                snode_iso = (SeparatorNode *)attr_data( edge->snode
);
 
                                if ( snode_iso->mark == tnode_iso->mark && snode_iso->mark >= 1 )
                                        make_edge( snode_iso->isocopy, tnode_iso->isocopy, make_attr( ATTR_DATA, (char *)edge ) );
                        }
                } end_for_sourcelist( node, edge );
 
        } end_for_all_nodes( graph, node );
 
	 

	return( comp_list );
		
}

static void     AdjacentNodesList(Slist list, Sgraph graph, int comp_nr, Slist *sep_list, Slist *graph_list, int same)
{
        Slist   elt;
        SeparatorNode   *iso;
        Snode   node,
                neighbour;
        bool    added;

        for_slist( list, elt )
        {
                node = (Snode)attr_data( elt );
                added = FALSE;
                for_all_adjacent_nodes( node, neighbour )
                {
                        iso = (SeparatorNode *)attr_data( neighbour );
                        if ( iso->mark == comp_nr )
                        {
                                if ( !added )
                                {
                                        *sep_list = add_to_slist( *sep_list, make_attr( ATTR_DATA, node ) );
                                        added = TRUE;

                                }
                                if ( same )
                                        *graph_list = add_to_slist( *graph_list,
make_attr( ATTR_DATA, neighbour ) );
                                else
                                        *graph_list = add_to_slist( *graph_list,
make_attr( ATTR_DATA, iso->isocopy ) );
                        }
                } end_for_all_adjacent_nodes( node, neighbour )
        } end_for_slist( list, elt );
 
        return;
}

static void    MarkTempComponent(Snode node)
{
        Snode           neighbour;
        SeparatorNode   *iso;

        for_all_adjacent_nodes( node, neighbour )
        {
                iso = (SeparatorNode *)attr_data( neighbour );
                if ( iso->temp_mark  == -1 )
                {
                        iso->temp_mark = 1;
                        MarkTempComponent( neighbour );
                }
        } end_for_all_adjacent_nodes( node, neighbour )

        return;
}
 
static bool     RestGraphConnected(Slist list, Sgraph graph)
{
	Snode	node,
		neighbour;
	SeparatorNode 	*marker;
	bool	component_marked = FALSE;
	Slist	elt;

	for_all_nodes( graph, node )
	{
		marker = (SeparatorNode *)attr_data( node );
		marker->temp_mark = -1;
	} end_for_all_nodes( graph, node );
	for_slist( list, elt )
	{
		node = (Snode)attr_data( elt );
		marker = (SeparatorNode *)attr_data( node );
		marker->temp_mark = 0;

	} end_for_slist( list, elt );
	for_slist( list, elt )
	{
		node = (Snode)attr_data( elt );
		for_all_adjacent_nodes( node, neighbour )
		{
			marker = (SeparatorNode *)attr_data( neighbour );
			if ( marker->temp_mark == -1 )
			{
				
				marker->temp_mark = 1;
				MarkTempComponent( neighbour );
				
				component_marked = TRUE;
				break;
			}
			
		} end_for_all_adjacent_nodes( node, neighbour );
		if ( component_marked )
			break;
	} end_for_slist( list, elt );
		
	for_all_nodes( graph, node )
	{
		marker = (SeparatorNode *)attr_data( node );
		if ( marker->temp_mark == -1 )
		{
			return( FALSE );
		}
	} end_for_all_nodes( graph, node );
	return( TRUE );
	
}
 
static void	MoveSeparator(int size_of_separator, Slist moving_nodes, Sgraph graph, int direction, int depth, int component_number)
{
	int	size_of_moving_nodes = size_of_slist( moving_nodes );

	Slist	separator,
		set_list = empty_slist,
		nodes_of_component = empty_slist,
		nodes_of_moving_nodes = empty_slist,
		elt;
	SeparatorNode	*iso;
	Snode	node;


	if ( size_of_separator < size_of_moving_nodes )
	{
		separator = Separator( graph );
		Separate( graph, separator, depth+1, DIRECTION_NONE );
		free_slist( separator );
		return;
	}


	/* mark the 'moving_nodes' no more belonging to the 'graph' */
        for_slist( moving_nodes, elt )
        {
                node = (Snode)attr_data( elt );
                iso = (SeparatorNode *)attr_data( node );
                iso->mark = 0;
        } end_for_slist( moving_nodes, elt );

	 

	if ( RestGraphConnected( moving_nodes, graph ) )
	{
		/* get the new 'moving_nodes' */
		AdjacentNodesList( moving_nodes, graph, component_number,
			&nodes_of_moving_nodes, &nodes_of_component, TRUE );


		if ( size_of_separator >= size_of_slist( nodes_of_component ) )
		{
			if ( direction == DIRECTION_FORWARD )
			{
				for_slist( moving_nodes, elt )
                        	{
                                        node = (Snode)attr_data( elt );
                                        SetOnCheck( node->iso );
                                        remove_node( node );
				} end_for_slist( moving_nodes, elt );
			}
			else
			{
				/* remove the 'moving_nodes' but save
				   their isomorphic orginal nodes for
				   latter setting */
				for_slist( moving_nodes, elt )
                        	{
                                        node = (Snode)attr_data( elt );
					set_list = add_to_slist( set_list,
						make_attr( ATTR_DATA, node->iso ) );
					remove_node( node );

				} end_for_slist( moving_nodes, elt );
			}
			if ( nodes_of_component != empty_slist )
			{
				MoveSeparator( size_of_separator, nodes_of_component,
					graph, direction, depth, component_number );
			}
			if ( direction == DIRECTION_REVERSE )
			{
	                        for_slist( set_list, elt )
                                {
                                        node = (Snode)attr_data( elt );
					/* we saved the '->iso' ! */
                                        SetOnCheck( node );
                                } end_for_slist( set_list, elt );
				free_slist( set_list );

			}
		}
		else
		{
			/* separate the 'graph' including the 'moving_nodes' */
			separator = Separator( graph );
			Separate( graph, separator, depth+1, DIRECTION_NONE );
			free_slist( separator );

		}
	}
	else
	{
		/* Move now every component for itself */
		Component	*component_list,
				*component_item;

		component_list = GetComponentsOfSeparator( graph, moving_nodes );
		component_list = SortComponentsSize( component_list, direction );	 
		component_item = component_list;
		while ( component_item->graph != empty_graph )
		{

			AdjacentNodesList( moving_nodes, graph, component_item->mark,

				&nodes_of_moving_nodes, &nodes_of_component, FALSE );

			if ( size_of_separator >= size_of_slist( nodes_of_component ) )
			{
				if ( direction == DIRECTION_FORWARD )
				{
					for_slist( nodes_of_moving_nodes, elt )
					{
						node = (Snode)attr_data( elt );
						SetOnCheck( node->iso );
					} end_for_slist( nodes_of_moving_nodes, elt );
				}
				if ( nodes_of_component != empty_slist )
				{
					MoveSeparator( size_of_separator, nodes_of_component,
						component_item->graph, direction, depth, component_item->mark );
				}
				if ( direction == DIRECTION_REVERSE )
				{
					for_slist( nodes_of_moving_nodes, elt )
					{
						node = (Snode)attr_data( elt );
						SetOnCheck( node->iso );
					} end_for_slist( nodes_of_moving_nodes, elt );

				}
			}
			else
			{
				/* separate the 'graph' including the 'moving_nodes' */
				separator = Separator( component_item->graph );
				Separate( component_item->graph, separator, depth+1, DIRECTION_NONE );
				free_slist( separator );

			}
			component_item++;
			if ( nodes_of_component != empty_slist )
			{ 
				free_slist( nodes_of_component );
				nodes_of_component = empty_slist;
			}
			if ( nodes_of_moving_nodes != empty_slist )
			{
				free_slist( nodes_of_moving_nodes );
				nodes_of_moving_nodes = empty_slist;
			}
		}
		for_slist( moving_nodes, elt )
		{
			node = (Snode)attr_data( elt );
			SetOnCheck( node->iso );
			remove_node( node );
		} end_for_slist( moving_nodes, elt );


	}
	return;
}

static void     Separate(Sgraph graph, Slist separator, int depth, int direction)
{
        Component       *component_list,
                        *component_item,
			*component_item_next;

	Slist		elt,
			nodes_of_separator = empty_slist,
			nodes_of_component = empty_slist,
			separator_of_component;
	Snode		node,
			neighbour;
	int		current_direction;
	SeparatorNode	*iso;

	component_list = GetComponentsOfSeparator( graph, separator );
	component_list = SortComponentsOfSeparator( component_list, direction );

	if ( component_list->graph == empty_graph )
	{
		SetOnSlist( separator );
		return; 
	}

 
        /* mark the adjacent nodes to the separator with 'depth' */
        for_slist( separator, elt )
        {
                node = (Snode)attr_data( elt );
                for_all_adjacent_nodes( node, neighbour )
                {
                        iso = (SeparatorNode *)attr_data( neighbour );
                        if ( iso->mark == 0 ) continue;
 
                        iso = (SeparatorNode *)attr_data( iso->isocopy );
                        iso->separator_mark = depth;
 
                } end_for_all_adjacent_nodes( node, neighbour );
                
        } end_for_slist( separator, elt );
 


	
        component_item = component_list;

	AdjacentNodesList( separator, graph, component_item->mark,
		&nodes_of_separator, &nodes_of_component, FALSE );

	if ( nodes_of_component != empty_slist )
	{
		if ( direction == DIRECTION_REVERSE )
			current_direction = DIRECTION_FORWARD;
		else
			current_direction = DIRECTION_REVERSE;
		MoveSeparator( size_of_slist( nodes_of_separator ),
			nodes_of_component, component_item->graph,
			current_direction, depth, component_item->mark );

		remove_graph( component_item->graph );
	}

	SetOnSlist( nodes_of_separator );


	component_item++;

	if ( component_item->graph == empty_graph )
	{
		SetOnSlist( separator );
		return;
	}

	component_item_next= component_list+2;
	while ( component_item_next->graph != empty_graph )
	{
		component_item++;
		component_item_next = component_item+1;
		separator_of_component = Separator( component_item->graph );
		Separate( component_item->graph, separator_of_component, depth+1, DIRECTION_NONE );
		remove_graph( component_item->graph );
		free_slist( separator_of_component );
	}


 
        if ( nodes_of_separator != empty_slist )
        {
                free_slist( nodes_of_separator );
                nodes_of_separator = empty_slist;
        }
        if ( nodes_of_component != empty_slist )
        {
                free_slist( nodes_of_component );
                nodes_of_component = empty_slist;
        }


	component_item = component_list + 1;
	
	AdjacentNodesList( separator, graph, component_item->mark,
		&nodes_of_separator, &nodes_of_component, FALSE );

	for_slist( nodes_of_separator, elt ) 
        {
                node = (Snode)attr_data( elt );                
                SetOnCheck( node->iso );
        } end_for_slist( nodes_of_separator, elt );
 
	if ( nodes_of_component != empty_slist )
	{
		if ( direction == DIRECTION_REVERSE )
			current_direction = DIRECTION_REVERSE;
		else
			current_direction = DIRECTION_FORWARD;
		MoveSeparator( size_of_slist( nodes_of_separator ),
			nodes_of_component, component_item->graph,
			current_direction, depth, component_item->mark );
		remove_graph( component_item->graph );
	}

        if ( nodes_of_separator != empty_slist )
                free_slist( nodes_of_separator );
        if ( nodes_of_component != empty_slist )
                free_slist( nodes_of_component );

	free( component_list );

	return;	
}




/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/

void    MovingSepSearch(Sgraph graph, Method method)
{
	Sgraph  local;
	Slist	separator;
	Snode	node;
	SeparatorNode *new;


	
	local = MakeCopyOfGraph( graph );
        for_all_nodes( local, node )
        {
                new = (SeparatorNode *)malloc( sizeof( SeparatorNode ) );
                new->mark = 0;
                new->isoorg = node->iso;
                new->isocopy = node;
                set_nodeattrs( node, make_attr( ATTR_DATA, new ) );
        } end_for_all_nodes( local, node );

	separator = Separator( local );
	Separate( local, separator, 0, DIRECTION_NONE );
	free_slist( separator );
	remove_graph( local );

	COnotifyInfo( "Result is not the optimum" );
	return;
}



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
