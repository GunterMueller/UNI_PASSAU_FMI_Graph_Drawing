#ifdef UNIX5
#ifndef lint
static char version_id[] = "%Z% File : %M% %E%  %U%  Version %I% Copyright (C) 1992/93 Schweikardt Andreas";
#endif
#endif

/*******************************************************************************
*                                                                              *
*                                                                              *
*                       SEARCH STRATEGIES ON GRAPHS                            * 
*                                                                              *
*                                                                              *
*       Copyright (C) 1992/93 Andreas Schweikardt                              * 
********************************************************************************




        File    :       %M%

        Date    :       %E%     (%U%)

        Version :       %I%

        Author  :       Schweikardt, Andreas



Portability

        Language                :       C
        Operating System        :       Sun-OS (UNIX)
        User Interface (graphic):
        Other                   :       GraphEd & Sgraph


********************************************************************************


Layer   :       Algorithm

Modul   :       Tree


********************************************************************************


Description of %M% :


This file is an implementation for search algorithms on trees. All three
algorithms computes optimal number of searchers used by each method and
determines an optimal searchplan for the animation.

There has been some difficulties: the three methods don't have the
same behaviour on trees, so for every method the algorithm must 
have been reinvented. But they are quite similar, but so similar
to use the same algorithm with some 'if ..'.

Method: edge search

The algorithm for the method of edge searching is described in:
N.Meggido, S.L.Hakimi, M.R.Garey & D.S.Johnson, C.H.Papadimitrio "The Complexity
of Searching a Graph" in Journal of the ACM, Vol. 35, No.1, 1988

I have tried to get close to the used terminology in this article.

Method: node search

This algorithm is described in:
Petra Scheffler "Die Baumweite von Graphen als ein Mass fuer die
Kompliziertheit algorithmischer Probleme" PhD-thesis Akademie der
Wissenschaften der DDR, 1989

The animation part is done by myself.

I have tried to get close to the used terminology in this article.

Method: mixed search

Its my own algorithm.


********************************************************************************


Functions of %M% :

TreeInitAndTest
TreeSearch
TreeFree




*******************************************************************************/


/******************************************************************************
*                                                                             *
*			standard includes				      *
*                                                                             *
*******************************************************************************/

#include <math.h>


/******************************************************************************
*                                                                             *
*			gui includes    	 			      *
*                                                                             *
*******************************************************************************/

/* -- none -- */

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

/* -- none -- */

/******************************************************************************
*                                                                             *
*			local macros  		 			      *
*                                                                             *
*******************************************************************************/

/* -- none -- */

/******************************************************************************
*                                                                             *
*			local types		 			      *
*                                                                             *
*******************************************************************************/


/******************************************************************************
*  for a detailed description of this structure(s) see
	N.Meggido et al. "The Complexity of Searching a graph",
	J. ACM Vol.35, No.1, 1988, pp 18-44 
*******************************************************************************/

/****************/
/* see page 24: */
/****************/
typedef enum {
	HUB,		/* type: H */
	ENDPOINT,	/*       E */
	INTERIOR,	/* 	 I */
	MIDDLE		/*	 M */
} RootTypeES;

/****************/
/* see page 28: */
/****************/
typedef enum {
	TIGHT,		/* label of the pointers */
	LOOSE,		/*			 */
	_TIGHT_,
	_LOOSE_
} PointerLabelES;

typedef struct Path {
	Snode		node;
	struct Path	*suc,
			*pre;	/* a duoble linked list */
} Path;

typedef struct TreeInfoES {
	/******************/
	/* see page 24-25 */
	/******************/
	RootTypeES		type;		/* type H, E, I or M */
	unsigned int		search_number;
	struct TreeInfoES		*info,		/* called M-info */

	/*******************************************************/
	/* see page 28-29, fasten the algorithm to linear time */
	/*******************************************************/
				/* 'info' is the first pointer described */
				*rev_pointer,	/* reverse pointer 2) */
				*closure,	/* third pointer */
				*endpoint;	/* 4) */
	PointerLabelES		label,		/* tight or loose of 'info'*/
				clabel;		/* closure label */

	/*************************************************/
	/* see page 31, to keep track of the search plan */
	/*************************************************/
	Path			*path;

} TreeInfoES;

/******************************************************************************
   for a detailed description of this structure(s) see
	Petra Scheffler "Die Baumweite von Graphen als ein Mass fuer die
	Kompliziertheit algorithmischer Probleme" PhD-thesis Akademie der
	Wissenschaften der DDR, 1989
******************************************************************************/

typedef struct TreeInfoNS {
	/******************/
	/* see page 47-56 */
	/******************/
	unsigned int		p1,
				p2,
				p3;
	struct TreeInfoNS	*rest;
	Path			*path; 
} TreeInfoNS;

typedef struct Queue { 
	union {
		Snode		node; 
		struct Queue	*last; 
	} entry;

	struct Queue		*next;
} Queue;




/******************************************************************************
   for a deatiled description of these structures see
	Andreas Schweikardt "Suche von beweglichen Objekten auf Graphen"
	Diplomarbeit, Universitaet Passau, Germany, 1994
******************************************************************************/


/******************************************************************************
The algorithm is similar to P.Schefflers algorithm,
we have used same routines
******************************************************************************/
typedef struct {
	unsigned int	degree;		/* the current degree of a node */
	bool		marked;		/* is the node marked ? */
	Snode		father;		/* who is the father ? */

	TreeInfoNS	*info;	/* see P.Scheffler */

} TreeNode;




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

static Sgraph	local_graph = (Sgraph)NULL;
/* a local copy of the original graph */
static bool	animation;	/* for a faster access to the flag */

static Queue		*queue  = (Queue *)NULL;
static TreeInfoNS	*vectorNS = (TreeInfoNS *)NULL;
static TreeNode		*node_info_vector = (TreeNode *)NULL;

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
*                                                                             *
*		      E D G E  S E A R C H   R O U T I N E S		      *
*		     ========================================		      *
*                                                                             *
*                                                                             *
*                                                                             *
******************************************************************************/

