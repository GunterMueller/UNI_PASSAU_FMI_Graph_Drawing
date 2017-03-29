/* FERTIG 200393 */
/********************************************************************************/
/*										*/
/*	MODUL:	parser.c							*/
/*										*/
/*	FUNKTION: Hauptmodul des Graphgrammatik-Parsers.			*/
/*										*/
/********************************************************************************/
/*-->@	-Dparser								*/
/*										*/
/*	Benutzt die Datenstrukturen des Moduls 'types'.				*/
/*	Ausserdem:								*/
/*										*/
/*m	PRS_info								*/
/*										*/
/*m	changed_what								*/
/*										*/
/********************************************************************************/

#include <sgraph_interface.h>
#include <sgragra_interface.h>

/*-->@	PRS_info						*/
EXTERN	struct {
		enum {
				PRS_INACTIVE,
				PRS_RESET,
				PRS_RUNNING,
				PRS_PAUSED,
				PRS_FINISHED,
				PRS_ERROR,
				number_of_parser_states
			}					status;
			
		int	/* bool */				grammar_loaded;
		int	/* bool */				grammar_directed;
		int	/* bool */				grammar_boundary;
		int						grammar_edgeset_size;
		int						grammar_nodelabelset_size;
		int						grammar_edgelabelset_size;
		
		int	/* bool */				graph_loaded;
		int	/* bool */				graph_directed;
		int						graph_size;
		int						graph_x_left;
		int						graph_y_top;
		int						graph_width;
		int						graph_height;
		
		int	/* bool */				link_isomorph_pes;
		
		int	/* bool */				graph_in_grammar;
		PE_set						start_elements;
	
		Parsing_element					insert_pe;
		
		Parsing_element					current_pe;
		PE_production					current_prod;
		PE_grammar					grammar;
		
		int						grammar_start_label_num;
	
		union _pe {
			PE_list	graph;			/*	pe.graph	*/
			PE_list	tab;			/*	pe.tab		*/
		} pe;
		
		/* internal variables */
		
		int	/* bool */				firstflag;
		int	/* bool */				no_pes_created;
		int	/* bool */				advance_pe;
		
		int						number_of_pes;
		int	/* changed_what */			status_changed;
		int	/* bool */				parser_stop_with_first_startnode;
		char						*message;

		/********************************************************************************/
		/*			Layout Graph Grammars: BEGIN				*/
		/********************************************************************************/

		LP_Parsing_element				pars_table;

		LP_Parsing_element				pars_tree;
		Sgraph						derivated_graph;

		/********************************************************************************/
		/*			Layout Graph Grammars: END				*/
		/********************************************************************************/
		
	} PRS_info;
/**/

/*-->@	changed_what	*/

typedef	enum {
	CHG_STATUS	= 1,
	CHG_GRAM_LOAD	= 2,
	CHG_GRAPH_LOAD	= 4,
	CHG_MESSAGE	= 8,
	CHG_PTAB_SIZE	= 16
	} changed_what;

#define PRS_changed( flag ) \
	PRS_info.status_changed |= (flag)
#define PRS_test_changed( flag ) \
	((PRS_info.status_changed & (flag)) != 0)
#define PRS_clear_changed() \
	PRS_info.status_changed = 0

/**/
extern	char	*PRS_status_string(void);
extern	void	PRS_scan_graph(Sgraph_proc_info info);
extern	void	PRS_scan_grammar(Sgragra_proc_info info);
extern	int	PRS_reset(void);
extern	int	PRS_deactivate(void);
extern	void	PRS_pause(void);
extern	void	PRS_create_next_parsing_element();
extern	void	PRS_init(void);
extern	void	PRS_step(void);

extern	int	PRS_edgespec(int nodelabel_num, int edgelabel_num, edge_direction edge_dir);
extern	void	PRS_make_lost_edges(Parsing_element pe);
extern	void	PRS_init_lost_edges(struct parsing_element *pgraph);
extern	void	PRS_init_embeddings(PE_production pgrammar);
extern	int	PRS_check_gnode_sets(Parsing_element pe);
extern	int	PRS_check_internal_edge_condition(Parsing_element pe);
extern	void	PRS_make_level(Parsing_element pe);
extern	void	PRS_kill_parsing_element(Parsing_element *PE);
extern	int	special_advance(PE_edge *edge, PE_set *right_elem);
extern	void	copy_edgelist_to_embedgechoice(PE_edge elist, PE_embedge_choice *embchoice);
extern	void	PRS_compute_embedge_choices(PE_edge edge, PE_set right_elem, PE_embedding *emb_array, Parsing_element pe, Parsing_element tnode, int depth);
extern	void	PRS_unmark_graph_and_grammar(void);
extern	void	PRS_prepare_parser(void);
extern	int	PRS_get_next_fixed_position_of_cur_pe(void);
extern	int	PRS_get_isomorph_node_overlay(void);
extern	void	PRS_create_next_parsing_elements(void);
extern	void	PRS_make_parsing_elements_of(PE_set pset, Parsing_element pe, int *count);
extern	void	DBX_fshow_lost_edges(FILE *file, Bitset set);
