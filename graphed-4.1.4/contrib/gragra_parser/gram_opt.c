/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Egram_opt								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Fgram_opt								*/
/*										*/
/*	MODUL:	gram_opt							*/
/*										*/
/*	FUNKTION: Prozeduren und Funktionen, die als Tools auf Grammatiken	*/
/*		  bzw. Produktionen arbeiten.					*/
/*										*/
/********************************************************************************/

#include "misc.h"
#include "types.h"
#include "lab_int.h"

#include "graph_op.h"
#include "gram_opt.h"

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*-->@	-Pgram_opt

	void	GMO_make_node_and_edgelabel_list	( PE_grammar gram )
	void	GMO_make_term_ableitbar_and_index	( PE_grammar gram )
	void	GMO_make_erreichbar			( PE_grammar gram )
	void	GMO_reverse_production_order		( PE_grammar *gram )
	void	GMO_sort_productions			( PE_grammar *gram )
	void	GMO_make_production_status		( PE_grammar *gram )
(bool)	int	GMO_isomorph_productions		( PE_production prod1, prod2 )
(bool)	int	GMO_check_boundary			( PE_grammar gram )

**/

/********************************************************************************/
/*										*/
/*-->	GMO_make_production_status						*/
/*										*/
/*	PARAMETER:	PE_grammar	*gram	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Berechne die Eigenschaften der Produktionen von *gram.	*/
/*										*/
/*	ERKLAERUNG:	Berechnet werden ..->index, ..->erreichbar und		*/
/*			..->term_ableitbar der Produktionen.			*/
/*			Die Grammatik wird dabei nicht reduziert und auch die	*/
/*			Reihenfolge der Produktionen wird nicht veraendert.	*/
/*										*/
/********************************************************************************/

void	GMO_make_production_status(PE_production *gram)
          	      			/* call BY REFERENCE ! */
{
	GMO_make_term_ableitbar_and_index( *gram );
	GMO_make_erreichbar( *gram );
}

/********************************************************************************/
/*										*/
/*-->	GMO_sort_productions							*/
/*										*/
/*	PARAMETER:	PE_grammar	*gram	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Sortiere die Produktionen von *gram gemaess ihrer	*/
/*			Indizes (dabei kleine Indizes am Anfang der Liste).	*/
/*										*/
/*	BESONDERES:	Wurden die Indizes (..->index) der Produktionen nicht	*/
/*			vor diesem Aufruf berechnet (d.h. alle auf 0), so	*/
/*			bewirkt diese Prozedur eine Umkehr der Reihenfolge der	*/
/*			Produktionen.						*/
/*										*/
/********************************************************************************/

void	GMO_sort_productions(PE_production *gram)
          	      			/* call BY REFERENCE ! */
{
	PE_grammar	tmp_grammar = (PE_grammar)NULL;
	PE_production	tmp;

	if( (gram==(PE_grammar *)NULL) || (*gram==(PE_grammar)NULL) ) {
		return;
	}
	tmp = *gram;
	while( tmp != (PE_production)NULL ) {
		PE_remove_production( gram, tmp );
		PE_insert_production( &tmp_grammar, tmp );
		tmp = *gram;
	}
	*gram = tmp_grammar;
}

/********************************************************************************/
/*										*/
/*-->	GMO_reverse_production_order						*/
/*										*/
/*	PARAMETER:	PE_grammar	*gram	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Kehre Reihenfolge der Produktionen um.			*/
/*										*/
/*	BESONDERES:	Einfach implementiert: negiere alle Indizes und 	*/
/*			sortiere anschliessend neu.				*/
/*										*/
/********************************************************************************/

void	GMO_reverse_production_order(PE_production *gram)
          	      			/* call BY REFERENCE ! */
{
	PE_grammar	tmp_grammar = (PE_grammar)NULL;
	PE_production	tmp;

	if( (gram==(PE_grammar *)NULL) || (*gram==(PE_grammar)NULL) ) {
		return;
	}
	tmp = *gram;
	while( tmp != (PE_production)NULL ) {
		PE_remove_production( gram, tmp );
		tmp->index = -(tmp->index);
		PE_insert_production( &tmp_grammar, tmp );
		tmp = *gram;
	}
	*gram = tmp_grammar;
}

/********************************************************************************/
/*										*/
/*-->	GMO_make_erreichbar							*/
/*										*/
/*	PARAMETER:	PE_grammar	gram					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Berechne fuer alle Produktionen von gram, ob in einer	*/
/*			Folge von Ableitungen die Produktion jemals anwendbar	*/
/*			ist. Falls ja, setze ..->erreichbar = TRUE.		*/
/*										*/
/********************************************************************************/

