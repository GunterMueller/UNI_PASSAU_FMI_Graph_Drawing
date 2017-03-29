#include "graph.h"
#include "misc.h"
#include "type.h"
#include "adjust.h"

#include "ps.h"
#include "repaint.h"
#include "nodetypes/nodetypes.h"



void init_diamond_nodetype (void)
{
  System_nodetype diamond;

  diamond = new_system_nodetype();
  diamond->name = "#diamond";
  diamond->adjust_func = adjust_edgeline_to_diamond_node;
  diamond->pm_paint_func = pm_paint_diamond_node;
  diamond->ps_paint_func = ps_paint_diamond_node;

  add_system_nodetype (diamond);
}


void	adjust_edgeline_to_diamond_node (Node node, int *x1, int *y1, int x2, int y2)
{
  int dx, dy;
  int n_x = node_x (node),
      n_y = node_y (node),
      n_width  = node_width  (node),
      n_height = node_height (node),
      n_left = node_left (node),
      n_top  = node_top  (node);
  int x[4],               /* The diamond's endpoints	*/
      y[4];
  int intersect_x;        /* The intersection point	*/
  int intersect_y;
  int intersects = FALSE; /* Do they really intersect ?	*/
	
	
  x[0] = n_left;		y[0] = n_top + n_height/2 -1;
  x[1] = n_left + n_width/2 -1;	y[1] = n_top;
  x[2] = n_left + n_width   -1;	y[2] = n_top + n_height/2;
  x[3] = n_left + n_width/2;	y[3] = n_top + n_height   -1;

  dx = x2 - n_x; /* Ignore any value in *x1 and *x2	*/
  dy = y2 - n_y;
	
  if (dx < 0) {
    if (dy < 0) {
      /* IV.  Quadrant	*/
      intersects = line_line_intersection (x[0],y[0],
					   x[1],y[1],
					   n_x,n_y, x2,y2,
					   &intersect_x, &intersect_y);
    } else			/* dy >= 0 */ {
      /* III. Quadrant	*/
      intersects = line_line_intersection (x[3],y[3],
					   x[0],y[0],
					   n_x,n_y,
					   x2,y2,
					   &intersect_x, &intersect_y);
    }
  } else			/* dx >= 0 */ {
    if (dy < 0) {
      /* I.   Quadrant	*/
      intersects = line_line_intersection (x[1],y[1],
					   x[2],y[2],
					   n_x,n_y,
					   x2,y2,
					   &intersect_x, &intersect_y);
    } else			/* dy >= 0 */ {
      /* II.   Quadrant	*/
      intersects = line_line_intersection (x[2],y[2],
					   x[3],y[3],
					   n_x,n_y,
					   x2,y2,
					   &intersect_x, &intersect_y);
    }
  }
	
  if (intersects) {
    *x1 = intersect_x;
    *y1 = intersect_y;
  } else {
    *x1 = n_x;
    *y1 = n_y;
  }
}


void		pm_paint_diamond_node(Pixmap pm, int x, int y, int w, int h, int op)
{
  int x1,y1, x2,y2, x3,y3, x4,y4;
	
  x1 = x;       y1 = y+h/2-1;
  x2 = x+w/2-1; y2 = y;
  x3 = x+w-1;   y3 = y+h/2;
  x4 = x+w/2;   y4 = y+h-1;

  XDrawLine(display,pm,pixmap_gc,x1,y1,x2,y2);
  XDrawLine(display,pm,pixmap_gc,x2,y2,x3,y3);
  XDrawLine(display,pm,pixmap_gc,x3,y3,x4,y4);
  XDrawLine(display,pm,pixmap_gc,x4,y4,x1,y1);
}


void	ps_paint_diamond_node(PsPage *pspage, Node node)
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


