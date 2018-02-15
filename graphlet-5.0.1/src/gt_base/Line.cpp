/* This software is distributed under the Lesser General Public License */
//
// Line.cpp
//
// This file implements the class GT_Line.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Line.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:14 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"

#include "Line.h"
#include <cassert>

GT_Line::GT_Line(const GT_Point &p1, const GT_Point &p2) :
	the_p1(p1), the_p2(p2)
{
}

GT_Line::GT_Line (const GT_Point& p, double angle) :
	the_p1(p)
{
    assert (angle == 0);
    the_p2 = GT_Point (p.x () + 10, p.y());
}

GT_Point GT_Line::point1 () const
{
    return the_p1;
}

GT_Point GT_Line::point2 () const
{
    return the_p2;
}

bool GT_Line::intersection (const GT_Line& s, GT_Point& inter) const
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
    inter = GT_Point(point1().x()+l*x21,point1().y()+l*y21);
    return true;
}

//--------------------------------------------------------------------------
//   target of file
//--------------------------------------------------------------------------

