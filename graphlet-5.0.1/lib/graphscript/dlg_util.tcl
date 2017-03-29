# This software is distributed under the Lesser General Public License
#
# dlg_util.tcl
#
# several utilities for dialogs
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/dlg_util.tcl,v $
# $Author: himsolt $
# $Revision: 1.7 $
# $Date: 1999/03/05 20:40:01 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet Project
# Michael Forster, Walter Bachl & Michael Himsolt
#

package require Graphlet
package provide Graphscript [gt_version]

namespace eval ::GT::DU {
    namespace export \
	parse_args \
	create_grouping \
	grouping_contents \
	center \
	tab_window \
	tab_window_hidebuttons \
	tab_window_disablebuttons \
	tab_window_enablebuttons \
	tab_window_cursheet \
	tab_window_create \
	tab_window_switchto \
	create_tab_button \
	create_tabs \
	create_border \
	show_selection \
	create_buttons \
	ok \
	defaults \
	reset \
	cancel \
	font_changed
}

#--------------------------------------------------------------------------
#   GT::DU::parse_args - parse the argument list of the calling procedure
#--------------------------------------------------------------------------

proc GT::DU::parse_args { ArgList } {
    upvar args args

    # Check the given argument pairs
    
    foreach Pair $ArgList {
	set Option [lindex $Pair 0]
	set Default [lindex $Pair 1]
	set Index [lsearch -exact $args "-$Option"]
	if {$Index != -1} {
	    set Index1 [expr $Index + 1]
	    set Value [lindex $args $Index1]
	    set args [lreplace $args $Index $Index1]
	} else {
	    set Value $Default
	}

	# set option variable 

	uplevel 1 [list set $Option $Value]
    }
}

#---------------------------------------------------------------------------
#   GT::DU::create_grouping - create a grouping box
#---------------------------------------------------------------------------

proc GT::DU::create_grouping { Frame Label CreationCommand } {
    global GT GT_options

    set Distance 4

    frame $Frame

    # Distance above the label

    pack [frame $Frame._distance -height $Distance]

    # Border

    set Box [frame $Frame.border -relief groove -bd 2]
    pack $Box -fill both -expand true 

    # Label

    place [label $Frame.label -text $Label] \
	-in $Box \
	-x 5 -y 0 \
	-bordermode outside \
	-anchor w
   
    # Distance below the label

    pack [frame $Box._distance -height $Distance]
    
    # Create box contents

    set Contents $Box.contents
    eval $CreationCommand [frame $Contents]
    pack $Contents -fill both -expand true -padx 5 -pady 5

    # done

    return $Frame
}


proc GT::DU::grouping_contents { Box } {
    return $Box.border.contents
}

#--------------------------------------------------------------------------
#   GT::DU::center - Center a dialog window
#--------------------------------------------------------------------------

proc GT::DU::center { Dlg Mode {Geometry ""} } {
    if {"$Mode" == "yes"} {
	set Mode parent
    }
    switch $Mode {
	parent {
	    set Parent [winfo parent $Dlg]
	    set x [expr [winfo x $Parent] + \
		       ([winfo width $Parent]-[winfo reqwidth $Dlg])/2]
	    set y [expr [winfo y $Parent] + \
		       ([winfo height $Parent]-[winfo reqheight $Dlg])/2]
	    set Geometry "+$x+$y"
	}
	screen {
	    set x [expr ([winfo screenwidth $Dlg] - \
			     [winfo reqwidth $Dlg])/2]
	    set y [expr ([winfo screenheight $Dlg] - \
			     [winfo reqheight $Dlg])/2]
	    set Geometry "+$x+$y"
	}
	default {}
    }
    wm geometry $Dlg $Geometry
}

#--------------------------------------------------------------------------
#   Tab window routines
#--------------------------------------------------------------------------

proc GT::DU::tab_window { args } {
    set cmd [lindex $args 1]
    if {[lsearch -exact {cursheet create disablebuttons enablebuttons \
			     hidebuttons switchto} $cmd] == -1 } {
	error "bad option \"$cmd\": must be cursheet, create, \
                     disablebuttons, enablebuttons, hidebuttons or switchto"
    } else {
	eval GT::DU::tab_window_$cmd \
	    [lindex $args 0] [lrange $args 2 end]
    }
}


