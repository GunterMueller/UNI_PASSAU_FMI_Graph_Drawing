#ifndef mystd_h
#define mystd_h
/*******************************************************************************/
/*                                                                             */
/*                 A l l g e m e i n e   P r o z e d u r e n                   */
/*                                                                             */
/* Modul        : mystd.h						       */
/* erstellt von : Nikolas Motte                                                */
/* erstellt am  : 10.12.1992                                                   */
/*                                                                             */
/*     Dieses Modul enthaelt Funktionen, die fuer fast alle anderen Module     */
/*     benoetigt werden.						       */
/*******************************************************************************/

/********************************************************************************/
/*						                                */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		                */
/*								                */
/********************************************************************************/
/*   Ausgabeprozeduren:						                */
/*								                */
/*	void 		printsnode		(Snode) 			*/
/*			gibt den label eines Snodes aus.			*/
/*								                */
/*	void 		printslist		(Slist)				*/
/*			gibt eine Slist von Snodes aus (die Label)		*/
/*								                */
/*	void 		my_print_sgraph		(Sgraph)			*/
/*	void 		mein_print_sgraph	(Sgraph)			*/
/*                      zwei Ausgabeprozeduren fuer verschiedene Anwendungen	*/
/*								                */
/*	void 		print_cliquenmenge	(Sgraph,Slist cliquenmenge)	*/
/*                      gibt eine Slist von Mengen aus. 			*/
/*								                */
/********************************************************************************/
/*   Wegen verwendtenen 'gcc'-compiler notwendig:				*/
/*   								                */
/*	Attributes	make_attr	 	(va_alist)			*/
/*        		Der 'gcc'-Compiler kann make_attr nicht korrekt unter   */
/*			seinem Namen ausfuehren.Daher wurde hier diese Funktion */
/* 			unter anderne Namen aber kopiert.			*/
/*			bei Aenderungen von 'make_attr, muss dies hier auch	*/
/*			geaendert werden.					*/
/*								                */
/********************************************************************************/
/*   Eigene mein_malloc-Ueberwachung:						*/
/*      Kontroliert die Fehlermeldung von 'mein_malloc' bzw. 'free'		*/
/*								                */
/*	void 		free_all		()				*/
/*								                */
/*	char* 		mein_mein_malloc		(int groesse)		*/
/*								                */
/*	void		mein_free		(char* zeiger)			*/
/*								                */
/********************************************************************************/
/*   Initialisierungsprozeduren:						*/
/*								                */
/*	int 		anzahl_nodes		(Sgraph)			*/
/*			Es wird die Knotenanzahl des Sgraphen zurueckgegeben	*/
/*								                */
/*	void		init_sgraph		(Sgraph)			*/
/*			initialisiert Sgraph : Die Attributes-Felder der Snodes */
/*			werden auf '0' gesetzt. 'anzahl_nodes' und 		*/
/*			'anzahl_edges' werden berechnet.			*/
/*			Die 'nr'-Felder der Snodes werden von 1 beginnend fort- */
/*			laufend nummeriert.					*/
/*								                */
/*         void       init2_sgraph(sgraph)                                      */
/*								                */
/********************************************************************************/
/*    sonstiges:								*/
/*								                */
/*	bool 		slists_identical	(Slist,Slist)			*/
/*                      prueft ob beide Slists identische Elemente beinhalten	*/
/*								                */
/********************************************************************************/


#include <std.h>
#include <sgraph.h>
#include <slist.h>
#include <graphed.h>
#include <stdarg.h>
#include "menge.h"
#include "my_misc.h"
#include "default.h"


#define vereinigung(a,b)    add_slists(a,b)
/*#define obschnitt(a,b)       slist_intersects_slist(a,b)*/
#define anhaengen(a,b)       add_to_slist(a,make_attr(ATTR_DATA,(char*)b))  
#define neue_liste(a)        new_slist(make_attr(ATTR_DATA,(char*)a))
#define dattrs(a)            (a->attrs.value.data)
#define lattrs(a)            ((Slist)(a->attrs.value.data))
#define sattrs(a)            ((Snode)(a->attrs.value.data))
#define eattrs(a)            ((Sedge)(a->attrs.value.data))
#define entferne_from_slist(a,b)                                subtract_from_slist(a,make_attr(ATTR_DATA,b))
#define entferne_sofort_from_slist(a,b)                   subtract_immediately_from_slist(a,make_attr(ATTR_DATA,b))
#define entferne(a,b)        subtract_immediately_from_slist(a,b)
#define empty_attr           (make_attr(ATTR_DATA,NULL))
#define make_dattr(a)        (make_attr(ATTR_DATA,a))
#define make_flattr(a)       (make_attr(ATTR_FLAGS,a))
#define echte_vereinigung(a,b)  add_slists(a,b)


extern Slist malloc_slist;
extern int groesse_graph;
extern int my_node_type;
extern int anzahl_kanten;
extern bool finde_minbw_durch_max_clique;


extern Slist separatorenliste;
extern Slist blattliste;
extern int fortschrittzaehler;
extern void   baue_baumzerlegung_aus_listen(Sgraph sgraph);
extern void print_daten(Sgraph sgraph);



extern Attributes	make_attr (Attributes_type, ...);
extern int anzahl_nodes(Sgraph sgraph);
extern void init_sgraph(Sgraph sgraph);
extern  void init_sgraph_fuer_manipulate_treedec(Sgraph sgraph);
extern void mein_print_sgraph(Sgraph sgraph);
extern bool slists_identical (Slist menge1, Slist menge2);
extern char* mein_malloc(int groesse);
extern int   min_baumweite,max_baumweite;
extern Sgraph konvert_listen(Sgraph sgraph);

Attributes	make_attr (Attributes_type, ...);

extern void my_print_sgraph(Sgraph sgraph);
extern void mein_free(/* char *zeiger */); /* kein parameter damit keine
                                              warnings kommen ;-) */
extern void print_cliquenmenge(Sgraph sgraph, Slist cliquenmenge);

#endif
