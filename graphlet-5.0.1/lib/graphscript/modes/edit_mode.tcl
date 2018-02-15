# This software is distributed under the Lesser General Public License
#
# edit_mode.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/modes/edit_mode.tcl,v $
# $Author: himsolt $
# $Revision: 1.11 $
# $Date: 1999/03/05 20:42:22 $
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
	action_edit_mode \
	action_bends_mode \
	ev_move_node_start \
	ev_move_node_motion \
	ev_move_node_end \
	ev_move_edge_start \
	ev_move_edge_motion \
	ev_move_edge_end \
	ev_rubberbox_start \
	ev_rubberbox_motion \
	ev_rubberbox_end \
	leave_bends_mode \
	leave_edit_mode \
	find_bend_near_point
}


###########################################
#
# Edit mode action
#
###########################################


proc GT::action_edit_mode  { editor  } {
    GT::switch_to_mode $editor edit_mode
}

proc GT::action_bends_mode  { editor  } {
    GT::switch_to_mode $editor bends_mode
}




##########################################
#
# Event handlers for node move operations
#
##########################################


proc GT::ev_move_node_start { { modifier {} } } {

    global GT_event GT_selection GT_options

    set canvas $GT_event(W)
    set editor $GT_event(editor)
    set graph $GT_event(graph)
    set x $GT_event(canvasx)
    set y $GT_event(canvasy)

    GT::adjust_coordinates_for_grid $editor x y

    set node [GT::find_object $editor node current]
    if { $node != {} } {	

	# Select the node only if we dont use a modifier.
	if { $modifier != "add" } {
	    if { [lsearch $GT_selection($editor,selected,node) $node] == -1 } {
		GT::select $GT_event(editor) select $node
	    }
	}

	set node_x [$graph get $node -x]
	set node_y [$graph get $node -y]
	set gw [GT::get_option $editor grid 0]
	if { $gw != {} && $gw != 0 } {
	    set dx [expr $node_x - (round($x / $gw) * $gw)]
	    set dy [expr $node_y - (round($y / $gw) * $gw)]
	} else {
	    set dx 0
	    set dy 0
	}

	set GT_event(start_x) [expr $x + $dx]
	set GT_event(start_y) [expr $y + $dy]
	set GT_event(last_x)  $GT_event(start_x)
	set GT_event(last_y)  $GT_event(start_y)
	
	set GT_event(move_node_start_object) $node
	set GT_event(did_move_node) 0

	set GT_event(move_node_vars) {
	    start_x
	    start_y
	    last_x
	    last_y
	    move_node_start_object
	    did_move_node
	}

	GT::undo $editor newframe
	GT::undo $editor attributes \
	    $GT_selection($editor,selected,node) \
	    -x -y

    } else {
	bell
	foreach i $GT_event(move_node_vars) {
	    catch {unset GT_event($i)}
	}
    }

    return -code break
}


proc GT::ev_move_node_motion {args} {
    
    global GT_event GT_selection GT_options

    set x $GT_event(canvasx)
    set y $GT_event(canvasy)
    set canvas $GT_event(W)
    set editor $GT_event(editor)
    set graph $GT_event(graph)

    #
    # Sometimes we get a move event without a preceeding
    # start. In this case, abort.
    #

    if ![info exists GT_event(move_node_start_object)] {
	catch { GT::move_node_start $args dummy_return_value }
	return -code break
    }

    GT::adjust_coordinates_for_grid $editor x y

    set move_x [expr $x - $GT_event(last_x)]
    set move_y [expr $y - $GT_event(last_y)]

    if { $GT_event(did_move_node) == 0 } {

	#
	# This is the first time we move. Mark all items with the
	# tag GT::move_nodes to be faster.
	#

	$canvas dtag GT::move_nodes

	foreach node $GT_selection($editor,selected,node) {
	    $canvas addtag GT::move_nodes withtag $node
	}

	foreach node $GT_selection($editor,selected,node) {
	    foreach edge [$graph edges -node $node] {
		set opposite [$graph nodes -opposite $node $edge]
		if { [lsearch $GT_selection($editor,selected,node) \
			  $opposite] != -1 } {
		    $canvas addtag GT::move_nodes withtag $edge
		}
	    }
	}

# 	$GT_event(graph) mark delete $canvas selected \
# 	    $GT_selection($editor,selected,edge)

    }

    set surrounding_edges \
	[$graph edges -embedding $GT_selection($editor,selected,node)]
    if { [llength $surrounding_edges] <
	 $GT_options(small_selection_treshold) } {
	$graph nodemove \
	    -fast $canvas $GT_selection($editor,selected,node) \
	    $move_x $move_y
    }

    $canvas move GT::move_nodes \
	[$graph translate $canvas -x $move_x] \
	[$graph translate $canvas -y $move_y]
    
    set GT_event(last_x) $x
    set GT_event(last_y) $y
    set GT_event(did_move_node) 1

    return -code break
}



