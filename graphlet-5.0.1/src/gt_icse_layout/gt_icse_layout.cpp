/* This software is distributed under the Lesser General Public License */
//
// algorithms.cc
//
// This file initializes the algorithms module.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_icse_layout/gt_icse_layout.cpp,v $
// $Author: himsolt $
// $Revision: 1.1.1.1 $
// $Date: 1998/08/27 17:19:16 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//

#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include "gt_icse_layout.h"
#include "icse_algorithm.h"



int Gt_icse_layout_Init (Tcl_Interp* interp)
{
    int code = TCL_OK;    

    GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm*
	layout_iterative_constraint_se = 
	new GT_Tcl_Iterative_Constraint_Spring_Embedder_Algorithm
	("layout_iterative_constraint_se");
    code = layout_iterative_constraint_se->install (interp);
    if (code != TCL_OK) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_icse_layout", version);
    if (code != TCL_OK) {
	return code;
    }

    return GT_OK; 
}
