/***************************************************************/
/*                                                             */
/*  filename:  pl_sweep.c                                      */
/*  filetype:  C-Code                                          */
/*  author:    Christian Ramsauer                              */
/*  date:      31.01.1994                                      */
/*                                                             */
/***************************************************************/
/*                                                             */
/*  description:                                               */
/*                                                             */
/*  imports:                                                   */
/*   Sedge make_a_triangulation_edge();                        */
/*   Snode *create_an_array_for_the_nodes();                   */
/*   int left_or_right_turn();                                 */
/*   void print_memoryerrormessage();                          */
/*   int *allocating_a_1_dimensional_array_of_typ_int();       */
/*   Snode *allocating_a_1_dimensional_array_of_typ_Snode();   */
/*   void freeing_a_1_dimensional_array_of_typ_int();          */
/*   void freeing_a_1_dimensional_array_of_typ_Snode();        */
/*                                                             */
/*  exports:                                                   */
/*    void plan_sweep();                                       */
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


/***************************************************************/
/*                                                             */
/*                       local variables                       */
/*                                                             */
/***************************************************************/

Local Snode *ND;


/***************************************************************/
/*     this function compares the coordiantes of two nodes     */     
/*     it returns 1 if <a> lies left of <b>                    */
/***************************************************************/

int compare_two_Snodes(Snode a, Snode b)
{
  if ((a->x<b->x) || 
      ((a->x==b->x) && (a->y<b->y))) 
    return(1); 
  else 
    return(0);
}
/***************************************************************/


/***************************************************************/
/*      merging two already sorted arrays of type <Snode>      */
/***************************************************************/

