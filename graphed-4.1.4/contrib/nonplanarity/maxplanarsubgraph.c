/****************************************************************************\
 *                                                                          *
 *  maxplanarsubgraph.c                                                     *
 *  -------------------                                                     *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
\****************************************************************************/


/* references:								    */
/* Ozawa, Takahashi: A GRAPH-PLANARIZATION ALGORITHM AND ITS APPLICATION    */
/*                   TO RANDOM GRAPHS in:                                   */
/* Graph Theory and Algorithms: Lect. Notes in Comp. Sc. 108 (1981) 95-107  */

/* Jayakumar, Thulasiraman, Swamy: O(n^2) ALGORITHMS FOR GRAPH              *
 *                                 PLANARIZATION in:                        */



#include <stdlib.h>
#include <string.h>

#if defined SUN_VERSION
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <sgraph/random.h>
#include <error.h>
#else
#include <sgraph\std.h>
#include <sgraph\slist.h>
#include <sgraph\sgraph.h>
#include <sgraph\random.h>
#include <graphed\error.h>
#endif

/* #include "planar.h" */

#include "nonplanarity_settings.h"
#include "sattrs.h"
#include "pqtree.h"

#include "stnumber.h"

#include "pqplanarity.h"
#include "pqhandle.h"
#include "reduce.h"

#include "random.h"

#if defined DEBUG
#include "sdebug.h"
#endif


#if defined SUN_VERSION
Local   bool    planarize(Sgraph G);
/*Local   bool    maxplanarize(Sgraph G);*/
/*Local  Sgraph	create_mpg_from_graph(Sgraph ingraph);*/
Local  Sgraph	create_max_mpg_from_graph(Sgraph ingraph, int *edgeCount1, int *edgeCount2);
Local	void	store_mpg_edges_in_graph_edge_attrs(Sgraph g);
Local	void	store_edges_in_mpg_edge_attrs(Sgraph g);
/*Local	int	recount_graph(Sgraph g);*/
#else
Local   bool    planarize(Sgraph);
/*Local   bool    maxplanarize(Sgraph);*/
/*Local  Sgraph	create_mpg_from_graph(Sgraph);*/
Local  Sgraph	create_max_mpg_from_graph(Sgraph, int *, int *);
Local	void	store_mpg_edges_in_graph_edge_attrs(Sgraph);
Local	void	store_edges_in_mpg_edge_attrs(Sgraph);
/*Local	int	recount_graph(Sgraph);*/
#endif


#ifdef DEBUG
extern  FILE    *outfile;
	FILE    *pqfile;
#endif

extern	char    buffer[255];
extern	MaxPlanarSettings	currMaxPlanarSettings;
extern	ThicknessSettings	currThicknessSettings;
extern	CrossingNumberSettings	currCrossingNumberSettings;




/****************************************************************************\
 *                                                                          *
 *  Global Sgraph maxplanarsubgraph(Graph)                                  *
 *  --------------------------------------                                  *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : graph of type Sgraph                                        *
 *               - in case that the input graph is not planar this will be  *
 *                 the maximal planar subgraph of the input graph.          *
 *              empty_graph                                                 *
 *               - in case that the given input graph is already planar     *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : global function DETERMINE_BICONNECTED_COMPONENTS and        *
 *              local functions planarize and maxplanarize                  *
 *                                                                          *
 *  task      : determines all biconnected components and computes for each *
 *              at first a spanning planar subgraph and then makes this     *
 *              graph maximal by inserting possible edges.                  *
 *              if the input graph itself is planar, then the functions     *
 *              work directly on that and marks the attributes in edges.    *
 *                                                                          *
\****************************************************************************/

