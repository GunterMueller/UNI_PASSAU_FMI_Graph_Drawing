#include "mystd.h"
/**************************************************************************/
/*                 A l l g e m e i n e   P r o z e d u r e n              */
/*                                                                        */
/* Modul        : my_misc.c			  		          */
/* erstellt von : Nikolas Motte                                           */
/* erstellt am  : 10.12.1992                                              */
/*                                                                        */
/*   Enthaelt alle default-werte fuer die Panel-items und allgemeine      */
/*   Variablen								  */
/*                                                                        */
/**************************************************************************/
char erstes_zulaessiges_zeichen_decomp='a' ;
char letztes_zulaessiges_zeichen_decomp='z';
char erstes_zulaessiges_zeichen_graph='a' ;
char letztes_zulaessiges_zeichen_graph='z';
int label_graph_default=1;  /* a...z   */
int label_decomp_default=3;   /* cliquen   */
bool longlabel=true;
int my_node_type=0;
int max_laenge_label=50;
int hashgroesse = 300000;

