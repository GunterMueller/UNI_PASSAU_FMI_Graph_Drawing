# This software is distributed under the Lesser General Public License
#
# tabwindow.tcl
#
# This module implements a tab window similar to those in MS
# Windows. Code by Walter Bachl, Michael Forster und Michael
# Himsolt. Current maintainer is Michael Himsolt.
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/widgets/tabwindow.tcl,v $
# $Author: himsolt $
# $Revision: 1.16 $
# $Date: 1999/04/10 15:27:12 $
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
	create_tabwindow
}

namespace eval GT::tabwindow {
}

namespace eval GT::Widgets {
    variable options
    array set options {
	label_gap 5
	number_entry_width 10
	string_entry_width 30
	scale_width 100
	frame_padx 5
	frame_pady 2
	frame_label_gap 10
    }
    namespace export \
 	group \
 	menu \
 	radio \
 	checkbox \
 	number
}


proc GT::Widgets::group {top name {label ""} args} {

    variable options

    set pack 1
    for {set i 0} {$i < [llength $args]} {incr i} {
	switch -- [lindex $args $i] {
	    -pack {
		set pack [lindex $args [incr i]]
	    }
	    default {
		error "Unknown switch [lindex [lindex $args $i] 0]"
	    }
	}
    }

    set outer_frame [frame $top.$name]

    set groove [frame $outer_frame.groove \
		    -relief groove \
		    -bd 2]

    label $outer_frame.label \
	-text $label
    set height \
	[expr \
	     [font metrics [$outer_frame.label cget -font] -linespace] + \
	     [$outer_frame cget -borderwidth] + \
	     [$outer_frame.label cget -borderwidth]]
    if {$label != {}} {
	place $outer_frame.label \
	    -in $groove \
	    -bordermode outside \
	    -x $options(frame_label_gap) \
	    -y -[expr $height/2]
    }

    frame $groove.inner

    pack $groove.inner \
	-fill x \
	-expand true \
	-pady [expr $height/2] \
	-padx [expr $height/2]

    pack $groove \
	-pady [expr $height/2] \
	-padx [expr $height/2] \
	-fill x \
	-expand true

    if {$pack} {
	pack $outer_frame \
	    -fill x \
	    -expand true
    }

    return $groove.inner
}


##########################################
#
# GT::Widgets::menu
#
##########################################


proc GT::Widgets::menu { top label label_width variable values args } {

    global GT GT_options
    variable options

    set frame [frame $top.var$variable]

    if {$label != ""} {
	label $frame.label \
	    -text $label \
	    -anchor e
	if {$label_width != {} && $label_width > 0} {
	    $frame.label configure \
		-width $label_width
	}
	pack $frame.label \
	    -side left
    }

    Combobox::create $frame.menubutton \
	-textvariable GT_options($variable)
    pack $frame.menubutton \
	-side left

    foreach value $values {
	Combobox::add $frame.menubutton $value
	if { $value == $GT_options($variable) } {
	    Combobox::assign $frame.menubutton $value
	}
    }

    return $frame
}


##########################################
#
# GT::Widgets::radio
#
##########################################


proc GT::Widgets::radio { top label label_width variable values args } {

    global GT GT_options
    variable options

    set frame [frame $top.var$variable]

    set direction horizontal
    for {set i 0} {$i < [llength $args]} {incr i} {
	switch -- [lindex $args $i] {
	    -vertical {
		set direction vertical
	    }
	    -horizontal {
		set direction horizontal
	    }
	    -direction {
		set direction [lindex $args [incr i]]
	    }
	}
    }

    if {$label != {}} {
	label $frame.label \
	    -text $label
	if {$label_width != {} && $label_width > 0} {
	    $frame.label configure \
		-width $label_width
	}
	if {$direction == "horizontal"} {
	    $frame.label configure \
		-anchor e
	    pack $frame.label \
		-side left
	} else {
	    $frame.label configure \
		-anchor w
	    pack $frame.label \
		-side top
	}
    }

    set item_nr 0
    foreach item $values {

	if { [llength $item] == 1 } {
	    set label $item
	    set value $item
	} else {
	    set label [lindex $item 0]
	    set value [lindex $item 1]
	}

	radiobutton $frame.$item_nr \
	    -text $label \
	    -variable GT_options($variable) \
	    -value $value \
	    -anchor w
	if {$direction == "horizontal"} {
	    pack $frame.$item_nr \
		-side left \
		-anchor w
	} else {
	    pack $frame.$item_nr \
		-side top \
		-anchor w
	}
	incr item_nr
    }

    return $frame
}



##########################################
#
# GT::Widgets::checkbox
#
##########################################