void	GMO_make_erreichbar(PE_production gram)
{
	Nr_label_list	reached_labels = NULL;
	int		another_production_marked;
	PE_production	prod;
	Parsing_element pe;

	(void) LI_add_label( MISC_get_grammar_start_symbol(), &reached_labels );

	do {
		another_production_marked = FALSE;
		PE_for_all_productions( gram, prod ) {
			if( !(prod->erreichbar) &&
			    (LI_get_number_of_label( prod->left_side, reached_labels ) != NOT_IN_LIST)
			   ) {
				PE_for_all_parsing_elements( prod->right_side, pe ) {
					(void) LI_add_label( pe->label, &reached_labels );
				} PE_end_for_all_parsing_elements( prod->right_side, pe );
				prod->erreichbar = TRUE;
				another_production_marked = TRUE;
			}
		} PE_end_for_all_productions( gram, prod );
	} while( another_production_marked );
	LI_reset_list( &reached_labels );
}

/********************************************************************************/
/*										*/
/*-->	GMO_make_term_ableitbar_and_index					*/
/*										*/
/*	PARAMETER:	PE_grammar	gram					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:     1. Berechne fuer alle Produktionen von gram, ob deren	*/
/*			rechte Seite jemals terminal abgeleitet werden kann.	*/
/*			Wenn ja, setze ..->term_ableitbar = TRUE.		*/
/*		     2. Parallel dazu berechne den Index (..->index) der	*/
/*			Produktionen. Der Index gibt an, wieviele Produktionen	*/
/*			mindestens auf die rechte Seite einer Prod. angewandt	*/
/*			werden muessen, damit diese terminal wird. Ist eine	*/
/*			Produktion nicht terminal ableitbar, so ist ihr 	*/
/*			Index = 0.						*/
/*										*/
/********************************************************************************/

void	GMO_make_term_ableitbar_and_index(PE_production gram)
{
	Nr_label_list	label_indices = NULL;
	int		another_production_marked;
	int		check_result;
	int		prod_index, tmp;
	PE_production	prod;
	Parsing_element pe;

	do {
		another_production_marked = FALSE;
		PE_for_all_productions( gram, prod ) {
		    if( !(prod->term_ableitbar) ) {
			prod_index = 0;
			check_result = TRUE;
			PE_for_all_parsing_elements( prod->right_side, pe ) {
				if( !MISC_is_terminal( pe->label ) ) {
					if( (tmp = LI_get_number_of_label( pe->label, label_indices )) == NOT_IN_LIST ) {
						check_result = FALSE;
						break;
					} else {
						prod_index += tmp;
					}
				}
			} PE_end_for_all_parsing_elements( prod->right_side, pe );
			if( check_result ) {
/*				prod->index = prod_index;*/
				prod->term_ableitbar = TRUE;
				another_production_marked = TRUE;
				prod_index++;
				(void) LI_add_label_with_index( prod->left_side, prod_index, &label_indices );
			}
		    }
		} PE_end_for_all_productions( gram, prod );
	} while( another_production_marked );

	PE_for_all_productions( gram, prod ) {
		if( prod->term_ableitbar ) {
			prod_index = 0;
			PE_for_all_parsing_elements( prod->right_side, pe ) {
				tmp = LI_get_number_of_label( pe->label, label_indices );
				if( tmp == NOT_IN_LIST ) {
					tmp = 0;
				}
				prod_index += tmp;
			} PE_end_for_all_parsing_elements( prod->right_side, pe );
			prod->index = prod_index;
		}
	} PE_end_for_all_productions( gram, prod );

	LI_reset_list( &label_indices );
}

/********************************************************************************/
/*										*/
/*-->	GMO_make_node_and_edgelabel_list					*/
/*										*/
/*	PARAMETER:	PE_grammar	gram					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Baue (globale) Datenstruktur fuer Knoten- und Kanten-	*/
/*			alphabet von gram auf.					*/
/*										*/
/*	ERKLAERUNG:	Obwohl durch das Sgragra-Modul bereits Knoten- und	*/
/*			Kantenalphabete unterstuetzt werden, koennen diese	*/
/*			fuer den Parser nicht benutzt werden, da sie nach	*/
/*			einer Grammatikreduktion u.U. ebenfalls reduziert	*/
/*			sein sollten.						*/
/*			Daher werden in dieser Prozedur die beiden, in		*/
/*			"lab_int" definierten Alphabete 'Nodelabel_list' und	*/
/*			'Edgelabel_list' erzeugt.				*/
/*			Auf diese beiden Listen kann dann ueber die entspr.	*/
/*			Makros von "lab_int" zugegriffen werden. (-Plab_int)	*/
/*										*/
/*	BESONDERES:	Mit dieser Prozedur werden auch die Labelnummern der	*/
/*			Knoten, Kanten und Einbettungsregeln fuer alle		*/
/*			Produktionen von 'gram' festgelegt.			*/
/*										*/
/********************************************************************************/

