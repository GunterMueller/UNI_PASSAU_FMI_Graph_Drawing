/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				canvas.c				*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul verwaltet die Arbeitsflaeche (working_area_canvas)	*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"
#include "graphed_subwindows.h"
#include "graphed_mpr.h"

#include "user.h"
#include "repaint.h"

#include "menu.h"

/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_canvas (n, canvas_width, canvas_height)		*/
/*									*/
/*	void	set_working_area_canvas       (canvas)			*/
/*	void	set_wac_mouse_position        (x,y)			*/
/*	void	set_working_area_size         (width, height)		*/
/*									*/
/*	void	set_canvas_window             (n, x,y, width, height)	*/
/*	void	set_canvas_window_size        (n, width, height)	*/
/*									*/
/*	void	scroll_working_area           (x,y)			*/
/*	void	scroll_working_area_relative  (dx,dy)			*/
/*	void	scroll_working_area_to_middle (dx,dy)			*/
/*									*/
/*	void	get_scroll_offset (buffer, offset_x, offset_y)		*/
/*	void	translate_wac_to_base_frame_space (x,y)			*/
/*									*/
/************************************************************************/


static	Notify_value	canvas_destroyer    (Notify_client frame, Destroy_status status);
static	void		set_canvas_colormap (Canvas canvas);

/************************************************************************/
/*									*/
/*			GLOBALE VARIABLEN				*/
/*									*/
/************************************************************************/


/***************	working_area_canvas (wac)	*****************/

Graphed_canvas	canvases [N_BUFFERS];

Canvas	working_area_canvas;		/* Aktuelle Arbeitsflaeche	*/
Panel	menubar_panel;
Panel	toolbar_panel;

/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/


u_char	red   [GRAPHED_COLORMAPSIZE],
	green [GRAPHED_COLORMAPSIZE],
	blue  [GRAPHED_COLORMAPSIZE];


/************************************************************************/
/*									*/
/*				CANVAS AUFBAUEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_canvas (n, canvas_width, canvas_height)		*/
/*									*/
/*	Createsa canvas no. n.						*/
/*	XV_SHOW remains FALSE.						*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	set_canvas_window      (n, x,y, width, height)		*/
/*	void	set_canvas_window_size (n, width, height)		*/
/*									*/
/*	Set size and position (resp. only size) of canvases[n].		*/
/*	This procedure does not change the canvas, but the frame !	*/
/*	XV_SHOW is set to TRUE.						*/
/*									*/
/*======================================================================*/
/*									*/
/*	show_grid (draw.c) and set_filename (load.c) also modify the	*/
/*	'canvases'-structure !						*/
/*									*/
/************************************************************************/


void	init_canvases (void)
{
	int	i;
	
	for (i=0; i<N_BUFFERS; i++) {
		canvases[i].frame  = (Frame)NULL;
		canvases[i].canvas = (Canvas)NULL;
		canvases[i].horizontal_scrollbar = (Scrollbar)NULL;
		canvases[i].vertical_scrollbar   = (Scrollbar)NULL;
		canvases[i].gridwidth = 0;
		canvases[i].startup_scroll_x = -42;	
		canvases[i].startup_scroll_y = -42;
		canvases[i].canvas_seen_by_working_area_event_proc = FALSE;
	}
}


#include <images/graphed_canvas_icon.xbm>

/************************************************************************/
/*									*/
/*				Create Canvas				*/
/*									*/
/************************************************************************/


static	void canvas_frame_done_proc (Frame frame);


