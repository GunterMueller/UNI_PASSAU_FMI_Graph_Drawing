/******************************************************************************/
/*                                                                            */
/* FILE: VISIBILITY_SF.C                                                      */
/*                                                                            */
/* Beschreibung: enthaelt die Funktionen, die das Subframe "Visibility        */
/*               Representations" erzeugen und initialisieren                 */
/*                                                                            */
/******************************************************************************/



#include <xview/xview.h>
#include <xview/panel.h>
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed/gridder.h>
#include <graphed/graphed_pin_sf.h>
#include "visibility_layout2_export.h"


typedef void (*voidproc)();
static void visibility_sf_set (Panel_item item, Event *event);
static void visibility_sf_reset (Panel_item item, Event *event);
static void visibility_sf_do_RT_weak (Panel_item item, Event *event);
static void visibility_sf_do_TT_weak (Panel_item item, Event *event);
static void visibility_sf_do_TT_epsilon (Panel_item item, Event *event);
static void visibility_sf_do_Kant_weak (Panel_item item, Event *event);
static void visibility_sf_do_Nummenmaa_weak (Panel_item item, Event *event);
static void visibility_sf_do_tree_strong (Panel_item item, Event *event);
static void visibility_sf_done (Frame frame);

static Panel_item choice_nummen_nodes,
                  check_compression,
                  choice_st_nodes;
static Gridder    vertical_gridder,
                  horizontal_gridder,
                  height_gridder;


Visibility2_Settings visibility_layout2_settings;


Visibility2_Settings init_visibility_layout2_settings (void)
{
   Visibility2_Settings settings;

   settings.nummen_nodes = u_and_w;
   settings.compression = TRUE;
   settings.st_nodes = maximal_degree;
   settings.vertical_distance = 64;
   settings.horizontal_distance = 64;
   settings.height = 32;
   settings.defaults_y_distance = GRIDDER_DISTANCE_2_DEFAULT_SIZE;
   settings.defaults_x_distance = GRIDDER_DISTANCE_2_DEFAULT_SIZE;
   settings.defaults_height = GRIDDER_DISTANCE_1_DEFAULT_SIZE;

   return settings;
}



static Graphed_pin_subframe visibility_subframe = (Graphed_pin_subframe)NULL;


