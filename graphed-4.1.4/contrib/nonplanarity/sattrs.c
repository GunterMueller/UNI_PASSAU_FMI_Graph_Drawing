#if defined SUN_VERSION
#include <std.h>
#include <slist.h>
#include <sgraph.h>
#else
#include <sgraph\std.h>
#include <sgraph\slist.h>
#include <sgraph\sgraph.h>
#endif

#include <string.h>

#include "sattrs.h"


Global	int	CheckAssumedGraphProperties(Sgraph g, int *nodeCount, int *edgeCount)
{
/* check the graph for the following properties which are *
 * assumed to process a graph properly */
/* assume further that attrs are attached to the nodes for marking */

   Snode	n;
   Sedge	e;
   int		rc;


/* for_all_nodes(g, n) {
      node_marker(n) = UNDEFINED;
   } end_for_all_nodes(g, n); */

   rc = PROPER_GRAPH;

   *nodeCount = 0;
   *edgeCount = 0;
   for_all_nodes(g, n) {
      (*nodeCount)++;
      for_sourcelist(n, e) {
	 if (n->nr == e->tnode->nr) {
	    rc = LOOP;
	 } else if (n->nr < e->tnode->nr) {
	   (*edgeCount)++;
	   if (node_temp_flag(e->tnode) != n->nr) {
	      node_temp_flag(e->tnode) = n->nr;
	   } else {
	      rc = MULTI_EDGE;
	   }  /* endif */
	 }  /* endif */
      } end_for_sourcelist(n, e);
   } end_for_all_nodes(g, n);

  return rc;
}



Global  Sgraph  copy_graph_with_attrs(Sgraph g)
{
   Sgraph       g2;

   Snode        node, node2;
   Sedge        edge, edge2;

   if (g) {
      g2 = make_graph(empty_attrs);
      g2->directed = g->directed;
      for_all_nodes(g, node) {
	 node2 = make_node(g2, empty_attrs);
	 node2->nr = node->nr;
	 node2->x  = node->x;
	 node2->y = node->y;
	 if (node->label != NULL) {
	    node2->label = NEW_LABEL;
	    strcpy(node2->label, node->label);
	 } /* endif */
	 node2->iso = node;
	 node->iso  = node2;
/*       create_and_init_node_attributes(node2); */
      } end_for_all_nodes(g, node);
      for_all_nodes(g, node) {
	 for_sourcelist(node, edge) {
	    if (node->nr < edge->tnode->nr) {
	       edge2 = make_edge(node->iso, edge->tnode->iso, empty_attrs);
	       if (attr_data_of_type(edge, Sedge)) {
/*	          attr of edge points to mpg 			            */
		  attr_data(edge2) = attr_data(edge);
		  attr_data(edge2->tsuc) = attr_data(edge->tsuc);
		  set_attr_data(edge, edge2);
		  set_attr_data(edge->tsuc, edge2->tsuc);
	       } else {
		  set_attr_data(edge2, empty_sedge);
		  set_attr_data(edge2->tsuc, empty_sedge);
		  set_attr_data(edge, edge2);
		  set_attr_data(edge->tsuc, edge2->tsuc); 
	       } /* endif */
	    } /* endif */
	  } end_for_sourcelist(node, edge);
      } end_for_all_nodes(g, node);

      return g2;

   } else {
      return empty_sgraph;
   } /* endif */
}



Global  Sgraph  copy_graph_with_flags(Sgraph g)
{
   Sgraph       g2;

   Snode        node, node2;
   Sedge        edge, edge2;

   if (g) {
      g2 = make_graph(empty_attrs);
      g2->directed = g->directed;
      for_all_nodes(g, node) {
	 node2 = make_node(g2, empty_attrs);
	 node2->nr = node->nr;
	 node2->x  = node->x;
	 node2->y = node->y;
	 if (node->label != NULL) {
	    node2->label = NEW_LABEL;
	    strcpy(node2->label, node->label);
	 } /* endif */
	 node2->iso = node;
	 node->iso  = node2;
	 attr_flags(node2) = attr_flags(node);
/*       create_and_init_node_attributes(node2); */
      } end_for_all_nodes(g, node);
      for_all_nodes(g, node) {
	 for_sourcelist(node, edge) {
	    if (node->nr < edge->tnode->nr) {
	       edge2 = make_edge(node->iso, edge->tnode->iso, empty_attrs);
               attr_flags(edge2) = attr_flags(edge);
	       attr_flags(edge2->tsuc) = attr_flags(edge->tsuc);
	    } /* endif */
	  } end_for_sourcelist(node, edge);
      } end_for_all_nodes(g, node);

      return g2;

   } else {
      return empty_sgraph;
   } /* endif */
}


