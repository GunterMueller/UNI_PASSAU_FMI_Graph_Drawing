# This software is distributed under the Lesser General Public License
#
# animation.tcl
#
# This module implements several utilities for animations with Graphlet.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/animation.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:39:54 $
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
	create_Demo_animations_menu \
	save_attributes \
	restore_attributes \
	animation \
	animation_step \
	dfs_animation \
	action_dfs_animation \
	action_dfs \
	animation_command_start \
	animation_command_stop \
	animation_command_pause \
	animation_command_step \
	create_animation_default_options \
	action_dfs_animation_window \
	create_animation_dfs_options \
	close_dfs_animation_window
}


proc GT::create_Demo_animations_menu { top menu } {

    global GT

#     $menu add separator

#     set GT(action,dfs) GT::action_dfs
#     GT::add_menu_command $top $menu {
# 	"Animation without animation" dfs
#     }

    set GT(action,dfs_animation_window) GT::action_dfs_animation_window
    GT::add_menu_command $top $menu {
	"Animation" dfs_animation_window
    }
}



##########################################
#
# GT::save_attributes    graph node_attr edge_attr in_array what
#
# graph:      a graph
# node_attr:  a list of attributes
# edge_attr:  a list of attributes
# in_array:   save the attributes here
#
# Saves the attributes $attributes of $what of $graph in an array
# $in_array. Attributes are saved in a form
# in_array($node,$attribute) resp. in_array($node,$attribute).
#
##########################################
#
# GT::restore_attributes graph node_attrs edge_attrs in_array what
#
# Restores attributes saved with the above procedure.
#
##########################################


proc GT::save_attributes { graph node_attrs edge_attrs in_array } {

    upvar $in_array saved

    foreach a $node_attrs {
	foreach node [$graph nodes] {
	    set saved($node,$a) [$graph get $node -$a]
	}
    }

    foreach a $edge_attrs {
	foreach edge [$graph edges] {
	    set saved($edge,$a) [$graph get $edge -$a]
	}
    }
}



proc GT::restore_attributes { graph node_attrs edge_attrs from_array } {

    upvar $from_array saved

    if [info exists saved] {

	foreach a $node_attrs {
	    foreach node [$graph nodes] {
		$graph configure $node -$a $saved($node,$a)
	    }
	}

	foreach a $edge_attrs {
	    foreach edge [$graph edges] {
		$graph configure $edge -$a $saved($edge,$a)
	    }
	}
    }
}


##########################################
#
# GT::animation
# GT::animation_step
#
##########################################


proc GT::animation { top name args } {

    global GT
    set graph $GT($top,graph)

    set node_attrs {}
    set edge_attrs {}
    set start {}
    set body {}
    set end {}

    for {set i 0} { $i < [llength $args] } { incr i } {

	switch -glob -- [lindex $args $i] {

	    -node* {
		if { [incr i] < [llength $args] } {
		    set node_attrs [lindex $args $i]
		} else {
		    error "Illegal argument: [lindex $args $i]"
		}
	    }
	    -edge* {
		if { [incr i] < [llength $args] } {
		    set edge_attrs [lindex $args $i]
		} else {
		    error "Illegal argument: [lindex $args $i]"
		}
	    }

	    -start {
		if { [incr i] < [llength $args] } {
		    set start [lindex $args $i]
		} else {
		    error "Illegal argument: [lindex $args $i]"
		}
	    }
	    -body {
		if { [incr i] < [llength $args] } {
		    set body [lindex $args $i]
		} else {
		    error "Illegal argument: [lindex $args $i]"
		}
	    }
	    -end {
		if { [incr i] < [llength $args] } {
		    set end [lindex $args $i]
		} else {
		    error "Illegal argument: [lindex $args $i]"
		}
	    }

	}
    }

    #
    # Initialize global variable animation
    #

    upvar #0 $name animation
    set animation(name) $name
    set animation(stop) 0
    set animation(pause) 0
    set animation(running) 1

    ##########################################
    #
    # Run
    #
    ##########################################

    #
    # Save attributes
    #
    GT::save_attributes $graph $node_attrs $edge_attrs animation

    #
    # Do animation specific initializations
    #
    if { $start != {} } {
	uplevel $start
    }

    #
    # Run the algorithm with animation
    #

    if { $body != {} } {
	set code [catch { uplevel $body } msg]
	if { $code == 2377 && $msg == "aborted_animation" } {
	    # OK
	} elseif { $code != 0 } {
	    global errorInfo
	    error $msg $errorInfo
	}
    }

    #
    # Do animation specific cleanup
    #

    if { $end != {} } {
	uplevel $end
    }

    #
    # Restore original attributes
    #

    GT::restore_attributes $graph $node_attrs $edge_attrs animation
    $graph draw

    #
    # Go away
    #

    unset animation
}



