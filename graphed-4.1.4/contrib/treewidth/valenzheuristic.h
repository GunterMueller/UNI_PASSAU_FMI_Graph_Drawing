#ifndef randseparatorheuristik_h
#define randseparatorheuristik_h

/*****************************************************************************/
/*                                                                           */
/*                       V A L E N Z H E U R I S T I K			     */
/*                                                                           */
/* Modul	: valenzheuristic.c					     */
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
/*	Sgraph	 valenzheuristic 			(Sgraph)	     */
/*		 Kontrollprozedur fuer die Valenzheuristik		     */
/*								             */
/*****************************************************************************/

#include "mystd.h"
#include "menge.h"
#include "notice.h"
#include "cliquen.h"
#include "mainwindow.h"

extern Sgraph valenzheuristic(Slist teilgraphen, Sgraph sgraph);
extern Sgraph reduziere_sgraph(Sgraph sgraph);


#endif
