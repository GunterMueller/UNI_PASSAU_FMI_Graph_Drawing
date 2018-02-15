/****************************************************************************\
 *                                                                          *
 *  thicknes.c                                                              *
 *  ----------                                                              *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/93.                                    *
 *                                                                          *
\****************************************************************************/

#if defined SUN_VERSION
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#else
#include <sgraph\std.h>
#include <sgraph\slist.h>
#include <sgraph\sgraph.h>
#endif

#include "sattrs.h"
#include "maxplanarsubgraph.h"

#if defined SUN_VERSION
/*Local	void	mark_layers_in_input_graph(Sgraph g1, int c);*/
Local   Sgraph  subtract_mpg_edges_from_graph(Sgraph ing, Sgraph temp_g, Sgraph mpg);
Local   void	store_remaining_edges_in_graph_attrs(Sgraph ing, Sgraph temp_g);
#else
/*Local	void	mark_layers_in_input_graph(Sgraph, int);*/
Local   Sgraph  subtract_mpg_edges_from_graph(Sgraph, Sgraph, Sgraph);
Local   void	store_remaining_edges_in_graph_attrs(Sgraph, Sgraph);
#endif




/****************************************************************************\
 *                                                                          *
 *  Global int	thickness(Sgraph, Slist *)                                  *
 *  --------------------------------------                                  *
 *                                                                          *
 *  author:  a.j. winter (11027)  05/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: graph of type Sgraph (see 'sgraph.h' for details)           *
 *		address of Slist, serves as a return value		    *
 *                                                                          *
 *  returns   : counter of type int, the returns the number of planar 	    *
 *		subgraphs into that the input graph was decomposed.         *
 *		the second parameter is set to a list of graphs, 	    *
 *		every graph in that list represents a planar subgraph of    *
 *		the given input graph. 					    *
 *		(see task for details).					    *
 *                                                                          *
 *              in case that the given input graph is already planar,       *
 *		0 is returned as counter (although the thickness of a       *
 *		planar graph is per definition 1) and the empty_list	    *
 *	 	as parameter.                                               *
 *									    *
 *		CHANGED: thickness now returns 1 for a given planar graph.  *						    *
 *                                                                          *
 *  call from : main-module (this is the interface to the user)             *
 *                                                                          *
 *  uses      : global function maxplanarsubgraph in module maxplana.c and  *
 *              several local functions 				    *
 *                                                                          *
 *  task      : the thickness of a graph G = (V,E) is defined by the 	    *
 *		smallest n, for which can be found a         		    *
 *		decompostion of the edgeset E of the graph (E1,...,En),     *
 *		whereas all subgraphs Gi = (V,Ei) have to be planar.        *
 *		the input graph remains untouched, but attrs field of the   *
 *		are assigned values to identify the layer to which an edge  *
 *		finally belongs in the embedding.			    *
 *		note that every new subgraph representing a layer, contains *
 *		all nodes of the input graph, but the subgraph needs not    *
 *		necessarily be spanning, i.e. the subgraph may not be 	    *
 *		biconnected and even worse not even be connected.	    *
 *				      					    *
 *		this is the main reason why maxplanarsubgraph determines    *
 *		all biconnected components and performs its work by         *
 *		computing the maximal planar subgraph on all biconnected    *
 *		components seperately.					    *
 *								            *
 *		the work is done by firstly computing the mpg of the input  *
 *		graph. this defines a new graph in which all edges that     *
 *		belong to the mpg are deleted. and work continues on this   *
 *		resulting graph by iteratively determining the mpg and      *
 *		its edges deleting. finally the graph may fall into several *
 *		unconnected components.					    *
 *									    *
 *		if the input graph is already planar, nothing is done.      *
 *                                                                          *
\****************************************************************************/

Global int	thickness(Sgraph inG, Slist *outList)
{
   Sgraph	G;
   Sgraph	mpg;
   Slist	G_List;
   int		count;

/* create slist of graphs, one for each layer and 			    *
 * mark corresponding edges in input graph according to layer 		    */
/* at first copy inputgraph inG to G and leave it then alone until 	    *
 * layers are determined. 						    */
/* begin with G and find the mpg of G, then delete the 			    *
 * corresponding edges of mpg in G, while G itself is nonplanar 	    *
 * if G is eventually planar, it is the last graph in the list 		    */
/* if input_graph is planar then return empty_slist 			    */

/* first step:								    *
 *   - get maximal planar subgraph, 					    *
 *     if empty_graph is returned by function maximalplanarsubgraph 	    *
 *     then the input graph inG was already planar, 			    *
 *     return 1 and set outList to empty_slist 				    */

   mpg = maxplanarsubgraph(inG);
   if (mpg == empty_graph) {
      *outList = empty_slist;
      return 1;
   } else {
/*    copy especially the edge-attr-field edge_in_mpg 			    */
      G = copy_graph_with_attrs(inG);
      G_List = empty_slist;
      count = 0;
      do {
/*       the edge-attr-field edge_in_mpg indicates wether a must be deleted */
/*       delete also attrs for those edges 				    */
/*	 mark_layers_in_input_graph(G, count); */
	 set_graphattrs(mpg, make_attr(ATTR_FLAGS, count));
	 count++;
	 G = subtract_mpg_edges_from_graph(inG, G, mpg);
/*	 re_init_all_attributes(G); */
	 G_List = enqueue(G_List, mpg);
/*	 DANGER: after deleting edges, the graph need not be biconnected    *
 *	 anymore, i.e. that STNUMBER may fail on G 			    */
/*       because of this maxplanarsubgraph now determines the biconnected   *
 *	 components of a given graph first, and then does its work on all   *
 *	 components.							    */
      } while ((mpg = maxplanarsubgraph(G)) != empty_graph);
/*    delete attributes for the remaining G 				    */
/*    mark_layers_in_input_graph(G, count); */
      set_graphattrs(G, make_attr(ATTR_FLAGS, count));
      count++;
      store_remaining_edges_in_graph_attrs(inG, G);
      G_List = enqueue(G_List, G);

      *outList = G_List;

      return count;
   } /* endif */
}

