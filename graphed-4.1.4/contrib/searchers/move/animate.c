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


Layer   : 	basics

Modul   :	animation


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

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include <sgraph/sgraph_interface.h>


/******************************************************************************
*                                                                             *
*			GraphEd & Sgraph includes			      *
*                                                                             *
*******************************************************************************/

#include <search/search.h>
#include <search/control.h>
#include <search/move.h>


/******************************************************************************
*                                                                             *
*			local includes		 			      *
*                                                                             *
*******************************************************************************/

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

typedef struct Dlist {
	int		x1, y1,
			x2, y2;
	struct Dlist	*suc, *pre;
} Dlist;

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

void	AnimateNodeNotSet(Snode node)
{
	node_set( graphed_node( node ),
		NODE_TYPE,	1,
		NULL );
	unlock_user_interface();
	COrepaint();
	if ( !COmanual() && !COplay() )
		lock_user_interface();
}

void	AnimateNodeSet(Snode node)
{
	node_set( graphed_node( node ),
		NODE_TYPE,	COgetSetNodeType(),
		NULL );

	unlock_user_interface();
	COrepaint();
	if ( !COmanual()  && !COplay())
		lock_user_interface();

}

void	AnimateNodeUnset(Snode node)
{
	node_set( graphed_node( node ),
		NODE_TYPE,	COgetUnsetNodeType(),
		NULL );

	unlock_user_interface();
	COrepaint();
	if ( !COmanual()  && !COplay())
		lock_user_interface();
}

void	AnimateEdgeClear(Sedge edge)
{
	edge_set( graphed_edge( edge ),
		EDGE_TYPE,	2,
		NULL );
	if ( COmethod() != METHOD_NODE_SEARCH )
		set_edgelabel( edge, "" );

	unlock_user_interface();
	COrepaint();
	if ( !COmanual() && !COplay() )
		lock_user_interface();
}

void	AnimateEdgeNotClear(Sedge edge)
{
	edge_set( graphed_edge( edge ),
		EDGE_TYPE,	0,
		NULL );
	if ( COmethod() != METHOD_NODE_SEARCH )
		set_edgelabel( edge, "" );

	unlock_user_interface();
	COrepaint();
	if ( !COmanual() && !COplay() )
		lock_user_interface();
}

void	AnimateEdgeClearing(Sedge edge, Snode source_node)
{

/* Bresenham-Algorithmus: von Michael Himsolt */
	Snode	tnode,
		snode;

	Snode	moving;
	Graphed_node	gnode;

	Edgeline	edgeline,
			el;

	int		ind1, ind2,
			dep1, dep2,
			d_ind, d_dep;
	int		x1,y1,x2,y2;
	int		x,y;
	bool		first;
	int		x_is_ind;
	int		incr_only_ind,
			incr_ind_and_dep,
			d,
			ind,
			dep,
			ind_end,
			check_d_smaller_zero,
			incr_ind,
			incr_dep;
	Dlist		*dlist = (Dlist *)NULL,
			*new,
			*start;	
	bool		reverse;

	unlock_user_interface();
	snode = edge->tnode;
	tnode = edge->snode;

	edgeline = (Edgeline)edge_get( graphed_edge( edge ), EDGE_LINE );


	moving = make_node( COgetGraph(), make_attr( ATTR_FLAGS, 0 ) );
	moving->x = edgeline_x( edgeline );
	moving->y = edgeline_y( edgeline );
	gnode = create_graphed_node_from_snode( moving );
	node_set( gnode, NODE_TYPE, COgetMoveNodeType(),
		NODE_SIZE, 16, 16, NULL );



	first = TRUE;	
	for_edgeline( edgeline, el )
	{
		if ( first )
		{
			first = FALSE;
			x2 = edgeline_x( el );
			y2 = edgeline_y( el );
		}
		else
		{
			x1 = x2; y1 = y2;
			x2 = edgeline_x( el );
			y2 = edgeline_y( el );
			new = (Dlist *)malloc( sizeof( Dlist ) );
			
			if ( dlist == (Dlist *)NULL )
			{
				new->pre = new->suc = new;
				dlist = new;
			}
			else
			{
				dlist->pre->suc = new;
				new->pre = dlist->pre;
				new->suc = dlist;
				dlist->pre = new;
			}
			new->x1 = x1;
			new->x2 = x2;
			new->y1 = y1;
			new->y2 = y2;
		}
	} end_for_edgeline( edgeline, el );

	if ( source_node != sedge_real_source( edge ) )
		reverse = TRUE;
	else
		reverse = FALSE;

	

	if ( reverse )
	{
		start = dlist->pre;
		dlist = dlist->pre;
	}
	else
		start = dlist;
	do
	{
		if ( reverse )
		{
			x1 = dlist->x2;
			x2 = dlist->x1;
			y1 = dlist->y2;
			y2 = dlist->y1;
		}
		else
		{
			x1 = dlist->x1;
			x2 = dlist->x2;
			y1 = dlist->y1;
			y2 = dlist->y2;
		}

		if (abs(x2 - x1) > abs(y2 - y1)) {
			x_is_ind = TRUE;
			ind1 = x1; dep1 = y1;
			ind2 = x2;  dep2 = y2;
		} else {
			x_is_ind = FALSE;
			ind1 = y1; dep1 = x1;
			ind2 = y2;  dep2 = x2;
		}
		 
		d_ind = ind2 - ind1;
		d_dep = dep2 - dep1;
		incr_only_ind    = iif (d_ind > 0, 2*d_dep, - 2*d_dep);
		incr_ind_and_dep = incr_only_ind - iif (d_dep > 0, 2*d_ind, -2*d_ind);
		d = (incr_only_ind + incr_ind_and_dep) / 2;
		ind = ind1;
		dep = dep1;
		ind_end = ind2;
		incr_ind = iif (d_ind >= 0, 1, -1);
		incr_dep = iif (d_dep >= 0, 1, -1);
		check_d_smaller_zero = (d_ind > 0  && d_dep > 0) ||
				       (d_ind < 0  && d_dep < 0) ||
				       (d_dep == 0 && d_ind < 0);
		
		while (ind != ind_end) {
			if ( iif (check_d_smaller_zero, d<0, d>0) ) {
				d += incr_only_ind;
			} else {
				d += incr_ind_and_dep;
				dep += incr_dep;
			}
			ind += incr_ind;
			if ( x_is_ind )
			{
				x = ind;
				y = dep;
			}
			else
			{
				x = dep;
				y = ind;
			}
			node_set( graphed_node( moving ), NODE_POSITION, x, y, NULL );
			force_repainting();
		 
		}

		if ( reverse )
		{
			dlist = dlist->pre;
		}
		else
		{
			dlist = dlist->suc;
		}

		/*
		COrepaint();
		*/
	}
	while ( dlist != start );

	remove_node( moving );
	edge_set( graphed_edge( edge ),
		EDGE_TYPE,	2,
		NULL );
	COrepaint();
	if ( !COmanual() && !COplay() )
		lock_user_interface();
}




/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
