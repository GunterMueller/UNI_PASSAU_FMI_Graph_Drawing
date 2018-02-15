
extern	File_attributes	new_file_attributes (void);
extern	void	free_file_attributes (File_attributes list);

extern	File_attributes	get_graph_file_attributes (struct graph *graph);
extern	File_attributes	get_node_file_attributes (struct node *node);
extern	File_attributes	get_edge_file_attributes (struct edge *edge);

extern	void	set_graph_file_attributes (struct graph *graph, File_attributes attrs);
extern	void	set_node_file_attributes (struct node *node, File_attributes attrs);
extern	void	set_edge_file_attributes (struct edge *edge, File_attributes attrs);

extern	void	print_file_attributes (FILE *file, File_attributes list);
