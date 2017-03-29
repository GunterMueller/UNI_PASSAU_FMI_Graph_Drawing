/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Ereduce								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Freduce								*/
/*										*/
/*	MODUL:	    reduce							*/
/*										*/
/*	FUNKTION:   Prozeduren / Funktionen zur Reduzierung von Grammatiken.	*/
/*										*/
/********************************************************************************/

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include "misc.h"
#include "types.h"
#include "lab_int.h"
#include "gram_opt.h"
#include "graph_op.h"

/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/
#include "lp_datastruc.h"
/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/

#include "reduce.h"

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*-->@	-Preduce

	void	RED_reduce_productions		(PE_grammar *gram);
	void	RED_partial_reduce_productions	(PE_grammar *gram);

	void	RED_reduce_embeddings		(PE_grammar gram);

	void	RED_link_isomorph_productions	(PE_grammar *gram )

**/

/********************************************************************************/
/*										*/
/*-->	RED_reduce_productions							*/
/*										*/
/*	PARAMETER:	PE_grammar  *gram	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne aus '*gram' alle Produktionen, die nicht	*/
/*			erreichbar oder nicht terminal ableitbar sind.		*/
/*										*/
/*	BESONDERES:	Ruft GMO_make_production_status(..) auf.		*/
/*										*/
/*	ERKLAERUNG:	Nicht erreichbare Produktionen koennen nie angewandt	*/
/*			werden. 						*/
/*			Nicht terminal ableitbare Produktionen durften bei der	*/
/*			Erzeugung eines terminalen Graphen nicht angewandt	*/
/*			werden. 						*/
/*			Also sind solche Prod.en beim Parsen eines TERMINALEN	*/
/*			Graphen ohne jede Bedeutung ==> raus damit!		*/
/*										*/
/********************************************************************************/

void	RED_reduce_productions(PE_production *gram)
          	      			/* call BY REFERENCE ! */
{
	PE_grammar	tmp_grammar = (PE_grammar)NULL ;
	PE_production	tmp;

	if( (gram==(PE_grammar *)NULL) || (*gram==(PE_grammar)NULL) ) {
		return;
	}

	GMO_make_production_status( gram );	/* compute index, erreichbar & term_ableitbar	*/
						/* of each production of *gram			*/

	tmp = *gram;
	while( tmp != (PE_production)NULL ) {
		PE_remove_production( gram, tmp );
		if( (tmp->erreichbar) && (tmp->term_ableitbar) ) {
			PE_insert_production( &tmp_grammar, tmp );
		} else {
			PE_dispose_production( &tmp );
		}
		tmp = *gram;
	}
	*gram = tmp_grammar;
}

/********************************************************************************/
/*										*/
/*-->	RED_reduce_embeddings							*/
/*										*/
/*	PARAMETER:	PE_grammar   gram					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne aus den Produktionen (bzw. deren rechte	*/
/*			Seiten ) von 'gram' alle nicht anwendbaren Einbettuns-	*/
/*			regeln. 						*/
/*										*/
/*	ERKLAERUNG:	Nicht anwendbare Einbettungsregeln sind PE_embedding's, */
/*			die eine Anwendbarkeitsumgebung erwarten welche die	*/
/*			Grammatik 'gram' nicht erzeugen kann.			*/
/*			Solche Regeln tragen nichts zur Erzeugung von Kanten	*/
/*			bei ==> raus damit!					*/
/*										*/
/********************************************************************************/

#define SPEC( snode_label, edge_label, tnode_label ) \
	( ( LI_get_number_of_nodelabel( snode_label ) * edge_size + LI_get_number_of_edgelabel( edge_label ) ) \
	  * node_size + LI_get_number_of_nodelabel( tnode_label ) )

