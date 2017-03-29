# This software is distributed under the Lesser General Public License
#
# modebar.tcl
#
# This module implements the modebar of the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/editor/modebar.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
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
	create_modebar \
	init_status
}


#
# Create Toolbar
#

proc GT::create_modebar { editor } {

    global GT GT_status GT_modes GT_menu

    set modebar [frame $editor.modebar -relief groove -borderwidth 2]

    foreach mode $GT_modes(modes) {
	add_mode_button $editor $mode $editor.modebar 
    }

    label $editor.modebar.separator \
	-image [GT::get_image invisible.xbm]
    pack $editor.modebar.separator

    return $modebar
}


proc GT::add_mode_button {editor mode {window {}}} {

    global GT_modes

    if {$window == {}} {
	set window $editor.modebar
    }

    if [info exists GT_modes($mode,name)] {
	
	set image [GT::get_image $mode]
	if { $image != {} } {
	    set image_switch image
	    set image_data $image
	} else {
	    set image_switch text
	    set image_data $GT_modes($mode,name)
	}

	set mode_button [button \
			     $window.[string tolower $mode] \
			     -$image_switch $image_data \
			     -command [list GT::action $editor $mode] \
			     -activebackground white \
			     -relief flat \
			     -borderwidth 1]
	bind $mode_button <Enter> \
	    +[list ::GT::enter_mode_button $mode_button]
	bind $mode_button <Leave> \
	    +[list ::GT::leave_mode_button $mode_button]
	    
	if [info exists GT_modes($mode,tooltips)] {
	    GT::tooltips $mode_button $GT_modes($mode,tooltips)
	}

	pack $mode_button
    }
}


proc GT::enter_mode_button {button} {

    global GT
    variable priv

    set relief [$button cget -relief]
    if {$relief == "flat"} {
 	$button configure \
	    -relief raised
    } else {
	$button configure \
	    -background [lindex [$button configure -background] 3]
    }
}


proc GT::leave_mode_button {button} {

    global GT

    set relief [$button cget -relief]
    if {$relief == "raised"} {
	$button configure \
	    -relief flat \
	    -background [lindex [$button configure -background] 3]
    } else {
 	$button configure \
 	    -background white
    }
}


proc GT::init_status { graph } {

    global GT_status

    set GT_status($graph,nodes) [llength [$graph nodes]]
    set GT_status($graph,edges) [llength [$graph edges]]
    set GT_status($graph,dirty) 0

    set GT($graph,autonumber_nodes) -1
    set GT($graph,autonumber_edges) -1
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
