/***************************************************************/
/*                                                             */
/*  filename:  pl_maxmaxdegree.c                               */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implementation of an esay         */
/*    algorithm for constructin a triangulation of a planar    */
/*    graph with a maximal maximal nodedegree                  */
/*                                                             */
/*  imports:                                                   */
/*   Sedge make_a_triangulation_edge();                        */
/*   Snode *create_an_array_for_the_nodes();                   */
/*   int degree_of_node();                                     */
/*   int test_for_an_intersection();                           */
/*   int **allocating_a_2_dimensional_array_of_typ_int();      */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_2_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*                                                             */
/*  exports:                                                   */
/*    int plan_maxmaxdegree();                                 */
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
#include "miscmath_utility.h"
#include "globaldefinitions.h"
#include "pl_sweep.h"

/***************************************************************/
/*                                                             */
/*       generating a (n x n) - matrix with the                */
/*       visibilityinformation of the planar graph             */  
/*                                                             */
/***************************************************************/

void create_planargraphvisibilityinformation(int **V, Snode *ND, int n)
{
  int i,j,k,cr;      /* help-variables */
  Sedge edge;        /* help-variable  */    

  for (i=0;i<n;i++)
  {
    for (j=i;j<n;j++)
    {
      V[i][j]=0;
      if (i!=j)
      {
        V[i][j]=1;
        for (k=0;k<n;k++)
        {
          cr=test_for_an_intersection(ND[k]->x,ND[k]->y,
                                      ND[k]->x,ND[k]->y,
                                      ND[i]->x,ND[i]->y,
                                      ND[j]->x,ND[j]->y);
          if (cr==2) V[i][j]=0;

          for_edgelist(ND[k],edge)  if (unique_edge(edge))
          {
            cr=test_for_an_intersection(edge->snode->x,edge->snode->y,
                                        edge->tnode->x,edge->tnode->y,
                                        ND[i]->x,ND[i]->y,
                                        ND[j]->x,ND[j]->y);
            if (cr>1) V[i][j]=0;
          }
          end_for_edgelist(ND[k],edge);
          if (V[i][j]==0) k=n;
        }
      }
      V[j][i]=V[i][j];
    }
  }
}
/***************************************************************/




/***************************************************************/
/*                                                             */
/*       triangulating a planar graph with max max degree      */
/*                                                             */
/***************************************************************/

void plan_maxmaxdegree(Sgraph inputgraph)
{
  int i,j,degree;     /* help-variables                         */
  Snode node;         /* help-node                              */
  int maxdegree;      /* maximal degree of a node               */
  int maxdegree_node; /* number of the node with maximal degree */
  int **V;
  Snode *ND;
  int n;

  /* allocating dataarrays */
  ND=create_an_array_for_the_nodes(inputgraph,&n);
  V=allocating_a_2_dimensional_array_of_typ_int(n);

  /* creating the visibilityinformation */
  create_planargraphvisibilityinformation(V,ND,n);

  i=0;
  maxdegree=0;
  maxdegree_node=0;
  for_all_nodes(inputgraph,node)
  {
    /* calculating the degree of each node in the inputgraph */
    degree=degree_of_node(node);

    /* adding the posible increase of the degree for each node */
    for(j=0;j<n;j++)
      if (V[i][j]==1) degree++;

    /* hold the best node */
    if (degree>maxdegree)
    {
      maxdegree=degree;
      maxdegree_node=i;
    }

    i++;
  }
  end_for_all_nodes(inputgraph,node);

  for(j=0;j<n;j++)
    if (V[maxdegree_node][j]==1)
      make_a_triangulation_edge(ND[maxdegree_node],ND[j]);

  /* freeing dataarrays */
  freeing_a_2_dimensional_array_of_typ_int(V,n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);

  /* triangulating the rest */
  plan_sweep(inputgraph);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                   end of pl_maxmaxdegree.c                  */
/*                                                             */
/***************************************************************/
