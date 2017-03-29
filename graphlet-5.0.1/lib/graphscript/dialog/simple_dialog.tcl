# This software is distributed under the Lesser General Public License
#
# lsd_algorithms.tcl
#
# This module implements menu commands which are specific for the
# lsd algorithms.
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/simple_dialog.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:41:24 $
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
	simple_dialog_radio_input \
	simple_dialog_check_input \
	simple_dialog_int_input \
	simple_dialog_float_input \
	simple_dialog_string_input \
	simple_dialog
}

set GT(LA_dialog,label_gap) 5
set GT(LA_dialog,float_entry_width) 10
set GT(LA_dialog,string_entry_width) 32

#
# Create a radiobutton as an input-element
#

proc GT::simple_dialog_radio_input { editor framedescription w framename } {
    
    global GT GT_options
    set cur $editor.$framename

    set description [lindex $framedescription 1]
    set var [lindex $framedescription 2]
    set items [lindex $framedescription 3]

    frame $cur

    label $cur.l \
	-text $description \
	-width $w \
	-anchor e
    pack $cur.l \
	-side left \
	-padx $GT(LA_dialog,label_gap)

    set item_nr 0
    foreach item $items {
	if { [llength $item] == 1 } {
	    radiobutton $cur.$item_nr \
		-text $item \
		-variable GT_options($var) \
		-value $item_nr
	} else {
	    radiobutton $cur.$item_nr \
		-text [lindex $item 0] \
		-variable GT_options($var) \
		-value [lindex $item 1]
	}
	pack $cur.$item_nr \
	    -side left
	incr item_nr
    }

    pack $cur \
	-anchor w \
	-padx 5 \
	-pady 2
}

#
# Input-element with a checkbox.
#

proc GT::simple_dialog_check_input { editor framedescription w framename } {
    
    global GT GT_options
    set cur $editor.$framename

    set description [lindex $framedescription 1]
    set var [lindex $framedescription 2]

    frame $cur

    label $cur.l \
	-text $description \
	-width $w \
	-anchor e
    pack $cur.l \
	-side left \
	-padx $GT(LA_dialog,label_gap)

    checkbutton $cur.c \
	-variable GT_options($var)
    pack $cur.c \
	-side left

    pack $cur \
	-anchor w \
	-padx 5 \
	-pady 2
}

#
# Create a input-element to read a integer
# In:  parent framedescription w(idth) 
#      framedescrition is a list. See contents at the argument spliting
#

proc GT::simple_dialog_int_input { editor framedescription w framename } {

    global GT_options
    set cur $editor.$framename
    set description [lindex $framedescription 1]
    set var [lindex $framedescription 2]
    if { [lindex $framedescription 3] != {} } {
	set goody_parameters [lindex $framedescription 3]
	switch [lindex $goody_parameters 0] {
	    scale {
		set scale_from [lindex $goody_parameters 1]
		set scale_to [lindex $goody_parameters 2]
		set goody scale
	    }
	}
    } else {
	set goody {}
    }
    
    global GT

    frame $cur

    label $cur.l \
	-text $description \
	-width $w \
	-anchor e
    entry $cur.e \
	-width 4 \
	-textvariable GT_options($var)
    if { $goody == "scale" } {
	scale $cur.s \
	    -orient horizontal \
	    -from $scale_from \
	    -to $scale_to \
	    -showvalue false \
	    -variable GT_options($var)
    }

    pack $cur.l \
	-side left \
	-padx $GT(LA_dialog,label_gap)
    pack $cur.e \
	-side left
    if { $goody == "scale" } {
	pack $cur.s \
	    -side left
    }

    pack $cur \
	-anchor w \
	-padx 5 \
	-pady 2
}

