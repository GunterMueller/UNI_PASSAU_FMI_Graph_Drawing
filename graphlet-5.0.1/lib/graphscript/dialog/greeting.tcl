# This software is distributed under the Lesser General Public License
#
# greeting.tcl
#
# This file implements the "Greeting Graphlet" dialog.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/greeting.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:41:12 $
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
	show_greeting_graphlet_dialog
}

proc GT::show_greeting_graphlet_dialog { } {

    global GT GT_options

    if ![file exists $GT_options(greetingsfile)] {
	set try [catch {
	    set file [open $GT_options(greetingsfile) w]
	    close $file
	} errormsg]
    } else {
	return
    }

    if [info exists GT(nogreeting)] {
	return
    }

    if [winfo exists .graphlet_greeting] {
	return .graphlet_greeting
    }


    #
    # Create the Window
    #

    set greeting_toplevel [toplevel .graphlet_greeting]
    wm title $greeting_toplevel "Graphlet"
    wm resizable $greeting_toplevel 0 0
    # wm overrideredirect .graphlet_greeting 1

    #
    # Create the logo window
    #
    
    set logo_frame [frame $greeting_toplevel.logo_frame -relief raised -bd 1]

    if {[catch {GT::get_image pwrd200}] == 0} {
	label $logo_frame.tcllogo -image pwrd200
	pack $logo_frame.tcllogo \
	    -side left \
	    -pady 10 \
	    -padx 10
    }

    if {[catch {GT::get_image logo}] == 0} {
	label $logo_frame.logo -image logo
	pack $logo_frame.logo \
	    -side top \
	    -anchor c \
	    -pady 10
    }
    
    pack [label $logo_frame.message -text $GT(default_message)]

    #
    # Create a nice frame with all the copyrights
    #

    set i 0
    foreach copyright $GT(copyright) {
	incr i
	label $logo_frame.copyright$i \
	    -text $copyright
	pack $logo_frame.copyright$i
    }

    pack $logo_frame \
	-side top \
	-expand true \
	-fill x


    #
    # OK button
    #

    frame $greeting_toplevel.ok_frame \
	-relief raised \
	-bd 1
    button $greeting_toplevel.ok_frame.ok \
	-text "OK" \
	-command "destroy $greeting_toplevel" \
	-default active
    pack $greeting_toplevel.ok_frame.ok \
	-pady 10
    pack $greeting_toplevel.ok_frame \
	-side bottom \
	-expand true \
	-fill x

    raise .graphlet_greeting
    focus .graphlet_greeting

    bind .graphlet_greeting <Return> \
	"destroy .graphlet_greeting"
    # wm transient .graphlet_greeting .
    focus .graphlet_greeting

    after 10000 {
	catch {destroy .graphlet_greeting}
    }
    tkwait window .graphlet_greeting

    return .graphlet_greeting
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
