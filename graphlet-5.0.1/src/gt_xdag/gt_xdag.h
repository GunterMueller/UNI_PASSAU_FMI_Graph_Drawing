/* This software is distributed under the Lesser General Public License */
#ifndef GT_XDAG_H
#define GT_XDAG_H

//
// gt_xdag.h
//
// This file initializes the 'extended dag' module.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/gt_xdag.h,v $
// $Author: himsolt $
// $Revision: 1.1.1.1 $
// $Date: 1998/08/27 17:19:30 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1997, Graphlet Project
//
//     Author: Harald Mader (mader@fmi.uni-passau.de)
//


//
// Initialization procedure
//

extern "C" {
    int Gt_xdag_Init (Tcl_Interp* interp);
}

#endif
