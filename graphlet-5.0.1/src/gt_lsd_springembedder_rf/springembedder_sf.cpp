/* This software is distributed under the Lesser General Public License */
#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/notice.h>

#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>
#include "graphed/gridder.h"
#include "graphed/graphed_pin_sf.h"
#include "springembedder_rf_export.h"

extern	Frame	base_frame;

static	void	springembedder_rf_sf_set   (Panel_item item, Event *event);
static	void	springembedder_rf_sf_reset (Panel_item item, Event *event);
static	void	springembedder_rf_sf_do    (Panel_item item, Event *event);
static	void	springembedder_rf_sf_done  (Frame frame);

int	check_springembedder_rf_settings (void);

static	Graphed_pin_subframe	spring_sf = (Graphed_pin_subframe)NULL;

static	Panel_item	springembedder_animation,
			springembedder_draw_weighted,
			springembedder_max_force,
			springembedder_vibration,
			springembedder_max_iter,
			springembedder_anim_interval;
Gridder			springembedder_opt_distance_gridder;

Springembedder_rf_settings	springembedder_rf_settings;

Springembedder_rf_settings	init_springembedder_rf_settings (void)
{
	Springembedder_rf_settings settings;

	settings.draw_weighted = FALSE;
	settings.max_force = 3.0;
	settings.opt_distance = 64.0;
	settings.vibration = 0.4;
	settings.max_iterations = 1000;
	settings.animation_intervals = 10;

	settings.animation = TRUE;
	settings.opt_distance_gridder = GRIDDER_DISTANCE_2_LARGEST_SIZE;

	return	settings;
}



static	char	*float_to_ascii_2 (float f)
{
	static	char	buf[50];
	
	sprintf (buf, "%.2f", f);
	
	return buf;
}


static	void	create_springembedder_subframe(void)
{
	int	row_count = 0;
	
	if (spring_sf == (Graphed_pin_subframe)NULL) {
		spring_sf = new_graphed_pin_subframe((Frame)0);
	}
	spring_sf->set_proc   = springembedder_rf_sf_set;
	spring_sf->reset_proc = springembedder_rf_sf_reset;
	spring_sf->do_proc    = springembedder_rf_sf_do;
	spring_sf->done_proc  = springembedder_rf_sf_done;

	graphed_create_pin_subframe (spring_sf, "Spring Embedder (FR)");

	row_count = 1;
	springembedder_animation = xv_create(
		spring_sf->panel, 	PANEL_CHECK_BOX,
		PANEL_LABEL_STRING,	"anination",
		XV_X,			xv_col (spring_sf->panel, 0),
		XV_Y,              	xv_row (spring_sf->panel, row_count),
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_VALUE,		springembedder_rf_settings.animation,
		0);


	row_count += 1;
	springembedder_draw_weighted = xv_create(
		spring_sf->panel,	PANEL_CHECK_BOX,
		XV_X,			xv_col (spring_sf->panel, 0),
		XV_Y,              	xv_row (spring_sf->panel, row_count),
		PANEL_LABEL_STRING,	"Weighted Layout",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_VALUE,		springembedder_rf_settings.draw_weighted,
		0);


	row_count += 1;
	springembedder_max_force = xv_create(
		spring_sf->panel,	PANEL_TEXT,
		XV_X,			xv_col (spring_sf->panel, 0),
		XV_Y,              	xv_row (spring_sf->panel, row_count),
		PANEL_LABEL_STRING,	"Maximum Force",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_VALUE,		float_to_ascii_2(springembedder_rf_settings.max_force),
		PANEL_VALUE_STORED_LENGTH,  10,
		PANEL_VALUE_DISPLAY_LENGTH, 10,
		0);

		
	row_count += 1;
	springembedder_vibration = xv_create(
		spring_sf->panel,	PANEL_TEXT,
		XV_X,			xv_col (spring_sf->panel, 0),
		XV_Y,              	xv_row (spring_sf->panel, row_count),
		PANEL_LABEL_STRING,	"Vibration",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_VALUE,		float_to_ascii_2(springembedder_rf_settings.vibration),
		PANEL_VALUE_STORED_LENGTH,  10,
		PANEL_VALUE_DISPLAY_LENGTH, 10,
		0);

		
	row_count += 1;
	springembedder_max_iter = xv_create(
		spring_sf->panel,	PANEL_TEXT,
		XV_X,			xv_col (spring_sf->panel, 0),
		XV_Y,			xv_row (spring_sf->panel, row_count),
		PANEL_LABEL_STRING,	"Iterations (max)",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_VALUE,		int_to_ascii(springembedder_rf_settings.max_iterations),
		PANEL_VALUE_STORED_LENGTH,  10,
		PANEL_VALUE_DISPLAY_LENGTH, 10,
		0);

		
	row_count += 1;
	springembedder_anim_interval = xv_create(
		spring_sf->panel,	PANEL_TEXT,
		XV_X,			xv_col (spring_sf->panel, 0),
		XV_Y,			xv_row (spring_sf->panel, row_count),
		PANEL_LABEL_STRING,	"Animate after iterations",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_VALUE,		int_to_ascii(springembedder_rf_settings.animation_intervals),
		PANEL_VALUE_STORED_LENGTH,  10,
		PANEL_VALUE_DISPLAY_LENGTH, 10,
		0);

	row_count +=1;
	springembedder_opt_distance_gridder = create_gridder (
		spring_sf->panel,
		GRIDDER_MAX_OF_BOTH,
		"optimal distance",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);
		

	window_fit(spring_sf->panel);
	window_fit(spring_sf->frame);
}


