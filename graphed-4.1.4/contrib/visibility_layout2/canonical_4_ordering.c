/*************************************************************************/
/*                                                                       */
/* FILE: CANONICAL_4_ORDERING.C                                          */
/*                                                                       */
/* Beschreibung: berechnet kanonische 4-Nummerierung eines triangulier-  */
/*               ten, eingebetteten Graphen fuer den Algorithmus von     */
/*               Kant; Nummerierung in node->nr                          */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include "visibility_definitions.h"

extern int number_of_nodes (Sgraph graph); /* springembedder_rf/spring.c */

typedef struct internal_node_info {
           int   interval; /* 0 = noch nicht gesehen, -1 = genau einmal gesehen */
           int   chords;
           bool  on_exterior_face; /* Knoten auf dem aeusseren Face? */
           bool  old; /* Knoten schon gesehen? */
           Slist edge_rotation;
           Slist position_in_list;
} *Internal_Node_Info;

typedef struct internal_edge_info {
           bool  mark; /* Kante schon gesehen? */
           Snode source;
           Slist position_in_source_rotation; /* Pos. von edge in der Rotation von source */
           Slist position_in_target_rotation; /* Pos. von edge in der Rotation von target */
} *Internal_Edge_Info;


#define INTERNAL_NODE_INFO(node) (attr_data_of_type((node),Internal_Node_Info))
#define INTERVAL(node) (INTERNAL_NODE_INFO(node)->interval)
#define CHORDS(node) (INTERNAL_NODE_INFO(node)->chords)
#define ON_EXTERIOR_FACE(node) (INTERNAL_NODE_INFO(node)->on_exterior_face)
#define OLD(node) (INTERNAL_NODE_INFO(node)->old)
#define EDGE_ROTATION(node) (INTERNAL_NODE_INFO(node)->edge_rotation)
#define POSITION_IN_LIST(node) (INTERNAL_NODE_INFO(node)->position_in_list)
#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define MARK(edge) (INTERNAL_EDGE_INFO(edge)->mark)
#define SOURCE(edge) (INTERNAL_EDGE_INFO(edge)->source)
#define POSITION_IN_SOURCE_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_source_rotation)
#define POSITION_IN_TARGET_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_target_rotation)
#define POSITION(edge,node) (iif((node)==SOURCE(edge),POSITION_IN_SOURCE_ROTATION(edge), POSITION_IN_TARGET_ROTATION(edge)))
#define SUC_EDGE(edge,node) (attr_sedge(POSITION(edge,node)->suc))
#define PRE_EDGE(edge,node) (attr_sedge(POSITION(edge,node)->pre))


Local Slist list; /* Knoten, die als naechste nummeriert werden koennen */



Local void make_internal_attrs (Sgraph graph) /* erzeugt und initialisiert   */
                                       /* intern benoetigte Attribute */
{
   Snode              node;
   Sedge              edge;
   Slist              l;
   Internal_Node_Info node_info;
   Internal_Edge_Info edge_info;

   for_all_nodes (graph,node)
      node_info = (Internal_Node_Info) malloc(sizeof(struct internal_node_info));
      node_info->interval = node_info->chords = 0;
      node_info->on_exterior_face = node_info->old = FALSE;
      node_info->edge_rotation = attr_slist(node);
      node_info->position_in_list = empty_slist;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->mark = FALSE;
            edge_info->source = empty_snode;
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
         } else {
            POSITION_IN_TARGET_ROTATION(edge) = l;
         }
      end_for_slist (EDGE_ROTATION(node),l);
   end_for_all_nodes (graph,node);
}



Local void process_exterior_face (Sgraph graph, Snode u, Snode v, Snode w) /* bearbeitet das aeussere Face */
             
             
{
   Slist l;
   Sedge edge,edge1,edge2;
   Snode node,node1,node2;

   u->nr = 1;
   v->nr = 2;
   w->nr = number_of_nodes (graph);
   ON_EXTERIOR_FACE(u) = ON_EXTERIOR_FACE(v) = TRUE;
   OLD(u) = OLD(v) = OLD(w) = TRUE;

   for_slist (EDGE_ROTATION(w),l)
      edge = attr_sedge(l);
      MARK(edge) = TRUE;
      node = TARGET_NODE(edge,w);
      if ((node != u) && (node != v))
         INTERVAL(node) = -1;
      if (node == u) {
         edge1 = attr_sedge(l->pre);
         edge2 = attr_sedge(l->suc);
         node1 = TARGET_NODE(edge1,w);
         node2 = TARGET_NODE(edge2,w);
         if (node1 != v)
            list = add_snode_to_slist(list,node1);
         else if (node2 != v)
            list = add_snode_to_slist(list,node2);
      }
      ON_EXTERIOR_FACE(node) = OLD(node) = TRUE;
   end_for_slist (EDGE_ROTATION(w),l);
}



