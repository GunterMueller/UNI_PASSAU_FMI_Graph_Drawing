/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				state.h					*/
/*									*/
/************************************************************************/

#ifndef __GRAPHED_STATE_H__
#define __GRAPHED_STATE_H__


/************************************************************************/
/*									*/
/*				Graph_state				*/
/*									*/
/*		Aktuelle Defaultwerte der Graphenattribute		*/
/*									*/
/************************************************************************/


typedef	struct	{
	Node_attributes		*node;
	Edge_attributes		*edge;
	
	int		max_edgelabel_width;
	int		max_edgelabel_height;
	int		directedness;
	
#ifdef LP_LAYOUT
	Lp_graph_state	lp_graph_state;
#endif

	Gragra_type	gragra_type;
	char		*gragra_terminals;
	char		*gragra_nonterminals;
	int		gragra_always_match_empty;
	
	int		always_pretty_print_productions;
	char		*global_embedding_name;
	unsigned	embed_match_attributes;

	char		*disable_all_modifying_commands;
	char		*disable_all_structure_modifying_commands;
	char		*disable_all_commands;
}
	Graph_state;


extern	Graph_state	graph_state;


/************************************************************************/
/*									*/
/*			NODE- AND EDGESTYLES				*/
/*									*/
/************************************************************************/

#define	NORMAL_NODE_STYLE	0
#define	LEFT_SIDE_NODE_STYLE	1
#define	EMBED_NODE_STYLE	2
#define	NUMBER_OF_NODE_STYLES	3
	
#define	NORMAL_EDGE_STYLE	0
#define	EMBED_EDGE_STYLE	1
#define	NUMBER_OF_EDGE_STYLES	2


extern	void		set_node_style (int i, Node_attributes attr);
extern	void		set_edge_style (int i, Edge_attributes attr);

extern	Node_attributes	get_node_style (int i);
extern	Edge_attributes	get_edge_style (int i);


/************************************************************************/
/*			GRAPHED_STATE					*/
/************************************************************************/


typedef enum	{
	GRAPHED_MODE_CREATE_MODE = 0, /* Values needed in toolbar.c */
	GRAPHED_MODE_EDIT_MODE = 1
}
	Graphed_mode;


typedef	enum	{
	GRAPHED_FULL_OPERATIONS_AVAILABLE,
	GRAPHED_VIEW_ONLY
}
	Graphed_view_only_mode;


typedef	enum	{
	GRAPHED_NO_SNOOP, 
	GRAPHED_SNOOPING
}
	Graphed_snoop;


typedef	struct {
	Graphed_snoop	snooping;
	char		*filename;
}
	Graphed_snoop_mode;


typedef struct {
	int	startup;
	int	shutdown;
	int	loading;
	
	int	default_working_area_canvas_width,
		default_working_area_canvas_height,
		default_working_area_window_width,
		default_working_area_window_height;
	
	int	colorscreen;

	Graphed_mode	mode;
	int		constrain_is_active;
	Node_or_edge	group_labelling_operation_goes_to;

	Graphed_view_only_mode	view_only;	/* View only, no modify	*/
	Graphed_snoop_mode	snooping;	/* snoop mode		*/
	char			*layouter;	/* default layouter	*/

	int			show_base_frame;
}
	Graphed_state;


extern	Graphed_state	graphed_state;

extern	void	set_current_node_edge_interface  (Node_edge_interface node_edge_interface);
extern	void	set_current_nodelabel_placement  (Nodelabel_placement nodelabel_placement);
extern	void	set_current_nodesize             (int width, int height);
extern	void	set_current_nodetype             (int index);
extern	void	set_current_edgetype             (int index);
extern	void	set_current_nodefont             (int index);
extern	void	set_current_edgefont             (int index);
extern	void	set_current_edgelabelsize        (int width, int height);
extern	void	set_current_nodelabel_visibility (int visible);
extern	void	set_current_edgelabel_visibility (int visible);
extern	void	set_current_arrowlength          (int length);
extern	void	set_current_arrowangle           (float angle);
extern	void	set_current_nodecolor            (int color);
extern	void	set_current_edgecolor            (int color);
extern	void	set_current_directedness         (int directed);

