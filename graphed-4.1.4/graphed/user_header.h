/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	USER_HEADER_HEADER
#define	USER_HEADER_HEADER

#include "misc.h"
#include "graph.h"
#include "group.h"
#include "load.h"
#include "store.h"
#include "draw.h"
#include "adjust.h"
#include "find.h"
#include "type.h"


#include <xview/scrollbar.h> /* SCROLL_ENTER etc. */

typedef	enum {
	EVP_STARTUP,
	EVP_SHUTDOWN,
	EVP_CONSUME
	}
	Message_to_event_proc;

typedef	enum {
	EVP_CONSUMED,
	EVP_FINISHED,	/* Consumed + action is finished		*/
	EVP_ERROR,
	EVP_OK,		/* Initialisation / shutdown o.k.		*/
	EVP_VETO	/* Initialisation / shutdown failed		*/
	}
	Evp_result;

#include "graphed_subwindows.h"
#include "graphed_mpr.h"
#include "menu.h"
#include "user.h"
#include <ctype.h>



typedef	struct	{

	Edge_drag_info	what;
		
	union {
		struct {
			Node		source;
			Edgeline	el;
		}
			new_edge;
		struct {
			Edge		edge;
			Edgeline	el;
		}
			real_point;
		struct {
			Edge		edge;
			Edgeline	el;
		}
			imaginary_point;
	}
		which;
		
	int	x,y;

	int	do_default_action;
}
	Drag_edge_info;


typedef	struct	{

	Node_drag_info	what;
		
	Node	node;
	
	int	x,y, sx,sy;	/* Platzierung und Groesse	*/
	
	int	correction_x,	/* Korrekturen, da Klickpunkt	*/
		correction_y;	/* nicht immer der Knoten-	*/
				/* mittelpunkt ist.		*/

	int	do_default_action;
}
	Drag_node_info;



typedef	struct	{
	Group	group;	/* DIE Gruppe			*/
	int	x ,y,	/* Position von group->node	*/
		x0,y0;	/* Ursprungsposition		*/
	int	correction_x,	/* siehe Drag_node_info	*/
		correction_y;
	int	do_default_action;
}
	Drag_group_info;


typedef	struct	{
	int	x1,y1,	/* Linke obere Ecke		*/
		x2,y2;	/* Rechte untere Ecke		*/
	int	shift_is_down;
	int	do_default_action;
}
	Drag_group_box_info;


typedef	struct	{
        int     do_default_action;
}
	Click_info;

typedef	struct	{
        int     do_default_action;
}
	Double_click_info;

extern	Evp_result	default_user_event_proc (Xv_Window window, Event *event, Message_to_event_proc message);
extern	Evp_result	create_mode_event_proc  (Xv_Window window, Event *event, Message_to_event_proc message);
extern	Evp_result	edit_mode_event_proc  (Xv_Window window, Event *event, Message_to_event_proc message);

extern	Evp_result	drag_node_proc          (Xv_Window window, Event *event, Drag_node_info *info);
extern	Evp_result	drag_edge_proc          (Xv_Window window, Event *event, Drag_edge_info *info);
extern	Evp_result	drag_group_proc         (Xv_Window window, Event *event, Drag_group_info *info);
extern	Evp_result	drag_group_box_proc     (Xv_Window window, Event *event, Drag_group_box_info *info);

extern	Picklist	dispatch_picklist       (Picklist pl, int complain_if_empty);
extern	void		pick                    (Picklist object);
extern	void		unpick                  (void);

extern	void		constrain_event         (Event *event, int x, int y);

extern	char		*mini_textedit		(char *text, char *input_string);

extern	int		inside_scrollbar;
extern	int		ms_left_down;
extern	int		ms_middle_down;
extern	int		ms_right_down;
extern	int		shift_is_down;
extern	int		ctrl_is_down;
extern	int		meta_is_down;
extern	int		last_event_id;
extern	int		double_click;

extern	int		constrain_is_active;
extern	Node_or_edge	group_labelling_operation_goes_to;

extern	int	multi_click_space;
extern	int	multi_click_timeout;

extern	Picklist	pl_head;
extern	Picklist	picked_object;
extern	int		something_picked;
extern	Picklist	get_picked_object (void);



typedef	struct	{
	Node	node;
	Edge	edge;
	Graph	graph;
}
	Last_worked_object;

extern	Last_worked_object	last;
extern	Graph			current_production;


extern	int	current_event_x;
extern	int	current_event_y;
extern	int	current_event_id;


typedef struct {

	Evp_result	(*user_event_proc)();

	int	making_node;
	int	making_edge;

	int	dragging_node;	/* Flag, ob gerade ein Knoten	*/
	int	dragging_edge;	/* oder eine Kante gezogen wird	*/
	
	int	dragging_group;		/* ... oder eine Gruppe	*/
	int	dragging_group_box;	/* ... oder eine Box	*/
					/* zum Auswaehlen einer Gruppe	*/
}
	Graphed_ui_state;

Graphed_ui_state	ui_state;


#include "user_event_functions.h"

#endif
