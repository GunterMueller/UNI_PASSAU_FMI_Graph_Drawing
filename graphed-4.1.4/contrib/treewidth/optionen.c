#include "optionen.h"
#include "control.h"
#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>
#include <xview/notice.h>
#include <stdio.h>

/*****************************************************************************/
/*                                                                           */
/*                O P T I O N E N  -  F E N S T E R 			     */
/*                                                                           */
/* Modul	: optionen.c						     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

#define opionen_frame_size_x        390
#define optinen_frame_size_y        210
#define abstand_vo            10
#define abstand_item           40
#define links	              15
#define mitte			250


#define true        (0==0)
#define false       (0!=0)
typedef int bool;

extern int max_laenge_label;    

extern int hashgroesse;
extern Frame base_frame;
extern bool finde_minbw_durch_max_clique;


extern char erstes_zulaessiges_zeichen_graph;
extern char letztes_zulaessiges_zeichen_graph;
int labelnodes_default = 0;
extern int label_graph_default;


bool jede_baumzerlegung_in_neues_fenster=false;

Panel_item max_laenge_label_option;
Panel_item  label_nodes_optionen;
Panel_item hashgroesse_option;
Panel_item finde_min_bw_durch_choice;
Panel_item baumzerlegung_fenster_optionen;
Frame optionen_frame;
extern Frame main_control_window_frame;
Panel optionen_frame_panel;

bool optionen_frame_active=false;

/********************************************************************/

void label_graph_optionen(void)
{label_graph_default=xv_get(label_nodes_optionen,PANEL_VALUE);
 switch(label_graph_default)
    {case 0: {erstes_zulaessiges_zeichen_graph='a';
              letztes_zulaessiges_zeichen_graph='z';
              break;}
     case 1: {erstes_zulaessiges_zeichen_graph='0';
              letztes_zulaessiges_zeichen_graph='9';
              break;}
     case 2: {erstes_zulaessiges_zeichen_graph='0';
              letztes_zulaessiges_zeichen_graph='1';
              break;}
      }
label_nodes_aufruf();
}

/********************************************************************/

void hashgroesse_optionen(void)
{hashgroesse=xv_get(hashgroesse_option,PANEL_VALUE)*25000;}
/********************************************************************/

void max_laenge_label_optionen(void)
{max_laenge_label=xv_get(max_laenge_label_option,PANEL_VALUE);}

/********************************************************************/

void finde_min_bw_durch_choice_notify_proc(void)
{untere_schranke=xv_get(finde_min_bw_durch_choice,PANEL_VALUE);}

/********************************************************************/
void baumzerlegung_fenster_optionen_notify_proc(void)
{jede_baumzerlegung_in_neues_fenster=xv_get(baumzerlegung_fenster_optionen,
			PANEL_VALUE);}

/********************************************************************/
void quit_optionen_frame(void)
{xv_destroy_safe(optionen_frame);
 optionen_frame_active=FALSE;}
/************************************************************************/
/********************************************************************/
/********************************************************************/
/********************************************************************/
/********************************************************************/

