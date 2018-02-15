/***************************************************************/
/*                                                             */
/*  filename:  mkgr_mnu.c                                      */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the menus for creating randomly a     */
/*    planar graph or a simple polygon. there is also a        */
/*    routine to remove all triangulation edges.               */
/*                                                             */
/*  imports:                                                   */
/*   int make_a_random_planar_graph();                         */
/*   int make_a_random_simple_polygon();                       */
/*   void remove_all_triangulation_edges();                    */
/*                                                             */
/*  exports:                                                   */
/*    char *menu_makegraph_polygon();                          */
/*    char *menu_makegraph_planargraph();                      */
/*    char *menu_remove_triangulationedges();                  */
/*    char *menu_separator();                                  */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                        include section                      */
/*                                                             */
/***************************************************************/

#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include "globaldefinitions.h"
#include "makegraph.h"
#include "misc_utility.h"

#define MAX_X_FOR_RANDOMGRAPH 600
#define MAX_Y_FOR_RANDOMGRAPH 400
#define RANDOM_POLYGON_NODES 15
#define RANDOM_PLANARGRAPH_NODES 15
#define RANDOM_PLANARGRAPH_EDGES 5


/***************************************************************/
/*                                                             */
/*            creating randomly a new planar graph             */
/*                                                             */
/***************************************************************/

void call_makegraph_planargraph(Sgraph_proc_info info)
{
  message("creating a planar graph\n");
  message("Be careful, this function may cause some trouble!!!\n");
  make_a_random_planar_graph(info->sgraph,
                             RANDOM_PLANARGRAPH_NODES,
                             RANDOM_PLANARGRAPH_EDGES,
                             MAX_X_FOR_RANDOMGRAPH,
                             MAX_Y_FOR_RANDOMGRAPH);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*          creating randomly a new simple polygon             */
/*                                                             */
/***************************************************************/

void call_makegraph_polygon(Sgraph_proc_info info)
{       
  message("creating a simple polygon\n");
  message("Be careful, this function may cause some trouble!!!\n");

  make_a_random_simple_polygon(info->sgraph,
                               RANDOM_POLYGON_NODES,
                               MAX_X_FOR_RANDOMGRAPH,
                               MAX_Y_FOR_RANDOMGRAPH);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*               removing all triangulationedges               */
/*                                                             */
/***************************************************************/

void call_remove_triangulationedges(Sgraph_proc_info info)
{
  if ((info->sgraph!=nil) && (info->sgraph->nodes!=nil))
    remove_all_triangulation_edges(info->sgraph);
}
/***************************************************************/



/***************************************************************/
/*                                                             */
/*                  menu callback procedures                   */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*     Menuentry: makegraph_polygon                            */
/***************************************************************/

void menu_makegraph_polygon(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_makegraph_polygon, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: makegraph_planargraph                        */
/***************************************************************/

void menu_makegraph_planargraph(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_makegraph_planargraph, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: makegraph_planargraph                        */
/***************************************************************/

void menu_remove_triangulationedges(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_remove_triangulationedges, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: menuseparator                                */
/***************************************************************/

void menu_separator(Menu menu, Menu_item menu_item)
{
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of mkgr_mnu.c                     */
/*                                                             */
/***************************************************************/
