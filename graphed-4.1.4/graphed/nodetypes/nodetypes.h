extern void init_system_nodetypes (void);
extern void init_diamond_nodetype (void);
extern void init_elliptical_nodetype (void);
extern void init_box_nodetype (void);
extern void init_pixmap_nodetype (void);
extern void init_double_box_nodetype (void);
extern void init_black_box_nodetype(void);
extern void init_double_diamond_nodetype(void);
extern void init_black_diamond_nodetype(void);
extern void init_double_elliptical_nodetype(void);
extern void init_black_elliptical_nodetype(void);
extern void init_black_nodetype(void);
extern void init_white_nodetype(void);

extern	void	adjust_edgeline_to_box_node ();
extern	void	adjust_edgeline_to_elliptical_node ();
extern	void	adjust_edgeline_to_diamond_node ();

extern	void	pm_paint_box_node ();
extern	void	pm_paint_elliptical_node ();
extern	void	pm_paint_diamond_node ();
extern	void	pm_paint_double_box_node ();
extern	void	pm_paint_double_elliptical_node ();
extern	void	pm_paint_double_diamond_node ();
extern	void	pm_paint_black_box_node ();
extern	void	pm_paint_black_elliptical_node ();
extern	void	pm_paint_black_diamond_node ();
extern	void	pm_paint_black_node ();
extern	void	pm_paint_white_node ();

extern	void	ps_paint_box_node ();
extern	void	ps_paint_elliptical_node ();
extern	void	ps_paint_diamond_node ();
extern	void	ps_paint_double_box_node ();
extern	void	ps_paint_double_elliptical_node ();
extern	void	ps_paint_double_diamond_node ();
extern	void	ps_paint_black_box_node ();
extern	void	ps_paint_black_elliptical_node ();
extern	void	ps_paint_black_diamond_node ();
extern	void	ps_paint_black_node ();
extern	void	ps_paint_white_node ();

#define DOUBLE_OFFSET 3
