/* This software is distributed under the Lesser General Public License */
//
// Graph.cc
//
// This module implements the class GT_Keys.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Keys.cpp,v $
// $Author: himsolt $
// $Revision: 1.7 $
// $Date: 1999/06/09 11:12:48 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, Graphlet Project
//

#include "Graphlet.h"


//
// Constructor & Destructor
//

GT_Keys::GT_Keys()
{
}



GT_Keys::~GT_Keys()
{
}


//
// Initialization of Keys
//

typedef struct {
    GT_Key* key; const char* value;
} Keys_init;


static Keys_init keys_init[] = {

    //
    // Management
    //

    { &GT_Keys::def, "default" },
    { &GT_Keys::empty, "" },

    //
    // General
    //
    
    { &GT_Keys::graph, "graph" },
    { &GT_Keys::node, "node" },
    { &GT_Keys::edge, "edge" },
    { &GT_Keys::graphics, "graphics" },
    { &GT_Keys::label_graphics, "LabelGraphics" },

    { &GT_Keys::version, "version" },
    { &GT_Keys::option_version, "-version" },

    { &GT_Keys::creator, "Creator" },
    { &GT_Keys::option_creator, "-creator" },

    { &GT_Keys::id, "id" },
    { &GT_Keys::option_id, "-id" },

    { &GT_Keys::uid, "uid" },
    { &GT_Keys::option_uid, "-uid" },

    { &GT_Keys::label_uid, "labelUid" },
    { &GT_Keys::option_label_uid, "-label_uid" },

    { &GT_Keys::name, "name" },
    { &GT_Keys::option_name, "-name" },

    { &GT_Keys::label, "label" },
    { &GT_Keys::option_label, "-label" },

    { &GT_Keys::center, "center" },
    { &GT_Keys::option_center, "-center" },

    { &GT_Keys::directed, "directed" },
    { &GT_Keys::option_directed, "-directed" },

    { &GT_Keys::source_port, "sourcePort" },
    { &GT_Keys::source_port_alternative, "source_port" },
    { &GT_Keys::option_source_port, "-source_port" },
    { &GT_Keys::target_port, "targetPort" },
    { &GT_Keys::target_port_alternative, "target_port" },
    { &GT_Keys::option_target_port, "-target_port" },
    { &GT_Keys::port, "port" },
    { &GT_Keys::option_port, "port" },
    { &GT_Keys::ports, "ports" },
    { &GT_Keys::option_ports, "-ports" },

    { &GT_Keys::label_anchor_bend, "labelAnchorBend" },
    { &GT_Keys::label_anchor_bend_alternative, "label_anchor_bend" },
    { &GT_Keys::option_label_anchor_bend, "-label_anchor_bend" },
    { &GT_Keys::label_anchor_x, "labelAnchorX" },
    { &GT_Keys::label_anchor_x_alternative, "label_anchor_x" },
    { &GT_Keys::option_label_anchor_x, "-label_anchor_x" },
    { &GT_Keys::label_anchor_y, "labelAnchorY" },
    { &GT_Keys::label_anchor_y_alternative, "label)_anchor_y" },
    { &GT_Keys::option_label_anchor_y, "-label_anchor_y" },

    //
    // Graphics
    //
	
    { &GT_Keys::type, "type" },
    { &GT_Keys::option_type, "-type" },

    { &GT_Keys::type_arc, "arc" },
    { &GT_Keys::type_bitmap, "bitmap" },
    { &GT_Keys::type_image, "image" },
    { &GT_Keys::type_line, "line" },
    { &GT_Keys::type_oval, "oval" },
    { &GT_Keys::type_polygon, "polygon" },
    { &GT_Keys::type_rectangle, "rectangle" },
    { &GT_Keys::type_text, "text" },

    { &GT_Keys::x, "x" },
    { &GT_Keys::y, "y" },
    { &GT_Keys::w, "w" },
    { &GT_Keys::h, "h" },

    { &GT_Keys::option_w, "-w" },
    { &GT_Keys::option_h, "-h" },
    { &GT_Keys::option_x, "-x" },
    { &GT_Keys::option_y, "-y" },

    { &GT_Keys::anchor, "anchor" },
    { &GT_Keys::arrow, "arrow" },
    { &GT_Keys::arrowshape, "arrowshape" },
    { &GT_Keys::arrowshape_touching_length, "arrowshapeTouchingLength" },
    { &GT_Keys::arrowshape_touching_length_alternative,
      "arrowshape_touching_length" },
    { &GT_Keys::option_arrowshape_touching_length,
      "-arrowshape_touching_length" },
    { &GT_Keys::arrowshape_overall_length, "arrowshapeOverallLength" },
    { &GT_Keys::arrowshape_overall_length_alternative,
      "arrowshape_overall_length" },
    { &GT_Keys::option_arrowshape_overall_length,
      "-arrowshape_overall_length" },
    { &GT_Keys::arrowshape_width, "arrowshapeWidth" },
    { &GT_Keys::arrowshape_width_alternative, "arrowshape_width" },
    { &GT_Keys::option_arrowshape_width, "-arrowshape_width" },
    { &GT_Keys::background, "background" },
    { &GT_Keys::bitmap, "bitmap" },
    { &GT_Keys::capstyle, "capstyle" },
    { &GT_Keys::extent, "extent" },
    { &GT_Keys::fill, "fill" },
    { &GT_Keys::foreground, "foreground" },
    { &GT_Keys::image, "image" },
    { &GT_Keys::joinstyle, "joinstyle" },
    { &GT_Keys::justify, "justify" },
    { &GT_Keys::line, "Line" },
    { &GT_Keys::outline, "outline" },
    { &GT_Keys::point, "point" },
    { &GT_Keys::smooth, "smooth" },
    { &GT_Keys::source, "source" },
    { &GT_Keys::splinesteps, "splinesteps" },
    { &GT_Keys::start, "start" },
    { &GT_Keys::stipple, "stipple" },
    { &GT_Keys::style, "style" },
    { &GT_Keys::target, "target" },
    { &GT_Keys::visible, "visible" },
    { &GT_Keys::width, "width" },
    { &GT_Keys::xfont, "xfont" },
    { &GT_Keys::font, "font" },
    { &GT_Keys::font_style, "fontstyle" },
    { &GT_Keys::font_size, "fontsize" },
	
    // Walter
    { &GT_Keys::edge_anchor, "edgeAnchor" },
    { &GT_Keys::label_anchor, "labelAnchor" },

    { &GT_Keys::option_anchor, "-anchor" },
    { &GT_Keys::option_arrow, "-arrow" },
    { &GT_Keys::option_arrowshape, "-arrowshape" },
    { &GT_Keys::option_background, "-background" },
    { &GT_Keys::option_bitmap, "-bitmap" },
    { &GT_Keys::option_capstyle, "-capstyle" },
    { &GT_Keys::option_extent, "-extent" },
    { &GT_Keys::option_fill, "-fill" },
    { &GT_Keys::option_foreground, "-foreground" },
    { &GT_Keys::option_image, "-image" },
    { &GT_Keys::option_joinstyle, "-joinstyle" },
    { &GT_Keys::option_justify, "-justify" },
    { &GT_Keys::option_line, "-line" },
    { &GT_Keys::option_line_relative, "-line_relative" },
    { &GT_Keys::option_outline, "-outline" },
    { &GT_Keys::option_point, "-point" },
    { &GT_Keys::option_smooth, "-smooth" },
    { &GT_Keys::option_source, "-source" },
    { &GT_Keys::option_splinesteps, "-splinesteps" },
    { &GT_Keys::option_start, "-start" },
    { &GT_Keys::option_stipple, "-stipple" },
    { &GT_Keys::option_style, "-style" },
    { &GT_Keys::option_target, "-target" },
    { &GT_Keys::option_visible, "-visible" },
    { &GT_Keys::option_width, "-width" },
    { &GT_Keys::option_xfont, "-xfont" },
    { &GT_Keys::option_font, "-font" },
    { &GT_Keys::option_font_style, "-font_style" },
    { &GT_Keys::option_font_size, "-font_size" },
	
    // Walter
    { &GT_Keys::option_edge_anchor, "-edge_anchor" },
    { &GT_Keys::option_label_anchor, "-label_anchor" },

    //
    // Walter: NEI
    //

    { &GT_Keys::edge_anchor, "edgeAnchor" },
    { &GT_Keys::delta_x_source, "xSource" },
    { &GT_Keys::delta_y_source, "ySource" },
    { &GT_Keys::delta_x_target, "xTarget" },
    { &GT_Keys::delta_y_target, "yTarget" },
    { &GT_Keys::source_function, "sourceFunction" },
    { &GT_Keys::target_function, "targetFunction" },
    { &GT_Keys::default_function, "defaultFunction" },

    { &GT_Keys::option_edge_anchor, "-edge_anchor" },

    { &GT_Keys::option_delta_x_source, "-delta_x_source" },
    { &GT_Keys::option_delta_y_source, "-delta_y_source" },
    { &GT_Keys::option_delta_x_target, "-delta_x_target" },
    { &GT_Keys::option_delta_y_target, "-delta_y_target" },

    { &GT_Keys::option_source_function, "-source_function" },
    { &GT_Keys::option_target_function, "-target_function" },
    { &GT_Keys::option_default_function, "-default_function" },
    
    // NEI-functions
    { &GT_Keys::empty_function, "None" },
    { &GT_Keys::EA_next_corner, "Next corner" },
    { &GT_Keys::EA_next_middle, "Next middle" },
    { &GT_Keys::EA_orthogonal, "Orthogonal" },
    { &GT_Keys::EA_connect_corner_shortest, "Connect corners" },
    { &GT_Keys::EA_connect_middle_shortest, "Connect middles" },
    { &GT_Keys::EA_connect_orthogonal, "Connect orthogonal" },

    //
    // Anchors
    //
	
    { &GT_Keys::anchor_center, "c" },
    { &GT_Keys::anchor_n, "n" },
    { &GT_Keys::anchor_ne, "ne" },
    { &GT_Keys::anchor_e, "e" },
    { &GT_Keys::anchor_se, "se" },
    { &GT_Keys::anchor_s, "s" },
    { &GT_Keys::anchor_sw, "sw" },
    { &GT_Keys::anchor_w, "w" },
    { &GT_Keys::anchor_nw, "nw" },

    { &GT_Keys::anchor_first, "first" },
    { &GT_Keys::anchor_none, "none" },
    { &GT_Keys::anchor_last, "last" },
    { &GT_Keys::anchor_bend, "bend" },
    
    { &GT_Keys::anchor_clip, "clip" },
    { &GT_Keys::anchor_corners, "corners" },
    { &GT_Keys::anchor_middle, "middle" },
    { &GT_Keys::anchor_8, "corner_8" },

    //
    // Keys for internals
    //
	
    { &GT_Keys::graph_attrs, "@graph_attrs" },
    { &GT_Keys::node_attrs, "@node_attrs" },
    { &GT_Keys::edge_attrs, "@edge_attrs" },
	
    //
    // Keys for paths (optional)
    //
	
    { &GT_Keys::graphics_center_x, ".graphics.center.x" },
    { &GT_Keys::graphics_center_y, ".graphics.center.y" },
    { &GT_Keys::graphics_w, ".graphics.w" },
    { &GT_Keys::graphics_h, ".graphics.h" },
    { &GT_Keys::graphics_image, ".graphics.image" },


    //
    // Types of UIObject's
    //
	
    { &GT_Keys::uiobject_unknown, "unknown" },
    { &GT_Keys::uiobject_node, "node:node" },
    { &GT_Keys::uiobject_edge, "edge:edge" },
    { &GT_Keys::uiobject_graph, "graph:graph" },
    { &GT_Keys::uiobject_label, "label" },
    { &GT_Keys::uiobject_node_label, "node:label" },
    { &GT_Keys::uiobject_edge_label, "edge:label" },
    { &GT_Keys::uiobject_graph_label, "graph:label" },

    //
    // Color names
    //

    { &GT_Keys::white, "#FFFFFF" },
    { &GT_Keys::black, "#000000" },
    { &GT_Keys::red, "#FF0000" },
    { &GT_Keys::green, "#00FF00" },
    { &GT_Keys::blue, "#0000FF" },


    //
    // Style keys
    //

    { &GT_Keys::style_pieslice, "pieslice" },
    { &GT_Keys::style_chord, "chord" },
    { &GT_Keys::style_arc, "arc" },

    //
    // Arrow keys
    //

    { &GT_Keys::arrow_none, "none" },
    { &GT_Keys::arrow_first, "first" },
    { &GT_Keys::arrow_last, "last" },
    { &GT_Keys::arrow_both, "both" },

    //
    // Handlers
    //

    { &GT_Keys::pre_new_graph_handler, "pre_new_graph_handler" },
    { &GT_Keys::post_new_graph_handler, "post_new_graph_handler" },
    { &GT_Keys::pre_clear_handler, "pre_clear_handler" },
    { &GT_Keys::post_clear_handler, "post_clear_handler" },

    { &GT_Keys::pre_new_node_handler, "pre_new_node_handler" },
    { &GT_Keys::post_new_node_handler, "post_new_node_handler" },
    { &GT_Keys::pre_del_node_handler, "pre_del_node_handler" },
    { &GT_Keys::post_del_node_handler, "post_del_node_handler" },

    { &GT_Keys::pre_new_edge_handler, "pre_new_edge_handler" },
    { &GT_Keys::post_new_edge_handler, "post_new_edge_handler" },
    { &GT_Keys::pre_del_edge_handler, "pre_del_edge_handler" },
    { &GT_Keys::post_del_edge_handler, "post_del_edge_handler" },
 
    { &GT_Keys::pre_move_edge_handler, "pre_move_edge_handler" },
    { &GT_Keys::post_move_edge_handler, "post_move_edge_handler" },
    { &GT_Keys::pre_hide_edge_handler, "pre_hide_edge_handler" },
    { &GT_Keys::post_hide_edge_handler, "post_hide_edge_handler" },
    { &GT_Keys::pre_restore_edge_handler, "pre_restore_edge_handler" },
    { &GT_Keys::post_restore_edge_handler, "post_restore_edge_handler" },

    { &GT_Keys::touch_node_handler, "touch_node_handler" },
    { &GT_Keys::touch_edge_handler, "touch_edge_handler" },
    { &GT_Keys::comment_handler, "comment_handler" },
    { &GT_Keys::query_handler, "query_handler" },
    { &GT_Keys::query_node_node_handler, "query_node_node_handler" },
    { &GT_Keys::query_edge_handler, "query_edge_handler" },

    { &GT_Keys::default_node_style, "default_node_style" },
    { &GT_Keys::default_edge_style, "default_edge_style" },

    { &GT_Keys::node_style, "node_style" },
    { &GT_Keys::edge_style, "edge_style" },

    { 0,0 }
};


