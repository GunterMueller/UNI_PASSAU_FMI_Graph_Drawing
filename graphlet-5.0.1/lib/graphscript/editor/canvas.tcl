# This software is distributed under the Lesser General Public License
#
# canvas.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/editor/canvas.tcl,v $
# $Author: himsolt $
# $Revision: 1.20 $
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

namespace eval GT {
    namespace export \
	find_object \
	create_drawing_canvas \
	init_canvas_bindings \
	visible_drawing_area \
	center_drawing_area \
	ev_scroll_drawing_area_left \
	ev_scroll_drawing_area_right \
	ev_scroll_drawing_area_up \
	ev_scroll_drawing_area_down \
	pre_canvas_hook \
	post_canvas_hook \
	guarantee_visible \
	action_adapt_drawing_area
}




##########################################
#
# Create the canvas
#
##########################################


proc GT::create_drawing_canvas { editor } {

    global GT GT::tooltips GT_options

    frame $editor.drawing

    #
    # Create the canvas
    #

    canvas $editor.drawing.canvas \
	-scrollregion [list \
			   $GT_options(canvas_minx) \
			   $GT_options(canvas_miny) \
			   $GT_options(canvas_maxx) \
			   $GT_options(canvas_maxy)] \
	-xscrollcommand "$editor.drawing.hscrollbar set" \
	-yscrollcommand "$editor.drawing.vscrollbar set" \
	-bg $GT_options(canvas_bg)

    #
    # individual look for windows & unix
    #
    
    global tcl_platform
    if { $tcl_platform(platform) == "windows" } {
	
	$editor.drawing configure \
	    -relief sunken -bd 1

	$editor.drawing.canvas configure \
	    -highlightthickness 0
	
    } else {
	
	$editor.drawing.canvas configure \
	    -highlightthickness 1 \
	    -relief sunken -bd 2
    }
    
    # The variable GT($editor,canvas) is the official access to
    # the canvas

    set GT($editor,canvas) $editor.drawing.canvas

    #
    # Create the scrollbars
    #

    scrollbar $editor.drawing.vscrollbar \
	-orient vertical \
	-command "$editor.drawing.canvas yview"
    set GT($editor,vscroll) $editor.drawing.vscrollbar

    scrollbar $editor.drawing.hscrollbar \
	-orient horizontal \
	-command "$editor.drawing.canvas xview"
    set GT($editor,hscroll) $editor.drawing.hscrollbar

    #
    # Compute the width of the right scrollbar
    #
    set pad [expr [$editor.drawing.vscrollbar cget -width]+ 2* \
		 [$editor.drawing.vscrollbar cget -bd] + 2*\
		 [$editor.drawing.vscrollbar cget -highlightthickness]]

    #
    # Create the bottom scrollbar + a pad
    #

    button $editor.drawing.pad \
 	-image [GT::get_image find.xbm] \
 	-foreground blue3 \
	-width $pad \
	-height $pad \
	-borderwidth 0 \
	-padx 0 \
	-pady 0\
	-highlightthickness 0 \
 	-relief flat \
 	-command "GT::action $editor find_graph"
    GT::tooltips $editor.drawing.pad \
	"Find the graph"

    #
    # Create a "dirty" indicator within the pad
    #

    image create photo GT::dirty_pad_image \
	-width [expr $pad < 10 ? $pad : 10] \
	-height [expr $pad < 10 ? $pad : 10]
    GT::dirty_pad_image put "#800000" -to 0 0 10 10

    image create photo GT::clean_pad_image \
	-width [expr $pad < 10 ? $pad : 10] \
	-height [expr $pad < 10 ? $pad : 10]
    GT::clean_pad_image put "#FFFFFF" -to 0 0 10 10

    grid $editor.drawing.canvas -row 0 -column 0 -sticky news
    grid $editor.drawing.vscrollbar -row 0 -column 1 -sticky ns
    grid $editor.drawing.hscrollbar -row 1 -column 0 -sticky ew
    grid $editor.drawing.pad -row 1 -column 1 -sticky {}

    # Dot know why, but the next two lines are needed to reize
    # the window properly ...
    grid rowconfigure $editor.drawing 0 -weight 1
    grid columnconfigure $editor.drawing 0 -weight 1

    #
    # Create bindings and menu
    #

    GT::init_canvas_bindings $editor
    # GT::create_canvas_menu $editor
    set ::GT_menu($editor,canvas,create_procs) GT::create_canvas_menu

    #
    # Scroll to position (0,0) 
    #

    $editor.drawing.canvas xview moveto 0
    $editor.drawing.canvas yview moveto 0
    # focus $editor.drawing.canvas

    return $editor.drawing
}



