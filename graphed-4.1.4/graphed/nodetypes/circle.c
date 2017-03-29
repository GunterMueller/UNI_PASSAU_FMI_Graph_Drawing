#include "misc.h"
#include "graph.h"
#include "type.h"

#include "ps.h"
#include "repaint.h"
#include "nodetypes/nodetypes.h"



void init_elliptical_nodetype (void)
{
  System_nodetype elliptical;

  elliptical = new_system_nodetype();
  elliptical->name = "#circle";
  elliptical->adjust_func = adjust_edgeline_to_elliptical_node;
  elliptical->pm_paint_func = pm_paint_elliptical_node;
  elliptical->ps_paint_func = ps_paint_elliptical_node;

  add_system_nodetype (elliptical);
}


void	adjust_edgeline_to_elliptical_node (Node node, int *x1,int *y1, int x2, int y2)
{
	double	x,y, m, a,b, xx,yy,aa,bb;
	
	a = (double)node_width(node)  / 2.0;	/* Halbachsen der	*/
	b = (double)node_height(node) / 2.0;	/* Ellipse		*/
	x = x2 - node_x(node);	/* (x2,y2) im Koordinatensystem	*/
	y = y2 - node_y(node);	/* des Knotens			*/
	aa = a*a; bb = b*b;
	xx = x*x; yy = y*y;
	
	if ( xx/aa + yy/bb < 1 ) {
		x = 0;
		y = 0;
	} else if (x != 0) {
		m = y/x;
		if (x>0)
			x =   a*b * sqrt ( 1 / (bb + aa * m*m) );
		else
			x = - a*b * sqrt ( 1 / (bb + aa * m*m) );
		y = m * x;
	} else /* x == 0 */ {
		x = 0;
		y = iif (y2 >= node_y(node), b, -b);
	}
	
	*x1 = node_x(node) + x;
	*y1 = node_y(node) + y;
}


void		pm_paint_elliptical_node(Pixmap pm, int x0, int y0, int w, int h, int op)
{
/** ----------------- von fb hinzugefuegt     Anfang ----------------- **/
  XDrawArc(display,pm,pixmap_gc,x0,y0,w-1,h-1,0,360*64);
/** ----------------- von fb hinzugefuegt      Ende ------------------ **/

/** ----------------- von fb auskommentiert   Anfang ----------------- **
	register int	a, b, d, xm,
			xm_plus_x, xm_minus_x,
			ym_plus_y, ym_minus_y,
			a_quadrat, b_quadrat,
			zwei_a_quadrat, zwei_b_quadrat,
			delta_x, delta_y;
	register int	y;
	
	register int	pr_put_value =
			iif (graphed_state.colorscreen == TRUE,
			     PIX_OPCOLOR(op),
			     iif (PIX_OPCOLOR(op) == 0, 0, 1));

	w-- ; h--;
	a = w / 2 ;  b = h / 2 ;
	if ( b==0 ) {
		pr_vector(pr, x0, y0,   x0+w, y0,   op, PIX_OPCOLOR(op));
		pr_vector(pr, x0, y0+h, x0+w, y0+h, op, PIX_OPCOLOR(op));
	} else {
		xm_minus_x = x0 + a ;
		xm_plus_x = xm_minus_x + (w & 1) ;
		ym_plus_y = y0 + h ; ym_minus_y = y0 ;

		a_quadrat = a*a;
		b_quadrat = b*b;
		zwei_a_quadrat = 2 * a_quadrat ;
		zwei_b_quadrat = 2 * b_quadrat ;
		delta_y = a_quadrat * ( 2*b - 1 );
		delta_x = b_quadrat ;

		d = delta_y - 1;**/  /* - 2*a*b */ /**

		y = b;

		do {
			pr_put (pr, xm_plus_x  , ym_plus_y  , pr_put_value);
			pr_put (pr, xm_plus_x  , ym_minus_y , pr_put_value);
			pr_put (pr, xm_minus_x , ym_plus_y  , pr_put_value);
			pr_put (pr, xm_minus_x , ym_minus_y , pr_put_value);
		
			if (d >= 0) {
				d -= delta_x;
				delta_x += zwei_b_quadrat ;
				xm_plus_x++;
				xm_minus_x--;
			}
			if (d < 0) {
				delta_y -= zwei_a_quadrat ;
				d += delta_y ;
				y--;
				ym_plus_y--;
				ym_minus_y++;
			}
		} while (y >= 0);
	}
 ** ----------------- von fb auskommentiert    Ende ------------------ **/


}

void	ps_paint_elliptical_node(PsPage *pspage, Node node)
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
	   NULL);
}	


