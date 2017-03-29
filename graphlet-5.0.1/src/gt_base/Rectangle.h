/* This software is distributed under the Lesser General Public License */
#ifndef GT_RECTANGLE_H
#define GT_RECTANGLE_H

//
// Rectangle.h
//
// This file defines the class GT_Rectangle
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Rectangle.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:50 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


#ifndef GT_POINT_H
#include "Point.h"
#endif



//////////////////////////////////////////
//
// class GT_Rectangle: a GT_Point at the center + width and height
//
//////////////////////////////////////////

class GT_Rectangle : public GT_Point {

    GT_CLASS (GT_Rectangle, GT_Point);
    
    double the_w;
    double the_h;
	
public:

    GT_Rectangle ();
    GT_Rectangle (const GT_Point& p, double w, double h);
    GT_Rectangle (double x, double y, double w, double h);
    virtual ~GT_Rectangle ();

    inline double w() const;
    virtual void w (double);
    inline double h() const;
    virtual void h (double);

    bool includes (const GT_Point& p) const;
    void expand (double x, double y);
    void union_with (const GT_Rectangle& rect);
    
    // move is the same as for GT_Point
    virtual void scale (double by, const GT_Point& origin = GT_Point (0.0,0.0));

    //
    // Tk anchor conformant positions
    //
	
    GT_Point anchor_c() const;
    GT_Point anchor_n() const;
    GT_Point anchor_ne() const;
    GT_Point anchor_e() const;
    GT_Point anchor_se() const;
    GT_Point anchor_s() const;
    GT_Point anchor_sw() const;
    GT_Point anchor_w() const;
    GT_Point anchor_nw() const;

    //
    // max/min coordinates (note: NOT Tcl/Tk anchor conformant)
    //
	
    inline double top() const;	
    inline double right() const;
    inline double bottom() const;
    inline double left() const;

    //
    // Operators
    //

    bool operator== (const GT_Rectangle& p) const;
    friend ostream& operator<< (ostream& out, const GT_Rectangle& p);
};

ostream& operator<< (ostream& out, const GT_Rectangle& p);


//
// Accessories
//


inline double GT_Rectangle::w () const
{
    return the_w;
}


inline double GT_Rectangle::h () const
{
    return the_h;
}
	

double GT_Rectangle::top() const
{
    return the_y - the_h / 2;
}

	
double GT_Rectangle::right() const
{
    return the_x +  the_w / 2;
}


double GT_Rectangle::bottom() const
{
    return the_y + the_h / 2;
}


double GT_Rectangle::left() const
{
    return the_x - the_w / 2;
}


#endif
