/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Egraph_op								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Fgraph_op								*/
/*										*/
/*	MODUL:	  graph_op							*/
/*										*/
/*	FUNKTION: Prozeduren und Funktionen, die als Tools auf Graphen, Knoten, */
/*		  Kanten usw. arbeiten. 					*/
/*										*/
/********************************************************************************/

#include "misc.h"
#include "types.h"
#include "lab_int.h"

#include "graph_op.h"

#define SUCC( direction, pe ) \
	((direction) ? (pe->succ) : (pe->pre))

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*-->@	-Pgraph_op

	void		GPO_unmark_PE_list			( PE_list plist,
								  gpo_move_direction dir )
							  
	Parsing_element GPO_next_fitting_pe			( PE_list plist,
								  Parsing_element pe,
								  gpo_move_direction dir )
	
	int		GPO_get_first_isomorph_node_overlay	( PE_list plist, pgraph,
								  gpo_move_direction dir )
							  
	int		GPO_get_next_isomorph_node_overlay	( PE_list plist, pgraph,
								  gpo_move_direction dir )

	void		GPO_print_number_of_isomorph_overlays	( PE_list plist, pgraph )
							  
	void		GPO_unmark_PE_edgelist			( PE_edgelist elist )
	int		GPO_find_isomorph_edge			( PE_edgelist search_list,
								  PE_edge e )
							  
	int		GPO_check_graphedges_with_isoedges	( PE_list graph )
	int		GPO_check_isoedges_with_graphedges	( PE_list graph )
	int		GPO_check_edge_isomorphy		( PE_list graph )
	
	void		GPO_unmark_embeddings			( PE_embedding emb_list )
	int		GPO_all_embeddings_marked		( PE_embedding emb_list )
	int		GPO_find_isomorph_embedding		( PE_embedding emb_list,
								  PE_embedding emb )
								  
	int		GPO_check_embedding_isomorphy		( PE_list graph )
	int		GPO_check_isomorph_embeddings		( Parsing_element pe1, pe2 )

	int		GPO_check_graph_labels			( PE_list graph )

	int		GPO_check_pe_isomorphism		( Parsing_element pe1, pe2 )
	Parsing_element GPO_find_isomorph_pe			( PE_list list,
								  Parsing_element pe,
								  gpo_move_direction dir )
	void		GPO_add_to_isomorph_pes 		( PE_list list,
								  Parsing_element pe )
**/

/********************************************************************************/
/*										*/
/*-->	GPO_unmark_PE_list							*/
/*										*/
/*	PARAMETER:	1. PE_list		plist				*/
/*			2. gpo_move_direction	dir				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche Markierung der Parsing-Elemente von 'plist'	*/
/*			gemaess 'dir'.						*/
/*			Falls fuer ein markiertes PE  PE->pe_iso != NULL :	*/
/*			Loesche auch die Markierung von PE->pe_iso.		*/
/*										*/
/*	ERKLAERUNG:	Da 'plist' auch in der Mitte einer PE_list stehen kann, */
/*			erhaelt 'dir' folgende Bedeutung:			*/
/*										*/
/*			dir == FORWARD : Loesche Markierung der PE's ab 	*/
/*					 (einchliesslich) plist.		*/
/*										*/
/*			dir == BACKWARD : Loesche Markierung der PE's bis	*/
/*					 (einschliesslich) plist.		*/
/*										*/
/*	BESONDERES:   - Falls die Markierung eines PE 'FIXED' ist, so wird sie	*/
/*			NICHT geloescht.					*/
/*		      - Immer wenn die Markierung eines PE geloescht wird, wird */
/*			auch PE->pe_iso auf NULL gesetzt.			*/
/*										*/
/********************************************************************************/

void	GPO_unmark_PE_list(struct parsing_element *plist, int direction)
{
	while( plist != (PE_list)NULL ) {
		if( PE_IS_MARKED( plist ) ) {
			if( plist->pe_iso!= NULL ) {
				if( PE_IS_MARKED(plist->pe_iso) ) {
					PE_UNMARK( plist->pe_iso );
					plist->pe_iso->pe_iso = NULL;
				}
			} else {
				ERROR( "Warning from GPO_unmark_PE_list: PE marked, but its pe_iso == NULL!\n" );
			}
			PE_UNMARK( plist );
			plist->pe_iso = NULL;
		}
		plist = SUCC( direction, plist );
	}
}

/********************************************************************************/
/*										*/
/*-->	GPO_unmark_PE_edgelist							*/
/*										*/
/*	PARAMETER:	PE_edgelist	elist					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche Markierung aller PE_edge's von 'elist'. 	*/
/*										*/
/********************************************************************************/