static void create_visibility_subframe (void)
{
   int row_count = 0;

   if (visibility_subframe == (Graphed_pin_subframe)NULL) {
      visibility_subframe = new_graphed_pin_subframe ((Frame)0);
   }

   visibility_subframe->set_proc   = visibility_sf_set;
   visibility_subframe->reset_proc = visibility_sf_reset;
   visibility_subframe->done_proc  = visibility_sf_done;

   graphed_create_pin_subframe (visibility_subframe, "Visibility Representations");

   row_count++;
   (void) xv_create (visibility_subframe->panel,
                     PANEL_BUTTON,
                     XV_X, xv_col(visibility_subframe->panel, 0),
                     XV_Y, xv_row(visibility_subframe->panel, row_count),
                     PANEL_LABEL_STRING, "RT-weak-visibility",
                     PANEL_NOTIFY_PROC, visibility_sf_do_RT_weak,
                     0);

   row_count++;
   (void) xv_create (visibility_subframe->panel,
                     PANEL_BUTTON,
                     XV_X, xv_col(visibility_subframe->panel, 0),
                     XV_Y, xv_row(visibility_subframe->panel, row_count),
                     PANEL_LABEL_STRING, "TT-weak-visibility",
                     PANEL_NOTIFY_PROC, visibility_sf_do_TT_weak,
                     0);

   row_count++;
   (void) xv_create (visibility_subframe->panel,
                     PANEL_BUTTON,
                     XV_X, xv_col(visibility_subframe->panel, 0),
                     XV_Y, xv_row(visibility_subframe->panel, row_count),
                     PANEL_LABEL_STRING, "TT-epsilon-visibility",
                     PANEL_NOTIFY_PROC, visibility_sf_do_TT_epsilon,
                     0);

   row_count++;
   (void) xv_create (visibility_subframe->panel,
                     PANEL_BUTTON,
                     XV_X, xv_col(visibility_subframe->panel, 0),
                     XV_Y, xv_row(visibility_subframe->panel, row_count),
                     PANEL_LABEL_STRING, "Kant-weak-visibility",
                     PANEL_NOTIFY_PROC, visibility_sf_do_Kant_weak,
                     0);

   row_count++;
   (void) xv_create (visibility_subframe->panel,
                     PANEL_BUTTON,
                     XV_X, xv_col(visibility_subframe->panel, 0),
                     XV_Y, xv_row(visibility_subframe->panel, row_count),
                     PANEL_LABEL_STRING, "Nummenmaa-weak-visibility",
                     PANEL_NOTIFY_PROC, visibility_sf_do_Nummenmaa_weak,
                     0);

   choice_nummen_nodes = xv_create (visibility_subframe->panel,
                                    PANEL_CHOICE_STACK,
                                    XV_X, xv_col(visibility_subframe->panel,28),
                                    XV_Y, xv_row(visibility_subframe->panel,row_count),
                                    PANEL_LABEL_STRING, "selected nodes",
                                    PANEL_CHOICE_STRINGS, "u and v","u and w",0,
                                    PANEL_VALUE, visibility_layout2_settings.nummen_nodes,
                                    0);

   row_count++;
   (void) xv_create (visibility_subframe->panel,
                     PANEL_BUTTON,
                     XV_X, xv_col(visibility_subframe->panel, 0),
                     XV_Y, xv_row(visibility_subframe->panel, row_count),
                     PANEL_LABEL_STRING, "tree-strong-visibility",
                     PANEL_NOTIFY_PROC, visibility_sf_do_tree_strong,
                     0);

   row_count++;
   check_compression = xv_create (visibility_subframe->panel,
                                  PANEL_CHECK_BOX,
                                  XV_X, xv_col(visibility_subframe->panel, 0),
                                  XV_Y, xv_row(visibility_subframe->panel, row_count),
                                  PANEL_LABEL_STRING, "compression",
                                  PANEL_LABEL_BOLD, TRUE,
                                  PANEL_VALUE, visibility_layout2_settings.compression,
                                  0);

   choice_st_nodes = xv_create (visibility_subframe->panel,
                                PANEL_CHOICE_STACK,
                                XV_X, xv_col(visibility_subframe->panel,18),
                                XV_Y, xv_row(visibility_subframe->panel,row_count),
                                PANEL_LABEL_STRING, "st-selection",
                                PANEL_CHOICE_STRINGS, "maximal degree",
                                                      "minimal degree",
                                                      "maximal sum of degrees",
                                                      "minimal sum of degrees",
                                                      0,
                                PANEL_VALUE, visibility_layout2_settings.st_nodes,
                                0);


   row_count++;
   vertical_gridder = create_gridder (visibility_subframe->panel,
                                      GRIDDER_HEIGHT,
                                      "vertical distance",
                                      GRIDDER_DISTANCE_2_DEFAULT_SIZE,
                                      64,
                                      row_count);

   row_count += 2;
   horizontal_gridder = create_gridder (visibility_subframe->panel,
                                        GRIDDER_WIDTH,
                                        "horizontal distance",
                                        GRIDDER_DISTANCE_2_DEFAULT_SIZE,
                                        64,
                                        row_count);

   row_count += 2;
   height_gridder = create_gridder (visibility_subframe->panel,
                                         GRIDDER_HEIGHT,
                                         "node height",
                                         GRIDDER_DISTANCE_1_DEFAULT_SIZE,
                                         32,
                                         row_count);

   window_fit (visibility_subframe->panel);
   window_fit (visibility_subframe->frame);
}



void show_visibility_subframe (void *done_proc)
{
   if ((visibility_subframe == (Graphed_pin_subframe)NULL) ||
       !visibility_subframe->showing) {
      create_visibility_subframe ();
   }

   visibility_subframe->graphed_done_proc = (voidproc) done_proc;

   if (visibility_subframe != (Graphed_pin_subframe)NULL) {

      xv_set (choice_nummen_nodes,
              PANEL_VALUE,
              visibility_layout2_settings.nummen_nodes,
              0);
      xv_set (check_compression,
              PANEL_VALUE,
              visibility_layout2_settings.compression,
              0);
      xv_set (choice_st_nodes,
              PANEL_VALUE,
              visibility_layout2_settings.st_nodes,
              0);
      gridder_set (vertical_gridder,
                   visibility_layout2_settings.defaults_y_distance,
                   visibility_layout2_settings.vertical_distance);
      gridder_set (height_gridder,
                   visibility_layout2_settings.defaults_height,
                   visibility_layout2_settings.height);
      gridder_set (horizontal_gridder,
                   visibility_layout2_settings.defaults_x_distance,
                   visibility_layout2_settings.horizontal_distance);

      compute_subwindow_position_at_graph_of_current_selection (
                   visibility_subframe->frame);
      xv_set (visibility_subframe->frame ,WIN_SHOW, TRUE, NULL);

      visibility_subframe->showing = TRUE;

   } else {

      visibility_subframe->showing = FALSE;

   }
}



