/*****************************************************************************/
/*                                                                           */
/*                      C L I Q U E N H E U R I S T I K                      */
/*                      K A N T E N H E U R I S T I K                        */
/*                                                                           */
/* Modul 	: heuristic.c						     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

#include "heuristic.h"
#include "untere_schranke.h"
#include "my_misc.h"
#include "math.h"  /*fuer sqrt in Knatenbeschraenkung*/

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*      Sgraph     baumzerlegung              (Sgraph)                       */
/*                 gibt eine zulaessige Baumzerlegung des Graphen zurueck    */
/*                 legt die Baumweite in den globalen Variablen              */
/*                 min_baumweite und max_baumweite ab.                       */
/*                 abhaengig von der globalen Variable 'algorithmen'         */
/*		   wird entweder die Cliquen- oder die Kantenheuristik ver-  */
/*		   wendet.						     */
/*							 	             */
/*****************************************************************************/
/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*     void       print_hgraph           (Sgraph,Hgraph)                     */
/*                gibt die hgraph Struktur zu Kontrollzwecken                */
/*                an Stdout aus.                                             */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Verwaltung der Hgraphdatenstrucktur:                       */
/*								             */
/*     Hgraph     new_hgraph             (clique)                            */
/*                initialisert eine Hgraphstrucktur unter Verwendung         */
/*                von 'mein_malloc'.                                         */
/*								             */
/*     void       free_hnode             (hnode)                             */
/*                gibt Hgraph und seine Unterstruckturen wieder  frei.       */
/*								             */
/*     Hgraph     add_to_hgraph          (Hgraph,clique)                     */
/*                setze Zeiger fuer neues Hgraphelement um                   */
/*								             */
/*     void       unmark_hgraph          (hgraph)                            */
/*                loescht markierungen im gesamten Hgraph                    */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Verwaltung der Helperdatenstrucktur:                       */
/*								             */
/*     Helper     new_helper             (Menge,alt)                         */
/*                initialisert eine Helperstrucktur unter Verwendung         */
/*                von 'mein_malloc'.                                         */
/*		  						             */
/*     Helper     add_to_helper          (Helper,Menge,alt)                  */
/*                setze Zeiger fuer neues Helperelement um                   */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Verwaltung der Adjlistdatenstrucktur:                      */
/*								             */
/*     Adjlist    new_adjlist            (hnode1,hnode2)                     */
/*                initialisert eine Adjliststrucktur unter Verwendung        */
/*                von 'mein_malloc'.                                         */
/*								             */
/*     void       add_edge_to_hgraph     (hnode1,hnode2)                     */
/*                setze Zeiger fuer neues Adjlistelement um                  */
/*								             */
/*     void       entferne_kante       (Hgraph,Hgraph knoten1,Hgraph knoten2 */
/*                entfernt eine Kante aus der Adjliststrucktur               */
/*								             */
/*     Hgraph     remove_hnode           (Hgraph,Hgraph hnode)               */
/*                entfernt einen Knoten aus der Hgraphtstrucktur und         */
/*                alle zugehoerigen Kanten.                                  */
/*								             */
/*     void       kante_umhaengen        (hnode1,hnode2,hnode3)              */
/*                entfernt eine Knate zwischen hnode1 und hnode2 und         */
/*                setzt zwischen hnode1 und hnode3 eine neue.                */
/*								             */
/*     Adjlist    subtract_from_adjlist  (adjlist2,hnode2)                   */
/*                ???????????????????????????????????????????                */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Konvertierung  Hgraph->Sgraph:                             */
/*								             */
/*     int        dfs                    (Hgraph,int tiefe)                  */
/*								             */
/*     void       baue_baum           (Sgraph,Hgraph,Snode,Sgraph new_sgraph)*/
/*								             */
/*     Hgraph     finde_wurzel           (Hgraph)                            */
/*								             */
/*     Sgraph     baue_sgraph_aus_hgraph (Sgraph,Hgraph)                     */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Erzeugung und berechnung des Hgraphen:                     */
/*								             */
/*     Hgraph     baue_hilfsgraph        (Sgraph, Slist cliquenmenge)        */
/*                erzeugt den Hgraph aus der cliquenliste (bzw Knatenliste)  */
/*								             */
/*     void       dfs_graph              (Hgraph)                            */
/*								             */
/*     Hgraph     aufspalten             (Sgraph,Hgraph)                     */
/*								             */
/*     Hgraph     kreis_aufschneiden     (Sgraph,Hgraph,Hgraph hnode2,hnode1)*/
/*                erkennt einenn von 'zerlege' gefundene Kreis und           */
/*                loescht die fuer die Baumweite lokal guenstigste Kante     */
/*								             */
/*     Hgraph     zerlege                (Sgraph,Hgraph)                     */
/*                sucht nach Kreisen im Hgraph und entfernt die lokal        */
/*                guenstigsten Kanten.                                       */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Heurisikablaufsteuerung:                                   */
/*								             */
/*     void       enferne_self_loops     (Sgraph)                            */
/*								             */
/*     int        baumweite              (Sgraph,Hgraph)                     */
/*								             */
/*     int        groesste_slist         (Sgraph,Slist)                      */
/*								             */
/*     int        kantenbeschraenkung    (Sgraph)                            */
/*								             */
/*     Slist      berechne_knatenmenge   (Sgraph)                            */
/*								             */
/*     Hgraph     berechne_baumbreite    (Sgraph)                            */
/*								             */
/*							 	             */
/*****************************************************************************/

/*****************************************************************************/
/*             Makros fuer die Hgraphdatenstrucktur                          */
/*****************************************************************************/

#define empty_hgraph         ((Hgraph)NULL)               /*    NULL         */
#define for_hgraph(a,b)      {if(((b)=(a))!=(Hgraph)NULL) do {
#define end_for_hgraph(a,b)  } while(((b)=(b)->suc)!=a);} /*   Schleife      */
#define vorgaengerm(a)        (a->vorgaenger)             
#define markm(a)              a->mark=TRUE
#define unmark(a)            a->mark=FALSE
#define hattrs(a)            ((Hgraph)(a->attrs.value.data))    /*Attribute zugriff*/
#define marked(a)            (a->mark)


/*****************************************************************************/
/*             Makros fuer die Adjlistdatenstrucktur                         */
/*****************************************************************************/

#define for_adjlist(a,b)      {if(((b)=(a))!=(Adjlist)NULL) do {
#define end_for_adjlist(a,b)  } while(((b)=(b)->suc)!=a);}/*   Schleife      */
#define adjlistm(a)           (a->adjlist)
#define empty_adjlist        ((Adjlist)NULL)              /*    NULL         */


/*****************************************************************************/
/*            Makros fuer die Helperdatenstrucktur                           */
/*****************************************************************************/