void	GPO_unmark_PE_edgelist(PE_edge elist)
{
	while( elist != (PE_edgelist)NULL ) {
		EDGE_UNMARK( elist );
		elist = elist->succ;
	}
}

/********************************************************************************/
/*										*/
/*-->	GPO_unmark_embeddings							*/
/*										*/
/*	PARAMETER:	PE_embedding	emblist 				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche Markierung aller PE_embedding's von 'emblist'.	*/
/*										*/
/********************************************************************************/

void	GPO_unmark_embeddings(PE_embedding emblist)
{
	while( emblist != (PE_embedding)NULL ) {
		EMB_UNMARK( emblist );
		emblist = emblist->succ;
	}
}

/********************************************************************************/
/*										*/
/*-->	GPO_find_isomorph_embedding						*/
/*										*/
/*	PARAMETER:	1. PE_embedding emblist 				*/
/*			2. PE_embedding emb					*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	suche in 'emblist' nach einer unmarkierten, zu 'emb'	*/
/*			isomorphen Einbettungsregel.				*/
/*			Falls eine gefunden wird: Markiere diese und gib TRUE	*/
/*			zurueck. Ansonsten Rueckgabe FALSE.			*/
/*										*/
/*	ERKLAERUNG:	zwei PE_embedding's emb1 und emb2 sind zueinander	*/
/*			isomorph gdw. (SEMATISCH betrachtet):			*/
/*										*/
/*			1. emb1->nodelabel	== emb2->nodelabel		*/
/*			2. emb1->edgedir_pre	== emb2->edgedir_pre		*/
/*			3. emb1->edgelabel_pre	== emb2->edgelabel_pre		*/
/*			4. emb1->edgedir_post	== emb2->edgedir_post		*/
/*			5. emb1->edgelabel_post == emb2->edgelabel_post 	*/
/*										*/
/********************************************************************************/

int	GPO_find_isomorph_embedding(PE_embedding emblist, PE_embedding emb)
{
	if( emb == NULL ) {
		return	TRUE;
	}
	
	while( emblist != NULL ) {
		if( EMB_IS_UNMARKED( emblist ) ) {
			if(	(emblist->edgedir_pre == emb->edgedir_pre)			&&
				(emblist->edgedir_post == emb->edgedir_post)			&&
				(!strcmp( emblist->edgelabel_pre, emb->edgelabel_pre )) 	&&
				(!strcmp( emblist->edgelabel_post, emb->edgelabel_post ))	&&
				(!strcmp( emblist->nodelabel, emb->nodelabel ))
			   ) {
				EMB_MARK( emblist );
				return TRUE;
			}
		}
		emblist = emblist->succ;
	}
	return FALSE;
}

/********************************************************************************/
/*										*/
/*-->	GPO_all_embeddings_marked						*/
/*										*/
/*	PARAMETER:	PE_embedding	emblist 				*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Ueberprueft, ob in der Liste 'emblist' alle Einbet-	*/
/*			tungsregeln markiert sind. Falls ja, genau dann 	*/
/*			Rueckgabe TRUE. 					*/
/*										*/
/********************************************************************************/

int	GPO_all_embeddings_marked(PE_embedding emblist)
{
	while( emblist != NULL ) {
		if( !EMB_IS_MARKED( emblist ) ) {
			return FALSE;
		}
		emblist = emblist->succ;
	}
	return TRUE;
}

/********************************************************************************/
/*										*/
/*-->	GPO_next_fitting_pe							*/
/*										*/
/*	PARAMETER:	1. PE_list		plist				*/
/*			2. Parsing_element	pe				*/
/*			3. gpo_move_direction	dir				*/
/*										*/
/*	ZURUECK:	Parsing_element oder NULL				*/
/*										*/
/*	AUFGABE:	Suche in 'plist' gemaess 'dir' nach einem unmarkierten	*/
/*			PE mit gleichem Label wie 'pe'. 			*/
/*										*/
/*			Falls ein solches existiert : Markieren und zurueck-	*/
/*			geben. Ansonsten Rueckgabe NULL.			*/
/*										*/
/*	BESONDERES:	Diese Funktion beruehrt weder pe->pe_iso noch		*/
/*			pe->pe_iso->iso_status noch pe->iso_status.		*/
/*			Alle diese Felder muessen je nach Bedarf neu gesetzt	*/
/*			werden. 						*/
/*										*/
/********************************************************************************/

