/* This software is distributed under the Lesser General Public License */
// ---------------------------------------------------------------------
// node_NEI.cpp
// 
// Memberfunctions for the Node-Edge-Interface
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/node_NEI.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:13 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project, Walter Bachl
//

#include <gt_base/Graphlet.h>
#include <gt_base/Point.h>
#include "Graph.h"

#include <vector>

#include "Keys.h"
#include "NEI.h"


// -------------------------------------------------------------------------
// Constructor
// -------------------------------------------------------------------------

GT_Node_NEI::GT_Node_NEI ()
{
    the_node_attrs = 0;
    the_default_function = GT_Keys::empty_function;
}

// --------------------------------------------------------------------------
// --------------------------------------------------------------------------

void GT_Node_NEI::node_attrs (GT_Node_Attributes* attrs)
{
    the_node_attrs = attrs;
}


void GT_Node_NEI::default_function (GT_Key function)
{
    the_default_function = function;
    set_tagged_attribute (this, tag_default_function,
	&GT_Node_NEI::the_default_function);
}


//
// Copy & Clone
//

void GT_Node_NEI::copy (const GT_Node_NEI* from,
    GT_Copy type)
{
    baseclass::copy (from, type);

    if (from->is_initialized (GT_Node_NEI::tag_default_function)) {
	default_function (from->the_default_function);
    }
}


GT_List_of_Attributes* GT_Node_NEI::clone (GT_Copy type) const
{
    GT_Node_NEI* new_node_nei = new GT_Node_NEI ();
    new_node_nei->copy (this, type);
    return new_node_nei;
}


void GT_Node_NEI::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Node_NEI*)parent(), copy_type);
}


// --------------------------------------------------------------------------
// Output of parameters of NEI
// --------------------------------------------------------------------------

bool GT_Node_NEI::do_print () const
{
    return print_test (tag_default_function, GT_Keys::default_function,
	default_function(), GT_Keys::empty_function);
}


void GT_Node_NEI::print (ostream& out) const
{
    print_object (out, tag_default_function, GT_Keys::default_function,
	default_function(), GT_Keys::empty_function);
}

// -------------------------------------------------------------------------
// Extract values from file
// -------------------------------------------------------------------------

int GT_Node_NEI::extract (GT_List_of_Attributes* current_list,
    string& /* message */)
{
    GT_Key xtr_fun;
    if (current_list->extract (GT_Keys::default_function, xtr_fun)) {
	default_function(xtr_fun);
    }

    return GT_OK;
}

// -------------------------------------------------------------------------
// Update edgeanchors of adjacent edges
// -------------------------------------------------------------------------

// int GT_Node_NEI::update ()
// {
//     return EA_default_function ();
// }

// -------------------------------------------------------------------------
// execute default function. Apply the function only to the edge e
// -------------------------------------------------------------------------

// int GT_Node_NEI::EA_default_function (edge e, int where)
// {
//     GT_Edge_NEI *edge_nei = the_node_attrs->g()->gt(e).edge_nei();
    
//     if(default_function() == GT_Keys::EA_next_corner) {
// 	edge_nei->set_EA_next_corner (where);
//     }
//    else if(default_function() == GT_Keys::EA_next_middle) {
// 	edge_nei->set_EA_next_middle (where);
//     }
//    else if(default_function() == GT_Keys::EA_orthogonal) {
// 	edge_nei->set_EA_orthogonal (where);
//     }
//     else if(default_function() == GT_Keys::empty_function) {
// 	// Do nothing
//     }			
	
//     return GT_OK;
// }

// -------------------------------------------------------------------------
// execute default function. Apply the function to all adjacent edges.
// -------------------------------------------------------------------------

// int GT_Node_NEI::EA_default_function()
// {
//     if(default_function() == GT_Keys::EA_next_corner) {
// 	alledges_set_EA_next_corner ();
//     }
//    else if(default_function() == GT_Keys::EA_next_middle) {
// 	alledges_set_EA_next_middle ();
//     }
//     else if(default_function() == GT_Keys::EA_orthogonal) {
// 	alledges_set_EA_orthogonal ();
//     }
//     // else if(default_function() == GT_Keys::EA_distribute_uniform) {
// 	// distribute_edges_uniform ();
//     // }
//     else if(default_function() == GT_Keys::empty_function) {
// 	// Do nothing
//     }			
	
//     return GT_OK;
// }


// -------------------------------------------------------------------------
// get and set default function
// -------------------------------------------------------------------------

int GT_Node_NEI::set_EA_default_function (GT_Key function)
{    
    default_function (function);
    return GT_OK;
}


int GT_Node_NEI::set_EA_default_function (string function)
{
    GT_Key key_fun = graphlet->keymapper.add (function);
    set_EA_default_function(key_fun);

    return GT_OK;
}


GT_Key GT_Node_NEI::get_EA_default_function ()
{
    return default_function();
}

// -------------------------------------------------------------------------
// -------------------------------------------------------------------------


