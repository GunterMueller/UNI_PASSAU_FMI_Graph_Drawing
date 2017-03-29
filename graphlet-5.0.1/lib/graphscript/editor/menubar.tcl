# This software is distributed under the Lesser General Public License
#
# menubar.tcl
#
# This module implements the menu bar of the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/editor/menubar.tcl,v $
# $Author: himsolt $
# $Revision: 1.14 $
# $Date: 1999/07/27 18:06:28 $
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
	create_menubar \
	add_to_menubar \
	create_menu \
	add_menu_command \
	add_submenu \
	generate_binding \
	disable_menu
}


#
# GT::create_menubar
#

proc GT::create_menubar { editor menus } {

    global GT_menu GT_options

    set menubar [menu $editor.menubar -type menubar -relief raise]
    # Register as menubar
    if {$editor == {}} {
	. config -menu $editor.menubar
    } else {
	$editor config -menu $editor.menubar
    }

    foreach name $menus {

	set underline [string first ^ $name]
	if {$underline != -1} {
	    regsub \\^ $name "" name
	} else {
	    set underline 0
	}

	if [info exists GT_menu($name,create_procs) ] {
	    eval lappend GT_menu($editor,$name,create_procs) \
		$GT_menu($name,create_procs)
	}

	GT::add_to_menubar $editor $name $underline
    }    

    return $editor.menubar
}



proc GT::add_to_menubar { editor name {underline 0}} {

    global GT_menu GT_options

    set menu [GT::create_menu $editor $name $underline]
    if $GT_options(create_menus_dynamically) {
	$menu configure \
	    -postcommand [list GT::post_menu $editor $menu $name]
    } else {
	GT::post_menu $editor $menu $name
    }
}


proc GT::post_menu { editor menu name } {

    global GT_menu

    if [info exists GT_menu($editor,$name,create_procs) ] {

	foreach proc $GT_menu($editor,$name,create_procs) {
	    $proc $editor $menu
	}

	# Delete if the name starts with a capital
	# letter. Otherwise, the menu creation procedure is
	# executed every time the menu is posted.
	if {[string match {[a-z]*} $name] == 0} {
	    unset GT_menu($editor,$name,create_procs)
	}
    }
}


#
# GT::create_menu 
#

proc GT::create_menu { editor name {underline 0}} {

    global GT_options

    set tkname [string tolower $name]

    $editor.menubar add cascade \
	-menu $editor.menubar.$tkname \
	-label $name \
	-underline $underline
    return [menu $editor.menubar.$tkname]
}



#
# GT::add_menu_command
#


proc GT::add_menu_command { editor menu description {tooltips "" } } {

    global GT

    foreach {label action accel underline state} $description {}

    if {![string match {[0-9]*} $underline]} {
	set state $underline
    }

    set underline [string first ^ $label]
    if {$underline != -1} {
	regsub \\^ $label "" label
    } else {
	set underline {}
    }

    set arguments [list \
		       -label $label \
		       -command "eval GT::menu_action $editor $action"]

    if {$accel != {}} {
	lappend arguments -accelerator $accel
	bind $editor [GT::generate_binding $accel] \
	    "GT::menu_action $editor $action"
    }

    if {$underline != {}} {
	lappend arguments -underline $underline
    }

    if {$state != {}} {
	lappend arguments -state $state
    }

    eval $menu add command $arguments

}


proc GT::add_submenu {editor menu description} {

    global GT_menu
    foreach {label name underline state} $description {}

    if {![string match {[0-9]*} $underline]} {
	set state $underline
    }

    set underline [string first ^ $label]
    if {$underline != -1} {
	regsub \\^ $label "" label
    } else {
	set underline {}
    }

    if {$name == ""} {
	regsub -all " " $label _ name
    }
    set submenu [string tolower $name]

    $menu add cascade \
	-label $label \
	-menu $menu.$submenu

    if { $underline != {} } {
	$menu entryconfigure end -underline $underline
    }

    menu $menu.$submenu \
	-tearoff 0 \
	-postcommand [list GT::post_menu $editor $menu.$submenu $name]

    set create GT::create_${name}_menu
    if {![info exists GT_menu($editor,$name,create_procs)] ||
	[lsearch $GT_menu($editor,$name,create_procs) $create] == -1} {
	lappend GT_menu($editor,$name,create_procs) $create
    }
}


##########################################
#
# GT::generate_binding accelerator
#
# Generate a Tk style binding from a OSF/Motif style accelerator
#
##########################################


proc GT::generate_binding accelerator {

    if {[string match *+* $accelerator]} {
	set list [split $accelerator "+" ]
	regsub "Ctrl" [lindex $list 0]  "Control" modifier
	set key [string tolower [lindex $list 1]]
	return "<$modifier-$key>"
    } else {
	return "<$accelerator>"
    }
}



##########################################
#
# GT::disable_menu
#
##########################################

proc GT::disable_menu editor {

    global GT GT_menu

    # Not Yet Implemented
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
