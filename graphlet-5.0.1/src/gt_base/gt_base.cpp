/* This software is distributed under the Lesser General Public License */
//
// gt_tcl.cpp
//
// This file implements the initialization code for gt_tcl.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/gt_base.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:45:10 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Graphlet.h"
#include "gt_base.h"

//
// This is a dummy Tcl initialization procedure. libgt_base.so
// can now be loaded into a Tcl file.
//

int Gt_base_Init (void *interp)
{
    return GT_OK;
}
