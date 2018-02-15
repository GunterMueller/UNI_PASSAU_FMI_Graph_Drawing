/* This software is distributed under the Lesser General Public License */
//
// Graphscript.cc
//
// This file implements the class GT_Graphscript.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Graphscript.cpp,v $
// $Author: himsolt $
// $Revision: 1.10 $
// $Date: 1999/03/05 20:45:52 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"
#include <gt_base/GML.h>
#include <gt_base/Graphlet.h>

#include <tk.h>

#include "Graphscript.h"

#include "Tcl_Rotate_Command.h"
#include "Tcl_Scale_Command.h"

//
// Constructors and Destructors
//


GT_Graphscript::GT_Graphscript (Tcl_Interp* interp, bool /* loadable_module */)
{
    the_interp = interp;

    the_option_debug = 0;
    the_option_debug_graphics = 0;
    the_option_integer_coordinates = 0;

    the_option_marker_width = 0;
    the_option_marker_color = 0;

    the_option_autonumber_nodes = 0;
    the_option_autonumber_nodes_by_id = 0;
    the_option_autonumber_nodes_by_degree = 0;
    the_option_autonumber_edges = 0;
    the_option_autonumber_edges_by_id = 0;

    the_option_adjust_size_to_label = 0;
    the_option_adjust_size_to_label_gap_x = 0;
    the_option_adjust_size_to_label_gap_y = 0;
    the_option_adjust_size_to_label_minimum = 0;

    the_option_default_font = 0;
    the_option_default_font_size = 0;
    the_option_default_font = 0;
}

bool GT_Graphscript::the_tcl_only = false;



GT_Graphscript::~GT_Graphscript ()
{
    delete the_option_debug;
    delete the_option_debug_graphics;
    delete the_option_integer_coordinates;

    delete the_option_marker_width;
    delete the_option_marker_color;

    delete the_option_autonumber_nodes;
    delete the_option_autonumber_nodes_by_id;
    delete the_option_autonumber_nodes_by_degree;
    delete the_option_autonumber_edges;
    delete the_option_autonumber_edges_by_id;

    delete the_option_adjust_size_to_label;
    delete the_option_adjust_size_to_label_gap_x;
    delete the_option_adjust_size_to_label_gap_y;
    delete the_option_adjust_size_to_label_minimum;

    delete *the_option_default_font;
    delete the_option_default_font;
    delete the_option_default_font_size;
    delete *the_option_default_font_style;
    delete the_option_default_font_style;
}


//////////////////////////////////////////
//
// int GT_Graphscript::application_init (Tcl_Interp *interp)
//
// we need a global variable here because this is the only way to
// communicate between GT_Graphscript::main and
// GT_Graphscript::application_init
//
//////////////////////////////////////////



int GT_Graphscript::application_init (Tcl_Interp * /* interp */)
{
    //
    // Initialize the Graphlet base
    //
    
    GT::init();

    //
    // Initialize Graphscript
    //
    
    int code = this->init();
    if (code == TCL_ERROR) {
	return code;
    }

    //
    // Add more initializations and commands here
    //
	
    return code;
}



//////////////////////////////////////////
//
// Initialization of Graphscript
//
//////////////////////////////////////////


static int gt_version_command (ClientData clientData,
    Tcl_Interp *interp,
    int argc,
    char **argv)
{
    char version[(2+2+2) + 2 + 1]; // Three numbers, two dots, one '\0'
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    Tcl_SetResult (interp, version, TCL_VOLATILE);
    return TCL_OK;
}


