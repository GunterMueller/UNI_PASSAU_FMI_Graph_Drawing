/* This software is distributed under the Lesser General Public License */
#ifndef GT_TCL_DEVICE_H
#define GT_TCL_DEVICE_H

//
// Tk_Device.h
//
// This file defines the classes GT_Tk_Device.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_Device.h,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include <gt_base/Device.h>

class GT_Graphscript;

class GT_Tk_Device : public GT_Device {

    GT_CLASS (GT_Tk_Device, GT_Device);

    GT_VARIABLE (GT_Graphscript*, graphscript);
	
public:

    GT_Tk_Device (const string& name, GT_Graphscript* graphscript);
    virtual ~GT_Tk_Device ();

    Tcl_Interp* interp();

    void translate (double from_x, double from_y, int& to_x, int& to_y) const;
    void translate (double from_x[], double from_y[],
	int to_x[], int to_y[],
	int n) const;

    virtual bool del_full (int uid);
};

#endif
