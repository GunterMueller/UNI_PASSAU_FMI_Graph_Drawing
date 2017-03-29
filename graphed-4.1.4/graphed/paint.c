/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt, Thomas Lamshoeft	*/
/************************************************************************/
/*									*/
/*				paint.c					*/
/*									*/
/************************************************************************/

/* rect_intersection */
#define _OTHER_RECT_FUNCTIONS
#include <xview/rect.h>

#include "misc.h"
#include "graph.h"
#include "paint.h"

#include "graphed_subwindows.h"

extern Display *display;
extern GC global_gc;
extern GC pixmap_gc;
extern Window xwin;


/************************************************************************/
/*									*/
/*		GLOBALE FUNKTIONEN UND PROZEDUREN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	paint_node_internal (node, op)				*/
/*	void	paint_virtual_node  (x,y, w,h, image, op)		*/
/*									*/
/*	void	paint_nodetype_on_pr (type, pr, w,h)			*/
/*									*/
/*	void	paint_single_edgeline_internal (el, op)			*/
/*	void	paint_line_internal            (x0,y1, x1,y1, op)	*/
/*	void	paint_edgelabel_internal       (edge, op)		*/
/*									*/
/*	Rect	*set_clip_region (clip_rect)				*/
/*									*/
/*	void	paint_dot_internal     (x,y)				*/
/*	void	paint_rectangle        (pw, x1,y1, x2,y2)		*/
/*	void	paint_line             (pw, x1,y1, x2,y2)		*/
/*	void	paint_marker_square    (pw, x,y)			*/
/*	void	paint_marker_rect      (pw, x,y)			*/
/*	void	paint_marker_rectangle (pw, x,y)			*/
/*									*/
/************************************************************************/
/************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/

Pixmap paint_pm = (Pixmap)0; /** WICHTIG: kein Pointer wie bei PixRects! */
			       
			       
			       
			       /************************************************************************/
/*									*/
/*			LOKALE VARIABLEN				*/
/*									*/
/************************************************************************/


static	int	offset_x = 0;		/* zum Zeichnen in	*/
static	int	offset_y = 0;		/* Clip-Rechteck	*/
/************************************************************************/
/*									*/
/*			ALLGEMEINES					*/
/*									*/
/************************************************************************/
/*									*/
/*			Namensgebung					*/
/*									*/
/*	Prozeduren :							*/
/*									*/
/*	..._internal	Diese Prozeduren sind die eigentlichen Zeichen-	*/
/*			prozeduren. Hier wird auf das Pixrect		*/
/*			paint_pr (s.u.) gezeichnet. force_repainting	*/
/*			(-> repaint.c) etc. arbeiten immer (bis auf	*/
/*			do_unmark_...) ueber diese Prozeduren (sie	*/
/*			werden auch an keiner anderen Stelle verwendet)	*/
/*	..._virtual_...	Hier wird ein "virtuelles" Objekt gezeichnet.	*/
/*			Virtuelle Objekte werden mit PIX_XOR direkt auf	*/
/*			working_area_pixwin_gezeichnet und innerhalb	*/
/*			des "normalen" Neuzeichnens in repaint.c	*/
/*			nicht beachtet.					*/
/*									*/
/*	Argumente :							*/
/*									*/
/*	op		ist immer eine RasterOp - Operation. Naehres	*/
/*			die Handbuecher zu dem Graphiksystem "Pixrect"	*/
/*			und zu "Pixwin" in "SunView".			*/
/*	pr		ist immer ein Pixrect, auf das gezeichnet wird.	*/
/*									*/
/*	Ist kein Pixrect als Argument vorhanden und faellt die Prozedur	*/
/*	nicht unter die beiden oben angegebenen Kategorien, so wird	*/
/*	immer auf die Zeichenflaeche (working_area_canvas_pxiwin)	*/
/*	ausgegeben (ein  Argument op ist in einem solchen Fall immer	*/
/*	vorhanden).							*/
/*									*/
/*======================================================================*/
/*									*/
/*		Die Sache mit "paint_pr"				*/
/*									*/
/*	Beim Neuzeichnen von Teilen des Graphen wird immer		*/
/*	Rechteckweise vorgegangen. Jedes dieser Rechtecke wird einzeln	*/
/*	neu gezeichnet. Dazu wird ein Bitmap der benoetigten Groesse im	*/
/*	Speicher unter der Variablen paint_pr angelegt, das fuer das	*/
/*	Neuzeichnen verwendet wird.					*/
/*	Aus technischen Gruenden muessen dann alle Koordinaten im	*/
/*	System dieses paint_pr liegen, so dass in den Prozeduren,	*/
/*	die auf paint_pr zeichnen (Name paint_..._internal), eine	*/
/*	Transformation erfolgen muss. Diese erfolgt mit den in diesem	*/
/*	Modul deklarierten lokalen Variablen offset_x und offset_y.	*/
/*									*/
/************************************************************************/
/************************************************************************/
/*									*/
/*			KNOTEN ZEICHNEN					*/
/*									*/
/************************************************************************/
/*									*/
/*	void	paint_node_internal (node, op)				*/
/*									*/
/*	Bei systemdefinierten Knotentypen wird auf type->pr_paint_func	*/
/*	zurueckgegriffen.						*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	paint_virtual_node (x,y, w,h, image, op)		*/
/*									*/
/*	Zeichnet einen "virtuellen" Knoten im Rechteck (x,y, w,h)	*/
/*	mit Operation op.						*/
/*									*/
/************************************************************************/


