/* This software is distributed under the Lesser General Public License */
//
// Edge_Attributes.cpp
//
// This file implements the class GT_Edge_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Edge_Attributes.cpp,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/07/14 10:53:21 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"
#include "Graph.h"
#include "GML.h"
#include "NEI.h"

#ifndef GT_ATTRIBUTE_LIST_H
#include "Attribute_list.h"
#endif

//////////////////////////////////////////
//
// class GT_Edge_Graphics
//
//////////////////////////////////////////


GT_Edge_Graphics::GT_Edge_Graphics ()
{
}


GT_Edge_Graphics::~GT_Edge_Graphics ()
{
}


void GT_Edge_Graphics::copy (const GT_Edge_Graphics* from,
    GT_Copy type)
{
    baseclass::copy (from, type);
}


GT_List_of_Attributes* GT_Edge_Graphics::clone (GT_Copy type) const
{
    GT_Edge_Graphics* new_edge_graphics = new GT_Edge_Graphics ();
    new_edge_graphics->copy (this, type);
    return new_edge_graphics;
}


void GT_Edge_Graphics::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Edge_Graphics*)parent(), copy_type);
}


void GT_Edge_Graphics::print_geometry (ostream& out) const
{
    if (line().size() > 2 || graphlet->gml->version() < 2) {
	print_object (out, tag_line, GT_Keys::line, line());
    }
}


bool GT_Edge_Graphics::do_print () const
{
    return line().size() > 2 || baseclass::do_print();
}

void GT_Edge_Graphics::print_type (ostream& out) const
{
    print_object (out, tag_type, GT_Keys::type, type(), GT_Keys::type_line);
}


//////////////////////////////////////////
//
// class GT_Edge_Label_Graphics
//
//////////////////////////////////////////


GT_Edge_Label_Graphics::GT_Edge_Label_Graphics ()
{
}


GT_Edge_Label_Graphics::~GT_Edge_Label_Graphics ()
{
}


void GT_Edge_Label_Graphics::copy (const GT_Edge_Label_Graphics* from,
    GT_Copy type)
{
    baseclass::copy (from, type);
}


GT_List_of_Attributes* GT_Edge_Label_Graphics::clone (GT_Copy type) const
{
    GT_Edge_Label_Graphics* new_edge_label_graphics =
	new GT_Edge_Label_Graphics ();
    new_edge_label_graphics->copy (this, type);
    return new_edge_label_graphics;
}


void GT_Edge_Label_Graphics::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Edge_Label_Graphics*)parent(), copy_type);
}


void GT_Edge_Label_Graphics::print_type (ostream& out) const
{
    print_object (out, tag_type, GT_Keys::type, type(), GT_Keys::type_text);
}


//////////////////////////////////////////
//
// class GT_Edge_Attributes
//
//////////////////////////////////////////


GT_Edge_Attributes::GT_Edge_Attributes ()
{
    the_id = -1;

    the_g = 0;
    
    the_graphics = 0;
    the_label_graphics = 0;
    the_edge_nei = 0;

    the_label_anchor_bend = 0;
    the_label_anchor_x = 0;
    the_label_anchor_y = 0;
}


GT_Edge_Attributes::~GT_Edge_Attributes()
{
    if (id() != -1) {
	the_g->undefine_edge_by_id (id());
    }
}



//
// Accessories
//


int GT_Edge_Attributes::id () const
{
    return the_id;
}


void GT_Edge_Attributes::id (int i)
{
    assert (the_id == -1);
    assert (the_g != 0);

    the_id = i;
    the_g->edge_by_id (the_e, i);
}


void GT_Edge_Attributes::g (GT_Graph* g)
{
    assert (the_g == 0);
    this->the_g = g;
}


void GT_Edge_Attributes::e (edge e)
{
    assert (the_e == edge());
    this->the_e = e;
}


void GT_Edge_Attributes::source_port (GT_Key new_name)
{
    // assert (new_name.defined());

    the_source_port = new_name;

    set_tagged_attribute (this, tag_source_port,
	&GT_Edge_Attributes::the_source_port);
}


void GT_Edge_Attributes::target_port (GT_Key new_name)
{
    // assert (new_name.defined());

    the_target_port = new_name;

    set_tagged_attribute (this, tag_target_port,
	&GT_Edge_Attributes::the_target_port);
}



