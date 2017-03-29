/*****************************************************************************/
/*                                                                           */
/*          R E D U C E   T R E E  -  D E C O M P O S I T I O N              */
/*                                                                           */
/* Modul        : reduce-tree-dec.h					     */
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
/*         void       min_tree_dec       (Sgraph_proc_info)                  */
/*                    Dieser Algorithmus fasst die Label zweier		     */
/*	 	      aufeinanderfolgende Knoten zu einem einzigen zusammen, */
/*		      falls dies nicht die Baumweite erhoet.	 	     */
/*								             */
/*****************************************************************************/
#ifndef reduce_tree_dec_h
#define reduce_tree_dec_h

#include "mystd.h"

extern void min_tree_dec(Sgraph_proc_info info);
extern Snode finde_wurzel_des_Baumes(Sgraph sgraph);

#endif