proc GT::DU::tab_window_hidebuttons { TW Buttons } {
    global GT_DU

    foreach Button $Buttons {
	pack forget $GT_DU($TW,button,[string tolower $Button])
    }
}


proc GT::DU::tab_window_disablebuttons { TW Buttons } {
    global GT_DU

    foreach Button $Buttons {
	$GT_DU($TW,button,[string tolower $Button]) configure \
	    -state disabled
    }
}


proc GT::DU::tab_window_enablebuttons { TW Buttons } {
    global GT_DU

    foreach Button $Buttons {
	$GT_DU($TW,button,[string tolower $Button]) configure -state normal
    }
}


proc GT::DU::tab_window_cursheet { TW } {
    global GT_DU

    return [lindex [split $GT_DU($TW,curSheet) .] end]
}


proc GT::DU::tab_window_create { TW args } {
    global GT_DU
    
    # initialize variables
    
    GT::DU::parse_args \
	[list {title "Dialog"} {tabs ""} {initialsheet ""} {butheight ""}  \
	     {butwidth ""} {padx 10} {pady 10} {constsize 1} {bright white}\
	     {dark gray50} {command ""} {variables ""} {resetarray "GT"} \
	     {optionsarray "GT_options"} \
	     {defaultarray "GT_default_options"} \
	     {resetcommand ""}]

    set autobutwidth [expr [string compare $butwidth ""] == 0]
    set curSheet ""
    
    foreach Var {autobutwidth bright butheight butwidth command constsize \
		     curSheet dark padx pady variables optionsarray \
		     defaultarray resetarray resetcommand} {
	set GT_DU($TW,$Var) [set $Var]
    }
    
    # create window
    
    toplevel $TW -class TabWindow
    wm title $TW $title
    wm resizable $TW 0 0

    # create window contents

    pack [GT::DU::create_tabs $TW $tabs] \
	-padx $GT_DU($TW,padx) \
	-anchor w

    pack [GT::DU::create_border $TW] \
	-padx $GT_DU($TW,padx) \
	-fill both \
	-expand true

    pack [GT::DU::create_buttons $TW] \
	-padx $GT_DU($TW,padx) \
	-pady $GT_DU($TW,pady) \
	-fill x
    
    # show initial sheet
    
    GT::DU::tab_window $TW switchto $initialsheet
    
    return $TW
}


proc GT::DU::tab_window_switchto { TW Target } {
    global GT_DU

    set to $TW.$Target
    if {[lsearch -exact $GT_DU($TW,sheets) $to] == -1} {
	set to [lindex $GT_DU($TW,sheets) 0]
    }
    
    # replace old sheet (if any) by new one (if neccessary)
    
    if { [string compare $GT_DU($TW,curSheet) $to] != 0} {
	pack forget $GT_DU($TW,curSheet)
	pack $to -in $TW.border -anchor w
	raise $to
	set GT_DU($TW,curSheet) $to
    }
    
    # redraw frame
    
    GT::DU::show_selection $TW
}


proc GT::DU::create_tab_button { TW Tag left top width height } {
    global GT_DU

    set Tabs $TW.tabs
    set right [expr $left + $width - 1]
    set bot [expr $top + $height]
    
    # left & top
    
    $Tabs create line \
	$left $bot \
	$left [expr $top + 2]\
	[expr $left + 2] $top \
	[expr $right - 1] $top \
	-tag $Tag \
	-fill $GT_DU($TW,bright)
    
    # right (dark gray)
    
    $Tabs create line \
	[expr $right - 1] [expr $top + 2] \
	[expr $right - 1] [expr $bot] \
	-tag $Tag \
	-fill $GT_DU($TW,dark)
    
    # right (black)

    $Tabs create line \
	[expr $right - 1] [expr $top + 1] \
	[expr $right] [expr $top + 2] \
	[expr $right] [expr $bot] \
	-tag $Tag \
	-fill black
    
    # rectangle for binding
    
    return [$Tabs create rectangle \
		[expr $left + 1] [expr $top +2] \
		[expr $right - 1] [expr $bot] \
		-tag $Tag \
		-outline ""]
}


