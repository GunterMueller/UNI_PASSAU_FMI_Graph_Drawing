/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				main.c					*/
/*									*/
/************************************************************************/
/*									*/
/*		G R A P H  E D    -   M A I N   F I L E			*/
/*									*/
/*	Dieses Modul enthaelt neben der obligatorischen main - Prozedur	*/
/*	die Verwaltung des base_frame (inklusive Erzeugung aller seiner	*/
/*	Subwindows), Initialisierungen fuer verschiedene globale	*/
/*	Variablen und den "Interpreter" fuer Argumente aus der		*/
/*	Kommandozeile.							*/
/*									*/
/************************************************************************/

#include "misc.h"
#include "graph.h"
#include "state.h"
#include "repaint.h"
#include "nodetypes/nodetypes.h"

#include "graphed_subwindows.h"

#include "graphed_mpr.h"
#include "user.h"
#include "load.h"
#include <sys/stat.h>

#define GRAPHED_USAGE	"Usage : graphed [-wa w h] [-f file] [-] [file]"

#define	GRAPHED_BACKGROUND_PIXRECT_FILENAME "graphed.background.pr"

extern void	create_base_frame (int *argc_ptr, char **argv);
extern void	init_canvases (void);
extern void	init_graphs (void);
extern void	set_working_area_canvas (Canvas canvas);
extern void	reset_buffer_has_changed (int buffer);
extern int	yyparse(void);

/************************************************************************/
/*									*/
/*			GLOBALE PROZEDUREN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	set_base_frame_label ()					*/
/*	void	bell ()							*/
/*									*/
/*	void	fill_panel_choice_attr_list_of_strings ();		*/
/*	void	fill_panel_choice_attr_list_of_images  ();		*/
/*									*/
/************************************************************************/

void	fill_panel_choice_attr_list_of_strings (char **strings, int n, char **attr_list);
void	fill_panel_choice_attr_list_of_images  (Server_image *images, int n, char **attr_list);



/************************************************************************/
/*									*/
/*			GLOBALE VARIABLE				*/
/*									*/
/************************************************************************/


Frame	base_frame;

int	screenwidth,	/* Bildschirmgroesse, wird dynamisch in		*/
	screenheight;	/* create_base_frame ermittelt.			*/


Graphed_state	graphed_state;	/* Globaler Programmzustand		*/


/*	temporary_subframe_shown : "Semaphor", um zu verhindern, dass	*/
/*	zwei temporaere Subframes gleichzeitig erzeugt werden (das	*/
/*	spart Filedeskriptoren, da jeder FRAME, jedes PANEL und jeder	*/
/*	CANVAS einen, jedes TEXTSW sogar drei dieser leider auch in	*/
/*	UNIX nicht unbegrenzt verfuegbaren Kanaele belegen).		*/

int	temporary_subframe_shown = FALSE;
	


/*	Listen mit Texten bzw. Bildern, mit denen Aufzahelungstypen	*/
/*	in Menues etc. dargestellt werden koennen.			*/
/*	Jede Liste ist zweimal vorhanden :				*/
/*	- die Texte (..._strings) bzw. Bilder (..._images) selbst	*/
/*	- dieselben Listen so aufbereitet (..._for_cycle), dass sie in	*/
/*	  PANEL_CHOICE's ueber ATTR_LIST uebergeben werden koennen.	*/
/*	Die Aufbereitung erfolgt in der Prozedur init_misc.		*/

char		*nei_strings         [NUMBER_OF_NODE_EDGE_INTERFACES];
Server_image	nei_images           [NUMBER_OF_NODE_EDGE_INTERFACES];
char		*nlp_strings         [NUMBER_OF_NODELABEL_PLACEMENTS];
Server_image	nlp_images           [NUMBER_OF_NODELABEL_PLACEMENTS];
char		*scaling_strings     [NUMBER_OF_SCALINGS];
char		*gragra_type_strings [NUMBER_OF_GRAGRA_TYPES];

char	*nei_strings_for_cycle [NUMBER_OF_NODE_EDGE_INTERFACES+3];
char	*nei_images_for_cycle  [NUMBER_OF_NODE_EDGE_INTERFACES+3];
char	*nlp_strings_for_cycle [NUMBER_OF_NODELABEL_PLACEMENTS+3];
char	*nlp_images_for_cycle  [NUMBER_OF_NODELABEL_PLACEMENTS+3];
char	*scaling_strings_for_cycle [NUMBER_OF_SCALINGS+3];
char	*gragra_type_strings_for_cycle [NUMBER_OF_GRAGRA_TYPES+3];