Parsing_element GPO_next_fitting_pe(struct parsing_element *plist, Parsing_element pe, int direction)
{
	while( plist != (PE_list)NULL ) {
		if( PE_IS_UNMARKED( plist ) ) {
			if( !strcmp( plist->label, pe->label ) ) {
				PE_MARK( plist );
				plist->pe_iso = pe;
				return plist;
			}
		}
		plist = SUCC( direction, plist );
	}
	return NULL;
}	
	
/********************************************************************************/
/*										*/
/*-->	GPO_get_next_isomorph_node_overlay					*/
/*										*/
/*	PARAMETER:	1. PE_list		plist				*/
/*			2. PE_list		pgraph				*/
/*			3. gpo_move_direction	dir				*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Suche in 'plist' gemaess 'dir' den von 'pgraph' aus	*/
/*			gesehen naechsten knotenisomorphen Subgraphen.		*/
/*										*/
/*			Gdw. ein solcher existiert, wird TRUE zurueckgegeben.	*/
/*			Man kann dann den gefundenen Graphen ueber die		*/
/*			'..->pe_iso'-Felder der Knoten von 'pgraph' ansprechen. */
/*										*/
/*	BESONDERES:   - 'pgraph' MUSS vor dem ersten Aufruf dieser Funktion	*/
/*			mittels GPO_get_first_isomorph_node_overlay(..) 	*/
/*			initialisiert worden sein, sonst erhaelt man immer	*/
/*			FALSE.							*/
/*		      - Diese Funktion benoetigt die '..->pe_iso'- und		*/
/*			'..->iso_status'-Felder der PE's von 'plist' & 'pgraph' */
/*			D.h. dass diese Felder fuer eigene Zwecke tabu sind,	*/
/*			solange man diese Fkt. benutzt. 			*/
/*		      - Falls die Markierung eines Knotens von 'plist' oder	*/
/*			'pgraph' FIXED ist, wird dieser Knoten als nicht	*/
/*			existent behandelt.					*/
/*										*/
/*	ERKLAERUNG:	GPO_get_first_isom.. und GPO_get_next_isom.. haben den	*/
/*			Zweck, aus 'plist' gemaess 'dir' alle zu 'pgraph'	*/
/*			knotenisomorphen Subgraphen samt ihrer konkreten	*/
/*			Abbildung auf 'pgraph' aufzuspueren.			*/
/*										*/
/*			'Konkrete Abbildung' bedeutet, dass falls fuer einen	*/
/*			isomorphen Subgraphen mehrere Abbildungen auf 'pgraph'	*/
/*			existieren, diese auch als separate Faelle behandelt	*/
/*			werden. 						*/
/*										*/
/********************************************************************************/

int	GPO_get_next_isomorph_node_overlay(struct parsing_element *plist, struct parsing_element *pgraph, int direction)
              		/* where to search */
                	/* usually right side of a production */
   	          	/* direction to search in plist */
{
	PE_list tmp;
	PE_list stack;
	PE_list stack_level;
	
	stack = pgraph;
	do {
		if( stack == NULL ) {
			return FALSE;
		} else if( PE_IS_UNMARKED(stack) ) {
			stack = stack->succ;
		} else if( PE_IS_FIXED( stack ) ) {
			stack = stack->succ;
		} else {
			tmp = GPO_next_fitting_pe( stack->pe_iso, stack, direction );
			PE_UNMARK( stack->pe_iso );
			if( stack->pe_iso != NULL ) {
				stack->pe_iso->pe_iso = NULL;
			}
			PE_UNMARK( stack );
			if( tmp != NULL ) {
				PE_MARK( stack );
				stack->pe_iso = tmp;
				stack_level = stack;
				stack = stack->pre;
				while( stack != NULL ) {
					if( !PE_IS_FIXED( stack ) ) {
						tmp= GPO_next_fitting_pe( plist, stack, direction );
						if( tmp != (PE_list)NULL ) {
							PE_MARK( stack );
							stack->pe_iso = tmp;
						} else {
							break;
						}
					}
					stack = stack->pre; /* push on stack */
				}
	
				if( stack == (Parsing_element)NULL ) {	/* alle PE's markiert */
					return TRUE;
				} else {
				/**/	GPO_unmark_PE_list( stack_level->pre, BACKWARD );
					stack = stack_level;
				}
			} else {
				stack->pe_iso = NULL;
				stack = stack->succ;
			}
		}
	} while( TRUE );
}

/********************************************************************************/
/*										*/
/*-->	GPO_print_number_of_isomorph_overlays					*/
/*										*/
/*	PARAMETER:	1. PE_list	big_graph				*/
/*			2. PE_list	pgraph					*/
/*										*/
/*	AUFGABE:	Ausgabe von:						*/
/*			Anzahl der zu 'pgraph' knotenisomorphen Subgraphen	*/
/*			von 'big_graph'.					*/
/*										*/
/*	BESONDERES:	reine Testprozedur					*/
/*										*/
/********************************************************************************/

