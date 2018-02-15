# This software is distributed under the Lesser General Public License
#
# algorithms/layout_icse.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_icse.tcl,v $
# $Author: himsolt $
# $Revision: 1.10 $
# $Date: 1999/04/10 15:27:09 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

package require Gt_cfr_layout
package require Gt_icse_layout

namespace eval GT {
    namespace export \
	action_layout_iterative_constraint_se \
	action_layout_iterative_constraint_se_options
}


####################################################
#
# Iterative Constraint Springembedder
#
# In the current version this algorithm shares its
# variables with its sub-algorithm constraint_fr.
# I am not sure, if this is a feature ...
#
####################################################


proc GT::action_layout_iterative_constraint_se { editor } {
    
    global GT GT_options
    set graph $GT($editor,graph)

    set window_width [winfo width $editor.drawing.canvas]
    set window_height [winfo height $editor.drawing.canvas]
    set canvas_width [lindex \
			  [$editor.drawing.canvas cget -scrollregion] 2]
    set canvas_height [lindex \
			   [$editor.drawing.canvas cget -scrollregion] 3]
    set xview [lindex [$editor.drawing.canvas xview] 0]
    set yview [lindex [$editor.drawing.canvas yview] 0]
    set xoffset [expr ($xview * $canvas_width)]
    set yoffset [expr ($yview * $canvas_height)]

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::remove_bends $editor

    GT::excursion $editor \
	-check_selection 0 \
	-name "Spring Embedder (ICSE)" \
	-command {

	    layout_iterative_constraint_se $GT($editor,graph) \
		-animation $GT_options(constraint_fr_animation) \
		-optimal_distance $GT_options(constraint_fr_optimal_distance) \
		-constraint_minimal_distance $GT_options(constraint_fr_minimal_distance) \
		-respect_sizes $GT_options(constraint_fr_respect_sizes) \
		-iteration1 $GT_options(constraint_fr_phase1_iteration) \
		-iteration2 $GT_options(constraint_fr_phase2_iteration) \
		-iteration3 $GT_options(constraint_fr_phase3_iteration) \
		-damping1 $GT_options(constraint_fr_phase1_damping) \
		-damping2 $GT_options(constraint_fr_phase2_damping) \
		-damping3 $GT_options(constraint_fr_phase3_damping) \
		-vibration_ratio $GT_options(constraint_fr_vibration_ratio) \
		-minimal_force $GT_options(constraint_fr_minimal_force) \
		-width $window_width \
		-height $window_height \
		-xoffset $xoffset \
		-yoffset $yoffset \
		-random $GT_options(constraint_fr_random) \
		-new_bends $GT_options(constraint_fr_new_bends) \
		-delimiter $GT_options(constraint_fr_delimiter)
	}
}


proc GT::action_layout_iterative_constraint_se_options  { editor  } {

    global GT GT_options

    lappend options {
	tab "General"
    } {
	integer "Optimal Edgelength (0=Auto)" constraint_fr_optimal_distance
	""
	-scale on
	-from 0
	-to 300
    } {
	integer "Constraint Distance (0=Auto)" constraint_fr_minimal_distance
	""
	-scale on
	-from 0
	-to 300
    } {
	integer "Animation Speed (0=Off)" constraint_fr_animation
	"0: No animation and highest speed
n: Show intermediate stage every n-th round"
	-scale on
	-from 0
	-to 50
    } {
	string "Label/Constraints Delimiter" constraint_fr_delimiter
	"Choose a seldom used character. Default is ','.
The label after the delimiter is interpreted as a constraint: h = horizontal,  v = vertical"
    } {
	check "Initial Random Placement" constraint_fr_random
	"Initial node positions are generated at random.
Otherwise, start with the last placement."
    } {
	check "Respect Node Sizes" constraint_fr_respect_sizes
	"If this option is enabled, then optimal distances are measured from the borders of the nodes."
    } {
	check "Create Edges with Bends" constraint_fr_new_bends
	""
    } {
	endtab
    }


    lappend options {
	tab "Change Heuristics"
    } {
	float "Vibration Dection Ratio" constraint_fr_vibration_ratio
	""
	-scale on
	-from 0.0
	-to 1.0
    } {
	float "Minimal Force" constraint_fr_minimal_force
	""
	-scale on
	-from 0.0
	-to 10.0
    } {
	newgroup "Phases"
    } {
	integer "Phase 1 Maximal Iterations |V| *" constraint_fr_phase1_iteration
	""
    } {
	float "Phase 1 Damping" constraint_fr_phase1_damping
	""
	-scale on
	-from 1.0
	-to 100.0
    } {
	integer "Phase 2 Maximal Iterations |V| *" constraint_fr_phase2_iteration
	""
    } {
	float "Phase 2 Damping" constraint_fr_phase2_damping
	""
	-scale on
	-from 1.0
	-to 100.0
    } {
	integer "Phase 3 Maximal Iterations |V| *" constraint_fr_phase3_iteration
	""
    } {
	float "Phase 3 Damping" constraint_fr_phase3_damping
	""
	-scale on
	-from 1.0
	-to 100.0
    } {
	endgroup
    } {
	endfolder
    }

    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    layout_icse_options "Iterative Constraint Springembedder" \
	    $options layout_iterative_constraint_se
    } else {
	GT::create_tabwindow $editor \
	    layout_icse_options "Iterative Constraint Springembedder" \
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