#ifdef LP_LAYOUT
lp_init_grammar_preconditions
#endif


/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/

#if 0
static	int	type_setup_width,	/* Groessen der einzelnen	*/
		font_setup_width,	/* Fenster			*/
		type_setup_height,
		font_setup_height,
		message_textsw_height,
		message_textsw_width;
#endif

/************************************************************************/
/*									*/
/*		LOKALE FUNKTIONEN / PROZEDUREN				*/
/*									*/
/************************************************************************/


static	void		init_misc                              (void);

static	void		load_initialisation_file               (void);
static	void		dispatch_command_line_arguments        (int argc, char **argv);
static	void		command_line_error                     (void);


/************************************************************************/
/*									*/
/*									*/
/*	+=======================================================+	*/
/*	!							!	*/
/*	!	+---------------------------------------+	!	*/
/*	!	|					|	!	*/
/*	!   *   |	H A U P T P R O G R A M M	|   *	!	*/
/*	!	|					|	!	*/
/*	!	+---------------------------------------+	!	*/
/*	!							!	*/
/*	+=======================================================+	*/
/*									*/
/*									*/
/************************************************************************/
/*									*/
/*	   Initialisiert alles moegliche usw. und startet die		*/
/*			   window_main_loop				*/
/*									*/
/************************************************************************/


void my_error_proc (Xv_object object, Attr_attribute *avlist)
{
	printf ("%s\n", xv_error_format (object, avlist));
	fflush (stdout);
}


void graphed_main(int argc, char **argv)
{
	int i;

#undef GRAPHED_DEBUG_MALLOC
#ifdef GRAPHED_DEBUG_MALOOC
#include <malloc.h>
malloc_debug (2);
/* remember to link with /usr/lib/debug/malloc.o */
#endif

	xv_init(XV_INIT_ARGC_PTR_ARGV, &argc, argv,
		XV_ERROR_PROC, my_error_proc,
		NULL);

	init_graphed_state ();	/* -> state.c				*/
	graphed_state.startup = TRUE;

	init_config   ();
	init_misc     ();	
	init_graphed_colormap ();
	
	create_base_frame(&argc, argv);
	create_message_textsw ();
	window_fit (base_frame);
	create_working_area_menu ();

	graphed_state.colorscreen = FALSE;
	init_graphed_graphics_state ();

	init_canvases ();
			
	create_node_subframe    ();
	create_edge_subframe    ();
	create_group_subframe   ();
	create_node_defaults_subframe ();
	create_edge_defaults_subframe ();
	
	init_fonts   ();	/* -> fonts.c				*/
	init_types   ();	/* -> types.c				*/
	init_system_nodetypes (); /* -> nodetypes/nodetypes.c		*/
	init_buffers ();	/* -> ggraph.c				*/
	
	init_graph_state   ();	/* -> state.c				*/

#ifdef LP_LAYOUT
	lp_init_lgg_settings();
#endif

	init_graphs        ();	/* -> ggraph.c				*/
	
	set_working_area_canvas (canvases[create_buffer()].canvas);

	buffer_set_filename (wac_buffer, "");
	
	init_user_interface      (); /* -> user.c			*/
	load_initialisation_file (); /* Kann evtl. Progamm abbrechen !	*/

	dispatch_command_line_arguments (argc, argv);
	
	init_extra_menu ();

	init_user_event_functions ();
	init_user_menu ();
	
#ifdef LP_LAYOUT
	lp_add_items_to_layout_menu();
#endif

	menu_called_from = MENU_CALLED_FROM_CANVAS;
	if (buffer_is_empty(wac_buffer)) {
		dispatch_user_action (CREATE_MODE);
	} else {
		dispatch_user_action (EDIT_MODE);
	}
	
	graphed_state.startup = FALSE;

/*
	load_graphed_background_pixrect (GRAPHED_BACKGROUND_PIXRECT_FILENAME);
*/
	
	for (i=N_PASTE_BUFFERS; i<N_BUFFERS; i++) {
		reset_buffer_has_changed (i);
	}

	xv_main_loop(base_frame);
}
/************************************************************************/
/*									*/
/*			INITIALISIERUNGEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	static	void	load_initialisation_file ()			*/
/*									*/
/*	Initialisiert Zeichensatztabelle, Knoten- und Kantentypen-	*/
/*	tabellen, Attribute von Knoten und Kanten sowie Groesse und	*/
/*	Scrollbars der working_area.					*/
/*	Die zu setzenden Werte werden einer Datei			*/
/*	GRAPHED_INITIALISATION_FILE (".graphed") entnommen, die		*/
/*	zunaechst im "."- und dann im "Home" - Directory gesucht wird.	*/
/*	Falls sie dort nicht gefunden wird, sucht die Prozedur nach	*/
/*	der Datei GRAPHED_DEFAULT_INITIALISATION_FILE. Ist auch das	*/
/*	erfolglos, so wird das Programm abgebrochen.			*/
/*	30/11/93 now also searching in GRAPHED_INPUTS			*/
/*	IN DIESER ROUTINE KANN DAS PROGRAMM ABGEBROCHEN WERDEN, falls	*/
/*	naemlich beim parsen der Datei ein Fehler (Syntax, aber auch	*/
/*	wenn keine Zeichensaetze, Knoten - oder Kantentypen gefunden)	*/
/*	auftritt (oder die Datei gar nicht gefunden wird).		*/
/*									*/
/*======================================================================*/
/*									*/
/*	static	void	init_misc ()					*/
/*									*/
/*	Tut, was der Name sagt : Initialisierung von			*/
/*	- nlp_strings, nlp_images					*/
/*	- nei_strings, nei_images					*/
/*	- scaling_strings						*/
/*	- nlp_strings_for_cycle, nei_images_for_cycle			*/
/*	- nei_strings_for_cycle, nlp_images_for_cycle			*/
/*	- scaling_strings_for_cycle					*/
/*	- global_repaint_rectlist, global_erase_rectlist (->repaint.c,	*/
/*	  direkt schafft das C-Compiler leider nicht)			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	fill_panel_choice_attr_list_of_strings (		*/
/*				strings, n, attr_list)			*/
/*	void	fill_panel_choice_attr_list_of_images (			*/
/*				images,  n, attr_list)			*/
/*									*/
/*	Hilfsprozeduren fuer init_misc : erzeugen aus dem Array		*/
/*	strings (images) der Laenge n eine Liste attr_list, die an ein	*/
/*	PANEL_CHOICE als ATTR_LIST uebergeben werden kann.		*/
/*	Genug Speicherplatz fuer attr_list muss bereitgestellt werden !	*/
/*									*/
/************************************************************************/



