/******************************************************************************/
/*                                                                            */
/*    SUNInterface.c                                                          */
/*                                                                            */
/******************************************************************************/
/*  Implementation of the interface of the Graph-Separator package to GraphEd.*/
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  11.06.1994                                                    */
/*  Modified :  11.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifdef SUN_VERSION

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include "SUNInterface.h"
#include <xview/xview.h>
#include <xview/panel.h>

/******************************************************************************/
/*  Global variables                                                          */
/******************************************************************************/

extern  Frame       base_frame;

static  Frame       GSFrame;
static  Panel       GSPanel;
static  Panel_item  /*NodeEdgeChoice,*/ EdgeChoice, /*NodeChoice, */
                    CompactCheck, AlphaCheck, AlphaText, /*IterateSlide,*/
                    AnimateCheck, GoButton;

static  GSAlgInfo    currSettings;
static  GSAlgSelect  currAlgorithm = KERNIGHAN_LIN;
static  bool         compaction    = FALSE;

/******************************************************************************/
/*  Function prototypes                                                       */
/******************************************************************************/

#ifdef ANSI_HEADERS_OFF
/*      void   init_user_menu          ();*/
/*Local char  *MenuGS                  ();*/
Local bool   GSWindow                ();
Local bool   ShowingGSWindow         (/*toggle*/);
Local void   GSDone                  (/*currFrame*/);
Local void   GoNotify                (/*item, event*/);
Local void   CompactionChoiceNotify  (/*item, value, event*/);
Local void   AlphaChoiceNotify       (/*item, value, event*/);
/*Local void   NodeEdgeChoiceNotify    ();*/
Local void   EdgeChoiceNotify        (/*item, value, event*/);
/*Local void   NodeChoiceNotify        ();*/
Local void   AnimateChoiceNotify     (/*item, value, event*/);
Local void   StartAlgorithm          (/*info*/);
#endif

#ifdef ANSI_HEADERS_ON
/*      void   init_user_menu          (void);*/
/*Local char  *MenuGS                  (char *menu, char *menu_item);*/
Local bool   GSWindow                (void);
Local bool   ShowingGSWindow         (bool  toggle);
Local void   GSDone                  (Frame  currFrame);
Local void   GoNotify                (Panel_item item, Event *event);
Local void   CompactionChoiceNotify  (Panel_item item, int value, Event *event);
Local void   AlphaChoiceNotify       (Panel_item item, int value, Event *event);
/*
Local void   NodeEdgeChoiceNotify    (Panel_item item, int value, Event *event);
*/
Local void   EdgeChoiceNotify        (Panel_item item, int value, Event *event);
/*
Local void   NodeChoiceNotify        (Panel_item item, int value, Event *event);
*/
Local void   AnimateChoiceNotify     (Panel_item item, int value, Event *event);
Local void   StartAlgorithm          (Sgraph_proc_info  info);
#endif

/******************************************************************************/
/*  Machine dependend stuff (SUN)                                             */
/******************************************************************************/

/*
int main (argc, argv)
  int  argc;
  char *argv[];
{
  graphed_main (argc, argv);
  return 0;
}

void init_user_menu ()
{
  add_to_user_menu ("Graph-Separators", MenuGS);
}
*/

void MenuGS (Menu menu, Menu_item menu_item)
{
  currSettings.Graph     = empty_sgraph;
  currSettings.NodeSet_A = empty_slist;
  currSettings.NodeSet_B = empty_slist;
  currSettings.Separator = empty_slist;
  currSettings.Alpha     = 2.0/3.0;
#ifdef ANIMATION_ON
    currSettings.animate = TRUE;
#endif

  if (ShowingGSWindow (FALSE))
  {    
    ;
  } 
  else if (GSWindow () == FALSE) 
  {
  warning ("No more windows available\n");
  bell ();
  }
  
}

/******************************************************************************/
/*  The XV-Interface                                                          */
/******************************************************************************/

