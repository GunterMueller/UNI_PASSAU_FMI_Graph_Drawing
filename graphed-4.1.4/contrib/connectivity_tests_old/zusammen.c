/* (C) Universitaet Passau 1986-1994 */
#include "std.h"
#include "sgraph.h"
#include "slist.h"
#include "graphed.h"
#include "zusammen.h"

/*-------------------------------------------------------------------------------------*/
/* 
       PROGRAMMIERMETHODISCHES PRAKTIKUM                 WS  88/89
       LEHRSTUHL PROF. BRANDENBURG
       
       Graph-Algorithmen unter Verwendung der Sgraph-Datenstruktur



       Carl Frerichs  12.88 - 1.89   

       
       ZUSAMMENHANG          bei ungerichteten Graphen
       STARKER ZUSAMMENHANG  bei gerichteten Graphen
       ZWEIFACH-ZUSAMMENHANG bei zusammenhaengenden ungerichteten Graphen 
                                                                        	       */ 
/*-------------------------------------------------------------------------------------*/


Local void Fehler(char *s)
{ 
  error ("\n<<< %s >>>\n\n",s);
}


/*-------------------------------------------------------------------------------------*/
/*         KNOTENLISTE         DEFINITION  UND  OPERATIONEN            

           Datenstruktur fuer Mengen von Sgraph-Knoten .
           Hier verwendet zur Darstellung jeweils einer 
 	   Zusammenhangs-Komponente .                                		       */
/*-------------------------------------------------------------------------------------*/


#define   ERRFIRSTNODE  "FirstNode: Knotenliste ist bereits leer"
#define   ERRRESTNODE   "RestNodelist: Knoteniste ist bereits leer"


Nodelist MakeNodelist(Snode n)				/* ERZEUGE LISTE MIT KNOTEN n  */ 
        
{
  Nodelist h;
   
  h = (Nodelist) malloc(sizeof(struct nodelist));
  h->elem = n;
  h->next = empty_nodelist;
  return(h);
}


void AppNode(Nodelist *l, Snode n)					/* HAENGE KNOTEN n AN  LISTE l */
             
        
{
  Nodelist g;

  if (*l == empty_nodelist)   *l = MakeNodelist(n);
    else                    {  g = (*l);
           	               while ((g->next != NULL) && (g->elem->nr != n->nr))
                               g=g->next;
		              if (g->elem->nr != n->nr)  g->next=MakeNodelist(n);
 		            };
}


void DelNode(Nodelist l, Snode n)      					/* LOESCHE KNOTEN n AUS l      */
           
        
{
  Nodelist h,alt;
  
  h=l;
  alt=empty_nodelist;
  if (h != empty_nodelist)
    {
      while ((h->next != empty_nodelist) && (h->elem->nr != n->nr))
        {  alt = h;
     	   h=h->next;
        };
      if (h != empty_nodelist)
        {
          if (alt != empty_nodelist)  { alt->next = h->next;
			                free(h);
			              }
            else                      { alt = l;
			                l = l->next;
			                *(alt->elem) = *(l->elem);
                                        alt->next = l->next;
 			                free(l);
			              };

	};
    };
}


Snode FirstNode(Nodelist l)					/* ERSTER KNOTEN DER LISTE l   */
           
{
  if (l != empty_nodelist)   return(l->elem);
    else                   { Fehler(ERRFIRSTNODE);
	          	     return(NULL);
		           };
}


Nodelist RestNodelist(Nodelist l)				/* ANWENDUNG: l=RestNodelist(l)
							   SPEICHERFREIGABE , REST     */
           
{
  Nodelist h;

  if ( l == empty_nodelist)  { Fehler(ERRRESTNODE);
	  	               return(empty_nodelist);
		             }
    else 	             { h = l->next;
   		               free(l);
		               return(h);
		             }; 
}


Nodelist CopyNodelist(Nodelist l)				/* KNOTENLISTE KOPIEREN        */
           
{
  Nodelist h;

  if (l == empty_nodelist)   return(empty_nodelist);
    else	           { h = MakeNodelist(FirstNode(l));
		    	     while (l->next != empty_nodelist)
		  	      {
      			        l = l->next;
		 	        AppNode(&h,l->elem);
		  	      };
			    return(h);
		 };
}