// int GT_Node_NEI::EA_apply_function (GT_Key function)
// {  
//     if (function == GT_Keys::EA_next_corner) {
// 	alledges_set_EA_next_corner ();
//     }
//     else if (function == GT_Keys::EA_next_middle) {
// 	alledges_set_EA_next_middle ();
//     }
//     else if (function == GT_Keys::EA_orthogonal) {
// 	alledges_set_EA_orthogonal ();
//     }
// //     else if (function == GT_Keys::EA_distribute_uniform) {
// // 	distribute_edges_uniform ();
// //     }	
//     else if (function == GT_Keys::empty_function) {
// 	// Do nothing
//     }
	
//     return GT_OK;
// }


// int GT_Node_NEI::EA_apply_function (string function)
// {
//     GT_Key function_key =  graphlet->keymapper.add (function);

//     return EA_apply_function (function_key);  
// }

// -------------------------------------------------------------------------
// Same functions as for edges. But now for all adjacent edges
// We apply the functions at the node for which we call it
// -------------------------------------------------------------------------

int GT_Node_NEI::alledges_set_EA (double delta_x, double delta_y)
{
    edge cur_edge;
    int error_occured = false;
    forall_out_edges (cur_edge, the_node_attrs->n()) {
	error_occured = error_occured ||
	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
		GT_Source, delta_x, delta_y);
    }
	
    forall_in_edges (cur_edge, the_node_attrs->n()) {
	error_occured = error_occured ||
	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
		GT_Target, delta_x, delta_y);
    }

    if (error_occured) {
	return GT_ERROR;
    }
	
    return GT_OK;
}

// int GT_Node_NEI::alledges_set_EA_center ()
// {
//     edge cur_edge;
//     int error_occured = false;
//     forall_out_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
// 		GT_Source, 0.0, 0.0);
//     }
	
//     forall_in_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
// 		GT_Target, 0.0, 0.0);
//     }

//     if (error_occured) {
// 	return GT_ERROR;
//     }
	
//     return GT_OK;
// }

int GT_Node_NEI::alledges_set_EA (GT_Key direction)
{
    edge cur_edge;
    int error_occured = false;
    forall_out_edges (cur_edge, the_node_attrs->n()) {
	error_occured = error_occured ||
	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
		GT_Source, direction);
    }
	
    forall_in_edges (cur_edge, the_node_attrs->n()) {
	error_occured = error_occured ||
	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
		GT_Target, direction);
    }

    if (error_occured) {
	return GT_ERROR;
    }
	
    return GT_OK;
}

// int GT_Node_NEI::alledges_set_EA_next_corner ()
// {
//     edge cur_edge;
//     int error_occured = false;
//     forall_out_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA_next_corner (GT_Source);
//     }
	
//     forall_in_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA_next_corner (GT_Target);
//     }

//     if (error_occured) {
// 	return GT_ERROR;
//     }
	
//     return GT_OK;
// }

// int GT_Node_NEI::alledges_set_EA_next_middle ()
// {
//     edge cur_edge;
//     int error_occured = false;
//     forall_out_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA_next_middle (GT_Source);
//     }
	
//     forall_in_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA_next_middle (GT_Target);
//     }

//     if (error_occured) {
// 	return GT_ERROR;
//     }
	
//     return GT_OK;
// }

// int GT_Node_NEI::alledges_set_EA_orthogonal ()
// {
//     edge cur_edge;
//     int error_occured = false;
	
//     forall_out_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->
// 	    set_EA_orthogonal (GT_Source);
//     }
	
//     forall_in_edges (cur_edge, the_node_attrs->n()) {
// 	error_occured = error_occured ||
// 	    the_node_attrs->g()->gt(cur_edge).edge_nei()->
// 	    set_EA_orthogonal (GT_Target);
//     }

//     if (error_occured) {
// 	return GT_ERROR;
//     }
	
//     return GT_OK;
// }

int GT_Node_NEI::alledges_set_EA (GT_Key src_key, GT_Key trg_key)
{
    edge cur_edge;
    int error_occured = false;
	
    forall_inout_edges (cur_edge, the_node_attrs->n()) {
	error_occured = error_occured ||
	    the_node_attrs->g()->gt(cur_edge).edge_nei()->
	    set_EA (src_key, trg_key);
    }
	
   if (error_occured) {
	return GT_ERROR;
    }
	
    return GT_OK;
}

// int GT_Node_NEI::alledges_set_EA_connect_middle_shortest ()
// {
//     // gibts bald nimma
//    return GT_OK;
// }

// int GT_Node_NEI::alledges_set_EA_connect_corner_shortest ()
// {
//    return GT_OK;
// }

// int GT_Node_NEI::alledges_set_EA_connect_orthogonal ()
// {
 	
//     return GT_OK;
// }


// #########################################################################
// STOP
// Don't use the functions below. They don't work
// #########################################################################

// -------------------------------------------------------------------------
// And now the really pretty functions
// -------------------------------------------------------------------------

