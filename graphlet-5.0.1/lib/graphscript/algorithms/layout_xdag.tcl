# This software is distributed under the Lesser General Public License
#
# algorithms/layout_xdag.tcl
#
# ########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_xdag.tcl,v $
# $Author: himsolt $
# $Revision: 1.12 $
# $Date: 1999/04/10 15:27:10 $
# $Locker:  $
# $State: Exp $
#
# ########################################
#
# (C) University of Passau 1995-1997, graphlet Project
#
#     Author: Harald Mader (mader@fmi.uni-passau.de)
#

package require Graphlet
package provide Graphscript [gt_version]

package require Gt_xdag

namespace eval GT {
    namespace export \
	action_layout_xdag \
	action_layout_xdag_options
}

namespace eval GT_xdag {
    namespace export \
	init \
	init_Layout_menu \
	init_Layout_options_menu \
	add_layout_menu_entries \
	add_layout_options_menu_entries
}


proc GT_xdag::init { } {

    global GT GT_menu GT_default_options

    array set GT_default_options {
	xdag_min_node_node_distance        20
	xdag_min_node_edge_distance        10
	xdag_min_edge_edge_distance         5
	xdag_default_edge_length           50
	xdag_animation                      0
	xdag_iterations_crossing_reduction  3
	xdag_last_phase_crossing_reduction  0
	xdag_iterations_node_positioning    3
	xdag_last_phase_node_positioning    0
	xdag_stepping                       0
	xdag_protocol                       0
    }
}


proc GT_xdag::add_layout_menu_entries {editor menu} {

    global GT

    set GT(action,layout_xdag) \
	GT::action_layout_xdag

    GT::add_menu_command $editor $menu {
	"E^xtended DAG" layout_xdag "" 1
    }
}


proc GT_xdag::add_layout_options_menu_entries {editor menu} {

    global GT

    set GT(action,layout_xdag_options) \
	GT::action_layout_xdag_options

    GT::add_menu_command $editor $menu {
	"E^xtended DAG ..." layout_xdag_options "" 1
    }
}


#########################################
#
# GT::action_layout_xdag
#
# This is the run-function. It contains the Graphscript commands to be 
# executed when we invoke the XDAG algorithm from the GUI or from the 
# command line, and is therefore the interface between the Tcl/Tk GUI 
# and the C++ implementation of the layout algorithm.
#
#########################################

proc GT::action_layout_xdag { editor } {

    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-check_selection 0 \
	-name "Extended DAG Layout" \
	-command {
	    layout_xdag $graph \
		-node_node_distance $GT_options(xdag_min_node_node_distance) \
		-node_edge_distance $GT_options(xdag_min_node_edge_distance) \
		-edge_edge_distance $GT_options(xdag_min_edge_edge_distance) \
		-edgelength $GT_options(xdag_default_edge_length) \
		-animation $GT_options(xdag_animation) \
		-crossing_reduction_iterations $GT_options(xdag_iterations_crossing_reduction) \
		-last_crossing_reduction $GT_options(xdag_last_phase_crossing_reduction) \
		-node_positioning_iterations $GT_options(xdag_iterations_node_positioning) \
		-last_node_positioning $GT_options(xdag_last_phase_node_positioning) \
		-protocol $GT_options(xdag_protocol) \
		-stepping $GT_options(xdag_stepping)
	}
}



#########################################
#
# GT::action_layout_xdag_options
#
# This function defines the dialog box that pops up when we
# invoke the options for the XDAG layout from the menu.
#
#########################################

proc GT::action_layout_xdag_options { editor } {

    global GT GT_options

    lappend options {
	newtab "General"
    } {
	integer "Node-Node distance" xdag_min_node_node_distance
	"Minimal horizontal distance between two nodes."
    } {
	integer "Node-Edge distance" xdag_min_node_edge_distance
	"Minimal horizontal distance between nodes and edges"
    }  {
	integer "Edge-Edge distance" xdag_min_edge_edge_distance
	"Minimal horizontal distance between edges."
    } {
	integer "Edge Height" xdag_default_edge_length
	"minimal horizontal distance between to connected nodes.
A numeric label at an edge sets an individual edge height."
    } {
	radio "Animation" xdag_animation
	"Watch steps in intermediate phases."
	{
	    {"None" 0}
	    {"Phase" 1}
	    {"Level" 2}
	    {"Node" 3}
	}
    } {
	endtab
    }

    lappend options {
	newtab "Iteration"
    } {
	newgroup "Crossing Reduction"
    } {
	integer "Number of Iterations" xdag_iterations_crossing_reduction
	"More iterations may improve the quality, but they are time consuming."
	-scale on
	-from 1
	-to 10
    } {
	radio "Last Phase" xdag_last_phase_crossing_reduction
	"The last phase has a direct impact on crossings."
        {
	    {"Down" 0}
	    {"Up" 1}
	}
    } {
	endgroup
    } {
	newgroup "Node Positioning"
    } {
	integer "Number of Iterations" xdag_iterations_node_positioning
	"More iterations may improve the quality, but they are time consuming."
	-scale on
	-from 1
	-to 10
    } {
	radio "Last Phase" xdag_last_phase_node_positioning
	"The last phase has a direct impact on the node positioning."
        {
	    {"Down" 0}
	    {"Up" 1}
	}
    } {
	endtab
    }

#     lappend options {
# 	newtab "Debugging"
#     } {
# 	check "Step through functions" xdag_stepping
# 	""
#     } {
# 	check "Protocol" xdag_protocol
# 	""
#     } {
# 	endtab
#     }
    
    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    layout_xdag_options "DAG Layout (Extended) by Harald Mader" \
	    $options layout_xdag
    } else {
	GT::create_tabwindow $editor \
	    layout_xdag_options "DAG Layout (Extended) by Harald Mader" \
	    $options
    }
}


#
# Initialization procedure
#


GT_xdag::init
