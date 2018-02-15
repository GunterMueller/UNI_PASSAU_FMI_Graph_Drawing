/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/************************************************************************
 * File    : Sgragra.c                                      Version 1.0 *
 * Aufgabe : Datenstruktur fuer Graphgrammatiken			*
 *									*
 * Autor   : Torsten Bachmann						*
 * Datum   : 17.12.90							*
 ************************************************************************/

#include <string.h>		/* for strdup */

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "sgragra.h"

Sgragra	make_sgragra     (void)
    /*
	Reserviert Speicher fuer den Kopf einer Graphgrammatik.
	*/
{	Sgragra gg = (Sgragra) malloc (sizeof(struct sgragra));
gg->my_class		= S_UNDEFINED;
gg->tv = gg->te		= empty_slist;
gg->nv = gg->ne		= empty_slist;
gg->productions		= empty_sprod;
gg->global_embeddings	= empty_sglobalembed;
gg->startnode		= empty_node;
gg->label		= NULL;
gg->attrs		= make_attr(ATTR_DATA, NULL);
return gg;
}


void	remove_sgragra   (Sgragra gragra)
               
    /*
	Loescht saemtlichen durch die Graphgrammatik reservierten Speicher.
	Dabei wird auch der Speicher saemtlicher Produktionen, Alphabete und
	des Labels freigegeben. Attribute bleiben unberuecksichtigt.
	gragra muss beim Aufruf != NULL sein.
	*/
{       
    while (gragra->productions != empty_sprod)
	remove_sprod  ( gragra->productions );
    if (gragra->startnode != empty_node)
	remove_node  ( gragra->startnode );
    S_alphabet_remove(gragra->tv);
    S_alphabet_remove(gragra->te);
    S_alphabet_remove(gragra->nv);
    S_alphabet_remove(gragra->ne);
    if (gragra->label != NULL)
	free(gragra->label);
		
#if __SUNPRO_CC == 0x401
    free((char*) gragra);
#else
    free(gragra);
#endif
}


void	set_sgragralabel (Sgragra gragra, char *label)
{       
    if (gragra != empty_sgragra)
	{
	    if (gragra->label != NULL)
		{
		    free(gragra->label);
		    gragra->label = NULL;
		}
	    if (label != NULL)
		gragra->label = strdup(label);
	}
}