Local bool  GSWindow  (void)
{
  GSFrame = (Frame) xv_create (base_frame, FRAME_CMD,
                XV_LABEL,               "Graph-Separators",
                FRAME_CMD_PUSHPIN_IN,   TRUE,
                FRAME_SHOW_LABEL,       TRUE,
                WIN_SHOW,               TRUE,
                FRAME_NO_CONFIRM,       TRUE,
                FRAME_DONE_PROC,        GSDone,
                NULL);

  if (GSFrame == (Frame)NULL)  return FALSE;

  GSPanel = (Panel) xv_get (GSFrame, FRAME_CMD_PANEL);
  
  if (GSPanel == (Frame)NULL)  return FALSE;

  GoButton = xv_create (GSPanel, PANEL_BUTTON,
                     XV_Y, xv_row (GSPanel, 1),
                     PANEL_LABEL_STRING, "  Start Algorithm  ",
                     PANEL_NOTIFY_PROC, GoNotify,
                     NULL);

  xv_create (GSPanel, PANEL_MESSAGE, 
             PANEL_LABEL_STRING, "  with the settings:",
             NULL);
/*
  xv_set (GSPanel, 
          PANEL_LAYOUT, PANEL_VERTICAL, 
          PANEL_ITEM_Y_GAP, 40,
          NULL);

  NodeEdgeChoice = xv_create (GSPanel, PANEL_CHOICE,
                     PANEL_LABEL_STRING, "Separator-Type:    ",
                     PANEL_CHOICE_STRINGS, "  Edge  ", "  Node  ", NULL,
                     PANEL_NOTIFY_PROC, NodeEdgeChoiceNotify,
                     PANEL_VALUE, 0,
                     NULL);
*/
  xv_set (GSPanel, 
          PANEL_LAYOUT, PANEL_VERTICAL, 
          PANEL_ITEM_X_GAP, 30,
          PANEL_ITEM_Y_GAP, 20,
          NULL);

  xv_create (GSPanel, PANEL_MESSAGE, 
             PANEL_LABEL_STRING, "Heuristic:   ",
             NULL);
  xv_set (GSPanel, PANEL_LAYOUT, PANEL_HORIZONTAL, NULL);

  EdgeChoice = xv_create (GSPanel, PANEL_CHOICE,
/*                     PANEL_LABEL_STRING, "Edge-Separator:   ",*/
                     PANEL_CHOICE_STRINGS, 
                       "Brute-Force", "Naive", "Greedy", "Kernighan-Lin",
                       "KL-Exchange", "Fiduccia-Mattheyses", "Plaisted A", 
/*                       "Plaisted A*", */
                       NULL,
                     PANEL_LAYOUT, PANEL_VERTICAL,
                     PANEL_NOTIFY_PROC, EdgeChoiceNotify,
                     PANEL_VALUE, 3,
                     NULL);
/*
  xv_set (GSPanel, PANEL_LAYOUT, PANEL_HORIZONTAL, NULL);

  NodeChoice = xv_create (GSPanel, PANEL_CHOICE,
                     PANEL_LABEL_STRING, "Node-Separator:",
                     PANEL_CHOICE_STRINGS, 
                       "Brute-Force", "Greedy", "Liu", NULL,
                     PANEL_LAYOUT, PANEL_VERTICAL,
                     PANEL_NOTIFY_PROC, NodeChoiceNotify,
                     PANEL_VALUE, 0,
                     PANEL_INACTIVE, TRUE,
                     NULL);
*/
  xv_set (GSPanel, 
          PANEL_LAYOUT, PANEL_VERTICAL, 
          PANEL_ITEM_X_GAP, 30,
          NULL);

  CompactCheck = xv_create (GSPanel, PANEL_CHECK_BOX,
                     PANEL_LABEL_STRING, "Compaction:      ",
                     PANEL_CHOICE_STRINGS,  "On    ", "Off", NULL,
                     PANEL_LAYOUT, PANEL_HORIZONTAL,
                     PANEL_NOTIFY_PROC, CompactionChoiceNotify,
                     PANEL_VALUE, 1,
                     PANEL_CHOOSE_ONE, TRUE,
                     NULL);

  AlphaCheck = xv_create (GSPanel, PANEL_CHECK_BOX,
                     PANEL_LABEL_STRING, "Alpha:   ",
                     PANEL_CHOICE_STRINGS,  "1/2   ", "2/3   ", "other", NULL,
                     PANEL_LAYOUT, PANEL_HORIZONTAL,
                     PANEL_NOTIFY_PROC, AlphaChoiceNotify,
                     PANEL_VALUE, 1,
                     PANEL_CHOOSE_ONE, TRUE,
                     NULL);

  AlphaText = xv_create (GSPanel, PANEL_TEXT,
                     XV_X, xv_col (GSPanel, 6),
                     PANEL_LABEL_STRING, "(1/2 <= alpha < 1)",
                     PANEL_NOTIFY_PROC, NULL,
                     PANEL_VALUE, "",
                     PANEL_VALUE_DISPLAY_LENGTH, 10,
                     PANEL_INACTIVE, TRUE,
                     NULL);
/*
  xv_set (GSPanel, 
          PANEL_LAYOUT, PANEL_VERTICAL, 
          PANEL_ITEM_Y_GAP, 40,
          NULL);

  IterateSlide = xv_create (GSPanel, PANEL_SLIDER,
                     PANEL_LABEL_STRING, "Iterate:",
                     PANEL_LAYOUT, PANEL_HORIZONTAL,
                     PANEL_NOTIFY_PROC, NULL,
                     PANEL_VALUE, 1,
                     PANEL_MIN_VALUE, 1,
                     PANEL_MAX_VALUE, 1000,
                     PANEL_SLIDER_WIDTH, 120,
                     PANEL_TICKS, 10,
                     NULL);
*/
  xv_set (GSPanel, 
          PANEL_LAYOUT, PANEL_VERTICAL, 
          PANEL_ITEM_Y_GAP, 20,
          NULL);

  AnimateCheck = xv_create (GSPanel, PANEL_CHECK_BOX,
                     PANEL_LABEL_STRING, "Animation:      ",
                     PANEL_CHOICE_STRINGS,  "On    ", "Off", NULL,
                     PANEL_LAYOUT, PANEL_HORIZONTAL,
                     PANEL_NOTIFY_PROC, AnimateChoiceNotify,
                     PANEL_VALUE, 0,
                     PANEL_CHOOSE_ONE, TRUE,
                     NULL);

  window_fit (GSPanel);
  window_fit (GSFrame);
  compute_subwindow_position_at_graph_of_current_selection (GSFrame);

  ShowingGSWindow (TRUE);
  return TRUE;
}

