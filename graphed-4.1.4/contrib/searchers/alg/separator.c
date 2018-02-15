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
#include <search/control.h>
#include <search/algorithm.h>
#include <search/move.h>
#include <search/error.h>

#include <treewidth/mystd.h>

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

int     MarkNodes(Sgraph graph, int with);

/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	CopyGraph

arguments   :	
   	type		name		description(-I/-O)
1.	Sgraph		graph		-I

return()    :	void

description :

use	    :	

restrictions:	the graph 'graph' must have a iso pointer to the
		original graph if he was copied, if it is the 
		original graph itself then it must have a self-
		reference to itself.
		( well, only the nodes, and not the edges or the 
		  graph pointer, just use node->iso )
		'graph' must have have 'SeparatorNode' in his attr field !!

bugs	    :	none reported

*******************************************************************************/
static Sgraph	CopyGraph(Sgraph graph)
{
	Sgraph	newgraph;
	Snode	node,
		newnode;
	Sedge	edge;
	SeparatorNode	*iso,
			*tnode_iso,
			*snode_iso;
	int	N = COnodes();

        if ( graph == empty_sgraph )
                 return( empty_sgraph );

        newgraph = make_graph( make_attr( ATTR_FLAGS, 0 ) );
        if ( graph->nodes == empty_node )
                 return( newgraph );

        newgraph->directed = FALSE;

/* copy all nodes */
        for_all_nodes( graph, node )
        {
                newnode = make_node( newgraph, make_attr( ATTR_DATA, NULL ));
		iso = (SeparatorNode *)malloc( sizeof( SeparatorNode ) );
		newnode->iso = node->iso;
		iso->separator_mark = N;
		iso->isoorg = node;
		iso->mark = FALSE;
		iso->isocopy = (Snode)FALSE;
		set_nodeattrs( newnode, make_attr( ATTR_DATA, iso ) );

		iso = (SeparatorNode *)attr_data( node );
		iso->isocopy = newnode;
        } end_for_all_nodes( graph, node );


/* copy all edges */
        for_all_nodes( graph, node )
        {
                for_sourcelist( node, edge )
                {
                        if ( unique_edge( edge ) )
                        {
                                tnode_iso = (SeparatorNode *)attr_data( edge->tnode );
                                snode_iso = (SeparatorNode *)attr_data( edge->snode );
 
 
         
                                make_edge( snode_iso->isocopy, tnode_iso->isocopy, make_attr( ATTR_DATA, (char *)edge ) );
                        }
                } end_for_sourcelist( node, edge );
        } end_for_all_nodes( graph, node );
 
        return( newgraph );
}
/******************************************************************************/

static int	ComponentSize(Sgraph graph, Snode *nodelist, int index)
{
        Snode   node, neighbour;
	SeparatorNode	*iso;
	int	size = 0,
		N,
		i;
	bool	changed;
	
/* mark all nodes as not checked */ 
	N = MarkNodes( graph, FALSE );

/* get starting node */ 
        node = *(nodelist+index);
/* mark it as checked, first one found ==> increase size */
        iso = (SeparatorNode *)attr_data( node );
        iso->mark = TRUE;
        size++;
  


        do {
		/* let's assume nothing will change */
                changed = FALSE;

		/* test all nodes in the nodelist */ 
                for( i = 0; i < index; i++ )
                {
                        node = *(nodelist+i);
                        iso = (SeparatorNode *)attr_data( node );
                        if ( iso->mark )
                                continue; /* already checked */
 
                        for_all_adjacent_nodes( node, neighbour )
                        {
                                iso = (SeparatorNode *)attr_data( neighbour );
                                if ( iso->mark )
                                {
                                        iso = (SeparatorNode *)attr_data( node );
                                        iso->mark = TRUE;
                                        changed = TRUE;
                                        size++;
					if ( size > N/2 )
						return( N );
                                        break;
                                }
                        } end_for_all_adjacent_nodes( node, neighbour );
                }
        } while ( changed );
 
        return ( size );
}

