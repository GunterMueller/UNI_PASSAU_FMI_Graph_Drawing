/* This software is distributed under the Lesser General Public License */
//
// cfr_io.cpp
//
// This is the implementation of the methods of the class
// FR_Constraint_Graph, which are used to read the constraints,
// initilize the algorithm or process the result.
//
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/cfr_io.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:23 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>

#include <map>
#include <stack>

#include "cfr_layout.h"

//
// set_parameters:
// Main intialization method.
// This is called from cfr_algorithm.
//

void FR_Constraint_Graph::set_parameters(
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
    double yoffset)
{
    this->animation(animation);
    this->colour_the_nodes(colour_the_nodes);
    this->constraint_forces(constraint_forces);
    this->delimiter(delimiter);
    
    this->minimal_distance(minimal_distance);
    this->minimal_force(minimal_force);
	
    this->max_iteration[0] =
	phase1_max_iteration * this->attached()->number_of_nodes();
    this->max_iteration[1] =
	phase2_max_iteration * this->attached()->number_of_nodes();
    this->max_iteration[2] =
	phase3_max_iteration * this->attached()->number_of_nodes();

    this->new_bends(new_bends);
    this->optimal_distance(optimal_distance);
    
    this->phase_damping[0] = phase1_damping;
    this->phase_damping[1] = phase2_damping;
    this->phase_damping[2] = phase3_damping;

    this->respect_sizes(respect_sizes);
    this->random_placement(random_placement);

    this->tcl_graph(tcl_graph);
    this->tcl_interp(tcl_interp);

    this->vibration_ratio(vibration_ratio);

    this->window_width(window_width);
    this->window_height(window_height);

    this->xoffset(xoffset);
    this->yoffset(yoffset);
}


//
// notify_errors:
//
// This is called from check.
// The group_count indicates errors.
//
// Backedges closing a loop are coloured red.
// Backedges closing a cycle are coloured black.
//

void FR_Constraint_Graph::notify_errors(
    const node_map<FR_One_Dimensional_Constraint*>& constraint)
{
    GT_Key ok_fill = GT_Keys::white;
    GT_Key err_loop_fill = GT_Keys::red;
    GT_Key err_cycle_fill = GT_Keys::black;
	
    GT_Key ok_foreground = GT_Keys::black;
    GT_Key err_loop_foreground = GT_Keys::black;
    GT_Key err_cycle_foreground = GT_Keys::white;
	
    node n;
	
    forall_nodes (n, *this->attached()) {
	if(constraint[n]->group_count() == ERR_LOOP) {
	    this->gt_graph()->gt(n).graphics()
		->fill(err_loop_fill);
	    this->gt_graph()->gt(n).label_graphics()
		->fill(err_loop_foreground);
	}
	else if(constraint[n]->group_count() == ERR_CYCLE) {
	    this->gt_graph()->gt(n).graphics()
		->fill(err_cycle_fill);
	    this->gt_graph()->gt(n).label_graphics()
		->fill(err_cycle_foreground);
	} else {
	    this->gt_graph()->gt(n).graphics()
		->fill(ok_fill);
	    this->gt_graph()->gt(n).label_graphics()
		->fill(ok_foreground);
	}
	// Force a redraw so that the colors will be updated
	this->gt_graph()->gt(n).set_changed
	    (GT_Common_Attributes::tag_label);
    }
}


//
// check:
// Here we actually look for cycles and loops in the constraints,
// using a recursive function check_dfs. As the name indicates
// this is a kind of DFS.
//

