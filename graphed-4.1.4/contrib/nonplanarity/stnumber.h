#if defined SUN_VERSION
extern	int	STNUMBER(Sgraph graph, Snode *ps_node, Snode *pt_node);
extern	int	DETERMINE_BICONNECTED_COMPONENTS(Sgraph G, Slist *ComponentList);
extern	Sgraph	DFS_SPANNING_TREE(Sgraph graph, Slist *rest_list);
extern	void	Decompose_Graph_Into_Biconnected_Components(Sgraph g);
extern  int     is_bipartite(Sgraph in_graph);
#else 
extern	int	STNUMBER(Sgraph, Snode *, Snode *);
extern	int	DETERMINE_BICONNECTED_COMPONENTS(Sgraph, Slist *);
extern	Sgraph	DFS_SPANNING_TREE(Sgraph, Slist *);
extern	void	Decompose_Graph_Into_Biconnected_Components(Sgraph);
extern  int     is_bipartite(Sgraph);
#endif
