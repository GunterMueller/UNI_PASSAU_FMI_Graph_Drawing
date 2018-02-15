/*****************************************************************************/
/*                                                                           */
/*                S E P A R A T O R H E U R I S T I K 			     */
/*                                                                           */
/* Modul	: separatorheuristik.c					     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/


#include "separatorheuristik.h"
#include <maxclique/maxclique_export.h>

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	Sgraph	 separatorheuristic 		(Sgraph)		     */
/*		 Kontrollprozedur fuer die Separatorheuristik		     */
/*								             */
/*****************************************************************************/
/*								             */
/*	Slist 	graph_durch_cliquen_aufspalten  (Sgraph,Menge cliquenmenge)  */
/*		spaltet eiene Sgraphen wenn moeglich durch die Cliquen als   */
/*		Separatoren in Teilgraphen auf. Slist enthaelt eine Liste    */
/*		der gefundenen Teilgraphen.				     */
/*		Kann der Graph nicht aufgespaltet werden wird empty_slist    */
/*		zurueckgegeben.						     */
/*								             */
/*****************************************************************************/
/*								             */
/*      Slist  pruefe_auf_separator (sgraph,Menge sep)			     */
/*		pruefe ob die Menge sep einen Separator darstellt.	     */
/*		return `empty_slist' wenn nicht.			     */
/*	        sonst gebe Slist der Zusammenhangskomponenten zurueck        */
/*		 (Sgraphen nicht Knotenmengen)				     */
/*								             */
/*****************************************************************************/
/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	My_attr new_my_attr		(Snode)				     */
/*		initialisert `My_attr' und allokiert Speicherplatz	     */
/*								             */
/*	void	mark_menge_separatorheuristic  	 (Sgraph,Menge)	             */
/*		markiert eine Knotenmenge im Sgraphen			     */
/*								             */
/*	voi	unmark_sgraph_separatorheuristic (Sgraph)		     */
/*		loescht alle Markierungen im Sgraphen			     */
/*								             */
/*	int 	kantenzahl		(Sgraph)			     */
/*		berechnet die Kantenzahl				     */
/*								             */
/*	void 	dfs_sgraph_separatorheuristic	(Snode)		             */
/*		rekursive Tiefensuche					     */
/*								             */
/*	void 	free_sgraph_with_menge	(Sgraph)			     */
/*		gibt den angfordrten speicher frei			     */
/*								             */
/*	void	speichere_teilgraph	(Sgraph,Menge sep,int momentane_bw)  */
/*								             */
/*	bool	baue_separator_strucktur(Sgraph,Menge sep,int momentane_bw)  */
/*		prueft ob k-Menge einen Separator darstellt.		     */
/*								             */
/*	bool	bilde_separator (Sgraph,int anf,int tiefe,Menge sep	     */
/*					 ,int groesse,int momentane_bw)	     */
/*		Findet alle k-Knotenteilmengen des Sgraphens		     */
/*								             */
/*	void	finde_sep		(Sgraph,int,int,int)		     */
/*		findet einen Separator fuer einen Sgraph oder erkennt eine   */
/*		eine Clique.						     */
/*									     */
/*****************************************************************************/
/*								             */
/*	void	dfs_sgraph_graph_durch_cliquen_aufspalten  (Snode)    	     */
/*									     */
/*	Sgraph 	speichere_teilgraph1		(Sgraph,Menge sep)	     */
/*								             */
/*****************************************************************************/

extern Slist berechne_sep(Sgraph sgraph); /* searchers/alg/separator.c */


void finde_sep(Sgraph sgraph, int groesse, int momentane_bw, int anzahl_kanten);   /*Vorwaertsdeklaration*/

/***** lokale baumweite *****/
static int teilgraph_bw;

static char *text;


/*****************************************************************************/
/*		lokale Attributstrucktur				     */
/*****************************************************************************/

typedef struct my_attr    {
          int             marke;
          Snode           original_snode;
        }         *My_attr;

#define attr_flag(a)            (((My_attr)(a->attrs.value.data))->marke)

#define original_node(a)   (((My_attr)(a->attrs.value.data))->original_snode)



/*****************************************************************************/
/*               initialisere My_attr-Strucktur                              */
/*****************************************************************************/
/*                                                                           */
/* Parameter    : Snode snode : wird an original_snode uebergeben	     */
/*                                                                           */
/*  Rueckgabeparameter :   My_attr    neue  My_attr-Strucktur                */
/*                                                                           */
/*****************************************************************************/

