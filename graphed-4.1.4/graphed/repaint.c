/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				repaint.c				*/
/*									*/
/************************************************************************/
/*									*/
/*	repaint.c ist das Kernmodul der Graphik. In diesem Modul	*/
/*	befinden sich Routinen, die Teile der working_area loeschen	*/
/*	un neuzeichnen.							*/
/*									*/
/************************************************************************/

#define xview_other_rl_funcs

#include "misc.h"
#include "graph.h"
#include "draw.h"
#include "paint.h"

#include "graphed_subwindows.h"
#include "repaint.h"
#include "type.h"

#include <xview/xv_xrect.h> /** hinzugefuegt fuer neues 'repaint-canvas' **/
#include <xview/rectlist.h>
/************************************************************************/
/*									*/
/*			GLOBALE PROZEDUREN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	force_repainting            ()				*/
/*	void	repaint_working_area_canvas (canvas, pw, repaint_area)	*/
/*	void	redraw_all                  ()				*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*			GLOBALE VARIABLEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	Edgeline	temporary_edgeline				*/
/*									*/
/*	Diese Edgeline wird beim Neuzeichnen von Teilen des Graphen	*/
/*	wie ein Teil des "regulaeren" Graphen beruecksichtigt.		*/
/*	temporary_edgeline wird im Modul user.c verwendet, um einen	*/
/*	noch nicht vollstaendig eingegebenen Kantenzug zu zeichnen.	*/
/*									*/
/************************************************************************/


Edgeline	temporary_edgeline = (Edgeline)NULL;

Rectlist	global_repaint_rectlists[N_BUFFERS];
Rectlist	global_erase_rectlists[N_BUFFERS];

/************************************************************************/
/*									*/
/*		LOKALE FUNKTIONEN UND PROZEDUREN			*/
/*									*/
/************************************************************************/


static	void	repaint_graph_in_rectlist       (register Rectlist *rectlist, int buffer);
static	void	repaint_graph_in_rect           (Rect *rect, int buffer);

/************************************************************************/
/*									*/
/*			ALLGEMEINES ZUM ALGORITHMUS			*/
/*									*/
/************************************************************************/
/*									*/
/*	Vorgehensweise zum Zeichnen von Objekten			*/
/*									*/
/*	Die Prozeduren draw_... (erase_...) in draw.c tragen fuer zu	*/
/*	zeichnende (loeschende) Objekte zunaechst nur das um diese	*/
/*	liegende Rechteck (node->box, edge->box etc.) in die		*/
/*	global_repaint_rectlist (global_erase_rectlist) ein.		*/
/*	Erst mit dem Aufruf von force_repainting werden diese Listen	*/
/*	abgearbeitet (in erase_graph_in_rectlist und			*/
/*	repaint_graph_in_rectlist bzw. repaint_graph_in_rect).		*/
/*	Das eigentlich Zeichnen findet dann in den Prozeduren		*/
/*	paint_..._internal statt.					*/
/*									*/
/*======================================================================*/
/*									*/
/*	Wird ein Objekt mit erase_... geloescht, so darf		*/
/*	force_repainting erst nach dessen physikalischer Loeschung bzw.	*/
/*	Aenderung verlassen werden, da es sonst noch (genauso)		*/
/*	vorhanden ist und doch wieder gezeichnet wird !			*/
/*									*/
/*	Also ist folgendes Schema einzuhalten :				*/
/*									*/
/*		erase_...						*/
/*		.							*/
/*		.							*/
/*		.							*/
/*		delete_... oder (Aenderung; draw_...)			*/
/*		.							*/
/*		.							*/
/*		.							*/
/*		force_repainting();					*/
/*									*/
/*	force_repainting wird i.a. am Ende von Notify - und Event -	*/
/*	Prozeduren aufgerufen, i.a. am Ende der "Hauptbenutzerstelle"	*/
/*	in user.c							*/
/*									*/
/************************************************************************/



/** globale Variablen **/
GC	 global_gc; /** fuer Operationen auf Bildschirm **/
GC       pixmap_gc; /** fuer Operationen auf einer Bitplane **/
Display  *display;
Window   xwin;

