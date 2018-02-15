/* This software is distributed under the Lesser General Public License */
//
// gt_tcl.cpp
//
// This file implements the initialization code for gt_tcl.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/gt_tcl.cpp,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/03/05 20:46:48 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include <tk.h>

#include <gt_base/Graphlet.h>
#include "Graphscript.h"
#include "gt_tcl.h"

int Gt_tcl_Init (Tcl_Interp *interp)
{
    //
    // Create a Graphscript handler. For customization, initialize
    // graphscript with a derived class.
    //

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    int code = Tcl_PkgProvide (interp, "Graphlet", version);
    if (code != TCL_OK) {
	return code;
    }

    GT_Graphscript* graphscript = new GT_Graphscript (interp);
    
    code = graphscript->application_init (interp);
    if (code == TCL_ERROR) {
	return code;
    }

//     char version[(2+2+2) + 2 + 1];
//     sprintf (version, "%d.%d.%d",
// 	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
//     code = Tcl_PkgProvide (interp, "Gt_tcl", version);
//     if (code != TCL_OK) {
// 	return code;
//     }

    return code;
}

#if defined(_WINDOWS) && defined(GT_DLL)
#include "../graphscript/modules.h"
int __declspec(dllexport) Graphlet_Init (Tcl_Interp *interp)
#else
int Graphlet_Init (Tcl_Interp *interp)
#endif
{
    int code;

    code = Gt_tcl_Init (interp);
    if (code != TCL_OK) {
	return code;
    }

#if defined(_WINDOWS) && defined(GT_DLL)
#include "../graphscript/modules.cpp"
#endif

    return TCL_OK;
}
