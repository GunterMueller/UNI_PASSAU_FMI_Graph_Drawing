# This software is distributed under the Lesser General Public License
#
# tooltips.tcl
#
# Balloonhelp for Dialogs, Menus and the Toolbar (AKA ToolTips)
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/widgets/tooltips.tcl,v $
# $Author: himsolt $
# $Revision: 1.3 $
# $Date: 1999/03/05 20:42:35 $
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
	init_tooltips \
	tooltips \
	menu_tooltips \
	update_menu_tooltips \
	show_tooltips \
	hide_tooltips \
	start_tooltips_waiting \
	stop_tooltips_waiting \
	restart_tooltips_waiting
}


proc GT::init_tooltips { } {
    global tcl_platform

    if {$tcl_platform(platform) == "unix"} {
	option add *GraphletHelp*font { Helvetica 8 }
    } else {
	option add *GraphletHelp*font ansi
    }

    set tooltips [toplevel .tooltips \
		       -class GraphletHelp \
		       -borderwidth 1 \
		       -background black \
		       ]
    
    pack [message .tooltips.text \
	      -bd 0 \
	      -aspect 1000 \
	      -background "#FFFFC0" \
	  ]
    
    # wm transient $tooltips .
    wm overrideredirect $tooltips 1
    wm positionfrom $tooltips program
    
    # Dont show the window now
    wm withdraw $tooltips

    # binding
    
    bind GT_tooltips <Enter> [namespace code "start_tooltips_waiting %W"]
    bind GT_tooltips <Leave> [namespace code "stop_tooltips_waiting %W"]
    bind GT_tooltips <Button> [namespace code "restart_tooltips_waiting %W"]
}


proc GT::tooltips { window text } {
    global GT_tooltips
    
    if { $text != "" } {
	set GT_tooltips($window) $text
	bindtags $window [concat [bindtags $window] GT_tooltips]
    }
 }


proc GT_menu_tooltips { Menu Array Index } {
    global $Array

    GT::tooltips $Menu [set ${Array}(${Index},0)]
    bind $Menu <Motion> "GT::update_menu_tooltips $Array $Index %W %y"
}


proc GT::update_menu_tooltips { Array Index Win y} {
    set HelpTextVar ${Array}($Index,[$Win index @$y])
    if {[info exists $HelpTextVar]} {
	.tooltips.text configure -text [set $HelpTextVar]
    }
}


proc GT::show_tooltips { window } {
    global GT_options GT_tooltips

    if {$GT_options(tooltips) && [info exists GT_tooltips($window)]} {

	.tooltips.text configure -text $GT_tooltips($window)

	set x [expr [winfo pointerx $window]+5]
	set y [expr [winfo pointery $window]+5]

	update idletasks

	if {$x + [winfo reqwidth .tooltips] >
	    [winfo screenwidth .tooltips]
	} {
	    set x [expr [winfo screenwidth .tooltips] - \
		       [winfo reqwidth .tooltips]]
	}
	if {$y + [winfo reqheight .tooltips] >
	    [winfo screenheight .tooltips]
	} {
	    set y [expr [winfo screenheight .tooltips] - \
		       [winfo reqheight .tooltips]]
	}

	wm geometry .tooltips +$x+$y
	update idletasks

	raise .tooltips
	wm deiconify .tooltips
    }
}

proc GT::hide_tooltips { window } {
    if {[winfo viewable .tooltips]} {
	wm withdraw .tooltips
    }
}

proc GT::start_tooltips_waiting { window } {
    global GT_tooltips GT_options

    set GT_tooltips($window,WaitID) \
	    [after $GT_options(tooltips_interval) "GT::show_tooltips $window"]
}

proc GT::stop_tooltips_waiting { window } {
    global GT_tooltips

    GT::hide_tooltips $window

    if {[info exists GT_tooltips($window,WaitID)]} {
	after cancel $GT_tooltips($window,WaitID)
	unset GT_tooltips($window,WaitID)
    }
}

proc GT::restart_tooltips_waiting { window } {
    GT::stop_tooltips_waiting $window
    GT::start_tooltips_waiting $window
}

##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