string FR_Constraint_Graph::check()
{
    bool cycle = false, loop = false;
    int dfs_num = 1, comp_num = 1;

    list<node>::const_iterator it, end;

    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	this->the_v_constraint[*it]->dfs_num(UNMARKED);
	this->the_v_constraint[*it]->comp_num(0);
    }

    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	if(this->the_v_constraint[*it]->dfs_num() == UNMARKED) {
	    this->the_v_constraint[*it]->dfs_num(++dfs_num);
	    this->the_v_constraint[*it]->check_dfs(
		dfs_num, comp_num, cycle, loop);
	    this->the_v_constraint[*it]->comp_num(++comp_num);
	}
    }

    if((cycle)||(loop)) {
	string message = "\nUnsolvable V-Constraints.";

	if(loop) {
	    message += "\nCheck the red nodes for illegal group-constraints combinations e.g. 'vr'.";
	}

	if(cycle) {
	    message += "\nConsider the constraints of the black nodes to eliminate cycles.";
	}
		
	this->notify_errors(this->the_v_constraint);
	return message;
    }

    dfs_num = comp_num = 1;

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	this->the_h_constraint[*it]->dfs_num(UNMARKED);
	this->the_h_constraint[*it]->comp_num(0);
    }

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	if(this->the_h_constraint[*it]->dfs_num() == UNMARKED) {
	    this->the_h_constraint[*it]->dfs_num(++dfs_num);

	    this->the_h_constraint[*it]->check_dfs(
		dfs_num, comp_num, cycle, loop);
	    this->the_h_constraint[*it]->comp_num(++comp_num);
	}
    }

    if((cycle)||(loop)) {
	string message = "\nUnsolvable H-Constraints.";

	if(loop) {
	    message += "\nCheck the red nodes for illegal group-constraints combinations e.g. 'ho'.";
	}

	if(cycle) {
	    message += "\nConsider the constraints of the black nodes to eliminate cycles.";
	}
		
	this->notify_errors(this->the_h_constraint);
	return message;
    }
	
    return "ok";
}


//
// parse_node_constraints:
// Revert node constraints if the edge has the wrong direction,
// otherwise simply lower the literals.
//

string FR_Constraint_Graph::parse_node_constraints(
    const string original, const bool source_node)
{
    string result, label, effect;

    if (source_node) {
	label = "OAUBRL";
	effect = "oaubrl";
    }
    else { // target_node 
	label = "OAUBRL";
	effect = "uboalr";
    }
		
    string::const_iterator it, end;
    for(it = original.begin(), end = original.end();
	it != end; ++end)
    {
	string::size_type pos = label.find(*it);

	if(pos != string::npos)
	    result += effect[pos];
	else
	    result += *it;
    }
    
    return result;
}


//
// remove_comment:
// Remeber there are two different modes for comments.
//

string FR_Constraint_Graph::remove_comment(
    const string original)
{
    string result = original;
    string::size_type offset;

    if(result.find(this->the_delimiter) == string::npos) {
	return "";
    }

    while ((offset = result.find(this->the_delimiter)) != string::npos) {
	//	result = result.del(0, offset + this->the_delimiter.length() - 1);
	result = result.substr(offset + this->the_delimiter.length());
    }

    return result;
}


//
// read_label:
// Join node and egde_labels.
//

string FR_Constraint_Graph::read_label(const edge e)
{
    node target = e.target();
    node source = e.source();

    string label = parse_node_constraints(
	remove_comment(this->gt_graph()->gt(source).label()), true);

    label += " " + parse_node_constraints(
	remove_comment(this->gt_graph()->gt(target).label()), false);

    label += " " + remove_comment(this->gt_graph()->gt(e).label());

    return label;
}


//
// read_constraints:
// This is the main parsing method.
// Here we collect all informations about geometric constraints,
// but not the H- and V-groups, which
// are already initialized.
//

