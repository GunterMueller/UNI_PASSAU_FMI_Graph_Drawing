# This software is distributed under the Lesser General Public License
#
# algorithms/layout_radial_tree.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_radial_tree.tcl,v $
# $Author: boerncke $
# $Revision: 1.14 $
# $Date: 1999/03/08 12:06:06 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#


package require Graphlet
package provide Graphscript [gt_version]

package require Gt_radial_tree_layout

namespace eval GT_layout_radial_tree {
    namespace export \
	init \
	init_Layout_menu \
	init_Layout_options_menu \
	add_layout_menu_entries \
	add_layout_options_menu_entries
}

namespace eval GT {
    namespace export \
	action_layout_radial \
	action_layout_radial_options
}


##########################################
#
# Initialization code
#
##########################################

proc GT_layout_radial_tree::init { } {

    global GT GT_menu GT_default_options

    global GT_default_options

    array set GT_default_options {
	radial_root 2
	radial_parent_child_distance 32
	radial_distance_rigid_flexible_flag 0
	radial_padding_factor 6
	radial_padding_type 1
	radial_avoid_escaping_edges 0
	radial_avoid_collinear_families 0  
	radial_center_parent 1 
	radial_center_children 1
	radial_fill_space 1
	radial_enforce_corradiality 0
	radial_Eades 0
	radial_Eades_avoid_crossing_edges 2
	radial_Eades_border_leaves 1
    }

    array set GT {
	action,layout_radial GT::action_layout_radial
	action,layout_radial_options GT::action_layout_radial_options
    }

#     radial_hshift 0
#     radial_vshift 0
#     radial_automatic_expansion 0
#     radial_eades 1
#     radial_eades2 1
#     radial_eades_straight 1
#     radial_eades_pi 1
#     radial_debug 1

}



proc GT_layout_radial_tree::add_layout_menu_entries {editor menu} {

    global GT

    GT::add_menu_command $editor $menu {
	"Tree (^Radial)" layout_radial ""
    }
    GT::add_menu_command $editor $menu {
	"Tree (Co^ncentric)" layout_concentric ""
    }
}


proc GT_layout_radial_tree::add_layout_options_menu_entries {editor menu} {

    global GT

    GT::add_menu_command $editor $menu {
	"Tree (^Radial) ..." layout_radial_options ""
    }
    GT::add_menu_command $editor $menu {
	"Tree (Co^ncentric) ..." layout_concentric_options ""
    }
}

# (fb) changed
#		    -eades $GT_options(radial_Eades) 
#		    -fill_space $GT_options(radial_fill_space) 
# to 
#		    -eades 0
#		    -fill_space 1

proc GT::action_layout_radial { editor } {

    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-check_selection 0 \
	-name "Tree Layout (Radial)" \
	-command {
	    if [catch { 
		layout_radial $graph \
		    -root $GT_options(radial_root) \
		    -father_son $GT_options(radial_parent_child_distance) \
		    -rigid_distance $GT_options(radial_distance_rigid_flexible_flag) \
		    -padding_factor $GT_options(radial_padding_factor) \
		    -padding_type $GT_options(radial_padding_type) \
		    -escaping_edges $GT_options(radial_avoid_escaping_edges) \
		    -collinear_families $GT_options(radial_avoid_collinear_families) \
		    -center_parent $GT_options(radial_center_parent) \
		    -center_children $GT_options(radial_center_children) \
		    -fill_space 1 \
		    -corradiality $GT_options(radial_enforce_corradiality) \
		    -eades 0 \
		    -eades_avoid_crossing $GT_options(radial_Eades_avoid_crossing_edges) \
		    -eades_border_leaves $GT_options(radial_Eades_border_leaves) } \
		    error_message] {
		GT::select  $editor [lindex $error_message 1]
		tk_dialog .my_errormsg "Tree Layout" \
		    [lindex $error_message 0] error 0 "Ok"	
	    }	
	}
}

# (fb) changed
#		    -eades $GT_options(radial_Eades) 
#		    -fill_space $GT_options(radial_fill_space) 
# to 
#		    -eades 1
#		    -fill_space 1