Global Sgraph maxplanarsubgraph(Sgraph Graph)
{
   int          CompCount;
   Snode    n, n2;
   Sedge    e2;
   Sgraph 	G;
   int		BiCompCount;
   Slist	GraphList;
   Sgraph	mpg;
   bool         is_planar, all_planar;
   int		property, nodes, edges;
/* bool		is_max_planar; */

/* test for multiple edges and self-loops */

#  ifdef DEBUG
   pqfile = fopen("pqtree.out","w");
   fflush(pqfile);
#  endif

   create_and_init_all_attributes(Graph);
   property = CheckAssumedGraphProperties(Graph, &nodes, &edges);
   if (property != PROPER_GRAPH) {
      if (property == MULTI_EDGE) {
	 message("\ngraph has multiple edges.\n");
	 clear_all_attributes(Graph);
	 return empty_sgraph;
      } else if (property == LOOP) {
	 message("\ngraph has self loops.\n");
	 clear_all_attributes(Graph);
	 return empty_sgraph;
      } /* endif */
   } /*endif */

   BiCompCount = DETERMINE_BICONNECTED_COMPONENTS(Graph, &GraphList);
   CompCount = 0;
#  ifdef DEBUG
   fprintf(outfile, "\nfound %d biconnected components", BiCompCount);
   fflush(outfile);
#  endif

   all_planar = TRUE;
   while (GraphList != empty_slist) {
      G = sgraph_in_slist(GraphList);
      GraphList = rest(GraphList);
      CompCount++;
      if (BiCompCount == 0) {
	 re_init_all_attributes(G);
      } else {
	 create_and_init_all_attributes(G);
      }
#     ifdef DEBUG
      fprintf(outfile, "\nstarting maxplanarsubgraph\n");
      printGraph(outfile, G);
#     endif

/*    the planar subgraph of this component is given by the edge attribute  *
 *    edge_in_mpg(edge). is_planar returns TRUE if the given graph is       *
 *    already planar, else FALSE is returned.                               */

      is_planar = planarize(G);
      if (!is_planar) {
	 all_planar = FALSE;
/*	 is_max_planar = maxplanarize(G);  * should never return false ! */
	 if (BiCompCount != 0) {
/*          store all necessary information of G in Graph */
/*	    exp: store attribute component number in edge_marker  (counter) */
	    for_all_nodes(G, n) {
	       n2 = n->iso;
	       for_sourcelist(n2, e2) {
		  if (n2->nr < e2->tnode->nr) {
		     if (edge_marker(e2) == CompCount) {
			edge_in_mpg(e2) = edge_in_mpg(edge_iso(e2));
		     } /* endif */
		  } /* endif */
	       } end_for_sourcelist(n2, e2);
	    } end_for_all_nodes(G, n);
	    clear_all_attributes(G);
	    remove_graph(G);
	 } /* endif */
      } else {
	 if (BiCompCount != 0) {
	    clear_all_attributes(G);
	    remove_graph(G);
	 } /* endif */
      } /* endif */
   } /* endwhile */

#  ifdef DEBUG
   fclose(pqfile);
#  endif

   if (all_planar) {
#     ifdef DEBUG
      sprintf(buffer, "\nall components are planar!\n");
      message(buffer);
#     endif

      clear_all_attributes(Graph);
      return empty_graph;
   } else {
/*    edges with attribute 'in_mpg' are copied to mpg, others not */
#     ifdef DEBUG
      sprintf(buffer, "\nfound at least one non-planar component\n");
      message(buffer);
#     endif

/*    ??????????? changed parameter from G to Graph, although results       *
 *    have been correct ?????????????					    */
/*    mpg = create_mpg_from_graph(Graph); */


      {
      int	delEdges, maxEdges;

      mpg = create_max_mpg_from_graph(Graph, &delEdges, &maxEdges);
      if (!currMaxPlanarSettings) {
	 message("\nno settings available !\n");
      } else {
	 currMaxPlanarSettings->deleted_edge_count = delEdges;
	 currMaxPlanarSettings->re_inserted_edge_count = maxEdges;
      } /* endif */

#     ifdef	VERBOSE
/*    store parameter maxEdges in settings->re_inserted_edge_count */
      sprintf(buffer, "\nre-inserted %d edges for maximal subgraph", maxEdges);
      message(buffer);
#     endif
      }

      clear_all_attributes(Graph);
      store_mpg_edges_in_graph_edge_attrs(mpg);
      return mpg;
   } /* endif */
}



Local bool      planarize(Sgraph G)
{
   int      N, NumberOfNodes;
   PQtree   ROOT, T_N, T_NN, PRUNED_ROOT;
   bool     TYPE;
   Snode    s_node, t_node, curr_node;
   Slist    S, STACK;
   int      S_LENGTH;
   bool     is_reducible, is_planar;


#  ifdef DEBUG
   Slist        l;
#  endif


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
           "\n\nmpg: found %2d nodes, s (%2d) and t (%2d)",
           NumberOfNodes, node_stnumber(s_node), node_stnumber(t_node));

   sprintf(buffer,
           "\n\nmpg: found %2d nodes, s (%2d) and t (%2d)",
           NumberOfNodes, node_stnumber(s_node), node_stnumber(t_node));
   message(buffer);
   fflush(pqfile);
