/**************************************************************************/
/*                                                                        */
/* FILE: TT_VISIBILITY.C                                                  */
/*                                                                        */
/* Beschreibung: Hauptfunktionen fuer die Algorithmen von Tamassia/Tollis */
/*               und Rosenstiehl/Tarjan                                   */
/*                                                                        */
/* ben. ext. Funktionen: st_numbering                                     */
/*                       construct_dual_graph                             */
/*                       compute_length_of_longest_path                   */
/*                       compute_source                                   */
/*                       construct_w_visibility_representation            */
/*                       construct_biconnected_graph                      */
/*                       free_visibility_node_info                        */
/*                       free_visibility_edge_info                        */
/*                       free_visibility_face_info                        */
/*                       construct_epsilon_visibility_biconnected_graph   */
/*                       construct_epsilon_visibility_representation      */
/*                                                                        */
/**************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <algorithms.h>
#include <limits.h>
#include "visibility_definitions.h"
#include "TTvisibility.h"


#define VISIBILITY_NODE_INFO(node) (attr_data_of_type((node),Visibility_Node_Info))
#define LEVEL(node) (VISIBILITY_NODE_INFO(node)->level)
#define XL(node) (VISIBILITY_NODE_INFO(node)->xl)
#define XR(node) (VISIBILITY_NODE_INFO(node)->xr)
#define IS_DUMMY_NODE(node) (VISIBILITY_NODE_INFO(node)->is_dummy)



Sgraph save_nodeattrs (Sgraph graph) /* sichert voruebergehend Knotenattribute */
             
{
   Sgraph save_graph;
   Snode  node,save_node;

   save_graph = make_graph (empty_attr);
   for_all_nodes (graph,node)
      save_node = make_node (save_graph,node->attrs);
      node->iso = save_node;
   end_for_all_nodes (graph,node);

   return save_graph;
}


void reset_nodeattrs (Sgraph graph, Sgraph save_graph) /* setzt gesicherte Knoten- */
                                        /* attribute wieder ein     */
                  
{
   Snode node;

   for_all_nodes (graph,node)
      set_nodeattrs (node,node->iso->attrs);
      remove_node (node->iso);
      node->iso = empty_snode;
   end_for_all_nodes (graph,node);
   remove_graph (save_graph);
}



Sgraph TT_w_visibility_biconnected (Sgraph graph, Snode s, Snode t, Snode dummy_node, Slist dummy_edges, int contains_dummies, Visibility_Type type)
                                       /* ruft Funktionen fuer die     */
                                       /* Algorithmen von Tam/Tol und  */
                                       /* Ros/Tar auf; Eingabe ist ein */       
                                       /* 2-fach zus.hgd. Graph        */
                                 
                     
{
   Sgraph  dual_graph;
   Sgraph  save_graph;
   Snode   dual_source;
   Snode   node;

   if (type != Kant_weak)
      st_numbering (graph,s,t);

   embed (graph);

   dual_graph = construct_dual_graph (graph,s,t);

   for_all_nodes (graph,node)
      free_slist (attr_slist(node));
   end_for_all_nodes (graph,node);

   compute_length_of_longest_path (graph,s);

   dual_source = compute_source (dual_graph);
   save_graph = save_nodeattrs (dual_graph); /* Zwischensicherung der Knotenattr. */
   compute_length_of_longest_path (dual_graph,dual_source);

   for_all_nodes (dual_graph,node)
      if ((type == TT_weak) || (type == TT_epsilon))
         set_nodeattrs (node,make_attr(ATTR_INTEGER,2*attr_int(node)));
      set_nodeattrs (node,make_attr(ATTR_INTEGER,attr_int(node)+1));
   end_for_all_nodes (dual_graph,node);

   construct_w_visibility_representation (graph,s,t,dummy_node,dummy_edges,contains_dummies,type);

   reset_nodeattrs (dual_graph,save_graph); /* Wiedereinsetzen der gesicherten */
                                            /* Knotenattribute                 */

   return dual_graph; /* dualer Graph wird noch fuer                 */
                      /* epsilon-visibility representation benoetigt */
}



