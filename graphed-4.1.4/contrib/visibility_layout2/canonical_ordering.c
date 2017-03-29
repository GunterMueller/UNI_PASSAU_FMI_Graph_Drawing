/*************************************************************************/
/*                                                                       */
/* FILE: CANONICAL_ORDERING.C                                            */
/*                                                                       */
/* Beschreibung: berechnet kanonische Nummerierung eines triangulierten, */
/*               eingebetteten Graphen fuer den Algorithmus von Nummen-  */
/*               maa; Nummerierung in node->nr; Rueckgabe einer Liste    */
/*               mit den Knoten in aufsteigender Nummerierung            */
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
           int   degree;
           int   indegree;
           Slist edge_rotation;
           Slist position_in_list1;
           Slist position_in_list2;
} *Internal_Node_Info;

typedef struct internal_edge_info {
           Snode source;
           bool  mark; /* Kante schon gesehen? */
           Slist position_in_source_rotation; /* Pos. in der Rotation von source */
           Slist position_in_target_rotation; /* Pos. in der Rotation von target */
} *Internal_Edge_Info;


#define INTERNAL_NODE_INFO(node) (attr_data_of_type((node),Internal_Node_Info))
#define INTERVAL(node) (INTERNAL_NODE_INFO(node)->interval)
#define DEGREE(node) (INTERNAL_NODE_INFO(node)->degree)
#define INDEGREE(node) (INTERNAL_NODE_INFO(node)->indegree)
#define OUTDEGREE(node) (DEGREE(node) - INDEGREE(node))
#define EDGE_ROTATION(node) (INTERNAL_NODE_INFO(node)->edge_rotation)
#define POSITION_IN_LIST1(node) (INTERNAL_NODE_INFO(node)->position_in_list1)
#define POSITION_IN_LIST2(node) (INTERNAL_NODE_INFO(node)->position_in_list2)
#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define SOURCE(edge) (INTERNAL_EDGE_INFO(edge)->source)
#define MARK(edge) (INTERNAL_EDGE_INFO(edge)->mark)
#define POSITION_IN_SOURCE_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_source_rotation)
#define POSITION_IN_TARGET_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_target_rotation)
#define POSITION(edge,node) (iif((node)==SOURCE(edge),POSITION_IN_SOURCE_ROTATION(edge), POSITION_IN_TARGET_ROTATION(edge)))
#define SUC_EDGE(edge,node) (attr_sedge(POSITION(edge,node)->suc))
#define PRE_EDGE(edge,node) (attr_sedge(POSITION(edge,node)->pre))


Local Slist list1; /* Knoten mit interval = 1 und outdegree >= indegree */
Local Slist list2; /* Knoten mit interval = 1 und outdegree < indegree */
Local Slist result_list; /* Knoten in aufsteigender Ordnung */



Local void make_internal_attrs (Sgraph graph) /* erzeugt intern benoetigte Attribute */
             
