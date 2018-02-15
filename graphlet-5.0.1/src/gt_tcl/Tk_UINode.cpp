/* This software is distributed under the Lesser General Public License */
//
// Tk_UINode.cc
//
// This file implements the class GT_Tk_UINode.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UINode.cpp,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:46:41 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"

#include "Tk_UINode.h"
#include "Graphscript.h"

#include <gt_base/Node_Attributes.h>


//////////////////////////////////////////
//
// Constructors and Destructors
//
//////////////////////////////////////////


GT_Tk_UINode::GT_Tk_UINode (GT_Tk_Device* device,
    GT_Graph& g, const node n) :
	GT_Tk_UIObject (device),
	the_node_attrs (&(g.gt(n)))
{
}
		

GT_Tk_UINode::~GT_Tk_UINode ()
{
}


//////////////////////////////////////////
//
// type, id, graphics
//
//////////////////////////////////////////


const GT_Key& GT_Tk_UINode::type() const
{
    return GT_Keys::uiobject_node;
}


GT_Common_Graphics* GT_Tk_UINode::graphics() const
{
    return the_node_attrs->graphics();
}


int GT_Tk_UINode::id () const
{
    return the_node_attrs->id();
}


//////////////////////////////////////////
//
// make_node_cmd
// make_tags_cmd
//
//////////////////////////////////////////


void GT_Tk_UINode::make_create_cmd (string& cmd )
{
    baseclass::make_create_cmd (cmd);
}


void GT_Tk_UINode::make_tags_cmd (string& cmd,
    bool /* force */)
{
    cmd = GT::format("%s GT:%d GT:%d",
	type().name().c_str(),
	the_node_attrs->id(),
	the_node_attrs->graphics()->uid());
}


bool GT_Tk_UINode::update (bool force)
{
    const bool type_changed = graphics()->is_changed(
	GT_Common_Graphics::tag_type);
	
    if (!type_changed) {
	return update_coords (force) && update_attrs (force);
    } else {
	graphics()->reset ();
	int label_uid = the_node_attrs->label_graphics()->uid();
	GT_UIObject* uilabel = device()->get (label_uid);
	if (uilabel != 0) {
	    return del () && create () && uilabel->raise ();
	} else {
	    return del () && create ();
	}
    }
}



//////////////////////////////////////////
//
// make_mark_cmd
// make_update_mark_cmd
//
//////////////////////////////////////////


void GT_Tk_UINode::coords_frame (const GT_Common_Graphics* cg,
    double double_marker_width,
    int ix[2],
    int iy[2])
{
    int width = int(cg->width ());
    if (width == 0) {
	width = 1;
    }
    int marker_width = int (double_marker_width);
    if (marker_width == 0) {
	marker_width = 1;
    }

    double x[2];
    double y[2];
    coords_box (cg->center(), x,y);
    tk_device()->translate (x,y, ix,iy, 2);

    int frame_width = (width + int(marker_width)) / 2;

    if (width % 2 == 0) {
	if (marker_width % 2 == 0) {
	    ix[0] -= frame_width;
	    iy[0] -= frame_width;
	    ix[1] += frame_width;
	    iy[1] += frame_width;
	} else {
	    ix[0] -= frame_width+1;
	    iy[0] -= frame_width+1;
	    ix[1] += frame_width;
	    iy[1] += frame_width;
	}
    } else {
	if (marker_width % 2 == 0) {
	    ix[0] -= frame_width;
	    iy[0] -= frame_width;
	    ix[1] += frame_width+1;
	    iy[1] += frame_width+1;
	} else {
	    ix[0] -= frame_width+1;
	    iy[0] -= frame_width+1;
	    ix[1] += frame_width+1;
	    iy[1] += frame_width+1;
	}
    }
} 



