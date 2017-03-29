/****************************************************************************\
 *                                                                          *
 *  nonplanarity_subframe.c                                                 *
 *  -----------------------                                                 *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h> 


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>

#include "graphed_pin_sf.h"

#include "nonplanarity_export.h"


Local	bool	createMaxPlanarSubFrame(void);
Local	void	showMaxPlanarSubFrame(void);
Local	void	createThicknessSubFrame(void);
Local	void	showThicknessSubFrame(void);
Local	void	createCrossingNumberSubFrame(void);
Local	void	showCrossingNumberSubFrame(void);

Local	void	MPGWindowNotify(Panel_item item, int value, Event *event);
Local	void	MPGNewWindowNotify(Panel_item item, int value, Event *event);
Local	void	MPGDone(Frame frame);



#include "nonplanarity_settings.h"

extern	MaxPlanarSettings	currMaxPlanarSettings;
extern	ThicknessSettings	currThicknessSettings;
extern	CrossingNumberSettings	currCrossingNumberSettings;



extern	Frame			base_frame;
Local	Frame			MPGFrame;
Local   bool			MPGFrameShowing;
Local	Panel			MPGPanel;
Local	Panel_item		WindowCheck, NewWindowCheck;
/* Local	Graphed_pin_subframe	MaxPlanarSubFrame =
					(Graphed_pin_subframe)NULL; */

static	Graphed_pin_subframe	CrossingNumberSubFrame =
					(Graphed_pin_subframe)NULL;
static	Graphed_pin_subframe	ThicknessSubFrame =
					(Graphed_pin_subframe)NULL;




void MenuMaxPlanarSettings (Menu menu, Menu_item menu_item)
{
  showMaxPlanarSubFrame();
/*  if (createMaxPlanarSubFrame() == FALSE) {
     warning ("\nSettings window could not be created !\n");
     bell();
  } */

}


void MenuThicknessSettings (Menu menu, Menu_item menu_item)
{
	showThicknessSubFrame();
}



void MenuCrossingNumberSettings (Menu menu, Menu_item menu_item)
{
	showCrossingNumberSubFrame();
}



Local	bool	createMaxPlanarSubFrame(void)
{
  MPGFrame = (Frame) xv_create (base_frame, FRAME_CMD,
		XV_LABEL,               "Maximaler planarer Subgraph",
		FRAME_CMD_PUSHPIN_IN,   TRUE,
		FRAME_SHOW_LABEL,       TRUE,
		WIN_SHOW,               TRUE,
		FRAME_NO_CONFIRM,       TRUE,
		FRAME_DONE_PROC,        MPGDone,
		NULL);

  if (MPGFrame == (Frame)NULL)  return FALSE;

  MPGPanel = (Panel) xv_get (MPGFrame, FRAME_CMD_PANEL);

  if (MPGPanel == (Panel)NULL)  return FALSE;

   xv_set (MPGPanel,
		PANEL_LAYOUT,	PANEL_VERTICAL,
		NULL);

/* choose possible algorithms and go button ??? */

   WindowCheck = xv_create (MPGPanel, PANEL_CHECK_BOX,
		     PANEL_LABEL_STRING, "window:   ",
		     PANEL_CHOICE_STRINGS,
		     "create new window for max. pl. subgraph",
		     "mark edges in old window", NULL,
		     PANEL_LAYOUT, PANEL_VERTICAL,
		     PANEL_NOTIFY_PROC, MPGWindowNotify,
		     PANEL_VALUE, 1,
		     PANEL_CHOOSE_ONE, TRUE,
		     NULL);

  if (WindowCheck == (Panel_item)NULL)  return FALSE;

      if (!currMaxPlanarSettings) {
	 warning("\nno settings available !\n");
      } else {
	 currMaxPlanarSettings->create_new_window_for_mpg = FALSE;
      } /* endif */

   NewWindowCheck = xv_create (MPGPanel, PANEL_CHECK_BOX,
		     PANEL_LABEL_STRING, "new window:   ",
		     PANEL_CHOICE_STRINGS,
		     "use planar embedding",
		     "leave embedding untouched", NULL,
		     PANEL_LAYOUT, PANEL_VERTICAL,
		     PANEL_NOTIFY_PROC, MPGNewWindowNotify,
		     PANEL_VALUE, 1,
		     PANEL_CHOOSE_ONE, TRUE,
		     PANEL_INACTIVE, TRUE,
		     NULL);

  if (NewWindowCheck == (Panel_item)NULL)  return FALSE;

      if (!currMaxPlanarSettings) {
	 warning("\nno settings available !\n");
      } else {
	 currMaxPlanarSettings->use_planar_embedding = FALSE;
      } /* endif */


   window_fit(MPGPanel);
   window_fit(MPGFrame);
/* compute_subwindow_position_at_graph_of_current_selection (MPGFrame); */

   return TRUE;
}



