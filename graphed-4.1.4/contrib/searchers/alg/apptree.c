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

#include <math.h>
#include <stdio.h>
#include <string.h>

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

#define FifoIsEmpty( PTR )  ( (PTR) == (Fifo)NULL )
 

/******************************************************************************
*                                                                             *
*			local types		 			      *
*                                                                             *
*******************************************************************************/

/* Type: Fifo
 the 'Fifo'-type is used to implement a first-in-first-out structure.
 The FIFO-structure contains the nodes and the sequence im which the
 branches (definition see above) has to be searched */
 
typedef struct RecFifo {
        struct RecFifo *suc;
        Snode node;
} RecFifo, *Fifo;
 
 
/* Type: AppTreeNodeType
 the 'AppTreeNodeType'-type contains additional information to compute
 the strategy for the graph */
 
typedef struct RecAppTreeNodeType{
        int degree;
        int blocked;
        Fifo last, first;
} RecAppTreeNodeType, *AppTreeNodeType;
 

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

static Slist    deg1list = (Slist)0;  /* used in two functions, initialization
                                         and the strategy function itself */

static bool	tree_structure_initialized = FALSE;


/******************************************************************************
*                                                                             *
*                                                                             *
*                       local functions		                              *
*                                                                             *
*                                                                             *
******************************************************************************/



static  Fifo    FifoAppend(Fifo ptr, Snode node)
{
        Fifo    newptr;

        /* get a new ptr */
        newptr = (Fifo)malloc( sizeof( RecFifo ) );

        if ( newptr == (Fifo)NULL )
        {
                SetErrorAndReturn( IERROR_NO_MEM, (Fifo)0 );
        }

        newptr->node = node;
        newptr->suc = (Fifo)NULL; /* the end of the list */

        if ( ptr != (Fifo)NULL ) ptr->suc = newptr; /* append to 'ptr' */

        return ( newptr );
}

static Snode     FifoGetElement(Fifo *ptr)
{
        Snode   node;
        Fifo    hptr;

        if ( FifoIsEmpty( *ptr ) ) node = (Snode)NULL;
        else
        {
                node = (*ptr)->node;
                hptr = (*ptr);
                *ptr = (*ptr)->suc;
 
                free( hptr );
        }
        return( node );
}
 


static void    AppTreeTesting(Sgraph graph)
{
	if ( COnodes() != COedges()+1 )
        {
                SetError( ERROR_NOT_A_TREE );
                /* return; *//* only useful for additional code */
        }
        return;
}


static void     AppTreeProcSearch(Snode node, Method method)
{
        NodeState       nodestate;
        AppTreeNodeType    node_info;
        Snode           nextnode;
        Fifo            ptr;

        nodestate = (NodeState)attr_data( node );
        node_info = (AppTreeNodeType)attr_data( nodestate );

        while ( node_info->first != (Fifo)NULL )
        {
                ptr = node_info->first;
                nextnode = FifoGetElement( &ptr );
                node_info->first = ptr;

                /* MoveTo does a doubling of a searcher on a node only
                   if it is necessary,
                   MoveTo and SetOn delete all searchers
                   if it is possible without recontamination of the graph */
                if ( method == METHOD_NODE_SEARCH )
                         SetOn( nextnode );
                else MoveTo( node, nextnode );

                AppTreeProcSearch( nextnode ,method ); /* recursion */
        }
}



static bool AppTreeTestMoreThanTwo(Slist list)
{
        int count = 0;
        Slist   elt;
        for_slist( list, elt )
        {
                count++;
 
                /* it is sufficient for this algorithm, if we
                   know that more than two nodes are in the list 'list'
*/
                if ( count > 2 ) return( TRUE );
        } end_for_slist( list, elt )
        return( FALSE );
}
static bool AppTreeTestExactlyTwo(Slist list)
{
        int count = 0;
        Slist   elt;
        for_slist( list, elt )
        {
                count++;
        } end_for_slist( list, elt )
        if ( count == 2 ) return( TRUE );
        else return( FALSE );
}

