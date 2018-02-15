/*===========================================================================*/
/*  
	 PROJECT 	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_auxiliary.c
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code for some auxiliary functions for the algorithm upward_draw
	 presented by Guiseppe Di Battista and Roberto Tamassia, published
	 in "Algorithms For Plane Representations of Acyclic Digraphs",
	 Theoretical Computer Science 61 (1988), pp. 175-198.
	 In the following this work will be referenced as [DiBatTam88].
*/
/*===========================================================================*/

#include <values.h>
#include <math.h>
#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/graphed.h>
#include <sgraph/slist.h>
#include <sgraph/algorithms.h>

#include "st_upward_draw_algorithm.h"
#include "st_upward_draw_export.h"


/*===========================================================================*/
/*
	Sgraph	make_new_graph ()

	Creates a new (temporary) sgraph and its attributes. Attributes are:
	- candidate_list:   	Slist of Snodes who have at most five neighbors
				in the sgraph; access via the macro CANDLIST(g)
	- source,           	the nodes of the external face of the sgraph;
	  target,           	access via the macros SOURCE(g), TARGET(g)
	  third:            	and THIRD(g)
	- min_theta,        	minimal and maximal slopes of edges of the
	  max_theta:        	external face; access via the macros EXTERN_MIN(g)
				and EXTERN_MIN(g)
	- alpha:          	the tolerance angle; access via the macro
				ALPHA(g) (the authors of the algorithm suggest
				to halve the tolerance angle with every recursiv
				call of upward_draw, but experience shows that the
				so obtained results are not so good, therefore
				the tolerance angle will never be halved. But
				by the existence of this attribut, it is easy
				to change the implementation for later adjustments.)
*/
/*===========================================================================*/

Sgraph	make_new_graph (void)
{
	Sgraph g;
	Graphattrs gattr;

	gattr = (Graphattrs)malloc(sizeof(struct graphattrs));
	gattr->candidate_list = empty_slist;
	gattr->source = empty_node;
	gattr->target = empty_node;
	gattr->third  = empty_node;
	gattr->min_theta = 0.0;
	gattr->max_theta = 0.0;
	gattr->alpha = 0.0;
	g = make_graph(make_attr(ATTR_DATA,gattr));
	g->directed = true;

	return(g);
}

/*===========================================================================*/
/*
	Snode	make_new_node (g, old_node)

	Creates a new (temporary) snode of the Sgraph g whose origin is
	old_node. Attributes are:
	- embed_list:       	Slist of Snodes that are counterclockwise
				ordered around the node in a planar embedding
				of g; access via the macro EMBEDLIST(n)
	- state:		flag that shows whether the node is EXTERN,
				INTERN or ON_THE_CYCLE of the actual cycle;
				the flag is initialised with INTERN (important
				for procedure scan_nodes !!!); access
				via the macro STATE(n)
	- lfx, lfy:		coordinates of the node in double format (integer
				arithmetic is not so good here); access via the macros
				X(n) and Y(n)
	- back_node:		the origin node of this node, important for
				updating the coordinates after layouting
				a subgraph; access via the macro BACKNODE(n)
	- rank:			the rank of this node (result of the topological
				sorting in the preprocessing phase)
                     
*/
/*===========================================================================*/

Snode	make_new_node (Sgraph g, Snode old_node)
{
	Snode new_node;
	Nodeattrs nattr;

	nattr = (Nodeattrs)malloc(sizeof(struct nodeattrs));
	nattr->embed_list = empty_slist;
	nattr->state = INTERN;
	nattr->lfx = 0.0;
	nattr->lfy = 0.0;
	nattr->back_node = old_node;
	nattr->rank = RANK(old_node);
	new_node = make_node(g,make_attr(ATTR_DATA,nattr));
	old_node->iso = new_node;

	return (new_node);
}

/*===========================================================================*/
/*
	void		remove_new_graph (g)

	Removes the (temporary) Sgraph g and all contained Snodes from memory.
	The allocated memory of the attributes of g and the snodes is
	also freed.
*/
/*===========================================================================*/

void	remove_new_graph (Sgraph g)
{
	Snode n;
	Slist node_list,l;

	node_list = make_slist_of_sgraph(g);
	for_slist(node_list,l) {
		n = NODE(l);
		free_slist(EMBEDLIST(n));
		free(attr_data(n));
		remove_node(n);
	} end_for_slist(node_list,l);
	free_slist(node_list);
	free_slist(CANDLIST(g));
	free(attr_data(g));
	remove_graph(g);
}

void	remove_new_node(Snode n)
{
	free_slist(EMBEDLIST(n));
	free(attr_data(n));
	remove_node(n);
}
