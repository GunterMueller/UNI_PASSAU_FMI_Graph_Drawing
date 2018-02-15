/**************************************************************************/
/***                                                                    ***/
/*** Filename: VISI_EXPORT.H                                            ***/
/***                                                                    ***/
/**************************************************************************/

typedef struct tarjan_settings
{
	int	size_defaults_x,
		size_defaults_y,
		node_defaults_y;
	int	vertical_distance,
		horizontal_distance,
		vertical_height;
	int	largest_face,
		polyline,
		betterpolyline,
		verbose,
		greedy;
}
	Tarjan_settings;

extern	Tarjan_settings	tarjan_settings;

extern	Tarjan_settings	init_tarjan_settings(void);

extern	void	save_tarjan_settings(void);

extern	void	show_tarjan_subframe(void);


extern	char	*tarjan_exit();
extern	GraphEd_Menu_Proc menu_otten_planar_layout;
extern	GraphEd_Menu_Proc menu_tarjan_planar_layout;
extern	GraphEd_Menu_Proc menu_tarjan2_planar_layout;
extern	GraphEd_Menu_Proc menu_tamassia_w_planar_layout;
extern	GraphEd_Menu_Proc menu_tamassia_e_planar_layout;
extern	GraphEd_Menu_Proc menu_tamassia_s_planar_layout;
extern	GraphEd_Menu_Proc menu_wismath_planar_layout;
extern	GraphEd_Menu_Proc menu_cylinder_planar_layout;
extern	GraphEd_Menu_Proc menu_tarjan_planar_layout_settings;
extern	void	call_otten_planar_layout(Sgraph_proc_info info);
extern	void	call_tarjan_planar_layout(Sgraph_proc_info info);
extern	void	call_tarjan2_planar_layout(Sgraph_proc_info info);
extern	void	call_tamassia_w_planar_layout(Sgraph_proc_info info);
extern	void	call_tamassia_e_planar_layout(Sgraph_proc_info info);
extern	void	call_tamassia_s_planar_layout(Sgraph_proc_info info);
extern	void	call_wismath_planar_layout(Sgraph_proc_info info);
extern	void	call_cylinder_planar_layout(Sgraph_proc_info info);
extern	int	test_conditions_for_layout_drawing(Sgraph g);

/****************************************************************************/
/***                     END OF FILE: TARJAN_EXPORT.H                     ***/
/****************************************************************************/
