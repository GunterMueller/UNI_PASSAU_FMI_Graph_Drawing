/*===========================================================================*/
/*  
	 PROJECT	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_export.h
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code of the first part of the local_main part of the graphed
	 extension module st_upward_draw, based on the algorithm upward_draw
	 presented by Guiseppe Di Battista and Roberto Tamassia, published
	 in "Algorithms For Plane Representations of Acyclic Digraphs",
	 Theoretical Computer Science 61 (1988), pp. 175-198.
	 In the following this work will be referenced as [DiBatTam88].
*/
/*===========================================================================*/

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/slist.h>
#include <sgraph/algorithms.h>
#include <xview/xview.h>
 
typedef struct {
	int	alpha;
	int 	max_len;
	int	t_div;
	int	max_node_dist;
	int	select_candidate_mode;
	int	size_x;
	int	size_y;
	int	vertical_dist;
	int	horizontal_dist;
}
	st_Upward_Draw_Settings;

#define	SCM_HIGHEST_CANDIDATE	0
#define	SCM_MIDDLE_CANDIDATE    1	
#define	SCM_LOWEST_CANDIDATE	2
#define	SCM_RANDOM_CANDIDATE	3

		/* the settings and initialisation procedure */
extern 	st_Upward_Draw_Settings	st_settings;
extern	st_Upward_Draw_Settings	init_st_settings(void);

extern	void	save_st_settings(void);
extern	void	show_st_subframe(void *done_proc);

		/* call sgraph proc */
extern	void	call_st_upward_draw_layout(Sgraph_proc_info info);
		/* the layout algorithm */
extern	bool	st_upward_draw_layout(Sgraph graph);
extern  bool	st_upward_draw();

		/* Menu callback procedure: algorithm */
GraphEd_Menu_Proc menu_st_upward_draw_layout;
		/* Menu callback procedure: settings */
GraphEd_Menu_Proc menu_st_upward_draw_layout_subframe;
	
 
