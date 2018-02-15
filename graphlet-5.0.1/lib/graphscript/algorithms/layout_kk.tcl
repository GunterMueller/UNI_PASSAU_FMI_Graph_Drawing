# This software is distributed under the Lesser General Public License
#
# algorithms/.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_kk.tcl,v $
# $Author: himsolt $
# $Revision: 1.11 $
# $Date: 1999/04/10 15:27:10 $
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
package require Gt_lsd_springembedder_kamada

namespace eval GT {
    namespace export \
	LSD_action_layout_spring_kk \
	LSD_action_layout_spring_kk_options
}

proc GT::LSD_action_layout_spring_kk { editor } {
    
    global GT GT_options
    set graph $GT($editor,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor \
	-check_selection 0 \
	-name "Spring Embedder Layout (Kamada)" \
	-command {
	    layout_spring_kk $graph \
		-edgelength $GT_options(spring_kk_edgelength)
	}
}



proc GT::LSD_action_layout_spring_kk_options { editor } {

    global GT GT_options

    lappend options {
	newtab "General"
    } {
	integer "Edge Length" spring_kk_edgelength
	"Desired edge length and distance between nodes."
    } {
	endtab
    }

    if {$GT_options(expert)} {
	GT::create_tabwindow $editor \
	    springembedder_kk_options "Springembedder KK" \
	    $options layout_spring_kk
    } else {
	GT::create_tabwindow $editor \
	    springembedder_kk_options "Springembedder KK" \
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
