
/*************************************************************************/
/*                                                                       */
/* FILE: TT_FREE_MEMORY.C                                                */
/*                                                                       */
/* Beschreibung: gibt Speicher fuer extern benoetigte Attribute bei den  */
/*               Algorithmen von Ros/Tar, Tam/Tol und Kant frei          */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include "TTvisibility.h"



void free_visibility_node_info (Sgraph graph)
{
   Snode node;

   for_all_nodes (graph,node)
      free (attr_data_of_type(node,Visibility_Node_Info));
   end_for_all_nodes (graph,node);
}


void free_visibility_edge_info (Sgraph graph)
{
   Snode node;
   Sedge edge;

   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         if (unique_edge(edge)) free (attr_data_of_type(edge,Visibility_Edge_Info));
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);
}


void free_visibility_face_info (Sgraph dual_graph)
{
   Snode face;

   for_all_nodes (dual_graph,face)
      free (attr_data_of_type(face,Visibility_Face_Info)->right_boundary);
      free (attr_data_of_type(face,Visibility_Face_Info)->left_boundary);
      free (attr_data_of_type(face,Visibility_Face_Info));
   end_for_all_nodes (dual_graph,face);
}



/*************************************************************************/
/*                                                                       */
/*                     END OF FILE: TT_FREE_MEMORY.C                     */
/*                                                                       */
/*************************************************************************/
