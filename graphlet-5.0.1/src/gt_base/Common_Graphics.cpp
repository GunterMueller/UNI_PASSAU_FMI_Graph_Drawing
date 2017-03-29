/* This software is distributed under the Lesser General Public License */
//
// Common_Graphics.cc
//
// This file defines the class GT_Common_Graphics.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Common_Graphics.cpp,v $
// $Author: himsolt $
// $Revision: 1.8 $
// $Date: 1999/07/27 13:17:00 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

#include "Graphlet.h"
#include "Graph.h"
#include "GML.h"


//////////////////////////////////////////
//
// class GT_Common_Graphics
//
//////////////////////////////////////////


//
// Constructor
//

GT_Common_Graphics::GT_Common_Graphics() :
	the_old_center (0.0, 0.0, 0.0, 0.0),
	the_center (0.0, 0.0, 0.0, 0.0)
{
    the_uid = graphlet->id.next_id();

    the_smooth = false;
    the_splinesteps = -1;
    the_extent = 90.0;
    the_start = 0.0;
    the_width = 0.0;

    the_arrowshape_touching_length = 8;
    the_arrowshape_overall_length = 10;
    the_arrowshape_width = 3;

    the_font_size = 0;
}


//
// Destructor
//

GT_Common_Graphics::~GT_Common_Graphics()
{
}


//
// Accessories
//

void GT_Common_Graphics::uid (int u)
{
    the_uid = u;
}


void GT_Common_Graphics::old_center (const GT_Rectangle& r) 
{
    the_old_center = r;
}


//
// Copy & Clone
//



void GT_Common_Graphics::copy (const GT_Common_Graphics* from,
    GT_Copy copy_type)
{
    baseclass::copy (from, copy_type);

    // id, uid and label_uid are *not* copied. If we would do so,
    // consistency could not be guaranteed.
    if (from->the_uid != -1 && (GT_Tagged_Attributes*)from != parent()) {
	uid (graphlet->id.next_id());
    }

    if (copy_test (from, GT_Common_Graphics::tag_geometry, copy_type)) {
	line (from->the_line);
	center (from->the_center);
	the_old_center = the_center;
    }


    if (copy_test (from, GT_Common_Graphics::tag_type, copy_type)) {
	type (from->the_type);
    }
    
    if (copy_test (from, GT_Common_Graphics::tag_fill, copy_type)) {
	fill (from->the_fill);
    }
    if (copy_test (from, GT_Common_Graphics::tag_outline, copy_type)) {
	outline (from->the_outline);
    }
    if (copy_test (from, GT_Common_Graphics::tag_stipple, copy_type)) {
	stipple (from->the_stipple);
    }
    if (copy_test (from, GT_Common_Graphics::tag_anchor, copy_type)) {
	anchor (from->the_anchor);
    }
    if (copy_test (from, GT_Common_Graphics::tag_width, copy_type)) {
	width (from->the_width);
    }

    if (copy_test (from, GT_Common_Graphics::tag_extent, copy_type)) {
	extent (from->the_extent);
    }
    if (copy_test (from, GT_Common_Graphics::tag_start, copy_type)) {
	start (from->the_start);
    }
    if (copy_test (from, GT_Common_Graphics::tag_style, copy_type)) {
	style (from->the_style);
    }

    if (copy_test (from, GT_Common_Graphics::tag_background, copy_type)) {
	background (from->the_background);
    }
    if (copy_test (from, GT_Common_Graphics::tag_foreground, copy_type)) {
	foreground (from->the_foreground);
    }
    if (copy_test (from, GT_Common_Graphics::tag_bitmap, copy_type)) {
	bitmap (from->the_bitmap);
    }

    if (copy_test (from, GT_Common_Graphics::tag_image, copy_type)) {
	image (from->the_image);
    }

    if (copy_test (from, GT_Common_Graphics::tag_arrow, copy_type)) {
	arrow (from->the_arrow);
    }
    if (copy_test (from, GT_Common_Graphics::tag_arrowshape_touching_length,
	copy_type)) {
	arrowshape_touching_length (from->the_arrowshape_touching_length);
    }
    if (copy_test (from, GT_Common_Graphics::tag_arrowshape_overall_length,
	copy_type)) {
	arrowshape_overall_length (from->the_arrowshape_overall_length);
    }
    if (copy_test (from, GT_Common_Graphics::tag_arrowshape_width,
	copy_type)) {
	arrowshape_width (from->the_arrowshape_width);
    }
    if (copy_test (from, GT_Common_Graphics::tag_capstyle, copy_type)) {
	capstyle (from->the_capstyle);
    }
    if (copy_test (from, GT_Common_Graphics::tag_joinstyle, copy_type)) {
	joinstyle (from->the_joinstyle);
    }

    if (copy_test (from, GT_Common_Graphics::tag_smooth, copy_type)) {
	smooth (from->the_smooth);
    }
    if (copy_test (from, GT_Common_Graphics::tag_splinesteps, copy_type)) {
	splinesteps (from->the_splinesteps);
    }

    if (copy_test (from, GT_Common_Graphics::tag_justify, copy_type)) {
	justify (from->the_justify);
    }
    if (copy_test (from, GT_Common_Graphics::tag_font, copy_type)) {
	font (from->the_font);
    }
    if (copy_test (from, GT_Common_Graphics::tag_font_style, copy_type)) {
	font_style (from->the_font_style);
    }
    if (copy_test (from, GT_Common_Graphics::tag_font_size, copy_type)) {
	font_size (from->the_font_size);
    }
}