proc GT::DU::create_tabs { TW Buttons } {
    global GT_DU

    # set some variables

    foreach var { autobutwidth butheight butwidth pady } {
	set $var $GT_DU($TW,$var)
    }

    # create canvas
    
    set Tabs [canvas $TW.tabs -highlightthickness 0]

    # start loop for sheets and tabs

    set left 2 ;# space to enlarge button
    set count 0
    foreach Pair $Buttons {
	set Name [lindex $Pair 0]
	set Label [lindex $Pair 1]
	
	# create sheets

	lappend GT_DU($TW,sheets) $TW.$Name
	set Sub [frame $TW.$Name]

	# label
	if [info exists GT_DU(font)] {
	    set Label [$Tabs create text \
			   [expr $left] [expr $pady+2] \
			   -text $Label \
			   -font $GT_DU(font) \
			   -tag "Label-$count"]
	} else {
	    set Label [$Tabs create text \
			   [expr $left] [expr $pady+2] \
			   -text $Label \
			   -tag "Label-$count"]
	}
	    
	# calculate button size

	set BBox [$Tabs bbox $Label]
	if {$autobutwidth} {
	    set butwidth [expr [lindex $BBox 2] - [lindex $BBox 0]+20]
	}
	if {[expr [string compare $butheight ""] == 0]} {
	    set butheight [expr [lindex $BBox 3] - [lindex $BBox 1]+5]
	}
	set GT_DU($TW,butwidth,$count) $butwidth
	set GT_DU($TW,butheight) $butheight

	# center label

	$Tabs move $Label [expr $butwidth/2] [expr $butheight/2-1]

        # draw tab button

	set But [GT::DU::create_tab_button \
		     $TW "" $left $pady $butwidth $butheight]

	# bindings

	$Tabs bind $But <Button> "GT::DU::tab_window $TW switchto $Name"

	# next button

	incr left $butwidth
	incr count 
    }

    # set canvas dimensions - $left is now the right border of the rightmost
    # tab button.

    $Tabs configure \
	-height [expr $butheight + $pady] \
	-width [expr $left + 2] 

    return $Tabs
}


proc GT::DU::create_border { TW } {
    global GT_DU tcl_platform

    if { $tcl_platform(platform) == "unix" } {
	set bd 1
    } {
	set bd 2
    }
    return [frame $TW.border \
		-relief raised \
		-bd $bd] 
}


proc GT::DU::show_selection { TW } {
    global GT_DU

    # select which tab ?

    set sel [lsearch -exact $GT_DU($TW,sheets) $GT_DU($TW,curSheet)]

    # calculate coordinates

    set left 2 ;# space to enlarge if selected
    for {set i 0} {$i < $sel} {incr i} {
	incr left $GT_DU($TW,butwidth,$i)
    }
    set width $GT_DU($TW,butwidth,$sel)
    set right [expr $left + $width]
    
    # replace a part of the top line of the sheet border, to let it look
    # like it was connected with the tab

    set Sel1 $TW._sel1
    set Sel2 $TW._sel2
    if ![winfo exists $Sel1] {
	frame $Sel1 -height 1
	frame $Sel2 -height 1 -width 1 -bg $GT_DU($TW,dark)
    } 
    $Sel1 configure -width [expr $width + 1]
    place $Sel1 -in $TW.border -x [expr $left - 1] -y 0 -bordermode outside
    place $Sel2 -in $TW.border -x $right -y 0 -bordermode outside

    # enlarge tab button

    set Tag SelectedButton
    $TW.tabs delete $Tag

    GT::DU::create_tab_button $TW $Tag \
	[expr $left - 2] [expr $GT_DU($TW,pady) - 2]\
	[expr $width + 4] [expr $GT_DU($TW,butheight) + 2]

    # hide original (smaller) tab button

    $TW.tabs create rectangle \
	[expr $left] [expr $GT_DU($TW,pady) +1] \
	[expr $left + $width - 1] \
	[expr $GT_DU($TW,pady) + $GT_DU($TW,butheight) + 1]\
	-width 2 \
	-outline [$TW cget -bg] \
	-tag $Tag

    # Raise label

    if [info exists GT_DU($TW,oldsel)] {
	$TW.tabs move "Label-$GT_DU($TW,oldsel)" 0 +2
    }
    set GT_DU($TW,oldsel) $sel
    $TW.tabs move "Label-$sel" 0 -2
}


