/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	USER_HEADER
#define	USER_HEADER

#include <find.h>
#include <user_header.h>

/************************************************************************/
/*									*/
/*			BENUTZERINTERFACE				*/
/*									*/
/************************************************************************/

#include "dispatch_commands.h"
#include "menu.h"


extern	void		working_area_event_proc      (Xv_Window xv_window, Event *event, Notify_arg arg);
extern	void		change_working_area(Xv_window window);

extern	char		*dispatch_user_action        (User_action action, ...);

extern	int		node_is_picked  (Node node);
extern	int		edge_is_picked  (Edge edge);
extern	int		graph_is_picked (Graph graph);

extern	Node		get_picked_node   (void);
extern	Edge		get_picked_edge   (void);
extern	Group		get_picked_group  (void);
extern	Graph		get_picked_graph  (void);
extern	Graph		get_picked_or_only_existent_graph (void);

extern	Rect		compute_rect_around_selection (Picklist selection);
extern	Rect		compute_rect_around_current_selection (void);
extern	Rect		compute_rect_around_graph_of_current_selection (void);

extern	Node		get_last_node  (void);
extern	Edge		get_last_edge  (void);
extern	Graph		get_last_graph (void);

extern	void		set_last_node  (Node node);
extern	void		set_last_edge  (Edge edge);
extern	void		set_last_graph (Graph graph);

extern	void		lock_user_interface   (void);
extern	void		unlock_user_interface (void);
extern	int		test_user_interface_locked (void);

extern	void		init_extra_menu (void);
extern	void		init_user_menu (void);

extern  int      	user_interface_check_destroy_buffer(int buffer); /*eingefuegt*/
extern  void    	init_user_interface(void);                 /*eingefuegt*/
extern  int	        set_user_event_proc(Evp_result (*proc) ());                 /*eingefuegt*/
extern  int     	remove_user_event_proc(void);              /*eingefuegt*/    
extern	void		compute_subwindow_position_at_graph_of_current_selection (Xv_Window window);


typedef	enum	{
	MENU_CALLED_FROM_CANVAS,
	MENU_CALLED_FROM_MENUBAR,
	MENU_CALLED_FROM_NOWHERE
}
	Menu_called_from;

extern	Menu_called_from menu_called_from;

#endif
