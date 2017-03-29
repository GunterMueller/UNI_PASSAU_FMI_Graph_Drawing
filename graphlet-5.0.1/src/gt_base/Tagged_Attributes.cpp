/* This software is distributed under the Lesser General Public License */
//
// Tagged_Attributes.cc
//
// This file defines the class GT_Tagged_Attributes
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Tagged_Attributes.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:59 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"
#include "Attributes.h"
#include "Tagged_Attributes.h"
#include "GML.h"

#include "Point.h"
#include "Rectangle.h"
#include "Polyline.h"
#include "Ports.h"

//////////////////////////////////////////
//
// class GT_Tagged_Attributes
//
//////////////////////////////////////////


GT_Tagged_Attributes::GT_Tagged_Attributes()
{
    the_initialized = 0;
    the_changed = 0;
    the_from_parent = 0;

    the_parent = 0;
}


GT_Tagged_Attributes::~GT_Tagged_Attributes()
{
}


//
// Accessors
//


void GT_Tagged_Attributes::parent (GT_Tagged_Attributes* p)
{
    the_parent = p;
    the_from_parent = 0;

    GT_List_of_Attributes* list;
    GT_List_of_Attributes* list_parent;

    for(const_iterator it = begin(); it != end(); ++it)
    {
	GT_Attribute_Base* attribute = *it;
	if (attribute->is_complex() && attribute->value_list(list)) {
	    if (the_parent->find (attribute->key(), list_parent) !=
		the_parent->end())
	    {

		GT_Tagged_Attributes* tagged_list =
		    (GT_Tagged_Attributes*)list;
		GT_Tagged_Attributes* tagged_list_parent =
		    (GT_Tagged_Attributes*)list_parent;
		tagged_list->the_from_parent = 0;
		tagged_list->the_parent = tagged_list_parent;
	    }
	}
    }

    update_from_parent (GT_Copy::deep_update_from_parent);
}


//
// Copy
//

void GT_Tagged_Attributes::copy (const GT_Tagged_Attributes* from,
    GT_Copy type)
{
    if (type.is_copy_from_parent() && the_parent != from) {
	parent ((GT_Tagged_Attributes*) from);
    }

    baseclass::copy (from, type);
}


bool GT_Tagged_Attributes::copy_test (const GT_Tagged_Attributes* from,
    int tag,
    GT_Copy copy_type) const
{
    return
	(copy_type.is_update_from_parent() &&
	    from->is_changed (tag))
	||
	(!copy_type.is_update_from_parent() &&
	    from->is_initialized (tag));
}



//
// all_changed
//

void GT_Tagged_Attributes::all_changed (int begin, int end)
{
    for (int tag = begin; tag <= end; tag = tag << 1) {	    
	if (is_initialized (tag)) {
	    set_changed (tag);
	}
    }
}


//
// Printing Optimization
//
    
bool GT_Tagged_Attributes::do_print () const
{
    return baseclass::do_print() || (the_initialized != 0);
}


//
// Initialization
//

void GT_Tagged_Attributes::initialized_and_changed (int tags)
{
    the_initialized |= tags;
    the_changed |= tags;
}


