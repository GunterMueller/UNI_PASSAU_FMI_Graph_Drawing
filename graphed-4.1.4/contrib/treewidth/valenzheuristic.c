/*****************************************************************************/
/*                                                                           */
/*                       V A L E N Z H E U R I S T I K			     */
/*                                                                           */
/* Modul	: valenzheuristic.c					     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/


#include "valenzheuristic.h"
#include "cliquen.h"

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	Sgraph	 valenzheuristic 			(Sgraph)	     */
/*		 Kontrollprozedur fuer die Valenzheuristik		     */
/*								             */
/*****************************************************************************/
/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	Mein_attr new_mein_attr			()			     */
/*		  Allociert Speicherplatz und initialisiert `Mein_attr'      */
/*								             */
/*	Sgraph	  valenzheuristic_copy_sgraph	(Sgraph)		     */
/*		  kopiert den vollstaendigen Sgraph			     */
/*								             */
/*	void    baue_baumzerlegung (Sgarph original_sgraph,sgraph,new_sgraph)*/
/*		baut rekursiv aus dem `original_sgraph' eine Baumzerlegung   */
/*		new_sgraph ist der Ausgabegraph fuer die Baumzerlegung	     */
/*		sgraph ist der Arbeitsgraph der Schritt fuer Schritt re-     */
/*		duziert wird. Muss im ersten Aufruf mit `original_sgraph'    */
/*		identisch sein.						     */
/*								             */
/*****************************************************************************/

typedef struct mein_attr {
 /*         bool    mark;*/   /* durch Adjazanzmatriz ueberfluessig geworden */
            int     attr_valenz;
        }         *Mein_attr;

static bool reduziere;


/*****************************************************************************/
/*						                             */
/*		LOKALE Makros					             */
/*		fuer die Mein_attr-strucktur				     */
/*								             */
/*****************************************************************************/
#define valenz(a)               (((Mein_attr)(a->attrs.value.data))->attr_valenz)
#define erhoehe_valenz(a)       (((Mein_attr)(a->attrs.value.data))->attr_valenz)++
#define reduziere_valenz(a)     (((Mein_attr)(a->attrs.value.data))->attr_valenz)--
/*#define markiert(a)             (((Mein_attr)(a->attrs.value.data))->mark)
#define markiere(a)             (((Mein_attr)(a->attrs.value.data))->mark)=1
#define loesche_markierung(a)   (((Mein_attr)(a->attrs.value.data))->mark)=0*/

/*****************************************************************************/
/*								             */
/*    zwei Felder fuer die beschleunigung des Algorithmusse		     */
/*    benoetigt n*n Speicherplatz, ermoeglicht aber dennoch die Berechnung   */
/*    von groesseren Graphen als dies zeitlich mit nur einer Adjazenz-       */
/*    liste moeglich waere.						     */
/*								             */
/*****************************************************************************/

/***   Feld fuer Adjazenzmatriz  *****/
Sedge *sedgefeld;
/***   Feld fuer Separatorenmenge  ***/
Snode *blattnodefeld;

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
/*****************************************************************************/
/*****************************************************************************/

/*void valenzheuristic_unmark_sgraph(Sgraph sgraph)
{Snode snode;
 for_all_nodes(sgraph,snode)
      {loesche_markierung(snode);
      }end_for_all_nodes(sgraph,snode)
}*/

/*****************************************************************************/
Mein_attr new_mein_attr(void)
{Mein_attr new=(Mein_attr)mein_malloc(sizeof(Mein_attr));
 
 new->attr_valenz=0;
 /*new->mark=0;*/
 return new;
}
/*****************************************************************************/
/*                       print hgraph                                        */
/*								             */
/*                gibt die hgraph Struktur zu Kontrollzwecken                */
/*                an Stdout aus.                                             */
/*								             */
/*****************************************************************************/

/*****************************************************************************/
/*                                                                           */
/*      =================================================                    */
/*      !Prozeduren zur Verwaltung der Hilfsgraphstuktur!                    */
/*      =================================================                    */
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


