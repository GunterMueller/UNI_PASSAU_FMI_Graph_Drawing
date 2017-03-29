/* This software is distributed under the Lesser General Public License */
//
// gt_radial_tree_layout.cpp
//
// This file initializes the algorithms module.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_radial_tree_layout/gt_radial_tree_layout.cpp,v $
// $Revision: 1.1.1.1 $
// $Date: 1998/08/27 17:19:25 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//

#include <gt_base/Graphlet.h>

#include <gt_tcl/Tcl_Graph.h>
#include <gt_tcl/Tcl_Algorithm.h>

#include "gt_radial_tree_layout.h"

#ifdef GT_RADIAL_TREE_LAYOUT_H
#include "radial_tree_layout.h"
#endif

int Gt_radial_tree_layout_Init (Tcl_Interp* interp)
{
    int code = TCL_OK;

    GT_Tcl_Algorithm<GT_Layout_Radial_Algorithm>* layout_radial =
	new GT_Tcl_Layout_Radial_Algorithm ("layout_radial");
    code = layout_radial->install (interp);
    if (code == TCL_ERROR) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Gt_radial_tree_layout", version);
    if (code != TCL_OK) {
	return code;
    }

    return code;
}




