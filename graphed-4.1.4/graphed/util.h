/* (C) Universitaet Passau 1986-1994 */
#ifndef __GRAPHED_UTIL_H__
#define __GRAPHED_UTIL_H__
#include <xview/xview.h>
#include <graphed/graph.h>

void menu_shrink_buffer (Menu menu, Menu_item menu_item);
void menu_make_window (Menu menu, Menu_item menu_item);
void fit_node_to_text (Node node);
int test_graph_is_drawn_planar (Graph graph);
int test_find_non_straight_line_edge (Graph graph);
int test_graph_edges_are_straight_line_planar (Graph graph);
int test_graph_edges_are_drawn_planar (Graph graph);
void shrink_buffer (int buffer);
void make_window (Graph graph, int at_x, int at_y, int width, int height);
#endif
