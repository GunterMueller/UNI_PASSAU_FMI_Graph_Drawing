/* This software is distributed under the Lesser General Public License */
#ifndef GT_COMMON_GRAPHICS_H
#define GT_COMMON_GRAPHICS_H

//
// Common_Graphics.h
//
// This file defines the class GT_Common_Graphics.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Common_Graphics.h,v $
// $Author: himsolt $
// $Revision: 1.5 $
// $Date: 1999/03/05 20:43:21 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//


//////////////////////////////////////////
//
// class GT_Common_Graphics
//
//////////////////////////////////////////


//
// The following include statements are WRONG. But MSVC++ has a BUG.
//

#ifndef GT_ATTRIBUTES_H
#include "Attributes.h"
#endif

#ifndef GT_TAGGED_ATTRIBUTES_H
#include "Tagged_Attributes.h"
#endif

#ifndef GT_POINT_H
#include "Point.h"
#endif

#ifndef GT_RECTANGLE_H
#include "Rectangle.h"
#endif

#ifndef GT_POLYLINE_H
#include "Polyline.h"
#endif


class GT_Common_Graphics : public GT_Tagged_Attributes
{

    GT_CLASS (GT_Common_Graphics, GT_Tagged_Attributes);

private:
	
    int the_uid;

    GT_Rectangle the_old_center;
    GT_Rectangle the_center;
    GT_Polyline the_line;

    // Tk Graphics
	
    GT_Key the_type;
	
    // Global
	
    GT_Key the_fill;
    GT_Key the_outline;
    GT_Key the_stipple;
    GT_Key the_anchor;
    double the_width;

    //
    // Arc
    //
	
    double the_extent;
    double the_start;
    GT_Key the_style;

    //
    // Bitmap
    //
	
    GT_Key the_background;
    GT_Key the_foreground;
    GT_Key the_bitmap;

    //
    // Image
    //
	
    GT_Key the_image;

    //
    // Line
    //
	
    GT_Key the_arrow;
    double the_arrowshape_touching_length;
    double the_arrowshape_overall_length;
    double the_arrowshape_width;
    GT_Key the_capstyle;
    GT_Key the_joinstyle;

    //
    // Polygon
    //
	
    bool the_smooth;
    int the_splinesteps;

    //
    // Text
    //
	
    GT_Key the_justify;
    GT_Key the_font;
    GT_Key the_font_style;
    int the_font_size;

	
public:
	
    GT_Common_Graphics();
    virtual ~GT_Common_Graphics();

    // Accessories

    inline int uid () const;
    virtual void uid (int u);

    inline const GT_Rectangle& old_center() const;
    virtual void old_center (const GT_Rectangle&);

    inline const GT_Rectangle& center() const;
    virtual void center (const GT_Rectangle&);

    inline const GT_Polyline& line() const;
    virtual void line (const GT_Polyline&);

    inline GT_Key type() const;
    virtual void type (GT_Key);

    inline GT_Key fill() const;
    virtual void fill (GT_Key);

    inline GT_Key outline() const;
    virtual void outline (GT_Key);

    inline GT_Key stipple() const;
    virtual void stipple (GT_Key);

    inline GT_Key anchor() const;
    virtual void anchor (GT_Key);
    
    inline double width() const;
    virtual void width (double);

    inline double extent() const;
    virtual void extent (double);
    
    inline double start() const;
    virtual void start (double);

    inline GT_Key style() const;
    virtual void style (GT_Key);

    inline GT_Key background() const;
    virtual void background (GT_Key);

    inline GT_Key foreground() const;
    virtual void foreground (GT_Key);

    inline GT_Key bitmap() const;
    virtual void bitmap (GT_Key);

    inline GT_Key image() const;
    virtual void image (GT_Key);

    inline GT_Key arrow() const;
    virtual void arrow (GT_Key);

    inline double arrowshape_touching_length() const;
    virtual void arrowshape_touching_length (double);

    inline double arrowshape_overall_length() const;
    virtual void arrowshape_overall_length (double);

    inline double arrowshape_width() const;
    virtual void arrowshape_width (double);

    inline GT_Key capstyle() const;
    virtual void capstyle (GT_Key);

    inline GT_Key joinstyle() const;
    virtual void joinstyle (GT_Key);

    inline bool smooth() const;
    virtual void smooth (bool);

    inline int splinesteps() const;
    virtual void splinesteps (int);

    inline GT_Key justify() const;
    virtual void justify (GT_Key);

    inline GT_Key font() const;
    virtual void font (GT_Key);

    inline GT_Key font_style() const;
    virtual void font_style (GT_Key);

    inline int font_size() const;
    virtual void font_size (int);
   
    //
    // convenient x/y/w/h access
    //
	
    inline double x () const;
    void x (const double x);
    inline double y () const;
    void y (const double y);
    inline double w () const;
    void w (const double w);
    inline double h () const;	
    void h (const double h);

    // virtual copy constructor
    void copy (const GT_Common_Graphics* from,
	GT_Copy type);
    virtual GT_List_of_Attributes* clone (GT_Copy type) const;
    
    //
    // extract
    //
	
    virtual int extract (GT_List_of_Attributes* list, string& message);

    //
    // print
    //
    
