#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "graphed_subwindows.h"
#include "user_header.h"

#include <sgraph/sgraph.h>
#include <sgraph/graphed.h>

#include "lp_general_functions.h"
#include "lp_subframe.h"

#include "lpa_create_struc.h"

/*****************************************************************************************
function	remove_dependency
Input:	tree_ref	father

	Loescht alle Dependency-Knoten im Graphen. Steht nur deswegen in diesem file,
	weil mir nicht eingefallen ist, wo es sonst besser hinpasst.
*****************************************************************************************/

void	remove_dependency(tree_ref father)
{
	tree_ref		cur;

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
	             ( cur->tree_rec.node->first_son != NULL ) && 
		     ( !cur->tree_rec.node->leaf ) ) remove_dependency( cur );
		cur = cur->next_brother;
	}

	if( father->tree_rec.node->first_son &&
	    father->tree_rec.node->graph_iso )
	{
		erase_and_delete_node( father->tree_rec.node->graph_iso );
		father->tree_rec.node->graph_iso = NULL;
	}
}

/*****************************************************************************************
function:		lpa_draw_sizes
Input:	LPA_Sizes min_sizes; int which_min_sizes, upper_left_x, upper_left_y
	Graph graph in den reingezeichnet wird

	Zeichnet min_sizes[which_min_sizes] relativ zu upper_left_x, upper_left_y
	und rekursiv Rest der Ableitung.
	ACHTUNG: Damit ich komfortabel den Graphen von den Dependencies aus zeichnen kann,
		 muss ich von tree_node aus, auf den x- und y-dependency gemeinsam zeigen,
		 auf den neu erzeugten Knoten zeigen.

Output:	---
*****************************************************************************************/

void	lpa_draw_sizes(LPA_Sizes min_sizes, int which_min_sizes, int upper_left_x, int upper_left_y, int upper_width, int upper_height, Graph graph)
{
	LPA_Dependency	cur_dep;			/*** Laufvariable ***/
	Node		new_node, cur_node;		/*** Neu erzeugter Knoten ***/
	int		set_at_x, set_at_y,		/*** Hilfsvariable ***/
			width, height;			/*** Hilfsvariable ***/
	char*		label;				/*** Hilfsvariable ***/
	int		min_x, min_y, max_x, max_y;

	cur_dep = min_sizes[which_min_sizes].x_dependency;

	min_x = 100000000;
	max_x = 0;

	while( cur_dep )
	{
		if ( cur_dep->prod_node )
		{
			if ( cur_dep->new_coord > max_x ) max_x = cur_dep->new_coord;
			if ( cur_dep->new_coord < min_x ) min_x = cur_dep->new_coord;
		}
		cur_dep = cur_dep->next;
	}
		
	cur_dep = min_sizes[which_min_sizes].y_dependency;

	min_y = 100000000;
	max_y = 0;

	while( cur_dep )
	{
		if ( cur_dep->prod_node )
		{
			if ( cur_dep->new_coord > max_y ) max_y = cur_dep->new_coord;
			if ( cur_dep->new_coord < min_y ) min_y = cur_dep->new_coord;
		}
		cur_dep = cur_dep->next;
	}
		

	upper_left_x = upper_left_x + ( ( upper_width - ( max_x - min_x ) ) / 2);
	upper_left_y = upper_left_y + ( ( upper_height - ( max_y - min_y ) ) / 2);
	
	cur_dep = min_sizes[which_min_sizes].x_dependency;

	while( cur_dep )
	{
		if( (cur_dep->side == RIGHT) && cur_dep->prod_node ) /****** Rechte Seite und Kein LHS_node ******/
		{
			width		= cur_dep->new_coord - cur_dep->first_border->new_coord;
			height		= UNDEFINED;
			set_at_x	= upper_left_x + cur_dep->new_coord - width / 2;
			set_at_y 	= UNDEFINED;

			if( cur_dep->tree_node->graph_iso )
			{
				new_node	= cur_dep->tree_node->graph_iso;
				label 		= mem_copy_string((char *)node_get(cur_dep->tree_node->graph_iso, NODE_LABEL));
			}
			else
			{
				new_node	= create_node(graph);
				label 		= mem_copy_string((char *)node_get(cur_dep->prod_node, NODE_LABEL));
			}

			node_set( new_node, ONLY_SET, DEFAULT_NODE_ATTRIBUTES, 0 );
			node_set( new_node, ONLY_SET, NODE_LABEL, label, 0 );
			node_set( new_node, ONLY_SET, NODE_POSITION, set_at_x, set_at_y, 0);
			node_set( new_node, ONLY_SET, NODE_SIZE, width, height, 0);
			cur_dep->tree_node->new_graph_node	= new_node;
			cur_dep->tree_node->graph_iso = new_node;
		}
		cur_dep = cur_dep->next;
	}

	cur_dep = min_sizes[which_min_sizes].y_dependency;

	while( cur_dep )
	{
		if( cur_dep->side == DOWN && cur_dep->prod_node ) /****** Rechte Seite und Kein LHS_node ******/
		{
			cur_node	= cur_dep->tree_node->new_graph_node;

			width		= (int)node_get( cur_node, NODE_WIDTH );
			height		= cur_dep->new_coord - cur_dep->first_border->new_coord;
			set_at_x	= (int)node_get( cur_node, NODE_X );
			set_at_y	= upper_left_y + cur_dep->new_coord - height / 2;

			/****** Jetzt muss unterschieden werden: Soll verkleinert( d.h. Groessen aus Produktion genommen ) werden ******/
			if( LP_WIN.min_nodes )
			{
				if( !cur_dep->derivation ) /****** Wenn nach unten abgeleitet dann muss gross bleiben ******/
				{
					width	= (int)node_get( cur_dep->prod_node, NODE_WIDTH );
					height	= (int)node_get( cur_dep->prod_node, NODE_HEIGHT );
				}
			}

			node_set( cur_node, ONLY_SET, NODE_POSITION, set_at_x, set_at_y, 0);
			node_set( cur_node, ONLY_SET, NODE_SIZE, width, height, 0);
			node_set( cur_node, ONLY_SET, NODE_NEI, NO_NODE_EDGE_INTERFACE, 0);

			if( cur_dep->derivation )
			{
				/****** Rekursiv nach unten weiterzeichnen ******/
				lpa_draw_sizes( cur_dep->derivation, cur_dep->der_nr, set_at_x - width / 2 , set_at_y - height / 2, width, height, graph );

				/****** Falls gesetzt ist, dass abgeleiteter Knoten NICHT gezeichnet wird loeschen ******/
				if( LP_WIN.what_to_do_with_derivated_node == 0 )
				{
					erase_and_delete_node( cur_node );
					cur_dep->tree_node->graph_iso = NULL;
				}
				else
				{
					/****** Bei abgeleiteten Knoten muss Nodelabel weg sein ******/
					node_set( cur_node, ONLY_SET, NODE_LABEL, "", 0 );
				}
			}
			else
			{
				/****** Es existiert im alten Graph Entsprechung ******/
				cur_dep->graph_node->lp_node.iso_in_area_opt = cur_node;
			}
		}
		cur_dep = cur_dep->next;
	}
}

