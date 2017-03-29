extern	GraphEd_Menu_Proc menu_fpp_layout;
extern	GraphEd_Menu_Proc menu_fpp_layout_asslia;
extern	GraphEd_Menu_Proc menu_fpp_layout_compression;
extern	GraphEd_Menu_Proc menu_fpp_subframe;	

extern	void	call_fpp_layout(Sgraph_proc_info info);
extern	void	call_fpp_layout_asslia(Sgraph_proc_info info);
extern	void	call_fpp_layout_compression (Sgraph_proc_info info);

typedef	struct	{
	int	grid;
	int	grid_defaults;
}
	Fpp_settings;

extern	Fpp_settings	fpp_settings;
extern	Fpp_settings	init_fpp_settings(void);
extern	void			save_fpp_settings(void);

extern	void			show_fpp_subframe(void);

extern	int			fpp_prechecks_result;
