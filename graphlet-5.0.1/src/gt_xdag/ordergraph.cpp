/* This software is distributed under the Lesser General Public License */
//
// ordergraph.cpp
//
// This file implements the class Order_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/ordergraph.cpp,v $
// $Author: himsolt $
// $Revision: 1.1.1.1 $
// $Date: 1998/08/27 17:19:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//


#include <list>

#include "ordergraph.h"
#include "level.h"


// ============================================================================
//
// class Order_Graph
//
// ============================================================================

//
// constructor
//

Order_Graph::Order_Graph (int nn_dist, int ne_dist, int ee_dist)
{
    order(new graph);
    the_dag_node.init(*order(),0);
    the_distance.init(*order(),0.0);

    min_node_node_distance(nn_dist);
    min_node_edge_distance(ne_dist);
    min_edge_edge_distance(ee_dist);
}


//
// destructor
//

Order_Graph::~Order_Graph ()
{
    delete order();
}


//
// relation
//
// This function inserts a relation between the two nodes dn_left and dn_right
// into the order graph [relation(dn_left,dn_right,...) means that dn_left
// has to be drawn to the left of dn_right in our final layout!]. First we
// insert the two nodes dn_left and dn_right into the order graph by calling
// the function insert. Then we insert an edge between the nodes correspond-
// ing to dn_left and dn_right and set the weight of the new edge to the
// value dist, which means that the distance between the nodes dn_right and
// dn_left has to be at least dist units.
//

void Order_Graph::relation (DAG_Node* dn_left, DAG_Node* dn_right, double dist)
{
    node n1 = insert(dn_left);
    node n2 = insert(dn_right);

    //
    // We have to guarantee that there is no edge (n2,n1) in the order graph,
    // because this would lead to a cycle after inserting edge (n1,n2) and
    // hence it would be impossible to sort the order graph togologically.
    //

    node::adj_nodes_iterator it = find (n2.adj_nodes_begin(), n2.adj_nodes_end(), n1);
    assert (it == n2.adj_nodes_end());

//     assert (order()->adj_nodes(n2).search(n1) == 0);

    //
    // Then we test whether there is already an edge between n1 and n2. If
    // we find a matching edge, we won't do anything (not even correcting the
    // edge's length, because it has already been set to the right value on
    // a previous level). Otherwise we insert a new edge and set its weight.
    //

    edge e;
    it = n1.adj_nodes_begin();
    node::adj_nodes_iterator end = n1.adj_nodes_end();

    while (it != end) {
	if (*it == n2) {
	    break;
	}
	++it;
    }

    if (it == end) {
	e = order()->new_edge(n1, n2);
	dist += (dn_left->width() + dn_right->width())/2.0;
	distance(e, dist);
    }

//     edge e, ident = 0;
//     list<edge> edges = order()->adj_edges(n1);
//     forall(e,edges) {
// 	if (target(e) == n2) {
// 	    ident = e;
// 	}
//     }

//     if (ident == 0) {
// 	e = order()->new_edge(n1, n2);
// 	dist += (dn_left->width() + dn_right->width())/2.0;
// 	distance(e, dist);
//     }
}


//
// insert
//
// This function checks if there is already a node in the order graph
// which corresponds to the DAG_Node 'dn'. If such a node exists, we
// return this node to the caller. Otherwise, we insert a node into
// the order graph, update the data structures and return the new node
// to the caller.
//

node Order_Graph::insert (DAG_Node* dn)
{
    if (the_order_node.find(dn) ==  the_order_node.end()) {
	node n = order()->new_node();
	dag_node(n, dn);
	order_node(dn, n);
	return n;
    } else { 
	return the_order_node[dn];
    }
 
//     if (!the_order_node.defined(dn)) {
// 	node n = order()->new_node();
// 	dag_node(n, dn);
// 	order_node(dn, n);
//     }
}


//
// compute_leftmost_xcoord
//
// This function calculates the leftmost possible x-coordinate for node v
// which depends on the predecessors of v in the order graph and the node
// distances to each other. These predecessors in the order graph are
// exactly those nodes in the original DAG that collide with v, because for
// each predecessor pred in the order graph there is at least one level on
// which both v and pred are situated. By means of min_x we obtain that no
// node has an x-coordinate less than min_x (=x-coordinate  of the leftmost
// node on the canvas before starting the DAG algorithm), so that we "stay"
// on the visible part of the canvas when animating the graph during compu-
// tation.
//

double Order_Graph::compute_leftmost_xcoord (node v, double min_x)
{
    double x = min_x;
    edge in_edge;
    node pred;

    forall_in_edges (in_edge,v) {
	pred = in_edge.source();
	if (x < xcoord(pred)+distance(in_edge))
	    x = xcoord(pred)+distance(in_edge);
    }
	

//     forall_in_edges (in_edge,v) {
// 	pred = source(in_edge);
// 	if (x < xcoord(pred)+distance(in_edge))
// 	    x = xcoord(pred)+distance(in_edge);
//     }

    return x;
}


//
// include
//
// This function includes the Level 'level' into the order graph, i.e. all
// nodes of 'level' are inserted into the order graph and connected with
// edges according to their order on the level.
//

