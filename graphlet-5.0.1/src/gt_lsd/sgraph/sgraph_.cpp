/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

/************************************************************************/
/*									*/
/*				stdraph.c				*/
/*									*/
/************************************************************************/
/*									*/
/*	STANDARDIZED DATA STRUCTURE FOR GRAPHS				*/
/*									*/
/*	Further Information : N.A.					*/
/*									*/
/************************************************************************/

#include "std.h"
#include "sgraph.h"


Sgraph		make_graph (Attributes attrs)
{
    Sgraph	new_graph;
	
    new_graph = (Sgraph)malloc (sizeof(struct sgraph));

    new_graph->nodes    = empty_node;
    new_graph->label    = nil;
    new_graph->directed = false;
#ifdef GRAPHED_POINTERS
    new_graph->graphed  = NULL;
#endif

    new_graph->make_node_proc    = NULL;
    new_graph->make_edge_proc    = NULL;
    new_graph->remove_node_proc  = NULL;
    new_graph->remove_edge_proc  = NULL;
    new_graph->remove_graph_proc = NULL;

    new_graph->iso = empty_sgraph;

    new_graph->attrs    = attrs;
    new_graph->saved_attrs = empty_slist;
    new_graph->attrs_key = NULL;

    return new_graph;
}


Global	Sgraph	copy_sgraph (Sgraph sgraph)
{
    Sgraph	new_sgraph;
    Snode	snode;
    Sedge	sedge;

    new_sgraph = make_graph (sgraph->attrs);
    new_sgraph->directed = sgraph->directed;

    new_sgraph->iso = sgraph;
    sgraph->iso     = new_sgraph;

    for_all_nodes (sgraph, snode) {
	copy_snode (snode);
    } end_for_all_nodes (sgraph, snode);

    for_all_nodes (sgraph, snode) {
	for_sourcelist (snode, sedge) if (sgraph->directed || unique_edge(sedge) ) {
	    copy_sedge (sedge);
	} end_for_sourcelist (snode, sedge);
    } end_for_all_nodes (sgraph, snode);

    return new_sgraph;
}


void	remove_graph (Sgraph graph)
{
    Slist	l;

    for_slist (graph->saved_attrs, l) {
	Saved_sgraph_attrs	saved;
	saved = attr_data_of_type (l, Saved_sgraph_attrs);
	if (saved->remove_graph_proc != NULL && saved->remove_graph_proc != graph->remove_graph_proc) {
	    saved->remove_graph_proc (graph);
	}
    } end_for_slist (graph->saved_attrs, l);

    if (graph->remove_graph_proc != NULL) {
	graph->remove_graph_proc (graph);
    }

    while (graph->nodes != empty_node)
	remove_node (graph->nodes);

#if __SUNPRO_CC == 0x401
    free ((char*) graph);
#else
    free (graph);
#endif
}


Global	void	set_graphlabel (Sgraph graph, char *text)
{
    graph->label = text;
}


Global	void	set_graphattrs (Sgraph graph, Attributes attrs)
{
    graph->attrs = attrs;
}




Global	void	set_graph_directed (Sgraph graph, int directed)
{
    graph->directed = directed;
}



Global	void	print_graph (FILE *file, Sgraph g,
			     void (*print_graph_attributes) (FILE *file, Sgraph g),
			     void (*print_node_attributes) (FILE *file, Snode n),
			     void (*print_edge_attributes) (FILE *file, Sedge e))
{
    Snode	n;
    Sedge	e;
	
    fprintf (file, "GRAPH \"%s\" = %s\n",
	     g->label, iif(g->directed, "DIRECTED", "UNDIRECTED"));
    if (print_graph_attributes) {
	print_graph_attributes (file, g);
    }
    for_all_nodes (g,n) {
	fprintf (file, "%d ", n->nr);
	if (print_node_attributes) {
	    print_node_attributes (file, n);
	}
	fprintf (file, "\"%s\" ",  n->label);
	for_sourcelist (n,e) {
	    if (g->directed ||
		(e->snode->nr < e->tnode->nr)) {
		/* print edge				*/
		/* Caution : in undirected graphs, each*/
		/* edge is printed only once !		*/
		fprintf (file,"%d ", e->tnode->nr);
		if (print_edge_attributes) {
		    print_edge_attributes (file, e);
		}
		fprintf (file," \"%s\" ", e->label);
	    }
	} end_for_sourcelist (n,e);
	fprintf (file, ";\n");
    } end_for_all_nodes (g,n);
    fprintf (file, "END\n");
}