My_attr new_my_attr(Snode snode)
{
 My_attr new=(My_attr)mein_malloc(sizeof(My_attr));
 
 new->original_snode=snode;
 new->marke=0;
 return new;
}

/*****************************************************************************/
/*		markiert eine Knotenmenge im Sgraphen			     */
/*****************************************************************************/
/*                                                                           */
/* Parameter    : Sgraph sgraph    : aktueller Teilraph                      */
/*		  Menge menge      : im sgraph zu markierende Knotenmenge    */
/*                                                                           */
/*  Rueckgabeparameter :   void					             */
/*                                                                           */
/*****************************************************************************/

void mark_menge_separatorheuristic(Sgraph sgraph, Menge menge)
{Snode snode;
 for_menge(menge,snode)
    {attr_flag(snode)=1;
    }end_for_menge(menge,snode)
}


/*****************************************************************************/
/*		loescht alle Markierungen im Sgraphen			     */
/*****************************************************************************/
/*                                                                           */
/* Parameter    : Sgraph sgraph    : aktueller Teilraph                      */
/*                                                                           */
/*  Rueckgabeparameter :   void					             */
/*                                                                           */
/*****************************************************************************/

void unmark_sgraph_separatorheuristic(Sgraph sgraph)
{Snode snode;
 for_all_nodes(sgraph,snode)
    {attr_flag(snode)=0;
    }end_for_all_nodes(sgraph,snode)
}


/*****************************************************************************/
/*		berechnene die Kantenzahl des Sgraphen			     */
/*****************************************************************************/
/*                                                                           */
/*  Parameter    : Sgraph sgraph    : aktueller Teilraph                     */
/*                                                                           */
/*  Rueckgabeparameter :   int     : Kantenanzahl		             */
/*                                                                           */
/*****************************************************************************/

int kantenzahl(Sgraph sgraph)
{Snode snode;
 Sedge sedge;
 int i=0;

 for_all_nodes(sgraph,snode)
    {for_sourcelist(snode,sedge)
       {i++;
       }end_for_sourcelist(snode,sedge)
    }end_for_all_nodes(sgraph,snode)
return i/2;}


/*****************************************************************************/
/*		rekursive Tiefensuche					     */
/*****************************************************************************/
/*                                                                           */
/*  Parameter    :  Snode snode : aktuller Startknoten 			     */
/*		                                                             */
/*  Rueckgabeparameter :   void					             */
/*                                                                           */
/*****************************************************************************/

void dfs_sgraph_separatorheuristic(Snode snode)
{Sedge sedge;
 attr_flag(snode)=1;
 for_sourcelist(snode,sedge)
    {if(attr_flag(sedge->tnode)!=1)
         { dfs_sgraph_separatorheuristic(sedge->tnode); }
     }end_for_sourcelist(snode,sedge)
}



/*****************************************************************************/
/*		rekursive Tiefensuche					     */
/*****************************************************************************/
/*                                                                           */
/*  Parameter    :  Snode snode : aktuller Startknoten 			     */
/*		                                                             */
/*  Rueckgabeparameter :   void					             */
/*                                                                           */
/*****************************************************************************/

void dfs_sgraph_graph_durch_cliquen_aufspalten(Snode snode)
{Sedge sedge;
 snode->attrs.value.integer=1;
 for_sourcelist(snode,sedge)
    {if(sedge->tnode->attrs.value.integer!=1)
         { dfs_sgraph_graph_durch_cliquen_aufspalten(sedge->tnode); }
     }end_for_sourcelist(snode,sedge)
}


/*****************************************************************************/
/*		gebe den angfordrten speicher frei			     */
/*****************************************************************************/
/*                                                                           */
/*  Parameter    : Sgraph sgraph    : freizugebeneder Sgraph                 */
/*                                                                           */
/*  Rueckgabeparameter :   void					             */
/*                                                                           */
/*****************************************************************************/
/*
static void free_sgraph_with_menge(Sgraph sgraph)
{
 Slist element,knoten=empty_slist;
 Snode snode;
 
 for_all_nodes(sgraph,snode);
     {knoten=anhaengen(knoten,snode);
     }end_for_all_nodes(sgraph,snode)
 for_slist(knoten,element)
    {remove_node(sattrs(element));
    }end_for_slist(knoten,element)
 free_slist(knoten);
}	
*/


