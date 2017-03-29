/*-->	Restarbeit_types_h

	Erklaerung der Datenstruktur PE_embedge_choice

*/

/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>

#include "lp_datastructures.h"

/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/


/********************************************************************************/
/*										*/
/*	MODUL:	  types.h							*/
/*										*/
/*	FUNKTION: Deklaration der zentralen Datenstrukturen des Parsers 	*/
/*										*/
/********************************************************************************/

#ifndef TYPES_HEADER
#define TYPES_HEADER


#include "bitset.h"

/********************************************************************************/
/*										*/
/*-->@	-Dtypes 								*/
/*										*/
/*	Liste der im Modul 'types' definierten Datenstrukturen. 		*/
/*	Die mit <> gekennzeichneten Datenstrukturen sind Hauptdatenstrukturen.	*/
/*	Bei ihnen findet sich auch noch eine genauere Beschreibung.		*/
/*										*/
/*	   Edgeset		   edge_direction				*/
/*	   pe_mark_status	   pe_attributes				*/
/*										*/
/*	   parsing_element	<> Parsing_element	<> PE_list		*/
/*	   pe_edge		<> PE_edge		<> PE_edgelist		*/
/*	   pe_embedding 	<> PE_embedding 				*/
/*	   pe_embedge_choice	<> PE_embedge_choice				*/
/*	   pe_production	<> PE_production	<> PE_grammar		*/
/*	   pe_set		<> PE_set					*/
/*										*/
/********************************************************************************/

/********************************************************************************/
/*										*/
/*	Datenstrukturen fuer Parsingelemente (konvertierte Graphen)		*/
/*										*/
/********************************************************************************/
/*-->@ Edgeset	*/

#define Edgeset Bitset
			/* type Bitset defined in "bitset.h" */
/**/

#define PE_list struct parsing_element *

/*-->@ pe_mark_status */

typedef enum {
		NONE,
		MARKED,
		FIXED,
		nr_of_pe_mark_status_types
	} pe_mark_status;
/**/

/*-->@ edge_direction */

typedef enum {
		IN,
		OUT,
		UNDIRECTED,
		BLOCKING,
		UNDEFINED,
		nr_of_edgedir_types
	} edge_direction;
/**/

/*-->@ pe_edge		*/

typedef struct	pe_edge {
		char			*label;
		int			label_num;
		struct parsing_element	*partner;
		edge_direction		dir;
		pe_mark_status		mark;
		int			attributes;
		struct pe_edge		*pre;
		struct pe_edge		*succ;
		struct pe_edge		*dual_edge;
	} *PE_edge;

#define PE_edgelist PE_edge
/**/

/*-->@ pe_attributes	 */

typedef enum	{
		PE_adjacent	=	1,
		PE_processed	=	2,
		PE_traced	=	4,
		PE_virtual	=	8,
		PE_real 	=	16,

		PE_free1	=	32,
		PE_free2	=	(PE_free1 << 1),
		PE_free3	=	(PE_free2 << 1)
	} pe_attributes;
/**/

/*-->@ pe_embedge_choice */

typedef struct	pe_embedge_choice {
		PE_edgelist			edges;
		struct pe_embedge_choice	*succ;
	} *PE_embedge_choice;
/**/

/*-->@ pe_set */

typedef struct	pe_set {
		struct parsing_element	*pe;
		struct parsing_element	*prod_iso;
		struct pe_set		*succ;

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
		struct	snode			*lp_prod_iso;
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/

	} *PE_set;
/**/

/*-->@ parsing_element */

