/* This software is distributed under the Lesser General Public License */
#ifndef GT_GT_TCL_H
#define GT_GT_TCL_H

//
// gt_tcl.h
//
// This is the initialization file for the module gt_tcl.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/gt_tcl.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:46:50 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

extern "C" {
    int Gt_tcl_Init (Tcl_Interp* interp);
#if defined(_WINDOWS)
    int __declspec(dllexport) Graphlet_Init (Tcl_Interp* interp);
#else
    int Graphlet_Init (Tcl_Interp* interp);
#endif
}

#endif
