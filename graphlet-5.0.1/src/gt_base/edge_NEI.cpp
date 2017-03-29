/* This software is distributed under the Lesser General Public License */
// ---------------------------------------------------------------------
// NEI.cpp
// 
// Memberfunctions for the Node-Edge-Interface
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/edge_NEI.cpp,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/06/24 11:13:07 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project, Walter Bachl
//

#include <gt_base/Graphlet.h>
#include <gt_base/Graph.h>
#include <gt_base/Point.h>
#include <gt_base/Common_Graphics.h>

#include <vector>
#include <iostream>

#include "Keys.h"
#include "NEI.h"


// ------------------------------------------------------------------------
// Memberfunctions GT_Edge_NEI
// ------------------------------------------------------------------------


void GT_Edge_NEI::edge_attrs (GT_Edge_Attributes* attrs)
{
    the_edge_attrs = attrs;
}


void GT_Edge_NEI::source_function (GT_Key function)
{
    the_source_function = function;
    set_tagged_attribute (this, tag_source_function,
	&GT_Edge_NEI::the_source_function);
}


void GT_Edge_NEI::target_function (GT_Key function)
{
    the_target_function = function;
    set_tagged_attribute (this, tag_target_function,
	&GT_Edge_NEI::the_target_function);
}


void GT_Edge_NEI::delta_x_source (double x)
{
    the_delta_x_source = x;
    set_tagged_attribute (this, tag_delta_x_source,
	&GT_Edge_NEI::the_delta_x_source);
}


void GT_Edge_NEI::delta_y_source (double y)
{
    the_delta_y_source = y;
    set_tagged_attribute (this, tag_delta_y_source,
	&GT_Edge_NEI::the_delta_y_source);
}


void GT_Edge_NEI::delta_x_target (double x)
{
    the_delta_x_target = x;
    set_tagged_attribute (this, tag_delta_x_target,
	&GT_Edge_NEI::the_delta_x_target);
}


void GT_Edge_NEI::delta_y_target (double y)
{
    the_delta_y_target = y;
    set_tagged_attribute (this, tag_delta_y_target,
	&GT_Edge_NEI::the_delta_y_target);
}



// ------------------------------------------------------------------------
// Functions to set the offset of the anchor. With rangecheck
// ------------------------------------------------------------------------

int GT_Edge_NEI::d_x_source (double d_x)
{
    int error = GT_OK;
    // security check
    if (d_x > 1.0) {
	d_x = 1.0;
	error = GT_ERROR;
    }
    if (d_x < -1.0) {
	d_x = -1.0;
	error = GT_ERROR;
    }
    delta_x_source (d_x);
    return error;
}


int GT_Edge_NEI::d_y_source (double d_y)
{
    int error = GT_OK;
    // security check
    if (d_y > 1.0) {
	d_y = 1.0;
	error = GT_ERROR;
    }
    if (d_y < -1.0) {
	d_y = -1.0;
	error = GT_ERROR;
    }
    delta_y_source (d_y);
    return error;
}


int GT_Edge_NEI::d_x_target (double d_x)
{
    int error = GT_OK;
    // security check
    if (d_x > 1.0) {
	d_x = 1.0;
	error = GT_ERROR;
    }
    if (d_x < -1.0) {
	d_x = -1.0;
	error = GT_ERROR;
    }
    delta_x_target (d_x);
    return error;
}


int GT_Edge_NEI::d_y_target (double d_y)
{
    int error = GT_OK;
    // security check
    if (d_y > 1.0) {
	d_y = 1.0;
	error = GT_ERROR;
    }
    if (d_y < -1.0) {
	d_y = -1.0;
	error = GT_ERROR;
    }

    delta_y_target (d_y);
    return error;
}

//
// Copy & Clone
//

void GT_Edge_NEI::copy (const GT_Edge_NEI* from,
    GT_Copy type)
{
    baseclass::copy (from, type);

    if (from->is_initialized (GT_Edge_NEI::tag_edge_nei)) {
	source_function (from->the_source_function);
	target_function (from->the_target_function);

	delta_x_source (from->the_delta_x_source);
	delta_y_source (from->the_delta_y_source);
	delta_x_target (from->the_delta_x_target);
	delta_y_target (from->the_delta_y_target);
    }
}


GT_List_of_Attributes* GT_Edge_NEI::clone (GT_Copy type) const
{
    GT_Edge_NEI* new_edge_nei = new GT_Edge_NEI ();
    new_edge_nei->copy (this, type);
    return new_edge_nei;
}


void GT_Edge_NEI::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Edge_NEI*)parent(), copy_type);
}


// ------------------------------------------------------------------------
// Support functions
// ------------------------------------------------------------------------

// ------------------------------------------------------------------------
// Get the point where an edge intersects a list of line-segments
// PRECONDITION: line.source() is the point outside the node
// ------------------------------------------------------------------------

GT_Point GT_Edge_NEI::intersection (GT_Polyline &node, GT_Segment &line)
{
    list<GT_Point> intersections;
    GT_Point p;
    
    GT_Polyline::const_iterator it_poly = node.begin();
    GT_Polyline::const_iterator end = --(node.end());

    while(it_poly != end)
    {
	const GT_Point &p1 = *it_poly;
	const GT_Point &p2 = *(++it_poly);
	GT_Segment bord (p1, p2);
	
	// Is there an intersection?
	if ( line.intersection_of_lines(bord, p) ) {
		if ((min(bord.xcoord1(), bord.xcoord2()) -0.1<= p.x()) &&
		(max(bord.xcoord1(), bord.xcoord2()) +0.1>= p.x()) &&
		(min(bord.ycoord1(), bord.ycoord2()) -0.1<= p.y()) &&
		(max(bord.ycoord1(), bord.ycoord2()) +0.1>= p.y())) {
		intersections.push_front (p);
	    }
	}
    }

    // None found
    if (intersections.empty()) {
	return line.target();
    }

    // Get the first
    
    // Init the minimum
    GT_Point min = intersections.front();
    GT_Segment s = GT_Segment(line.source(), intersections.front());
    double length = s.length();


    for(list<GT_Point>::const_iterator it_point = intersections.begin();
	it_point != intersections.end(); ++it_point)
    {
	s= GT_Segment (line.source(), *it_point);
	double akt = s.length();
	if (length > akt) {
	    length = akt;
	    min = *it_point;
	}
    }
    
    return min;
}

