/*****************************************************************************/
/*                                                                           */
/*                  T R E E  -  W I D T H  -  W I N D O W                    */
/*                                                                           */
/* Modul        : mainwindow.c                                               */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/* Dises Fenster dient zur Auswahl der verschiedenen Algorithmen          */
/*                                                                      */
/* Aussehen :							     */
/*                                                                 */
/*=============================================================   */
/*|  V                     TREE-WIDTH                         |   */
/*|===========================================================|   */
/*|                                                           |   */
/*|   Algorithm output : V Treewidth			      |   */
/*|                                                           |   */
/*|   with Algorithm   : V Degreeheuristic		      |	  */
/*|                                                           |   */
/*|   <START>               <label nodes>      <options>      |   */
/*|                                                           |   */
/*|   Status :    waiting for input                           |   */
/*|                                                           |   */
/*|   Algorithmus fortschritt  <=============== - - - - - >   |   */
/*|                             0                        100  |   */
/*|                                                           |   */
/*=============================================================   */
/*                                                                */
/******************************************************************/

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>
#include <xview/notice.h>
#include <stdio.h>

/*#include <slist.h>*/

#include "mainwindow.h"
#include "graphed/graph.h"
#include "graphed/graphed_subwindows.h"

#include "control.h"
#include "optionen.h"
#include "schnittstelle.h"

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	void	main_control_window		()			     */
/*                ruft oder baut das `TREE-WIDTH-Fenster auf                 */
/*								             */
/*****************************************************************************/
/* Prozedueren um die 'Algorithmus Fortschritt anzeige zu steuern            */
/*								             */
/*	void 	init_fortschritt		(char* text,int max)         */
/*               setzt `text' in die Statuszewile und die Prozentanzeige auf */
/*		 Null. `max' enthaelt den 100% Wert fuer die Anzeige	     */
/*								             */
/*	void 	fortschritt			(int aktueller_wert)	     */
/*               der aktuele Stand des Algorithmus wird ausgegeben.          */
/*		 `aktueller_wert' sollte <= `max' sein.		    	     */
/*								             */
/*	void 	end_fortschritt			()			     */
/*		 die Fortschritt-anzeige wird auf Null gesetzt und der Staus-*/
/*		 text auf "waiting for input"				     */
/*								             */
/*****************************************************************************/

/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/* Notify Pozeduren :						             */
/*								             */
/*	void 	algo_output_option				()	     */
/*								             */
/*	void 	arnborg_button_notify_proc			()	     */
/*								             */
/*	void 	cliquenheuristic_button_notify_proc 		()	     */
/*								             */
/*	void	kantenheuristic_button_notify_proc 		()	     */
/*								             */
/*	void 	separatorenheuristic_button_notify_proc		()	     */
/*								             */
/*	void	separatorenseparatorheuristik_button_notify_proc 	()   */
/*								             */
/*	void	maxcliquen_button_notify_proc			()	     */
/*								             */
/*	void	dominantecliquen_button_notify_proc		()	     */
/*								             */
/*	void	erweiterung_button_notify_proc			()	     */
/*								             */
/*	void 	label_graph_optionen				()	     */
/*								             */
/*****************************************************************************/
/*	void 	quit_main_control_window_frame			()	     */
/*		 vernichtet das `TREE-WIDTH-Fenster		             */
/*								             */
/*****************************************************************************/
/***** Konstante Fenstergroesseneinstellungen */
#define main_frame_size_x        340
#define abstand_items            40
#define mitte_fuer_buttons       125
#define abstand_nach_oben        10
#define linker_abstand_button    30
#define rechter_button           230
#define breite_button            60
#define spalt_groesse            20
#define text_pos_y               370
#define main_frame_panel_size_y  200

/***** bool definition */
/*#define true        (0==0)
#define false       (0!=0)
typedef int bool;*/

/***** Globale Variablen */
enum ALGO_OUTPUT_TYPE algo_output_type=BAUMZERLEGUNG;
enum MAIN_ALGORITHMEN algorithmus=VALENZHEURISTIK;

bool main_control_window_frame_active=false;
/*extern char* erweitungsliste;
char* call_erweiterung ();
void  set_segmentation_abfrage();*/

Frame main_control_window_frame;

