/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

#ifndef STD
#define STD

/*
#define SGRAPH_STANDALONE
*/

#ifdef SGRAPH_STANDALONE
#undef GRAPHED
#endif


#include <assert.h>
#include <stdio.h>
#ifndef __GNUC__
#ifndef MALLOC_HEADER
#define MALLOC_HEADER
#include <malloc.h>
#endif
#else
#include <malloc.h>
#endif

#define	nil	NULL

#ifndef TRUE
#define TRUE	(0==0)
#endif
#ifndef FALSE
#define	FALSE	(0==1)
#endif

#define	true	TRUE
#define	false	FALSE

#ifndef iif
#define	iif(b,e1,e2)	((b) ? (e1) : (e2))
#endif
#ifndef maximum
#define	maximum(x,y)	iif ((x) > (y), (x), (y))
#endif
#ifndef minimum
#define	minimum(x,y)	iif ((x) < (y), (x), (y))
#endif

#ifndef bool
#define	bool	int
#endif
#ifndef Local
#define	Local	static
#endif
#ifndef Global
#define	Global
#endif

typedef	void (*Pointer_to_procedure)();

typedef	struct	attributes {
/*	char	*key; */
	union	{
		int	integer;
		char	*data;
	}
		value;
}
	Attributes;

#define	attr_flags(x)		((x)->attrs.value.integer)
#define	attr_int(x)		((x)->attrs.value.integer)
#define	attr_data(x)		((x)->attrs.value.data)
#define	attr_data_of_type(x,t)	((t)attr_data(x))
#define	set_attr_data(x, v)	(attr_data(x)=(char*)v)

#if FALSE
#define attr_key(x)		((x)->attrs.key)
#endif

typedef	enum {
	ATTR_INTEGER,
	ATTR_DATA
#if FALSE
	/* The following two are unused ... */
	,ATTR_KEY_INTEGER,
	ATTR_KEY_DATA
#endif
}
	Attributes_type;

#define ATTR_FLAGS ATTR_INTEGER

extern	Attributes	make_attr (Attributes_type attr_type, ...);

extern	char	*strsave(char *s);
extern	char	*strnsave(char *s, int len);

#ifndef GRAPHED
#ifndef SGRAPH_STANDALONE
extern	char	*mymalloc (unsigned int size);
extern	char	*mycalloc (unsigned int n, unsigned int size);
extern	void	myfree   (); /* no parameter due to mixed pointer types */

#endif
#endif

#ifdef SGRAPH_STANDALONE
#define mymalloc(s)   malloc(s)
#define mycalloc(n,s) calloc((n),(s))
#define myfree(s)     free(s)
#endif

#endif
