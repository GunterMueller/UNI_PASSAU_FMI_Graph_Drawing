/* (C) Universitaet Passau 1986-1994 */
/* Letzte Aenderung: 08.02.91 */
   
#ifndef RESULT_TYPE_ALREADY_DEFINED   /* Verhindert Doppeldefinition */
                                      /* des Rueckgabetyps           */
#define RESULT_TYPE_ALREADY_DEFINED

/* Rueckgabewert der Funktion 'planarity' */
 
typedef	enum	Result {
	SUCCESS,        /* Prozedur erfolgreich - Graph planar */
	NONPLANAR,      /* Nichtplanaritaet entdeckt */
	SELF_LOOP,      /* Schleifen entdeckt */
	MULTIPLE_EDGE,  /* Mehrfachkante entdeckt */
	NO_MEM          /* Nicht genuegend Speicherplatz */
} 
	RESULT;

#endif

extern RESULT planarity(Sgraph graph);	/* planarity test		*/
extern Sgraph dual(Sgraph graph);		/* compute planer embedding	*/
extern RESULT embed(Sgraph graph);

/* Menu callbacks	*/
extern	GraphEd_Menu_Proc menu_planarity_ht;
extern	GraphEd_Menu_Proc menu_planarity_ht_embedding;
extern	GraphEd_Menu_Proc menu_planarity_ht_dualgraph;

