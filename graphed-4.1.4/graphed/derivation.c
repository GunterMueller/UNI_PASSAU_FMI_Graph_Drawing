/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include "user.h"
#include "util.h"


#include "graphed_subwindows.h"

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/graphed.h>

#include <graphed/existing_extensions.h>
#include <utils/utils_export.h>
#ifdef EXTENSION_tree_layout_walker
#include <tree_layout_walker/tree_layout_walker_export.h>
#endif

#include "derivation.h"

extern void   get_buffer_center (int buffer, int *x, int *y);
extern void   set_working_area_canvas (Canvas canvas);
extern int    ggtaf_get_number (Gragra_textual_apply_form ggtaf);

void	add_derivation_tree (Graph production, Node replaced_node, Group right_side)
{
	Group				g;
	Gragra_textual_history_form	textual_apply_form;	/* to save the derivation history */

	if (replaced_node->graph->derivation_history == empty_sgraph) {
		replaced_node->graph->derivation_history = make_graph (make_attr (ATTR_DATA, NULL));
		replaced_node->graph->derivation_history->directed = TRUE;
	}
	if (replaced_node->derivation_history == empty_snode) {
		replaced_node->derivation_history = make_node (
			replaced_node->graph->derivation_history,
			make_attr (ATTR_DATA, NULL));
	}

	for_group (right_side, g) {

		textual_apply_form = (Gragra_textual_history_form) malloc (sizeof (struct gragra_textual_history_form));
		textual_apply_form->node_of_right_side = strsave (g->node->label.text);
		textual_apply_form->production = strsave (production->firstnode->label.text);

		g->node->derivation_history = make_node (
			replaced_node->graph->derivation_history,
			make_attr (ATTR_DATA, (char *)textual_apply_form));
		set_nodelabel (g->node->derivation_history, strsave (g->node->label.text));

		if (replaced_node->derivation_history != empty_snode) {
			make_edge (replaced_node->derivation_history,
				g->node->derivation_history,
				make_attr (ATTR_DATA, NULL));
		}

		if (g->node->derivation_history->tlist != empty_sedge) {
			set_edgelabel (g->node->derivation_history->tlist,
				strsave (g->node->iso->label.text));
				/* must take    ->iso because that one is */
				/* in the production and has still '/''s  */
		}

	} end_for_group (right_side, g);

	/* set node label in derivation history, for debugging purposes	*/
	if (replaced_node->derivation_history != empty_snode && textual_apply_form != NULL) {
		set_nodelabel (replaced_node->derivation_history, strsave (textual_apply_form->production));
	}
}


Graph	find_production_with_left_side (char *label)
{
	int	buffer;
	Graph	graph;

	if (label != NULL) for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) {
		for_all_graphs (buffer, graph){
			if (graph->is_production &&
			    graph->compile_time > 0 &&
			    !strcmp (graph->firstnode->label.text, label)) {
				return graph;
			}
		} end_for_all_graphs (buffer, graph);
	}

	return empty_graph;
}


Node	find_node_with_history (Graph graph, char *label, Slist history)
     	      
    	       
     	         /* Slist of Gragra_textual_history_form */
{
	Node	node, found_node;
	int	search_successful;
	Snode	derivation_history;
	Gragra_textual_history_form	ggthf;

	search_successful = FALSE;
	found_node = empty_node;
	for_nodes (graph, node) if (!strcmp (label, node->label.text)) {

		derivation_history = node->derivation_history;
		search_successful = TRUE;

		for_ggthf_list (history, ggthf) {

			if (derivation_history->tlist != empty_sedge &&
			    !strcmp (derivation_history->tlist->label, ggthf_get_right_side(ggthf)) &&
			    derivation_history->tlist->snode != empty_snode &&
			    !strcmp (derivation_history->tlist->snode->label, ggthf_get_production(ggthf))) {
#ifdef DEBUG
				fprintf (stderr, "    [ Node: %s , Production: %s ]\n",
					ggthf_get_right_side(ggthf),
					ggthf_get_production(ggthf));
#endif
				derivation_history = derivation_history->tlist->snode;

			} else {
				search_successful = FALSE;
			}

		} end_for_ggthf_list (history, ggthf);

		if (search_successful && (history == NULL || derivation_history->tlist == empty_sedge)) {
			found_node = node;
			break;
		}

	} end_for_nodes (graph, node);

	return found_node;
}
/************************************************************************/
/*									*/
/*		Evaluation of derivation sequences			*/
/*									*/
/************************************************************************/


Node	create_startnode (Graph graph, char *label)
{
	Node	startnode;
	int	x,y;

	if (graph == empty_graph) {
		graph = (Graph)dispatch_user_action (CREATE_GRAPH);
	} else {
		dispatch_user_action (UNSELECT);
		while (graph->firstnode != empty_node) {
			erase_and_delete_node (graph->firstnode);
		}
	}

	if (graph != empty_graph) {
		get_buffer_center (graph->buffer, &x,&y);
		startnode = (Node)dispatch_user_action (CREATE_NODE, graph, x,y);
		node_set (startnode,
			NODE_LABEL, strsave (label),
			NULL);
	} else {
		startnode = empty_node;
	}
	
	return startnode;
}


