/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
#include <xview/xview.h>
#include <xview/panel.h>

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/algorithms.h>
#include "sugiyama_export.h"
#include "graphed/gridder.h"
#include <graphed/graphed_pin_sf.h>

#include <lsd/ge_dummy.h>

Sugiyama_settings	sugiyama_settings;


void	show_sugiyama_subframe (void * /* done_proc */)
{
}

// Sugiyama_settings	init_sugiyama_settings (void)
// {
// 	Sugiyama_settings settings;

// 	settings.vertical_distance   = 64;
// 	settings.horizontal_distance = 64;
// 	settings.it1 = 10;
// 	settings.it2 = 3;
// 	settings.size_defaults_x = GRIDDER_DISTANCE_2_LARGEST_SIZE;
// 	settings.size_defaults_y = GRIDDER_DISTANCE_2_LARGEST_SIZE;
// 	settings.leveling = 1;
// 	settings.up       = 1;
// 	settings.reduce_crossings_algorithm = 0;
// 	settings.mult_distance    = 1;
// 	settings.width            = 1000;

// 	return settings;
// }


Sugiyama_settings init_sugiyama_settings (int vert_dist,
    int horiz_dist, int /* it1 */, int /* it2 */, int arrange, int res_cycles,
    int reduce_cross)
{
	Sugiyama_settings settings;

	settings.vertical_distance   = vert_dist;
	settings.horizontal_distance = horiz_dist;
	settings.it1 = 10;
	settings.it2 = 3;
	settings.size_defaults_x = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.size_defaults_y = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.leveling = arrange;
	settings.up       = res_cycles;
	settings.reduce_crossings_algorithm = reduce_cross;
	settings.mult_distance    = 1;
	settings.width            = 1000;

	return settings;
}


void	save_sugiyama_settings (void)
{
	sugiyama_settings.vertical_distance   = 64;
	sugiyama_settings.horizontal_distance = 64;
	sugiyama_settings.it1 = 10;
	sugiyama_settings.it2 = 3;
	sugiyama_settings.size_defaults_x = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	sugiyama_settings.size_defaults_y = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	sugiyama_settings.leveling = 1;
	sugiyama_settings.up       = 1;
	sugiyama_settings.reduce_crossings_algorithm = 0;
	sugiyama_settings.mult_distance    = 1;
	sugiyama_settings.width            = 1000;


	sugiyama_settings.vertical_distance   = get_current_node_height ();
	sugiyama_settings.horizontal_distance = get_current_node_width ();
	sugiyama_settings.vertical_distance   = 64;
	sugiyama_settings.horizontal_distance = 64;
}





