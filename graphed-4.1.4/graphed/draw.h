/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef DRAW_HEADER
#define DRAW_HEADER

extern	void	draw_node		(Node node);
extern	void	erase_node		(Node node);

extern	void	draw_nodelabel		(Node node);
extern	void	erase_nodelabel		(Node node);

extern	void	erase_and_delete_node	(Node node);

extern	void	draw_virtual_node	(int x, int y, int sx, int sy, Nodetypeimage image);
extern	void	erase_virtual_node	(int x, int y, int sx, int sy, Nodetypeimage image);

extern	void	do_mark_node		(Node node);
extern	void	do_unmark_node		(Node node);


extern	void	draw_edge		(Edge edge);
extern	void	erase_edge		(Edge edge);

extern	void	draw_edgelines		(int buffer, Edgeline el);
extern	void	erase_edgelines		(int buffer, Edgeline el);
extern	void	draw_single_edgeline	(int buffer, Edgeline el);
extern	void	erase_single_edgeline	(int buffer, Edgeline el);

extern	void	draw_edgelabel		(Edge edge);
extern	void	erase_edgelabel		(Edge edge);

extern	void	draw_arrow		(Edge edge);
extern	void	erase_arrow		(Edge edge);

extern	void	erase_and_delete_edge	(Edge edge);

extern	void	draw_edge_sourcelist	(Node node);
extern	void	draw_edge_targetlist	(Node node);
extern	void	erase_edge_sourcelist	(Node node);
extern	void	erase_edge_targetlist	(Node node);

extern	void	draw_virtual_line	(int x1, int y1, int x2, int y2);
extern	void	erase_virtual_line	(int x1, int y1, int x2, int y2);

extern	void	do_mark_edge		(Edge edge);
extern	void	do_unmark_edge		(Edge edge);

extern  void	draw_edge_head          (Edge edge); 
extern  void	draw_edge_tail          (Edge edge); 
extern  void	erase_edge_head         (Edge edge);
extern  void	erase_edge_tail         (Edge edge);  
extern  void	erase_edges_at_node     (Node node);     
extern  void	draw_edges_at_node      (Node node);
   
extern	void	draw_group		(Group group);
extern	void	erase_group		(Group group);
extern	void	erase_and_delete_group	(Group group);
extern	void	draw_virtual_group	(Group group, int dx, int dy);
extern	void	erase_virtual_group	(Group group, int dx, int dy);
extern	void	draw_virtual_group_box	(int x1, int y1, int x2, int y2);
extern	void	erase_virtual_group_box	(int x1, int y1, int x2, int y2);

extern	void	show_grid		(int buffer, int width);
extern	int	get_gridwidth		(int buffer);

/*	Prozeduren aus repaint.c	*/

extern	void	force_repainting	(void);
extern	void	redraw_all		(void);

#endif
