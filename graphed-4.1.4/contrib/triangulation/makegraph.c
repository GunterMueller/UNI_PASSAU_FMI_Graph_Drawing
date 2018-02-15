/***************************************************************/
/*                                                             */
/*  filename:  makegraph.c                                     */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains two routines for creating a random    */
/*    graph. the first routine creates a random planar graph.  */
/*    the second makes a random simple polygon.                */
/*                                                             */
/*  imports:                                                   */
/*   Sedge make_a_prescribed_edge();                           */
/*   Snode *create_an_array_for_the_nodes();                   */
/*   int test_for_an_intersection();                           */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*                                                             */
/*  exports:                                                   */
/*    int make_a_random_planar_graph();                        */
/*    int make_a_random_simple_polygon();                      */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                        Include-Section                      */
/*                                                             */
/***************************************************************/

#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <math.h>

#include "globaldefinitions.h"
#include "planargraph_utility.h"
#include "memory_allocation.h"
#include "misc_utility.h"
#include "miscmath_utility.h"
#include "sgraph/random.h"



/***************************************************************/
/*                                                             */
/*        this procedure creates a random planar graph         */
/*                                                             */
/***************************************************************/

int make_a_random_planar_graph(Sgraph inputgraph, int number_of_nodes, int number_of_edges, int Max_X, int Max_Y)
{
  int i,s,t,test,cr;      /* help-variables               */
  Snode node;             /* help-node                    */
  Sedge edge;             /* help-edge                    */
  Snode *ND;              /* array for the nodes          */
  int n,nn;               /* number of nodes in the graph */
  Sedge *deledgelist;     /* array for edges to remove    */
  int delcount;


  if (inputgraph==nil)
  {
    error("there must be a undirected graph - switch to create mode!\n");
    return(0);
  }
  
  if (inputgraph->directed)
  {
    error("switch to undirected mode!\n");
    return(0);
  }


  /* remove graph */
  ND=create_an_array_for_the_nodes(inputgraph,&nn);
  deledgelist=allocating_a_1_dimensional_array_of_typ_Sedge(nn*3);
  delcount=0;
  for (i=0;i<nn;i++) 
    for_edgelist(ND[i],edge) if (unique_edge(edge))
    {
      deledgelist[delcount++]=edge;
    }
    end_for_edgelist(ND[i],edge);

  for (i=0;i<delcount;i++)
    remove_edge(deledgelist[i]);
  freeing_a_1_dimensional_array_of_typ_Sedge(deledgelist,nn*3);


  /* create nodes randomly */
  for (i=0;i<number_of_nodes;i++)
  {
    if (i<nn) node=ND[i];
    else node=make_node(inputgraph,make_attr(ATTR_DATA,(char *)nil));
    node->x=random() % Max_X;
    node->y=random() % Max_Y;
  }

  for (i=number_of_nodes;i<nn;i++)
    remove_node(ND[i]);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,nn);

  /* create edges randomly */
  ND=create_an_array_for_the_nodes(inputgraph,&n);

  for (i=0;i<number_of_edges;i++)
  {
    do
    {
      test=0;
      s=random() % n;
      t=s;
      while (s==t) t=random() % n;
      for_all_nodes(inputgraph,node)
      {
        for_sourcelist(node,edge)
        {
          cr=test_for_an_intersection(ND[s]->x,ND[s]->y,
                                      ND[t]->x,ND[t]->y,
                                      edge->snode->x,edge->snode->y,
                                      edge->tnode->x,edge->tnode->y);
          if ((cr==2) || (cr==3) || (cr==4))  test=1;
        }
        end_for_sourcelist(node,edge);
      }
      end_for_all_nodes(inputgraph,node);
    }
    while (test);
    make_a_prescribed_edge(ND[s],ND[t]);
  }
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);

  return(1);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*        this procedure creates a random simple polygon       */
/*                                                             */
/***************************************************************/

