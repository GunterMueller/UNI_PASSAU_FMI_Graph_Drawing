# This software is distributed under the Lesser General Public License
#===========================================================================
#
#   scale_mode.tcl 
#
#===========================================================================
# $Id: scale_mode.tcl,v 1.5 1999/01/25 13:34:22 forster Exp $

#---------------------------------------------------------------------------
#   Header
#---------------------------------------------------------------------------

package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT::scale_mode {
    namespace export action

    variable marks
    array set marks {
	w 4
	h 4
	style { -fill black -width 0 }
    }

    variable line
    array set line {
	gap 2
	style { -fill blue -stipple gray50 }
    }
}

#---------------------------------------------------------------------------
#   Mode definition
#---------------------------------------------------------------------------

proc GT::scale_mode::action  { editor  } {
    GT::switch_to_mode $editor scale_mode
}

proc GT::scale_mode::init { editor mode } {
    
    global GT_selection
    variable scaling
    
    GT::add_hook post_canvas_hook GT::scale_mode::post_canvas_hook
    GT::add_hook post_draw_hook GT::scale_mode::post_draw_hook

    set scaling($editor) 0

    trace var GT_selection($editor,selected,node) w \
	[namespace code "selection_changed $editor"]

    show_selection $editor
}

proc GT::scale_mode::leave { editor mode } {

    global GT_selection

    GT::remove_hook post_canvas_hook GT::scale_mode::post_canvas_hook
    GT::remove_hook post_draw_hook GT::scale_mode::post_draw_hook
    
    trace vdelete GT_selection($editor,selected,node) w \
	[namespace code "selection_changed $editor"]

    hide_selection $editor

    # allow leaving

    return
}

#---------------------------------------------------------------------------
#   Selection
#---------------------------------------------------------------------------

proc GT::scale_mode::show_selection { editor } {

    global GT GT_selection
    variable bbox
    
    set g $GT($editor,graph)
    set nodes $GT_selection($editor,selected,node)
    if { ! [GT::lempty $nodes] } {
	set bbox($editor) [$g bbox -subgraph $nodes]
	show_line $editor
	show_marks $editor
    }
}

proc GT::scale_mode::hide_selection { editor } {
    
    hide_marks $editor
    hide_line $editor
}

