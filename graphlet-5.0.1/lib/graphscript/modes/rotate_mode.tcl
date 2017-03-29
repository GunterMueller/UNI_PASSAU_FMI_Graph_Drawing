# This software is distributed under the Lesser General Public License
#===========================================================================
#
#   rotate_mode.tcl 
#
#===========================================================================
# $Id: rotate_mode.tcl,v 1.5 1999/01/25 13:34:22 forster Exp $

#---------------------------------------------------------------------------
#   Header
#---------------------------------------------------------------------------

package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT::rotate_mode {
    
    namespace export action

    variable center
    array set center {
	w	10
	h	10
	fill	white
	outline	blue
    }
}

#---------------------------------------------------------------------------
#   Mode definition
#---------------------------------------------------------------------------

proc GT::rotate_mode::action  { editor } {
    GT::switch_to_mode $editor rotate_mode
}

proc GT::rotate_mode::init { editor mode } {

    # install canvas hook (for catching View->Zoom)
    
    GT::add_hook post_canvas_hook GT::rotate_mode::post_canvas_hook

    # calculate position for the rotation center and show it
    
    init_center $editor
    show_center $editor
}

proc GT::rotate_mode::leave { editor mode } {

    # clean up: remove our hook and delete the rotation center
    
    GT::remove_hook post_canvas_hook GT::rotate_mode::post_canvas_hook

    hide_center $editor

    return {}	;# empty result => allow leaving
}

#---------------------------------------------------------------------------
#   Center
#---------------------------------------------------------------------------

proc GT::rotate_mode::init_center { editor } {
    
    global GT GT_selection
    variable center
    
    set nodes $GT_selection($editor,selected,node)

    # calculate the initial position of the rotation center. If we have a
    # selection use the center of the selection's center. If not use the
    # center of the visible drawing area.
    
    if { [llength $nodes] > 0 } {
	
	GT::pset { x1 y1 x2 y2 } [$GT($editor,graph) bbox -subgraph $nodes]
	
    } else {
	
	GT::pset { x1 y1 x2 y2 } [GT::visible_drawing_area $editor]

 	set x1 [expr $x1 / $GT($editor,zoomx)]
 	set y1 [expr $y1 / $GT($editor,zoomy)]
 	set x2 [expr $x2 / $GT($editor,zoomx)]
 	set y2 [expr $y2 / $GT($editor,zoomy)]
    }

    set center($editor,x) [expr ($x1 + $x2)/2.0]
    set center($editor,y) [expr ($y1 + $y2)/2.0]
}

proc GT::rotate_mode::show_center { editor } {

    global GT
    variable center

    set c $GT($editor,canvas)

    # canvas coordinates of the rotation center
    
    set x [expr $center($editor,x) * $GT($editor,zoomx)]
    set y [expr $center($editor,y) * $GT($editor,zoomy)]

    # outer circle
    
    $c create oval \
	[expr $x - $center(w)/2.0] [expr $y - $center(h)/2.0] \
	[expr $x + $center(w)/2.0] [expr $y + $center(h)/2.0] \
	-tag CENTER \
	-outline $center(outline) \
	-fill $center(fill)

    # vertical line
    
    $c create line \
	$x [expr $y - $center(h)/2.0] \
	$x [expr $y + $center(h)/2.0] \
	-tag CENTER \
	-fill $center(outline)

    # horizontal line
    
    $c create line \
	[expr $x - $center(w)/2.0] $y \
	[expr $x + $center(w)/2.0] $y \
	-tag CENTER \
	-fill $center(outline)
}

proc  GT::rotate_mode::hide_center { editor } {
    
    global GT

    $GT($editor,canvas) delete CENTER
}

#---------------------------------------------------------------------------
#   Hooks
#---------------------------------------------------------------------------

proc GT::rotate_mode::post_canvas_hook { graph hook canvases } {
    
    global GT_modes

    # catch zooming

    foreach editor [$graph editor] {
    
	# update center mark
	
	if { $GT_modes($editor,current) == "rotate_mode" } {
	    hide_center $editor
	    show_center $editor
	}
    }
}

#---------------------------------------------------------------------------
#   Events
#---------------------------------------------------------------------------

proc GT::rotate_mode::ev_rotate_start {} {
    
    global GT GT_event

    set editor $GT_event(editor)

    set GT_event(last_x) [expr 1.0 * $GT_event(canvasx) / $GT($editor,zoomx)]
    set GT_event(last_y) [expr 1.0 * $GT_event(canvasy) / $GT($editor,zoomy)]
}

proc GT::rotate_mode::ev_rotate {} {
    
    global GT GT_event GT_selection
    variable center
    
    set editor $GT_event(editor)
    set graph $GT_event(graph)
    set nodes $GT_selection($editor,selected,node)

    set x [expr 1.0 * $GT_event(canvasx) / $GT($editor,zoomx)]
    set y [expr 1.0 * $GT_event(canvasy) / $GT($editor,zoomy)]

    set dx1 [expr $GT_event(last_x) - $center($editor,x)]
    set dy1 [expr $GT_event(last_y) - $center($editor,y)]
    
    set dx2 [expr $x - $center($editor,x)]
    set dy2 [expr $y - $center($editor,y)]

    set a1 [dx_dy_to_angle $dx1 $dy1]
    set a2 [dx_dy_to_angle $dx2 $dy2]
    
    GT_rotate_nodes $graph $nodes \
	$center($editor,x) $center($editor,y) \
	[expr ($a2 - $a1)]

    $graph draw
    
    set GT_event(last_x) $x
    set GT_event(last_y) $y
}

proc GT::rotate_mode::ev_move_center {} {
    
    global GT GT_event
    variable center

    set editor $GT_event(editor)
    set c $GT($editor,canvas)
    
    set x [$c canvasx $GT_event(x)]
    set y [$c canvasy $GT_event(y)]
    
    set dx [expr $x - $center($editor,x) * $GT($editor,zoomx)]
    set dy [expr $y - $center($editor,y) * $GT($editor,zoomy)]

    set center($editor,x) [expr 1.0 * $x / $GT($editor,zoomx)]
    set center($editor,y) [expr 1.0 * $y / $GT($editor,zoomy)]
    
    $c move CENTER $dx $dy
}

#---------------------------------------------------------------------------
#   Utilities
#---------------------------------------------------------------------------

proc GT::rotate_mode::dx_dy_to_angle { dx dy } {

    set PI 3.14159265358979323846
           
    if { $dy == 0 } {
	return [expr ($dx > 0) ? $PI/2 : -$PI/2]
    } elseif { $dy < 0 } {
	return [expr atan($dx/$dy) + $PI]
    } else {
	return [expr atan($dx/$dy)]
    }
}

#---------------------------------------------------------------------------
#   end of file
#---------------------------------------------------------------------------