void FR_Constraint_Graph::read_constraints()
{
    edge e;

    forall_edges (e, *this->attached()) {
	bool hidden = false;
		
	node target = e.target();
	node source = e.source();

	string label = read_label(e);

#if DEBUG
#if 0
	if(!label) {
	    cout << "WARNING label is 0, " << __FILE__
		 << " line " << __LINE__ << endl;
	}
#endif
#endif
	if(!(source == target)) {
	    string::const_iterator it, end;
	    for(it = label.begin(), end = label.end(); it != end; ++it)
	    {
		switch (*it) {
		    // 'o' and 'u' might be confusing but if you try to
		    // understand this remember that (0,0) is above (0,100)
		    case 'o': // o-berhalb
		    case 'a': // a-bove
			this->the_h_constraint[target]->
			    add_lower_constraint(the_h_constraint[source]);
			this->has_geometric_constraints(true);
			break;
		    case 'u': // u-nterhalb
		    case 'b': // b-elow
			this->the_h_constraint[source]->
			    add_lower_constraint(the_h_constraint[target]);
			this->has_geometric_constraints(true);
			break;
		    case 'r': // r-echts / r-ight
			this->the_v_constraint[source]
			    ->add_lower_constraint(
				the_v_constraint[target]);
			this->has_geometric_constraints(true);
			break;
		    case 'l': // l-inks / l-eft
			this->the_v_constraint[source]->
			    add_upper_constraint(the_v_constraint[target]);
			this->has_geometric_constraints(true);
			break;
		    case '*':
			// This is just a constraint edge - no graph edge
			hidden = true;
			break;
		}
	    }
	} else {
	    hidden = true;
	}

	if(hidden) {
	    this->the_hidden_edges.push_back(e);
	} else {
	    this->the_edges.push_back(e);
	}
    }
}


//
// read_lengths_constraints:
// Here we parse the length-constraints, which are indicated
// by natural numbers.
//

void FR_Constraint_Graph::read_lengths_constraints()
{
    edge e;
    map<int, int> characteristic;
    edge_map <int> lengths_group(*this->attached(), -1); 

    this->has_lengths_constraints(false);
	
    forall_edges (e, *this->attached()) {

	string label = read_label(e);
	string digits;

	string::const_iterator it, end;
	for(it = label.begin(), end = label.end(); it != end; ++it)
	{
	    if (*it >= '0' && *it <= '9') {
		digits += *it;
	    } else {
		int group = atoi(digits.c_str());
		digits = "";

		if (group > 0) {
		    if(characteristic.find(group) == characteristic.end())
			characteristic[group] = group;
		    
		    if (lengths_group[e] < 0) {
			lengths_group[e] =  characteristic[group];
		    } else { // group for this edge already defined
			edge e2;

			forall_edges (e2, *this->attached()) {
			    if(lengths_group[e2] == characteristic[group]) {
				lengths_group[e2] = lengths_group[e];
			    }
			}

			characteristic[group] = lengths_group[e];
		    }
		}
	    }
	}
    }
    
    map<int,int>::const_iterator it, end;
    for(it = characteristic.begin(), end = characteristic.end();
	it != end; ++it)
    {
	//    forall_defined(group, characteristic) {
	list<edge> group_list;

	if(it->first == it->second)
	{
	    //	if(group == characteristic[group]) {
	    list<edge>::const_iterator it2, end2;
	    for(it2 = the_edges.begin(), end2 = the_edges.end();
		it2 != end2; ++it2)
	    {
		//	    forall (e, this->the_edges) {
		if(lengths_group[*it2] == it->first) {
		    group_list.push_back(*it2);
		}
	    }

	    if(group_list.size()) {
		this->the_lengths_constraints.push_back(group_list);
		this->has_lengths_constraints(true);
	    }
	}
    }
}


//
// set_group_repulsive_factors:  
// This initializes another heuristic handling H- and V-groups.
// We calculate a scale-facor for the repulsive forces
// between all pairs of nodes, which is used in the first
// phase of the algorithm.
//

