/*****************************************************************************/
/*                                                                           */
/*  M A N I P U L A T E - T R E E - D E C O M P O S I T I O N - W I N D O W  */
/*                                                                           */
/* Modul        : decomp_window.c                                            */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/* Dises Fenster dient zur Steuerung der Baumzerlegungslayout veraenderungen */
/*                                                                           */
/* Aussehen :							             */
/* =======================================================================   */
/*|  V                 MANIPULATE TREE-DECOMPOSITION                     |   */
/*|=======================================================================   */
/*|                                                                      |   */
/*|   <  show  Nodecontent  >                             ---------      |   */
/*|                                           with : V    |       |      |   */
/*|   <   unmark cliquen    >                             ---------      |   */
/*|                                                                      |   */
/*|   <   Wurzel aendern    >		      <   Wurzel == Mitte  >     |   */
/*|                                                                      |   */
/*|   <   nice-tree-decomp  >		      <  min-tree-decomp   >     |   */
/*|                                                                      |   */
/*|    change label    V  Cliquen             min tree-width   2         |   */
/*| 					      max tree-width   3         |   */
/*|                                                                      |   */
/* =======================================================================   */
/*                                                                           */
/*****************************************************************************/

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	void	 decomposition_window		()			     */
/*                ruft oder baut das `manipulate-tree-decomposition-Fenster  */
/*		  auf							     */
/*								             */
/*****************************************************************************/

/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/* Notify Pozeduren :						             */
/*								             */
/*	void 	label_decomp_optionen		()			     */
/*               aendert die Werte fuer label_decompostion und ruft diese auf*/
/*								             */
/*	void  	type_optionen			()			     */
/*		 setzt my_node_type auf den gewuenschten Knotentyp	     */
/*								             */
/*	void 	unmark_button_notify_proc	()			     */
/*		 ruft unmark_nodes auf und setzt setzt den button inactive   */
/*								             */
/*	void 	durchlaufe_graph_notify_proc	()			     */
/*		 ruft `durchlaufe_graph_aufruf' auf und setzt den	     */
/*		 `unmark_labels'-Button active				     */
/*								             */
/*****************************************************************************/
/*	void 	quit_decomp_frame		()			     */
/*		 vernichtet das `manipulate-tree-decomposition-Fenster       */
/*								             */
/*****************************************************************************/
/*	void 	init_node_type_item		()			     */
/*		 aktualisiert die aktuellen Knotentypen			     */
/*								             */
/*****************************************************************************/




#include "decomp_window.h"
#include "mystd.h"
#include "my_misc.h"
#include <graphed/misc.h>
#include <graphed/graphed_svi.h>

#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>
#include <xview/notice.h>
#include <stdio.h>

/*#include <type.h>
*/

extern void pruefe_sgraph_auf_aenderung_aufruf(void);

#include "control.h"

#define main_frame_size_x        410
#define main_frame_panel_size_y  250
#define abstand_items            40
#define mitte_fuer_buttons       125
#define abstand_nach_oben        20
#define linker_abstand_button    30
#define rechter_button           240
#define breite_button            120

#define decomp_panel_size_y      240
#define spalt_groesse            20

#define text_pos_y               370

extern bool errorflag_zusammenhang_gegeben; /*z.Z. nich benutzt*/


static bool clickabfrage_gesetzt=false;
static bool decomp_frame_active=false;
int label_decomp_option=0;

extern Frame base_frame;
extern int min_baumweite;
extern int max_baumweite;

static Frame decomp_frame;
static Panel_item  node_type;
static Panel_item  label_decomp_darstellung;
static Panel_item  unmark_button;
static Panel_item  wurzel_aendern_button;
static Panel_item  wurzel_mitte_button;
static Panel_item  nice_tree_decomp_button;
static Panel_item  min_tree_decomp_button;
static Panel_item  min_baumweite_item;
static Panel_item  max_baumweite_item;
static Panel_item  clique_durchlaufen;

