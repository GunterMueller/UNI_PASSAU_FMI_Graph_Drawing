/*===========================================================================*/
/*  
	 PROJECT	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:		st_upward_draw_preprocessing.c
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code for the preprocessing phase of the algorithm upward_draw
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
	void	st_init_graph (graph)
	Sgraph 	graph;

	Initialises the attributes of the orginal graph and nodes.
	The result of the already implemented planar embedding algorithm "embed"
	becomes the node attribute embed_list.
	This procedure is called before triangulating the graph.
*/
/*===========================================================================*/

void	st_init_graph (Sgraph graph)
{
	Snode n;
	Pregraphattrs gattr;
	Prenodeattrs nattr;

	gattr = (Pregraphattrs)malloc(sizeof(struct pregraphattrs));
	gattr->dummy_nodes_list = empty_slist;
	gattr->dummy_edges_list = empty_slist;
	set_graphattrs(graph,MAKE_DATA_ATTR(gattr));
	for_all_nodes (graph,n) {
		nattr = (Prenodeattrs)malloc(sizeof(struct prenodeattrs));
		nattr->sorted_in = false;
		nattr->visited = false;
		nattr->embed_list = attr_data_of_type(n,Slist);
		set_nodeattrs(n,make_attr(ATTR_DATA,nattr));
	} end_for_all_nodes (graph,n);
}

/*===========================================================================*/
/*
	void	re_st_init_graph (graph)
	Sgraph	graph;

	Reinitialises the attributes of the orginal graph and nodes.
	The result of the already implemented planar embedding algorithm "embed"
	becomes the node attribute embed_list.
	This procedure is called after triangulatine the graph.
*/
/*===========================================================================*/

void	re_st_init_graph (Sgraph graph)
{
	Snode n;
	Sedge e;
	Prenodeattrs nattr;
	Preedgeattrs eattr;

	for_all_nodes (graph,n) {
		nattr = (Prenodeattrs)malloc(sizeof(struct prenodeattrs));
		nattr->sorted_in = false;
		nattr->visited = false;
		nattr->embed_list = attr_data_of_type(n,Slist);
		set_nodeattrs(n,make_attr(ATTR_DATA,nattr));
		for_sourcelist (n,e) {
			eattr = (Preedgeattrs)malloc(sizeof(struct preedgeattrs));
			eattr->iso = empty_edge;
			set_edgeattrs(e,make_attr(ATTR_DATA,eattr));
		} end_for_sourcelist (n,e);
	} end_for_all_nodes (graph,n);
}

/*===========================================================================*/
/*
	void	free_data_structures (graph)
	Sgraph 	graph;

	Frees the allocated memory for all data structures that are allocated
	within in the preprocessing phase.
*/
/*===========================================================================*/

/* Changed to local MH 23/9/1994 */
Local	void	free_data_structures (Sgraph graph)
{
	Snode n;
	Sedge e;

	for_all_nodes (graph,n) {
		free_slist(PREEMBEDLIST(n));
		free(attr_data(n));
		for_sourcelist (n,e) {
			free (attr_data(e));
		} end_for_sourcelist (n,e);
	} end_for_all_nodes (graph,n);
}

/*===========================================================================*/
/*
	bool 	graph_is_st_graph (graph)
	Sgraph 	graph;

	Checks whether the original is a st-diagraph, i.e. the graph has exactly
	one source and exactly one target connected by an edge. The global
	variables source and target are set.
*/
/*===========================================================================*/

bool 	graph_is_st_graph (Sgraph graph)
{
	Snode n;
	Sedge e;
	int nr_of_s,nr_of_t;
	bool found_st;

	nr_of_s = 0;
	nr_of_t = 0;
	for_all_nodes (graph,n) {
		if (n->tlist == empty_edge) {
			nr_of_s += 1;
			source = n;
		}
		if (n->slist == empty_edge) {
			nr_of_t += 1;
			target = n;
		}
	} end_for_all_nodes (graph,n);

	found_st = false;
	if ((nr_of_s == 1) && (nr_of_t == 1)) {
		for_sourcelist(source,e) {
			if (e->tnode == target) {
				found_st = true;
				break;
			}
		} end_for_sourcelist(source,e);
		if (found_st) return (true);
		else	return (false);
	} else return (false);


}