/***** lokale Variablen */
static int max_fuer_fortschritt;
static int oldprozent;
static int add_to_treewidth_nr;

static proced add_to_treewidth_feld[20];

static Panel main_control_window_frame_panel;

static Panel_item  algo_output_optionen;
static Panel_item  algorithm_option;
static Panel_item  start_button;
static Panel_item  labelnodes_button;
static Panel_item  fortschritt_item;
static Panel_item  status_message;
static Panel_item  optionen_button;

extern void loesche_cliquabfrage_gesetzt(void);
 
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*                !        Notify Procedures       !                */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/

static void algo_output_option_np(void)
{
 algo_output_type=xv_get(algo_output_optionen,PANEL_VALUE);
}


/********************************************************************/

static void algo_notify_proc(void)
{
 algorithmus=xv_get(algorithm_option,PANEL_VALUE);
 if (algorithmus>=ERWEITERUNG)
 {algorithmus=ERWEITERUNG;}
}

/********************************************************************/

static void start_button_np(void)
{
/* Vor Start eines Algorithmus muss eine eventuelle markierung durch */
/* show contents geloescht werden.			*/
 loesche_cliquabfrage_gesetzt();
 main_algorithmen_aufruf();
}
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*          !           Fuege Fremdroutienen ein          !        */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/

void add_to_treewidth_menu(char *text, proced procedure)
{     xv_set(algorithm_option,PANEL_CHOICE_STRING,add_to_treewidth_nr,text,NULL);
add_to_treewidth_feld[add_to_treewidth_nr]=procedure;
add_to_treewidth_nr++;
}
/********************************************************************/
/********************************************************************/

proced get_fremd_procedure(void)
{return add_to_treewidth_feld[xv_get(algorithm_option,PANEL_VALUE,NULL)];}


/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*          !           Quit  TREE-WIDTH-Window            !        */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/

static void quit_main_control_window_frame(void)
{
 xv_destroy_safe(main_control_window_frame);
 main_control_window_frame_active=FALSE;
}
/********************************************************************/
/********************************************************************/
/* fuer denn Fall, dass ser Benutzer den Ausgabe typ umsetzt.*/
void aktuallisiere_algo_output_type(void)
{/* xv_set(algo_output_optionen,
	                 PANEL_VALUE,           algo_output_type, 
                 NULL);
*/  }

/********************************************************************/

/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*          ! Rufe Manipulate-Tree-Decompositon-Window auf !        */
/*         =================================================        */
/*                                                                  */
/********************************************************************/

/********************************************************************/