int	create_canvas (int n, int canvas_width, int canvas_height)
{
	/*	Create a frame to contain the canvas			*/
	
	canvases[n].frame = (Frame)xv_create(XV_NULL, FRAME,
		FRAME_DONE_PROC,	canvas_frame_done_proc,
		XV_WIDTH,		canvas_width,
		XV_HEIGHT,		canvas_height,
		NULL);

	xv_set (canvases[n].frame,
		FRAME_ICON, xv_create (canvases[n].frame, ICON,
			ICON_IMAGE, xv_create(XV_NULL, SERVER_IMAGE,
				XV_WIDTH,            graphed_canvas_icon_width,
				XV_HEIGHT,           graphed_canvas_icon_width,
				SERVER_IMAGE_X_BITS, graphed_canvas_icon_bits,
				NULL),
			NULL),
		NULL);

	canvases[n].menubar = create_menubar_panel (n);
	canvases[n].toolbar = create_toolbar_panel (n);

	canvases[n].canvas = (Canvas)xv_create(canvases[n].frame, CANVAS,
		WIN_BELOW,		canvases[n].menubar.panel,
		WIN_RIGHT_OF,		canvases[n].toolbar.panel,
		CANVAS_RETAINED,        FALSE,
		CANVAS_AUTO_SHRINK,	FALSE,
		CANVAS_AUTO_EXPAND,	FALSE,
		OPENWIN_AUTO_CLEAR,	FALSE,
		CANVAS_FIXED_IMAGE,	FALSE,
		CANVAS_REPAINT_PROC,	repaint_canvas,
		CANVAS_X_PAINT_WINDOW,  TRUE,
		NULL);
	xv_set (canvases[n].menubar.panel,
		WIN_CONSUME_EVENTS,	WIN_MOUSE_BUTTONS, NULL,
		WIN_CLIENT_DATA,	canvases[n].canvas,
		NULL);

	canvases[n].horizontal_scrollbar = (Scrollbar)xv_create(canvases[n].canvas, SCROLLBAR,
		SCROLLBAR_DIRECTION,	SCROLLBAR_HORIZONTAL,
		SCROLLBAR_SPLITTABLE,	FALSE,
		NULL);
	canvases[n].vertical_scrollbar = (Scrollbar)xv_create(canvases[n].canvas, SCROLLBAR,
		SCROLLBAR_DIRECTION,	SCROLLBAR_VERTICAL,
		SCROLLBAR_SPLITTABLE,	FALSE,
		NULL);

	xv_set(canvas_paint_window(canvases[n].canvas),
		WIN_EVENT_PROC,		working_area_event_proc,
		WIN_CONSUME_EVENTS,	KBD_DONE, KBD_USE,
					LOC_DRAG, LOC_MOVE,
					LOC_WINENTER, LOC_WINEXIT,
					WIN_ASCII_EVENTS, WIN_META_EVENTS,
					WIN_MOUSE_BUTTONS,
	       				WIN_TOP_KEYS, WIN_LEFT_KEYS, WIN_RIGHT_KEYS,
					NULL,
		NULL);

	canvases[n].pixwin = canvas_paint_window (canvases[n].canvas);

	set_canvas_colormap(canvases[n].canvas);
	
	set_canvas_frame_label(n);
	notify_interpose_destroy_func(canvases[n].frame, canvas_destroyer);

	return	TRUE;
}


static	void canvas_frame_done_proc (Frame frame)
{
	xv_destroy_safe (frame);
}


