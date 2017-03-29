/*****************************************************************************/
/*                 A R N B O R G  -  A L G O R I T H M U S                   */
/*                                                                           */
/* Modul        : arnborg.c                                                  */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

#include "mystd.h"
#include "menge.h"
#include "mainwindow.h"

#include "arnborg.h"
#include "optionen.h"
#include "untere_schranke.h"
#include <math.h>
#include "notice.h"
#include "gragra_parser/w_memory.h"    /* malloc verwaltungs modul von T.Lamshoeft*/


/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*      Sgraph   arnborg                           (Sgraph)                  */
/*		 Kontrollprozedur fuer den Arnborgalgorithmus		     */
/*								             */
/*      void     free_speicher_tree_decomposition  (Sgraph)                  */
/*		 gibt den benoetigten Speicherplatz wieder frei		     */
/*								             */
/*****************************************************************************/

/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Verwaltung der Hashtabelle :				     */
/*								             */
/*	void     init_hashtab                 ()                             */
/*		 Initialisierung und Speicherallocation			     */
/*								             */
/*	void     init_hash_multiplikator      ()                             */
/*		 Berechnung und initialisierung des Hashmultiplikators       */
/*								             */
/*	int      bestimme_hashtab_addresse    (Menge)                        */
/*		 Berechnung der Hashadresse aus der Mengeneigenschaft.	     */
/*								             */
/*	void     speichere_in_hashtab         (Menge,C,Sgraph)               */
/*		 Eintragung in die Hashtabelle.				     */
/*								             */
/*	C        finde_hash_element           (Menge,Sgraph)                 */
/*               Suche nach einem Element in der Hashtabelle.		     */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Verwaltung der Separatoren-Datenstrucktur :		     */
/*								             */
/*	C        new_c                        (Menge,sep)         	     */
/*               Initialisierung und Speicherallocation			     */ 
/*								             */
/*	C        add_to_c                     (C ,Menge sep,Sgraph)          */
/*               Listenzeigerverwaltung					     */
/*								             */
/*	void     mein_free_all_c              (C)			     */
/*		 Speicherfreigabe der Separatorenstrucktur und aller	     */
/*		 Unterstruckturen.					     */
/*								             */
/*****************************************************************************/
/* Prozeduren zur Verwaltung der Zusammenhangskomponenten-Datenstruckturen : */
/*		 Ihr Speicher wird durch die 'mein_free_all_c' freigegeben   */
/*								             */
/*	Zk       new_zk                       (C,Menge cj,int groesse)       */
/*               Initialisierung und Speicherallocation			     */ 
/*								             */
/*	Zk       add_to_zk		      (C,Menge cj,int groesse)       */
/*               Listenzeigerverwaltung					     */
/*								             */
/*	Yes      new_yes		      (C,Yes)                        */
/*               Initialisierung und Speicherallocation			     */ 
/*								             */
/*	Yes      add_to_yes		      (C,Yes)                        */
/*               Listenzeigerverwaltung					     */
/*								             */
/*****************************************************************************/
/*								             */
/*	void     print_sep		      (Sgraph,C)		     */
/*								             */
/*	void     print_sort_zk		      (Sgraph,C)		     */
/*								             */
/*	void     speichere_mark		      (Sgraph,C)		     */
/*	         Speicherung eines markierten Zusammenhangskomponenten	     */
/*								             */
/*	void     arnborg_dfs_sgraph		      (Snode)		     */
/*		 Tiefensuche						     */
/*								             */
/*	C        baue_c_zk		      (C,Sgraph,Meng sep)	     */
/*               eine k-Knotenteilmenge wird auf Separatoreigenschaft unter- */
/*               sucht als solcher in die Datenstrucktur uebernommen         */
/*								             */
/*	C        bilde_sep	 (C,Sgraph,int anf,int tiefe,Menge sep)      */
/*		 findet alle k-Knotenteilmengen und ruft fuer jede           */
/*		 'baue_c_zk' auf.					     */
/*								             */
/*	Yes      einbettung_existiert_bereits (C)			     */
/*		 uberprueft ob eine Einbettung in einen k-Baum nach Aufbau   */
/* 		 der Datenstrucktur automatisch gefunden wurde		     */
/*								             */
/*	void     zk_an_sort_anhaengen	      (Zk)			     */
/*								             */
/*	void     sort_zk		      (Sgraph,C)		     */
/*               sortiert die Zusammnehangskomponenten nach Groesse          */
/*								             */
/*	bool     mache_aus_zk_yes	      (Zk)			     */
/*								             */
/*	bool     pruefe_ob_alle_Sep_bereits_unzulaessig  (Zk)		     */
/*               pueft ob fuer jeden Separator eine unzulaessige             */
/*               Zusammenhangskomponente existiert.			     */
/*								             */
/*	void     setze_baumzeiger	      (Zk,Yes)			     */
/*								             */
/*	Yes      pruefe_auf_ktree	      (Sgraph,C)		     */
/*		 prueft ob eine Einbettung in einen k-Baum moeglich ist.     */
/*								             */
/*      Menge    baue_baum2      (Sgraph,Yes wurzel,Snode,Sgraph new_sgraph  */
/*                                                          ,Menge teilbaum) */
/*								             */
/*	Sgraph   gebe_tdec_zurueck2           (Sgraph,C,Yes wurzel)          */
/*    		 gewinnt aus der Einbettung die Baumzerlegung		     */
/*								             */
/*	Sgraph   make_sgraph_from_clique      (Sgraph)              	     */
/*		 baut einen Sgraphen mit einem einzigen Knoten 		     */
/*								             */
/*	Slist zeige_zhk_dfs(zeige,Snode,Slist)				     */
/*								             */
/*	void zeige_zhk(sgraph)						     */
/*								             */
/*****************************************************************************/
/*								             */
/*                lokale    Makros                                           */
/*								             */
/*****************************************************************************/
/*								             */
/*	for_c   (C,C c2)    ----   end_for_c   (C,C c2)			     */
/*                Schleife ueber alle Separatorenelemente             	     */
/*								             */
/*      for_zk  (C,Zk)      ----   end_for_zk  (C,Zk)			     */
/*		  Schleife ueber alle Zk (Zusammenhangs)-elemente	     */
/*                eines Separators.					     */
/*								             */
/*      for_yes (C,Yes)     ----   end_for_yes (C,Yes) 			     */
/*  	          Schleife ueber allen Yes-elementen eine Separators 	     */
/*								             */
/*      for_sort_zk (C,Zk)  ----   end_for_sort_zk (C,Zk)                    */
/*                Schleife von dem kelinsten Zk-element bis zum groessten    */
/*								             */
/*      empty_yes,empty_zk,empty_c					     */
/*                NULL-zeiger der jeweiligen Struckturen		     */
/*								             */
/*****************************************************************************/
/*****************************************************************************/
/*              lokale variablen                                             */
/*****************************************************************************/