##########################################
#
# GT::animation_step
#
# name  - The name of the animation
# nodes - a list of nodes which to be redrawn.
# edges - a list of edges which to be redrawn.
# proc  - some Tcl code which is executed only if animation is on.
#
# Performs a single animation step.
#
##########################################

proc GT::animation_step { name graph nodes edges proc } {

    upvar #0 $name animation

    if { [info exists animation] && $animation(running) == 1 } {

	uplevel $proc

	global GT GT_options
	
	$graph draw [concat $nodes $edges]

	update
	
	if { $GT(animation,stop) == 1 } {
	    return -code 2377 aborted_animation
	} elseif { $GT(animation,pause) == 1 } {
	    vwait GT(animation,pause)
	} elseif { $GT_options($name,method) == "Step" } {
	    vwait GT(animation,step)
	    if { $animation(stop) == 1 } {
		return -code 2377 aborted_animation
	    }
	} elseif { $GT_options($name,interval) > 0 } {
	    after $GT_options($name,interval)
	}
    }
}



##########################################
#
# GT::action_dfs_animation
#
# This is a sample animation.
#
##########################################


proc GT::dfs_animation { top { start_node {} } } {

    global GT GT_options

    #
    # Initialize variables
    #

    set graph $GT($top,graph)
    set number 0

    if { [llength [$graph nodes]] == 0 } {
	return
    }

    #
    # Initialize algorithm variables
    #

    GT::node_array visited $graph 0
    if { $start_node == {} } {
	set start_node [lindex [$graph nodes] 0]
    }

    set stack {}
    set visited($start_node) 1
    GT::push stack $start_node

    #
    # Animation step #1 : start node
    #

    #
    # The DFS Algorithm
    #

    while { [llength $stack] > 0 } {

	set n [GT::pop stack]

	#
	# Animation step
	#

	GT::animation_step dfs_animation $graph $n {} {
	    if $GT_options(dfs_animation,number) {
		$graph configure $n \
		    -label [incr number]
	    }
	    if { $GT_options(dfs_animation,color) != {} } {
		$graph configure $n graphics \
		    -fill $GT_options(dfs_animation,color)
	    }
	}

	foreach edge [$graph edges -node $n] {

	    set w [$graph nodes -opposite $n $edge]

	    if { $visited($w) == 0 } {

		set visited($w) 1
		GT::push stack $w

	    }

	}
    }
}


proc GT::action_dfs_animation { top { start_node {} } } {

    GT::animation $top dfs_animation \
	-node { fill label } \
	-start {
	    GT::select $top remove selection
	} \
	-body {
	    GT::dfs_animation $top $start_node
	}
}


proc GT::action_dfs { top { start_node {} } } {

    GT::dfs_animation $top $start_node
}


proc GT::animation_command_start { name procedure args } {

    global GT

    upvar #0 $name animation
    if [info exists animation(running)] {
	return
    }

    set GT(animation,stop) 0
    set GT(animation,pause) 0

    eval $procedure $args
}


proc GT::animation_command_stop { name this_button} {

    global GT GT_options

    #
    # Signal that the animation has been stopped.
    #

    upvar #0 $name animation
    if [info exists animation(running)] {

	set GT(animation,stop) 1
	set GT(animation,pause) 0

	#
	# If we are running step-by-step, we must perform a final step.
	#

	if { $GT_options($name,method) == "Step" } {
	    set GT(animation,step) 42
	}
    }
}


proc GT::animation_command_pause { name this_button} {

    global GT GT_options

    #
    # Signal that the animation has been paused.
    #

    upvar #0 $name animation
    if [info exists animation(running)] {
	if { $GT(animation,pause) == 0 } {
	    set GT(animation,pause) 1
	    $this_button configure \
		-text "Continue" \
		-width [string length "Continue"]
	} else {
	    set GT(animation,pause) 0
	    $this_button configure \
		-text "Pause" \
		-width [string length "Continue"]
	}
    }
}



proc GT::animation_command_step { name button } {

    global GT

    upvar #0 $name animation
    if [info exists animation(running)] {
	set GT(animation,step) 42
    }
}


##########################################
#
# GT::create_animation_default_options
#
##########################################


