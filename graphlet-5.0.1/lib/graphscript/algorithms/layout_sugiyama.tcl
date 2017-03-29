# This software is distributed under the Lesser General Public License
#
# algorithms/layout_sugiyama.tcl
#
# 
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_sugiyama.tcl,v $
# $Author: himsolt $
# $Revision: 1.12 $
# $Date: 1999/04/10 15:27:10 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

package require Gt_lsd
package require Gt_lsd_sugiyama

namespace eval GT {
    namespace export \
	action_layout_sugiyama \
	action_layout_sugiyama_options
}

proc GT::action_layout_sugiyama { editor } {

    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-check_selection 0 \
	-name "DAG Layout" \
	-command {
	    layout_dag $graph \
		-vertical_distance $GT_options(sugiyama_vert_dist) \
		-horizontal_distance $GT_options(sugiyama_horiz_dist) \
		-it1 $GT_options(sugiyama_it1) \
		-it2 $GT_options(sugiyama_it2) \
		-arrange $GT_options(sugiyama_level_arrange) \
		-resolve_cycles $GT_options(sugiyama_resolve_cycles) \
		-reduce_crossings $GT_options(sugiyama_reduce_crossings) 
	}
}


proc GT::action_layout_sugiyama_options { editor } {

    global GT GT_options

    #     lappend options { integer
    # 	"It 1"
    # 	sugiyama_it1
    # 	{ scale 1 10 }
    #     }
    #     lappend options { integer
    # 	"It 2"
    # 	sugiyama_it2
    # 	{ scale 1 10 }
    #     }

    lappend options {
	tab "General"
    } {
	group "Distances"
    } {
	integer "Vertical" sugiyama_vert_dist
	"Vertical distance between adjacent levels.
Choose at least the height of the vertices."
    } {
	integer "Horizontal" sugiyama_horiz_dist
	"Horizontal distance between vertices and/or edges on the same level.
Choose at least the width of the vertices."
    } {
	endgroup
    } {
	group "Algorithm"
    } {
	radio "Leveling" sugiyama_level_arrange
	"Low: Place a node as low (late) as possible (ALAP)
High: Place a node as high (soon) as possible (ASAP)
Medium:  A recursive method for an intermediate level."
	{
	    {"Low" 0}
	    {"High" 1}
	    {"Medium" 2}
	}
    } {
	radio "Resolve cycles" sugiyama_resolve_cycles
	"Reverse edges at node with higest indegree or outdegree
or compute by a divide and conquer strategy. "
	{
	    {"Highest In" 0}
	    {"Highest Out" 1}
	    {"Divide and Conquer" 2}
	}
    } {
	radio "Reduce Crossings" sugiyama_reduce_crossings
	"Barycenter: Sort vertices on the same level by the barycenter of their neighbors on the adjacent levels."
	{
	    {"Barrycenter" 0}
	    {"Bubbling" 1}
	}
    } {
	endgroup
    } {
	endtab
    }

    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    dag_layout_options "DAG Layout Options" \
	    $options layout_sugiyama
    } else {
	GT::create_tabwindow $editor \
	    dag_layout_options "DAG Layout Options" \
	    $options
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
