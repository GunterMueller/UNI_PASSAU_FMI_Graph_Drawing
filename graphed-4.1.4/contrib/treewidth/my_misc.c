/*******************************************************************************/
/*                 A l l g e m e i n e   P r o z e d u r e n                   */
/*                                                                             */
/* Modul        : my_misc.c						       */
/* erstellt von : Nikolas Motte                                                */
/* erstellt am  : 10.12.1992                                                   */
/*                                                                             */
/*******************************************************************************/

#include "my_misc.h"
#include <math.h>             /* fuer sqrt in my_set_nodelabel   */
#include <string.h>           /* fuer         my_set_nodelabel   */
#include <xview/xview.h>

#include "separatorheuristik.h"
#include "valenzheuristic.h"        /* fuer reduziere_sgraph*/
#include "cliquen.h"          /* fuer max_cliquen     */

#include "tree_layout_walker/tree_layout_walker_export.h"
extern void fit_nodes_to_text(Menu menu, Menu_item menu_item);


/********************************************************************************/
/*						                                */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		                */
/*								                */
/********************************************************************************/
/*								                */
/*		void 	relabel_nodes		(Sgraph_proc_info)		*/
/*								                */
/*		void 	markierungen_loeschen	(Sgraph_proc_info)		*/
/*			setze die ogrinal nodetypes wieder			*/
/*								                */
/*		void 	clique_anzeigen		(Sgraph_proc_info)		*/
/*			markiere einen knoten durch `my_nodetype'		*/
/*								                */
/*		void 	durchlaufe_graph	(Sgraph_proc_info)		*/
/*			markiere den selektierten Knoten mit `clique_anzeigen'  */
/*			und setze die Selektion einen Knoten weiter.		*/
/*								                */
/*		void 	tree_umbauen		(Snode,Snode oldsnode)		*/
/*			Rekursionsschritt fuer `baum_ausgleichen'		*/
/*								                */
/*		void 	baum_ausgleichen	(Snode wurzel)			*/
/*			Vewraendere die Kantenrichtungen so, dass alle Knaten	*/
/*			von der neuen Wurzel wwegzeigen.			*/
/*								                */
/*		void 	wurzel_aendern		(Sgraph_proc_info)		*/
/*			Sgraph_proc fuer `baum_ausgleichen'			*/
/*								                */
/*		void 	traverse_tree		(Snode,int tiefe,Snode oldsnode)*/
/*			Rekursionsschritt fuer `wurzel_mitte'			*/
/*								                */
/*		void 	wurzel_mitte		(Sgraph_proc_info)		*/
/*			finde die Mitte des laengsten Pfades im Baum		*/
/*								                */
/*		void 	my_set_nodelabel	(Sgraph,Snode,Menge clique)	*/
/*			label den Knoten mit den Labels aus der Menge bezueglich*/
/*			des Sgraphen						*/
/*								                */
/*		void 	my_set_nodelabel2	(sgraph,snode,Slist clique)	*/
/*			wie zuvor nur Slist statt Menge				*/
/*								                */
/*		void	aktualisiere_str	(int i,char* string)		*/
/*								                */
/*		int 	stringlaenge		(Sgraph)			*/
/*								                */
/*		void 	label_nodes		(Sgraph_proc_info)		*/
/*								                */
/*		void	change_label		(Sgraph_proc_info)		*/
/*								                */
/*		void 	baum_zeichnen		(Sgraph_proc_info)		*/
/*			rufe Baumzeichenroutine (Fremdroutine) auf		*/
/*			hierzu muessen zuerst alle Attribute gerettet werden	*/
/*								                */
/*		bool 	valenzbedingung		(Sgraph,Menge clique		*/
/*							,int max_cliquensize)   */
/*								                */
/*		int	finde_untere_schranke_durch_maxCliquenseparator (Sgraph)*/
/*			berechne die untere Schranke der Baumweite durch 	*/
/*			CliquenSeparatorbedingungen (siehe hierzu Diplomarbeit) */
/*								                */
/*		Slist 	finde_max_cliquen_aus_dominanten			*/
/*					(Sgraph,Slist dominante_cliquenmenge)	*/
/*								                */
/*   int finde_untere_schranke_durch_maxCliquenseparator_fuer_Cliquenheuristic	*/
/*                                             (sgraph,dominante_cliquenmenge)	*/
/*			wie oben, nur fuer den Fall, dass bereits alle do-	*/
/*			minanten Cliquen bekannt sind.				*/
/* 										*/
/********************************************************************************/

/********************************************************************************/
/*						                                */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		                */
/*								                */
/********************************************************************************/
/*								                */
/*								                */
/********************************************************************************/
/*******************************************************************************/
/*              lokale variablen                                               */
/*******************************************************************************/


   

/*global*/
static int *original_node_type_origi_sgraph;
int original_node_type_tree_dec;
/* Snode *snodefeld_tree_dec=(Snode*)NULL;
 Snode *snodefeld=(Snode*)NULL;
 Sgraph ursprungsgraph;
 Sgraph tree_decomposition_graph;
 int groesse_tree_decomposition_graph;
 int groesse_ursprungsgraph;*/
 bool knotentypen_veraendert=false;
 void mpruefe_sgraph_auf_aenderung(Sgraph_proc_info info); 
