# This software is distributed under the Lesser General Public License
#
# The description of the file goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/modes/text_mode.tcl,v $
# $Author: himsolt $
# $Revision: 1.6 $
# $Date: 1999/03/05 20:42:28 $
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
	action_text_mode \
	ev_text_click \
	ev_text_set_cursor \
	ev_text_select_with_cursor \
	ev_text_paste \
	ev_text_move_cursor \
	ev_text_right \
	ev_text_left \
	ev_text_select_node_or_edge \
	ev_text_begin_of_line \
	ev_text_end_of_line \
	ev_text_begin \
	ev_text_end \
	leave_text_mode \
	text_write_label
}

#
# The following procedures are taken and partially modified from
# Brent Welch's book on Tcl/Tk.
#


###########################################
#
# Text mode action
#
###########################################


proc GT::action_text_mode  { editor  } {
    GT::switch_to_mode $editor text_mode
}



##########################################
#
# Click on empty (i.e. non-text) area
#
##########################################


proc GT::ev_text_click args {

    global GT_event GT_options

    set canvas $GT_event(W)

    if { [$canvas find withtag current] == {} } {
	$canvas focus {}
	$canvas select clear
	GT::select $GT_event(editor) remove selection
    }

    return -code break
}



##########################################
#
# Set resp. move cursor
#
##########################################


proc GT::ev_text_set_cursor args {

    global GT_event

    set canvas $GT_event(W)
    set editor $GT_event(editor)

    $canvas focus current
    $canvas icursor current @$GT_event(canvasx),$GT_event(canvasy)
    $canvas select clear
    $canvas select from current @$GT_event(canvasx),$GT_event(canvasy)

    set under_cursor [GT::find_object $editor node|edge current]
    if { $under_cursor != {} } {
	GT::select $GT_event(editor) select $under_cursor
    }

    return -code break
}


proc GT::ev_text_select_with_cursor args {

    global GT_event

    set canvas $GT_event(W)
    set in_focus [$canvas focus]

    if { $in_focus != {} } {
	$canvas icursor $in_focus @$GT_event(canvasx),$GT_event(canvasy)
	$canvas select to $in_focus \
	    [expr [$canvas index $in_focus insert]-1]
    }

    return -code break
}


proc GT::ev_text_select_all args {

    global GT_event

    set canvas $GT_event(W)
    set in_focus [$canvas focus]

    if { $in_focus != {} } {
	$canvas icursor $in_focus 0
	$canvas select from $in_focus 0
	$canvas select to $in_focus [$canvas index $in_focus end]
    }

    return -code break
}



##########################################
#
# Paste text into a label
#
##########################################


proc GT::ev_text_paste args {

    global GT_event

    set canvas $GT_event(W)

    if {[catch {selection get} _s] == 0} {
	set in_focus [$canvas focus]
	if {$in_focus != {}} {
	    $canvas insert [$canvas focus] insert $_s
	    GT::text_write_label $canvas $in_focus
	} else {
	    bell
	}
	unset _s
    }

    return -code break
}



##########################################
#
# Cursor movement
#
##########################################



proc GT::ev_text_move_cursor { where {shift {}} } {

    global GT_event

    set canvas $GT_event(W)
    set in_focus [$canvas focus]

    if { $in_focus != {}} {

	set index [$canvas index $in_focus insert]
	set old_index $index
	set label [$canvas itemcget $in_focus -text]

	switch $where {
	    left {
		incr index -1
	    }
	    right {
		incr index
	    }
	    begin {
		while {$index > 0 &&
		       [string index $label [expr $index-1]] != "\n"} {
		    incr index -1
		}
	    }
	    end {
		while {$index < [string length $label] &&
		       [string index $label [expr $index]] != "\n"} {
		    incr index
		}
	    }
	    up {
		set line 0

		set begin(0) 0
		for {set i 0} {$i < [string length $label]} {incr i} {
		    if {[string index $label $i] == "\n"} {
			set end($line) $i
			incr line
			set begin($line) [expr $i+1]
		    }
		}
		set end($line) $i

		foreach l [array names begin] {
		    if {$index >= $begin($l) && $index <= $end($l)} {
			set prev_l [expr ($l > 0) ? ($l - 1) : $l]
			set index [expr $begin($prev_l) + $index - $begin($l)]
			break
		    }
		}
	    }
	    down {
		set line 0

		set begin(0) 0
		for {set i 0} {$i < [string length $label]} {incr i} {
		    if {[string index $label $i] == "\n"} {
			set end($line) $i
			incr line
			set begin($line) [expr $i+1]
		    }
		}
		set end($line) $i

		foreach l [array names begin] {
		    if {$index >= $begin($l) && $index <= $end($l)} {
			set next_l [expr ($l < $line) ? ($l + 1) : $l]
			set index [expr $begin($next_l) + $index - $begin($l)]
			break
		    }
		}
	    }
	    prior {
		set index 0
	    }
	    next {
		set index [string length $label]
	    }
	}

	$canvas icursor $in_focus $index

	if { $shift == "shift" } {
	    if { [$canvas select item] != {} } {
		$canvas select adjust $in_focus insert
	    } else {
		$canvas select from $in_focus $old_index
		$canvas select to $in_focus insert
	    }
	} else {
	    $canvas select clear
	}
    }

    return -code break
}


##########################################
#
# GT::ev_text_select_node_or_edge
#
##########################################


proc GT::ev_text_select_node_or_edge {args} {

    global GT_event GT_options

    set canvas $GT_event(W)
    set editor $GT_event(editor)

    GT::select $editor select [GT::find_object $editor node|edge current]
    $canvas focus {}
    $canvas select clear

    if {$GT_options(text_mode_uses_label_editor) == 1} {
	GT::action $editor edit_label_dialog
    }

    # must not use -code break here 
    return
}



##########################################
#
# Leave text mode
#
##########################################


proc GT::leave_text_mode { editor mode } {
    
    global GT GT_event

    set canvas $GT($editor,canvas)

    $canvas select clear
    $canvas focus {}

    return {}
}


proc GT::text_write_label {canvas in_focus} {

    global GT_event GT_options
    set graph $GT_event(graph)
    set editor $GT_event(editor)

    set object [GT::find_object $editor node|edge $in_focus]
    if { $object != {}} {

	GT::undo $editor newframe
	GT::undo $editor attributes $object -label
	GT::undo $editor endframe

	set label [lindex [$canvas itemconfigure $in_focus -text] end]
	$graph configure $object -label $label
    }

    $graph draw
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
