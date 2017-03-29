#include "misc.h"
#include "graph.h"
#include "graphed_subwindows.h"
#include "user.h"
#include "menu.h"


/************************************************************************/
/*									*/
/*				Menubar					*/
/*									*/
/************************************************************************/


static	void	menubar_button_notify_proc (Panel_item item, Event *event)
{
	Frame	parent_frame;
	Panel	menubar;
	Canvas	canvas;
	int	i;

	menubar = (Panel)xv_get (item, XV_OWNER);
	parent_frame = (Frame)xv_get (menubar, WIN_PARENT);

	if (parent_frame != canvases[wac_buffer].frame) {

		for (i=0; i<N_BUFFERS; i++) if (buffers[i].used) {
			if (canvases[i].frame == parent_frame) {
				canvas = canvases[i].canvas;
				break;
			}
		}

		change_working_area (canvas);
	}
	menu_called_from = MENU_CALLED_FROM_MENUBAR;
	/* message ("Hello %d \n", parent_frame); */
}


Menubar	create_menubar_panel (int canvas_n)
{
	Panel_item	panel_create_menu_button,
			panel_edit_menu_button,
			panel_gragra_menu_button,
			panel_file_menu_button,
			panel_misc_menu_button,
			panel_tools_menu_button,
			panel_layout_menu_button,
			panel_goodies_menu_button,
			panel_user_menu_button,
			panel_about_menu_button;


	Menubar	menubar;

	menubar.panel = (Panel)xv_create(canvases[canvas_n].frame, PANEL,
		PANEL_LAYOUT,		PANEL_HORIZONTAL,
		XV_X,                   0,
		XV_Y,                   0,
		WIN_ERROR_MSG,		"Could not create menubar panel.\nGood bye!\n",
		XV_HEIGHT,		25,
		NULL);
	panel_about_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"GraphEd",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	about_submenu,
		NULL);
	panel_file_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"File",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	file_submenu,
		NULL);
	panel_create_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,     "Create",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	create_submenu,
		NULL);
	panel_edit_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"Edit",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	edit_submenu,
		NULL);
	panel_gragra_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,     "GraGra",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	gragra_submenu,
		NULL);
	panel_misc_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"Misc",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	misc_submenu,
		NULL);
	panel_tools_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"Tools",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	tools_submenu,
		NULL);
	panel_layout_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"Layout",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	layout_submenu,
		NULL);
	panel_goodies_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"Goodies",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	goodies_submenu,
		NULL);
	panel_user_menu_button = xv_create(menubar.panel, PANEL_BUTTON,
		PANEL_LABEL_STRING,	"User",
		PANEL_NOTIFY_PROC,	menubar_button_notify_proc,
		PANEL_ITEM_MENU,	user_submenu,
		NULL);

	return menubar;
}


