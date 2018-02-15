/*************************************************************************/
/*                                                                       */
/* FILE: NUMMENMAA_VISIBILITY.C                                          */
/*                                                                       */
/* Beschreibung: Funktionen fuer den Algorithmus von Nummenmaa           */
/*                                                                       */
/* benoetigte externe Funktionen: number_of_edges                        */
/*                                visibility_layout2_triangulate                            */
/*                                canonical_ordering                     */
/*                                save_nodeattrs                         */
/*                                reset_nodeattrs                        */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <algorithms.h>
#include <limits.h>
#include "visibility_layout2_export.h"
#include "visibility_definitions.h"
#include "Nummenmaa_visibility.h"

extern int number_of_nodes (Sgraph graph); /* springembedder_rf/spring.c */

typedef struct internal_node_info {
           Slist edge_rotation; /* Reihenfolge der Kanten in der Einbettung */
           Slist targetlist; /* Kanten zu Vorgaengern, die zwischen wp und wq liegen */
           int   indegree;
           int   outdegree;
           Snode wp; /* linker Vorgaenger */
           Snode wq; /* rechter Vorgaenger */
           int   level;
           int   a; /* Mindestlaenge */
           int   b; /* widening effect */
           int   p; /* Ueberlappung mit wp */
           int   q; /* Ueberlappung vit wq */
           int   xl; /* linker Endpunkt des Knotensegments */
           int   xr; /* rechter Endpunkt des Knotensegments */
           int   xl_real; /* kleinste x-Koordinate aller inzidenten Kantensegmente */
           int   xr_real; /* groesste x-Koordinate aller inzidenten Kantensegmente */
           int   vl;
           int   vr;
} *Internal_Node_Info;

typedef struct internal_edge_info {
           int  x; /* x-Koordinate */
           bool is_dummy; /* Dummy-Kante? */
} *Internal_Edge_Info;


#define INTERNAL_NODE_INFO(node) (attr_data_of_type((node),Internal_Node_Info))
#define EDGE_ROTATION(node) (INTERNAL_NODE_INFO(node)->edge_rotation)
#define TARGETLIST(node) (INTERNAL_NODE_INFO(node)->targetlist)
#define INDEGREE(node) (INTERNAL_NODE_INFO(node)->indegree)
#define OUTDEGREE(node) (INTERNAL_NODE_INFO(node)->outdegree)
#define WP(node) (INTERNAL_NODE_INFO(node)->wp)
#define WQ(node) (INTERNAL_NODE_INFO(node)->wq)
#define LEVEL(node) (INTERNAL_NODE_INFO(node)->level)
#define A(node) (INTERNAL_NODE_INFO(node)->a)
#define B(node) (INTERNAL_NODE_INFO(node)->b)
#define P(node) (INTERNAL_NODE_INFO(node)->p)
#define Q(node) (INTERNAL_NODE_INFO(node)->q)
#define XL(node) (INTERNAL_NODE_INFO(node)->xl)
#define XR(node) (INTERNAL_NODE_INFO(node)->xr)
#define XL_REAL(node) (INTERNAL_NODE_INFO(node)->xl_real)
#define XR_REAL(node) (INTERNAL_NODE_INFO(node)->xr_real)
#define VL(node) (INTERNAL_NODE_INFO(node)->vl)
#define VR(node) (INTERNAL_NODE_INFO(node)->vr)
#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define EDGE_X(edge) (INTERNAL_EDGE_INFO(edge)->x)
#define IS_DUMMY_EDGE(edge) (INTERNAL_EDGE_INFO(edge)->is_dummy)
#define COMPRESSION_NODE_INFO(node) (attr_data_of_type((node),Compression_Node_Info))
#define COMPRESSION_EDGE_INFO(edge) (attr_data_of_type((edge),Compression_Edge_Info))



Local void init_values (Slist nodelist) /* initialisiert Attribute */
               
