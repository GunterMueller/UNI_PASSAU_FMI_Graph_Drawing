/* This software is distributed under the Lesser General Public License */
/*                                                           
  L      S      D
  Leda & Sgraph Do it
 
  an interface to run 
  Sgraph-alorithms on LEDA-graph-structures

  Author: Dirk Heider
  email: heider@fmi.uni-passau.de
 */
/********************************************************* 
 * MODULE DESCRIPTION
 *
 * This module contains dummy-functions and -variables from
 * GraphEdd, which are needed to call Sgraph-algorithms.
 * Annotation:
 * To make the linkage between C-code-algorithms and the C++
 * based LSD-interface possible, this code is declared
 * as extern  "C". CHANGED WA: 11.6.96 Now all compiled with C++.
 *
 *********************************************************/

#ifndef _GRAPHED_DUMMY_H
#define _GRAPHED_DUMMY_H

extern int wac_buffer;

#include <sgraph/graphed_structures.h>
	
#ifndef MALLOC_HEADER
#define MALLOC_HEADER
#include <malloc.h>
#endif

#define mymalloc(s)   malloc(s)

extern void	call_sgraph_proc (void (*proc) (), char *user_args);
extern int test_graph_is_drawn_planar (Graph graph);
	
extern int node_height(Node lnode);
extern int node_width(Node lnode);


extern int                 node_width(Node gnode);
extern int                 node_height(Node gnode);
extern int                 node_x(Node gnode);
extern int                 node_y(Node gnode);
extern Node_edge_interface node_edge_interface(Node gnode);
extern Nodelabel_placement node_label_placement(Node gnode);
extern char*               node_label_text(Node gnode);
extern int                 node_font_index(Node gnode);
extern int                 node_type_index(Node gnode);
extern int                 node_label_visible(Node gnode);
extern int                 node_color(Node gnode);

extern void set_node_width(Node gnode, int width);
extern void set_node_height(Node gnode, int height);
extern void set_node_x(Node gnode, int x);
extern void set_node_y(Node gnode, int y);
extern void set_node_edge_interface(Node gnode, Node_edge_interface nei);
extern void set_node_label_placement(Node gnode, Nodelabel_placement nlp);
extern void set_node_label_text(Node gnode, char *text);
extern void set_node_font_index(Node gnode, int index);
extern void set_node_type_index(Node gnode, int index);
extern void set_node_label_visible(Node gnode, int visible);
extern void set_node_color(Node gnode, int color);

extern Edgeline edge_line(Edge gedge);
extern int      edge_arrow_length(Edge gedge);
extern int      edge_arrow_angle(Edge gedge);
extern char*    edge_label_text(Edge gedge);
extern int      edge_font_index(Edge gedge);
extern int      edge_type_index(Edge gedge);
extern int      edge_label_visible(Edge gedge);
extern int      edge_color(Edge gedge);

extern void set_edge_line(Edge gedge, Edgeline edgeline);
extern void set_edge_arrow_length(Edge gedge, int length);
extern void set_edge_label_text(Edge gedge, char *text);
extern void set_edge_font_index(Edge gedge, int font_index);
extern void set_edge_type_index(Edge gedge, int edgetype_index);
extern void set_edge_label_visible(Edge gedge, int visible);
extern void set_edge_color(Edge gedge, int color);
extern void set_edge_arrow_angle(Edge gedge, int angle);

/*extern int  IsConnected(Sgraph g);*/
extern int  get_gridwidth(int buffer);
extern int  get_current_node_height(void);
extern int  get_current_node_width(void);

#endif /*_GRAPHED_DUMMY_H */









