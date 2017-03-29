# This software is distributed under the Lesser General Public License
##########################################
#
# The
#
# Graphlet Unified Algorithms Interface (GUAI)
#
##########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/guai.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:40:34 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	run_algorithm
}



proc GT::run_algorithm {graph algorithm args} {

    global GT_options

    #
    # Tunkelang
    #

    array set arguments {

	tunkelang,-edgelength  $GT_options(tunkelang_edgelength)
	tunkelang,-quality     $GT_options(tunkelang_quality)
	tunkelang,-depth       $GT_options(tunkelang_depth)
	tunkelang,-randomize   $GT_options(tunkelang_randomize)
	tunkelang,-cut         $GT_options(tunkelang_cut)
	tunkelang,-scan_corner $GT_options(tunkelang_scan_corner)

	tunkelang,command      layout_tunkelang
	tunkelang,packages     {Gt_lsd Gt_lsd_tunkelang}
	tunkelang,required_arguments     {
	    -edgelength
	    -quality
	    -depth
	    -randomize
	    -cut
	    -scan_corner
	}
    }
    
    #
    # Random
    #

    array set arguments {
	random,packages        Gt_algorithms
	random,command         layout_random
    }
    
    #
    # GEM
    #

    array set arguments {

	gem,-insert_max_temp $GT_options(gem_insert_max_temp)
	gem,-insert_start_temp $GT_options(gem_insert_start_temp)
	gem,-insert_final_temp $GT_options(gem_insert_final_temp)
	gem,-insert_max_iter $GT_options(gem_insert_max_iter)
	gem,-insert_gravity $GT_options(gem_insert_gravity)
	gem,-insert_oscilation $GT_options(gem_insert_oscilation)
	gem,-insert_rotation $GT_options(gem_insert_rotation)
	gem,-insert_shake $GT_options(gem_insert_shake)
	gem,-insert_skip $GT_options(gem_insert_skip)

	gem,-arrange_max_temp $GT_options(gem_arrange_max_temp)
	gem,-arrange_start_temp $GT_options(gem_arrange_start_temp)
	gem,-arrange_final_temp $GT_options(gem_arrange_final_temp)
	gem,-arrange_max_iter $GT_options(gem_arrange_max_iter)
	gem,-arrange_gravity $GT_options(gem_arrange_gravity)
	gem,-arrange_oscilation $GT_options(gem_arrange_oscilation)
	gem,-arrange_rotation $GT_options(gem_arrange_rotation)
	gem,-arrange_shake $GT_options(gem_arrange_shake)
	gem,-arrange_skip $GT_options(gem_arrange_skip)

	gem,-optimize_max_temp $GT_options(gem_optimize_max_temp)
	gem,-optimize_start_temp $GT_options(gem_optimize_start_temp)
	gem,-optimize_final_temp $GT_options(gem_optimize_final_temp)
	gem,-optimize_max_iter $GT_options(gem_optimize_max_iter)
	gem,-optimize_gravity $GT_options(gem_optimize_gravity)
	gem,-optimize_oscilation $GT_options(gem_optimize_oscilation)
	gem,-optimize_rotation $GT_options(gem_optimize_rotation)
	gem,-optimize_shake $GT_options(gem_optimize_shake)
	gem,-optimize_skip $GT_options(gem_optimize_skip)

	gem,-random $GT_options(gem_random)
	gem,-quality $GT_options(gem_quality)
	gem,-default_edgelength $GT_options(gem_default_edgelength)

	gem,command layout_gem
	gem,packages {Gt_lsd Gt_lsd_gem}
	gem,required_arguments {
	    -insert_max_temp
	    -insert_start_temp
	    -insert_final_temp
	    -insert_max_iter
	    -insert_gravity
	    -insert_oscilation
	    -insert_rotation
	    -insert_shake
	    -insert_skip

	    -arrange_max_temp
	    -arrange_start_temp
	    -arrange_final_temp
	    -arrange_max_iter
	    -arrange_gravity
	    -arrange_oscilation
	    -arrange_rotation
	    -arrange_shake
	    -arrange_skip

	    -optimize_max_temp
	    -optimize_start_temp
	    -optimize_final_temp
	    -optimize_max_iter
	    -optimize_gravity
	    -optimize_oscilation
	    -optimize_rotation
	    -optimize_shake
	    -optimize_skip

	    -random
	    -quality
	    -default_edgelength
	}
    }

    #
    # CSE
    #

    array set arguments {
	cse,-animation $GT_options(constraint_fr_animation)
	cse,-optimal_distance $GT_options(constraint_fr_optimal_distance)
	cse,-constraint_minimal_distance $GT_options(constraint_fr_minimal_distance)
	cse,-constraint_forces $GT_options(constraint_fr_constraint_forces)
	cse,-respect_sizes $GT_options(constraint_fr_respect_sizes)
	cse,-iteration1 $GT_options(constraint_fr_phase1_iteration)
	cse,-iteration2 $GT_options(constraint_fr_phase2_iteration)
	cse,-iteration3 $GT_options(constraint_fr_phase3_iteration)
	cse,-damping1 $GT_options(constraint_fr_phase1_damping)
	cse,-damping2 $GT_options(constraint_fr_phase2_damping)
	cse,-damping3 $GT_options(constraint_fr_phase3_damping)
	cse,-colour_nodes $GT_options(constraint_fr_colour_nodes)
	cse,-vibration_ratio $GT_options(constraint_fr_vibration_ratio)
	cse,-minimal_force $GT_options(constraint_fr_minimal_force)
	cse,-width $window_width
	cse,-height $window_height
	cse,-xoffset $xoffset
	cse,-yoffset $yoffset
	cse,-random $GT_options(constraint_fr_random)
	cse,-delimiter $GT_options(constraint_fr_delimiter)

	cse,command layout_constraint_fr
	cse,packages Gt_cfr_layout
    }

    #
    # ICSE
    #

    array set arguments {
	
	icse,-animation $GT_options(constraint_fr_animation)
	icse,-optimal_distance $GT_options(constraint_fr_optimal_distance)
	icse,-constraint_minimal_distance $GT_options(constraint_fr_minimal_distance)
	icse,-respect_sizes $GT_options(constraint_fr_respect_sizes)
	icse,-iteration1 $GT_options(constraint_fr_phase1_iteration)
	icse,-iteration2 $GT_options(constraint_fr_phase2_iteration)
	icse,-iteration3 $GT_options(constraint_fr_phase3_iteration)
	icse,-damping1 $GT_options(constraint_fr_phase1_damping)
	icse,-damping2 $GT_options(constraint_fr_phase2_damping)
	icse,-damping3 $GT_options(constraint_fr_phase3_damping)
	icse,-vibration_ratio $GT_options(constraint_fr_vibration_ratio)
	icse,-minimal_force $GT_options(constraint_fr_minimal_force)
	icse,-width $window_width
	icse,-height $window_height
	icse,-xoffset $xoffset
	icse,-yoffset $yoffset
	icse,-random $GT_options(constraint_fr_random)
	icse,-new_bends $GT_options(constraint_fr_new_bends)
	icse,-delimiter $GT_options(constraint_fr_delimiter)
    
	icse,command layout_iterative_constraint_se
	icse,packages Gt_icse_layout
    }

    #
    # Fruchterman/Reingold (FR)
    #

    array set arguments {
	
	fr,-weighted  $GT_options(spring_fr_weighted)
	fr,-max_force $GT_options(spring_fr_max_force)
	fr,-vibration $GT_options(spring_fr_vibration)
	fr,-max_iter  $GT_options(spring_fr_max_iter)
	fr,-edgelen   $GT_options(spring_fr_edgelen)

	fr,command layout_spring_fr
	fr,packages {Gt_lsd Gt_lsd_springembedder_rf}
	fr,required_arguments {
	    -weighted
	    -max_force
	    -vibration
	    -max_iter
	    -edgelen
	}
    }

    #
    # Kamada/Kawai (KK)
    #

    array set arguments {
	
	kk,-edgelength $GT_options(spring_kk_edgelength)

	kk,command layout_spring_kk
	kk,packages {Gt_lsd Gt_lsd_springembedder_kamada}
	kk,required_arguments {
	    -edgelength
	}
    }

    #
    # Sugiyama (dag)
    #

    array set arguments {

	dag,-sugiyama_vert_dist $GT_options(sugiyama_vert_dist)
	dag,-sugiyama_horiz_dist $GT_options(sugiyama_horiz_dist)
	dag,-sugiyama_it1 $GT_options(sugiyama_it1)
	dag,-sugiyama_it2 $GT_options(sugiyama_it2)
	dag,-sugiyama_level_arrange $GT_options(sugiyama_level_arrange)
	dag,-sugiyama_resolve_cycles $GT_options(sugiyama_resolve_cycles)
	dag,-sugiyama_reduce_crossings $GT_options(sugiyama_reduce_crossings) 
	
	dag,command layout_dag
	dag,packages {Gt_lsd Gt_lsd_sugiyama}
	dag,required_arguments {
	    -sugiyama_vert_dist
	    -sugiyama_horiz_dist
	    -sugiyama_it1
	    -sugiyama_it2
	    -sugiyama_level_arrange
	    -sugiyama_resolve_cycles
	    -sugiyama_reduce_crossings
	}
    }

    #
    # Tree
    #

    array set arguments {

	tree,-tree_tr_leveling $GT_options(tree_tr_leveling)
	tree,-tree_tr_orientation $GT_options(tree_tr_orientation)
	tree,-tree_tr_direction $GT_options(tree_tr_direction)
	tree,-tree_tr_routing $GT_options(tree_tr_routing)
	tree,-tree_tr_father_place $GT_options(tree_tr_father_place)
	tree,-tree_tr_permutation $GT_options(tree_tr_permutation)
	tree,-tree_tr_vert_node_node $GT_options(tree_tr_vert_node_node)
	tree,-tree_tr_hor_node_node $GT_options(tree_tr_hor_node_node)
	tree,-tree_tr_node_edge $GT_options(tree_tr_node_edge)
	tree,-tree_tr_channel_width $GT_options(tree_tr_channel_width)
	tree,-tree_tr_edge_connection $GT_options(tree_tr_edge_connection)
	tree,-tree_tr_bend_reduction $GT_options(tree_tr_bend_reduction)
	tree,-tree_tr_edge_connection_for_bend $GT_options(tree_tr_edge_connection_for_bend)
	tree,-tree_tr_marked_root $GT_options(tree_tr_marked_root)

	tree,command layout_extended_tr_tree
	tree,packages Gt_tree_layout
	tree,required_arguments {
	    -tree_tr_leveling
	    -tree_tr_orientation
	    -tree_tr_direction
	    -tree_tr_routing
	    -tree_tr_father_place
	    -tree_tr_permutation
	    -tree_tr_vert_node_node
	    -tree_tr_hor_node_node
	    -tree_tr_node_edge
	    -tree_tr_channel_width
	    -tree_tr_edge_connection
	    -tree_tr_bend_reduction
	    -tree_tr_edge_connection_for_bend
	    -tree_tr_marked_root
	}
    }

    #
    # Collect the local arguments
    #

    foreach {attribute value} $args {
	set local_arguments($attribute) $value
    }

    #
    # Merge the local arguments with the required arguments of
    # the algorithm
    #

    if [info exists arguments($algorithm,required_arguments)] {
	foreach attribute $arguments($algorithm,required_arguments) {
	    if [info exists local_arguments($attribute)] {
		lappend flat_arguments $local_arguments($attribute)
	    } else {
		eval lappend flat_arguments $arguments($algorithm,$attribute)
	    }
	}
    } else {
	foreach a [array names arguments $algorithm,-*] {
	    set attribute [lindex [split $a ,] 1]
	    if [info exists local_arguments($attribute)] {
		lappend flat_arguments $attribute $local_arguments($attribute)
	    } else {
		eval lappend flat_arguments $attribute $arguments($algorithm,$attribute)
	    }
	}
	foreach attribute [array names local_arguments] {
	    lappend flat_arguments $attribute $local_arguments($attribute)
	}
    }

    #
    # Load all required packages
    #

    if [info exists arguments($algorithm,packages)] {
	foreach package $arguments($algorithm,packages) {
	    package require $package
	}
    }

    #
    # Execute the commands ... and off we go !
    #

    set command $arguments($algorithm,command)
    if [info exists flat_arguments] {
	eval $command $graph $flat_arguments
    } else {
	eval $command $graph
    }
}


# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
