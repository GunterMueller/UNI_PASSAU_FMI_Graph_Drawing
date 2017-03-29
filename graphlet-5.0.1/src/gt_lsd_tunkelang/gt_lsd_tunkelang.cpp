/* This software is distributed under the Lesser General Public License */
// 
// L      S      D
// Leda & Sgraph Do it
//
//
// Author: Dirk Heider
// email: heider@fmi.uni-passau.de
// Changes and adjustment to graphlet: Walter Bachl
// Additional Changes: Michael Himsolt
//

///////////////////////////////////////////////////////////
//
// Main Programm to call the algorithms which are
// implemented in sgraph.
//
///////////////////////////////////////////////////////////

#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl.h>
#include <gt_tcl/Tcl_Algorithm.h>
#include <gt_tcl/Graphscript.h>
#include <gt_base/NEI.h>

// LSD standard includes
#include "lsd/lsdstd.h"
#include "gt_lsd.h"
#include "gt_lsd_tunkelang.h"
#include "tunkelang_export.h"



//////////////////////////////////////////
//
// Tunkelang Layout
//
//////////////////////////////////////////


//
// Algortihm
//

GT_Tunkelang::GT_Tunkelang (const string& name) : GT_Algorithm (name)
{
    the_edgelength = 0;
    the_cut_value = 2;
    the_scan_corners = 1;
    the_quality = 4;
    the_rec_depth = 1000;
    the_randomize = 1;
}

GT_Tunkelang::~GT_Tunkelang ()
{
}

int GT_Tunkelang::run (GT_Graph& g)
{
    // check on empty graph
    if (g.leda().number_of_nodes() < 1 ) {
	return GT_OK;
    }
    
    //graph *leda = g.attached();
    LSD   lsd;
    bool is_undirected = g.leda().is_undirected ();
    g.leda().make_directed();
	
    lsd.gt_graph (&g);
	
    lsd.init_call();
    call_tunkelang(lsd.proc_info(), edgelength(), quality(), recursion_depth(),
	randomize(), crossings(), scan_corners());
    lsd.clean_up();

    remove_all_bends (g);
    adjust_coordinates (g, 10, 10);
    reset_NEIs (g);
	
    if (is_undirected ) {
	g.leda().make_undirected();
    }

    return GT_OK;
}

int GT_Tunkelang::check (GT_Graph& /* g */, string& message)
{
    message = "";
    return GT_OK;
}

//
// Tcl Wrapper
//

GT_Tcl_Tunkelang::GT_Tcl_Tunkelang (const string name) : 
	GT_Tcl_Algorithm<GT_Tunkelang> (name)
{
}

GT_Tcl_Tunkelang::~GT_Tcl_Tunkelang ()
{
}

int GT_Tcl_Tunkelang::parse (GT_Tcl_info& info, int& index,
    GT_Tcl_Graph* /* g */)
{
    int code = TCL_OK;

    if(info.argv(index)[0] == '-') {

        if (!strcmp(info.argv(index),"-edgelength")) {
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_edgelength);
	} else if (!strcmp(info.argv(index),"-quality")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_quality);
	} else if (!strcmp(info.argv(index),"-recursion_depth")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_rec_depth);
	} else if (!strcmp(info.argv(index),"-scan_corners")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_scan_corners);
	} else if (!strcmp(info.argv(index),"-randomize")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_randomize);
	} else if (!strcmp(info.argv(index),"-crossings")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_cut_value);
	}
    }

    index++;
    return code;	
}


//////////////////////////////////////////////
//
// Main Initialization function for interface
//
// Tcl/Tk naming conventions
//
//////////////////////////////////////////////

int Gt_lsd_tunkelang_Init (Tcl_Interp* interp)
{
    int code = 0;

    GT_Tcl_Algorithm_Command* layout_tunkelang =
	new GT_Tcl_Tunkelang ("layout_tunkelang");
    code = layout_tunkelang->install(interp);
    if (code == TCL_ERROR) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_lsd_tunkelang", version);
    if (code != TCL_OK) {
	return code;
    }

    return code;
}
