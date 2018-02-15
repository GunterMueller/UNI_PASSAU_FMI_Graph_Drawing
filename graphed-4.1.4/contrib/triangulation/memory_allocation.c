/***************************************************************/
/*                                                             */
/*  filename:  memory_allocation.c                             */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*    this file contains many routines for allocating and      */
/*    freeing dataarrays and matrizes at running-time          */
/*                                                             */
/*  imports:                                                   */
/*    -                                                        */
/*                                                             */
/*  exports:                                                   */
/*    void print_memoryerrormessage();                         */
/*    int *allocating_a_1_dimensional_array_of_typ_int();      */
/*    int **allocating_a_2_dimensional_array_of_typ_int();     */
/*    int ***allocating_a_3_dimensional_array_of_typ_int();    */
/*    double *allocating_a_1_dimensional_array_of_typ_double();*/
/*    double **allocating_a_2_dimensional_array_of_typ_double();*/
/*    Snode *allocating_a_1_dimensional_array_of_typ_Snode();  */
/*    Sedge *allocating_a_1_dimensional_array_of_typ_Sedge();  */
/*    void freeing_a_1_dimensional_array_of_typ_int();         */
/*    void freeing_a_2_dimensional_array_of_typ_int();         */
/*    void freeing_a_3_dimensional_array_of_typ_int();         */
/*    void freeing_a_1_dimensional_array_of_typ_double();      */
/*    void freeing_a_2_dimensional_array_of_typ_double();      */
/*    void freeing_a_1_dimensional_array_of_typ_Snode();       */
/*    void freeing_a_1_dimensional_array_of_typ_Sedge();       */
/*                                                             */
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                      include section                        */
/*                                                             */
/***************************************************************/

#include "std.h"
#include "sgraph.h"



/***************************************************************/
/*                                                             */
/*  printing an errormessage, if the memoryallocation          */
/*  is not successful                                          */    
/*                                                             */
/***************************************************************/