void FR_Constraint_Graph::set_group_repulsive_factors(
    const node_map<FR_One_Dimensional_Constraint*>& constraint,
    const char dimension)
{
    this->the_h_opposite_pairs.clear();
    this->the_v_opposite_pairs.clear();

    list<node>::const_iterator it1, it2, nend = attached()->nodes_end();
    list<edge>::const_iterator it, end;
    
    for(it1 = attached()->nodes_begin(); it1 != nend; ++it1)
    {
	for(it2 = it1, ++it2; it2 != nend; ++it2)
	{
	    if (constraint[*it1] == constraint[*it2])
	    {
		this->the_repulsive_factor[*it1][*it2] = 0;
	    }
	}
    }

    node_map<int> adj_member(*this->attached(), 0);

    for(it = the_edges.begin(), end = the_edges.end(); it != end; ++it)
    {
	node source = it->source();
	node target = it->target();
		
	if (constraint[source] == constraint[target]) {
	    adj_member[source]++;
	    adj_member[target]++;
	}
    }

    for(it1 = attached()->nodes_begin(); it1 != nend; ++it1)
    {
	for(it2 = it1, ++it2; it2 != nend; ++it2)
	{
	    if (constraint[*it1] == constraint[*it2])
		if(1) {
		    if ((adj_member[*it1] == 1) && (adj_member[*it2] == 1)) {
			MATRIX_TYPE factor;
					
			factor = constraint[*it1]->group_count() - 1;
			factor *= factor;

			this->the_repulsive_factor[*it1][*it2] = factor;
					
			Pair_of_Nodes opposite_pair(*it1, *it2);
					
			if(dimension == 'v') {
			    this->the_v_opposite_pairs.push_back(
				opposite_pair);
			} else { // if(dimension == 'h')
			    this->the_h_opposite_pairs.push_back(
				opposite_pair);
			}
		    }
		}

	    if(this->the_repulsive_factor[*it1][*it2] != 1) {
		this->has_repulsive_factors(true);
	    }
	}
    }
}


//
// init_node_width_and_height:
// Initialize all node_size dependent variables, this includes
// the_optimal_matrix !
// 

void FR_Constraint_Graph::init_node_width_and_height()
{
    node n;
    double avg_width = 0, avg_height = 0;

    this->the_node_width.init(*this->attached(), 0);
    this->the_node_height.init(*this->attached(), 0);

    this->the_optimal_matrix.init(*this->attached());
    forall_nodes(n, *this->attached()) {
	this->the_optimal_matrix[n].init(*this->attached(), 0);
    }
    
    forall_nodes(n, *this->attached()) {
	GT_Node_Attributes& node_attrs = this->gt_graph()->gt(n);	    
	double size = node_attrs.graphics()->w() / 2;
	this->the_node_width[n] = size;
	avg_width += size;
	
	if (this->the_v_constraint[n]->max_size() < size) {
	    this->the_v_constraint[n]->max_size(size);
	}
	
	size = node_attrs.graphics()->h() / 2;
	this->the_node_height[n] = size;
	avg_height += size;
	
	if (this->the_h_constraint[n]->max_size() < size) {
	    this->the_h_constraint[n]->max_size(size);
	}
    }

    double avg = min(1.0, (avg_width + avg_height) /
	(2 * this->attached()->number_of_nodes()));
    this->overlap_pitch(avg / 6);

#if DEBUG
    cout << "Overlap pitch " << this->overlap_pitch() << endl; 
#endif

    list<node>::const_iterator it1, it2, nend = attached()->nodes_end();
    
    for(it1 = attached()->nodes_begin(); it1 != nend; ++it1)
    {
	for(it2 = it1, ++it2; it2 != nend; ++it2)
	{
	    this->the_optimal_matrix[*it1][*it2] =
		this->the_optimal_matrix[*it2][*it1]=
		(MATRIX_TYPE) (this->optimal_distance() +
		min(this->the_node_height[*it1], this->the_node_width[*it1]) +
		min(this->the_node_height[*it2], this->the_node_width[*it2]));
	}
    }
}


//
// init_group_constraints:
// Initialize the H-/ V-groups.
// Here we allocate memory using new. 
// This is released in the destructor of FR_Constraint_Graph. 
//