/***** Makros fuer den Umgang mit den Datenstruckturen : C,ZK,Yes *****/

#define for_c(a,b)      {if(((b)=(a))!=(C)NULL) do {
#define end_for_c(a,b)  } while(((b)=(b)->suc)!=a);}

#define for_zk(a,b)      {if(((b)=(a)->zk)!=(Zk)NULL) do {
#define end_for_zk(a,b)  } while(((b)=(b)->suc)!=(a)->zk);}

#define for_yes(a,b)      {if(((b)=(a)->yes)!=(Yes)NULL) do {
#define end_for_yes(a,b)  } while(((b)=(b)->suc)!=(a)->yes);}

#define for_sort_zk(a)    {if(((a)=kleinstes_sort_zk)!=empty_zk) do {
#define end_for_sort_zk(a)  } while(((a)=(a)->sortsuc)!=empty_zk);}

#define zattrs(a)         ((Zk)(a->attrs.value.data))
#define yattrs(a)         ((Yes)(a->attrs.value.data))
#define empty_c              ((C)NULL)
#define empty_zk             ((Zk)NULL)
#define empty_yes            ((Yes)NULL)


/***** Eigene Mallocfehler abfrage ****/
/*#undef mein_malloc*/

/*****************************************************************************/
/*              lokale Datenstruckturen                                      */
/*****************************************************************************/

typedef struct  c { 
      Menge        sep;            /*Knotenmenge des Separatoren                    */
      struct c     *suc, *pre;     /*Vorgaenger und NAchfolger der Liste            */
      struct zk     *zk;           /*Zeiger auf zugehoerige Zussammenhangskomponente*/
      struct yes    *yes;          /*              mit |zhk|=k+1                    */
      int          anzahl_zk;
     } *C;

typedef struct zk {
      Menge        cj, cj_c;        /*Knotenmenge der Zhkomp mit und ohne Separator    */     
      struct zk    *pre, *suc;      /*Listen zeiger der zu einem Sep gahoerenden Zhkomp*/
      struct zk    *sortpre, *sortsuc; /*Zeiger zu naechst kleinerem und groesseren Zhk*/
      struct c     *c;              /*Zeiger zur zugehoerigem Separator*/
      int          groesse;         /*groesse von cj                                   */
      struct slist  *pointer;       /*Liste der Einbettung von cj                      */
      struct yes   *yes;
      } *Zk;

typedef struct yes {               /* partiale k-trees */
      Menge        yes, yes_c;     /* listenzeiger*/
      struct c     *c;             /* Zeiger zum zugehoerigen Separator */
      struct yes   *suc, *pre;     
      struct zk    *zk;
      } *Yes;

/*****************************************************************************/
/*              lokale variablen                                             */
/*****************************************************************************/

bool sgraph_gezeichnet;
extern bool zhk_ausgeben;
Slist blaetter;

Zk  groesstes_sort_zk,kleinstes_sort_zk;
int k=0;
int anzahl_sep;
int anzahl_zhk;
int anzahl_zhk2;
int anzahl_unzulaessiger_separatoren;
Slist *hashtab;
bool my_fatal_error;
C cmerken;

float hash_multiplikator;
 
/********************************************************************/
/*                                                                  */
/*            =======================================               */
/*            !Prozeduren zur Hashtabellenverwaltung!               */
/*            =======================================               */
/*                                                                  */
/********************************************************************/
/* init_hashtab :  reserviert Speicher fuer Hashtabelle und         */
/*                 initialisiert diese                              */
/* init_hahmultiplikator : berechnet optimalen hashmultiplikator    */
/*                         fuer optimale Speicherausnutzung fuer die*/
/*                         Hashfunktion                             */
/* bestimme_hastab_addresse : berechent die Hashadresse aus der     */
/*                            den Mengenelementen                   */
/* speichere_in_hashtab     : speichert eineEintrag in der Hashtab  */
/* finde_hashelement  : pruft ob eine Menge eien Hash eintrag hat   */
/*                      und gibt diesen zurueck                     */
/********************************************************************/
/* Hashfunktion:                                                    */
/*    Es wird eine Funktion benoetigt die Separatoren mit moeglichst*/
/*    wenig Kollisionen ueber die Hashtabelle verteilt.             */
/*     Da die Separatoren die Eigenschaft haben sich haeufig in nur */
/*    einem Knoten zu unterscheiden, ist es notwendig fuer aehnliche*/
/*    Mengen verschiedene Hashfunktionswerte zu erhalten.           */
/*     Eine Aufsummierung der Knotennummern der im Separator ent-   */
/*    haltenen Knoten erfuellt diesen Anspruch. Um den Abbildungs-  */
/*    raum moeglichst gut an den verfuegbaren Speicherplatz anzu-   */
/*    gleichen, wird ein Hashmultiplikator aus dem verfuegbaren     */
/*    Speicherplatz und der abbzubildenden Menge berschnet.         */
/*                                                                  */
/*    Hashfunktion: Summe uber i=1 bis k aus : Knotennr*(c**i)      */
/*     wobei        c:= Hashmultiplikator.                          */
/*    c berechnet sich aus: (Speicheplatz/Groess_Graph)**(1/k+1)    */
/*    somit ist sichergestellt, dass der gesamte Speicherplatz ge-  */
/*    nutzt werden kann,und Kollision moeglichst vermiden werden.   */
/*                                                                  */
/*    Im Falle einer Kollision wird der neu Eintrag in Form einer   */
/*    Liste angehaengt, die beim Auslesen lienear durchlaufen wird  */
/*                                                                  */
/*   Anmerkung : Da dieser Algorithmus ohnehin sehr langsam ist und */
/*               auch eine Hashcodeoptimierung wahrscheinich nur zu */
/*               geringen Verbesserungen fuehrt, die sich in der    */
/*               Anwendung praktisch nicht bemerkbar machen wuerden */
/*               bin ich hier auf aufwendigere Verfahren eingegangen*/
/********************************************************************/
/********************************************************************/
/*                 initialisier Hashmultiplikator                   */
/********************************************************************/

void init_hash_multiplikator(void)

{hash_multiplikator=(float)pow( (double)((hashgroesse)/groesse_graph) ,
                         ((double)(1))/((double)(k+1)) ) ;
/* message("hash_multiplikator : %f",hash_multiplikator);*/
}
  

