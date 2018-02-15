/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
/************************************************************************/
/*									*/
/*				adjust.c				*/
/*									*/
/************************************************************************/
/*									*/
/*	Dieses Modul enthaelt Prozeduren fuer das automatische		*/
/*	Anpassen u.a. von						*/
/*	- Kanten an Knoten						*/
/*	- Label an Knoten und Kanten					*/
/*	- Pfeile an Kanten						*/
/*	- Neujustieren von Knoten nach Formaenderung			*/
/*									*/
/************************************************************************/


#include "misc.h"
#include "graph.h"
#include "draw.h"
#include "adjust.h"

extern void adjust_edgeline_to_pixmap_node (Node node, int *x1, int *y1, int x2, int y2); /* nodetypes/pixmap.c */

/************************************************************************/
/*									*/
/*		GLOBALE FUNKTIONEN UND PROZEDUREN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	adjust_line_to_node (mode, source, x1,y1,		*/
/*		                                   x2,y2, target)	*/
/*	void	adjust_edgeline_to_node    (mode, edge, sourcenode,	*/
/*		                                        targetnode)	*/
/*									*/
/*	void	adjust_nodelabel_position     (node)			*/
/*	void	adjust_edgelabel_position     (edge)			*/
/*	void	adjust_nodelabel_text_to_draw (node)			*/
/*	void	adjust_edgelabel_text_to_draw (edge)			*/
/*									*/
/*	void	adjust_arrow_to_edge (edge)				*/
/*									*/
/*	void	adjust_edge_box (edge)					*/
/*									*/
/*	void	adjust_edge_head (edge)					*/
/*	void	adjust_edge_tail (edge)					*/
/*	void	adjust_all_edges (node)					*/
/*									*/
/*      void   adjust_boxes_in_graph (graph)                            */
/*      void   adjust_boxes_in_group  (group)				*/
/*									*/
/*	void	clip_line_out_of_node (node, x1,y1, x2,y2);		*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*			GLOBALE VARIABLEN				*/
/*									*/
/************************************************************************/

int	box_adjustment_enabled = TRUE;


/************************************************************************/
/*									*/
/*		LOKALE FUNKTIONEN UND PROZEDUREN			*/
/*									*/
/************************************************************************/


static	void	clip_straight_line_nei (Node node, int *x1, int *y1, int x2, int y2);


/************************************************************************/
/*									*/
/*			KONVENTIONEN					*/
/*									*/
/*	- Alle globalen Prozeduren in diesem Modul beginnen mit		*/
/*	  adjust_... .							*/
/*	- Keine Prozedur loescht bzw. zeichnet das anzupassende Objekt.	*/
/*	  Das ist vielmehr Aufgabe der aufrufenden Prozedur.		*/
/*									*/
/************************************************************************/



/************************************************************************/
/*									*/
/*		Anpassung von Kanten an Knoten				*/
/*									*/
/*======================================================================*/
/*									*/
/*	Die Art der Anpassung wird durch das Attribut			*/
/*	node_edge_interface im source- und/oder target- Knoten		*/
/*	bestimmt :							*/
/*	NO_NODE_EDGE_INTERFACE : die Endpunkte der Linien werden nicht	*/
/*		veraendert.						*/
/*	TO_BORDER_OF_BOUNDING_BOX : die anzupassenden Endpunkte werden	*/
/*		auf die Mitte der naechstliegenden Seite der		*/
/*		bounding_box des Knotens gesetzt.			*/
/*	TO_CORNER_OF_BOUNDING_BOX : die anzupassenden Endpunkte werden	*/
/*		auf die naechstliegende Ecke der bounding_box des	*/
/*		Knotens gesetzt.					*/
/*	CLIPPED_TO_MIDDLE_OF_NODE : die Linie wird zunaechst auf den	*/
/*		Mittelpunkt des Knotens gefuehrt und dann an der	*/
/*		bounding_box abgeschnitten.				*/
/*	SPECIAL_NODE_EDGE_INTERFACE : hier wird zwischen system-	*/
/*		definierten Knotentypen (is_system == TRUE) und		*/
/*		benutzerdefinierten Typen (ueber Pixelmuster)		*/
/*		unterschieden :						*/
/*		- bei benutzerdefinierten Typen wird analog zu		*/
/*		CLIPPED_TO_MIDDLE_OF_NODE verfahren, jedoch nicht schon	*/
/*		an der bounding_box, sondern erst am ersten gesetzten	*/
/*		Pixel abgeschnitten;					*/
/*		- bei systemdefinierten Typen wird die im Typ		*/
/*		angegebene Prozedur (adjust_func) aufgerufen (die	*/
/*		dasselbe wie oben bewirkt, nur schneller ist).		*/
/*									*/
/************************************************************************/
/*									*/
/*	void	adjust_line_to_node (mode, source, x1,y1,		*/
/*                                                 x2,y2, target)	*/
/*									*/
/*	Passt die Linie (x1,y1) - (x2,y2) an source und/oder target	*/
/*	an, je nachdem mode gesetzt ist :				*/
/*	ADJUST_EDGELINE_HEAD : Anpassung an source			*/
/*	ADJUST_EDGELINE_TAIL : Anpassung an tail			*/
/*	ADJUST_EDGELINE_HEAD_AND_TAIL : Anpassung an source UND tail.	*/
/*									*/
/*	WICHTIG : Soll die Linie an beide Knoten angepasst werden, muss	*/
/*	unbedingt mit ADJUST_EDGELINE_HEAD_AND_TAIL angepasst werden,	*/
/*	da sonst Inkonsistenzen auftreten koennen (Reihenfolge !).	*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	adjust_edgeline_to_node (mode, edge, sourcenode,	*/
/*		                                     targetnode)	*/
/*									*/
/*	Passt eine Kante an ihre Anfangs- und/oder Endknoten an.	*/
/*	Falls edge == NULL, so wird die temporary_edgeline		*/
/*	(->repaint.c) an sourcenode und/oder targetnode angepasst,	*/
/*	ansonsten wird edge an edge->source bzw. edge->target		*/
/*	angepasst.							*/
/*	Mode gibt an, an welche(n) Knoten angepasst wird :		*/
/*	ADJUST_EDGELINE_HEAD :          Anpassung an source		*/
/*	ADJUST_EDGELINE_TAIL :          Anpassung an target		*/
/*	ADJUST_EDGELINE_HEAD_AND_TAIL : Anpassung an source UND tail.	*/
/*	Diese Prozedur verwendet (natuerlich) adjust_line_to_node.	*/
/*									*/
/*	WICHTIG : Soll die Kante an beide Knoten angepasst werden, muss	*/
/*	unbedingt mit ADJUST_EDGELINE_HEAD_AND_TAIL angepasst werden,	*/
/*	da sonst Inkonsistenzen auftreten koennen (Reihenfolge !).	*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	clip_line_out_of_node (node, x1,y1, x2,y2)		*/
/*									*/
/*	Klippt die Linie (x1,y1) - (x2,y2) an der bounding_box von	*/
/*	node, so dass sie ganz ausserhalb zu liegen kommt. Liegt die	*/
/*	Linie urspruenglich ganz innerhalb des Knotens, so schrumpft	*/
/*	sie zu (x2,y2) - (x2,y2) zusammen.				*/
/*									*/
/************************************************************************/



