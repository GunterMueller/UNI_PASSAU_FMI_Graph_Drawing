extern	LP_upper_production		create_lp_upper_production(void);
extern	LP_upper_production		append_to_lp_upper_production(LP_upper_production old_list, LP_upper_production new_elem);
extern	void				free_lp_upper_production_with_layouts_and_lower_derivation(LP_upper_production upper);

extern	LP_array_of_productions		create_lp_array_of_upper_productions(int number_of_layouts);

extern	LP_lower_derivation		create_lp_lower_derivation(void);
extern	LP_lower_derivation		append_to_lp_lower_derivation(LP_lower_derivation old_list, LP_lower_derivation new_elem);
extern	void				free_lp_lower_derivation(LP_lower_derivation lower);

extern	LP_array_of_lower_productions	create_lp_array_of_lower_productions(int number_of_prods);

extern	LP_sizes_array			create_lp_sizes_array(int number);
extern	void				free_lp_sizes_array(LP_sizes_array sizes, int nr_of_sizes);
extern	LP_sizes_ref			create_lp_sizes_ref(int nr);

extern	LP_dependency_graph		create_copy_of_lp_dependency_graph(LP_dependency_graph old);
extern	LP_dependency_graph		insert_in_lp_dependency_graph(LP_dependency_graph old, LP_dependency_graph new);
extern	LP_dependency_graph		create_copy_of_lp_dependency_graph(LP_dependency_graph old);
extern	LP_dependency_graph		create_lp_x_dependency_graph_from_sprod(Sprod prod, LP_lower_derivation lower_der, LP_Parsing_element_list parsing_elements);
extern	LP_dependency_graph		create_lp_y_dependency_graph_from_sprod(Sprod prod, LP_lower_derivation lower_der, LP_Parsing_element_list parsing_elements);
extern	void				free_lp_dependency_graph(LP_dependency_graph head);
