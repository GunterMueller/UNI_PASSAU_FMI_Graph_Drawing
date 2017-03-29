/*************************************************************************/
/*                                                                       */
/* FILE: 4_BLOCK_TREE.C                                                  */
/*                                                                       */
/* Beschreibung: Funktionen zur Konstruktion und zur Entfernung des      */
/*               4-block trees fuer den Algorithmus von Kant; Knoten des */
/*               Baums enthalten in attr_sgraph Zeiger auf entsprechende */
/*               Komponente, Kanten des Baums enthalten Attribute vom    */
/*               Typ Tree_Edge_Info, Knoten und Kanten einer Komponente  */
/*               enthalten Zeiger auf die entsprechenden Knoten und Kan- */
/*               ten des Graphen (in attr_sedge(edge) bzw. node->iso);   */
/*               Graph muss eingebettet uebergeben werden (Kantenliste   */
/*               fuer jeden Knoten).                                     */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include "visibility_definitions.h"
#include "Kant_visibility.h"

extern int number_of_nodes (Sgraph graph); /* springembedder_rf/spring.c */

typedef struct internal_node_info { /* interne Knotenattribute */
           Slist edge_rotation; /* Reihenfolge der Kanten in der Einbettung */
           bool  mark;        /************************************/
           bool  deleted;     /* fuer die Bestimmung der Dreiecke */
           Sedge mark_edge;   /************************************/
} *Internal_Node_Info;

typedef struct internal_edge_info { /* intene Kantenattribute */
           Snode source;
           Slist triangles; /* sep. Dreiecke, die die Kante enthalten */
           Slist sorted_triangles; /* sep. Dreiecke sortiert */
           int   number; /* Nummerierung in einer Rotation */
           Slist position_in_source_rotation; /* Pos. der Kante in der */
                                              /* Rotation von source   */
           Slist position_in_target_rotation; /* Pos. ... von target   */
           Sedge subgr_edge; /* aktuelle Subgraph-Kante */
           Sedge curr_subgr_edge; /* Kante im gegenwaertigen Subgraphen */
           Snode tree_node; /* Baumknoten, der im Moment die Kante enthaelt */
} *Internal_Edge_Info;

typedef struct subgraph_node_info { /* Knotenattribute eines Subgraphen */
           Slist edge_rotation; 
           bool  seen;
           bool  ready;
} *Subgraph_Node_Info;

typedef struct subgraph_edge_info { /* Kantenattribute eines Subgraphen */
           Sedge graph_edge; /* zugehoerige Kante im Graphen */
           Snode source;
           Slist position_in_source_rotation;
           Slist position_in_target_rotation;
} *Subgraph_Edge_Info;



#define INTERNAL_NODE_INFO(node) (attr_data_of_type((node),Internal_Node_Info))
#define EDGE_ROTATION(node) (INTERNAL_NODE_INFO(node)->edge_rotation)
#define MARK(node) (INTERNAL_NODE_INFO(node)->mark)
#define DELETED(node) (INTERNAL_NODE_INFO(node)->deleted)
#define MARK_EDGE(node) (INTERNAL_NODE_INFO(node)->mark_edge)
#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define SOURCE(edge) (INTERNAL_EDGE_INFO(edge)->source)
#define TRIANGLES(edge) (INTERNAL_EDGE_INFO(edge)->triangles)
#define SORTED_TRIANGLES(edge) (INTERNAL_EDGE_INFO(edge)->sorted_triangles)
#define NUMBER(edge) (INTERNAL_EDGE_INFO(edge)->number)
#define POSITION_IN_SOURCE_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_source_rotation)
#define POSITION_IN_TARGET_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_target_rotation)
#define POSITION(edge,node) (iif((node)==SOURCE(edge),POSITION_IN_SOURCE_ROTATION(edge),POSITION_IN_TARGET_ROTATION(edge)))
#define SUBGR_EDGE(edge) (INTERNAL_EDGE_INFO(edge)->subgr_edge)
#define CURR_SUBGR_EDGE(edge) (INTERNAL_EDGE_INFO(edge)->curr_subgr_edge)
#define TREE_NODE(edge) (INTERNAL_EDGE_INFO(edge)->tree_node)
#define SUBGRAPH_NODE_INFO(node) (attr_data_of_type((node),Subgraph_Node_Info))
#define SUBGR_EDGE_ROTATION(node) (SUBGRAPH_NODE_INFO(node)->edge_rotation)
#define SEEN(node) (SUBGRAPH_NODE_INFO(node)->seen)
#define READY(node) (SUBGRAPH_NODE_INFO(node)->ready)
#define SUBGRAPH_EDGE_INFO(edge) (attr_data_of_type((edge),Subgraph_Edge_Info))
#define GRAPH_EDGE(edge) (SUBGRAPH_EDGE_INFO(edge)->graph_edge)
#define SUBGR_EDGE_SOURCE(edge) (SUBGRAPH_EDGE_INFO(edge)->source)
#define POS_IN_SUBGR_SOURCE_ROT(edge) (SUBGRAPH_EDGE_INFO(edge)->position_in_source_rotation)
#define POS_IN_SUBGR_TARGET_ROT(edge) (SUBGRAPH_EDGE_INFO(edge)->position_in_target_rotation)
#define SUBGR_POS(edge,node) (iif((node)==SUBGR_EDGE_SOURCE(edge),POS_IN_SUBGR_SOURCE_ROT(edge),POS_IN_SUBGR_TARGET_ROT(edge)))



