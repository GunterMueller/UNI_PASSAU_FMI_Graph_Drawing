# This software is distributed under the Lesser General Public License
#
# undo.tcl
#
# This file implements Graphlet's undo facilities
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/undo.tcl,v $
# $Author: himsolt $
# $Revision: 1.7 $
# $Date: 1999/03/05 20:40:27 $
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
	undo
}

proc GT::undo {editor command args} {

    global GT
    variable undo
    set graph $GT($editor,graph)

    if ![info exists undo($editor,graph)] {
	set undo($editor,graph) [graph]
    }
    set undo_graph $undo($editor,graph)

    #
    # Parse Parameters
    #

    set nodes {}
    set edges {}

    switch $command {

	newframe {	
	}

	endframe {
	}

	add {
	}

	push {
	    foreach arg $args {
		foreach object $arg {
		    switch [$graph typeof -short $object] {
			node {
			    lappend nodes $object
			}
			edge {
			    lappend edges $object
			}
		    }
		}
	    }
	}

	attributes {
	    set objects [lindex $args 0]
	    foreach arg [lrange $args 1 end] {
		lappend attributes $arg
	    }
	}

	delete {
	    foreach arg $args {
		foreach object $arg {
		    lappend to_delete $object
		}
	    }
	}

	undo {
	    # We will take care about that later ...
	}

	default {
	    error "Illegal argument $command in [lindex [info level 1] 0]"
	}

    }

    #
    # Interpret commands
    #

    switch $command {

	newframe {
	    lappend undo($editor,stack) frame
	}
	endframe {
	    # reserved for future use
	}

	add {
	    lappend undo($editor,stack) $args
	}

	push {

	    #
	    # Analyze nodes and edges to find out what needs to be backed 
	    # up
	    #

	    set nodes_to_backup $nodes
	    set edges_to_backup $edges

	    # Add copies of all neighbour nodes
	    set inner_nodes_to_backup \
		$nodes_to_backup
	    set neighbour_nodes_to_backup \
		[$graph nodes -adj $nodes_to_backup]

	    set inner_edges_to_backup \
		[$graph edges -inner $inner_nodes_to_backup]
	    set neighbour_edges_to_backup \
		[$graph edges -embedding $inner_nodes_to_backup]

	    # Special treatment for isolated edges
	    foreach edge $edges {
		if {[lsearch $inner_edges_to_backup $edge] == -1 &&
		    [lsearch $neighbour_edges_to_backup $edge] == -1
		} {

		    lappend neighbour_edges_to_backup $edge

		    set source [$graph get $edge -source]
		    if {[lsearch $inner_nodes_to_backup $source] == -1 &&
			[lsearch $neighbour_nodes_to_backup $source] == -1
		    } {
			lappend neighbour_nodes_to_backup $source
		    }

		    set target [$graph get $edge -target]
		    if {[lsearch $inner_nodes_to_backup $target] == -1 &&
			[lsearch $neighbour_nodes_to_backup $target] == -1
		    } {
			lappend neighbour_nodes_to_backup $target
		    }		 
		}
	    }

	    # Copy inner subgraph into undo_graph
	    set copied [$graph copynode $inner_nodes_to_backup $undo_graph]

	    # Compute isomorphic mapping for inner subgraph
	    set copied_inner_nodes [lindex $copied 0]
	    set iso_inner_nodes {}
	    for {set i 0} {$i < [llength $copied_inner_nodes]} {incr i} {
		lappend iso_inner_nodes \
		    [lindex $inner_nodes_to_backup $i] \
		    [lindex $copied_inner_nodes $i]
		set iso([lindex $inner_nodes_to_backup $i]) \
		    [lindex $copied_inner_nodes $i]
	    }
	    
	    set copied_inner_edges [lindex $copied 1]
	    set iso_inner_edges {}
	    for {set i 0} {$i < [llength $copied_inner_edges]} {incr i} {
		lappend iso_inner_edges \
		    [lindex $inner_edges_to_backup $i] \
		    [lindex $copied_inner_edges $i]
	    }

	    set iso_neighbour_nodes {}
	    foreach node $neighbour_nodes_to_backup {
		set copied [$graph copynode $node $undo_graph]
		lappend iso_neighbour_nodes \
		    $node \
		    [lindex [lindex $copied 0] 0]
		set iso($node) [lindex [lindex $copied 0] 0]
	    }

	    set iso_neighbour_edges {}
	    foreach edge $neighbour_edges_to_backup {
		set copied [$graph copyedge $edge \
				$iso([$graph get $edge -source]) \
				$iso([$graph get $edge -target]) \
				$undo_graph]
		lappend iso_neighbour_edges \
		    $edge \
		    [lindex [lindex $copied 0] 0]
	    }

	    # Push on stack
	    lappend undo($editor,stack) \
		[list backup \
		     $iso_inner_nodes $iso_inner_edges \
		     $iso_neighbour_nodes $iso_neighbour_edges]
	}

	attributes {
	    set undo_element {}
	    foreach object $objects {
		foreach attr $attributes {
		    lappend undo_element \
			$object $attr [eval $graph get $object $attr]
		}
	    }
	    lappend undo($editor,stack) [list attributes $undo_element]	    
	}

	delete {
	    if {[info exists to_delete]} {
		lappend undo($editor,stack) [list delete $to_delete]
	    }
	}

	undo {

	    while {
		   [lindex [lindex $undo($editor,stack) end] 0] != "frame" &&
		   $undo($editor,stack) != {}
	       } {

		set undo_element [lindex $undo($editor,stack) end]
		GT::undo_step $editor $undo_element

		set undo($editor,stack) \
		    [lrange $undo($editor,stack) \
			 0 \
			 [expr [llength $undo($editor,stack)]-2]]
	    }

	    set undo($editor,stack) \
		[lrange $undo($editor,stack) \
		     0 \
		     [expr [llength $undo($editor,stack)]-2]]
	    set undo($editor,stack) {}
	}

	default {
	    error "Illegal argument $command in [lindex [info level 1] 0]"
	}
    }
}


