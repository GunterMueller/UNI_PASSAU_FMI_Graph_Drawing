#ifndef __LP_LGG_TO_GRAPHED_H__
#define __LP_LGG_TO_GRAPHED_H__

/******					gragra.c					******/
extern	void	lp_production_test_and_change		(struct graph *prod);
extern	void	lp_update_derived_edge			(struct edge *derived_edge, struct edge *prod_edge, struct edge *new_edge);
extern	void	lp_apply_production			(struct graph *prod, struct node *node, Group copy_of_right_side);
extern	int	grammar_fits_algorithm_conditions	(int alg);
extern	int	lp_lgg_derivation_tests			(struct graph *graph);
/******					nnode.c						******/
extern	void	init_lp_node				(struct node *node);
extern	void	delete_lp_node				(struct node *node);

/******					eedge.c						******/
extern	void	init_lp_edge				(struct edge *edge);
extern	void	delete_lp_edge				(struct edge *edge);
extern	void	copy_lp_edge_without_line		(struct edge *new_edge, struct edge *old_edge);

/***************************************ggraph.c**********************************************/
extern	void	init_lp_graph				(struct graph *graph);
extern	void	clear_lp_graph				(struct graph *graph);

/***************************************dispatch.c********************************************/
extern	int	lp_test_if_node_can_be_derivated	(struct node *node);

/***************************************main.c************************************************/
extern	void	lp_add_items_to_layout_menu		(void);
extern	void	lp_init_lgg_settings			(void);

/***************************************state.c************************************************/
extern	void		init_lp_graph_state		(void);
extern	void		lp_set_production_deleted	(int value);

#endif