void	paint_node_internal (Node node, int op)
{
  int	line;
  int	lenge;


  if (node->type->is_system) {
    node->type->pm_paint_func (paint_pm,
			       (int)(node_left(node) - offset_x),
			       (int)(node_top(node) - offset_y),
			       (int) node_width (node),
			       (int) node_height (node),
			       op);
  } else {
    /** XSetFunction(display,pixmap_gc,GXor);**/
    XCopyPlane(display,node->image->pm,paint_pm,pixmap_gc,0,0,
	       (int) node_width (node),
	       (int) node_height (node),
	       (int)(node_left(node) - offset_x),
	       (int)(node_top(node) - offset_y),1);
  }
  /**	 neues Font-Handling im Knoten **/
  if (node->label.visible && node->label.text_to_draw != NULL) {
    XSetFont(display,pixmap_gc,(Font)xv_get(node->label.font->xvFont,XV_XID));
    for (line = 0; node->label.text_to_draw[line] != NULL; line++) {
      /** Textlaenge bestimmen **/
      lenge = strlen(node->label.text_to_draw[line]);
      XDrawImageString(display,paint_pm, pixmap_gc,
		       node->label.x - offset_x,
		       node->label.y - offset_y + line*node->label.line_height,
		       node->label.text_to_draw[line],lenge);
    }
  }
}



void		paint_virtual_node (int x, int y, int w, int h, Nodetypeimage image, int op)
{
  XSetFunction(display,global_gc,GXxor);
  XCopyPlane(display,image->pm,xwin,global_gc,0,0,w,h,x-w/2,y-h/2,1);
  XSetFunction(display,global_gc,GXcopy);
}

/************************************************************************/
/*									*/
/*   selbst entwickelte Routine zum Zeichnen von gestrichelten Linien	*/
/*									*/
/************************************************************************/
/*									*/
/* void 	PR_line( pr, x0, y0, x1, y1, brush, tex, op )		*/
/*									*/
/************************************************************************/