#define httrs(a)              ((Helper)(a->attrs.value.data))   /*Attribute zugriff*/
#define empty_helper          ((Helper)(NULL))            /*     NULL        */
#define for_helper(a,b)      {if(((b)=(a))!=(Helper)NULL) do {
#define end_for_helper(a,b)  } while(((b)=(b)->suc)!=a);} /*    Schleife     */


#define attrsm(a)             (a->attrs.value.data)    


/*****************************************************************************/
/*                  lokale Datenstruckturen                                  */
/*****************************************************************************/

typedef struct hgraph {
	struct hgraph  *pre, *suc;
	Menge           clique;
	struct adjlist *adjlist;
	bool            mark;
	bool            exponiert;
	int             dist;
	int             tiefe;
	struct snode   *snode;
	struct hgraph  *vorgaenger;
}              *Hgraph;

typedef struct adjlist {
	struct adjlist *pre, *suc;
	Menge           kante;
	struct hgraph  *snode, *tnode;
}              *Adjlist;

typedef struct helper {
	struct helper  *suc, *pre;
	struct hgraph  *neu;
	struct hgraph  *alt;
	Menge           anschl;
}              *Helper;

/*****************************************************************************/
/*               lokale Variablen :                                          */
/*****************************************************************************/

/*     groesse des Hilfsgarphen   */
static int anzahl_hnodes;
static int anzahl_hedges;


/*     Flag ob hilfsgraph vorbehandelt werden soll */
bool obaufspalten=FALSE;

/*     Hgraphstrucktur zum anscliessenden freigeben merken */
static Hgraph hilfsgraph_zum_freigeben;
static Slist dominante_cliquenmenge_zum_freigeben;

/*****************************************************************************/
/*****************************************************************************/
/*                                                                           */
/*      =================================================                    */
/*      !Prozeduren zur Verwaltung der Hilfsgraphstuktur!                    */
/*      =================================================                    */
/*                                                                           */
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*                       print hgraph                                        */
/*								             */
/*                gibt die hgraph Struktur zu Kontrollzwecken                */
/*                an Stdout aus.                                             */
/*								             */
/*****************************************************************************/


void 
print_hgraph(Sgraph sgraph, Hgraph hgraph)
{
	Hgraph          hnode;
	Adjlist         adjnode;
	message("\nHGRAPH: ");
	for_hgraph(hgraph, hnode) {
		message("\nHNODE: /*mit Tiefe %i*/" /* ,hnode->tiefe */ );
		printmenge(sgraph, hnode->clique);
		for_adjlist(hnode->adjlist, adjnode) {
			message("\n     ADJKNOTEN: ");
			printmenge(sgraph, adjnode->tnode->clique);
			message(" adr.:%i", adjnode->tnode);
		} end_for_adjlist(hnode->adjlist, adjnode)
	} end_for_hgraph(hgraph, hnode)
		message("\n");
}

/*****************************************************************************/
/*                 new_hgraph                                                */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Hgraph 
new_hgraph(Menge clique)
{
	Hgraph          new;
	new = (Hgraph) mein_malloc(sizeof(struct hgraph));
	new->suc = new;
	new->pre = new;
	new->clique = clique;
	new->adjlist = empty_adjlist;
	new->exponiert = FALSE;
	return new;
}

/*****************************************************************************/
/*                 free_hgraph                                               */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

void free_hnode(Hgraph hnode)
{
	free_menge(hnode->clique);
	mein_free(hnode);
}



void free_hgraph(Hgraph hgraph)
{
	Hgraph          suc, hnode = hgraph;
	if (hnode != empty_hgraph)
		do {
			suc = hnode->suc;
			free_hnode(hnode);
			hnode = suc;
		} while (hnode != hgraph);
}
/*****************************************************************************/
/*             add_to_hgraph                                                 */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Hgraph 
add_to_hgraph(Hgraph hgraph, Menge clique)
{
	Hgraph          new;
	new = new_hgraph(clique);
	if (hgraph == empty_hgraph) {
		hgraph = new;
	} else {
		new->suc = hgraph;
		new->pre = hgraph->pre;
		new->suc->pre = new;
		new->pre->suc = new;
	}
	return new;
}

/*****************************************************************************/
/*****************************************************************************/
/*                                                                           */
/*      =================================================                    */
/*      !Prozeduren zur Verwaltung der Unterstuezungsstuktur!                */
/*      =================================================                    */
/*                                                                           */
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*                 new_helper                                                */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Helper new_helper(Menge menge, Hgraph alt)
{ Helper new;
  new=(Helper)mein_malloc(sizeof(struct helper));
  new->suc = new;
  new->pre = new;
  new->alt = alt;
  new->anschl = menge;
  return new;
}

/****************************************************************************/
/*             add_to_helper                                                */
/****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Helper add_to_helper(Helper helper, Menge menge, Hgraph alt)
{Helper new;

 new=new_helper(menge,alt);
 if (helper==empty_helper)
     {helper=new;}
 else 
     {new->suc=helper;
      new->pre=helper->pre;
      new->suc->pre=new;
      new->pre->suc=new;
     }
 return new;
}
/****************************************************************************/
/*              Schnitt von 2slists                                         */
/****************************************************************************/
/*
 Slist schnitt(Slist menge2, Slist menge1)
{Slist menge3;
 Slist element,ele;
 menge3=empty_slist;
 for_slist(menge2,element)
     {for_slist(menge1,ele)
          {if (dattrs(element)==dattrs(ele))
               { menge3=add_to_slist(menge3,element->attrs);
                 break;      
               }
           }end_for_slist(menge1,ele)
     } end_for_slist(menge2,element)
 return (menge3);
}
*/
/****************************************************************************/
/*              enthaelt slist von slists eine slist ?                      */
/****************************************************************************/
/*bool slist_teilmenge_von_slist(Slist slist1, Slist slist2)
{if (gleich(schnitt(slist1,slist2),slist1))
   {return TRUE;}
else {return FALSE;}
}
*/
/*****************************************************************************/
/*****************************************************************************/
/*                                                                           */
/*      =====================================================                */
/*      !Prozeduren zur Verwaltung der Adjazenzlistenstuktur!                */
/*      =====================================================                */
/*                                                                           */
/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/*               neues adjlist element                                       */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Adjlist new_adjlist(Hgraph hnode1, Hgraph hnode2)
{ Adjlist new;

  new=(Adjlist)mein_malloc(sizeof(struct adjlist));
  new->suc = new;
  new->pre = new;
  new->snode = hnode1; 
  new->tnode = hnode2;
  new-> kante=mengenschnitt(copy_menge(hnode1->clique),hnode2->clique);
  return new;
}


/*****************************************************************************/
/*             fuege Kante hinzu                                             */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