void	GMO_make_node_and_edgelabel_list(PE_production gram)
{
	PE_production	prod;
	Parsing_element pe;
	PE_embedding	emb;
	PE_edge 	edge;

	LI_reset_nodelabel_list();
	LI_reset_edgelabel_list();

	PE_for_all_productions( gram, prod ) {
		prod->left_side_num = LI_add_nodelabel( prod->left_side );
		PE_for_all_parsing_elements( prod->right_side, pe ) {
			pe->label_num = LI_add_nodelabel( pe->label );
			PE_for_all_embeddings( pe, emb ) {
				emb->edgelabel_pre_num = LI_add_edgelabel( emb->edgelabel_pre );
				emb->edgelabel_post_num = LI_add_edgelabel( emb->edgelabel_post );
				emb->nodelabel_num =  LI_add_nodelabel( emb->nodelabel );
			} PE_end_for_all_embeddings( pe, emb );
			PE_for_all_edges( pe, edge ) {
				edge->label_num = LI_add_edgelabel( edge->label );
			} PE_end_for_all_edges( pe, edge );
		} PE_end_for_all_parsing_elements( prod, pe );
	} PE_end_for_all_productions( gram, prod );
}

/********************************************************************************/
/*										*/
/*-->	GMO_isomorph_productions						*/
/*										*/
/*	PARAMETER:	1. PE_production	prod1				*/
/*			2. PE_production	prod2				*/
/*										*/
/*	ZURUECK:	TRUE gdw. prod1 isomorph zu prod2			*/
/*										*/
/*	AUFGABE:	Teste, ob zwei Produktionen isomorph sind.		*/
/*										*/
/*	ERKLAERUNG:	Isomorph bedeutet hier: 				*/
/*			1. identische linke Seite (Label)			*/
/*			2. isomorphe rechte Seiten mit				*/
/*			3. identischen Einbettungsregeln fuer isomorphe Knoten. */
/*										*/
/********************************************************************************/

int	GMO_isomorph_productions(PE_production prod1, PE_production prod2)
{
	PE_list g1;
	PE_list search_graph;

	if( prod1 == prod2 ) {
		return TRUE;	/* even if both are NULL */
	}
	if( (prod1==NULL) || (prod2==NULL) ) {
		return FALSE;
	}
	g1 = prod1->right_side;
	search_graph = prod2->right_side;
	if( strcmp( prod1->left_side, prod2->left_side ) ) {
		return FALSE;
	}
	if( (g1!=NULL) && (search_graph!=NULL) ) {
		if( SIZE_OF_GRAPH(g1) != SIZE_OF_GRAPH(search_graph) ) {
			return FALSE;
		}
	} else {
		ERROR( "Detected production with empty right side!\n" );
		if( g1 == search_graph ) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

	if( GPO_get_first_isomorph_node_overlay( search_graph, g1, FORWARD ) ) {
		do {
			if( GPO_check_edge_isomorphy( g1 ) && GPO_check_embedding_isomorphy( g1 ) ) {

				return TRUE;
			}
		} while( GPO_get_next_isomorph_node_overlay( search_graph, g1, FORWARD ) );
	}
	return FALSE;
}

/********************************************************************************/
/*										*/
/*-->	GMO_check_boundary							*/
/*										*/
/*	PARAMETER:	PE_grammar	gram					*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste, ob die grammatik 'gram' die Boundary-Eigenschaft */
/*			besitzt.						*/
/*										*/
/*	ERKLAERUNG:	'gram' ist boundary					*/
/*										*/
/*			gdw.							*/
/*										*/
/*			In keinem durch 'gram' abgeleiteten Graphen existiert	*/
/*			eine nichtterminale Nachbarschaft.			*/
/*										*/
/*			gdw.							*/
/*										*/
/*			In keiner rechten Seite einer Produktion von 'gram'	*/
/*			existiert eine nichtterminale Nachbarschaft.		*/
/*										*/
/********************************************************************************/

int	GMO_check_boundary(PE_production grammar)
{
	PE_production	prod;
	Parsing_element pe;
	PE_edge 	edge;
	
	if( grammar == NULL ) {
		return FALSE;
	}
	
	PE_for_all_productions( grammar, prod ) {
		PE_for_all_parsing_elements( prod->right_side, pe ) {
			if( !MISC_is_terminal( pe->label ) ) {
				PE_for_all_edges( pe, edge ) {
					if( !MISC_is_terminal( edge->partner->label ) ) {
						return FALSE;
					}
				} PE_end_for_all_edges( pe, edge );
			}
		} PE_end_for_all_parsing_elements( prod->right_side, pe );
	} PE_end_for_all_productions( grammar, prod );
	return TRUE;
}

/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mgram_opt								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fgram_opt							*/
/*m		-Egram_opt							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dgram_opt							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pgram_opt							*/
/********************************************************************************/

