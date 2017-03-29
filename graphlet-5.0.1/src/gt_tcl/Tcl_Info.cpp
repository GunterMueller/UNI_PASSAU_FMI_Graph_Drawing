/* This software is distributed under the Lesser General Public License */
//
// Tcl_Info.h
//
// This file implements the class GT_Tcl_info
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Info.cpp,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:46:21 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>

#include "Tcl_Graph.h"
#include "Tcl_Command.h"


GT_Tcl_info::GT_Tcl_info ()
{
    ;
}


GT_Tcl_info::GT_Tcl_info (Tcl_Interp* interp,
    int argc, char** argv)
{
    the_interp      = interp;
    the_argc        = argc;
    the_argv        = argv;
}


GT_Tcl_info::~GT_Tcl_info()
{
    ;
}


//
// Special access to msg
//


void GT_Tcl_info::msg (const string& s)
{
    char* buffer = Tcl_Alloc (s.size()+1);
    sprintf (buffer, "%s", s.c_str());
    Tcl_SetResult (the_interp, buffer, TCL_VOLATILE);
}


void GT_Tcl_info::msg (const char* s)
{
    Tcl_SetResult (the_interp, (char*)s, TCL_VOLATILE);
}


const string GT_Tcl_info::msg () const
{
    return string (Tcl_GetStringResult(the_interp));
}


//
// Special access to argv
//

const char* GT_Tcl_info::argv (const int i) const
{
    assert (i < the_argc);
    return the_argv[i];
}


char* GT_Tcl_info::argv (const int i)
{
    assert (i < the_argc);
    return the_argv[i];
}


const GT_Key GT_Tcl_info::operator() (const int i) const
{
    return graphlet->keymapper.add (argv(i));
}



GT_Key GT_Tcl_info::operator() (const int i)
{
    return graphlet->keymapper.add (argv(i));
}



//
// argc utils
//

bool GT_Tcl_info::is_last_arg (const int index) const
{
    return args_left (index, 0, true);
}


bool GT_Tcl_info::exists (const int index) const
{
    return args_left (index, 0, false);
}


int GT_Tcl_info::args_left (const int index) const
{
    return (the_argc-1 - index);
}


bool GT_Tcl_info::args_left (const int index, const int n, bool exact) const
{
    if (exact) {
	return (the_argc-1 == index+n);
    } else {
	return (the_argc-1 >= index+n);
    }
}


bool GT_Tcl_info::args_left_at_least (const int index, const int n) const
{
    return args_left (index, n, false);
}


bool GT_Tcl_info::args_left_exactly (const int index, const int n) const
{
    return args_left (index, n);
}


void GT_Tcl_info::msg (const int error)
{
    msg (graphlet->error.msg (error));
}


void GT_Tcl_info::msg (const int error, const int i)
{
    msg (graphlet->error.msg (error, i));
}


void GT_Tcl_info::msg (const int error, const string& s)
{
    msg (graphlet->error.msg (error, s));
}



//////////////////////////////////////////
//
// "GT:" Prefix Management
//
//////////////////////////////////////////

//
// Strip leading "GT:" prefix and convert to integer
//

int GT_Tcl_info::strip_GT_prefix (const char* s, int& stripped)
{
    //
    // strip off leading "GT:"
    //

    if (GT_Tcl::is_gt_object(s)) {

	//
	// convert to integer
	//
	
	int id;
	int code = Tcl_GetInt (the_interp, (char*)(s+3), &id);

	if (code != TCL_OK) {
	    msg (GT_Error::no_id, s);
	    return code;
	} else {
	    stripped = id;
	    return code;
	}
    }

    return TCL_ERROR;
}


int GT_Tcl_info::strip_GT_prefix (int index, int& stripped)
{
    return strip_GT_prefix (the_argv[index], stripped);
}

    

//////////////////////////////////////////
//
// Parser Utilities
//
//////////////////////////////////////////


//
// Parse int
//

