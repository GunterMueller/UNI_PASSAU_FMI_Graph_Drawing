/*****************************************************************************/
/*                                                                           */
/*          N I C E  -  T R E E  -  D E C O M P O S I T I O N                */
/*                                                                           */
/* Modul        : nice-tree-dec.c					     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/

#include "nice_tree_dec.h"

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*         void       nice_tree_decomp   (Sgraph_proc_info)                  */
/*		      baut aus der aktuellen Baumzerlegung eine 	     */
/*		      'nice-tree-decomposition'.			     */
/*		      'nice-tree-decomposition' wird von Bodlaender in	     */
/*    		      defineiert.					     */
/*		      eine 'nice...' ist eine Baumzerlegung die folgeneden   */
/*		      Eigenschaften genuegt :   			     */
/*		      Jeder Knoten hat hoechstens zwei Nachfolger.	     */
/*		      hat zwei Nachfolger, so sind diese identisch mit ihm   */
/*		      selbst.						     */
/*		      Besitzt er einen Nachfolger so unterscheiden sich beide*/
/*		      in genau einem Label.				     */	
/*								             */
/*****************************************************************************/


/*****************************************************************************/
/*						                             */
/*		LOKALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*   Unterprozeduren zu nice_tree_decomp :				     */
/*								             */
/*         Sgraph     erweitere_baum             (Sgraph,Snode)              */
/*								             */
/*         void       zwischenknoten_einfuegen   (Sgraph,Snode,Snode)        */
/*								             */
/*         Snode      finde_wurzel_des_Baumes    (Sgraph)                    */
/*								             */
/*         void       entferne_doppelte_knoten   (Sgraph)                    */
/*								             */
/*****************************************************************************/

/*****************************************************************************/
/*								             */
/*      =================================================                    */
/*          !      Nice-tree-decomposition          ! 			     */
/*      =================================================                    */
/*								             */
/*****************************************************************************/
/*****************************************************************************/
/*                       Fuege Zwischenknoten ein                            */
/*****************************************************************************/
/*								             */
/* aufrufende Prozedur    : erweitere Baum	                             */
/*								             */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                              */
/*              Snode snode1 : Der ober von zwei aufeinanderfolgenden Knoten */
/*              Snode snode2 : der untere Knoten                             */
/*                             Clique                                        */
/*                                                                           */
/* Rueckgabeparameter :   void			                             */ /*                                                                           */
/*****************************************************************************/
/* Verfahren:                                                                */
/*      Es wird die disjunkte Teilmenge der Knoteninhalte gebildet und die   */
/*   	Inhalt anschliesend induktiv abgebaut. Dabei wird jeweils ein neuer  */
/*      Knoten generiert.			                             */
/*                                                                           */
/*****************************************************************************/

void zwischenknoten_einfuegen(Sgraph sgraph, Snode snode1, Snode snode2)
{Slist disjunkte_vereinigung,element;
 bool knoten_eingefuegt=false;
 Snode new_node1,new_node2;
 Slist aktuelle_slist1,aktuelle_slist2;
 Sgraph oldsgraph=(Sgraph) sgraph->attrs.value.data;

 /*message("\nzwischen : ");printinfo2(snode1->attrs.value.data);
 message("\n und: ");printinfo2(snode2->attrs.value.data);*/

 remove_edge(snode1->slist);
 new_node1=snode1;new_node2=snode2;
 disjunkte_vereinigung=copy_slist((Slist)dattrs(snode1));
 disjunkte_vereinigung=add_slists_disjoint(disjunkte_vereinigung,
                                                           lattrs(snode2));
 aktuelle_slist1=copy_slist(lattrs(snode1));
 aktuelle_slist2=copy_slist(lattrs(snode2));
 /*message("disj.Vereinigung: ");printinfo2(disjunkte_vereinigung);*/

 for_slist(disjunkte_vereinigung,element)
 
  {if(element->suc!=disjunkte_vereinigung)   /*letztes element uebergehen*/
  
     {if(contains_slist_element(lattrs(snode1),element->attrs))
           {aktuelle_slist1=subtract_from_slist(aktuelle_slist1,element->attrs);
            new_node1=make_node(sgraph,make_dattr(copy_slist(aktuelle_slist1)));
            my_set_nodelabel2(oldsgraph,new_node1,aktuelle_slist1);
            new_node1->x=50;new_node2->y=50;
            make_edge(snode1,new_node1,empty_attr);
            snode1=new_node1;
            knoten_eingefuegt=true;
           }
 
       else
           {aktuelle_slist2=subtract_from_slist(aktuelle_slist2,element->attrs);
            new_node2=make_node(sgraph,make_dattr(copy_slist(aktuelle_slist2)));
            my_set_nodelabel2(oldsgraph,new_node2,aktuelle_slist2);
            new_node2->x=50;new_node2->y=50;
            make_edge(new_node2,snode2,empty_attr);
            snode2=new_node2;
            knoten_eingefuegt=true;
           }

       }
    }end_for_slist(disjunkte_vereinigung,element)

 /* beide teile zusammenfuegen           */
 make_edge(snode1,snode2,empty_attr);
 free_slist(aktuelle_slist1);
 free_slist(aktuelle_slist2);
 free_slist(disjunkte_vereinigung);
}