##########################################
#
# GT::init_canvas_bindings editor
#
##########################################


proc GT::init_canvas_bindings { editor } {

    global GT_modes

    if [info exists GT_modes(global,bindings)] {
	GT::install_bindings $editor global
    }

}



##########################################
#
# Utility procs
#
# GT::find_object editor cmd ?tags?
#
##########################################


proc GT::find_object { editor cmd { tags selected } } {

    global GT

    set tags_of_object [$GT($editor,canvas) gettags $tags]

    if { $tags_of_object != "" } {

	#
	# Graphlet stores the type in the first tag
	#

	set type [lindex $tags_of_object 0]
	set object [lindex $tags_of_object 1]

	if { $object != "" && [regexp $cmd $type] } {
	    return $object
	} else {
	    return {}
	}
    } else {
	return {}
    }
}



##########################################
#
# GT::visible_drawing_area editor
#
# Returns for coordinates which describe of the bounding box of
# the visible area of the canvas in $editor. 
#
###########################################


proc GT::visible_drawing_area {editor {minus_node_sizes {}}} {

    global GT
    set graph $GT($editor,graph)
    set canvas $GT($editor,canvas)

    set scroll_bbox [$canvas cget -scrollregion]
    set scroll_minx [lindex $scroll_bbox 0]
    set scroll_miny [lindex $scroll_bbox 1]
    set scroll_maxx [lindex $scroll_bbox 2]
    set scroll_maxy [lindex $scroll_bbox 3]
    set canvas_width [expr $scroll_maxx - $scroll_minx]
    set canvas_height [expr $scroll_maxy - $scroll_miny]

    set xview [$canvas xview]
    set yview [$canvas yview]

    set x0 [expr [lindex $xview 0] * $canvas_width + $scroll_minx]
    set y0 [expr [lindex $yview 0] * $canvas_height + $scroll_miny]
    set x1 [expr [lindex $xview 1] * $canvas_width + $scroll_minx]
    set y1 [expr [lindex $yview 1] * $canvas_height + $scroll_miny]

    if {$minus_node_sizes != {}} {
	set max_width 0
	set max_height 0
	foreach node [$graph nodes] {
	    set width [$graph get $node -w]
	    if {$width > $max_width} {
		set max_width $width
	    }
	    set height [$graph get $node -h]
	    if {$height > $max_height} {
		set max_height $height
	    }
	}
	set x0 [expr $x0 + $max_width/2]
	set y0 [expr $y0 + $max_height/2]
	set x1 [expr $x1 - $max_width/2]
	set y1 [expr $y1 - $max_height/2]
    }

    return [list $x0 $y0 $x1 $y1]
}



##########################################
#
# GT::center_drawing_area
#
# Center the drawing area around  bbox
#
##########################################