void GT_Tk_UINode::make_mark_cmd (string& cmd,
    const string& selection,
    Marker_type /* type */)
{
    const GT_Common_Graphics* cg = graphics();
    const GT_Graphscript* graphscript = tk_device()->graphscript();

    //
    // width and marker_width
    //

    double marker_width = graphscript->option_marker_width();
//     int code = Tcl_GetDouble (tk_device()->interp(), "", &marker_width);
//     if (code == TCL_ERROR) {
// 	marker_width = 2.0;
//     }

    //
    // Create the command
    //

    switch (the_marker_type) {

	case blocks :
	{
// 	    int ix[2];
// 	    int iy[2];
// 	    ix[0] = int(cg->x()) - int(cg->w() + cg->width()) / 2;
// 	    ix[1] = int(cg->x()) + int(cg->w() + cg->width()) / 2;
// 	    iy[0] = int(cg->y()) - int(cg->h() + cg->width()) / 2;
// 	    iy[1] = int(cg->y()) + int(cg->h() + cg->width()) / 2;
// 	    if (int(cg->w() + cg->width()) % 2 == 0) {
// 		// ix[0] ++;
// 		// ix[1] ++;
// 	    }
// 	    if (int(cg->h() + cg->width()) % 2 == 0) {
// 		// iy[0] ++;
// 		// iy[1] ++;
// 	    }
	    double x[2];
	    double y[2];
	    x[0] = cg->x() - (cg->w() + cg->width()) / 2.0;
	    x[1] = cg->x() + (cg->w() + cg->width()) / 2.0;
	    y[0] = cg->y() - (cg->h() + cg->width()) / 2.0;
	    y[1] = cg->y() + (cg->h() + cg->width()) / 2.0;
	    int ix[2];
	    int iy[2];
	    tk_device()->translate (x,y, ix,iy, 2);

	    string cmdline = GT::format (
		"%s create rectangle %%d %%d %%d %%d -tags {node:marker:%s GT:%d marker:%s:GT:%d marker:%s:GT:%d:%%d} -fill %s -outline %s\n",
		device()->name().c_str(),
			selection.c_str(),
			id(),
			selection.c_str(), id(),
			selection.c_str(), id(),
			graphscript->option_marker_color(),
			graphscript->option_marker_color());
		

	    cmd += GT::format(cmdline.c_str(),
		ix[0]-int(marker_width)*2, iy[0]-int(marker_width)*2,
		ix[0],                     iy[0],
		0);
	    cmd += GT::format(cmdline.c_str(),
		ix[1],                     iy[0]-int(marker_width)*2,
		ix[1]+int(marker_width)*2, iy[0],
		2);
	    cmd += GT::format(cmdline.c_str(),
		ix[0]-int(marker_width)*2, iy[1],
		ix[0],                     iy[1]+int(marker_width)*2,
		6);
	    cmd += GT::format(cmdline.c_str(),
		ix[1],                     iy[1],
		ix[1]+int(marker_width)*2, iy[1]+int(marker_width)*2,
		8);
	}
	break;

	default :
	{
	    int ix[2];
	    int iy[2];
	    coords_frame (cg, marker_width, ix, iy);
	    cmd = GT::format("%s create rectangle %d %d %d %d -tags {node:marker:%s GT:%d marker:%s:GT:%d} -outline %s -width %f",
		device()->name().c_str(),
		ix[0], iy[0], ix[1], iy[1],
		selection.c_str(),
		id(),
		selection.c_str(), id(),
		graphscript->option_marker_color(),
		graphscript->option_marker_width());
	}
	break;
    }
}


void GT_Tk_UINode::make_update_mark_cmd (string& cmd,
    const string& selection,
    Marker_type /* type */)
{
    const GT_Common_Graphics* cg = graphics();
    const GT_Graphscript* graphscript = tk_device()->graphscript();

    //
    // width and marker_width
    //

    double marker_width = graphscript->option_marker_width();

    //
    // Create the command
    //

    switch (the_marker_type) {

	case blocks :
	{
	    double x[2];
	    double y[2];
	    x[0] = cg->x() - (cg->w() + cg->width()) / 2.0;
	    x[1] = cg->x() + (cg->w() + cg->width()) / 2.0;
	    y[0] = cg->y() - (cg->h() + cg->width()) / 2.0;
	    y[1] = cg->y() + (cg->h() + cg->width()) / 2.0;
	    int ix[2];
	    int iy[2];
	    tk_device()->translate (x,y, ix,iy, 2);

	    string cmdline = GT::format (
		"%s coords marker:%s:GT:%d:%%d %%d %%d %%d %%d\n",
		device()->name().c_str(),
		selection.c_str(), id());

	    cmd += GT::format(cmdline.c_str(),
		0,
		ix[0]-int(marker_width)*2, iy[0]-int(marker_width)*2,
		ix[0],                     iy[0]);
	    cmd += GT::format(cmdline.c_str(),
		2,
		ix[1],                     iy[0]-int(marker_width)*2,
		ix[1]+int(marker_width)*2, iy[0]);
	    cmd += GT::format(cmdline.c_str(),
		6,
		ix[0]-int(marker_width)*2, iy[1],
		ix[0],                     iy[1]+int(marker_width)*2);
	    cmd += GT::format(cmdline.c_str(),
		8,
		ix[1],                     iy[1],
		ix[1]+int(marker_width)*2, iy[1]+int(marker_width)*2);
	}
	break;

	default :
	{
	    int ix[2];
	    int iy[2];
	    coords_frame (cg, marker_width, ix, iy);
	    cmd = GT::format("%s coords marker:%s:GT:%d %d %d %d %d",
		device()->name().c_str(),
		selection.c_str(), id(),
		ix[0], iy[0], ix[1], iy[1]);
	}
	break;
    }
}