proc GT::create_animation_default_options { window options_name } {

    global GT_options

    frame $window.animation_options \
	-relief groove \
	-borderwidth 2

    ##########################################
    #
    # Generate the item labels
    #
    ##########################################

    set labels(interval) "Interval:"
    set labels(method) "Method: "
    set maxwidth [GT::max_string_length_in_array labels]

    #########################################
    #
    # Interval settings
    #
    ##########################################

    GT::create_frame_with_label $window.animation_options.interval \
	$labels(interval) \
	$maxwidth

    scale $window.animation_options.interval.scale \
	-length 200 \
	-from 0 -to 2000 \
	-tickinterval 500  -resolution 100 \
	-variable GT_options($options_name,interval) \
	-orient horizontal
    pack $window.animation_options.interval.scale -side right

    pack $window.animation_options.interval -anchor w
   
    ##########################################
    #
    # Method
    #
    ##########################################

    GT::create_frame_with_label $window.animation_options.method \
	$labels(method) \
	$maxwidth

    frame $window.animation_options.method.options
    radiobutton $window.animation_options.method.options.continous \
	-text "Continous" \
	-variable GT_options($options_name,method) \
	-value Continous
    pack $window.animation_options.method.options.continous -side left
    radiobutton $window.animation_options.method.options.step \
	-text "Step by Step" \
	-variable GT_options($options_name,method) \
	-value Step
    pack $window.animation_options.method.options.step -side left
    pack $window.animation_options.method.options -side right

    pack $window.animation_options.method -anchor w

    pack $window.animation_options \
	-anchor w

    return $window.animation_options
}



##########################################
#
# GT::action_dfs_animation_window
#
##########################################


proc GT::action_dfs_animation_window { top {start_node {}} } {

    global GT GT_options
    set graph $GT($top,graph)
    set window $top.dfs_animation    

    if [winfo exists $window] {
	raise $window
	wm deiconify $window
	return
    }

    ##########################################
    #
    # Initialize the options
    #
    ##########################################

    if { [info exists GT_options(dfs_animation,color)] == 0 } {
	set GT_options(dfs_animation,color) red
    }
    if { [info exists GT_options(dfs_animation,interval)] == 0 } {
	set GT_options(dfs_animation,interval) 500
    }
    if { [info exists GT_options(dfs_animation,method)] == 0 } {
	set GT_options(dfs_animation,method) Continous
    }
    if { [info exists GT_options(dfs_animation,number)] == 0 } {
	set GT_options(dfs_animation,number) 1
    }

    ##########################################
    #
    # Initialize the window
    #
    ##########################################

    toplevel $window
    wm title $window "Animation Sample"

    ##########################################
    #
    # Controls
    #
    # Note: need some nifty images here.
    #
    ##########################################

    frame $window.controls

    button $window.controls.settings \
	-text "Settings" \
	-command [list GT::toggle_packing_of_window $window.animation_options]
    pack $window.controls.settings -side left

    button $window.controls.start \
	-text "Start" \
	-command [list GT::animation_command_start dfs_animation \
		      GT::action_dfs_animation $top $start_node]
    pack $window.controls.start -side left

    button $window.controls.step \
	-text "Step" \
	-command [list GT::animation_command_step dfs_animation \
		      $window.controls.step]
    pack $window.controls.step -side left

    button $window.controls.pause \
	-text "Pause" \
	-width [string length "Continue"] \
	-command [list GT::animation_command_pause dfs_animation \
		      $window.controls.pause]
    pack $window.controls.pause -side left

    button $window.controls.stop \
	-text "Stop" \
	-command [list GT::animation_command_stop dfs_animation \
		      $window.controls.stop]
    pack $window.controls.stop -side left

    button $window.controls.cancel \
	-text "Cancel" \
	-command [list GT::close_dfs_animation_window $window dfs_animation]
    pack $window.controls.cancel -side right

    pack $window.controls

    frame $window.animation_options
    GT::create_animation_default_options \
	$window.animation_options \
	dfs_animation
    GT::create_animation_dfs_options \
	$window.animation_options \
	dfs_animation

    GT::position_window_near_graph $top $window below

    wm protocol $window WM_DELETE_WINDOW \
	"GT::close_dfs_animation_window $window dfs_animation"
}


proc GT::create_animation_dfs_options { window options_name } {

    global GT_options

    frame $window.dfs_animation_options \
	-relief groove \
	-borderwidth 2

    ##########################################
    #
    # Generate the item labels
    #
    ##########################################

    set labels(color) "Color: "
    set labels(number) "Number: "
    set maxwidth [GT::max_string_length_in_array labels]

    ##########################################
    #
    # Select color for marking
    #
    ##########################################

    pack [GT::create_color_button \
	      $window.dfs_animation_options.color \
	      GT_options($options_name,color) \
	      $labels(color) \
	      $maxwidth \
	     ] \
	-anchor w

    ##########################################
    #
    # Insert DFS number in label ?
    #
    ##########################################

    GT::create_frame_with_label $window.dfs_animation_options.number \
	$labels(number) \
	$maxwidth
    checkbutton $window.dfs_animation_options.number.checkbutton \
	-text "Insert DFS number in label" \
	-variable GT_options($options_name,number)
    pack $window.dfs_animation_options.number.checkbutton \
	-side right
    pack $window.dfs_animation_options.number \
	-anchor w

    pack $window.dfs_animation_options \
	-anchor w

    return $window.dfs_animation_options
}


proc GT::close_dfs_animation_window { window name } {

    upvar #0 $name animation

    if [info exists animation] {
	GT::animation_command_stop $name $window.controls.stop
	# unset animation
    }

    destroy $window
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
