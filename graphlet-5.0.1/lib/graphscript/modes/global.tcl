# This software is distributed under the Lesser General Public License
#
# global.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/modes/global.tcl,v $
# $Author: himsolt $
# $Revision: 1.6 $
# $Date: 1999/03/05 20:42:24 $
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
	ev_select_node \
	ev_select_edge \
	label_selected_object
}


##########################################
#
# Global select/deselect event handlers
#
##########################################

proc GT::ev_select_node { {hints {}} args} {

    global GT_event GT_selection
    set editor $GT_event(editor)

    set node [GT::find_object $editor node current]
    if { [lsearch $GT_selection($editor,selected,node) $node] >= 0 } {
	if { [lsearch $hints if_not_selected] >= 0 } {
	    return
	}
    }
    GT::select $GT_event(editor) $node

    return -code break;
}


proc GT::ev_select_edge { {hints {}} } {

    global GT_event GT_selection
    set editor $GT_event(editor)

    set edge [GT::find_object $editor edge current]
    if { [lsearch $GT_selection($editor,selected,edge) $edge] >= 0 } {
	if { [lsearch $hints if_not_selected] >= 0 } {
	    return
	}
    }

    if { [lsearch $hints shift] >= 0 } {
	GT::select $GT_event(editor) toggle $edge
    } else {
	GT::select $GT_event(editor) select $edge
    }

    return -code break;
}



##########################################
#
#  Global Event handler for <Key>
#
# GT::label_selected_object {interpret_delete {}}
#
# If interpret_delete is set, then a <Delete> event deletes the
# last character of the label. Otherwise, the procedure returns
# with error code continue (which usually results in the object
# being deleted).
#
##########################################


proc GT::label_selected_object {{interpret_delete {}}} {

    global GT_event GT_options GT_selection

    set key $GT_event(A)
    set canvas $GT_event(W)
    set graph $GT_event(graph)
    set editor $GT_event(editor)

    if { $GT_event(K) == "Delete" } {
	if {$interpret_delete != {}} {
	    set key \x7F
	} else {
	    return
	}
    }

    if { $key != {} } {

	set select_item [$canvas select item]
	set in_focus [$canvas focus]

	if { $select_item != {} } {

	    #
	    # First see what is selected
	    #

	    switch $key {
		\b {
		    $canvas dchars $select_item sel.first sel.last
		}
		\x7f {
		    $canvas dchars $select_item sel.first sel.last
		}
		\n {
		    $canvas dchars $select_item sel.first sel.last
		    $canvas insert $select_item insert \n
		}
		default {
		    $canvas dchars $select_item sel.first sel.last
		    $canvas insert $select_item insert $key
		}
	    }

	    GT::text_write_label $canvas $select_item

	} elseif { $in_focus != {} } {

	    #
	    # Next, try the object in the focus
	    #

	    switch -- $key {
		\b {
		    set index [$canvas index $in_focus insert]
		    if { $index > 0 } {
			$canvas icursor $in_focus [expr $index-1]
			$canvas dchars $in_focus insert
		    }
		}
		\x7f {
		    $canvas dchars $in_focus insert
		}
		\r {
		    $canvas insert $in_focus insert \n
		}
		default {
		    $canvas insert $in_focus insert $key
		}
	    }

	    GT::text_write_label $canvas $in_focus

	} else {

	    #
	    # If none of the above holds, label all selected objects
	    #

	    foreach type $GT_selection(types) {
		foreach object $GT_selection($editor,selected,$type) {
		    
		    set label [$graph get $object -label]
		    switch -regexp -- $key {
			\b|\x7f {
			    set newlastindex [expr [string length $label] - 2 ]
			    set newlabel [string range $label 0 $newlastindex]
			}
			\r {
			    set newlabel "$label\n"
			}
			default {
			    set newlabel "$label$key"
			}
		    }

		    GT::undo $editor newframe
		    GT::undo $editor attributes $object -label
		    $graph configure $object -label $newlabel
		    GT::undo $editor endframe

		}
	    }

# 	    if $GT_options(adjust_size_to_label) {
# 		GT::adjust_size_to_label $graph \
# 		    $GT_selection($editor,selected,node)
# 	    }

	    $graph draw
	}


	return -code break
    }

    return -code continue
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