// void insert_in_sortseq (edge e, double where, sortseq<double,edge> &sequenz)
// {
//     // determine key for the sequenz

//     if (sequenz.lookup(where)) {
// 	if ( sequenz.pred(sequenz.lookup(where)) ) {
// 	    where = (where
// 		+ sequenz.key (sequenz.pred(sequenz.lookup(where)))) /2;
// 	} else {
// 	    where = sequenz.key (sequenz.lookup(where))- 0.1;
// 	}
//     }
//     sequenz.insert (where, e);
// }


// int GT_Node_NEI::distribute_edges_uniform (int side)
// {
//     edge                  cur_edge;
//     sortseq<double, edge> edge_sequenz;
//     int                   counter = 0;
//     // const GT_Rectangle&   node_center = the_node_attrs.graphics()->center();
//     double                offset, anchor_side, anchor_actual;
//     double                 the_key;
	
//     // collect all edges at the side

//     cout << "----------Distribute edges------------" << endl;
//     cout << "For side: " << side << endl;
	
//     forall_in_edges (cur_edge, the_node_attrs->n()) {
// 	if (the_node_attrs->g()->gt(cur_edge).edge_nei()->get_side(GT_Target)
// 	    == side ) {
// 	    counter++;

// 	    if (side == GT_Top || side == GT_Bottom) {
// 		the_key = the_node_attrs->g()->gt(cur_edge).edge_nei()->
// 		    clip_edge(GT_Target).xcoord();
// 	    } else {
// 		the_key = the_node_attrs->g()->gt(cur_edge).edge_nei()->
// 		    clip_edge(GT_Target).ycoord();
// 	    }
// 	    insert_in_sortseq (cur_edge, the_key, edge_sequenz);
// 	}
//     }
//     forall_out_edges (cur_edge, the_node_attrs->n()) {
// 	cout << "for outedges: " << endl;
// 	if (the_node_attrs->g()->gt(cur_edge).edge_nei()->get_side(GT_Source)
// 	    == side ) {
	    
// 	    counter++;	
// 	    if (side == GT_Top || side == GT_Bottom) {
// 		the_key = the_node_attrs->g()->gt(cur_edge).edge_nei()->
// 		    clip_edge(GT_Source).xcoord();
// 	    } else {
// 		the_key = the_node_attrs->g()->gt(cur_edge).edge_nei()->
// 		    clip_edge(GT_Source).ycoord();
// 	    }
// 	    insert_in_sortseq (cur_edge, the_key, edge_sequenz);
// 	}
//     }

//     // easy. go through sequenz and set edgeanchors

//     // get offset
//     offset = 2.0 / (double)(counter+1);
//     // cout << "Offset " << offset << endl;
	
//     // get anchor-value for the vertical side
//     if (side == GT_Top || side == GT_Right) {
// 	anchor_side = 1.0;
//     } else {
// 	anchor_side = -1.0;
//     }
//     anchor_actual = -1.0;
	
//     if (side == GT_Top || side == GT_Bottom) {
		
// 	while (edge_sequenz.min()) {
// 	    anchor_actual = anchor_actual + offset;
// 	    // cout << "Anchor " << anchor_actual << endl;
// 	    cur_edge = edge_sequenz.inf (edge_sequenz.min());
// 	    edge_sequenz.del (edge_sequenz.key (edge_sequenz.min()));
			
// 	    if (the_node_attrs->g()->gt(cur_edge).source() ==
// 		the_node_attrs->n()) {
		
// 		the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
// 		    GT_Source, anchor_actual, anchor_side);
// 	    } else {
		
// 		the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
// 		    GT_Target, anchor_actual, anchor_side);
// 	    }
// 	}

//     } else {
		
// 	while (edge_sequenz.min()) {
// 	    anchor_actual = anchor_actual + offset;
// 	    cur_edge = edge_sequenz.inf (edge_sequenz.min());
// 	    edge_sequenz.del (edge_sequenz.key (edge_sequenz.min()));

// 	    if (the_node_attrs->g()->gt(cur_edge).source() ==
// 		the_node_attrs->n()) {
		
// 		the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
// 		    GT_Source, anchor_side, anchor_actual);

// 	    } else {
		
// 		the_node_attrs->g()->gt(cur_edge).edge_nei()->set_EA (
// 		    GT_Target, anchor_side, anchor_actual);
// 	    }
// 	}
//     }
	
//     return GT_OK;
// }


// int GT_Node_NEI::distribute_edges_uniform ()
// {
//     distribute_edges_uniform (GT_Top);
//     distribute_edges_uniform (GT_Bottom);
//     distribute_edges_uniform (GT_Left);
//     distribute_edges_uniform (GT_Right);
	
//     return GT_OK;
// }


// int GT_Node_NEI::distribute_edges_uniform_in_sectors (int /*side*/)
// {
	
//     return GT_OK;
// }


// int GT_Node_NEI::distribute_edges_uniform_in_sectors ()
// {
	
//     return GT_OK;
// }


