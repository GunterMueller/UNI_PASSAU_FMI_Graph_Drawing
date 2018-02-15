/* FERTIG 130293 */
/********************************************************************************/
/*										*/
/*	MODUL:	win_defs							*/
/*										*/
/*	FUNKTION: kleines Xview/OpenWindows-Toolkit				*/
/*										*/
/********************************************************************************/

#ifndef WIN_DEFS_HEADER
#define WIN_DEFS_HEADER

#include <xview/xview.h>
#include <xview/panel.h>

/*-->@	-Dwin_defs

	Keine eigenen Datenstrukturen.
	Benutzt die Datenstrukturen von <xview/xview.h> und <xview/panel.h> .
	
**/

/********************************************************************************/
/*										*/
/*	XV_awake	(#define)						*/
/*										*/
/*	PARAMETER:	Panel_item	item	(Typ: PANEL_BUTTON)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Setze PANEL_INACTIVE von 'item' auf FALSE.		*/
/*										*/
/********************************************************************************/

#define XV_awake( panel_item ) \
	if( (panel_item) != XV_NULL ) xv_set( panel_item, PANEL_INACTIVE, FALSE , 0 )

/********************************************************************************/
/*										*/
/*	XV_sleep	(#define)						*/
/*										*/
/*	PARAMETER:	Panel_item	item	(Typ: PANEL_BUTTON)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Setze PANEL_INACTIVE von 'item' auf TRUE.		*/
/*										*/
/********************************************************************************/

#define XV_sleep( panel_item ) \
	if( (panel_item) != XV_NULL ) xv_set( panel_item, PANEL_INACTIVE, TRUE, 0 )

/********************************************************************************/
/*										*/
/*	XV_hide_item	(#define)						*/
/*										*/
/*	PARAMETER:	Panel_item	item	(Typ: PANEL_BUTTON)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Setze XV_SHOW von 'item' auf FALSE.			*/
/*										*/
/********************************************************************************/

#define	XV_hide_item( panel_item ) \
	xv_set( panel_item, XV_SHOW, FALSE, 0)
	
/********************************************************************************/
/*										*/
/*	XV_show_item	(#define)						*/
/*										*/
/*	PARAMETER:	Panel_item	item	(Typ: PANEL_BUTTON)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Setze XV_SHOW von 'item' auf TRUE.			*/
/*										*/
/********************************************************************************/

#define XV_show_item( panel_item ) \
	xv_set( panel_item, XV_SHOW, TRUE, 0)

/********************************************************************************/
/*										*/
/*	XV_win_position_down	(#define)					*/
/*										*/
/*	PARAMETER:	1. Panel	panel					*/
/*			2. int		gap					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Verschiebe die Position von 'panel' um 'gap' Pixel nach */
/*			unten.							*/
/*										*/
/*	ERKLAERUNG:	Damit ist es moeglich, verschiedene Panels innerhalb	*/
/*			eines Frames auch visuell zu trennen. Denn bei		*/
/*			korrekter Anwendung entsteht eine horizontale Trenn-	*/
/*			linie zwischen den Panels.				*/
/*										*/
/*	ANWENDUNG:	(panel_1 muss bereits in Groesse und Position fertig	*/
/*			 sein ) 						*/
/*										*/
/*			panel_2 = xv_create(	frame,		PANEL,		*/
/*						WIN_BELOW,	panel_1,	*/
/*						...,				*/
/*						0);				*/
/*										*/
/*			XV_win_position_down( panel_2, 1 );			*/
/*			...							*/
/*										*/
/*			(So entsteht vertikale Luecke zw. panel_1 und panel_2)	*/
/*										*/
/********************************************************************************/

#define XV_win_position_down( win, gap ) \
	xv_set( win, XV_Y, (xv_get( win, XV_Y ) + gap), 0)

extern	void	XV_CMD_position_relative(Frame win, Frame reference, int x, int y);
extern	void	XV_CMD_position_absolute(Frame win, int x, int y);
extern	void	XV_WIN_position_relative(Frame win, Frame reference, int x, int y);
extern	void	XV_WIN_position_absolute(Frame win, int x, int y);
extern	void	XV_cycle_panel_choice_stack(Panel_item item, int value, Event *event);
extern	void	XV_show_panel_item_proc(Panel_item item, int value, Event *event);

#endif
