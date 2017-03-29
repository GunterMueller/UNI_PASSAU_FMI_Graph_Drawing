/* (C) Universitaet Passau 1986-1994 */
#include <xview/xview.h>
#include <xview/panel.h>

#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include "graphed/gridder.h"
#include "graphed/graphed_pin_sf.h"
#include "convex_layout_export.h"

static	void	create_convex_draw_sf (void);
static	void	convex_draw_sf_set   (Panel_item item, Event *event);
static	void	convex_draw_sf_reset (Panel_item item, Event *event);
static	void	convex_draw_sf_do    (Panel_item item, Event *event);
static	void	convex_draw_sf_done  (Frame frame);


static	Graphed_pin_subframe	convex_draw_sf = (Graphed_pin_subframe)NULL;
static	Panel_item		convex_draw_editable_toggle;
static	Gridder			gridder;


Convex_draw_settings convex_draw_settings;

Convex_draw_settings	init_convex_draw_settings(void)
{
	Convex_draw_settings	settings;

	settings.grid = 64;
	settings.grid_defaults = GRIDDER_DISTANCE_2_LARGEST_SIZE;
	settings.editable = TRUE;
	
	return settings;
}



static	void	create_convex_draw_sf(void)
{
	int	row_count = 0;
	
	if (convex_draw_sf == (Graphed_pin_subframe)NULL)
		convex_draw_sf = new_graphed_pin_subframe((Frame)0);
	convex_draw_sf->set_proc   = convex_draw_sf_set;
	convex_draw_sf->reset_proc = convex_draw_sf_reset;
	convex_draw_sf->do_proc    = convex_draw_sf_do;
	convex_draw_sf->done_proc  = convex_draw_sf_done;

	graphed_create_pin_subframe (convex_draw_sf, "Planar / Convex Layout");

	row_count = 1;
	convex_draw_editable_toggle = xv_create(convex_draw_sf->panel,
		PANEL_TOGGLE,
		PANEL_CHOICE_STRINGS, "preserve node sizes", NULL,
		XV_X,	xv_col(convex_draw_sf->panel, 00),
		XV_Y,	xv_row(convex_draw_sf->panel, row_count),
		NULL);

	row_count += 2;
	gridder = create_gridder (convex_draw_sf->panel,
		GRIDDER_HEIGHT,
		"shortest edge",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);


	window_fit(convex_draw_sf->panel);
	window_fit(convex_draw_sf->frame);
}


void	show_convex_draw_subframe (void (*done_proc) ())
{
	if (!showing_convex_draw_subframe()) {
		create_convex_draw_sf();
	}
	convex_draw_sf->graphed_done_proc = done_proc;

	if (convex_draw_sf !=  (Graphed_pin_subframe)NULL) {
	
		gridder_set (gridder,
			convex_draw_settings.grid_defaults,
			convex_draw_settings.grid);
		xv_set(convex_draw_editable_toggle,
			PANEL_VALUE, (unsigned int)convex_draw_settings.editable,
			NULL);
			
		compute_subwindow_position_at_graph_of_current_selection (
			convex_draw_sf->frame);
		xv_set(convex_draw_sf->frame, WIN_SHOW, TRUE, NULL);

		convex_draw_sf->showing = TRUE;
		
	}
}



int	showing_convex_draw_subframe(void)
{
	return showing_graphed_pin_subframe(convex_draw_sf);
}


static	void	convex_draw_sf_set (Panel_item item, Event *event)
{
	save_convex_draw_settings ();
}


static	void	convex_draw_sf_reset (Panel_item item, Event *event)
{
	;
}


static	void	convex_draw_sf_do (Panel_item item, Event *event)
{
	if (convex_draw_settings.editable) {
		call_sgraph_proc (DrawGraphConvexEditable, NULL);
	} else {
		call_sgraph_proc (DrawGraphConvexStructur, NULL);
	}
}


static	void	convex_draw_sf_done (Frame frame)
{
	save_convex_draw_settings ();

	free (gridder);   gridder = (Gridder)NULL;
}


void	save_convex_draw_settings (void)
{
	if (showing_convex_draw_subframe()) {
		convex_draw_settings.grid = gridder_get_size (gridder);
		convex_draw_settings.grid_defaults =
			(int)gridder_get_value (gridder);
		convex_draw_settings.editable =
			(int)xv_get(convex_draw_editable_toggle, PANEL_VALUE);
	} else {
		if (convex_draw_settings.grid_defaults != GRIDDER_DISTANCE_OTHER) {
			convex_draw_settings.grid = recompute_gridder_size (
				NULL,
				convex_draw_settings.grid_defaults,
				GRIDDER_HEIGHT);
		}
	}
}


void menu_convex_layout_settings (Menu menu, Menu_item menu_item)
{
	save_convex_draw_settings ();
	show_convex_draw_subframe (NULL);
}

  
  
void menu_convex_layout_structure_only (Menu menu, Menu_item menuitem)
{
	save_convex_draw_settings ();
	call_sgraph_proc(DrawGraphConvexStructur, NULL);
}


void menu_convex_layout (Menu menu, Menu_item menuitem)
{
	save_convex_draw_settings ();
	call_sgraph_proc(DrawGraphConvexEditable, NULL);
}
