/* This software is distributed under the Lesser General Public License */
#ifndef EMBEDDER_H
#define EMBEDDER_H

#include "adtgraph.h"

/* parameter defaults */

#define	IMAXTEMPDEF	1.0
#define	ISTARTTEMPDEF	0.3
#define	IFINALTEMPDEF	0.05
#define	IMAXITERDEF	10
#define	IGRAVITYDEF	0.05
#define	IOSCILLATIONDEF	0.4
#define	IROTATIONDEF	0.5
#define	ISHAKEDEF	0.2
#define	AMAXTEMPDEF	1.5
#define	ASTARTTEMPDEF	1.0
#define	AFINALTEMPDEF	0.02
#define	AMAXITERDEF	3
#define	AGRAVITYDEF	0.1
#define	AOSCILLATIONDEF	0.4
#define	AROTATIONDEF	0.9
#define	ASHAKEDEF	0.3
#define	OMAXTEMPDEF	0.25
#define	OSTARTTEMPDEF	0.05
#define	OFINALTEMPDEF	0.02
#define	OMAXITERDEF	3
#define	OGRAVITYDEF	0.1
#define	OOSCILLATIONDEF	0.4
#define	OROTATIONDEF	0.9
#define	OSHAKEDEF	0.3

/* global vars */

extern unsigned long 	iteration;
extern scalar		temperature;

extern float		i_maxtemp, a_maxtemp, o_maxtemp,
			i_starttemp, a_starttemp, o_starttemp,
			i_finaltemp, a_finaltemp, o_finaltemp,
			i_maxiter, a_maxiter, o_maxiter,
			i_gravity, i_oscillation, i_rotation, i_shake,
			a_gravity, a_oscillation, a_rotation, a_shake,
			o_gravity, o_oscillation, o_rotation, o_shake;


/* prototypes */

vertex 	gem_select (void);
void	vertexdata_init (const float);
void 	displace (const vertex, vector);
vector 	i_impulse (const vertex);
void 	insert (void);
vector 	a_impulse (const vertex);
void	a_round (void);
void 	arrange (int (*intrrpt) (void));
vector 	o_impulse (const vertex);
void	o_round (void);
void 	optimize (int (*intrrpt) (void));
void 	randomize_graph (void);

#endif