{
   Snode u,v,w,node,node1,node2;
   Sedge edge,edge1,edge2;
   Slist l1,l2,help_pointer;
   Slist wp_pointer,wq_pointer; /* Position von wp bzw. wq in der Rotation */

   u = attr_snode(nodelist);
   v = attr_snode(nodelist->suc);
   w = attr_snode(nodelist->pre);

   for_sourcelist (u,edge)
      if (!IS_DUMMY_EDGE(edge))
         OUTDEGREE(u)++;
   end_for_sourcelist (u,edge);
   A(u) = B(u) = OUTDEGREE(u);

   for_slist (nodelist,l1)
      node = attr_snode(l1);
      if (node != u) {
         /* Bestimmung der 1. eingehenden Kante */
         if (node != w) {
            for_slist (EDGE_ROTATION(node),l2)
               edge1 = attr_sedge(l2);
               edge2 = attr_sedge(l2->pre);
               if ((TARGET_NODE(edge1,node)->nr < node->nr) &&
                   (TARGET_NODE(edge2,node)->nr > node->nr)) {
                  help_pointer = l2;
                  break;
               }
            end_for_slist (EDGE_ROTATION(node),l2);
         } else {
            for_slist (EDGE_ROTATION(node),l2)
               edge = attr_sedge(l2);
               if (TARGET_NODE(edge,node) == v) {
                  help_pointer = l2;
                  break;
               }
            end_for_slist (EDGE_ROTATION(node),l2);
         }
         EDGE_ROTATION(node) = help_pointer; /* zeigt zu 1. eingehender Kante */
         edge = attr_sedge(EDGE_ROTATION(node));
         if (!IS_DUMMY_EDGE(edge)) {
            WQ(node) = TARGET_NODE(edge,node);
            wq_pointer = EDGE_ROTATION(node);
         }
         for_slist (EDGE_ROTATION(node),l2)
            edge = attr_sedge(l2);
            node1 = TARGET_NODE(edge,node);
            if (node1->nr < node->nr) { /* eingehende Kante */
               if (!IS_DUMMY_EDGE(edge)) {
                  WP(node) = node1;
                  wp_pointer = l2;
                  if (WQ(node) == empty_snode) { /* wq noch nicht gefunden */
                     WQ(node) = node1;
                     wq_pointer = l2;
                  }
               }
            } else if (!IS_DUMMY_EDGE(edge)) /* ausgehende Nicht-Dummy-Kante */
               OUTDEGREE(node)++;
         end_for_slist (EDGE_ROTATION(node),l2);
         if (WP(node) == empty_snode) { /* alle Kanten zu Vorgaengern sind Dummies */
            if (node != v) {
               for_slist (EDGE_ROTATION(node),l2)
                  edge1 = attr_sedge(l2);
                  edge2 = attr_sedge(l2->suc);
                  node1 = TARGET_NODE(edge1,node);
                  node2 = TARGET_NODE(edge2,node);
                  if (node2->nr > node->nr) break;
                  if ((WP(node) == empty_snode) ||
                      (B(node1) + B(node2) < B(WP(node)) + B(WQ(node)))) {
                     WQ(node) = node1;
                     wq_pointer = l2;
                     WP(node) = node2;
                  }
               end_for_slist (EDGE_ROTATION(node),l2);
               OUTDEGREE(WP(node))++;
               A(WP(node)) = maximum (INDEGREE(WP(node)),OUTDEGREE(WP(node)));
               B(WP(node))++;
               OUTDEGREE(WQ(node))++;
               A(WQ(node)) = maximum (INDEGREE(WQ(node)),OUTDEGREE(WQ(node)));
               B(WQ(node))++;
            } else {
               WP(node) = WQ(node) = u;
               OUTDEGREE(u)++;
               A(u)++;
               B(u)++;
            }
         }
         if ((node != v) && (WP(node) == WQ(node))) { /* nur eine eingehende   */
            edge = attr_sedge(wp_pointer);            /* Nicht-Dummy-Kante     */
            edge1 = attr_sedge(wp_pointer->suc);
            edge2 = attr_sedge(wp_pointer->pre);
            node1 = TARGET_NODE(edge1,node);
            node2 = TARGET_NODE(edge2,node);
            if (((node == w) && (WP(node) == v)) ||
                ((node != w) && (node2->nr > node->nr))) {
               WP(node) = node1;
               OUTDEGREE(WP(node))++;
               A(WP(node)) = maximum (INDEGREE(WP(node)),OUTDEGREE(WP(node)));
               B(WP(node))++;
            } else {
               WQ(node) = node2;
               wq_pointer = wp_pointer->pre;
               OUTDEGREE(WQ(node))++;
               A(WQ(node)) = maximum (INDEGREE(WQ(node)),OUTDEGREE(WQ(node)));
               B(WQ(node))++;
            }
         }
         /* Bestimmung von TARGETLIST(node) und INDEGREE(node) */
         if (node != v) {
            EDGE_ROTATION(node) = wq_pointer;
            for_slist (EDGE_ROTATION(node),l2)
               edge = attr_sedge(l2);
               node1 = TARGET_NODE(edge,node);
               TARGETLIST(node) = add_sedge_to_slist(TARGETLIST(node),edge);
               if ((node1 == WP(node)) || (node1 == WQ(node)) || !IS_DUMMY_EDGE(edge))
                  INDEGREE(node)++;
               if (node1 == WP(node))
                  break;
            end_for_slist (EDGE_ROTATION(node),l2);
         } else {
            for_slist (EDGE_ROTATION(node),l2)
               edge = attr_sedge(l2);
               if (TARGET_NODE(edge,node) == u) {
                  TARGETLIST(node) = add_sedge_to_slist(TARGETLIST(node),edge);
                  break;
               }
            end_for_slist (EDGE_ROTATION(node),l2);
            INDEGREE(node) = 1;
         }
         /* Berechnung von A(node) und B(node) */
         A(node) = maximum (INDEGREE(node),OUTDEGREE(node));
         B(node) = OUTDEGREE(node) - INDEGREE(node);
      }
   end_for_slist (nodelist,l1);
}