Local void make_internal_attrs (Sgraph graph) /* erzeugt intern benoetigte Attribute */
             
{
   Snode              node;
   Sedge              edge;
   Slist              l;
   Internal_Node_Info node_info;
   Internal_Edge_Info edge_info;

   for_all_nodes (graph,node)
      node_info = (Internal_Node_Info) malloc(sizeof(struct internal_node_info));
      node_info->edge_rotation = attr_slist(node);
      node_info->mark = node_info->deleted = FALSE;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->source = empty_snode;
            edge_info->triangles = edge_info->sorted_triangles = empty_slist;
            set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   for_all_nodes (graph,node)
      for_slist (EDGE_ROTATION(node),l)
         edge = attr_sedge(l);
         if (SOURCE(edge) == empty_snode) {
            SOURCE(edge) = node;
            POSITION_IN_SOURCE_ROTATION(edge) = l;
         } else
            POSITION_IN_TARGET_ROTATION(edge) = l;
      end_for_slist (EDGE_ROTATION(node),l);
   end_for_all_nodes (graph,node);
}



Local Slist bucket_sort (Sgraph graph) /* sortiert Knoten nach fallender Valenz */
             
{
   int    n;
   Slist *bucket;
   int    i,degree;
   Snode  node;
   Slist  sourcelist,nodelist,l;

   n = number_of_nodes (graph);

   bucket = (Slist*) calloc(n,sizeof(Slist));

   for (i=0;i<n;i++)
      bucket[i] = empty_slist;

   for_all_nodes (graph,node)
      sourcelist = make_slist_of_sourcelist (node);
      degree = size_of_slist (sourcelist);
      free_slist (sourcelist);
      bucket[degree] = add_snode_to_slist(bucket[degree],node);
   end_for_all_nodes (graph,node);

   nodelist = empty_slist;

   for (i=n-1;i>=0;i--) {
      for_slist (bucket[i],l)
         node = attr_snode(l);
         nodelist = add_snode_to_slist(nodelist,node);
      end_for_slist (bucket[i],l);
      free_slist (bucket[i]);
   }

   free (bucket);

   return nodelist;
}



Local Slist compute_triangles (Sgraph graph) /* berechnet alle Dreiecke von graph */
             
{
   Slist    nodelist,triangles,neighbors,l,n;
   Snode    node,neighbor,target_node;
   Sedge    edge;
   Triangle triangle;

   nodelist = bucket_sort (graph);
   triangles = empty_slist;

   for_slist (nodelist,l)

      node = attr_snode(l);
      neighbors = empty_slist;

      for_sourcelist (node,edge)
         neighbor = TARGET_NODE(edge,node);
         if (!DELETED(neighbor)) {
            MARK(neighbor) = TRUE;
            MARK_EDGE(neighbor) = edge;
            neighbors = add_snode_to_slist(neighbors,neighbor);
         }
      end_for_sourcelist (node,edge);

      for_slist (neighbors,n)
         neighbor = attr_snode(n);
         for_sourcelist (neighbor,edge)
            target_node = TARGET_NODE(edge,neighbor);
            if (MARK(target_node)) {
               triangle = (Triangle) malloc(sizeof(struct triangle));
               triangle->edge1 = MARK_EDGE(neighbor);
               triangle->u = triangle->edge1->snode;
               triangle->v = triangle->edge1->tnode;
               if ((edge->snode == triangle->v) || (edge->tnode == triangle->v)) {
                  triangle->edge2 = edge;
                  triangle->edge3 = MARK_EDGE(target_node);
                  triangle->w = iif (edge->snode == triangle->v,edge->tnode,edge->snode);
               } else {
                  triangle->edge2 = MARK_EDGE(target_node);
                  triangle->edge3 = edge;
                  triangle->w = iif (edge->snode == triangle->u,edge->tnode,edge->snode);
               }                                      
               triangles = add_triangle_to_slist(triangles,triangle);
            }
         end_for_sourcelist (neighbor,edge);
         MARK(neighbor) = FALSE;
      end_for_slist (neighbors,n);

      DELETED(node) = TRUE;
      free_slist (neighbors);

   end_for_slist (nodelist,l);

   free_slist (nodelist);

   return triangles;
}



Local Slist compute_separating_triangles (Sgraph graph) /* berechnet alle separie-   */ 
                                                 /* renden Dreiecke von graph */
{
   Slist    triangles,removed_triangles,l;
   Triangle triangle;
   Sedge    edge1,edge3;
   Snode    u;

   triangles = compute_triangles (graph);

   removed_triangles = empty_slist;

   for_slist (triangles,l)
      triangle = TRIANGLE(l);
      edge1 = triangle->edge1;
      u  = triangle->u;
      edge3 = triangle->edge3;
      if ((POSITION(edge3,u) == POSITION(edge1,u)->suc) ||
          (POSITION(edge3,u) == POSITION(edge1,u)->pre))  /* Dreieck ist Face */
         removed_triangles = add_slist_to_slist(removed_triangles,l);
   end_for_slist (triangles,l);

   for_slist (removed_triangles,l)
      free (TRIANGLE(attr_slist(l)));
      triangles = subtract_immediately_from_slist (triangles,attr_slist(l));
   end_for_slist (removed_triangles,l);

   free_slist (removed_triangles);

   return triangles;
}



