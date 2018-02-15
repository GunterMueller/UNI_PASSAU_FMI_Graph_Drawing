/* Copyright Universitaet Passau 1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				edge_sf.c				*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul enthaelt die Verwaltung des edge_subframe zum	*/
/*	Editieren von Kanten.						*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"
#include "graphed_subwindows.h"

#include "font.h"
#include "type.h"
#include "graphed_svi.h"
#include "draw.h"


/************************************************************************/
/*									*/
/*		GLOBALE FUNKTIONEN / PROZEDUREN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_edge_subframe ()					*/
/*	void	show_edge_subframe   (edge)				*/
/*									*/
/*	void	install_fontlist_in_edge_subframe            (list)	*/
/*	void	install_edgetypelist_in_edge_subframe        (list)	*/
/*	void	update_edgelabel_visibility_in_edge_subframe ()		*/
/*	void	install_current_edgefont_in_edge_subframe    ()		*/
/*									*/
/*	Edge	get_currently_edited_edge ()				*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*		LOKALE FUNKTIONEN / PROZEDUREN				*/
/*									*/
/************************************************************************/


static	void		hide_edge_subframe           (Frame frame);
static	void		notify_edge_subframe_buttons (Panel_item item, Event *event);
static	void		notify_edge_subframe_cycles  (Panel_item item, int value, Event *event);
static	Panel_setting	notify_arrowlength_selection (Panel_item item, Event *event);
static	Panel_setting	notify_arrowangle_selection  (Panel_item item, Event *event);



/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/


#define	EDGELABEL_MAX_LENGTH       1000
#define	EDGELABEL_DISPLAYED_LENGTH 40

static	Frame		edge_subframe;
static	Panel		edge_subframe_panel;

/* static	Panel_item	edge_subframe_quit_button; */
static	Panel_item	edge_subframe_clear_edgelabel_button;
static	Panel_item	edge_subframe_arrowlength_selection;
static	Panel_item	edge_subframe_arrowangle_selection;
static	Panel_item	edge_subframe_label_visibility_cycle;
static	Panel_item	edge_subframe_edgefont_selection;
static	Panel_item	edge_subframe_edgetype_selection;
static	Panel_item	edge_subframe_edgecolor_selection;

static	int		showing_edge_subframe = FALSE;



/************************************************************************/
/*									*/
/*		EDGE_SUBFRAME AUFBAUEN UND VERWALTEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_edge_subframe ()					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	show_edge_subframe (edge)				*/
/*									*/
/*	Setzt die Anzeigen in edge_subframe auf den Stand von edge und	*/
/*	XV_SHOW auf TRUE.						*/
/*	Falls edge_subframe bereits auf dem Bildschirm steht, wird als	*/
/*	erstes hide_edge_subframe ausgefuehrt.				*/
/*									*/
/*======================================================================*/
/*									*/
/*	static	void	hide_edge_subframe ()				*/
/*									*/
/*	Laesst den Kantensubframe bis zum naechsten Gebrauch wieder	*/
/*	verschwinden (edge_subframe wird aus Zeitgruenden nicht		*/
/*	physikalisch geloescht).					*/
/*									*/
/*======================================================================*/
/*									*/
/*	Edge	get_currently_edited_edge ()				*/
/*									*/
/*	Die Kante, die momentan mit edge_subframe editiert wird.	*/
/*	Falls gerade keine editiert wird, empty_edge.			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	install_fontlist_in_edge_subframe     (list)		*/
/*	void	install_edgetypelist_in_edge_subframe (list)		*/
/*									*/
/*	void	update_edgelabel_visibility_in_edge_subframe ()		*/
/*									*/
/*	Installiert edge->label.visibility in edge_subframe neu, wobei	*/
/*	edge = get_currently_edited_edge() (natuerlich nur, wenn diese	*/
/*	existiert !).							*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	install_current_edgefont_in_edge_subframe    ()		*/
/*									*/
/************************************************************************/



