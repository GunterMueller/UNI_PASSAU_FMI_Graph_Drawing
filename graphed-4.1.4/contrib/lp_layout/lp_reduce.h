extern	void 		set_flag			(tree_ref tree);
extern	void 		reset_flag			(tree_ref tree);
extern  history_ref	create_unflagged_history	(history_ref first);
extern  char		*get_label			(history_ref first);
extern	int 		edge_exists 			(Node source, Node target, char *lab);
extern	void 		delete_graphed_nodes_of_tree	(tree_ref tree);
extern	void 		reduce_tree			(void);
