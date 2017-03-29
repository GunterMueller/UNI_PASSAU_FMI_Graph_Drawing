/* This software is distributed under the Lesser General Public License */
#ifndef QUALITY_H
#define QUALITY_H

/* constants */

#define	QD_WEIGHT	0.10
#define	QE_WEIGHT	1.00
#define	QV_WEIGHT	0.25
#define	QX_WEIGHT	25.0

/* global vars */

extern double	Qe, Qx, Qv, Qd, Q, Ae;
extern long int	Nx, diam;

/* prototypes */

void 	quality (void);

#endif
