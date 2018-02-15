/* FERTIG 130293 */
/********************************************************************************/
/*										*/
/*	MODUL:	gram_opt							*/
/*										*/
/*	FUNKTION: Prozeduren und Funktionen, die als Tools auf Grammatiken	*/
/*		  bzw. Produktionen arbeiten.					*/
/*										*/
/********************************************************************************/

#ifndef GRAM_OPT_HEADER
#define GRAM_OPT_HEADER

extern	void	GMO_make_node_and_edgelabel_list(PE_production gram);
extern	void	GMO_make_term_ableitbar_and_index(PE_production gram);
extern	void	GMO_make_erreichbar(PE_production gram);
extern	void	GMO_reverse_production_order(PE_production *gram);
extern	void	GMO_sort_productions(PE_production *gram);
extern	void	GMO_make_production_status(PE_production *gram);
extern	int	GMO_isomorph_productions(PE_production prod1, PE_production prod2);
extern	int	GMO_check_boundary(PE_production grammar);

/*-->@	-Dgram_opt

	Keine eigenen Datenstrukturen.

	Benutzt die Datenstrukturen von "types" und "lab_int".

**/

#endif

