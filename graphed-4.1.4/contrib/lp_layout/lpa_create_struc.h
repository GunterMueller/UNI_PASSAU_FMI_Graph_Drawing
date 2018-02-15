extern	LPA_Array_of_productions	create_lpa_array_of_productions	(void);
extern	LPA_Production			create_lpa_production		(int number);

extern	LPA_Dependency			create_lpa_dependency		(void);
extern	LPA_Dependency			insert_in_lpa_dependency	(LPA_Dependency old, LPA_Dependency new);
extern	LPA_Dependency			lpa_create_copy_of_dependency	(LPA_Dependency old);
extern	void				free_lpa_dependency		(LPA_Dependency dep);

extern	LPA_Array_of_nodes		create_lpa_array_of_nodes	(void);
extern	LPA_Node			create_lpa_node			(int number);

extern	LPA_Upper_prod_array		create_lpa_upper_prod_array	(void);
extern	LPA_Upper_prod			create_lpa_upper_prod		(int nr);

extern	LPA_Lower_prod			create_lpa_lower_prod		(int width, int height);
extern	LPA_Lower_prod			create_copy_of_lpa_lower_array	(LPA_Lower_prod source, int width, int height);

extern	LPA_Sizes			create_lpa_sizes		(int nr);
extern	LPA_Sizes			lpa_compact_and_sort_sizes	(LPA_Sizes sizes, int *len);

extern	void				free_area_structures		(LPA_Upper_prod_array head);
