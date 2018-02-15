/***************************************************************/
/*                                                             */
/*  filename:  polygon_utility.c                               */
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
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge make_a_prescribed_edge();                           */
/*   double edgelength();                                      */
/*   double trianglesurface();                                 */
/*   void triangleangles();                                    */
/*   double circumcircleradius();                              */
/*   double angle_ccw();                                       */
/*   int test_for_an_intersection();                           */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   int **allocating_a_2_dimensional_array_of_typ_int();      */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   Sedge *allocating_a_1_dimensional_array_of_typ_Sedge();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_2_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*   void freeing_a_1_dimensional_array_of_typ_Sedge();        */
/*   void calculating_the_values_for_the_subpolygons_modified();*/
/*                                                             */
/*  exports:                                                   */
/*    void polygontriangulation_info();                        */
/*    int simplepolygon_test();                                */
/*    void create_polygonvisibilityinformation();              */
/*    void remove_outerpolygon();                              */
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

#include "po_dynamic.h"
#include "miscmath_utility.h"
#include "misc_utility.h"
#include "globaldefinitions.h"
#include "memory_allocation.h"

/***************************************************************/
/*                                                             */
/*                      local variables                        */
/*                                                             */
/***************************************************************/

Local Snode *PolyNodes;                   /* Snode-array with all edges   */
                                          /* of the polygon in correct    */
                                          /* order                        */
Local struct tms starttime_of_algorithm,  /* variables to determine the   */
                 endtime_of_algorithm;    /* needed time of the algorithm */


/***************************************************************/
/*                                                             */
/*    store all nodes of the polygon in the array Polynodes    */
/*       return <0>, if the graph is not a simple polygon      */
/*                                                             */
/***************************************************************/

Local int create_an_array_for_the_polygon_nodes(Sgraph inputgraph)
{
  int nodecount, not_simple_polygon,i,k;
  double sum_of_angles;
  Snode node,pnode[2];
  Sedge edge;

  not_simple_polygon=0;
  nodecount=0;
  for_all_nodes(inputgraph,node)
  {
     nodecount++;
     set_nodelabel(node,NODEMARKER_1);
  }
  end_for_all_nodes(inputgraph,node);

  if (nodecount<3)
  {
    return(0);
  }

  PolyNodes=allocating_a_1_dimensional_array_of_typ_Snode(nodecount);

  node=first_node_in_graph(inputgraph);
  for (i=0;i<nodecount;i++)
  {
    PolyNodes[i]=node;
    set_nodelabel(node,NODEMARKER_2); 
    k=0;
    for_edgelist(node,edge)
    {
      if (k<2) pnode[k]=edge->tnode;
      k++;
    }
    end_for_edgelist(node,edge);
    if (k!=2)
    {
      not_simple_polygon=1;
      i=nodecount;
    }

    node=pnode[0];
    if ((i>0) && (i<nodecount-1))
    {
      node=pnode[0];

      if (strcmp(node->label,NODEMARKER_2)==0)  
        node=pnode[1];

      if (strcmp(node->label,NODEMARKER_2)==0)  
      {  
        not_simple_polygon=1;
        i=nodecount;
      }

    }

    if (i==nodecount-1)
    {
      if ((pnode[0]!=PolyNodes[0]) && (pnode[1]!=PolyNodes[0]))
      {
        not_simple_polygon=1;
        i=nodecount;
      }
    }
  }
  for_all_nodes(inputgraph,node)
  {
    set_nodelabel(node,"");
  }
  end_for_all_nodes(inputgraph,node);

  if (not_simple_polygon)
  {
    freeing_a_1_dimensional_array_of_typ_Snode(PolyNodes,nodecount);
    return(0);
  }

  /* is the rotation clockwise */
  sum_of_angles=0.0;
  for (i=0;i<nodecount;i++)
  {
    sum_of_angles=sum_of_angles+angle_ccw(PolyNodes[i],
                                          PolyNodes[(i+1) % nodecount],
                                          PolyNodes[(i+2) % nodecount]);
  } 

  /* sum of angles in a polygon must be = pi * (n-2)     */
  /* otherwise the direction must be changed             */
  if (((sum_of_angles/(double)(nodecount-2))<(0.9*acos(-1.0))) ||
      ((sum_of_angles/(double)(nodecount-2))>(1.1*acos(-1.0))))
  {
    /* changing the direction */
    for (i=0;i<(nodecount / 2);i++)
    {
      node=PolyNodes[i];
      PolyNodes[i]=PolyNodes[nodecount-i-1];
      PolyNodes[nodecount-i-1]=node;
    }
  }

  return(nodecount);
}
/***************************************************************/


