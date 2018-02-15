/*===========================================================================*/
/*  
	 PROJECT	st_upward_draw

	 SYSTEM:       	GraphEd extension module
	 FILE:        	st_upward_draw_main.c
	 AUTHOR:       	Roland Stuempfl (diploma 1994)


	 Overview
	 ========
	 Source code for callback functions of the algorithm upward_draw
	 presented by Guiseppe Di Battista and Roberto Tamassia, published
	 in "Algorithms For Plane Representations of Acyclic Digraphs",
	 Theoretical Computer Science 61 (1988), pp. 175-198.
	 In the following this work will be referenced as [DiBatTam88].
*/
/*===========================================================================*/

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/openmenu.h>

#include <sgraph/std.h>
#include <sgraph/sgraph.h>
#include <sgraph/graphed.h>
#include "st_upward_draw_export.h"
#include "st_upward_draw_algorithm.h"

extern	Sgraph	st_preprocessing_phase(Sgraph graph);
extern	void	upward_draw(Sgraph g);
extern	void	st_postprocessing_phase(Sgraph graph, Sgraph g);

/*****************************************************************************/
/*
	void	call_st_upward_draw_layout (info)
	Sgraph_proc_info info;

	Callback function of the call_sgraph_proc
	Forbidden graphs (empty graphs, graphs with only one or two nodes,
	and undirected graphs) are rejected. If the graph is o.k., the
	main layout procedure st_upward_draw_layout is called.

*/
/*****************************************************************************/

void	call_st_upward_draw_layout (Sgraph_proc_info info)
{
	Sgraph	graph;

	graph = info->sgraph;

	info->recompute = TRUE;
	info->repaint = TRUE;
	info->recenter = TRUE;

	if (graph == empty_sgraph) {
		error ("No graph selected\n");
	} else if (graph->nodes == empty_node) {
		error ("Empty_graph\n");
	} else if (!graph->directed) {
		error ("Graph is not directed\n");
	} else if (graph->nodes == graph->nodes->suc) {
		error ("Graph must have at least three vertices\n");
	} else if (graph->nodes == graph->nodes->suc->suc) {
		error ("Graph must have at least three vertices\n");
	} else {
		st_upward_draw_layout(graph);
	}
}

/*****************************************************************************/
/*
	int		st_upward_draw_layout(graph)
	Sgraph		graph;

	Calls the three parts of the algorithm.
*/
/*****************************************************************************/

int	st_upward_draw_layout(Sgraph graph)
{
	Sgraph	g;

	g = st_preprocessing_phase(graph);
	if (g == empty_sgraph) return (0);

	upward_draw(g);

	st_postprocessing_phase(graph,g);

	return (0);
}

/*****************************************************************************/
/*
	void menu_st_upward_draw_layout_subframe(menu, item)
	Menu	menu;
	Menu_item item;

	Callback function of the menu entry "settings..."
*/
/*****************************************************************************/

void menu_st_upward_draw_layout_subframe(Menu menu, Menu_item item)
{
	save_st_settings();
	show_st_subframe(NULL);
}

/*****************************************************************************/
/*
	void menu_st_upward_draw_layout(menu, item)
	Menu	menu;
	Menu_item item;

	Callback function of the menu entry "st upward draw algorithm..."
*/
/*****************************************************************************/

void menu_st_upward_draw_layout(Menu menu, Menu_item item)
{
	save_st_settings();
	call_sgraph_proc(call_st_upward_draw_layout, NULL);
}
