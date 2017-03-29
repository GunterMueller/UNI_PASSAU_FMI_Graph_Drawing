/*************************************************************************/
/*                                                                       */
/* FILE: ST_NUMBERING.C                                                  */
/*                                                                       */
/* Beschreibung: berechnet st-Nummerierung wie von Even/Tarjan 1976 be-  */
/*               schrieben; Ergebnis in node->nr                         */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include "visibility_definitions.h"


typedef enum {new,old} Mark; /* Knoten- und Kantenmarkierung */

typedef struct internal_node_info { /* interne Knotenattribute */
         int     dfsnum; /* DFS-Nummer */
         int     lowpt; /* Lowpoint */
         bool    visited; /* Knoten schon gesehen? */
         Mark    mark; /* Knoten neu oder alt? */
         Slist   tree_forward; /* Vorwaerts-Baumkanten */
         Sedge   tree_backward; /* Rueckwaerts-Baumkanten */
         Slist   forward; /* Vorwaertskanten */
         Slist   backward; /* Rueckwaertskanten */
         Sedge   lowpt_edge; /* 1. Kante im Pfad zum lowpt */
} *Internal_Node_Info;

typedef struct internal_edge_info { /* interne Kantenattribute */
         bool  seen; /* Kante schon gesehen? */
         Mark  mark; /* Kante neu oder alt */
} *Internal_Edge_Info;


#define INTERNAL_NODE_INFO(node) (attr_data_of_type((node),Internal_Node_Info))
#define DFSNUM(node) (INTERNAL_NODE_INFO(node)->dfsnum)
#define LOWPT(node) (INTERNAL_NODE_INFO(node)->lowpt)
#define VISITED(node) (INTERNAL_NODE_INFO(node)->visited)
#define NODE_MARK(node) (INTERNAL_NODE_INFO(node)->mark)
#define TREE_FORWARD(node) (INTERNAL_NODE_INFO(node)->tree_forward)
#define TREE_BACKWARD(node) (INTERNAL_NODE_INFO(node)->tree_backward)
#define FORWARD(node) (INTERNAL_NODE_INFO(node)->forward)
#define BACKWARD(node) (INTERNAL_NODE_INFO(node)->backward)
#define LOWPT_EDGE(node) (INTERNAL_NODE_INFO(node)->lowpt_edge)
#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define SEEN(edge) (INTERNAL_EDGE_INFO(edge)->seen)
#define EDGE_MARK(edge) (INTERNAL_EDGE_INFO(edge)->mark)


Local int count; /* naechste DFS-Nummer */



Local void make_internal_attrs (Sgraph graph) /* erzeugt intern benoetigte Attribute */
             
{
   Snode node;
   Sedge edge;
   Internal_Node_Info node_info;
   Internal_Edge_Info edge_info;

   for_all_nodes (graph,node)
      node_info = (Internal_Node_Info) malloc(sizeof(struct internal_node_info));
      node_info->visited = FALSE;
      node_info->mark = new;
      node_info->tree_forward = node_info->forward = node_info->backward = empty_slist;
      node_info->tree_backward = empty_sedge;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Internal_Edge_Info)malloc(sizeof(struct internal_edge_info));
            edge_info->seen = FALSE;
            edge_info->mark = new;
            set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
         }
      end_for_sourcelist (node,edge);
    end_for_all_nodes (graph,node);
}



Local void depth_first_search (Snode v) /* rekursive Tiefensuche */
        
{
   Sedge edge;
   Snode w;
  
   for_sourcelist (v,edge)
      if (!SEEN(edge)) {
         SEEN(edge) = TRUE;
         w = TARGET_NODE(edge,v);
         if (!VISITED(w)) {
            VISITED(w) = TRUE;
            LOWPT(w) = DFSNUM(w) = count++;
            TREE_FORWARD(v) = add_sedge_to_slist(TREE_FORWARD(v),edge); 
            TREE_BACKWARD(w) = edge;
            depth_first_search (w);
            if (LOWPT(w) < LOWPT(v)) {
               LOWPT(v) = LOWPT(w);
               LOWPT_EDGE(v) = edge;
            }
         } else {
            BACKWARD(v) = add_sedge_to_slist(BACKWARD(v),edge);
            FORWARD(w) = add_sedge_to_slist(FORWARD(w),edge);
            if (DFSNUM(w) < LOWPT(v)) {
               LOWPT(v) = DFSNUM(w);
               LOWPT_EDGE(v) = edge;
            }
         }
      }
   end_for_sourcelist (v,edge);
}