/*****************************************************************************************
function:	lpa_draw_area_optimal_tree
Input:	tree_ref derivation_tree, Graph derivated_graph

	Alles ist ausgerechnet. Jetzt muss nur noch an der Wurzel (derivation_tree) das 
	Layout rausgesucht werden, das optimal ist. Dann kann neu gezeichnet werden.

	IDEE: 	Suche Optimum. Dann rufe eine rekursive Fkt. auf, die relativ zu 
		uebergebenen Koordinaten sich selbst (die uebergebene Sizes) und dann
		die Soehne zeichnet

Output:	NUR AM BILDSCHIRM
*****************************************************************************************/

void	lpa_draw_area_optimal_tree(tree_ref derivation_tree, Graph derivated_graph)
{
	int		min,				/*** Aktuelles Minimum ***/
			which_min_sizes,		/*** eintrag in array von sizes mit opt***/
			i, j,				/*** Laufvariable ***/
			upper_len;			/*** Hilfsvariable ***/
	LPA_Sizes	min_sizes,			/*** Wo steht aktuelles Minimum in sizes***/
			cur_sizes;			/*** Hilfsvariable ***/
	LPA_Upper_prod	upper;				/*** Hilfsvariable ***/
	Node		new_node;			/*** Neuer Knoten fuer Wurzel ***/
	Graph		graph;				/*** Graph in den gezeichnet wird ***/
	int		set_at_x, set_at_y,		/*** Hilfsvariable ***/
			width, height;			/*** Hilfsvariable ***/
	char*		label;				/*** Hilfsvariable ***/
	Node		cur_node;
	Edge		cur_edge;
	Edgeline	new_line;
	int		x1, x2, y1, y2;
	int		n1x, n1w, n2x, n2w;
	int		n1y, n1h, n2y, n2h;
	int		n1x1,n2x1,n1x2,n2x2;
	int		n1y1,n2y1,n1y2,n2y2;

	min		= UNDEFINED;	/*** -1 ***/


	graph = derivated_graph;

	/*** Nach reduce und delete kann auch gar kein Baum dasein ***/
	if( !derivation_tree->tree_rec.node->area_structures )
	{
		new_node = derivation_tree->tree_rec.node->graph_iso;
		node_set( new_node, ONLY_SET, NODE_POSITION, 32, 32, 0);
	}
	else
	{
		upper		= derivation_tree->tree_rec.node->area_structures->array_head;
		upper_len	= derivation_tree->tree_rec.node->area_structures->number;

		/****** Minimum raussuchen ******/
		for( i = 0; i < upper_len; i++ )
		{
			cur_sizes = upper[i].sizes;

			for( j = 0; j < upper[i].sizes_nr; j++ )
			{
				if( (min == UNDEFINED) || (min > cur_sizes[j].width * cur_sizes[j].height) )
				{
					/*** Neues Minimum eintragen ***/
					min		= cur_sizes[j].width * cur_sizes[j].height;
					min_sizes	= cur_sizes;
					which_min_sizes	= j;
				}
			}
		}

		width		= min_sizes[which_min_sizes].width;
		height		= min_sizes[which_min_sizes].height;
		set_at_x	= width / 2 + 16;
		set_at_y 	= height / 2 + 16;

		if( derivation_tree->tree_rec.node->graph_iso )
		{
			new_node	= derivation_tree->tree_rec.node->graph_iso;
			label 		= mem_copy_string((char *)node_get(derivation_tree->tree_rec.node->graph_iso, NODE_LABEL));
		}
		else
		{
			new_node	= create_node( graph );
			label 		= mem_copy_string((char *)node_get(derivation_tree->tree_rec.node->prod_iso, NODE_LABEL));
		}

		node_set( new_node, ONLY_SET, DEFAULT_NODE_ATTRIBUTES, 0 );
		node_set( new_node, ONLY_SET, NODE_LABEL, label, 0 );
		node_set( new_node, ONLY_SET, NODE_POSITION, set_at_x, set_at_y, 0);
		node_set( new_node, ONLY_SET, NODE_SIZE, width, height, 0);
		node_set( new_node, ONLY_SET, NODE_NLP, NODELABEL_UPPERLEFT, 0 );

		derivation_tree->tree_rec.node->new_graph_node	= new_node;
		derivation_tree->tree_rec.node->graph_iso	= new_node;


		if( LP_WIN.what_to_do_with_derivated_node != 0 )
		{
			node_set( new_node, ONLY_SET, NODE_LABEL, "", 0 );
		}
		else
		{
			/****** Falls einer da ist loeschen ******/
			if( derivation_tree->tree_rec.node->graph_iso )
			{
				erase_and_delete_node( derivation_tree->tree_rec.node->graph_iso );
				derivation_tree->tree_rec.node->graph_iso = NULL;
			}
		}

		/****** Das minimum haben wir, jetzt rekursive Fkt. die Zeichnet ******/
		lpa_draw_sizes( min_sizes, which_min_sizes, 16, 16 , width, height, graph);

		graph->lp_graph.derivation_net = derivation_tree;

		/****** Jetzt zeichne noch die Kanten (!!!Made by Timo!!!)******/
		for_nodes( graph, cur_node )
		{
			for_edge_sourcelist( cur_node, cur_edge )
			{
				n1x	= (int)node_get( cur_node, NODE_X );
				n1w	= (int)node_get( cur_node, NODE_WIDTH );
				n2x	= (int)node_get( cur_edge->target, NODE_X );
				n2w	= (int)node_get( cur_edge->target, NODE_WIDTH );

				n1y	= (int)node_get( cur_node, NODE_Y );
				n1h	= (int)node_get( cur_node, NODE_HEIGHT );
				n2y	= (int)node_get( cur_edge->target, NODE_Y );
				n2h	= (int)node_get( cur_edge->target, NODE_HEIGHT );

				n1x1 	= n1x - n1w /2;
				n1x2 	= n1x + n1w /2;

				n2x1 	= n2x - n2w /2;
				n2x2 	= n2x + n2w /2;

				n1y1 	= n1y - n1h /2;
				n1y2 	= n1y + n1h /2;

				n2y1 	= n2y - n2h /2;
				n2y2 	= n2y + n2h /2;

				if( n1x1 > n2x2 )
				{
					x1 = n1x1;
					x2 = n2x2;
				}
				else
				{
					if( n1x2 < n2x1 )
					{
						x1 = n1x2;
						x2 = n2x1;
					}
					else
					{
						x1 = n1x;
						x2 = n2x;
					}
				}

				if( n1y1 > n2y2 )
				{
					y1 = n1y1;
					y2 = n2y2;
				}
				else
				{
					if( n1y2 < n2y1 )
					{
						y1 = n1y2;
						y2 = n2y1;
					}
					else
					{
						y1 = n1y;
						y2 = n2y;
					}
				}

				new_line	= new_edgeline( x1, y1 );
				(void)add_to_edgeline( new_line, x2, y2 );

				edge_set( cur_edge, EDGE_LINE, new_line, 0 );
			}
			end_for_edge_sourcelist( cur_node, cur_edge );
		}
		end_for_nodes( graph, cur_node );
	}
	/* restore_graph( graph ); */
}