#  endif


   ROOT = Create_Initial_PQtree(s_node);

   N = 1;
   curr_node = s_node;
   T_N = ROOT;

   while (N < NumberOfNodes) {  /* alternativ: while (curr_node != t_node)
*/

/* Step 2: For vertex N of G, construct PQ-tree T_NN consisting of a P-node *
 *         and its children. The P-node is the root and its children are    *
 *         all leaves corrsponding to the vertices into which the edges     *
 *         from vertex N go. Add T_NN to ROOT by replacing the leaf T_N     *
 *         corresponding to vertex N with the root of T_NN.                 *
 *         Let T_N+1 be the resultant PQ_tree.                              *
 *         Set N <- N+1.                                                    */

      T_NN = Create_Pnode_for_N(N, curr_node);
/*    print_PQtree(pqfile, T_NN); */
      ROOT = Replace_Leaf(ROOT, T_N, T_NN);
/*    print_PQtree(pqfile, ROOT); */

      N = N+1;
#     ifdef DEBUG
      fprintf(pqfile, "\n\n-->set counter N to %d !!!", N);
      sprintf(buffer, "\n\n-->set counter N to %d !!!", N);
      message(buffer);
#     endif

      curr_node = node_stsucc(curr_node);


/* Step 3: Let S be the set of all the leaves in ROOT corresponding to      *
 *         vertex N of G. If ROOT is reducible for S, then go to step 5.    */

      is_reducible = false;

      while (!is_reducible) {
/*	 in the case that COMPUTE_MAXPLANAR_VALUES marked some PQ-Nodes *
 *	 as DELETED, they are REALLY deleted in the next function */
	 S_LENGTH = 0;
	 S = Search_Leaves_in_PQtree(ROOT, N, curr_node, &S_LENGTH);
#        ifdef DEBUG
	 fprintf(pqfile,"\n\nNew Complete PQ-Tree:");
	 print_PQtree(pqfile, ROOT);
	 fprintf(pqfile,"\nfound %d leaves with number: %2d\n",
		 S_LENGTH,
		 node_stnumber(curr_node));

	 for_slist(S,l) {
	    fprintf(pqfile,"  leaf: type %d  entry %2d",
		    pqtree_type(attr_data_of_type(l,PQtree)),
		    pqtree_entry(attr_data_of_type(l,PQtree)));
	 } end_for_slist(S,l);
	 fflush(pqfile);
#        endif

	 PRUNED_ROOT = BUBBLE_MPG(ROOT, S, S_LENGTH);
/* #     ifdef DEBUG
	 fprintf(pqfile,"\n\nComplete PQ-Tree after bubbling:");
	 print_PQtree(pqfile, ROOT);
   #     endif */

	 TYPE = COMPUTE_MAXPLANAR_VALUES(S, S_LENGTH, &STACK);
/*       benutze ein anderes BUBBLE fuer maxplanar-algorithmus, *
 *       das alle parent-pointer erhaelt. dieser teil kann aber in *
 *       einem schritt mit compute_values geschehen. */
	 if (TYPE) {
#           ifdef DEBUG
	    fprintf(pqfile,"\nPQ-Tree ist reduzierbar (MAXPLANAR)!");
	    sprintf(buffer,"\nPQ-Tree ist reduzierbar (MAXPLANAR)!");
	    message(buffer);
#           endif
/*          reduction should eventually only be executed if PQ-Tree is *
 *          reducible due to COMPUTE_MAXPLANR_VALUES ! */
/*	    after COMPUTE_MAXPLANAR_VALUES recompute in every case set S */

	    if (!REDUCE(ROOT, S, S_LENGTH)) {
/*	       this case should certainly not occur !!! */
#              ifdef DEBUG
	       fprintf(pqfile,
		       "\nPQ-Tree nicht reduzierbar (REDUCE)  OOOPS!");
#              endif
	       sprintf(buffer,
		       "\nPQ-Tree nicht reduzierbar (REDUCE)  OOOPS!");
	       message(buffer);
	       return false;
	    } else {
/*             PQ-Tree ist reduzierbar und damit *
 *             ist der zugrunde liegende Graph planar */
	       is_reducible = true;
	    }/* endif */

	 } else {
/* Step 4: Delete a MINIMUM number of leaves in ROOT such that ROOT becomes *
 *         reducible.  */
/*           fuehre BUBBLE aus, dann COMPUTE_VALUES,
	     bestimme Typ von S_root, PQ-tree ist reduzierbar,
	     g.d.w. typ von S-root B, H oder A, in diesem Fall
	     fuehre reduction aus, im anderen fall fuehre
	     delete_edges und anschl. reduce aus */
/*           DELETE_EDGES;                                                  */
/*           obwohl der PQ-Tree jetzt reduzierbar sein sollte, *
 *           wirde das erst im naechsten Durchlauf der schleife *
 *           bestaetigt, daher wird hier das flag is_planar noch *
 *           nicht auf true gesetzt */

#           ifdef DEBUG
	    fprintf(pqfile,"\nPQ-Tree nicht reduzierbar (MAXPLANAR)!");
	    sprintf(buffer,"\nPQ-Tree nicht reduzierbar (MAXPLANAR)!");
	    message(buffer);
#           endif
	    is_planar = FALSE;
	    DELETE_MINIMUM_PERTINENT_LEAVES(STACK);
	 } /* endif */
      } /* endwhile */


/* Step 5: Reduce ROOT. T_N is the leaf corresponding to vertex N of G.     *
 *         If N=n, stop. If N<n, go to step 2.                              */

#     ifdef DEBUG
      fprintf(pqfile,"\nPQ-Tree IST reduzierbar !");
      sprintf(buffer,"\nPQ-Tree IST reduzierbar !");
      message(buffer);
#     endif

#     ifdef DEBUG
      fprintf(pqfile,"\n\nComplete PQ-Tree after reduction:");
      print_PQtree(pqfile, ROOT);
#     endif

/*    fprintf(pqfile,
	      "\nPQtree-leaf: type %d  entry %2d  parent %d  l.s. %d  r.s. %d",
	      pqtree_type(T_N), pqtree_entry(T_N),
	      pqtree_entry(pqtree_parent(T_N)),
	      pqtree_entry(pqtree_left_sibling(T_N)),
	      pqtree_entry(pqtree_right_sibling(T_N))); */

/*    free S after use                                                       */
/*    although S is empty after next call and S_LENGTH = 0, the values are *
 *    not changed until the next use of both variables *
 *    be sure to reinitialize them correctly */
      T_N = Reduce_Pertinent_Leaves_To_Unique_Leaf(S, S_LENGTH);
#     ifdef DEBUG
      fprintf(pqfile,"\n\nComplete PQ-Tree after ALL reduction:");
      print_PQtree(pqfile, ROOT);
#     endif
   } /* endwhile */

/* free complete PQ-Tree !! */
   free_complete_PQtree(ROOT);

   return is_planar;
}


#if 0
Local bool      maxplanarize(Sgraph G)
{
   int      N, NumberOfNodes;
   PQtree   ROOT, T_N, T_NN, PRUNED_ROOT;
   bool     TYPE;
   Snode    s_node, t_node, curr_node;
   Slist    S, STACK;
   int      S_LENGTH;
   bool     is_reducible, is_planar;


#  ifdef DEBUG
   Slist        l;
#  endif


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
           "\n\nmpg: found %2d nodes, s (%2d) and t (%2d)",
           NumberOfNodes, node_stnumber(s_node), node_stnumber(t_node));

   sprintf(buffer,
           "\n\nmpg: found %2d nodes, s (%2d) and t (%2d)",
           NumberOfNodes, node_stnumber(s_node), node_stnumber(t_node));
   message(buffer);
   fflush(pqfile);
