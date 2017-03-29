/***************************************************************************/
/*                                                                         */
/* FILE: COMPRESSION.C                                                     */
/*                                                                         */
/* Beschreibung: enthaelt alle Funktionen fuer das Verfahren Compression;  */
/*               in node->attrs muessen Attribute vom Typ Compression_Node_*/
/*               Info vorhanden sein und in edge->attrs Attribute vom Typ  */ 
/*               Compression_Edge_Info; diese werden auch wieder zurueck-  */
/*               gegeben.                                                  */
/*                                                                         */
/* benoetigte externe Funktionen: keine                                    */
/*                                                                         */
/***************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <limits.h>
#include "visibility_layout2_export.h"
#include "visibility_definitions.h"

extern int number_of_nodes (Sgraph graph); /* springembedder_rf/spring.c */

#define COMPRESSION_NODE_INFO(node) (attr_data_of_type((node),Compression_Node_Info))
#define LEVEL(node) (COMPRESSION_NODE_INFO(node)->level)
#define XL(node) (COMPRESSION_NODE_INFO(node)->xl)
#define XR(node) (COMPRESSION_NODE_INFO(node)->xr)
#define COMPRESSION_EDGE_INFO(edge) (attr_data_of_type((edge),Compression_Edge_Info))
#define EDGE_X(edge) (COMPRESSION_EDGE_INFO(edge)->x)
#define IS_DUMMY_EDGE(edge) (COMPRESSION_EDGE_INFO(edge)->is_dummy)



Local Slist bucket_sort (Sgraph graph) /* bestimmt Liste aller Knoten */
                                /* mit steigendem Level        */
{
   int    n;
   Slist *bucket;
   int    i;
   Snode  node;
   Slist  nodelist;
   Slist  l;

   n = number_of_nodes (graph);

   bucket = (Slist*) calloc(n,sizeof(Slist));

   for (i=0;i<n;i++)
      bucket[i] = empty_slist;

   for_all_nodes (graph,node)
      bucket[LEVEL(node)] = add_snode_to_slist(bucket[LEVEL(node)],node);
   end_for_all_nodes (graph,node);

   nodelist = empty_slist;

   for (i=0;i<n;i++) {
      for_slist (bucket[i],l)
         node = attr_snode(l);
         nodelist = add_snode_to_slist(nodelist,node);
      end_for_slist (bucket[i],l);
      free_slist (bucket[i]);
   }

   free (bucket);

   return nodelist;
}



Local int *compute_level_array (Sgraph graph) /* wieviele Knotensegmente */
                                       /* sind auf jedem Level?   */
{
   int    n;
   int    *level_array;
   int    i;
   Snode  node;

   n = number_of_nodes (graph);

   level_array = (int*) calloc(n,sizeof(int));

   for (i=0;i<n;i++)
      level_array[i] = 0;

   for_all_nodes (graph,node)
      level_array[LEVEL(node)]++;
   end_for_all_nodes (graph,node);

   return level_array;
}



Local Slist compute_ingoing_edges (Snode node) /* in node eingehende Kanten */
           
{
   Slist edgelist;
   Sedge edge;

   edgelist = empty_slist;

   for_sourcelist (node,edge)
      if (!IS_DUMMY_EDGE(edge) && (LEVEL(TARGET_NODE(edge,node)) < LEVEL(node)))
         edgelist = add_sedge_to_slist(edgelist,edge);
   end_for_sourcelist (node,edge);

   return edgelist;
}



Local Slist compute_outgoing_edges (Snode node) /* von node ausgehende Kanten */
           
{
   Slist edgelist;
   Sedge edge;

   edgelist = empty_slist;

   for_sourcelist (node,edge)
      if (!IS_DUMMY_EDGE(edge) && (LEVEL(TARGET_NODE(edge,node)) > LEVEL(node)))
         edgelist = add_sedge_to_slist(edgelist,edge);
   end_for_sourcelist (node,edge);

   return edgelist;
}



Local int highest_level (Slist edgelist, Snode node) /* Level des hoechsten Nachbarn */
                                        /* von node in edgelist         */
           