int GT_Graphscript::init ()
{
    int code = TCL_OK;

    //
    // Initialize GT(graphlet_dir)
    //
	
    char* graphlet_dir_env = getenv ("GRAPHLET_DIR");

    char* graphlet_dir_prog = "\n\
set file [info nameofexecutable]\n\
if {$tcl_platform(platform) == \"windows\"} {\n\
set file [file dirname $file]\n\
} else {\n\
set file [file dirname [file dirname $file]]\n\
}\n\
if [file readable [file join $file lib graphscript init.tcl]] {\n\
return $file\n\
} elseif [file readable [file join [file dirname $file] lib graphscript init.tcl]] {\n\
return [file dirname $file]\n\
}";

    string graphlet_dir;
    if (graphlet_dir_env == 0) {
 	code = Tcl_Eval (the_interp, graphlet_dir_prog);
	if (code != TCL_ERROR && strcmp (the_interp->result, "")) {
	    graphlet_dir = the_interp->result;
	} else {
	    graphlet_dir = GT_GRAPHLET_DIR;
	}
    } else {
	graphlet_dir = graphlet_dir_env;
    }


    char* graphlet_dir_value = GT_Tcl::SetVar2 (the_interp,
	"GT", "graphlet_dir", 
	graphlet_dir.c_str(),
	TCL_GLOBAL_ONLY);
    if (graphlet_dir_value == NULL) {
	Tcl_AddErrorInfo (the_interp, "Error initializing GT(graphlet_dir)");
	return TCL_ERROR;
    }

    //
    // Initialize GT(major_version)
    // Initialize GT(minor_version)
    // Initialize GT(mini_version)
    // Initialize GT(release)
    //

    string major_version = GT::format ("%d", GT_MAJOR_VERSION);
    char* major_version_value = GT_Tcl::SetVar2 (the_interp,
	"GT", "major_version", major_version,
	TCL_GLOBAL_ONLY);
    string minor_version = GT::format ("%d", GT_MINOR_VERSION);
    char* minor_version_value = GT_Tcl::SetVar2 (the_interp,
	"GT", "minor_version", minor_version,
	TCL_GLOBAL_ONLY);
    string mini_version = GT::format("%d", GT_MINI_VERSION);
    char* mini_version_value = GT_Tcl::SetVar2 (the_interp,
	"GT", "mini_version", mini_version,
	TCL_GLOBAL_ONLY);
    char* release_value = Tcl_SetVar2 (the_interp,
	"GT", "release", GT_RELEASE,
	TCL_GLOBAL_ONLY);
    char* gt_GTL_major_version_value = GT_Tcl::SetVar2 (the_interp,
	"GT", "GTL_major_version", GTL_MAJOR_VERSION,
	TCL_GLOBAL_ONLY);
    char* gt_GTL_minor_version_value = GT_Tcl::SetVar2 (the_interp,
	"GT", "GTL_minor_version", GTL_MINOR_VERSION,
	TCL_GLOBAL_ONLY);
    char* gt_GTL_mini_version_value = GT_Tcl::SetVar2 (the_interp,
	"GT", "GTL_mini_version", GTL_MINI_VERSION,
	TCL_GLOBAL_ONLY);
    if (major_version_value == NULL ||
	minor_version_value == NULL ||
	mini_version_value == NULL ||
	release_value == NULL ||
	gt_GTL_major_version_value == NULL ||
	gt_GTL_minor_version_value == NULL ||
	gt_GTL_mini_version_value == NULL) {
	return TCL_ERROR;
    }

    Tcl_CreateCommand (the_interp, "gt_version", gt_version_command,
	0,
	(Tcl_CmdDeleteProc *)0);

    //
    // Initialize GT(graphscript) as
    // $GT(graphlet_dir)/lib/graphscript,
    // AND DO IT PLATFORM INDEPENDEND
    //
    // (cant eval because $ in Tcl_Eval crashes)
    //
    
    char* graphscript_dir_path[] = {
	graphlet_dir_value, "lib", "graphscript"
    };
    Tcl_DString graphscript_dir;
    Tcl_DStringInit (&graphscript_dir);
    Tcl_JoinPath (sizeof(graphscript_dir_path) / sizeof(char*),
	graphscript_dir_path,
	&graphscript_dir);
    char* graphscript_dir_value = Tcl_SetVar2 (the_interp,
	"GT", "graphscript_dir",
	Tcl_DStringValue (&graphscript_dir),
	TCL_GLOBAL_ONLY | TCL_LEAVE_ERR_MSG);
    if (graphscript_dir_value == NULL) {
	return TCL_ERROR;
    }
    Tcl_DStringFree (&graphscript_dir);
    
    //
    // Insert graphscript_dir in front of auto_path
    //
    
    code = Tcl_Eval (the_interp,
	"set auto_path [linsert $auto_path 0 $GT(graphscript_dir)]");
    if (code != TCL_OK) {
	return code;
    }

    code = Tcl_Eval (the_interp,
	"source [file join $GT(graphscript_dir) init.tcl]");
    if (code != TCL_OK) {
 	return code;
    }
		
    code = options_init ();
    if (code != TCL_OK) {
	return code;
    }
    
    code = commands_init ();
    if (code != TCL_OK) {
	return code;
    }

    return TCL_OK;
}


