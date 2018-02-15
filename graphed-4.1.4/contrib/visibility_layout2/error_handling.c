/******************************************************************************/
/*                                                                            */
/* FILE: ERROR_HANDLING.C                                                     */
/*                                                                            */
/* Beschreibung: Dieses File enthaelt Funktionen, die testen, ob der Eingabe- */
/*               graph eine bestimmte Sichtbarkeitsdarstellung zulaesst.      */
/*               Falls nicht, so wird eine entsprechende Fehlermeldung ausge- */
/*               geben.                                                       */
/*                                                                            */
/* benoetigte externe Funktion: construct_block_cutpoint_tree                 */
/*                              remove_block_cutpoint_tree                    */
/*                                                                            */
/******************************************************************************/



#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <algorithms.h>
#include "visibility_definitions.h"
#include "TTvisibility.h"

extern int number_of_nodes (Sgraph graph); /* springembedder_rf/spring.c */


bool w_visibility_error (Sgraph graph) /* gibt FALSE zurueck, falls graph eine w-visibility */
                                /* representation zulaesst, sonst TRUE               */
{
   if (graph->nodes == empty_snode) {
      error ("The graph is empty!\n");
      return TRUE;
   }

   if (!test_sgraph_connected(graph)) {
      error ("The graph is not connected!\n");
      return TRUE;
   }

   if (graph->directed) {
      error ("The graph must be undirected!\n");
      return TRUE;
   }

   switch (planarity(graph)) {
      case SUCCESS:
         return FALSE;
      case NONPLANAR:
         error ("The graph is nonplanar!\n");
         return TRUE;
      case SELF_LOOP:
         error ("The graph contains self-loops!\n");
         return TRUE;
      case MULTIPLE_EDGE:
         error ("The graph contains multiple edges!\n");
         return TRUE;
      case NO_MEM:
         error ("There is not enough memory!\n");
         return TRUE;
   }
   return TRUE; /* should not be reached */
}



Local Snode construct_test_graph (Sgraph graph, Slist cutpoint_list) /* verbindet Dummy-Knoten mit */
                                                       /* allen cutpoints von graph  */
                     
{
   Snode dummy_node,cutpoint;
   Slist l;

   dummy_node = make_node (graph,empty_attr);

   for_slist (cutpoint_list,l)
      cutpoint = attr_snode(l);
      make_edge (dummy_node,cutpoint,empty_attr);
   end_for_slist (cutpoint_list,l);

   return dummy_node;
}



bool epsilon_visibility_error (Sgraph graph) /* gibt FALSE zurueck, falls graph eine epsilon-  */
                                      /* visibility representation zulaesst, sonst TRUE */
{
   Sgraph block_cutpoint_tree;
   Slist  cutpoint_list;
   Snode  dummy_node;
   bool   result;

   if (w_visibility_error(graph))
      return TRUE;

   if (test_sgraph_biconnected(graph))
      return FALSE;

   block_cutpoint_tree = construct_block_cutpoint_tree (graph,first_node_in_graph(graph));
   remove_block_cutpoint_tree (block_cutpoint_tree);
   cutpoint_list = attr_slist(graph);
   dummy_node = construct_test_graph (graph,cutpoint_list);
   free_slist (cutpoint_list);

   switch (planarity(graph)) {
         case SUCCESS:
            result = FALSE;
            break;
         case NONPLANAR:
            error ("The graph does not allow an epsilon-visibility representation!\n");
	    result = TRUE;
            break;
         case NO_MEM:
            error ("There is not enough memory!\n");
            result = TRUE;
            break;
         default:
            result = TRUE;
            break;
   }

   remove_node (dummy_node);

   return result;
   
}



int number_of_edges (Sgraph graph) /* bestimmt Anzahl der Kanten von graph */
             
{
   int   number;
   Snode node;
   Sedge edge;

   number = 0;

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (graph->directed || unique_edge(edge))
            number++;
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   return number;
}



Local bool is_tree (Sgraph graph) /* testet, ob graph ein Baum ist */
             
{
   return iif ((test_sgraph_connected(graph)) && 
               (number_of_edges(graph) == number_of_nodes(graph) - 1),
               TRUE,FALSE);
}



bool tree_visibility_error (Sgraph graph) /* gibt FALSE zurueck, falls tree-strong-visibility */
                                   /* angewendet werden kann, sonst TRUE               */
{
   if (graph->nodes == empty_snode) {
      error ("The graph is empty!\n");
      return TRUE;
   }

   if (graph->directed) {
      error ("The graph must be undirected!\n");
      return TRUE;
   }

   if (!is_tree(graph)) {
      error ("The graph is not a tree!\n");
      return TRUE;
   }

   return FALSE;
}



/******************************************************************************/
/*                                                                            */
/*                        END OF FILE: ERROR_HANDLING.C                       */
/*                                                                            */
/******************************************************************************/
