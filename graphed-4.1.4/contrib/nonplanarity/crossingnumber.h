#if defined SUN_VERSION
extern	int	crossingnumber_naive_embedding(Sgraph inG);
extern	int	crossingnumber_complete_embedding(Sgraph inG);
extern	int	crossingnumber_bipartite_embedding(Sgraph inG);
extern	Slist	get_edges_not_in_mpg(Sgraph g);
extern	int	embed_remaining_edges(Sgraph inG);
extern  int     count_all_edge_crossings_in_graph(Sgraph g, int *ncount, int *ecount);
#else
extern	int	crossingnumber_naive_embedding(Sgraph);
extern	int	crossingnumber_complete_embedding(Sgraph);
extern	int	crossingnumber_bipartite_embedding(Sgraph);
extern	Slist	get_edges_not_in_mpg(Sgraph);
extern	int	embed_remaining_edges(Sgraph);
extern  int     count_all_edge_crossings_in_graph(Sgraph, int *, int *);
#endif
