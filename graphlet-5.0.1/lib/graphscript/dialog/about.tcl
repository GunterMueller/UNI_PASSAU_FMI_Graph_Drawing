# This software is distributed under the Lesser General Public License
#
# about.tcl
#
# This file implements the "About Graphlet" dialog.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/about.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:40:58 $
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
	show_about_graphlet_dialog
}

proc GT::show_about_graphlet_dialog { editor } {

    if [winfo exists .graphlet_help_on_version] {
	return .graphlet_help_on_version
    }

    global GT GT_options

    #
    # Create the Window
    #

    set on_version_toplevel [toplevel .graphlet_help_on_version]
    wm title $on_version_toplevel "Graphlet"
    wm resizable $on_version_toplevel 0 0
    focus $on_version_toplevel

    #
    # OK button
    #

    frame $on_version_toplevel.ok_frame \
	-relief raised \
	-bd 1
    button $on_version_toplevel.ok_frame.ok \
	-text "OK" \
	-command "destroy $on_version_toplevel" \
	-default active
    pack $on_version_toplevel.ok_frame.ok \
	-pady 10
    pack $on_version_toplevel.ok_frame \
	-side bottom \
	-expand true \
	-fill both

    #
    # Create the logo window
    #

    set logo_frame [frame $on_version_toplevel.logo_frame \
			-relief raised \
			-bd 1]

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
    
    pack [label $logo_frame.graphlet -text $GT(default_message)]
    set i 0
    foreach copyright $GT(copyright) {
	pack [label $logo_frame.copyright[incr i] -text $copyright]
    }
    pack [button $logo_frame.www \
	      -text $GT_options(www_address) \
	      -command "GT::browse_url $GT_options(www_address) new-window"
	  ]

    pack $logo_frame \
	-side left \
	-ipadx 10 \
	-ipady 10 \
	-expand y \
	-fill both
    #
    # Insert creators
    #

    # need update to determine the height of $logo_frame
    update 
    set people_frame [frame $on_version_toplevel.people_frame \
			  -relief raised \
			  -bd 1]

#     label $people_frame.acknowledgements \
# 	-text "Acknowledgements"
#     pack $people_frame.acknowledgements \
# 	-side top

    set people {
	{Alf-Ivar Holm}
	{Andreas Pick}
	{Andreas Stübinger}
	{Andrew J. Kompanek}
	{Arne Frick}
	{Carsten Braun}
	{Carsten Friedrichs}
	{Carsten Gutwenger}
	{David Alberts}
	{Dean Forbes}
	{Falk Schreiber}
	{Frank Boerncke}
	{Franz J. Brandenburg}
	{Fred Dichtl}
	{Jan Wuerthner}
	{Jens Utech}
	{Marcus Raitner}
	{Michael Forster}
	{Petra Mutzel}
	{Philippe Chassany}
	{Richard Webber}
	{Robert Schirmer}
	{Roland Preiss}
	{Sabine Wetzel}
	{Sergey Fialko}
	{Stephan Naeher}
	{Thomas Lange}
	{Uwe Hubert}
	{Walter Bachl}

    }

    label $people_frame.head \
	-text "Acknowledgements"
    pack $people_frame.head \
	-side top
    listbox $people_frame.message \
	-yscrollcommand [list $people_frame.sb set] \
	-font Times
    foreach i $people {
	$people_frame.message insert end $i
    }
    pack $people_frame.message \
	-side left \
	-fill y \
	-expand true

    scrollbar $people_frame.sb \
	-orient vertical \
	-command [list $people_frame.message yview]
    pack $people_frame.sb \
	-side right \
	-fill y

    pack $people_frame \
	-side right \
	-fill y \
	-expand true

    raise .graphlet_help_on_version

    bind .graphlet_help_on_version <Return> {destroy .graphlet_help_on_version}

    return .graphlet_help_on_version
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
