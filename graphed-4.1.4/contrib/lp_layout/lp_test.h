#ifndef __LP_TEST_H__
#define __LP_TEST_H__

#include <graph.h>
#include "lp_graph.h"
extern	int	lp_test_on_frame				(struct graph *prod);
extern	int	rectangular_edgelines 				(struct graph *p);
extern	int 	connection_relations_for_outgoing_bridges	();
extern 	int	grid_layout_production				(struct graph *p, int unit);
extern 	int	unit_layout_production				(struct graph *p, int unit);
extern  int	schnitt_edge_node 				(struct graph *p);
extern	int	edgelines_in_production  			(struct graph *p);
extern	int	degenerated_edges  				(struct graph *p);
extern  int	number_of_in_embedding_edges_is_incorrect	(struct graph *graph);
extern	int	all_embeddings_are_different			(struct graph *p);
extern	int	my_strcmp					(char *string1, char *string2);
extern	int	edge_is_no_out_embedding			(struct edge *edge, struct graph *p);
extern	int	inside						(int x, int y, struct node *n);
extern	int	test_for_allowed_first_production		(struct node *node);
extern	void	create_production_isomorphism			(struct graph *production);
extern	int	lp_test_nodes_non_touching			(struct graph *prod);
extern	int	lp_test_non_overlapping_edges			(struct graph *prod);
extern	int	non_term_unit					(struct graph *p, int unit);
extern	int	edge_relabeling_graph_grammar			(void);
extern	int	test_grammar_changed				(int graph_creation_time);
extern	int	is_ENCE_1_GRAMMAR				(struct graph *graph);
extern	int	lp_pre_test					(struct node *node);
extern	void	recompute_all_production_isomorphisms		(void);

extern	int	grid_lp_edgeline				(lp_Edgeline lp, int unit);

#endif
