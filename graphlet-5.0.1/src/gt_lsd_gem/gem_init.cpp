/* This software is distributed under the Lesser General Public License */

  Menu menu;

  gem_init_graph();
  set_gem_default_config();
  menu = graphed_create_submenu();
  add_entry_to_menu(menu, "run algorithm", menu_gem_layout);
  add_entry_to_menu(menu, "settings ...", menu_gem_show_subframe);
  add_menu_to_layout_menu("Spring Embedder (Gem)", menu);

ls_algorithm_set(ls_algorithm_create(),
	LS_NAME,		"gem",
	LS_FULL_NAME,		"Spring Embesser (Gem)",
	LS_CALL_PROC,		call_gem,
	LS_SHOW_SETTINGS,	show_gem_subframe,
	LS_ACTIVE,		TRUE,
	NULL);
}

void menu_gem_layout(Menu menu, Menu_item menu_item)
{
  read_config();
  call_sgraph_proc(call_gem, NULL);
}