void optionen_window(void)
{
int item_nr;

if (  optionen_frame_active )
   {xv_set(optionen_frame,
               	XV_SHOW, 	TRUE,		/* foreground */
	        FRAME_CLOSED,	FALSE,		/* de-iconize */
		NULL );


/* Hashtabellengroese kann sich geaendert haben.  */
    xv_set(hashgroesse_option,
                PANEL_VALUE,     hashgroesse/25000,
	        NULL );	
    }
else{
 optionen_frame_active=true;

 optionen_frame=(Frame)xv_create(main_control_window_frame, FRAME_CMD,
               FRAME_CMD_PUSHPIN_IN,     TRUE,
               FRAME_SHOW_RESIZE_CORNER, FALSE,
               FRAME_DONE_PROC,         quit_optionen_frame,
               XV_WIDTH,                opionen_frame_size_x,
               XV_HEIGHT,               optinen_frame_size_y,
               XV_X ,                   10,
               XV_Y ,                   10,
	       FRAME_LABEL,		"OPTIONS",
	       NULL);

 optionen_frame_panel =(Panel)xv_create(optionen_frame,
                                        PANEL,
               XV_X ,                   0,
               XV_Y ,                   0,

                XV_HELP_DATA,           "optionenwin:panel",
                                        NULL);

 item_nr=0;
 finde_min_bw_durch_choice=xv_create(optionen_frame_panel,
                PANEL_CHOICE_STACK,
                PANEL_LAYOUT,           PANEL_HORIZONTAL,
                PANEL_LABEL_STRING,     "compute min. treewidth with:",
                PANEL_CHOICE_STRINGS,   "edgenumber",             "max Clique",
                                        "max Cliquesseparator",    NULL,
		PANEL_LABEL_X, 		links,
		PANEL_LABEL_Y,		item_nr*abstand_item+abstand_vo,
		PANEL_VALUE_X,		mitte,
		PANEL_VALUE_Y,		item_nr*abstand_item+abstand_vo,
                PANEL_NOTIFY_PROC,      finde_min_bw_durch_choice_notify_proc, 
                XV_HELP_DATA,           "optionenwin:type",
                PANEL_VALUE,            finde_minbw_durch_max_clique,
                NULL);

 item_nr++;
 baumzerlegung_fenster_optionen=xv_create(optionen_frame_panel,
	        PANEL_CHOICE_STACK,
 		PANEL_LABEL_STRING ,     "each decomp. in a new window?",
                PANEL_CHOICE_STRINGS,    "No",
                                         "Yes"
                                             ,NULL,
                PANEL_NOTIFY_PROC,      baumzerlegung_fenster_optionen_notify_proc, 
		PANEL_LABEL_X,	         links,
		PANEL_LABEL_Y,		 item_nr*abstand_item+abstand_vo,
		PANEL_VALUE_X,		mitte,
		PANEL_VALUE_Y,		item_nr*abstand_item+abstand_vo,
                PANEL_VALUE,            jede_baumzerlegung_in_neues_fenster, 
                XV_HELP_DATA,           "optionenwin:fenster_optionen",
                 NULL);

item_nr++;

label_nodes_optionen=xv_create(optionen_frame_panel,
		PANEL_CHOICE_STACK,
                PANEL_LAYOUT,            PANEL_HORIZONTAL,
                PANEL_LABEL_STRING,      "label nodes with:",
                PANEL_CHOICE_STRINGS,    "a...x","0...9","0,1",NULL,
                PANEL_NOTIFY_PROC,       label_graph_optionen, 
		PANEL_LABEL_X,	        links,
		PANEL_LABEL_Y,		item_nr*abstand_item+abstand_vo,
		PANEL_VALUE_X,		mitte,
		PANEL_VALUE_Y,		item_nr*abstand_item+abstand_vo,
                PANEL_VALUE,             labelnodes_default, 
                XV_HELP_DATA,           "optionenwin:labeloption",
                 NULL);
item_nr++;
max_laenge_label_option=xv_create(optionen_frame_panel,PANEL_SLIDER,
                PANEL_LABEL_STRING,      "max labelsize:                      ",
                PANEL_MIN_VALUE,         1, 
                PANEL_MAX_VALUE,         200, 
                PANEL_GAUGE_WIDTH,       100,
		PANEL_LABEL_X,	         links,
		PANEL_LABEL_Y,		item_nr*abstand_item+abstand_vo,
		PANEL_VALUE_X,		mitte,
		PANEL_VALUE_Y,		item_nr*abstand_item+abstand_vo,
                PANEL_TICKS,             5,
		PANEL_SHOW_RANGE,	FALSE,
                PANEL_NOTIFY_PROC,       max_laenge_label_optionen,
                PANEL_VALUE,             max_laenge_label,
                XV_HELP_DATA,           "optionenwin:maxlaenge",
                NULL);

 item_nr++;
 hashgroesse_option=xv_create(optionen_frame_panel,PANEL_SLIDER,
                PANEL_LABEL_STRING,      "hashtablesize 100kbyte:    ",
                PANEL_MIN_VALUE,         1, 
                PANEL_MAX_VALUE,         1000, 
                PANEL_GAUGE_WIDTH,       100,
		PANEL_LABEL_X,	         links,
		PANEL_LABEL_Y,		item_nr*abstand_item+abstand_vo,
		PANEL_VALUE_X,		mitte,
		PANEL_VALUE_Y,		item_nr*abstand_item+abstand_vo,
                PANEL_TICKS,             5,
		PANEL_SHOW_RANGE,	FALSE,
                PANEL_NOTIFY_PROC,      hashgroesse_optionen,
                PANEL_VALUE,            hashgroesse/25000,
                XV_HELP_DATA,           "optionenwin:hashgroesse",
                NULL);


/*window_fit(optionen_frame_panel);
 window_fit(optionen_frame);*/
 xv_set(optionen_frame,XV_SHOW,TRUE,NULL);
 }
}
