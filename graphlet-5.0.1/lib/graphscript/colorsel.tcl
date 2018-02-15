# This software is distributed under the Lesser General Public License
#
# This module implements the color selector
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/colorsel.tcl,v $
# $Author: himsolt $
# $Revision: 1.6 $
# $Date: 1999/03/05 20:39:58 $
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
    namespace export color_selector
}
namespace eval GT::CS {
    namespace export \
	init \
	run \
	done \
	create \
	create_toplevel \
	create_predef_col_box \
	create_userdef_col_box \
	create_definition_box \
	create_new_col_box \
	create_color_table \
	ok \
	color_changed \
	set_color \
	select_button \
	color_name \
	get_userdef_colors \
	button_down \
	button_up \
	startup
}

########################################################################
#
#   Main Procedure
#
########################################################################

proc GT::color_selector { args } {
    global GT_CS

    eval GT::CS::init $args
    GT::CS::run 
    return [GT::CS::done]
}


proc GT::CS::init { args } {
    global GT_CS

    # check the arguments

    GT::DU::parse_args [list \
			  {parent "."} \
			  {title "Select color"} \
			  {default "white"} \
			  {geometry "+100+100"} \
			  {center "parent"} \
			  {predefcolors ""} \
			  {usercolors ""} \
		     ]

    set GT_CS(predefcolors) $predefcolors
    set GT_CS(usercolors) $usercolors

    if {"$default" == ""} {
	set default "white"
    }

    # Create the dialog window

    set Dlg [GT::CS::create $parent]

    # set initial Color

    set GT_CS(result) $default
    GT::CS::set_color $default

    # window manager options

    wm title $Dlg $title
    wm transient $Dlg $parent
    wm resizable $Dlg false false

    # show window offscreen to calculate its size

    wm geometry $Dlg +[winfo screenwidth $Dlg]+[winfo screenheight $Dlg]
    tkwait visibility $Dlg

    # position window

    GT::DU::center $Dlg $center $geometry

    # grab 

    grab set $Dlg
}


proc GT::CS::run { } {
    global GT_CS

    set GT_CS(done) false
    tkwait variable GT_CS(done)
}


proc GT::CS::done { } {
    global GT_CS

    # delete dialog

    grab release $GT_CS(dialog)
    destroy $GT_CS(dialog)

    # delete all global variables except those in $Variables

    set Result $GT_CS(result) 
    unset GT_CS

    return $Result
}

########################################################################
#
#   Dialog creation
#
########################################################################

proc GT::CS::create { Top } {
    global GT_CS

    # set parameters

    set pad 5

    # create toplevel

    set Dlg [GT::CS::create_toplevel $Top]

    pack [frame $Dlg.a] -fill both -expand true
    pack [frame $Dlg.b] -fill both -expand true

    # Predefined Colors

    pack [GT::DU::create_grouping $Dlg.a.predef "Predefined" \
	      "GT::CS::create_predef_col_box"] \
	-side left \
	-padx $pad -pady $pad

    # User defined Colors

    pack [GT::DU::create_grouping $Dlg.a.userdef "User defined" \
	      "GT::CS::create_userdef_col_box"] \
	-side right \
	-padx $pad -pady $pad

    # Color definition

    pack [GT::DU::create_grouping $Dlg.b.define "Define Color" \
	     "GT::CS::create_definition_box"] \
	-side left \
	-padx $pad -pady $pad

    # New Color

    pack [GT::DU::create_grouping $Dlg.b.new "New Color" \
	      "GT::CS::create_new_col_box"] \
	-side right \
	-fill both -expand true \
	-padx $pad -pady $pad

    # Buttons

    pack [frame $Dlg.c] \
	-side bottom 
    set Frame [frame $Dlg.c.buttons] 
    pack $Frame -pady $pad
    
    set Ok [button $Frame.ok -text Ok -command GT::CS::ok]

    set Cancel [button $Frame.cancel -text Cancel -command {
	set GT_CS(result) ""
	set GT_CS(done) true
    }]

    pack $Ok -side left
    pack $Cancel -side left

    # finished

    return $GT_CS(dialog)
}


proc GT::CS::create_toplevel { Top } {
    global GT_CS

    return [set GT_CS(dialog) [toplevel $Top.colorSel]]
}


proc GT::CS::create_predef_col_box { Box } {
    global GT_CS

    upvar \#0 $GT_CS(predefcolors) predefcolors

    GT::CS::create_color_table $Box $predefcolors GT::CS::constColBut
}


proc GT::CS::create_userdef_col_box { Box } {
    global GT_CS
    
    upvar \#0 $GT_CS(usercolors) usercolors
    set GT_CS(userdefColorBox) $Box
    GT::CS::create_color_table $Box $usercolors GT::CS::varColBut
}


proc GT::CS::create_definition_box { Box } {
    global GT_CS

    set y 0
    foreach Channel {red green blue} {

	grid [label $Box.l$Channel \
		  -text $Channel: \
		  -width 5 \
		  -anchor e] \
	    -column 0 \
	    -row $y

	grid [entry $Box.e$Channel \
		  -width 3 \
		  -textvariable GT_CS($Channel)] \
	    -column 1 \
	    -row $y

	grid [scale $Box.s$Channel \
		  -orient h \
		  -variable GT_CS($Channel) \
		  -from 0 \
		  -to 255 \
		  -showvalue false \
		  -length 95] \
	    -column 2 \
	    -row $y

	lappend GT_CS(scales) $Box.s$Channel

	trace variable GT_CS($Channel) w GT::CS::color_changed

	incr y
    }
}