// ------------------------------------------------------------------------
// Taken from the original NEI.
// Function gets two lists of points and returns in min1 and min2 the
// two points with minimal distance.
// Used for all 'jumping' functions (set_EA_next...)
// ------------------------------------------------------------------------

double sqr (double x)
{
    return x*x;
}

void GT_Edge_NEI::compute_min (
    const vector<GT_Point>& points1, GT_Point& min1, int p1_high,
    const vector<GT_Point>& points2, GT_Point& min2, int p2_high)
{    
    double minimum = points1[0].distance (points2[0]);
	
    int i, j;
    int min_i, min_j;
	
    min_i = 0;
    min_j = 0;
	
    for (i = 0; i < p1_high; i++) {
	for (j = 0; j < p2_high; j++) {
	    double dist = points1[i].distance (points2[j]);
	    if (dist < minimum) {
		min_i = i;
		min_j = j;
		minimum = dist;
	    }
	}
    }

    min1 = points1[min_i];
    min2 = points2[min_j];
}

// ------------------------------------------------------------------------
// Reset all NEI's in the graph to the default-value
// ------------------------------------------------------------------------

int reset_NEIs (GT_Graph &g)
{
    GT_Node_NEI   *node_nei;
    GT_Edge_NEI   *edge_nei;
    edge          e;
    node          n;
    graph         *gtl_graph = g.attached();

    forall_nodes (n, *gtl_graph) {
        node_nei = g.gt(n).node_nei();
        node_nei->set_EA_default_function (GT_Keys::empty_function);
    }

    forall_edges (e, *gtl_graph) {
        edge_nei = g.gt(e).edge_nei();
        edge_nei->set_sEA_fast (0.0, 0.0);
        edge_nei->set_tEA_fast (0.0, 0.0);
        edge_nei->set_EA_default_function (GT_Keys::empty_function, GT_Source);
        edge_nei->set_EA_default_function (GT_Keys::empty_function, GT_Target);
    }
    return GT_OK;
}

// ------------------------------------------------------------------------
// Konstruktor and Destructor
// ------------------------------------------------------------------------

GT_Edge_NEI::GT_Edge_NEI ()
{
    the_edge_attrs = 0;
    
    the_source_function = GT_Keys::empty_function;
    the_target_function = GT_Keys::empty_function;
    the_delta_x_source = 0.0;
    the_delta_y_source = 0.0;
    the_delta_x_target = 0.0;
    the_delta_y_target = 0.0;
}


GT_Edge_NEI::~GT_Edge_NEI ()
{
}

// ------------------------------------------------------------------------
// Functions to set the edgeanchor by using one of the default-functions
// If function is an unknown key then nothing happens.
// ------------------------------------------------------------------------

int GT_Edge_NEI::EA_apply_function (GT_Key function, int where)
{
    if (function == GT_Keys::EA_next_corner) {
	set_EA_next_corner (where);
    }
    else if (function == GT_Keys::EA_next_middle) {
	set_EA_next_middle (where);
    }
    else if (function == GT_Keys::EA_orthogonal) {
	set_EA_orthogonal (where);
    }
    else if (function == GT_Keys::empty_function) {
	// Do nothing
    }
    return GT_OK;
}


int GT_Edge_NEI::EA_apply_function (string function, int where)
{
    GT_Key function_key = graphlet->keymapper.add (function);

    return EA_apply_function (function_key, where);    
}

// ------------------------------------------------------------------------
// Only a limited set of functions is allowed. The default
// function is applied at every redraw or create_edge function.
// ------------------------------------------------------------------------

int GT_Edge_NEI::set_EA_default_function (GT_Key function, int where)
{
    int error = GT_OK;
    
    //if (check_function (function) == GT_ERROR) {

   if ((function != GT_Keys::EA_next_corner) &&
	(function != GT_Keys::EA_next_middle) &&
	(function != GT_Keys::EA_orthogonal) &&
	(function != GT_Keys::empty_function)) {
       
       error = GT_ERROR;
       function = GT_Keys::empty_function;
    }
    
    if (where == GT_Source) {
	source_function (function);
    } else {
	target_function (function);
    }		
    return error;
}


int GT_Edge_NEI::set_EA_default_function (string function, int where)
{
    GT_Key function_key = graphlet->keymapper.add (function);

    return set_EA_default_function (function_key, where);
}

// -------------------------------------------------------------------------
// For output of the parameters of the GT_Edge_NEI
// -------------------------------------------------------------------------

bool GT_Edge_NEI::do_print () const
{
    if (print_test (tag_source_function, GT_Keys::source_function,
	source_function(), GT_Keys::empty_function)) {
	return true;
    }

    if (print_test (tag_target_function, GT_Keys::target_function,
	target_function(), GT_Keys::empty_function)) {
	return true;
    }

    if (source_function() == GT_Keys::empty_function &&
	(the_edge_attrs->source() == node::node() ||
	    source_attributes().node_nei()->default_function() ==
	    GT_Keys::empty_function))
    {
	if (print_test (tag_delta_x_source, GT_Keys::delta_x_source,
	    delta_x_source())) {
	    return true;
	}
	if (print_test (tag_delta_y_source, GT_Keys::delta_y_source,
	    delta_y_source())) {
	    return true;
	}
    }

    if (target_function() == GT_Keys::empty_function &&
	(the_edge_attrs->target() == node::node() ||
	    target_attributes().node_nei()->default_function() ==
	    GT_Keys::empty_function))
    {
	if (print_test (tag_delta_x_target, GT_Keys::delta_x_target,
	    delta_x_target())) {
	    return true;
	}
	if (print_test (tag_delta_y_target, GT_Keys::delta_y_target,
	    delta_y_target())) {
	    return true;
	}
    }

    return false;
}


