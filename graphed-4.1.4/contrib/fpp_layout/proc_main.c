/* (C) Universitaet Passau 1986-1994 */
/********************************************************************
 **                                                                **
 **     In dieser Datei wird die Benutzerschnittstelle aufge-      **
 **     baut.                                                      **
 **     Die Hauptprozeduren fuer die einzelnen Menuepunkte sind    **
 **     hier enthalten.                                            **
 **                                                                **
 ********************************************************************/



#include "decl.h"
#include "graphed/gridder.h"
#include "graphed/util.h"
#include "sgraph/algorithms.h"


/********************************************************************
 **                                                                **
 **     Das Benutzerfenster:                                       **
 **                                                                **
 ********************************************************************/
 
extern	Frame	base_frame;

static	Frame		fpp_frame;
static	Panel		fpp_panel;
static	Panel_item	run_fpp_button,
			fpp_cond_stretching_toggle,
			run_assila_button,
			assila_cond_stretching_toggle, 
			compression_button,
			compression_iterations_toggle,
			stepwise_compression_button,
			stepwise_compression_toggle,
			compression_animation_button,
			compression_remove_added_edges_button,
			compression_hide_show_added__edges_button;
static	Gridder		gridder;
static	int		showing_fpp_window = FALSE;

Fpp_settings fpp_settings = {
	64,				 /* Grid */
	GRIDDER_DISTANCE_2_LARGEST_SIZE
};


static	void	call_main_fpp			(Sgraph_proc_info info);
static	void	call_main_fpp_cond_stretching	(Sgraph_proc_info info);
static	void	call_main_asslia				(Sgraph_proc_info info);
static	void	call_main_asslia_cond_stretching		(Sgraph_proc_info info);
static	void	call_main_compression_y			(Sgraph_proc_info info);
static	void	call_main_compression_x			(Sgraph_proc_info info);
#if 0
static	void	call_main_zoom				(Sgraph_proc_info info);
#endif
static	void	call_main_compression			(Sgraph_proc_info info);
static	void	call_main_compression_without_iterations	(Sgraph_proc_info info);
static	void	call_main_animation				(Sgraph_proc_info info);
static	void	call_main_hide_show_added_edges		(Sgraph_proc_info info);
static	void	call_main_remove_added_edges			(Sgraph_proc_info info);
static	void	call_main_compression_stepwise		(Sgraph_proc_info info);

	void	save_fpp_settings (void);

static	void	fpp_notify_buttons (Panel_item item, Event *event);
static	void	hide_fpp_frame     (Frame frame);

void	color_remove (Sgraph sgraph);

Sgraph sgraph;


Fpp_settings init_fpp_settings(void)
{
	Fpp_settings	settings;

	settings.grid = 64;
	settings.grid_defaults = GRIDDER_DISTANCE_2_LARGEST_SIZE;

	return	settings;
}