bool errorflag_zusammenhang_gegeben;

/* fuer    label_nodes */
static char erstes_zulaessiges_zeichen;
static char letztes_zulaessiges_zeichen;

/* fuer    Wurzel_mitte */
static  int max_tiefe;    /*enthaelt die laenge des laengsten Pfades*/

/*******************************************************************************/
/*******************************************************************************/
Slist markierte_knoten; /*enthaelt alle markierten Knoten als Strucktur-> */

/*******************************************************************************/
typedef struct markierte_knoten {
       int        old_type;     /*alter type*/
       Snode      snode;        /*Knoten*/
        }         *Markierte_knoten;

/*******************************************************************************/

Markierte_knoten make_markierte_knoten(Snode snode, int old_type)
                                
                        
/* allokiere Speicherplatz und initalisiere Strucktur */
{Markierte_knoten new=(Markierte_knoten)mein_malloc(sizeof(Markierte_knoten));
 
 new->old_type=old_type;
 new->snode=snode;
 return new;
}




/*******************************************************************************/
/*                    relabel  nodes                                           */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info                                               */
/*                                                                             */
/* Rueckgabeparameter :     void		                               */
/*                                                                             */
/*******************************************************************************/
/* Funktion : die Knoten einer Baumzerlegung werden wieder mit ihrem Knoten-   */
/*	      gelabelt                                                         */
/*******************************************************************************/

void relabel_nodes(Sgraph_proc_info info)
{Snode snode;
 Menge menge;
init_sgraph_fuer_manipulate_treedec((Sgraph)info->sgraph->attrs.value.data);
for_all_nodes(info->sgraph,snode)
   {menge=baue_menge_aus_slist((Slist)dattrs(snode));
    my_set_nodelabel((Sgraph)(info->sgraph->attrs.value.data),snode,menge);
   }end_for_all_nodes(info->sgraph,snode)
info->recompute=TRUE;
}

/********************************************************************************/
/*                	                                                     	*/
/*     		=================================================          	*/
/*	  	      !    markiere angeclickte  Knoten   !     	       	*/
/*		      !       und durchlaufe Graph	  !			*/
/*		=================================================          	*/
/*                                                                    		*/
/********************************************************************************/

/********************************************************************************/
/*               Setze wieder die urspruenglichen Knotentypen                   */
/********************************************************************************/
/*				       				                */
/* Parameter :  Sgraph_proc_info                                                */
/*				       				                */
/* Rueckgabeparameter :    void			    				*/
/*                                                                              */
/********************************************************************************/

void setze_original_nodetypes(Sgraph_proc_info info)
{loesche_markierungen();
}



/********************************************************************************/
/********************************************************************************/
/********************************************************************************/
/*     Prozeduren zur Verwaltung der Liste der markierten Knoten                */
/*										*/
/* Die Liste wird beim Aufruf eines Baumweitenalgortimus geleert. Beim markeiren */
/* werden jeweils alle bisher markierten Knoten aus der Liste geloescht (und ihr */
/* orignal Knotentyp wieder hergestellt() und die neu markierten Knoten in die   */
/* liste eingetragen.								*/
/* Bei Aufruf einer Funktion die die Strucktur der Baumzerlegung veraendert muss */
/* die unmark Funktion aufgerufen werden. 					*/
/* Da mir keine Methode bekannt ist manuelle Knotenloeschungen abzufangen       */
/* (Absprache mit Himsolt) wird in einem solchen Fall auf einen Knoten zugegriffen*/
/*  und das Programm stuerzt ab.						*/
/********************************************************************************/

void merke_neu_markierten_knoten(Snode snode)
{markierte_knoten=anhaengen(markierte_knoten,make_markierte_knoten(snode,(int)node_get(graphed_node(snode),NODE_TYPE)));
}


/********************************************************************************/

void merke_markierte_slist(Slist slist)
{Slist node;
for_slist(slist,node)
   {merke_neu_markierten_knoten((Snode)dattrs(node));
   }end_for_slist(slist,node)
}

/********************************************************************************/


void loesche_markierungen(void)
{Slist node;
 Markierte_knoten mnode;
for_slist(markierte_knoten,node)
      {mnode=(Markierte_knoten)(dattrs(node));
       node_set(graphed_node(mnode->snode),  NODE_TYPE,mnode->old_type,0);
       free(mnode);
      }end_for_slist(markierte_knoten,node)
free_slist(markierte_knoten);
markierte_knoten=empty_slist;
}


/********************************************************************************/
void keine_markierung(void)
{markierte_knoten=empty_slist;}


/********************************************************************************/

void leere_markierte_knotenliste(void)
{free_slist(markierte_knoten);
markierte_knoten=empty_slist;
}



