/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	PAINT_HEADER
#define	PAINT_HEADER

extern	Rect	*set_clip_region               (Rect *clip_rect, int buffer, int clip_at_window_boundary, int color);
extern	void	paint_node_internal            (Node node, int op);
extern	void	paint_edgelabel_internal       (Edge edge, int op);
extern	void	paint_single_edgeline_internal (Edgeline el, Edgetype type, int op);
extern  void	paint_dot_internal             (int x, int y); /*eingefuegt*/ 
/*extern  void    paint_edgetype_on_pr           ();
extern  void    PR_line                        (); */   
extern	void	paint_rectangle                (int x1, int y1, int x2, int y2, int op);
extern	void	paint_virtual_node             (int x, int y, int w, int h, Nodetypeimage image, int op);
extern	void	paint_line                     (int x1, int y1, int x2, int y2, int op);
extern	void	paint_background               (Rect *rect);
extern	void	paint_line_internal            (int x0, int y0, int x1, int y1, int op);
extern	void	paint_marker_rect              (int x, int y);
extern	void	paint_marker_square            (int x, int y);
extern	void	paint_marker_rectangle         (int x1, int y1, int x2, int y2);

extern	void	paint_nodetype_on_pm           (Nodetype type, Pixmap pm, int w, int h, int color);
extern	void	paint_edgetype_on_pm           (Edgetype type, Pixmap pm, int w, int h, int color);
extern	void	pm_paint_box_node              (Pixmap pm, int x, int y, int w, int h, int op);
extern	void	pm_paint_elliptical_node       (Pixmap pm, int x, int y, int w, int h, int op);
extern	void	pm_paint_diamond_node          (Pixmap pm, int x, int y, int w, int h, int op);
extern	void	pm_paint_rect_node             (Pixmap pm, int x, int y, int w, int h, int op);
extern	void	pw_paint_box_node              (Pixmap pm, int x, int y, int w, int h, int op);
extern	void	pw_paint_elliptical_node       (Pixmap pm, int x, int y, int w, int h, int op);
extern	void	pw_paint_diamond_node          (Pixmap pm, int x, int y, int w, int h, int op);

extern	void	scale_pm (Pixmap source_pm, Pixmap target_pm, int w, int h, int color);

#define PIX_PAINT (PIX_SRC | PIX_DST)
#define PIX_ERASE (PIX_NOT(PIX_SRC) & PIX_DST)
#define PIX_XOR   (PIX_SRC ^ PIX_DST)

#define WHITE 0	/* WARNING : MUST BE CONSISTENT WITH	*/
#define BLACK 7	/* THE COLORMAP IN CANVAS.c !		*/

/**extern	Pixrect	*paint_pr;**/
extern  Pixmap  paint_pm; /** hinzugefuegt **/
#endif
