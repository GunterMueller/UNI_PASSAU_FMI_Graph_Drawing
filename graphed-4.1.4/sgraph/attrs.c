#include "std.h"
#include "sgraph.h"
#include "stdarg.h"


/************************************************************************/
/*									*/
/*		        Attributes	make_attr			*/
/*									*/
/************************************************************************/

Attributes	make_attr (Attributes_type attr_type, ...)
{
	va_list 	args;
	Attributes	attrs;
	
	va_start (args, attr_type);
		
	switch (attr_type) {
	    case ATTR_INTEGER :
		/* attrs.key = (char *)ATTR_INTEGER; */
		attrs.value.integer = va_arg (args, int);
		break;
	    case ATTR_DATA :
		/* attrs.key = (char *)ATTR_DATA; */
		attrs.value.data = va_arg (args, char *);
		break;
#if FALSE
	    case ATTR_KEY_INTEGER :
		attrs.key = va_arg (args, char *);
		attrs.value.integer = va_arg (args, int);
		break;
	    case ATTR_KEY_DATA :
		attrs.key = va_arg (args, char *);
		attrs.value.data = va_arg (args, char *);
		break;
#endif
	}
	
	va_end (args);
	return attrs;
}


Slist	slist_assoc (Slist slist, char *key)
{
	Slist	l, found;

	found = empty_slist;
	for_slist (slist, l) if (slist_key(l) == key) {
		found = l;
		break;
	} end_for_slist (slist, l);

	return found;
}

/************************************************************************/
/*									*/
/*			Find Saved Attributes				*/
/*									*/
/************************************************************************/


static	Slist	find_saved_sgraph_attrs (Sgraph graph, char *key)
{
	Slist	l, found;

	found = empty_slist;
	for_slist (graph->saved_attrs, l)
		if (attr_data_of_type(l,Saved_sgraph_attrs)->key == key) {
		found = l;
		break;
	} end_for_slist (graph->saved_attrs, l);

	return found;
}


static	Slist	find_saved_snode_attrs (Snode node, char *key)
{
	Slist	l, found;

	found = empty_slist;
	for_slist (node->saved_attrs, l)
		if (attr_data_of_type(l,Saved_snode_attrs)->key == key) {
		found = l;
		break;
	} end_for_slist (node->saved_attrs, l);

	return found;
}


static	Slist	find_saved_sedge_attrs (Sedge edge, char *key)
{
	Slist	l, found;

	found = empty_slist;
	for_slist (edge->saved_attrs, l)
		if (attr_data_of_type(l,Saved_sedge_attrs)->key == key) {
		found = l;
		break;
	} end_for_slist (edge->saved_attrs, l);

	return found;
}



/************************************************************************/
/*									*/
/*			Save/Restore Sedge Attributes			*/
/*									*/
/************************************************************************/


#define sedge_saved(x) (attr_data_of_type(x, Saved_sedge_attrs))


static	void	save_sedge_attrs (Sedge edge, char *key)
{
	Saved_sedge_attrs	saved_attrs;
	Slist			saved_with_this_key;

	saved_with_this_key = find_saved_sedge_attrs (edge, key);
	if (saved_with_this_key == empty_slist) {
		saved_attrs = (Saved_sedge_attrs)malloc (
			sizeof(struct saved_sedge_attrs));
		edge->saved_attrs = add_immediately_to_slist (
			edge->saved_attrs,
			make_attr (ATTR_DATA, (char *)saved_attrs));
	} else {
		saved_attrs = sedge_saved(saved_with_this_key);
	}

	saved_attrs->attrs = edge->attrs;
	saved_attrs->key   = key;
	edge->attrs_key    = key;

	if (!edge->snode->graph->directed) {
		edge->tsuc->saved_attrs = edge->saved_attrs;
		edge->tsuc->attrs_key   = key;
	}
}


static	void	restore_sedge_attrs (Sedge edge, char *key, int remove)
{
	Slist	saved_with_this_key;
	
	saved_with_this_key = find_saved_sedge_attrs (edge, key);
	if (saved_with_this_key != empty_slist) {

		edge->attrs     = sedge_saved(saved_with_this_key)->attrs;
		edge->attrs_key = key;

		if (!edge->snode->graph->directed) {
			edge->tsuc->attrs    = edge->attrs;
			edge->tsuc->attrs_key = key;
		}

		if (remove) {
			free (sedge_saved(saved_with_this_key));
			edge->saved_attrs = subtract_immediately_from_slist (
				edge->saved_attrs, saved_with_this_key);
			if (!edge->snode->graph->directed) {
				edge->tsuc->saved_attrs = edge->saved_attrs;
			}
		}
	} else {
		if (edge->snode->graph->make_edge_proc != NULL) {
			edge->snode->graph->make_edge_proc (edge);
			edge->attrs_key = key;
			if (!edge->snode->graph->directed) {
				edge->tsuc->attrs_key = key;
			}
		}
	}
}



/************************************************************************/
/*									*/
/*			Save/Restore Snode Attributes			*/
/*									*/
/************************************************************************/


#define snode_saved(x) (attr_data_of_type(x, Saved_snode_attrs))


