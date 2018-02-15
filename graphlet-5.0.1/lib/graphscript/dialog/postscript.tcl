# This software is distributed under the Lesser General Public License
#
# dialog/postscript.tcl
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dialog/postscript.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/03/05 20:41:19 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#

package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	show_postscript_options_dialog
}


proc GT::show_postscript_options_dialog  { editor } {

    lappend options {
	newtab "General"
    } {
	radio "Orientation" postscript_rotate
	""
	{
	    {"Portrait" false}
	    {"Landscape" true}
	}
    } {
	radio "Area" postscript_area
	""
	{
	    {"All" all}
	    {"Selected" selected}
	    {"Visible" visible}
	    {"Custom" custom}
	}
    } {
	check "Crop Marks" postscript_crop_marks
	""
    } {
	check "Signature" postscript_signature
	""
    } {
	endtab
    }


    lappend options {
	newtab "Page Size"
    } {
	option_menu "Page Size" postscript_select
	""
	{
	    "None"
	    "A3"
	    "A4"
	    "A5"
	    "A6"
	    "US Letter"
	    "Custom"
	}
    } {
	newgroup "Custom Page"
    } {
	string "X" postscript_pagex
	""
    } {
	string "Y" postscript_pagey
	""
    } {
	string "Width" postscript_pagewidth
	""
    } {
	string "Height" postscript_pageheight
	""
    } {
	endgroup
    } {
	newgroup "Outer Frame"
    } {
	string "Left" postscript_outer_frameleft
	""
    } { 
	string "Right" postscript_outer_frameright
	""
    } {
	string "Top" postscript_outer_frametop
	""
    } {
	string "Bottom" postscript_outer_framebottom
	""
    } {
	endgroup
    } {
	group "Inner Frame"
    } {
	string "Left" postscript_inner_frameleft
	""
    } {
	string "Inner Frame Right" postscript_inner_frameright
	""
    } {
	string "Inner Frame Top" postscript_inner_frametop
	""
    } {
	string "Inner Frame Bottom" postscript_inner_framebottom
	""
    } {
	endgroup
    } {
	endtab
    }
    

    lappend options {
	newtab "Custom Print Area"
    } {
	group "Custom Print Area"
    } {
	float "X" postscript_x
	""
    } {
	float "Y" postscript_y
	""
    } {
	float "Width" postscript_width
	""
    } {
	float "Height" postscript_height
	""
    } {
	endgroup
    } {
	endtab
    }

    GT::create_tabwindow $editor \
	postscript_options "Postscript Options" \
	$options
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