void add_edge_to_hgraph(Hgraph hnode1, Hgraph hnode2)
{
 Adjlist new;

 if (hnode1->adjlist==empty_adjlist)
    {new=new_adjlist(hnode1,hnode2);
     hnode1->adjlist=new;
     }
 else
    {new=new_adjlist(hnode1,hnode2);
     new->suc=hnode1->adjlist;
     new->pre=hnode1->adjlist->pre;
     new->suc->pre=new;
     new->pre->suc=new;
    }

if (hnode2->adjlist==empty_adjlist)
    {new=new_adjlist(hnode2,hnode1);
     hnode2->adjlist=new;
    }
 else
    {new=new_adjlist(hnode2,hnode1);
     new->suc=hnode2->adjlist;
     new->pre=hnode2->adjlist->pre;
     new->suc->pre=new;
     new->pre->suc=new;
    }
} 

/*****************************************************************************/
/*              entferne Kante                                               */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

void entferne_kante(Hgraph hgraph, Hgraph knoten1, Hgraph knoten2)
{Adjlist adjlist;  

 /* fuer Knoten 1 */

 if(adjlistm(knoten1)==(adjlistm(knoten1)->suc))
      {if(adjlistm(knoten1)->tnode==knoten2)
          {adjlistm(knoten1)=empty_adjlist;}}
 else {for_adjlist(adjlistm(knoten1),adjlist)
          {if(knoten2==adjlist->tnode)
               {adjlist->pre->suc=adjlist->suc;
                adjlist->suc->pre=adjlist->pre;
                knoten1->adjlist=adjlist->suc;
                mein_free(adjlist);
                break;
                }
         }end_for_adjlist(adjlistm(knoten1),adjlist)
      }

/* fuer Knoten 2*/

 if(adjlistm(knoten2)==(adjlistm(knoten2)->suc))
      {if(adjlistm(knoten2)->tnode==knoten1)
          {adjlistm(knoten2)=empty_adjlist;}}
 else {for_adjlist(adjlistm(knoten2),adjlist)
          {if(knoten1==adjlist->tnode)
               {adjlist->pre->suc=adjlist->suc;
                adjlist->suc->pre=adjlist->pre;
                knoten2->adjlist=adjlist->suc;
                mein_free(adjlist);
                break;
               }
          }end_for_adjlist(adjlistm(knoten2),adjlist)
      }

}

/*****************************************************************************/
/*                remove hnode  (inclusive aller Kanten)                     */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Hgraph remove_hnode(Hgraph hgraph, Hgraph hnode)
{Adjlist adjlist;

 if (hnode==hgraph)
     {hgraph=hgraph->suc;}/*kann nicht der einzige sein(durch aufruf)*/
 for_adjlist(hnode->adjlist,adjlist)
     {entferne_kante(hgraph,hnode,adjlist->tnode);
     }end_for_adjlist(hnode->adjlist,adjlist)
 hnode->suc->pre=hnode->pre;
 hnode->pre->suc=hnode->suc;
 free_hnode(hnode);
 return hgraph;
}

/*****************************************************************************/
/*                 Kante umhaengen                                           */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

void kante_umhaengen(Hgraph hnode1, Hgraph hnode2, Hgraph hnode3)
{entferne_kante(hnode1,hnode1,hnode2);
 add_edge_to_hgraph(hnode1,hnode3);
}

/****************************************************************************/
/*             subtract from adjlist                                        */
/****************************************************************************/

Adjlist subtract_from_adjlist(Adjlist adjlist2, Hgraph hnode2)
{
 Adjlist adjlist;
 Adjlist g=adjlist2;

 for_adjlist(adjlist2,adjlist)
     {if (adjlist->tnode==hnode2)
        {adjlist->suc->pre=adjlist->pre;
         adjlist->pre->suc=adjlist->suc;
         g=adjlist->suc;
         mein_free(adjlist);
         break;
        }
     }end_for_adjlist(adjlist2,adjlist)
 return g;
}


/*****************************************************************************/
/*               loesche markierungen                                        */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

void unmark_hgraph(Hgraph hgraph)
{Hgraph hnode;

 for_hgraph(hgraph,hnode)
     {unmark(hnode);
     }end_for_hgraph(hgraph,hnode)
}

/*****************************************************************************/
/*                                                                           */
/*         =================================================                 */
/*         ! Baue Sgraphstrucktur aus Hilfsgraph strucktur !                 */
/*         =================================================                 */
/*                                                                           */
/*****************************************************************************/
/*  zugehoerige Prozeduren : dfs,baue_baum,finde_wurzel                      */
/*     baue_sgraph_aus_hagraph  :  baut Sgarph aus Hilfsgarph                */
/*     baue_baum                :  realisiert Tiefensuche zum Sgraph         */
/*                                 aufbau    (rekursiv)                      */  /*                                                                           */
/*     finde_wurzel             :  bestimmt wurzel als Mittelpunkt           */
/*                                 des laengsten Pfades                      */
/*     dfs                      :  realisiert Tiefenfensuche fuer            */
/*                                 finde wurzel  (rekusiv)                   */
/*  benoetigte Prozeduren  :     marked,for_adjlist,maximum,                 */
/*                  my_set_nodelabel,make_edge,end_for_adjlistmarkm          */
/*****************************************************************************/
/*****************************************************************************/
/*                    Finde Wurzel (Mitte)                                   */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              int tiefe : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

int dfs(Hgraph hgraph, int tiefe)
{
 Adjlist adjnode;
 int max_tiefe=tiefe;
 int ruecktiefe;

 markm(hgraph);
 hgraph->tiefe=tiefe;
 for_adjlist(hgraph->adjlist,adjnode)
     {if(!(marked(adjnode->tnode)))
            {ruecktiefe=dfs(adjnode->tnode,tiefe+1);
             max_tiefe=maximum(max_tiefe,ruecktiefe);
            }
     }end_for_adjlist(hgraph->adjlist,adjnode)
 return (max_tiefe);
}


/*****************************************************************************/
/*                      baue Baumzerlegun                                    */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph  : Berechnente Hilfsgraphgraph                        */
/*              Sgraph  : zu berechnender Graph			             */
/*              Snode   : letzter neugenerierte Snod (gehoert zu new_sgraph) */
/*              Sgraph  new_sgraph : zu generierende baumzerlegung	     */
/*                                                                           */
/*  Rueckgabeparameter :   void					             */
/*                                                                           */
/*  Aufgabe und Funktion:      Rekusionsschritt : Baut eine Snode auf und    */
/*                             ruft fuer jeden Nachfolger sich erneut auf.   */
/*                                                                           */
/*****************************************************************************/

