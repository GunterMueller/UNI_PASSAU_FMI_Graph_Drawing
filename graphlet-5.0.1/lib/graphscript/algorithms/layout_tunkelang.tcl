# This software is distributed under the Lesser General Public License
#
# algorithms/.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_tunkelang.tcl,v $
# $Author: himsolt $
# $Revision: 1.9 $
# $Date: 1999/03/05 20:40:54 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#

package require Graphlet
package provide Graphscript [gt_version]

package require Gt_lsd
package require Gt_lsd_tunkelang

namespace eval GT {
    namespace export \
	LSD_action_layout_tunkelang \
	LSD_action_layout_tunkelang_options
}



proc GT::LSD_action_layout_tunkelang { editor } {
    
    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-check_selection 0 \
	-name "Tunkelang Layout" \
	-command {
	    layout_tunkelang $graph \
		-edgelength $GT_options(tunkelang_edgelength) \
		-quality $GT_options(tunkelang_quality) \
		-recursion_depth $GT_options(tunkelang_depth) \
		-randomize $GT_options(tunkelang_randomize) \
		-crossings $GT_options(tunkelang_cut) \
		-scan_corners $GT_options(tunkelang_scan_corner)
	}
}



proc GT::LSD_action_layout_tunkelang_options { top } {

    global GT GT_options

    lappend options {
	tab "General"
    } {
	integer "Quality (small=fast)" tunkelang_quality
	""
	-scale on
	-from 2
	-to 20
    } {
	integer "Max. recursion depth" tunkelang_depth
	""
	-scale on
	-from 5
	-to 1000
    } {
	radio "Randomize adjacency lists" tunkelang_randomize
	""
	{
	    {"Yes" 0}
	    {"No" 1}
	}
    } {
	radio "Crossings" tunkelang_cut
	""
	{
	    {"Always" 0}
	    {"Finetuning" 1}
	    {"Never" 2}
	}
    } {
	radio "Scan Corners (if cross. is always)" tunkelang_scan_corner
	""
	{
	    {"Yes" 0}
	    {"No" 1}
	}
    } {
	integer "Edgelength (0=auto)" tunkelang_edgelength
	""
	-scale on
	-from 0
	-to 300
    }

    if {$GT_options(expert)} {
	GT::create_tabwindow $top \
	    layout_tunkelang_options "Tunkelang Layout" \
	    $options layout_tunkelang
    } else {
	GT::create_tabwindow $top \
	    layout_tunkelang_options "Tunkelang Layout" \
	    $options
    }
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
