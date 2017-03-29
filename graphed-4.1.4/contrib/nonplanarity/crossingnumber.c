/****************************************************************************\
 *                                                                          *
 *  crossingnumber.c                                                        *
 *  ----------------                                                        *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/

#include <math.h>

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

#include "sattrs.h"
#include "stnumber.h"
#include "maxplanarsubgraph.h"
#include "crossingnumber.h"

# if defined SUN_VERSION
Local	double	mysqrt(double arg);
Local	int	mydiv(int n, int m);
#else
Local double	mysqrt(double);
Local int	mydiv(int, int);
#endif


/* const double pi = 3.141592654; */

#define	distance_between(x1, y1, x2, y2)	(mysqrt((float)(((x1-x2)*(x1-x2)) + \
							      ((y1-y2)*(y1-y2)))))


#if defined SUN_VERSION
Local   int     embedNonPlanarGraphNaive(Sgraph g);
Local	Slist	sort_nodes_by_y_coordinate_ascending(Sgraph graph);
/*Local	Slist	sort_nodes_by_degree_descending(Sgraph graph);*/
Local	Slist	insort_node_by_embedded_degree_descending(Slist list, Snode node);
Local	int	embed_new_edge(Sedge insertedge, Slist nodelist);
Local	bool	edges_do_cross(int e1x1, int e1y1, int e1x2, int e1y2, int e2x1, int e2y1, int e2x2, int e2y2);
Local	void	embed_initial_node(Snode n);
Local	int	embed_node_strategyI(Snode n);
Local	void	embed_node_in_complete_graph(Snode n, int i);
Local void	rotate_and_stretch_node(Snode n, double angle, int factor);
#else
Local   int     embedNonPlanarGraphNaive(Sgraph);
Local	Slist	sort_nodes_by_y_coordinate_ascending(Sgraph);
/*Local	Slist	sort_nodes_by_degree_descending(Sgraph);*/
Local	Slist	insort_node_by_embedded_degree_descending(Slist, Snode);
Local	int	embed_new_edge(Sedge, Slist);
Local	bool	edges_do_cross(int, int, int, int, int, int, int, int);
Local	void	embed_initial_node(Snode);
Local	int	embed_node_strategyI(Snode);
Local	void	embed_node_in_complete_graph(Snode, int);
Local	void	rotate_and_stretch_node(Snode, double, int);
#endif


extern  char    buffer[];
FILE	*crossfile;




/****************************************************************************\
 *                                                                          *
 *  Global int	embed_remaining_edges(Sgraph)                               *
 *  -----------------------------------------                               *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : counter of type int, returns the number of crossings        *
 *		that actually exist in the here determined embedding. 	    *
 *		(see task for details).					    *
 *                                                                          *
 *              in case that the given input graph is already planar,       *
 *		0 is returned as counter.                                   *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : global function maxplanarsubgraph in module maxplana.c and  *
 *              several local functions 				    *
 *                                                                          *
 *  task      : the crossing-number of a graph G = (V,E) is defined by the  *
 *		minimum of edge-crossings in all possible embeddings of a   *
 *		given graph.			         		    *
 *									    *
 *		the work is done by firstly computing the mpg of the input  *
 *		graph. This graph can be drawn planarily by several well    *
 *	        known planar-embedding-algorithms, which will be the basis  *
 *		for further operations. The idea is now to insert the 	    *
 *		remaining edges by avoiding as many crossings as possible.  *
 *									    *
 *		if the input graph is already planar, nothing is done.      *
 *                                                                          *
\****************************************************************************/

Global int	embed_remaining_edges(Sgraph inG)
{
   Slist	sortedNodeList, edgeList;
   Sedge	edge;
   int		crossingCount, edgeCrossings;
   int		edgeCount;
   Slist	Selem;

/* first step:								    */
/*   - get maximal planar subgraph, 					    *
 *     if empty_graph is returned by function maximalplanarsubgraph 	    *
 *     then the input graph inG was already planar, 			    *
 *     return 0 and set outList to empty_slist 				    */
/*   - create slist of edges that are not in the mpg of the input graph	    */

   crossingCount = 0;
   edgeList = attr_data_of_type(inG, Slist);

   edgeCount = 0;

   if (isempty(edgeList)) {
      sprintf(buffer, "\nembed: found %d edges to embed.", edgeCount);
      message(buffer);
      return 0;
   } /* endif */

   for_slist(edgeList, Selem) {
      edgeCount++;
   } end_for_slist(edgeList, Selem);
   sprintf(buffer, "\nembed: found %d edges to embed.", edgeCount);
   message(buffer);

/*    NOTE:                                                                 *
 *    the maximal planar subgraph of any graph is defined by a subset of    *
 *    its edges (and not of its nodes), i.e. it is a spanning subgraph	    */

      sortedNodeList = sort_nodes_by_y_coordinate_ascending(inG);

      while (!isempty(edgeList)) {
	 edge = sedge_in_slist(edgeList);

/*	 returns the number of crossings, that are caused by inserting edge */
	 edgeCrossings = embed_new_edge(edge, sortedNodeList);

	 crossingCount = crossingCount + edgeCrossings;

/*	 delete edge from edgeList					    */
	 edgeList = rest(edgeList);
      } /* endwhile */

/*    free sortedNodeList						    */
      while (!isempty(sortedNodeList)) {
	 sortedNodeList = rest(sortedNodeList);
      } /* endwhile */

      set_graphattrs(inG, empty_attrs);

      return crossingCount;
}



