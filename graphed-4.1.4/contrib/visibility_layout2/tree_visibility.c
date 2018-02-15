/*******************************************************************/
/*                                                                 */
/* FILE: TREE_VISIBILITY.C                                         */
/*                                                                 */
/* Beschreibung: Funktionen zur Berechnung einer strong-visibility */
/*               representation fuer Baeume                        */
/*                                                                 */
/* benoetigte externe Funktionen: keine                            */
/*                                                                 */
/*******************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>
#include "visibility_layout2_export.h"
#include "visibility_definitions.h"


#define LEVEL(node) (attr_int(node))
#define vnode_width(node) ((int)node_get(graphed_node(node),NODE_WIDTH))



Local bool test_if_leaf (Snode node, Sgraph tree) /* Test, ob node ein Blatt von tree ist */
            
            
{
   bool  result;
   Slist sourcelist;

   sourcelist = make_slist_of_sourcelist (node);

   result = iif ((LEVEL(node) != 0) && (size_of_slist(sourcelist) == 1),
                 TRUE, FALSE);

   free_slist (sourcelist);

   return result;
}



Local Snode compute_left_child (Snode node) /* am weitesten links liegender */
                                      /* Sohn von node                */
{
   Snode left_child,target_node;
   Sedge edge;

   left_child = empty_snode;

   for_sourcelist (node,edge)
      target_node = TARGET_NODE(edge,node);
      if ((LEVEL(target_node) > LEVEL(node)) &&
          ((left_child == empty_snode) || (target_node->x < left_child->x)))
         left_child = target_node;
   end_for_sourcelist (node,edge);

   return left_child;
}



Local Snode compute_right_child (Snode node) /* am weitesten rechts liegender */
                                       /* Sohn von node                 */
{
   Snode right_child,target_node;
   Sedge edge;

   right_child = empty_snode;

   for_sourcelist (node,edge)
      target_node = TARGET_NODE(edge,node);
      if ((LEVEL(target_node) > LEVEL(node)) &&
          ((right_child == empty_snode) || (target_node->x > right_child->x)))
         right_child = target_node;
   end_for_sourcelist (node,edge);

   return right_child;
}



Local void construct_representation (Sgraph tree, Snode node, int *x) /* berechnet rekursiv */
                                                  /* die Darstellung    */
            
          
{
   Snode        target_node,left_child,right_child,source,target;
   Sedge        edge;
   int          xl,xr; /* x-Koordinaten von Knotensegmenten */
   Edgeline     edgeline;

   if (test_if_leaf(node,tree)) { /* node ist ein Blatt */
      node->x = (*x) * visibility_layout2_settings.horizontal_distance;
      node->y = LEVEL(node) * visibility_layout2_settings.vertical_distance;
      node_set (graphed_node(node),
                ONLY_SET,
                NODE_SIZE, visibility_layout2_settings.height + 1,visibility_layout2_settings.height,
                NODE_NEI, SPECIAL_NODE_EDGE_INTERFACE,
                NODE_TYPE, find_nodetype("#box"),
                0);
      (*x)++;
   } else {
      for_sourcelist (node,edge)
         target_node = TARGET_NODE(edge,node);
         if (LEVEL(target_node) == -1) { /* target_node ist Sohn von node */
            set_nodeattrs (target_node,make_attr(ATTR_INTEGER,LEVEL(node) + 1));
                                         /* LEVEL(target_node) */
            construct_representation (tree,target_node,x);
         }
      end_for_sourcelist (node,edge);

      left_child = compute_left_child (node);
      right_child = compute_right_child (node);
      xl = left_child->x - vnode_width(left_child)/2;
      xr = right_child->x + vnode_width(right_child)/2;
      node->x = (xl + xr)/2;
      node->y = LEVEL(node) * visibility_layout2_settings.vertical_distance;
      node_set (graphed_node(node),
                ONLY_SET,
                NODE_SIZE, xr - xl + 1, visibility_layout2_settings.height,
                NODE_NEI, SPECIAL_NODE_EDGE_INTERFACE,
                NODE_TYPE, find_nodetype("#box"),
                0);

      for_sourcelist (node,edge)
         target_node = TARGET_NODE(edge,node);
         if (LEVEL(target_node) > LEVEL(node)) { /* edge liegt unterhalb von node */
            edgeline = (Edgeline) edge_get(graphed_edge(edge),EDGE_LINE);
            free_edgeline (edgeline);
            source = sedge_real_source(edge);
            target = sedge_real_target(edge);
            if (node == source) {
               edgeline = new_edgeline (target_node->x,node->y + visibility_layout2_settings.height/2);
               edgeline = add_to_edgeline (edgeline,target_node->x,target_node->y - visibility_layout2_settings.height/2);
            } else {
               edgeline = new_edgeline (target_node->x,target_node->y - visibility_layout2_settings.height/2);
               edgeline = add_to_edgeline (edgeline,target_node->x,node->y + visibility_layout2_settings.height/2);
            }
            edgeline = add_to_edgeline (edgeline,target->x,target->y);
            edgeline = add_to_edgeline (edgeline,source->x,source->y);
            edge_set (graphed_edge(edge),
                      ONLY_SET,
                      EDGE_LINE,edgeline,
                      0);
         }
      end_for_sourcelist (node,edge);
   }
}



Local void compute_size_of_drawing (Sgraph tree, int x) /* berechnet Groesse */
                                            /* der Darstellung   */
         
{
   int   low;
   Snode node;
   int   width,height;

   low = 0;
   for_all_nodes (tree,node)
      low = maximum (low,LEVEL(node));
   end_for_all_nodes (tree,node);

   width = x;
   height = low + 1;

   message ("Width of drawing: %d\n",width);
   message ("Height of drawing: %d\n",height);
   message ("Required area: %d\n",width * height);
}



void tree_s_visibility (Sgraph tree, Snode root) /* Hauptfunktion fuer diesen Algorithmus */
            
            
{
   Snode node;
   int   x;

   for_all_nodes (tree,node)
      set_nodeattrs (node,make_attr(ATTR_INTEGER,-1)); /* Knoten wurde noch */
   end_for_all_nodes (tree,node);                      /* nicht betrachtet  */

   set_nodeattrs (root,make_attr(ATTR_INTEGER,0)); /* LEVEL(root) */

   x = 0; /* x-Koordinate des naechsten einzuordnenden Blattes */

   construct_representation (tree,root,&x);

   compute_size_of_drawing (tree,x);
}



/*******************************************************************/
/*                                                                 */
/*                  END OF FILE: TREE_VISIBILITY.C                 */
/*                                                                 */
/*******************************************************************/