Local void make_triangle_lists (Slist triangles) /* erzeugt fuer jede Kante Liste der */
                                           /* Dreiecke, die die Kante enthalten */
{
   Slist    l;
   Triangle triangle;
   Sedge    edge;

   for_slist (triangles,l)
      triangle = TRIANGLE(l);
      edge = triangle->edge1;
      TRIANGLES(edge) = add_triangle_to_slist(TRIANGLES(edge),triangle);
      edge = triangle->edge2;
      TRIANGLES(edge) = add_triangle_to_slist(TRIANGLES(edge),triangle);
      edge = triangle->edge3;
      TRIANGLES(edge) = add_triangle_to_slist(TRIANGLES(edge),triangle);
   end_for_slist (triangles,l);
}



bool equal_edge (Sedge edge1, Sedge edge2) /* edge1 = edge2? */
                  
{
   return ((edge1->snode == edge2->snode) || (edge1->snode == edge2->tnode)) &&
          ((edge1->tnode == edge2->snode) || (edge1->tnode == edge2->tnode));
}



Local Sedge other_edge (Triangle triangle, Sedge edge, Snode node) /* Kante != edge in triangle, */
                                            /* die zu node inzident ist   */
              
              
{
   if (!equal_edge(triangle->edge1,edge) &&
       ((triangle->edge1->snode == node) || (triangle->edge1->tnode == node)))
      return triangle->edge1;

   if (!equal_edge(triangle->edge2,edge) && 
       ((triangle->edge2->snode == node) || (triangle->edge2->tnode == node)))
      return triangle->edge2;

   return triangle->edge3;
}



Local void sort_triangles (Sgraph graph) /* sortiert separierende Dreiecke */
             
{
   Snode    node;
   int      degree,number,distance;
   Slist    l,l1,l2,help_pointer;
   Sedge    edge,edge1,edge2;
   Triangle triangle;

   for_all_nodes (graph,node)
      degree = size_of_slist (EDGE_ROTATION(node));

      number = 0;
      for_slist (EDGE_ROTATION(node),l)
         edge = attr_sedge(l);
         NUMBER(edge) = number;
         number++;
      end_for_slist (EDGE_ROTATION(node),l);

      for_slist (EDGE_ROTATION(node),l1)
         edge1 = attr_sedge(l1);
         for_slist (TRIANGLES(edge1),l2)
            triangle = TRIANGLE(l2);
            edge2 = other_edge (triangle,edge1,node);
            if (node == SOURCE(edge2))
               SORTED_TRIANGLES(edge2) = add_triangle_to_slist(SORTED_TRIANGLES(edge2),triangle);
         end_for_slist (TRIANGLES(edge1),l2);
      end_for_slist (EDGE_ROTATION(node),l1);

      for_sourcelist (node,edge1)
         if ((node == SOURCE(edge1)) &&
             (size_of_slist(SORTED_TRIANGLES(edge1)) > 1)) {
            distance = 0;
            for_slist (SORTED_TRIANGLES(edge1),l)
               triangle = TRIANGLE(l);
               edge2 = other_edge (triangle,edge1,node);
               if (distance == 0) {
                  if (NUMBER(edge2) - NUMBER(edge1) > 0)
                     distance = NUMBER(edge2) - NUMBER(edge1);
                  else
                     distance = degree + NUMBER(edge2) - NUMBER(edge1);
                  help_pointer = l;
               } else if (NUMBER(edge2) - NUMBER(edge1) > 0) {
                  if (NUMBER(edge2) - NUMBER(edge1) < distance) {
                     help_pointer = l;
                     break;
                  }
               } else if (degree + NUMBER(edge2) - NUMBER(edge1) < distance) {
                  help_pointer = l;
                  break;
               }
            end_for_slist (SORTED_TRIANGLES(edge1),l);
            SORTED_TRIANGLES(edge1) = help_pointer;
         }
      end_for_sourcelist (node,edge1);
   end_for_all_nodes (graph,node);
}



Local bool is_correct_triangle (Triangle triangle) /* ist triangle das 1. oder letzte    */
                                          /* Element von SORTED_TRIANGLES(edge) */
{                                         /* fuer alle Kanten edge von triangle */
   Sedge edge1,edge2,edge3;

   edge1 = triangle->edge1;
   edge2 = triangle->edge2;
   edge3 = triangle->edge3;

   return ((triangle == TRIANGLE(SORTED_TRIANGLES(edge1))) || (triangle == TRIANGLE(SORTED_TRIANGLES(edge1)->pre))) &&
          ((triangle == TRIANGLE(SORTED_TRIANGLES(edge2))) || (triangle == TRIANGLE(SORTED_TRIANGLES(edge2)->pre))) &&
          ((triangle == TRIANGLE(SORTED_TRIANGLES(edge3))) || (triangle == TRIANGLE(SORTED_TRIANGLES(edge3)->pre)));
}



Local Slist make_triangle_sublist (Slist separating_triangles) /* Dreiecke, die als   */
                                                         /* naechste bearbeitet */
{                                                        /* werden koennen      */
   Slist    sublist = empty_slist;
   Slist    l;
   Triangle triangle;

   for_slist (separating_triangles,l)
      triangle = TRIANGLE(l);
      if (is_correct_triangle(triangle))
         sublist = add_triangle_to_slist(sublist,triangle);
   end_for_slist (separating_triangles,l);

   return sublist;
}