Local void make_internal_attrs (Slist nodelist, Slist dummy_edges) /* erzeugt intern benoe- */
                                                      /* tigte Attribute       */
                  
{
   Slist              l;
   Snode              node;
   Sedge              edge;
   Internal_Node_Info node_info;
   Internal_Edge_Info edge_info;

   for_slist (nodelist,l)
      node = attr_snode(l);
      node_info = (Internal_Node_Info) malloc(sizeof(struct internal_node_info));
      node_info->edge_rotation = attr_slist(node);
      node_info->targetlist = empty_slist;
      node_info->indegree = node_info->outdegree = 0;
      node_info->wp = node_info->wq = empty_snode;
      node_info->level = node_info->p = node_info->q = 0;
      node_info->xl_real = INT_MAX;
      node_info->xr_real = INT_MIN;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->is_dummy = FALSE;
            set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
         }
      end_for_sourcelist (node,edge);
   end_for_slist (nodelist,l);

   for_slist (dummy_edges,l)
      edge = attr_sedge(l);
      IS_DUMMY_EDGE(edge) = TRUE;
   end_for_slist (dummy_edges,l);

   init_values (nodelist);
}



Local void process_widening_effect (Snode node, Snode u) /* bearbeitet B(node) */
             
{
   Slist l;
   Sedge edge;
   Snode w,wq,wp;

   if (B(node) > 0) {
      for_slist (TARGETLIST(node),l)
         if (B(node) == 0)
            break;
         edge = attr_sedge(l);
         w = TARGET_NODE(edge,node);
         if ((w != WP(node)) && (w != WQ(node))) {
            if (-B(w) >= B(node)) {
               B(w) = B(w) + B(node);
               B(node) = 0;
            } else if (B(w) < 0) {
               B(node) = B(node) + B(w);
               B(w) = 0;
            }
         }
      end_for_slist (TARGETLIST(node),l);
   }

   if (B(node) > 0) {
      wq = WQ(node);
      if (-B(wq) >= B(node)) {
         B(wq) = B(wq) + B(node);
         Q(node) = B(node);
         B(node) = 0;
      } else if (B(wq) < 0) {
         B(node) = B(node) + B(wq);
         Q(node) = -B(wq);
         B(wq) = 0;
      }
   }

   if (B(node) > 0) {
      wp = WP(node);
      if (wp == u) {
         A(wp) = A(wp) + B(node);
         P(node) = B(node);
         B(node) = 0;
      } else if (-B(wp) >= B(node)) {
         B(wp) = B(wp) + B(node);
         P(node) = B(node);
         B(node) = 0;
      } else {
         P(node) = B(node);
         if (B(wp) < 0) {
            B(node) = B(node) + B(wp);
            B(wp) = 0;
         }
         A(wp) = A(wp) + B(node);
         B(wp) = B(wp) + B(node);
         B(node) = 0;
      }
   }
}



