/*************************************************************************/
/*                                                                       */
/* FILE: BLOCK_CUTPOINT_TREE.C                                           */
/*                                                                       */
/* Beschreibung: Funktionen zur Konstruktion und zum Loeschen des block- */
/*               cutpoint-trees eines zusammenhaengenden Graphen; jeder  */
/*               Knoten in einem Block enthaelt in attr_snode einen Zei- */
/*               ger auf den zugehoerigen Knoten im Graphen, ebenso die  */
/*               Knoten des block-cutpoint-trees, die cutpoints darstel- */
/*               len; jede Kante des bct enthaelt in attr_snode einen    */
/*               Zeiger auf den cutpoint im entsprechenden Block; jeder  */
/*               Knoten des bct, der zu einem Block gehoert, enthaelt in */
/*               attr_sgraph einen Zeiger auf den Block; der Eingabegraph*/
/*               enthaelt in attr_slist eine Liste aller cutpoints       */
/*                                                                       */
/* benoetigte externe Funktionen: keine                                  */
/*                                                                       */
/*************************************************************************/



#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include "visibility_definitions.h"
#include "TTvisibility.h"


typedef struct internal_node_info { /* interne Knotenattribute */
           int    dfsnum; /* DFS-Nummer */
           int    lowpt; /* Lowpoint */
           bool   visited; /* Knoten schon besucht? */
           Sedge  tree_edge; /* Baumkante zum aktuellen Nachfolger */
           Snode  bct_cutpoint_node; /* zugehoeriger Knoten eines        */
                                     /* cutpoints im block-cutpoint-tree */
} *Internal_Node_Info;


#define INTERNAL_NODE_INFO(node) (attr_data_of_type((node),Internal_Node_Info))
#define DFSNUM(node) (INTERNAL_NODE_INFO(node)->dfsnum)
#define LOWPT(node) (INTERNAL_NODE_INFO(node)->lowpt)
#define VISITED(node) (INTERNAL_NODE_INFO(node)->visited)
#define TREE_EDGE(node) (INTERNAL_NODE_INFO(node)->tree_edge)
#define BCT_CUTPOINT_NODE(node) (INTERNAL_NODE_INFO(node)->bct_cutpoint_node)
#define SEEN(edge) (attr_int(edge))


Local Sgraph block_cutpoint_tree;
Local Slist  nodelist; /* besuchte und noch nicht einem Block      */
                       /* zugeordnete Knoten in umgek. Reihenfolge */
Local Slist  edgelist; /* besuchte und noch nicht einem Block      */
                       /* zugeordnete Kanten in umgek. Reihenfolge */
Local int    count; /* naechste DFS-Nummer */



Local void make_internal_attrs (Sgraph graph) /* erzeugt intern benoetigte Attribute */
             
{
   Snode              node;
   Internal_Node_Info node_info;

   for_all_nodes (graph,node)
      node_info = (Internal_Node_Info) malloc(sizeof(struct internal_node_info));
      node_info->visited = FALSE;
      node_info->tree_edge = empty_sedge;
      node_info->bct_cutpoint_node = empty_snode;
      set_nodeattrs (node,make_attr(ATTR_DATA,(char*)node_info));
   end_for_all_nodes (graph,node);
}



Local void extend_block_cutpoint_tree (Snode node) /* erweitert den bct um einen */
                                             /* Block mit cutpoint node    */
{
   Sgraph block; /* aktueller Block */
   Snode  graph_node; /* Knoten im Graphen */
   Snode  block_node; /* zu graph_node gehoerender Knoten im Block */
   Snode  bct_block_node; /* Knoten im bct, der einen Block darstellt */
   Snode  bct_cutpoint_node; /* Knoten im bct, der zu einem cutpoint gehoert */
   Snode  cutpoint,node1;
   Slist  cutpoint_list = empty_slist; /* cutpoints != node, die in block liegen */
   Slist  l;
   Sedge  graph_edge; /* Kante im Graphen */
   Sedge  block_edge; /* zu graph_edge gehoerende Kante im Block */

   block = make_graph (empty_attr);
   block->directed = FALSE;

   do { /* Bestimmung der Knoten von block */
      graph_node = attr_snode(nodelist);
      if (graph_node != node)
         nodelist = subtract_first_element_from_slist(nodelist);
      block_node = make_node (block,make_attr_snode(graph_node));
      block_node->x = graph_node->x;
      block_node->y = graph_node->y;
      block_node->nr = graph_node->nr;
      set_nodelabel (block_node,graph_node->label);
      graph_node->iso = block_node;
      if ((BCT_CUTPOINT_NODE(graph_node) != empty_snode) && (graph_node != node))
         cutpoint_list = add_snode_to_slist(cutpoint_list,graph_node);
   } while (graph_node != node);

   do { /* Bestimmung der Kanten von block */
      graph_edge = attr_sedge(edgelist);
      edgelist = subtract_first_element_from_slist(edgelist);
      block_edge = make_edge(graph_edge->snode->iso,
                             graph_edge->tnode->iso,empty_attr);
      set_edgelabel (block_edge,graph_edge->label);
   } while (graph_edge != TREE_EDGE(node));

   bct_block_node = make_node (block_cutpoint_tree,make_attr_sgraph(block));

   for_slist (cutpoint_list,l) /* bct_block_node in block-cutpoint */
                               /* tree einhaengen                  */
      cutpoint = attr_snode(l);
      make_edge (bct_block_node,BCT_CUTPOINT_NODE(cutpoint),
                 make_attr_snode(cutpoint->iso));
   end_for_slist (cutpoint_list,l);

   free_slist (cutpoint_list);

   if (BCT_CUTPOINT_NODE(node) == empty_snode) { /* node bisher noch kein cutpoint */
      bct_cutpoint_node = make_node (block_cutpoint_tree,make_attr_snode(node));
      BCT_CUTPOINT_NODE(node) = bct_cutpoint_node;
   }
   make_edge (BCT_CUTPOINT_NODE(node),bct_block_node,make_attr_snode(node->iso));

   for_all_nodes (block,node1)
      attr_snode(node1)->iso = empty_snode;
   end_for_all_nodes (block,node1);
}



