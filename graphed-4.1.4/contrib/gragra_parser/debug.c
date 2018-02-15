/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Edebug 								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*-->@	-Fdebug 								*/
/*										*/
/*	MODUL: debug								*/
/*										*/
/*	FUNKTION: Prozeduren zur Darstellung der zentralen Datenstrukturen des	*/
/*		  Parsers auf Textdateien bzw. Textbildschirmen.		*/
/*										*/
/*	ZWECK:	Ueberpruefung der Datenstrukturen bei der Fehlersuche.		*/
/*										*/
/********************************************************************************/

#include "misc.h"
#include "types.h"
#include "debug.h"

#define DBX_GRAPH	    TRUE
#define DBX_PRODUCTION	    FALSE

/********************************************************************************/
/*										*/
/*	exportierte Prozeduren/Funktionen :					*/
/*										*/
/********************************************************************************/
/*-->@	-Pdebug

void	DBX_fshow_PE_list		( FILE *file, PE_list plist, int gop );
void	DBX_fshow_PE_set		( FILE *file, PE_set  set );
void	DBX_fshow_Parsing_element	( FILE *file, Parsing_element pe, int gop );

					dabei gop E { DBX_GRAPH, DBX_PRODUCTION }

void	DBX_fshow_PE_edge		( FILE *file, PE_edge edge, char *nodelabel);
void	DBX_fshow_embedding		( FILE *file, PE_embedding emb );
void	DBX_fshow_production		( FILE *file, PE_production prod );
void	DBX_fshow_grammar		( FILE *file, PE_grammar gram );
void	DBX_fshow_embedge_choices	( FILE *file, Parsing_element pe );

**/

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_PE_list							*/
/*										*/
/*	PARAMETER:	1. FILE 	*file					*/
/*			2. PE_list	plist					*/
/*			3. (bool)int	graph_or_production			*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Darstellung der Parsingelement-Liste 'plist' in Text-	*/
/*			form auf dem Stream 'file'.				*/
/*			'graph_or_production' kann die definierten Werte	*/
/*			DBX_GRAPH bzw. DBX_PRODUCTION annehmen. 		*/
/*			Der Parameter gibt dabei an, ob 'plist' einen normalen	*/
/*			Graphen (DBX_GRAPH) oder die rechte Seite einer 	*/
/*			Produktion (DBX_PRODUCTION) darstellt.			*/
/*										*/
/*			Der Unterschied der beiden Darstellungsarten besteht	*/
/*			darin, dass im Fall DBX_GRAPH die ..->gnode_set's	*/
/*			der Parsingelemente dargestellt werden und bei		*/
/*			DBX_PRODUCTION die Einbettungsregeln ..->loc_embedding. */
/*										*/
/********************************************************************************/

