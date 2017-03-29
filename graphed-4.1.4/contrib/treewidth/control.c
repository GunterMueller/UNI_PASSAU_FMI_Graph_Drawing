/*****************************************************************************/
/*                                                                           */
/*                         M A I N   -   C O N T R O L                       */
/*                                                                           */
/* Modul        : control.c						     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/* Dieses modul enthelt die Shcnittslellen zwischen Graphed und Sgraphauf-   */
/* rufen.   (call_sgraph_proc)						     */
/*****************************************************************************/
#include "control.h"
#include "mystd.h"
#include <algorithms.h>

#include <xview/xview.h>

#include <tree_layout_walker/tree_layout_walker_export.h>
#include <maxclique/maxclique.h>

#include "notice.h"
#include "menge.h"
#include "arnborg.h"
#include "nice_tree_dec.h"
#include "reduce_tree_dec.h"
#include "decomp_window.h"
#include "untere_schranke.h"
#include "heuristic.h"
#include "valenzheuristic.h"
#include "separatorheuristik.h"
#include "optionen.h"
#include "schnittstelle.h"
#include "mainwindow.h"
#include <sgraph_interface.h>

/*#define MEM_DEBUG  */ /*um Memoryallocation verfolgen zu koennen*/
#include "gragra_parser/w_memory.h"

#include <setjmp.h>

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/* Aufrufe fuere Prozedueren, die auf Sgraph arbeiten			     */
/*								             */
/*	void 	relabel_nodes_aufruf		()			     */
/*								             */
/*	void 	relabel_nodes_aufruf		()			     */
/*								             */
/*	void 	durchlaufe_graph_aufruf		()			     */
/*								             */
/*	void 	change_label_aufruf		()			     */
/*								             */
/*	void 	nice_tree_decomp_aufruf		()			     */
/*								             */
/*	void 	min_tree_decomp_aufruf		()			     */
/*								             */
/*	void 	wurzel_aendern_aufruf		()			     */
/*								             */
/*	void 	wurzel_mitte_aufruf		()			     */
/*								             */
/*	void 	graph_reduzieren_aufruf		()			     */
/*								             */
/*	int 	clickactions_aufruf		(char* info,Event)	     */
/*								             */
/*	void 	clickabfrage_setzen		()			     */
/*								             */
/*	void 	clickabfrage_loeschen		()			     */
/*								             */
/*	void 	main_algorithmen_aufruf		()			     */
/*								             */
/*****************************************************************************/

/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	bool  	G_hat_doppelte_Kante	(Sgraph)			     */
/*            	 Prueft auf Mehrfachkanten				     */
/*								             */
/*	void  	main_algorithmen		(Sgarph_proc_info info)	     */
/*		 ruft die diversen Mainalgorithmen auf			     */
/*		 enthaelt zentrale Fehlerabfrage und Ausgabesteuerung	     */
/*								             */
/*****************************************************************************/

extern void speichere_sgraph_fuer_vergleich(Sgraph sgraph);
extern zhk_ausgeben;
extern main_control_window_frame_active;
int errorflag;
extern sgraph_gezeichnet;

jmp_buf my_enviroment; /* longjump bei Speicherueberlauf*/



static bool baumzerlegung_gezeichnet;
/* praetyping*/ Sgraph call_erweiterung(Sgraph_proc_info info, Slist teilgraphen);

/*#include </home/br/graphed/src/graphed/type.h>*/
/*********************************************************************/
/*********************************************************************/
/*						 		     */
/**                aufrufe fuer sgraph-prozeduren                   **/
/*								     */
/*********************************************************************/
/*********************************************************************/
/*    Schnittstelle zwischen Graphed und eigene Graphalgorithmen     */
/*   Die eigene Algorithmen werden durch call_sgraph_proc            */
/*   aufgerufen							     */
/*********************************************************************/