Local void	showMaxPlanarSubFrame(void)
{
	if (!MPGFrameShowing) {
	   if (createMaxPlanarSubFrame()) {
/*	      xv_set (MPGFrame, XV_SHOW, TRUE, 0); */
	      MPGFrameShowing = TRUE;
	   } else {
	      warning("\nSetting window could not be created !\n");
	      MPGFrameShowing = FALSE;
	   } /* endif */
/*	} else {  do nothing, or what ??? */
	} /* endif */
}


Local void  MPGWindowNotify  (Panel_item item, int value, Event *event)
{
   if (value == 0) {
      xv_set (NewWindowCheck, PANEL_INACTIVE, FALSE, NULL);
      if (!currMaxPlanarSettings) {
	 warning("\nno settings available !\n");
      } else {
	 currMaxPlanarSettings->create_new_window_for_mpg = TRUE;
      } /* endif */
   } else {
      xv_set (NewWindowCheck, PANEL_INACTIVE, TRUE, NULL);
      if (!currMaxPlanarSettings) {
	 warning("\nno settings availabale !\n");
      } else {
	 currMaxPlanarSettings->create_new_window_for_mpg = FALSE;
      } /* endif */
   } /* endif */

   return;
}

Local void  MPGNewWindowNotify  (Panel_item item, int value, Event *event)
{
   if (value == 0) {
      if (!currMaxPlanarSettings) {
	 warning("\nno settings available !\n");
      } else {
	 currMaxPlanarSettings->use_planar_embedding = TRUE;
      } /* endif */
   } else {
      if (!currMaxPlanarSettings) {
	 warning("\nno settimgs available !\n");
      } else {
	 currMaxPlanarSettings->use_planar_embedding = FALSE;
      } /* endif */
   } /* endif */

   return;
}




Local	void	createThicknessSubFrame(void)
{
	ThicknessSubFrame = new_graphed_pin_subframe((Frame)0);
	graphed_create_pin_subframe (ThicknessSubFrame,
					"Thickness - Settings...");
	xv_set (ThicknessSubFrame->panel,
		PANEL_LAYOUT,	PANEL_VERTICAL,
		NULL);

   window_fit(ThicknessSubFrame->panel);
   window_fit(ThicknessSubFrame->frame);
}



Local void	showThicknessSubFrame(void)
{
	if (ThicknessSubFrame == (Graphed_pin_subframe)NULL ||
		!ThicknessSubFrame->showing) {
		createThicknessSubFrame();
	}

	if (ThicknessSubFrame != (Graphed_pin_subframe)NULL) {
		xv_set (ThicknessSubFrame->frame, XV_SHOW, TRUE, 0);
		ThicknessSubFrame->showing = TRUE;
	} else {
		ThicknessSubFrame->showing = FALSE;
	}
}



Local	void	createCrossingNumberSubFrame(void)
{
	CrossingNumberSubFrame = new_graphed_pin_subframe((Frame)0);
	graphed_create_pin_subframe (CrossingNumberSubFrame, "Crossingnumber - Settings...");
	xv_set (CrossingNumberSubFrame->panel,
		PANEL_LAYOUT,	PANEL_VERTICAL,
		NULL);

/*	xv_create (CrossingNumberSubFrame->panel, PANEL_BUTTON,
			PANEL_LABEL_STRING,
				"Insert crossing edges into convex embedding",
			PANEL_NOTIFY_PROC,
				ChoiceCrossingNumberConvexDraw,
			0); */

   window_fit(CrossingNumberSubFrame->panel);
   window_fit(CrossingNumberSubFrame->frame);
}



Local void	showCrossingNumberSubFrame(void)
{
	if (CrossingNumberSubFrame == (Graphed_pin_subframe)NULL || 		
		!CrossingNumberSubFrame->showing) {
		createCrossingNumberSubFrame();
	}

	if (CrossingNumberSubFrame != (Graphed_pin_subframe)NULL) {
		xv_set (CrossingNumberSubFrame->frame, XV_SHOW, TRUE, 0);
		CrossingNumberSubFrame->showing = TRUE;
	} else {
		CrossingNumberSubFrame->showing = FALSE;
	}
}


Local void  MPGDone  (Frame frame)
{
  xv_destroy_safe(MPGFrame);
  MPGFrameShowing = FALSE;  /* ????? */
}



