/***************************************************************/
/*                                                             */
/*  filename:  planargraph_utility.c                           */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*                                                             */
/*  imports:                                                   */
/*   void remove_all_triangulation_edges();                    */
/*   double edgelength();                                      */
/*   double trianglesurface();                                 */
/*   void triangleangles();                                    */
/*   double circumcircleradius();                              */
/*   double angle_();                                          */
/*   int left_or_right_turn();                                 */
/*   int test_for_an_intersection();                           */
/*                                                             */
/*  exports:                                                   */
/*    void planargraphtriangulation_info();                    */
/*    int planarity_test();                                    */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      include section                        */
/*                                                             */
/***************************************************************/

#include <sys/types.h>
#include <sys/times.h>
#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>
#include <algorithms.h>
#include "miscmath_utility.h"
#include "misc_utility.h"
#include "globaldefinitions.h"

#include "memory_allocation.h"


/***************************************************************/
/*                                                             */
/*                      local variables                        */
/*                                                             */
/***************************************************************/

Local struct tms starttime_of_algorithm,  /* variables to determine the   */
                 endtime_of_algorithm;    /* needed time of the algorithm */


/***************************************************************/
/*                                                             */
/*  this procedures determinates the nodes <c> and <d> of the  */
/*  quadrilateral, that is definied by the nodes <a> and <b>.  */
/*  if one node does not exist then <c> is set to <a>, or <d>  */
/*  is set to <b>.                                             */
/*                                                             */
/***************************************************************/

