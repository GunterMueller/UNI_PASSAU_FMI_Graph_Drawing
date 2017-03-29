/* This software is distributed under the Lesser General Public License */
#include <xview/xview.h>
#include <xview/panel.h>
 
#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/algorithms.h>
#include "tree_layout_walker_export.h"
#include <graphed/gridder.h>
#include <graphed/graphed_pin_sf.h>
 
Tree_layout_walker_settings tree_layout_walker_settings;

void	save_tree_layout_walker_settings (void)
{
	tree_layout_walker_settings.size_defaults_x_sibling = GRIDDER_DISTANCE_1_DEFAULT_SIZE;
	tree_layout_walker_settings.size_defaults_x_subtree = GRIDDER_DISTANCE_1_DEFAULT_SIZE;
	tree_layout_walker_settings.size_defaults_y         = GRIDDER_DISTANCE_15_DEFAULT_SIZE;
}

/*
void	show_tree_layout_walker_subframe (void (*graphed_done_proc) ())
{
}
*/
