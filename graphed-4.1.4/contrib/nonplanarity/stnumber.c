/****************************************************************************\
 *                                                                          *
 *  stnumber.c                                                              *
 *  ----------                                                              *
 *                                                                          *
 *  author:  a.j. winter (11027)  04/93.                                    *
 *                                                                          *
\****************************************************************************/

/* the algorithms in this file are taken from the following papers:         */

/* Even, Tarjan: COMPUTING AN ST-NUMBERING in:                              */
/* Theoretical Computer Science 2 (1976) 339-344                            */
/* O(n+m)-time algorithm for computing an st-numbering,                     */
/* by using depth-first search                                              */

/* Tarjan: DEPTH-FIRST SEARCH AND LINEAR GRAPH ALGORITHMS-NUMBERING in:     */
/* SIAM Journal On Computing Vol.1 (1972) 146-160                           */
/* different algorithms using depth-first search                            */


/* Implementation:                                                          */
/* Andreas Winter (11027)                                                   */
/* 24.02.93 - 02.03.93                                                      */


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

#ifdef  DEBUG
#undef  DEBUG
#endif

#include "sattrs.h"
#include "stnumber.h"

#if defined SUN_VERSION
Local Slist PATHFINDER(Snode v);
Local void  BICONNECT(Snode v, Snode u, int *num, Slist *edge_stack, int *comp_count, Slist *comp_list);
Local void search(Snode v, int *dfnum, int *compnum);
Local void pre_order(Snode node);
Local int       mark_node(Snode n, int layer);
Local Sedge find_new_forward_tree_edge(Snode n);
Local Sedge find_new_forward_cycle_edge(Snode n);
Local Sedge find_new_backward_cycle_edge(Snode n);
Local Sedge find_new_edge_to_common_Lnode(Snode n);
Local Sedge find_new_backward_tree_edge(Snode n);
#else
Local Slist PATHFINDER(Snode);
Local void  BICONNECT(Snode, Snode, int *, Slist *, int *, Slist *);
Local void search(Snode, int *, int *);
Local void pre_order(Snode);
Local int       mark_node(Snode, int);
Local Sedge find_new_forward_tree_edge(Snode);
Local Sedge find_new_forward_cycle_edge(Snode);
Local Sedge find_new_backward_cycle_edge(Snode);
Local Sedge find_new_edge_to_common_Lnode(Snode);
Local Sedge find_new_backward_tree_edge(Snode);
#endif

#ifdef DEBUG
extern FILE *outfile;
#endif
extern char	buffer[255];

Local void search(Snode v, int *dfnum, int *compnum)
{
   Sedge    e;
   Snode    w;

      node_visited(v) = CURRENT;
      node_dfnumber(v)  = ++(*dfnum);
#     ifdef DEBUG
      fprintf(outfile,
	      "entering search with node %d set dfsnum to %d\n",
	      v->nr, node_dfnumber(v));
#     endif
/*    Lvalue(v) = min({v} union {u ! ex. w such that v->w* and w--u})       */
/*    for every adjacent node:                                              */
/*    Lvalue(v) = min(number(v), number(z) for v--z, Lvalue(y) for child y  */
      node_Lvalue(v)  = node_dfnumber(v);
      node_Lnode(v)   = v;
      node_toLnode(v) = empty_snode;
      for_sourcelist(v,e) {
	 w = e->tnode;
	 if (node_visited(w) == UNVISITED) {
	    edge_visited(e) = TRUE;
	    edge_type(e) = TREE_EDGE;
	    node_dfsucc_count(v)++;
	    search(w,dfnum,compnum);
	    if (node_Lvalue(w) < node_Lvalue(v)) {
#              ifdef DEBUG
	       fprintf(outfile,
		       "set L of node %2d to %2d (dfs) by child\n",
		       v->nr, node_Lvalue(w));
#              endif
	       node_Lvalue(v) = node_Lvalue(w);
	       node_Lnode(v)  = node_Lnode(w);
	       node_toLnode(v) = w;
	    } /* endif */
	    if (node_Lvalue(w) >= node_dfnumber(v)) {
	       node_bcc_property(v) = ARTICULATION_POINT;
	    } /* endif */
	 } else {
	    if (!edge_visited(e)) {
	    /* avoid marking an edge twice                                  */
	       edge_visited(e) = TRUE;
	       edge_type(e) = CYCLE_EDGE;
	    } /* endif */
	    if (edge_type(e) == CYCLE_EDGE) {
	       if (node_dfnumber(w) < node_dfnumber(v)) {
/*                path w->*v exists                                      */
		  if (node_dfnumber(w) < node_Lvalue(v)) {
#                    ifdef DEBUG
		     fprintf(outfile,
			"set L of node %2d to %2d (dfs) by cycle-edge\n",
			v->nr, node_Lvalue(w));
#                    endif
		     node_Lvalue(v) = node_dfnumber(w);
		     node_Lnode(v)  = w;
		     node_toLnode(v) = w;
		  } /* endif */
	       } else {
/*                path v->*w exists                                      */
	       } /* endif */
	    } /* endif */
	 } /* endif */
      } end_for_sourcelist(v, e);
      node_visited(v) = COMPLETED;
      node_compnumber(v) = ++(*compnum);

#     ifdef DEBUG
      fprintf(outfile,"leaving search with node %d\n",v->nr);
      fflush(outfile);
#     endif

}


