/* This software is distributed under the Lesser General Public License */
//
// Circle.cpp
//
// This file implements the class GT_Circle.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Circle.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:13 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"

#include "Circle.h"

#include <cmath>

bool
GT_Circle::intersection (GT_Segment s) {
    double dist = s.distance (middle);
    double m_to_p1 = middle.distance (s.source());
    double m_to_p2 = middle.distance (s.target());
    
    if (radius < dist) {
	return false;
    } else if (m_to_p1 < radius && m_to_p2 < radius) {
	return false;
    }

    return true;
}

double
GT_Circle::distance_between_intersecting_points (GT_Segment s) {
    double dist = s.distance (middle);
    double m_to_p1 = middle.distance (s.source());
    double m_to_p2 = middle.distance (s.target());
    
    if (radius < dist) {
	return -1; // no intersection
    } else if (m_to_p1 < radius && m_to_p2 < radius) {
	return -1; // no intersection
    }
    
    return 2 * sqrt (pow (dist, 2) + pow (radius, 2));
}    
