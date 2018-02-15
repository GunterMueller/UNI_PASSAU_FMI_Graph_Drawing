/* This software is distributed under the Lesser General Public License */
//
// dag.h
//
// This file defines the class Directed_Acyclic_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/dag.h,v $
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


#ifndef DAG_H
#define DAG_H


#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>

#include <GTL/graph.h>
#include <list>

#include <gt_tcl/Tcl.h>

#include "dag_node.h"


//
// macros (for better readability)
//

#define forall_active_nodes(x,S,func)\
forall(x,S) if ((x)->func()==(S).ycoord())


//
// predefinitions
//

class Level;
class Scanline;
class Order_Graph;
class Region;


enum Border_Flag {
    Top = 1,
    Bottom
};

enum Animation_Mode {
    A_none  = 1,
    A_phase = 2,
    A_level = 4,
    A_node  = 8,
    A_node_level = A_node | A_level,
    A_level_phase = A_level | A_phase
};


// ============================================================================
//
// class Directed_Acyclic_Graph
//
// This class contains all data and functions that are required for the
// algorithm to generate the desired layout.
//  * gt_graph is a pointer to the Graphlet representation of the graph
//    with all it's properties (cf. gt_base directory).
//  * attached is a pointer to the LEDA representation of the graph.
//  * scanline stores the levels and pointers to the nodes on the 
//    respective levels.
//  * tcl_interpreter is required for the animation during the computation.
//  * min_x and min_y are the leftmost and topmost coordinates of the 
//    graph before starting the algorithm. We need this values so that we
//    do not 'disappear' from the canvas during animation.
//  * The variables min_node_node_distance, min_node_edge_distance, and
//    min_edge_edge_distance speak for themselves. These values are among 
//    others responsible for the width of the layout.
//  * default_edge_length is used for all edges that have no label telling
//    the desired length. These lengths are considered when running the
//    topsort algorithm.
//  * animation decides what type of animation we want. This variable is
//    set bitwise:   1 1 1 1
//		     | | | |
//		     | | | +-- 1 = no animation
//		     | | +---- 2 = phase-wise animation
//		     | +------ 4 = level-wise animation
//		     +-------- 8 = node-wise animation
//  * iterations_crossing_reduction tells us how often we execute the up
//    and down phases during the reduction of edge crossing.
//  * last_phase_crossing_reduction gives information if we have to add a
//    final down phase after all the iterations of the crossing reduction
//  * iterations_node_positioning holds the value how often we pass the up
//    and down phases when giving the nodes their final positions.
//  * last_phase_node_positioning decides whether we add a final down
//    phase after all the iterations of the node positioning
//  * The node map the_dag_node is a mapping from all nodes of the LEDA
//    graph to the set of DAG_Nodes which are an abstract data type to keep
//    information about node positions, sizes, and other things.
//  * The edge map the_length stores for each edge a double value that has
//    been read from the edge's label or been taken from the default.
//  * Finally, the list the_dummies contains all nodes that have been inser-
//    ted into the graph to replace a long span edge. These dummy nodes must
//    be removed before drawing the final layout.
//  * The colors light_red, light_green, light_blue, dark_red, dark_green,
//    and dark_blue are used for animation purposes only.
//
// ============================================================================

class Directed_Acyclic_Graph
{
    GT_BASE_CLASS (Directed_Acyclic_Graph);

    GT_VARIABLE (GT_Graph*, gt_graph);
    GT_VARIABLE (graph*, attached);
    GT_VARIABLE (Scanline*, scanline);
    GT_VARIABLE (Tcl_Interp*, tcl_interpreter);

    GT_VARIABLE (double, min_x);
    GT_VARIABLE (double, min_y);

    GT_VARIABLE (bool, directed);
    GT_VARIABLE (bool, acyclic);

    //
    // parameters handed over by GT_Tcl_Extended_DAG_Algorithm
    //

    GT_VARIABLE (int, min_node_node_distance);
    GT_VARIABLE (int, min_node_edge_distance);
    GT_VARIABLE (int, min_edge_edge_distance);
    GT_VARIABLE (int, default_edge_length);
    GT_VARIABLE (int, animation);
    GT_VARIABLE (int, iterations_crossing_reduction);
    GT_VARIABLE (int, last_phase_crossing_reduction);
    GT_VARIABLE (int, iterations_node_positioning);
    GT_VARIABLE (int, last_phase_node_positioning);

    // the following two options are only temporary
    GT_VARIABLE (int, protocol);
    GT_VARIABLE (int, stepping);

private:

    //
    // data given in the graph
    //

    node_map<DAG_Node> the_dag_node;
    edge_map<double>   the_length;
    list<node>         the_dummies;
    list<edge>         the_reversed;