proc GT::simple_dialog_float_input { editor framedescription w framename } {

    global GT_options

    set cur $editor.$framename

    set description [lindex $framedescription 1]
    set var [lindex $framedescription 2]
    if { [lindex $framedescription 3] != {} } {
	set goody_parameters [lindex $framedescription 3]
	switch [lindex $goody_parameters 0] {
	    scale {
		set scale_from [lindex $goody_parameters 1]
		set scale_to [lindex $goody_parameters 2]
		set goody scale
	    }
	}
    } else {
	set goody {}
    }
    
    global GT

    frame $cur

    label $cur.l -text $description \
	-padx 3 \
	-width $w \
	-anchor e
    entry $cur.e \
	-width $GT(LA_dialog,float_entry_width) \
	-textvariable GT_options($var)
    if { $goody == "scale" } {
	scale $cur.s \
	    -orient horizontal \
	    -from $scale_from \
	    -to $scale_to \
	    -showvalue false \
	    -variable GT_options($var) \
	    -resolution 0
    }

    pack $cur.l \
	-side left \
	-anchor w \
	-padx $GT(LA_dialog,label_gap)
    pack $cur.e \
	-side left
    if { $goody == "scale" } {
	pack $cur.s \
	    -side left
    }

    pack $cur \
	-anchor w \
	-padx 5 \
	-pady 2
}


##########################################
#
# proc GT::simple_dialog_string_input
#
##########################################


proc GT::simple_dialog_string_input { editor framedescription w framename } {

    global GT_options

    set cur $editor.$framename

    set description [lindex $framedescription 1]
    set var [lindex $framedescription 2]
    
    global GT

    frame $cur

    label $cur.l -text $description \
	-padx 3 \
	-width $w \
	-anchor e
    entry $cur.e \
	-width $GT(LA_dialog,string_entry_width) \
	-textvariable GT_options($var)

    pack $cur.l \
	-side left \
	-anchor w \
	-padx $GT(LA_dialog,label_gap)
    pack $cur.e \
	-side left

    pack $cur \
	-anchor w \
	-padx 5 \
	-pady 2
}



proc GT::simple_dialog { editor var_dialog parameters { action {} } } {

    upvar $var_dialog dialog

    set window $editor.$dialog(name)

    # do nothing if dialog exists
    if [winfo exists $window] {
	raise $window
	wm deiconify $window
	return
    }


    #
    # Scan parameters
    #
    # - compute tabbings
    # - compute longest label
    # - initialize variables (to be changed)
    #

    global GT GT_options GT_default_options

    set tab 0
    set tabbings [list [list la_dialog_$tab "General"]]
    set variables {}
    set width 0

    foreach p $parameters {
	switch -regexp [lindex $p 0] {
	    newfolder|newtab {
		incr tab
		lappend tabbings \
		    [list la_dialog_$tab [lindex $p 1]]
	    }
	    endfolder|endtab {
	    }
	    default {

		set w [string length [lindex $p 1]]
		if { $w > $width } {
		    set width $w
		}

		set var [lindex $p 2]
		if ![info exists GT($window,$var,reset)] {
		    set GT($window,$var,reset) $GT_options($var)
		}

		lappend variables $var
	    }
	}
    }


    #
    # Create the window
    #

    if { $action != {} } {
	set command [list GT::action $editor $action]
    } else {
	set command {}
    }
    GT::DU::tab_window $window create \
	-title $dialog(title) \
	-tabs $tabbings \
	-command $command \
	-variables $variables

    #
    # Entries
    #

    set tab 0
    set tabbing la_dialog_0
    set i 0
    foreach p $parameters {
	switch -regexp [lindex $p 0] {
	    integer {
		GT::simple_dialog_int_input $window.$tabbing $p \
		    $width \
		    la_$i
		incr i
	    }
	    float {
		GT::simple_dialog_float_input $window.$tabbing $p \
		    $width \
		    la_$i
		incr i
	    }
	    radio {
		GT::simple_dialog_radio_input $window.$tabbing $p \
		    $width \
		    la_$i
		incr i
	    }
	    check {
		GT::simple_dialog_check_input $window.$tabbing $p \
		    $width \
		    la_$i
		incr i
	    }
	    string|color {
		GT::simple_dialog_string_input $window.$tabbing $p \
		    $width \
		    la_$i
		incr i
	    }
 	    newfolder|newtab {
		incr tab
		set tabbing la_dialog_$tab
 	    }
 	    endfolder|endtab {
		set tabbing la_dialog_0
 	    }
	}
    }

    GT::position_window_near_graph $editor $window near

    # return the name of the dialog
    return $window
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