void		adjust_line_to_node (Nei_adjust_mode mode, Node source, int *x1, int *y1, int *x2, int *y2, Node target)
{
  int	p1_x[4], p1_y[4],	/* Cluster von Punkten		*/
        p2_x[4], p2_y[4];
  int	min_i,min_j;
  int	x,y;

  /* in p1_x, ..., p2_y werden fuer TO_CORNER_OF_BOUNDING_BOX und	*/
  /* TO_BORDER_OF_BOUNDING_BOX die Ecken bzw. Seitenmittelpunkte	*/
  /* eingegeben							*/
	
  if ((mode == ADJUST_EDGELINE_HEAD) ||
      (mode == ADJUST_EDGELINE_HEAD_AND_TAIL) ) {
	
    switch (source->node_edge_interface) {
		
    case TO_CORNER_OF_BOUNDING_BOX :
      p1_x[0] = p1_x[3] = node_left(source);
      p1_x[1] = p1_x[2] = node_left(source)  + node_width(source);
      p1_y[0] = p1_y[1] = node_top(source);
      p1_y[2] = p1_y[3] = node_top(source) + node_height(source);
      break;
		
    case TO_BORDER_OF_BOUNDING_BOX :
      p1_x[3] =           node_left(source);
      p1_x[0] = p1_x[2] = node_left(source)  + node_width(source)/2;
      p1_x[1] =           node_left (source) + node_width(source);
      p1_y[0] =           node_top(source);
      p1_y[1] = p1_y[3] = node_top(source) + node_height(source)/2;
      p1_y[2] =           node_top(source) + node_height(source);
      break;
    default: break;
    }
  }
	
  if ((mode == ADJUST_EDGELINE_TAIL) ||
      (mode == ADJUST_EDGELINE_HEAD_AND_TAIL) ) {
	
    switch (target->node_edge_interface) {
		
    case TO_CORNER_OF_BOUNDING_BOX :
      p2_x[0] = p2_x[3] = node_left(target);
      p2_x[1] = p2_x[2] = node_left(target)  + node_width(target);
      p2_y[0] = p2_y[1] = node_top(target);
      p2_y[2] = p2_y[3] = node_top(target) + node_height(target);
      break;

    case TO_BORDER_OF_BOUNDING_BOX :
      p2_x[3] =           node_left(target);
      p2_x[0] = p2_x[2] = node_left(target) + node_width(target)/2;
      p2_x[1] =           node_left(target) + node_width(target);
      p2_y[0] =           node_top(target);
      p2_y[1] = p2_y[3] = node_top(target) + node_height(target)/2;
      p2_y[2] =           node_top(target) + node_height(target);
      break;
    default: break;
    }
  }

	
  switch (mode) {
	
  case ADJUST_EDGELINE_HEAD :
    
    switch (source->node_edge_interface) {
		
    case TO_CORNER_OF_BOUNDING_BOX :
    case TO_BORDER_OF_BOUNDING_BOX :
      p2_x[0] = *x2;
      p2_y[0] = *y2;
      find_min_distance_between_pointclusters (p1_x,p1_y,4, p2_x,p2_y,1, &min_i,&min_j);
      *x1 = p1_x[min_i];
      *y1 = p1_y[min_i];
      break;
		
    case CLIPPED_TO_MIDDLE_OF_NODE :
      clip_line_out_of_node (source, x1,y1, *x2,*y2);
      break;
			
    case STRAIGHT_LINE_NEI :
      clip_straight_line_nei (source, x1,y1, *x2,*y2);
      break;

    case SPECIAL_NODE_EDGE_INTERFACE :
      if (source->type->is_system)
	(source->type->adjust_func) (source, x1,y1, *x2,*y2);
      else
	adjust_edgeline_to_pixmap_node (source, x1,y1, *x2,*y2);
      break;
			
    default : 
      break;
    }
    break;
		
		
  case ADJUST_EDGELINE_TAIL :
	    
    switch (target->node_edge_interface) {
		
    case TO_CORNER_OF_BOUNDING_BOX :
    case TO_BORDER_OF_BOUNDING_BOX :
      p1_x[0] = *x1;
      p1_y[0] = *y1;
      find_min_distance_between_pointclusters (p1_x,p1_y,1, p2_x,p2_y,4, &min_i,&min_j);
      *x2 = p2_x[min_j];
      *y2 = p2_y[min_j];
      break;
	
    case CLIPPED_TO_MIDDLE_OF_NODE :
      clip_line_out_of_node (target, x2,y2, *x1,*y1);
      break;
			
    case STRAIGHT_LINE_NEI :
      clip_straight_line_nei (target, x2,y2, *x1,*y1);
      break;

    case SPECIAL_NODE_EDGE_INTERFACE :
      if (target->type->is_system)
	(target->type->adjust_func) (target, x2,y2, *x1,*y1);
      else
	adjust_edgeline_to_pixmap_node (target, x2,y2, *x1,*y1);
      break;
      
    default :
      break;
    }
    break;

			
  case ADJUST_EDGELINE_HEAD_AND_TAIL :
		
    if (source->node_edge_interface == target->node_edge_interface) {
		
      switch (source->node_edge_interface) {
	
      case TO_BORDER_OF_BOUNDING_BOX :
      case TO_CORNER_OF_BOUNDING_BOX :
			
	find_min_distance_between_pointclusters (p1_x,p1_y, 4,  p2_x,p2_y, 4,  &min_i,&min_j);
	*x1 = p1_x[min_i];  *y1 = p1_y[min_i];
	*x2 = p2_x[min_j];  *y2 = p2_y[min_j];
	break;
			
      case CLIPPED_TO_MIDDLE_OF_NODE :
	clip_line_out_of_node (source, x1,y1, node_x(target),node_y(target));
	clip_line_out_of_node (target, x2,y2, node_x(source),node_y(source));
	break;
			
      case STRAIGHT_LINE_NEI :
	clip_straight_line_nei (source, x1,y1, node_x(target),node_y(target));
	/* clip_straight_line_nei (target, x2,y2, node_x(source),node_y(source)); */
	clip_straight_line_nei (target, x2,y2, *x1, *y1);
	break;


      case SPECIAL_NODE_EDGE_INTERFACE :
	x = node_x (target); y = node_y (target);
	adjust_line_to_node (ADJUST_EDGELINE_HEAD, source, x1,y1, &x,&y, target);
	x = node_x (source); y = node_y (source);
	adjust_line_to_node (ADJUST_EDGELINE_TAIL, source, &x,&y, x2,y2, target);
	break;
			
      default :
	break;
      }

    } else {
		
      if ((int)source->node_edge_interface <
	  (int)target->node_edge_interface) {
	adjust_line_to_node (ADJUST_EDGELINE_HEAD,
			     source, x1,y1, x2,y2, target);
	adjust_line_to_node (ADJUST_EDGELINE_TAIL,
			     source, x1,y1, x2,y2, target);
      } else {
	adjust_line_to_node (ADJUST_EDGELINE_TAIL,
			     source, x1,y1, x2,y2, target);
	adjust_line_to_node (ADJUST_EDGELINE_HEAD,
			     source, x1,y1, x2,y2, target);
      }
    }
    
    break;
    
  }

}
			
			
			