GT_Key GT_Keys::def;
GT_Key GT_Keys::undefined (0);
GT_Key GT_Keys::empty;

GT_Key GT_Keys::graph;
GT_Key GT_Keys::node;
GT_Key GT_Keys::edge;
GT_Key GT_Keys::graphics;
GT_Key GT_Keys::label_graphics;

GT_Key GT_Keys::version,	GT_Keys::option_version;
GT_Key GT_Keys::creator,	GT_Keys::option_creator;
GT_Key GT_Keys::id,		GT_Keys::option_id;
GT_Key GT_Keys::uid,		GT_Keys::option_uid;
GT_Key GT_Keys::label_uid,	GT_Keys::option_label_uid;
GT_Key GT_Keys::name, 		GT_Keys::option_name;
GT_Key GT_Keys::label, 		GT_Keys::option_label;
GT_Key GT_Keys::center,		GT_Keys::option_center;
GT_Key GT_Keys::directed,	GT_Keys::option_directed;

GT_Key GT_Keys::source_port;
GT_Key GT_Keys::source_port_alternative;
GT_Key GT_Keys::option_source_port;
GT_Key GT_Keys::target_port;
GT_Key GT_Keys::target_port_alternative;
GT_Key GT_Keys::option_target_port;
GT_Key GT_Keys::ports,          GT_Keys::option_ports;
GT_Key GT_Keys::port,           GT_Keys::option_port;