void	GPO_print_number_of_isomorph_overlays(struct parsing_element *big_graph, struct parsing_element *pgraph)
{
	int count = 0;
	Parsing_element pe;
	
	if( GPO_get_first_isomorph_node_overlay( big_graph, pgraph, FORWARD ) ) {
		count++;
		PE_for_all_parsing_elements( pgraph, pe ) {
			if( PE_IS_FIXED( pe ) ) {
				printf( "FIXED	 " );
			} else {
				printf( "%s-%d	 ", pe->pe_iso->label, pe->pe_iso->nummer );
			}
		} PE_end_for_all_parsing_elements( pgraph, pe );
		printf( "\n" );
		while( GPO_get_next_isomorph_node_overlay( big_graph, pgraph, FORWARD ) ) {
			count++;
			PE_for_all_parsing_elements( pgraph, pe ) {
				if( PE_IS_FIXED( pe ) ) {
					printf( "FIXED	 " );
				} else {
					printf( "%s-%d	 ", pe->pe_iso->label, pe->pe_iso->nummer );
				}
			} PE_end_for_all_parsing_elements( pgraph, pe );
			printf( "\n" );
		}
	}
	printf( "\nGPO: found %d isomorph overlays.\n", count );
}
		
/********************************************************************************/
/*										*/
/*-->	GPO_get_first_isomorph_node_overlay					*/
/*										*/
/*	PARAMETER:	1. PE_list		plist				*/
/*			2. PE_list		pgraph				*/
/*			3. gpo_move_direction	dir				*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Suche in 'plist' gemaess 'dir' den ersten zu 'pgraph'	*/
/*			knotenisomorphen Subgraphen.				*/
/*										*/
/*			Gdw. ein solcher existiert, wird TRUE zurueckgegeben.	*/
/*			Man kann dann den gefundenen Graphen ueber die		*/
/*			'..->pe_iso'-Felder der Knoten von 'pgraph' ansprechen. */
/*										*/
/*	BESONDERES:   - Diese Funktion dient zur Initialisierung von		*/
/*			GPO_get_next_isomorph_node_overlay(..). 		*/
/*		      - Diese Funktion benoetigt die '..->pe_iso'- und		*/
/*			'..->iso_status'-Felder der PE's von 'plist' & 'pgraph' */
/*			D.h. dass diese Felder fuer eigene Zwecke tabu sind,	*/
/*			solange man diese Fkt. benutzt. 			*/
/*		      - Falls die Markierung eines Knotens von 'plist' oder	*/
/*			'pgraph' FIXED ist, wird dieser Knoten als nicht	*/
/*			existent behandelt.					*/
/*										*/
/*	ERKLAERUNG:	GPO_get_first_isom.. und GPO_get_next_isom.. haben den	*/
/*			Zweck, aus 'plist' gemaess 'dir' alle zu 'pgraph'	*/
/*			knotenisomorphen Subgraphen samt ihrer konkreten	*/
/*			Abbildung auf 'pgraph' aufzuspueren.			*/
/*										*/
/*			'Konkrete Abbildung' bedeutet, dass falls fuer einen	*/
/*			isomorphen Subgraphen mehrere Abbildungen auf 'pgraph'	*/
/*			existieren, diese auch als separate Faelle behandelt	*/
/*			werden. 						*/
/*										*/
/*										*/
/********************************************************************************/

int	GPO_get_first_isomorph_node_overlay(struct parsing_element *plist, struct parsing_element *pgraph, int direction)
              	/* where to search */
                /* usually right side of a production */
   	          
