/* This software is distributed under the Lesser General Public License */
//
// algorithms.cc
//
// This file initializes the algorithms module.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_cfr_layout/gt_cfr_layout.cpp,v $
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

#include "gt_cfr_layout.h"
#include "cfr_algorithm.h"


int Gt_cfr_layout_Init (Tcl_Interp* interp)
{
    int code = TCL_OK;
    
    GT_Tcl_Algorithm<GT_Layout_Constraint_Fruchterman_Reingold_Algorithm>*
	layout_constraint_fr = 
	new GT_Tcl_Layout_Constraint_Fruchterman_Reingold_Algorithm
	("layout_constraint_fr");
    code = layout_constraint_fr->install (interp);
    if (code != TCL_OK) {
	return code;
    }
    
    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_cfr_layout", version);
    if (code != TCL_OK) {
	return code;
    }

    return GT_OK; 
}