proc GT::center_drawing_area { editor bbox } {

    global GT
    set canvas $GT($editor,canvas)

    set minx [lindex $bbox 0]
    set miny [lindex $bbox 1]
    set maxx [lindex $bbox 2]
    set maxy [lindex $bbox 3]

    # Get the size of the window
    set window_width [winfo width $canvas]
    set window_height [winfo height $canvas]
    
    set scroll_bbox [$canvas cget -scrollregion]
    set scroll_minx [lindex $scroll_bbox 0]
    set scroll_miny [lindex $scroll_bbox 1]
    set scroll_maxx [lindex $scroll_bbox 2]
    set scroll_maxy [lindex $scroll_bbox 3]
    set canvas_width [expr $scroll_maxx - $scroll_minx]
    set canvas_height [expr $scroll_maxy - $scroll_miny]

    set scrollx [expr double($minx + $maxx - $window_width) / 2.0 ]
    set scrolly [expr double($miny + $maxy - $window_height) / 2.0]
    $canvas xview moveto [expr ($scrollx - $scroll_minx) / $canvas_width]
    $canvas yview moveto [expr ($scrolly - $scroll_miny) / $canvas_height]
}



##########################################
#
# GT::ev_scroll_drawing_area_left
# GT::ev_scroll_drawing_area_right
# GT::ev_scroll_drawing_area_up
# GT::ev_scroll_drawing_area_down
#
# Event handlers for scrolling the canvas
#
##########################################


proc GT::ev_scroll_drawing_area_left { } {

    global GT_event
    set canvas $GT_event(W)

    $canvas xview scroll -1 page
}


proc GT::ev_scroll_drawing_area_right { } {

    global GT_event
    set canvas $GT_event(W)

    $canvas xview scroll 1 page
}


proc GT::ev_scroll_drawing_area_up { } {

    global GT_event
    set canvas $GT_event(W)

    $canvas yview scroll -1 page
}


proc GT::ev_scroll_drawing_area_down { } {

    global GT_event
    set canvas $GT_event(W)

    $canvas yview scroll 1 page
}


##########################################
#
# GT::post_canvas_hook
#
# called *after* the list of canvases of a graph has been
# changed.
#
##########################################


proc GT::pre_canvas_hook { graph hook canvases } {

    variable canvas_hook_private

    foreach c [$graph canvases] {
	set canvas [lindex $c 0]
	set canvas_hook_private($canvas,zoomx) [lindex $c 1]
	set canvas_hook_private($canvas,zoomy) [lindex $c 2]
    }
}


proc GT::post_canvas_hook { graph hook canvases } {

    global GT
    variable canvas_hook_private

    if { $canvases != {} } {

	foreach editor [$graph editors] {
	    
	    #
	    # Decrypt description
	    #

	    foreach canvas_description $canvases {
		foreach {canvas zoomx zoomy} $canvas_description {}
		if { $canvas == $GT($editor,canvas) } {
		    break
		}
	    }

	    if { $zoomx == {} } {
		set zoomx 1
	    }
	    if { $zoomy == {} } {
		set zoomy 1
	    }

	    #
	    # set global menu variables
	    #

	    set GT($editor,zoomx) $zoomx
	    set GT($editor,zoomy) $zoomy

	    #
	    # compute zoom factor
	    #

	    if ![info exists canvas_hook_private($canvas,zoomx)] {
		set zoomx_factor $zoomx
	    } else {
		set last_zoomx $canvas_hook_private($canvas,zoomx)
		set zoomx_factor [expr double($zoomx)/double($last_zoomx)]
	    }

	    if ![info exists canvas_hook_private($canvas,zoomy)] {
		set zoomy_factor 1
	    } else {	    
		set last_zoomy $canvas_hook_private($canvas,zoomy)
		set zoomy_factor [expr double($zoomy)/double($last_zoomy)]
	    }

	    #
	    # Adjust scrolling
	    #

	    foreach {minx miny maxx maxy} [$canvas cget -scrollregion] {}
	    foreach {fromx tox} [$canvas xview] {}
	    foreach {fromy toy} [$canvas yview] {}

	    # Adjust scrollregion
	    set maxx [expr $maxx * $zoomx_factor]
	    set maxy [expr $maxy * $zoomy_factor]
	    $canvas configure -scrollregion [list $minx $miny $maxx $maxy]

	    # Adjust scrollbars
	    $canvas xview moveto [expr $fromx * $zoomx_factor]
	    $canvas yview moveto [expr $fromy * $zoomy_factor]

	    Combobox::assign $editor.toolbar.view [expr $zoomx*100]%
	}
    }

    foreach index [array names canvas_hook_private] {
	unset canvas_hook_private($index)
    }
}