void relabel_nodes_aufruf(void)
{call_sgraph_proc(relabel_nodes, NULL);}
/*********************************************************************/
/*                      label Nodes				     */
/*********************************************************************/
void label_nodes_aufruf(void)
{call_sgraph_proc(label_knoten, NULL);
knoten_anpassen();
}
/*********************************************************************/
/*                    durchlaufe Knoten				     */
/*********************************************************************/
void durchlaufe_graph_aufruf(void)
{call_sgraph_proc(durchlaufe_graph, NULL);}
/*********************************************************************/
/*                      label nodes				     */
/*********************************************************************/
void change_label_aufruf(void)
{call_sgraph_proc(change_label, NULL);
if(!errorflag)
knoten_anpassen();
call_sgraph_proc (baum_zeichnen, NULL);}
/*********************************************************************/
/*                     Nice-Tree_decomposition			     */
/*********************************************************************/
void nice_tree_decomp_aufruf(void)
{call_sgraph_proc(nice_tree_decomp, NULL);
change_label_aufruf();
knoten_anpassen();
call_sgraph_proc (baum_zeichnen, NULL);
dispatch_user_action(CENTER_SELECTION);}
/*********************************************************************/
/*                    min Tree-Decomposition			     */
/*********************************************************************/
void min_tree_decomp_aufruf(void)
{call_sgraph_proc(min_tree_dec, NULL);
change_label_aufruf();
knoten_anpassen();
call_sgraph_proc (baum_zeichnen, NULL);
dispatch_user_action(CENTER_SELECTION);}
/*********************************************************************/
/*               Waehle selektierten Knoten als Wurzel		     */
/*********************************************************************/
void wurzel_aendern_aufruf(void)
{call_sgraph_proc(wurzel_aendern, NULL);
call_sgraph_proc (baum_zeichnen, NULL);
dispatch_user_action(CENTER_SELECTION);}
/*********************************************************************/
/*                   Wahle Mitte als Wurzel			     */
/*********************************************************************/
void wurzel_mitte_aufruf(void)
{call_sgraph_proc(wurzel_mitte, NULL);
call_sgraph_proc (baum_zeichnen, NULL);
dispatch_user_action(CENTER_SELECTION);}
/*********************************************************************/
/*                      reduziere Graph				     */
/*********************************************************************/
/*void graph_reduzieren_aufruf()
{call_sgraph_proc(graph_reduzieren, NULL);}
*/

/****************************************************************************/
/*									    */
/*                    Steuere Mouseclickabfrage                             */
/*									    */
/****************************************************************************/
/* Die Clickabfrage wird hier gesetz oder geloescht                         */ 
/****************************************************************************/

/*********************************************************************/
/*                    Reagiere auf Mouseclick			     */
/*********************************************************************/
User_event_functions_result clickactions_aufruf(UEV_info info, Event *event)
{call_sgraph_event_proc(my_sgraph_click_event_func,info,event, NULL);return 0;}
/*********************************************************************/
/*                   Starte Klickabfrage			     */
/*********************************************************************/
void clickabfrage_setzen(void)
{set_user_event_func(SGRAPH_UEV_DOUBLE_CLICK,clickactions_aufruf);}
/*********************************************************************/
/*                  Beende  Klickabfrage			     */
/*********************************************************************/
void clickabfrage_loeschen(void)
{call_sgraph_proc (markierungen_loeschen, NULL);
 remove_user_event_func(SGRAPH_UEV_DOUBLE_CLICK,clickactions_aufruf);
}


/****************************************************************************/
/*		 Pruefe auf Selfloops 					    */
/****************************************************************************/
/*									    */
/*    Parameter  :     Sgraph sgraph  : aktueller Graph			    */
/*									    */
/*    Rueckgabeparameter : bool : true wenn doppelte Knaten existieren      */
/*									    */
/****************************************************************************/

bool self_loop(Sgraph sgraph)
{Snode snode;
 Sedge sedge;

 for_all_nodes(sgraph,snode)
    {for_sourcelist(snode,sedge)
        {if(sedge->tnode==snode)
                 {return true;}
        }end_for_sourcelist(snode,sedge)
    }end_for_all_nodes(sgraph,snode)
 return false;
}


