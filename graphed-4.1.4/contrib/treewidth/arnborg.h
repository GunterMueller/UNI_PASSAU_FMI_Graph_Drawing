#ifndef arnborg_h
#define arnborg_h

/*****************************************************************************/
/*                 A R N B O R G  -  A L G O R I T H M U S                   */
/*                                                                           */
/* Modul        : arnborg.h                                                  */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

#include "mystd.h"

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*      Sgraph   arnborg                           (Slist)                   */
/*		 Kontrollprozedur fuer den Arnborgalgorithmus		     */
/*		 Slist enhaelt die Menge der zu bearbeiteneden Graphen	     */
/*								             */
/*****************************************************************************/

extern Sgraph tree_decomp(Slist teilgraphen);
/*extern void zeige_zhk(*//*Sgraph*//*);*/

#endif
