/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

#ifndef SGRAPH_INTERFACE_H
#define SGRAPH_INTERFACE_H

#include <sgraph/sgraph.h>
#include <sgraph/graphed_structures.h>
#ifndef EXTERN_LINK_MODULE
#include  <graphed/user_event_functions.h>
#endif

extern	void	menu_call_sgraph_proc (Menu menu, Menu_item menu_item, void (*proc) ()); /* Menu, Menu_item, (void*)() */
extern	void	call_sgraph_proc( void* , char *user_args);
extern	void	call_sgraphs_proc (Slist (*proc) (), char *user_args);


typedef	enum	{
    SGRAPH_SELECTED_NONE,
    SGRAPH_SELECTED_NOTHING,
    SGRAPH_SELECTED_SNODE,
    SGRAPH_SELECTED_SEDGE,
    SGRAPH_SELECTED_GROUP,
	
    SGRAPH_SELECTED_SAME
}
Sgraph_selected;


typedef	union {
    Snode	snode;
    Sedge	sedge;
    Slist	group;
}
Sgraph_selection;


typedef struct sgraph_proc_info {
	
    Sgraph_selected  selected;
    Sgraph           sgraph;
    Sgraph_selection selection;
    int              buffer;
	
    Sgraph_selected  new_selected;
    Sgraph           new_sgraph;
    Sgraph_selection new_selection;
    int              new_buffer;
	
    int	repaint,
	recompute,
	no_changes,
	no_structure_changes,
	save_selection;
	
    int	recenter;
}
*Sgraph_proc_info;

#define	NEW_SEDGE	NEW_EDGE
#define	OLD_SEDGE_REAL_POINT	OLD_EDGE_REAL_POINT
#define	OLD_SEDGE_IMAGINARY_POINT	OLD_EDGE_IMAGINARY_POINT
#define	MOVE_SNODE	MOVE_NODE
#define	SCALE_SNODE_MIDDLE	SCALE_NODE_MIDDLE
#define	SCALE_SNODE_UPPER_LEFT	SCALE_NODE_UPPER_LEFT
#define	SCALE_SNODE_UPPER_RIGHT	SCALE_NODE_UPPER_RIGHT
#define	SCALE_SNODE_LOWER_LEFT	SCALE_NODE_LOWER_LEFT
#define	SCALE_SNODE_LOWER_RIGHT	SCALE_NODE_LOWER_RIGHT

/************** Extended Application Interface **************************/


typedef	struct	{

    Edge_drag_info	what;
		
    union {
	struct {
	    Snode		source;
	    Edgeline	el;
	}
	new_edge;
	struct {
	    Sedge		edge;
	    Edgeline	el;
	}
	real_point;
	struct {
	    Sedge		edge;
	    Edgeline	el;
	}
	imaginary_point;
    }
    which;
		
    int	x,y;
}
Sgraph_drag_edge_info;


typedef	struct	{
    Node_drag_info what;
		
    Snode	node;
	
    int	x,y, sx,sy;	/* Platzierung und Groesse	*/
	
    int	correction_x,	/* Korrekturen, da Klickpunkt	*/
	correction_y;	/* nicht immer der Knoten-	*/
    /* mittelpunkt ist.		*/
}
Sgraph_drag_node_info;



typedef	struct	{
    Slist	group;	/* DIE Gruppe			*/
    int	x ,y,	/* Position von group->node	*/
	x0,y0;	/* Ursprungsposition		*/
    int	correction_x,	/* siehe Drag_node_info	*/
	correction_y;
}
Sgraph_drag_group_info;


typedef	struct	{
    int	x1,y1,	/* Linke obere Ecke		*/
	x2,y2;	/* Rechte untere Ecke		*/
    int	shift_is_down;
}
Sgraph_drag_box_info;


typedef	struct	{
    int              x,y;		/* Rechte untere Ecke	*/
    Sgraph_selected  what;
    Sgraph_selection which;
}
Sgraph_click_info;


#define Sgraph_uev_state User_event_functions_state
#define SGRAPH_UEV_START UEV_START
#define SGRAPH_UEV_DRAG UEV_DRAG
#define SGRAPH_UEV_INTERMEDIATE_STOP UEV_INTERMEDIATE_STOP
#define SGRAPH_UEV_FINISH UEV_FINISH
#define SGRAPH_UEV_ERROR UEV_ERROR

#define Sgraph_uev_type User_event_functions_type
#define SGRAPH_UEV_CLICK UEV_CLICK
#define SGRAPH_UEV_DOUBLE_CLICK UEV_DOUBLE_CLICK
#define SGRAPH_UEV_DRAG_NODE UEV_DRAG_NODE
#define SGRAPH_UEV_DRAG_EDGE UEV_DRAG_EDGE
#define SGRAPH_UEV_DRAG_GROUP UEV_DRAG_GROUP
#define SGRAPH_UEV_DRAG_BOX UEV_DRAG_BOX
#define NUMBER_OF_SGRAPH_UEV_FUNCTIONS NUMBER_OF_UEV_FUNCTIONS

typedef	struct	sgraph_event_proc_info {
	
    Sgraph_uev_state	state;
    Sgraph_uev_type		type;

    union {
	Sgraph_click_info	click;
	Sgraph_drag_node_info	node;
	Sgraph_drag_edge_info	edge;
	Sgraph_drag_group_info	group;
	Sgraph_drag_box_info	box;
    }
    details;
	
    int	do_default_action;
}
*Sgraph_event_proc_info;


typedef	struct	sgraph_command_proc_info {
	
    User_action	action;
	
    union {
	Sgraph	sgraph;
	Snode	snode;
	Sedge	sedge;
    }
    data;
	
}
*Sgraph_command_proc_info;



/****************** Multiple Graphs Interface ****************************/


typedef	enum	{
    SIMPLE_SGRAPH_PROC,
    SGRAPH_EVENT_PROC
}
Sgraph_proc_type;
	


typedef	struct	call_sgraph_proc_info {
	
    int	x_center_before, y_center_before;
    int	n_nodes;

    struct	sgraph_proc_info 	sgraph_proc_info;
    struct	sgraph_event_proc_info	sgraph_event_proc_info;
	
    int	structure_changed, anything_changed;
}
*Call_sgraph_proc_info;


typedef struct sgraph_selection_info {

    Sgraph_selected		selected;
    Sgraph_selection	selection;
    int			save_selection;

}
*Sgraph_selection_info;

extern Snode sedge_real_source(Sedge sedge);
extern Snode sedge_real_target(Sedge sedge);

#endif