/*****************************************************************************/
/*                erweitere Baum   (Bedingung 1)                             */
/*****************************************************************************/
/*								             */
/* aufrufende Prozedur    : nice_tree_decomp                                 */
/*								             */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                              */
/*              Snode snode  : aktueller Snode                               */
/*                                                                           */
/* Rueckgabeparameter : Sgraph :  manipulierter Graph                        */  /*                                                                           */
/*****************************************************************************/
/* Verfahren:                                                                */
/*         Der Baum wird rekursiv durchlaufen (DFS)			     */
/*         besitzt der Knoten keinen Nachfolger so ist er ein Blatt	     */
/*	   besitzt er einen Nachfolger so rufe 'zwischenknoten_einfuegen'    */
/*          zur weitern behandlung auf.					     */
/*         besitzt er n>1 Nachfolger so baue einen binaeren Teil- 	     */
/*	   baum aus identischen Knoten und n blaettern.			     */
/*****************************************************************************/

Sgraph erweitere_baum(Sgraph sgraph, Snode snode)
{
Snode new_snode,snode2,new_snode2;
Sedge sedge;
int i,anzahl_sons=0;
Slist slist,slist2,element;

if (snode->slist!= ((Sedge)(NULL)) )     /* Blatt?               */
      {if(snode->slist!=snode->slist->ssuc)  /* mehr als einen Sohn ?*/
          {/* m indesten zwei Sohene*/
           slist=empty_slist;
           slist2=empty_slist;
           /*alle Kanten zu Soehnen merken und zaehlen*/
           for_sourcelist(snode,sedge)
                             {anzahl_sons++;
                  slist=anhaengen(slist,sedge);
                 }end_for_sourcelist(snode,sedge)
           /*entferne alle Kanten zu Nachfolgern und merke Nachfolger.*/
           for_slist(slist,element)
                 {
                  remove_edge((Sedge)(element->attrs.value.data));
                  slist2=anhaengen(slist2,eattrs(element)->tnode);
                 }end_for_slist(slist,element)
           
           slist=empty_slist;
           slist=anhaengen(slist,snode);
           /* Teilbaum nach obiger Spezifikation aufbauen */
           for(i=1;i<anzahl_sons;i++)
                {snode2=sattrs(slist);
                 new_snode=make_node(sgraph,snode->attrs);
                 new_snode2=make_node(sgraph,snode->attrs);    
                 set_nodelabel(new_snode,strsave(snode->label));
                 new_snode->x=50;new_snode->y=50;
                 make_edge(snode2,new_snode,empty_attr);
                 set_nodelabel(new_snode2,strsave(snode->label));
                 new_snode2->x=50;new_snode2->y=50;
                 make_edge(snode2,new_snode2,empty_attr);

                 slist=subtract_immediately_from_slist(slist,slist);
                 slist=anhaengen(slist,new_snode);                 
                 slist=anhaengen(slist,new_snode2);                 

                }/*end  for()*/
 
          /* urspruengliche Soehne mit den Blaettern verbinden */
          for_slist( slist,element)
                {make_edge(sattrs(element),sattrs(slist2),empty_attr);
                 slist2=slist2->suc;
                }end_for_slist(slist,element)


          /* naechster Knoten (Sohn) behandeln */
          for_slist(slist,element)
                {erweitere_baum(sgraph,sattrs(element));  
                }end_for_slist(slist,element)

         }
       else   /*geanau ein Sohn */
           {snode2=snode->slist->tnode;
            zwischenknoten_einfuegen(sgraph,snode,snode2);
            erweitere_baum(sgraph,snode2);
           }
      }
return sgraph;
}


