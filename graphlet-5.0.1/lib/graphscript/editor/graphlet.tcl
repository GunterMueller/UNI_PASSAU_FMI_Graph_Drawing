# This software is distributed under the Lesser General Public License
#
# graphlet.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/editor/graphlet.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:41:33 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

namespace eval :: {
    namespace export graphlet
}


##########################################
#
# graphlet args
#
##########################################

proc graphlet args {

    global GT GT_options

    if !$GT(graphlet_initialized) {
	GT::init_graphlet
	set GT(graphlet_initialized) 1
    }

    GT::show_greeting_graphlet_dialog

    set topwidget ""
    set file ""
    set startup_command {}
    set startup_action {}

    for {set i 0} { $i < [llength $args] } { incr i } {

	switch -regexp -- [lindex $args $i] {

	    -file|-f {
		if { [incr i] < [llength $args] } {
		    set file [lindex $args $i]
		} else {
		    error "Illegal argument [lindex $args $i]"
		}
	    }

	    -topwidget|-toplevel|-top {
		if { [incr i] < [llength $args] } {
		    set topwidget [lindex $args $i]
		} else {
		    error "Illegal argument [lindex $args $i]"
		}
	    }

	    -command|-cmd {
		if { [incr i] < [llength $args] } {
		    set startup_command [lindex $args $i]
		} else {
		   error "Illegal argument [lindex $args $i]"
		}
	    }

	    -action {
		if { [incr i] < [llength $args] } {
		    set startup_action [lindex $args $i]
		} else {
		    error "Illegal argument [lindex $args $i]"
		}
	    }

	    -debug {
		set GT_options(debug) 1
	    }

	    -debug_graphics {
		set GT_options(debug_graphics) 1
	    }

	    -debugg {
		set GT_options(debug_graphics) 1
	    }

	    -graph {
		if { [incr i] < [llength $args] } {
		    set graph [lindex $args $i]
		} else {
		    error "Illegal argument [lindex $args $i]"
		}
	    }

	    -console {
		set show_console_at_startup 1
	    }
	    
	    default {
		if { $i == [llength $args]-1 } {
		    set file [lindex $args $i]
		} else {
		    error "Illegal argument [lindex $args $i]"
		}
	    }
	}
    }

    #
    # Create the window and the graph
    #

    if { $topwidget == ""} {
 	set topwidget [GT::new_frame_name]
     }
    
    if [info exists graph] {
	set graph [GT::create_and_initialize_graph $topwidget $graph]
    } else {
	set graph [GT::create_and_initialize_graph $topwidget]
    }

    set editor [GT::create_frame [toplevel $topwidget]]

    if [info exists GT($editor,canvas)] {
	set canvases [$graph canvas]
	lappend canvases $GT($editor,canvas)
	$graph canvas $canvases
    }

    #
    # Init local variables
    #

    set GT_options($editor,grid) [GT::get_option $editor grid 0]

    #
    # Init selection & load graph
    #

    GT::init_selection $editor
    if { $file != "" && [file exists $file]} {
	set loaded [GT::load_graph $topwidget $file]
	if { $loaded == 1 } { # load error
	    set GT($editor,filename) ""
	}
    } else {
	set GT($editor,filename) $file
    }

    #
    # Init undo facilities
    #

    if ![info exists GT($editor,undo)] {
	set GT($editor,undo) [graph]
    }

    # GT::init_status $graph

    $graph draw -force on
 
    #
    # Set mode related options
    #

    global GT_options
    GT::switch_to_mode $editor $GT_options(default_mode)

    if { $startup_command != {} } {
	eval $startup_command
    }

    if { $startup_action != {} } {
	GT::action $editor $startup_action
    }

    if [info exists show_console_at_startup] {
	GT::action $editor show_console
    }

    return $editor
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
