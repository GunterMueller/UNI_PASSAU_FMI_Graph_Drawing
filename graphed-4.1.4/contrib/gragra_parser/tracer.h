/*-->@	-Dtracer								*/
	
typedef struct	trc_snode_attribute {
		int		is_graphnode;
		Parsing_element pe;
	} *TRC_snode_attribute; 

typedef struct trc_list {
		Parsing_element replaced_node;
		int		nr_expansion;
		int		next_exp_possible;
		Parsing_element expand_through;
		struct trc_list *succ;
	} *TRC_list;

/*-->@	TRC_info								*/

EXTERN	struct {
		enum {
				TRC_INACTIVE,
				TRC_ACTIVE,
				TRC_RESET,
				TRC_EXPAND,
				TRC_SELECT,
				TRC_ERROR,
				number_of_tracer_states
			}					status;
			
		enum {		GP_TOP_LEFT,
				GP_TOP,
				GP_TOP_RIGHT,
				GP_LEFT,
				GP_SAME,
				GP_RIGHT,
				GP_BOTTOM_LEFT,
				GP_BOTTOM,
				GP_BOTTOM_RIGHT
			}					trc_graph_position;
			
		enum {		NP_TOP,
				NP_LEFT,
				NP_RIGHT,
				NP_BOTTOM
			}					option_node_position;
			
		TRC_list					working_list;
		
		Sgraph						sgraph;
		Snode						selected_snode;
		PE_list 					graph;
		int						x_offset;
		int						y_offset;
		int						nr_start_element;
		int	/* bool */				next_start_possible;
		Parsing_element 				expand_pe;
		int						nr_expansion;
		Parsing_element 				expand_through;
		int	/* bool */				next_exp_possible;
		char						*message;
		int	/* bool */				test_result;
		int	/* bool */				default_expansion;
	} TRC_info;
/**/

extern	void	TRC_init(void);
extern	void	TRC_activate(void);
extern	void	TRC_reset(void);
extern	void	TRC_make_start_graph(void);
extern	void	TRC_insert_node(Parsing_element pe);
extern	void	TRC_complete_graph(void);
extern	void	TRC_replace_node(void);
extern	void	TRC_remove_node(Parsing_element trc_pe);
extern	void	TRC_re_insert_node(void);
extern	void	TRC_kill_node(Parsing_element trc_pe);
extern	void	TRC_remove_graph(void);
extern	void	TRC_deactivate(void);

extern	void	TRC_check_sgraph(Sgraph_proc_info info);
extern	void	TRC_make_sgraph_style(Sgraph_proc_info info);

extern	void	TRC_init_sgraph(void);
extern	void	TRC_make_sgraph(void);
extern	void	TRC_make_options_sgraph(void);
extern	void	TRC_remake_sgraph(void);
extern	Snode	TRC_get_snode_selection(void);
extern	void	TRC_clear_sgraph(void);
extern	void	TRC_remove_sgraph(void);

extern	void	TRC_make_snode_attrs(Snode sn);
extern	void	TRC_set_snode_attrs(Snode sn, int graph_or_option, Parsing_element pe);
extern	int	TRC_get_snode_attrs(Snode sn, TRC_snode_attribute *Attrs);
extern	void	TRC_remove_snode_attrs(Snode sn);


