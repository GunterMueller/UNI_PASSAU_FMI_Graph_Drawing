/* This software is distributed under the Lesser General Public License */
#ifndef COMMON_ATTRIBUTES_H
#define COMMON_ATTRIBUTES_H

//
// Common_Attributes.h
//
// This file defines the class GT_Common_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Common_Attributes.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:43:18 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


//////////////////////////////////////////
//
// class GT_Common_Attributes
//
//////////////////////////////////////////

class GT_Common_Attributes : public GT_Tagged_Attributes {
	
    typedef GT_Tagged_Attributes baseclass;

    string the_label;
    string the_name;
    GT_Key the_label_anchor;
    int the_visible;

public:

    // Constructor & Destructor

    GT_Common_Attributes();
    virtual ~GT_Common_Attributes();

    // Accessories

    virtual int id () const = 0; // redefined in node, edge, graph attributes

    inline const string& label () const;
    virtual void label (const string& s);
    inline const string& name () const;
    virtual void name (const string& s);

    inline GT_Key label_anchor () const;
    virtual void label_anchor (GT_Key anchor);

    inline int visible () const;
    virtual void visible (int v);

    // Extract & Print

    virtual int extract (GT_List_of_Attributes* list, string& message);
    virtual void print (ostream& out) const;

    // Virtual Copy Constructor

    void copy (const GT_Common_Attributes* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const = 0;

    // Tags

    enum {
	tag_label = 1,
	tag_name = (tag_label<<1),
	tag_label_anchor = (tag_name<<1),
	tag_visible = (tag_label_anchor<<1),
	common_attributes_tag_max = tag_label_anchor
    };

    //
    // Printing Optimization
    //
    
    virtual bool do_print () const;

};


//
// Accessories
//


inline const string& GT_Common_Attributes::label () const
{
    return the_label;
}

inline const string& GT_Common_Attributes::name () const
{
    return the_name;
}

inline GT_Key GT_Common_Attributes::label_anchor () const
{
    return the_label_anchor;
}

inline int GT_Common_Attributes::visible () const
{
    return the_visible;
}


#endif
