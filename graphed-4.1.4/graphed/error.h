/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt	*/
#ifndef ERROR_HEADER
#define ERROR_HEADER

extern	void	write_message (char *message);
extern	void	message       (char *format, ...);
extern	void	warning       (char *format, ...);
extern	void	error         (char *format, ...);
/*extern  void	sys_error     (int errno);  */
extern	void	fatal_error   (char *format, ...);
extern	void	die           (void);	/* main.c	*/
extern	void	bypass_messages_to_file (FILE *file);


/* UNIX - Fehlermeldungen	*/

#include <errno.h>

extern	int	sys_nerr;
/* extern	char	*sys_errlist[]; */
extern const char *const sys_errlist[];

#endif
