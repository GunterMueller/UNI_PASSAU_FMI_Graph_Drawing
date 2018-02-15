/* This software is distributed under the Lesser General Public License */
#ifndef GT_KEYS_H
#define GT_KEYS_H

//
// Graph.h
//
// This module defines the class GT_Keys. This class is just a
// collection of keys.
//
//------------------------------------------
//
// $Source: /home/br/CVS/graphlet/src/gt_base/Keys.h,v $
// $Author: himsolt $
// $Revision: 1.7 $
// $Date: 1999/06/09 11:12:48 $
// $Locker:  $
// $State: Exp $
//
//------------------------------------------
//
// (C) University of Passau 1995-1999, graphlet project
//

class GT_Keys {

    GT_BASE_CLASS (GT_Keys);

public:

    GT_Keys();
    virtual ~GT_Keys();

    static void init();

    //
    // Management
    //
	
    static GT_Key empty;
    // 	static GT_Key none;
    static GT_Key def;
    static GT_Key undefined;

    //
    // General
    //
	
    static GT_Key graphics, label_graphics;
    static GT_Key graph;
    static GT_Key node;
    static GT_Key edge;

    static GT_Key version, option_version;
    static GT_Key creator, option_creator;
    static GT_Key id, option_id;
    static GT_Key uid, option_uid;
    static GT_Key label_uid, option_label_uid;
    static GT_Key name, option_name;
    static GT_Key label, option_label;
    static GT_Key graph_attrs, option_graph_attrs;
    static GT_Key node_attrs, option_node_attrs;
    static GT_Key edge_attrs, option_edge_attrs;
    static GT_Key center, option_center;
    static GT_Key source_port, source_port_alternative, option_source_port;
    static GT_Key target_port, target_port_alternative, option_target_port;
    static GT_Key port, option_port;
    static GT_Key ports, option_ports;
    static GT_Key label_anchor_bend, label_anchor_bend_alternative;
    static GT_Key option_label_anchor_bend;
    static GT_Key label_anchor_x, label_anchor_x_alternative;
    static GT_Key option_label_anchor_x;
    static GT_Key label_anchor_y, label_anchor_y_alternative;
    static GT_Key option_label_anchor_y;

    static GT_Key directed, option_directed;

    //
    // Graphics
    //
	
    static GT_Key x, option_x;
    static GT_Key y, option_y;
    static GT_Key w, option_w;
    static GT_Key h, option_h;

    static GT_Key type, option_type;
    static GT_Key type_arc;
    static GT_Key type_bitmap;
    static GT_Key type_image;
    static GT_Key type_line;
    static GT_Key type_oval;
    static GT_Key type_polygon;
    static GT_Key type_rectangle;
    static GT_Key type_text;
	
    static GT_Key anchor, option_anchor;
    static GT_Key arrow, option_arrow;
    static GT_Key arrowshape, option_arrowshape;
    static GT_Key arrowshape_touching_length,
	arrowshape_touching_length_alternative,
	option_arrowshape_touching_length;
    static GT_Key arrowshape_overall_length,
	arrowshape_overall_length_alternative,
	option_arrowshape_overall_length;
    static GT_Key arrowshape_width,
	arrowshape_width_alternative,
	option_arrowshape_width;
    static GT_Key background, option_background;
    static GT_Key bitmap, option_bitmap;
    static GT_Key capstyle, option_capstyle;
    static GT_Key extent, option_extent;
    static GT_Key fill, option_fill;
    static GT_Key foreground, option_foreground;
    static GT_Key image, option_image;
    static GT_Key joinstyle, option_joinstyle;
    static GT_Key justify, option_justify;
    static GT_Key line, option_line;
    static GT_Key option_line_relative;
    static GT_Key outline, option_outline;
    static GT_Key point, option_point;
    static GT_Key smooth, option_smooth;
    static GT_Key source, option_source;
    static GT_Key splinesteps, option_splinesteps;
    static GT_Key start, option_start;
    static GT_Key stipple, option_stipple;
    static GT_Key style, option_style;
    static GT_Key target, option_target;
    static GT_Key visible, option_visible;
    static GT_Key width, option_width;
    static GT_Key xfont, option_xfont;
    static GT_Key font, option_font;
    static GT_Key font_style, option_font_style;
    static GT_Key font_size, option_font_size;

