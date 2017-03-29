/*===========================================================================*/
/*  
	 PROJECT	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_sf.
	 AUTHOR:       	Roland Stuempfl (diploma 1994)

	 Overview
	 ========
	 Source code for setting parameters of the algorithm upward_draw
	 presented by Guiseppe Di Battista and Roberto Tamassia, published
	 in "Algorithms For Plane Representations of Acyclic Digraphs",
	 Theoretical Computer Science 61 (1988), pp. 175-198.
	 In the following this work will be referenced as [DiBatTam88].
*/
/*===========================================================================*/

#include <xview/xview.h>
#include <xview/panel.h>

#include <math.h>

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/graphed.h>
#include <sgraph/algorithms.h>
#include "st_upward_draw_export.h"
#include <graphed/graphed_pin_sf.h>
#include <graphed/gridder.h>

typedef void (*voidproc)();

static	void	create_st_upward_draw_subframe (void);
static	void	st_upward_draw_sf_set(Panel_item item, Event *event);
static	void	st_upward_draw_sf_reset(Panel_item item, Event *event);
static	void	st_upward_draw_sf_do(Panel_item item, Event *event);
static	void	st_upward_draw_sf_done(Frame frame);

static	Panel_item	alpha_pi,
			select_candidate_mode_pi,
			max_len_pi,
			t_div_pi,
			max_node_dist_pi;

static	Gridder		vertical_gridder,
			horizontal_gridder;

st_Upward_Draw_Settings	st_settings;

/*****************************************************************************/
/*
	st_Upward_Draw_Settings	init_st_settings()

	Initialises the parameter settings. This function is called before all
	other function.
*/
/*****************************************************************************/

st_Upward_Draw_Settings	init_st_settings(void)
{
	st_Upward_Draw_Settings settings;

	settings.alpha = 120;
	settings.max_len = 75;
	settings.t_div = 50;
	settings.max_node_dist = 1000;
	settings.select_candidate_mode = SCM_RANDOM_CANDIDATE;
	settings.size_x = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.size_y = GRIDDER_DISTANCE_2_LARGEST_SIZE;

	return (settings);
}

static	Graphed_pin_subframe	st_sf = (Graphed_pin_subframe)NULL;

/*****************************************************************************/
/*
	static	void	create_st_upward_draw_subframe()

	Creates the pin subframe where the settings could be changed.
*/
/*****************************************************************************/

