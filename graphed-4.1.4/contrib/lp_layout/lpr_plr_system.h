/*******************************************************************
	Funktionen aus lpr_plr_system.c, die extern verwendbar
	sind.
*******************************************************************/
extern plrs_Node	create_plrs_node(void);
extern plrs_Node	add_plrs_node(plrs_Node list, plrs_Node element);
extern void remove_plrs_node_from_graph (plrs_Node node, plr_System system);

extern plrs_Edge	create_plrs_edge(plrs_Node source, int length, plrs_Node target);
extern void add_plrs_edge(plrs_Edge element);
extern int plrs_edge_exists(plrs_Node node1, int length, plrs_Node node2);
extern void inherit_all_plrs_edges(plrs_Node node1, plrs_Node node2);
extern void inherit_all_source_edges(plrs_Node node1, plrs_Node node2);
extern void inherit_all_target_edges(plrs_Node node1, plrs_Node node2);
extern void free_plrs_edges_of_one_node(plrs_Node plrs_node);

extern Dependency_list create_dep_list_with_node(plrs_Node plrs_node);
extern Dependency_list add_dep_to_dep_list(Dependency_list list, Dependency_list element);
extern void free_dep_list(Dependency_list list);
extern Dependency_list free_last_of_dep_list(Dependency_list list);
extern Dependency_list free_first_of_dep_list(Dependency_list list);
extern Dependency_list copy_dep_list(Dependency_list original);

extern plrs_Nodelist create_plrs_nodelist_with_node(plrs_Node plrs_node);
extern plrs_Nodelist add_plrs_nodelist_to_plrs_nodelist(plrs_Nodelist list, plrs_Nodelist element);
extern void free_plrs_nodelist(plrs_Nodelist list);
extern void free_plrs_nodes_with_edges(plrs_Node list);

extern plr_System create_plr_system(void);
extern plr_System compute_plr_system(lpr_Node node, int lpr_grid);
extern void free_plr_system(lpr_Node node);
