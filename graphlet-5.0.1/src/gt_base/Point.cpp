/* This software is distributed under the Lesser General Public License */
//
// Point.cpp
//
// This file implements the class GT_Point.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Point.cpp,v $
// $Author: himsolt $
// $Revision: 1.4 $
// $Date: 1999/03/05 20:44:35 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


#include "Graphlet.h"

#include "Point.h"



//////////////////////////////////////////
//
// GT_Point
//
//////////////////////////////////////////


//
// Accessories
//


void GT_Point::x (double x) {
    the_x = x;
};


void GT_Point::y (double y) {
    the_y = y;
};


//
// Operators: compare, input, output
//


bool GT_Point::operator== (const GT_Point& other) const
{
    return the_x == other.the_x && the_y == other.the_y;
}

GT_Point operator-(const GT_Point &p1, const GT_Point &p2)
{
    return GT_Point(p1.the_x - p2.the_x, p1.the_y - p2.the_y);
}
     

ostream& operator<< (ostream& out, const GT_Point& p)
{
    return out << "x=" << p.x() << " y= " << p.y();
}


istream& operator>> (istream& in, const GT_Point& /* p */)
{
    // DUMMY
    return in;
}


//
// Move & Scale & Rotate
//


void GT_Point::move (const GT_Point& move_xy)
{
    the_x += move_xy.x();
    the_y += move_xy.y();
}

void GT_Point::rotate (const GT_Point& center, double angle)
{
    double dx = the_x - center.x();
    double dy = the_y - center.y();
	
    double length = sqrt(dx*dx + dy*dy);

    double new_angle = angle;
    if(dy == 0)
    {
	new_angle += (dx > 0) ? M_PI/2 : -M_PI/2;
    }
    else 
    {
	new_angle += atan(dx/dy);
	if(dy < 0) new_angle += M_PI;
    }

    dx = sin(new_angle) * length;
    dy = cos(new_angle) * length;

    the_x = center.x() + dx;
    the_y = center.y() + dy;
}


void GT_Point::scale (double by, const GT_Point& origin)
{
     the_x = (the_x - origin.x()) * by;
     the_y = (the_y - origin.y()) * by;
}


void GT_Point::scale (double by_x, double by_y, const GT_Point& origin)
{
     the_x = (the_x - origin.x()) * by_x;
     the_y = (the_y - origin.y()) * by_y;
}


double GT_Point::distance(const GT_Point &p) const
{
    double dx = p.the_x-the_x;
    double dy = p.the_y-the_y;

    return sqrt(dx*dx+dy*dy);
}

int orientation (GT_Point a, GT_Point b, GT_Point c) {

    double result
        = a.x() * b.y()  // | a1  a2  1 |
        + a.y() * c.x()  // | b1  b2  1 |
        + b.x() * c.y()  // | c1  c2  1 |
        - b.y() * c.x()
        - c.y() * a.x()
        - b.x() * a.y();

    return
	(result > 0) ? 1 :
	(result < 0) ? -1 : 0;
}    
