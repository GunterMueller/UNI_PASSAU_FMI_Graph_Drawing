extern	lpr_Graph	create_lpr_graph(void);
extern	lpr_Graph	create_lpr_graph_with_nodes(lpr_Nodelist nodes);

extern	void		lpr_graph_SET_NODES(lpr_Graph graph, lpr_Nodelist nodes);
extern	void		lpr_graph_SET_PROD_ISO(lpr_Graph graph, Graph prod);
extern	void		lpr_graph_SET_GRAPH_ISO(lpr_Graph graph, Graph graphed_graph);
extern	void		lpr_graph_SET_IN_EMBEDDINGS(lpr_Graph graph, lpr_Edgelist edges);
extern	void		lpr_graph_SET_OUT_EMBEDDINGS(lpr_Graph graph, lpr_Edgelist edges);

extern	lpr_Nodelist	lpr_graph_GET_NODES(lpr_Graph graph);
extern	Graph		lpr_graph_GET_PROD_ISO(lpr_Graph graph);
extern	Graph		lpr_graph_GET_GRAPH_ISO(lpr_Graph graph);
extern	lpr_Edgelist	lpr_graph_GET_IN_EMBEDDINGS(lpr_Graph graph);
extern	lpr_Edgelist	lpr_graph_GET_OUT_EMBEDDINGS(lpr_Graph graph);

extern	void		free_lpr_graph(lpr_Graph graph);

extern	lpr_Graph	lpr_graph_create_copy_of_graphed_prod(Graph graphed_prod, lpr_Node derivated_node);
extern      void 		set_array_of_iso_prod_pointers(lpr_Graph lpr_graph);
