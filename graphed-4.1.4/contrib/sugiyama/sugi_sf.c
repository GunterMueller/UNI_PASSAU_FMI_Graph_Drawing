/* (C) Universitaet Passau 1986-1994 */
#include <xview/xview.h>
#include <xview/panel.h>

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/algorithms.h>
#include "sugiyama_export.h"
#include "graphed/gridder.h"
#include <graphed/graphed_pin_sf.h>

typedef void (*voidproc)();

/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	show_sugiyama_subframe ()				*/
/*									*/
/************************************************************************/

static	void	sugiyama_sf_set   (Panel_item item, Event *event);
static	void	sugiyama_sf_reset (Panel_item item, Event *event);
static	void	sugiyama_sf_do    (Panel_item item, Event *event);
static	void	sugiyama_sf_done  (Frame frame);

static	Panel_item	sugiyama_it1_pi,
			sugiyama_it2_pi,
			leveling_choose,
			up_arcs_choose,
			crossings_choose;

static	Gridder		vertical_gridder,
			horizontal_gridder;



Sugiyama_settings	sugiyama_settings;

Sugiyama_settings	init_sugiyama_settings (void)
{
	Sugiyama_settings settings;

	settings.vertical_distance   = 64;
	settings.horizontal_distance = 64;
	settings.it1 = 10;
	settings.it2 = 3;
	settings.size_defaults_x = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.size_defaults_y = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.leveling = 1;
	settings.up       = 1;
	settings.reduce_crossings_algorithm = 0;
	settings.mult_distance    = 1;
	settings.width            = 1000;

	return settings;
}


static	Graphed_pin_subframe	sugi_sf = (Graphed_pin_subframe)NULL;


static	void	create_sugi_sf(void)
{
	int	row_count = 0;
	
	if (sugi_sf == (Graphed_pin_subframe)NULL) {
		sugi_sf = new_graphed_pin_subframe((Frame)0);
	}
	sugi_sf->set_proc   = sugiyama_sf_set;
	sugi_sf->reset_proc = sugiyama_sf_reset;
	sugi_sf->do_proc    = sugiyama_sf_do;
	sugi_sf->done_proc  = sugiyama_sf_done;

	graphed_create_pin_subframe (sugi_sf, "DAG Layout");

	row_count ++;
	vertical_gridder = create_gridder (sugi_sf->panel,
		GRIDDER_HEIGHT,
		"vertical distance",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);
		
	row_count += 2;
	horizontal_gridder = create_gridder (sugi_sf->panel,
		GRIDDER_WIDTH,
		"horizontal distance",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);

	row_count +=2;
	sugiyama_it1_pi = xv_create(sugi_sf->panel, PANEL_NUMERIC_TEXT,
		XV_X,				xv_col(sugi_sf->panel, 0),
		XV_Y,				xv_row(sugi_sf->panel, row_count),
		PANEL_LABEL_STRING,		"iterations 1 : ",
		PANEL_LABEL_BOLD,		TRUE,
		PANEL_VALUE,			sugiyama_settings.it1,
		PANEL_MIN_VALUE,		0,
		PANEL_MAX_VALUE,		100,
		PANEL_VALUE_STORED_LENGTH,	5,
		PANEL_VALUE_DISPLAY_LENGTH,	5,
		NULL);

	row_count ++;
	sugiyama_it2_pi = xv_create(sugi_sf->panel, PANEL_NUMERIC_TEXT,
		XV_X,				xv_col(sugi_sf->panel, 0),
		XV_Y,				xv_row(sugi_sf->panel, row_count),
		PANEL_LABEL_STRING,		"iterations 2 : ",
		PANEL_LABEL_BOLD,		TRUE,
		PANEL_VALUE,			sugiyama_settings.it2,
		PANEL_MIN_VALUE,		0,
		PANEL_MAX_VALUE,		100,
		PANEL_VALUE_STORED_LENGTH,	5,
		PANEL_VALUE_DISPLAY_LENGTH,	5,
		NULL);


	row_count ++;
	leveling_choose=xv_create(sugi_sf->panel, PANEL_CHOICE_STACK,
		XV_X,			xv_col(sugi_sf->panel, 0),
		XV_Y,			xv_row(sugi_sf->panel, row_count),
                PANEL_LABEL_STRING,     "arrange in levels with",
                PANEL_CHOICE_STRINGS,   "Coffman-Graham",
					"topologic sorting",
                                        "recursiv",
					NULL,
                PANEL_VALUE,            sugiyama_settings.leveling,
                NULL);

	row_count ++;
	up_arcs_choose=xv_create(sugi_sf->panel, PANEL_CHOICE_STACK,
		XV_X,			xv_col(sugi_sf->panel, 0),
		XV_Y,			xv_row(sugi_sf->panel, row_count),
                PANEL_LABEL_STRING,     "resolve cycles width",
                PANEL_CHOICE_STRINGS,   "Greedy-In-Alg.",
				        "Greedy-Out-Alg.",
                                        "DandC-Alg.",
					NULL,
                PANEL_VALUE,            sugiyama_settings.up,       
                NULL);

	row_count ++;
	crossings_choose=xv_create(sugi_sf->panel, PANEL_CHOICE_STACK,
		XV_X,			xv_col(sugi_sf->panel, 0),
		XV_Y,			xv_row(sugi_sf->panel, row_count),
                PANEL_LABEL_STRING,     "reduce crossings with",
                PANEL_CHOICE_STRINGS,   "Barrycenter",
				        "Bubbling",
					NULL,
                PANEL_VALUE,            sugiyama_settings.reduce_crossings_algorithm,       
                NULL);

#if FALSE
	row_count ++;
	distance_choose=xv_create(sugi_sf->panel, PANEL_CHOICE_STACK,
		XV_X,			xv_col(sugi_sf->panel, 0),
		XV_Y,			xv_row(sugi_sf->panel, row_count),
                PANEL_LABEL_STRING,     "Distance",
                PANEL_CHOICE_STRINGS,   "1",
					"2",
				        "3",
					NULL,
                PANEL_VALUE,            sugiyama_settings.mult_distance-1,
                NULL);

	row_count ++;
	node_nr_in_level_choose=xv_create(sugi_sf->panel, PANEL_CHOICE_STACK,
		XV_X,			xv_col(sugi_sf->panel, 0),
		XV_Y,			xv_row(sugi_sf->panel, row_count),
                PANEL_LABEL_STRING,     "max. number of nodes per level",
                PANEL_CHOICE_STRINGS,   "1",
					"2",
					"3",
					"4",
					"5",
					"6",
					"7",
					"8",
					"9",
					"10",
					"1000",
					NULL,
                PANEL_VALUE,            sugiyama_settings.width-1,
                NULL);
#endif

	window_fit(sugi_sf->panel);
	window_fit(sugi_sf->frame);
}



