/* This software is distributed under the Lesser General Public License */
//
// Node_Attributes.cc
//
// This file defines the class GT_Node_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Node_Attributes.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:28 $
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
#include "node_NEI.h"

#ifndef GT_ATTRIBUTE_LIST_H
#include "Attribute_list.h"
#endif



//////////////////////////////////////////
//
// class GT_Node_Graphics
//
//////////////////////////////////////////


GT_Node_Graphics::GT_Node_Graphics ()
{
}


GT_Node_Graphics::~GT_Node_Graphics ()
{
}


void GT_Node_Graphics::copy (const GT_Node_Graphics* from,
    GT_Copy type)
{
    baseclass::copy (from, type);
}


GT_List_of_Attributes* GT_Node_Graphics::clone (GT_Copy type) const
{
    GT_Node_Graphics* new_node_graphics = new GT_Node_Graphics ();
    new_node_graphics->copy (this, type);
    return new_node_graphics;
}


void GT_Node_Graphics::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Node_Graphics*)parent(), copy_type);
}


void GT_Node_Graphics::print_geometry (ostream& out) const
{
    if (is_initialized (GT_Common_Graphics::tag_geometry)) {

	if (type() != GT_Keys::type_line) {
	    if (x() > 0.0) {
		GT_print (out, GT_Keys::x, x());
	    }
	    if (y() > 0.0) {
		GT_print (out, GT_Keys::y, y());
	    }
	}

	if (type() != GT_Keys::type_text &&
// 	    type() != GT_Keys::type_bitmap &&
// 	    type() != GT_Keys::type_image &&
	    type() != GT_Keys::type_line) {
	    
	    if (w() > 0.0) {
		if (parent() == 0 ||
		    w() != ((GT_Common_Graphics*)parent())->w() ||
		    graphlet->gml->version() < 2) {
		    GT_print (out, GT_Keys::w, w());
		}
	    }
	    if (h() > 0.0) {
		if (parent() == 0 ||
		    h() != ((GT_Common_Graphics*)parent())->h() ||
		    graphlet->gml->version() < 2) {
		    GT_print (out, GT_Keys::h, h());
		}
	    }
	}
    }

    print_object (out, tag_line, GT_Keys::line, line());
}


void GT_Node_Graphics::print_type (ostream& out) const
{
    print_object (out, tag_type, GT_Keys::type, type(),
	GT_Keys::type_rectangle);
}


bool GT_Node_Graphics::do_print () const
{
    return baseclass::do_print();
}


//////////////////////////////////////////
//
// class GT_Node_Label_Graphics
//
//////////////////////////////////////////


GT_Node_Label_Graphics::GT_Node_Label_Graphics ()
{
}


GT_Node_Label_Graphics::~GT_Node_Label_Graphics ()
{
}


void GT_Node_Label_Graphics::copy (const GT_Node_Label_Graphics* from,
    GT_Copy type)
{
    baseclass::copy (from, type);
}


GT_List_of_Attributes* GT_Node_Label_Graphics::clone (GT_Copy type) const
{
    GT_Node_Label_Graphics* new_node_graphics = new GT_Node_Label_Graphics ();
    new_node_graphics->copy (this, type);
    return new_node_graphics;
}


void GT_Node_Label_Graphics::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Node_Label_Graphics*)parent(), copy_type);
}


void GT_Node_Label_Graphics::print_type (ostream& out) const
{
    print_object (out, tag_type, GT_Keys::type, type(), GT_Keys::type_text);
}



//////////////////////////////////////////
//
// class GT_Node_Attributes
//
//////////////////////////////////////////


GT_Node_Attributes::GT_Node_Attributes ()
{
    the_id = -1;
    the_g = 0;

    the_graphics = 0;
    the_label_graphics = 0;

    the_node_nei = 0;
}



GT_Node_Attributes::~GT_Node_Attributes()
{
    if (id() != -1) {
	the_g->undefine_node_by_id (id());
    }
}


//
// Accessories
//


int GT_Node_Attributes::id () const
{
    return the_id;
}


void GT_Node_Attributes::id (int i)
{
    assert (the_id == -1);
    assert (the_g != 0);

    the_id = i;
    the_g->node_by_id (the_n, i);
}


void GT_Node_Attributes::g (GT_Graph* g)
{
    assert (the_g == 0);
    this->the_g = g;
}


void GT_Node_Attributes::n (class node n)
{
    assert (the_n == node::node());
    this->the_n = n;
}


void GT_Node_Attributes::ports (const GT_Ports& new_ports)
{
    the_ports = new_ports;
    set_tagged_attribute (this, tag_ports, &GT_Node_Attributes::the_ports);
}