void FR_Constraint_Graph::init_group_constraints(
    node_map<FR_One_Dimensional_Constraint*>& constraint,
    list<node>& representative,
    const char dimension,
    const string group_identifier,
    int random_coords)
{
    // DESCRIPTION: We do a DFS over the attached graph, respcting only
    // edges, which have constraints. For every connected component of the
    // graph, a FR_One_Dimensional_Constraint is allocated and the entries
    // constraint[n] are set to point to it for all nodes in the component.
    
    constraint.init(*attached(), 0);
    node_map<bool> visited(*attached(), false);

    node n;
    forall_nodes(n, *attached())
    {
	if(!visited[n])
	{
	    stack<node> nodes;
	    nodes.push(n);

	    while(!nodes.empty())
	    {
		node current = nodes.top();
		nodes.pop();
		visited[current] = true;

		double coord =
		    random_coords	? rand() % random_coords :
		    dimension == 'v'	? gt_graph()->gt(n).graphics()->x() :
		    			  gt_graph()->gt(n).graphics()->y() ;

		if(current == n)
		{
		    constraint[current] =
			new FR_One_Dimensional_Constraint(coord);
		    
		    representative.push_back(current);	
		}
		else
		{
		    constraint[current] = constraint[n];
		    constraint[current]->add_member(coord);
		}    
		
		edge e;
		forall_inout_edges(e, current)
		{
		    string edge_label = read_label(e);
		    node n2 = current.opposite(e);

		    if (edge_label.find(group_identifier) != string::npos &&
			!visited[n2])
		    {
			nodes.push(n2);
		    }
		}
	    }
	}
    }	
}	


//
// init_orthogonal_constraints:
// Initialize orthogonal H-/ V-edges.
//

void FR_Constraint_Graph::init_orthogonal_constraints(
    node_map< list<node> >& orthogonal_constraint,
    const string group_identifier)
{
    edge e;
    bool has_orthogonal_constraints = false;

    list<node> empty_list;
    
    orthogonal_constraint.init(*this->attached(), empty_list);
	
    forall_edges (e, *this->attached()) {
	string edge_label = read_label(e);

	if (edge_label.find(group_identifier) != string::npos) {
	    orthogonal_constraint[e.source()].push_back(e.target());
	    orthogonal_constraint[e.target()].push_back(e.source());

	    has_orthogonal_constraints = true;
	}
    }
    
    if (has_orthogonal_constraints) {
	this->has_geometric_constraints(true);
    }
}


//
// make_readable_labels:
// Debug function
//

void FR_Constraint_Graph::make_readable_labels()
{
   node n;
	
    forall_nodes (n, *this->attached()) {
	GT_Node_Attributes& node_attrs = this->gt_graph()->gt(n);
		
	node_attrs.graphics()->fill(GT_Keys::white);
	node_attrs.label_graphics()->fill(GT_Keys::black);

	// Force a redraw so that the colors will be updated
	this->gt_graph()->gt(n).set_changed
	    (GT_Common_Attributes::tag_label);
    }

    edge e;

    forall_edges (e, *this->attached()) {
	GT_Edge_Attributes& edge_attrs = this->gt_graph()->gt(e);
	edge_attrs.label_graphics()->fill(GT_Keys::black);
    }
}


//
// colour_nodes:
// Coulour the nodes according to the remaining forces.
// Most tense node will be coloured red. 
//