void	init_graphed_graphics_state (void)
{
  int screen_num = 0;
  XGCValues values;		/** nur voruebergehend hier **/
  Pixmap dummy_pm = (Pixmap)0;	/** wird nur hier kurz verwendet **/

  display = (Display *)xv_get(base_frame, XV_DISPLAY); /** display zum generierten base_frame holen ("Hauptfenster") **/
  screen_num = DefaultScreen(display);
  global_gc =DefaultGC(display,screen_num); /** GC von oben erben **/;
  XSetGraphicsExposures(display,global_gc,False); /** Wichtig, sonst koennen
						    keine Knoten mehr
						    verschoben werden, nur noch Chaos... !!!**/
  xwin = (Window)xv_get(base_frame,XV_XID); /** Window ID vom base_frame holen **/

  dummy_pm =XCreatePixmap(display,xwin,1,1,DEFAULT_ICON_DEPTH);
  values.foreground = BlackPixel(display,DefaultScreen(display));
  values.background = WhitePixel(display,DefaultScreen(display));

  pixmap_gc = XCreateGC(display,dummy_pm,(GCForeground|GCBackground),&values);
  XSetGraphicsExposures(display,pixmap_gc,False);
}

/************************************************************************/
/*									*/
/*			REPAINTING, GLOBALE FUNKTIONEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	force_repainting ()					*/
/*									*/
/*	global_repaint_rectlist und global_erase_rectlist werden	*/
/*	ausgewertet und anschliessend geloescht.			*/
/*									*/
/*	Algorithmus :							*/
/*									*/
/*	- Losche Alle Markierungen von Knoten und Kanten		*/
/*	- Fuege global_erase_rectlist in global_repaint_rectlist mit	*/
/*	  ein (alles, was geloescht wird, soll auch wieder		*/
/*	  neugezeichnet werden !)					*/
/*	- Loesche alle Rechtecke in global_erase_rectlist		*/
/*	- Zeichne alle Abschnitte in global_repaint_rectlist neu	*/
/*	  (da sowieso ueberschrieben wird, braucht nichts geloescht	*/
/*	  werden !)							*/
/*	- Bringe jetzt alle Markierungen wieder neu an.			*/
/*									*/
/*	Der Umweg mit den Markierungen ist notwendig, da diese mit	*/
/*	XOR gezeichnet werden. Liegt naemlich eine Markierung (auch	*/
/*	teilweise) in einem Bereich, der geloescht oder ueberschrieben	*/
/*	werden soll, so gibt es Probleme mit dem Nachzeichnen.		*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	repaint_working_area_canvas (canvas, pw, repaint_area)	*/
/*									*/
/*	Wird dem working_area_canvas als REPAINT_PROC uebergeben.	*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	redraw_all ()						*/
/*									*/
/*	Loescht alles und zeichnet den kompletten Graphen neu.		*/
/*									*/
/************************************************************************/


void	force_repainting (void)
{
  Graph	graph;
  Node	node;
  Edge	edge;
  int	buffer;
	
  for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) if (buffers[buffer].used) {
	
    if (rl_empty (&(global_erase_rectlists[buffer])) &&
	rl_empty (&(global_repaint_rectlists[buffer])))
      continue;
		
	
    rl_coalesce (&(global_erase_rectlists[buffer]));
    rl_union (&(global_repaint_rectlists[buffer]),
	      &(global_erase_rectlists[buffer]),
	      &(global_repaint_rectlists[buffer]));
    rl_free (&(global_erase_rectlists[buffer]));
	
#ifdef SV1_COMPATIBLE
    pw_lock (canvases[buffer].pixwin,
	     &(global_repaint_rectlists[buffer].rl_bound));
    pw_batch_on (canvases[buffer].pixwin);
#endif

    for_all_graphs (buffer, graph)
      for_nodes (graph, node)
	set_node_to_be_marked (node);
    for_edge_sourcelist (node, edge)
      set_edge_to_be_marked (edge);
    end_for_edge_sourcelist (node, edge);
    end_for_nodes (graph, node);
    end_for_all_graphs (buffer, graph);
	
	
    if (!rl_empty(&(global_repaint_rectlists[buffer]))) {
		
      rl_coalesce (&(global_repaint_rectlists[buffer]));
      repaint_graph_in_rectlist (
				 &(global_repaint_rectlists[buffer]),
				 buffer);
      rl_free (&(global_repaint_rectlists[buffer]));
    }
	
    for_all_graphs (buffer, graph)
      for_nodes (graph, node)
	switch (node->marked) {
	case TO_BE_MARKED_WITH_SQUARES :
	  set_node_marked (node, MARKED_WITH_SQUARES);
	  break;
	case TO_BE_MARKED_AT_BOUNDARY  :
	  set_node_marked (node, MARKED_AT_BOUNDARY);
	  break;
	default :
	  break;
	}
    for_edge_sourcelist (node, edge) switch (edge->marked) {
    case TO_BE_MARKED_WITH_SQUARES :
      set_edge_marked (edge, MARKED_WITH_SQUARES);
      break;
    case TO_BE_MARKED_AT_BOUNDARY  :
      set_edge_marked (edge, MARKED_AT_BOUNDARY);
      break;
    default :
      break;
    } end_for_edge_sourcelist (node, edge);
    end_for_nodes (graph, node);
    end_for_all_graphs (buffer, graph);

#ifdef SV1_COMPATIBLE
    pw_batch_off (canvases[buffer].pixwin);
    pw_unlock (canvases[buffer].pixwin);
#endif
  }
}

