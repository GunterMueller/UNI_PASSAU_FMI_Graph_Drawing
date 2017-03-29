/***************************************************************/
/*                                                             */
/*  filename:  po_dynamic.c                                    */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains the implemenation of two algorithms   */
/*    for triangulating a simple polygon, based on dynamic     */
/*    programming. the first algorithm uses the standard       */
/*    method, the second is a little modified and solves       */
/*    problems on the dualgraph of a triangulation             */
/*                                                             */
/*  imports:                                                   */
/*   int number_of_nodes_in_the_inputgraph();                  */
/*   Sedge make_a_triangulation_edge();                        */
/*   double edgelength();                                      */
/*   double trianglesurface();                                 */
/*   void triangleangles();                                    */
/*   double circumcircleradius();                              */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   int **allocating_a_2_dimensional_array_of_typ_int();      */
/*   double **allocating_a_2_dimensional_array_of_typ_double();*/
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_2_dimensional_array_of_typ_int();          */
/*   void freeing_a_2_dimensional_array_of_typ_double();       */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*   void create_polygonvisibilityinformation();               */
/*   void getting_the_polygonnodes();                          */
/*                                                             */
/*  exports:                                                   */
/*   double standarddynamic_polygontriangulation();            */
/*   int modifieddynamic_polygontriangulation();               */
/*   void calculating_the_values_for_the_subpolygons_modified();*/
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       include section                       */
/*                                                             */
/***************************************************************/

#include <std.h>
#include <sgraph.h>
#include <math.h>

#include "miscmath_utility.h"
#include "misc_utility.h"
#include "globaldefinitions.h"
#include "memory_allocation.h"
#include "polygon_utility.h"

#include "po_dynamic.h"

/***************************************************************/
/*                                                             */
/*                       local variables                       */
/*                                                             */
/***************************************************************/


Local double balanced_edgelength_in_polygon;
Local double balanced_trianglesurface_in_polygon;
Local double balanced_circumcircleradius_in_polygon;
Local Snode *ND;
Local int n;



