/*************************************************************************/
/*                                                                       */
/* FILE: KANT_VISIBILITY.C                                               */
/*                                                                       */
/* Beschreibung: Funktionen fuer den Algorithmus von Kant                */
/*                                                                       */
/* benoetigte externe Funktionen: number_of_edges                        */
/*                                visibility_layout2_triangulate         */
/*                                construct_4_block_tree                 */
/*                                remove_4_block_tree                    */
/*                                equal_edge                             */
/*                                canonical_4_ordering                   */
/*                                TT_w_visibility_biconnected            */
/*                                free_visibility_node_info              */
/*                                free_visibility_edge_info              */
/*                                free_visibility_face_info              */
/*                                compute_size_of_TT_drawing             */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <algorithms.h>
#include "visibility_definitions.h"
#include "Kant_visibility.h"
#include "TTvisibility.h"

extern int number_of_nodes (Sgraph graph); /* springembedder_rf/spring.c */

typedef enum {
           up,
           down,
           unmarked
} Mark;

typedef struct edgelist_elem {  /* Listenelement zum Sichern der Kantenattribute */
           Sedge edge; /* Kante in Komponente */
           Sedge graph_edge; /* zugehoerige Kante im Graphen */
} *Edgelist_Elem;


#define GRAPH_EDGE(comp_edge) (attr_sedge(comp_edge)) /* zur Kante in der Komponente */
                                                      /* gehoerende Kante im Graphen */
#define MARK(node) (attr_int(node)) /* up,down,unmarked */
#define MARK_SET(node,set) (set_nodeattrs((node),make_attr(ATTR_INTEGER,(set)))) /* up,down,unmarked */
#define SOURCE(edge) (attr_snode(edge)) /* bestimmt Kantenrichtung */
#define EDGELIST_ELEM(x) (attr_data_of_type((x),Edgelist_Elem))
#define add_edgelist_elem_to_slist(list,elem) (add_immediately_to_slist(list,make_attr(ATTR_DATA,(char*)(elem))))
#define VISITED(node) (attr_int(node)) /* fuer die topologische Sortierung */



Local Sedge compute_st_edge (Sgraph graph, Snode s, Snode t) /* bestimmt Kante (s,t) */
             
           
{
   Sedge edge;

   for_sourcelist (s,edge)
      if (TARGET_NODE(edge,s) == t)
         return edge;
   end_for_sourcelist (s,edge);
   return NULL;
}



