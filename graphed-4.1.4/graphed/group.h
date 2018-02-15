/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	GROUP_HEADER
#define GROUP_HEADER
	
extern  int	group_nodes_are_all_of_same_graph (Group group);

extern	Group	new_group		 (Node node);
extern	Group	add_to_group		 (Group group, Node node);
extern	Group	add_immediately_to_group (Group group, Node node);
extern	Group	subtract_from_group	 (Group group, Node node);
extern	Group	subtract_immediately_from_group (Group group, Group g);
extern	Group	add_groups		 (Group g1, Group g2);
extern	Group	add_groups_disjoint	 (Group g1, Group g2);
extern	Group	subtract_groups		 (Group g1, Group g2);
extern	void	free_group		 (Group group);
extern	Group	copy_group		 (Group group);

extern	void	group_set (Group group, ...);


extern	int	group_contains_exactly_one_node	(Group group);
extern	Group	contains_group_node		(Group group, Node node);
extern	int	contains_group_graph		(Group group, Graph graph);
extern	int	intersects_group_graph		(Group group, Graph graph);
extern	int	group_intersects_group		(Group group1, Group group2);

extern	Rect	compute_rect_around_group       (Group group);
extern	int	size_of_group			(Group group);


extern	Group	make_group_of_graph		(Graph graph);
extern	Group	make_group_of_all		(void);
extern	Group	copy_group_to_graph		(Group group, Graph graph);
extern	Group	make_group_of_sourcelist	(Node node);
extern	Group	make_group_of_targetlist	(Node node);

extern	Graph	make_graph_of_group		(int buffer, Group group);

extern   void	move_group                      (Group group, int dx, int dy, int only_set);
#endif