static	Notify_value	canvas_destroyer (Notify_client frame, Destroy_status status)
{
	Prompt	user_choice;
	int	i, this_buffer;
	int	frame_still_exists;
	int	used_buffers;

	for (i=0; i<N_BUFFERS; i++) {
		if (canvases[i].frame == frame) {
			break;
		}
	}
	frame_still_exists = (i<N_BUFFERS);
	if (frame_still_exists) {
		this_buffer = i;
	}

	used_buffers = 0;
	for (i=N_PASTE_BUFFERS; i<N_BUFFERS; i++) {
		if (buffers[i].used) {
			used_buffers ++;
		}
	}

	
	if (status == DESTROY_CHECKING && !graphed_state.shutdown) {
		
		dispatch_user_action (UNSELECT);

		if (frame_still_exists) {
			if (user_interface_check_destroy_buffer (i) == FALSE) {
				(void)notify_veto_destroy(frame);
				bell ();
				return NOTIFY_DONE;
			} else if (buffers[this_buffer].changed &&
                	           !((buffers[this_buffer].filename != NULL) &&
			             (buffers[this_buffer].filename[0] == '-'))) {
				user_choice = notice_prompt (frame, NULL,
					NOTICE_FOCUS_XY,
						screenwidth/3,
						screenheight/2,
					NOTICE_MESSAGE_STRINGS,
						"The contents of this window have not been saved.",
						"Do you really want to close this window ?",
						NULL,
					NOTICE_BUTTON, "yes",    PROMPT_ACCEPT,
					NOTICE_BUTTON, "no",     PROMPT_REFUSE,
					NOTICE_BUTTON, "cancel", PROMPT_CANCEL,
					NULL);
				switch (user_choice) {
				    case PROMPT_ACCEPT :
					break;
				    case PROMPT_REFUSE :
				    case PROMPT_CANCEL :
					(void)notify_veto_destroy(frame);
					return NOTIFY_DONE;
					break;
				}
			}
		}
		
		xv_set(frame, FRAME_NO_CONFIRM, TRUE, NULL);

		if (frame_still_exists) {

			/* Added MH Conversion			    */
			/* because frame might be already destroyed */
			delete_graphs_in_buffer (this_buffer);

			/* cannot call delete_buffer because	*/
			/* this proc calls xv_destroy_safe !	*/
			unuse_buffer (this_buffer);

			canvases[this_buffer].frame  = (Frame)NULL;
			canvases[this_buffer].canvas = (Canvas)NULL;
			canvases[this_buffer].horizontal_scrollbar = (Scrollbar)NULL;
			canvases[this_buffer].vertical_scrollbar   = (Scrollbar)NULL;
			canvases[this_buffer].pixwin               = (Xv_Window)NULL;
			canvases[this_buffer].gridwidth            = 0;
		}
	
		if (!graphed_state.shutdown && used_buffers == 1) {
			xv_destroy_safe (base_frame);
			return NOTIFY_DONE;
		}

	} else {
	
		/* skip	*/
	}

	return(notify_next_destroy_func(frame,status));
}



void	destroy_frame_and_canvas (int n)
{
	if (canvases[n].frame != XV_NULL) {
		xv_destroy_safe(canvases[n].frame);
	
		canvases[n].frame  = (Frame)NULL;
		canvases[n].canvas = (Canvas)NULL;
		canvases[n].horizontal_scrollbar = (Scrollbar)NULL;
		canvases[n].vertical_scrollbar   = (Scrollbar)NULL;
		canvases[n].pixwin               = (Xv_Window)NULL;
		canvases[n].gridwidth = 0;
	}
}




void	set_canvas_frame_label (int n)
{
	int	edited;
	char	filename [FILENAMESIZE];
	char	buffer   [FILENAMESIZE];
	char	icon_buffer [FILENAMESIZE];
	
	if (canvases[n].frame == XV_NULL) {
		/* After a window was deleted, GraphEd still wants to re-set	*/
		/* its frame label, MH 20/9/94					*/
		return;
	}
	
	edited = buffers[n].changed;
	
	if (buffers[n].filename == NULL || !strcmp(buffers[n].filename, "")) {
		strcpy (filename, "(None)");
	} else {
		strncpy (filename, buffers[n].filename, FILENAMESIZE);
	}
	
	sprintf (buffer, "%s FILE : %s %s  ---  [%s] [%s] [%s]",
		iif (n == wac_buffer, "*", " "),
		filename,
		iif (edited, "(edited)", ""),
		iif (get_graphed_mode() == GRAPHED_MODE_CREATE_MODE,
			"Create",
			"Edit"),
		iif (get_graphed_constrained(),
			"constrained",
			"unconstrained"),
		iif (get_graphed_group_labelling_operation() == EDGE,
		     "group labels edge",
		     "group labels node")
		);
	
	sprintf (icon_buffer, "%s%s", iif (edited, ">", ""), filename);
	
	xv_set(canvases[n].frame, XV_LABEL, buffer, NULL);
	icon_set (xv_get(canvases[n].frame, FRAME_ICON),
		XV_LABEL, strsave (icon_buffer),
		0);
}



