/**************************************************************************/
/***                                                                    ***/
/*** Filename: OTTENCALL.C                                              ***/
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

Global     void    otten_draw(Sgraph g, int face, int verbose);


/*                                                                            */
/* ************************************************************************** */
/* This function is the call-function for the Otten-algorithm. It combines    */
/* the different parts of the algorithm and returns 1, if all went right      */
/*                                              and 0, if not.                */
/* ************************************************************************** */
/*                                                                            */

Global	void	call_otten_planar_layout (Sgraph_proc_info info)
{
int	x_gridsize = tarjan_settings.horizontal_distance,
	y_gridsize = tarjan_settings.vertical_distance,
	node_height = tarjan_settings.vertical_height,
	face	 = tarjan_settings.largest_face,
	poly_line = tarjan_settings.polyline,
	betterpoly_line = tarjan_settings.betterpolyline,
	verbose		= tarjan_settings.verbose,
	greedy		= tarjan_settings.greedy;
Sgraph  g;
g = info->sgraph;

if(test_conditions_for_layout_drawing(g) == 1)
    	{
        /*message("Creating Otten and van Wijk layout \n");*/

	if((greedy == BEST)&&verbose)message("Option 'Best ST-Edge' ignored !\n");
	init_node_and_edges(g,face,0,greedy);
	/* create structures , st-number the graph */

	make_dual_graph(g);
	/* compute level(g), faces(g),create dual nodes and edges,
	   compute level(dual_g);*/

	otten_draw(g,face,verbose);
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



void menu_otten_planar_layout (Menu menu, Menu_item menu_item)
{
    save_tarjan_settings();
    call_sgraph_proc(call_otten_planar_layout, NULL);
}


/****************************************************************************/
/***                       END OF FILE: OTTENCALL.C                       ***/
/****************************************************************************/