#  endif


   ROOT = Create_Initial_PQtree(s_node);

   N = 1;
   curr_node = s_node;
   T_N = ROOT;

   while (N < NumberOfNodes) {  /* alternativ: while (curr_node != t_node)
*/

/* Step 2: For vertex N of G, construct PQ-tree T_NN consisting of a P-node *
 *         and its children. The P-node is the root and its children are    *
 *         all leaves corrsponding to the vertices into which the edges     *
 *         from vertex N go. Add T_NN to ROOT by replacing the leaf T_N     *
 *         corresponding to vertex N with the root of T_NN.                 *
 *         Let T_N+1 be the resultant PQ_tree.                              *
 *         Set N <- N+1.                                                    */

      T_NN = Create_Pnode_for_N(N, curr_node);
/*    print_PQtree(pqfile, T_NN); */
      ROOT = Replace_Leaf(ROOT, T_N, T_NN);
/*    print_PQtree(pqfile, ROOT); */

      N = N+1;
#     ifdef DEBUG
      fprintf(pqfile, "\n\n-->set counter N to %d !!!", N);
      sprintf(buffer, "\n\n-->set counter N to %d !!!", N);
      message(buffer);
#     endif

      curr_node = node_stsucc(curr_node);


/* Step 3: Let S be the set of all the leaves in ROOT corresponding to      *
 *         vertex N of G. If ROOT is reducible for S, then go to step 5.    */

      is_reducible = false;

      while (!is_reducible) {
/*	 in the case that COMPUTE_MAXPLANAR_VALUES marked some PQ-Nodes *
 *	 as DELETED, they are REALLY deleted in the next function */
	 S_LENGTH = 0;
	 S = Search_Leaves_in_PQtree(ROOT, N, curr_node, &S_LENGTH);
#        ifdef DEBUG
	 fprintf(pqfile,"\n\nNew Complete PQ-Tree:");
	 print_PQtree(pqfile, ROOT);
	 fprintf(pqfile,"\nfound %d leaves with number: %2d\n",
		 S_LENGTH,
		 node_stnumber(curr_node));

	 for_slist(S,l) {
	    fprintf(pqfile,"  leaf: type %d  entry %2d",
		    pqtree_type(attr_data_of_type(l,PQtree)),
		    pqtree_entry(attr_data_of_type(l,PQtree)));
	 } end_for_slist(S,l);
	 fflush(pqfile);
#        endif

	 PRUNED_ROOT = BUBBLE_MPG(ROOT, S, S_LENGTH);
/* #     ifdef DEBUG
	 fprintf(pqfile,"\n\nComplete PQ-Tree after bubbling:");
	 print_PQtree(pqfile, ROOT);
   #     endif */

	 TYPE = COMPUTE_MAXPLANAR_VALUES(S, S_LENGTH, &STACK);
/*       benutze ein anderes BUBBLE fuer maxplanar-algorithmus, *
 *       das alle parent-pointer erhaelt. dieser teil kann aber in *
 *       einem schritt mit compute_values geschehen. */
	 if (TYPE) {
#           ifdef DEBUG
	    fprintf(pqfile,"\nPQ-Tree is reducible (MAXPLANAR)!");
	    sprintf(buffer,"\nPQ-Tree is reducible (MAXPLANAR)!");
	    message(buffer);
#           endif
/*          reduction should eventually only be executed if PQ-Tree is *
 *          reducible due to COMPUTE_MAXPLANR_VALUES ! */
/*	    after COMPUTE_MAXPLANAR_VALUES recompute in every case set S */

	    if (!REDUCE(ROOT, S, S_LENGTH)) {
/*	       this case should certainly not occur !!! */
#              ifdef DEBUG
	       fprintf(pqfile,
		       "\nPQ-Tree is not reducible (REDUCE)  OOOPS!");
#              endif
	       sprintf(buffer,
		       "\nPQ-Tree is not reducible (REDUCE)  OOOPS!");
	       message(buffer);
	       return false;
	    } else {
/*             PQ-Tree ist reduzierbar und damit *
 *             ist der zugrunde liegende Graph planar */
	       is_reducible = true;
	    }/* endif */

	 } else {
/* Step 4: Delete a MINIMUM number of leaves in ROOT such that ROOT becomes *
 *         reducible.  */
/*           fuehre BUBBLE aus, dann COMPUTE_VALUES,
	     bestimme Typ von S_root, PQ-tree ist reduzierbar,
	     g.d.w. typ von S-root B, H oder A, in diesem Fall
	     fuehre reduction aus, im anderen fall fuehre
	     delete_edges und anschl. reduce aus */
/*           DELETE_EDGES;                                                  */
/*           obwohl der PQ-Tree jetzt reduzierbar sein sollte, *
 *           wirde das erst im naechsten Durchlauf der schleife *
 *           bestaetigt, daher wird hier das flag is_planar noch *
 *           nicht auf true gesetzt */

#           ifdef DEBUG
	    fprintf(pqfile,"\nPQ-Tree is not reducible (MAXPLANAR)!");
	    sprintf(buffer,"\nPQ-Tree is not reducible (MAXPLANAR)!");
	    message(buffer);
#           endif
	    is_planar = FALSE;
	    DELETE_MINIMUM_PERTINENT_LEAVES(STACK);
	 } /* endif */
      } /* endwhile */


/* Step 5: Reduce ROOT. T_N is the leaf corresponding to vertex N of G.     *
 *         If N=n, stop. If N<n, go to step 2.                              */

#     ifdef DEBUG
      fprintf(pqfile,"\nPQ-Tree IS reducible !");
      sprintf(buffer,"\nPQ-Tree IS reducible !");
      message(buffer);
#     endif

#     ifdef DEBUG
      fprintf(pqfile,"\n\nComplete PQ-Tree after reduction:");
      print_PQtree(pqfile, ROOT);
#     endif

/*    fprintf(pqfile,
	      "\nPQtree-leaf: type %d  entry %2d  parent %d  l.s. %d  r.s. %d",
	      pqtree_type(T_N), pqtree_entry(T_N),
	      pqtree_entry(pqtree_parent(T_N)),
	      pqtree_entry(pqtree_left_sibling(T_N)),
	      pqtree_entry(pqtree_right_sibling(T_N))); */

/*    free S after use                                                       */
/*    although S is empty after next call and S_LENGTH = 0, the values are *
 *    not changed until the next use of both variables *
 *    be sure to reinitialize them correctly */
      T_N = Reduce_Pertinent_Leaves_To_Unique_Leaf(S, S_LENGTH);
#     ifdef DEBUG
      fprintf(pqfile,"\n\nComplete PQ-Tree after ALL reduction:");
      print_PQtree(pqfile, ROOT);
#     endif
   } /* endwhile */

