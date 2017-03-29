# This software is distributed under the Lesser General Public License
#
# action.tcl
#
# This module implements the actions.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/action.tcl,v $
# $Author: himsolt $
# $Revision: 1.6 $
# $Date: 1999/03/05 20:39:53 $
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
	action \
	menu_action \
	button_action
}

##########################################
#

# GT::action is a wrapper to call a action procedure.
# GT::menu_action is a wrapper to call a action procedure from a menu.
# GT::button_action is a wrapper to call a action procedure from a button.
#
# Parameters are
#  editor -- toplevel window
#  name   -- action name
#  args   -- argumts as passed to the action procedure
#
##########################################

proc GT::action { editor name args } {
	
    global GT

    if ![info exists GT(action,$name)] {
	if {[info commands ::GT::action_$name] != {}} {
	    set GT(action,$name) ::GT::action_$name
	} else {
	    return -code error
	}
    }

    if {$name != "undo"} {
	GT:::undo $editor newframe
    }

    eval $GT(action,$name) $editor $args
}


proc GT::menu_action { editor name args } {

    global GT

    if ![info exists GT(action,$name)] {
	if {[info commands ::GT::action_$name] != {}} {
	    set GT(action,$name) ::GT::action_$name
	} else {
	    return -code error
	}
    }

    if {$name != "undo"} {
	GT:::undo $editor newframe
    }

    eval $GT(action,$name) $editor $args
}


proc GT::button_action { editor name args } {

    global GT

    if ![info exists GT(action,$name)] {
	if {[info commands ::GT::action_$name] != {}} {
	    set GT(action,$name) ::GT::action_$name
	} else {
	    return -code error
	}
    }

    if {$name != "undo"} {
	GT:::undo $editor newframe
    }

    eval $GT(action,$name) $editor $args
}


#
# Empty action
#

proc GT::no_action  { editor } {
} 

##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