void merge_Snodearray(Snode *A, int a, int m, Snode *B, int b, int n, Snode *C, int c)
{
  int i=0,j=0,k=0;
  if (m>0) 
  {
    while ((i<m) && (j<n))
    {
      if (compare_two_Snodes(A[a+i],B[b+j]))
        C[c+k++]=A[a+i++];
      else
        C[c+k++]=B[b+j++];
    }
    while (i<m) 
      C[c+k++]=A[a+i++];
    while (j<n) 
      C[c+k++]=B[b+j++];
  }
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*     sorting a Snode-Array with the mergesort algorithms     */
/*                                                             */
/***************************************************************/

void mergesort_Snodearray(Snode *r, int n)
{
  int k,L,L1,L2;
  Snode *t;

  t=allocating_a_1_dimensional_array_of_typ_Snode(n);
  L=1;
  while (L<n)
  {
    k=0;
    do
    {
      L1=L;
      L2=L;
      if (k+L1>n) L1=n-k;
      if (k+L1+L2>n) L2=n-k-L1;
      merge_Snodearray(r,k,L1,r,k+L1,L2,t,k);
      k=k+L1+L2;
    }
    while (k<n);
    for(k=0;k<n;k++) r[k]=t[k];
    L=L*2;
  }
  freeing_a_1_dimensional_array_of_typ_Snode(t,n);
}
/***************************************************************/


/***************************************************************/
/*           data structure for the active lines               */
/***************************************************************/

/* datastructure for a single active line */
struct activeline {
                    int x1,y1,  /* coordinates of the startpoint   */
                        x2,y2,  /* coordinates of the endpoint     */
                        rw,     /* rightmost node in the intervall */
                        end;    /* ending nodenumber               */ 
                   };

struct activeline *Activ_Line;  /* datastructure for all current active lines */
int activline_count;            /* number of current active lines             */



/***************************************************************/
/*                                                             */
/*     allocating a 1 dimensional array for values of typ      */            
/*                    <struct activeline>                      */           
/*                                                             */
/***************************************************************/

struct activeline *allocating_a_1_dimensional_array_of_typ_activeline(int n)
{
  int error;                /* help-variale                           */
  struct activeline *array; /* pointer to the allocated datastructure */

  error=0;
  if ((array=(struct activeline*) 
       calloc(n,sizeof(struct activeline)))==NULL)
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
/*      freeing a 1 dimensional array for values of typ        */
/*                    <struct activeline>                      */
/*                                                             */
/***************************************************************/

void freeing_a_1_dimensional_array_of_typ_activeline(struct activeline *array, int n)
{
  if (array!=NULL)
    free(array);
}
/***************************************************************/


/***************************************************************/
/*      inserts the specified line to the datastructure        */
/*      of the active lines at the specified position.         */
/***************************************************************/

void insertactivelineposition(int position, int x1, int y1, int x2, int y2, int rw, int end)
{
  int i;

  for (i=activline_count;i>position;i--)
    Activ_Line[i]=Activ_Line[i-1];
  activline_count++;
  Activ_Line[position].x1=x1;
  Activ_Line[position].y1=y1;
  Activ_Line[position].x2=x2;
  Activ_Line[position].y2=y2;
  Activ_Line[position].rw=rw;
  Activ_Line[position].end=end;
}
/***************************************************************/


/***************************************************************/
/*       deletes an active line at the specified position      */   
/***************************************************************/

void deleteactiveline(int position)
{
  int i;

  for (i=position;i<activline_count-1;i++)
    Activ_Line[i]=Activ_Line[i+1];
  activline_count--;
}
/***************************************************************/


/***************************************************************/
/*       returns the active line above the specified node      */
/***************************************************************/

int activeline_above(Snode node)
{
  int position;

  position=0;
  while (left_or_right_turn(Activ_Line[position].x1,
                            Activ_Line[position].y1,
                            Activ_Line[position].x2,
                            Activ_Line[position].y2,
                            node->x,
                            node->y)==-1)
    position++;
  position--;

  return(position);
}
/***************************************************************/


/***************************************************************/
/*       returns the active line below the specified node      */
/***************************************************************/

int activeline_below(Snode node)
{
  int position;
  position=activline_count-1;
  while (left_or_right_turn(Activ_Line[position].x1,
                            Activ_Line[position].y1,
                            Activ_Line[position].x2,
                            Activ_Line[position].y2,
                            node->x,
                            node->y)==1)
    position--;
  position++;

  return(position);
}
/***************************************************************/


/***************************************************************/
/*   inserts the specified line to the datastructure    */
/*   of the active lines at the correct position.       */
/***************************************************************/

void insertactiveline(int x1, int y1, int x2, int y2, int rw, int end)
{
  int position,i,hx,hy;

  /* switching the coordinates, if necessary */
  if ((x1>x2) || ((x1==x2) && (y1>y2)))
  {
    hx=x1; 
    x1=x2; 
    x2=hx;
    hy=y1; 
    y1=y2; 
    y2=hy;
  }

  position=-1;

  /* calculating the correkt position for the new activ line */
  while (left_or_right_turn(Activ_Line[position+1].x1,
                            Activ_Line[position+1].y1,
                            Activ_Line[position+1].x2,
                            Activ_Line[position+1].y2,
                            x1,
                            y1)==-1)
     position++;
  while ((left_or_right_turn(Activ_Line[position+1].x1,
                             Activ_Line[position+1].y1,
                             Activ_Line[position+1].x2,
                             Activ_Line[position+1].y2,
                             x1,
                             y1)==0) 
         && 
         (left_or_right_turn(Activ_Line[position+1].x1,
                             Activ_Line[position+1].y1,
                             Activ_Line[position+1].x2,
                             Activ_Line[position+1].y2,
                             x2,
                             y2)==-1))
     position++;
 
  position++;

  /* inserting the Line at the correct position */
  for (i=activline_count;i>position;i--)
    Activ_Line[i]=Activ_Line[i-1];
  activline_count++;
  Activ_Line[position].x1=x1;
  Activ_Line[position].y1=y1;
  Activ_Line[position].x2=x2;
  Activ_Line[position].y2=y2;
  Activ_Line[position].rw=rw;
  Activ_Line[position].end=end;
}
/***************************************************************/


/***************************************************************/
/*          inits the datastructure of the active lines.       */
/***************************************************************/

void initactivelines(void)
{
  activline_count=0;
  insertactivelineposition(0,
                           ND[0]->x-1,ND[0]->y,
                           ND[0]->x-1,-100000,
                           0,-1);
  insertactivelineposition(1,
                           ND[0]->x-1,ND[0]->y,
                           ND[0]->x-1,100000,
                           0,-1);
}
/***************************************************************/


/***************************************************************/
/*  testing, whether a line allready exists as an active line  */
/***************************************************************/

int there_is_not_already_an_edge(int a, int b, int j)
        /* first node of tested edge        */
        /* second node of tested edge       */
        /* number of the tested active line */
{
  if ((ND[a]->x!=Activ_Line[j].x1) || 
      (ND[a]->y!=Activ_Line[j].y1) ||
      (ND[b]->x!=Activ_Line[j].x2) || 
      (ND[b]->y!=Activ_Line[j].y2))
    return(1);
  else
    return(0);
}
/***************************************************************/



/***************************************************************/
/*                                                             */
/*    generating a triangulation of a planar graph bases       */
/*    on the sweep-method                                      */
/*                                                             */
/***************************************************************/

void plan_sweep(Sgraph inputgraph)
{
  Sedge edge;              /* help-edge                            */
  int i,j;                 /* help-variables                       */
  int current_node_of_ch;  /* help-variables                       */
  int AL_above,AL_below;   /* help-variables                       */
  int n;                   /* number of nodes in the inputgraph    */
  int *CW,*CCW;            /* datastructure to store the clockwise */
                           /* and counterclockwise neighbor        */

  
  /* allocating dataarrays */
  ND=create_an_array_for_the_nodes(inputgraph,&n);
  CW=allocating_a_1_dimensional_array_of_typ_int(n);
  CCW=allocating_a_1_dimensional_array_of_typ_int(n);
  Activ_Line=allocating_a_1_dimensional_array_of_typ_activeline(n*3);

  /* sorting the nodes from left to right */
  mergesort_Snodearray(ND,n); 


  /* init the active line  datastructure */
  initactivelines();

  /* insert all new active line s */
  /* starting form the leftmost node        */
  for_edgelist(ND[0],edge)
  {
    if ((edge->tnode->x > edge->snode->x) || 
        ((edge->tnode->x == edge->snode->x) && 
         (edge->tnode->y > edge->snode->y)))
      insertactiveline(edge->snode->x,
                       edge->snode->y,
                       edge->tnode->x,
                       edge->tnode->y,
                       0,
                       0);
  }
  end_for_edgelist(ND[0],edge);


  /* init the clockwise and counterclockwise */
  /* pointer of the leftmost node            */
  CCW[0]=0;
  CW[0]=0;

  /* starting the sweep-method with the second node */
  i=1;

  while (i<n)
  {
    /* finding the activ lines above and below node i */
    AL_above=activeline_above(ND[i]);
    AL_below=activeline_below(ND[i]);

    CW[i]=i;
    CCW[i]=i;

    /* triangulate all intervalls between to active lines */
    for (j=AL_above;j<AL_below;j++)
    {
      /* making an edge from node i to the rightmost  */
      /* edge in the current intervall                */
      current_node_of_ch=Activ_Line[j].rw;
      if ((there_is_not_already_an_edge(current_node_of_ch,i,j)) && 
          (there_is_not_already_an_edge(current_node_of_ch,i,j+1)))
        make_a_triangulation_edge(ND[current_node_of_ch],ND[i]);

      /* walking in counterclockwise order */
      while ((left_or_right_turn(ND[i]->x,
                                 ND[i]->y,
                                 ND[current_node_of_ch]->x,
                                 ND[current_node_of_ch]->y,
                                 ND[CCW[current_node_of_ch]]->x,
                                 ND[CCW[current_node_of_ch]]->y)==-1) 
              && (current_node_of_ch!=Activ_Line[j].end))
      {
        current_node_of_ch=CCW[current_node_of_ch];
        if (there_is_not_already_an_edge(current_node_of_ch,i,j))
          make_a_triangulation_edge(ND[current_node_of_ch],ND[i]);
      }
      if (j==AL_above) CCW[i]=current_node_of_ch;

      /* walking in clockwise order */
      current_node_of_ch=Activ_Line[j].rw;
      while ((left_or_right_turn(ND[i]->x,
                                 ND[i]->y,
                                 ND[current_node_of_ch]->x,
                                 ND[current_node_of_ch]->y,
                                 ND[CW[current_node_of_ch]]->x,
                                 ND[CW[current_node_of_ch]]->y)==1) 
         && (current_node_of_ch!=Activ_Line[j+1].end))
      {
        current_node_of_ch=CW[current_node_of_ch];
        if (there_is_not_already_an_edge(current_node_of_ch,i,j+1))
          make_a_triangulation_edge(ND[current_node_of_ch],ND[i]);
      }
      if (j==AL_below-1) CW[i]=current_node_of_ch;

      Activ_Line[j].rw=i;
    }

    /* delete the active lines the are no more activ jet */
    for (j=AL_above+1;j<AL_below;j++)
      deleteactiveline(AL_above+1);

    /* inserting new active lines starting from node i */
    for_edgelist(ND[i],edge)
    {
      if ((edge->tnode->x>edge->snode->x) || 
          ((edge->tnode->x==edge->snode->x) &&
           (edge->tnode->y>edge->snode->y)))
        insertactiveline(edge->snode->x,
                         edge->snode->y,
                         edge->tnode->x,
                         edge->tnode->y,
                         i,
                         i);
    }
    end_for_edgelist(ND[i],edge);

    /* switching to the next point */
    i++;
  }

  /* freeing dataarrays */
  freeing_a_1_dimensional_array_of_typ_Snode(ND,n);
  freeing_a_1_dimensional_array_of_typ_int(CCW,n);
  freeing_a_1_dimensional_array_of_typ_int(CW,n);
  freeing_a_1_dimensional_array_of_typ_activeline(Activ_Line,n*3);
}
/***************************************************************/


/***************************************************************/
/*                                                             */
/*                       end of pl_sweep.c                     */
/*                                                             */
/***************************************************************/