GT_List_of_Attributes* GT_Common_Graphics::clone (GT_Copy copy_type) const
{
    GT_Common_Graphics* new_graphics = new GT_Common_Graphics;
    new_graphics->copy (this, copy_type);
    return new_graphics;
}


//
// Attribute set
//


	
void GT_Common_Graphics::center (const GT_Rectangle& c)
{
    this->the_center = c;
    set_tagged_attribute (this, tag_geometry,
	&GT_Common_Graphics::the_center);
}


void GT_Common_Graphics::x (const double x)
{
    double x_diff = x - the_center.x();

    the_center.x (x);
    set_tagged_attribute (this, tag_x,
	&GT_Common_Graphics::the_center);
    the_line.move (GT_Point (x_diff, 0.0));
}


void GT_Common_Graphics::y (const double y)
{
    double y_diff = y - the_center.y();

    the_center.y (y);
    set_tagged_attribute (this, tag_y,
	&GT_Common_Graphics::the_center);
    the_line.move (GT_Point (0.0, y_diff));
}


void GT_Common_Graphics::w (const double w)
{
    double w_diff;
    if (the_center.w() != 0) {
	w_diff = w / the_center.w();
    } else {
	w_diff = 1.0;
    }

    the_center.w (w);
    set_tagged_attribute (this, tag_w,
	&GT_Common_Graphics::the_center);

    list<GT_Point>::iterator it;
    for (it = the_line.begin(); it != the_line.end(); ++it) {
	it->x ((it->x() - the_center.x()) * w_diff + the_center.x());
    }
}


void GT_Common_Graphics::h (const double h)
{
    double h_diff;
    if (the_center.h() > 0) {
	h_diff = h / the_center.h();
    } else {
	h_diff = 1.0;
    }

    the_center.h (h);
    set_tagged_attribute (this, tag_h,
	&GT_Common_Graphics::the_center);


    list<GT_Point>::iterator it;
    for (it = the_line.begin(); it != the_line.end(); ++it) {
	it->y ((it->y() - the_center.y()) * h_diff + the_center.y());
    }
}


void GT_Common_Graphics::line (const GT_Polyline& l)
{
    this->the_line = l;
    set_changed (tag_line);
    set_initialized (tag_line);
    set_tagged_attribute (this, tag_line,
  	&GT_Common_Graphics::the_line);
    center (line().bbox());
}


void GT_Common_Graphics::type (GT_Key value)
{
    the_type = value;
    set_tagged_attribute (this, tag_type,
	&GT_Common_Graphics::the_type);
}

