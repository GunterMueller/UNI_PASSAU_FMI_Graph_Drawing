/* This software is distributed under the Lesser General Public License */
#ifndef GT_LINE_H
#define GT_LINE_H

//
// Line.h
//
// This file defines the class GT_Line
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Line.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:16 $
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

class GT_Line
{
public:

    //================================================== Constructors
    
    GT_Line (const GT_Point&, const GT_Point&);
    GT_Line (const GT_Point&, double);

    //================================================== Operations

    GT_Point point1 () const;
    GT_Point point2 () const;
    
    bool intersection (const GT_Line&, GT_Point&) const;

    //================================================== Implementation
    
private:    
    GT_Point the_p1, the_p2;
};

#endif // GT_LINE_H

//--------------------------------------------------------------------------
//   end of file
//--------------------------------------------------------------------------

