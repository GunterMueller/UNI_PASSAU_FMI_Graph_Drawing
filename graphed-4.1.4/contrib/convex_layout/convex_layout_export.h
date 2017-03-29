/* menu callback procs	*/
extern	GraphEd_Menu_Proc menu_convex_layout_structure_only;	
extern	GraphEd_Menu_Proc menu_convex_layout;
extern	GraphEd_Menu_Proc menu_convex_layout_settings;

/* call_sgraph_procs	*/
extern	void	DrawGraphConvexStructur(Sgraph_proc_info info);
extern	void	DrawGraphConvexEditable(Sgraph_proc_info info);

typedef	struct {
	int	grid;
	int	grid_defaults;
	int	editable;
}
	Convex_draw_settings;

extern	Convex_draw_settings	convex_draw_settings;
extern	Convex_draw_settings	init_convex_draw_settings(void);
extern	void			save_convex_draw_settings(void);

extern	void			show_convex_draw_subframe(void (*done_proc) ());
extern	int			showing_convex_draw_subframe(void);