/********************************************************************************/
/********************************************************************************/
/*                         Markiere Knoteninhalt                                */
/********************************************************************************/
/*				       				                */
/* Parameter :  Sgraph_proc_info                                                */
/*				       				                */
/* Rueckgabeparameter :    void			    				*/
/*                                                                              */
/********************************************************************************/
/*  Funktion :									*/
/*        die urspruenglichen Knotentypen werden zuerst wieder hergatellt und   */
/*	  anschliessend der */
/********************************************************************************/

void my_sgraph_click_event_func(Sgraph_proc_info info, Sgraph_event_proc_info uev_info, char **event)
{Snode snode,snode2;
 Slist slist=empty_slist;

 if((info->selected==SGRAPH_SELECTED_SNODE)&&
                     (info->sgraph!=nil)&&(info->selection.snode!=nil))
   {if (event!=NULL)   /* Knoten wurde angeklickt*/
        {mpruefe_sgraph_auf_aenderung(info);
         if (errorflag_zusammenhang_gegeben)
              {error("Graphen wurden veraendert\n");
	       uev_info->do_default_action=FALSE;
		return;}
        }

    snode=info->selection.snode;

    /* urspruengliche Knotentypen setzen */
    {loesche_markierungen();}

    /* aktuellen Knoten auf 'my_node_type' setzen **/
    merke_neu_markierten_knoten(snode);
    node_set(graphed_node(snode),  NODE_TYPE,my_node_type,0);

    /*** unterscheide ob aktueller Knoten in Tree-dec oder im Originalgraph ***/
    if(strcmp(info->sgraph->label,"tree-decomposition")==0)

        {/*** Fuer Baumzerlegungs graph ***/
        /* message("Knoteninhalt:");
         printinfo2(snode->attrs.value.data);
         message("\n");*/
        merke_markierte_slist((Slist)dattrs(snode));
         group_set(create_graphed_group_from_slist((Slist)dattrs(snode)),
                                  NODE_TYPE,my_node_type,0);
        }
    else 
        {/*** Fuer Originalgraph ***/
	 for_all_nodes((Sgraph)info->sgraph->attrs.value.data,snode2)
           {if(contains_slist_element((Slist)snode2->attrs.value.data,
                                                         make_dattr(snode)))
              {slist=anhaengen(slist,snode2);}
           }end_for_all_nodes((Sgraph)info->sgraph->attrs.value.data,snode2)
        merke_markierte_slist(slist);
         group_set(create_graphed_group_from_slist(slist),
                               NODE_TYPE,my_node_type,0);
        }


    force_repainting();
   }
 /*info->new_selection.snode=((Sgraph)(info->sgraph->attrs.value.data))->nodes;
 info->selected=SGRAPH_SELECTED_SNODE;
 info->no_changes=false;*/ 
 if(event!=NULL)   
       {uev_info->do_default_action=FALSE;}
  return;
}

/*******************************************************************************/
/*                     loesche alle markeiten Knoten                           */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */
/*       die urspruenglichen Knotentypen werden mit `setze_original_nodetypes' */
/*	 wieder gesetzt und der benoetigte Speicher freigegeben.	       */
/*******************************************************************************/

void markierungen_loeschen(Sgraph_proc_info info)
{
 if (info->sgraph==nil)
   {error("Bitte erst einen Knoten anklicken\n");}

 else {
       setze_original_nodetypes(info);

       info->recompute=true;       
       knotentypen_veraendert=false;
       force_repainting();
       mein_free(original_node_type_origi_sgraph);
       }
}


/*******************************************************************************/
/*                     rufe 'markiere Knoteninhalte auf                        */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */
/*       die urspruenglichen Knotentypen werden mit `setze_original_nodetypes' */
/*	 wieder gesetzt und der benoetigte Speicher freigegeben.	       */
/*******************************************************************************/

void clique_anzeigen(Sgraph_proc_info info)
{
if(info->selected!=SGRAPH_SELECTED_SNODE)
    {error("no node selected\n");}
 else
    {my_sgraph_click_event_func(info,nil,nil);
    }
}


/*******************************************************************************/
/*        durchlaufe den Graphen und markeire jeweils die Knoteninhalte        */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */
/*      markiert den selektierten Knoten mit 'my_sgraph_click_event_func'      */
/*      und selektiere den naechsren Knoten    				       */
/*******************************************************************************/

void durchlaufe_graph(Sgraph_proc_info info)
{Snode snode=info->selection.snode;
 Snode snode2;

 if(info->selected!=SGRAPH_SELECTED_SNODE)
    {error("no node selected\n");}
 else{
      groesse_graph=anzahl_nodes(info->sgraph);
      my_sgraph_click_event_func(info,NULL,NULL);
      for_all_nodes(info->sgraph,snode2)
           {if( (snode->nr+1==snode2->nr) && (groesse_graph > snode->nr) )
               {break;}
            if( (snode2->nr==1) && (groesse_graph == snode->nr) )
               {break;}
           }end_for_all_nodes(info->sgraph,snode2)
     info->new_selection.snode=snode2;
     my_sgraph_click_event_func(info,NULL,NULL);
     info->new_selected=SGRAPH_SELECTED_SNODE;}
}


