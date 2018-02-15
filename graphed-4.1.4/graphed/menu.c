/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				menu.c					*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul uebernimmt die Verwaltung des main_menu, des	*/
/*	Menues der working_area.					*/
/*									*/
/************************************************************************/


#include "misc.h"
#include "graph.h"

#include <sgraph/std.h>
#include <sgraph/slist.h>

#include "graphed_subwindows.h"
#include "graphed_mpr.h"
#include "menu.h"
#include "derivation.h"
#include "user.h"
#include <xview/font.h>

/* Trennstrich im Menue nach Mac-Art	*/

#define MENU_OLD_SEPARATOR(string)			\
	MENU_ITEM,					\
		MENU_STRING,		string,		\
		MENU_CLIENT_DATA,	NO_ACTION,	\
		NULL


#define MENU_SEPARATOR(string) \
	MENU_ITEM,			           \
		MENU_STRING,		string,    \
		MENU_CLIENT_DATA,	NO_ACTION, \
		0


/* #define MENU_EMPTY_SEPARATOR MENU_SEPARATOR("") */
#define MENU_EMPTY_SEPARATOR MENU_APPEND_ITEM, xv_create (XV_NULL, MENUITEM_SPACE, NULL)

/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_working_area_menu ()				*/
/*									*/
/*	void	install_node_edge_interface_in_menu (nei)		*/
/*	void	install_nodelabel_placement_in_menu (nlp)		*/
/*	void	install_nodesize_in_menu      (x,y)			*/
/*	void	install_edgelabelsize_in_menu (x,y)			*/
/*	void	install_arrowlength_in_menu  (length)			*/
/*	void	install_arrowangle_in_menu   (angle)			*/
/*	void	install_nodelabel_visibility_in_menu (visible)		*/
/*	void	install_edgelabel_visibility_in_menu (visible)		*/
/*	void	install_grid_in_menu (width)				*/
/*									*/
/*	void	set_menu_selection ()					*/
/*									*/
/*	void	activate_menu_item   (action)				*/
/*	void	inactivate_menu_item (action)				*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*			GLOBALE VARIABLEN				*/
/*									*/
/************************************************************************/


Menu		main_menu;

		
/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/


	Menu	file_submenu;
	Menu		file_export_submenu;
	Menu	create_submenu;
static	Menu		create_graph_submenu;
static	Menu		create_production_submenu;
static	Menu		create_embedding_submenu;
static	Menu			node_edge_interface_menu;
static	Menu			nodelabel_placement_menu;
static	Menu			nodelabel_visibility_menu;
static	Menu			nodesize_scaling_menu;
static	Menu			edgelabelsize_menu;
static	Menu			edgelabel_visibility_menu;
static	Menu			arrowlength_menu;
static	Menu			arrowangle_menu;
	Menu	edit_submenu;
static	Menu		edit_select_submenu;
static	Menu		edit_edit_submenu;
static	Menu		edit_delete_submenu;
static	Menu		edit_copy_submenu;
static	Menu		edit_paste_submenu;
static	Menu		edit_direction_submenu;
static	Menu		edit_zoom_submenu;
	Menu	gragra_submenu;
static	Menu		gragra_type_submenu;
static	Menu		gragra_productions_submenu;
static	Menu		gragra_goodies_submenu;
	Menu	misc_submenu;
static	Menu		misc_grid_submenu;
static	Menu		misc_customize_submenu;
static	Menu		misc_statistics_submenu;
	Menu	tools_submenu;
	Menu	goodies_submenu;
	Menu	layout_submenu;
	Menu	user_submenu;
	Menu	about_submenu;

typedef	struct	{
	User_action	main_action;
	User_action	node_edge_interface;
	User_action	nodelabel_placement;
	User_action	nodelabel_visibility;
	User_action	all_nodelabel_visibility;
	User_action	nodesize_scaling;
	User_action	edgelabelsize_scaling;
	User_action	edgelabel_visibility;
	User_action	all_edgelabel_visibility;
	User_action	arrowlength;
	User_action	arrowangle;
	User_action	gragra_type;
	User_action	grid;
	}
	Selected;

Selected	selected;      /* Siehe set_menu_selection unten	*/


/************************************************************************/
/*									*/
/*			LOKALE FUNKTIONEN				*/
/*									*/
/************************************************************************/


static	void	mark_working_area_menu   (User_action action);
static	void	unmark_working_area_menu (User_action action);
static	void	set_menu_default_item    (Menu menu, User_action action);

void	add_menu_to_menu(Menu menu, char *string, Menu add_menu);


/************************************************************************/
/*									*/
/*			MAIN_MENU AUFBAUEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_working_area_menu ()				*/
/*									*/
/*	Baut main_menu auf.						*/
/*									*/
/*======================================================================*/
/*									*/
/*	Zum Aufbau :							*/
/*									*/
/*	Ein menu_item hat folgende Gestalt :				*/
/*									*/
/*	User_action	client_data;					*/
/*									*/
/*	MENU_ITEM,							*/
/*		MENU_IMAGE / MENU_STRING, ...,				*/
/*		...							*/
/*		MENU_CLIENT_DATA, client_data, (Auch bei Pullright !)	*/
/*	0,								*/
/*									*/
/*	Dieses Item wird im folgenden (von aussen) immer ueber		*/
/*	client_data angesprochen; bei Auswahl eines Items durch den	*/
/*	Benutzer gibt client_data an, welche Aktion mit diesem Item	*/
/*	assoziiert ist.							*/
/*	Das Attribut MENU_BOXED wird verwendet, um ein Item zu		*/
/*	markieren. (Out of Date)					*/
/*									*/
/************************************************************************/


static	void	graphed_menu_action_proc (Menu menu, Menu_item menu_item)
{
	/* menu_called_from = MENU_CALLED_FROM_CANVAS; */

	dispatch_user_action(
		(User_action)xv_get (menu_item, MENU_CLIENT_DATA));

	menu_called_from = MENU_CALLED_FROM_NOWHERE;

	force_repainting();
}


