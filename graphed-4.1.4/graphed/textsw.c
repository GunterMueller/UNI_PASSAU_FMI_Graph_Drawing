/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				textsw.c				*/
/*									*/
/************************************************************************/
/*									*/
/*	Verwaltung des Textfensters zur Ausgabe von Meldungen.		*/
/*	Ausgaben auf dieses Fenster laufen i.a. ueber die Prozeduren	*/
/*	in error.c.							*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"

#include "graphed_subwindows.h"


/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_message_textsw ()				*/
/*	void	write_message         (message)				*/
/*									*/
/************************************************************************/


/************************************************************************/
/*									*/
/*			GLOBALE VARIABLEN				*/
/*									*/
/************************************************************************/

Textsw	message_textsw = (Textsw)NULL;


/************************************************************************/
/*									*/
/*			MESSAGE_TEXTSW AUFBAUEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_message_textsw ()				*/
/*									*/
/*	"Erzeugt" das message_textsw. "Load" und "Set directory" werden	*/
/*	im Menue des Textfensters verboten, ebenso wird readonly	*/
/*	gesetzt.							*/
/*									*/
/************************************************************************/

		
void	create_message_textsw(void)
{
	char	buffer [1000];
	
	sprintf (buffer,
		 "GraphEd %s\nUniversity of Passau 1986 - 1995\nLehrstuhl für Theoretische Informatik\n*****     Messages     *****\n\n",
		 GRAPHED_VERSION);
	
	message_textsw = (Textsw)xv_create(base_frame, TEXTSW,
		XV_X, 0,
		XV_Y, 0,
		XV_HEIGHT,	100,
		XV_WIDTH,	400,
		WIN_ERROR_MSG,	"Could not create message textsw.\nGood bye!\n",
		NULL);

	xv_set(message_textsw,
		TEXTSW_DISABLE_LOAD,		TRUE,
		TEXTSW_DISABLE_CD,		TRUE,
		TEXTSW_INSERTION_POINT,		TEXTSW_INFINITY,
		TEXTSW_READ_ONLY,		TRUE,
		TEXTSW_INSERT_MAKES_VISIBLE,	TRUE,
		TEXTSW_IGNORE_LIMIT,		TEXTSW_INFINITY,
		TEXTSW_MEMORY_MAXIMUM,		20000,
		TEXTSW_CONTENTS,		buffer,
		NULL);
}
/************************************************************************/
/*									*/
/*		MELDUNG AUF MESSAGE_TEXTSW AUSGEBEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	write_message (message)					*/
/*									*/
/*	Ausgabe von message in message_textsw oder, wenn dieses (noch)	*/
/*	nicht, existiert, auf stderr.					*/
/*	BUG : um auf das message_textsw schreiben zu koennen, muss hier	*/
/*	kurzzeitig der Readonly-Status aufgehoben werden.		*/
/*									*/
/************************************************************************/


void	write_message (char *message)
{
	if (message_textsw != (Textsw)NULL) {
		xv_set(message_textsw,
			TEXTSW_INSERTION_POINT,	TEXTSW_INFINITY,
			TEXTSW_READ_ONLY,	FALSE,
			/* sonst kann auch das Programm nicht	*/
			/* schreiben ! (?)			*/
			NULL);

		if (xv_get(message_textsw, TEXTSW_LENGTH) >
	            xv_get(message_textsw, TEXTSW_MEMORY_MAXIMUM) - 1000) {
			textsw_reset (message_textsw, 0, 0);
		}
		textsw_possibly_normalize (message_textsw,
			xv_get(message_textsw, TEXTSW_INSERTION_POINT));
	}

	if (message_textsw != (Textsw)NULL) {
		textsw_insert (message_textsw, message, strlen(message));
	} else {
		fprintf (stderr, "%s", message);
	}
	
	if (message_textsw != (Textsw)NULL) {
		xv_set(message_textsw, TEXTSW_READ_ONLY, TRUE, NULL);
	}
}