int fpp_window (void)
{
	int	row_count = 0;
	
	fpp_frame = (Frame)xv_create(base_frame, FRAME_CMD,
		XV_LABEL,		"Chrobak & Payne algorithm",
		FRAME_CMD_PUSHPIN_IN,	TRUE,
		FRAME_SHOW_LABEL,	TRUE,
		WIN_SHOW,		TRUE,
		FRAME_NO_CONFIRM,	TRUE,
		FRAME_DONE_PROC,	hide_fpp_frame,
		NULL);

	if (fpp_frame == (Frame)NULL)
		return FALSE;

	fpp_panel = (Panel)xv_get(fpp_frame, FRAME_CMD_PANEL);
	
	if (fpp_panel == (Frame)NULL)
		return FALSE;

#define	COL1 0
#define COL2 25

	run_fpp_button = xv_create(fpp_panel, PANEL_BUTTON,
		XV_X,			xv_col(fpp_panel, COL1),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_LABEL_STRING,	"Chrobak-Payne",
		PANEL_NOTIFY_PROC,	fpp_notify_buttons,
		NULL);

	fpp_cond_stretching_toggle = xv_create(fpp_panel, PANEL_CHECK_BOX,
		XV_X,			xv_col(fpp_panel, COL2),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_CHOICE_STRINGS,	"conditional stretching", NULL,
		NULL);

	row_count += 1;
	run_assila_button = xv_create(fpp_panel, PANEL_BUTTON,
		XV_X,			xv_col(fpp_panel, COL1),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_LABEL_STRING,	"Nejia Assila Idea",
		PANEL_NOTIFY_PROC,	fpp_notify_buttons,
		NULL);

	assila_cond_stretching_toggle = xv_create(fpp_panel, PANEL_CHECK_BOX,
		XV_X,			xv_col(fpp_panel, COL2),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_CHOICE_STRINGS,	"conditional stretching", NULL,
		NULL);

	row_count += 1;
	compression_button = xv_create(fpp_panel, PANEL_BUTTON,
		XV_X,			xv_col(fpp_panel, COL1),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_LABEL_STRING,	"compression",
		PANEL_NOTIFY_PROC,	fpp_notify_buttons,
		NULL);

	compression_iterations_toggle = xv_create(fpp_panel, PANEL_CHECK_BOX,
		XV_X,			xv_col(fpp_panel, COL2),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_CHOICE_STRINGS,	"iterations", NULL,
		NULL);

	row_count += 1;
	stepwise_compression_button = xv_create(fpp_panel, PANEL_BUTTON,
		XV_X,			xv_col(fpp_panel, COL1),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_LABEL_STRING,	"compression stepwise",
		PANEL_NOTIFY_PROC,	fpp_notify_buttons,
		NULL); 

	stepwise_compression_toggle = xv_create(fpp_panel, PANEL_CHECK_BOX,
		XV_X,			xv_col(fpp_panel, COL2),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_CHOICE_STRINGS,	"x", "y", NULL,
		NULL);

	row_count += 1;
	compression_animation_button = xv_create(fpp_panel, PANEL_BUTTON,
		XV_X,			xv_col(fpp_panel, COL1),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_LABEL_STRING,	"animation",
		PANEL_NOTIFY_PROC,	fpp_notify_buttons,
		NULL);

	row_count += 1;
	compression_hide_show_added__edges_button = xv_create(fpp_panel, PANEL_BUTTON,
		XV_X,			xv_col(fpp_panel, COL1),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_LABEL_STRING,	"hide/show added edges",
		PANEL_NOTIFY_PROC,	fpp_notify_buttons,
		NULL);

	compression_remove_added_edges_button = xv_create(fpp_panel, PANEL_BUTTON,
		XV_X,			xv_col(fpp_panel, COL2),
		XV_Y,			xv_row(fpp_panel, row_count),
		PANEL_LABEL_STRING,	"remove added edges",
		PANEL_NOTIFY_PROC,	fpp_notify_buttons,
		NULL);

	row_count += 1;
	gridder = create_gridder (fpp_panel,
		GRIDDER_HEIGHT,
		"grid",
		GRIDDER_DISTANCE_2_DEFAULT_SIZE,
		64,
		row_count);

	gridder_set (gridder,
		fpp_settings.grid_defaults,
		fpp_settings.grid);


	window_fit (fpp_panel);
	window_fit (fpp_frame);

	compute_subwindow_position_at_graph_of_current_selection (
		fpp_frame);

	showing_fpp_window = TRUE;
	return TRUE;

} /* fpp_window */



void	show_fpp_subframe (void)
{
	if (showing_fpp_window) {
		;
	} else if (fpp_window () == FALSE) {
		error ("No more windows available\n");
	}
}


/* Notifiers rewitten by MH 6/10/91 */

int	fpp_prechecks_result;

