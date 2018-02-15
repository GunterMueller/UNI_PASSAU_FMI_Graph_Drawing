{
	Menu	menu;

	layout_suite_settings = init_layout_suite_settings();

	menu = graphed_create_submenu ();

	add_entry_to_menu (menu,
		"this graph",
		menu_layout_suite);
	add_entry_to_menu (menu,
		"all files *.g in current directory",
		menu_layout_suite_all);
	add_entry_to_menu (menu,
		"settings ...",
		menu_layout_suite_subframe);
	add_menu_to_layout_menu ("Layout Suite", menu);
}
