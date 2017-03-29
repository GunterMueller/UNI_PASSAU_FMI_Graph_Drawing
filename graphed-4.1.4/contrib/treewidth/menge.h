#ifndef mengen_h
#define mengen_h

#include "mystd.h"

/*********************************************************************/
/*                                                                   */
/*                 Mengenoperationen                                 */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*    Diese Modul enthaelt eine Mengenimplementierung.               */
/*                                                                   */
/* Um moeglichst eine moeglichst schnelle Vereinigung und Schnitt-   */
/* operation zur verfuegung zu haben wurde eine Knotenmengenrepre-   */
/* sentation in Form eines Bitfeldes gewaehlt.                       */
/* Jedem Knoten eines Graphen wird hierzu ein fester Platz in der    */
/* Bitfolge zugewiesen. Dieser Platz wird durch die Knotennummmer    */
/* festgelegt. (snode->nr)                                           */
/*                                                                   */
/*                                                                   */
/* globale Prozeduren:                                               */
/*   Menge new_menge()                                               */
/*         gibt eine neue leere menge zurueck                        */
/*         benuetzt 'malloc'                                         */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge copy_menge(menge)                                         */
/*         kopiert eine Menge                                        */
/*         benuetzt 'malloc'                                         */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge copy_in_menge(menge1,menge2)                              */
/*         kopiert eine menge1 in eine bereits existierende menge2   */
/*         benoetigt kein 'malloc' und spart somit Rechenzeit        */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge empty_menge(Menge)                                        */
/*         leert eine bereits existierende Menge                     */
/*         ACHTUNG: nicht fuer Initialisierung geeignet              */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge mengenschnitt(menge1,menge2)                              */
/*         berechnet den Schnitt und gibt ihn an menge1 zurueck      */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   int   size_of_mengenschnitt(sgraph,menge1,menge2)               */
/*         berechnet die Anzahl der Elemente des Schnittes           */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge mengenvereinigung(Menge,Menge)                            */
/*         berechnet die Vereinigung und gibt ihn an menge1 zurueck  */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge add_mengen_disjoint(Menge,Menge)                          */
/*         berechnet die disjunkte Vereinigung und gibt ihn an       */
/*         menge1 zurueck                                            */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   int   size_of_menge(sgraph,menge)                               */
/*         bestimmt die Anzahl der Mengenelemente                    */ 
/*         sgraph ist notwendig anzugeben da eine for_all_nodes-     */
/*         Schleife durchlaufen wird                                 */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge add_to_menge(menge,snode)                                 */
/*         nimmt sndoe in Menge auf                                  */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge entferne_from_menge(Menge,Snode)                          */
/*         entfernt snode aus menge                                  */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge baue_menge_aus_slist(Slist)                               */
/*         transformiert eine Slist in eine Menge                    */
/*         die Attribute felder muessen dazu jeweils einen           */
/*         Snode enthalten                                           */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge make_adjmenge(Snode)                                      */
/*         bildet eine Menge aus der Adjazenz eines Snode            */
/*         (gilt fuer ungerichteten Graphen)                         */
/*         (im gerichteten Graphenm wird die sourceliste gebildet)   */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge make_menge_of_teilgraph(Sgraph)                           */
/*         bildet eine Menge aus dem Sgraphen                        */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   bool mengengleich (menge1,menge2)                               */
/*        gibt 'true' zurueck wenn menge1 identisch mit menge2 ist   */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   bool menge_teilmenge_von_menge(menge1,menge2)                   */
/*        gibt 'true' zurueck, wenn menge1 eine Teilmenge            */
/*        von menge2 ist.                                            */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   bool menge_echte_teilmenge_von_menge(sgraph,menge1,menge2)      */
/*        gibt 'true' zurueck, wenn menge1 eine echte Teilmenge      */
/*        von menge2 ist.                                            */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   bool mengenobschnitt(menge1,menge2)                             */
/*        gibt 'true' zurueck, wenn die Schnittmenge zwischen        */
/*        menge 1 und menge2 nicht leer ist.                         */                 /*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   void printmenge(Sgraph,Menge)                                   */
/*        gibt die Label der Mengenelemente aus                      */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge menge_ohne_menge(menge1,menge2)                           */
/*         Entfernt alle Elemente aus der menge2 von der menge1      */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   void mark_menge(Sgraph,Menge)                                   */
/*        markiert (setzt 'attrs.value.integer'=1) die Snode aus Sgarph,     */
/*        die Elemente der Menge sind.                               */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   void unmark_sgraph(Sgraph)                                      */
/*        setzt alle markierungen wieder zurueck                     */
/*          (snode->attrs.value.integer=0)                                   */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   bool snode_in_menge(Snode,Menge)                                */
/*        gibt 'true' zureuck wenn Snode in der Menge enthalten ist  */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   void free_slist_of_menge(Slist)                                 */
/*        loescht eine aus Menge bestehende Slist und gibt den       */
/*        Speicher wieder frei.                                      */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Slist save_menge(Sgraph,Menge)                                  */
/*         speichert die Snodeelemente einer Menge als Zeiger in     */
/*         einer Slist.                                              */
/*         baut eine neue Slist auf und benuetzt dafuer 'malloc'     */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   bool  ist_menge_leer (Menge)				     */
/*         prueft ob die Menge leer ist.			     */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/* globale Makros:                                                   */
/*                                                                   */
/*********************************************************************/
/*   free_menge(Menge)                                               */
/*         gibt den Speicheplatz einer Menge wieder frei             */        
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   for_menge(Menge,Snode) - end_for_menge(Menge,Snode)             */
/*         Schleife ueber alle Mengenelemente                        */
/*                                                                   */
/*                                                                   */
/*********************************************************************/



