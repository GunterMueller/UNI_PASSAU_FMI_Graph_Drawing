#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "user_header.h"

#include "lp_general_functions.h"
#include "lp_test.h"
#include "lp_history.h"
#include "lp_edgeline.h"
#include "lp_reduce.h"
#include "lp_free_tree.h"
#include "lpr_top_down_cost_opt.h"
#include "lpr_ggraph.h"
#include "lpa_redraw.h"

#include "lp_delete.h"

/**************************************************************************************************
function	delete_sub_tree
Input:	tree_ref tree, Node node

	Node ist der neu erzeugte Knoten, der alles was geloescht wird ersetzt. Kuemmert sich nur
	um die Kanten, die an ihm haengen.
	Achtung: Es wird auch nachgeschaut, ob evtl. verschwundene Kanten wieder erzeugt werden
		 muessen.

Output:	---
**************************************************************************************************/

void 		delete_sub_tree	(tree_ref tree, Node node)
{
	tree_ref	first;
	Node 		cur_node;
	Edge 		edge, new_edge;
	Edgeline 	line;
	int 		x = 0; 
	int 		y = 0;

	if ( tree->tree_rec.node->leaf )
	{
		cur_node = tree->tree_rec.node->graph_iso;

		for_edge_targetlist(cur_node, edge)
		{
			if ( edge->source->lp_node.tree_iso->tree_rec.node->flag == 0 ) 
			{
				char	*old_label = get_label( edge->lp_edge.history ); 

			     	if ( !edge_exists( edge->source, node, old_label ) )
				{
					line = new_edgeline(x, y);
					(void)add_to_edgeline (line, x,y);

					new_edge = create_edge (edge->source, node);

					edge_set ( new_edge, ONLY_SET, DEFAULT_EDGE_ATTRIBUTES, 0);
					edge_set ( new_edge, ONLY_SET, EDGE_LINE, line, 0);
					edge_set ( new_edge, ONLY_SET, EDGE_LABEL, old_label, 0);
					new_edge->lp_edge.history = create_unflagged_history( edge->lp_edge.history );
				}
			}
		}
		end_for_edge_targetlist(cur_node, edge);

		for_edge_sourcelist(cur_node, edge)
		{
			if ( edge->target->lp_node.tree_iso->tree_rec.node->flag == 0 ) 
			{
				char	*old_label = get_label( edge->lp_edge.history ); 

			     	if ( !edge_exists( edge->target, node, old_label ) )
				{
					line = new_edgeline(x, y);
					(void)add_to_edgeline (line, x,y);

					new_edge = create_edge ( node, edge->target );

					edge_set ( new_edge, ONLY_SET, DEFAULT_EDGE_ATTRIBUTES, 0);
					edge_set ( new_edge, ONLY_SET, EDGE_LINE, line, 0);
					edge_set ( new_edge, ONLY_SET, EDGE_LABEL, old_label, 0);
					new_edge->lp_edge.history = create_unflagged_history( edge->lp_edge.history );
				}
			}
		}
		end_for_edge_sourcelist(cur_node, edge);
	}
	else
	{
		first = tree->tree_rec.node->first_son;

		while ( first )
		{
			if ( first->tree_rec_type == TREE_NODE )
			{
				delete_sub_tree(first, node);
			}
			first = first->next_brother;
		}
	}
}		

/**************************************************************************************************
function	delete_t
Input:	tree_ref tree, Graph graph

	Loesche alles was im Ableitungsbaum unter tree liegt

Output:	---
**************************************************************************************************/

void delete_t	(tree_ref tree, Graph graph)
{
	Node 		new_node;

	new_node = create_node( graph );

	node_set (new_node, ONLY_SET, DEFAULT_NODE_ATTRIBUTES, 0 );
	node_set (new_node, ONLY_SET, NODE_LABEL, strsave(tree->tree_rec.node->prod_iso->label.text), 0);
	node_set (new_node, ONLY_SET, NODE_TYPE, find_nodetype("#box"), 0);


	set_flag( tree );
	delete_sub_tree( tree, new_node );
	delete_graphed_nodes_of_tree( tree );
	reset_flag( tree );
	new_node->lp_node.tree_iso = tree;
	tree->tree_rec.node->graph_iso = new_node;
	tree->tree_rec.node->leaf = TRUE;
	tree->tree_rec.node->big = FALSE;
	tree->tree_rec.node->flag = FALSE;

	free_tree_without_root( tree );

	/****** Ab jetzt darf nur noch area-optimization ausgefuehrt werden					******/

	graph->lp_graph.reduced = TRUE;

	lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph( graph );
	restore_graph( graph );

	dispatch_user_action (UNSELECT);
}

/**************************************************************************************************
function	delete_tree
Input:	---

	Suche selektierten Knoten im Graph und loesche alles was zum Ableitungsbaum unter seiner
	Wurzel gehoert.

Output:	---
**************************************************************************************************/

void	delete_tree	(void)
{
	Node	node = NULL;
	Graph	graph;
	int	result;
			
	if (something_picked)
	{ 
		if( picked_object->what == NODE_PICKED ) 
		{
			node = picked_object->which.node;
		}
		else
		{
			MsgBox( "No node picked.", CMD_OK );
		}

		if (node != NULL)
		{
			if ( node->graph->is_production )
			{
				MsgBox( "No hierarchies for productions.", CMD_OK );
			}
			else
			{
				if ( node->graph->lp_graph.derivation_net == NULL )
				{
					MsgBox( "No hierarchical graph.", CMD_OK );
				}
				else
				{
					graph = node->graph;
					if( graph_state.lp_graph_state.LGG_Algorithm != TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING )
					{
						MsgBox( "Only possible for top down area optimization.", CMD_OK );
						dispatch_user_action (UNSELECT);
					}
					else
					{
						if( !graph->lp_graph.hierarchical_graph )
						{
							MsgBox( "This is no hierarchical graph. Only possible for a hierarchical graph.", CMD_OK );
						}
						else
						{
						  	if ( test_grammar_changed( node->graph->lp_graph.creation_time ) )
			     				{
			       					/* node->graph->lp_graph.hierarchical_graph = FALSE; */
			     				}
			    				else
			     				{
			        				if( lp_lgg_derivation_tests(node->graph) ) 
								{
									if( !edge_relabeling_graph_grammar() )
									{
										MsgBox( "Not possible for an edge relabeling grammar", CMD_OK );
									}
									else
									{
										if( graph->lp_graph.changed )
										{
											result =	notice_prompt (base_frame, NULL,		/*fisprompt*/
											NOTICE_MESSAGE_STRINGS,	"WARNING!\nGraph has been changed.\nCompute anyway?", NULL,
											NOTICE_BUTTON_YES,	"Yes",
											NOTICE_BUTTON_NO,	"No",
											NULL);

											if( result == NOTICE_YES )
											{
												delete_t( node->lp_node.tree_iso->father, graph);
												graph->lp_graph.changed = FALSE;
											}
										}
										else
										{
											delete_t( node->lp_node.tree_iso->father, graph);
											graph->lp_graph.changed = FALSE;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
	else
	{
		MsgBox( "No Node picked.", CMD_OK );
	}
}