typedef struct	parsing_element {

	/* PE information: */

		char			*label;
		int			label_num;
		struct pe_production	*which_production;
		PE_set			right_side;
		Bitset			gnode_set;
		Edgeset 		lost_edges;
		PE_edgelist		edges;

	/* PTab information: */

		PE_list 		pre;
		PE_list 		succ;
		PE_list 		isomorph_pes;
		struct parsing_element	*isomorph_main_pe;

	/* embedding information if in right side of production: */

		struct pe_embedding	*loc_embedding;

	/* (layout) information for tracer: */

		int			x, y;
		char			*snode;
		struct parsing_element	*trc_iso;

	/* debugging information: */

		int			nummer;
		int			level;

	/* internal information for parsing procedures and vice versa: */

		int			attributes;

		PE_embedge_choice	embedge_choices;

		struct parsing_element	*pe_iso;
		pe_mark_status		iso_status;

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
		struct	snode			*prod_iso; /* fuer Kopie von Produktion */
		struct	sprod			*used_prod; /* Wenn PE abgeleitet ist	*/
		struct	LP_parsing_element	*copy_iso; /* Zum Umkopieren 		*/
		int				visited; /* Schon Kopiert ?		*/
		struct	parsing_element		*next_iso_node; /* Fuer isomorph Prods  */
		int				width, height; /* Fuers neuzeichnen	*/
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/

	} *Parsing_element;
/**/

#define PE_for_all_parsing_elements( plist, pe ) \
	{ if( ((pe)=(plist))!=(Parsing_element)NULL ) do {
#define PE_end_for_all_parsing_elements( plist, pe ) \
	} while( ((pe)=(pe)->succ) != (Parsing_element)NULL ); }
#define PE_for_all_edges( pe, edge ) \
	{ if( ((edge)=(pe)->edges) != (PE_edgelist)NULL ) do {
#define PE_end_for_all_edges( pe, edge ) \
	} while( ((edge)=(edge)->succ) != (PE_edgelist)NULL ); }
#define PE_for_all_embeddings( pe, emb ) \
	{ if( ((emb)=(pe)->loc_embedding) != (PE_embedding)NULL ) do {
#define PE_end_for_all_embeddings( pe, emb ) \
	} while( ((emb)=(emb)->succ) != (PE_embedding)NULL ); }

#define PE_UNMARK( pe ) \
	if( (pe)!=NULL ) (pe)->iso_status = NONE
#define PE_MARK( pe ) \
	if( (pe)!=NULL ) (pe)->iso_status = MARKED
#define PE_FIX( pe ) \
	if( (pe)!=NULL ) (pe)->iso_status = FIXED
#define PE_IS_UNMARKED( pe ) \
	(((pe)!=NULL) ? ((pe)->iso_status == NONE) : TRUE)
#define PE_IS_MARKED( pe ) \
	(((pe)!=NULL) ? ((pe)->iso_status == MARKED) : FALSE)
#define PE_IS_FIXED( pe ) \
	(((pe)!=NULL) ? ((pe)->iso_status == FIXED) : FALSE)

#define EDGE_UNMARK( edge ) \
	if( (edge)!=NULL ) (edge)->mark = NONE
#define EDGE_MARK( edge ) \
	if( (edge)!=NULL ) (edge)->mark = MARKED
#define EDGE_IS_UNMARKED( edge ) \
	(((edge)!=NULL) ? ((edge)->mark == NONE) : TRUE)
#define EDGE_IS_MARKED( edge ) \
	(((edge)!=NULL) ? ((edge)->mark == MARKED) : FALSE)

#define EMB_UNMARK( emb ) \
	if( (emb)!=NULL ) (emb)->mark = NONE
#define EMB_MARK( emb ) \
	if( (emb)!=NULL ) (emb)->mark = MARKED
#define EMB_IS_UNMARKED( emb ) \
	(((emb)!=NULL) ? ((emb)->mark == NONE) : TRUE)
#define EMB_IS_MARKED( emb ) \
	(((emb)!=NULL) ? ((emb)->mark == MARKED) : FALSE)

#define SIZE_OF_GRAPH( graph ) \
	(((graph)==NULL) ? 0 : (graph)->nummer)

#define ATTRS_RESUME( pe ) \
	(pe)->attributes = 0;
#define ATTRS_TEST( pe, attr ) \
	( ((pe)->attributes & (attr)) == (attr) )
#define ATTRS_SET( pe, attr ) \
	(pe)->attributes |= (attr)
#define ATTRS_CLEAR( pe, attr ) \
	(pe)->attributes &= ~(attr)



