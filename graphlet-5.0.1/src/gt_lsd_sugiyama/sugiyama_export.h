/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */

#include "sgraph/std.h"
#include "sgraph/sgraph.h"
#include "sgraph/sgraph_interface.h"
#include <xview/xview.h>

#define	SIZE	250	/* maximum number of nodes per level	*/
#define	level(n)	((n)->y)
/* That was *before* the manual did forbid it - MH */

extern int maxlevel;		 	/* groesstes auftretendes Level */
extern int nodes_of_level[SIZE];	/* Anzahl der Knoten pro Level  */

typedef	struct {

	int	vertical_distance;	/* distance between levels	*/
	int	horizontal_distance;	/* distance between nodes/bends	*/
	int	it1, it2;		/* Iterations phase 1 / phase 2 */
	int	size_defaults_x,
		size_defaults_y;

	int	leveling; 	/* algorithm to compute Levels		*/
	int	up;		/* algorithm to compute upward arcs	*/
	int	reduce_crossings_algorithm;
	int	mult_distance;	/* obsolete */
	int	width;		/* parameter to compute  number of	*/
				/* nodes per level (Graham_Coffman)	*/
}
	Sugiyama_settings;

extern	Sugiyama_settings sugiyama_settings;

/* extern	Sugiyama_settings init_sugiyama_settings(void);   */
Sugiyama_settings init_sugiyama_settings (int vert_dist,
    int horiz_dist, int it1, int it2, int arrange, int res_cycles,
    int reduce_cross);


/* Misc	*/
extern	void	save_sugiyama_settings (void);
extern	void	show_sugiyama_subframe (void * done_proc);

extern	void	call_sugiyama_layout (Sgraph_proc_info info);	/* call_sgraph_proc	*/
extern	void	call_sugiyama_left_to_right_layout (Sgraph_proc_info info);
extern	int	sugiyama (Sgraph sgraph, int horizontal_distance, int vertical_distance);			/* the layouter - old	*/
extern	int	sugiyama_layout (Sgraph sgraph, Sugiyama_settings settings);		/* the layouter		*/
extern	int	sugiyama_left_to_right_layout (Sgraph sgraph, Sugiyama_settings settings);
	
/* Menu callback procs	*/
/* WA
GraphEd_Menu_Proc menu_sugiyama_layout;
GraphEd_Menu_Proc menu_sugiyama_left_to_right_layout;
GraphEd_Menu_Proc menu_sugiyama_layout_subframe;
*/