void GT_Common_Graphics::fill (GT_Key value)
{
    the_fill = value;
    set_tagged_attribute (this, tag_fill,
	&GT_Common_Graphics::the_fill);
}


void GT_Common_Graphics::outline (GT_Key value)
{
    the_outline = value;
    set_tagged_attribute (this, tag_outline,
	&GT_Common_Graphics::the_outline);
}


void GT_Common_Graphics::stipple (GT_Key value)
{
    the_stipple = value;
    set_tagged_attribute (this, tag_stipple,
	&GT_Common_Graphics::the_stipple);
}


void GT_Common_Graphics::anchor (GT_Key value)
{
    the_anchor = value;
    set_tagged_attribute (this, tag_anchor,
	&GT_Common_Graphics::the_anchor);
}


void GT_Common_Graphics::width (double value)
{
    the_width = value;
    set_tagged_attribute (this, tag_width,
	&GT_Common_Graphics::the_width);
}

void GT_Common_Graphics::extent (double value)
{
    the_extent = value;
    set_tagged_attribute (this, tag_extent,
	&GT_Common_Graphics::the_extent);
}

void GT_Common_Graphics::start (double value)
{
    the_start = value;
    set_tagged_attribute (this, tag_start,
	&GT_Common_Graphics::the_start);
}


void GT_Common_Graphics::style (GT_Key value)
{
    the_style = value;
    set_tagged_attribute (this, tag_style,
	&GT_Common_Graphics::the_style);
}

void GT_Common_Graphics::background (GT_Key value)
{
    the_background = value;
    set_tagged_attribute (this, tag_background,
	&GT_Common_Graphics::the_background);
}


void GT_Common_Graphics::foreground (GT_Key value)
{
    the_foreground = value;
    set_tagged_attribute (this, tag_foreground,
	&GT_Common_Graphics::the_foreground);
}


void GT_Common_Graphics::bitmap (GT_Key value)
{
    the_bitmap = value;
    set_tagged_attribute (this, tag_bitmap,
	&GT_Common_Graphics::the_bitmap);
}


void GT_Common_Graphics::image (GT_Key value)
{
    the_image = value;
    set_tagged_attribute (this, tag_image,
	&GT_Common_Graphics::the_image);
}


void GT_Common_Graphics::arrow (GT_Key value)
{
    the_arrow = value;
    set_tagged_attribute (this, tag_arrow,
	&GT_Common_Graphics::the_arrow);
}


void GT_Common_Graphics::arrowshape_touching_length (double value)
{
    the_arrowshape_touching_length = value;
    set_tagged_attribute (this, tag_arrowshape_touching_length,
	&GT_Common_Graphics::the_arrowshape_touching_length);
}


void GT_Common_Graphics::arrowshape_overall_length (double value)
{
    the_arrowshape_overall_length = value;
    set_tagged_attribute (this, tag_arrowshape_overall_length,
	&GT_Common_Graphics::the_arrowshape_overall_length);
}


void GT_Common_Graphics::arrowshape_width (double value)
{
    the_arrowshape_width = value;
    set_tagged_attribute (this, tag_arrowshape_width,
	&GT_Common_Graphics::the_arrowshape_width);
}


void GT_Common_Graphics::capstyle (GT_Key value)
{
    the_capstyle = value;
    set_tagged_attribute (this, tag_capstyle,
	&GT_Common_Graphics::the_capstyle);
}

void GT_Common_Graphics::joinstyle (GT_Key value)
{
    the_joinstyle = value;
    set_tagged_attribute (this, tag_joinstyle,
	&GT_Common_Graphics::the_joinstyle);
}


void GT_Common_Graphics::smooth (bool value)
{
    the_smooth = value;
    set_tagged_attribute (this, tag_smooth,
	&GT_Common_Graphics::the_smooth);
}


void GT_Common_Graphics::splinesteps (int value)
{
    the_splinesteps = value;
    set_tagged_attribute (this, tag_splinesteps,
	&GT_Common_Graphics::the_splinesteps);
}


void GT_Common_Graphics::justify (GT_Key value)
{
    the_justify = value;
    set_tagged_attribute (this, tag_justify,
	&GT_Common_Graphics::the_justify);
}