/* free complete PQ-Tree !! */
   free_complete_PQtree(ROOT);

   return is_planar;  /* serves as an error indicator */
}
#endif


/****************************************************************************\
 *                                                                          *
 *  Global Sgraph maxplanarsubgraph_greedy(Sgraph)                          *
 *  ----------------------------------------------                          *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : graph of type Sgraph                                        *
 *               - in case that the input graph is not planar this will be  *
 *                 the maximal planar subgraph of the input graph.          *
 *              empty_graph                                                 *
 *               - in case that the given input graph is already planar     *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : global function DETERMINE_BICONNECTED_COMPONENTS and        *
 *              local functions planarize and maxplanarize                  *
 *                                                                          *
 *  task      : determines all biconnected components and computes for each *
 *              at first a spanning planar subgraph and then makes this     *
 *              graph maximal by inserting possible edges.                  *
 *              if the input graph itself is planar, then the functions     *
 *              work directly on that and marks the attributes in edges.    *
 *                                                                          *
\****************************************************************************/

Global Sgraph	maxplanarsubgraph_greedy(Sgraph G)
{

    Sgraph	mpg;
    Slist    	restlist;
    Snode	node, new_node;
    Sedge	edge, new_edge;
    int		pq_return;
    int		delEdges;

/*	mark all edges: edge_in_mpg = FALSE */
/*	create a spanning tree T on G *
 *	try for every edge not in T to insert it into G, *
 *	while maintaining planarity */

/*	several alternatives are possible: *
 *   	 i) create different enumerations on the edges (randomized) and *
 *	    compare the results, according to the number of edges in the *
 *	    final maximal planar subgraph *
 *	ii) if an inserted edge destroys planarity, keep that edge *
 *	    and delete the other edges that disturb the edge to be inserted */

   mpg = empty_sgraph;

   if ((G) && (G->nodes)) {
      create_and_init_all_attributes(G);

      if ((pq_return = pq_planarity(G)) != TRUE) {
	 if (pq_return == MULTI_EDGE) {
	    message("\ngraph has multiple edges.\n");
	    clear_all_attributes(G);
	    return empty_sgraph;
	 } else if (pq_return == LOOP) {
	    message("\ngraph has self loops.\n");
	    clear_all_attributes(G);
	    return empty_sgraph;
	 } /* endif */

/*	 since in the planarity test a dfs is performed it is not necessary *
 *	 to perform it in the next step 				    */
/*       the dfs implicates a dfs spanning tree on the graph */
/*       mpg = DFS_SPANNING_TREE(G, &restlist); */

	 restlist = empty_slist;
	 mpg = make_graph(empty_attrs);
	 mpg->directed = FALSE;
	 for_all_nodes(G, node) {
	    new_node = make_node(mpg, empty_attrs);
	    new_node->nr  = node->nr;
	    new_node->x   = node->x;
	    new_node->y   = node->y;
	    if (node->label != NULL) {
	       new_node->label = NEW_LABEL;
	       strcpy(new_node->label, node->label);
	    } /* endif */
	    new_node->iso = node;
	    node->iso     = new_node;
	 } end_for_all_nodes(G, node);

	 for_all_nodes(G, node) {
	    for_sourcelist(node, edge) {
	       if (node->nr < edge->tnode->nr) {
		  if (edge_type(edge) == TREE_EDGE) {
		     new_edge = make_edge(node->iso, edge->tnode->iso,
					  empty_attrs);
		     edge_iso(edge) = new_edge;
		  } else {
		     restlist = enqueue(restlist, edge);
		     edge_in_mpg(edge) = FALSE;
		  } /* endif */
	       } /* endif */
	    } end_for_sourcelist(node, edge);
	 } end_for_all_nodes(G, node);

	 create_and_init_all_attributes(mpg);

/*       for every edge in restlist, try to include in graph, while         *
 *       maintaining planarity                                              */
	 delEdges = 0;
	 while (!isempty(restlist)) {
	    edge = sedge_in_slist(restlist);
	    restlist = rest(restlist);

/*          create new_edge                                                 */
	    new_edge = make_edge(edge->snode->iso, edge->tnode->iso,
				 empty_attrs);
	    create_and_init_edge_attributes(new_edge);
	    re_init_all_attributes(mpg);
	    edge_in_mpg(edge) = TRUE;
	    edge_iso(edge) = new_edge;

/*	    if (planarity(mpg) != SUCCESS) { */ 
	    if (pq_planarity(mpg) != TRUE) {
/*             edge destroys planarity                                      */
	       clear_edge_attributes(new_edge);
	       remove_edge(new_edge);
	       edge_in_mpg(edge) = FALSE;
	       edge_iso(edge) = empty_sedge;
	       delEdges++;

/*          else edge may remain in graph, since planarity is maintained    */
	    } /* endif */
	 } /* endwhile */

/*       free all allocated space for attributes                            */
	 clear_all_attributes(mpg);
	 store_edges_in_mpg_edge_attrs(G);
	 clear_all_attributes(G);
	 store_mpg_edges_in_graph_edge_attrs(mpg);

         if (!currMaxPlanarSettings) {
	    message("\nno settings available !\n");
         } else {
	    currMaxPlanarSettings->deleted_edge_count = delEdges;
	    currMaxPlanarSettings->re_inserted_edge_count = 0;
         } /* endif */

	 return mpg;

      } else {  /* input-graph was already planar */
	 clear_all_attributes(G);
	 return mpg;
      } /* endif */
   } else {

/*    no graph found or graph has no nodes attached                         */
      return mpg;

   } /* endif */
}



