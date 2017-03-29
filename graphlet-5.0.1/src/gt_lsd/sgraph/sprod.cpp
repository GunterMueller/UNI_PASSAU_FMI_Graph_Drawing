/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/************************************************************************
 * File    : Sprod.c                                        Version 1.0 *
 * Aufgabe : Datenstruktur fuer Produktionen von Graphgrammatiken	*
 *									*
 * Autor   : Torsten Bachmann						*
 * Datum   : 19.11.90							*
 ************************************************************************/

#include <string.h>	/* for strdup */

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "sgragra.h"


Sprod	make_sprod (Sgragra gragra)
               
    /*
	Reserviert Speicher fuer eine neue Produktion in der Graphgramatik
	gragra. Die neue Produktion wird am Anfang der Produktionsliste
	eingefuegt, und kann ueber gragra->productions angesprochen werden.
	Werte fuer die Produktion muessen nach dem Einfuegen eingetragen
	werden.
	*/
{	Sprod new_prod = (Sprod) malloc(sizeof(struct sprod));
if (gragra->productions == empty_sprod)
    {	new_prod->pre = new_prod;
    new_prod->suc = new_prod;
    gragra->productions = new_prod;
    }
else
    {	new_prod->pre = gragra->productions;
    new_prod->suc = gragra->productions->suc;
    gragra->productions->suc->pre = new_prod;
    gragra->productions->suc = new_prod;
    gragra->productions = new_prod;
    }
new_prod->gragra = gragra;
new_prod->left = NULL;
new_prod->right = empty_graph;
new_prod->embedding = empty_sembed;
new_prod->label = NULL;
new_prod->attrs = make_attr(ATTR_DATA, NULL);
return new_prod;
}

void	remove_sprod (Sprod prod)
           
    /*
	Entfernt die Produktion prod aus der Liste der Produktionen. Es
	wird auch der Speicher fuer die Einbettungsregeln und das label
	freigeben. Speicher fuer Attribute wird NICHT wieder freigegeben.
	*/
{       
    while (prod->embedding != empty_sembed)
	remove_sembed ( prod->embedding );
    if (prod->right != empty_graph)
	remove_graph ( prod->right );
    if (prod->gragra->productions == prod)
	{	if (prod->suc == prod)
	    {	prod->gragra->productions = empty_sprod;
	    }
	else
	    {	prod->suc->pre = prod->pre;
	    prod->pre->suc = prod->suc;
	    prod->gragra->productions = prod->suc;
	    }
	}
    else
	{	prod->suc->pre = prod->pre;
	prod->pre->suc = prod->suc;
	}
    if (prod->label != NULL)
	free(prod->label);
#if __SUNPRO_CC == 0x401
    free((char*) prod);
#else
    free(prod);
#endif
}



void	set_sprodlabel (Sprod prod, char *label)
{       
    if (prod != empty_sprod)
	{
	    if (prod->label != NULL)
		{
		    free(prod->label);
		    prod->label = NULL;
		}
	    if (label != NULL)
		prod->label = strdup(label);
	    prod->label = strdup(label);
	}
}
