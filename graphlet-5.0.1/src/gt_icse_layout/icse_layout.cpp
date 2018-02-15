/* This software is distributed under the Lesser General Public License */
// icse_layout.cpp
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_icse_layout/icse_layout.cpp,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:45:39 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include <gt_base/Graphlet.h>

#include "icse_layout.h"
#include "gt_cfr_layout/cfr_layout.h"	

#include <gt_base/NEI.h>

//
// Constructor:
//

Iterative_Constraint_Spring_Embedder::Iterative_Constraint_Spring_Embedder
(GT_Graph* gt_graph) : FR_Constraint_Graph(gt_graph)
{
}


//
// Destructor:
//

Iterative_Constraint_Spring_Embedder::
~Iterative_Constraint_Spring_Embedder()
{

    //
    // MR: 5.2.1999
    // 
    // the lists the_v_representative and the_h_representative seem
    // to contain bend nodes which would normally be deleted in 
    // create_gt_edges, but in this case we have undefined references here.
    // 
    // PLEASE NOTE: This is only a workaround !!!
    //

    list<node>::iterator it, end;
    while (!the_v_representative.empty())
    {
	it = the_v_representative.begin();
	delete the_v_constraint[*it];
	the_v_representative.erase (it);
    }


    while (!the_h_representative.empty())
    {
	it = the_h_representative.begin();
	delete the_h_constraint[*it];
	the_h_representative.erase (it);
    }

    list<node>& del = this->the_bend_nodes;
    for(it = del.begin(), end = del.end(); it != end; ++it)
	this->attached()->del_node(*it);
}


//
// springembedder_phase:
//
// Perform one springembedder phase with given heuristics
// and given temperature (damping).
//

void Iterative_Constraint_Spring_Embedder::springembedder_phase(
    const double damping, const double max_phase_iteration,
    const bool opposite_pair_heuristic,
    const bool use_repulsive_factor,
    const bool length_constraints,
    const bool increase_optimal_distance)
{
    int phase_iteration = 0;
    double force_4 = 0.0, force_3 = 0.0, force_2 = 0.0, force_1 = 0.0;
    double ratio_1 = HUGE, ratio_2 = HUGE;
    
    while ((phase_iteration < max_phase_iteration) &&
	((force_4 == 0.0) ||
	    ((force_1 > 1) &&
		(ratio_1 > this->vibration_ratio()) ||
		(ratio_2 > this->vibration_ratio())))) {
	phase_iteration++;
	
	this->calculate_attractive_displacement();
	
	if ((this->constraint_forces()) &&
	    (this->has_geometric_constraints())) {
	    this->calculate_constraint_displacement();
	}
	
	if (opposite_pair_heuristic &&
	    (this->has_opposite_pairs())) {
	    this->opposite_pair_heuristic(the_h_constraint,
		the_v_opposite_pairs);
	    this->opposite_pair_heuristic(the_v_constraint,
		the_h_opposite_pairs);
	}

	this->calculate_repulsive_displacement(
	    use_repulsive_factor, increase_optimal_distance);
		
	if ((length_constraints) && this->has_lengths_constraints()) {
	    this->calculate_length_constraint_displacement();
	}
	
	force_4 = force_3;
	force_3 = force_2;
	force_2 = force_1;
			
	force_1 = this->limit_displacement(damping);

	if (this->has_geometric_constraints()) {
	    this->constraint_displace_nodes();
	} else {
	    this->unconstraint_displace_nodes();
	}

	if(this->animation()) {
	    if (!(phase_iteration % this->animation())) {
		this->redraw();                 
	    }
        }
		
	if (force_1) {
	    ratio_1 = fabs((force_1 - force_3) / force_1);
	} else {
	    ratio_1 = 0;
	}

	if (force_2) {
	    ratio_2 = fabs((force_2 - force_4) / force_2);
	} else {
	    ratio_2 = 0;
	}
    }
#if DEBUG
    cout << " iteration " << phase_iteration << endl;
#endif
}


//
// init_edge_alignments:
//
// Forall edges determine the aligment of the start-layout.
// Valid aligments are H_ALIGN (horizontal), V_ALIGN (vertical)
// and NO_ALIGN (not aligned).
//

void Iterative_Constraint_Spring_Embedder::init_edge_alignments()
{
    this->the_edge_alignment.init(*this->attached(), NO_ALIGN);
    this->the_bend_edge.init(*this->attached(), 0);
    this->the_bend_node.init(*this->attached(), false);

    list<edge>::const_iterator it, end;

    for(it = the_edges.begin(), end = the_edges.end();
	it != end; ++it)
    {
	node source = it->source();
	node target = it->target();

	if(the_h_constraint[source] == the_h_constraint[target]) {
	    this->the_edge_alignment[*it] = H_ALIGN;
	}
	if(the_v_constraint[source] == the_v_constraint[target]) {
	    this->the_edge_alignment[*it] = V_ALIGN;
	}
    }
}


//
// orientation:
//
// Here we calculate / count how the graph should be rotated
// so that as much as possible edges are aligned nearly vertically
// or horizontally.
//

