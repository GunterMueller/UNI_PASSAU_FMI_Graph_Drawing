/* This software is distributed under the Lesser General Public License */
#ifndef NODE_NEI_H
#define NODE_NEI_H 
#define node_NEI_h 

// ---------------------------------------------------------------------
// NEI.h
// 
// Classes for the Node-Edge-Interface
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/node_NEI.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:15 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project, Walter Bachl
//

// ---------------------------------------------------------------------------
// ---------------------------------------------------------------------------


class GT_Node_NEI : public GT_Tagged_Attributes
{
    typedef GT_Tagged_Attributes baseclass;

private:
    GT_Node_Attributes* the_node_attrs;
    GT_Key the_default_function;
    
    // functions which are not ready 
    int distribute_edges_uniform (int side);
    int distribute_edges_uniform ();
    int distribute_edges_uniform_in_sectors (int side);
    int distribute_edges_uniform_in_sectors ();

public:

    // Construktor and Destructor
    GT_Node_NEI ();
    virtual ~GT_Node_NEI() {};

    // Accessors
    inline const GT_Node_Attributes* node_attrs () const;
    virtual void node_attrs (GT_Node_Attributes*);
    inline GT_Key default_function () const;
    virtual void default_function (GT_Key);

    // virtual copy constructor
    void copy (const GT_Node_NEI* from, GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);
	
    // Function to get attributes after a graph is read from a file.
    int extract (GT_List_of_Attributes* current_list, string& /* message */);

    // Function to output attributes 
    void print (ostream& out) const;
    virtual bool do_print () const;

    // Function to update the edgeanchor of adjacent edges
    // virtual int update ();
    
    // Apply the default function
    // virtual int EA_default_function ();
    // Apply the default_function only to edge e
    // virtual int EA_default_function (edge e, int where);
    
    
    int set_EA_default_function (GT_Key function);
    int set_EA_default_function (string function);
    GT_Key get_EA_default_function ();
    
    // Apply function to all adjacent edges
    // virtual int EA_apply_function (string function);
    // virtual int EA_apply_function (GT_Key function);
    
    // Functions to change the edge-anchors
    virtual int alledges_set_EA (double delta_x, double delta_y);
    virtual int alledges_set_EA (GT_Key direction);
    virtual int alledges_set_EA (GT_Key src_key, GT_Key trg_key);
    
     // virtual int alledges_set_EA_center ();
    // virtual int alledges_set_EA_direction (GT_Key direction);
    // virtual int alledges_set_EA_next_corner ();
    // virtual int alledges_set_EA_next_middle ();
    // virtual int alledges_set_EA_orthogonal ();
    

    // enumeration for tagged variables
    enum {
	tag_node_nei = 1, // global
	tag_default_function = tag_node_nei,
	tag_node_NEI_max = tag_default_function
    };
 
};



//
// Accessors
//


inline const GT_Node_Attributes* GT_Node_NEI::node_attrs () const
{
    return the_node_attrs;
}


inline GT_Key GT_Node_NEI::default_function () const
{
    return the_default_function;
}

#endif