GT_Key GT_Keys::label_anchor_bend;
GT_Key GT_Keys::label_anchor_bend_alternative;
GT_Key GT_Keys::option_label_anchor_bend;
GT_Key GT_Keys::label_anchor_x;
GT_Key GT_Keys::label_anchor_x_alternative;
GT_Key GT_Keys::option_label_anchor_x;
GT_Key GT_Keys::label_anchor_y;
GT_Key GT_Keys::label_anchor_y_alternative;
GT_Key GT_Keys::option_label_anchor_y;

GT_Key GT_Keys::type,		GT_Keys::option_type;
GT_Key GT_Keys::x,		GT_Keys::option_x;
GT_Key GT_Keys::y,		GT_Keys::option_y;
GT_Key GT_Keys::w,		GT_Keys::option_w;
GT_Key GT_Keys::h,		GT_Keys::option_h;

GT_Key GT_Keys::anchor,		GT_Keys::option_anchor;
GT_Key GT_Keys::arrow,		GT_Keys::option_arrow;
GT_Key GT_Keys::arrowshape,	GT_Keys::option_arrowshape;
GT_Key GT_Keys::arrowshape_touching_length;
GT_Key GT_Keys::arrowshape_touching_length_alternative;
GT_Key GT_Keys::option_arrowshape_touching_length;
GT_Key GT_Keys::arrowshape_overall_length;
GT_Key GT_Keys::arrowshape_overall_length_alternative;
GT_Key GT_Keys::option_arrowshape_overall_length;
GT_Key GT_Keys::arrowshape_width;
GT_Key GT_Keys::arrowshape_width_alternative;
GT_Key GT_Keys::option_arrowshape_width;
GT_Key GT_Keys::background,	GT_Keys::option_background;
GT_Key GT_Keys::bitmap,		GT_Keys::option_bitmap;
GT_Key GT_Keys::capstyle,	GT_Keys::option_capstyle;
GT_Key GT_Keys::extent,		GT_Keys::option_extent;
GT_Key GT_Keys::fill,		GT_Keys::option_fill;
GT_Key GT_Keys::foreground,	GT_Keys::option_foreground;
GT_Key GT_Keys::image,		GT_Keys::option_image;
GT_Key GT_Keys::joinstyle,	GT_Keys::option_joinstyle;
GT_Key GT_Keys::justify,	GT_Keys::option_justify;
GT_Key GT_Keys::line,		GT_Keys::option_line;
GT_Key                          GT_Keys::option_line_relative;
GT_Key GT_Keys::outline,	GT_Keys::option_outline;
GT_Key GT_Keys::point,		GT_Keys::option_point;
GT_Key GT_Keys::smooth,		GT_Keys::option_smooth;
GT_Key GT_Keys::source,		GT_Keys::option_source;
GT_Key GT_Keys::splinesteps,	GT_Keys::option_splinesteps;
GT_Key GT_Keys::start,		GT_Keys::option_start;
GT_Key GT_Keys::stipple,	GT_Keys::option_stipple;
GT_Key GT_Keys::style,		GT_Keys::option_style;
GT_Key GT_Keys::target,		GT_Keys::option_target;
GT_Key GT_Keys::visible,	GT_Keys::option_visible;
GT_Key GT_Keys::width,		GT_Keys::option_width;
GT_Key GT_Keys::xfont,		GT_Keys::option_xfont;
GT_Key GT_Keys::font,		GT_Keys::option_font;
GT_Key GT_Keys::font_style,	GT_Keys::option_font_style;
GT_Key GT_Keys::font_size,	GT_Keys::option_font_size;