void pm_line(Pixmap pm, int x0, int y0, int x1, int y1, char *pattern, int op)
{
/*  register int	pr_put_value = iif (graphed_state.colorscreen == TRUE,
				    PIX_OPCOLOR(op),
				    iif (PIX_OPCOLOR(op) == 0, 0, 1)); */

  int length;


  if (pattern != NULL && pattern[0] == 0) {
    return;
  }

  if(pattern) {
    length = 0;
    while(pattern[length])length++;
    XSetDashes(display,pixmap_gc,0,pattern,length);
    XSetLineAttributes(display,pixmap_gc,0,LineOnOffDash,CapButt,JoinRound);
  }

  XDrawLine(display,pm,pixmap_gc,x0,y0,x1,y1);
  XSetLineAttributes(display,pixmap_gc,0,LineSolid,CapButt,JoinRound);

  return;
}
/************************************************************************/
/*									*/
/*		KNOTENTYP AUF PIXRECT ZEICHNEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	paint_nodetype_on_pr (type, pr, w,h)			*/
/*									*/
/*	Zeichnet auf pr ein Bild des Knotentyps type der Groesse	*/
/*	(w,h), ohne type->images zu benutzen (da naemlich mit dieser	*/
/*	Prozedur gerade type->images erzeugt wird). Dazu wird		*/
/*	folgendermassen vorgegangen :					*/
/*	- ist type->is_system = FALSE, so skaliert die Routine das	*/
/*	  Pixelmuster in type->pr auf die passende Groesse		*/
/*	- sonst kann type->pr_paint_func verwendet werden.		*/
/*	Der Speicherplatz fuer pr wird selbstverstaendlich		*/
/*	bereitgestellt.							*/
/*									*/
/************************************************************************/

void		paint_nodetype_on_pm (Nodetype type, Pixmap pm, int w, int h, int color)
        	     
                   
   		    	/** Groesse des zu zeichnenden Bildes	**/
   		      
{
  if (!type->is_system) {
    scale_pm (type->pm, pm, w,h, color);
  } else {
    type->pm_paint_func (pm, 0,0,w,h, PIX_SRC | PIX_COLOR (color));
  }
}

void		scale_pm(Pixmap source_pm, Pixmap target_pm, int w, int h, int color)
{
  unsigned int	source_width,source_height;
  Window root;
  int x,y;
  unsigned int border_width,depth;
		
  XGetGeometry(display,source_pm,&root,&x,&y,&source_width,&source_height,&border_width,&depth);
		
  if (w > 0 && h > 0) {
	
    register int     i, x,y, xx,yy, xx1,yy1;
    register Pixmap tmp_pm;
		
    /* x,y :    Koordinaten in source_pr		*/
    /* xx,yy :  Koordinaten des zu setzenden	*/
    /*          Punktes in target_pr		*/
    /* xx1-xx : Groesse des zu setzenden Punktes	*/
    /* yy1-yy : in target_pr			*/

    tmp_pm = XCreatePixmap(display,xwin,
			   maximum (w,source_width),
			   maximum (h,source_height),
			   DEFAULT_ICON_DEPTH);

    /** Loeschen und zurueck auf 'or' Modus schalten **/
    XSetFunction(display,pixmap_gc,GXclear);
    XFillRectangle(display,tmp_pm,pixmap_gc,
		   0,0,
		   maximum (w,source_width),
		   maximum (h,source_height));
    XFillRectangle(display,target_pm,pixmap_gc,
		   0,0,
		   maximum (w,source_width),maximum (h,source_height));
    XSetFunction(display,pixmap_gc,GXor);

    for (x = 0; x < source_width; x++) {
			
      xx  = (x*w)/source_width;
      xx1 = ((x+1)*w)/source_width;
				
      if (xx1 == xx) xx1++;
						
      for (i = 0; i < xx1-xx; i++) {
	XCopyPlane(display,source_pm,tmp_pm,pixmap_gc,
		   x,0,
		   1,source_height,
		   xx+i,0,
		   1);

      }
    }
			

    for (y = 0; y < source_height; y++) {
		
      yy  = (y*h)/source_height;
      yy1 = ((y+1)*h)/source_height;
				
      if (yy1 == yy) yy1++;	
						
      for (i = 0; i < yy1-yy; i++) {
	XCopyPlane(display,tmp_pm,target_pm,pixmap_gc,
		   0,y,
		   w,1,0,
		   yy+i,
		   1);
      }
    }
    XSetFunction(display,pixmap_gc,GXcopy);
    XFreePixmap(display,tmp_pm);
  }
}