proc GT::Widgets::checkbox { top label label_width variable args } {

    global GT GT_options
    variable options

    set frame [frame $top.var$variable]

    checkbutton $frame.checkbutton \
	-variable GT_options($variable) \
	-anchor w
    pack $frame.checkbutton \
	-side left

    label $frame.label \
	-text $label \
	-anchor e

    pack $frame.label \
	-side left

    return $frame
}


##########################################
#
# GT::Widgets::number
#
##########################################


proc GT::Widgets::number { top label label_width variable args } {

    global GT GT_options
    variable options

    set scale 0
    set from 0
    set to 1
    set res 1
    set scale_args {}
    for {set i 0} {$i < [llength $args]} {incr i} {
	switch -glob -- [lindex $args $i] {
	    -scale {
		set scale [lindex $args [incr i]]
	    }
	    -from {
		set from [lindex $args [incr i]]
	    }
	    -to {
		set to [lindex $args [incr i]]
	    }
	    -res {
		set res [lindex $args [incr i]]
	    }
	    -scale_args {
		set scale_args [lindex $args [incr i]]
	    }
	    default {
		error "Unknown Option [lindex $args $i]"
	    }
	}
    }

    set frame [frame $top.$variable]

    label $frame.label \
	-text $label \
	-anchor e
    if {$label_width != {} && $label_width > 0} {
	$frame.label configure \
	    -width $label_width
    }
    pack $frame.label \
	-side left
    entry $frame.entry \
	-width $options(number_entry_width) \
	-textvariable GT_options($variable)
    pack $frame.entry \
	-side left

    if {$scale} {
	eval scale $frame.scale \
	    -orient horizontal \
	    -from $from \
	    -to $to \
	    -showvalue false \
	    -variable GT_options($variable) \
	    -resolution $res \
	    -length $options(scale_width) \
	    $scale_args
	pack $frame.scale \
	    -side left \
	    -fill x \
	    -expand true
    }

    return $frame
}


##########################################
#
# proc GT::Widgets::string
#
##########################################


proc GT::Widgets::string { top label label_width variable args } {

    global GT GT_options
    variable options

    set frame [frame $top.$variable]

    label $frame.label \
	-text $label \
	-anchor e
    if {$label_width != {} && $label_width > 0} {
	$frame.label configure \
	    -width $label_width
    }
    pack $frame.label \
	-side left

    entry $frame.entry \
	-width $options(string_entry_width) \
	-textvariable GT_options($variable)
    pack $frame.entry \
	-side left \
	-fill x \
	-expand true

    return $frame
}


##########################################
#
# Tabwindow main procedure
#
##########################################


