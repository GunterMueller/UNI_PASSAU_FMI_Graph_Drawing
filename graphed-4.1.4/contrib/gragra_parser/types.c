/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Etypes 								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Ftypes 								*/
/*										*/
/*	MODUL: types.c								*/
/*										*/
/*	FUNKTION: Prozeduren und Funktionen zur Erzeugung und Freigabe der	*/
/*		  zentralen Datenstrukturen des Parsers.			*/
/*										*/
/********************************************************************************/

/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/

#include "misc.h"
#include "types.h"

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*-->@	-Ptypes

Parsing_element PE_new_parsing_element		(void);
void		PE_dispose_parsing_element	(Parsing_element *pe);
void		PE_dispose_all_parsing_elements (PE_list *list);
void		PE_insert_parsing_element	(PE_list *list, Parsing_element pe);
void		PE_remove_parsing_element	(PE_list *list, Parsing_element pe);

void		PE_add_to_PE_set		(PE_set *pset, Parsing_element pe);
void		PE_delete_PE_set		(PE_set *pset);

void		PE_set_number_geneator		(int new_value);

PE_edge 	PE_new_edge			(void);
void		PE_dispose_edge 		(PE_edge *edge);
void		PE_dispose_both_edges		(PE_edge *edge);
void		PE_dispose_all_edges		(Parsing_element pe);
void		PE_insert_edge			(Parsing_element pe, PE_edge edge);
void		PE_remove_edge			(Parsing_element pe, PE_edge edge);
void		PE_remove_both_edges		(PE_edge edge);
void		PE_add_edge_to_edgelist 	(PE_edgelist *list, PE_edge edge)
void		PE_delete_edgelist		(PE_edgelist *list)
PE_embedge_choice PE_new_embedge_choice 	(void);
void		PE_insert_embedge_choice	(PE_embedge_choice *list, elem)
void		PE_remove_embedge_choice	(PE_embedge_choice *list )
void		PE_delete_all_embedge_choices	(PE_embedge_choice *list)

edge_direction	PE_inverse_dir			(edge_direction dir);

PE_embedding	PE_new_embedding		(void);
void		PE_dispose_embedding		(PE_embedding *emb);
void		PE_dispose_all_embeddings	(Parsing_element pe);
void		PE_insert_embedding		(Parsing_element pe, PE_embedding emb);
void		PE_remove_embedding		(Parsing_element pe, PE_embedding emb);

PE_production	PE_new_production		(void);
void		PE_dispose_production		(PE_production *prod);
void		PE_dispose_all_productions	(PE_grammar *gram);
void		PE_insert_production		(PE_grammar *gram, PE_production prod);
void		PE_remove_production		(PE_grammar *gram, PE_production prod);

**/


static	int number_generator = 0;

#define PE_get_number() \
		(++number_generator)

/********************************************************************************/
/*										*/
/*-->	PE_inverse_dir								*/
/*										*/
/*	PARAMETER:	edge_direction	dir					*/
/*										*/
/*	ZURUECK:	edge_direction						*/
/*										*/
/*	AUFGABE:	liefere die zu 'dir' inverse Kantenrichtung		*/
/*										*/
/*	ERKLAERUNG:	Abbildung wie folgt:					*/
/*			IN -> OUT,  OUT -> IN, sonstige -> sonstige		*/
/*										*/
/********************************************************************************/

edge_direction	PE_inverse_dir(edge_direction dir)
{
	switch( dir ) {
		case IN:	return OUT;
		case OUT:	return IN;
		default:	return dir;
	}
}

/********************************************************************************/
/*										*/
/*-->@	PE_reset_number_generator						*/
/*-->@	PE_set_number_generator 						*/
/*	PE_set_number_generator( new_value ) ,	PE_reset_number_generator()	*/
/*										*/
/*	PARAMETER:	int	new_value					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Setze automatischen Nummerngenerator auf neuen		*/
/*			Startwert.						*/
/*										*/
/*	BESONDERES:	(#define) PE_reset_number_generator() setzt neuen	*/
/*			Startwert auf 0.					*/
/*										*/
/********************************************************************************/

void	PE_set_number_generator(int new_value)
{
	number_generator = new_value;
}

/********************************************************************************/
/*										*/
/*-->	PE_new_parsing_element							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	Parsing_element, falls Speicheranforderung erfolgreich, */
/*			NULL sonst						*/
/*										*/
/*	AUFGABE:	Speicheranforderung eines Parsingelements,		*/
/*			im Erfolgsfall primitive Initialisierung.		*/
/*										*/
/********************************************************************************/

