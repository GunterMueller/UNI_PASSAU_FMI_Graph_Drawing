/* This software is distributed under the Lesser General Public License */

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>
#include <gt_base/NEI.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include <GTL/graph.h>

#include <iostream>

#include "../gt_tree_layout/tree_check.h"


#include "radial_tree_layout_algorithm.h"
// contains the class Layout_Radial_Algorithm_Implementation
// which implements the real algorithm

#include "radial_tree_layout.h"



// predefinitions

bool graph_is_tree(const graph& g);




//
// class GT_Layout_Radial_Algorithm
//



GT_Layout_Radial_Algorithm::GT_Layout_Radial_Algorithm (const string& name) :
	GT_Algorithm (name)
{
    // init vars
    root_selection (2); 
    parent_child_distance (15); 
    menu_padding_factor (2); 
    //automatic_expansion (); 
    distance_rigid_flexible_flag (0); 
    padding_type (1); 
    avoid_escaping_edges (0); 
    avoid_collinear_families (0); 
    center_parent (1); 
    center_children (1); 
    fill_space (1);
    enforce_corradiality (0); 
    Eades (0); 
    Eades_avoid_crossing_edges (2); 
    Eades_border_leaves (1); 
}

// method GT_Layout_Radial_Algorithm::run

int GT_Layout_Radial_Algorithm::run (GT_Graph& g)
{
  graph* l_graph; // (fb)
  edge e; //(fb)
  GT_Polyline line; // (fb)

    // check on empty graph
    if (g.attached()->number_of_nodes() < 1 ) {
	return GT_OK;
    }

    // remove bends from edges (fb)
    l_graph = g.attached();
    forall_edges(e, *(l_graph)){
      line = g.gt(e).graphics()->line();
      if (line.size() > 2) {
	line.erase (++line.begin(), --line.end());
      }
      g.gt(e).graphics()->line(line);
    }

    // reset edge anchors from every edge to the center of the node (fb)
    reset_NEIs (g);
    
    // instantiate algorithm
    Layout_Radial_Algorithm_Implementation* current_algorithm =
	new Layout_Radial_Algorithm_Implementation();

    current_algorithm->set_graph(&g);

    // class Layout_Radial_Algorithm_Implementation 
    // is taylored for directed graphs !!!!!

    // if an undirected graph is passed by graphlet
    // we call a method which will direct it
    // before any other method will try to process it

    if (g.attached()->is_undirected())
    {
	if (debug) cout << "UNDIRECTED GRAPH" << endl;
	current_algorithm->digraph();
    }

    // set translation parameters

    //	current_algorithm->set_hshift(hshift);
    //	current_algorithm->set_vshift(vshift);

    // determine shape of nodes
    // now inside method now_just_do_it
    //	current_algorithm->determine_shape_of_nodes();

    current_algorithm->set_how_to_select_root(root_selection());

    // determine root
    // now inside method now_just_do_it
    //	current_algorithm->determine_root();

    // set parameter for minimum parent-child-distance and padding factor

    current_algorithm->set_parent_child_distance(parent_child_distance());

    current_algorithm->set_padding_factor(menu_padding_factor());

    // set switch for automatic expansion
    automatic_expansion (0);	// shall always be zero (on)

    current_algorithm->set_automatic_expansion(automatic_expansion());

    // set switch for rigid/flexible parent-child-distance

    current_algorithm->set_distance_rigid_flexible_flag(distance_rigid_flexible_flag());

    current_algorithm->set_padding_type(padding_type());

    // set parameter for handling of escaping edges

    current_algorithm->set_avoid_escaping_edges(1 - avoid_escaping_edges());

    current_algorithm->set_avoid_collinear_families(1 - avoid_collinear_families());

    // set parameter for centering father over sons

    current_algorithm->set_center_parent(1 - center_parent());

    // set parameter for centering children under parent

    current_algorithm->set_center_children(1 - center_children());

    // variant of global symmetry

    current_algorithm->set_fill_space(1 - fill_space());

    // set parameter for enforcing corradial nodes

    current_algorithm->set_enforce_corradial_nodes(enforce_corradiality());

    // Tano option : Eades original algorithm

    //	current_algorithm->set_eades(eades);

    //	current_algorithm->set_eades2(eades2);

    //	current_algorithm->set_eades_straight(eades_straight);

    //	current_algorithm->set_eades_pi(eades_pi);

    // Eades parameters : final version

    current_algorithm->set_Eades(Eades());
    current_algorithm->set_Eades_avoid_crossing_edges(Eades_avoid_crossing_edges());
    current_algorithm->set_Eades_border_leaves(Eades_border_leaves());



    // set parameter for debugging on/off

    // NOW, as the debugger shall be no longer active
    // set it always off = 1

    current_algorithm->set_debug(1);

    // launch algorithm

    current_algorithm->now_just_do_it();

    return GT_OK;
}




