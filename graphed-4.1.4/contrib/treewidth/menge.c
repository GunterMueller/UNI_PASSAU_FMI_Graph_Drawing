
#include "menge.h"
#include "mystd.h"

#define sattrs(a)  ((Snode)(a->attrs.value.data))

extern groesse_graph;
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
/*  !!!!!!!!!  Wichtig !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!    */
/*  Um mit einer Menge auf einem Sgraph arbeiten zu keonen muss      */
/*  die globale Varialble groesse_graph auf die Knotenzahll der      */
/*  Sgraphen gesetzt sein. Ausserdem muessen die nr-Feder der Snodes */
/*  durchgehend nummeriert sein und duerfen sich nicht aendern.      */
/*                                                                   */
/* globale Prozeduren:                                               */
/*   Menge new_menge()                                               */
/*         gibt eine neue leere menge zurueck                        */
/*         benuetzt 'mein_malloc'                                         */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge copy_menge(menge)                                         */
/*         kopiert eine Menge                                        */
/*         benuetzt 'mein_malloc'                                         */
/*                                                                   */
/*********************************************************************/
/*                                                                   */
/*   Menge copy_in_menge(menge1,menge2)                              */
/*         kopiert eine menge1 in eine bereits existierende menge2   */
/*         benoetigt kein 'mein_malloc' und spart somit Rechenzeit        */
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
/*        markiert (setzt 'attrs.value.integer'=1) die Snode aus Sgraph,     */
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
/*         baut eine neue Slist auf und benuetzt dafuer 'mein_malloc'     */
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


/*********************************************************************/
/*                             new_menge                             */
/*********************************************************************/
Menge new_menge(void)
{int i;
 Menge menge=(Menge)mein_malloc(groesse_graph/mgroesse+1);
 for (i=0;i<=groesse_graph/mgroesse;i++)
    {menge[i]=0;}
 return menge;}
/*********************************************************************/
/*                            copy_menge                             */
/*********************************************************************/
Menge copy_menge(Menge menge1)
{int i;
 Menge menge4=new_menge();
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {menge4[i]=menge1[i];}
 return menge4;}
/*********************************************************************/
/*                          copy_in_menge                            */
/*********************************************************************/
Menge copy_in_menge(Menge menge1, Menge menge2)
{int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {menge2[i]=menge1[i];}
 return menge2;}
/*********************************************************************/
/*                            empty_menge                            */
/*********************************************************************/
Menge empty_menge(Menge menge)
{int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
    {menge[i]=0;}
 return menge;}
/*********************************************************************/
/*                         mengenschnitt                             */
/*********************************************************************/
Menge mengenschnitt(Menge menge1, Menge menge2)
{int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {menge1[i]=menge1[i]&menge2[i];}
 return menge1;}
/*********************************************************************/
/*                     Groesse des  mengenschnittes                  */
/*********************************************************************/
int size_of_mengenschnitt(Sgraph sgraph, Menge menge1, Menge menge2)
{int size=0;
 Snode snode;
 for_menge(menge1,snode)
   {if ( menge2[(snode->nr)/mgroesse]&1<<((snode->nr)%mgroesse) )
       {size++;}
   }end_for_menge(menge1,snode)
 return size;}
/*********************************************************************/
/*                          mengenverienigung                        */
/*********************************************************************/
Menge mengenvereinigung(Menge menge1, Menge menge2)
{int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {menge1[i]=menge1[i]|menge2[i];}
 return menge1;}
/*********************************************************************/
/*                disjunkte mengenvereinigung                        */
/*********************************************************************/
Menge add_mengen_disjoint(Menge menge1, Menge menge2)
{int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {menge1[i]=menge1[i]^menge2[i];}
 return menge1;}
/*********************************************************************/
/*                          mengen groesse                           */
/*********************************************************************/
int size_of_menge(Sgraph sgraph, Menge menge)
{int size=0;
 Snode snode;
for_all_nodes(sgraph,snode)
   {if (((menge[(snode->nr)/mgroesse])&(1<<((snode->nr)%mgroesse)))!=0)
        {size++;}
   }end_for_all_nodes(sgraph,snode)
/* message("\n groese %i von:",size);printmenge(sgraph,menge); */
   return size;}
