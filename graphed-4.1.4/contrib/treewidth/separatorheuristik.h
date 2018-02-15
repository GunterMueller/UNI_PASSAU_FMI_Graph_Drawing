#ifndef separatorheuristik_h
#define separatorheuristik_h

/*****************************************************************************/
/*                                                                           */
/*                S E P A R A T O R H E U R I S T I K 			     */
/*                                                                           */
/* Modul	: separatorheuristik.c					     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	Sgraph	 separatorheuristic 		(Sgraph)		     */
/*		 Kontrollprozedur fuer die Separatorheuristik		     */
/*								             */
/*****************************************************************************/
/*								             */
/*	Slist 	graph_durch_cliquen_aufspalten  (Sgraph,Menge cliquenmenge)  */
/*		spaltet eiene Sgraphen wenn moeglich durch die Cliquen als   */
/*		Separatoren in Teilgraphen auf. Slist enthaelt eine Liste    */
/*		der gefundenen Teilgraphen.				     */
/*		Kann der Graph nicht aufgespaltet werden wird empty_slist    */
/*		zurueckgegeben.						     */
/*								             */
/*****************************************************************************/
/*								             */
/*      Slist  pruefe_auf_separator (sgraph,Menge sep)			     */
/*		pruefe ob die Menge sep einen Separator darstellt.	     */
/*		return `empty_slist' wenn nicht.			     */
/*	        sonst gebe Slist der Zusammenhangskomponenten zurueck        */
/*		 (Sgraphen nicht Knotenmengen)				     */
/*								             */
/*****************************************************************************/

#include "mystd.h"
#include "menge.h"
#include "notice.h"
#include "cliquen.h"
#include "mainwindow.h"

extern Sgraph separatorheuristik(Slist teilgraphen);
extern Slist pruefe_auf_separator(Sgraph sgraph, Menge sep);
extern Slist graph_durch_cliquen_aufspalten(Sgraph sgraph, Slist cliquenmenge);


#endif