void		adjust_edgeline_to_node (Nei_adjust_mode mode, Edge edge, Node sourcenode, Node targetnode)
{
  int		x1,y1, x2,y2;
  Edgeline	el;
  Node		source, target;
  
  if (edge == empty_edge) {
    el = temporary_edgeline;
    source = sourcenode;
    target = targetnode;
  } else {
    el = edge->line;
    source = edge->source;
    target = edge->target;
  }
  switch (mode) {
  case ADJUST_EDGELINE_HEAD          :
  case ADJUST_EDGELINE_HEAD_AND_TAIL :
    x1 = el->x;
    y1 = el->y;
    x2 = el->suc->x;
    y2 = el->suc->y;
    break;
  case ADJUST_EDGELINE_TAIL :
    x1 = el->pre->pre->x;
    y1 = el->pre->pre->y;
    x2 = el->pre->x;
    y2 = el->pre->y;
    break;
  }
  
  adjust_line_to_node (mode, source, &x1,&y1, &x2,&y2, target);
  
  switch (mode) {
  case ADJUST_EDGELINE_HEAD          :
  case ADJUST_EDGELINE_HEAD_AND_TAIL :
    set_edgeline_xy (el,      x1,y1);
    set_edgeline_xy (el->suc, x2,y2);
    break;
  case ADJUST_EDGELINE_TAIL :
    set_edgeline_xy (el->pre->pre, x1,y1);
    set_edgeline_xy (el->pre,      x2,y2);
    break;
  }
}


