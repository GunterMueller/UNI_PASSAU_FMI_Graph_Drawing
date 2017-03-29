struct 
{
	Frame				mainframe;

		Panel			grammar_attributes;
			Panel_item	text_101;
			Panel_item	box_101;	/* BNLC_GRAMMAR */
			Panel_item	box_102;	/* DIFF_EMBEDDINGS */
			Panel_item	box_103;	/* RECTANGULAR_EDGELINES */
			Panel_item	box_104;	/* GRID_LAYOUT */
			Panel_item	box_105;	/* UNIT_LAYOUT */
			Panel_item	box_106;	/* INTERSECTING_LINE */
			Panel_item	box_107;	/* LINE_OUT_OF_PROD */
			Panel_item	box_108;	/* DEGENERATED_EDGE */
			Panel_item	box_109;	/* BORDER_GAP_FULLFILLED */
			Panel_item	box_110;	/* NODE_DISTANCE */
			Panel_item	box_111;	/* EDGE_OVERLAPPING */
			Panel_item	box_112;	/* NON_TERM_UNIT */
			Panel_item	box_113;	/*  */
			Panel_item	box_114;	/*  */


		Panel			cur_settings;
			Panel_item	text_201;
			Panel_item	text_202;
			Panel_item	text_203;
			Panel_item	text_204;
			Panel_item	text_207;
			Panel_item	choice_201;
			Panel_item	choice_202;

		Panel			algorithm_choice;
			Panel_item	text_301;
			Panel_item	text_302;
			Panel_item	text_303;
			Panel_item	text_304;
			Panel_item	text_305;
			Panel_item	text_306;
			Panel_item	text_307;
			Panel_item	text_308;
			Panel_item	choice_301;
			Panel_item	choice_302;
			Panel_item	choice_303;
			Panel_item	choice_304;
			Panel_item	choice_305;
			Panel_item	choice_306;
			int		old_value_301;
			int		old_value_302;
			int		old_value_303;
			int		old_value_304;
			int		old_value_305;
			int		old_value_306;

		Panel			immediate_execute;
			Panel_item	alg_401;	/* Alg. execute */
			Panel_item	alg_402;	/* reduce */
			Panel_item	alg_403;	/* delete */
			Panel_item	alg_404;	/* open */
			Panel_item	alg_405;	/* scale_down */
			Panel_item	alg_406;	/* scale_up */

	/*****************************************************************
	aktuelle Einstellungen
	*****************************************************************/

	int		cur_algorithm;
	char*		algorithm_label;

	int		what_to_do_with_derivated_node;

	int		min_nodes;

	int		frame_is_created;
}	LP_WIN;


#define	DONT_SHOW	0
#define	SHOW		1
#define	CONNECT		2


extern	void		lp_create_baseframe		(Menu menu, Menu_item menu_item);
extern	void		lp_reset_all_grammar_panels	(void);
