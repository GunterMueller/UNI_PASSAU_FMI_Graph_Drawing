# This software is distributed under the Lesser General Public License
#
# algorithms/leda_algorithms.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/leda_algorithms.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:40:56 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

# package require Gt_algorithms


namespace eval GT {
    namespace export \
	init_leda_algorithms \
	create_Leda_Algorithms_menu \
	action_leda_planarity_test \
	action_leda_kuratowski_subgraph \
	action_leda_connectivity_test \
	action_leda_label_with_component_numbers \
	action_leda_strong_connectivity_test \
	action_leda_label_with_strong_component_numbers \
	action_leda_is_acyclic \
	action_leda_topsort \
	action_leda_bfs \
	action_leda_bfs_numbers \
	action_leda_dfs_from_start \
	action_leda_dfs_tree \
	action_leda_dfs_numbers \
	action_leda_dfs_tree_and_numbers \
	action_leda_max_flow
}


proc GT_init_leda_algorithms { } {
    GT::init_leda_algorithms
}


proc GT::init_leda_algorithms { } {

    global GT

    set GT(action,leda_planarity_test) \
	GT::action_leda_planarity_test
    set GT(action,leda_kuratowski_subgraph) \
	GT::action_leda_kuratowski_subgraph

    set GT(action,leda_connectivity_test) \
	GT::action_leda_connectivity_test
    set GT(action,leda_strong_connectivity_test) \
	GT::action_leda_strong_connectivity_test
    set GT(action,leda_label_with_component_numbers) \
	GT::action_leda_label_with_component_numbers
    set GT(action,leda_label_with_strong_component_numbers) \
	GT::action_leda_label_with_strong_component_numbers

    set GT(action,leda_is_acyclic) \
	GT::action_leda_is_acyclic
    set GT(action,leda_topsort) \
	GT::action_leda_topsort

    set GT(action,leda_bfs) \
	GT::action_leda_bfs
    set GT(action,leda_bfs_numbers) \
	GT::action_leda_bfs_numbers

    set GT(action,leda_dfs_from_start) \
	GT::action_leda_dfs_from_start
    set GT(action,leda_dfs_tree) \
	GT::action_leda_dfs_tree
    set GT(action,leda_dfs_numbers) \
	GT::action_leda_dfs_numbers
    set GT(action,leda_dfs_tree_and_numbers) \
	GT::action_leda_dfs_tree_and_numbers

    set GT(action,leda_max_flow) \
	GT::action_leda_max_flow
}


proc GT::create_Leda_Algorithms_menu { editor menu } {

    #
    # Submenu "Algorithms"
    #

    $menu add cascade -label "Planarity Test" \
	-menu $menu.planarity_test
    menu $menu.planarity_test \
	-tearoff 0

    GT::add_menu_command $editor $menu.planarity_test {
	"Planarity Test" leda_planarity_test "" 0 active
    }

    GT::add_menu_command $editor $menu.planarity_test {
	"Find Kuratowski Subgraph" leda_kuratowski_subgraph "" 5 active
    }


    $menu add cascade -label "Connectivity Test" \
	-menu $menu.connectivity_test
    menu $menu.connectivity_test \
	-tearoff 0

    GT::add_menu_command $editor $menu.connectivity_test {
	"Connectivity Test" leda_connectivity_test "" 0
    }
    GT::add_menu_command $editor $menu.connectivity_test {
	"Strong Connectivity Test" leda_strong_connectivity_test "" 0
    }

    GT::add_menu_command $editor $menu.connectivity_test {
	"Label with Component Numbers"
	leda_label_with_component_numbers
    }
    GT::add_menu_command $editor $menu.connectivity_test {
	"Label with Strong Component Numbers"
	leda_label_with_strong_component_numbers
    }


    GT::add_menu_command $editor $menu {
	"Acyclic Test" leda_is_acyclic "" 0
    }

    GT::add_menu_command $editor $menu {
	"Topsort" leda_topsort "" 0
    }	


    $menu add cascade -label "BFS" \
	-menu $menu.bfs
    menu $menu.bfs \
	-tearoff 0

    GT::add_menu_command $editor $menu.bfs {
	"BFS" leda_bfs "" 0
    }

    GT::add_menu_command $editor $menu.bfs {
	"BFS Numbers" leda_bfs_numbers
    }	


    $menu add cascade -label "DFS" \
	-menu $menu.dfs
    menu $menu.dfs \
	-tearoff 0

    GT::add_menu_command $editor $menu.dfs {
	"DFS from Startnode" leda_dfs_from_start
    }

    GT::add_menu_command $editor $menu.dfs {
	"DFS Tree" leda_dfs_tree
    }

    GT::add_menu_command $editor $menu.dfs {
	"DFS Numbers" leda_dfs_numbers
    }

    GT::add_menu_command $editor $menu.dfs {
	"DFS Tree & Numbers" leda_dfs_tree_and_numbers
    }	


    GT::add_menu_command $editor $menu {
	"Max Flow" leda_max_flow "" 0
    }
}


