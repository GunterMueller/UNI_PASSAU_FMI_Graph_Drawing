/* This software is distributed under the Lesser General Public License */
#ifndef EDGE_NEI_H
#define EDGE_NEI_H
#define edge_NEI_h

// ---------------------------------------------------------------------
// edge_NEI.h
// 
// Classes for the Node-Edge-Interface
// ---------------------------------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/edge_NEI.h,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:45:08 $
// $Locker:  $
// $State: Exp $
// ----------------------------------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project, Walter Bachl
//

// -----------------------------------------------------------------------
// Edge_NEI
// Each Edge has one corresponding Edge_NEI.
//
// members:
//
// functions:
// -----------------------------------------------------------------------

#include <gt_base/Segment.h>

class GT_Edge_Attributes;
class GT_Node_Attributes;

class GT_Edge_NEI : public GT_Tagged_Attributes
{
    typedef GT_Tagged_Attributes baseclass;

    GT_Edge_Attributes* the_edge_attrs;

    GT_Key the_source_function;
    GT_Key the_target_function;
	
    double the_delta_x_source;
    double the_delta_y_source;
    double the_delta_x_target;
    double the_delta_y_target;

private:
    
    // private functions to have pretty user-functions
    inline void set_sEA_fast (GT_Point delta) {
	delta_x_source (delta.x());
	delta_y_source (delta.y());
    }
    inline void set_tEA_fast (GT_Point delta) {
	delta_x_target (delta.x());
	delta_y_target (delta.y());
    }

    const GT_Node_Attributes& source_attributes() const;
    const GT_Node_Attributes& target_attributes() const;

    
    void compute_min (const vector<GT_Point>& points1, GT_Point& min1, int p1_high,
	const vector<GT_Point>& points2, GT_Point& min2, int p2_high);

    // update edgeanchor each redraw
    void get_corners (const GT_Node_Attributes &attrs, vector<GT_Point> &points);
    void get_middles (const GT_Node_Attributes &attrs, vector<GT_Point> &points);
    void source_points (vector<GT_Point> &points, int &p1_high);
    void target_points (vector<GT_Point> &points, int &p2_high);
    // Special function if one default function is orthogonal
    void orthogonal ();
    
    // clipp the edge
	 
    virtual GT_Point clip_edge_at_polygon (int where);
    virtual GT_Point clip_edge_at_arc (int where);
    virtual GT_Point clip_edge_at_oval (int where, bool way); 
    virtual GT_Point clip_edge_at_line (int where);
    virtual GT_Point clip_edge_at_oval (int where);
    virtual GT_Point clip_edge_at_rectangle (int where);
    GT_Point intersection (GT_Polyline &node, GT_Segment &line);

    // Change anchor by applying a function.
    virtual int EA_apply_function (string function, int where);
    virtual int EA_apply_function (GT_Key function, int where);

public:
    
    // Construktor and Destructor
    GT_Edge_NEI ();
    virtual ~GT_Edge_NEI ();

    // Accessors
    inline const GT_Edge_Attributes* edge_attrs() const;
    virtual void edge_attrs (GT_Edge_Attributes*);

    inline GT_Key source_function() const;
    virtual void source_function (GT_Key);
    inline GT_Key target_function() const;
    virtual void target_function (GT_Key);
	
    inline double delta_x_source() const;
    virtual void delta_x_source (double);
    inline double delta_y_source() const;
    virtual void delta_y_source (double);
    inline double delta_x_target() const;
    virtual void delta_x_target (double);
    inline double delta_y_target() const;
    virtual void delta_y_target (double);

    // Secure functions to set delta x and y (Range check)
    int d_x_source (double d_x);
    int d_y_source (double d_y);
    int d_x_target (double d_x);
    int d_y_target (double d_y);

    // virtual copy constructor
    void copy (const GT_Edge_NEI* from, GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    virtual void update_from_parent (GT_Copy type);

    // Function to get attributes after a graph is read from a file.
    int extract (GT_List_of_Attributes* current_list, string& /* message */);
    // Function to output attributes 
    void print (ostream& out) const;
    virtual bool do_print () const;    

    // Functions to set the anchors
    virtual int set_EA (int where, double delta_x, double delta_y); 
    virtual int set_EA (int where, GT_Key direction);
    virtual int set_EA (GT_Key src_key, GT_Key trg_key);
    
    inline void set_sEA_fast (double delta_x, double delta_y) {
	delta_x_source (delta_x);
	delta_y_source (delta_y);
    }
	
    inline void set_tEA_fast (double delta_x, double delta_y) {
	delta_x_target (delta_x);
	delta_y_target (delta_y);
    }
    
    // Set default function
    int set_EA_default_function (GT_Key function, int where);
    int set_EA_default_function (string function, int where);
    
    // help-functions for all 'jumping' functions
    virtual int set_EA_connect_orthogonal ();
    GT_Point get_bend_outside (int where);
    GT_Point get_bend_inside (int where);
    virtual int set_EA_next_corner (int where);
    virtual int set_EA_next_middle (int where);
    virtual int set_EA_next_corner_orthogonal (int where);
    virtual int set_EA_orthogonal (int where);

    // Functions to get some information
    GT_Point get_clip_point (int where);
    GT_Point get_EA (int where) const;
    double get_EA_x (int where) const;
    double get_EA_y (int where) const;
    GT_Key get_EA_default_function (int where) const;

    // conversion between anchor and coordinates
    virtual GT_Point convert_anchor_to_coordinates (int where);
    virtual GT_Point coordinates_to_anchor (int where, GT_Point coordinates);

    // update edgeanchor each redraw
    int update_edgeanchor ();

   // Get the clipped point of the edge
    virtual GT_Point clip_edge (int where);    

    // new functions
    int get_side (int where);
    
    // enumeration for tagged variables
    enum {

	tag_source_function = 1,
	tag_target_function = tag_source_function << 1,
	
	tag_delta_x_source = tag_target_function << 1,
	tag_delta_y_source = tag_delta_x_source << 1,
	tag_delta_x_target = tag_delta_y_source << 1,
	tag_delta_y_target = tag_delta_x_target << 1,
    
	tag_edge_nei_max = tag_delta_y_target << 1,
    };

    enum {
	tag_edge_nei =
	tag_source_function |
	tag_target_function |
	tag_delta_x_source |
	tag_delta_y_source |
	tag_delta_x_target |
	tag_delta_y_target
    };
	
	
};


//
// Accessors
//


inline const GT_Edge_Attributes* GT_Edge_NEI::edge_attrs() const
{
    return the_edge_attrs;
}


inline GT_Key GT_Edge_NEI::source_function() const
{
    return the_source_function;
}


inline GT_Key GT_Edge_NEI::target_function() const
{
    return the_target_function;
}

	
inline double GT_Edge_NEI::delta_x_source() const
{
    return the_delta_x_source;
}


inline double GT_Edge_NEI::delta_y_source() const
{
    return the_delta_y_source;
}


inline double GT_Edge_NEI::delta_x_target() const
{
    return the_delta_x_target;
}


inline double GT_Edge_NEI::delta_y_target() const
{
    return the_delta_y_target;
}


#endif