Local void compute_levels (Slist nodelist) /* bestimmt die Levels der Knoten */
               
{
   Slist l;
   Snode node,target;
   Sedge edge;

   for_slist (nodelist,l)
      node = attr_snode(l);
      for_sourcelist (node,edge)
         target = TARGET_NODE(edge,node);
         if (target->nr > node->nr) /* ausgehende Kante */
            LEVEL(target) = maximum (LEVEL(target),LEVEL(node)+1);
      end_for_sourcelist (node,edge);
   end_for_slist (nodelist,l);
}



Local void compute_node_segment (Snode node, Snode u, Snode v, Snode w) /* konstruiert Segment von node */
            
             
{
   int node_width;

   if (node == u) {
      XL(node) = 1;
      XR(node) = A(node);
      VL(node) = 1;
      VR(node) = A(node) - 1;
   } else if (node == v) {
      XL(node) = A(u);
      XR(node) = A(u) + A(node) - 1;
      VL(node) = XL(node);
      VR(node) = XR(node);
   } else {
      XL(node) = VR(WP(node)) - P(node);
      XR(node) = VL(WQ(node)) + Q(node);
      VL(node) = XL(node);
      VR(node) = XR(node);
   }

   node->x = (XL(node) + XR(node)) * visibility_layout2_settings.horizontal_distance / 2;
   node->y = (LEVEL(w) - LEVEL(node)) * visibility_layout2_settings.vertical_distance;
   node_width = iif (XL(node) == XR(node),
                     visibility_layout2_settings.height,(XR(node) - XL(node)) * visibility_layout2_settings.horizontal_distance + 1);
   node_set (graphed_node(node),
             ONLY_SET,
             NODE_SIZE, node_width,visibility_layout2_settings.height,
             NODE_NEI, SPECIAL_NODE_EDGE_INTERFACE,
             NODE_TYPE, find_nodetype ("#box"),
             0);
}



Local void compute_edge_segment (Sedge edge, Snode node, Snode u, Snode v) /* konstruiert Segment von edge */
           
               
{   
   Snode    source,target,node1;
   Edgeline edgeline;

   edgeline = (Edgeline)edge_get (graphed_edge(edge),EDGE_LINE);
   free_edgeline (edgeline);

   source = sedge_real_source(edge);
   target = sedge_real_target(edge);

   node1 = TARGET_NODE(edge,node);

   if (node == v)
      EDGE_X(edge) = A(u);
   else
      EDGE_X(edge) = iif (node1 == WP(node),VR(node1),VL(node1));

   if (node == source) {
      edgeline = new_edgeline (EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,node->y + visibility_layout2_settings.height/2);
      edgeline = add_to_edgeline (edgeline,EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,node1->y - visibility_layout2_settings.height/2);
   } else {
      edgeline = new_edgeline (EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,node1->y - visibility_layout2_settings.height/2);
      edgeline = add_to_edgeline (edgeline,EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,node->y + visibility_layout2_settings.height/2);
   }
   edgeline = add_to_edgeline (edgeline,target->x,target->y);
   edgeline = add_to_edgeline (edgeline,source->x,source->y);
   edge_set (graphed_edge(edge),
             RESTORE_IT,
             EDGE_LINE,edgeline,
             0);

   XL_REAL(node) = minimum (XL_REAL(node),EDGE_X(edge));
   XR_REAL(node) = maximum (XR_REAL(node),EDGE_X(edge));
   XL_REAL(node1) = minimum (XL_REAL(node1),EDGE_X(edge));
   XR_REAL(node1) = maximum (XR_REAL(node1),EDGE_X(edge));
}