proc GT::action_leda_planarity_test  { editor  } {

    global GT

    set cmd "graph_leda_planarity_test $GT($editor,graph)"
    set error [catch $cmd result]

    if { $error == 1 } {
	set msg [lindex $result 0]
	GT::message $editor $msg error
    } else {
	if $result {
	    GT::message $editor "The graph is planar"
	} else {
	    GT::message $editor "The graph is not planar"
	}
    }
}

proc GT::action_leda_kuratowski_subgraph  { editor  } {

    global GT
    set graph $GT($editor,graph)

    set cmd "graph_leda_planarity_test $graph -kuratowski"
    set error [catch $cmd result]

    if $error {
	set msg [lindex $result 0]
	GT::message $editor $msg error
    } else {

	set isplanar [lindex $result 0]
	if $isplanar {
	    GT::message $editor "The graph is planar"
	} else {

	    GT::message $editor "The graph is not planar"

	    set nodes {}
	    set edges [lindex $result 1]
	    foreach edge $edges {
		set source [$graph get $edge -source]
		if { [lsearch $nodes $source] == -1 } {
		    lappend nodes $source
		}
		set target [$graph get $edge -target]
		if { [lsearch $nodes $target] == -1 } {
		    lappend nodes $target
		}
	    }
	    eval GT::select $editor select \
		[concat $edges $nodes]
	}
    }
}



##########################################
#
# Connectivity Tests
#
##########################################


proc GT::action_leda_connectivity_test { editor  } {

    global GT
    set graph $GT($editor,graph)

    set error [catch {graph_leda_connectivity_test $graph} components]

    if !$error {
	if { $components == 1 } {
	    GT::message $editor "The graph is connected"
	} else {
	    GT::message $editor "The graph has $components components"
	}
    } else {
	GT::message $editor $components error
	return $components
    }
}


proc GT::action_leda_label_with_component_numbers { editor  } {

    global GT
    set graph $GT($editor,graph)

    set error [catch \
		   {graph_leda_connectivity_test $graph -list_numbers compnums} \
		   components]

    if !$error {
	foreach node [$graph nodes] {
	    $graph configure $node -label $compnums($node)
	}
	$graph draw
    } else {
	GT::message $editor $components error
	return $components
    }
}


proc GT::action_leda_strong_connectivity_test  { editor  } {

    global GT
    set graph $GT($editor,graph)

    set error [catch {graph_leda_connectivity_test $graph -strong} components]

    if !$error {
	if { $components == 1 } {
	    GT::message $editor "The graph is strong connected"
	} else {
	    GT::message $editor "The graph has $components strong components"
	}
    } else {
	GT::message $editor $components error       
	return $components
    }
}


proc GT::action_leda_label_with_strong_component_numbers { editor  } {

    global GT
    set graph $GT($editor,graph)

    set error [catch \
		   {graph_leda_connectivity_test $graph \
			-strong \
			-list_numbers compnums} \
		   components]

    if !$error {
	foreach node [$graph nodes] {
	    $graph configure $node -label $compnums($node)
	}
	$graph draw
    } else {
	GT::message $editor $components error
	return $components
    }
}


##########################################
#
# is_acyclic & topsort
#
##########################################

proc GT::action_leda_is_acyclic  { editor  } {

    global GT
    set graph $GT($editor,graph)

    set error [catch {graph_leda_topsort $graph} result]
    if $error {
	GT::message $editor $result error
	return $result
    } else {
	if $result {
	    GT::message $editor "The graph acyclic"
	} else {
	    GT::message $editor "The graph is not acyclic"
	}
    }
}