void	DBX_fshow_PE_list(FILE *file, struct parsing_element *plist, int graph_or_production)
{
	Parsing_element pe;
	
	if( plist == (PE_list)NULL ) {
		fprintf( file, "PE_list/Graph: NULL pointer !\n" );
	} else {
		PE_for_all_parsing_elements( plist, pe ) {
			DBX_fshow_Parsing_element( file, pe, graph_or_production );
		} PE_end_for_all_parsing_elements( plist, pe );
	}
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_PE_set							*/
/*										*/
/*	PARAMETER:	1. FILE 	*file					*/
/*			2. PE_set	set					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Darstellung der Parsingelement-Menge 'set' in Text-	*/
/*			form auf dem Stream 'file'.				*/
/*										*/
/********************************************************************************/

void	DBX_fshow_PE_set(FILE *file, PE_set set)
{
	while( set != NULL ) {
		printf( "'%s'-%d ", set->pe->label, set->pe->nummer );
		set = set->succ;
	}
	printf( "\n" );
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_embedding							*/
/*										*/
/*	PARAMETER:	1. FILE 	*file					*/
/*			2. PE_embedding emb					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe einer Einbettungsregel 'emb' auf dem Stream	*/
/*			'file' in Textform.					*/
/*										*/
/********************************************************************************/

void	DBX_fshow_embedding(FILE *file, PE_embedding emb)
{
	if( emb != (PE_embedding)NULL ) {
		switch( emb->edgedir_pre ) {
			case IN:
				fprintf( file, "		<<< %s <<< %s	becomes ", emb->edgelabel_pre, emb->nodelabel);
				break;
			case OUT:
				fprintf( file, "		>>> %s >>> %s	becomes ", emb->edgelabel_pre, emb->nodelabel);
				break;
			case UNDIRECTED:
				fprintf( file, "		--- %s --- %s	becomes ", emb->edgelabel_pre, emb->nodelabel);
				break;
			default:
				fprintf( file, "		??? %s ??? %s	becomes ", emb->edgelabel_pre, emb->nodelabel);
				break;
		}
		switch( emb->edgedir_post ) {
			case IN:
				fprintf( file, "<<< %s <<< %s\n", emb->edgelabel_post, emb->nodelabel);
				break;
			case OUT:
				fprintf( file, ">>> %s >>> %s\n", emb->edgelabel_post, emb->nodelabel);
				break;
			case UNDIRECTED:
				fprintf( file, "--- %s --- %s\n", emb->edgelabel_post, emb->nodelabel);
				break;
			default:
				fprintf( file, "??? %s ??? %s\n", emb->edgelabel_post, emb->nodelabel);
				break;
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_Parsing_element						*/
/*										*/
/*	PARAMETER:	1. FILE 		*file				*/
/*			2. Parsing_element	pe				*/
/*			3. (bool)int		is_graphnode			*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe eines Parsingelements (Knoten) 'pe' auf dem	*/
/*			Stream 'file' in Textform.				*/
/*			Falls 'is_graphnode' TRUE (oder DBX_GRAPH) ist, wird	*/
/*			pe->gnode_set angezeigt. Im anderen Fall werden die	*/
/*			Einbettungsregeln pe->loc_embedding dargestellt.	*/
/*										*/
/********************************************************************************/

void	DBX_fshow_Parsing_element(FILE *file, Parsing_element pe, int is_graphnode)
{
	PE_edge 	edge;
	PE_embedding	emb;
	
	if( pe == (Parsing_element)NULL ) {
		fprintf( file, "Parsing_element: NULL pointer !\n" );
	} else {
		fprintf( file, "    Parsing_element: '%s'-%d\n", pe->label, pe->nummer );
		if( is_graphnode ) {
			fprintf( file, "      gnode_set = " );
			BS_fprintf( file, pe->gnode_set, AS_BITVECTOR );
			fprintf( file, "      lost_edges = " );
			BS_fprintf( file, pe->lost_edges, AS_BITVECTOR );
		} else { /* node in right side of production */
			fprintf( file, "      Embeddings:\n" );
			PE_for_all_embeddings( pe, emb ) {
				DBX_fshow_embedding( file, emb );
			} PE_end_for_all_embeddings( pe, emb );
		}
		fprintf( file, "      Edges:\n" );
		PE_for_all_edges( pe, edge ) {
			DBX_fshow_PE_edge( file, edge );
		} PE_end_for_all_edges( pe, edge );
	}
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_PE_edge							*/
/*										*/
/*	PARAMETER:	1. FILE 	*file					*/
/*			2. PE_edge	edge					*/
/*			3. char 	*nodelabel				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe einer Kante auf dem Stream 'file' in Textform.	*/
/*			Da 'edge' nur eine Manifestation der Kante ist braucht	*/
/*			man zur vollstaendigen Beschreibung noch einen Verweis	*/
/*			auf den Knoten, in dessen ..->edges 'edge' enthalten	*/
/*			ist.							*/
/*			Dies ist mittels 'nodelabel' realisiert.		*/
/*										*/
/********************************************************************************/

void	DBX_fshow_PE_edge(FILE *file, PE_edge edge)
{
	char	*p_lab;
	int	p_num;
	
	if( edge == (PE_edge)NULL ) {
		fprintf( file, "	edge: NULL pointer !\n" );
	} else {
		if( edge->partner != (Parsing_element)NULL ) {
			p_lab = edge->partner->label;
			p_num = edge->partner->nummer;
		} else {
			p_lab = "(partner == NULL)";
			p_num = 0;
		}
		switch( edge->dir ) {
			case IN:
				fprintf( file, "\t<<<< %s <<<<\t%s-%d", edge->label, p_lab, p_num );
				break;
			case OUT:
				fprintf( file, "\t>>>> %s >>>>\t%s-%d", edge->label, p_lab, p_num );
				break;
			case UNDIRECTED:
				fprintf( file, "\t---- %s ----\t%s-%d", edge->label, p_lab, p_num );
				break;
			case BLOCKING:
				fprintf( file, "\t- BLOCKING -\t%s-%d", p_lab, p_num );
				break;
			default:
				fprintf( file, "\t???? %s ????\t%s-%d", edge->label, p_lab, p_num );
				break;
		}
		if( edge->dual_edge == (PE_edge)NULL ) {
			fprintf( file, "   NO DUAL_EDGE DEFINED\n" );
		} else {
			fprintf( file, "\n" );
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_production							*/
/*										*/
/*	PARAMETER:	1. FILE 		*file				*/
/*			2. PE_production	prod				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe der Produktion 'prod' auf dem Stream 'file' in	*/
/*			Textformat.						*/
/*										*/
/********************************************************************************/

void	DBX_fshow_production(FILE *file, PE_production prod)
{
	char *erreichb, *term_ab;
	
	if( prod != (PE_production)NULL ) {
		fprintf( file, "Production : left_side = '%s'\n", prod->left_side );
		if( prod->erreichbar ) {
			erreichb = "";
		} else {
			erreichb = " nicht";
		}
		if( prod->term_ableitbar ) {
			term_ab = "";
		} else {
			term_ab = " nicht";
		}
		fprintf( file, "  index: %d , status: %s erreichbar,%s terminal ableitbar\n", prod->index, erreichb, term_ab );
		fprintf( file, "Right side:\n");
		DBX_fshow_PE_list( file, prod->right_side, DBX_PRODUCTION );
	}
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_grammar							*/
/*										*/
/*	PARAMETER:	1. FILE 	*file					*/
/*			2. PE_grammar	gram					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe der Grammatik 'gram' auf dem Stream 'file' in	*/
/*			Textformat						*/
/*										*/
/********************************************************************************/

void	DBX_fshow_grammar(FILE *file, PE_production gram)
{
	PE_production	prod;
	
	fprintf( file, "START GRAMMAR - - - - - - - - - - - - - -\n" );
	PE_for_all_productions( gram, prod ) {
		DBX_fshow_production( file, prod );
	} PE_end_for_all_productions( gram, prod );
	fprintf( file, "END GRAMMAR - - - - - - - - - - - - - - -\n" );
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_embedge_choices						*/
/*										*/
/*	PARAMETER:	1. FILE 		*file				*/
/*			2. Parsing_element	pe				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe der Menge der Einbettungskantenmengen von pe	*/
/*			auf dem File file.					*/
/*										*/
/********************************************************************************/

void	DBX_fshow_embedge_choices(FILE *file, Parsing_element pe)
{
	PE_embedge_choice	lauf;
	PE_edge 		e;
	
	if( pe == NULL ) {
		fprintf( file, "Parsing_element: NULL pointer !\n" );
	} else {
		fprintf( file, "    Parsing_element: '%s'-%d\n", pe->label, pe->nummer );
		fprintf( file, "\tedge embeddings:\n" );
		lauf = pe->embedge_choices;
		if( lauf == NULL ) {
			fprintf( file, "\n\tNONE!\n" );
		} else {
			while( lauf != NULL ) {
				fprintf( file, "\n\t(\n" );
				e = lauf->edges;
				while( e != NULL ) {
					DBX_fshow_PE_edge( file, e );
					e = e->succ;
				}
				fprintf( file, "\t)\n" );
				lauf = lauf->succ;
			}
		}
	}
}

/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mdebug 								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fdebug 							*/
/*m		-Edebug 							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Ddebug 							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pdebug 							*/
/********************************************************************************/

