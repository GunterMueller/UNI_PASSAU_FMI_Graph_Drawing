/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef ADJUST_HEADER
#define	ADJUST_HEADER

typedef	enum {
  ADJUST_EDGELINE_HEAD,
  ADJUST_EDGELINE_TAIL,
  ADJUST_EDGELINE_HEAD_AND_TAIL
}
  Nei_adjust_mode;
/* fuer adjust_line_to_node / adjust_edgeline_to_node : welche	*/
/* Enden sollen angepasst werden ?				*/

extern	void	adjust_line_to_node           (Nei_adjust_mode mode, Node source, int *x1, int *y1, int *x2, int *y2, Node target);
extern	void	adjust_edgeline_to_node       (Nei_adjust_mode mode, Edge edge, Node sourcenode, Node targetnode);

extern	void	adjust_nodelabel_position     (Node node);
extern	void	adjust_edgelabel_position     (Edge edge);
extern	void	adjust_nodelabel_text_to_draw (Node node);
extern	void	adjust_edgelabel_text_to_draw (Edge edge);

extern	void	adjust_arrow_to_edge          (Edge edge);

extern	void	adjust_edge_box               (Edge edge);

extern	void	adjust_edge_head              (Edge edge);
extern	void	adjust_edge_tail              (Edge edge);
extern	void	adjust_all_edges              (Node node);
extern  void    adjust_graph_box              (Graph graph);

extern	void	clip_line_out_of_node         (Node node, int *x1, int *y1, int x2, int y2);


extern	struct	pm_subregion	compute_lines_subregion_size (char **lines, Graphed_font font);


extern	void	adjust_boxes_in_graph  (Graph graph);
extern	void	adjust_boxes_in_group  ();

extern	int	line_line_intersection (int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int *x, int *y);

#endif