int make_a_random_simple_polygon(Sgraph inputgraph, int number_of_nodes, int Max_X, int Max_Y)
{
  Snode node,node0,node1;            /* help-nodes     */
  Sedge edge;                        /* help-edge      */
  int i,j,ii,test;                   /* help-variables */
  int x1,x2,y1,y2,xx,yy;             /*       "        */
  int *NDX,*NDY;                     /* help-arrays    */
  Snode *ND;                         /* array for the nodes          */
  int n,nn;                          /* number of nodes in the graph */
  Sedge *deledgelist;                /* array for edges to remove    */
  int delcount;


  if (inputgraph==nil)
  {
    error("there must be a undirected graph - switch to create mode!\n");
    return(0);
  }
  
  if (inputgraph->directed)
  {
    error("switch to undirected mode!\n");
    return(0);
  }


  /* create nodes randomly */
  n=3;
  NDX=allocating_a_1_dimensional_array_of_typ_int(number_of_nodes);
  NDY=allocating_a_1_dimensional_array_of_typ_int(number_of_nodes);
  for (i=0;i<3;i++)
  {
    NDX[i]=random() % Max_X;
    NDY[i]=random() % Max_Y;
  }

  while (n<number_of_nodes)
  {
    /* determine randomly polygon-edge (i,i+1) */
    i=random() % n;
    j=(i+1) % n;
    x1=NDX[i];
    y1=NDY[i];
    x2=NDX[j];
    y2=NDY[j];

    /* choose a random node */
    xx=random() % Max_X;
    yy=random() % Max_Y;

    test=1;
    /* test, whether the new edges can inserted */ 
    for (ii=0;ii<n-2;ii++)
    {
      if (test_for_an_intersection(x1,y1,xx,yy,
                                   NDX[(ii+j) % n],NDY[(ii+j) % n],
                                   NDX[(ii+1+j) % n],NDY[(ii+1+j) % n])!=0)
        test=0;
      if (test_for_an_intersection(x2,y2,xx,yy,
                                   NDX[(ii+j+1) % n],NDY[(ii+j+1) % n],
                                   NDX[(ii+j+2) % n],NDY[(ii+j+2) % n])!=0)
        test=0;
      if (test==0) ii=n;
    }
    if (test_for_an_intersection(x1,y1,xx,yy,
                                 x1,x1,NDX[(i-1+n) % n],NDY[(i-1+n) % n])==4)
      test=0;
    if (test_for_an_intersection(x2,y2,xx,yy,
                                 x2,x2,NDY[(j+1) % n],NDY[(j+1) % n])==4)
      test=0;

    if (test==1)
    {
      if (j!=0)
      {
        for (ii=n;ii>j;ii--)
        {
          NDX[ii]=NDX[ii-1];
          NDY[ii]=NDY[ii-1];
        }
        NDX[j]=xx;
        NDY[j]=yy;
      }
      else
      {
        NDX[n]=xx;
        NDY[n]=yy;
      }
      n++;
    }
  }

  /* remove graph */
  ND=create_an_array_for_the_nodes(inputgraph,&nn);
  deledgelist=allocating_a_1_dimensional_array_of_typ_Sedge(nn*3);
  delcount=0;
  for (i=0;i<nn;i++) 
    for_edgelist(ND[i],edge) if (unique_edge(edge))
    {
      deledgelist[delcount++]=edge;
    }
    end_for_edgelist(ND[i],edge);

  for (i=0;i<delcount;i++)
    remove_edge(deledgelist[i]);
  freeing_a_1_dimensional_array_of_typ_Sedge(deledgelist,nn*3);
  for (i=0;i<n;i++)
  {
    if (i<nn) node=ND[i];
    else node=make_node(inputgraph,make_attr(ATTR_DATA,(char*)nil));
    node->x=NDX[i];
    node->y=NDY[i];
    if (i>0)
      edge=make_a_prescribed_edge(node,node0);
    node0=node;
    if (i==0) node1=node; 
  }
  edge=make_a_prescribed_edge(node0,node1); 


  for (i=n;i<nn;i++)
    remove_node(ND[i]);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,nn);

  /* freeing dataarrays */
  freeing_a_1_dimensional_array_of_typ_int(NDX,number_of_nodes);
  freeing_a_1_dimensional_array_of_typ_int(NDY,number_of_nodes);


  return(1);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                     end of makegraph.c                      */
/*                                                             */
/***************************************************************/