double Iterative_Constraint_Spring_Embedder::orientation()
{
    int angle[18];
    int max = 0, max_arc = 0;
    
    int i;
    for(i = 0; i < 18; i++) {
	angle[i] = 0;
    }

    list<edge>::const_iterator it, end;
    for(it = the_edges.begin(), end = the_edges.end(); it != end; ++it)
    {
	node source = it->source();
	node target = it->target();

	double dx = this->the_v_constraint[source]->coord() -
	    this->the_v_constraint[target]->coord();
	double dy = this->the_h_constraint[source]->coord() -
	    this->the_h_constraint[target]->coord();
	
	double arc;
	
	if(dy) {
	    arc = dx / dy;
	    if(arc < 0) {
		arc = -1 / arc;
	    }

	    arc = atan(arc) / M_PI_2;
	    
	    if (arc == 1) {
		arc = 0;
	    }
	} else {
	    arc = 0;
	}

	arc *= 18;
	int arc_num = (int)arc; 
	
	if (++angle[arc_num] > max) {
	    max = angle[arc_num];
	    max_arc = arc_num; 
	}
    }
    return (M_PI - (M_PI * (double)(5 * max_arc) / 180));
}


//
// turn_graph:
//
// Turn the graph to a good start position. Angle is computed
// in method orientation. This does not respect any constraints
// and therefore is not called if the graph has geometrical constraints.
// You may want to improve this, since the rotation of the layout
// results in improved layouts for many graphs.
//

void Iterative_Constraint_Spring_Embedder::turn_graph()
{
    double center_x = 0, center_y = 0;

    double turn = this->orientation();
    
#if DEBUG
    cout << "Turning layout by "
	 << (180 * (turn / M_PI))
	 << " (" << turn << ") "
	 << endl;
#endif

    node n;
    forall_nodes (n, *this->attached()) {
	center_x += this->the_v_constraint[n]->coord();
	center_y += this->the_h_constraint[n]->coord();
    }
    center_x /= this->attached()->number_of_nodes();
    center_y /= this->attached()->number_of_nodes();
	
    forall_nodes (n, *this->attached()) {
	double xo = this->the_v_constraint[n]->coord();
	double yo = this->the_h_constraint[n]->coord();

	this->the_v_constraint[n]->coord(
	    xo * cos(turn) + yo * sin(turn));
	this->the_h_constraint[n]->coord(
	    yo * cos(turn) - xo * sin(turn));
    }
}


//
// analyse_node_constraints:
//
// Here we count how many edges are currently on each side of 
// the node. This information is used to distribute the edges
// evenly on the sides of a node.
// 

void Iterative_Constraint_Spring_Embedder::analyse_node_constraints(
    const node n, int& up_h_constraints, int& low_h_constraints,
    int& up_v_constraints, int& low_v_constraints)
{
    up_h_constraints = low_h_constraints = 0;
    up_v_constraints = low_v_constraints = 0;
    
    edge io_edge;

    forall_inout_edges(io_edge, n) {
	node adj = n.opposite(io_edge);

	if (this->the_h_constraint[n] == this->the_h_constraint[adj]) {
	    if ((this->the_v_constraint[n]->coord() -
		this->the_v_constraint[adj]->coord()) > 0) {
		low_h_constraints++;
	    } else {
		up_h_constraints++;
	    }
	}

	if (this->the_v_constraint[n] == this->the_v_constraint[adj]) {
	    if ((this->the_h_constraint[n]->coord() -
		this->the_h_constraint[adj]->coord()) > 0) {
		low_v_constraints++;
	    } else {
		up_v_constraints++;
	    }
	}
    }
}


//
// h_align:
//
// Determine if the edge e may be aligned horizontally.
// This is forbidden if this would join groups of constraints so that
// some edges must be drawn on top of each other.
//

bool Iterative_Constraint_Spring_Embedder::h_align(const edge e)
{
    node source = e.source();
    node target = e.target();

    if (this->the_v_constraint[source] ==
	this->the_v_constraint[target]) {
	return false;
    }
    
    if(!this->the_bend_edge[e]) {
	int s_up_hc, s_low_hc, s_up_vc, s_low_vc;
	int t_up_hc, t_low_hc, t_up_vc, t_low_vc;
    
	this->analyse_node_constraints(source,
	    s_up_hc, s_low_hc, s_up_vc, s_low_vc);
	this->analyse_node_constraints(target,
	    t_up_hc, t_low_hc, t_up_vc, t_low_vc);
    
	if ((this->the_v_constraint[source]->coord() -
	    this->the_v_constraint[target]->coord()) > 0) {
	    if ((s_low_hc + t_up_hc) > 0) {
		return false;
	    }
	} else {
	    if ((s_up_hc + t_low_hc) > 0) {
		return false;    
	    }
	}
    
	node n1;
	forall_nodes (n1, *this->attached()) {
	    if (this->the_h_constraint[n1] ==
		this->the_h_constraint[source]) {
		node n2;
		forall_nodes (n2, *this->attached()) {
		    if (this->the_h_constraint[n2] ==
			this->the_h_constraint[target]) {
			if (this->the_v_constraint[n1] ==
			    this->the_v_constraint[n2]) {
			    return false;
			}
		    }
		}
	    }
	}
    }
    
    if (this->join_hv_groups(
	this->the_h_constraint,
	this->the_h_representative,
	this->the_h_constraint[source],
	this->the_h_constraint[target])) {

	this->the_edge_alignment[e] = H_ALIGN;
	
	return true;
    } else {
	return false;
    }
}


