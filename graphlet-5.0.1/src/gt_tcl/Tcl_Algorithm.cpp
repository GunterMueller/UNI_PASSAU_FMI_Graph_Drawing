/* This software is distributed under the Lesser General Public License */
//
// Tcl_Algorithm.cc
//
// This file implements the class GT_Algorithm.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Algorithm.cpp,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:45:59 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"

#include "Tcl_Algorithm.h"


//////////////////////////////////////////
//
// class GT_Tcl_Algorithm
//
//////////////////////////////////////////


//
// Constructors and Destructor
//

GT_Tcl_Algorithm_Command::GT_Tcl_Algorithm_Command (const string& name) :
	GT_Tcl_Command (name, graphlet->keymapper.add (name))
{
    reset_before_run (false);   
    might_change_structure (true);
    might_change_coordinates (true);
}



GT_Tcl_Algorithm_Command::~GT_Tcl_Algorithm_Command ()
{
}


	
//
// command line parser
//


int GT_Tcl_Algorithm_Command::algorithm_parser (GT_Tcl_info& info, int& index,
    GT_Algorithm& algorithm)
{
    string msg;
    int code = TCL_OK;

    if (!info.exists (index)) {
		
	string usage = GT::format("Usage: %s graph ?arguments?", info.argv(0));
	Tcl_SetResult (info.interp(), const_cast<char*>(usage.c_str()),
	    TCL_VOLATILE);
	code = TCL_ERROR;
		
    } else {

	//
	// If the first argument is a Graphlet identier, it must
	// be the graph
	//
	
	GT_Tcl_Graph* g;
	code = info.parse (index, g);
	if (code != TCL_OK) {
	    Tcl_AppendElement (info.interp(),
		const_cast<char*>(info.msg().c_str()));
	    return code;
	} else {
	    index ++;
	}

	//
	// Parse the arguments.
	//
	
	// Reset (probably).
	if (the_reset_before_run) {
	    algorithm.reset();
	}
	result ("");
	
	while (info.exists (index)) {
	    code = parse (info, index, g);
	    if (code == TCL_ERROR) {
		Tcl_AppendElement (info.interp(),
		    const_cast<char*>(info.msg().c_str()));
		return code;
	    }
	}

	//
	// If we have a graph, run the algorithm.
	//
	
	if (g != 0) {

	    // Check first.
	    
	    string check_msg;
	    code = algorithm.check (*g, check_msg);

	    if (code == GT_OK) {

		// Run and save result if not empty.
		code = algorithm.run (*g);

		// Draw
		g->begin_draw();
		((GT_Graph*)g)->draw(); // ????
		g->end_draw();

		if (the_result != "") {
		    Tcl_SetResult (info.interp(),
			const_cast<char*>(the_result.c_str()),
			TCL_VOLATILE);
		}
			
	    } else {

		if (the_result != "") {
		    Tcl_ResetResult (info.interp());
		    Tcl_AppendElement (info.interp(),
			const_cast<char*>(check_msg.c_str()));
		    Tcl_AppendElement (info.interp(),
			const_cast<char*>(the_result.c_str()));
		} else {
		    Tcl_SetResult (info.interp(),
			const_cast<char*>(check_msg.c_str()),
			TCL_VOLATILE);
		}
	    }   
	}
    }

    return code;
}


int GT_Tcl_Algorithm_Command::parse (GT_Tcl_info& info,
    int& /* index */,
    GT_Tcl_Graph* /* g */)
{
    info.msg ("Error while parsing parameters\n");
    return TCL_ERROR;
}



//////////////////////////////////////////
//
// Utility methods for result management
//
//////////////////////////////////////////


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g)
{
    result (GT_Tcl::tcl(g));
}

void GT_Tcl_Algorithm_Command::result (const GT_Graph& g, const node n)
{
    result (GT_Tcl::tcl (g, n));
}

void GT_Tcl_Algorithm_Command::result (const GT_Graph& g, const edge e)
{
    result (GT_Tcl::tcl (g, e));
}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const list<node>& nodes)
{
    result (GT_Tcl::tcl (g, nodes));
}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const list<edge>& edges)
{
    result (GT_Tcl::tcl (g, edges));
}


