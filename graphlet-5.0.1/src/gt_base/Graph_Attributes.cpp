/* This software is distributed under the Lesser General Public License */
//
// Graph_Attributes.cc
//
// This module implements the class GT_Graph_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Graph_Attributes.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:43 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//


#include "Graphlet.h"
#include "Graph.h"
#include "GML.h"

#ifndef GT_ATTRIBUTE_LIST_H
#include "Attribute_list.h"
#endif


//////////////////////////////////////////
//
// class GT_Graph_Graphics
//
//////////////////////////////////////////


GT_Graph_Graphics::GT_Graph_Graphics ()
{
}


GT_Graph_Graphics::~GT_Graph_Graphics ()
{
}


void GT_Graph_Graphics::copy (const GT_Graph_Graphics* from,
    GT_Copy type)
{
    baseclass::copy (from, type);
}


GT_List_of_Attributes* GT_Graph_Graphics::clone (GT_Copy type) const
{
    GT_Graph_Graphics* new_graph_graphics = new GT_Graph_Graphics ();
    new_graph_graphics->copy (this, type);
    return new_graph_graphics;
}


void GT_Graph_Graphics::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Graph_Graphics*)parent(), copy_type);
}



//////////////////////////////////////////
//
// class GT_Graph_Label_Graphics
//
//////////////////////////////////////////


GT_Graph_Label_Graphics::GT_Graph_Label_Graphics ()
{
}


GT_Graph_Label_Graphics::~GT_Graph_Label_Graphics ()
{
}


void GT_Graph_Label_Graphics::copy (const GT_Graph_Label_Graphics* from,
    GT_Copy type)
{
    baseclass::copy (from, type);
}


GT_List_of_Attributes* GT_Graph_Label_Graphics::clone (GT_Copy type) const
{
    GT_Graph_Label_Graphics* new_graph_graphics =
	new GT_Graph_Label_Graphics ();
    new_graph_graphics->copy (this, type);
    return new_graph_graphics;
}


void GT_Graph_Label_Graphics::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Graph_Label_Graphics*)parent(), copy_type);
}



//////////////////////////////////////////
//
// class GT_Graph_Attributes
//
//////////////////////////////////////////


GT_Graph_Attributes::GT_Graph_Attributes ()
{
    the_g = 0;

    the_id = -1;
    the_graphics = 0;
    the_label_graphics = 0;
}


GT_Graph_Attributes::~GT_Graph_Attributes()
{
//     delete the_graphics;
//     delete the_label_graphics;
}


//
// Accessories
//


int GT_Graph_Attributes::id () const
{
    return the_id;
}


void GT_Graph_Attributes::id (int i)
{
    assert (the_id == -1);
    assert (the_g != 0);

    the_id = i;
}


void GT_Graph_Attributes::g (GT_Graph* g)
{
    the_g = g;
}


void GT_Graph_Attributes::creator (const string& c)
{
    the_creator = c;
}


void GT_Graph_Attributes::graphics (GT_Graph_Graphics* graphics)
{
    assert (the_graphics == 0);
    this->the_graphics = graphics;

    if (find (GT_Keys::graphics) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::graphics, the_graphics));
    }
}



void GT_Graph_Attributes::label_graphics (
    GT_Graph_Label_Graphics* label_graphics)
{
    assert (the_label_graphics == 0);
    this->the_label_graphics = label_graphics;

    if (find (GT_Keys::label_graphics) == end()) {
	push_back (new GT_Attribute_list (GT_Keys::label_graphics,
	    the_label_graphics));
    }
}



//
// Virtual Copy Constructor
//

void GT_Graph_Attributes::copy (const GT_Graph_Attributes* from,
    GT_Copy type)
{
    GT_Common_Attributes::copy (from, type);

    if (type.is_deep()) {
	
	GT_List_of_Attributes* graph_graphics;
	find (GT_Keys::graphics, graph_graphics);
	if (graph_graphics != 0) {
	    graphics ((GT_Graph_Graphics*)graph_graphics);
	}
	
	GT_List_of_Attributes* graph_label_graphics;
	find (GT_Keys::label_graphics, graph_label_graphics);
	if (graph_label_graphics != 0) {
	    label_graphics ((GT_Graph_Label_Graphics*)graph_label_graphics);
	}

    } else {
	graphics (from->the_graphics);
	label_graphics (from->the_label_graphics);
    }
}


GT_List_of_Attributes* GT_Graph_Attributes::clone (GT_Copy type) const
{
    GT_Graph_Attributes* new_graph_attrs = new GT_Graph_Attributes ();
    new_graph_attrs->copy (this, type);
    return new_graph_attrs;
}


void GT_Graph_Attributes::update_from_parent (GT_Copy copy_type)
{
    copy ((GT_Graph_Attributes*)parent(), copy_type);
}



//
// extract
//


int GT_Graph_Attributes::extract (GT_List_of_Attributes* current_list,
    string& message)
{
    int code;
    code = baseclass::extract (current_list, message);
    if (code != GT_OK) {
	return GT_ERROR;
    }
	
    string xtr_creator;
    if (current_list->extract (GT_Keys::creator, xtr_creator)) {
	creator (xtr_creator);
    }

    GT_List_of_Attributes* graphics_list;
    if (current_list->extract (GT_Keys::graphics, graphics_list)) {

	if (the_graphics == 0) {
	    graphics (the_g->new_graph_graphics());
	}
		
	code = the_graphics->extract (graphics_list, message);
	if (code != GT_OK) {
	    return GT_ERROR;
	}
	
	the_graphics->splice(the_graphics->begin(), *graphics_list);
    }

    GT_List_of_Attributes* label_graphics_list;
    if (current_list->extract (GT_Keys::label_graphics,
	label_graphics_list)) {

	if (the_label_graphics == 0) {
	    label_graphics (the_g->new_graph_label_graphics());
	}
		
	code = the_label_graphics->extract (label_graphics_list, message);
	if (code != GT_OK) {
	    return GT_ERROR;
	}

	the_label_graphics->splice(the_label_graphics->begin(), *label_graphics_list);
    }

    return GT_OK;
}



//
// print
//


void GT_Graph_Attributes::print (ostream& out) const
{
    GT_print (out, GT_Keys::version, graphlet->gml->version());

    if (the_creator.length() > 0) {
	GT_print (out, GT_Keys::creator, the_creator);
    }

    baseclass::print(out);	
}