/********************************************************************/
/*                   intialisiere Hashtabelle                       */
/********************************************************************/
/* Es wird der durchs Optionenfenster definierte Speicher angefordert*/
/* sollte der Speicher nicht reichen wird die anforderung durch 4 geteilt.*/
/* Koennen weniger als 50 kByte allociert werden bricht das Programm */
/* mit der Meldung "zu wenig Speicher"  ab			     */
/********************************************************************/

void init_hashtab(void)
{bool again;
 int i;

 mein_malloc(10000);

 do{
    again=false;
    hashtab=(Slist*)w_malloc(sizeof(Slist*)*hashgroesse);
    if (hashtab==NULL)
        {again=true;
	 message("To less memory! I reduce Hashtabelsize\n");
	 hashgroesse=hashgroesse/4;  
         message("I try to allocate %i Bytes\n", hashgroesse*4);
         if(hashgroesse<=50000) 
		{mein_malloc(500000000); }/*loest Fehler aus*/
         init_hash_multiplikator(); 
	 optionen_window();
	}
   }while (again);
 for(i=0;i<hashgroesse;i++)
             {hashtab[i]=empty_slist;}
}

/********************************************************************/
/*                      bestimme Hashadresse                        */
/********************************************************************/

int bestimme_hashtab_addresse(Menge menge)
{int i;
 int hashtab_addr=0;

 for (i=0;i<=groesse_graph;i++)
     {if ( menge[i/mgroesse]&(1<<(i%mgroesse)) ) /*ex i.tes element in Menge */
         {hashtab_addr=(hashtab_addr*hash_multiplikator)+i;}
     } 
/*printf("  hashtab_addr:%i",hashtab_addr);*/
return hashtab_addr;
}

/*******************************************************************/
/*                  speichere in Hashtabelle                       */
/*******************************************************************/

void speichere_in_hashtab(Menge menge, C c, Sgraph sgraph)
{ int hashadr=bestimme_hashtab_addresse(menge);
 if(hashtab[hashadr]!=empty_slist)  {/*printf("*******kollision********");*/}
 hashtab[hashadr]=anhaengen(hashtab[hashadr],c);  
                  
/*printf("\n speicere Menge an Hashadr:%i",bestimme_hashtab_addresse(menge));
printmenge(sgraph,menge);*/}

/*******************************************************************/
/*                 lese aus Hashtabelle                            */
/*******************************************************************/

C finde_hash_element(Menge menge, Sgraph sgraph)
{C c=empty_c;
 Slist element;
 Slist hashelement=hashtab[bestimme_hashtab_addresse(menge)];

/*printf("\nsuche nach Menge :");printmenge(sgraph,menge);*/
 for_slist(hashelement,element)
     {if(mengengleich(menge,((C)(element->attrs.value.data))->sep))
         {c=(C)(element->attrs.value.data);
     /*     printf("...gefunden");  */
          break;}
     }end_for_slist(hashelement,element)
 return c;
}
/********************************************************************/
/*                                                                  */
/*      =================================================           */
/*           !Prozeduren zur Verwaltung der C-stuktur!              */
/*      =================================================           */
/*                                                                  */
/********************************************************************/
/* new_c  , new_yes  , new_zk :                                     */
/*          reserviert Speicherplatz und initialisiert Strucktur    */
/* add_to_c, add_to_yes, add_to_zk :                                */
/*           fuegt neues c/zk/yes in Liste ein                      */
/* mein_free_c : gibt Speicherplatz fuer die C-strucktur inklusive  */
/*               aller Eintraege und unterstruckturen wie Zk und YES*/
/********************************************************************/
/********************************************************************/
/*                 new_c                                            */
/********************************************************************/

C new_c(Menge sep)
{ C new;
  new=(C)mein_malloc(sizeof(struct c));
  new->suc = new;
  new->pre = new;
  new->sep = sep;
  new->zk  = empty_zk;
  new->yes = empty_yes;
  new->anzahl_zk=0;
  return new;
}

/*******************************************************************/
/*             add_to_c                                            */
/*******************************************************************/

C add_to_c(C c, Menge sep, Sgraph sgraph)
{C new;
 new=new_c(sep);
 if (c==empty_c)
     {c=new;}
 else 
     {new->suc=c;
      new->pre=c->pre;
      new->suc->pre=new;
      new->pre->suc=new;
     }
 speichere_in_hashtab(sep,new,sgraph);
 anzahl_sep++;
 return new;
}

/*******************************************************************/
/*                 mein_free c                                     */
/*******************************************************************/

void mein_free_all_c(C c)
{
 C c2,csuc;
 Yes yes2,yessuc;
 Zk zk2,zksuc;
 
 c2=c;
 if(c!=empty_c) do{
       csuc=c2->suc;
       zk2=c2->zk;
       if(c2->zk!=empty_zk) do {
               zksuc=zk2->suc;
               free_slist(zk2->pointer) ;
               mein_free(zk2->cj);
               mein_free(zk2->cj_c);
               mein_free(zk2);
               zk2=zksuc;
       }while(zk2!=c2->zk);
       yes2=c2->yes;
       if(c2->yes!=empty_yes) do{
               yessuc=yes2->suc;
               mein_free(yes2->yes);
               mein_free(yes2->yes_c);
               mein_free(yes2);
               yes2=yessuc;
       }while(yes2!=c2->yes);    
       mein_free(c2->sep);
       mein_free(c2);
       c2=csuc;
 }while(c2!=c);
}      

/********************************************************************/
/*                                                                  */
/*      ====================================================        */
/*      !Prozeduren zur Verwaltung der Yes und Zk-Strucktur!        */
/*      ====================================================        */
/*                                                                  */
/********************************************************************/
/*******************************************************************/
/*                 new_zk                                          */
/*******************************************************************/

Zk new_zk(C c, Menge cj, int groesse)
{ Zk new;
  new=(Zk)mein_malloc(sizeof(struct zk));
  new->suc = new;
  new->pre = new;

  new->groesse = groesse;
  new->sortpre=empty_zk;
  new->sortsuc=empty_zk;
  new->c   = c;
  new->cj = cj;
  new->cj_c = menge_ohne_menge(copy_menge(cj),c->sep);
  new->pointer=empty_slist;
  new->c->anzahl_zk++;
  new->yes=empty_yes;
  return new;
}

/*******************************************************************/
/*             add_to_zk                                           */
/*******************************************************************/

Zk add_to_zk(C c, Menge cj, int groesse)
{Zk new;
 new=new_zk(c,cj,groesse);
 if (c->zk==empty_zk)
     {c->zk=new;}
 else 
     {new->suc=c->zk;
      new->pre=c->zk->pre;
      new->suc->pre=new;
      new->pre->suc=new;
     }
 return new;
}

