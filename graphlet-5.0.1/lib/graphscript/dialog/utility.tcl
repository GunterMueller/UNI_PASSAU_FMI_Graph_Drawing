# This software is distributed under the Lesser General Public License
#
# utility.tcl
#
# This module implements several utilities for the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/utility.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:41:26 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	toggle_packing_of_window \
	create_frame_with_label \
	create_color_button \
	color_button_command \
	create_anchor_menu \
	position_window_near_graph
}


##########################################
#
# GT::toggle_packing_of_window
#
##########################################

proc GT::toggle_packing_of_window window {

    if [winfo ismapped $window] {
	pack forget $window
    } else {
	pack $window
    }
}



##########################################
#
# GT::create_frame_with_label
#
##########################################


proc GT::create_frame_with_label { frame {label {}} {labelwidth 0} } {

    frame $frame

    if { $label != {} } {
	if { $labelwidth == 0 } {
	    set labelwidth [string length $label]
	}
	label $frame.label \
	    -text $label \
	    -width $labelwidth \
	    -anchor e
	pack $frame.label \
	    -side left
    }

    return $frame
}


##########################################
#
# GT::create_color_button window var_name label labelwidth
#
# Creates a button (with label and surrounding frame) which
# selects a color.
#
##########################################

proc GT::create_color_button { window var_name {label {}} {labelwidth 0} } {

    upvar $var_name color

    GT::create_frame_with_label $window $label $labelwidth

    button $window.button \
	-background $color \
	-command "GT::color_button_command $window.button $var_name"
    pack $window.button -side right

    return $window
}


proc GT::color_button_command { button var_name { title "Choose Color"} } {

    upvar $var_name var

    set color [tk_chooseColor -initialcolor $var -title $title]
    if { $color != {} } {
	$button configure -background $color
	set var $color
    }
}


##########################################
#
# GT::create_anchor_menu editor variable
#
##########################################


proc GT::create_anchor_menu { editor variable_name } {

    global GT

    menubutton $editor \
	-textvariable GT($editor,text) \
	-indicatoron true \
	-relief raised \
	-menu $editor.menu
    set menu [menu $editor.menu]

    set anchor(c) "Centered"
    set anchor(n) "Top Side"
    set anchor(ne) "Top Right Corner"
    set anchor(e) "Right Side"
    set anchor(se) "Lower Right Corner"
    set anchor(s) "Bottom Side"
    set anchor(sw) "Lower Left Corner"
    set anchor(w) "Left Side"
    set anchor(nw) "Top Left Corner"

    foreach a [array names anchor] {
	set cmd "set $variable_name $a ; set GT($editor,text) \"$anchor($a)\""
	$menu add command \
	    -label $anchor($a) \
	    -command $cmd
    }

    upvar #0 $variable_name variable
    set GT($editor,text) $anchor($variable)

    return $editor
}


##########################################
#
# GT::position_window_near_graph editor window
#
##########################################

proc GT::position_window_near_graph { editor window { method near } } {

    global GT GT_options

    #
    # Determine bounding box of what is visible
    #

    eval $GT($editor,canvas) addtag GT::position_window_near_graph \
	enclosed [GT::visible_drawing_area $editor]
    set bbox [$GT($editor,canvas) bbox GT::position_window_near_graph]
    $GT($editor,canvas) dtag GT::position_window_near_graph

    if { $bbox == {} } {
	if { $method == "near" } {
	    set method right
	}
    }


    switch $method {

	near {
	    #
	    # Compute (x,y) coordinate right of what is visible
	    #

	    set canvas_width \
		[lindex [$GT($editor,canvas) cget -scrollregion] 2]
	    set canvas_height \
		[lindex [$GT($editor,canvas) cget -scrollregion] 3]
	    set x [expr \
		       [lindex $bbox 2] - \
		       [lindex [$GT($editor,canvas) xview] 0] * \
		           $canvas_width + \
		       [winfo rootx $GT($editor,canvas)] \
		      ]
	    set y [expr \
		       [lindex $bbox 1] - \
		       [lindex [$GT($editor,canvas) yview] 0] * \
		           $canvas_height + \
		       [winfo rooty $GT($editor,canvas)] \
		      ]

	    set x [expr $x + $GT_options(position_window_gap_x)]
	    set y [expr $y + $GT_options(position_window_gap_y)]
	}

	below {
	    set x [winfo rootx $editor]
	    set y [expr [winfo rooty $editor] + [winfo reqheight $editor]]

	    # set x [expr $x + $GT_options(position_window_gap_x)]
	    set y [expr $y + $GT_options(position_window_gap_y)]
	}

	default {
	    set x [expr [winfo rootx $editor] + [winfo reqwidth $editor]]
	    set y [winfo rooty $editor]

	    set x [expr $x + $GT_options(position_window_gap_x)]
	    # set y [expr $y + $GT_options(position_window_gap_y)]
	}
    }

    wm withdraw $window
    update idletasks
    set screenwidth [winfo screenwidth $window]
    set screenheight [winfo screenheight $window]
    set width [winfo reqwidth $window]
    set height [winfo reqheight $window]

    if { $x + $width > $screenwidth } {
	set x [expr $screenwidth - $width]
    }
    if { $y + $height > $screenheight } {
	set y [expr $screenwidth - $height]
    }

    wm geometry $window "+[expr round($x)]+[expr round($y)]"
    wm deiconify $window
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
