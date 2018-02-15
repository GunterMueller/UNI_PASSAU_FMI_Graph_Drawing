/* This software is distributed under the Lesser General Public License */
//
// cfr_layout.cpp
//
// This is the implementation of the main-methods of the class
// FR_Constraint_Graph, which allows to layout a graph using an
// enhanced Fruchterman Reingold springembedder algorithm to
// layout a graph. Labels are interpreted as constraints.
// See the README file for a description of the constraints.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/cfr_layout.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:26 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>
#include <gt_base/NEI.h>

#include <cmath>

#include "cfr_layout.h"
#include "cfr_io.h"	

#if 0
#include <sys/times.h>

int utime()
{
    struct tms tms_buf;
    times(&tms_buf);
    return (int) tms_buf.tms_utime;
}
#endif

//
// Constructor:
// Most Variables are set by cfr_algorithm
// using set_parameters.
//

FR_Constraint_Graph::FR_Constraint_Graph(
    GT_Graph* gt_graph)
{
    this->gt_graph(gt_graph);
    this->attached(gt_graph->attached());
}


//
// Destructor:
// Here we have to free the memory, which was allocated for
// the FR_One_Dimensional_Constraint.
//

FR_Constraint_Graph::~FR_Constraint_Graph()
{
    list<node>::const_iterator it, end;

    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	delete the_v_constraint[*it];
    }

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	delete the_h_constraint[*it];
    }
    
}


//
// reduce_constraints:
// This starts a recursive method in FR_One_Dimensional_Constraint,
// which removes redundant constraints.
//

void FR_Constraint_Graph::reduce_constraints(
    node_map<FR_One_Dimensional_Constraint*>& constraint,
    list<node>& representative)
{
    set<FR_One_Dimensional_Constraint*> empty_set;
	
    list<node>::const_iterator it, end;

    for(it = representative.begin(), end = representative.end();
	it != end; ++it)
    {
	constraint[*it]->comp_num(0);
    }

    for(it = representative.begin(), end = representative.end();
	it != end; ++it)
    {
	if (constraint[*it]->upper_constraints().empty()) {
	    //
	    // Top nodes must be initialized with -1,
	    // since the number of visits is increased instantly to 0.
	    //
	    constraint[*it]->comp_num(-1);
	    constraint[*it]->reduce_dependants(empty_set);
	}
    }

    for(it = representative.begin(), end = representative.end();
	it != end; ++it)
    {
	constraint[*it]->final_cut();
    }
}


//
// order_nodes:
// This starts a recursive method in FR_One_Dimensional_Constraint,
// which moves nodes violating a constraint downwards.
//

void FR_Constraint_Graph::order_nodes(
    node_map<FR_One_Dimensional_Constraint*>& constraint,
    list<node>& representative)
{
    list<node>::const_iterator it, end;

    for(it = representative.begin(), end = representative.end();
	it != end; ++it)
    {
	constraint[*it]->comp_num(0);
    }
	
    for(it = representative.begin(), end = representative.end();
	it != end; ++it)
    {
	if (constraint[*it]->upper_constraints().empty()) {
	    if (!(constraint[*it]->lower_constraints().empty())) {
		constraint[*it]->order_dependants(
		    HUGE, this->minimal_distance());
	    }
	}
    }
}


//
// opposite_pair_heuristic
// This is heuristic which helps with H- and V-Groups.
// Without this heuristic such groups are often knoted.
// This heuristic is described detailed in the
// according paper of this algorithm.
//