/*****************************************************************************/
/*            speichere Zusammenhangskomponente als neuen Sgraph             */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph       :  aktueller Teilgraph			     */
/*		Menge sep    :  Separatorknotenmenge			     */
/*		int momentane_bw : max. Baumweite des Teilgraphen	     */
/*                                                                           */
/*  Rueckgabeparameter :    void				             */
/*                                                                           */
/*  Aufgabe :   speichert die gefundene mit `1' markierte Zusammenhangs-     */
/*		knotenmenge als neuen Sgarphen. Dazu werden alle Kanten      */
/*		kopiert und um alle moeglichen Kanten inerhalb des Separators*/
/*		ergaenzt.				                     */
/*		Anschliessend wird von hier aus eine weitere Zerlegung des   */
/*		Graphen aufgerufen (`finde sep')			     */
/*                                                                           */
/*****************************************************************************/

static void speichere_teilgraph(Sgraph sgraph, Menge sep, int momentane_bw)
{Sedge sedge;
 Snode new_snode; 
 Snode snode,snode2;
 int anzahl_kanten=0;
 int groesse=0;
 Sgraph new_sgraph=make_graph(make_dattr(sgraph));/* zum spaetern feigeben */
 sgraph->attrs=make_dattr(sgraph);
 
 /******  kopiere alle it 1 markierte Knoten (Zhk+Separator)   ****/
/* printf("\n     untersuche ZHkomp:");*/
 for_all_nodes(sgraph,snode)
    {if(attr_flag(snode)==1) 
         {groesse++;
          new_snode=make_node(new_sgraph,empty_attr);
          new_snode->x=100;new_snode->y=100;
          set_nodelabel(new_snode,snode->label);
          snode->iso=new_snode;
          new_snode->iso=snode->iso;
          new_snode->attrs=make_dattr(new_my_attr(original_node(snode)));
          new_snode->nr=groesse;
       /*   printf(" %s ",snode->label);*/
         }
    }end_for_all_nodes(sgraph,snode)



/* printf("\n    gehoert zu Sep:");printmenge(sgraph,sep);*/

 /******  kopiere Kanten in neuen Teilgraphen,      ***
   *****  lasse Kanten innerhalb des Separators aus ***/

 for_all_nodes(sgraph,snode)
    {if( (attr_flag(snode)==1) && (!snode_in_menge(snode,sep) )  )
       {attr_flag(snode)=3;
	for_sourcelist(snode,sedge)
          {if (attr_flag(sedge->tnode)==1) 
	       {make_edge(snode->iso,sedge->tnode->iso,empty_attr);
                anzahl_kanten++;
               }
          }end_for_sourcelist(snode,sedge)}
    }end_for_all_nodes(sgraph,snode)


 /****** fuege alle moeglichen Kanten innerhalb des Separators hinzu ******/

 for_menge(sep,snode)
     {snode2=snode;
      while ((snode2=snode2->suc)!=sgraph->nodes)
           {if ( snode_in_menge((Snode)snode2,sep) ) 
                {make_edge(snode->iso,snode2->iso,empty_attr);
                 anzahl_kanten++;
	   }
        }
     }end_for_menge(sep,snode)
			
 

 /* neuen sgraph an liste anhaengen */
 /* mein_print_sgraph(new_sgraph);  */
/* printf("/n***********************************************************"); */
 groesse_graph=groesse;
 finde_sep(new_sgraph,groesse,momentane_bw,anzahl_kanten);
 groesse_graph=0;
 for_all_nodes(sgraph,snode)
     {groesse_graph++;
      if(attr_flag(snode)!=0)    {attr_flag(snode)=2;}
     }end_for_all_nodes(sgraph,snode)

}

