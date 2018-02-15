#include <math.h>

#include "untere_schranke.h"
#include "separatorheuristik.h"
#include "cliquen.h"

/*****************************************************************************/
/*                                                                           */
/*                    U N T E R E   S C H R A N K E N                        */
/*                                                                           */
/* Modul 	: untere_schranke.c					     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/*  Dieses Modul enthaelt zwei Berechnungsmethoden fuer untere Schranken     */
/*  der Baumweite von Graphen.						     */
/*                                                                           */
/*  int	 kantenbeschraenkung	(Sgraph)				     */
/*       Findet eine untere Shcranke ueber Kanten/Knotenverhaeltnis	     */
/*                                                                           */
/*  Die ueberigen Routinen berechnen einen Cliquenminor (beschrieben in	     */
/*  meiner Diplomarbeit.						     */
/*                                                                           */
/*  bool 	valenzbedingung		(Sgraph,Menge clique		     */
/*							,int max_cliquensize)*/
/*								             */
/*  int	finde_untere_schranke_durch_maxCliquenseparator (Sgraph)	     */
/*			berechne die untere Schranke der Baumweite durch     */
/*			CliquenSeparatorbedingungen (siehe hierzu Diplomarbeit) */
/*								             */
/*  Slist 	finde_max_cliquen_aus_dominanten			     */
/*					(Sgraph,Slist dominante_cliquenmenge)*/
/*								             */
/*  int finde_untere_schranke_durch_maxCliquenseparator_fuer_Cliquenheuristik*/
/*                                             (sgraph,dominante_cliquenmenge)*/
/*			wie oben, nur fuer den Fall, dass bereits alle do-   */
/*			minanten Cliquen bekannt sind.			     */
/*                                                                           */
/*****************************************************************************/




/******************************************************************************/
/*   ueber die Gleichung |E| <= k|V| - k(k+1)/2 laesst sich eine untere Grenze*/
/*   fuer k finden wenn |V| und |E| bekannt sind.                             */
/*  laesst sich umformen zu k >= n-.5 -sqrt((n-.5)^2-2*|E|)                   */
/******************************************************************************/
/*  der minimale Grad des Graphens stellt eine weitere untere Schranke dar    */
/******************************************************************************/
/*   Hier wir das maximum der beiden Werte zurueckgegeben.		      */
/******************************************************************************/


int kantenbeschraenkung(Sgraph sgraph)
{Snode snode;
 Sedge sedge;
 double e,n;
 int kantenzahl=0,knotenzahl=0;
 int valenz=0,min_valenz=10000000;
 int ret;

 for_all_nodes(sgraph,snode)
   {knotenzahl+=1;
    valenz=0;
    for_sourcelist(snode,sedge)
        {kantenzahl+=1;
	 valenz++;
	}end_for_sourcelist(snode,sedge)
    min_valenz=minimum(valenz,min_valenz);
   }end_for_all_nodes(sgraph,snode)
/*message("kantebes.: %i\n", (int)(((double)kantenzahl/2)/(double)knotenzahl+.9));*/
n=knotenzahl;
n=n-.5;
e=kantenzahl/2;   /* Jede Kante wurde zweimal gezaehlt */

ret= (int)( n-sqrt(n*n-2*e) + .99);

return (maximum(ret,min_valenz));
}

/*****************************************************************************/


/*******************************************************************************/
/*         sortiere slist nach groesse ihrer Mengenelemente                    */
/*******************************************************************************/
/*								               */
/* benoetigtee Prozeduren : menge_teilmenge_von_menge                          */
/* aufrufende Prozedur    : finde_dominante_cliquen                            */
/*								               */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                                */
/*              Slist slist  : eine Slist von Mengen.                          */
/*                             die Mengen enthalten jeweils eine               */
/*                             Clique                                          */
/*                                                                             */
/* Rueckgabeparameter :   Slist die sorierte Liste                             */  
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */

bool valenzbedingung(Sgraph sgraph, Menge clique, int max_cliquensize)
{Snode snode;
 Sedge sedge;
 int counter;

 for_menge(clique,snode)
     {counter =0;

      for_sourcelist(snode,sedge)
             {counter++;
     }end_for_sourcelist(snode,sedge)

      if ( counter < max_cliquensize )
             {return false; }

     }end_for_menge(clique,snode)

 return true;
}

/***********************************************************************/
/*******************************************************************************/
/*         sortiere slist nach groesse ihrer Mengenelemente                    */
/*******************************************************************************/
/*								               */
/* benoetigtee Prozeduren : menge_teilmenge_von_menge                          */
/* aufrufende Prozedur    : finde_dominante_cliquen                            */
/*								               */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                                */
/*              Slist slist  : eine Slist von Mengen.                          */
/*                             die Mengen enthalten jeweils eine               */
/*                             Clique                                          */
/*                                                                             */
/* Rueckgabeparameter :   Slist die sorierte Liste                             */
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */

