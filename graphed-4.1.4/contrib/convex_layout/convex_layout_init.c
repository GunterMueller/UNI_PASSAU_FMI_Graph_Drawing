{
	Menu	menu;

	convex_draw_settings = init_convex_draw_settings();

	menu = graphed_create_submenu ();
	add_entry_to_menu (menu,
		"run algorithm",
		menu_convex_layout);
	add_entry_to_menu (menu,
		"run algorithm (structure only)",
		menu_convex_layout_structure_only);
	add_entry_to_menu (menu,
		"settings ...",
		menu_convex_layout_settings);

	add_menu_to_layout_menu ("planar / convex faces", menu);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "Planar_Convex_Faces",
		LS_FULL_NAME,      "Planar / Convex Faces",
		LS_CALL_PROC,      DrawGraphConvexEditable,
		LS_SETTINGS,       &convex_draw_settings,
		LS_SHOW_SETTINGS,  show_convex_draw_subframe,
		LS_ACTIVE,         TRUE,
		NULL);
		
}
