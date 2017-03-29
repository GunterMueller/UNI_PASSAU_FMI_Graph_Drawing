typedef	struct	{

	bool   		draw_weighted;
	float		max_force;
	float		opt_distance;
	float		vibration;
	int		max_iterations;
	int		animation_intervals;

	int		animation;
	int		opt_distance_gridder;
}
	Springembedder_rf_settings;

extern	Springembedder_rf_settings	springembedder_rf_settings;
extern	Springembedder_rf_settings	init_springembedder_rf_settings(void);
extern	void				save_springembedder_rf_settings(void);

/*	Call_sgraph_procs	*/
extern	void	call_animation_springembedder_rf(Sgraph_proc_info info);
extern	void	call_fast_springembedder_rf(Sgraph_proc_info info);
extern	void	call_springembedder_rf(Sgraph_proc_info info);

/*	Menu callback procs	*/
extern	GraphEd_Menu_Proc menu_springembedder_rf_show_subframe;
extern	GraphEd_Menu_Proc menu_springembedder_rf_fast_springembedder;
extern	GraphEd_Menu_Proc menu_springembedder_rf_animation_springembedder;

extern	void	show_springembedder_rf_subframe (void (*done_proc) ());
extern	int	showing_springembedder_rf_subframe (void);