static	void	create_st_upward_draw_subframe(void)
{
	int	row_count = 0;
	Panel_item message;

	if (st_sf == (Graphed_pin_subframe)NULL) {
		st_sf = new_graphed_pin_subframe((Frame)0);
	}
	st_sf->set_proc 	= st_upward_draw_sf_set;
	st_sf->reset_proc	= st_upward_draw_sf_reset;
	st_sf->do_proc 	= st_upward_draw_sf_do;
	st_sf->done_proc 	= st_upward_draw_sf_done;

	graphed_create_pin_subframe (st_sf,"st upward draw");

	row_count++;

	select_candidate_mode_pi = (Panel_item)xv_create(st_sf->panel, PANEL_CHOICE_STACK,
		XV_X, xv_col(st_sf->panel, 0),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "Mode of selecting candidate ",
		PANEL_CHOICE_STRINGS, "candidate with actual highest rank",
				      "candidate with actual middle rank",
				      "candidate with actual lowest rank",
				      "any random candidate", NULL,
		PANEL_VALUE, st_settings.select_candidate_mode,
		NULL);
	
	row_count++;
	row_count++;
	
	alpha_pi = (Panel_item)xv_create(st_sf->panel, PANEL_NUMERIC_TEXT,
		XV_X, xv_col(st_sf->panel, 0),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "Tolerance angle ",
		PANEL_LABEL_BOLD, TRUE,
		PANEL_VALUE, st_settings.alpha,
		PANEL_MIN_VALUE, 1,
		PANEL_MAX_VALUE, 359,
		PANEL_VALUE_STORED_LENGTH, 3,
		PANEL_VALUE_DISPLAY_LENGTH, 3,
		NULL);

	row_count++;

	message = (Panel_item) xv_create(st_sf->panel, PANEL_MESSAGE,
		XV_X, xv_col(st_sf->panel, 5),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "(this value will internally be halved)",
		NULL);

	row_count++;
	row_count++;

	max_node_dist_pi = (Panel_item)xv_create(st_sf->panel, PANEL_NUMERIC_TEXT,
		XV_X, xv_col(st_sf->panel, 0),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "Overall maximal node distance ",
		PANEL_LABEL_BOLD, TRUE,
		PANEL_VALUE, st_settings.max_node_dist,
		PANEL_MIN_VALUE, 100,
		PANEL_MAX_VALUE, 32000,
		PANEL_VALUE_STORED_LENGTH, 5,
		PANEL_VALUE_DISPLAY_LENGTH, 5,
		NULL);

	row_count++;

	message = (Panel_item) xv_create(st_sf->panel, PANEL_MESSAGE,
		XV_X, xv_col(st_sf->panel, 5),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "(distance between any two nodes)",
		NULL);

	row_count++;

	max_len_pi = (Panel_item)xv_create(st_sf->panel, PANEL_NUMERIC_TEXT,
		XV_X, xv_col(st_sf->panel, 0),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "Maximal internal node distance ",
		PANEL_LABEL_BOLD, TRUE,
		PANEL_VALUE, st_settings.max_len,
		PANEL_MIN_VALUE, 1,
		PANEL_MAX_VALUE, 200,
		PANEL_VALUE_STORED_LENGTH, 3,
		PANEL_VALUE_DISPLAY_LENGTH, 3,
		NULL);

	row_count++;

	message = (Panel_item) xv_create(st_sf->panel, PANEL_MESSAGE,
		XV_X, xv_col(st_sf->panel, 5),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "(% of overall maximal node distance)",
		NULL);

	row_count++;
	row_count++;

	t_div_pi = (Panel_item)xv_create(st_sf->panel, PANEL_NUMERIC_TEXT,
		XV_X, xv_col(st_sf->panel, 0),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "Decreasing internal edge length while placing ",
		PANEL_LABEL_BOLD, TRUE,
		PANEL_VALUE, st_settings.t_div,
		PANEL_MIN_VALUE, 1,
		PANEL_MAX_VALUE, 99,
		PANEL_VALUE_STORED_LENGTH, 2,
		PANEL_VALUE_DISPLAY_LENGTH, 2,
		NULL);

	row_count++;

	message = (Panel_item) xv_create(st_sf->panel, PANEL_MESSAGE,
		XV_X, xv_col(st_sf->panel, 5),
		XV_Y, xv_row(st_sf->panel, row_count),
		PANEL_LABEL_STRING, "(% of last tried edge length)",
		NULL);

	row_count++;
	row_count++;

	vertical_gridder = create_gridder (st_sf->panel,
		GRIDDER_HEIGHT,
		"Minimal vertical node distance",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		10,
		row_count);

	row_count += 2;
	horizontal_gridder = create_gridder (st_sf->panel,
		GRIDDER_WIDTH,
		"Minimal horizontal node distance",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		10,
		row_count);

	window_fit(st_sf->panel);
	window_fit(st_sf->frame);
}

/*****************************************************************************/
/*
	void	show_st_subframe (done_proc)
	void	(*done_proc);

	Shows the settings pin subframe.
*/
/*****************************************************************************/
void	show_st_subframe (void *done_proc)
{
	if (st_sf == (Graphed_pin_subframe)NULL || !st_sf->showing) {
		create_st_upward_draw_subframe();
	}
	st_sf->graphed_done_proc = (voidproc)done_proc;

	if (st_sf != (Graphed_pin_subframe)NULL) {
		xv_set(alpha_pi, PANEL_VALUE,
			st_settings.alpha, NULL);
		xv_set(max_len_pi, PANEL_VALUE,
			st_settings.max_len, NULL);
		xv_set(max_node_dist_pi, PANEL_VALUE,
			st_settings.max_node_dist, NULL);
		xv_set(t_div_pi, PANEL_VALUE,
			st_settings.t_div, NULL);
		xv_set(select_candidate_mode_pi, PANEL_VALUE,
			st_settings.select_candidate_mode, NULL);

		gridder_set (vertical_gridder,
			st_settings.size_y,
			st_settings.vertical_dist);
		gridder_set (horizontal_gridder,
			st_settings.size_x,
			st_settings.horizontal_dist);

		compute_subwindow_position_at_graph_of_current_selection(st_sf->frame);
		xv_set(st_sf->frame, WIN_SHOW, TRUE, NULL);

		st_sf->showing = TRUE;
	}
}

