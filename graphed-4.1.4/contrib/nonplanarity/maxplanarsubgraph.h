#if defined SUN_VERSION
extern	Sgraph	maxplanarsubgraph(Sgraph Graph);
extern	Sgraph	maxplanarsubgraph_greedy(Sgraph G);
extern	Sgraph	maxplanarsubgraph_randomized_greedy(Sgraph G);
#else
extern	Sgraph	maxplanarsubgraph(Sgraph);
extern	Sgraph	maxplanarsubgraph_greedy(Sgraph);
extern	Sgraph	maxplanarsubgraph_randomized_greedy(Sgraph);
#endif