/**********************************************************************/
/*                                                                    */
/*         =================================================          */
/*              !    Wurzel des Baumes verschieben   !                */
/*         =================================================          */
/*                                                                    */
/**********************************************************************/
/*				                                      */
/*  Hier befinden sich verschiedene Prozeduren die erlauben die Wurzel*/
/*  des Garphen zu verschieben.					      */
/*				                                      */
/**********************************************************************/

/*******************************************************************************/
/*                      Kanten des Baumes umbauen                              */
/*******************************************************************************/
/*								               */
/*								               */
/* Parameter :  Snode snode    : aktueller Knoten                              */
/*              Snode oldsnode : letzter besuchter Knoten		       */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Verfahren:     Dfs                                                          */
/*******************************************************************************/


void tree_umbauen(Snode snode, Snode oldsnode)
{
Sedge sedge;
Slist slist,element;
slist=empty_slist;

for_sourcelist(snode,sedge)
   {if(sedge->tnode!=oldsnode)
        {tree_umbauen(sedge->tnode,snode);}
   }end_for_sourcelist(snode,sedge)
for_targetlist(snode,sedge)
   {if(sedge->snode!=oldsnode)
        {slist=anhaengen(slist,sedge);
         tree_umbauen(sedge->snode,snode);}
   }end_for_targetlist(snode,sedge)
 for_slist(slist,element)
        { make_edge(snode,eattrs(element)->snode,empty_attr);
         remove_edge(eattrs(element));
        } end_for_slist(slist,element)
free_slist(slist);
}

/*******************************************************************************/
/*                             Wurzel == Snode                                 */
/*******************************************************************************/
/*								               */
/* Parameter :  Snode wurzel  : Die neue Wurzel des Baumes                     */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*									       */
/*******************************************************************************/
/* Verfahren:                                                                  */
/*   Snode enthaelt denjenigen Knoten des Baumes der die neue Wurzel werden    */
/*   soll. Rekusoionsstart von `tree_umbauen'				       */
/*******************************************************************************/


void baum_ausgleichen(Snode wurzel)
{ 
 tree_umbauen(wurzel,wurzel);
}


/*******************************************************************************/
/*           Aufruf von 'baum_ausgleichen' fuer info-Strucktur                 */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info:	neue Wurzel muss selektiert sein       */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* fuer Graph uebergabe durch `call_sgraph_proc'. Derjenige Knoten der 	       */
/* Wurzel werden soll, muss selektiert sein.				       */
/*******************************************************************************/

void wurzel_aendern(Sgraph_proc_info info)
{if(info->selected!=SGRAPH_SELECTED_SNODE)
    {error("no node selected\n");}
 else if(strcmp(info->sgraph->label,"tree-decomposition")!=0)
            {error("Bitte Tree-decomposition anklicken\n");return;}
 else
    {baum_ausgleichen(info->selection.snode);}
}


/*******************************************************************************/
/*           Durchlaufe den Baum (DFS) fuer   `wurzel_mitte'                   */
/*******************************************************************************/
/*								               */
/* Parameter :  Snode snode    : aktueller Knoten                              */
/*              Snode oldsnode : letzter besuchter Knoten		       */
/*	        int tiefe      : Rekursionstiefe und Entfernung des aktuellen  */
/*				 Knotens vom Startknoten		       */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Besonderheit :						       */
/* tree muss auch gegen Kantenrichtung durchlaufen werden koennen da   */
/*  start nicht immer dei Wurzel des Trees ist.                        */
/***********************************************************************/

void traverse_tree(Snode snode, int tiefe, Snode oldsnode)
{
Sedge sedge;
snode->attrs=make_attr(ATTR_FLAGS,tiefe);

for_sourcelist(snode,sedge)
   {if(sedge->tnode!=oldsnode)
        {traverse_tree(sedge->tnode,tiefe+1,snode);
         max_tiefe=maximum(max_tiefe,tiefe+1);}
   }end_for_sourcelist(snode,sedge)
for_targetlist(snode,sedge)
   {if(sedge->snode!=oldsnode)
        {traverse_tree(sedge->snode,tiefe+1,snode);
         max_tiefe=maximum(max_tiefe,tiefe+1);}
   }end_for_targetlist(snode,sedge)
}


/*******************************************************************************/
/*           finde Mitte des Baumes (== mitte des laengsten Pfades)            */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */
/*     der Baum wird zweimal durchlaufen. Beim esten durchlauf wird der vom    */
/*     Startknoten entfernteste Knoten gefunden und somit ein Ende des         */
/*     laengsten Pfades. Anschliessend wird der Baum von diesem Ende durch-    */
/*     laufen und das zweite Ende entdeckt. 				       */
/*     Die laenge der Entfernung wird durch zwei geteilt und mit dieseer       */
/*     information die Mitte des Pfades gefunden.			       */
/*     Da fuer den Algorithmus die Attributfelder benoetigt werden, werden sie */
/*     zuerst in ein Feld gerettet und am ende wieder zurueckgeschrieben       */
/*   Zeitkomplexitaet :O[Knotenzahl]					       */
/*******************************************************************************/


