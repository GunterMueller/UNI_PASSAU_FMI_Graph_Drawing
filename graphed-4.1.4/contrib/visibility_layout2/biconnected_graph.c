/*************************************************************************/
/*                                                                       */
/* FILE: BICONNECTED_GRAPH.C                                             */
/*                                                                       */
/* Beschreibung: macht einen einfach zusammenhaengenden Graphen zweifach */
/*               zusammenhaengend und gibt Liste der eingefuegten Kanten */
/*               zurueck;                                                */
/*               Quelle: "G. Kant: Triangulating planar graphs while     */
/*                        minimizing the maximum degree (1992)"          */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <algorithms.h>
#include "visibility_definitions.h"


typedef struct internal_node_info { /* interne Knotenattribute */
           int    dfsnum; /* DFS-Nummer */
           int    lowpt; /* Lowpoint */
           bool   visited; /* Knoten schon gesehen? */
           Slist  edge_rotation; /* Einbettung */
           Sedge  tree_edge; /* aktuelle Baumkante zum Nachfolger */
           int    number_of_blocks; /* Knoten != s ist cutpoint,  */
                                    /* falls number_of_blocks > 0 */
} *Internal_Node_Info;

typedef struct internal_edge_info { /* interne Kantenattribute */
           Snode  source;
           bool   seen; /* Kante schon gesehen? */
           int    block; /* Block, in der sich Kante befindet */
           bool   is_dummy; /* eingefuegte Kante? */
           Slist  position_in_source_rotation; /* Pos. in der Rotation von source */
           Slist  position_in_target_rotation; /* Pos. in der Rotation von target */
} *Internal_Edge_Info;


#define INTERNAL_NODE_INFO(node) (attr_data_of_type((node),Internal_Node_Info))
#define DFSNUM(node) (INTERNAL_NODE_INFO(node)->dfsnum)
#define LOWPT(node) (INTERNAL_NODE_INFO(node)->lowpt)
#define VISITED(node) (INTERNAL_NODE_INFO(node)->visited)
#define EDGE_ROTATION(node) (INTERNAL_NODE_INFO(node)->edge_rotation)
#define TREE_EDGE(node) (INTERNAL_NODE_INFO(node)->tree_edge)
#define NUMBER_OF_BLOCKS(node) (INTERNAL_NODE_INFO(node)->number_of_blocks)
#define INTERNAL_EDGE_INFO(edge) (attr_data_of_type((edge),Internal_Edge_Info))
#define SOURCE(edge) (INTERNAL_EDGE_INFO(edge)->source)
#define SEEN(edge) (INTERNAL_EDGE_INFO(edge)->seen)
#define BLOCK(edge) (INTERNAL_EDGE_INFO(edge)->block)
#define IS_DUMMY(edge) (INTERNAL_EDGE_INFO(edge)->is_dummy)
#define POSITION_IN_SOURCE_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_source_rotation)
#define POSITION_IN_TARGET_ROTATION(edge) (INTERNAL_EDGE_INFO(edge)->position_in_target_rotation)
#define POSITION(edge,node) (iif((node)==SOURCE(edge),POSITION_IN_SOURCE_ROTATION(edge),POSITION_IN_TARGET_ROTATION(edge)))


Local int   count; /* naechste DFS-Nummer */
Local int   block; /* Block-Identifikation fuer Kanten */
Local Slist edgelist; /* besuchte und noch nicht identifizierte */
                      /* Kanten in umgekehrter Reihenfolge      */
Local Slist cutpoint_list; /* cutpoints in der Reihenfolge, in  */
                           /* der sie als solche erkannt wurden */



Local void make_internal_attrs (Sgraph graph) /* erzeugt intern benoetigte Attribute */
             
