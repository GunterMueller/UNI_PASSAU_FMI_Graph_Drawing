/*-->@	TRC.							*/

EXTERN	struct {
	Frame		baseframe;
	Panel			main_panel;
	Panel_item			reset_button;
	Panel_item			reset_previous_button;
	Panel_item			reset_next_button;
	Panel_item			expand_default_choice;
	Panel_item			select_button;
	Panel_item			expand_button;
	Panel_item			select_and_expand_button;
	Panel_item			step_back_button;
	Panel_item			message_text_item1;
	Panel_item			message_text_item2;
	
	Frame		opt_subframe;
	Panel			opt_sf_panel;
	Panel_item			graph_placement_choice;
	Panel_item			opnode_placement_choice;
	
	} TRC;
/**/

extern	void	TRC_nf_quit(void);
extern	void	TRC_create_tracer_subframe(Frame baseframe);