static	void		fpp_do_prechecks (Sgraph_proc_info info)
{
	Snode	node;
	int	n = 0;
	
	fpp_prechecks_result = TRUE;
	if (info->sgraph == NULL) {
		fpp_prechecks_result = TRUE;
	} else if (test_sgraph_biconnected (info->sgraph) == FALSE) {
		error ("graph is not biconnected\n");
		fpp_prechecks_result = FALSE;
	} else {

		fpp_prechecks_result =
			fpp_prechecks_result &&
			(test_find_non_straight_line_edge (graphed_graph(info->sgraph)) == FALSE);
		fpp_prechecks_result =
			fpp_prechecks_result &&
			(test_graph_edges_are_drawn_planar (graphed_graph(info->sgraph)) == TRUE);

		
		for_all_nodes (info->sgraph, node) {
			n++;
		} end_for_all_nodes (info->sgraph, node);
		
		if (n > 3) {
			fpp_prechecks_result =
				fpp_prechecks_result && TRUE;
		} else {
			warning ("The graph must contain at least four nodes\n");
			fpp_prechecks_result =
				fpp_prechecks_result && FALSE;
		}
	}
}


static	void	fpp_notify_buttons (Panel_item item, Event *event)
{

#define	toggle_bit_on(value,bit)	(((unsigned int)value) & (1 << (bit)))
#define	toggle_bit_off(value,bit)	(!(toggle_bit_on(value,bit)))

	save_fpp_settings ();

	if (item == run_fpp_button                  ||
	    item == run_assila_button                         ||
	    item == compression_button                        ||
	    item == stepwise_compression_button) {
	
		call_sgraph_proc (fpp_do_prechecks, NULL);
		if (fpp_prechecks_result == FALSE) {
			error ("The algorithm cannot run\n");
			return;
		}
	
	}
	if (item == run_fpp_button) {
		if (toggle_bit_off (xv_get(fpp_cond_stretching_toggle, PANEL_VALUE), 1)) {
			call_sgraph_proc (call_main_fpp, NULL);
		} else {
			call_sgraph_proc (call_main_fpp_cond_stretching, NULL);
		}
	} else if (item == run_assila_button) {
		if (toggle_bit_off (xv_get(assila_cond_stretching_toggle, PANEL_VALUE), 1)) {
			call_sgraph_proc (call_main_asslia, NULL);
		} else {
			call_sgraph_proc (call_main_asslia_cond_stretching, NULL);
		}
	} else if (item == compression_button) {
		if (toggle_bit_on (xv_get(compression_iterations_toggle, PANEL_VALUE), 1)) {
			call_sgraph_proc (call_main_compression, NULL);
		} else {
			call_sgraph_proc (call_main_compression_without_iterations, NULL);
		}
	} else if (item == stepwise_compression_button) {
		if (toggle_bit_on (xv_get(stepwise_compression_toggle, PANEL_VALUE), 1)) {
			call_sgraph_proc (call_main_compression_x, NULL); /* x-direction */
		} else if (toggle_bit_on (xv_get(stepwise_compression_toggle, PANEL_VALUE), 2)) {
			call_sgraph_proc (call_main_compression_y, NULL); /* y-direction */
		} else {
			call_sgraph_proc (call_main_compression_stepwise, NULL); /* none or both */
		}
	} else if (item == compression_animation_button) {
		call_sgraph_proc (call_main_animation, NULL);
	} else if (item == compression_hide_show_added__edges_button) {
		call_sgraph_proc (call_main_hide_show_added_edges, NULL);
	} else if (item == compression_remove_added_edges_button) {
		call_sgraph_proc (call_main_remove_added_edges, NULL);
	}
}



void	save_fpp_settings (void)
{
	if (showing_fpp_window) {
		fpp_settings.grid = gridder_get_size (gridder);
		fpp_settings.grid_defaults = (int)gridder_get_value (gridder);
	} else {
		if (fpp_settings.grid_defaults != GRIDDER_DISTANCE_OTHER) {
			fpp_settings.grid = recompute_gridder_size (
				NULL,
				fpp_settings.grid_defaults,
				GRIDDER_HEIGHT);
		}
	}
}


