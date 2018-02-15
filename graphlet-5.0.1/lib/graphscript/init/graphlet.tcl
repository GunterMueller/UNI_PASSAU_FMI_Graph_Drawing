# This software is distributed under the Lesser General Public License
#
# graphlet.tcl
#
# The description of filename goes HERE.
#
#------------------------------------------
#
# $Source: /home/br/CVS/graphlet/lib/graphscript/init/graphlet.tcl,v $
# $Author: himsolt $
# $Revision: 1.15 $
# $Date: 1999/03/05 20:41:44 $
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
	init_graphlet \
	init_hooks \
	fix_tkMenuFind
}


##########################################
#
# GT::init_graphlet
#
##########################################


proc GT::init_graphlet { } {

    global GT GT_menu GT_options env

    set GT(frame_nr) -1

    set GT(default_message) "Graphlet Version $GT(version)"

    set default_menubar { File Edit Select View Graph Node E^dge Tool Layout }

    if {
	$GT_options(developer) == 1 ||
	([info exists GT_options(show_developer_menu)] &&
	 $GT_options(show_developer_menu))
    } {
	lappend default_menubar Develo^per
    }

    if [info exists GT(menubar)] {
	set GT(menubar) \
	    [concat $default_menubar $GT(menubar)]
    } else {
	set GT(menubar) $default_menubar
    }

    foreach menu "$default_menubar Help" {
	regsub \\^ $menu "" menu
 	lappend GT_menu($menu,create_procs) GT::create_${menu}_menu
    }

    GT::init_modes
    GT::init_tooltips
    GT::init_hooks

    set GT(graphlet_initialized) 1	

    if [file readable $GT_options(initfile)] {
	namespace eval :: [list source $GT_options(initfile)]
    }
}



proc GT::init_hooks { } {

    #
    # Handlers
    #
    # Handlers are called from equivalent LEDA's graph handlers.
    #

    GT::add_hook post_new_graph_handler {
	GT::debug_hook
    }

    GT::add_hook pre_new_node_handler {
	GT::debug_hook
    }
    GT::add_hook post_new_node_handler {
	GT::debug_hook
    }

    GT::add_hook pre_del_node_handler {
	GT::debug_hook
    }
    GT::add_hook post_del_node_handler {
	GT::debug_hook
    }


    GT::add_hook pre_new_edge_handler {
	GT::debug_hook
    }
    GT::add_hook post_new_edge_handler {
	GT::debug_hook
    }

    GT::add_hook pre_del_edge_handler {
	GT::debug_hook
    }
    GT::add_hook post_del_edge_handler {
	GT::debug_hook
    }

    #
    # Hooks
    #
    # Hooks are called from the Graphscript interface only.
    #

    GT::add_hook pre_new_graph_hook {
	GT::debug_hook
    }
    GT::add_hook post_new_graph_hook {
	GT::debug_hook
	GT::post_new_graph_hook
    }
    GT::add_hook pre_new_node_hook {
	GT::debug_hook
    }
    GT::add_hook post_new_node_hook {
	GT::debug_hook
    }
    GT::add_hook pre_new_edge_hook {
	GT::debug_hook
    }
    GT::add_hook post_new_edge_hook {
	GT::debug_hook
    }

    GT::add_hook pre_delete_graph_hook {
	GT::debug_hook
    }
    GT::add_hook post_delete_graph_hook {
	GT::debug_hook
    }
    GT::add_hook pre_delete_node_hook {
	GT::debug_hook
    }
    GT::add_hook post_delete_node_hook {
	GT::debug_hook
    }
    GT::add_hook pre_delete_edge_hook {
	GT::debug_hook
    }
    GT::add_hook post_delete_edge_hook {
	GT::debug_hook
    }

    GT::add_hook pre_copy_graph_hook {
	GT::debug_hook
    }
    GT::add_hook post_copy_graph_hook {
	GT::debug_hook
    }
    GT::add_hook pre_copy_node_hook {
	GT::debug_hook
    }
    GT::add_hook post_copy_node_hook {
	GT::debug_hook
    }
    GT::add_hook pre_copy_edge_hook {
	GT::debug_hook
    }
    GT::add_hook post_copy_edge_hook {
	GT::debug_hook
    }

    GT::add_hook pre_configure_graph_hook {
	GT::debug_hook
    }
    GT::add_hook post_configure_graph_hook {
	GT::debug_hook
    }
    GT::add_hook pre_configure_node_hook {
	GT::debug_hook
    }
    GT::add_hook post_configure_node_hook {
	GT::debug_hook
    }
    GT::add_hook pre_configure_edge_hook {
	GT::debug_hook
    }
    GT::add_hook post_configure_edge_hook {
	GT::debug_hook
    }
    GT::add_hook pre_configure_style_hook {
	GT::debug_hook
    }
    GT::add_hook post_configure_style_hook {
	GT::debug_hook
    }

    GT::add_hook pre_directed_hook {
	GT::debug_hook
    }
    GT::add_hook post_directed_hook {
	GT::debug_hook
	GT::post_directed_hook
    }

    GT::add_hook pre_canvas_hook {
	GT::debug_hook
	GT::pre_canvas_hook
    }
    GT::add_hook post_canvas_hook {
	GT::debug_hook
	GT::post_canvas_hook
    }

    GT::add_hook pre_editor_hook {
	GT::debug_hook
	GT::pre_editor_hook 
    }
    GT::add_hook post_editor_hook {
	GT::debug_hook
	GT::post_editor_hook 
    }

    GT::add_hook pre_draw_hook {
	GT::debug_hook
    }
    GT::add_hook post_draw_hook {
	GT::debug_hook
    }

    GT::add_hook pre_save_hook {
	GT::debug_hook
    }
    GT::add_hook post_save_hook {
	GT::debug_hook
    }
    GT::add_hook pre_load_hook {
	GT::pre_load_graph_hook
	GT::debug_hook
    }
    GT::add_hook post_load_hook {
	GT::post_load_graph_hook
	GT::debug_hook
    }

    GT::add_hook pre_scale_hook {
	GT::debug_hook
    }
    GT::add_hook post_scale_hook {
	GT::debug_hook
    }
    
    GT::add_hook pre_style_hook {
	GT::debug_hook
    }
    GT::add_hook post_style_hook {
	GT::debug_hook
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
