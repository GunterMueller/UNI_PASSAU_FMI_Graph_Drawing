/* This software is distributed under the Lesser General Public License */
//
// GT_Tcl_Graph.h
// 
// This module implements the class GT_Tcl_Graph
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Graph_handlers.cpp,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/04/15 16:10:21 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include "Tcl_Graph.h"

#include <gt_base/Key_description.h>

#include "Graphscript.h"

//
// Local helpers
//


static int update_status (Tcl_Interp* interp, int id, const char* type,
    int increment);



//////////////////////////////////////////
//
// int GT_Tcl_Graph::run_hooks (const list<string>& hooks,
//   const int id,
//   const GT_Key& handler_name,
//   const string& additional_arguments)
//
// Note: no real error checking, but returns TCL_ERROR if one
// hook fails.
//
//////////////////////////////////////////


int GT_Tcl_Graph::run_hooks (Tcl_Interp* interp, int& the_hook_return_code,
    char* graph_name,
    char* handler_name,
    char* additional_args1,
    char* additional_args2,
    char* additional_args3,
    char* additional_args4,
    char* additional_args5)
{
    int& code = the_hook_return_code;

    if (code != TCL_ERROR) {

	int argc = 2; // 2 arguments (graph_name and graph_handler) in any case
	char* argv[2 + 5]; // 5 additonal_args

	argv[0] = (graph_name == 0) ? "{}" : graph_name;
	argv[1] = (handler_name == 0) ? "{}" : handler_name;;

	if (additional_args1 != 0) {
	    argv[argc++] = additional_args1;
	}
	if (additional_args2 != 0) {
	    argv[argc++] = additional_args2;
	}
	if (additional_args3 != 0) {
	    argv[argc++] = additional_args3;
	}
	if (additional_args4 != 0) {
	    argv[argc++] = additional_args4;
	}
	if (additional_args5 != 0) {
	    argv[argc++] = additional_args5;
	}
	
	char* cmd = Tcl_Merge (argc, argv);
	
	char* hook_var = Tcl_GetVar2 (interp, "GT::hooks", handler_name,
	    TCL_GLOBAL_ONLY);
	if (hook_var != NULL) {
	    int argc;
	    char** argv;
	    code = Tcl_SplitList (interp, hook_var, &argc, &argv);
	    for (int i = 0; code != TCL_ERROR && i < argc; i++) {
		// cout << argv[i] << " " << cmd << endl;
		code = Tcl_VarEval (interp, argv[i], " ", cmd, NULL);
	    }
	}

	Tcl_Free (cmd);

    }

    return code;
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    char* additional_args1,
    char* additional_args2,
    char* additional_args3,
    char* additional_args4,
    char* additional_args5)
{
    if (the_hook_return_code != TCL_ERROR) {

	char graph_name [GT_Tcl::id_length];
	sprintf (graph_name, "GT:%d", gt().id());

	the_hook_return_code = run_hooks (the_graphscript->interp(),
	    the_hook_return_code,
	    graph_name,
	    const_cast<char*>(handler_name.name().c_str()),
	    additional_args1,
	    additional_args2,
	    additional_args3,
	    additional_args4,
	    additional_args5);

    }

    return the_hook_return_code;
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    node n,
    char* additional_args)
{
    char node_name [GT_Tcl::id_length];
    sprintf (node_name, "GT:%d", gt(n).id());
    
    return run_hooks (handler_name, node_name,
	additional_args);
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    const list<node>& nodes,
    char* additional_args)
{
    string node_names;
    GT_Tcl::tcl (*this, nodes, node_names);

    return run_hooks (handler_name, const_cast<char*>(node_names.c_str()),
	additional_args);
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    node n1,
    node n2,
    char* additional_args)
{
    char node1_name [GT_Tcl::id_length];
    sprintf (node1_name, "GT:%d", gt(n1).id());
    char node2_name [GT_Tcl::id_length];
    sprintf (node2_name, "GT:%d", gt(n2).id());

    return run_hooks (handler_name, node1_name, node2_name,
	additional_args);
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    edge e,
    char* additional_args)
{
    char edge_name [GT_Tcl::id_length];
    sprintf (edge_name, "GT:%d", gt(e).id());

    return run_hooks (handler_name, edge_name, additional_args);
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    edge e,
    node n1,
    node n2,
    char* additional_args)
{
    char node1_name [GT_Tcl::id_length];
    sprintf (node1_name, "GT:%d", gt(n1).id());
    char node2_name [GT_Tcl::id_length];
    sprintf (node2_name, "GT:%d", gt(n2).id());
    char edge_name [GT_Tcl::id_length];
    sprintf (edge_name, "GT:%d", gt(e).id());

    return run_hooks (handler_name, node1_name, node2_name, edge_name,
	additional_args);
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    const list<edge>& edges,
    char* additional_args)
{
    string edge_names;
    GT_Tcl::tcl (*this, edges, edge_names);

    return run_hooks (handler_name, const_cast<char*>(edge_names.c_str()),
	additional_args);
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
	const list<node>& nodes,
	const list<edge>& edges,
	char* additional_args)
{
    string node_names;
    GT_Tcl::tcl (*this, nodes, node_names);
    string edge_names;
    GT_Tcl::tcl (*this, edges, edge_names);

    return run_hooks (handler_name,
	const_cast<char*>(node_names.c_str()),
	const_cast<char*>(edge_names.c_str()),
	additional_args);
}


