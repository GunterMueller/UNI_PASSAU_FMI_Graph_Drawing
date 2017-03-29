#if defined SUN_VERSION
extern	void	init_random_number_generator(void);
extern	Slist	get_slist_elem_by_random(Slist list, int elements);
extern	Sgraph	create_randomized_graph(int max_random_nodes);
#else
extern	void	init_random_number_generator(void);
extern	Slist	get_slist_elem_by_random(Slist, int);
extern	Sgraph	create_randomized_graph(int);
#endif