//
// v_align:
//
// Determine if the edge e may be aligned vertically.
// This is forbidden if this would join groups of constraints so that
// some edges must be drawn on top of each other.
//

bool Iterative_Constraint_Spring_Embedder::v_align(const edge e)
{
    node source = e.source();
    node target = e.target();

    if(this->the_h_constraint[source] ==
	this->the_h_constraint[target]) {
	return false;
    }

    if(!this->the_bend_edge[e]) {
	int s_up_hc, s_low_hc, s_up_vc, s_low_vc;
	int t_up_hc, t_low_hc, t_up_vc, t_low_vc;
    
	this->analyse_node_constraints(source,
	    s_up_hc, s_low_hc, s_up_vc, s_low_vc);
	this->analyse_node_constraints(target,
	    t_up_hc, t_low_hc, t_up_vc, t_low_vc);

	if ((this->the_h_constraint[source]->coord() -
	    this->the_h_constraint[target]->coord()) > 0) {
	    if ((s_low_vc + t_up_vc) > 0) {
		return false;
	    }
	} else {
	    if ((s_up_vc + t_low_vc) > 0) {
		return false;    
	    }
	}

	node n1;
    
	forall_nodes (n1, *this->attached()) {
	    if (this->the_v_constraint[n1] ==
		this->the_v_constraint[source]) {
		node n2;
		forall_nodes (n2, *this->attached()) {
		    if (this->the_v_constraint[n2] ==
			this->the_v_constraint[target]) {
			if (this->the_h_constraint[n1] ==
			    this->the_h_constraint[n2]) {
			    return false;
			}
		    }
		}
	    }
	}
    }
    
    if(this->join_hv_groups(
	this->the_v_constraint,
	this->the_v_representative,
	this->the_v_constraint[source],
	this->the_v_constraint[target])) {

	this->the_edge_alignment[e] = V_ALIGN;
	
	return true;
    } else {
	return false;
    }
}


//
// insert_constraints:
//
// Insert new constraints for all unconstraint edges, which can
// be aligned by a rotation of at most max_deg degree.
// The methods v_align and h_align are used to determine if a
// constraint is valid or would yield into a degenerated layout.
//

int Iterative_Constraint_Spring_Embedder::insert_constraints(
    double max_deg)
{
    int h_alignments = 0, v_alignments = 0;
    
    double quality = tan(M_PI * (90 - max_deg) / 180);
    
    list<edge>::const_iterator it, end;

    for(it = the_edges.begin(), end = the_edges.end();
	it != end; ++it)
    {
	if(this->the_edge_alignment[*it] == NO_ALIGN) {
	    node source = it->source();
	    double x1 = this->the_v_constraint[source]->coord();
	    double y1 = this->the_h_constraint[source]->coord();
	    
	    node target = it->target();
	    double x2 = this->the_v_constraint[target]->coord();
	    double y2 = this->the_h_constraint[target]->coord();

	    if(x1 == x2) {
		if(this->v_align(*it)) {
		    v_alignments++;
		}
	    } else if(y1 == y2) {
		if(this->h_align(*it)) {
		    h_alignments++;
		}
	    } else {
		double q;
		double m = fabs((y2 - y1) / (x2 - x1));

		if(m < 1) {
		    q = 1 / m;
		} else {
		    q = m;
		}
		
		if(q > quality) {
		    if(m < 1) {
			if(this->h_align(*it)) {
			    h_alignments++;
			}
		    } else {
			if(this->v_align(*it)) {
			    v_alignments++;
			}
		    }
		}
	    }
	}
    }
#if DEBUG
    cout << "Done: h_alignments " << h_alignments
	 << " / v_alignments " << v_alignments << endl;
#endif
    return h_alignments + v_alignments;
}


//
// split_edges:
//
// Create dummy nodes and edges to simulate edges with bends.
//
// Here we actually change the graph to simulate bends for the
// edges which couldn't be aligned. Since for all new (dummy) edges
// alignments are assigned we get a completly orthogonal graph here.
//
// There is some work done to determine if it is possible to create
// just one bend - or if this would join some edges partially.
// Otherwise two bends, i. e. two dummy nodes and three new dummy edges
// are created.
//
// Since we change the graph here - we also change node and edge ids
// for some nodes and edges. This confuses graphlets selection mechanism
// so we do remove the selection in the graphscript part of this algorithm.
//
// CAVEAT: The graph might become rather large and since the springembedder
// is not linear you might need an unreasonable amount of time _and_ memory.
//

