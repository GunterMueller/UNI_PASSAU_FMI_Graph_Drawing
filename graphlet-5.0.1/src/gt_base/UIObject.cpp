/* This software is distributed under the Lesser General Public License */
//
// UIObject.cc
//
// This file implements the class GT_UIObject.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/UIObject.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:03 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Graphlet.h"
#include "Device.h"

#include "UIObject.h"


//
// GT_UIObject
//

GT_UIObject::GT_UIObject (GT_Device* const device) :
    the_device (device), the_marker_type (unmarked)
{
}


GT_UIObject::~GT_UIObject ()
{
}


//
// mark commands
//

bool GT_UIObject::mark (const string& /* selection */,
    GT_UIObject::Marker_type marker)
{
    the_marker_type = marker;
    return true;
}


bool GT_UIObject::update_mark (const string& /* selection */)
{
    return true;
}


bool GT_UIObject::del_mark (const string& /* selection */)
{
    the_marker_type = unmarked;
    return true;
}