/************************************************************************/
/*              pruefe ob k-Menge ==  Separator		                */
/************************************************************************/
/*                                                                      */
/* Parameter  : Sgraph     : aktueller Teilgraph			*/
/*		Menge sep  : k-Knotenmenge				*/
/*		int momentane_bw : max. Baumweite des Teilgraphen       */
/*									*/
/* Rueckgabeparameter : bool : Ob k-menge == Separator		   	*/
/*									*/
/* Funktionbeschreibung:                                             	*/
/*    Ausgehend von einem beliebigen Knoten wird der Sgraph          	*/
/*    durchlaufen ohne einen Knoten aus sep zu passieren.            	*/
/*    Dabei wird jeder markierte Knoten markiert.                    	*/
/*     Sind anschliessend alle Knoten markiert ist Sep kein Separator	*/
/*    anderenfalls ist sep ein Separator und die erste Zusammenhangs-	*/
/*    komponente entspricht den markierten Knoten.                   	*/
/*    Betracht nun die noch nicht markierten knoten. Suche noch nicht	*/
/*    markierten Knoten und Durchlaufe den Graph wie oben.           	*/
/*     Die neu markierten Knoten entsprechen der 2. Zhkomponennte.   	*/
/*   Fahre so fort bis alle Knoten markiert.                         	*/
/*									*/
/************************************************************************/

static bool baue_separator_strucktur(Sgraph sgraph, Menge sep, int momentane_bw)
{bool flag,flag2;
 Snode snode;

 flag2=FALSE; 
 flag =FALSE;

 /*printf("\n separator:  ");printmenge(sgraph,sep);*/
 unmark_sgraph_separatorheuristic(sgraph);
 mark_menge_separatorheuristic(sgraph,sep);
 for_all_nodes(sgraph,snode)
    {if(attr_flag(snode)==0)
        {if(!flag)
           {dfs_sgraph_separatorheuristic(snode);
            flag=TRUE;
	   }
         else
           {if(!flag2)
                 {/*message("speichere Sep:");printmenge(sgraph,sep);message("\sep");*/
                  separatorenliste=anhaengen(separatorenliste
						,save_menge(sgraph,sep));
		 }
            speichere_teilgraph(sgraph,sep,momentane_bw);
                                                       /*markierte Knoten*/
            mark_menge_separatorheuristic(sgraph,sep);
            dfs_sgraph_separatorheuristic(snode);
            flag2=TRUE;
	   }            
       }
     }end_for_all_nodes(sgraph,snode)
 if (flag2)
   { speichere_teilgraph(sgraph,sep,momentane_bw);
    return true;
   }
/* message("Fehler");*/
 return false;}

/*********************************************************************/
/*        Finde alle k-Knotenteilmengen des Sgraphens                */
/*********************************************************************/
/*                                                                   */
/* Parameter    : Sgraph sgraph    : aktueller Teilraph              */
/*                int anf          : in diesem Schritt erster zu-    */
/*                                   laessiger Knoten                */
/*                int tiefe        : Anzahl der bereits aufgenommenen*/
/*                                   Knoten                          */
/*                Menge sep        : enthaelt die bereits aufge-     */
/*                                   nommenen Knoten                 */
/*		  int momentane_bw : max. Baumweite des 	     */
/*				     Teilgraphen	             */
/*                                                                   */
/* Rueckgabeparameter : bool  ob fuer `momentane_bw' ein k-Separator */
/*		              gefunden wurde.			     */
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

bool bilde_separator(Sgraph sgraph, int anf, int tiefe, Menge sep, int groesse, int momentane_bw)
{
 int kk=momentane_bw;
 register int i;

/* if (algorithmus==SEPHEUHEURISTIC)*/
 /* In diesem Fall 2/3 Separatorenheuristik*/
 /*   {
*/
	/*Arbeite mit Separatorliste*/
 	/* Slist slist;
	 slist= berechne_sep(sgraph);
	 momentane_bw=maximum(momentane_bw,size_of_slist(slist));
*/	/* message("Separator groesse %i :%",momentane_bw);
         printinfo2(slist);*/
 	/*baue_separator_strucktur(sgraph,baue_menge_aus_slist(slist),momentane_bw);
	 return true;
 }*/
 /*sonst minimaler Separator*/

 if(tiefe==kk+1)
   {/*printf("\n%i-menge:",kk);printmenge(sgraph,sep); */
    if(baue_separator_strucktur(sgraph,sep,momentane_bw))
           {return true;}
   }
 else
   {
    for(i=anf;i<=groesse-kk+tiefe;i++)
       {if(tiefe==1) {fortschritt(i);}                       
        sep[i/mgroesse]=sep[i/mgroesse]|1<<(i%mgroesse);/*i anhaengen*/
        if(bilde_separator(sgraph,i+1,tiefe+1,sep,groesse,momentane_bw))  
                  {return true;}
        sep[i/mgroesse]=sep[i/mgroesse]&(255-(1<<(i%mgroesse)));/*i entfernen*/
       }
   }
 return false;
}