static Path	*SinglePathES(Snode node)
{
	Path	*path;

	path = (Path *)malloc( sizeof( Path ) );
	path->node = node;
	path->pre = path;
	path->suc = path;

	return( path );
}

static Path	*AppendPathES(Path *path, Snode node)
{
	Path	*new;
	Snode	neighbour;


	if ( path->node == node || path->pre->node == node )
		return( path );

	for_all_adjacent_nodes( node, neighbour )
	{
		if ( path->node == neighbour || path->pre->node == neighbour )
			break; /* found it and save it */
	} end_for_all_adjacent_nodes( node, neighbour )

	new = (Path *)malloc( sizeof( Path ) );
	new->node = node;

	new->suc = path;
	new->pre = path->pre;
	new->pre->suc = new;
	path->pre = new;

	if ( neighbour == path->node )
		return( new );
	else
		return( path );

}
static Path	*ReversePath(Path *path)
{
	Path	*next,
		*start;
	/* reverse path, swap pre and suc */
	start = path;
	do
	{
		next = path->suc;
		path->suc = path->pre;
		path->pre = next;
		path = next;
	} while ( start != path ); 

	return( path );

}
static Path	*MergePathsES(Path *path1, Path *path2, Snode node)
{
	Path	*next,
		*start;

	if ( path1->node != node && path1->pre->node != node )
		path1 = AppendPathES( path1, node );

	if ( path2->node != node && path2->pre->node != node )
		path2 = AppendPathES( path2, node );

	/* both paths have know 'node', know check orientation of them */

	if ( path1->node == node )
	{
		if ( path2->node == node )
		{

			next = path2->suc;
			next->pre = path2->pre;
			path2->pre->suc = next;

			if ( path2 == next )
			{
				free( next );
				return( path1 );
			}
			start = path2;
			path2 = next;
			free( start );

			path2 = ReversePath( path2 );
			start = path2->suc; /* yeah, the former pre */


		}
		else
		{
			next = path2->pre;
			path2->pre = next->pre;
			next->pre->suc = path2;

			if ( path2 == next )
			{
				free( next );
				return( path1 );
			}
			free( next );

			/* 'path2' has already the right orientation */

			start = path2;
			path2 = path2->pre;


		}
		path2->suc = path1;
		path1->pre->suc = start;
		start->pre = path1->pre;
		path1->pre = path2;

	}
	else
	{
		if ( path2->node == node )
		{
			next = path2->suc;
			next->pre = path2->pre;
			path2->pre->suc = next;

			if ( path2 == next )
			{
				free( next );
				return( path1 );
			}
			start = path2;
			path2 = next;
			free( start );

			path1 = ReversePath( path1 );
			start = path1->suc;
		}
		else
		{
			next = path2->pre;
			path2->pre = next->pre;
			next->pre->suc = path2;

			if ( path2 == next )
			{
				free( next );
				return( path1 );
			}
			free( next );

			start = path1;
			path1 = path1->pre;
			/* 'path2' has already the right orientation */
		}
		path1->suc = path2;
		path2->pre->suc = start;
		start->pre = path2->pre;
		path2->pre = path1;

	}


	return( start );
}



static unsigned int	SplitDegree(Snode node)
{
	Sedge	dummy;
	unsigned int	deg = 0;

	for_sourcelist( node, dummy )
	{
		deg++;
	} end_for_sourcelist( node, dummy )

	return( deg );
}


static Snode	Neighbour(Snode node)
{
	Snode	neighbour;

	/* get the (single) adjacent node of 'node' */

	for_all_adjacent_nodes( node, neighbour )
	{ 
		break; 
		/*NOTREACHED*/
	} end_for_all_adjacent_nodes( node, neighbour );

	return( neighbour );
}



/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	Split

arguments   :	
   	type		name		description(-I/-O)
1.	Snode		mtree		original node-I
2.	*Snode		tree1		new node-O

return()    :	void

description :	splits a graph at node 'mtree' into two parts. Splitting
		the node into two new nodes. ...
		Well a picture is more worth than thousand words:

		old graph	   resulting graph

		\ | /		   \ | /
		 \|/		    \|/
		--o----	  ===>     --o    n----
		  |		     |
	
		'o' is the old node an 'n' the new one.


use	    :	

restrictions:	do not use on the original graph, only on a copy of a it

bugs	    :	none reported

*******************************************************************************/
static void	Split(Snode mtree, Snode *tree1)
{
	Sedge	edge,
		nedge;

/* insert a new node */
	*tree1 = make_node( local_graph, make_attr( ATTR_DATA, (char *)mtree ));

/* copy also the pointer to the real graph, used for animation */	
	(*tree1)->iso = mtree->iso;

/* just split the node into two nodes, the new one gets only a single edge */
	for_sourcelist( mtree, edge )
	{
		break;
		/*NOTREACHED*/
	} end_for_sourcelist( mtree, edge );

/* insert the new edge and remove the old edge */
	nedge = make_edge( *tree1, (edge->snode==mtree)?(edge->tnode):(edge->snode), make_attr( ATTR_DATA, attr_data( edge ) ) );
	remove_edge( edge );



}

static void	Unsplit(Snode mtree, Snode tree1)
{
	Sedge	edge,
		oedge;

/* tree1 can only have a single edge, see function 'Split' */
	oedge = tree1->slist; /* this method allowed ? */

	/* if not allowed then use this terrific loop :
		for_sourcelist( tree1, oedge )
		{
			break;
		} end_for_sourcelist( tree1, oedge );
	*/

/* insert the removed edge from function 'Split' */
	edge = make_edge( mtree, (oedge->snode==tree1)?(oedge->tnode):(oedge->snode), make_attr( ATTR_DATA, attr_data( oedge ) ) );

/* and delete the (in 'Split') inserted edge and node */
	remove_edge( oedge );
	remove_node( tree1 );
}