//////////////////////////////////////////
//
// print Helpers
//
//////////////////////////////////////////


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    const char* s, const char* def) const
{
    if (is_initialized(tag)) {
	if (graphlet->gml->version() < 2) {
	    GT_print (out, k, s);
	} else if (!is_from_parent(tag) &&
	    (the_parent != 0 || (strlen(s) > 0 && strcmp(s,def) != 0))) {
	    GT_print (out, k, s);
	}
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    const string &s, const string &def) const
{
    print_object(out, tag, k, s.c_str(), def.c_str());
}

    
void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    int i, int def) const
{
    if (is_initialized(tag)) {
	if (graphlet->gml->version() < 2) {
	    GT_print (out, k, i);
	} else if (!is_from_parent(tag) && (the_parent != 0 || i != def)) {
	    GT_print (out, k, i);
	}
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    bool b, bool def) const
{
    if (is_initialized(tag)) {
	if (graphlet->gml->version() < 2) {
	    GT_print (out, k, b);
	} else if (!is_from_parent(tag) && (the_parent != 0 || b != def)) {
	    GT_print (out, k, b);
	}
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    const double d, double def) const
{
    if (is_initialized(tag)) {
	if (graphlet->gml->version() < 2) {
	    GT_print (out, k, d);
	} else if (!is_from_parent(tag) && (the_parent != 0 || d != def)) {
	    GT_print (out, k, d);
	}
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    const GT_Key key, const GT_Key def) const
{

    if (is_initialized(tag) && key.active() && key != GT_Keys::undefined) {
	if (graphlet->gml->version() < 2) {
	    GT_print (out, k, key);
	} else if ((!is_from_parent(tag)) && (the_parent != 0 || key != def)) {
	    GT_print (out, k, key);
	}
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key /* k */,
    const GT_Point& p) const
{
    if (is_initialized(tag) &&
	(graphlet->gml->version() < 2 || !is_from_parent(tag))) {
	if (p.x() != 0.0) {
	    GT_print (out, GT_Keys::x, p.x());
	}
	if (p.y() != 0.0) {
	    GT_print (out, GT_Keys::y, p.y());
	}
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key /* k */,
    const GT_Rectangle& p) const
{
    if (is_initialized(tag) &&
	(graphlet->gml->version() < 2 || !is_from_parent(tag))) {
	if (p.x() != 0.0) {
	    GT_print (out, GT_Keys::x, p.x());
	}
	if (p.y() != 0.0) {
	    GT_print (out, GT_Keys::y, p.y());
	}
	if (p.w() != 0.0) {
	    GT_print (out, GT_Keys::h, p.w());
	}
	if (p.h() != 0.0) {
	    GT_print (out, GT_Keys::w, p.h());
	}
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    const GT_Polyline& l) const
{
    if (is_initialized(tag) &&
	(graphlet->gml->version() < 2 || !is_from_parent(tag))
	&& !l.empty()) 
    {
	GT_List_of_Attributes::print_list_head (out, k);
	for(GT_Polyline::const_iterator it = l.begin();
	    it != l.end(); ++it)
	{
	    GT_List_of_Attributes::print_list_head (out, GT_Keys::point);
	    GT_print (out, GT_Keys::x, it->x());
	    GT_print (out, GT_Keys::y, it->y());
	    GT_List_of_Attributes::print_list_tail (out);
	}
	GT_List_of_Attributes::print_list_tail (out);
    }
}


void GT_Tagged_Attributes::print_object (ostream& out, int tag, GT_Key k,
    const GT_Ports& ports) const
{
    if (is_initialized(tag) &&
	(graphlet->gml->version() < 2 || !is_from_parent(tag)) &&
	!ports.empty()) {

	GT_List_of_Attributes::print_list_head (out, k);

	GT_Ports::const_iterator it;
	for (it = ports.begin(); it != ports.end(); ++it) {
	    GT_List_of_Attributes::print_list_head (out, GT_Keys::port);
	    GT_print (out, GT_Keys::name, it->name());
	    GT_print (out, GT_Keys::x, it->x());
	    GT_print (out, GT_Keys::y, it->y());
	    GT_List_of_Attributes::print_list_tail (out);
	}

	GT_List_of_Attributes::print_list_tail (out);
    }
}


//////////////////////////////////////////
//
// print tests
//
//////////////////////////////////////////


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    const char* s, const char* def) const
{
    if (is_initialized(tag) && !is_from_parent(tag) &&
	(the_parent != 0 || (strlen(s) > 0 && strcmp(s,def) != 0))) {
	return true;
    } else {
	return false;
    }
}


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    int i, int def) const
{
    if (is_initialized(tag) && !is_from_parent(tag) && i != 0 &&
	(the_parent != 0 || i != def)) {
	return true;
    } else {
	return false;
    }
}


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    bool b, bool def) const
{
    if (is_initialized(tag) && !is_from_parent(tag) &&
	(the_parent != 0 || b != def)) {
	return true;
    } else {
	return false;
    }
}


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    const double d, double def) const
{
    if (is_initialized(tag) && !is_from_parent(tag) &&
	(the_parent != 0 || d != def)) {
	return true;
    } else {
	return false;
    }
}


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    const GT_Key key, const GT_Key def) const
{
    if (is_initialized(tag) && !is_from_parent(tag) &&
	(the_parent != 0 || key != def) &&
	key.active() && key != GT_Keys::undefined) {
	return true;
    } else {
	return false;
    }
}


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    const GT_Point& p) const
{
    if (is_initialized(tag) && !is_from_parent(tag) &&
	(p.x() != 0.0 || p.y() != 0.0)) {
	return true;
    } else {
	return false;
    }

}


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    const GT_Rectangle& p) const
{
    if (is_initialized(tag) && !is_from_parent(tag) &&
	(p.x() != 0.0 || p.y() != 0.0 || p.w() != 0.0 || p.h() != 0.0)) {
	return true;
    } else {
	return false;
    }
}


bool GT_Tagged_Attributes::print_test (int tag, GT_Key k,
    const GT_Polyline& l) const
{
    if (is_initialized(tag) && !is_from_parent(tag) && !l.empty()) {
	return true;
    } else {
	return false;
    }
}