void baue_baum(Sgraph sgraph, Hgraph hgraph, Snode snode, Sgraph new_sgraph)
{
 Adjlist adjnode;
 Snode neuer_snode;
 
 markm(hgraph);
 for_adjlist(hgraph->adjlist,adjnode)
     {if(!(marked(adjnode->tnode)))
         {
          neuer_snode=make_node(new_sgraph,make_dattr((save_menge
                                           (sgraph,adjnode->tnode->clique))));
          my_set_nodelabel(sgraph,neuer_snode,adjnode->tnode->clique);
          make_edge(snode,neuer_snode,empty_attr);
          neuer_snode->x=350;
          neuer_snode->y=350;
          adjnode->tnode->snode=neuer_snode;
          baue_baum(sgraph,adjnode->tnode,neuer_snode,new_sgraph);
         }
     }end_for_adjlist(hgraph->adjlist,adjnode)
}

/*****************************************************************************/
/*               loesche markierungen                                        */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Hgraph finde_wurzel(Hgraph hgraph)
              
/* finde durch Search knoten mit groeseter Tiefe */

{
 int max_tiefe2;
 int wurzeltiefe;
 Hgraph hnode;

 unmark_hgraph(hgraph);
 max_tiefe2=dfs(hgraph,1);
 for_hgraph(hgraph,hnode)
     {if (max_tiefe2==hnode->tiefe)
           {break;}
      }end_for_hgraph(hgraph,hnode)

 /* finde durch saerch anders Baumende  */
 unmark_hgraph(hgraph);
 max_tiefe2=dfs(hnode,1);
 wurzeltiefe=max_tiefe2/2+1;
 for_hgraph(hgraph,hnode)
     {if (wurzeltiefe==hnode->tiefe)
          {break;}
      }end_for_hgraph(hgraph,hnode)
 return(hnode);
}


/*****************************************************************************/
/*                   baue Sgraph aus hgraph   (main)                        */
/*****************************************************************************/
/*  benoetigte Prozeduren:    make_graph,strsave,finde_wurzel                */
/*                            make_node ,save_menge ,baue_baum               */
/*  aufrufende Prozedur:      baumzerlegung                                  */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Sgraph       : zu berechnender Graph		             */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe und Funktion:   Rekursionsstart. Die Hilfsgarphstrucktur wird    */
/*			    in eine Sgraphstrucktur konvertiert.             */
/*                                                                           */
/*****************************************************************************/

Sgraph baue_sgraph_aus_hgraph(Sgraph sgraph, Hgraph hgraph)
{
 Sgraph new_sgraph;
 Snode snode;

 Hgraph wurzel;

 new_sgraph=make_graph(empty_attr);
 set_graphlabel(new_sgraph,strsave("tree-decomposition"));
 new_sgraph->attrs=make_dattr(sgraph);   /* zeiger auf sgraph merken */
 new_sgraph->directed=TRUE;
 wurzel=finde_wurzel(hgraph);
 /*message("\n WURZEL: ");printmenge(sgraph,wurzel->clique);*/
 snode=make_node(new_sgraph,make_dattr(save_menge(sgraph,wurzel->clique)));
 my_set_nodelabel(sgraph,snode,wurzel->clique);
 snode->x=370;snode->y=370;
 unmark_hgraph(hgraph);
 baue_baum(sgraph,wurzel,snode,new_sgraph);
 return(new_sgraph);
}

/*****************************************************************************/
/*                                                                           */
/*     =========================================================             */
/*     !Baue einen Hilfsgraph aus der vollstaenigen Cliquenmenge!             */
/*     =========================================================             */
/*                                                                           */
/*  Parameter:  Sgraph             : Zu berechnender Graph	             */
/*              Menge cliquenmenge : gefundene dominante Cliquen	     */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    Hilfsgraphstrucktur	             */
/*                                                                           */
/*  Aufgabe und Funktion :						     */
/*   Fuer jede Clique wird ein Knoten aufgebaut der einen Zeiger             */
/*   auf die Cliquenmenge enthaelt.                                          */
/*   Zwischen zwei Knoten wird genau, dann eine Kante gesetz wenn            */
/*   der Schnitt zwischen den zugehoerigen Cliquenmnegen nicht leer          */
/*   ist.                                                                    */
/*                                                                           */
/*  benoetigte Prozeduren und Makros :  for_slist, add_to_hgraph,            */
/*                                      end_for_slist, for_hgraph,           */
/*                             mengenobschnitt, add_edge_to_hgraph,          */
/*                                      end_for_hgraph                       */
/*****************************************************************************/

Hgraph baue_hilfsgraph(Sgraph sgraph, Slist cliquenmenge)
{
 Hgraph hgraph,hnode,hnode2;
 Slist  clique;

 anzahl_hnodes=0;
 anzahl_hedges=0;
 hgraph=empty_hgraph;

 for_slist(cliquenmenge,clique)
      {hgraph=add_to_hgraph(hgraph,copy_menge(dattrs(clique)));
       anzahl_hnodes++; 
      /*message("\n clique:");printmenge(sgraph,clique->attrs.value.data);*/
      }end_for_slist(cliquenmenge,clique)
/* printf ("ich habe %i Hnodes generiert ",anzahl_hnodes);*/

 for_hgraph(hgraph,hnode)
     {hnode2=hnode;
      while ( (hnode2=(hnode2->suc))!= hgraph)
          {if(mengenobschnitt(hnode->clique,hnode2->clique))
                 {add_edge_to_hgraph(hnode,hnode2);
                  anzahl_hedges++;
                 }
         }
     }end_for_hgraph(hgraph,hnode)
  /*print_hgraph(sgraph,hgraph); */
 return (hgraph);
}

/*****************************************************************************/
/*     finde cliquen die separatoren oder nur teilweise angeschlossen sind   */
/*****************************************************************************/

void dfs_graph(Hgraph hgraph)
{Adjlist adjnode;

 markm(hgraph);
 for_adjlist(hgraph->adjlist,adjnode)
     {if(!(marked(adjnode->tnode)))
          { dfs_graph(adjnode->tnode);
          }
      }end_for_adjlist(hgraph->adjlist,adjnode)
}

/*****************************************************************************/
/*               Zu Zeit nicht vom Programm aufgerufen, da fehlerhaft        */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/
/*****************************************************************************/