static	void	load_initialisation_file (void)
{
	FILE	*initialisation_file;
	char	initialisation_file_name [FILENAMESIZE];
	char	path [FILENAMESIZE], *extended_filename;

/*	static	char	*try_nodetypes[] = {
		"#box",
		"#circle",
		"#diamond"
	};
	static	char	*try_edgetypes[] = {
		"#solid",
		"#dashed",
		"#dotted",
		"#dashdotted",
		"#dashdotdotted",
		"#longdashed"
	};
	static	char	*try_fonts[] = {
		"/usr/lib/fonts/fixedwidthfonts/cour.r.10",
		"/usr/lib/fonts/fixedwidthfonts/cour.b.10",
		"/usr/lib/fonts/fixedwidthfonts/cour.r.12",
		"/usr/lib/fonts/fixedwidthfonts/cour.b.12",
		"/usr/lib/fonts/fixedwidthfonts/cour.r.14",
		"/usr/lib/fonts/fixedwidthfonts/cour.b.14",
		"/usr/lib/fonts/fixedwidthfonts/cour.r.16",
		"/usr/lib/fonts/fixedwidthfonts/cour.b.16",
		"/usr/lib/fonts/fixedwidthfonts/cour.r.18",
		"/usr/lib/fonts/fixedwidthfonts/cour.b.18",
		"/usr/lib/fonts/fixedwidthfonts/cour.r.24",
		"/usr/lib/fonts/fixedwidthfonts/cour.b.24"
		}; */

	char	*getenv_home, *getenv_graphed_inputs;

	getenv_home = getenv ("HOME");
	if (getenv_home == NULL) {
		getenv_home = "";
	}
	getenv_graphed_inputs = getenv ("GRAPHED_INPUTS");
	if (getenv_graphed_inputs == NULL) {
		getenv_graphed_inputs = "";
	}

	strcpy (initialisation_file_name, GRAPHED_INITIALISATION_FILE);
	sprintf (path, "./:%s/:%s", getenv_home, getenv_graphed_inputs);
	extended_filename = file_exists_somewhere (initialisation_file_name, path);

	if ((initialisation_file = fopen (extended_filename, "r")) == (FILE *)NULL) {
		if ((initialisation_file = fopen (GRAPHED_DEFAULT_INITIALISATION_FILE, "r")) ==
		    (FILE *)NULL) {
			fatal_error ("Can't find %s - startup failed\n",
			             GRAPHED_INITIALISATION_FILE);
		}
	}
	load_buffer = wac_buffer;
	set_lex_input (initialisation_file);
	if (yyparse() == 1)
		fatal_error ("error reading %s\n", GRAPHED_INITIALISATION_FILE);

	fclose (initialisation_file);
}



