{
	Menu	menu;
	
	minimal_bends_settings = init_minimal_bends_settings ();

	menu = graphed_create_submenu();
	add_entry_to_menu (menu,
		"run algorithm",
		menu_minimal_bends_layout);
	add_entry_to_menu (menu,
		"settings ...",
		menu_minimal_bends_layout_settings);
	
	add_menu_to_layout_menu ("Planar / Bends Minimization", menu);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "planar_minimal_bends",
		LS_FULL_NAME,      "Planar / Minimal Bends",
		LS_CALL_PROC,      check_and_call_minimal_bends_layout,
		LS_SETTINGS,       &minimal_bends_settings,
		LS_SHOW_SETTINGS,  show_bends_subframe,
		LS_ACTIVE,         TRUE,
		NULL);
}
