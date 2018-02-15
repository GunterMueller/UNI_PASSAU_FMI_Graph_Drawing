
/* ************************************************************************** */
/* **                                                                      ** */
/* ** Filename: CYLINDRICCALL.C                                            ** */
/* **                                                                      ** */
/* ** ******************************************************************** ** */
/* **                                                                      ** */
/* ** Description: This module provides the start-functions for producing  ** */
/* **              a planar drawing on a cylinder                          ** */
/* **                                                                      ** */
/* ** Date: 18.5.1994                                                      ** */
/* **                                                                      ** */
/* ************************************************************************** */
/*                                                                            */

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "algorithms.h"

#include "visibility_layout_export.h"
#include "tarjanmod.h"

extern void block_cut(Sgraph g);
extern int  caterpillar(Sgraph g);
extern void cylindric_draw(Sgraph g, int verbose);
extern void cylindric_set(Sgraph g, int x_gridsize, int y_gridsize, int node_height, int verbose);
extern void free_cyl_node_and_edges(Sgraph g);


/*   der Polylinetyp  */
#define CENTER	0
#define BETTER	1

static int test_conditions_for_cylinder_layout_drawing(Sgraph g)
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
    }

    return((int)condition);
}

/*                                                                            */
/* ************************************************************************** */
/* This function is the call-function for the Cylindric-algorithm. It combines*/
/* the different parts of the algorithm and returns 1, if all went right      */
/*                                              and 0, if not.                */
/* ************************************************************************** */
/*                                                                            */

Global	void		call_cylinder_planar_layout (Sgraph_proc_info info)
{
int	x_gridsize 	= tarjan_settings.horizontal_distance,
	y_gridsize 	= tarjan_settings.vertical_distance,
	node_height 	= tarjan_settings.vertical_height,
	poly_line 	= tarjan_settings.polyline,
	betterpoly_line = tarjan_settings.betterpolyline,
	verbose		= tarjan_settings.verbose;
Sgraph  g;
g = info->sgraph;

if(test_conditions_for_cylinder_layout_drawing(g) == 1)
    	{
        /*message("Creating block-cutpoint-tree \n");*/

	block_cut(g);
	/* compute blocks and cuts of g*/

	if(caterpillar(g))
	/* transform blocks to little graphs*/

		{
		/*message("Now creating cylindric layout\n");*/
		cylindric_draw(g,verbose);
		/* compute x,y,dx,dy for nodes and edges,*/

		cylindric_set(g,x_gridsize,y_gridsize,node_height,verbose);
		/*   set to graphed-structure */

		free_node_and_edges(g);
	
		if(betterpoly_line)polyline(g,BETTER,x_gridsize,y_gridsize,node_height);
		else	{
			if(poly_line)polyline(g,CENTER,x_gridsize,y_gridsize,node_height);
			}
		}
	else 	{
		message("Sorry, block-cutpoint-tree is no caterpillar !\n");				 		free_cyl_node_and_edges(g);
		}
    	info->recompute = 1;
    	info->repaint   = 1;
    	info->recenter  = 1;
	
        return;
    	}
}


void menu_cylinder_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_cylinder_planar_layout, NULL);
}



/*                                                                            */
/* ************************************************************************** */
/* **                       END OF FILE: CYLINDRICCALL.C                   ** */
/* ************************************************************************** */

