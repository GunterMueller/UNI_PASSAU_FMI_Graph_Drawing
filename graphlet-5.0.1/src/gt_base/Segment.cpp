/* This software is distributed under the Lesser General Public License */
//
// Segment.cpp
//
// This file implements the class GT_Segment.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Segment.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:52 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


#include "Graphlet.h"

#include "Segment.h"
#include "Line.h"

#include <cmath>

GT_Segment::GT_Segment(const GT_Point &p1, const GT_Point &p2) :
    the_p1(p1), the_p2(p2)
{
}

double GT_Segment::xcoord1() const
{
    return the_p1.x();
}

double GT_Segment::ycoord1() const
{
    return the_p1.y();
}

double GT_Segment::xcoord2() const
{
    return the_p2.x();
}

double GT_Segment::ycoord2() const
{
    return the_p2.y();
}

GT_Point GT_Segment::source() const
{
    return the_p1;
}

GT_Point GT_Segment::target() const
{
    return the_p2;
}
    
double GT_Segment::length() const
{
    return the_p1.distance(the_p2);
}

double GT_Segment::slope() const
{
    double dx = xcoord1() - xcoord2();
    double dy = ycoord1() - ycoord2();
    
    return (dx == 0) ? MAXDOUBLE : dy/dx; 
}

double GT_Segment::distance (const GT_Point& p) const 
{
    bool p1_lowest_y = true; 
    double max_y, min_y;

    if (the_p1.y () > the_p2.y ()) {
	p1_lowest_y = false;
	min_y = the_p2.y ();
	max_y = the_p1.y ();
    } else {
	min_y = the_p2.y ();
	max_y = the_p1.y ();
    }

    if (is_vertical ()) { 
	if (p.y () > max_y) {
	    if (p1_lowest_y) {
		return p.distance (the_p2);
	    } else {
		return p.distance (the_p1);
	    }

	} else if (p.y() < min_y) {	    
	    if (p1_lowest_y) {
		return p.distance (the_p1);
	    } else {
		return p.distance (the_p2);
	    }

	} else {
	    return fabs (p.x () - the_p1.x());
	}

    } else {
	GT_Line tmp (p, p - GT_Point (1, slope()));
	GT_Point sect;
	GT_Line this_line (the_p1, the_p2);

	if (tmp.intersection (this_line, sect)) {
	    return p.distance (sect);

	} else {
	    double p_to_p1 = p.distance (the_p1);
	    double p_to_p2 = p.distance (the_p2);

	    return p_to_p1 < p_to_p2 ? p_to_p1 : p_to_p2;
	}
    }	
}

bool GT_Segment::is_vertical() const
{
    return xcoord1() == xcoord2();
}

bool GT_Segment::is_horizontal() const
{
    return ycoord1() == ycoord2();
}

GT_Point GT_Segment::point1() const
{
    return the_p1;
}

GT_Point GT_Segment::point2() const
{
    return the_p2;
}

bool GT_Segment::intersection(const GT_Segment& s, GT_Point& inter) const
{
    // vector of lines
    double x21 =   point2().x() -   point1().x();
    double x43 = s.point2().x() - s.point1().x();
    double y21 =   point2().y() -   point1().y();
    double y43 = s.point2().y() - s.point1().y();

    // parallel ?
    if ( (x21==0 ? MAXDOUBLE : y21/x21) == (x43==0 ? MAXDOUBLE : y43/x43) )
	return false;

    // s-line is vertical or horizontal and line is not vertical or horizontal?
    if ( (y43 != 0 && x43 == 0) || (x21 != 0 && y43 == 0))
    {
	return s.intersection(*this, inter);
    }

    // line is vertical?
    if (x21 == 0)
    {
	// evaluate y-coordinate
	double l = (point1().x()-s.point1().x()) / x43;
	inter = GT_Point(point1().x(), s.point1().y()+l*y43);
	return true;
    }

    // line is horizontal?
    if (y21 == 0)
    {
	// evaluate y-coordinate
	double l = (point1().y()-s.point1().y()) / y43;
	inter = GT_Point(s.point1().x()+l*x43, point1().y());
	return true;
    }

    // otherwise both lines are not vertical or horizontal
    double l = ( (s.point1().x()-point1().x())*y43
	+(point1().y()-s.point1().y())*x43 ) /
	(x21*y43-y21*x43);
    double m = (point1().y()+l*y21-s.point1().y())/y43;
    // check wether point is within both segments
    if (l < 0 || l > 1 || m < 0 || m > 1) 
	return false;
    inter = GT_Point(point1().x()+l*x21,point1().y()+l*y21);
    return true;
}

bool GT_Segment::intersection_of_lines(const GT_Segment& s, GT_Point& inter) const
{
    // vector of lines
    double x21 =   point2().x() -   point1().x();
    double x43 = s.point2().x() - s.point1().x();
    double y21 =   point2().y() -   point1().y();
    double y43 = s.point2().y() - s.point1().y();

    // parallel?
    if ( (x21==0 ? MAXDOUBLE : y21/x21) == (x43==0 ? MAXDOUBLE : y43/x43) )
	return false;

    // s-line is vertical or horizontal and line is not vertical or horizontal?
    if ( (y43 != 0 && x43 == 0) || (x21 != 0 && y43 == 0))
    {
	return s.intersection(*this, inter);
    }

    // line is vertical?
    if (x21 == 0)
    {
	// evaluate y-coordinate
	double l = (point1().x()-s.point1().x()) / x43;
	inter = GT_Point(point1().x(), s.point1().y()+l*y43);
	return true;
    }

    // line is horizontal?
    if (y21 == 0)
    {
	// evaluate y-coordinate
	double l = (point1().y()-s.point1().y()) / y43;
	inter = GT_Point(s.point1().x()+l*x43, point1().y());
	return true;
    }

    // otherwise both lines are not vertical or horizontal
    double l = ( (s.point1().x()-point1().x())*y43
	+(point1().y()-s.point1().y())*x43 ) /
	(x21*y43-y21*x43);
    inter = GT_Point(point1().x()+l*x21,point1().y()+l*y21);
    return true;
}

//--------------------------------------------------------------------------
//   target of file
//--------------------------------------------------------------------------