void GT_Common_Graphics::font (GT_Key value)
{
    the_font = value;
    set_tagged_attribute (this, tag_font,
	&GT_Common_Graphics::the_font);
}


void GT_Common_Graphics::font_style (GT_Key value)
{
    the_font_style = value;
    set_tagged_attribute (this, tag_font_style,
	&GT_Common_Graphics::the_font_style);
}


void GT_Common_Graphics::font_size (int value)
{
    the_font_size = value;
    set_tagged_attribute (this, tag_font_size,
	&GT_Common_Graphics::the_font_size);
}



//
// extract
//

int GT_Common_Graphics::extract (GT_List_of_Attributes* graphics_list,
    string& /* message */)
{
    GT_List_of_Attributes* center_list;
    if (graphics_list->extract (GT_Keys::center, center_list)) {
		
	double xtr_x;
	if (center_list->extract (GT_Keys::x, xtr_x)) {
	    x (xtr_x);
	}
		
	double xtr_y;
	if (center_list->extract (GT_Keys::y, xtr_y)) {
	    y (xtr_y);
	}
    }

    double xtr_x;
    if (graphics_list->extract (GT_Keys::x, xtr_x)) {
	x (xtr_x);
    }
		
    double xtr_y;
    if (graphics_list->extract (GT_Keys::y, xtr_y)) {
	y (xtr_y);
    }

    double xtr_h;
    if (graphics_list->extract (GT_Keys::h, xtr_h)) {
	h (xtr_h);
    }
	
    double xtr_w;
    if (graphics_list->extract (GT_Keys::w, xtr_w)) {
	w (xtr_w);
    }

    GT_List_of_Attributes* line_list;
    if (graphics_list->extract (GT_Keys::line, line_list)) {

	GT_Polyline xtr_line;

	GT_List_of_Attributes* point_list;
	while (line_list->extract (GT_Keys::point, point_list)) {
				
	    GT_Point p;
					
	    double xtr_x;
	    if (point_list->extract (GT_Keys::x, xtr_x)) {
		p.x (xtr_x);
	    }

	    double xtr_y;
	    if (point_list->extract (GT_Keys::y, xtr_y)) {
		p.y (xtr_y);
	    }
					
	    int xtr_x_int;
	    if (point_list->extract (GT_Keys::x, xtr_x_int)) {
		p.x (xtr_x_int);
	    }

	    int xtr_y_int;
	    if (point_list->extract (GT_Keys::y, xtr_y_int)) {
		p.y (xtr_y_int);
	    }

	    xtr_line.push_back (GT_Point(p));
	}

	line (xtr_line);
    }

    GT_Key xtr_type;
    if (graphics_list->extract (GT_Keys::type, xtr_type)) {
	type (xtr_type);
    }
	
    GT_Key xtr_fill;
    if (graphics_list->extract (GT_Keys::fill, xtr_fill)) {
	fill (xtr_fill);
    }
	
    GT_Key xtr_outline;
    if (graphics_list->extract (GT_Keys::outline, xtr_outline)) {
	outline (xtr_outline);
    }

    GT_Key xtr_stipple;
    if (graphics_list->extract (GT_Keys::stipple, xtr_stipple)) {
	stipple (xtr_stipple);
    }
	
    GT_Key xtr_anchor;
    if (graphics_list->extract (GT_Keys::anchor, xtr_anchor)) {
	anchor (xtr_anchor);
    }
	
    double xtr_width;
    if (graphics_list->extract (GT_Keys::width, xtr_width)) {
	width (xtr_width);
    }
	
    int xtr_width_int;
    if (graphics_list->extract (GT_Keys::width, xtr_width_int)) {
	width (xtr_width_int);
    }
	
    double xtr_extent;
    if (graphics_list->extract (GT_Keys::extent, xtr_extent)) {
	extent (xtr_extent);
    }
	
    int xtr_extent_int;
    if (graphics_list->extract (GT_Keys::extent, xtr_extent_int)) {
	extent (xtr_extent_int);
    }
	
    double xtr_start;
    if (graphics_list->extract (GT_Keys::start, xtr_start)) {
	start (xtr_start);
    }
	
    int xtr_start_int;
    if (graphics_list->extract (GT_Keys::start, xtr_start_int)) {
	start (xtr_start_int);
    }
	
    GT_Key xtr_style;
    if (graphics_list->extract (GT_Keys::style, xtr_style)) {
	style (xtr_style);
    }
	
    GT_Key xtr_background;
    if (graphics_list->extract (GT_Keys::background, xtr_background)) {
	background (xtr_background);
    }

    GT_Key xtr_foreground;
    if (graphics_list->extract (GT_Keys::foreground, xtr_foreground)) {
	foreground (xtr_foreground);
    }
	
    GT_Key xtr_bitmap;
    if (graphics_list->extract (GT_Keys::bitmap, xtr_bitmap)) {
	bitmap (xtr_bitmap);
    }

    GT_Key xtr_image;
    if (graphics_list->extract (GT_Keys::image, xtr_image)) {
	image (xtr_image);
    }
	
    GT_Key xtr_arrow;
    if (graphics_list->extract (GT_Keys::arrow, xtr_arrow)) {
	arrow (xtr_arrow);
    }

    double xtr_arrowshape_touching_length;
    if (graphics_list->extract (GT_Keys::arrowshape_touching_length,
	xtr_arrowshape_touching_length) ||
	graphics_list->extract (
	    GT_Keys::arrowshape_touching_length_alternative,
	    xtr_arrowshape_touching_length)) {
	arrowshape_touching_length (xtr_arrowshape_touching_length);
    }

    double xtr_arrowshape_overall_length;
    if (graphics_list->extract (GT_Keys::arrowshape_overall_length,
	xtr_arrowshape_overall_length) ||
	graphics_list->extract (GT_Keys::arrowshape_overall_length_alternative,
	    xtr_arrowshape_overall_length)) {
	arrowshape_touching_length (xtr_arrowshape_overall_length);
    }

    double xtr_arrowshape_width;
    if (graphics_list->extract (GT_Keys::arrowshape_width,
	xtr_arrowshape_width) ||
	graphics_list->extract (GT_Keys::arrowshape_width_alternative,
	    xtr_arrowshape_width)) {
	arrowshape_touching_length (xtr_arrowshape_width);
    }

    GT_Key xtr_capstyle;
    if (graphics_list->extract (GT_Keys::capstyle, xtr_capstyle)) {
	capstyle (xtr_capstyle);
    }

    GT_Key xtr_joinstyle;
    if (graphics_list->extract (GT_Keys::joinstyle, xtr_joinstyle)) {
	joinstyle (xtr_joinstyle);
    }

    int xtr_smooth;
    if (graphics_list->extract (GT_Keys::smooth, xtr_smooth)) {
	smooth ((xtr_smooth == 0) ? false : true);
    }

    int xtr_splinesteps;
    if (graphics_list->extract (GT_Keys::splinesteps, xtr_splinesteps)) {
	splinesteps (xtr_splinesteps);
    }

    GT_Key xtr_justify;
    if (graphics_list->extract (GT_Keys::justify, xtr_justify)) {
	justify (xtr_justify);
    }

    GT_Key xtr_xfont;
    if (graphics_list->extract (GT_Keys::xfont, xtr_xfont)) {
	font (xtr_xfont);
    }

    GT_Key xtr_font;
    if (graphics_list->extract (GT_Keys::font, xtr_font)) {
	font (xtr_font);
    }

    GT_Key xtr_font_style;
    if (graphics_list->extract (GT_Keys::font_style, xtr_font_style)) {
	font_style (xtr_font_style);
    }

    int xtr_font_size;
    if (graphics_list->extract (GT_Keys::font_size, xtr_font_size)) {
	font_size (xtr_font_size);
    }
	
    return GT_OK;
}



