/* FERTIG 130293 */
/********************************************************************************/
/*										*/
/*	MODUL: debug								*/
/*										*/
/*	FUNKTION: Prozeduren zur Darstellung der zentralen Datenstrukturen des	*/
/*		  Parsers auf Textdateien bzw. Textbildschirmen.		*/
/*										*/
/*	ZWECK:	Ueberpruefung der Datenstrukturen bei der Fehlersuche.		*/
/*										*/
/********************************************************************************/

#ifndef DEBUG_HEADER
#define DEBUG_HEADER

/*-->@	-Ddebug

	Keine eigenen Datenstrukturen.
	Benutzt die Datenstrukturen des Moduls "types".
	
	Definitionen fuer DBX_fshow_PE_list(.., graph_or_production):

	#define DBX_GRAPH		TRUE
	#define DBX_PRODUCTION		FALSE

**/

#define DBX_GRAPH		TRUE
#define DBX_PRODUCTION		FALSE

/********************************************************************************/
/*										*/
/*	exportierte Prozeduren/Funktionen :					*/
/*										*/
/********************************************************************************/

extern	void	DBX_fshow_PE_list(FILE *file, struct parsing_element *plist, int graph_or_production);
extern	void	DBX_fshow_Parsing_element(FILE *file, Parsing_element pe, int is_graphnode);
extern	void	DBX_fshow_PE_edge(FILE *file, PE_edge edge);
extern	void	DBX_fshow_embedding(FILE *file, PE_embedding emb);
extern	void	DBX_fshow_production(FILE *file, PE_production prod);
extern	void	DBX_fshow_grammar(FILE *file, PE_production gram);
extern	void	DBX_fshow_embedge_choices(FILE *file, Parsing_element pe);

#endif