/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*                !        Notify Procedures       !                */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/

void label_decomp_optionen(void)
{label_decomp_option=xv_get(label_decomp_darstellung,PANEL_VALUE);
 longlabel=false;
 switch(label_decomp_option)
    {case 1: {erstes_zulaessiges_zeichen_decomp='a';
              letztes_zulaessiges_zeichen_decomp='z';
              break;}
     case 2: {erstes_zulaessiges_zeichen_decomp='0';
              letztes_zulaessiges_zeichen_decomp='9';
              break;}
     case 3: {erstes_zulaessiges_zeichen_decomp='0';
              letztes_zulaessiges_zeichen_decomp='1';
              break;}
     case 0: longlabel=true;}
change_label_aufruf();

}

/********************************************************************/
/*void mark_button_notify_proc(void)
{*//*static bool clickabfrage_gesetzt=false;

if(clickabfrage_gesetzt)
       {xv_set(mark_button,PANEL_LABEL_STRING,"start mark cliquen",NULL);
        clickabfrage_loeschen();
        clickabfrage_gesetzt=false;}
else   {xv_set(mark_button,PANEL_LABEL_STRING," end mark_cliquen ",NULL);*/
       /* clickabfrage_setzen();*/
     /*   clickabfrage_gesetzt=true;}*/
/*xv_set(unmark_button,PANEL_INACTIVE,FALSE,NULL);
}*/
/********************************************************************/

void  type_optionen(void)
{ my_node_type=xv_get(node_type,PANEL_VALUE);
}

/********************************************************************/

void unmark_button_notify_proc()
{if(clickabfrage_gesetzt)
    {
 clickabfrage_gesetzt=false;
 xv_set(unmark_button,PANEL_INACTIVE,TRUE,NULL);
 clickabfrage_loeschen();}
}

/********************************************************************/
void loesche_cliquabfrage_gesetzt(void)
{if (clickabfrage_gesetzt)
    {unmark_button_notify_proc();}
}
/********************************************************************/

void durchlaufe_graph_notify_proc(void)
{clickabfrage_gesetzt=true;
/* pruefe_sgraph_auf_aenderung_aufruf();  NOCH NICHT MOEGLICH !!
 if(errorflag_zusammenhang_gegeben)
     {error("Graphen wurden veraendert\n");
     }
 else
   {*/if (xv_get(unmark_button,PANEL_INACTIVE)==TRUE)
        { xv_set(unmark_button,PANEL_INACTIVE,FALSE,NULL);
          clickabfrage_setzen();
        }
    durchlaufe_graph_aufruf();

}



void nice_tree_decomp_aufrufvorbereitung(void)
{loesche_cliquabfrage_gesetzt();
nice_tree_decomp_aufruf();}


void min_tree_decomp_aufrufvorbereitung(void)
{loesche_cliquabfrage_gesetzt();
min_tree_decomp_aufruf();}
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*          !   Quit Manipulate-Tree-Decompositon-Window  !         */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/
void quit_decomp_frame(void)
{ unmark_button_notify_proc();
 xv_destroy_safe(decomp_frame);
decomp_frame_active=FALSE;}
/************************************************************************/
/************************************************************************/
void init_node_type_item(void)
{int i=0;
 struct nodetype *node_t;
 while ( (node_t=get_nodetype(i)) !=NULL )
    {
     xv_set(node_type,PANEL_CHOICE_IMAGE,i,pm_to_svi(node_t->pm),NULL);
    if (i==10) {break;}
     i++;}
     xv_set(node_type,PANEL_VALUE,my_node_type,NULL);

}

/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*          ! Rufe Manipulate-Tree-Decompositon-Window auf !        */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/