static Slist    AdjacentSeparator(Sgraph graph, Snode *nodelist, int index)
{
        Snode   node, neighbour;
        int     i,
		marked;
        bool    changed;
        Slist   list = (Slist)NULL, returnlist = (Slist)NULL,
                elt;
	SeparatorNode	*iso;
	
	MarkNodes( graph, 0 );
 
 
        node = *(nodelist+index);
        iso = (SeparatorNode *)attr_data( node );
        iso->mark = 1;
        list = add_to_slist( list, make_attr( ATTR_DATA, node ) );
 
        do {
                changed = FALSE;
 
                for( i = 0; i < index; i++ )
                {
                        node = *(nodelist+i);
                        iso = (SeparatorNode *)attr_data( node );
                        if ( iso->mark == 1)
                                continue;
 
                        for_all_adjacent_nodes( node, neighbour )
                        {
                                iso = (SeparatorNode *)attr_data( neighbour );
                                if ( iso->mark == 1 )
                                {
                                        iso = (SeparatorNode *)attr_data( node );
                                        iso->mark = 1;
                                        list = add_to_slist( list, make_attr( ATTR_DATA, node ) );
                                        changed = TRUE;
                                        break;
                                }
                        } end_for_all_adjacent_nodes( node, neighbour );
                }
        } while ( changed );

	/* list contains one separated component */ 
        for_slist( list, elt )
        {
                for_all_adjacent_nodes( (Snode)attr_data( elt ), node )
                {
                        iso = (SeparatorNode *)attr_data( node );
                        if ( iso->mark == 0 )
                                returnlist = add_to_slist( returnlist, make_attr( ATTR_DATA, node  ) );
                } end_for_all_adjacent_nodes( (Snode)attr_data( elt ), node )
        } end_for_slist( list, elt );
 
        free_slist( list );
	list = (Slist)NULL;
	free( nodelist );

	for_slist( returnlist, elt )
	{
		node = (Snode)attr_data( elt );
		iso = (SeparatorNode *)attr_data( node );
		iso->mark = 2;
	} end_for_slist( returnlist, elt )
	for_slist( returnlist, elt )
	{
		/* used changed as bool flag */
		changed = FALSE;
		marked = 3;
		for_all_adjacent_nodes( (Snode)attr_data( elt ), node )
		{
                        iso = (SeparatorNode *)attr_data( node );
			if ( iso->mark != 2 && marked == 3 )
				marked = iso->mark;

			if ( ( iso->mark == 0 && marked == 1 )
			     || (iso->mark == 1 && marked == 0 ) )
			{
				changed = TRUE;
				break;
			}	
		} end_for_all_adjacent_nodes( (Snode)attr_data( elt ), node )
		if ( !changed )
		{
			list = add_to_slist( list, make_attr( ATTR_DATA, elt ) );
		}
	} end_for_slist( returnlist, elt )

	if ( size_of_slist( list ) < size_of_slist( returnlist ) )
	{
		for_slist( list, elt )
		{
			returnlist = subtract_immediately_from_slist( returnlist,
					(Slist)attr_data( elt )  );
		} end_for_slist( list, elt );
	}

	if ( list != (Slist)NULL )
		free_slist( list );

	return( returnlist );
} 

static Slist	InitialSeparator(Sgraph graph)
{
	Sgraph	local;
	int	index,
		min,
		max,
		maxindex = 0,
		size,
		degree,
		N;
	Snode	*nodelist, 
		*nodelist_item;
	Snode	minnode,
		node,
		neighbour,
		node2;
	SeparatorNode	*iso;
	bool	notadjacent;

	local = CopyGraph( graph );
	N = MarkNodes( local, FALSE );

	if ( N <= 3 )
	{
		Slist 	list = (Slist)NULL;

		for_all_nodes( local, node )
		{
			list = add_to_slist( list, make_attr( ATTR_DATA, node ) );			
		} end_for_all_nodes( local, node )
		return( list );
	}

	
        nodelist = (Snode *)calloc( N, sizeof( Snode ) );

        index = 0;
        do
        { 
                min = N+1; /* cannot exceed N */
                for_all_nodes( local, node )
                {
                        if ( ( degree = DegreeNode( node ) ) < min )
                        {
                                min = degree;
                                minnode = node;
                        }
                } end_for_all_nodes( local, node );

                nodelist_item = nodelist+index;

                iso = (SeparatorNode *)attr_data( minnode );
                *nodelist_item = iso->isoorg;
 
                for_all_adjacent_nodes( minnode, node )
                {
                        for_all_adjacent_nodes( minnode, neighbour );
                        {
                                if ( node == neighbour )
                                        continue;
                                notadjacent = FALSE;
                                for_all_adjacent_nodes( node, node2 )
                                {
                                        if ( node2 == neighbour )
                                        {
                                                notadjacent = TRUE;
                                                break;
                                        }
                                } end_for_all_adjacent_nodes( node, node2 )

                                if ( !notadjacent )
                                        make_edge( node, neighbour, make_attr( ATTR_DATA, NULL ) );
                        } end_for_all_adjacent_nodes( minnode, neighbour );

                } end_for_all_adjacent_nodes( minnode, node );

		iso = (SeparatorNode *)attr_data( minnode );
		free( iso );
                remove_node( minnode );

                index++;
        } while ( local->nodes != empty_node );


        remove_graph( local );

        /* now nodelist contains c_0,c_1, ... , c_(N-1) */

        max = 0;
        for( index = 0; index < N; index++ )
        {
                size = ComponentSize( graph, nodelist, index );
                if ( size >= N/2 ) continue;
                if ( max < size )
                {
                        max = size;
                        maxindex = index;
                }

        }


        return( AdjacentSeparator( graph, nodelist, maxindex ) );
}




/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/
int 	MarkNodes(Sgraph graph, int with)
{
	int	N = 0;
	Snode	node;
	SeparatorNode	*iso;

        for_all_nodes( graph, node )
        {
                iso = (SeparatorNode *)attr_data( node );
                iso->mark = with;
		N++;
        } end_for_all_nodes( graph, node );

	return( N );
}


Slist berechne_sep(Sgraph sgraph)
{Sgraph sgraph2;
 Slist slist;


/* sgraph2=kopiere_sgraph(sgraph); */
 my_print_sgraph(sgraph);
  slist=Separator(sgraph2);
  /*
  slist=Separator(sgraph2);
  */

 return slist;
}





Slist	Separator(Sgraph graph)
{
	Slist	separator;
	
	separator = InitialSeparator( graph );

	return( separator );
}



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