void GT_Edge_NEI::print (ostream& out) const
{
    print_object (out, tag_source_function, GT_Keys::source_function,
	source_function(), GT_Keys::empty_function);
    print_object (out, tag_target_function, GT_Keys::target_function,
	target_function(), GT_Keys::empty_function);

    if (source_function() == GT_Keys::empty_function &&
	(the_edge_attrs->source() == node::node() ||
	    source_attributes().node_nei()->default_function() ==
	    GT_Keys::empty_function))
    {
	print_object (out, tag_delta_x_source, GT_Keys::delta_x_source,
	    delta_x_source());
	print_object (out, tag_delta_y_source, GT_Keys::delta_y_source,
	    delta_y_source());
    }

    if (target_function() == GT_Keys::empty_function &&
	(the_edge_attrs->target() == node::node() ||
	    target_attributes().node_nei()->default_function() ==
	    GT_Keys::empty_function))
    {
	print_object (out, tag_delta_x_target, GT_Keys::delta_x_target,
	    delta_x_target());
	print_object (out, tag_delta_y_target, GT_Keys::delta_y_target,
	    delta_y_target());
    }
}

// -------------------------------------------------------------------------
// For input of the parameters of the GT_Edge_NEI
// -------------------------------------------------------------------------

int GT_Edge_NEI::extract (GT_List_of_Attributes* current_list,
    string& /* message */)
{
    GT_Key xtr_fun;
    if (current_list->extract (GT_Keys::source_function, xtr_fun)) {
	source_function (xtr_fun);
    }
    
    if (current_list->extract (GT_Keys::target_function, xtr_fun)) {
	target_function (xtr_fun);
    }

    double xtr_delta;
    if (current_list->extract (GT_Keys::delta_x_source, xtr_delta)) {
	d_x_source (xtr_delta);
    }

    if (current_list->extract (GT_Keys::delta_y_source, xtr_delta)) {
	d_y_source (xtr_delta);
    }

    if (current_list->extract (GT_Keys::delta_x_target, xtr_delta)) {
	d_x_target (xtr_delta);
    }

    if (current_list->extract (GT_Keys::delta_y_target, xtr_delta)) {
	d_y_target (xtr_delta);
    }

    return GT_OK;
}

// --------------------------------------------------------------------
// some routines to get information
// --------------------------------------------------------------------


GT_Point GT_Edge_NEI::get_clip_point (int where)
{
    return clip_edge (where);
}

GT_Point GT_Edge_NEI::get_EA (int where) const
{
    if (where == GT_Source) {
	return GT_Point(delta_x_source(), delta_y_source());
    } 
    return GT_Point(delta_x_target(), delta_y_target());
}


double GT_Edge_NEI::get_EA_x (int where) const
{
    if (where == GT_Source) {
	return delta_x_source();
    }
    return delta_x_target();
}


double GT_Edge_NEI::get_EA_y (int where) const
{
    if (where == GT_Source) {
	return delta_y_source();
    }
    return delta_y_target();
}


GT_Key GT_Edge_NEI::get_EA_default_function (int where) const
{
    if (where == GT_Source) return source_function();
    return target_function();
}

// -------------------------------------------------------------------------
// Routines to set the edgeanchor
// -------------------------------------------------------------------------

int GT_Edge_NEI::set_EA (int where, double delta_x, double delta_y)
{
    // return GT_ERROR in case delta_x or delta_y is out of range
    if ( where == GT_Source) {
	int e1 = d_x_source (delta_x);
	int e2 = d_y_source (delta_y);
	if ((e1 == GT_ERROR) || (e2 == GT_ERROR)) return GT_ERROR;
    } else {
	int e1 = d_x_target (delta_x);
	int e2 = d_y_target (delta_y);
	if ((e1 == GT_ERROR) || (e2 == GT_ERROR)) return GT_ERROR;
    }
    return GT_OK;
}


int GT_Edge_NEI::set_EA (int where, GT_Key direction)
{
    // Return GT_ERROR if direction is an unknown key
    
    // Use delta_x as an flag whether the key is known
    double delta_x = 5.0, delta_y = 0.0;

    if (direction == GT_Keys::anchor_n) {
	delta_x = 0.0;
	delta_y = 1.0;
    }
    else if (direction == GT_Keys::anchor_s ) {
	delta_x = 0.0;
	delta_y = -1.0;
    }
    else if (direction == GT_Keys::anchor_w ) {
	delta_x = -1.0;
	delta_y = 0.0;
    }
    else if (direction == GT_Keys::anchor_e ) {
	delta_x = 1.0;
	delta_y = 0.0;
    }
    else if (direction == GT_Keys::anchor_ne ) {
	delta_x = 1.0;
	delta_y = 1.0;
    }
    else if (direction == GT_Keys::anchor_se ) {
	delta_x = 1.0;
	delta_y = -1.0;
    }
    else if (direction == GT_Keys::anchor_sw ) {
	delta_x = -1.0;
	delta_y = -1.0;
    }
    else if (direction == GT_Keys::anchor_nw ) {
	delta_x = -1.0;
	delta_y = 1.0;
    }
    else if (direction == GT_Keys::anchor_center ) {
	delta_x = 0;
	delta_y = 0;
    }

    if ((int)delta_x == 5) {
	return GT_ERROR;
    }

    if (where == GT_Source) {
	set_sEA_fast (delta_x, delta_y);
    } else {
	set_tEA_fast (delta_x, delta_y);
    }
	
    return GT_OK;
}

// ------------------------------------------------------------------------
// Prepare call to NEI
// Make sure the edge has at least two points
// ------------------------------------------------------------------------

// void GT_Edge_NEI::prepare_call ()
// {
//     GT_Polyline line = the_edge_attrs->graphics()->line();
//     const GT_Node_Attributes& source_attrs = source_attributes();
//     const GT_Node_Attributes& target_attrs = target_attributes();
    
//     if (line.length() == 0) {
// 	line.push_back (source_attrs.graphics()->center());
// 	line.push_back (target_attrs.graphics()->center());
//     }
//     the_edge_attrs->graphics()->line(line);
// }


// ------------------------------------------------------------------------
// Precondition: The edge has a corresponding edgeline (this are at least
//               two bends).
// Set the edge anchor in such a way that the line-segment betwen the last
// bend and the end-point of the edge has minimal length.
//
// Remark: in case that the line has no bends:
//         We don't check all possible combinations between source and target.
//         If you need this, then you have to take the function
//         set_EA_connect_corner_shortest 
// ------------------------------------------------------------------------

