/* This software is distributed under the Lesser General Public License */
#ifndef GT_PORT_H
#define GT_PORT_H

//
// Port.h
//
// This file defines the class GT_Port.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Port.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:44:44 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#ifndef GT_POINT_H
#include "Point.h"
#endif

class GT_Port : public GT_Point
{
    GT_Key the_name;

public:

    //
    // Constructor & Destructor
    //

    GT_Port ();
    GT_Port (GT_Key name, double x, double y);
    GT_Port (const string& name, double x, double y);
    ~GT_Port();

    //
    // Accessors
    //

    inline GT_Key name () const;
    virtual void name (GT_Key new_name);

    //
    // Utilities
    //

    friend bool operator== (const GT_Port& p1, const GT_Port& p2);
};


inline GT_Key GT_Port::name () const
{
    return the_name;
}


#endif