static void	InfoFreeES(TreeInfoES *info)
{
	if ( info->info != (TreeInfoES *)NULL )
	{
		/* move down the list */
		InfoFreeES( info->info );
		free( info );
	}

}

static TreeInfoES	MergeES(TreeInfoES info1, TreeInfoES info2, Snode root, int first)
{
	TreeInfoES	newinfo,
			hinfo,
			*hinfo2;
	RootTypeES	t1 = info1.type,
			t2 = info2.type;


	if ( info1.search_number == info2.search_number )
	{
		if ( t1 == HUB && t2 == HUB )
		{
/**********/
/* Case 1 */
/**********/
			newinfo.type = HUB;
			newinfo.search_number = info1.search_number;
			newinfo.info = (TreeInfoES *)NULL;
			if ( animation  && first )
			{
				newinfo.path = SinglePathES( root->iso );
				IfErrorReturn( newinfo );
			}	
		}
		else if ( ( t1 == HUB && t2 == ENDPOINT ) ||
			  ( t2 == HUB && t1 == ENDPOINT ) )
		{
/**********/
/* Case 2 */
/**********/
			newinfo.type = ENDPOINT;
			newinfo.search_number = info1.search_number;
			newinfo.info = (TreeInfoES *)NULL;
			if ( animation  && first )
			{
				if ( t1 == HUB )
				{
					newinfo.path = AppendPathES( info2.path, root->iso );
					IfErrorReturn( newinfo );
				}
				else
				{
					newinfo.path = AppendPathES( info1.path, root->iso );
					IfErrorReturn( newinfo );
				}
			}
		}
		else if ( t1 == ENDPOINT && t2 == ENDPOINT )
		{
/**********/
/* Case 3 */
/**********/
			newinfo.type = INTERIOR;
			newinfo.search_number = info1.search_number;
			newinfo.info = (TreeInfoES *)NULL;
			if ( animation  && first )
			{
				newinfo.path = MergePathsES( info1.path, info2.path, root->iso );
				IfErrorReturn( newinfo );
			}
		}
		else if ( ( t1 == HUB && t2 == INTERIOR ) ||
			  ( t2 == HUB && t1 == INTERIOR ) )
		{
/**********/
/* Case 4 */
/**********/
			newinfo.type = INTERIOR;
			newinfo.search_number = info1.search_number;
			newinfo.info = (TreeInfoES *)NULL;
			if ( animation  && first )
			{
				if ( t1 == INTERIOR )
				{
					newinfo.path = info1.path;
				}
				else /* 't2' is the 'HUB' */
				{
					newinfo.path = info2.path;
				}
			}
		}
		else if ( ( t1 == INTERIOR && t2 == INTERIOR ) ||
			  ( t1 == INTERIOR && t2 == ENDPOINT ) ||
			  ( t2 == INTERIOR && t1 == ENDPOINT ) ||
		          t1 == MIDDLE || 
			  t2 == MIDDLE 
			  )
		{
/**********/
/* Case 5 */
/**********/
			newinfo.type = HUB;
			newinfo.search_number = info1.search_number+1;
			newinfo.info = (TreeInfoES *)NULL;
			if ( t1 == MIDDLE )
				InfoFreeES( info1.info );
			if ( t2 == MIDDLE )
				InfoFreeES( info2.info );
			if ( animation  && first )
			{
				newinfo.path = SinglePathES( root->iso );
				IfErrorReturn( newinfo );
			}
		}
	}
	else if ( info1.search_number > info2.search_number )
	{
/************************/
/* Case 6 and 7 (s1>s2) */ 
/************************/
		if ( t1 != MIDDLE )
		{
/******************/
/* Case 6 (s1>s2) */
/******************/
			newinfo = info1;
			if ( animation  && first )
			{
				if ( t1 == ENDPOINT )
				{
					newinfo.path = AppendPathES( info1.path, root->iso );
					IfErrorReturn( newinfo );
				}
				else
				{
					newinfo.path = info1.path;
				}
			}
		}
		else
		{
/******************/
/* Case 7 (s1>s2) */
/******************/
			hinfo2 = info1.info;
			hinfo = MergeES( *hinfo2, info2, root, FALSE );
			IfErrorReturn( *hinfo2 );
			if ( hinfo.search_number < info1.search_number )
			{
				newinfo.type = MIDDLE;
				newinfo.search_number = info1.search_number;
				newinfo.info = (TreeInfoES *)malloc( sizeof( TreeInfoES ) );
				if ( newinfo.info == (TreeInfoES *)NULL )
				{
					SetErrorAndReturn( IERROR_NO_MEM, newinfo );
				}
				newinfo.info->type = hinfo.type;
				newinfo.info->search_number = hinfo.search_number;
				newinfo.info->info = hinfo.info;
				if ( animation  && first )
				{
					newinfo.info->path = hinfo.path;
					newinfo.path = info1.path;
				}
			}
			else	/* s' = s1 (hinfo.sn == info1.sn) */
			{
				newinfo.type = HUB;
				newinfo.search_number = info1.search_number +1;
				newinfo.info = (TreeInfoES *)NULL;
				if ( t1 == MIDDLE )
					InfoFreeES( info1.info );
				if ( t2 == MIDDLE )
					InfoFreeES( info2.info );
				if ( animation  && first )
				{
					newinfo.path = SinglePathES( root->iso );
					IfErrorReturn( newinfo );
				}
			}
		}
	}
	else
	{
/************************/
/* Case 6 and 7 (s1<s2) */
/************************/
		if ( t2 != MIDDLE )
		{
/******************/
/* Case 6 (s1<s2) */
/******************/
			newinfo = info2;
			if ( animation  && first )
			{
				if ( t2 == ENDPOINT )
				{
					newinfo.path = AppendPathES( info2.path, root->iso );
					IfErrorReturn( newinfo );
				}
				else
				{
					newinfo.path = info2.path;
				}
			}
		}
		else
		{
/******************/
/* Case 7 (s1<s2) */
/******************/
			hinfo2 = info2.info;
			hinfo = MergeES( *hinfo2, info1, root, FALSE );
			IfErrorReturn( *hinfo2 );
			if ( hinfo.search_number < info2.search_number )
			{
				newinfo.type = MIDDLE;
				newinfo.search_number = info2.search_number;
				newinfo.info = (TreeInfoES *)malloc( sizeof( TreeInfoES ) );
				if ( newinfo.info == (TreeInfoES *)NULL )
				{
					SetErrorAndReturn( IERROR_NO_MEM, newinfo );
				}
				newinfo.info->type = hinfo.type;
				newinfo.info->search_number = hinfo.search_number;
				newinfo.info->info = hinfo.info;
				if ( animation  && first )
				{
					newinfo.info->path = hinfo.path;
					newinfo.path = info2.path;
				}
			}
			else
			{
				newinfo.type = HUB;
				newinfo.search_number = info2.search_number +1;
				newinfo.info = (TreeInfoES *)NULL;
				if ( t1 == MIDDLE )
					InfoFreeES( info1.info );
				if ( t2 == MIDDLE )
					InfoFreeES( info2.info );
				if ( animation  && first )
				{
					newinfo.path = SinglePathES( root->iso );
					IfErrorReturn( newinfo );
				}
			}
		}
	}


	return( newinfo );
}