/*****************************************************************************/
/*
	static	void	st_upward_draw_sf_set (item, event)
	Panel_item	item;
	Event		*event;

	The set procedure of the pin subframe.
*/
/*****************************************************************************/

static	void	st_upward_draw_sf_set (Panel_item item, Event *event)
{
	save_st_settings();
}

/*****************************************************************************/
/*
	static	void	st_upward_draw_sf_reset (item, event)
	Panel_item	item;
	Event		*event;

	The reset procedure of the pin subframe.
*/
/*****************************************************************************/

static	void	st_upward_draw_sf_reset (Panel_item item, Event *event)
{
}

/*****************************************************************************/
/*
	static	void	st_upward_draw_sf_do (item, event)
	Panel_item	item;
	Event		*event;

	The do procedure of the pin subframe.
*/
/*****************************************************************************/

static	void	st_upward_draw_sf_do (Panel_item item, Event *event)
{
	save_st_settings();
	call_sgraph_proc(call_st_upward_draw_layout, NULL);
}

/*****************************************************************************/
/*
	static	void	st_upward_draw_sf_done (frame)
	Frame		frame;

	The done procedure of the pin subframe.
*/
/*****************************************************************************/

static	void	st_upward_draw_sf_done (Frame frame)
{
	save_st_settings();
}

/*****************************************************************************/
/*
	void	save_st_settings ()

	Saves the actual settings if a button of the pin subframe is pressed.
*/
/*****************************************************************************/

void	save_st_settings (void)
{
	if (st_sf != (Graphed_pin_subframe)NULL && st_sf->showing) {
		st_settings.alpha = (int) xv_get (alpha_pi, PANEL_VALUE);
		st_settings.max_len = (int) xv_get (max_len_pi, PANEL_VALUE);
		st_settings.t_div = (int) xv_get (t_div_pi, PANEL_VALUE);
		st_settings.max_node_dist = (int) xv_get (max_node_dist_pi, PANEL_VALUE);
		st_settings.select_candidate_mode = (int) xv_get (select_candidate_mode_pi, PANEL_VALUE);

		st_settings.horizontal_dist =
			gridder_get_size (horizontal_gridder);
		st_settings.vertical_dist =
			gridder_get_size (vertical_gridder);
		st_settings.size_x = 
			(int)gridder_get_value(horizontal_gridder);
		st_settings.size_y = 
			(int)gridder_get_value(vertical_gridder);
	} else {
		if (st_settings.size_y != GRIDDER_DISTANCE_OTHER) {
			st_settings.vertical_dist = 
				recompute_gridder_size(
					NULL,
					st_settings.size_y,
					GRIDDER_HEIGHT);
		}
		if (st_settings.size_x != GRIDDER_DISTANCE_OTHER) {
			st_settings.horizontal_dist =
				recompute_gridder_size (
					NULL,
					st_settings.size_x,
					GRIDDER_WIDTH);
		}
	}
	if (st_settings.vertical_dist == 0)
		st_settings.vertical_dist = get_current_node_height();
	if (st_settings.horizontal_dist == 0)
		st_settings.horizontal_dist = get_current_node_width();
	if (st_settings.alpha <= 0 || st_settings.alpha >= 360)
		st_settings.alpha = 120;
	if (st_settings.t_div <= 0 || st_settings.alpha >= 100)
		st_settings.t_div = 75;
	if (st_settings.max_len <= 0) 
		st_settings.max_len = 100;
	if (st_settings.max_node_dist > 32768) 
		st_settings.max_node_dist = 4000;
}