Local void make_first_tree_node (Sgraph tree, Sgraph graph) /* konstruiert den 1. Knoten */
                                             /* des 4-block trees         */
{
   Sgraph             subgraph;
   Snode              tree_node,node,subgr_node;
   Sedge              edge,subgr_edge;
   Subgraph_Node_Info node_info;
   Subgraph_Edge_Info edge_info;
   Slist              l;

   subgraph = make_graph (empty_attr);
   subgraph->directed = FALSE;
   tree_node = make_node (tree,make_attr_sgraph(subgraph));

   for_all_nodes (graph,node)
      node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
      node_info->edge_rotation = empty_slist;
      node_info->seen = node_info->ready = FALSE;
      subgr_node = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
      subgr_node->iso = node;
      node->iso = subgr_node;
   end_for_all_nodes (graph,node);

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (node == SOURCE(edge)) {
            edge_info = (Subgraph_Edge_Info) malloc(sizeof(struct subgraph_edge_info));
            edge_info->graph_edge = edge;
            edge_info->source = node->iso;
            subgr_edge = make_edge (node->iso,TARGET_NODE(edge,node)->iso,make_attr(ATTR_DATA,(char*)edge_info));
            SUBGR_EDGE(edge) = subgr_edge;
            TREE_NODE(edge) = tree_node;
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   for_all_nodes (graph,node)
      subgr_node = node->iso;
      for_slist (EDGE_ROTATION(node),l)
         edge = attr_sedge(l);
         subgr_edge = SUBGR_EDGE(edge);
         SUBGR_EDGE_ROTATION(subgr_node) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(subgr_node),subgr_edge);
         iif (subgr_node == SUBGR_EDGE_SOURCE(subgr_edge),
              POS_IN_SUBGR_SOURCE_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(subgr_node)->pre,
              POS_IN_SUBGR_TARGET_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(subgr_node)->pre);
      end_for_slist (EDGE_ROTATION(node),l);
   end_for_all_nodes (graph,node);
}



Local Slist refresh_triangle_sublist (Slist sublist, Triangle triangle) /* updated Liste der Drei-   */
                                                        /* ecke, die als naechste    */
                                                        /* bearbeitet werden koennen */
{
   Sedge    edge1,edge2,edge3;
   Triangle triangle1;

   edge1 = triangle->edge1;
   edge2 = triangle->edge2;
   edge3 = triangle->edge3;

   if (triangle == TRIANGLE(SORTED_TRIANGLES(edge1))) {
      SORTED_TRIANGLES(edge1) = subtract_first_element_from_slist(SORTED_TRIANGLES(edge1));
      if (size_of_slist(SORTED_TRIANGLES(edge1)) > 1) {
         triangle1 = TRIANGLE(SORTED_TRIANGLES(edge1));
         if (is_correct_triangle (triangle1))
            sublist = add_triangle_to_slist(sublist,triangle1);
      }
   } else {
      SORTED_TRIANGLES(edge1) = subtract_last_element_from_slist(SORTED_TRIANGLES(edge1));
      if (size_of_slist(SORTED_TRIANGLES(edge1)) > 1) {
         triangle1 = TRIANGLE(SORTED_TRIANGLES(edge1)->pre);
         if (is_correct_triangle (triangle1))
            sublist = add_triangle_to_slist(sublist,triangle1);
      }
   }

   if (triangle == TRIANGLE(SORTED_TRIANGLES(edge2))) {
      SORTED_TRIANGLES(edge2) = subtract_first_element_from_slist(SORTED_TRIANGLES(edge2));
      if (size_of_slist(SORTED_TRIANGLES(edge2)) > 1) {
         triangle1 = TRIANGLE(SORTED_TRIANGLES(edge2));
         if (is_correct_triangle (triangle1))
            sublist = add_triangle_to_slist(sublist,triangle1);
      }
   } else {
      SORTED_TRIANGLES(edge2) = subtract_last_element_from_slist(SORTED_TRIANGLES(edge2));
      if (size_of_slist(SORTED_TRIANGLES(edge2)) > 1) {
         triangle1 = TRIANGLE(SORTED_TRIANGLES(edge2)->pre);
         if (is_correct_triangle (triangle1))
            sublist = add_triangle_to_slist(sublist,triangle1);
      }
   }

   if (triangle == TRIANGLE(SORTED_TRIANGLES(edge3))) {
      SORTED_TRIANGLES(edge3) = subtract_first_element_from_slist(SORTED_TRIANGLES(edge3));
      if (size_of_slist(SORTED_TRIANGLES(edge3)) > 1) {
         triangle1 = TRIANGLE(SORTED_TRIANGLES(edge3));
         if (is_correct_triangle (triangle1))
            sublist = add_triangle_to_slist(sublist,triangle1);
      }
   } else {
      SORTED_TRIANGLES(edge3) = subtract_last_element_from_slist(SORTED_TRIANGLES(edge3));
      if (size_of_slist(SORTED_TRIANGLES(edge3)) > 1) {
         triangle1 = TRIANGLE(SORTED_TRIANGLES(edge3)->pre);
         if (is_correct_triangle (triangle1))
            sublist = add_triangle_to_slist(sublist,triangle1);
      }
   }

   return sublist;
}