Local bool  ShowingGSWindow  (bool toggle)
{
  static  showingGSWindow = FALSE;

  if (toggle)
  {
    showingGSWindow = !showingGSWindow;
  }

  return  showingGSWindow;
}

Local void  GSDone  (Frame currFrame)
{
  xv_destroy_safe(GSFrame);
  ShowingGSWindow (TRUE);
}

Local void  GoNotify  (Panel_item item, Event *event)
{
  call_sgraph_proc (StartAlgorithm, NULL);
}

Local void  CompactionChoiceNotify  (Panel_item item, int value, Event *event)
{
  if (value == 0)
  {
    compaction = TRUE;
  }
  else
  {
    compaction = FALSE;
  }
}

Local void  AlphaChoiceNotify  (Panel_item item, int value, Event *event)
{
  if (value == 2)
  {
    xv_set (AlphaText, PANEL_INACTIVE, FALSE, NULL);
    return;
  }
 
  xv_set (AlphaText, PANEL_INACTIVE, TRUE, NULL);

  currSettings.Alpha = (value == 0) ? 0.5 : 2.0/3.0;

}
/*
Local void  NodeEdgeChoiceNotify  (Panel_item item, int value, Event *event)
{
  if (value == 0)
  {
    xv_set (NodeChoice, PANEL_INACTIVE, TRUE, NULL);
    xv_set (EdgeChoice, PANEL_INACTIVE, FALSE, NULL);
  }
  else
  {
    xv_set (EdgeChoice, PANEL_INACTIVE, TRUE, NULL);
    xv_set (NodeChoice, PANEL_INACTIVE, FALSE, NULL);
  }
}
*/
Local void  EdgeChoiceNotify  (Panel_item item, int value, Event *event)
{
  switch (value)
  {
    case 0 :  currAlgorithm = BRUTE_FORCE;         break;
    case 1 :  currAlgorithm = NAIVE;               break;
    case 2 :  currAlgorithm = GREEDY;              break;
    case 3 :  currAlgorithm = KERNIGHAN_LIN;       break;
    case 4 :  currAlgorithm = KL_EXCHANGE;         break;
    case 5 :  currAlgorithm = FIDUCCIA_MATTHEYSES; break;
    case 6 :  currAlgorithm = PLAISTED_A;          break;
/*    case 7 :  currAlgorithm = PLAISTED_STAR;       break;*/
  }
}
/*
Local void  NodeChoiceNotify  (Panel_item item, int value, Event *event)
{
  switch (value)
  {
    case 0 :  currAlgorithm = BRUTE_FORCE;  break;
    case 1 :  currAlgorithm = NAIVE;        break;
    case 2 :  currAlgorithm = GREEDY;       break;
  }
}
*/
Local void  AnimateChoiceNotify  (Panel_item item, int value, Event *event)
{
  if (value == 0)
  {
#ifdef ANIMATION_ON
    currSettings.animate = TRUE;
#endif
  }
  else
  {
#ifdef ANIMATION_ON
    currSettings.animate = FALSE;
#endif
  }
}

Local void StartAlgorithm (Sgraph_proc_info info)
{
  /****************************************************************************/
  /*  Fill the algorithm info structure with the proper values.               */
  /****************************************************************************/

  currSettings.Graph = info->sgraph;

  /****************************************************************************/
  /*  Compute the separator                                                   */
  /****************************************************************************/

  ComputeSeparator (&currSettings, currAlgorithm, compaction);

  /****************************************************************************/
  /*  Clean up                                                                */
  /****************************************************************************/

  info->no_changes           = FALSE;
  info->no_structure_changes = FALSE;
  info->save_selection       = TRUE;
  info->recompute            = TRUE;
  info->repaint              = TRUE;
  info->recenter             = FALSE;
}


#endif 

/******************************************************************************/
/*  End of  SUNInterface.c                                                    */
/******************************************************************************/
