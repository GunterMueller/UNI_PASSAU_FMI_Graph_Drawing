/* (C) Universitaet Passau 1986-1994 */
/* Sgraph Source, 1988-1994 by Michael Himsolt */

#ifndef SLIST_HEADER
#define SLIST_HEADER


/* #include <sgraph/std.h> */
#include "std.h"


typedef	struct	slist {
	struct	slist	*pre,   *suc;
	Attributes	attrs;
	char            *key;
}
	*Slist;

/* #include <sgraph/sgraph.h> */
#include "sgraph.h"

#define	empty_slist	((Slist)NULL)
#define slist_key(l)    ((l)->key)

#define	for_slist(list, l) \
	{ if (((l) = (list)) != (Slist)NULL) do {
#define	end_for_slist(list, l) \
	} while (((l) = (l)->suc) != list); }

#define generic_for_slist(list,iterator,type)				\
	{ Slist _generic_for_slist_iterator;				\
	  for_slist ((list), _generic_for_slist_iterator)		\
	  (iterator) = attr_data_of_type (_generic_for_slist_iterator, type); {

#define end_generic_for_slist(list,iterator,type)			\
	} end_for_slist ((list), _generic_for_slist_iterator) }


extern	Slist	new_slist			(Attributes attrs);
extern	Slist	add_immediately_to_slist	(Slist slist, Attributes attrs);
extern	Slist	add_to_slist			(Slist slist, Attributes attrs);
extern	Slist	subtract_immediately_from_slist	(Slist slist, Slist g);
extern	Slist	subtract_from_slist		(Slist slist, Attributes attrs);
extern	Slist	add_slists			(Slist g1, Slist g2);
extern	Slist	add_slists_disjoint		(Slist g1, Slist g2);
extern	Slist	subtract_slists			(Slist g1, Slist g2);
extern	void	free_slist			(Slist slist);
extern	Slist	copy_slist			(Slist slist);



extern	int	slist_contains_exactly_one_element	(Slist slist);
extern	Slist	contains_slist_element 			(Slist slist, Attributes attrs);
extern	int	slist_intersects_slist			(Slist slist1, Slist slist2);
extern	int	size_of_slist				(Slist slist);


extern	Slist	make_slist_of_sgraph		(Sgraph sgraph);
extern	Slist	make_slist_of_sourcelist	(Snode snode);
extern	Slist	make_slist_of_targetlist	(Snode snode);


#endif
