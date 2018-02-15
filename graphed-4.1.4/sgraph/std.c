/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

#include <string.h>
#include "std.h"
#include "sgraph.h"


char	*strnsave (char *s, int len)
{
	char	*saved_s;
	
	if (s != NULL) {
		saved_s = (char *)malloc(len+1);
		strncpy (saved_s, s, len);
		saved_s[len] = '\0';
	} else {
		saved_s = NULL;
	}

	return saved_s;
}


char	*strsave (char *s)
{
	if (s != NULL) {
		return strnsave (s, strlen(s));
	} else {
		return NULL;
	}
}