extern	void	set_current_gragra_type          (Gragra_type type);
extern	void	set_current_gragra_terminals     (char *terminals);
extern	void	set_current_gragra_nonterminals  (char *nonterminals);
extern	void	set_always_pretty_print_productions (int boolean);
extern	void	set_global_embedding_name        (char *string);
extern	void	set_embed_match_attributes       (unsigned int set);
extern	void	set_gragra_always_match_empty    (int boolean);

extern	void	set_disable_all_commands(char *who);
extern	void	set_disable_all_modifying_commands(char *who);
extern	void	set_disable_all_structure_modifying_commands(char *who);

extern	void	set_graphed_mode(Graphed_mode mode);
extern	void	set_graphed_constrained(int constrained);
extern	void	set_graphed_group_labelling_operation(Node_or_edge goes_to);

extern	void	set_graphed_view_only_mode (Graphed_view_only_mode mode);
extern	void	set_graphed_snoop_mode (Graphed_snoop_mode mode);
extern	void	set_graphed_default_layouter (char *name);
extern	void	set_show_base_frame (int show);


extern	Node_edge_interface	get_current_node_edge_interface (void);
extern	Nodelabel_placement	get_current_nodelabel_placement (void);
extern	float			get_current_arrow_angle (void);
extern	int			get_current_arrow_length (void);
extern	int			get_current_node_width (void);
extern	int			get_current_node_height (void);
extern	int			get_current_nodefont_index (void);
extern	int			get_current_edgefont_index (void);
extern	int			get_current_nodetype_index (void);
extern	int			get_current_edgetype_index (void);
extern	int			get_current_nodelabel_visibility (void);
extern	int			get_current_edgelabel_visibility (void);
extern	int			get_current_edgelabel_width (void);
extern	int			get_current_edgelabel_height (void);
extern	int			get_current_nodecolor (void);
extern	int			get_current_edgecolor (void);
extern	int			get_current_directedness (void);

extern	Gragra_type		get_current_gragra_type (void);
extern	char			*get_current_gragra_terminals (void);
extern	char			*get_current_gragra_nonterminals (void);
extern	int			get_always_pretty_print_produtions (void);
extern	char			*get_global_embedding_name (void);
extern	unsigned		get_embed_match_attributes (void);
extern	int			get_gragra_always_match_empty (void);

extern	char 	*get_disable_all_commands(void);
extern	char 	*get_disable_all_modifying_commands(void);
extern	char 	*get_disable_all_structure_modifying_commands(void);

extern	Graphed_mode	get_graphed_mode(void);
extern	int		get_graphed_constrained(void);
extern	Node_or_edge	get_graphed_group_labelling_operation(void);

extern	Graphed_view_only_mode	get_graphed_view_only_mode (void);
extern	Graphed_snoop		get_graphed_snooping (void);
extern	Graphed_snoop_mode	get_graphed_snoop_mode (void);
extern	char			*get_graphed_default_layouter (void);
extern	int			get_show_base_frame (int show);

/* Compatibility Macros */

#define	current_node_edge_interface  (get_current_node_edge_interface())
#define	current_nodelabel_placement  (get_current_nodelabel_placement())
#define	current_node_width           (get_current_node_width())
#define	current_node_height          (get_current_node_height())
#define	current_nodetype_index       (get_current_nodetype_index())
#define	current_edgetype_index       (get_current_edgetype_index())
#define	current_nodefont_index       (get_current_nodefont_index())
#define	current_edgefont_index       (get_current_edgefont_index())
#define	current_edgelabel_width      (get_current_edgelabel_width())
#define	current_edgelabel_height     (get_current_edgelabel_height())
#define	current_nodelabel_visibility (get_current_nodelabel_visibility())
#define	current_edgelabel_visibility (get_current_edgelabel_visibility())
#define current_arrow_length         (get_current_arrow_length())
#define current_arrow_angle          (get_current_arrow_angle())
#define	current_nodecolor            (get_current_nodecolor())
#define	current_edgecolor            (get_current_edgecolor())
#define current_directedness         (get_current_directedness())
#define current_gragra_type          (get_current_gragra_type())
#define current_gragra_terminals     (get_current_gragra_terminals())
#define current_gragra_nonterminals  (get_current_gragra_nonterminals())

extern void    init_graphed_state (void);
extern void    init_graph_state   (void);

#endif
