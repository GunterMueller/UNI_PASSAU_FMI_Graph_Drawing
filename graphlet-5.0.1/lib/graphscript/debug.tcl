# This software is distributed under the Lesser General Public License
#
# debug.tcl
#
# This module implements several utilities for the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/debug.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:40:00 $
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
	    debug \
	    debug+
}

##########################################
#
# debug string
#
# A debugging utility.
#
##########################################

proc GT::debug proc {

    global GT GT_options tcl_platform

    if $GT_options(debug) {
	if {$tcl_platform(platform) == "unix"} {
	    puts "proc: [info level 1]"
	    if { $proc != { } } {
		uplevel $proc
	    }
	} else {
	    lappend GT(error_messages) "proc: [info level 1]"
	}
    }
}

proc GT::debug+ proc {

    global GT GT_options tcl_platform

    if $GT_options(debug) {
	if {$tcl_platform(platform) == "unix"} {
	    puts "proc: [info level 1]"
	    if { $proc != { } } {
		uplevel $proc
	    }
	} else {
	    for {set i 0} {$i < [info level]} {incr i} {
		lappend GT(error_messages) proc: [info level -$i]
	    }
	}
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
