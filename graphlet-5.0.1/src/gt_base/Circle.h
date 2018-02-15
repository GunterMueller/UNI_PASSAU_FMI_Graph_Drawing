/* This software is distributed under the Lesser General Public License */
#ifndef GT_CIRCLE_H
#define GT_CIRCLE_H

//
// Circle.h
//
// This file defines the class GT_Circle
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Circle.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:14 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Segment.h"

class GT_Circle {
public:
    GT_Circle (double x, double y, double r) : middle (x,y), radius (r){};
    bool intersection (GT_Segment);
    double distance_between_intersecting_points (GT_Segment);

private:
    GT_Point middle;
    double radius;
};

#endif
