/*******************************************************************************/
/*                                                                             */
/*                 A l l g e m e i n e   P r o z e d u r e n                   */
/*                                                                             */
/* Modul        : mystd.c						       */
/* erstellt von : Nikolas Motte                                                */
/* erstellt am  : 10.12.1992                                                   */
/*                                                                             */
/*     Dieses Modul enthaelt Funktionen, die fuer fast alle anderen Module     */
/*     benoetigt werden.						       */
/*******************************************************************************/

#include "mystd.h"
#include "mainwindow.h"
#include "notice.h"
#include <setjmp.h>
#include "gragra_parser/w_memory.h"    /* malloc verwaltungs modul von T.Lamshoeft*/
/********************************************************************************/
/*						                                */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		                */
/*								                */
/********************************************************************************/
/*   Ausgabeprozeduren:						                */
/*								                */
/*	void 		printsnode		(Snode) 			*/
/*			gibt den label eines Snodes aus.			*/
/*								                */
/*	void 		printslist		(Slist)				*/
/*			gibt eine Slist von Snodes aus (die Label)		*/
/*								                */
/*	void 		my_print_sgraph		(Sgraph)			*/
/*	void 		mein_print_sgraph	(Sgraph)			*/
/*                      zwei Ausgabeprozeduren fuer verschiedene Anwendungen	*/
/*								                */
/*	void 		print_cliquenmenge	(Sgraph,Slist cliquenmenge)	*/
/*                      gibt eine Slist von Mengen aus. 			*/
/*								                */
/********************************************************************************/
/*   Wegen verwendetenen 'gcc'-compiler notwendig:				*/
/*   								                */
/*	Attributes	make_attr	 	(va_alist)			*/
/*        		Der 'gcc'-Compiler kann make_attr nicht korrekt unter   */
/*			seinem Namen ausfuehren.Daher wurde hier diese Funktion */
/* 			unter anderne Namen aber kopiert.			*/
/*			bei Aenderungen von 'make_attr, muss dies hier auch	*/
/*			geaendert werden.					*/
/*								                */
/********************************************************************************/
/*   Eigene mein_malloc-Ueberwachung:						*/
/*      Kontroliert die Fehlermeldung von 'mein_malloc' bzw. 'free'		*/
/*								                */
/*	void 		free_all		()				*/
/*								                */
/*	char* 		mein_mein_malloc		(int groesse)		*/
/*								                */
/*	void		mein_free		(char* zeiger)			*/
/*								                */
/********************************************************************************/
/*   Initialisierungsprozeduren:						*/
/*								                */
/*	int 		anzahl_nodes		(Sgraph)			*/
/*			Es wird die Knotenanzahl des Sgraphen zurueckgegeben	*/
/*								                */
/*	void		init_sgraph		(Sgraph)			*/
/*			initialisiert Sgraph : Die Attributes-Felder der Snodes */
/*			werden auf '0' gesetzt. 'anzahl_nodes' und 		*/
/*			'anzahl_edges' werden berechnet.			*/
/*			Die 'nr'-Felder der Snodes werden von 1 beginnend fort- */
/*			laufend nummeriert.					*/
/*								                */
/*         void       init2_sgraph(sgraph)                                      */
/*								                */
/********************************************************************************/
/*    sonstiges:								*/
/*								                */
/*	bool 		slists_identical	(Slist,Slist)			*/
/*                      prueft ob beide Slists identische Elemente beinhalten	*/
/*								                */
/********************************************************************************/


typedef struct my_attr    {
          int             flag;
          Snode           original_snode;
        }         *My_attr;

#define flag(a)            (((My_attr)(a->attrs.value.data))->flag)

#define original_node(a)   (((My_attr)(a->attrs.value.data))->original_snode)


int min_baumweite;
int max_baumweite;
int groesse_graph;
int anzahl_kanten;
bool finde_minbw_durch_max_clique=0;

Slist separatorenliste;
Slist blattliste;
int fortschrittzaehler;
extern jmp_buf my_enviroment;  /* bei Speicherueberlauf longjump zu control.h*/

/********************************************************************/
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*                   !      Print Prozeduren   !                    */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/* print Snode ; print Sgraph ; print Cliquenmenge                  */
/********************************************************************/

/********************************************************************/
/*                       print node                                 */
/********************************************************************/
void printsnode(Snode node3)
{message("info:%s\n",node3->label);}
/********************************************************************/
void printinfo2(Slist llist)
{Slist node4;
for_slist(llist,node4) {
  message(" %s ",((Snode)(node4->attrs.value.data))->label);
}end_for_slist(llist,node4)
}