void GT_Edge_Attributes::graphics (GT_Edge_Graphics* graphics)
{
    assert (the_graphics == 0);

    this->the_graphics = graphics;

    if (find (GT_Keys::graphics) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::graphics, the_graphics));
    }
}



void GT_Edge_Attributes::label_graphics (
    GT_Edge_Label_Graphics* label_graphics)
{
    assert (the_label_graphics == 0);

    this->the_label_graphics = label_graphics;

    if (find (GT_Keys::label_graphics) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::label_graphics,
	    the_label_graphics));
	back()->set_safe ();
    }
}


void GT_Edge_Attributes::edge_nei (GT_Edge_NEI* edge_nei)
{
    assert (the_edge_nei == 0);

    this->the_edge_nei = edge_nei;
    
    this->the_edge_nei->edge_attrs (this);
    
    if (find (GT_Keys::edge_anchor) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::edge_anchor, the_edge_nei));
    }
}


void GT_Edge_Attributes::label_anchor_bend (int bend)
{
    assert (bend >= 0);

    this->the_label_anchor_bend = bend;

    set_tagged_attribute (this, tag_label_anchor_bend,
	&GT_Edge_Attributes::the_label_anchor_bend);
}


void GT_Edge_Attributes::label_anchor_x (double x)
{
    this->the_label_anchor_x = x;

    set_tagged_attribute (this, tag_label_anchor_x,
	&GT_Edge_Attributes::the_label_anchor_x);
}


void GT_Edge_Attributes::label_anchor_y (double y)
{
    this->the_label_anchor_y = y;

    set_tagged_attribute (this, tag_label_anchor_y,
	&GT_Edge_Attributes::the_label_anchor_y);
}



//
// copy & clone
//


void GT_Edge_Attributes::copy (const GT_Edge_Attributes* from,
    GT_Copy type)
{
    GT_Common_Attributes::copy (from, type);

    if (copy_test (from, tag_source_port, type)) {
	source_port (from->source_port());
    }

    if (copy_test (from, tag_target_port, type)) {
	target_port (from->target_port());
    }

    if (copy_test (from, tag_label_anchor_bend, type)) {
	label_anchor_bend (from->label_anchor_bend());
    }

    if (copy_test (from, tag_label_anchor_x, type)) {
	label_anchor_x (from->label_anchor_x());
    }

    if (copy_test (from, tag_label_anchor_y, type)) {
	label_anchor_y (from->label_anchor_y());
    }

    if (!type.is_update_from_parent()) {

	if (type.is_deep()) {
	    
	    GT_List_of_Attributes* edge_graphics;
	    find (GT_Keys::graphics, edge_graphics);
	    if (edge_graphics != 0) {
		graphics ((GT_Edge_Graphics*)edge_graphics);
	    }
	    
	    GT_List_of_Attributes* edge_label_graphics;
	    find (GT_Keys::label_graphics, edge_label_graphics);
	    if (edge_label_graphics != 0) {
		label_graphics ((GT_Edge_Label_Graphics*)edge_label_graphics);
	    }
	    
	    GT_List_of_Attributes* nei;
	    find (GT_Keys::edge_anchor, nei);
	    if (nei != 0) {
		edge_nei ((GT_Edge_NEI*)nei);
	    }
	    
	} else {
	    graphics (from->the_graphics);
	    label_graphics (from->the_label_graphics);
	    edge_nei (from->the_edge_nei);
	}
    }
}


GT_List_of_Attributes* GT_Edge_Attributes::clone (GT_Copy type) const
{
    GT_Edge_Attributes* new_edge_attrs = new GT_Edge_Attributes ();
    new_edge_attrs->copy (this, type);
    return new_edge_attrs;
}


void GT_Edge_Attributes::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Edge_Attributes*)parent(), copy_type);
}


//
// extract
//