proc GT::ev_move_node_end { {modifier {}} } {

    global GT_event GT_selection GT_options
    set graph $GT_event(graph)
    set canvas $GT_event(W)
    set editor $GT_event(editor)

    set x $GT_event(canvasx)
    set y $GT_event(canvasy)

    GT::adjust_coordinates_for_grid $editor x y

    if [info exists GT_event(move_node_start_object)] {

	set x_from_start [expr $x - $GT_event(start_x)]
	set y_from_start [expr $y - $GT_event(start_y)]

	if { $GT_event(did_move_node) == 0 } {

	    #
	    # We did not move since mouse down, so IT'S A CLICK !
	    #

	    set node $GT_event(move_node_start_object)
	    if { $modifier == "add" } {
		GT::select $GT_event(editor) toggle $node
	    }

	} else {

	    #
	    # We did move since mouse down, so IT'S A MOVE !
	    #

	    set surrounding_edges \
		[$graph edges -embedding $GT_selection($editor,selected,node)]
	    if { [llength $surrounding_edges] <
		 $GT_options(small_selection_treshold) } {
	    
		set move_x [expr $x - $GT_event(last_x)]
		set move_y [expr $y - $GT_event(last_y)]

	    } else {
		
		set move_x $x_from_start
		set move_y $y_from_start
		# We need to undo the fake movement
		$canvas move GT::move_nodes \
		    [$graph translate $canvas -x [expr $GT_event(start_x) - $GT_event(last_x)]] \
		    [$graph translate $canvas -y [expr $GT_event(start_y) - $GT_event(last_y)]]
	    }

	    if { $move_x != 0 } {
		set move_x [expr round($move_x)]
	    }
	    if { $move_y != 0 } {
		set move_y [expr round($move_y)]
	    }

	    if { [llength $surrounding_edges] <
		 $GT_options(small_selection_treshold) } {
 		$graph nodemove \
		    -fast $canvas $GT_selection($editor,selected,node) \
 		    $move_x $move_y
	    } else {
		$graph nodemove $GT_selection($editor,selected,node) \
 		    $move_x $move_y
	    }

# 	    $GT_event(graph) mark create $canvas selected \
# 		$GT_selection($editor,selected,edge)

	    foreach i $GT_event(move_node_vars) {
		if [info exists GT_event($i)] {
		    unset GT_event($i)
		}
	    }
	}
    }

    return -code break
}



##########################################
#
# Event handlers for edge move operations
#
##########################################


proc GT::find_bend_near_point {graph canvas line canvasx canvasy} {

    global GT_options

    for {set i 0} {$i < [llength $line]-2} {incr i 2} {

	set x0 [$graph translate $canvas -x [lindex $line $i]]
	set y0 [$graph translate $canvas -y [lindex $line [expr $i+1]]]
	set x1 [$graph translate $canvas -x [lindex $line [expr $i+2]]]
	set y1 [$graph translate $canvas -y [lindex $line [expr $i+3]]]
	if {abs($y1-$y0) > 0} {
	    set dist_from_line_x \
		[expr abs($x0 + double($x1-$x0)/double($y1-$y0) * double($canvasy - $y0) - \
			      $canvasx)]
	} else {
	    set dist_from_line_x [expr abs($x0 - $canvasx)]
	}
	if {abs($x1-$x0) > 0} {
	    set dist_from_line_y \
		[expr abs($y0 + double($y1-$y0)/double($x1-$x0) * double($canvasx - $x0) - \
			      $canvasy)]
	} else {
	    set dist_from_line_y [expr abs($y0 - $canvasy)]
	}
	set dist_from_bend_x [expr abs($x1- $canvasx)]
	set dist_from_bend_y [expr abs($y1- $canvasy)]


	if {$dist_from_bend_x < $GT_options(bend_overlap_gap) &&
	    $dist_from_bend_y < $GT_options(bend_overlap_gap)
	} {

	    return [expr $i+2]

	} elseif {($dist_from_line_x <= $GT_options(bend_overlap_gap) &&
		   (($x0 <= $canvasx && $canvasx <= $x1) ||
		    ($x1 <= $canvasx && $canvasx <= $x0))
		   )
		  ||
		  ($dist_from_line_y <= $GT_options(bend_overlap_gap) &&
		   (($y0 <= $canvasy && $canvasy <= $y1) ||
		    ($y1 <= $canvasy && $canvasy <= $y0))
		   )
	      } {

	    return [list $i [expr $i+1]]

	}
    }

    return {}
}