/****************************************************************************/
/*		 Pruefe auf doppelte Kanten				    */
/****************************************************************************/
/*									    */
/*    Parameter  :     Sgraph sgraph  : aktueller Graph			    */
/*									    */
/*    Rueckgabeparameter : bool : true wenn doppelte Knaten existieren      */
/*									    */
/****************************************************************************/

bool G_hat_doppelte_Kante(Sgraph sgraph)
              
	/*
	Bestimmt, ob irgendwelche Kanten mehrfach auftreten, d.h. ob 
	zwischen je zwei Knoten hoechstens eine Kanten existiert.
	Falls eine Kante gefunden wird, wird dies zurueckgegeben, sonst
	NULL.
	*/
	{	Snode n;
		for_all_nodes(sgraph,n)
		{	Sedge e;
			for_sourcelist(n,e)
			{	Sedge lauf = e->ssuc; 
				while (lauf != n->slist)
				{	if (e->tnode == lauf->tnode)
						return true;
					lauf = lauf->ssuc;
				}					
			} end_for_sourcelist(n,e);
		} end_for_all_nodes(sgraph,n);
		return false;
	}

/****************************************************************************/
/*                    rufe Hauptalgorithmen auf                             */
/****************************************************************************/
/****************************************************************************/
/* Parameter              : Sgraph_proc_info				    */
/* aufrufende Prozeduren  : main_algorithmen                                */
/****************************************************************************/
/* Diese Prozedur ruft entsprechen der Parameter des Main Windows die       */ 
/* Hauptprozeduren auf.                                                     */
/* Dafuer wird der Sgraph uebernommen und auf Zulaessigkeit ueberprueft     */
/* Wird eine Baumzerlegung gewuenscht wird der neue Sgraph zurueckgegeben   */
/****************************************************************************/

void kein_fenster_mehr(void) {error("es ist nicht moeglich ein\n neues Fenster zu oeffnen\n");}
/****************************************************************************/

