/* This software is distributed under the Lesser General Public License */
#ifndef GT_PORTS_H
#define GT_PORTS_H

//
// Ports.h
//
// This file defines the class GT_Ports.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Ports.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:47 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//


#ifndef GT_PORT_H
#include "Port.h"
#endif

class GT_Ports : public list<GT_Port>
{
public:

    //
    // Constructor & Destructor
    //

    GT_Ports ();
    virtual ~GT_Ports ();

    virtual const GT_Port* find (const GT_Key& name) const;
};


#endif
