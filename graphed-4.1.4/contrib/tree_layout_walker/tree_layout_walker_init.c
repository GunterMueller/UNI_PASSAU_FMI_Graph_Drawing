{
	Menu	menu;

	tree_layout_walker_settings = init_tree_layout_walker_settings ();

	menu = graphed_create_pin_menu ("Tree Layout");
	add_entry_to_menu (menu,
		"top-to-botttom",
		menu_tree_layout_walker);
	add_entry_to_menu (menu,
		"left-to-right",
		menu_tree_layout_walker_left_to_right);
	add_entry_to_menu (menu,
		"settings ...",
		menu_tree_layout_walker_settings);
	add_menu_to_layout_menu ("Tree Layout", menu);

	ls_algorithm_set (ls_algorithm_create(),
		LS_NAME,           "tree_walker",
		LS_FULL_NAME,      "Tree Layout (Walker)",
		LS_CALL_PROC,      call_tree_layout_walker,
		LS_SETTINGS,       &tree_layout_walker_settings,
		LS_SHOW_SETTINGS,  show_tree_layout_walker_subframe,
		LS_ACTIVE,         TRUE,
		NULL);	
}