/*******************************************************************/
/*                 new_yes                                         */
/*******************************************************************/

Yes new_yes(C c, Menge yes)
{ Yes new;
  new=(Yes)mein_malloc(sizeof(struct yes));
  new->suc = new;
  new->pre = new;
  new->c   = c;
  new->yes = yes;
  new->yes_c = menge_ohne_menge(copy_menge(yes),c->sep);
  new->zk=empty_zk; 
  return new;
}

/*******************************************************************/
/*             add_to_yes                                           */
/*******************************************************************/

Yes add_to_yes(C c, Menge yes)
{Yes new;
 new=new_yes(c,yes);
 if (c->yes==empty_yes)
     {c->yes=new;}
 else 
     {new->suc=c->yes;
      new->pre=c->yes->pre;
      new->suc->pre=new;
      new->pre->suc=new;
     }
 return new;
}

/*********************************************************************/
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*                   !      Print Prozeduren   !                    */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/*********************************************************************/

void print_sep(Sgraph sgraph, C c)
{C c2;
 Zk zk;
 Yes yes;
 for_c(c,c2)
    {printf("\nseparator:   ");printmenge(sgraph,c2->sep);
     printf("\n Zhangskomp:");
     for_zk(c2,zk)
       {printmenge(sgraph,zk->cj_c);printf("\n        ");
       }end_for_zk(c2,zk)
     for_yes(c2,yes)
       {printmenge(sgraph,yes->yes_c);printf("\n        ");
       }end_for_yes(c2,yes)
     }end_for_c(c,c2)
printf("\n *************************\n");
}

/*********************************************************************/

void print_sort_zk(Sgraph sgraph, C c)
{Zk zk;
 C c2;
 Yes yes;

 printf("\n yes:  ");
 for_c(c,c2)
    {for_yes(c2,yes)
       {printmenge(sgraph,yes->yes);printf("\n");
       }end_for_yes(c2,yes)
    }end_for_c(c,c2)
 printf("\n sortierte Zkliste  : \n ");
 for_sort_zk(zk)
    {printmenge(sgraph,zk->cj);printf("\n");}
 end_for_sort_zk(zk)
}

/*********************************************************************/
/*********************************************************************/
/*                                                                   */
/*         =================================================         */
/*             ! Finde Separatoren und speichere diese !             */
/*         =================================================         */
/*                                                                   */
/*********************************************************************/
/*********************************************************************/
/*     speichere neu markierte Knoten als Zussamenhangskomponente    */
/*********************************************************************/
/*                                                                   */
/* aufrufende Prozedur   :    baue_c_zk                              */
/* benoetigte Prozeduren :    mark_menge,unmark_sgraph,add_to_c,     */
/*                            arnborg_dfs_sgraph,speichere_mark              */
/*                                                                   */
/* Parameter          : C c : Zeiger auf aktuelle C-Strucktur        */
/*                      Sgraph sgraph : aktueller Graph              */
/* Rueckgabeparameter : void                                         */
/*********************************************************************/

void speichere_mark(Sgraph sgraph, C c)
{
 Snode snode;
 Menge menge=new_menge();
 int groesse=0;
 anzahl_zhk++;

 for_all_nodes(sgraph,snode)
    {if(snode->attrs.value.integer==1)          /* neu markiert => gehoert zur Zhkomp. */
         {add_to_menge(menge,snode);    /* => an Zhkomp anhaengen              */
          snode->attrs.value.integer=2;         /* neu markiert in alt markiert aendern*/
          groesse++;}
    }end_for_all_nodes(sgraph,snode)
 if (groesse>k+1)                         /* unterscheid zwischen k+1 und groesser*/
      {add_to_zk(c,menge,groesse);
       anzahl_zhk2++;}        
 else  {add_to_yes(c,copy_menge(menge));}
}

/*********************************************************************/
/*        Pruefe ob Menge ein Separator ist                          */
/*********************************************************************/
/*                                                                   */
/* aufrufende Prozedur   :    bilde_sep                              */
/* benoetigte Prozeduren :    mark_menge,unmark_sgraph,add_to_c,     */
/*                            arnborg_dfs_sgraph,speichere_mark              */
/*                                                                   */
/* Parameter          : C c : Zeiger auf aktuelle C-Strucktur        */
/*                      Sgraph sgraph : aktueller Graph              */
/*                      Menge sep     : zu untersuchende Menge       */
/* Rueckgabeparameter : C c : aktualiserter Zeiger auf C-Strucktur   */
/*********************************************************************/
/* Funktionbeschreibung:                                             */
/*    Ausgehend von einem beliebigen Knoten wird der Sgraph          */
/*    durchlaufen ohne einen Knoten aus sep zu passieren.            */
/*    Dabei wird jeder markierte Knoten markiert.                    */
/*     Sind anschliessend alle Knoten markiert ist Sep kein Separator*/
/*    anderenfalls ist sep ein Separator und die erste Zusammenhangs-*/
/*    komponente entspricht den markierten Knoten.                   */
/*    Betracht nun die noch nicht markierten knoten. Suche noch nicht*/
/*    markierten Knoten und Durchlaufe den Graph wie oben.           */
/*     Die neu markierten Knoten entsprechen der 2. Zhkomponennte.   */
/*   Fahre so fort bis alle Knoten markiert.                         */
/*********************************************************************/
/*********************************************************************/
/*         rekursive  Tiefensuche  'fuer baue_c_zk'                  */
/*********************************************************************/

void arnborg_dfs_sgraph(Snode snode)
{Sedge sedge;
 snode->attrs.value.integer=1;
 for_sourcelist(snode,sedge)
    {if(sedge->tnode->attrs.value.integer!=1)
        { arnborg_dfs_sgraph(sedge->tnode); }
     }end_for_sourcelist(snode,sedge)
}

/********************************************************************/
/*            erkenne Separatoren                                   */
/********************************************************************/

