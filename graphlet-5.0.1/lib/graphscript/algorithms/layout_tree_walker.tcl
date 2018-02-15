# This software is distributed under the Lesser General Public License
#
# algorithms/.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_tree_walker.tcl,v $
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
package require Gt_lsd_tree_layout_walker

namespace eval GT {
    namespace export \
	LSD_action_layout_tree_walker \
	LSD_action_layout_tree_walker_options
}

proc GT::LSD_action_layout_tree_walker { editor } {
    
    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor -name "Tree Layout (Walker)" {
	if [catch { 
	    layout_tree_walker $graph \
		-vertical_separation $GT_options(walker_Vertical) \
		-sibling_separation $GT_options(walker_Sibling) \
		-subtree_separation $GT_options(walker_Subtree) }\
		error_message] {
	    GT::select  $editor [lindex $error_message 1]
	    tk_dialog .my_errormsg "Tree Layout Walker" \
		[lindex $error_message 0] error 0 "Ok"	
	}	
    }
    
}



proc GT::LSD_action_layout_tree_walker_options { editor } {

    global GT GT_options

    lappend options {
	tab "General"
    } {
	group "Separations"
    } {
	integer "Vertical Distance" walker_Vertical
	"Vertical distance between node and its descendants."
    } {
	integer "Horizontal Distance" walker_Sibling
	"Horizontal distance between descendants of a node."
    } {
	integer "Subtree Distance" walker_Subtree
	"Horizontal distance between Subtrees.
For nice layouts it should be larger than the horizontal separation."
    } {
	endgroup
    } {
	endtab
    }

    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    layout_tree_walker_options "Tree Layout Options (Walker)" \
	    $options layout_tree_walker
    } else {
	GT::create_tabwindow $editor \
	    layout_tree_walker_options "Tree Layout Options (Walker)" \
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