/*********************************************************************/
/*                      Snode in Menge einfuegen                     */
/*********************************************************************/
Menge add_to_menge(Menge menge, Snode snode)
{menge[(snode->nr)/mgroesse]=menge[(snode->nr)/mgroesse]|1<<((snode->nr)%mgroesse);
return menge;}
/*********************************************************************/
/*                      Snode aus menge entfernen                    */
/*********************************************************************/
Menge entferne_from_menge(Menge menge, Snode snode)
{menge[(snode->nr)/mgroesse]=menge[(snode->nr)/mgroesse]&(255-(1<<((snode->nr)%mgroesse)));
return menge;}
/*********************************************************************/
/*                   Slist in Menge uberfuehren                      */
/*********************************************************************/
Menge baue_menge_aus_slist(Slist slist)
{Menge menge=new_menge();
 Slist element;
 for_slist(slist,element)
    {menge[(sattrs(element)->nr)/mgroesse]=
    menge[(sattrs(element)->nr)/mgroesse]|1<<((sattrs(element)->nr)%mgroesse);
    }end_for_slist(slist,element)
return menge;}
/*********************************************************************/
/*                     baue Adijazensmenge                           */
/*********************************************************************/
Menge make_adjmenge(Snode snode)
{Menge menge=new_menge();
 Sedge sedge;
 for_sourcelist(snode,sedge)
    {menge[(sedge->tnode->nr)/mgroesse]=
                 menge[(sedge->tnode->nr)/mgroesse]|1<<((sedge->tnode->nr)%mgroesse);
    }end_for_sourcelist(snode,sedge)
return menge;}
/*********************************************************************/
/*                    pruefe ob mengen identisch                     */
/*********************************************************************/
bool mengengleich (Menge menge1, Menge menge2)
{bool gleich =TRUE;
int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {if(menge1[i]!=menge2[i])
      {gleich=FALSE; break; } }
return gleich;}
/*********************************************************************/
/*                       pruefe auf Teilmenge                        */
/*********************************************************************/
bool menge_teilmenge_von_menge(Menge menge1, Menge menge2)
{bool rueck=TRUE;
 int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {if(menge1[i]!=(menge1[i]&menge2[i]))
      {rueck=FALSE; break; } }
 return rueck;
}
/*********************************************************************/
/*                       pruefe auf echte Teilmenge                  */
/*********************************************************************/
bool menge_echte_teilmenge_von_menge(Sgraph sgraph, Menge menge1, Menge menge2)
{bool rueck=TRUE;
 int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {if(menge1[i]!=(menge1[i]&menge2[i]))
      {rueck=FALSE; break; } }
 if(size_of_menge(sgraph,menge1)==size_of_menge(sgraph,menge2))
      {rueck=FALSE;  } 
 return rueck;
}
/*********************************************************************/
/*                       pruefe auf Schnitt                          */
/*********************************************************************/
bool mengenobschnitt(Menge menge1, Menge menge2)
{bool obschnitt= FALSE;
   int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {if((menge1[i]&menge2[i])!=0)
         {obschnitt=TRUE;break;}}
 return obschnitt;}
/*********************************************************************/
/*                         Print menge                               */
/*********************************************************************/
void printmenge(Sgraph sgraph, Menge menge)
{Snode snode;
 for_all_nodes(sgraph,snode)
    {if(menge[(snode->nr)/mgroesse]&1<<((snode->nr)%mgroesse))
        {message(" %s",snode->label);}
    }end_for_all_nodes(sgraph,snode)
}
/*********************************************************************/
/*********************************************************************/
/*                   subtrahiere Menge von Menge                     */
/*********************************************************************/
Menge menge_ohne_menge(Menge menge1, Menge menge2)
{int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {menge1[i]=menge1[i]&(255-menge2[i]);}
 return menge1;}
/*********************************************************************/
/*                    markiere Menge in Sgraph                       */
/*********************************************************************/
void mark_menge(Sgraph sgraph, Menge menge)
{Snode snode;
 for_menge(menge,snode)
    {snode->attrs=make_attr(ATTR_FLAGS,1);
    }end_for_menge(menge,snode)
}
/*********************************************************************/
/*                   loesche markierungen in Sgraph                  */
/*********************************************************************/
void unmark_sgraph(Sgraph sgraph)
{Snode snode;
 for_all_nodes(sgraph,snode)
    {snode->attrs=make_attr(ATTR_FLAGS,0);
    }end_for_all_nodes(sgraph,snode)
}
/*********************************************************************/
/*                     pruefe ob Snode in Menge                      */
/*********************************************************************/
bool snode_in_menge(Snode snode, Menge menge)
{return (menge[(snode->nr)/mgroesse]&1<<((snode->nr)%mgroesse));}

/*********************************************************************/
/*                   baue Menge aus Sgraph                           */
/*********************************************************************/
Menge make_menge_of_teilgraph(Sgraph sgraph)
{Snode snode;
 Menge menge=new_menge();
 for_all_nodes(sgraph,snode)
    {add_to_menge(menge,snode->iso);
    }end_for_all_nodes(sgraph,snode)
 return menge;
}
 /*********************************************************************/
/*                   free_slist_von_mege                            */
/*********************************************************************/
void free_slist_of_menge(Slist slist)
{Slist element;
 for_slist(slist,element)
    {mein_free(mattrs(element));
    }end_for_slist(slist,element)
 free_slist(slist);
}
/*********************************************************************/
/*        kopiere inhalt der zeiger einer Menge in eine Slist      */
/*********************************************************************/

Slist save_menge(Sgraph sgraph, Menge menge)
{Slist neue_menge;
 Snode snode2;
neue_menge=empty_slist;
for_menge(menge,snode2)
       {neue_menge=anhaengen(neue_menge,snode2);
   }end_for_menge(menge,snode2)
return neue_menge;}

/*********************************************************************/
/*                     pruefe  Menge leer ist                        */
/*********************************************************************/
bool ist_menge_leer(Menge menge)
{int i;
 for (i=0;i<=groesse_graph/mgroesse;i++)
   {if (menge[i]!=0) {return false;} }
 return true;}