C baue_c_zk(C c, Sgraph sgraph, Menge sep)
{bool flag,flag2;
 Snode snode;
 flag2=FALSE; 
 flag =FALSE;

 /* printf("\n separator:  ");printmenge(sgraph,sep);*/
 unmark_sgraph(sgraph);
 mark_menge(sgraph,sep);
 for_all_nodes(sgraph,snode)         /* suche unmarkiert Knoten */
    {if(snode->attrs.value.integer==0)
        {if(!flag)                       
           {arnborg_dfs_sgraph(snode);            /* erstes mal extra behandeln, da noch kein Sep */
            flag=TRUE;}
          else
           {if(!flag2)
               {c=add_to_c(c,copy_menge(sep),sgraph); /*erste Zhkomp gefunden, speicher Separator*/
                /*printf("\ngefundener Sep:");printmenge(sgraph,sep);*/}
            speichere_mark(sgraph,c);                 /* speicher zhkomponente*/
            mark_menge(sgraph,sep);
            arnborg_dfs_sgraph(snode);
            flag2=TRUE;}            
       }
     }end_for_all_nodes(sgraph,snode)
if (flag2)
  {speichere_mark(sgraph,c);}          /* wenn Separator ekannt=> speichere letzte Zhkomp*/
return c;
}

/*********************************************************************/
/*        Bilde alle k-Knotenteilmengen des Sgraphens                */
/*********************************************************************/
/*                                                                   */
/* aufrufende Prozedur   :    tree-decomp                            */
/* benoetigte Prozeduren :    baue_c_zk                              */
/*                                                                   */
/* Parameter          : C c : Zeiger auf aktuelle C-Strucktur        */
/*                      Sgraph sgraph : aktueller Graph              */
/*                      int anf : in diesem Schritt erster zu-       */
/*                                laessiger Knoten                   */
/*                      int tiefe : Anzahl der bereits aufgenommenen */
/*                                  Knoten                           */
/*                      Menge menge : enthaelt die bereits aufge-    */
/*                                    nommenen Knoten                */
/* Rueckgabeparameter : C c : aktualiserter Zeiger auf C-Strucktur   */
/*                                                                   */
/*********************************************************************/
/* Funktionbeschreibung:                                             */
/*       Es wird rekursiv eine k-Teilmenge der Knotenmenge aufgebaut */
/*       In jedem Schritt wird ein Knoten angehaengt, dann der       */
/*       naechste Schritt aufrerufen und nach rueckkehr der ange     */
/*       haengte Knoten wieder entfernt.                             */
/*       Sobald tiefe == k+1,d.h. essind k Knoten in der Menge, wird */
/*       'baue_c_zk' aufgerufen um zu testen, ob diese Menge einen   */
/*       Separator darstellt.                                        */
/*********************************************************************/

C bilde_sep(C c, Sgraph sgraph, int anf, int tiefe, Menge sep)
{
 register int i;
 notify_dispatch();

 if(tiefe==k+1)                                       /*bereits k Knoten aufgenommen? */
   {/*printf("\nk-menge:");printmenge(sgraph,sep); */
    c=baue_c_zk(c,sgraph,sep);}                  /* untersuche ob k-Menge == Separator*/
 else 
   {for(i=anf;i<=groesse_graph-k+tiefe;i++)      /*Schleife ueber alle in dieser Stufe*/
       {if(tiefe==1) {fortschritt(i);}                           /* moeglichen Knoten*/
        sep[i/mgroesse]=sep[i/mgroesse]|1<<(i%mgroesse);          /*Knoten i anhaengen*/
        c=bilde_sep(c,sgraph,i+1,tiefe+1,sep);               /*naechsten Knoten suchen*/
        sep[i/mgroesse]=sep[i/mgroesse]&(255-(1<<(i%mgroesse)));}/*Knoten i entfternen*/
   }
return c;
}

/*********************************************************************/
/*   Exisitier eine Einbettung durch gefundene Zusammenhangskomp ?   */
/*********************************************************************/
/*                                                                   */
/* Parameter          : C c : Zeiger auf aktuelle C-Strucktur        */
/* Rueckgabeparameter : Yes : Zeiger auf ein Yes-Element oder NULL   */
/*                                                                   */
/*********************************************************************/
/* Funktionbeschreibung:                                             */
/*       Es wird ein Separator gesucht dessen saemtliche Zusammen-   */
/*	 hangskomponente die Goresse k+1 haben. Existiert ein solcher*/
/*       so muss nicht weiter nach einer Einbettung gesucht werden.  */
/*       Der Einbettungsalgorithmus wuerde eine solche Einbettung    */
/*       nicht beruecksichtigen, was diesen Extraschritt notwendig   */
/*       macht.							     */
/*       Exisitert kein solcher Separator, so wird NULL zurueck-     */
/*	 gegeben.						     */	
/*********************************************************************/


Yes einbettung_existiert_bereits(C c)
{C c2;
 for_c(c,c2)
     {if( c2->anzahl_zk==0 )
           {return c->yes;}
     }end_for_c(c,c2)
return empty_yes;
}

/********************************************************************/
/*                                                                  */
/*       ====================================================       */
/*       ! Sortiere die gefundenen Zusammenhangskomponenten !       */
/*       ====================================================       */
/*                                                                  */
/********************************************************************/
/********************************************************************/

void zk_an_sort_anhaengen(Zk zk)
{groesstes_sort_zk->sortsuc=zk;
 zk->sortpre=groesstes_sort_zk;
 groesstes_sort_zk=zk;
 zk->sortsuc=empty_zk;
}

/*********************************************************************/

void sort_zk(Sgraph sgraph, C c)
{
Slist *feld;
int i;                        
Zk zk;
C c2;
Slist element;
bool flag=FALSE;

init_fortschritt("sortiere Zhkomp.",10);

feld=(Slist*)mein_malloc(sizeof(Slist*)*(groesse_graph-k-1));/* Der groeste Zhk hat groesse-1 und der kleiste k+2*/
                           /* Daraus resultiert  groesse-k-1*/

for(i=0;i<groesse_graph-k-1;i++)
    {feld[i]=empty_slist;}

for_c(c,c2)
    {for_zk(c2,zk)
        {feld[zk->groesse-k-1]=anhaengen(feld[zk->groesse-k-1],zk);
        }end_for_zk(c2,zk)
    }end_for_c(c,c2)
 
fortschritt(5);
for(i=0;i<groesse_graph-k-1;i++)
  {for_slist(feld[i],element)
     {zk=zattrs(element);
      if(!flag)
           {kleinstes_sort_zk=zk;
            groesstes_sort_zk=zk;
            flag=TRUE;}
       zk_an_sort_anhaengen(zk);
      }end_for_slist(feld[i],element)
   }
fortschritt(10);
w_free((char *)feld);
}

