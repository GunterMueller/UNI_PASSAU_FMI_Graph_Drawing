/*******************************************************************/
/*                         C L I Q U E N                           */
/*                                                                 */
/* Modul        : cliquen.c					   */
/* erstellt von : Nikolas Motte                                    */
/* erstellt am  : 10.12.1992                                       */
/*                                                                 */
/*******************************************************************/

#include "mystd.h"
#include "cliquen.h"
#include "mainwindow.h" /* um Algorithmus fortschritt auszugeben */

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
/*******************************************************************/
/*						                   */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		   */
/*								   */
/*******************************************************************/
/*								   */
/*     Slist       sortiere_slist        (Sgraph,Slist)            */
/*                 sortiert eine Liste von Mengen aufsteigend nach */
/*                 Anzahl der Mengenelemente.                      */
/*							 	   */
/*     Slist       nicht_dominante_entfernen  (Sgraph,Slist)       */
/*                 entfernt aus einer Liste von Cliquen die nicht  */
/*                 dominanten Cliquen.                             */
/*							 	   */
/*******************************************************************/

  

/*******************************************************************/
/*         sortiere slist nach groesse ihrer Mengenelemente        */
/*******************************************************************/
/*								   */
/* benoetigtee Prozeduren : menge_teilmenge_von_menge              */
/* aufrufende Prozedur    : finde_dominante_cliquen                */
/*								   */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                    */
/*              Slist slist  : eine sotierte Slist von Mengen.     */
/*                             die Mengen enthalten jeweils eine   */
/*                             Clique (Menge)                      */
/*                                                                 */
/* Rueckgabeparameter :   Slist die sorierte Liste                 */  
/*                                                                 */
/*******************************************************************/
/* Verfahren:                                                      */
/*     Da die Groesse der maximalen Clique durch die Graphen-      */
/*     groesse betimmt ist, findet BUCKET-SORT anwendung           */
/*     fuer jede moegliche Cliquengroesse wird eine Slist aufgebaut*/
/*     die Slist wird eimal durchlaufen und ihre Elemente          */
/*     entsprechend ihrer groesse in die zugehoerige Slist         */
/*     eingetragen.                                                */
/*     Anschliesend werden die einzelne Slisten aufsteigend        */
/*     in eine neue Slist kopiert                                  */
/*                                                                 */
/* Laufzeit : O(Graphgroesse)                                      */
/*******************************************************************/

Slist sortiere_slist(Sgraph sgraph, Slist slist)
{
/*** Slist feld[groesse_graph+1]; */
Slist *feld =(Slist*)mein_malloc(sizeof(Slist*)*groesse_graph+1);
 int i;
 Slist element;
 Slist  sort_slist=empty_slist;
 Menge menge;
 int groesse;

 /****** initialisiere Slist ******/
 for(i=0;i<=groesse_graph;i++)
    {feld[i]=empty_slist; }


 for_slist(slist,element)
        {menge=mattrs(element);groesse=size_of_menge(sgraph,menge);
         feld[groesse]=anhaengen(feld[groesse],menge);
    }end_for_slist(slist,element)
 

 for(i=1;i<=groesse_graph;i++)
  {for_slist(feld[i],element)
      {sort_slist=anhaengen(sort_slist,mattrs(element));
          /*zeiger soll immer auf erstes=kleinstes element zeigen,damit
          richtig angehaengt wird.*/
     }end_for_slist(feld[i],element)
   }
 free_slist(slist);
 return sort_slist;
}


/*******************************************************************/
/*              entferne nicht dominante cliquen                   */
/*******************************************************************/
/*                                                                 */
/* benoetigtee Prozeduren : menge_teilmenge_von_menge              */
/*                          sortiere_slist                         */
/*                                                                 */
/* aufrufende Prozedur    : finde_dominante_cliquen                */
/*                                                                 */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                    */
/*              Slist slist  : eine Slist von Mengen.              */
/*                                                                 */
/* Rueckgabeparameter :                                            */
/*            Slist : die dereinigte dominante Cliquenmenge        */           
/*                                                                 */
/*******************************************************************/
/* Verfahren und Ziel:                                             */
/*  aus der Menge aller im letzten Schritt gefundnenen Cliquen     */
/*  sollen di nichtdominanten Cliquen entfernt werden.             */
/*                                                                 */
/*  Dazu werden die gefundenen Cliquen der groesse nach sortiert   */
/*  und anschliessend beginnend mit der kleinsten nach obermengen  */
/*  durchsucht. Ist eine solche Obermenge gefunden wird die Clique */
/*  aus der Liste entfernt.                                        */
/*******************************************************************/

Slist nicht_dominante_entfernen(Sgraph sgraph, Slist slist)
{Slist slist2,slist3;
 Slist nicht_dominant=empty_slist;
 slist=sortiere_slist(sgraph,slist);
 for_slist(slist,slist2)
         {slist3=slist2;
          while((slist3=slist3->suc)!=slist)
            {
             if (menge_teilmenge_von_menge(dattrs(slist2),dattrs(slist3)))
                {nicht_dominant=anhaengen(nicht_dominant,dattrs(slist2)); 
                 break;}
            }
         }end_for_slist(slist,slist2)
 return subtract_slists(slist,nicht_dominant);
}

