/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

/************************************************************************/
/*									*/
/*				stdnode.c				*/
/*									*/
/************************************************************************/

#include "std.h"
#include "sgraph.h"


Snode		make_node (Sgraph graph, Attributes attrs)
{
    return make_node_with_number (graph, attrs, -1);
}



Snode		make_node_with_number (Sgraph graph, Attributes attrs, int nr)
{
    Snode		node, new_node;
    static	int	highest_number_up_to_now = 1;

    if (nr != -1) {
	/* Look for a node with the given number.	*/
	/* If one exists, return it.			*/
	for_all_nodes (graph, node)
	    if (node->nr == nr) return node;
	end_for_all_nodes (graph, node);
    }
	
    /* We have not found a node with nr -- create a new one	*/
	
    new_node = (Snode)malloc (sizeof(struct snode));

    /* Insert the new node					*/
	
    if (graph->nodes == empty_node) {
	graph->nodes  = new_node;
	new_node->suc = new_node;
	new_node->pre = new_node;
    } else {
	new_node->pre      = last_node_in_graph  (graph);
	new_node->suc      = first_node_in_graph (graph);
	new_node->pre->suc = new_node;
	new_node->suc->pre = new_node;
    }
	
    new_node->slist = empty_edge;
    new_node->tlist = empty_edge;
    new_node->graph = graph;
    new_node->nr    = iif (nr == -1, ++highest_number_up_to_now, nr);
    new_node->label = nil;
    new_node->x     = -1;
    new_node->y     = -1;
#ifdef GRAPHED_POINTERS
    new_node->graphed = NULL;
#endif
#ifdef SGRAGRA_POINTERS
    new_node->embedding = NULL;
#endif
    new_node->filter = empty_node;


    if (nr > highest_number_up_to_now)
	highest_number_up_to_now = nr;

    new_node->attrs = attrs;
    new_node->saved_attrs = empty_slist;
    new_node->attrs_key = NULL;

    if (new_node->graph->make_node_proc != NULL) {
	new_node->graph->make_node_proc (new_node);
    }

    return	new_node;
}


Global	Snode	copy_snode (Snode snode)
{
    Snode 	new_snode;

    new_snode = make_node (snode->graph->iso, snode->attrs);
    set_node_xy (new_snode, snode->x, snode->y);
    set_nodelabel (new_snode, strsave(snode->label));

    new_snode->iso = snode->iso;
    snode->iso     = new_snode;

    return	snode;
}



Global	void	remove_node (Snode node)
{
    Sgraph	graph;
    Sedge	edge;
    Slist	l;

    for_slist (node->graph->saved_attrs, l) {
	Saved_sgraph_attrs	saved;
	saved = attr_data_of_type (l, Saved_sgraph_attrs);
	if (saved->remove_node_proc != NULL &&
	    saved->remove_node_proc != node->graph->remove_node_proc) {
	    saved->remove_node_proc (node);
	}
    } end_for_slist (node->graph->saved_attrs, l);

    if (node->graph->remove_node_proc != NULL) {
	node->graph->remove_node_proc (node);
    }

    while ( (edge = node->slist) != empty_edge)
	remove_edge (edge);
    while ( (edge = node->tlist) != empty_edge)
	remove_edge (edge);

    graph = node->graph;
    if (node->suc == node)
	graph->nodes = empty_node;
    else {
	node->pre->suc = node->suc;
	node->suc->pre = node->pre;
	if (node == graph->nodes)
	    graph->nodes = node->suc;
    }
	
#if __SUNPRO_CC == 0x401
    free ((char*) node);
#else
    free (node);
#endif
}


Global	void	set_nodelabel (Snode node, char *text)
{
    node->label = text;
}


Global	void	set_nodeattrs (Snode node, Attributes attrs)
{
    node->attrs = attrs;
}


Global	void	set_node_xy (Snode node, int x, int y)
{
    node->x = x;
    node->y = y;
}


Global	void	set_nodefilter (Snode node, Snode filter)
{
    node->filter = filter;
#ifdef GRAPHED_POINTERS
    node->graphed = node->filter->graphed;
#endif
}