void	clip_line_out_of_node (Node node, int *x1, int *y1, int x2, int y2)
{
  register int	x = node_x (node),
  y = node_y (node),
  dx  = x2 - x,
  dy  = y2 - y,
  adx = abs(dx),
  ady = abs(dy),
  width  = node_width  (node),
  height = node_height (node);
  
  if ( (adx > width/2) || (ady > height/2) ) {
    /* (x2,y2) ausserhalb des Knotens	*/
    if (adx * height >= ady * width ) {
      if (dx >0) {
	*x1 = x + (width - width/2);
	*y1 = y + ((width -width/2) * dy) / dx;
      } else {
	*x1 = x - width/2;
	*y1 = y - (width/2 * dy) / dx;
      }
    } else {
      if (dy >0) {
	*x1 = x + ((height - height/2) * dx) / dy;
	*y1 = y + (height - height/2);
      } else {
	*x1 = x - (height/2 * dx) / dy;
	*y1 = y - height/2;
      }
    }
  } else {
    /* (x2,y2) innerhalb des Knotens	*/
    *x1 = x2;
    *y1 = y2;
  }
}




static	void	clip_straight_line_nei (Node node, int *x1, int *y1, int x2, int y2)
{
  register int	x = node_x (node),
  y   = node_y (node),
  dx  = x2 - x,
  dy  = y2 - y,
  adx = abs(dx),
  ady = abs(dy),
  width  = node_width  (node),
  height = node_height (node);
	
  if ( (adx > width/2) || (ady > height/2) ) {
    /* (x2,y2) ausserhalb des Knotens	*/
    if (dx > 0) {
      *x1 = minimum (x2, x + (width - width/2));
    } else {
      *x1 = maximum (x2, x - width/2);
    }
    if (dy > 0) {
      *y1 = minimum (y2, y + (height - height/2));
    } else {
      *y1 = maximum (y2, y - height/2);
    }
  } else {
    /* (x2,y2) innerhalb des Knotens	*/
    *x1 = x2;
    *y1 = y2;
  }
}



int	line_line_intersection (int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int *x, int *y)
   	             	/* first line				*/
	             	/* second line				*/
   	       		/* *the* return point if result is TRUE	*/
{
	/* Note : this procedure does NOT check whether the	*/
	/* intersection point lies in [x1,x2]*[x3,x4] and	*/
	/* [y1,y2]*[y3,y4].					*/
	
	double	dx12 = x2 - x1,
		dy12 = y2 - y1,
		dx34 = x4 - x3,
		dy34 = y4 - y3;
		
	if (dx12 == 0 && dx34 == 0) {
		if (x1 == x3) {
			*x = x1; *y = y1; return TRUE;
		} else {
			return FALSE;
		}
	} else if (dx12 == 0) /* dx34 != 0 */ {
		*y = y3 + dy34/dx34 * (x1 - x3);
		*x = x1;
		return TRUE;
	} else if (dx34 == 0) /* dx12 != 0 */ {
		*y = y1 + dy12/dx12 * (x3 - x1);
		*x = x3;
		return TRUE;
	} else /* dx12 !=0 && dx34 != 0 */ {
		double	m12 = dy12 / dx12,	/* Slopes	*/
			m34 = dy34 / dx34;
		*x = (y3-y1 - (m34*x3 - m12*x1)) / (m12 - m34);
		*y = y1 + (*x-x1)*m12;
		return TRUE;
	}	
}



/************************************************************************/
/*									*/
/*		LABEL AN KNOTEN UND KANTEN ANPASSEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	Zum Anpassen von Labeln sind zwei Arten von Prozeduren		*/
/*	vorhandem :							*/
/*		- adjust_..._text_to_draw passt den Text an (Feld	*/
/*		text_to_draw in Nodelabel bzw. Edgelabel). Dazu werden	*/
/*		nicht wiedergebbare Zeichen aus dem Text entfernt und	*/
/*		gleichzeitig der Text auf die erste Zeile gekuerzt.	*/
/*		Ausserdem darf der Text die vorgegebene Maximalgroesse	*/
/*		nicht ueberschreiten.					*/
/*		- adjust_..._position passt nur die Position des Labels	*/
/*		an, nicht aber den Text.				*/
/*	Wird also der Text des Labels geaendert, muessen beide		*/
/*	Prozeduren aufgerufen werden, aendert sich nur die Position,	*/
/*	so genuegt adjust_...position.					*/
/*									*/
/*	Am Knotenrand werden jeweils NODELABEL_GAP Pixel Platz		*/
/*	gelassen; damit kann der Label auch dann in die Ecke		*/
/*	gezeichnet werden, wenn der Knoten eine Umrahmung (z.B.		*/
/*	Rechteckknoten) hat.						*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	adjust_nodelabel_position (node)			*/
/*									*/
/*	Passt den Knotenlabel entsprechend node->label.placement an.	*/
/*	Knotenlabel liegen grundsaetzlich innerhalb der Bounding-box	*/
/*	des Knotens.							*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	void	adjust_edgelabel_position (edge)			*/
/*									*/
/*	Justiert die Position des Kantenlabels neu. Der Kantenlabel	*/
/*	wird in die Mitte des ersten Teilstuecks von edge->line		*/
/*	gesetzt.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	adjust_nodelabel_text_to_draw (node)			*/
/*									*/
/*	Maximalgroesse fuer den Text ist die bounding_box des Knotens.	*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	void	adjust_edgelabel_text_to_draw (edge)			*/
/*									*/
/*	Maximalgroesse fuer den Text ist (current_edgelabel_size_x *	*/
/*	current_edgelabelsize_y).					*/
/*									*/
/************************************************************************/


