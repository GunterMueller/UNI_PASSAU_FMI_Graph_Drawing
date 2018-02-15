# This software is distributed under the Lesser General Public License
#
# patches.tcl
#
# This file patches some bugs in the Tcl code.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/patches.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:40:17 $
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
	init_patches
}

##########################################
#
# GT::init_patches
#
# Modelled after
# http://www.cs.uoregon.edu/research/tcl/patch/80/menu2.txt
#
##########################################


proc GT::init_patches { } {

#     global tcl_version tcl_patchLevel

#     if { $tcl_version == "8.0" && $tcl_patchLevel == "8.0" } {

# 	rename ::tk_popup ::tk_popup.sun

# 	#
# 	# tk_popup
# 	#

# 	proc ::tk_popup {menu x y {entry {}}} {
# 	    global tkPriv
# 	    global tcl_platform
# 	    if {($tkPriv(popup) != "") || ($tkPriv(postedMb) != "")} {
# 		tkMenuUnpost {}
# 	    }
# 	    tkPostOverPoint $menu $x $y $entry
# 	    if {$tcl_platform(platform) == "unix"} {
# 		tkSaveGrabInfo $menu
# 		grab -global $menu
# 		set tkPriv(popup) $menu
# 		tk_menuSetFocus $menu
# 	    }
# }

#     }


#     #
#     # tkMenuUnpost
#     #

#     rename ::tkMenuUnpost ::tkMenuUnpost.sun

#     proc ::tkMenuUnpost menu {
# 	global tcl_platform
# 	global tkPriv
# 	set mb $tkPriv(postedMb)
	
# 	# Restore focus right away (otherwise X will take focus away when
# 	# the menu is unmapped and under some window managers (e.g. olvwm)
# 	# we'll lose the focus completely).
	
# 	catch {focus $tkPriv(focus)}
# 	set tkPriv(focus) ""

# 	# Unpost menu(s) and restore some stuff that's dependent on
# 	# what was posted.
	
# 	catch {
# 	    if {$mb != ""} {
# 		set menu [$mb cget -menu]
# 		$menu unpost
# 		set tkPriv(postedMb) {}
# 		$mb configure -cursor $tkPriv(cursor)
# 		$mb configure -relief $tkPriv(relief)
# 	    } elseif {$tkPriv(popup) != ""} {
# 		$tkPriv(popup) unpost
# 		set tkPriv(popup) {}
# 	    } elseif {(!([$menu cget -type] == "menubar")
# 		       && !([$menu cget -type] == "tearoff"))} {
# 		# We're in a cascaded sub-menu from a torn-off menu or popup.
# 		# Unpost all the menus up to the toplevel one (but not
# 		# including the top-level torn-off one) and deactivate the
# 		# top-level torn off menu if there is one.
		
# 		while 1 {
# 		    set parent [winfo parent $menu]
# 		    if {([winfo class $parent] != "Menu")
# 			|| ![winfo ismapped $parent]} {
# 			break
# 		    }
# 		    $parent activate none
# 		    $parent postcascade none
# 		    set type [$parent cget -type]
# 		    if {($type == "menubar")|| ($type == "tearoff")} {
# 			break
# 		    }
# 		    set menu $parent
# 		}
# 		if {[$menu cget -type] != "menubar"} {
# 		    $menu unpost
# 		}
# 	    }
# 	}

# 	if {($tkPriv(tearoff) != 0) || ($tkPriv(menuBar) != "")} {
# 	    # Release grab, if any, and restore the previous grab, if there
# 	    # was one.
	    
# 	    if {$menu != ""} {
# 		set grab [grab current $menu]
# 		if {$grab != ""} {
# 		    grab release $grab
# 		}
# 	    }
# 	    tkRestoreOldGrab
# 	    if {$tkPriv(menuBar) != ""} {
# 		$tkPriv(menuBar) configure -cursor $tkPriv(cursor)
# 		set tkPriv(menuBar) {}
# 	    }
# 	    if {$tcl_platform(platform) != "unix"} {
# 		set tkPriv(tearoff) 0
# 	    }
# 	}
#     }


#     #
#     # tkMenuButtonDown
#     #

#     rename ::tkMenuButtonDown ::tkMenuButtonDown.sun

#     proc ::tkMenuButtonDown menu {
# 	global tkPriv
# 	global tcl_platform
# 	$menu postcascade active
# 	if {$tkPriv(postedMb) != ""} {
# 	    grab -global $tkPriv(postedMb)
# 	} else {
# 	    while {([$menu cget -type] == "normal") 
# 		   && ([winfo class [winfo parent $menu]] == "Menu")
# 		   && [winfo ismapped [winfo parent $menu]]} {
# 		set menu [winfo parent $menu]
# 	    }
	    
# 	    if {$tkPriv(menuBar) == {}} {
# 		set tkPriv(menuBar) $menu
# 		set tkPriv(cursor) [$menu cget -cursor]
# 		$menu configure -cursor arrow
# 	    }
	    
# 	    # Don't update grab information if the grab window isn't changing.
# 	    # Otherwise, we'll get an error when we unpost the menus and
# 	    # restore the grab, since the old grab window will not be viewable
# 	    # anymore.
	    
# 	    if {$menu != [grab current $menu]} {
# 		tkSaveGrabInfo $menu
# 	    }

# 	    # Must re-grab even if the grab window hasn't changed, in order
# 	    # to release the implicit grab from the button press.
	    
# 	    if {$tcl_platform(platform) == "unix"} {
# 		grab -global $menu
# 	    }
# 	}
#     }
    

    return -code ok
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