int GT_Tcl_info::parse (const char* s, int& result)
{
    return Tcl_GetInt (the_interp, (char*)s, &result);
}


int GT_Tcl_info::parse (int& index, int& result)
{
    return parse (the_argv[index], result);
}


//
// Parse double
//

int GT_Tcl_info::parse (const char* s, double& result)
{
    return Tcl_GetDouble (the_interp, (char*)s, &result);
}


int GT_Tcl_info::parse (int& index, double& result)
{
    return parse (the_argv[index], result);
}


//
// Parse bool
//

int GT_Tcl_info::parse (const char* s, bool& result)
{
    int i = 0;
    int code = Tcl_GetBoolean (the_interp, (char*)s, &i);
    result = (i != 0);
    return code;
}

int GT_Tcl_info::parse (int& index, bool& result)
{
    return parse (the_argv[index], result);
}


//
// Parse list<double>
//

int GT_Tcl_info::parse (const char* s, list<double>& l)
{
    int list_argc;
    char** list_argv;

    int code = Tcl_SplitList (the_interp, (char*)s, &list_argc, &list_argv);
    if (code != TCL_OK) {
	return TCL_ERROR;
    }

    double d;
    for (int i=0; i<list_argc; i++) {
	code = Tcl_GetDouble (the_interp, list_argv[i], &d);
	if (code != TCL_OK) {
	    break;
	}
	l.push_back (d);
    }

    Tcl_Free ((char*)list_argv);
    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, list<double>& l)
{
    return parse (the_argv[index], l);
}


//
// Parse GT_Tcl_Graph
//

int GT_Tcl_info::parse (const char* s, GT_Tcl_Graph*& g)
{
    const char* graph_name;
    
    if (GT_Tcl::is_gt_object (s)) {
	graph_name = s;
    } else {
	string gt_index = GT::format("%s,graph", s);
	graph_name = Tcl_GetVar2 (the_interp,
	    "GT",
	    const_cast<char*>(gt_index.c_str()),
	    TCL_GLOBAL_ONLY);
    }

    if (graph_name != NULL) {
	int code = GT_Tcl_Graph_Command::get (the_interp,
	    graph_name, g);
	if (code != TCL_OK) {
	    return code;
	} else {
	    return TCL_OK;
	}
    } else {
	msg (GT::format("%s is not a graph", s));
	return TCL_ERROR;
    }
}


int GT_Tcl_info::parse (int& index, GT_Tcl_Graph*& g)
{
    return parse (the_argv[index], g);
}


//
// Parse GT_Graph
//

int GT_Tcl_info::parse (const char* s, GT_Graph*& g)
{
    GT_Tcl_Graph* tcl_g;
    int code = parse (s, tcl_g);
    if (code != TCL_OK) {
	return code;
    } else {
	g = tcl_g;
	return TCL_OK;
    }
}


int GT_Tcl_info::parse (int& index, GT_Graph*& g)
{
    return parse (the_argv[index], g);
}


//
// Parse node
//

int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g, node& n)
{
    int id;
    int code = strip_GT_prefix (s, id);
    if (code != TCL_OK) {
	msg (GT::format ("%s is not an edge", s));
	return code;
    }

    node found = g->find_node (id);
    if (found != node()) {
	n = found;
	return TCL_OK;
    } else {
	msg (GT_Error::no_id, id);
	return TCL_ERROR;
    }
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g, node& n)
{
    return parse (the_argv[index], g, n);
}


//
// Parse edge
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g, edge& e)
{
    int id;
    int code = strip_GT_prefix (s, id);
    if (code != TCL_OK) {
	msg (GT::format("%s is not an edge", s));
	return code;
    }

    edge found = g->find_edge (id);

    if (found != edge()) {
	e = found;
	return TCL_OK;
    } else {
	msg (GT_Error::no_id, id);
	return TCL_ERROR;
    }
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g, edge& e)
{
    return parse (the_argv[index], g, e);
}