proc GT::action_layout_concentric { editor } {

    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-check_selection 0 \
	-name "Tree Layout (Concentric)" \
	-command {
	    if [catch { 
		layout_radial $graph \
		    -root $GT_options(radial_root) \
		    -father_son $GT_options(radial_parent_child_distance) \
		    -rigid_distance $GT_options(radial_distance_rigid_flexible_flag) \
		    -padding_factor $GT_options(radial_padding_factor) \
		    -padding_type $GT_options(radial_padding_type) \
		    -escaping_edges $GT_options(radial_avoid_escaping_edges) \
		    -collinear_families $GT_options(radial_avoid_collinear_families) \
		    -center_parent $GT_options(radial_center_parent) \
		    -center_children $GT_options(radial_center_children) \
		    -fill_space 1 \
		    -corradiality $GT_options(radial_enforce_corradiality) \
		    -eades 1 \
		    -eades_avoid_crossing $GT_options(radial_Eades_avoid_crossing_edges) \
		    -eades_border_leaves $GT_options(radial_Eades_border_leaves) } \
		    error_message] {
		GT::select  $editor [lindex $error_message 1]
		tk_dialog .my_errormsg "Tree Layout" \
		    [lindex $error_message 0] error 0 "Ok"	
	    }	
	}
}



proc GT::action_layout_radial_options { editor } {

    global GT GT_options
    
    #    lappend options { integer
    #    "Horizontal Shift"
    #    radial_hshift
    #    { scale 0 100 }
    #}

    #lappend options { integer
    #"Vertical Shift"
    #radial_vshift
    #{ scale 0 100 }
    #}

    #lappend options { radio
    #"Automatic Expansion"
    #radial_automatic_expansion
    #{ yes no }
    #}


# Changes concerning strings made by Frank Börncke (fb)

    lappend options {
	tab "General"
    } {
	radio "Center" radial_root
	"Choose the node for the center.\nPick the 'most important' node."
	{
	    {"Largest  node  " 1}
	    {"Graph center  " 2}
	    {"Root  " 3}
	}
    } {
	integer "Father/Son Distance" radial_parent_child_distance
	"Distance between the borders of adjacent nodes. \nThe radii of adjacent concentric circles differ at least by the radii of \nneighbored nodes and this distance."
	-scale on
	-from 1
	-to 100
    } {
	radio "Varying with Node Size" radial_distance_rigid_flexible_flag
	"Varies the Father/Son distances by a factor from zero to two."
	{
	    {"Yes" 1}
	    {"No" 0}
	}
    } {
	integer "Arc Distance" radial_padding_factor
	"Distance of a node from the border of its sector."
	-scale on
	-from 0
	-to 20
    } {
	radio "Adjust Arc Distance" radial_padding_type
	"Varies the Arc Distance by a factor from zero to two according to the size of \nthe nodes."
	{
	    {"Yes" 1}
	    {"No" 0}
	}
    } {
	radio "Circularity" radial_enforce_corradiality
	" individual: individual father/son distance\n\
               local: same father/son distance for all sons\n\
               global: all nodes on concentric circles around the center"
	{
	    {"Individual  " 0}
	    {"Local  " 1}
	    {"Global  " 2}
	}
    } {
	endtab
    }

    #options "escaping edges" to "uniform distribution"
    #originally, these were RADIO
    #with settings "0 = YES" and "1 = NO" 
    #now, they are CHECK
    #as check assigns "0 = NO" and "1 = YES" (you can't foresee them all ...)
    #parameter passing in file radial_tree_layout.cpp
    #has been modified to pass "1 - x" instead of "x"

    lappend options { tab
	"Options"
    } {
	check "Centralize" radial_center_parent
	"Place each node in the middle of its sector. Should be enabled."
    } {
	check "Spacing" radial_center_children
	"Distribute free space uniformly. Should be enabled."
    } {
	check "Avoid Edge Crossings" radial_avoid_escaping_edges
	"Rare edge crossings can be avoided; but it costs space."
    } {
	check "Avoid Node-Edge Crossings" radial_avoid_collinear_families
	"Avoid rare passings of edges through lined-up nodes."
    } {
	endtab
    }

    #testing Eades
    #lappend options { radio
    #"EADES"
    #radial_eades
    #{ yes no }
    #}

    #lappend options { radio
    #"EADES2"
    #radial_eades2
    #{ yes no }
    #}

    #lappend options { radio
    #"EADES straight"
    #radial_eades_straight
    #{ yes no }
    #}

    #lappend options { radio
    #"EADES pi"
    #radial_eades_pi
    #{ yes no }
    #}


    #        FOR DEVELOPERS:
    #        the debugger is still there
    #        if you wish to make it visible again
    #        change statement
    #        current_algorithm->set_debug(1);
    #        to
    #        current_algorithm->set_debug(debug);
    #        in file
    #        radial_tree_layout.cpp
    #        and delete all # and // comments 
    #        concerning its parameter passing
    #        in this file and in radial_tree_layout.cpp
    #

    #lappend options { radio
    #"DEBUG"
    #radial_debug
    #{ yes no }
    #}

# following region changed by (fb)
#     lappend options {
# 	tab "Eades"
#     } {
# 	check "Eades" radial_Eades
# 	"Eades"
#     } {
# 	radio "Avoid Crossing Edges" radial_Eades_avoid_crossing_edges
# 	"Avoid Crossing Edges"
# 	{
# 	    {Yes 0}
# 	    {Partial 1}
# 	    {No 2}
# 	}
#     } {
# 	radio "Border Leaves" radial_Eades_border_leaves
# 	"Border Leaves"
# 	{
# 	    {Yes 0}
# 	    {No 1}
# 	}
#     } {
# 	endtab
#     }



    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    radial_layout_options "Radial Layout" \
	    $options layout_radial
    } else {
	GT::create_tabwindow $editor \
	    radial_layout_options "Radial Layout" \
	    $options
    }
}

