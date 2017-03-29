/***************************************************************/
/*                                                             */
/*  filename:  misc_utility.c                                  */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    This file contains some functions and procedures that    */
/*    are needed by nearly all triangulion-algorithms          */
/*                                                             */
/*  imports:                                                   */
/*   double angle_();                                          */
/*   int left_or_right_turn();                                 */
/*   int test_for_an_intersection();                           */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   Sedge *allocating_a_1_dimensional_array_of_typ_Sedge();   */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*   void freeing_a_1_dimensional_array_of_typ_Sedge();        */
/*                                                             */
/*  exports:                                                   */
/*    remove_all_triangulation_edges()                         */
/*    Sedge make_a_prescribed_edge();                          */
/*    Sedge make_a_triangulation_edge();                       */
/*    int create_an_array_for_the_nodes();                     */
/*    int degree_of_node();                                    */
/*    int get_konvex_quadrilateral();                          */
/*    int number_of_nodes_in_the_inputgraph();                 */
/*    int first_char_in_string();                              */
/*                                                             */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      include section                        */
/*                                                             */
/***************************************************************/

#include <math.h>
#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include "globaldefinitions.h"
#include "memory_allocation.h"
#include "miscmath_utility.h"


/***************************************************************/
/*                                                             */
/*  returns the number of the nodes that are actully in the    */
/*  inputgraph                                                 */
/*                                                             */
/***************************************************************/

int number_of_nodes_in_the_inputgraph(Sgraph inputgraph)
{
  int number_of_nodes;
  Snode node;

  number_of_nodes=0;
  for_all_nodes(inputgraph,node)
  {
    set_nodelabel(node,"");
    number_of_nodes++;
  }
  end_for_all_nodes(inputgraph,node);

  return(number_of_nodes);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*    removing all triangulation edges in the current graph    */
/*                                                             */
/***************************************************************/

void remove_all_triangulation_edges(Sgraph inputgraph)
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
        if (edge->label!=NULL)
        {
          if (strcmp(edge->label,TRIANGULATION_EDGELABEL)==0)  
            DelEdge[deledgecount++]=edge;  
        }
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
/*                 generating a prescribed edge                */
/*                                                             */
/***************************************************************/

Sedge make_a_prescribed_edge(Snode a, Snode b)
{
  Sedge edge;

  edge=make_edge(a,b,make_attr(ATTR_DATA,(char *)nil));
  set_edgelabel(edge,"");

  return(edge);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*               generating a triangulation edge               */
/*                                                             */
/***************************************************************/

Sedge make_a_triangulation_edge(Snode a, Snode b)
{
  Sedge edge;
  Graphed_edge gedge;
  edge=make_edge(a,b,make_attr(ATTR_DATA,(char *)nil));
  gedge=create_graphed_edge_from_sedge(edge);
 
  edge_set(gedge,EDGE_TYPE,find_edgetype("#dashed"),NULL); 
  edge_set(gedge,EDGE_LABEL_VISIBILITY,FALSE,NULL);
  set_edgelabel(edge,TRIANGULATION_EDGELABEL);

  return(edge);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*               store all nodes in the array ND               */
/*                 return the number of nodes                  */
/*                                                             */
/***************************************************************/

Snode  *create_an_array_for_the_nodes(Sgraph inputgraph, int *n)
{
  Snode *ND;
  int nodecount;
  Snode node;

  nodecount=0;
  for_all_nodes(inputgraph,node)
  {
     nodecount++;
  }
  end_for_all_nodes(inputgraph,node);


  ND=allocating_a_1_dimensional_array_of_typ_Snode(nodecount);
  nodecount=0;

  for_all_nodes(inputgraph,node)
  {
     ND[nodecount]=node;
     nodecount++;
  }
  end_for_all_nodes(inputgraph,node);

  *n=nodecount;
  return(ND);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*             calculate the degree of a node                  */
/*                                                             */
/***************************************************************/

int degree_of_node(Snode node)
{
  int degree;
  Sedge edge;

  degree=0;
  for_edgelist(node,edge)
  {
    degree++;
  }
  end_for_edgelist(node,edge);

  return(degree);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   this function determinates, whether the quadrilateral     */    
/*   around the edge [ab] in the current triangulation is      */
/*   convex. And if it is, the functions returns the edges     */
/*   and the two other nodes of that convex quadrilateral      */
/*                                                             */
/***************************************************************/

int get_konvex_quadrilateral(Sedge ab, Sedge *ac, Sedge *ad, Sedge *bd, Sedge *bc, Snode *c, Snode *d)
{
  Snode a,b,cc,dd,help;
  Sedge edge1,edge2;

  a=ab->snode;
  b=ab->tnode;
  *c=a;
  *d=b;


  for_edgelist(a,edge1)
  {
    for_edgelist(b,edge2)
    {
      if (edge1->tnode==edge2->tnode)
      {
        help=edge1->tnode;
        if (left_or_right_turn(a->x,a->y,b->x,b->y,help->x,help->y)==1)
        {
          if ((*c==a) || (angle_(a,b,help)<angle_(a,b,*c)))
          {
            *c=help;
            *ac=edge1;
            *bc=edge2;
          }
        }
        else
        {
          if ((*d==b) || (angle_(a,b,help)<angle_(a,b,*d)))
          {
            *d=help;
            *ad=edge1;
            *bd=edge2;
          }
        }
      }
    }
    end_for_edgelist(b,edge2);
  }
  end_for_edgelist(a,edge1);


  cc=*c;
  dd=*d;
  if ((a!=*c) && 
      (b!=*d) && 
      (test_for_an_intersection(ab->snode->x,ab->snode->y,
                                ab->tnode->x,ab->tnode->y,
                                cc->x,cc->y,
                                dd->x,dd->y)==2))
    return(1);
  else 
    return(0);
}
/***************************************************************/




/***************************************************************/
/*                                                             */
/*                     end of miscutility.c                    */  
/*                                                             */
/***************************************************************/
