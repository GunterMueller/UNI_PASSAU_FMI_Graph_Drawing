/* This software is distributed under the Lesser General Public License */
#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/notice.h>

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include <math.h>
#include <graphed/gridder.h>
#include <graphed/graphed_pin_sf.h>
#include "springembedder_rf_export.h"

Springembedder_rf_settings	springembedder_rf_settings;

Springembedder_rf_settings	init_springembedder_rf_settings (int weighted,
	double max_force, double vibration, int max_iter, int edgelen)
{
	Springembedder_rf_settings settings;

	settings.draw_weighted = weighted;
	settings.max_force = float(max_force);
	settings.opt_distance = float(edgelen);
	settings.vibration = float(vibration);
	settings.max_iterations = max_iter;
	settings.animation_intervals = 10;

	settings.animation = FALSE;
	settings.opt_distance_gridder = GRIDDER_DISTANCE_2_LARGEST_SIZE;

	return	settings;
}

