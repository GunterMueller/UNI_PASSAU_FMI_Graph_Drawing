/* This software is distributed under the Lesser General Public License */
//
// Polyline.cpp
//
// This file implements the class GT_Polyline.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Polyline.cpp,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:38 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


#include "Graphlet.h"

#include "Polyline.h"



//////////////////////////////////////////
//
// GT_Polyline
//
//////////////////////////////////////////


//
// Constructor & Destructor
//


GT_Polyline::GT_Polyline ()
{
}


GT_Polyline::GT_Polyline (const GT_Polyline& l)
	: list<GT_Point> (l)
{
}


GT_Polyline::GT_Polyline (const list<GT_Point>& l)
	: list<GT_Point> (l)
{
}


GT_Polyline::~GT_Polyline ()
{
}



//
// Comparison Operator
//


// bool GT_Polyline::operator== (const GT_Polyline& other) const
// {
//     if (length() == other.length()) {
// 	list_item it = first();
// 	list_item other_it = other.first();
// 	while (it != 0) {
// 	    if (contents(it) == contents(other_it)) {
// 		it = succ(it);
// 		other_it = succ(other_it);
// 	    } else {
// 		return false;
// 	    }
// 	}
// 	return true;
//     } else {
// 	return false;
//     }

//     // return *this == other;
// }


//
// nth_segment
//


GT_Segment GT_Polyline::nth_segment (const int n) const
{
    assert (n < (signed)size());

    const_iterator it = begin();
    advance(it, n);

    const GT_Point& p1 = *it;
    const GT_Point& p2 = *(++it);

    return GT_Segment (p1, p2);
}


//
// move
//


void GT_Polyline::move (const GT_Point& move_xy)
{
    for(iterator it = begin(); it != end(); ++it)
	it->move (move_xy);	
}


//
// scale
//


void GT_Polyline::scale (double by, const GT_Point& origin)
{
    for(iterator it = begin(); it != end(); ++it)
	it->scale (by, origin);
}


void GT_Polyline::scale (double by_x, double by_y, const GT_Point& origin)
{
    for(iterator it = begin(); it != end(); ++it)
	it->scale (by_x, by_y, origin);
}


void GT_Polyline::bbox (GT_Rectangle& rect) const
{
    GT_Point p;
    double minx = MAXDOUBLE;
    double maxx = 0;
    double miny = MAXDOUBLE;
    double maxy = 0;
    double x;
    double y;
    
    for(const_iterator it = begin(); it != end(); ++it)
    {
	x = it->x();
	y = it->y();
	if (x < minx) {
	    minx = x;
	}
	if (y < miny) {
	    miny = y;
	}
	if (x > maxx) {
	    maxx = x;
	}
	if (y > maxy) {
	    maxy = y;
	}
    }

    rect = GT_Rectangle (
	(minx + maxx) / 2.0,
	(miny + maxy) / 2.0,
	maxx - minx,
	maxy - miny);
}


GT_Rectangle GT_Polyline::bbox() const
{
    GT_Rectangle rect;
    bbox (rect);
    return rect;
}


//
// Output operator
//

ostream& operator<< (ostream& out, const GT_Polyline& p)
{
    bool first = true;
    for(GT_Polyline::const_iterator it = p.begin();
	it != p.end(); ++it) 
    {
	out << *it;
	if (!first) {
	    out << ' ';
	} else {
	    first = false;
	}
    }

    return out;
}
