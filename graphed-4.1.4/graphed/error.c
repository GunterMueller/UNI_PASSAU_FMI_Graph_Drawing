/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				ERROR.C					*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul enthaelt Routinen zur Ausgabe von Meldungen,	*/
/*	Warnungen und (schweren) Fehlern und zum Programmabbruch.	*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"

#include "graphed_subwindows.h"


/************************************************************************/
/*									*/
/*			GLOBALE PROZEDUREN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	message     (format, *)					*/
/*	void	warning     (format, *)					*/
/*	void	error       (format, *)					*/
/*	void	sys_error   (errno)					*/
/*	void	fatal_error (format, *)					*/
/*									*/
/*	void	die  ()							*/
/*									*/
/************************************************************************/
/************************************************************************/
/*									*/
/*	void	message     (format, *)					*/
/*	void	warning     (format, *)					*/
/*	void	error       (format, *)					*/
/*	void	sys_error   (errno)					*/
/*	void	fatal_error (format, *)					*/
/*									*/
/*	(format, *) = Gleiche Syntax wie printf				*/
/*	errno = Systemfehlernummer, wie gleichnamige globale Variable	*/
/*									*/
/*======================================================================*/
/*									*/
/*	Es gibt vier Stufen von Meldungen :				*/
/*	- message     : normale Meldung.				*/
/*	- warning     : Warnung, Format : "WARNING : ..."		*/
/*	- error       : Fehler,  Format : "ERROR   : ..."		*/
/*	  sys_error   : wie error					*/
/*	- fatal_error : schwerer, nicht wiedergutzumachender Fehler,	*/
/*	                fuehrt zu automatischem Programmabbruch.	*/
/*	                Format : "Graphed : FATAL ERROR : ..."		*/
/*	Die Anteile am Format vor "..." werden von den entsprechenden	*/
/*	Prozeduren erzeugt.						*/
/*									*/
/*	Die ersten vier Prozeduren arbeiten ueber write_message (d.h.	*/
/*	Ausgabe in message_textsw, wenn existent, sonst auf stderr),	*/
/*	die letzte gibt grundsaetzlich auf stderr aus.			*/
/*									*/
/*	Die Routine fatal_error gibt zusaetzlich den Inhalt des		*/
/*	message_textsw auf stderr aus.					*/
/*									*/
/************************************************************************/



static	int	message_bypassed = FALSE;
static	FILE	*message_bypass_file = (FILE *)NULL;


void	bypass_messages_to_file (FILE *file)
{
	if (file != (FILE *)NULL) {
		message_bypassed = TRUE;
		message_bypass_file = file;
	} else {
		message_bypassed = FALSE;
		message_bypass_file = (FILE *)NULL;
	}
}


void	message (char *format, ...)
{
	char	buffer[1000];
	va_list	args;
	
	va_start (args, format);
	
	vsprintf (buffer, format, args);

	if (!message_bypassed) {
		write_message (buffer);
	} else {
		fprintf (message_bypass_file, "%s", buffer);
	}
	
	va_end (args);
}



void	warning (char *format, ...)
{
	char	buffer[1000];
	va_list	args;
	
	va_start (args, format);
	
	vsprintf (buffer, format, args);

	if (!message_bypassed) {
		write_message ("WARNING : ");
		write_message (buffer);
	} else {
		fprintf (message_bypass_file, "WARNING : %s", buffer);
	}
	
	va_end (args);
}



void	error (char *format, ...)
{
	char	buffer[1000];
	va_list	args;
	
	va_start (args, format);
	
	vsprintf (buffer, format, args);

	if (!message_bypassed) {
		write_message ("ERROR : ");
		write_message (buffer);
		bell ();
	} else {
		fprintf (message_bypass_file, "ERROR : %s", buffer);		
	}

	va_end (args);
}



void	sys_error (int errno)
{
	if (errno <= sys_nerr)
		error ("%s\n", sys_errlist[errno]);
}



void	fatal_error (char *format, ...)
{
	char	*buffer;
	va_list	args;
	int	length;
	
			
	bell (); bell ();
	
	length = (int)xv_get(message_textsw, TEXTSW_LENGTH);
	buffer = mymalloc ((unsigned)(length+1));
	xv_get(message_textsw, TEXTSW_CONTENTS, 0, buffer, length);
	buffer[length] = '\0';
	
	fprintf (stderr, "%s\n", buffer);
	
	fprintf (stderr, "Graphed : FATAL ERROR : ");
	
	va_start (args, format);
	vfprintf (stderr, format, args);
	fprintf (stderr, "good bye !\n");
	
	va_end (args);
	
	die ();
}
/************************************************************************/
/*									*/
/*			PROGRAMM ABBRECHEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	die ();							*/
/*									*/
/*	Bricht das Programm ab und erzeugt dabei ein core-file.		*/
/*									*/
/************************************************************************/


void die (void)
{
	abort();
}
