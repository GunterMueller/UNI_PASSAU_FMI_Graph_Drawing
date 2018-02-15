#ifndef control_h
#define control_h
/*****************************************************************************/
/*                                                                           */
/*                         M A I N   -   C O N T R O L                       */
/*                                                                           */
/* Modul        : control.h						     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/* Dieses modul enthelt die Schnittstellen zwischen Graphed und Sgraphauf-   */
/* rufen.   (call_sgraph_proc)						     */
/*****************************************************************************/
/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/* Aufrufe fuer  Prozedueren, die auf Sgraph arbeiten			     */
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


/*extern label_nodes;*/
extern void label_nodes_aufruf(void);
extern void change_label_aufruf(void);
/*extern void baumzerlegung_aufruf();
extern void baumzerlegung_aufruf2();
extern void dominante_cliquen_aufruf();*/
extern void max_cliquen_aufruf(void);
/*extern void baumbreite_aufruf();*/
extern void relabel_nodes_aufruf(void);
extern void label_nodes_aufruf(void);
extern void change_label_aufruf(void);
extern void nice_tree_decomp_aufruf(void);
extern void wurzel_aendern_aufruf(void);
extern void wurzel_mitte_aufruf(void);
/*extern void separatorheuristik_aufruf();
extern void tree_decomposition_aufruf();
extern void graph_reduzieren_aufruf();
extern void tree_decomposition2_aufruf();
extern void fortschritt_aufruf();*/
extern void clickabfrage_setzen(void);
extern void clickabfrage_loeschen(void);
extern void min_tree_decomp_aufruf(void);
extern void durchlaufe_graph_aufruf(void);
extern void dom_Cliquen_aufruf (void);
extern void max_cliquen_aufruf (void);

/*extern void baumweite_intelligent_aufruf();*/

extern void main_algorithmen_aufruf(void);

extern errorflag;
#endif
