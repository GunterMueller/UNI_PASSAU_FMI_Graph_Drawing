/* This software is distributed under the Lesser General Public License */
#ifndef GT_TREE_LAYOUT_H
#define GT_TREE_LAYOUT_H

//
// algorithms.h
//
// This file initializes the algorithms module.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/gt_tree_layout.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/01/24 16:35:05 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//

//
// Initialization procedure
//

extern "C" {
#if defined(_WINDOWS)
    __declspec(dllexport) int Gt_tree_layout_Init (Tcl_Interp* interp);
#else
    int Gt_tree_layout_Init (Tcl_Interp* interp);
#endif
}

#endif