void FR_Constraint_Graph::opposite_pair_heuristic(
    const node_map<FR_One_Dimensional_Constraint*>& constraint,
    const list<Pair_of_Nodes>& opposite_pairs)
{
    list<Pair_of_Nodes>::const_iterator it, end;

    for(it = opposite_pairs.begin(), end = opposite_pairs.end();
	it != end; ++it)
    {
	const node n1 = it->node1();
	const node n2 = it->node2();
	
	double delta_old = constraint[n1]->coord() -
	    constraint[n2]->coord();

	if (fabs(delta_old) < this->the_optimal_matrix[n1][n2]) {
	    double displacement_node1 =
		constraint[n1]->displacement();
				
	    double displacement_node2 =
		constraint[n2]->displacement();
				
	    double delta_new = delta_old + displacement_node1 -
		displacement_node2;

	    if ((fabs(delta_new) > (2 * fabs(delta_old))) &&
		(((delta_old < 0) && (delta_new > 0)) ||
		    ((delta_old > 0) && (delta_new < 0)))) {
		this->the_swap_lock -= 1;
				
		if (this->the_swap_lock <= 0) {
		    this->the_swap_lock =
			4 * this->has_opposite_pairs()-1;
					
		    double swap = constraint[n2]->coord();
		    constraint[n2]->coord(
			constraint[n1]->coord());
		    constraint[n1]->coord(swap);
				
		    constraint[n1]->displacement(0);
		    constraint[n2]->displacement(0);
		}
	    }
	}	
    }
}


//
// calculate_repulsive_displacement:
// This reasembles the repulisve forces of the original algorithm.
// the_repulsive_factor is a special node_matrix to scale these
// forces. This is an additional heuristic to handle X- and Y-
// groups nicely.
//

void FR_Constraint_Graph::calculate_repulsive_displacement(
    const bool use_the_repulsive_factor,
    const bool increase_optimal_distance)
{
    static int lock = 0;
    bool changed_optimal_distance = false;
    lock--;
    const double overlap_pitch = this->overlap_pitch();

    graph::node_iterator it1, it2, end = attached()->nodes_end();

    for(it1 = attached()->nodes_begin(); it1 != end; ++it1)
    {
	for(it2 = it1, ++it2; it2 != end; ++it2)
	{
	    node n1 = *it1;
	    node n2 = *it2;

	    double scale = 1;

	    if (use_the_repulsive_factor)
	    {
		scale = this->the_repulsive_factor[n1][n2];
	    }
                                
	    if (scale) {
		double dx = this->the_v_constraint[n1]->coord() -
		    this->the_v_constraint[n2]->coord();
		double dy = this->the_h_constraint[n1]->coord() -
		    this->the_h_constraint[n2]->coord();
		
		if ((this->respect_sizes()) &&
		    (increase_optimal_distance) &&
		    (lock < 1)) {
		    const double h_overlap =
			fabs(dx) - this->the_node_width[n1] -
			this->the_node_width[n2] - overlap_pitch;
		    const double v_overlap =
			fabs(dy) - this->the_node_height[n1] -
			this->the_node_height[n2] - overlap_pitch;
		    
		    if ((h_overlap < 0) &&
			(v_overlap < 0)) {
			const MATRIX_TYPE additional_distance =
			    (MATRIX_TYPE)
			    max (1.0, (-h_overlap -v_overlap) / 20);

			this->the_optimal_matrix[n1][n2] +=
			    additional_distance;
			this->the_optimal_matrix[n1][n2] +=
			    additional_distance;
			changed_optimal_distance = true;
		    }
#if 0
		    else if ((h_overlap >
			(2 * this->optimal_distance())) &&
			(v_overlap > (2 * this->optimal_distance()))) {
			if (this->the_optimal_matrix[n1][n2] >
			    this->optimal_distance()) {
			    this->the_optimal_matrix[n1][n2] -= 1;
			    this->the_optimal_matrix[n2][n1] -= 1;
			    changed_optimal_distance = true;
#if DEBUG
			    cout << "Decreasing optimal matrix" << endl;
#endif
			}
		    }
#endif		    
		}

		const double length_2 =  dx * dx + dy * dy;
		
		if (length_2) {
		    scale *=  this->the_optimal_matrix[n1][n2]
			* this->the_optimal_matrix[n1][n2] / length_2;

		    dx *= scale;
		    dy *= scale;
		} else {
		    dx = rand();
		    dy = rand();
		}

		this->the_v_constraint[n1]->add_displacement(dx);
		this->the_v_constraint[n2]->add_displacement(-dx);
		this->the_h_constraint[n1]->add_displacement(dy);
		this->the_h_constraint[n2]->add_displacement(-dy);
	    }
	}
    }

    if (changed_optimal_distance) {
	lock = 5;
    }
}