void main_algorithmen(Sgraph_proc_info info)
{Sgraph new_sgraph;
 Sgraph sgraph=info->sgraph;
 Slist cliquenmenge;
 Snode snode;
 int oldbuffer;
 Slist teilgraphen;
 Slist loesche,element;

 if(setjmp(my_enviroment)) /*longjmp bei fehlerhaftem mein_malloc*/
	{return;}

 errorflag=FALSE;
 loesche_markierungen();
 baumzerlegung_gezeichnet=false;
/* pruefe auf zulaessigkeit des Graphen (ungerichtet,keine self-loops...)*/
 if (sgraph==nil)  {error("   ***   graph is empty   ***\n");
                           errorflag=TRUE;} 
 else if (sgraph->nodes==nil)  {error("   ***   There is no graph   ***\n");
                           errorflag=TRUE;} 
 else if ((sgraph->label!=nil)&&(strcmp(sgraph->label,"tree-decomposition")==0))
               {error("** graph is already a tree-decomposition **\n");
                            errorflag=TRUE;}
 else if ((sgraph->label!=nil)&&(strcmp(sgraph->label,"tree-decomposition")==0))
               {error("* graph is already a tree-decomposition *\n");
                            errorflag=TRUE;}
 else if (sgraph->directed==TRUE) {error("   ***  graph is directed   ***\n");
                            errorflag=TRUE;}
 else if (test_sgraph_connected(sgraph)!=TRUE)
                           {error("  ***  graph is not connected   ***   \n");
                            errorflag=TRUE;}
 else if ( self_loop(sgraph) ){error("  ***  graph contains a self loop ***\n");
                             errorflag=TRUE;} 
 else if (G_hat_doppelte_Kante(sgraph))  {error("   ***  graph has multiple edges   ***\n");
                            errorflag=TRUE;}

 else     {/*  es ist notwendig dass die Knoten gelabelt sind */
      for_all_nodes(sgraph,snode)
        {if(snode->label==nil) {error("   *** there is a unlabeled node ***\n");
                                errorflag=TRUE;
                                break;}
        }end_for_all_nodes(sgraph,snode)}

 if( !errorflag) 
   {init_fortschritt("computing",10);                       
    init_sgraph(sgraph);

/**** Ueberpruefe ob Graph nur aus einem Knoten besteht => direkte Bearbeitung*/
  if (sgraph->nodes ==sgraph->nodes->suc)
	{new_sgraph=make_graph(empty_attr);
	 new_sgraph->attrs=make_dattr(sgraph);   /* zeiger auf sgraph merken */
          new_sgraph->directed=TRUE;
          snode=make_node(new_sgraph,make_dattr(sgraph->nodes));
          set_nodelabel(snode,strsave(sgraph->nodes->label));
          snode->x=370;snode->y=370;
    	  goto vorzeitig_berechnetes_ergebnis;
	}

/*****   Vereinfache Graph soweit sinnvoll ********/
/*****     entferne alle Baumstruckturen und Verschmelze alle Endknoten mit Valenz=2 */
/*****     Versuche Garph durch Cliquen in Teilgraphen zu Zerlegen *****/
/*****     Hierbei muss beachtet werden, dass die Baumweite sich reduzieren kann. ***/
/*****    Daher muss sie gesichert werden.                                         ***/

 min_baumweite=0;
 max_baumweite=0;


/**** hier kann auch ein leerer Graph entstehen !!!! ****/
/**** die Baumweite ist in diesem Fall = minbaumweite. Erkennbar an sgraph=NULL ****/

 if ( (algo_output_type==BAUMBREITE)  &&  (algorithmus!=VALENZHEURISTIK)
         && (!( (algorithmus==MAX_CLIQUEN)||(algorithmus==DOM_CLIQUEN) ) )     )
     {
      sgraph=reduziere_sgraph(sgraph);
     }
 if (sgraph==(Sgraph)NULL)
   {/* sonst weitere Berechnung unnoetig da Baumweite bekannt.*/
    max_baumweite=min_baumweite;
    goto vorzeitig_berechnetes_ergebnis;}

   separatorenliste=empty_slist;
   blattliste=empty_slist;
   if (  (algo_output_type==BAUMBREITE) 
         && (!( (algorithmus==MAX_CLIQUEN)||(algorithmus==DOM_CLIQUEN) ) ) 
      )
        {/*  Durch aufspalten kann die Baumweite nicht veraendert werden !!*/
	 init_fortschritt("Suche Cliquenseparatoren",10);
	 fortschritt(5);
	 teilgraphen=graph_durch_cliquen_aufspalten
				(sgraph,berechne_cliquen(sgraph));

	/*nun jeden Teilgraphen nocheinmal reduzieren und zu Null reduzierte */
	/* Teilgraphen aus der Slist streichen		*/
	loesche=empty_slist;
	for_slist(teilgraphen,element)
		{reduziere_sgraph((Sgraph)dattrs(element));
		 if((Sgraph)(dattrs(element))==(Sgraph)(NULL))
			{loesche=anhaengen(loesche,(Sgraph)(dattrs(element)) );}
		}end_for_slist(teilgraphen,element)
	for_slist(loesche,element)
		{subtract_from_slist(teilgraphen,make_attr(ATTR_DATA, (Sgraph)dattrs(element)));
		}end_for_slist(loesche,element)

        free_slist(loesche);
	}

  if(teilgraphen==empty_slist)
	{
	teilgraphen=empty_slist;
	teilgraphen=anhaengen(teilgraphen,sgraph);
        }
  w_reset_observation();

      switch (algorithmus)
        { case ARNBORG              :{ if(algo_output_type != BAUMBREITE) 
						{message("!!! The arnborgalgorihm can't compute\n    a tree-decomposition !!!!!"); 
						 algo_output_type = BAUMBREITE;}
new_sgraph=tree_decomp        (teilgraphen);
                                      break;}
         case CLIQUENHEURISTIC     :{ new_sgraph=baumzerlegung      (teilgraphen);
                                      break;}
         case KANTENHEURISTIC      :{ new_sgraph=baumzerlegung      (teilgraphen);
                                      break;}
         case SEPERATORENHEURISTIC :{ new_sgraph=separatorheuristik (teilgraphen); break;}
             case VALENZHEURISTIK      :{ new_sgraph=valenzheuristic      (teilgraphen,sgraph);
                                      break;}
         case UNTERESCHRANKE       :{ finde_beste_untere_Schranke(sgraph);
                                      return;}
         case ERWEITERUNG          :{  new_sgraph=call_erweiterung(info,teilgraphen);
                                       break;}
         case MAX_CLIQUEN          :{ cliquenmenge=maximale_cliquen (sgraph);
                                      break;}
         case DOM_CLIQUEN          :  {cliquenmenge=berechne_cliquen (sgraph); break;}
       }

 /*  w_mem_remain();*/
if (  main_control_window_frame_active )
  { main_control_window(XV_NULL,XV_NULL);}

    if (errorflag) {return;}


/**** Ergebniss ausgaben (und untere Schranke berechnung)		***/

    if ( (algorithmus==MAX_CLIQUEN) || (algorithmus==DOM_CLIQUEN) )
       {print_cliquenmenge(sgraph,cliquenmenge);
        free_slist(cliquenmenge);}

    else{if((algorithmus==KANTENHEURISTIC)
                                    ||(algorithmus==SEPERATORENHEURISTIC)
                                    ||(algorithmus==VALENZHEURISTIK       ) )
             {min_baumweite=maximum(min_baumweite,kantenbeschraenkung(sgraph));
              if(untere_schranke==FINDE_MINBW_DURCH_MAX_CLIQUE)
                  {min_baumweite=maximum(min_baumweite,
                              size_of_slist(M_find_max_clique(sgraph))-1);
                  }
             }
              if(untere_schranke==FINDE_MINBW_DURCH_MAXCLIQUENSEPERATOR)
                  {min_baumweite=
                        finde_untere_schranke_durch_maxCliquenseparator(sgraph);
                  }


    /* Hierhin wird mit Goto gesprungen,Ergebniss ohne Algorithmen berechnet wurde*/    
            vorzeitig_berechnetes_ergebnis:
              if(max_baumweite==min_baumweite)
                {message("\nThe treewidth is exactly %i\n",max_baumweite); 
                }
            else
               {message("\nlower bound: %i, upper bound: %i \n",min_baumweite,max_baumweite);
    
                }
         
/*********** Baumzerlegungsausgabe ******************/

         if ( (algo_output_type != BAUMBREITE)  && (new_sgraph!=empty_sgraph) )
            {/*   Baumzerlegung (in new_slist)  ausgeben  */
                         set_graphlabel(new_sgraph,strsave("tree-decomposition"));
             new_sgraph->attrs=make_dattr(info->sgraph);

	     if (jede_baumzerlegung_in_neues_fenster)
		 {info->new_buffer=create_buffer();}

	     else{	
  	          if ((oldbuffer=find_buffer_by_name("-tree_decomposition"))!=-1)
                     {dispatch_user_action(UNSELECT);
	              delete_graphs_in_buffer(oldbuffer); 
		     }

                  info->new_buffer=get_buffer_by_name("-tree_decomposition");
		 }
             if(info->new_buffer==-1) 
			 {
			  kein_fenster_mehr();
	                 }
             else      {if (jede_baumzerlegung_in_neues_fenster)
			      {buffer_set_filename(info->new_buffer,"tree-decomposition");
			      }
			info->sgraph->attrs=make_dattr(new_sgraph);
             		info->new_sgraph=new_sgraph;
             		info->sgraph->label=strsave("old_graph");
             		info->no_structure_changes=FALSE;
             		info->recompute=TRUE;
             		info->new_selected=SGRAPH_SELECTED_NONE;
             		info->recenter=TRUE;
             		baumzerlegung_gezeichnet=TRUE;
             		decomposition_window();
			speichere_sgraph_fuer_vergleich(sgraph);
		       }
             }

        }
    } 
aktuallisiere_algo_output_type(); /* fuer denn Fall, dass ser Benutzer den
Ausgabe typ umsetzt.*/
 end_fortschritt();

}     

 
/*********************************************************************/
/*              Hauptalogorithmen aufruf		             */
/*********************************************************************/
/*    Parameter          :   void				     */
/*    Rueckgabeparameter :   void				     */
/*********************************************************************/
/* Schnittstelle zwieschen Graphed und Hauptalgorithmen              */
/* Der Sgraph wird an `main_algorithmen_aufruf' ubergeben.           */
/* Anschliessend werden, falls ein Graph ausgegeben soll, noch ver-  */
/* schiedene Layoutalgoritmen aufgerufen.			     */
/*********************************************************************/


