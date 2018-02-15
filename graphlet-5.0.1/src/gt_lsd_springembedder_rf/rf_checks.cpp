/* This software is distributed under the Lesser General Public License */
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_lsd_springembedder_rf/rf_checks.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:45:44 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project, Walter Bachl
//

// #include <gt_base/Graphlet.h>
#include <GTL/graph.h>
#include <GTL/node_map.h>


char *spring_rf_is_correct (graph &g)              
{
     // check on empty graph
    if (g.number_of_nodes() < 1 ) {
	return "";
    }

   
    node_map<bool> Map;
    node cur_node, v;
    edge e;
    list<node> node_list;
    
    // can we mark all edges if we start from the root?
    Map.init(g, false);
    cur_node = g.choose_node();
    node_list.push_back(cur_node);
    Map[cur_node] = true;
    while(!node_list.empty()){
	cur_node = node_list.front();
	node_list.pop_front();

	forall_inout_edges(e, cur_node){
	    v = cur_node.opposite (e);
 
	    if (Map[v] == false) {
		Map[v] = true;
		node_list.push_back(v);
	    }
	}
    }
    
    forall_nodes(v, g){
        if(Map[v] == false){
            return "The graph is not connected!";
        }
    }
    
    
//     if (multiple_connected_nodes(graph)) {
//  	return "The graph contains a multiple edge!";

//     }

    return "";
}	 