static	void	init_misc (void)
{
	int	i;

	svi_init (); /* Initialize XView Server_image's */

	nlp_strings [(int)NODELABEL_MIDDLE]     = "middle";
	nlp_strings [(int)NODELABEL_UPPERLEFT]  = "upper left";
	nlp_strings [(int)NODELABEL_UPPERRIGHT] = "upper right";
	nlp_strings [(int)NODELABEL_LOWERLEFT]  = "lower left";
	nlp_strings [(int)NODELABEL_LOWERRIGHT] = "lower right";
	
	nlp_images [(int)NODELABEL_MIDDLE]     = nlp_middle_icon_svi;
	nlp_images [(int)NODELABEL_UPPERLEFT]  = nlp_upperleft_icon_svi;
	nlp_images [(int)NODELABEL_UPPERRIGHT] = nlp_upperright_icon_svi;
	nlp_images [(int)NODELABEL_LOWERLEFT]  = nlp_lowerleft_icon_svi;
	nlp_images [(int)NODELABEL_LOWERRIGHT] = nlp_lowerright_icon_svi;
	
	
	nei_strings [(int)NO_NODE_EDGE_INTERFACE]      = "none";
	nei_strings [(int)TO_BORDER_OF_BOUNDING_BOX]   = "to middle of border of bounding box";
	nei_strings [(int)TO_CORNER_OF_BOUNDING_BOX]   = "to corner of bounding box";
	nei_strings [(int)CLIPPED_TO_MIDDLE_OF_NODE]   = "clipped to middle of node";
	nei_strings [(int)SPECIAL_NODE_EDGE_INTERFACE] = "special";
	nei_strings [(int)STRAIGHT_LINE_NEI] = "straight line";

	nei_images [(int)NO_NODE_EDGE_INTERFACE]      = nei_none_icon_svi;
	nei_images [(int)TO_BORDER_OF_BOUNDING_BOX]   = nei_middle_icon_svi;
	nei_images [(int)TO_CORNER_OF_BOUNDING_BOX]   = nei_corner_icon_svi;
	nei_images [(int)CLIPPED_TO_MIDDLE_OF_NODE]   = nei_clipped_icon_svi;
	nei_images [(int)SPECIAL_NODE_EDGE_INTERFACE] = nei_special_icon_svi;
	nei_images [(int)STRAIGHT_LINE_NEI] = nei_straight_icon_svi;

	
	scaling_strings [(int)SCALE_16_16]    = "16  x 16    ";
	scaling_strings [(int)SCALE_32_32]    = "32  x 32    ";
	scaling_strings [(int)SCALE_64_64]    = "64  x 64    ";
	scaling_strings [(int)SCALE_96_96]    = "96  x 96    ";
	scaling_strings [(int)SCALE_128_128]  = "128 x 128   ";
	scaling_strings [(int)SCALE_192_192]  = "192 x 192   ";
	scaling_strings [(int)SCALE_256_256]  = "256 x 256   ";
	scaling_strings [(int)SCALE_384_384]  = "384 x 384   ";
	scaling_strings [(int)SCALE_512_512]  = "512 x 512   ";
	scaling_strings [(int)SCALE_IDENTITY] = "...         ";
	scaling_strings [(int)SCALE_DOWN_XY]  = "down   x & y";
	scaling_strings [(int)SCALE_DOWN_X]   = "down   x    ";
	scaling_strings [(int)SCALE_DOWN_Y]   = "down   y    ";
	scaling_strings [(int)SCALE_UP_XY]    = "up     x & y";
	scaling_strings [(int)SCALE_UP_X]     = "up     x    ";
	scaling_strings [(int)SCALE_UP_Y]     = "up     y    ";
	scaling_strings [(int)SCALE_SQUARE_X] = "square x    ";
	scaling_strings [(int)SCALE_SQUARE_Y] = "square y    ";
	
	gragra_type_strings [gragra_type_to_int(ENCE_1)] = "1-ENCE";
	gragra_type_strings [gragra_type_to_int(NCE_1)]  = "1-NCE";
	gragra_type_strings [gragra_type_to_int(NLC)]    = "NLC";
	gragra_type_strings [gragra_type_to_int(BNLC)]   = "BNLC";
	
	fill_panel_choice_attr_list_of_strings (
		nlp_strings, NUMBER_OF_NODELABEL_PLACEMENTS,
		nlp_strings_for_cycle);
	fill_panel_choice_attr_list_of_images (
		nlp_images, NUMBER_OF_NODELABEL_PLACEMENTS,
		nlp_images_for_cycle);
	fill_panel_choice_attr_list_of_strings (
		nei_strings, NUMBER_OF_NODE_EDGE_INTERFACES,
		nei_strings_for_cycle);
	fill_panel_choice_attr_list_of_images (
		nei_images, NUMBER_OF_NODE_EDGE_INTERFACES,
		nei_images_for_cycle);
	fill_panel_choice_attr_list_of_strings (
		scaling_strings, 10,
		scaling_strings_for_cycle);
	fill_panel_choice_attr_list_of_strings (
		gragra_type_strings, NUMBER_OF_GRAGRA_TYPES,
		gragra_type_strings_for_cycle);
	
	for (i=0; i<N_BUFFERS; i++) {
		global_repaint_rectlists[i] = rl_null;
		global_erase_rectlists[i]   = rl_null;
	}
}