void	show_sugiyama_subframe (void *done_proc)
{
	if (sugi_sf == (Graphed_pin_subframe)NULL || !sugi_sf->showing) {
		create_sugi_sf();
	}
	sugi_sf->graphed_done_proc = (voidproc)done_proc;


	if (sugi_sf != (Graphed_pin_subframe)NULL) {
	
		xv_set(sugiyama_it1_pi, PANEL_VALUE,
			sugiyama_settings.it1, NULL);
		xv_set(sugiyama_it2_pi, PANEL_VALUE,
			sugiyama_settings.it2, NULL);
		gridder_set (vertical_gridder,
			sugiyama_settings.size_defaults_y,
			sugiyama_settings.vertical_distance);
		gridder_set (horizontal_gridder,
			sugiyama_settings.size_defaults_x,
			sugiyama_settings.horizontal_distance);
		
		compute_subwindow_position_at_graph_of_current_selection (
			sugi_sf->frame);
		xv_set (sugi_sf->frame, WIN_SHOW, TRUE, NULL);
		
		sugi_sf->showing = TRUE;
		
	}
}


static	void	sugiyama_sf_set (Panel_item item, Event *event)
{
	save_sugiyama_settings ();
}


static	void	sugiyama_sf_reset (Panel_item item, Event *event)
{
/*	save_sugiyama_settings (); */
}


static	void	sugiyama_sf_do (Panel_item item, Event *event)
{
	save_sugiyama_settings ();
	call_sgraph_proc (call_sugiyama_layout, NULL);
}


static	void	sugiyama_sf_done (Frame frame)
{
	save_sugiyama_settings ();
	
	free (horizontal_gridder); horizontal_gridder = (Gridder)NULL;
	free (vertical_gridder);   vertical_gridder = (Gridder)NULL;
}


void	save_sugiyama_settings (void)
{
	if (sugi_sf != NULL && sugi_sf->showing) {

#if 0
		int	width_menu;
#endif

		sugiyama_settings.vertical_distance   =
			gridder_get_size (vertical_gridder);
		sugiyama_settings.horizontal_distance =
			gridder_get_size (horizontal_gridder);
		sugiyama_settings.size_defaults_y =
			(int)gridder_get_value (vertical_gridder);
		sugiyama_settings.size_defaults_x =
			(int)gridder_get_value (horizontal_gridder);
		sugiyama_settings.it1 = xv_get(sugiyama_it1_pi, PANEL_VALUE);
		sugiyama_settings.it2 = xv_get(sugiyama_it2_pi, PANEL_VALUE);

		sugiyama_settings.leveling =
			xv_get( leveling_choose, PANEL_VALUE );
		sugiyama_settings.up =
			xv_get( up_arcs_choose, PANEL_VALUE );
		sugiyama_settings.reduce_crossings_algorithm =
			xv_get( crossings_choose, PANEL_VALUE );
#if FALSE
		sugiyama_settings.mult_distance =
			xv_get( distance_choose, PANEL_VALUE ) +1;
		width_menu =xv_get( node_nr_in_level_choose, PANEL_VALUE );
		sugiyama_settings.width =
			iif (width_menu == 10, 1000, width_menu+1);
#endif

	} else {

		if (sugiyama_settings.size_defaults_y != GRIDDER_DISTANCE_OTHER) {
			sugiyama_settings.vertical_distance =
				recompute_gridder_size (
					NULL,
					sugiyama_settings.size_defaults_y,
					GRIDDER_HEIGHT);
		}
		if (sugiyama_settings.size_defaults_y != GRIDDER_DISTANCE_OTHER) {
			sugiyama_settings.horizontal_distance =
				recompute_gridder_size (
					NULL,
					sugiyama_settings.size_defaults_x,
					GRIDDER_WIDTH);
		}
	}

	if (sugiyama_settings.vertical_distance == 0)
		sugiyama_settings.vertical_distance = get_current_node_height ();
	if (sugiyama_settings.horizontal_distance == 0)
		sugiyama_settings.horizontal_distance = get_current_node_width ();

}