static void visibility_sf_set (Panel_item item, Event *event)
{
   save_visibility_layout2_settings ();
}



static void visibility_sf_reset (Panel_item item, Event *event)
{
}



static void visibility_sf_do_RT_weak (Panel_item item, Event *event)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_RT_weak_visibility, NULL);
}



static void visibility_sf_do_TT_weak (Panel_item item, Event *event)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_TT_weak_visibility, NULL);
}



static void visibility_sf_do_TT_epsilon (Panel_item item, Event *event)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_TT_epsilon_visibility, NULL);
}



static void visibility_sf_do_Kant_weak (Panel_item item, Event *event)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_Kant_weak_visibility, NULL);
}



static void visibility_sf_do_Nummenmaa_weak (Panel_item item, Event *event)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_Nummenmaa_weak_visibility, NULL);
}



static void visibility_sf_do_tree_strong (Panel_item item, Event *event)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_tree_strong_visibility, NULL);
}



static void visibility_sf_done (Frame frame)
{
   save_visibility_layout2_settings ();
   free (horizontal_gridder);
   horizontal_gridder = (Gridder)NULL;
   free (vertical_gridder);
   vertical_gridder = (Gridder)NULL;
   free (height_gridder);
   height_gridder = (Gridder)NULL;
}



void save_visibility_layout2_settings (void)
{
   if (visibility_subframe != NULL && visibility_subframe->showing) {

      visibility_layout2_settings.nummen_nodes =
         xv_get (choice_nummen_nodes,PANEL_VALUE);
      visibility_layout2_settings.compression =
         xv_get (check_compression,PANEL_VALUE);
      visibility_layout2_settings.st_nodes =
         xv_get (choice_st_nodes,PANEL_VALUE);
      visibility_layout2_settings.horizontal_distance = 
         gridder_get_size (horizontal_gridder);
      visibility_layout2_settings.vertical_distance =
         gridder_get_size (vertical_gridder);
      visibility_layout2_settings.height =
         gridder_get_size (height_gridder);
      visibility_layout2_settings.defaults_x_distance =
         (int)gridder_get_value (horizontal_gridder);
      visibility_layout2_settings.defaults_y_distance =
         (int)gridder_get_value (vertical_gridder);
      visibility_layout2_settings.defaults_height =
         (int)gridder_get_value (height_gridder);

   } else {

      if (visibility_layout2_settings.defaults_x_distance != GRIDDER_DISTANCE_OTHER) {
         visibility_layout2_settings.horizontal_distance = 
            recompute_gridder_size (NULL,
                                    visibility_layout2_settings.defaults_x_distance,
                                    GRIDDER_WIDTH);
      }

      if (visibility_layout2_settings.defaults_y_distance != GRIDDER_DISTANCE_OTHER) {
         visibility_layout2_settings.vertical_distance = 
            recompute_gridder_size (NULL,
                                    visibility_layout2_settings.defaults_y_distance,
                                    GRIDDER_HEIGHT);
      }

      if (visibility_layout2_settings.defaults_height != GRIDDER_DISTANCE_OTHER) {
         visibility_layout2_settings.height = 
            recompute_gridder_size (NULL,
                                    visibility_layout2_settings.defaults_height,
                                    GRIDDER_HEIGHT);
      }
   }

   if (visibility_layout2_settings.vertical_distance == 0)
      visibility_layout2_settings.vertical_distance = 2 * get_current_node_height ();
   if (visibility_layout2_settings.horizontal_distance == 0)
      visibility_layout2_settings.horizontal_distance = 2 * get_current_node_width ();
   if (visibility_layout2_settings.height == 0)
      visibility_layout2_settings.height = get_current_node_height ();
}



/******************************************************************************/
/*                                                                            */
/*                        END OF FILE: VISIBILITY_SF.C                        */
/*                                                                            */
/******************************************************************************/