void    fill_panel_choice_attr_list_of_strings (char **strings, int n, char **attr_list)
{
	int	i;
	
	attr_list [0] = (char *)PANEL_CHOICE_STRINGS;
	for (i=0; i<n; i++)
		attr_list[i+1] = strings[i];
	attr_list [n+1] = (char *)0;
	attr_list [n+2] = (char *)0;
}



void	fill_panel_choice_attr_list_of_images (Server_image *images, int n, char **attr_list)
{
	int	i;
	
	attr_list [0] = (char *)PANEL_CHOICE_IMAGES;
	for (i=0; i<n; i++)
		attr_list[i+1] = (char *)images[i];
	attr_list [n+1] = (char *)0;
	attr_list [n+2] = (char *)0;
}



int	load_graphed_background_pixrect (char *name)
{
	FILE	*file;
	char	*full_name;
	
	full_name = file_exists_somewhere (GRAPHED_BACKGROUND_PIXRECT_FILENAME, getenv ("GRAPHED_INPUTS"));
	
	if ((full_name != NULL) && ((file = fopen (full_name, "r")) != (FILE *)NULL)) {
/** ----------------- von fb auskommentiert   Anfang ----------------- **
		background_pixrect = pr_load (file, NULL);
 ** ----------------- von fb auskommentiert    Ende ------------------ **/
		fclose (file);
		return TRUE;
	} else {
		return FALSE;
	}
}
/************************************************************************/
/*									*/
/*		ARGUMENTE AUS DER KOMMANDOZEILE				*/
/*									*/
/************************************************************************/
/*									*/
/*	static	void	dispatch_command_line_arguments (argc, argv)	*/
/*									*/
/*	Wertet die Argumente in der Kommandozeile aus, argc und argv	*/
/*	wie in main. Bei einem Fehler bricht das Programm ab.		*/
/*									*/
/*	Moegliche Argumente sind:					*/
/*	-wa -working_area <w> <h>	Groesse der working_area	*/
/*	-f  -file         <datei>	lade Graph von Datei		*/
/*	-   -stdin			lade Graph von stdin		*/
/*	-h  -graphed_help		Argumente ausgeben		*/
/*	                  <datei>	lade Graph von Datei		*/
/*									*/
/*	wobei <datei> = [^-].* (also alles, das nicht mit '-'		*/
/*	anfaengt); die Option "-f" wird eigentlich nur dann gebraucht,	*/
/*	wenn ein Dateiname mit '-' beginnt (!?!).			*/
/*									*/
/*======================================================================*/
/*									*/
/*	static	void	command_line_error ()				*/
/*									*/
/*	Gibt eine "Usage : ..." - Meldung auf stderr aus und BRICHT	*/
/*	DAS PROGRAMM AB.						*/
/*									*/
/************************************************************************/