void wurzel_mitte(Sgraph_proc_info info)
{Attributes *attrib;
 Snode snode,ende1,ende2,wurzel=(Snode)NULL;
 int i=0;
 int wurzeltiefe;
 Sgraph sgraph=info->sgraph;
 Menge schnitt,mitte_von_ende1=new_menge();
 Menge mitte_von_ende2=new_menge();

 if(strcmp(info->sgraph->label,"tree-decomposition")!=0)
    {error("Bitte Tree-decomposition anklicken\n");return;}

if(sgraph->nodes->suc!=sgraph->nodes)  /* mehr als einen Knoten   */
{
/**** Atribute in Feld retten, da Routine die attrs.value.integer felder benoetigt *****/ 
 attrib=(Attributes*)mein_malloc(sizeof(Attributes*)*(anzahl_nodes(info->sgraph)+1));
 for_all_nodes(info->sgraph,snode)
   {attrib[i++]=snode->attrs;}
    end_for_all_nodes(info->sgraph,snode)

/******** eigentlicher Algorithmus **********************/
 
 init_sgraph(sgraph);
 max_tiefe=0;
 /******************** berechne erstes Ende   */
 traverse_tree(sgraph->nodes,1,sgraph->nodes);
 for_all_nodes(sgraph,ende1)
   {if (max_tiefe==ende1->attrs.value.integer)
        {break;}
    }end_for_all_nodes(sgraph,ende1)
 
 traverse_tree(ende1,1,ende1);
 /***************** finde anderes Ende eines laengsten Pfades   */
 wurzeltiefe=max_tiefe/2+1;    /* Wurzel liegt genau zwischen beiden Enden   */ 

for_all_nodes(sgraph,snode)
   {if (max_tiefe==snode->attrs.value.integer)
        {ende2=snode;}
    if (wurzeltiefe == snode->attrs.value.integer)
          {mitte_von_ende1=add_to_menge(mitte_von_ende1,snode);} 
    }end_for_all_nodes(sgraph,snode)

 /****************************** markiere Tiefe von 2.Ende   */
 traverse_tree(ende2,1,ende2);

 for_all_nodes(sgraph,snode)
   {if ( ( (int) ( ((float)(max_tiefe)) /2+.6 ))==snode->attrs.value.integer )
        {mitte_von_ende2=add_to_menge(mitte_von_ende2,snode);}
    }end_for_all_nodes(sgraph,snode)
 
 schnitt=mengenschnitt(mitte_von_ende1,mitte_von_ende2);
 for_menge(schnitt,snode)
           {break;  /*kann ohnehin nur ein Element existieren   */
     }end_for_menge(schnitt,snode)
 wurzel=snode;

  tree_umbauen(wurzel,wurzel);
mein_free(mitte_von_ende2);
mein_free(mitte_von_ende1);
/*********** alte Attribute wieder herstellen *******************/
 i=0;
 for_all_nodes(info->sgraph,snode)
   {snode->attrs=attrib[i++];}
    end_for_all_nodes(info->sgraph,snode)
 mein_free(attrib);
}}

/**********************************************************************/
/*                                                                    */
/*         =================================================          */
/*                 !      Label nodes Prozeduren   !                  */
/*         =================================================          */
/*                                                                    */
/**********************************************************************/
/*******************************************************************************/
/*           Label Nodes der Baumzerlegung mit den Knoteninhalten              */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgaph sgraph : urspruenglicher Input-Sgraph                    */
/*              Snode snode  : zu labelnder Snode                              */
/*              Menge menge  : Menge des Knoteninhalts			       */
/*                                                                             */
/* Rueckgabeparameter :   Slist die sorierte Liste                             */
/*                                                                             */
/*******************************************************************************/
/* Funktion  :                                                                 */
/*	  der Snode wird mit den Einzelnen labeln der Knoten des sgraphen, die */
/*	 in der Menge enthalten sind gelabelt. Die einelnen Labels werden      */
/*       wenn sie in der selben Zeile stehen duch komma getrennt sonst nur     */
/*       durch ein '\n'.					               */
/*       Das Format wird ungefaehr durch die Wurzel der gesamtstringlaenge     */
/*       betimmt.							       */
/*******************************************************************************/


void my_set_nodelabel(Sgraph sgraph, Snode snode, Menge clique)
{
int laenge=0;
int sqrtlaenge,slistsize=0;
char *label,*nodelabel;
bool erstes_zeichen=TRUE;
Snode snode2;

if(longlabel==TRUE)
{label=mein_malloc(sizeof(label)*max_laenge_label);
 for_menge(clique,snode2)
     {laenge+=minimum((int)strlen(snode2->label),max_laenge_label);
      slistsize+=1;}
   end_for_menge(clique,snode2);
 if(slistsize!=0)
  {sqrtlaenge=(int)sqrt((double)laenge+(double)slistsize-1);  /*zeilenlaenge*/
   nodelabel=mein_malloc(sizeof(label)*(laenge+slistsize-1));
   nodelabel=strcpy(nodelabel,"");
   laenge =0;
   for_menge(clique,snode2)
      {label=strncpy(label,snode2->label,max_laenge_label);
       if (laenge+((int)strlen(label)/2)>sqrtlaenge)
          {nodelabel=strcat(nodelabel,"\n");
           nodelabel=strncat(nodelabel,label,max_laenge_label);
           laenge=strlen(label)+1;
          }
       else
          {if (!(erstes_zeichen))
                {nodelabel=strcat(nodelabel,",");}
           nodelabel=strncat(nodelabel,label,max_laenge_label);
           laenge+=(int)strlen(label)+1;
           erstes_zeichen=FALSE;
          }
      }end_for_menge(clique,snode2);
   if((int)strlen(nodelabel)>max_laenge_label) 
        {nodelabel=strncpy(nodelabel,nodelabel,20);
         nodelabel=strcat(nodelabel,"\n ...");}
   set_nodelabel(snode,nodelabel);
   /*message("\n nodelabel:%s =  ",snode->label);printinfo2(attrsm(snode));*/
  } 
}
}