void	create_working_area_menu (void)
{
	main_menu = graphed_create_pin_menu ("GraphEd");

	/*
	 *  File submenu
	 */

	file_export_submenu = graphed_create_pin_menu ("Export");
	add_graphed_string_entry_to_menu (file_export_submenu, "Postscript ...", PRINT);

	file_submenu = graphed_create_pin_menu ("File");

	add_graphed_string_entry_to_menu (file_submenu, "Load ...    <>L", LOAD_BY_SUBFRAME);
	add_graphed_string_entry_to_menu (file_submenu, "Load again   ^L", LOAD_AGAIN);
	add_graphed_string_entry_to_menu (file_submenu, "Store ...   <>S", STORE_BY_SUBFRAME);
	add_graphed_string_entry_to_menu (file_submenu, "Store again  ^S", STORE_TO_SAME_FILE);
	add_graphed_string_entry_to_menu (file_submenu, "New window   ^W", CREATE_BUFFER);
	add_menu_to_menu                 (file_submenu, "Export ...     ", file_export_submenu);
	add_graphed_string_entry_to_menu (file_submenu, "",                NO_ACTION);
	add_graphed_string_entry_to_menu (file_submenu, "Quit           ", QUIT_GRAPHED);


	/*
	 *  Create submenu
	 */

	create_submenu = graphed_create_pin_menu("Create");

	create_graph_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (create_graph_submenu, "Directed    ^D", CREATE_DIRECTED_GRAPH);
	add_graphed_string_entry_to_menu (create_graph_submenu, "Undirected  ^U", CREATE_UNDIRECTED_GRAPH);

	create_production_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (create_production_submenu, "Directed  ", CREATE_DIRECTED_PRODUCTION);
	add_graphed_string_entry_to_menu (create_production_submenu, "Undirected", CREATE_UNDIRECTED_PRODUCTION);

	create_embedding_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (create_embedding_submenu, "Directed  ", CREATE_DIRECTED_EMBEDDING);
	add_graphed_string_entry_to_menu (create_embedding_submenu, "Undirected", CREATE_UNDIRECTED_EMBEDDING);

	add_graphed_string_entry_to_menu (create_submenu, "Create Mode        F1",  CREATE_MODE);
	add_menu_to_menu                 (create_submenu, "Graph              ^N",  create_graph_submenu);
	add_menu_to_menu                 (create_submenu, "GraGra production    ",  create_production_submenu);
	add_menu_to_menu                 (create_submenu, "GraGra embedding     ",  create_embedding_submenu);
	add_graphed_string_entry_to_menu (create_submenu, "Node defaults ...  F6", NODE_DEFAULTS);
	add_graphed_string_entry_to_menu (create_submenu, "Edge defaults ...  F7", EDGE_DEFAULTS);


	/*
	 *  Edit submenu
	 */

	edit_submenu = graphed_create_pin_menu ("Edit");

	edit_delete_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (edit_delete_submenu, "Selection  CUT", DELETE_SELECTION);
	add_graphed_string_entry_to_menu (edit_delete_submenu, "All      <>CUT", DELETE_ALL);

	edit_copy_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (edit_copy_submenu, "Selection  COPY", PUT_SELECTION);
	add_graphed_string_entry_to_menu (edit_copy_submenu, "Graph    <>COPY", PUT_WHOLE_GRAPH);

	edit_paste_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (edit_paste_submenu, "Into current graph  PASTE", GET_SELECTION);
	add_graphed_string_entry_to_menu (edit_paste_submenu, "Into a new graph  <>PASTE", GET_AS_GRAPH);

	edit_select_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (edit_select_submenu, "This graph  ^G", SELECT_GRAPH_OF_SELECTION);
	add_graphed_string_entry_to_menu (edit_select_submenu, "All        <>A", SELECT_ALL);

	edit_edit_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (edit_edit_submenu, "Selection  ^E", EDIT_SELECTION);
	add_graphed_string_entry_to_menu (edit_edit_submenu, "Graph     <>E", EDIT_GRAPH_OF_SELECTION);


	edit_direction_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (edit_direction_submenu, "Reverse edge          ^R", REVERSE_EDGE);
	add_graphed_string_entry_to_menu (edit_direction_submenu, "Graph un<->directed  <>R", SWAP_SELECTED_GRAPH_DIRECTEDNESS);

	edit_zoom_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (edit_zoom_submenu, "In   ^,", EXPAND_SELECTION);
	add_graphed_string_entry_to_menu (edit_zoom_submenu, "Out  ^.", SHRINK_SELECTION);

	add_graphed_string_entry_to_menu (edit_submenu, "Edit Mode  F1", EDIT_MODE);
	add_menu_to_menu                 (edit_submenu, "Copy         ", edit_copy_submenu);
	add_menu_to_menu                 (edit_submenu, "Paste        ", edit_paste_submenu);
	add_menu_to_menu                 (edit_submenu, "Delete       ", edit_delete_submenu);
	add_menu_to_menu                 (edit_submenu, "Select       ", edit_select_submenu);
	add_menu_to_menu                 (edit_submenu, "Edit         ", edit_edit_submenu);
	add_graphed_string_entry_to_menu (edit_submenu, "-------------", NO_ACTION);
	add_menu_to_menu                 (edit_submenu, "Direction    ", edit_direction_submenu);
	add_graphed_string_entry_to_menu (edit_submenu, "Split      ^V", SPLIT_SELECTION);
	add_graphed_string_entry_to_menu (edit_submenu, "Merge     <>V", MERGE_SELECTION);
	add_graphed_string_entry_to_menu (edit_submenu, "Smaller    ^,", SHRINK_SELECTION);
	add_graphed_string_entry_to_menu (edit_submenu, "Larger     ^.", EXPAND_SELECTION);
	add_graphed_string_entry_to_menu (edit_submenu, "Center     ^C", CENTER_SELECTION);


	/*
	 *  Gragra submenu
	 */

	gragra_submenu = graphed_create_pin_menu ("GraGra");

	gragra_type_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (gragra_type_submenu, "1-eNCE", SET_GRAGRA_TYPE_ENCE_1);
	add_graphed_string_entry_to_menu (gragra_type_submenu, "1-NCE ", SET_GRAGRA_TYPE_NCE_1);
	add_graphed_string_entry_to_menu (gragra_type_submenu, "NLC   ", SET_GRAGRA_TYPE_NLC);
	add_graphed_string_entry_to_menu (gragra_type_submenu, "BNLC  ", SET_GRAGRA_TYPE_BNLC);

	gragra_productions_submenu = graphed_create_pin_menu ("productions");
	add_graphed_string_entry_to_menu (gragra_productions_submenu, "Apply current  ^A", APPLY_CURRENT_PRODUCTION);
	add_graphed_string_entry_to_menu (gragra_productions_submenu, "Apply random     ", RANDOM_APPLY_PRODUCTION);
	add_graphed_string_entry_to_menu (gragra_productions_submenu, "-----------------", NO_ACTION);

	gragra_goodies_submenu = graphed_create_pin_menu ("Goodies");
	add_entry_to_menu                (gragra_goodies_submenu, "Convert graph to gragra", menu_convert_to_gragra);
/*
	add_entry_to_menu                (gragra_goodies_submenu, "Store derivation       ", menu_store_derivation_sequence);
*/
	add_graphed_string_entry_to_menu (gragra_goodies_submenu, "Pretty print current   ", PRETTY_PRINT_CURRENT_PRODUCTION);
	add_graphed_string_entry_to_menu (gragra_goodies_submenu, "Pretty print all       ", PRETTY_PRINT_ALL_PRODUCTIONS);


	add_graphed_string_entry_to_menu (gragra_submenu, "Compile             ESC", COMPILE_ALL_PRODUCTIONS);
	add_graphed_string_entry_to_menu (gragra_submenu, "Set current          ^Q", SET_CURRENT_PRODUCTION);
	add_menu_to_menu                 (gragra_submenu, "Apply                  ", gragra_productions_submenu);
	add_menu_to_menu                 (gragra_submenu, "Goodies                ", gragra_goodies_submenu);
	add_graphed_string_entry_to_menu (gragra_submenu, "Defaults ...         F8", EDIT_GRAGRA);
	add_entry_to_menu                (gragra_submenu, "Show derivation tree   ", menu_display_derivation_graph);


	/*
	 *  Misc submnenu
	 */

	misc_submenu = graphed_create_pin_menu ("Misc");

	misc_grid_submenu = graphed_create_pin_menu ("Grid");
	add_graphed_string_entry_to_menu (misc_grid_submenu, "8   x   8  <>F5", SET_GRID_8_8);
	add_graphed_string_entry_to_menu (misc_grid_submenu, "16  x  16  <>F6", SET_GRID_16_16);
	add_graphed_string_entry_to_menu (misc_grid_submenu, "32  x  32  <>F7", SET_GRID_32_32);
	add_graphed_string_entry_to_menu (misc_grid_submenu, "64  x  64      ", SET_GRID_64_64);
	add_graphed_string_entry_to_menu (misc_grid_submenu, "128 x 128      ", SET_GRID_128_128);
	add_graphed_string_entry_to_menu (misc_grid_submenu, "Off        <>F8", SET_GRID_OFF);

	misc_customize_submenu = graphed_create_submenu ();
	add_graphed_string_entry_to_menu (misc_customize_submenu, "Node types", EDIT_NODETYPES);
/*
	add_graphed_string_entry_to_menu (misc_customize_submenu, "Edge types", EDIT_EDGETYPES);
	add_graphed_string_entry_to_menu (misc_customize_submenu, "Node fonts", EDIT_NODEFONTS);
	add_graphed_string_entry_to_menu (misc_customize_submenu, "Edge fonts", EDIT_EDGEFONTS);
*/
	misc_statistics_submenu = graphed_create_pin_menu ("Statistics");
	add_graphed_string_entry_to_menu (misc_statistics_submenu, "Selection", SELECTION_STATISTICS);
	add_graphed_string_entry_to_menu (misc_statistics_submenu, "Buffer   ", BUFFER_STATISTICS);
	add_graphed_string_entry_to_menu (misc_statistics_submenu, "All      ", ALL_STATISTICS);

	add_graphed_string_entry_to_menu (misc_submenu, "Redraw all               ", REDRAW_ALL);
	add_graphed_string_entry_to_menu (misc_submenu, "Constrained            F2", TOGGLE_CONSTRAINED);
	add_graphed_string_entry_to_menu (misc_submenu, "Group lables node      F3", TOGGLE_GROUP_LABELLING_OPERATION);
	add_menu_to_menu                 (misc_submenu, "Grid                     ", misc_grid_submenu);
	add_graphed_string_entry_to_menu (misc_submenu, "Save state in .graphed   ", SAVE_STATE);
	add_menu_to_menu                 (misc_submenu, "Customize                ", misc_customize_submenu);
	add_menu_to_menu                 (misc_submenu, "Statistics               ", misc_statistics_submenu);


	/*
	 *  Tools submenu
	 */

	tools_submenu = graphed_create_pin_menu ("Tools");


	/*
	 *  Layout submenu
	 */

	layout_submenu = graphed_create_pin_menu ("Layout");


	/*
	 *  Goodies submenu
	 */

	goodies_submenu = graphed_create_pin_menu ("Goodies");


	/*
	 *  User submenu
	 */

	user_submenu = graphed_create_pin_menu ("User");


	/*
	 *  About submenu
	 */

	about_submenu = graphed_create_pin_menu ("GraphEd");
	add_graphed_string_entry_to_menu (about_submenu,
		"About", ABOUT_GRAPHED);

	/* add_menu_to_menu (main_menu, "File      ", file_submenu); */
	add_menu_to_menu (main_menu, "Create  F1", create_submenu);
	add_menu_to_menu (main_menu, "Edit    F1", edit_submenu);
	add_menu_to_menu (main_menu, "Gragra    ", gragra_submenu);
	/* add_menu_to_menu (main_menu, "Misc      ", misc_submenu); */
	add_menu_to_menu (main_menu, "Tools     ", tools_submenu);
	add_menu_to_menu (main_menu, "Layout    ", layout_submenu);
	add_menu_to_menu (main_menu, "Goodies   ", goodies_submenu);
	add_menu_to_menu (main_menu, "User      ", user_submenu);
	/* add_menu_to_menu (main_menu, "About     ", about_submenu); */


	/* Defaults fuer die Submenues einstellen :			*/
	/* Waehlt der Benutzer nur das Item, von dem aus ein Submenue	*/
	/* aus aufgeklappt wird, an, so wird das MENU_DEFAULT_ITEM AUS	*/
	/* DEM SUBMENUE zurueckgegeben. Dieses Item wird hier fuer die	*/
	/* Menues gesetzt, in denen es nicht von current_... (wie z.B.	*/
	/* bei nodelabel_placement) abhaengt; in den anderen Faellen	*/
	/* wird es in set_menue_selection (s.u.) eingestellt.		*/
	
	set_menu_default_item (create_submenu,      CREATE_MODE);
	set_menu_default_item (edit_submenu,        EDIT_MODE);
	set_menu_default_item (gragra_submenu,      COMPILE_ALL_PRODUCTIONS);
	set_menu_default_item (file_submenu,        LOAD_BY_SUBFRAME);
	set_menu_default_item (misc_submenu,        REDRAW_ALL);
	
	set_menu_default_item (create_graph_submenu,       CREATE_DIRECTED_GRAPH);
	set_menu_default_item (create_production_submenu,  CREATE_DIRECTED_PRODUCTION);
	set_menu_default_item (create_embedding_submenu,   CREATE_DIRECTED_EMBEDDING);
	set_menu_default_item (about_submenu,       ABOUT_GRAPHED);

	
	/* Deaktiviere zu Beginn nicht ausfuehrbare Aktionen		*/
		
	inactivate_menu_item (LOAD_AGAIN);
	inactivate_menu_item (STORE_TO_SAME_FILE);
	inactivate_menu_item (EDIT_SELECTION);
	inactivate_menu_item (DELETE_SELECTION);
	inactivate_menu_item (EXPAND_SELECTION);
	inactivate_menu_item (SHRINK_SELECTION);
	inactivate_menu_item (EXPAND_WORKING_AREA);
	inactivate_menu_item (SHRINK_WORKING_AREA);
	inactivate_menu_item (PUT_SELECTION);
	inactivate_menu_item (GET_SELECTION);
	
}
/************************************************************************/
/*									*/
/*			MENUEAUSWAHL VERWALTEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	Die folgenden Prozeduren setzen eine Auswahl im Menue ein :	*/
/*									*/
/*	void	install_node_edge_interface_in_menu (nei)		*/
/*	void	install_nodelabel_placement_in_menu (nlp)		*/
/*	void	install_nodesize_in_menu      (x,y)			*/
/*	void	install_edgelabelsize_in_menu (x,y)			*/
/*	void	install_arrowlength_in_menu (length)			*/
/*	void	install_arrowangle_in_menu  (angle)			*/
/*	void	install_nodelabel_visibility_in_menu (visible)		*/
/*	void	install_edgelabel_visibility_in_menu (visible)		*/
/*	void	install_grid_in_menu (width)				*/
/*									*/
/*	Der Umweg ueber diese Prozeduren ist aus folgenden Gruenden	*/
/*	sinnvoll :							*/
/*	- Modularisierung						*/
/*	- Items im Menue werden ueber eine Konstante vom Typ		*/
/*	  User_action angesprochen. Deshalb ist i.a. eine "Umrechnung"	*/
/*	  erforderlich.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	void		set_menu_selection (action)			*/
/*									*/
/*	Markiert den zu action gehoerenden Eintrag; der bisher		*/
/*	markierte Eintrag in dieser Kategorie (-> selected) wird	*/
/*	geloescht.							*/
/*	Diese Prozedur wird auch von den obigen install_... -		*/
/*	Prozeduren verwendet.						*/
/*									*/
/*	selected hat folgende Gestalt :					*/
/*									*/
/*	typedef	struct	{						*/
/*		User_action	main_action;				*/
/*		User_action	node_edge_interface;			*/
/*		User_action	nodelabel_placement;			*/
/*		User_action	nodelabel_visibility;			*/
/*		User_action	all_nodelabel_visibility;		*/
/*		User_action	nodesize_scaling;			*/
/*		User_action	edgelabelsize_scaling;			*/
/*		User_action	edgelabel_visibility;			*/
/*		User_action	all_edgelabel_visibility;		*/
/*		User_action	arrowlength;				*/
/*		User_action	arrowangle;				*/
/*		User_action	gragra_type;				*/
/*		User_action	grid;					*/
/*	}								*/
/*		Selected;						*/
/*									*/
/*	Selected	selected;					*/
/*									*/
/*	Dabei ist jedes Feld eine Kategorie, in der immer nur ein	*/
/*	Punkt markiert werden kann (i.a. gleichbedeutend mit		*/
/*	Submenues).							*/
/*									*/
/************************************************************************/


