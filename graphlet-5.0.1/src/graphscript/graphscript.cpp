/* This software is distributed under the Lesser General Public License */
//
// graphscript.cpp
//
// This is a sample Graphscript main file. This file declares and
// implements a sample layout algorithm and creates a Graphscript
// interpreter.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/graphscript/graphscript.cpp,v $
// $Author: himsolt $
// $Revision: 1.6 $
// $Date: 1999/01/30 14:09:02 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//

#ifdef WIN32
#define list dummy_list
#endif
#include <tk.h>
#ifdef WIN32
#undef list
#endif

#include <gt_base/Graphlet.h>

#include <gt_tcl/Graphscript.h>

#include "graphscript.h"

#include "modules.h"


//////////////////////////////////////////
//
// static int application_init (Tcl_Interp *interp)
//
// This is a sample Tk application initialization procedure.
//
//////////////////////////////////////////

extern "C" {
    int Gt_tcl_Init (Tcl_Interp *interp);
    int Graphlet_Init (Tcl_Interp *interp);
}

static int application_init (Tcl_Interp *interp)
{
    int code;
    code = Tcl_Init (interp);
    if (code == TCL_ERROR) {
	return code;
    }

    if (!GT_Graphscript::tcl_only()) {
	code = Tk_Init (interp);
	if (code == TCL_ERROR) {
	    return code;
	}
    }

    code = Graphlet_Init (interp);
    if (code == TCL_ERROR) {
	return code;
    }

    char version[(2+2+2) + 2 + 1];
    sprintf (version, "%d.%d.%d",
	GT_MAJOR_VERSION, GT_MINOR_VERSION, GT_MINI_VERSION);
    code = Tcl_PkgProvide (interp, "Graphlet", version);
    if (code != TCL_OK) {
	return code;
    }
    Tcl_StaticPackage(interp, "Graphlet", Graphlet_Init, 0);

#include "modules.cpp"

    //
    // We are finished for today.
    //
	
    return GT_OK;
}


//////////////////////////////////////////
//
// This is the main program for UNIX and Windows.
//
//////////////////////////////////////////

//
// UNIX version
//

#if (defined(_CONSOLE) && defined(WIN32)) || !defined(WIN32)

int main (int argc, char **argv) 
{
    return GT_Graphscript::gt_main (argc, argv,
	application_init);
}
#endif


//
// Windows Version
//

#if defined(_WINDOWS) && defined(WIN32)

int APIENTRY WinMain (HINSTANCE hInstance, HINSTANCE hPrevInstance,
		      LPSTR lpszCmdLine, int nCmdShow)
{    
    return GT_Graphscript::gt_main (hInstance, hPrevInstance,
	lpszCmdLine, nCmdShow,
        application_init);
}

#endif
