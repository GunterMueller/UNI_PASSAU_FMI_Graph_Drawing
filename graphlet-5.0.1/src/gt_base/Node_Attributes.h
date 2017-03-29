/* This software is distributed under the Lesser General Public License */
#ifndef GT_NODE_ATTRIBUTES_H
#define GT_NODE_ATTRIBUTES_H

//
// Node_Attributes.h
//
// This file defines the class GT_Node_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Node_Attributes.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

class GT_Node_NEI;

#ifndef GT_PORTS_H
#include "Ports.h"
#endif

//////////////////////////////////////////
//
// class GT_Node_Graphics
//
//////////////////////////////////////////


class GT_Node_Graphics : public GT_Common_Graphics {

    typedef GT_Common_Graphics baseclass;

public:
    
    GT_Node_Graphics ();
    virtual ~GT_Node_Graphics ();

    // Virtual Copy Constructor
    void copy (const GT_Node_Graphics* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);

    virtual void print_geometry (ostream& out) const;
    virtual void print_type (ostream& out) const;
    virtual bool do_print () const;
};



//////////////////////////////////////////
//
// class GT_Node_Label_Graphics
//
//////////////////////////////////////////


class GT_Node_Label_Graphics : public GT_Common_Graphics {

    typedef GT_Common_Graphics baseclass;

public:
    
    GT_Node_Label_Graphics ();
    virtual ~GT_Node_Label_Graphics ();

    // virtual copy constructor
    void copy (const GT_Node_Label_Graphics* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);
    
    virtual void print_type (ostream& out) const;
};



//////////////////////////////////////////
//
// class GT_Node_Attributes
//
//////////////////////////////////////////


class GT_Graph;

class GT_Node_Attributes : public GT_Common_Attributes {

    typedef GT_Common_Attributes baseclass;

    int the_id;
    GT_Graph* the_g;
    node the_n;

    GT_Ports the_ports;

    GT_Node_Graphics* the_graphics;
    GT_Node_Label_Graphics* the_label_graphics;
    GT_Node_NEI* the_node_nei;
    
public:

    // Constructor & Destructor

    GT_Node_Attributes ();
    virtual ~GT_Node_Attributes();

    virtual int id () const;
    virtual void id (int i);

    inline GT_Graph* g ();
    inline const GT_Graph* g () const;
    virtual void g (GT_Graph* g);

    inline node n () const;
    virtual void n (node n);

    inline const GT_Ports& ports () const;
    virtual void ports (const GT_Ports& new_ports);

    inline GT_Node_Graphics* graphics();
    inline const GT_Node_Graphics* graphics() const;
    virtual void graphics (GT_Node_Graphics* graphics);

    inline GT_Node_Label_Graphics* label_graphics();
    inline const GT_Node_Label_Graphics* label_graphics() const;
    virtual void label_graphics (GT_Node_Label_Graphics* label_graphics);

    inline GT_Node_NEI* node_nei ();
    inline const GT_Node_NEI* node_nei () const;
    virtual void node_nei (GT_Node_NEI* node_nei);

    // Virtual Copy Constructor

    void copy (const GT_Node_Attributes* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);

    // Extract & Print

    virtual int extract (GT_List_of_Attributes* list, string& message);
    virtual void print (ostream& out) const;

    // Tags

    enum {
	tag_ports = common_attributes_tag_max<<1,
	node_attributes_tag_max = tag_ports
    };
};


//
// Accessories
//


inline GT_Graph* GT_Node_Attributes::g ()
{
    return the_g;
}


inline const GT_Graph* GT_Node_Attributes::g () const
{
    return the_g;
}



inline node GT_Node_Attributes::n () const
{
    return the_n;
}


inline const GT_Ports& GT_Node_Attributes::ports () const
{
    return the_ports;
}



inline GT_Node_Graphics* GT_Node_Attributes::graphics()
{
    return the_graphics;
}


inline const GT_Node_Graphics* GT_Node_Attributes::graphics() const
{
    return the_graphics;
}



inline GT_Node_Label_Graphics* GT_Node_Attributes::label_graphics()
{
    return the_label_graphics;
}


inline const GT_Node_Label_Graphics* GT_Node_Attributes::label_graphics() const
{
    return the_label_graphics;
}



inline GT_Node_NEI* GT_Node_Attributes::node_nei ()
{
    return the_node_nei;
}


inline const GT_Node_NEI* GT_Node_Attributes::node_nei () const
{
    return the_node_nei;
}



#endif
