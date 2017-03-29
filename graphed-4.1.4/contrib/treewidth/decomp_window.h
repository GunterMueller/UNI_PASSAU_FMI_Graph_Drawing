#ifndef decomp_window_h
#define decomp_window_h

/*****************************************************************************/
/*                                                                           */
/*  M A N I P U L A T E - T R E E - D E C O M P O S I T I O N - W I N D O W  */
/*                                                                           */
/* Modul        : decomp_window.h                                            */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/* Dises Fenster dient zur Steuerung der Baumzerlegungslayout veraenderungen */
/*                                                                           */
/* Aussehen :							             */
/* =======================================================================   */
/*|  V                 MANIPULATE TREE-DECOMPOSITION                     |   */
/*|=======================================================================   */
/*|                                                                      |   */
/*|   <  show  Nodecontent  >                             ---------      |   */
/*|                                           with : V    |       |      |   */
/*|   <   unmark cliquen    >                             ---------      |   */
/*|                                                                      |   */
/*|   <   Wurzel aendern    >		      <   Wurzel == Mitte  >     |   */
/*|                                                                      |   */
/*|   <   nice-tree-decomp  >		      <  min-tree-decomp   >     |   */
/*|                                                                      |   */
/*|    change label    V  Cliquen             min tree-width   2         |   */
/*| 					      max tree-width   3         |   */
/*|                                                                      |   */
/* =======================================================================   */
/*                                                                           */
/*****************************************************************************/

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	void	 decomposition_window		()			     */
/*                ruft oder baut das `manipulate-tree-decomposition-Fenster  */
/*		  auf							     */
/*								             */
/*****************************************************************************/


extern int longlabel;
extern char erstes_zulaessiges_zeichen_decomp ;
extern char letztes_zulaessiges_zeichen_decomp;

extern int label_decomp_default;
extern int my_node_type;
extern int longlabel;
extern int k;

extern void decomposition_window(void);


#endif
