/****************************************************************************\
 *                                                                          *
 *  attrsandlabels.c                                                        *
 *  ----------------                                                        *
 *                                                                          *
 *  author:  a.j. winter (11027)  06/93.                                    *
 *                                                                          *
\****************************************************************************/


#include <std.h>
#include <slist.h>
#include <sgraph.h>
#include <graphed.h>

#include "sattrs.h"


extern	char	buffer[];

 
/****************************************************************************/
/*                                                                          */
/* label routines                                                           */
/*                                                                          */
/****************************************************************************/


Global void ClearLabels (Sgraph graph)
{

  Snode  node;
  Sedge  edge;
  int    solid_edge_type;

/* setzen der Kantenattribute des in GraphEd angezeigten Graphen: */

  solid_edge_type = find_edgetype("#solid");
  for_all_nodes (graph, node) {
     if (node->label) {
        set_nodelabel (node, ""); 
     }
     for_sourcelist (node, edge) {
        if (node->nr < edge->tnode->nr) {
	   edge_set(graphed_edge(edge),
		    EDGE_TYPE, solid_edge_type, 0);
           if (edge->label) {
                 set_edgelabel (edge, "");   
	   }
         }
     }  end_for_sourcelist (node, edge);
  }  end_for_all_nodes (graph, node);

}


Global	void	make_new_node_labels(Sgraph g)
{
   Snode	n;
   int		count;

   count = 0;

   for_all_nodes(g, n) {
      n->label = NEW_LABEL;
      sprintf(n->label, "%2d", count);
      count++;
   } end_for_all_nodes(g, n);
}

Global	void	clear_node_labels(Sgraph g)
{
   Snode	n;

   for_all_nodes(g, n) {
      free(n->label);
      n->label = NULL;
   } end_for_all_nodes(g, n);
}



/*****************************************************************************\
 *                                                                           *
 *  void make_edge_attributes_for_mpg(Sgraph graph)                          *
 *  ---------------------------------------------------                      *
 *                                                                           *
 *  Autor:  a.j. winter (11027)  12/90.                                      *
 *                                                                           *
 *****************************************************************************
 *                                                                           *
 *  Eingabe: Graph vom Typ Sgraph, der in GraphEd angezeigt werden soll.     *
 *                                                                           *
 *  Ausgabe: ---                                                             *
 *                                                                           *
 *                                                                           *
 *  Aufruf : in compute_bipartition (proprak1.c)                             *
 *           mit dem bipartiten Subgraphen und der Liste der Residualnetze.  *
 *                                                                           *
 *                                                                           *
 *  Aufgabe: Setzt in GraphEd die Kantenattribute des Eingabe-Graphen        *
 *           (der in GraphEd im Moment angezeigt wird)                       *
 *           entsprechend des Attributes 'marker' im Aufruf-Graphen um.      *
 *           Mögliche Werte:                                                 *
 *            - 0 (Layer1): durchgezogene Linie                              *
 *            - 1 (Layer2): grob gestrichelte Linie                          *
 *            - 2 (Residualnetze): punktierte Linie                          *
 *                                                                           *
\*****************************************************************************/

Global void make_edge_attributes_for_mpg(Sgraph graph)
{
   Snode node;
   Sedge edge;
   int	 DeletionCount, EdgeCount;
   int   solid_edge_type, dotted_edge_type;

   solid_edge_type = find_edgetype("#solid");
   dotted_edge_type = find_edgetype("#dotted");

   EdgeCount = 0;
   DeletionCount = 0;

/* setzen der Kantenattribute des in GraphEd angezeigten Graphen: */
   for_all_nodes(graph, node) {
      node_set(graphed_node(node), NODE_LABEL, node->label, 0);
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    EdgeCount++;
	    if (attr_data_of_type(edge, Sedge) == empty_sedge) {
	       DeletionCount++;
	       edge_set(graphed_edge(edge),
			EDGE_TYPE, dotted_edge_type, 0);
	    } else {
	       edge_set(graphed_edge(edge),
			EDGE_TYPE, solid_edge_type, 0);
	    } /* endif */
         } /* endif */
      } end_for_sourcelist(node, edge);
   } end_for_all_nodes(graph, node);

/*  set_edgelabel (GET_SEPARATOR_EDGE(currEntry), SEPARATOR_LABEL);
    edge_set (graphed_edge (GET_SEPARATOR_EDGE(currEntry)),
              EDGE_LABEL,            SEPARATOR_LABEL,
              EDGE_LABEL_VISIBILITY, TRUE,
	      EDGE_COLOR           , SEPARATOR_COLOR,
              0);

    set_nodelabel (GET_NODESET_ENTRY(currEntry), LEFT_LABEL);
    node_set (graphed_node (GET_NODESET_ENTRY(currEntry)),
              NODE_LABEL,            LEFT_LABEL,
              NODE_LABEL_VISIBILITY, TRUE,
              NODE_COLOR           , LEFT_COLOR,
              0); */

}



Global void make_edge_attributes_for_crossno(Sgraph graph)
{
   Snode node;
   Sedge edge;
   int	 DeletionCount, EdgeCount;
   int   solid_edge_type, dotted_edge_type;

   solid_edge_type = find_edgetype("#solid");
   dotted_edge_type = find_edgetype("#dotted");

   EdgeCount = 0;
   DeletionCount = 0;

/* setzen der Kantenattribute des in GraphEd angezeigten Graphen: */
   for_all_nodes(graph, node) {
      for_sourcelist(node, edge) {
	 if (node->nr < edge->tnode->nr) {
	    EdgeCount++;
	    if (attr_data_of_type(edge, Sedge) == empty_sedge) {
	       DeletionCount++;
	       edge_set(graphed_edge(edge),
			EDGE_TYPE, dotted_edge_type, 0);
	    } else {
	       edge_set(graphed_edge(edge),
			EDGE_TYPE, solid_edge_type, 0);
	    } /* endif */
         } /* endif */
      } end_for_sourcelist(node, edge);
   } end_for_all_nodes(graph, node);

/* sprintf(buffer, "\n\ngraph has %2d edges, deleted %2d for planarity.\n",
		   EdgeCount, DeletionCount);
   message(buffer); */


}





Global	void	make_edge_labels_for_layers(Sgraph g)
{
   Snode	n;
   Sedge	e;
   char		*edge_label;
 
  
   for_all_nodes(g, n) {
      for_sourcelist(n, e) {
	 if (n->nr > e->tnode->nr) {
	    edge_label = NEW_LABEL;
            if (attr_data_of_type(e, Sedge) != empty_sedge) {
	       sprintf(edge_label, "%2d", 
		    attr_flags(attr_data_of_type(e, Sedge)->snode->graph));
	       set_edgelabel(e, edge_label);
	    } else {
	       edge_label = strsave("");
	       set_edgelabel(e, edge_label);
	    } /* endif */
	 }
      } end_for_sourcelist(n,e);
   } end_for_all_nodes(g,n);
}


