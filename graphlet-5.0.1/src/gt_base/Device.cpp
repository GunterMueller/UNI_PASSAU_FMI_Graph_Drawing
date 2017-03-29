/* This software is distributed under the Lesser General Public License */
//
// Device.cpp
//
// This file implements the classes GT_Device and GT_Device.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Device.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:43:23 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Graphlet.h"
#include "Device.h"

// Appearant bug with MS Windows systems

#ifdef WIN32
#include "Common_Graphics.h"
#include "UIObject.h"
#endif

//////////////////////////////////////////
//
// GT_Device
//
//////////////////////////////////////////



GT_Device::GT_Device (const string& name,
    double scale_x,
    double scale_y) :
	the_objects (),
	the_name (name),
	the_scale_x (scale_x),
	the_scale_y (scale_y)
{
}


GT_Device::~GT_Device ()
{
}
    

void GT_Device::name (const string& n)
{
    the_name = n;
}


void GT_Device::scale_x (double x)
{
    the_scale_x = x;
}


void GT_Device::scale_y (double y)
{
    the_scale_y = y;
}


void GT_Device::scale (double x, double y)
{
    the_scale_x = x;
    the_scale_y = y;
}




GT_UIObject* GT_Device::insert (int uid, GT_UIObject* uiobject)
{
    assert (uid >= 0);
    assert (uiobject != 0);

    the_objects[uid] = uiobject;

    return uiobject;
}


GT_UIObject* GT_Device::get (int id) const
{
    if (the_objects.find(id) != the_objects.end()) {
	return (*the_objects.find(id)).second;
    } else {
	return 0;
    }
}


bool GT_Device::defined (int id) const
{
    return the_objects.find(id) != the_objects.end();
}


bool GT_Device::del (int id)
{
    the_objects.erase (id);
    return true;
}


bool GT_Device::del_full (int /* id */)
{
    return false;
}


//
// Coordinate transformation
//


double GT_Device::translate_x (double x) const
{
    return x * the_scale_x;
}


double GT_Device::translate_y (double y) const
{
    return y * the_scale_y;
}


double GT_Device::translate_x_reverse (double x) const
{
    assert (the_scale_x != 0);
    
    return x / the_scale_x;
}


double GT_Device::translate_y_reverse (double y) const
{
    assert (the_scale_y != 0);
    
    return y / the_scale_y;
}


//////////////////////////////////////////
//
// Translation Utilities
//
//////////////////////////////////////////


void GT_Device::translate (double x[], double y[], int n) const
{
    for (int i=0; i<n; i++) {
	x[i] = the_scale_x * x[i];
	y[i] = the_scale_y * y[i];
    }
}


void GT_Device::translate (double from_x[], double from_y[],
    double to_x[], double to_y[],
    int n) const
{ 
    for (int i=0; i<n; i++) {
	to_x[i] = the_scale_x * from_x[i];
	to_y[i] = the_scale_y * from_y[i];
    }
}