/********************************************************************************/
/*										*/
/*	Datenstrukturen fuer Parsing-Grammatik (konvertierte Grammatik) 	*/
/*										*/
/********************************************************************************/

/*-->@ pe_embedding */

typedef struct	pe_embedding {
		char			*nodelabel;
		int			nodelabel_num;

		char			*edgelabel_pre;
		int			edgelabel_pre_num;

		edge_direction		edgedir_pre;

		char			*edgelabel_post;
		int			edgelabel_post_num;

		edge_direction		edgedir_post;

		pe_mark_status		mark;
		struct pe_embedding	*succ;
	} *PE_embedding;
/**/

/*-->@ pe_production */

typedef struct	pe_production {

	/* production information: */

		char			*left_side;
		int			left_side_num;
		PE_list 		right_side;

	/* production status information: */

		int			index;
		int	/* bool */	erreichbar;
		int	/* bool */	term_ableitbar;

	/* grammar structure information: */

		struct pe_production	*isomorph_prods;
		struct pe_production	*succ;

	/* (layout) information for tracer: */

		int			x, y, width, height;	/* GraphEd's position of production */
		int			nr_options;

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
		struct	sprod		*prod_iso;
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/

	} *PE_production;

#define PE_grammar	PE_production

/**/

#define PE_for_all_productions( grammar, prod ) \
	{ if( ((prod) = (grammar)) != (PE_production)NULL ) do {
#define PE_end_for_all_productions( grammar, prod ) \
	} while( ((prod)=(prod)->succ) != (PE_production)NULL ); }


/********************************************************************************/
/*										*/
/*	exportierte Prozeduren/Funktionen :					*/
/*										*/
/********************************************************************************/


extern	Parsing_element PE_new_parsing_element(void);
extern	void		PE_dispose_parsing_element(Parsing_element *PE);
extern	void		PE_dispose_all_parsing_elements(struct parsing_element **Plist);
extern	void		PE_insert_parsing_element(struct parsing_element **Plist, Parsing_element pe);
extern	void		PE_remove_parsing_element(struct parsing_element **Plist, Parsing_element pe);

extern	void		PE_add_to_PE_set(PE_set *Pset, Parsing_element pe);
extern	void		PE_delete_PE_set(PE_set *Pset);

extern	void		PE_add_edge_to_edgelist(PE_edge *list, PE_edge edge);
extern	void		PE_delete_edgelist(PE_edge *list);
extern	PE_embedge_choice PE_new_embedge_choice(void);
extern	void		PE_insert_embedge_choice(PE_embedge_choice *list, PE_embedge_choice elem);
extern	void		PE_delete_all_embedge_choices(PE_embedge_choice *list);

extern	void		PE_set_number_generator(int new_value);

extern	PE_edge 	PE_new_edge(void);
extern	void		PE_dispose_edge(PE_edge *Edge);
extern	void		PE_dispose_both_edges(PE_edge *Edge);
extern	void		PE_dispose_all_edges(Parsing_element pe);
extern	void		PE_insert_edge(Parsing_element pe, PE_edge edge);
extern	void		PE_remove_edge(Parsing_element pe, PE_edge edge);
extern	void		PE_remove_both_edges(PE_edge edge);

extern	edge_direction	PE_inverse_dir(edge_direction dir);

extern	PE_embedding	PE_new_embedding(void);
extern	void		PE_dispose_embedding(PE_embedding *Emb);
extern	void		PE_dispose_all_embeddings(Parsing_element pe);
extern	void		PE_insert_embedding(Parsing_element pe, PE_embedding emb);
extern	void		PE_remove_embedding(Parsing_element pe, PE_embedding emb);
extern	void		PE_remove_embedge_choice(PE_embedge_choice *list);

extern	PE_production	PE_new_production(void);
extern	void		PE_dispose_production(PE_production *Prod);
extern	void		PE_dispose_all_productions(PE_production *Gram);
extern	void		PE_insert_production(PE_production *Gram, PE_production prod);
extern	void		PE_remove_production(PE_production *Gram, PE_production prod);

