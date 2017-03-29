/***************************************************************/
/*                                                             */
/*  filename:  po_bushy.c                                      */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implementation of an iterativ     */
/*    algorithm for creating a bushy triangulation of a        */
/*    simple polygon                                           */
/*                                                             */
/*  imports:                                                   */
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge make_a_triangulation_edge();                        */
/*   Sedge make_a_prescribed_edge();                           */
/*   int left_or_right_turn();                                 */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   Sedge *allocating_a_1_dimensional_array_of_typ_Sedge();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*   void freeing_a_1_dimensional_array_of_typ_Sedge();        */
/*   void remove_outerpolygon();                               */
/*   void getting_the_polygonnodes();                          */
/*                                                             */
/*  exports:                                                   */
/*    int polygontriangulation_bushy();                        */
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
#include "miscmath_utility.h"
#include "misc_utility.h"
#include "globaldefinitions.h"
#include "memory_allocation.h"
#include "polygon_utility.h"
#include "pl_sweep.h"



/***************************************************************/
/*                                                             */
/*        deleting all edges in the current graph              */
/*                                                             */
/***************************************************************/

void delete_all_edges(Sgraph inputgraph)
{
  int i,deledgecount;       /* help-variables               */
  Snode node;               /* help-node                    */
  Sedge edge;               /* help-edge                    */
  Sedge *DelEdge;           /* edge-array for deleting      */
  int n;                    /* number of nodes in the graph */

  /* allocating dataarray */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  deledgecount=0;
  DelEdge=allocating_a_1_dimensional_array_of_typ_Sedge(n*3);

  /* store all edges in an array */
  for_all_nodes(inputgraph,node)
  {
    for_edgelist(node,edge)
    {
      if (unique_edge(edge))
      {
        DelEdge[deledgecount++]=edge;
      }
    }
    end_for_edgelist(node,edge);
  }
  end_for_all_nodes(inputgraph,node);

  /* removing the edges stored in the array */
  for (i=0;i<deledgecount;i++)
    remove_edge(DelEdge[i]);

  /* freeing the dataarray */
  freeing_a_1_dimensional_array_of_typ_Sedge(DelEdge,n*3);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*       generating the border of the simple polygon,          */
/*       defined by the n nodes                                */
/*                                                             */
/***************************************************************/

void make_around_polygon(Snode *ND, int n)
{
  int i;

  for (i=0;i<n;i++)
    make_a_prescribed_edge(ND[i],ND[(i+1) % n]);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   returns 1, when the polygon-node <n2> is an ear,          */
/*   otherwise the function returns 0.                         */
/*                                                             */
/***************************************************************/

int CheckEar(Snode n1, Snode n2, Snode n3)
{
  int check;
  Sedge eg;

  check=0;
  if (left_or_right_turn(n1->x,n1->y,
                         n2->x,n2->y,
                         n3->x,n3->y)<0)
  {
    check=1;
    for_edgelist(n2,eg)
    {
      if (left_or_right_turn(n1->x,n1->y,
                             n3->x,n3->y,
                             eg->tnode->x,eg->tnode->y)>0)
        check=0;
    }
    end_for_edgelist(n2,eg);
  }
  return(check);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*     creating a bushy-triangulation of a simple polygon      */
/*                                                             */
/***************************************************************/

int polygontriangulation_bushy(Sgraph inputgraph)
{
  int bus,i,j;    /* help-variables               */
  int *Ohr;       /* help-array                   */
  Snode *ND;      /* array for the nodes          */
  int n;          /* number of nodes in the graph */


  /* creating an initial-triangulation */
  plan_sweep(inputgraph);
  remove_outerpolygon(inputgraph);

  /* allocating the dataarrays */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
  getting_the_polygonnodes(inputgraph,ND,n);

  Ohr=allocating_a_1_dimensional_array_of_typ_int(n);

  /* deciding, wether a node i is an ear or not */
  for (i=0;i<n;i++)
    Ohr[i]=CheckEar(ND[(i-1+n) % n],ND[i],ND[(i+1) % n]);


  /* deleting all edges of the polygon */
  delete_all_edges(inputgraph);


  /* connecting every second ear */
  i=0;
  while (i<n)
  {
     if ((Ohr[i]==0)&&(Ohr[(i+1) % n]==1))
       break;
     i++;
  }
  if (i==n)
  {
    i=0;
    Ohr[i]=0;
  }

  bus=0;
  for(j=0;j<n;j++)
    if ((Ohr[(i+j)%n]==0)&&(Ohr[(i+1+j) % n]==1))
    {
      make_a_triangulation_edge(ND[(i+j)%n],ND[(i+j+2)%n]);
      bus=bus+1;
      Ohr[(i+j+1)%n]=0;
      Ohr[(i+j+2)%n]=0;
    }

  /* draw the edges of the polygon */
  make_around_polygon(ND,n);


  /* freeing dataarrays */
  freeing_a_1_dimensional_array_of_typ_int(Ohr,n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);


  /* triangulating the rest */

  plan_sweep(inputgraph);

  remove_outerpolygon(inputgraph);


  return(bus);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of po_bushy.c                     */
/*                                                             */
/***************************************************************/
