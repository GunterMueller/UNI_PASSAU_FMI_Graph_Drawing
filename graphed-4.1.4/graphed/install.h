/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	INSTALL_HEADER
#define	INSTALL_HEADER

extern	void	install_fontlist_in_node_subframe      (char **list);
extern	void	install_nodetypelist_in_node_subframe  (char **list);
extern	void	install_fontlist_in_edge_subframe      (char **list);
extern	void	install_edgetypelist_in_edge_subframe  (char **list);

extern	void	install_fontlist_in_nodefont_selection (char **list);
extern	void	install_fontlist_in_edgefont_selection (char **list);
extern	void	install_current_nodefont_in_nodefont_selection (void);
extern	void	install_current_edgefont_in_edgefont_selection (void);

extern	void	install_nodetypelist_in_nodetype_selection (char **list);
extern	void	install_edgetypelist_in_edgetype_selection (char **list);
extern	void	install_current_nodetype_in_nodetype_selection (void);
extern	void	install_current_edgetype_in_edgetype_selection (void);

extern	void	install_node_edge_interface_in_menu (Node_edge_interface nei);
extern	void	install_nodelabel_placement_in_menu (Nodelabel_placement nlp);
extern	void	install_nodesize_in_menu            (int x, int y);

extern	void	update_nodelabel_visibility_in_node_subframe (void);

#endif