proc GT::CS::create_new_col_box { Box } {
    global GT_CS

    set But [frame $Box.c0 -highlightthickness 2]
    pack $But -fill both -expand true
    bindtags $But [concat [bindtags $But] GT::CS::varColBut]
    
    set GT_CS(newCol) $But
    GT::CS::select_button $But
}


proc GT::CS::create_color_table { Box Colors Tag } {

    set Count 0
    set Columns 6

    foreach Color $Colors {
	set but [frame $Box.c$Count \
		     -width 20 \
		     -height 20 \
		     -bd 1 -relief solid \
		     -highlightthickness 2 \
		     -bg $Color]
	grid $but \
	    -column [expr $Count % $Columns] \
	    -row [expr $Count / $Columns] \
	    -sticky "nwse"
	bindtags $but [concat [bindtags $but] $Tag]

	incr Count
    }
}

########################################################################
#
#   traces & bindings
#
########################################################################

proc GT::CS::ok { } {
    global GT_CS

    upvar \#0 GT_CS(usercolors) usercolors
    set $usercolors [GT::CS::get_userdef_colors]
    
    set GT_CS(result) [$GT_CS(newCol) cget -bg]
    set GT_CS(done) true
}

proc GT::CS::color_changed { args } {
    global GT_CS

    set Color [GT::CS::color_name $GT_CS(red) \
		   $GT_CS(green) $GT_CS(blue)]

    $GT_CS(selected) config -bg $Color
    $GT_CS(newCol) config -bg $Color
}


proc GT::CS::set_color { Col } {
    global GT_CS

    $GT_CS(newCol) config -bg $Col
    if {[string index $Col 0] == "\#"} {
	set GT_CS(red)   [eval expr 0x[string range $Col 1 2]]
	set GT_CS(green) [eval expr 0x[string range $Col 3 4]]
	set GT_CS(blue)  [eval expr 0x[string range $Col 5 6]]
    } else {
	set Channels [winfo rgb $GT_CS(dialog) $Col]
	set GT_CS(red)   [expr [lindex $Channels 0] / 256]
	set GT_CS(green) [expr [lindex $Channels 1] / 256]
	set GT_CS(blue)  [expr [lindex $Channels 2] / 256]
    }
}


proc GT::CS::select_button { But } {
    global GT_CS

    if [info exists GT_CS(selected)] {
	$GT_CS(selected) config \
	    -highlightbackground $GT_CS(backCol)
    } else {
	set GT_CS(backCol) [$But cget -highlightbackground]
    }
    $But config -highlightbackground black
    set GT_CS(selected) $But

    if {$But != $GT_CS(newCol)} {
	GT::CS::set_color [$But cget -bg]
    }

    if {[string first ".predef." $But] != -1} {
	foreach scl $GT_CS(scales) {
	    $scl config -state disabled
	}
    } else {
	foreach scl $GT_CS(scales) {
	    $scl config -state normal
	}
    }
}

########################################################################
#
#   utility procedures
#
########################################################################

proc GT::CS::color_name { r g b } {
    foreach channel {r g b} {
	if {[set $channel] == 256} {
	    set $channel 255
	}
    }
    return "#[format "%02x" $r][format "%02x" $g][format "%02x" $b]"
}


proc GT::CS::get_userdef_colors { } {
    global GT_CS

    foreach But [winfo children $GT_CS(userdefColorBox)] {
	lappend List [$But cget -bg]
    }

    return $List
}

########################################################################
#
#   event handling, especcially color drag & drop
#
########################################################################

# Added MH 05/30/97: Dont execute if the Tk is not loaded.  This
# is neccessary for Tcl packages.

if [info exists tk_version] {
    bind "GT::CS::varColBut" <Button-1> "GT::CS::button_down %W"
    bind "GT::CS::constColBut" <Button-1> "GT::CS::button_down %W"
    bind "GT::CS::varColBut" <ButtonRelease-1> "GT::CS::button_up %W %x %y"
    bind "GT::CS::constColBut" <ButtonRelease-1> "GT::CS::button_up %W %x %y"
}

proc GT::CS::button_down { Window } {
    global GT_CS tcl_platform

    GT::CS::select_button $Window
    if {$tcl_platform(platform) == "windows"} {
	set Cursor "dotbox"
    } else {
	set Cursor "dotbox black [$Window cget -bg]"
    }	
    $GT_CS(dialog) config -cursor $Cursor
}

proc GT::CS::button_up { SourceWindow x y} {
    global GT_CS

    $GT_CS(dialog) config -cursor ""

    incr x [winfo rootx $SourceWindow]
    incr y [winfo rooty $SourceWindow]
    set TargetWindow [winfo containing $x $y]

    if {$TargetWindow != ""} {
	if {[lsearch [bindtags $TargetWindow] "GT::CS::varColBut"] != -1} {
	    set Color [$SourceWindow cget -bg]
	    GT::CS::select_button $TargetWindow
	    GT::CS::set_color $Color
	}
    }
}

########################################################################
#
#   end of file
#
########################################################################











