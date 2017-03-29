#ifndef untere_schranke_h
#define untere_schranke_h
/*****************************************************************************/
/*                                                                           */
/*                    U N T E R E   S C H R A N K E N                        */
/*                                                                           */
/* Modul 	: untere_schranke.h					     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/*  Dieses Modul enthaelt zwei Berechnungsmethoden fuer untere Schranken     */
/*  der Baumweite von Graphen.						     */
/*                                                                           */
/*  int	 kantenbeschraenkung	(Sgraph)				     */
/*       Findet eine untere Shcranke ueber Kanten/Knotenverhaeltnis	     */
/*                                                                           */
/*  Die ueberigen Routinen berechnen einen Cliquenminor (beschrieben in	     */
/*  meiner Diplomarbeit.						     */
/*                                                                           */
/*  bool 	valenzbedingung		(Sgraph,Menge clique		     */
/*							,int max_cliquensize)*/
/*								             */
/*  int	finde_untere_schranke_durch_maxCliquenseparator (Sgraph)	     */
/*			berechne die untere Schranke der Baumweite durch     */
/*			CliquenSeparatorbedingungen (siehe hierzu Diplomarbeit) */
/*								             */
/*  Slist 	finde_max_cliquen_aus_dominanten			     */
/*					(Sgraph,Slist dominante_cliquenmenge)*/
/*								             */
/*  int finde_untere_schranke_durch_maxCliquenseparator_fuer_Cliquenheuristik*/
/*                                             (sgraph,dominante_cliquenmenge)*/
/*			wie oben, nur fuer den Fall, dass bereits alle do-   */
/*			minanten Cliquen bekannt sind.			     */
/*                                                                           */
/*****************************************************************************/
#include "mystd.h"
#include "menge.h"


extern int kantenbeschraenkung(Sgraph sgraph);
extern int finde_untere_schranke_durch_maxCliquenseparator(Sgraph sgraph);
extern Slist finde_max_cliquen_aus_dominanten(Sgraph sgraph, Slist dominante_cliquenmenge);
extern int finde_untere_schranke_durch_maxCliquenseparator_fuer_Cliquenheuristic
                                              (Sgraph sgraph, Slist dominante_cliquenmenge);
extern void finde_beste_untere_Schranke(Sgraph sgraph);





#endif
