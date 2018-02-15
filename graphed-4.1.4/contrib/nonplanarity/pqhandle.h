#if defined SUN_VERSION
extern	PQtree	Init_PQnode(short int type);
extern	void	enqueue_child(PQtree t, PQtree l);
extern	PQtree	delete_child(PQtree child);
extern	void	delete_leaf_in_proper_PQtree(PQtree leaf);
extern	void	free_complete_PQtree(PQtree tree);
extern	PQtree	Create_Initial_PQtree(Snode node);
extern	PQtree	Create_Pnode_for_N(int number, Snode node);
extern	PQtree	Replace_Leaf(PQtree root, PQtree leaf, PQtree tree);
#else
extern	PQtree	Init_PQnode(short);
extern	void	enqueue_child(PQtree, PQtree);
extern	PQtree	delete_child(PQtree);
extern	void	delete_leaf_in_proper_PQtree(PQtree);
extern	void	free_complete_PQtree(PQtree);
extern	PQtree	Create_Initial_PQtree(Snode);
extern	PQtree	Create_Pnode_for_N(int, Snode);
extern	PQtree	Replace_Leaf(PQtree, PQtree, PQtree);
#endif