#define PE_reset_grammar( Gram )	PE_dispose_all_productions( Gram )
#define PE_dispose_grammar( Gram )	PE_dispose_all_productions( Gram )
#define PE_reset_number_generator()	PE_set_number_generator( 0 )


/********************************************************************************/
/*										*/
/*	Erklaerung der Datenstrukturen						*/
/*										*/
/********************************************************************************/
/*										*/
/*-->	PE_embedge_choice							*/
/*										*/
/*										*/
/*m	pe_embedge_choice							*/
/********************************************************************************/
/*										*/
/*-->	PE_set									*/
/*										*/
/*	Ganz einfach: Eine Menge von Parsing-Elementen, implementiert als	*/
/*	einfach verkettete Liste von (Verweisen auf) PE's.			*/
/*	Diese Datenstruktur hat vorwiegend den Zweck, die Menge der Parsing-	*/
/*	Elemente zu speichern, die aus einem PE mittels einer Produktion	*/
/*	entstehen (siehe PE->right_side). In einer solchen ..->right_side wird	*/
/*	ausserdem in den ..->prod_iso's der Knotenisomorphismus h von		*/
/*	PE->right_side nach PE->which_production->right_side abgespeichert.	*/
/*										*/
/*m	pe_set									*/
/********************************************************************************/
/*										*/
/*-->@	Parsing_element 							*/
/*-->@	PE_list 								*/
/*										*/
/*	Parsing_element / PE_list						*/
/*										*/
/*    ( BEMERKUNG: PE_list ist eine doppelt verkettete Liste von Parsing-    )	*/
/*    ( 	   Elementen. Da die Zeiger der Liste im Typ Parsing_element )	*/
/*    ( 	   bereits enthalten sind sind beide Datentypen identisch.   )	*/
/*    ( 	   Die Zeiger der Liste sind dabei PE->pre und PE->succ .    )	*/
/*										*/
/*	Ein Parsing-Element (PE) repraesentiert entweder den KNOTEN des Einga-	*/
/*	begraphen oder einen (moeglichen) ABLEITUNGSSCHRITT bei der Erzeugung	*/
/*	des Graphen durch eine (gegebene) Graph-Grammatik.			*/
/*										*/
/*	ALLGEMEIN gueltige Komponenten eines Parsingelements:			*/
/*										*/
/*		PE->label     : Markierung des Knotens				*/
/*				(terminal, falls Graph terminal)		*/
/*		PE->label_num : interner Zahlenwert, der eindeutig mit		*/
/*				PE->label korrespondiert.			*/
/*		PE->which_production : Zeiger auf Produktion, mittels der aus	*/
/*				PE die PE->right_side abgeleitet wird		*/
/*		PE->right_side : Menge von Parsingelementen (mit zugehoeriger	*/
/*				Bijektion auf rechte Seite von 			*/
/*				PE->which_production), die im ersten Schritt	*/
/*				der gesteuerten Ableitung aus PE abgeleitet	*/
/*				werden						*/
/*		PE->gnode_set : enthaelt die Teilmenge der Graphknoten, die	*/
/*				durch dieses PE erzeugt werden koennen (Bitset)	*/
/*		PE->lost_edges: (Bit)Menge von Kantenbeschreibungen, die	*/
/*				angibt, welche Kanten bei der gesteuerten 	*/
/*				Ableitung von PE verlorengehen			*/
/*		PE->edges     : Liste von Kanten adjazenter Knoten		*/
/*										*/
/*		PE->isomorph_pes : Liste von zu PE isomorphen Parsingelementen	*/
/*				   (PE_list !!!)				*/
/*				ACHTUNG: das erste Element der Liste ist der	*/
/*				Repraesentant der gesamten Liste. Fuer alle	*/
/*				Parsingelemente einer solchen Liste gilt, dass	*/
/*				PE->isomorph_main_pe auf diesen Repraesentanten	*/
/*				zeigt. Daraus folgt zwingend, dass gilt:	*/
/*				PE != PE->isomorph_main_pe			*/
/*				==> PE->isomorph_pes == NULL			*/
/*										*/
/*										*/
/*	Als KNOTEN des Eingabegraphen genuegt PE folgenden Eigenschaften	*/
/*										*/
/*		PE->gnode_set 		ist einelementig			*/
/*		PE->lost_edges		= leere Menge				*/
/*		PE->level		= 0					*/
/*		PE->which_production	= NULL					*/
/*		PE->right_side		= NULL					*/
/*										*/
/*										*/
/*	Als ABLEITUNGSSCHRITT gelten fuer ein PE folgende Zusaetze:		*/
/*										*/
/*		PE->label     : ist immer nichtterminal 			*/
/*		PE->which_production != NULL					*/
/*		PE->right_side != NULL						*/
/*		PE->level > 0							*/
/*										*/
/*										*/
/*	BEMERKUNG: da Parsing-Elemente auch als GRAPHKNOTEN rechter Seiten	*/
/*		   von PRODUKTIONEN auftreten existiert hier noch zusaetzlich	*/
/*		   ein Feld							*/
/*										*/
/*		PE->loc_embedding :						*/
/*				 Menge von Einbettungsregeln fuer diesen	*/
/*				 Knoten als Teil der rechten Seite einer	*/
/*				 Produktion.					*/
/*										*/
/*m	parsing_element 							*/
/*------------------------------------------------------------------------------*/
/*										*/
/*-->@	PE_edge 								*/
/*-->@	PE_edgelist								*/
/*										*/
/*	PE_edge / PE_edgelist							*/
/*										*/
/*    ( BEMERKUNG: PE_edgelist ist eine doppelt verkettete Liste von PE_edge(s))*/
/*    ( 	   Da die Zeiger der Liste im Typ PE_edge bereits enthalten    )*/
/*    ( 	   sind ,sind beide Datentypen identisch.		       )*/
/*    ( 	   Die Zeiger der Liste sind dabei EDGE->pre und EDGE->succ .  )*/
/*										*/
/*	PE_edge (EDGE) repraesentiert eine Implementierung von Kanten. Dabei	*/
/*	stellt jedoch EDGE keine Datenstruktur im Sinne EINES (Graphik-)	*/
/*	Objektes 'Kante' dar, sondern lediglich den passenden Teil fuer die	*/
/*	Adjazenzliste eines Knotens (PE's).					*/
/*	Somit bekommt jede Kante genau zwei Manifestationen (PE_edge's), und	*/
/*	zwar bei jedem der beiden Endknoten aus der Sicht desselben.		*/
/*										*/
/*	Die beiden Manifestationen einer Kante verweisen ueber ..->dual_edge	*/
/*	aufeinander und ergeben zusammen ein vollstaendige Beschreibung der	*/
/*	Kante.									*/
/*										*/
/*										*/
/*	Inhaltsbeschreibung: dabei NODE= PE in dessen ->edges EL enthalten ist. */
/*										*/
/*	EDGE->label	: Markierung der Kante					*/
/*	EDGE->partner	: Knoten, der ueber die Kante mit NODE verbunden ist	*/
/*	EDGE->dir	: Richtung der Kante bzgl. NODE 			*/
/*	EDGE->dual_edge : Verweis auf die andere Manifestation der Kante	*/
/*										*/
/*m	pe_edge 								*/
/*------------------------------------------------------------------------------*/
/*										*/
/*-->	PE_embedding								*/
/*										*/
/*	PE_embedding (EMB) stellt eine einfach verkettete Liste von Ein-	*/
/*	bettungsregeln dar. Einbettungsregeln sind nur fuer Knoten von rechten	*/
/*	Seiten von Produktionen definiert. Sie beschreiben, wie im Falle	*/
/*	einer Ableitung die Einbettungsstruktur der linken Seite in eine	*/
/*	Einbettung der rechten Seite uebergeht. 				*/
/*										*/
/*	Beschreibung: (Ersetzungsschritt)					*/
/*	Sei LEFT der Knoten, der ersetzt wird, N ein Knoten der rechten Seite	*/
/*	der Produktion, N->pe_iso der zu N gehoerige Knoten der isomorphen	*/
/*	Kopie der rechten Seite und EMB eine Regel aus N->loc_embeddings.	*/
/*										*/
/*	Dann gilt fuer EMB:							*/
/*										*/
/*	Falls ein Knoten K mit LEFT ueber Kante E derart verbunden ist, dass	*/
/*		1. K->label = EMB->nodelabel					*/
/*		2. E->label = EMB->edgelabel_pre				*/
/*	   und	3. Richtung von E (aus Sicht von LEFT) entspricht		*/
/*		   EMB->edgedir_pre						*/
/*										*/
/*	dann erzeuge neue Kante E' fuer die gilt:				*/
/*		1. E'->label := EMB->edgelabel_post				*/
/*		2. E' verbindet N->pe_iso und K 				*/
/*	   und	3. Richtung von E' (aus Sicht von N->pe_iso) entspricht 	*/
/*		   EMB->edgedir_post						*/
/*										*/
/*m	pe_embedding								*/
/*------------------------------------------------------------------------------*/
/*										*/
/*-->@	PE_production								*/
/*-->@	PE_grammar								*/
/*										*/
/*	PE_production / PE_grammar						*/
/*										*/
/*    ( BEMERKUNG: PE_grammar ist eine einfach verkettete Liste von	      ) */
/*    ( 	   PE_production(s). Da der Zeiger der Liste im Typ PE_prod...) */
/*    ( 	   bereits enthalten ist, sind beide Datentypen identisch.    ) */
/*    ( 	   Der Zeiger der Liste ist dabei PROD->succ		      ) */
/*										*/
/*										*/
/*	PE_production (PROD) realisiert die Implementierung von Produktionen	*/
/*	bzw. Grammatiken.							*/
/*										*/
/*	Beschreibung:								*/
/*										*/
/*	PROD->left_side : Markierung eines Knotens, auf den die Produktion	*/
/*			  anwendbar ist 					*/
/*	PROD->right_side: Graph durch dessen Kopie ein Knoten ersetzt wird.	*/
/*										*/
/*	Einbettungsregeln werden bei den Knoten der rechten Seite gespeichert.	*/
/*										*/
/*										*/
/*	PROD->index	: Ordnungsindex einer Prod. innerhalb der Grammatik	*/
/*			  (wird fuer Strategiezwecke benutzt).			*/
/*	PROD->erreichbar: boolesches Flag, das anzeigt, ob die Produktion	*/
/*			  jemals in einer Ableitung anwendbar ist.		*/
/*	PROD->term_ableitbar: boolesches Flag, das anzeigt, ob nach Anwendung	*/
/*			  der Produktion noch ein terminaler Graph abgeleitet	*/
/*			  werden kann.						*/
/*										*/
/*	Die beiden Flags werden benutzt, um die Grammatik zu reduzieren, denn	*/
/*	nur Produktionen mit beiden Flags auf TRUE koennen zur Erzeugung von	*/
/*	terminalen Graphen beitragen.						*/
/*										*/
/*m	pe_production								*/
/*------------------------------------------------------------------------------*/
/*										*/
/*-->	Nummerngenerator							*/
/*										*/
/*	Zweck:	Unterscheidung von PE's mit identischem Label bei der Ausgabe	*/
/*		durch DBX-Prozeduren.						*/
/*		Immer, wenn ein SGraph transformiert wird, wird der automa-	*/
/*		tische Nummerngenerator zurueckgesetzt. So wird gewaehrleistet, */
/*		dass innerhalb eines Graphen kleinstmoegliche Nummern vergeben	*/
/*		werden. 							*/
/*		Man kann nach der Transformation eines Graphen g die groesste	*/
/*		PE-Nummer durch g->nummer erfahren und vor einer weiteren	*/
/*		Manipulation von g durch PE_set_number_generator( g->nummer )	*/
/*		wieder aktualisieren.						*/
/*										*/
/********************************************************************************/

#endif