//
// calculate_attractive_displacement:
//
 
void FR_Constraint_Graph::calculate_attractive_displacement()
{
    const double overlap_pitch = this->overlap_pitch();
    
    list<edge>::const_iterator it, end;

    for(it = the_edges.begin(), end = the_edges.end();
	it != end; ++it)
    {
        node n1 = it->source();
        node n2 = it->target();
 
        double distance;

	//
	// Distance is < 0 if the nodes overlap,
	// So this force becomes repulsive.
	//

	double dx = this->the_v_constraint[n1]->coord() -
	    this->the_v_constraint[n2]->coord();
	double dy = this->the_h_constraint[n1]->coord() -
	    this->the_h_constraint[n2]->coord();

	if (this->respect_sizes())
	{
	    const double abs_dx = fabs(dx);
	    const double abs_dy = fabs(dy);

	    double h_distance = abs_dx -
		this->the_node_width[n1] - this->the_node_width[n2];
	    double v_distance = abs_dy -
		this->the_node_height[n1] - this->the_node_height[n2];

	    if (abs_dx == 0) {
		if (abs_dy == 0) { // same position
#if DEBUG
		    cout << "Warning: same position (attractive)" << endl;
#endif
		    distance = v_distance + h_distance;

		} else { // vertical aligned
		    distance = v_distance;
		}
	    } else {
		if (abs_dy == 0) { // horizontal aligned
		    distance = h_distance;
		} else { // no h/v alignment
		    if (h_distance <= 0) {
			if (v_distance <=0) {
			    distance = h_distance + v_distance;
			} else {
			    distance = v_distance;
			}
		    } else {
			if (v_distance <= 0) {
			    distance = h_distance;
			} else {
			    distance = hypot(h_distance, v_distance);
			}
		    }
		}
	    }
	    
	    distance -= overlap_pitch;
	} else {
	    distance = hypot(dx, dy);
	}
	
        const double scale = distance / the_optimal_matrix[n1][n2];
        dx *= scale;
        dy *= scale;
 
        this->the_v_constraint[n1]->add_displacement(-dx);
        this->the_h_constraint[n1]->add_displacement(-dy);
        this->the_v_constraint[n2]->add_displacement(dx);
        this->the_h_constraint[n2]->add_displacement(dy);
    }
}


//
// calculate_length_constraint_displacement:
// Generate additional forces to strive for uniform
// edgelengths of given edge-groups.
// this->the_lengths_constraints is the list
// containing this groups.
//

void FR_Constraint_Graph::calculate_length_constraint_displacement()
{
    list<edge> group_list;

    list<list<edge> >::const_iterator it, end;
    for(it = the_lengths_constraints.begin(), end = the_lengths_constraints.end();
	it != end; ++it)
    {
	double avg_length = 0;

	list<edge>::const_iterator it2, end2;
	for(it2 = it->begin(), end2 = it->end(); it2 != end2; ++it2)
	{
	    node n1 = it2->source();
	    node n2 = it2->target();
		
	    double dx = this->the_v_constraint[n1]->coord() -
		this->the_v_constraint[n2]->coord();
	    double dy = this->the_h_constraint[n1]->coord() -
		this->the_h_constraint[n2]->coord();
			
	    avg_length += hypot(dx, dy);
	}

	avg_length /= it->size();
		
	for(it2 = it->begin(), end2 = it->end(); it2 != end2; ++it2)
	{
	    node n1 = it2->source();
	    node n2 = it2->target();
		
	    double dx = this->the_v_constraint[n1]->coord() -
		this->the_v_constraint[n2]->coord();
	    double dy = this->the_h_constraint[n1]->coord() -
		this->the_h_constraint[n2]->coord();
			
	    double length = hypot(dx, dy);
	    double force = (length - avg_length) / 3;

	    dx *= force;
	    dy *= force;

	    this->the_v_constraint[n1]->add_displacement(-dx);
	    this->the_h_constraint[n1]->add_displacement(-dy);
	    this->the_v_constraint[n2]->add_displacement(dx);
	    this->the_h_constraint[n2]->add_displacement(dy);	
	}
    }
}