/*===========================================================================*/
/*
	bool 	topsort (n, state)
	Snode 	n;
	bool	state;

	This function is the recursive part of the topological sorting procedure.
	n is the actual considered node and state indicates whether it is
	necessary to continue with topological sorting. If an error occurs, i.e.
	sorting is not possible, the function could decide to stop sorting
	prematurely.
*/
/*===========================================================================*/

bool 	topsort (Snode n, int state)
{
	Sedge e;
	Snode n1;
	bool ts_ok;

	ts_ok = state;
	if (! ts_ok) return (false);

	PRENODEATTR(n)->visited = true;
	for_sourcelist(n,e) {
		n1 = e->tnode;
		if (! PRENODEATTR(n1)->visited) {
			ts_ok = topsort(n1,ts_ok);
		} else {
			if (! PRENODEATTR(n1)->sorted_in) return (false);
		}
	} end_for_sourcelist(n,e);

	ts_list = add_to_slist(ts_list, MAKE_DATA_ATTR(n));
	PRENODEATTR(n)->sorted_in = true;
	return (ts_ok);
}

/*===========================================================================*/
/*
	bool	graph_is_topological_sorted (graph)
	Sgraph	graph;

	This function is the control part of the topological sorting procedure.
	It calls the function topsort (if it is necessary) and assigns ranking
	numbers to each node according to the global variable ts_list, which is
	a Slist containing the original nodes in topological order.
*/
/*===========================================================================*/

bool	graph_is_topological_sorted (Sgraph graph)
{
	Snode n;
	bool ts_ok;
	int rank;
	Slist l;

	ts_ok = true;
	ts_list = empty_slist;
	for_all_nodes (graph,n) {
		if (ts_ok) {
			if (! PRENODEATTR(n)->visited) ts_ok = topsort(n,ts_ok);
		}
	} end_for_all_nodes (graph,n);

	rank = 1;
	if (ts_ok) {
		for_slist_reverse (ts_list,l) {
			n = NODE(l);
			PRENODEATTR(n)->rank = rank;
			rank += 1;
		} end_for_slist_reverse (ts_list,l);
		free_slist(ts_list);
		return (true);
	} else {
		free_slist(ts_list);
		return (false);
	}
}

/*===========================================================================*/
/*
	void	delete_edgelines (graph)
	Sgraph	graph;

	Deletes the possible existing bends in the original layout in GraphEd.
	Because the final result should be a drawing without bends, it is
	necessary to delete the bends.
*/
/*===========================================================================*/

void	delete_edgelines (Sgraph graph)
{
	Snode n;
	Sedge e;
	Edgeline el;

	for_all_nodes(graph,n) {
		for_sourcelist(n,e) {
			el = (Edgeline)edge_get(graphed_edge(e),EDGE_LINE);
			if (el != (Edgeline)NULL) {
				free_edgeline(el);
				el = (Edgeline)NULL;
				el = add_to_edgeline(new_edgeline(e->snode->x,e->snode->y),
				e->tnode->x,e->tnode->y);
				edge_set(graphed_edge(e),RESTORE_IT,EDGE_LINE,el,0);
			}
		} end_for_sourcelist(n,e);
	} end_for_all_nodes(graph,n);
}

/*===========================================================================*/
/*
	void	free_preattrs (graph)
	Sgraph 	graph;

	Frees only the allocated memory of the node attributes.
*/
/*===========================================================================*/

void	free_preattrs (Sgraph graph)
{
	Snode n;

	for_all_nodes(graph,n) {
		free_slist(PREEMBEDLIST(n));
		free (attr_data(n));
		set_nodeattrs(n,MAKE_DATA_ATTR(NULL));
	} end_for_all_nodes(graph,n);
}

/*===========================================================================*/
/*
	void	free_all_preattrs (graph)
	Sgraph 	graph;

	Frees the allocated memory of all attributes both of the graph and the
	nodes.
*/
/*===========================================================================*/

void	free_all_preattrs (Sgraph graph)
{
	Snode n;
	Sedge e;

	for_all_nodes(graph,n) {
		free_slist(PREEMBEDLIST(n));
		free (attr_data(n));
		set_nodeattrs(n,MAKE_DATA_ATTR(NULL));
		for_sourcelist(n,e) {
			free (attr_data(e));
			set_edgeattrs(e,MAKE_DATA_ATTR(NULL));
		} end_for_sourcelist(n,e);
	} end_for_all_nodes(graph,n);
}