void	show_springembedder_rf_subframe (void (*done_proc) ())
{
	if (!showing_springembedder_rf_subframe()) {
		create_springembedder_subframe();
	}
	spring_sf->graphed_done_proc = done_proc;
	
	compute_subwindow_position_at_graph_of_current_selection (
		spring_sf->frame);
	gridder_set (springembedder_opt_distance_gridder,
		springembedder_rf_settings.opt_distance,
		(int) springembedder_rf_settings.opt_distance);
	
	xv_set (spring_sf->frame, WIN_SHOW, TRUE, 0);
	spring_sf->showing = TRUE;
}


int	showing_springembedder_rf_subframe(void)
{
	return	showing_graphed_pin_subframe(spring_sf);
}



static	void	springembedder_rf_sf_set (Panel_item item, Event *event)
{
	save_springembedder_rf_settings ();
}


static	void	springembedder_rf_sf_reset (Panel_item item, Event *event)
{
}


static	void	springembedder_rf_sf_do (Panel_item item, Event *event)
{
	save_springembedder_rf_settings();
	check_springembedder_rf_settings();
	
	if (springembedder_rf_settings.animation) {
		call_sgraph_proc (call_animation_springembedder_rf, NULL);
	} else {
		call_sgraph_proc (call_fast_springembedder_rf, NULL);
	}
}


static	void	springembedder_rf_sf_done (Frame frame)
{
	save_springembedder_rf_settings ();
	
	free (springembedder_opt_distance_gridder);
	springembedder_opt_distance_gridder = NULL;
}


void	save_springembedder_rf_settings (void)
{
	if (showing_springembedder_rf_subframe()) {

		springembedder_rf_settings.animation = (int) 
			panel_get_value(springembedder_animation) ;
		springembedder_rf_settings.draw_weighted = (int) 
			panel_get_value(springembedder_draw_weighted) ;
		springembedder_rf_settings.max_force = atof ((char *)
			panel_get_value(springembedder_max_force));
		springembedder_rf_settings.opt_distance = (float)
			gridder_get_size(springembedder_opt_distance_gridder);
		springembedder_rf_settings.vibration = atof ((char *)
			panel_get_value(springembedder_vibration));
		springembedder_rf_settings.max_iterations = atoi ((char *)
			panel_get_value(springembedder_max_iter));
		springembedder_rf_settings.animation_intervals = atoi ((char *)
			panel_get_value(springembedder_anim_interval));
		springembedder_rf_settings.opt_distance_gridder =
			gridder_get_value(springembedder_opt_distance_gridder);

	 } else {

		if (springembedder_rf_settings.opt_distance_gridder !=
		    GRIDDER_DISTANCE_OTHER) 
			springembedder_rf_settings.opt_distance = recompute_gridder_size (
				NULL,
				springembedder_rf_settings.opt_distance_gridder,
				GRIDDER_MAX_OF_BOTH);
	}
}


void menu_springembedder_rf_fast_springembedder (Menu menu, Menu_item menu_item)
{	
	save_springembedder_rf_settings ();

	if (check_springembedder_rf_settings()) {
		call_sgraph_proc (call_fast_springembedder_rf, NULL);
	}

}


void menu_springembedder_rf_animation_springembedder (Menu menu, Menu_item menu_item)
{	
	save_springembedder_rf_settings();
	if (check_springembedder_rf_settings()) {
		call_sgraph_proc (call_animation_springembedder_rf, NULL);
	}

}


void menu_springembedder_rf_show_subframe (Menu menu, Menu_item menu_item)
{
	save_springembedder_rf_settings ();
	show_springembedder_rf_subframe (NULL);

}



int	check_springembedder_rf_settings (void)
{
	Frame		frame;

	frame = iif (showing_springembedder_rf_subframe(),
		spring_sf->frame,
		base_frame);

	if (springembedder_rf_settings.opt_distance_gridder == GRIDDER_DISTANCE_NONE || springembedder_rf_settings.opt_distance == 0) {

		notice_prompt(frame, (Event*)NULL,
			NOTICE_MESSAGE_STRINGS,
				"Error in settings :",
				"The optimal distance  must not be zero",
				NULL,
			NOTICE_BUTTON_YES, "Ok",
			NULL);

		return FALSE;

        } else if (springembedder_rf_settings.opt_distance < 0.0 || 
	           springembedder_rf_settings.max_force <= 0.0 ||
	           springembedder_rf_settings.vibration <= 0.0 ||
	           springembedder_rf_settings.max_iterations  <= 0.0 ||
	           springembedder_rf_settings.animation_intervals <= 0.0 ) {

		notice_prompt(frame, (Event*)NULL,
			NOTICE_MESSAGE_STRINGS,
				"Error in settings :",
				"All values must be numbers greater than zero",
				NULL,
			NOTICE_BUTTON_YES, "Ok",
		NULL);
		return FALSE;

        }

	return TRUE;
}