void FR_Constraint_Graph::colour_nodes()
{
    node n;
    double max_force = 0, sum_forces = 0;
    node_map<double> force(*this->attached());
    GT_Key force_colour[11], white, black;

    force_colour[ 0] = GT_Keys::white;
    force_colour[ 1] = graphlet->keymapper.add("grey90");
    force_colour[ 2] = graphlet->keymapper.add("grey80");
    force_colour[ 3] = graphlet->keymapper.add("grey70");
    force_colour[ 4] = graphlet->keymapper.add("grey60");
    force_colour[ 5] = graphlet->keymapper.add("grey40");
    force_colour[ 6] = graphlet->keymapper.add("grey30");
    force_colour[ 7] = graphlet->keymapper.add("grey20");
    force_colour[ 8] = graphlet->keymapper.add("grey10");
    force_colour[ 9] = GT_Keys::black;
    force_colour[10] = GT_Keys::red;

    forall_nodes (n, *this->attached()) {

	double dx = this->the_v_constraint[n]->displacement();
	double dy = this->the_h_constraint[n]->displacement();

	double length = hypot(dx ,dy);
	if (length > max_force) max_force = length;
	force[n] = length;
	sum_forces += length;
    }

    forall_nodes (n, *this->attached()) {
	GT_Node_Attributes& node_attrs = this->gt_graph()->gt(n);
		
	int colour = (int) (10.0 * force[n] / max_force); 
	node_attrs.graphics()->fill(force_colour[colour]);

	if(colour <= 4) {
	    node_attrs.label_graphics()->fill(GT_Keys::black);
	} else {
	    node_attrs.label_graphics()->fill(GT_Keys::white);
	}
		
	// Force a redraw so that the colors will be updated
	this->gt_graph()->gt(n).set_changed
	    (GT_Common_Attributes::tag_label);
    }

    //
    // You might want to know this if you do analyses.
    //

#if DEBUG    
#if 0
    cout << " maximum force (red node) " << max_force <<
	", average force " << (sum_forces /
	    this->attached()->number_of_nodes()) << "." << endl;
#endif
#endif
}


//
// update_gt_properties:
// Write back coordinates to the gt_graph.
//

void FR_Constraint_Graph::update_gt_properties()
{
    node n;
    double coord;
	
    forall_nodes (n, *this->attached()) {
	GT_Node_Attributes& node_attrs = this->gt_graph()->gt(n);
		
	coord = this->the_v_constraint[n]->coord();
	node_attrs.graphics()->x(coord);
		
	coord = this->the_h_constraint[n]->coord();
	node_attrs.graphics()->y(coord);
    }
}


//
// fit_graph_to_window:
// Optional post-processing of the layout.
//

void FR_Constraint_Graph::fit_graph_to_window(
    const int margin, const bool do_scale)
{
    double min_x = HUGE, min_y = HUGE;
    double max_x = -HUGE, max_y = -HUGE;
    double scale_x, scale_y, scale;
    double center_x, center_y;

    list<node>::const_iterator it, end;
    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	if (this->the_v_constraint[*it]->coord() < min_x) {
	    min_x = this->the_v_constraint[*it]->coord();
	}
	if (this->the_v_constraint[*it]->coord() > max_x) {
	    max_x = this->the_v_constraint[*it]->coord();
	}
    }

    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	if (this->the_h_constraint[*it]->coord() < min_y) {
	    min_y = this->the_h_constraint[*it]->coord();
	}
	if (this->the_h_constraint[*it]->coord() > max_y) {
	    max_y = this->the_h_constraint[*it]->coord();
	}
    }

#if DEBUG
    cout << "width " << max_x - min_x
	 << " height " << max_y - min_y << endl;
