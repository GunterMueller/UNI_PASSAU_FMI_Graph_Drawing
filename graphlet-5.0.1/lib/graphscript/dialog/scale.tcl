# This software is distributed under the Lesser General Public License
#
# dialog/scale.tcl
#
# This module implements several test routines for the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/scale.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:41:20 $
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
	show_scale_dialog \
	close_scale_dialog \
	reset_scale_dialog
}


proc GT::show_scale_dialog { editor } {

    global GT

    if [winfo exists $editor.scale_dialog] {
	raise $editor.scale_dialog
	return
    }

    toplevel $editor.scale_dialog
    frame $editor.scale_dialog.value
    scale $editor.scale_dialog.value.scale \
	-from 0.25 -to 4.00 \
	-orient horizontal \
	-resolution 0.05 \
	-showvalue true \
	-command "GT::scale $editor"

    set GT($editor.scale_dialog,previous_value) 1.0
    set GT($editor.scale_dialog,initial_value) 1.0
    $editor.scale_dialog.value.scale set $GT($editor.scale_dialog,previous_value)
    pack $editor.scale_dialog.value.scale
    pack $editor.scale_dialog.value

    set buttons [frame $editor.scale_dialog.buttons]
    foreach b { OK Apply Reset Cancel } {
	button $buttons.b$b -text $b -state disabled
	pack $buttons.b$b -side left 
    }

    $buttons.bOK configure \
	-state normal \
	-command "destroy $editor.scale_dialog"
    $buttons.bReset configure \
	-state normal \
	-command "GT::reset_scale_dialog $editor"
    $buttons.bCancel configure \
	-state normal \
	-command "GT::reset_scale_dialog $editor destroy"
    pack $buttons

    wm protocol $editor WM_DELETE_WINDOW "GT::close_scale_dialog $editor"
}


proc GT::close_scale_dialog editor {

    global GT

    # Delete all associated variables

    foreach name [array names GT $editor.scale_dialog,] {
	unset GT($name)
    }
}


proc GT::reset_scale_dialog { editor { destroy ""} } {

    global GT

    $editor.scale_dialog.value.scale set \
	$GT($editor.scale_dialog,initial_value)

    if { $destroy != "" } {
	destroy $editor.scale_dialog
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
