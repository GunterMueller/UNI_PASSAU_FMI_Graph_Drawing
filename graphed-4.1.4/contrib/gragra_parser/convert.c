/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Econvert								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Fconvert								*/
/*										*/
/*	MODUL:	    convert							*/
/*										*/
/*	FUNKTION:   Zentrale Schnittstelle zu GraphEd (ueber sgraph/sgragra).	*/
/*		    Das Modul enthaelt Prozeduren zur Konvertierung von 	*/
/*		    sgraph/sgragra Datenstrukturen in die Datenstrukturen des	*/
/*		    Parsers und umgekehrt.					*/
/*										*/
/********************************************************************************/

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include "misc.h"
#include "types.h"

/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/

#include "lp_datastruc.h"

/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/

#include "convert.h"

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*-->@	-Pconvert

static	void	CVT_add_local_embeddings_of		( Sprod    sp );
static	void	CVT_add_global_embedding_of		( Sgragra sgg, Sprod	     sp   );
	void	CVT_convert_sgraph_to_PE_list		( Sgraph   sg, PE_list	     *pl  );
	void	CVT_convert_Sprod_to_PE_production	( Sprod    sp, PE_production *pp  );
	void	CVT_convert_sgragra_to_PE_grammar	( Sgragra sgg, PE_grammar    *pgg );
	void	CVT_show_info				();
	void	CVT_init				();
	
**/

/********************************************************************************/
/*										*/
/*-->	CVT_add_local_embeddings_of						*/
/*										*/
/*	PARAMETER:	Sprod	prod						*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Transformiere lokale Sgragra-Einbettungsregeln in	*/
/*			Parser-Einbettungsregeln (PE_embedding's).		*/
/*										*/
/*	BESONDERES:	Diese Prozedur wird von CVT_convert_Sprod_to_PE_prod..	*/
/*			benutzt.						*/
/*			Eine besondere Vorgehensweise besteht darin, dass diese */
/*			hier die zu 'prod' gehoerige PE_production indirekt	*/
/*			ueber das Attribut-Feld der Knoten der rechten Seite	*/
/*			angesprochen wird. (Die Funktion des Attributes 	*/
/*			entspricht dabei einem "PE-isonode"-Verweis)		*/
/*										*/
/********************************************************************************/

static	void	CVT_add_local_embeddings_of(Sprod prod)
{
	Sembed		emb;
	Parsing_element pe;
	PE_embedding	pemb;

	if( prod == (Sprod)NULL ) {
		return;
	}

	for_all_sembeds( prod, emb ) {
		pemb = PE_new_embedding();
		if( pemb != (PE_embedding)NULL ) {
			pemb->nodelabel = w_strsave( emb->node_embed );
			pemb->edgelabel_pre = w_strsave( emb->oldedge );
			pemb->edgelabel_post = w_strsave( emb->newedge );
			if( CVT_info.grammar_is_directed ) {
				switch( emb->olddir ) {
					case S_out:
						pemb->edgedir_pre = OUT;
						break;
					case S_in:
						pemb->edgedir_pre = IN;
						break;
					default:
						pemb->edgedir_pre = UNDEFINED;
						break;
				}
				switch( emb->newdir ) {
					case S_out:
						pemb->edgedir_post = OUT;
						break;
					case S_in:
						pemb->edgedir_post = IN;
						break;
					default:
						pemb->edgedir_post = UNDEFINED;
						break;
				}
			} else {
				pemb->edgedir_pre = UNDIRECTED;
				pemb->edgedir_post = UNDIRECTED;
			}
			pe = attr_data_of_type( emb->node_right, Parsing_element );
			PE_insert_embedding( pe, pemb );
		}
	} end_for_all_sembeds( prod, emb );
}

/********************************************************************************/
/*										*/
/*-->	CVT_add_global_embedding_of						*/
/*										*/
/*	PARAMETER:	1. Sgragra	gram					*/
/*			2. Sprod	prod					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	wandle globale Sgragra-Einbettungsregeln von 'gram' in	*/
/*			lokale PE_embedding's von der zu 'prod' gehoerigen	*/
/*			isomorphen PE_production um.				*/
/*										*/
/*	BESONDERES:	Bei Aufruf dieser Prozedur MUSS 'prod' bereits in eine	*/
/*			PE_production umgewandelt sein. 			*/
/*			Zwischen CVT_convert_Sprod_to_...( prod, &pe_prod )	*/
/*			und	 CVT_add_global_emb...( gram, prod )		*/
/*			sollte kein weiterer Befehl stehen, da auch hier analog */
/*			zu CVT_add_local_emb... auf pe_prod ueber die isonodes	*/
/*			von prod zugegriffen wird.				*/
/*										*/
/********************************************************************************/

