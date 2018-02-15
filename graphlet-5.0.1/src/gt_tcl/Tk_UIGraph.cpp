/* This software is distributed under the Lesser General Public License */
//
// Tk_UIGraph.cc
//
// This file implements the class GT_Tk_UIGraph.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UIGraph.cpp,v $
// $Author: himsolt $
// $Revision: 1.2 $
// $Date: 1999/03/05 20:46:35 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"
#include "Tk_UIGraph.h"

#include <gt_base/Graph_Attributes.h>


//////////////////////////////////////////
//
// Constructors and Destructors
//
//////////////////////////////////////////


GT_Tk_UIGraph::GT_Tk_UIGraph (GT_Tk_Device* device, GT_Graph& g) :
	GT_Tk_UIObject (device),
	the_graph_attrs (&(g.gt()))
{
}
		

GT_Tk_UIGraph::~GT_Tk_UIGraph ()
{
}


//////////////////////////////////////////
//
// type, uid, graphics
//
//////////////////////////////////////////


const GT_Key& GT_Tk_UIGraph::type() const
{
    return GT_Keys::uiobject_graph;
}


GT_Common_Graphics* GT_Tk_UIGraph::graphics() const
{
    return the_graph_attrs->graphics();
}


int GT_Tk_UIGraph::id () const
{
    return the_graph_attrs->id();
}



//////////////////////////////////////////
//
// make_graph_cmd
// make_tags_cmd
//
//////////////////////////////////////////


void GT_Tk_UIGraph::make_create_cmd (string& /* cmd */)
{
}


void GT_Tk_UIGraph::make_tags_cmd (string& cmd,
    bool /* force */)
{
    cmd = GT::format("%s GT:%d GT:%d",
	type().name().c_str(),
	the_graph_attrs->id(),
	the_graph_attrs->graphics()->uid());
}


//////////////////////////////////////////
//
// make_mark_cmd
// make_update_mark_cmd
//
//////////////////////////////////////////


void GT_Tk_UIGraph::make_mark_cmd (string& /* cmd */,
    const string& /* selection */,
    Marker_type /* type */)
{
    // UNUSED
}


void GT_Tk_UIGraph::make_update_mark_cmd (string& /* cmd */,
    const string& /* selection */,
    Marker_type /* type */)
{
    // UNUSED
}
