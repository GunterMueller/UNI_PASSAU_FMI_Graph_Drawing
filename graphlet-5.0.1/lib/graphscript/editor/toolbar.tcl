# This software is distributed under the Lesser General Public License
#
# toolbar.tcl
#
# This module implements the toolbar of the editor.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/editor/toolbar.tcl,v $
# $Author: himsolt $
# $Revision: 1.18 $
# $Date: 1999/04/10 15:27:11 $
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
	create_toolbar
}


#
# Create Toolbar
#

proc GT::create_toolbar { editor } {

    global GT GT_menu GT_options

    set toolbar [frame $editor.toolbar -relief groove -borderwidth 2]

    #
    #   Create standard toolbar buttons
    #
    
    set space_nr 0
    foreach { but tooltip } {
	file_new	"New Graph (Alt-N)"
	file_open	"Open Graph (Ctrl-O)"
	file_save	"Save Graph (Ctrl-S)"
	.		""
	file_postscript	"Save Graph as Postscript"
	.		""
	cut		"Cut (Ctrl-X)"
	copy		"Copy (Ctrl-C)"
	paste		"Paste (Ctrl-V)"
	.		""
	undo		"Undo (Ctrl-Z)"
    } {
	if { $but == "." } {
	    
	    pack [frame $toolbar.space[incr space_nr] -width 5] \
		-side left
	    
	} else {
	    
	    pack [button $toolbar.$but \
		      -image [GT::get_image $but] \
		      -relief flat \
		      -borderwidth 1 \
		      -command [namespace code "action $editor $but"] \
		     ] \
		-side left \
		-ipadx 1 -ipady 1

	    bind $toolbar.$but <Enter> \
		+[list $toolbar.$but configure -relief raised]
	    bind $toolbar.$but <Leave> \
		+[list $toolbar.$but configure -relief flat]
	    GT::tooltips $toolbar.$but $tooltip
	}
    }

    # $toolbar.undo configure -state disabled

    #
    # Add a combobox for "View"
    #

    package require Combobox
    Combobox::create $toolbar.view \
	-width 10 \
 	-command \
	"GT::menu_action $editor zoom \[Combobox::value $toolbar.view\]" \
	-value 100% \
	-regexp {^[0-9][0-9]?[0-9]?[0-9]?[0-9]?[0-9]?(\.[0-9]*)?%?$}
    foreach size $GT_options(zoom_sizes) {
	Combobox::add $toolbar.view [expr int($size*100)]%
    }
    pack $toolbar.view \
	-side left \
	-padx 25

    #
    #   Create Special Toolbar Buttons (Note: order in list is reversed)
    #

# 	{ generate "Generate" generate.xbm
# 	    "Generate Graphs" }
# 	{ clean_up "Clean_Up" clean_up.xbm
# 	    "Clean Up" }
# 	{ arrange Arrange arrange.xbm
# 	    "Arrange Nodes and Edges" }
# 	{ select Select select.xbm
# 	    "Manipulate the Selection" }

    foreach button {
	{ mini_inspector "*" mini_inspector.xbm
	    "Mini Inspector (Prototype).
The mini inspector is a menu which lets you manipulate the attributes of an object." }
    } {

	foreach {menu Name bitmap tooltips} $button {}

	menubutton $toolbar.$menu \
	    -image [GT::get_image $bitmap] \
	    -relief flat \
	    -menu $toolbar.$menu.menu \
	    -padx 0 \
	    -pady 0
	menu $toolbar.$menu.menu \
	    -postcommand [list GT::post_menu $editor $toolbar.$menu.menu $Name]

	bind $toolbar.$menu <Enter> \
	    +[list %W configure -relief raised]
	bind $toolbar.$menu <Leave> \
	    +[list %W configure -relief flat]

	set create GT::create_${Name}_menu
	if {![info exists GT_menu($editor,$Name,create_procs)] ||
	    [lsearch $GT_menu($editor,$Name,create_procs) $create] == -1} {
	    lappend GT_menu($editor,$Name,create_procs) $create
	}

	# GT::tooltips $toolbar.$menu $tooltips

	pack $toolbar.$menu \
	    -side right \
	    -ipadx 1 \
	    -ipady 1
    }


    return $toolbar
}


proc GT::toolbox_view {editor combobox view} {
    global GT
    $combobox set $view
    GT::menu_action $editor zoom $view
    focus $GT($editor,canvas)
}


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
