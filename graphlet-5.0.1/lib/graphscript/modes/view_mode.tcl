# This software is distributed under the Lesser General Public License
#
# This file implements Graphlet's view mode. The view mode is a
# read only mode where no modifications are allowed.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/modes/view_mode.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:42:30 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#


package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	action_view_mode \
	leave_view_mode
}

###########################################
#
# View mode action
#
###########################################


proc GT::action_view_mode  { top { blocker {} } } {

    global GT

    set GT(view_mode_blocked_by) $blocker
    GT::switch_to_mode $top view_mode
}


##########################################
#
# Leave view mode
#
# View mode can be left only if view mode is not blocked. The
# global variable GT(view_mode_blocked_by) holds the blocker.
#
##########################################


proc GT::leave_view_mode { top mode } {
    
    global GT

    if [info exists GT(view_mode_blocked_by)] {
	if { $GT(view_mode_blocked_by) == {} } {
	    return {}
	} else {
	    return "View mode blocked by $GT(view_mode_blocked_by)"
	}
    } else {
	return {}
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
