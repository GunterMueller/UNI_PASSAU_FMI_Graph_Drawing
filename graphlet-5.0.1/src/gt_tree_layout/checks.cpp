/* This software is distributed under the Lesser General Public License */
//***********************************************************//
// checks.cpp                                                //
//                                                           //
// This file checks the preconditions for the tree algo.     //
//                                                           //
//                                                           //
// Source: /home/br/CVS/graphlet/src/gt_algorithms/          //
//                 tree_layout/checks.cpp                    //
// Author: Wetzel Sabine                                     //
//                                                           //
// (C) University of Passau 1995-1999, graphlet Project      //
//                                                           //
//***********************************************************//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/checks.cpp,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:46:54 $
// $Locker:  $
// $State: Exp $

#include <gt_base/Graphlet.h>

#include <GTL/node_map.h>
#include "checks.h"

//*************************************************************//
// predefinitions                                              //
//*************************************************************//
int nr_of_roots(graph& g);
bool graph_is_connected(graph& g);


//*************************************************************//
// function: check_on_tree                                     //
// description: This function checks whether the given graph is//
//      a tree or not. It returns the empty string, if the     //
//      graph is a tree or detailed information if it is not a //
//      tree.                                                  //
//*************************************************************//
char* check_on_tree(graph& g)
{
    //check whether the graph is undirected or not
//     if(g.is_undirected() == true){
// 	return "Graph is undirected.";
//     }

    //check if the graph is connected
//     if(nr_of_roots(g) != 0){
	if(graph_is_connected(g) == false){
	    return "The graph is not a tree. \n It is not connected.";
	}
//     }

    //check if the graph has several roots
    if(g.is_directed() == true && nr_of_roots(g) != 1){
	return "The graph is not a tree. \n It has several roots or no root.";
    }

    //check if the graph has too many edges
    if(g.number_of_edges() != g.number_of_nodes() - 1){
	return "The graph is not a tree. \n It has too many edges.";
    }
    
    return "";
}

//*************************************************************//
// function: nr_of_roots                                       //
// description: This function determines the number of roots   //
//      (= number of nodes with 0 inedges). nr_of_roots must   //
//      be 1 for the tree algorithm.                           //
//*************************************************************//
int nr_of_roots(graph& g)
{
    int number = 0;
    node v;
    
    forall_nodes(v, g){
	if(v.indeg() == 0){
	    number = number + 1;
	}
    }
    return number;
}

//*************************************************************//
// function: graph_is_connected                                //
// description: This functions checks, whether the graph is    //
//      connected or not.                                      //
//      We assume, that the graph has at least one node.       //
//*************************************************************//
bool graph_is_connected(graph& g)
{
    return g.is_connected();
//     node v;
//     edge e;
//     node cur_node;
//     node one_node;
//     node_map<bool> Map;
//     list<node> node_list;
   
//     Map.init(g, false);

//     //chose one node
//     one_node = g.choose_node();
//     Map[one_node] = true;
//     node_list.push_back (one_node);

//     while(!node_list.empty()){
// 	//delete the first element of the list
// 	cur_node = node_list.front ();
// 	node_list.pop_front ();
// 	//cur_node = node_list.del_item(node_list.first());
// 	forall_inout_edges(e, cur_node){
// 	    v = g.opposite(cur_node, e);
// 	    if(Map[v] == false){
// 		node_list.push_back(v);
// 		Map[v] = true;
// 	    }
// 	}
//     }

//     //
//     // check if all nodes are marked
//     //

//     forall_nodes(v, g){
// 	if(Map[v] == false){
// 	    return false;
// 	}
//     }

//     return true;
}