static TreeInfoES	*RerootES(TreeInfoES *info)
{

	switch( info->type )
	{
	case HUB:
		info->type = ENDPOINT;
		break;
	case ENDPOINT:
		break;
	case INTERIOR:
		if ( info->search_number == 1 )
		{
			info->type = ENDPOINT;
		}
		else
		{
			info->type = MIDDLE;
			info->info = (TreeInfoES *)malloc( sizeof( TreeInfoES ) );
			if ( info->info == (TreeInfoES *)NULL )
			{
				SetErrorAndReturn( IERROR_NO_MEM, (TreeInfoES *)0 );
			}
			info->info->type = ENDPOINT;
			info->info->search_number = 1;
			info->info->info = (TreeInfoES *)NULL;
		}
		break;
	case MIDDLE:
		info->info = RerootES( info->info );
		/*
		IfErrorReturn( info );  not used, implicit done
		*/
		break;
	}

	return( info );
}

static TreeInfoES	Compute_infoES(Snode mtree)
{
	TreeInfoES	minfo, *hinfo;
	Snode		tree1;
	TreeInfoES	info1,
			info2;
	Snode		neighbour;

	if ( SplitDegree( mtree ) == 1 )
	{
		neighbour = Neighbour( mtree );
		if ( SplitDegree( neighbour ) == 1 )
		{
			minfo.type = ENDPOINT;
			minfo.search_number = 1;
			minfo.info = (TreeInfoES *)NULL;
			minfo.path = (Path *)malloc( sizeof( Path ) );
			if ( minfo.path == (Path *)NULL )
			{
				SetErrorAndReturn( IERROR_NO_MEM, minfo );
			}
			minfo.path->node = mtree->iso;
			minfo.path->suc = (Path *)malloc( sizeof( Path ) );
			if ( minfo.path->suc == (Path *)NULL )
			{
				SetErrorAndReturn( IERROR_NO_MEM, minfo );
			}
			minfo.path->suc->node = neighbour->iso;
			minfo.path->pre = minfo.path->suc;
			minfo.path->suc->pre = minfo.path;
			minfo.path->suc->suc = minfo.path;
		}
		else
		{
			info1 = Compute_infoES( neighbour );
			IfErrorReturn( info1 );
			hinfo = RerootES( &info1 );
			IfErrorReturn( info1 );
			minfo = *hinfo;
		}
	}
	else
	{
		Split( mtree, &tree1 );
		info1 = Compute_infoES( mtree );
		IfErrorReturn( info1 );
		info2 = Compute_infoES( tree1 );
		IfErrorReturn( info1 );
		Unsplit( mtree, tree1 );
		minfo = MergeES( info1, info2, mtree, TRUE );
		/*
		IfErrorReturn( ...  implicit done 
		*/
	}

	return( minfo );
}




static void	SearchPlanForES(Snode node)
{
	TreeInfoES	info;
	Path		*path,
			*start;
	unsigned int	s;

	Snode		split,
			neighbour,
			set_node;

	info = Compute_infoES( node );
	IfErrorReturn;
	s = info.search_number;


	path = start = info.path;


	do
	{
		if ( StateNode( path->node ) == NODE_NOT_SET )
		{
			SetOn( path->node );
			for_all_adjacent_set_nodes( path->node, set_node )
			{
				MoveTo( path->node, set_node );
			} end_for_all_adjacent_set_nodes( path->node, set_node );
			if ( s != 1 ) 
			{
				bool again = 1;
				node = path->node->iso;	
				do
				{
					Split( node, &split );

					if ( SplitDegree( node ) == 1  &&
					     SplitDegree( split ) == 1 )
						again = 0;

					if ( SplitDegree( node ) == 1 )
					{
						neighbour = Neighbour( node );
						if ( path->suc->node != neighbour->iso &&
					     	     path->pre->node != neighbour->iso )
						{
							SearchPlanForES( node );
							IfErrorReturn;
						/* split has degree > 1, then
						   search the other branches */
						}
						node = split;
					}
					
					if ( SplitDegree( split ) == 1 )
					{
						neighbour = Neighbour( split);
						if ( path->suc->node != neighbour->iso &&
					     	     path->pre->node != neighbour->iso )
						{
							SearchPlanForES( split );
							IfErrorReturn;
						}
					}
				
				} while( again );
			}
		}
		path = path->suc;
	} while ( path != start );

}