{
   Snode              node;
   Sedge              edge;
   Slist              l;
   Internal_Node_Info node_info;
   Internal_Edge_Info edge_info;

   for_all_nodes (graph,node)
      node_info = (Internal_Node_Info) malloc(sizeof(struct internal_node_info));
      node_info->visited = FALSE;
      node_info->edge_rotation = attr_slist(node);
      node_info->number_of_blocks = 0;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
      for_sourcelist (node,edge)
         if (unique_edge (edge)) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->source = empty_snode;
            edge_info->seen = FALSE;
            edge_info->is_dummy = FALSE;
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



Local void compute_block_edges (Snode node) /* identifiziert Kanten */
                                      /* des aktuellen Blocks */
{
   Sedge edge;

   do {
      edge = attr_sedge(edgelist);
      edgelist = subtract_first_element_from_slist(edgelist);
      BLOCK(edge) = block;
   } while (edge != TREE_EDGE(node));
}



Local void depth_first_search (Snode v) /* rekursive Tiefensuche */
        
{
   Slist l;
   Sedge edge;
   Snode w;

   for_slist (EDGE_ROTATION(v),l)
      edge = attr_sedge(l);
      if (!SEEN(edge)) {
         SEEN(edge) = TRUE;
         edgelist = add_sedge_to_slist(edgelist,edge);
         edgelist = edgelist->pre;
         w = TARGET_NODE(edge,v);
         if (!VISITED(w)) {
            VISITED(w) = TRUE;
            TREE_EDGE(v) = edge;
            DFSNUM(w) = count++;
            LOWPT(w) = DFSNUM(v);
            depth_first_search (w);
            if (LOWPT(w) == DFSNUM(v)) { /* Block gefunden */
               NUMBER_OF_BLOCKS(v)++;
               compute_block_edges (v); /* Kanten des Blocks identifizieren */
               block++;
            }
            else LOWPT(v) = minimum (LOWPT(v),LOWPT(w));
         }
         else LOWPT(v) = minimum (LOWPT(v),DFSNUM(w));
      }
   end_for_slist (EDGE_ROTATION(v),l);

   if (NUMBER_OF_BLOCKS(v) > 0)
      cutpoint_list = add_snode_to_slist(cutpoint_list,v);
}



Local void compute_blocks (Sgraph graph) /* berechnet alle Bloecke */
             
{
   Snode s;

   edgelist = cutpoint_list = empty_slist;
   count = 1;
   block = 1;
   s = first_node_in_graph (graph);
   VISITED(s) = TRUE;
   DFSNUM(s) = LOWPT(s) = count++;

   depth_first_search (s);

   free_slist (edgelist);

   if (NUMBER_OF_BLOCKS(s) <= 1) /* s kein cutpoint */
      cutpoint_list = subtract_last_element_from_slist(cutpoint_list);
}



Local void construct_dummy_edges (void) /* fuegt Dummy-Kanten zwischen */
{                                   /* verschiedenen Bloecken ein  */
   Slist              removed_dummies; /* Dummies, die wieder entfernt werden koennen */
   Slist              l,l1,l2;
   Snode              cutpoint,node1,node2;
   Sedge              first_edge,edge1,edge2,dummy_edge;
   Internal_Edge_Info edge_info;

   removed_dummies = empty_slist;

   for_slist (cutpoint_list,l1)
      cutpoint = attr_snode(l1);
      first_edge = attr_sedge(EDGE_ROTATION(cutpoint)); /* 1. betrachtete Kante */
      for_slist (EDGE_ROTATION(cutpoint),l2)
         edge1 = attr_sedge(l2);
         edge2 = attr_sedge(l2->suc);
         node1 = TARGET_NODE(edge1,cutpoint);
         node2 = TARGET_NODE(edge2,cutpoint);
         if ((edge2 != first_edge) && (BLOCK(edge1) != BLOCK(edge2))) {
            edge_info = (Internal_Edge_Info) malloc(sizeof(struct internal_edge_info));
            edge_info->is_dummy = TRUE;
            dummy_edge = make_edge (node1,node2,make_attr(ATTR_DATA,(char*)edge_info));
            if (DFSNUM(node1) < DFSNUM(cutpoint)) { /* node1 moeglicherweise spaeter */
                                                    /* zu betrachtender cutpoint     */
               EDGE_ROTATION(node1) = add_sedge_to_slist(POSITION(edge1,node1),dummy_edge);
               BLOCK(dummy_edge) = BLOCK(edge1);
            } else if (DFSNUM(node2) < DFSNUM(cutpoint)) { /* node2 moeglicherweise spaeter */
                                                           /* zu betrachtender cutpoint     */
               EDGE_ROTATION(node2) = add_sedge_to_slist(POSITION(edge2,node2)->suc,dummy_edge);
               BLOCK(dummy_edge) = BLOCK(edge2);
            }
            if (IS_DUMMY(edge1)) /* edge1 kann wieder entfernt werden */
               removed_dummies = add_sedge_to_slist(removed_dummies,edge1);
            if (IS_DUMMY(edge2)) /* edge2 kann wieder entfernt werden */
               removed_dummies = add_sedge_to_slist(removed_dummies,edge2);
         }
      end_for_slist (EDGE_ROTATION(cutpoint),l2);
   end_for_slist (cutpoint_list,l1);

   for_slist (removed_dummies,l)
      dummy_edge = attr_sedge(l);
      free (INTERNAL_EDGE_INFO(dummy_edge));
      remove_edge (dummy_edge);
   end_for_slist (removed_dummies,l);
   free_slist (removed_dummies);
}



Slist construct_biconnected_graph (Sgraph graph) /* Hauptfunktion fuer dieses Modul */
             
{
   Slist        dummy_edges;
   Snode        node;
   Sedge        edge;

   embed (graph);

   make_internal_attrs (graph);

   compute_blocks (graph); /* bestimmt Bloecke */

   construct_dummy_edges (); /* fuegt Dummy-Kanten ein */

   free_slist (cutpoint_list);

   dummy_edges = empty_slist;
   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge) && IS_DUMMY(edge))
            dummy_edges = add_sedge_to_slist(dummy_edges,edge);
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   for_all_nodes (graph,node)
      free_slist (EDGE_ROTATION(node));
      free (INTERNAL_NODE_INFO(node));
      for_sourcelist (node,edge)
         if (unique_edge(edge)) free (INTERNAL_EDGE_INFO(edge));
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   return dummy_edges;
}



/*************************************************************************/
/*                                                                       */
/*                  END OF FILE: BICONNECTED_GRAPH.C                     */
/*                                                                       */
/*************************************************************************/