//
// calculate_constraint_displacement:
// Generate extra forces according to the constraints.
// This option is disabled be default.
//

void FR_Constraint_Graph::calculate_constraint_displacement()
{
    list<node>::const_iterator it, end;

    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	this->the_v_constraint[*it]->calculate_constraint_displacement(
	    this->optimal_distance(), this->minimal_distance());
    }

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	this->the_h_constraint[*it]->calculate_constraint_displacement(
	    this->optimal_distance(), this->minimal_distance());
    }
}


//
// limit_displacement:
// This is somewhat different from the original algorithm.
// Here we limit each force according to its length.
// In the original paper all forces are cuted at the same
// length.
//

double FR_Constraint_Graph::limit_displacement(double damp)
{
    node n;
    node_map<double> limit_force(*this->attached(), 1.0);
    
    double max_force = 0;
    
    forall_nodes (n, *this->attached()) {
	double length = hypot(this->the_h_constraint[n]->displacement(),
	    this->the_v_constraint[n]->displacement());

	if (length > max_force) {
	    max_force = length;
	}
	
	//  The next is: limit = sqrt(length) / damp / length;

	double limit = 1 / damp / sqrt(length);
	
	if (limit < limit_force[n]) {
	    limit_force[n] = limit;
	}
    }

    list<node>::const_iterator it, end;

    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	double dx = this->the_v_constraint[*it]->displacement();
	this->the_v_constraint[*it]->displacement(dx * limit_force[*it]);
    }

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	double dy = this->the_h_constraint[*it]->displacement();
	this->the_h_constraint[*it]->displacement(dy * limit_force[*it]);
    }

    return max_force;
}


//
// constraint_displace_nodes:
// Here we move the nodes but only as long as we do not
// violate any constraints.
//

void FR_Constraint_Graph::constraint_displace_nodes()
{
    list<node>::const_iterator it, end;

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	node n = *it;
	
	double min = -HUGE;
	double max = HUGE;

	list<node>::const_iterator it2, end2;

	for(it2 = the_h_orthogonal_constraint[n].begin(),
	    end2 = the_h_orthogonal_constraint[n].end();
	    it2 != end2; ++it2)
	{
	    node n2 = *it2;
	    
	    double up, down;
	    up = down = this->the_h_constraint[n2]->coord();
	    up += this->the_node_height[n2] + this->the_node_height[n];
	    down -= this->the_node_height[n2] + this->the_node_height[n];
	    
	    if (up < max) {
		max = up;
	    }
	    
	    if (down > min) {
		min = down;
	    }
	}
	
	this->the_h_constraint[n]->constraint_displace(
	    this->minimal_distance(), min, max);
    }

    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	node n = *it;
	double min = -HUGE;
	double max = HUGE;

	list<node>::const_iterator it2, end2;

	for(it2 = the_v_orthogonal_constraint[n].begin(),
	    end2 = the_v_orthogonal_constraint[n].end();
	    it2 != end2; ++it2)
	{
	    node n2 = *it2;
	    
	    double up, down;
	    up = down = this->the_v_constraint[n2]->coord();
	    up += this->the_node_width[n2] + this->the_node_width[n];
	    down -= this->the_node_width[n2] + this->the_node_width[n];
	    
	    if (up < max) {
		max = up;
	    }
	    
	    if (down > min) {
		min = down;
	    }
	}

	this->the_v_constraint[n]->constraint_displace(
	    this->minimal_distance(), min, max);
    }
}


