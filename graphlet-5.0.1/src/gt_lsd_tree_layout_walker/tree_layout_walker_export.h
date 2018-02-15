/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */

#include <sgraph/sgraph_interface.h>

typedef	struct {
	int	vertical_separation;
	int	siblingseparation;
	int	subtreeseparation;

	int	size_defaults_x_sibling,
		size_defaults_x_subtree,
		size_defaults_y;
}
	Tree_layout_walker_settings;

extern	Tree_layout_walker_settings	tree_layout_walker_settings;
extern	Tree_layout_walker_settings	init_tree_layout_walker_settings(void);
extern	void	save_tree_layout_walker_settings (void);

/* Menu procedures	
extern	GraphEd_Menu_Proc menu_tree_layout_walker;
extern	GraphEd_Menu_Proc menu_tree_layout_walker_left_to_right;
extern	GraphEd_Menu_Proc menu_tree_layout_walker_settings;
*/

extern	void	show_tree_layout_walker_subframe (void (*graphed_done_proc) ());
extern	int	showing_tree_layout_walker_subframe (void);

/*call_sgraph_proc's	*/
extern	void	call_tree_layout_walker (Sgraph_proc_info info, int v, int si, int su);
extern	void	call_tree_layout_walker_left_to_right (Sgraph_proc_info info);

/* The real one	*/
extern	int	tree_layout_walker (Sgraph sgraph, Tree_layout_walker_settings settings);
