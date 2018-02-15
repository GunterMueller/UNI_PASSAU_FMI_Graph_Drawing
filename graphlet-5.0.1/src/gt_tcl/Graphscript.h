/* This software is distributed under the Lesser General Public License */
#ifndef GT_GRAPHSCRIPT_H
#define GT_GRAPHSCRIPT_H

//
// Graphscript.h
//
// This file defines the class GT_Graphscript.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Graphscript.h,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:45:54 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#if defined(WIN32)
#define WIN32_LEAN_AND_MEAN	    
#define WIN32_EXTRA_LEAN	   
#include <windows.h>
#endif


class GT_Graphscript {

    GT_BASE_CLASS (GT_Graphscript);

    Tcl_Interp* the_interp;

    int* the_option_debug;
    int* the_option_debug_graphics;
    int* the_option_integer_coordinates;

    double* the_option_marker_width;
    char** the_option_marker_color;

    int* the_option_autonumber_nodes;
    int* the_option_autonumber_nodes_by_id;
    int* the_option_autonumber_nodes_by_degree;
    int* the_option_autonumber_edges;
    int* the_option_autonumber_edges_by_id;

    int* the_option_adjust_size_to_label;
    double* the_option_adjust_size_to_label_gap_x;
    double* the_option_adjust_size_to_label_gap_y;
    double* the_option_adjust_size_to_label_minimum;

    char** the_option_default_font;
    int*   the_option_default_font_size;
    char** the_option_default_font_style;

    static bool the_tcl_only;

public:

    static bool tcl_only ();

    GT_Graphscript (Tcl_Interp* interp, bool loadable_module = false);
    virtual ~GT_Graphscript ();

    //
    // Accessories
    //

    inline Tcl_Interp* interp ();
    inline const Tcl_Interp* interp () const;

    //
    // Initailize Graphscript. This cannot be done from the
    // constructor because virtual methods are involved.
    //

    virtual int init ();
    virtual int commands_init ();
    // Tcl's application_init
    virtual int application_init (Tcl_Interp *interp);
    
    //
    // gt_main comes in a UNIX and a WINDOWS version
    //

    static int gt_main  (int argc, char **argv,
	Tcl_AppInitProc* application_init);
    
#if defined(_WINDOWS) && defined(WIN32)
    static int gt_main (
	HINSTANCE hInstance, HINSTANCE hPrevInstance,
	LPSTR lpszCmdLine,
	int nCmdShow,
	Tcl_AppInitProc* application_init);    
#endif

    virtual int options_init ();
    virtual int link_option (const char* name, int* int_ptr,
	int default_value = 0);
    virtual int link_option (const char* name, double* double_ptr,
	bool default_value = 0);
    virtual int link_option (const char* name, char** char_ptr,
	char** default_value = 0);
    
    virtual bool option_debug () const;
    virtual bool option_debug_graphics () const;
    virtual bool option_integer_coordinates () const;
    virtual double option_marker_width () const;
    virtual const char* option_marker_color () const;

    virtual bool option_autonumber_nodes () const;
    virtual bool option_autonumber_nodes_by_id () const;
    virtual bool option_autonumber_nodes_by_degree () const;
    virtual bool option_autonumber_edges () const;
    virtual bool option_autonumber_edges_by_id () const;

    virtual bool option_adjust_size_to_label () const;
    virtual double option_adjust_size_to_label_gap_x () const;
    virtual double option_adjust_size_to_label_gap_y () const;
    virtual double option_adjust_size_to_label_minimum () const;

    virtual const char* option_default_font () const;
    virtual int option_default_font_size () const;
    virtual const char* option_default_font_style () const;

    virtual bool tcl_eval (const string& cmd, bool is_graphics = true);

};


inline Tcl_Interp* GT_Graphscript::interp ()
{
    return the_interp;
}


inline const Tcl_Interp* GT_Graphscript::interp () const
{
    return the_interp;
}


#endif