/********************************************************************/
/*                       print sgraph                               */
/********************************************************************/
void my_print_sgraph(Sgraph sgraph)
{
Snode snode;
Sedge sedge;
message("\nSGRAPH: %s",sgraph->label);
for_all_nodes(sgraph,snode)
   {message("\nSNODE: %s ",snode->label);
      for_sourcelist(snode,sedge)
         {message("\n     ADJKNOTEN: %s ",sedge->tnode->label);
         }end_for_sourcelist(snode,sedge)
    }end_for_all_nodes(sgraph,snode)
}

/*******************************************************************/
void mein_print_sgraph(Sgraph sgraph)
{/*Snode snode;
 Sedge sedge;

 for_all_nodes(sgraph,snode)
    {message("\n Knoten: %s adjknote:",snode->label);
     for_sourcelist(snode,sedge)
        {message("%s,",sedge->tnode->label);
         }end_for_sourcelist(snode,sedge)
    }end_for_all_nodes(sgraph,snode)  */
}



/*******************************************************************/
/*            print Cliquenmenge                                   */
/*******************************************************************/
void print_cliquenmenge(Sgraph sgraph, Slist cliquenmenge)
{
Slist clique;
Menge clique2;
message("***********Cliquen:*************%i\n",size_of_slist(cliquenmenge));
for_slist(cliquenmenge,clique)
   {clique2=(Menge)clique->attrs.value.data;
    printmenge(sgraph,clique2);
    message(" groesse: %i \n",size_of_menge(sgraph,clique2));
   }end_for_slist(cliquenmenge,clique)
   message("**************************************************\n");
}



/*****************************************************************************/
/*                       print hgraph                                        */
/*								             */
/*                gibt die hgraph Struktur zu Kontrollzwecken                */
/*                an Stdout aus.                                             */
/*								             */
/*****************************************************************************/
/*********************************************************************/
void print_daten(Sgraph sgraph)
{/*Slist element;
 printf("\nBlattliste:");
 for_slist(blattliste,element)
     {printf("\n      Knoten :");
      printmenge(sgraph,mattrs(sattrs(element)) );
      printf("  === %s",sattrs(element)->label);       }end_for_slist(blattliste,element)
 printf("\nseparatorenliste:");
 for_slist(separatorenliste,element)
     {printf("\n      Knoten :");
      printmenge(sgraph,mattrs(sattrs(element)) );
     }end_for_slist(separatorenliste,element)
*/}

/*****************************************************************************/

/*******************************************************************/
/*******************************************************************/


/*Attributes	make_attr (va_alist)
va_dcl
{
	va_list 	args;
	Attributes	attr;
	Attributes_type	attr_type;
	
	va_start (args);
	
	attr_type = va_arg (args, Attributes_type);
	
	switch (attr_type) {
	    case ATTR_FLAGS :
		attr.flags = va_arg (args, int);
		break;
	    case ATTR_DATA :
		attr.data = va_arg (args, char *);
		break;
	}
	
	va_end (args);
	return attr;
}
*/
/********************************************************************/
/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*              !      eigene mein_mallocfehler abfrage    !             */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/*******************************************************************/
char* mein_malloc(int groesse)
{char *rueck;
/* text=w_malloc(sizeof(char)*60);
 if(text==NULL)  {kein_speicher_mehr_frei();longjmp(my_enviroment,1);}*/

 rueck=w_malloc(groesse);
 if(rueck==NULL)  {kein_speicher_mehr_frei();longjmp(my_enviroment,1);}
/* text=sprintf(text,"an Adresse %i           Groesse:%i",rueck,groesse);
 mein_malloc_slist=anhaengen(mein_malloc_slist,text);*/
 return rueck;
}
/*******************************************************************/
/*******************************************************************/
void mein_free(char *zeiger)
{w_free(zeiger);
}

