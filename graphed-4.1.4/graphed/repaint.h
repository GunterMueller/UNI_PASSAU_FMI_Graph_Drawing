/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef REPAINT_HEADER
#define REPAINT_HEADER

#include <xview/xview.h>
#include <xview/xv_xrect.h>
#include <xview/canvas.h>

extern	void		repaint_canvas   (Canvas canvas, Xv_Window pw, Display *dpy, Window xwindow, Xv_xrectlist *xrects);
extern  void    	force_repainting (void);
extern  void     	redraw_all       (void);
extern  void  init_graphed_graphics_state (void);


extern Pixmap 	paint_graph_in_rect (Rect *rect, int buffer, int color);

#include <X11/Xlib.h>

/** in repaint.c definiert **/
extern	Display	*display;
extern	GC	pixmap_gc;
extern	GC	global_gc;
extern	Window	xwin;

#endif