#endif
    
    if(do_scale) {
	scale_x = (max_x - min_x + margin) / this->window_width();
	scale_y = (max_y - min_y + margin) / this->window_height(); 

	if(scale_x > scale_y) {
	    scale = scale_x;
	} else {
	    scale = scale_y;
	}

	center_x = this->xoffset() + margin;
	center_y = this->yoffset() + margin;
    } else {
	scale = 1;
	center_x = this->xoffset() -
	    ((max_x - min_x - this->window_width()) / 2);
	if(center_x < margin) {
	    center_x = margin;
	}
		
	center_y = this->yoffset() -
	    ((max_y - min_y - this->window_height()) / 2);
	if(center_y < margin) {
	    center_y = margin;
	}
    }
	
    for(it = the_v_representative.begin(), end = the_v_representative.end();
	it != end; ++it)
    {
	double coord = (this->the_v_constraint[*it]->coord() - min_x)
	    / scale + center_x;
	this->the_v_constraint[*it]->coord(coord);
    }
		 
    for(it = the_h_representative.begin(), end = the_h_representative.end();
	it != end; ++it)
    {
	double coord = (this->the_h_constraint[*it]->coord() - min_y)
	    / scale + center_y;
	this->the_h_constraint[*it]->coord(coord);
    }
}


void FR_Constraint_Graph::redraw()
{
    if ((this->tcl_graph()) && (this->tcl_interp()))
    {
	this->fit_graph_to_window(20, false);
	this->update_gt_properties();
	this->tcl_graph()->draw();
	Tcl_Eval(this->tcl_interp(), "update");

	//
	// For Tcl's sake the variable must be in writeable memory.
	// So no Tcl_GetVar( ,"...", ) is possible.
	//
	
	char lookup[80];
	strcpy (lookup, "GT_options(constraint_fr_animation)");

	this->animation(atoi(Tcl_GetVar(this->tcl_interp(),
	    lookup, 0)));
    }
}


//
// join_hv_groups:
// This method isn't called from the base algorithm, but from
// the iterative constraint springembedder (ICSE).
//
// CAVEAT: After doing one ore more joins the repulsive_forces
// and the opposite_pairs have to be recalculated.
// This is not done here for efficiency reasons (cause we
// do more than one join at a time).
//

bool FR_Constraint_Graph::join_hv_groups(
    node_map<FR_One_Dimensional_Constraint*>& constraint,
    list<node>& representative,
    FR_One_Dimensional_Constraint* first,
    FR_One_Dimensional_Constraint* second)
{
    if((first == second) ||
       (first->upper_constraints().find(second) !=
	first->upper_constraints().end()) ||
       (first->lower_constraints().find(second) !=
	first->lower_constraints().end()))
    {
	return false;
    }

    const double minimal_distance = this->minimal_distance();
    double max_allowed = max(first->coord(), second->coord());
    double min_allowed = min(first->coord(), second->coord());

    set<FR_One_Dimensional_Constraint *>::const_iterator it, end;

    for(it = first->upper_constraints().begin(),
        end = first->upper_constraints().end();
	it != end; ++it)
    {
	if (((*it)->coord() - minimal_distance) < max_allowed) {
	    max_allowed = (*it)->coord() - minimal_distance;
	}
    }

    for(it = second->upper_constraints().begin(),
        end = second->upper_constraints().end();
	it != end; ++it)
    {
	if (((*it)->coord() - minimal_distance) < max_allowed) {
	    max_allowed = (*it)->coord() - minimal_distance;
	}
    }

    for(it = first->lower_constraints().begin(),
        end = first->lower_constraints().end();
	it != end; ++it)
    {
	if (((*it)->coord() + minimal_distance) > min_allowed) {
	    min_allowed = (*it)->coord();
	}
    }

    for(it = second->lower_constraints().begin(),
        end = second->lower_constraints().end();
	it != end; ++it)
    {
	if (((*it)->coord() + minimal_distance) > min_allowed) {
	    min_allowed = (*it)->coord();
	}
    }

    if (max_allowed - min_allowed < 0) {
	return false;
    }

    first->coord(min_allowed + (max_allowed - min_allowed) / 2);
        
    first->group_count(first->group_count() +
	second->group_count());

    first->max_size(max(first->max_size(), second->max_size()));
    
    while (second->upper_constraints().size()) {
	FR_One_Dimensional_Constraint* upper_constraint =
	    *second->upper_constraints().begin();
	second->del_upper_constraint(upper_constraint);
	first->add_upper_constraint(upper_constraint);
    }
	
    while (second->lower_constraints().size()) {
	FR_One_Dimensional_Constraint* lower_constraint =
	    *second->lower_constraints().begin();
	second->del_lower_constraint(lower_constraint);
	first->add_lower_constraint(lower_constraint);
    }

    this->del_representative(constraint, representative, second);
    
    node n;
    forall_nodes (n, *this->attached()) {
	if (constraint[n] == second) {
	    constraint[n] = first;
	}
    }
    
    return true;
}