void	create_edge_subframe (void)
{
	int		row_count = 0;
	static	char	arrowangle_selection_notify_string [] = {
				CTRL_A, CTRL_B, CTRL_C, CR, '\0'};
	static	char	arrowlength_selection_notify_string [] = {
				CTRL_A, CTRL_B, CTRL_C, CTRL_D, CR, '\0'};

	edge_subframe = (Frame)xv_create(base_frame, FRAME_CMD,
		FRAME_LABEL,			"Edge Editor",
		FRAME_CMD_PUSHPIN_IN,		TRUE,
		FRAME_DONE_PROC,		hide_edge_subframe,
		WIN_CLIENT_DATA,		empty_edge,
		NULL);

	edge_subframe_panel = (Panel)xv_get(edge_subframe, FRAME_CMD_PANEL);

	edge_subframe_label_visibility_cycle = xv_create(edge_subframe_panel, PANEL_CHECK_BOX,
		XV_X,			xv_col(edge_subframe_panel, 0),
		XV_Y,			xv_row(edge_subframe_panel, row_count),
		PANEL_LABEL_STRING,	"label visible",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_CHOICES_BOLD,	TRUE,
		PANEL_NOTIFY_PROC,	notify_edge_subframe_cycles,
		NULL);

	edge_subframe_clear_edgelabel_button = xv_create(edge_subframe_panel, PANEL_BUTTON,
		XV_X,			xv_col(edge_subframe_panel, 20),
		XV_Y,			xv_row(edge_subframe_panel, row_count),
		PANEL_NOTIFY_PROC,	notify_edge_subframe_buttons,
		PANEL_LABEL_STRING,	"Clear Label",
		NULL);	

	row_count++;
	edge_subframe_edgetype_selection = xv_create(edge_subframe_panel, PANEL_CYCLE,
		XV_X,			xv_col(edge_subframe_panel, 0),
		XV_Y,			xv_row(edge_subframe_panel, row_count),
		PANEL_LAYOUT,		PANEL_VERTICAL,
		PANEL_LABEL_STRING,	"edgetype",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_CHOICE_IMAGES,	white_icon_svi, NULL,
		PANEL_NOTIFY_PROC,	notify_edge_subframe_cycles,
		NULL);
	
	edge_subframe_edgefont_selection = xv_create(edge_subframe_panel, PANEL_CYCLE,
		XV_X,			xv_col(edge_subframe_panel, 20),
		XV_Y,			xv_row(edge_subframe_panel, row_count),
		PANEL_LAYOUT,		PANEL_VERTICAL,
		PANEL_LABEL_STRING,	"label font",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_NOTIFY_PROC,	notify_edge_subframe_cycles,
		NULL);
	
	row_count ++;
	edge_subframe_arrowlength_selection = xv_create(edge_subframe_panel, PANEL_TEXT,
		XV_X,				xv_col(edge_subframe_panel, 0),
		XV_Y,				xv_row(edge_subframe_panel, row_count) + DEFAULT_ICON_HEIGHT,
		PANEL_LABEL_STRING,		"arrow length",
		PANEL_LABEL_BOLD,		TRUE,
		PANEL_VALUE,			"",
		PANEL_VALUE_STORED_LENGTH,	5,
		PANEL_VALUE_DISPLAY_LENGTH,	5,
		PANEL_NOTIFY_STRING,		arrowlength_selection_notify_string,
		PANEL_NOTIFY_LEVEL,		PANEL_SPECIFIED,
		PANEL_NOTIFY_PROC,		notify_arrowlength_selection,
		NULL);
	
	edge_subframe_arrowangle_selection = xv_create(edge_subframe_panel, PANEL_TEXT,
		XV_X,				xv_col(edge_subframe_panel, 20),
		XV_Y,				xv_row(edge_subframe_panel, row_count) + DEFAULT_ICON_HEIGHT,
		PANEL_LABEL_STRING,		"angle",
		PANEL_LABEL_BOLD,		TRUE,
		PANEL_VALUE,			"",
		PANEL_VALUE_STORED_LENGTH,	5,
		PANEL_VALUE_DISPLAY_LENGTH,	5,
		PANEL_NOTIFY_STRING,		arrowangle_selection_notify_string,
		PANEL_NOTIFY_LEVEL,		PANEL_SPECIFIED,
		PANEL_NOTIFY_PROC,		notify_arrowangle_selection,
		NULL);

/*
	row_count += 2;


	edge_subframe_edgecolor_selection = create_graphed_color_selection_item (
		edge_subframe_panel,
		xv_col(edge_subframe_panel, 11),
		xv_row(edge_subframe_panel, row_count) + DEFAULT_ICON_HEIGHT,
		notify_edge_subframe_cycles);
*/

	window_fit(edge_subframe_panel);	
	window_fit(edge_subframe);
}


