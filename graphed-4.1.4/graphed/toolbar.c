/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/

#include "user_header.h"

#include "install.h"
#include <sgraph/std.h>
#include <sgraph/slist.h>

/* siehe graphed_mpr.c : */
#define my_mpr_static(name,w,h,depth,image)

/************************************************************************/
/*									*/
/*			GLOBALE FUNKTIONEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	create_toolbar_panel ()					*/
/*									*/
/*	void	install_nodetypelist_in_nodetype_selection (list)	*/
/*	void	install_edgetypelist_in_edgetype_selection (list)	*/
/*	void	install_current_nodetype_in_nodetype_selection ()	*/
/*	void	install_current_edgetype_in_edgetype_selection ()	*/
/*									*/
/*	void	install_fontlist_in_nodefont_selection (list)		*/
/*	void	install_fontlist_in_edgefont_selection (list)		*/
/*	void	install_current_nodefont_in_nodefont_selection ()	*/
/*	void	install_current_edgefont_in_edgefont_selection ()	*/
/*									*/
/************************************************************************/


/************************************************************************/
/*									*/
/*			LOKALE FUNKTIONEN				*/
/*									*/
/************************************************************************/


static	void	notify_nodetype_selection(Panel_item item, int value, Event *event),
		notify_edgetype_selection(Panel_item item, int value, Event *event),
		notify_nodefont_selection(Panel_item item, int value, Event *event),
		notify_edgefont_selection(Panel_item item, int value, Event *event) /* ,
		notify_nodecolor_selection(Panel_item item, int value, Event *event),
		notify_edgecolor_selection(Panel_item item, int value, Event *event) */;


/************************************************************************/
/*									*/
/*				Toolbar					*/
/*									*/
/************************************************************************/


#include <images/create_mode_icon.xbm>
#include <images/edit_mode_icon.xbm>
Server_image	create_mode_icon_svi;
Server_image	edit_mode_icon_svi;

static	void	toolbar_mode_choice_notify_proc (Panel_item item, int value, Event *event);


Toolbar	create_toolbar_panel (int canvas_n)
{
	Toolbar		toolbar;
	Graphed_canvas	canvas;

	canvas = canvases[canvas_n];

	toolbar.panel = xv_create (canvas.frame, PANEL,
		WIN_BELOW,    canvases[canvas_n].menubar.panel,
		XV_X,         0,
		PANEL_LAYOUT, PANEL_VERTICAL,
		NULL);

	create_mode_icon_svi = (Server_image) xv_create (XV_NULL, SERVER_IMAGE,
		XV_WIDTH,  create_mode_icon_width,
		XV_HEIGHT, create_mode_icon_height,
		SERVER_IMAGE_X_BITS, create_mode_icon_bits,
		NULL),

	edit_mode_icon_svi = (Server_image) xv_create (XV_NULL, SERVER_IMAGE,
		XV_WIDTH,  edit_mode_icon_width,
		XV_HEIGHT, edit_mode_icon_height,
		SERVER_IMAGE_X_BITS, edit_mode_icon_bits,
		NULL),

	toolbar.mode_choice = xv_create (toolbar.panel, PANEL_CHOICE,
		XV_X, 1,
		XV_Y, 5,
		PANEL_CHOICE_IMAGES,	create_mode_icon_svi,
					edit_mode_icon_svi,
					NULL,
		PANEL_NOTIFY_PROC,	toolbar_mode_choice_notify_proc,
		NULL);

	toolbar.nodetype_selection = xv_create(
		toolbar.panel,          PANEL_CHOICE_STACK,
		/* There seems to be some error in XView placement */
		XV_Y,			xv_get (toolbar.mode_choice, XV_X) +
					xv_get (toolbar.mode_choice, XV_HEIGHT) +
					10,
		PANEL_CHOICE_IMAGES,	create_mode_icon_svi, NULL,
		PANEL_FEEDBACK,		PANEL_NONE,
		PANEL_NOTIFY_PROC,	notify_nodetype_selection,
		NULL);
		
/* Commented MH Conversion	
	
	toolbar.nodecolor_selection = create_graphed_color_selection_item (toolbar.panel,
		notify_nodecolor_selection);
			
	toolbar.edgecolor_selection = create_graphed_color_selection_item (toolbar.panel,
		notify_edgecolor_selection);
	
*/
	
	toolbar.nodefont_selection = xv_create(
		toolbar.panel,          PANEL_CHOICE_STACK,
		PANEL_CHOICE_STRINGS,	"42", NULL,
		PANEL_NOTIFY_PROC,	notify_nodefont_selection,
		NULL);
	
	toolbar.edgetype_selection = xv_create(
		toolbar.panel,          PANEL_CHOICE_STACK,
		PANEL_CHOICE_IMAGES,	create_mode_icon_svi, NULL,
		PANEL_FEEDBACK,		PANEL_NONE,
		PANEL_NOTIFY_PROC,	notify_edgetype_selection,
		NULL);

	toolbar.edgefont_selection = xv_create(
		toolbar.panel,          PANEL_CHOICE_STACK,
		PANEL_CHOICE_STRINGS,	"42", NULL,
		PANEL_NOTIFY_PROC,	notify_edgefont_selection,
		NULL);


	xv_set (toolbar.panel,
		XV_WIDTH, xv_get (toolbar.mode_choice, XV_WIDTH),
		NULL);


	if (!graphed_state.startup) {
		xv_set(toolbar.edgetype_selection,
			ATTR_LIST, get_edgetypelist_for_cycle(),
			PANEL_VALUE, current_edgetype_index,
			NULL);
		xv_set(toolbar.nodetype_selection,
			ATTR_LIST, get_nodetypelist_for_cycle(),
			PANEL_VALUE, current_nodetype_index,
			NULL);
		xv_set(toolbar.nodefont_selection,
			ATTR_LIST, get_fontlist_for_cycle (),
			PANEL_VALUE, current_nodefont_index,
			NULL);
		xv_set(toolbar.edgefont_selection,
			ATTR_LIST, get_fontlist_for_cycle (),
			PANEL_VALUE, current_edgefont_index,
			NULL);
	}

	return toolbar;
}