/*****************************************************************************/
/*               finde einen Separator fuer sgraph                           */
/*****************************************************************************/
/*                                                                           */
/* Parameter    : Sgraph sgraph    : aktueller Teilraph                      */
/*		  int momentane_bw : max. Baumweite des Teilgraphen          */
/*		  int anzahl_kanten: Kantenzahl des Teilgraphen		     */
/*                                                                           */
/*  Rueckgabeparameter :   Hgraph    ergaentzte Hgarphstrucktur              */
/*                                                                           */
/*  Aufgabe:   findet mit Hilfe von `bilde_separator' den kleinsten Separator*/
/*	       und erkennt vollstaendige Cliquen	                     */
/*                                                                           */
/*****************************************************************************/

void finde_sep(Sgraph sgraph, int groesse, int momentane_bw, int anzahl_kanten)
{
 Snode snode;
Slist slist;

 int lok_bw=minimum(momentane_bw,groesse-2); /*wenn groesse = n und lok_bw =n+1
                                              => findet keine Separator mehr */

 if( ((groesse-1)*groesse)/2 == anzahl_kanten)
       {/*printf("\n   -----blatt_gefunden:----");*/
        for_all_nodes(sgraph,snode)
           {/*printf("  %s",snode->label);*/
       }end_for_all_nodes(sgraph,snode)
      teilgraph_bw=maximum(teilgraph_bw,groesse-1);
      slist=make_slist_of_sgraph(sgraph);
      blattliste=anhaengen( blattliste,slist);
    /*  message("Blatt gespeichert :");printinfo2(slist);message("\n");*/
     }
else {while (true) 
         {/*printf("suchsep der groesse: %i",lok_bw);*/
	  sprintf(text,"Suche Sep der Groesse %i",lok_bw);
          init_fortschritt(text,groesse-k);
          if(bilde_separator(sgraph,1,1,new_menge(),groesse,lok_bw)) 
		
		{break;}
          lok_bw++;
         }
      teilgraph_bw=maximum(teilgraph_bw,lok_bw);
     }
}


/*****************************************************************************/
/*            speichere Zusammenhangskomponente als neuen Sgraph             */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph       :  aktueller Teilgraph			     */
/*		Menge sep    :  Separatorknotenmenge			     */
/*                                                                           */
/*  Rueckgabeparameter :    Sgraph : Zusammenhangskomponente als neuer Graph */
/*                                                                           */
/*  Aufgabe :   speichert die gefundene mit `1' markierte Zusammenhangs-     */
/*		knotenmenge als neuen Sgarphen. Dazu werden alle Kanten      */
/*		kopiert. 				                     */
/*                                                                           */
/*****************************************************************************/

Sgraph speichere_teilgraph1(Sgraph sgraph, Menge sep)
{
 Sedge sedge;
 Snode new_snode; 
  Snode snode;
 Sgraph new_sgraph=make_graph(empty_attr);

 /*kopiere alle it 1 markierte Knoten (Zhk+Separator)*/
 /*printf("\nuntersuche ZHkomp:");*/
 for_all_nodes(sgraph,snode)
    {if(snode->attrs.value.integer==1) 
         {new_snode=make_node(new_sgraph,empty_attr);
          new_snode->x=100;new_snode->y=100;
          set_nodelabel(new_snode,snode->label);
          snode->iso=new_snode;
          new_snode->iso=snode;
          new_snode->nr=snode->nr;
        /*  printf(" %s ",snode->label);*/
          }
    }end_for_all_nodes(sgraph,snode)
/*printf("\ngehoert zu Sep:");printmenge(sgraph,sep);*/
/*  kopiere Kanten in neuen Teilgraphen   */
 for_all_nodes(sgraph,snode)
    {if(snode->attrs.value.integer==1) 
       {for_sourcelist(snode,sedge)
          {if(sedge->tnode->attrs.value.integer==1)
             {make_edge(snode->iso,sedge->tnode->iso,empty_attr);
                          snode->attrs.value.integer=3;
             }
          }end_for_sourcelist(snode,sedge)}
    }end_for_all_nodes(sgraph,snode)
for_all_nodes(new_sgraph,snode)
      {snode->iso->attrs.value.integer=2;/*bleiben markiert, naechstes mal ignorieren*/
      }end_for_all_nodes(new_sgraph,snode)
/* neuen sgraph an liste anhaengen */
/* mein_print_sgraph(new_sgraph);*/
/* init_sgraph(new_sgraph); */
return new_sgraph;
}


