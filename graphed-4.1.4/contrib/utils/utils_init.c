{
	Menu		trans_menu, test_menu;
	/* extern	Menu	wrappers_menu; */

	trans_menu = graphed_create_pin_menu ("Transformations");
	add_entry_to_menu (trans_menu,
		"turn left", menu_call_turn_left_sgraph);
	add_entry_to_menu (trans_menu,
		"turn right", menu_call_turn_right_sgraph);
	add_entry_to_menu (trans_menu,
		"mirror horizontal", menu_call_mirror_horizontal_sgraph);
	add_entry_to_menu (trans_menu,
		"mirror vertical",   menu_call_mirror_vertical_sgraph);
	add_entry_to_menu (trans_menu,
		"mirror q1", menu_call_mirror_q1_sgraph);
	add_entry_to_menu (trans_menu,
		"mirror q2", menu_call_mirror_q2_sgraph);
	add_entry_to_menu (trans_menu,
		"larger",  menu_call_larger_sgraph);
	add_entry_to_menu (trans_menu,
		"smaller", menu_call_smaller_sgraph);
	add_entry_to_menu (trans_menu,
		"swap node width/height",
		menu_call_swap_width_and_height_in_nodes);
	add_menu_to_goodies_menu ("Transformations", trans_menu);


	test_menu = graphed_create_pin_menu ("GraphEd Internal Test Suite");
	add_entry_to_menu (test_menu,
		"TEST 1", menu_test_call_external_program);
	add_entry_to_menu (test_menu,
		"TEST 2", menu_test_test_proc);
	add_entry_to_menu (test_menu,
		"TEST 3", menu_test3_proc);
	add_entry_to_menu (test_menu,
		"TEST 4", menu_test4_proc);
	add_entry_to_menu (test_menu,
		"TEST 5", menu_test5_proc);
	add_entry_to_menu (test_menu,
		"TEST 6", menu_test6_proc);
#ifdef GRAPHED_PRIVATE
	add_menu_to_goodies_menu ("Tests", test_menu);
#endif

	add_entry_to_menu (wrappers_menu, "Planarify", menu_planarify);
}