/************************************************************************/
/*									*/
/*			Toolbar management				*/
/*									*/
/************************************************************************/


#if FALSE
static	Slist	list_of_toolbars = NULL;

#define for_toolbars(tb,t)	generic_for_slist((tb),(t),Toolbar)
#define end_for_toolbars(tb,t)	end_generic_for_slist((tb),(t),Toolbar)

static	void	add_toolbar (Toolbar toolbar)
{
	list_of_toolbars = add_to_slist (list_of_toolbars,
		make_attr (ATTR_DATA, (char *)toolbar));
}
#endif


void		set_canvas_toolbar_mode (Graphed_mode mode)
{
	int i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set (canvases[i].toolbar.mode_choice,
			PANEL_VALUE, mode,
			NULL);
	}

	switch (mode) { /* Currently unused */
	    case GRAPHED_MODE_CREATE_MODE :
		break;
	    case GRAPHED_MODE_EDIT_MODE :
		break;
	}
}



/************************************************************************/
/*									*/
/*			    Notify - Procedures				*/
/*									*/
/************************************************************************/



static	void	toolbar_mode_choice_notify_proc (Panel_item item, int value, Event *event)
{
	switch ((Graphed_mode)value) {
	    case GRAPHED_MODE_CREATE_MODE :
		dispatch_user_action (CREATE_MODE);
		break;
	    case GRAPHED_MODE_EDIT_MODE :
		dispatch_user_action (EDIT_MODE);
		break;
	    default :
		break;
	}
}


static	void	notify_nodetype_selection (Panel_item item, int value, Event *event)
{
	dispatch_user_action (SET_NODETYPE, value);
}


static	void	notify_edgetype_selection (Panel_item item, int value, Event *event)
{
	dispatch_user_action (SET_EDGETYPE, value);
}



static	void	notify_nodefont_selection (Panel_item item, int value, Event *event)
{
	dispatch_user_action (SET_NODEFONT, value);
}


static	void	notify_edgefont_selection (Panel_item item, int value, Event *event)
{
	dispatch_user_action (SET_EDGEFONT, value);
}

/*
static	void	notify_nodecolor_selection (Panel_item item, int value, Event *event)
{
	dispatch_user_action (SET_NODECOLOR, value);
}


static	void	notify_edgecolor_selection (Panel_item item, int value, Event *event)
{
	dispatch_user_action (SET_EDGECOLOR, value);
}
*/

/************************************************************************/
/*									*/
/*			    Install-procedures				*/
/*									*/
/************************************************************************/


void	install_nodetypelist_in_nodetype_selection (char **list)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.nodetype_selection,
			ATTR_LIST, list,
			NULL);
	}
}


void	install_edgetypelist_in_edgetype_selection (char **list)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.edgetype_selection,
			ATTR_LIST, list,
			NULL);
	}
}



void	install_current_nodetype_in_nodetype_selection (void)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.nodetype_selection,
			PANEL_VALUE, current_nodetype_index,
			NULL);
	}
}


void	install_current_edgetype_in_edgetype_selection (void)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.edgetype_selection,
			PANEL_VALUE, current_edgetype_index,
			NULL);
	}
}


void	install_fontlist_in_nodefont_selection (char **list)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.nodefont_selection,
			ATTR_LIST, list,
			NULL);
	}
}


void	install_fontlist_in_edgefont_selection (char **list)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.edgefont_selection,
			ATTR_LIST, list,
			NULL);
	}
}



void	install_current_nodefont_in_nodefont_selection (void)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.nodefont_selection,
			PANEL_VALUE, current_nodefont_index,
			NULL);
	}
}


void	install_current_edgefont_in_edgefont_selection (void)
{
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.edgefont_selection,
			PANEL_VALUE, current_edgefont_index,
			NULL);
	}
}