void main_control_window(Menu menu, Menu_item menu_item)
{
int item_nr;
/*Slist element;*/

/* falls das Fenster noch/schon active ist wird es geoeffnet und in den */
/*  Vordergrund gesetzt und soweit noetig aktualisiert.                 */

if (  main_control_window_frame_active )
   {xv_set(main_control_window_frame,
               	XV_SHOW, 	TRUE,		/* foreground */
	        FRAME_CLOSED,	FALSE,		/* de-iconize */
		NULL );
    xv_set(algo_output_optionen,PANEL_VALUE,           algo_output_type, 
                 NULL);
    xv_set(algorithm_option,        PANEL_VALUE,           algorithmus, 
                 NULL);
    }
else{
/* set_segmentation_abfrage();*/
/* main_frame_panel_size_y+=size_of_slist(erweitungsliste)*abstand_items;*/
  
 main_control_window_frame_active=true;

 main_control_window_frame=(Frame)xv_create(base_frame, FRAME,
               FRAME_SHOW_RESIZE_CORNER, FALSE,
		WIN_ERROR_MSG,	"Could not create Treewidthwindow\n",
               FRAME_DONE_PROC,         quit_main_control_window_frame,
               XV_WIDTH,                main_frame_size_x,
               XV_HEIGHT,               main_frame_panel_size_y,
               XV_X ,                   600,
               XV_Y ,                   10,
	       FRAME_LABEL,		"TREE-WIDTH",
	       NULL);


 main_control_window_frame_panel =(Panel)xv_create(main_control_window_frame,
                                                          PANEL,
                XV_HELP_DATA,           "mainwindow:panel",
               NULL);

 item_nr=0;
 
 algo_output_optionen=xv_create(main_control_window_frame_panel,
		PANEL_CHOICE_STACK,
                PANEL_LAYOUT,            PANEL_HORIZONTAL,
                PANEL_LABEL_STRING,     "Outputtype : ",
                PANEL_CHOICE_STRINGS,    " Treewidth ", " Tree-Decomposition ",
					 NULL,
                PANEL_NOTIFY_PROC,      algo_output_option_np, 
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_nach_oben,
		PANEL_LABEL_X,	        linker_abstand_button,
		PANEL_LABEL_Y,		abstand_nach_oben,
		PANEL_VALUE_X,		mitte_fuer_buttons,
		PANEL_VALUE_Y,		abstand_nach_oben,
                XV_HELP_DATA,           "mainwindow:algo_output",
                 PANEL_VALUE,           algo_output_type, 
                 NULL);


 item_nr++;
 algorithm_option=xv_create(main_control_window_frame_panel,PANEL_CHOICE_STACK,
                PANEL_LAYOUT,            PANEL_HORIZONTAL,
                PANEL_LABEL_STRING,     "Algorithms : ",
                PANEL_CHOICE_STRINGS,           " Arnborgalgorithm",
						" Cliqueheuristic",
					        " Separatorheuristic",
						" Edgeheuristic",	
 						" Degreeheuristic",
						" only lower bound ",
						NULL,
                PANEL_NOTIFY_PROC,      algo_notify_proc, 
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
		PANEL_LABEL_X,	        linker_abstand_button,
		PANEL_LABEL_Y,		abstand_items*item_nr+abstand_nach_oben,
		PANEL_VALUE_X,		mitte_fuer_buttons,
		PANEL_VALUE_Y,		abstand_items*item_nr+abstand_nach_oben,
                 PANEL_VALUE,           algorithmus, 
               XV_HELP_DATA,           "mainwindow:algorithmus",
                NULL);
 


/* F"uge Fremdroutinen in Menue ein. */
add_to_treewidth_nr=6;
init_treewidth_menu();

 item_nr++;

 start_button=xv_create(main_control_window_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      "START",
/*                PANEL_LABEL_WIDTH,      breite_button,
*/		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,       start_button_np, 
                XV_HELP_DATA,           "mainwindow:start_button",
                NULL);

 labelnodes_button=xv_create(main_control_window_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      "label nodes",
 		XV_X,			mitte_fuer_buttons,
/*                PANEL_LABEL_WIDTH,      breite_button,
*/		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,       label_nodes_aufruf, 
                XV_HELP_DATA,           "mainwindow:label_nodes",
                 NULL);

optionen_button=xv_create(main_control_window_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      "options",
/*	        PANEL_LABEL_WIDTH,      breite_button,
*/		XV_X,			rechter_button+20,
		XV_Y,			abstand_items*item_nr+
                                              abstand_nach_oben,
                PANEL_NOTIFY_PROC,      optionen_window, 
                XV_HELP_DATA,           "mainwindow:options",
                 NULL);


 item_nr++;
               xv_create( main_control_window_frame_panel, PANEL_MESSAGE,
                PANEL_LABEL_BOLD,          TRUE,
                PANEL_LABEL_STRING,        "Status:" ,
		XV_X,	                   linker_abstand_button,
	 	XV_Y,                   abstand_items*item_nr
                                                    +abstand_nach_oben, 
                XV_SHOW,                   TRUE,
                XV_HELP_DATA,           "mainwindow:status",
		NULL );
 status_message=xv_create( main_control_window_frame_panel, PANEL_MESSAGE,
                PANEL_LABEL_BOLD,       TRUE,
                PANEL_LABEL_STRING,     "waiting for input" ,
		XV_X,	                mitte_fuer_buttons,
	 	XV_Y,                   abstand_items*item_nr
                                                   +abstand_nach_oben, 
                XV_SHOW,                TRUE,
                XV_HELP_DATA,           "mainwindow:status",
		NULL );

item_nr++;

 fortschritt_item=xv_create(main_control_window_frame_panel,PANEL_GAUGE,
          /*      PANEL_LABEL_STRING,      "Percent of algorithm ",*/
                PANEL_VALUE ,            0,
                PANEL_MIN_VALUE,         0, 
                PANEL_MAX_VALUE,         100, 
                PANEL_GAUGE_WIDTH,       170,
		XV_X,			mitte_fuer_buttons,
		XV_Y,			abstand_items*item_nr
					+abstand_nach_oben-15,
                PANEL_TICKS,             5,
                XV_HELP_DATA,           "mainwindow:algo_fortschritt",
                NULL);


 xv_set(main_control_window_frame,XV_SHOW,TRUE,NULL);

 }
}

