/* This software is distributed under the Lesser General Public License */
#ifndef GT_TCL_COMMAND_H
#define GT_TCL_COMMAND_H

//
// Tcl_Command.h
//
// This file defines the class GT_Tcl_Command.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Command.h,v $
// $Author: himsolt $
// $Revision: 1.3 $
// $Date: 1999/03/05 20:46:04 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

//
// class GT_Tcl_Command
//

#include "Tcl.h"
#include "Tcl_Info.h"


class GT_Tcl_Command {

    GT_BASE_CLASS (GT_Tcl_Command);

    GT_COMPLEX_VARIABLE (string, name);
    GT_VARIABLE (Tcl_Interp*, tcl_interpreter);
    GT_VARIABLE (GT_Key, type);
	
public:

    //
    // Constructors and Destructors
    //
	
    GT_Tcl_Command (const string& name, const GT_Key& type);
    virtual ~GT_Tcl_Command ();

    //
    // Installer
    //
	
    virtual int cmd_install (Tcl_Interp* interp,
	Tcl_CmdProc *proc,
	Tcl_CmdDeleteProc *delete_proc);

    virtual int install (Tcl_Interp* interp);
	
    //
    // cmd is called by the Tcl/Tk Callback Handler
    //
	
    static int cmd (ClientData client_data,
	Tcl_Interp*  interp,
	int argc,
	char** argv);

    virtual int cmd_parser (GT_Tcl_info& info, int& index);
	
    //
    // Utilities (static)
    //
	
    static bool exists (Tcl_Interp* interp, const string& name);

    static int get (Tcl_Interp* interp,
	const string& name,
	GT_Tcl_Command*& cmd);
};



//
// class GT_Graph_Tcl_Command
//

class GT_Tcl_Graph_Command : public GT_Tcl_Command {

    GT_CLASS (GT_Tcl_Graph_Command, GT_Tcl_Command);

    GT_Tcl_Graph* the_graph;
	
public:

    GT_Tcl_Graph_Command (const string& name, GT_Tcl_Graph& g);
    virtual ~GT_Tcl_Graph_Command ();

    virtual int install (Tcl_Interp* interp);
    inline GT_Tcl_Graph* get_tcl_graph ();

    static int get (Tcl_Interp* interp,
	const string& name,
	GT_Tcl_Graph*& g);
};


inline  GT_Tcl_Graph* GT_Tcl_Graph_Command::get_tcl_graph ()
{
    return the_graph;
}

#endif
