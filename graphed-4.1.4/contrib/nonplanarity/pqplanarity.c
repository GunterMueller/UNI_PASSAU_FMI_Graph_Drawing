/****************************************************************************\
 *                                                                          *
 *  pqplanarity.c                                                           *
 *  -------------                                                           *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
\****************************************************************************/

/* Ozawa, Takahashi: A GRAPH-PLANARIZATION ALGORITHM AND ITS APPLICATION    */
/*                   TO RANDOM GRAPHS in:                                   */
/* Graph Theory and Algorithms: Lect. Notes in Comp. Sc. 108 (1981) 95-107  */
/* Booth, Lueker */

/* Implementation:                                                          */
/* Andreas Winter (11027)                                                   */
/* 03.03.93 - xx.xx.93                                                      */

#if defined SUN_VERSION
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <error.h>
#else
#include <sgraph\std.h>
#include <sgraph\slist.h>
#include <sgraph\sgraph.h>
#include <graphed\error.h>
#endif


/* #include "planar.h" */

#include "sattrs.h"
#include "pqtree.h"

#include "stnumber.h"

#include "pqhandle.h"
#include "reduce.h"

#include "pqplanarity.h"

#if defined DEBUG
#include "sdebug.h"
#endif


#if defined SUN_VERSION
Local   bool    is_pq_planar(Sgraph G);
#else
Local   bool    is_pq_planar(Sgraph);
#endif


#ifdef DEBUG
extern  FILE    *outfile;
	FILE    *pqfile;
#endif

extern	char    buffer[255];



/****************************************************************************\
 *                                                                          *
 *  Global int pq_planarity(Sgraph)                                         *
 *  -------------------------------                                         *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : int, returncode of planarity-test                           *
 *              FALSE                                                       *
 *               - in case that the input graph is not planar.              *
 *              TRUE                                                        *
 *               - in case that the given input graph is already planar     *
 *              several returncodes                                         *
 *               - in case that the graph does not comply with assumptions  *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : global function DETERMINE_BICONNECTED_COMPONENTS            *
 *                                                                          *
 *  task      : determines all biconnected components and computes for each *
 *              the planarity-property.                                     *
 *                                                                          *
\****************************************************************************/

Global int pq_planarity(Sgraph Graph)
{
   Sgraph 	G;
   int          nodes, edges;
   int		BiCompCount;
   Slist	GraphList;
   int		Property;
   int         is_planar;


/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */
/* IMPORTANT !!!                                                            *
 * DO NOT IGNORE !!!							    *
 * it necessary that before the call of this function, attributes           *
 * are assigned to the graph. for this purpose use                          *
 * create_all_attributes(graph) before and                                  *
 * clear_all_attributes(graph) after calling this function.		    */
/* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */

#  ifdef DEBUG
   pqfile = fopen("pqtree.out","w");
   fflush(pqfile);
#  endif

/* test for multiple edges and self-loops */
/* and test for euler's planarity-property e <= 3*v-6 */
/* therefore edges need to be counted */
/* the following properties a assumed for a graph before it is *
 * processed: *
 *  - no self-loops *
 *  - no multiple edges */
   Property = CheckAssumedGraphProperties(Graph, &nodes, &edges);
   if (Property != PROPER_GRAPH) {
      return Property;
   } /* endif */

   if (nodes < 5) {
       return TRUE;
   } /* endif */

   if (edges > 3*nodes - 6) {
      return TOO_MANY_EDGES;
   } /* endif */

   BiCompCount = DETERMINE_BICONNECTED_COMPONENTS(Graph, &GraphList);

   is_planar = TRUE;
   while ((GraphList != empty_slist) && (is_planar)) {
      G = sgraph_in_slist(GraphList);
      GraphList = rest(GraphList);
#     ifdef DEBUG
      fprintf(outfile, "\nstarting planaritytest for graph\n");
      printGraph(outfile, G);
#     endif
      if (BiCompCount == 0) {
	 re_init_all_attributes(G);
      } else {
	 create_and_init_all_attributes(G);
      }

      is_planar = is_pq_planar(G);

      if (BiCompCount != 0) {
	 clear_all_attributes(G);
	 remove_graph(G);
      } /* endif */

   } /* endwhile */

   while (GraphList != empty_slist) {
      G = sgraph_in_slist(GraphList);
      GraphList = rest(GraphList);
      remove_graph(G);
   } /* endwhile */

#  ifdef DEBUG
   fprintf(pqfile, "\nremoved all not tested components, isplanar: %d",
	      is_planar);
   fflush(pqfile);
#  endif

#  ifdef DEBUG
   fclose(pqfile);
#  endif

   return is_planar;
}