Global  Sgraph  copy_graph_without_attrs(Sgraph g)
{
   Sgraph       g2;

   Snode        node, node2;
   Sedge        edge, edge2;

   if (g) {
      g2 = make_graph(empty_attrs);
      for_all_nodes(g, node) {
	 node2 = make_node(g2, empty_attrs);
	 node2->nr = node->nr;
	 node2->x  = node->x;
	 node2->y = node->y;
	 node2->label = NEW_LABEL;
	 strcpy(node2->label, node->label);
	 node2->iso = node;
	 node->iso  = node2;
      } end_for_all_nodes(g, node);
      for_all_nodes(g, node) {
	 for_sourcelist(node, edge) {
	    if (node->nr < edge->tnode->nr) {
	       edge2 = make_edge(node->iso, edge->tnode->iso, empty_attrs);
	    } /* endif */
	  } end_for_sourcelist(node, edge);
      } end_for_all_nodes(g, node); 

      return g2;

   } else {
      return empty_sgraph;
   } /* endif */
}


Global void create_and_init_all_attributes(Sgraph g)
{
    Snode node;
    Sedge edge;

    for_all_nodes(g, node) {
       attr_data(node)       = NEW_ATTRS(struct _nodeattrs);
       node_visited(node)    = UNVISITED;
       node_dfnumber(node)   = UNDEFINED;
       node_compnumber(node) = UNDEFINED;
       node_dfsucc_count(node) = 0;
       node_bcc_property(node) = UNDEFINED;
       node_stnumber(node)   = UNDEFINED;
       node_stsucc(node)     = empty_snode;
       node_marker(node)     = NEW;
       node_temp_flag(node)  = UNDEFINED;
       node_Lvalue(node)     = UNDEFINED;
       node_Lnode(node)      = empty_snode;
       node_toLnode(node)    = empty_snode;
       node_degree(node)     = 0;
       for_sourcelist(node, edge) {
	  node_degree(node)++;
	  if (node->nr < edge->tnode->nr) {
	     attr_data(edge)        = NEW_ATTRS(struct _edgeattrs);
	     attr_data(edge->tsuc)  = attr_data(edge);
	     edge_quer(edge)        = edge->tsuc;
	     edge_quer(edge->tsuc)  = edge;
	     edge_visited(edge)     = FALSE;
	     edge_marker(edge)      = NEW;
	     edge_type(edge)        = UNDEFINED;
	     edge_in_mpg(edge)	    = TRUE;
	     edge_layer(edge)       = NO_LAYER;
	     edge_iso(edge)	    = empty_sedge;
	  } /* endif */
       } end_for_sourcelist(node, edge);
    } end_for_all_nodes(g, node);
}

Global void create_and_init_node_attributes(Snode node)
{
       attr_data(node)       = NEW_ATTRS(struct _nodeattrs);
       node_visited(node)    = UNVISITED;
       node_dfnumber(node)   = UNDEFINED;
       node_compnumber(node) = UNDEFINED;
       node_dfsucc_count(node) = 0;
       node_bcc_property(node) = UNDEFINED;
       node_stnumber(node)   = UNDEFINED;
       node_stsucc(node)     = empty_snode;
       node_marker(node)     = NEW;
       node_temp_flag(node)  = UNDEFINED;
       node_Lvalue(node)     = UNDEFINED;
       node_Lnode(node)      = empty_snode;
       node_toLnode(node)    = empty_snode;
}


Global void create_and_init_edge_attributes(Sedge edge)
{
	     attr_data(edge)        = NEW_ATTRS(struct _edgeattrs);
	     attr_data(edge->tsuc)  = attr_data(edge);
	     edge_quer(edge)        = edge->tsuc;
	     edge_quer(edge->tsuc)  = edge;
	     edge_visited(edge)     = FALSE;
	     edge_marker(edge)      = NEW;
	     edge_type(edge)        = UNDEFINED;
	     edge_in_mpg(edge)	    = TRUE;
	     edge_layer(edge)       = NO_LAYER;
	     edge_iso(edge)	    = empty_sedge;
}



