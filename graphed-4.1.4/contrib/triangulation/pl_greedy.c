/***************************************************************/
/*                                                             */
/*  filename:  pl_greedy.c                                     */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains an greedy-algorithm for the           */
/*    minimal edgelength triangulation of a planar graph       */
/*                                                             */
/*  imports:                                                   */
/*   Sedge make_a_triangulation_edge();                        */
/*   Snode *create_an_array_for_the_nodes();                   */
/*   double edgelength();                                      */
/*   int test_for_an_intersection();                           */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*                                                             */
/*  exports:                                                   */
/*    void plan_greedy_minlength();                            */
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


/***************************************************************/
/*                                                             */
/*                      local variables                        */
/*                                                             */
/***************************************************************/

Local Snode *ND;


/***************************************************************/
/*               merging two sorted arrays                     */
/***************************************************************/

Local void merge_edgearray(int *A1, int *A2, int a, int m, int *B1, int *B2, int b, int n, int *C1, int *C2, int c)
{
  int i=0,j=0,k=0;

  if (m>0) 
  {
    while ((i<m) && (j<n))
    {
      if (edgelength(ND[A1[a+i]]->x,ND[A1[a+i]]->y,
                     ND[A2[a+i]]->x,ND[A2[a+i]]->y)
          <
          edgelength(ND[B1[b+j]]->x,ND[B1[b+j]]->y,
                     ND[B2[b+j]]->x,ND[B2[b+j]]->y))
      {
        C1[c+k]=A1[a+i];
        C2[c+k++]=A2[a+i++];     
      }
      else
      {
        C1[c+k]=B1[b+j];
        C2[c+k++]=B2[b+j++];
      }
    }
    
    while (i<m) 
    {
      C1[c+k]=A1[a+i];
      C2[c+k++]=A2[a+i++];
    }
    while (j<n) 
    {
      C1[c+k]=B1[b+j];
      C2[c+k++]=B2[b+j++];
    }  

  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*    sorting the edges from the shortest to the longest one   */
/*    using the mergesort-algorithm                            */
/*                                                             */
/***************************************************************/

Local void mergesort_edgearray(int *r1, int *r2, int n)
{
  int k,L,L1,L2;
  int *t1,*t2;
  t1=allocating_a_1_dimensional_array_of_typ_int(n);
  t2=allocating_a_1_dimensional_array_of_typ_int(n);
  L=1;
  while (L<n)
  {
    k=0;
    do
    {
      L1=L;
      L2=L;
      if (k+L1>n) L1=n-k;
      if (k+L1+L2>n) L2=n-k-L1;
      merge_edgearray(r1,r2,k,L1,r1,r2,k+L1,L2,t1,t2,k);
      k=k+L1+L2;
    }
    while (k<n);
    for(k=0;k<n;k++)
    {
      r1[k]=t1[k];
      r2[k]=t2[k];
    }
    L=L*2;
  }
  freeing_a_1_dimensional_array_of_typ_int(t1,n); 
  freeing_a_1_dimensional_array_of_typ_int(t2,n); 
}
/***************************************************************/


/***************************************************************/
/*     testing, whether the edge vom node <S> to node <T>      */
/*     intersects any of the other edges.                      */
/***************************************************************/

Local int intersectiontest(Sgraph inputgraph, int S, int T)
{
  int cr;
  Snode node;
  Sedge edge;

  for_all_nodes(inputgraph,node)
  {
    for_sourcelist(node,edge)
    {
      cr=test_for_an_intersection(edge->snode->x,edge->snode->y,
                                  edge->tnode->x,edge->tnode->y,
                                  ND[S]->x,ND[S]->y,
                                  ND[T]->x,ND[T]->y);
      if ((cr<=4) && (cr>=2))
        return(0);
    }
    end_for_sourcelist(node,edge);
  }
  end_for_all_nodes(inputgraph,node);

  return(1);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*        generating a greedy minlength triangulation          */
/*                                                             */
/***************************************************************/

void plan_greedy_minlength(Sgraph inputgraph)
{
  int i,j,k;                /* help-variables                */
  int *EdgesS, *EdgesT;     /* arrays for all possible edges */
  int n;

  /* allocating dataarrays */
  ND=create_an_array_for_the_nodes(inputgraph,&n);
  k=n*(n-1)/2;
  EdgesS=allocating_a_1_dimensional_array_of_typ_int(k);
  EdgesT=allocating_a_1_dimensional_array_of_typ_int(k);
  
  /* generating all possible edges */
  k=0;
  for(i=0;i<n;i++)
    for(j=i+1;j<n;j++)
    {
      EdgesS[k]=i;
      EdgesT[k]=j;
      k++;
    }

  k=n*(n-1)/2;

  /* sorting the edges, smallest first */
  mergesort_edgearray(EdgesS,EdgesT,k);

  /* inserting the edges, when possible */
  for(i=0;i<k;i++)
    if (intersectiontest(inputgraph,EdgesS[i],EdgesT[i]))
      make_a_triangulation_edge(ND[EdgesS[i]],ND[EdgesT[i]]);

  /* freeing dataarrays */
  freeing_a_1_dimensional_array_of_typ_int(EdgesS,k);
  freeing_a_1_dimensional_array_of_typ_int(EdgesT,k);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of pl_greedy.c                    */
/*                                                             */
/***************************************************************/