//
// Parse list<node>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    list<node>& nodes)
{
    char** list_argv;
    int    list_argc;
    int code = TCL_OK;

    //
    // Split the list
    //
    
    code = Tcl_SplitList (the_interp, (char*)s, &list_argc, &list_argv);
    if (code == TCL_ERROR) {
	return code;
    }
    
    //
    // Parse the list of nodes
    //

    node n;
    for (int i = 0; i < list_argc; i++) {
	if (strcmp (list_argv[i], "nodes") == 0) {
	    list<node> all_nodes = g->leda().all_nodes();
	    nodes.splice(nodes.begin(), all_nodes);
	} else {
	    code = parse (list_argv[i], g, n);
	    if (code != TCL_OK) {
		break;
	    }
	    nodes.push_back (n);
	}
    }
    Tcl_Free ((char*)list_argv);
    
    return code;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g, list<node>& nodes)
{
    return parse (the_argv[index], g, nodes);
}


//
// Parse list<edge>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    list<edge>& edges)
{
    char** list_argv;
    int    list_argc;
    int    code = TCL_OK;

    //
    // Split the list
    //
    
    code = Tcl_SplitList (the_interp, (char*)s, &list_argc, &list_argv);
    if (code != TCL_OK) {
	return code;
    }
    
    //
    // Parse the list of edges
    //
	
    edge n;
    for (int i = 0; i < list_argc; i++) {
	if (strcmp (list_argv[i], "edges") == 0) {
	    list<edge> all_edges = g->leda().all_edges();
	    edges.splice(edges.begin(), all_edges);
	} else {
	    code = parse (list_argv[i], g, n);
	    if (code != TCL_OK) {
		break;
	    }   
	    edges.push_back (n);
	}
    }
    
    Tcl_Free ((char*)list_argv);

    return code;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g, list<edge>& edges)
{
    return parse (the_argv[index], g, edges);
}




//
// Parse list<node> + list<edge>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    list<node>& nodes, list<edge>& edges)
{
    char** list_argv;
    int    list_argc;
    int    code = TCL_OK;

    //
    // Split the list
    //
    
    code = Tcl_SplitList (the_interp, (char*)s, &list_argc, &list_argv);
    if (code != TCL_OK) {
	return code;
    }
    
    //
    // Parse the list of nodes and edges
    //

    for (int i = 0; i < list_argc; i++) {
	
	if (strcmp (list_argv[i], "nodes") == 0) {
	    list<node> all_nodes = g->leda().all_nodes();
	    nodes.splice (nodes.begin(), all_nodes);
	} else if (strcmp (list_argv[i], "edges") == 0) {
	    list<edge> all_edges = g->leda().all_edges();
	    edges.splice (edges.begin(), all_edges);
	} else {
	    
	    edge e;
	    node n;
	
	    int id;
	    code = strip_GT_prefix (list_argv[i], id);
	    if (code != TCL_OK) {
		msg (GT::format("%s is not a valid GT object", s));
		return code;
	    }

	    if ((n = g->find_node (id)) != node()) {
		nodes.push_back (n);
	    } else if ((e = g->find_edge (id)) != edge()) {
		edges.push_back (e);
	    } else {
		msg (GT_Error::no_id, id);
		code = TCL_ERROR;
		break;
	    }
	}
    }

    Tcl_Free ((char*)list_argv);

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g,
    list<node>& nodes, list<edge>& edges)
{
    return parse (the_argv[index], g, nodes, edges);
}



//
// Parse node_map<int>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    node_map<int>*& array)
{
    if (array == 0) {
	array = new node_map<int> (g->leda(), 0);
    }

    node n;
    forall_nodes (n, g->leda()) {

	char array_index [GT_Tcl::int_string_length+3+1];
	sprintf (array_index, "GT:%d", g->gt(n).id());

	char* value = Tcl_GetVar2 (the_interp,
	    (char*)s, array_index,
	    TCL_LEAVE_ERR_MSG);
	if (value != NULL) {
	    int code = Tcl_GetInt (the_interp, value, &((*array)[n]));
	    if (code != TCL_OK) {
		return code;
	    }
	}
    }

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g,
    node_map<int>*& array)
{
    return parse (the_argv[index], g, array);
}


