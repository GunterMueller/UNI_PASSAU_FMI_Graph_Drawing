/* This software is distributed under the Lesser General Public License */
#ifndef ADTGRAPH_H
#define ADTGRAPH_H

#include <sgraph/sgraph.h>

#define	VMAX		400     /* maximum number of vertices */
#define	EMAX		3000	/* maximum number of edges */

/* graph handling macros */

#define	for_all_vertices(v) 		for((v)=1;(v)<=number_vertices;(v)++)
#define	for_all_edges(e) 		for((e)=1;(e)<=number_edges;(e)++)
#define for_all_incident_edges(v,e)	for((e)=FIRST[v];(e);(e)=NEXT[e])
#define	this_vertex(e)			ANODE[-(e)]
#define	that_vertex(e)			ANODE[e]

/* typedefs */

typedef	short int		vertex; /* vertices are enumerated from 1..VMAX */
typedef	short int		edge; /* edges are 1..EMAX or -1..-EMAX (reverse) */
typedef	long int		scalar; /* integer arithmetics */
typedef	struct { scalar x, y; }	vector; /* two-dimensional vectors */
typedef struct {
   Snode snode;
   vector	pos; /* current position */
   short int	mass; /* mass/weight */
   scalar	heat; /* current temperature */
   vector	imp; /* last impulse */
   short int	dir; /* direction gauge, for detection of rotations */
   short int	degree; /* number of incident edges */
   short int	in; /* used as mark or counter in bfs */
   short int	x, y;	/* scaled coordinates */
}				vertexinfo; /* data mapped to each vertex */

/* global vars */

extern int		number_vertices, number_edges; /* |V|, |E| */
extern vertex		*ANODE; /* ANODE[e], ANODE[-e] are e's incident vertices */
extern edge		*FIRST; /* FIRST[v] is an incident edge of v */
extern edge		*NEXT; /* NEXT[e], NEXT[-e] are adjacent edges of e */
extern vertexinfo	*vi; /* vi[v] maps v to its associated data */

/* prototypes */

void 	gem_init_graph (void); /* allocates memory for link structures */
void 	exit_graph (void); /* deallocates memory */
void 	create_gem_graph (void); /* initializes graph structures */
void 	insert_vertices (unsigned long); /* creates n new vertices */
void 	insert_edge (const vertex, const vertex); /* creates new edge */

#endif