/************************************************************************/
/*									*/
/*		KANTENTYP AUF PIXRECT ZEICHNEN				*/
/*									*/
/************************************************************************/
/*									*/
/* 	void paint_edgetype_on_pr( type, pr, w, h, color)		*/
/*									*/
/* Zeichnet den Kantentyp type auf das pixrect pr als horizontale Linie	*/
/* auf Hoehe h/2 und ueber die volle Breite w (des pixrects).		*/
/* Zusaetzlich wird in der unteren linken Ecke des pixrects der Name	*/
/* des Kantentyps ausgegeben.						*/
/* Diese Routine wird bisher nur benutzt, um die pixrects der Kanten-	*/
/* typen fuer die Darstellung in der Menueauswahl zu erzeugen.		*/
/************************************************************************/


void paint_edgetype_on_pm(Edgetype type, Pixmap pm, int w, int h, int color)
{
  h-=2; w-=2;
				
  /** PR_line in pm_line umbenannt **/
  pm_line(pm, 0, h/2, w, h/2, 
	  type->pattern,
	  PIX_SRC | PIX_COLOR (color)
	  );

  /* Commented MH Conversion
     pr_text( pr, 0 , h-3 , PIX_SRC | PIX_COLOR (color), pf , &type->filename[last_slash] );
     */
}


/************************************************************************/
/*									*/
/*		SPEZIELLE (SYSTEM -) KNOTENTYPEN ZEICHNEN.		*/
/*									*/
/*	Now in nodetypes.c.  MH, Jul 7, Anno Domini 1994		*/
/*									*/
/************************************************************************/
/*									*/
/*	Hier befinden sich die Prozeduren zum zeichnen von		*/
/*	vordefinierten Knotentypen (d.h. type->system == TRUE).		*/
/*	Bei den entsprechenden Knotentypen werden sie (bzw. Zeiger	*/
/*	darauf) unter type->pr_paint_func und type->pw_paint_func,	*/
/*	je nachdem auf Pixwin oder Pixrect gezeichnet wird, gefuehrt.	*/
/*	(die sich anbietende Parameterisierung ist problematisch, da	*/
/*	die Pixwin- bzw Pixrectzeichenprozeduren keine sind, sondern	*/
/*	Makros).							*/
/*									*/
/*	Die Parameterliste fuer solche Prozeduren muss folgenden	*/
/*	Aufbau haben :							*/
/*									*/
/*	Pixrect	*pr;	Pixrect, auf das gezeichnet wird bzw.	*/
/*	struct	pixwin	*pw;	Pixwin, auf das gezeichnet wird		*/
/*	int	x,y, w,h;	Linke obere Ecke, Breite, Hoehe		*/
/*	int	op;		Pixrect-Zeichenoperation		*/
/*									*/
/*	Die pr_... Prozeduren werden nicht zum Neuzeichnen, sondern	*/
/*	nur beim Anlegen des Bildes (node->image) verwendet.		*/
/*									*/
/*	Weitere Einzelheiten siehe auch use_nodetypeimage im Modul	*/
/*	type.c.								*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	pr_paint_box_node(pr, x,y, w,h, op)			*/
/*									*/
/*	Zeichenprozedur fuer Knoten "#box" (-> system_nodetypes,	*/
/*	type.c).							*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	pr_paint_elliptical_node (pr, x0,y0, a,b, op)		*/
/*									*/
/*	Zeichenprozedur fuer Knoten "#circle" (-> system_nodetypes,	*/
/*	type.c).							*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	pr_paint_diamond_node (pr, x0,y0, a,b, op)		*/
/*									*/
/*	Zeichenprozedur fuer Knoten "#diamond" (-> system_nodetypes,	*/
/*	type.c).							*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*			KANTEN ZEICHNEN					*/
/*									*/
/************************************************************************/
/*									*/
/*	void	paint_single_edgeline_internal (el, op)			*/
/*	void	paint_line_internal            (x0,y0, x1,y1, op)	*/
/*									*/
/*	Zeichnet das Kantenstueck (el) - (el->suc) bzw. die Linie	*/
/*	(x0,y0) - (x1,y1) auf paint_pr.					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	paint_edgelabel_internal       (edge, op)		*/
/*									*/
/*	Zeichne *(edge->label.text_to_draw) (wenn nicht NULL) in	*/
/*	paint_pr.							*/
/*									*/
/************************************************************************/


