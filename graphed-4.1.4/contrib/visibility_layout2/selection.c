/*************************************************************************/
/*                                                                       */
/* FILE: SELECTION.C                                                     */
/*                                                                       */
/* Beschreibung: Funktionen zur st-Auswahl und zur Bestimmung bestimmter */
/*               Knoten in einem Graphen                                 */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <algorithms.h>
#include "visibility_layout2_export.h"
#include "visibility_definitions.h"



Snode highest_node_in_graph (Sgraph graph) /* bestimmt hoechsten Knoten in graph */
             
{
   Snode highest_node,node;

   highest_node = first_node_in_graph (graph);

   for_all_nodes (graph,node)
      if (node->y < highest_node->y)
         highest_node = node;
   end_for_all_nodes (graph,node);

   return highest_node;
}



Snode highest_node_in_nodelist (Slist nodelist) /* bestimmt hoechsten Knoten */
                                          /* in nodelist               */
{
   Snode highest_node,node;
   Slist l;

   highest_node = attr_snode(nodelist);

   for_slist (nodelist,l)
      node = attr_snode(l);
      if (node->y < highest_node->y)
         highest_node = node;
   end_for_slist (nodelist,l);

   return highest_node;
}



Snode compute_source (Sgraph graph) /* bestimmt Quelle eines gerichteten Graphen */
             
{
   Snode node;

   for_all_nodes (graph,node)
      if (node->tlist == empty_sedge)
         return node;
   end_for_all_nodes (graph,node);

   return empty_snode;
}



Slist compute_sinks (Sgraph graph) /* bestimmt alle Senken eines gerichteten Graphen */
             
{
   Slist sinks;
   Snode node;

   sinks = empty_slist;

   for_all_nodes (graph,node)
      if (node->slist == empty_sedge)
         sinks = add_snode_to_slist(sinks,node);
   end_for_all_nodes (graph,node);

   return sinks;
}



Local Snode node_with_maximal_degree (Slist nodelist) /* bestimmt aus nodelist Knoten */
                                                /* mit hoechstem Knotengrad     */
{
   Snode result_node,node;
   int   max_degree;
   Slist sourcelist,l;

   result_node = attr_snode(nodelist);
   sourcelist = make_slist_of_sourcelist (result_node);
   max_degree = size_of_slist (sourcelist);
   free_slist (sourcelist);

   for_slist (nodelist,l)
      node = attr_snode(l);
      sourcelist = make_slist_of_sourcelist (node);
      if (size_of_slist (sourcelist) > max_degree) {
         result_node = node;
         max_degree = size_of_slist (sourcelist);
      }
      free_slist (sourcelist);
   end_for_slist (nodelist,l);

   return result_node;
}



Local Snode node_with_minimal_degree (Slist nodelist) /* bestimmt aus nodelist Knoten */
                                                /* mit niedrigstem Knotengrad   */
{
   Snode result_node,node;
   int   min_degree;
   Slist sourcelist,l;

   result_node = attr_snode(nodelist);
   sourcelist = make_slist_of_sourcelist (result_node);
   min_degree = size_of_slist (sourcelist);
   free_slist (sourcelist);

   for_slist (nodelist,l)
      node = attr_snode(l);
      sourcelist = make_slist_of_sourcelist (node);
      if (size_of_slist (sourcelist) < min_degree) {
         result_node = node;
         min_degree = size_of_slist (sourcelist);
      }
      free_slist (sourcelist);
   end_for_slist (nodelist,l);

   return result_node;
}



Local Sedge edge_with_maximal_sum_of_degrees (Sgraph graph) /* bestimmt Kante mit  */
                                                     /* maximaler Gradsumme */
{
   Snode node;
   Sedge result_edge,edge;
   int   maximal_sum;
   Slist sourcelist1,sourcelist2;

   node = first_node_in_graph (graph);
   result_edge = node->slist;
   sourcelist1 = make_slist_of_sourcelist (result_edge->snode);
   sourcelist2 = make_slist_of_sourcelist (result_edge->tnode);
   maximal_sum = size_of_slist (sourcelist1) + size_of_slist (sourcelist2);
   free_slist (sourcelist1);
   free_slist (sourcelist2);

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            sourcelist1 = make_slist_of_sourcelist (edge->snode);
            sourcelist2 = make_slist_of_sourcelist (edge->tnode);
            if (size_of_slist(sourcelist1) + size_of_slist(sourcelist2) > maximal_sum) {
               result_edge = edge;
               maximal_sum = size_of_slist (sourcelist1) + size_of_slist (sourcelist2);
            }
            free_slist (sourcelist1);
            free_slist (sourcelist2);
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   return result_edge;
}



Local Sedge edge_with_minimal_sum_of_degrees (Sgraph graph) /* bestimmt Kante mit  */
                                                     /* minimaler Gradsumme */
{
   Snode node;
   Sedge result_edge,edge;
   int   minimal_sum;
   Slist sourcelist1,sourcelist2;

   node = first_node_in_graph (graph);
   result_edge = node->slist;
   sourcelist1 = make_slist_of_sourcelist (result_edge->snode);
   sourcelist2 = make_slist_of_sourcelist (result_edge->tnode);
   minimal_sum = size_of_slist (sourcelist1) + size_of_slist (sourcelist2);
   free_slist (sourcelist1);
   free_slist (sourcelist2);

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) {
            sourcelist1 = make_slist_of_sourcelist (edge->snode);
            sourcelist2 = make_slist_of_sourcelist (edge->tnode);
            if (size_of_slist(sourcelist1) + size_of_slist(sourcelist2) < minimal_sum) {
               result_edge = edge;
               minimal_sum = size_of_slist (sourcelist1) + size_of_slist (sourcelist2);
            }
            free_slist (sourcelist1);
            free_slist (sourcelist2);
         }
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   return result_edge;
}



