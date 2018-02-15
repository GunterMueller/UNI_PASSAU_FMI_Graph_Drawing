
/*************************************************************************** */
/***                                                                      ** */
/*** Filename: WOODS_SF.C                                                 ** */
/***                                                                      ** */
/*** ******************************************************************** ** */
/***                                                                      ** */
/*** Description: The functions in this file are used to pop up a window  ** */
/***              (to set the horizontal and vertical distance between    ** */
/***              the gridpoints) when the Woods-algorithm is activated   ** */
/***              by clicking the left mouse button and holding down the  ** */
/***              CTRL-key at the same time                               ** */
/***                                                                      ** */
/*** Date: 18.5.1994                                                      ** */
/***                                                                      ** */
/*************************************************************************** */
/*                                                                           */


#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "woods_planar_layout_export.h"
#include <xview/xview.h>
#include <xview/panel.h>
#include "graphed/gridder.h"
#include "graphed/graphed_pin_sf.h"

static Gridder		vertical_gridder,
                        horizontal_gridder;

static	void	create_woods_subframe (void);
static	void	woods_sf_set   (Panel_item item, Event *event);
static	void	woods_sf_reset (Panel_item item, Event *event);
static	void	woods_sf_do    (Panel_item item, Event *event);
static	void	woods_sf_done  (Frame frame);

Woods_settings woods_settings;


Woods_settings	init_woods_settings (void)
{
	Woods_settings	settings;

	settings.size_defaults_x = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.size_defaults_y = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.vertical_distance = 64;
	settings.horizontal_distance = 64;

	return	settings;
}


static	Graphed_pin_subframe	woods_sf = (Graphed_pin_subframe)NULL;


static	void	create_woods_subframe(void)
{
	int row_count = 0;

	if (woods_sf == (Graphed_pin_subframe)NULL) {
		woods_sf = new_graphed_pin_subframe((Frame)0);
	}
	woods_sf->set_proc   = woods_sf_set;
	woods_sf->reset_proc = woods_sf_reset;
	woods_sf->do_proc    = woods_sf_do;
	woods_sf->done_proc  = woods_sf_done;

	graphed_create_pin_subframe (woods_sf, "Planar / Woods Layout");

	row_count ++;
	vertical_gridder = create_gridder(woods_sf->panel,
		GRIDDER_HEIGHT,
		"vertical distance",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);

	row_count += 2;
	horizontal_gridder = create_gridder(woods_sf->panel,
		GRIDDER_WIDTH,
		"horizontal distance",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);

	window_fit (woods_sf->panel);
	window_fit (woods_sf->frame);
}


void	show_woods_subframe(void (*done_proc) ())
{
	if (!showing_woods_subframe()) {
		create_woods_subframe ();
	}
	woods_sf->graphed_done_proc = done_proc;

	if (woods_sf != (Graphed_pin_subframe)NULL) {

		gridder_set(vertical_gridder,woods_settings.size_defaults_y,
			woods_settings.vertical_distance);
		gridder_set(horizontal_gridder,woods_settings.size_defaults_x,
			woods_settings.horizontal_distance);

		compute_subwindow_position_at_graph_of_current_selection(
			woods_sf->frame);

		xv_set(woods_sf->frame ,WIN_SHOW, TRUE, NULL);
		woods_sf->showing = TRUE;
	}
}


int	showing_woods_subframe (void)
{
	return showing_graphed_pin_subframe (woods_sf);
}


void save_woods_settings(void)
{
	if(woods_sf != (Graphed_pin_subframe)NULL && woods_sf->showing) {
		woods_settings.vertical_distance   = gridder_get_size (vertical_gridder);
		woods_settings.horizontal_distance = gridder_get_size (horizontal_gridder);
		woods_settings.size_defaults_y = (int)gridder_get_value (vertical_gridder);
		woods_settings.size_defaults_x = (int)gridder_get_value (horizontal_gridder);
	} else {
		if(woods_settings.size_defaults_y != GRIDDER_DISTANCE_OTHER) {
			woods_settings.vertical_distance = recompute_gridder_size (
				NULL,
				woods_settings.size_defaults_y,
				GRIDDER_HEIGHT);
		}

		if(woods_settings.size_defaults_x != GRIDDER_DISTANCE_OTHER) {
			woods_settings.horizontal_distance = recompute_gridder_size (
				NULL,
				woods_settings.size_defaults_x,
				GRIDDER_WIDTH);
		}
	}
}


static	void	woods_sf_set (Panel_item item, Event *event)
{
	save_woods_settings ();
}


static	void	woods_sf_reset (Panel_item item, Event *event)
{
}


static	void	woods_sf_do (Panel_item item, Event *event)
{
	save_woods_settings ();
	call_sgraph_proc (call_woods_planar_layout, NULL);
}


static	void	woods_sf_done(Frame frame)
{
	save_woods_settings();
	free(vertical_gridder);
	free(horizontal_gridder);
}