Local void pre_order(Snode node)
{
   int num1 = 0;
   int num2 = 0;

#     ifdef DEBUG
      fprintf(outfile,"entering pre_order with values %d %d\n",num1, num2);
      fflush(outfile);
#     endif

   search(node, &num1, &num2);

#     ifdef DEBUG
      fprintf(outfile,"leaving pre_order with values %d %d\n",num1, num2);
      fflush(outfile);
#     endif

}


/***************************************************************************/
Local   void    BICONNECT(Snode v, Snode u, int *num, Slist *edge_stack, int *comp_count, Slist *comp_list)
{
   Sedge        e;
   Snode        w;
   Sgraph       new_graph;
   Snode        new_node;
   Sedge        new_edge;

   node_visited(v)  = CURRENT;
   node_dfnumber(v) = ++(*num);
      node_Lvalue(v)  = node_dfnumber(v);
      node_Lnode(v)   = v;

      for_sourcelist(v,e) {
	 w = e->tnode;
	 if (node_visited(w) == UNVISITED) {
	    edge_visited(e) = TRUE;
	    edge_type(e) = TREE_EDGE;
	    node_dfsucc_count(v)++;
            *edge_stack = push(*edge_stack, e);
	    BICONNECT(w, v, num, edge_stack, comp_count, comp_list);
	    if (node_Lvalue(w) < node_Lvalue(v)) {
	       node_Lvalue(v) = node_Lvalue(w);
	       node_Lnode(v)  = node_Lnode(w);
	    } /* endif */
	    if (node_Lvalue(w) >= node_dfnumber(v)) {
	       node_bcc_property(v) = ARTICULATION_POINT;
/*             start new biconnected component */
               ++(*comp_count);
	       new_graph = make_graph(empty_attrs);
	       new_graph->directed = FALSE;
               *comp_list = enqueue(*comp_list, new_graph);
               while (!isempty(*edge_stack)) {
                  e = top_edge(*edge_stack);
                  if (node_dfnumber(e->snode) < node_dfnumber(w)) {
                     break;
                  } /* endif */
/*                delete (v,w) from edge_stack and add it to component */
                  e = top_edge(*edge_stack);
                  *edge_stack = pop(*edge_stack);
/*                if (the corresponding nodes don't exist, yet for the new *
 *                biconnected component, create them */
                  if (node_marker(e->snode) != *comp_count) {
                     new_node = make_node(new_graph, empty_attrs);
                     new_node->nr = e->snode->nr;
                     new_node->iso = e->snode;
                     e->snode->iso = new_node;
                     node_marker(e->snode) = *comp_count;
                  } /* endif */
                  if (node_marker(e->tnode) != *comp_count) {
                     new_node = make_node(new_graph, empty_attrs);
                     new_node->nr = e->tnode->nr;
                     new_node->iso = e->tnode;
                     e->tnode->iso = new_node;
                     node_marker(e->tnode) = *comp_count;
                  } /* endif */
                  new_edge = make_edge(e->snode->iso, e->tnode->iso, empty_attrs);
                  edge_marker(e) = *comp_count;
                  edge_iso(e) = new_edge;
               } /* endwhile */
               *edge_stack = pop(*edge_stack);
               if (node_marker(e->snode) != *comp_count) {
                  new_node = make_node(new_graph, empty_attrs);
                  new_node->nr = e->snode->nr;
                  new_node->iso = e->snode;
                  e->snode->iso = new_node;
                  node_marker(e->snode) = *comp_count;
               } /* endif */
               if (node_marker(e->tnode) != *comp_count) {
                  new_node = make_node(new_graph, empty_attrs);
                  new_node->nr = e->tnode->nr;
                  new_node->iso = e->tnode;
                  e->tnode->iso = new_node;
                  node_marker(e->tnode) = *comp_count;
               } /* endif */
               new_edge = make_edge(e->snode->iso, e->tnode->iso, empty_attrs);
               edge_marker(e) = *comp_count;
               edge_iso(e) = new_edge;
	    } /* endif */
	 } else {
	    if (!edge_visited(e)) {
	    /* avoid marking an edge twice                                  */
	       edge_visited(e) = TRUE;
	       edge_type(e) = CYCLE_EDGE;
	    } /* endif */
	    if (edge_type(e) == CYCLE_EDGE) {
	       if ((node_dfnumber(w) < node_dfnumber(v))
                && (w != u)) {
/*                path w->*v exists                                      */ 
                  *edge_stack = push(*edge_stack, e);
		  if (node_dfnumber(w) < node_Lvalue(v)) {
		     node_Lvalue(v) = node_dfnumber(w);
		     node_Lnode(v)  = w;
		  } /* endif */
	       } /* endif */
	    } /* endif */
	 } /* endif */
      } end_for_sourcelist(v, e);
      node_visited(v) = COMPLETED;


}