/******************************************************************************
*                                                                             *
*                                                                             *
*                                                                             *
*		      N O D E  S E A R C H   R O U T I N E S		      *
*		     ========================================		      *
*                                                                             *
*                                                                             *
*                                                                             *
******************************************************************************/

#define NodeInfo( NODE )	((TreeNode *)(attr_data( NODE )))->info
#define NodeFather( NODE )      ((TreeNode *)(attr_data( NODE )))->father
#define NodeDegree( NODE )      ((TreeNode *)(attr_data( NODE )))->degree
#define NodeMarked( NODE )      ((TreeNode *)(attr_data( NODE )))->marked

static void	QueueAppend(Snode node)
{
	Queue	*cqueue,
		*new_q;



	cqueue = queue+NodeInfo( node )->p1-1;

	new_q = (Queue *)malloc( sizeof( Queue ) );
	new_q->entry.node = node;
	new_q->next = (Queue *)NULL;
	if ( cqueue->entry.last == (Queue *)NULL )
	{
		cqueue->next = new_q;
		cqueue->entry.last = new_q;
	}
	else
	{
		cqueue->entry.last->next = new_q;
		cqueue->entry.last = new_q;
	}

}

static Snode	QueueGet(void)
{
	Snode	node;
	Queue	*cq,
		*cqptr = queue;

	while ( cqptr->next == (Queue *)NULL )
		cqptr++;

	node = cqptr->next->entry.node;

	cq = cqptr->next;
	cqptr->next = cq->next;
	free( cq );

	if ( cqptr->next == (Queue *)NULL )
		cqptr->entry.last = (Queue *)NULL; 

	return( node );
}

static Path	*SinglePath(Snode node)
{
	Path	*path;

	path = (Path *)malloc( sizeof( Path ) );
	path->node = node;
	path->pre = path;
	path->suc = path;

	return( path );
}
static Path	*AppendPath(Path *path, Snode node)
{
	Path	*new;

	new = (Path *)malloc( sizeof( Path ) );
	new->node = node;
	path->pre->suc = new;
	new->suc = path;
	new->pre = path->pre;
	path->pre = new;

	return( path );
}
static Path	*MergePaths(Path *path1, Path *path2)
{
	Path	*next;

	
	path2 = ReversePath( path2 );

	path2 = path2->suc;

	path1->pre->suc = path2;
	path2->pre->suc = path1;
	next = path1->pre;
	path1->pre = path2->pre;
	path2->pre = next;

	return( path1 );
}




/*******************************************************************************
*									       *
*			local function					       *
*									       *
********************************************************************************

name        :	Zufuegen

arguments   :	
   	type		name		description(-I/-O)

return()    :	TreeInfoNS

description :

use	    :	

restrictions:	

bugs	    :	none reported

*******************************************************************************/

static TreeInfoNS	Zufuegen(Snode t, TreeInfoNS *p, Snode s, TreeInfoNS *q, int first)
{
	TreeInfoNS	n,
			r,
			*new;

/* p and q, see algorithm of P.Scheffler p.54
   the values are according to Lemma 2, p49:

   (i)   p3 = 0 ==> p2 = 0
   (iii) p3 = 2 ==> p2 = 1

*/
	if ( q->p1 > p->p1 )
	{

/************/
/* Fall (1) */
/************/
		if ( q->p2 == 0 )
		{
			n.p1 = q->p1;
			n.p2 = 0;
			n.p3 = 1;
			n.rest = (TreeInfoNS *)NULL;
			if ( animation && first )
				n.path = AppendPath( q->path, t );
		}
		else if ( q->p3 == 2 ) /* ==> 'q->p2' == 1 */
		{
			n.p1 = q->p1;
			n.p2 = 1;
			n.p3 = 1;
			if ( animation && first )
				n.path = AppendPath( q->path, t );
			new = (TreeInfoNS *)malloc( sizeof( TreeInfoNS ) );
			new->p1 = p->p1;
			new->p2 = p->p2;
			new->p3 = p->p3;
			new->rest = p->rest;
			n.rest = new;

		}
		else /* if ( q->p2 == 1 && q->p3 == 1 ) */
		{
			r = Zufuegen( t, p, s, q->rest, FALSE );
			if ( r.p1 == q->p1 )
			{
				n.p1 = q->p1+1;
				n.p2 = 0;
				n.p3 = 0;
				n.rest = (TreeInfoNS *)NULL;
				if ( animation && first )
					n.path = SinglePath( t );

			}
			else
			{
				n.p1 = q->p1;
				n.p2 = 1;
				n.p3 = 1;
				if ( animation && first )
					n.path = AppendPath( q->path, t );
				new = (TreeInfoNS *)malloc( sizeof( TreeInfoNS ) );
				new->p1 = r.p1;
				new->p2 = r.p2;
				new->p3 = r.p3;
				new->rest = r.rest;
				n.rest = new;
			}
		}

	}
	else if ( q->p1 == p->p1 )
	{
/************/
/* Fall (2) */
/************/
		if ( q->p2 == 1 || p->p2 == 1 )
		{
			n.p1 = q->p1+1;
			n.p2 = 0;
			n.p3 = 0;
			n.rest = (TreeInfoNS *)NULL;
			if ( animation && first )
				n.path = SinglePath( t );
		}
		else if ( q->p2 == 0 && p->p2 == 0 && p->p3 == 0 )
		{
			n.p1 = q->p1;
			n.p2 = 0;
			n.p3 = 1;
			n.rest = (TreeInfoNS *)NULL;
			if ( animation && first )
				n.path = AppendPath( q->path, t );
		}
		else /* if ( q->p2 == 0 && p->p2 == 0 && p->p3 == 1 ) */
		{
			n.p1 = q->p1;
			n.p2 = 1;
			n.p3 = 2;
			n.rest = (TreeInfoNS *)NULL;
			if ( animation && first )
				n.path = MergePaths( q->path, p->path );
		}
	}
	else
	{
/************/
/* Fall (3) */
/************/
		if ( p->p2 == 0 || p->p3 == 2 )
		{
			n.p1 = p->p1;
			n.p2 = p->p2;
			n.p3 = p->p3;
			n.rest = p->rest;
			if ( animation && first )
				n.path = p->path;
		}
		else /* if ( p->p2 == 1 && p->p3 == 1 )*/
		{
			r = Zufuegen( t, p->rest, s, q, FALSE );
			if ( r.p1 == p->p1 )
			{
				n.p1 = p->p1+1;
				n.p2 = 0;
				n.p3 = 0;
				n.rest = (TreeInfoNS *)NULL;
				if ( animation && first )
					n.path = SinglePath( t );
			}
			else
			{
				n.p1 = p->p1;
				n.p2 = p->p2;
				n.p3 = p->p3;
				if ( animation && first )
					n.path = p->path;
				new = (TreeInfoNS *)malloc( sizeof( TreeInfoNS ) );
				new->p1 = r.p1;
				new->p2 = r.p2;
				new->p3 = r.p3;
				new->rest = r.rest;
				n.rest = new;
			}
		}
	}

	return( n );
	
}