int Iterative_Constraint_Spring_Embedder::split_edges()
{
    int new_bends = 0;
	
    list<edge> split;
	
    list<edge>::const_iterator it, end;
	
    for(it = the_edges.begin(), end = the_edges.end();
	it != end; ++it)
    {
	if(this->the_edge_alignment[*it] == NO_ALIGN) {
	    split.push_back(*it);
	}
    }
    
    for(it = split.begin(), end = split.end();
	it != end; ++it)
    {
	node source = it->source();
	double x1 = this->the_v_constraint[source]->coord();
	double y1 = this->the_h_constraint[source]->coord();
		
	node target = it->target();
	double x2 = this->the_v_constraint[target]->coord();
	double y2 = this->the_h_constraint[target]->coord();
		
	int th_position, tv_position;
		
	if ((y1-y2) > 0) {
	    tv_position = UP;
	} else {
	    tv_position = DOWN;
	}
		
	if ((x1-x2) > 0) {
	    th_position = LEFT;
	} else {
	    th_position = RIGHT;
	}
		
	bool hv_allowed = true;
	bool vh_allowed = true;
	bool split_2_times = false;
		
	edge io_edge;
	forall_inout_edges (io_edge, source) {
	    node n = source.opposite(io_edge);
			
	    if ((this->the_h_constraint[n] ==
		this->the_h_constraint[source]) &&
		(this->the_v_constraint[n] ==
		    this->the_v_constraint[target])) {
		hv_allowed = false;
	    }
	    if ((this->the_v_constraint[n] ==
		this->the_v_constraint[source]) &&
		(this->the_h_constraint[n] ==
		    this->the_h_constraint[target])) {
		vh_allowed = false;
	    }
	}
		
	int first_align = H_ALIGN;
		
	if (hv_allowed) {
	    if(vh_allowed) {
		int s_up, s_down, s_left, s_right;
		int t_up, t_down, t_left, t_right;
				
		this->analyse_node_constraints(source,
		    s_right, s_left, s_down, s_up);
		this->analyse_node_constraints(target,
		    t_right, t_left, t_down, t_up);
				
		if (tv_position == UP) {
		    if (th_position == RIGHT) {
			if ((s_up + t_left) < (s_right + t_down)) {
			    first_align = V_ALIGN;
			}
		    } else {
			if ((s_up + t_right) < (s_left + t_down)) {
			    first_align = V_ALIGN;
			}
		    }
		} else {
		    if (th_position == RIGHT) {
			if ((s_down + t_left) < (s_right + t_up)) {
			    first_align = V_ALIGN;
			}
		    } else {
			if ((s_down + t_right) < (s_left + t_up)) {
			    first_align = V_ALIGN;
			}
		    }
		}
	    } else {
		first_align = H_ALIGN;
	    }
	} else {
	    if (vh_allowed) {
		first_align = V_ALIGN;
	    } else {
		split_2_times = true;
				
		int s_up, s_down, s_left, s_right;
		int t_up, t_down, t_left, t_right;
				
		this->analyse_node_constraints(source,
		    s_right, s_left, s_down, s_up);
		this->analyse_node_constraints(target,
		    t_right, t_left, t_down, t_up);
				
		if (tv_position == UP) {
		    if (th_position == RIGHT) {
			if ((s_up + t_down) < (s_right + t_left)) {
			    first_align = V_ALIGN;
			}
		    } else {
			if ((s_up + t_down) < (s_left + t_right)) {
			    first_align = V_ALIGN;
			}
		    }
		} else {
		    if (th_position == RIGHT) {
			if ((s_down + t_up) < (s_right + t_left)) {
			    first_align = V_ALIGN;
			}
		    } else {
			if ((s_down + t_up) < (s_left + t_right)) {
			    first_align = V_ALIGN;
			}
		    }
		}
	    }
	}
		
	int old_id = this->gt_graph()->gt(*it).id();
	string label = this->gt_graph()->gt(*it).label();
	this->del_edge(*it);
		
	double bend_x1, bend_y1, bend_x2 = 0, bend_y2 = 0;
	edge e1, e2, e3;
		
	if (split_2_times) {
	    if (first_align == H_ALIGN) {
		bend_x1 = bend_x2 = (x1 + x2) / 2;
		bend_y1 = y1;
		bend_y2 = y2;
	    } else {
		bend_x1 = x1;
		bend_x2 = x2;
		bend_y1 = bend_y2 = (y1 + y2) / 2;
	    }
	} else {
	    if (first_align == H_ALIGN) {
		bend_x1 = x2;
		bend_y1 = y1;
	    } else {
		bend_x1 = x1;
		bend_y1 = y2;
	    }
	}
		
	node bend_node1 = this->new_node(bend_x1, bend_y1, 1);
	//  Mike: this cannot happen, I think
	// 	if (!bend_node1) {
	// 	    return new_bends;
	// 	} 
	new_bends++;
		
	node bend_node2;
	list<edge> new_edges;
		
	if (split_2_times) {
	    bend_node2 = this->new_node(bend_x2, bend_y2, 1);
	    //  Mike: this cannot happen, I think
	    // 	    if (!bend_node2) {
	    // 		this->del_node(bend_node1);
	    // 		return (new_bends-1);
	    // 	    }
	    new_bends++;
			
	    this->the_optimal_matrix[source][bend_node1] =
		this->the_optimal_matrix[bend_node1][source] =
		this->the_optimal_matrix[bend_node1][bend_node2] =
		this->the_optimal_matrix[bend_node2][bend_node1] =
		this->the_optimal_matrix[bend_node2][target] =
		this->the_optimal_matrix[target][bend_node2] =
		this->the_optimal_matrix[source][target] / 12;
			
	    e1 = this->new_edge(source, bend_node1);
	    new_edges.push_back(e1);
	    e2 = this->new_edge(bend_node1, bend_node2);
	    new_edges.push_back(e2);
	    e3 = this->new_edge(bend_node2, target);
	    new_edges.push_back(e3);
	} else {
	    this->the_optimal_matrix[source][bend_node1] =
		this->the_optimal_matrix[bend_node1][source] =
		this->the_optimal_matrix[target][bend_node1] =
		this->the_optimal_matrix[bend_node1][target] =
		this->the_optimal_matrix[source][target] / 4;
			
	    e1 = this->new_edge(source, bend_node1);
	    new_edges.push_back(e1);
	    e2 = this->new_edge(bend_node1, target);
	    new_edges.push_back(e2);
	}
		
	this->gt_graph()->gt(e1).label(label);
		
	list<edge>::const_iterator it, end;
	for(it = new_edges.begin(), end = new_edges.end();
	    it != end; ++it)
	{
	    this->gt_graph()->gt(*it).edge_nei()->
		set_EA_default_function(
		    GT_Keys::EA_orthogonal, GT_Source);
	    this->gt_graph()->gt(*it).edge_nei()->
		set_EA_default_function(
		    GT_Keys::EA_orthogonal, GT_Target);
	    this->the_edge_alignment[*it] = NO_ALIGN;
	    this->the_bend_edge[*it] = old_id;
	}
		
	if (first_align == H_ALIGN) {
	    this->h_align(e1);
	    this->v_align(e2);
	} else {
	    this->v_align(e1);
	    this->h_align(e2);
	}
		
	this->the_bend_nodes.push_back(bend_node1);
	this->the_bend_node[bend_node1] = true;
		
	if (split_2_times) {
	    if (first_align == H_ALIGN) {
		this->h_align(e3);
	    } else {
		this->v_align(e3);
	    }
	    this->the_bend_nodes.push_back(bend_node2);
	    this->the_bend_node[bend_node2] = true;
	}
    }
    return new_bends;
}


