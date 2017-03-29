/* This software is distributed under the Lesser General Public License */
//
// Tcl_Command.cc
//
// This file implements the class GT_Tcl_Command.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tcl_Command.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:03 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"

#include "Tcl_Command.h"



//////////////////////////////////////////
//
// class GT_Tcl_Command
//
//////////////////////////////////////////


//
// Constructors and Destructors
//


GT_Tcl_Command::GT_Tcl_Command (const string& name,
    const GT_Key& type)
{
    this->the_name = name;
    this->the_type = type;
    this->the_tcl_interpreter = 0;
}



GT_Tcl_Command::~GT_Tcl_Command ()
{
}



//
// exists
//


bool GT_Tcl_Command::exists (Tcl_Interp* interp, const string& name)
{
    Tcl_CmdInfo infoPtr;
    return (Tcl_GetCommandInfo (interp, const_cast<char*>(name.c_str()),
	&infoPtr) == 1);
}


//
// cmd_install
//


int GT_Tcl_Command::cmd_install (Tcl_Interp* interp,
    Tcl_CmdProc *proc,
    Tcl_CmdDeleteProc *delete_proc)
{
    if (exists (interp, the_name)) {
	Tcl_SetResult (interp, const_cast<char*>(
	    graphlet->error.msg (GT_Error::name_exists, the_name).c_str()),
	    TCL_VOLATILE);
		
	return TCL_ERROR;
    }

    the_tcl_interpreter = interp;
    
    GT_Tcl::CreateCommand (interp,
	name().c_str(),
	proc,
	(ClientData*)this,
	delete_proc);

    Tcl_SetResult (interp, const_cast<char*>(the_name.c_str()), TCL_VOLATILE);

    return TCL_OK;
}


int GT_Tcl_Command::install (Tcl_Interp* interp)
{
    return cmd_install (interp, cmd, 0);
}


	
int GT_Tcl_Command::cmd (ClientData client_data,
    Tcl_Interp*  interp,
    int argc,
    char** argv)
{
    GT_Tcl_Command* this_cmd = (GT_Tcl_Command*)client_data;

    int index = 1;
    GT_Tcl_info info (interp, argc, argv);
    Tcl_SetResult (info.interp(), "", TCL_STATIC);

    int code = this_cmd->cmd_parser (info, index);
    return code;
}


int GT_Tcl_Command::cmd_parser (GT_Tcl_info& /* info */, int& /* index */)
{
    return TCL_OK;
}



//
// GT_Tcl_Graph_Command::get
//



int GT_Tcl_Command::get (Tcl_Interp* interp,
    const string& name,
    GT_Tcl_Command*& cmd)
{
    if (!GT_Tcl::is_gt_object (name.c_str())) {
	string msg = GT::format("%s is not a GT object.", name.c_str());
	Tcl_SetResult (interp, const_cast<char*>(msg.c_str()), TCL_VOLATILE);
	return TCL_ERROR;
    }
	
    if (!exists (interp, name)) {
	string msg = GT::format("%s does not exist.", name.c_str());
	Tcl_SetResult (interp, const_cast<char*>(msg.c_str()), TCL_VOLATILE);
	return TCL_ERROR;		
    }
	
    Tcl_CmdInfo infoPtr;
    Tcl_GetCommandInfo (interp, const_cast<char*>(name.c_str()), &infoPtr);
    cmd = (GT_Tcl_Command*)(infoPtr.clientData);

    return TCL_OK;
}



//////////////////////////////////////////
//
// class GT_Tcl_Graph_Command
//
//////////////////////////////////////////


//
// Constructors and Destructors
//


GT_Tcl_Graph_Command::GT_Tcl_Graph_Command (const string& name,
    GT_Tcl_Graph& g):
	GT_Tcl_Command (name, GT_Keys::graph)
{
    this->the_graph = &g;
}



GT_Tcl_Graph_Command::~GT_Tcl_Graph_Command ()
{
}



int GT_Tcl_Graph_Command::install (Tcl_Interp* interp)
{
    return baseclass::cmd_install (interp,
	GT_Tcl_Graph::static_parser,
	(Tcl_CmdDeleteProc *)NULL);
}



int GT_Tcl_Graph_Command::get (Tcl_Interp* interp,
    const string& name,
    GT_Tcl_Graph*& g)
{
    GT_Tcl_Command* cmd;
    int code = GT_Tcl_Command::get (interp, name, cmd);
    if (code != TCL_ERROR && cmd->type() == GT_Keys::graph) {
		
	GT_Tcl_Graph_Command* graph_cmd = (GT_Tcl_Graph_Command*)cmd;
	g = graph_cmd->the_graph;
	return code;
		
    } else {
		
	g = 0;
	string msg = GT::format("%s is not graph", name.c_str());
	return TCL_ERROR;
    }
}