static void	CallZufuegen(Snode t, Snode s)
{
	TreeInfoNS	*p,
			*q,
			n;

	p = NodeInfo( t );
	q = NodeInfo( s );

	n = Zufuegen( t, p, s, q, TRUE );
	p->p1 = n.p1;
	p->p2 = n.p2;
	p->p3 = n.p3;
	p->rest = n.rest;
	p->path = n.path;

}

static void	Markiere(Snode node, int what)
{	
	NodeMarked( node ) = what;
}

static bool	NurEinenNichtMarkiertenNachbarn(Snode node)
{
	Snode		neighbour,
			not_marked_node;
	int		not_marked = 0;

	for_all_adjacent_nodes( node, neighbour )
	{
		if ( !NodeMarked( neighbour ) )
		{
			not_marked++;
			not_marked_node = neighbour;	
			if ( not_marked == 2 )
				return( FALSE );
		}
		
	} end_for_all_adjacent_nodes( node, neighbour );

	if ( not_marked == 1 )
	{
		NodeFather( node ) = not_marked_node;
		return( TRUE );
	}
	else
		return( FALSE );
}

static Snode	MinPathWidthNS(Sgraph graph)
{
	unsigned int	index = 0,
			N = COnodes();
	Snode		node,
			neighbour;


	for_all_nodes( graph, node )
	{
		Markiere( node, FALSE );
	} end_for_all_nodes( graph, node );


	for_all_nodes( graph, node )
	{
		if ( NodeDegree( node ) == 1 )
		{
		/* 'node' is a leaf */
			index++;
			if ( index < N )
				Markiere( node, TRUE );
			neighbour = Neighbour( node );
			NodeInfo( neighbour )->p1 = 1;
			NodeInfo( neighbour )->p2 = 0;
			NodeInfo( neighbour )->p3 = 0;
			NodeInfo( neighbour )->rest = (TreeInfoNS *)NULL;

			NodeInfo( neighbour )->path = SinglePath( neighbour );

			NodeFather( node ) = neighbour;

			if ( NurEinenNichtMarkiertenNachbarn( neighbour ) )
			{
				QueueAppend( neighbour );
			}
		}

	} end_for_all_nodes( graph, node );

	while ( index < N-1 )
	{
		node = QueueGet();
		index++;
		Markiere( node, TRUE );
		neighbour = NodeFather( node );

		CallZufuegen( neighbour, node );

		if ( NurEinenNichtMarkiertenNachbarn( neighbour ) )
		{
			QueueAppend( neighbour );
		}


	}


	return( QueueGet()  );
	
}

static int	InitializeMarkierungNS(Snode node, Snode father)
{
	Snode	adj_node;
	int	count_nodes = 1; /* we do mark always one node */

	NodeMarked( node ) = FALSE;
	NodeFather( node ) = (Snode)NULL;
	NodeInfo( node )->p1 = 0;
	NodeInfo( node )->p2 = 1;
	NodeInfo( node )->p3 = 2;
	NodeInfo( node )->rest = (TreeInfoNS *)NULL;
	for_all_adjacent_nodes( node, adj_node )
	{
		if ( adj_node != father )
		{
			count_nodes += InitializeMarkierungNS( adj_node, node );
		}
	} end_for_all_adjacent_nodes( node, adj_node )

	return( count_nodes );
}
static void	InitializeLeafsNS(Snode node, Snode father, unsigned int N, unsigned int *count_leafs)
{
	Snode	adj_node;

	for_all_adjacent_nodes( node, adj_node )
	{
		if ( adj_node != father )
		{
			if ( NodeDegree( adj_node ) == 1 )
			{
				(*count_leafs)++;
				if ( *count_leafs < N )
					Markiere( adj_node, TRUE );
				NodeFather( adj_node ) = node;
				NodeInfo( node )->p1 = 1;
				NodeInfo( node )->p2 = 0;
				NodeInfo( node )->p3 = 0;
				NodeInfo( node )->rest = (TreeInfoNS *)NULL;
				NodeInfo( node )->path = SinglePath( node );
				if ( NurEinenNichtMarkiertenNachbarn( node ) )
				{
					QueueAppend( node );
				}
			}
			else
			{
				InitializeLeafsNS( adj_node, node, N, count_leafs );
			}
		}
	} end_for_all_adjacent_nodes( node, adj_node );

	if ( father == (Snode)NULL )
	{
		Snode	neighbour;
		if ( NodeDegree( node ) == 1 )
		{
				(*count_leafs)++;
				if ( *count_leafs < N )
					Markiere( node, TRUE );
				neighbour = Neighbour( node );
				NodeFather( node ) = neighbour;
				NodeInfo( neighbour )->p1 = 1;
				NodeInfo( neighbour )->p2 = 0;
				NodeInfo( neighbour )->p3 = 0;
				NodeInfo( neighbour )->rest = (TreeInfoNS *)NULL;
				NodeInfo( neighbour )->path = SinglePath( neighbour );
				if ( NurEinenNichtMarkiertenNachbarn( neighbour ) )
				{
					QueueAppend( neighbour );
				}
		}
	}
	return;
}