proc GT::action_leda_topsort  { editor  } {

    global GT
    set graph $GT($editor,graph)

    set error [catch {graph_leda_topsort $graph -list_ord ord} result]
    if $error {
	GT::message $editor $result error
	return $result
    } else {
	if $result {
	    foreach node [$graph nodes] {
		$graph configure $node -label $ord($node)
	    }
	} else {
	    GT::message $editor "Graph contains cycles" error
	}
    }

    $graph draw
}



##########################################
#
# bfs & bfs numbers
#
##########################################


proc GT::action_leda_bfs  { editor  } {

    global GT GT_selection
    set graph $GT($editor,graph)

    set selected_nodes $GT_selection($editor,selected,node)
    if { [llength $selected_nodes] != 1 } {
	GT::message $editor "No start node for BFS selected" error
	return

    } 

    set error [catch {graph_leda_bfs $graph -start $selected_nodes} result]
    if $error {
	GT::message $editor $result error
    } else {
	GT::select $editor select $result
    }
}



proc GT::action_leda_bfs_numbers  { editor  } {

    global GT GT_selection
    set graph $GT($editor,graph)

    set selected_nodes $GT_selection($editor,selected,node)
    if { [llength $selected_nodes] != 1 } {
	GT::message $editor "No start node for BFS selected" error
	return
    } 

    set error [catch \
		   {graph_leda_bfs $graph -start $selected_nodes -list_dist dist} \
		   result]

    if $error {
	GT::message $editor $result error
	return $result
    } else {
	foreach node [array names dist] {
	    $graph configure $node -label $dist($node)
	}
	GT::select $editor select $result
	$graph draw
    }
}




##########################################
#
# dfs & dfs numbers
#
##########################################


proc GT::action_leda_dfs_from_start  { editor  } {

    global GT GT_selection
    set graph $GT($editor,graph)

    set selected_nodes $GT_selection($editor,selected,node)
    if { [llength $selected_nodes] != 1 } {
	GT::message $editor "No start node for DFS selected" error
	return

    } 

    set error [catch {graph_leda_dfs $graph -start $selected_nodes} result]
    if $error {
	GT::message $editor $result error
	return $result
    } else {
	GT::select $editor select $result
    }
}



proc GT::action_leda_dfs_tree  { editor  } {

    global GT GT_selection
    set graph $GT($editor,graph)

    set error [catch {graph_leda_dfs $graph} result]
    if $error {
	GT::message $editor $result error
	return $result
    } else {
	GT::select $editor select $result
    }
}



proc GT::action_leda_dfs_numbers  { editor  } {

    global GT GT_selection
    set graph $GT($editor,graph)

    set error [catch {graph_leda_dfs $graph -list_dfsnum dfsnum} result]
    if $error {
	GT::message $editor $result error
	return $result
    } else {
	foreach node [array names dfsnum] {
	    $graph configure $node -label $dfsnum($node)
	}
	$graph draw
    }
}



proc GT::action_leda_dfs_tree_and_numbers  { editor  } {

    global GT GT_selection
    set graph $GT($editor,graph)

    set error [catch {graph_leda_dfs $graph -list_dfsnum dfsnum} result]
    if $error {
	GT::message $editor $result error
	return $result
    } else {
	foreach node [array names dfsnum] {
	    $graph configure $node -label $dfsnum($node)
	}
	$graph draw
	GT::select $editor select $result
    }
}



proc GT::action_leda_max_flow  { editor  } {

    global GT
    set graph $GT($editor,graph)

    foreach node [$graph nodes] {
	if {[$graph edges -in $node] == {}} {
	    set source $node
	}
	if {[$graph edges -out $node] == {}} {
	    set target $node
	}
    }

    foreach edge [$graph edges] {
	set label [$graph get $edge -label]
	if {$label == ""} {
	    GT::message $editor "Empty edge label" error
	    GT::select $editor $edge
	    return
	}
	set capacities($edge) [lindex [split [split $label :] ,] 0]
    }

    if ![info exists source] {
	GT::message $editor "Graph has no source" error
    } elseif ![info exists target] {
	GT::message $editor "Graph has no target" error
    } else {

	set error [catch {
	    graph_leda_max_flow $graph \
		-source $source \
		-target $target \
		-capacities capacities \
		-flow flow
	} result]
	if $error {
	    GT::message $editor $result error
	    return $result
	}
	
	foreach edge [$graph edges] {
	    $graph set $edge \
		-label "[$graph get $edge -label]:$flow($edge)"
	}
	GT::message $editor "Flow size: $result"
    }

    $graph draw
}

GT::init_leda_algorithms


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