void			install_node_edge_interface_in_menu (Node_edge_interface nei)
{
	switch (nei) {
	    case NO_NODE_EDGE_INTERFACE :
		set_menu_selection (NEI_NO_NODE_EDGE_INTERFACE);
		break;
	    case TO_BORDER_OF_BOUNDING_BOX :
		set_menu_selection (NEI_TO_BORDER_OF_BOUNDING_BOX);
		break;
	    case TO_CORNER_OF_BOUNDING_BOX :
		set_menu_selection (NEI_TO_CORNER_OF_BOUNDING_BOX);
		break;
	    case CLIPPED_TO_MIDDLE_OF_NODE :
		set_menu_selection (NEI_CLIPPED_TO_MIDDLE_OF_NODE);
		break;
	    case SPECIAL_NODE_EDGE_INTERFACE :
		set_menu_selection (NEI_SPECIAL);
		break;
	    default: break;
	}
}



void			install_nodelabel_placement_in_menu (Nodelabel_placement nlp)
{
	switch (nlp) {
	    case NODELABEL_MIDDLE :
		set_menu_selection (NLP_MIDDLE);
		break;
	    case NODELABEL_UPPERLEFT :
		set_menu_selection (NLP_UPPERLEFT);
		break;
	    case NODELABEL_UPPERRIGHT :
		set_menu_selection (NLP_UPPERRIGHT);
		break;
	    case NODELABEL_LOWERLEFT :
		set_menu_selection (NLP_LOWERLEFT);
		break;
	    case NODELABEL_LOWERRIGHT :
		set_menu_selection (NLP_LOWERRIGHT);
		break;
	    default: break;
	}
}


