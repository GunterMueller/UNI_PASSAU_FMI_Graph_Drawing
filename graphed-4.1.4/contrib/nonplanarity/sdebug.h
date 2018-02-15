#if defined SUN_VERSION
extern	void	printGraph(FILE *file, Sgraph g);
extern	void	printGraphWithAttrs(FILE *file, Sgraph g);
extern	void	print_PQtree(FILE *file, PQtree tree);
extern	Sgraph	loadGraph(FILE *file);
#else
extern	void	printGraph(FILE *, Sgraph);
extern	void	printGraphWithAttrs(FILE *, Sgraph);
extern	Sgraph	loadGraph(FILE *);
#ifdef PQTREE
extern	void	print_PQtree(FILE *, PQtree);
#endif
#endif