/***************************************************************/
/*                                                             */
/*             returns the minimum value of a,b,c              */  
/*                                                             */
/***************************************************************/
double minvalue_of_three_double(double a, double b, double c)
{
  if (a>b) a=b;
  if (a>c) a=c;
  return(a);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*              returns the maximum value of a,b,c             */
/*                                                             */
/***************************************************************/

double maxvalue_of_three_double(double a, double b, double c)
{
  if (a<b) a=b;
  if (a<c) a=c;
  return(a);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*       returns the absolute value of a double-number         */
/*                                                             */
/***************************************************************/

double doubleabsolut(double a)
{
  if (a>=0.0) 
    return(a);
  else 
    return(0.0-a);
}
/***************************************************************/


/***************************************************************/
/*     returns the triangulation-value of the triangle abc     */
/*     for the different kinds of triangulations               */
/***************************************************************/

double value_for_triangle(polygon_triangulation_dynamic typ, int i, int k, int j)
{
  double value, wa, wb, wc;

  value=NULL_DOUBLE;

  switch (typ)
  {
    case minmaxsurface :
      value=trianglesurface(ND[i]->x,ND[i]->y,
                            ND[k]->x,ND[k]->y,
                            ND[j]->x,ND[j]->y);
      break;
    case maxminsurface :
      value=trianglesurface(ND[i]->x,ND[i]->y,
                            ND[k]->x,ND[k]->y,
                            ND[j]->x,ND[j]->y);
      break;
    case getpolygonsurface :
      value=trianglesurface(ND[i]->x,ND[i]->y,
                            ND[k]->x,ND[k]->y,
                            ND[j]->x,ND[j]->y);
      break;
    case balancedsurface :
      value=doubleabsolut(trianglesurface(ND[i]->x,ND[i]->y,
                                          ND[k]->x,ND[k]->y,
                                          ND[j]->x,ND[j]->y)-
                          balanced_trianglesurface_in_polygon);
      break;
    case minmaxcircumcircle :
      value=circumcircleradius(ND[i]->x,ND[i]->y,
                               ND[k]->x,ND[k]->y,
                               ND[j]->x,ND[j]->y);
      break;
    case maxmincircumcircle :
      value=circumcircleradius(ND[i]->x,ND[i]->y,
                               ND[k]->x,ND[k]->y,
                               ND[j]->x,ND[j]->y);
      break;
    case balancedcircumcircle :
      value=doubleabsolut(circumcircleradius(ND[i]->x,ND[i]->y,
                                             ND[k]->x,ND[k]->y,
                                             ND[j]->x,ND[j]->y)-
                          balanced_circumcircleradius_in_polygon);
      break;
    case maxminangle :
      triangleangles(ND[i]->x,ND[i]->y,
                     ND[k]->x,ND[k]->y,
                     ND[j]->x,ND[j]->y,
                     &wa,&wb,&wc);
      value=wa;
      if (wb<value) value=wb;
      if (wc<value) value=wc;
      break;
    case minmaxangle :
      triangleangles(ND[i]->x,ND[i]->y,
                     ND[k]->x,ND[k]->y,
                     ND[j]->x,ND[j]->y,
                     &wa,&wb,&wc);
      value=wa;
      if (wb>value) value=wb;
      if (wc>value) value=wc;
      break;
    case balancedangle :
      triangleangles(ND[i]->x,ND[i]->y,
                     ND[k]->x,ND[k]->y,
                     ND[j]->x,ND[j]->y,
                     &wa,&wb,&wc);
      wa=doubleabsolut(wa-DEGREE_60);
      wb=doubleabsolut(wb-DEGREE_60);
      wc=doubleabsolut(wc-DEGREE_60);
      value=wa+wb+wc;
      break;
    case minlength :
      if (i==j-2) value=NULL_DOUBLE;
      if ((k==i+1) && (k!=j-1))
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y);
      if ((k>i+1) && (k<j-1))
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y)+
              edgelength(ND[i]->x,ND[i]->y,
                         ND[k]->x,ND[k]->y);
      if ((k!=i+1) && (k==j-1))
        value=edgelength(ND[i]->x,ND[i]->y,
                         ND[k]->x,ND[k]->y);
      break;
    case maxlength :
      if (i==j-2) value=NULL_DOUBLE;
      if ((k==i+1) && (k!=j-1))
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y);
      if ((k>i+1) && (k<j-1))
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y)+
              edgelength(ND[i]->x,ND[i]->y,
                         ND[k]->x,ND[k]->y);
      if ((k!=i+1) && (k==j-1))
        value=edgelength(ND[i]->x,ND[i]->y,
                         ND[k]->x,ND[k]->y);
      break;
    case minmaxlength :
      if (i==j-2) value=NULL_DOUBLE;
      if ((k==i+1) && (k!=j-1))
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y);
      if ((k>i+1) && (k<j-1))
      {
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y);
        if (value<edgelength(ND[i]->x,ND[i]->y,
                             ND[k]->x,ND[k]->y))
            value=edgelength(ND[i]->x,ND[i]->y,
                             ND[k]->x,ND[k]->y);
      }
      if ((k!=i+1) && (k==j-1))
        value=edgelength(ND[i]->x,ND[i]->y,
                         ND[k]->x,ND[k]->y);
      break;
    case maxminlength : 
      if (i+2==j) value=1000000000.0;
      if ((k==i+1) && (k+1!=j)) 
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y);
      if ((k>i+1) && (k<j-1)) 
      { 
        value=edgelength(ND[j]->x,ND[j]->y,
                         ND[k]->x,ND[k]->y);
        if (value>edgelength(ND[i]->x,ND[i]->y,
                             ND[k]->x,ND[k]->y))
            value=edgelength(ND[i]->x,ND[i]->y,
                             ND[k]->x,ND[k]->y); 
      }
      if ((k!=i+1) && (k==j-1))
        value=edgelength(ND[i]->x,ND[i]->y,
                         ND[k]->x,ND[k]->y);
      break;
    case balancedlength : 
      if (i==j-2) value=NULL_DOUBLE;
      if ((k==i+1) && (k!=j-1)) 
        value=doubleabsolut(edgelength(ND[j]->x,ND[j]->y,
                                       ND[k]->x,ND[k]->y)-
                            balanced_edgelength_in_polygon);
      if ((k>i+1) && (k<j-1)) 
      {
        value=doubleabsolut(edgelength(ND[j]->x,ND[j]->y,
                                       ND[k]->x,ND[k]->y)-
                            balanced_edgelength_in_polygon);
                 
        if (value<doubleabsolut(edgelength(ND[i]->x,ND[i]->y,
                                           ND[k]->x,ND[k]->y)-
                                balanced_edgelength_in_polygon))
           value=doubleabsolut(edgelength(ND[i]->x,ND[i]->y,
                                          ND[k]->x,ND[k]->y)-
                               balanced_edgelength_in_polygon);
      }
      if ((k!=i+1) && (k==j-1)) 
        value=doubleabsolut(edgelength(ND[i]->x,ND[i]->y,
                                       ND[k]->x,ND[k]->y)-
                            balanced_edgelength_in_polygon);
      break;
    case thin : 
      if (i==j-2) value=1.0;
      break;
    case bushy : 
      if (i==j-2) value=1.0;
      break;
    case mindualheight :
      break;
    case minmaxexz : 
      break;
    case maxmaxexz : 
      break;
  }
  return(value);
}
/***************************************************************/


