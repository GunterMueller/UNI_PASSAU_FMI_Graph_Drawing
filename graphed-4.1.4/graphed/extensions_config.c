/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/

#include "user_header.h"

#include "sgraph/std.h"
#include "sgraph/random.h"
#include "sgraph/sgraph.h"
#include "sgraph/slist.h"
#include "sgraph/graphed.h"
#include "sgraph/algorithms.h"
#include "graphed_sgraph_interface.h"

#include "existing_extensions.h"
#include "layout_suite/layout_suite_export.h"
#include "extensions.h"
#include "menu.h"
        

static	void	null_proc (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
}


Slist	sgraphs_proc (Sgraph_selection_info selection_info, Slist list)
{
	Slist	l, group;
	Sgraph_proc_info info;
	char	buffer[1000];
	int	i;

	group = empty_slist;
	for_slist (list, l) {
		info = &((attr_data_of_type (l, Call_sgraph_proc_info))->sgraph_proc_info);
		message ("a graph %d\n");
		if (info->sgraph != NULL &&
		    info->sgraph->nodes != NULL &&
		    info->sgraph->nodes->label != NULL) {
			message ("* label %s\n", info->sgraph->nodes->label);
			sprintf (buffer, "%d", i++);
			set_nodelabel (info->sgraph->nodes, strsave (buffer));
			if (info->sgraph->nodes->suc != empty_snode) {
				remove_node (info->sgraph->nodes->suc);
			}
		}
		if (info->sgraph != empty_sgraph && info->sgraph->nodes != empty_snode) {
			group = add_to_slist (group, make_attr(ATTR_DATA, (char *)info->sgraph->nodes));
		}
	} end_for_slist (list, l);

	get_buffer_by_name ("- a new buffer");

	selection_info->selected  = SGRAPH_SELECTED_GROUP;
	selection_info->selection.group = group;
	return list;
}

#if 0
static	void	test_disable_proc (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	static int	count;
	
	set_disable_all_structure_modifying_commands (NULL);
	set_disable_all_modifying_commands (NULL);
	set_disable_all_commands (NULL);
	switch (count % 3) {
	    case 0 :
		set_disable_all_commands ("null_null_proc 0");
		message ("All commands disabled\n");
		break;
	    case 1 :
		set_disable_all_modifying_commands ("null_null_proc 1");
		message ("All modifying commands disabled\n");
		break;
	    case 2 :
		set_disable_all_structure_modifying_commands ("null_null_proc 2");
		message ("All structure modifying commands disabled\n");
		break;
	}
	count ++;

	call_sgraphs_proc (sgraphs_proc, NULL);
}

Local	void	print_node_attributes (FILE *file, Snode n)
{
  printf (" {$ %ld %ld $} ", random() % 1000, random() % 1000);
}
#endif


void	init_extra_menu (void)
{
	/* local extensions */

	Menu	wrappers_menu;

	extern	GraphEd_Menu_Proc menu_test_graph_is_drawn_planar;
	extern	GraphEd_Menu_Proc menu_test_graph_edges_are_drawn_planar;
	extern	GraphEd_Menu_Proc menu_test_graph_edges_are_straight_line_planar;
	extern	GraphEd_Menu_Proc menu_test_find_non_straight_line_edge;
	extern	GraphEd_Menu_Proc menu_test_find_cycle_in_directed_graph;
	extern	GraphEd_Menu_Proc menu_remove_all_self_loops_in_graph;
	extern	GraphEd_Menu_Proc menu_remove_all_multiple_edges_in_graph;
	extern	GraphEd_Menu_Proc menu_shrink_buffer;
	extern	GraphEd_Menu_Proc menu_make_window;
	extern	GraphEd_Menu_Proc set_on_grid_points;
	extern	GraphEd_Menu_Proc fit_nodes_to_text;

	add_to_goodies_menu ("Set on grid points", set_on_grid_points);
	add_to_goodies_menu ("Fit nodes to text", fit_nodes_to_text);

	wrappers_menu = graphed_create_pin_menu("Wrappers");

	add_entry_to_menu (wrappers_menu,
		"Check planar drawing",
		menu_test_graph_is_drawn_planar);
	add_entry_to_menu (wrappers_menu,
		"Check planar edges",
		menu_test_graph_edges_are_drawn_planar);
/*
	add_entry_to_menu (wrappers_menu,
		"check planar straight line edges",
		menu_test_graph_edges_are_straight_line_planar);
*/
	add_entry_to_menu (wrappers_menu,
		"Check straight line edges",
		menu_test_find_non_straight_line_edge);
	add_entry_to_menu (wrappers_menu,
		"Find a directed cycle",
		menu_test_find_cycle_in_directed_graph);
	add_entry_to_menu (wrappers_menu,
		"Remove all self loops",
		menu_remove_all_self_loops_in_graph);
	add_entry_to_menu (wrappers_menu,
		"Remove all multiple edges",
		menu_remove_all_multiple_edges_in_graph);

	add_menu_to_goodies_menu ("Wrappers", wrappers_menu);
	add_to_goodies_menu ("Fit window to graph", menu_shrink_buffer);
	add_to_goodies_menu ("---", null_proc);

#include "extensions.c"

}
