/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// tree_check.cpp                                            //
//                                                           //
// This file checks preconditions for tree algorithms.       //
//                                                           //
//                                                           //
// Author: Walter Bachl                                      //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/tree_check.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:47:28 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>
#include <GTL/node_map.h>
#include <GTL/graph.h>
#include <GTL/node.h>
#include "tree_check.h"



// ************************************************
// unique_root (g, error_msg)
//
// Precondition: The graph is directed
//
// If an error occurs, then the error description is stored in error_msg.
// Additionally all possible root nodes are marked.
//
// return: TRUE  iff g has an unique root
//         FALSE else
// ************************************************

bool unique_root (const GT_Graph& gt, string &error_msg, list<node> &nodes)
{
    int number = 0;
    node v;
    graph *g = gt.attached();

    forall_nodes(v, *g){
	if(v.indeg() == 0){
	    number = number + 1;
	}
    }

    
    if (number == 0) {
	error_msg = "The graph is not a tree: \n\
It contains no node without incoming edges.";
	return false;
    }
    if (number > 1) {
	error_msg = "The graph is not a tree: \n\
It has more then one node without incoming edges which are selected.";

	forall_nodes(v, *g){
	    if(v.indeg() == 0){
		nodes.push_back (v);
	    }
	}

	return false;
    }

    return true;
}

// ************************************************
// connected (g, error_msg)
//
// Precondition: None
//
// If an error occurs, then the error description is stored in error_msg.
// Additionally one componenent is marked.
//
// return: TRUE  iff g has is connected
//         FALSE else
// ************************************************

bool connected (const GT_Graph& gt, string &error_msg, list<node> &nodes)
{
    graph *g = gt.attached();

    if (g->is_connected()) {
	return true;
    }

    // Mark nodes starting at an arbitrary node.
    node v = g->choose_node();
    node w;
    edge e;
    list<node> node_list;
    node_list.push_back(v);

    node_map<bool> map(*g, false);
    map[v] = true;

    while (!node_list.empty()){
	v = node_list.front ();
	node_list.pop_front ();
	forall_inout_edges(e, v){
	    w = v.opposite(e);
	    nodes.push_back (w);
 	    if(map[w] == false) {
 		node_list.push_back(w);
 		map[w] = true;
	    }
 	}
    }


    error_msg = "The graph is not a tree:\n\
It is not connected. One of its components is selected.";
    return false;
}

// ************************************************
// superfluous_edge (g, error_msg)
//
// Precondition: none
//
// Check whether there is one edge which is not allowed in a tree.
// If an error occurs, then the error description is stored in error_msg.
// Additionally the first superfluous edge which is found by a bfs 
// is marked. 
// Chooses one node to start with and performs a bfs on the (underlying) 
// undirected graph.
//
// return: TRUE  iff g has no superfluous edge.
//         FALSE else
// ************************************************

bool no_superfluous_edge (const GT_Graph& gt, string &error_msg, list<edge> &edges)
{
    graph *g = gt.attached();
    node w;
    int count;
    edge e;
    node v = g->choose_node();

    list<node> node_list;
    node_list.push_back(v);

    node_map<bool> map(*g, false);
    map[v] = true;

    while (!node_list.empty()){
	v = node_list.front ();
	node_list.pop_front ();
	count = 0;
	forall_inout_edges(e, v){
	    w = v.opposite(e);
 	    if(map[w] == false) {
 		node_list.push_back(w);
 		map[w] = true;
	    } else {
		if (count == 0) {
		    // edge to father. Ignore it.
		    count = 1;
		} else {
		    edges.push_back(e);
		    error_msg = "The graph is not a tree:\n\
It has too many edges. One possibly bothering edge is selected.";
		    return false;
		}
	    }
 	}
    }

    return true;
}

// ************************************************
// edge_orientation (g, error_msg)
//
// Precondition: g is directed
//
// Check whether there is an edge which wrong oriented.
// If an error occurs, then the error description is stored in error_msg.
// Additionally the first wrong oriented edge which is found by a bfs 
// is marked.
//
// If unique_root and connected is checked before, then this function is 
// unneccessary.
//
// return: TRUE  iff g has no wrong oriented edge.
//         FALSE else
// ************************************************

bool edge_orientation (const GT_Graph& gt, string &error_msg, list<node> &nodes)
{
    graph *g = gt.attached();
    node v;
    bool error_found = false;

    // find root
    forall_nodes(v, *g) {
	if(v.indeg() > 1) {
	    // MARKIEREN
	    nodes.push_back (v);
	    error_found = true;
	}
    }

    if (error_found == true) {
	error_msg = "The graph is not a tree:\n\
It containes at least one wrong oriented edge which is selected";
	return false;
    }
 
    return true;
}


bool check_tree (const GT_Graph& gt, string &error_msg, list<node> &nodes, 
    list<edge> &edges)
{
    graph *g = gt.attached();

   
    if(g->is_directed()) {
	// connected
	if (!connected (gt, error_msg, nodes)) {
	    return false;
	}

	if (!unique_root (gt, error_msg, nodes)) {
	    return false;
	}

	if (!no_superfluous_edge (gt, error_msg, edges)) {
	    return false;
	}
	return true;
    }

    // g is not directed
    if (!connected (gt, error_msg, nodes)) {
	return false;
    }

    if (!no_superfluous_edge (gt, error_msg, edges)) {
	return false;
    }
    
    return true;
}


bool check_directed_tree (const GT_Graph& gt, string &error_msg, list<node> &nodes,
    list<edge> &edges)
{
    graph *g = gt.attached();

    if(!g->is_directed()) {
	error_msg = "The graph is not directed. This algorithm only works with directed trees.";
	return false;
    }

    if (!connected (gt, error_msg, nodes)) {
	return false;
    }

    if (!unique_root (gt, error_msg, nodes)) {
	return false;
    }

    if (!no_superfluous_edge (gt, error_msg, edges)) {
	return false;
    }


    return true;
}
