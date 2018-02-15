/******************************************************************************/
/*                                                                            */
/*    FlowMatch.h                                                             */
/*                                                                            */
/******************************************************************************/
/*  Max-Flow          :  Dimic                                                */
/*  Weighted Matching :  Edmonds & Johnson                                    */
/*  The code is essentially from the GraphEd source. The main change is the   */
/*  the addition of support of an extended attribute field, which I needed    */
/*  for the separator algorithms. It is now possible to save the current      */
/*  attributes, compute e.g. a matching and restore the attributes.           */
/*  Also message output was ommited.                                          */
/*  This version expects a valid attribute field for all nodes of the         */
/*  graph.                                                                    */
/******************************************************************************/
/*  Owner    :  Harald Lauer                                                  */
/*  Created  :  13.02.1994                                                    */
/*  Modified :  23.06.1994                                                    */
/*  History  :  Version:    Changes:                                          */
/*              1.1         Don't touch node attributes.                      */
/*              1.0         First Revision                                    */
/******************************************************************************/

#ifndef  FLOW_MATCH
#define  FLOW_MATCH

/******************************************************************************/
/*  Includes                                                                  */
/******************************************************************************/

#include  <Separator.h>

/******************************************************************************/
/*  Definitions                                                               */
/******************************************************************************/

#ifndef FLOAT_MAX 
#define FLOAT_MAX 1e9
#endif
#define max_nr_vvalues 3
#define max_nr_evalues 3
#define bits_int 32
#define makearray(typ,maxindex) (typ*)calloc(((maxindex) > 1) ? (maxindex)+1 : 3,sizeof(typ))
#define cleararray(typ,name,maxindex) memset(name,0,sizeof(typ)*((maxindex)+1))
/*#define charray(typ,name,maxindex) check_array(name,maxindex,"typ","name"); */
#define mitrauf(v1,v2) if (v1 < (v2)) v1=v2 ;else
#define mitrunter(v1,v2) if (v1 > (v2)) v1=v2;else
#define swap(typ,v1,v2) {typ t=v1;v1=v2;v2=t;}


/******************************************************************************/
/*  Data structure                                                            */
/******************************************************************************/

typedef struct pair_of_edgevalues
{
  float float1,float2;
  int nr;               /* for internal use only */
  Attributes  attrs;    /* Harald Lauer, 13.05.1994 */
} *Pair_of_edgevalues;

struct fm_graph 
  {
  int  *v, *neighbor, *previous, *next, *degree_out, *degree_in , new_v, new_e, 
       nr_v, nr_e, nr_vvalues, nr_evalues, range_v, range_e, max_nr_v, max_nr_e, 
       directed, multigraph, bipartite, tree_root;
  float *vvalues[max_nr_vvalues],*evalues[max_nr_evalues]; 
  };

#endif

/******************************************************************************/
/*  End of  FlowMatch.h                                                       */
/******************************************************************************/