void	show_edge_subframe (Edge edge)
{
	int		x = rect_right  (&(edge->box)) + 10,
			y = rect_bottom (&(edge->box)) + 10;
	int		base_frame_height, base_frame_width;
	int		edge_subframe_height, edge_subframe_width;

	if (showing_edge_subframe) hide_edge_subframe (edge_subframe);
	
	translate_wac_to_base_frame_space (&x,&y);
	base_frame_height    = (int)xv_get(base_frame, XV_HEIGHT);
	base_frame_width     = (int)xv_get(base_frame, XV_WIDTH);
	edge_subframe_height = (int)xv_get(edge_subframe, XV_HEIGHT);
	edge_subframe_width  = (int)xv_get(edge_subframe, XV_WIDTH);
		
	xv_set(edge_subframe_arrowlength_selection,	PANEL_VALUE, int_to_ascii (edge->arrow.length), NULL);
	xv_set(edge_subframe_arrowangle_selection,	PANEL_VALUE, int_to_ascii ((int)rad_to_deg((edge->arrow.angle))), NULL);
	xv_set(edge_subframe_label_visibility_cycle,	PANEL_VALUE, bool_to_int(edge->label.visible), NULL);
	xv_set(edge_subframe_edgetype_selection,	PANEL_VALUE, get_edgetype_index (edge->type), NULL);
	xv_set(edge_subframe_edgefont_selection,	PANEL_VALUE, 
		iif (edgelabel_font(edge) != (Graphed_font)NULL, get_font_index (edgelabel_font(edge)), current_edgefont_index ), NULL);
/* Commented out MH conversion
	xv_set(edge_subframe_edgecolor_selection,	PANEL_VALUE,	edge->color, NULL);
*/
	xv_set(edge_subframe,
		WIN_CLIENT_DATA,	edge,
		WIN_X,			maximum (minimum (x, screenwidth  - edge_subframe_width),  0),
		WIN_Y,			maximum (minimum (y, screenheight - edge_subframe_height), 0),
		XV_SHOW,		TRUE,
		NULL);
	
	showing_edge_subframe = TRUE;
}


static	void	hide_edge_subframe (Frame frame)
     		        /* unused, for FRAME_DONE_PROC only */
{	
	showing_edge_subframe = FALSE;
	
	xv_set(edge_subframe, XV_SHOW, FALSE, NULL);
	xv_set(edge_subframe, WIN_CLIENT_DATA, empty_edge, NULL);
}



Edge	get_currently_edited_edge (void)
{
	return (Edge)xv_get(edge_subframe, WIN_CLIENT_DATA);
}



void	install_fontlist_in_edge_subframe (char **list)
{
	xv_set(edge_subframe_edgefont_selection, ATTR_LIST, list, NULL);
}


void	install_edgetypelist_in_edge_subframe (char *list)
{
	xv_set(edge_subframe_edgetype_selection, ATTR_LIST, list, NULL);
}


void	update_edgelabel_visibility_in_edge_subframe (void)
{
	Edge	edge = (Edge)xv_get(edge_subframe, WIN_CLIENT_DATA);
	
	if (edge != empty_edge)
		xv_set(edge_subframe_label_visibility_cycle, PANEL_VALUE, bool_to_int(edge->label.visible), NULL);
}


void	install_current_edgefont_in_edge_subframe (void)
{
	Edge		edge = (Edge)xv_get(edge_subframe,WIN_CLIENT_DATA);
			
	if (edge != empty_edge && edge->label.font == (Graphed_font)NULL) {
		xv_set(edge_subframe_edgefont_selection, PANEL_VALUE, current_edgefont_index, NULL);
	}
}


/************************************************************************/
/*									*/
/*			NOTIFYPROZEDUREN				*/
/*									*/
/************************************************************************/
/*									*/
/*	static notify_edge_subframe_buttons (item, event)		*/
/*	static notify_edge_subframe_cycles  (item, value, event)	*/
/*									*/
/*	static Panel_setting notify_arrowlength_selection (item, event)	*/
/*	static Panel_setting notify_arrowangle_selection  (item, event)	*/
/*									*/
/*	Wichtig : diese Prozeduren rufen am Ende force_repainting auf.	*/
/*									*/
/************************************************************************/



