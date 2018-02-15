/* This software is distributed under the Lesser General Public License */
/* Dummy-Header for LSD by Dirk Heider */

#ifndef _LSD

/*
if this dummy-header is included and _LSD is NOT defined,
then create an error to prevent illegal usage:
*/

DANGER! Including LSD-header without defining _LSD!

#endif /* _LSD */

#ifndef GRAPH_HEADER
#define GRAPH_HEADER

#include "font.h"

/* define pointers to GraphEd-structures as simple char pointers: */

typedef char *          Graph;
typedef char *          Node;
typedef char *          Edge;
typedef char *          Group;

#include "dispatch_commands.h"

/**************************************************************************/

typedef enum {
        NODELABEL_MIDDLE,
        NODELABEL_UPPERLEFT,
        NODELABEL_UPPERRIGHT,
        NODELABEL_LOWERLEFT,
        NODELABEL_LOWERRIGHT,

        NUMBER_OF_NODELABEL_PLACEMENTS          /* Dummy                */
        }
        Nodelabel_placement;


typedef enum {
        NO_NODE_EDGE_INTERFACE,                 /* "none"               */
        TO_BORDER_OF_BOUNDING_BOX,              /* "middle"             */
        TO_CORNER_OF_BOUNDING_BOX,              /* "corner"             */
        CLIPPED_TO_MIDDLE_OF_NODE,              /* "clipped"            */
        SPECIAL_NODE_EDGE_INTERFACE,            /* "special"            */

        NUMBER_OF_NODE_EDGE_INTERFACES          /* Dummy                */
        }
        Node_edge_interface;


typedef struct  edgeline
{
        coord           x,y;            /* Koordinaten                  */
        struct edgeline *pre,           /* vorheriges Stueck            */
                        *suc;           /* naehstes   Stueck            */
        Rect            box;            /* Rechteck, in dem die         */
                                        /* Edgeline (mit ->suc) liegt   */
}
        *Edgeline;

#define edgeline_x(el)   ((el)->x)
#define edgeline_y(el)   ((el)->y)
#define edgeline_pre(el) ((el)->pre)
#define edgeline_suc(el) ((el)->suc)
#define is_single_edgeline(el) \
        (((el) != (Edgeline)NULL) && ((el)->suc->suc == (el)))
         
extern  Edgeline        new_edgeline         (int x, int y);
extern  Edgeline        add_to_edgeline      (Edgeline el_tail, int x, int y);
extern  Edgeline        remove_from_edgeline (Edgeline el);
extern  void            set_edgeline_xy      (Edgeline el, int x, int y);
extern  void            free_edgeline        (Edgeline el_head);
extern  Edgeline        copy_edgeline        (Edgeline el_head);

#define for_edgeline(el_head,el) \
        { if (((el) = (el_head)) != (Edgeline)NULL) do {
#define end_for_edgeline(el_head,el) \
        } while (((el) = (el)->suc) != (el_head)); }



/*      Macros to set values from a Node_attributes/Edge_attributes     */
/*      structure.                                                      */
/*      POSITIONS and LABELS are NOT set !                              */

#define SET_NODE_ATTRIBUTES(attr) \
        NODE_SIZE,              (attr).width, (attr).height,    \
        NODE_FONT,              (attr).font_index,              \
        NODE_TYPE,              (attr).type_index,              \
        NODE_NEI,               (attr).node_edge_interface,     \
        NODE_NLP,               (attr).nodelabel_placement,     \
        NODE_LABEL_VISIBILITY,  (attr).label_visibility,        \
        NODE_COLOR,             (attr).color

#define SET_EDGE_ATTRIBUTES(attr) \
        EDGE_TYPE,              (attr).type_index,              \
        EDGE_FONT,              (attr).font_index,              \
        EDGE_ARROW_LENGTH,      (attr).arrow_length,            \
        EDGE_ARROW_ANGLE,       (attr).arrow_angle,             \
        EDGE_LABEL_VISIBILITY,  (attr).label_visibility,        \
        EDGE_COLOR,             (attr).color


