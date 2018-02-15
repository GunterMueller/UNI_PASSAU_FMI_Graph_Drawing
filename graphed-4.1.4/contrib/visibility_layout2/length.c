/********************************************************************************/
/*                                                                              */
/* FILE: LENGTH.C                                                               */
/*                                                                              */
/* Beschreibung: Berechnung der Laenge der laengsten Wege in einem azyklischen  */
/*               Digraphen von einem festen Knoten s zu allen anderen Knoten    */
/*               und Ablage des Ergebnisses in attr_int(node). Die Angabe der   */
/*               Kantenrichtungen kann auf 2 Arten erfolgen: durch Setzen von   */
/*               graph->directed auf TRUE oder durch eine st-Nummerierung der   */
/*               Knoten in node->nr.                                            */
/*                                                                              */
/* benoetigte externe Funktionen: keine                                         */
/*                                                                              */
/********************************************************************************/


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include "visibility_definitions.h"


#define VISITED(node) (attr_int(node)) /* gibt an, ob node bei der Tiefensuche */
                                       /* schon gesehen worden ist             */
#define LEVEL(node) (attr_int(node))   /* Laenge des laengsten Weges zu node   */
#define NODE_SET(node,value) (set_nodeattrs((node),make_attr(ATTR_INTEGER,(value))))


Local Slist topological_list; /* Liste der topologisch sortierten Knoten */



Local void topological_ordering (Sgraph graph, Snode v) /* berechnet rekursiv eine topologi- */
                                          /* sche Sortierung der Nachfolger    */
                                          /* von v                             */
{
   Sedge edge;
   Snode w;

   for_sourcelist (v,edge)
      w = TARGET_NODE(edge,v);
      if ((graph->directed || (v->nr < w->nr)) && !VISITED(w)) {
         NODE_SET(w,TRUE);
         topological_ordering (graph,w);
         topological_list = add_snode_to_slist(topological_list,w);
         topological_list = topological_list->pre;
      }
   end_for_sourcelist (v,edge);
}



void compute_length_of_longest_path (Sgraph graph, Snode s) /* Hauptfunktion fuer die Berech- */
                                              /* nung der laengsten Wege        */
             
         
{
   Snode  node,v;
   Slist  l;
   Sedge  edge;
   Snode  w;

   for_all_nodes (graph,node)
      NODE_SET(node,FALSE); /* VISITED(node) = FALSE */
   end_for_all_nodes (graph,node);

   topological_list = empty_slist;
   NODE_SET(s,TRUE); /* VISITED(s) = TRUE */

   topological_ordering (graph,s);

   topological_list = add_snode_to_slist(topological_list,s);
   topological_list = topological_list->pre; 

   for_all_nodes (graph,node)
      NODE_SET(node,0); /* LEVEL(node) = 0 */
   end_for_all_nodes (graph,node);

   for_slist (topological_list,l)
      v = attr_snode(l);
      for_sourcelist (v,edge)
         w = TARGET_NODE(edge,v);
         if ((graph->directed || (v->nr < w->nr)) && (LEVEL(v)+1 > LEVEL(w)))
            NODE_SET(w,LEVEL(v)+1);
      end_for_sourcelist (v,edge);
   end_for_slist (topological_list,l);

   free_slist (topological_list);
}



/********************************************************************************/
/*                                                                              */
/*                            END OF FILE: LENGTH.C                             */
/*                                                                              */
/********************************************************************************/