{
   int   level;
   Slist l;
   Sedge edge;
   Snode target;

   level = INT_MIN;

   for_slist (edgelist,l)
      edge = attr_sedge(l);
      target = TARGET_NODE(edge,node);
      level = maximum (level,LEVEL(target));
   end_for_slist (edgelist,l);

   return level;
}



Local int lowest_level (Slist edgelist, Snode node) /* Level des niedrigsten Nachbarn */
                                       /* von node in edgelist           */
           
{
   int   level;
   Slist l;
   Sedge edge;
   Snode target;

   level = INT_MAX;

   for_slist (edgelist,l)
      edge = attr_sedge(l);
      target = TARGET_NODE(edge,node);
      level = minimum (level,LEVEL(target));
   end_for_slist (edgelist,l);

   return level;
}



Local Slist reverse_list (Slist list1) /* dreht Liste um */
            
{
   Slist list2,l;
   Snode node;

   list2 = empty_slist;

   for_slist (list1,l)
      node = attr_snode(l);
      list2 = add_snode_to_slist(list2,node);
      list2 = list2->pre;
   end_for_slist (list1,l);

   return list2;
}



Local void compute_edge_coordinates (Snode node)  /* berechnet Kantenkoordinaten neu */
           
{
   Sedge    edge;
   Edgeline edgeline;
   
   for_sourcelist (node,edge)
      if (!IS_DUMMY_EDGE(edge)) {
         edgeline = (Edgeline) edge_get(graphed_edge(edge),EDGE_LINE);
         if (LEVEL(TARGET_NODE(edge,node)) < LEVEL(node))
            iif (node == (Snode)sedge_real_source(edge),
                 set_edgeline_xy (edgeline->suc,EDGE_X(edge)*visibility_layout2_settings.horizontal_distance,node->y+visibility_layout2_settings.height/2),
                 set_edgeline_xy (edgeline->suc->suc,EDGE_X(edge)*visibility_layout2_settings.horizontal_distance,node->y+visibility_layout2_settings.height/2));
         else
            iif (node == (Snode)sedge_real_source(edge),
                 set_edgeline_xy (edgeline->suc,EDGE_X(edge)*visibility_layout2_settings.horizontal_distance,node->y-visibility_layout2_settings.height/2),
                 set_edgeline_xy (edgeline->suc->suc,EDGE_X(edge)*visibility_layout2_settings.horizontal_distance,node->y-visibility_layout2_settings.height/2));
         edge_set(graphed_edge(edge),
                  ONLY_SET,
                  EDGE_LINE,edgeline,
                  0);
      }
   end_for_sourcelist (node,edge);
}



void visibility_compression (Sgraph graph, int max, Visibility_Type type) /* Hauptfunktion fuer Compression */
                      
                     /* maximal moegliches Knotenlevel */
                     
