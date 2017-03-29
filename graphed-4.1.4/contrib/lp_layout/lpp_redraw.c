#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lp_general_functions.h"
#include "lp_history.h"
#include "lp_draw.h"
#include "lp_tree_top_sort.h"
#include "lpp_redraw.h"
#include "lpr_apply_production.h"
#include "lpr_ggraph.h"


/***************************************************************************************************************
function:	lpp_apply_production
Input:	Graph prod, Node node

	Simuliert einen Graphed-Ableitungsschrit, wobei der Ableitungsbaum mit Aktualisiert wird.
	Der Graph wird waehrend der Ausfuehrungszeit nicht neugezeichnet und auch die Attribute fuer einen
	bestimmten Layoutalgorithmus werden nicht berechnet

Output:	---
***************************************************************************************************************/

void	lpp_apply_production (Graph prod, Node node)
{
	lpr_Node	head;
	Group		copy_of_right_side;
	Group		g;
        Graph 	  tmp_graph;
	

	/* At first, copy the right side into graph		*/
	copy_of_right_side = copy_group_to_graph (prod->gra.gra.nce1.right_side, node->graph);
	
	
	/****** Bei derivation mit parsing muss Zeiger von Zwischenbaum erzeugt werden			******/
	/****** Alle benoetigten Zeiger von der Produktion zum Tree wurden in redraw graph (local )	******/
	/****** gesetzt ( kann ja nicht fuer immer gelten )						******/

	for_group( copy_of_right_side, g)
	{
		g->node->iso->lp_node.tree_iso->tree_rec.node->graph_iso = g->node;
	}
	end_for_group( copy_of_right_side, g);

	/* Ableitungsbaum aktualisieren*/
	make_node_father_to_production(node, prod );
	create_personal_history_of_edge( copy_of_right_side );
	make_local_embedding (ENCE_1, node, prod);  
	create_net_edges = TRUE;
	copy_lp_edgelines_to_tree( prod );
	copy_topological_sorting_to_tree( node->lp_node.tree_iso );
	tmp_graph = node->graph; 
	head = lpr_apply_production( prod, node );
	set_array_of_iso_prod_pointers(head->applied_production);

	erase_and_delete_node (node);
	
}





/*****************************************************************************************************
function:	create_pointer_from_prod_to_tree
Input:	tree_ref head

	Erzeugt Zeiger "tree_iso" von den Knoten der verwendeten Produktion zum  derivation_tree
*****************************************************************************************************/

void	create_pointer_from_prod_to_tree(tree_ref head)
{
	tree_ref	cur = head->tree_rec.node->first_son;

	while( cur )
	{
		if( cur->tree_rec_type == TREE_NODE )
		{
			cur->tree_rec.node->prod_iso->lp_node.tree_iso = cur;
		}
		cur = cur->next_brother;
	}
}

/*****************************************************************************************************
function:	redraw_graph
Input:	tree_ref father

	Erzeugt bei derivation_by_parsing aus dem Ableitungsbaum den neuen Graphen. In father sind
	Zeiger auf verwendete Produktion und entsprechenden Knoten im Graphen eingetragen, so dass
	apply_production verwendet werden kann ( apply_production muss etwas veraendert werden 
	damit alle Zeiger passend eingetragen werden und in pass_2 darf nicht orientiert werden
	(wird angezeigt durch globale Variable create_by_derivation, die vor Aufruf dieser 
	Prozedur auf TRUE gesetzt wird) ).
	REKURSIV!!
*****************************************************************************************************/

void	redraw_graph(tree_ref father)
{
	tree_ref	cur = father;

	while( cur )
	{
		if( cur->tree_rec_type == TREE_NODE )
		{
			if( cur->tree_rec.node->used_prod )
			{
				create_pointer_from_prod_to_tree( cur );

				node_test = TRUE;

				lpp_apply_production( cur->tree_rec.node->used_prod, cur->tree_rec.node->graph_iso );

				redraw_graph( cur->tree_rec.node->first_son );
			}

		}
		cur = cur->next_brother;
	}
}


void	redraw_graph_modified(tree_ref father)
{
	Graph	graph = father->tree_rec.node->graph_iso->graph;

	redraw_graph( father );

	compute_attributes( graph );
	restore_graph( graph );
}


