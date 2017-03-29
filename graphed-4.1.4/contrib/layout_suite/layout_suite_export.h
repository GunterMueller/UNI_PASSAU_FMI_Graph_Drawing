/* (C) Universitaet Passau 1986-1994 */

#ifndef LAYOUT_SUITE_HEADERS
#define LAYOUT_SUITE_HEADERS

typedef	struct	ls_algorithm {

	char	*name;
	char	*full_name;
	void	(*call_proc)();
	void	(*test_proc)();

	void	(*show_settings)();
	void	(*showing_settings)();
	void	(*done_settings)();
	char	*settings;

	int	active;	

	Panel_item	active_item;
	Panel_item	settings_item;
	Panel_item	do_item;
}
	*LS_algorithm;


typedef enum {
	END_OF_LS_ALGORITHM_SPECS = 0,

	LS_NAME,
	LS_FULL_NAME,

	LS_CALL_PROC,
	LS_TEST_PROC,
	
	LS_SETTINGS,
	LS_SHOW_SETTINGS,
	LS_SHOWING_SETTINGS,
	LS_SETTINGS_DONE,

	LS_ACTIVE,

	LS_ACTIVE_ITEM,
	LS_SETTINGS_ITEM,
	LS_DO_ITEM
}
	LS_algorithm_specs;


typedef	struct {

	Slist	algorithms;

	int	create_graph_file;
	int	create_info_file;
	int	create_xbitmap_file;
	int	create_postscript_file;

	int	save_and_reload;
	int	label_graph;
}
	Layout_suite_settings;

#define ls_list_algorithm(l) attr_data_of_type((l),LS_algorithm)

extern	Layout_suite_settings layout_suite_settings;
extern	Layout_suite_settings init_layout_suite_settings(void);

/* Misc	*/
extern	void	save_layout_suite_settings (void);
extern	void	show_layout_suite_subframe (void (*done_proc) ());
extern	int	showing_layout_suite_subframe (void);

extern	void	layout_suite (Layout_suite_settings settings);
	
/* Menu callback procs	*/
extern	GraphEd_Menu_Proc menu_layout_suite;
extern	GraphEd_Menu_Proc menu_layout_suite_all;
extern	GraphEd_Menu_Proc menu_layout_suite_subframe;

extern	LS_algorithm	ls_algorithm_create(void);
extern	void		ls_algorithm_set(LS_algorithm algorithm, ...);
extern	void		ls_algorithm_delete(LS_algorithm algorithm);

#endif
