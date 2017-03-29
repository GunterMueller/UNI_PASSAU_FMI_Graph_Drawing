/* This software is distributed under the Lesser General Public License */
//
// Tk_UIEdge.cc
//
// This file implements the class GT_Tk_UIEdge.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UIEdge.cpp,v $
// $Author: himsolt $
// $Revision: 1.9 $
// $Date: 1999/03/05 20:46:31 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

#include "Tcl_Graph.h"
#include "Graphscript.h"

#include "Tk_UIEdge.h"


//////////////////////////////////////////
//
// Constructors and Destructors
//
//////////////////////////////////////////


GT_Tk_UIEdge::GT_Tk_UIEdge (GT_Tk_Device* device,
    GT_Graph& g, const edge e) :
	GT_Tk_UIObject (device),
	the_edge_attrs (&(g.gt(e))),
	the_line_length (0)
{
}


GT_Tk_UIEdge::~GT_Tk_UIEdge ()
{
}



//////////////////////////////////////////
//
// type, uid, graphics
//
//////////////////////////////////////////


const GT_Key& GT_Tk_UIEdge::type() const
{
    return GT_Keys::uiobject_edge;
}


GT_Common_Graphics* GT_Tk_UIEdge::graphics() const
{
    return the_edge_attrs->graphics();
}


int GT_Tk_UIEdge::id () const
{
    return the_edge_attrs->id();
}



//////////////////////////////////////////
//
// make commands
//
//////////////////////////////////////////


void GT_Tk_UIEdge::make_create_cmd (string& cmd )
{
    GT_Common_Graphics* cg = graphics();

    if (!cg->type().active()) {
	graphics()->type (GT_Keys::type_line);		
    }

    if (!cg->arrow().active() && the_edge_attrs->g()->leda().is_directed()) {
	cg->arrow (GT_Keys::anchor_last);
    }
    
    baseclass::make_create_cmd (cmd);
}


void GT_Tk_UIEdge::make_tags_cmd (string& cmd,
    bool /* force */)
{
    cmd = GT::format("%s GT:%d GT:%d",
	type().name().c_str(),
	the_edge_attrs->id(),
	the_edge_attrs->graphics()->uid());
}




