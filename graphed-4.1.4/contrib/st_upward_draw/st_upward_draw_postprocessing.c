/*===========================================================================*/
/*  
	 PROJECT	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_postprocessing.c
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code for the postprocessing phase of the algorithm upward_draw
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
	void	flip_graph(graph)
	Sgraph	graph;

	This procedure flips the entire layout to get a real upward drawing.
*/
/*===========================================================================*/

void	flip_graph(Sgraph graph)
{
	Snode n;
	int maxY;

	maxY = 0;
	for_all_nodes (graph,n) {
		if (n->y > maxY) maxY = n->y;
	} end_for_all_nodes (graph,n);

	for_all_nodes (graph,n) {
		n->y = maxY - n->y;
	} end_for_all_nodes (graph,n);
}

/*===========================================================================*/
/*
	void	make_pretty_drawing (g)
	Sgraph	g;

	This procedure is responsible for converting the real coordinates to
	integer coordinates. The user has the ability to require either that
	the final length of the edge (s, t) must be st_len or that every node
	has a minimal distance of min_node_dist to each other node.
	Dependant upon the user's choice the factor stretch is computed and
	the final integer coordinates are assigned.
*/
/*===========================================================================*/

void	make_pretty_drawing (Sgraph g)
{
	Snode 	n;
	Slist 	l;
	double	max_stretch;
	double	ddx,ddy,ddist,max_dist,min_dist;
	double  act_y_stretch;
	double	act_x_stretch;

	min_dist = (double)st_settings.max_node_dist;
	max_dist = 0.0;

	for_all_nodes(g,n) {
		for_slist(EMBEDLIST(n),l) {
			ddx = X(n) - X(NODE(l));
			ddy = Y(n) - Y(NODE(l));
			ddist = sqrt (ddx*ddx + ddy*ddy);
			if (ddist > max_dist) max_dist = ddist;
			if (ddist < min_dist) min_dist = ddist;
		} end_for_slist(EMBEDLIST(n),l);
	} end_for_all_nodes(g,n);
	max_stretch = (double)st_settings.max_node_dist / max_dist;
	if (min_dist != 0.0) {
		act_x_stretch = (double)st_settings.horizontal_dist / min_dist;
		act_y_stretch = (double)st_settings.vertical_dist / min_dist;
		if (act_x_stretch > max_stretch) act_x_stretch = max_stretch;
		if (act_y_stretch > max_stretch) act_y_stretch = max_stretch;

	} else {
		act_x_stretch = max_stretch;
		act_y_stretch = max_stretch;
	}

	for_all_nodes(g,n) { 
		BACKNODE(n)->x = (int)(X(n) * act_x_stretch); 
		BACKNODE(n)->y = (int)(Y(n) * act_y_stretch);
	} end_for_all_nodes(g,n); 
}

/*===========================================================================*/
/*
	void	remove_dummy_datas (graph)
	Sgraph 	graph;

	By triangulating the graph it is very probably that additional edges
	and even nodes were added. These dummy datas are here removed.
*/
/*===========================================================================*/

void	remove_dummy_datas (Sgraph graph)
{
	Snode n;
	Sedge e;
	Slist l;

	for_slist(DUMMY_EDGES_LIST(graph),l) {
		e = EDGE(l);
		remove_edge(e);
	} end_for_slist(DUMMY_EDGES_LIST(graph),l);

	for_slist(DUMMY_NODES_LIST(graph),l) {
		n = NODE(l);
		remove_node(n);
	} end_for_slist(DUMMY_NODES_LIST(graph),l);

	free_slist(DUMMY_NODES_LIST(graph));
	free_slist(DUMMY_EDGES_LIST(graph));
	free (attr_data(graph));
}

/*===========================================================================*/
/*
	void	st_postprocessing_phase(graph,g)
	Sgraph	graph;
	Sgraph	g;

	This procedure controls the postprocessing phase. First the integer
	coordinates are computed, then the layout is flipped to get an upward
	drawing. Finally the temporary graph is removed and all necessary
	edges and nodes to triangulate the graph are deleted.
*/
/*===========================================================================*/

void	st_postprocessing_phase(Sgraph graph, Sgraph g)
{	
	make_pretty_drawing(g);

	flip_graph(graph);

	remove_new_graph(g);

	remove_dummy_datas(graph);
}