//
// prepare_edges:
//
// There exits edge_lines with length 0. Since the followings
// methods does not like this they are set to standard
// two-point edge_lines.
//

void Iterative_Constraint_Spring_Embedder::prepare_edges()
{
    edge e;

    forall_edges (e, *this->attached()) {
	GT_Polyline line =
	    this->gt_graph()->gt(e).graphics()->line();

	if(line.size() <= 2) {
	    const node source = e.source();
	    const node target = e.target();

	    line.clear();
	    line.push_back(GT_Point(this->the_v_constraint[source]->coord(),
		this->the_h_constraint[source]->coord()));
	    line.push_back(GT_Point(this->the_v_constraint[target]->coord(),
		this->the_h_constraint[target]->coord()));
	    this->gt_graph()->gt(e).graphics()->line(line);
	}
    }
}

//
// compare_edges:
//
// This compare method is used to sort the edges of one side of a node
// (top, bottom, right and left) so that as few as possible edge-crossings
// are generated near the node, i. e. the first two segments of each
// adjacent edge are considered.
//


Iterative_Constraint_Spring_Embedder::compare_edges::compare_edges
(Iterative_Constraint_Spring_Embedder &_se,
    node _source, int _mode) :
	se(_se), source(_source), mode(_mode)
{
}

bool Iterative_Constraint_Spring_Embedder::compare_edges::operator()
    (const edge& e1, const edge& e2)
{
    node target1, target2, bend1, bend2;
    
    const double sx = se.the_v_constraint[source]->coord();
    const double sy = se.the_h_constraint[source]->coord();

    if (e1.source() == source) {
	bend1 = e1.target();
	if (se.the_bend_node[bend1]) {
	    target1 = bend1.adj_edges_begin()->target();
	} else {
	    target1 = bend1;
	}
    } else {
	bend1 = e1.source();
	if (se.the_bend_node[bend1]) {
	    target1 = bend1.in_edges_begin()->source();
	} else {
	    target1 = bend1;
	}
    }

    const bool e1_has_bend = se.the_bend_node[bend1];
    
    const double e1x0 = se.the_v_constraint[bend1]->coord();
    const double e1y0 = se.the_h_constraint[bend1]->coord();
    const double e1x1 = se.the_v_constraint[target1]->coord();
    const double e1y1 = se.the_h_constraint[target1]->coord();

    if (e2.source() == source) {
	bend2 = e2.target();
	if (se.the_bend_node[bend2]) {
	    target2 = bend2.adj_edges_begin()->target();
	} else {
	    target2 = bend2;
	}
    } else {
	bend2 = e2.source();
	if (se.the_bend_node[bend2]) {
	    target2 = bend2.in_edges_begin()->source();
	} else {
	    target2 = bend2;
	}
    }

    const bool e2_has_bend = se.the_bend_node[bend2];

    const double e2x0 = se.the_v_constraint[bend2]->coord();
    const double e2y0 = se.the_h_constraint[bend2]->coord();
    const double e2x1 = se.the_v_constraint[target2]->coord();
    const double e2y1 = se.the_h_constraint[target2]->coord();

    double d1, d2, d3, d4;
    
    if ((mode == UP)||(mode == DOWN)) {
	d1 = sx - e1x1;
	d2 = sx - e2x1;
	d3 = fabs(sy - e1y0);
	d4 = fabs(sy - e2y0);
    } else {
	d1 = sy - e1y1;
	d2 = sy - e2y1;
	d3 = fabs(sx - e1x0);
	d4 = fabs(sx - e2x0);
    }

    if (!e1_has_bend) {
	if (!e2_has_bend) {
#if DEBUG
	    cout << "compare: 2 edges without bends" << endl;
#endif
	    return false;
	} else { // e2 has bend, e1 not
	    return (d2 <= 0);
	}
    } else { 
	if (!e2_has_bend) { // e1 has bend, e2 not
	    return(d1 > 0);
	}
    }

    if ((d1 * d2) < 0) {
	return (d1 > 0);
    }

    if (d1 > 0) {
	return (d3 < d4);
    } else {
	return (d3 >= d4);
    }
}

