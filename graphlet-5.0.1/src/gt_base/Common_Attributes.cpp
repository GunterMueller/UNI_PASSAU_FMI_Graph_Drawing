/* This software is distributed under the Lesser General Public License */
//
// Common_Attributes.cc
//
// This file defines the class GT_Common_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Common_Attributes.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:43:16 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"
#include "Graph.h"


//////////////////////////////////////////
//
// class GT_Common_Attributes
//
//////////////////////////////////////////


GT_Common_Attributes::GT_Common_Attributes()
{
    this->the_visible = true;
}



GT_Common_Attributes::~GT_Common_Attributes()
{
}



//////////////////////////////////////////
//
// Accessories
//
//////////////////////////////////////////


void GT_Common_Attributes::label (const string& s)
{
    the_label = s;

    set_tagged_attribute (this, tag_label, &GT_Common_Attributes::the_label);
}


void GT_Common_Attributes::name (const string& s)
{
    the_name = s;

    set_tagged_attribute (this, tag_name, &GT_Common_Attributes::the_name);
}


void GT_Common_Attributes::label_anchor (GT_Key anchor)
{
    the_label_anchor = anchor;

    set_tagged_attribute (this, tag_label_anchor,
	&GT_Common_Attributes::the_label_anchor);
}


void GT_Common_Attributes::visible (int v)
{
    the_visible = v;

    set_tagged_attribute (this, tag_visible,
	&GT_Common_Attributes::the_visible);
}



//////////////////////////////////////////
//
// Methods
//
//////////////////////////////////////////


void GT_Common_Attributes::copy (const GT_Common_Attributes* from,
	GT_Copy copy_type)
{
    baseclass::copy (from, copy_type);

    if (copy_test (from, GT_Common_Attributes::tag_label, copy_type)) {
	label (from->the_label);
    }
    if (copy_test (from, GT_Common_Attributes::tag_name, copy_type)) {
	name (from->the_name);
    }
    if (copy_test (from, GT_Common_Attributes::tag_label_anchor, copy_type)) {
	label_anchor (from->the_label_anchor);
    }
    if (copy_test (from, GT_Common_Attributes::tag_visible, copy_type)) {
	visible (from->the_visible);
    }
}



//////////////////////////////////////////
//
// extract & print
//
//////////////////////////////////////////


int GT_Common_Attributes::extract (GT_List_of_Attributes* current_list,
    string& /* message */)
{
    string xtr_label;
    if (current_list->extract (GT_Keys::label, xtr_label)) {
	label (xtr_label);
    }

    string xtr_name;
    if (current_list->extract (GT_Keys::name, xtr_name)) {
	name (xtr_name);
    }

    GT_Key xtr_label_anchor;
    if (current_list->extract (GT_Keys::label_anchor, xtr_label_anchor)) {
	label_anchor (xtr_label_anchor);
    }

    int xtr_visible;
    if (current_list->extract (GT_Keys::visible, xtr_visible)) {
	visible (xtr_visible);
    }

    return GT_OK;
}	



void GT_Common_Attributes::print (ostream& out) const
{
    print_object (out, tag_label, GT_Keys::label, label());
    print_object (out, tag_name, GT_Keys::name, name());
    print_object (out, tag_label_anchor, GT_Keys::label_anchor,
	label_anchor());
    print_object (out, tag_visible, GT_Keys::visible, visible());

    GT_List_of_Attributes::print (out);
}



//
// Printing Optimization
//
    
bool GT_Common_Attributes::do_print () const
{
    return baseclass::do_print();
}