void	install_nodesize_in_menu (int x, int y)
{
	if (x == y) switch (x) {
	     case 16 :
		set_menu_selection (SCALE_NODESIZE_16_16);
		break;
	     case 32 :
		set_menu_selection (SCALE_NODESIZE_32_32);
		break;
	     case 64 :
		set_menu_selection (SCALE_NODESIZE_64_64);
		break;
	     case 128 :
		set_menu_selection (SCALE_NODESIZE_128_128);
		break;
	     default :
		set_menu_selection (SCALE_NODESIZE_AS_SELECTED);
		break;
	} else
		set_menu_selection (SCALE_NODESIZE_AS_SELECTED);
}


void	install_edgelabelsize_in_menu (int width, int height)
{
	if (width == 64 && height == 64)
		set_menu_selection (SCALE_EDGELABELSIZE_64_64);
	else if (width == 256 && height == 64)
		set_menu_selection (SCALE_EDGELABELSIZE_256_64);
	else if (width == (MAXINT-1) && height == (MAXINT-1))
		set_menu_selection (SCALE_EDGELABELSIZE_UNCONSTRAINED);
}


void	install_arrowlength_in_menu (int length)
{
	if (length == 8)
		set_menu_selection (SCALE_ARROWLENGTH_8);
	else if (length == 12)
		set_menu_selection (SCALE_ARROWLENGTH_12);
	else if (length == 16)
		set_menu_selection (SCALE_ARROWLENGTH_16);
	else
		set_menu_selection (SCALE_ARROWLENGTH_AS_SELECTED);
}


