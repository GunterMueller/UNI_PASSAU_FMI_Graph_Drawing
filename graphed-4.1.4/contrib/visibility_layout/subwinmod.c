/**************************************************************************/
/***                                                                    ***/
/*** Filename: SUBWINMOD.C                                              ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "visibility_layout_export.h"
#include <xview/xview.h>
#include <xview/panel.h>
#include "gridder.h"


extern Frame     	base_frame;
static Xv_Window	flags_frame,
                        panel;
static Gridder		node_gridder;
static Gridder		vertical_gridder,
                        horizontal_gridder;
static int              WindowOpen = FALSE;

Tarjan_settings tarjan_settings = {
                        GRIDDER_DISTANCE_1_DEFAULT_SIZE,
                        GRIDDER_DISTANCE_1_DEFAULT_SIZE,
                        GRIDDER_DISTANCE_1_DEFAULT_SIZE,
                        32,
                        32,
                        32,
			0,
			0,
			0,
			0,
			0
                    };

Tarjan_settings	init_tarjan_settings (void)
{
Tarjan_settings	settings;
	settings.size_defaults_x = GRIDDER_DISTANCE_1_DEFAULT_SIZE;
	settings.size_defaults_y = GRIDDER_DISTANCE_1_DEFAULT_SIZE;
	settings.node_defaults_y = GRIDDER_DISTANCE_1_DEFAULT_SIZE;
	settings.vertical_distance = 32;
	settings.horizontal_distance = 32;
	settings.vertical_height = 32;
	settings.largest_face = 0;
	settings.polyline = 0;
	settings.betterpolyline = 0;
	settings.greedy = 0;
	settings.verbose = 0;
	return	settings;
}

static	void tarjan_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tarjan_planar_layout, NULL);
}

static	void tarjan2_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tarjan2_planar_layout, NULL);
}

static	void otten_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_otten_planar_layout, NULL);
}

static	void tamassia_w_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tamassia_w_planar_layout, NULL);
}

static	void tamassia_e_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tamassia_e_planar_layout, NULL);
}

static	void tamassia_s_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tamassia_s_planar_layout, NULL);
}

static	void wismath_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_wismath_planar_layout, NULL);
}

static	void cylinder_done_proc(void)
{
    save_tarjan_settings();
    call_sgraph_proc(call_cylinder_planar_layout, NULL);
}

static	void tarjan_set_proc(void)
{
    save_tarjan_settings();
}

static	void tarjan_quit_proc(void)
{
    save_tarjan_settings();
    free(vertical_gridder);
    free(horizontal_gridder);
    free(node_gridder);
    xv_destroy_safe(flags_frame);
    WindowOpen = FALSE;
}

static void options_notify1(Panel_item item, int value, Event *event)
{
/*printf("value = %d\n",value);*/
tarjan_settings.largest_face = (value & 1);
tarjan_settings.verbose = (value & 2);
/*printf("largest_face = %d\n",tarjan_settings.largest_face);*/
}

static void options_notify2(Panel_item item, int value, Event *event)
{
/*printf("value = %d\n",value);*/
tarjan_settings.polyline = (value & 1);
tarjan_settings.betterpolyline = (value & 2);
/*printf("good_polyline = %d\n",tarjan_settings.polyline);
printf("better_polyline = %d\n",tarjan_settings.betterpolyline);*/
}

static void options_notify3(Panel_item item, int value, Event *event)
{
/*printf("value = %d\n",value);*/
tarjan_settings.greedy = value;
/*printf("greedy = %d\n",tarjan_settings.greedy);*/
}


