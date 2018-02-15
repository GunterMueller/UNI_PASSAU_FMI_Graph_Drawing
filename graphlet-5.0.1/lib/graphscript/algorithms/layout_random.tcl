# This software is distributed under the Lesser General Public License
#
# layout_random.tcl
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_random.tcl,v $
# $Author: himsolt $
# $Revision: 1.4 $
# $Date: 1999/03/05 20:40:48 $
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
	action_layout_random
}




proc GT::action_layout_random editor {

    global GT
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    foreach {x0 y0 x1 y1} [GT::visible_drawing_area $editor corrected] {}

    foreach node [$graph nodes] {
	$graph set $node graphics \
		-x [expr $x0 + rand()*($x1-$x0)] \
		-y [expr $y0 + rand()*($y1-$y0)] 
    }

    foreach edge [$graph edges] {
	set line [$graph get $edge -line]
	if {[llength $line] > 4} {
 	    set newline [list [lindex $line 0] [lindex $line 1]]
	    for {set i 2} {$i < ([llength $line]-2)} {incr i 2} {
		lappend newline [expr $x0 + rand()*($x1-$x0)]
		lappend newline [expr $y0 + rand()*($y1-$y0)]
	    }
	    lappend newline [lindex $line [expr [llength $line]-2]]
	    lappend newline [lindex $line end]
	    $graph set $edge -line $newline
	}
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