//
// Parse edge_map<int>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    edge_map<int>*& array)
{
    if (array == 0) {
	array = new edge_map<int> (g->leda(), 0);
    }

    edge e;
    forall_edges (e, g->leda()) {

	char array_index [GT_Tcl::int_string_length+3+1];
	sprintf (array_index, "GT:%d", g->gt(e).id());

	char* value = Tcl_GetVar2 (the_interp,
	    (char*)s, array_index,
	    TCL_LEAVE_ERR_MSG);
	if (value != NULL) {
	    int code = Tcl_GetInt (the_interp, value, &((*array)[e]));
	    if (code != TCL_OK) {
		return code;
	    }
	}
    }

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g,
    edge_map<int>*& array)
{
    return parse (the_argv[index], g, array);
}



//
// Parse node_map<double>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    node_map<double>*& array)
{
    if (array == 0) {
	array = new node_map<double> (g->leda(), 0.0);
    }

    node n;
    forall_nodes (n, g->leda()) {

	char array_index [GT_Tcl::int_string_length+3+1];
	sprintf (array_index, "GT:%d", g->gt(n).id());

	char* value = Tcl_GetVar2 (the_interp,
	    (char*)s, array_index,
	    TCL_LEAVE_ERR_MSG);
	if (value != NULL) {
	    int code = Tcl_GetDouble (the_interp, value, &((*array)[n]));
	    if (code != TCL_OK) {
		return code;
	    }
	}
    }

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g,
    node_map<double>*& array)
{
    return parse (the_argv[index], g, array);
}



//
// Parse edge_map<double>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    edge_map<double>*& array)
{
    if (array == 0) {
	array = new edge_map<double> (g->leda(), 0.0);
    }

    edge e;
    forall_edges (e, g->leda()) {

	char array_index [GT_Tcl::int_string_length+3+1];
	sprintf (array_index, "GT:%d", g->gt(e).id());

	char* value = Tcl_GetVar2 (the_interp,
	    (char*)s, array_index,
	    TCL_LEAVE_ERR_MSG);
	if (value != NULL) {
	    int code = Tcl_GetDouble (the_interp, value, &((*array)[e]));
	    if (code != TCL_OK) {
		return code;
	    }
	}
    }

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g,
    edge_map<double>*& array)
{
    return parse (the_argv[index], g, array);
}


//
// Parse node_map<string>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    node_map<string>*& array)
{
    if (array == 0) {
	array = new node_map<string> (g->leda(), "");
    }

    node n;
    forall_nodes (n, g->leda()) {

	char array_index [GT_Tcl::int_string_length+3+1];
	sprintf (array_index, "GT:%d", g->gt(n).id());

	char* value = Tcl_GetVar2 (the_interp,
	    (char*)s, array_index,
	    TCL_LEAVE_ERR_MSG);
	if (value == NULL) {
	    (*array)[n] = "";
	} else {
	    (*array)[n] = value;
	}
    }

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g,
    node_map<string>*& array)
{
    return parse (the_argv[index], g, array);
}



//
// Parse edge_map<string>
//


int GT_Tcl_info::parse (const char* s, const GT_Tcl_Graph* g,
    edge_map<string>*& array)
{
    if (array == 0) {
	array = new edge_map<string> (g->leda(), "");
    }

    edge e;
    forall_edges (e, g->leda()) {

	char array_index [GT_Tcl::int_string_length+3+1];
	sprintf (array_index, "GT:%d", g->gt(e).id());

	char* value = Tcl_GetVar2 (the_interp,
	    (char*)s, array_index,
	    TCL_LEAVE_ERR_MSG);
	if (value == NULL) {
	    (*array)[e] = "";
	} else {
	    (*array)[e] = value;
	}
    }

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, const GT_Tcl_Graph* g,
    edge_map<string>*& array)
{
    return parse (the_argv[index], g, array);
}