/*===========================================================================*/
/*
	Slist 	make_face(sw, rf)
	Snode	sw;
	Sedge	rf;

	This function returns a Slist of Sedge which builds a face with the
	source switch sw and having rf as an edge. Beginning with the source
	switch, the face is buildt by scanning the right path of the face up
	until the target switch is reached. Then the left path is scanned down
	until the source switch is again reached.
*/
/*===========================================================================*/

Slist 	make_face(Snode sw, Sedge rf)
{
	Slist edges_list,next_edge_elem,last_edge_elem;
	Sedge last_edge,next_edge;
	Snode node;
	bool  scanning_up,scanning_down;

	edges_list = empty_slist;
	edges_list = add_to_slist(edges_list,MAKE_DATA_ATTR(rf));
	last_edge = rf;

	scanning_up = true;
	while(scanning_up) {
		node = last_edge->tnode;
		last_edge_elem = contains_slist_element(PREEMBEDLIST(node),
			MAKE_DATA_ATTR(last_edge));
		next_edge_elem = last_edge_elem->suc;
		next_edge = EDGE(next_edge_elem);
		edges_list = add_to_slist(edges_list,MAKE_DATA_ATTR(next_edge));
		last_edge = next_edge;
		if (node == next_edge->tnode) scanning_up = false;
	}

	scanning_down = true;
	while (scanning_down) {
		node = last_edge->snode;
		last_edge_elem = contains_slist_element(PREEMBEDLIST(node),
			MAKE_DATA_ATTR(last_edge));
		next_edge_elem = last_edge_elem->suc;
		next_edge = EDGE(next_edge_elem);
		edges_list = add_to_slist(edges_list,MAKE_DATA_ATTR(next_edge));
		last_edge = next_edge;
		if (sw == next_edge->snode) scanning_down = false;
	}

	return (edges_list);
}

/*===========================================================================*/
/*
	Slist	init_faces_list(g)
	Sgraph	g;

	This function builds s Slist of the Slists made in make_face and therefore
	initialises the Slist faces_list, which is the starting point for
	triangulation.
*/
/*===========================================================================*/

Slist	init_faces_list(Sgraph g)
{
	Slist faces_list,face_edges_list;
	Snode n;
	Slist l;
	Sedge e1,e2;
	Snode w1,w2;

	faces_list = empty_slist;
	for_all_nodes(g,n) {
		for_slist(PREEMBEDLIST(n),l) {
			e1 = EDGE(l);
			e2 = EDGE(l->suc);
			w1 = OTHERNODE(e1,n);
			w2 = OTHERNODE(e2,n);
			if ((w1 == e1->tnode) && (w2 == e2->tnode)) {
				face_edges_list = make_face(n,e2);
				faces_list = add_to_slist(faces_list,
					MAKE_DATA_ATTR(face_edges_list));
			}
		} end_for_slist(PREEMBEDLIST(n),l);
	} end_for_all_nodes(g,n);

	return (faces_list);
}

/*===========================================================================*/
/*
	bool	edge_already_exists (n1, n2)
	Snode	n1,n2;

	This small function is necessary because the triangulation algorithm
	proposed by the authors possibly inserts multiple edges (details see
	at triangulate_face)
*/
/*===========================================================================*/

bool	edge_already_exists (Snode n1, Snode n2)
{
	Sedge e;

	for_sourcelist(n1,e) {
		if (e->tnode == n2) return true;
	} end_for_sourcelist(n1,e);
	return false;
}

/*===========================================================================*/
/*
	void	triangulate_face (edges_list, graph)
	Slist 	edges_list;
	Sgraph 	graph;

	The Slist edges_list contains some edges that are building a face.
	This face has to be triangulated. The authors propose the following
	triangulation algorithm: If the face has more than three nodes, simply
	insert an edge between the source switch and each other node of the face.
	This additional edge has to be oriented from the source switch to the other
	node and embedded internal to the face. Therefore the planarity is
	preserved. BUT: If there exists already an edge between the source switch
	and another node external to the face, we insert a multiple edge
	by this procedure which confuses the layouting so that the runtime behaviour
	could not be predicted. Sometimes it works, sometimes it hangs, and
	sometimes it crashes. This situation is obviously not very good. Therefore
	I have to distinguish two possible cases (indicator critical_face !):
	1. There exists such an edge: The best way to overcome this situation is to
		create an additional dummy_node and three additional dummy_edges: An edge
		from source switch sw to this dummy_node, a second edge from the
		dummy_node to the first left face neighbor of sw and a third edge
		from the	dummy_node to the first right face neighbor of sw (clearly all
		embedded in the interior of the face). By this way
		I create two additional faces consisting of exactly three nodes.
		Then the dummy_node becomes the new source switch of the original face
		and this face has now to be triangulated with the standard algorithm
		proposed by the authors.
	2. There does not exist such an edge: Simply apply the standard algorithm
		proposed by the authors.
*/
/*===========================================================================*/

