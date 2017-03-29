/***************************************************************/
/*                                                             */
/*  filename:  po_maxmaxdegree.c                               */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains a routine for constructing a          */
/*    triangulation of a simple polygon with a maximal         */
/*    maximal degree.                                          */
/*                                                             */
/*  imports:                                                   */
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge make_a_triangulation_edge();                        */
/*   int **allocating_a_2_dimensional_array_of_typ_int();      */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_2_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*   void create_polygonvisibilityinformation();               */
/*   void remove_outerpolygon();                               */
/*   void getting_the_polygonnodes();                          */
/*                                                             */
/*  exports:                                                   */
/*    int polygontriangulation_maxmaxdegree();                 */
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

#include "misc_utility.h"
#include "globaldefinitions.h"
#include "memory_allocation.h"
#include "polygon_utility.h"
#include "pl_sweep.h"




/***************************************************************/
/*                                                             */
/*     triangulating a simple polygon with max max degree      */
/*                                                             */
/***************************************************************/

int polygontriangulation_maxmaxdegree(Sgraph inputgraph)
{
  int maxmaxdeg,best,i,j,jmax; /* help-variables                  */
  int **V;                     /* array for visibilityinformation */
  Snode *ND;                   /* array for the nodes             */
  int n;                       /* number of nodes in the graph    */
  

  /* allocating the dataarrays */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
  getting_the_polygonnodes(inputgraph,ND,n);
  V=allocating_a_2_dimensional_array_of_typ_int(n);

  /* creating the visibility information */
  create_polygonvisibilityinformation(V,ND,n);

  /* searching for the node with possible maximum degree */
  maxmaxdeg=0;
  best=0;
  for (i=0;i<n;i++)
  {
    jmax=0;
    for(j=0;j<n;j++)
      if (V[i][j]) jmax++;
    if (jmax>maxmaxdeg)
    {
      best=i;
      maxmaxdeg=jmax;
    }
  }

  /* generating all edges starting from the node for maxmax degree */
  for (j=2;j<n-1;j++)
    if (V[best][(best+j) % n])
      make_a_triangulation_edge(ND[best],ND[(best+j) % n]);
  
  /* freeing dataarrays */
  freeing_a_2_dimensional_array_of_typ_int(V,n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);

  /* triangulating the rest of the polygon */
  plan_sweep(inputgraph);
  remove_outerpolygon(inputgraph);

  return(maxmaxdeg);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                  end of po_maxmaxdegree.cc                  */
/*                                                             */
/***************************************************************/
