/* (C) Universitaet Passau 1986-1994 */

typedef	struct {
	int	grid;
	int	grid_defaults;
}
	Minimal_bends_settings;

extern	Minimal_bends_settings	minimal_bends_settings;
extern	Minimal_bends_settings	init_minimal_bends_settings(void);

extern	void	minimal_bends_layout (Sgraph eingabegraph, Minimal_bends_settings settings);

/* Menu procs	*/
extern	GraphEd_Menu_Proc menu_minimal_bends_layout;
extern	GraphEd_Menu_Proc menu_minimal_bends_layout_settings;

/* call_sgraph_proc's	*/
extern	void	call_minimal_bends_layout (Sgraph_proc_info info);
extern	void	check_and_call_minimal_bends_layout (Sgraph_proc_info info);

/* others */
extern	void	show_bends_subframe (void );
extern	int	showing_bends_subframe (void);
extern	void	save_minimal_bends_settings (void);


