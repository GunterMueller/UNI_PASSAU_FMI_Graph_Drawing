/* FERTIG 200393 */
/********************************************************************************/
/*-->@	-Eparser								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Fparser								*/
/*										*/
/*	MODUL:	parser.c							*/
/*										*/
/*	FUNKTION: Hauptmodul des Graphgrammatik-Parsers.			*/
/*										*/
/*		Dieses Modul enthaelt drei Gruppen von Prozeduren:		*/
/*										*/
/*		1. Prozeduren zum Initialisieren des Parsers, angepasst an	*/
/*		   Schnittstellen zu GraphEd.					*/
/*										*/
/*		2. Prozeduren zur (interaktiven) Steuerung des Parsers. 	*/
/*										*/
/*		3. Prozeduren zum Aufbau neuer Parsing-Elemente.		*/
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
#include "debug.h"
#include "graph_op.h"
#include "gram_opt.h"
#include "convert.h"
#include "reduce.h"

/*
#include <konfluenztest.h>
*/

/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/
#include "lp_datastruc.h"
				
/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/

/*
**  #include "main_sf.h" 	u.U. fuer ERROR-Messages
*/

#include "parser.h"

#ifdef ERROR
#	undef ERROR
#endif

#define ERROR( msg ) \
	PRS_info.message = msg; PRS_changed( CHG_MESSAGE )

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*--> -Pparser
:::::::::::: INITIALISIERUNG ::::::::::::

void		PRS_init			()
void		PRS_prepare_parser		()
void		PRS_init_lost_edges		( PE_list pgraph )
void		PRS_init_embeddings		( PE_grammar pgrammar )
void		PRS_unmark_graph_and_grammar	()

::::::::: interaktive STEUERUNG :::::::::

void		PRS_scan_graph			( Sgraph_proc_info info )
void		PRS_scan_grammar		( Sgragra_proc_info info )
int		PRS_reset			()
int		PRS_deactivate			()
void		PRS_pause			()
void		PRS_step			()

::::::::: neue PARSING-ELEMENTE :::::::::

int		PRS_get_next_fixed_position_of_cur_pe()
int		PRS_get_isomorph_node_overlay	()

void		PRS_make_lost_edges		( Parsing_element pe )
void		PRS_make_level			( Parsing_element pe )
int		PRS_check_internal_subedge_condition( pe1, edge_label_num, edge_dir, pe2 )

int		PRS_check_gnode_sets		( Parsing_element pe )		=> Knotenbedingung
int		PRS_check_internal_edge_condition( Parsing_element pe )		=> interne Kantenbedingung

die naechsten beiden Prozeduren: => externe Kantenbedingung, ZKE_tmp

void		PRS_compute_embedge_choices	( PE_edge edge,
						  PE_set right_elem,
						  PE_embedding emb_array[], 
						  Parsing_element pe, tnode,
						  int depth )
						  
void		PRS_make_edges_consistent	( Parsing_element old_pe, new_pe )

void		PRS_create_next_parsing_elements()
void		PRS_make_parsing_elements_of	( PE_set pset,
						  Parsing_element pe ,
						  int *count)

:::::::::::: HILFSPROZEDUREN ::::::::::::


int		PRS_edgespec			( int nodelabel_num, edgelabel_num,
						  edge_direction edge_dir )

void		PRS_kill_parsing_element	( Parsing_element *PE )
int		special_advance 		( PE_edge *edge,
						  PE_set  *right_elem )
void		copy_edgelist_to_embedgechoice	( PE_egdelist elist,
						  PE_embedge_choice *embchoice )

void		DBX_fshow_lost_edges		( FILE *file,
						  Bitset set )

int		PRS_is_start_element		( Parsing_element pe )
void		PRS_check_start_elements	()

PE_embedding	*PRS_create_emb_array		()
void		PRS_delete_emb_array		( PE_embedding *(array[]) )

char		*PRS_status_string		()
int		EMB_SPEC			( PE_edge edge )
void		PRS_prepare_edges		( Parsing_element new_pe, target_pe )
void		PRS_update_insert_pe		()

**/

/********************************************************************************/
/*										*/
/*-->	PRS_status_string							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	char *							*/
/*										*/
/*	AUFGABE:	Rueckgabe des augenblicklichen Zustands des Parsers	*/
/*			als String.						*/
/*										*/
/********************************************************************************/

static char	*prs_status_string[] = {
			"inactive",
			"reset",
			"running",
			"paused",
			"finished",
			"ERROR"
			};


char	*PRS_status_string(void)
{
	return	prs_status_string[PRS_info.status];
}

/********************************************************************************/
/*										*/
/*-->	PRS_scan_graph								*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info	info				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Einlesen des Eingabegraphen fuer den Parser.		*/
/*										*/
/*	BESONDERES:   - Der Eingabegraph wird als Sgraph ueber die zu GraphEd	*/
/*			gehoerige Schnittstelle uebergeben.			*/
/*		      - Der Zustand des Parsers (PRS_info) wird gemaess dem	*/
/*			Ergebnis der Prozedur modifiziert.			*/
/*										*/
/********************************************************************************/

