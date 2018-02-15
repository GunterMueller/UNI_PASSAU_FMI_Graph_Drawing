/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				about.c					*/
/*									*/
/************************************************************************/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"

#include "graphed_subwindows.h"
#include "graphed_mpr.h"

extern void    fill_panel_choice_attr_list_of_strings (char **strings, int n, char **attr_list); /* main.c */

/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	show_about_subframe ()					*/
/*									*/
/************************************************************************/


/************************************************************************/
/*									*/
/*			GLOBALE VARIABLEN				*/
/*									*/
/************************************************************************/


Frame		about_subframe;


/************************************************************************/
/*									*/
/*		LOKALE FUNKTIONEN UND PROZEDUREN			*/
/*									*/
/************************************************************************/


static	void	create_about_subframe    (void);
/*static void	notify_about_buttons     (Panel_item item, Event *event);*/
static	void	notify_about_cycle       (Panel_item item, Event *event);
static	void	about_subframe_done      (Frame frame);


/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/


static	Panel		about_panel;
static	Textsw		about_textsw;

static	Panel_item	graphed_icon_button;
static	Panel_item	unipassau_icon_button;
static	Panel_item	graphed_about_cycle;
/*static	Panel_item	graphed_about_quit_button; */

#define N_ABOUT_TOPICS 16

static	char		*about_topics[N_ABOUT_TOPICS] = {
  "GraphEd",
  /*
     "General",
     "Create",
     "Edit",
     "Graph Grammars",
     "GraGra",
     "InOut",
     "Misc",
     "Tools",
     "Layout",
     "Goody",
     "User",
     "Termgraph",
     "References",
     "Source",
     "People"
     */
};

static	char		*about_topics_for_cycle[N_ABOUT_TOPICS+3];

static	char		*about_topic_filenames[N_ABOUT_TOPICS] = {
  "help/GraphEd",
  /*
     "help/General",
     "help/Create",
     "help/Edit",
     "help/GraphGrammars",
     "help/GraGra",
     "help/InOut",
     "help/Misc",
     "help/Tools",
     "help/Layout",
     "help/Goody",
     "help/User",
     "help/Termgraph",
     "help/References",
     "help/Source",
     "help/People"
     */
};

static	Pixmap	uni_passau_logo = (Pixmap)NULL;
static	Pixmap	uni_passau_big_logo = (Pixmap)NULL;

static void	create_about_subframe(void)
{
  char	*filename;
  FILE	*f;

  about_subframe = (Frame)xv_create(base_frame, FRAME_CMD,
				    FRAME_NO_CONFIRM, TRUE,
				    FRAME_LABEL, "GraphEd online help",
				    FRAME_ICON, icon_create(ICON_IMAGE,
							    about_icon_svi,
							    0),
				    FRAME_CMD_PUSHPIN_IN, TRUE,
				    FRAME_DONE_PROC, about_subframe_done,
				    NULL);

  about_panel = (Panel)xv_get(about_subframe, FRAME_CMD_PANEL);

  if (uni_passau_logo == (Pixmap)NULL) {
    filename = file_exists_somewhere ("uni-passau.logo",
				      getenv ("GRAPHED_INPUTS"));
    if (filename != NULL) {
      f = fopen (filename, "r");
      fclose (f);
    }
  }
 
  if (uni_passau_big_logo == (Pixmap)NULL) {
    filename = file_exists_somewhere ("uni-passau.big.logo",
				      getenv ("GRAPHED_INPUTS"));
    if (filename != NULL) {
      f = fopen (filename, "r");
      fclose (f);
    }
  }

  if (uni_passau_logo != (Pixmap)NULL && uni_passau_big_logo != (Pixmap)NULL) {
    unipassau_icon_button = xv_create(about_panel, PANEL_BUTTON,
				      XV_X, xv_col (about_panel, 0),
				      XV_Y, xv_row (about_panel, 0),
				      NULL);
  }

  graphed_icon_button = xv_create(about_panel, PANEL_BUTTON,
				  NULL);

  fill_panel_choice_attr_list_of_strings (about_topics,
					  N_ABOUT_TOPICS,
					  about_topics_for_cycle);

  graphed_about_cycle = xv_create(about_panel, PANEL_CHOICE,
				  ATTR_LIST, about_topics_for_cycle,
				  PANEL_DISPLAY_LEVEL, PANEL_CURRENT,
				  XV_X, xv_col (about_panel, 0),
				  XV_Y, xv_row (about_panel, 14),
				  PANEL_LABEL_STRING, "Topic :",
				  PANEL_NOTIFY_PROC, notify_about_cycle,
				  NULL);

  window_fit(about_panel);

	
  about_textsw = (Textsw)xv_create(about_subframe, TEXTSW,
				   WIN_Y, 0,
				   WIN_RIGHT_OF, about_panel,
				   WIN_COLUMNS, 80,
				   XV_HEIGHT, WIN_EXTEND_TO_EDGE,
				   TEXTSW_DISABLE_LOAD,	TRUE,
				   TEXTSW_DISABLE_CD,	TRUE,
				   TEXTSW_READ_ONLY,	TRUE,
				   TEXTSW_BROWSING,	TRUE,
				   NULL);
		
  window_fit(about_subframe);
}


static	showing_about_subframe = FALSE;


void		show_about_subframe(void)
{
  char	*filename;
	
  if (showing_about_subframe) {
    return;
  }
	
  create_about_subframe();
	
  xv_set(about_subframe,
	 WIN_X,   screenwidth/2  - (int)xv_get(about_subframe, XV_WIDTH)/2,
	 WIN_Y,   screenheight/2 - (int)xv_get(about_subframe, XV_HEIGHT)/2,
	 XV_SHOW, TRUE,
	 NULL);
	
  filename = file_exists_somewhere (about_topic_filenames[0],
				    getenv ("GRAPHED_INPUTS"));
	
  if (filename != NULL) {
    xv_set(about_textsw, TEXTSW_FILE, filename, NULL);
  } else {
    warning ("No Information about %s available\n", about_topics[0]);
  }
	
  showing_about_subframe = TRUE;
}



/*
static	void	notify_about_buttons (Panel_item item, Event *event)
{
}
*/

static	void	about_subframe_done (Frame frame)
{
  xv_destroy_safe (about_subframe);
  showing_about_subframe = FALSE;
}


static	void	notify_about_cycle (Panel_item item, Event *event)
{
  int	topic = (int)xv_get(item, PANEL_VALUE);
  char	*filename;
	
  filename = file_exists_somewhere (about_topic_filenames[topic],
				    getenv ("GRAPHED_INPUTS"));
	
  if (filename != NULL) {
    xv_set(about_textsw, TEXTSW_FILE, filename, NULL);
  } else {
    warning ("No Information on %s available\n", about_topics[topic]);
  }
}
