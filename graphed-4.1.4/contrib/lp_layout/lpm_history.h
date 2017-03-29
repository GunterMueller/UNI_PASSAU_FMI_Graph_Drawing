extern	void		create_elements_for_multi_lgg(tree_ref father);
extern	Multi_edge	new_multi_lp_list(Lp_of_father target);
extern	Multi_edge	add_to_multi_lp_list(Multi_edge list, Multi_edge new);
extern	void		create_LP_costs(tree_ref father);
extern	void		create_multi_edge_for_root(tree_ref father, tree_ref son);
extern	void		create_multi_lp_elements(tree_ref father, tree_ref son);
extern	Lp_of_son	create_multi_lp_for_lower(Graph graph);