Local Sedge find_new_backward_cycle_edge(Snode n)
{
   Sedge e;
  /* Slist elem;

   if ((elem = node_path_from(n)) != empty_slist) {
      e = sedge_in_slist(elem);
      if (edge_marker(e) == NEW) {
	 node_path_from(n) = subtract_immediately_from_slist(node_path_from(n),
							     elem);
	 return e;
      }
    }*/

   for_sourcelist(n,e) {
      if ((edge_type(e) == CYCLE_EDGE) &&
	  (edge_marker(e) == NEW) &&
	  (node_dfnumber(n) > node_dfnumber(e->tnode))) {
	     return e;
      } /* endif */
    } end_for_sourcelist(n,e);

   return empty_sedge;
}


Local Sedge find_new_forward_cycle_edge(Snode n)
{
   Sedge e;
/*   Slist elem;

   if ((elem = node_path_to(n)) != empty_slist) {
      e = sedge_in_slist(elem);
      if (edge_marker(e) == NEW) {
	 node_path_to(n) = subtract_immediately_from_slist(node_path_to(n),
							     elem);
	 return e;
      }
    }*/

   for_sourcelist(n,e) {
      if ((edge_type(e) == CYCLE_EDGE) &&
	  (edge_marker(e) == NEW) &&
	  (node_dfnumber(n) < node_dfnumber(e->tnode))) {
	     return e;
      } /* endif */
    } end_for_sourcelist(n,e);

   return empty_sedge;
}


Local Sedge find_new_forward_tree_edge(Snode n)
{
   Sedge e;

   for_sourcelist(n,e) {
      if ((edge_type(e) == TREE_EDGE) &&
	  (edge_marker(e) == NEW) &&
	  (node_dfnumber(n) < node_dfnumber(e->tnode))) {
	     return e;
      } /* endif */
   } end_for_sourcelist(n,e);

   return empty_sedge;
}


Local Sedge find_new_backward_tree_edge(Snode n)
{
   Sedge e;

   for_sourcelist(n,e) {
      if ((edge_type(e) == TREE_EDGE) &&
	  (edge_marker(e) == NEW) &&
	  (node_dfnumber(n) > node_dfnumber(e->tnode))) {
	     return e;
      } /* endif */
   } end_for_sourcelist(n,e);

   return empty_sedge;
}


/*
Local Sedge find_new_edge_with_common_Lnode(n)
Snode n;
{
   Sedge e;

   for_sourcelist(n,e) {
      if ((edge_marker(e) == NEW) &&
	  ((e->tnode == node_Lnode(n)) ||
	   (node_Lnode(e->tnode) == node_Lnode(n)))) {
	      return e;
      } * endif *
   } end_for_sourcelist(n,e);

   return empty_sedge;
}
*/