/*******************************************************************************/
/*           Label Nodes der Baumzerlegung mit den Knoteninhalten              */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgaph sgraph : urspruenglicher Input-Sgraph                    */
/*              Snode snode  : zu labelnder Snode                              */
/*              Slist slist  : Liste des Knoteninhalts			       */
/*                                                                             */
/* Rueckgabeparameter :   Slist die sorierte Liste                             */
/*                                                                             */
/*******************************************************************************/
/* Funktion  :                                                                 */
/*     wandelt die slit in eine Menge um und ruft `my_set_nodelabel' auf.      */
/*******************************************************************************/

void my_set_nodelabel2(Sgraph sgraph, Snode snode, Slist slist)
{my_set_nodelabel(sgraph,snode,baue_menge_aus_slist(slist));
}


/**********************************************************************/
/*                            label nodes                             */
/**********************************************************************/ 
/*								      */
/* Zu diesem Algorithmus gehoeren die prozeduren : aktualisiere_str   */
/*						   stringlaenge       */
/*						   label_nodes        */
/*								      */
/* Aufgabe : die Knoten eines Graphen sollen unterschiedlich gelabelt */
/*	     werden.						      */
/*								      */
/* Moeglichkeiten : label_nodes labelt die Knoten mit einer Folge von */
/*                  Zeichen. Die Folge ist fuer alle Knoten gleich    */
/*		    lang (zB Fuehrende Nullen werden nicht unter-     */
/*		    drueckt.)					      */
/*		    als zulaessige Zeichen koennen alle durchgehenden */
/*		    ASCII-Folgen verwendet werden (zB a-z,A-Z,0-9,0-4)*/
/*		    das erste und das lezte Zeichen der Folge wird    */
/*		    durch die globale Varliablen 		      */
/*	            `erstes_zulaessiges_zeichen_graph'		      */
/*		    und `leztes_zulaessiges_zeichen_graph' uebergeben */
/*                  (fuer allgemeine Graphen)			      */
/*		    auf diese Weise koennen alle Zahlensystem ver-    */
/*		    wendet werden.				      */
/**********************************************************************/

/*******************************************************************************/
/*				aktualisiere_str			       */
/*******************************************************************************/
/*                                                                             */
/* Parameter :  int i         : Stelle des zu aendernden Zeichens              */
/*              char *string  : Zeiger auf den String des aktuellen knotens    */
/*                                                                             */
/* Rueckgabeparameter :   void                                                 */ 
/*                                                                             */
/*******************************************************************************/
/* Funktion : aendere ein Zeichen im String                                    */
/*******************************************************************************/
/* Vorgehnswiese :							       */
/*		Der String wird um eins hochgezaehlt, dh das letzte Zeichen    */
/*	        wird um eins erhoet. Falls das hinterste Zeichen schon gleich  */
/*		dem letzten zulaessigen Zeichen ist, wird dieses Zeichen auf   */
/*		das erste Zulaessige Zeichen gesetzt und das Zeichen eine      */
/*		Position weiter vorne um eins erhoeht. usw.		       */
/*******************************************************************************/

void aktualisiere_str(int i, char *string)
{if(string[i]==letztes_zulaessiges_zeichen)
    {string[i]=erstes_zulaessiges_zeichen;
     aktualisiere_str(i-1,string);}
 else
    {string[i]+=1;}
}


/*******************************************************************************/
/*                Betimme die notwendige Stringlaenge                          */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgaph sgraph : aktueller Sgraph (zur betimmung der Knotenzahl) */
/*                                                                             */
/* Rueckgabeparameter :   int : die fuer die Labels benoetigte Laenge          */  
/*                                                                             */
/*******************************************************************************/

int stringlaenge(Sgraph sgraph)
{int i=0;
 float n=anzahl_nodes(sgraph);

 while(n>(((int)(letztes_zulaessiges_zeichen))-
                                ((int)(erstes_zulaessiges_zeichen))+1))
   {i++;
    n=n/(((int)(letztes_zulaessiges_zeichen))-
                                ((int)(erstes_zulaessiges_zeichen))+1);}
return i+1;
}


/*******************************************************************************/
/*              label nodes - Hauptroutine		                       */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/

