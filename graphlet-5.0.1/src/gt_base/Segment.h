/* This software is distributed under the Lesser General Public License */
#ifndef GT_SEGMENT_H
#define GT_SEGMENT_H

//
// Segment.h
//
// This file defines the class GT_Segment
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Segment.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:54 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Point.h"

//--------------------------------------------------------------------------
//   Class declaration
//--------------------------------------------------------------------------

class GT_Segment
{
public:

    //================================================== Constructors
    
    GT_Segment(const GT_Point &, const GT_Point &);

    //================================================== Operations
    
    double xcoord1() const;
    double ycoord1() const;
    double xcoord2() const;
    double ycoord2() const;

    GT_Point source() const;
    GT_Point target() const;
    
    double length() const;
    
    bool intersection (const GT_Segment &, GT_Point &) const;
    bool intersection_of_lines (const GT_Segment &, GT_Point &) const;

    double slope() const;
    double distance (const GT_Point&) const;
    
    //================================================== Implementation
    
private:
    GT_Point the_p1, the_p2;
    bool is_vertical() const;
    bool is_horizontal() const;
    GT_Point point1() const;
    GT_Point point2() const;
};

#endif // GT_SEGMENT_H

//--------------------------------------------------------------------------
//   end of file
//--------------------------------------------------------------------------

