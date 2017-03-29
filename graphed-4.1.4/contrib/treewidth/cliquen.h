#ifndef cliquen_h
#define cliquen_h

/*******************************************************************/
/*                         C L I Q U E N                           */
/*                                                                 */
/* Modul        : cliquen.h					   */
/* erstellt von : Nikolas Motte                                    */
/* erstellt am  : 10.12.1992                                       */
/*                                                                 */
/*******************************************************************/

/*******************************************************************/
/*						                   */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		   */
/*								   */
/*******************************************************************/
/*								   */
/*      Slist      berechne_cliquen      (Sgraph)                  */
/*                 gibt eine Liste aller dominaten Cliquen zurueck */
/*							 	   */
/*      Slist      maximale_cliquen      (Sgraph)                  */
/*                 gibt eine Liste aller maximaler Cliquen zurueck */
/*							 	   */
/*******************************************************************/

#include "mystd.h"

extern Slist berechne_cliquen (Sgraph sgraphw);
extern Slist maximale_cliquen(Sgraph sgraph);
extern Slist nicht_dominante_entfernen(Sgraph sgraph, Slist slist);

#endif