int GT_Tcl_Graph::run_hooks (const GT_Key& handler_name,
    const list<string>& strings,
    char* additional_args)
{
    string merged = GT_Tcl::merge (strings);

    return run_hooks (handler_name, const_cast<char*>(merged.c_str()),
	additional_args);
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::pre_new_graph_handler ()
// GT_Tcl_Graph::post_new_graph_handler ()
//
//////////////////////////////////////////


void GT_Tcl_Graph::pre_new_graph_handler ()
{
    baseclass::pre_new_graph_handler ();
}


void GT_Tcl_Graph::post_new_graph_handler ()
{
    baseclass::post_new_graph_handler ();
}


//////////////////////////////////////////
//
// GT_Tcl_Graph::pre_new_node_handler()
// GT_Tcl_Graph::post_new_node_handler (node n)
//
//////////////////////////////////////////


void GT_Tcl_Graph::pre_new_node_handler()
{
    baseclass::pre_new_node_handler ();

    run_hooks (GT_Keys::pre_new_node_handler);
}


void GT_Tcl_Graph::post_new_node_handler (node n)
{
    baseclass::post_new_node_handler (n);

    GT_Node_Attributes& attrs = gt(n);

    //
    // Status (number of nodes & edges)
    //

    update_status (the_graphscript->interp(), gt().id(), "nodes", 1);

    //
    // Autonumber nodes
    //

    if (the_graphscript->option_autonumber_nodes()) {

	string index = GT::format("GT:%d,autonumber_nodes", gt().id());
	char* autonumber_string = Tcl_GetVar2 (the_graphscript->interp(),
	    "GT", const_cast<char*>(index.c_str()),
	    TCL_GLOBAL_ONLY);
	
	if (autonumber_string != 0) {

	    int autonumber;
	    int code = Tcl_GetInt (the_graphscript->interp(),
		autonumber_string,
		&autonumber);
	    if (code != TCL_OK) {
		the_hook_return_code = code;
	    } else {
		
		char buffer[GT_Tcl::int_string_length];
		sprintf (buffer, "%d", ++autonumber);

		attrs.label (buffer);
		
		Tcl_SetVar2 (the_graphscript->interp(),
		    "GT", const_cast<char*>(index.c_str()), buffer,
		    TCL_GLOBAL_ONLY);
	    }
	}
    }

    if (the_graphscript->option_autonumber_nodes_by_id()) {
	char buffer[GT_Tcl::int_string_length];
	sprintf (buffer, "%d", attrs.id());
	attrs.label (buffer);
    }

    if (the_graphscript->option_autonumber_nodes_by_degree()) {	
	attrs.label ("0"); // This is a *new* node
    }

    run_hooks (GT_Keys::post_new_node_handler, n);
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::post_new_edge_handler (node source, node target)
// GT_Tcl_Graph::post_new_edge_handler (edge e)
//
//////////////////////////////////////////


void GT_Tcl_Graph::pre_new_edge_handler (node source, node target)
{
    baseclass::pre_new_edge_handler (source, target);

    run_hooks (GT_Keys::pre_new_edge_handler, source, target);
}


void GT_Tcl_Graph::post_new_edge_handler (edge e)
{
    baseclass::post_new_edge_handler (e);
	
    GT_Edge_Attributes& attrs = gt(e);
	
    //
    // Status (number of nodes & edges)
    //

    update_status (the_graphscript->interp(), gt().id(), "edges", 1);

    //
    // autonumber label
    //

    if (the_graphscript->option_autonumber_edges()) {

	string index = GT::format("GT:%d,autonumber_edges", gt().id());
	char* autonumber_string = Tcl_GetVar2 (the_graphscript->interp(),
	    "GT", const_cast<char*>(index.c_str()),
	    TCL_GLOBAL_ONLY);

	if (autonumber_string != 0) {

	    int autonumber;
	    int code = Tcl_GetInt (the_graphscript->interp(),
		autonumber_string,
		&autonumber);
	    if (code != TCL_OK) {
		the_hook_return_code = code;
	    } else {
		
		char buffer[GT_Tcl::int_string_length];
		sprintf (buffer, "%d", ++autonumber);

		attrs.label (buffer);
	    
		Tcl_SetVar2 (the_graphscript->interp(),
		    "GT", const_cast<char*>(index.c_str()), buffer,
		    TCL_GLOBAL_ONLY);
	    }
	}
    }

    if (the_graphscript->option_autonumber_edges_by_id()) {
	char buffer[GT_Tcl::int_string_length];
	sprintf (buffer, "%d", attrs.id());
	attrs.label (buffer);
    }

    if (the_graphscript->option_autonumber_nodes_by_degree()) {

	char buffer [GT_Tcl::int_string_length];

	node source = attrs.source();
	sprintf (buffer, "%d", source.degree());
	gt(source).label (buffer);

	node target = attrs.target();
	sprintf (buffer, "%d", target.degree());
	gt(target).label (buffer);
    }

    run_hooks (GT_Keys::post_new_edge_handler, e);
}


//////////////////////////////////////////
//
// GT_Tcl_Graph::pre_del_node_handler (node)
// GT_Tcl_Graph::post_del_node_handler ()
//
//////////////////////////////////////////



void GT_Tcl_Graph::pre_del_node_handler (node n)
{
    //
    // Status (number of nodes & edges)
    //

    update_status (the_graphscript->interp(), gt().id(), "nodes", -1);

    //
    // Hooks
    //

    run_hooks (GT_Keys::pre_del_node_handler, n);

    baseclass::pre_del_node_handler (n);
}


void GT_Tcl_Graph::post_del_node_handler ()
{
    run_hooks (GT_Keys::post_del_node_handler);

    baseclass::post_del_node_handler ();
}



//////////////////////////////////////////
//
// GT_Tcl_Graph::pre_del_edge_handler (edge e)
// GT_Tcl_Graph::post_del_edge_handler (node /* source */, node /* target */)
//
//////////////////////////////////////////


void GT_Tcl_Graph::pre_del_edge_handler (edge e)
{
    GT_Edge_Attributes& attrs = gt(e);

    //
    // Status (number of nodes & edges)
    //

    update_status (the_graphscript->interp(), gt().id(), "edges", -1);

    //
    // Autonumbering
    //

    if (the_graphscript->option_autonumber_nodes_by_degree()) {

	char buffer [GT_Tcl::int_string_length];

	node source = attrs.source();
	sprintf (buffer, "%d", source.degree() - 1);
	gt(source).label (buffer);

	node target = attrs.target();
	sprintf (buffer, "%d", target.degree() - 1);
	gt(target).label (buffer);
    }

    //
    // Hooks
    //

    run_hooks (GT_Keys::pre_del_edge_handler, e);

    baseclass::pre_del_edge_handler (e);
}


void GT_Tcl_Graph::post_del_edge_handler (node source, node target)
{
    run_hooks (GT_Keys::post_del_edge_handler);

    baseclass::post_del_edge_handler (source, target);
}


//////////////////////////////////////////
//
// Move Edge
//
// void GT_Tcl_Graph::pre_move_edge_handler (edge e, node n1, node n2)
// void GT_Tcl_Graph::post_move_edge_handler (edge e, node n1, node n2)
//
//////////////////////////////////////////


void GT_Tcl_Graph::pre_move_edge_handler (edge e, node n1, node n2)
{
    run_hooks (GT_Keys::pre_move_edge_handler, e, n1, n2);

    baseclass::pre_move_edge_handler (e, n1, n2);
}


void GT_Tcl_Graph::post_move_edge_handler (edge e, node n1, node n2)
{
    baseclass::post_move_edge_handler (e, n1, n2);

    run_hooks (GT_Keys::post_move_edge_handler, e, n1, n2);
}


	
//////////////////////////////////////////
//
// void GT_Tcl_Graph::pre_hide_edge_handler (edge)
// void GT_Tcl_Graph::post_hide_edge_handler (edge)
// void GT_Tcl_Graph::pre_restore_edge_handler (edge)
// void GT_Tcl_Graph::post_restore_edge_handler (edge)
//
//////////////////////////////////////////


void GT_Tcl_Graph::pre_hide_edge_handler (edge e)
{
    run_hooks (GT_Keys::pre_hide_edge_handler, e);

    baseclass::pre_hide_edge_handler (e);
}


void GT_Tcl_Graph::post_hide_edge_handler (edge e)
{
    baseclass::post_hide_edge_handler (e);

    run_hooks (GT_Keys::post_hide_edge_handler, e);
}


void GT_Tcl_Graph::pre_restore_edge_handler (edge e)
{
    run_hooks (GT_Keys::pre_restore_edge_handler, e);

    baseclass::pre_restore_edge_handler (e);
}


void GT_Tcl_Graph::post_restore_edge_handler (edge e)
{
    baseclass::post_restore_edge_handler (e);

    run_hooks (GT_Keys::post_restore_edge_handler, e);
}



//////////////////////////////////////////
//
// void GT_Tcl_Graph::pre_clear_handler ()
// void GT_Tcl_Graph::post_clear_handler ()
//
//////////////////////////////////////////


void GT_Tcl_Graph::pre_clear_handler ()
{
    run_hooks (GT_Keys::pre_clear_handler);

    baseclass::pre_clear_handler ();
}


void GT_Tcl_Graph::post_clear_handler ()
{
    run_hooks (GT_Keys::post_clear_handler);

    baseclass::post_clear_handler ();
}



//////////////////////////////////////////
//
// void GT_Tcl_Graph::touch (node, const string&)
// void GT_Tcl_Graph::touch (edge, const string&)
//
// void GT_Tcl_Graph::comment (const string&)
//
// bool GT_Tcl_Graph::query ()
// bool GT_Tcl_Graph::query (node, node)
// bool GT_Tcl_Graph::query (edge)
//
//////////////////////////////////////////


void GT_Tcl_Graph::touch (node n, const string& s)
{
    run_hooks (GT_Keys::touch_node_handler, n,
	const_cast<char*>(s.c_str()));
}


void GT_Tcl_Graph::touch (edge e, const string& s)
{
    run_hooks (GT_Keys::touch_edge_handler, e,
	const_cast<char*>(s.c_str()));
}


void GT_Tcl_Graph::comment (const string& s)
{
    run_hooks (GT_Keys::comment_handler,
	const_cast<char*>(s.c_str()));
}


static bool query_result (Tcl_Interp* interp, int& the_hook_return_code)
{
    assert (interp != 0);

    if (the_hook_return_code != TCL_ERROR) { 

	int result;
	int code = Tcl_GetBoolean (interp, interp->result, &result);

	if (code != TCL_ERROR) {
	    the_hook_return_code = TCL_OK;
	    return true;
	} else {
	    the_hook_return_code = TCL_ERROR;
	}   
    } 

    return false;
}

bool GT_Tcl_Graph::query ()
{
    run_hooks (GT_Keys::comment_handler);
    return query_result (the_graphscript->interp(), the_hook_return_code);
}


bool GT_Tcl_Graph::query (node n1, node n2)
{
    run_hooks (GT_Keys::query_node_node_handler, n1, n2);
    return query_result (the_graphscript->interp(), the_hook_return_code);
}


bool GT_Tcl_Graph::query (edge e)
{
    run_hooks (GT_Keys::query_edge_handler, e);
    return query_result (the_graphscript->interp(), the_hook_return_code);
}



//
//
//

static int update_status (Tcl_Interp* interp, int id, const char* type,
    int increment)
{
    int code = TCL_OK;

    string index = GT::format("GT:%d,%s", id, type);
    char* status_string = Tcl_GetVar2 (interp, "GT_status",
	const_cast<char*>(index.c_str()),
	TCL_GLOBAL_ONLY);

    if (status_string != 0) {
	int status;
	code = Tcl_GetInt (interp, status_string, &status);
	if (code == TCL_OK) {
	    
	    char buffer[GT_Tcl::int_string_length];
	    sprintf (buffer, "%d", status + increment);
	    
	    Tcl_SetVar2 (interp, "GT_status",
		const_cast<char*>(index.c_str()), buffer,
		TCL_GLOBAL_ONLY | TCL_LEAVE_ERR_MSG);
	}
    }

    string index_dirty = GT::format("GT:%d,dirty", id);
    Tcl_SetVar2 (interp, "GT_status",
	const_cast<char*>(index_dirty.c_str()), "1",
	TCL_GLOBAL_ONLY | TCL_LEAVE_ERR_MSG);

    return code;
}
