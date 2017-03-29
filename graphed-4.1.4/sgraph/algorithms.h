/* (C) Universitaet Passau 1986-1994 */
/* Hopcroft/Tarjan Planarity test */

#ifndef RESULT_TYPE_ALREADY_DEFINED   /* Verhindert Doppeldefinition */
                                      /* des Rueckgabetyps           */
#define RESULT_TYPE_ALREADY_DEFINED

/* Rueckgabewert der Funktion 'planarity' */
 
typedef
  enum Result {
               SUCCESS,        /* Prozedur erfolgreich - Graph planar */
               NONPLANAR,      /* Nichtplanaritaet entdeckt */
               SELF_LOOP,      /* Schleifen entdeckt */
               MULTIPLE_EDGE,  /* Mehrfachkante entdeckt */
               NO_MEM          /* Nicht genuegend Speicherplatz */
              } 
       HT_PLANARITY_RESULT;

#define RESULT HT_PLANARITY_RESULT

#endif

/* Fuehrt Planaritaetstest fuer 'Sgraph' durch;
   Rueckgebewert ist eine Meldung vom Typ 'RESULT'. */

extern RESULT planarity(Sgraph graph);	/* planarity_ht */

/* Nach Aufruf von 'embed(sgraph)' wird im 'attrs'-Feld der 
   'Snode'-Struktur eine Liste vom Typ 'Slist' abgelegt, in
   der die Kanten um den jeweiligen Knoten - unabhaengig von
   einer eventuellen Orientierung bei gerichteten Graphen -
   als Zeiger vom Typ 'Sedge' im Uhrzeigersinn eingetragen 
   sind.
   Rueckgebewert ist eine Meldung vom Typ 'RESULT'.          */
   
/* ACHTUNG: Fuer das Loeschen der 'Slist' ist der Benutzer 
            selbst verantwortlich !                          */
 
extern RESULT embed(Sgraph graph);	/* planarity_ht */


extern	bool test_sgraph_connected(Sgraph g);		/* connectivity_tests */
extern	bool test_sgraph_biconnected(Sgraph g);		/* connectivity_tests */
extern	bool test_sgraph_strongly_connected(Sgraph g);	/* connectivity_tests */