void	set_canvas_window_size (int n, int width, int height)
{
	xv_set(canvases[n].frame,
		XV_WIDTH,	width,
		XV_HEIGHT,	height,
		NULL);

	if ((int)xv_get (canvases[n].frame, XV_SHOW) == FALSE) {
		xv_set (canvases[n].frame,
			XV_SHOW,   TRUE,
			0);
	}
}




void	set_canvas_window (int n, int x, int y, int width, int height)
{
	xv_set(canvases[n].frame,
		WIN_X,		x,
		WIN_Y,		y,
		XV_WIDTH,	width,
		XV_HEIGHT,	height,
		NULL);

	if ((int)xv_get (canvases[n].frame, XV_SHOW) == FALSE) {
		xv_set (canvases[n].frame,
			XV_SHOW,   TRUE,
			0);
	}
}



void	set_canvas_window_silent (int n, int x, int y, int width, int height)
{
	xv_set(canvases[n].frame,
		WIN_X,		x,
		WIN_Y,		y,
		XV_WIDTH,	width,
		XV_HEIGHT,	height,
		NULL);
}
/************************************************************************/
/*									*/
/*			COLORMAP					*/
/*									*/
/************************************************************************/
/*									*/
/*	void	init_graphed_colormap ()				*/
/*									*/
/*======================================================================*/
/*									*/
/*	static	void	set_canvas_colormap (canvas)			*/
/*									*/
/************************************************************************/


void	init_graphed_colormap (void)
{
#define set_color(i,r,g,b)	\
	red  [(i)] = (r);	\
	green[(i)] = (g);	\
	blue [(i)] = (b);
	
	/* MUST BE CONSISTENT WITH THE CONSTANTS BLACK AND WHITE	*/
	/* DECLARED IN PAINT.H						*/
	
	set_color (0,  255, 255, 255);
	set_color (1,  255, 0,   0);
	set_color (2,  0,   255, 0);
	set_color (3,  0,   0,   255);
	set_color (4,  0,   255, 255);
	set_color (5,  255, 255, 0);
	set_color (6,  255, 0,   255);
	set_color (7,  0,   0,   0);
	
	set_color (8,  192, 192, 192);
	set_color (9,  192, 0,   0);
	set_color (10, 0,   192, 0);
	set_color (11, 0,   0,   192);
	set_color (12, 0,   192, 192);
	set_color (13, 192, 192, 0);
	set_color (14, 192, 0,   192);
	set_color (15, 0,   0,   0);
}


static	void	set_canvas_colormap (Canvas canvas)
{	
/* Commented out MH Conversion

	pw = canvas_paint_window (canvas);
	
	pw_setcmsname  (pw, GRAPHED_COLORMAP_NAME);
	pw_putcolormap (pw, 0, GRAPHED_COLORMAPSIZE, red, green, blue);
	
	xv_set(canvas,
		WIN_VERTICAL_SCROLLBAR,
			(Scrollbar)xv_get(canvas, WIN_VERTICAL_SCROLLBAR),
		WIN_HORIZONTAL_SCROLLBAR,
			(Scrollbar)xv_get(canvas, WIN_HORIZONTAL_SCROLLBAR),
		NULL);
*/
}
/************************************************************************/
/*									*/
/*			VERWALTUNGSPROZEDUREN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	set_working_area_canvas (canvas)			*/
/*									*/
/*	Setzt canvas als aktuelle working area. Eine Anzahl globaler	*/
/*	Variablen, die dazu gehoeren, werden ebenfalls umgesetzt.	*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	set_wac_mouse_position (x,y)				*/
/*									*/
/*	Setzt die Cursorposition auf der working_area.			*/
/*									*/
/*======================================================================*/
/*									*/
/*	int	set_working_area_size (width, height)			*/
/*									*/
/*	Setzt die Groesse des working_area_canvas neu. Falls die neue	*/
/*	Groesse nicht gesetzt werden kann, da der Graph zu gross ist,	*/
/*	wird FALSE zurueckgegeben, sonst TRUE.				*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	scroll_working_area           (x,y)			*/
/*	(x,y) sind linke obere Ecke des Fensters.			*/
/*									*/
/*	void	scroll_working_area_relative  (dx,dy)			*/
/*									*/
/*	void	scroll_working_area_to_middle ()			*/
/*									*/
/************************************************************************/


