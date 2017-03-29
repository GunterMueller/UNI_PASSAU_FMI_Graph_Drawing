/* This software is distributed under the Lesser General Public License */
//
// Tk_UIObject.cc
//
// This file implements the class GT_Tk_UIObject.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_tcl/Tk_UIObject.cpp,v $
// $Author: himsolt $
// $Revision: 1.13 $
// $Date: 1999/03/05 20:46:45 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet Project
//

// Needto include <tk.h> to get version number
#include <tk.h>

#include "Tcl_Graph.h"
#include "Graphscript.h"

#include "Tk_UIObject.h"

#include <string>

#define GT_INTEGER_COORDINATES

//
// Constructors
//

GT_Tk_UIObject::GT_Tk_UIObject (GT_Tk_Device* const device) :
	GT_UIObject (device),
	the_tk_items (0)
{

}


//
// Destructor
//


GT_Tk_UIObject::~GT_Tk_UIObject ()
{
}



//
// Auxiliary procedures
//


static bool accepts (const GT_Key& type,
    const GT_Common_Graphics::Graphics attribute)
{
    int long accepted = 0;
	
    if (type == GT_Keys::type_arc) {
		
	accepted =
	    GT_Common_Graphics::tag_extent |
	    GT_Common_Graphics::tag_fill |
	    GT_Common_Graphics::tag_outline |
	    GT_Common_Graphics::tag_start |
	    GT_Common_Graphics::tag_stipple |
	    GT_Common_Graphics::tag_style |
	    GT_Common_Graphics::tag_width;
		
    } else if (type == GT_Keys::type_bitmap) {
		
	accepted =
	    GT_Common_Graphics::tag_anchor |
	    GT_Common_Graphics::tag_background |
	    GT_Common_Graphics::tag_foreground |
	    GT_Common_Graphics::tag_bitmap;
		
    } else if (type == GT_Keys::type_image) {

	accepted =
	    GT_Common_Graphics::tag_anchor |
	    GT_Common_Graphics::tag_image;
		
    } else if (type == GT_Keys::type_line) {
		
	accepted =
	    GT_Common_Graphics::tag_arrow |
	    GT_Common_Graphics::tag_arrowshape_touching_length |
	    GT_Common_Graphics::tag_arrowshape_overall_length |
	    GT_Common_Graphics::tag_arrowshape_width |
	    GT_Common_Graphics::tag_capstyle |
	    GT_Common_Graphics::tag_fill |
	    GT_Common_Graphics::tag_joinstyle |
	    GT_Common_Graphics::tag_smooth |
	    GT_Common_Graphics::tag_splinesteps |
	    GT_Common_Graphics::tag_stipple |
	    GT_Common_Graphics::tag_width;
		
    } else if (type == GT_Keys::type_polygon) {

	accepted =
	    GT_Common_Graphics::tag_fill |
	    GT_Common_Graphics::tag_outline |
	    GT_Common_Graphics::tag_smooth |
	    GT_Common_Graphics::tag_splinesteps |
	    GT_Common_Graphics::tag_stipple |
	    GT_Common_Graphics::tag_width;
		
    } else if (type == GT_Keys::type_oval) {

	accepted =
	    GT_Common_Graphics::tag_fill |
	    GT_Common_Graphics::tag_outline |
	    GT_Common_Graphics::tag_stipple |
	    GT_Common_Graphics::tag_width;
		
    } else if (type == GT_Keys::type_rectangle) {

	accepted =
	    GT_Common_Graphics::tag_fill |
	    GT_Common_Graphics::tag_outline |
	    GT_Common_Graphics::tag_stipple |
	    GT_Common_Graphics::tag_width;
		
    } else if (type == GT_Keys::type_text) {

	accepted =
	    GT_Common_Graphics::tag_anchor |
	    GT_Common_Graphics::tag_fill |
	    GT_Common_Graphics::tag_font |
	    GT_Common_Graphics::tag_justify |
	    GT_Common_Graphics::tag_stipple |
	    GT_Common_Graphics::tag_width;
		
    }

    return ((attribute & accepted) != 0);
}


