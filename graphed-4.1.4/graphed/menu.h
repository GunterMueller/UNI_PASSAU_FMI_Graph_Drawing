/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	MENU_HEADER
#define	MENU_HEADER

#include <xview/xview.h>
#include <dispatch_commands.h>

#include "graph.h"

typedef void (GraphEd_Menu_Proc)(Menu menu, Menu_item menu_item); 

extern	Menu	main_menu;
extern	void	create_working_area_menu (void);
extern	void	set_menu_selection (User_action action);
extern	void	activate_menu_item (User_action action);
extern	void	inactivate_menu_item (User_action action);
extern	void	set_menu_string (User_action action, char *string);

extern	Menu	graphed_create_pin_menu (char *name);
extern	Menu	graphed_create_submenu (void);
extern	void	add_graphed_string_entry_to_menu (Menu menu, char *string, User_action client_data);
extern	void	add_graphed_image_entry_to_menu (Menu menu, Server_image image, User_action client_data);
extern	void	add_string_entry_to_menu (Menu menu, char *string, User_action client_data, GraphEd_Menu_Proc proc);
extern	void	add_image_entry_to_menu (Menu menu, Server_image image, User_action client_data, GraphEd_Menu_Proc proc);
extern	void	add_entry_to_menu (Menu menu, char *string, GraphEd_Menu_Proc proc);
extern	void	add_menu_to_menu (Menu menu, char *string, Menu add_menu);

extern	void	add_to_tools_menu (char *string, GraphEd_Menu_Proc proc);
extern	void	add_to_layout_menu (char *string, GraphEd_Menu_Proc proc);
extern	void	add_to_goodies_menu (char *string, GraphEd_Menu_Proc proc);
extern  void	add_to_user_menu (char *string, GraphEd_Menu_Proc proc);
extern  void	add_to_extra_menu (char *string, GraphEd_Menu_Proc proc);

extern	void	add_menu_to_tools_menu (char *string, Menu menu);
extern	void	add_menu_to_layout_menu (char *string, Menu menu);
extern	void	add_menu_to_goodies_menu (char *string, Menu menu);
extern  void	add_menu_to_user_menu (char *string, Menu menu);

extern	void	rebuild_gragra_productions_submenu (void);


extern  void	install_gragra_type_in_menu         (Gragra_type gragra_type);  /*eingefuegt*/
extern  void	install_directedness_in_menu        (int directed);  /*eingefuegt*/
extern  void	install_group_labelling_operation_in_menu  (Node_or_edge goes_to); /*eingefuegt*/
extern  void	install_constrained_in_menu                (int constrain); /*eingefuegt*/ 
extern  void	install_grid_in_menu                       (int width); /*eingefuegt*/
extern  void	install_edgelabel_visibility_in_menu       (int visible); /*eingefuegt*/
extern  void	install_nodelabel_visibility_in_menu       (int visible); /*eingefuegt*/
extern  void	install_arrowangle_in_menu                 (float angle); /*eingefuegt*/
extern  void	install_arrowlength_in_menu                (int length); /*eingefuegt*/
extern  void	install_edgelabelsize_in_menu              (int width, int height); /*eingefuegt*/
extern  void	install_nodesize_in_menu                   (int x, int y); /*eingefuegt*/
extern  void	install_nodelabel_placement_in_menu        (Nodelabel_placement nlp); /*eingefuegt*/
extern  void	install_node_edge_interface_in_menu        (Node_edge_interface nei); /*eingefuegt*/

extern	Menu	file_submenu;
extern	Menu		file_export_submenu;
extern	Menu	create_submenu;
extern	Menu	edit_submenu;
extern	Menu	gragra_submenu;
extern	Menu	misc_submenu;
extern	Menu	tools_submenu;
extern	Menu	layout_submenu;
extern	Menu	goodies_submenu;
extern	Menu	user_submenu;
extern	Menu	about_submenu;

#endif