void GT_Tcl_Algorithm_Command::result (const int i)
{
    result (GT_Tcl::tcl (i));
}


void GT_Tcl_Algorithm_Command::result (const list<int>& integers)
{
    result (GT_Tcl::tcl (integers));
}


void GT_Tcl_Algorithm_Command::result (const double d)
{
    result (GT_Tcl::tcl (d));
}


void GT_Tcl_Algorithm_Command::result (const list<double>& doubles)
{
    result (GT_Tcl::tcl (doubles));
}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const char* array_name,
    const node_map<int>& array)
{
    if (array_name != 0 && strlen(array_name) > 0) {
	
	node n;
	forall_nodes (n, g.leda()) {
	    
	    char array_index[GT_Tcl::int_string_length+3+1];
	    sprintf (array_index, "GT:%d", g.gt(n).id());
	    char new_value[GT_Tcl::int_string_length+3+1];
	    sprintf (new_value, "%d", array[n]);
		
	    Tcl_SetVar2 (tcl_interpreter(),
		(char*)array_name, array_index,
		new_value,
		TCL_LEAVE_ERR_MSG);
	}	
    }
}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const char* array_name,
    const edge_map<int>& array)
{
    if (array_name != 0 && strlen(array_name) > 0) {
	
	edge e;
	forall_edges (e, g.leda()) {
	    
	    char array_index[GT_Tcl::int_string_length+3+1];
	    sprintf (array_index, "GT:%d", g.gt(e).id());
	    char new_value[GT_Tcl::int_string_length+3+1];
	    sprintf (new_value, "%d", array[e]);
		
	    Tcl_SetVar2 (tcl_interpreter(),
		(char*)array_name, array_index,
		new_value,
		TCL_LEAVE_ERR_MSG);
	}	
    }
}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const char* array_name,
    const node_map<double>& array)
{
    if (array_name != 0 && strlen(array_name) > 0) {
	
	node n;
	forall_nodes (n, g.leda()) {
	    
	    char array_index[GT_Tcl::int_string_length+3+1];
	    sprintf (array_index, "GT:%d", g.gt(n).id());
	    char new_value[GT_Tcl::int_string_length+3+1];
	    sprintf (new_value, "%f", array[n]);
		
	    Tcl_SetVar2 (tcl_interpreter(),
		(char*)array_name, array_index,
		new_value,
		TCL_LEAVE_ERR_MSG);
	}	
    }
}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const char* array_name,
    const edge_map<double>& array)
{
    if (array_name != 0 && strlen(array_name) > 0) {
	
	edge e;
	forall_edges (e, g.leda()) {
	    
	    char array_index[GT_Tcl::int_string_length+3+1];
	    sprintf (array_index, "GT:%d", g.gt(e).id());
	    char new_value[GT_Tcl::int_string_length+3+1];
	    sprintf (new_value, "%f", array[e]);
		
	    Tcl_SetVar2 (tcl_interpreter(),
		(char*)array_name, array_index,
		new_value,
		TCL_LEAVE_ERR_MSG);
	}	
    }
}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const char* array_name,
    const node_map<string>& array)
{
    if (array_name != 0 && strlen(array_name) > 0) {
	
	node n;
	forall_nodes (n, g.leda()) {
	    
	    char array_index[GT_Tcl::int_string_length+3+1];
	    sprintf (array_index, "GT:%d", g.gt(n).id());

	    Tcl_SetVar2 (tcl_interpreter(),
		(char*)array_name, array_index,
		const_cast<char*>(array[n].c_str()),
		TCL_LEAVE_ERR_MSG);
	}	
    }

}


void GT_Tcl_Algorithm_Command::result (const GT_Graph& g,
    const char* array_name,
    const edge_map<string>& array)
{
    if (array_name != 0 && strlen(array_name) > 0) {
	
	edge e;
	forall_edges (e, g.leda()) {
	    
	    char array_index[GT_Tcl::int_string_length+3+1];
	    sprintf (array_index, "GT:%d", g.gt(e).id());
		
	    Tcl_SetVar2 (tcl_interpreter(),
		(char*)array_name, array_index,
		const_cast<char*>(array[e].c_str()),
		TCL_LEAVE_ERR_MSG);
	}	
    }

}
