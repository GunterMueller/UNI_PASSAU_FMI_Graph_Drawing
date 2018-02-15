/*****************************************************************************/
/*                      C L I Q U E N H E U R I S T I K                      */
/*                      K A N T E N H E U R I S T I K                        */
/*                                                                           */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

#ifndef heuristic_h
#define heuristic_h

#include "mystd.h"
#include "menge.h"
#include "cliquen.h"
#include "mainwindow.h"

/*     Flag ob hilfsgraph vorbehandelt werden soll */
extern bool obaufspalten;

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*      Sgraph     baumzerlegung              (Slist)                        */
/*                 gibt eine zulaessige Baumzerlegung des Graphen zurueck    */
/*                 legt die Baumweite in den globalen Variablen              */
/*                 min_baumweite und max_baumweite ab.                       */
/*                 abhaengig von der globalen Variable 'algorithmen'         */
/*		   wird entweder die Cliquen- oder die Kantenheuristik ver-  */
/*		   wendet.						     */
/*		 Slist enhaelt die Menge der zu bearbeiteneden Graphen	     */
/*							 	             */
/*****************************************************************************/

extern Sgraph baumzerlegung(Slist teilgraphen);
/*extern void graph_reduzieren();*/
/*extern Sgraph reduziere_graph();*/

#endif
