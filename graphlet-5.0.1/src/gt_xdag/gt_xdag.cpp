/* This software is distributed under the Lesser General Public License */
//
// gt_xdag.cpp
//
// This file initializes the 'extended dag' module.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_xdag/gt_xdag.cpp,v $
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

#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include "gt_xdag.h"
#include "xdag_algorithm.h"


int Gt_xdag_Init (Tcl_Interp* interp)
{
    int code = TCL_OK;
 
    GT_Tcl_Algorithm<GT_Extended_DAG_Algorithm>* layout_extended_dag =
	new GT_Tcl_Extended_DAG_Algorithm ("layout_xdag");
    code = layout_extended_dag->install (interp);
    if (code != TCL_OK) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_xdag", version);
    if (code != TCL_OK) {
	return code;
    }

    return code; 
}
