/* (C) Universitaet Passau 1986-1994 */
#include <xview/xview.h>
#include <xview/panel.h>

#include <std.h>
#include <sgraph.h>
#include <algorithms.h>
#include <slist.h>
#include <graphed.h>
#include "minimal_bends_layout_export.h"
#include <graphed/graphed_pin_sf.h>
#include <graphed/gridder.h>
#include <graphed/util.h>


static	void	create_bends_subframe (void);
static	void	bends_sf_set   (Panel_item item, Event *event);
static	void	bends_sf_reset (Panel_item item, Event *event);
static	void	bends_sf_do    (Panel_item item, Event *event);
static	void	bends_sf_done  (Frame frame);

static	Graphed_pin_subframe	bends_sf;
static	Gridder			gridder;

Minimal_bends_settings minimal_bends_settings;


Minimal_bends_settings init_minimal_bends_settings(void)
{
	Minimal_bends_settings settings;

	settings.grid = 64;
	settings.grid_defaults = GRIDDER_DISTANCE_2_LARGEST_SIZE;

	return settings;
}


static	void	create_bends_subframe(void)
{
	int	row_count = 0;
	
	if (bends_sf == (Graphed_pin_subframe)NULL) {
		bends_sf = new_graphed_pin_subframe((Frame)0);
	}
	bends_sf->set_proc   = bends_sf_set;
	bends_sf->reset_proc = bends_sf_reset;
	bends_sf->do_proc    = bends_sf_do;
	bends_sf->done_proc  = bends_sf_done;

	graphed_create_pin_subframe (bends_sf, "Planar / Bends Layout");

	row_count += 1;
	gridder = create_gridder (bends_sf->panel,
		GRIDDER_HEIGHT,
		"grid",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);


	window_fit(bends_sf->panel);
	window_fit(bends_sf->frame);
}


void	show_bends_subframe (void)
{
	if (!showing_bends_subframe()) {
		create_bends_subframe();
	}

	if (bends_sf != (Graphed_pin_subframe)NULL) {
	
		gridder_set (gridder,
			minimal_bends_settings.grid_defaults,
			minimal_bends_settings.grid);
		
		compute_subwindow_position_at_graph_of_current_selection (
			bends_sf->frame);
		xv_set(bends_sf->frame, WIN_SHOW, TRUE, NULL);
		
		bends_sf->showing = TRUE;
		
	}
}


int	showing_bends_subframe(void)
{
	return showing_graphed_pin_subframe(bends_sf);
}


static	void	bends_sf_set (Panel_item item, Event *event)
{
	save_minimal_bends_settings ();
}


static	void	bends_sf_reset (Panel_item item, Event *event)
{
}


static	void	bends_sf_do (Panel_item item, Event *event)
{
	save_minimal_bends_settings ();
	call_sgraph_proc (check_and_call_minimal_bends_layout, NULL);
}


static	void	bends_sf_done (Frame frame)
{
	save_minimal_bends_settings ();
	
	free (gridder);   gridder = (Gridder)NULL;
}


void	save_minimal_bends_settings (void)
{
	if (showing_bends_subframe()) {
		minimal_bends_settings.grid = gridder_get_size (gridder);
		minimal_bends_settings.grid_defaults = (int)gridder_get_value (gridder);
	} else {
		if (minimal_bends_settings.grid != GRIDDER_DISTANCE_OTHER) {
			minimal_bends_settings.grid = recompute_gridder_size (
				NULL,
				minimal_bends_settings.grid_defaults,
				GRIDDER_HEIGHT);
		}
	}
}


void menu_minimal_bends_layout (Menu menu, Menu_item menu_item)
{
	save_minimal_bends_settings ();
	call_sgraph_proc (check_and_call_minimal_bends_layout, NULL);
	
}


void menu_minimal_bends_layout_settings (Menu menu, Menu_item menu_item)
{
	save_minimal_bends_settings ();
	show_bends_subframe ();
}


static	int	snode_degree (Snode node)
{
	Sedge	edge;
	int	degree = 0;
	
	for_sourcelist (node, edge) {
		degree ++;
	} end_for_sourcelist (node, edge);
	if (node->graph->directed) for_targetlist (node, edge) {
		degree ++;
	} end_for_targetlist (node, edge);
	
	return degree;
}

/*
static	int	sgraph_degree (Sgraph graph)
{
	Snode	node;
	int	degree = 0, node_deg;
	
	for_all_nodes (graph, node) {
		node_deg = snode_degree (node);
		degree = maximum (degree, node_deg);
	} end_for_all_nodes (graph, node);
		
	return degree;
}
*/

int	test_minimal_bends_layout (Sgraph_proc_info info)
{
	Sgraph	graph;
	Snode	node;
	
	graph = info->sgraph;
	if (graph == empty_graph || graph->nodes == empty_node) {
		error ("empty graph\n");
		return FALSE;
	} else if (graph->nodes == graph->nodes->suc) {
		return TRUE;
	} else if (!test_sgraph_connected(graph)) {
		error ("graph is not connected\n");
		return FALSE;
	} else {
	
		Slist	nodes_with_degree_greater_four;
		int	degree, degree_ok;
		
		
		/* Check for nodes with degree > 4 */
		
		nodes_with_degree_greater_four = empty_slist;
		degree_ok = TRUE;
		for_all_nodes (graph, node) {
			degree = snode_degree (node);
			if (degree > 4) {
				degree_ok = FALSE;
				nodes_with_degree_greater_four = add_to_slist (
					nodes_with_degree_greater_four,
					make_attr(ATTR_DATA, (char *)node));
			}
		} end_for_all_nodes (graph, node);
		
		if (!degree_ok) {
			if (nodes_with_degree_greater_four == nodes_with_degree_greater_four->suc) {
				error ("There is a node with degree > 4\n");
			} else {
				error ("There are nodes with degree > 4\n");
			}
			info->new_selected = SGRAPH_SELECTED_GROUP;
			info->new_selection.group = nodes_with_degree_greater_four;
			return FALSE;
		}

		return test_graph_is_drawn_planar (graphed_graph(graph));
		
	}
}


void	 	 check_and_call_minimal_bends_layout (Sgraph_proc_info info)
{
	Sgraph	graph;
	
	graph = info->sgraph;
	
	if (test_minimal_bends_layout(info)) {
		call_minimal_bends_layout (info);
		info->recenter = TRUE;
	} else {
		error ("Cannot apply algorithm\n");
	}
}