void	RED_reduce_embeddings(PE_production gram)
{
	int	bitsetsize, node_size, edge_size;
	Bitset	reached_node_edge_specifications;
	int	found_another_specification;
	int	tmp_spec;
	PE_production	prod;
	Parsing_element pe;
	PE_edge 	e;
	PE_embedding	emb, tmp_emb;

	GMO_make_node_and_edgelabel_list( gram );
	node_size = LI_sizeof_nodelabel_list() + 1;
	edge_size = LI_sizeof_edgelabel_list() + 1;
	bitsetsize = node_size * edge_size * node_size;
	if( bitsetsize == 0 ) {
		return;
	}

	if( !BS_init_set( &reached_node_edge_specifications, bitsetsize ) ) {
		return;
	}

	/* initialize 'reached_node_edge_specifications' */
	PE_for_all_productions( gram, prod ) {
		PE_for_all_parsing_elements( prod->right_side, pe ) {
			PE_for_all_edges( pe, e ) {
				if( (e->dir == OUT) || (e->dir == UNDIRECTED) ) {
					tmp_spec = SPEC( pe->label, e->label, e->partner->label );
					BS_include( reached_node_edge_specifications, tmp_spec );
				}
			} PE_end_for_all_edges( pe, e );
		} PE_end_for_all_parsing_elements( prod->right_side, pe );
	} PE_end_for_all_productions( gram, prod );

	/* now test if embeddings produce new node_edge_specifications */
	do {
	    found_another_specification = FALSE;
	    PE_for_all_productions( gram, prod ) {
		PE_for_all_parsing_elements( prod->right_side, pe ) {
		    PE_for_all_embeddings( pe, emb ) {
			switch( emb->edgedir_pre ) {
				case	IN:	tmp_spec = SPEC( emb->nodelabel, emb->edgelabel_pre, prod->left_side );
						break;
				case	OUT:	tmp_spec = SPEC( prod->left_side, emb->edgelabel_pre, emb->nodelabel );
						break;
				case UNDIRECTED:tmp_spec = SPEC( prod->left_side, emb->edgelabel_pre, emb->nodelabel );
						break;
				default:	tmp_spec = 0;
						break;
			}
			if( BS_is_in_set( reached_node_edge_specifications, tmp_spec ) ) {
				switch( emb->edgedir_post ) {
					case	IN:	tmp_spec = SPEC( emb->nodelabel, emb->edgelabel_post, pe->label );
							break;
					case	OUT:	tmp_spec = SPEC( pe->label, emb->edgelabel_post, emb->nodelabel );
							break;
					case UNDIRECTED:tmp_spec = SPEC( pe->label, emb->edgelabel_post, emb->nodelabel );
							break;
					default:	tmp_spec = 0;
							break;
				}
				if( !BS_is_in_set( reached_node_edge_specifications, tmp_spec ) ) {
					found_another_specification = TRUE;
					BS_include( reached_node_edge_specifications, tmp_spec );
					if( emb->edgedir_post == UNDIRECTED ) {
						tmp_spec = SPEC( emb->nodelabel, emb->edgelabel_post, pe->label );
						BS_include( reached_node_edge_specifications, tmp_spec );
					}
				}
			}
		    } PE_end_for_all_embeddings( pe, emb );
		} PE_end_for_all_parsing_elements( prod->right_side, pe );
	    } PE_end_for_all_productions( gram, prod );
	} while ( found_another_specification );

	/* now remove all embedding rules that never can be applied */
	PE_for_all_productions( gram, prod ) {
		PE_for_all_parsing_elements( prod->right_side, pe ) {
			emb = pe->loc_embedding;
			while( emb != (PE_embedding)NULL ) { /* PE_for_all_embeddings must not be used
								because of possible PE_remove_embedding()'s ! */
				switch( emb->edgedir_pre ) {
					case	IN:	tmp_spec = SPEC( emb->nodelabel, emb->edgelabel_pre, prod->left_side );
							break;
					case	OUT:	tmp_spec = SPEC( prod->left_side, emb->edgelabel_pre, emb->nodelabel );
							break;
					case UNDIRECTED:tmp_spec = SPEC( prod->left_side, emb->edgelabel_pre, emb->nodelabel );
							break;
					default:	tmp_spec = 0;
							break;
				}
				tmp_emb = emb->succ;
				if( !BS_is_in_set( reached_node_edge_specifications, tmp_spec ) ) {
					PE_remove_embedding( pe, emb );
					PE_dispose_embedding( &emb );
				}
				emb = tmp_emb;
			}
		} PE_end_for_all_parsing_elements( prod->right_side, pe );
	} PE_end_for_all_productions( gram, prod );

	(void) BS_delete_set( &reached_node_edge_specifications );
}