/********************************************************************/
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*         !    Pruefe auf Einbettung in einen k-Tree      !        */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/
/*  Es wird fuer jede Zusammenhangskomponente in aufsteigender      */
/*  Groesse geprueft, ob fuer sie eine Einbettung existiert. Ist    */
/*  dies der Fall so wird die Zhkomponente zum partialen k-Baum     */
/*  Sobald ein Separator gefundnen ist dessen Zhkomp. alle partiale */
/*  k-Baeume sind, ist auch der gesammte Graph ein partieller k-    */
/*  Baum.							    */
/*  Andererseits, sobald fuer jeden Separator eine nichtzulaessige  */
/*  Zusammenhangskomponente gefunden wurde ist es nicht moeglich    */
/*  eine Einbettung fuer den gesammten Graph zu finden und die      */
/*  Prozedur wird abgebrochen.					    */
/********************************************************************/

bool  mache_aus_zk_yes(Zk zk)
{Yes new_yes;

 new_yes=add_to_yes(zk->c,zk->cj);
 new_yes->zk=zk;
 zk->yes=new_yes;
 zk->c->anzahl_zk--;
 if(zk->c->anzahl_zk==0)
   {return true;}
 return false;
}

/*********************************************************************/
/*********************************************************************/

bool pruefe_ob_alle_Sep_bereits_unzulaessig(Zk zk)
{
 if(zk->c->anzahl_zk>0)
     { zk->c->anzahl_zk=-1;
       anzahl_unzulaessiger_separatoren++;
       if(anzahl_unzulaessiger_separatoren==anzahl_sep)
               {return true;}
     }
 return false;}

/*********************************************************************/

void setze_baumzeiger(Zk zk, Yes yes)
{zk->pointer=anhaengen(zk->pointer,yes);
 if (yes->zk==empty_zk)
         {blattliste=anhaengen(blattliste,yes->yes);
         }
}

/*********************************************************************/

Yes pruefe_auf_ktree(Sgraph sgraph, C c)
{Zk zk;
 C c2;
 Yes wurzel=empty_yes;
 Yes yes;
 bool ktree=FALSE;
 Snode snode;
 bool answer_zk=false;
 Menge menge=new_menge();
/* Menge menge2=new_menge();*/
 Menge menge3=new_menge();
 Snode snode2;
 char text[30];
 int fortschr=0;

sprintf(text,"pruefe Bweite=%i",k);

init_fortschritt(text,anzahl_zhk2);
/*printf("\n********* for_sort_zk************************");*/
for_sort_zk(zk)
   {fortschritt(++fortschr);
    answer_zk=false;
    menge=empty_menge(menge);
    /*  printf("\naktuelle ZKKomp sep: ");printmenge(sgraph,zk->c->sep);
       printf("\nm-Sep ");printmenge(sgraph,zk->cj_c);   */
     

    /********   for each v element von ci     */
    for_menge(zk->cj_c,snode)
      {/*printf("\n    aktuelle Knoten %s : ",snode->label);*/

       /********   for all k-vertex-Separatores c in ci vereinigt v */
          
          menge3=copy_in_menge(zk->c->sep,menge3);
          add_to_mengem(menge3,snode); /*Makro ist schneller*/
          for_menge(zk->c->sep,snode2)
               {entferne_from_mengem(menge3,snode2); /*Makro->schneller*/
                c2=finde_hash_element(menge3,sgraph);
                /*printf("\n        ist menge sep (cm Menge;)?:");printmenge(sgraph,menge3);*/
                if (c2==empty_c)
                        {/*printf("    ---no---")*/;}
                else    {/*printf("     ---yes---");*/
            
                        /*****   consider all clm in (cji-ci)vereinigt cm   (== clm-cm in cij) */
                        for_yes(c2,yes)
                            {
                             if(menge_teilmenge_von_menge(yes->yes_c,zk->cj_c))
                                {/*printf("\n            zulaessige YesMenge : ");
                                 printmenge(sgraph,yes->yes);*/

                                 /*if(!(menge_teilmenge_von_menge(yes->yes,menge)))*/
                                 /*printf("\n an zk:");*/
                                 /*printmenge(sgraph,menge2);printf(" anhaengen ");
                                 printmenge(sgraph,c2->); 
                                 menge2=mengenvereinigung(menge2,yes->yes);*/
                                 setze_baumzeiger(zk,yes);
                                 menge=mengenvereinigung(menge,yes->yes);
                                 }
                           }end_for_yes(c2,yes)
                      /*mein_free(menge2);*/
                      }
               add_to_mengem(menge3,snode2); /*Makro->schneller*/
              }end_for_menge(zk->c->sep,snode2)

          /********* if their union over m and l contains cji-ci then cij=>yes */
          if(menge_teilmenge_von_menge(zk->cj_c,menge))
              {ktree=mache_aus_zk_yes(zk);
               answer_zk=true;
              /* printf("\n        aus zk :");printmenge(sgraph,zk->cj);
               printf(" wird yes menge");*/
               separatorenliste=anhaengen(separatorenliste,zk->c->sep);
               break;}  
   /********* end do */
       }end_for_menge(zk->cj_c,snode)
/* if G has a Separator such that all Clm graphs have answer YES then G */
/*      is a partial tree  return YES*/
    if(ktree)
       {wurzel=zk->yes;
        break;}

/* if no answer was set for Cji then set the answer to NO */
/* if each Separator of G has a Clm with answer No then G is not a partial k-tree*/
    if(!answer_zk)
      {if (pruefe_ob_alle_Sep_bereits_unzulaessig(zk))
               {break;}}
   }end_for_sort_zk(zk)
mein_free(menge);
mein_free(text);
return wurzel;
}

/*********************************************************************/
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*         ! Baue Baumzerlegung aus gefundener Einbettung  !        */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/*********************************************************************/
/*   erstes slist-element kann dabei nicht verloren gehen !!!!       */
/*********************************************************************/
/*
void entferne_doppelte_aus_slist(Slist slist)
{
Slist slist2,element,slist3=empty_slist;
for_slist(slist,element)
    {slist2=element;
     while((slist2=slist2->suc)!=slist)
         {if(yattrs(slist)->c==yattrs(slist2)->c)
            {slist3=anhaengen(slist3,slist2);
             break;}
         }
    }end_for_slist(slist,element)
 for_slist(slist3,element)
     {subtract_immediately_from_slist(slist,element->attrs.value.data);
     }end_for_slist(slist3,element)
free_slist(slist3);
}
*/
/*********************************************************************/