/********************************************************************/
/********************************************************************/
/*void show_mein_malloc(void)
{Slist element;
 printf("Speicherliste:");
 for_slist(mein_malloc_slist,element)
     {printf("%s",(char*)dattrs(element));
     }end_for_slist(mein_malloc_slist,element)
}*/
/********************************************************************/
/********************************************************************/
/***********************************************/
/* konvertiere blattliste und separatorenliste */
/***********************************************/
Sgraph konvert_listen(Sgraph sgraph)
{
 Sgraph new_sgraph;
 Slist element,element2;
 Snode snode;
 Menge menge=new_menge();

 new_sgraph=make_graph(make_dattr(sgraph));
 new_sgraph->directed=TRUE;


 for_slist(separatorenliste,element)
     {menge=empty_menge(menge);
      for_slist( (Slist)dattrs(element) ,element2)
            {menge=add_to_menge( menge , original_node(sattrs(element2)) );
	    }end_for_slist( (Slist)dattrs(element) ,element2)
    /*   printf("\nsep: ");printmenge(sgraph,menge);
    */   snode=make_node(  new_sgraph,make_dattr(copy_menge(menge))  );  
       my_set_nodelabel(sgraph,snode,menge);  
       snode->x=100;snode->y=100;
       element->attrs=make_dattr(snode);
    /*   printf("  ==: ");printmenge(sgraph,mattrs(sattrs(element)));
    */  }end_for_slist(separatorenliste,element)


 for_slist(blattliste,element)
      {menge=empty_menge(menge);
       for_slist( (Slist)dattrs(element) ,element2)
           {menge=add_to_menge( menge , original_node(sattrs(element2)) );
           }end_for_slist( (Slist)dattrs(element) ,element2)
    /*   printf("blatt: ");printmenge(sgraph,menge);
     */  snode=make_node(  new_sgraph,make_dattr(copy_menge(menge))  );  
       my_set_nodelabel(sgraph,snode,menge);  
       snode->x=100;snode->y=100;
       element->attrs=make_dattr(snode);
     }end_for_slist(blattliste,element);
   return NULL; /* ??? */
}


/**********************************************/
/*****************************************************************************/
/*                                                                           */
/*         =================================================                 */
/*         ! Baue Sgraphstrucktur aus Hilfsgraph strucktur !                 */
/*         =================================================                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Hgraph       : Bisheriger Hilfsgraphgraph                    */
/*              int tiefe : Initialisierungsmenge fuer neuen hnode           */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:      setze Zeiger fuer neues Hgraphelement um                   */
/*                                                                           */
/*****************************************************************************/

Slist identische_knoten_loeschen(Sgraph sgraph, Slist slist)
{Slist element,element2;
 Slist loeschen=empty_slist;

 fortschritt(fortschrittzaehler++);
 
 for_slist(slist,element)
     {element2=element;
      while((element2=element2->suc)!=slist)
            {if(mengengleich(mattrs(sattrs(element)),
                                             mattrs(sattrs(element2)) ) )
                  {loeschen=anhaengen(loeschen,sattrs(element));
                   remove_node(sattrs(element));
                   break;
		  }
	    }
     }end_for_slist(slist,element)

 slist=subtract_slists(slist,loeschen);
 free_slist(loeschen);
 return slist;
}

/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/

Slist nicht_dominante_snodes_entfernen(Sgraph sgraph, Slist slist)
{Slist element,element2;
 Slist loeschen=empty_slist;

 for_slist(slist,element)
     {element2=element;
      while((element2=element2->suc)!=slist)
            {if(menge_teilmenge_von_menge(mattrs(sattrs(element)),
                                             mattrs(sattrs(element2)) ) )
                  {loeschen=anhaengen(loeschen,sattrs(element));
                   break;
		  }
	     if(menge_teilmenge_von_menge(mattrs(sattrs(element2)),
                                             mattrs(sattrs(element)) )  )
                  {loeschen=anhaengen(loeschen,sattrs(element2));
                   break;
		  }
	    }
     }end_for_slist(slist,element)

 /*** nun noch alle gestzten Kanten zwischen blatt und rezesiven seps loeschen*/

 slist=subtract_slists(slist,loeschen);
 free_slist(loeschen);
 return slist;
}

/*****************************************************************************/
/*****************************************************************************/
/*****************************************************************************/
/**** pruefe ob fuer einen Separator ein dominanter Separator in der 
       ueberigen Separatorenliste existiert . Falls ja entferne ersezte den
       rezesiven durch den dominanten Separator. Nur durch einen !!) **********/

Slist ersetze_rezesive_durch_dominante(Sgraph sgraph, Slist aktuelle_sep)
{Slist element,element2;
 Slist neu_elemente=empty_slist;
 Slist loeschen =empty_slist;

 for_slist(aktuelle_sep,element)
      {for_slist(separatorenliste,element2)
	    {if (menge_echte_teilmenge_von_menge(sgraph,mattrs(sattrs(element)),
                                                 mattrs(sattrs(element2)) ) )
                   {loeschen=anhaengen(loeschen,sattrs(element));
		    neu_elemente=anhaengen(neu_elemente,sattrs(element2));
		   break;
		   }
	    }end_for_slist(separatorenliste,element2)
      }end_for_slist(aktuelle_sep,element)
 aktuelle_sep=subtract_slists(aktuelle_sep,loeschen);
 aktuelle_sep=add_slists(aktuelle_sep,neu_elemente);
 return aktuelle_sep;
}