void		PRS_scan_graph(Sgraph_proc_info info)
{
	if( PRS_info.status == PRS_RESET ) {
		CVT_convert_sgraph_to_PE_list( info->sgraph, &(PRS_info.pe.graph) );
		info->new_selected = SGRAPH_SELECTED_NONE;
		info->no_changes = TRUE;
		info->no_structure_changes = TRUE;
		if( PRS_info.pe.graph != NULL ) {
			PRS_info.graph_loaded = TRUE;
		} else {
			PRS_info.graph_loaded = FALSE;
		}
		PRS_info.graph_x_left = CVT_info.x_left;
		PRS_info.graph_y_top = CVT_info.y_top;
		PRS_info.graph_width = CVT_info.x_right - CVT_info.x_left;
		PRS_info.graph_height = CVT_info.y_bottom - CVT_info.y_top;
		PRS_info.graph_directed = CVT_info.graph_is_directed;
		PRS_info.graph_size = BS_get_size( BS_PARSER_GRAPH_SIZE );
		PRS_info.number_of_pes = PRS_info.graph_size;
		PRS_changed( CHG_GRAPH_LOAD );
		PRS_changed( CHG_PTAB_SIZE );
#		ifdef	DEBUG
			DBX_fshow_PE_list( stdout, (PRS_info.pe.graph), DBX_GRAPH );
#		endif
	} else {
		ERROR( "Parser must be reset before scanning a graph." );
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_is_start_element							*/
/*										*/
/*	PARAMETER:	Parsing_element pe					*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste, ob 'pe' die Wurzel eines Ableitungsbaums ist.	*/
/*										*/
/*	ERKLAERUNG:	Ergebnis ist TRUE gdw.					*/
/*			die Markierung von 'pe' gleich dem Startsymbol der	*/
/*			Graphgrammatik ist und					*/
/*			der gesamte Eingabegraph sich aus pe ableiten laesst.	*/
/*										*/
/********************************************************************************/

int		PRS_is_start_element(Parsing_element pe)
{
	Bitset	tmp_set = NULL;
	int	result;

	if( (pe==NULL) || (pe->label==NULL) ) {
		return FALSE;
	}

	if( pe->label_num != PRS_info.grammar_start_label_num ) {
		return FALSE;
	}

	BS_init_set( &tmp_set, PRS_info.graph_size );
	BS_reverse_set( tmp_set );
	result = BS_equal_sets( tmp_set, pe->gnode_set );
	BS_delete_set( &tmp_set );

	return result;
}

/********************************************************************************/
/*										*/
/*-->	PRS_check_start_elements						*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Update PRS_info.start_elements				*/
/*										*/
/*	ERKLAERUNG:	Falls PRS_info.current_pe ein (bisher) groesstes	*/
/*			(bzgl. Level) Parsing-Element oder sogar Wurzel eines	*/
/*			Ableitungsbaums ist, wird es in ..start_elements	*/
/*			aufgenommen oder ersetzt diese sogar.			*/
/*										*/
/********************************************************************************/

void	PRS_check_start_elements(void)
{
	if( PRS_is_start_element( PRS_info.current_pe ) ) {
		if( !PRS_info.graph_in_grammar ) {
			PE_delete_PE_set( &(PRS_info.start_elements) );
			PRS_info.graph_in_grammar = TRUE;
			PRS_info.message = "g in L(gg)";
			PRS_changed( CHG_MESSAGE );
			if( PRS_info.parser_stop_with_first_startnode ) {
				PRS_pause();
				PRS_info.no_pes_created = FALSE;
			}
		}
		PE_add_to_PE_set( &(PRS_info.start_elements), PRS_info.current_pe );
	} else {
		if( !PRS_info.graph_in_grammar ) {
			if(	(PRS_info.start_elements!=NULL) &&
				(PRS_info.current_pe->level > PRS_info.start_elements->pe->level)
			  ) {
				PE_delete_PE_set( &(PRS_info.start_elements) );
			}
			if(	(PRS_info.start_elements==NULL) ||
				(PRS_info.current_pe->level == PRS_info.start_elements->pe->level)
			   ) {
				PE_add_to_PE_set( &(PRS_info.start_elements), PRS_info.current_pe );
			}
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_scan_grammar							*/
/*										*/
/*	PARAMETER:	Sgragra_proc_info	info				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Einlesen der Graphgrammatik fuer den Parser.		*/
/*										*/
/*	BESONDERES:   - Die Grammatik wird als Sgragra ueber die zu GraphEd	*/
/*			gehoerige Schnittstelle uebergeben.			*/
/*		      - Die Grammatik wird gemaess den eingestellten Scan-	*/
/*			Options (CVT_info) reduziert.				*/
/*		      - Der Zustand des Parsers (PRS_info) wird gemaess dem	*/
/*			Ergebnis dieser Prozedur modifiziert.			*/
/*										*/
/********************************************************************************/

void		PRS_scan_grammar(Sgragra_proc_info info)
{
	int		test_result;
	PE_production	prod;
	
	if( PRS_info.status == PRS_RESET ) {
		if( info != (Sgragra_proc_info)NULL ) {
			
			PE_dispose_grammar( &(PRS_info.grammar) );
			PRS_info.grammar_loaded = FALSE;
			PRS_changed( CHG_GRAM_LOAD );
			
			/* check finite church rosser property */
		

/*	ACHTUNG: Der hier benutzte Konfluenztest von Martin Neuhaus
**		 arbeitet noch nicht korrekt!
**		 Deshalb ist dieser Programmabschnitt noch als Kommentar ausgeschlossen. 
**
**			if( konfluenztest_pre_check( info ) ) {
**				test_result = konfluent_sgragra_proc( info );
**				if( test_result == 0 ) {
**					ERROR( "grammar is not confluent!" );
**					return;
**				}
**				if( test_result != 1 ) {
**					ERROR( "Fehler im Konfluenztest (Martin Neuhaus)" );
**					printf( "%d\n", test_result );
**				/+	return; +/
**				}
**			} else {
**				ERROR( "konfluenztest_pre_check failed" );
**			}
*/
			
			/* ---- */
			
			CVT_convert_sgragra_to_PE_grammar( info->sgragra, &(PRS_info.grammar) );
			
			GMO_make_production_status( &(PRS_info.grammar) );
			if( CVT_info.grammar_reduce_productions ) {
				RED_reduce_productions( &(PRS_info.grammar) );
			}
			
			/* check if grammar contains lambda productions */
			
			test_result = FALSE;
			PE_for_all_productions( PRS_info.grammar, prod ) {
				if( prod->right_side == NULL ) {
					test_result = TRUE;
					break;
				}
			} PE_end_for_all_productions( PRS_info.grammar, prod );
			
			if( test_result ) {
				ERROR( "grammar contains lambda productions!" );
				PE_dispose_grammar( &(PRS_info.grammar) );
				return;
			}
			
			/* ---- */
			
			if( CVT_info.grammar_reduce_embeddings ) {
				RED_reduce_embeddings( (PRS_info.grammar) );
			}
			if( CVT_info.grammar_link_isomorph_productions ) {
				RED_link_isomorph_productions( &(PRS_info.grammar) );
			}
			GMO_make_node_and_edgelabel_list( (PRS_info.grammar) );

			if( PRS_info.grammar != NULL ) {
				PRS_info.grammar_loaded = TRUE;
				ERROR( "" );
			} else {
				ERROR( "(reduced) grammar is empty!" );
			}

			if( !CVT_info.grammar_is_boundary ) {
				PRS_info.grammar_boundary = GMO_check_boundary( PRS_info.grammar );
			} else {
				PRS_info.grammar_boundary = TRUE;
			}

			PRS_info.link_isomorph_pes = PRS_info.grammar_boundary;
			PRS_info.grammar_directed = CVT_info.grammar_is_directed;
			PRS_info.grammar_nodelabelset_size = LI_sizeof_nodelabel_list() + 1;
			PRS_info.grammar_edgelabelset_size = LI_sizeof_edgelabel_list() + 1;
			PRS_info.grammar_edgeset_size = 2 * ( LI_sizeof_nodelabel_list() + 1 )
							  * ( LI_sizeof_edgelabel_list() + 1 );
			PRS_info.grammar_start_label_num = LI_get_number_of_nodelabel( MISC_get_grammar_start_symbol() );

#ifdef	DEBUG
			DBX_fshow_grammar( stdout, (PRS_info.grammar) );
			printf( "Nodelabels are : " );
			LI_fshow_nodelabel_list( stdout );
			printf( "Edgelabels are : " );
			LI_fshow_edgelabel_list( stdout );
#endif
		}
	} else {
		ERROR( "Parser must be reset before scanning a grammar." );
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_reset								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ruecksetzen des Parsers.				*/
/*										*/
/*	BESONDERES:	Ohne Auswirkung, falls der Parser noch am Bearbeiten	*/
/*			einer Aufgabe ist. (...status == PRS_RUNNING )		*/
/*										*/
/*	ERKLAERUNG:	Ruecksetzen bedeutet Vorbereiten des Parsers auf einen	*/
/*			neuen Eingabegraphen (neue Aufgabe).			*/
/*			Dabei wird die eventuell noch vorhandene Parsingtabelle */
/*			geloescht und auch der Zustand des Parser re-		*/
/*			initialisiert.						*/
/*			Die aktuell geladene Grammatik bleibt unberuehrt.	*/
/*										*/
/********************************************************************************/

int	PRS_reset(void)
{
	if(	(PRS_info.status != PRS_RUNNING)
		) {
			PE_dispose_all_parsing_elements( &(PRS_info.pe.tab) );
			PRS_info.graph_loaded = FALSE;
			PRS_info.graph_in_grammar = FALSE;
			PE_delete_PE_set( &(PRS_info.start_elements) );
			PRS_info.status = PRS_RESET;
			PRS_info.message = "";
			PRS_info.number_of_pes = 0;
			PRS_changed( CHG_GRAPH_LOAD | CHG_STATUS | CHG_MESSAGE | CHG_PTAB_SIZE );
			return	TRUE;
	} else {
		ERROR( "Parser is running! Stop it before resetting" );
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_deactivate								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Deaktivieren des Parsers, falls er nicht mehr gebraucht */
/*			wird.							*/
/*										*/
/*	BESONDERES:	Ohne Auswirkung, falls der Parser noch am Bearbeiten	*/
/*			einer Aufgabe ist. (...status == PRS_RUNNING )		*/
/*										*/
/*	ERKLAERUNG:	Zusaetzlich zu PRS_reset() wird hier noch die aktuell	*/
/*			geladene Graphgrammatik geloescht.			*/
/*										*/
/********************************************************************************/

int	PRS_deactivate(void)
{
	if( PRS_info.status != PRS_RUNNING ) {
		PRS_reset();
		PE_dispose_grammar( &(PRS_info.grammar) );
		PRS_info.grammar_loaded = FALSE;
		PRS_info.current_prod = (PE_production)NULL;
		PRS_info.current_pe = (Parsing_element)NULL;
		PRS_info.status = PRS_INACTIVE;
		return TRUE;
	} else {
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_pause								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Stoppen des Parsers ohne Ruecksetzen.			*/
/*										*/
/*	BESONDERES:   - Funktioniert nur, wenn der Parser gerade arbeitet	*/
/*			(...status == PRS_RUNNING).				*/
/*		      - Ist nur dann ein sinnvolles Feature, wenn der Parser	*/
/*			quasiparallel laeuft.					*/
/*		      - Ist aber dann eine Moeglichkeit, den Parser in einen	*/
/*			'Step'-Modus zu versetzen.				*/
/*		      - wegen 'quasiparallel' siehe Stichwort 'PRS_PARALLEL'.	*/
/*										*/
/********************************************************************************/

void	PRS_pause(void)
{
	if( PRS_info.status == PRS_RUNNING ) {
		PRS_info.status = PRS_PAUSED;
		PRS_changed( CHG_STATUS );
	} else {
		ERROR( "Parser not running." );
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_step								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuehre einen INTERAKTIVEN Arbeitsschritt des Parsers	*/
/*			aus.							*/
/*										*/
/*	ERKLAERUNG:	Diese Prozedur gehoert zur Gruppe der (interaktiven)	*/
/*			Steuerprozeduren (s. -Fparser). Als solche ist sie	*/
/*			dafuer zustaendig, eine Reihe von Anweisungen zur	*/
/*			Erzeugung neuer PE's auszufuehren.			*/
/*			Im Gegensatz zu anderen Programmen ist der Parser so	*/
/*			ausgelegt, dass die gesamte Arbeit in eine Folge von	*/
/*			PRS_step's zerlegt wird, wobei zwischen je zwei dieser	*/
/*			PRS_step's fuer den Benutzer die Moeglichkeit besteht,	*/
/*			den Parser in einen anderen Zustand zu ueberfuehren	*/
/*			(z.B. terminieren oder Step-Modus).			*/
/*										*/
/*			Da PRS_step im wesentlichen ZENTRALE Arbeitsschritte	*/
/*			ausfuehrt, ist es dem Programmierer moeglich ueber die	*/
/*			Maechtigkeit (= Anzahl der zentr. ARbeitsschritte)	*/
/*			dieser Prozedur die Haeufigkeit der Benutzereingriffe	*/
/*			zu steuern.						*/
/*										*/
/*			Derzeit realisiert:					*/
/*			Fuehre solange zentr. Arbeitsschritte aus bis neue PE's */
/*			generiert wurden, hoechstens aber 200 Schritte. 	*/
/*										*/
/********************************************************************************/

void	PRS_step(void)
{
	int	count;

	count = 200;
	while( --count != 0 ) {
		PRS_create_next_parsing_elements();
		if( !PRS_info.no_pes_created || (PRS_info.status!=PRS_RUNNING) ) {
			break;
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_init								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Erstinitialisierung der Datenstruktur PRS_info. 	*/
/*										*/
/********************************************************************************/

void	PRS_init(void)
{
	static	int first = TRUE;

	if( first ) {
		PRS_info.status 	= PRS_INACTIVE;
		PRS_info.grammar_loaded = FALSE;
		PRS_info.graph_loaded	= FALSE;
		PRS_info.pe.graph	= NULL;
		PRS_info.grammar	= NULL;
		PRS_info.current_prod	= NULL;
		PRS_info.insert_pe	= NULL;
		PRS_info.current_pe	= NULL;
		PRS_info.start_elements = NULL;
		PRS_info.number_of_pes	= 0;
		PRS_info.message	= "";
		PRS_info.parser_stop_with_first_startnode = TRUE;

		PRS_info.graph_in_grammar = FALSE;
		PRS_info.link_isomorph_pes = FALSE;
		PRS_info.grammar_boundary = TRUE;
		PRS_info.status_changed = -1;

		PRS_info.pars_table	= NULL;

	}
}

/********************************************************************************/
/*										*/
/*-->	DBX_fshow_lost_edges							*/
/*										*/
/*	PARAMETER:	1. FILE   *file 					*/
/*			2. Bitset set						*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe der (..->lost_edges) 'set' auf 'file' in	*/
/*			geeigneter Textform.					*/
/*										*/
/*	BESONDERES:	Diese Prozedur ist wegen der Verwendung von		*/
/*			PRS_edgespec(..) nicht im debug-Modul enthalten.	*/
/*										*/
/********************************************************************************/

void	DBX_fshow_lost_edges(FILE *file, Bitset set)
{
	int i,j, tmp;

	for( i=1; i<PRS_info.grammar_nodelabelset_size; i++ ) {
		for( j=1; j<PRS_info.grammar_edgelabelset_size; j++ ) {
			tmp = PRS_edgespec( i, j, IN );
			if( BS_is_in_set( set, tmp ) ) {
				if( PRS_info.graph_directed ) {
					fprintf( file, "%s<%s ",LI_get_edgelabel_of_number(j), LI_get_nodelabel_of_number(i));
				} else {
					fprintf( file, "%s-%s ",LI_get_edgelabel_of_number(j), LI_get_nodelabel_of_number(i));
				}
			}
			tmp = PRS_edgespec( i, j, OUT ); /* tmp++ */
			if( PRS_info.graph_directed && BS_is_in_set( set, tmp )  ) {
				fprintf( file, "%s>%s ",LI_get_edgelabel_of_number(j), LI_get_nodelabel_of_number(i));
			}
		}
	}
	fprintf( file, "\n" );
}

/********************************************************************************/
/*										*/
/*-->	PRS_edgespec								*/
/*										*/
/*	PARAMETER:	1. int		   nodelabel_num			*/
/*			2. int		   edgelabel_num			*/
/*			3. edge_direction  edge_dir				*/
/*										*/
/*	ZURUECK:	Nummer der Kantenspezifikation fuer die ..->lost_edges	*/
/*										*/
/*	AUFGABE:	Injektive Abbildung einer Kantenspezifikation auf die	*/
/*			natuerlichen Zahlen (integer).				*/
/*										*/
/*	ERKLAERUNG:	Verwendung des Resultats als Elementnummer der Kanten-	*/
/*			spezifikation in den ..->lost_edges-Bitsets der PE's.	*/
/*										*/
/********************************************************************************/

int	PRS_edgespec(int nodelabel_num, int edgelabel_num, edge_direction edge_dir)
{
	register int	tmp = 0;

	if(	(edgelabel_num < 0)	||
		(nodelabel_num < 0)	||
		(edge_dir == UNDEFINED) ||
		(edge_dir == BLOCKING)
	  ) {
		return 0;
	}

	tmp = 2 * ( edgelabel_num * PRS_info.grammar_nodelabelset_size + nodelabel_num );
	if( edge_dir == OUT ) {
		tmp++;
	}

	return tmp;
}

/*
** | setzt voraus, dass pe bereits bis auf lost_edges vollstaendig kreiert ist, d.h.
** | pe->which_production und pe->right_side muessen vorhanden sein.
** | ausserdem: in right_side steht zu jedem ..->pe in ..->prod_iso der zu ..->pe
** v isomorphe Knoten der rechten Seite von which->production.
*/

/********************************************************************************/
/*										*/
/*-->	PRS_make_lost_edges							*/
/*										*/
/*	PARAMETER:	Parsing_element pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Berechne pe->lost_edges aus pe->right_side.		*/
/*										*/
/*	BESONDERES:	Falls pe nicht Level-0-Element ist, muss zur korrekten	*/
/*			Berechnung natuerlich pe->right_side existieren.	*/
/*			Ebenso muss pe->which_production bereits gesetzt sein.	*/
/*			Ausserdem muss fuer jedes Element von right_side auch	*/
/*			..->prod_iso gesetzt sein.				*/
/*										*/
/********************************************************************************/

void	PRS_make_lost_edges(Parsing_element pe)
{
	PE_set		lauf;
	int		tmp1, tmp2;
	PE_embedding	emb;

	if( !PRS_info.grammar_loaded || (pe==NULL) ) {
		return;
	}

	if( pe->lost_edges != NULL ) {
		BS_clear_set( (pe->lost_edges) );
	} else {
		if( !BS_init_set( &(pe->lost_edges), PRS_info.grammar_edgeset_size ) ) {
			PRS_info.status = PRS_ERROR;
		}
	}

	if( (pe->which_production == NULL) || (pe->right_side == NULL) ) {
		return;
	}

	BS_reverse_set( pe->lost_edges );
	lauf = pe->right_side;
	while( lauf != NULL ) {
		PE_for_all_embeddings( lauf->prod_iso, emb ) {
			tmp1 = PRS_edgespec( emb->nodelabel_num, emb->edgelabel_post_num, emb->edgedir_post );
			if( !BS_is_in_set( lauf->pe->lost_edges, tmp1 ) ) {
				tmp2 = PRS_edgespec( emb->nodelabel_num, emb->edgelabel_pre_num, emb->edgedir_pre );
				BS_exclude( pe->lost_edges, tmp2 );
			}
		} PE_end_for_all_embeddings( lauf->prod_iso, emb );
		lauf = lauf->succ;
	}
/*	DBX_fshow_lost_edges( stdout, pe->lost_edges );
*/
}

/********************************************************************************/
/*										*/
/*-->	PRS_init_lost_edges							*/
/*										*/
/*	PARAMETER:	PE_list pgraph						*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Initialsierung der ..->lost_edges des (Eingabe)Graphen	*/
/*			'pgraph'.						*/
/*										*/
/*	BESONDERES:	Da davon ausgegangen wird, dass alle PE's von 'pgraph'	*/
/*			Level-0-Elemente sind, werden alle ..->lost_edges auf	*/
/*			leere Menge gesetzt.					*/
/*										*/
/********************************************************************************/

void	PRS_init_lost_edges(struct parsing_element *pgraph)
{
	Parsing_element pe;

	PE_for_all_parsing_elements( pgraph, pe ) {
		PRS_make_lost_edges( pe );
	} PE_end_for_all_parsing_elements( pgraph, pe );
}

/********************************************************************************/
/*										*/
/*-->	PRS_init_embeddings							*/
/*										*/
/*	PARAMETER:	PE_grammar  pgrammar					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Praepariere die Einbettungsregeln von 'pgrammar' fuer	*/
/*			die Verwendung durch den Parser.			*/
/*										*/
/*	ERKLAERUNG:	Der Parser macht viel ueberfluessige Arbeit, wenn er	*/
/*			bei Produktionsknoten auf mehrfach vorhandene,		*/
/*			identische Einbettungsregeln stoesst.			*/
/*			Um dies zu vermeiden werden in einer solchen Situation	*/
/*			die identischen Regeln bis auf einen Repraesentanten	*/
/*			markiert.						*/
/*										*/
/********************************************************************************/

void	PRS_init_embeddings(PE_production pgrammar)
{
	Parsing_element pe;
	PE_embedding	emb, emb2;

	while( pgrammar != NULL ) {
		if( pgrammar->right_side != NULL ) {
			PE_for_all_parsing_elements( pgrammar->right_side, pe ) {
				PE_for_all_embeddings( pe, emb ) {
					if( EMB_IS_UNMARKED( emb ) ) {
						emb2 = emb->succ;
						while( emb2 != NULL ) {
							if(	(emb2->nodelabel_num==emb->nodelabel_num) &&
								(emb2->edgelabel_pre_num==emb->edgelabel_pre_num) &&
								(emb2->edgedir_pre==emb->edgedir_pre) &&
								(emb2->edgelabel_post_num==emb->edgelabel_post_num) &&
								(emb2->edgedir_post==emb->edgedir_post)
							  ) {
								EMB_MARK( emb2 );
							}
							emb2 = emb2->succ;
						}
					}
				} PE_end_for_all_embeddings( pe, emb );
			} PE_end_for_all_parsing_elements( pgrammar->right_side, pe );
		}
		pgrammar = pgrammar->succ;
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_check_gnode_sets							*/
/*										*/
/*	PARAMETER:	Parsing_element  pe					*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE.					*/
/*										*/
/*	AUFGABE:	Teste Knotenbedingung fuer 'pe' 			*/
/*										*/
/*	ERKLAERUNG:	Die Knotenbedingung besagt, dass fuer ein PE die	*/
/*			gnode_set's der rechten Seite paarweise disjunkt sein	*/
/*			muessen.						*/
/*										*/
/*	BESONDERES:	Parallel zum Test wird pe->gnode_set mit aufgebaut.	*/
/*			Falls der Test FALSE zurueckgibt ist dann pe->gnode_set */
/*			als ungueltig zu betrachten (d.h. eigentlich muesste	*/
/*			pe als Ganzes verworfen werden).			*/
/*										*/
/********************************************************************************/

int	PRS_check_gnode_sets(Parsing_element pe)
{
	PE_set	tmp;

	if( pe->gnode_set != (Bitset)NULL ) {
		BS_clear_set( pe->gnode_set );
	} else {
		BS_init_set( &(pe->gnode_set), PRS_info.graph_size );
	}

	tmp = pe->right_side;
	while( tmp != NULL ) {
		if( BS_empty_intersection( tmp->pe->gnode_set, pe->gnode_set ) ) {
			BS_union( tmp->pe->gnode_set, pe->gnode_set, pe->gnode_set );
		} else {
			return FALSE;
		}
		tmp = tmp->succ;
	}
	return TRUE;
}

/********************************************************************************/
/*										*/
/*-->	PRS_check_internal_subedge_condition					*/
/*										*/
/*	PARAMETER:	1. Parsing_element  pe1 				*/
/*			2. int		    edge_label_num			*/
/*			3. egde_dir	    edge_dir				*/
/*			4. Parsing_element  pe2 				*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste, ob Kante durch Level-0-Ableitung verschwindet.	*/
/*			Getestet wird die Kante zwischen 'pe1' und 'pe2' mit	*/
/*			Label 'edge_label_num' und 'edge_dir' bzgl. pe1.	*/
/*										*/
/*	BESONDERES:	Funktion nur korrekt, wenn Grammatik konfluent. 	*/
/*										*/
/*	ERKLAERUNG:	Kante 'verschwindet' heisst, dass durch diese Kante	*/
/*			bei Level-0-Ableitung von pe1 und pe2 keine Kante des	*/
/*			Eingabegraphen entstehen darf.				*/
/*										*/
/********************************************************************************/

int	PRS_check_internal_subedge_condition(Parsing_element pe1, int edge_label_num, edge_direction edge_dir, Parsing_element pe2)
{
	int	tmp;
	PE_set		lauf;
	PE_embedding	emb;

	lauf = pe1->right_side;
	while( lauf != NULL ) {
		PE_for_all_embeddings( lauf->prod_iso, emb ) {
			if(	(emb->nodelabel_num == pe2->label_num)	&&
				(emb->edgelabel_pre_num == edge_label_num) &&
				(emb->edgedir_pre == edge_dir)
			  ) {
				tmp = PRS_edgespec(	lauf->pe->label_num,
							emb->edgelabel_post_num,
							PE_inverse_dir( emb->edgedir_post )
						    );
				if( !BS_is_in_set( pe2->lost_edges, tmp ) ) {
					if( lauf->pe->level == 0 ) {
						return FALSE;
					} else {
						if( !PRS_check_internal_subedge_condition(	lauf->pe,
												emb->edgelabel_post_num,
												emb->edgedir_post,
												pe2
											  )
						  ) {
							return FALSE;
						}
					}
				}
			}
		} PE_end_for_all_embeddings( lauf->prod_iso, emb );
		lauf = lauf->succ;
	}
	return TRUE;
}

/********************************************************************************/
/*										*/
/*-->	PRS_check_internal_edge_condition					*/
/*										*/
/*	PARAMETER:	Parsing_element  pe					*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Teste interne Kantenbedingung fuer 'pe'.		*/
/*										*/
/*	ERKLAERUNG:	Interne Kantenbedingung fuer 'pe' besagt, dass, wenn	*/
/*			man die rechte Seite von pe->which_production gemaess	*/
/*			pe->right_side ableitet, genau der Eingabegraph, ein-	*/
/*			geschraenkt auf pe->gnode_set entsteht. 		*/
/*										*/
/********************************************************************************/

int	PRS_check_internal_edge_condition(Parsing_element pe)
{
	PE_set	lauf;
	int	tmp1, tmp2;
	PE_edge e;
	int	result;


	/* 1. Teste, ob die real vorhandene Kantenmenge eine Teilmenge
	      der Kantenmenge der rechten Seite von pe->which_production
	      ist.
	      Der Zugriff auf die rechte Seite erfolgt durch ..->prod_iso.
	*/
	lauf = pe->right_side;
	while( lauf != NULL ) {
		GPO_unmark_PE_edgelist( lauf->prod_iso->edges );
		PE_for_all_edges( lauf->pe, e ) {
			if( !PE_IS_UNMARKED( e->partner ) ) {
				if( !GPO_find_isomorph_edge( lauf->prod_iso->edges, e ) ) {
					result = FALSE;
					goto	PRS_check_finish;
					/* wir haben real eine Kante zuviel */
				}
			}
		} PE_end_for_all_edges( lauf->pe, e );
		lauf = lauf->succ;
	}

	/* 2. Teste, ob die nicht real vorhandenen Kanten der rechten Seite
	      von pe->which_production durch die Ableitung gemaess der
	      rechten Seite von pe verschwinden.
	*/
	lauf = pe->right_side;
	while( lauf != NULL ) {
		PE_for_all_edges( lauf->prod_iso, e ) {
			if( EDGE_IS_UNMARKED( e ) ) { /* => e nicht real vorhanden */
				tmp1 = PRS_edgespec( e->partner->label_num, e->label_num, e->dir );
				tmp2 = PRS_edgespec( e->dual_edge->partner->label_num, e->label_num, e->dual_edge->dir );

				if(	!BS_is_in_set( lauf->pe->lost_edges, tmp1 ) &&
					!BS_is_in_set( e->partner->pe_iso->lost_edges, tmp2 )
				   ) {
					if( (lauf->pe->level > 0) && (e->partner->pe_iso->level > 0) ) {
						result = PRS_check_internal_subedge_condition(	lauf->pe,
												e->label_num,
												e->dir,
												e->partner->pe_iso
											    );
						if( !result ) {
							goto	PRS_check_finish;
						}
					} else {
						result = FALSE;
						goto	PRS_check_finish;
						/* haben eine real fehlende Kante gefunden */
					}
				}
			}
		} PE_end_for_all_edges( lauf->prod_iso, e );
		lauf = lauf->succ;
	}

	result = TRUE;

PRS_check_finish:

	lauf = pe->right_side;
	while( lauf != NULL ) {
		GPO_unmark_PE_edgelist( lauf->prod_iso->edges );
		lauf = lauf->succ;
	}

	return	result;

}

/********************************************************************************/
/*										*/
/*-->	PRS_make_level								*/
/*										*/
/*	PARAMETER:	Parsing_element  pe					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Berechne den Level von (dem ansonsten vollstaendig	*/
/*			spezifizierten) 'pe'.					*/
/*										*/
/*	BESONDERES:	Es sind in der Prozedur mehrere Moeglichkeiten vor-	*/
/*			gesehen. Die derzeit gueltige ist durch (*) markiert:	*/
/*			( Bem.: 'E' steht fuer 'ist Element von' )		*/
/*										*/
/*			1. lev(pe) := 1 + max{ lev(pe') | pe' E pe->right_side} */
/*										*/
/*			   Dies entspricht der Tiefe des (Teil)Ableitungsbaums	*/
/*			   den pe darstellt.					*/
/*										*/
/*				       / 0     falls Knoten des Eingabegraphen	*/
/*		    (*) 2. lev(pe) := < 					*/
/*				       \ 1 + sum  lev(pe')	     sonst	*/
/*										*/
/*					     pe' E pe->right_side		*/
/*										*/
/*			   Dies entspricht der Anzahl der Ableitungen die	*/
/*			   noetig sind um die Level-0-Ableitung von pe zu	*/
/*			   erhalten.						*/
/*										*/
/*				       / 0     falls Knoten des Eingabegraphen	*/
/*			3. lev(pe) := < 					*/
/*				       \ sizeof( pe->gnode_set )	sonst	*/
/*										*/
/*			   Selbsterklaerend!					*/
/*			   Die 0 allerdings entsteht aus der Konvention, dass	*/
/*			   die Eingabeknoten immer Level == 0 haben muessen.	*/
/*										*/
/********************************************************************************/


void	PRS_make_level(Parsing_element pe)
{
	PE_set	lauf;

	if( pe == NULL ) {
		return;
	}
/*	1st possibility: pe->level := max( pe_level_of_right_side ) + 1 .  ( 0 if empty right_side )

	this level specifies for pe the stage above ground ( = initial graph, level 0 ),
	i.e. the height of the derivation tree of pe minus one.

	pe->level = 0;
	lauf = pe->right_side;
	while( lauf != NULL ) {
		if( lauf->pe->level >= pe->level ) {
			pe->level = lauf->pe->level + 1;
		}
		lauf = lauf->succ;
	}
*/

/*	2nd possibility: pe->level :=	0 if empty right_side OR
					1 + sum( pe_level_of_right_side )
	this level specifies the number of production applications needed to make pe 'terminal'
	( actually to reach the graph nodes of pe )
*/
	pe->level = 0;
	lauf = pe->right_side;

	if( lauf != NULL ) pe->level++;

	while( lauf != NULL ) {
		pe->level += lauf->pe->level;
		lauf = lauf->succ;
	}

/*	3rd possibility: pe->level :=	0			if pe is in initial graph
					sizeof( pe->gnode_set ) else

	pe->level = 0;
	lauf = pe->right_side;
	while( lauf != NULL ) {
		pe->level += ( (lauf->pe->level==0) ? 1 : (lauf->pe->level) );
		lauf = lauf->succ;
	}
*/

}

/********************************************************************************/
/*										*/
/*-->	special_advance 							*/
/*										*/
/*	PARAMETER:	1. PE_edge *edge	(VAR-Parameter) 		*/
/*			2. PE_set  *right_elem	(VAR-Parameter) 		*/
/*										*/
/*	ZURUECK:	TRUE gdw. *edge enthaelt gueltigen Nachfolger.		*/
/*										*/
/*	AUFGABE:	Berechne Nachfolgekante von *edge in der Menge der	*/
/*			Kanten der PE's von *right_elem.			*/
/*										*/
/*	ERKLAERUNG:	Diese Prozedur ist eine Hilfsprozedur zu		*/
/*			PRS_compute_embedge_choices. Dort muss die Menge der	*/
/*			Kanten untersucht werden, die einen Knoten der rechten	*/
/*			Seite eines PE's als Endknoten haben.			*/
/*			Diese Prozedur ermoeglicht es nun, die Menge der zu	*/
/*			untersuchenden Kanten als (Kanten)Liste zu durchlaufen. */
/*										*/
/*			Verwendung dieser Prozedur wie folgt:  (z.B.)		*/
/*										*/
/*			r_elem = pe->right_side;				*/
/*			edge   = r_elem->pe->edges;  (sofern dereferenzierbar)	*/
/*										*/
/*			do {							*/
/*				...						*/
/*			} while( special_advance( &edge, &r_elem );		*/
/*										*/
/********************************************************************************/

int	special_advance(PE_edge *edge, PE_set *right_elem)
{
	int	result = FALSE;

	if( *edge != NULL ) {
		*edge = (*edge)->succ;
	}
	while( (*edge == NULL) && (*right_elem != NULL) ) {
		*right_elem = (*right_elem)->succ;
		result = TRUE;
		if( *right_elem != NULL ) {
			*edge = (*right_elem)->pe->edges;
		}
	}
	return result;
}

/********************************************************************************/
/*										*/
/*-->	copy_edgelist_to_embedgechoice						*/
/*										*/
/*	PARAMETER:	1. PE_edgelist		elist				*/
/*			2. PE_embedge_choice	*embchoice  (VAR-Parameter)	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Wandle die Kantenliste 'elist' in PE_embedge_choice	*/
/*			um und fuege diese in die Liste '*embchoice' ein.	*/
/*										*/
/*	BESONDERES:	Hilfsprozedur fuer PRS_compute_embedge_choices		*/
/*										*/
/********************************************************************************/

void	copy_edgelist_to_embedgechoice(PE_edge elist, PE_embedge_choice *embchoice)
{
	PE_edge 		te;
	PE_embedge_choice	tmpchoice;

	if( embchoice == NULL ) {
		return;
	}
	if( elist != NULL ) {
		tmpchoice = PE_new_embedge_choice();
		while( elist != NULL ) {
			te = PE_new_edge();
			te->label = w_strsave( elist->label );
			te->label_num = elist->label_num;
			te->partner = elist->partner;
			te->dir = elist->dir;
			PE_add_edge_to_edgelist( &(tmpchoice->edges), te );
			elist = elist->succ;
		}
		PE_insert_embedge_choice( embchoice, tmpchoice );
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_create_emb_array							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	PE_embedding[]	(Array of ...)				*/
/*										*/
/*	AUFGABE:	Speicheranforderung fuer ein Array of PE_embedding.	*/
/*										*/
/*	BESONDERES:   - Hilfsprozedur fuer PRS_compute_embedge_choices. 	*/
/*		      - Groesse wird durch Information aus PRS_info berechnet.	*/
/*										*/
/********************************************************************************/

PE_embedding	*PRS_create_emb_array(void)
{
	int		sizeof_emb_array;
	PE_embedding	*result;

	sizeof_emb_array = 2*sizeof(PE_embedding *)*PRS_info.grammar_edgelabelset_size ;
	result = (PE_embedding *) w_malloc( sizeof_emb_array );
	if( result != NULL ) {
		sizeof_emb_array = 2*PRS_info.grammar_edgelabelset_size ;
		for( --sizeof_emb_array; sizeof_emb_array>=0; sizeof_emb_array-- ) {
			result[sizeof_emb_array] = (PE_embedding) NULL;
		}
	}
	return result;
}

/********************************************************************************/
/*										*/
/*-->	PRS_delete_emb_array							*/
/*										*/
/*	PARAMETER:	PE_embedding *(array[])		(VAR-Parameter)		*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Freigabe des durch PRS_create_emb_array() belegten	*/
/*			Speichers.						*/
/*										*/
/*	BESONDERES:	Hilfsprozedur fuer PRS_compute_embedge_choices. 	*/
/*										*/
/********************************************************************************/

void	PRS_delete_emb_array(PE_embedding **array)
            	        			/* call BY REFERENCE */
{
	if( (array!=NULL) && (*array!=NULL) ) {
		w_free((char *) *array );
		*array = NULL;
	}
}

/********************************************************************************/
/*										*/
/*-->	EMB_SPEC								*/
/*										*/
/*	PARAMETER:	PE_edge edge						*/
/*										*/
/*	ZURUECK:	(int) Array-Index von 'edge'				*/
/*										*/
/*	BESONDERES:	Hilfsprozedur fuer PRS_compute_embedge_choices. 	*/
/*			Verwendung des Resultats ausschliesslich fuer das dort	*/
/*			definierte 'emb_array'. 				*/
/*										*/
/********************************************************************************/

int	EMB_SPEC(PE_edge edge)
{
	if( (edge==NULL) || (edge->dir>UNDIRECTED) || (edge->label_num<1) ) {
		return 0;
	} else {
		if( edge->dir == IN ) {
			return ( 2*edge->label_num + 1 );
		} else {
			return ( 2*edge->label_num );
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_compute_embedge_choices						*/
/*										*/
/*	PARAMETER:	1. PE_edge		edge				*/
/*			2. PE_set		right_elem			*/
/*			3. PE_embedding 	emb_array[]			*/
/*			4. Parsing_element	pe				*/
/*			5. Parsing_element	target_node			*/
/*			6. int			depth				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Berechne die embedge_choices von 'target_node' bzgl.	*/
/*			'pe'.							*/
/*										*/
/*	ERKLAERUNG:	Die mit komplizierteste Prozedur des Parsers. Sie ist	*/
/*			rekursiv und benoetigt dabei vier (4) variable Rekur-	*/
/*			sionsparameter. Dies sind:				*/
/*										*/
/*			edge, right_elem, emb_array[] und depth 		*/
/*										*/
/*			Sie sorgen dafuer, dass die embedge_choices moeglichst	*/
/*			effizient berechnet werden. Sie detailiert zu erklaeren */
/*			waere auch nicht verstaendlicher als der zugehoerige	*/
/*			Sourcekode.						*/
/*			Deshalb hier nur, wie diese Prozedur zu verwenden ist.	*/
/*										*/
/*			gegeben: pe, target_node				*/
/*			gesucht: embedge_choices von target_node		*/
/*										*/
/*			Kode:							*/
/*										*/
/*			...							*/
/*			emb_array = PRS_create_emb_array();			*/
/*			PRS_compute_embedge_choices(				*/
/*						     pe->right_side->pe->edges, */
/*						     pe->right_side,		*/
/*						     emb_array, 		*/
/*						     pe,			*/
/*						     target_node,		*/
/*						     1				*/
/*						   );				*/
/*			PRS_delete_emb_array(); 				*/
/*			...							*/
/*										*/
/********************************************************************************/

void	PRS_compute_embedge_choices(PE_edge edge, PE_set right_elem, PE_embedding *emb_array, Parsing_element pe, Parsing_element tnode, int depth)
{
	static PE_edgelist     tmpelist=NULL;

	int		found, error, set_cur_emb;
	PE_embedding	emb, emb2, cur_emb, emb_backup;
	PE_edge 	tmpedge, e;
	PE_set		lauf;
	int		tmp_spec1, tmp_spec2;

	/* 1. */
	found = FALSE;
	set_cur_emb = TRUE;

	if( edge == NULL ) {
		(void) special_advance( &edge, &right_elem );
		if( (right_elem!=NULL) && (right_elem->prod_iso!=NULL) ) {
			cur_emb = right_elem->prod_iso->loc_embedding;
			set_cur_emb = FALSE;
		}
	}

	while( !found && (edge!=NULL) ) {
		if( (edge->partner==tnode) && (edge->mark==0)) {
			found = TRUE;
		} else {
			if( special_advance( &edge, &right_elem ) ) {
				if( (right_elem!=NULL) && (right_elem->prod_iso!=NULL) ) {
					cur_emb = right_elem->prod_iso->loc_embedding;
					set_cur_emb = FALSE;
				}
			}
		}
	}
	if( !found ) {
		copy_edgelist_to_embedgechoice( tmpelist, &(tnode->embedge_choices) );
		return;
	}

	/* 2. */
	if( set_cur_emb ) {
		cur_emb = emb_array[ EMB_SPEC( edge ) ];
		if( cur_emb == NULL ) {
			cur_emb = right_elem->prod_iso->loc_embedding;
		}
	}

	emb = cur_emb;
	while( emb != NULL ) {
	    if( EMB_IS_UNMARKED( emb ) ) {
		if( (emb->nodelabel_num == tnode->label_num) &&
		    (emb->edgelabel_post_num == edge->label_num) &&
		    (edge->dir == emb->edgedir_post)
		  ){
			tmpedge = PE_new_edge();
			tmpedge->label = w_strsave( emb->edgelabel_pre );
			tmpedge->label_num = emb->edgelabel_pre_num;
			tmpedge->partner = tnode;
			tmpedge->dir = emb->edgedir_pre;
			PE_add_edge_to_edgelist( &tmpelist, tmpedge );
			error = FALSE;

			/* 3. */
			if( !error ) {
				lauf = pe->right_side;
				while( lauf != NULL ) {
					PE_for_all_embeddings( lauf->prod_iso, emb2 ) {
						if(	(emb2->nodelabel_num==tnode->label_num)       &&
							(emb2->edgelabel_pre_num==tmpedge->label_num) &&
							(emb2->edgedir_pre==tmpedge->dir)
						  ) {
							error = TRUE;
							PE_for_all_edges( lauf->pe, e ) {
								if(	(e->partner==tnode) &&
									(e->mark==0) &&
									(e->label_num==emb2->edgelabel_post_num) &&
									(e->dir==emb2->edgedir_post)
								  ) {
									e->mark = depth;
									error = FALSE;
									break;
								}
							} PE_end_for_all_edges( lauf->pe, e );

							if( error ) {
								tmp_spec1 = PRS_edgespec( emb2->nodelabel_num,
											 emb2->edgelabel_post_num,
											 emb2->edgedir_post );
								tmp_spec2 = PRS_edgespec( lauf->pe->label_num,
											 emb2->edgelabel_post_num,
											 PE_inverse_dir(emb2->edgedir_post) );
								if(	BS_is_in_set( lauf->pe->lost_edges, tmp_spec1 ) ||
									BS_is_in_set( tnode->lost_edges, tmp_spec2)
								   ) {
									error = FALSE;
								} else {
									if( (lauf->pe->level > 0) && (tnode->level > 0) ) {
										error = !PRS_check_internal_subedge_condition(
												lauf->pe,
												emb2->edgelabel_post_num,
												emb2->edgedir_post,
												tnode
											);
									}
								}
							}
						}
						if( error ) {
							break;
						}
					} PE_end_for_all_embeddings( lauf->prod_iso, emb2 );
					if( error ) {
						break;
					}
					lauf = lauf->succ;
				}
			}

			/* 4. */
			if( !error ) {
				emb_backup = emb_array[ EMB_SPEC( edge ) ];
				emb_array[ EMB_SPEC( edge ) ] = emb;
				PRS_compute_embedge_choices( edge, right_elem, emb_array, pe, tnode, depth+1 );
				emb_array[ EMB_SPEC( edge ) ] = emb_backup;
			}

			/* remove tmpedge of tmpelist */

			tmpelist = tmpelist->succ;
			if( tmpelist != NULL ) {
				tmpelist->pre = NULL;
			}
			tmpedge->succ = NULL;
			tmpedge->pre = NULL;

			PE_dispose_edge( &tmpedge );

			lauf = pe->right_side;
			while( lauf != NULL ) {
				PE_for_all_edges( lauf->pe, e ) {
					if(	(e->partner==tnode) &&
						(e->mark==depth)
					  ) {
						e->mark = 0;
					}
				} PE_end_for_all_edges( lauf->pe, e );
				lauf = lauf->succ;
			}
		}
	    }
	    emb = emb->succ;
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_unmark_graph_and_grammar						*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche ALLE Markierungen von Graph und Grammatik	*/
/*			des Parsers.						*/
/*										*/
/*	BESONDERES:	Teil der Vorbereitung des Parsers auf die Erzeugung	*/
/*			der Parsing-Tabelle.					*/
/*										*/
/********************************************************************************/

void	PRS_unmark_graph_and_grammar(void)
{
	PE_production	prod;
	Parsing_element pe;

	if( PRS_info.graph_loaded ) {
		PE_for_all_parsing_elements( PRS_info.pe.graph, pe ) {
			GPO_unmark_PE_edgelist( pe->edges );
			PE_UNMARK( pe );
		} PE_end_for_all_parsing_elements( PRS_info.pe.graph, pe );
	}

	if( PRS_info.grammar_loaded ) {
		PE_for_all_productions( PRS_info.grammar, prod ) {
			PE_for_all_parsing_elements( prod->right_side, pe ) {
				GPO_unmark_PE_edgelist( pe->edges );
				GPO_unmark_embeddings( pe->loc_embedding );
				PE_UNMARK( pe );
			} PE_end_for_all_parsing_elements( prod->right_side, pe );
		} PE_end_for_all_productions( PRS_info.grammar, prod );
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_prepare_parser							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Bereite den Parser auf die Erzeugung der Parsing-	*/
/*			Tabelle vor.						*/
/*										*/
/********************************************************************************/

void	PRS_prepare_parser(void)
{
	if( !PRS_info.graph_loaded || !PRS_info.grammar_loaded ) {
		ERROR( "You must first scan grammar and graph!" );
		PRS_info.status = PRS_ERROR;
		return;
	}

	if( !GPO_check_graph_labels( PRS_info.pe.graph ) ) {
		/* graph not in grammar because it has labels out of the grammar's labels */
		ERROR( "g NOT in L(GG). Label mismatch!" );
		PRS_info.status = PRS_ERROR;
		return;
	}
	PE_set_number_generator( PRS_info.pe.graph->nummer );
	PRS_unmark_graph_and_grammar();
	PRS_init_lost_edges( PRS_info.pe.graph );
	PRS_init_embeddings( PRS_info.grammar );

	PRS_info.current_pe	= PRS_info.pe.tab;
	PRS_info.current_prod	= PRS_info.grammar;
	PRS_info.firstflag	= TRUE;
	PRS_info.advance_pe	= TRUE;
	PRS_info.number_of_pes	= PRS_info.pe.graph->nummer;
	PRS_info.status_changed = -1;

	/* set PRS_info.insert_pe */
	PRS_info.insert_pe = PRS_info.current_pe;
	if( PRS_info.insert_pe != NULL ) {
		while( PRS_info.insert_pe->succ != NULL ) {
			PRS_info.insert_pe = PRS_info.insert_pe->succ;
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_update_insert_pe							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Update von PRS_info.insert_pe.				*/
/*										*/
/*	ERKLAERUNG:	..insert_pe gibt an, an welcher Stelle der Parsing-	*/
/*			Tabelle (PTab) neue PE's eingefuegt werden.		*/
/*			Update bedeutet nun, dass gewaehrleistet werden muss,	*/
/*			dass ..insert_pe in der PTab nie vor ..current_pe	*/
/*			stehen darf.						*/
/*										*/
/********************************************************************************/

void	PRS_update_insert_pe(void)
{
	/* 1st possibility: after initial graph && after current_pe
	*/
	if( (PRS_info.insert_pe != NULL) && (PRS_info.insert_pe->succ == PRS_info.current_pe) ) {
		PRS_info.insert_pe = PRS_info.current_pe;
	}

	/* 2nd possibility: always after current_pe

		PRS_info.insert_pe = PRS_info.current_pe;
	*/
	/* 3rd possibility: always at end of pe.tab

		PRS_info.insert_pe = PRS_info.insert_pe->succ;
	*/
}

/********************************************************************************/
/*										*/
/*-->	PRS_get_next_fixed_position_of_cur_pe					*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Suche (naechste) Produktion (mit Position in rechter	*/
/*			Seite) durch welche PRS_info.current_pe entstanden sein */
/*			koennte. Rueckgabe TRUE gdw. Suche erfolgreich. 	*/
/*										*/
/*	BESONDERES:	Modifiziert werden:					*/
/*			- PRS_info.current_pe->pe_iso				*/
/*			- PRS_info.current_pe->iso_status			*/
/*			- PRS_info.current_prod 				*/
/*			Bei Rueckgabe TRUE laesst sich ueber current_prod die	*/
/*			gefundene Produktion und ueber current_pe->pe_iso die	*/
/*			gefundene Position innerhalb der rechten Seite von	*/
/*			current_prod bestimmen. 				*/
/*										*/
/*	ERKLAERUNG:	Idee ist folgende:					*/
/*			Betrachte alle Knoten aller rechten Seiten ( der	*/
/*			Produktionen ) als EINE grosse Liste von Knoten.	*/
/*			Falls current_pe->pe_iso == NULL beginne Suche am	*/
/*			Anfang der Liste, sonst suche ab current_pe->pe_iso.	*/
/*			Suche nach einem Knoten mit gleichem Label.		*/
/*			Falls ein solcher gefunden wird (=:PE), setze		*/
/*			current_pe->pe_iso = PE sowie PE_FIX(current_pe).	*/
/*										*/
/********************************************************************************/

int	PRS_get_next_fixed_position_of_cur_pe(void)
{
	Parsing_element lauf;
	int		found;

	PE_UNMARK( PRS_info.current_pe );

	lauf = PRS_info.current_pe->pe_iso;
	if( lauf == NULL ) {
		lauf = PRS_info.current_prod->right_side;
	} else {
		PE_UNMARK( lauf );
		lauf->pe_iso = NULL;
		PRS_info.current_pe->pe_iso = NULL;
		lauf = lauf->succ;
	}
	found = FALSE;
	while( !found && (PRS_info.current_prod!=NULL) ) {
		if( lauf == NULL ) {
			GPO_unmark_PE_list( PRS_info.current_prod->right_side, FORWARD );
			PRS_info.current_prod = PRS_info.current_prod->succ;
			if( PRS_info.current_prod != NULL ) {
				lauf = PRS_info.current_prod->right_side;
			}
		} else {
			if( lauf->label_num == PRS_info.current_pe->label_num ) {
				found = TRUE;
			} else {
				lauf = lauf->succ;
			}
		}
	}

	if( found ) {
		PRS_info.current_pe->pe_iso = lauf;
		lauf->pe_iso = PRS_info.current_pe;
		PE_FIX( PRS_info.current_pe );
		PE_FIX( lauf );

		PRS_info.firstflag = TRUE;

		return TRUE;
	} else {
		PRS_info.current_prod = PRS_info.grammar;
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_get_isomorph_node_overlay						*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	TRUE oder FALSE 					*/
/*										*/
/*	AUFGABE:	Suche (naechste) isomorphe Knotenueberdeckung von	*/
/*			PRS_info.current_prod(->right_side).			*/
/*			Rueckgabe TRUE gdw. Suche erfolgreich.			*/
/*										*/
/*	BESONDERES:	Basiert auf GPO_get_first_isomorph... und		*/
/*			GPO_get_next_isomorph... (dort weitere Information)	*/
/*										*/
/********************************************************************************/

int	PRS_get_isomorph_node_overlay(void)
{
	if( PRS_info.firstflag ) {
		PRS_info.firstflag = FALSE;
		return GPO_get_first_isomorph_node_overlay( PRS_info.current_pe, PRS_info.current_prod->right_side,BACKWARD );
	} else {
		return GPO_get_next_isomorph_node_overlay( PRS_info.current_pe, PRS_info.current_prod->right_side, BACKWARD );
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_prepare_edges							*/
/*										*/
/*	PARAMETER:	1. Parsing_element	new_pe				*/
/*			2. Parsing_element	target_pe			*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Loesche Markierung von Kanten				*/
/*										*/
/*	ERKLAERUNG:   - Geloescht werden Markierungen von Kanten e von new_pe	*/
/*			mit e->partner == target_pe.				*/
/*		      - Kanten von target_pe bleiben unberuehrt !!!		*/
/*										*/
/*	BESONDERES:	War mal Hilfsprozedur fuer PRS_create_next_parsing_...	*/
/*			Moeglicherweise kann sie mal jemand gebrauchen. 	*/
/*										*/
/********************************************************************************/

void	PRS_prepare_edges(Parsing_element new_pe, Parsing_element target_pe)
{
	PE_set	lauf;
	PE_edge edge;

	if( new_pe == NULL ) {
		return;
	}

	lauf = new_pe->right_side;
	while( lauf != NULL ) {
		PE_for_all_edges( lauf->pe, edge ) {
			if( edge->partner == target_pe ) {
				if( !EDGE_IS_UNMARKED( edge ) ) {
					printf( "WARNING: edge marked!\n" );
				}
				/*
				edge->mark = 0;
				*/
			}
		} PE_end_for_all_edges( lauf->pe, edge );
		lauf = lauf->succ;
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_create_next_parsing_elements					*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Fuehre einen ZENTRALEN Arbeitsschritt des Parsers aus.	*/
/*										*/
/*	BESONDERES:	Falls der Parser im Step-Modus ist			*/
/*			(...status == PRS_PAUSED), werden solange Arbeits-	*/
/*			schritte ausgefuehrt, bis der Parser neue PE's		*/
/*			generiert hat oder terminiert.				*/
/*										*/
/*	ERKLAERUNG:	zentraler Arbeitsschritt:				*/
/*										*/
/*m			Zentraler_Arbeitsschritt	(progman-submanual)	*/
/********************************************************************************/

void	PRS_create_next_parsing_elements(void)
{
	PE_set		tmp_pe_set, lauf;
	Parsing_element tmp_pe, brd_pe, pe, cur_pe;
	PE_edge 	e;
	PE_embedding	*emb_array;
	PE_embedge_choice	tmp_choice;
	int		test_ok;

	int		zaehler;


	PRS_info.no_pes_created = TRUE;

	do {
		if( PRS_info.advance_pe ) {
			if( PRS_get_next_fixed_position_of_cur_pe() ) {
				PRS_info.advance_pe = FALSE;
			} else {
				PRS_info.current_pe = PRS_info.current_pe->succ;
				/* 1st poss. or 2nd poss: */
				PRS_update_insert_pe();
				if( PRS_info.current_pe == NULL ) {
					/* no more parsing elements */
					PRS_info.status = PRS_FINISHED;
					if( !PRS_info.graph_in_grammar ) {
						PRS_info.message = "g NOT in L(gg)";
					}
					PRS_changed( CHG_STATUS | CHG_MESSAGE );
				} else {
					do {
						cur_pe = PRS_info.current_pe;
						if( cur_pe != NULL ) {
							pe = GPO_find_isomorph_pe( cur_pe->pre, cur_pe, BACKWARD );
						} else {
							PRS_info.status = PRS_FINISHED;
							if( !PRS_info.graph_in_grammar ) {
								PRS_info.message = "g NOT in L(gg)";
							}
							PRS_changed( CHG_STATUS | CHG_MESSAGE );
							break;
						}
						if( pe != NULL ) {

							int	eq_result;
							PE_set	eq_lauf1, eq_lauf2;

							/* check real equality */
							eq_result = TRUE;

							if( pe->which_production != cur_pe->which_production ) {
								eq_result = FALSE;
							}
							eq_lauf1 = pe->right_side;
							eq_lauf2 = cur_pe->right_side;
							while( (eq_lauf1!=NULL) && (eq_lauf2!=NULL) ) {
								if(	(eq_lauf1->pe != eq_lauf2->pe) ||
									(eq_lauf1->prod_iso!=eq_lauf2->prod_iso)
								  ) {
									eq_result = FALSE;
									break;
								}
								eq_lauf1 = eq_lauf1->succ;
								eq_lauf2 = eq_lauf2->succ;
							}
							if( eq_lauf1 != eq_lauf2 ) {
								eq_result = FALSE;
							}


							if( eq_result ) { /* really equal */
							/*
								tmp_pe = cur_pe->succ;
								PE_remove_parsing_element( &(PRS_info.pe.tab), cur_pe );
								PRS_kill_parsing_element( &cur_pe );
								PRS_info.current_pe = tmp_pe;
							*/
								printf( "Equal PE's\n\nPE1 =\n\n" );
								DBX_fshow_Parsing_element( stdout, cur_pe, DBX_GRAPH );
								printf( "\nPE2=\n\n" );
								DBX_fshow_Parsing_element( stdout, pe, DBX_GRAPH );
								pe = NULL;
							} else {
							/*
								if( PRS_info.link_isomorph_pes ) {
							*/	if( TRUE ) {
									PRS_info.current_pe = cur_pe->succ;
									/* 1st poss. or 2nd poss: */
									PRS_update_insert_pe();

									PE_remove_parsing_element( &(PRS_info.pe.tab), cur_pe );
									GPO_add_to_isomorph_pes( &(pe->isomorph_pes), cur_pe );
									cur_pe->isomorph_main_pe = pe;
									PRS_info.number_of_pes--;
									PRS_changed( CHG_PTAB_SIZE );
								} else {
									pe = NULL;
								}
							}
						}
					} while( pe != NULL );
					if( PRS_info.current_pe != NULL ) {
						PRS_check_start_elements();
					}
				}
			}
		} else {
			if( PRS_get_isomorph_node_overlay() ) {

				tmp_pe_set = NULL;
				tmp_pe = PE_new_parsing_element();

				PE_set_number_generator( tmp_pe->nummer - 1 );
				tmp_pe->label = w_strsave( PRS_info.current_prod->left_side );
				tmp_pe->label_num = PRS_info.current_prod->left_side_num;
				tmp_pe->which_production = PRS_info.current_prod;
				PE_for_all_parsing_elements( PRS_info.current_prod->right_side, pe ) {
					PE_add_to_PE_set( &(tmp_pe->right_side), pe->pe_iso );
				} PE_end_for_all_parsing_elements( PRS_info.current_prod->right_side, pe );
				PRS_make_level( tmp_pe );
				PRS_make_lost_edges( tmp_pe );


				test_ok = PRS_check_gnode_sets( tmp_pe );
				if( test_ok ) {
					test_ok &= PRS_check_internal_edge_condition( tmp_pe );
				}
				if( test_ok ) { /* check external edge condition */
					lauf = tmp_pe->right_side;
					while( (lauf != NULL) && test_ok ) {
						PE_for_all_edges( lauf->pe, e ) {
							brd_pe = e->partner;
							if(	PE_IS_UNMARKED( brd_pe )		&&
								(brd_pe->embedge_choices==NULL) 	&&
								BS_empty_intersection( tmp_pe->gnode_set, brd_pe->gnode_set )
							   ) {
								emb_array = PRS_create_emb_array();
							/*
								PRS_prepare_edges( tmp_pe, brd_pe );
							*/
								PRS_compute_embedge_choices(	tmp_pe->right_side->pe->edges,
										tmp_pe->right_side,
										emb_array,
										tmp_pe, brd_pe, 1
								    );
								PRS_delete_emb_array( &emb_array );
								PE_add_to_PE_set( &tmp_pe_set, brd_pe );
								ATTRS_SET( brd_pe, PE_adjacent );
								if( brd_pe->level == 0 ) {
									if( brd_pe->embedge_choices==NULL ) {
										/* mind. eine Kante zu einem Level0-PE
										   kann nicht hochgezogen werden */
										test_ok = FALSE;
										break;
									}
								} else if ( brd_pe->embedge_choices == NULL ) {
								    tmp_choice = PE_new_embedge_choice();
								    if( tmp_choice != NULL ) {
									tmp_choice->edges = PE_new_edge();
									if( tmp_choice->edges != NULL ) {
									    tmp_choice->edges->partner = brd_pe;
									    tmp_choice->edges->dir = BLOCKING;
									}
									PE_insert_embedge_choice( &(brd_pe->embedge_choices),
												  tmp_choice );
								    }
								}
							}
						} PE_end_for_all_edges( lauf->pe, e );
						lauf = lauf->succ;
					}
				}
				if( test_ok ) {
					zaehler = 0;
					PRS_make_parsing_elements_of( tmp_pe_set, tmp_pe, &zaehler );

#					ifdef DEBUG
						if( zaehler > 0 ) {
							printf( " ==> %d new PE's.\n", zaehler );
						}
#					endif
				}

				/* temp. Speicher wieder freigeben */
				lauf = tmp_pe_set;
				while( lauf != NULL ) {
					PE_delete_all_embedge_choices( &(lauf->pe->embedge_choices) );
					ATTRS_RESUME( lauf->pe );
					lauf = lauf->succ;
				}
				PE_delete_PE_set( &tmp_pe_set );
				PE_dispose_parsing_element( &tmp_pe );

			} else {
				PRS_info.advance_pe = TRUE;
			}
		}
	} while( PRS_info.no_pes_created && ( (PRS_info.status==PRS_PAUSED)) );
}

/********************************************************************************/
/*										*/
/*-->	PRS_make_edges_consistent						*/
/*										*/
/*	PARAMETER:	1. Parsing_element	old_pe				*/
/*			2. Parsing_element	new_pe				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Sorge dafuer, dass die Kanten des durch 		*/
/*			PRS_make_parsing_elements_of(...) erzeugten 'new_pe'	*/
/*			eine konsistente Struktur erhalten.			*/
/*			'old_pe' muss dabei ein Nachbar von 'new_pe' sein.	*/
/*										*/
/*	ERKLAERUNG:	Hilfsprozedur zu PRS_make_parsing_elements_of(...)	*/
/*										*/
/*			Bei Parsing-Elementen, die eine Vorgaenger/Nachfolger-	*/
/*			Beziehung zueinander haben (d.h. sie sind Teil eines	*/
/*			(gemeinsamen) Ableitungsbaums) bestehen ebenfalls	*/
/*			Abhaengigkeiten zwischen Kanten(mengen) zu gemeinsamen	*/
/*			Nachbar-PE's.						*/
/*										*/
/*			Da PRS_make_parsing_elements_of(..) auf solche Kanten-	*/
/*			abhaengigkeiten keine Ruecksicht nimmt, bedarf es eines */
/*			Postprocessings auf den Kantenmengen neu erzeugter PE's.*/
/*										*/
/*			Genau dieses wird durch diese Prozedur erledigt.	*/
/*										*/
/********************************************************************************/

void	PRS_make_edges_consistent(Parsing_element old_pe, Parsing_element new_pe)
{
	PE_edge 	e, e1, e2;
	PE_set		ps_lauf;
	PE_embedding	*emb_array;
	PE_embedge_choice	eec_lauf;
	int		found, error, tmp_spec;


	ps_lauf = old_pe->right_side;
	while( ps_lauf != NULL ) {

		/* remark: attribute 'PE_adjacent' has been set while constructing new_pe ! */

		if( (ps_lauf->pe->level > 0) && ATTRS_TEST( ps_lauf->pe, PE_adjacent ) ) {
			PRS_make_edges_consistent( ps_lauf->pe, new_pe );
			ps_lauf->pe->level = - ps_lauf->pe->level; /* misused as a mark */
		}
		ps_lauf = ps_lauf->succ;
	}

	emb_array = PRS_create_emb_array();
	PRS_compute_embedge_choices(	old_pe->right_side->pe->edges,
					old_pe->right_side,
					emb_array,
					old_pe, new_pe /*as border old_pe*/ , 1
				    );
	PRS_delete_emb_array( &emb_array );

	found = FALSE;
	eec_lauf = new_pe->embedge_choices;
	while( (eec_lauf!=NULL) && !found ) {
		error = FALSE;

		/* check if every edge of old_pe has an isomorph edge in eec_lauf->edges */
		GPO_unmark_PE_edgelist( eec_lauf->edges );
		PE_for_all_edges( old_pe, e2 ) {
			if( e2->partner == new_pe ) {
				if( !GPO_find_isomorph_edge( eec_lauf->edges, e2 ) ) {
					tmp_spec = PRS_edgespec( new_pe->label_num, e2->label_num, e2->dir );
					if( !BS_is_in_set( old_pe->lost_edges, tmp_spec ) ) {
						error = TRUE;
						break;
					}
				}
			} else {
				/* speed up! ( remove following statement to
				   get a slow but correct algorithm

				break;
				*/
			}
		} PE_end_for_all_edges( old_pe, e2 );

		if( !error ) {
			/* check if every edge of eec_lauf->edges has an isomorph edge in old_pe */

			e2 = eec_lauf->edges;
			while( e2 != NULL ) {
				if( EDGE_IS_UNMARKED( e2 ) ) {
					tmp_spec = PRS_edgespec( old_pe->label_num, e2->label_num, PE_inverse_dir( e2->dir ) );
					if( !BS_is_in_set( new_pe->lost_edges, tmp_spec ) ) {
						error = TRUE;
						break;
					}
				}
				e2 = e2->succ;
			}
		}

		if( !error ) {
			/* heureka, we've found it */
			found = TRUE;
			break;
		}

		GPO_unmark_PE_edgelist( eec_lauf->edges );
		eec_lauf = eec_lauf->succ;
	}

	if( !found ) {
		e = old_pe->edges;
		while( e != NULL ) {
			e2 = e->succ;
			if( e->partner == new_pe ) {
				PE_remove_both_edges( e );
				PE_dispose_both_edges( &e );
			}
			e = e2;
		}

		e = PE_new_edge();
		e2 = PE_new_edge();
		if( (e!=NULL) && (e2!=NULL) ) {
			e->dual_edge = e2;
			e->dir = BLOCKING;
			e->partner = new_pe;
			e2->dual_edge = e;
			e2->dir = BLOCKING;
			e2->partner = old_pe;
			PE_insert_edge( old_pe, e );
			PE_insert_edge( new_pe, e2 );
		} else {
			PE_dispose_edge( &e );
			PE_dispose_edge( &e2 );
		}
	} else {
		e = eec_lauf->edges;
		while( e != NULL ) {
			if( EDGE_IS_UNMARKED( e ) ) {
				e1 = PE_new_edge();
				e2 = PE_new_edge();
				if( (e1!=NULL) && (e2!=NULL) ) {
					e1->dual_edge = e2;
					e1->label = w_strsave( e->label );
					e1->label_num = e->label_num;
					e1->partner = new_pe;
					e1->dir = e->dir;

					e2->dual_edge = e1;
					e2->label = w_strsave( e->label );
					e2->label_num = e->label_num;
					e2->partner = old_pe;
					e2->dir = PE_inverse_dir( e->dir );
					PE_insert_edge( old_pe, e1 );
					PE_insert_edge( new_pe, e2 );
				} else {
					PE_dispose_edge( &e1 );
					PE_dispose_edge( &e2 );
				}
			}
			e = e->succ;
		}
		GPO_unmark_PE_edgelist( eec_lauf->edges );
	}

	PE_delete_all_embedge_choices( &(new_pe->embedge_choices) );
}

/********************************************************************************/
/*										*/
/*-->	PRS_make_parsing_elements_of						*/
/*										*/
/*	PARAMETER:	1. PE_set	   pset 				*/
/*			2. Parsing_element pe					*/
/*			3. int		   *count	(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Generiere neue Parsing-Elemente 			*/
/*										*/
/*	BESONDERES:								*/
/*	ERKLAERUNG:	Hilfsprozedur zu PRS_create_next_parsing_elements().	*/
/*										*/
/*			Es werden neue Parsing-Elemente generiert gemaess dem	*/
/*			vorgegebenen Prototypen 'pe'. Werden mehr als ein PE	*/
/*			generiert, so unterscheiden sich diese nur durch die	*/
/*			Art ihrer Kanteneinbettung bzgl. ihrer Nachbarn.	*/
/*										*/
/*			Die Nachbarn der erzeugten PE's sind in 'pset'		*/
/*			aufgelistet.						*/
/*			Die Anzahl der erzeugten PE's ist gleich der Maechtig-	*/
/*			keit des Kreuzproduktes der embedge_choices der 	*/
/*			Nachbarn.						*/
/*			Die Variable '*count' wird um diese Anzahl erhoeht.	*/
/*										*/
/********************************************************************************/

void	PRS_make_parsing_elements_of(PE_set pset, Parsing_element pe, int *count)
{
	Parsing_element 	tmp_pe;
	PE_edge 		tmp_edge, tmp_dual_edge, e;
	PE_embedge_choice	eec_lauf, tmp_eec;
	PE_set			ps_lauf;

	if( pset == NULL ) {
		tmp_pe = PE_new_parsing_element();
		if( tmp_pe != NULL ) {
			tmp_pe->label = w_strsave( pe->label );
			tmp_pe->label_num = pe->label_num;
			tmp_pe->level = pe->level;
			ps_lauf = pe->right_side;
			while( ps_lauf != NULL ) {
				/* Setzt als !!! Seiteneffekt !!! den Zeiger auf die Produktion */
				PE_add_to_PE_set( &(tmp_pe->right_side), ps_lauf->pe );
				/* tmp_pe->right_side->prod_iso = ps_lauf->prod_iso; */
				ps_lauf = ps_lauf->succ;
			}
			BS_init_set( &(tmp_pe->gnode_set), PRS_info.graph_size );
			BS_union( tmp_pe->gnode_set, pe->gnode_set, tmp_pe->gnode_set );
			BS_init_set( &(tmp_pe->lost_edges), PRS_info.grammar_edgeset_size );
			BS_union( tmp_pe->lost_edges, pe->lost_edges, tmp_pe->lost_edges );

			eec_lauf = pe->embedge_choices;
			while( eec_lauf != NULL ) {
				e = eec_lauf->edges;
				while( e != NULL ) {
					tmp_edge = PE_new_edge();
					tmp_dual_edge = PE_new_edge();
					if( (tmp_edge!=NULL) && (tmp_dual_edge!=NULL) ) {
						tmp_edge->label = w_strsave( e->label );
						tmp_edge->label_num = e->label_num;
						tmp_edge->partner = e->partner;
						tmp_edge->dir = e->dir;
						tmp_edge->dual_edge = tmp_dual_edge;

						tmp_dual_edge->label = w_strsave( e->label );
						tmp_dual_edge->label_num = e->label_num;
						tmp_dual_edge->partner = tmp_pe;
						tmp_dual_edge->dir = PE_inverse_dir( e->dir );
						tmp_dual_edge->dual_edge = tmp_edge;
						PE_insert_edge( tmp_pe, tmp_edge );
						PE_insert_edge( e->partner, tmp_dual_edge );
					} else {
						PE_dispose_edge( &tmp_edge );
						PE_dispose_edge( &tmp_dual_edge );
					}
					e = e->succ;
				}
				eec_lauf = eec_lauf->succ;
			}

			tmp_pe->which_production = pe->which_production;

			/********************************************************************************/
			/*			Layout Graph Grammars: BEGIN				*/
			/********************************************************************************/
			if( !ORGINAL_LAMSHOFT )
			{
				tmp_pe->used_prod = tmp_pe->which_production->prod_iso;
			}
			/********************************************************************************/
			/*			Layout Graph Grammars: END				*/
			/********************************************************************************/

			/* insert tmp_pe */

			tmp_pe->succ = PRS_info.insert_pe->succ;
			tmp_pe->pre = PRS_info.insert_pe;
			PRS_info.insert_pe->succ = tmp_pe;
			if( tmp_pe->succ != NULL ) {
				tmp_pe->succ->pre = tmp_pe;
			}
			/* if BFS (Breitensuche): 3rd possibility
				PRS_update_insert_pe();
			*/
			(*count)++;
			PRS_info.no_pes_created = FALSE;
			PRS_info.number_of_pes ++ ;
			PRS_changed( CHG_PTAB_SIZE );

			e = tmp_pe->edges;
			while( e != NULL ) {
				if( e->partner->level > 0 ) {
					e->partner->level = - e->partner->level;
					PRS_make_edges_consistent( e->partner, tmp_pe );
					e = tmp_pe->edges; /*	edge structure possibly has changed,
								so restart 'e'	*/
				} else {
					e = e->succ;
				}
			}
			PE_for_all_edges( tmp_pe, e ) {
				if( e->partner->level < 0 ) {
					e->partner->level = - e->partner->level;
				}
			} PE_end_for_all_edges( tmp_pe, e );


		} else {
			ERROR( "out of memory!" );
		}
	} else {
		eec_lauf = pset->pe->embedge_choices;
		while( eec_lauf != NULL ) {
			tmp_eec = eec_lauf->succ;

			PE_insert_embedge_choice( &(pe->embedge_choices), eec_lauf );	/*push*/
			PRS_make_parsing_elements_of( pset->succ, pe, count);
			PE_remove_embedge_choice( &(pe->embedge_choices) );		/*pop*/

			eec_lauf->succ = tmp_eec;
			eec_lauf = eec_lauf->succ; /*tmp_eec*/
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	PRS_kill_parsing_element						*/
/*										*/
/*	PARAMETER:								*/
/*	ZURUECK:								*/
/*	AUFGABE:								*/
/*	BESONDERES:								*/
/*	ERKLAERUNG:	sollte PE_kill_parsing_element heissen			*/
/*			Ist derzeit nahezu funktionsgleich zu TRC_kill_node.	*/
/*										*/
/********************************************************************************/

void	PRS_kill_parsing_element(Parsing_element *PE)
                    				/* call BY REFERENCE! */
{
	PE_edge e, tmp;

	/* remove and dispose all manifestations of edges of *PE */
	e = (*PE)->edges;
	while( e != NULL ) {
		tmp = e->succ;
		PE_remove_both_edges( e );
		PE_dispose_both_edges( &e );
		e = tmp;
	}

	/* now dispose the parsing element */
	PE_dispose_parsing_element( PE );
}


/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mparser								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fparser							*/
/*m		-Eparser							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dparser							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pparser							*/
/********************************************************************************/

/*-->@	Zentraler_Arbeitsschritt

	Wie sieht der grundsaetzliche Arbeitsablauf des Parsers fuer einen
	Eingabegraphen aus?

	Lade Eingabegraphen.
	Fange mit erstem PE an. (current_pe = pe.graph)

	Solange PE's vorhanden sind: (status == PRS_RUNNING)

		Bestimme alle Produktionen durch die das aktuelle PE entstanden
		sein koennte.

		Fuer alle diese Produktionen: (current_prod)

			Bestimme alle Moeglichkeiten, die rechte Seite der
			Produktion durch das aktuelle PE und PE's der PTab
			aufzufuellen.
			( ...next_fixed_position.. & ..isomorph_node_overlay )

			Fuer alle Moeglichkeiten:

			    (* hier beginnt ein ZENTRALER Arbeitsschritt *)

				Teste Knotenbedingung.
				Teste interne Kantenbedingung.
				Teste externe Kantenbedingung.
				Falls alle Tests positiv:
					Generiere neue PE's und fuege
					diese (z.B.) am Ende der Ptab
					ein
				)

			    (* und hier endet er *)
			)
		)
	)


	Bemerkung:

	Der Parser ist so konstruiert, dass der Zustand der auesseren
	Schleifen in den globalen Variablen bzw. den Datenstrukturen
	gespeichert wird. Somit wird es moeglich, nach jedem ZENTRALEN
	Arbeitsschritt diesen Zustand lokal eine 'Position' weiter zu
	setzen und damit den gesamten Parsingvorgang ( nach der
	Initialisierung) in eine Folge von ZENTRALEN Arbeitsschritten
	umzuwandeln.

	Verweise in Klammern (...) sind grobe Hinweise auf bedeutende
	Zusammenhaenge.

**/

