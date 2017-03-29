#include "misc.h"
#include "graph.h"
#include "type.h"

#include "ps.h"
#include "repaint.h"
#include "nodetypes/nodetypes.h"



void init_black_elliptical_nodetype (void)
{
  System_nodetype black_elliptical;

  black_elliptical = new_system_nodetype();
  black_elliptical->name = "#black_circle";
  black_elliptical->adjust_func = adjust_edgeline_to_elliptical_node;
  black_elliptical->pm_paint_func = pm_paint_black_elliptical_node;
  black_elliptical->ps_paint_func = ps_paint_black_elliptical_node;

  add_system_nodetype (black_elliptical);
}


void		pm_paint_black_elliptical_node(Pixmap pm, int x0, int y0, int w,int h, int op)
{
  XFillArc(display,pm,pixmap_gc,x0,y0,w-1,h-1,0,360*64);
}

void	ps_paint_black_elliptical_node(PsPage *pspage, Node node)
{
  if(!pspage) return;

  ps_paint(pspage, PS_CIRCLE,
	   PS_PRIM_LOCATION_XY,
	   (double)node->box.r_left + (double)node->box.r_width / 2.0,
	   (double)node->box.r_top + (double)node->box.r_height / 2.0,
	   PS_PRIM_RADIUS,
	   (double)node->box.r_width / 2.0,
	   PS_PRIM_SCALING_Y,
	   (double)node->box.r_height / (double)node->box.r_width,
	   PS_PRIM_COLOR,  0.0,
	   PS_PRIM_FILLED, TRUE,
	   NULL);
}	


