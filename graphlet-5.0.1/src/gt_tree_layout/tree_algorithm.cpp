/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// tree_algorithm.cpp                                        //
//                                                           //
// This file implements the the connection between the c++ - //
// part and the tcl-part of the tree algorithm.              //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/tree_algorithm.cpp            //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/tree_algorithm.cpp,v $
// $Author: himsolt $
// $Revision: 1.8 $
// $Date: 1999/03/05 20:47:25 $
// $Locker:  $
// $State: Exp $

#include <iostream>

#include <gt_base/Graphlet.h>

#include <GTL/graph.h>
#include <GTL/node.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>
#include <gt_base/NEI.h>

#include "tree_algorithm.h"
#include "tree_check.h"
//#include "checks.h"
#include "tree_structure.h"


extern "C"
{
#include <string.h>
#include <stdlib.h>
}

/******************/
/* predefinitions */
/******************/
node determine_root(GT_Graph& g);
node determine_center (GT_Graph& g);
int determine_sum (graph* l_graph, node* v, node* grandpa, int level);
//////////////////////////////////////////
//
// class GT_Extended_TR
//
// This is a Tcl/LEDA algorithm for tree layout.
//
//////////////////////////////////////////


//
// This is the constructor of class GT_Extended_TR.
//
// The parameter name is both used as the name of the
// algorithm and the name of the GraphScript command.
//


GT_Extended_TR::GT_Extended_TR (const string& name) :
	GT_Algorithm (name)
{
    leveling = Local;
    orientation = Top;
    direction = TopBottom;
    routing = Straightline;
    father_place = Median;
    permutation = KeepIt;
    vert_node_node_dist = 20;
    hor_node_node_dist = 16;
    node_edge_dist = 50;
    channel_width = 3;
    edge_connection = 70;
    bend_reduction = Enabled;
    edge_connection_for_bend = 90;
    marked_root = 1;
    root_id = -1;
}

GT_Extended_TR::~GT_Extended_TR()
{
}


//
// int GT_Extended_TR::run (GT_Graph& g)


