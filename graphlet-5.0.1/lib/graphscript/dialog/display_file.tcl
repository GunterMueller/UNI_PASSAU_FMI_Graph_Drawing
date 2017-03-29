# This software is distributed under the Lesser General Public License
#
# display_file.tcl
#
# This file implements the "About Graphlet" dialog.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/display_file.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:41:05 $
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
	display_file_dialog
}

proc GT::display_file_dialog { editor {filename {}} } {

    global GT GT_options

    set dialog $editor.display_file_[incr GT(frame_nr)]

    toplevel $dialog
    wm minsize $dialog 1 1
    wm title $dialog [file tail $filename]
    # wm transient $dialog $editor

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

    set file [open $filename r]
    while { [gets $file line] >= 0 } {
	$dialog.text insert end "$line\n"
    }
    close $file
    
    $dialog.text mark set insert 0.0
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
	-pady 5
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



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
