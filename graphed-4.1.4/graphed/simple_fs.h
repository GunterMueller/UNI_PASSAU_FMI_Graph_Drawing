typedef enum {
	SFS_LOAD_SGRAPH,
	SFS_STORE_SGRAPH,
	SFS_LOAD,
	SFS_STORE
}
	SFS_load_or_store;

extern	void	show_simple_selection_subframe (SFS_load_or_store load_or_store, void (*proc) ());
