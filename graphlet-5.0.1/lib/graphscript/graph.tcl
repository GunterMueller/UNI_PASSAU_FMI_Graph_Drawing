# This software is distributed under the Lesser General Public License
#
# graph.tcl
#
# The this class implements several functions which work on graphs.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/graph.tcl,v $
# $Author: himsolt $
# $Revision: 1.7 $
# $Date: 1999/07/14 10:53:20 $
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
	create_and_initialize_graph \
	hook_update_status \
	hook_init_node_attributes \
	hook_init_edge_attributes \
	load_graph \
	pre_editor_hook \
	post_editor_hook \
	pre_load_graph_hook \
	post_load_graph_hook \
	post_new_graph_hook \
	post_directed_hook \
	save_graph
}




##########################################
#
# Create & Initialize Graph
#
##########################################


proc GT::create_and_initialize_graph { editor {graph {}} args } {
    
    global GT GT_options

    if { $graph == {}} {
	set graph [graph]
	$graph set -directed $GT_options(directed)
    }
    
    set editors [$graph editor]
    lappend editors $editor
    $graph editor $editors

    return $graph
}


##########################################
#
# Graph Hooks
#
##########################################


#
# Initialization related hooks
#

proc GT::post_new_graph_hook { graph hook args } {

    global GT

    $graph configure default_node_style graphics \
 	-w 16 \
 	-h 16

#     $graph configure default_node_style graphics \
# 	-w 20 \
# 	-h 20 \
# 	-fill "#8080FF" \
# 	-type oval
#     $graph configure default_node_style label_graphics \
# 	-fill white
#     $graph configure default_edge_style graphics \
# 	-fill blue \
# 	-width 2

    set GT($graph,autonumber_nodes) 0
    set GT($graph,autonumber_edges) 0

    GT::init_status $graph
}


proc GT::pre_editor_hook { graph hook editors } {
    global GT
    foreach editor $editors {
	if [info exists GT($editor,graph)] {
	    unset GT($editor,graph)
	}
    }
}


proc GT::post_editor_hook { graph hook editors } {
    global GT
    foreach editor $editors {
	set GT($editor,graph) $graph
	set GT(,graph) $graph
    }
}


#
# Loading Graphs
#

proc GT::load_graph { editor filename} {

    global GT GT_options
    set graph $GT($editor,graph)

    set code [catch {$graph load -file $filename} error_message]
    if { $code != 0 } {
	GT::message $editor "Error loading $filename: $error_message" error
	return 1
    }

    return 0
}


proc GT::pre_load_graph_hook { graph hook filename } {

    foreach editor [$graph editors] {
	GT::select $editor remove selection
    }
}


proc GT::post_load_graph_hook { graph hook filename } {

    global GT GT_options

    foreach editor [$graph editors] {

	set GT($editor,filename) $filename
	$graph draw

	set GT_options(directed) [$graph get -directed]
	GT::init_status $graph
	
#	GT::action $editor find_graph
    }
}


#
# Loading Graphs
#

proc GT::save_graph { editor filename} {

    global GT GT_status
    set graph $GT($editor,graph)

    set cmd [list $graph print -file $filename]
    if { [catch $cmd error_message] == 0 } {
	set GT_status($graph,dirty) 0
	set GT($editor,filename) $filename
    } else {
	GT::message $editor $error_message error
	return 1
    }
}


proc GT::post_save_graph_hook { graph hook filename} {

    global GT_status

    catch {set GT_status($graph,dirty) 0}
}


##########################################
#
# Switch directed/undirected
#
##########################################


proc GT::post_directed_hook { graph hook directed } {

    global GT_status

    switch $directed {
	0 {
	    foreach e [$graph edges] {
		$graph set $e -arrow none
	    }
	}
	1 {
	    foreach e [$graph edges] {
		$graph set $e -arrow last
	    }
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