{
   Snode              node;
   Sedge              edge;
   Slist              sourcelist,l;
   Internal_Node_Info node_info;
   Internal_Edge_Info edge_info;

   for_all_nodes (graph,node)
      node_info = (Internal_Node_Info) malloc(sizeof(struct internal_node_info));
      node_info->interval = 0;
      sourcelist = make_slist_of_sourcelist (node);
      node_info->degree = size_of_slist (sourcelist);
      node_info->indegree = 0;
      free_slist (sourcelist);
      node_info->edge_rotation = attr_slist(node);
      node_info->position_in_list1 = node_info->position_in_list2 = empty_slist;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->source = empty_snode;
            edge_info->mark = FALSE;
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



Local void process_external_face (Sgraph graph, Snode u, Snode v, Snode w) /* bearbeitet das aeussere Face */
             
             
{
   Sedge edge;
   Snode node;

   u->nr = 1;
   v->nr = 2;
   w->nr = number_of_nodes (graph);
   result_list = add_snode_to_slist(
                 add_snode_to_slist(result_list,u),v);

   for_sourcelist (u,edge)
      MARK(edge) = TRUE;
      node = TARGET_NODE(edge,u);
      if ((node != v) && (node != w)) {
         INDEGREE(node)++;
         INTERVAL(node) = -1;
      }
   end_for_sourcelist (u,edge);

   for_sourcelist (v,edge)
      MARK(edge) = TRUE;
      node = TARGET_NODE(edge,v);
      if ((node != u) && (node != w)) {
         INDEGREE(node)++;
         if (INTERVAL(node) == 0)
            INTERVAL(node) = -1;
         else if (MARK(SUC_EDGE(edge,node))) {
            INTERVAL(node) = 1;
            if (OUTDEGREE(node) >= INDEGREE(node)) {
               list1 = add_snode_to_slist(list1,node);
               POSITION_IN_LIST1(node) = list1->pre;
            } else {
               list2 = add_snode_to_slist(list2,node);
               POSITION_IN_LIST2(node) = list2->pre;
            }
         } else INTERVAL(node) = 2;
      }
   end_for_sourcelist (v,edge);
}



Local void process_interval (Snode node, Snode w, Sedge edge) /* veraendert INTERVAL(node) */
             
           
{
   Slist l;

   if (INTERVAL(node) == 0)
      INTERVAL(node) = -1;
   else if (INTERVAL(node) == -1) {
      if (MARK(SUC_EDGE(edge,node)) || MARK(PRE_EDGE(edge,node))) {
         INTERVAL(node) = 1;
         if (OUTDEGREE(node) >= INDEGREE(node)) {
            list1 = add_snode_to_slist(list1,node);
            POSITION_IN_LIST1(node) = list1->pre;
         } else {
            list2 = add_snode_to_slist(list2,node);
            POSITION_IN_LIST2(node) = list2->pre;
         }
      } else INTERVAL(node) = 2;
   } else {
      if (MARK(SUC_EDGE(edge,node)) && MARK(PRE_EDGE(edge,node))) {
         INTERVAL(node)--;
         if (INTERVAL(node) == 1) {
            if (OUTDEGREE(node) >= INDEGREE(node)) {
               list1 = add_snode_to_slist(list1,node);
               POSITION_IN_LIST1(node) = list1->pre;
            } else {
               list2 = add_snode_to_slist(list2,node);
               POSITION_IN_LIST2(node) = list2->pre;
            }
         }
      } else if (!MARK(SUC_EDGE(edge,node)) && !MARK(PRE_EDGE(edge,node))) {
         INTERVAL(node)++;
         if ((l = POSITION_IN_LIST1(node)) != empty_slist) {
            list1 = subtract_immediately_from_slist(list1,l);
            POSITION_IN_LIST1(node) = empty_slist;
         } else if ((l = POSITION_IN_LIST2(node)) != empty_slist) {
            list2 = subtract_immediately_from_slist(list2,l);
            POSITION_IN_LIST2(node) = empty_slist;
         }
      }
   }
}



Local void free_memory (Sgraph graph) /* gibt intern benoetigten Speicher frei */
                               /* und setzt wieder Kantenrotationen ein */
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



Slist canonical_ordering (Sgraph graph, Snode u, Snode v, Snode w) /* Hauptfunktion fuer die   */
                                       /* kanonische Nummerierung; */
                                       /* aeusseres Face: u,v,w    */
{
   int   number,n;
   Snode current_node; /* naechster zu nummerierender Knoten */
   Snode node;
   Sedge edge;
   Slist l;

   make_internal_attrs (graph);

   list1 = list2 = result_list = empty_slist;

   process_external_face (graph,u,v,w);

   n = number_of_nodes (graph);

   for (number = 3;number < n;number++) {

      if (list1 != empty_slist) {
         current_node = attr_snode(list1);
         list1 = subtract_first_element_from_slist(list1);
      } else {
         current_node = attr_snode(list2);
         list2 = subtract_first_element_from_slist(list2);
      }

      current_node->nr = number;
      result_list = add_snode_to_slist(result_list,current_node);
   
      for_sourcelist (current_node,edge)
         if (!MARK(edge)) {  /* Kante wurde noch nicht gesehen */
            node = TARGET_NODE(edge,current_node);
            if (node != w) {
               MARK(edge) = TRUE;
               INDEGREE(node)++;
               if ((OUTDEGREE(node) < INDEGREE(node)) && 
                   ((l = POSITION_IN_LIST1(node)) != empty_slist)) {
                  list1 = subtract_immediately_from_slist(list1,l);
                  list2 = add_snode_to_slist(list2,node);
                  POSITION_IN_LIST1(node) = empty_slist;
                  POSITION_IN_LIST2(node) = list2->pre;
               }
               process_interval (node,w,edge);
            }
         }
      end_for_sourcelist (current_node,edge);

   }

   result_list = add_snode_to_slist(result_list,w);

   free_memory (graph);

   return result_list;
}



/*************************************************************************/
/*                                                                       */
/*                  END OF FILE: CANONICAL_ORDERING.C                    */
/*                                                                       */
/*************************************************************************/
