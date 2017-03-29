{
	Menu	menu;

	sugiyama_settings = init_sugiyama_settings();

	menu = graphed_create_submenu ();
	add_entry_to_menu (menu,
		"top to bottom",
		menu_sugiyama_layout);
	add_entry_to_menu (menu,
		"left to right",
		menu_sugiyama_left_to_right_layout);
	add_entry_to_menu (menu,
		"settings ...",
		menu_sugiyama_layout_subframe);
	add_menu_to_layout_menu ("DAG layout", menu);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "DAG_Layout",
		LS_FULL_NAME,      "DAG Layout",
		LS_CALL_PROC,      call_sugiyama_layout,
		LS_SETTINGS,       &sugiyama_settings,
		LS_SHOW_SETTINGS,  show_sugiyama_subframe,
		LS_ACTIVE,         TRUE,
		NULL);	
}