#following  options added by (fb)
proc GT::action_layout_concentric_options { editor } {

    global GT GT_options
    
    lappend options {
	tab "General"
    } {
	radio "Center" radial_root
	"Choose the node for the center. Pick the 'most important' node."
	{
	    {"Largest  node  " 1}
	    {"Graph center  " 2}
	    {"Root  " 3}
	}
    } {
	integer "Circle Distance" radial_parent_child_distance
	"Default distance between two concentric circles."
	-scale on
	-from 1
	-to 100
    } {
	radio "Leaves on outer Circle" radial_Eades_border_leaves
	"Leaves are placed along the most outside circle."
	{
	    {"Yes  " 0}
	    {"No  " 1}
	}
    } {
	radio "Avoid Crossing Edges" radial_Eades_avoid_crossing_edges
	"Check for edge crossings."
	{
	    {"Yes  " 0}
	    {"Partial  " 1}
	    {"No  " 2}
	}
    } {
	endtab
    }


    #testing Eades
    #lappend options { radio
    #"EADES"
    #radial_eades
    #{ yes no }
    #}

    #lappend options { radio
    #"EADES2"
    #radial_eades2
    #{ yes no }
    #}

    #lappend options { radio
    #"EADES straight"
    #radial_eades_straight
    #{ yes no }
    #}

    #lappend options { radio
    #"EADES pi"
    #radial_eades_pi
    #{ yes no }
    #}


    #        FOR DEVELOPERS:
    #        the debugger is still there
    #        if you wish to make it visible again
    #        change statement
    #        current_algorithm->set_debug(1);
    #        to
    #        current_algorithm->set_debug(debug);
    #        in file
    #        radial_tree_layout.cpp
    #        and delete all # and // comments 
    #        concerning its parameter passing
    #        in this file and in radial_tree_layout.cpp
    #

    #lappend options { radio
    #"DEBUG"
    #radial_debug
    #{ yes no }
    #}

    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    radial_layout_options "Concentric Layout" \
	    $options layout_radial
    } else {
	GT::create_tabwindow $editor \
	    radial_layout_options "Concentric Layout" \
	    $options
    }
}



#
# Initialization procedure
#

GT_layout_radial_tree::init