void FreeNodelist(Nodelist *l)					/* SPEICHER FREIGEBEN          */
            
{
  Nodelist g,h;

  g = *l;
  while (g != empty_nodelist)  { h = g;
		                 g = g->next;
		 	         free(h);
		 	       };
  *l = empty_nodelist;
}






/*-------------------------------------------------------------------------------------*/
/*     KOMPONENTENLISTE       DEFINITON UND OPERATIONEN

       Liste von Teilmengen eines Graphen in Sgraph , hier
       verwendet als Liste der Zusammenhangs-Komponenten .
                                                               			       */
/*-------------------------------------------------------------------------------------*/


#define ERRFIRSTCOMP  "FirstComp: Komponentenliste ist bereits leer"
#define ERRRESTCOMP   "RestComplist: Komponentenliste ist bereits leer"


Complist MakeComplist(Nodelist n)				/* ERZEUGE LISTE MIT ELEM. n   */
           
{
  Complist h;

  h = (Complist) malloc(sizeof(struct complist));
  h->elem = CopyNodelist(n); 
  h->next = empty_complist;
  return(h);
}


void AppComp(Complist *l, Nodelist n)					/* HAENGE KNOTENLISTE n
							   AN KOMPONENTENLISTE l       */
            
           
{
  Complist g;

  
  if ((*l) == empty_complist)  (*l) = MakeComplist(n);
    else       		     { g = (*l);
			       while (g->next != empty_complist)  g = g->next;
            	   	       g->next = MakeComplist(n);
			     };
}


Nodelist FirstComp(Complist l)					/* ERSTE KOMPONENTE VON l      */
           
{
  if (l != empty_complist)   return(l->elem);
    else       		   { Fehler(ERRFIRSTCOMP);
			     return(empty_nodelist);
			   };
}


Complist RestComplist(Complist l)				/* ANWENDUNG: l=RestComplist(l)
							   SPEICHERFREIGABE ( ACHTUNG:
							   AUCH DER KNOTENLISTEN ) , 
							   REST                        */
           
{
  Complist h;

  if (l == empty_complist) { Fehler(ERRRESTCOMP);
 		   	     return(empty_complist);
		 	   }
    else 	     	   { h = l->next;
                 	     FreeNodelist(&(l->elem));
		  	     free(l);
		    	     return(h);
			   };
}


void FreeComplist(Complist *l)					/* SPEICHERFREIGABE ( ACHTUNG:
							   AUCH DER KNOTENLISTEN )     */ 
				
            
{
  Complist g,h;

  g = *l;
  while (g != empty_complist)
    {
      h = g;
      g = g->next;
      FreeNodelist(&(h->elem));
      free(h);
    };
  (*l) = empty_complist;
}



int SingleComp(Complist cl)					/* TEST , OB LISTE NUR EINE
							   KOMPONENTE ENTHAELT         */
            
{
  if (cl == empty_complist)   return(FALSE);
    else                      return(cl->next == empty_complist);
}




/*-------------------------------------------------------------------------------------*/
/*                       ALGORITHMEN       ZUSAMMENHANG                                */
/*-------------------------------------------------------------------------------------*/


#define ERRCON    "GetConComps/IsConnected: Erwarte ungerichteten Graph"
#define ERRSTRONG "GetStrongConComps/IsStrongConn.: Erwarte gerichteten Graph"
#define ERRBIDIR  "GetBiconComps/IsBiConnected: Erwarte ungerichteten Graph"
#define ERRBICON "GetBiconComps/IsBiConnected: Erwarte zusammenhaengenden Graph"


Complist ZusFehler(char *s)
{
  Fehler(s);
  return(empty_complist);
}


/***************************************************************************************/
/******************************* INTERNE DATENSTRUKTUR *********************************/
/***************************************************************************************/

typedef struct intern  {  struct intern  *next;
			  struct intern  *father;
			  struct sedge   *Edge;
			  struct snode   *Node;
                          int             InCurrent;
			  int		  InS;
		          int		  DfsNum;
			  int		  LowPt;
		       }
			 *Intern;