// method GT_Layout_Radial_Algorithm::check

int GT_Layout_Radial_Algorithm::check (GT_Graph& g, string& message)
{

    // check on empty graph

    if (g.attached()->number_of_nodes() < 1 ) {
	message = "This graph is empty";
	return GT_OK;
    }

    if (check_directed_tree(g, message, the_nodes, the_edges)) {
	return GT_OK;
    }

    return GT_ERROR;
}



//
// class GT_Tcl_Layout_Radial_Algorithm
//




GT_Tcl_Layout_Radial_Algorithm::GT_Tcl_Layout_Radial_Algorithm (
    const string& name) :
        GT_Tcl_Algorithm<GT_Layout_Radial_Algorithm> (name)
{
}



GT_Tcl_Layout_Radial_Algorithm::~GT_Tcl_Layout_Radial_Algorithm ()
{
}




// method GT_Tcl_Layout_Radial_Algorithm::parse


int GT_Tcl_Layout_Radial_Algorithm::parse (GT_Tcl_info& info, int& index,
    GT_Tcl_Graph* /* g */)
{
    int code = TCL_OK;
    int value;
    
    if(info.argv(index)[0] == '-') {

        if (!strcmp(info.argv(index),"-root")) {
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    root_selection (value);
	} else if (!strcmp(info.argv(index),"-father_son")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    parent_child_distance  (value);
	} else if (!strcmp(info.argv(index),"-rigid_distance")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    distance_rigid_flexible_flag (value);
	} else if (!strcmp(info.argv(index),"-padding_factor")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    menu_padding_factor (value);
	} else if (!strcmp(info.argv(index),"-padding_type")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    padding_type (value);
	} else if (!strcmp(info.argv(index),"-escaping_edges")){
	    code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    avoid_escaping_edges (value);
	} else if (!strcmp(info.argv(index),"-collinear_families")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    avoid_collinear_families (value);
	} else if (!strcmp(info.argv(index),"-center_parent")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    center_parent (value);
	} else if (!strcmp(info.argv(index),"-center_children")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    center_children (value);
	} else if (!strcmp(info.argv(index),"-fill_space")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    fill_space (value);
	} else if (!strcmp(info.argv(index),"-corradiality")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    enforce_corradiality (value);
	} else if (!strcmp(info.argv(index),"-eades")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    Eades (value);
	} else if (!strcmp(info.argv(index),"-eades_avoid_crossing")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    Eades_avoid_crossing_edges (value);
	} else if (!strcmp(info.argv(index),"-eades_border_leaves")){
            code = Tcl_GetInt (info.interp(), info.argv(++index), &value);
	    Eades_border_leaves (value);
	}
    }

    index++;
    return code;
}




// function for checking if graph is tree


bool graph_is_tree(const graph& g)
{
	
    // first
    // check correct relationship between number of nodes and edges

    if (g.number_of_nodes() - 1 != g.number_of_edges())
	return false;

    // second
    // must be connected
    if (!g.is_connected())
	return false;

    // ok
    return true;
}