//
// compare_edges / sort edges:
//
// cause of MSVCC dos not (yet) support member templates
// we cannot use the preimplemented sort-funktion for lists ...
//
#ifdef __GTL_MSVCC

void Iterative_Constraint_Spring_Embedder::sort_edges(
    list<edge> &to_sort, Iterative_Constraint_Spring_Embedder &_se,
    node _source, int _mode)
{
    // generate compare_edges
    compare_edges comp(_se, _source, _mode);

    list<edge> temp;
	
    // ok, no really good implementation ( insertion sort O(n^2) ),
    list<edge>::iterator end = to_sort.end();
    list<edge>::iterator it = to_sort.begin();
    while (it != end)
    {
        // search for position
        list<edge>::iterator temp_end = temp.end();
        list<edge>::iterator temp_it = temp.begin();
        while ((temp_it != temp_end) && !comp(*it, *temp_it))
    	    ++temp_it;

        // insert element
        temp.insert(temp_it, *it);
    
	// delete Element from origin list
	to_sort.erase(it);
	it = to_sort.begin();
    }

    // copy temp to to_sort
    to_sort.merge(temp);
}

#endif // __GTL_MSVCC

//
// distribute_edges:
// see also compare_edges 
//
    
void Iterative_Constraint_Spring_Embedder::distribute_edges()
{
    node n0;
    forall_nodes (n0, *this->attached()) {
	if (!this->the_bend_node[n0]) {
	    list <edge> up, down, left, right;
	    edge e;

	    forall_inout_edges(e, n0) {
		node n1 = n0.opposite(e);
	    
		const double x0 = this->the_v_constraint[n0]->coord();
		const double y0 = this->the_h_constraint[n0]->coord();
		const double x1 = this->the_v_constraint[n1]->coord();
		const double y1 = this->the_h_constraint[n1]->coord();
		
		const double dx = x0 - x1;
		const double dy = y0 - y1;

		if (!dy) {
		    if (dx < 0) {
			right.push_back(e);
		    } else {
			left.push_back(e);
		    }
		} else {
#if DEBUG
		    if (dx) {
			cout << "distribute_edges: Non aligned edge !"
			     << " dx " << dx << " dy " << dy << endl;
		    }
#endif
		    if (dy < 0) {
			down.push_back(e);
		    } else {
			up.push_back(e);
		    }	
		}
	    }

#ifdef __GTL_MSVCC
	    if (up.size() > 1) {
		this->sort_edges(up, *this, n0, UP);
		this->set_edge_anchors(up, n0, UP);
	    }

	    if (down.size() > 1) {
		this->sort_edges(down, *this, n0, DOWN);
		this->set_edge_anchors(down, n0, DOWN);
	    }
	    if (left.size() > 1) {
		this->sort_edges(left, *this, n0, LEFT);
		this->set_edge_anchors(left, n0, LEFT);
	    }
	    if (right.size() > 1) {
		this->sort_edges(right, *this, n0, RIGHT);
		this->set_edge_anchors(right, n0, RIGHT);
	    }
#else
	    if (up.size() > 1) {
		up.sort(compare_edges(*this, n0, UP));
		this->set_edge_anchors(up, n0, UP);
	    }
	    if (down.size() > 1) {
		down.sort(compare_edges(*this, n0, DOWN));
		this->set_edge_anchors(down, n0, DOWN);
	    }
	    if (left.size() > 1) {
		left.sort(compare_edges(*this, n0, LEFT));
		this->set_edge_anchors(left, n0, LEFT);
	    }
	    if (right.size() > 1) {
		right.sort(compare_edges(*this, n0, RIGHT));
		this->set_edge_anchors(right, n0, RIGHT);
	    }
#endif
	}
    }
}


//
// set_bend_and_anchor:
//
// Adjust a bend to the edge anchor
//

void Iterative_Constraint_Spring_Embedder::set_bend_and_anchor(
    edge e, node n, double abs_pos, int mode)
{
    if (!this->the_bend_edge[e]) {
#if DEBUG
	cout << "Called set_bend_and_anchor for no bend edge" << endl;
	return;
#endif
    }

    node bend = n.opposite(e);
    
    switch(mode) {
	case UP:
	case DOWN:
	    this->gt_graph()->gt(bend).graphics()->x(abs_pos);
	    break;
	case LEFT:
	case RIGHT:
	    this->gt_graph()->gt(bend).graphics()->y(abs_pos);
	    break;
    }
}