#define   empty_intern     ((Intern) NULL)


Intern MakeIntern(Snode n)
{
  Intern h;

  h = (Intern) malloc(sizeof(struct intern));

  h->next      = empty_intern;
  h->father    = empty_intern;
  h->Edge      = n->slist;
  h->Node      = n;
  h->InCurrent = FALSE;
  h->InS       = FALSE;
  h->DfsNum    = 0;
  h->LowPt     = 0;

  return(h);
}



void AppIntern(Intern *l, Snode n)
{
  Intern g;

  if ((*l) == empty_intern)   (*l) = MakeIntern(n);
    else                    { g = (*l);
         	 	      while (g->next != empty_intern)  g = g->next;
                   	      g->next = MakeIntern(n);
		  	    };
}


void  FreeIntern(Intern *l)
{
  Intern g,h;

  g = (*l);
  while (g != empty_intern)
   {
     h = g;
     g = g->next;
     free(h);
   };
  (*l) = empty_intern;
}



/***************************************************************************************/
/********************************  DEPTH FIRST SEARCH  *********************************/
/***************************************************************************************/


Intern   AttList;
int      DfsCount;
int      Bicon;
Complist ZusComps;



Intern Nachfolger(Intern v)
{
  Intern h;
  int n;
  
  n = v->Edge->tnode->nr;
  h = AttList;
  while (h->Node->nr != n)  h = h->next;
  return(h);
}


void ZusDfsFound(Intern w)
{
  w->InS = TRUE;
  w->InCurrent = TRUE;
  w->DfsNum = ++DfsCount;
}
 

void ZusDFS(Intern v)
{
  Intern w,h;
  Sedge he;
  Nodelist l;

  v->LowPt = v->DfsNum;
  he = NULL;

  while (v->Edge != he)
    {
      w = Nachfolger(v);
      v->Edge = v->Edge->ssuc;
      if (he == NULL)  he = v->Node->slist;
      if (!(w->InS))
        {
	  ZusDfsFound(w);
	  w->father = v;
	  ZusDFS(w);
	  if (v->LowPt > w->LowPt)  v->LowPt = w->LowPt;
	};
      if ((w->DfsNum < v->DfsNum) &&
          (Bicon || (w->InCurrent))
         )
        if (v->LowPt > w->DfsNum)  v->LowPt = w->DfsNum;
    };
  if ( ( (  Bicon ) && (v->DfsNum > 1) && (v->LowPt == v->father->DfsNum) )
       ||
       ( (!(Bicon)) && (v->LowPt == v->DfsNum) ) 
     )
    {  h = AttList;
       l = MakeNodelist(v->Node); 
       v->InCurrent = FALSE;
       if (Bicon)  AppNode(&l,v->father->Node);
       while (h != empty_intern) 
         { 
           if ((h->DfsNum >= v->DfsNum) && (h->InCurrent))
	     {
	        h->InCurrent = FALSE;
		AppNode(&l,h->Node);
	     };
	   h = h->next;
         };
       AppComp(&ZusComps,l);
    };
}



Complist ZusDfsWork(Sgraph g, int bi)
{
  Intern h;
  Snode n,an;

  ZusComps = empty_complist;
  DfsCount = 0;
  Bicon = bi;

  an = g->nodes;
  AttList = empty_intern;
  n = an;
  if (n != NULL)  { AppIntern(&AttList,n);
		    n = n->suc;
		    while (n != an)  { AppIntern(&AttList,n);
				       n = n->suc;
				     };
		  };
  h = AttList;
  while (h != empty_intern)  { if (!(h->InS))  { ZusDfsFound(h);
          					 ZusDFS(h);
				      	       };
		       	       h = h->next;
			     };
  FreeIntern(&AttList);
  return(ZusComps);
}




/***************************************************************************************/
/*    STARKER ZUSAMMENHANG 

      Returnwert:  Liste der stark zusammenhaengenden Komponenten 
		   eines gerichteten Graphen .
                   Falls g ungerichtet , dann Feglermeldung .
                                                                                       */
/***************************************************************************************/



