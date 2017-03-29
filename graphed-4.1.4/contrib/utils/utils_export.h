extern void move_sgraph (Sgraph g, int x, int y);
extern void swap_width_and_height_in_nodes (Sgraph g);

extern GraphEd_Menu_Proc menu_call_turn_left_sgraph;
extern GraphEd_Menu_Proc menu_call_turn_right_sgraph;
extern GraphEd_Menu_Proc menu_call_mirror_q1_sgraph;
extern GraphEd_Menu_Proc menu_call_mirror_q2_sgraph;
extern GraphEd_Menu_Proc menu_call_mirror_horizontal_sgraph;
extern GraphEd_Menu_Proc menu_call_mirror_vertical_sgraph;

extern GraphEd_Menu_Proc menu_call_larger_sgraph;
extern GraphEd_Menu_Proc menu_call_smaller_sgraph;
extern GraphEd_Menu_Proc menu_call_swap_width_and_height_in_nodes;

extern GraphEd_Menu_Proc menu_test_call_external_program;
extern GraphEd_Menu_Proc menu_test_test_proc;
extern GraphEd_Menu_Proc menu_test3_proc;
extern GraphEd_Menu_Proc menu_test4_proc;
extern GraphEd_Menu_Proc menu_test5_proc;
extern GraphEd_Menu_Proc menu_test6_proc;

extern void turn_left_sgraph(Sgraph g);
extern void turn_right_sgraph(Sgraph g);

extern void call_fit_nodes_to_text(Sgraph_proc_info info);
extern void call_set_dummy_coordinates_and_labels(Sgraph_proc_info info);

extern GraphEd_Menu_Proc menu_planarify;
extern void call_planarify (Sgraph_proc_info info);