void	install_arrowangle_in_menu (float angle)
{
	if (angle == deg_to_rad (30))
		set_menu_selection (SCALE_ARROWANGLE_30);
	else if (angle == deg_to_rad (45))
		set_menu_selection (SCALE_ARROWANGLE_45);
	else if (angle == deg_to_rad (60))
		set_menu_selection (SCALE_ARROWANGLE_60);
	else
		set_menu_selection (SCALE_ARROWANGLE_AS_SELECTED);
}


void	install_nodelabel_visibility_in_menu (int visible)
{
	if (visible)
		set_menu_selection (SET_NODELABEL_VISIBLE);
	else
		set_menu_selection (SET_NODELABEL_INVISIBLE);
}


void	install_edgelabel_visibility_in_menu (int visible)
{
	if (visible)
		set_menu_selection (SET_EDGELABEL_VISIBLE);
	else
		set_menu_selection (SET_EDGELABEL_INVISIBLE);
}


void	install_grid_in_menu (int width)
{
	if (width == 0)
		set_menu_selection (SET_GRID_OFF);
	else if (width == 8)
		set_menu_selection (SET_GRID_8_8);
	else if (width == 16)
		set_menu_selection (SET_GRID_16_16);
	else if (width == 32)
		set_menu_selection (SET_GRID_32_32);
	else if (width == 64)
		set_menu_selection (SET_GRID_64_64);
	else if (width == 128)
		set_menu_selection (SET_GRID_128_128);
}


void	install_constrained_in_menu (int constrain)
{
	if (constrain)
		set_menu_string (TOGGLE_CONSTRAINED, "Unconstrained      F2");
	else
		set_menu_string (TOGGLE_CONSTRAINED, "Constrained        F2");
}



void		install_group_labelling_operation_in_menu (Node_or_edge goes_to)
{
	if (goes_to == NODE)
		set_menu_string (TOGGLE_GROUP_LABELLING_OPERATION, "Group lables edge  F3");
	else
		set_menu_string (TOGGLE_GROUP_LABELLING_OPERATION, "Group lables node  F3");
}


void	install_directedness_in_menu (int directed)
{
	if (directed) {
		set_menu_selection (CREATE_DIRECTED_GRAPH);
		set_menu_selection (CREATE_DIRECTED_PRODUCTION);
		set_menu_selection (CREATE_DIRECTED_EMBEDDING);
	} else {
		set_menu_selection (CREATE_UNDIRECTED_GRAPH);
		set_menu_selection (CREATE_UNDIRECTED_PRODUCTION);
		set_menu_selection (CREATE_UNDIRECTED_EMBEDDING);
	}
}



void		install_gragra_type_in_menu (Gragra_type gragra_type)
{
	switch (gragra_type) {
	    case NCE_1 :
		set_menu_selection (SET_GRAGRA_TYPE_NCE_1);
		break;
	    case ENCE_1 :
		set_menu_selection (SET_GRAGRA_TYPE_ENCE_1);
		break;
	    case NLC :
		set_menu_selection (SET_GRAGRA_TYPE_NLC);
		break;
	    case BNLC :
		set_menu_selection (SET_GRAGRA_TYPE_BNLC);
		break;
	    default: break;
	}
}