//
// Parse list<GT_Node_Attributes*> + list<GT_Edge_Attributes*>
// 
// (nodes, edges, node styles, edge styles)
//


int GT_Tcl_info::parse (const char* s, GT_Tcl_Graph* g,
    list<GT_Tcl_Graph*>& graphs,
    list<node>& nodes,
    list<edge>& edges,
    list<GT_Key>& node_styles,
    list<GT_Key>& edge_styles)
{
    char** list_argv;
    int    list_argc;
    int    code = TCL_OK;

    //
    // Split the list
    //
    
    code = Tcl_SplitList (the_interp, (char*)s, &list_argc, &list_argv);
    if (code != TCL_OK) {
	return code;
    }
    
    //
    // Parse the list of nodes and edges
    //

    for (int i = 0; i < list_argc; i++) {
	    
	if (strcmp (list_argv[i], "graph") == 0) {
	    
	    graphs.push_back (g);
	    
	} else if (strcmp (list_argv[i], "nodes") == 0) {
	    
	    node n;
	    forall_nodes (n, g->leda()) {
		nodes.push_back (n);
	    }

	} else if (strcmp (list_argv[i], "edges") == 0) {

	    edge e;
	    forall_edges (e, g->leda()) {
		edges.push_back (e);
	    }

	} else if (strcmp (list_argv[i], "nodestyles") == 0) {
	    
	    list<GT_Key>::const_iterator it;
	    list<GT_Key>::const_iterator end = node_styles.end();
	    for (it = node_styles.begin(); it != end; ++it)
	    {
		node_styles.push_back (*it);
	    }

	} else if (strcmp (list_argv[i], "edgestyles") == 0) {

	    list<GT_Key>::const_iterator it;
	    list<GT_Key>::const_iterator end = edge_styles.end();
	    for (it = edge_styles.begin(); it != end; ++it)
	    {
		edge_styles.push_back (*it);
	    }

	} else if (strncmp (list_argv[i], "GT:", 3) != 0) {

	    // Look for a node or edge style

	    GT_Key style = graphlet->keymapper.add (list_argv[i]);

	    if (g->node_style(style) != 0) {
		node_styles.push_back (style);
	    }
	    if (g->edge_style(style) != 0) {
		edge_styles.push_back (style);
	    }

	    if (g->edge_style(style) == 0 && g->edge_style(style)) {
		msg (GT::format ("No such style \"%s\".", list_argv[i]));
		return TCL_ERROR;
	    }

	} else {
	    
	    // Node or Edge

	    edge e;
	    node n;
	    
	    int id;
	    code = strip_GT_prefix (list_argv[i], id);
	    if (code != TCL_OK) {
		msg (GT::format ("%s is not a valid GT object", s));
		return code;
	    }
	    
	    if ((n = g->find_node (id)) != node()) {
		nodes.push_back (n);
	    } else if ((e = g->find_edge (id)) != edge()) {
		edges.push_back (e);
	    } else {
		msg (GT::format("%s is not a node or edge", list_argv[i]));
		code = TCL_ERROR;
		break;
	    }
	}
    }
	
    Tcl_Free ((char*)list_argv);

    return TCL_OK;
}


int GT_Tcl_info::parse (int& index, GT_Tcl_Graph* g,
    list<GT_Tcl_Graph*>& graphs,
    list<node>& nodes,
    list<edge>& edges,
    list<GT_Key>& node_styles,
    list<GT_Key>& edge_styles)
{
    return parse (the_argv[index], g,
	graphs, nodes, edges, node_styles, edge_styles);
}


//////////////////////////////////////////
//
// Parse -option value
//
//////////////////////////////////////////

//
// -option int
//

