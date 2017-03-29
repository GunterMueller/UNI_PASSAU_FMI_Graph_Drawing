/**************************************************************************/
/***                                                                    ***/
/*** Filename: TAMASSIACALL.C                                           ***/
/***                                                                    ***/
/**************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "algorithms.h"

#include "visibility_layout_export.h"
#include "tarjanmod.h"
#include "dualmod.h"

extern void tamassia_draw(Sgraph g, int visitype, int face, int verbose);
extern void tamassia_e_draw(Sgraph g, int face, int verbose);
extern void tamassia_s_draw(Sgraph g, int face, int verbose);
extern int  test_strong_st_numbering(Sgraph g);

/****************************************************************************/
/*This function is the call-function for the Tamassia-algorithm. It combines*/
/*the different parts of the algorithm and returns 1, if all went right     */
/*                                             and 0, if not.               */
/****************************************************************************/

Global	void	call_tamassia_w_planar_layout (Sgraph_proc_info info)
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
g = info->sgraph;

if(test_conditions_for_layout_drawing(g) == 1)
    	{
        /*message("Creating w-visibility \n");*/

	if(greedy)greedy = TAMAS;
	init_node_and_edges(g,face,greedy,stedge);
	/* create structures , st-number the graph */

	make_dual_graph(g);
	/* compute level(g), faces(g),create dual nodes and edges,
	   compute level(dual_g);*/

	tamassia_draw(g,0,face,verbose);
	/* compute x,y,dx,dy for nodes and edges,*/

	tarjan_set(g,x_gridsize,y_gridsize,node_height,verbose);
	/*   set to graphed-structure */

	free_node_and_edges(g);

	if(betterpoly_line)polyline(g,BETTER,x_gridsize,y_gridsize,node_height);
	else	{
		if(poly_line)polyline(g,CENTER,x_gridsize,y_gridsize,node_height);
		}

    	info->recompute = 1;
    	info->repaint   = 1;
    	info->recenter  = 1;
    	}
}

Global	void	call_tamassia_e_planar_layout (Sgraph_proc_info info)
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
g = info->sgraph;

if(test_conditions_for_layout_drawing(g) == 1)
    	{
        /*message("Creating e-visibility \n");*/

	if(greedy)greedy = TAMAS;
	init_node_and_edges(g,face,greedy,stedge);
	/* create structures , st-number the graph */

	make_dual_graph(g);
	/* compute level(g), faces(g),create dual nodes and edges,
	   compute level(dual_g);*/

	tamassia_draw(g,1,face,verbose);
	/* compute x,y,dx,dy for nodes and edges,*/

	tamassia_e_draw(g,face,verbose);
	/* extend segments for nodes*/

	tarjan_set(g,x_gridsize,y_gridsize,node_height,verbose);
	/*   set to graphed-structure */

	free_node_and_edges(g);

	if(betterpoly_line)polyline(g,BETTER,x_gridsize,y_gridsize,node_height);
	else	{
		if(poly_line)polyline(g,CENTER,x_gridsize,y_gridsize,node_height);
		}

    	info->recompute = 1;
    	info->repaint   = 1;
    	info->recenter  = 1;
    	}
}

Global	void	call_tamassia_s_planar_layout (Sgraph_proc_info info)
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
g = info->sgraph;

if(test_conditions_for_layout_drawing(g) == 1)
    	{
	if(greedy)greedy = TAMAS;
	init_node_and_edges(g,face,greedy,stedge);
	/* create structures , st-number the graph */

	make_dual_graph(g);
	/* compute level(g), faces(g),create dual nodes and edges,
	   compute level(dual_g);*/

	if(test_strong_st_numbering(g) >= 1)
		{
        	/*message("Creating s-visibility \n");*/

		tamassia_draw(g,1,face,verbose);
		/* compute x,y,dx,dy for nodes and edges,*/

		tamassia_s_draw(g,face,verbose);
		/* extend segments for nodes*/
		}
	else	{
		message("Graph is not strong-st-numbered\n");
		message("Creating w-visibility\n");
		tamassia_draw(g,0,face,verbose);
		/* compute x,y,dx,dy for nodes and edges,*/
		}

	tarjan_set(g,x_gridsize,y_gridsize,node_height,verbose);
	/*   set to graphed-structure */

	if(betterpoly_line)polyline(g,BETTER,x_gridsize,y_gridsize,node_height);
	else	{
		if(poly_line)polyline(g,CENTER,x_gridsize,y_gridsize,node_height);
		}
	free_node_and_edges(g);

    	info->recompute = 1;
    	info->repaint   = 1;
    	info->recenter  = 1;
    	}
}



void menu_tamassia_w_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tamassia_w_planar_layout, NULL);
}

void menu_tamassia_e_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tamassia_e_planar_layout, NULL);
}

void menu_tamassia_s_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tamassia_s_planar_layout, NULL);
}




/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: TOMASSIACALL.C                    ** */
/* ************************************************************************** */