proc GT::scale_mode::selection_changed { editor args } {

    variable _wait_id

    # wait a bit, because updating too often slows down

    set waittime 30
    
    set IDVar _wait_id($editor)
    if {[info exists $IDVar]} {
	after cancel [set $IDVar]
    }
    set $IDVar [after $waittime [namespace code "unset $IDVar;
                                                 update_selection $editor"]]
}

proc GT::scale_mode::update_selection { editor } {
    hide_selection $editor 
    show_selection $editor
}

#---------------------------------------------------------------------------
#   Marks
#---------------------------------------------------------------------------

proc GT::scale_mode::show_marks { editor } {
    
    global GT
    variable marks
    variable line

    set c $GT($editor,canvas)

    GT::pset { cx cy dx dy } [graph_to_screen $editor [relative_bbox $editor]]

    set dx [expr $dx + $line(gap)]
    set dy [expr $dy + $line(gap)]

    foreach { x y } { 
	-1 -1    0 -1    1 -1
	-1  0            1  0
	-1  1    0  1    1  1
    } {
	set mx [expr $x*$dx + $cx]
	set my [expr $y*$dy + $cy]

	set mark [eval $c create rectangle \
		      [expr $mx - $marks(w)/2.0] [expr $my - $marks(h)/2.0] \
		      [expr $mx + $marks(w)/2.0] [expr $my + $marks(h)/2.0] \
		      $marks(style) \
		      -fill black -width 0 \
		      -tag [list [list MARK MARK:$x:$y \
				      MARKX:$x MARKY:$y ]] \
		     ]
    }
}

proc  GT::scale_mode::hide_marks { editor } {
    global GT

    $GT($editor,canvas) delete MARK
}

#---------------------------------------------------------------------------
#   Line
#---------------------------------------------------------------------------

proc GT::scale_mode::show_line { editor } {
    
    global GT
    variable line

    GT::pset { cx cy dx dy } [graph_to_screen $editor [relative_bbox $editor]]

    set dx [expr $dx + $line(gap)]
    set dy [expr $dy + $line(gap)]
    
    foreach { xa ya xb yb } {
	-1 -1  1 -1
	 1 -1  1  1
	 1  1 -1  1
	-1  1 -1 -1
    } {
	eval $GT($editor,canvas) create line \
	    [expr $xa*$dx + $cx] [expr $ya*$dy + $cy] \
	    [expr $xb*$dx + $cx] [expr $yb*$dy + $cy] \
	    $line(style) \
	    -tag MARK:line
    }
}

proc  GT::scale_mode::hide_line { editor } {
    global GT

    $GT($editor,canvas) delete MARK:line
}

#---------------------------------------------------------------------------
#   Hooks
#---------------------------------------------------------------------------

proc GT::scale_mode::post_canvas_hook { graph hook canvases } {
    
    global GT_modes

    # catch zooming

    foreach editor [$graph editor] {
    
	# update center mark
	
	if { $GT_modes($editor,current) == "scale_mode" } {
	    update_selection $editor
	}
    }
}

proc GT::scale_mode::post_draw_hook { graph args } {

    global GT GT_modes
    variable scaling

    foreach editor [$graph editor] {
    
	if { $GT_modes($editor,current) == "scale_mode" } {

	    if { !$scaling($editor) } {
		update_selection $editor
	    }
	}
    }
}

#---------------------------------------------------------------------------
#   Events
#---------------------------------------------------------------------------

proc GT::scale_mode::ev_move_mark {} {

    global GT GT_event
    variable marks
    variable bbox

    set w 4
    set h 4

    set editor $GT_event(editor)
    set c $GT_event(W)
    set x [$c canvasx $GT_event(x)]
    set y [$c canvasy $GT_event(y)] 
    
    set tags [$c gettags current]
    
    GT::pset { dummy relx rely } \
	[split [lindex $tags [lsearch -glob $tags MARK:*]] :]

    GT::pset { x1 y1 x2 y2 } [$c coords current]
    
    set old_x [expr $x1 + $marks(w)/2.0]
    set old_y [expr $y1 + $marks(h)/2.0]

    set dx [expr $x - $old_x]
    set dy [expr $y - $old_y]

    if { $relx != 0 } {
	$c move MARKX:$relx $dx            0 
	$c move MARKX:0     [expr $dx/2.0] 0
    }
    if { $rely != 0 } {
	$c move MARKY:$rely 0 $dy
	$c move MARKY:0     0 [expr $dy/2.0]
    }

    GT::pset { x-1 y-1 x1 y1 } $bbox($editor) 

    set x$relx [expr ($x - $relx * $marks(w) / 2.0) / $GT($editor,zoomx)]
    set y$rely [expr ($y - $rely * $marks(h) / 2.0) / $GT($editor,zoomy)]

    set_bbox $editor [list ${x-1} ${y-1} $x1 $y1]
}

#---------------------------------------------------------------------------
#   Utilities
#---------------------------------------------------------------------------

proc GT::scale_mode::screen_to_graph { editor coords } {

    global GT
    
    set result {}
    foreach { x y } $coords {
	lappend result \
	    [expr $x / $GT($editor,zoomx)] \
	    [expr $y / $GT($editor,zoomy)]
    }
    return $result
}

proc GT::scale_mode::graph_to_screen { editor coords } {
    
    global GT
    
    set result {}
    foreach { x y } $coords {
	lappend result \
	    [expr $x * $GT($editor,zoomx)] \
	    [expr $y * $GT($editor,zoomy)]
    }
    return $result
}

proc GT::scale_mode::relative_bbox { editor } {

    global GT
    variable bbox
    
    GT::pset { x1 y1 x2 y2 } $bbox($editor)
    
    set dx [expr ($x2 - $x1)/2.0]
    set dy [expr ($y2 - $y1)/2.0]
    
    set cx [expr ($x1 + $x2)/2.0]
    set cy [expr ($y1 + $y2)/2.0]

    return [list $cx $cy $dx $dy]
}

proc GT::scale_mode::set_bbox { editor new_bbox } {

    global GT GT_selection
    variable bbox
    variable scaling

    set c $GT($editor,canvas)
    set g $GT($editor,graph)
    set nodes $GT_selection($editor,selected,node)
    
    GT::pset { cx1 cy1 dx1 dy1 } [relative_bbox $editor]
    set bbox($editor) $new_bbox
    GT::pset { cx2 cy2 dx2 dy2 } [relative_bbox $editor]

    GT_scale_nodes $g $nodes \
	$cx1 $cy1 $dx1 $dy1 \
	$cx2 $cy2 $dx2 $dy2

    set scaling($editor) 1
    $g draw
    set scaling($editor) 0

    # update line
    
    hide_line $editor
    show_line $editor
    $c raise MARK
}

#---------------------------------------------------------------------------
#   end of file
#---------------------------------------------------------------------------