int GT_Edge_NEI::set_EA_next_corner_orthogonal (int where)
{
    // Special case: connect orthogonal for one end of the edge,
    //               next corner for the other    
    vector<GT_Point> permute_corners (4);
    vector<GT_Point> single_point(1);
    GT_Point min_corner, static_point;

    const GT_Rectangle& source_center =
	source_attributes().graphics()->center();
    const GT_Rectangle& target_center =
	target_attributes().graphics()->center();

    // If the edge has no bend, don't use the clip point, use the nodecenter
    GT_Polyline line = the_edge_attrs->graphics()->line();
    
    if (where == GT_Source) {
	get_corners (source_attributes(), permute_corners);
	
	if (line.size() == 2) {
	    single_point[0] = GT_Point (target_center.x(), target_center.y());
	} else {
	    single_point[0] = *(++line.begin());
	}
    } else {
	get_corners (target_attributes(), permute_corners);

	if (line.size() == 2) {
	    single_point[0] = GT_Point (source_center.x(), source_center.y());
	} else {
	    single_point[0] = *(-- --line.end());
	}
    }
    
    // Get the minimum
    compute_min (permute_corners, min_corner, 4, single_point, static_point, 1);

    // set the minimum
    if (where == GT_Source) {
	GT_Point anchor = coordinates_to_anchor (GT_Source, min_corner);
	set_sEA_fast (anchor);	
    } else {
	GT_Point anchor = coordinates_to_anchor (GT_Target, min_corner);
	set_tEA_fast (anchor);	
    }	
    return (GT_OK);
}


int GT_Edge_NEI::set_EA_next_corner (int where)
{
    vector<GT_Point>        permute_corners (4);
    vector<GT_Point>        static_point_array (1);
    GT_Point               min_corner, static_point;

    static_point_array[0] = get_bend_outside(where);
	
    if (where == GT_Source) {
	get_corners (source_attributes(), permute_corners);
    } else {
	get_corners (target_attributes(), permute_corners);
    }

    // Get the minimum
    compute_min ( permute_corners, min_corner, 4,
	static_point_array, static_point, 1);

    // set the minimum
    if (where == GT_Source) { 
	GT_Point anchor = coordinates_to_anchor (GT_Source, min_corner);
	set_sEA_fast (anchor);	
    } else {
	GT_Point anchor = coordinates_to_anchor (GT_Target, min_corner);
	set_tEA_fast (anchor);	
    }
    return (GT_OK);
}

// ------------------------------------------------------------------------
// Precondition: The edge has a corresponding edgeline (this are at least
//               two bends).
// Set the edge anchor in such a way that the line-segment betwen the last
// bend and the end-point of the edge has minimal length.
// ------------------------------------------------------------------------

int GT_Edge_NEI::set_EA_next_middle (int where)
{
    vector<GT_Point>        permute_corners (4);
    vector<GT_Point>        static_point_array (1);
    GT_Point               min_corner, static_point;

    static_point_array[0] = get_bend_outside(where);
	
    if (where == GT_Source) { 
	get_middles (source_attributes(), permute_corners);
    } else {
	get_middles (target_attributes(), permute_corners);
    }

    // Get the minimum
    compute_min ( permute_corners, min_corner, 4,
	static_point_array, static_point, 1);

    // set the minimum
    if (where == GT_Source) { 
	GT_Point anchor = coordinates_to_anchor (GT_Source, min_corner);
	set_sEA_fast (anchor);	
    } else {
	GT_Point anchor = coordinates_to_anchor (GT_Target, min_corner);
	set_tEA_fast (anchor);	
    }	
    return GT_OK;
}

// -------------------------------------------------------------------------
// Connect last bend orthogonal if possible.
// This is the easy case: Take last bend and try to find the perpendicular.
// -------------------------------------------------------------------------

int GT_Edge_NEI::set_EA_orthogonal (int where)
{
    const GT_Common_Graphics*   node_graphics;
    GT_Polyline           line = the_edge_attrs->graphics()->line();
    double                node_x, node_y, node_w, node_h;
    GT_Point                 last_bend = get_bend_outside (where);

    // make further work independent from where
    if (where == GT_Source) {
	node_graphics = source_attributes().graphics();
    } else {
	node_graphics = target_attributes().graphics();
    }
    // Get geometric information
    const GT_Rectangle&  node_center = node_graphics->center();

    // get node geometry
    node_x = node_center.x();
    node_y = node_center.y();
    node_w = node_center.w()/2;
    node_h = node_center.h()/2;

    //determine sector
    // above 
    if (node_y + node_h < last_bend.y()) {
	// left of
	if (node_x - node_w > last_bend.x()) {
	    set_EA (where, GT_Keys::anchor_nw);
	    return GT_OK;
	}
	// right of
	if (node_x + node_w < last_bend.x()) {
	    set_EA (where, GT_Keys::anchor_ne);
	    return GT_OK;
	}
	// orthogonal from top
	if (where == GT_Source) {
	    set_sEA_fast ((last_bend.x() -node_x) / node_w, 0.0);
	} else {
	    set_tEA_fast ((last_bend.x() -node_x) / node_w, 0.0);
	}
	
	return GT_OK;
    }

    // below
    if (node_y - node_h > last_bend.y()) {
	// left of
	if (node_x - node_w > last_bend.x()) {
	    set_EA (where, GT_Keys::anchor_sw);
	    return GT_OK;
	}
	// right of
	if (node_x + node_w < last_bend.x()) {
	    set_EA (where, GT_Keys::anchor_se);
	    return GT_OK;
	}
	// orthogonal from bottom
	if (where == GT_Source) {
	    set_sEA_fast ((last_bend.x()-node_x) / node_w, 0.0);
	} else {
	    set_tEA_fast ((last_bend.x()-node_x) / node_w, 0.0);
	}
	return GT_OK;
    }

    // orthogonal to the left
    if (node_x - node_w > last_bend.x()) {
	if (where == GT_Source) {
	    set_sEA_fast (-1.0, (last_bend.y()-node_y) / node_h);
	} else {
	    set_tEA_fast (-1.0, (last_bend.y()-node_y) / node_h);
	}
	return GT_OK;
    }

    // orthogonal to the right
    if (where == GT_Source) {
	set_sEA_fast (0.0, (last_bend.y()-node_y) / node_h);
    } else {
	set_tEA_fast (0.0, (last_bend.y()-node_y) / node_h);
    }
    return GT_OK;
}

// -------------------------------------------------------------------------
// Clip edge at border of node.
// Special care has to be taken if the anchor is on the nodeborder
// -------------------------------------------------------------------------