// Walter
GT_Key GT_Keys::edge_anchor,    GT_Keys::option_edge_anchor;

GT_Key GT_Keys::source_function,   GT_Keys::option_source_function;
GT_Key GT_Keys::target_function,   GT_Keys::option_target_function;
GT_Key GT_Keys::default_function,   GT_Keys::option_default_function;

GT_Key GT_Keys::delta_x_source,   GT_Keys::option_delta_x_source;
GT_Key GT_Keys::delta_y_source,   GT_Keys::option_delta_y_source;
GT_Key GT_Keys::delta_x_target,   GT_Keys::option_delta_x_target;
GT_Key GT_Keys::delta_y_target,   GT_Keys::option_delta_y_target;

// GT_Key GT_Keys::ea_default_source,   GT_Keys::option_ea_default_source;
// GT_Key GT_Keys::ea_default_target,   GT_Keys::option_ea_default_target;
// GT_Key GT_Keys::ea_default_both,   GT_Keys::option_ea_default_both;

GT_Key     GT_Keys::empty_function;
GT_Key     GT_Keys::EA_next_corner;
GT_Key     GT_Keys::EA_next_middle;
GT_Key     GT_Keys::EA_orthogonal;
GT_Key     GT_Keys::EA_connect_corner_shortest;
GT_Key     GT_Keys::EA_connect_middle_shortest;
GT_Key     GT_Keys::EA_connect_orthogonal;