void		set_menu_selection (User_action action)
{
	switch (action) {

	    case CREATE_MODE        :
	    case EDIT_MODE      :
		unmark_working_area_menu (selected.main_action);
		selected.main_action = action;
		mark_working_area_menu   (selected.main_action);
		break;

	    case CREATE_DIRECTED_GRAPH :
	    case CREATE_UNDIRECTED_GRAPH :
		set_menu_default_item (create_graph_submenu, action);
		break;
	    
	    case CREATE_DIRECTED_PRODUCTION :
	    case CREATE_UNDIRECTED_PRODUCTION :
		set_menu_default_item (create_production_submenu, action);
		break;
	    
	    case CREATE_DIRECTED_EMBEDDING :
	    case CREATE_UNDIRECTED_EMBEDDING :
		set_menu_default_item (create_embedding_submenu, action);
		break;
	    
	    case NEI_NO_NODE_EDGE_INTERFACE    :
	    case NEI_TO_BORDER_OF_BOUNDING_BOX :
	    case NEI_TO_CORNER_OF_BOUNDING_BOX :
	    case NEI_CLIPPED_TO_MIDDLE_OF_NODE :
	    case NEI_SPECIAL                   :
		unmark_working_area_menu (selected.node_edge_interface);
		selected.node_edge_interface = action;
		mark_working_area_menu   (selected.node_edge_interface);
		set_menu_default_item (node_edge_interface_menu, action);
		break;
	
	    case NLP_MIDDLE     :
	    case NLP_UPPERLEFT  :
	    case NLP_UPPERRIGHT :
	    case NLP_LOWERLEFT  :
	    case NLP_LOWERRIGHT :
	    	unmark_working_area_menu (selected.nodelabel_placement);
		selected.nodelabel_placement = action;
		mark_working_area_menu   (selected.nodelabel_placement);
		set_menu_default_item (nodelabel_placement_menu, action);
		break;
	
	    case SET_NODELABEL_VISIBLE   :
	    case SET_NODELABEL_INVISIBLE :
		unmark_working_area_menu (selected.nodelabel_visibility);
		selected.nodelabel_visibility = action;
		mark_working_area_menu   (selected.nodelabel_visibility);
		set_menu_default_item (nodelabel_visibility_menu, action);
		break;
	
	    case SCALE_NODESIZE_16_16       :
	    case SCALE_NODESIZE_32_32       :
	    case SCALE_NODESIZE_64_64       :
	    case SCALE_NODESIZE_128_128     :
	    case SCALE_NODESIZE_AS_SELECTED :
	    	unmark_working_area_menu (selected.nodesize_scaling);
		selected.nodesize_scaling = action;
		mark_working_area_menu   (selected.nodesize_scaling);
		set_menu_default_item (nodesize_scaling_menu, action);
		break;
	
	    case SET_EDGELABEL_VISIBLE   :
	    case SET_EDGELABEL_INVISIBLE :
		unmark_working_area_menu (selected.edgelabel_visibility);
		selected.edgelabel_visibility = action;
		mark_working_area_menu   (selected.edgelabel_visibility);
		set_menu_default_item (edgelabel_visibility_menu, action);
		break;
	
	    case SCALE_EDGELABELSIZE_64_64         :
	    case SCALE_EDGELABELSIZE_256_64        :
	    case SCALE_EDGELABELSIZE_UNCONSTRAINED :
	    	unmark_working_area_menu (selected.edgelabelsize_scaling);
		selected.edgelabelsize_scaling = action;
		mark_working_area_menu   (selected.edgelabelsize_scaling);
		set_menu_default_item (edgelabelsize_menu, action);
		break;
	
	    case SCALE_ARROWLENGTH_8           :
	    case SCALE_ARROWLENGTH_12          :
	    case SCALE_ARROWLENGTH_16          :
	    case SCALE_ARROWLENGTH_AS_SELECTED :
		unmark_working_area_menu (selected.arrowlength);
		selected.arrowlength = action;
		mark_working_area_menu   (selected.arrowlength);
		set_menu_default_item (arrowlength_menu, action);
		break;
	
	    case SCALE_ARROWANGLE_30          :
	    case SCALE_ARROWANGLE_45          :
	    case SCALE_ARROWANGLE_60          :
	    case SCALE_ARROWANGLE_AS_SELECTED :
		unmark_working_area_menu (selected.arrowangle);
		selected.arrowangle = action;
		mark_working_area_menu   (selected.arrowangle);
		set_menu_default_item (arrowangle_menu, action);
		break;
	
	    case SET_GRAGRA_TYPE_NCE_1       :
	    case SET_GRAGRA_TYPE_ENCE_1      :
	    case SET_GRAGRA_TYPE_NLC         :
	    case SET_GRAGRA_TYPE_BNLC        :
		unmark_working_area_menu (selected.gragra_type);
		selected.gragra_type = action;
		mark_working_area_menu   (selected.gragra_type);
		break;
	
	    case SET_GRID_8_8                :
	    case SET_GRID_16_16              :
	    case SET_GRID_32_32              :
	    case SET_GRID_64_64              :
	    case SET_GRID_128_128            :
	    case SET_GRID_OFF                :
		unmark_working_area_menu (selected.grid);
		selected.grid = action;
		mark_working_area_menu   (selected.grid);
		break;
	
	    case DELETE_ALL                  : /* werden alle nicht markiert */
	    case EDIT_SELECTION              :
	    case CREATE_BUFFER               :
	    case EDIT_GRAPH_OF_SELECTION     :
	    case EDIT_GRAGRA                 :
	    case DELETE_SELECTION            :
	    case PUT_SELECTION               :
	    case GET_SELECTION               :
	    case SET_ALL_NODELABEL_VISIBLE   :
	    case SET_ALL_NODELABEL_INVISIBLE :
	    case SET_ALL_EDGELABEL_VISIBLE   :
	    case SET_ALL_EDGELABEL_INVISIBLE :
	    case REDRAW_ALL                  :
	    case SAVE_STATE                  :
	    case EXPAND_WORKING_AREA         :
	    case SHRINK_WORKING_AREA         :
	    case CREATE_PRODUCTION           :
	    case COMPILE_ALL_PRODUCTIONS     :
	    case SET_CURRENT_PRODUCTION      :
	    case APPLY_CURRENT_PRODUCTION    :
	    case TOGGLE_CONSTRAINED          :
	    case TOGGLE_GROUP_LABELLING_OPERATION :
	    default :
		break;
	}
}
/************************************************************************/
/*									*/
/*			HILFSFUNKTIONEN					*/
/*									*/
/************************************************************************/
/*									*/
/*	Allgemeines :							*/
/*									*/
/*	Die folgenden Prozeduren suchen in Menues nach einen Item mit	*/
/*	MENU_CLIENT_DATA = action. Ist action mehrfach vorhanden (z.B.	*/
/*	in einem Pullright - Item und in einem Item des Submenues), so	*/
/*	wird die erste gefundene markiert !				*/
/*									*/
/*======================================================================*/
/*									*/
/*	static	void	mark_working_area_menu     (action)		*/
/*	static	void	unmark_working_area_menu   (action)		*/
/*									*/
/*	Markiert / Unmarkiert den mit action verbundenen Eintrag im	*/
/*	main_menu bzw. einer seiner Submenus.				*/
/*	Markiert wird mit dem Attribut MENUE_BOXED.			*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	activate_menu_item   (action)				*/
/*	void	inactivate_menu_item (action)				*/
/*									*/
/*	Aktiviert / deaktiviert das mit action verbundene Item mittels	*/
/*	MENUE_INACTIVE. Ein inaktives Item kann (und soll) vom Benutzer	*/
/*	nicht angewaehlt werden !					*/
/*									*/
/*======================================================================*/
/*									*/
/*	static	void	set_menu_default_item (menu, action)		*/
/*									*/
/*	Setze MENU_DEFAULT_ITEM im angegebenen Menue.			*/
/*	(Waehlt der Beutzer ein Pullright - Item an, ohne das		*/
/*	dazugehoerige Submenue aufzuklappen, so wirkt das wie Auswahl	*/
/*	von MENU_DEFAULT_ITEM.)						*/
/*	Im Gegensatz zu den beiden obigen Prozeduren wirkt sich diese	*/
/*	auf ein bestimmtes Menue aus !					*/
/*									*/
/************************************************************************/



