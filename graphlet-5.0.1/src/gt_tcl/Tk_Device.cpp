/* This software is distributed under the Lesser General Public License */
//
// Tcl_Devices.cc
//
// This file implements the classes GT_UIObjects, GT_Device and
// GT_Devices.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_Device.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:28 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"
#include "Graphscript.h"

#include "Tk_Device.h"



//////////////////////////////////////////
//
// Constructor & Destructor
//
//////////////////////////////////////////


GT_Tk_Device::GT_Tk_Device (const string& name,
    GT_Graphscript* graphscript) :
	GT_Device (name),
	the_graphscript (graphscript)
{
}


GT_Tk_Device::~GT_Tk_Device ()
{
}



//////////////////////////////////////////
//
// Tcl_Interp* GT_Tk_Device::interp()
//
// Returns the interpreter associated with this device. The
// interpreter is actually stored in the graphscript object.
//
//////////////////////////////////////////


Tcl_Interp* GT_Tk_Device::interp()
{
    return the_graphscript->interp();
}

//////////////////////////////////////////
//
//  translate (double from_x, double from_y, int& to_x, int& to_y)
//  translate (double from_x[], double from_y[], int to_x[], int to_y[], int n)
//
//  Translates coordinates to integer, rounds in Tk fashion.
//
//////////////////////////////////////////


void GT_Tk_Device::translate (double from_x, double from_y,
    int& to_x, int& to_y) const
{
    if (from_x >= 0.0) {
	to_x = (int) (baseclass::translate_x (from_x) + 0.5);
    } else {
	to_x = (int) (baseclass::translate_x (from_x) - 0.5);
    }

    if (from_y >= 0.0) {
	to_y = (int) (baseclass::translate_y (from_y) + 0.5);
    } else {
	to_y = (int) (baseclass::translate_y (from_y) - 0.5);
    }
}


void GT_Tk_Device::translate (double from_x[], double from_y[],
    int to_x[], int to_y[],
    int n) const
{
    for (int i=0; i<n; i++) {

	if (from_x[i] >= 0.0) {
	    to_x[i] = (int) (baseclass::translate_x (from_x[i]) + 0.5);
	} else {
	    to_x[i] = (int) (baseclass::translate_x (from_x[i]) - 0.5);
	}

	if (from_y[i] >= 0.0) {
	    to_y[i] = (int) (baseclass::translate_y (from_y[i]) + 0.5);
	} else {
	    to_y[i] = (int) (baseclass::translate_y (from_y[i]) - 0.5);
	}
    }
}



//////////////////////////////////////////
//
// del_full
//
//////////////////////////////////////////


bool GT_Tk_Device::del_full (int id)
{
    string cmd = GT::format("%s delete GT:%d", name().c_str(), id);
	
    return the_graphscript->tcl_eval (cmd);
}