/*****************************************************************************/
/*                       suche Wurzel des Baumes                             */
/*****************************************************************************/
/*								             */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                              */
/*								             */
/* Rueckgabeparameter :   Snode : Wurzel des Baumes                          */  /*                                                                           */
/*****************************************************************************/
/* Verfahren:								     */
/*      Derjenige Knoten der keinen Vorgaenger besitzt muss die Wurzel sein  */
/*****************************************************************************/

Snode finde_wurzel_des_Baumes(Sgraph sgraph)
{Snode snode=sgraph->nodes;

  while (snode->tlist!=(Sedge)NULL)
        {snode=snode->tlist->snode;}
return snode;}

/*****************************************************************************/
/*              entferne  identische aufeinanderfolgende Knoten              */
/*****************************************************************************/
/*								             */
/* aufrufende Prozedur    : nice_tree_decomp	                             */
/*								             */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                              */
/*                                                                           */
/* Rueckgabeparameter : void  / die Sgraph Strucktur wird direkt geaendert.  */
/*                                                                           */
/*****************************************************************************/
/* Funktion:                                                                 */
/*      iterativer Aufruf der nice tree-decomposition wuerde ohne diese      */
/*      Prozedur zu einer staendigen Vergroeserung des Baumes fuehren.       */
/*****************************************************************************/

void entferne_doppelte_knoten(Sgraph sgraph)
{Snode snode;
  
 for_all_nodes(sgraph,snode)
    {if (   (snode->tlist != ((Sedge)(NULL)) )        /* mind. ein Vorgaenger*/
             &&(snode->slist!=(Sedge)NULL)            /* mind. ein Nachfolger*/
             &&(((Sedge)(snode->slist))->ssuc == snode->slist) )/*genau einer*/
       {if(slists_identical((Slist)snode->attrs.value.data,
                                    (Slist)snode->slist->tnode->attrs.value.data))
          {make_edge(snode->tlist->snode,snode->slist->tnode,empty_attr);
           remove_node(snode);}
       }
    }end_for_all_nodes(sgraph,snode)
}

/*****************************************************************************/
/*               nice-tree-decomposition Hauptroutine                        */
/*****************************************************************************/
/*								             */
/* Parameter :  Sgaph_info_proc : aktueller Sgraph                           */
/*                                                                           */
/* Rueckgabeparameter :   void			                             */  /*****************************************************************************/

void nice_tree_decomp(Sgraph_proc_info info)
{Sgraph new_sgraph,sgraph;
 Snode wurzel;

 if(strcmp(info->sgraph->label,"tree-decomposition")!=0)
    {error("Please select Tree-decomposition first");return;}

    init_sgraph_fuer_manipulate_treedec((Sgraph)info->sgraph->attrs.value.data);
    sgraph=info->sgraph;
    wurzel=finde_wurzel_des_Baumes(sgraph);
    new_sgraph=erweitere_baum(sgraph,wurzel);
    entferne_doppelte_knoten(new_sgraph); /*entferne ueberfluessige Knoten*/
    info->recompute=TRUE;
    info->new_sgraph=new_sgraph;
    info->new_selected=SGRAPH_SELECTED_NONE;
}

