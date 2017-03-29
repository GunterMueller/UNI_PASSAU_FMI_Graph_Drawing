/***********************************************************************/
/* FILE: VISIBILITY_CALL.C                                             */
/*                                                                     */
/* Beschreibung: von hier aus werden alle Algorithmen gestartet; ent-  */
/*               haelt auch eine Funktion, die Sichtbarkeitsdarstel-   */
/*               lungen fuer Graphen mit weniger als 3 Knoten berech-  */
/*               net.                                                  */
/*                                                                     */
/* benoetigte externe Funktionen: st_selection                         */
/*                                root_selection                       */
/*                                RT_w_visibility                      */
/*                                TT_w_visibility                      */
/*                                TT_epsilon_visibility                */
/*                                Kant_w_visibility                    */
/*                                Nummenmaa_w_visibility               */
/*                                tree_s_visibility                    */
/*                                save_visibility_layout2_settings             */
/*                                show_visibility_subframe             */
/*                                                                     */
/***********************************************************************/


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include <sgraph/algorithms.h>
#include "visibility_layout2_export.h"
#include "visibility_definitions.h"
#include "TTvisibility.h"
#include "Kant_visibility.h"
#include "Nummenmaa_visibility.h"
#include "tree_visibility.h"


 
Local void small_graph_representation (Sgraph graph)  /* berechnet Darstellung fuer Graphen */
                                               /* mit hoechstens 2 Knoten            */
{
   Snode    s;
   Snode    t;
   Sedge    edge,new_edge;

   if ((s = first_node_in_graph(graph)) == (t = last_node_in_graph(graph)))
                                    /* graph enthaelt nur 1 Knoten */
      node_set (graphed_node(s),
                ONLY_SET,
                NODE_SIZE, visibility_layout2_settings.height,
		           visibility_layout2_settings.height,
                NODE_TYPE, find_nodetype("#box"),
                0);
   else {
      s->x = t->x;
      s->y = 0;
      t->y = visibility_layout2_settings.vertical_distance;
      node_set (graphed_node(s),
                ONLY_SET,
                NODE_SIZE, visibility_layout2_settings.height,
		           visibility_layout2_settings.height,
                NODE_TYPE, find_nodetype("#box"),
                NODE_NEI, SPECIAL_NODE_EDGE_INTERFACE,
                0);
      node_set (graphed_node(t),
                ONLY_SET,
                NODE_SIZE, visibility_layout2_settings.height,
		           visibility_layout2_settings.height,
                NODE_TYPE, find_nodetype("#box"),
                NODE_NEI, SPECIAL_NODE_EDGE_INTERFACE,
                0);
      edge = s->slist;
      new_edge = make_edge (s,t,edge->attrs);
      new_edge->label = edge->label;
      remove_edge (edge);
   }
}



void call_RT_weak_visibility (Sgraph_proc_info info)
{
   Sgraph               graph;
   Slist                nodelist;
   S_T_Selection_Result s_t_result;
   Snode                s,t;
   Sedge                dummy_edge;

   graph = info->sgraph; 

   if (!w_visibility_error(graph)) {

      nodelist = make_slist_of_sgraph (graph);

      if (size_of_slist(nodelist) <= 2)
         small_graph_representation (graph); 
      else {
         s_t_result = s_t_selection (info);
         s = s_t_result->s;
         t = s_t_result->t;
         dummy_edge = s_t_result->dummy_edge;
         free (s_t_result);
         TT_w_visibility (graph,s,t,dummy_edge,RT_weak);
      }

      free_slist (nodelist);

      info->repaint = TRUE;
      info->recompute = TRUE;
      info->recenter = TRUE;
   }
}



void call_TT_weak_visibility (Sgraph_proc_info info)
{
   Sgraph               graph;
   Slist                nodelist;
   S_T_Selection_Result s_t_result;
   Snode                s,t;
   Sedge                dummy_edge;

   graph = info->sgraph; 

   if (!w_visibility_error(graph)) {

      nodelist = make_slist_of_sgraph (graph);

      if (size_of_slist(nodelist) <= 2)
         small_graph_representation (graph); 
      else {
         s_t_result = s_t_selection (info);
         s = s_t_result->s;
         t = s_t_result->t;
         dummy_edge = s_t_result->dummy_edge;
         free (s_t_result);
         TT_w_visibility (graph,s,t,dummy_edge,TT_weak);
      }

      free_slist (nodelist);

      info->repaint = TRUE;
      info->recompute = TRUE;
      info->recenter = TRUE;
   }
}