Hgraph aufspalten(Sgraph sgraph, Hgraph hgraph)
{Hgraph hnode,hgraph2;
 Menge  neue_menge;
 bool trennen;
 Slist slist,slist2;
 Adjlist adjnode;
 
 /*print_hgraph (sgraph,hgraph);*/
 /***********   nur teilweise angeschlossene als neuen hnode abzweigen   ******/
 /*********** erkennen ******/
 /****** hgraph3=hgraph;
 helper=empty_helper;
 for_hgraph(hgraph,hnode)
   {if(hnode->adjlist->suc!=hnode->adjlist) *//*nur Knoten mit >= 2 Kanten*//*
     {trennen=FALSE;
       anschluss=new_menge();
       cliquengroesse=size_of_menge(sgraph,hnode->clique);
       for_menge(hnode->clique,node)
             {if ((node->attrs.value.integer==1)||
               (size_of_menge(sgraph,make_adjmenge(node))!=(cliquengroesse-1)))
                {anschluss=add_to_menge(anschluss,node);
                 node->attrs.value.integer=1;}
             else {trennen=TRUE;}
          }end_for_menge(hnode->clique,node)
       if(trennen)
         {helper=add_to_helper(helper,anschluss,hnode);
          message("hnode: ");printmenge(sgraph,hnode->clique);
          message("  anschluss: ");printmenge(sgraph,anschluss);message("\n");
          }
      }
      else {hnode->exponiert=TRUE;}
      }end_for_hgraph(hgraph,hnode)  *****/
 /************ abzweigen  ***/
 /*  neu === neu gesetzt aber alter Inhalt */
 /*******  for_helper(helper,helper2)
     {helper2->neu=add_to_hgraph(hgraph,helper2->alt->clique);
     add_edge_to_hgraph(helper2->neu,helper2->alt);
     helper2->alt->clique=helper2->anschl;    
     helper2->neu->exponiert=TRUE;
   }end_for_helper(helper,helper2)   *****/
 /*   print_hgraph(sgraph,hgraph);*/
 /****************     gleiche Knoten miteineander identifizieren     ****/
 /* unmark_hgraph(hgraph);
 slist=empty_slist;
 for_helper(helper,helper2)
   {if(!helper2->alt->mark)
    {helper3=helper2;
     while((helper3=helper3->suc)!=helper)
         {if ((mengengleich(helper3->anschl,helper2->anschl))&&
                                                (!helper3->alt->mark))
              {add_edge_to_hgraph(helper3->neu,helper2->alt);
               slist=anhaengen(slist,helper3->alt);
               markm(helper3->alt);}
          }
    }}end_for_helper(helper,helper2)
 for_slist(slist,slist2)
     {hgraph=remove_hnode(hgraph,hattrs(slist2));
      message("entferne hnode: ");printmenge(sgraph,hattrs(slist)->clique);
      message("\n");
     }end_for_slist(slist,slist2)*/
  /* print_hgraph(sgraph,hgraph);*/

 /***********   separatoren aufloesen (exponierte uebergehen)           ******/
 for_hgraph(hgraph,hnode)
   {if (hnode->exponiert ==FALSE)
        {unmark_hgraph(hgraph);
         markm(hnode);
         for_adjlist(hgraph->adjlist,adjnode)
           {slist=empty_slist;
            if(!adjnode->tnode->mark)
            {dfs_graph(adjnode->tnode);
             neue_menge=new_menge();
             trennen=FALSE;
             for_adjlist(hgraph->adjlist,adjnode)
                {if(!(adjnode->tnode->mark))
                    {trennen=TRUE;}
                 else
                    {neue_menge=
                           mengenvereinigung(neue_menge,adjnode->tnode->clique);
                     slist=anhaengen(slist,adjnode->tnode);}
                }end_for_adjlist(hgraph->adjlist,adjnode)
             if(trennen)
                {message("\ntrennen:");printmenge(sgraph,hnode->clique);
                 message("  in : ");printmenge(sgraph,neue_menge);
                 neue_menge=mengenschnitt(hnode->clique,neue_menge);
                 hgraph=add_to_hgraph(hgraph,neue_menge);
                 add_edge_to_hgraph(hgraph2,hnode);
                 hgraph->exponiert=TRUE;
                 for_slist(slist,slist2)
                     {kante_umhaengen((Hgraph)dattrs(slist),hnode,hgraph2);
                     }end_for_slist(slist,slist2)
                 }
             }}end_for_adjlist(hgraph->adjlist,adjnode)
   }}end_for_hgraph(hgraph,hnode)
 /* print_hgraph(sgraph,hgraph);*/
return hgraph;}

/*****************************************************************************/
/*                                                                           */
/*     =========================================================             */
/*     !Baue eine Hilfsgarph aus der vollstaenigen Cliquenmenge!             */
/*     =========================================================             */
/*                                                                           */
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/
/*  Funktion :                                                               */
/*   gegeben: Anfang- und Endkonten in gefundenem Kreis .                    */
/*   rekonstruiere kompleten Kreis und speichere Kreis in Slist              */
/*   finde im Kreis diejenige Kante, die die Baumweite am innerhalb          */
/*   des Kreises (annder Knoten sind nicht betroffen) am wenigsten           */
/*   erhoet wenn man diese entfernt.                                         */
/*   Entferne diese Kante und aender Baumzerlegung (Knotenmengen)            */
/*   entsprechend der Baumzerlegungsdefinition.                              */
/*   M.a.W: neue Knotenmenge= alte Knotenmenge vereinigt dem Schnitt         */
/*                       der beiden Adjazentknoten der entfernten            */
/*                       Kante.                                              */
/*                                                                           */
/*****************************************************************************/

Hgraph kreis_aufschneiden(Sgraph sgraph, Hgraph hgraph, Hgraph hnode2, Hgraph hnode1)
{
 Slist slist;
 bool auf_weg=TRUE;
 int lokBw = anzahl_nodes(sgraph);
 int lokBw2;
 Slist shnode,shnode2;
 Menge kante,best_kante_inhalt,kante2;
 Hgraph best_kante_source,best_kante_target;
 
 /*            ***********baue slist aus kreis**********                     */
 
 slist=empty_slist;
 do{
     slist=anhaengen(slist,hnode1);
    unmark(hnode1);
   } while( (hnode1=vorgaengerm(hnode1)) != NULL );
 do{
   if(!(auf_weg))
       {slist=subtract_from_slist(slist,make_dattr(hnode2));}
   else {if (!(marked(hnode2)))
            {auf_weg=FALSE;}
         else
            {slist=anhaengen (slist,hnode2);
             slist=slist->pre;
            }
        }
   }while ((hnode2=vorgaengerm(hnode2))!=NULL);
 /*message ("*****kreis:****");
 for_slist(slist,shnode)
    {message("\n");printmenge(sgraph,hattrs(shnode)->clique);}end_for_slist(slist,sh node)*/
/*             *********finde guestigste Kante ***** */

 lokBw=1000;
 /*message("\nKanten:");*/
 for_slist(slist,shnode)
    {kante=copy_menge(hattrs(shnode)->clique);
     kante=mengenschnitt(kante,hattrs(shnode->suc)->clique);
 /*     message("   ;  ");printmenge(sgraph,kante);*/
     lokBw2=0;
     for_slist(slist,shnode2)
        {kante2=copy_menge(kante);
         lokBw2=maximum(lokBw2,size_of_menge(sgraph,
                   mengenvereinigung(kante2,(hattrs(shnode2))->clique)));
         mein_free(kante2);
        }end_for_slist(slist,shnode2)
     if(lokBw2<lokBw)
         {best_kante_source=hattrs(shnode);
         best_kante_inhalt=copy_menge(kante);
         best_kante_target=hattrs(shnode->suc);
         lokBw=lokBw2;
        }
    }end_for_slist(slist,shnode)
 /*message("\n guenstigste Kante: ");printmenge(sgraph,best_kante_inhalt);
  message("zwischen: ");printmenge(sgraph,best_kante_source->clique);+
  message("  und ");printmenge(sgraph,best_kante_target->clique);*//*              *********entferne diese kante ***** */
 entferne_kante(hgraph,best_kante_source,best_kante_target); 
 /*           ********** cliquen umbauen      ******/

 for_slist(slist,shnode)
   {hattrs(shnode)->clique=mengenvereinigung(hattrs(shnode)->clique,
                                                     best_kante_inhalt);
 /*   message("\n neuer Knoten"); printmenge(hattrs(shnode)->clique); */
   }end_for_slist(slist,shnode)

 free_slist(slist);mein_free(best_kante_inhalt);
 return hgraph;
}