static void	SearchPlanForNS(Snode first_node, Snode father)
{

	unsigned int 	N,
			index = 0;
	Snode		node,
			neighbour;
	Path		*start, 
			*next;
	Slist		list = (Slist)NULL,
			elt;

	int b;
	b = DegreeNode( first_node );
	if ( DegreeNode( first_node ) == 0 )
	{
		SetOn( first_node->iso );
		return;
	}
	N = InitializeMarkierungNS( first_node, father );
	InitializeLeafsNS( first_node, father, N, &index );


        while ( index < N-1 )
        {
                node = QueueGet();
                index++;
                Markiere( node, TRUE );
                neighbour = NodeFather( node );
 
                CallZufuegen( neighbour, node );
 
                if ( NurEinenNichtMarkiertenNachbarn( neighbour ) )
                {
                        QueueAppend( neighbour );
                }
 
 
        }

        node = QueueGet();

	start = next = NodeInfo( node )->path;

	do
	{
		node = next->node;
		SetOn( node->iso );
		for_all_adjacent_nodes( node, neighbour )
		{
			if ( neighbour != next->suc->node  &&
			     neighbour != next->pre->node )
			{
				NodeDegree( neighbour ) = NodeDegree( neighbour )-1;
				list = add_to_slist( list, make_attr( ATTR_DATA, (char *)neighbour ) );
			}
		} end_for_all_adjacent_nodes( node, neighbour );

		remove_node( node );

		for_slist( list, elt )
		{
			node = (Snode)attr_data( elt );
			SearchPlanForNS( node, (Snode)NULL );
		} end_for_slist( list, elt )
		free_slist( list );
		list = (Slist)NULL;

		next = next->suc;
	} while ( next != start );
	
	return;

}
/******************************************************************************
*                                                                             *
*                                                                             *
*                                                                             *
*		    M I X E D  S E A R C H   R O U T I N E S		      *
*		   ==========================================		      *
*                                                                             *
*                                                                             *
*                                                                             *
******************************************************************************/

static Snode	MixedTreeSearch(Sgraph graph)
{
        unsigned int    index = 0,
                        N = COnodes();
        Snode           node,
                        neighbour;


        for_all_nodes( graph, node )
        {
                Markiere( node, FALSE );
        } end_for_all_nodes( graph, node );


        for_all_nodes( graph, node )
        {
                if ( NodeDegree( node ) == 1 )
                {
                /* 'node' is a leaf */
			NodeFather( node ) = Neighbour( node );
			QueueAppend( node );
                }
 
        } end_for_all_nodes( graph, node );

	index = 0; 
        while ( index < N-1 )
        {
                node = QueueGet();
                index++;
                Markiere( node, TRUE );
                neighbour = NodeFather( node );
 
                CallZufuegen( neighbour, node );
                if ( NurEinenNichtMarkiertenNachbarn( neighbour ) )
                {
                        QueueAppend( neighbour );
                }
 
 
        }
 
 
        return( QueueGet()  );
         

}
static int      InitializeMarkierungMS(Snode node, Snode father)
{
        Snode   adj_node;
        int     count_nodes = 1; /* we do mark always one node */

        NodeMarked( node ) = FALSE;
        NodeFather( node ) = (Snode)NULL;
        NodeInfo( node )->p1 = 1;
        NodeInfo( node )->p2 = 0;
        NodeInfo( node )->p3 = 0;
        NodeInfo( node )->rest = (TreeInfoNS *)NULL;
        for_all_adjacent_nodes( node, adj_node )
        {
                if ( adj_node != father )
                {
                        count_nodes += InitializeMarkierungMS( adj_node, node );                }
        } end_for_all_adjacent_nodes( node, adj_node );

        return( count_nodes );
}
static void     InitializeLeafsMS(Snode node, Snode father)
{
        Snode   	adj_node;
	TreeInfoNS	*info;

        for_all_adjacent_nodes( node, adj_node )
        {
                if ( adj_node != father )
                {
                        if ( NodeDegree( adj_node ) == 1 )
                        {
                                NodeFather( adj_node ) = node;
				info = NodeInfo( adj_node );
				info->path = SinglePath( adj_node );
				
                                QueueAppend( adj_node );
                        }
                        else
                        {
                                InitializeLeafsMS( adj_node, node );
                        }
                }
        } end_for_all_adjacent_nodes( node, adj_node );
 
        if ( father == (Snode)NULL )
        {
                if ( NodeDegree( node ) == 1 )
                {
			
			NodeFather( node ) = Neighbour( node );
			info = NodeInfo( node );
			info->path = SinglePath( node );
			QueueAppend( node );
                }
        }
        return;
}

static void     SearchPlanForMS(Snode first_node, Snode father)
{
 
        unsigned int    N,
                        index = 0;
        Snode           node,
                        neighbour;
        Path            *start,
                        *next;
        Slist           list = (Slist)NULL,
                        elt;
 
        int b;   
        b = DegreeNode( first_node );
        if ( DegreeNode( first_node ) == 0 )
        {
                SetOn( first_node->iso );
                return; 
        }
 
        N = InitializeMarkierungMS( first_node, father );
        InitializeLeafsMS( first_node, father );
 
        while ( index < N-1 )
        {
                node = QueueGet();
                index++;
                Markiere( node, TRUE );
                neighbour = NodeFather( node );
 
                CallZufuegen( neighbour, node );
 
                if ( NurEinenNichtMarkiertenNachbarn( neighbour ) )
                {
                        QueueAppend( neighbour );
                }
 
 
        }
 
        node = QueueGet();
 
        start = next = NodeInfo( node )->path;
 
        do
        {
                node = next->node;
                SetOn( node->iso );
                for_all_adjacent_nodes( node, neighbour )
                {
                        if ( neighbour != next->suc->node  &&
                             neighbour != next->pre->node )
                        {
                                NodeDegree( neighbour ) = NodeDegree( neighbour
)-1;
                                list = add_to_slist( list, make_attr( ATTR_DATA, (char *)neighbour ) );
                        }
                } end_for_all_adjacent_nodes( node, neighbour );
 
                remove_node( node );
 
                for_slist( list, elt )
                {
                        node = (Snode)attr_data( elt );
                        SearchPlanForMS( node, (Snode)NULL );
                } end_for_slist( list, elt )
                free_slist( list );
                list = (Slist)NULL;

                next = next->suc;
        } while ( next != start );

        return;

}


