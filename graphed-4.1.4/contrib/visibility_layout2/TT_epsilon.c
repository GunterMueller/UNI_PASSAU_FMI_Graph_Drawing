/********************************************************************************/
/*                                                                              */
/* FILE: TT_EPSILON.C                                                           */
/*                                                                              */
/* Beschreibung: enthaelt Funktionen, die fuer TT-epsilon-visibility benoe-     */
/*               tigt werden.                                                   */
/*                                                                              */
/* benoetigte externe Funktionen: construct_block_cutpoint_tree                 */
/*                                remove_block_cutpoint_tree                    */
/*                                highest_node_in_graph                         */
/*                                compute_sinks                                 */
/*                                compute_source                                */
/*                                                                              */
/********************************************************************************/


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include "visibility_layout2_export.h"
#include "visibility_definitions.h"
#include "TTvisibility.h"


#define VISIBILITY_NODE_INFO(node) (attr_data_of_type((node),Visibility_Node_Info))
#define LEVEL(node) (VISIBILITY_NODE_INFO(node)->level)
#define XL(node) (VISIBILITY_NODE_INFO(node)->xl)
#define XR(node) (VISIBILITY_NODE_INFO(node)->xr)
#define VISIBILITY_EDGE_INFO(edge) (attr_data_of_type((edge),Visibility_Edge_Info))
#define EDGE_X(edge) (VISIBILITY_EDGE_INFO(edge)->x)
#define IS_DUMMY_EDGE(edge) (VISIBILITY_EDGE_INFO(edge)->is_dummy)
#define VISIBILITY_FACE_INFO(face) (attr_data_of_type((face),Visibility_Face_Info))
#define RIGHT_BOUNDARY(face) (VISIBILITY_FACE_INFO(face)->right_boundary)
#define LEFT_BOUNDARY(face) (VISIBILITY_FACE_INFO(face)->left_boundary)
#define LOWPOINT(face) (VISIBILITY_FACE_INFO(face)->lowpoint)
#define HIGHPOINT(face) (VISIBILITY_FACE_INFO(face)->highpoint)
#define FACE_LEVEL(face) (EDGE_X(attr_sedge(RIGHT_BOUNDARY(face))) - 1)
#define SOURCE(edge) (iif((edge)->snode->nr < (edge)->tnode->nr,(edge)->snode,(edge)->tnode))



Snode construct_epsilon_visibility_biconnected_graph (Sgraph graph)
                    /* macht graph durch Einfuegen eines Dummy-Knotens und Kan- */ 
                    /* ten, die den Dummy-Knoten mit graph verbinden, zweifach  */
                    /* zusammenhaengend; der Dummy-Knoten wird als Resultat zu- */ 
                    /* zurueckgegeben.                                          */
{
   Sgraph       block_cutpoint_tree;
   Slist        bct_leaves,l;
   Snode        dummy_node;
   Snode        block,source_block;
   Snode        block_cutpoint;
   Snode        block_target_node;
   Sedge        dummy_edge;
   Slist        sourcelist;

   block_cutpoint_tree = construct_block_cutpoint_tree (graph,highest_node_in_graph(graph));
   free_slist (attr_slist(graph)); /* Liste der cutpoints wird hier nicht benoetigt */

   bct_leaves = compute_sinks (block_cutpoint_tree);
                             /* Blaetter des block-cutpoint-trees */

   dummy_node = make_node (graph,empty_attr);

   for_slist (bct_leaves,l)
      block = attr_snode(l);
      block_cutpoint = attr_snode(block->tlist); /* cutpoint von block */
      block_target_node = TARGET_NODE(block_cutpoint->slist,block_cutpoint);
                                      /* Nachbar von block_cutpoint in block */
      dummy_edge = make_edge (dummy_node,attr_snode(block_target_node),empty_attr);
   end_for_slist (bct_leaves,l);

   source_block = compute_source (block_cutpoint_tree);
                                /* Wurzel des block-cutpoint-trees */
  
   sourcelist = make_slist_of_sourcelist (source_block);

   if (slist_contains_exactly_one_element(sourcelist)) { /* auch source_block */
                                                         /* ist ein Blatt     */
      block_cutpoint = attr_snode(source_block->slist); 
                                /* cutpoint von source_block */
      block_target_node = TARGET_NODE(block_cutpoint->slist,block_cutpoint);
                                /* Nachbar von block_cutpoint in source_block */

      dummy_edge = make_edge (dummy_node,attr_snode(block_target_node),empty_attr);
   }

   free_slist (sourcelist);
   free_slist (bct_leaves);
   remove_block_cutpoint_tree (block_cutpoint_tree);

   return dummy_node;
}



void construct_epsilon_visibility_representation (Sgraph graph, Sgraph dual_graph)
                            /* konstruiert aus einer w-visibility represen-  */
                            /* tation eine epsilon-visibility representation */
{
   Slist dual_sinks;
   Snode dual_sink;
   Snode face;
   Slist l;
   Sedge edge;
   Snode node;

   dual_sinks = compute_sinks (dual_graph);
   dual_sink = attr_snode(dual_sinks); /* aeusseres Face der Darstellung */
   free_slist (dual_sinks);

   for_all_nodes (dual_graph,face)
      if (face != dual_sink) {
         for_slist (RIGHT_BOUNDARY(face),l)
            edge = attr_sedge(l);
            if (!IS_DUMMY_EDGE(edge)) {
               node = SOURCE(edge);
               if (node != LOWPOINT(face)) {
                  XL(node) = FACE_LEVEL(face);
                  node->x = (XL(node) + XR(node)) *
		    visibility_layout2_settings.horizontal_distance / 2;
                  node_set (graphed_node(node),
                            ONLY_SET,
                            NODE_SIZE,
			      (XR(node) - XL(node)) *
			      visibility_layout2_settings.horizontal_distance + 1,
			      visibility_layout2_settings.height,
                            0);
               }
            }
         end_for_slist (RIGHT_BOUNDARY(face),l);

         for_slist (LEFT_BOUNDARY(face),l)
            edge = attr_sedge(l);
            if (!IS_DUMMY_EDGE(edge)) {
               node = SOURCE(edge);
               if (node != LOWPOINT(face)) {
                   XR(node) = FACE_LEVEL(face);
                   node->x = (XL(node) + XR(node)) *
		     visibility_layout2_settings.horizontal_distance / 2;
                   node_set (graphed_node(node),
                             ONLY_SET,
                             NODE_SIZE, (XR(node) - XL(node)) *
			       visibility_layout2_settings.horizontal_distance + 1,
			       visibility_layout2_settings.height,
                             0);
               }
            }
          end_for_slist (LEFT_BOUNDARY(face),l);
      }
   end_for_all_nodes (dual_graph,face);
}


/********************************************************************************/
/*                                                                              */
/*                           END OF FILE: TT_EPSILON.C                          */
/*                                                                              */
/********************************************************************************/