void			apply_derivation_sequence (Graph graph, Derivation_sequence sequence)
{
	int	buffer, saved_wac_buffer;
	char	*filename;
	Node	startnode;
	Graph	graph_of_startnode;
	Gragra_apply_form		apply_form;
	Gragra_textual_apply_form	ggtaf;
	
	/* Preparations ... */
	dispatch_user_action (UNSELECT);
	apply_form = (Gragra_apply_form) malloc (sizeof (struct gragra_apply_form));
	saved_wac_buffer = wac_buffer;

	for_ds_files (sequence, filename) {
		buffer = get_buffer_by_name (filename);
		if (file_exists(filename) && file_is_readable(filename)) {
			dispatch_user_action (BASIC_LOAD, buffer, filename);
		} else {
			error ("Cannot access file %s\n", filename);
			goto error_apply_derivation_sequence;
		}
	} end_for_ds_files (sequence, filename);


	if ((int)dispatch_user_action (COMPILE_ALL_PRODUCTIONS) == FALSE) {
		goto error_apply_derivation_sequence;
	};
	set_working_area_canvas (canvases[saved_wac_buffer].canvas);

	/* Now create a startnode */
	startnode = create_startnode (graph, ds_get_startnode (sequence));
	graph_of_startnode = startnode->graph;

	for_ds_apply_forms (sequence, ggtaf) {

		apply_form->production = find_production_with_left_side (
			ggtaf_get_production(ggtaf));
		apply_form->apply_on = find_node_with_history (
			graph_of_startnode,
			(ggtaf_get_node(ggtaf)),
			ggtaf_get_node_history(ggtaf));

		if (apply_form->production == empty_graph) {
			error ("Apply form %d\n", ggtaf_get_number(ggtaf));
			message ("Cannot apply %s on %s\n", ggtaf_get_production (ggtaf), ggtaf_get_node (ggtaf));
			message ("production %s not found\n", ggtaf_get_production (ggtaf));
			goto error_apply_derivation_sequence;
		} else if (apply_form->apply_on == empty_node) {
			error ("Apply form %d\n", ggtaf_get_number(ggtaf));
			message ("Cannot apply %s on %s\n", ggtaf_get_production (ggtaf), ggtaf_get_node (ggtaf));
			message ("node %s not found\n", ggtaf_get_node (ggtaf));
			goto error_apply_derivation_sequence;
		} else {
			dispatch_user_action (APPLY_PRODUCTION, apply_form);
		}

	} end_for_ds_apply_forms (sequence, ggtaf);


	dispatch_user_action (SELECT_GRAPH, graph_of_startnode);
	dispatch_user_action (CENTER_SELECTION);

error_apply_derivation_sequence :
	free (apply_form);
	return;
}


void	menu_apply_production (Menu menu, Menu_item menu_item)
{
	Graph	prod;
	Group	group;
	Node	node;
	Gragra_apply_form apply_form;

	node = get_picked_node ();
	if (node == empty_node) { /* accept a group that consists of a single node */
		group = get_picked_group();
		if (group != empty_group && group->suc == group) {
			node = group->node;
		}
	}

	if (node != empty_node && node->label.text != NULL) {
		prod = find_production_with_left_side ((char *)xv_get (menu_item, MENU_STRING));
		if (prod != empty_graph && prod->directed == node->graph->directed && prod != node->graph) {
			apply_form = (Gragra_apply_form) malloc (sizeof (struct gragra_apply_form));
			apply_form->production = prod;
			apply_form->apply_on = node;
			dispatch_user_action (APPLY_PRODUCTION, apply_form);
			free (apply_form);
		}
	} else {
		bell ();
	}

	force_repainting ();
}

/************************************************************************/
/*									*/
/*			Display the derivation graph			*/
/*									*/
/************************************************************************/



void	call_display_derivation_graph (Sgraph_proc_info info, Sgraph derivation_history)
{
	info->new_buffer = get_buffer_by_name ("- derivation tree -");
	delete_graphs_in_buffer (info->new_buffer);
	info->new_sgraph = copy_sgraph (derivation_history);
}


void		menu_display_derivation_graph (Menu menu, Menu_item menu_item)
{
	Graph	graph;

	graph = get_picked_graph();

	if (graph != empty_graph &&
	    graph->derivation_history != empty_sgraph) {

		call_sgraph_proc (call_display_derivation_graph,
				  (char *)graph->derivation_history);
		dispatch_user_action (SELECT_GRAPH,
				      graph->derivation_history->iso->graphed);
		call_sgraph_proc (call_fit_nodes_to_text, NULL);
#ifdef EXTENSION_tree_layout_walker
		call_sgraph_proc (call_tree_layout_walker_left_to_right, NULL);
#endif
		shrink_buffer (((Graph)(graph->derivation_history->iso->graphed))->buffer);

	} else if (graph != empty_graph &&
	           graph->derivation_history == empty_sgraph) {

		error ("Picked graph has no derivation tree\n");

	} else {

		error ("No graph picked\n");

	}

}