/***************************************************************/
/*     returns the value of the better subpolygon for the      */
/*     different kinds of polygon triangulation                */
/***************************************************************/

int better_value(polygon_triangulation_dynamic typ, double a, double b)
{
  int value;

  value=0;
  switch (typ)
  {
    case maxminangle          : if (a>b) value=1; break;
    case minmaxangle          : if (a<b) value=1; break;
    case balancedangle        : if (a<b) value=1; break;
    case maxminsurface        : if (a>b) value=1; break;
    case minmaxsurface        : if (a<b) value=1; break;
    case getpolygonsurface    : if (a>b) value=1; break;
    case balancedsurface      : if (a<b) value=1; break;
    case maxmincircumcircle   : if (a>b) value=1; break;
    case minmaxcircumcircle   : if (a<b) value=1; break;
    case balancedcircumcircle : if (a<b) value=1; break;
    case bushy                : if (a>b) value=1; break;
    case thin                 : if (a<b) value=1; break;
    case minlength            : if (a<b) value=1; break;
    case maxlength            : if (a>b) value=1; break;
    case minmaxlength         : if (a<b) value=1; break;
    case maxminlength         : if (a>b) value=1; break;
    case balancedlength       : if (a<b) value=1; break;
    case mindualheight        : if (a<b) value=1; break;
    case minmaxexz            : if (a<b) value=1; break;
    case maxmaxexz            : if (a>b) value=1; break;
  }
  return(value);
}
/***************************************************************/


/***************************************************************/
/*   returns the infinitly bad value for the different kinds   */               
/*   of polygon triangulations                                 */
/***************************************************************/

double infinitly_bad(polygon_triangulation_dynamic typ)
{
  double value;

  switch(typ)
  {
    case maxminangle          : value=NEGATIV_DOUBLE_INFINITLY; break;
    case minmaxangle          : value=POSITIV_DOUBLE_INFINITLY; break;
    case balancedangle        : value=POSITIV_DOUBLE_INFINITLY; break;
    case maxminsurface        : value=NEGATIV_DOUBLE_INFINITLY; break;
    case minmaxsurface        : value=POSITIV_DOUBLE_INFINITLY; break;
    case getpolygonsurface    : value=NEGATIV_DOUBLE_INFINITLY; break;
    case balancedsurface      : value=POSITIV_DOUBLE_INFINITLY; break;
    case maxmincircumcircle   : value=NEGATIV_DOUBLE_INFINITLY; break;
    case minmaxcircumcircle   : value=POSITIV_DOUBLE_INFINITLY; break;
    case balancedcircumcircle : value=POSITIV_DOUBLE_INFINITLY; break;
    case thin                 : value=POSITIV_DOUBLE_INFINITLY; break;
    case bushy                : value=NEGATIV_DOUBLE_INFINITLY; break;
    case minlength            : value=POSITIV_DOUBLE_INFINITLY; break;
    case maxlength            : value=NEGATIV_DOUBLE_INFINITLY; break;
    case minmaxlength         : value=POSITIV_DOUBLE_INFINITLY; break;
    case maxminlength         : value=NEGATIV_DOUBLE_INFINITLY; break;
    case balancedlength       : value=POSITIV_DOUBLE_INFINITLY; break;
    case mindualheight        : value=POSITIV_DOUBLE_INFINITLY; break;
    case minmaxexz            : value=POSITIV_DOUBLE_INFINITLY; break;
    case maxmaxexz            : value=NEGATIV_DOUBLE_INFINITLY; break;
  }
  return(value);
}
/***************************************************************/