proc GT::ev_move_edge_start { { modifier {} } } {

    global GT_event GT_selection GT_options

    set canvas $GT_event(W)
    set editor $GT_event(editor)
    set graph $GT_event(graph)

    set GT_event(move_edge_vars) {
	start_x
	start_y
	last_x
	last_y
	move_edge_start_object
	did_move_edge
	edge_bend_index
    }


    set x $GT_event(canvasx)
    set y $GT_event(canvasy)
    GT::adjust_coordinates_for_grid $editor x y

    set edge [GT::find_object $editor edge current]

    if { $edge != {} } {	

	if { $modifier != "add" } {
	    if { [lsearch $GT_selection($editor,selected,edge) $edge] == -1 } {
		GT::select $GT_event(editor) select $edge
	    }
	}

	set GT_event(start_x) $GT_event(canvasx)
	set GT_event(start_y) $GT_event(canvasy)
	set GT_event(last_x)  $GT_event(start_x)
	set GT_event(last_y)  $GT_event(start_y)
	
	set GT_event(move_edge_start_object) $edge
	set GT_event(did_move_edge) 0

	set line [$graph get $edge -line]

	if {$modifier == "bend"} {

	    set bend [GT::find_bend_near_point $graph $canvas \
			  $line $GT_event(canvasx) $GT_event(canvasy)]
	    switch [llength $bend] {
		1 {
		    # delete
		    set index $bend
		    set line \
			[concat \
			     [lrange $line 0 [expr $index-1]] \
			     [lrange $line [expr $index+2] end]]
		    $graph set $edge -line $line
		    $graph draw

		    catch {unset GT_event(edge_bend_index)}
		}
		2 {
		    set index [lindex $bend 0]
		    set line \
			[concat \
			     [lrange $line 0 [expr $index+1]] \
			     $x $y \
			     [lrange $line [expr $index+2] end]]
		    $graph set $edge -line $line
		    $graph draw

		    set GT_event(edge_bend_index) [expr $index+2]
		}
		default {
		}
	    }

	} else {

	    set bend [find_bend_near_point $graph $canvas \
			  $line $GT_event(canvasx) $GT_event(canvasy)]
	    if {[llength $bend] == 1} {
		set line [concat \
			      [lrange $line 0 [expr $bend-1]] \
			      $x $y \
			      [lrange $line [expr $bend+2] end]]
		$graph set $edge -line $line
		$graph draw

		set GT_event(edge_bend_index) $bend
	    }

	}

	GT::undo $editor newframe
	GT::undo $editor attributes \
	    $GT_selection($editor,selected,edge) \
	    -line

    } else {
	bell
	foreach i $GT_event(move_edge_vars) {
	    if [info exists GT_event($i)] {
		unset GT_event($i)
	    }
	}
    }

    return -code break
}


proc GT::ev_move_edge_motion {args} {
    
    global GT_event GT_selection GT_options

    set canvas $GT_event(W)
    set editor $GT_event(editor)
    set graph $GT_event(graph)

    set x $GT_event(canvasx)
    set y $GT_event(canvasy)
    GT::adjust_coordinates_for_grid $editor x y

    #
    # Sometimes we get a move event without a preceeding
    # start. In this case, abort.
    #

    if ![info exists GT_event(move_edge_start_object)] {
	catch { GT::move_edge_start $args dummy_return_value }
	return -code break
    }

    set move_x [expr $x - $GT_event(last_x)]
    set move_y [expr $y - $GT_event(last_y)]

    if [info exists GT_event(edge_bend_index)] {
	set edge $GT_event(move_edge_start_object)
	set line [$graph get $edge -line]
	set index $GT_event(edge_bend_index)
	set line [concat \
		      [lrange $line 0 [expr $index-1]] \
		      $x $y \
		      [lrange $line [expr $index+2] end]]
	$graph configure $edge graphics -line $line
	$graph draw
    }

    set GT_event(last_x) $x
    set GT_event(last_y) $y
    set GT_event(did_move_edge) 1

    return -code break
}



proc GT::ev_move_edge_end { {modifier {}} } {

    global GT_event GT_selection GT_options
    set graph $GT_event(graph)
    set canvas $GT_event(W)
    set editor $GT_event(editor)

    set x $GT_event(canvasx)
    set y $GT_event(canvasy)
    GT::adjust_coordinates_for_grid $editor x y

    if [info exists GT_event(move_edge_start_object)] {

	set x_from_start [expr $x - $GT_event(start_x)]
	set y_from_start [expr $y - $GT_event(start_y)]

	if { $GT_event(did_move_edge) == 0 } {

	    #
	    # We did not move since mouse down, so IT'S A CLICK !
	    #

	    set edge $GT_event(move_edge_start_object)
	    if { $modifier == "add" } {
		GT::select $GT_event(editor) toggle $edge
	    }

	} else {

	    #
	    # We did move since mouse down, so IT'S A MOVE !
	    #

	    set move_x [expr $x - $GT_event(last_x)]
	    set move_y [expr $y - $GT_event(last_y)]

	    foreach i $GT_event(move_edge_vars) {
		catch {
		    unset GT_event($i)
		}
	    }
	}
    }

    return -code break
}



