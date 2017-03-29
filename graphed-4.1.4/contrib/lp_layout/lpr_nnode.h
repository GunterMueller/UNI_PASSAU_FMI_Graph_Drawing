/************************************************************************************************/
/******					lpr_Node					   ******/
/************************************************************************************************/

extern	lpr_Node	create_lpr_node(void);
extern	lpr_Node	create_lpr_node_with_edgelists(lpr_Edgelist source_edges, lpr_Edgelist target_edges);

extern	void		lpr_node_SET_TARGET_EDGES(lpr_Node node, lpr_Edgelist edges);
extern	void		lpr_node_SET_SOURCE_EDGES(lpr_Node node, lpr_Edgelist edges);
extern	void		lpr_node_SET_LATER_SOURCE_EDGES(lpr_Node node, lpr_Edgelist edges);
extern	void		lpr_node_SET_LATER_TARGET_EDGES(lpr_Node node, lpr_Edgelist edges);
extern	void		lpr_node_SET_LABEL(lpr_Node node, char *label);
extern	void		lpr_node_SET_GRAPH(lpr_Node node, lpr_Graph graph);
extern	void		lpr_node_SET_IS_TERMINAL(lpr_Node node);
extern	void		lpr_node_SET_IS_NON_TERMINAL(lpr_Node node);
extern	void		lpr_node_SET_APPLIED_PRODUCTION(lpr_Node node, lpr_Graph graph);
extern	void		lpr_node_SET_PROD_ISO(lpr_Node node, Node prod_node);
extern	void		lpr_node_SET_GRAPH_ISO(lpr_Node node, Node graph_node);
extern	void		lpr_node_SET_NODETYPE(lpr_Node node, lpr_NODETYPE type);

extern	lpr_Edgelist	lpr_node_GET_TARGET_EDGES(lpr_Node node);
extern	lpr_Edgelist	lpr_node_GET_SOURCE_EDGES(lpr_Node node);
extern	lpr_Edgelist	lpr_node_GET_LATER_SOURCE_EDGES(lpr_Node node);
extern	lpr_Edgelist	lpr_node_GET_LATER_TARGET_EDGES(lpr_Node node);
extern	char*		lpr_node_GET_LABEL(lpr_Node node);
extern	lpr_Graph	lpr_node_GET_GRAPH(lpr_Node node);
extern	int		lpr_node_IS_TERMINAL(lpr_Node node);
extern	lpr_Graph	lpr_node_GET_APPLIED_PRODUCTION(lpr_Node node);
extern	Node		lpr_node_GET_PROD_ISO(lpr_Node node);
extern	Node		lpr_node_GET_GRAPH_ISO(lpr_Node node);
extern	lpr_NODETYPE	lpr_node_GET_NODETYPE(lpr_Node node);

extern	void		free_lpr_node(lpr_Node node);
extern	void		free_lpr_node_with_lower_part(lpr_Node node);


/************************************************************************************************/
/******					lpr_Nodelist					   ******/
/************************************************************************************************/

extern	lpr_Nodelist	create_lpr_nodelist(void);
extern	lpr_Nodelist	create_lpr_nodelist_with_node(lpr_Node node);

extern	void		lpr_nodelist_SET_NODE(lpr_Nodelist list, lpr_Node node);

extern	lpr_Node	lpr_nodelist_GET_NODE(lpr_Nodelist list);

extern	lpr_Nodelist	add_nodelist_to_lpr_nodelist(lpr_Nodelist old_list, lpr_Nodelist new);
extern	lpr_Nodelist	add_node_to_lpr_nodelist(lpr_Nodelist old_list, lpr_Node new);

extern	void		free_lpr_nodelist(lpr_Nodelist list);
extern	void		free_lpr_nodelist_with_node(lpr_Nodelist list);
extern	void		free_lpr_nodelist_with_node_and_lower_part(lpr_Nodelist list);


extern	lpr_Nodelist	lpr_node_create_copy_of_prod(Graph graphed_prod, lpr_Graph lpr_graph, lpr_Node derivated_node);