void	CVT_add_global_embedding_of(Sgragra gram, Sprod prod)
{
	Sglobalembed	emb;
	Snode		n;
	Parsing_element pe;
	PE_embedding	pemb;
	
	for_all_sglobalembeds( gram, emb ) {
		for_all_nodes( prod->right, n ) {
			if( !strcmp( emb->node_right, n->label ) ) {
				pemb = PE_new_embedding();
				if( pemb != (PE_embedding)NULL ) {
					pemb->nodelabel = w_strsave( emb->node_embed );
					pemb->edgelabel_pre = w_strsave( emb->oldedge );
					pemb->edgelabel_post = w_strsave( emb->newedge );
					if( CVT_info.grammar_is_directed ) {
						switch( emb->olddir ) {
							case S_out:
								pemb->edgedir_pre = OUT;
								break;
							case S_in:
								pemb->edgedir_pre = IN;
								break;
							default:
								pemb->edgedir_pre = UNDEFINED;
								break;
						}
						switch( emb->newdir ) {
							case S_out:
								pemb->edgedir_post = OUT;
								break;
							case S_in:
								pemb->edgedir_post = IN;
								break;
							default:
								pemb->edgedir_post = UNDEFINED;
								break;
						}
					} else {
						pemb->edgedir_pre = UNDIRECTED;
						pemb->edgedir_post = UNDIRECTED;
					}
					pe = attr_data_of_type( n, Parsing_element );
					PE_insert_embedding( pe, pemb );
				}
			}
		} end_for_all_nodes( prod->right, n );
	} end_for_all_sglobalembeds( gram, emb );
}

/********************************************************************************/
/*										*/
/*-->	CVT_convert_sgraph_to_PE_list						*/
/*										*/
/*	PARAMETER:	1. Sgraph	graph					*/
/*			2. PE_list	*Plist		(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Transformiere einen Sgraph-Graphen 'graph' in einen	*/
/*			Parsing-Graphen 'Plist'.				*/
/*										*/
/*	BESONDERES:	Falls 'Plist' kein leerer Graph ist, wird er vor der	*/
/*			Transformation geloescht.				*/
/*			Bei jeder Graphtransformation wird der automatische	*/
/*			Nummerngenerator zurueckgesetzt.			*/
/*										*/
/********************************************************************************/