Local Sedge find_new_edge_to_common_Lnode(Snode n)
{
   Sedge e;

/* for_sourcelist(n,e) {
      if ((edge_marker(e) == NEW) &&
	  (((edge_type(e) == CYCLE_EDGE) &&
	    (e->tnode == node_Lnode(n))) ||
	   ((edge_type(e) == TREE_EDGE) &&
	    (node_Lnode(e->tnode) == node_Lnode(n))))) {
	      return e;
      } * endif *
   } end_for_sourcelist(n,e); */

/* for_sourcelist(n,e) {
      if ((edge_marker(e) == NEW) &&
	  (edge_type(e) == CYCLE_EDGE) &&
	  (e->tnode == node_Lnode(n))) {
	      return e;
      } * endif *
   } end_for_sourcelist(n,e);

   for_sourcelist(n,e) {
      if ((edge_marker(e) == NEW) &&
	  (edge_type(e) == TREE_EDGE) &&
	  (node_number(n) < node_number(e->tnode)) &&  * unnoetig ??? *
	  (node_Lnode(e->tnode) == node_Lnode(n))) {
	      return e;
      } * endif *
   } end_for_sourcelist(n,e);
*/

   for_sourcelist(n,e) {
      if ((edge_marker(e) == NEW) &&
	  (e->tnode == node_toLnode(n))) {
	      return e;
      } /* endif */
   } end_for_sourcelist(n,e);

   return empty_sedge;
}


/*
Local Sedge find_new_edge(Snode n)
{
   Sedge e;

   for_sourcelist(n,e) {
      if (edge_marker(e) == NEW) {
	 return e;
      } * endif *
   } end_for_sourcelist(n,e);

   return empty_sedge;
}
*/


Local Slist PATHFINDER(Snode v)
{
   Slist    p;
   Snode    w;
   Sedge    e;

   p = empty_slist;
/* if there is a new cycle edge {v,w} with w->*v}                           */
   e = find_new_backward_cycle_edge(v);
   if (e != empty_sedge) {
#     ifdef DEBUG
      fprintf(outfile, "part I of pathfinder(%d)\n",v->nr);
#     endif
/*    mark e={v,w} old                                                      */
      edge_marker(e) = OLD;
/*    let path be {v,w}                                                     */
      p = enqueue(p, e->snode);
      p = enqueue(p, e->tnode);
   } else {
/*    if there is a new tree edge v->w                                         */
      e = find_new_forward_tree_edge(v);
      if (e != empty_sedge) {
#        ifdef DEBUG
	 fprintf(outfile, "part II of pathfinder(%d)\n",v->nr);
	 fprintf(outfile,
		 "\nfound tree edge (%d->%d)\n", v->nr, e->tnode->nr);
#        endif
/*       mark e={v,w} old                                                   */
	 edge_marker(e) = OLD;
/*       initialize path to be {v,w}                                        */
	 p = enqueue(p, e->snode);
	 p = enqueue(p, e->tnode);
	 w = e->tnode;
/*       while w is new do                                                  */
	 while (node_marker(w) == NEW) {
/*          find a (new) edge {w,x} with x = L(w) or L(x) = L(w)            */
	    e = find_new_edge_to_common_Lnode(w);
/*          mark w old                                                      */
	    node_marker(w) = OLD;
	    if (e != empty_sedge) {
#           ifdef DEBUG
	    fprintf(outfile, "found edge to Lnode (%d->%d)  ", w->nr, e->tnode->nr);
	    if (node_Lnode(w) == e->tnode) {
		  fprintf(outfile, "x is Lnode of w\n");
	       } else {
		  fprintf(outfile, "x and w have common Lnode: %d\n",node_Lvalue(w));
	       }
#            endif
/*             mark {w,x} old                                               */
	       edge_marker(e) = OLD;
/*             add {w,x} to path                                               */
	       p = enqueue(p, e->tnode);
/*             w := x                                                          */
	       w = e->tnode;
	    } /* endif */
	 } /* endwhile */
      } else {
/*       if there is a new cycle edge {v,w} with v->*w}                           */
	 e = find_new_forward_cycle_edge(v);
	 if (e != empty_sedge) {
#           ifdef DEBUG
	    fprintf(outfile, "part III of pathfinder(%d)\n",v->nr);
#           endif
/*          mark e={v,w} old                                                      */
	    edge_marker(e) = OLD;
/*          initialize path to be {v,w}                                           */
	    p = enqueue(p, e->snode);
	    p = enqueue(p, e->tnode);
	    w = e->tnode;
/*          while w is new do                                                     */
	    while (node_marker(w) == NEW) {
/*             find a (new) edge {w,x} with x->w                                  */
	       e = find_new_backward_tree_edge(w);
/*             mark w old                                                   */
	       node_marker(w) = OLD;
	       if (e != empty_sedge) {
/*                mark {w,x} old                                            */
		  edge_marker(e) = OLD;
/*                add {w,x} to path                                               */
		  p = enqueue(p, e->tnode);
/*                w := x                                                          */
		  w = e->tnode;
	       } /* endif */
	    } /* endwhile */
	 } /* endif */
      } /* endif */
   } /* endif */
#  ifdef DEBUG 
   fflush(outfile);
#  endif
   return p;
}


