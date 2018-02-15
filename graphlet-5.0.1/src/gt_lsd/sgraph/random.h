/* This software is distributed under the Lesser General Public License */
#ifndef __SGRAPH_RANDOM_H__
#define __SGRAPH_RANDOM_H__

#include <stdlib.h>

#ifndef _BSD_SOURCE
#define random() (long)rand()
#define srandom(seed) srand(seed)
#endif

#endif