static	void	mark_working_area_menu (User_action action)
{
	Menu_item	menu_item;
	
	if (action != NO_ACTION) {
		menu_item = menu_find (main_menu, MENU_CLIENT_DATA, action, 0);
		if (menu_item != (Menu_item)NULL)
			xv_set (menu_item, 0);
/* UMARBEITUNG noetig MH conversion */
	}
}



static	void	unmark_working_area_menu (User_action action)
{
	Menu_item	menu_item;

	if (action != NO_ACTION) {
		menu_item = menu_find (main_menu, MENU_CLIENT_DATA, action, 0);
		if (menu_item != (Menu_item)NULL)
			xv_set (menu_item, 0);
	}
}



void		activate_menu_item (User_action action)
{
	Menu_item	menu_item;
	
	if (action != NO_ACTION) {
		menu_item = menu_find (main_menu, MENU_CLIENT_DATA, action, 0);
		if (menu_item != (Menu_item)NULL)
			xv_set (menu_item, MENU_INACTIVE, FALSE, 0);
	}
}


void		inactivate_menu_item (User_action action)
{
	Menu_item	menu_item;
	
	if (action != NO_ACTION) {
		menu_item = menu_find (main_menu, MENU_CLIENT_DATA, action, 0);
		if (menu_item != (Menu_item)NULL)
			xv_set (menu_item, MENU_INACTIVE, TRUE, 0);
	}
}



static	void	set_menu_default_item (Menu menu, User_action action)
{
	Menu_item	menu_item;
	
	if (action != NO_ACTION) {
		menu_item = menu_find (menu, MENU_CLIENT_DATA, action, 0);
		if (menu_item != (Menu_item)NULL)
			xv_set (menu, MENU_DEFAULT_ITEM, menu_item, 0);
	}
}


void		set_menu_string (User_action action, char *string)
{
	Menu_item	menu_item;
	
	if (action != NO_ACTION) {
		menu_item = menu_find (main_menu, MENU_CLIENT_DATA, action, 0);
		if (menu_item != (Menu_item)NULL)
			xv_set (menu_item, MENU_STRING, string, 0);
	}
}


#ifdef OUTDATED
Pixrect	*menu_create_separator (char	*string)
{
/* Added MH conversion */
	extern	struct	pr_size	pf_textwidth ();
	struct	pr_size	size;
	int	len;
	Pixrect	*pr;
	Pixfont	*font;
	
	font = (Pixfont *)xv_get (main_menu, XV_FONT);

#ifdef XVIEW_COMMENT
     XView CONVERSION - Compatibility attr, use XV_FONT instead  Sect 3.1
#endif
	
	/* Determine the length of string in Pixels */
	size = pf_textwidth (strlen (string), font, string);
	size.x += (int)xv_get (main_menu, XV_RIGHT_MARGIN);
	size.x += (int)xv_get (main_menu, XV_LEFT_MARGIN);
	size.x += 2*(int)xv_get (main_menu, XV_MARGIN);
	size.x += 16; /* guess pullright */
	
	/* Now create a PixRect of the appropriate size */
	pr = mem_create (size.x, size.y, 1);
	
	/* Draw a line */
	pr_vector (pr,  0, size.y/2,  size.x, size.y/2,  PIX_SET, 1);
	
	return pr;
}
#endif

/************************************************************************/
/*									*/
/*		Benutzerspezifische Menues Anfuegen			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	add_to_extra_menu (string, proc)			*/
/*	void	add_to_user_menu  (string, proc)			*/
/*									*/
/*	These procedures are used to add additional procedures to	*/
/*	the menue. These procedures are called as menu action procs;	*/
/*	their parameters are therefore a menu and a menu item.		*/
/*	The extra menu is reserved for common utilities, whereas user	*/
/*	is for the user's own algorithms.				*/
/*									*/
/************************************************************************/



Menu	graphed_create_pin_menu (char *name)
{
	Menu	menu;

	menu = (Menu)xv_create(XV_NULL, MENU,
		MENU_CLIENT_DATA,	NULL,
		MENU_TITLE_ITEM, 	name,
		MENU_GEN_PIN_WINDOW,	base_frame, name,
		XV_FONT, xv_find (base_frame, FONT,
			FONT_FAMILY, FONT_FAMILY_DEFAULT_FIXEDWIDTH,
			NULL),
		NULL);

	return menu;
}


Menu	graphed_create_submenu (void)
{
	Menu	menu;

	menu = (Menu)xv_create(XV_NULL, MENU,
		MENU_CLIENT_DATA, NULL,
		XV_FONT, xv_find (base_frame, FONT,
			FONT_FAMILY, FONT_FAMILY_DEFAULT_FIXEDWIDTH,
			NULL),
		NULL);

	return menu;
}


void	add_graphed_string_entry_to_menu (Menu menu, char *string, User_action client_data)
{
	add_string_entry_to_menu (menu, string, client_data, graphed_menu_action_proc);
}


void	add_string_entry_to_menu (Menu menu, char *string, User_action client_data, GraphEd_Menu_Proc proc)
{
	if (!strcmp(string,"") ||(string[0] == '-')) {
		xv_set (menu,
			MENU_EMPTY_SEPARATOR,
		0);
	} else {
		xv_set (menu,
			MENU_APPEND_ITEM,
			menu_create_item (
				MENU_STRING,      string,
				MENU_CLIENT_DATA, client_data,
				MENU_ACTION_PROC, proc,
				0),
			0);
	}
}


void	add_graphed_image_entry_to_menu (Menu menu, Server_image image, User_action client_data)
{
	add_image_entry_to_menu (menu, image, client_data, graphed_menu_action_proc);
}


void	add_image_entry_to_menu (Menu menu, Server_image image, User_action client_data, GraphEd_Menu_Proc proc)
{
	xv_set (menu,
		MENU_APPEND_ITEM,
		menu_create_item (
			MENU_CLIENT_DATA, client_data,
			MENU_IMAGE,       image,
			MENU_ACTION_PROC, proc,
			0),
		0);
}