static	void	hide_fpp_frame (Frame frame)
{
	free (gridder); gridder = (Gridder)NULL;
	xv_destroy_safe(fpp_frame);
	showing_fpp_window = FALSE;

}
/********************************************************************
 **                                                                **
 **     Die Hauptprozeduren:                                       **
 **                                                                **
 ********************************************************************/



static void call_main_fpp (Sgraph_proc_info info)
			/* Menuepunkt "Chrobak-Payne". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
	
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	fpp_algorithm (1);
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};

	if (plan == FALSE) {
		return;
	};
		
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_fpp */




static void call_main_fpp_cond_stretching (Sgraph_proc_info info)
			/* Erster Menuepunkt "with conditional stretching". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	fpp_algorithm (2);
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;

} /* call_main_fpp_cond_stretching */




static void call_main_asslia (Sgraph_proc_info info)
			/* Menuepunkt "Nejia Assila Idea". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	fpp_algorithm (3);
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;

} /* call_main_asslia */




static void call_main_asslia_cond_stretching (Sgraph_proc_info info)
			/* Zweiter Menuepunkt "with conditional stretching". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	
    if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	

	fpp_algorithm (4);
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_asslia_cond_stretching */




static void call_main_compression_y (Sgraph_proc_info info)
			/* Menuepunkt "in y-direction". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	no_graph = FALSE;

	
    if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	no_graph = TRUE;  /* Kom. */

	do {
		store_last_graph ();
		compress (sgraph, 1);
	} while (test_changes () == TRUE);
	
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_compression_y */




static void call_main_compression_x (Sgraph_proc_info info)
			/* Menuepunkt "in x-direction". */
                	     
