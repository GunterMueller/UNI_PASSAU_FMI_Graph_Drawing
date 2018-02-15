/* This software is distributed under the Lesser General Public License */
#ifndef GSEARCH_H
#define GSEARCH_H

#include "adtgraph.h"


/* ADT Queue */

#define	Qsize	(1+VMAX)

typedef	struct {
   vertex	item[Qsize];
   int		top, used;
}  				queue;

#define	newqueue(q)	(q).top=(q).used=0
#define	enqueue(q,v)	if((q).used<Qsize)(q).item[((q).top+(q).used++)%Qsize]=(v)
#define	dequeue(q,v)	if((q).used){(q).used--;(v)=(q).item[(q).top++];(q).top%=Qsize;}
#define	empty(b)	((b).used<=0)

/* ADT Stack */

#define	Ssize	(1+VMAX)

typedef	struct {
   vertex	item[Ssize];
   int		used;
}  				stack;

#define	newstack(s)	(s).used=0
#define	push(s,v)	if((s).used<Ssize)(s).item[(s).used++]=(v)
#define	pop(s,v)	if((s).used)(v)=(s).item[--(s).used]

/* Prototypes */

vertex 	gem_bfs (vertex);
vertex 	gem_dfs (vertex);
/* xfs (root) == root, xfs (0) == next vertex in buffer or 0 (end);
   vi[v].in == 0, if v is not (yet) visited, else == v's search depth;
   use xfs (-root) to keep .in uninitialized, for repeated call */
vertex	graph_center (void);
int	graph_diameter (void);
/* in case there are several components,
   graph_center returns the center of the largest component found whereas
   graph_diameter returns the sum of the component's diameters */

#endif
