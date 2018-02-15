/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef	FIND_HEADER
#define	FIND_HEADER

#define PICK_GAP (minimum(DEFAULT_CURSOR_WIDTH, DEFAULT_CURSOR_HEIGHT) / 2)

typedef	enum {
	FIND_FIRST,
        FIND_NEXT
}
        Find_which;

extern	Node	node_finder                (int x, int y, Find_which which);
extern	Edge	edge_finder                (int x, int y, Find_which which);
extern	Node	find_node_containing_point (int x, int y);
/*extern	Edge	find_edge_near_point       ();*/

extern	int	pick_gap_x;
extern	int	pick_gap_y;


typedef	enum {
	NODE_PICKED,
	EDGE_PICKED,
	GROUP_PICKED
}
	What_is_picked;


typedef	struct {
	Node	node;
	Edge	edge;
	Group	group;
}
	Which_is_picked;


typedef	struct	picklist {
	What_is_picked	what;
	Which_is_picked	which;
	struct picklist	*pre,
	                *suc;
}
	*Picklist;

#define GRAPHED_PICKLIST_DEFINED

#define	picklist_is_single(pl) \
	(((pl) != (Picklist)NULL) && ((pl)->suc == (Picklist)NULL))
#define empty_picklist ((Picklist)NULL)


typedef	enum {
	PICK_NODE,
	PICK_EDGE,
	PICK_NODE_OR_EDGE
}
	Pick_mode;


typedef	struct {
	enum {
		NO_EDGE_POINT_PICKED,
		REAL_POINT_PICKED,
		IMAGINARY_POINT_PICKED
	} what;
	union {
		struct {
			Edgeline	el;
		}
			real_point;
		struct {
			Edgeline	el;
			int		x,y;
		}
			imaginary_point;
	} which;
}
	Picked_point_of_edgeline;

typedef	enum {
		NO_NODE_POINT_PICKED,
		UPPER_LEFT_POINT_PICKED,
		UPPER_RIGHT_POINT_PICKED,
		LOWER_LEFT_POINT_PICKED,
		LOWER_RIGHT_POINT_PICKED
	}
	Picked_point_of_node;


extern  Picklist	new_picklist (What_is_picked what, ...);
extern	Picklist	add_to_picklist (Picklist pl, What_is_picked what, ...);
extern	void		free_picklist   (Picklist pl);
extern	Picklist	remove_left_side_of_productions_from_picklist (Picklist picklist);

extern	Picked_point_of_edgeline	find_picked_point_of_edgeline (Edgeline el, int x, int y);
extern	Picked_point_of_node		find_picked_point_of_node     (Node node, int x, int y);

extern	Rect		compute_bounding_rect_of_picklist (Picklist pl);
extern	Picklist	picklist_contains_object (Picklist picklist, Picklist picked_object);
extern	void		mark_picked_object       (Picklist pl);
extern	void		unmark_picked_object     (Picklist pl);

extern	Picklist	xpicker (int x, int y, Pick_mode mode);

#endif
