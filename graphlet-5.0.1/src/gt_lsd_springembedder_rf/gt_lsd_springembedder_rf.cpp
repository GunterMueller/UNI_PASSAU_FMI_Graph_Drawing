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
#include "gt_lsd.h"
#include "gt_lsd_springembedder_rf.h"
#include "springembedder_rf_export.h"
#include "rf_checks.h"

//////////////////////////////////////////
//
// Springembedder RF
//
//////////////////////////////////////////


//
// Algorithm
//

GT_SpringRf::GT_SpringRf (const string& name) : GT_Algorithm (name)
{
}

GT_SpringRf::~GT_SpringRf ()
{
}

int GT_SpringRf::run (GT_Graph& g)
{
    // check on empty graph
    if (g.leda().number_of_nodes() < 1 ) {
	return GT_OK;
    }

    // find mult-edges and self-loops and hide them
    list<edge> edges;
    hide_selfloops (g.attached(), edges);
    hide_multiedges (g.attached(), edges);

    //graph leda = g.leda();
    LSD   lsd;

    lsd.gt_graph (&g);
    springembedder_rf_settings = init_springembedder_rf_settings(
	weighted(), maximal_force(),
	vibration(), maximal_iterations(), edgelength());
    lsd.callSgraph(call_springembedder_rf);

    // restore hidden edges
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

int GT_SpringRf::check (GT_Graph& g, string& message)
{
    message = spring_rf_is_correct (*g.attached());              
    if (message == "") {
	return GT_OK;
    }
    return GT_ERROR;
}

//
// Tcl Wrapper
//

GT_Tcl_SpringRf::GT_Tcl_SpringRf (const string& name) :
	GT_Tcl_Algorithm<GT_SpringRf> (name)
{
    the_weighted = false;
    the_max_iter = 1000;
    the_edgelength = 128;
    the_max_force = 3.0;
    the_vibration = 0.4;
}

GT_Tcl_SpringRf::~GT_Tcl_SpringRf ()
{
}

int GT_Tcl_SpringRf::parse (GT_Tcl_info& info, int& index,
    GT_Tcl_Graph* /* g*/)
{
    int code = TCL_OK;

    if(info.argv(index)[0] == '-') {

        if (!strcmp(info.argv(index),"-edgelength")) {
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_edgelength);
	} else if (!strcmp(info.argv(index),"-weighted")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_weighted);
	} else if (!strcmp(info.argv(index),"-maximal_iterations")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_max_iter);
	} else if (!strcmp(info.argv(index),"-maximal_force")){
            code = Tcl_GetDouble (info.interp(), info.argv(++index), &the_max_force);
	} else if (!strcmp(info.argv(index),"-vibration")){
            code = Tcl_GetDouble (info.interp(), info.argv(++index), &the_vibration);
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

int Gt_lsd_springembedder_rf_Init (Tcl_Interp* interp)
{
    int code = 0;

    GT_Tcl_Algorithm_Command* layout_spring_rf =
	new GT_Tcl_SpringRf ("layout_spring_fr");
    code = layout_spring_rf->install(interp);
    if (code == TCL_ERROR) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_lsd_springembedder_rf", version);
    if (code != TCL_OK) {
	return code;
    }

    return code;
}
