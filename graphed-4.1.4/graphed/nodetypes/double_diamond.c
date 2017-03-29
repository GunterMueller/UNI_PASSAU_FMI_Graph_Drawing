#include "misc.h"
#include "graph.h"
#include "type.h"

#include "ps.h"
#include "repaint.h"
#include "nodetypes/nodetypes.h"



void init_double_diamond_nodetype (void)
{
  System_nodetype double_diamond;

  double_diamond = new_system_nodetype();
  double_diamond->name = "#double_diamond";
  double_diamond->adjust_func = adjust_edgeline_to_diamond_node;
  double_diamond->pm_paint_func = pm_paint_double_diamond_node;
  double_diamond->ps_paint_func = ps_paint_double_diamond_node;

  add_system_nodetype (double_diamond);
}


void		pm_paint_double_diamond_node(Pixmap pm, int x, int y, int w, int h, int op)
{
  int x1,y1, x2,y2, x3,y3, x4,y4;
	
  x1 = x;       y1 = y+h/2-1;
  x2 = x+w/2-1; y2 = y;
  x3 = x+w-1;   y3 = y+h/2;
  x4 = x+w/2;   y4 = y+h-1;

  XDrawLine(display, pm, pixmap_gc,
	    x1, y1,
	    x2, y2);
  XDrawLine(display, pm, pixmap_gc,
	    x2, y2,
	    x3, y3);
  XDrawLine(display, pm, pixmap_gc,
	    x3, y3,
	    x4, y4);
  XDrawLine(display, pm, pixmap_gc,
	    x4, y4,
	    x1, y1);

  if (w > 2*DOUBLE_OFFSET && h > 2*DOUBLE_OFFSET) {
    XDrawLine(display, pm, pixmap_gc,
	      x1+DOUBLE_OFFSET, y1,
	      x2, y2+DOUBLE_OFFSET);
    XDrawLine(display, pm, pixmap_gc,
	      x2, y2+DOUBLE_OFFSET,
	      x3-DOUBLE_OFFSET, y3);
    XDrawLine(display, pm, pixmap_gc,
	      x3-DOUBLE_OFFSET, y3,
	      x4, y4-DOUBLE_OFFSET);
    XDrawLine(display, pm, pixmap_gc,
	      x4, y4-DOUBLE_OFFSET,
	      x1+DOUBLE_OFFSET, y1);
  }
}


void	ps_paint_double_diamond_node(PsPage *pspage, Node node)
{
  if(!pspage) return;
  
  ps_paint(pspage, PS_POLYGON,
	   PS_PRIM_POINTS, 4,

	   (double)node->box.r_left,
	   (double)node->box.r_top + (double)node->box.r_height / 2.0,

	   (double)node->box.r_left + (double)node->box.r_width / 2.0,
	   (double)node->box.r_top,

	   (double)node->box.r_left + (double)node->box.r_width,
	   (double)node->box.r_top + (double)node->box.r_height / 2.0,

	   (double)node->box.r_left + (double)node->box.r_width / 2.0,
	   (double)node->box.r_top + (double)node->box.r_height,
	   NULL);
}


