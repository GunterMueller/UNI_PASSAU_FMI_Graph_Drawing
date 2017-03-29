/* This software is distributed under the Lesser General Public License */
//
// icfr_layout.h
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_icse_layout/icse_layout.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:41 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#ifndef ICFR_LAYOUT_H
#define ICFR_LAYOUT_H

#include "gt_cfr_layout/cfr_layout.h"

//
// The number of phases is hard-coded.
// Don't change until you know what you are doing.
//

#define MAX_PHASE 3

//
// Possible states of edge-alignemts used by ICSE
//

#define INVALID -1
#define NO_ALIGN 0
#define H_ALIGN 1
#define V_ALIGN 2

//
// Relative positions used by ICSE
//

#define UP 3
#define DOWN 4
#define RIGHT 5
#define LEFT 6

//
// This class implements ICSE. It is an sub-class from
// FR_Constraint_Graph which is the base-class from CFR
//

class Iterative_Constraint_Spring_Embedder
    : public FR_Constraint_Graph
{
private:
    //
    //For settings see CFR
    // 

    edge_map<int> the_edge_alignment;
    edge_map<int> the_bend_edge;
    node_map<bool> the_bend_node;
    list <node> the_bend_nodes;
    
    //
    // compare_edges / sort edges:
    //
    // cause of MSVCC dos not (yet) support member templates
    // we cannot use the preimplemented sort-funktion for lists
    //

#ifdef __GTL_MSVCC
    void sort_edges(list<edge> &to_sort,
	Iterative_Constraint_Spring_Embedder &_se,
	node _source, int _mode);
#endif

    //
    // private methods, used during ICSE
    //

    class compare_edges
    {
    public:
	compare_edges(Iterative_Constraint_Spring_Embedder &_se,
		       node _source, int _mode);
	bool operator()(const edge& e1, const edge& e2);
    private:
	Iterative_Constraint_Spring_Embedder &se;
	node source;
	int mode;
    };
    friend class compare_edges;

    void analyse_node_constraints(const node n,
	int& up_h_constraints, int& low_h_constraints,
	int& up_v_constraints, int& low_v_constraints);
    void create_gt_edges();
    void distribute_edges();
    bool h_align(const edge e);
    void init_edge_alignments();
    int insert_constraints(double max_deg);
    double orientation();
    void prepare_edges();
    void remove_obsolete_bends();
    void set_bend_and_anchor(edge e, node n, double abs_pos, int mode);
    void set_edge_anchors(list<edge> &edge_list, node n, int mode);
    int split_edges();
    void springembedder_phase(
	const double damping, const double max_phase_iteration,
	const bool opposite_pair_heuristic,
	const bool use_repulsive_factor,
	const bool length_constraints,
	const bool increase_extra_length);
    void turn_graph();
    bool v_align(const edge e);

    //
    // public methods
    //

public:
    Iterative_Constraint_Spring_Embedder(GT_Graph* gt_graph);
    ~Iterative_Constraint_Spring_Embedder();
    string iterative_constraint_placement();
};

#endif