/***************************************************************/
/*  returns the value for a degenerate subpolygon of length 2  */
/***************************************************************/

double value_for_subpolygon_of_length_2(polygon_triangulation_dynamic typ)
{
  double value;

  switch(typ)
  {
    case maxminangle          : value = POSITIV_DOUBLE_INFINITLY; break;
    case minmaxangle          : value = NEGATIV_DOUBLE_INFINITLY; break;
    case balancedangle        : value = NULL_DOUBLE; break;
    case maxminsurface        : value = POSITIV_DOUBLE_INFINITLY; break;
    case minmaxsurface        : value = NEGATIV_DOUBLE_INFINITLY; break;
    case getpolygonsurface    : value = NULL_DOUBLE; break;
    case balancedsurface      : value = NEGATIV_DOUBLE_INFINITLY; break;
    case maxmincircumcircle   : value = POSITIV_DOUBLE_INFINITLY; break;
    case minmaxcircumcircle   : value = NEGATIV_DOUBLE_INFINITLY; break;
    case balancedcircumcircle : value = NEGATIV_DOUBLE_INFINITLY; break;
    case thin                 : value = NULL_DOUBLE; break;
    case bushy                : value = NULL_DOUBLE; break;
    case minlength            : value = NULL_DOUBLE; break;
    case maxlength            : value = NULL_DOUBLE; break;
    case minmaxlength         : value = NEGATIV_DOUBLE_INFINITLY; break;
    case maxminlength         : value = POSITIV_DOUBLE_INFINITLY; break;
    case balancedlength       : value = NEGATIV_DOUBLE_INFINITLY; break;
   case mindualheight         : value = NULL_DOUBLE; break;
    case minmaxexz            : value = NULL_DOUBLE; break;
    case maxmaxexz            : value = NULL_DOUBLE; break;
  }
  return(value);
}
/***************************************************************/


/***************************************************************/
/*   returns the whole value if connecting the subpolygon <a>  */
/*   and <c> and the middle triangle <b> for the different     */
/*   kinds of polygontriangulation                             */
/***************************************************************/