proc GT::tabwindow::interpret {window options var_label_width} {

    upvar $var_label_width label_width
    set tab 0
    set group_nr 0
    set current_group {}

    foreach option $options {

	set command [lindex $option 0]
	set label [lindex $option 1]
	set var [lindex $option 2]
	set tooltips [lindex $option 3]
	if {$tooltips == ""} {
	    set tooltips ""
	}

	switch -regexp [lindex $option 0] {

	    integer|float|number {
		if {$current_group == {}} {
		    set group group[incr group_nr]
		    set current_group \
			[GT::Widgets::group $window.tab$tab $group]
		}
		set frame [eval GT::Widgets::number $current_group \
			       [list $label] $label_width($tab) $var \
			       [lrange $option 4 end]]
		pack $frame \
		    -anchor w \
		    -fill x \
		    -expand true
# 		GT::tooltips $frame $tooltips
		GT::tabwindow::show_help $frame $window $tab $tooltips
	    }

	    option_menu|optionmenu|menu {
		if {$current_group == {}} {
		    set group group[incr group_nr]
		    set current_group \
			[GT::Widgets::group $window.tab$tab $group]
		}
		set frame [eval GT::Widgets::menu $current_group \
			       [list $label] $label_width($tab) $var \
			       [list [lindex $option 4]] \
			       [lrange $option 5 end]]
		pack $frame \
		    -anchor w \
		    -fill x \
		    -expand true
# 		GT::tooltips $frame $tooltips
		GT::tabwindow::show_help $frame $window $tab $tooltips
	    }

	    radio {
		if {$current_group == {}} {
		    set group group[incr group_nr]
		    set current_group \
			[GT::Widgets::group $window.tab$tab $group]
		}
		set frame [eval GT::Widgets::radio $current_group \
			       [list $label]  $label_width($tab) $var \
			       [list [lindex $option 4]] \
			       [lrange $option 5 end]]
		pack $frame \
		    -anchor w \
		    -fill x \
		    -expand true
# 		GT::tooltips $frame $tooltips
		GT::tabwindow::show_help $frame $window $tab $tooltips
	    }

	    check|checkbox {
		if {$current_group == {}} {
		    set group group[incr group_nr]
		    set current_group \
			[GT::Widgets::group $window.tab$tab $group]
		}
		set frame [eval GT::Widgets::checkbox $current_group \
			       [list $label] $label_width($tab) $var \
			       [lrange $option 4 end]]
		pack $frame \
		    -anchor w \
		    -fill x \
		    -expand true
# 		GT::tooltips $frame $tooltips
		GT::tabwindow::show_help $frame $window $tab $tooltips
	    }

	    string|color {
		if {$current_group == {}} {
		    set group group[incr group_nr]
		    set current_group \
			[GT::Widgets::group $window.tab$tab $group]
		}
		set frame [eval GT::Widgets::string $current_group \
			       [list $label] $label_width($tab) $var \
			       [lrange $option 4 end]]
		pack $frame \
		    -anchor w \
		    -fill x \
		    -expand true
# 		GT::tooltips $frame $tooltips
		GT::tabwindow::show_help $frame $window $tab $tooltips
	    }

	    ^group$|newgroup {
		end_current_group $window $tab $current_group
		# Add new group
		set label [lindex $option 1]
		set name group[incr group_nr]
		set current_group \
		    [GT::Widgets::group $window.tab$tab $name $label]
	    }

	    endgroup {
		end_current_group $window $tab $current_group
	    }

	    ^tab$|^folder$|newfolder|newtab {
		end_current_group $window $tab $current_group
		incr tab
		set current_group {}
	    }

	    endfolder|endtab {
		text $window.tab$tab.help \
		    -height 4 \
		    -background [$window.tab$tab cget -background]
		bind $window.tab$tab.help <Key> {break}
		#bind $window.tab$tab.help <Button> {break}
		pack $window.tab$tab.help \
		    -expand true \
		    -fill x \
		    -pady 5
		end_current_tab $window $tab $current_group
	    }

	    default {
		error "Unknown switch [lindex $option 0] in tabwindow"
	    }
	}

    }

    end_current_tab $window $tab $current_group
}


proc GT::tabwindow::end_current_group {window current_tab current_group} {

    if {$current_group != {}} {
	pack $current_group \
	    -anchor w \
	    -fill x \
	    -expand true
	pack [winfo parent $current_group] \
	    -anchor w \
	    -fill x \
	    -expand true
    }
}


proc GT::tabwindow::end_current_tab {window current_tab current_group} {

    end_current_group $window $current_tab $current_group
}


proc GT::tabwindow::show_help {frame window current_tab text} {

    bind $frame <Enter> \
	[list $window.tab$current_tab.help insert 0.0 $text]
    bind $frame <Leave>  \
	[list $window.tab$current_tab.help delete 0.0 end]
}


proc GT::create_tabwindow { editor name title options {action {}} } {

    global GT GT_options GT_default_options

    set window $editor.$name

    # do nothing but raise if dialog exists
    if [winfo exists $window] {
	raise $window
	wm deiconify $window
	return
    }

    #
    # Interpret parameters & Create tabwindow
    #

    set tab 0
    foreach option $options {
	switch -regexp [lindex $option 0] {
	    ^folder$|^tab$|newfolder|newtab {
		incr tab
		lappend tabbings [list tab$tab [lindex $option 1]]
	    }
	    integer|float|number|.*menu|radio|string|color {
		lappend tabs($tab) [lindex $option 1]
		lappend variables [lindex $option 2]
	    }
	    check.* {
		lappend tabs($tab) ""
		lappend variables [lindex $option 2]
	    }
	}
    }

    foreach variable $variables {
	if ![info exists GT($window,$variable,reset)] {
	    set GT($window,$variable,reset) $GT_options($variable)
	}
    }

    foreach tab [array names tabs] {
	set maxlabelwidth($tab) 0
	foreach label $tabs($tab) {
	    if {[string length $label] > $maxlabelwidth($tab)} {
		set maxlabelwidth($tab) [string length $label]
	    }
	}
    }

    if {$tabbings == {}} {
	set tabbings [list [list tab0 "General"]]
    }

    if {$action != {}} {
	set command [list GT::action $editor $action]
    } else {
	set command {}
    }

    GT::DU::tab_window $window create \
	-title $title \
	-tabs $tabbings \
	-command $command \
	-variables $variables

    #
    # Interpret the Options
    #

    GT::tabwindow::interpret $window $options maxlabelwidth

    GT::position_window_near_graph $editor $window near

    focus $window

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



