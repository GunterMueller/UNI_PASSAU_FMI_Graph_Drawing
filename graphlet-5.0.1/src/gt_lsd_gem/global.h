/* This software is distributed under the Lesser General Public License */
#ifndef GLOBAL_H
#define GLOBAL_H

/* internal parameters */

extern int	ELEN;

#define	ELENSQR		(ELEN*ELEN)
#define	MAXATTRACT	1048576L

/* termination procedures */

#define	MAXTERMPROX	10

/* auxiliary function macros */

#define	MIN(x,y)	((x)<(y)?(x):(y))
#define	MAX(x,y)	((x)>(y)?(x):(y))
#define	ABS(x)		((x)<0?-(x):(x))
#define	NORM2(x,y)	sqrt((x)*(x)+(y)*(y))

/* typedefs */

typedef	void (*termproc)(void);

/* prototypes */

void add_termproc (termproc);
void terminate (const char *);

#endif