proc GT::DU::create_buttons { TW } {
    global GT_DU

    set Buttons [frame $TW.buttons]
    set command $GT_DU($TW,command)

    if { $command == {} } {
	bind $TW <Return> [list destroy $TW]
    } else {
	bind $TW <Return> $command
    }

    # Create Buttons

    foreach Label { "OK" "Reset" "Defaults" "Cancel" } {
	set Name [string tolower $Label]
	set GT_DU($TW,button,$Name) [button $Buttons.b$Label \
					 -text $Label \
					 -command "GT::DU::$Name $TW"
				    ]
    }

    pack $Buttons.bOK $Buttons.bReset \
	$Buttons.bDefaults $Buttons.bCancel \
	-side left -padx 3

    if { $command == {} } {
	$Buttons.bOK configure -default active
    } else {
	set GT_DU($TW,button,apply) [button $Buttons.bApply \
					 -text "Apply" \
					 -command "$command"]
	pack $Buttons.bApply \
	    -side left -padx 3 \
	    -after $Buttons.bOK
	$Buttons.bApply configure -default active
    }
    
    set GT_DU($TW,button,help) [button $Buttons.bHelp \
				    -text "Help" \
				    -state disabled] 
    pack $Buttons.bHelp -side right -padx 3
   
	
    return $Buttons    
}


#---------------------------------------------------------------------------
#  default button routines
#---------------------------------------------------------------------------

proc GT::DU::ok { TW } {
    global GT_DU

    upvar "\#0" $GT_DU($TW,resetarray) reset

    foreach var $GT_DU($TW,variables) {
	unset reset($TW,$var,reset)
    }

    foreach index [array names GT_DU $TW*] {
	unset GT_DU($index)
    }

    destroy $TW
}


proc GT::DU::defaults { TW } {
    global GT_DU

    upvar "\#0" $GT_DU($TW,optionsarray) options
    upvar "\#0" $GT_DU($TW,defaultarray) default

    foreach var $GT_DU($TW,variables) {
	set options($var) $default($var)
    }
}


proc GT::DU::reset { TW } {
    global GT_DU

    if { $GT_DU($TW,resetcommand) != "" } {

	eval $GT_DU($TW,resetcommand)

    } else {
	
	upvar "\#0" $GT_DU($TW,optionsarray) options
	upvar "\#0" $GT_DU($TW,resetarray) reset
	
	foreach var $GT_DU($TW,variables) {
	    set options($var) $reset($TW,$var,reset)
	}
    }
}


proc GT::DU::cancel { TW } {
    GT::DU::reset $TW
    GT::DU::ok $TW
}

#--------------------------------------------------------------------------
#   handle font changes
#--------------------------------------------------------------------------

proc GT::DU::font_changed { args } {
    global GT_options GT_DU tcl_platform
    
    if {$GT_options(tabwindows_small_font)} {
	if {$tcl_platform(platform) == "windows"} {
	    set GT_DU(font) "-*-MS Sans Serif-*-*-*-*-10-*-*-*-*-*-*-*"
	} else {
	    set GT_DU(font) \
		"-adobe-helvetica-medium-r-*-*-*-100-*-*-*-*-*-*"
	}
    } else {
	set GT_DU(font) "-adobe-helvetica-bold-r-*-*-*-120-*-*-*-*-*-*"
    }
    if {[info command option] != {}} {
	option add *TabWindow*font $GT_DU(font)
    }
}

# Added MH 05/30/97: Dont trace if the option does not
# exist. This is neccessary for Tcl packages.

if [info exists GT_options(tabwindows_small_font)] {
    trace variable GT_options(tabwindows_small_font) w GT::DU::font_changed
    GT::DU::font_changed
}

#--------------------------------------------------------------------------
#   end of file
#--------------------------------------------------------------------------