void Order_Graph::include (const Level& level)
{
    double dist;
    DAG_Node *dn1, *dn2;
//     list_item item1, item2;

    //
    // If there is only one DAG_Node on the level, we cannot insert a
    // relation. Thus we have to insert that node separately. This is
    // necessary be make sure that every DAG_Node has a corresponding
    // order node.
    //

//     if (level.size()==1) {
// 	insert(level.head());
// 	return;
//     }

    if (level.size()==1) {
	insert(level.front());
	return;
    }

    //
    // However, if there is more than one node on a level, we insert
    // relations between each pair of neighbored nodes.
    //
    
    list<DAG_Node*>::const_iterator it, succ;
    list<DAG_Node*>::const_iterator end = level.end();

    for (it = level.begin(); it != end; ++it) {
	succ = it;
	++succ;
	
	if (succ != end) {
	    dn1 = *it;
	    dn2 = *succ;

	    if (dn1->is_dummy() && dn2->is_dummy()) {
		dist = min_edge_edge_distance();
	    }
	    else if (dn1->is_dummy() || dn2->is_dummy()) {
		dist = min_node_edge_distance();
	    }
	    else {
		dist = min_node_node_distance();
	    }
	    relation(dn1, dn2, dist);
	}
    }


//     forall_items (item1, level) {
// 	item2 = level.succ(item1);
// 	if (item2) {
// 	    dn1 = level[item1];
// 	    dn2 = level[item2];
// 	    if (dn1->is_dummy() && dn2->is_dummy()) {
// 		dist = min_edge_edge_distance();
// 	    }
// 	    else if (dn1->is_dummy() || dn2->is_dummy()) {
// 		dist = min_node_edge_distance();
// 	    }
// 	    else {
// 		dist = min_node_node_distance();
// 	    }
// 	    relation(dn1, dn2, dist);
// 	}
//     }
}


//
// reorder
//
// This functions computes new x-coordinates for all nodes in the order
// graph by running a top sort algorithm on this graph.
//

void Order_Graph::reorder (double leftmost)
{
//     node_array<int> INDEG(*order(),0);
    node_map<int> INDEG(*order(),0);
    list<node>      ZEROINDEG;
    node v, w;

    //
    // find all nodes without any predecessors
    //

    forall_nodes (v, *order()) {
	if ( (INDEG[v]=v.indeg()) == 0 )
	    ZEROINDEG.push_back(v);
    }

    //
    // visit all nodes without any unvisited predecessors
    //

    node::adj_nodes_iterator it, end;

    while (! ZEROINDEG.empty()) {
	v = ZEROINDEG.front();
	ZEROINDEG.pop_front();
	xcoord(v, compute_leftmost_xcoord(v, leftmost));

	it = v.adj_nodes_begin();
	end = v.adj_nodes_end();
	
	while (it != end) {
	    w = *it;
	    if (--INDEG[w] == 0)
		ZEROINDEG.push_back(w);
	    ++it;
	}
// 	forall_adj_nodes (w,v) {
// 	    if (--INDEG[w] == 0)
// 		ZEROINDEG.push_back(w);
// 	}
    }

//     while (! ZEROINDEG.empty()) {
// 	v = ZEROINDEG.pop();
// 	xcoord(v, compute_leftmost_xcoord(v, leftmost));
// 	forall_adj_nodes (w,v) {
// 	    if (--INDEG[w] == 0)
// 		ZEROINDEG.append(w);
// 	}
//     }
}


//
// compute_max_left_shift
//
// This function examines a node dn and computes how much this node can
// be shifted to the left without overlapping a neighboring node. We take
// a look at all predecessors of dn in the order graph and search for the
// one with the smallest distance to dn. Of course, we may only consider
// nodes that are not marked as active, because the ones marked as active
// belong to the same region as dn and are therefore no obstacle for dn.
//

void Order_Graph::compute_max_left_shift (DAG_Node* dn, double& max) const
{
    edge e;
    node n;
    DAG_Node* dn_pred;
    double dist;

    n = order_node(dn);

    //
    // There must be an according node in the order graph.
    //

    assert(n != node());

    forall_in_edges (e, n) {
	dn_pred = dag_node(e.source());
	// We may only consider unmarked nodes, which means that
	// they are not in the current region.
	if (!dn_pred->active()) {
	    dist = dn->xcoord() - dn_pred->xcoord() - distance(e);
	    if (-dist > max)
		max = -dist;
	}
    }
}


//
// compute_max_right_shift
//
// This function examines a node dn and computes how much this node can
// be shifted to the right without overlapping a neighboring node. We take
// a look at all successors of dn in the order graph and search for the
// one with the smallest distance to dn. Of course, we may only consider
// nodes that are not marked as active, because the ones marked as active
// belong to the same region as dn and are therefore no obstacle for dn.
//

void Order_Graph::compute_max_right_shift (DAG_Node* dn, double& max) const
{
    edge e;
    node n;
    DAG_Node* dn_succ;
    double dist;

    n = order_node(dn);

    //
    // There must be an according node in the order graph.
    //

    assert(n != node());

    forall_out_edges (e, n) {
	dn_succ = dag_node(e.target());
	// We may only consider unmarked nodes, which means that
	// they are not in the current region.
	if (!dn_succ->active()) {
	    dist = dn_succ->xcoord() - dn->xcoord() - distance(e);
	    if (dist < max)
		max = dist;
	}
    }
}