/****************************************************************************\
 *                                                                          *
 *  Local bool is_pq_planar(Sgraph)                                         *
 *  -------------------------------                                         *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : bool, returncode of planarity-test                          *
 *              FALSE                                                       *
 *               - in case that the input graph is not planar.              *
 *              TRUE                                                        *
 *               - in case that the given input graph is already planar     *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---                                                         *
 *                                                                          *
 *  task      : this is the real call for planarity-test.                   *
 *              it is only called for proper graphs, i.e. those that have   *
 *              no multiple edges, no self-loops and over all, those that   *
 *              are biconnected.                                            *
 *              see Lempel, Even, Cederbaum for details of planarity-test   *
 *              and Boot, Lueker for details concerning their datastructure *
 *              PQ-Trees.                                                   *
 *                                                                          *
\****************************************************************************/

Local bool is_pq_planar(Sgraph G)
{
   int      N, NumberOfNodes;
   PQtree   ROOT, T_N, T_NN;
   Snode    s_node, t_node, curr_node;
   Slist    S;
   int      S_LENGTH;
   bool     is_planar;


/* assume graph to be biconnected                                           */
/* either test this property or decompose graph into biconnected components */


/* Algorithm PLAN                                                           */

/* Let G be a biconnected graph with n nodes.                               */

/* Step 1: Choose two vertices s and t in G, and determine an st-numbering  *
 *         for G. Construct PQ-tree T_1 consisting of a leaf corresponding  *
 *         to vertex 1 of G.                                                *
 *         Set N <- 1.                                                      */

/*    in a final performance-tuning step, try not to perform pre_order in *
 *    STNUMBER, since pre_order is already performed in DETERMINE_BICO... */
/*    another point is, that biconnected components with only two nodes *
 *    need not be further regarded, since they are planar */

   is_planar = TRUE;

   NumberOfNodes = STNUMBER(G, &s_node, &t_node);

#  ifdef DEBUG
/* printGraphWithAttrs(outfile, G); */
   fprintf(pqfile,
           "\n\nfound %2d nodes, s (%2d) and t (%2d)",
           NumberOfNodes, node_stnumber(s_node), node_stnumber(t_node));

   sprintf(buffer,
           "\n\nfound %2d nodes, s (%2d) and t (%2d)",
           NumberOfNodes, node_stnumber(s_node), node_stnumber(t_node));
   message(buffer);
   fflush(pqfile);
#  endif


      ROOT = Create_Initial_PQtree(s_node);

      N = 1;
      curr_node = s_node;
      T_N = ROOT;

      while ((N < NumberOfNodes) &&
	     (is_planar)) {  /* alternativ: while (curr_node != t_node) */

/* Step 2: For vertex N of G, construct PQ-tree T_NN consisting of a P-node *
 *         and its children. The P-node is the root and its children are    *
 *         all leaves corrsponding to the vertices into which the edges     *
 *         from vertex N go. Add T_NN to ROOT by replacing the leaf T_N     *
 *         corresponding to vertex N with the root of T_NN.                 *
 *         Let T_N+1 be the resultant PQ_tree.                              *
 *         Set N <- N+1.                                                    */

	 T_NN = Create_Pnode_for_N(N, curr_node);
	 ROOT = Replace_Leaf(ROOT, T_N, T_NN);

	 N = N+1;
#     ifdef DEBUG
      fprintf(pqfile, "\n\n-->set counter N to %d !!!", N);
      sprintf(buffer, "\n\n-->set counter N to %d !!!", N);
      message(buffer);
#     endif

	 curr_node = node_stsucc(curr_node);


/* Step 3: Let S be the set of all the leaves in ROOT corresponding to      *
 *         vertex N of G. If ROOT is reducible for S, then go to step 5.    */

	 S_LENGTH = 0;
	 S = Search_Leaves_in_PQtree(ROOT, N, curr_node, &S_LENGTH);
#        ifdef DEBUG
	 fprintf(pqfile,"\n\nNew Complete PQ-Tree:");
	 print_PQtree(pqfile, ROOT);
	 fprintf(pqfile,"\nfound %d leaves with number: %2d\n",
		 S_LENGTH,
		 node_stnumber(curr_node));
	 fflush(pqfile);
#        endif

	 if (!BUBBLE_PLANAR(ROOT, S, S_LENGTH)) {
#           ifdef DEBUG
	    sprintf(buffer,"\nPQ-Tree is not reducible (BUBBLE)!");
	    message(buffer);
#           endif
	    is_planar = FALSE;
	 } else {
	    if (!REDUCE(ROOT, S, S_LENGTH)) {
#              ifdef DEBUG
	       sprintf(buffer,"\nPQ-Tree is not reducible (REDUCE)!");
	       message(buffer);
#              endif
	       is_planar = FALSE;
	    } else {
/*          PQ-Tree ist reduzierbar und damit *
 *          ist der zugrunde liegende Graph planar */
	    }/* endif */
	 } /* endif */

/*    free S after use                                                       */
/*    although S is empty after next call and S_LENGTH = 0, the values are *
 *    not changed until the next use of both variables *
 *    be sure to reinitialize them correctly */
	 T_N = Reduce_Pertinent_Leaves_To_Unique_Leaf(S, S_LENGTH);
#        ifdef DEBUG
         fprintf(pqfile,"\n\nComplete PQ-Tree after ALL reduction:");
         print_PQtree(pqfile, ROOT);
#        endif
   } /* endwhile */

/* free complete PQ-Tree !! */
   free_complete_PQtree(ROOT);

   return is_planar;
}


