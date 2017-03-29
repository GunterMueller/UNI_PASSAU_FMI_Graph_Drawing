#include "graph.h"
#include "misc.h"
#include <xview/xview.h>
#include <xview/panel.h>
#include "graphed_pin_sf.h"
#include "graphed_subwindows.h"

Graphed_pin_subframe	new_graphed_pin_subframe (Frame frame)
{
	Graphed_pin_subframe	sf;

	sf = (Graphed_pin_subframe) malloc(sizeof(struct graphed_pin_subframe));

	sf->frame = (Frame)NULL;
	sf->panel = (Panel)NULL;

	sf->set_button   = (Panel_item)NULL;
	sf->reset_button = (Panel_item)NULL;
	sf->do_button    = (Panel_item)NULL;

	sf->set_proc = (void (*)())NULL;
	sf->reset_proc = (void (*)())NULL;
	sf->do_proc = (void (*)())NULL;
	sf->done_proc = (void (*)())NULL;
	sf->graphed_done_proc = (void (*)())NULL;

	sf->showing = FALSE;

	sf->do_label = NULL;
	sf->reset_label = NULL;
	sf->set_label = NULL;

	return sf;
}



static	void pin_sf_done_proc (Frame frame)
{
	Graphed_pin_subframe	sf;

	sf = (Graphed_pin_subframe) xv_get (
		xv_get(frame, FRAME_CMD_PANEL),
		WIN_CLIENT_DATA);

	if (sf->done_proc != NULL) {
		(sf->done_proc)(frame);
	}

	if (sf->graphed_done_proc != NULL) {
		(sf->graphed_done_proc)(sf);
	}

	xv_destroy_safe(sf->frame);
	sf->showing = FALSE;
}



static	void	pin_sf_set_proc (Panel_item item, Event *event)
{
	Graphed_pin_subframe	sf;

	sf = (Graphed_pin_subframe) xv_get (item, PANEL_CLIENT_DATA);

	if (sf->set_proc != NULL) {
		(sf->set_proc)(item, event);
	}
}



static	void pin_sf_reset_proc (Panel_item item, Event *event)
{
	Graphed_pin_subframe	sf;

	sf = (Graphed_pin_subframe) xv_get (item, PANEL_CLIENT_DATA);

	if (sf->reset_proc != NULL) {
		(sf->reset_proc)(item, event);
	}
}



static	void pin_sf_do_proc (Panel_item item, Event *event)
{
	Graphed_pin_subframe	sf;

	sf = (Graphed_pin_subframe) xv_get (item, PANEL_CLIENT_DATA);

	if (sf->set_proc != NULL) {
		(sf->set_proc)(item, event);
	}
	if (sf->do_proc != NULL) {
		(sf->do_proc)(item, event);
	}
}



Graphed_pin_subframe	graphed_create_pin_subframe (Graphed_pin_subframe sf, char *label)
{
	sf->frame = (Frame)xv_create(base_frame, FRAME_CMD,
		FRAME_LABEL,		label,
		FRAME_CMD_PUSHPIN_IN,	TRUE,
		FRAME_DONE_PROC,	pin_sf_done_proc,
		NULL);

	if (sf->frame == (Panel)NULL) {
		error ("Cannot create frame %s\n", label);
		sf = (Graphed_pin_subframe)NULL;
		return sf;
	}

	sf->panel = (Panel)xv_get(sf->frame, FRAME_CMD_PANEL);

	if (sf->panel == (Panel)NULL) {
		error ("Cannot create panel of frame %s\n", label);
		sf = (Graphed_pin_subframe)NULL;
		return sf;
	}

	xv_set (sf->panel, WIN_CLIENT_DATA, sf, NULL);
	sf->showing = FALSE;

	if (sf->set_proc != (void (*)())NULL) {
		sf->set_button = xv_create(sf->panel, PANEL_BUTTON,
			PANEL_LABEL_STRING,	iif (sf->set_label != NULL,
				sf->set_label,
				"Set"),
			XV_X,			xv_col(sf->panel, 0),
			PANEL_NOTIFY_PROC,	pin_sf_set_proc,
			PANEL_CLIENT_DATA,	sf,
			NULL);
	}

	if (sf->reset_proc != (void (*)())NULL) {
		sf->reset_button = xv_create(sf->panel, PANEL_BUTTON,
			PANEL_LABEL_STRING,	iif (sf->reset_label != NULL,
				sf->reset_label,
				"Reset"),
			XV_X,			xv_col(sf->panel, 10),
			PANEL_NOTIFY_PROC,	pin_sf_reset_proc,
			PANEL_CLIENT_DATA,	sf,
			NULL);
	}

	if (sf->do_proc != (void (*)())NULL) {
		sf->do_button = xv_create(sf->panel, PANEL_BUTTON,
			PANEL_LABEL_STRING,	iif (sf->do_label != NULL,
				sf->do_label,
				"run"),
			XV_X,			xv_col(sf->panel, 20),
			PANEL_NOTIFY_PROC,	pin_sf_do_proc,
			PANEL_CLIENT_DATA,	sf,
			NULL);
	}

	return sf;
}



int	showing_graphed_pin_subframe (Graphed_pin_subframe sf)
{
	return sf != (Graphed_pin_subframe)NULL && sf->showing;
}