int GT_Tcl_info::parse (int& index, const char* option, int& result,
    bool optional)
{
    const char* s = the_argv[index];

    if (!exists (index)) {
	if (optional) {
	    return TCL_OK;
	} else {
	    msg (GT::format("Expecting %s", option));
	    return TCL_ERROR;
	}
    }

    if (GT::streq (s, option)) {

	// Advance index
	index ++;

	// Parse result
	if (!exists (index)) {
	    msg (GT::format(
		"Missing argument for option %s, expecting integer",
		option));
	}
	int code = parse (index, result);
	if (code != TCL_OK) {
	    msg (GT::format(
		"Incorrect argument for option %s, expecting integer",
		option));
	    return code;
	}
	index ++;
    }

    return TCL_OK;
}


//
// -option double
//

int GT_Tcl_info::parse (int& index, const char* option, double& result,
    bool optional)
{
    const char* s = the_argv[index];

    if (!exists (index)) {
	if (optional) {
	    return TCL_OK;
	} else {
	    msg (GT::format("Expecting %s", option));
	    return TCL_ERROR;
	}
    }

    if (GT::streq (s, option)) {

	// Advance index
	index ++;

	// Parse result
	if (!exists (index)) {
	    msg (GT::format(
		"Missing argument for option %s, expecting double",
		option));
	}
	int code = parse (index, result);
	if (code != TCL_OK) {
	    msg (GT::format(
		"Incorrect argument for option %s, expecting double",
		option));
	    return code;
	}
	index ++;
    }

    return TCL_OK;
}


//
// -option bool
//

int GT_Tcl_info::parse (int& index, const char* option, bool& result,
    bool optional)
{
    const char* s = the_argv[index];

    if (!exists (index)) {
	if (optional) {
	    return TCL_OK;
	} else {
	    msg (GT::format("Expecting %s", option));
	    return TCL_ERROR;
	}
    }

    if (GT::streq (s, option)) {

	// Advance index
	index ++;

	// Parse result
	if (!exists (index)) {
	    msg (GT::format(
		"Missing argument for option %s, expecting boolean",
		option));
	}
	int code = parse (index, result);
	if (code != TCL_OK) {
	    msg (GT::format(
		"Incorrect argument for option %s, expecting boolean",
		option));
	    return code;
	}
	index ++;
    }

    return TCL_OK;
}


//
// -option string
//

int GT_Tcl_info::parse (int& index, const char* option, string& result,
    bool optional)
{
    const char* s = the_argv[index];

    if (!exists (index)) {
	if (optional) {
	    return TCL_OK;
	} else {
	    msg (GT::format("Expecting %s", option));
	    return TCL_ERROR;
	}
    }

    if (GT::streq (s, option)) {

	// Advance index
	index ++;

	// Parse result
	if (!exists (index)) {
	    msg (GT::format(
		"Missing argument for option %s, expecting string",
		option));
	}
	result = the_argv[index++];
    }

    return TCL_OK;
}



//////////////////////////////////////////
//
// Utility methods for msg management
//
//////////////////////////////////////////


void GT_Tcl_info::msg (const GT_Graph& g)
{
    msg (GT_Tcl::tcl(g));
}

void GT_Tcl_info::msg (const GT_Graph& g, const node n)
{
    msg (GT_Tcl::tcl (g, n));
}

void GT_Tcl_info::msg (const GT_Graph& g, const edge e)
{
    msg (GT_Tcl::tcl (g, e));
}


void GT_Tcl_info::msg (const GT_Graph& g,
    const list<node>& nodes)
{
    msg (GT_Tcl::tcl (g, nodes));
}


void GT_Tcl_info::msg (const GT_Graph& g,
    const list<edge>& edges)
{
    msg (GT_Tcl::tcl (g, edges));
}


void GT_Tcl_info::msg (const list<double>& doubles)
{
    msg (GT_Tcl::tcl (doubles));
}


void GT_Tcl_info::msg (const list<int>& integers)
{
    msg (GT_Tcl::tcl (integers));
}
