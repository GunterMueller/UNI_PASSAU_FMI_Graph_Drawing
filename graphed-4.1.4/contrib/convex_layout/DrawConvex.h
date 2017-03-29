/* (C) Universitaet Passau 1986-1994 */
/*1994-91 von Stefan Jockenhoevel und Gerd Nienhaus.*/

/*****************************************************************************************/
/*****************************************************************************************/
/**                                                                                     **/
/**                   MODULE DrawConvex.h                                               **/
/**                                                                                     **/
/*****************************************************************************************/
/*****************************************************************************************/


#ifndef DRAWCONVEX_HEADER
#define DRAWCONVEX_HEADER

/********************************************************************************/
/* Die Prozedure zeichnet den Graphen 'TheGraph' konvex falls dieses moeglich   */
/* ist. Fuer den Graphen wird vorausgesetzt, dass er 2-fach-zusammenhaengend    */
/* ist und das keine Mehrfachkanten sowie Kanten von einem Knoten auf sich      */
/* selbst auftreten. 'TheFacialCycle' ist das Ergebnis der Funktion             */
/* 'ConvexityTest'. Der Parameter 'edit' beeinflusst das Aussehen des Graphen.  */
/* Fuer 'edit = FALSE' wird der Graph konvex gezeichnet und die Knoten des      */
/* Graphen weden "unsichtbar". Ausserdem "verschwinden" die Pfeile der Kanten   */
/* bei gerichteten Graphen. D.h. also in diesem Fall besteht der Graph nur noch */
/* aus Linien, so dass die Struktur des Graphen gut zu erkennen ist.            */
/* Im Fall 'edit = TRUE' wird der Graph ebenfalls konvex gezeichnet, aber das   */
/* Aussehen der Kanten und Knoten wird nicht veraendert (nur die Kanten sind    */
/* natuerlich geradlinig).                                                      */
/********************************************************************************/

extern void ExtendFacialCycle(Sgraph TheGraph, Slist TheFacialCycle, int edit);

#include "convex_layout_export.h"

#endif