Local void construct_representation (Slist nodelist) /* eigentliche Konstruktion */
                                               /* der Darstellung          */
{
   Snode u,v,w;
   Slist l1,l2;
   Snode node;
   Sedge edge;

   u = attr_snode(nodelist);
   v = attr_snode(nodelist->suc);
   w = attr_snode(nodelist->pre);

   compute_levels (nodelist);

   for_slist (nodelist,l1)
      node = attr_snode(l1);
      compute_node_segment (node,u,v,w);
      for_slist (TARGETLIST(node),l2)
         edge = attr_sedge(l2);
         if (!IS_DUMMY_EDGE(edge))
            compute_edge_segment (edge,node,u,v);
      end_for_slist (TARGETLIST(node),l2);
      if ((node != u) && (node != v)) {
         VR(WP(node)) = XL(node) - 1;
         VL(WQ(node)) = XR(node) + 1;
      }
   end_for_slist (nodelist,l1);
}



Local void shorten_node_segment (Snode node) /* verkuerzt Knotensegment  */
                                       /* von node soweit moeglich */
{
   int node_width;

   if (XL(node) != XL_REAL(node)) /* Verkuerzung moeglich */
      XL(node) = XL_REAL(node);

   if (XR(node) != XR_REAL(node)) /* Verkuerzung moeglich */
      XR(node) = XR_REAL(node);

   node->x = (XL(node) + XR(node)) * visibility_layout2_settings.horizontal_distance / 2;
   node_width = iif (XL(node) == XR(node),
                     visibility_layout2_settings.height,(XR(node) - XL(node)) * visibility_layout2_settings.horizontal_distance + 1);
   node_set (graphed_node(node),
             ONLY_SET,
             NODE_SIZE, node_width,visibility_layout2_settings.height,
             0);
}



Local void compute_size_of_drawing (Sgraph graph) /* berechnet Groesse der Darstellung */
             
{
   int   left,right,down,up,width,height,area;
   Snode node;

   left = down = INT_MAX;
   right = up = INT_MIN;

   for_all_nodes (graph,node)
      down = minimum (down,LEVEL(node));
      up = maximum (up,LEVEL(node));
      left = minimum (left,XL(node));
      right = maximum (right,XR(node));
   end_for_all_nodes (graph,node);

   width = right - left + 1;
   height = up - down + 1;
   area = width * height;

   message ("Width of drawing: %d\n",width);
   message ("Height of drawing: %d\n",height);
   message ("Required area: %d\n\n",area);
}