S_T_Selection_Result s_t_selection (Sgraph_proc_info info) /* Auswahl von s und t */
                      
{
   Slist                nodelist,sourcelist,sourcelist1,sourcelist2;
   Snode                node1,node2;
   Snode                s,t;
   Sedge                dummy_edge = empty_sedge;
   Sedge                edge;
   bool                 success = FALSE; /* war Auswahl schon erfolgreich? */
   S_T_Selection_Result result;

   if (info->selected == SGRAPH_SELECTED_GROUP) { /* Knoten sind markiert */
      nodelist = info->selection.group;
      if (size_of_slist(nodelist) == 2) {
         node1 = attr_snode(nodelist);
         node2 = attr_snode(nodelist->suc);
         s = iif (node1->y > node2->y,node1,node2);
         t = iif (node1->y > node2->y,node2,node1);
         sourcelist = make_slist_of_sourcelist (s);
         if (contains_slist_element(sourcelist,make_attr_snode(t)) == empty_slist) {
            dummy_edge = make_edge (s,t,empty_attr);
            if (planarity(info->sgraph) != SUCCESS) {
                message ("Cannot connect the selected nodes: Graph would not be planar!\nTwo other nodes have been selected!\n\n");
                remove_edge (dummy_edge);
                dummy_edge = empty_sedge;
            }
            else success = TRUE;
         } 
         else success = TRUE;
         free_slist (sourcelist);
      }
      free_slist (nodelist);
   }

   if (!success && (info->selected == SGRAPH_SELECTED_SEDGE)) { /* Kante ist markiert */
      edge =  info->selection.sedge;
      s = iif (edge->snode->y > edge->tnode->y,edge->snode,edge->tnode);
      t = TARGET_NODE(edge,s);
      success = TRUE;
   }

   if (!success) {
      switch (visibility_layout2_settings.st_nodes) { /* welches Auswahlkriterium? */
         case maximal_degree:
            nodelist = make_slist_of_sgraph (info->sgraph);
            s = node_with_maximal_degree (nodelist);
            nodelist = subtract_from_slist (nodelist,make_attr_snode(s));
            t = node_with_maximal_degree (nodelist);
            sourcelist = make_slist_of_sourcelist (s);
            if (contains_slist_element(sourcelist,make_attr_snode(t)) == empty_slist) {
               dummy_edge = make_edge (s,t,empty_attr);
               if (planarity(info->sgraph) != SUCCESS) {
                  remove_edge (dummy_edge);
                  dummy_edge = empty_sedge;
                  t = node_with_maximal_degree (sourcelist);
               }
            }
            free_slist (sourcelist);
            free_slist (nodelist);
            break;

         case minimal_degree:
            nodelist = make_slist_of_sgraph (info->sgraph);
            s = node_with_minimal_degree (nodelist);
            nodelist = subtract_from_slist (nodelist,make_attr_snode(s));
            t = node_with_minimal_degree (nodelist);
            sourcelist = make_slist_of_sourcelist (s);
            if (contains_slist_element(sourcelist,make_attr_snode(t)) == empty_slist) {
               dummy_edge = make_edge (s,t,empty_attr);
               if (planarity(info->sgraph) != SUCCESS) {
                  remove_edge (dummy_edge);
                  dummy_edge = empty_sedge;
                  t = node_with_minimal_degree (sourcelist);
               }
            }
            free_slist (sourcelist);
            free_slist (nodelist);
            break;

         case maximal_sum_of_degrees:
            edge = edge_with_maximal_sum_of_degrees (info->sgraph);
            sourcelist1 = make_slist_of_sourcelist (edge->snode);
            sourcelist2 = make_slist_of_sourcelist (edge->tnode);
            s = iif (size_of_slist(sourcelist1)>=size_of_slist(sourcelist2),
                     edge->snode,edge->tnode);
            t = TARGET_NODE(edge,s);
            free_slist (sourcelist1);
            free_slist (sourcelist2);
            break;

         case minimal_sum_of_degrees:
            edge = edge_with_minimal_sum_of_degrees (info->sgraph);
            sourcelist1 = make_slist_of_sourcelist (edge->snode);
            sourcelist2 = make_slist_of_sourcelist (edge->tnode);
            s = iif (size_of_slist(sourcelist1)<=size_of_slist(sourcelist2),
                     edge->snode,edge->tnode);
            t = TARGET_NODE(edge,s);
            free_slist (sourcelist1);
            free_slist (sourcelist2);
            break;
      }
   }

   result = (S_T_Selection_Result) malloc(sizeof(struct s_t_selection_result));
   result->s = s;
   result->t = t;
   result->dummy_edge = dummy_edge;

   return result;
}



Snode root_selection (Sgraph_proc_info info) /* Auswahl der Wurzel bei "Tree-strong-visibility" */
                      
{
   Snode root;

   if (info->selected == SGRAPH_SELECTED_SNODE) /* Knoten ist markiert */
      root = info->selection.snode;
   else
      root = highest_node_in_graph (info->sgraph);

   return root;
}



/*************************************************************************/
/*                                                                       */
/*                        END OF FILE: SELECTION.C                       */
/*                                                                       */
/*************************************************************************/