/*****************************************************************************/
/*                                                                           */
/*                 ===================================                       */
/*                 !finde Kreise und spalte diese auf!                       */
/*                 ===================================                       */
/*                                                                           */
/*  Parameter:  Sgraph       : aktueller Sgraph			             */
/*              Hgraph       : bisheriger Hilfsgarph		             */
/*                                                                           */
/*  Rueckgabeparamenter : Hgraph   : berechneter Hilfsgarph		     */
/*                                                                           */
/*   Suche durh Breitensuche einen Kreis im Hilfsgraphen und splate          */
/*   diesen so auf, dass die Baumzerlegungbedingungen erhalten               */
/*   bleiben.                                                                */
/*   Suche dazu zuerst gezielt Kreise mit einer Kante die ohne die           */
/*   Baumweite zu erhoehen entfernt werden kann.                             */
/*   Suche anschliessend willkuerlich einen Kreis. Entferne die              */
/*   "guenstigste" Kante (siehe entferne_kante).                             */
/*                                                                           */
/*****************************************************************************/
/*   benoetigte Prozeduren und Makros :     for_hgraph unmark,               */
/*                   vorgaengerm, end_for_hgraph, anhaengen, hattrs,         */ 
/*         for_adjlist,  end_for_adjlist, menge_teilmenge_von_menge,         */ 
/*           marked, end_for_hgraph, kreis_aufschneiden                      */
/*****************************************************************************/
/*****************************************************************************/
/*                  finde Kreis    (ohne Bwerhoeung)                         */
/*****************************************************************************/

Hgraph zerlege(Sgraph sgraph, Hgraph hgraph)
{ Adjlist adjlist,adjnode,adjnode2;
 Hgraph  hnode,tnode,hnode2;
 Slist   slist,queue;
 Menge   kante;
 bool    kreis_gefunden;
 int     entfernte_kanten1=0;
 int     entfernte_kreise2=0;
 int     zaehler=0;

 init_fortschritt("remove cycles",anzahl_hedges-anzahl_hnodes+1);
 do{
     for_hgraph(hgraph,hnode2) 
         {for_adjlist(hnode2->adjlist,adjnode2)
             {kante=adjnode2->kante;
              for_hgraph(hgraph,hnode)
                  {unmark(hnode);
                   vorgaengerm(hnode)=NULL;
                  }end_for_hgraph(hgraph,hnode)
              queue=anhaengen(empty_slist,adjnode2->tnode);
              markm(hnode2);
              vorgaengerm(adjnode2->tnode)=hnode2;
              kreis_gefunden=FALSE;
     /*    search   */
              while(queue!=empty_slist)
                  {slist=queue;
                  /* entferne slist aus queue (anfangselement) */
                  /* queue=subtract_immediately_from_slist(queue,queue) ;  */         
                 if(queue->suc==queue)
                      {queue=empty_slist;}
                 else
                      {queue->suc->pre=queue->pre;
                       queue->pre->suc=queue->suc;
                       queue=queue->suc;
                      }
                 adjlist=hattrs(slist)->adjlist;
                 for_adjlist(adjlist,adjnode)
                      {if(adjnode->tnode!=vorgaengerm(hattrs(slist)))
                           {if(menge_teilmenge_von_menge(kante,adjnode->kante)) 
                                {if(adjnode->tnode==hnode2)
                                     {kreis_gefunden=TRUE ; break;}
                                else
                                    {if(!(marked(adjnode->tnode)))
                                         {tnode=adjnode->tnode;
                                          markm(tnode);
                                          vorgaengerm(tnode)=hattrs(slist);
                                          queue=anhaengen(queue,tnode);
                                         }
                                    }
                                }
                           }   
                      }end_for_adjlist(adjlist,adjnode)
                 if (kreis_gefunden) {break;}
             }/*while(warteschlange nicht leer)-schleife*/
          if (kreis_gefunden) 
            {        
      /*       printmenge(sgraph,hnode2->clique);printmenge(sgraph,adjnode2->tnode->clique);   */
             entferne_kante(hgraph,hnode2,adjnode2->tnode);
             entfernte_kanten1++;
             fortschritt(entfernte_kanten1);
             mein_free(slist);
             /*  print_hgraph(sgraph,hgraph);*/
             }
          }end_for_adjlist(hnode2->adjlist,adjnode2)
       }end_for_hgraph(hgraph,hnode2)
   } while(kreis_gefunden);

/*****************************************************************************/
/*                  finde Kreis       (mit Bwerhoehung)                      */
/*****************************************************************************/

 /*message("computing cycles type 2...\n");*/
 init_fortschritt("remove cycles 2",anzahl_hedges-anzahl_hnodes
                                                   -entfernte_kanten1+1);
 /*fortschritt_sf();*/
 do{
    for_hgraph(hgraph,hnode)
        {unmark(hnode);
             vorgaengerm(hnode)=NULL;
        }end_for_hgraph(hgraph,hnode)
    queue=neue_liste(hgraph);
    markm(hgraph);
    kreis_gefunden=FALSE;
    /*    search   */
    while(queue!=empty_slist)
        {slist=queue;
         /* entferne slist aus queue (anfangselement) */
         if(queue->suc==queue)
               {queue=empty_slist;}
         else
               {
                queue->suc->pre=queue->pre;
                queue->pre->suc=queue->suc;
                queue=queue->suc;
               }
         adjlist=hattrs(slist)->adjlist;
         for_adjlist(adjlist,adjnode)
               {if(adjnode->tnode!=vorgaengerm(hattrs(slist)))
                   {if(marked(adjnode->tnode))
                        {kreis_gefunden=TRUE ; break;}
                    else
                        {tnode=adjnode->tnode;
                         markm(tnode);
                         vorgaengerm(tnode)=hattrs(slist);
                         queue=anhaengen(queue,tnode);
                        }
                   }
               }end_for_adjlist(adjlist,adjnode)
         mein_free(slist);
         if (kreis_gefunden) {break;}
        }  /*end while-Schleife(Warteschlange)*/
    if (kreis_gefunden)
        {        
         /*  printmenge(sgraph,adjnode->tnode->clique);
              printmenge(sgraph,hattrs(slist)->clique); */  
         hgraph =   kreis_aufschneiden(sgraph,hgraph,adjnode->tnode,
                                                             hattrs(slist));
         entfernte_kreise2++;
         if((zaehler++)==10)
               {zaehler=0;
                fortschritt(entfernte_kreise2); 
                /*message("noch %i, (Kreise",anzahl_hedges-anzahl_hnodes+1-
                                        entfernte_kanten1-entfernte_kreise2);*/
               } 
        /*  print_hgraph(sgraph,hgraph);*/}
   } while(kreis_gefunden);

 /*print_hgraph(sgraph,hgraph);*/

/* message("\nentfernte Kanten 1:%i  Entfernte K. 2:%i\n",entfernte_kanten1,entfernte_kreise2);*/
 end_fortschritt();
 return hgraph;
}