void show_tarjan_subframe(void)
{
int row_count = 0;
if(WindowOpen)
    {
        return;
    }
    flags_frame = (Frame)xv_create(base_frame,FRAME_CMD,
				   FRAME_CMD_PUSHPIN_IN, TRUE,
				   XV_LABEL,             "Visibility layouts",
				   FRAME_SHOW_LABEL,     TRUE,
				   FRAME_DONE_PROC,      tarjan_quit_proc,
				   NULL);
    panel = (Panel)xv_get(flags_frame,FRAME_CMD_PANEL);
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,6),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "Set",
		    PANEL_NOTIFY_PROC,  tarjan_set_proc,
		    NULL);
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,42),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "Quit",
		    PANEL_NOTIFY_PROC,  tarjan_quit_proc,
		    NULL);
    row_count += 2;
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,2),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "Otten layout",
		    PANEL_NOTIFY_PROC,  otten_done_proc,
		    NULL);
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,19),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "Tarjan layout",
		    PANEL_NOTIFY_PROC,  tarjan_done_proc,
		    NULL);
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,36),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "Wismath layout",
		    PANEL_NOTIFY_PROC,  wismath_done_proc,
		    NULL);
    row_count += 1;
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,0),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "w-visibility layout",
		    PANEL_NOTIFY_PROC,  tamassia_w_done_proc,
		    NULL);
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,18),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "e-visibility layout",
		    PANEL_NOTIFY_PROC,  tamassia_e_done_proc,
		    NULL);
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,36),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "s-visibility layout",
		    PANEL_NOTIFY_PROC,  tamassia_s_done_proc,
		    NULL);
    row_count += 1;
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,10),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "Tamassia layout",
		    PANEL_NOTIFY_PROC,  tarjan2_done_proc,
		    NULL);
    (void)xv_create(panel,PANEL_BUTTON,
		    PANEL_LABEL_X,      xv_col(panel,29),
		    PANEL_LABEL_Y,      xv_row(panel,row_count),
		    PANEL_LABEL_STRING, "cylindric layout",
		    PANEL_NOTIFY_PROC,  cylinder_done_proc,
		    NULL);
    row_count += 2;
    vertical_gridder = create_gridder(panel,
				      GRIDDER_HEIGHT,
				      "vertical distance",
				      GRIDDER_DISTANCE_1_DEFAULT_SIZE,
				      32,
				      row_count);
    row_count += 1;
    horizontal_gridder = create_gridder(panel,
				        GRIDDER_WIDTH,
				        "horizontal distance",
				        GRIDDER_DISTANCE_1_DEFAULT_SIZE,
				        32,
				        row_count);
    row_count += 1;
    node_gridder = create_gridder(panel,
				        GRIDDER_HEIGHT,
				        "node height",
				        GRIDDER_DISTANCE_1_DEFAULT_SIZE,
				        32,
				        row_count);
    gridder_set(vertical_gridder,tarjan_settings.size_defaults_y,
                tarjan_settings.vertical_distance);
    gridder_set(horizontal_gridder,tarjan_settings.size_defaults_x,
                tarjan_settings.horizontal_distance);
    gridder_set(node_gridder,tarjan_settings.node_defaults_y,
                tarjan_settings.vertical_height);

    xv_create(panel, PANEL_CHECK_BOX,
	PANEL_LAYOUT,		PANEL_VERTICAL,
	PANEL_LABEL_X,      	xv_col(panel,0),
	PANEL_LABEL_STRING,	"Options :",
	PANEL_CHOICE_STRINGS,	"Largest Face","Statistics",NULL,
	PANEL_NOTIFY_PROC,	options_notify1,
	PANEL_VALUE,		tarjan_settings.largest_face + 
				2* tarjan_settings.verbose,
	NULL);
		
    xv_create(panel, PANEL_CHOICE_STACK,
	PANEL_LAYOUT,		PANEL_VERTICAL, 
	PANEL_LABEL_X,      	xv_col(panel,14),
	PANEL_LABEL_STRING,	"ST-Edge :",
	PANEL_CHOICE_STRINGS,	"Any","Good","Best",NULL,
	PANEL_NOTIFY_PROC,	options_notify3,
	PANEL_VALUE,		tarjan_settings.greedy,
	NULL);
		
    xv_create(panel, PANEL_CHOICE_STACK,
	PANEL_LAYOUT,		PANEL_VERTICAL,
	PANEL_LABEL_X,      	xv_col(panel,28),
	PANEL_LABEL_STRING,	"Polyline-Postprocessing :",
	PANEL_CHOICE_STRINGS,	"None","Good","Better",NULL,
	PANEL_NOTIFY_PROC,	options_notify2,
	PANEL_VALUE,		tarjan_settings.polyline*2 +
				tarjan_settings.betterpolyline*4,
	NULL);
		
    window_fit(panel);
    window_fit(flags_frame);
    compute_subwindow_position_at_graph_of_current_selection(flags_frame);
    xv_set(flags_frame,WIN_SHOW,TRUE,NULL);
    WindowOpen = TRUE;
}

void save_tarjan_settings(void)
{
if(WindowOpen) 
    {
    tarjan_settings.vertical_distance   = gridder_get_size (vertical_gridder);
    tarjan_settings.horizontal_distance = gridder_get_size (horizontal_gridder);
    tarjan_settings.vertical_height = gridder_get_size (node_gridder);
    tarjan_settings.size_defaults_y = (int)gridder_get_value (vertical_gridder);
    tarjan_settings.size_defaults_x = (int)gridder_get_value (horizontal_gridder);
    tarjan_settings.node_defaults_y = (int)gridder_get_value (node_gridder);
} else 
    {
    if(tarjan_settings.size_defaults_y != GRIDDER_DISTANCE_OTHER)
        {
        tarjan_settings.vertical_distance = recompute_gridder_size (NULL,
                       tarjan_settings.size_defaults_y,GRIDDER_HEIGHT);
        }

    if(tarjan_settings.size_defaults_x != GRIDDER_DISTANCE_OTHER)
        {
        tarjan_settings.horizontal_distance = recompute_gridder_size (NULL,
                       tarjan_settings.size_defaults_x,GRIDDER_WIDTH);
        }
    if(tarjan_settings.node_defaults_y != GRIDDER_DISTANCE_OTHER)
        {
        tarjan_settings.vertical_height = recompute_gridder_size (NULL,
                       tarjan_settings.node_defaults_y,GRIDDER_HEIGHT);
        }
    }
}


/*                                                                            */
/* ************************************************************************** */
/* **                        END OF FILE: SUBWINMOD.C                      ** */
/* ************************************************************************** */