void	triangulate_face (Slist edges_list, Sgraph graph)
{
	Slist l,node_list;
	Sedge e,dummy_edge;
	Snode sw,neighbor1,neighbor2,w,dummy_node;
	bool  critical_face;
	Prenodeattrs nattr;

	neighbor1 = EDGE(edges_list)->tnode;
	neighbor2 = EDGE(edges_list->pre)->tnode;
	sw = EDGE(edges_list)->snode;
	critical_face = false;
	node_list = empty_slist;
	for_slist(edges_list,l) {
		e = EDGE(l);
		w = e->tnode;
		if ((w == neighbor1) || (w == neighbor2)) continue;
		node_list = add_to_slist(node_list,MAKE_DATA_ATTR(w));

		if (edge_already_exists(sw,w)) critical_face = true;
	} end_for_slist(edges_list,l);

	if (critical_face) {
		nattr = (Prenodeattrs)malloc(sizeof(struct prenodeattrs));
		nattr->sorted_in = false;
		nattr->visited = false;
		nattr->embed_list = empty_slist;
		dummy_node = make_node(graph,MAKE_DATA_ATTR(nattr));
		DUMMY_NODES_LIST(graph) = add_to_slist(DUMMY_NODES_LIST(graph),MAKE_DATA_ATTR(dummy_node));

		dummy_edge = make_edge(sw,dummy_node,MAKE_DATA_ATTR(NULL));
		DUMMY_EDGES_LIST(graph) = add_to_slist(DUMMY_EDGES_LIST(graph),MAKE_DATA_ATTR(dummy_edge));
		dummy_edge = make_edge(dummy_node,neighbor1,MAKE_DATA_ATTR(NULL));
		DUMMY_EDGES_LIST(graph) = add_to_slist(DUMMY_EDGES_LIST(graph),MAKE_DATA_ATTR(dummy_edge));
		dummy_edge = make_edge(dummy_node,neighbor2,MAKE_DATA_ATTR(NULL));
		DUMMY_EDGES_LIST(graph) = add_to_slist(DUMMY_EDGES_LIST(graph),MAKE_DATA_ATTR(dummy_edge));

		sw = dummy_node;
	}

	for_slist(node_list,l) {
		w = NODE(l);
		dummy_edge = make_edge(sw,w,MAKE_DATA_ATTR(NULL));

		DUMMY_EDGES_LIST(graph) = add_to_slist(DUMMY_EDGES_LIST(graph),MAKE_DATA_ATTR(dummy_edge));
	} end_for_slist(node_list,l);

	free_slist(node_list);
 }

/*===========================================================================*/
/*
	void	triangulate_faces (faces_list, graph)
	Slist	faces_list;
	Sgraph	graph;

	This procedure is the control part of the triangulation algorithm.
*/
/*===========================================================================*/

void	triangulate_faces (Slist faces_list, Sgraph graph)
{
	Slist fl,edges_list;

	for_slist(faces_list,fl) {
		edges_list = attr_data_of_type(fl,Slist);
		if (edges_list->suc->suc->suc == edges_list) continue;
		triangulate_face(edges_list, graph);
	} end_for_slist(faces_list,fl);
}

/*===========================================================================*/
/*
	void	free_faces_list (faces_list)
	Slist	faces_list;

	After triangulation the allocated memory for the Slists is freed.
*/
/*===========================================================================*/

void	free_faces_list (Slist faces_list)
{
	Slist fl,edges_list;

	for_slist(faces_list,fl) {
		edges_list = attr_data_of_type(fl,Slist);
		free_slist(edges_list);
	} end_for_slist(faces_list,fl);
	free_slist(faces_list);
}

