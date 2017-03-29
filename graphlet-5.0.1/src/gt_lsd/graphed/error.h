/* This software is distributed under the Lesser General Public License */
/* Dummy-Header for LSD by Dirk Heider */

#ifndef _LSD

/*
if this dummy-header is included and _LSD is NOT defined,
then create an error to prevent illegal usage:
*/

DANGER! Including LSD-header without defining _LSD!

#endif

/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt */
#ifndef ERROR_HEADER
#define ERROR_HEADER

extern  void    write_message (char *message);
extern  void    message       (char *format, ...);
extern  void    warning       (char *format, ...);
extern  void    error         (char *format, ...);
extern  void    sys_error     (int errno); 
extern  void    fatal_error   (char *format, ...);
extern  void    die           (void);   /* main.c       */
extern  void    bypass_messages_to_file (FILE *file);


/* UNIX - Fehlermeldungen       */

#include <errno.h>

/*
  commented out, Michael Forster, 30.01.99:
  
  These Declarations give problems with glibc2 / libc6.
  I think, they are not needed with graphlet. I did not find any references
  to them.

extern  int     sys_nerr;
extern  char    *sys_errlist[];
*/

#endif
