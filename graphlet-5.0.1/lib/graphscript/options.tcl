# This software is distributed under the Lesser General Public License
#
# options.tcl
#
# This file manages the options.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/options.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:40:15 $
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
	get_option \
	action_options_save \
	save_options \
	restore_options
}


##########################################
#
# GT::option editor option default
#
# Support for global/local options. Returns
#
# - $GT::option($editor,$option) if is exists
# - $GT::option($option) if it exists
# - $default otherwise
#
# The default value for the parameter default is {}.
#
##########################################

proc GT::get_option { editor option {default {}} } {

    global GT_options

    if [info exists GT_options($editor,$option)] {
	return $GT_options($editor,$option)
    } elseif [info exists GT_options($option)] {
	return $GT_options($option)
    } else {
	return $default
    }
}


##########################################
#
#  GT::action_options_save 
#
##########################################


proc GT::action_options_save  { editor  } {

    global GT_options tk_strictMotif

    if [catch { open $GT_options(optionsfile) w } prefs] {

	GT::message $editor "Cannot open $GT_options(optionsfile)" error

    } else {

	foreach option [array names GT_options] {
	    set value $GT_options($option)
	    if { $value != {} } {
		puts $prefs "set GT_options($option) {$value}"
	    } else {
		puts $prefs "set GT_options($option) {}"
	    }
	}

	puts $prefs "set tk_strictMotif $tk_strictMotif"
	close $prefs

	GT::message $editor "Wrote $GT_options(optionsfile)"
    }
}



##########################################
#
# GT::save_options save_in_array_name options_name pattern
# GT::restore_options save_in_array_name options_name pattern
#
##########################################


proc GT::save_options {
    save_in_array_name
    { options_name GT_options }
    { pattern *}
} {

    upvar #0 $options_name options
    upvar #0 $save_in_array_name save_in_array

    foreach option [array names options $pattern] {
	set save_in_array($option) $options($option)
    }
}


proc GT::restore_options {
    save_in_array_name
    { options_name GT_options }
    { pattern *}
} {

    upvar #0 $options_name options
    upvar #0 $save_in_array_name save_in_array

    debug {
	puts $options_name
	puts $save_in_array_name
	parray options
	parray save_in_array
    }

    foreach option [array names save_in_array $pattern] {
	set options($option) $save_in_array($option)
    }
}


##########################################
#
#  GT::action_revert_options_to_defaults
#
##########################################


proc GT::action_revert_options_to_defaults args {
    global GT
    source [file join $GT(graphscript_dir) init options.tcl]
    GT_init_options
    source [file join $GT(graphscript_dir) init postscript.tcl]
    GT_init_postscript_options
}


proc GT::action_revert_options_to_factory_settings args {
    global GT_options GT_default_options
    foreach i [array names GT_default_options] {
	set GT_options($i) $GT_default_options($i)
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