##########################################
#
# GT::guarantee_visible
#
##########################################


proc GT::guarantee_visible {editor {what selection} {complain_if_empty yes}} {

    global GT GT_selection
    set graph $GT($editor,graph)
    set canvas $GT($editor,canvas)

    eval $canvas addtag visible enclosed [GT::visible_drawing_area $editor]

    switch -regexp -- $what {
	selection {
	    set nodes $GT_selection($editor,selected,node)
	    set edges $GT_selection($editor,selected,edge)
	    set objects [concat \
			     $GT_selection($editor,selected,node) \
			     $GT_selection($editor,selected,edge)]
	    foreach object $objects {				 
		if {[lsearch [$canvas gettags $object] visible] > -1} {
		    lappend visible_objects $object
		}
	    }
	}
	graph {
	    set nodes [$graph nodes]
	    set edges [$graph edges]
	    set objects [concat [$graph nodes] [$graph edges]]
	    foreach object $objects {
		if {[lsearch [$canvas gettags $object] visible] > -1} {
		    lappend visible_objects $object
		}
	    }
	}
	default {
	    lappend objects $what
	    foreach object $objects {
		if {[lsearch [$canvas gettags $object] visible] > -1} {
		    lappend visible_objects $object
		}
		if [$graph isnode $object] {
		    lappend nodes $object
		} else {
		    lappend edges $object
		}
	    }
	}
    }

    $canvas dtag visible

    if ![info exists visible_objects] {

	set bbox [$graph translate $canvas [$graph bbox $objects]]
	set minx [lindex $bbox 0]
	set miny [lindex $bbox 1]
	set maxx [lindex $bbox 2]
	set maxy [lindex $bbox 3]

	set drawing_area [$graph translate $canvas -reverse 1 \
			      [GT::visible_drawing_area $editor]]
	set vminx [lindex $drawing_area 0]
	set vminy [lindex $drawing_area 1]
	set vmaxx [lindex $drawing_area 2]
	set vmaxy [lindex $drawing_area 3]

	if {
	    $vmaxx - $vminx >= $maxx - $minx ||
	    $vmaxy - $vminy >= $maxy - $maxy
	} {
	    GT::center_drawing_area $editor $bbox
	} else {
	    if [info exists nodes] {
		GT::center_drawing_area $editor \
		    [$graph bbox -node [lindex $nodes 0]]
	    } elseif [info exists edges] {
		GT::center_drawing_area $editor \
		    [$graph bbox -edge [lindex $edges 0]]
	    }
	}
    }
}


###########################################
#
# Actions
#
###########################################


proc GT::action_zoom { editor {zoom_x 100} {zoom_y {}} {relative {}}} {

    global GT GT_selection
    set canvas $GT($editor,canvas)
    set graph $GT($editor,graph)

    set old_selection [concat \
			   $GT_selection($editor,selected,node) \
			   $GT_selection($editor,selected,edge)]
    GT::select $editor remove

    if { $zoom_y == {} } {
	set zoom_y $zoom_x
    }

    if {$zoom_x < 0} {
	set zoom_x 100
    }
    if {$zoom_y < 0} {
	set zoom_y 100
    }

    if [string match *% $zoom_x] {
	set zoom_x [expr [string trimright $zoom_x %]/100.0]
    } else {
	set zoom_x [expr $zoom_x/100.0]	
    }
    if [string match *% $zoom_y] {
	set zoom_y [expr [string trimright $zoom_y %]/100.0]
    } else {
	set zoom_y [expr $zoom_y/100.0]	
    }

    if {$zoom_x == 0} {
	set zoom_x 1
    }
    if {$zoom_y == 0} {
	set zoom_y 1
    }

    foreach c [$graph canvas] {
	if { [lindex $c 0] == $canvas } {
	    if { $relative != {}} {
		set x [expr [lindex $c 1] * $zoom_x]
		set y [expr [lindex $c 2] * $zoom_y]
	    } else {
		set x $zoom_x
		set y $zoom_y
	    }
	    lappend canvases [list $canvas $x $y]
	} else {
	    lappend canvases $c
	}
    }

    $graph canvas $canvases
    $graph draw -force true

    GT::select $editor $old_selection

    GT::action $editor find_graph nocomplain_if_empty
}


