/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// tree_layout.h                                             //
//                                                           //
// This file implements the the connection between the c++ - //
// part and the tcl-part of the tree algorithm.              //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/tree_layout.h                 //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/tree_algorithm.h,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/03/05 20:47:26 $
// $Locker:  $
// $State: Exp $


#ifndef TREE_LAYOUT_H
#define TREE_LAYOUT_H

//////////////////////////////////////////
//
// class GT_Layout_Tree_Algorithm
//
// This is a sample Tcl/LEDA algorithm for tree layout.
//
//////////////////////////////////////////


class GT_Extended_TR : public GT_Algorithm {

protected:
    int leveling;
    int orientation;
    int direction;
    int routing;
    int father_place;
    int permutation;
    int vert_node_node_dist;
    int hor_node_node_dist;
    int node_edge_dist;
    int channel_width;
    int edge_connection;
    int bend_reduction;
    int edge_connection_for_bend;
    int marked_root;
    int root_id;
    list<node> the_nodes;
    list<edge> the_edges;
	
public:
    GT_Extended_TR (const string& name);
    virtual ~GT_Extended_TR ();
	
    virtual int run (GT_Graph& g);
    virtual int check (GT_Graph& g, string& message);
};


class GT_Tcl_Extended_TR :
    public GT_Tcl_Algorithm<GT_Extended_TR>
{
    
public:

    GT_Tcl_Extended_TR (const string& name);
    virtual ~GT_Tcl_Extended_TR ();
 
    virtual int parse (GT_Tcl_info& info, int& index, GT_Tcl_Graph* g);
    virtual int check (GT_Graph& g, string& message) {
	int code = GT_Extended_TR::check (g, message);
	if (!the_nodes.empty()) {
	    result (g, the_nodes);
	}
	if (!the_edges.empty()) {
	    result (g, the_edges);
	}

	if (the_nodes.empty() && the_edges.empty()) {
	    result (" ");
	}

	the_nodes.clear();
	the_edges.clear();
 	return code;
    } 
};


#endif
