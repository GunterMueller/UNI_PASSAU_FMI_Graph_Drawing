/* (C) Universitaet Passau 1986-1994 */

/*-------------------------------------------------------------------------------------*/
/* 
       Carl Frerichs  WS'88
       
       ZUSAMMENHANG          bei ungerichteten Graphen
       STARKER ZUSAMMENHANG  bei gerichteten Graphen
       ZWEIFACH-ZUSAMMENHANG bei zusammenhaengenden ungerichteten Graphen 

       BIBLIOTHEK: /monique/ih88046/libzus.a   [ cc .... -lzus -L/monique/ih88046 ]					           	                                       */ 
/*-------------------------------------------------------------------------------------*/
/*     				    KNOTENLISTE  	           		       */
/*-------------------------------------------------------------------------------------*/

typedef struct nodelist { struct nodelist *next;
                          Snode            elem;
                        }
                         *Nodelist;



#define   empty_nodelist   ((Nodelist) NULL)      

extern Nodelist MakeNodelist(Snode n);		
extern void AppNode(Nodelist *l, Snode n);				
extern void DelNode(Nodelist l, Snode n);      			
extern Snode FirstNode(Nodelist l);	
extern Nodelist RestNodelist(Nodelist l);		
extern Nodelist CopyNodelist(Nodelist l);		
extern void FreeNodelist(Nodelist *l);		


/*-------------------------------------------------------------------------------------*/
/*  			        KOMPONENTENLISTE                  		       */
/*-------------------------------------------------------------------------------------*/

typedef struct complist  {  struct complist  *next;
   			    Nodelist          elem;
			 }
			   *Complist;


#define   empty_complist   ((Complist) NULL)

extern Complist MakeComplist(Nodelist n);	
extern void AppComp(Complist *l, Nodelist n);
extern Nodelist FirstComp(Complist l);
extern Complist RestComplist(Complist l);	
extern void FreeComplist(Complist *l);
extern int SingleComp(Complist cl);


/*-------------------------------------------------------------------------------------*/
/*  			      ZUSAMMENHANGSTESTS            		      	       */
/*-------------------------------------------------------------------------------------*/

extern Complist GetStrongConComps(Sgraph g);
extern int IsStrongConnected(Sgraph g);
extern Complist GetConComps(Sgraph g);
extern int IsConnected(Sgraph g);
extern Complist GetBiconComps(Sgraph g);
extern int IsBiConnected(Sgraph g);
