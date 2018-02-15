/****************************************************************************\
 *                                                                          *
 *  nonplanarity_settings.c                                                 *
 *  -----------------------                                                 *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/

#include <std.h>
#include <slist.h>
#include <sgraph.h>

#include "nonplanarity_settings.h"


Global	MaxPlanarSettings	create_and_init_maxplanar_settings(void)
{
   MaxPlanarSettings	maxplanarsettings;

   maxplanarsettings = NEW_MAXPLANAR_SETTINGS;
   maxplanarsettings->graph_is_already_planar = FALSE;
   maxplanarsettings->create_new_window_for_mpg = FALSE;
   maxplanarsettings->use_planar_embedding = FALSE;
   maxplanarsettings->index_for_edgestyle = MY_EDGESTYLE_DASHED;
   maxplanarsettings->algorithm_to_run = MPG_JAYAKUMAR;
   maxplanarsettings->deleted_edge_count = 0;
   maxplanarsettings->re_inserted_edge_count = 0;
   maxplanarsettings->iterations_for_randomized_greedy = 50;

   return maxplanarsettings;
}
