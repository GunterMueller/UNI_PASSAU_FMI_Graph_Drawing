/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	GRAPHED_SUBWINDOWS
#define	GRAPHED_SUBWINDOWS

#include <xview/xview.h>
#include <xview/panel.h>

#include "graphed_svi.h"	/* Pixrects zu Server Images ! */


#define	MY_WIN_ROW_GAP       5
#define	MY_WIN_COLUMN_GAP    5
#define	MY_WIN_LEFT_MARGIN   5
#define	MY_WIN_RIGHT_MARGIN  5
#define	MY_WIN_TOP_MARGIN    10
#define	MY_WIN_BOTTOM_MARGIN 10

#define	MY_WIN_DEFAULTS \
	WIN_ROW_GAP,       MY_WIN_ROW_GAP,      \
	WIN_COLUMN_GAP,    MY_WIN_COLUMN_GAP,   \
	XV_LEFT_MARGIN,   MY_WIN_LEFT_MARGIN,  \
	XV_RIGHT_MARGIN,  MY_WIN_RIGHT_MARGIN, \
	XV_TOP_MARGIN,    MY_WIN_TOP_MARGIN,   \
	XV_BOTTOM_MARGIN, MY_WIN_BOTTOM_MARGIN
	
/************************************************************************/
/*									*/
/*	    Node- and Edge_defaults_subframe : info structure		*/
/*									*/
/************************************************************************/

typedef	struct {
	int		showing;
	Node_attributes	attr;
	Node_attributes	orig_attr;
}
	Node_defaults_subframe_info;

typedef	struct {
	int		showing;
	Edge_attributes	attr;
	Edge_attributes	orig_attr;
}
	Edge_defaults_subframe_info;


/************************************************************************/
/*									*/
/*				Base-frame				*/
/*									*/
/************************************************************************/


extern	Frame	base_frame;

extern	int	screenwidth;
extern	int	screenheight;



typedef	enum {
	FRAME_LABEL_FILENAME,
	FRAME_LABEL_EDITED,
	FRAME_LABEL_CONSTRAINED,
	FRAME_LABEL_GROUP_LABELLING_OPERATION,
	FRAME_LABEL_MODE_STRING
}
	Frame_label_attribute;
	
extern	void	set_base_frame_label (void);



/************************************************************************/
/*									*/
/*				Notice					*/
/*									*/
/************************************************************************/

#include <xview/notice.h>

typedef enum {	/*fisprompt - vielleicht besser in user_header.h definieren*/
	PROMPT_ACCEPT,
	PROMPT_REFUSE,
	PROMPT_CANCEL
}	Prompt;

/************************************************************************/
/*									*/
/*				Subframes allgemein			*/
/*									*/
/************************************************************************/


extern	void	create_node_subframe (void);
extern	void	show_node_subframe   (Node node);

extern	void	create_node_defaults_subframe (void);
extern	void	show_node_defaults_subframe   (Node_defaults_subframe_info *caller_info, void (*caller_completion_proc) ());

extern	void	create_edge_subframe (void);
extern	void	show_edge_subframe   (Edge edge);

extern	void	create_edge_defaults_subframe (void);
extern	void	show_edge_defaults_subframe   (Edge_defaults_subframe_info *caller_info, void (*caller_completion_proc) ());

extern	void	create_group_subframe (void);
extern	void	show_group_subframe   (Group group);

extern	void	show_file_selection_subframe (Load_or_store load_or_store);
extern	void	show_font_edit_subframe      (Node_or_edge node_or_edge);
extern	void	show_type_edit_subframe      (Node_or_edge node_or_edge);

extern	Node	get_currently_edited_node  (void);
extern	Edge	get_currently_edited_edge  (void);
extern	Group	get_currently_edited_group (void);
extern	Graph	get_currently_edited_graph (void);



/************************************************************************/
/*									*/
/*				Canvas					*/
/*									*/
/************************************************************************/


#include <xview/canvas.h>
#include <xview/scrollbar.h>
#include <xview/cursor.h>

