/* This software is distributed under the Lesser General Public License */
//
// cfr_algorithm.cpp
//
// This implements the class
// GT_Layout_Constraint_Fruchterman_Reingold_Algorithm
// which is the interface to the Constraint Fruchterman
// Reingold Layout Algorithm.
// Here we actually call the real algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/cfr_algorithm.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:19 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include "cfr_layout.h"
#include "cfr_algorithm.h"


//
// Constructor:
// Set some reasonable defaults. It might happen that
// the Algorithm isn't called with all parameters.
//

GT_Layout_Constraint_Fruchterman_Reingold_Algorithm::
GT_Layout_Constraint_Fruchterman_Reingold_Algorithm(const string& name)
	:GT_Algorithm (name)
{
    this->animation(0);
    this->colour_the_nodes(false);
    this->constraint_forces(false);
    this->delimiter(",");
    this->minimal_distance(0);
    this->minimal_force(0.5);
    this->new_bends(false);
    this->optimal_distance(0);
    this->phase1_damping(2);
    this->phase1_max_iteration(10);
    this->phase2_damping(4);
    this->phase2_max_iteration(10);
    this->phase3_damping(15);
    this->phase3_max_iteration(10);
    this->random_placement(0);
    this->tcl_interp((Tcl_Interp*)0);
    this->tcl_graph((GT_Tcl_Graph*)0);
    this->vibration_ratio(0.001);
    this->window_height(480);
    this->window_width(640);
    this->xoffset(0);
    this->yoffset(0);
}


void GT_Layout_Constraint_Fruchterman_Reingold_Algorithm::
set_cfr_parameters(FR_Constraint_Graph& fr_constraint_graph)
{
    fr_constraint_graph.set_parameters(
	this->animation(),
	this->colour_the_nodes(),
	this->constraint_forces(),
	this->delimiter(),
	this->minimal_distance(),
	this->minimal_force(),
	this->new_bends(),
	this->optimal_distance(),
	this->phase1_damping(),
	this->phase1_max_iteration(),
	this->phase2_damping(),
	this->phase2_max_iteration(),
	this->phase3_damping(),
	this->phase3_max_iteration(),
	this->random_placement(),
	this->respect_sizes(),
	this->tcl_graph(),
	this->tcl_interp(),
	this->vibration_ratio(),
	this->window_height(),
	this->window_width(),
	this->xoffset(),
	this->yoffset()
	);
}

//
// run:
// Set parameters and run the real algorithm.
//

int GT_Layout_Constraint_Fruchterman_Reingold_Algorithm::run(
    GT_Graph &g)
{
    FR_Constraint_Graph fr_constraint_graph(&g);

    this->set_cfr_parameters(fr_constraint_graph);
	
    string message = fr_constraint_graph.force_directed_placement();
	
    if (message != "ok") {
	return GT_ERROR;
    }
    return GT_OK;
}


//
// check:
// The check can only be done after the initilization
// of the constraints. Additionally the errors should
// be shown in the graph so we have to make the real
// check later.  	
//

int GT_Layout_Constraint_Fruchterman_Reingold_Algorithm::
check (GT_Graph& /* g */, string& /* message */)
{
    return GT_OK;
}


//
// Constructor:
// The Tcl interface to the algorithm.
//

GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm::
GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm (
    const string& name) :
	GT_Tcl_Algorithm
<GT_Layout_Constraint_Fruchterman_Reingold_Algorithm> (name)
{
}


int GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm::
get_int(Tcl_Interp* interp, char* argv, int& code)
{
    int int_value;
    
    code = Tcl_GetInt(interp, argv, &int_value);
    return int_value;
}

bool GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm::
get_boolean(Tcl_Interp* interp, char* argv, int& code)
{
    int int_value;
    
    code = Tcl_GetInt(interp, argv, &int_value);

    if (int_value) {
	return true;
    } else {
	return false;
    }
}

double GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm::
get_double(Tcl_Interp* interp, char* argv, int& code)
{
    double double_value;
    
    code = Tcl_GetDouble(interp, argv, &double_value);
    return double_value;
}

//
// parse:
// Here we parse various arguments of the algorithm.
// There are defaults - so it hasn't to be called with
// all arguments.
//

int GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm::
parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g)
{
    int code = TCL_OK;

    if(info.argv(index)[0] == '-') {
	if(!strcmp(info.argv(index),"-animation")) {
	    this->tcl_graph(g);
	    this->tcl_interp(info.interp());
	    
	    this->animation(this->get_int(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-optimal_distance")) {
	    this->optimal_distance(this->get_int(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),
	    "-constraint_minimal_distance")) {
	    this->minimal_distance(this->get_int(
		info.interp(), info.argv(++index), code));
	    
	} else if(!strcmp(info.argv(index),"-constraint_forces")) {
	    this->constraint_forces(this->get_boolean(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-iteration1")) {
	    this->phase1_max_iteration(this->get_int(
		info.interp(), info.argv(++index), code));
	    
	} else if(!strcmp(info.argv(index),"-iteration2")) {
	    this->phase2_max_iteration(this->get_int(
		info.interp(), info.argv(++index), code));
		
	} else if(!strcmp(info.argv(index),"-iteration3")) {
	    this->phase3_max_iteration(this->get_int(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-damping1")) {
	    this->phase1_damping(this->get_double(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-damping2")) {
	    this->phase2_damping(this->get_double(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-damping3")) {
	    this->phase3_damping(this->get_double(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-colour_nodes")) {
	    this->colour_the_nodes(this->get_boolean(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-new_bends")) {
	    this->new_bends(this->get_boolean(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-respect_sizes")) {
	    this->respect_sizes(this->get_boolean(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-vibration_ratio")) {
	    this->vibration_ratio(this->get_double(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-minimal_force")) {
	    this->minimal_force(this->get_double(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-width")) {
	    this->window_width(this->get_int(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-height")) {
	    this->window_height(this->get_int(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-xoffset")) {
	    this->xoffset(this->get_double(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-yoffset")) {
	    this->yoffset(this->get_double(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-random")) {
	    this->random_placement(this->get_boolean(
		info.interp(), info.argv(++index), code));

	} else if(!strcmp(info.argv(index),"-delimiter")) {
	    this->delimiter(info.argv(++index));
	}

	if (code != TCL_OK) {
	    return code;
	}
    }
    index++;

    return code;
}


//
// run:
// This gets called after parse.
//

int GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm::run(
    GT_Graph &g)
{
    FR_Constraint_Graph fr_constraint_graph(&g);

    this->set_cfr_parameters(fr_constraint_graph);

    //
    // "ok" is the default message.
    // If an error occured message contains a description.
    //

    string message = fr_constraint_graph.force_directed_placement();
	
    if (message != "ok") {
	this->result(message);
	return GT_ERROR;
    }

    return GT_OK;
} 







