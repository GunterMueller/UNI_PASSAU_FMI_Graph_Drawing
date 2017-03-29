/* (C) Universitaet Passau 1986-1994 */
#include "sgraph/std.h"
#include "sgraph/slist.h"
#include "sgraph/sgraph.h"
#include "sgraph/graphed.h"
#include "utils_export.h"


/************************************************************************/
/*									*/
/*		swap_width_and_height_in_nodes				*/
/*									*/
/************************************************************************/

Global	void	swap_width_and_height_in_nodes (Sgraph g)
{
	Snode	n;
	int	width, height;

	for_all_nodes (g, n) if (graphed_node(n) != (Graphed_node)NULL) {
		width = (int)node_get (graphed_node(n), NODE_WIDTH);
		height = (int)node_get (graphed_node(n), NODE_HEIGHT);
		node_set (graphed_node (n), ONLY_SET,
			NODE_SIZE, height, width,
			0);
	} end_for_all_nodes (g, n);

	graph_set (graphed_graph(g), RESTORE_IT, 0);
}


void	call_swap_width_and_height_in_nodes (Sgraph_proc_info info, char *arg)
{
	if (info != (Sgraph_proc_info)NULL && info->sgraph != empty_sgraph)  {
		swap_width_and_height_in_nodes (info->sgraph);
	}
	info->recompute = TRUE;
}

void menu_call_swap_width_and_height_in_nodes (Menu menu, Menu_item menu_item)
{
	call_sgraph_proc (call_swap_width_and_height_in_nodes, (char *)0);
}


/************************************************************************/
/*									*/
/*	move_sgraph (g, x,y)						*/
/*									*/
/************************************************************************/

Global void	move_sgraph (Sgraph g, int x, int y)
{
	Snode	n;
	Sedge	e;
	Edgeline line, el;

	for_all_nodes (g,n) {

	    n->x += x;
	    n->y += y;

	    for_sourcelist (n,e) {
		if (graphed_edge(e) != (Graphed_edge)NULL &&
		    (g->directed || unique_edge(e)) ) {

		    line = (Edgeline)edge_get (graphed_edge(e), EDGE_LINE);
		    for_edgeline (line, el) {
			set_edgeline_xy (el, el->x + x, el->y + y);
		    } end_for_edgeline (line, el);
		    edge_set (graphed_edge(e), ONLY_SET, EDGE_LINE, el, 0);

		}
	    } end_for_sourcelist (n,e);

	} end_for_all_nodes (g,n);
}



/************************************************************************/
/*									*/
/*	Procedures for management of geometrical transformations	*/
/*									*/
/*	Global	void	geometric_transform_sgraph (			*/
/*			g, x_function, argx, y_function, argy)		*/
/*	Sgraph	g;							*/
/*	int	(*x_function)();					*/
/*	int	(*y_function)();					*/
/*	char	*argx, *argy;						*/
/*									*/
/************************************************************************/


#define make_simple_gt_functions(xname,xfunc,yname,yfunc)		\
Global	int	xname (int center_x, int center_y, int x, char *argx,	\
						   int y, char *argy)	\
{									\
	return (xfunc);							\
}									\
									\
Global	int	yname (int center_x, int center_y, int x, char *argx,	\
						   int y, char *argy)	\
{									\
	return (yfunc);							\
}


/* Warnimg : in the first four, the signs are swapped because the	*/
/* y axis is oriented top-down on the screen				*/

make_simple_gt_functions (
	gt_turn_left_x, (center_x + (y - center_y)),
	gt_turn_left_y, (center_y - (x - center_x)))

make_simple_gt_functions (
	gt_turn_right_x, (center_x - (y - center_y)),
	gt_turn_right_y, (center_y + (x - center_x)))

make_simple_gt_functions (
	gt_mirror_q1_x, (center_x - (y - center_y)),
	gt_mirror_q1_y, (center_y - (x - center_x)))

make_simple_gt_functions (
	gt_mirror_q2_x, (center_x + (y - center_y)),
	gt_mirror_q2_y, (center_y + (x - center_x)))

make_simple_gt_functions (
	gt_mirror_horizontal_x, (x),
	gt_mirror_horizontal_y, (center_y - (y - center_y)))