/****************************************************************************\
 *                                                                          *
 *  Global Sgraph maxplanarsubgraph_randomized_greedy(Sgraph)               *
 *  ---------------------------------------------------------               *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : graph of type Sgraph                                        *
 *               - in case that the input graph is not planar this will be  *
 *                 the maximal planar subgraph of the input graph.          *
 *              empty_graph                                                 *
 *               - in case that the given input graph is already planar     *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : global function DETERMINE_BICONNECTED_COMPONENTS and        *
 *              local functions planarize and maxplanarize                  *
 *                                                                          *
 *  task      : this function determines  a maximal planar subgraph 	    *
 *		this is done by randomly inserting edges into an empty 	    *
 *		graph, this function does not start with a dfs-spanning     *
 *		tree, but gives all edges the same chance to be inserted    *
 *		the main idea is to start this algorithm iterated, and then *
 *		to compare the results and to take the best.                *
 *                                                                          *
\****************************************************************************/

Global Sgraph	maxplanarsubgraph_randomized_greedy(Sgraph G)
{

    Sgraph	mpg;
    Slist    	restlist, Selem;
    int		restcount;
    Snode	node, new_node;
    Sedge	edge, new_edge;
    int		pq_return;

/*	mark all edges: edge_in_mpg = FALSE */
/*	create a spanning tree T on G *
 *	try for every edge not in T to insert it into G, *
 *	while maintaining planarity */

/*	several alternatives are possible: *
 *   	 i) create different enumerations on the edges (randomized) and *
 *	    compare the results, according to the number of edges in the *
 *	    final maximal planar subgraph *
 *	ii) if an inserted edge destroys planarity, keep that edge *
 *	    and delete the other edges that disturb the edge to be inserted */

   mpg = empty_sgraph;

   if ((G) && (G->nodes)) {
      create_and_init_all_attributes(G);

      if ((pq_return = pq_planarity(G)) != TRUE) {
	 if (pq_return == MULTI_EDGE) {
	    message("\ngraph has multiple edges.\n");
	    clear_all_attributes(G);
	    return empty_sgraph;
	 } else if (pq_return == LOOP) {
	    message("\ngraph has self loops.\n");
	    clear_all_attributes(G);
	    return empty_sgraph;
	 } /* endif */

/*	 since in the planarity test a dfs is performed it is not necessary *
 *	 to perform it in the next step 				    */
/*       the dfs implicates a dfs spanning tree on the graph */
/*       mpg = DFS_SPANNING_TREE(G, &restlist); */

	 init_random_number_generator();

	 restlist = empty_slist;
	 restcount = 0;

	 mpg = make_graph(empty_attrs);
	 mpg->directed = FALSE;
	 for_all_nodes(G, node) {
	    new_node = make_node(mpg, empty_attrs);
	    new_node->nr  = node->nr;
	    new_node->x   = node->x;
	    new_node->y   = node->y;
	    if (node->label != NULL) {
	       new_node->label = NEW_LABEL;
	       strcpy(new_node->label, node->label);
	    } /* endif */
	    new_node->iso = node;
	    node->iso     = new_node;
	 } end_for_all_nodes(G, node);

	 for_all_nodes(G, node) {
	    for_sourcelist(node, edge) {
	       if (node->nr < edge->tnode->nr) {
		  restlist = enqueue(restlist, edge);
		  restcount++;
		  edge_in_mpg(edge) = FALSE;
	       } /* endif */
	    } end_for_sourcelist(node, edge);
	 } end_for_all_nodes(G, node);

	 create_and_init_all_attributes(mpg);

/*       for every edge in restlist, try to include in graph, while         *
 *       maintaining planarity                                              */
	 while (!isempty(restlist)) {
/*	    insert here randomized choice of edges			    */
	    Selem = get_slist_elem_by_random(restlist, restcount);
	    edge = sedge_in_slist(Selem);

	    restlist = remove_slist_elem(restlist, Selem);
	    restcount--;

/*          create new_edge                                                 */
	    new_edge = make_edge(edge->snode->iso, edge->tnode->iso,
				 empty_attrs);
	    create_and_init_edge_attributes(new_edge);
	    re_init_all_attributes(mpg);
	    edge_in_mpg(edge) = TRUE;
	    edge_iso(edge) = new_edge;

/*	    if (planarity(mpg) != SUCCESS) { */
	    if (pq_planarity(mpg) != TRUE) {
/*             edge destroys planarity                                      */
	       clear_edge_attributes(new_edge);
	       remove_edge(new_edge);
	       edge_in_mpg(edge) = FALSE;
	       edge_iso(edge) = empty_sedge;

/*          else edge may remain in graph, since planarity is maintained    */
	    } /* endif */
	 } /* endwhile */

/*       free all allocated space for attributes                            */
	 clear_all_attributes(mpg);
	 store_edges_in_mpg_edge_attrs(G);
	 clear_all_attributes(G);
	 store_mpg_edges_in_graph_edge_attrs(mpg);
	 return mpg;

      } else {  /* input-graph was already planar */
	 clear_all_attributes(G);
	 return mpg;
      } /* endif */
   } else {

/*    no graph found or graph has no nodes attached                         */
      return mpg;

   } /* endif */
}



