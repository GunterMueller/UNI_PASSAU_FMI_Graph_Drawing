/***************************************************************/
/*                                                             */
/*  filename:  triangulation_init.c                            */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file integrates the triangulation algorithm into    */
/*    the tools menu.                                          */
/*                                                             */
/*  imports:                                                   */
/*    all Menus for the algorithms                             */
/*                                                             */
/*  exports:  -                                                */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                      include section                        */  
/***************************************************************/

/* unneccessary within GraphEd, MH 29/3/94
#include <xview/xview.h>
#include <xview/panel.h>
#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
*/




/***************************************************************/
/*     Adding the new submenus graphed's tools_menu            */
/***************************************************************/

{
  Menu PlanarGraph_Triangulation_Menu;
  Menu Polygon_Triangulation_Menu;
  

  PlanarGraph_Triangulation_Menu=graphed_create_pin_menu("planar graph triangulation");

  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "sweep",
                    menu_plantri_sweep);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "delaunay",
                    menu_plantri_delaunay);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "minmax angle",
                    menu_plantri_minmaxangle); 
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "maxmax degree",
                    menu_plantri_maxmaxdegree);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "",
                    menu_separator);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "random",
                    menu_plantri_random);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "greedy",
                    menu_plantri_greedy);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "edgeflip minlength",
                    menu_plantri_edgeflip_minlength);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "edgeflip minmaxdegree",
                    menu_plantri_edgeflip_minmaxdegree);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "edgeflip equalsurface",
                    menu_plantri_edgeflip_mostlyequalsurface);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "",
                    menu_separator);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "create a random planar graph",
                    menu_makegraph_planargraph);
  add_entry_to_menu(PlanarGraph_Triangulation_Menu,
                    "remove all triangultion edges",
                    menu_remove_triangulationedges);
  

  Polygon_Triangulation_Menu=graphed_create_pin_menu("polygon triangulation");

  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "minimal length",
                    menu_polytri_minlength);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "maximal length",
                    menu_polytri_maxlength);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "maxmin length",
                    menu_polytri_maxminlength);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "minmax length",
                    menu_polytri_minmaxlength);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "balanced length",
                    menu_polytri_balancedlength);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "maxmin surface",
                    menu_polytri_maxminsurface);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "minmax surface",
                    menu_polytri_minmaxsurface);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "balanced surface",
                    menu_polytri_balancedsurface);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "maxmin angle",
                    menu_polytri_maxminangle);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "minmax angle",
                    menu_polytri_minmaxangle);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "balanced angle",
                    menu_polytri_balancedangle);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "maxmin circumcircle",
                    menu_polytri_maxmincircumcircle);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "minmax circumcircle",
                    menu_polytri_minmaxcircumcircle);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "balanced circumcircle",
                    menu_polytri_balancedcircumcircle);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "thin",
                    menu_polytri_thin);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "bushy",
                    menu_polytri_bushy);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "minmax degree",
                    menu_polytri_minmaxdegree);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "maxmax degree",
                    menu_polytri_maxmaxdegree);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "minmax eccentricity",
                    menu_polytri_minmaxeccentricity);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "maxmax eccentricity",
                    menu_polytri_maxmaxeccentricity);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "delaunay",
                    menu_polytri_delaunay);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "sweep",
                    menu_polytri_nlogn);
  add_entry_to_menu(Polygon_Triangulation_Menu,                     
                    "random uniform",
                    menu_polytri_random);
  add_entry_to_menu(Polygon_Triangulation_Menu,
                    "",
                    menu_separator);
  add_entry_to_menu(Polygon_Triangulation_Menu,
                    "create a random simple polygon",
                    menu_makegraph_polygon);
  add_entry_to_menu(Polygon_Triangulation_Menu,
                    "remove all triangultion edges",
                    menu_remove_triangulationedges);
  

  add_menu_to_tools_menu("planar graph triangulation",
                        PlanarGraph_Triangulation_Menu);
  add_menu_to_tools_menu("polygon triangulation",
                        Polygon_Triangulation_Menu);

}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*               end of triangulation_init.c                   */  
/*                                                             */
/***************************************************************/
