#if defined SUN_VERSION
extern	Slist	Search_Leaves_in_PQtree(PQtree tree, int number, Snode node, int *length);
extern	PQtree	BUBBLE_PLANAR(PQtree T, Slist S, int S_LENGTH);
extern	PQtree	BUBBLE_MPG(PQtree T, Slist S, int S_LENGTH);
extern	PQtree	REDUCE(PQtree T, Slist S, int S_LENGTH);
extern	bool	COMPUTE_MAXPLANAR_VALUES(Slist S, int S_LENGTH, Slist *DEL_STACK);
extern	PQtree	Reduce_Pertinent_Leaves_To_Unique_Leaf(Slist S, int S_LENGTH);
extern	void	DELETE_MINIMUM_PERTINENT_LEAVES(Slist STACK);

#else
extern	Slist	Search_Leaves_in_PQtree(PQtree, int, Snode, int *);
extern	PQtree	BUBBLE_PLANAR(PQtree, Slist, int);
extern	PQtree	BUBBLE_MPG(PQtree, Slist, int);
extern	PQtree	REDUCE(PQtree, Slist, int);
extern	bool	COMPUTE_MAXPLANAR_VALUES(Slist, int, Slist *);
extern	PQtree	Reduce_Pertinent_Leaves_To_Unique_Leaf(Slist, int);
extern	void	DELETE_MINIMUM_PERTINENT_LEAVES(Slist);
#endif