/****************************************************************************\
 *                                                                          *
 *  Local functions                                                        *
 *  ---------------                                                         *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  only to be called from this module.				            *
 *                                                                          *
\****************************************************************************/



/****************************************************************************\
 *                                                                          *
 *  Local       Sgraph  create_mpg_from_graph(Sgraph)                       *
 *  ------------------------------------------------                        *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached an  *
 *		edge of type Sedge in its attribute-field.		    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : determine these edges of a graph, that are not members of   *
 *	        the corresponding maximal planar subgraph.		    *
 *		These edges can be recognized by looking at their 	    *
 *		attribute field:					    *
 *		any edges which has a counterpart in the maximal planar     *
 *		subgraph refers to this edge, other edges have a NULL-entry *
 *		and will therefore be added to the list of 'not_mpg_edges.  *
 *                                                                          *
\****************************************************************************/
#if 0
Local Sgraph create_mpg_from_graph(Sgraph ingraph)
{
   Sgraph	outgraph;
   Snode	node, new_node;
   Sedge	edge, new_edge;

   if (ingraph != empty_graph) {
      outgraph = make_graph(empty_attrs);
      outgraph->directed = FALSE;
      for_all_nodes(ingraph, node) {
	 new_node = make_node(outgraph, empty_attrs);
	 new_node->nr  = node->nr;
	 new_node->x   = node->x;
	 new_node->y   = node->y;
	 if (node->label != NULL) {
	    new_node->label = NEW_LABEL;
	    strcpy(new_node->label, node->label);
	 } /* endif */
	 new_node->iso = node;
	 node->iso     = new_node;
      } end_for_all_nodes(ingraph, node);

      for_all_nodes(ingraph, node) {
	 for_sourcelist(node, edge) {
	    if (node->nr < edge->tnode->nr) {
	       if (edge_in_mpg(edge)) {
		  new_edge = make_edge(node->iso, edge->tnode->iso,
					empty_attrs);
		  set_attr_data(new_edge, edge);
		  set_attr_data(new_edge->tsuc, edge->tsuc);
		  edge_iso(edge) = new_edge;
	       } /* endif */
	    } /* endif */
	 } end_for_sourcelist(node, edge);
      } end_for_all_nodes(ingraph, node);

   } else {
      outgraph = empty_graph;
   } /* endif */

   return outgraph;
}
#endif

/****************************************************************************\
 *                                                                          *
 *  Local       Sgraph  create_max_mpg_from_graph(Sgraph)                   *
 *  -----------------------------------------------------                   *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached an  *
 *		edge of type Sedge in its attribute-field.		    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : determine these edges of a graph, that are not members of   *
 *	        the corresponding maximal planar subgraph.		    *
 *		These edges can be recognized by looking at their 	    *
 *		attribute field:					    *
 *		any edges which has a counterpart in the maximal planar     *
 *		subgraph refers to this edge, other edges have a NULL-entry *
 *		and will therefore be added to the list of 'not_mpg_edges.  *
 *                                                                          *
\****************************************************************************/

