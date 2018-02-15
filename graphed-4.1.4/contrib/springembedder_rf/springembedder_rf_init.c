{
	Menu	menu;
	
	springembedder_rf_settings = init_springembedder_rf_settings();

	menu = graphed_create_submenu();
	add_entry_to_menu (menu,
		"with Animation",
		menu_springembedder_rf_animation_springembedder);
	add_entry_to_menu (menu,
		"without Animation",
		menu_springembedder_rf_fast_springembedder);
	add_entry_to_menu (menu,
		"settings ...",
		menu_springembedder_rf_show_subframe);
	
	add_menu_to_layout_menu ("Spring Embedder (FR)", menu);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "spring_rf",
		LS_FULL_NAME,      "Spring Embedder (FR)",
		LS_CALL_PROC,      call_springembedder_rf,
		LS_SETTINGS,       &springembedder_rf_settings,
		LS_SHOW_SETTINGS,  show_springembedder_rf_subframe,
		LS_ACTIVE,         TRUE,
		NULL);	
}
