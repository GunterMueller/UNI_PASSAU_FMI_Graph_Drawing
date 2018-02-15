# This software is distributed under the Lesser General Public License
#
# algorithms/.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_fr.tcl,v $
# $Author: himsolt $
# $Revision: 1.12 $
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

package require Gt_lsd
package require Gt_lsd_springembedder_rf

namespace eval GT {
    namespace export \
	LSD_action_layout_spring_fr \
	LSD_action_layout_spring_fr_options
}

##############################
#
# Springembedder/FR.
#
##############################

#
# This is the run-function. It is called when Run-Button is
# pressed. Name of the function must be unique. First argument to
# function layout_spring_fr is always the same. The rest depends
# on the algorithm.
# Note: GT_options(..) is set by the functions above. You must
#       replace the name for which the dots stand.
#       NAMING CONVENTION: Names have to be unique. Because of
#       that, a part of the variable-name must be the algorithm
#       name.
#

proc GT::LSD_action_layout_spring_fr { editor } {
    
    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-check_selection 0 \
	-name "Spring Embedder Layout (FR)" \
	-command {
	    layout_spring_fr $graph \
		-weighted $GT_options(spring_fr_weighted) \
		-maximal_force $GT_options(spring_fr_max_force) \
		-vibration $GT_options(spring_fr_vibration) \
		-maximal_iterations $GT_options(spring_fr_max_iter) \
		-edgelength $GT_options(spring_fr_edgelen)
	}
}



proc GT::LSD_action_layout_spring_fr_options { editor } {

    global GT GT_options

    # Create array with input-fields 
    lappend options {
	newtab "General"
    } {
	check "Weighted Layout" spring_fr_weighted
	"Individual edge length defined by numeric edge labels are 
taken into account."
    } {
	integer "Edge Length" spring_fr_edgelen
	"Desired edge length and distance between nodes."
    } {
	newgroup "Iteration Parameters"
    } {
	float "Maximum Force" spring_fr_max_force
	""
	-scale on
	-from 1.0
	-to 10.0
    } {
	float "Vibration" spring_fr_vibration
	""
	-scale on
	-from 0.01
	-to 1.0
	-res 0.01
    } {
	integer "Maximum number of Iterations" spring_fr_max_iter
	""
	-scale on
	-from 10
	-to 10000
    } {
	endgroup
    } {
	endtab
    }

    # Create the dialog

    if {$GT_options(expert) == 1} {
	GT::create_tabwindow $editor \
	    spring_fr_options "Spring Embedder (FR) Options" \
	    $options layout_spring_fr
    } else {
	GT::create_tabwindow $editor \
	    spring_fr_options "Spring Embedder (FR) Options" \
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