/*****************************************************************************/
/*****************************************************************************/
/*                                                                           */
/*                     Ende Hauptroutienen                                   */
/*                                                                           */
/*****************************************************************************/
/*****************************************************************************/
/*             Graph vorbereiten (1valenz und2valenz entfernen               */
/*****************************************************************************/
/*****************************************************************************/

/*****************************************************************************/

/*reduziere_valenz_1(Sgraph sgraph, Slist valenz1liste)
{Snode snode;
 Sedge sedge;
 bool weiter=false;
 Slist element,slist;
 Slist valenz1liste2=empty_slist;

 do{valenz1liste2=empty_slist;
   weiter=false;
   slist=empty_slist;
   for_slist(valenz1liste,element) 
      {snode=(Snode)element->attrs.value.data;
       slist=anhaengen(slist,snode);
       snode->slist->tnode->attrs.value.integer--;
       if(snode->slist->tnode->attrs.value.integer==1)
         { valenz1liste2=anhaengen(valenz1liste2,snode->slist->tnode);
           weiter=true;}
       snode->attrs.value.integer=0;
       }end_for_slist(valenz1liste,element)
    free_slist(valenz1liste);
    valenz1liste=valenz1liste2;
    for_slist(slist,element);
        {remove_node(element->attrs.value.data);
        }end_for_slist(slist,element)
    free_slist(slist);
   } while (weiter);

}
*/
/*****************************************************************************/
/*
void reduziere_valenz_2(Sgraph sgraph)
{Snode snode;
 Sedge sedge,sedge2;
 bool weiter=false;
 Slist neue_kanten;
 Slist element2;
 Slist element,slist=empty_slist;

*//* Achtung ein Kreis wird zu einem punkt reduziert und nicht zur Dreierclique*/
 /* do{*/
/*   weiter=false;
   for_all_nodes(sgraph,snode)
      {if (snode->attrs.value.integer==2)
         {slist=anhaengen(slist,snode);
          neue_kanten=empty_slist;
          weiter=true;
 */         /* da bereits neue Knaten gesetzsein koennen,kann man nicht davon
             ausgehen, dass nur 2 Nachbarknoten existieren.*/
/*          for_sourcelist(snode,sedge)
                  {neue_kanten=anhaengen(neue_kanten,sedge->tnode);
                  }end_for_sourcelist(snode,sedge)
          for_slist(neue_kanten,element)
               {element2=element;
                while( (element2=element2->suc)!=neue_kanten )
                   {make_edge(sattrs(element),sattrs(element2),empty_attr);}
               }end_for_slist(neue_kanten,element)
         free_slist(neue_kanten);
         }
       }end_for_all_nodes(sgraph,snode)
 */ /*  } while (weiter);*/
/*for_slist(slist,element);
     {remove_node(element->attrs.value.data);
     }end_for_slist(slist,element)
free_slist(slist);

}
*/
/*****************************************************************************/
/*****************************************************************************/
/*
Sgraph reduziere_gtree_layout_walker/tree_layout_walker_export.hraph(Sgraph sgraph)
{Sgraph new_sgraph=make_graph(empty_attr);
 int valenz;
 Snode snode,new_snode;
 Sedge sedge;
 Slist valenz1liste=empty_slist;

 new_sgraph->directed=false;
 for_all_nodes(sgraph,snode)
            {new_snode=make_node(new_sgraph,empty_attr);
          new_snode->x=snode->x;
          new_snode->y=snode->y;
          set_nodelabel(new_snode,strsave(snode->label));
          new_snode->nr=snode->nr;
          new_snode->attrs=make_attr(ATTR_FLAGS,0);
          snode->iso=new_snode;
     }end_for_all_nodes(sgraph,snode)
*/ /*  kopiere Kanten in neuen Teilgraphen   */
 /*

 for_all_nodes(sgraph,snode)
       {valenz=0;
        for_sourcelist(snode,sedge)
          {if(sedge->tnode->attrs.value.integer==0)
             {make_edge(snode->iso,sedge->tnode->iso,empty_attr);
              valenz++;
         }}end_for_sourcelist(snode,sedge)
     snode->iso->attrs=make_attr(ATTR_FLAGS,valenz);
     message("valenz=%i",valenz);
     if(valenz==1)
         {valenz1liste=anhaengen(valenz1liste,snode->iso);}
    }end_for_all_nodes(sgraph,snode)

 reduziere_valenz_1(new_sgraph,valenz1liste);
 reduziere_valenz_2(new_sgraph);
 return new_sgraph;
}
*/
/*****************************************************************************/
/*****************************************************************************/
/*
void graph_reduzieren(Sgraph_proc_info info)
{Sgraph new_sgraph=reduziere_graph(info->sgraph);
     info->new_sgraph=new_sgraph;
     info->sgraph->label=strsave("old_graph");
     info->no_structure_changes=FALSE;
     info->new_buffer=(int)dispatch_user_action(CREATE_BUFFER);
     info->recompute=TRUE;
     info->new_selected=SGRAPH_SELECTED_NONE;
     info->recenter=TRUE;
}
*/
/*****************************************************************************/
/*    berechne Cliquen,Baumbreite und Baumzerlegung Hauptroutinen            */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              Menge Clique : Initialisierungsmenge fuer neuen hnode        */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

