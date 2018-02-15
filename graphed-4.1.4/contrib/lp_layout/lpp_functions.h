extern	lpp_Parsing_element			new_parsing_element(void);
extern	void				add_to_source_and_target_edges(lpp_Parsing_element source, lpp_Parsing_element target, char *label);
extern	void				free_parsing_element(lpp_Parsing_element cur);
extern	lpp_Parsing_element			create_parsing_element_from_group(Set_of_parsing_elements set, char *label);

extern	int				is_in_nodeset(lpp_Parsing_element element, Nodelist list);
extern	Nodelist			new_nodelist(Node node, Edge edge);
extern	Nodelist			add_to_nodelist(Nodelist list, Nodelist cur);
extern	void				free_nodelist(Nodelist list);

extern	Set_of_parsing_elements		new_set_of_parsing_elements(lpp_Parsing_element target);
extern	Set_of_parsing_elements		add_to_set_of_parsing_elements(Set_of_parsing_elements list, Set_of_parsing_elements cur);
extern	Set_of_parsing_elements		delete_from_set_of_parsing_elements(Set_of_parsing_elements set, Set_of_parsing_elements element);
extern	Set_of_parsing_elements		union_to_set_of_parsing_elements(Set_of_parsing_elements group_1, Set_of_parsing_elements group_2);
extern	Set_of_parsing_elements		union_to_tree(Set_of_parsing_elements group1, Set_of_parsing_elements group2);
extern	int				set_of_parsing_elements_consists_one_with_level(Set_of_parsing_elements group, int hierarchy_level);
extern	int				is_in_set(lpp_Parsing_element elem, Set_of_parsing_elements group);
extern	int				edge_exists_from(Set_of_parsing_elements node, Set_of_parsing_elements group);
extern	int				disjunkt(lpp_Parsing_element node, Set_of_parsing_elements group);
extern	void				free_set_of_parsing_elements(Set_of_parsing_elements list);

extern	Edge_list			new_edgelist(lpp_Parsing_element source, lpp_Parsing_element target, char *label);
extern	Edge_list			add_to_edgelist(Edge_list list, Edge_list cur);
extern	void				free_edgelist(Edge_list list);

extern	Int_list			new_intlist(int nr);
extern	Int_list			add_to_intlist(Int_list list, int nr);
extern	int				is_in_intlist(Int_list list, int nr);
extern	void				free_intlist(Int_list list);

extern	Set_of_nodelist			new_set_of_nodelist(Nodelist list);
extern	Set_of_nodelist			add_to_set_of_nodelist(Set_of_nodelist list, Set_of_nodelist element);
extern	void				free_set_of_nodelist(Set_of_nodelist list);

extern	Derivation			new_derivation(void);
extern	Derivation			add_to_derivations(Derivation list, Derivation new);
extern	void				free_derivation(Derivation list);
extern	int				edge_does_not_already_exist(Edge_list edge, lpp_Parsing_element source, lpp_Parsing_element target);