/***************************************************************/
/*   Filling an array of type <snode> with the nodes of the    */  
/*   polygon in the correct order.                             */
/***************************************************************/

void getting_the_polygonnodes(Sgraph inputgraph, Snode *ND, int n)
{
  int i;

  for (i=0;i<n;i++)
    ND[i]=PolyNodes[i];
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  calulating the characteristics of a triangulated polygon   */
/*                                                             */
/***************************************************************/

void polygontriangulation_info(Sgraph inputgraph)
{
  /* variables for the various charakteristics */
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
  int maximal_degree=NULL_DOUBLE;
  int number_of_tips=NULL_DOUBLE;

  double length_of_edge,            /* help-variables               */
         surface_of_triangle,
         angle_a,angle_b,angle_c,
         circumcircle_of_triangle,
         degree;
  Snode node;                       /* help-node                    */
  Sedge edge;                       /* help-edge                    */
  int i,j,a,z,help;                 /* help-variables               */
  int **V;                          /* visibility-information       */
  int *A,*Z;                        /* stack                        */
  Snode *ND;                        /* array for the nodes          */
  int n;                            /* number of nodes in the graph */
  int x,y;
  double koptvalue,dualtreeheight;
  double **Q;
  int **C;

  if (OUTPUT_INFORMATION)
  {

    /* take the time the polygontriangulation-algorithm needed */
    times(&endtime_of_algorithm);

    /* allocating dataarrays */
    n=number_of_nodes_in_the_inputgraph(inputgraph);
    ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
    getting_the_polygonnodes(inputgraph,ND,n);
    V=allocating_a_2_dimensional_array_of_typ_int(n);

    freeing_a_1_dimensional_array_of_typ_Snode(PolyNodes,n);

    for(i=0;i<n;i++)
      for(j=0;j<n;j++)
        V[i][j]=0;

    /* calculating the characteristiks with edgelength */
    for (i=0;i<n;i++)
    {
      for_edgelist(ND[i],edge) if (unique_edge(edge))
      {
        if ((edge->tnode!=ND[(i+1) % n]) &&
            (edge->tnode!=ND[(i-1+n) % n]))
        {
          length_of_edge=edgelength(edge->snode->x,edge->snode->y,
                                    edge->tnode->x,edge->tnode->y);
          sum_of_edgelength=sum_of_edgelength+length_of_edge;
          if (length_of_edge<minimal_edgelength)
            minimal_edgelength=length_of_edge;
          if (length_of_edge>maximal_edgelength) 
            maximal_edgelength=length_of_edge;
        }
        for (j=0;j<n;j++)
          if (edge->tnode==ND[j])
          {
            V[i][j]=1;
            V[j][i]=1;
          }
      }
      end_for_edgelist(ND[i],edge);
    }

    /* creating tables for dynamic programming */
    C=allocating_a_2_dimensional_array_of_typ_int(n);
    Q=allocating_a_2_dimensional_array_of_typ_double(n);

    /* filling the tables with dynamic programming */
    calculating_the_values_for_the_subpolygons_modified(mindualheight,V,C,Q,n);


    /* calculate dualtreeheight */

    dualtreeheight=POSITIV_DOUBLE_INFINITLY;
    for(x=0;x<n;x++)
     for(y=x+1;y<n;y++)
       for(z=y+1;z<n;z++)
         if ((V[x][y]) && (V[y][z]) && (V[z][x]))
         {
           koptvalue=Q[x][y];
           if (koptvalue<Q[z][x]) koptvalue=Q[z][x];
           if (koptvalue<Q[y][z]) koptvalue=Q[y][z];
           koptvalue=koptvalue+1.0;
           if (koptvalue<dualtreeheight)
             dualtreeheight=koptvalue;
          
         }

    freeing_a_2_dimensional_array_of_typ_double(Q,n);
    freeing_a_2_dimensional_array_of_typ_int(C,n);

    /* calculating the characteristics about single triangles */
    a=0;
    z=n-1;
    if ((z-a+n) % n>1)
    {
      A=allocating_a_1_dimensional_array_of_typ_int(n);
      Z=allocating_a_1_dimensional_array_of_typ_int(n);
      i=0;
      A[i]=a;
      Z[i]=z;

      i++;
      while (i>0)
      {
        /* pop element */
        i--;
        help=(A[i]+1) % n;
        while (((V[A[i]][help]==0) ||
                (V[help][Z[i]]==0)) &&
               (help!=Z[i]))
          help=(help+1) % n;
 
        surface_of_triangle=trianglesurface(ND[A[i]]->x,ND[A[i]]->y,
                                            ND[help]->x,ND[help]->y,
                                            ND[Z[i]]->x,ND[Z[i]]->y);
        sum_of_trianglesurface=sum_of_trianglesurface+surface_of_triangle;
        if (surface_of_triangle>maximal_trianglesurface)
          maximal_trianglesurface=surface_of_triangle;
        if (surface_of_triangle<minimal_trianglesurface)
          minimal_trianglesurface=surface_of_triangle;

        circumcircle_of_triangle=circumcircleradius(ND[A[i]]->x,ND[A[i]]->y,
                                                    ND[help]->x,ND[help]->y,
                                                    ND[Z[i]]->x,ND[Z[i]]->y);
        if (circumcircle_of_triangle>maximal_circumcircle)
          maximal_circumcircle=circumcircle_of_triangle;
        if (circumcircle_of_triangle<minimal_circumcircle)
          minimal_circumcircle=circumcircle_of_triangle;


        triangleangles(ND[A[i]]->x,ND[A[i]]->y,
                       ND[help]->x,ND[help]->y,
                       ND[Z[i]]->x,ND[Z[i]]->y,
                       &angle_a,&angle_b,&angle_c);
        if (angle_a>maximal_angle) maximal_angle=angle_a;
        if (angle_b>maximal_angle) maximal_angle=angle_b;
        if (angle_c>maximal_angle) maximal_angle=angle_c;
        if (angle_a<minimal_angle) minimal_angle=angle_a;
        if (angle_b<minimal_angle) minimal_angle=angle_b;
        if (angle_c<minimal_angle) minimal_angle=angle_c;


        if (help==Z[i])
        {
          help=(A[i]+1) % n;
        }
        a=A[i];
        z=Z[i];
        if (help-a>1)
        {
          /* push element */
          A[i]=a;
          Z[i]=help;
          i++;
        }
        if (z-help>1)
        {
          /* push element */
          A[i]=help;
          Z[i]=z;
          i++;
        }
      }
      freeing_a_1_dimensional_array_of_typ_int(A,n);
      freeing_a_1_dimensional_array_of_typ_int(Z,n);
    }

    /* calculating the maximum degree an the number of tips */
    for_all_nodes(inputgraph,node)
    {
      degree=0;
      for_edgelist(node,edge)
      {
        degree++;
      }
      end_for_edgelist(node,edge);
      if (degree>maximal_degree) maximal_degree=degree;
      if (degree==2) number_of_tips++;
    }
    end_for_all_nodes(inputgraph,node);

    /* freeing dataarrays */
    freeing_a_2_dimensional_array_of_typ_int(V,n);
    freeing_a_1_dimensional_array_of_typ_Snode(ND,n);

    /* output of the information */
    message("sum of edgelength:      %13.3f\n",sum_of_edgelength);
    message("minimal edgelength:     %13.3f\n",minimal_edgelength);
    message("maximal edgelength:     %13.3f\n",maximal_edgelength);
    message("minimal surface:        %13.3f\n",minimal_trianglesurface);
    message("maximal surface:        %13.3f\n",maximal_trianglesurface);
    message("minimal angle:          %13.3f\n",minimal_angle*90.0/acos(1));
    message("maximal angle:          %13.3f\n",maximal_angle*90.0/acos(1));
    message("minimal circumcircle:   %13.3f\n",minimal_circumcircle);
    message("maximal circumcircle:   %13.3f\n",maximal_circumcircle); 
    message("height of dualtree:     %13.0f\n",dualtreeheight);
    message("maximal degree:         %13d\n",maximal_degree);
    message("number of ears:         %13d\n",number_of_tips);
    message("needed time:  %6.2f sec\n",
             (double)(endtime_of_algorithm.tms_utime-
                      starttime_of_algorithm.tms_utime)/60.0);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   testing, whether the current graph is a simple polygon    */
/*                                                             */
/***************************************************************/

int simplepolygon_test(Sgraph inputgraph)
{
  Snode node1,node2;                  /* help-nodes             */
  Sedge edge1,edge2;                  /* help-edges             */
  int cr;                             /* help-variables         */
  int n;                              /* number of polygonnodes */

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
    for_all_nodes(inputgraph,node1)
    {
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
                  if ((cr!=0) && (cr!=1) && (cr!=4))
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

    /* creating the array <PolyNodes> an fill it, <n> = number of nodes */
    n=create_an_array_for_the_polygon_nodes(inputgraph);


    /* if n=0 then the inputgraph is not a simple polygon */
    if (n==0)
    {
      error("graph is not a simple polygon!\n");
      return(0);
    }

    if (n==3)
    {
      error("graph is only a triangle!\n");
      return(0);
    }

    /* reset the timer */
    times(&starttime_of_algorithm);

  }
  return(1);
}
/***************************************************************/



/***************************************************************/
/*         generating the visibility information               */
/*                 for the simple polygon                      */
/***************************************************************/

void create_polygonvisibilityinformation(int **V, Snode *ND, int n)
{
  int i,j,k,cr,newk;            /* help-variables */
  int xi,yi,xj,yj;              /* help-variables */
  Snode A,B,C,D;                /* help-nodes     */
  double Winkel_ABC;            /* help-variable  */


  int *EdgesS, *EdgesT;     /* arrays for all possible edges */


  /* allocating dataarrays */

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

  
  for (i=0;i<n;i++)
  {
    xi=ND[i]->x;
    yi=ND[i]->y;
    xj=ND[(i+1) % n]->x;
    yj=ND[(i+1) % n]->y;
    newk=0;
    for (j=0;j<k;j++)
    {
       cr=test_for_an_intersection(xi,yi,xj,yj,
                                   ND[EdgesS[j]]->x,ND[EdgesS[j]]->y,
                                   ND[EdgesT[j]]->x,ND[EdgesT[j]]->y);
       if ((cr>3) || (cr<2))
       {
         EdgesS[newk]=EdgesS[j];
         EdgesT[newk]=EdgesT[j];
         newk++;
       }
    }
    k=newk;
  }

  for (i=0;i<n;i++)
    for (j=0;j<n;j++)
      V[i][j]=0;
  for (i=0;i<k;i++)
  {
    V[EdgesS[i]][EdgesT[i]]=1; 
    V[EdgesT[i]][EdgesS[i]]=1; 
  }


  /* testing, whether a line lies in or outside of the polygon */
  for(i=0;i<n;i++)
  {
    A=ND[(i+n-1) % n];
    B=ND[i];
    C=ND[(i+1) % n];
    Winkel_ABC=angle_ccw(A,B,C);
    for(j=0;j<n;j++)
    {
      if (V[i][j])
      {
        D=ND[j];
        if ((D!=A) && (D!=C) && (angle_ccw(A,B,D)>=Winkel_ABC))
        {
          k--;
          V[i][j]=0; 
          V[j][i]=0;
        } 
      } 
    }
  }
  k=n*(n-1)/2;
  freeing_a_1_dimensional_array_of_typ_int(EdgesS,k);
  freeing_a_1_dimensional_array_of_typ_int(EdgesT,k);
}
/***************************************************************/


/***************************************************************/
/*         generating the visibility information               */
/*                 for the simple polygon                      */
/***************************************************************/

void create_polygonvisibilityinformation2(int **V, Snode *ND, int n)
{
  int i,j,k,cr,exitloop;        /* help-variables */
  int xi,yi,xj,yj;              /* help-variables */
  Snode A,B,C,D;                /* help-nodes     */
  Sedge edge;                   /* help-edges     */
  double Winkel_ABC;            /* help-variable  */


  /* testing all possible edges for intersection with the polygon */
  for (i=0;i<n;i++)
  {
    for (j=i;j<n;j++)
    {
      V[i][j]=0;
      if (i!=j)
      {
        V[i][j]=1;
        xi=ND[i]->x;
        yi=ND[i]->y;
        xj=ND[j]->x;
        yj=ND[j]->y;
        exitloop=0;
        for (k=0;k<n;k++)
        {
          for_sourcelist(ND[(i+k+1)%n],edge)
          {
            cr=test_for_an_intersection(edge->snode->x,edge->snode->y,
                                        edge->tnode->x,edge->tnode->y,
                                        xi,yi,
                                        xj,yj);
            if ((cr<=3) && (cr>=2))
            {
              V[i][j]=0;
              exitloop=1;
            }
          }
          end_for_sourcelist(ND[(i+k+1)%n],edge);
          if (exitloop==1)
            k=n;
        }
      }
      V[j][i]=V[i][j];
    }
  }


  /* testing, whether a line lies in or outside of the polygon */
  for(i=0;i<n;i++)
  {
    A=ND[(i+n-1) % n];
    B=ND[i];
    C=ND[(i+1) % n];
    Winkel_ABC=angle_ccw(A,B,C);
    for(j=0;j<n;j++)
    {
      if (V[i][j])
      {
        D=ND[j];
        if ((D!=A) && (D!=C) && (angle_ccw(A,B,D)>=Winkel_ABC))
        {
          V[i][j]=0; 
          V[j][i]=0;
        } 
      } 
    }
  }
}
/***************************************************************/



/***************************************************************/
/*                                                             */
/*    deleting all edges, which are outside of the polygon     */
/*                                                             */
/***************************************************************/

void remove_outerpolygon(Sgraph inputgraph)
{
  int i,deledgecount;                 /* help-variables               */
  double angle_between_abc_ccw;       /* help-variable                */
  Snode node_a,node_b,node_c,node_d;  /* help-nodes                   */
  Sedge edge;                         /* help-edge                    */
  Sedge *DelEdge;                     /* edge-array for deleting      */
  Snode *ND;                          /* array for the nodes          */
  int n;                              /* number of nodes in the graph */
  
  /* allocating dataarrays */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
  getting_the_polygonnodes(inputgraph,ND,n);
  DelEdge=allocating_a_1_dimensional_array_of_typ_Sedge(3*n);

  /* storing all edges out of the polygon in an array */
  deledgecount=0;
  for(i=0;i<n;i++)
  {
    node_a=ND[(i+n-1) % n];
    node_b=ND[i];
    node_c=ND[(i+1) % n];
    angle_between_abc_ccw=angle_ccw(node_a,node_b,node_c);
    for_edgelist(ND[i],edge) 
    {
      if (unique_edge(edge))
      {
        node_d=edge->tnode;
        if ((node_d!=node_a) && 
            (node_d!=node_c) && 
            (angle_ccw(node_a,node_b,node_d)>=angle_between_abc_ccw))
        {
          DelEdge[deledgecount++]=edge; 
        }
      }
    }
    end_for_edgelist(ND[i],edge); 
  } 

  /* removing all the edges in the array */
  for (i=0;i<deledgecount;i++) 
    remove_edge(DelEdge[i]); 

  /* freeing dataarrays */
  freeing_a_1_dimensional_array_of_typ_Sedge(DelEdge,3*n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n); 

}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      end of polyutil.c                      */  
/*                                                             */
/***************************************************************/
