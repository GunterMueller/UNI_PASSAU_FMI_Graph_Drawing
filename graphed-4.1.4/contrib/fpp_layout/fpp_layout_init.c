{
	Menu	menu;

	fpp_settings = init_fpp_settings();

	menu = graphed_create_pin_menu ("planar / FPP");

	add_entry_to_menu (menu,
		"FPP layout",
		menu_fpp_layout);
	add_entry_to_menu (menu,
		"Asslia layout",
		menu_fpp_layout_asslia);
	add_entry_to_menu (menu,
		"FPP with compression",
		menu_fpp_layout_compression);
	add_entry_to_menu (menu,
		"settings ...",
		menu_fpp_subframe);
	add_menu_to_layout_menu ("planar / FPP", menu);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "FPP_layout",
		LS_FULL_NAME,      "Planar / FPP",
		LS_CALL_PROC,      call_fpp_layout,
		LS_SETTINGS,       &fpp_settings,
		LS_SHOW_SETTINGS,  show_fpp_subframe,
		LS_ACTIVE,         TRUE,
		NULL);	

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "FPPA_layout",
		LS_FULL_NAME,      "Planar / FPP & Asslia",
		LS_CALL_PROC,      call_fpp_layout_asslia,
		LS_SETTINGS,       &sugiyama_settings,
		LS_SHOW_SETTINGS,  show_fpp_subframe,
		LS_ACTIVE,         TRUE,
		NULL);	
}
