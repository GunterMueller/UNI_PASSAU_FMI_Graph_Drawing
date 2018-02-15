# This software is distributed under the Lesser General Public License
#
# cutpaste.tcl
#
# This file implements Graphlet's user interface for cut&paste 
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/clipboard.tcl,v $
# $Author: himsolt $
# $Revision: 1.5 $
# $Date: 1999/07/27 18:06:27 $
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
	cutpaste
}


#
# GT::cutpaste  {editor operation to {x {}} {y {}}}
#
# "operation" is either cut|copy|paste, OR a 2-element list with
# the first element cot|copy|paste, and the second element the
# name of the clipboard. The default clipboard is named
# "clipboard" (obviously).
#
# "to" is either "clipboard" (for cut,copy only), or the name of the
# graph to paste into (for paste only)
#
# "x" and "y" are optional coordinates for paste operations. If
# present, the graph is centered over ($x,$y).
#

proc GT::cutpaste {editor operation to {x {}} {y {}}} {

    global GT GT_selection
    set graph $GT($editor,graph)
    set canvas $GT($editor,canvas)
    set canvas_selected [$canvas select item]
    set canvas_focus [$canvas focus]
    set pasted {}

    #
    # If operation is a list, then the second item is the name of 
    # the clipboard
    #

    if {[llength $operation] == 2} {
	set clipboard_name [lindex $operation 1]
    } else {
	set clipboard_name clipboard
    }

    #
    # Determine the target graph & prepare
    #

    if ![info exists GT($clipboard_name,graph)] {
	set GT($clipboard_name,graph) [graph]
    }
    if ![info exists GT($clipboard_name,text)] {
	set GT($clipboard_name,text) ""
    }
    set clipboard $GT($clipboard_name,graph)

    switch $to {
	clipboard {
	    if { $canvas_selected == {} } {
		$clipboard delete [$clipboard nodes]
		set tograph $clipboard
	    }
	}
	default {
	    set tograph $to
	}
    }

    #
    # Copy
    #

    switch -regexp [lindex $operation 0] {
	cut|copy {
	    if { $canvas_selected == {} } {
		$graph copynode $GT_selection($editor,selected,node) $tograph 
	    } else {
		set first [$canvas index $canvas_selected sel.first]
		set last [$canvas index $canvas_selected sel.last]
		set label \
		    [lindex [$canvas itemconfigure $canvas_selected -text] end]
		set GT($clipboard_name,text) [string range $label $first $last]
	    }
	}
	paste {
	    if { $canvas_focus == {} } {
		if {$x != {}} {
		    set pasted [$clipboard copynode \
				    [$clipboard nodes] \
				    $tograph \
				    $x $y]
		} else {
		    set pasted [$clipboard copynode \
				    [$clipboard nodes] \
				    $tograph]
		}
		if {[$clipboard get -directed] != [$tograph get -directed]} {
		    switch [$clipboard get -directed] {
			0 {
			    # Undirected -> Directed
			    $tograph set [lindex $pasted 1] graphics \
				-arrow last
			}
			1 {
			    # Directed -> Uirected
			    $tograph set [lindex $pasted 1] graphics \
				-arrow none
			}
			default {
			    error
			}
		    }
		}
	    } else {
		$canvas insert $canvas_focus insert $GT($clipboard_name,text)
		GT::text_write_label $canvas $canvas_focus
	    }
	}
    }

    #
    # Delete
    #

    switch [lindex $operation 0] {
	cut {
	    if { $canvas_selected == {} } {
		set objects [concat \
				 $GT_selection($editor,selected,node) \
				 $GT_selection($editor,selected,edge)]
		GT::select $editor remove $objects
		$graph delete $objects
	    } else {
		$canvas dchars $canvas_selected sel.first sel.last
		GT::text_write_label $canvas $canvas_selected
	    }
	}
    }

    return $pasted
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
