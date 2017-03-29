# This software is distributed under the Lesser General Public License
#
# identity.tcl
#
# This file implements a dialog which displays interesting things
# about Graphlet.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/mail_bugreport.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:41:13 $
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
	display_mail_developers_dialog \
	mail_bugreport_to_developers \
	mail_developers
}



proc GT::display_mail_developers_dialog { info } {

    global GT GT_options

    global tcl_platform
    if { $tcl_platform(platform) != "unix" } {
	set query [tk_dialog \
	     .notavailable \
	     "Feature Not Available" \
	     "Mailing to developers is only available on UNIX platforms." \
	     info \
	     0 \
	     Sorry]
	return
    }

    set dialog .graphlet_mail_develpers_dialog

    toplevel $dialog
    wm minsize $dialog 1 1
    wm title $dialog "Mail Developers"

    frame $dialog.subject
    label $dialog.subject.label \
	-text "Subject:"
    entry $dialog.subject.text \
	-width 65 \
	-font $GT_options(default_bold_font)
    $dialog.subject.text insert end "Insert Subject HERE"
    pack $dialog.subject.label \
	-side left
    pack $dialog.subject.text \
	-side top \
	-fill x \
	-expand true
    pack $dialog.subject \
	-side top \
	-fill x

    frame $dialog.edit
    text $dialog.edit.text \
	-relief raised \
	-bd 1 \
	-yscrollcommand "$dialog.edit.scroll set" \
	-setgrid true \
	-width 80 \
	-height 20 \
	-background white
    scrollbar $dialog.edit.scroll \
	-relief sunken \
	-command "$dialog.edit.text yview"
    pack $dialog.edit.scroll \
	-side right \
	-fill y
    pack $dialog.edit.text \
	-expand yes \
	-fill both
    pack $dialog.edit \
	-side top \
	-expand yes \
	-fill both

    frame $dialog.buttons \
	-relief raised \
	-borderwidth 1
    button $dialog.buttons.mail \
	-text "Mail $GT_options(graphlet_bug_address)" \
	-command [list GT::mail_bugreport_to_developers $dialog]
    pack $dialog.buttons.mail \
	-side left \
	-padx 10m
    button $dialog.buttons.cancel \
	-text Cancel \
	-command "destroy $dialog"
    pack $dialog.buttons.cancel \
	-side right \
	-padx 10m
    pack $dialog.buttons \
	-side bottom \
	-fill x \
	-ipady 5m

    # Center the window on the screen.

    wm withdraw $dialog
    update idletasks
    set x [expr [winfo screenwidth $dialog]/2 - [winfo reqwidth $dialog]/2 \
	    - [winfo vrootx [winfo parent $dialog]]]
    set y [expr [winfo screenheight $dialog]/2 - [winfo reqheight $dialog]/2 \
	    - [winfo vrooty [winfo parent $dialog]]]
    wm geom $dialog +$x+$y
    wm deiconify $dialog

    #
    # Enter a default text in the window
    #

    $dialog.edit.text insert end "\n\n"

    if $GT_options(graphlet_bug_mail_stack) {
	if { $info != {} } {
	    $dialog.edit.text insert end \
		"------------------------------------------\n"
	    $dialog.edit.text insert end \
		$info
	    $dialog.edit.text insert end \
		"\n------------------------------------------\n"
	}
    }

    if $GT_options(graphlet_bug_mail_stack) {

	$dialog.edit.text insert end "------------------------------------------\n"
	global tcl_version tcl_patchLevel tcl_library tk_library machine \
	    os osVersion platform tk_patchLevel tk_version tcl_platform
	foreach pair {
	    { "Graphlet major version :" GT(major_version) }
	    { "Graphlet minor version :" GT(minor_version) }
	    { "Graphlet mini version :" GT(mini_version) }
	    { "Graphlet release :" GT(release) }
	    { "Tcl version :" tcl_version }
	    { "Tcl patchlevel :" tcl_patchLevel }
	    { "Tk version :" tk_version }
	    { "Tk patchlevel :" tk_patchLevel }
	    { "Machine :" tcl_platform(machine) }
	    { "OS :" tcl_platform(os) }
	    { "OS Version :" tcl_platform(osVersion) }
	    { "Platform :" tcl_platform(platform) }
	} {
	    set name [lindex $pair 0]
	    eval set value $[lindex $pair 1]
	    $dialog.edit.text insert end "$name $value\n"
	}
	
	$dialog.edit.text insert end "------------------------------------------\n"
    }

    $dialog.edit.text mark set insert 0.0
}


proc GT::mail_bugreport_to_developers { dialog} {

    global GT

    set GT(bugreport_text) \
	"Subject: [$dialog.subject.text get]\n[$dialog.edit.text get 0.0 end]"
    GT::mail_developers
    destroy $dialog
}


proc GT::mail_developers { } {

    global GT GT_options

    catch { set fd [open "|mail $GT_options(graphlet_bug_address)" w]} \
	error_message

    if [info exists fd] {

	if [info exists GT(bugreport_text)] {
	    puts $fd $GT(bugreport_text)
	    unset GT(bugreport_text)
	}

	close $fd

    } else {
	tk_dialog .cannotsendmail "Cannot send mail" \
	    "Cannot send mail: $error_message" error 0 Ok
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
