typedef	struct	gragra_apply_form {
	Node		apply_on;
	struct	graph	*production; /* data type Graph not available at time of declaration */
}
	*Gragra_apply_form;

typedef	struct	devtree_node {
	Node	production_left_side;
}
	Devtree_node;

typedef	struct	devtree_edge {
	Node	node_in_right_side;
}
	Devtree_edge;


/************************************************************************/
/*									*/
/*			Gragra textual history form			*/
/*									*/
/************************************************************************/

typedef struct	gragra_textual_history_form {
	char	*node_of_right_side;
	char	*production;
}
	*Gragra_textual_history_form;

extern	Gragra_textual_history_form	ggthf_create (void);
extern	void				ggthf_delete (Gragra_textual_history_form ggthf);

extern	void	ggthf_set_right_side (Gragra_textual_history_form ggthf, char *label);
extern	void	ggthf_set_production (Gragra_textual_history_form ggthf, char *label);

extern	char	*ggthf_get_right_side (Gragra_textual_history_form ggthf);
extern	char	*ggthf_get_production (Gragra_textual_history_form ggthf);

#define for_ggthf_list(ggthf_list,ggthf) \
	generic_for_slist ((ggthf_list), (ggthf), Gragra_textual_history_form)
#define end_for_ggthf_list(ggthf_list,ggthf) \
	end_generic_for_slist ((ggthf_list), (ggthf), Gragra_textual_history_form)


/************************************************************************/
/*									*/
/*			Gragra textual apply form			*/
/*									*/
/************************************************************************/

typedef struct	gragra_textual_apply_form {
  	int	number;
	char	*production;
	char	*node;
	Slist	node_history;	/* Slist of Gragra_textual_history_form	*/
}
	*Gragra_textual_apply_form;

extern	Gragra_textual_apply_form	ggtaf_create (void);
extern	void				ggtaf_delete (Gragra_textual_apply_form ggtaf);

extern	void	ggtaf_set_production (Gragra_textual_apply_form ggtaf, char *production);
extern	void	ggtaf_set_node (Gragra_textual_apply_form ggtaf, char *node);
extern	void	ggtaf_add_node_history (Gragra_textual_apply_form ggtaf, char *right_side, char *production);

extern	char	*ggtaf_get_production (Gragra_textual_apply_form ggtaf);
extern	char	*ggtaf_get_node (Gragra_textual_apply_form ggtaf);
extern	Slist	ggtaf_get_node_history (Gragra_textual_apply_form ggtaf);


#define for_ggtaf_list(ggtaf_list,ggtaf) \
	generic_for_slist ((ggtaf_list), (ggtaf), Gragra_textual_apply_form)
#define end_for_ggtaf_list(ggtaf_list,ggtaf) \
	end_generic_for_slist ((ggtaf_list), (ggtaf), Gragra_textual_apply_form)

#define for_ggtaf_node_history(ggtaf,ggthf) \
	for_ggthf_list ((ggtaf->node_history),ggthf)
#define end_for_ggtaf_node_history(ggtaf,ggthf) \
	end_for_ggthf_list ((ggtaf->node_history),ggthf)


/************************************************************************/
/*									*/
/*			Derivation sequence				*/
/*									*/
/************************************************************************/

typedef	struct	derivation_sequence {
	Slist	files;		/* Slist of char *			*/
	char	*startnode;	/* Startnode of the production		*/
	Slist	apply_forms;	/* Slist of Gragra_textual_apply_form	*/
}
	*Derivation_sequence;

extern	Derivation_sequence	ds_create (void);
extern	void			ds_delete (Derivation_sequence sequence);

extern	void	ds_add_file       (Derivation_sequence sequence, char *filename);
extern	void	ds_set_startnode  (Derivation_sequence sequence, char *label);
extern	void	ds_add_apply_form (Derivation_sequence sequence, Gragra_textual_apply_form apply_form);

extern	Slist	ds_get_files       (Derivation_sequence sequence);
extern	char	*ds_get_startnode  (Derivation_sequence sequence);
extern	Slist	ds_get_apply_forms (Derivation_sequence sequence);

extern	void	add_derivation_tree (Graph production, Node replaced_node, Group right_side);
extern	void	apply_derivation_sequence (Graph graph, Derivation_sequence sequence);

#define for_ds_files(sequence,file)	\
	generic_for_slist ((sequence->files), (file), char*)
#define end_for_ds_files(sequence,file)	\
	end_generic_for_slist ((sequence->files), (file), char*)

#define for_ds_apply_forms(sequence,ggtaf) \
	for_ggtaf_list ((sequence->apply_forms), ggtaf)
#define end_for_ds_apply_forms(sequence,ggtaf) \
	end_for_ggtaf_list ((sequence->apply_forms), ggtaf)

extern	void	menu_display_derivation_graph(Menu menu, Menu_item menu_item);
extern	void	menu_store_derivation_sequence(Menu menu, Menu_item menu_item);
extern	void	menu_apply_production(Menu menu, Menu_item menu_item);