void GT_Common_Graphics::print (ostream& out) const
{
    print_geometry (out);
    print_type (out);

    print_object (out, tag_fill, GT_Keys::fill, fill());
    print_object (out, tag_outline, GT_Keys::outline, outline());
    print_object (out, tag_stipple, GT_Keys::stipple, stipple());
    print_object (out, tag_anchor, GT_Keys::anchor, anchor());
    print_object (out, tag_width, GT_Keys::width, width());
    print_object (out, tag_extent, GT_Keys::extent, extent());
    print_object (out, tag_start, GT_Keys::start, start());
    print_object (out, tag_style, GT_Keys::style, style());
    print_object (out, tag_background, GT_Keys::background, background());
    print_object (out, tag_foreground, GT_Keys::foreground, foreground());
    print_object (out, tag_bitmap, GT_Keys::bitmap, bitmap());
    print_object (out, tag_image, GT_Keys::image, image());
    print_object (out, tag_arrow, GT_Keys::arrow, arrow());
    print_object (out, tag_arrowshape_touching_length,
	GT_Keys::arrowshape_touching_length, arrowshape_touching_length());
    print_object (out, tag_arrowshape_overall_length,
	GT_Keys::arrowshape_overall_length, arrowshape_overall_length());
    print_object (out, tag_arrowshape_width,
	GT_Keys::arrowshape_width, arrowshape_width());
    print_object (out, tag_capstyle, GT_Keys::capstyle, capstyle());
    print_object (out, tag_joinstyle, GT_Keys::joinstyle, joinstyle());
    print_object (out, tag_smooth, GT_Keys::smooth, smooth());
    print_object (out, tag_splinesteps, GT_Keys::splinesteps, splinesteps());
    print_object (out, tag_justify, GT_Keys::justify, justify());
    print_object (out, tag_font, GT_Keys::font, font());
    print_object (out, tag_font_style, GT_Keys::font_style, font_style());
    print_object (out, tag_font_size, GT_Keys::font_size, font_size());

    GT_List_of_Attributes::print (out);
}



