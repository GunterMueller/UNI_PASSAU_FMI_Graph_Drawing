/* This software is distributed under the Lesser General Public License */
//
// xdag_algorithm.cpp
//
// This file implements the classes GT_Extended_DAG_Algorithm and
// GT_Tcl_Extended_DAG_Algorithm which build the interface between
// C++ and Tcl for the extended DAG algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/xdag_algorithm.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 11:16:33 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//


#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>

#include <gt_tcl/Tcl_Algorithm.h>

#include "xdag_algorithm.h"
#include "dag.h"

#include <GTL/node_map.h>

// ========================================
//
// class GT_Extended_DAG_Algorithm
//
// ========================================

//
// constructor
//
// set the algorithm's name and initialize the parameters stored in this class
//

GT_Extended_DAG_Algorithm::GT_Extended_DAG_Algorithm (const string& name) :
	GT_Algorithm (name)
{
    min_node_node_distance(0);
    min_node_edge_distance(0);
    min_edge_edge_distance(0);
    default_edge_length(0);
    animation(0);
    iterations_crossing_reduction(0);
    last_phase_crossing_reduction(0);
    iterations_node_positioning(0);
    last_phase_node_positioning(0);
    protocol(0);
    stepping(0);
}


//
// destructor
//
// there's nothing to be done
//

GT_Extended_DAG_Algorithm::~GT_Extended_DAG_Algorithm ()
{
}


//
// run
//
// execute the algorithm and return the result of its execution
//

int GT_Extended_DAG_Algorithm::run (GT_Graph& /* g */)
{
    // This function is not intended to be executed, because there
    // is also a run method in class GT_Tcl_Extended_DAG_Algorithm.
    // Therefore we do not defined any actions in here.

    return GT_OK;
}


//
// check
//
// This function makes sure that the given graph is not empty and does
// not contain any self loops.  The rest, i.e. the check if the graph 
// is directed and acyclic is done in a separate function after starting
// the algorithm.
//

int GT_Extended_DAG_Algorithm::check (GT_Graph& g, string& message)
{
    // check if our graph is empty
    if (g.attached()->number_of_nodes() == 0) {
// 	message = "The graph is empty.";
// 	return GT_ERROR;
	return TCL_OK;
    }
    
    // check if our graph contains self loops
    edge e;
    forall_edges (e, g.leda()) {
	if (e.source() == e.target()) {
	    message = "The graph contains self loops.";
	    return GT_ERROR;
	}
    }
    
    return GT_OK;
}


// ========================================
//
// class GT_Tcl_Extended_DAG_Algorithm
//
// ========================================

//
// constructor
//
// this constructor is for the Tcl interface
//

GT_Tcl_Extended_DAG_Algorithm::GT_Tcl_Extended_DAG_Algorithm (const
    string& name) : GT_Tcl_Algorithm<GT_Extended_DAG_Algorithm> (name)
{
}


//
// destructor
//
// there's nothing to be done
//

GT_Tcl_Extended_DAG_Algorithm::~GT_Tcl_Extended_DAG_Algorithm ()
{
}


//
// parse
//
// parsing the command-line arguments entered in GraphScript
//

int GT_Tcl_Extended_DAG_Algorithm::parse (GT_Tcl_info& info,
    int& index, GT_Tcl_Graph* /* g */)
{
    int integer_value,
	code = TCL_OK;

   if(info.argv(index)[0] == '-') {

        if (!strcmp(info.argv(index),"-node_node_distance")) {
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    min_node_node_distance(integer_value);
	} else if (!strcmp(info.argv(index),"-node_edge_distance")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    min_node_edge_distance(integer_value);
	} else if (!strcmp(info.argv(index),"-edge_edge_distance")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    min_edge_edge_distance(integer_value);
	} else if (!strcmp(info.argv(index),"-edgelength")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    default_edge_length(integer_value);
	} else if (!strcmp(info.argv(index),"-animation")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    animation(1<<integer_value);
	} else if (!strcmp(info.argv(index),"-crossing_reduction_iterations")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    iterations_crossing_reduction(integer_value);
	} else if (!strcmp(info.argv(index),"-node_positioning_iterations")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    iterations_node_positioning(integer_value);
	} else if (!strcmp(info.argv(index),"-last_crossing_reduction")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    last_phase_crossing_reduction(integer_value);
	} else if (!strcmp(info.argv(index),"-last_node_positioning")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    last_phase_node_positioning(integer_value);
	} else if (!strcmp(info.argv(index),"-protocol")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    protocol(integer_value);
	} else if (!strcmp(info.argv(index),"-stepping")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &integer_value);
	    stepping(integer_value);
	}
    }

    index++;
    return code;
}


//
// run
//
// Execute the algorithm and return the result of its execution. The call of
// 'initialize' is necessary to hand the parameters from the "Options" menu
// over to the algorithm.
//

int GT_Tcl_Extended_DAG_Algorithm::run (GT_Graph& g)
{
    if (g.attached()->number_of_nodes() == 0) {
	return GT_OK;
    }

    Directed_Acyclic_Graph* dag = new Directed_Acyclic_Graph (&g);

//     cout << "min_node_node_distance   = " << min_node_node_distance()
// 	 << "\nmin_node_edge_distance   = " << min_node_edge_distance()
// 	 << "\nmin_edge_edge_distance   = " << min_edge_edge_distance()
// 	 << "\ndefault_edge_length      = " << default_edge_length()
// 	 << "\nanimation                = " << animation()
// 	 << "\niterations_crossing_red. = " << iterations_crossing_reduction()
// 	 << "\nlast_phase_crossing_red. = " << last_phase_crossing_reduction()
// 	 << "\niterations_node_posit.   = " << iterations_node_positioning()
// 	 << "\nlast_phase_node_posit.   = " << last_phase_node_positioning()
// 	 << "\nstepping                 = " << stepping()
// 	 << "\nprotocol                 = " << protocol() << endl;

    dag->initialize(
	tcl_interpreter(),
	min_node_node_distance(),
	min_node_edge_distance(),
	min_edge_edge_distance(),
	default_edge_length(),
	animation(),
	iterations_crossing_reduction(),
	last_phase_crossing_reduction(),
 	iterations_node_positioning(),
	last_phase_node_positioning(),
	protocol(),
	stepping() );

    string message = dag->generate_layout();

    delete dag;
    
    if (message != "ok") {
	result(message);
	return GT_ERROR;
    }
    
    return GT_OK;
}
