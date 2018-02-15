/***************************************************************/
/*                                                             */
/*  filename:  po_delaunay.c                                   */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    creating a delaunay triangulation for simple polygons    */
/*                                                             */
/*  imports:                                                   */
/*    void plan_delaunay();                                    */
/*    void remove_outerpolygon();                              */
/*                                                             */
/*  exports:                                                   */
/*    void polygontriangulation_delaunay();                    */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      include section                        */
/*                                                             */
/***************************************************************/

#include <std.h>
#include <sgraph.h>
#include "polygon_utility.h"
#include "pl_delaunay.h"


/***************************************************************/
/*                                                             */
/* this procedure creates a delaunay triangulation for simple  */
/* polygons. it uses the algorithm for normale planar graphs   */ 
/*                                                             */
/***************************************************************/

void polygontriangulation_delaunay(Sgraph inputgraph)
{
  /* creating a delaunay triangulation for a planar graph */
  plan_delaunay(inputgraph);

  /* deleting all edges that lie not inside the simple polygon */
  remove_outerpolygon(inputgraph);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                     end of po_delaunay.c                    */
/*                                                             */
/***************************************************************/
