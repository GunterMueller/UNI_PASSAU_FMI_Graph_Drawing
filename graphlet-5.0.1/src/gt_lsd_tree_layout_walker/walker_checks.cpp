/* This software is distributed under the Lesser General Public License */
#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

//#include <LEDA/graph.h>
#include <GTL/node_map.h>

// ----------------------------------------------------------------------------
// Walter: is_correct is not used any more.
// New function is leda_is_correct.
// Now works on ledagraph since we don't have to copy all structures in this
// case.
// Memory allocation is now done in memory_allocate
// ----------------------------------------------------------------------------

char *walker_is_correct (graph &g)              
{
    // check on empty graph
    if (g.number_of_nodes() < 1 ) {
	return "";
    }

    bool has_root;
    node_map<bool> Map;
    node root;
    node cur_node;
    node v;
    list<node> node_list;
    
    
    Map.init(g, false);

    // look for root
    has_root = false;
    forall_nodes(v, g){
	if(v.indeg() == 0){
	    root = v;
	    has_root = true;
	}
    }
    if(!has_root) {
	return "The graph has no root!";
    }

    // can we mark all edges if we start from the root?
    Map[root] = true;
    node_list.push_back(root);
    
    edge e;

    while(!node_list.empty()){
	cur_node = node_list.front ();
	node_list.pop_front();

	forall_adj_edges (e, cur_node) {
	    v = cur_node.opposite(e);

	    // MR: Map[v] == true ist't a sufficient condition for the existence
	    // of a cycle. Since the graph can contain multiedges and selfloops 
	    // (they will be hidden later), Map[v] will be true, but there isn't 
	    // any cycle !

	    if (Map[v] == true) {
		return "The graph contains a cycle!";
	    }
	    Map[v] = true;
	    node_list.push_back(v);
	}
    }

    forall_nodes(v, g){
	if(Map[v] == false){
	    return "The graph is not connected!";
	}
    }

    // check number of edges
    if (g.number_of_nodes() - 1 < g.number_of_edges()) {
	return "This graph is no tree because the graph has to much edges!";
    }
    if (g.number_of_nodes() - 1 > g.number_of_edges()) {
	return "This graph is no tree because the graph has not enough edges!";
    }

    return "";
}
