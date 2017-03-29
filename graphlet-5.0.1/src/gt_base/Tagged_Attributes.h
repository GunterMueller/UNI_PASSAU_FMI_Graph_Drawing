/* This software is distributed under the Lesser General Public License */
#ifndef TAGGED_ATTRIBUTES_H
#define TAGGED_ATTRIBUTES_H

//
// Tagged_Attributes.h
//
// This file defines the class GT_Tagged_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Tagged_Attributes.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:45:01 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


//////////////////////////////////////////
//
// class GT_Tagged_Attributes
//
//////////////////////////////////////////


template<class T, class V>
void set_tagged_attribute (T* object, int tag, V T::* value)
{
    object->set_changed (tag);
    object->set_initialized (tag);

    if (object->parent() != 0 &&
	(object->*value) == (((T*)object->parent())->*value)) {
	object->set_from_parent (tag);
    } else {
	object->reset_from_parent (tag);
    }
}


class GT_Point;
class GT_Rectangle;
class GT_Polyline;
class GT_Ports;

class GT_Tagged_Attributes : public GT_List_of_Attributes {

    typedef GT_List_of_Attributes baseclass;

protected:	
    int the_initialized;
    int the_changed;
    int the_from_parent;

    GT_Tagged_Attributes* the_parent;



public:
	
    //
    // Constructor & Destructor
    //

    GT_Tagged_Attributes();    
    virtual ~GT_Tagged_Attributes();

    //
    // Accessories
    //

    inline const GT_Tagged_Attributes* parent() const;
    inline GT_Tagged_Attributes* parent();
    virtual void parent (GT_Tagged_Attributes*);

    // Copy

    void copy (const GT_Tagged_Attributes* from, GT_Copy type);
    virtual bool copy_test (const GT_Tagged_Attributes* from, int tag,
	GT_Copy type) const;
    
    //
    // Initialized
    //
	
    virtual void set_initialized (const int tag) {
	// assert (is_initialized(tag));
	the_initialized |= tag;
    }

    bool is_initialized (const int tag) const {
	return (the_initialized & tag) != 0;
    }

    void reset_initialized (const int tag) {
	the_initialized &= ~tag;
    }

    int initialized() const {
	return the_initialized;
    }

    bool nothing_initialized () const {
	return the_initialized == 0;
    }
    
    
    //
    // Changed
    //
	
    virtual void set_changed (const int tag) {
	// assert (is_initialized(tag));
	the_changed |= tag;
    }

    bool is_changed (const int tag) const {
	return (the_changed & tag) != 0;
    }

    void reset_changed (const int tag) {
	the_changed &= ~tag;
    }

    int changed() const {
	return the_changed;
    }

    bool nothing_changed () const {
	return the_changed == 0;
    }

    
    //
    // From_Parent
    //
	
    virtual void set_from_parent (const int tag) {
	// assert (is_initialized(tag));
	the_from_parent |= tag;
    }

    bool is_from_parent (const int tag) const {
	return (the_from_parent & tag) != 0;
    }

    void reset_from_parent (const int tag) {
	the_from_parent &= ~tag;
    }	

    int from_parent() const {
	return the_from_parent;
    }

    bool nothing_from_parent () const {
	return the_from_parent == 0;
    }

    void all_changed (int begin, int end);

    //
    // Printing Optimization
    //
    
    virtual bool do_print () const;

    virtual void print_object (ostream& out, int tag, GT_Key k,
	const char* s, const char* def = "") const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	const string &s, const string &def = "") const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	int i, int def = 0) const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	bool b, bool def = false) const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	const double d, double def = 0.0) const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	const GT_Key key,
	const GT_Key def = GT_Keys::undefined) const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	const GT_Point& p) const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	const GT_Polyline& l) const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	const GT_Rectangle& r) const;
    virtual void print_object (ostream& out, int tag, GT_Key k,
	const GT_Ports& p) const;

    virtual bool print_test (int tag, GT_Key k, const char* s,
	const char* def = "") const;
    virtual bool print_test (int tag, GT_Key k, int i,
	int def = 0) const;
    virtual bool print_test (int tag, GT_Key k, bool b,
	bool def = false) const;
    virtual bool print_test (int tag, GT_Key k, const double d,
	double def = 0.0) const;
    virtual bool print_test (int tag, GT_Key k,
	const GT_Key key,
	const GT_Key def = GT_Keys::undefined) const;
    virtual bool print_test (int tag, GT_Key k,
	const GT_Point& p) const;
    virtual bool print_test (int tag, GT_Key k,
	const GT_Polyline& l) const;
    virtual bool print_test (int tag, GT_Key k,
	const GT_Rectangle& r) const;

    //
    // Initialization
    //

    void initialized_and_changed (int tags);
};



//
// Accessories
//

inline const GT_Tagged_Attributes* GT_Tagged_Attributes::parent() const
{
    return the_parent;
}


inline GT_Tagged_Attributes* GT_Tagged_Attributes::parent()
{
    return the_parent;
}


#endif
