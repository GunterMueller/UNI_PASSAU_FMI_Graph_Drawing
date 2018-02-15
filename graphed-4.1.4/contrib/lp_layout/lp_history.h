extern	history_ref	new_history_ref(void);
extern	history_ref	add_to_history(history_ref liste, history_ref element);
extern	void		create_history_of_edge(tree_ref upper, tree_ref lower);
extern	void		create_history_of_edge(tree_ref upper, tree_ref lower);
extern	void		make_node_father_to_production(Node node_father, Graph prod);
extern	void		create_personal_history_of_edge(Group p);
extern	void		update_history_of_the_embedding(Edge replaced_edge, Edge embedding_edge, Edge new_edge);
extern 	void		free_history(history_ref liste);
extern 	void		init_attributes_of_history_edge_ref(history_edge_ref ref);
extern 	void		init_attributes_of_tree_node_ref(tree_node_ref ref);
extern 	void		init_attributes_of_tree_edge_ref(tree_edge_ref ref);
extern	void		print_histories	(Graph graph);
extern	tree_ref	new_tree_ref(void);
extern	tree_node_ref	new_tree_node_ref(void);
extern	tree_ref	append(tree_ref father, tree_ref new_son, tree_ref old_son);
extern	tree_edge_ref	new_tree_edge_ref(void);
extern	void		lp_increment_derivation_tree(Node node, Graph prod, Group copy_of_right_side);

extern	int		edge_is_out_embedding(Edge edge, Graph p);
