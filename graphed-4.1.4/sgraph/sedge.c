/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

/************************************************************************/
/*									*/
/*				stdedge.c				*/
/*									*/
/************************************************************************/
/*									*/
/*	STANDARDIZED DATA STRUCTURE FOR EDGES IN A GRAPH		*/
/*									*/
/*	Further Information : N.A.					*/
/*									*/
/************************************************************************/

#include "std.h"
#include "sgraph.h"


Local	Sedge	_make_edge   (Snode snode, Snode tnode, Attributes attrs, int is_a_double);
Local	void	_remove_edge (Sedge edge, int is_a_double);


Global	Sedge	make_edge (Snode snode, Snode tnode, Attributes attrs)
{
	Sedge	new_edge;

	new_edge = _make_edge (snode, tnode, attrs, FALSE);

        if (new_edge->snode->graph->make_edge_proc != NULL) {
		new_edge->snode->graph->make_edge_proc (new_edge);
	}

	return new_edge;
}



Local	Sedge	_make_edge (Snode snode, Snode tnode, Attributes attrs, int is_a_double)
{
	Sedge	new_edge;
	
	new_edge = (Sedge)malloc (sizeof(struct sedge));
	new_edge->spre  = empty_edge;
	new_edge->ssuc  = empty_edge;
	new_edge->tpre  = empty_edge;
	new_edge->tsuc  = empty_edge;
	new_edge->snode = snode;
	new_edge->tnode = tnode;
	new_edge->label = nil;
#ifdef GRAPHED_POINTERS
	new_edge->graphed = NULL;
#endif
	new_edge->filter = empty_edge;

	new_edge->attrs = attrs;
	new_edge->saved_attrs = empty_slist;
	new_edge->attrs_key = NULL;

	if (snode->slist == empty_edge) {
		snode->slist = new_edge;
		new_edge->spre = new_edge;
		new_edge->ssuc = new_edge;
	} else {
		new_edge->spre = snode->slist->spre;
		new_edge->ssuc = snode->slist;
		new_edge->spre->ssuc = new_edge;
		new_edge->ssuc->spre = new_edge;
	}
	
	if (snode->graph->directed) {
	
		/* directed_graph --- use tsuc, ssuc to link all the	*/
		/* nodes having the same target				*/
		
		if (tnode->tlist == empty_edge) {
			tnode->tlist = new_edge;
			new_edge->tpre = new_edge;
			new_edge->tsuc = new_edge;
		} else {
			new_edge->tpre = tnode->tlist->tpre;
			new_edge->tsuc = tnode->tlist;
			new_edge->tpre->tsuc = new_edge;
			new_edge->tsuc->tpre = new_edge;
		}
		
	} else /* undirected */ if (!is_a_double) {
	
		/* undirected_graph --- use tsuc, tpre as links to the	*/
		/* corresponding edge target -> source			*/
		
		Sedge	new_edges_double;

		new_edges_double = _make_edge (tnode, snode, attrs, TRUE);
		new_edge->tpre         = new_edges_double;
		new_edge->tsuc         = new_edges_double;
		new_edges_double->tpre = new_edge;
		new_edges_double->tsuc = new_edge;
	}
	
	return	new_edge;
}



Global	Sedge	copy_sedge (Sedge sedge)
{
	Sedge	new_sedge;

	new_sedge = make_edge (sedge->snode->iso, sedge->tnode->iso, sedge->attrs);
	set_edgelabel (new_sedge, strsave (sedge->label));

	return new_sedge;
}


Global	void	remove_edge (Sedge edge)
{
	Slist	l;

	for_slist (edge->snode->graph->saved_attrs, l) {
		Saved_sgraph_attrs	saved;
		saved = attr_data_of_type (l, Saved_sgraph_attrs);
		if (saved->remove_edge_proc != NULL &&
		    saved->remove_edge_proc != edge->snode->graph->remove_edge_proc) {
			saved->remove_edge_proc (edge);
		}
	} end_for_slist (edge->snode->graph->saved_attrs, l);

        if (edge->snode->graph->remove_edge_proc != NULL) {
		edge->snode->graph->remove_edge_proc (edge);
	}

	_remove_edge (edge, FALSE);
}



Local	void	_remove_edge (Sedge edge, int is_a_double)
{
	if (edge->ssuc == edge)
		edge->snode->slist = empty_edge;
	else {
		edge->spre->ssuc = edge->ssuc;
		edge->ssuc->spre = edge->spre;
		if (edge->snode->slist == edge)
			edge->snode->slist = edge->ssuc;
	}
	
	if (edge->snode->graph->directed) {
		if (edge->tsuc == edge)
			edge->tnode->tlist = empty_edge;
		else {
			edge->tpre->tsuc = edge->tsuc;
			edge->tsuc->tpre = edge->tpre;
			if (edge->tnode->tlist == edge)
				edge->tnode->tlist = edge->tsuc;
		}
	} else /* undirected */ if (!is_a_double) {
		_remove_edge (edge->tsuc, TRUE);
	}
	
	free (edge);
}


Global	void	set_edgelabel (Sedge edge, char *text)
{
	edge->label = text;
	if (!edge->snode->graph->directed)
		edge->tsuc->label = text;
}


Global	void	set_edgeattrs (Sedge edge, Attributes attrs)
{
	edge->attrs = attrs;
	if (!edge->snode->graph->directed)
		edge->tsuc->attrs = attrs;
}


Global	void	set_edgefilter (Sedge edge, Sedge filter)
{
	edge->filter = filter;
#ifdef GRAPHED_POINTERS
	edge->graphed = edge->filter->graphed;
#endif
}


Global	Sedge	get_edgefilter (Sedge edge, Sedge filter)
{
	return edge->filter;
}

Global	Sedge	get_unique_edge_handle (Sedge edge)
{
	if (edge->snode->graph->directed || unique_edge(edge)) {
		return edge;
	} else {
		return edge->tsuc;
	}
}