void decomposition_window(void)
{
 int item_nr;
 Panel decomp_frame_panel;

char *text=(char*)mein_malloc(sizeof(char*)*5);


/* falls das Fenster noch/schon active ist wird es geoeffnet und in den */
/*  Vordergrund gesetzt und soweit noetig aktualisiert.                 */

if (  decomp_frame_active )
   {xv_set(decomp_frame,
               	XV_SHOW, 	TRUE,		/* foreground */
	        FRAME_CLOSED,	FALSE,		/* de-iconize */
		NULL );


    sprintf(text,"%i",max_baumweite);
    xv_set( max_baumweite_item,  PANEL_LABEL_STRING,  text,NULL );

    sprintf(text,"%i",min_baumweite);
    xv_set( min_baumweite_item,  PANEL_LABEL_STRING,  text,NULL);
    init_node_type_item();          /* Knotentypen aktualisieren */
   }
else{
 decomp_frame_active=true;

 keine_markierung();

decomp_frame =xv_create(base_frame,FRAME,
                FRAME_LABEL,    " MANIPULATE TREE-DECOMPOSITION ",
                FRAME_SHOW_RESIZE_CORNER, FALSE,
                FRAME_DONE_PROC,        quit_decomp_frame,
                XV_X,                   10,
                XV_Y,                   main_frame_panel_size_y+30,
                XV_WIDTH,               main_frame_size_x,
                XV_HEIGHT,              decomp_panel_size_y,
		NULL);

decomp_frame_panel =(Panel) xv_create(decomp_frame,PANEL,
                XV_HELP_DATA,           "decomp_window:panel",
		NULL);

/*	xv_create( decomp_frame_panel,		PANEL_MESSAGE,
               PANEL_LABEL_STRING,  
                           "=================================================",
		XV_X,			5,
	 	XV_Y,			0, 
		NULL );

	xv_create( decomp_frame_panel,PANEL_MESSAGE,
               PANEL_LABEL_STRING,   "      MANIPULATE TREE-DECOMPOSITION ",
		XV_X,			80,
		XV_Y,			15,
		NULL );*/
item_nr=0;                                                                
clique_durchlaufen=xv_create(decomp_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      "show nodecontents" ,
                PANEL_LABEL_WIDTH,      breite_button,
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,      durchlaufe_graph_notify_proc, 
                XV_HELP_DATA,           "decomp_window:clique_durchlaufen",
                NULL);

/*node_type = xv_create(decomp_frame_panel, PANEL_CHOICE_STACK,
                PANEL_LAYOUT,            PANEL_HORIZONTAL,
		PANEL_LABEL_STRING,	 "with nodetype",
		PANEL_NOTIFY_PROC,	 type_optionen,
                PANEL_CHOICE_STRINGS,  
                                          "1","2","3","4",NULL,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
		XV_X,                   rechter_button,
                PANEL_VALUE,            my_node_type, 
                XV_HELP_DATA,           "decomp_window:panel",
        	NULL);*/

 node_type = xv_create(decomp_frame_panel, PANEL_CHOICE_STACK,
                PANEL_LAYOUT,            PANEL_HORIZONTAL,
		PANEL_LABEL_STRING,	 "with :",
	/*	PANEL_CHOICE_IMAGES,	  0,*/
		PANEL_NOTIFY_PROC,	 type_optionen,
                PANEL_CHOICE_STRINGS,  
                                          "1","2","3","4",NULL,
		XV_Y,			abstand_items*item_nr
					+abstand_nach_oben-10,
		XV_X,                   rechter_button,
   /*             PANEL_VALUE,            my_node_type, */
                XV_HELP_DATA,           "decomp_window:node_type",
        	NULL);

init_node_type_item();


item_nr++;
/*mark_button=xv_create(decomp_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,     "    mark cliques    ",
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,      mark_button_notify_proc, 
                 NULL);*/

	
unmark_button=xv_create(decomp_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,     "   unmark  nodes    ",
                PANEL_LABEL_WIDTH,      breite_button,
                PANEL_INACTIVE,         TRUE,
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,      unmark_button_notify_proc, 
                XV_HELP_DATA,           "decomp_window:unmark",
                NULL);

item_nr++;
wurzel_aendern_button=xv_create(decomp_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      "  change  root  ",
                PANEL_LABEL_WIDTH,      breite_button,
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,      wurzel_aendern_aufruf, 
                XV_HELP_DATA,           "decomp_window:wurzel_aendern",
                NULL);

wurzel_mitte_button=xv_create(decomp_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      "   root = center  ",
                PANEL_LABEL_WIDTH,      breite_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
		XV_X,			rechter_button,
                PANEL_NOTIFY_PROC,       wurzel_mitte_aufruf, 
                XV_HELP_DATA,           "decomp_window:wurzel_mitte",
                NULL);

item_nr++;
nice_tree_decomp_button=xv_create(decomp_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      "nice-tree-decomp",
                PANEL_LABEL_WIDTH,      breite_button,
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,       nice_tree_decomp_aufrufvorbereitung, 
                XV_HELP_DATA,           "decomp_window:nice_tree_dec",
                NULL);

min_tree_decomp_button=xv_create(decomp_frame_panel,
                PANEL_BUTTON,
                PANEL_LABEL_STRING,      " min-tree-decomp",
                PANEL_LABEL_WIDTH,      breite_button,
		XV_X,			rechter_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_NOTIFY_PROC,      min_tree_decomp_aufrufvorbereitung, 
                XV_HELP_DATA,           "decomp_window:min_tree_dec",
                NULL);

item_nr++;
label_decomp_darstellung=xv_create(decomp_frame_panel,PANEL_CHOICE_STACK,
                PANEL_LAYOUT,            PANEL_HORIZONTAL,
                PANEL_LABEL_STRING,      "change label",
                PANEL_CHOICE_STRINGS,    "contents","a...x","0...9","0,1",NULL,
                PANEL_NOTIFY_PROC,       label_decomp_optionen, 
		XV_X,			linker_abstand_button,
		XV_Y,			abstand_items*item_nr+abstand_nach_oben,
                PANEL_VALUE,             label_decomp_option, 
                XV_HELP_DATA,           "decomp_window:label_decomp",
                NULL);

	xv_create( decomp_frame_panel,		PANEL_MESSAGE,
              PANEL_LABEL_BOLD,                FALSE,
              PANEL_LABEL_STRING,              "min treewidth:",
		XV_X,	                        rechter_button,
	 	XV_Y,              abstand_items*(item_nr)+abstand_nach_oben, 
                XV_HELP_DATA,           "decomp_window:min_baumweite",
		NULL );

sprintf(text,"%i",min_baumweite);
min_baumweite_item = xv_create( decomp_frame_panel,		PANEL_MESSAGE,
               PANEL_LABEL_BOLD,           FALSE,
               PANEL_LABEL_STRING,        text,
		XV_X,	                  rechter_button+120,
	 	XV_Y,              abstand_items*(item_nr)+abstand_nach_oben, 
                XV_HELP_DATA,           "decomp_window:min_baumweite",
		NULL );

	xv_create( decomp_frame_panel,		PANEL_MESSAGE,
               PANEL_LABEL_BOLD,           FALSE,
               PANEL_LABEL_STRING,        "max treewidth:",
		XV_X,	                  rechter_button,
	 	XV_Y,              abstand_items*(item_nr)+2*abstand_nach_oben, 
                XV_HELP_DATA,           "decomp_window:max_baumweite",
		NULL );

sprintf(text,"%i",max_baumweite);
max_baumweite_item = xv_create( decomp_frame_panel,		PANEL_MESSAGE,
               PANEL_LABEL_BOLD,           FALSE,
               PANEL_LABEL_STRING,        text,
		XV_X,	                  rechter_button+120,
	 	XV_Y,              abstand_items*(item_nr)+2*abstand_nach_oben, 
                XV_HELP_DATA,           "decomp_window:max_baumweite",
		NULL );


xv_set (decomp_frame,XV_SHOW,TRUE,NULL);
 }
mein_free(text);
}
 
         
