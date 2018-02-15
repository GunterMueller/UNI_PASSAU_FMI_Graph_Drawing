/* (C) Universitaet Passau 1986-1994 */
/* A clone of the sugyiama subframe */
#include <xview/xview.h>
#include <xview/panel.h>

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>

#include <graphed/menu.h>
#include "layout_suite_export.h"
#include <graphed/graphed_pin_sf.h>


extern	Frame	base_frame;
extern	char	*int_to_ascii (int);
extern	void	single_layout_suite (LS_algorithm algorithm, Layout_suite_settings settings);


static	void	create_suite_subframe (void);
static	void	suite_sf_set   (Panel_item item, Event *event);
/*static	void	suite_sf_reset (Panel_item item, Event *event);*/
static	void	suite_sf_do    (Panel_item item, Event *event);
static	void	suite_sf_done  (Frame frame);

static	void	suite_select_algorithm (Panel_item item, int value, Event *event);
static	void	suite_show_algorithm_settings (Panel_item item, Event *event);
static	void	suite_do_algorithm (Panel_item item, Event *event);

static	Graphed_pin_subframe	suite_subframe = (Graphed_pin_subframe)NULL;
static	Panel_item	file_options;
static	Panel_item	save_and_reload;
static	Panel_item	label_graph;


#define CREATE_GRAPH_FILE (0x1)
#define CREATE_INFO_FILE  (CREATE_GRAPH_FILE << 1)
#define CREATE_RASTERFILE (CREATE_INFO_FILE  << 1)
#define CREATE_POSTSCRIPT_FILE (CREATE_RASTERFILE << 1)

void	save_layout_suite_settings (void)
{
	if (showing_layout_suite_subframe()) {
		layout_suite_settings.create_graph_file = iif(
			(xv_get (file_options, PANEL_VALUE) & CREATE_GRAPH_FILE) != 0, TRUE, FALSE);
		layout_suite_settings.create_info_file = iif(
			(xv_get (file_options, PANEL_VALUE) & CREATE_INFO_FILE) != 0, TRUE, FALSE);
		layout_suite_settings.create_xbitmap_file = iif(
 			(xv_get (file_options, PANEL_VALUE) & CREATE_RASTERFILE) != 0, TRUE, FALSE);
		layout_suite_settings.create_postscript_file = iif(
			(xv_get (file_options, PANEL_VALUE) & CREATE_POSTSCRIPT_FILE) != 0, TRUE, FALSE);
		layout_suite_settings.save_and_reload = xv_get (save_and_reload, PANEL_VALUE);
		layout_suite_settings.label_graph = xv_get (label_graph, PANEL_VALUE);
	}
}


Layout_suite_settings	init_layout_suite_settings (void)
{
	Layout_suite_settings	settings;

	settings.algorithms = empty_slist;

	settings.create_graph_file      = FALSE;
	settings.create_info_file       = TRUE;
	settings.create_xbitmap_file      = TRUE;
	settings.create_postscript_file = TRUE;
	settings.save_and_reload        = TRUE;
	settings.label_graph            = FALSE;
	return settings;
}