##########################################
#
# Event handlers for rubberbox selection
#
##########################################


proc GT::ev_rubberbox_start {args} {

    global GT_event

    set x $GT_event(canvasx)
    set y $GT_event(canvasy)
    set canvas $GT_event(W)

    set under_cursor [$canvas find withtag current]

    if { $under_cursor == "" } {

	set GT_event(rubberbox_start_x) $x
	set GT_event(rubberbox_start_y) $y
	set GT_event(rubberboxing) 1

	$canvas create rect $x $y $x $y \
	    -tag selection_rect
    }

    return -code break
}


proc GT::ev_rubberbox_motion {args} {
    
    global GT_event

    if [info exists GT_event(rubberboxing)] {
	$GT_event(W) coords selection_rect \
	    $GT_event(rubberbox_start_x) $GT_event(rubberbox_start_y) \
	    $GT_event(canvasx) $GT_event(canvasy)
    }
    
    return -code break
}


proc GT::ev_rubberbox_end { {shift {}} } {

    global GT_event GT_options GT_selection
    set canvas $GT_event(W)
    set editor $GT_event(editor)

    if [info exists GT_event(rubberboxing)] {

	$canvas delete selection_rect

	set x0 $GT_event(rubberbox_start_x)
	set y0 $GT_event(rubberbox_start_y)
	set x1 $GT_event(canvasx)
	set y1 $GT_event(canvasy)
	
	set width  [expr abs ($x1-$x0) ]
	set height [expr abs ($y1-$y0) ]
	
	if { $width < $GT_options(minimum_selection_rect_width) } {
	    # very small rectangle, assume click
	    set x0 [ expr $x0 - 4 ]
	    set y0 [ expr $y0 - 4 ]
	    set x1 [ expr $x1 + 4 ]
	    set y1 [ expr $y1 + 4 ]
	    # need to use "overlapping" as there is nothing to that
	    # fits into this space
	    set mode overlapping
	} else {
	    set mode $GT_options(select_in_rectangle)
	}

	
	set nodes {}
	set edges {}
	set objects [$canvas find $mode $x0 $y0 $x1 $y1]
	foreach object $objects {
	    set node [GT::find_object $editor node $object]
	    if { $node != {} && [lsearch $nodes $node] < 0 } {
		lappend nodes $node
	    }
	    set edge [GT::find_object $editor edge $object]
	    if { $edge != {} && [lsearch $edges $edge] < 0 } {
		lappend edges $edge
	    }
	}

	switch $GT_options(select_with_rubberbox) {

	    nodes {
		if {$nodes != {}} {
		    if {$shift != {}} {
			GT::select $GT_event(editor) toggle $nodes
		    } else {
			GT::select $GT_event(editor) remove edges
			GT::select $GT_event(editor) select $nodes
		    }
		} else {
		    if {$shift == {}} {
			GT::select $GT_event(editor) remove all
		    }
		}
	    }

	    edges {
		if {$edges != {}} {
		    if {$shift != {}} {
			GT::select $GT_event(editor) toggle $edges
		    } else {
			GT::select $GT_event(editor) remove nodes
			GT::select $GT_event(editor) select $edges
		    }
		} else {
		    if {$shift == {}} {
			GT::select $GT_event(editor) remove all
		    }
		}
	    }

	    all {
		if {$nodes != {} || $edges != {}} {
		    if {$shift != {}} {
			GT::select $GT_event(editor) toggle \
			    [concat $nodes $edges]
		    } else {
			GT::select $GT_event(editor) select \
			    [concat $nodes $edges]
		    }
		} else {
		    if {$shift == {}} {
			GT::select $GT_event(editor) remove all
		    }
		}
	    }
	}

	$canvas dtag GT::in_rectangle
	
	unset GT_event(rubberbox_start_x)
	unset GT_event(rubberbox_start_y)
	unset GT_event(rubberboxing)	
    }

    return -code break
}


##########################################
#
# GT::leave_edit_mode
#
##########################################

proc GT::leave_edit_mode { editor mode } {
    return {}
}

proc GT::leave_bends_mode { editor mode } {
    return {}
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
