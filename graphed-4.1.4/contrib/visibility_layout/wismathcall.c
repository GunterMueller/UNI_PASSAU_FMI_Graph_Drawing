/**************************************************************************/
/***                                                                    ***/
/*** Filename: WISMATHCALL.C                                            ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "algorithms.h"

#include "visibility_layout_export.h"
#include "tarjanmod.h"

Global   void    wismath_draw(Sgraph g, int verbose);

/*                                                                            */
/* ************************************************************************** */
/* This function is the call-function for the Wismath-algorithm. It combines  */
/* the different parts of the algorithm and returns 1, if all went right      */
/*                                              and 0, if not.                */
/* ************************************************************************** */
/*                                                                            */

Global	void	call_wismath_planar_layout (Sgraph_proc_info info)
{
int	x_gridsize = tarjan_settings.horizontal_distance,
	y_gridsize = tarjan_settings.vertical_distance,
	node_height = tarjan_settings.vertical_height,
	face	 = tarjan_settings.largest_face,
	poly_line = tarjan_settings.polyline,
	betterpoly_line = tarjan_settings.betterpolyline,
	greedy		= tarjan_settings.greedy,
	verbose		= tarjan_settings.verbose,
	stedge		= tarjan_settings.greedy;
Sgraph  g;
int	scale=1;
g = info->sgraph;

if(test_conditions_for_layout_drawing(g) == 1)
    	{
        /*message("Creating wismath layout \n");*/

	if(greedy)greedy = WISMATH;
	init_node_and_edges(g,face,greedy,stedge);
	/* create structures , st-number the graph */

	wismath_draw(g,verbose);
	/* compute x,y,dx,dy for nodes and edges,*/

	tarjan_set(g,x_gridsize/scale,y_gridsize/scale,node_height/scale,verbose);
	/*   set to graphed-structure */

	free_node_and_edges(g);

	if(betterpoly_line)polyline(g,BETTER,x_gridsize/scale,y_gridsize/scale,node_height/scale);
	else	{
		if(poly_line)polyline(g,CENTER,x_gridsize/scale,y_gridsize/scale,node_height/scale);
		}
    	info->recompute = 1;
    	info->repaint   = 1;
    	info->recenter  = 1;
    	}
}

void menu_wismath_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_wismath_planar_layout, NULL);
}



/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: WISMATHCALL.C                     ** */
/* ************************************************************************** */