/************************************************************************/
/*              pruefe ob k-Menge ==  Separator		                */
/************************************************************************/
/*                                                                      */
/* Parameter  : Sgraph     : aktueller Teilgraph			*/
/*		Menge sep  : k-Knotenmenge				*/
/*		int momentane_bw : max. Baumweite des Teilgraphen       */
/*									*/
/* Rueckgabeparameter : bool : Ob k-menge == Separator		   	*/
/*									*/
/* Funktionbeschreibung:                                             	*/
/*    Ausgehend von einem beliebigen Knoten wird der Sgraph          	*/
/*    durchlaufen ohne einen Knoten aus sep zu passieren.            	*/
/*    Dabei wird jeder markierte Knoten markiert.                    	*/
/*     Sind anschliessend alle Knoten markiert ist Sep kein Separator	*/
/*    anderenfalls ist sep ein Separator und die erste Zusammenhangs-	*/
/*    komponente entspricht den markierten Knoten.                   	*/
/*    Betracht nun die noch nicht markierten knoten. Suche noch nicht	*/
/*    markierten Knoten und Durchlaufe den Graph wie oben.           	*/
/*     Die neu markierten Knoten entsprechen der 2. Zhkomponennte.   	*/
/*   Fahre so fort bis alle Knoten markiert.                         	*/
/*									*/
/************************************************************************/


Slist pruefe_auf_separator(Sgraph sgraph, Menge sep)
{bool flag,flag2;
 Snode snode;
 Slist zhkomp=empty_slist;

 flag2=FALSE; 
 flag =FALSE;
/* message("\n separator:  ");printmenge(sgraph,sep);*/
 unmark_sgraph(sgraph);
 mark_menge(sgraph,sep);
 for_all_nodes(sgraph,snode)
    {if(snode->attrs.value.integer==0)
        {if(!flag)
          {dfs_sgraph_graph_durch_cliquen_aufspalten(snode);
            flag=TRUE;}
          else
           {if(!flag2)
                 {
		  separatorenliste=anhaengen(separatorenliste
						,save_menge(sgraph,sep));
		}
                
            zhkomp=anhaengen(zhkomp,speichere_teilgraph1(sgraph,sep));
            /*mein_print_sgraph((Sgraph)(zhkomp->pre->attrs.value.data));*/
            mark_menge(sgraph,sep);
            dfs_sgraph_graph_durch_cliquen_aufspalten(snode);
            flag2=TRUE;
           }            
       }
     }end_for_all_nodes(sgraph,snode)
if (flag2)
  {            
   zhkomp=anhaengen(zhkomp,speichere_teilgraph1(sgraph,sep));
           /* mein_print_sgraph((Sgraph)(zhkomp->pre->attrs.value.data));*/}
return zhkomp;
}

/*****************************************************************************/
/*                Spalte sgraph durch Cliquen in Teilgraphemn auf            */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph            : aktueller Inputgraph	             */
/*              Slist Cliquemenge : Liste aller Cliquen (Mengen)	     */
/*                                                                           */
/*  Rueckgabeparameter :   Slist  : Liste der Teilgraphen	             */
/*                                                                           */
/*  Aufgabe:    Prueft ob der Sgraph Cliquen besitzt die Separatoren sind.   */
/*		Wenn ja wird der Graph in Teilgraphen aufgespalten. 	     */
/*	        Aufspalten in Teilgraphen durch Cliquen als Separatoren be-  */
/*		einflusst die Baumweite nicht. => Graph kann sinnvoll ver-   */
/*		einfacht werden.					     */
/*		Kann der Graph nicht aufgespaltet werden wird empty_slist    */
/*		zurueckgegeben.						     */
/*                                                                           */
/*****************************************************************************/