//
// set_edge_anchors:
// see also distribute_edges
//
// We use a little trick here:
// There is exactly one edge which has no bend (no connection to
// a dummy-edge) for each direction.
// This edge will be drawn in the the middle of the side - The
// according nodes are aligned! The edges are split into two groups
// for each side. The edges before and beneath such middle edge.
//

void Iterative_Constraint_Spring_Embedder::set_edge_anchors(
    list<edge> &edge_list, node n, int mode)
{
    list<edge> pre_mid, post_mid;

    bool pre = true;

    list<edge>::const_iterator it, end;

    for(it = edge_list.begin(), end = edge_list.end(); it != end; ++it)
    {
	if (!this->the_bend_edge[*it]) {
	    pre = false;
	} else {
	    if (pre == true) {
		pre_mid.push_back(*it);
	    } else {
		post_mid.push_back(*it);
	    }
	}
    }

    double length, pos;
    if ((mode == UP) || (mode == DOWN)) {
	length = this->the_node_width[n];
	pos = this->the_v_constraint[n]->coord();
    } else {
	length = this->the_node_height[n];
	pos = this->the_h_constraint[n]->coord();
    }
    
    int count = 0;
    double spacing = pre_mid.size()+1;

    if (pre) {
	spacing /= 2;
    }
    
    for(it = pre_mid.begin(), end = pre_mid.end(); it != end; ++it)
    {
	double rel_pos = (double)(++count) / spacing - 1;

	double abs_pos = pos + rel_pos * length;
	
	this->set_bend_and_anchor(*it, n, abs_pos, mode);
    }

    count = 0;
    spacing = post_mid.size()+1;
    
    for(it = post_mid.begin(), end = post_mid.end(); it != end; ++it)
    {	
	double rel_pos = (double)(++count) / spacing;
	double abs_pos = pos + rel_pos * length;
	
	this->set_bend_and_anchor(*it, n, abs_pos, mode);
    }
}


//
// remove_obsolete_bends:
//
// Here we remove bends between nodes where we can draw the edge
// orthogonal without bends.
//

void Iterative_Constraint_Spring_Embedder::remove_obsolete_bends()
{
    const double shift = 1;

    list<node>::const_iterator it, end;

    for(it = the_bend_nodes.begin(), end = the_bend_nodes.end();
	it != end; ++it)
    {
	node source, target;
	
	for (source = *it;
	     this->the_bend_node[source];
	     source = source.in_edges_begin()->source());
	     
	for (target = *it;
	     this->the_bend_node[target];
	     target = target.adj_edges_begin()->target());

	const double x = this->gt_graph()->gt(*it).graphics()->x();
	const double y = this->gt_graph()->gt(*it).graphics()->y();

	const double source_x = this->the_v_constraint[source]->coord();
	const double source_y = this->the_h_constraint[source]->coord();
	const double source_dx = fabs(source_x - x);
	const double source_dy = fabs(source_y - y);
	const double source_h = this->the_node_height[source];
	const double source_w = this->the_node_width[source];
	
	const double target_x = this->the_v_constraint[target]->coord();
	const double target_y = this->the_h_constraint[target]->coord();
	const double target_dx = fabs(target_x - x);
	const double target_dy = fabs(target_y - y);
	const double target_h = this->the_node_height[target];
	const double target_w = this->the_node_width[target];
	
	if ((source_dx < source_w) && (source_dy < source_h)) {
#if DEBUG
	    cout << "Shifting bend from source node" << endl;
#endif
	    edge e_in = *it->in_edges_begin();
	    
	    if (this->the_edge_alignment[e_in] == V_ALIGN) {
		if (source_x > target_x) {
		    this->gt_graph()->gt(*it).graphics()->x(
			source_x - source_w - shift);
		} else {
		    this->gt_graph()->gt(*it).graphics()->x(
			source_x + source_w + shift);
		}
	    } else {
		if (source_y > target_y) {
		    this->gt_graph()->gt(*it).graphics()->y(
			source_y - source_h - shift);
		} else {
		    this->gt_graph()->gt(*it).graphics()->y(
			source_y + source_h + shift);
		}
	    }
	}

	if ((target_dx < target_w) && (target_dy < target_h)) {
#if DEBUG
	    cout << "Shifting bend from target node" << endl;
#endif
	    edge e_out = *it->adj_edges_begin();
	    
	    if (this->the_edge_alignment[e_out] == V_ALIGN) {
		if (target_x > source_x) {
		    this->gt_graph()->gt(*it).graphics()->x(
			target_x - target_w);
		} else {
		    this->gt_graph()->gt(*it).graphics()->x(
			target_x + target_w);
		}
	    } else {
		if (target_y > source_y) {
		    this->gt_graph()->gt(*it).graphics()->y(
			target_y - target_h);
		} else {
		    this->gt_graph()->gt(*it).graphics()->y(
			target_y + target_h);
		}
	    }
	}
    }
}


//
// create_gt_edges:
//
// Finally we have to replace the dummy node and edges with
// real gt_edges with bends.
//