Local void process_interval (Snode node, Sedge edge) /* veraendert INTERVAL(node) */
           
           
{
   Slist l;

   if (INTERVAL(node) == -1) {
      if (MARK(SUC_EDGE(edge,node)) || MARK(PRE_EDGE(edge,node))) {
         INTERVAL(node) = 1;
         if (CHORDS(node) == 0) {
            list = add_snode_to_slist(list,node);
            POSITION_IN_LIST(node) = list->pre;
         }
      } else INTERVAL(node) = 2;
   } else {
      if (MARK(SUC_EDGE(edge,node)) && MARK(PRE_EDGE(edge,node))) {
         INTERVAL(node)--;
         if ((INTERVAL(node) == 1) && (CHORDS(node) == 0)) {
            list = add_snode_to_slist(list,node);
            POSITION_IN_LIST(node) = list->pre;
         }
      } else if (!MARK(SUC_EDGE(edge,node)) && !MARK(PRE_EDGE(edge,node))) {
         INTERVAL(node)++;
         if ((l = POSITION_IN_LIST(node)) != empty_slist) {
            list = subtract_immediately_from_slist(list,l);
            POSITION_IN_LIST(node) = empty_slist;
         }
      }
   }
}



Local void process_chords (Slist neighbors, Snode u, Snode v) /* berechnet CHORDS(node) fuer alle    */
                                          /* neuen Knoten auf dem aeusseren Face */
          
{
   Snode node,node1;
   Slist l,l1;
   Sedge edge;

   if (size_of_slist(neighbors) == 2) {
      node = attr_snode(neighbors);
      node1 = attr_snode(neighbors->suc);
      if ((node != u) && (node != v)) {
         CHORDS(node)--;
         if ((CHORDS(node) == 0) && (INTERVAL(node) == 1)) {
            list = add_snode_to_slist(list,node);
            POSITION_IN_LIST(node) = list->pre;
         }
      }
      if ((node1 != u) && (node1 != v)) {
         CHORDS(node1)--;
         if (CHORDS(node1) == 0) {
            list = add_snode_to_slist(list,node1);
            POSITION_IN_LIST(node1) = list->pre;
         }
      }
   } else {
      for_slist (neighbors,l)
         node = attr_snode(l);
         if (!OLD(node)) { /* neuer Knoten */
            for_sourcelist (node,edge)
               node1 = TARGET_NODE(edge,node);
               if (ON_EXTERIOR_FACE(node1) && 
                   (node1 != attr_snode(l->suc)) && (node1 != attr_snode(l->pre))) {
                  CHORDS(node)++;
                  if (OLD(node1) && (node1 != u) && (node1 != v)) {
                     CHORDS(node1)++;
                     if ((l1 = POSITION_IN_LIST(node1)) != empty_slist) {
                        list = subtract_immediately_from_slist(list,l1);
                        POSITION_IN_LIST(node1) = empty_slist;
                     }
                  }
               }
            end_for_sourcelist (node,edge);
         }
      end_for_slist (neighbors,l);
   }
}



Local void free_memory (Sgraph graph) /* gibt intern benoetigten Speicher frei, */
                               /* setzt Kantenrotationen wieder ein      */
{
   Snode node;
   Sedge edge;
   Slist edge_rotation;

   for_all_nodes (graph,node)
      edge_rotation = EDGE_ROTATION(node);
      free (INTERNAL_NODE_INFO(node));
      set_nodeattrs (node,make_attr_slist(edge_rotation));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) free (INTERNAL_EDGE_INFO(edge));
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);
}



void canonical_4_ordering (Sgraph graph, Snode u, Snode v, Snode w) /* Hauptfunktion fuer die    */
                                        /* kanonische 4-Nummerierung */
             
{
   Slist neighbors,l;
   int   number;
   Snode node;
   Sedge edge;
   Snode current_node; /* naechster zu nummerierender Knoten */

   make_internal_attrs (graph);
   list = empty_slist;

   process_exterior_face (graph,u,v,w);

   for (number = number_of_nodes(graph)-1;number > 2;number--) {
      current_node = attr_snode(list);
      list = subtract_first_element_from_slist(list);
      ON_EXTERIOR_FACE(current_node) = FALSE;
      current_node->nr = number;
      neighbors = empty_slist;

      for_slist (EDGE_ROTATION(current_node),l)
         edge = attr_sedge(l);
         if (!MARK(edge)) { /* edge wurde noch nicht gesehen */
            MARK(edge) = TRUE;
            node = TARGET_NODE(edge,current_node);
            ON_EXTERIOR_FACE(node) = TRUE;
            neighbors = add_snode_to_slist(neighbors,node);
            if (!OLD(node)) /* node noch nicht gesehen */
               INTERVAL(node) = -1;
            else if ((node != u) && (node != v))
               process_interval (node,edge);
         }
      end_for_slist (EDGE_ROTATION(current_node),l);

      process_chords (neighbors,u,v);

      for_slist (neighbors,l)
         node = attr_snode(l);
         OLD(node) = TRUE;
      end_for_slist (neighbors,l);
      free_slist (neighbors);
   }

   free_memory (graph);

}


/*************************************************************************/
/*                                                                       */
/*                  END OF FILE: CANONICAL_4_ORDERING.C                  */
/*                                                                       */
/*************************************************************************/