static	int	command_line_loader (char *filename, int files_loaded)
{
	Load_filetype	loaded;
	int		buffer;

	if (files_loaded == 0) {
		buffer = wac_buffer;
	} else {
		buffer = get_buffer_by_name (filename);
	}

	loaded = load_from_file (buffer, filename, LOAD_ANY_FILE);

	if (loaded != LOAD_NOTHING) {
		set_working_area_canvas(canvases[buffer].canvas);
		files_loaded ++;
	}

	dispatch_user_action (EDIT_MODE);

	return files_loaded;
}


static	void	graphed_snooper (void)
{
	Graphed_snoop_mode	mode;
	struct	stat		buf1, buf2;
	int			stat1, stat2;

	char			filename_snooper[FILENAMESIZE];
	static	time_t		last_snoop_time = 0;

	mode = get_graphed_snoop_mode();
	sprintf (filename_snooper, "%s.in", mode.filename);

	stat1 = stat (mode.filename, &buf1);
	stat2 = stat (filename_snooper, &buf2);
	if (stat1 == -1 || stat2 == -1) {
		return;
	} else if (buf2.st_ctime > last_snoop_time) {
		last_snoop_time = buf2.st_ctime;
		dispatch_user_action (BASIC_LOAD, wac_buffer, mode.filename);
	}

}


static	void	install_graphed_snooper(void)
{
 	static	struct itimerval timer;

	timer.it_value.tv_usec = 100000;
	timer.it_interval.tv_usec = 100000;

	notify_set_itimer_func (base_frame, (Notify_func)graphed_snooper, ITIMER_REAL, &timer, NULL);
}



static	void	dispatch_command_line_arguments (int argc, char **argv)
{
	int	i;
	int	files_loaded = 0;

	for (i=1; i<argc; i++) {
		if ( !strcmp(argv[i], "-wa") || !strcmp(argv[i], "-working_area")) {

			if (i+2 < argc) {
				graphed_state.default_working_area_canvas_width  = atoi (argv[i+1]);
				graphed_state.default_working_area_canvas_height = atoi (argv[i+2]);
				set_working_area_size (graphed_state.default_working_area_canvas_width, graphed_state.default_working_area_canvas_height);
				i += 2;
			} else
				command_line_error ();

		} else if (!strcmp(argv[i], "-f") || !strcmp(argv[i], "-file")) {


			if (i+1 < argc)  {
				files_loaded = command_line_loader (argv[i+1], files_loaded);
				i += 1;
			} else {
				command_line_error ();
			}

		} else if (!strcmp(argv[i], "-h") || !strcmp(argv[i], "-graphed_help")) {

			printf ("FLAG  LONG FLAG     ARGS   DESCRIPTION\n");
			printf ("-wa  -working_area  w h    Set working area size\n");
			printf ("-f   -file          file   Load graph\n");
			printf ("-    -stdin                Load graph from stdin\n");
			printf ("-h   -graphed_help         Print this message\n");
			printf ("                    file   Load graph (same as -f)\n");
			exit (0);

		} else if (!strcmp(argv[i], "-") || !strcmp(argv[i], "-stdin")) {

			files_loaded = command_line_loader ("", files_loaded);

		} else if (!strcmp(argv[i], "-view_only") || !strcmp(argv[i], "-vo")) {

			set_graphed_view_only_mode (GRAPHED_VIEW_ONLY);

		} else if (!strcmp(argv[i], "-snoop") || !strcmp(argv[i], "-s")) {

			Graphed_snoop_mode mode;

			mode.snooping = GRAPHED_SNOOPING;
			if (i+1 < argc)  {
				mode.filename = argv[i+1];
				i += 1;
			} else {
				command_line_error ();
			}

			set_graphed_snoop_mode (mode);
			install_graphed_snooper ();

		} else if (!strcmp(argv[i], "-hide_base_frame")) {

			set_show_base_frame (FALSE);

		} else if (argv[i][0] != '-') {

			files_loaded = command_line_loader (argv[i], files_loaded);

		} else {
			command_line_error ();
		}
	}
}


static	void	command_line_error (void)
{
	fprintf (stderr, "%s\n", GRAPHED_USAGE);
	exit (1);
}


/************************************************************************/
/*									*/
/*			LAST, BUT NOT LEAST				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	bell ()							*/
/*									*/
/*	Klingelt.							*/
/*	BUG : wenn der Benutzer mit defaultsedit die Klingel		*/
/*	abgeschaltet hat, tut diese Prozedur nichts !			*/
/*									*/
/************************************************************************/


int	bell (void)
{
	window_bell (base_frame);
	return 0;
}