    //
    // Graphics related
    //

    // Walter
    // static GT_Key edge_anchor, option_edge_anchor;
    static GT_Key label_anchor, option_label_anchor;

    //
    // Walter: Keys for NEI
    //
    static GT_Key edge_anchor, option_edge_anchor;
	
    static GT_Key delta_x_source, option_delta_x_source;
    static GT_Key delta_y_source, option_delta_y_source;
    static GT_Key delta_x_target, option_delta_x_target;
    static GT_Key delta_y_target, option_delta_y_target;

    static GT_Key source_function, option_source_function;
    static GT_Key target_function, option_target_function;
    static GT_Key default_function, option_default_function;	

    static GT_Key empty_function;
    static GT_Key EA_next_corner;
    static GT_Key EA_next_middle;
    static GT_Key EA_orthogonal;
    static GT_Key EA_connect_corner_shortest;
    static GT_Key EA_connect_middle_shortest;
    static GT_Key EA_connect_orthogonal;
    
    //
    // Anchor keys
    //
	
    static GT_Key anchor_center;
    static GT_Key anchor_n;
    static GT_Key anchor_ne;
    static GT_Key anchor_e;
    static GT_Key anchor_se;
    static GT_Key anchor_s;
    static GT_Key anchor_sw;
    static GT_Key anchor_w;
    static GT_Key anchor_nw;

    static GT_Key anchor_first;
    static GT_Key anchor_none;
    static GT_Key anchor_last;
    static GT_Key anchor_bend;

    static GT_Key anchor_clip;
    static GT_Key anchor_corners;
    static GT_Key anchor_middle;
    static GT_Key anchor_8;

    //
    // Style keys
    //

    static GT_Key style_pieslice;
    static GT_Key style_chord;
    static GT_Key style_arc;

    //
    // Arrow keys
    //

    static GT_Key arrow_none;
    static GT_Key arrow_first;
    static GT_Key arrow_last;
    static GT_Key arrow_both;
    
    //
    // Path
    //
	
    static GT_Key graphics_center_x;
    static GT_Key graphics_center_y;
    static GT_Key graphics_w;
    static GT_Key graphics_h;
    static GT_Key graphics_image;

    //
    // UIObject Names
    //
	
    static GT_Key uiobject_unknown;
    static GT_Key uiobject_node;
    static GT_Key uiobject_edge;
    static GT_Key uiobject_graph;
    static GT_Key uiobject_label;
    static GT_Key uiobject_node_label;
    static GT_Key uiobject_edge_label;
    static GT_Key uiobject_graph_label;

    //
    // Color names
    //

    static GT_Key white;
    static GT_Key black;
    static GT_Key red;
    static GT_Key green;
    static GT_Key blue;

    //
    // Handlers
    //

    static GT_Key pre_new_graph_handler;
    static GT_Key post_new_graph_handler;
    static GT_Key pre_clear_handler;
    static GT_Key post_clear_handler;

    static GT_Key pre_new_node_handler;
    static GT_Key post_new_node_handler;
    static GT_Key pre_del_node_handler;
    static GT_Key post_del_node_handler;

    static GT_Key pre_new_edge_handler;
    static GT_Key post_new_edge_handler;
    static GT_Key pre_del_edge_handler;
    static GT_Key post_del_edge_handler;

    static GT_Key pre_move_edge_handler;
    static GT_Key post_move_edge_handler;

    static GT_Key pre_hide_edge_handler;
    static GT_Key post_hide_edge_handler;
    static GT_Key pre_restore_edge_handler;
    static GT_Key post_restore_edge_handler;

    static GT_Key touch_node_handler;
    static GT_Key touch_edge_handler;
    static GT_Key comment_handler;
    static GT_Key query_handler;
    static GT_Key query_node_node_handler;
    static GT_Key query_edge_handler;

    //
    // Predefined node & edge Styles
    //

    static GT_Key default_node_style;
    static GT_Key default_edge_style;

    static GT_Key node_style;
    static GT_Key edge_style;
};

#endif
