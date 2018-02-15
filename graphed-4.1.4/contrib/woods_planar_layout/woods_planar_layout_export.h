typedef struct woods_settings
{
	int	size_defaults_x,
		size_defaults_y;
	int	vertical_distance,
		horizontal_distance;
}
	Woods_settings;

extern	Woods_settings	woods_settings;
extern	Woods_settings	init_woods_settings(void);

extern	void	save_woods_settings(void);
extern	void	show_woods_subframe(void (*done_proc) ());
extern	int	showing_woods_subframe(void);
extern	void	call_woods_planar_layout(Sgraph_proc_info info);

extern	GraphEd_Menu_Proc menu_woods_planar_layout;
extern	GraphEd_Menu_Proc menu_woods_planar_layout_settings;

extern	int	woods_planar_layout(Sgraph g);
extern	int	test_conditions_for_woods_drawing(Sgraph g);
