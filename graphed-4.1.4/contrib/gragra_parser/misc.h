#ifndef GRAGRA_MISC_HEADER
#define GRAGRA_MISC_HEADER


#include <stdio.h>
#include <ctype.h>
#include <string.h>
#include "w_memory.h"

#define ERROR(x)	fprintf(stderr,(x))

#define BS_PARSER_GRAPH_SIZE	1

#define MISC_get_grammar_start_symbol() \
	"S"
	
#define MISC_is_terminal( str ) \
	(isalnum((str)[0])&&(!isupper((str)[0])))

#define	EXTERN		extern
#define DEFAULT( x )
#define NO_DEFAULT

#endif

#ifndef TRUE
#define TRUE	(1==1)
#endif

#ifndef FALSE
#define FALSE	!TRUE
#endif

#ifndef NULL
#define NULL	0L
#endif
