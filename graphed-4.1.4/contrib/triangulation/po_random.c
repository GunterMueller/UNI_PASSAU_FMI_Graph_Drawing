/***************************************************************/
/*                                                             */
/*  filename:  po_random.c                                     */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implementation of an algorithm    */
/*    that produces each possible triangulation of a simple    */
/*    polygon with the same possiblity                         */
/*                                                             */
/*  imports:                                                   */
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge make_a_triangulation_edge();                        */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   int **allocating_a_2_dimensional_array_of_typ_int();      */
/*   double *allocating_a_1_dimensional_array_of_typ_double(); */
/*   double **allocating_a_2_dimensional_array_of_typ_double();*/
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_2_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_double();       */
/*   void freeing_a_2_dimensional_array_of_typ_double();       */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*   void create_polygonvisibilityinformation();               */
/*                                                             */
/*  exports:                                                   */
/*      void random_polygontriangulation_uniform();            */
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
#include "misc_utility.h"
#include "globaldefinitions.h"
#include "memory_allocation.h"
#include "polygon_utility.h"
#include "sgraph/random.h"


/***************************************************************/
/*                                                             */
/*  counting the number of various triangulations of a simple  */
/*  polygon using dynamic programming.                         */
/*                                                             */
/***************************************************************/

double count_triangulations(double **Q, int **V, int n)
{
  int i,j,k,d;

  for (i=0;i<n-1;i++)
    Q[i][i+1]=1.0;

  for (i=0;i<n-2;i++)
    if (V[i][i+2])
      Q[i][i+2]=1;
    else
      Q[i][i+2]=0;

  for (d=3;d<n;d++)
    for (i=0;i<n-d;i++)
    {
      j=i+d;
      Q[i][j]=0;
      if (V[i][j])
        for (k=i+1;k<j;k++)
          if ((V[i][k]) && (V[k][j]))
            Q[i][j]=Q[i][j]+Q[i][k]*Q[k][j];
    }
  return(Q[0][n-1]);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   creating the with <number> specified triangulation of a   */
/*   simple polygon. every number stands for a different       */
/*   triangulation.                                            */
/*                                                             */
/***************************************************************/

void create_randompolygon(double number, int a, int z, double **Q, Snode *ND, int n)
{
  double *NUMBER;                         /* stack          */ 
  int *A,*Z;                              /* stack          */
  int i,help;                             /* help-variables */
  double value,number1,number2,oldvalue;  /* help-variables */

  /* allocating the stack */
  NUMBER=allocating_a_1_dimensional_array_of_typ_double(n);
  A=allocating_a_1_dimensional_array_of_typ_int(n);
  Z=allocating_a_1_dimensional_array_of_typ_int(n);

  i=0;
  NUMBER[i]=number;
  A[i]=a;
  Z[i]=z;
  i++;
  while (i>0)
  {
    /* pop element */
    i--;
    a=A[i];
    z=Z[i];
    number=NUMBER[i];

    value=0.0;
    help=a;
    do
    {                        
      oldvalue=value;
      help++;
      value=value+Q[a][help]*Q[help][z];
    }
    while(value<number);
    number=number-oldvalue;
    number1=fmod(number-1,Q[a][help])+1.0;
    number2=floor((number-1)/Q[a][help])+1.0;

    if (help-a>1)
    {
      /* push element */
      make_a_triangulation_edge(ND[a],ND[help]);
      NUMBER[i]=number1;
      A[i]=a;
      Z[i]=help;
      i++;
    }
    if (z-help>1)
    {
      /* push element */
      make_a_triangulation_edge(ND[help],ND[z]);
      NUMBER[i]=number2;
      A[i]=help;
      Z[i]=z;
      i++;
    }
  }

  /* freeing the stack */
  freeing_a_1_dimensional_array_of_typ_double(NUMBER,n);
  freeing_a_1_dimensional_array_of_typ_int(A,n);
  freeing_a_1_dimensional_array_of_typ_int(Z,n);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*    random triangulation of simple polygons with uniform     */
/*    distribution over all possible triangulations            */
/*                                                             */
/***************************************************************/

double random_polygontriangulation_uniform(Sgraph inputgraph)
{
  double count,number;    /* helpvariable                      */
  double **Q;             /* array for the number of           */
                          /* triangulations of the subpolygons */
  int **V;                /* datastructure for the visibility  */
                          /* information                       */
  Snode *ND;              /* array for the nodes               */
  int n;                  /* number of nodes in the graph      */
  
  /* allocating dataarrays */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
  getting_the_polygonnodes(inputgraph,ND,n);

  V=allocating_a_2_dimensional_array_of_typ_int(n);
  Q=allocating_a_2_dimensional_array_of_typ_double(n);
  
  /* creating the visibility information */
  create_polygonvisibilityinformation(V,ND,n);

  /* counting the number of various triangulations */
  count=count_triangulations(Q,V,n);

  /* selecting randomly one triangulation-number */
  /* must be change for a better random routine  */
  number=fmod((double)random()*(double)random(),count)+1.0;

  /* generating the selected triangulation */
  create_randompolygon(number,0,n-1,Q,ND,n);

  /* freeing the dataarrays */
  freeing_a_2_dimensional_array_of_typ_double(Q,n);
  freeing_a_2_dimensional_array_of_typ_int(V,n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);

  return(count);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of po_random.c                    */
/*                                                             */
/***************************************************************/