Menge baue_baum2(Sgraph sgraph, Yes wurzel, Snode snode, Sgraph new_sgraph, Menge teilbaum)
{
Snode neuer_snode;
Slist element;
Yes next_yes;
Menge smenge,menge2,restmenge;
Menge menge=new_menge();

 /*   separator generieren*/
/*printf("\ncall baue baum fuer teilbaum:");printmenge(sgraph,teilbaum);*/
menge2=copy_menge(teilbaum);
   /*if(wurzel->zk->pointer!=wurzel->zk->pointer->suc)*/
      /*kreiere Snode fuer Separator*/
      {smenge=copy_menge(teilbaum);
       smenge=mengenschnitt(smenge,wurzel->c->sep);
       restmenge=copy_menge(smenge);
       restmenge=add_mengen_disjoint(restmenge,wurzel->c->sep);       
/*  printf("\n=====Restmenge:");printmenge(sgraph,restmenge);*/

       neuer_snode=make_node(new_sgraph,make_dattr(
                (save_menge(sgraph,smenge))));
       my_set_nodelabel(sgraph,neuer_snode,smenge);
       if(snode!=(Snode)NULL)
             {/*printf("\n vorheriger snode: %s",snode->label);*/
              make_edge(snode,neuer_snode,empty_attr);
             }
       neuer_snode->x=350;
       neuer_snode->y=350;
       snode=neuer_snode;
      }
if(size_of_menge(sgraph,teilbaum)>k+1)   /* fuer nicht urspruenglich yes (d.h. Zusammnehangskomp)*/
/* teilbaum als Knoten generieren*/
   {  neuer_snode=make_node(new_sgraph,make_dattr(
                (save_menge(sgraph,teilbaum))));
       my_set_nodelabel(sgraph,neuer_snode,teilbaum);
       if(snode!=(Snode)NULL)
             {make_edge(snode,neuer_snode,empty_attr);}
        neuer_snode->x=350;
       neuer_snode->y=350;
       snode=neuer_snode;

     /* for_yes(wurzel->c*/
      for_slist(wurzel->zk->pointer,element)
       {next_yes=yattrs(element);
        menge=copy_in_menge(next_yes->yes,menge);
        if( (ist_menge_leer(restmenge)) ||
               (!menge_teilmenge_von_menge(restmenge,next_yes->yes )) )
             {baue_baum2(sgraph,next_yes,snode
                                    ,new_sgraph,mengenschnitt(menge,teilbaum));
              menge2=menge_ohne_menge(menge2,next_yes->yes_c);
             }
       }end_for_slist(wurzel->zk->pointer,element)


  /*  printf("\nRest von Teilbaum ");printmenge(sgraph,menge2);*/
       neuer_snode=make_node(new_sgraph,make_dattr(
                (save_menge(sgraph,menge2))));
       my_set_nodelabel(sgraph,neuer_snode,menge2);
       make_edge(snode,neuer_snode,empty_attr);
       neuer_snode->x=350;
       neuer_snode->y=350;


   }
else {  /* urspruengliche yeskomp*/
    /*   printf("\ndies war ein blatt Blatt ");*/
       neuer_snode=make_node(new_sgraph,make_dattr(
                (save_menge(sgraph,teilbaum))));
       my_set_nodelabel(sgraph,neuer_snode,teilbaum);
       make_edge(snode,neuer_snode,empty_attr);
       neuer_snode->x=350;
       neuer_snode->y=350;
       snode=neuer_snode;}
  
mein_free(menge);
return NULL;
}

/******************************************************************/
/*********************************************************************/

Sgraph gebe_tdec_zurueck2(Sgraph sgraph, C c, Yes wurzel)
{Sgraph new_sgraph;
 /*Menge menge=new_menge();*/
 Snode snode;
 Yes yes;

/* for_all_nodes(sgraph,snode)
    {menge=add_to_menge(menge,snode);
    }end_for_all_nodes(sgraph,snode)*/
 new_sgraph=make_graph(make_dattr(sgraph));
 new_sgraph->directed=TRUE;
 set_graphlabel(new_sgraph,strsave("tree-decomposition"));
  snode=make_node(new_sgraph,make_dattr(save_menge(sgraph,wurzel->c->sep)));
 my_set_nodelabel(sgraph,snode,wurzel->c->sep);
 snode->x=370;snode->y=370;

 for_yes(wurzel->c,yes)
    {baue_baum2(sgraph,yes,snode,new_sgraph
                   ,yes->yes);
    }end_for_yes(wurzel->c,yes) 

/* for_yes(wurzel->c,yes)
    {
     for_slist(yes->zk->pointer,element)
       {next_yes=yattrs(element);
        menge=copy_in_menge(next_yes->yes,menge);
             {baue_baum2(sgraph,next_yes,snode
                                    ,new_sgraph,mengenschnitt(menge,teilbaum));
              menge2=menge_ohne_menge(menge2,next_yes->yes_c);
             }
       }end_for_slist(yes->zk->pointer,element)
 ,yes->yes);
    }end_for_yes(wurzel->c,yes)
*/


 /*  for_slist(wurzel->zk->pointer,element)
       {next_yes=yattrs(element);
        menge=copy_in_menge(next_yes->yes,menge);
       *//* if(!menge_teilmenge_von_menge(smenge,next_yes->yes ))*/
     /*        {baue_baum2(sgraph,next_yes,snode
                                    ,new_sgraph,menge);
     */    /*     menge2=menge_ohne_menge(menge2,next_yes->yes_c);*/
     /*        }
       }end_for_slist(wurzel->zk->pointer,element)
*/return new_sgraph; 
}

/********************************************************************/
/*********************************************************************/
/*Slist finde_alle_blaetter2(Separator separator)
{Sgraph sgraph2;
 Slist ssgraph,slist=empty_slist;

  for_slist((Slist)separator->sgraphen_old,ssgraph)
         {sgraph2=(Sgraph)ssgraph->attrs.value.data;
          if(  ( (Separator)(dattrs(sgraph2)) )->blatt_old  ) 
             {slist=anhaengen(slist,sgraph2);}
          else {slist=add_slists(slist,finde_alle_blaetter((Separator)(dattrs(sgraph2))));}
         }end_for_slist(separator->sgraphen_old,ssgraph)
   return slist;
}*/