Sgraph valenzheuristic_copy_sgraph(Sgraph sgraph)
{Sedge sedge,new_edge;
 Snode new_snode; 
 Snode snode;
 Sgraph new_sgraph=make_graph(empty_attr);

 /******  kopiere alle it 1 markierte Knoten (Zhk+Separator)   ****/
 for_all_nodes(sgraph,snode)
         {new_snode=make_node(new_sgraph,empty_attr);
          new_snode->x=100;new_snode->y=100;
          set_nodelabel(new_snode,snode->label);
          snode->iso=new_snode;
          new_snode->iso=snode;
          new_snode->nr=snode->nr;
          new_snode->attrs=make_dattr(new_mein_attr());
          }end_for_all_nodes(sgraph,snode)

 /******  kopiere Kanten in neuen Teilgraphen                *****/
 for_all_nodes(sgraph,snode)
     {snode->attrs=make_flattr(1);
      for_sourcelist(snode,sedge)
          {if (sedge->tnode->attrs.value.integer!=1)
             {new_edge=make_edge(snode->iso,sedge->tnode->iso,empty_attr);
              sedgefeld[snode->nr*groesse_graph+sedge->tnode->nr]=new_edge;
              sedgefeld[sedge->tnode->nr*groesse_graph+snode->nr]=new_edge;
              /*printf("Kante gesetz zwischen %i und %i",sedge->tnode->iso->nr
               ,snode->iso->nr);   */         
              erhoehe_valenz(snode->iso);
              erhoehe_valenz(sedge->tnode->iso);
             }
          }end_for_sourcelist(snode,sedge)
     }end_for_all_nodes(sgraph,snode)
 return new_sgraph;
}

/*****************************************************************************/
/*                                                                           */
/*         =================================================                 */
/*         ! Baue Baumzerlegung (eigentlicher Algorithmus) !                 */
/*         =================================================                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph sgraph : zu berechnender Graph			     */
/*  		Sgraph sgraph : aktueller Arbeitsgraph (teilmenge des 	     */
/*					original_graph)			     */
/*		Sgraph new_sgraph : ausgabegraph (reduzierter Graph)	     */
/*                                                                           */
/*  Rueckgabeparameter :   void					             */
/*                                                                           */
/*  Aufgabe:      berechne Baumzerlegung durch iteratives Abspalten  des     */
/*		  Knotens mit der niedrigsten Valenz und Ergaenzung des      */
/*		  Arbeitsgraphen um allle moeglichen Kanten zwischen den     */
/*		  Knoten der  Separatoren		                     */
/*                                                                           */
/*                                                                           */
/*                Der Algorithmus kann auch zum reduzieren von Knoten mit    */
/*                Valenz <3 benutzt werden. Hierzu muss die globale Variable */
/*                `reduziere' auf true gesetzt werden.  		     */
/*                                                                           */
/*****************************************************************************/