/** nun folgt modifiziertes 'repaint_canvas' ... **/
/** Sprung hierher nach jedem Expose **/
void            repaint_canvas (Canvas canvas, Xv_Window pw, Display *dpy, Window xwindow, Xv_xrectlist *xrects)
                       
                         /** wird nicht verwendet **/
                     
                        
                        
{
  Rect	to_be_done_rect;
  int	i, buffer;


  int x,y,w,h;
  int no_areas;

  global_gc = DefaultGC(dpy,DefaultScreen(dpy)); /** GC 'von oben' erben **/
  display = dpy;   /** zu bearbeitendes display, global deklariert **/
  xwin = xwindow; /** betroffenes Fenster, global deklariert **/

  for (i=N_PASTE_BUFFERS; i<N_BUFFERS; i++) {
    if (canvases[i].canvas == canvas)
      break;
  }
  if (i == N_BUFFERS) {
    return;
  } else {
    buffer = i;
  }

  for(no_areas=0;no_areas<(*xrects).count;no_areas++) {
    x = (*xrects).rect_array[no_areas].x;
    y = (*xrects).rect_array[no_areas].y;
    w = (*xrects).rect_array[no_areas].width;
    h = (*xrects).rect_array[no_areas].height;
    rect_construct (&to_be_done_rect,x,y,w,h);

    rl_rectunion (&to_be_done_rect,
		  &global_repaint_rectlists[buffer],
		  &global_repaint_rectlists[buffer]);
  }

  force_repainting ();

}


void	redraw_all (void)
{
  int	buffer;
  Rect	full_rect;
	
  for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) {
    if (buffers[buffer].used) {
	
      rect_construct (&full_rect,
		      0, 0,
		      (int)xv_get(canvases[buffer].canvas, CANVAS_WIDTH),
		      (int)xv_get(canvases[buffer].canvas, CANVAS_HEIGHT));

      rl_rectunion (&full_rect,
		    &global_erase_rectlists[buffer],
		    &global_erase_rectlists[buffer]);
		              
      rl_rectunion (&full_rect,
		    &global_repaint_rectlists[buffer],
		    &global_repaint_rectlists[buffer]);
    }
  }
}
/************************************************************************/
/*									*/
/*		NEUZEICHNEN VON TEILEN DES GRAPHEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	static	void	repaint_graph_in_rectlist (rectlist, buffer)	*/
/*									*/
/*	Zeichnet einen Graphen ueber repaint_graph_in_rect in dem von	*/
/*	rectlist beschriebenen Gebiet					*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	static	void	repaint_graph_in_rect (rect)			*/
/*									*/
/*	Zeichnet den Graphen im angegebenen Rechteck neu. Die Reihen-	*/
/*	folge ist							*/
/*		- Gitter						*/
/*		- Knoten						*/
/*		- Kanten (Kantenzug - Pfeil - Label)			*/
/*		- temporary_edgeline.					*/
/*	Die Reihenfolge hat (zur Zeit) Einfluss auf das Verhalten bei	*/
/*	Ueberdeckungen (was zuletzt kommt, ist auf jeden Fall		*/
/*	sichtbar); das kann zu leichten Inkonsistenzen bei der		*/
/*	temporary_edgeline fuehren.					*/
/*									*/
/************************************************************************/