void	set_working_area_canvas (Canvas canvas)
{
	int	i,
		old_wac_buffer = wac_buffer;
	
	/* a call to force_repainting is necessary to repaint the	*/
	/* previous canvas ... but only if it is used			*/
	if (buffers[old_wac_buffer].used)
		force_repainting ();
	if (old_wac_buffer >= N_PASTE_BUFFERS)
		set_canvas_frame_label (old_wac_buffer);
	
	/* set the new buffer	*/
	working_area_canvas = canvas;
	for (i=0; i<N_BUFFERS; i++) {
		if (canvases[i].canvas == canvas)
			break;
	}
	wac_buffer = i;
	
	if (!graphed_state.startup) {
		set_canvas_frame_label (old_wac_buffer);
	}
	set_canvas_frame_label (wac_buffer);	/* update the labels */
}


void	set_mouse_position (int buffer, int x, int y)
{
	int	scroll_offset_x, scroll_offset_y;
	
	get_scroll_offset (buffer, &scroll_offset_x, &scroll_offset_y);
	xv_set (canvases[buffer].canvas,
		WIN_MOUSE_XY, x - scroll_offset_x, y - scroll_offset_y,
		NULL);
}


void	set_wac_mouse_position (int x, int y)
{
	set_mouse_position (wac_buffer, x,y);
}



int	set_buffer_size (int buffer, int width, int height)
{
	Rect	graphs_rect;
	
	graphs_rect = compute_rect_around_graphs (buffer);
	
	if (width  < rect_right(&graphs_rect) ||
	    height < rect_bottom(&graphs_rect)) {
		warning ("Cannot make buffer smaller.\n");
		return FALSE;
	} 
	
	xv_set(canvases[buffer].canvas,
		CANVAS_WIDTH,	width,
		CANVAS_HEIGHT,	height,
		NULL);
		
	return TRUE;
}



int	set_working_area_size (int width, int height)
{
	return	set_buffer_size (wac_buffer, width, height);
}



void	scroll_buffer (int buffer, int x, int y)
{
	x = minimum (x,
		(int)xv_get(canvases[buffer].horizontal_scrollbar,
			SCROLLBAR_OBJECT_LENGTH) -
		(int)xv_get(canvases[buffer].horizontal_scrollbar,
			SCROLLBAR_VIEW_LENGTH));
	y = minimum (y,
		(int)xv_get(canvases[buffer].vertical_scrollbar,
			SCROLLBAR_OBJECT_LENGTH) -
		(int)xv_get(canvases[buffer].vertical_scrollbar,
			SCROLLBAR_VIEW_LENGTH));
	x = maximum (x,0);
	y = maximum (y,0);

	xv_set (canvases[buffer].horizontal_scrollbar,
		SCROLLBAR_VIEW_START, x,
		NULL);
	xv_set (canvases[buffer].vertical_scrollbar,
		SCROLLBAR_VIEW_START, y,
		NULL);
}


void	scroll_working_area (int x, int y)
{
	scroll_buffer (wac_buffer, x,y);
}


void	scroll_working_area_relative (int dx, int dy)
{
	int	scroll_offset_x, scroll_offset_y;
	
	get_scroll_offset  (wac_buffer, &scroll_offset_x, &scroll_offset_y);
	scroll_working_area (scroll_offset_x + dx, scroll_offset_y + dy);
}