void	CVT_convert_sgraph_to_PE_list(Sgraph graph, struct parsing_element **Plist)
      	      
                				/* call BY REFERENCE ! */
{
	Snode		n;
	Sedge		e;
	Parsing_element pe;
	PE_edge 	pedge, dual_pedge;
	int		node_count = 0;
	
	if( (graph==(Sgraph)NULL) || (Plist==(PE_list *)NULL) ) {
		return;
	}
	PE_dispose_all_parsing_elements( Plist );
	
	CVT_info.x_left		=  1000000000;	/* should be max_int */
	CVT_info.x_right	= -1000000000;	/* should be min_int */
	CVT_info.y_top		=  1000000000;
	CVT_info.y_bottom	= -1000000000;
	CVT_info.graph_has_nonterminal_neighbours = FALSE;
	
	PE_reset_number_generator();
	for_all_nodes( graph, n ) {
		node_count++;
		pe = PE_new_parsing_element();
		if( pe != (Parsing_element)NULL ) {
			set_nodeattrs( n, make_attr( ATTR_DATA, (char *)pe ) );
			pe->label = w_strsave( n->label );
			pe->x = n->x;
			pe->y = n->y;
			if( n->x < CVT_info.x_left ) {
				CVT_info.x_left = n->x;
			}
			if( n->y < CVT_info.y_top ) {
				CVT_info.y_top = n->y;
			}
			if( n->x > CVT_info.x_right ) {
				CVT_info.x_right = n->x;
			}
			if( n->y > CVT_info.y_bottom ) {
				CVT_info.y_bottom = n->y;
			}
			PE_insert_parsing_element( Plist, pe );

			/********************************************************************************/
			/*			Layout Graph Grammars: BEGIN				*/
			/********************************************************************************/
			if( !ORGINAL_LAMSHOFT )
			{
				pe->prod_iso	= n;
				pe->width	= (int)node_get( graphed_node(n), NODE_WIDTH);
				pe->height	= (int)node_get( graphed_node(n), NODE_HEIGHT);
			}
			/********************************************************************************/
			/*			Layout Graph Grammars: END				*/
			/********************************************************************************/

		}
	} end_for_all_nodes( graph, n );
	
	BS_store_size( BS_PARSER_GRAPH_SIZE, node_count );
		
	node_count = 1;
	if( graph->directed ) {
		CVT_info.graph_is_directed = TRUE;
		for_all_nodes( graph, n ) {
			pe = attr_data_of_type( n, Parsing_element );
			(void) BS_init_set( &(pe->gnode_set), BS_get_size(BS_PARSER_GRAPH_SIZE) );
			BS_include( pe->gnode_set, node_count++ );
			for_sourcelist( n, e ) {
				pedge = PE_new_edge();
				dual_pedge = PE_new_edge();
				if( (pedge==(PE_edge)NULL) || (dual_pedge==(PE_edge)NULL) ) {
					PE_dispose_edge( &pedge );
					PE_dispose_edge( &dual_pedge );
				} else {
					pedge->label = w_strsave( e->label );
					pedge->partner = attr_data_of_type( e->tnode, Parsing_element );
					pedge->dir = OUT;
					pedge->dual_edge = dual_pedge;
					PE_insert_edge( pe, pedge );
					dual_pedge->label = w_strsave( e->label );
					dual_pedge->partner = pe;
					dual_pedge->dir = IN;
					dual_pedge->dual_edge = pedge;
					PE_insert_edge( pedge->partner, dual_pedge );
					if(	!MISC_is_terminal( pedge->partner->label ) &&
						!MISC_is_terminal( pe->label )
					  ) {
						CVT_info.graph_has_nonterminal_neighbours = TRUE;
					}
				}
			} end_for_sourcelist( n, e );
		} end_for_all_nodes( graph, n );
	} else {
		CVT_info.graph_is_directed = FALSE;
		for_all_nodes( graph, n ) {
			pe = attr_data_of_type( n, Parsing_element );
			(void) BS_init_set( &(pe->gnode_set), BS_get_size(BS_PARSER_GRAPH_SIZE) );
			BS_include( pe->gnode_set, node_count++ );
			for_sourcelist( n, e ) {
				if( unique_edge(e) ) {
					pedge = PE_new_edge();
					if( attr_data_of_type( e->tnode, Parsing_element )==pe ) {
						/* self_loop */
						dual_pedge = pedge;
					} else {
						dual_pedge = PE_new_edge();
					}
					if( (pedge==(PE_edge)NULL) || (dual_pedge==(PE_edge)NULL) ) {
						PE_dispose_edge( &pedge );
						PE_dispose_edge( &dual_pedge );
					} else {
						pedge->label = w_strsave( e->label );
						pedge->partner = attr_data_of_type( e->tnode, Parsing_element );
						pedge->dir = UNDIRECTED;
						pedge->dual_edge = dual_pedge;
						PE_insert_edge( pe, pedge );
						if( dual_pedge != pedge ) {
							dual_pedge->label = w_strsave( e->label );
							dual_pedge->partner = pe;
							dual_pedge->dir = UNDIRECTED;
							dual_pedge->dual_edge = pedge;
							PE_insert_edge( pedge->partner, dual_pedge );
						}
						if(	!MISC_is_terminal( pedge->partner->label ) &&
							!MISC_is_terminal( pe->label )
						  ) {
							CVT_info.graph_has_nonterminal_neighbours = TRUE;
						}
					}
				}
			} end_for_sourcelist( n, e );
		} end_for_all_nodes( graph, n );
	}
}

	
/********************************************************************************/
/*										*/
/*-->	CVT_convert_sgragra_to_PE_grammar					*/
/*										*/
/*	PARAMETER:	1. Sgragra	gragra					*/
/*			2. PE_grammar	*Pgrammar	(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Transformiere Sgragra-Grammatik in Parser-Grammatik.	*/
/*										*/
/*	BESONDERES:	Falls Pgrammar keine leere Grammatik ist, wird sie vor	*/
/*			der Transformation geloescht.				*/
/*										*/
/********************************************************************************/

void	CVT_convert_sgragra_to_PE_grammar(Sgragra gragra, PE_production *Pgrammar)
        	       
          	          				/* call BY REFERENCE! */
{
	Sprod		sprod;
	PE_production	pprod;
	
	if( Pgrammar == (PE_grammar *)NULL ) { /* invalid Pgrammar */
		return;
	}
	
	PE_reset_grammar( Pgrammar );
	
	if( gragra == (Sgragra)NULL ) { 	/* empty grammar */
		return;
	}
	
	if( ((int)(gragra->class) & 1) == 1 ) {
		CVT_info.grammar_is_directed = FALSE;
	} else {
		CVT_info.grammar_is_directed = TRUE;
	}

	CVT_info.grammar_is_boundary = TRUE;
	for_all_sprods( gragra, sprod ) {
		CVT_convert_Sprod_to_PE_production( sprod, &pprod );
		/*
		 remark: the conversion above uses CVT_convert_sgraph_to_PE_list.
		 So, every Snode in sprod->right knows its isomorph copy in 
		 pprod->right_side.
		 Now, CVT_add_global... below transforms the global embeddings
		 of gragra to local PE_embeddings of isonodes of sprod->right.
		*/
		CVT_add_global_embedding_of( gragra, sprod );
		PE_insert_production( Pgrammar, pprod );
	} end_for_all_sprods( gragra, sprod )
}