Local Sgraph construct_tree (Sgraph graph, Slist triangle_sublist) /* eigentliche Konstruktion */
                                                     /* des 4-block trees        */
                        
{
   Sgraph             tree,subgraph;
   Triangle           triangle,triangle1,triangle2,triangle3;
   Slist              nodelist,subgr_nodes,subgr_edges,rotation_elements,elem,removed_edges,l;
   Snode              tree_node_old,tree_node_new;
   Snode              u,v,w,u1,v1,w1,node,subgr_node,neighbor,subgr_neighbor;
   Sedge              edge,edge1,edge2,edge3,subgr_edge,tree_edge;
   Subgraph_Node_Info node_info;
   Subgraph_Edge_Info edge_info;
   Tree_Edge_Info     tree_edge_info;

   tree = make_graph (empty_attr);
   tree->directed = FALSE;

   make_first_tree_node (tree,graph);

   while (triangle_sublist != empty_slist) {

      triangle = TRIANGLE(triangle_sublist);
      triangle_sublist = subtract_first_element_from_slist(triangle_sublist);

      nodelist = subgr_nodes = subgr_edges = rotation_elements = empty_slist;

      subgraph = make_graph (empty_attr);
      subgraph->directed = FALSE;
      tree_node_new = make_node (tree,make_attr_sgraph(subgraph));

      edge1 = SUBGR_EDGE(triangle->edge1);
      edge2 = SUBGR_EDGE(triangle->edge2);
      edge3 = SUBGR_EDGE(triangle->edge3);

      u = iif (edge1->snode->iso == triangle->u,
               edge1->snode,edge1->tnode);
      v = iif (edge2->snode->iso == triangle->v,
               edge2->snode,edge2->tnode);
      w = iif (edge3->snode->iso == triangle->w,
               edge3->snode,edge3->tnode);
      tree_node_old = TREE_NODE(triangle->edge1);

      triangle1 = (Triangle) malloc(sizeof(struct triangle));
      triangle1->edge1 = edge1;
      triangle1->edge2 = edge2;
      triangle1->edge3 = edge3;
      triangle1->u = u;
      triangle1->v = v;
      triangle1->w = w;

      node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
      node_info->edge_rotation = empty_slist;
      node_info->seen = node_info->ready = FALSE;
      u1 = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
      u1->iso = triangle->u;
      triangle->u->iso = u1;
      node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
      node_info->edge_rotation = empty_slist;
      node_info->seen = node_info->ready = FALSE;
      v1 = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
      v1->iso = triangle->v;
      triangle->v->iso = v1;
      node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
      node_info->edge_rotation = empty_slist;
      node_info->seen = node_info->ready = FALSE;
      w1 = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
      w1->iso = triangle->w;
      triangle->w->iso = w1;

      SEEN(u) = SEEN(v) = SEEN(w) = TRUE;
      subgr_nodes = add_snode_to_slist( /* Knoten im Subgraphen */
                    add_snode_to_slist(
                    add_snode_to_slist(subgr_nodes,u),v),w);
      subgr_edges = add_sedge_to_slist( /* Kanten im Subgraphen */
                    add_sedge_to_slist(
                    add_sedge_to_slist(subgr_edges,edge1),edge2),edge3);

      triangle2 = (Triangle) malloc(sizeof(struct triangle));
      triangle2->u = u1;
      triangle2->v = v1;
      triangle2->w = w1;

      for_slist (SUBGR_POS(edge1,u),l) /* Adjazenzliste von u aufteilen */
         edge = attr_sedge(l);
         if (equal_edge(edge,edge3)) break;
         node = TARGET_NODE(edge,u);
         if (!SEEN(node)) {
            SEEN(node) = TRUE;
            nodelist = add_snode_to_slist(nodelist,node);
            subgr_nodes = add_snode_to_slist(subgr_nodes,node);
            node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
            node_info->edge_rotation = empty_slist;
            node_info->seen = node_info->ready = FALSE;
            subgr_node = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
            subgr_node->iso = node->iso;
            node->iso->iso = subgr_node;
         }
         edge_info = (Subgraph_Edge_Info) malloc(sizeof(struct subgraph_edge_info));
         edge_info->graph_edge = GRAPH_EDGE(edge);
         edge_info->source = u1;
         subgr_edge = make_edge (u1,node->iso->iso,make_attr(ATTR_DATA,(char*)edge_info));
         if (TREE_NODE(GRAPH_EDGE(edge)) == tree_node_old) {
            SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
            TREE_NODE(GRAPH_EDGE(edge)) = tree_node_new;
         }
         CURR_SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
         SUBGR_EDGE_ROTATION(u1) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(u1),subgr_edge);
         POS_IN_SUBGR_SOURCE_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(u1)->pre;
         if (equal_edge(edge,edge1))
            triangle2->edge1 = subgr_edge;
         else {
            subgr_edges = add_sedge_to_slist(subgr_edges,edge);
            rotation_elements = add_slist_to_slist(rotation_elements,l);
         }
      end_for_slist (SUBGR_POS(edge1,u),l);
      READY(u) = TRUE;

      for_slist (rotation_elements,l) /* Elemente der Rotation entfernen */
         elem = attr_slist(l);
         SUBGR_EDGE_ROTATION(u) = subtract_immediately_from_slist(SUBGR_EDGE_ROTATION(u),elem);
      end_for_slist (rotation_elements,l);
      free_slist (rotation_elements);
      rotation_elements = empty_slist;

      for_slist (SUBGR_POS(edge2,v),l) /* Adjazenzliste von v aufteilen */
         edge = attr_sedge(l);
         if (equal_edge(edge,edge1)) {
            SUBGR_EDGE_ROTATION(v1) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(v1),triangle2->edge1);
            POS_IN_SUBGR_TARGET_ROT(triangle2->edge1) = SUBGR_EDGE_ROTATION(v1)->pre;
            break;
         }
         node = TARGET_NODE(edge,v);
         if (!SEEN(node)) {
            SEEN(node) = TRUE;
            nodelist = add_snode_to_slist(nodelist,node);
            subgr_nodes = add_snode_to_slist(subgr_nodes,node);
            node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
            node_info->edge_rotation = empty_slist;
            node_info->seen = node_info->ready = FALSE;
            subgr_node = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
            subgr_node->iso = node->iso;
            node->iso->iso = subgr_node;
         }
         edge_info = (Subgraph_Edge_Info) malloc(sizeof(struct subgraph_edge_info));
         edge_info->graph_edge = GRAPH_EDGE(edge);
         edge_info->source = v1;
         subgr_edge = make_edge (v1,node->iso->iso,make_attr(ATTR_DATA,(char*)edge_info));
         if (TREE_NODE(GRAPH_EDGE(edge)) == tree_node_old) {
            SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
            TREE_NODE(GRAPH_EDGE(edge)) = tree_node_new;
         }
         CURR_SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
         SUBGR_EDGE_ROTATION(v1) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(v1),subgr_edge);
         POS_IN_SUBGR_SOURCE_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(v1)->pre; 
         if (equal_edge(edge,edge2))
            triangle2->edge2 = subgr_edge;
         else {
            subgr_edges = add_sedge_to_slist(subgr_edges,edge);
            rotation_elements = add_slist_to_slist(rotation_elements,l);
         }
      end_for_slist (SUBGR_POS(edge2,v),l);
      READY(v) = TRUE;

      for_slist (rotation_elements,l) /* Elemente der Rotation entfernen */
         elem = attr_slist(l);
         SUBGR_EDGE_ROTATION(v) = subtract_immediately_from_slist(SUBGR_EDGE_ROTATION(v),elem);
      end_for_slist (rotation_elements,l);
      free_slist (rotation_elements);
      rotation_elements = empty_slist;

      for_slist (SUBGR_POS(edge3,w),l) /* Adjazenzliste von w aufteilen */
         edge = attr_sedge(l);
         if (equal_edge(edge,edge2)) {
            SUBGR_EDGE_ROTATION(w1) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(w1),triangle2->edge2);
            POS_IN_SUBGR_TARGET_ROT(triangle2->edge2) = SUBGR_EDGE_ROTATION(w1)->pre;
            break;
         }
         node = TARGET_NODE(edge,w);
         if (!SEEN(node)) {
            SEEN(node) = TRUE;
            nodelist = add_snode_to_slist(nodelist,node);
            subgr_nodes = add_snode_to_slist(subgr_nodes,node);
            node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
            node_info->edge_rotation = empty_slist;
            node_info->seen = node_info->ready = FALSE;
            subgr_node = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
            subgr_node->iso = node->iso;
            node->iso->iso = subgr_node;
         }
         edge_info = (Subgraph_Edge_Info) malloc(sizeof(struct subgraph_edge_info));
         edge_info->graph_edge = GRAPH_EDGE(edge);
         edge_info->source = w1;
         subgr_edge = make_edge (w1,node->iso->iso,make_attr(ATTR_DATA,(char*)edge_info));
         if (TREE_NODE(GRAPH_EDGE(edge)) == tree_node_old) {
            SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
            TREE_NODE(GRAPH_EDGE(edge)) = tree_node_new;
         }
         CURR_SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
         SUBGR_EDGE_ROTATION(w1) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(w1),subgr_edge);
         POS_IN_SUBGR_SOURCE_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(w1)->pre; 
         if (equal_edge(edge,edge3)) {
            triangle2->edge3 = subgr_edge;
            SUBGR_EDGE_ROTATION(u1) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(u1),subgr_edge);
            POS_IN_SUBGR_TARGET_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(u1)->pre; 
         } else {
            subgr_edges = add_sedge_to_slist(subgr_edges,edge);
            rotation_elements = add_slist_to_slist(rotation_elements,l);
         }
      end_for_slist (SUBGR_POS(edge3,w),l);
      READY(w) = TRUE;

      for_slist (rotation_elements,l) /* Elemente der Rotation entfernen */
         elem = attr_slist(l);
         SUBGR_EDGE_ROTATION(w) = subtract_immediately_from_slist(SUBGR_EDGE_ROTATION(w),elem);
      end_for_slist (rotation_elements,l);
      free_slist (rotation_elements);
      rotation_elements = empty_slist;

      while (nodelist != empty_slist) { /* alle anderen Knoten des */
                                        /* Subgraphen finden       */
         node = attr_snode(nodelist);
         nodelist = subtract_first_element_from_slist(nodelist);
         subgr_node = node->iso->iso;
         for_slist (SUBGR_EDGE_ROTATION(node),l)
            edge = attr_sedge(l);
            neighbor = TARGET_NODE(edge,node);
            if (!SEEN(neighbor)) {
               SEEN(neighbor) = TRUE;
               nodelist = add_snode_to_slist(nodelist,neighbor);
               subgr_nodes = add_snode_to_slist(subgr_nodes,neighbor);
               node_info = (Subgraph_Node_Info) malloc(sizeof(struct subgraph_node_info));
               node_info->edge_rotation = empty_slist;
               node_info->seen = node_info->ready = FALSE;
               subgr_neighbor = make_node (subgraph,make_attr(ATTR_DATA,(char*)node_info));
               subgr_neighbor->iso = neighbor->iso;
               neighbor->iso->iso = subgr_neighbor;
            }
            if (!READY(neighbor)) {
               subgr_edges = add_sedge_to_slist(subgr_edges,edge);
               edge_info = (Subgraph_Edge_Info) malloc(sizeof(struct subgraph_edge_info));
               edge_info->graph_edge = GRAPH_EDGE(edge);
               edge_info->source = subgr_node;
               subgr_edge = make_edge (subgr_node,neighbor->iso->iso,make_attr(ATTR_DATA,(char*)edge_info));
               if (TREE_NODE(GRAPH_EDGE(edge)) == tree_node_old) {
                  SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
                  TREE_NODE(GRAPH_EDGE(edge)) = tree_node_new;
               }
               CURR_SUBGR_EDGE(GRAPH_EDGE(edge)) = subgr_edge;
            }
            subgr_edge = CURR_SUBGR_EDGE(GRAPH_EDGE(edge));
            SUBGR_EDGE_ROTATION(subgr_node) = add_sedge_to_slist(SUBGR_EDGE_ROTATION(subgr_node),subgr_edge);
            iif (subgr_node == SUBGR_EDGE_SOURCE(subgr_edge),
                 POS_IN_SUBGR_SOURCE_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(subgr_node)->pre,
                 POS_IN_SUBGR_TARGET_ROT(subgr_edge) = SUBGR_EDGE_ROTATION(subgr_node)->pre);
         end_for_slist (SUBGR_EDGE_ROTATION(node),l);
         READY(node) = TRUE;
      }

      removed_edges = empty_slist;
      for_sourcelist (tree_node_old,tree_edge) /* Kanten des Baumes aendern */
         triangle3 = iif (tree_node_old == TREE_EDGE_SOURCE(tree_edge),
                          TRIANGLE_IN_SOURCE(tree_edge),
                          TRIANGLE_IN_TARGET(tree_edge));
         if (SEEN(triangle3->u) && SEEN(triangle3->v) && SEEN(triangle3->w)) {
                                                      /* Baumkante muss geaendert werden */
            removed_edges = add_sedge_to_slist(removed_edges,tree_edge);
            triangle3->u = triangle3->u->iso->iso;
            triangle3->v = triangle3->v->iso->iso;
            triangle3->w = triangle3->w->iso->iso;
            triangle3->edge1 = CURR_SUBGR_EDGE(GRAPH_EDGE(triangle3->edge1));
            triangle3->edge2 = CURR_SUBGR_EDGE(GRAPH_EDGE(triangle3->edge2));
            triangle3->edge3 = CURR_SUBGR_EDGE(GRAPH_EDGE(triangle3->edge3));
            tree_edge_info = (Tree_Edge_Info) malloc(sizeof(struct tree_edge_info));
            tree_edge_info->source = tree_node_new;
            tree_edge_info->triangle_in_source = triangle3;
            tree_edge_info->triangle_in_target = 
                iif (tree_node_old == TREE_EDGE_SOURCE(tree_edge),
                     TRIANGLE_IN_TARGET(tree_edge),
                     TRIANGLE_IN_SOURCE(tree_edge));
            make_edge (tree_node_new,TARGET_NODE(tree_edge,tree_node_old),
                       make_attr(ATTR_DATA,(char*)tree_edge_info));
         }
      end_for_sourcelist (tree_node_old,tree_edge);

      for_slist (removed_edges,l) /* Baumkanten loeschen */
         remove_edge (attr_sedge(l));
      end_for_slist (removed_edges,l);
      free_slist (removed_edges);

      tree_edge_info = (Tree_Edge_Info) malloc(sizeof(struct tree_edge_info));
      tree_edge_info->source = tree_node_old;
      tree_edge_info->triangle_in_source = triangle1;
      tree_edge_info->triangle_in_target = triangle2;
      make_edge (tree_node_old,tree_node_new,make_attr(ATTR_DATA,(char*)tree_edge_info));

      /**** Bestimmung des Subgraphen, in dem alle separierenden Dreiecke sind, ****/
      /**** die eine Kante des gegenwaertigen separierenden Dreiecks enthalten  ****/

      if (((triangle->u == SOURCE(triangle->edge1)) && (triangle == TRIANGLE(SORTED_TRIANGLES(triangle->edge1)))) ||
          ((triangle->u != SOURCE(triangle->edge1)) && (triangle == TRIANGLE(SORTED_TRIANGLES(triangle->edge1)->pre)))) {
         SUBGR_EDGE(triangle->edge1) = triangle1->edge1;
         TREE_NODE(triangle->edge1) = tree_node_old;
      }

      if (((triangle->v == SOURCE(triangle->edge2)) && (triangle == TRIANGLE(SORTED_TRIANGLES(triangle->edge2)))) ||
          ((triangle->v != SOURCE(triangle->edge2)) && (triangle == TRIANGLE(SORTED_TRIANGLES(triangle->edge2)->pre)))) {
         SUBGR_EDGE(triangle->edge2) = triangle1->edge2;
         TREE_NODE(triangle->edge2) = tree_node_old;
      }

      if (((triangle->w == SOURCE(triangle->edge3)) && (triangle == TRIANGLE(SORTED_TRIANGLES(triangle->edge3)))) ||
          ((triangle->w != SOURCE(triangle->edge3)) && (triangle == TRIANGLE(SORTED_TRIANGLES(triangle->edge3)->pre)))) {
         SUBGR_EDGE(triangle->edge3) = triangle1->edge3;
         TREE_NODE(triangle->edge3) = tree_node_old;
      }


      /**** Loeschen von nicht mehr benoetigten Attributen ****/

      for_slist (subgr_edges,l)
         edge = attr_sedge(l);
         if (!equal_edge(edge,edge1) && !equal_edge(edge,edge2) && !equal_edge(edge,edge3)) {
            free (SUBGRAPH_EDGE_INFO(edge));
            remove_edge (edge);
         }
      end_for_slist (subgr_edges,l);
      free_slist (subgr_edges);

      for_slist (subgr_nodes,l)
         node = attr_snode(l);
         SEEN(node) = READY(node) = FALSE;
         if ((node != u) && (node != v) && (node != w)) {
            free_slist (SUBGR_EDGE_ROTATION(node));
            free (SUBGRAPH_NODE_INFO(node));
            remove_node (node);
         }
      end_for_slist (subgr_nodes,l);
      free_slist (subgr_nodes);

      triangle_sublist = refresh_triangle_sublist (triangle_sublist,triangle);
   }

   return tree;
}