GT_Tk_Device* GT_Tk_UIObject::tk_device () const
{
    return (GT_Tk_Device*) device();
}


//
// Procedures that generate Tcl Commands
//


void GT_Tk_UIObject::make_create_cmd (string& cmd)
{
    const GT_Common_Graphics* cg = graphics();

    //
    // First, set standard type, fill and outline
    //

    if (!cg->is_initialized (GT_Common_Graphics::tag_geometry)) {
	graphics()->x (0.0);
	graphics()->y (0.0);
	graphics()->w (0.0);
	graphics()->h (0.0);
    }

    const GT_Key& type = cg->type().active() ?
	cg->type() :
	GT_Keys::type_rectangle;
    
    if (cg->is_changed (GT_Common_Graphics::tag_type)) {
	graphics()->reset();
    }
    
    //
    // Create the command
    //
    
    if (type == GT_Keys::type_arc) {

	cmd = GT::format ("%s create arc ",
	    device()->name().c_str());
	make_update_coords_cmd (cmd, false);

    } else if (type == GT_Keys::type_bitmap) {
		
	cmd = GT::format("%s create bitmap ",
	    device()->name().c_str());
	make_update_coords_cmd (cmd, false);
	if (cg->bitmap().active()) {
	    cmd += GT::format(" -bitmap %s",
		cg->bitmap().name().c_str());
	}
		
    } else if (type == GT_Keys::type_image) {
		
	cmd = GT::format("%s create image ",
	    device()->name().c_str());
	make_update_coords_cmd (cmd, false);
	if (cg->image().active()) {
	    cmd += GT::format(" -image %s",
		cg->image().name().c_str());
	}
		
    } else if (type == GT_Keys::type_line) {

	cmd = GT::format("%s create line",
	    device()->name().c_str());		
	make_update_coords_cmd (cmd, false);
		
    } else if (type == GT_Keys::type_oval) {

	cmd = GT::format("%s create oval ",
	    device()->name().c_str());
	make_update_coords_cmd (cmd, false);

    } else if (type == GT_Keys::type_polygon) {

	cmd = GT::format("%s create polygon ",
	    device()->name().c_str());
	make_update_coords_cmd (cmd, false);

    } else if (type == GT_Keys::type_text) {

	cmd = GT::format("%s create text ",
	    device()->name().c_str());		
	make_update_coords_cmd (cmd, false);
		
    } else /* assume rectangle */ {

	cmd = GT::format("%s create rectangle ", device()->name().c_str());
	make_update_coords_cmd (cmd, false);
    }

    make_update_attrs_cmd (cmd, true);

    string tags;
    make_tags_cmd (tags, true);
	
    cmd += GT::format(" -tags {%s}", tags.c_str());

}


void GT_Tk_UIObject::make_move_cmd (
    const double x_move,
    const double y_move,
    string& cmd)
{
#ifdef GT_INTEGER_COORDINATES
    int x, y;
    tk_device()->translate (x_move, y_move, x,y);
    cmd = GT::format(
	"%s move GT:%d %d %d",
	device()->name().c_str(),
	id(),
	x, y);
#else
    cmd = GT::format(
	"%s move GT:%d %f %f",
	device()->name().c_str(),
	id(),
	device()->translate_x (x_move),
	device()->translate_y (y_move));
#endif
}