Sgraph baue_baumzerlegung(Sgraph original_sgraph, Sgraph sgraph, Sgraph new_sgraph)
{Snode snode,snode2,new_snode,new_snode2;
 Sedge sedge,new_edge;
 int min_valenz=10000;
 Snode best_node;
 Menge menge=new_menge();
 Menge blatt,sep;
 Menge blatt2;
 int groesse_blattnodefeld=0;
 int i,j;

 /**** finde denjenigen Knoten (best_node) mit der kleinsten Valenz ( O[n])***/
 fortschritt(fortschrittzaehler++);
/* printf("fortschrittzaehler: %i\n",fortschrittzaehler);*/
 for_all_nodes(sgraph,snode)
     {if(valenz(snode)<min_valenz)
          {best_node=snode;
           min_valenz=valenz(snode);}
      }end_for_all_nodes(sgraph,snode)

if  ( (min_valenz>2) && (reduziere) )
       {return sgraph;}
 max_baumweite=maximum(max_baumweite,min_valenz);
/* printf("\n Die minimale Valenz betraegt z.Z:%i\n Groesse Graph:%i",min_valenz,anzahl_nodes(sgraph));*/
 
 if(min_valenz!=(anzahl_nodes(sgraph)-1)) 
 /*in jedem Schritt wird ein Knoten entfernt */
     { /*** noch nicht nur Blatt ****/
       
 
      /**** bilde den Separator    (O[n]) ****/
      for_sourcelist(best_node,sedge)
           {menge=add_to_menge(menge,sedge->tnode);
            blattnodefeld[groesse_blattnodefeld]=sedge->tnode;
	    groesse_blattnodefeld++;
            reduziere_valenz(sedge->tnode);
	    sedgefeld[sedge->tnode->iso->nr*groesse_graph+best_node->iso->nr]=(Sedge)NULL;
	    sedgefeld[best_node->iso->nr*groesse_graph+sedge->tnode->iso->nr]=(Sedge)NULL;
           }end_for_sourcelist(best_node,sedge)
 
      /*printf("\naktueller Sep: ");printmenge(sgraph,menge);*/

       /*** Sep im bezug ***/
       sep=new_menge();
       for_menge(menge,snode)
           {sep=add_to_menge(sep,snode->iso);
           }end_for_menge(menge,snode)

       blatt=copy_menge(menge);
       blatt=add_to_menge(blatt,best_node);
       blatt2=new_menge();
       for_menge(blatt,snode)
           {blatt2=add_to_menge(blatt2,snode->iso);
           }end_for_menge(blatt,snode)

       /***** einen neuen Knoten fuer den Separator generieren ******/ 

       /*printf("\nnSepkoten");printmenge(original_sgraph,sep);*/
       new_snode=make_node(  new_sgraph,make_dattr(sep)  );    /*noch mengen */
       my_set_nodelabel(sgraph,new_snode,menge);  
       new_snode->x=100;new_snode->y=100;
       separatorenliste=anhaengen(separatorenliste,new_snode);
     
       /**** Blatt:   menge + snode ****/

       /*printf("\nneues Blatt");printmenge(sgraph,blatt);*/

       /**** bilde aus diesem Blatt einenen Snode im neuen Graphen ****/

       new_snode2=make_node(new_sgraph,make_dattr(blatt2));
       new_snode2->x=100;new_snode2->y=100;
       my_set_nodelabel(sgraph,new_snode2,blatt);  
       blattliste=anhaengen(blattliste,new_snode2);
       mein_free(blatt);

      /*** entferne Blatt ohen Sep aus altem Sgraph zur weiterverarbeitung ****/

      /*printf("\n remove snode :%s",best_node->label);*/
      remove_node(best_node);


 /**** fuege noch alle moeglichen Kanten zwischen Separatorenknoten hinzu   ***/
 /****  hier jetzt die Kanten hinzufuegen (jede kante nur einmal)    *********/

 /*       k*k schleifendurchlaeufe */ 
         for(i=0;i<groesse_blattnodefeld;i++)        
          {snode=blattnodefeld[i];
	   for(j=i+1;j<groesse_blattnodefeld;j++)
                  {snode2=blattnodefeld[j];
		   if(sedgefeld[snode->iso->nr*groesse_graph+snode2->iso->nr]
       					==(Sedge)NULL )
		     {
		      new_edge=make_edge(snode,snode2,empty_attr);
  		      sedgefeld[snode->iso->nr*groesse_graph+snode2->iso->nr]
           							=new_edge;
		      sedgefeld[snode2->iso->nr*groesse_graph+snode->iso->nr]
								=new_edge;
		      erhoehe_valenz(snode);
		      erhoehe_valenz(snode2);
                      anzahl_kanten++;
		     }	
                  }
          }

     mein_free(menge);
     /*mein_print_sgraph(sgraph);*/

     /**** naechsten Knotwen entfernen *********/

     return baue_baumzerlegung(original_sgraph,sgraph,new_sgraph);
     }

 else{
       /*letztes Blatt*/
      blatt=new_menge();
      blatt2=new_menge();
      for_all_nodes(sgraph,snode)
          {blatt=add_to_menge(blatt,snode);
           blatt2=add_to_menge(blatt2,snode->iso);
          }end_for_all_nodes(sgraph,snode);

       new_snode=make_node(new_sgraph,make_dattr(blatt2));
       new_snode->x=100;new_snode->y=100;
       my_set_nodelabel(sgraph,new_snode,blatt);  
       blattliste=anhaengen(blattliste,new_snode);
      mein_free(blatt);
      mein_free(menge);
      }

if(reduziere)  {min_baumweite=maximum(min_baumweite,min_valenz);}
return (Sgraph)(NULL);
 /***** einen neuen Knoten fuer den Separator generieren ******/ 
}

/*********************************************************************/
/*********************************************************************/
/*********************************************************************/

/*bool contains_slist_knoten(Snode slist,Slist snode)
{Slist element;

 for_slist(slist,element)
     {if (sattrs(element)==snode)
         {return true;}
     }end_for_slist(slist,element)
 return false;
}*/

/*********************************************************************/
/*********************************************************************/

/*void loesche_kanten_zu_slist(Snode snode1,Slist loeschen)
{Sedge sedge,sedge2;

 for_sourcelist(snode1,sedge)
     {if(contains_slist_knoten(loeschen,sedge->tnode))
          {remove(sedge);
	*//*   printf("\n**********kante geloescht.zwischen %s und %s********",snode1->label,sedge->tnode->label);*//*
          }
     }end_for_sourcelist(snode1,sedge)
}  */


/*****************************************************************************/
/*****************************************************************************/

