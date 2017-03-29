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
#include "gt_lsd_sugiyama.h"
#include "sugiyama_export.h"



//////////////////////////////////////////
//
// DAG Layout
//
//////////////////////////////////////////


//
// Algorithm
//

GT_Sugiyama::GT_Sugiyama (const string& name) : GT_Algorithm (name)
{
    the_vert_dist = 64;
    the_horiz_dist = 64;
    the_it1 = 1;
    the_it2 = 1;
    the_arrange = 0;
    the_res_cycles = 0;
    the_reduce_cross = 0;
}

GT_Sugiyama::~GT_Sugiyama ()
{
}

int GT_Sugiyama::run (GT_Graph& g)
{
    // check on empty graph
    if (g.leda().number_of_nodes() < 1 ) {
	return GT_OK;
    }

    //graph *leda = g.attached();
    LSD   lsd;
    bool is_undirected = g.leda().is_undirected ();
    if (is_undirected) {
	g.leda().make_directed();
    }
    lsd.gt_graph (&g);

    sugiyama_settings = init_sugiyama_settings (vertical_distance(),
	horizontal_distance(), the_it1, the_it2, arrange(), 
	resolve_cycles(), reduce_crossings());
    
    lsd.callSgraph(call_sugiyama_layout);

    if (is_undirected ) {
	g.leda().make_undirected();
    }
    adjust_coordinates (g, 10, 10);
    reset_NEIs (g);
    return GT_OK;
}

int GT_Sugiyama::check (GT_Graph& /* g */, string& message)
{
     message = "";
    return GT_OK;
}

//
// Tcl Wrapper
//

GT_Tcl_Sugiyama::GT_Tcl_Sugiyama (const string& name) :
	GT_Tcl_Algorithm<GT_Sugiyama> (name)
{
}

GT_Tcl_Sugiyama::~GT_Tcl_Sugiyama()
{
}

int GT_Tcl_Sugiyama::parse (GT_Tcl_info& info, int& index,
    GT_Tcl_Graph* /* g*/)
{
    int code = TCL_OK;

    if(info.argv(index)[0] == '-') {

        if (!strcmp(info.argv(index),"-vertical_distance")) {
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_vert_dist);
	} else if (!strcmp(info.argv(index),"-horizontal_distance")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_horiz_dist);
	} else if (!strcmp(info.argv(index),"-it1")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_it1);
	} else if (!strcmp(info.argv(index),"-it2")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_it2);
	} else if (!strcmp(info.argv(index),"-arrange")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_arrange);
	} else if (!strcmp(info.argv(index),"-resolve_cycles")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &the_res_cycles);
	} else if (!strcmp(info.argv(index),"-reduce_crossings")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), 
		&the_reduce_cross);
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

int Gt_lsd_sugiyama_Init (Tcl_Interp* interp)
{
    int code = 0;

    GT_Tcl_Algorithm_Command* layout_dag =
	new GT_Tcl_Sugiyama ("layout_dag");
    code = layout_dag->install(interp);
    if (code == TCL_ERROR) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_lsd_sugiyama", version);
    if (code != TCL_OK) {
	return code;
    }

    return code;
}
