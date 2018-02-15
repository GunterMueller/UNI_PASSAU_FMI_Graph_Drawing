/* FERTIG 130293 */
/********************************************************************************/
/*										*/
/*	MODUL:	  graph_op							*/
/*										*/
/*	FUNKTION: Prozeduren und Funktionen, die als Tools auf Graphen, Knoten, */
/*		  Kanten usw. arbeiten. 					*/
/*										*/
/********************************************************************************/

#ifndef GRAPH_OP_HEADER
#define GRAPH_OP_HEADER

/*-->@	-Dgraph_op

	Benutzt die Datenstrukturen von "types".
*//*-->@   gpo_move_direction

	enum {
		FORWARD,
		BACKWARD
	     } gpo_move_direction

**/

#define FORWARD 	TRUE
#define BACKWARD	FALSE

extern	Parsing_element GPO_next_fitting_pe(struct parsing_element *plist, Parsing_element pe, int direction);
extern	void		GPO_unmark_PE_list(struct parsing_element *plist, int direction);
extern	void		GPO_unmark_PE_edgelist(PE_edge elist);
extern	int		GPO_get_next_isomorph_node_overlay(struct parsing_element *plist, struct parsing_element *pgraph, int direction);
extern	void		GPO_print_number_of_isomorph_overlays(struct parsing_element *big_graph, struct parsing_element *pgraph);
extern	int		GPO_get_first_isomorph_node_overlay(struct parsing_element *plist, struct parsing_element *pgraph, int direction);
extern	int		GPO_find_isomorph_edge(PE_edge search_list, PE_edge e);
extern	int		GPO_check_graphedges_with_isoedges(struct parsing_element *graph);
extern	int		GPO_check_isoedges_with_graphedges(struct parsing_element *graph);
extern	int		GPO_check_edge_isomorphy(struct parsing_element *graph);
extern	void		GPO_unmark_embeddings(PE_embedding emblist);
extern	int		GPO_all_embeddings_marked(PE_embedding emblist);
extern	int		GPO_find_isomorph_embedding(PE_embedding emblist, PE_embedding emb);
extern	int		GPO_check_embedding_isomorphy(struct parsing_element *graph);
extern	int		GPO_check_isomorph_embeddings(Parsing_element pe1, Parsing_element pe2);
extern	int		GPO_check_graph_labels(struct parsing_element *graph);
extern	int		GPO_check_pe_isomorphism(Parsing_element pe1, Parsing_element pe2);
extern	Parsing_element	GPO_find_isomorph_pe(struct parsing_element *list, Parsing_element pe, int direction);
extern	void		GPO_add_to_isomorph_pes(struct parsing_element **list, Parsing_element pe);

#endif

