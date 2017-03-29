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
#include <gt_base/NEI.h>

// LSD standard includes
#include <lsd/lsdstd.h>
#include "gem_export.h"
#include "gt_lsd_gem.h"

//-----------------------------------------------------------------------------
// hide_multiedges
// hide_selfloops
// Functions are from Robert Schirmer.
// ----------------------------------------------------------------------------

//////////////////////////////////////////
//
// Gem
//
//////////////////////////////////////////


//
// Algorithm
//

GT_Gem::GT_Gem (const string& name) : GT_Algorithm (name)
{
    the_insert_max_temp = 1.0;
    the_insert_start_temp = 0.3;
    the_insert_final_temp = 0.05; 
    the_insert_max_iter = 10;
    the_insert_gravity = 0.05;
    the_insert_oscilation = 0.4;
    the_insert_rotation = 0.5;
    the_insert_shake = 0.2;
    the_insert_skip = false;
    
    the_arrange_max_temp = 1.5;
    the_arrange_start_temp = 1.0;
    the_arrange_final_temp = 0.02;
    the_arrange_max_iter = 3;
    the_arrange_gravity = 0.1;
    the_arrange_oscilation = 0.4;
    the_arrange_rotation = 0.9;
    the_arrange_shake = 0.3;
    the_arrange_skip = false;

    the_optimize_max_temp = 0.25;
    the_optimize_start_temp = 0.05;
    the_optimize_final_temp = 0.02;
    the_optimize_max_iter = 3;
    the_optimize_gravity = 0.1;
    the_optimize_oscilation = 0.4;
    the_optimize_rotation = 0.9;
    the_optimize_shake = 0.3;
    the_optimize_skip = false;

    the_rand = true;
    the_quality = false;
    the_default_edgelength = 128;

}

GT_Gem::~GT_Gem ()
{
}

int GT_Gem::run (GT_Graph& g)
{
    // check on empty graph
    if (g.leda().number_of_nodes() < 1 ) {
	return GT_OK;
    }

    LSD   lsd;

    lsd.gt_graph (&g);
    gem_init_graph();
    set_gem_default_config (
	insertion_maximal_temperature(), insertion_start_temperature(),
	insertion_final_temperature(), insertion_maximal_iterations(),
	insertion_gravity(), insertion_oscilation(),
	insertion_rotation(), insertion_shake(),
	skip_insertion(),
	arrange_maximal_temperature(), arrange_start_temperature(),
	arrange_final_temperature(), arrange_maximal_iterations(),
	arrange_gravity(), arrange_oscilation(),
	arrange_rotation(), arrange_shake(),
	skip_arrange(),
	optimize_maximal_temperature(), optimize_start_temperature(),
	optimize_final_temperature(), optimize_maximal_iterations(),
	optimize_gravity(), optimize_oscilation(),
	optimize_rotation(), optimize_shake(),
	skip_optimize(),
	the_random(), quality(), edgelength());
    read_config();
    lsd.callSgraph(call_gem);

    remove_all_bends (g);    
    adjust_coordinates (g, 10, 10);
    reset_NEIs (g);
    return GT_OK;
}

int GT_Gem::check (GT_Graph& /* g */, string& message)
{
    message = "";
    return GT_OK;
}

//
// Tcl Wrapper
//

GT_Tcl_Gem::GT_Tcl_Gem (const string& name) : GT_Tcl_Algorithm<GT_Gem> (name)
{
}

GT_Tcl_Gem::~GT_Tcl_Gem ()
{
}

int GT_Tcl_Gem::parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* /* g*/)
{
    int code = TCL_OK;
    
    if(info.argv(index)[0] == '-') {
        if(!strcmp(info.argv(index),"-edgelength")) {
            code = Tcl_GetInt (info.interp(), info.argv(++index), 
		&the_default_edgelength);
        } else if (!strcmp(info.argv(index),"-insertion_maximal_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_max_temp);
       } else if (!strcmp(info.argv(index),"-insertion_start_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_start_temp);
       } else if (!strcmp(info.argv(index),"-insertion_final_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_final_temp);
       } else if (!strcmp(info.argv(index),"-insertion_maximal_iterations")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_max_iter);
       } else if (!strcmp(info.argv(index),"-insertion_gravity")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_gravity);
       } else if (!strcmp(info.argv(index),"-insertion_oscilation")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_oscilation);
       } else if (!strcmp(info.argv(index),"-insertion_rotation")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_rotation);
       } else if (!strcmp(info.argv(index),"-insertion_shake")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_insert_shake);
       } else if (!strcmp(info.argv(index),"-skip_insertion")){
	    code = Tcl_GetInt (info.interp(), info.argv(++index), 
		&the_insert_skip);
	    
        } else if (!strcmp(info.argv(index),"-arrange_maximal_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_max_temp);
       } else if (!strcmp(info.argv(index),"-arrange_start_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_start_temp);
       } else if (!strcmp(info.argv(index),"-arrange_final_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_final_temp);
       } else if (!strcmp(info.argv(index),"-arrange_maximal_iterations")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_max_iter);
       } else if (!strcmp(info.argv(index),"-arrange_gravity")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_gravity);
       } else if (!strcmp(info.argv(index),"-arrange_oscilation")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_oscilation);
       } else if (!strcmp(info.argv(index),"-arrange_rotation")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_rotation);
       } else if (!strcmp(info.argv(index),"-arrange_shake")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_arrange_shake);
       } else if (!strcmp(info.argv(index),"-skip_arrange")){
	    code = Tcl_GetInt (info.interp(), info.argv(++index), 
		&the_arrange_skip);
	    
        } else if (!strcmp(info.argv(index),"-optimize_maximal_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_max_temp);
       } else if (!strcmp(info.argv(index),"-optimize_start_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_start_temp);
       } else if (!strcmp(info.argv(index),"-optimize_final_temperature")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_final_temp);
       } else if (!strcmp(info.argv(index),"-optimize_maximal_iterations")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_max_iter);
       } else if (!strcmp(info.argv(index),"-optimize_gravity")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_gravity);
       } else if (!strcmp(info.argv(index),"-optimize_oscilation")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_oscilation);
       } else if (!strcmp(info.argv(index),"-optimize_rotation")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_rotation);
       } else if (!strcmp(info.argv(index),"-optimize_shake")){
	    code = Tcl_GetDouble (info.interp(), info.argv(++index), 
		&the_optimize_shake);
       } else if (!strcmp(info.argv(index),"-skip_optimize")){
	    code = Tcl_GetInt (info.interp(), info.argv(++index), 
		&the_optimize_skip);
	    
       } else if (!strcmp(info.argv(index),"-random")){
	    code = Tcl_GetInt (info.interp(), info.argv(++index), 
		&the_rand);
       } else if (!strcmp(info.argv(index),"-quality")){
	    code = Tcl_GetInt (info.interp(), info.argv(++index), 
		&the_quality);
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

int Gt_lsd_gem_Init (Tcl_Interp* interp)
{
    int code = 0;

    GT_Tcl_Algorithm_Command* layout_gem =
	new GT_Tcl_Gem ("layout_gem");
    code = layout_gem->install(interp);
    if (code == TCL_ERROR) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_lsd_gem", version);
    if (code != TCL_OK) {
	return code;
    }

    return code;
}