int GT_Edge_Attributes::extract (GT_List_of_Attributes* current_list,
    string& message)
{
    GT_Key xtr_style;
    if (current_list->extract (GT_Keys::style, xtr_style) &&
	the_g->edge_style (xtr_style) != 0) {
	the_g->edge_style (the_e, xtr_style);
    }

    int code;
    code = baseclass::extract (current_list, message);
    if (code != GT_OK) {
	return GT_ERROR;
    }
	
    GT_Key xtr_source_port;
    if (current_list->extract (GT_Keys::source_port, xtr_source_port) ||
	current_list->extract (GT_Keys::source_port_alternative,
	    xtr_source_port)) {
	source_port (xtr_source_port);
    }
	
    GT_Key xtr_target_port;
    if (current_list->extract (GT_Keys::target_port, xtr_target_port) ||
	current_list->extract (GT_Keys::target_port_alternative,
	    xtr_target_port)) {
	target_port (xtr_target_port);
    }
	
    int xtr_label_anchor_bend;
    if (current_list->extract (GT_Keys::label_anchor_bend,
	    xtr_label_anchor_bend) ||
	current_list->extract (GT_Keys::label_anchor_bend_alternative,
	    xtr_label_anchor_bend)) {
	label_anchor_bend (xtr_label_anchor_bend);
    }

    double xtr_label_anchor_x;
    if (current_list->extract (GT_Keys::label_anchor_x,	xtr_label_anchor_x) ||
	current_list->extract (GT_Keys::label_anchor_x_alternative,
	    xtr_label_anchor_x)) {
	label_anchor_x (xtr_label_anchor_x);
    }

    double xtr_label_anchor_y;
    if (current_list->extract (GT_Keys::label_anchor_y, xtr_label_anchor_y) ||
	current_list->extract (GT_Keys::label_anchor_y_alternative,
	    xtr_label_anchor_y)) {
	label_anchor_y (xtr_label_anchor_y);
    }

    GT_List_of_Attributes* graphics_list;
    if (current_list->extract (GT_Keys::graphics, graphics_list)) {

	if (the_graphics == 0) {
	    graphics (the_g->new_edge_graphics());
	}
		
	code = the_graphics->extract (graphics_list, message);
	if (code != GT_OK) {
	    return GT_ERROR;
	}
	
	the_graphics->splice(the_graphics->begin(), *graphics_list);
    }

    GT_List_of_Attributes* label_graphics_list;
    if (current_list->extract (GT_Keys::label_graphics, label_graphics_list)) {

	if (the_label_graphics == 0) {
	    label_graphics (the_g->new_edge_label_graphics());
	}
		
	code = the_label_graphics->extract (label_graphics_list, message);
	if (code != GT_OK) {
	    return GT_ERROR;
	}
	
	the_label_graphics->splice(the_label_graphics->begin(),
	    *label_graphics_list);
    }

    GT_List_of_Attributes* edge_anchor_list;
    if (current_list->extract (GT_Keys::edge_anchor, edge_anchor_list)) {

	if (the_edge_nei == 0) {
	    edge_nei (new GT_Edge_NEI ());
	}
		
	code = the_edge_nei->extract (edge_anchor_list, message);
	if (code != GT_OK) {
	    return GT_ERROR;
	}
    }

    return GT_OK;
}



//
// print
//


void GT_Edge_Attributes::print (ostream& out) const
{	
    if (source() != node::node()) {
	GT_print (out, GT_Keys::source, g()->gt(source()).id());
    }
    if (target() != node::node()) {
	GT_print (out, GT_Keys::target, g()->gt(target()).id());
    }

    print_object (out, tag_source_port, GT_Keys::source_port, the_source_port);
    print_object (out, tag_target_port, GT_Keys::target_port, the_target_port);
    print_object (out, tag_label_anchor_bend, GT_Keys::label_anchor_bend,
	the_label_anchor_bend);
    print_object (out, tag_label_anchor_x, GT_Keys::label_anchor_x,
	the_label_anchor_x);
    print_object (out, tag_label_anchor_y, GT_Keys::label_anchor_y,
	the_label_anchor_y);

    if (the_g != 0) {
	GT_Key style = the_g->edge_find_style (the_e);
	if (style != GT_Keys::undefined &&
	    style != GT_Keys::default_edge_style) {
	    GT_print (out, GT_Keys::style, style);
	}
    }

    baseclass::print (out);
}


//
// Access to source and target node (backwards compatibility only)
//

node GT_Edge_Attributes::source () const
{
    return (the_g != 0 && the_e != edge()) ? the_e.source() : node::node();
}


node GT_Edge_Attributes::target () const
{
    return (the_g != 0 && the_e != edge()) ? the_e.target() : node::node();
}