make_simple_gt_functions (
	gt_mirror_vertical_x, (center_x - (x - center_x)),
	gt_mirror_vertical_y, (y))

make_simple_gt_functions (
	gt_larger_x, (center_x + 2*(x - center_x)),
	gt_larger_y, (center_y + 2*(y - center_y)))

make_simple_gt_functions (
	gt_smaller_x, (center_x + (x - center_x)/2),
	gt_smaller_y, (center_y + (y - center_y)/2))


Global	void	geometric_transform_sgraph (Sgraph g, int (*x_function) (), char *argx, int (*y_function) (), char *argy)
{
	int	center_x = 0,
		center_y = 0,
		size = 0;
	int	nx,ny;

	Snode	n;
	Sedge	e;
	Edgeline line, el;


	for_all_nodes (g,n) {
		center_x += n->x;
		center_y += n->y;
		size ++;
	} end_for_all_nodes (g,n);
	center_x /= size;
	center_y /= size;

	for_all_nodes (g,n) {

	    nx = n->x; ny= n->y;
	    n->x = x_function (center_x, center_y, nx, argx, ny, argy);
	    n->y = y_function (center_x, center_y, nx, argx, ny, argy);

	    for_sourcelist (n,e) {
		if (graphed_edge(e) != (Graphed_edge)NULL &&
		    (g->directed || unique_edge(e)) ) {

			line = (Edgeline)edge_get (graphed_edge(e), EDGE_LINE);
			for_edgeline (line, el) {
			    set_edgeline_xy (el,
				x_function (center_x, center_y,
				            el->x, argx, el->y, argy),
				y_function (center_x, center_y,
				            el->x, argx, el->y, argy));
			} end_for_edgeline (line, el);
			edge_set (graphed_edge(e), ONLY_SET, EDGE_LINE, el, 0);

		}
	    } end_for_sourcelist (n,e);

	} end_for_all_nodes (g,n);

}



#define make_transformation_proc(name,call_name,call_menu_name,gt_x,gt_y) \
void	name (Sgraph g)					\
{							\
	geometric_transform_sgraph (g,			\
			(gt_x), NULL,			\
			(gt_y), NULL);			\
}							\
							\
void	call_name (Sgraph_proc_info info, char *arg)	\
{							\
	if (info != (Sgraph_proc_info)NULL &&		\
	    info->sgraph != empty_sgraph) {		\
		name (info->sgraph);			\
	}						\
	info->recompute = TRUE;				\
}							\
							\
void call_menu_name (Menu menu, Menu_item menu_item)	\
{ 							\
	call_sgraph_proc (call_name, (char *)0);	\
}


make_transformation_proc (
	turn_left_sgraph, call_turn_left_sgraph, menu_call_turn_left_sgraph,
	gt_turn_left_x, gt_turn_left_y)

make_transformation_proc (
	turn_right_sgraph, call_turn_right_sgraph, menu_call_turn_right_sgraph,
	gt_turn_right_x, gt_turn_right_y)

make_transformation_proc (
	mirror_q1_sgraph, call_mirror_q1_sgraph, menu_call_mirror_q1_sgraph,
	gt_mirror_q1_x, gt_mirror_q1_y)

make_transformation_proc (
	mirror_q2_sgraph, call_mirror_q2_sgraph, menu_call_mirror_q2_sgraph,
	gt_mirror_q2_x, gt_mirror_q2_y)

make_transformation_proc (
	mirror_horizontal_sgraph, call_mirror_horizontal_sgraph, menu_call_mirror_horizontal_sgraph,
	gt_mirror_horizontal_x, gt_mirror_horizontal_y)

make_transformation_proc (
	mirror_vertical_sgraph, call_mirror_vertical_sgraph, menu_call_mirror_vertical_sgraph,
	gt_mirror_vertical_x, gt_mirror_vertical_y)

make_transformation_proc (
	larger_sgraph, call_larger_sgraph, menu_call_larger_sgraph,
	gt_larger_x, gt_larger_y)

make_transformation_proc (
	smaller_sgraph, call_smaller_sgraph, menu_call_smaller_sgraph,
	gt_smaller_x, gt_smaller_y)