static   void		repaint_graph_in_rectlist (register Rectlist *rectlist, int buffer)
{	
  Rectnode	*rn;
		
  /* Achtung : verwendet nichtdokumentierte Datenstruktur		*/
  /*           Rectlist						*/
	
  rn = rectlist_head(rectlist);
  if (rn != (Rectnode *)NULL) {
    repaint_graph_in_rect (&rectnode_rect(rn), buffer); 
    if (rn != rectlist_tail(rectlist))
      do {
	rn = rectnode_next(rn);
	repaint_graph_in_rect (&rectnode_rect(rn), buffer);
      } while (rn != rectlist_tail(rectlist));
  }
}


static	void	repaint_graph_in_rect (Rect *rect, int buffer)
{
  register Graph		g;
  register Node		n;
  register Edge		e;
  register Edgeline	el;
  register int		x,y;
  register int		gridwidth;
	
  /* Funktionsaufrufe zu Makros ... damit es schneller geht	*/
	
#define	graph_intersects_rect(graph, rect)          \
  rect_intersectsrect (&(graph->box), rect)
#define	node_intersects_rect(node, rect)          \
  rect_intersectsrect (&(node->full_box), rect)
#define	edge_intersects_rect(edge, rect)          \
  rect_intersectsrect (&(edge->box), rect)
#define	single_edgeline_intersects_rect(el, rect) \
  rect_intersectsrect (&(el->box), rect)
#define	edgelabel_intersects_rect(edge, rect)     \
  rect_intersectsrect (&(edge->label.box), rect)
#define	arrow_intersects_rect(arrow, rect)        \
  rect_intersectsrect (&(arrow.box), rect)


  rect_marginadjust (rect, MARKER_SQUARE_SIZE/2+1);
  if (rect_width(rect) == 0 || rect_height(rect) == 0) {
    return; /* nicht sichtbar	*/
  }
  rect = set_clip_region(rect, buffer, TRUE, TRUE);
  if (rect_width(rect) == 0 || rect_height(rect) == 0) {
    return; /* nicht sichtbar	*/
  }

  paint_background (rect);
	
	
  gridwidth = get_gridwidth (buffer);
  if (gridwidth > 0) {
	
    int	x_start = rect_left(rect) -  rect_left(rect) % gridwidth;
    int	x_end   = rect_right(rect) + DOTSIZE/2;
    int	y_start	= rect_top(rect) -  rect_top(rect) % gridwidth;
    int	y_end	= rect_bottom(rect) + DOTSIZE/2;
		
    for (x = x_start; x <= x_end; x += gridwidth) {
      for (y = y_start; y <= y_end; y += gridwidth) {
	paint_dot_internal (x,y);
      }
    }
  }
	
  for_all_graphs (buffer, g) if (graph_intersects_rect(g, rect)) {
    for_nodes (g, n) {
      if (node_intersects_rect (n, rect)) {
	paint_node_internal (n, PIX_SRC | PIX_COLOR(n->color));
      }
    } end_for_nodes (g, n);
  } end_for_all_graphs (buffer, g);
	

  for_all_graphs (buffer, g) if (graph_intersects_rect(g, rect)) {
		    
    for_nodes (g, n) {
      for_edge_sourcelist (n, e) {
		
	if (e->type->pattern != NULL && e->type->pattern[0] == 0) {
	  continue;
	}

	if (edge_intersects_rect (e, rect)) {
	  if ((el = e->line) != (Edgeline)NULL) do {
	    if (single_edgeline_intersects_rect (el, rect))
	      paint_single_edgeline_internal (el, e->type,
					      PIX_SRC | PIX_COLOR(e->color));
	    el = el->suc;
	  } while (el->suc != e->line);
	  if (e->source->graph->directed && e->arrow.length > 0 &&
	      arrow_intersects_rect (e->arrow, rect)) {
	    paint_line_internal (e->arrow.x0, e->arrow.y0,
				 e->arrow.x1, e->arrow.y1,
				 PIX_SRC | PIX_COLOR(e->color));
	    paint_line_internal (e->arrow.x1, e->arrow.y1,
				 e->arrow.x2, e->arrow.y2,
				 PIX_SRC | PIX_COLOR(e->color));
	  }
	  if (e->label.visible && edgelabel_intersects_rect (e, rect))
	    paint_edgelabel_internal (e, PIX_SRC | PIX_COLOR(e->color));
	}
			
      } end_for_edge_sourcelist (n, e);
    } end_for_nodes (g, n);
  } end_for_all_graphs (buffer, g);
	
  /* Druchlaufe noch die temporary_edgeline	*/
  if ((el = temporary_edgeline) != (Edgeline)NULL && el != el->suc) do {
    if (single_edgeline_intersects_rect (el, rect))
      paint_single_edgeline_internal (el,
				      get_current_edgetype(),
				      PIX_SRC | PIX_COLOR (current_edgecolor));
    el = el->suc;
  } while (el->suc != temporary_edgeline);
	
  xwin = (Window)xv_get(canvases[buffer].pixwin,XV_XID);
  /** Fertig gezeichnetes Pixmap auf die Arbeitsflaeche kopieren  **/
  XCopyPlane(display,paint_pm,xwin,global_gc,
	     0,0,
	     rect_width (rect), rect_height (rect),
	     rect_left  (rect), rect_top    (rect),
	     1);
}