void get_nodes_of_quadrilateral(Snode a, Snode b, Snode *c, Snode *d)
{
  Snode help;
  Sedge edge1,edge2;

  *c=a;
  *d=b;

  for_edgelist(a,edge1)
  {
    for_edgelist(b,edge2)
    {
      if (edge1->tnode==edge2->tnode)
      {
        help=edge1->tnode;
        if (left_or_right_turn(a->x,a->y,
                               b->x,b->y,
                               help->x,help->y)==1)
        {
          if ((*c==a) || (angle_(a,b,help)<angle_(a,b,*c)))
            *c=help;
        }
        else
        {
          if ((*d==b) || (angle_(a,b,help)<angle_(a,b,*d)))
            *d=help;
        }
      }
    }
    end_for_edgelist(b,edge2);
  }
  end_for_edgelist(a,edge1);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*              calulating the characteristics of              */
/*              a triangulated planar graph                    */
/*                                                             */
/***************************************************************/

void planargraphtriangulation_info(Sgraph inputgraph)
{
  Snode node,node_a,node_b,node_c,node_d;
  Sedge edge;
  double length_of_edge,
         surface_of_triangle,
         angle_a,angle_b,angle_c,
         circumcircle_of_triangle;
  int degree;
  double sum_of_edgelength=NULL_DOUBLE;
  double minimal_edgelength=POSITIV_DOUBLE_INFINITLY;
  double maximal_edgelength=NULL_DOUBLE;
  double sum_of_trianglesurface=NULL_DOUBLE;
  double minimal_trianglesurface=POSITIV_DOUBLE_INFINITLY;
  double maximal_trianglesurface=NULL_DOUBLE;
  double minimal_angle=POSITIV_DOUBLE_INFINITLY;
  double maximal_angle=NULL_DOUBLE;
  double minimal_circumcircle=POSITIV_DOUBLE_INFINITLY;
  double maximal_circumcircle=NULL_DOUBLE;
  int maximal_degree=0;


  if (OUTPUT_INFORMATION)
  {
    /* take the time the polgontriangulation-algorithm needed */
    times(&endtime_of_algorithm);

    for_all_nodes(inputgraph,node)
    {
      /* calculating the maximal degree */
      degree=0;
      for_edgelist(node,edge)
      {
        degree++;
      }
      end_for_edgelist(node,edge);
      if (degree>maximal_degree) maximal_degree=degree;
    }
    end_for_all_nodes(inputgraph,node);


    for_all_nodes(inputgraph,node)
    {
      /* calculating the information about the edgelength */
      for_edgelist(node,edge) if (unique_edge(edge))
      {
        if (edge->label!=NULL)
        {
          length_of_edge=edgelength(edge->snode->x,
                                    edge->snode->y,
                                    edge->tnode->x,
                                    edge->tnode->y);
          sum_of_edgelength=sum_of_edgelength+length_of_edge;
          if (length_of_edge<minimal_edgelength)
            minimal_edgelength=length_of_edge;
          if (length_of_edge>maximal_edgelength)
            maximal_edgelength=length_of_edge;
        }
      }
      end_for_edgelist(node,edge);
    }
    end_for_all_nodes(inputgraph,node);


    for_all_nodes(inputgraph,node)
    {
      /* calculating the information about the single triangles */
      for_edgelist(node,edge)
      {
        if (unique_edge(edge))
        {
          node_a=edge->snode;
          node_b=edge->tnode;
          get_nodes_of_quadrilateral(node_a,node_b,&node_c,&node_d);
          if (node_c!=node_a)
          {
            surface_of_triangle=trianglesurface(node_a->x,node_a->y,
                                                node_b->x,node_b->y,
                                                node_c->x,node_c->y);
            sum_of_trianglesurface=sum_of_trianglesurface+surface_of_triangle/3.0;
            if (surface_of_triangle>maximal_trianglesurface)
              maximal_trianglesurface=surface_of_triangle;
            if (surface_of_triangle<minimal_trianglesurface)
              minimal_trianglesurface=surface_of_triangle;
            circumcircle_of_triangle=circumcircleradius(node_a->x,node_a->y,
                                                        node_b->x,node_b->y,
                                                        node_c->x,node_c->y);
            if (circumcircle_of_triangle>maximal_circumcircle)
              maximal_circumcircle=circumcircle_of_triangle;
            if (circumcircle_of_triangle<minimal_circumcircle)
              minimal_circumcircle=circumcircle_of_triangle;
            triangleangles(node_a->x,node_a->y,
                           node_b->x,node_b->y,
                           node_c->x,node_c->y,
                           &angle_a,&angle_b,&angle_c);
            if (angle_a>maximal_angle) maximal_angle=angle_a;
            if (angle_b>maximal_angle) maximal_angle=angle_b;
            if (angle_c>maximal_angle) maximal_angle=angle_c;
            if (angle_a<minimal_angle) minimal_angle=angle_a;
            if (angle_b<minimal_angle) minimal_angle=angle_b;
            if (angle_c<minimal_angle) minimal_angle=angle_c;
          }
          if (node_d!=edge->tnode)
          {
            surface_of_triangle=trianglesurface(node_a->x,node_a->y,
                                                node_b->x,node_b->y,
                                                node_d->x,node_d->y);
            sum_of_trianglesurface=sum_of_trianglesurface+surface_of_triangle/3.0;
            if (surface_of_triangle>maximal_trianglesurface)
              maximal_trianglesurface=surface_of_triangle;
            if (surface_of_triangle<minimal_trianglesurface)
              minimal_trianglesurface=surface_of_triangle;
            circumcircle_of_triangle=circumcircleradius(node_a->x,node_a->y,
                                                        node_b->x,node_b->y,
                                                        node_d->x,node_d->y);
            if (circumcircle_of_triangle>maximal_circumcircle)
              maximal_circumcircle=circumcircle_of_triangle;
            if (circumcircle_of_triangle<minimal_circumcircle)
              minimal_circumcircle=circumcircle_of_triangle;
            triangleangles(node_a->x,node_a->y,
                           node_b->x,node_b->y,
                           node_d->x,node_d->y,
                           &angle_a,&angle_b,&angle_c);
            if (angle_a>maximal_angle) maximal_angle=angle_a;
            if (angle_b>maximal_angle) maximal_angle=angle_b;
            if (angle_c>maximal_angle) maximal_angle=angle_c;
            if (angle_a<minimal_angle) minimal_angle=angle_a;
            if (angle_b<minimal_angle) minimal_angle=angle_b;
            if (angle_c<minimal_angle) minimal_angle=angle_c;
          }
        }
      }
      end_for_edgelist(node,edge);
    }
    end_for_all_nodes(inputgraph,node);

    /* output of the calculated triangulation information */
    message("sum of edgelength:      %13.3f\n",sum_of_edgelength);
    message("minimal edgelength:     %13.3f\n",minimal_edgelength);
    message("maximal edgelength:     %13.3f\n",maximal_edgelength);
    message("minimal surface:        %13.3f\n",minimal_trianglesurface);
    message("maximal surface:        %13.3f\n",maximal_trianglesurface);
    message("minimal angle:          %13.3f\n",minimal_angle*90.0/acos(1));
    message("maximal angle:          %13.3f\n",maximal_angle*90.0/acos(1));
    message("minimal circumcircle:   %13.3f\n",minimal_circumcircle);
    message("maximal circumcircle:   %13.3f\n",maximal_circumcircle);
    message("maximal degree:         %13d\n",maximal_degree);
    message("needed time:  %f sec\n",
            (double)(endtime_of_algorithm.tms_utime-
                     starttime_of_algorithm.tms_utime)/60.0);
  }
}
/***************************************************************/



/***************************************************************/
/*                                                             */
/*   testing, whether the current graph is a correct planar    */
/*   graph, which can be triangulated.                         */
/*                                                             */
/***************************************************************/

int planarity_test(Sgraph inputgraph)
{
  Snode node,node1,node2;    /* help-nodes    */
  Sedge edge,edge1,edge2;    /* help-edges    */
  int n,cr;                  /* help-variable */
 

  if (CHECK_THE_INPUTGRAPH)
  {

    /* is there a graph at all */
    if ((inputgraph==nil) || (inputgraph->nodes==nil))
    {
       error("there is no graph at all!\n");
       return(0);
    }
    
    /* is it a undirected graph */
    if (inputgraph->directed)
    {
      error("graph must be undirected!\n");
      return(0);
    }

    /* removing all triangulation edges */
    remove_all_triangulation_edges(inputgraph);

    /* running a planarity-test */
    switch(embed(inputgraph))
    {
      case NONPLANAR : 
        error("graph is nonplanar!\n");
        return(0);
      case SELF_LOOP : 
        error("graph contains self-loops!\n");
        return(0);
      case MULTIPLE_EDGE :
        error("graph contains multiple edges!\n");
        return(0);
      case NO_MEM :
        error("not enough memory!\n");
        return(0);
      default: break;
    }

    /* is there an intersection */
    n=0;
    for_all_nodes(inputgraph,node1)
    {
      n++;
      for_edgelist(node1,edge1)
      {
        if (unique_edge(edge1))
        {
          for_all_nodes(inputgraph,node2)
          {
            for_edgelist(node2,edge2)
            {
              if (unique_edge(edge2))
              {
                if (edge1!=edge2)
                {
                  cr=test_for_an_intersection(edge1->snode->x,edge1->snode->y,
                                              edge1->tnode->x,edge1->tnode->y,
                                              edge2->snode->x,edge2->snode->y,
                                              edge2->tnode->x,edge2->tnode->y);
                  if ((cr==2) || (cr==3) || (cr==2))
                  {
                    error("graph is nonplanar!\n");
                    return(0);
                  }
                }
              }
            }
            end_for_edgelist(node2,edge2);
          }
          end_for_all_nodes(inputgraph,node2);
        }
      }
      end_for_edgelist(node1,edge1);
    }
    end_for_all_nodes(inputgraph,node1);

    /* remove the labels */
    for_all_nodes(inputgraph,node)
    {
      set_nodelabel(node,"");
      for_edgelist(node,edge)
      {
        if (unique_edge(edge))
        {
          set_edgelabel(edge,"");
        }
      }
      end_for_edgelist(node,edge)
    }
    end_for_all_nodes(inputgraph,node);

    if (n<3)
    {
      error("there must be at least three nodes!\n");
      return(0);
    }

    /* reset the timer */
    times(&starttime_of_algorithm);
   
  }
  return(1);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*               end of planargraph_utility.c                  */  
/*                                                             */
/***************************************************************/
