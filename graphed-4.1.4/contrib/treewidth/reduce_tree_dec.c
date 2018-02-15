/*****************************************************************************/
/*                                                                           */
/*          R E D U C E   T R E E  -  D E C O M P O S I T I O N              */
/*                                                                           */
/* Modul        : reduce-tree-dec.c					     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

#include "reduce_tree_dec.h"

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*         void       min_tree_dec       (Sgraph_proc_info)                  */
/*                    Dieser Algorithmus fasst die Label zweier		     */
/*	 	      aufeinanderfolgende Knoten zu einem einzigen zusammen, */
/*		      falls dies nicht die Baumweite erhoet.	 	     */
/*								             */
/*****************************************************************************/


/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*   Unterprozeduren zu min_tree_decomp :			             */
/*								             */
/*         void       bestimme_baumweite         (Sgraph)                    */
/*								             */
/*         Slist      reduce_tree_decomp         (Snode)                     */
/*								             */
/*****************************************************************************/

/*****************************************************************************/
/*              lokale variablen                                             */
/*****************************************************************************/

static int gueltige_baumweite;    /* enthaelt die aktuelle Baumweite         */

/*****************************************************************************/
/*								             */
/*      =================================================                    */
/*          !      Min-tree-decomposition          ! 			     */
/*      =================================================                    */
/*								             */
/*****************************************************************************/
/*****************************************************************************/
/*         sortiere slist nach groesse ihrer Mengenelemente                  */
/*****************************************************************************/
/*								             */
/* aufrufende Prozedur    : min-tree-dec	                             */
/*								             */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                              */
/*                                                                           */
/* Rueckgabeparameter :   void   die aktuelle baumweite wird an  	     */
/*                              'gueltige_baumweite' ubergeben.              */
/*                                                                           */
/*****************************************************************************/
/* Funktion:                                                                 */
/*       die Baumweite der aktuellen Baumzerlegung wird berechnet            */
/*           (der Groeste Knoten)					     */
/*****************************************************************************/

void bestimme_baumweite(Sgraph sgraph)
{Snode snode;

 for_all_nodes(sgraph,snode)
     {gueltige_baumweite=maximum(gueltige_baumweite
                                     ,size_of_slist((Slist)snode->attrs.value.data));
     }end_for_all_nodes(sgraph,snode)
}

/*****************************************************************************/
/*                   reduziere Tree-Decomposition                            */
/*****************************************************************************/
/*								             */
/* aufrufende Prozedur    : min-tree-dec	                             */
/*								             */
/* Parameter :  Snode snode  : Im rekursionsschritt aktueller Knoten         */
/*                                                                           */
/* Rueckgabeparameter : Slist : eine Liste von Knoten die entfernt werden    */
/*				sollen		                             */
/*                                                                           */
/*****************************************************************************/
/* Verfahren:                                                                */
/*     Der Baum wird von aus der Wurzel rekursiv durchluafen (DFS).	     */
/*     Wenn es moeglich ist Zwei aufeinanderfolgende Knoten zu Vereinigen    */
/*     ohne die Baumweite zu erhoehen, werden beide Knoten zu einem	     */
/*     zusammengefasst und der uebrige zum spaetern loeschen gespeichert.    */
/*     Anschliessend wird die Prozedur mit dem gleichen Knoten als Parasmeter*/
/*     nochmal aufgerufen fuer den Fall, dass ein weiterer Nachfolger eines  */
/*     von beiden veeinigten Knoten auch noch aufgenommen werden kann.	     */
/*                                                                           */
/*****************************************************************************/

Slist reduce_tree_decomp(Snode snode)
{
 Sedge sedge,sedge2;
 Sgraph sgraph=snode->graph;
 Sgraph originalsgraph=(Sgraph)sgraph->attrs.value.data;
 Slist copy_list;
 Slist zu_loeschend_knoten=empty_slist;
 Slist skante,neue_kanten=empty_slist;
  
 if (snode->slist!=(Sedge)NULL)          /* mind. ein Nachfolger*/     
   {for_sourcelist(snode,sedge)
      {if(strcmp(sedge->tnode->attrs.value.data,"geloescht")!=0)
          {
           copy_list=copy_slist((Slist)snode->attrs.value.data);
           if(gueltige_baumweite>=size_of_slist(add_slists(copy_list,
                                    (Slist)sedge->tnode->attrs.value.data)))
              {
               snode->attrs=make_dattr(add_slists((Slist)snode->attrs.value.data
                                             ,(Slist)sedge->tnode->attrs.value.data));
 
               snode->attrs=make_dattr(copy_list);
               my_set_nodelabel2(originalsgraph,snode,(Slist)snode->attrs.value.data);
          
               for_sourcelist(sedge->tnode,sedge2)
                  {neue_kanten=anhaengen(neue_kanten,sedge2->tnode);
                  }end_for_sourcelist(sedge->tnode,sedge2)
               sedge->tnode->attrs=make_dattr(strsave("geloescht"));
               zu_loeschend_knoten=anhaengen(zu_loeschend_knoten,sedge->tnode);

             }
         }
     }end_for_sourcelist(snode,sedge)
   for_slist(neue_kanten,skante)
       {make_edge(snode,(Snode)skante->attrs.value.data,empty_attr);
       }end_for_slist(neue_kanten,skante)
   if( neue_kanten!=empty_slist)
       {zu_loeschend_knoten=add_slists(zu_loeschend_knoten,
                                                   reduce_tree_decomp(snode));
       }
   for_sourcelist(snode,sedge)
       {if(strcmp(sedge->tnode->attrs.value.data,"geloescht")!=0)
            {zu_loeschend_knoten=add_slists(zu_loeschend_knoten,
                                           reduce_tree_decomp(sedge->tnode));
            }
       }end_for_sourcelist(snode,sedge)
   }
 return zu_loeschend_knoten;
}


/*****************************************************************************/
/*                        min-tree-dec Hauptroutine                          */
/*****************************************************************************/
/*								             */
/* Parameter :  Sgaph_info_proc : aktueller Sgraph                           */
/*                                                                           */
/* Rueckgabeparameter :   void			                             */  /*****************************************************************************/


void min_tree_dec(Sgraph_proc_info info)
{Slist zu_loeschend_knoten,sknoten;

 if(strcmp(info->sgraph->label,"tree-decomposition")!=0)
    {error("Bitte Tree-decomposition anklicken");return;}

 init_sgraph_fuer_manipulate_treedec(info->sgraph);

 gueltige_baumweite=0;

 /* betimme die aktuelle Baumweite*/
 bestimme_baumweite(info->sgraph);

 /* Aender die Knoteninhalte und merke zu loeschende Knoten */
 zu_loeschend_knoten=reduce_tree_decomp(finde_wurzel_des_Baumes(info->sgraph));

 /* Loesche diese Knoten */
 for_slist(zu_loeschend_knoten,sknoten)
      {remove_node((Snode)sknoten->attrs.value.data);
       }end_for_slist(zu_loeschend_knoten,sknoten)

 free_slist(zu_loeschend_knoten);
    info->recompute=TRUE;
    info->new_sgraph=info->sgraph;
    info->new_selected=SGRAPH_SELECTED_NONE;
}