Pixmap paint_graph_in_rect (Rect *rect, int buffer, int color)
{
  register Graph		g;
  register Node		n;
  register Edge		e;
  register Edgeline	el;
	
  /* Funktionsaufrufe zu Makros ... damit es schneller geht	*/
	
#define	graph_intersects_rect(graph, rect)          \
  rect_intersectsrect (&(graph->box), rect)
#define	node_intersects_rect(node, rect)          \
  rect_intersectsrect (&(node->full_box), rect)
#define	edge_intersects_rect(edge, rect)          \
  rect_intersectsrect (&(edge->box), rect)
#define	single_edgeline_intersects_rect(el, rect) \
  rect_intersectsrect (&(el->box), rect)
#define	edgelabel_intersects_rect(edge, rect)     \
  rect_intersectsrect (&(edge->label.box), rect)
#define	arrow_intersects_rect(arrow, rect)        \
  rect_intersectsrect (&(arrow.box), rect)

  rect = set_clip_region(rect, buffer, FALSE, color);
  if (rect_isnull(rect))
    return (Pixmap)NULL;
	
  for_all_graphs (buffer, g) if (graph_intersects_rect(g, rect))
    for_nodes (g, n)
      if (node_intersects_rect (n, rect))
	paint_node_internal (n, PIX_SRC | PIX_COLOR(n->color));
  end_for_nodes (g, n);
  end_for_all_graphs (buffer, g);
	

  for_all_graphs (buffer, g) if (graph_intersects_rect(g, rect)) {
		    
    for_nodes (g, n) {
      for_edge_sourcelist (n, e) {
		
	if (e->type->pattern != NULL && e->type->pattern[0] == 0) {
	  continue;
	}

	if (edge_intersects_rect (e, rect)) {
	  if ((el = e->line) != (Edgeline)NULL) do {
	    if (single_edgeline_intersects_rect (el, rect))
	      paint_single_edgeline_internal (el, e->type,
					      PIX_SRC | PIX_COLOR(e->color));
	    el = el->suc;
	  } while (el->suc != e->line);
	  if (e->source->graph->directed && e->arrow.length > 0 &&
	      arrow_intersects_rect (e->arrow, rect)) {
	    paint_line_internal (e->arrow.x0, e->arrow.y0,
				 e->arrow.x1, e->arrow.y1,
				 PIX_SRC | PIX_COLOR(e->color));
	    paint_line_internal (e->arrow.x1, e->arrow.y1,
				 e->arrow.x2, e->arrow.y2,
				 PIX_SRC | PIX_COLOR(e->color));
	  }
	  if (e->label.visible && edgelabel_intersects_rect (e, rect))
	    paint_edgelabel_internal (e, PIX_SRC | PIX_COLOR(e->color));
	}
			
      } end_for_edge_sourcelist (n, e);
    } end_for_nodes (g, n);
  } end_for_all_graphs (buffer, g);

  return  paint_pm;
}
