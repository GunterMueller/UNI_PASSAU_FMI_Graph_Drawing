/***************************************************************/
/*                                                             */
/*  filename:  po_minmaxdegree.c                               */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implementation of an algorithm    */
/*    that produces a triangulation of a simple polygon        */
/*    with a minimal maximal degree of the nodes.              */
/*    it uses dynamic programming.                             */
/*                                                             */
/*  imports:                                                   */
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge make_a_triangulation_edge();                        */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   int **allocating_a_2_dimensional_array_of_typ_int();      */
/*   int ***allocating_a_3_dimensional_array_of_typ_int();     */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_2_dimensional_array_of_typ_int();          */
/*   void freeing_a_3_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*   void create_polygonvisibilityinformation();               */
/*   void getting_the_polygonnodes();                          */
/*                                                             */
/*  exports:                                                   */
/*    int polygontriangulation_minmaxdegree();                 */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       include section                       */
/*                                                             */
/***************************************************************/

#include <std.h>
#include <sgraph.h>

#include "misc_utility.h"
#include "globaldefinitions.h"
#include "memory_allocation.h"
#include "polygon_utility.h"


#define INFINITLY_DEGREE 100000


/***************************************************************/
/*  this function tests, wether a triangulation with a maximum */
/*  degree smaller or equal than K exists or not.              */
/*  it returns 1, if there such a triangulation, 0 if not      */
/*                                                             */
/*  Fk is an 3-dimensional array for the degreevalues of the   */
/*     subpolygons                                             */
/*  P is an 3-dimensional array which contains the information */
/*    to construct an triangulation with maximal degree <= K   */
/*  K is the degreevalue to test                               */
/***************************************************************/