void		paint_single_edgeline_internal (Edgeline el, Edgetype type, int op)
{
  if ( type != (Edgetype) NULL ) {

    /** von pr nach pm angepasst **/
    pm_line(paint_pm,
	    (int)(el->x      - offset_x),
	    (int)(el->y      - offset_y),
	    (int)(el->suc->x - offset_x),
	    (int)(el->suc->y - offset_y),
	    type->pattern,
	    op
	    );
  } else {
    error( "paint_single_edgeline_internal :\n Edgetype = NULL !!!\n");
  }
}


void	paint_line_internal (int x0, int y0, int x1, int y1, int op)
{
  XDrawLine(display,paint_pm,pixmap_gc,
	    x0 - offset_x, y0 - offset_y,
	    x1 - offset_x, y1 - offset_y);

}


void	paint_edgelabel_internal (Edge edge, int op)
{		
  int lenge;

  if (edge != empty_edge && edge->label.text_to_draw != NULL) {
    XSetFont(display,pixmap_gc,(Font)xv_get(edge->label.font->xvFont,XV_XID));
    /** neues Font Handling **/
    lenge = strlen(*(edge->label.text_to_draw));
    XDrawImageString(display,paint_pm, pixmap_gc,
		     edge->label.x - offset_x,
		     edge->label.y - offset_y,
		     *(edge->label.text_to_draw),
		     lenge);
  }
}


/************************************************************************/
/*									*/
/*			CLIP_REGION SETZEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	Rect	*set_clip_region (clip_rect)				*/
/*									*/
/*	Erzeugt paint_pr, auf das von den paint_..._internal -		*/
/*	Funktionen gezeichnet wird (s.o.). Die Funktion liefert als	*/
/*	Resultat den ab den auf dem Bildschirm sichtbaren Teil von	*/
/*	clip_rect (wenn der also leer ist, braucht man beim		*/
/*	Neuzeichnen nichts zu tun). Achtung : das Resultat ist ein	*/
/*	Zeiger auf eine Funktionslokale static - Variable !		*/
/*									*/
/************************************************************************/