proc GT::undo_step {editor undo_element} {

    global GT GT_selection
    variable undo
    set graph $GT($editor,graph)
    set undo_graph $undo($editor,graph)

    switch [lindex $undo_element 0] {

	backup {

	    set iso_inner_nodes [lindex $undo_element 1]
	    set iso_inner_edges [lindex $undo_element 4]
	    set iso_neighbour_nodes [lindex $undo_element 3]
	    set iso_neighbour_edges [lindex $undo_element 4]

	    foreach {original_node node_copy} $iso_inner_nodes {
		lappend nodes_to_restore $node_copy
	    }

	    if [info exists nodes_to_restore] {
		set copied [$undo_graph copynode $nodes_to_restore $graph]
		for {set i 0} {$i < [llength $nodes_to_restore]} {incr i} {
		    set node_to_restore [lindex $nodes_to_restore $i]
		    set original_node [lindex [lindex $copied 0] $i]	
		    set iso($node_to_restore) $original_node
		}
	    }

	    foreach {original_node node_to_restore} $iso_neighbour_nodes {
		set iso($node_to_restore) $original_node
	    }

	    foreach {original_edge edge_copy} $iso_neighbour_edges {
		set s [$undo_graph get $edge_copy -source]
		set t [$undo_graph get $edge_copy -target]
		$undo_graph copyedge $edge_copy \
			$iso($s) $iso($t) $graph
	    }
	    
	    $undo_graph delete nodes
	}
	
	delete {
	    $graph delete [lindex $undo_element 1]
	}

	attributes {
	    foreach {object attr value} [lindex $undo_element 1] {
		eval $graph set $object $attr [list $value]
	    }
	}

	frame {
	}

	default {
	    error "Illegal element in undo stack |[lindex $undo_element 0]|"
	}
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
