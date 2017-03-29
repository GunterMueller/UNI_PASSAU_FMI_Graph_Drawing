/* This software is distributed under the Lesser General Public License */
#ifndef GT_POINT_H
#define GT_POINT_H

//
// Point.h
//
// This file defines the class GT_Point
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Point.h,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:44:37 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

//////////////////////////////////////////
//
// GT_Point
//
//////////////////////////////////////////

#include <iostream>

class GT_Point {

protected:
    double the_x;
    double the_y;

public:

    //
    // Constructor & Destructor
    //
    
    inline GT_Point ();
    inline GT_Point (const double x, const double y);
    inline GT_Point (const GT_Point& p);
    inline virtual ~GT_Point ();

    //
    // Accessors
    //
	
    void x (double x);
    void y (double y);

    inline double x () const;
    inline double y () const;

    //
    // Utilities
    //

    void move (const GT_Point& move_xy);
    virtual void scale (double by,
	const GT_Point& origin = GT_Point (0.0,0.0));
    virtual void scale (double by_x, double by_y,
	const GT_Point& origin = GT_Point (0.0,0.0));
    virtual void rotate (const GT_Point &center, double angle);
    double distance(const GT_Point &) const;
    
    friend ostream& operator<< (ostream& out, const GT_Point& p);
    friend istream& operator>> (istream& in, const GT_Point& p);

    //
    // Operators
    //

    bool operator== (const GT_Point& p) const;
    friend GT_Point operator-(const GT_Point &p1, const GT_Point &p2); 
};

int orientation (GT_Point a, GT_Point b, GT_Point c);

//
// Inline Constructor & Destructor
//

inline GT_Point::GT_Point () :
	the_x (0.0), the_y (0.0)
{
};


inline GT_Point::GT_Point (const double x, const double y) :
	the_x (x), the_y (y)
{
}


inline GT_Point::GT_Point (const GT_Point& p) :
	the_x (p.x()), the_y (p.y())
{
}

inline GT_Point::~GT_Point ()
{
}


//
// Accessories
//


inline double GT_Point::x () const {
    return the_x;
}


inline double GT_Point::y () const {
    return the_y;
}

#endif

