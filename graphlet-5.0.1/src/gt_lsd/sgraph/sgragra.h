/* This software is distributed under the Lesser General Public License */
/* (C) Universitaet Passau 1986-1994 */
/************************************************************************
 * File    : sgragra.h                                      Version 1.0 *
 * Aufgabe : Datenstruktur fuer Graphgrammatiken, aufbauend auf der	*
 *           Standard-Datenstruktur Sgraph fuer Graphen.                *
 * Autor   : Torsten Bachmann						*
 * Datum   : 18.12.90							*
 ************************************************************************/

#ifndef SGRAGRA_HEADER
#define SGRAGRA_HEADER



/************************************************************************
 *									*
 *			    alphabet-utilities				*
 *									*
 ************************************************************************/

extern	Slist	S_alphabet_append(Slist *alphabet, char *element);
extern	Slist	S_alphabet_test  (Slist alphabet, char *element);
extern	void	S_alphabet_remove(Slist alphabet);

#define Snode_label_is_terminal(sgragra, label) (S_alphabet_test((sgragra)->tv, (label)) != empty_slist)
#define Snode_label_is_nonterminal(sgragra, label) (S_alphabet_test((sgragra)->nv, (label)) != empty_slist)
#define Sedge_label_is_terminal(sgragra, label) (S_alphabet_test((sgragra)->te, (label)) != empty_slist)
#define Sedge_label_is_nonterminal(sgragra, label) (S_alphabet_test((sgragra)->ne, (label)) != empty_slist)


/************************************************************************
 *									*
 *				Sembed					*
 *									*
 ************************************************************************/

#define S_out	0
#define S_in	1

typedef struct sembed
{
    struct sembed	*pre, *suc;	/* Liste aller Regeln		*/
    struct sembed	*npre, *nsuc;	/* Einbettungen der Knoten	*/
    struct sprod	*prod;		/* "Vatergrammatik"		*/

    Snode		node_right;	/* Knoten der rechten Seite	*/
    char		*node_embed;	/* Markierung des Knotens aus	*/
    /* der Einbettung		*/

    char		*oldedge;	/* Markierung der alten Kante	*/
    int             olddir;		/* Richtung der alten Kanten	*/

    char		*newedge;	/* Markierung der neuen Kante	*/
    int		newdir;		/* Richtung der neuen Kante	*/

    Attributes	attrs;
}
*Sembed;



#define	empty_sembed	((Sembed)NULL)

#define	for_all_sembeds(prod, emb) \
{ if (((emb) = (prod)->embedding) != empty_sembed) do {
#define	end_for_all_sembeds(prod, emb) \
} while (((emb) = (emb)->suc) != (prod)->embedding); }

#define	for_all_snode_sembeds(node, emb) \
{ if (((emb) = (Sembed) node->embedding) \
      != empty_sembed) do {
#define	end_for_all_snode_sembeds(node, emb) \
      } while (((emb) = (emb)->nsuc) != (Sembed) node->embedding); }


/************************************************************************
 *									*
 *			      Sglobalembed				*
 *									*
 ************************************************************************/

typedef struct sglobalembed
{	
    struct sglobalembed	*pre, *suc;	/* Liste aller Regeln	*/
    struct sgragra		*gragra;	/* "Vaterproduktion"	*/

    char		*node_right;
    char		*node_embed;

    char		*oldedge;
    int             olddir;	
	
    char		*newedge;
    int		newdir;

    Attributes	attrs;
}
*Sglobalembed;



#define	empty_sglobalembed	((Sglobalembed)NULL)

#define	for_all_sglobalembeds(gragra, gemb) \
{ if (((gemb) = (gragra)->global_embeddings) != empty_sglobalembed) do {
#define	end_for_all_sglobalembeds(gragra, gemb) \
} while (((gemb) = (gemb)->suc) != (gragra)->global_embeddings); }

	

/************************************************************************
 *									*
 *				Sprod					*
 *									*
 ************************************************************************/

typedef struct sprod
{	struct sprod	*pre, *suc;	/* Liste der Produktionen	*/
    struct sgragra	*gragra;        /* "Vatergrammatik"		*/

    char		*left;		/* linke Seite der Produktion	*/
    Sgraph		right;		/* rechte Seite			*/
    Sembed		embedding;	/* Einbettungsvorschrift	*/
    char		*label;
    Attributes	attrs;

    char		*graphed_graph,
	*graphed_left;
}
*Sprod;

#define	empty_sprod	((Sprod)NULL)

#define	for_all_sprods(gg, prod) \
{ if (((prod) = (gg)->productions) != empty_sprod) do {
#define	end_for_all_sprods(gg, prod) \
} while (((prod) = (prod)->suc) != (gg)->productions); }


/************************************************************************
 *									*
 *				Sgragra					*
 *									*
 ************************************************************************/

typedef enum 
{	
    S_UNDEFINED		= -1,

    S_GG			=  0,	/* the lowest bit determines       */
    S_GG_UNDIRECTED		=  1,	/* the directedness of the grammar */
	
    S_NCE_1			=  2,
    S_NCE_1_UNDIRECTED	=  3,
	
    S_NLC			=  4,
    S_NLC_UNDIRECTED	=  5,
	
    S_BNLC			=  6,
    S_BNLC_UNDIRECTED	=  7,
	
    S_ENCE_1		=  8,
    S_ENCE_1_UNDIRECTED     =  9
	
}
Sgragra_type;

typedef struct sgragra
{	Sgragra_type	my_class;		/* Grammatikklasse		*/
    Slist		tv, te;		/* Terminalzeichen		*/
    Slist		nv, ne;		/* Nichtterminalzeichen		*/
    Sprod		productions;	/* Liste der Produktionen	*/
    Sglobalembed	global_embeddings; 
    Snode		startnode;	/* Startknoten aus nv		*/
    char 		*label;
    Attributes	attrs;
}
*Sgragra;

#define	first_prod_in_sgragra(g)	((g)->productions)
#define	last_prod_in_sgragra(g)		((g)->productions->pre)
#define	empty_sgragra			((Sgragra)NULL)


extern	Sembed	make_sembed	(Sprod prod, Snode node);
extern	void	remove_sembed	(Sembed embed);

extern	Sglobalembed	make_sglobalembed	(Sgragra gragra);
extern	void		remove_sglobalembed	(Sglobalembed gembed);

extern	Sprod	make_sprod             (Sgragra gragra);
extern	void	remove_sprod           (Sprod prod);
extern	void	set_sprodlabel         (Sprod prod, char *label);

extern	Sgragra	make_sgragra     (void);
extern	void	remove_sgragra   (Sgragra gragra);
extern	void	set_sgragralabel (Sgragra gragra, char *label);


#endif
