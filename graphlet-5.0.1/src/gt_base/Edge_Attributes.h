/* This software is distributed under the Lesser General Public License */
#ifndef GT_EDGE_ATTRIBUTES_H
#define GT_EDGE_ATTRIBUTES_H

//
// Edge_Attributes.h
//
// This file implements the class GT_Edge_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Edge_Attributes.h,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/06/08 13:53:06 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

class GT_Edge_NEI;


//////////////////////////////////////////
//
// class GT_Edge_Graphics
//
//////////////////////////////////////////

class GT_Edge_Graphics : public GT_Common_Graphics {

    typedef GT_Common_Graphics baseclass;
    
public:
    
    GT_Edge_Graphics ();
    virtual ~GT_Edge_Graphics ();

    // virtual copy constructor
    void copy (const GT_Edge_Graphics* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);

    virtual void print_geometry (ostream& out) const;
    virtual void print_type (ostream& out) const;
    virtual bool do_print () const;
};


//////////////////////////////////////////
//
// class GT_Edge_Label_Graphics
//
//////////////////////////////////////////

class GT_Edge_Label_Graphics : public GT_Common_Graphics {

    typedef GT_Common_Graphics baseclass;
    
public:
    
    GT_Edge_Label_Graphics ();
    virtual ~GT_Edge_Label_Graphics ();

    // virtual copy constructor
    void copy (const GT_Edge_Label_Graphics* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);

    virtual void print_type (ostream& out) const;
};



//////////////////////////////////////////
//
// class GT_Edge_Attributes
//
//////////////////////////////////////////

class GT_Graph;

class GT_Edge_Attributes : public GT_Common_Attributes {
	
    typedef GT_Common_Attributes baseclass;

    int the_id;
    GT_Graph* the_g;
    edge the_e;

    GT_Key the_source_port;
    GT_Key the_target_port;

    GT_Edge_Graphics* the_graphics;
    GT_Edge_Label_Graphics* the_label_graphics;
    GT_Edge_NEI* the_edge_nei;

    int the_label_anchor_bend;
    double the_label_anchor_x;
    double the_label_anchor_y;

public:

    // Constructor & Destructor

    GT_Edge_Attributes ();
    virtual ~GT_Edge_Attributes();
	
    // Accessors

    virtual int id () const;
    virtual void id (int i);

    inline GT_Graph* g();
    inline const GT_Graph* g() const;
    virtual void g (GT_Graph*);

    inline edge e() const;
    virtual void e (edge);

    inline GT_Key source_port () const;
    virtual void source_port (GT_Key new_name);

    inline GT_Key target_port () const;
    virtual void target_port (GT_Key new_name);

    inline GT_Edge_Graphics* graphics();
    inline const GT_Edge_Graphics* graphics() const;
    virtual void graphics (GT_Edge_Graphics* graphics);
    
    inline GT_Edge_Label_Graphics* label_graphics();
    inline const GT_Edge_Label_Graphics* label_graphics() const;
    virtual void label_graphics (GT_Edge_Label_Graphics* label_graphics);

    inline GT_Edge_NEI* edge_nei();
    inline const GT_Edge_NEI* edge_nei() const;
    virtual void edge_nei (GT_Edge_NEI* edge_nei);

    inline int label_anchor_bend() const;
    virtual void label_anchor_bend (int bend);

    inline double label_anchor_x() const;
    virtual void label_anchor_x (double x);

    inline double label_anchor_y() const;
    virtual void label_anchor_y (double y);

    // Virtual Copy Constructor

    void copy (const GT_Edge_Attributes* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);

    // Extract & Print

    virtual int extract (GT_List_of_Attributes* list, string& message);
    virtual void print (ostream& out) const;

    // Tags

    enum {
	tag_source_port = common_attributes_tag_max << 1,
	tag_target_port = tag_source_port << 1,
	tag_label_anchor_bend = tag_target_port << 1,
	tag_label_anchor_x = tag_label_anchor_bend << 1,
	tag_label_anchor_y = tag_label_anchor_x << 1,
	edge_attributes_tag_max = tag_label_anchor_y
    };

    node source () const;
    node target () const;
};



//////////////////////////////////////////
//
// Accessories
//
//////////////////////////////////////////


inline GT_Graph* GT_Edge_Attributes::g()
{
    return the_g;
}


inline const GT_Graph* GT_Edge_Attributes::g() const
{
    return the_g;
}


inline edge GT_Edge_Attributes::e() const
{
    return the_e;
}


inline GT_Key GT_Edge_Attributes::source_port () const
{
    return the_source_port;
}


inline GT_Key GT_Edge_Attributes::target_port () const
{
    return the_target_port;
}



inline GT_Edge_Graphics* GT_Edge_Attributes::graphics()
{
    return the_graphics;
}


inline const GT_Edge_Graphics* GT_Edge_Attributes::graphics() const
{
    return the_graphics;
}


    
inline GT_Edge_Label_Graphics* GT_Edge_Attributes::label_graphics()
{
    return the_label_graphics;
}


inline const GT_Edge_Label_Graphics* GT_Edge_Attributes::label_graphics() const
{
    return the_label_graphics;
}



inline GT_Edge_NEI* GT_Edge_Attributes::edge_nei()
{
    return the_edge_nei;
}


inline const GT_Edge_NEI* GT_Edge_Attributes::edge_nei() const
{
    return the_edge_nei;
}


inline int GT_Edge_Attributes::label_anchor_bend() const
{
    return the_label_anchor_bend;
}


inline double GT_Edge_Attributes::label_anchor_x() const
{
    return the_label_anchor_x;
}


inline double GT_Edge_Attributes::label_anchor_y() const
{
    return the_label_anchor_y;
}


#endif