void main_algorithmen_aufruf(void)
{call_sgraph_proc(main_algorithmen, NULL);
 if(baumzerlegung_gezeichnet)
     {if (longlabel==FALSE)
           {call_sgraph_proc(label_nodes, NULL);
           }
      knoten_anpassen();
      /* call_sgraph_proc (wurzel_mitte, NULL);*/
      call_sgraph_proc (baum_zeichnen, NULL);
      dispatch_user_action(CENTER_SELECTION);
     }
}

/*********************************************************************/
void dom_Cliquen_aufruf (void)
{ algorithmus=DOM_CLIQUEN;
  main_algorithmen_aufruf();
}


void max_cliquen_aufruf (void)
{ algorithmus=MAX_CLIQUEN;
  main_algorithmen_aufruf();
}

/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
/* zur Zeit nicht aktiv!  */

void baumweite_intelligent(Sgraph_proc_info info)
{}
/*Sgraph sgraph=info->sgraph;
 int obere_schranke;

 init_sgraph(sgraph);
 min_baumweite=kantenbeschraenkung(sgraph);
 min_baumweite=maximum(untere_schranke
                     ,finde_untere_schranke_durch_maxCliquenseparator(sgraph));
 valenzheuristic(sgraph);
 obere_schranke=max_baumweite;
 if (min_baumweite!=max_baumweite)
        {algorithmus=KANTENHEURISTIC;
     */    /*reduziere_sgraph(info)*/
   /*      baumzerlegung      (sgraph);
         max_baumweite=maximum(max_baumweite,obere_schranke);
        }

 if(max_baumweite==min_baumweite)
     {message("The Treewidth is exactly %i\n",max_baumweite); 
                }
 else
     {message("The Treewidth is larger than: %i \n             and lower than: %i \n" 
                               ,min_baumweite,max_baumweite);
    
     }
}*/
/*********************************************************************/
/*********************************************************************/
/*   Schnittstelle zu Fremdroutinen                                  */
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/

Sgraph call_erweiterung(Sgraph_proc_info info, Slist teilgraphen)
{Tree_width_info tw_info;
 proced procedure;
 procedure=get_fremd_procedure();

 /* Initialisiere <Tree_width_info>   */
 tw_info=(Tree_width_info)mein_malloc(sizeof(struct tree_width_info));
 tw_info->sgraph_proc_info=info;
 tw_info->min_baumweite=min_baumweite;
 tw_info->max_baumweite=max_baumweite;
 tw_info->new_sgraph=(Sgraph)NULL;
 tw_info->algo_output_type=algo_output_type;
 tw_info->errorflag=false;
 tw_info->teilgraphen=teilgraphen;
 
 /* Rufe Fremdroutine auf */
procedure (tw_info);

 /* gebe berechnete Daten zurueck */
 min_baumweite=tw_info->min_baumweite;
 max_baumweite=tw_info->max_baumweite;
 errorflag=tw_info->errorflag;
 algo_output_type=tw_info->algo_output_type;
 free(tw_info);
 return tw_info->new_sgraph;
}


/*********************************************************************/
/*********************************************************************/




/***************************************************************************/
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/