{
	Sgraph	sgraph = info->sgraph;
	no_graph = FALSE;

	
	
    if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	no_graph = TRUE;  /* Kom. */
		
	change_coordinates ();
	
	do {
		store_last_graph ();
		compress (sgraph, 1);
	} while (test_changes () == TRUE);
	
	change_coordinates ();
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_compression_x */



#if 0
static void call_main_zoom (Sgraph_proc_info info)
			/* Menuepunkt "zoom, double size". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	
    if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		zoom (sgraph);
	}
	else {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_zoom */
#endif



static void call_main_compression (Sgraph_proc_info info)
			/* Menuepunkt "compression with iterations". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	int counter;
	no_graph = FALSE;

	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
	
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	no_graph = TRUE;  /* Kom. */
		
	do {
		counter = 0;
		
		do {
			store_last_graph ();
			compress (sgraph, 0);
		} while (test_changes () == TRUE);
		
		change_coordinates ();
		
		do {
			counter++;
			store_last_graph ();
			compress (sgraph, 0);
		} while (test_changes () == TRUE);
		
		change_coordinates ();
	} while (counter > 1);
	
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_compression */




static void call_main_compression_without_iterations (Sgraph_proc_info info)
			/* Menuepunkt "compression without iterations". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	int chx = TRUE, chy = TRUE;
	no_graph = FALSE;

	
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	no_graph = TRUE;   /* Kom. */
		
	do {
		store_last_graph ();
		compress (sgraph, 0);
		chy = (test_changes ());
		change_coordinates ();
		store_last_graph ();
		compress (sgraph, 0);
		chx = (test_changes ());
		change_coordinates ();
	} while ((chy == TRUE) || (chx == TRUE));
	
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_compression_without_iterations */




static void call_main_animation (Sgraph_proc_info info)
			/* Menuepunkt "animation". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	int counter;
	no_graph = FALSE;

	
		
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
	
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	no_graph = TRUE;   /* Kom. */
	
	do {
		counter = 0;
		
		do {
			store_last_graph ();
			compress (sgraph, 1);
		} while (test_changes () == TRUE);
		
		change_coordinates ();
		
		do {
			counter++;
			store_last_graph ();
			compress (sgraph, 1);
		} while (test_changes () == TRUE);
		
		change_coordinates ();
	} while (counter > 1);
	
	/* message ("Compresssion completed.\n"); */
	
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_animation */




static void call_main_hide_show_added_edges (Sgraph_proc_info info)
			/* Menuepunkt "hide/show added edges". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		color_switch (sgraph);
	}
	else {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	info->repaint  = TRUE;
	
} /* call_main_hide_show_added_edges */




static void call_main_remove_added_edges (Sgraph_proc_info info)
			/* Menuepunkt "hide/show added edges". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		color_remove (sgraph);
	}
	else {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_hide_show_added_edges */




static void call_main_compression_stepwise (Sgraph_proc_info info)
			/* Menuepunkt "compression stepwise". */
                	     

{
	Sgraph	sgraph = info->sgraph;
	int chx = TRUE, chy = TRUE;
	no_graph = FALSE;

	
	if ((info != NULL) && (info->sgraph != NULL) && (info->sgraph->nodes != NULL)) {
		take_graph (sgraph);
	}
	else {
		return;
	};
		
	if (plan == FALSE) {
		return;
	};
	
	triangulation (sgraph);
			
	if (plan == FALSE) {
		return;
	};
	
	no_graph = TRUE;   /* Kom. */
		
	do {
		store_last_graph ();
		compress (sgraph, 2);
		chy = test_changes ();
		change_coordinates ();
		store_last_graph ();
		compress (sgraph, 2);
		chx = test_changes ();
		change_coordinates ();
	} while ((chy == TRUE) || (chx == TRUE));
	
	make_the_graph (sgraph);

	if (plan == FALSE) {
		return;
	};
	
	info->new_sgraph = info->sgraph;
	info->new_selected = SGRAPH_SELECTED_SAME;
	info->recenter = TRUE;
	
} /* call_main_compression_stepwise */





void	call_fpp_layout (Sgraph_proc_info info)
{
	call_sgraph_proc (fpp_do_prechecks, NULL);
	if (fpp_prechecks_result == FALSE) {
		error ("The algorithm cannot run\n");
		return;
	}

	call_sgraph_proc (call_main_fpp, NULL);
	call_sgraph_proc (call_main_remove_added_edges, NULL);
	return;
}


void menu_fpp_layout (Menu menu, Menu_item menu_item)
{
	save_fpp_settings ();
	call_fpp_layout ((Sgraph_proc_info)0);
}



void	call_fpp_layout_asslia (Sgraph_proc_info info)
{
	call_sgraph_proc (fpp_do_prechecks, NULL);
	if (fpp_prechecks_result == FALSE) {
		error ("The algorithm cannot run\n");
		return;
	}

	call_sgraph_proc (call_main_asslia, NULL);
	call_sgraph_proc (call_main_remove_added_edges, NULL);
}

void menu_fpp_layout_asslia (Menu menu, Menu_item menu_item)
{
	save_fpp_settings ();
	call_fpp_layout_asslia ((Sgraph_proc_info)0);
}



void	call_fpp_layout_compression (Sgraph_proc_info info)
{
	call_sgraph_proc (fpp_do_prechecks, NULL);
	if (fpp_prechecks_result == FALSE) {
		error ("The algorithm cannot run\n");
		return;
	}

	call_sgraph_proc (call_main_compression, NULL);
	call_sgraph_proc (call_main_remove_added_edges, NULL);
}


void menu_fpp_layout_compression (Menu menu, Menu_item menu_item)
{
	save_fpp_settings ();
	call_fpp_layout_compression ((Sgraph_proc_info)0);
}



void menu_fpp_subframe (Menu menu, Menu_item menu_item)
{
	show_fpp_subframe();
}