/****************************************************************************\
 *                                                                          *
 *  Global int	crossingnumber_naive_embedding(Sgraph)                      *
 *  --------------------------------------------------                      *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : counter of type int, returns the number of crossings        *
 *		that actually exist in the here determined embedding. 	    *
 *		(see task for details).					    *
 *                                                                          *
 *              in case that the given input graph is already planar,       *
 *		0 is returned as counter.                                   *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : global function maxplanarsubgraph in module maxplana.c and  *
 *              several local functions 				    *
 *                                                                          *
 *  task      : the crossing-number of a graph G = (V,E) is defined by the  *
 *		minimum of edge-crossings in all possible embeddings of a   *
 *		given graph.			         		    *
 *									    *
 *		the work is done by firstly computing the mpg of the input  *
 *		graph. This graph can be drawn planarily by several         *
 *	        planar-embedding-algorithms, which will be the basis        *
 *		for further operations. The idea is now to insert the 	    *
 *		remaining edges by avoiding as many crossings as possible.  *
 *
 *		The embedding-algorithms called from this function are      *
 *		all very naive, since they embed the whole graph, without   *
 *		computing the maximalplanarsubgraph first. They are the     *
 *		basis for the comparison of results found here, and results *
 *		found by applying well-known embedding-algorithms for       *
 *		planar graphs.		            			    *
 *									    *
 *		if the input graph is already planar, nothing is done.      *
 *                                                                          *
\****************************************************************************/

Global int	crossingnumber_naive_embedding(Sgraph inG)
{
   int		crossingCount;
/* Slist	sortedNodeList; */

#ifdef OTHER_EMBEDDING
   Sgraph	mpg;
#endif

/* first step:								    */

/*    EMBED THE GRAPH    				    	            */
/*    .....								    */
      create_and_init_all_attributes(inG);
/*    sortedNodeList = sort_nodes_by_y_coordinate_ascending(inG); */
      crossingCount = embedNonPlanarGraphNaive(inG);

#     ifdef OTHER_EMBEDDING
      if (!IsConnected(mpg)) {
	 error ("Graph is not connected.\n");
	 return 0;
      } else if (!IsBiConnected(mpg)) {
	 error ("Graph is not biconnected.\n");
	 return 0;
      } /* endif */

      if (DrawConvexPossible(mpg)) {
	 ExtendFacialCycle(mpg,ConvexityTest(mpg),TRUE);
      } /* endif */
#     endif

      clear_all_attributes(inG);

      return crossingCount;
}


/****************************************************************************\
 *                                                                          *
 *  Global int	crossingnumber_complete_embedding(Sgraph)   	            *
 *  -----------------------------------------------------    	            *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : counter of type int, returns the number of expected 	    *
 *		crossings when embedding a complete graph.	   	    *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : ---				 			    *
 *                                                                          *
 *  task      : the crossing-number of a graph G = (V,E) is defined by the  *
 *		minimum of edge-crossings in all possible embeddings of a   *
 *		given graph.			         		    *
 *									    *
 *		this idea for embedding is especially for complete graphs,  *
 *		although the given graph is not tested for completness, so  *
 *		this algorithm may be called for any graph, but the results *
 *		for some graphs may be unexpected.                          *
 *                                                                          *
 *		this arrangement is assumed to be the best for              *
 *		complete graphs.                                            *
 *                                                                          *
\****************************************************************************/

Global int	crossingnumber_complete_embedding(Sgraph inG)
{
   Snode	n;
   int		nodes;
   int          nodeCount1, nodeCount2;
   int		index;

/* expect no attributes assigned to nodes and edges in graph		    */
/* reset node_flags to UNVISITED 					    */
   nodes = 0;
   for_all_nodes(inG, n) {
      nodes++;
   } end_for_all_nodes(inG, n);

   nodeCount1 = (nodes + 2) / 3;
   nodeCount2 = (nodes + 1) / 3;


#  ifdef DEBUG
   crossfile = fopen ("crossing.out", "w");
#  endif

   index = 1;

   for_all_nodes(inG, n) {
      if (index <= nodeCount1) {
	 embed_node_in_complete_graph(n, index);
      } else if (index <= nodeCount1 + nodeCount2) {
	 embed_node_in_complete_graph(n, index - nodeCount1);
      } else {
	 embed_node_in_complete_graph(n, index - (nodeCount1 + nodeCount2));
      } /* endif */
      index++;
   } end_for_all_nodes(inG, n);

   index = 1;
   for_all_nodes(inG, n) {
      if (index <= nodeCount1) {
	 rotate_and_stretch_node(n, 0.0, MIN_DISTANCE*2);
      } else if (index <= nodeCount1 + nodeCount2) {
	 rotate_and_stretch_node(n, /* 2.094395 */ 1.9, MIN_DISTANCE*2);
      } else {
	 rotate_and_stretch_node(n, /* 4.188790 */ 4.0, MIN_DISTANCE*2);
      } /* endif */
      index++;
   } end_for_all_nodes(inG, n);

#  ifdef DEBUG
   fclose(crossfile);
#  endif


   return 0;
}



/****************************************************************************\
 *                                                                          *
 *  Global int	crossingnumber_bipartite_embedding(Sgraph)   	            *
 *  ------------------------------------------------------    	            *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : counter of type int, returns the number of expected 	    *
 *		crossings when embedding a bipartite complete graph.	    *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : is_bipartite (in module stnumber.c) 			    *
 *                                                                          *
 *  task      : the crossing-number of a graph G = (V,E) is defined by the  *
 *		minimum of edge-crossings in all possible embeddings of a   *
 *		given graph.			         		    *
 *									    *
 *		the given graph is tested wether it is bipartite or not.    *
 *		if it is bipartite the nodes are marked according to their  *
 *		presence in one of the two node-sets U, V, and are arranged *
 *		as follows:                                                 *
 *		set the coordinates of the center of the arrangement        *
 *		(predefined here) and put the nodes of the set U along the  *
 *		x-axis and the nodes of the set V along the y-axis.         *
 *                                                                          *
 *		this arrangement is assumed to be the best for bipartite    *
 *		complete graphs.                                            *
 *                                                                          *
\****************************************************************************/