/** ----------------- von fb auskommentiert   Anfang ----------------- **
struct	pr_subregion	compute_lines_subregion_size (lines, font)
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
struct	pm_subregion	compute_lines_subregion_size (char **lines, Graphed_font font)
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/
    			        
            		     
{
/** ----------------- von fb auskommentiert   Anfang ----------------- **
	struct	pr_subregion label_bound, this_line_bound;
 ** ----------------- von fb auskommentiert    Ende ------------------ **/
/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	struct	pm_subregion label_bound;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	int	line;
	int	label_bound_max_x, label_bound_max_y;
	
/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	/** neue lokale variablen **/
	int direction,ascent,descent;
	XCharStruct overall_return;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	if (lines == NULL)
		return label_bound;
	
	/* Zuerst berechnen wir in r eine neue Boundingbox fuer		*/
	/* den Label ...						*/
	label_bound_max_x = 0;
	label_bound_max_y = 0;
	for (line=0; lines[line] != NULL; line++) {
		
#ifdef XVIEW_COMMENT
     XView CONVERSION - Use pf_textbound instead, remember to extern it
#endif
/** ----------------- von fb auskommentiert   Anfang ----------------- **
pf_textbound (&this_line_bound,
			strlen(lines[line]),
			font->font,
			lines[line]);
		label_bound_max_x = maximum (label_bound_max_x, this_line_bound.size.x);
		label_bound_max_y = maximum (label_bound_max_y, this_line_bound.size.y);
	}
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
       XTextExtents((XFontStruct*)xv_get(font->xvFont,FONT_INFO),lines[line],strlen(lines[line]),
		    &direction,
		    &ascent,
		    &descent,
		    &overall_return);
		label_bound_max_x = maximum (label_bound_max_x, overall_return.width);
		label_bound_max_y = maximum (label_bound_max_y, ascent+descent);
	}
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	label_bound.size.x = label_bound_max_x;
	label_bound.size.y = line * label_bound_max_y;
	
	return label_bound;
}


void	adjust_nodelabel_position (Node node)
{
/** ----------------- von fb auskommentiert   Anfang ----------------- **
	struct	pr_subregion label_bound, first_line_bound;
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	struct	pm_subregion label_bound, first_line_bound;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	Rect	r;
	
/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	/** neue lokale variablen **/
	int direction,ascent,descent;
	XCharStruct overall_return;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	if (node->label.text_to_draw == NULL)
		return;
	
	label_bound = compute_lines_subregion_size (node->label.text_to_draw,
		node->label.font);

	if (node->label.text_to_draw [0] != NULL) {
		
#ifdef XVIEW_COMMENT
     XView CONVERSION - Use pf_textbound instead, remember to extern it
#endif
/** ----------------- von fb auskommentiert   Anfang ----------------- **
pf_textbound (&first_line_bound,
			strlen(node->label.text_to_draw[0]),
			node->label.font->font,
			node->label.text_to_draw[0]);
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
       XTextExtents((XFontStruct*)xv_get(node->label.font->xvFont,FONT_INFO),
		    node->label.text_to_draw[0],
		    strlen(node->label.text_to_draw[0]),
		    &direction,
		    &ascent,
		    &descent,
		    &overall_return);
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

/** ----------------- von fb modifiziert      Anfang ----------------- **/
     first_line_bound.pos.x = 0;
     first_line_bound.pos.y = -ascent;
     first_line_bound.size.x = overall_return.width;
     first_line_bound.size.y = ascent+descent;
/** ----------------- von fb modifiziert       Ende ------------------ **/

	} else {
		first_line_bound.pos.x = 0;
		first_line_bound.pos.y = 0;
		first_line_bound.size.x = 0;
		first_line_bound.size.y = 0;
	}
	
	
	/* ... dann passen wir diese box in den Knoten ein ...		*/
	
	switch (node->label.placement) {
	    case NODELABEL_MIDDLE :
		rect_construct (&r, node_left(node) + (node_width(node))/2  - label_bound.size.x /2,
		                    node_top(node)  + (node_height(node))/2 - label_bound.size.y /2,
		                    label_bound.size.x,
		                    label_bound.size.y);
		break;
	    case NODELABEL_UPPERLEFT :
		rect_construct (&r, node_left(node) + NODELABEL_GAP,
		                    node_top(node)  + NODELABEL_GAP,
		                    label_bound.size.x,
		                    label_bound.size.y);
		break;
	    case NODELABEL_UPPERRIGHT :
		rect_construct (&r, node_left(node) + node_width(node) - NODELABEL_GAP - label_bound.size.x,
		                    node_top(node) + NODELABEL_GAP,
		                    label_bound.size.x,
		                    label_bound.size.y);
		break;
	    case NODELABEL_LOWERLEFT :
		rect_construct (&r, node_left(node) + NODELABEL_GAP,
		                    node_top(node) + node_height(node) - NODELABEL_GAP - label_bound.size.y,
		                    label_bound.size.x,
		                    label_bound.size.y);
		break;
	    case NODELABEL_LOWERRIGHT :
		rect_construct (&r, node_left(node) + node_width(node) - NODELABEL_GAP - label_bound.size.x,
		                    node_top(node) + node_height(node) - NODELABEL_GAP - label_bound.size.y,
		                    label_bound.size.x,
		                    label_bound.size.y);
		break;
	    default: break;
	}
	
	/* ... und schon setzen wir noch die neue Boundingbox ein	*/
	
	node->label.box = r;	
	node->label.x = rect_left(&(node->label.box)) + first_line_bound.pos.x;
	node->label.y = rect_top (&(node->label.box)) - first_line_bound.pos.y;
	
	node->full_box = rect_bounding (&(node->box), &(node->label.box));
}