void	add_entry_to_menu (Menu menu, char *string, GraphEd_Menu_Proc proc)
{
	add_string_entry_to_menu (menu, string, NO_ACTION, proc);
}


void	add_menu_to_menu (Menu menu, char *string, Menu add_menu)
{
	xv_set (add_menu,
		XV_FONT, xv_find (base_frame, FONT,
			FONT_FAMILY, FONT_FAMILY_DEFAULT_FIXEDWIDTH,
			NULL),
		NULL);
	xv_set (menu, MENU_APPEND_ITEM,
		menu_create_item(
			MENU_STRING,      string,
			MENU_CLIENT_DATA, NO_ACTION,
			MENU_PULLRIGHT,   add_menu,
			0),
	0);

}


/*				*/
/*	Add item to menu	*/
/*				*/

void	add_to_file_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (file_submenu, string, proc);
}


void	add_to_create_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (create_submenu, string, proc);
}


void	add_to_edit_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (create_submenu, string, proc);
}


void	add_to_gragra_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (gragra_submenu, string, proc);
}


void	add_to_misc_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (misc_submenu, string, proc);
}


void	add_to_tools_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (tools_submenu, string, proc);
}


void	add_to_extra_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_to_tools_menu (string, proc);
}


void	add_to_layout_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (layout_submenu, string, proc);
}


void	add_to_goodies_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (goodies_submenu, string, proc);
}


void	add_to_user_menu (char *string, GraphEd_Menu_Proc proc)
{
	add_entry_to_menu (user_submenu, string, proc);
}


/*				*/
/*	Add menu to menu	*/
/*				*/


void	add_menu_to_file_menu (char *string, Menu menu)
{
	add_menu_to_menu (file_submenu, string, menu);
}


void	add_menu_to_create_menu (char *string, Menu menu)
{
	add_menu_to_menu (create_submenu, string, menu);
}


void	add_menu_to_edit_menu (char *string, Menu menu)
{
	add_menu_to_menu (edit_submenu, string, menu);
}


void	add_menu_to_gragra_menu (char *string, Menu menu)
{
	add_menu_to_menu (gragra_submenu, string, menu);
}


void	add_menu_to_misc_menu (char *string, Menu menu)
{
	add_menu_to_menu (misc_submenu, string, menu);
}


void	add_menu_to_tools_menu (char *string, Menu menu)
{
	add_menu_to_menu (tools_submenu, string, menu);
}


void	add_menu_to_layout_menu (char *string, Menu menu)
{
	add_menu_to_menu (layout_submenu, string, menu);
}


void	add_menu_to_goodies_menu (char *string, Menu menu)
{
	add_menu_to_menu (goodies_submenu, string, menu);
}


void	add_menu_to_user_menu (char *string, Menu menu)
{
	add_menu_to_menu (user_submenu, string, menu);
}



void	rebuild_gragra_productions_submenu (void)
{
	int	i, buffer;
	Graph	graph;
	Slist	productions, p;
	Menu_item	item;

#define ATTR_STRING ATTR_DATA
#define attr_string(x) (attr_data_of_type((x), char*))


	/* Create a list of all (labels of) productions */
	productions = empty_slist;
	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) {
	    for_all_graphs (buffer, graph) {
		if (graph->is_production && graph->compile_time > 0) {
		     productions = add_to_slist (productions,
			make_attr (ATTR_STRING, graph->firstnode->label.text));
		}
	    } end_for_all_graphs (buffer, graph);
	}


	/* Look for new productions to be inserted */
	for_slist (productions, p) {

		item = (Menu_item)xv_find (gragra_productions_submenu,
				MENUITEM,
				XV_AUTO_CREATE, FALSE,
				MENU_STRING, attr_string(p),
				NULL);

		if (item == (Menu_item)NULL) for (i = (int)xv_get (gragra_productions_submenu, MENU_NITEMS); i>3; i--) {
			char	*string;

			item = (Menu_item)xv_get (gragra_productions_submenu, MENU_NTH_ITEM, i);
			string = (char *)xv_get (item, MENU_STRING);

			if (strcmp (string, attr_string(p)) > 0) {
				continue;
			} else {
				xv_set (gragra_productions_submenu,
					MENU_INSERT_ITEM, item, menu_create_item (
						MENU_CLIENT_DATA, NO_ACTION,
						MENU_STRING,      strsave (attr_string (p)),
						MENU_ACTION_PROC, menu_apply_production,
						NULL),
					NULL);
				break;
			}
		}
	} end_for_slist (productions, p);


	/* Look for productions to be deleted */
	for (i = (int)xv_get (gragra_productions_submenu, MENU_NITEMS); i>4; i--) {

		char	*string;
		int	found;

		item = (Menu_item)xv_get (gragra_productions_submenu, MENU_NTH_ITEM, i);
		string = (char *)xv_get (item, MENU_STRING);

		found = FALSE;
		for_slist (productions, p) {
			if (attr_string(p) != NULL && string != NULL && !strcmp (attr_string(p), string)) {
				found = true;
				break;
			}			
		} end_for_slist (productions, p);

		if (!found) {
			xv_set (gragra_productions_submenu, MENU_REMOVE_ITEM, item, NULL);
			xv_destroy (item);
		}
	}

#if FALSE
	if (menu_needs_rebuild) {

		/* Delete all old entries in the menu */
		for (i = (int)xv_get (gragra_productions_submenu, MENU_NITEMS); i>4; i--) {
			item = (Menu_item)xv_get (gragra_productions_submenu, MENU_NTH_ITEM, i, NULL);
/*
*/
		}
		/*
		xv_set (gragra_productions_submenu, MENU_GEN_PIN_WINDOW, base_frame, "productions", NULL);
		*/

		/* Create the menu, alphabetically sorted */
		while (productions != empty_slist) {

			Slist smallest;

			smallest = productions;
			for_slist (productions, p) {
				if (strcmp (attr_string(p), attr_string(smallest)) < 0) {
					smallest = p;
				}
			} end_for_slist (productions, p);

			add_entry_to_menu (gragra_productions_submenu,
				strsave (attr_string (smallest)),
				menu_apply_production);
			productions = subtract_immediately_from_slist (
				productions, smallest);
		}

	}
#endif

	free_slist (productions);
}