void Nummenmaa_w_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge) /* Hauptfunktion fuer den    */
                                                   /* Algorithmus von Nummenmaa */
           
                  
{
   Slist                 dummy_edges = empty_slist;
   Snode                 u,v,w;
   Slist                 l;
   Sedge                 edge;
   Slist                 nodelist;
   Snode                 node;
   int                   max; /* maximales Level */
   Sgraph                save_graph;
   Compression_Node_Info node_info;
   Compression_Edge_Info edge_info;

   if (number_of_edges(graph) < 3 * number_of_nodes(graph) - 6) /* graph nicht trianguliert */
      dummy_edges = visibility_layout2_triangulate (graph);
   else embed (graph);

   if (dummy_edge != empty_sedge)
      dummy_edges = add_sedge_to_slist(dummy_edges,dummy_edge);

   if (visibility_layout2_settings.nummen_nodes == u_and_w) { /* uebergebene Knoten als u und w verwenden */
      u = s;
      w = t;
      for_slist (attr_slist(u),l)
         edge = attr_sedge(l);
         node = TARGET_NODE(edge,u);
         if (node == w) {
            edge = attr_sedge(l->pre);
            v = TARGET_NODE(edge,u);
            break;
         }
      end_for_slist (attr_slist(u),l);
   } else { /* uebergebene Knoten als u und v verwenden */
      u = s;
      v = t;
      for_slist (attr_slist(u),l)
         edge = attr_sedge(l);
         node = TARGET_NODE(edge,u);
         if (node == v) {
            edge = attr_sedge(l->suc);
            w = TARGET_NODE(edge,u);
            break;
         }
      end_for_slist (attr_slist(u),l);
   }

   nodelist = canonical_ordering (graph,u,v,w); /* kanonische Nummerierung */

   make_internal_attrs (nodelist,dummy_edges);

   nodelist = nodelist->pre;
   while ((node = attr_snode(nodelist)) != v) { /* Bearbeitung der b-Werte */
      process_widening_effect (node,u);
      nodelist = nodelist->pre;
   }
   nodelist = nodelist->pre;

   construct_representation (nodelist);

   free_slist (nodelist);

   for_all_nodes (graph,node)
      if ((XL(node) != XL_REAL(node)) || (XR(node) != XR_REAL(node)))
         shorten_node_segment (node); /* Verkuerzen der Knotensegmente */
   end_for_all_nodes (graph,node);

   compression_used = FALSE; /* Compression angewendet? */

   if ((dummy_edges != empty_slist) && visibility_layout2_settings.compression) { /* Anwendung von Compression */
      compression_used = TRUE; /* Compression wird angewendet */
      max = LEVEL(w);
      save_graph = save_nodeattrs (graph); /* Zwischenspeicherung der */
                                           /* Knotenattribute         */
      for_all_nodes (graph,node) /* Erzeugung der Attribute fuer Compression */
         node_info = (Compression_Node_Info) malloc(sizeof(struct compression_node_info));
         node_info->level = LEVEL(node->iso);
         node_info->xl = XL(node->iso);
         node_info->xr = XR(node->iso);
         set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
         for_sourcelist (node,edge)
            if (unique_edge(edge)) {
               edge_info = (Compression_Edge_Info) malloc(sizeof(struct compression_edge_info));
               edge_info->x = EDGE_X(edge);
               edge_info->is_dummy = IS_DUMMY_EDGE(edge);
               free (INTERNAL_EDGE_INFO(edge));
               set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
            }
         end_for_sourcelist (node,edge);
      end_for_all_nodes (graph,node);
      visibility_compression (graph,max,Numm_weak); /* Compression */
      for_all_nodes (graph,node)  /* Werte modifizieren */
         XL(node->iso) = COMPRESSION_NODE_INFO(node)->xl;
         XR(node->iso) = COMPRESSION_NODE_INFO(node)->xr;
         LEVEL(node->iso) = COMPRESSION_NODE_INFO(node)->level;
         free (COMPRESSION_NODE_INFO(node));
         for_sourcelist (node,edge)
            if (unique_edge(edge)) free (COMPRESSION_EDGE_INFO(edge));
         end_for_sourcelist (node,edge);
      end_for_all_nodes (graph,node);
      reset_nodeattrs (graph,save_graph); /* setzt Knotenattribute wieder ein */
   }

   compute_size_of_drawing (graph); /* Groesse der Darstellung */

   for_all_nodes (graph,node)  /* Speicherfreigabe der internen Attribute */
      free_slist (EDGE_ROTATION(node));
      free_slist (TARGETLIST(node));
      free (INTERNAL_NODE_INFO(node));
      if (!compression_used) /*Compression wurde nicht angewendet */
         for_sourcelist (node,edge)
            if (unique_edge(edge)) free (INTERNAL_EDGE_INFO(edge));
         end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   for_slist (dummy_edges,l)  /* Loeschen der Dummy-Kanten */
      edge = attr_sedge(l);
      remove_edge (edge);
   end_for_slist (dummy_edges,l);
   free_slist (dummy_edges);
}



/*************************************************************************/
/*                                                                       */
/*                  END OF FILE: NUMMENMAA_VISIBILITY.C                  */
/*                                                                       */
/*************************************************************************/