Global void re_init_all_attributes(Sgraph g)
{
    Snode node;
    Sedge edge;

    for_all_nodes(g, node) {
       node_visited(node)    = UNVISITED;
       node_dfnumber(node)   = UNDEFINED;
       node_compnumber(node) = UNDEFINED;
       node_dfsucc_count(node) = 0;
       node_bcc_property(node) = UNDEFINED;
       node_stnumber(node)   = UNDEFINED;
       node_stsucc(node)     = empty_snode;
       node_marker(node)     = NEW;
       node_temp_flag(node)  = UNDEFINED;
       node_Lvalue(node)     = UNDEFINED;
       node_Lnode(node)      = empty_snode;
       node_toLnode(node)    = empty_snode;
       node_degree(node)     = 0;
       for_sourcelist(node, edge) {
	  node_degree(node)++;
	  if (node->nr < edge->tnode->nr) {
	     attr_data(edge->tsuc)  = attr_data(edge);
	     edge_quer(edge)        = edge->tsuc;
	     edge_quer(edge->tsuc)  = edge;
	     edge_visited(edge)     = FALSE;
	     edge_marker(edge)      = NEW;
	     edge_type(edge)        = UNDEFINED;
	     edge_in_mpg(edge)	    = TRUE; 
	     edge_layer(edge)       = NO_LAYER;
	     edge_iso(edge)	    = empty_sedge; 
	  } /* endif */
       } end_for_sourcelist(node, edge);
    } end_for_all_nodes(g, node);
}


Global void clear_all_attributes(Sgraph graph)
{
   Snode node;
   Sedge edge;

   for_all_nodes(graph, node) {
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    free(attr_data_of_type(edge, EdgeAttrs));
	    attr_data(edge) = NULL;
	    attr_data(edge->tsuc)  = attr_data(edge);
	 } /* endif */
      } end_for_sourcelist(node, edge);
      free(attr_data_of_type(node, NodeAttrs));
      attr_data(node) = NULL;
   } end_for_all_nodes(graph, node);
}


Global void clear_node_attributes(Snode node)
{
      free(attr_data_of_type(node, NodeAttrs));
      attr_data(node) = NULL;
}

Global void clear_edge_attributes(Sedge edge)
{
	    free(attr_data_of_type(edge, EdgeAttrs));
	    attr_data(edge) = NULL;
	    attr_data(edge->tsuc)  = attr_data(edge);
}



Global Slist concat_slists(Slist l, Slist m)
{
   if (m == empty_slist) {
      return l;
   } else if (l == empty_slist) {
      return m;
   } else {
      l->pre->suc = m;
      m->pre->suc = l;
      l->pre = m->pre;
      m->pre = l->pre;
      return l;
   }
}


Global	void	count_all_nodes_and_edges_in_graph(Sgraph g, int *nc, int *ec)
{
   Snode	n;
   Sedge	e;
   int		ncount, ecount;

   ncount = 0;
   ecount = 0;
   if (g != empty_sgraph) {

      for_all_nodes(g, n) {
	 ncount++;
	 for_sourcelist(n, e) {
	    if (n->nr < e->tnode->nr) {
	       ecount++;
	    } /* endif */
	 } end_for_sourcelist(n, e);
      } end_for_all_nodes(g, n);
   } /* endif */

   *nc = ncount;
   *ec = ecount;
}

Global	int	count_edges_in_graph(Sgraph g)
{
   Snode	n;
   Sedge	e;
   int		count;

   count = 0;
   if (g != empty_sgraph) {

      for_all_nodes(g, n) {
	 for_sourcelist(n, e) {
	    if (n->nr < e->tnode->nr) {
	       count++;
	    } /* endif */
         } end_for_sourcelist(n, e);
      } end_for_all_nodes(g, n);
   } /* endif */

   return count;
}


Global void clear_all_labels(Sgraph graph)
{
   Snode node;
   Sedge edge;

   for_all_nodes(graph, node) {
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    if (edge->label) {
	       free(edge->label);
	       edge->label = NULL;
	    } /* endif */
	    if (edge->tsuc->label) {
	       free(edge->tsuc->label);
	       edge->tsuc->label = NULL;
	    } /* endif */
	 } /* endif */
      } end_for_sourcelist(node, edge);
      if (node->label) {
	 free(node->label);
	 node->label = NULL;
      } /* endif */
   } end_for_all_nodes(graph, node);
}