void Iterative_Constraint_Spring_Embedder::create_gt_edges()
{
    list<node>::const_iterator it, end;

    for(it = the_bend_nodes.begin(), end = the_bend_nodes.end();
	it != end; ++it)
    {
	node n = *it;
	
	const edge e_in = *n.in_edges_begin();
	const edge e_out = *n.adj_edges_begin();

	const node source = e_in.source();
	const node target = e_out.target();

	string label = this->gt_graph()->gt(e_in).label();
	
	GT_Polyline line =
	    this->gt_graph()->gt(e_in).graphics()->line();
	    
	line.pop_back();
	
	line.push_back(GT_Point(
	    this->gt_graph()->gt(n).graphics()->x(),
	    this->gt_graph()->gt(n).graphics()->y()));
	
	GT_Polyline line_out =
	    this->gt_graph()->gt(e_out).graphics()->line();

	line_out.pop_front();
	//line.conc(line_out);
	line.splice(line.end(), line_out);

	this->del_edge(e_in);
	this->del_edge(e_out);

	const edge bend_edge = this->new_edge(source, target);
	this->gt_graph()->gt(bend_edge).label(label);
	
	this->gt_graph()->gt(bend_edge).graphics()->line(line);
	this->gt_graph()->gt(bend_edge).edge_nei()->
            set_EA_default_function(
		GT_Keys::EA_orthogonal, GT_Source);
        this->gt_graph()->gt(bend_edge).edge_nei()->
            set_EA_default_function(
		GT_Keys::EA_orthogonal, GT_Target);
    }

    //
    // MR: 5.2.1999:
    //
    // The list the_bend_nodes can contain nodes which are contained 
    // in the lists the_[h/v]_representative, thus the bends are deleted after 
    // the associated constraints are deleted, which is normally done in the
    // destructor of this class.
    //
    // PLEASE NOTE: This is only a workaround
    //

    //    list<node> del = this->the_bend_nodes;
    //    for(it = del.begin(), end = del.end(); it != end; ++it)
    //	this->del_node(*it);
}


//
// iterative_constraint_placement:
//
// Well, this is the main method to be called.
// This world starts here.
//

string Iterative_Constraint_Spring_Embedder::
iterative_constraint_placement()
{
    if(!this->optimal_distance()) {
	this->optimal_distance(sqrt((double)this->window_width() *
	    (double)this->window_height() /
	    (double)this->attached()->number_of_nodes()/15));
    }
    if(!minimal_distance()) {
	this->minimal_distance(this->optimal_distance()/2);
    }
	
#if DEBUG
    cout << "----- Iterative_Constraint_Spring_Embedder ------" << endl;
#endif
	
    edge e;
    forall_edges (e, *this->attached()) {
        this->gt_graph()->gt(e).edge_nei()->
            set_EA_default_function(
		GT_Keys::EA_orthogonal, GT_Source);
        this->gt_graph()->gt(e).edge_nei()->
            set_EA_default_function(
		GT_Keys::EA_orthogonal, GT_Target);
    }
    
    //
    // The next method performs the base-layout with CFR.
    // CAVEAT: Also the initilization of the structures is automaticly
    // done there. You have to call initilization otherwise. 
    //
	
    string message = this->force_directed_placement();
    if (message != "ok") {
	return message;
    }
    
    if (!this->has_geometric_constraints()) {
	this->turn_graph();
    }
	
    bool undirected = (!this->attached()->is_directed());
	
    if (undirected) {
	this->attached()->make_directed();
    }
    
    this->init_edge_alignments();
	
    int new_bends = 0;
    int bend_run = 0;
    
    do {
	double arc = 5;
		
	while(arc <= 40) {
#if DEBUG
	    cout << "Run with arc " << arc << endl;
#endif
			
	    double count = this->insert_constraints(arc);
			
	    if((count) || (new_bends)) {
		this->springembedder_phase(
		    phase_damping[2], max_iteration[2],
		    false, false, true, true);
		new_bends = 0;
	    } else {
		arc += 5;
	    }
	}

	if ((this->new_bends()) && (bend_run++ < 1)) {
	    new_bends = this->split_edges();
#if DEBUG
	    cout << "Inserted " << new_bends << " new bends" << endl;
#endif
	} else {
	    new_bends = 0;
	}
    } while (new_bends > 0);
	
    //
    // You may use this for debugging purpose.
    // Otherwise it may not be nice to change the colours
    // without a request from the user.
    //
	
#if DEBUG
#if 0
    this->make_readable_labels();
#endif
#endif
	
    //
    // This should be called even is fit_graph == false,
    // since it sets the layout to a valid position,
    // that means ensures that all coordiantes are greater 0.
    //

    this->fit_graph_to_window(20, false);

    this->update_gt_properties();
    this->distribute_edges();
    //
    // Yes the second call is - seldom - neccessary. This problem
    // reminds of the problems with the second run on TeX and it
    // is really similiar.
    //
    this->distribute_edges();
    
    this->remove_obsolete_bends();
    
    this->prepare_edges();


    this->create_gt_edges();
	
    if (undirected) {
	this->attached()->make_undirected();
    }
    
    return message;
}




