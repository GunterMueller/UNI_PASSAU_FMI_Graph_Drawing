{
  Menu	menu;

  menu = graphed_create_pin_menu ("www");
  add_entry_to_menu (menu, "open www", menu_www_open);
  add_entry_to_menu (menu, "remove trees", menu_remove_trees);
#if defined(WWW_SNOOPER) || defined(DIFF_4_0_21_TO_22)
  add_entry_to_menu (menu, "www snooper", menu_www_snooper);
#endif

  add_menu_to_goodies_menu ("www", menu);
}
