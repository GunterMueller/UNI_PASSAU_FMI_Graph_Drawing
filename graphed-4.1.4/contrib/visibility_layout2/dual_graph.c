/*************************************************************************/
/*                                                                       */
/* FILE: DUAL_GRAPH.C                                                    */
/*                                                                       */
/* Beschreibung: konstruiert den dualen Graphen und richtet die Kanten   */
/*               so, wie es fuer die Algorithmen von Rosenstiehl/Tarjan  */
/*               und Tamassia/Tollis noetig ist;                         */
/*               Ausgabeattribute: Duality_Edge_Info fuer primale Kanten */
/*                                 Visibility_Face_Info fuer Faces       */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include "visibility_definitions.h"
#include "TTvisibility.h"

typedef struct internal_edge_info { /* interne Kantenattribute */
		bool   seen_from_source; /* Kante von source aus durchlaufen? */
		bool   seen_from_target; /* Kante von target aus durchlaufen? */
		Slist  position_in_source_rotation; /* Pos. in der Rotation von source */
		Slist  position_in_target_rotation; /* Pos. in der Rotation von target */
		Snode  left_face; /* linkes Face */
		Snode  right_face; /* rechtes Face */
} *Internal_Edge_Info;

typedef struct internal_face_info { /* interne Faceattribute */
                Slist  right_boundary; /* rechter Rand */
                Slist  left_boundary; /* linker Rand */
                Snode  lowpoint; /* Knoten auf Rand mit min. Nummer */
                Snode  highpoint; /* Knoten auf Rand mit max. Nummer */
                bool   mark; /* verhindert Mehrfachkanten im dualen Graphen */
} *Internal_Face_Info;


#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define SEEN_FROM_SOURCE(edge) (INTERNAL_EDGE_INFO(edge)->seen_from_source)
#define SEEN_FROM_TARGET(edge) (INTERNAL_EDGE_INFO(edge)->seen_from_target)
#define SEEN(edge,node) (iif((node)->nr < TARGET_NODE((edge),(node))->nr,SEEN_FROM_SOURCE(edge),SEEN_FROM_TARGET(edge)))
#define POSITION_IN_SOURCE_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_source_rotation)
#define POSITION_IN_TARGET_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_target_rotation)
#define LEFT_FACE(edge) (INTERNAL_EDGE_INFO(edge)->left_face)
#define RIGHT_FACE(edge) (INTERNAL_EDGE_INFO(edge)->right_face)
#define NEXT_EDGE(edge,node) (attr_sedge(iif((node)->nr < TARGET_NODE((edge),(node))->nr,POSITION_IN_SOURCE_ROTATION(edge),POSITION_IN_TARGET_ROTATION(edge))->suc))
#define INTERNAL_FACE_INFO(face) (attr_data_of_type((face),Internal_Face_Info))
#define RIGHT_BOUNDARY(face) (INTERNAL_FACE_INFO(face)->right_boundary)
#define LEFT_BOUNDARY(face) (INTERNAL_FACE_INFO(face)->left_boundary)
#define LOWPOINT(face) (INTERNAL_FACE_INFO(face)->lowpoint)
#define HIGHPOINT(face) (INTERNAL_FACE_INFO(face)->highpoint)
#define MARK(face) (INTERNAL_FACE_INFO(face)->mark)



Local void make_internal_attrs (Sgraph graph) /* erzeugt intern benoetigte Attribute */
             
{
   Snode               node;
   Sedge               edge;
   Internal_Edge_Info  edge_info;
   Slist               l;

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->seen_from_source = edge_info->seen_from_target = FALSE;
            edge_info->left_face = edge_info->right_face = empty_snode;
            set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   for_all_nodes (graph,node)
      for_slist (attr_slist(node),l)
         edge = attr_sedge(l);
         iif (node->nr < TARGET_NODE(edge,node)->nr,
              POSITION_IN_SOURCE_ROTATION(edge) = l,
              POSITION_IN_TARGET_ROTATION(edge) = l);
      end_for_slist (attr_slist(node),l);
   end_for_all_nodes (graph,node);
}



