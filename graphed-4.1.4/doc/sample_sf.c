/* (C) Universitaet Passau 1986-1994 */
/* A clone of the sugyiama subframe */
#include <xview/xview.h>
#include <xview/panel.h>

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/algorithms.h>
#include "layout_suite_export.h"
#include <graphed/graphed_pin_sf.h>


extern	Frame	base_frame;
extern	char	*int_to_ascii ();


static	void	create_suite_subframe ();
static	void	suite_sf_set   ();
static	void	suite_sf_reset ();
static	void	suite_sf_do    ();
static	void	suite_sf_done  ();


static	Graphed_pin_subframe	suite_subframe = (Graphed_pin_subframe)NULL;


static	void	create_suite_subframe()
{
	int	row_count = 0;
	
	suite_subframe = new_graphed_pin_subframe();
	suite_subframe->set_proc   = suite_sf_set;
	suite_subframe->reset_proc = suite_sf_reset;
	suite_subframe->do_proc    = suite_sf_do;
	suite_subframe->done_proc  = suite_sf_done;

	graphed_create_pin_subframe (suite_subframe, "Layout suite");

	window_fit(suite_subframe->panel);
	window_fit(suite_subframe->frame);
}



void	show_layout_suite_subframe ()
{
	if (suite_subframe == (Graphed_pin_subframe)NULL || !suite_subframe->showing) {
		create_suite_subframe();
	}
	
	if (suite_subframe != (Graphed_pin_subframe)NULL) {
		xv_set (suite_subframe->frame, XV_SHOW, TRUE, 0);
		suite_subframe->showing = TRUE;
	} else {
		suite_subframe->showing = FALSE;
	}
}


static	void	suite_sf_set (item, event)
Panel_item	item;
Event *		event;
{
	save_layout_suite_settings ();
}


static	void	suite_sf_reset (item, event)
Panel_item	item;
Event *		event;
{
/*	save_layout_suite_settings (); */
}


static	void	suite_sf_do (item, event)
Panel_item	item;
Event *		event;
{
	save_layout_suite_settings ();
	layout_suite(layout_suite_settings);
}


static	void	suite_sf_done (frame)
Frame		frame;
{
	save_layout_suite_settings ();
}