#define mgroesse              8
#define mattrs(a)             ((Menge)(a->attrs.value.data))
#define for_menge(a,b)        for_all_nodes(sgraph,b)                                                         {if(a[(b->nr)/mgroesse]&1<<((b->nr)%mgroesse)){
#define end_for_menge(a,b)    }}end_for_all_nodes(sgraph,b)
#define free_menge(a)         free(a)

#define add_to_mengem(menge,snode)   menge[((snode)->nr)/8]=menge[((snode)->nr)/8]|1<<(((snode)->nr)%8)
#define entferne_from_mengem(menge,snode) menge[((snode)->nr)/8]=menge[((snode)->nr)/8]&(255-(1<<(((snode)->nr)%8)))
typedef char  *Menge;


#define snode_in_mengem (menge[(snode->nr)/mgroesse]&1<<((snode->nr)%mgroesse))

extern Menge new_menge(void);
extern Menge copy_menge(Menge menge1);
extern Menge copy_in_menge(Menge menge1, Menge menge2);
extern Menge empty_menge(Menge menge);
extern Menge mengenschnitt(Menge menge1, Menge menge2);
extern int size_of_mengenschnitt(Sgraph sgraph, Menge menge1, Menge menge2);
extern Menge mengenvereinigung(Menge menge1, Menge menge2);
extern Menge add_mengen_disjoint(Menge menge1, Menge menge2);
extern int size_of_menge(Sgraph sgraph, Menge menge);
extern Menge add_to_menge(Menge menge, Snode snode);
extern Menge entferne_from_menge(Menge menge, Snode snode);
extern Menge baue_menge_aus_slist(Slist slist);
extern Menge make_adjmenge(Snode snode);
extern Menge make_menge_of_teilgraph(Sgraph sgraph);
extern bool mengengleich (Menge menge1, Menge menge2);
extern bool menge_echte_teilmenge_von_menge(Sgraph sgraph, Menge menge1, Menge menge2);
extern bool menge_teilmenge_von_menge(Menge menge1, Menge menge2);
extern bool mengenobschnitt(Menge menge1, Menge menge2);
extern void printmenge(Sgraph sgraph, Menge menge);
extern Menge menge_ohne_menge(Menge menge1, Menge menge2);
extern void mark_menge(Sgraph sgraph, Menge menge);
extern void unmark_sgraph(Sgraph sgraph);
extern bool snode_in_menge(Snode snode, Menge menge);
extern void free_slist_of_menge(Slist slist);
extern Slist save_menge(Sgraph sgraph, Menge menge);

extern bool ist_menge_leer(Menge menge);


#endif