//////////////////////////////////////////
//
// Commands initialization
//
//////////////////////////////////////////


int GT_Graphscript::commands_init ()
{
    //
    // install command "graph"
    //
	
    if (GT_Tcl::CreateCommand (
	the_interp,
	"graph", 
	GT_Tcl_Graph::cmd,
	this,
	(Tcl_CmdDeleteProc *)0) == 0) {
		
	return TCL_ERROR;
    }

    //
    // install command "rotate_nodes"
    //

    int code = (new GT_Tcl_Rotate_Command ("::GT_rotate_nodes"))
 	->install(the_interp);
    if(code != TCL_OK)
 	return code;
	    
    //
    // install command "scale_nodes"
    //

    code = (new GT_Tcl_Scale_Command ("::GT_scale_nodes"))
 	->install(the_interp);
    if(code != TCL_OK)
 	return code;
	
    return TCL_OK;
}


//////////////////////////////////////////
//
// Access to Standard options
//
//////////////////////////////////////////


int GT_Graphscript::options_init ()
{
    the_option_debug = new int;
    int code = link_option ("debug", the_option_debug);
    if (code != TCL_OK) {
	return code;
    }
     
    the_option_debug_graphics = new int;
    code = link_option ("debug_graphics", the_option_debug_graphics);
    if (code != TCL_OK) {
	return code;
    }
    
    the_option_integer_coordinates = new int;
    code = link_option ("integer_coordinates", the_option_integer_coordinates);
    if (code != TCL_OK) {
	return code;
    }
    
    the_option_marker_width = new double;
    code = link_option ("marker_width", the_option_marker_width);
    if (code != TCL_OK) {
	return code;
    }

    the_option_marker_color = new char*;
    *the_option_marker_color = 0;
    code = link_option ("marker_color", the_option_marker_color);
    if (code != TCL_OK) {
	return code;
    }

    the_option_autonumber_nodes = new int;
    *the_option_autonumber_nodes = 0;
    code = link_option ("autonumber_nodes", the_option_autonumber_nodes);
    if (code != TCL_OK) {
	return code;
    }

    the_option_autonumber_nodes_by_id = new int;
    *the_option_autonumber_nodes_by_id = 0;
    code = link_option ("autonumber_nodes_by_id",
	the_option_autonumber_nodes_by_id);
    if (code != TCL_OK) {
	return code;
    }

    the_option_autonumber_nodes_by_degree = new int;
    *the_option_autonumber_nodes_by_degree = 0;
    code = link_option ("autonumber_nodes_by_degree",
	the_option_autonumber_nodes_by_degree);
    if (code != TCL_OK) {
	return code;
    }

    the_option_autonumber_edges = new int;
    *the_option_autonumber_edges = 0;
    code = link_option ("autonumber_edges",
	the_option_autonumber_edges);
    if (code != TCL_OK) {
	return code;
    }

    the_option_autonumber_edges_by_id = new int;
    *the_option_autonumber_edges_by_id = 0;
    code = link_option ("autonumber_edges_by_id",
	the_option_autonumber_edges_by_id);
    if (code != TCL_OK) {
	return code;
    }

    the_option_adjust_size_to_label = new int;
    *the_option_adjust_size_to_label = 0;
    code = link_option ("adjust_size_to_label",
	the_option_adjust_size_to_label);
    if (code != TCL_OK) {
	return code;
    }

    the_option_adjust_size_to_label_gap_x = new double;
    *the_option_adjust_size_to_label_gap_x = 0;
    code = link_option ("adjust_size_to_label_gap_x",
	the_option_adjust_size_to_label_gap_x);
    if (code != TCL_OK) {
	return code;
    }

    the_option_adjust_size_to_label_gap_y = new double;
    *the_option_adjust_size_to_label_gap_y = 0;
    code = link_option ("adjust_size_to_label_gap_y",
	the_option_adjust_size_to_label_gap_y);
    if (code != TCL_OK) {
	return code;
    }

    the_option_default_font = new char*;
   *the_option_default_font = 0;
    code = link_option ("system_default_font", the_option_default_font);
    if (code != TCL_OK) {
	return code;
    }

    the_option_default_font_size = new int;
    code = link_option ("system_default_font_size", the_option_default_font_size);
    if (code != TCL_OK) {
	return code;
    }

    the_option_default_font_style = new char*;
    *the_option_default_font_style = GT::strsave ("");
    code = link_option ("system_default_font_style", the_option_default_font_style);
    if (code != TCL_OK) {
	return code;
    }

    code = link_option ("draw_edges_above", &graphlet->the_draw_edges_above);
    if (code != TCL_OK) {
	return code;
    }

    code = link_option ("gml_version", &graphlet->gml->the_version);
    if (code != TCL_OK) {
	return code;
    }

    code = link_option ("draw_edges_above", &graphlet->the_draw_edges_above);
    if (code != TCL_OK) {
	return code;
    }

    return code;
}