/******************************************************************************
*                                                                             *
*                                                                             *
*                       global functions		                      *
*                                                                             *
*                                                                             *
******************************************************************************/

void	TreeInitAndTest(Sgraph graph, Method method)
{
	if ( method != METHOD_NODE_SEARCH )
	{
		if ( COnodes() != COedges()+1 )
		{
			SetError( ERROR_NOT_A_TREE );
			return; 
		}
	}
	else
	{
		Snode	node;
		Sedge	edge;
		int	num_edges = 0;

		for_all_nodes( graph, node )
		{
			for_sourcelist( node, edge )
			{
				num_edges++;
			} end_for_sourcelist( node, edge )
		} end_for_all_nodes( graph, node )
		num_edges /= 2;
		if ( COnodes() != num_edges+1 )
		{
			SetError( ERROR_NOT_A_TREE );
			return; 
		}
	}
	local_graph = MakeCopyOfGraph( graph );

	if ( method != METHOD_EDGE_SEARCH )
	{
		unsigned int 	max,
				index,
				N = COnodes();
		Queue		*current;
		Snode		node;
		TreeNode	*node_info;

		if ( method == METHOD_NODE_SEARCH )
		{
			if ( N < 7 )
			{
				if ( N == 1 )
					max = 1;
				else
					max = 2;
			 
			}
			else
				max = (int)(log( ((float)N + 0.5 )/2.5 )/log(3.0)) +2;
		}
		else
		{
			max = (int)(log( ((float)N + 0.5)*2.0 )/log(3.0));
		}
		vectorNS = (TreeInfoNS *)calloc( N, sizeof( TreeInfoNS ) );
		queue = (Queue *)calloc( max, sizeof( Queue ) );

		for( index = 0; index < max; index++ )
		{
			current = queue+index;
			current->entry.last = (Queue *)NULL;
			current->next = (Queue *)NULL;
		}
		node_info_vector = (TreeNode *)calloc( N, sizeof( TreeNode ) );



		index = 0;
		for_all_nodes( local_graph, node )
		{
		
			node_info = node_info_vector+index;
			node_info->degree = ((NodeState)attr_data( node->iso ))->degree;
			node_info->father = (Snode)NULL;
			node_info->marked = FALSE;
	

			if ( method == METHOD_NODE_SEARCH )
			{
				node_info->info = vectorNS+index;
				node_info->info->p1 = 0;
				node_info->info->p2 = 1;
				node_info->info->p3 = 2;
				node_info->info->rest = (TreeInfoNS *)NULL;
			}
			else
			{
				node_info->info = vectorNS+index;
				node_info->info->p1 = 1;
				node_info->info->p2 = 0;
				node_info->info->p3 = 0;
				node_info->info->rest = (TreeInfoNS *)NULL;
			}
			set_nodeattrs( node, make_attr( ATTR_DATA, (char *)node_info ) );

			index++;
		} end_for_all_nodes( local_graph, node );
		
			
	}
        
	
	return;

}

void	TreeSearch(Sgraph graph, Method method)
{
	unsigned int	searcher;
	TreeInfoES	infoES;

	animation = COanimation();

	

	if ( COnodes() == 1 )
	{
		/* easiest "algorithm" for one node ! */
		SetOn( first_node_in_graph( graph ) );
	}
	else if ( animation )
	{
		if ( method == METHOD_EDGE_SEARCH )
		{
			SearchPlanForES( first_node_in_graph( local_graph ) );
			IfErrorReturn;
		}
		else if ( method == METHOD_NODE_SEARCH )
		{
			SearchPlanForNS( first_node_in_graph( local_graph ), (Snode)NULL );
		}
		else
		{
			SearchPlanForMS( first_node_in_graph( local_graph ), (Snode)NULL );
		}


	}
	else
	{

		if ( method == METHOD_EDGE_SEARCH )
		{
			infoES = Compute_infoES( first_node_in_graph( local_graph ) );
			IfErrorReturn;
			searcher = infoES.search_number;

		}
		else if ( method == METHOD_NODE_SEARCH )
		{
			searcher = 1+NodeInfo( MinPathWidthNS( local_graph ) )->p1;
		}
		else /* method == METHOD_MIXED_SEARCH */
		{
			searcher =NodeInfo( MixedTreeSearch( local_graph ) )->p1;
		}
		COsetHiddenMaxSearchers( searcher );
		COsetMaxSearchers( searcher );
	}

}

void	TreeFree(Sgraph graph, Method method)
{
	if ( method != METHOD_EDGE_SEARCH )
	{
		if ( queue != (Queue *)NULL )
		{
		    free( queue );
		    queue = (Queue *)NULL;
		}
		if ( vectorNS != (TreeInfoNS *)NULL )
		{
			free( vectorNS );
			vectorNS = (TreeInfoNS *)NULL;
		}
		if ( node_info_vector != (TreeNode *)NULL )
		{
			free( node_info_vector );
			node_info_vector = (TreeNode *)NULL;
		}
	}
	if ( local_graph != (Sgraph)NULL )
	{	
		remove_graph( local_graph );
		local_graph = (Sgraph)NULL;
	}
}

/******************************************************************************
*                      [EOF] end of file %M%
******************************************************************************/
