/* This software is distributed under the Lesser General Public License */
#include "geometry.h"


/* warning: optimized code */


vector EVdistance (const edge e, const vertex v) {

   vector	a = vi[this_vertex (e)].pos;
   vector	b = vi[that_vertex (e)].pos;
   vector	c = vi[v].pos;
   scalar	m, n;

   b.x -= a.x; b.y -= a.y; /* b' = b - a */
   m = b.x * (c.x - a.x) + b.y * (c.y - a.y); /* m = <b'|c-a> = <b-a|c-a> */
   n = b.x * b.x + b.y * b.y; /* n = |b'|^2 = |b-a|^2 */
   if (m < 0) m = 0;
   if (m > n) m = n = 1;
   if (m >> 17)	{  	/* prevent integer overflow */
      n /= m >> 16;
      m /= m >> 16;
   }
   a.x += b.x * m / n;	/* a' = m/n b' = a + m/n (b-a) */
   a.y += b.y * m / n;
   return a;
}


int EEintersect (const edge e, const edge f) {

   vector	p1, p2, p3, p4;
   scalar 	a, b;

   if (e == f || -e == f)
      return 1;

   /* bounding box quick rejection test */

   p1.x = vi[this_vertex (e)].pos.x;
   p2.x = vi[that_vertex (e)].pos.x;
   p3.x = vi[this_vertex (f)].pos.x;
   p4.x = vi[that_vertex (f)].pos.x;
   if (p1.x <= p2.x) {
      if (p3.x <= p4.x) {
	 if (p1.x >= p4.x || p2.x <= p3.x) return 0;
      }
      else
	 if (p1.x >= p3.x || p2.x <= p4.x) return 0;
   }
   else {
      if (p3.x <= p4.x) {
	 if (p2.x >= p4.x || p1.x <= p3.x) return 0;
      }
      else
	 if (p2.x >= p3.x || p1.x <= p4.x) return 0;
   }
   p1.y = vi[this_vertex (e)].pos.y;
   p2.y = vi[that_vertex (e)].pos.y;
   p3.y = vi[this_vertex (f)].pos.y;
   p4.y = vi[that_vertex (f)].pos.y;
   if (p1.y <= p2.y) {
      if (p3.y <= p4.y) {
	 if (p1.y >= p4.y || p2.y <= p3.y) return 0;
      }
      else
	 if (p1.y >= p3.y || p2.y <= p4.y) return 0;
   }
   else {
      if (p3.y <= p4.y) {
	 if (p2.y >= p4.y || p1.y <= p3.y) return 0;
      }
      else
	 if (p2.y >= p3.y || p1.y <= p4.y) return 0;
   }

   /* a = (p3-p1) x (p2-p1),  b = (p4-p1) x (p2-p1) */
   /* where  v x w = v1w2 - v2w1 = det(v,w) = - w x v */

   p2.x -= p1.x; p2.y -= p1.y;
   p3.x -= p1.x; p3.y -= p1.y;
   p4.x -= p1.x; p4.y -= p1.y;
   a = p3.x * p2.y - p3.y * p2.x;
   b = p4.x * p2.y - p4.y * p2.x;
   if (a == 0 && b == 0)
      return 1;
   if ((a >= 0 && b >= 0) || (a <= 0 && b <= 0))
      return 0;

   /* a = (p1-p3) x (p4-p3),  b = (p2-p3) x (p4-p3) */

   p4.x -= p3.x; p4.y -= p3.y;
   p2.x -= p3.x; p2.y -= p3.y;
   a = p3.y * p4.x - p3.x * p4.y;
   b = p2.x * p4.y - p2.y * p4.x;
   if ((a >= 0 && b >= 0) || (a <= 0 && b <= 0))
      return 0;
   return 1;
}
