# This software is distributed under the Lesser General Public License
#
# algorithms/.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_gem.tcl,v $
# $Author: himsolt $
# $Revision: 1.11 $
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
package require Gt_lsd_gem

namespace eval GT {
    namespace export \
	LSD_action_layout_spring_gem \
	LSD_action_layout_spring_gem_options
}


proc GT::LSD_action_layout_spring_gem { editor } {
    
    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-name "Spring Embedder (GEM)" \
	-check_selection 0 \
	-command {

	    layout_gem $graph \
		-insertion_maximal_temperature $GT_options(gem_insert_max_temp) \
		-insertion_start_temperature $GT_options(gem_insert_start_temp) \
		-insertion_final_temperature $GT_options(gem_insert_final_temp) \
		-insertion_maximal_iterations $GT_options(gem_insert_max_iter) \
		-insertion_gravity $GT_options(gem_insert_gravity) \
		-insertion_oscilation $GT_options(gem_insert_oscilation) \
		-insertion_rotation $GT_options(gem_insert_rotation) \
		-insertion_shake $GT_options(gem_insert_shake) \
		-skip_insertion $GT_options(gem_insert_skip) \
		\
		-arrange_maximal_temperature $GT_options(gem_arrange_max_temp) \
		-arrange_start_temperature $GT_options(gem_arrange_start_temp) \
		-arrange_final_temperature $GT_options(gem_arrange_final_temp) \
		-arrange_maximal_iterations $GT_options(gem_arrange_max_iter) \
		-arrange_gravity $GT_options(gem_arrange_gravity) \
		-arrange_oscilation $GT_options(gem_arrange_oscilation) \
		-arrange_rotation $GT_options(gem_arrange_rotation) \
		-arrange_shake $GT_options(gem_arrange_shake) \
		-skip_arrange $GT_options(gem_arrange_skip) \
		\
		-optimize_maximal_temperature $GT_options(gem_optimize_max_temp) \
		-optimize_start_temperature $GT_options(gem_optimize_start_temp) \
		-optimize_final_temperature $GT_options(gem_optimize_final_temp) \
		-optimize_maximal_iterations $GT_options(gem_optimize_max_iter) \
		-optimize_gravity $GT_options(gem_optimize_gravity) \
		-optimize_oscilation $GT_options(gem_optimize_oscilation) \
		-optimize_rotation $GT_options(gem_optimize_rotation) \
		-optimize_shake $GT_options(gem_optimize_shake) \
		-skip_optimize $GT_options(gem_optimize_skip) \
		\
		-random $GT_options(gem_random) \
		-quality $GT_options(gem_quality) \
		-edgelength $GT_options(gem_default_edgelength)
	}
}



proc GT::LSD_action_layout_spring_gem_options { editor } {

    global GT GT_options

    lappend options {
	newtab "General"
    } {
	check "Random Placement" gem_random
	"Initial placement of the nodes."
    } {
	check "Quality Check" gem_quality
	""
    } {
	integer "Edge Length" gem_default_edgelength
	"Desired edge length and distance between nodes."
    } {
	endtab
    }

    lappend options {
	newfolder "Insert"
    } {
	check "Skip" gem_insert_skip
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
    } {
	float "Maximum Temperature" gem_insert_max_temp
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Start Temperature" gem_insert_start_temp
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from 0.3
	-to 100
    } {
	float "Final Temperature" gem_insert_final_temp
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .001
	-to 100
    } {
	integer "Maximum Number of Iterations" gem_insert_max_iter
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from 1
	-to 100
    } {
	float "Gravity" gem_insert_gravity
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .001
	-to 100
    } {
	float "Oscilation" gem_insert_oscilation
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Rotation" gem_insert_rotation
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Shake" gem_insert_shake
	"Parameters for the iteration phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	endtab
    }
	
    lappend options { newfolder
	"Arrange"
    } {
	check "Skip" gem_arrange_skip
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
    } {
	float "Maximum Temperature" gem_arrange_max_temp
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Start Temperature" gem_arrange_start_temp
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Final Temperature" gem_arrange_final_temp
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .001
	-to 100
    } {
	integer "Maximum Number of Iterations" gem_arrange_max_iter
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from 1
	-to 100
    } {
	float "Gravity" gem_arrange_gravity
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .001
	-to 100
    } {
	float "Oscilation" gem_arrange_oscilation
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Rotation" gem_arrange_rotation
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Shake" gem_arrange_shake
	"Parameters for the arrangement phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	endtab
    }

    lappend options { newfolder
	"Optimize"
    } {
	float "Maximum Temperature" gem_optimize_max_temp
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Start Temperature" gem_optimize_start_temp
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Final Temperature" gem_optimize_final_temp
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .001
	-to 100
    } {
	integer "Maximum Number of Iterations" gem_optimize_max_iter
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from 1
	-to 100
    } {
	float "Gravity" gem_optimize_gravity
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .001
	-to 100
    } {
	float "Oscilation" gem_optimize_oscilation
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Rotation" gem_optimize_rotation
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	float "Shake" gem_optimize_shake
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
	-scale on
	-from .01
	-to 100
    } {
	check "Skip" gem_optimize_skip
	"Parameters for the optimization phase.
High values may improve the quality, however they increase the run time."
    } {
	endtab
    }

    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    springembedder_gem_options "Spring Embedder (GEM) Options" \
	    $options layout_spring_gem
    } else {
	GT::create_tabwindow $editor \
	    springembedder_gem_options "Spring Embedder (GEM) Options" \
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