/********************************************************************************/
/*										*/
/*-->	CVT_convert_Sprod_to_PE_production					*/
/*										*/
/*	PARAMETER:	1. Sprod		prod				*/
/*			2. PE_production	*Prod	(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Transformiere Sgragra-Produktion in Parser-Produktion.	*/
/*										*/
/*	BESONDERES:	Prod wird NICHT GELOESCHT, sondern UEBERSCHRIEBEN !	*/
/*			D.h. Prod muss entweder eine Laufvariable sein, oder	*/
/*			der Programmierer hat selber dafuer zu sorgen, dass	*/
/*			Prod korrekt geloescht wird.				*/
/*										*/
/********************************************************************************/

void	CVT_convert_Sprod_to_PE_production(Sprod sp, PE_production *Prod)
{
	Graphed_node	graphed_left;
	
	if( (sp==(Sprod)NULL) || (Prod==(PE_production *)NULL) ) {
		return;
	}
	
	/* create production */
	
	*Prod = PE_new_production();


	if(*Prod == (PE_production)NULL ) {
		return;
	}
	
	/* set left_side and bounding rect */
	
	(*Prod)->left_side = w_strsave( sp->left );
	graphed_left = (Graphed_node) sp->graphed_left;
	(*Prod)->width	= (int)	node_get( graphed_left, NODE_WIDTH );
	(*Prod)->height	= (int)	node_get( graphed_left, NODE_HEIGHT );
	(*Prod)->x	= (int)	node_get( graphed_left, NODE_X ) - ((*Prod)->width / 2);
	(*Prod)->y	= (int)	node_get( graphed_left, NODE_Y ) - ((*Prod)->height / 2);
	
	/* create right side */
	
	CVT_convert_sgraph_to_PE_list( sp->right, &((*Prod)->right_side) );
	
	if( CVT_info.graph_has_nonterminal_neighbours ) {
		CVT_info.grammar_is_boundary = FALSE;
	}
	
	/* create local embeddings */
	
	/* Node attributes of sp are still valid! So, every node in sp->right	*/
	/* knows its isomorph copy in (*Prod)->right_side.			*/

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
	if( !ORGINAL_LAMSHOFT )
	{
		(*Prod)->prod_iso = sp;
	}
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/
	
	CVT_add_local_embeddings_of( sp );	
}

/********************************************************************************/
/*										*/
/*-->	CVT_show_info								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe der Variable CVT_info auf stdout.		*/
/*										*/
/********************************************************************************/

void	CVT_show_info(void)
{
	printf( "CVT_info :\n" );
	printf( "\tgrammar_scan_from = %d\n", CVT_info.grammar_scan_from );
	printf( "\tgrammar_reduce_embeddings = %d\n", CVT_info.grammar_reduce_embeddings );
	printf( "\tgrammar_reduce_productions = %d\n", CVT_info.grammar_reduce_productions );
	printf( "\tgrammar_link_isomorph_productions = %d\n", CVT_info.grammar_link_isomorph_productions );
	printf( "\tgrammar_is_directed = %d\n\n", CVT_info.grammar_is_directed );
	printf( "\tgraph_is_directed = %d\n\n", CVT_info.graph_is_directed );
}

/********************************************************************************/
/*										*/
/*-->	CVT_init								*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Initialisiere CVT_info. 				*/
/*										*/
/********************************************************************************/

void	CVT_init(void)
{
	static	int	first = TRUE;
	if( first ) {
		CVT_info.grammar_scan_from = SGG_ALL_WINDOWS - 1;

				/* -1 because we have no "scan from (sgg_)undefined" */

		CVT_info.grammar_reduce_embeddings = TRUE;
		CVT_info.grammar_reduce_productions = TRUE;
		CVT_info.grammar_link_isomorph_productions = TRUE;
		first = FALSE;
	}
}

/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mconvert								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fconvert							*/
/*m		-Econvert							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dconvert							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pconvert							*/
/********************************************************************************/