int finde_untere_schranke_durch_maxCliquenseparator(Sgraph sgraph)
{  
   Slist clique,cliquenmenge;
   int max_cliquensize;
 
    /*finde eine maximalen Cliquen die Separator ist.*/  
    cliquenmenge=maximale_cliquen(sgraph); 
    max_cliquensize=size_of_menge( sgraph,mattrs(cliquenmenge) );

     /* finde eine Clique die kein Separator ist und alle Knoten Valenz>=|Clique|*/
     /*                                    => min_baumweite=|max_clique|           */
     /*                                    sonst  min_baumweite=|max_clique|-1     */
    for_slist(cliquenmenge,clique);
        {
         if(      (valenzbedingung(sgraph,mattrs(clique),max_cliquensize))
              &&  ((Slist)pruefe_auf_separator(sgraph,mattrs(clique)) == empty_slist)     )
		  {return max_cliquensize;
                  }
        }end_for_slist(cliquenmenge,clique)
   free_slist_of_menge(cliquenmenge);
   return max_cliquensize-1;
}

/*******************************************************************************/
/*         sortiere slist nach groesse ihrer Mengenelemente                    */
/*******************************************************************************/
/*								               */
/* benoetigtee Prozeduren : menge_teilmenge_von_menge                          */
/* aufrufende Prozedur    : finde_dominante_cliquen                            */
/*								               */
/* Parameter :  Sgraph sgraph : aktueller Sgraph                               */
/*              Slist slist  : eine Slist von Mengen.                          */
/*                             die Mengen enthalten jeweils eine               */
/*                             Clique                                          */
/*                                                                             */
/* Rueckgabeparameter :   Slist die sorierte Liste                             */
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */

Slist finde_max_cliquen_aus_dominanten(Sgraph sgraph, Slist dominante_cliquenmenge)
{
 Slist max_cliquenmenge=empty_slist;
 int max_size=0;
 Slist element;

 for_slist(dominante_cliquenmenge,element)
          {max_size=maximum(max_size,size_of_menge(sgraph,element->attrs.value.data));
          }end_for_slist(dominante_cliquenmenge,element)
 for_slist(dominante_cliquenmenge,element)
           {if(max_size==size_of_menge(sgraph,element->attrs.value.data))
              {max_cliquenmenge=anhaengen(max_cliquenmenge,element->attrs.value.data);
           }}end_for_slist(dominante_cliquenmenge,element)
      
return max_cliquenmenge;
}

/***********************************************************************/
/***********************************************************************/
/*******************************************************************************/
/*         sortiere slist nach groesse ihrer Mengenelemente                    */
/*******************************************************************************/
/*								               */
/* benoetigtee Prozeduren : menge_teilmenge_von_menge                          */
/* aufrufende Prozedur    : finde_dominante_cliquen                            */
/*								               */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                                */
/*              Slist slist  : eine Slist von Mengen.                          */
/*                             die Mengen enthalten jeweils eine               */
/*                             Clique                                          */
/*                                                                             */
/* Rueckgabeparameter :   Slist die sorierte Liste                             */
/*                                                                             */
/*******************************************************************************/
/* Verfahren:                                                                  */

int finde_untere_schranke_durch_maxCliquenseparator_fuer_Cliquenheuristic
                                              (Sgraph sgraph, Slist dominante_cliquenmenge)
{
   Slist clique,cliquenmenge;
   int max_cliquensize;
 
    /*finde eine maximalen Cliquen die Separator ist.*/  
    cliquenmenge=finde_max_cliquen_aus_dominanten(sgraph,dominante_cliquenmenge);
    max_cliquensize=size_of_menge( sgraph,mattrs(cliquenmenge) );

/* finde eine Clique die kein Separator ist und alle Knoten Valenz>=|Clique|*/
/*                                    => min_baumweite=|max_clique|           */
/*                                    sonst  min_baumweite=|max_clique|-1     */
   for_slist(cliquenmenge,clique);
        {
         if( (valenzbedingung(sgraph,mattrs(clique),max_cliquensize))
            && ((Slist)pruefe_auf_separator(sgraph,mattrs(clique))==empty_slist) )
                  {/*printf("  ist zumindets angeblich zulaessig");*/
                   return max_cliquensize;
                  }
        }end_for_slist(cliquenmenge,clique);
 free_slist_of_menge(cliquenmenge);
 return max_cliquensize-1;
}

/*******************************************************************************/
/*         finde beste untere Schranke  fuer Baumweite                         */
/*******************************************************************************/
/*								               */
/* benoetigtee Prozeduren : finde_untere_schranke_durch_maxCliquenseparator    */
/* aufrufende Prozedur    : main_algorithmen              	               */
/*								               */
/* Parameter :  Sgaph sgraph : aktueller Sgraph                                */
/*                                                                             */
/* Rueckgabeparameter :   void			                               */ 
/*                                                                             */
/*******************************************************************************/

void finde_beste_untere_Schranke(Sgraph sgraph)
{ 
   init_sgraph(sgraph);
   message("\n lower bound: %i \n",
        finde_untere_schranke_durch_maxCliquenseparator(sgraph));
}