void label_nodes(Sgraph_proc_info info)
{ Sgraph sgraph=info->sgraph;
 Snode snode;
 int laenge_string,i;
 char *string;

if ( (info->sgraph->label !=nil) && 
               (strcmp(info->sgraph->label ,"tree-decomposition")==0))
     {erstes_zulaessiges_zeichen=erstes_zulaessiges_zeichen_decomp;
      letztes_zulaessiges_zeichen=letztes_zulaessiges_zeichen_decomp;}
else {erstes_zulaessiges_zeichen=erstes_zulaessiges_zeichen_graph;
      letztes_zulaessiges_zeichen=letztes_zulaessiges_zeichen_graph;}

string=(char*)mein_malloc(sizeof(char*)*(stringlaenge(sgraph)+1));
laenge_string=stringlaenge(sgraph);

/*** gesamten Sring mit ersten Zeichen auffuellen ***/
for (i=0;i<laenge_string;i++)
    {string[i]=erstes_zulaessiges_zeichen; }
string[laenge_string]='\0';

/*i=laenge_string-1; */

for_all_nodes(sgraph,snode)
   {set_nodelabel(snode,strsave(string));
    /**** string um eins hochzaehlen ****/
    aktualisiere_str(laenge_string-1,string);
   }end_for_all_nodes(sgraph,snode)
info->recompute=TRUE;
info->new_selected=SGRAPH_SELECTED_NONE;
info->new_sgraph=sgraph;
force_repainting();
}


/*******************************************************************************/
/*                  Aufruf fuer label_nodes      	                       */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */
/*        Da die Prozedur `label_nodes' sowohl fuer die baumzerlegung als      */
/*	  auch fuer sonstige Garphen verwendet werden kann und wird muss       */
/*	  bei Aufruf aus dem Mainwindow ueberprueft werden, dass von dort      */
/*	  nicht die Baumzerlegung angesprochen werden kann.		       */ 
/*******************************************************************************/

void label_knoten(Sgraph_proc_info info)
{if ( (info->sgraph->label !=nil) && 
               (strcmp(info->sgraph->label ,"tree-decomposition")==0))
  {error("sorry, but this algorithm does not \n   work on Tree-Decompositions\n");}
 else {label_nodes(info);}
}



/**********************************************************************/
/*                                                                    */
/*         =================================================          */
/*                 !      Label nodes Prozeduren   !                  */
/*         =================================================          */
/*                                                                    */
/**********************************************************************/
/***********************************************************************/
/*                 change_label                                        */
/***********************************************************************/
/*******************************************************************************/
/*         Veraendere die Labels der Baumzerlegung	                       */
/*******************************************************************************/
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */
/*        abhaengig von der globalen Varible `longlabel' werden die Knoten     */
/*	  entweder mit den Labels des Knotneninhalts gelabelt (lonlabel=1)     */
/*	  oder die Knoten erhalten eine Durchnummerierung durch `label_nodes'  */
/*******************************************************************************/

void change_label(Sgraph_proc_info info)
{Menge menge;
 Snode snode;
 Sgraph sgraph=info->sgraph;

 if ((sgraph->label!=nil)&&(strcmp(sgraph->label,"tree-decomposition")!=0))
               {error("** Bitte zuerst Tree-Decomp anklicken **\n");
                errorflag=true;}
 else {
     init_sgraph_fuer_manipulate_treedec((Sgraph)sgraph->attrs.value.data);
     if (longlabel==FALSE)
         {
          label_nodes(info);}
     else 
         {
          for_all_nodes(sgraph,snode)
               {menge =baue_menge_aus_slist((Slist)snode->attrs.value.data);
              /*  message("\n Menge: "); printmenge(sgraph,menge);
                message("\n Slist: "); printinfo2(snode->attrs.value.data);    */    
                my_set_nodelabel((Sgraph)sgraph->attrs.value.data,snode,menge);
               }end_for_all_nodes(sgraph,snode)
          info->new_selected=SGRAPH_SELECTED_NONE;
          info->repaint=true;
          }
     errorflag=false;
     }
}



/**********************************************************************/
/*                                                                    */
/*         =================================================          */
/*               !     Baumausgabe Hilfsprozeduren   !                */
/*         =================================================          */
/*                                                                    */
/**********************************************************************/
/**********************************************************************/
/*******************************************************************************/
/*         Passe die groesse der Knoten an die Labels an                       */
/*******************************************************************************/
/*								               */
/* benoetigtee Prozeduren : fit_node_to_text		                       */
/*			    ein Layoutalgorithmus des Graphed	               */
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Funktion :   die knoten des Graphen muessen vor dem Aufruf von 	       */
/*	        `fit_node_to_text' selektiert werden.			       */
/*******************************************************************************/

void knoten_anpassen(void)
{Menu menu;
Menu_item menu_item;
dispatch_user_action(SELECT_GRAPH_OF_SELECTION);
fit_nodes_to_text(menu,menu_item);
}

