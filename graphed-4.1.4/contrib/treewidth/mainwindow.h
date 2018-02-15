#ifndef mainwindow_h
#define mainwindow_h
/*****************************************************************************/
/*                                                                           */
/*                  T R E E  -  W I D T H  -  W I N D O W                    */
/*                                                                           */
/* Modul        : mainwindow.h                                               */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/*                                                                           */
/* Dises Fenster dient zur Auswahl der verschiedenen Algorithmen          */
/*                                                                      */
/* Aussehen :							     */
/*                                                                 */
/*=============================================================   */
/*|  V                     TREE-WIDTH                         |   */
/*|===========================================================|   */
/*|                                                           |   */
/*|   Algorithm output : V Treewidth			      |   */
/*|                                                           |   */
/*|   with Algorithm   : V Degreeheuristic		      |	  */
/*|                                                           |   */
/*|   <START>               <label nodes>      <options>      |   */
/*|                                                           |   */
/*|   Status :    waiting for input                           |   */
/*|                                                           |   */
/*|   Algorithmus fortschritt  <=============== - - - - - >   |   */
/*|                             0                        100  |   */
/*|                                                           |   */
/*=============================================================   */
/*                                                                */
/******************************************************************/

/*****************************************************************************/
/*						                             */
/*		GLOBALE FUNKTIONEN UND PROZEDUREN		             */
/*								             */
/*****************************************************************************/
/*								             */
/*	void	main_control_window		()			     */
/*                ruft oder baut das `TREE-WIDTH-Fenster auf                 */
/*								             */
/*****************************************************************************/
/* Prozedueren um die 'Algorithmus Fortschritt anzeige zu steuern            */
/*								             */
/*	void 	init_fortschritt		(char* text,int max)         */
/*               setzt `text' in die Statuszewile und die Prozentanzeige auf */
/*		 Null. `max' enthaelt den 100% Wert fuer die Anzeige	     */
/*								             */
/*	void 	fortschritt			(int aktueller_wert)	     */
/*               der aktuele Stand des Algorithmus wird ausgegeben.          */
/*		 `aktueller_wert' sollte <= `max' sein.		    	     */
/*								             */
/*	void 	end_fortschritt			()			     */
/*		 die Fortschritt-anzeige wird auf Null gesetzt und der Staus-*/
/*		 text auf "waiting for input"				     */
/*								             */
/*****************************************************************************/

extern void fortschritt(int aktueller_wert);
extern void init_fortschritt(char *text, int max);
extern void end_fortschritt(void);
extern void main_control_window(Menu menu, Menu_item menu_item);


/*****************************************************************************/
/*						                             */
/*		GLOBALE Variablen und Datenstruckturen		             */
/*								             */
/*****************************************************************************/
/*								             */
/* 	algorithmus      :   enthaelt den gewuenschten Algorithmustyp	     */
/*								             */
/*	algo_output_type :   enthaelt den gewuenschten Ausgabetype	     */
/*								             */
/*****************************************************************************/

typedef void (*proced)();


enum MAIN_ALGORITHMEN {ARNBORG,
                       CLIQUENHEURISTIC,
                       SEPERATORENHEURISTIC,
                       KANTENHEURISTIC,
                       VALENZHEURISTIK,
		       UNTERESCHRANKE,
                       MAX_CLIQUEN,
                       DOM_CLIQUEN,
		       ERWEITERUNG   /* fuer Benutzer erweiterungen */
		      };

extern enum MAIN_ALGORITHMEN algorithmus;

enum ALGO_OUTPUT_TYPE {BAUMBREITE,
                       BAUMZERLEGUNG
                       };
extern enum ALGO_OUTPUT_TYPE algo_output_type;

extern void add_to_treewidth_menu(char *text, proced procedure);
extern main_control_window_frame_active;
extern proced get_fremd_procedure(void);
extern void aktuallisiere_algo_output_type(void);
#endif