Slist graph_durch_cliquen_aufspalten(Sgraph sgraph, Slist cliquenmenge)
{Slist sclique,szhk,zhkomp=empty_slist;
 Menge clique;
 Sgraph zhk;
 Slist zhkomp2;
 Snode snode;

 /* baue menge von teilsgarphen */
 zhkomp=anhaengen(zhkomp,sgraph);

 for_all_nodes(sgraph,snode)
     {snode->iso=snode;
     }end_for_all_nodes(sgraph,snode)

 for_slist(cliquenmenge,sclique)
     {clique=dattrs(sclique);
      /*   printf("\n untersuche folgende Clique:");printmenge(sgraph,clique);*/
      for_slist(zhkomp,szhk)
          {zhk=(Sgraph)dattrs(szhk);
           /*  printf("\nteilmenge von?");printmenge(sgraph,make_menge_of_teilgraph(zhk));*/
           if(menge_teilmenge_von_menge(clique,make_menge_of_teilgraph(zhk)))
               {
                zhkomp2=pruefe_auf_separator(zhk,clique);
                if(zhkomp2!=empty_slist)
                     {zhkomp=subtract_immediately_from_slist(zhkomp,szhk);
                      zhkomp=add_slists(zhkomp,zhkomp2);}
                break;}
          }end_for_slist(zhkomp,szhk) 
    }end_for_slist(cliquenmenge,sclique)
/*printf("\n erste Stufe beendet");*/
return zhkomp;
}

/*****************************************************************************/
/*		 Kontrollprozedur fuer die Separatorheuristik		     */
/*****************************************************************************/
/*                                                                           */
/*  Parameter:  Sgraph       : aktueller Inputgraph		             */
/*                                                                           */
/*  Rueckgabeparameter :   Sgraph    gefundene Baumzerlegung                 */
/*                                                                           */
/*****************************************************************************/

Sgraph separatorheuristik (Slist teilgraphen)
{Slist steilgraph;
 Sgraph sgraph;
 int max_bw=0;
 Snode snode;
 Slist element,element2;
 Sgraph new_sgraph;
 Menge menge=new_menge();

 
 teilgraph_bw=1;
 text=mein_malloc(sizeof(char*)*40); /* Platz fuer Statustext reservieren*/


 /*** iso-Zeiger auf sich selbst um spaeter immer auf urspruenglichen
    snode referenzieren zu koennen *****/
 for_slist(teilgraphen,steilgraph)
     {sgraph=(Sgraph)dattrs(steilgraph);
      init_sgraph(sgraph);
      for_all_nodes(sgraph,snode)
          {snode->attrs=make_dattr(new_my_attr(snode));
          }end_for_all_nodes(sgraph,snode)
      
      finde_sep((Sgraph)dattrs(steilgraph)
           ,anzahl_nodes((Sgraph)dattrs(steilgraph))
           ,1,kantenzahl((Sgraph)dattrs(steilgraph))) ;
      max_bw=maximum(teilgraph_bw,max_bw);
     }end_for_slist(teilgraphen,steilgraph)
 
 max_baumweite=max_bw;



if (algo_output_type==BAUMBREITE)
       { return empty_sgraph;  }   

/***********************************************/
/* konvertiere blattliste und separatorenliste */
/***********************************************/
 
 new_sgraph=make_graph(make_dattr(sgraph));
 new_sgraph->directed=TRUE;


 for_slist(separatorenliste,element)
     {menge=empty_menge(menge);
      for_slist( (Slist)dattrs(element) ,element2)
            {menge=add_to_menge( menge , original_node(sattrs(element2)) );
	    }end_for_slist( (Slist)dattrs(element) ,element2)
   /*    printf("\nsep: ");printmenge(sgraph,menge);*/
       snode=make_node(  new_sgraph,make_dattr(copy_menge(menge))  );  
       my_set_nodelabel(sgraph,snode,menge);  
       snode->x=100;snode->y=100;
       element->attrs=make_dattr(snode);
   /*    printf("  ==: ");printmenge(sgraph,mattrs(sattrs(element)));*/
      }end_for_slist(separatorenliste,element)


 for_slist(blattliste,element)
      {menge=empty_menge(menge);
       for_slist( (Slist)dattrs(element) ,element2)
           {menge=add_to_menge( menge , original_node(sattrs(element2)) );
           }end_for_slist( (Slist)dattrs(element) ,element2)
    /*   printf("blatt: ");printmenge(sgraph,menge);*/
       snode=make_node(  new_sgraph,make_dattr(copy_menge(menge))  );  
       my_set_nodelabel(sgraph,snode,menge);  
       snode->x=100;snode->y=100;
       element->attrs=make_dattr(snode);
     }end_for_slist(blattliste,element)

/**********************************************/

 print_daten(sgraph);
 baue_baumzerlegung_aus_listen(sgraph);


 end_fortschritt();
 return new_sgraph;     
}


