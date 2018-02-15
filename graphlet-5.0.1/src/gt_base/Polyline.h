/* This software is distributed under the Lesser General Public License */
#ifndef GT_POLYLINE_H
#define GT_POLYLINE_H

//
// Polyline.h
//
// This file defines the class GT_Polyline
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Polyline.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:40 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Segment.h"

#ifndef GT_POINT_H
#include "Point.h"
#endif

#ifndef GT_RECTANGLE_H
#include "Rectangle.h"
#endif


//////////////////////////////////////////
//
// GT_Polyline: a list of GT_Point objects
//
//////////////////////////////////////////


class GT_Polyline : public list<GT_Point> {

public:

    //
    // Constructor & Destructor
    //

    GT_Polyline ();
    GT_Polyline (const GT_Polyline& l);
    GT_Polyline (const list<GT_Point>& l);
    virtual ~GT_Polyline ();

    //
    // Operations
    //

    GT_Segment nth_segment (const int n) const;

    void move (const GT_Point& move_xy);
    virtual void scale (double by,
	const GT_Point& origin = GT_Point (0.0,0.0));
    virtual void scale (double by_x, double by_y,
	const GT_Point& origin = GT_Point (0.0,0.0));
    
    virtual void bbox(GT_Rectangle& bbox) const;
    GT_Rectangle bbox() const;

    //
    // Operators
    //

//     bool operator== (const GT_Polyline&) const;
    friend ostream& operator<< (ostream& out, const GT_Polyline& p);
};

ostream& operator<< (ostream& out, const GT_Polyline& p);


#endif
