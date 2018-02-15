# This software is distributed under the Lesser General Public License
#
# scale.tcl
#
# This module implements several test routines for the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/scale.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:40:22 $
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
	scale \
	action_scale

}


##########################################
#
# GT::scale
#
##########################################


proc GT::scale { editor scale } {

    global GT
    if [info exists GT($editor.zoomer,previous_value)] {
	set s [expr $scale/$GT($editor.zoomer,previous_value)]
    } else {
	set s $scale
    }
    set canvas $GT($editor,canvas)

    $canvas scale all 0 0 $s $s
    $GT($editor,graph) scale $s

    if [info exists GT($editor.zoomer,previous_value)] {
 	set GT($editor.zoomer,previous_value) $scale
    }
}


proc GT::action_scale { editor { scale {} } } {

    global GT_options

    if { $scale == {} } {
	set scale [expr double($GT_options(scale)) / 100.0]
    }

    GT::scale $editor $scale
}


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
