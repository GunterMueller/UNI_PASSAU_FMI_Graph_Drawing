
extern	void	WIN_create_parser_subframe(Menu menu, Menu_item menu_item);


#define	diplom_lamshoft_init_user_menu() 								\
	{												\
		add_to_user_menu( "test grammar ORGINAL", create_lamshoft_parser );			\
		add_to_user_menu( "test grammar EXTENSIONS", create_parser_with_layout_extensions );	\
	}

/*-->@	WIN.								*/

EXTERN	struct {
	Frame		baseframe;
	Panel			grammar_panel;
	Panel_item			grammar_text_item;
	Panel_item			number_pes_text_item;
	Panel_item			grammar_scan_button;
	Panel_item			grammar_info_button;
	Panel_item			grammar_options_button;
	Panel			graph_panel;
	Panel_item			graph_text_item;
	Panel_item			graph_scan_button;
	Panel			parser_panel;
	Panel_item			parser_text_item;
	Panel_item			parser_run_button;
	Panel_item			parser_stop_button;
	Panel_item			parser_reset_button;
	Panel_item			parser_result_text_item;
	Panel_item			activate_tracer_button;

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/

	Panel			redraw_panel;
	Panel_item				redraw_text_item;
	Panel_item				test_structure_button;
	Panel_item				test_big_structure_button;
	Panel_item				attribute_button;
	Panel_item				derivated_node_button;
	Panel_item				recompute_button;

	Frame			derivated_node_frame;
	Panel				derivated_node_panel;
	int					what_to_do_with_derivated_node;

	Panel					node_sizes_panel;
	Panel_item				node_size_item;
	int					create_with_graph_nodesizes;


	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/

	Frame		scan_options_baseframe;
	Panel			scan_options_panel;
	int		scan_from_where;
	
	Frame		gram_info_baseframe;
	Panel			gram_info_panel;
	Panel_item			gram_directed_text_item;
	Panel_item			gram_boundary_text_item;
	Panel_item			gram_start_symbol_text_item;
	Panel_item			gram_nodelabels_text_item;
	Panel_item			gram_edgelabels_text_item;
	
	Frame		parser_options_baseframe;
	Panel			parser_options_panel;
	Panel_item			parser_stop_with_first_startnode_choice;
	Panel_item			parser_link_iso_pes_choice;
	Panel_item			warning_text_item;

	} WIN;

/**/