/********************************************************************/
/*          finde cliquen ...                                       */
/********************************************************************/
/*                                                                  */
/* benoetigtee Prozeduren : nicht_dominante_entfernen,              */
/*                          mengenobschnitt,mengenschnitt           */
/*                          free_slist_of_menge                     */
/*                                                                  */
/* aufrufende Prozeduren  : diverse                                 */
/*                                                                  */
/* Rueckgabeparameter :                                             */
/*            Slist : eine Liste der dominanten Cliquen             */     
/*                                                                  */
/********************************************************************/
/* Verfahren :                                                      */
/*            von Paul und Ungerer                                  */
/*    beginne mit erstem Knoten. Dieser bildet eine Clique          */
/*    betrachte induktiv neuen Knoten. Betrachte den Schnitt seiner */
/*    Adjzentzmenge mit jeder einzelnen bereits gefundenen Clique   */
/*    Sind alle Schnittmengen leer so bildet der Knoten eine neue   */
/*    Clique.                                                       */
/*    Ansonsten bildet jede Schnittmenge vereinigt mit dem Knoten   */
/*    eine neue Clique.                                             */
/*    entferne nun Cliquen die rezessiv zu anderen sind.            */
/*    Fahre mit naechstem Knoten fort.                              */
/********************************************************************/


Slist berechne_cliquen (Sgraph sgraphw)
{Sgraph sgraph;
 Snode node,nodew;
 Slist cliquenmenge,zugehoerig,NeueClMenge,clique3;
 Menge  clique,llliste,adjmenge,schnittlist;
 bool zusammenhang;
 int knotennr=0;

 init_fortschritt("computing_cliquen...",groesse_graph);
 /*message("computing_cliquen...\n");*/
 cliquenmenge=empty_slist;
 for_all_nodes(sgraphw,nodew)
    {knotennr++;
     fortschritt(knotennr);
     sgraph=sgraphw;node=nodew;
     adjmenge=make_adjmenge(node);
     NeueClMenge=empty_slist;      
     zusammenhang=false;
     zugehoerig=empty_slist;
     for_slist(cliquenmenge,clique3)
         {clique=(Menge)clique3->attrs.value.data;
          if (mengenobschnitt(clique,adjmenge))
             {schnittlist=mengenschnitt(copy_menge(clique),adjmenge);
              if (mengengleich(schnittlist,clique) ) 
                  /* wenn gleich=> alte clique in neuer enthalten=> entfernen*/
                  {zugehoerig= anhaengen(zugehoerig,clique);}
              NeueClMenge=anhaengen(NeueClMenge,add_to_menge(schnittlist,node));
              zusammenhang=true;
             }  		 	              
          }end_for_slist(cliquenmenge,clique3) 
     if(zusammenhang==false)     /*Knoten ist bisher isoliert=>neue Clique*/
          { llliste=add_to_menge(new_menge(),node); 
            NeueClMenge=anhaengen(NeueClMenge,llliste);
          }      
     NeueClMenge=nicht_dominante_entfernen(sgraph,NeueClMenge);
     cliquenmenge =vereinigung(cliquenmenge,NeueClMenge);
     cliquenmenge =subtract_slists(cliquenmenge,zugehoerig); 
     mein_free(adjmenge);free_slist(NeueClMenge);
     free_slist_of_menge(zugehoerig);
    }end_for_all_nodes(sgraphw,nodew)
 end_fortschritt(); 
 return sortiere_slist(sgraph,cliquenmenge);
}


/********************************************************************/
/*          finde maximale  cliquen ...                             */
/********************************************************************/
/*                                                                  */
/* benoetigte Prozeduren  : berechne_cliquen,                       */
/*                          for_slist - end_for_slist               */
/*                          free_slist_of_menge                     */
/*                                                                  */
/* aufrufende Prozeduren  : main_algorithmen                        */
/*                                                                  */
/* Rueckgabeparameter :                                             */
/*            Slist : eine Liste der maximalen Cliquen              */     
/*                                                                  */
/********************************************************************/
/* Verfahren:                                                       */
/*   Die Cliquenmenge wird zweimal durchlaufen.                     */
/*   beim ersten Durchlauf wird die maximale Cliquengroesse bestimmt*/
/*   und beim zweiten alle maximalen Cliquen nach max_cliquenmenge  */
/*   kopiert                                                        */
/*                                                                  */
/********************************************************************/

Slist maximale_cliquen(Sgraph sgraph)
{
 Slist dominante_cliquenmenge,max_cliquenmenge=empty_slist;
 int max_size=0;
 Slist element;

 dominante_cliquenmenge=berechne_cliquen(sgraph);
 /*Liste ist sortiert und letztes Element ist das gr"osste Element */
 max_size=size_of_menge(sgraph,mattrs(dominante_cliquenmenge->pre));
 for_slist(dominante_cliquenmenge,element)
           {
	   if(max_size==size_of_menge(sgraph,mattrs(element) ))
              {max_cliquenmenge=anhaengen(max_cliquenmenge,copy_menge(mattrs(element)));
              }
           }end_for_slist(dominante_cliquenmenge,element)
      
free_slist_of_menge(dominante_cliquenmenge);
return max_cliquenmenge;
}