Global int	crossingnumber_bipartite_embedding(Sgraph inG)
{
   Snode	n;
   int		layer1_count, layer2_count;
   int		layer1_sign,  layer2_sign;

      layer1_count = 0;
      layer2_count = 0;
      layer1_sign  = 1;
      layer2_sign  = 1;

      for_all_nodes(inG, n) {
	 if (attr_flags(n) == LAYER1) {
	    if (layer1_sign > 0) {
	       layer1_count++;
	    } /* endif */
	    n->x = CENTER_X + layer1_sign * (MIN_DISTANCE * layer1_count);
	    n->y = CENTER_Y;
	    layer1_sign = layer1_sign * (-1);
	 } else {

	    if (layer2_sign > 0) {
	       layer2_count++;
	    } /* endif */
	    n->x = CENTER_X;
	    n->y = CENTER_Y + layer2_sign * (MIN_DISTANCE * layer2_count);
	    layer2_sign = layer2_sign * (-1);
	 } /* endif */
      } end_for_all_nodes(inG, n);

      for_all_nodes(inG, n) {
	 attr_data(n) = NULL;
      } end_for_all_nodes(inG, n);

     return 0;
}



/****************************************************************************\
 *                                                                          *
 *  Global	Slist	get_edges_not_in_mpg(Sgraph)			    *
 *  ------------------------------------------------                        *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached an  *
 *		edge of type Sedge in its attribute-field.		    *
 *                                                                          *
 *  call from : main module (this function may be called by the user)       *
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

Global	Slist	get_edges_not_in_mpg(Sgraph g)
{
   Snode	n;
   Sedge	e;
   Slist	el;

/* the edges of g that are in the maximalplanarsubgraph(g) have a pointer   *
 * to the coreesponding edge in their attr_field.			    */

   el = empty_slist;
   for_all_nodes(g, n) {
      for_sourcelist(n, e) {
/*       store each edge of undirected graph only once.			    */
	 if (n->nr < e->tnode->nr) {
	    if (!attr_data_of_type(e, Sedge)) {
	       el = enqueue(el, e);
	    }  /* endif */
	 }  /* endif */
      } end_for_sourcelist(n, e);
   } end_for_all_nodes(g, n);

   return el;
}


/****************************************************************************\
 *                                                                          *
 *  Global	int	count_all_edge_crossings_in_graph(Sgraph)	    *
 *  -------------------------------------------------------------           *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : int, counter of edge crossings that were found in the       *
 *              given embedding of the given graph.                         *
 *                                                                          *
 *  call from : main module (this function may be called by the user)       *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : for all edges that are present in a graph, determine wether *
 *		they cross or not. Try to avoid as much work as possible    *
 *		starting some pre-tests, as there are test for incidence of *
 *		two edges.						    *
 *		Imply on every edge a direction from low to high 	    *
 *		(y-coordinate) and in case of a draw left to right 	    *
 *		(x-coordinate) for reason of uniqueness of edges.	    *
 *                                                                          *
\****************************************************************************/

Global  int      count_all_edge_crossings_in_graph(Sgraph g, int *ncount, int *ecount)
          
                
   	         /* counter for nodes and edges */
{
   Snode        n, n2, firstnode;
   Sedge        e, e2;
   int          crossings;
   int		nodeCount, edgeCount;

#  ifdef DEBUG
   FILE	*crossfile;

   crossfile = fopen("cross.out", "w");
#  endif

   crossings = 0;
   if ((g == empty_sgraph) || (g->nodes == empty_snode)) {
      return 0;
   } /* endif */

   nodeCount = 0;
   edgeCount = 0;
   firstnode = g->nodes;
   for_all_nodes(g, n) {
      nodeCount++;
      if (n->slist != empty_sedge) {
      for_sourcelist(n, e) {
	 if (n->nr < e->tnode->nr) {
	    edgeCount++;
	 } /* endif */
	 if ((e->tnode->y > e->snode->y) ||
	     ((e->tnode->y == e->snode->y) &&
	      (e->tnode->x > e->snode->x))) {
	    n2 = n->suc;
	    while (n2 != firstnode) {
	       if (n2 != e->tnode) {
		  for_sourcelist(n2, e2) {
		     if ((e2->tnode != n) && (e2->tnode != e->tnode)) {
			if ((e2->tnode->y > e2->snode->y) ||
			    ((e2->tnode->y == e2->snode->y) &&
			     (e2->tnode->x > e2->snode->x))) {
#			   ifdef DEBUG
			   fprintf(crossfile,
				   "\ntest edge %d (%3d/%3d) -> %d (%3d/%3d)",
				   e->snode->nr, e->snode->x, e->snode->y,
				   e->tnode->nr, e->tnode->x, e->tnode->y);
			   fprintf(crossfile,
				   "  against edge %d (%3d/%3d) -> %d (%3d/%3d)",
				   e2->snode->nr, e2->snode->x, e2->snode->y,
				   e2->tnode->nr, e2->tnode->x, e2->tnode->y);
#			   endif
			   if (edges_do_cross(e->snode->x, e->snode->y,
					      e->tnode->x, e->tnode->y,
					      e2->snode->x, e2->snode->y,
					      e2->tnode->x, e2->tnode->y)) {
#			      ifdef DEBUG
			      fprintf(crossfile, "   found SUCCESS");
#			      endif
			      crossings++;
			   } else {
#			      ifdef DEBUG
			      fprintf(crossfile, "   found NO SUCCESS");
#			      endif
			   } /* endif */
			} /* endif */
		     } /* endif */
		  } end_for_sourcelist(n2, e2);
	       } /* endif */
	       n2 = n2->suc;
	    } /* endwhile */
	 } /* endif */
      } end_for_sourcelist(n, e);
      } /* endif */
   } end_for_all_nodes(g, n);

#  ifdef DEBUG
   fclose(crossfile);
#  endif

   (*ncount) = nodeCount;
   (*ecount) = edgeCount;
   return crossings;
}