GT_Key GT_Keys::label_anchor,   GT_Keys::option_label_anchor;
	
GT_Key GT_Keys::anchor_center;
GT_Key GT_Keys::anchor_n;
GT_Key GT_Keys::anchor_ne;
GT_Key GT_Keys::anchor_e;
GT_Key GT_Keys::anchor_se;
GT_Key GT_Keys::anchor_s;
GT_Key GT_Keys::anchor_sw;
GT_Key GT_Keys::anchor_w;
GT_Key GT_Keys::anchor_nw;

GT_Key GT_Keys::anchor_first;
GT_Key GT_Keys::anchor_none;
GT_Key GT_Keys::anchor_last;
GT_Key GT_Keys::anchor_bend;

GT_Key GT_Keys::anchor_clip;
GT_Key GT_Keys::anchor_corners;
GT_Key GT_Keys::anchor_middle;
GT_Key GT_Keys::anchor_8;

GT_Key GT_Keys::graph_attrs;
GT_Key GT_Keys::node_attrs;
GT_Key GT_Keys::edge_attrs;
GT_Key GT_Keys::graphics_center_x;
GT_Key GT_Keys::graphics_center_y;
GT_Key GT_Keys::graphics_w;
GT_Key GT_Keys::graphics_h;
GT_Key GT_Keys::graphics_image;