bool GT_Tk_UIEdge::update (bool force)
{
    const bool type_changed = graphics()->is_changed(
	GT_Common_Graphics::tag_type);
	
    if (!type_changed) {
	return update_coords (force) && update_attrs (force);
    } else {
	graphics()->reset ();
	int label_uid = the_edge_attrs->label_graphics()->uid();
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


void GT_Tk_UIEdge::make_mark_cmd (string& cmd,
    const string& selection,
    Marker_type /* type */)
{
    const GT_Common_Graphics* cg = graphics();
    const GT_Graphscript* graphscript = tk_device()->graphscript();

    //
    // width and marker_width
    //

    double marker_width = graphscript->option_marker_width();
    int code = Tcl_GetDouble (tk_device()->interp(), "", &marker_width);
    if (code == TCL_ERROR) {
	marker_width = 2.0;
    }

    double width = cg->width ();
    if (width == 0.0) {
	width = 1.0;
    }

    const list<GT_Point>& line = cg->line();

    //
    // Create the command
    //

    switch (the_marker_type) {
	
	case blocks:
	{
	    int point_nr = 0;
	    const list<GT_Point>& line = cg->line();
	    list<GT_Point>::const_iterator point;
	    for (point = line.begin(); point != line.end(); ++point) {
		int ix, iy;
		tk_device()->translate (point->x(), point->y(), ix,iy);
		cmd += GT::format ("%s create rectangle %d %d %d %d -tags {edge:marker:%s GT:%d marker:%s:GT:%d marker:%s:GT:%d:%d} -fill %s -outline %s\n",
		    device()->name().c_str(),
		    ix - int(marker_width),
		    iy - int(marker_width),
		    ix + int(marker_width),
		    iy + int(marker_width),
		    selection.c_str(),
		    id(),
		    selection.c_str(), id(),
		    selection.c_str(), id(), point_nr++,
		    graphscript->option_marker_color(),
		    graphscript->option_marker_color());
	    }
	}
	break;

	default :
	{
	    string coordinates;
	    if (!cg->line().empty()) {	    
		const list<GT_Point>& line = cg->line();
		list<GT_Point>::const_iterator point;
		for (point = line.begin(); point != line.end(); ++point)
		{ 
		    int ix, iy;
		    tk_device()->translate (point->x(), point->y(), ix,iy);
		    coordinates += GT::format (" %d %d", ix, iy);
		}
	    }

	    cmd = GT::format ("%s create line %s -tags {edge:marker:%s GT:%d marker:%s:GT:%d} -fill %s -width %f",
		device()->name().c_str(),
		coordinates.c_str(),
		selection.c_str(),
		id(),
		selection.c_str(), id(),
		graphscript->option_marker_color(),
		width + marker_width);
	    
	    if (cg->arrow().active()) {
		cmd += GT::format (" -arrow {%s}", cg->arrow().name().c_str());
	    } else if (the_edge_attrs->g()->leda().is_directed()) {
		cmd += " -arrow end";
	    }
	    
	    if (cg->is_initialized (GT_Common_Graphics::tag_splinesteps)) {
		cmd += GT::format (" -splinesteps %d", cg->splinesteps());
	    }
	    
	    if (cg->is_initialized (GT_Common_Graphics::tag_smooth)) {
		cmd += GT::format (" -smooth %s",
		    cg->smooth() ? "true" : "false");
	    }
	    
	    if (cg->is_initialized (
		GT_Common_Graphics::tag_arrowshape_touching_length |
		GT_Common_Graphics::tag_arrowshape_overall_length |
		GT_Common_Graphics::tag_arrowshape_width) ||
		device()->scale_x() != 1.0)
	    {
		int arrowshape_touching_length =
		    int(cg->arrowshape_touching_length());
		int arrowshape_overall_length =
		    int(cg->arrowshape_overall_length());
		int arrowshape_width =
		    int(cg->arrowshape_width());
		cmd += GT::format(" -arrowshape {%d %d %d}",
		    int(device()->translate_x (arrowshape_touching_length)),
		    int(device()->translate_x (arrowshape_overall_length)),
		    int(device()->translate_x (arrowshape_width)));
	    }
	}
	break;
    }

    // Save line length to detect changes for updates
    the_line_length = line.size();
}



void GT_Tk_UIEdge::make_update_mark_cmd (string& cmd,
    const string& selection,
    Marker_type /* type */)
{
    const GT_Common_Graphics* cg = graphics();
    const GT_Graphscript* graphscript = tk_device()->graphscript();

    //
    // width and marker_width
    //

    double marker_width = graphscript->option_marker_width();
    int code = Tcl_GetDouble (tk_device()->interp(), "", &marker_width);
    if (code == TCL_ERROR) {
	marker_width = 2.0;
    }

    double width = cg->width ();
    if (width == 0.0) {
	width = 1.0;
    }

    const list<GT_Point>& line = cg->line();

    //
    // Create the command
    //

    switch (the_marker_type) {

// 	case blocks:
// 	{
// 	    cmd += GT::format ("%s delete marker:%s:GT:%d\n",
// 		device()->name().c_str(),
// 		selection.c_str(), id());

// 	    int point_nr = 0;
// 	    const list<GT_Point>& line = cg->line();
// 	    list<GT_Point>::const_iterator point;
// 	    for (point = line.begin(); point != line.end(); ++point) {
// 		int ix, iy;
// 		tk_device()->translate (point->x(), point->y(), ix,iy);
// 		cmd += GT::format ("%s create rectangle %d %d %d %d -tags {edge:marker:%s GT:%d marker:%s:GT:%d marker:%s:GT:%d:%d} -fill %s -outline %s\n",
// 		    device()->name().c_str(),
// 		    ix - int(marker_width),
// 		    iy - int(marker_width),
// 		    ix + int(marker_width),
// 		    iy + int(marker_width),
// 		    selection.c_str(),
// 		    id(),
// 		    selection.c_str(), id(),
// 		    selection.c_str(), id(), point_nr++,
// 		    graphscript->option_marker_color(),
// 		    graphscript->option_marker_color());
// 	    }
// 	}
// 	break;
	case blocks:
	{
	    int point_nr = 0;
	    list<GT_Point>::const_iterator point;
	    if (cg->is_changed (GT_Common_Graphics::tag_geometry)) {

		for (point = line.begin(); point != line.end(); ++point) {

		    int ix, iy;
		    tk_device()->translate (point->x(), point->y(), ix,iy);

		    if (point_nr < the_line_length) {

			cmd += GT::format (
			    "%s coords marker:%s:GT:%d:%d %d %d %d %d\n",
			    device()->name().c_str(),
			    selection.c_str(), id(), point_nr++,
			    ix - int(marker_width),
			    iy - int(marker_width),
			    ix + int(marker_width),
			    iy + int(marker_width));

		    } else {

			cmd += GT::format ("%s create rectangle %d %d %d %d -tags {edge:marker:%s GT:%d marker:%s:GT:%d marker:%s:GT:%d:%d} -fill %s -outline %s\n",
			    device()->name().c_str(),
			    ix - int(marker_width),
			    iy - int(marker_width),
			    ix + int(marker_width),
			    iy + int(marker_width),
			    selection.c_str(),
			    id(),
			    selection.c_str(), id(),
			    selection.c_str(), id(), point_nr++,
			    graphscript->option_marker_color(),
			    graphscript->option_marker_color());

		    }
		}

		while (point_nr < the_line_length) {
		    cmd += GT::format ("%s delete marker:%s:GT:%d:%d\n",
			device()->name().c_str(),
			selection.c_str(), id(), point_nr++);
		}
	    }
	}
	break;

	default:
	{
	    string coordinates;
	    if (!line.empty()) {	    
		list<GT_Point>::const_iterator point;
		for (point = line.begin(); point != line.end(); ++point)
		{ 
		    int ix, iy;
		    tk_device()->translate (point->x(), point->y(), ix,iy);
		    coordinates += GT::format (" %d %d", ix, iy);
		}
	    }

	    if (cg->is_changed (GT_Common_Graphics::tag_geometry)) {
		cmd = GT::format ("%s coords marker:%s:GT:%d %s",
		    device()->name().c_str(),
		    selection.c_str(), id(),
		    coordinates.c_str());
	    }

	    if (cg->is_changed (GT_Common_Graphics::tag_arrow |
		GT_Common_Graphics::tag_width |
		GT_Common_Graphics::tag_smooth |
		GT_Common_Graphics::tag_splinesteps |
		GT_Common_Graphics::tag_arrowshape_touching_length |
		GT_Common_Graphics::tag_arrowshape_overall_length |
		GT_Common_Graphics::tag_arrowshape_width))
	    {
		cmd += GT::format("%c%s itemconfigure marker:%s:GT:%d ",
		    cg->is_changed (
			GT_Common_Graphics::tag_geometry) ? '\n' : ' ',
		    device()->name().c_str(),
		    selection.c_str(), id());

		if (cg->is_changed (GT_Common_Graphics::tag_width)) {
		    cmd += GT::format (" -width %f", width + marker_width);
		}

		if (cg->is_changed (GT_Common_Graphics::tag_arrow)) {
		    cmd += GT::format(" -arrow {%s}",
			cg->arrow().name().c_str());
		}
	
		if (cg->is_changed (GT_Common_Graphics::tag_smooth)) {
		    cmd += GT::format(" -smooth %s",
			cg->smooth() ? "true" : "false");
		}

		if (cg->is_changed (GT_Common_Graphics::tag_splinesteps)) {
		    cmd += GT::format (" -splinesteps %d", cg->splinesteps());
		}
    
		if (cg->is_changed (
		    GT_Common_Graphics::tag_arrowshape_touching_length |
		    GT_Common_Graphics::tag_arrowshape_overall_length |
		    GT_Common_Graphics::tag_arrowshape_width) ||
		    device()->scale_x() != 1.0)
		{
		    int arrowshape_touching_length =
			int(cg->arrowshape_touching_length());
		    int arrowshape_overall_length =
			int(cg->arrowshape_overall_length());
		    int arrowshape_width =
			int(cg->arrowshape_width());
		    cmd += GT::format(" -arrowshape {%d %d %d}",
			int(device()->translate_x(arrowshape_touching_length)),
			int(device()->translate_x(arrowshape_overall_length)),
			int(device()->translate_x(arrowshape_width)));
		}
	    }
	}
	break;
    }

    // Save line length to detect changes for updates
    the_line_length = line.size();
}
