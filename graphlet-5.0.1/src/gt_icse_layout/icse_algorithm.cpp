/* This software is distributed under the Lesser General Public License */
//
// icse_algorithm.cpp
//
// This implements the class
// GT_Layout_Iterative_Constraint_Spring_Embedder
// Here we actually call the real algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_icse_layout/icse_algorithm.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:36 $
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

#include "icse_layout.h"
#include "icse_algorithm.h"

//
// Constructor:
//

GT_Iterative_Constraint_Spring_Embedder_Algorithm::
GT_Iterative_Constraint_Spring_Embedder_Algorithm(const string& name)
	: GT_Layout_Constraint_Fruchterman_Reingold_Algorithm(name)
{
}


//
// run
// Set parameters and run the real algorithm.
// The CFR-method set_parameters is used.
//

int GT_Iterative_Constraint_Spring_Embedder_Algorithm::run(
    GT_Graph &g)
{
    Iterative_Constraint_Spring_Embedder
	iterative_constraint_spring_embedder(&g);

    this->set_cfr_parameters(iterative_constraint_spring_embedder);

    string message = iterative_constraint_spring_embedder.
	iterative_constraint_placement();

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

int GT_Iterative_Constraint_Spring_Embedder_Algorithm::
check (GT_Graph& /* g */, string& /* message */)
{
    return GT_OK;
}


//
// Constructor:
// The Tcl interface to the algorithm.
//

GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm::
GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm (
    const string& name) :
	GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm(name)
{
}


//
// parse:
// All parsing is done in the called CFR-method
//

int GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm::parse
(GT_Tcl_info& info, int& index, GT_Tcl_Graph*  g )
{
    int code = this->
	GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm
	::parse(info, index, g);

    return code;
}


//
// run:
// This gets called after parse.
//

int GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm::run(
    GT_Graph &g)
{
    Iterative_Constraint_Spring_Embedder
	iterative_constraint_spring_embedder(&g);

    this->set_cfr_parameters(iterative_constraint_spring_embedder);

    string message = iterative_constraint_spring_embedder.
	iterative_constraint_placement();

    //
    // "ok" is our default message.
    // If an error occured message contains a description.
    //

    if (message != "ok") {
	this->result(message);
	return GT_ERROR;
    }

    return GT_OK;
} 