int GT_Extended_TR::run (GT_Graph& g)
{
    
    // check on empty graph
    if (g.attached()->number_of_nodes() < 1 ) {
	return GT_OK;
    }
    
    GT_TreeAlgo* treealgo = new GT_TreeAlgo();
	node root;

	GT_Graph* gt_graph;
	graph* l_graph;
	node v;
	double width = 0.0;
	double height = 0.0;
	
	GT_Polyline line;
	edge e;

	//reset NEI
	reset_NEIs(g);

	// determine the root of the tree
//   	cout << "bestimme root" << endl;
	if((*g.attached()).is_undirected() == true && root_id != -1 && marked_root == 0){
// 	    root = *root_node;
	    root = g.node_by_id(root_id);
	} else {
	    root = determine_root(g);
	}
// 	cout << root << endl;
// 	cout << &root << endl;
//   	cout << "setze root" << endl;
	treealgo->set_root(root);
// 	cout << treealgo->get_root() << endl;
// 	cout << "ROOT: " << g.gt((treealgo->get_root())).label() << endl;
// 	cout << "ende setzen" << endl;
	// set options and the graph
//   	cout << "setze optionen und graph" << endl;
	treealgo->set_options(leveling,
 	    orientation,
	    direction,
	    routing,
	    father_place,
	    permutation,
	    vert_node_node_dist,
	    hor_node_node_dist,
	    node_edge_dist,
	    channel_width,
	    edge_connection,
	    bend_reduction,
	    edge_connection_for_bend);
// 	cout << "setze graph" << endl;
	treealgo->set_graph(&g);

// 	cout << "setze order" << endl;
 	treealgo->compute_order_of_sons();
// 	cout << "ende order" << endl;
	
	gt_graph = treealgo->get_graph();
	l_graph = gt_graph->attached();

// 	cout << "Koordinatenaenderung" << endl;
	//if the drawing direction is left->right, change width and
	//height of all nodes
	//the same for right/left!
	if(this->direction == LeftRight ||
	   this->direction == RightLeft){
		forall_nodes(v, *(l_graph)){
			height = gt_graph->gt(v).graphics()->h();
			width = gt_graph->gt(v).graphics()->w();
			gt_graph->gt(v).graphics()->h(width);
			gt_graph->gt(v).graphics()->w(height);
		}
	}
// 	cout << "Koordinatenaenderung ende" << endl;


	//reset the edges (so that we have no bends for straightline
	//drawing; the bends for orthogonal drawing are insertet later)
// 	cout << "Loeschen der Knicke" << endl;
	forall_edges(e, *(l_graph)){
// 	    line.push_back (gt_graph->gt(e).graphics()->line().front());
// 	    line.push_back (gt_graph->gt(e).graphics()->line().back());
	    line = gt_graph->gt(e).graphics()->line();
	    if (line.size() > 2) {
		line.erase (++line.begin(), --line.end());
	    }
	    gt_graph->gt(e).graphics()->line(line);
// 	    line.pop_back ();
// 	    line.pop_back ();
	}
// 	cout << "Loeschen der Knicke ende" << endl;

	// compute the y-coordinates for all nodes
//   	cout << "vor Aufruf von compute_levels" << endl;
	treealgo->compute_levels();
//  	cout << "nach Aufruf von compute_levels" << endl;
//  	treealgo->print_treeinfo();

	//compute the deepest y-coord beginning at a node
//  	cout << "vor Aufruf von compute_deepest_y_coord" << endl;
	treealgo->compute_deepest_y_coord(root);
//   	cout << "nach Aufruf von compute_deepest_y_coord" << endl;

	// call of the algorithm
//  	cout << "vor Aufruf von tree_algorithm" << endl;
	treealgo->tree_algorithm();
//  	cout << "FERTIG" << endl;

	//free treealgo
	delete(treealgo);
	
	return GT_OK;
	
}
	

//
// Check conditions on the graph
//

int GT_Extended_TR::check (GT_Graph& g, string& message)
{

    // check on empty graph
    if (g.attached()->number_of_nodes() < 1 ) {
	message = "The graph is empty.";
	return GT_OK;
    }

    if (check_tree(g, message, the_nodes, the_edges)) {
	return GT_OK;
    }

    return GT_ERROR;

}



//////////////////////////////////////////
//
// GT_Tcl_Extended_TR (Tcl-Wrapper)
//
//////////////////////////////////////////


GT_Tcl_Extended_TR::GT_Tcl_Extended_TR (const string& name) :
	GT_Tcl_Algorithm<GT_Extended_TR> (name)
{
}


GT_Tcl_Extended_TR::~GT_Tcl_Extended_TR ()
{
}


//
// parse
//

int GT_Tcl_Extended_TR::parse (GT_Tcl_info& info, int& index,
    GT_Tcl_Graph* /* g */)
{
    int code = TCL_OK;

    if(info.argv(index)[0] == '-') {

        if(!strcmp(info.argv(index),"-leveling")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &leveling);
        } else if(!strcmp(info.argv(index), "-orientation")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &orientation);
        }else if(!strcmp(info.argv(index), "-direction")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &direction);
        }else if(!strcmp(info.argv(index), "-routing")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &routing);
        }else if(!strcmp(info.argv(index), "-father_place")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &father_place);
        }else if(!strcmp(info.argv(index), "-permutation")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &permutation);
        }else if(!strcmp(info.argv(index), "-father_son_dist")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &vert_node_node_dist);
        }else if(!strcmp(info.argv(index), "-son_son_dist")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &hor_node_node_dist);
        }else if(!strcmp(info.argv(index), "-node_edge_dist")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &node_edge_dist);
        }else if(!strcmp(info.argv(index), "-channel_width")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &channel_width);
        }else if(!strcmp(info.argv(index), "-edge_connection")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &edge_connection);
        }else if(!strcmp(info.argv(index), "-bend_reduction")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &bend_reduction);
        }else if(!strcmp(info.argv(index), "-edge_connection_for_bend")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &edge_connection_for_bend);
        }else if(!strcmp(info.argv(index), "-marked_root")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &marked_root);
        }else if(!strcmp(info.argv(index), "-root_id")) {
	  code = Tcl_GetInt (info.interp(), info.argv(++index), &root_id);
        }

        if (code != TCL_OK) {
            return code;
        }
    }
    index++;

    return code;

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&leveling);
//     if (code != TCL_OK) {
// 	return code;
//     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&orientation);
//     if (code != TCL_OK) {
// 	return code;
//     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&direction);
//     if (code != TCL_OK) {
// 	return code;
//     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&routing);
//     if (code != TCL_OK) {
// 	return code;
//     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&father_place);
//     if (code != TCL_OK) {
// 	return code;
//     }