/* Die folgenden Event's werden in working_area_canvas an die Event-	*/
/* prozeduren weitergeleitet.						*/

#define	MY_CANVAS_PICK_EVENTS	WIN_IN_TRANSIT_EVENTS, \
				LOC_DRAG, \
				WIN_MOUSE_BUTTONS,     \
				WIN_LEFT_KEYS,         \
				WIN_TOP_KEYS,          \
				WIN_RIGHT_KEYS

#define	MY_CANVAS_KBD_EVENTS	WIN_ASCII_EVENTS


/* Standardgroesse fuer working_area_canvas	*/

#define	DEFAULT_WORKING_AREA_CANVAS_WIDTH  4000
#define	DEFAULT_WORKING_AREA_CANVAS_HEIGHT 4000
#define	DEFAULT_WORKING_AREA_WINDOW_WIDTH  500
#define	DEFAULT_WORKING_AREA_WINDOW_HEIGHT 500

typedef	struct	toolbar {
	Panel		panel;
	Panel_item	mode_choice;
	Panel_item	nodetype_selection;
	Panel_item	edgetype_selection;
	Panel_item	nodefont_selection;
	Panel_item	edgefont_selection;
	Panel_item	nodecolor_selection;
	Panel_item	edgecolor_selection;
}
	Toolbar;

typedef	struct	menubar {
	Panel		panel;
}
	Menubar;

typedef	struct	graphed_canvas {
	Canvas		canvas;
	Frame		frame;
	Scrollbar	vertical_scrollbar,
			horizontal_scrollbar;
	Xv_Window	pixwin;
	int		gridwidth;

	int	startup_scroll_x, startup_scroll_y;
	int	canvas_seen_by_working_area_event_proc;

	Toolbar toolbar;
	Menubar menubar;
}
	Graphed_canvas;

extern	Canvas		working_area_canvas;
extern	Graphed_canvas	canvases [];

#define	working_area_canvas_pixwin (canvases[wac_buffer].pixwin)

extern	int	create_canvas                 (int n, int canvas_width, int canvas_height);
extern	void	destroy_frame_and_canvas      (int n);

extern	Menubar	create_menubar_panel	(int canvas_n);
extern	Toolbar	create_toolbar_panel	(int canvas_n);

extern	void	init_graphed_colormap         (void);

extern	void	set_wac_mouse_position        (int x, int y);
extern	int	set_working_area_size         (int width, int height);
extern	void	scroll_working_area           (int x, int y);
extern	void	scroll_working_area_relative  (int dx, int dy);
extern	void	scroll_working_area_to_middle (void);

extern	void	get_scroll_offset                 (int buffer, int *offset_x, int *offset_y);
extern	void	translate_wac_to_base_frame_space (int *x, int *y);

extern	void	set_canvas_frame_label (int n);


extern	Rectlist	global_repaint_rectlists [N_BUFFERS];
extern	Rectlist	global_erase_rectlists   [N_BUFFERS];
			/* in main.c initialisiert	*/


/************************************************************************/
/*									*/
/*			   Messge-textsw				*/
/*									*/
/************************************************************************/


#include <xview/textsw.h>

extern	Textsw	message_textsw;

extern	void	create_message_textsw (void);


/************************************************************************/
/*									*/
/*				COLOR					*/
/*									*/
/************************************************************************/


#define GRAPHED_COLORMAPSIZE 16

extern	u_char	red   [GRAPHED_COLORMAPSIZE],
		green [GRAPHED_COLORMAPSIZE],
		blue  [GRAPHED_COLORMAPSIZE];
	
extern	void	init_graphed_colormap         (void);





extern	char	*nei_strings           [];
extern	Server_image	nei_images            [];
extern	char	*nei_strings_for_cycle [];
extern	char	*nei_images_for_cycle  [];



extern	char	*nlp_strings           [];
extern	Server_image	nlp_images            [];
extern	char	*nlp_strings_for_cycle [];
extern	char	*nlp_images_for_cycle  [];


#endif