    virtual void print (ostream& out) const;
    virtual void print_geometry (ostream& out) const;
    virtual void print_type (ostream& out) const;
    virtual bool do_print () const;

    //
    // reset
    //

    virtual void reset ();

    //
    // Utilities
    //

    virtual void move (const GT_Point& move_xy);
    virtual void scale (double by, const GT_Point& origin);

    virtual void bbox(GT_Rectangle& bbox) const;
    GT_Rectangle bbox() const;    

    //
    // Tags
    //

    enum Graphics {

	tag_x = 1,
	tag_y = (tag_x<<1),
	tag_w = (tag_y<<1),
	tag_h = (tag_w<<1),
	tag_geometry = (tag_x | tag_y | tag_w | tag_h),
	tag_type = (tag_h<<1),

	tag_anchor = (tag_type<<1),
	tag_arrow = (tag_anchor<<1),
	tag_arrowshape_touching_length = (tag_arrow<<1),
	tag_arrowshape_overall_length = (tag_arrowshape_touching_length<<1),
	tag_arrowshape_width = (tag_arrowshape_overall_length<<1),
	tag_background = (tag_arrowshape_width<<1),
	tag_bitmap = (tag_background<<1),
	tag_capstyle = (tag_bitmap<<1),
	tag_extent = (tag_capstyle<<1),
	tag_fill = (tag_extent<<1),
	tag_font = (tag_fill<<1),
	tag_font_size = (tag_font<<1),
	tag_font_style= (tag_font_size<<1),
	tag_foreground = (tag_font_style<<1),
	tag_image = (tag_foreground<<1),
	tag_joinstyle = (tag_image<<1),
	tag_justify = (tag_joinstyle<<1),
	tag_line = (tag_justify<<1),
	tag_outline = (tag_line<<1),
	tag_smooth = (tag_outline<<1),
	tag_splinesteps = (tag_smooth<<1),
	tag_start = (tag_splinesteps<<1),
	tag_stipple = (tag_start<<1),
	tag_style = (tag_stipple<<1),
	tag_width = (tag_style<<1),

	common_graphics_tag_min = tag_x,
	common_graphics_tag_max = tag_width
    };

};



//
// Accessories
//

inline int GT_Common_Graphics::uid () const
{
    return the_uid;
}


inline const GT_Rectangle& GT_Common_Graphics::old_center () const
{
    return the_old_center;
}


inline const GT_Rectangle& GT_Common_Graphics::center () const
{
    return the_center;
}


inline const GT_Polyline& GT_Common_Graphics::line () const
{
    return the_line;
}


inline GT_Key GT_Common_Graphics::type() const
{
    return the_type;
}


inline GT_Key GT_Common_Graphics::fill() const
{
    return the_fill;
}


inline GT_Key GT_Common_Graphics::outline() const
{
    return the_outline;
}


inline GT_Key GT_Common_Graphics::stipple() const
{
    return the_stipple;
}


inline GT_Key GT_Common_Graphics::anchor() const
{
    return the_anchor;
}


inline double GT_Common_Graphics::width() const
{
    return the_width;
}


inline double GT_Common_Graphics::extent() const
{
    return the_extent;
}


inline double GT_Common_Graphics::start() const
{
    return the_start;
}


inline GT_Key GT_Common_Graphics::style() const
{
    return the_style;
}


inline GT_Key GT_Common_Graphics::background() const
{
    return the_background;
}


inline GT_Key GT_Common_Graphics::foreground() const
{
    return the_foreground;
}


inline GT_Key GT_Common_Graphics::bitmap() const
{
    return the_bitmap;
}


inline GT_Key GT_Common_Graphics::image() const
{
    return the_image;
}


inline GT_Key GT_Common_Graphics::arrow() const
{
    return the_arrow;
}


inline double GT_Common_Graphics::arrowshape_touching_length() const
{
    return the_arrowshape_touching_length;
}


inline double GT_Common_Graphics::arrowshape_overall_length() const
{
    return the_arrowshape_overall_length;
}


inline double GT_Common_Graphics::arrowshape_width() const
{
    return the_arrowshape_width;
}


inline GT_Key GT_Common_Graphics::capstyle() const
{
    return the_capstyle;
}


inline GT_Key GT_Common_Graphics::joinstyle() const
{
    return the_joinstyle;
}


inline bool GT_Common_Graphics::smooth() const
{
    return the_smooth;
}


inline int GT_Common_Graphics::splinesteps() const
{
    return the_splinesteps;
}


inline GT_Key GT_Common_Graphics::justify() const
{
    return the_justify;
}


inline GT_Key GT_Common_Graphics::font() const
{
    return the_font;
}


inline GT_Key GT_Common_Graphics::font_style() const
{
    return the_font_style;
}


inline int GT_Common_Graphics::font_size() const
{
    return the_font_size;
}



//
// x/y/w/h direct access
//
	
inline double GT_Common_Graphics::x () const
{
    return the_center.x();
}


inline double GT_Common_Graphics::y () const
{
    return the_center.y();
}


inline double GT_Common_Graphics::w () const
{
    return the_center.w();
}


inline double GT_Common_Graphics::h () const
{
    return the_center.h();
}


#endif
