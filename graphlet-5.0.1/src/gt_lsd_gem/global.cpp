/* This software is distributed under the Lesser General Public License */
#include <stdio.h>

#include "global.h"


termproc	termprocstack[MAXTERMPROX];
unsigned int	numtermprox = 0L;


void add_termproc (termproc tp) {

   if (tp)
      termprocstack[numtermprox ++] = tp;
}


void terminate (const char *errortext) {

   while (numtermprox --)
      termprocstack[numtermprox]();
   if (errortext)
      fputs (errortext, stderr);
}

