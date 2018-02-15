/* This software is distributed under the Lesser General Public License */
//
// Ports.cpp
//
// This file implements the class GT_Port.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Ports.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:44:45 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Graphlet.h"

#include "Ports.h"


//////////////////////////////////////////
//
// Constructor & Destructor
//
//////////////////////////////////////////


GT_Ports::GT_Ports ()
{
}


GT_Ports::~GT_Ports ()
{
}


//////////////////////////////////////////
//
// Utilities
//
//////////////////////////////////////////


const GT_Port* GT_Ports::find (const GT_Key& name) const
{
    list<GT_Port>::const_iterator it;
    for (it = begin(); it != end(); ++it) {
	if (it->name() == name) {
	    return &(*it);
	}
    }

    return 0;
}