Rect	*set_clip_region (Rect *clip_rect, int buffer, int clip_at_window_boundary, int color)
{
  Rect		rect_to_draw_in;
  Rect		clipped_rect_to_draw_in;
  int		scroll_offset_x = 0;
  int		scroll_offset_y = 0;
  static Rect	clipped_rect;
  Rect		canvas_rect;

  int w,h;

  /* Hole die Groesse des canvas auf dem Bildschirm	*/
  if (clip_at_window_boundary) {
    canvas_rect = *((Rect *)xv_get(canvases[buffer].canvas,
				   CANVAS_VIEWABLE_RECT,
				   canvases[buffer].pixwin));
  } else {
    rect_construct (&canvas_rect, 0,0,
		    (int)xv_get(canvases[buffer].canvas, CANVAS_WIDTH),
		    (int)xv_get(canvases[buffer].canvas, CANVAS_HEIGHT));
  }
  if (paint_pm != (Pixmap)NULL)
    XFreePixmap(display,paint_pm);

  /* Hole den Scroll-Offset	*/
  if (clip_at_window_boundary) {
    get_scroll_offset (buffer, &scroll_offset_x, &scroll_offset_y);
  } else {
    scroll_offset_x = 0;
    scroll_offset_y = 0;
  }
	
  rect_to_draw_in = *clip_rect;
  rect_intersection (&canvas_rect,
		     &rect_to_draw_in,
		     &clipped_rect_to_draw_in);
	
  /*
     fprintf (stderr, "---\n");
     fprintf (stderr, "canvas_rect : ");
     rect_print(&canvas_rect);
     fprintf (stderr, "rect_to_draw_in : ");
     rect_print(&rect_to_draw_in);
     fprintf (stderr, "clipped_rect_to_draw_in : ");
     rect_print(&clipped_rect_to_draw_in);
     */

  /** Erzeuge paint_pm  **/
  /** Manchmal werden Pixmaps mit Dimension 0 angefordert (woher?), das fuehrt im 
    schlimmsten Fall zum Absturz, zumindest aber zum unsinnigem Verhalten 
    der Routinen. Abhilfe: In diesem Fall wird die Ausdehnung einfach um 1 erhoeht **/
  if(!(w = rect_width  (&clipped_rect_to_draw_in)))w++;
  if(!(h = rect_height (&clipped_rect_to_draw_in)))h++;

  /*
     fprintf (stderr, "w = %d h = %d\n", w, h);
     */
  paint_pm =XCreatePixmap(display,xwin,w,h, DEFAULT_ICON_DEPTH);

  /** das pixmap loeschen **/
  XSetFunction(display,pixmap_gc,GXclear);
  XFillRectangle(display,paint_pm,pixmap_gc,0,0,
		 rect_width  (&clipped_rect_to_draw_in),
		 rect_height (&clipped_rect_to_draw_in));
  XSetFunction(display,pixmap_gc,GXcopy);


  /* Berechne den Offset fuer die Koordinaten, da diese	*/
  /* im System des clip_rect liegen muessen		*/
  offset_x = rect_left (&clipped_rect_to_draw_in);
  offset_y = rect_top  (&clipped_rect_to_draw_in);
	 
  clipped_rect = clipped_rect_to_draw_in;
	
  return &clipped_rect;
  /* ACHTUNG : Rueckgabe Zeiger auf statische Variable !	*/
}

/************************************************************************/
/*									*/
/*		ZUSAETZLICHE ZEICHENPROZEDUREN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	paint_dot_internal (x,y)				*/
/*									*/
/*	Gibt einen Punkt (Groesse DOTSIZE, ->paint.h) an der Stelle	*/
/*	(x,y) aus. Diese Prozedur wird verwendet, um die Gitterpunkte	*/
/*	(-> show_grid in draw.c) zu zeichnen.				*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	paint_rectangle (pw, x1,y1, x2,y2, op)			*/
/*									*/
/*	Zeichnet ein Rechteck mit linker oberer Ecke (x1,y1) und	*/
/*	rechter unterer Ecke (x2,y2).					*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	paint_line (pw, x1,y1, x2,y2, op)			*/
/*									*/
/*	Zeichnet eine Linie (x1,y1) - (x2,y2).				*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	paint_marker_square    (pw, x,y)			*/
/*	void	paint_marker_rect      (pw, x,y)			*/
/*	void	paint_marker_rectangle (pw, x,y)			*/
/*									*/
/*	Zeichnen Markierungen fuer do_mark_node bzw. do_unmark_node.	*/
/*	Beide haben die Groesse MARKER_SQUARE_SIZE			*/
/*	(-> paint.h); bei "square" handelt sich es um ein ausgefuelltes	*/
/*	Rechteck.							*/
/*	Da in beiden Faellen mit XOR gezeichnet wird, koennen Zeichnen	*/
/*	und Loeschen mit der selben Prozedur erfolgen.			*/
/*									*/
/************************************************************************/