// //     code = Tcl_GetInt (info.interp(), info.argv(index++),
// // 	&permutation);
// //     if (code != TCL_OK) {
// // 	return code;
// //     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&vert_node_node_dist);
//     if (code != TCL_OK) {
// 	return code;
//     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&hor_node_node_dist);
//     if (code != TCL_OK) {
// 	return code;
//     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&node_edge_dist);
//     if (code != TCL_OK) {
// 	return code;
//     }

//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&channel_width);
//     if (code != TCL_OK) {
// 	return code;
//     }
    
//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&edge_connection);
//     if (code != TCL_OK) {
// 	return code;
//     }
    
//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&bend_reduction);
//     if (code != TCL_OK) {
// 	return code;
//     }
    
//     code = Tcl_GetInt (info.interp(), info.argv(index++),
// 	&edge_connection_for_bend);
//     if (code != TCL_OK) {
// 	return code;
//     }
    
//     return code;
}



node determine_root(GT_Graph& g)
{
    node root;
    node v;
    
    if((*g.attached()).is_undirected() == true){
//       cout << "vor determine_center" << endl;
      root = determine_center(g);
//       cout << "root: " << g.gt(root).label() << endl;
    } else {
      forall_nodes(v, *(g.attached())){
	if(v.indeg() == 0){
	    root = v;
	}
      }
    }   
    return root;
}

node determine_center (GT_Graph& g)
{
  graph* l_graph = g.attached();
  node center;
  int minsum = 0;
  node v;
  int sum;


  forall_nodes(v, *l_graph){
//     cout << "---------- Anfangsknoten: " << g.gt(v).label() << "------------" << endl;
    sum = determine_sum (l_graph,&v,0,1);
//     cout << "Summe: " << sum << endl;
    if(sum < minsum || minsum == 0){
//       cout << "Ersetze Knoten" << endl;
      minsum = sum;
      center = v;
    }
//     cout << "Ende fuer diesen Knoten" << endl;
  }
//   cout << "Ende determine_center" << endl;
  return center;
}

int determine_sum (graph* l_graph, node* v, node* grandpa, int level)
{
  int sum = 0;
  node i;
  node::adj_nodes_iterator it, end;

  it = v->adj_nodes_begin();
  end = v->adj_nodes_end();

  while (it != end) {
    i = *it;
    if(grandpa == 0){
//       cout << "Grandpa = 0" << endl;
      sum = sum + level + determine_sum (l_graph, &i, v, level+1);
//       cout << "  level: " << level << endl;
//       cout << "  sum: " << sum << endl;
    } else {
//       cout << "Grandpa != 0" << endl;
      if(i != *grandpa){
// 	cout << "i != grandpa" << endl;
	sum = sum + level + determine_sum (l_graph, &i, v, level+1);
// 	cout << "  level: " << level << endl;
// 	cout << "  sum: " << sum << endl;
      }
      else {
// 	cout << "i == grandpa" << endl;
      }
    }
    ++it;
  }
//   cout << "Returnwert: " << sum << endl;
  return sum;
}