void compute_size_of_TT_drawing (Sgraph graph) /* berechnet Groesse der Darstellung */
             
{
   int   left,right,down,up,width,height,area;
   Snode node;

   left = down = INT_MAX;
   right = up = INT_MIN;

   for_all_nodes (graph,node)
      if (!IS_DUMMY_NODE(node)) { /* fuer epsilon-visibility */
         down = minimum (down,LEVEL(node));
         up = maximum (up,LEVEL(node));
         left = minimum (left,XL(node));
         right = maximum (right,XR(node));
      }
   end_for_all_nodes (graph,node);

   width = right - left + 1;
   height = up - down + 1;
   area = width * height;

   message ("Width of drawing: %d\n",width);
   message ("Height of drawing: %d\n",height);
   message ("Required area: %d\n\n",area);
}



void TT_w_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge, Visibility_Type type) /* Hauptfunktion fuer die   */
                                                 /* w-visibility-Algorithmen */
                                                 /* von Tam/Tol und Ros/Tar  */
                           
                     
{
   bool   biconnected;
   Slist  dummy_edges = empty_slist;
   Slist  l;
   Sgraph dual_graph;

   biconnected = test_sgraph_biconnected (graph);

   if (!biconnected)
      dummy_edges = construct_biconnected_graph (graph);

   if (dummy_edge != empty_sedge)
      dummy_edges = add_sedge_to_slist(dummy_edges,dummy_edge);

   dual_graph = TT_w_visibility_biconnected (graph,s,t,empty_snode,dummy_edges,!biconnected,type);

   free_visibility_face_info (dual_graph);
   remove_graph (dual_graph);

   compute_size_of_TT_drawing (graph);

   free_visibility_node_info (graph);

   if (!compression_used) /* Compression wurde nicht angewendet */
      free_visibility_edge_info (graph);

   for_slist (dummy_edges,l)
      remove_edge (attr_sedge(l));
   end_for_slist (dummy_edges,l);
   free_slist (dummy_edges);
}



void TT_epsilon_visibility (Sgraph graph, Snode s, Snode t, Sedge dummy_edge) /* Hauptfunktion fuer den   */
                                                  /* epsilon-visibility-Algo- */
                                                  /* rithmus von Tam/Tol      */
                  
{
   Snode  dummy_node = empty_snode;
   Slist  dummy_edges = empty_slist;
   Slist  l;
   Sgraph dual_graph;
   Slist  sourcelist;

   if (s == empty_snode) { /* graph ist nicht 2-fach zusammenhaengend */
      dummy_node = construct_epsilon_visibility_biconnected_graph (graph);
      for_sourcelist (dummy_node,dummy_edge)
         dummy_edges = add_sedge_to_slist(dummy_edges,dummy_edge);
      end_for_sourcelist (dummy_node,dummy_edge);
      s = dummy_node;
      sourcelist = make_slist_of_sourcelist (s);
      t = highest_node_in_nodelist (sourcelist);
      free_slist (sourcelist);
   } else if (dummy_edge != empty_sedge)
      dummy_edges = add_sedge_to_slist(dummy_edges,dummy_edge);

   dual_graph = TT_w_visibility_biconnected (graph,s,t,dummy_node,dummy_edges,FALSE,TT_epsilon);

   construct_epsilon_visibility_representation (graph,dual_graph);

   free_visibility_face_info (dual_graph);
   remove_graph (dual_graph);

   compute_size_of_TT_drawing (graph);

   free_visibility_node_info (graph);
   free_visibility_edge_info (graph);

   for_slist (dummy_edges,l)
      remove_edge (attr_sedge(l));
   end_for_slist (dummy_edges,l);
   free_slist (dummy_edges);

   if (dummy_node != empty_snode)
      remove_node (dummy_node);
}



/**************************************************************************/
/*                                                                        */
/*                     END OF FILE: TT_VISIBILITY.C                       */
/*                                                                        */
/**************************************************************************/
