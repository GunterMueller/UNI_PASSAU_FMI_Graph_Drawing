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
#include <lsd/lsdstd.h>
#include <gt_lsd/gt_lsd.h>
#include "springembedder_kamada_export.h"
#include "gt_lsd_springembedder_kamada.h"

//////////////////////////////////////////
//
// SPRINGEMBEDDER KAMADA
//
//////////////////////////////////////////

GT_SpringKamada::GT_SpringKamada (const string& name) : GT_Algorithm (name)
{
    the_edgelength = 128;
}   

GT_SpringKamada::~GT_SpringKamada ()
{
}

int GT_SpringKamada::run (GT_Graph& g)
{
    // check on empty graph
    if (g.leda().number_of_nodes() < 1 ) {
	return GT_OK;
    }
    
 	
    // find multi-edges
    list<edge> edges;
    hide_selfloops (g.attached(), edges);
    hide_multiedges (g.attached(), edges);

    //graph leda = g.leda();
    LSD   lsd;
    lsd.gt_graph (&g);

    // Call the algorithm
    lsd.init_call();
    call_springembedder_kamada(lsd.proc_info(), edgelength());
    lsd.clean_up();

    // restore multi-edges
    list<edge>::iterator it;
    list<edge>::iterator end = edges.end();

    for (it = edges.begin (); it != end; ++it) {
	g.attached()->restore_edge (*it);
    }
    
    remove_all_bends (g);
    reset_NEIs (g);
    adjust_coordinates (g, 10, 10);

    return GT_OK;
}

int GT_SpringKamada::check (GT_Graph&  g , string& message)
{
    // check on empty graph
    if (g.leda().number_of_nodes() < 1 ) {
	return GT_OK;
    }
    
    LSD   lsd;
    lsd.gt_graph (&g);

    // Call the algorithm
    lsd.init_call();
    message = check_springembedder_kamada(lsd.proc_info());
    lsd.clean_up();

    if (message != (string)"") {
	return GT_ERROR;
    }
    
    message = "";
    return GT_OK;
}

//
// Tcl Wrapper
//


GT_Tcl_SpringKamada::GT_Tcl_SpringKamada (const string& name) :
	GT_Tcl_Algorithm<GT_SpringKamada> (name)
{
}

GT_Tcl_SpringKamada::~GT_Tcl_SpringKamada ()
{
}

int GT_Tcl_SpringKamada::parse (GT_Tcl_info& info, int& index,
    GT_Tcl_Graph* /* g*/)
{
    int code = TCL_OK;

    if(info.argv(index)[0] == '-') {
        if(!strcmp(info.argv(index),"-edgelength")) {
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_edgelength);
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

int Gt_lsd_springembedder_kamada_Init (Tcl_Interp* interp)
{
    int code = 0;

    GT_Tcl_SpringKamada* layout_spring_kamada =
	new GT_Tcl_SpringKamada ("layout_spring_kk");
    code = layout_spring_kamada->install(interp);
    if (code == TCL_ERROR) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_lsd_springembedder_kamada", version);
    if (code != TCL_OK) {
	return code;
    }

    return code;
}
