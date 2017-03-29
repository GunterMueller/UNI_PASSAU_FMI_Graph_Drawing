{
	Menu	menu;

	woods_settings = init_woods_settings();

	menu = graphed_create_submenu ();
	add_entry_to_menu (menu,
		"run algoritm",
		menu_woods_planar_layout);
	add_entry_to_menu (menu,
		"settings ...",
		menu_woods_planar_layout_settings);
	add_menu_to_layout_menu ("planar / woods", menu);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "planar_woods",
		LS_FULL_NAME,      "Planar / Woods",
		LS_CALL_PROC,      call_woods_planar_layout,
		LS_SETTINGS,       &woods_settings,
		LS_SHOW_SETTINGS,  show_woods_subframe,
		LS_ACTIVE,         TRUE,
		NULL);	
}
