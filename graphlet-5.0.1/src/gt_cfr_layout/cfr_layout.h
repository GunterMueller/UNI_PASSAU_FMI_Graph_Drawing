/* This software is distributed under the Lesser General Public License */
//
// cfr_layout.h
//
// Definitons of the class FR_Constraint_Graph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/cfr_layout.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:28 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#ifndef CFR_LAYOUT_H
#define CFR_LAYOUT_H

#include <list>
#include <set>

#include <gt_tcl/Tcl.h>
#include <gt_tcl/Tcl_Graph.h>

#include "cfr_one_dimension.h"

#define DEBUG 0

#define MAX_PHASE 3

//
// Well, due to a bug in the LEDA memory handler we can't use
// use something like node_map<node_map<double>> so this is
// a hopefully temporary work-around :-/
//
// MF, 17.6.: Yes, it was. GTL copes with node_map<node_map<double>>

#define MATRIX_TYPE double
 
ostream& operator<< (ostream& os, const list<edge>& dummy);
inline istream& operator>> (istream& is, const list<edge>&)
{
    return is;
}

class Pair_of_Nodes
{
    GT_VARIABLE (node, node1);
    GT_VARIABLE (node, node2);
public:
    Pair_of_Nodes() {};
    Pair_of_Nodes(node node1, node node2)
    {
	this->the_node1 = node1;
	this->the_node2 = node2;
    }
    virtual ~Pair_of_Nodes() {}
    	
    inline friend ostream& operator<<
    ( ostream& os, const Pair_of_Nodes& ) { return os; };
    inline friend istream& operator>>
    ( istream& is, const Pair_of_Nodes& ) { return is; };
};


class FR_Constraint_Graph
{
private:
    GT_VARIABLE (int, animation);
    GT_VARIABLE (bool, constraint_forces);
    GT_VARIABLE (bool, colour_the_nodes);
    GT_VARIABLE (string, delimiter);
    GT_VARIABLE (double, minimal_distance);
    GT_VARIABLE (double, minimal_force);
    GT_VARIABLE (bool, new_bends);
    GT_VARIABLE (double, optimal_distance);
    GT_VARIABLE (bool, random_placement);
    GT_VARIABLE (bool, respect_sizes);	
    GT_VARIABLE (double, vibration_ratio);
    GT_VARIABLE (int, window_width);
    GT_VARIABLE (int, window_height);
    GT_VARIABLE (double, xoffset);
    GT_VARIABLE (double, yoffset);
	
    GT_VARIABLE (bool, has_geometric_constraints);
    GT_VARIABLE (bool, has_lengths_constraints);
    GT_VARIABLE (bool, has_group_constraints);
    GT_VARIABLE (int, has_opposite_pairs);
    GT_VARIABLE (bool, has_repulsive_factors);
    GT_VARIABLE (double, overlap_pitch);
    GT_VARIABLE (int, swap_lock);
    
    GT_VARIABLE (GT_Tcl_Graph*, tcl_graph);
    GT_VARIABLE (Tcl_Interp*, tcl_interp);

    GT_VARIABLE (GT_Graph*, gt_graph);
    GT_VARIABLE (graph*, attached);

    node_map<FR_One_Dimensional_Constraint*> the_h_constraint;
    node_map<FR_One_Dimensional_Constraint*> the_v_constraint;

    node_map< list<node> > the_h_orthogonal_constraint;
    node_map< list<node> > the_v_orthogonal_constraint;
	
    node_map<double> the_node_width;
    node_map<double> the_node_height;

    list<edge> the_hidden_edges; 
    list<edge> the_edges; 

    node_map<node_map<MATRIX_TYPE> > the_repulsive_factor;
    node_map<node_map<MATRIX_TYPE> > the_optimal_matrix;
	
    list < list<edge> > the_lengths_constraints;
	
    list <node> the_v_representative;
    list <node> the_h_representative;

    list <Pair_of_Nodes> the_h_opposite_pairs;
    list <Pair_of_Nodes> the_v_opposite_pairs;

    int max_iteration[MAX_PHASE];
    double phase_damping[MAX_PHASE];
	
    //
    //		private methods (alphabetical order)
    //
		   
    void calculate_attractive_displacement();
    void calculate_constraint_displacement();
    void calculate_length_constraint_displacement();
    void calculate_repulsive_displacement(
	const bool use_the_repulsive_factor,
	const bool increase_optimal_distance);
    void colour_nodes();
    void constraint_displace_nodes();
    void count_constraints();
    void del_edge(edge e);
    void del_node(node n);
    void del_representative(
	node_map<FR_One_Dimensional_Constraint*>& constraint,
	list<node>& representative,
	FR_One_Dimensional_Constraint* to_delete);
    void fit_graph_to_window(const int margin, const bool do_scale);
    void group_initialization();
    void initilization(bool &fit_graph);
    void init_group_constraints(
	node_map<FR_One_Dimensional_Constraint*>& constraint,
	list<node>& representative,
	const char dimension,
	const string group_identifier,
	int random_coords);
    void init_node_width_and_height();
    void init_orthogonal_constraints(
	node_map< list<node> >& orthogonal_constraint,
	const string group_identifier);
    bool join_hv_groups(
	node_map<FR_One_Dimensional_Constraint*>& constraint,
	list<node>& representative,
	FR_One_Dimensional_Constraint* first,
	FR_One_Dimensional_Constraint* second);
    double limit_displacement(double damp);
    void make_readable_labels();
    node new_node(const double x, const double y,
	const double scale_repulsive_force);
    edge new_edge(node source, node target);
    void notify_errors(
	const node_map<FR_One_Dimensional_Constraint*>& constraint);
    void opposite_pair_heuristic(
	const node_map<FR_One_Dimensional_Constraint*>& constraint,
	const list<Pair_of_Nodes>& opposite_pairs);
    void order_nodes(
	node_map<FR_One_Dimensional_Constraint*>& constraint,
	list<node>& representative);
    string parse_node_constraints(const string original,
	const bool source_node);
    void read_constraints();
    string read_label(const edge e);
    void read_lengths_constraints();
    void redraw();
    void reduce_constraints(
	node_map<FR_One_Dimensional_Constraint*>& constraint,
	list<node>& representative);
    string remove_comment(const string original);
    void set_group_repulsive_factors(
	const node_map<FR_One_Dimensional_Constraint*>& constraint,
	const char dimension);
    void unconstraint_displace_nodes();
    void update_gt_properties();

    //
    //		public methods
    //	
	
public:
    FR_Constraint_Graph(GT_Graph* gt_graph);
    virtual ~FR_Constraint_Graph();
    string check();
    string force_directed_placement();
    list<edge>& edges() 
    { return this->the_edges; }
    list< list<edge> >& lengths_constraints()
    { return this->the_lengths_constraints; }
    void set_parameters(
	int animation,
	bool colour_the_nodes,
	bool constraint_forces,
	string delimiter,
	double minimal_distance,
	double minimal_force,
	bool new_bends,
	double optimal_distance,
	double phase1_damping,
	int phase1_max_iteration,
	double phase2_damping,
	int phase2_max_iteration,
	double phase3_damping,
	int phase3_max_iteration,
	bool random_placement,
	bool respect_sizes,
	GT_Tcl_Graph* tcl_graph,
	Tcl_Interp* tcl_interp,
	double vibration_ratio,
	int window_height,
	int window_width,
	double xoffset,
	double yoffset
	);
};

#endif