proc GT::action_zoom_in { editor  } {
    GT::action $editor zoom 200 200 relative
    GT::action $editor find_graph
}


proc GT::action_zoom_out { editor  } {
    GT::action $editor zoom 50 50 relative
    GT::action $editor find_graph
}


##########################################
#
# GT::action_shrink_drawing_area
# GT::action_expand_drawing_area
# GT::action_adapt_drawing_area
#
##########################################


proc GT::action_shrink_drawing_area {editor} {

    global GT
    set canvas $GT($editor,canvas)
    
    set scroll_bbox [$canvas cget -scrollregion]
    set scroll_minx [lindex $scroll_bbox 0]
    set scroll_miny [lindex $scroll_bbox 1]
    set scroll_maxx [lindex $scroll_bbox 2]
    set scroll_maxy [lindex $scroll_bbox 3]

    $canvas configure \
	-scrollregion [list \
			   [expr $scroll_minx/2] [expr $scroll_miny/2] \
			   [expr $scroll_maxx/2] [expr $scroll_maxy/2]]
}


proc GT::action_expand_drawing_area {editor} {

    global GT
    set canvas $GT($editor,canvas)
    
    set scroll_bbox [$canvas cget -scrollregion]
    set scroll_minx [lindex $scroll_bbox 0]
    set scroll_miny [lindex $scroll_bbox 1]
    set scroll_maxx [lindex $scroll_bbox 2]
    set scroll_maxy [lindex $scroll_bbox 3]

    $canvas configure \
	-scrollregion [list \
			   [expr 2*$scroll_minx] [expr 2*$scroll_miny] \
			   [expr 2*$scroll_maxx] [expr 2*$scroll_maxy]]
}



proc GT::action_adapt_drawing_area {editor} {

    global GT
    set canvas $GT($editor,canvas)
    
    set graph_bbox [$GT($editor,graph) bbox {nodes edges}]
    set graph_minx [lindex $graph_bbox 0]
    set graph_miny [lindex $graph_bbox 1]
    set graph_maxx [lindex $graph_bbox 2]
    set graph_maxy [lindex $graph_bbox 3]
    set scroll_bbox [$canvas cget -scrollregion]
    set scroll_minx [lindex $scroll_bbox 0]
    set scroll_miny [lindex $scroll_bbox 1]
    set scroll_maxx [lindex $scroll_bbox 2]
    set scroll_maxy [lindex $scroll_bbox 3]


    set enlarge_right [expr $graph_maxx > $scroll_maxx]
    set enlarge_left [expr $graph_minx < $scroll_minx]
    set enlarge_top [expr $graph_miny < $scroll_miny]
    set enlarge_bottom [expr $graph_maxy > $scroll_maxy]

    if {$enlarge_right} {
	set scroll_maxx [expr $graph_maxx+1]
    }
#     if {$enlarge_left} {
# 	set scroll_minx [expr $scroll_minx - ($scroll_maxx-$scroll_minx)]
#     }
#     if {$enlarge_top} {
# 	set scroll_miny [expr $scroll_miny - ($scroll_maxy-$scroll_miny)]
#     }
    if {$enlarge_bottom} {
	set scroll_maxy [expr $graph_maxy+1]
    }

    $canvas configure -scrollregion [list \
					 $scroll_minx $scroll_miny \
					 $scroll_maxx $scroll_maxy]
}


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
