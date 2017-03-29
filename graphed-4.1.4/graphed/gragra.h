#ifndef __GRAGRA_H__
#define __GRAGRA_H__
/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/

#include <xview/xview.h> /* fuer Menu und Menu_item */

/************************************************************************/
/*									*/
/*				Embeddings				*/
/*									*/
/************************************************************************/


typedef	struct	embed	{

	Node	right_side;
	Group	embed;
	
}
	*Embedding;


typedef	enum {
	NCE_1,
	NLC,
	BNLC,
	ENCE_1,
	NUMBER_OF_GRAGRA_TYPES
}
	Gragra_type;

int	gragra_type_to_int (Gragra_type type);
int	int_to_gragra_type (int n);

extern	char	*gragra_type_strings[];
extern	char	*gragra_type_strings_for_cycle[];

extern	char	*attribute_type_strings[];
extern	char	*attribute_type_strings_for_cycle[];

extern	int	embed_match_node		(Node in_production, Node match);
extern	int	embed_match_edge		(Edge in_production, Edge match);
extern	void	make_local_embedding		(Gragra_type type, Node node, struct graph *use_prod);
extern	void	make_new_edge_from_embedding 	(Edge in_production, Gragra_type type, Edge derive_edge, int where_are_the_endpoints, Node source_or_target);
extern	void	make_global_embedding		(Gragra_type type, Node node, struct graph *use_prod, struct graph *embed_prod);
extern	int	additional_bnlc_test		(struct graph *prod);

typedef	struct	nce1_gragra	{

	Group		right_side;
	Group		left_side;
	Embedding	embed_in, embed_out;

#ifdef LP_LAYOUT
	Lp_nce1_gragra	lp_nce1_gragra;
#endif
}
	Nce_1_gragra;


typedef	struct	gragra	{
	
	Gragra_type	type;
	
	union {
		Nce_1_gragra	nce1;
	} gra;
}
	Gragra_prod;

#define	empty_embedding	((Embedding)NULL)


extern	int	compile_production (struct graph *prod);
extern	int	size_of_embedding  (Embedding embed);
extern	void	free_embedding     (Embedding embed);

extern	int	node_is_nonterminal(Node node);
extern	int	node_is_terminal   (Node node);
extern	int 	graph_is_global_embedding_to(struct graph *embed, struct graph * prod);

extern	struct picklist	*compile_production_error_list;
extern	char		*compile_production_error_message;

extern	Group	apply_production (struct graph * prod, Node node);

extern	void	pretty_print_production  (struct graph *prod);

extern	void menu_convert_to_gragra(Menu menu, Menu_item menu_item);

#endif