/*****************************************************************************/
/*                                                                           */
/*  =======================================================================  */
/*  ! berechne Kanten zwischen den berechneten Knoten einer Baumzerlegung !  */
/*  =======================================================================  */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph sgraph     : Zu berechnender graph                    */
/*              Sgraph new_sgraph : bisherige Baumzerlegung	             */
/*		Slist blattliste  : Liste aller noch zu berarbeitender 	     */
/*				    Blaetter.(muss auf aktuelles Baltt zeigen*/ 
/*                                                                           */
/*  Rueckgabeparameter :  void					             */
/*                                                                           */
/*  Aufgabe:      bisher wurden nur die Blaetter und die Separatoren be-     */
/*	          rechnet. Hier muessen Kanten gefunden werden, die die	     */
/*   		  Knoten zu einer gueltigen Baumzerlegung verbinden.	     */
/*  Vorgehensweise :					                     */
/*       Es wird mit einem belibigen Blatt gestartet. Dieses Blatt wird mit  */
/*	 allen SeparatorenKnoten verbunden die ein dominante Teilmenge des   */
/*       Blattes bilden. 						     */
/*       Nun wird von jedem neu verbundenen Separator nach weitern Blaettern */
/*       gesucht, die wiederum eine Obermenge des Separators bilden.         */
/*	 Jedes gefundene Blatt wird mit dem Separator verbunden und die      */
/*       Prozedur wird erneut mit dem neuem Blatt als Parameter		     */
/*	 aufgerufen.							     */
/*                                                                           */
/*****************************************************************************/


void baue_baum_aus_listen(Sgraph sgraph, Snode blatt_snode)
{Slist element;
 Slist blatt,aktuelle_blaetter_je_sep;
 Slist aktuelle_sep=empty_slist;
 Slist aktuelle_blaetter=empty_slist;
 Menge blatt_menge;

 /*print_daten(sgraph); */

 blatt_menge=copy_menge(mattrs(blatt_snode));
 /*erstes Element entf*/
 
 /*printf("\n****aktuell behandeltes Blatt:%s",blatt_snode->label);*/
 
 fortschritt(fortschrittzaehler++);
 /**** alle Separatoren suchen die Teilmenge des Blattes sind  *****/
 for_slist(separatorenliste,element)
      {if (menge_teilmenge_von_menge( mattrs(sattrs(element)),blatt_menge ) )
            {aktuelle_sep=anhaengen(aktuelle_sep,sattrs(element));
             /*printf("\n    ***************Kante gesetzt************");
             printf("\n    bekommt Verbindung zu Sep: %s"
                                                 ,sattrs(element)->label);*/
            }
      }end_for_slist(separatorenliste,element)

 /**** Menge als Slist sichern ********/
 blatt_snode->attrs=make_dattr(save_menge(sgraph,mattrs(blatt_snode)));

 /**** nicht dominante Separatoren entfernen **********************/
 aktuelle_sep=nicht_dominante_snodes_entfernen(sgraph,aktuelle_sep);

  
/**** pruefe ob fuer einen Separator ein dominanter Separator in der 
       ueberigen Separatorenliste existiert. Falls ja entferne ersezte den
       rezesiven durch den dominanten Separator. **********/
 aktuelle_sep =  ersetze_rezesive_durch_dominante(sgraph,aktuelle_sep);


 /**** alle zugehoerigen Separatoren anhaengen ***********/
 for_slist(aktuelle_sep,element)
      {make_edge(blatt_snode,sattrs(element),empty_attr);
       /*printf("\n    ==== aktueller Sep:%s",sattrs(element)->label);*/
       aktuelle_blaetter_je_sep=empty_slist;
       for_slist(blattliste,blatt)
            {if (menge_teilmenge_von_menge( mattrs(sattrs(element)),
                                      mattrs(sattrs(blatt)) ) )
                {make_edge(sattrs(element),sattrs(blatt),empty_attr);
                 aktuelle_blaetter=anhaengen(aktuelle_blaetter,dattrs(blatt));
                 aktuelle_blaetter_je_sep=anhaengen(aktuelle_blaetter_je_sep,dattrs(blatt));
                /*printf("\n           ***************Kante gesetzt************");
                 printf("\n           bekommt Verbindung zu Blatt: %s"
                                                 ,sattrs(blatt)->label);
                 printf("\n           ======= (sollt = sein)");
                 printmenge(sgraph,mattrs(sattrs(blatt)));*/
                }
            }end_for_slist(blattliste,blatt)
       blattliste=subtract_slists(blattliste,aktuelle_blaetter_je_sep);
       free_slist(aktuelle_blaetter_je_sep);
       sattrs(element)->attrs=make_dattr(save_menge
                                                (sgraph,mattrs(sattrs(element))));
      }end_for_slist(aktuelle_sep,element)

 separatorenliste=subtract_slists(separatorenliste,aktuelle_sep);
 free_slist(aktuelle_sep);

 /**** fuer jedes Blatt prozedur erneut aufrufen ******/
 for_slist(aktuelle_blaetter,blatt)
      {baue_baum_aus_listen(sgraph,(Snode)blatt->attrs.value.data);
      }end_for_slist(aktuelle_blaetter,blatt)

 free_slist (aktuelle_blaetter);
}

