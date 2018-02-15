#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lpp_create_attribute_structures.h"
#include "lpp_graph_functions.h"

#include "lpp_table_to_tree.h"
#include "lpp_clear_table.h"
#include "lp_free_tree.h"

#include "lpp_1_pass.h"
#include "lpp_2_pass.h"

#include "lpp_redraw.h"

#include "lp_free_tree.h"
#include "lp_attribute_init_and_clear.h"
#include "lp_general_functions.h"

#include "lpp_1_pass_layout.h"
#include "lpp_parse.h"

/************************************************************************/
/*									*/
/*	Hauptprogramm fuer parsing					*/
/*									*/
/************************************************************************/

/*************************************************************************
function	derivation_by_parsing

17.2 Angepasst an subframe
*************************************************************************/

void	derivation_by_parsing(void)
{
	Graph			graph;
	tree_ref		my_tree;
	Node			new_node;
	char* 			new_label;
	lpp_Parsing_element	derivation_table;

	graph = create_parsing_tree();

	if( graph )	/****** Der Graph konnte geparst werden. Berechnung des Layouts					******/
	{
		current_size	= graph->lp_graph.current_size;

		/****** Sorge dafuer, dass alter Graph mit Ableitungsbaum unveraendert bleibt				******/

		derivation_table	= graph->lp_graph.table;

		/****** Zusaetzliche Datenstrukturen fuer die Attributberechnung in den derivation_table einbauen	******/

		create_data_structures_for_attributes( derivation_table );
		create_same_upper_prod_in_next_for_tree( derivation_table );

		/****** Berechnung der Attribute									******/

		lpp_1_pass( derivation_table );
		lpp_2_pass( derivation_table );	

		/****** Berechnung des tree aus dem table								******/

		my_tree = convert_table_to_tree( derivation_table );
	
		/******	setze, dass keine Orientierungen
								******/
		create_by_derivation	= TRUE;


		/******	Erzeuge den Graphen und den ersten Knoten							******/
		dispatch_user_action(CREATE_GRAPH);
		new_label	= mem_copy_string( "S" );
		new_node 	= create_node( last.graph );
		node_set( new_node, ONLY_SET, DEFAULT_NODE_ATTRIBUTES, 0 );
		node_set( new_node, ONLY_SET, NODE_POSITION, 64, 64, 0);
		node_set( new_node, ONLY_SET, NODE_SIZE, 32, 32, 0);
		node_set( new_node, ONLY_SET, NODE_LABEL, new_label, 0);
		last.graph->lp_graph.current_size = current_size;

		/****** Erzeuge die Zeiger, damit vom Tree aus der Graph-Knoten gefunden werden kann			******/
		my_tree->tree_rec.node->graph_iso = new_node;

		/****** Eigentliche Prozedur zum Neuzeichnen des Graphen						******/

		redraw_graph_modified( my_tree );

		/****** Es darf wieder orientiert werden								******/

		create_by_derivation	= FALSE;

		restore_graph( last.graph );
		last.graph->lp_graph.changed = FALSE;

		free_table		( graph->lp_graph.table );
		clear_attributes	( my_tree );
		free_tree		( my_tree );

		graph->lp_graph.table	= NULL;
	}
	dispatch_user_action( UNSELECT );
}

/*************************************************************
wie oben, nur fester graph, nicht suchen
*************************************************************/

void	derivation_by_parsing_graph(Graph graph)
{
	tree_ref		my_tree;
	Node			new_node;
	char* 			new_label;
	lpp_Parsing_element 	graph_table;


	graph = create_parsing_tree_graph(graph);

	if( graph == NULL )
	{
		/****** Der Graph konnte mit dieser Grammatik nicht geparst werden					******/

			MsgBox( "Can't generate graph with this grammar", CMD_OK );
	}
	else	/****** Der Graph konnte geparst werden. Berechnung des Layouts						******/
	{
		current_size	= graph->lp_graph.current_size;

		/****** Loesche das alten derivation_net								******/

			if( graph->lp_graph.derivation_net )
			{
				free_tree		( graph->lp_graph.derivation_net );
				clear_attributes	( graph->lp_graph.derivation_net );
				clear_lp_edgelines	( graph );
				clear_edge_historys	( graph );
			}

		/****** Zusaetzliche Datenstrukturen fuer die Attributberechnung in den derivation_table einbauen	******/

				create_data_structures_for_attributes( graph->lp_graph.table );
				create_same_upper_prod_in_next_for_tree( graph->lp_graph.table );

		/****** Berechnung der Attribute									******/
		
				lpp_1_pass( graph->lp_graph.table );
				lpp_2_pass( graph->lp_graph.table );	

		/****** Berechnung des tree aus dem table								******/

				my_tree = convert_table_to_tree( graph->lp_graph.table );

		/******	setze, dass keine Orientierungen								******/
				create_by_derivation	= TRUE;

				graph_table = graph->lp_graph.table;

		/******	loesche den alten Graphen									******/
			      delete_graph( graph );

		/******	Erzeuge den Graphen und den ersten Knoten							******/
				dispatch_user_action(CREATE_GRAPH);
	
				new_label = mem_copy_string( "S" );

				new_node 		= create_node( last.graph );
				node_set( new_node, ONLY_SET, DEFAULT_NODE_ATTRIBUTES, 0 );
				node_set( new_node, ONLY_SET, NODE_POSITION, 64, 64, 0);
				node_set( new_node, ONLY_SET, NODE_SIZE, 32, 32, 0);
				node_set( new_node, ONLY_SET, NODE_LABEL, new_label, 0);

				last.graph->lp_graph.current_size = current_size;

		/****** Erzeuge die Zeiger, damit vom Tree aus der Graph-Knoten gefunden werden kann			******/
				new_node->lp_node.tree_iso = my_tree;
				my_tree->tree_rec.node->graph_iso = new_node;

		/****** Eigentliche Prozedur zum Neuzeichnen des Graphen						******/

				redraw_graph_modified( my_tree );


		/****** Es darf wieder orientiert werden								******/
				create_by_derivation	= FALSE;

		/****** Derivation_table auch am neuen Graphen								******/
				last.graph->lp_graph.table = graph_table;

	}
}