{
	PE_list tmp;
	PE_list stack_bottom;
	PE_list pe;
	
/*
	/+* Loesche Markierungen von plist. Richtung gemaess direction *+/
	GPO_unmark_PE_list( plist, direction );
*/
	
	/* Loesche Markierungen von pgraph */
	GPO_unmark_PE_list( pgraph, FORWARD );

	/* suche erste isomorphe Knotenueberdeckung :
	   Dabei werden die Knoten von pgraph in umgekehrter Reihenfolge belegt */
	
	stack_bottom = NULL;
	PE_for_all_parsing_elements( pgraph, pe ) {
		if( !PE_IS_FIXED( pe ) ) {
			stack_bottom = pe;
		}
	} PE_end_for_all_parsing_elements( pgraph, pe );

	if( stack_bottom == NULL ) { /* all elements are fixed */
		return TRUE;
	}
	while( stack_bottom != NULL ) {
		if( !PE_IS_FIXED( stack_bottom ) ) {
			tmp= GPO_next_fitting_pe( plist, stack_bottom, direction );
			if( tmp != (PE_list)NULL ) {
				PE_MARK( stack_bottom );
				stack_bottom->pe_iso = tmp;
			} else {
				break;
			}
		}
		stack_bottom = stack_bottom->pre; /* push on stack */
	}
	
	if( stack_bottom == (Parsing_element)NULL ) {	/* alle PE's markiert */
		return TRUE;
	} else {	/* fuer pe kein passendes PE aus plist gefunden */
			/* ==> alle Markierungen wieder loeschen	*/
		GPO_unmark_PE_list( pgraph, FORWARD );
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	GPO_find_isomorph_edge							*/
/*										*/
/*	PARAMETER:	1. PE_edgelist	search_list				*/
/*			2. PE_edge	e					*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	suche in 'search_list' nach einer unmarkierten, zu 'e'	*/
/*			isomorphen Kante.					*/
/*			Falls eine gefunden wird: Markiere diese und gib TRUE	*/
/*			zurueck. Ansonsten Rueckgabe FALSE.			*/
/*										*/
/*	ERKLAERUNG:	zwei PE_edge's e1 und e2 sind zueinander isomorph gdw.	*/
/*			(SEMATISCH betrachtet): 				*/
/*										*/
/*			1. e1->label == e2->label				*/
/*			2. e1->dir   == e2->dir 				*/
/*		    und 3. e1->partner isomorph zu e2->partner			*/
/*			   ( d.h.						*/
/*			     falls e1->partner markiert:			*/
/*			       e1->partner->pe_iso == e2->partner   u. umgekehrt*/
/*			     falls e1->partner nicht markiert:			*/
/*			       e1->partner == e2->partner			*/
/*			    )							*/
/*										*/
/********************************************************************************/

int	GPO_find_isomorph_edge(PE_edge search_list, PE_edge e)
{
	Parsing_element edge_partner;
	
	if( e == NULL ) {
		return FALSE;
	}
	if( PE_IS_UNMARKED( e->partner ) ) {
		edge_partner = e->partner;
	} else {
		edge_partner = e->partner->pe_iso;
	}
	while( search_list != NULL ) {
		if( EDGE_IS_UNMARKED( search_list ) ) {
			if( (search_list->dir==e->dir) && (search_list->partner == edge_partner) ) {
				if( (e->dir == BLOCKING) || !strcmp(search_list->label,e->label) ) {
					EDGE_MARK( search_list );
					return TRUE;
				}
			}
		}
		search_list = search_list->succ;
	}
	return FALSE;
}

/********************************************************************************/
/*										*/
/*-->	GPO_check_graphedges_with_isoedges					*/
/*										*/
/*	PARAMETER:	PE_list graph						*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste, ob fuer jede Kante von 'graph' eine isomorphe	*/
/*			Kante im isomorphen Graphen vorhanden ist.		*/
/*										*/
/*	BESONDERES:   - alle Knoten von 'graph' muessen markiert sein ( d.h.	*/
/*			in den ..->pe_iso's der Graphknoten muss ein knoten-	*/
/*			isomorpher Graph spezifiziert sein).			*/
/*		      - auch mit 'FIXED' markierte Knoten werden hier		*/
/*			beruecksichtigt.					*/
/*										*/
/********************************************************************************/

int	GPO_check_graphedges_with_isoedges(struct parsing_element *graph)
{
	Parsing_element pe;
	PE_edge 	e;
	
	if( graph == NULL ) {
		return TRUE;
	}
	
	PE_for_all_parsing_elements( graph, pe ) {
		if( PE_IS_UNMARKED( pe ) ) {
			ERROR( "GPO_check_graphedges... : inconsistent graph as parameter!\n" );
			return FALSE;
		} else {
			GPO_unmark_PE_edgelist( pe->pe_iso->edges );
			PE_for_all_edges( pe, e ) {
				if( !GPO_find_isomorph_edge( pe->pe_iso->edges , e ) ) {
					GPO_unmark_PE_edgelist( pe->pe_iso->edges );
					return FALSE;
				}
			} PE_end_for_all_edges( pe, e );
			GPO_unmark_PE_edgelist( pe->pe_iso->edges );
		}
	} PE_end_for_all_parsing_elements( graph, pe );
	return TRUE;
}

/********************************************************************************/
/*										*/
/*-->	GPO_check_isoedges_with_graphedges					*/
/*										*/
/*	PARAMETER:	PE_list graph						*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste, ob fuer jede Kante des zu 'graph' knotenisom.	*/
/*			(Sub)Graphen eine isomorphe Kante in 'graph' existiert. */
/*										*/
/*	BESONDERES:   - alle Knoten von 'graph' muessen markiert sein ( d.h.	*/
/*			in den ..->pe_iso's der Graphknoten muss ein knoten-	*/
/*			isomorpher Graph spezifiziert sein).			*/
/*		      - auch mit 'FIXED' markierte Knoten werden hier		*/
/*			beruecksichtigt.					*/
/*		      - Falls der zu 'graph' knotenisom. Graph ein Subgraph 'sb'*/
/*			einer PE_list 'pl' ist, duerfen in 'pl' nur die Knoten	*/
/*			aus 'sb' markiert sein, damit diese Funktion 100%-ig	*/
/*			korrekt arbeitet.					*/
/*										*/
/********************************************************************************/

int	GPO_check_isoedges_with_graphedges(struct parsing_element *graph)
{
	Parsing_element pe;
	PE_edge 	e;
	
	if( graph == NULL ) {
		return TRUE;
	}
	
	PE_for_all_parsing_elements( graph, pe ) {
		if( PE_IS_UNMARKED( pe ) ) {
			ERROR( "GPO_check_graphedges... : inconsistent graph as parameter!\n" );
			return FALSE;
		} else {
			GPO_unmark_PE_edgelist( pe->edges );
			PE_for_all_edges( pe->pe_iso, e ) {
				if( !GPO_find_isomorph_edge( pe->edges , e ) ) {
					GPO_unmark_PE_edgelist( pe->edges );
					return FALSE;
				}
			} PE_end_for_all_edges( pe->pe_iso, e );
			GPO_unmark_PE_edgelist( pe->edges );
		}
	} PE_end_for_all_parsing_elements( graph, pe );
	return TRUE;
}

/********************************************************************************/
/*										*/
/*-->	GPO_check_edge_isomorphy						*/
/*										*/
/*	PARAMETER:	PE_list graph						*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Ueberpruefe, ob die Kantenstruktur der knotenisom.	*/
/*			Graphen uebereinstimmt. 				*/
/*			Rueckgabe TRUE gdw. Uebereinstimmung			*/
/*										*/
/*	BESONDERES:   - Der zu 'graph' knotenisom. Graph wird durch die 	*/
/*			..->pe_iso's der Knoten von 'graph' spezifiziert.	*/
/*		      - Knoten mit Markierung 'FIXED' werden beruecksichtigt.	*/
/*		      - Diese Funktion benutzt direkt GPO_check_graphedges_...	*/
/*			und GPO_check_isoedges_... (naeheres siehe dort).	*/
/*										*/
/********************************************************************************/

int	GPO_check_edge_isomorphy(struct parsing_element *graph)
{
	if( graph == NULL ) {
		return FALSE;
	}
	return (GPO_check_graphedges_with_isoedges( graph ) &&
		GPO_check_isoedges_with_graphedges( graph ) );
}

/********************************************************************************/
/*										*/
/*-->	GPO_check_isomorph_embeddings						*/
/*										*/
/*	PARAMETER:	1. Parsing_element	pe1				*/
/*			2. Parsing_element	pe2				*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Ueberpruefe, ob die beiden Parsing-Elemente zueinander	*/
/*			isomorphe Mengen von Einbettungsregeln haben.		*/
/*			Falls ja, genau dann Rueckgabe TRUE.			*/
/*										*/
/*	ERKLAERUNG:	Mengen von Einbettungsregeln sind zueinander isomorph	*/
/*				gdw.						*/
/*			Fuer jede Einbettungsregel einer Menge existiert	*/
/*			genau eine isomorphe Einbettungsregel in der anderen	*/
/*			Menge.							*/
/*										*/
/********************************************************************************/

int	GPO_check_isomorph_embeddings(Parsing_element pe1, Parsing_element pe2)
{
	PE_embedding	emb;
	
	GPO_unmark_embeddings( pe2->loc_embedding );	
	PE_for_all_embeddings( pe1, emb ) {
		if( !GPO_find_isomorph_embedding( pe2->loc_embedding, emb ) ) {
			GPO_unmark_embeddings( pe2->loc_embedding );
			return FALSE;
		}
	} PE_end_for_all_embeddings( pe1, emb );
	if( GPO_all_embeddings_marked( pe2->loc_embedding ) ) {
		return TRUE;
	} else {
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	GPO_check_embedding_isomorphy						*/
/*										*/
/*	PARAMETER:	PE_list graph						*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste fuer alle Knoten pe von graph, ob die beiden	*/
/*			zueinander isomorphen Knoten pe und pe->pe_iso auch	*/
/*			isomorphe Mengen von Einbettungsregeln haben.		*/
/*			Falls dies fuer alle pe von 'graph' gilt, genau dann	*/
/*			Rueckgabe TRUE. 					*/
/*										*/
/*	BESONDERES:   - alle Knoten von 'graph' muessen markiert sein ( d.h.	*/
/*			in den ..->pe_iso's der Graphknoten muss ein knoten-	*/
/*			isomorpher Graph spezifiziert sein).			*/
/*		      - auch mit 'FIXED' markierte Knoten werden hier		*/
/*			beruecksichtigt.					*/
/*										*/
/********************************************************************************/

int	GPO_check_embedding_isomorphy(struct parsing_element *graph)
{
	Parsing_element pe;
	
	PE_for_all_parsing_elements( graph, pe ) {
		if( PE_IS_UNMARKED( pe ) ) {
			ERROR( "GPO_check_embedding_isom... : inconsistent graph as parameter!\n" );
			return FALSE;
		}	
		if( !GPO_check_isomorph_embeddings( pe, pe->pe_iso ) ) {
			return FALSE;
		}
	} PE_end_for_all_parsing_elements( graph, pe );
	return TRUE;
}	

/********************************************************************************/
/*										*/
/*-->	GPO_check_graph_labels							*/
/*										*/
/*	PARAMETER:	PE_list graph						*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste, ob alle Knotenlabel von 'graph' auch in		*/
/*			Nodelabel_list enthalten sind.				*/
/*			Teste, ob alle Kantenlabel von 'graph' auch in		*/
/*			Edgelabel_list	enthalten sind. 			*/
/*			Rueckgabe TRUE gdw. beide Bedingungen erfuellt. 	*/
/*										*/
/*	BESONDERES:   - Nodelabel_list und Edgelabel_list sind definiert in	*/
/*			Modul "lab_int".					*/
/*		      - Durch diese Prozedur werden auch die Labelnummern der	*/
/*			Knoten und Kanten von 'graph' gesetzt.			*/
/*		      - Der Rueckgabewert FALSE hat zwei moegliche Ursachen :	*/
/*			1. es wurde noch keine Grammatik eingelesen.		*/
/*			2. 'graph' enthaelt Labels, die nicht durch die 	*/
/*			   aktuelle Grammatik erzeugt werden koennen.		*/
/*			   ( d.h. 'graph' kann ebenfalls nicht durch die	*/
/*			    Grammatik erzeugt werden ! )			*/
/*										*/
/********************************************************************************/

int	GPO_check_graph_labels(struct parsing_element *graph)
{
	Parsing_element pe;
	PE_edge 	edge;
	int		all_ok;
	
	all_ok = TRUE;
	PE_for_all_parsing_elements( graph, pe ) {
		pe->label_num = LI_get_number_of_nodelabel( pe->label );
		if( pe->label_num == NOT_IN_LIST ) {
			all_ok = FALSE;
		}
		PE_for_all_edges( pe, edge ) {
			edge->label_num = LI_get_number_of_edgelabel( edge->label );
			if( edge->label_num == NOT_IN_LIST ) {
				all_ok = FALSE;
			}
		} PE_end_for_all_edges( pe, edge );
	} PE_end_for_all_parsing_elements( graph, pe );
	
	return	all_ok;
}

/********************************************************************************/
/*										*/
/*-->	GPO_check_pe_isomorphism						*/
/*										*/
/*	PARAMETER:	1. Parsing_element pe1					*/
/*			2. Parsing_element pe2					*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste Isomorphie von zwei Parsing-Elementen.		*/
/*										*/
/*	ERKLAERUNG:	Zwei PE's sind zueinander isomorph (per Def.)		*/
/*			gdw.							*/
/*			1. pe1->label	   == pe2->label			*/
/*			2. pe1->gnode_set  == pe2->gnode_set			*/
/*			3. pe1->lost_edges == pe2->lost_edges			*/
/*			4. isomorphe Kanteneinbettung von pe1 und pe2 in der	*/
/*			   Parsing-Tabelle.					*/
/*										*/
/*	BESONDERES:	Falls die Graph-Grammatik boundary oder kantenerhaltend */
/*			ist, reichen obige Bedingungen aus fuer vollstaendig	*/
/*			isomorphes Verhalten der beiden PE's bezueglich des	*/
/*			Parsers. D.h. fuer jedes aus pe1 erzeugte PE existiert	*/
/*			ein isomorphes aus pe2 erzeugtes PE (und umgekehrt).	*/
/*			Falls die Grammatik keine der beiden Bedingungen	*/
/*			erfuellt, koennen die aus pe1 und pe2 erzeugten 	*/
/*			PE-Mengen durchaus verschieden sein.			*/
/*										*/
/********************************************************************************/

int	GPO_check_pe_isomorphism(Parsing_element pe1, Parsing_element pe2)
{
	PE_edge e;
	int	result;
	
	if( pe1->label_num != pe2->label_num ) {
		return FALSE;
	}
	if(	!BS_equal_sets( pe1->gnode_set, pe2->gnode_set )	||
		!BS_equal_sets( pe1->lost_edges, pe2->lost_edges )
	  ) {
		return FALSE;
	}

	GPO_unmark_PE_edgelist( pe1->edges );
	PE_for_all_edges( pe2, e ) {
		if( !GPO_find_isomorph_edge( pe1->edges, e ) ) {
			result = FALSE;
			goto	check_finish;
		}
	} PE_end_for_all_edges( pe2, e );
	PE_for_all_edges( pe1, e ) {
		if( EDGE_IS_UNMARKED( e ) ) {
			result = FALSE;
			goto	check_finish;
		}
	} PE_end_for_all_edges( pe1, e );
	
	result = TRUE;
	
    check_finish: /* we have to clean up the edge marks */

	GPO_unmark_PE_edgelist( pe1->edges );
	return result;
}

/********************************************************************************/
/*										*/
/*-->	GPO_find_isomorph_pe							*/
/*										*/
/*	PARAMETER:	1. PE_list		list				*/
/*			2. Parsing_element	pe				*/
/*			3. gpo_move_direction	dir				*/
/*										*/
/*	ZURUECK:	NULL od. Parsing_element				*/
/*										*/
/*	AUFGABE:	Suche in 'list' gemaess 'dir' nach einem zu 'pe'	*/
/*			isomorphen Parsing-Element. Falls ein solches gefunden	*/
/*			wird, wird dieses zurueckgegeben, andernfalls NULL.	*/
/*										*/
/*	BESONDERES:	Benutzt GPO_check_pe_isomorphism(..)			*/
/*										*/
/********************************************************************************/

Parsing_element GPO_find_isomorph_pe(struct parsing_element *list, Parsing_element pe, int direction)
{
	if( (list==NULL) || (pe==NULL) ) {
		return NULL;
	}
	while( list != NULL ) {
		if( GPO_check_pe_isomorphism( list, pe ) ) {
			return list;
		}
		list = SUCC( direction, list );
	}
	return NULL;
}

/********************************************************************************/
/*										*/
/*-->	GPO_add_to_isomorph_pes 						*/
/*										*/
/*	PARAMETER:	1. PE_list	    *list   (VAR-Parameter)		*/
/*			2. Parsing_element  pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	1. Entferne alle Kanten von pe				*/
/*			2. Loesche gnode_set und lost_edges von pe		*/
/*			3. Fuege pe in list ein 				*/
/*										*/
/*	BESONDERES:	pe darf bei Aufruf dieser Prozedur in keiner Liste	*/
/*			mehr enthalten sein					*/
/*			( ==> vorher u.U. PE_remove_parsing_element(..) )	*/
/*										*/
/*	ERKLAERUNG:	Diese Prozedur dient zum Zusammenfassen isomorpher PE's */
/*			Da solche PE's isomorphe Kanten, gnode_set's und	*/
/*			lost_edges' haben, werden diese bei einem der beiden	*/
/*			geloescht.						*/
/*										*/
/********************************************************************************/

void	GPO_add_to_isomorph_pes(struct parsing_element **list, Parsing_element pe)
        	      				/* call BY REFERENCE! */
                   
{
	PE_edge e, tmp;
	
	if( (list==NULL) || (pe==NULL) ) {
		return;
	}
	
	/* save memory */
	
	e = pe->edges;
	while( e != NULL ) {
		tmp = e->succ;
		PE_remove_both_edges( e );
		PE_dispose_both_edges( &e );
		e = tmp;
	}
	pe->edges = NULL;
	
	BS_delete_set( &(pe->lost_edges) );
	BS_delete_set( &(pe->gnode_set) );
	
	/* insert pe to list */
	
	PE_insert_parsing_element( list, pe );
}

/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mgraph_op								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fgraph_op							*/
/*m		-Egraph_op							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dgraph_op							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pgraph_op							*/
/********************************************************************************/