GT_Point GT_Edge_NEI::clip_edge_at_rectangle (int where)
{
    // Node geometry
    GT_Rectangle center;
    double width;
    
    if (where == GT_Source) {
	center = source_attributes().graphics()->center();
	width = source_attributes().graphics()->width() /2;
    } else{
	center = target_attributes().graphics()->center();
	width = target_attributes().graphics()->width() /2;
    }

    GT_Polyline border;

    border.push_back (center.anchor_sw() - GT_Point (-width, width) );
    border.push_back (center.anchor_se() - GT_Point (width, width) );
    border.push_back (center.anchor_ne() - GT_Point (width, -width) );
    border.push_back (center.anchor_nw() - GT_Point (-width, -width) );
    border.push_back (center.anchor_sw() - GT_Point (-width, width) );
   
    // edge geometry. 
    GT_Point outside = get_bend_outside (where);
    GT_Point inside = get_bend_inside (where);
    GT_Segment s (outside, inside);

    return intersection (border, s);
}

// -------------------------------------------------------------------------
// Clip edge at a arc
// -------------------------------------------------------------------------

GT_Point GT_Edge_NEI::clip_edge_at_arc (int where)
{
    const GT_Common_Graphics*   node_attrs;
    if (where == GT_Source) {
	node_attrs = source_attributes().graphics();
    } else {
	node_attrs = target_attributes().graphics();
    }
    const GT_Rectangle&  node_rect = node_attrs->center();
    const double         node_x = node_rect.x();
    const double         node_y = node_rect.y();
    const double         node_w  = node_rect.w();
    const double         node_h = node_rect.h();


    // Get Clip-Point with surrounding oval
    GT_Point clip = clip_edge_at_oval (where);


    // be careful with nodes with different width and height
    double d_x = (node_x - clip.x())*node_h;
    double d_y = (node_y - clip.y())*node_w;

    // Check whether the Clip-point is on the oval
    double angle = 360.0 - (3.1415927+atan2(d_y, d_x))*180/3.1415927;

    double start = node_attrs->start();
    double extent = node_attrs->extent();


    // Make sure that extent is a value in the range from 0..360
    if ((int)extent == 0) {
	extent = 90.0;
    }

    extent = div((int)extent, 360).rem;

    if (extent < 0.0) {
	start = start + extent;
	extent = -extent;
    }

    // Make sure that start is a value in the range from 0..360
    start = div((int)start, 360).rem;
    if (start < 0) {
	start = start + 360.0;
    }

    start = start;
    extent = extent;
    double end = start + extent;

    if( ((start < angle) && (end > angle)) ||
	((start < angle+360) && (end > angle+360))) {
	
	return clip;
    }

    // Get type of the arc
    int type = 3;
    if (node_attrs->is_initialized(GT_Common_Graphics::tag_style)) {
	
	if (node_attrs->style() == GT_Keys::style_chord){
	    type = 1;
	    
	} else if (node_attrs->style() == GT_Keys::style_arc){
	    type = 2;
	    
	} else if (node_attrs->style() == GT_Keys::style_pieslice){
	    type = 3;
	}
    }
    
    if (type == 2) { // arc

	clip = clip_edge_at_oval (where, true);
	// be careful with nodes with different width and height
	d_x = (node_x - clip.x())*node_h;
	d_y = (node_y - clip.y())*node_w;
	
	// Check whether the Clip-point is on the oval
	angle = 360.0 - (3.1415927+atan2(d_y, d_x))*180/3.1415927;
	
	if( ((start < angle) && (end > angle)) ||
	    ((start < angle+360) && (end > angle+360))) {
	    
	    return clip;
	} 
	
	return convert_anchor_to_coordinates (where);
    }
  
    // Compute line(s)
    // 1. Coordinates of end and start of angle
    double start_b = 3.141592*start / 180;
    GT_Point p_start = GT_Point(node_x + node_w/2*cos(start_b), 
	node_y - node_h/2*sin(start_b));
    
    double end_b = 3.141592*end / 180;
    GT_Point p_end = GT_Point(node_x + node_w/2*cos(end_b), 
	node_y - node_h/2*sin(end_b));
    // 2. Points to line
    GT_Polyline border;
    if (type == 3) { // pieslice
	
	border.push_back (p_start);
	border.push_back (GT_Point (node_x, node_y) );
	border.push_back (p_end);
	
    } else if (type == 1) { // chord 

	border.push_back (p_start);
	border.push_back (p_end);
   }

    GT_Point outside = get_bend_outside (where);
    GT_Point inside = get_bend_inside (where);
    GT_Segment s (outside, inside);
    
    return intersection (border, s);
}

// -------------------------------------------------------------------------
// Clippen einer Kante am ovalen Knoten
// -------------------------------------------------------------------------

GT_Point GT_Edge_NEI::clip_edge_at_oval (int where)
{
  return clip_edge_at_oval (where, false);
}

// -------------------------------------------------------------------------
// Clip edge at an oval node.
// Formula taken from
// Bartsch: Taschenbuch math. Formeln
// 80/SH 500 B294 (9)
//
// Funktion wird auch benutzt, um bei einem Knotentyp arc den Schnitt mit
// der gegenueberliegenden Seite des Ovals zu berechnen. Flag wrongway
// ist true, wenn das der Fall ist.  
// -------------------------------------------------------------------------