//
// unconstraint_displace_nodes:
// Here we move the nodes. 
// This function gets called if there are no geometrical
// constraints in the graph. No check is done.
//

void FR_Constraint_Graph::unconstraint_displace_nodes()
{
    list<node>::const_iterator it, end;

    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	this->the_v_constraint[*it]->unconstraint_displace();
    }

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	this->the_h_constraint[*it]->unconstraint_displace();
    }	
}


//
// group_initialization:
// Start the group_initialization in FR_One_Dimensional_Constraint.
//

void FR_Constraint_Graph::group_initialization()
{
    int range_x = 0, range_y = 0;

    this->has_geometric_constraints(false);
    
    if (this->random_placement()) {
	range_x = this->window_width();
	range_y = this->window_height();
    }

    this->init_group_constraints(
	this->the_h_constraint, this->the_h_representative,
	'h', "h", range_y);
    this->init_group_constraints(
	this->the_v_constraint, this->the_v_representative,
	'v', "v", range_x);

    this->init_orthogonal_constraints(
	this->the_h_orthogonal_constraint, "H");
    this->init_orthogonal_constraints(
	this->the_v_orthogonal_constraint, "V");
}


void FR_Constraint_Graph::initilization(bool &fit_graph)
{
    const int num_nodes = this->attached()->number_of_nodes();
    const int num_edges = this->attached()->number_of_edges();
    
    if (this->optimal_distance() <= 0) {
	this->optimal_distance(((double)num_edges / (double)num_nodes) *
	    sqrt((double)this->window_width() *
		(double)this->window_height() / (double)num_nodes) / 5);
	fit_graph = true;

	if (this->optimal_distance() > 300) {
	    this->optimal_distance(300);
	}
    }

    if (this->minimal_distance() <= 0) {
	this->minimal_distance(this->optimal_distance() / 2);
    } else {
	fit_graph = false;
    }

    this->init_node_width_and_height();
    
#if DEBUG
    cout << "Optimal Distance " << this->optimal_distance() 
	 << " minimal_distance " << this->minimal_distance() << endl;
#endif

    this->the_repulsive_factor.init(*this->attached());
    node n;
    forall_nodes(n, *this->attached()) {
	this->the_repulsive_factor[n].init(*this->attached(), 1);
    }

    this->has_repulsive_factors(false);
    this->swap_lock(0);
    this->set_group_repulsive_factors(this->the_v_constraint, 'v');
    this->set_group_repulsive_factors(this->the_h_constraint, 'h');

    this->has_opposite_pairs(
	this->the_v_opposite_pairs.size() +
	this->the_h_opposite_pairs.size());
	
    this->read_lengths_constraints();

    this->reduce_constraints(this->the_v_constraint,
	this->the_v_representative);
    this->reduce_constraints(this->the_h_constraint,
	this->the_h_representative);
	
    this->order_nodes(this->the_v_constraint, this->the_v_representative);
    this->order_nodes(this->the_h_constraint, this->the_h_representative);
}



//
// force_directed_placement:
// Well, this is the main method to be called.
// 
//


