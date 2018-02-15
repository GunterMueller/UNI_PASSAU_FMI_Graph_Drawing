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

static Snode	*original_nodes;

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

Sgraph	SimplifyGraph(Sgraph graph, Method method)
{
	Sgraph  ngraph;
        Snode   node,
                nnode,
		*nodeptr;
        Sedge   edge,
		edge_iso;
	bool	multiple_edge;
	unsigned int	number_of_nodes = 0,
			number_of_edges = 0;

/******************************/
/* do not copy an empty graph */
/******************************/
        if ( graph == empty_sgraph )
                 return( empty_sgraph );
        if ( graph->nodes == empty_node )
                 return( empty_sgraph );

/*********************************/
/* create a new undirected graph */
/*********************************/
        ngraph = make_graph( make_attr( ATTR_FLAGS, 0 ) );

        ngraph->directed = FALSE;


/***************************/
/* now duplicate all nodes */
/***************************/
        for_all_nodes( graph, node )
        {
		number_of_nodes++;

		nnode = make_node( ngraph, make_attr( ATTR_DATA, (char *)node ));
	/*************************************/
	/* the copied node has to his origin */
	/*************************************/
		nnode->iso = node;
		node->iso = nnode;      /* only for copying the edges */
	} end_for_all_nodes( graph, node );
 
	original_nodes = (Snode *)calloc( number_of_nodes, sizeof( Snode ) );
	nodeptr = original_nodes;
/****************************************/
/* duplicate the edges of the old graph */
/****************************************/

	for_all_nodes( graph, node )
	{
		*nodeptr = node;
		nodeptr++;
		for_sourcelist( node, edge )
                {
                        if ( unique_edge( edge ) )
                        {
				number_of_edges++;
				if ( edge->snode == edge->tnode )
					continue;
				nnode = (edge->snode == node)?(edge->tnode->iso):(edge->snode->iso);
				multiple_edge = FALSE;
				for_sourcelist( node->iso, edge_iso )
				{
					if ( (edge_iso->tnode == edge->tnode->iso && edge_iso->snode == edge->snode->iso) || 
					 (edge_iso->tnode == edge->snode->iso && edge_iso->snode == edge->tnode->iso) )
					{
						multiple_edge = TRUE;
						break;
					}
				} end_for_sourcelist( node->iso, edge_iso )
				if ( !multiple_edge )
				{
                                	make_edge( edge->snode->iso, edge->tnode->iso, make_attr( ATTR_DATA, (char *)edge ) );
				}
                        }
                } end_for_sourcelist( node, edge );
        } end_for_all_nodes( graph, node );

/*********************************************************/
/* set (and display) the new information about the graph */
/*********************************************************/
	COsetNodes( number_of_nodes );
	COsetNodesNotSet( number_of_nodes );
	COsetEdgesNotClear( number_of_edges );
	COsetEdges( number_of_edges );
	COsetMaxSteps( 2*number_of_nodes+number_of_edges );

	return( ngraph );
}

void	ComputeSimplifiedSearch(Sgraph graph)
{
	Snode	*nodelist,
		*nodeitem,
		node;
	int	index,
		N = COnodes();
	MVrecordResetTape();

	nodelist = (Snode *)calloc( N, sizeof( Snode ) );
	nodeitem = nodelist;

	while( ( *nodeitem = MVrecordGetNextSetNode() ) != empty_node )
		nodeitem++;

	MVrecordClear();
	COsetHiddenSearchers( 0 );
	COsetHiddenMaxSearchers( 0 );


	nodeitem = original_nodes;
	for_all_nodes( graph, node )
	{
		node->iso = *nodeitem;
		nodeitem++;
	} end_for_all_nodes( graph, node )
	nodeitem = nodelist;	
	for( index = 0; index < N; index++ )
	{
		node = *nodeitem;
		
		SetOn( node->iso );
		nodeitem++;
	}

	free( nodelist );
	free( original_nodes );

	return;
}



/******************************************************************************
*		       [EOF] end of file %M% 
******************************************************************************/