GT_Point GT_Edge_NEI::clip_edge_at_oval (int where, bool wrongway)
{
    const GT_Common_Graphics*   node_graphics;
    double    x, y; // Clip -point
    GT_Point  inside, outside;
    GT_Polyline line = the_edge_attrs->graphics()->line();

    if (wrongway == true) {
      inside = get_bend_outside (where);
      outside = get_bend_inside (where);
    } else {
      inside = get_bend_inside (where);
      outside = get_bend_outside (where);
    }

    // Edge parameters
    if (where == GT_Source) {
	node_graphics = source_attributes().graphics();
    } else {
	node_graphics = target_attributes().graphics();
    }
    
    double delta_x = outside.x() - inside.x();
    double delta_y = outside.y() - inside.y();
    
    // Get parameters of oval
    // Correction value for different outline-width
    const double delta = (node_graphics->width()) /2;
    const GT_Rectangle&  node_rect = node_graphics->center();
    const double         node_x = node_rect.x();
    const double         node_y = node_rect.y();
    const double         node_width  = node_rect.w()/2 + delta;
    const double         node_height = node_rect.h()/2 + delta;

    // Check out whether edge is vertical
    if (delta_x == 0) {
	x = inside.x()-node_x;
	y = sqr(x)/sqr(node_width);
	y = (1- y) * sqr(node_height);	
	if (delta_y < 0 ) {
	    y = -sqrt ( y );
	} else {
	    y = sqrt ( y );
	}
	return GT_Point(x+node_x,y+node_y);
    }

    // Get parameters of edge (y=gradient*x +edge_dy) translated to the origin
    double gradient = (delta_y / delta_x);
   
    double edge_dy =  (inside.y() - node_y) -
	gradient*(inside.x() - node_x);

    // Precompute often used term
    double param1 = sqr(node_height) + sqr(node_width)*sqr(gradient);

    // Test on intersection
    if ((param1 - sqr(edge_dy)) < 0.0) {
	return inside; // No intersection
    }
    
    // Computation of x
    x = -(sqr(node_width)*gradient*edge_dy) / param1;
    
    if (delta_x < 0) {
	x = x - (node_width*node_height) / param1 *
	    sqrt (param1 -sqr(edge_dy));
    } else {
	x = x + (node_width*node_height) / param1 *
	    sqrt (param1 -sqr(edge_dy));
    }	
    y = gradient * x + edge_dy;

    // Translate to the real node position
    return GT_Point(x+node_x, y+node_y);
}

// DUMMY

GT_Point GT_Edge_NEI::clip_edge_at_polygon (int where)
{
    // Node geometry
    GT_Rectangle center;
    GT_Polyline border;
    
    if (where == GT_Source) {
	center = source_attributes().graphics()->center();
	border = source_attributes().graphics()->line();
    } else{
	center = target_attributes().graphics()->center();
	border = target_attributes().graphics()->line();
    }


    if (border.empty()) {
	border.push_back (center.anchor_w());
	border.push_back (center.anchor_s());
	border.push_back (center.anchor_e());
	border.push_back (center.anchor_n());
	border.push_back (center.anchor_w());
    }

    // Make sure last point has same coordinates as first
    border.push_back (border.front());
    
    // edge geometry. 
    GT_Point outside = get_bend_outside (where);
    GT_Point inside = get_bend_inside (where);
    GT_Segment s (outside, inside);

    return intersection (border, s);
}


GT_Point GT_Edge_NEI::clip_edge_at_line (int where)
{
    // Node geometry
    GT_Rectangle center;
    GT_Polyline border;
    
    if (where == GT_Source) {
	center = source_attributes().graphics()->center();
	border = source_attributes().graphics()->line();
    } else{
	center = target_attributes().graphics()->center();
	border = target_attributes().graphics()->line();
    }
    
    if (border.empty()) {
	border.push_back (center.anchor_nw());
	border.push_back (center.anchor_se());
    }
    
    // edge geometry. 
    GT_Point outside = get_bend_outside (where);
    GT_Point inside = get_bend_inside (where);
    GT_Segment s (outside, inside);

    return intersection (border, s);
}


GT_Point GT_Edge_NEI::clip_edge (int where)
{
    const GT_Node_Attributes& source_attrs = source_attributes ();
    const GT_Node_Attributes& target_attrs = target_attributes ();
    
    // Test different node types either for source or target
    if (where == GT_Source) {
	if (source_attrs.graphics()->type() == GT_Keys::type_polygon) {
	    return clip_edge_at_polygon (where);
	}
	if (source_attrs.graphics()->type() == GT_Keys::type_line) {
	    return clip_edge_at_line (where);
	}
	if (source_attrs.graphics()->type() == GT_Keys::type_oval) {
	    return clip_edge_at_oval (where);
	}
	if (source_attrs.graphics()->type() == GT_Keys::type_arc) {
	    return clip_edge_at_arc (where);
	}
		
    } else {

	if (target_attrs.graphics()->type() == GT_Keys::type_polygon) {
	    return clip_edge_at_polygon (where);
	}
	if (target_attrs.graphics()->type() == GT_Keys::type_line) {
	    return clip_edge_at_line (where);
	}
	if (target_attrs.graphics()->type() == GT_Keys::type_oval) {
	    return clip_edge_at_oval (where);
	}
	if (target_attrs.graphics()->type() == GT_Keys::type_arc) {
	    return clip_edge_at_arc (where);
	}
    }

    // default: clip at rectangle
    return clip_edge_at_rectangle (where);	
}

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------

int GT_Edge_NEI::get_side(int where)
{
    const GT_Common_Graphics*   node_graphics;
    GT_Point                 cut_point;
    double                anchor_x, anchor_y;
    
    GT_Polyline           line = the_edge_attrs->graphics()->line();
    GT_Point                 inside = get_bend_inside (where);
    GT_Point                 last_bend = get_bend_outside (where);

    if (where == GT_Source) {
	anchor_x = delta_x_source();
	anchor_y = delta_y_source();
	node_graphics = source_attributes().graphics();
    } else {
	node_graphics = target_attributes().graphics();
	anchor_x = delta_x_target();
	anchor_y = delta_y_target();
    }

    // Get geometric information
    const GT_Rectangle&  node_rect = node_graphics->center();
    const double         node_x = node_rect.x();
    const double         node_y = node_rect.y();
    const double         node_width  = node_rect.w();
    const double         node_height = node_rect.h();
		
    // Get the nodetype
    const GT_Key         node_type = node_graphics->type();

    // Default type is a rectangle
    const GT_Point upper_left (node_x - node_width/2, node_y + node_height/2);
    const GT_Point lower_left (node_x - node_width/2, node_y - node_height/2);
    const GT_Point upper_right (node_x+ node_width/2, node_y + node_height/2);
    const GT_Point lower_right (node_x+ node_width/2, node_y - node_height/2);
	
    GT_Segment s (last_bend, inside);

    if (anchor_y != 1) { 
	if ( s.intersection(GT_Segment(upper_left,upper_right), cut_point) ) {
	    cout << "Top" << endl;
	    return (GT_Top);  // Upper side
	}
    }
	
    if (anchor_y != -1) { 
	if ( s.intersection(GT_Segment(lower_left,lower_right), cut_point) ) {
	    cout << "Bottom" << endl;
	    return (GT_Bottom);  // lower side
	}
    }
	
    if (anchor_x != -1) { 
	if ( s.intersection(GT_Segment(lower_left,upper_left), cut_point) ) {
	    cout << "Left" << endl;
	    return (GT_Left);  // left side
	}
    }
	
    if (anchor_x != 1) { 
	if ( s.intersection(GT_Segment(lower_right,upper_right), cut_point) ) {
	    cout << "Right" << endl;
	    return (GT_Right);  // right side
	}
    }

    if (anchor_x == 1) return GT_Right;
    if (anchor_x == -1) return GT_Left;
    if (anchor_y == 1) return GT_Top;
    if (anchor_y == -1) return GT_Bottom;
	
    // 
    //cout << "Error in get_side!" << endl;
    return 0;
}

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------

