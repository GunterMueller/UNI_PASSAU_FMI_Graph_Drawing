/* This software is distributed under the Lesser General Public License */
//
// gt_tree_layout.cpp
//
// This file initializes the module gt_tree_layout.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tree_layout/gt_tree_layout.cpp,v $
// $Author: raitner $
// $Revision: 1.2 $
// $Date: 1998/11/16 11:41:28 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//

#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include "gt_tree_layout.h"
#include "tree_algorithm.h"


int Gt_tree_layout_Init (Tcl_Interp* interp)
{
    int code = TCL_OK;

    GT_Tcl_Algorithm<GT_Extended_TR>* layout_extended_tr_tree =
	new GT_Tcl_Extended_TR ("layout_extended_tr_tree");
    code = layout_extended_tr_tree->install (interp);
    if (code != TCL_OK) {
	return code;
    }    

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_tree_layout", version);
    if (code != TCL_OK) {
	return code;
    }

    return GT_OK; 
}
