/* This software is distributed under the Lesser General Public License */
//
// ordergraph.h
//
// This file defines the class Order_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/ordergraph.h,v $
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


#ifndef ORDERGRAPH_H
#define ORDERGRAPH_H


#include <gt_base/Graphlet.h>

#include <GTL/graph.h>
#include <GTL/node_map.h>
#include <map>
#include "dag_node.h"


//
// predefinitions
//

class Level;


// ============================================================================
//
// class Order_Graph
//
// This class is used to create and maintain a graph that is necessary
// for reordering our graph. Whenever we move nodes, there is the danger
// that the nodes moved overlap with other nodes that did not move,
// because unlike Sugiyama's algorithm we have nodes of different widths
// and heights. Therefore we have to reorder the modified graph in order
// to re-establish the necessary distances between the nodes. We do this
// by inserting a node into the order graph for each node in the DAG and
// by inserting edge between two order nodes into the order graph when
// the according DAG_Nodes are conflicting. 'Conflicting' means that there
// is at least one level where the are neighbored. The weight of the edge
// is set to the minimum distance that is necessary between these nodes.
// Whenever we have to reorder the order graph, we make a topological
// sorting according to the given edge lengths.
// An order graph has the following members:
//  - order is a graph that contains a node for each node in the original
//    graph.
//  - The distances min_node_node_distance, min_node_edge_distance, and
//    min_edge_edge_distance have the same meanings as the do in dag.h.
//    They are required here to get the distances right.
//  - dag_node is a map that assigns a DAG_Node to each node in the
//    graph 'order'.
//  - order_node is a map that assigns an order node to each DAG_Node
//    in the original graph.
//    Both maps together realize a bijective mapping between the set of
//    DAG_Nodes and the set of order nodes.
//  - Finally, the edge_map 'distance' stores the computed distances of
//    the edges.
//
// ============================================================================

class Order_Graph
{
    GT_BASE_CLASS (Order_Graph);

    GT_VARIABLE (graph*, order);
    GT_VARIABLE (int, min_node_node_distance);
    GT_VARIABLE (int, min_node_edge_distance);
    GT_VARIABLE (int, min_edge_edge_distance);

private:

    node_map<DAG_Node*> the_dag_node;
    map<DAG_Node*,node> the_order_node;
    edge_map<double> the_distance;

public:

    //
    // constructors and destructors
    //

    Order_Graph (int nn_dist, int ne_dist, int ee_dist);
    virtual ~Order_Graph ();

    //
    // accessors for member variables
    //

    DAG_Node* dag_node (node n) const
    { return the_dag_node[n]; }
    node order_node (DAG_Node* dn) const {
	map<DAG_Node*,node>::const_iterator it = the_order_node.find(dn);
	return it != the_order_node.end() ? (*it).second : node(); 
    }
    double distance (edge e) const
    { return the_distance[e]; }
    double xcoord (node n) const
    { return dag_node(n)->xcoord(); }

    void dag_node (node n, DAG_Node* dn)    { the_dag_node[n] = dn; }
    void order_node (DAG_Node* dn, node n)  { the_order_node[dn] = n; }
    void distance (edge e, double d)        { the_distance[e] = d; }
    void xcoord (node n, double x)          { dag_node(n)->xcoord(x); }

private:

    //
    // methods for creating the order graph
    //

    void relation (DAG_Node* dn_left, DAG_Node* dn_right, double dist);
    node insert (DAG_Node* dn);
    double compute_leftmost_xcoord (node v, double min_x);

public:

    void include (const Level& level);

    //
    // methods for evaluating the order graph
    //

    void reorder (double leftmost);

    void compute_max_left_shift (DAG_Node* dn, double& max) const;
    void compute_max_right_shift (DAG_Node* dn, double& max) const;
};


#endif