{
   Slist    nodelist,rev_list,l;
   int     *level_array; /* Anzahl der Knotensegmente auf bestimmtem Level */
   int      shift;  /* Mindestverschiebemoeglichkeit fuer die Knoten */
   Snode    node;
   bool     changed; /* wurde Knoten verschoben? */
   Slist    ingoing_edges,outgoing_edges; /* eingehende bzw. ausgehende Kanten */
   int      level;
   int      size;
   int      max_level; /* maximales Knotenlevel */

   nodelist = bucket_sort (graph); /* enthaelt Knoten mit steigendem Level */
   level_array = compute_level_array (graph);
   shift = 0;

   for_slist (nodelist,l)   /* Phase 1 */
      node = attr_snode(l);
      changed = FALSE;
      ingoing_edges = compute_ingoing_edges (node);
      outgoing_edges = compute_outgoing_edges (node);
      if (shift > 0) {
         changed = TRUE;
         level = LEVEL(node) - shift;
         level_array[level]++;
         level_array[LEVEL(node)]--;
         LEVEL(node) = level;
         node->y = (max - level) * visibility_layout2_settings.vertical_distance;
      }
      if (LEVEL(node) != 0) { /* niedrigster Knoten kann nicht verschoben werden */
         level = LEVEL(node) - 1;
         while (level_array[level] == 0) /* auf level befindet sich */
            level--;                     /* kein Knotensegment      */
         level++;  /* letztes Level in der while-Schleife ist nicht mehr leer */
         if (LEVEL(node) != level) { /* Knoten kann verschoben werden */
            changed = TRUE;
            shift = shift + LEVEL(node) - level;
            level_array[level]++;
            level_array[LEVEL(node)]--;
            LEVEL(node) = level;
            node->y = (max - level) * visibility_layout2_settings.vertical_distance;
         }
      }
      size = size_of_slist (ingoing_edges);
      if ((((type == RT_weak) || (type == Kant_weak) || (type == Numm_weak)) && (size == XR(node) - XL(node) + 1)) ||
          ((type == TT_weak) && (size == 1) && (XR(node) - XL(node) == 1)) ||
          ((type == TT_weak) && (size > 1) && ((XR(node) - XL(node)) / 2 + 1 == size))) {
         level = highest_level (ingoing_edges,node) + 1; /* wohin kann verschoben werden? */
         if (LEVEL(node) != level) { /* Knoten kann verschoben werden */
            changed = TRUE;
            level_array[level]++;
            level_array[LEVEL(node)]--;
            LEVEL(node) = level;
            node->y = (max - level) * visibility_layout2_settings.vertical_distance;
         }
      }
      if (changed) /* Knoten wurde verschoben */
         compute_edge_coordinates (node); /* modifiziere Kantenkoordinaten */
      free_slist (ingoing_edges);
      free_slist (outgoing_edges);
   end_for_slist (nodelist,l);

   max_level = 0; /* Berechnung maximales Level */
   for_slist (nodelist,l)
      node = attr_snode(l);
      max_level = maximum (max_level,LEVEL(node));
   end_for_slist (nodelist,l);

   free_slist (nodelist);

   nodelist = bucket_sort (graph);
   rev_list = reverse_list (nodelist); /* enthaelt Knoten mit fallendem Level */

   free_slist (nodelist);
   shift = 0;

   for_slist (rev_list,l)   /* Phase 2 */
      node = attr_snode(l);
      changed = FALSE;
      ingoing_edges = compute_ingoing_edges (node);
      outgoing_edges = compute_outgoing_edges (node);
      if (shift > 0) {
         changed = TRUE;
         level = LEVEL(node) + shift;
         level_array[level]++;
         level_array[LEVEL(node)]--;
         LEVEL(node) = level;
         node->y = (max - level) * visibility_layout2_settings.vertical_distance;
      }
      if (LEVEL(node) != max_level) { /* hoechster Knoten kann nicht */
                                      /* verschoben werden */
         level = LEVEL(node) + 1;
         while (level_array[level] == 0) /* auf level befindet sich */
            level++;                     /* kein Knotensegment      */
         level--;                        /* letztes Level in der Schleife nicht mehr leer */
         if (LEVEL(node) != level) { /* Knoten kann verschoben werden */
            changed = TRUE;
            shift = shift + level - LEVEL(node);
            level_array[level]++;
            level_array[LEVEL(node)]--;
            LEVEL(node) = level;
            node->y = (max - level) * visibility_layout2_settings.vertical_distance;
         }
      }
      size = size_of_slist (outgoing_edges);
      if ((((type == RT_weak) || (type == Kant_weak) || (type == Numm_weak)) && (size == XR(node) - XL(node) + 1)) ||
         ((type == TT_weak) && (size == 1) && (XR(node) - XL(node) == 1)) ||
         ((type == TT_weak) && (size > 1) && ((XR(node) - XL(node)) / 2 + 1 == size))) {
         level = lowest_level (outgoing_edges,node) - 1;
         if (LEVEL(node) != level) { /* Knoten kann verschoben werden */
            changed = TRUE;
            level_array[level]++;
            level_array[LEVEL(node)]--;
            LEVEL(node) = level;
            node->y = (max - level) * visibility_layout2_settings.vertical_distance;
         }
      }
      if (changed) /* Knoten wurde verschoben */
         compute_edge_coordinates (node); /* modifiziere Kantenkoordinaten */
      free_slist (ingoing_edges);
      free_slist (outgoing_edges);
   end_for_slist (rev_list,l);

   free_slist (rev_list);
   free (level_array);
}



/***************************************************************************/
/*                                                                         */
/*                      END OF FILE: COMPRESSION.C                         */
/*                                                                         */
/***************************************************************************/