Global	int	estimate_crossings_in_complete_graphs(Sgraph graph)
{
   int		n, maxiteration;
   unsigned int	crossings;
   float        limit;

   if (graph != empty_sgraph) {
      return 0;
   } else {
	 maxiteration = 500;
	 n = 5;

	 crossings = ((n/2) * (n-1)/2 * (n-2)/2 * (n-3)/2) / 4;

	 while (n < maxiteration) {
	    n++;
	    crossings = (crossings *  (n/2)) / ((n-4)/2);
	    limit = ((float) crossings) / (n*n*n*n);
	 } /* endwhile */

      return 0;
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
 *  Local   int     embedNonPlanarGraphNaive(Sgraph)                        *
 *  ------------------------------------------------                        *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : number of crossings that were found in embedding            *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : sort_nodes_by_dgree_descending			            *
 *                                                                          *
 *  task      : sort nodes of a graph by their degree descending            *
 *                                                                          *
 *		note:                                                       *
 *		do not embed two adjacent nodes on a (thought) horizontal   *
 *              or vertical line.                                           *
 *                                                                          *
\****************************************************************************/

Local   int     embedNonPlanarGraphNaive(Sgraph g)
{
   Slist        sortedNodeList;
   Snode        v, v1;
   Snode	n;
   Sedge	e;
   int          crossings;
   int		nodeCount;
   int		maxdeg;


   sortedNodeList = empty_slist;
/* node_visited can be UNVISITED, CURRENT, EMBEDDED   */
/* find three nodes that are mutually adjacent and an initial embedding     *
 * of them	      							    */
   maxdeg = 0;
   nodeCount = 0;

   for_all_nodes(g, n) {
      nodeCount++;
      if (node_degree(n) > maxdeg) {
	 maxdeg = node_degree(n);
	 v = n;
      } /* endif */
   } end_for_all_nodes(g, n);

   if (maxdeg == 0) {
      return 0;
   } /* endif */

   embed_initial_node(v);
   node_visited(v) = EMBEDDED;
   for_sourcelist(v, e) {
      node_marker(e->tnode) = 1;
      node_visited(e->tnode) = CURRENT;
      sortedNodeList =
	 insort_node_by_embedded_degree_descending(sortedNodeList, e->tnode);
   } end_for_sourcelist(v, e);


   while (!isempty(sortedNodeList)) {
      v1 = snode_in_slist(sortedNodeList);
      sortedNodeList = rest(sortedNodeList);
      embed_node_strategyI(v1);
      node_visited(v1) = EMBEDDED;
      for_sourcelist(v1, e) {
	 if (node_visited(e->tnode) != EMBEDDED) {
	    if (node_visited(e->tnode) == CURRENT) {
	       sortedNodeList =
		  remove_slist_elem(sortedNodeList, node_listElem(e->tnode));
	       node_marker(e->tnode)++;
	       sortedNodeList =
		  insort_node_by_embedded_degree_descending(sortedNodeList,
							 e->tnode);
	    } else { /* node_visited(e->tnode) == UNVISITED */
	       node_marker(e->tnode) = 1;
	       node_visited(e->tnode) = CURRENT;
	       sortedNodeList =
		  insort_node_by_embedded_degree_descending(sortedNodeList,
							 e->tnode);
	    } /* endif */
	 } /* endif */
      } end_for_sourcelist(v1, e);
   } /* endwhile */

   return crossings;
}


/****************************************************************************\
 *                                                                          *
 *  Local	Slist	sort_nodes_by_y_coordinate_ascending(Sgraph)	    *
 *  ----------------------------------------------------------------        *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached a   *
 *		node of type Snode in its attribute-field.		    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : sort nodes of a graph by their y-coordinate and in case of  *
 *		equal values by their x-coordinate.			    *
 *                                                                          *
 *		note:                                                       *
 *		it is assumed that no two nodes have exactly the same       *
 *		coordinates (both, x- and y-value).                         *
 *                                                                          *
\****************************************************************************/

Local	Slist	sort_nodes_by_y_coordinate_ascending(Sgraph graph)
{
   Slist	nodelist, cur_elem, new_elem;
   Snode	node;
   int		x, y, max_y, min_y, cur_y, cur_x;


   nodelist = empty_slist;

/* force first node to be added as last element of nodelist		    */
   max_y = graph->nodes->y - 1;
   min_y = graph->nodes->y;

   for_all_nodes(graph, node) {
      x = node->x;
      y = node->y;
      if (y > max_y) {
/*       add edge at end of list 					    */
	 nodelist = add_last(nodelist, node);
	 max_y = y;
      } else if (y < min_y) {
	 nodelist = add_first(nodelist, node);
	 min_y = y;
      } else if (y == min_y) {
	 if (x <= snode_in_slist(nodelist)->x) {
	    nodelist = add_first(nodelist, node);
	 } /* endif */
      } else {
/* 	 min_y <= y <= max_y 						    */
/*	 node will not be inserted as first element in nodelist 	    */
	 cur_elem = nodelist;
	 cur_y = snode_in_slist(cur_elem)->y;
	 while (cur_y < y) {
	    cur_elem = cur_elem->suc;
	    cur_y = snode_in_slist(cur_elem)->y;
	 } /* endwhile */
/*	 cur_y >= y							    */
	 if (cur_y > y) {
	    new_elem = make_slist_elem(node);
	    new_elem->pre = cur_elem->pre;
	    new_elem->suc = cur_elem;
	    cur_elem->pre->suc = new_elem;
	    cur_elem->pre = new_elem;
	 } else {  /* cur_y == y */
	    cur_x = snode_in_slist(cur_elem)->x;
	    while ((cur_y == y) && (cur_x < x)) {
	       cur_elem = cur_elem->suc;
	       cur_x = snode_in_slist(cur_elem)->x;
	       cur_y = snode_in_slist(cur_elem)->y;
	    } /* endwhile */
/*	    assume that cur_x != x since already cur_y == y 		    */
	    new_elem = make_slist_elem(node);
	    new_elem->pre = cur_elem->pre;
	    new_elem->suc = cur_elem;
	    cur_elem->pre->suc = new_elem;
	    cur_elem->pre = new_elem;
	 } /* endif */
      } /* endif */
   } end_for_all_nodes(graph, node);

   return nodelist;
}


/****************************************************************************\
 *                                                                          *
 *  Local	Slist	sort_nodes_by_degree_descending(Sgraph)	    *
 *  -----------------------------------------------------------             *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached a   *
 *		node of type Snode in its attribute-field.		    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : sort nodes of a graph by their degree                       *
 *              i.e. the more edges are incident to a given node, the       *
 *              higher its degree.                                          *
 *                                                                          *
\****************************************************************************/

#if 0
Local	Slist	sort_nodes_by_degree_descending(Sgraph graph)
{
   Slist	nodelist, cur_elem, new_elem;
   Snode	node;
   Sedge	edge;
   int		deg, max_deg, min_deg, cur_deg;


   nodelist = empty_slist;

/* force first node to be added as first element of nodelist		    */
   max_deg = -1;
   min_deg = -1;

   for_all_nodes(graph, node) {
      deg = 0;
      for_sourcelist(node, edge) {
         deg++;
      } end_for_sourcelist(node, edge);
      node_degree(node) = deg;

      if (deg >= max_deg) {
/*       add edge at front of list 					    */
	 nodelist = add_first(nodelist, node);
	 max_deg = deg;

	 if (min_deg == -1) {
            min_deg = deg;
	 } /* endif */

      } else if (deg <= min_deg) {
	 nodelist = add_last(nodelist, node);
	 min_deg = deg;

      } else {
/*	 node will not be inserted as first element in nodelist 	    */
	 cur_elem = nodelist;
	 cur_deg = node_degree(snode_in_slist(cur_elem));

	 while (cur_deg > deg) {
	    cur_elem = cur_elem->suc;
	    cur_deg = node_degree(snode_in_slist(cur_elem));
	 } /* endwhile */

	    new_elem = make_slist_elem(node);
	    new_elem->pre = cur_elem->pre;
	    new_elem->suc = cur_elem;
	    cur_elem->pre->suc = new_elem;
	    cur_elem->pre = new_elem;

      } /* endif */
   } end_for_all_nodes(graph, node);

   return nodelist;
}
#endif

/****************************************************************************\
 *                                                                          *
 *  Local	Slist	insort_node_by_embedded_degree_descending           *
 *							(Slist, Snode)	    *
 *  ------------------------------------------------------------------      *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: nodelist of type Slist                                      *
 *              node of type Snode                                          *
 *                                                                          *
 *  returns   : list of type Slist, in which every element has attached a   *
 *		node of type Snode in its attribute-field.		    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : insort a given node into the given list by degree           *
 *                                                                          *
\****************************************************************************/

Local	Slist	insort_node_by_embedded_degree_descending(Slist list, Snode node)
{
   Slist	cur_elem, new_elem;
   int		deg, cur_deg;
   int		deg2, cur_deg2;


   if (isempty(list)) {
      list = add_first(list, node);
      node_listElem(node) = list;
      return list;
   } else {
      deg = node_marker(node);
      deg2 = node_degree(node);

      if (deg > node_marker(snode_in_slist(list))) {
	 list = add_first(list, node);
	 node_listElem(node) = list;
      } else if ((deg == node_marker(snode_in_slist(list))) &&
		 (deg2 >= node_degree(snode_in_slist(list)))) {
	 list = add_first(list, node);
	 node_listElem(node) = list;
      } else if (deg < node_marker(snode_in_slist(list->pre))) {
	 list = add_last(list, node);
	 node_listElem(node) = list->pre;
      } else if ((deg == node_marker(snode_in_slist(list->pre))) &&
		 (deg2 <= node_degree(snode_in_slist(list->pre)))) {
	 list = add_last(list, node);
	 node_listElem(node) = list->pre;
      } /* endif */

      cur_elem = list;
      cur_deg = node_marker(snode_in_slist(cur_elem));
      cur_deg2 = node_degree(snode_in_slist(cur_elem));

      while ((cur_deg > deg) || ((cur_deg == deg) && (cur_deg2 > deg2))) {
	 cur_elem = cur_elem->suc;
	 cur_deg = node_marker(snode_in_slist(cur_elem));
	 cur_deg2 = node_degree(snode_in_slist(cur_elem));
      } /* endwhile */

      new_elem = make_slist_elem(node);
      node_listElem(node) = new_elem;
      new_elem->pre = cur_elem->pre;
      new_elem->suc = cur_elem;
      cur_elem->pre->suc = new_elem;
      cur_elem->pre = new_elem;
   } /* endif */

   return list;
}


/****************************************************************************\
 *                                                                          *
 *  Local	int	embed_new_edge(Sgraph, Sedge, Slist)		    *
 *  --------------------------------------------------------                *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *		edge of type Sedge					    *
 *		list of type Slist					    *
 *                                                                          *
 *  returns   : the count of crossings that were caused by embedding        *
 *		the given edge to the given graph.                          *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : embed the given edge in the graph and count the resulting   *
 *		crossings. Use for this task the sorted list of nodes.      *
 *		The idea is to start a sweep-line from the lowest node      *
 *		of the graph and to keep a list of current edges which      *
 *		could possibly be crossed by the given edge.		    *
 *                                                                          *
 *		A major problem arises, when any edge does not cross        *
 *		another edge properly, but passes through a node.   	    *
 *		This should be avoided by wobbling the node in its position *
 *		without disturbing the quality of the embedding.	    *
 *                                                                          *
 *		The idea that should be included in this function is to     *
 *		keep the number of crossings small, by intelligently 	    *
 *		embedding the new edge, but at this point there is no idea  *
 *		that can be implemented without the overkill of performing  *
 *		the work of a full embedding-algorithm.			    *
 *                                                                          *
 *		note:                                                       *
 *		it is assumed that no two nodes have exactly the same       *
 *		coordinates (both, x- and y-value).                         *
 *                                                                          *
\****************************************************************************/

Local	int	embed_new_edge(Sedge insertedge, Slist nodelist)
{
   int		count;
   Sedge	edge, new_edge;
   Snode	node, fromnode, tonode;
   Slist	nodeelem;
   int		lownode_x, lownode_y, highnode_x, highnode_y;


   count = 0;

/* delay embedding until new crossings are determined, otherwise it must    *
 * be taken care that no crossing of the new edge with itself is counted    */
/* the parameter insertedge is an edge in the original graph and therefore  *
 * its incident isonodes must be regarded.			            */
   fromnode = insertedge->snode->iso;
   tonode = insertedge->tnode->iso;

/* determine the higher and lower positioned incident node of the edge,     *
 * i.e. their coordinates 						    */
   if (fromnode->y < tonode->y) {
      lownode_x = fromnode->x;
      lownode_y = fromnode->y;
      highnode_x = tonode->x;
      highnode_y = tonode->y;
   } else if (tonode->y < fromnode->y) {
      lownode_x = tonode->x;
      lownode_y = tonode->y;
      highnode_x = fromnode->x;
      highnode_y = fromnode->y;
   } else {
/*    in case of the same y-coord, choose left-to-right order		    */
/*    assume that incident nodes of an edge cannot have the same            *
 *    coordinates in both, x- and y-coordinate				    */
      if (fromnode->x < tonode->x) {
	 lownode_x = fromnode->x;
	 lownode_y = fromnode->y;
	 highnode_x = tonode->x;
	 highnode_y = tonode->y;
      } else if (tonode->x < fromnode->x) {
	 lownode_x = tonode->x;
	 lownode_y = tonode->y;
	 highnode_x = fromnode->x;
	 highnode_y = fromnode->y;
      } /* endif */
   } /* endif */

/* imply on any edge low-to-high order (left-to-right order)		    */
   for_slist(nodelist, nodeelem) {
      node = snode_in_slist(nodeelem);
      if ((node != fromnode) && (node != tonode)) {
/*    node of a crossing edge must lie below the higher node of the         *
 *    other, since edge is regarded only low-to-high			    */
      if (node->y < highnode_y) {
	 for_sourcelist(node, edge) {
	    if ((edge->tnode != fromnode) && (edge->tnode != tonode)) {
/*	       regard edge directed low-to-high (left-to-right)             */
	       if ((edge->tnode->y > edge->snode->y) ||
		   ((edge->tnode->y == edge->snode->y) &&
		    (edge->tnode->x > edge->snode->x))) {
/*                higher node of a crossing edge must lie above the lower   *
 *                node of the other				            */
		  if (edge->tnode->y > lownode_y) {
/*	             do the edges really cross ?		            */
		     if (edges_do_cross(lownode_x, lownode_y,
					highnode_x, highnode_y,
					edge->snode->x, edge->snode->y,
					edge->tnode->x, edge->tnode->y)) {
			count++;
		     } /* endif */
		  } /* endif */
	       } /* endif */
	    } /* endif */
	 } end_for_sourcelist(node, edge);
      } else {
/*	 if nodes are pre-sorted, the loop could be terminated		    */
/*       break;         						    */
      } /* endif */
      } /* endif */
   } end_for_slist(nodelist, nodeelem);

/* create new edge, i.e. embed it straightlined				    */
   new_edge = make_edge(fromnode, tonode, empty_attrs);
   set_attr_data(insertedge, new_edge);
   set_attr_data(insertedge->tsuc, new_edge->tsuc);
/* set_attr_data(new_edge, insertedge);
   set_attr_data(new_edge->tsuc, insertedge->tsuc); */

   return count;
}


/****************************************************************************\
 *                                                                          *
 *  Local	bool	edges_do_cross(int, int, int, int,                  *
 *				       int, int, int, int)                  *
 *  ------------------------------------------------------                  *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: coordinates of both endpoints of two edges		    *
 *                                                                          *
 *  returns   : boolean value,                                              *
 *		TRUE:  if the given edges cross properly                    *
 *		FALSE: if the given edges do not cross		            *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : determine the crossingpoint of the two given edges.	    *
 *		if it is located properly between the two endnodes of each  *
 *		edge (i.e. no edge passes through any node wihout being     *
 *		incident to it), TRUE is returned, else FALSE.		    *
 *                                                                          *
 *              the edges are given in a low-to-high (left-to-right)        *
 *              direction, i.e. that the first two parameters describe the  *
 *              low (left) starting point of the first edge, the next two   *
 *              parameters are the high (right) endpoint of first edge, and *
 *              corresponding for the remaining edge (last 4 parameters).   *
 *                                                                          *
 *		note:                                                       *
 *		it is assumed that no two nodes have exactly the same       *
 *		coordinates (both, x- and y-value).                         *
 *                                                                          *
\****************************************************************************/

Local	bool	edges_do_cross(int e1x1, int e1y1, int e1x2, int e1y2, int e2x1, int e2y1, int e2x2, int e2y2)
{
   float	r, s;

   if (e1y2 < e2y1) {
      return FALSE;
   } else if (e2y2 < e1y1) {
      return FALSE;
   } else if ((((e1x1 == e2x1) && (e1y1 == e2y1))  ||
        ((e1x1 == e2x2) && (e1y1 == e2y2))) ||
       (((e1x2 == e2x2) && (e1y2 == e2y1))  ||
        ((e1x2 == e2x2) && (e1y2 == e2y2)))) {
/*    edges are incident                                                   */
      return FALSE;
   } else if (((e2y2-e2y1) * (e1x2-e1x1)) == ((e1y2-e1y1) * (e2x2-e2x1))) {
/*    edges are parallel                                                   */
      return FALSE;
   } else if ((e1x2 - e1x1) == 0) {
      if (((e2x1 > e1x1) && (e2x2 < e1x1)) || 
          ((e2x1 < e1x1) && (e2x2 > e1x1))) {
         if  ((((e2x2 - e2x1) * (e1y2 - e2y1)) <
               ((e1x1 - e2x1) * (e2y2 - e2y1))) &&
              (((e2x2 - e2x1) * (e1y1 - e2y1)) >
               ((e1x1 - e2x1) * (e2y2 - e2y1)))) {
             return TRUE;
         } else if  ((((e2x2 - e2x1) * (e1y2 - e2y1)) >
               ((e1x1 - e2x1) * (e2y2 - e2y1))) &&      
              (((e2x2 - e2x1) * (e1y1 - e2y1)) <
               ((e1x1 - e2x1) * (e2y2 - e2y1)))) {      
             return TRUE;                               
         } else {
            return FALSE;
         } /* endif */
      } else {
         return FALSE;
      } /* endif */
   } else {
      float denom;

      denom = ((float)(e2y2-e2y1)*(float)(e1x2-e1x1)-(float)(e1y2-e1y1)*(float)(e2x2-e2x1));

      if (denom != 0.0) {
	 s = ((float)(e1y1-e2y1)*(float)(e1x2-e1x1) +
	      (float)(e1y2-e1y1)*(float)(e2x1-e1x1)) / denom;
	 if ((0.0 < s) && (s < 1.0)) {
	    r = ((float)(e2x1-e1x1) + s*(float)(e2x2-e2x1)) / (float)(e1x2-e1x1);
	    if ((0.0 < r) && (r < 1.0)) {
	       return TRUE;
	    } else {
	       return FALSE;
	    } /* endif */
	 } else {
	    return FALSE;
	 } /* endif */
      } else {
	 message("\ndivision by zero detected !\n");
	 return FALSE;
      } /* endif */
   } /* endif */
}


/****************************************************************************\
 *                                                                          *
 *  Local	void	embed_initial_node(Snode)	                    *
 *  ---------------------------------------------                           *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: node of type Snode		              		    *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : set the coordinates of the first node to be embedded        *
 *		in a graph.                                                 *
 *		there are different possible embedding-strategies, but      *
 *		the simplest is to embedded the first node in the middle    *
 *		of the output-window. (given by predefined values           *
 *		CENTER_X and CENTER_Y.                                      *
 *                                                                          *
\****************************************************************************/

Local	void	embed_initial_node(Snode n)
{
   n->x = CENTER_X;
   n->y = CENTER_Y;
}


/****************************************************************************\
 *                                                                          *
 *  Local	int	embed_node_strategy1(Snode)	                    *
 *  -----------------------------------------------                         *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: node of type Snode					    *
 *                                                                          *
 *  returns   : int, counter of the crossings in the graph caused by        *
 *		embedding the given node.                                   *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : set the coordinates of the node to be embedded depending on *
 *		current embedding of the other nodes of the graph.          *
 *		there are different possible embedding-strategies, but      *
 *		the simplest is to embedded the node in the center of all   *
 *		its adjacent nodes. (in case there are any.)		    *
 *		it will be tried that all nodes have a minimum-distance     *
 *		(pre-defined) between each other, but to keep the work easy *
 *		it can not be ensured that two nodes do not touch or even   *
 *		lie above each other.					    *
 *                                                                          *
\****************************************************************************/

Local	int	embed_node_strategyI(Snode n)
{
   int	adjCount, count;
   int	center1_x, center1_y;
   float	dist;
   Sedge	e;
   Snode	n1, n2;

   adjCount = node_marker(n);
   if (adjCount == 1) {
      for_sourcelist(n, e) {
	 if (node_visited(e->tnode) == EMBEDDED) {
/*	    if no collision with other nodes found			    */
/*	    value could be depending on NODESIZE 			    */
	    n1 = e->tnode;
	    break;
	 } /* endif */
      } end_for_sourcelist(n, e);
      center1_x = 0;
      center1_y = 0;
      count = 0;
      for_sourcelist(n1, e) {
	 if (node_visited(e->tnode) == EMBEDDED) {
	    count++;
	    center1_x += e->tnode->x;
	    center1_y += e->tnode->y;
	 } /* endif */
      } end_for_sourcelist(n1, e);
      if (count > 0) {
	 center1_x = mydiv(center1_x, count);
	 center1_y = mydiv(center1_y, count);
	 dist = distance_between(n1->x, n1->y, center1_x, center1_y);
	 n->x = n1->x + mydiv((n1->x - center1_x) * (MIN_DISTANCE * FACTOR), (int) dist);
	 n->y = n1->y + mydiv((n1->y - center1_y) * (MIN_DISTANCE * FACTOR), (int) dist);
      } else {
	 n->x = n1->x + MIN_DISTANCE;
	 n->y = n1->y;
      } /* endif */

   } else if (adjCount == 2) {
      count = 0;
      for_sourcelist(n, e) {
	 if (node_visited(e->tnode) == EMBEDDED) {
/*	    if no collision with other nodes found			    */
/*	    value could be depending on NODESIZE 			    */
	    if (count == 0) {
	       n1 = e->tnode;
	       count++;
	    } else {
	       n2 = e->tnode;
	       break;
	    } /* endif */
	 } /* endif */
      } end_for_sourcelist(n, e);
      center1_x = 0;
      center1_y = 0;
      count = 0;
      for_sourcelist(n1, e) {
	 if (node_visited(e->tnode) == EMBEDDED) {
	    count++;
	    center1_x += e->tnode->x;
	    center1_y += e->tnode->y;
	 } /* endif */
      } end_for_sourcelist(n1, e);
      for_sourcelist(n2, e) {
	 if (node_visited(e->tnode) == EMBEDDED) {
	    count++;
	    center1_x += e->tnode->x;
	    center1_y += e->tnode->y;
	 } /* endif */
      } end_for_sourcelist(n2, e);
      if (count > 0) {
	 center1_x = mydiv(center1_x, count);
	 center1_y = mydiv(center1_y, count);
	 n->x = n1->x + mydiv((n1->x - center1_x) * (MIN_DISTANCE * FACTOR), (int) dist);
	 n->y = n1->y + mydiv((n1->y - center1_y) * (MIN_DISTANCE * FACTOR), (int) dist);
      } else {
	 n->x = n1->x + .5*(n2->x - n1->x) - mydiv((n2->y - n1->y) * (MIN_DISTANCE * FACTOR),
						 distance_between(n1->x, n1->y, n2->x, n2->y));
	 n->y = n1->y + .5*(n2->y - n1->y) + mydiv((n2->x - n1->x) * (MIN_DISTANCE * FACTOR),
						 distance_between(n1->x, n1->y, n2->x, n2->y));
      } /* endif */
   } else {
      center1_x = 0;
      center1_y = 0;
      count = 0;
      for_sourcelist(n, e) {
	 if (node_visited(e->tnode) == EMBEDDED) {
/*	    if no collision with other nodes found			    */
/*	    value could be depending on NODESIZE 			    */
	    center1_x = center1_x + e->tnode->x;
	    center1_y = center1_y + e->tnode->y;
	    count++;
	 } /* endif */
      } end_for_sourcelist(n, e);

      n->x = center1_x / count;
      n->y = center1_y / count;

   } /* endif */

   return 0;
}


Local double mysqrt(double arg)
{
   if (arg < 0) {
      message("\nargument of sqrt < 0.");
      return 1;
   } else if (arg == 0) {
      message("\nargument of sqrt = 0.");
      return 1;
   } else {
      return sqrt(arg);
   } /* endif */
}

Local	int	mydiv(int n, int m)
{
/*   div_t	res; */

   if (m == 0) {
      message("\ndivision by zero.");
      return 0;
   } else {
      return n / m;
/*      return res.quot; */ 
   } /* endif */
}



Local	void	embed_node_in_complete_graph(Snode n, int i)
{
   switch(i) {
      case	(1)	:	n->x = i;
				n->y = 0;
				break;
      case	(2)	:	n->x = i;
				n->y = 1;
				break;
      case	(3)	:	n->x = i;
				n->y = -1;
				break;
      default		:	n->x = i;
				n->y = (4 * (n->pre->pre->y)) -
					(2 * (n->pre->pre->pre->y));
   } /* endswitch */
}


Local void	rotate_and_stretch_node(Snode n, double angle, int factor)
{
   double	x, y, x2, y2;

#  ifdef DEBUG
   fprintf(crossfile, "\nentering function with node (%d/%d) and angle %5.4f",
		    n->x, n->y, angle);
#  endif

   x = (double) n->x;
   y = ((double) n->y) / 20.0;

   x2 = (x * cos(angle)) - (y * sin(angle));
   y2 = (x * sin(angle)) + (y * cos(angle));

#  ifdef DEBUG
   fprintf(crossfile, "\nset x to %5.4f and y to %5.4f",
		    x2, y2);
#  endif

   x2 = x2 * factor;
   y2 = y2 * factor;

   n->x = (int) x2 + CENTER_X;
   n->y = (int) y2 + CENTER_Y
   ;
#  ifdef DEBUG
   fprintf(crossfile, "\nleaving function with node (%d/%d)",
		    n->x, n->y);
#  endif
}