static void	AppTreeWriteInfo(int searcher)
{
	int	more_searcher = 0;
	int	n;
	char	dummy[80];

	switch ( COmethod() )
	{
	case METHOD_EDGE_SEARCH:
		n = (int)(3.0*pow(2.0, (double)searcher-1.0 )-2.0);
		n = (int)(log( (float)n -1.0 )/log(3.0)) +1;
		break;
	case METHOD_MIXED_SEARCH:
		n = (int)(3.0*pow(2.0, (double)searcher-1.0 )-2.0);
		n = (int)(log( ((float)n + 0.5)*2.0 )/log(3.0));
		break;
	case METHOD_NODE_SEARCH:
		n = (int)(9.0*pow(2.0, (double)searcher-2.0)-2.0);
		n = (int)(log( ((double)n + 0.5 )/2.5 )/log(3.0))+2;
		if ( n > searcher ) n = searcher;
		break;
	 default: break;
	}

	more_searcher = searcher-n;

	if ( more_searcher != 0 )
	{
		sprintf( (char *)dummy, "Result may differ up to %d searchers", more_searcher );
		COnotifyInfo( dummy );
	}
	else
	{
		COnotifyInfo( "Result is exact" );
	}
}

/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/

void	AppTreeInitAndTest(Sgraph graph, Method method)
{
        Snode   node;
        Sedge   edge;

        AppTreeNodeType    node_init;
        EdgeState       edgestate;
        NodeState       nodestate;
        int     deg;

	tree_structure_initialized = FALSE;
                        
	AppTreeTesting( graph );
	IfErrorReturn;

	tree_structure_initialized = TRUE; 


 
        for_all_nodes( graph, node )
        {
                deg = 0;
                /* computes the degree of the node `node` */
                for_sourcelist( node, edge )
                {
                        edgestate = (EdgeState)attr_data( edge );
                         
                        /* only for computing the strategy */
                        set_edgestateattrs( edgestate, make_attr( ATTR_FLAGS, EDGE_NOT_CLEAR ) );
                        set_edgeattrs( edge, make_attr( ATTR_DATA, edgestate ));
                        deg++;
                } end_for_sourcelist( node, edge )
 
                nodestate = (NodeState)attr_data( node );

                node_init = (AppTreeNodeType)malloc( sizeof( RecAppTreeNodeType ) );
                if ( node_init == (AppTreeNodeType)NULL )
                {
                        SetErrorAndReturnVoid( IERROR_NO_MEM );
                }
                node_init->degree = deg; /* = nodestate->degree; */
                node_init->blocked = 0;
                node_init->first = (Fifo)NULL;
                node_init->last = (Fifo)NULL;
                set_nodestateattrs( nodestate, make_attr( ATTR_DATA, (char *)node_init ) );
                set_nodeattrs( node, make_attr( ATTR_DATA, nodestate ));


                /* inserting the leaves into the list 'deg1list' */
                if ( deg == 1 )
                {
                        deg1list = add_to_slist( deg1list, make_attr( ATTR_DATA,(char *)node ));
                }
        } end_for_all_nodes( graph, node );

        if ( deg1list == (Slist)NULL )
        {
                node = first_node_in_graph( graph );
                deg1list = add_to_slist( deg1list, make_attr( ATTR_DATA,(char *)node ));
        }



}