void	paint_dot_internal (int x, int y)
{
  XFillRectangle(display,paint_pm,pixmap_gc,
		 x - DOTSIZE/2 - offset_x, y - DOTSIZE/2 - offset_y,
		 DOTSIZE, DOTSIZE);
}



void	paint_rectangle (int x1, int y1, int x2, int y2, int op)
{
  XSetFunction(display,global_gc,GXxor);
  XDrawLine(display,xwin,global_gc,x1,y1,x1,y2);
  XDrawLine(display,xwin,global_gc,x1,y2,x2,y2);
  XDrawLine(display,xwin,global_gc,x2,y2,x2,y1);
  XDrawLine(display,xwin,global_gc,x2,y1,x1,y1);
  XSetFunction(display,global_gc,GXcopy);
}


void	paint_line (int x1, int y1, int x2, int y2, int op)
{
  XSetFunction(display,global_gc,GXxor);
  XDrawLine(display,xwin,global_gc,x1,y1,x2,y2);
  XSetFunction(display,global_gc,GXcopy);
}


void	paint_marker_square (int x, int y)
{
  XSetFunction(display,global_gc,GXxor);
  XFillRectangle(display,xwin,global_gc,
		 x - MARKER_SQUARE_SIZE/2,
		 y - MARKER_SQUARE_SIZE/2,
		 MARKER_SQUARE_SIZE,
		 MARKER_SQUARE_SIZE);
  XSetFunction(display,global_gc,GXcopy);
}


void	paint_marker_rect (int x, int y)
{
  int	x1,y1, x2,y2;
	
  x1 = x - MARKER_SQUARE_SIZE/2;
  y1 = y - MARKER_SQUARE_SIZE/2;
  x2 = x1 + MARKER_SQUARE_SIZE;
  y2 = y1 + MARKER_SQUARE_SIZE;

  XSetFunction(display,global_gc,GXxor);
  XDrawRectangle(display,xwin,global_gc,x1,y1,abs(x2-x1)-1,abs(y2-y1)-1);
  XSetFunction(display,global_gc,GXcopy);
}


void	paint_marker_rectangle (int x1, int y1, int x2, int y2)
{
	
  x1 = x1 - MARKER_SQUARE_SIZE/2;
  y1 = y1 - MARKER_SQUARE_SIZE/2;
  x2 = x2 - MARKER_SQUARE_SIZE/2;
  y2 = y2 - MARKER_SQUARE_SIZE/2;

  XSetFunction(display,global_gc,GXxor);
  XFillRectangle(display,xwin,global_gc,
		 x1,
		 y1,
		 x2-x1+MARKER_SQUARE_SIZE,
		 y2-y1+MARKER_SQUARE_SIZE);
  XFillRectangle(display,xwin,global_gc,
		 x1+MARKER_SQUARE_SIZE,
		 y1+MARKER_SQUARE_SIZE,
		 x2-x1-MARKER_SQUARE_SIZE,
		 y2-y1-MARKER_SQUARE_SIZE);
  XSetFunction(display,global_gc,GXcopy);
}



void	paint_background (Rect *rect)
{
#if FALSE
  extern	Pixrect	*background_pixrect;

  if (background_pixrect != (Pixrect *)NULL) {
    pr_replrop (paint_pr,
		rect_left (rect)- offset_x,
		rect_top (rect) - offset_y,
		rect_width (rect),
		rect_height (rect),
		PIX_SRC | PIX_COLOR(8), /* Gray */
		background_pixrect,
		/*
		   rect_left (rect) % background_pixrect->pr_width,
		   rect_top (rect)  % background_pixrect->pr_height);
		   */
		rect_left (rect),
		rect_top (rect));
  }
#endif
}