    //
    // some colors for animation purposes only
    //

    GT_Key light_red, light_green, light_blue;
    GT_Key dark_red, dark_green, dark_blue;

    //
    // threshold for the force of regions that shall be moved
    //

    const double threshold;

    //
    // private methods
    //

    // for initialize
    void read_node_properties ();
    void read_edge_lengths ();

    // for compute_levels
    double max_level_of_predecessors (node v);

    // for eliminate_longspan_edges
    node new_node (double top, double bottom, double width);
    edge new_edge (node source, node target, double length, edge old_edge);

    // for reduce_crossings
    void crossing_reduction_down_phase ();
    void crossing_reduction_up_phase ();
    double upper_barycenter (node n);
    double lower_barycenter (node n);
    void mark_active_nodes (const Level& level, Border_Flag pos);
    void unmark_active_nodes (const Level& level);
    void adjust_nodes (Level& level, Order_Graph& ograph);
//     void sort (Level* l);

    // for position_nodes
    void node_positioning_down_phase (const Order_Graph& ograph);
    void node_positioning_up_phase (const Order_Graph& ograph);
    void merge_dependent_regions (list<Region*>& RL, const Level& level);
    bool influencing (const Region& reg1, const Region& reg2,
	const Level& level) const;
    bool neighboring (const Region& reg1, const Region& reg2,
	const Level& level) const;
    bool approaching (const Region& reg1, const Region& reg2) const;
    double minimum_distance (const DAG_Node& dn1, const DAG_Node& dn2) const;
    void shift_regions (list<Region*>& RL, const Order_Graph& ograph);
    double compute_shift (const Region& reg, const Order_Graph& ograph) const;

    // for updating and drawing
    bool animation_is (Animation_Mode mode) {
	return ( (animation() & mode) != 0 );
    }
    void animate (Animation_Mode mode);
    void update_node (node n);
    void update_all_nodes ();
    void draw ();
    void have_a_break ();

public:

    //
    // constructor / destructor
    //

    Directed_Acyclic_Graph (GT_Graph* g);
    virtual ~Directed_Acyclic_Graph ();

    //
    // accessors for private member variables
    //

    const node_map<DAG_Node>& dag_node () const  { return the_dag_node; }
    node_map<DAG_Node>& dag_node ()              { return the_dag_node; }

    const DAG_Node& dag_node (node n) const      { return the_dag_node[n]; }
    DAG_Node& dag_node (node n)                  { return the_dag_node[n]; }

    const edge_map<double>& length () const      { return the_length; }
    edge_map<double>& length ()                  { return the_length; }

    const list<node>& dummies () const           { return the_dummies; }
    list<node>& dummies ()                       { return the_dummies; }

    const list<edge>& reversed () const           { return the_reversed; }
    list<edge>& reversed ()                       { return the_reversed; }

    //
    // get-functions for node and edge properties
    //

    double xcoord (node n)  { return dag_node(n).xcoord(); }
    double ycoord (node n)  { return dag_node(n).ycoord(); }
    double width  (node n)  { return dag_node(n).width(); }
    double height (node n)  { return dag_node(n).height(); }
    double top    (node n)  { return dag_node(n).top(); }
    double bottom (node n)  { return dag_node(n).bottom(); }
    bool is_dummy (node n)  { return dag_node(n).is_dummy(); }

    double length (edge e)  { return the_length[e]; }

    //
    // set-functions for node and edge properties
    //

    void xcoord (node n, double value)  { dag_node(n).xcoord(value); }
    void ycoord (node n, double value)  { dag_node(n).ycoord(value); }
    void width  (node n, double value)  { dag_node(n).width(value); }
    void height (node n, double value)  { dag_node(n).height(value); }
    void top    (node n, double value)  { dag_node(n).top(value); }
    void bottom (node n, double value)  { dag_node(n).bottom(value); }
    void is_dummy (node n, bool value)  { dag_node(n).is_dummy(value); }

    void length (edge e, double value)  { the_length[e] = value; }

    //
    // public methods for generating the desired layout
    //

    void initialize (Tcl_Interp* tcl_interpreter,
	int min_node_node_distance,
	int min_node_edge_distance,
	int min_edge_edge_distance,
	int default_edge_length,
	int animation,
	int iterations_crossing_reduction,
	int last_phase_crossing_reduction,
	int iterations_node_positioning,
	int last_phase_node_positioning,
	int protocol,
	int stepping);

    string generate_layout ();

    void check ();
    void remove_bends ();
    void compute_levels ();
    void eliminate_longspan_edges ();
    void reduce_crossings ();
    void position_nodes ();
    void remove_dummies ();
    void cleanup ();
};


#endif