Local Slist pathfinder (Snode v) /* sucht Pfad ueber neue Kanten zu alten Knoten */
        
{
   Sedge edge;
   Snode w,x;
   Slist path = empty_slist;

   while (BACKWARD(v) != empty_slist) { /* neue Rueckwartskante suchen */
      edge = attr_sedge(BACKWARD(v));
      BACKWARD(v) = subtract_first_element_from_slist(BACKWARD(v));
      if (EDGE_MARK(edge) == new) { /* neue Kante */
         EDGE_MARK(edge) = old;
         w = TARGET_NODE(edge,v);
         path = add_snode_to_slist(path,v);
         path = add_snode_to_slist(path,w);
         return path; /* Funktionsaufruf beendet */
      }
   }

   while (TREE_FORWARD(v) != empty_slist) { /* neue Vorwaerts-Baumkante suchen */
      edge = attr_sedge(TREE_FORWARD(v));
      TREE_FORWARD(v) = subtract_first_element_from_slist(TREE_FORWARD(v));
      if (EDGE_MARK(edge) == new) { /* neue Kante */
         EDGE_MARK(edge) = old;
         w = TARGET_NODE(edge,v);
         path = add_snode_to_slist(path,v);
         path = add_snode_to_slist(path,w);
         while (NODE_MARK(w) == new) {
            edge = LOWPT_EDGE(w);
            EDGE_MARK(edge) = old;
            x = TARGET_NODE(edge,w);
            NODE_MARK(w) = old;
            path = add_snode_to_slist(path,x);
            w = x;
         }
         return path; /* Funktionsaufruf beendet */
      }
   }

   while (FORWARD(v) != empty_slist) { /* neue Vorwaertskante suchen */
      edge = attr_sedge(FORWARD(v));
      FORWARD(v) = subtract_first_element_from_slist(FORWARD(v));
      if (EDGE_MARK(edge) == new) { /* neue Kante */
         EDGE_MARK(edge) = old;
         w = TARGET_NODE(edge,v);
         path = add_snode_to_slist(path,v);
         path = add_snode_to_slist(path,w);
         while (NODE_MARK(w) == new) {
            edge = TREE_BACKWARD(w);
            EDGE_MARK(edge) = old;
            x = TARGET_NODE(edge,w);
            NODE_MARK(w) = old;
            path = add_snode_to_slist(path,x);
            w = x;
         }
         return path; /* Funktionsaufruf beendet */
      }
   }
  
   return empty_slist; /* kein Pfad gefunden */
}



void st_numbering (Sgraph graph, Snode s, Snode t) /* Hauptfunktion fuer die st-Nummerierung */
             
          
{
   Sedge edge;
   Snode v,node;
   Slist stack,path;
   int   i,j,size;                                                                                     

   make_internal_attrs (graph);

   VISITED(s) = VISITED(t) = TRUE;
   for_sourcelist (t,edge)
      if (TARGET_NODE(edge,t) == s) {
         SEEN(edge) = TRUE;
         break;
      }
   end_for_sourcelist (t,edge);

   DFSNUM(t) = LOWPT(t) = 1;
   DFSNUM(s) = LOWPT(s) = 2;
   count = 3;

   depth_first_search (s);

   NODE_MARK(s) = NODE_MARK(t) = old;

   for_sourcelist (t,edge)
      if (TARGET_NODE(edge,t) == s) {
         EDGE_MARK(edge) = new;
         break;
      }
   end_for_sourcelist (t,edge);

   stack = add_snode_to_slist(add_snode_to_slist(empty_slist,s),t);
   i = 1; /* naechste Nummer */
   while (stack != empty_slist) {
      v = attr_snode(stack);
      stack = subtract_first_element_from_slist(stack); /* v von stack */
                                                        /* entfernen   */
      path = pathfinder(v);
      if (path != empty_slist) {
         path = subtract_last_element_from_slist(path);
         stack = add_slists (stack,path);
         size = size_of_slist (path);
         free_slist (path);
         for (j = 0;j < size;j++) stack = stack->pre; /* v erstes Element */
                                                      /* auf stack        */
      }
      else v->nr = i++;
   }

   for_all_nodes (graph,node) /* Speicherfreigabe */
      free (INTERNAL_NODE_INFO(node));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) free (INTERNAL_EDGE_INFO(edge));
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);
}



/*************************************************************************/
/*                                                                       */
/*                     END OF FILE: ST_NUMBERING.C                       */
/*                                                                       */
/*************************************************************************/