/********************************************************************/


void   baue_baumzerlegung_aus_listen(Sgraph sgraph)
{Snode snode,erstes_blatt;
      init_fortschritt("entferne identische Separatoren",groesse_graph);
      fortschrittzaehler=0;
      groesse_graph=0;
      for_all_nodes(sgraph,snode)
          {groesse_graph++;
          }end_for_all_nodes(sgraph,snode)
      separatorenliste=identische_knoten_loeschen(sgraph,separatorenliste); 
      /* mein_print_sgraph(new_sgraph);*/
     /* print_daten(sgraph);*/
      init_fortschritt("baue Baumzerlegung",groesse_graph);
      fortschrittzaehler=0;
      erstes_blatt=sattrs(blattliste);
      blattliste=subtract_immediately_from_slist(blattliste,blattliste);
      baue_baum_aus_listen(sgraph,erstes_blatt);
}

/********************************************************************/
/*                                                                  */
/*         =================================================        */
/*                 !      Initialisiere Sgraph   !                  */
/*         =================================================        */
/*                                                                  */
/********************************************************************/
/********************************************************************/
/********************************************************************/
/*		Berechen Anzahl der Knoten im Graphen		    */
/********************************************************************/
int anzahl_nodes(Sgraph sgraph)
{
int i=0;
Snode node;
for_all_nodes(sgraph,node)
      {i+=1;
       node->nr=i;
  }end_for_all_nodes(sgraph,node)
return (i);
}
/********************************************************************/
/*			Initialisere Sgraph 			    */
/********************************************************************/
/* initialisiere Sgraph : Die Attributes-Felder der Snodes 	    */
/*	werden auf '0' gesetzt. 'anzahl_nodes' und 		    */
/*	'anzahl_edges' werden berechnet.		       	    */
/*	Die 'nr'-Felder der Snodes werden von 1 beginnend fort-     */
/*	laufend nummeriert.					    */
/********************************************************************/
void init_sgraph(Sgraph sgraph)
{
int i=0;
Snode node;
Sedge sedge;
anzahl_kanten=0;
for_all_nodes(sgraph,node)
      {i+=1;
       node->attrs=make_attr(ATTR_FLAGS,0);
       node->nr=i;
      for_sourcelist(node,sedge)
           {anzahl_kanten++;
           }end_for_sourcelist(node,sedge)
  }end_for_all_nodes(sgraph,node)
groesse_graph=i;
anzahl_kanten=anzahl_kanten/2;
/*standart_node_type=(int)node_get(node,NODE_TYPE,0);*/
return;}
/*****************************************************************************/
/*              initialisire fuer manipuliere Baumzerlegung                  */
/*****************************************************************************/
/*								             */
/* aufrufende Prozedur    : min_tree_dec 	                             */
/*                          change_labels				     */
/*								             */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                              */
/*                                                                           */
/* Rueckgabeparameter :   void			                             */  /*                                                                           */
/*****************************************************************************/

void init_sgraph_fuer_manipulate_treedec(Sgraph sgraph)
{int i=0;
 Snode node;

 for_all_nodes(sgraph,node)
      {i+=1;
       node->nr=i;
      }end_for_all_nodes(sgraph,node)
 groesse_graph=i;
}
/*******************************************************************/
/*                  slist = slist ?                                */
/*******************************************************************/
bool slists_identical (Slist menge1, Slist menge2)
{
Slist node5,node6;
bool gleich=true;
bool gleich2;
for_slist(menge1,node5)
    {gleich2=false;
     for_slist(menge2,node6)
         {if(node5->attrs.value.data==node6->attrs.value.data)
           { gleich2=true;break;}
         }end_for_slist(menge2,node6)
      if(gleich2==false) {gleich=false;break;}
     }end_for_slist(menge1,node5)
if (size_of_slist(menge1)!=size_of_slist(menge2))
     {gleich=false;}
return gleich;
}
