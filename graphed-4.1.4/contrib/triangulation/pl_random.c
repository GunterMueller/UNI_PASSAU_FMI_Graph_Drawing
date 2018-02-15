/***************************************************************/
/*                                                             */
/*  filename:  pl_random.c                                     */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this filge contains the implementation of an algorithm   */
/*    that produces each triangulation of a planar graph with  */
/*    a positiv possiblity                                     */
/*                                                             */
/*  imports:                                                   */
/*           crossing, MakeEdge, createnodearray, allocintarray */
/*           freeSnodearray, freeintarray                      */
/*                                                             */
/*  exports:                                                   */
/*    void plan_random();                                      */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      include section                        */
/*                                                             */
/***************************************************************/

#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>

#include "memory_allocation.h"
#include "misc_utility.h"
#include "planargraph_utility.h"
#include "globaldefinitions.h"
#include "pl_sweep.h"

#include "sgraph/random.h"


/***************************************************************/
/*                                                             */
/*      creating a triangulation of a planar graph by using    */
/*      edgeflips on convex quadrilaterals, where the          */
/*      specified criteria ist opimized at all convex          */
/*      quadrilaterals                                         */
/*                                                             */
/***************************************************************/

void plan_random(Sgraph inputgraph)
{
  Sedge *EdgeArray;          /* array for the triangulationedges */
  int edgecount;             /* number of triangulationedges     */
  int nodecount;             /* number of triangulationnodes     */
  int flipcount;             /* number of edgesflips             */
  Sedge edge,ab,ac,ad,bc,bd; /* help-edges                       */
  Snode node,c,d;            /* help-nodes                       */
  int i,selectededgenumber;  /* help-variables                   */


  /* calculating the number of nodes, the current graph contains */
  nodecount=number_of_nodes_in_the_inputgraph(inputgraph);

  /* creating an initial triangulation */
  plan_sweep(inputgraph); 

  /* get a randomnumber for the number of edgeflips */
  flipcount=random() % (nodecount*nodecount);

  
  /* pushing all triangulation edges into the edgearray and count them */
  
  EdgeArray=allocating_a_1_dimensional_array_of_typ_Sedge(nodecount*3);
  edgecount=0;
  for_all_nodes(inputgraph,node)
  {
    for_edgelist(node,edge) 
    {
      if (unique_edge(edge))
      {
        if (edge->label!=NULL)
        {
          if (strcmp(edge->label,TRIANGULATION_EDGELABEL)==0)  
            EdgeArray[edgecount++]=edge;
        }
      }
    }
    end_for_edgelist(node,edge);
  }
  end_for_all_nodes(inputgraph,node);

  if (edgecount>0)
  {
    /* flipping the edges */
    for (i=0;i<flipcount;i++)
    {
      selectededgenumber=random() % edgecount;
      ab=EdgeArray[selectededgenumber];
      if (get_konvex_quadrilateral(ab,&ac,&ad,&bd,&bc,&c,&d))
      {
        remove_edge(ab);
        EdgeArray[selectededgenumber]=make_a_triangulation_edge(c,d);
      }
    }
  }

  /* freeing the datastructure for the stored triangulation edges */
  freeing_a_1_dimensional_array_of_typ_Sedge(EdgeArray,nodecount*3);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of pl_random.c                    */
/*                                                             */
/***************************************************************/