/********************************************************************************/
/*										*/
/*-->	RED_partial_reduce_productions						*/
/*										*/
/*	PARAMETER:	PE_grammar  *gram	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Entferne aus '*gram' alle Produktionen, die nicht	*/
/*			erreichbar sind.					*/
/*										*/
/*	BESONDERES:	Ruft GMO_make_production_status(..) auf.		*/
/*										*/
/*	ERKLAERUNG:	Nicht erreichbare Produktionen koennen nie angewandt	*/
/*			werden. 						*/
/*			Also sind solche Prod.en beim Parsen eines Graphen ohne */
/*			jede Bedeutung ==> raus damit!				*/
/*										*/
/********************************************************************************/

void	RED_partial_reduce_productions(PE_production *gram)
{
	PE_grammar	tmp_grammar = (PE_grammar)NULL ;
	PE_production	tmp;

	if( (gram==(PE_grammar *)NULL) || (*gram==(PE_grammar)NULL) ) {
		return;
	}

	GMO_make_production_status( gram );	/* compute index, erreichbar & term_ableitbar	*/
						/* of each production of *gram			*/

	tmp = *gram;
	while( tmp != (PE_production)NULL ) {
		PE_remove_production( gram, tmp );
		if( (tmp->term_ableitbar) ) {
			PE_insert_production( &tmp_grammar, tmp );
		} else {
			PE_dispose_production( &tmp );
		}
		tmp = *gram;
	}
	*gram = tmp_grammar;
}

/********************************************************************************/
/*										*/
/*-->	RED_link_isomorph_productions						*/
/*										*/
/*	PARAMETER:	PE_grammar   *gram	(VAR-Parameter!)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fasse die isomorphen Produktionen von '*gram' zusammen. */
/*										*/
/*	ERKLAERUNG:	Isomorphe Produktionen generieren keine echten		*/
/*			Ableitungsalternativen bei der Erzeugung eines Graphen	*/
/*			durch '*gram'.						*/
/*			Allerdings erhoehen sie die Anzahl der generierten	*/
/*			Parsing-Elemente beim Parsen enorm.			*/
/*			Deshalb ist es ratsam, isomorphe Prod.en vor dem Parsen */
/*			zusammenzufassen.					*/
/*										*/
/*			Das technische Resultat dieser Prozedur ist, das fuer	*/
/*			eine Menge von isomorphen Prod.en ein (1) Repraesentant */
/*			'RP' in '*gram' behalten wird und die anderen in	*/
/*			RP->isomorph_prods abgelegt werden.			*/
/*										*/
/********************************************************************************/

void	RED_link_isomorph_productions(PE_production *gram)
{
	PE_production	prod1, prod2, tmp;

	PE_for_all_productions( *gram, prod1 ) {
		prod2 = prod1->succ;
		while( prod2 != (PE_production)NULL ) {
			tmp = prod2->succ;
			if( GMO_isomorph_productions( prod1, prod2 ) ) {
				/********************************************************************************/
				/*			Layout Graph Grammars: BEGIN				*/
				/********************************************************************************/
				/****** Wichtig fuer mich: Verkettung der PE's fuer Vergleich ueber pe_iso ******/
				if( !ORGINAL_LAMSHOFT )
				{
					append_node_isomorphism( prod1, prod2 );
				}
				/********************************************************************************/
				/*			Layout Graph Grammars: END				*/
				/********************************************************************************/
				PE_remove_production( gram, prod2 );

				PE_insert_production( &(prod1->isomorph_prods), prod2 );

			}
			prod2 = tmp;
		}
	} PE_end_for_all_productions( *gram, prod1 );
}

/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mreduce								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Freduce							*/
/*m		-Ereduce							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dreduce							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Preduce							*/
/********************************************************************************/