void	scroll_working_area_to_middle (void)
{
	scroll_working_area (
		(int)xv_get(working_area_canvas, CANVAS_WIDTH)/2
			- (int)xv_get(working_area_canvas, XV_WIDTH) / 2,
		(int)xv_get(working_area_canvas, CANVAS_HEIGHT)/2
			- (int)xv_get(working_area_canvas, XV_HEIGHT) / 2);
}


void	center_buffer_around (int buffer, int x, int y)
{
	int	win_width  = (int)xv_get(canvases[buffer].canvas, XV_WIDTH);
	int	win_height = (int)xv_get(canvases[buffer].canvas, XV_HEIGHT);
	
	scroll_buffer (buffer, x-win_width/2, y-win_height/2);
}


void	get_buffer_center (int buffer, int *x, int *y)
{
	int	win_width  = (int)xv_get(canvases[buffer].canvas, XV_WIDTH);
	int	win_height = (int)xv_get(canvases[buffer].canvas, XV_HEIGHT);
	int	scroll_start_x, scroll_start_y;
	
	get_scroll_offset (buffer, &scroll_start_x, &scroll_start_y);
	
	*x = scroll_start_x + win_width/2;
	*y = scroll_start_y + win_height/2;
}


void	get_buffer_visible_rect (int buffer, Rect *rect)
{
	int	win_width  = (int)xv_get(canvases[buffer].canvas, XV_WIDTH);
	int	win_height = (int)xv_get(canvases[buffer].canvas, XV_HEIGHT);
	int	scroll_start_x, scroll_start_y;
	
	get_scroll_offset (buffer, &scroll_start_x, &scroll_start_y);
	
	rect_construct (rect, scroll_start_x,
	                      scroll_start_y,
	                      win_width,
	                      win_height);	
}


void	get_buffer_rect (int buffer, Rect *rect)
{
	int	win_width  = (int)xv_get(canvases[buffer].canvas, CANVAS_WIDTH);
	int	win_height = (int)xv_get(canvases[buffer].canvas, CANVAS_HEIGHT);
	
	rect_construct (rect, 0, 0, win_width, win_height);
}
/************************************************************************/
/*									*/
/*	    HILFSPROZEDUREN ZUR KOORNINATENTRANSFORMATION		*/
/*									*/
/************************************************************************/
/*									*/
/*	void	get_scroll_offset (buffer, offset_x,offset_y);		*/
/*									*/
/*	Gibt den Wert zurueck, um den der working_area_canvas gescrollt	*/
/*	ist (in Pixeln).						*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	translate_wac_to_base_frame_space (x,y)			*/
/*									*/
/*	Koordinatenumrechnung. Diese Transformation wird z.B.		*/
/*	gebraucht, um einen Subframe anhand von Canvaskoordinaten	*/
/*	zu positionieren.						*/
/*									*/
/************************************************************************/


void	get_scroll_offset (int buffer, int *offset_x, int *offset_y)
{
	*offset_x = xv_get(canvases[buffer].horizontal_scrollbar, SCROLLBAR_VIEW_START);
	*offset_y = xv_get(canvases[buffer].vertical_scrollbar, SCROLLBAR_VIEW_START);
}


void	translate_wac_to_base_frame_space (int *x, int *y)
{
	int		scrolled_x, scrolled_y;
	
	get_scroll_offset (wac_buffer, &scrolled_x, &scrolled_y);
	*x = *x - (scrolled_x + (int)xv_get(working_area_canvas, WIN_X))
	        + (int)xv_get(canvases[wac_buffer].canvas, XV_LEFT_MARGIN)
	        + (int)xv_get(canvases[wac_buffer].frame, WIN_X)
		+ (int)xv_get(canvases[wac_buffer].toolbar.panel, XV_WIDTH);
	*y = *y - (scrolled_y + (int)xv_get(working_area_canvas, WIN_Y))
	        + (int)xv_get(canvases[wac_buffer].canvas, XV_TOP_MARGIN)
	        + (int)xv_get(canvases[wac_buffer].frame, WIN_Y)
		+ (int)xv_get(canvases[wac_buffer].menubar.panel, XV_HEIGHT);;

}
