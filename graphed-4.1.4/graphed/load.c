/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				load.c					*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul enthaelt die Prozedur load zum Laden von Graphen.	*/
/*	WICHTIG : Das eigentliche Laden wird im Parser yyparse ()	*/
/*	(-> scanner.l, parser.y) beschrieben.				*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"
#include "load.h"
#include "draw.h"

#include "graphed_subwindows.h"
#include "menu.h"
#include "user.h"

#include <std.h>
#include <slist.h>

#include "derivation.h"

extern int yyparse(void);

/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	int	load_from_file (buffer, filename, filetype)		*/
/*	void	set_lex_input (file)					*/
/*	char	buffer_get_filename(wac_buffer) ()					*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*			GLOBALE VARIABLE				*/
/*									*/
/************************************************************************/


int	lex_input_file_linenumber    = 0;
int	overwrite_state = TRUE;

int	load_buffer;

Load_filetype		filetype_last_loaded;
Derivation_sequence	last_loaded_derivation_sequence;

/************************************************************************/
/*									*/
/*			LADEN VON GRAPHEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	int	load_from_file (buffer, filename, filetype)		*/
/*									*/
/*	Laedt einen neuen Graphen (mittels yyparse) aus filename.	*/
/*	in <buffer>.							*/
/*	Ist filename == "", so wird von stdin eingelesen.		*/
/*	Rueckmeldung ist TRUE, falls das Laden erfolgreich war.		*/
/*	WICHTIG : Ein Fehler beim Einlesen hat zur Folge, dass der	*/
/*	ganze bis dahin eingelesene Graph (im Parser) geloescht wird.	*/
/*									*/
/*	load aktiviert bzw. deaktiviert im Menue die Punkte		*/
/*	LOAD_AGAIN und STORE_TO_SAME_FILE, falls das			*/
/*	Laden erfolgreich bzw. erfolglos verlaufen ist.			*/
/*	Der bisherige Graph wird geloescht und (bei erfolgreichem	*/
/*	Laden) im Label des base_frame der neue Filename eingetragen.	*/
/*	Load_graph oeffnet und schliesst die Datei selbststaendig	*/
/*	Wichtige Ausnahme : stdin).					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	set_lex_input (file)					*/
/*									*/
/*	Setzt die Eingabe von yylex() (= yyin) auf file. Die		*/
/*	Zeilennummer lex_input_file_linenumber wird auf 1		*/
/*	zurueckgesetzt.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	char	buffer_get_filename(wac_buffer) ()			*/
/*									*/
/*	Gibt einen Zeiger auf den Filenamen aus dem aktuellen buffer	*/
/*	zurueck.							*/
/*									*/
/************************************************************************/


Load_filetype	load_from_file (int buffer, char *filename, Load_filetype filetype)
{
	FILE	*file;
	char	*fname, fn[FILENAMESIZE];
	int	loading_successful;
	
	if (!strcmp(filename, "")) {
		message ("Loading graph from stdin\n");
		file = stdin;
	} else {
		if (filename == NULL) {
			error ("Can't find file %s anywhere\n", filename);
			return FALSE;
		} else {
			if ((fname = file_exists_somewhere (filename, getenv ("GRAPHED_INPUTS"))) != NULL) {
				strcpy (fn, fname);
			} else {
				error ("Can't get file %s\nFile does not exist or too many files match pattern\n", filename);
				return LOAD_NOTHING;
			}
			if ( (file = fopen (fn, "r")) == (FILE *)NULL) {
				error ("Can't open file %s\n", fn);
				sys_error (errno);
				inactivate_menu_item (LOAD_AGAIN);
				inactivate_menu_item (STORE_TO_SAME_FILE);
				return FALSE;
			} else
				message ("Loading graph from file %s\n", fn);
		}
	}
	
	load_buffer = buffer;
	graphed_state.loading = TRUE;
	filetype_last_loaded = LOAD_NOTHING;
	set_last_graph (empty_graph);
	delete_graphs_in_buffer (buffer);
	
	set_lex_input (file);
	
	loading_successful = (yyparse() == 0);	/* jetzt wird geladen	*/
	
	redraw_all       (); /* Jetzt zeichnen. Damit erspart man sich	*/
	                     /* eine grosse global_repaint_rectlist	*/
	force_repainting ();
	
	if (file != stdin) { fclose (file); file = NULL; }
	
	if (loading_successful) {
		buffer_set_filename (load_buffer, fn);
		/* reset_buffer_has_changed (load_buffer); */
		activate_menu_item (LOAD_AGAIN);
		activate_menu_item (STORE_TO_SAME_FILE);
	} else {
		buffer_set_filename (load_buffer, "");
		inactivate_menu_item (LOAD_AGAIN);
		inactivate_menu_item (STORE_TO_SAME_FILE);
	}
	
	if (buffers[buffer].graphs != empty_graph &&
	    buffers[buffer].graphs == buffers[buffer].graphs->suc) {
		set_last_graph (buffers[buffer].graphs->suc);
	}
	
	graphed_state.loading = FALSE;

	if (filetype_last_loaded & LOAD_DERIVATION) {
		apply_derivation_sequence (get_picked_graph(), last_loaded_derivation_sequence);
		force_repainting ();
	}

	return iif (loading_successful, filetype_last_loaded, LOAD_NOTHING);
}



void	set_lex_input (FILE *file)
{
	extern	FILE	*yyin;	/* Aus der Datei scanner.c, die von lex	*/
				/* erzeugt wird				*/
	
	yyin = file;
	lex_input_file_linenumber    = 1;
}
