# This software is distributed under the Lesser General Public License
#
# sample_startup.tcl
#
#
# To make Graphlet autoload thie file, proceed as follows:
# - put this file in your working directory
# - create a Tcl auto Index, e.g.
#   graphscript
#   % auto_mkindex . sample_startup.tcl
# - start Graphlet as usual
#
# GraphScript will automatically load any procedure
# "GraphScript_user_init" if present.
#
#
# You can also put startup code into the file ".graphlet"
#
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/src/graphscript/sample_startup.tcl,v $
# $Author: himsolt $
# $Revision: 1.2 $
# $Date: 1999/03/05 20:42:46 $
# $Locker:  $
# $State: Exp $
#
#------------------------------------------
#
# (C) University of Passau 1995-1999, graphlet project
#



##########################################
#
# GraphScript_user_init
#
# GraphScript executes this procedure automatically
#
##########################################


proc GraphScript_user_init {} {

    #
    # GT is a global variable which holds information on the
    # current state of Graphlet.
    #

    global GT GT_menu

    #
    # Add a new menu to the menubar
    #
    
    lappend GT(menubar) Sample

    #
    # The menu Sample is created with the procedure GT_create_Sample_menu
    #
    
    set GT_menu(Sample,create_procs) GT_create_Sample_menu
}


##########################################
#
# GT_create_Sample_menu { top menu }
#
# Per convention, top is always the current editor window.
# menu is the (already created) Tk menu "Sample".
#
##########################################


proc GT_create_Sample_menu { top menu } {

    #
    # GT is a global variable which holds information on the
    # current state of Graphlet.
    #

    global GT

    #
    # Add a menu entry "Sample Layout".
    #
    # Parameters to GT_add_menu_command are
    # - The menu entry
    # - the action
    # - The keyboard accelerator
    # - The memnonic (0 if none)
    # - menu state: normal, active or disabled (mey be empty)
    #
    
    GT_add_menu_command $top $menu {
	"Sample Layout" layout_Sample "Meta+S" 0 active
    }

    #
    # Define the action layout_Sample. Graphlet does specify
    # procedures in menu or button callbacks, but rather uses an
    # action name.
    #
    # The global variable GT(action,name) maps name to a
    # procedure.
    #
    
    set GT(action,layout_Sample) GT_action_layout_Sample

}



##########################################
#
# GT_action_layout_Sample { top }
#
# Per convention, top is always the current editor window.
#
##########################################


proc GT_action_layout_Sample  { top  } {

    #
    # GT is a global variable which holds information on the
    # current state of Graphlet.
    #

    global GT

    #
    # $GT($top,graph) is the graph in the current window
    #

    set graph $GT($top,graph)
    
    #
    # Since we are changing the graph, we need to remove the
    # edge selection markers
    #

    GT_mark_selection $top selected unmark edge

    #
    # Perform a simple layout: swap x and y coordinates
    #

    foreach node [$graph nodes] {
	set x [$graph nodeget $node -x]
	set y [$graph nodeget $node -y]
	$graph nodeconfigure $node -x $y
	$graph nodeconfigure $node -y $x
    }	

    #
    # After we have changed coordinates, draw the graph
    #

    $graph draw

    #
    # Since we are changing the graph, we need to add the
    # edge selection markers
    #

    GT_mark_selection $top selected mark edge
}


##########################################
#
# Set emacs variables
#
##########################################

# ;;; Local Variables: ***
# ;;; major-mode: tcl-mode ***
# ;;; tcl-indent-level: 4 ***
# ;;; End: ***