Global int STNUMBER(Sgraph graph, Snode *ps_node, Snode *pt_node)
{
   Snode    s_node, t_node;
   Snode    vertex, old_v;
   int      i;
   Slist    stack, path;
#  ifdef DEBUG
   Slist        elem;
#endif

/* let g be biconnected                                                     */
/* initialize attributes for nodes and edges  and                           *
 * set all vertices to be unnumbered                                        */

/* choose any edge as {s,t} to construct an st-numbering                    */
/* since g is biconnected,                                                  *
 * any adjacent nodes can be chosen as s_node and t_node, resp.             */
   t_node = first_node_in_graph(graph);
   s_node = t_node->slist->tnode;

/* construct a depth-first spanning tree (T,t) of G with rootnode t,        *
 * so that the first edge of the search is {t,s}                            */
   pre_order(t_node);

/* mark s,t and {s,t} old, all other vertices and edges new                 */
   node_marker(s_node)          = OLD;
   node_marker(t_node)          = OLD;
   edge_marker(t_node->slist)   = OLD;

/* initialize stack to contain s on top of t                                */
   stack = push(
	    push(
	     empty_stack, t_node), s_node);
   i = 0;
   old_v = empty_snode;
   while (!isempty(stack)) {
      vertex = top_node(stack);
      stack = pop(stack);
/*    let {v1,v2},...,{vk-1,vk} path found by PATHFINDER(v)                 */
#     ifdef DEBUG
      fprintf(outfile, "\ncurrent node is %d\n",vertex->nr);
#     endif
      path = PATHFINDER(vertex);
#     ifdef DEBUG
      if (path == NULL) {
	 fprintf(outfile, "path is null");
      } else {
	 for_slist(path,elem) {
	    fprintf(outfile,"-> %d ",snode_in_slist(elem)->nr);
	 } end_for_slist(path,elem);
      } /* endif */
      fprintf(outfile, "\n");
#     endif
      if (path != NULL) {
/*       found path v1,...,vk-1,vk                                          */
/*       add vk-1,...,v1 (v1=v on top) to stack                             */
	 path = subtract_immediately_from_slist(path, path->pre);
	 stack = concat_slists(path, stack);
      } else {
	 if (old_v != empty_snode) {
	    node_stsucc(old_v) = vertex;
	 } /* endif */
	 node_stnumber(vertex) = ++i;
	 old_v = vertex;
      } /* endif */
   } /* endwhile */

   *ps_node = s_node;
   *pt_node = t_node;

#  if defined DEBUG
   fprintf(outfile, "leaving stnumber...\n");
   fflush(outfile);
#  endif

   return node_stnumber(t_node);
}


Global Sgraph	DFS_SPANNING_TREE(Sgraph graph, Slist *rest_list)
{
   Snode    s_node;
   Sgraph	span_tree;
   Snode	node, new_node;
   Sedge	edge, new_edge;
   Slist	edge_list;

/* let g be connected                                                     */
/* initialize attributes for nodes and edges  and                           *
 * set all vertices to be unnumbered                                */

   *rest_list = empty_slist;

   if (graph == empty_sgraph) {
      return empty_sgraph;
   } /* endif */

/* choose any edge as {s,t} to construct an st-numbering                    */
/* since g is connected,                                                  *
 * any adjacent nodes can be chosen as s_node and t_node, resp.             */
   s_node = first_node_in_graph(graph);

/* construct a depth-first spanning tree (T,t) of G with rootnode t,        *
 * so that the first edge of the search is {t,s}                            */
   pre_order(s_node);

   edge_list = empty_slist;
   span_tree = make_graph(empty_attrs);
   span_tree->directed = FALSE;
   for_all_nodes(graph, node) {
      new_node = make_node(span_tree, empty_attrs);
      new_node->nr  = node->nr;
      new_node->x   = node->x;
      new_node->y   = node->y;
      new_node->iso = node;
      node->iso     = new_node;
   } end_for_all_nodes(graph, node);

   for_all_nodes(graph, node) {
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    if (edge_type(edge) == TREE_EDGE) {
	       new_edge = make_edge(node->iso, edge->tnode->iso, empty_attrs);
	    } else {
	       edge_list = enqueue(edge_list, edge);
	       edge_in_mpg(edge) = FALSE;
	    } /* endif */
	 } /* endif */
      } end_for_sourcelist(node, edge);
   } end_for_all_nodes(graph, node);

   *rest_list = edge_list;
   return span_tree;
}