//
// new_node: (Only used by ICSE)
// This is used to generate dummy-nodes to simulate bends
//

node FR_Constraint_Graph::new_node(
    const double x, const double y,
    const double scale_repulsive_force)
{
    node n_new = this->attached()->new_node();

    this->gt_graph()->gt(n_new).graphics()->w(5);
    this->gt_graph()->gt(n_new).graphics()->h(5);
    this->the_node_width[n_new] = 1;
    this->the_node_height[n_new] = 1;
    
    this->the_h_constraint[n_new] = new FR_One_Dimensional_Constraint(y);
    this->the_v_constraint[n_new] = new FR_One_Dimensional_Constraint(x);
    this->the_h_representative.push_back(n_new);
    this->the_v_representative.push_back(n_new);

    this->the_repulsive_factor[n_new].init(*this->attached());
    this->the_optimal_matrix[n_new].init(*this->attached());
    
    node n;
    forall_nodes(n, *this->attached()) {
	this->the_repulsive_factor[n][n_new] =
	    this->the_repulsive_factor[n_new][n] = 1;
	
	this->the_optimal_matrix[n][n_new] =
	    this->the_optimal_matrix[n_new][n] =
	    (MATRIX_TYPE) ( 
	    (this->optimal_distance() +
		min(this->the_node_height[n], this->the_node_width[n]) + 4) *
	    scale_repulsive_force);
    }
    return n_new;
}


//
// del_representative: (Only used by ICSE)
// Needed when groups of constraints are joint.
//

void FR_Constraint_Graph::del_representative(
    node_map<FR_One_Dimensional_Constraint*>& constraint,
    list<node>& representative,
    FR_One_Dimensional_Constraint* to_delete)
{
    list<node>::iterator it, end;

    for(it = representative.begin(), end = representative.end();
	it != end; ++it)
    {
	if (constraint[*it] == to_delete) {
	    representative.erase(it);
	    break;
	}
    }
}


//
// del_node: (Only used by ICSE)
// Used when removing dummy nodes.
//

void FR_Constraint_Graph::del_node(node n)
{
    int count = this->the_h_constraint[n]->group_count();
    if (count > 1) {
	this->the_h_constraint[n]->group_count(count-1);
    } else {
	this->del_representative(
	    this->the_h_constraint,
	    this->the_h_representative,
	    this->the_h_constraint[n]);
	delete this->the_h_constraint[n];
    }

    count = this->the_v_constraint[n]->group_count();
    if (count > 1) {
	this->the_v_constraint[n]->group_count(count-1);
    } else {
	this->del_representative(
	    this->the_v_constraint,
	    this->the_v_representative,
	    this->the_v_constraint[n]);
	delete this->the_v_constraint[n];
    }
    this->attached()->del_node(n);
}


//
// new_edge: (Only used by ICSE)
//

edge FR_Constraint_Graph::new_edge(node source, node target)
{

    edge e_new = attached()->new_edge(source, target);

    the_edges.push_back(e_new);
    
    return e_new;
}


//
// del_edge: (Only used by ICSE)
//

void FR_Constraint_Graph::del_edge(edge e)
{
    this->attached()->del_edge(e);

    list<edge>::iterator it, end;

    for(it = the_edges.begin(), end = the_edges.end();
	it != end; ++it)
    {
	if(*it == e) {
	    the_edges.erase(it);
	    break;
	}
    }
}