GT_Key GT_Keys::type_arc;
GT_Key GT_Keys::type_bitmap;
GT_Key GT_Keys::type_image;
GT_Key GT_Keys::type_line;
GT_Key GT_Keys::type_oval;
GT_Key GT_Keys::type_polygon;
GT_Key GT_Keys::type_rectangle;
GT_Key GT_Keys::type_text;


GT_Key GT_Keys::uiobject_unknown;
GT_Key GT_Keys::uiobject_node;
GT_Key GT_Keys::uiobject_edge;
GT_Key GT_Keys::uiobject_graph;
GT_Key GT_Keys::uiobject_label;
GT_Key GT_Keys::uiobject_node_label;
GT_Key GT_Keys::uiobject_edge_label;
GT_Key GT_Keys::uiobject_graph_label;

//
// Color names
//

GT_Key GT_Keys::white;
GT_Key GT_Keys::black;
GT_Key GT_Keys::red;
GT_Key GT_Keys::green;
GT_Key GT_Keys::blue;

//
// Style keys
//

GT_Key GT_Keys::style_pieslice;
GT_Key GT_Keys::style_chord;
GT_Key GT_Keys::style_arc;

//
// Arrow keys
//

GT_Key GT_Keys::arrow_none;
GT_Key GT_Keys::arrow_first;
GT_Key GT_Keys::arrow_last;
GT_Key GT_Keys::arrow_both;


//
// Handlers
//

GT_Key GT_Keys::pre_new_graph_handler;
GT_Key GT_Keys::post_new_graph_handler;
GT_Key GT_Keys::pre_clear_handler;
GT_Key GT_Keys::post_clear_handler;

GT_Key GT_Keys::pre_new_node_handler;
GT_Key GT_Keys::post_new_node_handler;
GT_Key GT_Keys::pre_del_node_handler;
GT_Key GT_Keys::post_del_node_handler;

GT_Key GT_Keys::pre_new_edge_handler;
GT_Key GT_Keys::post_new_edge_handler;
GT_Key GT_Keys::pre_del_edge_handler;
GT_Key GT_Keys::post_del_edge_handler;

GT_Key GT_Keys::pre_move_edge_handler;
GT_Key GT_Keys::post_move_edge_handler;

GT_Key GT_Keys::pre_hide_edge_handler;
GT_Key GT_Keys::post_hide_edge_handler;
GT_Key GT_Keys::pre_restore_edge_handler;
GT_Key GT_Keys::post_restore_edge_handler;

GT_Key GT_Keys::touch_node_handler;
GT_Key GT_Keys::touch_edge_handler;
GT_Key GT_Keys::comment_handler;
GT_Key GT_Keys::query_handler;
GT_Key GT_Keys::query_node_node_handler;
GT_Key GT_Keys::query_edge_handler;

GT_Key GT_Keys::node_style;
GT_Key GT_Keys::edge_style;

GT_Key GT_Keys::default_node_style;
GT_Key GT_Keys::default_edge_style;



//////////////////////////////////////////
//
// void GT_Keys::init()
//
// Initialize keys
//
//////////////////////////////////////////


void GT_Keys::init()
{
    for (int i=0; keys_init[i].key != 0; i++) {
	*(keys_init[i].key) = graphlet->keymapper.add (keys_init[i].value);
    }
}