void GT_Node_Attributes::graphics (GT_Node_Graphics* graphics)
{
    assert (the_graphics == 0);
    this->the_graphics = graphics;

    if (find (GT_Keys::graphics) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::graphics, the_graphics));
    }
}


void GT_Node_Attributes::label_graphics (
    GT_Node_Label_Graphics* label_graphics)
{
    assert (the_label_graphics == 0);
    this->the_label_graphics = label_graphics;

    if (find (GT_Keys::label_graphics) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::label_graphics,
	    the_label_graphics));
	back()->set_safe ();
    }
}


void GT_Node_Attributes::node_nei (GT_Node_NEI* node_nei)
{
    assert (the_node_nei == 0);
    this->the_node_nei = node_nei;
    this->the_node_nei->node_attrs (this);
    
    if (find (GT_Keys::edge_anchor) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::edge_anchor, the_node_nei));
    }
}


//
// copy & clone
//

void GT_Node_Attributes::copy (const GT_Node_Attributes* from,
    GT_Copy type)
{
    GT_Common_Attributes::copy (from, type);

    if (copy_test (from, tag_ports, type)) {
	ports (from->ports());
    }

    if (!type.is_update_from_parent()) {

	if (type.is_deep()) {

	    GT_List_of_Attributes* node_graphics;
	    find (GT_Keys::graphics, node_graphics);
	    if (node_graphics != 0) {
		graphics ((GT_Node_Graphics*)node_graphics);
	    }
	
	    GT_List_of_Attributes* node_label_graphics;
	    find (GT_Keys::label_graphics, node_label_graphics);
	    if (node_label_graphics != 0) {
		label_graphics ((GT_Node_Label_Graphics*)node_label_graphics);
	    }
	
	    GT_List_of_Attributes* nei;
	    find (GT_Keys::edge_anchor, nei);
	    if (nei != 0) {
		node_nei ((GT_Node_NEI*)nei);
	    }
	
	} else {
	    graphics (from->the_graphics);
	    label_graphics (from->the_label_graphics);
	    node_nei (from->the_node_nei);
	}
    }
}


GT_List_of_Attributes* GT_Node_Attributes::clone (GT_Copy type) const
{
    GT_Node_Attributes* new_node_attrs = new GT_Node_Attributes ();
    new_node_attrs->copy (this, type);
    return new_node_attrs;
}


void GT_Node_Attributes::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Node_Attributes*)parent(), copy_type);
}



//
// extract
//


int GT_Node_Attributes::extract (GT_List_of_Attributes* current_list,
    string& message)
{	
    GT_Key xtr_style;
    if (current_list->extract (GT_Keys::style, xtr_style) &&
	the_g->node_style (xtr_style) != 0) {
	the_g->node_style (the_n, xtr_style);
    }

    int code;
    code = baseclass::extract (current_list, message);
    if (code != GT_OK) {
	return GT_ERROR;
    }

    GT_List_of_Attributes* ports_list;
    if (current_list->extract (GT_Keys::ports, ports_list)) {

	GT_Ports ports;

	GT_List_of_Attributes* port_list;
	while (ports_list->extract (GT_Keys::port, port_list)) {

	    GT_Port new_port;
	    
	    double xtr_x;
	    if (port_list->extract (GT_Keys::x, xtr_x)) {
		new_port.x (xtr_x);
	    }
	    double xtr_y;
	    if (port_list->extract (GT_Keys::y, xtr_y)) {
		new_port.y (xtr_y);
	    }
	    GT_Key xtr_name;
	    if (port_list->extract (GT_Keys::name, xtr_name)) {
		new_port.name (xtr_name);
	    }

	    ports.push_back (new_port);
	}
	    
	this->ports (ports);
    }

    GT_List_of_Attributes* graphics_list;
    if (current_list->extract (GT_Keys::graphics, graphics_list)) {

	if (the_graphics == 0) {
	    graphics (the_g->new_node_graphics());
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
	    label_graphics (the_g->new_node_label_graphics());
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

	if (the_node_nei == 0) {
	    node_nei (new GT_Node_NEI ());
	}
		
	code = the_node_nei->extract (edge_anchor_list, message);
	if (code != GT_OK) {
	    return GT_ERROR;
	}
    }

    return GT_OK;
}	



//
// print
//


void GT_Node_Attributes::print (ostream& out) const
{
    if (the_id != -1) {
	GT_print (out, GT_Keys::id, the_id);
    }

    print_object (out, tag_ports, GT_Keys::ports, the_ports);

    if (the_g != 0) {
	GT_Key style = the_g->node_find_style (the_n);
	if (style != GT_Keys::undefined &&
	    style != GT_Keys::default_node_style) {
	    GT_print (out, GT_Keys::style, style);
	}
    }

    baseclass::print (out);
}