void	install_current_nodecolor_in_nodecolor_selection (void)
{
/* Commented MH Conversion
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.nodecolor_selection,
			PANEL_VALUE, current_nodecolor,
			NULL);
	}
*/
}


void	install_current_edgecolor_in_edgecolor_selection (void)
{
/* Commented MH Conversion
	int	i;

	for (i=0; i<N_BUFFERS; i++) if (canvases[i].frame != XV_NULL) {
		xv_set(canvases[i].toolbar.edgecolor_selection,
			PANEL_VALUE, current_edgecolor,
			NULL);
	}
*/
}

/************************************************************************/
/*									*/
/*		Hilfsprozedur zum Erzeugen einer Farbauswahl		*/
/*									*/
/************************************************************************/
/*									*/
/*  static Pixrect *create_colored_pr (width, heigth, depth, color)	*/
/*									*/
/*======================================================================*/
/*									*/
/*  Panel_item	   create_graphed_color_selection_item (panel,		*/
/*                 x,y, notify_proc)					*/
/*									*/
/************************************************************************/


#if FALSE
static	Pixrect	*create_colored_pr (int width, int height, int depth, int color)
{
/** ----------------- von fb auskommentiert   Anfang ----------------- **/
/** Funktion wird anscheinend eh nicht mehr aufgerufen **/
	Pixrect	*pr;

	pr = mem_create (width, height, depth);
	
	pr_rop (pr, 0,0, width,height, PIX_SRC | PIX_COLOR(color), (Pixrect *)NULL, 0, 0);
	
	return pr;
/** ----------------- von fb auskommentiert    Ende ------------------ **/
}
#endif


/* include a little image	*/
static	short	triangle_up_array[] = {
#include "images/triangle_up.pr"
};
my_mpr_static (triangle_up, 16,16,1, triangle_up_array)

#if 0
Panel_item	create_graphed_color_selection_item (Panel panel, int x, int y, int (*notify_proc) ())
{	
	Panel_item	color_selection;
	Xv_Window	pw;
	
	
	pw= (Xv_Window)xv_get(panel, WIN_PIXWIN);
	pw_setcmsname  (pw, GRAPHED_COLORMAP_NAME); 
	pw_putcolormap (pw, 0, GRAPHED_COLORMAPSIZE, red, green, blue);


#define	SQUARE_WIDTH  16
#define	SQUARE_HEIGHT 16
/* Changed MH conversion
#define	SQUARE_DEPTH  (pw->pw_pixrect->pr_depth)
*/
/* UMARBEITUNG NOTWENDIG */

#define SQUARE_DEPTH 1

	color_selection = (Panel)xv_create(panel, PANEL_CHOICE,

/* Changed MH Conversion
#define IMAGE(i) create_colored_pr (SQUARE_WIDTH, SQUARE_HEIGHT, SQUARE_DEPTH, (i))
*/
#define IMAGE(i) xv_create(XV_NULL, SERVER_IMAGE, XV_WIDTH, SQUARE_WIDTH, XV_HEIGHT, SQUARE_HEIGHT, SERVER_IMAGE_BITS, create_colored_pr (SQUARE_WIDTH, SQUARE_HEIGHT, SQUARE_DEPTH, (i)), 0)

		PANEL_CHOICE_IMAGES,
			IMAGE(0),  IMAGE(1),  IMAGE(2),  IMAGE(3),
			IMAGE(4),  IMAGE(5),  IMAGE(6),  IMAGE(7),
			IMAGE(8),  IMAGE(9),  IMAGE(10), IMAGE(11),
			IMAGE(12), IMAGE(13), IMAGE(14), IMAGE(15),
			0,

#define	X(i) (x + SQUARE_WIDTH*(i))
#define	Y(i) (y + SQUARE_HEIGHT*(i))
		PANEL_CHOICE_XS,	X(0), X(1), X(2), X(3), X(4), X(5), X(6), X(7),
					X(0), X(1), X(2), X(3), X(4), X(5), X(6), X(7), 0,
		PANEL_CHOICE_YS,	Y(0), Y(0), Y(0), Y(0), Y(0), Y(0), Y(0), Y(0),
					Y(2), Y(2), Y(2), Y(2), Y(2), Y(2), Y(2), Y(2), 0,
		
		PANEL_MARK_IMAGES,	&triangle_up, 0,
		PANEL_NOMARK_IMAGES,	0,
		PANEL_MARK_XS,		X(0), X(1), X(2), X(3), X(4), X(5), X(6), X(7),
					X(0), X(1), X(2), X(3), X(4), X(5), X(6), X(7), 0,
		PANEL_MARK_YS,		Y(1), Y(1), Y(1), Y(1), Y(1), Y(1), Y(1), Y(1),
					Y(3), Y(3), Y(3), Y(3), Y(3), Y(3), Y(3), Y(3), 0,
					
		PANEL_NOTIFY_PROC,	notify_proc,
		NULL);
	
	return color_selection;
}
#endif
