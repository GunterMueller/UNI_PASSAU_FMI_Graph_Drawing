/***************************************************************/
/*                                                             */
/*  filename:  pl_edgeflip.c                                   */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implementation of an edgeflip     */
/*    algorithm for generating triangulation the approximates  */
/*    an optimal triangulation for a certain criterion         */
/*                                                             */
/*                                                             */
/*  imports:                                                   */
/*   Sedge make_a_triangulation_edge();                        */
/*   int degree_of_node();                                     */
/*   int get_konvex_quadrilateral();                           */
/*   double edgelength();                                      */
/*   double trianglesurface();                                 */
/*   Sedge *allocating_a_1_dimensional_array_of_typ_Sedge();   */
/*   void freeing_a_1_dimensional_array_of_typ_Sedge();        */
/*                                                             */
/*  exports:                                                   */
/*    void plan_edgeflip();                                    */
/*    enum flip_criterion;                                     */
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
#include "pl_random.h"

#include "pl_edgeflip.h"

/***************************************************************/
/*                                                             */
/*  this function decides for different criteria, whether in   */
/*  the convex quadrilateral an edgeflip must be done or not   */
/*                                                             */
/***************************************************************/

int flip_criterion(Snode a, Snode b, Snode c, Snode d, edgeflip_criterion ef_criterion)
{
  int value;
  double w1,w2,w3,w4,w5,w6,ww1,ww2;
  double abc,abd,cda,cdb,A1,A2;

  value=0;
  switch (ef_criterion)
  {
   /* minimal edgelength */
   case ef_minlength : 
     if (edgelength(a->x,a->y,b->x,b->y)
         >
         edgelength(c->x,c->y,d->x,d->y)) 
       value=1;
     break;

   /* min max degree */
   case ef_minmaxdegree : 
     if (maximum(maximum(degree_of_node(a)-1,
                         degree_of_node(b)-1),
                 maximum(degree_of_node(c)+1,
                         degree_of_node(d)+1))
         <
         maximum(maximum(degree_of_node(a),
                         degree_of_node(b)),
                 maximum(degree_of_node(c),
                         degree_of_node(d))))
       value=1;
     break;

    /* min max angle */
    case ef_minmaxangle :
      triangleangles(a->x,a->y,b->x,b->y,c->x,c->y,&w1,&w2,&w3);
      triangleangles(a->x,a->y,b->x,b->y,d->x,d->y,&w4,&w5,&w6);
      ww1=w1;
      if (ww1<w2) ww1=w2;
      if (ww1<w3) ww1=w3;
      if (ww1<w4) ww1=w4;
      if (ww1<w5) ww1=w5;
      if (ww1<w6) ww1=w6;
      triangleangles(c->x,c->y,d->x,d->y,a->x,a->y,&w1,&w2,&w3);
      triangleangles(c->x,c->y,d->x,d->y,b->x,b->y,&w4,&w5,&w6);
      ww2=w1;
      if (ww2<w2) ww2=w2;
      if (ww2<w3) ww2=w3;
      if (ww2<w4) ww2=w4;
      if (ww2<w5) ww2=w5;
      if (ww2<w6) ww2=w6;
      if (ww2<ww1) value=1;
      break;

    /* mostly equal surfaces */
    case ef_mostlyequalsurface :
      abc=trianglesurface(a->x,a->y,b->x,b->y,c->x,c->y);
      abd=trianglesurface(a->x,a->y,b->x,b->y,d->x,d->y);
      A1=abc;
      if (abc<abd) A1=abd;
      cda=trianglesurface(c->x,c->y,d->x,d->y,a->x,a->y);
      cdb=trianglesurface(c->x,c->y,d->x,d->y,b->x,b->y);
      A2=cda;
      if (cda<cdb) A2=cdb;
      if (A2<A1) value=1;
      break;
  }
  return(value);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*      creating a triangulation of a planar graph by using    */
/*      edgeflips on convex quadrilaterals, where the          */
/*      specified criteria ist opimized at all convex          */
/*      quadrilaterals                                         */
/*                                                             */
/***************************************************************/

void plan_edgeflip(Sgraph inputgraph, edgeflip_criterion ef_criterion)
{
  Sedge *nonlocaloptstack;  /* the stack for the non local optimal edges */
  int stackpointer;
  int n;                    /* number of inputnodes */

  Sedge edge,ab,ac,ad,bc,bd;  /* help-variables */
  Snode node,c,d;             /* help-variables */

  
  /* generating the random initial triangulation */
  plan_random(inputgraph); 

  /* calculating the number of nodes, the current graph contains */
  n=number_of_nodes_in_the_inputgraph(inputgraph);

  /* pushing all triangulation edges onto the stack and mark them */
  
  nonlocaloptstack=allocating_a_1_dimensional_array_of_typ_Sedge(n*3);
  
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
            nonlocaloptstack[stackpointer++]=edge;
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
    ab=nonlocaloptstack[--stackpointer];
    set_edgelabel(ab,TRIANGULATION_EDGELABEL);

    /* determining the konvex quadrilateral for the edge ab */
    if (get_konvex_quadrilateral(ab,&ac,&ad,&bd,&bc,&c,&d))
    {

      /* is the edge (a,b) local optimal? */
      if (flip_criterion(ab->snode,ab->tnode,c,d,ef_criterion))
      {
        
        /* make a local optimal edgeflip */
        remove_edge(ab);
        ab=make_a_triangulation_edge(c,d);
        
        if (ef_criterion==ef_minmaxdegree)
        {
          /* push all edges starting from a,b,c and d     */
          /* onto the stack, if they are not prescribed   */
          /* and not already on the stack                 */

          for_edgelist(ac->snode,edge) 
          {
            if (unique_edge(edge))
            {
              if (edge->label!=NULL)
              {
                if ((edge!=ab) && (strcmp(edge->label,TRIANGULATION_EDGELABEL))==0)
                {
                  set_edgelabel(edge,EDGEMARKER);
                  nonlocaloptstack[stackpointer++]=edge;
                }
              }
            }
          }

          end_for_edgelist(ac->snode,edge);
          for_edgelist(ac->tnode,edge) 
          {
            if (unique_edge(edge))
            {
              if (edge->label!=NULL)
              {
                if ((edge!=ab) && (strcmp(edge->label,TRIANGULATION_EDGELABEL))==0)
                {
                  set_edgelabel(edge,EDGEMARKER);
                  nonlocaloptstack[stackpointer++]=edge;
                }
              }
            }
          }
          end_for_edgelist(ac->tnode,edge);

          for_edgelist(bd->snode,edge) 
          {
            if (unique_edge(edge))
            {
              if (edge->label!=NULL)
              {
                if ((edge!=ab) && (strcmp(edge->label,TRIANGULATION_EDGELABEL))==0)
                {
                  set_edgelabel(edge,EDGEMARKER);
                  nonlocaloptstack[stackpointer++]=edge;
                }
              }
            }
          }
          end_for_edgelist(bd->snode,edge);

          for_edgelist(bd->tnode,edge) 
          {
            if (unique_edge(edge))
            {
              if (edge->label!=NULL)
              {
                if ((edge!=ab) && (strcmp(edge->label,TRIANGULATION_EDGELABEL))==0)
                {
                  set_edgelabel(edge,EDGEMARKER);
                  nonlocaloptstack[stackpointer++]=edge;
                }
              }
            }
          }
          end_for_edgelist(bd->tnode,edge);
        }
        
        else
        
        {
          /* push the edges (a,c), (a,d), (b,c) and (b,d) */
          /* onto the stack, if they are not prescribed   */
          /* and not already on the stack                 */
          if (ac->label!=NULL)
          {
            if (strcmp(ac->label,TRIANGULATION_EDGELABEL)==0)  
            {
              set_edgelabel(ac,EDGEMARKER);
              nonlocaloptstack[stackpointer++]=ac;
            }
          }

          if (ad->label!=NULL)
          {
            if (strcmp(ad->label,TRIANGULATION_EDGELABEL)==0)  
            {
              set_edgelabel(ad,EDGEMARKER);
              nonlocaloptstack[stackpointer++]=ad;
            }
          }

          if (bc->label!=NULL)
          {
            if (strcmp(bc->label,TRIANGULATION_EDGELABEL)==0)  
            {
              set_edgelabel(bc,EDGEMARKER);
              nonlocaloptstack[stackpointer++]=bc;
            }
          }

          if (bd->label!=NULL)
          {
            if (strcmp(bd->label,TRIANGULATION_EDGELABEL)==0)  
            {
              set_edgelabel(bd,EDGEMARKER);
              nonlocaloptstack[stackpointer++]=bd;
            } 
          }

        }
      }
    } 
  } 

  /* freeing the stack for the non local optimal edges */
  freeing_a_1_dimensional_array_of_typ_Sedge(nonlocaloptstack,n*3);

}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                    end of pl_edgeflip.c                     */
/*                                                             */
/***************************************************************/