static	void	notify_edge_subframe_buttons (Panel_item item, Event *event)
{
	Edge	edge = (Edge)xv_get(edge_subframe, WIN_CLIENT_DATA);
	

	if (get_disable_all_modifying_commands() != NULL) {
		return;
	}

	if (item == edge_subframe) {
		hide_edge_subframe ((Frame)0);
	} else if (item == edge_subframe_clear_edgelabel_button) {
		edge_set (edge, EDGE_LABEL, NULL, 0);
		install_current_edgefont_in_edge_subframe ();
	}
	
	force_repainting();
}


static	void	notify_edge_subframe_cycles (Panel_item item, int value, Event *event)
{
	Edge	edge = (Edge)xv_get(edge_subframe, WIN_CLIENT_DATA);
	
	if (get_disable_all_modifying_commands() != NULL) {
		return;
	}

	if (item == edge_subframe_label_visibility_cycle) {
		edge_set (edge, EDGE_LABEL_VISIBILITY, int_to_bool(value), 0);
	} else if (item == edge_subframe_edgetype_selection) {
		edge_set (edge, EDGE_TYPE, value, 0);
	} else if (item == edge_subframe_edgefont_selection) {
		edge_set (edge, EDGE_FONT, value, 0);
	} else if (item == edge_subframe_edgecolor_selection) {
		edge_set (edge, EDGE_COLOR, value, 0);
	}
	force_repainting();
}


static	Panel_setting	notify_arrowlength_selection (Panel_item item, Event *event)
{
	Edge	edge = (Edge)xv_get(edge_subframe, WIN_CLIENT_DATA);
	int	length;
	
	if (get_disable_all_modifying_commands() != NULL) {
		return PANEL_NONE;
	}

	switch (event_id (event)) {
		case CTRL_A :	xv_set(edge_subframe_arrowlength_selection, PANEL_VALUE, "0", NULL);
				edge_set (edge, EDGE_ARROW_LENGTH, 0, 0);
	 			break;
		case CTRL_B :	if (edge->source->graph->directed) {
					edge_set (edge, EDGE_ARROW_LENGTH, 8, 0);
	 				xv_set(edge_subframe_arrowlength_selection, PANEL_VALUE, "8", NULL);
	 			}
	 			break;
		case CTRL_C :	if (edge->source->graph->directed) {
					edge_set (edge, EDGE_ARROW_LENGTH, 12, 0);
	 				xv_set(edge_subframe_arrowlength_selection, PANEL_VALUE, "12", NULL);
	 			}
				break;
		case CTRL_D :	if (edge->source->graph->directed) {
					edge_set (edge, EDGE_ARROW_LENGTH, 16, 0);
	 				xv_set(edge_subframe_arrowlength_selection, PANEL_VALUE, "16", NULL);
				}
				break;
		case CR :	length = atoi ((char *)xv_get(edge_subframe_arrowlength_selection, PANEL_VALUE));
				if (edge->source->graph->directed)
					edge_set (edge, EDGE_ARROW_LENGTH, length, 0);
				else {
					edge_set (edge, EDGE_ARROW_LENGTH, 0, 0);
	 				xv_set(edge_subframe_arrowlength_selection, PANEL_VALUE, "0", NULL);
				}
				break;
		default :
				break;
	}
	
	force_repainting ();
	return PANEL_NONE;
}


static	Panel_setting	notify_arrowangle_selection (Panel_item item, Event *event)
{
	Edge	edge = (Edge)xv_get(edge_subframe, WIN_CLIENT_DATA);
	double	angle;
	
	if (get_disable_all_modifying_commands() != NULL) {
		return PANEL_NONE;
	}

	switch (event_id (event)) {
		case CTRL_A :	xv_set(edge_subframe_arrowangle_selection, PANEL_VALUE, "30", NULL);
				edge_set (edge, EDGE_ARROW_ANGLE, deg_to_rad (30), 0);
	 			break;
		case CTRL_B :	xv_set(edge_subframe_arrowangle_selection, PANEL_VALUE, "45", NULL);
				edge_set (edge, EDGE_ARROW_ANGLE, deg_to_rad (45), 0);
				break;
		case CTRL_C :	xv_set(edge_subframe_arrowangle_selection, PANEL_VALUE, "60", NULL);
				edge_set (edge, EDGE_ARROW_ANGLE, deg_to_rad (60), 0);
				break;
		case CR :	angle  = deg_to_rad (atoi ((char *)xv_get(edge_subframe_arrowangle_selection, PANEL_VALUE)));
				edge_set (edge, EDGE_ARROW_ANGLE, angle, 0);
				break;
	}
	
	force_repainting ();
	return PANEL_NONE;
}
