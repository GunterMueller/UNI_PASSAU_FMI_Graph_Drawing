/* FERTIG 130293 */
/********************************************************************************/
/*										*/
/*	MODUL:	    reduce							*/
/*										*/
/*	FUNKTION:   Prozeduren / Funktionen zur Reduzierung von Grammatiken.	*/
/*										*/
/********************************************************************************/

#ifndef REDUCE_HEADER
#define REDUCE_HEADER

/*-->@	-Dreduce

	Keine eigenen Datenstrukturen.

	Benutzt Datenstrukturen von "types", "lab_int" und "bitset".

**/

extern	void	RED_reduce_productions(PE_production *gram);
extern	void	RED_reduce_embeddings(PE_production gram);
extern	void	RED_partial_reduce_productions(PE_production *gram);
extern	void	RED_link_isomorph_productions(PE_production *gram);

#endif

