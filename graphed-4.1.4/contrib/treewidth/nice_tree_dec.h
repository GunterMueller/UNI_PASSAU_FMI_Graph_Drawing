/*****************************************************************************/
/*                                                                           */
/*          N I C E  -  T R E E  -  D E C O M P O S I T I O N                */
/*                                                                           */
/* Modul        : nice-tree-dec.h					     */
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
/*         void       nice_tree_decomp   (Sgraph_proc_info)                  */
/*		      baut aus der aktuellen Baumzerlegung eine 	     */
/*		      'nice-tree-decomposition'.			     */
/*		      'nice-tree-decomposition' wird von Bodlaender in	     */
/*    		      defineiert.					     */
/*		      eine 'nice...' ist eine Baumzerlegung die folgeneden   */
/*		      Eigenschaften genuegt :   			     */
/*		      Jeder Knoten hat hoechstens zwei Nachfolger.	     */
/*		      hat zwei Nachfolger, so sind diese identisch mit ihm   */
/*		      selbst.						     */
/*		      Besitzt er einen Nachfolger so unterscheiden sich beide*/
/*		      in genau einem Label.				     */	
/*								             */
/*****************************************************************************/
#ifndef nice_tree_dec_h
#define nice_tree_dec_h

#include "mystd.h"

extern void nice_tree_decomp(Sgraph_proc_info info);

#endif
