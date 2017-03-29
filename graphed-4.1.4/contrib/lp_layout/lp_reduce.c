#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "user_header.h"

#include "lpa_redraw.h"
#include "lpr_eedge.h"
#include "lpr_hhierarchie.h"
#include "lp_general_functions.h"
#include "lp_history.h"
#include "lp_edgeline.h"
#include "lp_draw.h"
#include "lp_test.h"

/************************************************************************/
/*									*/
/*	modul	lp_reduce.c						*/
/*		Reduzieren eines Teilgraphen.				*/
/*									*/
/************************************************************************/

/*************************************************************************
function:	set_flag
Input:	tree_ref tree

	Set flag of tree and recursivly for all nodes of the below part of tree
	in the derivation net to TRUE
*************************************************************************/

void	set_flag	(tree_ref tree)
{
	tree_ref	first;
	
	tree->tree_rec.node->flag = TRUE;
	first = tree->tree_rec.node->first_son;
	while ( first )
	{
		if ( first->tree_rec_type == TREE_NODE )
		{
			set_flag( first );
		}
		first = first->next_brother;
	}
}

/*************************************************************************
function:	reset_flag
Input:	tree_ref tree

	Set flag of tree and recursivly for all nodes of the below part of tree
	in the derivation net to FALSE
*************************************************************************/

void reset_flag	(tree_ref tree)
{
	tree_ref first;
	
	tree->tree_rec.node->flag = FALSE;
	first = tree->tree_rec.node->first_son;
	while ( first )
	{
		if ( first->tree_rec_type == TREE_NODE )
		{
			reset_flag( first );
		}
		first = first->next_brother;
	}
}

/*************************************************************************
function:	create_unflagged_history
Input:	history_ref first

Output: list of all history_ref of first which have an unflagged father
*************************************************************************/

history_ref	create_unflagged_history	(history_ref first)
{
	history_ref	result = NULL;

	if ( first != NULL ) 
	{
		history_ref	cur = first;
		history_ref	new;
		
		do
		{
			if ( !cur->element->father->tree_rec.node->flag )
			{
				new = new_history_ref();
				new->element = cur->element;
				result = add_to_history( result, new );
			}
			cur = cur->suc;
		}
		while ( cur != first );
	}
	return( result );
}
	
/*************************************************************************
function:	get_label
Input:	history_ref first

Output:	label of the corresponding edge in the production
*************************************************************************/

char		*get_label (history_ref first)
{
	char	*result = NULL;

	if ( first != NULL )
	{
		result = strsave( first->element->tree_rec.history_elem->prod_iso->label.text );
	}
	return( result );
}

/*************************************************************************
function:	equal_strings
Input:	char s, t

Output:	TRUE iff. s = t
	FALSE otherwise
*************************************************************************/

int 	equal_strings	(char *s, char *t)
{
	if ( s == NULL )
	{
		if ( t == NULL )
		{
			return(1);
		}
		else
		{
			return(0);
		}
	}
	
	if ( t == NULL )
	{
		return(0);
	}

	return( !strcmp(s, t) );
}

/*************************************************************************
function:	edge_exists
Input:	Node source, target, char lab

Output:	True iff. an edge from source to target with label lab exists
*************************************************************************/

int 	edge_exists 	(Node source, Node target, char *lab)
{
	Edge edge;

	for_edge_sourcelist(source, edge)
	{
		if ( equal_strings( edge->label.text, lab ) && ( edge->target == target ) )
		{
			return( TRUE );
		}
	}
	end_for_edge_sourcelist(source, edge);
	return( FALSE );
}

/*************************************************************************
function	reduce_sub_tree

	Eigentlich wird hier dafuer gesorgt, dass die Kanten nicht ver-
	schwunden bleiben.

*************************************************************************/

void 		reduce_sub_tree	(tree_ref tree, Node node)
{
	tree_ref	first;
	char*		old_label;
	Node 		cur_node;
	Edge 		edge, new_edge;
	Edgeline 	line;
	int 		x; 
	int 		y;


	if ( tree->tree_rec.node->leaf )
	{
		cur_node = tree->tree_rec.node->graph_iso;
		x = 0; 
		y = 0;

		for_edge_targetlist(cur_node, edge)
		{
			if ( edge->source->lp_node.tree_iso->tree_rec.node->flag == 0 ) 
			{
				old_label = get_label( edge->lp_edge.history ); 

			     	if ( !edge_exists( edge->source, node, old_label ) )
				{
					/*** Hier muesste jetzt abhaengig vom verwendeten Algorithmus die Kante neu gezogen werden ***/
					line		= new_edgeline(x, y);
					(void)add_to_edgeline (line, x,y);
					new_edge	= create_edge (edge->source, node);

					edge_set( new_edge, ONLY_SET, DEFAULT_EDGE_ATTRIBUTES, 0 );
					edge_set( new_edge, ONLY_SET, EDGE_LINE, line, 0 );
					edge_set( new_edge, ONLY_SET, EDGE_LABEL, old_label, 0 );
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
				reduce_sub_tree(first, node);
			}
			first = first->next_brother;
		}
	}
}		

/*************************************************************************
function:	
*************************************************************************/

void	delete_graphed_nodes_of_tree	(tree_ref tree)
{
	tree_ref	first;

	if (tree->tree_rec.node->leaf)
	{
		erase_and_delete_node(tree->tree_rec.node->graph_iso);
		tree->tree_rec.node->graph_iso = NULL;
	}
	else
	{
		first = tree->tree_rec.node->first_son;
		while ( first )
		{
			if ( first->tree_rec_type == TREE_NODE )
			{
				delete_graphed_nodes_of_tree( first );
			}
			first = first->next_brother;
		}
	}
}

/*************************************************************************
function	reduce
Input:	tree_ref tree, Graph graph

	tree ist die Wurzel, ab der reduced werden muss
*************************************************************************/

void reduce	(tree_ref tree, Graph graph)
{
	Node 	new_node;
	char	*label = (char *)malloc(1);

	label[0] = '\0';
	new_node = create_node (graph);

	node_set( new_node, ONLY_SET, DEFAULT_NODE_ATTRIBUTES, 0 );
	node_set (new_node, ONLY_SET, NODE_LABEL, label, 0);
	node_set (new_node, ONLY_SET, NODE_TYPE, find_nodetype("#box"), 0);

	set_flag( tree );			/*** Notwendig, damit Kantengeschichten unflagged erzeugt werden koennen ***/
	reduce_sub_tree( tree, new_node );
	delete_graphed_nodes_of_tree( tree );
	reset_flag( tree );

	new_node->lp_node.tree_iso = tree;
	tree->tree_rec.node->graph_iso = new_node;
	tree->tree_rec.node->leaf = TRUE;
	tree->tree_rec.node->big = TRUE;

	/****** Ab jetzt darf nur noch area-optimization ausgefuehrt werden					******/

	graph->lp_graph.reduced = TRUE;

	lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph( graph );

	restore_graph( graph );
	graph->lp_graph.changed = FALSE;
	dispatch_user_action (UNSELECT);
}

/*************************************************************************
function	reduce_tree
Input:	---

	Sucht, ob der Knoten eines Graphen mit Ableitungsbaum selektiert ist.
	Wenn ja wird der Ableitungsbaum ab diesem knoten reduced

Output:	---
*************************************************************************/

void	reduce_tree	(void)
{
	Node	node = NULL;
	Graph	graph;
	int	result;
			
	if( something_picked )
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
												reduce(node->lp_node.tree_iso->father, graph);
											}
										}
										else
										{
											reduce(node->lp_node.tree_iso->father, graph);
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
