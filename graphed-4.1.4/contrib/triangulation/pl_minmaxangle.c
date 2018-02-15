/***************************************************************/
/*                                                             */
/*  filename:  pl_minmaxangle.c                                */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implementation of an algorithm    */
/*    for a minmax angle triangulation                         */
/*                                                             */
/*  imports:                                                   */
/*   Sedge make_a_triangulation_edge();                        */
/*   double triangleangles();                                  */
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge *allocating_a_1_dimensional_array_of_typ_Sedge();   */
/*   void freeing_a_1_dimensional_array_of_typ_Sedge();        */
/*   int left_or_right_turn();                                 */
/*   angle_();                                                 */
/*   plan_delaunay();                                          */
/*                                                             */
/*  exports:                                                   */
/*    void plan_minmaxangle();                                 */
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
#include "pl_delaunay.h"



/***************************************************************/
/*                                                             */
/* this procedure creates a triangulation for a planar graph   */
/* that minimizes the maximal angle.                           */
/*                                                             */
/***************************************************************/

void plan_minmaxangle(Sgraph inputgraph)
{
  Sedge *nonoptstack;  /* the stack for the non optimal edges */
  int stackpointer;
  int n;               /* number of inputnodes */

  Sedge edge,ab,ac,ad,bc,bd,edge1,edge2;     /* help-variables */
  Snode node,a,b,c,cc,d,dd,help;             /* help-variables */
  double w1,w2,w3,w4,w5,w6,ww1,ww2;
 

  /* creating a initial triangulation */
  plan_delaunay(inputgraph);    

  /* calculating the number of nodes, the current graph contains */
  n=number_of_nodes_in_the_inputgraph(inputgraph);

  /* pushing all triangulation edges onto the stack and mark them */
  
  nonoptstack=allocating_a_1_dimensional_array_of_typ_Sedge(n*3);
  
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
            nonoptstack[stackpointer++]=edge;
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
    ab=nonoptstack[--stackpointer];
    set_edgelabel(ab,TRIANGULATION_EDGELABEL);

    /* determining the konvex quadrilateral for the edge ab */

    a=ab->snode;
    b=ab->tnode;
    c=a;
    d=b;


    for_edgelist(a,edge1)
    {
      for_edgelist(b,edge2)
      {
        if (edge1->tnode==edge2->tnode)
        {
          help=edge1->tnode;
          if (left_or_right_turn(a->x,a->y,b->x,b->y,help->x,help->y)==1)
          {
            if ((c==a) || (angle_(a,b,help)<angle_(a,b,c)))
            {
              c=help;
              ac=edge1;
              bc=edge2;
            }
          }
          else
          {
            if ((d==b) || (angle_(a,b,help)<angle_(a,b,d)))
            {
              d=help;
              ad=edge1;
              bd=edge2;
            }
          }
        }
      }
      end_for_edgelist(b,edge2);
    }
    end_for_edgelist(a,edge1);


    cc=c;
    dd=d;
    if ((a!=c) && 
        (b!=d) && 
        (test_for_an_intersection(ab->snode->x,ab->snode->y,
                                  ab->tnode->x,ab->tnode->y,
                                  cc->x,cc->y,
                                  dd->x,dd->y)==2))
    {

      /* is the edge (a,b) local optimal? */
      triangleangles(ab->snode->x,ab->snode->y,
                     ab->tnode->x,ab->tnode->y,
                     c->x,c->y,
                     &w1,&w2,&w3);
      triangleangles(ab->snode->x,ab->snode->y,
                     ab->tnode->x,ab->tnode->y,
                     d->x,d->y,
                     &w4,&w5,&w6);
      ww1=w1;
      if (ww1<w2) ww1=w2;
      if (ww1<w3) ww1=w3;
      if (ww1<w4) ww1=w4;
      if (ww1<w5) ww1=w5;
      if (ww1<w6) ww1=w6;
      triangleangles(c->x,c->y,
                     d->x,d->y,
                     ab->snode->x,ab->snode->y,
                     &w1,&w2,&w3);
      triangleangles(c->x,c->y,
                     d->x,d->y,
                     ab->tnode->x,ab->tnode->y,
                     &w4,&w5,&w6);
      ww2=w1;
      if (ww2<w2) ww2=w2;
      if (ww2<w3) ww2=w3;
      if (ww2<w4) ww2=w4;
      if (ww2<w5) ww2=w5;
      if (ww2<w6) ww2=w6;
      if (ww2<ww1)
      {
        /* removing and inserting an edge */
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
            nonoptstack[stackpointer++]=ac;
          }
        }

        if (ad->label!=NULL)
        {
          if (strcmp(ad->label,TRIANGULATION_EDGELABEL)==0)  
          {
            set_edgelabel(ad,EDGEMARKER);
            nonoptstack[stackpointer++]=ad;
          }
        }

        if (bc->label!=NULL)
        {
          if (strcmp(bc->label,TRIANGULATION_EDGELABEL)==0)  
          {
            set_edgelabel(bc,EDGEMARKER);
            nonoptstack[stackpointer++]=bc;
          }
        }

        if (bd->label!=NULL)
        {
          if (strcmp(bd->label,TRIANGULATION_EDGELABEL)==0)  
          {
            set_edgelabel(bd,EDGEMARKER);
            nonoptstack[stackpointer++]=bd;
          } 
        }

      }
    } 
  } 

  /* freeing the stack for the non optimal edges */
  freeing_a_1_dimensional_array_of_typ_Sedge(nonoptstack,n*3);

}
/***************************************************************/



/***************************************************************/
/*                                                             */
/*                   end of pl_minmaxangle.c                   */
/*                                                             */
/***************************************************************/