//
// Utility procedures which link a variable with an option
//

int GT_Graphscript::link_option (const char* name, int* int_ptr,
    int default_value)
{
    assert (int_ptr != 0);
    
    char* var_value = Tcl_GetVar2 (the_interp, "GT_options", (char*)name,
	TCL_GLOBAL_ONLY);
    if (var_value != 0) {
	int code = Tcl_GetInt (the_interp, var_value, int_ptr);
	if (code != TCL_OK) {
	    return code;
	}
    } else {
	*int_ptr = default_value;
    }
    
    string gt_options_name = GT::format("GT_options(%s)", name);
    return Tcl_LinkVar (the_interp, const_cast<char*>(gt_options_name.c_str()),
	(char*) int_ptr,
	TCL_LINK_INT);
}


int GT_Graphscript::link_option (const char* name,
    double* double_ptr,
    bool default_value)
{
    assert (double_ptr != 0);
    
    char* var_value = Tcl_GetVar2 (the_interp, "GT_options", (char*)name,
	TCL_GLOBAL_ONLY);
    if (var_value != 0) {
	int code = Tcl_GetDouble (the_interp, var_value, double_ptr);
	if (code != TCL_OK) {
	    return code;
	}
    } else {
	*double_ptr = default_value;
    }
    
    string gt_options_name = GT::format("GT_options(%s)", name);
    return Tcl_LinkVar (the_interp, const_cast<char*>(gt_options_name.c_str()),
	(char*) double_ptr,
	TCL_LINK_DOUBLE);
}


int GT_Graphscript::link_option (const char* name,
    char** char_ptr,
    char** default_value)
{
    assert (char_ptr != 0);

    char* var_value = Tcl_GetVar2 (the_interp, "GT_options", (char*)name,
	TCL_GLOBAL_ONLY);
    if (var_value != 0) {
	if (*char_ptr != 0) {
	    free (*char_ptr);
	}
	*char_ptr = (char*) malloc (strlen(var_value)+1);
	strcpy (*char_ptr, var_value);
    } else {
	char_ptr = default_value;
    }
    
    string gt_options_name = GT::format ("GT_options(%s)", name);
    return Tcl_LinkVar (the_interp, const_cast<char*>(gt_options_name.c_str()),
	(char*) char_ptr,
	TCL_LINK_STRING);
}

//////////////////////////////////////////
//
// C++ accessors for some options
//
//////////////////////////////////////////


bool GT_Graphscript::option_debug () const
{
    assert (the_option_debug != 0);
    
    return (*the_option_debug != 0);
}


bool GT_Graphscript::option_debug_graphics () const
{
    assert (the_option_debug_graphics != 0);
    
    return (*the_option_debug_graphics != 0);
}


bool GT_Graphscript::option_integer_coordinates () const
{
    assert (the_option_integer_coordinates != 0);
    
    return (*the_option_integer_coordinates != 0);
}


double GT_Graphscript::option_marker_width () const
{
    assert (the_option_marker_width != 0);
    
    return *the_option_marker_width;
}


const char* GT_Graphscript::option_marker_color () const
{
    assert (the_option_marker_color != 0);
    
    return *the_option_marker_color;
}


bool GT_Graphscript::option_autonumber_nodes () const
{
    assert (the_option_autonumber_nodes != 0);

    return (*the_option_autonumber_nodes != 0);
}


bool GT_Graphscript::option_autonumber_nodes_by_id () const
{
    assert (the_option_autonumber_nodes_by_id != 0);

    return (*the_option_autonumber_nodes_by_id != 0);
}


bool GT_Graphscript::option_autonumber_nodes_by_degree () const
{
    assert (the_option_autonumber_nodes_by_degree != 0);

    return (*the_option_autonumber_nodes_by_degree != 0);
}


bool GT_Graphscript::option_autonumber_edges () const
{
    assert (the_option_autonumber_edges != 0);

    return (*the_option_autonumber_edges != 0);
}