void	AppTreeSearch(Sgraph graph, Method method)
{


        Slist   list = (Slist)0;
        Slist   helplist = (Slist)0, elt;

        int searcher = 0;


        NodeState       nextnodestate;
        EdgeState   edgestate;
        AppTreeNodeType nextnode_info;
        Snode   node, nextnode;
        Sedge   edge;

        bool is_leave = TRUE; /* only used for the node-search method */

        /* 'deg1list' is global, was initialized in AppTreeInitialize */
        while ( AppTreeTestMoreThanTwo( deg1list ) )    /* |deg1list| > 2 */
        {
                /* deg1list contains not the avenue */
                /* ==> one searcher more to clear the (searcher)-Branch */
                searcher++;

                /* list is now the working list */
                list = deg1list;
                /* deg1list saves the new nodes with degree 1 of
                   the next (searcher)-Branch for the next reduction */
                deg1list = (Slist)0;

                do /* while list not empty */
                {
                        /* proceed all elements in 'list', that are  nodes
                           with degree 1 and not blocked nodes, if we find
                           nodes that are members of a 1-Branch, we append
                           them into a new list 'helplist', because it isn't
                           allowed to change 'list' while traversing with
                           'for_slist' */
                        for_slist( list, elt )
                        {
                                /* get the node of data structure */
                                node = (Snode)attr_data( elt );
                
                                /* get the adjacent node 'nextnode',
                                   there is only one, that's why we can
                                   'break' the loop */
                                for_sourcelist( node, edge)
                                {
                                        edgestate = (EdgeState)attr_data( edge );
                                        if ( attr_flags( edgestate ) ==
                                                EDGE_NOT_CLEAR ) break;
                                        /* the first edge, and the only,
                                           which is not clear is incident
                                           to the next node */
                                } end_for_sourcelist( node, edge)
                
                                /* set this edge cleared to avoid further use */
                                set_edgestateattrs( edgestate,
                                        make_attr( ATTR_FLAGS, EDGE_CLEAR ) );
                                set_edgeattrs( edge,
                                        make_attr( ATTR_DATA, edgestate ) );
 
                                /* get the adjacent node 'nextnode'  */
                                nextnode = iif(
                                                /* cond */ edge->snode == node,
                                                /* then */ edge->tnode,
                                                /* else */ edge->snode
                                                  );
 
                                /*  get the node information */
                                nextnodestate = (NodeState)attr_data(nextnode );
                                nextnode_info = (AppTreeNodeType)attr_data( nextnodestate );
 
                                /* reduce the degree of the node,
                                   because 'edge'is cleared */
                                nextnode_info->degree = nextnode_info->degree-1;
                
                                /* append 'node' into 'nextnode's list */
                                nextnode_info->last =
                                        FifoAppend( nextnode_info->last, node );
                                IfErrorReturn;
                                /* save the beginning of the fifo-list */
                                if ( nextnode_info->first == (Fifo)NULL )
                                {
                                        nextnode_info->first =
                                                nextnode_info->last;
                                }
 
                                /* if the method is the node-search method,
                                   then remove the (real) leaves of the tree
                                   and not the whole 1-Branches
                                */

                                if ( method == METHOD_NODE_SEARCH && is_leave )
                                {
                                        if ( nextnode_info->degree == 1 )
                                        {
                                                deg1list = add_to_slist( deg1list,
                                                make_attr( ATTR_DATA, (char *)nextnode ));
                                        }
                                }
                                else if ( nextnode_info->degree >= 2 )
                                {
                                        /* block the node of the higher branch
                                           to prevent deletion during this
                                           reduction -- we cannot distinguish
                                           among a node of 2-Branch and the
                                           last node of a the last 1-Branch
                                           ( of this 1-Branch ), both will
                                           have degree 2, so we block the
                                           node of the 2-Branch */
                                        nextnode_info->blocked = searcher;
                                }
                                else if ( nextnode_info->blocked == searcher )
                                {
                                        /* the last 1-Branch of the 2-Branch
                                           with 'nextnode' as a leaf was
                                           deleted -- we have found a new
                                           1-Branch of the reduced tree */
                                        deg1list =
                                        add_to_slist( deg1list,
                                           make_attr( ATTR_DATA, (char *)nextnode ));
                                }
                                else
                                {   
                                        /* we have found a node of the 1-Branch,
                                           save it for the next loop in THIS
                                           reduction */
                                        helplist = add_to_slist( helplist, make_attr( ATTR_DATA, (char *)nextnode ));
                                }

                                /* save the changes to node
                                   ( see degree/blocked ) */
                                set_nodestateattrs( nextnodestate,
                                 make_attr( ATTR_DATA, (char *)nextnode_info ) );
                                set_nodeattrs( nextnode,
                                 make_attr( ATTR_DATA, (char *)nextnodestate ) );

                        } end_for_slist( list, elt )
                        /* delete the 'list', only the pointers of the list-
                           structure, but NOT the elements ( nodes ) */
                        free_slist( list );
                        /* reduce the rest of the 1-Branches (same reduction!)*/
                        list = helplist; helplist = (Slist)0;

                        /* node-search, only a different for the (real) leaves*/
                        is_leave = FALSE;

                } while ( list != (Slist)0 ); /* some "rest 1-Branches" exist */
        } /*  |deg1list| > 2 */


        /* we have filled the fifo-structure of every node, except the leaves
           which makes no sense, and the not-end nodes of the avenue, i.e.
           we didn't insert the avenue nodes to the avenue fifo-structure,
           so we have to insert the node that follows a node of the avenue in
           the strategy */
        if ( AppTreeTestExactlyTwo( deg1list ) )   /* |deg1list| == 2 */
        {
                /* the avenue has more than one node */

                bool lastfind; /* TRUE if the other end-node of the avenue
                                  is reached */

                node = (Snode)attr_data( deg1list );
                do
                { 
                        lastfind = TRUE;

                        /* get the adjacent node 'nextnode',there is only one */
                        for_sourcelist( node, edge)
                        {
                                edgestate = (EdgeState)attr_data( edge );
                                if ( attr_flags( edgestate ) == EDGE_NOT_CLEAR )
                                {
                                        /* this wasn't the last node of the
                                           avenue */
                                        lastfind = FALSE;
                                        break;
                                }
                                /* the first edge, and the only, which is
                                   not clear is incident to the next node
                                   of th avenue */
                        } end_for_sourcelist( node, edge)
                        if ( !lastfind )
                        {
                                /* set this edge cleared to avoid further use */
                                set_edgestateattrs( edgestate, make_attr( ATTR_FLAGS, EDGE_CLEAR ) );
                                set_edgeattrs( edge, make_attr( ATTR_DATA, edgestate ) );

                                /* get the adjacent node 'nextnode'  */
                                nextnode = (edge->snode == node)?(edge->tnode):(edge->snode);
                                nextnodestate = (NodeState)attr_data(nextnode );
                                nextnode_info = (AppTreeNodeType)attr_data( nextnodestate );

                                /* append 'node' into 'nextnode's list */
                                nextnode_info->last = FifoAppend( nextnode_info->last, node );
                                IfErrorReturn;
                                /* save the beginning of the fifo-list */
                                if ( nextnode_info->first == (Fifo)NULL )
                                {
                                        nextnode_info->first = nextnode_info->last;
                                }
                        }
                        node = nextnode;

                } while ( !lastfind ); /* we still search for other nodes of
                                          the avenue */



        }
        else node = (Snode)attr_data( deg1list ); /* an end node of the avenue */

	searcher++; /* the result, but not exact */

        if ( COanimate() )
        {
                SetOn( node );
                AppTreeProcSearch( node, method );
        }
        else
        {
                /* save the time for the recursive search, the
                   user only wants to know the number of searchers */
                COsetMaxSearchers( searcher );
                COsetHiddenMaxSearchers( searcher );
                /* write it on the screen */
        }

	/* message to the user */
	AppTreeWriteInfo( searcher );




}

void	AppTreeFree(Sgraph graph, Method method)
{

        Snode   node;

        AppTreeNodeType    nodeinfo;
        NodeState       nodestate;

	if ( tree_structure_initialized == FALSE )
		return;

        /* reset the `deg1list` for the next search */
        free_slist( deg1list );
        deg1list = (Slist)NULL;

        for_all_nodes( graph, node )
        {
		nodestate = (NodeState)attr_data( node );
                nodeinfo = (AppTreeNodeType)attr_data( nodestate );
                if ( nodeinfo == (AppTreeNodeType)NULL )
                {
                        SetErrorAndReturnVoid( IERROR_FREE_NULL_PTR );
                }
                free( nodeinfo );

        } end_for_all_nodes( graph, node );

}

/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
