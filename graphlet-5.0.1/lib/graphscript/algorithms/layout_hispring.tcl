# This software is distributed under the Lesser General Public License
#
# algorithms/.tcl
#
###########################################
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/algorithms/layout_hispring.tcl,v $
# $Author: himsolt $
# $Revision: 1.6 $
# $Date: 1999/03/05 20:40:41 $
# $Locker:  $
# $State: Exp $
#
###########################################
#
# (C) University of Passau 1995-1999, graphlet Project
#


package require Graphlet
package provide Graphscript [gt_version]

namespace eval GT {
    namespace export \
	action_layout_hispring \
	action_layout_hispring_options
}


##############################
#
# Hierarchical Springembedder
#
##############################


proc GT::action_layout_hispring { top } {
    
    global GT GT_options
    set graph $GT($top,graph)

    GT::undo $editor attributes [$graph nodes] -x -y
    GT::undo $editor attributes [$graph edges] -line

    GT::excursion $editor {
	layout_hispring $graph \
	    $GT_options(hispring_show_hie) \
	    $GT_options(hispring_unite_edges) \
	    $GT_options(hispring_minimize_nodes) \
	    $GT_options(hispring_variable_edgelen) \
	    $GT_options(hispring_force_GEM) \
	    $GT_options(hispring_what_hierarchie) \
	    $GT_options(hispring_max_nodes_per_set) \
	    $GT_options(hispring_external_scaling)
    }

    GT::action_find_graph $top
}



proc GT::action_layout_hispring_options { top } {

    global GT GT_options

    set setting {newfolder "Additional"}

    lappend options { check
	"Unite Edges"
	hispring_unite_edges
    }
	
    lappend options { check
	"Minimize Nodes"
	hispring_minimize_nodes
    }

    lappend options { check
	"Variable Edgelength"
	hispring_variable_edgelen
    }

    lappend options { check
	"Forces a'la GEM"
	hispring_force_GEM
    }

    lappend options { radio
	"What Hierarchie"
	hispring_what_hierarchie
	{ "Minimimal Cuts" "Graph Center" }
    }

    lappend options { check
	"Show Hierarchie"
	hispring_show_hie
    }
	
    lappend options { integer
	"Maximal Nodenumber"
	hispring_max_nodes_per_set
	{ scale 1 150 }
    }

    lappend options { integer
	"External Scaling"
	hispring_external_scaling
	{ scale 1 10 }
    }

    set dialog(title) "Hierarchical Springembedder"
    set dialog(name) lsd_hispring_frame

    GT::simple_dialog $top dialog $options layout_hispring
}



##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; mode: tcl ***
# ;;; End: ***