Local void free_memory (Slist separating_triangles, Sgraph graph) /* gibt intern benoetigten */
                                                    /* Speicher wieder frei    */
             
{
   Slist l;
   Snode node;   
   Sedge edge;

   for_slist (separating_triangles,l)
      free (TRIANGLE(l));
   end_for_slist (separating_triangles,l);
   free_slist (separating_triangles);

   for_all_nodes (graph,node)
      free_slist (EDGE_ROTATION(node));
      free (INTERNAL_NODE_INFO(node));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            free_slist (TRIANGLES(edge));
            free_slist (SORTED_TRIANGLES(edge));
            free (INTERNAL_EDGE_INFO(edge));
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);
}



Local void make_external_component_attrs (Sgraph tree) /* erzeugt extern sichtbare  */
                                                /* Attribute der Komponenten */
{
   Snode  tree_node,node;
   Sgraph component;
   Slist  rotation;
   Sedge  edge,graph_edge;

   for_all_nodes (tree,tree_node)
      component = attr_sgraph(tree_node);
      for_all_nodes (component,node)
         rotation = SUBGR_EDGE_ROTATION(node);
         free (SUBGRAPH_NODE_INFO(node));
         set_nodeattrs (node,make_attr_slist(rotation));
         for_sourcelist (node,edge)
            if (unique_edge(edge)) {
               graph_edge = GRAPH_EDGE(edge);
               set_edgeattrs (edge,make_attr_sedge(graph_edge));
            }
         end_for_sourcelist (node,edge);
      end_for_all_nodes (component,node);
   end_for_all_nodes (tree,tree_node);
}