void enferne_self_loops(Sgraph sgraph)
{Snode snode;
 Sedge sedge;
 Slist slist,element;
 slist=empty_slist;
 for_all_nodes(sgraph,snode)
    {for_sourcelist(snode,sedge)
        {if(sedge->tnode==snode)
                 {slist=anhaengen(slist,sedge);}
        }end_for_sourcelist(snode,sedge)
    }end_for_all_nodes(sgraph,snode)
 for_slist(slist,element)
   {remove_edge(eattrs(element));}
 end_for_slist(slist,element)
 free_slist(slist);
}

/*****************************************************************************/
/*          Finde groesten Knoten (=Baumweite)                               */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph       : aktueller Sgraph			             */
/*              Hgraph       : berechneter Hilfsgarph		             */
/*                                                                           */
/*  Rueckgabeparameter : int  Groesse-1 des groessten Knoten des Hilfgraphen */
/*                                                                           */
/*  Aufgabe:                        					     */
/*  findet den groessten Knoten im Hilfsgraph un gibt diese groesse-1 zurueck*/
/*  (entspricht der Baumweite des Hilfsgraphen und somit der maximalen       */
/*  Baumweite des Orginalgraphen.                                            */
/*****************************************************************************/

int baumweite(Sgraph sgraph, Hgraph hgraph)
{Hgraph hnode;
 int i,baumweite=0;
 for_hgraph(hgraph,hnode)
     {if((i=size_of_menge(sgraph,hnode->clique))>baumweite)
         {baumweite=i;}
     }end_for_hgraph(hgraph,hnode)
 return baumweite-1;
}

/*****************************************************************************/
/*int groesste_slist(Sgraph sgraph, Slist slist)
{int mini=0;
 Slist element;
 for_slist(slist,element)
       {mini=maximum(mini,size_of_menge(sgraph,(Menge)dattrs(element)));}
 end_for_slist(slist,element)
*//* message("grosste_clique%i\n",mini);*//*
return mini;
}*/

/*****************************************************************************/
void rette_attr_in_feld(Sgraph sgraph)
{int i=1;
 Snode snode;
 Slist slist=empty_slist;
i=anzahl_nodes(sgraph);
for_all_nodes(sgraph,snode)
   {snode->nr=i;
    slist=anhaengen(slist,dattrs(snode));
    i--; 
  }end_for_all_nodes(sgraph,snode)
sgraph->attrs=make_dattr(slist->suc);
}

/******************************************************************************/


Slist mein_berechne_kantenmenge(Sgraph sgraph)
{Snode snode;
 Sedge sedge;
 Slist slist=empty_slist;

 unmark_sgraph(sgraph);
 for_all_nodes(sgraph,snode)
    {snode->attrs=make_flattr(1);
     for_sourcelist(snode,sedge)
         {if (sedge->tnode->attrs.value.integer==0)
            {slist=anhaengen(slist,add_to_menge(
                                add_to_menge(new_menge(),sedge->tnode),snode));}
          }end_for_sourcelist(snode,sedge)
    }end_for_all_nodes(sgraph,snode)
/*printf ("ich habe %i Kanten gezaelt",size_of_slist(slist));*/
return slist;}

/*****************************************************************************/
/*               baumzerlegung                                               */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph       : Zu bearbeitender Grapg                        */
/*                                                                           */
/*  Rueckgabeparameter :   Sgraph    gefundene Baumzerlegung                 */
/*                                                                           */
/*  Aufgabe:      steuere Abluaf der Heuristic                               */
/*                                                                           */
/*****************************************************************************/

Sgraph baumzerlegung_control(Sgraph sgraph)
{Hgraph hilfsgraph=nil;
 Slist dominante_cliquenmenge;
 Sgraph new_sgraph;

 if ( algorithmus == CLIQUENHEURISTIC )
       { dominante_cliquenmenge=berechne_cliquen(sgraph); }
 
 else  {dominante_cliquenmenge=mein_berechne_kantenmenge(sgraph);
        
       }
        /* print_cliquenmenge(sgraph,dominante_cliquenmenge);*/

        init_fortschritt("baue Hilfsgarph",10);
        hilfsgraph=baue_hilfsgraph(sgraph,dominante_cliquenmenge);
        if((hilfsgraph->suc!=hilfsgraph ) && (hilfsgraph->suc->suc!=hilfsgraph))
             {
               /*  print_hgraph(sgraph,hilfsgraph);*/
          /*     message("Hilfsgarph enthaelt %i Kreise"
                                              ,anzahl_hedges-anzahl_hnodes+1);*/
            /*  if (obaufspalten)
                    {hilfsgraph=aufspalten(sgraph,hilfsgraph);}    */     
              /*message("computing cycles type 1...\n");*/
              hilfsgraph=zerlege(sgraph,hilfsgraph); 
              /*nur 1 oder 2 Knoten=>kein Kreis*/
                         }
        else {max_baumweite=maximum(max_baumweite,min_baumweite);}
 new_sgraph=baue_sgraph_aus_hgraph(sgraph,hilfsgraph);
 max_baumweite=maximum(max_baumweite,baumweite(sgraph,hilfsgraph));

 /* fuer die Cliquenheuristic wird min/max_baumweite schon hier berechnet   */
 /* da die Cliquenmenge ohnehin schon gefunden wurde und so Zeit gespart wird.*/ 
 if ( algorithmus == CLIQUENHEURISTIC )
     {min_baumweite=maximum(min_baumweite,
        finde_untere_schranke_durch_maxCliquenseparator_fuer_Cliquenheuristic
                                           (sgraph,dominante_cliquenmenge) );
      min_baumweite=maximum(min_baumweite,kantenbeschraenkung(sgraph));
     }
 hilfsgraph_zum_freigeben=hilfsgraph;
 dominante_cliquenmenge_zum_freigeben=dominante_cliquenmenge;
 return new_sgraph;
}

/*********************************************************************/
/*********************************************************************/
Sgraph baumzerlegung(Slist teilgraphen)
{
 Sgraph sgraph;
 Slist steilgraph;

 for_slist(teilgraphen,steilgraph)
     {init_sgraph((Sgraph)dattrs(steilgraph));
      sgraph=baumzerlegung_control((Sgraph)dattrs(steilgraph));
     }end_for_slist(teilgraphen,steilgraph)

 return sgraph;
}

/*****************************************************************************/
/*               loesche markierungen                                        */
/*****************************************************************************/
/*  Aufgabe:      Gebe den Hilfsgraph wieder frei                            */
/*****************************************************************************/

void free_speicher_baumbreite(void)
{free_hgraph(hilfsgraph_zum_freigeben);
 free_slist_of_menge(dominante_cliquenmenge_zum_freigeben);}

