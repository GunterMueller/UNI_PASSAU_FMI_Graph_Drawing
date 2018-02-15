/********************************************************************************/
/*                                                                              */
/* FILE: TRIANGULATION.C                                                        */
/*                                                                              */
/* Beschreibung: Triangulierung eines  zusammenhaengenden, planaren Graphen.    */
/*               Die Knoten des triangulierten Graphen enthalten in attr_slist  */
/*               die Einbettung des Graphen. Als Resultat wird eine Liste der   */
/*               eingefuegten Kanten zurueckgegeben.                            */
/*                                                                              */
/* benoetigte externe Funktion: construct_biconnected_graph                     */
/*                                                                              */
/********************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <algorithms.h>
#include "visibility_definitions.h"


extern Slist construct_biconnected_graph (Sgraph graph);


typedef struct internal_edge_info { /* interne Kantenattribute */
           Snode source;
           Slist position_in_source_rotation; /* Pos. in der Rotation von source */
           Slist position_in_target_rotation; /* Pos. in der Rotation von target */
           bool  seen_from_source; /* Kante von source aus schon durchlaufen? */
           bool  seen_from_target; /* Kante von target aus schon durchlaufen? */
} *Internal_Edge_Info;


#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define SOURCE(edge) (INTERNAL_EDGE_INFO(edge)->source)
#define POSITION_IN_SOURCE_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_source_rotation)
#define POSITION_IN_TARGET_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_target_rotation)
#define POSITION(edge,node) (iif((node)==SOURCE(edge),POSITION_IN_SOURCE_ROTATION(edge), POSITION_IN_TARGET_ROTATION(edge)))
#define SUC_EDGE(edge,node) (attr_sedge(iif((node)==SOURCE(edge), POSITION_IN_SOURCE_ROTATION(edge),POSITION_IN_TARGET_ROTATION(edge))->suc))
#define SEEN_FROM_SOURCE(edge) (INTERNAL_EDGE_INFO(edge)->seen_from_source)
#define SEEN_FROM_TARGET(edge) (INTERNAL_EDGE_INFO(edge)->seen_from_target)
#define SEEN(edge,node) (iif((node)==SOURCE(edge),SEEN_FROM_SOURCE(edge),SEEN_FROM_TARGET(edge)))
#define EDGE_ROTATION(node) (attr_slist(node))



Local void make_internal_attrs (Sgraph graph) /* erzeugt interne Attribute */
             
{
   Snode              node;
   Sedge              edge;
   Slist              l;
   Internal_Edge_Info edge_info;

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->source = empty_snode;
            edge_info->seen_from_source = edge_info->seen_from_target = FALSE;
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



Local Slist compute_face (Snode node, Sedge edge) /* bestimmt Face, das man erhaelt, wenn Kante  */      
                                     /* edge von Knoten node aus durchlaufen wird,  */
                                     /* anschliessend die Kante, die in der Liste   */
                                     /* von TARGET_NODE(edge,node) auf edge folgt,  */
                                     /* u.s.w.; gibt Liste der Kanten auf dem Face  */
                                     /* zurueck.                                    */
{
   Slist face = empty_slist;
   Snode target;
   Snode current_node;
   Sedge current_edge; /* naechste zu durchlaufende Kante (von current_node aus) */

   if (!SEEN(edge,node)) { /* edge wurde von node aus noch nicht durchlaufen */
      face = add_sedge_to_slist(face,edge);
      target = TARGET_NODE(edge,node);
      if (node == SOURCE(edge))
         SEEN_FROM_SOURCE(edge) = TRUE;
      else
         SEEN_FROM_TARGET(edge) = TRUE;
      current_node = target;
      current_edge = edge;
      while (current_node != node) { /* face noch nicht vollstaendig */
         current_edge = SUC_EDGE(current_edge,current_node);
         target = TARGET_NODE(current_edge,current_node);
         iif (current_node == SOURCE(current_edge),
              SEEN_FROM_SOURCE(current_edge) = TRUE,
              SEEN_FROM_TARGET(current_edge) = TRUE);
         face = add_sedge_to_slist(face,current_edge);
         current_node = target;
      }
   }

   return face;
}



Local Slist compute_facelist (Sgraph graph) /* gibt Liste der Faces von graph zurueck */
             
{
   Snode node;
   Sedge edge;
   Slist face;
   Slist facelist;

   facelist = empty_slist;

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         face = compute_face (node,edge);
         if (face != empty_slist)
            facelist = add_slist_to_slist(facelist,face);
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   return facelist;
}



Local void free_memory (Sgraph graph) /* gibt intern benoetigten Speicher frei */
             
{
   Snode node;
   Sedge edge;

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) free (INTERNAL_EDGE_INFO(edge));
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);
}



