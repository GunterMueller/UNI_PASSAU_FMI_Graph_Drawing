/* This software is distributed under the Lesser General Public License */
//
// Rectangle.cpp
//
// This file implements the class GT_Rectangle
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Rectangle.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:49 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


#include "Graphlet.h"

#include "Rectangle.h"



//////////////////////////////////////////
//
// Rectangle utilities
//
//////////////////////////////////////////


GT_Rectangle::GT_Rectangle () : GT_Point (0,0)
{
    the_w = 0;
    the_h = 0;
}


GT_Rectangle::GT_Rectangle (const GT_Point& p, double w, double h) :
	GT_Point (p)
{
    the_w = w;
    the_h = h;
}


GT_Rectangle::GT_Rectangle (double x, double y, double w, double h) :
	GT_Point (x,y)
{
    the_w = w;
    the_h = h;
}


GT_Rectangle::~GT_Rectangle ()
{
}


//
// Accessories
//


void GT_Rectangle::w (double w)
{
    the_w = w;
}


void GT_Rectangle::h (double h)
{
    the_h = h;
}


//
// Compare, Output operators
//


bool GT_Rectangle::operator== (const GT_Rectangle& other) const
{
    return baseclass::operator== (*this) &&
	the_w == other.the_w &&
	the_h == other.the_h;
}


ostream& operator<< (ostream& out, const GT_Rectangle& p)
{
    return out << (GT_Rectangle::baseclass)p
	       << " w=" << p.w()
	       << " h=" << p.h();
}


//
// includes, expand, union_with, scale
//


bool GT_Rectangle::includes (const GT_Point& p) const
{
    double x_dist = fabs (p.x() - the_x);
    double y_dist = fabs (p.y() - the_y);

    return (x_dist <= the_w/2) && (y_dist < the_h/2);
}

void GT_Rectangle::expand (double x, double y)
{
    the_w += x;
    the_h += y;
}


void GT_Rectangle::union_with (const GT_Rectangle& rect)
{
    const double minx = min (left(),   rect.left());
    const double miny = min (top(),    rect.top());
    const double maxx = max (right(),  rect.right());
    const double maxy = max (bottom(), rect.bottom());
    
    *this = GT_Rectangle (
	(minx + maxx) / 2.0,
	(miny + maxy) / 2.0,
	maxx - minx,
	maxy - miny);
}


void GT_Rectangle::scale (double by, const GT_Point& origin)
{
    baseclass::scale (by, origin);
    the_w *= by;
    the_h *= by;
}



//
// The following procedures are modelled after Tcl/Tk anchor
// positions.
//

GT_Point GT_Rectangle::anchor_c() const
{
    return GT_Point (*this);
}


GT_Point GT_Rectangle::anchor_n() const
{
    return GT_Point (x(), y() + h() / 2);
}


GT_Point GT_Rectangle::anchor_ne() const
{
    return GT_Point (x() - w() / 2, y() + h() / 2);
}


GT_Point GT_Rectangle::anchor_e() const
{
    return GT_Point (x() - w() / 2, y());
}


GT_Point GT_Rectangle::anchor_se() const
{
    return GT_Point (x() - w() / 2, y() - h() / 2);
}


GT_Point GT_Rectangle::anchor_s() const
{
    return GT_Point (x(), y() - h() / 2);
}


GT_Point GT_Rectangle::anchor_sw() const
{
    return GT_Point (x() + w() / 2, y() - h() / 2);
}


GT_Point GT_Rectangle::anchor_w() const
{
    return GT_Point (x() + w() / 2, y());
}


GT_Point GT_Rectangle::anchor_nw() const
{
    return GT_Point (x() + w() / 2, y() + h() / 2);
}