bool GT_Graphscript::option_autonumber_edges_by_id () const
{
    assert (the_option_autonumber_edges_by_id != 0);

    return (*the_option_autonumber_edges_by_id != 0);
}


bool GT_Graphscript::option_adjust_size_to_label () const
{
    assert (the_option_adjust_size_to_label != 0);

    return (*the_option_adjust_size_to_label != 0);
}


double GT_Graphscript::option_adjust_size_to_label_gap_x () const
{
    assert (the_option_adjust_size_to_label_gap_x != 0);

    return *the_option_adjust_size_to_label_gap_x;
}


double GT_Graphscript::option_adjust_size_to_label_gap_y () const
{
    assert (the_option_adjust_size_to_label_gap_y != 0);

    return *the_option_adjust_size_to_label_gap_y;
}


double GT_Graphscript::option_adjust_size_to_label_minimum () const
{
    assert (the_option_adjust_size_to_label_minimum != 0);

    return *the_option_adjust_size_to_label_minimum;
}


const char* GT_Graphscript::option_default_font () const
{
    assert (the_option_default_font != 0);

    return *the_option_default_font;
}


int GT_Graphscript::option_default_font_size () const
{
    return *the_option_default_font_size;
}


const char* GT_Graphscript::option_default_font_style () const
{
    assert (the_option_default_font_style != 0);

    return *the_option_default_font_style;
}

//////////////////////////////////////////
//
// gt_main 
//
// The method gt_main is direct called from main or WinMain.  It
// initialized Tcl/Tk and performs some preliminary parsing of
// command line parameters.
//
//////////////////////////////////////////


//
// UNIX verson of gt_main
//

int GT_Graphscript::gt_main (int argc, char **argv,
    Tcl_AppInitProc *application_init)
{
    if (argc > 1 && GT::streq(argv[1], "-tcl_only")) {
	GT_Graphscript::the_tcl_only = true;
	Tcl_Main (argc-1, argv+1, application_init);
    } else {
	GT_Graphscript::the_tcl_only = false;
	Tk_Main (argc, argv, application_init);
    }

    return 0;
}

//
// Windows Version of gt_main
//

#if defined(_WINDOWS) && defined(WIN32)

#define WIN32_LEAN_AND_MEAN
#define WIN32_EXTRA_LEAN
#include <windows.h>
#undef WIN32_LEAN_AND_MEAN
#include <malloc.h>
#include <locale.h>

/*
 *-------------------------------------------------------------------------
 *
 * setargv --
 *
 *	Parse the Windows command line string into argc/argv.  Done here
 *	because we don't trust the builtin argument parser in crt0.  
 *	Windows applications are responsible for breaking their command
 *	line into arguments.
 *
 *	2N backslashes + quote -> N backslashes + begin quoted string
 *	2N + 1 backslashes + quote -> literal
 *	N backslashes + non-quote -> literal
 *	quote + quote in a quoted string -> single quote
 *	quote + quote not in quoted string -> empty string
 *	quote -> begin quoted string
 *
 * Results:
 *	Fills argcPtr with the number of arguments and argvPtr with the
 *	array of arguments.
 *
 * Side effects:
 *	Memory allocated.
 *
 *--------------------------------------------------------------------------
 */

static void
setargv(int *argcPtr, char ***argvPtr)
{
    char *cmdLine, *p, *arg, *argSpace;
    char **argv;
    int argc, size, inquote, copy, slashes;
    
    cmdLine = GetCommandLine();

    /*
     * Precompute an overly pessimistic guess at the number of arguments
     * in the command line by counting non-space spans.
     */

    size = 2;
    for (p = cmdLine; *p != '\0'; p++) {
	if (isspace(*p)) {
	    size++;
	    while (isspace(*p)) {
		p++;
	    }
	    if (*p == '\0') {
		break;
	    }
	}
    }
    argSpace = (char *) ckalloc((unsigned) (size * sizeof(char *) 
	+ strlen(cmdLine) + 1));
    argv = (char **) argSpace;
    argSpace += size * sizeof(char *);
    size--;

    p = cmdLine;
    for (argc = 0; argc < size; argc++) {
	argv[argc] = arg = argSpace;
	while (isspace(*p)) {
	    p++;
	}
	if (*p == '\0') {
	    break;
	}

	inquote = 0;
	slashes = 0;
	while (1) {
	    copy = 1;
	    while (*p == '\\') {
		slashes++;
		p++;
	    }
	    if (*p == '"') {
		if ((slashes & 1) == 0) {
		    copy = 0;
		    if ((inquote) && (p[1] == '"')) {
			p++;
			copy = 1;
		    } else {
			inquote = !inquote;
		    }
                }
                slashes >>= 1;
            }

            while (slashes) {
		*arg = '\\';
		arg++;
		slashes--;
	    }

	    if ((*p == '\0') || (!inquote && isspace(*p))) {
		break;
	    }
	    if (copy != 0) {
		*arg = *p;
		arg++;
	    }
	    p++;
        }
	*arg = '\0';
	argSpace = arg + 1;
    }
    argv[argc] = NULL;

    *argcPtr = argc;
    *argvPtr = argv;
}



