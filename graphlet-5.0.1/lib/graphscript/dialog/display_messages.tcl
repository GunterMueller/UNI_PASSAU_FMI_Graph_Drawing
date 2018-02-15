# This software is distributed under the Lesser General Public License
#
# display_messages.tcl
#
# This file implements the "About Graphlet" dialog.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/display_messages.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:41:07 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet Project
#
# Part of this file copied from Tcl/Tk's bgerror.tcl
#

package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	display_messages_dialog \
	update_messages_dialog \
	clear_messages_dialog
}

proc GT::display_messages_dialog { editor var_name {title {}} } {

    global GT GT_options

    set dialog $editor.display_messages_$var_name
    if [winfo exists $dialog] {
	return;
    }

    toplevel $dialog
    wm minsize $dialog 1 1
    wm title $dialog $title

    text $dialog.text \
	-relief raised \
	-bd 1 \
	-yscrollcommand "$dialog.scroll set" \
	-setgrid true \
	-width 80 \
	-height 20 \
	-background white
    scrollbar $dialog.scroll \
	-relief sunken \
	-command "$dialog.text yview"

    upvar \#0 $var_name text
    if [info exists text] {
	$dialog.text insert 0.0 $text
    }

    $dialog.text mark set insert 0.0
    $dialog.text see end
    $dialog.text configure \
	-state disabled

    frame $dialog.buttons \
	-relief raised \
	-borderwidth 1
    button $dialog.buttons.ok \
	-text OK \
	-command "destroy $dialog" \
	-default active
    pack $dialog.buttons.ok \
	-side left \
	-pady 5
    button $dialog.buttons.clear \
	-text Clear \
	-command [list GT::clear_messages_dialog $editor $var_name]
    pack $dialog.buttons.clear \
	-side right
    button $dialog.buttons.update \
	-text Update \
	-command [list GT::update_messages_dialog $editor $var_name]
    pack $dialog.buttons.update \
	-side right
    pack $dialog.buttons \
	-side bottom \
	-fill x

    pack $dialog.scroll \
	-side right \
	-fill y
    pack $dialog.text \
	-side left \
	-expand yes \
	-fill both

    bind $dialog <Return> "destroy $dialog"

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
}


proc GT::update_messages_dialog { editor var_name } {

    set dialog $editor.display_messages_$var_name
    if ![winfo exists $dialog] {
	return;
    }

    upvar \#0 $var_name text
    if [info exists text] {
	$dialog.text configure \
	    -state normal
	$dialog.text delete 0.0 end
	$dialog.text insert 0.0 $text
	$dialog.text mark set insert end
	$dialog.text see end
	$dialog.text configure \
	    -state disabled
    }
}


proc GT::clear_messages_dialog { editor var_name } {

    set dialog $editor.display_messages_$var_name
    if ![winfo exists $dialog] {
	return;
    }

    upvar \#0 $var_name text
    set text ""
    if [info exists text] {
	$dialog.text configure \
	    -state normal
	$dialog.text delete 0.0 end
	$dialog.text see end
	$dialog.text configure \
	    -state disabled
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
