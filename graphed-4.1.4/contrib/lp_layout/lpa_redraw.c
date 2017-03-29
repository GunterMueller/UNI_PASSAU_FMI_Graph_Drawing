#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_general_functions.h"

#include "lpa_optimization_structures.h"
#include "lpa_create_area_struc.h"
#include "lpa_create_sizes.h"
#include "lpa_draw_new_tree.h"

#include "lpa_redraw.h"



/****************************************************************************************/
/*											*/
/*	Hauptfile fuer die Platzoptimale Neuzeichnung eines Graphen			*/
/*											*/
/****************************************************************************************/


/*****************************************************************************************
function:	lp_create_optimal_graph_layout_for_derivation_tree
Input:	----

	1.sucht einen selektierten Graphen und holt sich dessen Ableitungsbaum
	2.Zeichnet diesen Graphen mit platzoptimalen Layout neu
	dabei : NEUBERECHNUNG aller Attribute und Datenstrukturen

Output:	---
*****************************************************************************************/

void	lp_create_optimal_graph_layout_for_derivation_tree(void)
{
	Graph		derivated_graph;
	tree_ref	derivation_tree;


	derivated_graph = compute_graph();
	if( derivated_graph )
	{
		if( (derivation_tree = derivated_graph->lp_graph.derivation_net))
		{
			/*** Wir haben einen selektierten Graphen mit Derivation_net gefunden 			***/
			/*** 1. Erzeuge Datenstrukturen, die zum platzoptimalen Neuzeichenen notwendig sind 	***/
				/*** Falls alte Datenstrukturen vorhanden sind, loesche diese  			***/

					lpa_free_area_struc_pointers( derivation_tree );

				/*** ... und erzeuge die neuen 							***/

					lpa_create_possible_productions_and_nodes_for_derivation_tree( derivation_tree );
					lpa_create_area_structures_for_derivation_tree( derivation_tree );

			/*** 2. Berechne optimales Layout ***/

					lpa_create_sizes_for_derivation_tree( derivation_tree );

			/*** 3. Zeichne das neue Layout ***/

					lpa_draw_area_optimal_tree( derivation_tree, derivated_graph );
		}
		else
		{
			/*** Nachricht ausgeben, dass der selektierte Graph keinen Ableitungsbaum besitzt ***/
			MsgBox( "Graph has no derivation_tree. \n You can use an algorithm which parses Graph.", CMD_OK );
		}
	}
}

/*****************************************************************************************
function:	lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph
Input:	Graph graph

	2.Zeichnet diesen Graphen mit platzoptimalen Layout neu
	dabei : NEUBERECHNUNG aller Attribute und Datenstrukturen
	Kommentierung siehe Funktion oben

Output:	---
*****************************************************************************************/

void	lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph(Graph graph)
{
	Graph		derivated_graph;
	tree_ref	derivation_tree;


	if( (derivated_graph = graph) )
	{
		if( (derivation_tree = derivated_graph->lp_graph.derivation_net) )
		{
			lpa_free_area_struc_pointers( derivation_tree );
			lpa_create_possible_productions_and_nodes_for_derivation_tree( derivation_tree );
			lpa_create_area_structures_for_derivation_tree( derivation_tree );
			lpa_create_sizes_for_derivation_tree( derivation_tree );
			lpa_draw_area_optimal_tree( derivation_tree, derivated_graph );
		}
		else
		{
			/*** Nachricht ausgeben, dass der selektierte Graph keinen Ableitungsbaum besitzt	***/
			/*** Wird beim Aufruf im subframe bereits gemacht					***/
		}
	}
}