void print_memoryerrormessage(void)
{
  fprintf(stderr,"memory-allocation-error.\n");
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  allocating a 1 dimensional array for values of typ <int>   */
/*                                                             */
/***************************************************************/

int *allocating_a_1_dimensional_array_of_typ_int(int n)
{
  int error;         /* help-variale                           */
  int *array;        /* pointer to the allocated datastructure */

  error=0;
  if ((array=(int*) calloc(n,sizeof(int)))==NULL)
    error=1;

  if (error)
  {
    print_memoryerrormessage();
    return(NULL);
  }
  else
  {
    return(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  allocating a 2 dimensional array for values of typ <int>   */ 
/*                                                             */
/***************************************************************/

int **allocating_a_2_dimensional_array_of_typ_int(int n)
{
  int i,error;       /* help-variales                          */
  int **array;       /* pointer to the allocated datastructure */

  error=0;
  if ((array=(int**) calloc(n,sizeof(*array)))!=NULL)
  {
    for (i=0;i<n;i++)
      if ((array[i]=(int*) calloc(n,sizeof(int)))==NULL)
        error=1;
  }
  else error=1;

  if (error)
  {
    print_memoryerrormessage();
    return(NULL);
  }
  else
  {
    return(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  allocating a 3 dimensional array for values of typ <int>   */                   
/*                                                             */
/***************************************************************/

int ***allocating_a_3_dimensional_array_of_typ_int(int n, int m, int k)
{
  int i,j,error;     /* help-variales                          */
  int ***array;      /* pointer to the allocated datastructure */

  error=0;
  if ((array=(int***) calloc(n,sizeof(**array)))!=NULL)
  {
    for (i=0;i<n;i++)
      if ((array[i]=(int**) calloc(m,sizeof(*array)))!=NULL)
        for (j=0;j<m;j++)
          if ((array[i][j]=(int *) calloc(m,sizeof(int)))==NULL)
            error=1;
  }
  else error=1;

  if (error)
  {
    print_memoryerrormessage();
    return(NULL);
  }
  else
  {
    return(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/* allocating a 1 dimensional array for values of typ <double> */                   
/*                                                             */
/***************************************************************/

double *allocating_a_1_dimensional_array_of_typ_double(int n)
{
  int error;         /* help-variale                           */
  double *array;     /* pointer to the allocated datastructure */

  error=0;
  if ((array=(double*) calloc(n,sizeof(double)))==NULL)
    error=1;

  if (error)
  {
    print_memoryerrormessage();
    return(NULL);
  }
  else
  {
    return(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/* allocating a 2 dimensional array for values of typ <double> */                   
/*                                                             */
/***************************************************************/

double **allocating_a_2_dimensional_array_of_typ_double(int n)
{
  int i,error;       /* help-variales                          */
  double **array;    /* pointer to the allocated datastructure */

  error=0;
  if ((array=(double**) calloc(n,sizeof(*array)))!=NULL)
  {
    for (i=0;i<n;i++)
      if ((array[i]=(double*) calloc(n,sizeof(double)))==NULL)
        error=1;
  }
  else error=1;

  if (error)
  {
    print_memoryerrormessage();
    return(NULL);
  }
  else
  {
    return(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  allocating a 1 dimensional array for values of typ <Snode> */                   
/*                                                             */
/***************************************************************/

Snode *allocating_a_1_dimensional_array_of_typ_Snode(int n)
{
  int error;         /* help-variale                           */
  Snode *array;      /* pointer to the allocated datastructure */

  error=0;
  if ((array=(Snode*) calloc(n,sizeof(Snode)))==NULL)
    error=1;

  if (error)
  {
    print_memoryerrormessage();
    return(NULL);
  }
  else
  {
    return(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*  allocating a 1 dimensional array for values of typ <Sedge> */                   
/*                                                             */
/***************************************************************/

Sedge *allocating_a_1_dimensional_array_of_typ_Sedge(int n)
{
  int error;         /* help-variale                           */
  Sedge *array;      /* pointer to the allocated datastructure */

  error=0;
  if ((array=(Sedge*) calloc(n,sizeof(Sedge)))==NULL)
    error=1;

  if (error)
  {
    print_memoryerrormessage();
    return(NULL);
  }
  else
  {
    return(array);
  }
}
/***************************************************************/



/***************************************************************/
/*                                                             */
/*    freeing a 1 dimensional array for values of typ <int>    */                   
/*                                                             */
/***************************************************************/

void freeing_a_1_dimensional_array_of_typ_int(int *array, int n)
{
  if (array!=NULL)
    free(array);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*    freeing a 2 dimensional array for values of typ <int>    */                   
/*                                                             */
/***************************************************************/

void freeing_a_2_dimensional_array_of_typ_int(int **array, int n)
{
  int i;

  if (array!=NULL)
  {
    for (i=0;i<n;i++)
      free(array[i]);
    free(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*    freeing a 3 dimensional array for values of typ <int>    */                   
/*                                                             */
/***************************************************************/

void freeing_a_3_dimensional_array_of_typ_int(int ***array, int n, int m, int k)
{
  int i,j;

  if (array!=NULL)
  {
    for (i=0;i<n;i++)
    {
      for(j=0;j<m;j++)
        free(array[i][j]);
      free(array[i]);
    }
    free(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   freeing a 1 dimensional array for values of typ <double>  */                   
/*                                                             */
/***************************************************************/

void freeing_a_1_dimensional_array_of_typ_double(double *array, int n)
{
  if (array!=NULL)
    free(array);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   freeing a 2 dimensional array for values of typ <double>  */                   
/*                                                             */
/***************************************************************/

void freeing_a_2_dimensional_array_of_typ_double(double **array, int n)
{
  int i;

  if (array!=NULL)
  {
    for (i=0;i<n;i++)
      free(array[i]);
    free(array);
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   freeing a 1 dimensional array for values of typ <Snode>   */                   
/*                                                             */
/***************************************************************/

void freeing_a_1_dimensional_array_of_typ_Snode(Snode *array, int n)
{
  if (array!=NULL)
    free(array);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*   freeing a 1 dimensional array for values of typ <Sedge>   */
/*                                                             */
/***************************************************************/

void freeing_a_1_dimensional_array_of_typ_Sedge(Sedge *array, int n)
{
  if (array!=NULL)
    free(array);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                 end of memory_allocation.c                  */  
/*                                                             */
/***************************************************************/
