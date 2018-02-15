/**************************************************************************/
/***                                                                    ***/
/*** Filename: TARJANCALL.C                                             ***/
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



/*                                                                            */
/* This function returns 1, if all conditions are satisfied                   */
/*                   and 0, if one condition is hurt (a error message is      */
/*                          printed on the message-window                     */
/*                                                                            */

Global int test_conditions_for_layout_drawing(Sgraph g)
{
    int condition = 1;
    if(g == empty_sgraph || g->nodes == (Snode)NULL)
    {
        error("graph is empty \n");
        condition = 0;
    }
    else
    {
        if(g->nodes == g->nodes->suc)
        {
            error("graph contains only one node \n");
            condition = 0;
        }
        else
        {
            if (!test_sgraph_biconnected(g))
            {
                error("graph is not biconnected \n");
                condition = 0;
            }
            else
            {
                /*message("Checking planarity \n");*/
                switch(embed(g))
                {
                 case NONPLANAR    : error("graph is nonplanar \n");
                                     condition = 0;
                                     break;
                 case SELF_LOOP    : error("graph contains self-loops \n");
                                     condition = 0;
                                     break;
                 case MULTIPLE_EDGE: error("graph contains multiple edges \n");
                                     condition = 0;
                                     break;
                 case NO_MEM       : error("not enough memory for embed \n");
                                     condition = 0;
                                     break;
		default		   : break;
                }
            }
        }
    }

    return((int)condition);
}


/*                                                                            */
/* ************************************************************************** */
/* This function is the call-function for the Tarjan-algorithm. It combines   */
/* the different parts of the algorithm and returns 1, if all went right      */
/*                                              and 0, if not.                */
/* ************************************************************************** */
/*                                                                            */

Global	void	call_tarjan_planar_layout (Sgraph_proc_info info)
{
int	x_gridsize 	= tarjan_settings.horizontal_distance,
	y_gridsize 	= tarjan_settings.vertical_distance,
	node_height 	= tarjan_settings.vertical_height,
	face	 	= tarjan_settings.largest_face,
	poly_line 	= tarjan_settings.polyline,
	betterpoly_line = tarjan_settings.betterpolyline,
	greedy		= tarjan_settings.greedy,
	verbose		= tarjan_settings.verbose,
	stedge		= tarjan_settings.greedy;
Sgraph  g;
g = info->sgraph;

if(test_conditions_for_layout_drawing(g) == 1)
    	{
        /*message("Creating tarjan-drawing \n");*/

	if(greedy)greedy = TARJ;
	init_node_and_edges(g,face,greedy,stedge);
	/* create structures , st-number the graph */

	make_dual_graph(g);
	/* compute level(g), faces(g),create dual nodes and edges,
	   compute level(dual_g);*/
	
	tarjan_draw(g,face,TARJAN,verbose);
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
	
        return;
    	}
}

Global	void	call_tarjan2_planar_layout (Sgraph_proc_info info)
{
int	x_gridsize 	= tarjan_settings.horizontal_distance,
	y_gridsize 	= tarjan_settings.vertical_distance,
	node_height 	= tarjan_settings.vertical_height,
	face	 	= tarjan_settings.largest_face,
	poly_line 	= tarjan_settings.polyline,
	betterpoly_line = tarjan_settings.betterpolyline,
	greedy		= tarjan_settings.greedy,
	verbose		= tarjan_settings.verbose,
	stedge		= tarjan_settings.greedy;
Sgraph  g;
g = info->sgraph;

if(test_conditions_for_layout_drawing(g) == 1)
    	{
        /*message("Creating tamassia-drawing \n");*/

	if(greedy)greedy = TARJ;
	init_node_and_edges(g,face,greedy,stedge);
	/* create structures , st-number the graph */

	make_dual_graph(g);
	/* compute level(g), faces(g),create dual nodes and edges,
	   compute level(dual_g);*/
	
	tarjan_draw(g,face,TAMASSIA,verbose);
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
	
        return;
    	}
}



void menu_tarjan_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tarjan_planar_layout, NULL);
}

void menu_tarjan2_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_tarjan2_planar_layout, NULL);
}


void menu_tarjan_planar_layout_settings (Menu menu, Menu_item menu_item)
{
    show_tarjan_subframe();
}


/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: TARJANCALL.C                      ** */
/* ************************************************************************** */

