# This software is distributed under the Lesser General Public License
#
# edit_label.tcl
#
# This file implements the "About Graphlet" dialog.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/edit_label.tcl,v $
# $Author: himsolt $
# $Revision: 1.7 $
# $Date: 1999/07/27 18:06:27 $
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
	action_edit_label_dialog \
	edit_text_attribute_dialog
}


proc GT::action_edit_label_dialog { editor } {
    edit_text_attribute_dialog $editor -label "Label Editor"
}


proc GT::edit_text_attribute_dialog {editor {attribute -label} {title ""}} {

    global GT GT_options GT_selection

    if [info exists GT_selection($editor,selected,node)] {
	set selected $GT_selection($editor,selected,node)
	if {[llength $selected] > 1} {
	    GT::message $editor "Please select exactly one object" error
	    return
	} elseif {$selected != {}} {
	    set object $selected
	}
    }
    if [info exists GT_selection($editor,selected,edge)] {
	set selected $GT_selection($editor,selected,edge)
	if { [llength $selected] > 1} {
	    GT::message $editor "Please select exactly one object" error
	    return
	} elseif {$selected != {}} {
	    set object $selected
	}
    }

    if ![info exists object] {
	# GT::message $editor "Nothing selected" error
	return
    }

    set dialog $editor.edit_text
    set GT($dialog,object) $object

    set graph $GT($editor,graph)
    if {[catch {$graph get $object $attribute} text] == 1} {
	set text ""
    }

    if [winfo exists $dialog] {
	wm deiconify $dialog
    } else {

	#
	# Create the dialog
	#

	toplevel $dialog
	wm minsize $dialog 1 1
	wm title $dialog $title
	
	text $dialog.text \
	    -relief raised \
	    -bd 1 \
	    -yscrollcommand "$dialog.scroll set" \
	    -setgrid true \
	    -background white
	scrollbar $dialog.scroll \
	    -relief sunken \
	    -command "$dialog.text yview"
	
	set font [list \
		      [$graph get $object label_graphics -font] \
		      [$graph get $object label_graphics -font_size] \
		      [$graph get $object label_graphics -font_style]]
	if {[lindex $font 0] != {}} {
	    $dialog.text configure -font $font
	} else {
	    global tcl_platform
	    if {$tcl_platform(platform) == "windows"} {
		$dialog.text configure -font {{MS Sans Serif} 8}
	    } else {
		$dialog.text configure -font {Helvetica 12}
	    }
	}

	#
	# Manufacture a line of buttons at the bottom of the window
	#
	
	frame $dialog.buttons \
	    -relief raised \
	    -borderwidth 1
	
	button $dialog.buttons.set \
	    -text Set \
	    -width 10 \
	    -command "GT::edit_text_attribute_dialog_update $editor $attribute"
	pack $dialog.buttons.set \
	    -side left \
	    -padx 5
	bind $dialog.text <Control-Return> \
	    "GT::edit_text_attribute_dialog_update $editor $attribute; break"
	
	button $dialog.buttons.cancel \
	    -text Cancel \
	    -width 10 \
	    -command "destroy $dialog"
	pack $dialog.buttons.cancel \
	    -side right \
	    -padx 5

	button $dialog.buttons.ok \
	    -text OK \
	    -width 10 \
	    -command "GT::edit_text_attribute_dialog_update $editor $attribute; destroy $dialog"
	pack $dialog.buttons.ok \
	    -side right \
	    -padx 5
	bind $dialog.text <Alt-Return> \
	    "GT::edit_text_attribute_dialog_update $editor $attribute; destroy $dialog; break"
	
	pack $dialog.buttons \
	    -side bottom \
	    -fill x \
	    -ipady 5

	pack $dialog.scroll \
	    -side right \
	    -fill y
	pack $dialog.text \
	    -side left \
	    -expand yes \
	    -fill both

    }

    #
    # Compute #lines and max line length
    #

    set lines [split $text \n]
    if [info exists GT_options(label_edit_dialog_minwidth)] {
	set maxlen $GT_options(label_edit_dialog_minwidth)
    } else {
	set maxlen 0
    }
    foreach line $lines {
	if { [string length $line] > $maxlen } {
	    set maxlen [string length $line]
	}
    }
    $dialog.text configure \
	-width $maxlen \
	-height [llength $lines]

    #
    # Insert the label
    #

    $dialog.text delete 0.0 end
    $dialog.text insert 0.0 $text
    $dialog.text mark set insert 0.0
    $dialog.text see end

    # Center the window on the screen.

    wm withdraw $dialog
    GT::position_window_near_graph $editor $dialog

    wm deiconify $dialog
    focus $dialog.text
}


proc GT::edit_text_attribute_dialog_update {editor {attribute -label}} {

    global GT GT_options

    set graph $GT($editor,graph)
    set object $GT($editor.edit_text,object)

    set newtext [$editor.edit_text.text get 0.0 end]
    # Remove trailing newline
    $graph configure $object $attribute \
	[string range $newtext 0 [expr [string length $newtext]-2]]

    $graph draw
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
