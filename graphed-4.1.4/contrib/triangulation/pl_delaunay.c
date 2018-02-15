/***************************************************************/
/*                                                             */
/*  filename:  pl_delaunay.c                                   */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implementation of an algorithm    */
/*    for generating a delaunay triangulation of a planar      */
/*    graph                                                    */
/*                                                             */
/*  imports:                                                   */
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge make_a_triangulation_edge();                        */
/*   int get_konvex_quadrilateral();                           */
/*   int left_or_right_turn();                                 */
/*   Sedge *allocating_a_1_dimensional_array_of_typ_Sedge();   */
/*   void freeing_a_1_dimensional_array_of_typ_Sedge();        */
/*                                                             */
/*  exports:                                                   */
/*    void plan_delaunay();                                    */
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
#include <string.h>
#include "memory_allocation.h"
#include "misc_utility.h"
#include "miscmath_utility.h"
#include "globaldefinitions.h"
#include "pl_sweep.h"



/***************************************************************/
/*      calculates the value of a  (2 x 2) - determinante      */
/***************************************************************/

double det2x2(int a1, int a2, int b1, int b2)
{
  return((double)((double)a1*(double)b2-(double)b1*(double)a2));
}
/***************************************************************/


/***************************************************************/
/*      calculates the value of a  (3 x 3) - determinante      */
/***************************************************************/

double det3x3(int a1, int a2, int a3, int b1, int b2, int b3, int c1, int c2, int c3)
{   
  return( (double)a1*det2x2(b2,b3,c2,c3)
         -(double)b1*det2x2(a2,a3,c2,c3)
         +(double)c1*det2x2(a2,a3,b2,b3));
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   this function decides, whether a point <d> lies in- or    */
/*   outside of the circle through the points <a>,<b>,<c>      */
/*                                                             */
/*   it returns 1, if <d> lies in the circle                   */
/*              0, if <d> lies on the circle                   */
/*             -1, if <d> lies out of the circle               */
/*                                                             */
/***************************************************************/

int in_circle_test(Snode a, Snode b, Snode c, Snode d)
{
  Snode help;
  int a2,a3,a4,b2,b3,b4,c2,c3,c4,d2,d3,d4;
  double det,a1,b1,c1,d1;
  
  /* change a and b, if (abc) do not form a leftturn */
  if (left_or_right_turn(a->x,a->y,b->x,b->y,c->x,c->y)==1)
  {
    help=a;
    a=b;
    b=help;
  }

  a1=(double)a->x;
  a2=a->y;
  a3=(a1*a1+a2*a2);
  a4=1;
  b1=(double)b->x;
  b2=b->y;
  b3=(b1*b1+b2*b2);
  b4=1;
  c1=(double)(c->x);
  c2=(c->y);
  c3=(c1*c1+c2*c2);
  c4=1;
  d1=(double)(d->x);
  d2=(d->y);
  d3=(d1*d1+d2*d2);
  d4=1;
  det=a1*det3x3(b2,b3,b4,c2,c3,c4,d2,d3,d4)
     -b1*det3x3(a2,a3,a4,c2,c3,c4,d2,d3,d4)
     +c1*det3x3(a2,a3,a4,b2,b3,b4,d2,d3,d4)
     -d1*det3x3(a2,a3,a4,b2,b3,b4,c2,c3,c4);

  /* return, whether d is in abc or not */
  if (det<0.0) return(-1);  /* d is not in abc */
  if (det>0.0) return(1);   /* d is in abc */
  return(0);  /* d is on abc */
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*    this procedure creates a delaunay triangulation for a    */
/*    planar graph. it uses an edgeflip-algorithm.             */ 
/*                                                             */
/***************************************************************/

void plan_delaunay(Sgraph inputgraph)
{
  Sedge *nondelaunaystack;  /* the stack for the non delaunay edges */
  int stackpointer;
  int n;                    /* number of inputnodes */

  Sedge edge,ab,ac,ad,bc,bd;  /* help-variables */
  Snode node,c,d;             /* help-variables */
  
  
  /* generating the initial triangulation */
  plan_sweep(inputgraph); 

  /* calculating the number of nodes, the current graph contains */
  n=number_of_nodes_in_the_inputgraph(inputgraph);

  /* pushing all triangulation edges onto the stack and mark them */
  
  nondelaunaystack=allocating_a_1_dimensional_array_of_typ_Sedge(n*3);
  
  stackpointer=0;
  
  for_all_nodes(inputgraph,node)
  {
    for_edgelist(node,edge) 
    {
      if (unique_edge(edge))
      {
        if (edge->label!=NULL)
        {
          if (strcmp(edge->label,TRIANGULATION_EDGELABEL)==0)
          {
            nondelaunaystack[stackpointer++]=edge;
            set_edgelabel(edge,EDGEMARKER); 
          }
        }
      }
    }
    end_for_edgelist(node,edge);
  }
  end_for_all_nodes(inputgraph,node);

  /* do until there are no more edges on the stack */
  while (stackpointer>0)
  {
    /* pop an edge from the stack and unmark it */
    ab=nondelaunaystack[--stackpointer];
    set_edgelabel(ab,TRIANGULATION_EDGELABEL);

    /* determining the konvex quadrilateral for the edge ab */
    if (get_konvex_quadrilateral(ab,&ac,&ad,&bd,&bc,&c,&d))
    {
      /* is the edge (a,b) local delaunay? */
      if (in_circle_test(ab->snode,ab->tnode,c,d)==1)
      {
        
        /* make a delaunay edgeflip */
        remove_edge(ab);
        ab=make_a_triangulation_edge(c,d);
        

        /* push the edges (a,c), (a,d), (b,c) and (b,d) */
        /* onto the stack, if they are not prescribed   */
        /* and not already on the stack                 */
        if (ac->label!=NULL)
        {
          if (strcmp(ac->label,TRIANGULATION_EDGELABEL)==0)  
          {
            set_edgelabel(ac,EDGEMARKER);
            nondelaunaystack[stackpointer++]=ac;
          }
        }

        if (ad->label!=NULL)
        {
          if (strcmp(ad->label,TRIANGULATION_EDGELABEL)==0)  
          {
            set_edgelabel(ad,EDGEMARKER);
            nondelaunaystack[stackpointer++]=ad;
          }
        }

        if (bc->label!=NULL)
        {
          if (strcmp(bc->label,TRIANGULATION_EDGELABEL)==0)  
          {
            set_edgelabel(bc,EDGEMARKER);
            nondelaunaystack[stackpointer++]=bc;
          }
        }

        if (bd->label!=NULL)
        {
          if (strcmp(bd->label,TRIANGULATION_EDGELABEL)==0)  
          {
            set_edgelabel(bd,EDGEMARKER);
            nondelaunaystack[stackpointer++]=bd;
          } 
        }
      } 
    }
  } 
  
  /* freeing the stack for the non local delaunay edges */
  freeing_a_1_dimensional_array_of_typ_Sedge(nondelaunaystack,n*3);

}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                    end of pl_delaunay.c                     */
/*                                                             */
/***************************************************************/
