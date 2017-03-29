/***************************************************************/
/*                                                             */
/*  filename:  po_nlogn.c                                      */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    creating a triangulation for simple polygons in time     */
/*    O(n log n) using the sweep algorithm                     */
/*                                                             */
/*  imports:                                                   */
/*    void plan_sweep();                                       */
/*    void remove_outerpolygon();                              */
/*                                                             */
/*  exports:                                                   */
/*    void polygontriangulation_nlogn();                       */
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
#include "pl_sweep.h"


/***************************************************************/
/*                                                             */
/* this procedure creates a triangulation for simple polygons  */
/* in time O(n log n). it uses the sweep algorithm for normal  */
/* planar graphs                                               */ 
/*                                                             */
/***************************************************************/

void polygontriangulation_nlogn(Sgraph inputgraph)
{
  /* creating a triangulation for a planar graph in O(n log n) */
  plan_sweep(inputgraph);

  /* deleting all edges that lie not inside the simple polygon */
  remove_outerpolygon(inputgraph); 
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      end of po_nlogn.c                      */
/*                                                             */
/***************************************************************/