Local void compute_dual_node (Snode node, Sedge edge, Sgraph graph, Sgraph dual_graph) /* berechnet dualen Knoten, wenn      */
                                                          /* edge von node aus durchlaufen wird */
             
                         
{
   Internal_Face_Info face_info;
   Snode              target;
   Snode              current_node;
   Sedge              current_edge;
   Snode              face;

   if (!SEEN(edge,node)) { /* edge wurde von node aus noch nicht durchlaufen */
      face_info = (Internal_Face_Info) malloc(sizeof(struct internal_face_info));
      face = make_node (dual_graph,make_attr(ATTR_DATA,(char*)face_info));
      face_info->right_boundary = face_info->left_boundary = empty_slist;
      target = TARGET_NODE(edge,node);
      face_info->lowpoint = iif(node->nr < target->nr,node,target);
      face_info->highpoint = iif(node->nr > target->nr,node,target);
      if (node->nr < target->nr) {
         SEEN_FROM_SOURCE(edge) = TRUE;
         LEFT_FACE(edge) = face;
         face_info->right_boundary = add_sedge_to_slist(face_info->right_boundary,edge);
      } else {
           SEEN_FROM_TARGET(edge) = TRUE;
           RIGHT_FACE(edge) = face;
           face_info->left_boundary = add_sedge_to_slist(face_info->left_boundary,edge);
      }
      current_node = target;
      current_edge = edge; /* naechste von current_node aus zu durchlaufende Kante */
      while (current_node != node) {
         current_edge = NEXT_EDGE(current_edge,current_node);
         target = TARGET_NODE(current_edge,current_node);
         if (current_node->nr <  target->nr) {
            SEEN_FROM_SOURCE(current_edge) = TRUE;
            LEFT_FACE(current_edge) = face;
            face_info->right_boundary = add_sedge_to_slist(face_info->right_boundary,current_edge);
         } else {
              SEEN_FROM_TARGET(current_edge) = TRUE;
              RIGHT_FACE(current_edge) = face;
              face_info->left_boundary = add_sedge_to_slist(face_info->left_boundary,current_edge);
         }
         current_node = target;
         if (current_node->nr < face_info->lowpoint->nr)
             face_info->lowpoint = current_node;
         if (current_node->nr > face_info->highpoint->nr)
             face_info->highpoint = current_node;
      }
   }
}



Local void compute_dual_edges (Sgraph dual_graph, Snode s, Snode t) /* berechnet und richtet */
                                               /* die dualen Kanten     */
           
{
   Snode face;
   Slist facelist;
   Slist l;
   Sedge edge;

   for_all_nodes (dual_graph,face)
      MARK(face) = FALSE;   /* verhindert mehrfache Kanten */
   end_for_all_nodes (dual_graph,face);

   for_all_nodes (dual_graph,face)
      facelist = empty_slist; /* Nachbarn von face bzgl. seines linken Randes */
      for_slist (LEFT_BOUNDARY(face),l)
         edge = attr_sedge(l);
         if (!MARK(LEFT_FACE(edge))) {
            MARK(LEFT_FACE(edge)) = TRUE;
            facelist = add_snode_to_slist(facelist,LEFT_FACE(edge));
            iif (((edge->snode == s) && (edge->tnode == t)) ||
                 ((edge->snode == t) && (edge->tnode == s)),
                 make_edge (RIGHT_FACE(edge),LEFT_FACE(edge),empty_attr),
                 make_edge (LEFT_FACE(edge),RIGHT_FACE(edge),empty_attr));
         }
      end_for_slist (LEFT_BOUNDARY(face),l);
      for_slist (facelist,l)
         MARK(attr_snode(l)) = FALSE;
      end_for_slist (facelist,l);
      free_slist (facelist);
   end_for_all_nodes (dual_graph,face);
}



Local void make_external_attrs (Sgraph graph, Sgraph dual_graph) /* erzeugt extern      */
                                                  /* sichtbare Attribute */
{
   Snode                node;
   Sedge                edge;
   Snode                face;
   Duality_Edge_Info    edge_info;
   Visibility_Face_Info face_info;

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            edge_info = (Duality_Edge_Info) malloc(sizeof(struct duality_edge_info));
            edge_info->left_face = LEFT_FACE(edge);
            edge_info->right_face = RIGHT_FACE(edge);
            free (INTERNAL_EDGE_INFO(edge));
            set_edgeattrs (edge,make_attr(ATTR_DATA,(char*)edge_info));
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   for_all_nodes (dual_graph,face)
      face_info = (Visibility_Face_Info) malloc(sizeof(struct visibility_face_info));
      face_info->right_boundary = RIGHT_BOUNDARY(face);
      face_info->left_boundary = LEFT_BOUNDARY(face);
      face_info->lowpoint = LOWPOINT(face);
      face_info->highpoint = HIGHPOINT(face);
      free (INTERNAL_FACE_INFO(face));
      set_nodeattrs (face,make_attr(ATTR_DATA,(char*)face_info));
   end_for_all_nodes (dual_graph,face);
}



Sgraph construct_dual_graph (Sgraph graph, Snode s, Snode t) /* Hauptfunktion fuer die Konstruktion */
                                        /* des dualen Graphen                  */
          
{
   Sgraph  dual_graph;
   Snode   node;
   Sedge   edge;

   make_internal_attrs (graph);

   dual_graph = make_graph (empty_attr);
   dual_graph->directed = TRUE;

   for_all_nodes (graph,node) /* Berechnung duale Knoten */
      for_sourcelist (node,edge)
         compute_dual_node (node,edge,graph,dual_graph);
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   compute_dual_edges (dual_graph,s,t); /* Berechnung duale Kanten */

   make_external_attrs (graph,dual_graph);

   return dual_graph;
}



/*************************************************************************/
/*                                                                       */
/*                      END OF FILE: DUAL_GRAPH.C                        */
/*                                                                       */
/*************************************************************************/
