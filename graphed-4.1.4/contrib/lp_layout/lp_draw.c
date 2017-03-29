#include "misc.h"
#include "graph.h"
#include "draw.h"
#include "type.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <X11/Xos.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>

#include "lp_general_functions.h"

#include "lp_attribute_init_and_clear.h"
#include "lp_1_pass.h"
#include "lp_2_pass.h"
#include "lp_3_pass.h"
#include "lp_4_pass.h"
#include "lp_5_pass.h"
#include "lp_6_pass.h"
#include "lp_7_pass.h"
#include "lp_draw.h"
#include "lp_subframe.h"

/****************************************************************/
/*								*/
/*	modul	lp_draw.c					*/
/*								*/
/****************************************************************/

/*****************************************************************
function:	lp_compute_hierarchie
Input:	Graph	graph; Tree_ref	tree

	Das Layout von Graph wurde neu berechnet. Jetzt wird, falls
	vom Benutzer gewuenscht die hierarchie mit eingezeichnet
	(in den SELBEN Graphen, da Graphed manchmal aus mir uner-
	findlichen Gruenden sonst ein neues Fenster aufmacht, des-
	halb geht auch der Algorithmus von Timo nicht).

Output:	---
****************************************************************/

void	lp_compute_hierarchie(Graph graph, tree_ref tree)
{
	tree_node_ref	tree_node 	= tree->tree_rec.node;
	tree_ref	first 		= tree_node->first_son;
	Node		new_node;

	if( tree_node->first_son && !tree_node->leaf )	/****** Wenn nicht immer gezeichnet werden muss ******/
	{						/****** ACHTUNG: Wenn leaf, dann nicht loeschen ******/
		if( tree_node->graph_iso )
		{
			erase_and_delete_node( tree_node->graph_iso );	/*** Loesche alten Knoten, wenn da ***/
			tree_node->graph_iso	= NULL;
		}
		if( LP_WIN.what_to_do_with_derivated_node )	/*** und erzeuge neuen wenn gewollt ***/
		{
			new_node = create_node( graph );
			node_set( 	new_node, 	DEFAULT_NODE_ATTRIBUTES, 					0 );
			node_set( 	new_node, 	NODE_COLOR, 	6,
	        	 				NODE_TYPE, 	find_nodetype ("#box"),
							NODE_POSITION,  (tree_node->x1 + tree_node->x2) / 2 + 16, 
								(tree_node->y1 + tree_node->y2) / 2 + 16, 
							NODE_SIZE, 	tree_node->w + 32, tree_node->h + 32,		0 );

			tree_node->graph_iso		= new_node;
			new_node->lp_node.tree_iso	= tree;
		}
	}

	while ( first != NULL )
	{
		if ( ( first->tree_rec_type == TREE_NODE ) && 
		     ( first->tree_rec.node->first_son != NULL ) )
		{
			/* if ( !first->tree_rec.node->leaf ) */
			lp_compute_hierarchie(  graph, first  );
		}
		first = first->next_brother;
	}
}

/****************************************************************
function:	compute_attributes
Input:	Graph graph

	Compute the attributes of the derivation net of graph
****************************************************************/

void	compute_attributes(Graph graph)
{

	current_size = graph->lp_graph.current_size;

	clear_attributes( graph->lp_graph.derivation_net );

	clear_lp_edgelines( graph );
	pass_1( graph->lp_graph.derivation_net );
	pass_2( graph->lp_graph.derivation_net );
	pass_3( graph->lp_graph.derivation_net );
	pass_4( graph->lp_graph.derivation_net );
	pass_5( graph->lp_graph.derivation_net );
	graph->lp_graph.derivation_net->tree_rec.node->x1 = 0;
	graph->lp_graph.derivation_net->tree_rec.node->x2 = graph->lp_graph.derivation_net->tree_rec.node->w;

	graph->lp_graph.derivation_net->tree_rec.node->y1 = 0;
	graph->lp_graph.derivation_net->tree_rec.node->y2 = graph->lp_graph.derivation_net->tree_rec.node->h;

	pass_6( graph->lp_graph.derivation_net );
	pass_7( graph ); 

	/****** Now show the hierarchie, if needed. Or remove, if neccessary ******/
	lp_compute_hierarchie( graph, graph->lp_graph.derivation_net );
}


