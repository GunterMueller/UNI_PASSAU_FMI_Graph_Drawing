/*************************************************************************/
/*                                                                       */
/* FILE: TT_WEAK.C                                                       */
/*                                                                       */
/* Beschreibung: enthaelt die eigentliche Konstruktion der Knoten- und   */
/*               Kantensegmente bei den w-visibility-Algorithmen von     */
/*               Rosenstiehl/Tarjan, Tamassia/Tollis und Kant; vorher    */
/*               berechnet wurden die Laengen der laengsten Wege im      */
/*               gerichteten Graphen und im dualen Graphen.              */
/*                                                                       */
/* benoetigte externe Funktionen: save_nodeattrs                         */
/*                                reset_nodeattrs                        */
/*                                visibility_compression                 */
/*                                                                       */
/*************************************************************************/




#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <limits.h>
#include "visibility_layout2_export.h"
#include "visibility_definitions.h"
#include "TTvisibility.h"


#define VISIBILITY_NODE_INFO(node) (attr_data_of_type((node),Visibility_Node_Info))
#define NODE_LEVEL(node) (VISIBILITY_NODE_INFO(node)->level)
#define XL(node) (VISIBILITY_NODE_INFO(node)->xl)
#define XR(node) (VISIBILITY_NODE_INFO(node)->xr)
#define IS_DUMMY_NODE(node) (VISIBILITY_NODE_INFO(node)->is_dummy)
#define VISIBILITY_EDGE_INFO(edge) (attr_data_of_type((edge),Visibility_Edge_Info))
#define LEFT_FACE(edge) (VISIBILITY_EDGE_INFO(edge)->left_face)
#define EDGE_X(edge) (VISIBILITY_EDGE_INFO(edge)->x)
#define IS_DUMMY_EDGE(edge) (VISIBILITY_EDGE_INFO(edge)->is_dummy)
#define DUALITY_EDGE_INFO(edge) (attr_data_of_type((edge),Duality_Edge_Info))
#define FACE_LEVEL(face) (attr_int(face))
#define COMPRESSION_NODE_INFO(node) (attr_data_of_type((node),Compression_Node_Info))
#define COMPRESSION_EDGE_INFO(edge) (attr_data_of_type((edge),Compression_Edge_Info))



Local void make_visibility_attrs (Sgraph graph, Snode s, Snode t, Snode dummy_node, Slist dummy_edges, Visibility_Type type)
                                   /* erzeugt und initialisiert Attribute */
                    
                           
                            
                     
{
   Snode                node;
   Sedge                edge;
   Sedge                dummy_edge;
   Slist                l;
   Visibility_Node_Info node_info;
   Visibility_Edge_Info edge_info;

   for_all_nodes (graph,node)
      node_info = (Visibility_Node_Info) malloc(sizeof(struct visibility_node_info));
      node_info->level = attr_int(node);
      node_info->xl = INT_MAX;
      node_info->xr = -1;  /* reicht zur Initialisierung, da kein */
                           /* Knotensegment xr = -1 hat           */
      node_info->is_dummy = FALSE;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Visibility_Edge_Info) malloc(sizeof(struct visibility_edge_info));
            edge_info->left_face = DUALITY_EDGE_INFO(edge)->left_face;
            edge_info->right_face = DUALITY_EDGE_INFO(edge)->right_face;
            if (((edge->snode == s) && (edge->tnode == t)) ||
                ((edge->tnode == s) && (edge->snode == t)))
               edge_info->x = iif (type == RT_weak,FACE_LEVEL(edge_info->left_face),0);
            else
               edge_info->x = iif ((type == RT_weak) || (type == Kant_weak),
                                   FACE_LEVEL(edge_info->left_face),
                                   FACE_LEVEL(edge_info->left_face)+1);
            edge_info->is_dummy = FALSE;
            free (DUALITY_EDGE_INFO(edge)); /* gibt Speicher fuer bisherige */
                                            /* Kantenattribute frei         */
            set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   if (dummy_node != empty_snode)
      IS_DUMMY_NODE(dummy_node) = TRUE;

   for_slist (dummy_edges,l)
      dummy_edge = attr_sedge(l);
      IS_DUMMY_EDGE(dummy_edge) = TRUE;
   end_for_slist (dummy_edges,l);
}



Local void construct_node_segments (Sgraph graph, Snode t, Visibility_Type type) /* konstruiert alle Knotensegmente */
                      
                  
                     