/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*          !          Steure Fortschritt-Anzeige          !        */
/*         =================================================        */
/*                                                                  */
/********************************************************************/

/*********************************************************************/
/*        Initialisiere Staus und Fortschrittsanzeige                */
/*********************************************************************/
/*                                                                   */
/* Parameter          :    char* text :  Text fuer Stauszeile        */
/*			   int   max  :  maximaler Wert fuer Fort-   */
/*					 schritt-Anzeige.	     */
/*                                                                   */
/* Rueckgabeparameter :    void					     */
/*                                                                   */
/*********************************************************************/
/* Funktionbeschreibung:                                             */
/*  Die Statuszeile wird auf *text gesetzt und die Fortschrittanzeige*/
/*  aud Null. der amximale Wert fuer die Anzeige wird uebernommen.   */
/*********************************************************************/


void init_fortschritt(char *text, int max)
{
 if (main_control_window_frame_active)
 {
max_fuer_fortschritt=max;
 oldprozent=-max_fuer_fortschritt; /* um sofort eine fortschrittausgabe zu*/
				   /* erzielen.*/

 xv_set( status_message,
                PANEL_LABEL_STRING,        text,
	        XV_SHOW,                   TRUE,
		NULL );
fortschritt(0);
}
}


/*********************************************************************/
/*        Beende Fortschrittsanzeige			             */
/*********************************************************************/
/* Parameter          :    void					     */
/*                                                                   */
/* Rueckgabeparameter :    void					     */
/*                                                                   */
/*********************************************************************/
/* Funktionbeschreibung:                                             */
/*  setze den Staus text wieder auf Ursprungstext. Setze Fort-	     */
/*  schrittsanzeige auf Null.					     */
/*********************************************************************/


void end_fortschritt(void)

 { if (main_control_window_frame_active)
 {
xv_set(status_message, PANEL_LABEL_STRING, "waiting for input",NULL);
  xv_set(fortschritt_item,PANEL_VALUE,0,NULL);}
}



/*********************************************************************/
/*                 Aktualisiere   Fortschrittsanzeige                */
/*********************************************************************/
/*                                                                   */
/* Parameter          :   int aktueller_wert  :  aktueller Wert fuer */
/*					        Fortschritts-Anzeige */
/*                                                                   */
/* Rueckgabeparameter :    void					     */
/*                                                                   */
/*********************************************************************/
/* Funktionbeschreibung:                                             */
/*  der aktuelle Wert wird mit hilfe des maximalen Wertes in Prozent */
/*  umgerechnet und angezeigt.					     */
/*  Ueber `number_of_refresh' kann die Haeufigkeit der Fensteraktu-  */
/*  alisierung angegeben werden. (Je haeufiger desto langsamer laeuft*/
/*  das Programm.)						     */
/*********************************************************************/

void fortschritt(int aktueller_wert)
{
 int number_of_refresh=100; /* so oft wird Fenster neu gezeichnet.*/

 if (main_control_window_frame_active)
 {
 int prozent=(float)((float)aktueller_wert/(float)max_fuer_fortschritt)*100;
 if(oldprozent<prozent-max_fuer_fortschritt/number_of_refresh)
	   {
            xv_set(fortschritt_item,PANEL_VALUE,prozent,NULL);
            oldprozent=prozent;
            panel_paint(main_control_window_frame_panel,PANEL_NO_CLEAR);
           }
 }
}

/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
/*Notify_value react_on_sementation(void)
{printf ("\n ***** Aetsch Segmentationfault ********");
 exit(0);
}
*/
/*********************************************************************/
 
void set_segmentation_abfrage(void)
{/*notify_set_signal_func(main_control_window_frame_panel,react_on_sementation,
                       SIGBUS,NOTIFY_SYNC);*/
}