void GT_Common_Graphics::print_geometry (ostream& out) const
{
    if (graphlet->gml->version() < 2) {
	if (is_initialized (GT_Common_Graphics::tag_geometry)) {
	    
	    if (type() != GT_Keys::type_line) {
		if (x() > 0.0) {
		    GT_print (out, GT_Keys::x, x());
		}
		if (y() > 0.0) {
		    GT_print (out, GT_Keys::y, y());
		}
		if (w() > 0.0) {
		    if (parent() == 0 ||
			w() != ((GT_Common_Graphics*)parent())->w()) {
			GT_print (out, GT_Keys::w, w());
		    }
		}
		if (h() > 0.0) {
		    if (parent() == 0 ||
			h() != ((GT_Common_Graphics*)parent())->h()) {
			GT_print (out, GT_Keys::h, h());
		    }
		}
	    }
	} else {	
	    print_object (out, tag_line, GT_Keys::line, line());
	}
    }
}


void GT_Common_Graphics::print_type (ostream& out) const
{
    print_object (out, tag_type, GT_Keys::type, type(),
	GT_Keys::type_rectangle);
}



bool GT_Common_Graphics::do_print () const
{
    return baseclass::do_print();
}


void GT_Common_Graphics::reset ()
{
    all_changed (common_graphics_tag_min, common_graphics_tag_max);
}



//
// Utilities
//

void GT_Common_Graphics::move (const GT_Point& move_xy)
{
    the_center.move (move_xy);
    
    if (!the_line.empty()) {
	the_line.move (move_xy);
	the_line.bbox (the_center);
    }
}


void GT_Common_Graphics::scale (double by, const GT_Point& origin)
{
    the_center.scale (by, origin);
    
    if (!the_line.empty()) {
	the_line.scale (by, origin);
	the_line.bbox (the_center);
    }
}


//
// Boundary Box Management
//

void GT_Common_Graphics::bbox (GT_Rectangle& rect) const
{
    rect = the_center;
    rect.expand (the_width / 2.0, the_width / 2.0);
}


GT_Rectangle GT_Common_Graphics::bbox () const
{
    GT_Rectangle rect;
    bbox (rect);
    return rect;
}