Parsing_element PE_new_parsing_element(void)
{
	Parsing_element tmp;

	tmp = (Parsing_element) w_malloc( sizeof(struct parsing_element) );
	if( tmp != (Parsing_element)NULL ) {
		tmp->label		= (char *)		NULL;
		tmp->label_num		=			-1;
		tmp->nummer		= PE_get_number();
		tmp->level		=			0;
		tmp->attributes 	=			0;
		tmp->right_side 	= (PE_set)		NULL;
		tmp->gnode_set		= (Bitset)		NULL;
		tmp->edges		= (PE_edgelist) 	NULL;
		tmp->lost_edges 	= (Edgeset)		NULL;
		tmp->pre		= (PE_list)		NULL;
		tmp->succ		= (PE_list)		NULL;
		tmp->isomorph_pes	= (PE_list)		NULL;
		tmp->isomorph_main_pe	= (Parsing_element)	tmp;
		tmp->embedge_choices	= (PE_embedge_choice)	NULL;
		tmp->trc_iso		= (Parsing_element)	NULL;
		tmp->pe_iso		= (Parsing_element)	NULL;
		tmp->iso_status 	= (pe_mark_status)	NONE;
		tmp->which_production	= (PE_production)	NULL;
		tmp->loc_embedding	= (PE_embedding)	NULL;
		tmp->snode		= (char *)		NULL;
		tmp->x			=			0;
		tmp->y			=			0;

		/********************************************************************************/
		/*			Layout Graph Grammars: BEGIN				*/
		/********************************************************************************/
		tmp->prod_iso		= NULL;
		tmp->used_prod		= NULL;
		tmp->copy_iso		= NULL;
		tmp->visited		= FALSE;
		tmp->next_iso_node	= NULL;
		tmp->width		= 0;
		tmp->height		= 0;
		/********************************************************************************/
		/*			Layout Graph Grammars: END				*/
		/********************************************************************************/
	}
	return tmp;
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_parsing_element						*/
/*										*/
/*	PARAMETER:	Parsing_element  *PE	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Freigabe des in Verbindung mit dem PE allociertem	*/
/*			Speichers						*/
/*										*/
/*	BESONDERES:   - Es wird nicht nur der Speicher der			*/
/*			Parsing-Element-Struktur freigegeben, sondern auch	*/
/*			..->label, ..->edges, usw.				*/
/*		      - *PE wird auf NULL gesetzt.				*/
/*		      - ohne Auswirkung, falls *PE bereits NULL ist.		*/
/*										*/
/********************************************************************************/

void	PE_dispose_parsing_element(Parsing_element *PE)
                    			/* call BY REFERENCE ! */
{
	if( (PE != NULL) && ((*PE) != NULL) ) {
		if( (*PE)->label != (char *)NULL ) {
			w_free( (*PE)->label );
		}
		if( (*PE)->right_side != (PE_set)NULL ) {
			PE_delete_PE_set( &((*PE)->right_side) );
		}
		if( (*PE)->gnode_set != (Bitset)NULL ) {
			BS_delete_set( &((*PE)->gnode_set ) );
		}
		if( (*PE)->edges != (PE_edgelist)NULL ) {
			PE_dispose_all_edges( *PE );
		}
		if( (*PE)->lost_edges != (Edgeset)NULL ) {
			BS_delete_set( &((*PE)->lost_edges) );
		}
		if( (*PE)->isomorph_pes != (PE_list)NULL ) {
			PE_dispose_all_parsing_elements( &((*PE)->isomorph_pes) );
		}
		if( (*PE)->embedge_choices != (PE_embedge_choice)NULL ) {
			PE_delete_all_embedge_choices( &((*PE)->embedge_choices) );
		}
		if( (*PE)->loc_embedding != (PE_embedding)NULL ) {
			PE_dispose_all_embeddings( *PE );
		}
		w_free((char *) *PE );
		*PE = (Parsing_element)NULL;
	}
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_all_parsing_elements 					*/
/*										*/
/*	PARAMETER:	PE_list   *Plist	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	loesche Grammatik, loesche Graph			*/
/*			Freigabe des gesamten mit Plist allociertem Speicher	*/
/*										*/
/*	BESONDERES:   - *Plist wird auf NULL gesetzt.				*/
/*		      - ohne Auswirkung, falls *Plist bereits NULL ist. 	*/
/*										*/
/********************************************************************************/

void	PE_dispose_all_parsing_elements(struct parsing_element **Plist)
                				/* call BY REFERENCE ! */
{
	PE_list 	lauf;
	PE_list 	tmp;

	if( (Plist==(PE_list *)NULL) || (*Plist==(PE_list)NULL) ) {
		return;
	}
	lauf = *Plist;
	while( lauf != (PE_list)NULL ) {
		tmp = lauf->succ;
		PE_dispose_parsing_element( &lauf );
		lauf = tmp;
	}
	*Plist = (PE_list) NULL;
}

/********************************************************************************/
/*										*/
/*-->	PE_insert_parsing_element						*/
/*										*/
/*	PARAMETER:	1. PE_list	   *Plist (VAR-Parameter!)		*/
/*			2. Parsing_element pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuege ein (existierendes) Parsing-Element in eine	*/
/*			Liste von Parsing-Elementen (PE_list) ein.		*/
/*										*/
/********************************************************************************/

void	PE_insert_parsing_element(struct parsing_element **Plist, Parsing_element pe)
                				/* call BY REFERENCE ! */
                   
{
	if( (Plist==(PE_list *)NULL) || (pe==(Parsing_element)NULL ) ) {
		return;
	}

	pe->pre = (Parsing_element)NULL;
	if( *Plist != (PE_list)NULL ) {
		(*Plist)->pre = pe;
	}
	pe->succ = *Plist;
	*Plist = pe;
}

/********************************************************************************/
/*										*/
/*-->	PE_remove_parsing_element						*/
/*										*/
/*	PARAMETER:	1. PE_list	   *Plist (VAR-Parameter!)		*/
/*			2. Parsing_element pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne ein Parsing-Element aus einer Liste von	*/
/*			Parsing-Elementen (PE_list).				*/
/*										*/
/*	BESONDERES:	pe wird immer entfernt, falls pe nicht erstes Element	*/
/*			einer Liste ist (in diesem Fall wird auch nicht auf	*/
/*			*Plist zugegriffen).					*/
/*			Ist pe erstes Element einer anderen Liste (nicht	*/
/*			*Plist), so wird pe auch nicht entfernt.		*/
/*			Ist pe erstes Element von *Plist, dann Entfernen mit	*/
/*			Update *Plist.						*/
/*										*/
/********************************************************************************/

void	PE_remove_parsing_element(struct parsing_element **Plist, Parsing_element pe)
                				/* call BY REFERENCE ! */
                   
{
	if( (Plist==(PE_list *)NULL) || (pe==(Parsing_element)NULL ) ) {
		return;
	}

	if( pe->pre != (PE_list)NULL ) {
		pe->pre->succ = pe->succ;
	} else { /* first element of list */
		if( *Plist == pe ) {
			*Plist = pe->succ;
		} else { /* pe is not member of *Plist */
			return;
		}
	}
	if( pe->succ != (PE_list)NULL ) {
		pe->succ->pre = pe->pre;
	}
	pe->pre = (PE_list)NULL;
	pe->succ = (PE_list)NULL;
}

/********************************************************************************/
/*										*/
/*-->	PE_add_to_PE_set							*/
/*										*/
/*	PARAMETER:	1. PE_set	   *Pset (VAR-Parameter!)		*/
/*			2. Parsing_element pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuege ein (existierendes) Parsing-Element in eine	*/
/*			MENGE von Parsing-Elementen (PE_set) ein.		*/
/*										*/
/********************************************************************************/

void	PE_add_to_PE_set(PE_set *Pset, Parsing_element pe)
      		      				/* call BY REFERENCE! */
                   
{
	PE_set	tmp;

	tmp = (PE_set)w_malloc(sizeof(struct pe_set));
	if( tmp != (PE_set)NULL ) {
		tmp->pe = pe;
		tmp->prod_iso = pe->pe_iso;
		/********************************************************************************/
		/*			Layout Graph Grammars: BEGIN				*/
		/********************************************************************************/
		if( !ORGINAL_LAMSHOFT )
		{
			if( pe->pe_iso )
			{
				tmp->lp_prod_iso	= pe->pe_iso->prod_iso;
			}
			else
			{
				tmp->lp_prod_iso	= NULL;
			}
		}
		/********************************************************************************/
		/*			Layout Graph Grammars: END				*/
		/********************************************************************************/

		tmp->succ = *Pset;
		*Pset = tmp;
	}
}

/********************************************************************************/
/*										*/
/*-->	PE_delete_PE_set							*/
/*										*/
/*	PARAMETER:	PE_set	   *Pset	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche eine MENGE von Parsing-Elementen.		*/
/*			*Pset wird dadurch auf NULL gesetzt.			*/
/*										*/
/********************************************************************************/

void	PE_delete_PE_set(PE_set *Pset)
      	      					/* call BY REFERENCE! */
{
	PE_set	tmp;

	while( *Pset != (PE_set)NULL ) {
		tmp = (*Pset)->succ;
		w_free((char *) *Pset );
		*Pset = tmp;
	}
}


/********************************************************************************/
/*										*/
/*-->	PE_new_edge								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	PE_edge, falls Speicheranforderung erfolgreich, 	*/
/*			NULL sonst						*/
/*										*/
/*	AUFGABE:	Speicheranforderung fuer eine PE_edge,			*/
/*			im Erfolgsfall primitive Initialisierung.		*/
/*										*/
/********************************************************************************/

PE_edge PE_new_edge(void)
{
	PE_edge tmp;
	tmp = (PE_edge) w_malloc( sizeof( struct pe_edge ) );
	if( tmp != (PE_edge)NULL ) {
		tmp->label	= (char *)		NULL;
		tmp->label_num	=			-1;
		tmp->partner	= (Parsing_element)	NULL;
		tmp->dir	=			UNDEFINED;
		tmp->mark	=			NONE;
		tmp->pre	= (PE_edgelist) 	NULL;
		tmp->succ	= (PE_edgelist) 	NULL;
		tmp->dual_edge	= (PE_edgelist) 	NULL;
		tmp->attributes =			0;
	}
	return tmp;
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_edge 							*/
/*										*/
/*	PARAMETER:	PE_edge   *Edge 	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	gibt Speicher einer PE_edge frei			*/
/*										*/
/*	BESONDERES:   - Es wird nicht nur der Speicher der Datenstruktur	*/
/*			PE_edge freigegeben, sondern auch ..->label.		*/
/*		      - *Edge wird auf NULL gesetzt.				*/
/*		      - ohne Auswirkung, falls *Edge bereits NULL ist.		*/
/*										*/
/*		      - KEINE SPEICHERFREIGABE, falls *Edge noch in einer	*/
/*			PE_edgelist steht.					*/
/*			==> vorher mittels PE_remove_(both_)edge(s) entfernen!	*/
/*										*/
/********************************************************************************/

void	PE_dispose_edge(PE_edge *Edge)
              				/* call BY REFERENCE ! */
{
	PE_edge tmp;

	tmp = *Edge;
	if( tmp != (PE_edge)NULL ) {
		if( (tmp->pre  != (PE_edge)NULL) ||
		    (tmp->succ != (PE_edge)NULL) ) {
			ERROR( "PE_dispose_edge: edge not removed yet!\n" );
			return;
		}
		if( tmp->label != (char *)NULL ) {
			w_free( tmp->label );
		}
		if( tmp->dual_edge != (PE_edge)NULL ) {
			tmp->dual_edge->dual_edge = (PE_edge)NULL;
		}
		w_free((char *) tmp );
		*Edge = (PE_edge)NULL;
	}
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_both_edges							*/
/*										*/
/*	PARAMETER:	PE_edge   *Edge 	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Gibt den Speicher BEIDER MANIFESTATIONEN einer Kante	*/
/*			frei.							*/
/*										*/
/*	BESONDERES:   - benutzt PE_dispose_edge(..)				*/
/*			( ==> vollst. Speicherfreigabe, *Edge := NULL ) 	*/
/*		      - nur die PE_edge's, die in keiner Liste mehr sind,	*/
/*			werden freigegeben ( s. PE_dispose_edge(..) ).		*/
/*		      - einzige (?) Moeglichkeit, nach PE_remove_both_edges(..) */
/*			den Speicher einer Kante korrekt freizugeben.		*/
/*		      - *Edge wird auf NULL gesetzt.				*/
/*		      - ohne Auswirkung, falls *Edge bereits NULL ist.		*/
/*										*/
/********************************************************************************/

void	PE_dispose_both_edges(PE_edge *Edge)
{
	if( (Edge==(PE_edge *)NULL) || ((*Edge)==(PE_edge)NULL) ) {
		return;
	}
	PE_dispose_edge( &((*Edge)->dual_edge) );
	PE_dispose_edge( Edge );
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_all_edges							*/
/*										*/
/*	PARAMETER:	Parsing_element  pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	loesche pe->edges (Adjazenzliste von pe)		*/
/*										*/
/*	BESONDERES:   - Gibt GESAMTEN Speicher einer PE_edgelist frei.		*/
/*		      - setzt pe->edges auf NULL				*/
/*		      - ohne Auswirkung, falls pe->edges bereits NULL ist.	*/
/*										*/
/********************************************************************************/

void	PE_dispose_all_edges(Parsing_element pe)
{
	if( pe==(Parsing_element)NULL ) {
		return;
	}
	PE_delete_edgelist( &(pe->edges) );
}

/********************************************************************************/
/*										*/
/*-->	PE_insert_edge								*/
/*										*/
/*	PARAMETER:	1. Parsing_element  pe					*/
/*			2. PE_edge	    edge				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuege eine PE_edge in die Adjazenzliste von pe ein.	*/
/*										*/
/********************************************************************************/

void	PE_insert_edge(Parsing_element pe, PE_edge edge)
{
	if( (pe == (Parsing_element)NULL) || (edge == (PE_edge)NULL) ) {
		return;
	} else {
		PE_add_edge_to_edgelist( &(pe->edges), edge );
	}
}

/********************************************************************************/
/*										*/
/*-->	PE_remove_edge								*/
/*										*/
/*	PARAMETER:	1. Parsing_element  pe					*/
/*			2. PE_edge	    edge				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne edge aus der Adjazenzliste von pe		*/
/*										*/
/*	BESONDERES:   - WICHTIG: edge muss in pe->edges (Adj.liste) enthalten	*/
/*				 sein! Sonst Datensalat.			*/
/*										*/
/*		      - PE_remove_edge(..) notwendig vor PE_dispose_edge(..)	*/
/*										*/
/********************************************************************************/

void	PE_remove_edge(Parsing_element pe, PE_edge edge)
{
	if( (pe == (Parsing_element)NULL) || (edge == (PE_edge)NULL) ) {
		return;
	}
	if( edge->pre != (PE_edge)NULL ) {
		edge->pre->succ = edge->succ;
	} else {
		pe->edges = edge->succ;
	}
	if( edge->succ != (PE_edge)NULL ) {
		edge->succ->pre = edge->pre;
	}
	edge->pre  = (PE_edge)NULL;
	edge->succ = (PE_edge)NULL;
}

/********************************************************************************/
/*										*/
/*-->	PE_remove_both_edges							*/
/*										*/
/*	PARAMETER:	PE_edge   edge						*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne beide Manifestationen einer Kante aus den	*/
/*			Adjazenzlisten der Endknoten.				*/
/*										*/
/*	BESONDERES:   - KEIN ENTFERNEN, falls nur noch eine Manifestation	*/
/*			vorhanden (in dem Fall hat man dann eine inkonsistente	*/
/*			Datenstruktur)						*/
/*		      - Eine mit PE_remove_both_edges(..) entfernte Kante	*/
/*			sollte mit PE_dispose_both_edges(..) geloescht werden.	*/
/*										*/
/********************************************************************************/

void	PE_remove_both_edges(PE_edge edge)
{
	Parsing_element node, dual_node;
	PE_edge 	dual_edge;

	if( edge == (PE_edge)NULL ) {
		return;
	}
	if( edge->dual_edge == (PE_edge)NULL ) {
		ERROR( "PE_remove_both_edges: only one edge left!\n" );
		return;
	}

	dual_node = edge->partner;
	dual_edge = edge->dual_edge;

	node = dual_edge->partner;

	PE_remove_edge( node, edge );
	PE_remove_edge( dual_node, dual_edge );
}

/********************************************************************************/
/*										*/
/*-->	PE_new_embedding							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	PE_embedding, falls Speicheranforderung erfolgreich,	*/
/*			NULL sonst						*/
/*										*/
/*	AUFGABE:	Speicheranforderung fuer eine Einbettungsregel		*/
/*			(PE_embedding). Im Erfolgsfall primitive		*/
/*			Initialisierung.					*/
/*										*/
/********************************************************************************/

PE_embedding	PE_new_embedding(void)
{
	PE_embedding	tmp;

	tmp = (PE_embedding) w_malloc( sizeof(struct pe_embedding) );
	if( tmp != (PE_embedding)NULL ) {
		tmp->nodelabel		= (char *)	NULL;
		tmp->nodelabel_num	=		-1;
		tmp->edgelabel_pre	= (char *)	NULL;
		tmp->edgelabel_pre_num	=		-1;
		tmp->edgedir_pre	=		UNDEFINED;
		tmp->edgelabel_post	= (char *)	NULL;
		tmp->edgelabel_post_num =		-1;
		tmp->edgedir_post	=		UNDEFINED;
		tmp->mark		=		NONE;
		tmp->succ		= (PE_embedding)NULL;
	}
	return tmp;
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_embedding							*/
/*										*/
/*	PARAMETER:	PE_embedding  *Emb	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche eine Einbettungsregel.				*/
/*			Gibt den gesamten Speicher einer PE_embedding frei.	*/
/*										*/
/*	BESONDERES:   - auch ..->nodelabel, ..->edgelabel_pre, usw. wird	*/
/*			freigegeben						*/
/*		      - *Emb muss bereits aus Einbettungsregel-Liste		*/
/*			( ..->loc_embedding ) entfernt worden sein,		*/
/*			sonst Datensalat.					*/
/*		      - *Emb wird auf NULL gesetzt				*/
/*		      - ohne Auswirkung, falls *Emb bereits NULL ist.		*/
/*										*/
/********************************************************************************/

void		PE_dispose_embedding(PE_embedding *Emb)
            	     				/* call BY REFERENCE ! */
{
	if( (Emb!=(PE_embedding *)NULL) && ((*Emb)!=(PE_embedding)NULL) ) {
		if( (*Emb)->nodelabel !=(char *)NULL ) {
			w_free( (*Emb)->nodelabel );
		}
		if( (*Emb)->edgelabel_pre !=(char *)NULL ) {
			w_free( (*Emb)->edgelabel_pre );
		}
		if( (*Emb)->edgelabel_post !=(char *)NULL ) {
			w_free( (*Emb)->edgelabel_post );
		}
		w_free((char *) *Emb );
		*Emb = (PE_embedding)NULL;
	}
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_all_embeddings						*/
/*										*/
/*	PARAMETER:	Parsing_element  pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	loesche alle Einbettungsregeln eines Parsing-Elements	*/
/*			(genauer: loesche pe->loc_embedding)			*/
/*										*/
/*	BESONDERES:   - pe->loc_embedding wird auf NULL gesetzt 		*/
/*		      - ohne Auswirkung, falls pe->loc_emb... bereits NULL ist. */
/*										*/
/********************************************************************************/

void		PE_dispose_all_embeddings(Parsing_element pe)
{
	PE_embedding	lauf, tmp;

	if( pe==(Parsing_element)NULL ) {
		return;
	}
	lauf = pe->loc_embedding;
	while( lauf != (PE_embedding)NULL ) {
		tmp = lauf->succ;
		PE_dispose_embedding( &lauf );
		lauf = tmp;
	};
	pe->loc_embedding = (PE_embedding)NULL;
}

/********************************************************************************/
/*										*/
/*-->	PE_insert_embedding							*/
/*										*/
/*	PARAMETER:	1. Parsing_element  pe					*/
/*			2. PE_embedding     emb 				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuege eine Einbettungsregel an ein Parsing-Element	*/
/*										*/
/********************************************************************************/

void		PE_insert_embedding(Parsing_element pe, PE_embedding emb)
{
	if( (pe==(Parsing_element)NULL) || (emb==(PE_embedding)NULL) ) {
		return;
	}
	emb->succ = pe->loc_embedding;
	pe->loc_embedding = emb;
}

/********************************************************************************/
/*										*/
/*-->	PE_remove_embedding							*/
/*										*/
/*	PARAMETER:	1. Parsing_element  pe					*/
/*			2. PE_embedding     emb 				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	entferne Einbettungsregel von einem Parsing-Element	*/
/*										*/
/*	BESONDERES:   - ohne Auswirkung, falls emb nicht in pe->loc_embedding	*/
/*			enthalten, ABER 					*/
/*		      - damit auch keine Garantie, dass emb dann in keiner	*/
/*			Liste mehr enthalten ist (Gefahr von Datensalat)	*/
/*										*/
/********************************************************************************/

void		PE_remove_embedding(Parsing_element pe, PE_embedding emb)
{
	PE_embedding	*tmp;
	if( (pe == (Parsing_element)NULL) || (emb == (PE_embedding)NULL) ) {
		return;
	}
	tmp = &(pe->loc_embedding);
	while( (*tmp)!=(PE_embedding)NULL ) {
		if( (*tmp)==emb ) {
			*tmp = emb->succ;
			emb->succ = (PE_embedding)NULL;
			break;
		}
		tmp = &((*tmp)->succ);
	}
}

/********************************************************************************/
/*										*/
/*-->	PE_new_production							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	PE_production falls Speicheranforderung erfolgreich,	*/
/*			NULL sonst						*/
/*										*/
/*	AUFGABE:	Speicheranforderung fuer eine Produktion		*/
/*			(PE_production). Im Erfolgsfall primitive		*/
/*			Initialisierung.					*/
/*										*/
/********************************************************************************/

PE_production	PE_new_production(void)
{
	PE_production	tmp;
	tmp = (PE_production) w_malloc( sizeof(struct pe_production) );
	if( tmp != (PE_production)NULL ) {
		tmp->left_side		= (char *)		NULL;
		tmp->left_side_num	=			-1;
		tmp->right_side 	= (PE_list)		NULL;
		tmp->index		=			0;
		tmp->erreichbar 	=			FALSE;
		tmp->term_ableitbar	=			FALSE;
		tmp->isomorph_prods	= (PE_production)	NULL;
		tmp->succ		= (PE_production)	NULL;
		tmp->x			=			0;
		tmp->y			=			0;
		tmp->width		=			0;
		tmp->height		=			0;
		tmp->nr_options 	=			0;

		/********************************************************************************/
		/*			Layout Graph Grammars: BEGIN				*/
		/********************************************************************************/
			tmp->prod_iso		= (Sprod)		NULL;
		/********************************************************************************/
		/*			Layout Graph Grammars: END				*/
		/********************************************************************************/
	}
	return tmp;
}

/********************************************************************************/
/*										*/
/*-->	PE_dispose_production							*/
/*										*/
/*	PARAMETER:	PE_production	*Prod	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	loesche Produktion					*/
/*			Gibt den gesamten Speicher einer PE_production frei.	*/
/*										*/
/*	BESONDERES:   - auch Freigabe von ..->left_side, ..->right_side, usw.	*/
/*		      - *Prod darf in keiner Grammatik mehr sein,		*/
/*			sonst Datensalat					*/
/*		      - *Prod wird auf NULL gesetzt.				*/
/*		      - ohne Auswirkung, falls *Prod bereits NULL ist.		*/
/*										*/
/********************************************************************************/

void		PE_dispose_production(PE_production *Prod)
             	      				/* call BY REFERENCE ! */
{
	if( (Prod==(PE_production *)NULL) || (*Prod==(PE_production)NULL) ) {
		return;
	}
	if( (*Prod)->left_side != (char *)NULL ) {
		w_free( (*Prod)->left_side );
	}
	if( (*Prod)->right_side != (PE_list)NULL ) {
		PE_dispose_all_parsing_elements( &((*Prod)->right_side) );
	}
	if( (*Prod)->isomorph_prods != (PE_production)NULL ) {
		PE_dispose_all_productions( &((*Prod)->isomorph_prods) );
	}
	w_free((char *) *Prod );
	*Prod = (PE_production)NULL;
}

/********************************************************************************/
/*										*/
/*-->	PE_insert_production							*/
/*										*/
/*	PARAMETER:	1. PE_grammar	  *Gram (VAR-Parameter!)		*/
/*			2. PE_production  prod					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuege Produktion in eine Grammatik ein			*/
/*										*/
/*	BESONDERES:	'Gram' entspricht einer PARTIELL GEORDNETEN Liste von 	*/
/*			Produktionen, wobei die Ordnung durch die Produktions-	*/
/*			indizes festgelegt wird.				*/
/*										*/
/********************************************************************************/

void		PE_insert_production(PE_production *Gram, PE_production prod)
          	      					/* call BY REFERENCE ! */
             	     
{
	PE_grammar	*Tmp;

	if( (Gram==(PE_grammar *)NULL) || (prod==(PE_production)NULL) ) {
		return;
	}
	Tmp = Gram;
	while( (*Tmp!=(PE_grammar)NULL) && ((*Tmp)->index<prod->index) ) {
		Tmp = &((*Tmp)->succ);
	}
	prod->succ = *Tmp;
	*Tmp = prod;
}

/********************************************************************************/
/*										*/
/*-->	PE_remove_production							*/
/*										*/
/*	PARAMETER:	1. PE_grammar	  *Gram (VAR-Parameter!)		*/
/*			2. PE_production  prod					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne Produktion aus einer Grammatik 		*/
/*										*/
/*	BESONDERES:   - ohne Auswirkung, falls prod nicht in *Gram enthalten,	*/
/*			ABER							*/
/*		      - damit auch keine Garantie, dass prod anschliessend in	*/
/*			keiner Liste mehr enthalten ist.(Gefahr von Datensalat) */
/*										*/
/********************************************************************************/

void		PE_remove_production(PE_production *Gram, PE_production prod)
          	      					/* call BY REFERENCE ! */
             	     
{
	PE_grammar	*Tmp;

	if( (Gram==(PE_grammar *)NULL) || (prod==(PE_production)NULL) ) {
		return;
	}
	Tmp = Gram;
	while( *Tmp != (PE_grammar)NULL ) {
		if( (*Tmp)==prod ) {
			break;
		}
		Tmp = &((*Tmp)->succ);
	}
	*Tmp = prod->succ;  /* sollte gleich (*Tmp)->succ sein */
}

/********************************************************************************/
/*										*/
/*-->@	PE_dispose_all_productions						*/
/*-->@	PE_reset_grammar							*/
/*-->@	PE_dispose_grammar							*/
/*										*/
/*	PE_dispose_all_productions, PE_reset_grammar, PE_dispose_grammar	*/
/*										*/
/*	PARAMETER:	PE_grammar  *Gram	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche Grammatik.					*/
/*			Gibt gesamten Speicher einer Grammatik frei.		*/
/*										*/
/*	BESONDERES:   - *Gram wird auf NULL gesetzt				*/
/*		      - ohne Auswirkung, falls *Gram bereits NULL ist.		*/
/*		      - die drei Prozeduren sind identisch			*/
/*										*/
/********************************************************************************/

void		PE_dispose_all_productions(PE_production *Gram)
          	      					/* call BY REFERENCE ! */
{
	PE_grammar	tmp, lauf;

	if( (Gram==(PE_grammar *)NULL) ) {
		return;
	}
	lauf = *Gram;
	while( lauf != (PE_grammar)NULL ) {
		tmp = lauf->succ;
		PE_dispose_production( &lauf );
		lauf = tmp;
	}
	*Gram = (PE_grammar)NULL;
}

/********************************************************************************/
/*										*/
/*-->	PE_add_edge_to_edgelist 						*/
/*										*/
/*	PARAMETER:	1. PE_edgelist *list	(VAR-Parameter) 		*/
/*			2. PE_edge     edge					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuege 'edge' in 'list' ein.				*/
/*										*/
/********************************************************************************/

void	PE_add_edge_to_edgelist(PE_edge *list, PE_edge edge)
           	      					/* call BY REFERENCE! */
        	     
{
	if( (list==NULL) || (edge==NULL) ) {
		return;
	}
	if( *list != (PE_edgelist)NULL ) {
		(*list)->pre = edge;
	}
	edge->succ = *list;
	*list = edge;
}

/********************************************************************************/
/*										*/
/*-->	PE_delete_edgelist							*/
/*										*/
/*	PARAMETER:	PE_edgelist *list	(VAR-Parameter) 		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche Kantenliste '*list'.				*/
/*										*/
/*	BESONDERES:	- *list wird auf NULL gesetzt.				*/
/*			- ohne Auswirkung, falls '*list' bereits NULL ist.	*/
/*										*/
/********************************************************************************/

void	PE_delete_edgelist(PE_edge *list)
           	      					/* call BY REFERENCE! */
{
	PE_edgelist	lauf, tmp;

	lauf = *list;
	while( lauf != (PE_edgelist)NULL ) {
		tmp = lauf->succ;
		lauf->succ = NULL;
		lauf->pre = NULL;
		PE_dispose_edge( &lauf );
		lauf = tmp;
	};
	*list = (PE_edgelist)NULL;
}

/********************************************************************************/
/*										*/
/*-->	PE_new_embedge_choice							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	PE_embedge_choice oder NULL				*/
/*										*/
/*	AUFGABE:								*/
/*	BESONDERES:								*/
/*	ERKLAERUNG:								*/
/*										*/
/********************************************************************************/

PE_embedge_choice PE_new_embedge_choice(void)
{
	PE_embedge_choice	tmp;

	tmp = (PE_embedge_choice) w_malloc( sizeof(struct pe_embedge_choice) );
	if( tmp != NULL ) {
		tmp->edges	= (PE_edgelist) 	NULL;
		tmp->succ	= (PE_embedge_choice)	NULL;
	}
	return	tmp;
}

/********************************************************************************/
/*										*/
/*-->	PE_insert_embedge_choice						*/
/*										*/
/*	PARAMETER:	1. PE_embedge_choice  *list	(VAR-Parameter) 	*/
/*			2. PE_embedge_choice  elem				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:								*/
/*	BESONDERES:								*/
/*	ERKLAERUNG:								*/
/*										*/
/********************************************************************************/

void	PE_insert_embedge_choice(PE_embedge_choice *list, PE_embedge_choice elem)
                        				/* call BY REFERENCE! */
                       
{
	if( (list==NULL) || (elem==NULL) ) {
		return;
	}
	elem->succ = *list;
	*list = elem;
}

/********************************************************************************/
/*										*/
/*-->	PE_remove_embedge_choice						*/
/*										*/
/*	PARAMETER:	PE_embedge_choice  *list	(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne erstes Element aus 'list'.			*/
/*										*/
/*	BESONDERES:								*/
/*	ERKLAERUNG:								*/
/*										*/
/********************************************************************************/

void	PE_remove_embedge_choice(PE_embedge_choice *list)
                 	      				/* call BY REFERENCE! */
{
	if( (list==NULL) || (*list==NULL) ) {
		return;
	}
	*list = (*list)->succ;
}

/********************************************************************************/
/*										*/
/*-->	PE_delete_all_embedge_choices						*/
/*										*/
/*	PARAMETER:	PE_embedge_choice  *list	(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche gesamte Embedge_choice-Liste.			*/
/*										*/
/*	BESONDERES:								*/
/*	ERKLAERUNG:								*/
/*										*/
/********************************************************************************/

void	PE_delete_all_embedge_choices(PE_embedge_choice *list)
                        				/* call BY REFERENCE! */
{
	PE_embedge_choice	tmp, lauf;

	lauf = *list;
	while( lauf != NULL ) {
		tmp = lauf->succ;
		PE_delete_edgelist( &(lauf->edges) );
		w_free((char *) lauf );
		lauf = tmp;
	}
	*list = NULL;
}

/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mtypes 								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Ftypes 							*/
/*m		-Etypes 							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dtypes 							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Ptypes 							*/
/********************************************************************************/