int GT_Edge_NEI::set_EA_connect_orthogonal ()
{
    GT_Polyline           line = the_edge_attrs->graphics()->line();

    if (line.size() > 2) {
	set_EA_orthogonal (GT_Source);
	set_EA_orthogonal (GT_Target);
	return (GT_OK);
    }

    const GT_Rectangle&  source_center =
	source_attributes().graphics()->center();
    const GT_Rectangle&  target_center =
	target_attributes().graphics()->center();
    double               source_x, source_y, source_w, source_h;
    double               target_x, target_y, target_w, target_h;
    double               left_point, right_point, upper_point, lower_point;
	
    // get source geometry
    source_x = source_center.x();
    source_y = source_center.y();
    source_w = source_center.w()/2;
    source_h = source_center.h()/2;
    // get target geometry
    target_x = target_center.x();
    target_y = target_center.y();
    target_w = target_center.w()/2;
    target_h = target_center.h()/2;

	
    // determine sector (from point of view of source)
    // above 
    if (source_y+source_h < target_y-target_h) {
	left_point = max (source_x - source_w, target_x - target_w);
	right_point = min (source_x + source_w, target_x + target_w);
	if (left_point > right_point) {
	    // No straight line possible
	    set_EA (GT_Keys::EA_next_corner, GT_Keys::EA_next_corner);
	    return GT_OK;
	}
	// set straight line
	set_sEA_fast (
	    ((left_point+right_point)/2 -source_x) / source_w,0.0);
	set_tEA_fast (
	    ((left_point+right_point)/2 -target_x) / target_w,0.0);
	return GT_OK;
    }

    // below
    if (source_y-source_h > target_y+target_h) {
	left_point = max (source_x - source_w, target_x - target_w);
	right_point = min (source_x + source_w, target_x + target_w);
	if (left_point > right_point) {
	    // No straight line possible
	    set_EA (GT_Keys::EA_next_corner, GT_Keys::EA_next_corner);
	    return GT_OK;
	}
	// set straight line
	set_sEA_fast (
	    ((left_point+right_point)/2 -source_x) / source_w,0.0);
	set_tEA_fast (
	    ((left_point+right_point)/2 -target_x) / target_w,0.0);
	return GT_OK;
    }

    upper_point = min (source_y + source_h, target_y + target_h);
    lower_point = max (source_y - source_h, target_y - target_h);
	
    // left
    if (source_x-source_w > target_x+target_w) {
	// orthogonal from left
	set_sEA_fast (0.0,
	    ((upper_point+lower_point)/2-source_y) / source_h);
	set_tEA_fast (0.0,
	    ((upper_point+lower_point)/2 -target_y) / target_h);
	return GT_OK;
    }
    // right
    set_sEA_fast (0.0,
	((upper_point+lower_point)/2 - source_y) / source_h);
    set_tEA_fast (0.0,
	((upper_point+lower_point)/2 - target_y) / target_h);
    return GT_OK;

}

// ------------------------------------------------------------------------
// Conversion between anchor and screencoordinates
// ------------------------------------------------------------------------

GT_Point GT_Edge_NEI::convert_anchor_to_coordinates (int where)
{
    double node_x, node_y;
    double node_width, node_height;

    if (where == GT_Source) {
	const GT_Node_Attributes& source_attrs = source_attributes();
	node_x = source_attrs.graphics()->center().x();
	node_y = source_attrs.graphics()->center().y();
	node_width = source_attrs.graphics()->center().w();
	node_height = source_attrs.graphics()->center().h();

	return (GT_Point(
	    node_x + delta_x_source()*node_width/2.0,
	    node_y + delta_y_source()*node_height/2.0) );
		
    } else {
	const GT_Node_Attributes& target_attrs = target_attributes();
	node_x = target_attrs.graphics()->center().x();
	node_y = target_attrs.graphics()->center().y();
	node_width = target_attrs.graphics()->center().w();
	node_height = target_attrs.graphics()->center().h();

	return (GT_Point(
	    node_x + delta_x_target()*node_width/2,
	    node_y + delta_y_target()*node_height/2) );
    }
}

// ------------------------------------------------------------------------
// ------------------------------------------------------------------------

GT_Point GT_Edge_NEI::coordinates_to_anchor (int where, GT_Point coord)
{
    double node_x, node_y, node_width, node_height;
    double anchor_x, anchor_y;
    if (where == GT_Source) {
	node_x = source_attributes().graphics()->center().x();
	node_y = source_attributes().graphics()->center().y();
	node_width = source_attributes().graphics()->center().w();
	node_height = source_attributes().graphics()->center().h();
    } else{
	node_x = target_attributes().graphics()->center().x();
	node_y = target_attributes().graphics()->center().y();
	node_width = target_attributes().graphics()->center().w();
	node_height = target_attributes().graphics()->center().h();
    }
    
    if (node_width == 0.0) {
	anchor_x = 0.0;
    } else {
	anchor_x = 2.0*(coord.x() - node_x) / node_width;
    }

    if (node_height == 0.0) {
	anchor_y = 0.0;
    } else {
	anchor_y = 2.0*(coord.y() - node_y) /node_height;
    }
    
    return GT_Point(anchor_x, anchor_y);
}

// ------------------------------------------------------------------------
// some private functions to make other routines easier
// ------------------------------------------------------------------------

GT_Point GT_Edge_NEI::get_bend_inside (int where)
{
    return convert_anchor_to_coordinates (where);
}