/*******************************************************************************/
/*             zeichne Tree-decomposition		                       */
/*******************************************************************************/
/*								               */
/* benoetigtee Prozeduren :      call_tree_layout_walker(info);                     */
/*				 ein Layoutalgorithmus des Graphed	       */
/*								               */
/* Parameter :  Sgraph_proc_info info;		                               */
/*                                                                             */
/* Rueckgabeparameter :   void  (die Koordinaten des Graphen werden veraendert */ 
/*                                                                             */
/*******************************************************************************/
/* Funktion :  Die Attributsfelder werden in einem lokaelen Feld zwische-      */
/*	       gespeichert, da `call_tree_layout_walker' die Attributsfelder ver-   */
/*	       aendert.					                       */ 
/*******************************************************************************/

void baum_zeichnen(Sgraph_proc_info info)
{Attributes *attrib;
 Snode snode;
 int i=0;

attrib=(Attributes*)mein_malloc(sizeof(Attributes*)*(anzahl_nodes(info->sgraph)+1));
 for_all_nodes(info->sgraph,snode)
   {attrib[i++]=snode->attrs;}
    end_for_all_nodes(info->sgraph,snode)

 call_tree_layout_walker(info);
 info->recenter=TRUE;

 i=0;
 for_all_nodes(info->sgraph,snode)
   {snode->attrs=attrib[i++];}
    end_for_all_nodes(info->sgraph,snode)
mein_free(attrib);
}


/*******************************************************************************/
/*******************************************************************************/


void speichere_sgraph_fuer_vergleich(Sgraph sgraph)
{}

/*
 Snode snode;
 int i=0;

  for_all_nodes(sgraph,snode)
        {i++;  
	}end_for_all_nodes(sgraph,snode)

 if (snodefeld!=(Snode*)NULL)    {mein_free(snodefeld);}
 snodefeld=(Snode*)mein_malloc(sizeof(Snode*)*i);
 ursprungsgraph=sgraph;
 groesse_ursprungsgraph=i;

 i=0;
 for_all_nodes(sgraph,snode)
          {snodefeld[i++]=snode;
          }end_for_all_nodes(sgraph,snode)

 sgraph=(Sgraph)sgraph->attrs.value.data;
 i=0; 
  for_all_nodes(sgraph,snode)
        {i++;  
	}end_for_all_nodes(sgraph,snode)

 if (snodefeld_tree_dec!=(Snode*)NULL)    {mein_free(snodefeld_tree_dec);}
 snodefeld_tree_dec=(Snode*)mein_malloc(sizeof(Snode*)*i);

 tree_decomposition_graph=sgraph;
 groesse_tree_decomposition_graph=i;

 i=0;
 for_all_nodes(sgraph,snode)
          {snodefeld_tree_dec[i++]=snode;
          }end_for_all_nodes(sgraph,snode)
errorflag_zusammenhang_gegeben=false;
}

*/
/*******************************************************************************/
/*******************************************************************************/

void mpruefe_sgraph_auf_aenderung(Sgraph_proc_info info)
{}
/*Sgraph sgraph;
 Snode snode;
 int i=0;
 char *text =mein_malloc(sizeof(char*)*20);

 if(errorflag_zusammenhang_gegeben==true) {return;}

 if(strcmp(info->sgraph->label,"tree-decomposition")==0)
   {sgraph=(Sgraph)dattrs(info->sgraph);
   }
 else
   {sgraph=info->sgraph;
   }

 if (sgraph!=ursprungsgraph)                  
               { errorflag_zusammenhang_gegeben=true;return;}
 if ((Sgraph)sgraph->attrs.value.data!=tree_decomposition_graph) 
               { errorflag_zusammenhang_gegeben=true;return;}
 if ((Sgraph)tree_decomposition_graph->attrs.value.data!=sgraph)  
               { errorflag_zusammenhang_gegeben=true;return;}

 for_all_nodes(sgraph,snode)
          {if( (graphed_node(snode)==(Graphed_node)NULL) || (snodefeld[i++]!=snode) )
                  {
		   errorflag_zusammenhang_gegeben=true;
	           return ;
		  }
          }end_for_all_nodes(sgraph,snode)
*//* if (i!=groesse_ursprungsgraph)  {errorflag_zusammenhang_gegeben=true;return;}*/
/*
 
  i=0;
  for_all_nodes(tree_decomposition_graph,snode)
          {if( (snodefeld_tree_dec[i++]!=snode) 
			|| (graphed_node(snode)==(Graphed_node)NULL) )
                  {
		   errorflag_zusammenhang_gegeben=true;
	           return ;
		  }
          }end_for_all_nodes(tree_decomposition_graph,snode)
*//* if (i!=groesse_tree_decomposition_graph)  
	{errorflag_zusammenhang_gegeben=true;return;}*/
/*
 errorflag_zusammenhang_gegeben=false;
}
*/


/*******************************************************************************/
void pruefe_sgraph_auf_aenderung_aufruf(void)
{/*call_sgraph_proc(mpruefe_sgraph_auf_aenderung);*/}

/*******************************************************************************/
/*******************************************************************************/
/*******************************************************************************/
/*******************************************************************************/
/*******************************************************************************/
/*******************************************************************************/
/*******************************************************************************/
/*******************************************************************************/