Local void depth_first_search (Snode v) /* rekursive Tiefensuche */
        
{
   Sedge edge;
   Snode w;

   for_sourcelist (v,edge)
      if (!SEEN(edge)) {
         set_edgeattrs (edge,make_attr(ATTR_INTEGER,TRUE)); /* SEEN(edge) = TRUE */
         edgelist = add_sedge_to_slist(edgelist,edge);
         edgelist = edgelist->pre;
         w = TARGET_NODE(edge,v);
         if (!VISITED(w)) {
            VISITED(w) = TRUE;
            TREE_EDGE(v) = edge;
            DFSNUM(w) = count++;
            LOWPT(w) = DFSNUM(v);
            nodelist = add_snode_to_slist(nodelist,w);
            nodelist = nodelist->pre;
            depth_first_search (w);
            if (LOWPT(w) == DFSNUM(v))
               extend_block_cutpoint_tree (v);
            else LOWPT(v) = minimum (LOWPT(v),LOWPT(w));
         }
         else LOWPT(v) = minimum (LOWPT(v),DFSNUM(w));
      }
   end_for_sourcelist (v,edge);
}



Sgraph construct_block_cutpoint_tree (Sgraph graph, Snode s) /* Hauptfunktion fuer die */
                                               /* Berechnung des bct     */
         
{
   Snode node,bct_source;
   Sedge edge,bct_first_edge;
   Slist sourcelist;
   Slist cutpoint_list = empty_slist;

   make_internal_attrs (graph);
   block_cutpoint_tree = make_graph (empty_attr);
   block_cutpoint_tree->directed = TRUE;

   nodelist = edgelist = empty_slist;
   count = 1;
   VISITED(s) = TRUE;
   DFSNUM(s) = LOWPT(s) = count++;
   nodelist = add_snode_to_slist(nodelist,s);
   for_all_nodes (graph,node)
      for_sourcelist (node,edge)
         set_edgeattrs (edge,make_attr(ATTR_INTEGER,FALSE)); /* SEEN(edge) = FALSE */
      end_for_sourcelist (node,edge);
   end_for_all_nodes (graph,node);

   depth_first_search (s);

   free_slist (nodelist);
   free_slist (edgelist);

   bct_source = BCT_CUTPOINT_NODE(s); /* bisherige Quelle des bct */
   bct_first_edge = bct_source->slist;
   sourcelist = make_slist_of_sourcelist (bct_source);
   if (slist_contains_exactly_one_element(sourcelist)) { /* s kein cutpoint */
      remove_node (bct_source);
      BCT_CUTPOINT_NODE(s) = empty_snode;
   } else { /* Block zu Wurzel des bct machen */
      make_edge (bct_first_edge->tnode,bct_source,bct_first_edge->attrs);
      remove_edge (bct_first_edge);
   }
   free_slist (sourcelist);

   for_all_nodes (graph,node)
      if (BCT_CUTPOINT_NODE(node) != empty_snode) /* cutpoints sammeln */
         cutpoint_list = add_snode_to_slist(cutpoint_list,node);
      free (INTERNAL_NODE_INFO(node));
   end_for_all_nodes (graph,node);

   set_graphattrs (graph,make_attr_slist(cutpoint_list));

   return block_cutpoint_tree;
}



void remove_block_cutpoint_tree (Sgraph block_cutpoint_tree) /* loescht den block- */
                                                      /* cutpoint tree und  */
{                                                     /* alle Bloecke       */
   Snode  bct_source,block_node,cutpoint_node;
   Sedge  edge1,edge2;
   Slist  nodelist;

   bct_source = compute_source (block_cutpoint_tree);
   nodelist = add_snode_to_slist(empty_slist,bct_source);

   while (nodelist != empty_slist) {
      block_node = attr_snode(nodelist);
      nodelist = subtract_first_element_from_slist(nodelist);
      remove_graph (attr_sgraph(block_node));
      for_sourcelist (block_node,edge1)
         cutpoint_node = edge1->tnode;
         for_sourcelist (cutpoint_node,edge2)
            nodelist = add_snode_to_slist(nodelist,edge2->tnode);
         end_for_sourcelist (cutpoint_node,edge2);
      end_for_sourcelist (block_node,edge1);
   }

   remove_graph (block_cutpoint_tree);
}



/*************************************************************************/
/*                                                                       */
/*                  END OF FILE: BLOCK_CUTPOINT_TREE.C                   */
/*                                                                       */
/*************************************************************************/
