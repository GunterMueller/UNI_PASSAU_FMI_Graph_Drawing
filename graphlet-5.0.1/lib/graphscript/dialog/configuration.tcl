# This software is distributed under the Lesser General Public License
#
# identity.tcl
#
# This file implements a dialog which displays interesting things
# about Graphlet.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/configuration.tcl,v $
# $Author: himsolt $
# $Revision: 1.7 $
# $Date: 1999/03/05 20:41:03 $
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
	display_configuration_dialog
}

proc GT::display_configuration_dialog { } {

    global GT GT_options

    set dialog .graphlet_configuration

    toplevel $dialog
    wm minsize $dialog 1 1
    wm title $dialog "Current Graphlet Configuration"
    wm resizable $dialog 0 0

    frame $dialog.frame \
	-relief raised \
	-borderwidth 1
    set count 0
    global tcl_version tcl_patchLevel tcl_library tk_library machine os \
	osVersion platform tk_patchLevel tk_version tcl_platform
    foreach pair {
	{ "Graphscript directory"  {set GT(graphscript_dir)} }
	{ "Graphlet mini version"  {set GT(mini_version)} }
	{ "Graphlet minor version" {set GT(minor_version)} }
	{ "Graphlet major version" {set GT(major_version)} }
	{ "Graphlet release"       {set GT(release)} }
	{ "Tcl version"            {set tcl_version} }
	{ "Tcl patchlevel"         {set tcl_patchLevel} }
	{ "Tcl Library"            {file nativename $tcl_library} }
	{ "Tk version"             {set tk_version} }
	{ "Tk patchlevel"          {set tk_patchLevel} }
	{ "Tk library"             {file nativename $tk_library} }
	{ "Machine"                {set tcl_platform(machine)} }
	{ "OS"                     {set tcl_platform(os)} }
	{ "OS Version"             {set tcl_platform(osVersion)} }
	{ "Platform"               {set tcl_platform(platform)} }
    } {
	incr count
	frame $dialog.frame.$count
	label $dialog.frame.$count.left \
	    -text [lindex $pair 0] \
	    -width 25 \
	    -anchor e
	pack $dialog.frame.$count.left \
	    -side left
	eval label $dialog.frame.$count.right \
	    -text [list [eval [lindex $pair 1]]]
	pack $dialog.frame.$count.right \
	    -side left
	pack $dialog.frame.$count \
	    -side bottom \
	    -anchor w
    }
    pack $dialog.frame \
	-ipadx 5 \
	-ipady 2 \
	-expand true \
	-fill both
    
    frame $dialog.buttons \
	-relief raised \
	-borderwidth 1
    button $dialog.buttons.ok \
	-text OK \
	-command "destroy $dialog" \
	-default active
    pack $dialog.buttons.ok \
	-pady 5
    pack $dialog.buttons \
	-side bottom \
	-fill x

    # Center the window on the screen.

    wm withdraw $dialog
    update idletasks
    set x [expr [winfo screenwidth $dialog]/2 - [winfo reqwidth $dialog]/2 \
	    - [winfo vrootx [winfo parent $dialog]]]
    set y [expr [winfo screenheight $dialog]/2 - [winfo reqheight $dialog]/2 \
	    - [winfo vrooty [winfo parent $dialog]]]
    wm geom $dialog +$x+$y
    wm deiconify $dialog

    focus $dialog
    bind $dialog <Return> "destroy $dialog"
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
