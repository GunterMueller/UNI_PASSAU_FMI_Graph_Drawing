/***************************************************************/
/*                                                             */
/*  filename:  menu_polygon.c                                  */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*   this file contains all menudeclarations for the           */
/*   polygontriangulation menu.                                */
/*                                                             */
/*  imports:                                                   */
/*   void polygontriangulation_info();                         */
/*   int simplepolygon_test();                                 */
/*   void polygontriangulation_nlogn();                        */
/*   double random_polygontriangulation_uniform();             */
/*   double standarddynamic_polygontriangulation();            */
/*   int modifieddynamic_polygontriangulation();               */
/*   int polygontriangulation_maxmaxdegree();                  */
/*   void polygontriangulation_delaunay();                     */
/*   int polygontriangulation_bushy();                         */
/*   int polygontriangulation_minmaxdegree();                  */
/*                                                             */
/*  exports:                                                   */
/*    char *menu_polytri_balancedcircumcircle();               */
/*    char *menu_polytri_balancedlength();                     */
/*    char *menu_polytri_balancedsurface();                    */
/*    char *menu_polytri_balancedangle();                      */
/*    char *menu_polytri_minlength();                          */
/*    char *menu_polytri_maxlength();                          */
/*    char *menu_polytri_maxminlength();                       */
/*    char *menu_polytri_minmaxlength();                       */
/*    char *menu_polytri_maxmincircumcircle();                 */
/*    char *menu_polytri_minmaxcircumcircle();                 */
/*    char *menu_polytri_maxminsurface();                      */
/*    char *menu_polytri_minmaxsurface();                      */
/*    char *menu_polytri_maxminangle();                        */
/*    char *menu_polytri_minmaxangle();                        */
/*    char *menu_polytri_thin();                               */
/*    char *menu_polytri_bushy();                              */
/*    char *menu_polytri_minmaxdegree();                       */
/*    char *menu_polytri_maxmaxdegree();                       */
/*    char *menu_polytri_minmaxeccentricity();                 */
/*    char *menu_polytri_maxmaxeccentricity();                 */
/*    char *menu_polytri_delaunay();                           */
/*    char *menu_polytri_nlogn();                              */
/*    char *menu_polytri_random();                             */
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
#include <sys/types.h>
#include <sys/times.h>
#include <math.h>
#include "globaldefinitions.h"
#include "po_random.h"
#include "po_dynamic.h"
#include "po_minmaxdegree.h"
#include "po_delaunay.h"
#include "po_maxmaxdegree.h"
#include "po_bushy.h"
#include "po_sweep.h"
#include "polygon_utility.h"




/***************************************************************/
/*                                                             */
/*       procedures for triangulating a simple polygon         */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*        minimal edgelength with dynamic programming          */
/***************************************************************/

