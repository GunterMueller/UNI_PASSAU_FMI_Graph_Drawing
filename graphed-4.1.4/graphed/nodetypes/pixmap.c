#include "misc.h"
#include "graph.h"
#include "adjust.h"

#include <X11/Xutil.h>

#include "ps.h"
#include "repaint.h"
#include "nodetypes/nodetypes.h"

void init_pixmap_nodetype (void)
{
  /* Dummy */
}

/************************************************************************/
/*									*/
/*	static	void	special_adjust_to_node (node, x1,y1, x2,y2)	*/
/*									*/
/*	Diese Prozedur fuehrt SPECIAL_NODE_EDGE_INTERFACE bei benutzer-	*/
/*	definierten Knotentypen aus : Die Linie wird vom Schnitt-	*/
/*	punkt mit der bounding_box bis zur Knotenmitte mittels eines	*/
/*	modifizierten Bresenham-Algorithmus verfolgt und der erste	*/
/*	Pixel, den die Kante trifft, gesucht. "Treffen" kann hier auch	*/
/*	eine Position unterhalb bzw. oberhalb bedeuten, da es sonst	*/
/*	moeglich ist, dass sich die Linie "durchschlaengelt".		*/
/*									*/
/************************************************************************/


void	adjust_edgeline_to_pixmap_node (Node node, int *x1, int *y1, int x2, int y2)
{
  /* Die Prozedur folgt einem modifizierten Bresenhamalgorithmus.	*/
  /* Die Gerade durch (x1,y1) - (x2,y2) wird von der bounding_box	*/
  /* des Knotens aus bis zum Mittelpunkt verfolgt. Der erste	*/
  /* dabei beruehrte Punkt [(x,y), (x,y-1), (x,y+1)] wird als	*/
  /* neuer Endpunkt (x1,y1) ausgegeben; falls nichts gefunden	*/
  /* wurde, der Knotenmittelpunkt.				*/
  /* DIESEM VERFAHERN LIEGT EINE PROZEDUR AUS DER SOURCE ZUM	*/
  /* SUNTOOLS - BEISPIELPROGRAMM "ICONEDIT" ZUGRUNDE.		*/
	
  register int	d;	/* bleibt dep gleich  oder aendert dep sich	*/
  register int	ind;	/* Abhaengige Variable				*/
  register int	dep;	/* Unabhaengige Variable			*/
  register int	incr_only_ind;    /* Inkrement fuer d, je nachdem dep	*/
  register int	incr_ind_and_dep; /* geaendert wird oder nur ind	*/
  register int	ind_end;	/* Letzter Wert fuer ind ( = Knotenmitte) */
  register int	incr_ind;	/* Inkrement fuer ind			*/
  register int	incr_dep;	/* Inkrement fuer dep			*/
  register int	x_is_ind;	/* Ist x die unabhaengige Variable	*/
  register int	check_d_smaller_zero;	/* d auf <0 oder >0 pruefen ?	*/
  
  int	ind1,  ind2;	/* Startpunkt			*/
  int	dep1,  dep2;	/* Endpunkt			*/
  int	d_ind, d_dep;	/* |Endpunkt - Startpunkt|	*/

  /** direktes Pixellesen aus Pixmap nicht moeglich, darum aus Image lesen **/

  XImage *xi;

  xi = XGetImage(display,
		 node->image->pm,
		 0,0,
		 node_width(node),node_height(node),
		 1,
		 XYPixmap);

  clip_line_out_of_node (node, x1,y1, x2,y2);
  *x1 -= node_left (node);
  *y1 -= node_top  (node);
	
  x2 = node_width  (node) / 2;
  y2 = node_height (node) / 2;
  if (abs(x2 - *x1) > abs(y2 - *y1)) {
    x_is_ind = TRUE;
    ind1 = *x1; dep1 = *y1;
    ind2 = x2;  dep2 = y2;
  } else {
    x_is_ind = FALSE;
    ind1 = *y1; dep1 = *x1;
    ind2 = y2;  dep2 = x2;
  }
	
  d_ind = ind2 - ind1;
  d_dep = dep2 - dep1;
  incr_only_ind    = iif (d_ind > 0, 2*d_dep, - 2*d_dep);
  incr_ind_and_dep = incr_only_ind - iif (d_dep > 0, 2*d_ind, -2*d_ind);
  d = (incr_only_ind + incr_ind_and_dep) / 2;
  ind = ind1;
  dep = dep1;
  ind_end = ind2;
  incr_ind = iif (d_ind >= 0, 1, -1);
  incr_dep = iif (d_dep >= 0, 1, -1);
  check_d_smaller_zero =
    (d_ind > 0  && d_dep > 0) ||
    (d_ind < 0  && d_dep < 0) ||
    (d_dep == 0 && d_ind < 0);


  while (ind != ind_end) {
    if (x_is_ind) {
      if (XGetPixel(xi, ind, dep)) {
	goto found;
      } else if (XGetPixel(xi, ind, dep-1)) {
	goto found;
      } else if (XGetPixel(xi, ind, dep+1)) {
	goto found;
      }
    } else {
      if (XGetPixel (xi, dep, ind)) {
	goto found;
      } else if (XGetPixel (xi, dep-1, ind)) {
	goto found;
      } else if (XGetPixel (xi, dep+1, ind)) {
	goto found;
      }
    }
    if ( iif (check_d_smaller_zero, d<0, d>0) ) {
      d += incr_only_ind;
    } else {
      d += incr_ind_and_dep;
      dep += incr_dep;
    }
    ind += incr_ind;
  }
	
found :	/* goto & label are bad but fast */
  if (x_is_ind) {
    *x1 = node_left (node) + ind;
    *y1 = node_top  (node) + dep;
  } else {
    *x1 = node_left (node) + dep;
    *y1 = node_top  (node) + ind;
  }


  /** Das Image wird nicht mehr gebraucht, Speicher freigeben **/
  XDestroyImage(xi);
}


void	ps_paint_pixmap_node(PsPage *pspage, Node node)
{
  if(!pspage) return;

  ps_paint(pspage, PS_IMAGE,
	   PS_PRIM_LOCATION_XY,
	     (double)node->box.r_left, (double)node->box.r_top,
	   PS_PRIM_SCALING_XY,
	     (double)node->box.r_width, (double)node->box.r_height,
	   PS_IMAGE_NAME,
	     node->type->filename,
	   NULL);
}