void call_TT_epsilon_visibility (Sgraph_proc_info info)
{
   Sgraph               graph;
   Slist                nodelist;
   S_T_Selection_Result s_t_result;
   Snode                s,t;
   Sedge                dummy_edge;

   graph = info->sgraph;

   if (!epsilon_visibility_error (graph)) {

      nodelist = make_slist_of_sgraph (graph);

      if (size_of_slist(nodelist) <= 2)
         small_graph_representation (graph);
      else {
         if (test_sgraph_biconnected(graph)) {
            s_t_result = s_t_selection (info);
            s = s_t_result->s;
            t = s_t_result->t;
            dummy_edge = s_t_result->dummy_edge;
            free (s_t_result);
         }
         else {
            s = t = empty_snode;
            dummy_edge = empty_sedge;
         }
         TT_epsilon_visibility (graph,s,t,dummy_edge);
      }

      free_slist (nodelist);

      info->repaint = TRUE;
      info->recompute = TRUE;
      info->recenter = TRUE;
   }
}



void call_Kant_weak_visibility (Sgraph_proc_info info)
{
   Sgraph               graph;
   Slist                nodelist;
   S_T_Selection_Result s_t_result;
   Snode                s,t;
   Sedge                dummy_edge; 

   graph = info->sgraph;

   if (!w_visibility_error(graph)) {

      nodelist = make_slist_of_sgraph (graph);

      if (size_of_slist(nodelist) <= 2)
         small_graph_representation (graph); 
      else {
         s_t_result = s_t_selection (info);
         s = s_t_result->s;
         t = s_t_result->t;
         dummy_edge = s_t_result->dummy_edge;
         free (s_t_result);
         Kant_w_visibility (graph,s,t,dummy_edge);
      }

      free_slist (nodelist);

      info->repaint = TRUE;
      info->recompute = TRUE;
      info->recenter = TRUE;
   }
}



void call_Nummenmaa_weak_visibility (Sgraph_proc_info info)
{
   Sgraph               graph;
   Slist                nodelist;
   S_T_Selection_Result s_t_result;
   Snode                s,t;
   Sedge                dummy_edge;

   graph = info->sgraph; 

   if (!w_visibility_error(graph)) {

      nodelist = make_slist_of_sgraph (graph);

      if (size_of_slist(nodelist) <= 2)
         small_graph_representation (graph); 
      else {
         s_t_result = s_t_selection (info);
         s = s_t_result->s;
         t = s_t_result->t;
         dummy_edge = s_t_result->dummy_edge;
         free (s_t_result);
         Nummenmaa_w_visibility (graph,s,t,dummy_edge);
      }

      free_slist (nodelist);

      info->repaint = TRUE;
      info->recompute = TRUE;
      info->recenter = TRUE;
   }
}



void call_tree_strong_visibility (Sgraph_proc_info info)
{
   Sgraph tree;
   Slist  nodelist;
   Snode  root;

   tree = info->sgraph;

   if (!tree_visibility_error(tree)) {

      nodelist = make_slist_of_sgraph (tree);

      if (size_of_slist(nodelist) <= 2)
         small_graph_representation (tree);
    
      else {
         root = root_selection (info);
         tree_s_visibility (tree,root);
      }

      free_slist (nodelist);

      info->repaint = TRUE;
      info->recompute = TRUE;
      info->recenter = TRUE;
   }
}



void menu_RT_w_visibility (Menu menu, Menu_item menu_item)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_RT_weak_visibility, NULL);
}



void menu_TT_w_visibility (Menu menu, Menu_item menu_item)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_TT_weak_visibility, NULL);
}



void menu_TT_epsilon_visibility (Menu menu, Menu_item menu_item)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_TT_epsilon_visibility, NULL);
}



void menu_Kant_w_visibility (Menu menu, Menu_item menu_item)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_Kant_weak_visibility, NULL);
}



void menu_Nummenmaa_w_visibility (Menu menu, Menu_item menu_item)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_Nummenmaa_weak_visibility, NULL);
}



void menu_tree_s_visibility (Menu menu, Menu_item menu_item)
{
   save_visibility_layout2_settings ();
   call_sgraph_proc (call_tree_strong_visibility, NULL);
}



void menu_visibility_layout2_settings (Menu menu, Menu_item menu_item)
{
   show_visibility_subframe (NULL);
}


/***********************************************************************/
/*                                                                     */
/*                    END OF FILE: VISIBILITY_CALL.C                   */
/*                                                                     */
/***********************************************************************/