int minmaxdegree_smaller_or_equal(int **V, int ***Fk, int ***P, int K, int *pwert, int n)
{
  int i,j,d,ii,p;      /* help-variables */

  /* filling the tables */
  for (j=0;j<K+1;j++)
    for (i=0;i<n-1;i++)
      if (j<1)
        Fk[i][i+1][j]=INFINITLY_DEGREE;
      else
        Fk[i][i+1][j]=1;

  for (j=0;j<K+1;j++)
    for (i=0;i<n-2;i++)
      if (j<2)
        Fk[i][i+2][j]=INFINITLY_DEGREE;
      else
      {
        Fk[i][i+2][j]=2;
        P[i][i+2][j]=i+1;
      }

  for (d=3;d<n;d++)
  {
    for (i=0;i<n-d;i++)
    {
      j=i+d;
      Fk[i][j][1]=INFINITLY_DEGREE;
      for (ii=2;ii<K+1;ii++)
      {
        Fk[i][j][ii]=INFINITLY_DEGREE;
        if (V[i][j])
        {
          for (p=i+1;p<j;p++)
          {
            if ((V[i][p]) && (V[p][j]))
            {
              if (K-Fk[i][p][ii-1]>0)
              if (Fk[p][j][K-Fk[i][p][ii-1]]+1<Fk[i][j][ii])
              {
                Fk[i][j][ii]=Fk[p][j][K-Fk[i][p][ii-1]]+1;
                P[i][j][ii]=p;
              }
            }
          }
        }
      }
    }
  }
  for (i=1;i<K+1;i++)
    if (Fk[0][n-1][i]<=K)
    {
      *pwert=i;
      return(1);
    }
  return(0);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/* this function creates a triangulation with minmax degree    */
/* using dynamic programming and other technics.               */
/*                                                             */
/***************************************************************/

int polygontriangulation_minmaxdegree(Sgraph inputgraph)
{
  int i,help,a,z;            /* help-variables                 */
  int K,uG,oG,pwert;         /* help-variables                 */
  int possible,returnvalue;  /* help-variables                 */
  int *A,*Z,*PW;             /* stack for the reconstruction   */
  int ***Fk;                 /* max degrees of subpolygons     */
  int ***P;                  /* information for reconstruction */
  int **V;                   /* visibility information         */
  Snode *ND;                 /* array for the nodes            */
  int n;                     /* number of nodes in the graph   */


  /* allocating the dataarrays */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
  getting_the_polygonnodes(inputgraph,ND,n);

  V=allocating_a_2_dimensional_array_of_typ_int(n);

  /* creating the visibilityinformation */
  create_polygonvisibilityinformation(V,ND,n);

  /* searching for an upper bound */
  oG=2;
  do
  {
    uG=oG;
    oG=oG*2;
    /* allocating data arrays */
    Fk=allocating_a_3_dimensional_array_of_typ_int(n,n,oG+1);
    P=allocating_a_3_dimensional_array_of_typ_int(n,n,oG+1);

    /* test for a triangulation with minmax degree <= oG */
    possible=minmaxdegree_smaller_or_equal(V,Fk,P,oG,&pwert,n);

    /* freeing dataarrays */
    freeing_a_3_dimensional_array_of_typ_int(Fk,n,n,oG+1);
    freeing_a_3_dimensional_array_of_typ_int(P,n,n,oG+1);
  }
  while (possible==0);

  /* searching for the optimal value of K */
  possible=0;
  while (oG>uG+1)
  {
    K=(oG+uG+1) / 2;

    /* allocating dataarrays */
    Fk=allocating_a_3_dimensional_array_of_typ_int(n,n,K+1);
    P=allocating_a_3_dimensional_array_of_typ_int(n,n,K+1);

    /* test for a triangulation with minmax degree <= oG */
    possible=minmaxdegree_smaller_or_equal(V,Fk,P,K,&pwert,n);

    if (possible)
      oG=K;
    else
      uG=K;

    /* freeing dataarrays */
    freeing_a_3_dimensional_array_of_typ_int(Fk,n,n,K+1);
    freeing_a_3_dimensional_array_of_typ_int(P,n,n,K+1);
  }
  K=oG;

  /* allocating dataarrays */
  Fk=allocating_a_3_dimensional_array_of_typ_int(n,n,K+1);
  P=allocating_a_3_dimensional_array_of_typ_int(n,n,K+1);

  /* generating the table-information of a optimal triangulation */
  possible=minmaxdegree_smaller_or_equal(V,Fk,P,oG,&pwert,n);


  /* constructing the triangulation from */
  /* the information of the P-table      */
  A=allocating_a_1_dimensional_array_of_typ_int(n);
  Z=allocating_a_1_dimensional_array_of_typ_int(n);
  PW=allocating_a_1_dimensional_array_of_typ_int(n);
  i=0;
  A[i]=0;
  Z[i]=n-1;
  PW[i]=pwert;
  i++;
  while (i>0)
  {
    i--;
    help=P[A[i]][Z[i]][PW[i]];
    a=A[i];
    z=Z[i];
    pwert=PW[i];
    if (help-a>1)
    {
      make_a_triangulation_edge(ND[a],ND[help]);
      A[i]=a;
      Z[i]=help;
      PW[i]=pwert-1;
      i++;
    }
    if (z-help>1)
    {
      make_a_triangulation_edge(ND[help],ND[z]);
      A[i]=help;
      Z[i]=z;
      PW[i]=K-Fk[a][help][pwert-1];
      i++;
    }
  }

  returnvalue=oG;

  /* freeing the dataarrays */
  freeing_a_1_dimensional_array_of_typ_int(A,n);
  freeing_a_1_dimensional_array_of_typ_int(Z,n);
  freeing_a_1_dimensional_array_of_typ_int(PW,n);
  freeing_a_3_dimensional_array_of_typ_int(Fk,n,n,K+1);
  freeing_a_3_dimensional_array_of_typ_int(P,n,n,K+1);
  freeing_a_2_dimensional_array_of_typ_int(V,n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);

  return(returnvalue);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                   end of po_minmagdegree.c                  */
/*                                                             */
/***************************************************************/
