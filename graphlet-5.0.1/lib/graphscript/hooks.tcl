# This software is distributed under the Lesser General Public License
#
# hook.tcl
#
# The this class implements the basic functions and data
# structures for manipulating hooks.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/hooks.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:40:08 $
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
	add_hook \
	remove_hook \
	run_hooks
    variable hooks
}


#
# Hook management
#

proc GT::add_hook { hook procs } {

    variable hooks
    
    foreach proc $procs {
	lappend hooks($hook) $proc
    }
}

proc GT::remove_hook { hook procs } {

    variable hooks

    foreach proc $procs {
	ldelete hooks($hook) $proc
    }
}

#
# GT::run_hooks
#
# Note: from C++, hooks are called with a more efficient and native
# implementation
#

proc GT::run_hooks { hook graph args } {

    variable hooks

    if [info exists hooks($hook)] {
	foreach h $hooks($hook) {
 	    set res [eval $h $hook $graph $args]
 	    if { $res != {} } {
 		return $res
 	    }
	}
    }

    return {}
}


#
# Hook debugging
#


proc GT::debug_hook { hook graph args } {
    # puts "$hook $graph $args"
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