Slist visibility_layout2_triangulate (Sgraph graph) /* Hauptfunktion fuer die Triangulierung */
             
{
   Slist              dummy_edges = empty_slist;
   Slist              facelist;
   Slist              face;
   int                size_of_face; /* Anzahl der Kanten auf einem Face */
   Slist              l1,l2,l3;
   bool               start; /* 1. Knoten eines Faces wird betrachtet */
   bool               fixed_pre_node; /* kann bestimmter Knoten mit allen anderen */
                                      /* Knoten auf dem Face verbunden werden?    */
   Sedge              edge,old_edge,new_edge;
   Snode              current_node,pre_node,suc_node;
   bool               connected; /* Knoten schon verbunden? */
   Sedge              dummy_edge;
   Internal_Edge_Info edge_info;

   if (!test_sgraph_biconnected (graph))
      dummy_edges = construct_biconnected_graph (graph);
                           /* macht den Graphen zweifach zusammenhaengend */

   embed (graph);

   make_internal_attrs (graph);

   facelist = compute_facelist (graph); /* Liste der Faces */

   for_slist (facelist,l1)
      face = attr_slist(l1);
      start = TRUE;
      fixed_pre_node = FALSE;
      size_of_face = size_of_slist (face);
      for_slist (face,l2)
         if (size_of_face == 3) break; /* face ist trianguliert */
         if (start) {
            start = FALSE;
            old_edge = attr_sedge(l2->pre); /* Kante von current_node zu pre_node */
            new_edge = attr_sedge(l2); /* Kante von current_node zu suc_node */
            current_node = iif ((old_edge->snode == new_edge->snode) || (old_edge->snode == new_edge->tnode),
                                old_edge->snode,old_edge->tnode);
            pre_node = TARGET_NODE(old_edge,current_node);
            suc_node = TARGET_NODE(new_edge,current_node);
         } else {
            new_edge = attr_sedge(l2);
            current_node = TARGET_NODE(old_edge,pre_node);
            suc_node = TARGET_NODE(new_edge,current_node);
         }
         if (!fixed_pre_node) {
            set_attr_data(pre_node, POSITION(old_edge,pre_node)->pre);
            connected = FALSE; /* pre_node und suc_node verbunden? */
            for_slist (EDGE_ROTATION(pre_node),l3)
               edge = attr_sedge(l3);
               if (TARGET_NODE(edge,pre_node) == suc_node) {
                  connected = TRUE;
                  break;
               }
            end_for_slist (EDGE_ROTATION(pre_node),l3);
         }
         if (!connected || fixed_pre_node) { /* pre_node und suc_node koennen verbunden werden */
            edge_info = (Internal_Edge_Info) malloc (sizeof(struct internal_edge_info));
            dummy_edge = make_edge (pre_node,suc_node,make_attr(ATTR_DATA,(char*)edge_info));
            set_attr_data(pre_node,add_sedge_to_slist(POSITION(old_edge,pre_node),dummy_edge));
            set_attr_data(suc_node,add_sedge_to_slist(POSITION(new_edge,suc_node)->suc,dummy_edge));
            if (pre_node == SOURCE(dummy_edge)) {
               POSITION_IN_SOURCE_ROTATION(dummy_edge) = POSITION(old_edge,pre_node)->pre;     
               POSITION_IN_TARGET_ROTATION(dummy_edge) = POSITION(new_edge,suc_node)->suc;
            } else {
               POSITION_IN_TARGET_ROTATION(dummy_edge) = POSITION(old_edge,pre_node)->pre;     
               POSITION_IN_SOURCE_ROTATION(dummy_edge) = POSITION(new_edge,suc_node)->suc;
            }
            dummy_edges = add_sedge_to_slist(dummy_edges,dummy_edge);
            size_of_face--; /* Groesse des verbliebenen Faces */
            old_edge = dummy_edge;
         } else { /* pre_node und suc_node sind schon verbunden, ab jetzt fixed_pre_node = TRUE */
            fixed_pre_node = TRUE;
            old_edge = new_edge;
            pre_node = current_node;
         }
      end_for_slist (face,l2);
   end_for_slist (facelist,l1);

   free_memory (graph);

   return dummy_edges;
}


/************************************************************************************/
/*                                                                                  */
/*                         END OF FILE: TRIANGULATION.C                             */
/*                                                                                  */
/************************************************************************************/
