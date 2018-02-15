/************************************************************************************************/
/******					lpr_Edge					   ******/
/************************************************************************************************/

extern	lpr_Edge	create_lpr_edge(void);
extern	lpr_Edge	create_lpr_edge_with_source_and_target(lpr_Node source, lpr_Node target);

extern	void		lpr_edge_SET_LABEL(lpr_Edge edge, char *label);
extern	void		lpr_edge_SET_GENERATED_EDGES(lpr_Edge edge, lpr_Hierarchie hierarchie);
extern	void		lpr_edge_SET_FOLLOWING_EDGES(lpr_Edge edge, lpr_Hierarchie hierarchie);
extern	void		lpr_edge_SET_PROD_ISO(lpr_Edge edge, Edge prod_edge);
extern	void		lpr_edge_SET_GRAPH_ISO(lpr_Edge edge, Edge graph_edge);
extern	void		lpr_edge_SET_SOURCE(lpr_Edge edge, lpr_Node node);
extern	void		lpr_edge_SET_TARGET(lpr_Edge edge, lpr_Node node);
extern	void		lpr_edge_SET_SOURCE_LABEL(lpr_Edge edge, char *label);
extern	void		lpr_edge_SET_TARGET_LABEL(lpr_Edge edge, char *label);
extern	void		lpr_edge_SET_EDGETYPE(lpr_Edge edge, lpr_EDGETYPE type);

extern	char*		lpr_edge_GET_LABEL(lpr_Edge edge);
extern	lpr_Hierarchie	lpr_edge_GET_GENERATED_EDGES(lpr_Edge edge);
extern	lpr_Hierarchie	lpr_edge_GET_FOLLOWING_EDGES(lpr_Edge edge);
extern	Edge		lpr_edge_GET_PROD_ISO(lpr_Edge edge);
extern	Edge		lpr_edge_GET_GRAPH_ISO(lpr_Edge edge);
extern	lpr_Node	lpr_edge_GET_SOURCE(lpr_Edge edge);
extern	lpr_Node	lpr_edge_GET_TARGET(lpr_Edge edge);
extern	char*		lpr_edge_GET_SOURCE_LABEL(lpr_Edge edge);
extern	char*		lpr_edge_GET_TARGET_LABEL(lpr_Edge edge);
extern	lpr_EDGETYPE	lpr_edge_GET_EDGETYPE(lpr_Edge edge);

extern	void		lpr_edge_create_copy_of_prod_edges(Graph graphed_prod, lpr_Graph lpr_graph);
extern	void		lpr_edge_create_embedding_edges(lpr_Graph copy_of_production, lpr_Node derivated_node);

extern	void		free_lpr_edge(lpr_Edge edge);

/************************************************************************************************/
/******					lpr_Edgelist					   ******/
/************************************************************************************************/

extern	lpr_Edgelist	create_lpr_edgelist(void);
extern	lpr_Edgelist	create_lpr_edgelist_with_edge(lpr_Edge edge);

extern	void		lpr_edgelist_SET_EDGE(lpr_Edgelist list, lpr_Edge edge);

extern	lpr_Edge	lpr_edgelist_GET_EDGE(lpr_Edgelist list);

extern	lpr_Edgelist	add_edgelist_to_lpr_edgelist(lpr_Edgelist old_list, lpr_Edgelist new);
extern	lpr_Edgelist	add_edge_to_lpr_edgelist(lpr_Edgelist old_list, lpr_Edge new);
extern  lpr_Edgelist 	copy_lpr_edgelist(lpr_Edgelist original);
extern  lpr_Edgelist 	remove_edgelist_from_lpr_edgelist(lpr_Edgelist list, lpr_Edgelist elem);
extern	void		free_lpr_edgelist(lpr_Edgelist list);
extern	void		free_lpr_edgelist_with_edge_and_in_source(lpr_Edgelist list);
extern	void		free_lpr_edgelist_with_edge_and_in_target(lpr_Edgelist list);

extern	void		clear_edgelist_in_source(lpr_Edge edge);
extern	void		clear_edgelist_in_target(lpr_Edge edge);