void call_polytri_minlength(Sgraph_proc_info info)
{
  double minlen;

  if (simplepolygon_test(info->sgraph))
  {
    minlen=standarddynamic_polygontriangulation(info->sgraph,minlength);
    message("** minimal length: (%f) **\n",minlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*        maximal edgelength with dynamic programming          */
/***************************************************************/

void call_polytri_maxlength(Sgraph_proc_info info)
{
  double maxlen;

  if (simplepolygon_test(info->sgraph))
  {
    maxlen=standarddynamic_polygontriangulation(info->sgraph,maxlength);
    message("** maximal length: (%f) **\n",maxlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*        min max edgelength with dynamic programming          */
/***************************************************************/

void call_polytri_minmaxlength(Sgraph_proc_info info)
{
  double minmaxlen;

  if (simplepolygon_test(info->sgraph))
  {
    minmaxlen=standarddynamic_polygontriangulation(info->sgraph,minmaxlength);
    message("** minmax length: (%f) **\n",minmaxlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*        max min edgelength with dynamic programming          */
/***************************************************************/

void call_polytri_maxminlength(Sgraph_proc_info info)
{
  double maxminlen;



  if (simplepolygon_test(info->sgraph))
  {
    maxminlen=standarddynamic_polygontriangulation(info->sgraph,maxminlength);
    message("** maxmin length: (%f) **\n",maxminlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*       balanced edgelength with dynamic programming          */
/***************************************************************/

void call_polytri_balancedlength(Sgraph_proc_info info)
{
  double balancedlen;

  if (simplepolygon_test(info->sgraph))
  {
    balancedlen=standarddynamic_polygontriangulation(info->sgraph,balancedlength);
    message("** balanced length: (%f) **\n",balancedlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*        min max circumcircle with dynamic programming        */
/***************************************************************/

void call_polytri_minmaxcircumcircle(Sgraph_proc_info info)
{
  double minmaxlen;

  if (simplepolygon_test(info->sgraph))
  {
    minmaxlen=standarddynamic_polygontriangulation(info->sgraph,minmaxcircumcircle);
    message("** minmax circumcircle: (%f) **\n",minmaxlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*      max min edgecircumcircle with dynamic programming      */
/***************************************************************/

void call_polytri_maxmincircumcircle(Sgraph_proc_info info)
{
  double maxminlen;

  if (simplepolygon_test(info->sgraph))
  {
    maxminlen=standarddynamic_polygontriangulation(info->sgraph,maxmincircumcircle);
    message("** maxmin circumcircle: (%f) **\n",maxminlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*       balanced circumcircle with dynamic programming        */
/***************************************************************/

void call_polytri_balancedcircumcircle(Sgraph_proc_info info)
{
  double balancedlen;

  if (simplepolygon_test(info->sgraph))
  {
    balancedlen=standarddynamic_polygontriangulation(info->sgraph,balancedcircumcircle);
    message("** balanced circumcircle: (%f) **\n",balancedlen);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*       max min trianglesurface with dynamic programming      */
/***************************************************************/

void call_polytri_maxminsurface(Sgraph_proc_info info)
{
  double maxminsur;

  if (simplepolygon_test(info->sgraph))
  {
    maxminsur=standarddynamic_polygontriangulation(info->sgraph,maxminsurface);
    message("** maxmin surface: (%f) **\n",maxminsur);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*       min max trianglesurface with dynamic programming      */
/***************************************************************/

void call_polytri_minmaxsurface(Sgraph_proc_info info)
{
  double minmaxsur;

  if (simplepolygon_test(info->sgraph))
  {
    minmaxsur=standarddynamic_polygontriangulation(info->sgraph,minmaxsurface);
    message("** minmax surface: (%f) **\n",minmaxsur);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*      balanced trianglesurface with dynamic programming      */
/***************************************************************/

void call_polytri_balancedsurface(Sgraph_proc_info info)
{
  double balancedsur;

  if (simplepolygon_test(info->sgraph))
  {
    balancedsur=standarddynamic_polygontriangulation(info->sgraph,balancedsurface);
    message("** balanced surface: (%f) **\n",balancedsur);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*            max min angle with dynamic programming           */
/***************************************************************/

void call_polytri_maxminangle(Sgraph_proc_info info)
{
  double maxminangl;

  if (simplepolygon_test(info->sgraph))
  {
    maxminangl=standarddynamic_polygontriangulation(info->sgraph,maxminangle);
    message("** maxmin angle: (%f) **\n",maxminangl*90.0/acos(1));
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*            min max angle with dynamic programming           */
/***************************************************************/

void call_polytri_minmaxangle(Sgraph_proc_info info)
{
  double minmaxangl;

  if (simplepolygon_test(info->sgraph))
  {
    minmaxangl=standarddynamic_polygontriangulation(info->sgraph,minmaxangle);
    message("** minmax angle: (%f) **\n",minmaxangl*90.0/acos(1));
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*           balanced angle with dynamic programming           */
/***************************************************************/

void call_polytri_balancedangle(Sgraph_proc_info info)
{
  double balancedangl;

  if (simplepolygon_test(info->sgraph))
  {
    balancedangl=standarddynamic_polygontriangulation(info->sgraph,balancedangle);
    message("** balanced angle: (%f) **\n",balancedangl*90.0*acos(1));
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                 thin with dynamic programming               */
/***************************************************************/

void call_polytri_thin(Sgraph_proc_info info)
{
  double thin_;

  if (simplepolygon_test(info->sgraph))
  {
    thin_=standarddynamic_polygontriangulation(info->sgraph,thin);
    message("** thin (tips: %.0f) **\n",thin_+1.0);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                   bushy - triangulation                     */
/***************************************************************/

void call_polytri_bushy(Sgraph_proc_info info)
{
  int bus;

  if (simplepolygon_test(info->sgraph))
  {
    bus=polygontriangulation_bushy(info->sgraph);
    message("** bushy (tips: %d) **\n",bus);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*               maxmax degree - triangulation                 */
/***************************************************************/

void call_polytri_maxmaxdegree(Sgraph_proc_info info)
{
  int maxmaxdeg;

  if (simplepolygon_test(info->sgraph))
  {
    maxmaxdeg=polygontriangulation_maxmaxdegree(info->sgraph);
    message("** maxmax degree: (%d) **\n",maxmaxdeg);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*            minmax degree with dynamic programming           */
/***************************************************************/

void call_polytri_minmaxdegree(Sgraph_proc_info info)
{
  int minmaxdeg;

  if (simplepolygon_test(info->sgraph))
  {
    minmaxdeg=polygontriangulation_minmaxdegree(info->sgraph);
    message("** minmax degree: (%d) **\n",minmaxdeg);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*        minmax eccentricity with dynamic programming         */
/***************************************************************/

void call_polytri_minmaxeccentricity(Sgraph_proc_info info)
{
  int minmaxecc;

  if (simplepolygon_test(info->sgraph))
  {
    minmaxecc=modifieddynamic_polygontriangulation(info->sgraph,minmaxexz);
    message("** minmax eccentricity: (%d) **\n",minmaxecc);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*        maxmax eccentricity with dynamic programming         */
/***************************************************************/

void call_polytri_maxmaxeccentricity(Sgraph_proc_info info)
{
  int maxmaxecc;

  if (simplepolygon_test(info->sgraph))
  {
    maxmaxecc=modifieddynamic_polygontriangulation(info->sgraph,maxmaxexz);
    message("** maxmax eccentricity: (%d) **\n",maxmaxecc);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                   delaunay triangulation                    */
/***************************************************************/

void call_polytri_delaunay(Sgraph_proc_info info)
{


  if (simplepolygon_test(info->sgraph))
  {
    polygontriangulation_delaunay(info->sgraph);
    message("** delaunay **\n");
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*               triangulation in O(n log n)                   */
/***************************************************************/

void call_polytri_nlogn(Sgraph_proc_info info)
{

  if (simplepolygon_test(info->sgraph))
  {
    polygontriangulation_nlogn(info->sgraph);
    message("** sweep **\n");
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                random triangulation uniform                 */
/***************************************************************/

void call_polytri_random(Sgraph_proc_info info)
{
  double trnumber;


  if (simplepolygon_test(info->sgraph))
  {
    trnumber=random_polygontriangulation_uniform(info->sgraph);
    message("** random (1 of %.0f) **\n",trnumber);
    polygontriangulation_info(info->sgraph);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*              menu callback procedures for the               */
/*              polygon triangulation menu                     */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_minlength               */
/***************************************************************/

void menu_polytri_minlength(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_minlength, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_maxlength               */
/***************************************************************/

void menu_polytri_maxlength(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_maxlength, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_maxminlength            */
/***************************************************************/

void menu_polytri_maxminlength(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_maxminlength, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_minmaxlength            */
/***************************************************************/

void menu_polytri_minmaxlength(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_minmaxlength, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_balancedlength          */
/***************************************************************/

void menu_polytri_balancedlength(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_balancedlength, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_maxmincircumcircle      */
/***************************************************************/

void menu_polytri_maxmincircumcircle(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_maxmincircumcircle, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_minmaxcircumcircle      */
/***************************************************************/

void menu_polytri_minmaxcircumcircle(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_minmaxcircumcircle, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_balancedcircumcircle    */
/***************************************************************/

void menu_polytri_balancedcircumcircle(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_balancedcircumcircle, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_maxminsurface           */
/***************************************************************/

void menu_polytri_maxminsurface(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_maxminsurface, NULL);
}

/***************************************************************/
/*     Menuentry: polygontrinagulation_minmaxsurface           */
/***************************************************************/

void menu_polytri_minmaxsurface(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_minmaxsurface, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_balancedsurface         */
/***************************************************************/

void menu_polytri_balancedsurface(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_balancedsurface, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_maxminangle             */
/***************************************************************/

void menu_polytri_maxminangle(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_maxminangle, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_minmaxangle             */
/***************************************************************/

void menu_polytri_minmaxangle(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_minmaxangle, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_balancedangle           */
/***************************************************************/

void menu_polytri_balancedangle(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_balancedangle, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_thin                    */
/***************************************************************/

void menu_polytri_thin(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_thin, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_bushy                   */
/***************************************************************/

void menu_polytri_bushy(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_bushy, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_minmaxdegree            */
/***************************************************************/

void menu_polytri_minmaxdegree(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_minmaxdegree, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_maxmaxdegree            */
/***************************************************************/

void menu_polytri_maxmaxdegree(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_maxmaxdegree, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_minmaxeccentricity      */
/***************************************************************/

void menu_polytri_minmaxeccentricity(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_minmaxeccentricity, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_maxmaxeccentricity      */
/***************************************************************/

void menu_polytri_maxmaxeccentricity(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_maxmaxeccentricity, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_nlogn                   */
/***************************************************************/

void menu_polytri_nlogn(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_nlogn, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_delaunay                */
/***************************************************************/

void menu_polytri_delaunay(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_delaunay, NULL);
}
/***************************************************************/


/***************************************************************/
/*     Menuentry: polygontrinagulation_random                  */
/***************************************************************/

void menu_polytri_random(Menu menu, Menu_item menu_item)
{
  call_sgraph_proc(call_polytri_random, NULL);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of poly_mnu.c                     */
/*                                                             */
/***************************************************************/