GT_Point GT_Edge_NEI::get_bend_outside (int where)
{
    GT_Polyline         line = the_edge_attrs->graphics()->line();

    if (line.size() == 2) {
	
	// can't trust the line. Convert the anchor to the coordinates
	if (where == GT_Source) {
	    return convert_anchor_to_coordinates (GT_Target);
	}
	return convert_anchor_to_coordinates (GT_Source);
    }

    if (where == GT_Source) {
	return *(++line.begin());
    }
    return *(-- --line.end());
}


const GT_Node_Attributes& GT_Edge_NEI::source_attributes() const
{
    return the_edge_attrs->g()->gt(the_edge_attrs->source());
}


const GT_Node_Attributes& GT_Edge_NEI::target_attributes() const
{
    return the_edge_attrs->g()->gt(the_edge_attrs->target());
}

// ----------------------------------------------------------------------------
// Functions needed by update_edgeanchor
// ----------------------------------------------------------------------------

void GT_Edge_NEI::get_corners (const GT_Node_Attributes &attrs,
    vector<GT_Point> &points)
{
    const GT_Rectangle& center = attrs.graphics()->center();

    points[0] = center.anchor_ne();
    points[1] = center.anchor_nw();
    points[2] = center.anchor_se();
    points[3] = center.anchor_sw();  
}

void GT_Edge_NEI::get_middles (const GT_Node_Attributes &attrs,
    vector<GT_Point> &points)
{
    const GT_Rectangle& center = attrs.graphics()->center();

    points[0] = center.anchor_n();
    points[1] = center.anchor_w();
    points[2] = center.anchor_e();
    points[3] = center.anchor_s();
}


void GT_Edge_NEI::source_points (vector<GT_Point> &points, int &p_high)
{
    GT_Key node_fct = source_attributes().node_nei()->default_function();
    GT_Key function = GT_Keys::empty_function;

    // Determine actual function
    if (source_function() != GT_Keys::empty_function) {		
	function = source_function();		
    } else {		
	function = node_fct;
    }

    // determine list of points
    if (function == GT_Keys::empty_function) {
	p_high = 1;
	points[0] = convert_anchor_to_coordinates (GT_Source);
	return;
    }

    if (function == GT_Keys::EA_next_corner) {
	p_high = 4;
	get_corners (source_attributes(), points);
	return;
    }
    
    if (function == GT_Keys::EA_next_middle) {
	p_high = 4;
	get_middles (source_attributes(), points);
	return;
    }

    p_high = 0;
}

void GT_Edge_NEI::target_points (vector<GT_Point> &points, int &p_high)
{
    GT_Key node_fct = target_attributes().node_nei()->default_function();
    GT_Key function = GT_Keys::empty_function;

    // Determine actual function
    if (target_function() != GT_Keys::empty_function) {		
	function = target_function();		
    } else {		
	function = node_fct;
    }
    
    // determine list of points
    if (function == GT_Keys::empty_function) {
	p_high = 1;
	points[0] = convert_anchor_to_coordinates (GT_Target);
	return;
    }

    if (function == GT_Keys::EA_next_corner) {
	p_high = 4;
	get_corners (target_attributes(), points);
	return;
    }
    
    if (function == GT_Keys::EA_next_middle) {
	p_high = 4;
	get_middles (target_attributes(), points);
	return;
    }
    p_high = 0;
}    

// ----------------------------------------------------------------------------
// Function which is called each time an edge has to be redrawn
// ----------------------------------------------------------------------------

void GT_Edge_NEI::orthogonal ()
{

    // Determine actual functions
    GT_Key source_fct = source_attributes().node_nei()->default_function();
    if (source_function() != GT_Keys::empty_function) {		
	source_fct = source_function();
    }
    GT_Key target_fct = target_attributes().node_nei()->default_function();
    if (target_function() != GT_Keys::empty_function) {		
	target_fct = target_function();
    }

    if ((source_fct == GT_Keys::EA_orthogonal) &&
	(target_fct == GT_Keys::EA_orthogonal)) {
	set_EA_connect_orthogonal ();
	return;
    }
    
    if (source_fct == GT_Keys::EA_orthogonal) {
	if (target_fct == GT_Keys::EA_next_corner) {
	    // Special
	    set_EA_next_corner_orthogonal (GT_Target);
	} else {
	    EA_apply_function (target_fct, GT_Target);
 	}
	set_EA_orthogonal (GT_Source);
    }
    
    if (target_fct == GT_Keys::EA_orthogonal) {
	if (source_fct == GT_Keys::EA_next_corner) {
	    // Special
	    set_EA_next_corner_orthogonal (GT_Source);
	} else {
	    EA_apply_function (source_fct, GT_Source);
	}
	set_EA_orthogonal (GT_Target);
    }
}

int GT_Edge_NEI::set_EA (GT_Key src_key, GT_Key trg_key)
{
    // I use a similar function each time an edge has to be redrawn.
    // So I use that function to implement this

    GT_Key def_src = get_EA_default_function (GT_Source);
    GT_Key def_trg = get_EA_default_function (GT_Target);

    int e1 = set_EA_default_function (src_key, GT_Source);
    int e2 = set_EA_default_function (trg_key, GT_Target);

    update_edgeanchor ();
    
    set_EA_default_function (def_src, GT_Source);
    set_EA_default_function (def_trg, GT_Target);
    if ( (e1 == GT_ERROR) || (e2 == GT_ERROR)) {
	return GT_ERROR;
    }
    return GT_OK;
}

int GT_Edge_NEI::update_edgeanchor ()
{
    GT_Point source_min, target_min;
    GT_Point source_anchor, target_anchor;
    int s_high, t_high;
    
    vector<GT_Point> source_pts(4);
    vector<GT_Point> target_pts(4);
    source_points (source_pts, s_high);
    target_points (target_pts, t_high);

    // Check on orthogonal function
    if ((s_high == 0) || (t_high == 0)) {
	orthogonal ();
	return GT_OK;
    }
    

    compute_min (source_pts, source_min, s_high,
	target_pts, target_min, t_high);

    source_anchor = coordinates_to_anchor (GT_Source, source_min);
    target_anchor = coordinates_to_anchor (GT_Target, target_min);
    set_sEA_fast (source_anchor);
    set_tEA_fast (target_anchor);
    
    return GT_OK;
}