string FR_Constraint_Graph::force_directed_placement()
{
#if DEBUG
#if 0
    {
	node_map<bool> is_reached(*this->attached(), false);
	node_map<int> comp_num(*this->attached(), 0);
	
	list<node> component;

	int max = -1, comp = 0;

	node n;

	forall_nodes (n,*this->attached()) {
	    if (!is_reached[n]) {
		component = DFS(*this->attached(), n, is_reached);

		if (component.length() > max) {
		    max = component.length();
		    comp++;

		    node v;
		    
		    forall (v, component) {
			comp_num[v] = comp;
		    }
		}
	    }
	}

	list<node> waste;
	
	forall_nodes (n, *this->attached()) {
	    if(comp_num[n] != comp) {
		waste.append(n);
	    }
	}

	forall (n, waste) {
	    this->attached()->del_node(n);
	}

	if (waste.length()) {
	    cout << "Deleted " << waste.length()
		 << " nodes of small components !!!!!!" << endl; 
	    return "Deleted small components - retry";
	}
    }
#endif
#endif


#if DEBUG
    cout << "-------------------------------------------------" << endl;
#endif

    int phase_iteration = 0, phase = 0;
    double force_4 = 0.0, force_3 = 0.0, force_2 = 0.0, force_1 = 0.0;
    double ratio_1, ratio_2;
    double damp = this->phase_damping[0];
    bool fit_graph = false;

    this->group_initialization();
    this->read_constraints();

    string message = this->check();
    if (message != "ok") {
	return message;
    }
    
    this->initilization(fit_graph); 

    bool use_the_repulsive_factor = this->has_repulsive_factors();
    bool increase_optimal_distance = true;
    //
    // currently alwasy true - consider this as a compile time option.
    // You may wan't to change the algorithm that this is false while
    // first phase of the algorithm.
    //
    //  bool increase_optimal_distance = false;
    //

    while (phase < MAX_PHASE) {
	phase_iteration++;
	
	this->calculate_attractive_displacement();

	if ((this->constraint_forces()) &&
	    (this->has_geometric_constraints())) {
	    this->calculate_constraint_displacement();
	}

	if ((phase == 0) && (this->has_opposite_pairs())) {
	    this->opposite_pair_heuristic(the_h_constraint,
		the_v_opposite_pairs);
	    this->opposite_pair_heuristic(the_v_constraint,
		the_h_opposite_pairs);
	}

	this->calculate_repulsive_displacement(
	    use_the_repulsive_factor,
	    increase_optimal_distance
	    );
		
	if ((phase > 1) && (this->has_lengths_constraints())) {
	    this->calculate_length_constraint_displacement();
	}
		
	force_4 = force_3;
	force_3 = force_2;
	force_2 = force_1;
			
	force_1 = this->limit_displacement(damp);
	
	if (this->has_geometric_constraints()) {
	    this->constraint_displace_nodes();
	} else {
	    this->unconstraint_displace_nodes();
	}

	if (this->animation()) {
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

#if DEBUG
#if 0
	printf(": %7.4f  %7.4f  %7.4f\n",force_1, ratio_1, ratio_2);
#endif
#endif
	if ((phase_iteration >= this->max_iteration[phase]) ||
	    ((force_4 != 0) &&
		((force_1 < this->minimal_force()) || 
		    ((ratio_1 < this->vibration_ratio()) &&
			(ratio_2 < this->vibration_ratio()))))) {

#if DEBUG
	    cout << "Phase " << phase
		 << " iteration " << phase_iteration << endl;
#endif

	    if ((phase == 0) && (!this->has_opposite_pairs())) {
		phase++;
	    }

	    phase_iteration = 0;
			
	    force_4 = force_3 = force_2 = force_1 = 0;
			
	    damp = this->phase_damping[++phase];			
	    if (phase >= 2) {
		use_the_repulsive_factor = false;
		increase_optimal_distance = true;
	    }
	}
    }
    
    if (this->colour_the_nodes()) {
	this->calculate_attractive_displacement();
	if ((this->constraint_forces()) &&
	    (this->has_geometric_constraints())) {
	    this->calculate_constraint_displacement();
	}
		
	this->calculate_repulsive_displacement(
	    use_the_repulsive_factor, false);
		
	if (this->has_lengths_constraints()) {
	    this->calculate_length_constraint_displacement();
	}
	this->colour_nodes();
    }

    //
    // You may use this for debugging purpose.
    // Otherwise it may not be nice to change the colours
    // without a request from the user.
    //

#if DEBUG
#if 0
    else {
	this->make_readable_labels();
    }
#endif
#endif

    //
    // This should be called even is fit_graph == false,
    // since it sets the layout to a valid position,
    // that means ensures that all coordiantes are greater 0.
    //

    this->fit_graph_to_window(20, false);
    
    this->update_gt_properties();

    return message;
}