/*===========================================================================*/
/*
	void	triangulate (graph)
	Sgraph	graph;

	This procedure calls the three main procedures of the
	triangulation algorithm.
*/
/*===========================================================================*/

void	triangulate (Sgraph graph)
{
	Slist faces_list;

	faces_list = init_faces_list(graph);
	triangulate_faces(faces_list,graph);
	free_faces_list(faces_list);
}

/*===========================================================================*/
/*
	Sgraph	st_init_graph_for_drawing (graph)
	Sgraph	graph;

	This function returns a special copy of the original graph, so that
	the drawing phase could be performed. Actually the created graph is not a
	proper copy of the original graph, because no edges are created!
	By not copying the edges the entire algorithm is simplified, because
	I do not have to deal with edge creating, removing, etc. The attribut
	embed_list of each node stores in counterclockwise order the embedding
	of the neighbors of each node. If a node is in the embed_list of an other
	node, so this implies that there is an edge between the two nodes, although
	this edge does not properly exists. The orientation of this edge can be
	obtained by the ranks of the two nodes. To speak in Sgraph manner, the
	snode of the edge is the node with the lower rank, the tnode is the node
	with the higher rank. After creating the embed_lists of each node, the
	"start element" has to be found (the start element is that element of
	each embed_list to which the attribut embed_list points). The source start
	element is the Slist element that points to THIRD(g), the target start
	element is the Slist element that points to SOURCE(g), the start elements
	of the internal nodes (incl. THIRD(g)) is the first right outgoing "edge".
	Then the candidate list is created and the initial coordinates of the
	external nodes SOURCE(g), THIRD(g), and TARGET(g) are assigned. Finally
	the minimal and the maximal slopes of the external face are computed.

*/
/*===========================================================================*/

Sgraph	st_init_graph_for_drawing (Sgraph graph)
{
	Sgraph 	g;
	Snode 	n,new_node,other_new_node,other_old_node,n1,n2;
	Slist 	l,start_elem;
	Nodeattrs nattr;
	Angle	st_angle,sv_angle,vt_angle;

	g = make_new_graph();
	INK(g) = 0;

	for_all_nodes(graph,n) {
		nattr = (Nodeattrs)malloc(sizeof(struct nodeattrs));
		new_node = make_node(g,MAKE_DATA_ATTR(nattr));
		n->iso = new_node;
		EMBEDLIST(new_node) = empty_slist;
		STATE(new_node) = INTERN;
		X(new_node) = 0.0;
		Y(new_node) = 0.0;
		BACKNODE(new_node) = n;
		RANK(new_node) = PRENODEATTR(n)->rank;
	} end_for_all_nodes(graph,n);

	for_all_nodes(graph,n) {
		new_node = n->iso;
		for_slist_reverse(PREEMBEDLIST(n),l) {
			other_old_node = OTHERNODE(EDGE(l),n);
			other_new_node = other_old_node->iso;
			EMBEDLIST(new_node) = add_to_slist(EMBEDLIST(new_node),MAKE_DATA_ATTR(other_new_node));
		} end_for_slist_reverse(PREEMBEDLIST(n),l);
	} end_for_all_nodes(graph,n);

	for_all_nodes(g,new_node) {
		if (new_node == source->iso) {
			for_slist(EMBEDLIST(new_node),l) {
				if (NODE(l) == target->iso) {
					start_elem = l->suc;
					break;
				}
			} end_for_slist(EMBEDLIST(new_node),l);
			EMBEDLIST(new_node) = start_elem;
			continue;
		}
		if (new_node == target->iso) {
			for_slist(EMBEDLIST(new_node),l) {
				if (NODE(l) == source->iso) {
					start_elem = l;
					break;
				}
			} end_for_slist(EMBEDLIST(new_node),l);
			EMBEDLIST(new_node) = start_elem;
			continue;
		}
		for_slist(EMBEDLIST(new_node),l) {
			n1 = NODE(l);
			n2 = NODE(l->suc);
			if ((RANK(new_node) > RANK(n1)) && (RANK(new_node) < RANK(n2))) {
				start_elem = l->suc; 
				break;
			}
		} end_for_slist(EMBEDLIST(new_node),l); 
		EMBEDLIST(new_node) = start_elem; 
	} end_for_all_nodes(g,new_node);
	
	SOURCE(g) = source->iso;
	TARGET(g) = target->iso;
	THIRD(g)  = NODE(EMBEDLIST(SOURCE(g)));

	for_all_nodes(g,n) {
		if ((n != SOURCE(g)) && (n != THIRD(g) ) && (n != TARGET(g)) && node_is_candidate(n)) {
			CANDLIST(g) = add_to_slist(CANDLIST(g),MAKE_DATA_ATTR(n));
		}
	} end_for_all_nodes(g,n);

	X(SOURCE(g)) = 0.0;
	Y(SOURCE(g)) = 0.0;
	X(TARGET(g)) = 0.0;
	Y(TARGET(g)) = 2.0 * BASE_LENGTH;
	X(THIRD(g))  = 2.0 * BASE_LENGTH;
	Y(THIRD(g))  = BASE_LENGTH;

	st_angle = st_upward_draw_get_angle(X(SOURCE(g)), Y(SOURCE(g)), X(TARGET(g)), Y(TARGET(g)));
	sv_angle = st_upward_draw_get_angle(X(SOURCE(g)), Y(SOURCE(g)), X(THIRD(g)) , Y(THIRD(g)));
	vt_angle = st_upward_draw_get_angle(X(THIRD(g)) , Y(THIRD(g)),  X(TARGET(g)), Y(TARGET(g)));
 
	EXTERN_MIN(g) = st_angle;
	if (sv_angle < EXTERN_MIN(g)) EXTERN_MIN(g) = sv_angle; 
	if (vt_angle < EXTERN_MIN(g)) EXTERN_MIN(g) = vt_angle; 
	EXTERN_MAX(g) = st_angle;
	if (EXTERN_MAX(g) < sv_angle) EXTERN_MAX(g) = sv_angle;
	if (EXTERN_MAX(g) < vt_angle) EXTERN_MAX(g) = vt_angle; 
 
	ALPHA(g) = M_PI * (Angle)(st_settings.alpha) / 180.0; 
 
	return (g);
}

