/***************************************************************/
/*                                                             */
/*  filename:  menu_planargraph.c                              */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*   this file contains all menudeclarations for the           */
/*   planargraphtriangulation menu.                            */
/*                                                             */
/*  imports:                                                   */
/*   void planargraphtriangulation_info();                     */
/*   int planarity_test();                                     */
/*   void plan_delaunay();                                     */
/*   void plan_greedy_minlength();                             */
/*   void plan_edgeflip();                                     */
/*   double plan_maxmaxdegree();                               */
/*   void plan_random();                                       */
/*   void plan_sweep();                                        */
/*                                                             */
/*  exports:                                                   */
/*    char *menu_plantri_delaunay();                           */
/*    char *menu_plantri_minmaxangle();                        */
/*    char *menu_plantri_maxmaxdegree();                       */
/*    char *menu_plantri_sweep();                              */
/*    char *menu_plantri_random();                             */
/*    char *menu_plantri_greedy();                             */
/*    char *menu_plantri_edgeflip_minlength();                 */
/*    char *menu_plantri_edgeflip_minmaxdegree();              */
/*    char *menu_plantri_edgeflip_mostlyequalsurface();        */
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
#include "pl_random.h"
#include "pl_delaunay.h"
#include "pl_minmaxangle.h"
#include "pl_sweep.h"
#include "pl_greedy.h"
#include "pl_edgeflip.h"
#include "pl_maxmaxdegree.h"
#include "planargraph_utility.h"



/***************************************************************/
/*                                                             */
/*        procedures for triangulating a planar graph          */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                 delaunay triangulation                      */
/***************************************************************/

void call_plantri_delaunay(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** delaunay triangulation **\n");
    plan_delaunay(info->sgraph);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*               min max angle triangulation                   */
/***************************************************************/

void call_plantri_minmaxangle(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** minmax angle triangulation **\n");
    plan_minmaxangle(info->sgraph);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*              maxmax degree triangulation                    */
/***************************************************************/

void call_plantri_maxmaxdegree(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** maxmax degree triangulation **\n");
    plan_maxmaxdegree(info->sgraph);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                   sweepline triangulation                   */
/***************************************************************/

void call_plantri_sweep(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** sweepline triangulation **\n");
    plan_sweep(info->sgraph);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                    random triangulation                     */
/***************************************************************/

void call_plantri_random(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** random triangulation **\n");
    plan_random(info->sgraph);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*               greedy triangulation (minlength)              */
/***************************************************************/

void call_plantri_greedy(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** greedy triangulation (minlength) **\n");
    plan_greedy_minlength(info->sgraph);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                     edgeflip minlength                      */
/***************************************************************/

void call_plantri_edgeflip_minlength(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** edgeflip - minlength **\n");
    plan_edgeflip(info->sgraph,ef_minlength);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                    edgeflip minmax degree                   */
/***************************************************************/

void call_plantri_edgeflip_minmaxdegree(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** edgeflip - minmax degree **\n");
    plan_edgeflip(info->sgraph,ef_minmaxdegree);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                edgeflip mostly equal surface                */
/***************************************************************/

void call_plantri_edgeflip_mostlyequalsurface(Sgraph_proc_info info)
{
  if (planarity_test(info->sgraph))
  {
    message("** edgeflip mostly equal surfaces **\n");
    plan_edgeflip(info->sgraph,ef_mostlyequalsurface);
    planargraphtriangulation_info(info->sgraph);
  }
}
/***************************************************************/



/***************************************************************/
/*                                                             */
/*              menu callback procedures for the               */
/*              planar graph triangulation menu                */
/*                                                             */
/***************************************************************/



/***************************************************************/
/*     Menuentry: planargraphtriangulation_delaunay            */
/***************************************************************/

void menu_plantri_delaunay(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_delaunay, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: planargraphtriangulation_minmaxangle         */
/***************************************************************/

void menu_plantri_minmaxangle(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_minmaxangle, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: planargraphtriangulation_maxmaxdegree        */
/***************************************************************/

void menu_plantri_maxmaxdegree(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_maxmaxdegree, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: planargraphtriangulation_sweep               */
/***************************************************************/

void menu_plantri_sweep(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_sweep, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: planargraphtriangulation_random              */
/***************************************************************/

void menu_plantri_random(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_random, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: planargraphtriangulation_greedy              */
/***************************************************************/

void menu_plantri_greedy(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_greedy, NULL);
}
/***************************************************************/


/***************************************************************/
/*    Menuentry: planargraphtriangulation_edgeflip_minlength   */
/***************************************************************/

void menu_plantri_edgeflip_minlength(Menu menu, Menu_item menu_itemenu_item)
{
  call_sgraph_proc(call_plantri_edgeflip_minlength, NULL);
}
/***************************************************************/


/***************************************************************/
/*  Menuentry: planargraphtriangulation_edgeflip_minmaxdegree  */
/***************************************************************/

void menu_plantri_edgeflip_minmaxdegree(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_edgeflip_minmaxdegree, NULL);
}
/***************************************************************/


/***************************************************************/
/* Menuentry: planargraphtriangulation_edgeflip_mostleqsurface */
/***************************************************************/

void menu_plantri_edgeflip_mostlyequalsurface(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_plantri_edgeflip_mostlyequalsurface, NULL);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of plan_mnu.c                     */
/*                                                             */
/***************************************************************/