void	adjust_edgelabel_position (Edge edge)
{
/** ----------------- von fb auskommentiert   Anfang ----------------- **
	struct		pr_subregion label_bound;
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

	Edgeline	el;
	int		n, i;

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	/** neue lokale variablen **/
	int direction,ascent,descent;
	XCharStruct overall_return;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	if (edge->label.text_to_draw == NULL)
		return;
	
	
#ifdef XVIEW_COMMENT
     XView CONVERSION - Use pf_textbound instead, remember to extern it
#endif
/** ----------------- von fb auskommentiert   Anfang ----------------- **
pf_textbound (&label_bound,
	              strlen(*(edge->label.text_to_draw)),
	              edge->label.font->font,
	              *(edge->label.text_to_draw));
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
       XTextExtents((XFontStruct*)xv_get(edge->label.font->xvFont,FONT_INFO),
		    *(edge->label.text_to_draw),
		    strlen(*(edge->label.text_to_draw)),
		    &direction,
		    &ascent,
		    &descent,
		    &overall_return);
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/
	              
	/* determine the middle part of edge->line	*/
	if (!is_single_edgeline(edge->line)) {
		n = 0;
		for_edgeline (edge->line, el) {
			n ++;
		} end_for_edgeline (edge->line, el);
	
		el = edge->line;
		for (i=0; i<n/2-1; i++) {
			el = el->suc;
		}
	} else {
		el = edge->line;
	}
					
	edge->label.after_el = el;


/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	/** auf Xview und xlib angepasst **/
	rect_construct (&edge->label.box,
		(el->x + el->suc->x) / 2 - overall_return.width / 2,
		(el->y + el->suc->y) / 2 - (ascent+descent) / 2,
		minimum ( overall_return.width, current_edgelabel_width),
		minimum ((ascent+descent), current_edgelabel_height));

	edge->label.x = rect_left(&(edge->label.box)) + 0;/** war: label_bound.pos.x;**/
	edge->label.y = rect_top (&(edge->label.box)) + ascent; /** war: label_bound.pos.y;**/
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

/** ----------------- von fb auskommentiert   Anfang ----------------- **
	rect_construct (&edge->label.box,
		(el->x + el->suc->x) / 2 - label_bound.size.x / 2,
		(el->y + el->suc->y) / 2 - label_bound.size.y / 2,
		minimum (label_bound.size.x, current_edgelabel_width),
		minimum (label_bound.size.y, current_edgelabel_height));

	edge->label.x = rect_left(&(edge->label.box)) + label_bound.pos.x;
	edge->label.y = rect_top (&(edge->label.box)) - label_bound.pos.y;
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

}



void	adjust_nodelabel_text_to_draw (Node node)
{
	int	line, n_lines;
	char	**text;
	
	int	label_is_small_enough, length, i;
	int	line_max_height   = 0;

/** ----------------- von fb auskommentiert   Anfang ----------------- **
        struct  pr_subregion    label_bound;
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
       	/** neue lokale variablen **/
	int direction,ascent,descent;
	XCharStruct overall_return;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	if (node->label.text == NULL) {
		node->label.box = rect_null;
		if (node->label.text_to_draw != NULL) {
			myfree (node->label.text_to_draw);
		}
		node->label.text_to_draw = NULL;
		return;
	} else {
		if (node->label.text_to_draw != NULL) {
			myfree (node->label.text_to_draw);
		}
	}
	
	text = (char **)split_string (node->label.text);

	n_lines = 0;
	line_max_height = 0;
	for (line=0; text[line] != NULL; line++) {
/** ----------------- von fb auskommentiert   Anfang ----------------- **
		pf_textbound (&label_bound, strlen(text[line]),
			node->label.font->font,
			text[line]);
		line_max_height = maximum (line_max_height, label_bound.size.y);
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	  XTextExtents((XFontStruct*)xv_get(node->label.font->xvFont,FONT_INFO),text[line],strlen(text[line]),
		       &direction,
		       &ascent,
		       &descent,
		       &overall_return);
	  line_max_height = maximum (line_max_height, ascent+descent);
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

		n_lines ++;
	}
	
	for (line=0; text[line] != NULL; line++) {
		
		if ((line+1) * line_max_height > node_height (node)) {
		
			myfree (text[line]);
			text[line] = NULL;
			n_lines --;
		
		} else {
		
			length = strlen (text[line]);
			
			label_is_small_enough = TRUE;
			for (i=1; i<=length && label_is_small_enough; i++) {
/** ----------------- von fb auskommentiert   Anfang ----------------- **
				pf_textbound (&label_bound, i,
					node->label.font->font,
					text[line]);
				label_is_small_enough = (label_bound.size.x <= node_width(node));
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
			  XTextExtents((XFontStruct*)xv_get(node->label.font->xvFont,FONT_INFO),text[line],i,
				       &direction,
				       &ascent,
				       &descent,
				       &overall_return);
				label_is_small_enough = (overall_return.width <= node_width(node));
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

				if (!label_is_small_enough) {
					i --;
					break;
				}
			}
			(text[line])[i] = '\0';
		}
	}
	node->label.n_lines      = n_lines;
	node->label.line_height  = line_max_height;
	node->label.text_to_draw = text;

}



void	adjust_edgelabel_text_to_draw (Edge edge)
{
	int	label_is_small_enough = TRUE,
		i, length;
	char	*text;
/** ----------------- von fb auskommentiert   Anfang ----------------- **
	struct	pr_subregion	label_bound;
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	/** neue lokale variablen **/
	int direction,ascent,descent;
	XCharStruct overall_return;
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	if (edge->label.text == NULL) {
		edge->label.box = rect_null;
		if (edge->label.text_to_draw != NULL) {
			myfree (edge->label.text_to_draw);
		}
		edge->label.text_to_draw = NULL;
		return;
	} else {
		if (edge->label.text_to_draw != NULL) {
			myfree (edge->label.text_to_draw);
		}
	}
	
	text = remove_control_chars_from_string (edge->label.text);
	length = strlen (text);
	
	for (i=1; i<=length && label_is_small_enough; i++) {
/** ----------------- von fb auskommentiert   Anfang ----------------- **
		pf_textbound (&label_bound, i,
		              edge->label.font->font,
		              text);
		label_is_small_enough =
			(label_bound.size.x <= current_edgelabel_width) &&
			(label_bound.size.y <= current_edgelabel_height);
 ** ----------------- von fb auskommentiert    Ende ------------------ **/

/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
	  XTextExtents((XFontStruct*)xv_get(edge->label.font->xvFont,FONT_INFO),
		       text,i,
		       &direction,
		       &ascent,
		       &descent,
		       &overall_return);
		label_is_small_enough =
			( overall_return.width <= current_edgelabel_width) &&
			( ascent+descent <= current_edgelabel_height);
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

	}
	if (!label_is_small_enough)
		/* Dann ist das letzte Zeichen ungueltig */
		i--;
	
	edge->label.text_to_draw = (char **)mymalloc(sizeof(char *));
	*(edge->label.text_to_draw) = mymalloc(i);
	strncpy (*(edge->label.text_to_draw), text, i-1);
	(*(edge->label.text_to_draw)) [i-1] = '\0';
	myfree (text);
}



/************************************************************************/
/*									*/
/*			PFEIL AN KANTE ANPASSEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	adjust_arrow_to_edge (edge)				*/
/*									*/
/*	Justiert den Pfeil an edge entsprechend edge->arrow.length und	*/
/*	edge->arrow.angle.						*/
/*									*/
/************************************************************************/



void	adjust_arrow_to_edge (Edge edge)
{
	Arrow		arrow;
	Edgeline	el;	/* An diese Kante kommt der Pfeil	*/
	double		m;	/* Steigung der Kante			*/
	int		box_x,box_y,		/* Box um den		*/
			box_width,box_height;	/* zukuenftigen Pfeil	*/
	
	if (edge->line == (Edgeline)NULL) 
		return;
	
	
	arrow.length = edge->arrow.length;
	arrow.angle  = edge->arrow.angle;
	el = edge->line->pre->pre;
	
	/* Als erstes setzen wir die Spitze des Pfeiles ...		*/
	arrow.x1 = el->suc->x;
	arrow.y1 = el->suc->y;
	
	if (!edge->source->graph->directed || edge->arrow.length == 0) {
		/* ... wenn der Graph ungerichted ist, geht es schnell	*/
		box_x = arrow.x1;
		box_y = arrow.y1;
		box_width  = 0;
		box_height = 0;
	} else {
		/* ... wenn er aber gerichted ist ...				*/
		/* ... dann rechnen wir m (das ist die Steigung der Kante) aus	*/
		if ( !(el->x == el->suc->x && el->y == el->suc->y))
			m = atan2 ((double)(el->suc->y - el->y),
			           (double)(el->suc->x - el->x));
		else
			m = 0;
	
		/* ... und schon koennen wir den Pfeil berechnen !		*/
		arrow.x0 = arrow.x1 - (int)((double)arrow.length * cos(m + (double)arrow.angle));
		arrow.y0 = arrow.y1 - (int)((double)arrow.length * sin(m + (double)arrow.angle));
		arrow.x2 = arrow.x1 - (int)((double)arrow.length * cos(m - (double)arrow.angle));
		arrow.y2 = arrow.y1 - (int)((double)arrow.length * sin(m - (double)arrow.angle));
	
		box_x = minimum (minimum (arrow.x0, arrow.x1), arrow.x2);
		box_y = minimum (minimum (arrow.y0, arrow.y1), arrow.y2);
		box_width  = maximum (maximum (arrow.x0, arrow.x1), arrow.x2) - box_x + 1;
		box_height = maximum (maximum (arrow.y0, arrow.y1), arrow.y2) - box_y + 1;
	}
	
	rect_construct (&(arrow.box), box_x,box_y, box_width,box_height);
	
	edge->arrow = arrow;
}



/************************************************************************/
/*									*/
/*		BOX UM DIE KANTE NEU ANPASSEN				*/
/*									*/
/************************************************************************/
/*									*/
/*	void	adjust_edge_box (edge)					*/
/*									*/
/*	Passt edge->box (bounding_box um edge->line, edge->label und	*/
/*	edge->arrow) neu an.						*/
/*									*/
/************************************************************************/


void	adjust_edge_box (Edge edge)
{
	Edgeline	el;
	Rect		r;
	
	if (!box_adjustment_enabled)
		return;
		
	r = rect_bounding (&(edge->arrow.box), &(edge->label.box));
	if ((el = edge->line) != (Edgeline)NULL) do {
		r = rect_bounding (&r, &(el->box));
		el = el->suc;
	} while (el->suc != edge->line);
	
	edge->box = r;
}



/************************************************************************/
/*									*/
/*			KANTE INSGESAMT NEU ANPASSEN			*/
/*									*/
/************************************************************************/
/*									*/
/*	void	adjust_edge_head (edge)					*/
/*	void	adjust_edge_tail (edge)					*/
/*									*/
/*	Diese Prozeduren sollten aufgerufen werden, falls der erste	*/
/*	bzw. zweite Punkt von edge->line geaendert worden ist.		*/
/*	Sie erledigen alles, was in diesem Fall notwendig ist.		*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	adjust_all_edges (node)					*/
/*									*/
/*	Justiert alle Kanten an node (adjust_edge_head in		*/
/*	node->sourcelist, adjust_edge_tail in node->targetlist) neu.	*/
/*									*/
/************************************************************************/



void	adjust_edge_head (Edge edge)
{

	if (is_single_edgeline(edge->line)) {
		adjust_edgeline_to_node (ADJUST_EDGELINE_HEAD_AND_TAIL, edge, empty_node, empty_node);
		adjust_arrow_to_edge (edge);
	} else {
		adjust_edgeline_to_node (ADJUST_EDGELINE_HEAD, edge, empty_node, empty_node);
	}
	adjust_edgelabel_position (edge);
}


void	adjust_edge_tail (Edge edge)
{
	if (is_single_edgeline(edge->line)) {
		adjust_edgeline_to_node (ADJUST_EDGELINE_HEAD_AND_TAIL, edge, empty_node, empty_node);
	} else {
		adjust_edgeline_to_node (ADJUST_EDGELINE_TAIL, edge, empty_node, empty_node);
	}
	adjust_edgelabel_position (edge);
	adjust_arrow_to_edge (edge);
}


void	adjust_all_edges (Node node)
{
	Edge	edge;
	
	for_edge_sourcelist (node, edge)
		adjust_edge_head (edge);
		adjust_edge_box (edge);
	end_for_edge_sourcelist (node, edge);
	for_edge_targetlist (node, edge)
		adjust_edge_tail (edge);
		adjust_edge_box (edge);
	end_for_edge_targetlist (node, edge);
}



/************************************************************************/
/*									*/
/*			RECHTECK UM GRAPHEN NEU ANPASSEN		*/
/*									*/
/*									*/
/************************************************************************/



void	adjust_graph_box (Graph graph)
{
	if (box_adjustment_enabled)
		graph->box = compute_rect_around_graph (graph);
}



/************************************************************************/
/*									*/
/*	RECHTECKE UM OBJEKTE BERECHNEN ERLAUBEN / VERBIETEN		*/
/*									*/
/*									*/
/************************************************************************/



void	adjust_boxes_in_graph (Graph graph)
{
	Node	node;
	Edge	edge;

	for_nodes (graph, node) {
	
		for_edge_sourcelist (node, edge) {
			adjust_edge_box (edge);
		} end_for_edge_sourcelist (node, edge);
		
	} end_for_nodes (graph, node);
	
	adjust_graph_box (graph);
}