Local Sgraph create_max_mpg_from_graph(Sgraph ingraph, int *edgeCount1, int *edgeCount2)
{
   Sgraph	outgraph;
   Snode	node, new_node;
   Sedge	edge, new_edge;
   Slist	restlist;
   int		restcount, reInsertCount, cc, ccn;

   if (ingraph != empty_graph) {
      outgraph = make_graph(empty_attrs);
      outgraph->directed = FALSE;
      ccn = 0;
      for_all_nodes(ingraph, node) {
	 ccn++;
	 new_node = make_node(outgraph, empty_attrs);
	 new_node->nr  = node->nr;
	 new_node->x   = node->x;
	 new_node->y   = node->y;
	 if (node->label != NULL) {
	    new_node->label = NEW_LABEL;
	    strcpy(new_node->label, node->label);
	 } /* endif */
	 new_node->iso = node;
	 node->iso     = new_node;
      } end_for_all_nodes(ingraph, node);

      restlist = empty_slist;
      restcount = 0;

      for_all_nodes(ingraph, node) {
	 for_sourcelist(node, edge) {
	    if (node->nr < edge->tnode->nr) {
	       if (edge_in_mpg(edge)) {
		  new_edge = make_edge(node->iso, edge->tnode->iso,
					empty_attrs);
		  edge_iso(edge) = new_edge;
	       } else {
		  restlist = enqueue(restlist, edge);
		  restcount++;
	       } /* endif */
	    } /* endif */
	 } end_for_sourcelist(node, edge);
      } end_for_all_nodes(ingraph, node);

      *edgeCount1 = restcount;

      create_and_init_all_attributes(outgraph);
      reInsertCount = 0;
      cc = 0;
      while (!isempty(restlist)) {
	 edge = sedge_in_slist(restlist);
	 restlist = rest(restlist);
	 restcount--;
	 cc++;

	 if (ACCURACY_TRADEOFF(cc)) {
	 new_edge = make_edge(edge->snode->iso, edge->tnode->iso,
				 empty_attrs);
	 reInsertCount++;
	 create_and_init_edge_attributes(new_edge);
	 re_init_all_attributes(outgraph);
	 edge_in_mpg(edge) = TRUE;
	 edge_iso(edge) = new_edge;

	    if (pq_planarity(outgraph) != TRUE) {
/*             edge destroys planarity                                      */
	       clear_edge_attributes(new_edge);
	       remove_edge(new_edge);
	       reInsertCount--;
	       edge_in_mpg(edge) = FALSE;
	       edge_iso(edge) = empty_sedge;

/*          else edge may remain in graph, since planarity is maintained    */
	    } /* endif */
	 } /* endif */

	 } /* endwhile */

      clear_all_attributes(outgraph);

      for_all_nodes(ingraph, node) {
	 for_sourcelist(node, edge) {
	    if (node->nr < edge->tnode->nr) {
	       if (edge_in_mpg(edge)) {
		  new_edge = edge_iso(edge);
		  set_attr_data(new_edge, edge);
		  set_attr_data(new_edge->tsuc, edge->tsuc);
	       } /* endif */
	    } /* endif */
	 } end_for_sourcelist(node, edge);
      } end_for_all_nodes(ingraph, node);

      *edgeCount2 = reInsertCount;


   } else {
      outgraph = empty_graph;
      *edgeCount1 = 0;
      *edgeCount2 = 0;
   } /* endif */

   return outgraph;
}


/****************************************************************************\
 *                                                                          *
 *  Local	void	store_mpg_edges_in_graph_edge_attrs(Sgraph)         *
 *  ---------------------------------------------------------------         *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached an  *
 *		edge of type Sedge in its attribute-field.		    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : determine these edges of a graph, that are not members of   *
 *	        the corresponding maximal planar subgraph.		    *
 *		These edges can be recognized by looking at their 	    *
 *		attribute field:					    *
 *		any edges which has a counterpart in the maximal planar     *
 *		subgraph refers to this edge, other edges have a NULL-entry *
 *		and will therefore be added to the list of 'not_mpg_edges.  *
 *                                                                          *
\****************************************************************************/

Local	void	store_mpg_edges_in_graph_edge_attrs(Sgraph g)
{
   Snode n;
   Sedge e, iso_edge;


   for_all_nodes(g, n) {
      for_sourcelist(n, e) {
	 if (n->nr < e->tnode->nr) {
	    iso_edge = attr_data_of_type(e, Sedge);
	    set_attr_data(iso_edge, e);
	    set_attr_data(iso_edge->tsuc, e->tsuc);
	 } /* endif */
      } end_for_sourcelist(n, e);
   } end_for_all_nodes(g, n);
}


/****************************************************************************\
 *                                                                          *
 *  Local	void	store_edges_in_mpg_edge_attrs(Sgraph)		    *
 *  ---------------------------------------------------------               *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached an  *
 *		edge of type Sedge in its attribute-field.		    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : determine these edges of a graph, that are not members of   *
 *	        the corresponding maximal planar subgraph.		    *
 *		These edges can be recognized by looking at their 	    *
 *		attribute field:					    *
 *		any edges which has a counterpart in the maximal planar     *
 *		subgraph refers to this edge, other edges have a NULL-entry *
 *		and will therefore be added to the list of 'not_mpg_edges.  *
 *                                                                          *
\****************************************************************************/

Local	void	store_edges_in_mpg_edge_attrs(Sgraph g)
{
   Snode n;
   Sedge e, iso_edge;

   for_all_nodes(g, n) {
      for_sourcelist(n, e) {
	 if (n->nr < e->tnode->nr) {
	    if (edge_in_mpg(e)) {
	       iso_edge = edge_iso(e);
	       set_attr_data(iso_edge, e);
	       set_attr_data(iso_edge->tsuc, e->tsuc);
	    } /* endif */
	 } /* endif */
      } end_for_sourcelist(n, e);
   } end_for_all_nodes(g, n);
}

#if 0
Local	int	recount_graph(Sgraph g)
{
	Snode	n;
	Sedge	e;
	int	deletionCount;

	deletionCount = 0;
	for_all_nodes(g, n) {
	   for_sourcelist(n, e) {
	      if (n->nr < e->tnode->nr) {
		 if (!edge_in_mpg(e)) {
/*		 if (attr_data_of_type(e, Sedge) == empty_sedge) { */
		    deletionCount++;
		 }  /* endif */
	      }  /* endif */
	   } end_for_sourcelist(n, e);
	} end_for_all_nodes(g, n);

	return deletionCount;
}


#endif