int GT_Graphscript::gt_main (
    HINSTANCE hInstance, HINSTANCE hPrevInstance,
    LPSTR lpszCmdLine,
    int nCmdShow,
    Tcl_AppInitProc *application_init)
{
    char **argv, *p;
    int argc;
    char buffer[MAX_PATH];

    //     Tcl_SetPanicProc(WishPanic);

    /*
     * Set up the default locale to be standard "C" locale so parsing
     * is performed correctly.
     */

    setlocale(LC_ALL, "C");


    /*
     * Increase the application queue size from default value of 8.
     * At the default value, cross application SendMessage of WM_KILLFOCUS
     * will fail because the handler will not be able to do a PostMessage!
     * This is only needed for Windows 3.x, since NT dynamically expands
     * the queue.
     */
    SetMessageQueue(64);

    setargv(&argc, &argv);

    /*
     * Replace argv[0] with full pathname of executable, and forward
     * slashes substituted for backslashes.
     */

    GetModuleFileName(NULL, buffer, sizeof(buffer));
    argv[0] = buffer;
    for (p = buffer; *p != '\0'; p++) {
	if (*p == '\\') {
	    *p = '/';
	}
    }

    if (argc == 1 ||
	(argc > 1 && (argv[1][0] == '-' || argv[1][0] == '/' ||
	    Tcl_StringMatch (argv[1], "*.gml") == 1)))
    {
	char** argv1 = (char**)ckalloc ((argc+1)*sizeof(char*));
	for (int i = argc; i > 1; i--) {
	    argv1[i] = argv[i-1];
	}

	char path[MAX_PATH];
	GetModuleFileName(0, path, MAX_PATH);
	*(strrchr (path, '\\')) = 0;
	char* scriptname = ckalloc (MAX_PATH*sizeof(char));
	strcpy (scriptname, path);
	strcat (scriptname, "\\..\\lib\\graphscript\\startup");
	argv1[0] = argv[0];
	argv1[1] = scriptname;

	Tk_Main(argc+1, argv1, application_init);
    } else {
	Tk_Main(argc, argv, application_init);
    }
    return 1;
}
#endif



//////////////////////////////////////////
//
// tcl_eval (cmd)
//
// Wrapper for Tcl's Tcl_Eval procedure.
//
//////////////////////////////////////////


bool GT_Graphscript::tcl_eval (const string& cmd, bool is_graphics)
{
    int code;
    
    if (cmd.length() == 0) {
	return true;
    }
    
    if (is_graphics && *the_option_debug_graphics) {
	Tcl_SetVar2 (the_interp, "GT", "debug_graphics",
	    const_cast<char*>(cmd.c_str()),
	    TCL_GLOBAL_ONLY | TCL_APPEND_VALUE);
	Tcl_SetVar2 (the_interp, "GT", "debug_graphics", "\n",
	    TCL_GLOBAL_ONLY | TCL_APPEND_VALUE);
    }	
		
    code = Tcl_Eval (the_interp, const_cast<char*>(cmd.c_str()));
    
    if (code != TCL_OK) {
	string message = GT::format ("Command: %s\nCode: %d\nError: %s\n",
	    cmd.c_str(),
	    code,
	    the_interp->result);
	Tcl_SetVar2 (the_interp, "GT", "error_messages",
	    const_cast<char*>(message.c_str()),
	    TCL_GLOBAL_ONLY | TCL_APPEND_VALUE);
    }

    return code != TCL_ERROR;
}


//////////////////////////////////////////
//
// tcl_only
//
//////////////////////////////////////////


bool GT_Graphscript::tcl_only ()
{
    return GT_Graphscript::the_tcl_only;
}