{
   Snode node;
   Sedge edge;

   for_all_nodes (graph,node)
      if (!IS_DUMMY_NODE(node)) {
         for_sourcelist (node,edge)
            if (!IS_DUMMY_EDGE(edge)) {
               XL(node) = minimum (XL(node),EDGE_X(edge));
               XR(node) = maximum (XR(node),EDGE_X(edge));
            }
         end_for_sourcelist (node,edge);

         if (XL(node) == XR(node)) { /* Segment ist Punkt */
            switch (type) {
               case RT_weak: case Kant_weak:
                      node->x = XL(node) * visibility_layout2_settings.horizontal_distance;
                      node_set (graphed_node(node),
                                ONLY_SET,
                                NODE_SIZE, visibility_layout2_settings.height,visibility_layout2_settings.height,
                                0);
                      break;
               case TT_weak: case TT_epsilon:
                      node->x = XL(node) * visibility_layout2_settings.horizontal_distance - visibility_layout2_settings.horizontal_distance / 2;
                      node_set (graphed_node(node),
                                ONLY_SET,
                                NODE_SIZE, visibility_layout2_settings.horizontal_distance + 1,visibility_layout2_settings.height,
                                0);
                      XL(node)--;
                      break;
               default: break;
            }
         } else {
            node->x = (XL(node) + XR(node)) * visibility_layout2_settings.horizontal_distance / 2;
            node_set (graphed_node(node),
                      ONLY_SET,
                      NODE_SIZE, (XR(node) - XL(node)) * visibility_layout2_settings.horizontal_distance + 1,visibility_layout2_settings.height,
                      0);
         }

         node->y = (NODE_LEVEL(t) - NODE_LEVEL(node)) * visibility_layout2_settings.vertical_distance;
         node_set (graphed_node(node),
                   ONLY_SET,
                   NODE_NEI, SPECIAL_NODE_EDGE_INTERFACE,
                   NODE_TYPE, find_nodetype("#box"),
                   0);
      }
   end_for_all_nodes (graph,node);
}
 


Local void construct_edge_segments (Sgraph graph) /* konstruiert alle Kantensegmente */
             
{
   Snode    node;
   Sedge    edge;
   Snode    target_node,source,target;
   Edgeline edgeline;

   for_all_nodes (graph,node)
      if (!IS_DUMMY_NODE(node))
         for_sourcelist (node,edge)
            if (!IS_DUMMY_EDGE(edge)) {
               target_node = TARGET_NODE(edge,node);
               if (target_node->y > node->y) { /* edge liegt unterhalb von node */
                  edgeline = (Edgeline) edge_get(graphed_edge(edge),EDGE_LINE);
                  free_edgeline (edgeline);
                  source = sedge_real_source(edge);
                  target = sedge_real_target(edge);
                  if (node == source) {
                     edgeline = new_edgeline (EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,node->y + visibility_layout2_settings.height/2);
                     edgeline = add_to_edgeline (edgeline,EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,target_node->y - visibility_layout2_settings.height/2);
                  } else {
                     edgeline = new_edgeline (EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,target_node->y - visibility_layout2_settings.height/2);
                     edgeline = add_to_edgeline (edgeline,EDGE_X(edge) * visibility_layout2_settings.horizontal_distance,node->y + visibility_layout2_settings.height/2);
                  }
                  edgeline = add_to_edgeline (edgeline,target->x,target->y);
                  edgeline = add_to_edgeline (edgeline,source->x,source->y);

                  edge_set (graphed_edge(edge),
                            RESTORE_IT,
                            EDGE_LINE,edgeline,
                            0);
               }
            }
         end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);
}



void construct_w_visibility_representation (Sgraph graph, Snode s, Snode t, Snode dummy_node, Slist dummy_edges, int contains_dummies, Visibility_Type type)
                                     /* Hauptfunktion zur Konstruktion einer */
                                     /* w-visibility representation          */
                           
                            
                                 
                     
{
   Sgraph                save_graph;
   Snode                 node;
   Sedge                 edge;
   Compression_Node_Info node_info;
   Compression_Edge_Info edge_info;

   make_visibility_attrs (graph,s,t,dummy_node,dummy_edges,type);

   construct_node_segments (graph,t,type);

   construct_edge_segments (graph);

   compression_used = FALSE; /* Compression angewendet? */

   if (contains_dummies &&
       visibility_layout2_settings.compression && 
       ((type == RT_weak) || (type == Kant_weak) || (type == TT_weak))
       ) {
      compression_used = TRUE; /* Compression wird angewendet */
      save_graph = save_nodeattrs (graph); /* sichert Knotenattribute ab */
      for_all_nodes (graph,node)
         node_info = (Compression_Node_Info) malloc(sizeof(struct compression_node_info));
         node_info->level = NODE_LEVEL(node->iso);
         node_info->xl = XL(node->iso);
         node_info->xr = XR(node->iso);
         set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
         for_sourcelist (node,edge)
            if (unique_edge(edge)) {
               edge_info = (Compression_Edge_Info) malloc(sizeof(struct compression_edge_info));
               edge_info->x = EDGE_X(edge);
               edge_info->is_dummy = IS_DUMMY_EDGE(edge);
               free (VISIBILITY_EDGE_INFO(edge));
               set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
            }
         end_for_sourcelist (node,edge);
      end_for_all_nodes (graph,node);
      visibility_compression (graph,NODE_LEVEL(t),type); /* Aufruf von Compression */
      for_all_nodes (graph,node)
         XL(node->iso) = COMPRESSION_NODE_INFO(node)->xl;
         XR(node->iso) = COMPRESSION_NODE_INFO(node)->xr;
         NODE_LEVEL(node->iso) = COMPRESSION_NODE_INFO(node)->level;
         free (COMPRESSION_NODE_INFO(node));
         for_sourcelist (node,edge)
            if (unique_edge(edge)) free (COMPRESSION_EDGE_INFO(edge));
         end_for_sourcelist (node,edge);
      end_for_all_nodes (graph,node);
      reset_nodeattrs (graph,save_graph); /* setzt Knotenattribute wieder ein */
   }
}



/*************************************************************************/
/*                                                                       */
/*                        END OF FILE: TT_WEAK.C                         */
/*                                                                       */
/*************************************************************************/