Complist GetStrongConComps(Sgraph g)
{
  if (!(g->directed))  return(ZusFehler(ERRSTRONG));
    else               return(ZusDfsWork(g,FALSE));
}


/***************************************************************************************/
/*   STARKER ZUSAMMENHANG   JA/NEIN     

     Verwendung der obigen Funktion mit entsprechenden Fehlermeldungen . 
                                                                                       */
/***************************************************************************************/


int IsStrongConnected(Sgraph g)
{
  return(SingleComp(GetStrongConComps(g)));
}





/***************************************************************************************/
/*    ZUSAMMENHANG 

      Returnwert:  Liste der zusammenhaengenden Komponenten 
		   eines ungerichteten Graphen .
                   Falls g gerichtet , dann Feglermeldung .
                                                                                       */
/***************************************************************************************/

Complist GetConComps(Sgraph g)
{
  if (g->directed)   return(ZusFehler(ERRCON));
    else             return(ZusDfsWork(g,FALSE));
}

/***************************************************************************************/
/*   ZUSAMMENHANG   JA/NEIN     

     Verwendung der obigen Funktion mit entsprechenden Fehlermeldungen . 
                                                                                       */
/***************************************************************************************/


int IsConnected(Sgraph g)
{
  return(SingleComp(GetConComps(g)));
}



/***************************************************************************************/
/*    ZWEIFACHER ZUSAMMENHANG 

      Returnwert: Die zweifach-zusammenhaegenden Komponenten von g , wenn
                  g zusammenhaengend und ungerichtet ist .
                  Sonst: Entsprechende Fehlermeldungen und leere Liste . 
                                                                                       */
/***************************************************************************************/
                                  

Complist GetBiconComps(Sgraph g)
{
  if (g->directed)   return(ZusFehler(ERRBIDIR));
    else { if (!(IsConnected(g)))   return(ZusFehler(ERRBICON));
	     else                   return(ZusDfsWork(g,TRUE));
	 };
}


/***************************************************************************************/
/*   ZWEIFACH ZUSAMMENHANG   JA/NEIN     

     Verwendung der obigen Funktion mit entsprechenden Fehlermeldungen . 
                                                                                       */
/***************************************************************************************/


int IsBiConnected(Sgraph g)
{
  return(SingleComp(GetBiconComps(g)));
}


#include <xview/xview.h>

/************************************************************************/
/*									*/
/*			Menue check connectivity			*/
/*									*/
/************************************************************************/

static	void		check_connectivity (Sgraph_proc_info info)
{
	int	connected;
	Sgraph	sgraph = info->sgraph;
	
	if (sgraph == empty_sgraph) {
		bell ();
	} else {
	
		info->no_changes           = TRUE;
		info->no_structure_changes = TRUE;
		info->save_selection       = TRUE;
		
		if (!sgraph->directed) {
			connected = IsConnected (sgraph);
		} else {
			connected = IsStrongConnected (sgraph);
		}
		if (connected) {
			message ("This graph is connected\n");
		} else {
			message ("This graph is not connected\n");
		}
	}
}


void	menu_check_connectivity (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	call_sgraph_proc (check_connectivity, NULL);
}



/************************************************************************/
/*									*/
/*			Menue check biconnectivity			*/
/*									*/
/************************************************************************/

static	void		check_biconnectivity (Sgraph_proc_info info)
{
	int	connected;
	Sgraph	sgraph = info->sgraph;
	
	if (sgraph == empty_sgraph) {
		bell ();
	} else {
	
		info->no_changes           = TRUE;
		info->no_structure_changes = TRUE;
		info->save_selection       = TRUE;
		
		if (!sgraph->directed) {
			connected = IsBiConnected (sgraph);
		} else {
			message ("Test not implemented for directed graphs.\n");
			return;
		}
		if (connected) {
			message ("This graph is biconnected\n");
		} else {
			message ("This graph is not biconnected\n");
		}
	}
}


void	menu_check_biconnectivity (Menu menu, Menu_item menu_item)
    		     		/* The menu from which it is called	*/
         	          	/* The menu item from ...		*/
{
	call_sgraph_proc (check_biconnectivity, NULL);
}


