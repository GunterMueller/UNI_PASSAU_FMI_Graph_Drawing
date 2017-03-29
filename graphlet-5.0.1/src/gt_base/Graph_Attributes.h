/* This software is distributed under the Lesser General Public License */
#ifndef GT_GRAPH_ATTRIBUTES_H
#define GT_GRAPH_ATTRIBUTES_H

//
// Graph_Attributes.h
//
// This module defines the class GT_Graph_Attributes.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Graph_Attributes.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:44 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//



class GT_Graph;


//////////////////////////////////////////
//
// GT_Graph_Graphics
//
//////////////////////////////////////////


class GT_Graph_Graphics : public GT_Common_Graphics {

    typedef GT_Common_Graphics baseclass;

public:
    
    GT_Graph_Graphics ();
    virtual ~GT_Graph_Graphics ();

    // virtual copy constructor
    void copy (const GT_Graph_Graphics* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);
};


//////////////////////////////////////////
//
// GT_Graph_Label_Graphics
//
//////////////////////////////////////////


class GT_Graph_Label_Graphics : public GT_Common_Graphics {

    typedef GT_Common_Graphics baseclass;

public:
    
    GT_Graph_Label_Graphics ();
    virtual ~GT_Graph_Label_Graphics ();

    // virtual copy constructor
    void copy (const GT_Graph_Label_Graphics* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);
};



//////////////////////////////////////////
//
// class GT_Graph_Attributes
//
//////////////////////////////////////////



class GT_Graph_Attributes : public GT_Common_Attributes {
	
    typedef GT_Common_Attributes baseclass;

    int the_id;

    GT_Graph* the_g;
    string the_creator;

    GT_Graph_Graphics* the_graphics;
    GT_Graph_Label_Graphics* the_label_graphics;
    
public:

    GT_Graph_Attributes ();
    virtual ~GT_Graph_Attributes();

    // Accessories

    virtual int id () const;
    virtual void id (int i);

    inline const GT_Graph* g () const;
    virtual void g (GT_Graph*);

    inline const string& creator () const;
    virtual void creator (const string&);

    inline const GT_Graph_Graphics* graphics() const;
    inline GT_Graph_Graphics* graphics();
    virtual void graphics (GT_Graph_Graphics*);
    inline const GT_Graph_Label_Graphics* label_graphics() const;
    inline GT_Graph_Label_Graphics* label_graphics();
    virtual void label_graphics (GT_Graph_Label_Graphics*);

    //
    // Virtual Copy Constructor
    //

    void copy (const GT_Graph_Attributes* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);

    //
    // Extract & Print
    //

    virtual int extract (GT_List_of_Attributes* list, string& message);
    virtual void print (ostream& out) const;

    //
    // Tags
    //

    enum {
	graph_attributes_tag_max = common_attributes_tag_max
    };
};



//
// Accessories
//


inline const GT_Graph* GT_Graph_Attributes::g () const
{
    return the_g;
}


inline const string& GT_Graph_Attributes::creator () const
{
    return the_creator;
}


inline const GT_Graph_Graphics* GT_Graph_Attributes::graphics() const
{
    return the_graphics;
}


inline GT_Graph_Graphics* GT_Graph_Attributes::graphics()
{
    return the_graphics;
}


inline const GT_Graph_Label_Graphics* GT_Graph_Attributes::label_graphics()
    const
{
    return the_label_graphics;
}


inline GT_Graph_Label_Graphics* GT_Graph_Attributes::label_graphics()
{
    return the_label_graphics;
}


#endif
