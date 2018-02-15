/* This software is distributed under the Lesser General Public License */
// #include <values.h>
#include <stdlib.h>
#include <math.h>

#include "global.h"
#include "adtgraph.h"
#include "gsearch.h"
#include "geometry.h"
#include "quality.h"


double		Qe, Qx, Qv, Qd, Q;
double		Ae;
long int	Nx, diam;


void quality (void) {

   vertex	u, v, w;
   edge		e, f;
   vector	d;
   scalar	m, n;

   Ae = 0;
   Qe = 0;
   for_all_edges (e) {
      u = this_vertex (e);
      v = that_vertex (e);
      d.x = vi[u].pos.x - vi[v].pos.x;
      d.y = vi[u].pos.y - vi[v].pos.y;
      Qe += d.x * d.x + d.y * d.y;
      Ae += sqrt ((double)(d.x * d.x + d.y * d.y));
   }
   Ae /= number_edges;
   if (Ae == 0) {
      terminate ("Average edge length is zero");
      exit (1);
   }
   Qe = sqrt (Qe / (Ae * Ae * number_edges) - 1);

   Nx = 0;
   Qx = 0;
   m = (long)number_edges * number_edges + number_edges;
   for_all_edges (e)
      m -= vi[this_vertex (e)].degree + vi[that_vertex (e)].degree;
   if (m) {
      for_all_edges (e)
	 for_all_edges (f)
	    if (f == e)
	       break;
	    else
	       Nx += EEintersect (e, f);
      Qx = Nx * 2.0 / m;
      if ((Nx > 0) && (Qx < 0.001))
	 Qx = 0.001;
   }

   Qd = 0;
   for_all_vertices (u) {
      for_all_vertices (v) {
	 d.x = vi[u].pos.x - vi[v].pos.x;
	 d.y = vi[u].pos.y - vi[v].pos.y;
	 if (d.x * d.x + d.y * d.y > Qd)
	    Qd = d.x * d.x + d.y * d.y;
      }
   }
   diam = graph_diameter ();
   Qd = sqrt (Qd) / (Ae * diam);

   Qv = 0;
   for_all_vertices (u) {
      for_all_edges (e) {
	 v = this_vertex (e);
	 w = that_vertex (e);
	 if (v != u && w != u)
	    d = EVdistance (e, u);
	 else
	    if (v != u)
	       d = vi[v].pos;
	    else
	       d = vi[w].pos;
	 d.x -= vi[u].pos.x;
	 d.y -= vi[u].pos.y;
	 n = (long int)(NORM2 ((double)d.x, (double)d.y));
	 n = MAX (n, 1);
	 Qv += 1.0 / n;
      }
   }
   Qv *= Ae / number_vertices / number_edges;

   Q = QV_WEIGHT * Qv + QX_WEIGHT * Qx + QE_WEIGHT * Qe + QD_WEIGHT * Qd;
}
