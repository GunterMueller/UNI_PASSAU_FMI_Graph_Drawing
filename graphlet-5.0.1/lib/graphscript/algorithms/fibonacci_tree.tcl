# This software is distributed under the Lesser General Public License
#
# Graphlet sample module: create Fibonacci Trees
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/fibonacci_tree.tcl,v $
# $Author: himsolt $
# $Revision: 1.7 $
# $Date: 1999/03/05 20:40:32 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]


namespace eval GT_Fibonacci_Tree {

    namespace export \
	init \
	create_menu \
	action_generate_fibonacci_tree \
	generate_from_editor \
	generate

    proc init { {menu {}} } {

	global GT GT_menu
	if {[lsearch $GT(menubar) $menu] == -1} {
	    lappend GT(menubar) $menu
	}

	if {$menu != {}} {
	    lappend GT_menu($menu,create_procs) GT_Fibonacci_Tree::create_menu
	}
    }

    proc create_menu {editor menu} {

	global GT
	set GT(action,generate_fibonacci_tree) \
	    GT_Fibonacci_Tree::action_generate_fibonacci_tree

	GT::create_graphsize_menu $editor $menu \
	    "Generate ^Fibonacci Tree" \
	    generate_fibonacci_tree \
	    { 1 2 3 4 5 6 7 8 9 10 "Other ..." }
    }

    proc action_generate_fibonacci_tree {editor n args} {
	return [eval GT::action_generate_graph $editor $n \
		    GT_Fibonacci_Tree::generate_from_menu \
		    {{Fibonacci Tree}} \
		    Simple \
		    $args]
    }

    
    #
    # generate
    #

    proc generate_from_menu {editor depth args} {

	global GT
	set graph $GT($editor,graph)

	$graph set -directed 1
	eval generate $graph $depth $args
 	GT::run_algorithm $graph tree
   }

    proc generate {graph depth args} {

	switch $depth {
	    0 {
		set root {}
	    }
	    1 {
		set root [$graph create node]
	    }
	    default {
		set left [generate $graph [expr $depth-2]]
		set right [generate $graph [expr $depth-1]]
		set root [$graph create node]
		if {$right != {}} {
		    set e2 [$graph create edge $root $right]
		}
		if {$left != {}} {
		    set e1 [$graph create edge $root $left]
		}
	    }
	}
	return $root
    }
}