#if 0
Local	void	mark_layers_in_input_graph(Sgraph g1, int c)
{
   Snode	node;
   Sedge	edge;

   for_all_nodes(g1, node) {
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    if (edge_in_mpg(edge)) {
/*	       edge_layer(edge_original(edge)) = c; */
	    }  /* endif */
	 }  /* endif */
      } end_for_sourcelist(node, edge);
   } end_for_all_nodes(g1, node);
}
#endif


/****************************************************************************\
 *                                                                          *
 *  Local   Sgraph  subtract_mpg_edges_from_graph(Sgraph, Sgraph, Sgraph)   *
 *  ---------------------------------------------------------------------   *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: input graph of both endpoints of two edges		    *
 *		temporary graph in which edges will be deleted              *
 *		maxplanarsubgraph of input graph                            *
 *		each of type Sgraph (see 'sgraph.h' for details)            *
 *                                                                          *
 *  returns   : temporary graph (return graph in which changes are made)    *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : in every call, those edges are deleted from the temporary   *
 *		graph (second parameter) that were determined to be member  *
 *		of the maxplanarsubgraph.                                   *
 *		A sideffect is, that attribute-fields of the edges of the   *
 *		input graph refers to the corresponding edge in the current *
 *		layer (maxplanarsubgraph) and vice versa.		    *
 *                                                                          *
\****************************************************************************/

Local   Sgraph  subtract_mpg_edges_from_graph(Sgraph ing, Sgraph temp_g, Sgraph mpg)
{
   Snode        node;
   Sedge        edge, new_edge, temp_edge;
   Slist	DeletionList;

   DeletionList = empty_slist;
   for_all_nodes(ing, node) {
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    temp_edge = attr_data_of_type(edge, Sedge);
	    if (temp_edge->snode->graph == temp_g) {
	       new_edge = attr_data_of_type(temp_edge, Sedge);
	       if (new_edge != empty_sedge /* ->snode->graph == mpg */) {
	          DeletionList = enqueue(DeletionList, temp_edge);
	          set_attr_data(new_edge, edge);
		  set_attr_data(new_edge->tsuc, new_edge->tsuc);
		  set_attr_data(edge, new_edge);
	          set_attr_data(edge->tsuc, new_edge->tsuc);
	       } else {
	          set_attr_data(temp_edge, edge);
	          set_attr_data(temp_edge->tsuc, edge->tsuc);
	       } /* endif */
	     } /* endif */
	 } /* endif */
      } end_for_sourcelist(node, edge);
   } end_for_all_nodes(ing, node);

   while (!isempty(DeletionList)) {
      edge = sedge_in_slist(DeletionList);
      DeletionList = rest(DeletionList);
      remove_edge(edge);
   }  /* endwhile */
	

   return temp_g;
}


/****************************************************************************\
 *                                                                          *
 *  Local   void  store_remaining_edges_in_graph_attrs(Sgraph, Sgraph)      *
 *  ------------------------------------------------------------------      *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
 ****************************************************************************
 *                                                                          *
 *  parameters: input graph of both endpoints of two edges		    *
 *		temporary graph in which edges will be deleted              *
 *		each of type Sgraph (see 'sgraph.h' for details)            *
 *                                                                          *
 *  returns   : ---                                                         *
 *                                                                          *
 *  call from : this module (this is a local function)                      *
 *                                                                          *
 *  uses      : ---							    *
 *                                                                          *
 *  task      : does part of the work that is performed by the above 	    *
 *		function  subtract_mpg_edges_from_graph()  		    *
 *		Store in the attributes of each graph a refernce to the     *
 *		corresponding edge in the other graph, for further work.    *
 *                                                                          *
\****************************************************************************/

Local   void  store_remaining_edges_in_graph_attrs(Sgraph ing, Sgraph temp_g)
{
   Snode        node;
   Sedge        edge, temp_edge;

   for_all_nodes(ing, node) {
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    temp_edge = attr_data_of_type(edge, Sedge);
	    if (temp_edge->snode->graph == temp_g) {
	       set_attr_data(temp_edge, edge);
	       set_attr_data(temp_edge->tsuc, edge->tsuc);
	     } /* endif */
	 } /* endif */
      } end_for_sourcelist(node, edge);
   } end_for_all_nodes(ing, node);
}


