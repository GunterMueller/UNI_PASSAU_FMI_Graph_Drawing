/* (C) Universitaet Passau 1986-1994 */
#include <xview/xview.h>
#include <xview/panel.h>

#include <std.h>
#include <sgraph.h>
#include <algorithms.h>
#include "tree_layout_walker_export.h"
#include "graphed/gridder.h"
#include "graphed/graphed_pin_sf.h"


static	void	create_tree_subframe (void);
static	void	tree_layout_sf_set   (Panel_item item, Event *event);
static	void	tree_layout_sf_reset (Panel_item item, Event *event);
static	void	tree_layout_sf_do    (Panel_item item, Event *event);
static	void	tree_layout_sf_done  (Frame frame);

Graphed_pin_subframe	tree_sf;
static	Gridder		vertical_gridder,
			subtreeseparation_gridder,
			siblingseparation_gridder;

Tree_layout_walker_settings tree_layout_walker_settings;


Tree_layout_walker_settings init_tree_layout_walker_settings(void)
{
	Tree_layout_walker_settings settings;

	settings.vertical_separation = 64;
	settings.siblingseparation = 64;
	settings.subtreeseparation = 64;

	settings.size_defaults_x_sibling = GRIDDER_DISTANCE_1_DEFAULT_SIZE;
	settings.size_defaults_x_subtree = GRIDDER_DISTANCE_1_DEFAULT_SIZE;
	settings.size_defaults_y         = GRIDDER_DISTANCE_15_DEFAULT_SIZE;

	return settings;
}



static	void	create_tree_subframe(void)
{
	int	row_count = 0;
	
	if (tree_sf == (Graphed_pin_subframe)NULL) tree_sf = new_graphed_pin_subframe((Frame)0);
	tree_sf->set_proc   = tree_layout_sf_set;
	tree_sf->reset_proc = tree_layout_sf_reset;
	tree_sf->do_proc    = tree_layout_sf_do;
	tree_sf->done_proc  = tree_layout_sf_done;

	graphed_create_pin_subframe (tree_sf, "Tree Layout");

	row_count ++;
	vertical_gridder = create_gridder (tree_sf->panel,
		GRIDDER_HEIGHT,
		"vertical separation",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);
		
	row_count += 2;
	siblingseparation_gridder = create_gridder (tree_sf->panel,
		GRIDDER_WIDTH,
		"sibling separation",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);
		
	row_count += 2;
	subtreeseparation_gridder = create_gridder (tree_sf->panel,
		GRIDDER_WIDTH,
		"subtree separation",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);
		

	window_fit(tree_sf->panel);
	window_fit(tree_sf->frame);
}


void	show_tree_layout_walker_subframe (void (*graphed_done_proc) ())
{
	if (tree_sf == (Graphed_pin_subframe)NULL || !tree_sf->showing) {
		create_tree_subframe();
	}
	
	if (tree_sf != (Graphed_pin_subframe)NULL) {
	
		tree_sf->graphed_done_proc = graphed_done_proc;

		gridder_set (vertical_gridder,
			tree_layout_walker_settings.size_defaults_y,
			tree_layout_walker_settings.vertical_separation);
		gridder_set (siblingseparation_gridder,
			tree_layout_walker_settings.size_defaults_x_sibling,
			tree_layout_walker_settings.siblingseparation);
		gridder_set (subtreeseparation_gridder,
			tree_layout_walker_settings.size_defaults_x_subtree,
			tree_layout_walker_settings.subtreeseparation);
		
		compute_subwindow_position_at_graph_of_current_selection (
			tree_sf->frame);
		xv_set (tree_sf->frame, WIN_SHOW, TRUE, 0);
		
		tree_sf->showing = TRUE;
		
	}
}

int	showing_tree_layout_walker_subframe (void)
{
	return showing_graphed_pin_subframe (tree_sf);
}


static	void	tree_layout_sf_set (Panel_item item, Event *event)
{
	save_tree_layout_walker_settings ();
}


static	void	tree_layout_sf_reset (Panel_item item, Event *event)
{
}


static	void	tree_layout_sf_do (Panel_item item, Event *event)
{
	save_tree_layout_walker_settings ();
	call_sgraph_proc (call_tree_layout_walker, NULL);
}


static	void	tree_layout_sf_done (Frame frame)
{
	save_tree_layout_walker_settings ();
	
	free (siblingseparation_gridder);
	siblingseparation_gridder = (Gridder)NULL;
	free (subtreeseparation_gridder);
	subtreeseparation_gridder = (Gridder)NULL;
	free (vertical_gridder);
	vertical_gridder = (Gridder)NULL;
}

void	save_tree_layout_walker_settings (void)
{
	if (tree_sf != (Graphed_pin_subframe)NULL && tree_sf->showing) {
		tree_layout_walker_settings.vertical_separation =
			gridder_get_size (vertical_gridder);
		tree_layout_walker_settings.siblingseparation =
			gridder_get_size (siblingseparation_gridder);
		tree_layout_walker_settings.subtreeseparation =
			gridder_get_size (subtreeseparation_gridder);
		tree_layout_walker_settings.size_defaults_y =
			(int)gridder_get_value (vertical_gridder);
		tree_layout_walker_settings.size_defaults_x_sibling =
			(int)gridder_get_value (siblingseparation_gridder);
		tree_layout_walker_settings.size_defaults_x_subtree =
			(int)gridder_get_value (subtreeseparation_gridder);
	} else {
		if (tree_layout_walker_settings.size_defaults_y !=
		    GRIDDER_DISTANCE_OTHER) {
			tree_layout_walker_settings.vertical_separation =
			recompute_gridder_size (
				NULL,
				tree_layout_walker_settings.size_defaults_y,
				GRIDDER_HEIGHT);
		}
		if (tree_layout_walker_settings.size_defaults_x_sibling !=
		    GRIDDER_DISTANCE_OTHER) {
			tree_layout_walker_settings.siblingseparation =
			recompute_gridder_size (
				NULL,
				tree_layout_walker_settings.size_defaults_x_sibling,
				GRIDDER_WIDTH);
		}
		if (tree_layout_walker_settings.size_defaults_x_subtree !=
		    GRIDDER_DISTANCE_OTHER) {
			tree_layout_walker_settings.subtreeseparation =
			recompute_gridder_size (
				NULL,
				tree_layout_walker_settings.size_defaults_x_subtree,
				GRIDDER_WIDTH);
		}
	}
}


void menu_tree_layout_walker(Menu menu, Menu_item menu_item)
{ 
	save_tree_layout_walker_settings ();
	call_sgraph_proc (call_tree_layout_walker, NULL);
}


void menu_tree_layout_walker_left_to_right (Menu menu, Menu_item menu_item)
{ 
	save_tree_layout_walker_settings ();
	call_sgraph_proc (call_tree_layout_walker_left_to_right, NULL);
}


void menu_tree_layout_walker_settings (Menu menu, Menu_item menu_item)
{ 
	save_tree_layout_walker_settings ();
	show_tree_layout_walker_subframe (NULL);
}