static	void	save_snode_attrs (Snode node, char *key)
{
	Saved_snode_attrs	saved_attrs;
	Slist			saved_with_this_key;

	saved_with_this_key = find_saved_snode_attrs (node, key);
	if (saved_with_this_key == empty_slist) {
		saved_attrs = (Saved_snode_attrs)malloc (
			sizeof(struct saved_snode_attrs));
		node->saved_attrs = add_immediately_to_slist (
			node->saved_attrs,
			make_attr (ATTR_DATA, (char *)saved_attrs));
	} else {
		saved_attrs = snode_saved(saved_with_this_key);
	}

	saved_attrs->attrs = node->attrs;
	saved_attrs->key   = key;
	node->attrs_key    = key;
}


static	void	restore_snode_attrs (Snode node, char *key, int remove)
{
	Slist	saved_with_this_key;
	
	saved_with_this_key = find_saved_snode_attrs (node, key);
	if (saved_with_this_key != empty_slist) {

		node->attrs     = snode_saved(saved_with_this_key)->attrs;
		node->attrs_key = key;

		if (remove) {
			free (snode_saved(saved_with_this_key));
			node->saved_attrs = subtract_immediately_from_slist (
				node->saved_attrs, saved_with_this_key);
		}
	} else {
		if (node->graph->make_node_proc != NULL) {
			node->graph->make_node_proc (node);
			node->attrs_key    = key;
		}
	}
}



/************************************************************************/
/*									*/
/*			Save Sgraph Attributes				*/
/*									*/
/************************************************************************/


#define sgraph_saved(x) (attr_data_of_type(x, Saved_sgraph_attrs))


void	save_sgraph_attrs (Sgraph graph, char *key)
{
	Saved_sgraph_attrs	saved_attrs;
	Slist			saved_with_this_key;
	Snode			node;
	Sedge			edge;

	saved_with_this_key = find_saved_sgraph_attrs (graph, key);
	if (saved_with_this_key == empty_slist) {
		saved_attrs = (Saved_sgraph_attrs)malloc (
			sizeof (struct saved_sgraph_attrs));
		graph->saved_attrs = add_immediately_to_slist (
			graph->saved_attrs,
			make_attr (ATTR_DATA, (char *)saved_attrs));
	} else {
		saved_attrs = sgraph_saved (saved_with_this_key);
	}

	saved_attrs->attrs             = graph->attrs;
	saved_attrs->make_node_proc    = graph->make_node_proc;
	saved_attrs->make_edge_proc    = graph->make_edge_proc;
	saved_attrs->remove_node_proc  = graph->remove_node_proc;
	saved_attrs->remove_edge_proc  = graph->remove_edge_proc;
	saved_attrs->remove_graph_proc = graph->remove_graph_proc;
	saved_attrs->key               = key;
	graph->attrs_key               = key;

/*
	graph->make_node_proc    = NULL;
	graph->make_edge_proc    = NULL;
	graph->remove_node_proc  = NULL;
	graph->remove_edge_proc  = NULL;
	graph->remove_graph_proc = NULL;
	graph->attrs_key         = NULL;
*/

	for_all_nodes (graph, node) {
		save_snode_attrs (node, key);
		for_sourcelist (node, edge) {
			save_sedge_attrs (edge, key);
		} end_for_sourcelist (node, edge);
	} end_for_all_nodes (graph, node);
}


void	restore_sgraph_attrs (Sgraph graph, char *key, void (*make_node_proc) (), void (*make_edge_proc) (), int remove)
{
	Slist			saved_with_this_key;
	Snode			node;
	Sedge			edge;
	 
	saved_with_this_key = find_saved_sgraph_attrs (graph, key);
	if (saved_with_this_key != empty_slist) {

		graph->attrs = sgraph_saved(saved_with_this_key)->attrs;
		graph->make_node_proc = iif (make_node_proc != NULL,
			make_node_proc,
			sgraph_saved(saved_with_this_key)->make_node_proc);
		graph->make_edge_proc = iif (make_edge_proc != NULL,
			make_edge_proc,
			sgraph_saved(saved_with_this_key)->make_edge_proc);
		graph->remove_node_proc  = sgraph_saved(saved_with_this_key)->remove_node_proc;
		graph->remove_edge_proc  = sgraph_saved(saved_with_this_key)->remove_edge_proc;
		graph->remove_graph_proc = sgraph_saved(saved_with_this_key)->remove_graph_proc;
		graph->attrs_key = key;

		if (remove) {
			free (sgraph_saved(saved_with_this_key));
			graph->saved_attrs = subtract_immediately_from_slist(
				graph->saved_attrs, saved_with_this_key);
		}

		for_all_nodes (graph, node) {
			restore_snode_attrs (node, key, remove);
			for_sourcelist (node, edge) if (graph->directed || unique_edge(edge)) {
				restore_sedge_attrs (edge, key, remove);
			} end_for_sourcelist (node, edge);
		} end_for_all_nodes (graph, node);

	} else if (make_node_proc != NULL || make_edge_proc != NULL) {

		graph->make_node_proc = make_node_proc;
		graph->make_edge_proc = make_edge_proc;

		for_all_nodes (graph, node) {

			graph->make_node_proc (node);
			node->attrs_key = key;

			for_sourcelist (node, edge) if (graph->directed || unique_edge(edge)) {
				graph->make_edge_proc (edge);
				edge->attrs_key = key;
				if (!graph->directed) {
					edge->tsuc->attrs_key = key;
				}
			} end_for_sourcelist (node, edge);

		} end_for_all_nodes (graph, node);
	}
}