Tree_Result construct_4_block_tree (Sgraph graph, Sedge st_edge) /* Hauptfunktion fuer die         */
                                                   /* Konstruktion des 4-block trees */
               
{
   Slist       separating_triangles,triangle_sublist;
   Sgraph      tree;
   Snode       root;
   Tree_Result tree_result;

   make_internal_attrs (graph);

   separating_triangles = compute_separating_triangles (graph);

   make_triangle_lists (separating_triangles);

   sort_triangles (graph);

   triangle_sublist = make_triangle_sublist (separating_triangles);

   tree = construct_tree (graph,triangle_sublist);

   root = TREE_NODE(st_edge);

   free_memory (separating_triangles,graph);

   make_external_component_attrs (tree);

   tree_result = (Tree_Result) malloc(sizeof(struct tree_result));
   tree_result->tree = tree;
   tree_result->root = root;

   return tree_result;
}



void remove_4_block_tree (Sgraph tree, Sgraph graph) /* loescht 4-block tree von graph */
                  
{
   Snode node;
   Sedge edge;

   for_all_nodes (tree,node)
      remove_graph (attr_sgraph(node));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            free (TRIANGLE_IN_SOURCE(edge));
            free (TRIANGLE_IN_TARGET(edge));
            free (TREE_EDGE_INFO(edge));
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (tree,node);

   for_all_nodes (graph,node)
      node->iso = empty_snode;
   end_for_all_nodes (graph,node);

   remove_graph (tree);
}



/*************************************************************************/
/*                                                                       */
/*                     END OF FILE: 4_BLOCK_TREE.C                       */
/*                                                                       */
/*************************************************************************/