/****************************************************************************\
 *                                                                          *
 *  Global void PQ_Planarity_Test(Sgraph)                                   *
 *  -------------------------------------                                   *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : ---							    *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : pq_planarity					            *
 *                                                                          *
 *  task      : calls the planarity_test and expects all possible return    *
 *		values and shows a corresponding message.		    *
 *                                                                          *
\****************************************************************************/

Global void PQ_Planarity_Test(Sgraph g)
{
/* ueberpruefe noch zusaetzlich biconnected(graph) 			    */
/* count edges and determine wether EULER's formula 			    *
 * is satisfied (e <= 3*v - 6), if not then stop ! 			    */
/* this is a possible return value in pq_planarity			    */
   create_and_init_all_attributes(g);
   switch(pq_planarity(g)) {
      case FALSE           :
		 message("\n\nPQ: graph is nonplanar.\n");
		break;
      case TRUE            :
		message("\n\nPQ: graph is planar.\n");
		break;
      case TOO_MANY_EDGES  :
		message("\n\nPQ: graph is nonplanar (too many edges).\n");
		break;
      case MULTI_EDGE      :
		message("\n\nPQ: graph has multiple edges.\n");
		break;
      case LOOP        :
		message("\n\nPQ: graph has loops.\n");
		break;
      default              :
		message("\n\nPQ: this case shouldn't have occured.\n");
   }
   clear_all_attributes(g);
}