static	void	create_suite_subframe(void)
{
	Layout_suite_settings	settings;
	Slist			l;
	LS_algorithm		algorithm;
	int			row_count;
	int			max_name_width;


	settings = layout_suite_settings;
	suite_subframe = new_graphed_pin_subframe((Frame)0);
	suite_subframe->set_proc   = suite_sf_set;
	suite_subframe->reset_proc = (Pointer_to_procedure)NULL;
	suite_subframe->do_proc    = suite_sf_do;
	suite_subframe->done_proc  = suite_sf_done;
	suite_subframe->do_label   = "run all";

	graphed_create_pin_subframe (suite_subframe, "Layout suite");

	row_count = 1;
	file_options = xv_create (suite_subframe->panel, PANEL_TOGGLE,
		XV_X, xv_col (suite_subframe->panel, 0),
		XV_Y, xv_row (suite_subframe->panel, row_count),
		PANEL_LABEL_STRING, "Create File",
		PANEL_CHOICE_STRINGS,
			"Graph", "Info", "Rasterfile", "Postscript", NULL,
		PANEL_VALUE,
			iif(settings.create_graph_file, CREATE_GRAPH_FILE, 0) |
			iif(settings.create_info_file,  CREATE_INFO_FILE, 0) |
			iif(settings.create_xbitmap_file, CREATE_RASTERFILE, 0) |
			iif(settings.create_postscript_file, CREATE_POSTSCRIPT_FILE, 0),
		NULL);

	row_count ++;
	save_and_reload = xv_create (suite_subframe->panel, PANEL_CHECK_BOX,
		XV_X, xv_col (suite_subframe->panel, 0),
		XV_Y, xv_row (suite_subframe->panel, row_count),
		PANEL_LABEL_STRING, "Save and Reload",
		PANEL_VALUE, settings.save_and_reload,
		NULL);

	row_count ++;
	label_graph = xv_create (suite_subframe->panel, PANEL_CHECK_BOX,
		XV_X, xv_col (suite_subframe->panel, 0),
		XV_Y, xv_row (suite_subframe->panel, row_count),
		PANEL_LABEL_STRING, "Label graph (files only)",
		PANEL_VALUE, settings.label_graph,
		NULL);

	row_count ++; max_name_width = 0;
	for_slist (settings.algorithms, l) {

		Panel_item	algorithm_active;
		Panel_item	algorithm_name;
		Panel_item	algorithm_settings;
		Panel_item	algorithm_do;
		int		name_width;

		algorithm = ls_list_algorithm(l);
		
		row_count ++;
		algorithm_active = xv_create (suite_subframe->panel, PANEL_CHECK_BOX,
			XV_X,                  xv_col (suite_subframe->panel, 0),
			XV_Y,	               xv_row (suite_subframe->panel, row_count) - 5,
			PANEL_CLIENT_DATA,     algorithm,
			PANEL_NOTIFY_PROC,     suite_select_algorithm,
			PANEL_VALUE,           iif (algorithm->active, 1, 0),
			NULL);

		algorithm_name = xv_create (suite_subframe->panel, PANEL_MESSAGE,
			XV_X,                  xv_col (suite_subframe->panel, 3),
			XV_Y,	               xv_row (suite_subframe->panel, row_count) ,
			PANEL_LABEL_STRING,    algorithm->full_name,
			NULL);

		name_width = ((Rect *)xv_get (algorithm_name, XV_RECT))->r_width;
		max_name_width = maximum (name_width, max_name_width);

		algorithm_settings = xv_create (suite_subframe->panel, PANEL_BUTTON,
			XV_X,               xv_col (suite_subframe->panel, 10),
			XV_Y,	            xv_row (suite_subframe->panel, row_count),
			PANEL_LABEL_STRING, "settings ...",
			PANEL_CLIENT_DATA,  algorithm,
			PANEL_NOTIFY_PROC,  suite_show_algorithm_settings,
			NULL);

		algorithm_do = xv_create (suite_subframe->panel, PANEL_BUTTON,
			XV_X,               xv_col (suite_subframe->panel, 10),
			XV_Y,	            xv_row (suite_subframe->panel, row_count),
			PANEL_LABEL_STRING, "run",
			PANEL_CLIENT_DATA,  algorithm,
			PANEL_NOTIFY_PROC,  suite_do_algorithm,
			NULL);

		ls_algorithm_set (algorithm,
			LS_ACTIVE_ITEM,	  algorithm_active,
			LS_SETTINGS_ITEM, algorithm_settings,
			LS_DO_ITEM,       algorithm_do,
			NULL);
			
	} end_for_slist (settings.algorithms, l);


	for_slist (settings.algorithms, l) {

		algorithm = ls_list_algorithm(l);
		
		xv_set (algorithm->settings_item,
			XV_X,
				max_name_width +
				xv_col (suite_subframe->panel, 5),
			NULL);
		xv_set (algorithm->do_item,
			XV_X,
				max_name_width +
				((Rect *)xv_get (algorithm->settings_item, XV_RECT))->r_width +
				xv_col (suite_subframe->panel, 7),
			NULL);

	} end_for_slist (settings.algorithms, l);

	window_fit(suite_subframe->panel);
	window_fit(suite_subframe->frame);
}



void	show_layout_suite_subframe (void (*done_proc) ())
{
	if (!showing_layout_suite_subframe()) {
		create_suite_subframe();
	}
	suite_subframe->graphed_done_proc = done_proc;

	if (suite_subframe != (Graphed_pin_subframe)NULL) {
		xv_set (suite_subframe->frame, XV_SHOW, TRUE, 0);
		suite_subframe->showing = TRUE;
	} else {
		suite_subframe->showing = FALSE;
	}
}


int	showing_layout_suite_subframe(void)
{
	return showing_graphed_pin_subframe (suite_subframe);
}


static	void	suite_sf_set (Panel_item item, Event *event)
{
	save_layout_suite_settings ();
}

/*
static	void	suite_sf_reset (Panel_item item, Event *event)
{
	save_layout_suite_settings ();
}
*/

static	void	suite_sf_do (Panel_item item, Event *event)
{
	save_layout_suite_settings ();
	layout_suite(layout_suite_settings);
}


static	void	suite_sf_done (Frame frame)
{
	save_layout_suite_settings ();
}


static	void	suite_select_algorithm (Panel_item item, int value, Event *event)
{
	LS_algorithm	algorithm;

	save_layout_suite_settings ();
	algorithm = (LS_algorithm)xv_get (item, PANEL_CLIENT_DATA);

	if (value == 1) {
		algorithm->active = TRUE;
		xv_set (algorithm->settings_item,
			XV_SHOW, TRUE,
			NULL);
		xv_set (algorithm->do_item,
			XV_SHOW, TRUE,
			NULL);
	} else {
		algorithm->active = FALSE;
		xv_set (algorithm->settings_item,
			XV_SHOW, FALSE,
			NULL);
		xv_set (algorithm->do_item,
			XV_SHOW, FALSE,
			NULL);
	}
}


static	void	suite_show_algorithm_settings (Panel_item item, Event *event)
{
	LS_algorithm	algorithm;

	save_layout_suite_settings ();
	algorithm = (LS_algorithm)xv_get (item, PANEL_CLIENT_DATA);

	if (algorithm->show_settings != NULL) {
		algorithm->show_settings (NULL);
	}
}


static	void	suite_do_algorithm (Panel_item item, Event *event)
{
	LS_algorithm	algorithm;

	save_layout_suite_settings ();
	algorithm = (LS_algorithm)xv_get (item, PANEL_CLIENT_DATA);

	single_layout_suite (algorithm, layout_suite_settings);
}