Global	int	DETERMINE_BICONNECTED_COMPONENTS(Sgraph G, Slist *ComponentList)
      	  
     	                   /* list of graphs -> to be returned */
{
   Sgraph       g;
   Snode	node;
   bool		is_connected, is_biconnected;
   int		dfnum;
   int	 	RootCount, compCount/*, ArticulationCount, count */;
   Slist        edgeStack, compList;

   dfnum = 0;
   is_connected = TRUE;
   is_biconnected = TRUE;
   *ComponentList = empty_slist;
   RootCount = 0;
   edgeStack = empty_stack;
   compList = empty_slist;
   compCount = 0;
/* ArticulationCount = 0; */
/* count = 0; */

/* if G is connected then all nodes should be marked in the search */
   for_all_nodes(G, node) {
      if (node_visited(node) == UNVISITED) {
	 RootCount++;
	 BICONNECT(node, empty_snode, &dfnum, &edgeStack, &compCount, &compList);
/*	 if (node_dfsucc_count(node) > 1) {
            node_bcc_property(node) = ARTICULATION_POINT;
         } * endif */
/*	 components consisting of solely one node are not regarded */
      } /* endif */
/*    if (node_bcc_property(node) == ARTICULATION_POINT) {
	 ArticulationCount++;
      } * endif */
   } end_for_all_nodes(G, node);

   is_connected = (RootCount == 1);

      is_biconnected = ((is_connected) && (compCount == 1));

      if (is_biconnected) {
         while (!isempty(compList)) {
            g = sgraph_in_slist(compList);
            compList = rest(compList);
            remove_graph(g);
         } /* endwhile */
         *ComponentList = enqueue(*ComponentList, G);

	 return 0;
      } else {
	 if (!is_connected) {
	 } /* endif */
         *ComponentList = compList;
	 return compCount;
      } /* endif */
}



Global void Decompose_Graph_Into_Biconnected_Components(Sgraph g)
{
   int		Components;
   Slist	ComponentList;
   Snode	n;
   Sedge	e;
   char		*edge_label;


   Components = DETERMINE_BICONNECTED_COMPONENTS(g, &ComponentList);

   if (Components > 0) {
      sprintf(buffer, "\ngraph is not biconnected.");
      message(buffer);
      for_all_nodes(g, n) {
	 for_sourcelist(n, e) {
	    if (n->nr > e->tnode->nr) {
	       edge_label = NEW_LABEL;
	       sprintf(edge_label, "%2d", edge_marker(e));
	       set_edgelabel(e, edge_label);
	    }
         } end_for_sourcelist(n,e);
      } end_for_all_nodes(g,n);
      while (ComponentList != empty_slist) {
	 g = sgraph_in_slist(ComponentList);
	 remove_graph(g);
	 ComponentList = rest(ComponentList);
      }
	
   } else {
      sprintf(buffer, "\ngraph is biconnected.");
      message(buffer);
      ComponentList = rest(ComponentList);
   }
}



Local int mark_node(Snode n, int layer)
{
	Sedge e;
	int is_bip;

	is_bip = TRUE;
	if (attr_flags(n) == UNVISITED)
	  {
	    attr_flags(n) = layer ;
	    layer = ++layer % 2;
	    for_sourcelist(n, e)
	      {
		/* kein Aufruf von mark_node mehr wenn bereits erkannt, daß  *
		 * is_bip = FALSE gilt                                       */
		is_bip = is_bip && mark_node(e->tnode, layer);
		if (attr_flags(n) == attr_marker(e->tnode))
		  is_bip = FALSE;
	      }
	    end_for_sourcelist(n, e);
	  }
	return is_bip;
}




Global int is_bipartite(Sgraph in_graph)
{
	Snode node;
	int is_bip;
	int layer;

	layer = LAYER1;
	is_bip = TRUE;
	for_all_nodes(in_graph, node)
	  is_bip = is_bip && mark_node(node, layer);
	end_for_all_nodes(in_graph, node);
	return is_bip;
}