void GT_Tk_UIObject::make_update_coords_cmd (string& command,
    bool may_move_or_scale,
    bool force)
{
    const GT_Common_Graphics* cg = graphics();
    const GT_Key type = cg->type();

    if (!cg->is_changed (GT_Common_Graphics::tag_geometry) && !force) {
	command = "";
	return;
    }

#ifdef GT_OPTIMIZE_SCALE
    // may_move_or_scale = false;
    if (may_move_or_scale && !force &&
	(type != GT_Keys::type_line) &&
	(type != GT_Keys::type_polygon) &&
	cg->center().w() > GT_epsilon &&
	cg->center().h() > GT_epsilon &&
	cg->old_center().w() > GT_epsilon &&
	cg->old_center().h() > GT_epsilon) {
#else
    if (may_move_or_scale && !force &&
	(type != GT_Keys::type_line) &&
	(type != GT_Keys::type_polygon) &&
	fabs (cg->center().w() - cg->old_center().w()) <= GT_epsilon &&
	fabs (cg->center().h() - cg->old_center().h()) <= GT_epsilon) {
#endif
	
	bool new_command_created = false;

#ifdef GT_INTEGER_COORDINATES
	int x, old_x, y, old_y;
	tk_device()->translate (cg->center().x(), cg->center().y(),
	    x, y);
	tk_device()->translate (cg->old_center().x(), cg->old_center().y(),
	    old_x, old_y);
	int dx = x - old_x;
	int dy = y - old_y;
	
	if (abs(dx) > GT_epsilon || abs(dy) > GT_epsilon) {
	    command = GT::format("%s move GT:%d %d %d",
		device()->name().c_str(),
		id(),
		dx, dy);
	    new_command_created = true;
	}
#else
	double dx = device()->translate_x (cg->center().x()) -
	    device()->translate_x (cg->old_center().x());
	double dy = device()->translate_y (cg->center().y()) -
	    device()->translate_y (cg->old_center().y());
	if (fabs(dx) > GT_epsilon || fabs(dy) > GT_epsilon) {
	    command = GT::format("%s move GT:%d %f %f",
		device()->name().c_str(),
		id(),
		dx, dy);
	    new_command_created = true;
	}
	
#endif
	
#ifdef GT_OPTIMIZE_SCALE
#ifdef GT_INTEGER_COORDINATES
	int w, old_w, h, old_h;
	tk_device()->translate (cg->center().w(), cg->center().h(),
	    w, h);
	tk_device()->translate (cg->old_center().w(), cg->old_center().h(),
	    old_w, old_h);
	double scale_x = double(w) / double(old_w);
	double scale_y = double(h) / double(old_h);
	
	if ((fabs(scale_x - 1.0) > GT_epsilon) ||
	    (fabs(scale_y - 1.0) > GT_epsilon)) {
	    
	    command += GT::format("\n%s scale GT:%d %d %d %f %f",
		device()->name().c_str(),
		graphics()->uid(),
		x, y, scale_x, scale_y);
	    new_command_created = true;
	}
#else
	double scale_x = device()->translate_x (cg->center().w()) /
	    device()->translate_x (cg->old_center().w());
	double scale_y = device()->translate_y (cg->center().h()) /
	    device()->translate_y (cg->old_center().h());
	
	if ((fabs(scale_x - 1.0) > GT_epsilon) ||
	    (fabs(scale_y - 1.0) > GT_epsilon)) {
	    
	    command += GT::format("\n%s scale GT:%d %f %f %f %f",
		device()->name().c_str(),
		graphics()->uid(),
		device()->translate_x (cg->center().x()),
		device()->translate_y (cg->center().y()),
		scale_x,
		scale_y);
	    new_command_created = true;
	}
#endif
#endif	
	if (!new_command_created) {
	    command = "";
	}
	    
    } else if (type == GT_Keys::type_arc) {
	    
	double x[2];
	double y[2];
	coords_box (cg->center(), x,y);

#ifdef GT_INTEGER_COORDINATES
	int ix[2];
	int iy[2];
	tk_device()->translate (x,y, ix,iy, 2);
	command += GT::format(" %d %d %d %d ",
	    ix[0], iy[0],
	    ix[1], iy[1]);
#else
	command += GT::format(" %f %f %f %f ",
	    device()->translate_x (x[0]), device()->translate_y (y[0]),
	    device()->translate_x (x[1]), device()->translate_y (y[1]));
#endif
	
    } else if (type == GT_Keys::type_bitmap || type == GT_Keys::type_image) {

#ifdef GT_INTEGER_COORDINATES
	int ix, iy;
	tk_device()->translate (cg->x(), cg->y(), ix,iy);
	command += GT::format(" %d %d ", ix, iy);
#else
	command += GT::format(" %f %f ",
	    device()->translate_x (cg->x()), device()->translate_y (cg->y()));
#endif
	
    } else if (type == GT_Keys::type_line) {

	if (!cg->line().empty()) {
	    
	    GT_Point p;
	    list<GT_Point>::const_iterator it;
	    list<GT_Point>::const_iterator end = cg->line().end();
	    for (it = cg->line().begin(); it != end; ++it)
	    {
		p = *it;
#ifdef GT_INTEGER_COORDINATES
		int ix, iy;
		tk_device()->translate (p.x(), p.y(), ix,iy);
		command += GT::format(" %d %d", ix, iy);
#else
		command += GT::format(" %f %f ",
		    device()->translate_x (p.x()),
		    device()->translate_y (p.y()));
#endif		
	    }
	    
	} else {
	    
	    double x[2];
	    double y[2];
	    coords_box (cg->center(), x,y);

#ifdef GT_INTEGER_COORDINATES
	    int ix[2];
	    int iy[2];
	    tk_device()->translate (x,y, ix,iy, 2);
	    command += GT::format(" %d %d %d %d ",
		ix[0], iy[0],
		ix[1], iy[1]);
#else
	    command += GT::format(" %f %f %f %f ",
		device()->translate_x (x[0]), device()->translate_y (y[0]),
		device()->translate_x (x[1]), device()->translate_y (y[1]));
#endif
	}
	
    } else if (type == GT_Keys::type_oval) {
	
	double x[2];
	double y[2];
	coords_box (cg->center(), x,y);
	
#ifdef GT_INTEGER_COORDINATES
	int ix[2];
	int iy[2];
	tk_device()->translate (x,y, ix,iy, 2);
	command += GT::format(" %d %d %d %d ",
	    ix[0], iy[0],
	    ix[1], iy[1]);
#else
	command += GT::format(" %f %f %f %f ",
	    device()->translate_x (x[0]), device()->translate_y (y[0]),
	    device()->translate_x (x[1]), device()->translate_y (y[1]));
#endif
	
    } else if (type == GT_Keys::type_polygon) {

	if (!cg->line().empty()) {
	    
	    GT_Point p;
	    list<GT_Point>::const_iterator it;
	    list<GT_Point>::const_iterator end = cg->line().end();
	    for (it = cg->line().begin(); it != end; ++it)
	    {
		p = *it;
#ifdef GT_INTEGER_COORDINATES
		int ix;
		int iy;
		tk_device()->translate (p.x(),p.y(), ix,iy);
		command += GT::format(" %d %d", ix,iy);
#else
		command += GT::format(" %f %f ",
		    device()->translate_x (p.x()),
		    device()->translate_y (p.y()));
#endif
	    }

	} else {
	    
	    double x[5];
	    double y[5];
	    coords_diamond (cg->center(), x,y);
	    
#ifdef GT_INTEGER_COORDINATES
	    int ix[5];
	    int iy[5];
	    tk_device()->translate (x,y, ix,iy, 5);
	    command += GT::format(" %d %d %d %d %d %d %d %d %d %d ",
		ix[0], iy[0],
		ix[1], iy[1],
		ix[2], iy[2],
		ix[3], iy[3],
		ix[4], iy[4]);
#else
	    command += GT::format(" %f %f %f %f %f %f %f %f %f %f ",
		device()->translate_x (x[0]), device()->translate_y (y[0]),
		device()->translate_x (x[1]), device()->translate_y (y[1]),
		device()->translate_x (x[2]), device()->translate_y (y[2]),
		device()->translate_x (x[3]), device()->translate_y (y[3]),
		device()->translate_x (x[4]), device()->translate_y (y[4]));
#endif
	}
	
    } else if (type == GT_Keys::type_text) {

#ifdef GT_INTEGER_COORDINATES
	int ix, iy;
	tk_device()->translate (cg->x(), cg->y(), ix,iy);
	command += GT::format(" %d %d ", ix, iy);
#else
	command += GT::format(" %f %f ",
	    device()->translate_x (cg->x()), device()->translate_y (cg->y()));
#endif

    } else /* assume it behaves like a rectangle */ {
	
	double x[2];
	double y[2];
	coords_box (cg->center(), x,y);
	
#ifdef GT_INTEGER_COORDINATES
	int ix[2];
	int iy[2];
	tk_device()->translate (x,y, ix,iy, 2);
	command += GT::format(" %d %d %d %d ",
	    ix[0], iy[0], ix[1], iy[1]);
#else
	command += GT::format(" %f %f %f %f ",
	    device()->translate_x (x[0]), device()->translate_y (y[0]),
	    device()->translate_y (x[1]), device()->translate_y (y[1]));
#endif
    }

}


void GT_Tk_UIObject::make_update_attrs_cmd (string& command,
    bool force)
{
    GT_Common_Graphics* cg = graphics();
    string cmd;

    const GT_Key type = cg->type().active() ?
	cg->type() :
	GT_Keys::type_rectangle;
    
    bool need_update = false;
	
    if (accepts (type, GT_Common_Graphics::tag_fill)) {

	const GT_Key fill = cg->fill();

	if (fill.active()) {
	    if (cg->is_changed (GT_Common_Graphics::tag_fill)) {
		cmd += GT::format (" -fill {%s}",
		    fill.name().c_str());
		need_update = true;
	    }
	} else if (force) {
	    if (type == GT_Keys::type_line ||
		type == GT_Keys::type_text) {
		cmd += GT::format(" -fill {%s}",
		    GT_Keys::black.name().c_str());
	    } else {
		cmd += GT::format(" -fill {%s}",
		    GT_Keys::white.name().c_str());
	    }
	    need_update = true;
	}
    }
    
    if (accepts (type, GT_Common_Graphics::tag_outline)) {

	const GT_Key outline = cg->outline();
		
	if (outline.active()) {
	    if (cg->is_changed (GT_Common_Graphics::tag_outline)) {
		cmd += GT::format (" -outline {%s}",
		    outline.name().c_str());
		need_update = true;
	    }
	} else if (force) {
	    if (type == GT_Keys::type_polygon ||
		type == GT_Keys::type_rectangle ||
		type == GT_Keys::type_arc ||
		type == GT_Keys::type_line ||
		type == GT_Keys::type_oval) {
		cmd += GT::format(" -outline {%s}",
		    GT_Keys::black.name().c_str());
	    } else {
		cmd += GT::format(" -outline {%s}",
		    GT_Keys::white.name().c_str());
	    }
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_stipple) &&
	cg->is_changed (GT_Common_Graphics::tag_stipple)) {

	const GT_Key stipple = cg->stipple();
		
	if (stipple.active()) {

	    cmd += GT::format(" -stipple {%s}",
		stipple.name().c_str());
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_anchor) &&
	cg->is_changed (GT_Common_Graphics::tag_anchor)) {

	const GT_Key anchor = cg->anchor();

	if (cg->anchor().active()) {
		
	    cmd += GT::format (" -anchor {%s}",
		anchor.name().c_str());
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_width) &&
	(cg->is_changed (GT_Common_Graphics::tag_width) || force)) {

	cmd += GT::format(" -width %f", device()->translate_x (cg->width()));
	need_update = true;

    }

    if (accepts (type, GT_Common_Graphics::tag_extent) &&
	cg->is_changed (GT_Common_Graphics::tag_extent)) {

	cmd += GT::format (" -extent %f", cg->extent());
	need_update = true;
		
    }


    if (accepts (type, GT_Common_Graphics::tag_start) &&
	cg->is_changed (GT_Common_Graphics::tag_start)) {
		
	cmd += GT::format (" -start %f", cg->start());
	need_update = true;

    }

    if (accepts (type, GT_Common_Graphics::tag_style) &&
	cg->is_changed (GT_Common_Graphics::tag_style)) {		

	const GT_Key style = cg->style();

	if (cg->style().active()) {
		
	    cmd += GT::format(" -style {%s}",
		style.name().c_str());
	    need_update = true;
	}

    }
	
    if (accepts (type, GT_Common_Graphics::tag_foreground) &&
	cg->is_changed (GT_Common_Graphics::tag_foreground)) {
		
	const GT_Key foreground = cg->foreground();

	if (foreground.active()) {
		
	    cmd += GT::format(" -foreground {%s}",
		foreground.name().c_str());
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_background) &&
	cg->is_changed (GT_Common_Graphics::tag_background)) {
		
	const GT_Key background = cg->background();

	if (background.active()) {
		
	    cmd += GT::format(" -background {%s}",
		background.name().c_str());
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_bitmap) &&
	cg->is_changed (GT_Common_Graphics::tag_bitmap)) {
		
	const GT_Key bitmap = cg->bitmap();

	if (bitmap.active()) {
		
 	    bool exists = tcl_eval (GT::format ("GT::get_image %s",
 		bitmap.name().c_str()));
 	    if (exists) {
		cmd += GT::format(" -bitmap {%s}",
		    bitmap.name().c_str());
 	    }
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_image) &&
	cg->is_changed (GT_Common_Graphics::tag_image)) {
		
	const GT_Key image = cg->image();

	if (image.active()) {
		
 	    bool exists = tcl_eval (GT::format ("GT::get_image %s",
 		image.name().c_str()));
 	    if (exists) {
		cmd += GT::format(" -image {%s}",
		    image.name().c_str());
 	    }
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_arrow) &&
	cg->is_changed (GT_Common_Graphics::tag_arrow)) {
		
	const GT_Key arrow = cg->arrow();

	if (arrow.active()) {
		
	    cmd += GT::format(" -arrow {%s}",
		arrow.name().c_str());
	    need_update = true;
	}

    }
	
    if ((accepts (type, GT_Common_Graphics::tag_arrowshape_touching_length) ||
	accepts (type, GT_Common_Graphics::tag_arrowshape_overall_length) ||
	accepts (type, GT_Common_Graphics::tag_arrowshape_width)) &&
	(
	    cg->is_changed (
		GT_Common_Graphics::tag_arrowshape_touching_length |
		GT_Common_Graphics::tag_arrowshape_overall_length |
		GT_Common_Graphics::tag_arrowshape_width) ||
	    force
	))
    {
	
	double arrowshape_touching_length = cg->arrowshape_touching_length();
	double arrowshape_overall_length = cg->arrowshape_overall_length();
	double arrowshape_width = cg->arrowshape_width();
#ifdef GT_INTEGER_COORDINATES
	cmd += GT::format(" -arrowshape {%d %d %d}",
	    int(device()->translate_x (arrowshape_touching_length)),
	    int(device()->translate_x (arrowshape_overall_length)),
	    int(device()->translate_x (arrowshape_width)));
#else
	cmd += GT::format(" -arrowshape {%f %f %f}",
	    device()->translate_x (arrowshape_touching_length),
	    device()->translate_x (arrowshape_overall_length),
	    device()->translate_x (arrowshape_width));
#endif
	need_update = true;

    }

    if (accepts (type, GT_Common_Graphics::tag_capstyle) &&
	cg->is_changed (GT_Common_Graphics::tag_capstyle)) {
		
	const GT_Key capstyle = cg->capstyle();

	if (capstyle.active()) {
		
	    cmd += GT::format(" -capstyle {%s}",
		capstyle.name().c_str());
	    need_update = true;
	}
    }
	
    if (accepts (type, GT_Common_Graphics::tag_joinstyle) &&
	cg->is_changed (GT_Common_Graphics::tag_joinstyle)) {
		
	const GT_Key joinstyle = cg->joinstyle();

	if (joinstyle.active()) {
		
	    cmd += GT::format(" -joinstyle {%s}",
		joinstyle.name().c_str());
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_smooth) &&
	cg->is_changed (GT_Common_Graphics::tag_smooth)) {
		
	cmd += GT::format(" -smooth %s", cg->smooth() ? "true" : "false");
	need_update = true;

    }

    if (accepts (type, GT_Common_Graphics::tag_splinesteps) &&
	cg->is_changed (GT_Common_Graphics::tag_splinesteps)) {
		
	cmd += GT::format(" -splinesteps %d", cg->splinesteps());
	need_update = true;

    }


    if (accepts (type, GT_Common_Graphics::tag_justify) &&
	cg->is_changed (GT_Common_Graphics::tag_justify)) {
		
	const GT_Key justify = cg->justify();

	if (justify.active()) {
		
	    cmd += GT::format(" -justify {%s}",
		justify.name().c_str());
	    need_update = true;
	}

    }

    if (accepts (type, GT_Common_Graphics::tag_font)
	// We check for the font even if it is not set. This is NECCESSARY
	// for zooming. Note: further optimization could 
	) {
		
	const GT_Key font = cg->font();
	const GT_Key font_style = cg->font_style();
	const int font_size = cg->font_size();

	if (font.active() && font.name()[0] == '-') {

	    cmd += GT::format(" -font {%s}", font.name().c_str());
	    need_update = true;

	} else {

	    const char* font_name;
	    if (font.active()) {
		font_name = font.name().c_str();
	    } else {
		font_name = tk_device()->graphscript()->option_default_font();
	    }

	    const char* font_style_name;
	    if (font_style.active()) {
		font_style_name = font_style.name().c_str();
	    } else {
		font_style_name = tk_device()->graphscript()->
		    option_default_font_style();
	    }

	    int int_font_size;
	    if (font_size > 0) {
		int_font_size = int(device()->translate_y (font_size));
	    } else {
		int_font_size = int(device()->translate_y (
		    tk_device()->graphscript()->option_default_font_size()));
	    }

	    if (font_style.active() || !font.active()) {
		cmd += GT::format (" -font {{%s} %d %s}",
		    font_name,
		    int_font_size,
		    font_style_name);
		need_update = true;
	    } else {
		cmd += GT::format (" -font {{%s} %d}",
		    font_name,
		    int_font_size);
		need_update = true;
	    }
	}
    }
	
    if (need_update) {
	command += cmd;
    }
}


void GT_Tk_UIObject::make_delete_cmd (string& cmd, int uid)
{
    cmd = GT::format("%s delete GT:%d", device()->name().c_str(), uid);
}



//////////////////////////////////////////
//
// Commands for marking the object
//
//////////////////////////////////////////


bool GT_Tk_UIObject::mark (const string& selection,
    GT_UIObject::Marker_type marker)
{
    baseclass::mark (selection, marker);

    string cmd;
    make_mark_cmd (cmd, selection);

    return tcl_eval (cmd);
}


bool GT_Tk_UIObject::update_mark (const string& selection)
{
    if (the_marker_type != unmarked) {
	baseclass::update_mark (selection);
	
	string cmd;
	make_update_mark_cmd (cmd, selection);

	return tcl_eval (cmd);
    } else {
	return true;
    }
}


void GT_Tk_UIObject::make_del_mark_cmd (string& cmd,
    const string& selection)
{
    cmd = GT::format("%s delete marker:%s:GT:%d",
	device()->name().c_str(),
	selection.c_str(),
	id());

}


bool GT_Tk_UIObject::del_mark (const string& selection)
{
    if (the_marker_type != unmarked) {
	baseclass::del_mark (selection);

	string cmd;
	make_del_mark_cmd (cmd, selection);

	return tcl_eval (cmd);
    } else {
	return true;
    }
}




//////////////////////////////////////////
//
// tcl_eval (cmd)
//
// Wrapper for Tcl's Tcl_Eval procedure.
//
//////////////////////////////////////////


bool GT_Tk_UIObject::tcl_eval (const string& cmd)
{
    return tk_device()->graphscript()->tcl_eval (cmd);
}


//////////////////////////////////////////
//
// Tk_UIObject Virtuals ovverides
//
//////////////////////////////////////////


//
// create
//

bool GT_Tk_UIObject::create ()
{
    if (the_tk_items != 0) {
	the_tk_items->clear();
    }

    string cmd;
    make_create_cmd (cmd);

    if (tcl_eval (cmd)) {

	//
	// Must cast here since device points to a generic device
	//
	
	Tcl_Interp* interp = ((GT_Tk_Device*)device())->interp();

	//
	// assume only one item here (needs extension !)
	//
		
	const int it = atoi (interp->result);
	if (the_tk_items == 0) {
	    the_tk_items = new GT_Tk_Id_List;
	}
	the_tk_items->push_back(it);
	return true;
    }

    return false;
}


//
// move
//

bool GT_Tk_UIObject::move (const double move_x, const double move_y)
{
    string cmd;
    make_move_cmd (move_x, move_y, cmd);
    return tcl_eval (cmd);
}


//
// update
//

bool GT_Tk_UIObject::update (bool force)
{
    const bool type_changed = graphics()->is_changed(
	GT_Common_Graphics::tag_type);
	
    if (!type_changed) {
	return update_coords (force) && update_attrs (force);
    } else {
	graphics()->reset ();
	return del () && create ();
    }
}


bool GT_Tk_UIObject::update_attrs (bool force)
{
    string cmd;
    make_update_attrs_cmd (cmd, force);
    if (cmd != "" && the_tk_items != 0) {
	cmd = GT::format ("%s itemconfigure GT:%d ", device()->name().c_str(),
	    graphics()->uid()) + cmd;
	return tcl_eval (cmd);
    } else {
	return true;
    }
}


bool GT_Tk_UIObject::update_coords (bool force)
{
    if (the_tk_items != 0) {
	string cmd = GT::format("%s coords GT:%d ", device()->name().c_str(),
	    graphics()->uid());
	make_update_coords_cmd (cmd, true, force);
	return tcl_eval (cmd);
    } else {
	return true;
    }
}


//
// del
//

bool GT_Tk_UIObject::del ()
{
    string cmd;
	
    make_delete_cmd (cmd, graphics()->uid());
    the_tk_items->clear();
	
    return tcl_eval (cmd);
}



//////////////////////////////////////////
//
// raise
// lower
//
///////////////////////////////////////////

bool GT_Tk_UIObject::raise (const char* tag)
{
    string cmd = GT::format("%s raise GT:%d %s",
	device()->name().c_str(),
	graphics()->uid(),
	tag == 0 ? "" : tag);

    return tcl_eval (cmd);
}


bool GT_Tk_UIObject::lower (const char* tag)
{
    string cmd = GT::format("%s lower GT:%d %s",
	device()->name().c_str(),
	graphics()->uid(),
	tag == 0 ? "" : tag);

    return tcl_eval (cmd);
}

    

//
// Helpers
//


void GT_Tk_UIObject::coords_box (const GT_Rectangle& center,
    double box_x[], double box_y[])
{
    double x = center.x();
    double y = center.y();
    double w = center.w();
    double h = center.h();

    box_x[0] = x - w/2.0;
    box_y[0] = y - h/2.0;
    box_x[1] = x + w/2.0;
    box_y[1] = y + h/2.0;
}


void GT_Tk_UIObject::coords_diamond (const GT_Rectangle& center,
    double diamond_x[], double diamond_y[])
{    
    double x = center.x();
    double y = center.y();
    double w = center.w();
    double h = center.h();

    diamond_x[0] = x;
    diamond_y[0] = y - h/2.0;
    diamond_x[1] = x + w/2.0;
    diamond_y[1] = y;
    diamond_x[2] = x;
    diamond_y[2] = y + h/2.0;
    diamond_x[3] = x - w/2.0;
    diamond_y[3] = y;
    diamond_x[4] = diamond_x[0];
    diamond_y[4] = diamond_y[0];
}