Local Slist make_slist_of_edges (Sgraph component) /* Sichern der Kantenattribute */
                                            /* in der Liste edgelist       */
{
   Slist         edgelist;
   Snode         node;
   Sedge         edge;
   Edgelist_Elem elem;

   edgelist = empty_slist;

   for_all_nodes (component,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            elem = (Edgelist_Elem) malloc(sizeof(struct edgelist_elem));
            elem->edge = edge;
            elem->graph_edge = GRAPH_EDGE(edge);
            edgelist = add_edgelist_elem_to_slist(edgelist,elem);
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (component,node);

   return edgelist;
}



Local void direct_edges (Slist edgelist) /* Richten der Kanten entsprechend */
                                   /* der Knotennummerierungen        */
{
   Slist         l;
   Edgelist_Elem elem;
   Sedge         edge,graph_edge;

   for_slist (edgelist,l)
      elem = EDGELIST_ELEM(l);
      edge = elem->edge;
      graph_edge = elem->graph_edge;
      if (edge->snode->nr < edge->tnode->nr)
         set_edgeattrs (graph_edge,make_attr_snode(edge->snode->iso)); /* SOURCE */
      else
         set_edgeattrs (graph_edge,make_attr_snode(edge->tnode->iso)); /* SOURCE */
      free (elem);
   end_for_slist (edgelist,l);
   free_slist (edgelist);
}



Local void draw_root_component (Sgraph component, Snode s, Snode t, Sedge st_edge) /* nummeriert Knoten der */
                                                       /* Wurzelkomponente      */
           
               
{
   Snode         node,u,v,w;
   Slist         l,help_pointer;
   Sedge         edge;
   Slist         edgelist;

   for_all_nodes (component,node)
      if (node->iso == s) {
         u = node;
         for_slist (attr_slist(node),l)
            edge = attr_sedge(l);
            if (equal_edge(GRAPH_EDGE(edge),st_edge)) { /* GRAPH_EDGE(edge) = st_edge? */
               help_pointer = l;
               w = TARGET_NODE(edge,u);
               break;
            }
         end_for_slist (attr_slist(node),l);
         break;
      }
   end_for_all_nodes (component,node);

   edge = attr_sedge(help_pointer->pre);
   v = TARGET_NODE(edge,u);

   edgelist = make_slist_of_edges (component); /* Sichern der Kantenattribute */

   canonical_4_ordering (component,u,v,w);

   direct_edges (edgelist); /* Richten der Kanten */

   for_all_nodes (component,node)
      if (node->nr == 2)
         MARK_SET(node->iso,down);
      else if (node->nr == number_of_nodes(component) - 1)
         MARK_SET(node->iso,up);
      else
         MARK_SET(node->iso,unmarked);
   end_for_all_nodes (component,node);
}



Local void normal_ordering (Sgraph component, Snode u, Snode v, Snode w) /* NORMAL im Algorithmus von Kant */
                 
             
{
   canonical_4_ordering (component,u,v,w);
}



Local void reverse_ordering (Sgraph component, Snode u, Snode v, Snode w) /* REVERSE im Algorithmus von Kant */
                 
             
{
   int   number;
   Snode node;

   number = number_of_nodes (component);

   canonical_4_ordering (component,w,v,u);

   for_all_nodes (component,node)
      node->nr = number - node->nr + 1;
   end_for_all_nodes (component,node);
}



Local void draw_component (Snode tree_node, Sedge tree_edge) /* nummeriert die Knoten der Kom- */
                                                /* ponente, die in tree_node ist  */
                 /* Kante zum Vater von tree_node */
{
   Sgraph   component;
   Triangle triangle;
   Slist    edgelist;
   int      number;
   Snode    u,v,w,node,tree_node1;
   Sedge    edge,tree_edge1;

   component = attr_sgraph(tree_node);

   triangle = iif (tree_node == TREE_EDGE_SOURCE(tree_edge),
                   TRIANGLE_IN_SOURCE(tree_edge),
                   TRIANGLE_IN_TARGET(tree_edge)); /* zugehoeriges separierendes Dreieck */

   /********************** Bestimmung von u,v,w ************************/

   if ((triangle->u->iso == SOURCE(GRAPH_EDGE(triangle->edge1))) &&
       (triangle->u->iso == SOURCE(GRAPH_EDGE(triangle->edge3))))
      u = triangle->u;
   else if ((triangle->u->iso != SOURCE(GRAPH_EDGE(triangle->edge1))) &&
            (triangle->u->iso != SOURCE(GRAPH_EDGE(triangle->edge3))))
      w = triangle->u;
   else
      v = triangle->u;

   if ((triangle->v->iso == SOURCE(GRAPH_EDGE(triangle->edge1))) &&
       (triangle->v->iso == SOURCE(GRAPH_EDGE(triangle->edge2))))
      u = triangle->v;
   else if ((triangle->v->iso != SOURCE(GRAPH_EDGE(triangle->edge1))) &&
            (triangle->v->iso != SOURCE(GRAPH_EDGE(triangle->edge2))))
      w = triangle->v;
   else
      v = triangle->v;

   if ((triangle->w->iso == SOURCE(GRAPH_EDGE(triangle->edge2))) &&
       (triangle->w->iso == SOURCE(GRAPH_EDGE(triangle->edge3))))
      u = triangle->w;
   else if ((triangle->w->iso != SOURCE(GRAPH_EDGE(triangle->edge2))) &&
            (triangle->w->iso != SOURCE(GRAPH_EDGE(triangle->edge3))))
      w = triangle->w;
   else
      v = triangle->w;

   /*********************************************************************/

   edgelist = make_slist_of_edges (component); /* Sichern der Kantenattribute */

   number = number_of_nodes (component);

   switch (MARK(v->iso)) {
      case unmarked: normal_ordering (component,u,v,w);
                     MARK_SET(v->iso,down);
                     for_sourcelist (u,edge)
                        node = TARGET_NODE(edge,u);
                        if (node->nr == number - 1) {
                           MARK_SET(node->iso,up);
                           break;
                        }
                     end_for_sourcelist (u,edge);
                     break;
      case up      : normal_ordering (component,u,v,w);
                     MARK_SET(v->iso,unmarked);
                     for_sourcelist (u,edge)
                        node = TARGET_NODE(edge,u);
                        if (node->nr == number - 1) {
                           MARK_SET(node->iso,up);
                           break;
                        }
                     end_for_sourcelist (u,edge);
                     break;
      case down    : reverse_ordering (component,u,v,w);
                     MARK_SET(v->iso,unmarked);
                     for_sourcelist (u,edge)
                        node = TARGET_NODE(edge,u);
                        if (node->nr == 2) {
                           MARK_SET(node->iso,down);
                           break;
                        }
                     end_for_sourcelist (u,edge);
                     break;
   }

   for_all_nodes (component,node)
      if ((node->nr > 2) && (node->nr < number - 1))
         MARK_SET(node->iso,unmarked);
   end_for_all_nodes (component,node);

   direct_edges (edgelist); /* Richten der Kanten */

   for_sourcelist (tree_node,tree_edge1)
      if (!equal_edge(tree_edge1,tree_edge)) {
         tree_node1 = TARGET_NODE(tree_edge1,tree_node);  /* tree_node1 Sohn von tree_node */
         draw_component (tree_node1,tree_edge1);
      }
   end_for_sourcelist (tree_node,tree_edge1);
}



Local void topological_ordering (Snode v, int *nr) /* topologische Sortierung der Knoten */
         
           
{
   Sedge edge;
   Snode w;

   for_sourcelist (v,edge)
      w = TARGET_NODE(edge,v);
      if ((v == SOURCE(edge)) && !VISITED(w)) {
         set_nodeattrs (w,make_attr(ATTR_INTEGER,TRUE)); /* VISITED(w) */
         topological_ordering (w,nr);
      }
   end_for_sourcelist (v,edge);
   v->nr = (*nr)--;
}



void Kant_w_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge) /* Hauptfunktion fuer den */
                                              /* Algorithmus von Kant   */
           
                  
{
   Slist       dummy_edges = empty_slist;
   Slist       l;
   bool        contains_dummies;
   Sedge       st_edge,tree_edge;
   Tree_Result tree_result;
   Sgraph      tree;
   Snode       root,node,tree_node;
   Sgraph      component;
   int         nr;
   Sgraph      dual_graph;

   if (number_of_edges(graph) < 3 * number_of_nodes(graph) - 6) /* graph nicht trianguliert */
      dummy_edges = visibility_layout2_triangulate (graph);
   else embed (graph);

   contains_dummies = (dummy_edges != empty_slist);

   if (dummy_edge == empty_sedge)
      st_edge = compute_st_edge (graph,s,t);
   else {
      st_edge = dummy_edge;
      dummy_edges = add_sedge_to_slist(dummy_edges,dummy_edge);
   }

   tree_result = construct_4_block_tree (graph,st_edge);

   tree = tree_result->tree;
   root = tree_result->root; /* Wurzel des 4-block trees */
   for_all_nodes (graph,node)
      MARK_SET (node,unmarked);
   end_for_all_nodes (graph,node);

   component = attr_sgraph(root); /* Wurzelkomponente */

   draw_root_component (component,s,t,st_edge);

   for_sourcelist (root,tree_edge)
      tree_node = TARGET_NODE(tree_edge,root);
      draw_component (tree_node,tree_edge);
   end_for_sourcelist (root,tree_edge);

   remove_4_block_tree (tree,graph);

   for_all_nodes (graph,node)
      set_nodeattrs (node,make_attr(ATTR_INTEGER,FALSE)); /* VISITED(node) = FALSE */
   end_for_all_nodes (graph,node);

   nr = number_of_nodes (graph);
   topological_ordering (s,&nr);

   dual_graph = TT_w_visibility_biconnected (graph,s,t,empty_snode,dummy_edges,contains_dummies,Kant_weak); 
                                /* Konstruktion der Darstellung */  

   free_visibility_face_info (dual_graph); /* Speicherfreigabe */
   remove_graph (dual_graph);     

   compute_size_of_TT_drawing (graph); /* Groesse der Darstellung */

   free_visibility_node_info (graph); /* Speicherfreigabe */

   if (!compression_used) /* Compression wurde nicht angewendet */
      free_visibility_edge_info (graph); /* Speicherfreigabe */

   for_slist (dummy_edges,l)
      remove_edge (attr_sedge(l));
   end_for_slist (dummy_edges,l);
   free_slist (dummy_edges);
}



/*************************************************************************/
/*                                                                       */
/*                    END OF FILE: KANT_VISIBILITY.C                     */
/*                                                                       */
/*************************************************************************/