typedef enum    {
        UEV_CONSUMED,
/*	UEV_NOT_CONSUMED, */
        UEV_VETO
}
        User_event_functions_result;


/********************************************************************/





typedef void (GraphEd_Menu_Proc)(Menu menu, Menu_item menu_item); 

typedef enum {
		NEW_EDGE,
		OLD_EDGE_REAL_POINT,
		OLD_EDGE_IMAGINARY_POINT
} Edge_drag_info;

typedef enum {
		MOVE_NODE,
		SCALE_NODE_MIDDLE,
		SCALE_NODE_UPPER_LEFT,
		SCALE_NODE_UPPER_RIGHT,
		SCALE_NODE_LOWER_LEFT,
		SCALE_NODE_LOWER_RIGHT
} Node_drag_info;

typedef enum    {

        /* Dummy for end of list        */
        SET_ATTRIBUTE_END = 0,

        /* Node attributes              */
        NODE_POSITION = 1,
        NODE_SIZE  = NODE_POSITION << 1,
        NODE_TYPE  = NODE_SIZE     << 1,
        NODE_NEI   = NODE_TYPE     << 1,
        NODE_NLP   = NODE_NEI      << 1,
        NODE_LABEL = NODE_NLP      << 1,
        NODE_FONT  = NODE_LABEL    << 1,
        NODE_LABEL_VISIBILITY = NODE_FONT << 1,
        NODE_COLOR = NODE_LABEL_VISIBILITY << 1,

        /* Edge attributes              */
        EDGE_LINE = NODE_COLOR << 1,
        EDGE_TYPE = EDGE_LINE << 1,
        EDGE_ARROW_LENGTH = EDGE_TYPE << 1,
        EDGE_ARROW_ANGLE  = EDGE_ARROW_LENGTH << 1,
        EDGE_LABEL = EDGE_ARROW_ANGLE << 1,
        EDGE_FONT  = EDGE_LABEL << 1,
        EDGE_LABEL_VISIBILITY = EDGE_FONT << 1,
        EDGE_COLOR = EDGE_LABEL_VISIBILITY << 1,

        /* Misc */
        EDGE_INSERT = EDGE_COLOR + 1,
        EDGE_DELETE = EDGE_INSERT + 1,

        MOVE   = EDGE_DELETE + 1,
        RESIZE = MOVE + 1,

        /* Specialities                 */
        ONLY_SET   = RESIZE + 1,
        RESTORE_IT = ONLY_SET + 1,

        /* Sgraph goodies               */
        NODE_WIDTH  = RESTORE_IT + 1,
        NODE_HEIGHT = NODE_WIDTH + 1,
        NODE_X      = NODE_HEIGHT + 1,
        NODE_Y      = NODE_X + 1
}
        Set_attribute;


typedef	enum	{
	UEV_CLICK,
	UEV_DOUBLE_CLICK,
	UEV_DRAG_NODE,
	UEV_DRAG_EDGE,
	UEV_DRAG_GROUP,
	UEV_DRAG_BOX,
	NUMBER_OF_UEV_FUNCTIONS
}
	User_event_functions_type;	

typedef	enum	{
		UEV_START,
		UEV_DRAG,
		UEV_INTERMEDIATE_STOP,
		UEV_FINISH,
		UEV_ERROR
}
	User_event_functions_state;	

extern void graphed_main(int argc, char **argv);
extern  void    message       (char *format, ...);
extern  void    warning       (char *format, ...);
extern  void    error         (char *format, ...);
extern  void    fatal_error   (char *format, ...);
extern  void    add_to_file_menue (char *string, void (*proc)());
extern  void    add_to_user_menue (char *string, void (*proc)());
extern  void    add_to_layout_menue (char *string, void (*proc)());
extern  void    add_to_tools_menue (char *string, void (*proc)());
extern  void    add_to_goodies_menue (char *string, void (*proc)());
extern  void    add_to_extra_menue (char *string, void (*proc)());

#endif /* GRAPH_HEADER */

