#include "lp_graph.h"

extern	int		vertical_lp_edgeline_segment	(lp_Edgeline first, lp_Edgeline second);
extern	int		horizontal_lp_edgeline_segment	(lp_Edgeline first, lp_Edgeline second);
extern  lp_Edgeline	new_lp_edgeline			(int x, int y);
extern	lp_Edgeline 	add_to_lp_edgeline		(lp_Edgeline line, int x, int y);
extern	lp_Edgeline 	delete_from_lp_edgeline		(lp_Edgeline line, lp_Edgeline point);
extern	void		write_lp_edgeline		(lp_Edgeline line);
extern	lp_Edgeline	concat_lp_edgelines		(lp_Edgeline first, lp_Edgeline second);
extern 	lp_Edgeline	copy_lp_edgeline		(lp_Edgeline line);
extern	void		free_lp_edgeline		(lp_Edgeline line);
extern	lp_Edgeline	cp_edgeline_to_lp_edgeline	(Edgeline line);
extern	Edgeline	cp_lp_edgeline_to_edgeline	(lp_Edgeline line);
extern	int		neighbor_dirs			(int dir1, int dir2);
extern	int		lp_dir				(lp_Edgeline first, lp_Edgeline second);
extern	int		Xi				(int dir1, int dir2);
extern	int		first_dir			(Edge edge);
extern	int		last_dir			(Edge edge);
extern	int		T_dir				(int dir, int t);
extern	int		T_side				(int side, int t);
extern	int 		opposite_side			(int side);
extern	int 		opposite_dir			(int dir);
extern	void		clip_lp_edgeline		(lp_Edgeline outside, lp_Edgeline inside, Node node);
extern	void		write_tree_lp_edgelines		(tree_ref tree);
extern	int		bend_number_of_unique_graph	(Graph graph);
extern	int		lp_edgeline_length		(lp_Edgeline line);
