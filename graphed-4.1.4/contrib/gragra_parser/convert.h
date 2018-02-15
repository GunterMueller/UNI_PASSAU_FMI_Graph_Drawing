/* FERTIG 130293 */
/********************************************************************************/
/*										*/
/*	MODUL:	    convert							*/
/*										*/
/*	FUNKTION:   Zentrale Schnittstelle zu GraphEd (ueber sgraph/sgragra).	*/
/*			Das Modul enthaelt Prozeduren zur Konvertierung von 	*/
/*			sgraph/sgragra Datenstrukturen in die Datenstrukturen	*/
/*			des Parsers und umgekehrt.				*/
/*										*/
/********************************************************************************/
/*										*/
/*	in "misc.h" :	#define EXTERN		extern				*/
/*			#define DEFAULT(x)					*/
/*			#define NO_DEFAULT					*/
/*										*/
/********************************************************************************/
/*-->@	-Dconvert								*/
/*										*/
/*	Benutzt die Datenstrukturen von Sgraph, Sgragra (von GraphEd)		*/
/*	sowie die Datenstrukturen des Moduls 'types'.				*/

#ifndef CONVERT_HEADER
#define CONVERT_HEADER

#include <types.h>

/*-->@	CVT_info								*/
EXTERN	struct {

    /* Sgragra_create_mode */	int	grammar_scan_from;
    /* bool */			int	grammar_reduce_embeddings;
    /* bool */			int	grammar_reduce_productions;
    /* bool */			int	grammar_link_isomorph_productions;

    /* bool */			int	grammar_is_directed;
    /* bool */			int	grammar_is_boundary;
    /* bool */			int	graph_is_directed;
    /* bool */			int	graph_has_nonterminal_neighbours;
    
    int 				x_left;
    int					x_right;
    int 				y_top;
    int					y_bottom;

    } CVT_info;
	
/**/

extern	void	CVT_convert_sgraph_to_PE_list(Sgraph graph, struct parsing_element **Plist);
extern	void	CVT_convert_Sprod_to_PE_production(Sprod sp, PE_production *Prod);
extern	void	CVT_convert_sgragra_to_PE_grammar(Sgragra gragra, PE_production *Pgrammar);
extern	void	CVT_add_global_embedding_of(Sgragra gram, Sprod prod);
extern	void	CVT_show_info(void);
extern	void	CVT_init(void);

#endif
