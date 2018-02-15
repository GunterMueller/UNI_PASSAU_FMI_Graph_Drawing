/*************************************************************************** */
/***                                                                      ** */
/*** Filename: WOODSCALL.C                                                ** */
/***                                                                      ** */
/*** ******************************************************************** ** */
/***                                                                      ** */
/*** Description: This module provides the start-functions for producing  ** */
/***              a planar drawing by using the algorithm of Woods        ** */
/***                                                                      ** */
/*** Date: 18.5.1994                                                      ** */
/***                                                                      ** */
/*************************************************************************** */
/*                                                                           */

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "graphed.h"
#include "algorithms.h"

#include "woods_planar_layout_export.h"

#include "dfsmod.h"
#include "stnummod.h"
#include "woodsmod.h"

#include "xview/xview.h"

/*                                                                           */
/* This function returns 1, if all conditions are satisfied                  */
/*                   and 0, if one condition is hurt (a error message is     */
/*                          printed on the message-window                    */
/*                                                                           */

int test_conditions_for_woods_drawing(Sgraph g)
{
    int condition = 1;

    if(g == empty_sgraph)
    {
        error("Please give graph\n");
        condition = 0;
    } else if(g->nodes == (Snode)NULL)
    {
        error("Graph is empty \n");
        condition = 0;
    }
    else
    {
        if(g->nodes == g->nodes->suc)
        {
            error("Graph contains only one node \n");
            condition = 0;
        }
        else
        {
            if (!test_sgraph_biconnected(g))
            {
                error("Graph is not biconnected \n");
                condition = 0;
            }
            else
            {
                switch(embed(g))
                {
                 case NONPLANAR    : error("Graph is nonplanar \n");
                                     condition = 0;
                                     break;
                 case SELF_LOOP    : error("Graph contains self-loops \n");
                                     condition = 0;
                                     break;
                 case MULTIPLE_EDGE: error("Graph contains multiple edges \n");
                                     condition = 0;
                                     break;
                 case NO_MEM       : error("Not enough memory to compute planar embedding\n");
                                     condition = 0;
                                     break;
                 default           : break;
                }
            }
        }
    }

    return((int)condition);
}


/*                                                                            */
/* ************************************************************************** */
/* This function is the call-function for the Woods-algorithm. It combines    */
/* the different parts of the algorithm and returns 1, if all went right      */
/*                                              and 0, if not.                */
/* ************************************************************************** */
/*                                                                            */

int woods_planar_layout (Sgraph g)
{
    Sedge   firstedge;
    Slist   st_num;
    int     x_gridsize = woods_settings.horizontal_distance,
            y_gridsize = woods_settings.vertical_distance;

    if(test_conditions_for_woods_drawing(g) == 1)
    {
        /* message("Creating woods-drawing \n"); */

        /* prepares the graph and bisects the largest face */
           firstedge = dfs_main(g);

        /* creates a st-numbering for g in st_num */
           st_num = st_number(g,firstedge);

        /* frees the node and edge attributes needed in dfs_main */
           free_dfs_graph_attributes(g); 	 

        /* creates the planar drawing according to the Woods-algorithm */ 
           maxwoods(g,firstedge,st_num,x_gridsize,y_gridsize);

        /* frees the slist, that contains the st-numbering */
           free_st_number(st_num);

        return((int)1);
    }
    else
    {
        /*  message("        Graph cannot be drawn \n"); */
        return((int)0);
    }
}

void	call_woods_planar_layout (Sgraph_proc_info info)
{
    Sgraph  g;
 
    g=info->sgraph;

    woods_planar_layout (g);

    info->recompute = 1;
    info->repaint   = 1;
    info->recenter  = 1;
    
}


void menu_woods_planar_layout (Menu menu, Menu_item menu_item)
{
    save_woods_settings();
    call_sgraph_proc(call_woods_planar_layout, NULL);
}


void menu_woods_planar_layout_settings (Menu menu, Menu_item menu_item)
{
    show_woods_subframe(NULL);
}


/*                                                                           */
/*************************************************************************** */
/***                       END OF FILE: WOODSCALL.C                       ** */
/*************************************************************************** */