double wholevalue(polygon_triangulation_dynamic typ, double a, double b, double c)
{
  double value;

  switch(typ)
  {
    case minmaxangle : 
      value=maxvalue_of_three_double(a,b,c);
      break;
    case maxminangle : 
      value=minvalue_of_three_double(a,b,c);
      break;
    case balancedangle : 
      value=a+b+c;
      break;
    case maxminsurface :
      value=minvalue_of_three_double(a,b,c);
      break;
    case minmaxsurface :
      value=maxvalue_of_three_double(a,b,c);
      break;
    case getpolygonsurface : 
      value=a+b+c;
      break;
    case balancedsurface : 
      value=maxvalue_of_three_double(a,b,c);
      break;
    case maxmincircumcircle : 
      value=minvalue_of_three_double(a,b,c);
      break;
    case minmaxcircumcircle : 
      value=maxvalue_of_three_double(a,b,c);
      break;
    case balancedcircumcircle :
      value=maxvalue_of_three_double(a,b,c);
      break;
    case maxminlength : 
      value=minvalue_of_three_double(a,b,c);
      break;
    case minmaxlength : 
      value=maxvalue_of_three_double(a,b,c);
      break;
    case balancedlength : 
      value=maxvalue_of_three_double(a,b,c);
      break;
    case bushy : 
      value=a+b+c;
      break;
    case thin : 
      value=a+b+c;
      break;
    case minlength :
      value=a+b+c;
      break;
    case maxlength : 
      value=a+b+c;
      break;
    case mindualheight : 
      if (a>b) 
        value=a+1.0;
      else 
        value=b+1.0;
      break;
    case minmaxexz :
      if (a>b) 
        value=a+1.0;
      else 
        value=b+1.0;
      break;
    case maxmaxexz : 
      if (a>b) 
        value=a+1.0;
      else 
        value=b+1.0;
      break;
  }
  return(value);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  this procedure fills the Q- and C-Tables with the optimal  */
/*  triangulationinformation for the subpolygons               */
/*                                                             */
/***************************************************************/

void calculating_the_values_for_the_subpolygons(polygon_triangulation_dynamic typ, int **V, int **C, double **Q, int n)
{
  int i,j,k,d;          /* help-variables */

  for (i=0;i<n-1;i++)
    Q[i][i+1]=value_for_subpolygon_of_length_2(typ);

  for (i=0;i<n-2;i++)
    if (V[i][i+2])
    {
      Q[i][i+2]=value_for_triangle(typ,i,i+1,i+2);
      C[i][i+2]=i+1;
    }

  for (d=3;d<n;d++)
  {
    for (i=0;i<n-d;i++)
    {
      j=i+d;
      Q[i][j]=infinitly_bad(typ);
      if (V[i][j])
      {
        for (k=i+1;k<j;k++)
        {
          if ((V[i][k]) && (V[k][j]))
          {
            if (better_value(typ,wholevalue(typ,Q[i][k],
                                                Q[k][j],
                                                value_for_triangle(typ,i,k,j)),
                             Q[i][j]))
            {
              Q[i][j]=wholevalue(typ,Q[i][k],
                                 Q[k][j],
                                 value_for_triangle(typ,i,k,j));
              C[i][j]=k;
            }
          }
        }
      }
    }
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*    this procedure makes for some criteria the necessary     */
/*    initial calculations.                                    */
/*                                                             */
/***************************************************************/

void make_initial_calculations(polygon_triangulation_dynamic typ, int **V, int **C, double **Q, int n)
{
  double minmax_edgelength;
  double maxmin_edgelength;
  double minmax_circumcircleradius;
  double maxmin_circumcircleradius;


  switch (typ)
  {
    /* balancedlength: calulating the minimal and maximal egdelength */
    case balancedlength :

      /* filling the tables with dynamic programming */
      calculating_the_values_for_the_subpolygons(minmaxlength,V,C,Q,n);
      minmax_edgelength=Q[0][n-1];

      /* filling the tables with dynamic programming */
      calculating_the_values_for_the_subpolygons(maxminlength,V,C,Q,n);
      maxmin_edgelength=Q[0][n-1];
      balanced_edgelength_in_polygon=(minmax_edgelength+maxmin_edgelength)/2.0;
      break;

    /* balancedcircumcircle: calulating the minimal and maximal circumcircleradius */
    case balancedcircumcircle : 

      /* filling the tables with dynamic programming */
      calculating_the_values_for_the_subpolygons(minmaxcircumcircle,V,C,Q,n);
      minmax_circumcircleradius=Q[0][n-1];

      /* filling the tables with dynamic programming */
      calculating_the_values_for_the_subpolygons(maxmincircumcircle,V,C,Q,n);
      maxmin_circumcircleradius=Q[0][n-1];
      balanced_circumcircleradius_in_polygon=(minmax_circumcircleradius+
                                              maxmin_circumcircleradius)/2.0;
      break;

    /* balancedsurface: calulating the medium for a triangle */
    case balancedsurface :

      /* filling the tables with dynamic programming */
      calculating_the_values_for_the_subpolygons(getpolygonsurface,V,C,Q,n);
      balanced_trianglesurface_in_polygon=Q[0][n-1]/(n-2);
      break;
    default: break;
  }
}
/***************************************************************/


/***************************************************************/
/*    triangulating a simple polygon with standard dynamic     */
/*    programming for many different kinds.                    */
/*    generates an optimal triangulation and returns the       */
/*    optimal value.                                           */
/***************************************************************/

double standarddynamic_polygontriangulation(Sgraph inputgraph, polygon_triangulation_dynamic typ)
{
  int i,k,a,z;    /* help-variable                */
  int *A, *Z;              /* stack for the reconstruction */
  int **C;                 /* the C-table                  */
  double **Q;              /* the Q-table                  */
  double returnvalue;      /* the optimal value            */
  int **V;                 /* visibility information         */


  /* allocating the dataarrays */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
  getting_the_polygonnodes(inputgraph,ND,n);

  V=allocating_a_2_dimensional_array_of_typ_int(n);
  C=allocating_a_2_dimensional_array_of_typ_int(n);
  Q=allocating_a_2_dimensional_array_of_typ_double(n);

  /* creating the visibility information */
  create_polygonvisibilityinformation(V,ND,n);

  /* make the necessary initial calculations */
  make_initial_calculations(typ,V,C,Q,n);

  /* filling the tables with dynamic programming */
  calculating_the_values_for_the_subpolygons(typ,V,C,Q,n);


  /* constructing the triangulation from */
  /* the information of the C-table      */

  A=allocating_a_1_dimensional_array_of_typ_int(n);
  Z=allocating_a_1_dimensional_array_of_typ_int(n);
  i=0;
  A[i]=0;
  Z[i]=n-1;
  i++;
  while (i>0)
  {
    /* pop element */
    i--;
    k=C[A[i]][Z[i]];
    a=A[i];
    z=Z[i];
    if (k-a>1)
    {
      /* push element */
      make_a_triangulation_edge(ND[a],ND[k]);
      A[i]=a;
      Z[i]=k;
      i++;
    }
    if (z-k>1)
    {
      /* push element */
      make_a_triangulation_edge(ND[k],ND[z]);
      A[i]=k;
      Z[i]=z;
      i++;
    }
  }

  returnvalue=Q[0][n-1];

  /* freeing the dataarrays */
  freeing_a_1_dimensional_array_of_typ_int(A,n);
  freeing_a_1_dimensional_array_of_typ_int(Z,n);
  freeing_a_2_dimensional_array_of_typ_int(C,n);
  freeing_a_2_dimensional_array_of_typ_double(Q,n);
  freeing_a_2_dimensional_array_of_typ_int(V,n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);

  return(returnvalue);
}
/***************************************************************/


/***************************************************************/
/* returns the whole value if connecting the three subpolygons */
/* <a>, <b> and <c> for the different kinds of polygon-        */
/* triangulation, which depend on the dualtree                 */
/***************************************************************/

double connecting_three_subpolygons(polygon_triangulation_dynamic typ, double a, double b, double c)
{
  double value,m;
  int help;

  value=NULL_DOUBLE;
  switch (typ)
  {
    case minmaxexz : 
      m=a;
      if (b>m) m=b;
      if (c>m) m=c;

      help=0;
      if (a>m-2) help++;
      if (b>m-2) help++;
      if (c>m-2) help++;
      if (help>1) 
      {
        value=a+b;
        if (a+c>value) value=a+c;
        if (b+c>value) value=b+c;
        value=value+2.0;

      }
      else value=infinitly_bad(typ);
      break;
    case maxmaxexz : 
      value=a+b;
      if (a+c>value) value=a+c;
      if (b+c>value) value=b+c;
      value=value+1.0;

      break;
    case mindualheight : 
      value=a;
      if (b>value) value=b;
      if (c>value) value=c;
      value=value+1.0;

      break;
     default: break;
  }
  return(value);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  this procedure fills the Q- and C-Tables with the optimal  */
/*  triangulationinformation for the subpolygons (modified)    */
/*                                                             */
/***************************************************************/

void calculating_the_values_for_the_subpolygons_modified(polygon_triangulation_dynamic typ, int **V, int **C, double **Q, int n)
{
  int i,j,k,d;          /* help-variables */

  for (i=0;i<n;i++)
    Q[i][(i+1) % n]=NULL_DOUBLE;

  for (i=0;i<n;i++)
    if (V[i][(i+2) % n])
    {
      Q[i][(i+2) % n]=value_for_triangle(typ,i,(i+1) % n,(i+2) % n);
      C[i][(i+2) % n]=(i+1) % n;
    }

  for (d=3;d<n;d++)
  {
    for (i=0;i<n;i++)
    {
      j=(i+d) % n;
      Q[i][j]=infinitly_bad(typ);
      if (V[i][j])
      {
        k=i;
        while (k!=(j-1+n) % n)
        {
          k=(k+1) % n;
          if ((V[i][k]) && (V[k][j]))
          {
            if (better_value(typ,wholevalue(typ,Q[i][k],
                                        Q[k][j],
                                        value_for_triangle(typ,i,k,j)),
                             Q[i][j]))
            {
              Q[i][j]=wholevalue(typ,Q[i][k],
                                     Q[k][j],
                                     value_for_triangle(typ,i,k,j));
              C[i][j]=k;
            }
          }
        }
      }
    }
  }
}
/***************************************************************/


/***************************************************************/
/*    triangulating a simple polygon with modified dynamic     */
/*    programming for criteria that depend on the dualtree     */
/*    generates an optimal triangulation and returns the       */
/*    optimal value.                                           */
/***************************************************************/

int modifieddynamic_polygontriangulation(Sgraph inputgraph, polygon_triangulation_dynamic typ)
{
  int i,help,a,x,y,z;    /* help-variable                */
  int bestx,besty,bestz;       /* help-variable                */
  double optvalue,koptvalue;   /* help-variable                */
  int *A,*Z;                   /* stack for the reconstruction */
  int **C;                     /* the C-table                  */
  double **Q;                  /* the Q-table                  */
  int **V;                     /* visibility information       */


  /* allocating the dataarrays */
  n=number_of_nodes_in_the_inputgraph(inputgraph);
  ND=allocating_a_1_dimensional_array_of_typ_Snode(n);
  getting_the_polygonnodes(inputgraph,ND,n);

  V=allocating_a_2_dimensional_array_of_typ_int(n);
  C=allocating_a_2_dimensional_array_of_typ_int(n);
  Q=allocating_a_2_dimensional_array_of_typ_double(n);
  
  /* creating the visibility information */
  create_polygonvisibilityinformation(V,ND,n);

  /* filling the tables with dynamic programming */
  calculating_the_values_for_the_subpolygons_modified(typ,V,C,Q,n);


  /* constructing the triangulation from */
  /* the information of the C-table      */

  optvalue=infinitly_bad(typ);
  for(x=0;x<n;x++)
   for(y=x+1;y<n;y++)
     for(z=y+1;z<n;z++)
       if ((V[x][y]) && (V[y][z]) && (V[z][x]))
       {

         koptvalue=connecting_three_subpolygons(typ,Q[x][y],
                                                    Q[y][z],
                                                    Q[z][x]);
         if (better_value(typ,koptvalue,optvalue))
         {
           optvalue=koptvalue;
           bestx=x;
           besty=y;
           bestz=z;
         }
       }
  A=allocating_a_1_dimensional_array_of_typ_int(n);
  Z=allocating_a_1_dimensional_array_of_typ_int(n);
  i=0;
  A[i]=bestx;
  Z[i]=besty;
  i++;
  A[i]=besty;
  Z[i]=bestz;
  i++;
  A[i]=bestz;
  Z[i]=bestx;
  i++;
  while (i>0)
  {
    /* pop element */
    i--;
    a=A[i];
    z=Z[i];
    if ((z-a+n)%n>1)
    {
      help=C[A[i]][Z[i]];
      make_a_triangulation_edge(ND[a],ND[z]);
      if ((help-a+n)%n>1)
      {
        /* push element */
        A[i]=a;
        Z[i]=help;
        i++;
      }
      if ((z-help+n)%n>1)
      {
        /* push element */
        A[i]=help;
        Z[i]=z;
        i++;
      }
    }
  }
  
  /* freeing the dataarrays */

  freeing_a_1_dimensional_array_of_typ_int(A,n);
  freeing_a_1_dimensional_array_of_typ_int(Z,n);
  freeing_a_2_dimensional_array_of_typ_int(C,n);
  freeing_a_2_dimensional_array_of_typ_double(Q,n);
  freeing_a_2_dimensional_array_of_typ_int(V,n);
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);
  return(rint(optvalue));
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                     end of po_dynamic.c                     */
/*                                                             */
/***************************************************************/
