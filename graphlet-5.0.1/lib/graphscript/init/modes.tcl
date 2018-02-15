# This software is distributed under the Lesser General Public License
#
# init/modes.tcl
#
# This file initializes Graphlet's modes.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/init/modes.tcl,v $
# $Author: himsolt $
# $Revision: 1.22 $
# $Date: 1999/07/27 18:06:28 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

#
# This is code for the initialization of Graphlet
#

namespace eval GT {
    namespace export init_modes
}


##########################################
#
# GT::init_modes
#
# NOTE: I will probably change the interface for nodes to an
# array based one. You may make changes in the code below, but be
# careful.
#
##########################################


proc GT::init_modes { } {

    global GT_event GT_modes GT_options

    set global_bindings {

	{ NODE <Double-Button-%1> GT::inspector  }
	{ EDGE <Double-Button-%1> GT::inspector  }

	{ BACKGROUND <Key> GT::label_selected_object  }
	{ BACKGROUND <Control-Key> continue }
 	{ BACKGROUND <Meta-Key> continue }
	{ BACKGROUND <Alt-Key> continue }

	{ BACKGROUND <Alt-Left> GT::ev_scroll_drawing_area_left }
	{ BACKGROUND <Alt-Right> GT::ev_scroll_drawing_area_right }
	{ BACKGROUND <Alt-Up> GT::ev_scroll_drawing_area_up }
	{ BACKGROUND <Alt-Down> GT::ev_scroll_drawing_area_down }

	{ BACKGROUND <Alt-Home> {
	    global GT_event
	    GT::action $GT_event(editor) find_graph }
	}

	{ BACKGROUND <F22> {
	    global GT_event
	    GT::action $GT_event(editor) undo }
	}
	{ BACKGROUND <F20> {
	    global GT_event
	    GT::action $GT_event(editor) cut }
	}
	{ BACKGROUND <F18> {
	    global GT_event
	    GT::action $GT_event(editor) paste_here }
	}
	{ BACKGROUND <F16> {
	    global GT_event
	    GT::action $GT_event(editor) copy }
	}
	{ BACKGROUND <Help> {
	    global GT_options
	    set GT_options(tooltips) 1 }
	}

	{ BACKGROUND <ButtonPress-%3> {
	    global GT_event
	    GT::post_menu $GT_event(editor) {} canvas
	    tk_popup $GT_event(W).menu $GT_event(X) $GT_event(Y)
	}}

	{ BACKGROUND <Shift-F10> {
	    global GT_event
	    GT::post_menu $GT_event(editor) {} canvas
	    tk_popup $GT_event(W).menu $GT_event(X) $GT_event(Y)
	}}

	{ NODE <ButtonPress-%3> { GT::ev_select_node if_not_selected; break } }
	{ EDGE <ButtonPress-%3> { GT::ev_select_edge if_not_selected; break } }

    }
    set GT_modes(global_bindings) $global_bindings

    set create_mode_bindings {
	
	{ EDGE <ButtonPress-%1> GT::ev_select_edge }
	{ EDGE <Shift-ButtonPress-%1>  {GT::ev_select_edge shift} }

	{ BACKGROUND <ButtonPress-%1>   GT::ev_create_node }

	{ NODE       <ButtonPress-%1>   GT::ev_create_edge_start  }
	{ BACKGROUND <Button%1-Motion>  GT::ev_create_edge_motion }
	{ BACKGROUND <ButtonRelease-%1> GT::ev_create_edge_end }
	
	{ BACKGROUND <Escape> GT::ev_create_mode_undo_last_bend }

	{ EDGE <Shift-Double-ButtonPress-%1>  {
	    GT::ev_move_edge_start bend
	    break
	}}
	{ EDGE <Shift-Double-ButtonRelease-%1> {
	    GT::ev_move_edge_end bend
	    break
	}}
    }


    set edit_mode_bindings {

	{ BACKGROUND <ButtonPress-%1>   GT::ev_rubberbox_start  }
	{ BACKGROUND <Button%1-Motion>  GT::ev_rubberbox_motion }
	{ BACKGROUND <Shift-ButtonRelease-%1> { GT::ev_rubberbox_end shift } }
	{ BACKGROUND <ButtonRelease-%1> GT::ev_rubberbox_end    }	
	
	{ EDGE <Shift-ButtonPress-%1>           {GT::ev_move_edge_start add}  }
	{ EDGE <Control-ButtonPress-%1>         {GT::ev_move_edge_start add}  }
	{ EDGE <ButtonPress-%1>                  GT::ev_move_edge_start       }
	{ EDGE <Button%1-Motion>                 GT::ev_move_edge_motion      }
	{ EDGE <ButtonRelease-%1>                GT::ev_move_edge_end         }
	{ EDGE <Shift-ButtonRelease-%1>         {GT::ev_move_edge_end add}    }
	{ EDGE <Control-ButtonRelease-%1>       {GT::ev_move_edge_end add}    }

	{ EDGE <Shift-Double-ButtonPress-%1>  {
	    GT::ev_move_edge_start bend
	    break
	}}
	{ EDGE <Shift-Double-ButtonRelease-%1> {
	    GT::ev_move_edge_end bend
	    break
	}}

	{ NODE <Shift-ButtonPress-%1>   {GT::ev_move_node_start add}}
	{ NODE <ButtonPress-%1>          GT::ev_move_node_start     }
	{ NODE <Button%1-Motion>         GT::ev_move_node_motion    }
	{ NODE <ButtonRelease-%1>        GT::ev_move_node_end       }
	{ NODE <Shift-ButtonRelease-%1> {GT::ev_move_node_end add}}

    }

    set bends_mode_bindings {

	{ EDGE <Double-Button-%1> {
	    GT::ev_move_edge_start bend
	}}

	{ EDGE <ButtonPress-%1>          GT::ev_move_edge_start     }
	{ EDGE <Button%1-Motion>         GT::ev_move_edge_motion    }
	{ EDGE <ButtonRelease-%1>        GT::ev_move_edge_end       }

	{ NODE <ButtonPress-%1>          GT::ev_move_node_start     }
	{ NODE <Button%1-Motion>         GT::ev_move_node_motion    }
	{ NODE <ButtonRelease-%1>        GT::ev_move_node_end       }

	{ BACKGROUND <ButtonPress-%1>   GT::ev_rubberbox_start  }
	{ BACKGROUND <Button%1-Motion>  GT::ev_rubberbox_motion }
	{ BACKGROUND <Shift-ButtonRelease-%1> { GT::ev_rubberbox_end shift } }
	{ BACKGROUND <ButtonRelease-%1> GT::ev_rubberbox_end    }	
    }


    set text_mode_bindings {

	{ BACKGROUND <ButtonPress-%1> GT::ev_text_click	}
	{ NODE <ButtonPress-%1> GT::ev_text_select_node_or_edge }
	{ EDGE <ButtonPress-%1> GT::ev_text_select_node_or_edge  }

	{ GT_text <ButtonPress-%1> GT::ev_text_set_cursor }
	{ GT_text <B%1-Motion>     GT::ev_text_select_with_cursor }
	{ BACKGROUND <Delete> {
	    GT::label_selected_object interpret_delete; break
	}}

	{ GT_text <ButtonPress-%2> GT::ev_text_paste }
	{ NODE <Double-Button-%1> {
	    GT::ev_text_select_all
	}}

	{ BACKGROUND <Left>        {GT::ev_text_move_cursor left} }
	{ BACKGROUND <Shift-Left>  {GT::ev_text_move_cursor left shift} }
	{ BACKGROUND <Right>       {GT::ev_text_move_cursor right} }
	{ BACKGROUND <Shift-Right> {GT::ev_text_move_cursor right shift} }
	{ BACKGROUND <Up>          {GT::ev_text_move_cursor up} }
	{ BACKGROUND <Shift-Up>    {GT::ev_text_move_cursor up shift} }
	{ BACKGROUND <Down>        {GT::ev_text_move_cursor down} }
	{ BACKGROUND <Shift-Down>  {GT::ev_text_move_cursor down shift} }
 	{ BACKGROUND <Home>        {GT::ev_text_move_cursor begin} }
	{ BACKGROUND <Shift-Home>  {GT::ev_text_move_cursor begin shift } }
 	{ BACKGROUND <End>         {GT::ev_text_move_cursor end} }
	{ BACKGROUND <Shift-End>   {GT::ev_text_move_cursor end shift } }
 	{ BACKGROUND <Prior>       {GT::ev_text_move_cursor prior} }
 	{ BACKGROUND <Shift-Prior> {GT::ev_text_move_cursor prior shift} }
 	{ BACKGROUND <Next>        {GT::ev_text_move_cursor next} }
 	{ BACKGROUND <Shift-Next>  {GT::ev_text_move_cursor prior next} }
    }

    set zoom_mode_bindings {
	
	{ BACKGROUND <ButtonPress-%1>   GT::ev_zoombox_start  }
	{ BACKGROUND <Button%1-Motion>  GT::ev_zoombox_motion }
	{ BACKGROUND <ButtonRelease-%1> GT::ev_zoombox_end    }
    }
#	{ BACKGROUND <ButtonPress-%2>   GT::ev_zoom_out       }

    set rotate_mode_bindings {
	{ NODE   <ButtonPress-%1>  GT::rotate_mode::ev_rotate_start         }
	{ NODE   <Button%1-Motion> GT::rotate_mode::ev_rotate               }
	
	{ NODE   <ButtonPress-%1>  { GT::ev_select_node if_not_selected; break } }
	{ EDGE   <ButtonPress-%1>  { GT::ev_select_edge if_not_selected; break } }

	{ CENTER <Button%1-Motion> GT::rotate_mode::ev_move_center          }
	
	{ CENTER <Enter>	   { $GT_event(W) configure -cursor fleur } }
	{ CENTER <Leave>	   { $GT_event(W) configure -cursor {}    } }
    }
    
    set scale_mode_bindings {
	
	{ NODE   <ButtonPress-%1>  { GT::ev_select_node if_not_selected; break } }
	{ EDGE   <ButtonPress-%1>  { GT::ev_select_edge if_not_selected; break } }

	{ MARK:-1:-1 <Enter> { $GT_event(W) configure \
				   -cursor top_left_corner		} }
	{ MARK:0:-1  <Enter> { $GT_event(W) configure \
				   -cursor top_side			} }
	{ MARK:1:-1  <Enter> { $GT_event(W) configure \
				   -cursor top_right_corner		} }
	{ MARK:-1:0  <Enter> { $GT_event(W) configure \
				   -cursor left_side			} }
	{ MARK:1:0   <Enter> { $GT_event(W) configure \
				   -cursor right_side			} }
	{ MARK:-1:1  <Enter> { $GT_event(W) configure \
				   -cursor bottom_left_corner		} }
	{ MARK:0:1   <Enter> { $GT_event(W) configure \
				   -cursor bottom_side			} }
	{ MARK:1:1   <Enter> { $GT_event(W) configure \
				   -cursor bottom_right_corner		} }
	{ MARK       <Leave> { $GT_event(W) configure -cursor {}	} }
	{ MARK       <Button%1-Motion> GT::scale_mode::ev_move_mark $editor }
    }
    
    #
    # Create Mode
    #

    lappend GT_modes(modes) create_mode
    set GT_modes(create_mode,name) "Create"
    set GT_modes(create_mode,accelerator) "Meta+C"
    set GT_modes(create_mode,tooltips) \
	"Click into a free region to create a node. Click on a node to 
start an edge. Hold button until you reach the target node. 
Type a string to associate a label to an activated object."
    set GT_modes(create_mode,leave) GT::leave_create_mode
    set GT_modes(create_mode,bindings) \
	[concat \
	     [GT::translate_bindings $create_mode_bindings] \
	     [GT::translate_bindings $global_bindings] \
	    ]
    if {$GT_options(edit_mode_on_extra_mouse_button)} {
	set GT_modes(create_mode,bindings) \
	    [concat \
		 $GT_modes(create_mode,bindings) \
		 [GT::translate_bindings $edit_mode_bindings {{1 2}}] \
		]
    }
    

    #
    # Edit Mode
    #

    lappend GT_modes(modes) edit_mode
    set GT_modes(edit_mode,name) "Edit"
    set GT_modes(edit_mode,accelerator) "Meta+E"
    set GT_modes(edit_mode,tooltips) \
	"Choose a node to activate or move it. Click into a free region to 
activate all objects in a bounding box. 
Type a string to associate a label to an activated object."

    set GT_modes(edit_mode,leave) GT::leave_edit_mode

    set GT_modes(edit_mode,bindings) \
	[concat \
	     [GT::translate_bindings $edit_mode_bindings] \
	     [GT::translate_bindings $global_bindings]]

    #
    # Bend Mode
    #

    lappend GT_modes(modes) bends_mode
    set GT_modes(bends_mode,name) "Bends"
    set GT_modes(bends_mode,tooltips) \
	"Modify Bends"

    set GT_modes(bends_mode,leave) GT::leave_bends_mode

    set GT_modes(bends_mode,bindings) \
	[concat \
	     [GT::translate_bindings $bends_mode_bindings] \
	     [GT::translate_bindings $global_bindings]]

    if {$GT_options(edit_mode_on_extra_mouse_button)} {
	set GT_modes(bends_mode,bindings) \
	    [concat \
		 $GT_modes(bends_mode,bindings) \
		 [GT::translate_bindings $edit_mode_bindings {{1 2}}] \
		]
    }

    #
    # Text Mode
    #

    lappend GT_modes(modes) text_mode
    set GT_modes(text_mode,name) "Text"
    set GT_modes(text_mode,accelerator) "Meta+T"
    set GT_modes(text_mode,tooltips) \
	"Click anywhere on a string/label/text to get a cursor. 
Objects cannot be moved around."
    set GT_modes(text_mode,leave) GT::leave_text_mode
    set GT_modes(text_mode,bindings) \
	[concat \
	     [GT::translate_bindings $text_mode_bindings] \
	     [GT::translate_bindings $global_bindings] \
	    ]
    if {$GT_options(edit_mode_on_extra_mouse_button)} {
	set GT_modes(text_mode,bindings) \
	    [concat \
		 $GT_modes(text_mode,bindings) \
		 [GT::translate_bindings $edit_mode_bindings {{1 2}}]\
		]
    }

    #
    # View Mode
    #

#     lappend GT_modes(modes) view_mode
#     set GT_modes(view_mode,name) "View"
#     set GT_modes(view_mode,tooltips) "Read Only Mode"
#     set GT_modes(view_mode,leave) GT::leave_view_mode
#     set GT_modes(view_mode,bindings) {}

    #
    # Zoom Mode
    #

    lappend GT_modes(modes) zoom_mode
    set GT_modes(zoom_mode,name) "Zoom"
    set GT_modes(zoom_mode,accelerator) "Meta+Z"
    set GT_modes(zoom_mode,tooltips) \
	"Draw a rectangle with the mouse to view the selected region."
    set GT_modes(zoom_mode,init) GT::init_zoom_mode
    set GT_modes(zoom_mode,leave) GT::leave_zoom_mode
    set GT_modes(zoom_mode,bindings) \
	[concat \
	     [GT::translate_bindings $zoom_mode_bindings] \
	     [GT::translate_bindings $global_bindings] \
	    ]
    if {$GT_options(edit_mode_on_extra_mouse_button)} {
	set GT_modes(zoom_mode,bindings) \
	    [concat \
		 $GT_modes(zoom_mode,bindings) \
		 [GT::translate_bindings $edit_mode_bindings {{1 2}}] \
		]
    }

    #
    # Rotate Mode
    #

    lappend GT_modes(modes) rotate_mode
    set GT_modes(rotate_mode,name) "Rotate"
    set GT_modes(rotate_mode,accelerator) "Meta+R"
    set GT_modes(rotate_mode,tooltips) \
	"Drag nodes to rotate them around the center. \
         Drag the center to move it"
    set GT_modes(rotate_mode,init) GT::rotate_mode::init
    set GT_modes(rotate_mode,leave) GT::rotate_mode::leave
    set GT_modes(rotate_mode,bindings) \
	[concat \
	     [GT::translate_bindings $rotate_mode_bindings] \
	     [GT::translate_bindings $global_bindings] \
	    ]
    if {$GT_options(edit_mode_on_extra_mouse_button)} {
	set GT_modes(rotate_mode,bindings) \
	    [concat \
		 $GT_modes(rotate_mode,bindings) \
		 [GT::translate_bindings $edit_mode_bindings {{1 2}}] \
		]
    }

    #
    # Scale Mode
    #

    lappend GT_modes(modes) scale_mode
    set GT_modes(scale_mode,name) "Scale"
    set GT_modes(scale_mode,accelerator) "Meta+S"
    set GT_modes(scale_mode,tooltips) \
	"Scale the selection by dragging the rectangle."
    set GT_modes(scale_mode,init) GT::scale_mode::init
    set GT_modes(scale_mode,leave) GT::scale_mode::leave
    set GT_modes(scale_mode,bindings) \
	[concat \
	     [GT::translate_bindings $scale_mode_bindings] \
	     [GT::translate_bindings $global_bindings] \
	    ]
    if {$GT_options(edit_mode_on_extra_mouse_button)} {
	set GT_modes(scale_mode,bindings) \
	    [concat \
		 $GT_modes(scale_mode,bindings) \
		 [GT::translate_bindings $edit_mode_bindings {{1 2}}] \
		]
    }
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
