/********************************************************************************/
/*										*/
/*	FUNKTION: kleines Xview/OpenWindows-Toolkit				*/
/*										*/
/********************************************************************************/

#include <xview/xview.h>
#include <xview/panel.h>

#include "win_defs.h"


/********************************************************************************/
/*										*/
/*	XV_CMD_position_absolute						*/
/*										*/
/*	PARAMETER:	1. Frame	win		(Typ: FRAME_CMD)	*/
/*			2. int		x					*/
/*			3. int		y					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Positioniere den CMD-Frame 'win' auf die absoluten	*/
/*			Bildschirmkoordinaten (x,y).				*/
/*										*/
/********************************************************************************/

void	XV_CMD_position_absolute(Frame win, int x, int y)
{
	xv_set( win,	XV_X,	x,
			XV_Y,	y,
			0);
}

/********************************************************************************/
/*										*/
/*	XV_CMD_position_relative						*/
/*										*/
/*	PARAMETER:	1. Frame	win		(Typ: FRAME_CMD)	*/
/*			2. Frame	reference	(Typ: beliebig )	*/
/*			3. int		x					*/
/*			4. int		y					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Positioniere den CMD-Frame 'win' relativ zur oberen	*/
/*			linken Ecke von 'reference'				*/
/*										*/
/********************************************************************************/

void	XV_CMD_position_relative(Frame win, Frame reference, int x, int y)
{
	Rect	ref_rect;
	
	frame_get_rect( reference, &ref_rect );
	xv_set( win,	XV_X,	ref_rect.r_left + x,
			XV_Y,	ref_rect.r_top + y,
			0);
}

/********************************************************************************/
/*										*/
/*	XV_WIN_position_absolute						*/
/*										*/
/*	PARAMETER:	1. Frame	win		(Typ: FRAME )		*/
/*			2. int		x					*/
/*			3. int		y					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Positioniere den Frame 'win' auf die absoluten		*/
/*			Bildschirmkoordinaten (x,y).				*/
/*										*/
/********************************************************************************/

void	XV_WIN_position_absolute(Frame win, int x, int y)
{
	Xv_Window	parent;
	Rect		ref_rect;
	
	parent = (Xv_Window) xv_get( win, WIN_PARENT );

	if( parent != XV_NULL ) 
	{
		frame_get_rect( parent, &ref_rect );
		xv_set( win,	XV_X,	x - ref_rect.r_left,
			XV_Y,	y - ref_rect.r_top,
			0);
	} 
	else 
	{
		xv_set( win,	XV_X,	x,
				XV_Y,	y,
				0);
	}
}
	
/********************************************************************************/
/*										*/
/*	XV_WIN_position_relative						*/
/*										*/
/*	PARAMETER:	1. Frame	win		(Typ: FRAME   ) 	*/
/*			2. Frame	reference	(Typ: beliebig) 	*/
/*			3. int		x					*/
/*			4. int		y					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Positioniere den Frame 'win' relativ zur oberen 	*/
/*			linken Ecke von 'reference'				*/
/*										*/
/********************************************************************************/

void	XV_WIN_position_relative(Frame win, Frame reference, int x, int y)
{
	Rect	ref_rect;
	
	frame_get_rect( reference, &ref_rect );
	XV_WIN_position_absolute( win, ref_rect.r_left + x, ref_rect.r_top + y );
}

/********************************************************************************/
/*										*/
/*	XV_cycle_panel_choice_stack						*/
/*										*/
/*	PARAMETER:	1. Panel_item	item	(Typ: PANEL_CHOICE_STACK)	*/
/*			2. int		value					*/
/*			3. Event	*event					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Simulation von SunView-Cycles unter Xview/OpenWindows.	*/
/*										*/
/*	BENUTZUNG:	Die urspruengliche notify-proc von 'item' wird jetzt	*/
/*			als PANEL_CLIENT_DATA gesetzt und durch diese Prozedur	*/
/*			als notify-proc ersetzt.				*/
/*										*/
/*	ERKLAERUNG:	Durch diese Prozedur funktioniert 'item' wie eine	*/
/*			SunView-Choice. D.h., dass man durch fortlaufendes	*/
/*			Druecken der linken Maustaste nicht mehr nur den ersten */
/*			(bzw. urspruenglichen Default-) Wert von 'item' 	*/
/*			erhaelt, sondern in zyklischer Reihenfolge alle Werte.	*/
/*			Dies wird erreicht, indem der Defaultwert von 'item'	*/
/*			mit jedem Aufruf neu gesetzt wird.			*/
/*										*/
/********************************************************************************/

void	XV_cycle_panel_choice_stack(Panel_item item, int value, Event *event)
{
	Notify_func	user_notify_proc;
	int	number_of_choices;
	
	xv_set( item,	PANEL_VALUE,		value,
			0);

	user_notify_proc = (Notify_func)xv_get( item, PANEL_CLIENT_DATA );
	if( user_notify_proc != NULL ) 
	{
		user_notify_proc( item, value, event );
	}

	number_of_choices = (int) xv_get( item, PANEL_NCHOICES );
	
	 /* user_notify_proc may have changed 'value'. So get it again */
	 
	value = (int) xv_get( item, PANEL_VALUE );
	xv_set( item,	PANEL_DEFAULT_VALUE,	((value + 1) % number_of_choices),
			0);
}

/********************************************************************************/
/*										*/
/*	XV_show_panel_item_proc							*/
/*										*/
/*	PARAMETER:	1. Panel_item	item					*/
/*			2. int		value					*/
/*			3. Event	*event					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe von PANEL_LABEL_STRING (item) auf stdout.	*/
/*										*/
/*	BEMERKUNG:	Kann als Standard-PANEL_NOTIFY_PROC benutzt werden.	*/
/*										*/
/********************************************************************************/

void	XV_show_panel_item_proc(Panel_item item, int value, Event *event)
{
	if( item != (Panel_item)NULL ) 
	{
		printf( "%s\n", (char *)xv_get( item, PANEL_LABEL_STRING ) );
	}
}

