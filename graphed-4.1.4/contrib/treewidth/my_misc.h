#ifndef misc_h
#define misc_h
/*******************************************************************************/
/*                 A l l g e m e i n e   P r o z e d u r e n                   */
/*                                                                             */
/* Modul        : my_misc.h						       */
/* erstellt von : Nikolas Motte                                                */
/* erstellt am  : 10.12.1992                                                   */
/*                                                                             */
/*******************************************************************************/
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
/********************************************************************************/


#include "mystd.h"
#include "menge.h"
#include <sgraph_interface.h>
#include <sgraph.h>

extern int standart_node_type;
extern longlabel;

extern int my_node_type;
extern bool errorflag;

extern void relabel_nodes(Sgraph_proc_info info);
extern void my_sgraph_click_event_func(Sgraph_proc_info info, Sgraph_event_proc_info uev_info, char **event);
extern void markierungen_loeschen(Sgraph_proc_info info);
extern void clique_anzeigen(Sgraph_proc_info info);
extern void durchlaufe_graph(Sgraph_proc_info info);
extern void wurzel_aendern(Sgraph_proc_info info);
extern void wurzel_mitte(Sgraph_proc_info info);
extern void change_label(Sgraph_proc_info info);
extern void knoten_anpassen(void);
extern void baum_zeichnen(Sgraph_proc_info info);
extern void label_nodes(Sgraph_proc_info info);
extern void label_knoten(Sgraph_proc_info info);
extern void my_set_nodelabel(Sgraph sgraph, Snode snode, char *clique);
extern void my_set_nodelabel2(Sgraph sgraph, Snode snode, Slist slist);
extern void loesche_markierungen(void);
extern void keine_markierung(void);



#endif
