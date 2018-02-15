/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/************************************************************************
 * File    : Sembed.c                                       Version 1.0 *
 * Aufgabe : Datenstruktur fuer Einbettungen und globale Einbettungen	*
 *           von Graphgrammatiken					*
 *									*
 * Autor   : Torsten Bachmann						*
 * Datum   : 19.11.90							*
 ************************************************************************/

#include "std.h"
#include "slist.h"
#include "sgraph.h"
#include "sgragra.h"


Sembed	make_sembed (Sprod prod, Snode node)
           
           
    /*
	prod gibt die Produktion an, zu der die neue Einbettungsregel ge-
	hoert; node gibt einen Knoten aus dem Graphen prod->right der
	rechten Seite der Produktion an.
	Reserviert Speicher fuer den Eintrag einer Einbettung. Eingetragen
	wird dieser in die Produktion-Einbettungsliste, sowie in die Kno-
	ten-Einbettungsliste.
	Im Anschluss an die make_embed Operation zeigt prod->embedding auf
	die neu eingefuege Einbettung. Ein Zeiger auf diese Struktur wird
	zusaetzlich als Funktionsergebnis zurueckgegeben.
	*/
{	Sembed new_emb = (Sembed) malloc(sizeof(struct sembed));
/* Einfuegen in Produktionen-Einbettungsliste */
if (prod->embedding == empty_sembed)
    {	new_emb->pre = new_emb;
    new_emb->suc = new_emb;
    prod->embedding = new_emb;
    }
else
    {	new_emb->pre = prod->embedding;
    new_emb->suc = prod->embedding->suc;
    prod->embedding->suc->pre = new_emb;
    prod->embedding->suc = new_emb;
    prod->embedding = new_emb;
    }

/* Einfuegen in Knoten-Einbettungsliste */
if ((Sembed) node->embedding == empty_sembed)
    {	node->embedding = (char *) new_emb;
    new_emb->npre = new_emb;
    new_emb->nsuc = new_emb;
    }
else
    {	new_emb->npre = (Sembed) node->embedding;
    new_emb->nsuc = ((Sembed) node->embedding)->nsuc;
    ((Sembed) node->embedding)->nsuc->npre = new_emb;
    ((Sembed) node->embedding)->nsuc = new_emb;
    node->embedding = (char *) new_emb;
    }
new_emb->node_right = node;

new_emb->prod = prod;
new_emb->node_embed = NULL;
new_emb->oldedge = NULL;
new_emb->olddir  = S_out;
new_emb->newedge = NULL;
new_emb->newdir  = S_out;
new_emb->attrs   = make_attr(ATTR_DATA, NULL);
return new_emb;
}


void	remove_sembed (Sembed embed)
             
    /*
	Loescht die Einbettungsregel embed aus der Produktionen-Einbettungs-
	liste und aus der Knoten-Einbettungsliste. Speicher, der fuer Attri-
	bute belegt wird, wird NICHT wieder freigegeben.
	*/
{       /* Loeschen aus Produktionen-Einbettungsliste */
    if (embed->prod->embedding == embed)
	{	if (embed->suc == embed)
	    {	embed->prod->embedding = empty_sembed;
	    }
	else
	    {	embed->suc->pre = embed->pre;
	    embed->pre->suc = embed->suc;
	    embed->prod->embedding = embed->suc;
	    }
	}
    else
	{	embed->suc->pre = embed->pre;
	embed->pre->suc = embed->suc;
	}

    /* Loeschen aus Knoten-Einbettungsliste */
    if (((Sembed) embed->node_right->embedding) == embed)
	{	if (embed->nsuc == embed)
	    {	embed->node_right->embedding = (char *) empty_sembed;
	    }
	else
	    {	embed->nsuc->npre = embed->npre;
	    embed->npre->nsuc = embed->nsuc;
	    embed->node_right->embedding = (char *) embed->nsuc;
	    }
	}
    else
	{	embed->nsuc->npre = embed->npre;
	embed->npre->nsuc = embed->nsuc;
	}

#if __SUNPRO_CC == 0x401
    free((char*) embed);
#else
    free(embed);
#endif
}




/*************************** Global Embeddings **************************/




Sglobalembed	make_sglobalembed (Sgragra gragra)
       	       
    /*
	reserves memory for a global embedding. the new embedding is 
	inserted into the global_embeddings list from gragra.
	*/
{	Sglobalembed new_emb = (Sglobalembed) malloc(sizeof(struct sglobalembed));
if (gragra->global_embeddings == empty_sglobalembed)
    {	new_emb->pre = new_emb;
    new_emb->suc = new_emb;
    gragra->global_embeddings = new_emb;
    }
else
    {	new_emb->pre = gragra->global_embeddings;
    new_emb->suc = gragra->global_embeddings->suc;
    gragra->global_embeddings->suc->pre = new_emb;
    gragra->global_embeddings->suc = new_emb;
    gragra->global_embeddings = new_emb;
    }
		
new_emb->gragra  = gragra;

new_emb->node_right = NULL;
new_emb->node_embed = NULL;

new_emb->oldedge = NULL;
new_emb->olddir  = S_out;
		
new_emb->newedge = NULL;
new_emb->newdir  = S_out;
new_emb->attrs   = make_attr(ATTR_DATA, NULL);
return new_emb;
}


void	remove_sglobalembed (Sglobalembed gembed)
                    
    /*
	detaches gembed from gragra->global_embeddings and free's memory.
	*/
{       if (gembed->gragra->global_embeddings == gembed)
    {	if (gembed->suc == gembed)
	{	gembed->gragra->global_embeddings = empty_sglobalembed;
	}
    else
	{	gembed->suc->pre = gembed->pre;
	gembed->pre->suc = gembed->suc;
	gembed->gragra->global_embeddings = gembed->suc;
	}
    }
else
    {	gembed->suc->pre = gembed->pre;
    gembed->pre->suc = gembed->suc;
    }
#if __SUNPRO_CC == 0x401
free((char*) gembed);
#else
free(gembed);
#endif
}