Sgraph reduziere_sgraph(Sgraph sgraph)
{  Sgraph new_sgraph;
 Sgraph sgraph2;
 int i,j,max_sgraph=0;

 new_sgraph=make_graph(make_dattr(sgraph));
  new_sgraph->directed=TRUE;
 reduziere =true;
 init_fortschritt("kopiere_sgraph",groesse_graph);
 fortschrittzaehler=0;

/***** reserviere nur einmal entsprechend dem groessten Graphen Speicher ***/

  init_sgraph(sgraph);
  max_sgraph=groesse_graph;


 sedgefeld= (Sedge*)mein_malloc(sizeof(Sedge*)*(max_sgraph+1)*(max_sgraph+1));
/* if (sedgefeld==(Sedge*)NULL)
        {printf("************nicht genug memory**********");
         exit(0);}
*/

 blattnodefeld=(Snode*)mein_malloc(sizeof(Snode*)*(max_sgraph+1));
/* if (blattnodefeld==(Snode*)NULL)
        {printf("************nicht genug memory**********");
         exit(0);}
*/

/*********  eigentliche Bearbeitung ********/
      for(i=0;i<=groesse_graph;i++)
            {for(j=0;j<=groesse_graph;j++)
                 {sedgefeld[i*groesse_graph+j]=(Sedge)NULL;}
            }
      sgraph2=valenzheuristic_copy_sgraph(sgraph);
    /*  printf("\n Sgraph kopiert.");*/
      init_fortschritt("zerlege_Graph",groesse_graph);
      fortschrittzaehler=0;
      sgraph2=baue_baumzerlegung(sgraph,sgraph2,new_sgraph) ;



 mein_free(blattnodefeld);
 mein_free(sedgefeld);
 return sgraph2;
 } 

/*****************************************************************************/
/*****************************************************************************/
/*                                                                           */
/*         =================================================                 */
/*         !          Valenzheurisik-steuer-prozedur         !               */
/*         =================================================                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph sgraph :aktueller sgraph		                     */
/*                                                                           */
/*  Rueckgabeparameter :   Sgraph   : berechnete Baumzerlegung               */
/*			    die berechnete maximale Baumweite wird ueber die */
/*			    globale Variable 'max_baumweite' zurueckgegeben  */
/*                                                                           */
/*****************************************************************************/

Sgraph valenzheuristic (Slist teilgraphen, Sgraph sgraph)
{
 Sgraph new_sgraph;
 Slist  steilgraph;
 Sgraph sgraph2;
 Snode snode;
 int i,j,max_sgraph=0;

 new_sgraph=make_graph(make_dattr(sgraph));
 new_sgraph->directed=TRUE;


 init_fortschritt("kopiere_sgraph",groesse_graph);
 fortschrittzaehler=0;

 reduziere =false;
/***** reserviere nur einmal entsprechend dem groessten Graphen Speicher ***/

 for_slist(teilgraphen,steilgraph)
   {i=0;
    for_all_nodes((Sgraph)dattrs(steilgraph),snode)
       {i++;} end_for_all_nodes((Sgraph)dattrs(steilgraph),snode)

    max_sgraph=maximum(max_sgraph,i);
    }end_for_slist(teilgraphen,steilgraph)


 /*printf("\n***algo benoetigt %i Bytes Speicherplatz",sizeof(Sedge*)*(max_sgraph+1)*(max_sgraph+1));*/

 sedgefeld= (Sedge*)mein_malloc(sizeof(Sedge*)*(max_sgraph+1)*(max_sgraph+1));
 /*if (sedgefeld==(Sedge*)NULL)
        {printf("************nicht genug memory**********");
         exit(0);}*/


 blattnodefeld=(Snode*)mein_malloc(sizeof(Snode*)*(max_sgraph+1));
/* if (blattnodefeld==(Snode*)NULL)
        {printf("************nicht genug memory**********");
         exit(0);}
*/

/*********  eigentliche Bearbeitung ********/
 for_slist(teilgraphen,steilgraph)
     {init_sgraph((Sgraph)dattrs(steilgraph));
      for(i=0;i<=groesse_graph;i++)
            {for(j=0;j<=groesse_graph;j++)
                 {sedgefeld[i*groesse_graph+j]=(Sedge)NULL;}
            }
      sgraph2=valenzheuristic_copy_sgraph((Sgraph)dattrs(steilgraph));
/*      printf("\n Sgraph kopiert."); */
      init_fortschritt("zerlege_Graph",groesse_graph);
      fortschrittzaehler=0;
      baue_baumzerlegung((Sgraph)dattrs(steilgraph)
              ,sgraph2,new_sgraph) ;
    }end_for_slist(teilgraphen,steilgraph)


/* printf("\nDie Baumweite betraegt hoechstens :%i",max_baumweite);
 printf("\n*************************************************");
 printf("\n*************************************************");
 printf("\n*************************************************");
*/

 if  (algo_output_type != BAUMBREITE)
     {baue_baumzerlegung_aus_listen(sgraph);
     }

 mein_free(blattnodefeld);
 mein_free(sedgefeld);


 end_fortschritt(); 
 return new_sgraph;     
}