/*********************************************************************/
/*void ordne_blaetter2(newsgraph,snode1)
Sgraph newsgraph;
Snode snode1;

{Snode snode2,best_snode=empty_snode;
 int size,maxsize=0;
 Menge menge1,menge2;

 for_all_nodes(newsgraph,snode2)
       {menge1=baue_menge_aus_slist((Slist)snode1->attrs.value.data);
        if( (snode1->slist==(Sedge)NULL) && (snode1->tlist==(Sedge)NULL) )
       */ /* Kante darf noch nicht behandelt sein !!!*//*
             {menge2=baue_menge_aus_slist( (Slist)snode2->attrs.value.data);
              size=size_of_mengenschnitt(newsgraph,menge1,menge2);
              if (size>maxsize)
                  {best_snode=snode2;
                   maxsize=size;}
       }}end_for_all_nodes(newsgraph,snode2)

 if(maxsize!=0)   *//*ansonsten war kein unbehandelter Knoten mehr vorhanden*//*
         {make_edge(snode1,best_snode,empty_attr);
          ordne_blaetter(best_snode);}
}*/
/*********************************************************************/
/*Sgraph gebe_tdec_zurueck(Sgraph sgraph,C c,Yes wurzel)
{Snode snode,newsnode;
 Menge menge=new_menge();
 Slist blaetter,blatt;
 *//* first node *//*
 
 blaetter=finde_alle_blaetter2(separator);
 for_slist(blaetter,blatt)
        {menge=empty_menge(menge);
         for_all_nodes((Sgraph)dattrs(blatt),snode)
                  {menge=add_to_menge(menge,snode);
                  }end_for_all_nodes((Sgraph)dattrs(blatt),snode)
         newsnode=make_node(new_sgraph,make_dattr(save_menge(
                                                    (Sgraph)  dattrs(blatt),menge)));
         newsnode->x=100;newsnode->y=100;
         my_set_nodelabel((Sgraph)dattrs(blatt),newsnode,menge);
        }end_for_slist(blaetter,blatt)

*//*  Verbinde diejenigen knoten deren Schnitt am groessten ist.  *//*
 
ordne_blaetter2(new_sgraph,new_sgraph->nodes);
}
*/
/********************************************************************/
/********************************************************************/

Sgraph make_sgraph_from_clique(Sgraph sgraph)
{Sgraph new_sgraph=make_graph(make_dattr(sgraph));
 Snode snode= make_node(new_sgraph,make_dattr(make_slist_of_sgraph(sgraph)));
 my_set_nodelabel2(sgraph,snode,(Slist)snode->attrs.value.data);
 snode->x=100;snode->y=100;
 new_sgraph->directed=TRUE;
 max_baumweite= min_baumweite=groesse_graph-1;
 return new_sgraph;
}

/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*          !   Ablaufkontrolle fuer Arnborgalgorithmus   !         */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/

Sgraph tree_decomp_control(Sgraph sgraph)
{
 C c=empty_c;
 Yes wurzel=empty_yes;
 Sgraph new_sgraph=empty_sgraph;
 bool berechnet=true;
 char text[30];

 cmerken=empty_c;
 sgraph_gezeichnet=false;
 my_fatal_error=false;

 separatorenliste=empty_slist;
 blattliste=empty_slist;

 /*beginne mit unterer Schranke*/
 k=finde_untere_schranke_durch_maxCliquenseparator(sgraph)-1;

 if( ((groesse_graph-1)*groesse_graph)/2 == anzahl_kanten)  /*vollstaendige Clique erkennt ALgorithmus nicht*/
       {return make_sgraph_from_clique(sgraph);}
       do { k++;
       anzahl_unzulaessiger_separatoren=0;
       init_hash_multiplikator();
       init_hashtab();
       blaetter=empty_slist;
       anzahl_sep=0;                      /* zaehle Separatoren mit       */
       anzahl_zhk=0;                      /* zaehle Zusammenhangskomp.mit */
       anzahl_zhk2=0;   
      /* message("\nFuer k=%i:",k);*/
       sprintf(text,"searching for separators k=%i",k);
       init_fortschritt(text,groesse_graph-k+1);
       c=bilde_sep(c,sgraph,1,1,new_menge());   /*finde und speichere Separatoren   */
       if(c!=empty_c)                           /* wenn mindestens einner existiert */
           {/*print_sep(sgraph,c) ;*/
            /*message("\n %i Separatoren gespeichert ",anzahl_sep);
            message("\n %i Zusammenhangskomponente gespeichert ",anzahl_zhk);*/
            /*print_sep(sgraph,c);*/
            if (empty_yes!=(wurzel=einbettung_existiert_bereits(c)))  {w_free((char *)hashtab);break;}  
            sort_zk(sgraph,c);                 /*dann sortiere sie            */
            /*print_sort_zk(sgraph,c);*/
            wurzel=pruefe_auf_ktree(sgraph,c);
            if(empty_yes!=wurzel)
                         {w_free((char *)hashtab);break;}         /*und pruefe auf Einbettung in k-Baum */
            if (k==15)   {break;} 
            w_free((char *)hashtab);
            mein_free_all_c(c);                /*gebe Separatoren und Zhk wieder frei*/
            c=empty_c;
           }

       if ((k>=2) && (nextes_k_frage()==0))       /*Abfrage auf Fortsetzen des Slgortihmuses*/
           {berechnet=false;
            break;}
       }while(true);
   /*  printf("****baumweite betraegt %i *****",k);  */
   w_free((char *)hashtab);                              /*gebe Hashtabelle frei */
   if(berechnet==true)                  /**** gebe new_sgraph zurueck an GraphEd *******/
         {new_sgraph=gebe_tdec_zurueck2(sgraph,c,wurzel);
  
      mein_free_all_c(c);   /*   new_sgraph=konvert_listen(sgraph);
      print_daten(sgraph);  
      baue_baumzerlegung_aus_listen(sgraph);
    */ }
 min_baumweite=max_baumweite=k;     
 mein_free(text);
 end_fortschritt();
 return new_sgraph;
}

/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
Sgraph tree_decomp(Slist teilgraphen)
{
 Sgraph sgraph;
 Slist steilgraph;
 int lok_bw=0;

 for_slist(teilgraphen,steilgraph)
     {init_sgraph((Sgraph)dattrs(steilgraph));
      sgraph=tree_decomp_control((Sgraph)dattrs(steilgraph));
      lok_bw=maximum(lok_bw,max_baumweite);
     }end_for_slist(teilgraphen,steilgraph)

  min_baumweite=max_baumweite=lok_bw;     

 return sgraph;
}

/*********************************************************************/
/*********************************************************************/

/*********************************************************************/
Slist zeige_zhk_dfs(int zeige, Snode snode, Slist slist)
{
Sedge sedge;
bool source=false;

for_sourcelist(snode,sedge)
        {slist=zeige_zhk_dfs(!zeige,sedge->tnode,slist);
         source=true;
   }end_for_sourcelist(snode,sedge)
if((source)&&(zeige))
  {slist=anhaengen(slist,snode);}
return slist;
}
/*********************************************************************/
void zeige_zhk(Sgraph sgraph)
{Slist slist;
 slist=zeige_zhk_dfs(false,sgraph->nodes,empty_slist);
  group_set(create_graphed_group_from_slist(slist),
                                           NODE_TYPE,my_node_type,0);
 }    
 
/*********************************************************************/
/*********************************************************************/