/*===========================================================================*/
/*
	Sgraph	st_preprocessing_phase (graph)
	Sgraph	graph;

	This procedure is the main procedure of the preprocessing phase.
*/
/*===========================================================================*/

Sgraph	st_preprocessing_phase (Sgraph graph)
{
	Sgraph g;


		/*
			Checking whether the graph is an st-digraph
		*/

	if (! graph_is_st_graph(graph)) {
		error("Graph is not an st-digraph!\n");
		return (empty_sgraph);
	}

		/*
			Checking whether the graph is planar. embed returns an planar
			embedding. Besides computing a planar embedding, this implementation
			of the Hopcroft-Tarjan algorithm has the special feature that it
			performs an st-numbering. Therefore the source and the target of
			the graph are already part of the external face. This fact is very
			helpful for me, because I have not to take care of this task. 
		*/

	if (embed(graph) != SUCCESS) {
		error("Graph is not planar!\n");
		return (empty_sgraph);
	}

		/*
			Preparing the graph for triangulating
		*/

	st_init_graph(graph);

		/*
			Performing the topological sorting. If no topological
			sorting is possible, then we have a graph that is not acyclic.
		*/

	if (! graph_is_topological_sorted(graph)) {
		error("Graph is not acyclic!\n");
		free_data_structures(graph);
		return (empty_sgraph);
	}

		/*
			Removing the bends in the original layout in GraphEd.
		*/

	delete_edgelines(graph);

		/*
			Triangulating the graph
		*/

	triangulate(graph);

		/*
			The former computed planar embedding is now possibly not longer guilty
			because by triangulating additional edges and even nodes are
			inserted. Therefore we must free the old embedding and compute
			a new embedding.
			I decided to compute a new emdedding because sorting in new edges into
			the already existing embedding seems to be more complex as it looks
			for the first sight.
		*/

	free_preattrs(graph);

	embed(graph);

		/*
			The new embedding must be linked to the node attributes.
		*/

	re_st_init_graph(graph);

		/*
			If new nodes were added, a new topological sorting must be performed.
		*/

	graph_is_topological_sorted(graph);


		/*
			The graph will now be prepared for the drawing phase.
		*/

	g = st_init_graph_for_drawing(graph);

		/*
			All attributes needed in the preprocessing phase will now
			be freed.
		*/

	free_all_preattrs(graph);

	return (g);
}



