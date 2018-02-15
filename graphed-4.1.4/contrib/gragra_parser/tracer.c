
#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include "misc.h"
#include "types.h"
#include "debug.h"
#include "lab_int.h"
#include "convert.h"
#include "parser.h"
#include "graph_op.h"

#include "tracer.h"


#define	EMPTY_ATTR \
	make_attr( ATTR_DATA, NULL )

#define	TRC_GRAPHNODE	TRUE
#define TRC_OPTION	FALSE

/*-->@	-Ptracer

void	TRC_init		()
void	TRC_activate		()
void	TRC_reset		()
void	TRC_make_start_graph	()
void	TRC_insert_node		( pe )
void	TRC_complete_graph	()
void	TRC_replace_node	()
void	TRC_remove_node		( trc_pe )
void	TRC_re_insert_node	()
void	TRC_kill_node		( trc_pe )
void	TRC_remove_graph	()
void	TRC_deactivate		()

void	TRC_check_sgraph	( info )
void	TRC_make_sgraph_style	( info )

void	TRC_make_snode_attrs	( snode )
void	TRC_remove_snode_attrs	( snode )
void	TRC_set_snode_attrs	( snode, graph_or_option, pe )
int	TRC_get_snode_attrs	( snode , &attr )

void	TRC_init_sgraph		()
void	TRC_make_sgraph		()
void	TRC_make_options_sgraph	()
void	TRC_remake_sgraph	()
Snode	TRC_get_snode_selection	()
void	TRC_clear_sgraph	()
void	TRC_remove_sgraph	()

**/

void	TRC_make_snode_attrs(Snode sn)
{
	TRC_snode_attribute	tmp;
	tmp = (TRC_snode_attribute) w_malloc( sizeof(struct trc_snode_attribute) );
	if( tmp != NULL ) {
		tmp->is_graphnode = FALSE;
		tmp->pe = NULL;
	}
	set_nodeattrs( sn, make_attr( ATTR_DATA, tmp ) );
}

void	TRC_set_snode_attrs(Snode sn, int graph_or_option, Parsing_element pe)
{
	TRC_snode_attribute	tmp;
	
	tmp = attr_data_of_type( sn, TRC_snode_attribute );
	if( tmp != NULL ) {
		tmp->is_graphnode = graph_or_option;
		tmp->pe = pe;
	}
}

int	TRC_get_snode_attrs(Snode sn, TRC_snode_attribute *Attrs)
     	   
                   	       		/* call BY REFERENCE! */
{
	*Attrs = NULL;
	if( sn == NULL ) return FALSE;
	
	*Attrs = attr_data_of_type( sn, TRC_snode_attribute );
	if( *Attrs == NULL ) {
		return FALSE;
	} else {
		return TRUE;
	}
}

void	TRC_remove_snode_attrs(Snode sn)
{
	Parsing_element	tmp;
	
	if( sn != NULL ) {
		tmp = attr_data_of_type( sn, Parsing_element );
		if( tmp != NULL ) {
			w_free((char *) tmp );
		}
		set_nodeattrs( sn, EMPTY_ATTR );
	}
}

void	TRC_remake_sgraph(void)
{
	TRC_remove_sgraph();
	TRC_make_sgraph();
	TRC_make_options_sgraph();
}

void	TRC_check_sgraph(Sgraph_proc_info info)
{
	Snode			sn;
	Parsing_element		pe;
	TRC_snode_attribute	attr;
	
	if( (info==NULL) || (info->sgraph==NULL) || (info->sgraph!=TRC_info.sgraph)) {
		TRC_info.message = "You must select the tracing graph!";
		TRC_info.test_result = FALSE;
		return;
	}
	
	/* check if user added some nodes to the tracing graph */
	for_all_nodes( info->sgraph, sn ) {
		if( !TRC_get_snode_attrs( sn, &attr ) ) {
			TRC_info.message = "You must not modify the tracing graph!";
			TRC_info.test_result = FALSE;
			TRC_remake_sgraph();
			return;
		} else if( attr->is_graphnode ) {
			attr->pe->x = sn->x - TRC_info.x_offset;
			attr->pe->y = sn->y - TRC_info.y_offset;
		}
	} end_for_all_nodes( info->sgraph, sn );
	
	/* check if user removed some nodes of the tracing graph */
	PE_for_all_parsing_elements( TRC_info.graph, pe ) {
		if( pe->snode == NULL ) {
			TRC_info.message = "You must not modify the tracing graph!";
			TRC_info.test_result = FALSE;
			TRC_remake_sgraph();
			return;
		}
	} PE_end_for_all_parsing_elements( TRC_info.graph, pe );
/*
	if(	(info->selected==SGRAPH_SELECTED_SNODE) &&
		(info->selection.snode != NULL)		&&
		(attr_data(info->selection.snode)==NULL)
	   ) {
		TRC_info.message = "Please don't touch THIS node!";
		TRC_info.test_result = FALSE;
		return;
	}
*/
	TRC_info.test_result = TRUE;
	return;
}
		
void	TRC_activate(void)
{
	if(	((PRS_info.status == PRS_FINISHED) || (PRS_info.status == PRS_PAUSED))
/*
	     && (TRC_info.status == TRC_ACTIVE)
*/
	     && (PRS_info.start_elements != NULL)
	   ) {
		TRC_make_start_graph();
		TRC_info.status = TRC_RESET;
	}
}

void	TRC_reset(void)
{
	TRC_remove_graph();
	TRC_make_start_graph();
	TRC_info.expand_pe = NULL;
}

void	TRC_insert_node(Parsing_element pe)
{
	Parsing_element	trc_pe, lpe;
	PE_edge		edge, tmp_edge, tmp_dual_edge;
	int		tst;
	int /* bool */	error;
	
	if( pe == NULL ) {
		return;
	}
	
	trc_pe = PE_new_parsing_element();
	if( trc_pe == NULL ) {
		return;
	}
	
	/* else */
	
	trc_pe->label = w_strsave( pe->label );
	trc_pe->label_num = pe->label_num;
	trc_pe->nummer = pe->nummer;
	trc_pe->level = pe->level;
	trc_pe->trc_iso = pe;
	pe->trc_iso = trc_pe;
	ATTRS_SET( pe, PE_traced );
	
	if( pe->level == 0 ) {
		trc_pe->x = pe->x - PRS_info.graph_x_left;
		trc_pe->y = pe->y - PRS_info.graph_y_top;
	} else {
		for( tst = 1; tst <= PRS_info.graph_size; tst++ ) {
			if( BS_is_in_set( pe->gnode_set, tst ) ) {
				PE_for_all_parsing_elements( PRS_info.pe.tab, lpe ) {
					if( BS_is_in_set( lpe->gnode_set, tst ) ) {
						trc_pe->x = lpe->x - PRS_info.graph_x_left;
						trc_pe->y = lpe->y - PRS_info.graph_y_top;
						break;
					}
				} PE_end_for_all_parsing_elements( PRS_info.pe.tab, lpe );
				break;
			}
		}
	}
	
	error = FALSE;
	PE_for_all_edges( pe, edge ) {
		if( (edge->dir!=BLOCKING) && (ATTRS_TEST( edge->partner, PE_traced ) ) ) {
			tmp_edge = PE_new_edge();
			tmp_dual_edge = PE_new_edge();
			if( (tmp_edge==NULL) || (tmp_dual_edge==NULL) ) {
				PE_dispose_edge( &tmp_edge );
				PE_dispose_edge( &tmp_dual_edge );
				error = TRUE;
				break;
			} else {
				tmp_edge->label = w_strsave( edge->label );
				tmp_edge->label_num = edge->label_num;
				tmp_edge->dir = edge->dir;
				tmp_edge->partner = edge->partner->trc_iso;
				tmp_edge->dual_edge = tmp_dual_edge;
				ATTRS_SET( tmp_edge, PE_real );
				
				tmp_dual_edge->label = w_strsave( edge->label );
				tmp_dual_edge->label_num = edge->label_num;
				tmp_dual_edge->dir = edge->dual_edge->dir;
				tmp_dual_edge->partner = trc_pe;
				tmp_dual_edge->dual_edge = tmp_edge;
				ATTRS_SET( tmp_dual_edge, PE_real );

				PE_insert_edge( trc_pe, tmp_edge );
				PE_insert_edge( tmp_edge->partner, tmp_dual_edge );
			}
		}
	} PE_end_for_all_edges( pe, edge );
	
	if( !error ) {
		PE_insert_parsing_element( &(TRC_info.graph), trc_pe );
	} else {
		PE_dispose_parsing_element( &(trc_pe) );
	}
}

void	TRC_remove_node(Parsing_element trc_pe)
{
	PE_edge	e;
	
	if( (trc_pe==NULL) ) {
		return;
	}
	PE_for_all_edges( trc_pe, e ) {
		PE_remove_edge( e->partner, e->dual_edge );
	} PE_end_for_all_edges( trc_pe, e );
	PE_remove_parsing_element( &(TRC_info.graph), trc_pe );
	
	ATTRS_CLEAR( trc_pe->trc_iso, PE_traced );
}

void	TRC_complete_graph(void)
{
	Bitset		node_set = NULL;
	Parsing_element	pe, pe2, tmp_pe;
	PE_edge		e;
	int		tmp_level;
	int		correct;
	int		special_complete = TRUE;
	
	if( !BS_init_set( &node_set, PRS_info.graph_size ) ) {
		return;
	}
	
	PE_for_all_parsing_elements( TRC_info.graph, pe ) {
		BS_union( node_set, pe->trc_iso->gnode_set, node_set );
	} PE_end_for_all_parsing_elements( TRC_info.graph, pe );
	
	if( !special_complete ) {
		PE_for_all_parsing_elements( PRS_info.pe.tab, pe ) {
			if( (pe->level==0) && !BS_is_in_set( node_set, pe->nummer ) ) {
				TRC_insert_node( pe );
				BS_include( node_set, pe->nummer );
			}
		} PE_end_for_all_parsing_elements( PRS_info.pe.tab, pe );
	} else {
		do {
			tmp_pe = NULL; tmp_level = -1;
			PE_for_all_parsing_elements( PRS_info.pe.tab, pe ) {
				if( BS_empty_intersection( node_set, pe->gnode_set ) ) {
					if( pe->level > tmp_level ) {
						correct = TRUE;
						PE_for_all_parsing_elements( TRC_info.graph, pe2 ) {
							PE_for_all_edges( pe2->trc_iso, e ) {
								if(	(e->partner == pe)	&&
									(e->dir == BLOCKING)
								   ) {
									correct = FALSE;
									break;
								}
							} PE_end_for_all_edges( pe2, e);
						} PE_end_for_all_parsing_elements( TRC_info.graph, pe2 );
						if( correct ) {
							tmp_level = pe->level;
							tmp_pe = pe;
						}
					}
				}
			} PE_end_for_all_parsing_elements( PRS_info.pe.tab, pe );
			if( tmp_pe != NULL ) {
				TRC_insert_node( tmp_pe );
				BS_union( node_set, tmp_pe->gnode_set, node_set );
			}
		} while( tmp_pe != NULL );
	}
	BS_delete_set( &node_set );
}

void	TRC_replace_node(void)
{
	PE_set		lauf, lauf2;
	TRC_list	tmp;
	Parsing_element	trc_pe;
	Parsing_element	right_side_pe;
	
	PE_edge		e, tmp_edge, tmp_dual_edge;
	PE_embedding	emb;
	
	trc_pe = TRC_info.expand_pe;
	
	if( (trc_pe==NULL) || (trc_pe->trc_iso->level==0) ) {
		TRC_info.message = "Can't expand this node (selected other)";
		return;
	}
	
	tmp = (TRC_list) w_malloc( sizeof( struct trc_list ) );
	if( tmp == NULL ) {
		return;
	}
	
	TRC_remove_node( trc_pe );
	tmp->replaced_node = trc_pe;
	tmp->nr_expansion = TRC_info.nr_expansion;
	tmp->next_exp_possible = TRC_info.next_exp_possible;
	tmp->succ = TRC_info.working_list;
	TRC_info.working_list = tmp;

	right_side_pe = TRC_info.expand_through;
	
	tmp->expand_through = right_side_pe;
	
	lauf = right_side_pe->right_side;
	while( lauf != NULL ) {
		TRC_insert_node( lauf->pe );
		
		/* complete right side with virtual edges (those, who will be lost during derivation) */
		
		GPO_unmark_PE_edgelist( lauf->pe->trc_iso->edges );
		lauf2 = right_side_pe->right_side;
		while( lauf2 != lauf ) {
			PE_for_all_edges( lauf2->prod_iso, e ) {
				if( e->partner == lauf->prod_iso ) {
					tmp_edge = PE_new_edge();
					tmp_dual_edge = PE_new_edge();
					if( (tmp_edge!=NULL) && (tmp_dual_edge!=NULL) ) {
						tmp_edge->label = w_strsave( e->label );
						tmp_edge->label_num = e->label_num;
						tmp_edge->partner = lauf->pe->trc_iso;
						tmp_edge->dir = e->dir;
						tmp_edge->dual_edge = tmp_dual_edge;
						ATTRS_SET( tmp_edge, PE_virtual );
						
						tmp_dual_edge->label = w_strsave( e->label );
						tmp_dual_edge->label_num = e->label_num;
						tmp_dual_edge->partner = lauf2->pe->trc_iso;
						tmp_dual_edge->dir = PE_inverse_dir( e->dir );
						tmp_dual_edge->dual_edge = tmp_edge;
						ATTRS_SET( tmp_dual_edge, PE_virtual );
						
						if( !GPO_find_isomorph_edge( lauf->pe->trc_iso->edges, tmp_dual_edge ) ) {
						
							PE_insert_edge( lauf2->pe->trc_iso, tmp_edge );
							PE_insert_edge( lauf->pe->trc_iso , tmp_dual_edge );
							
						} else { /* edge already contained in parsing graph */
						
							PE_dispose_both_edges( &tmp_edge );
						}
					} else {
						PE_dispose_edge( &tmp_edge );
						PE_dispose_edge( &tmp_dual_edge );
					}
				}
			} PE_end_for_all_edges( lauf2->prod_iso, e );
			lauf2 = lauf2->succ;
		}
		
		/* complete embeddings with virtual edges (those, who will be lost during derivation) */
		
		GPO_unmark_PE_edgelist( lauf->pe->trc_iso->edges );
		PE_for_all_edges( trc_pe, e ) {
			PE_for_all_embeddings( lauf->prod_iso, emb ) {
				if(	(e->label_num == emb->edgelabel_pre_num)	&&
					(e->dir == emb->edgedir_pre)			&&
					(e->partner->label_num == emb->nodelabel_num)
				  ) {
					tmp_edge = PE_new_edge();
					tmp_dual_edge = PE_new_edge();
					if( (tmp_edge!=NULL) && (tmp_dual_edge!=NULL) ) {
						tmp_edge->label = w_strsave( emb->edgelabel_post );
						tmp_edge->label_num = emb->edgelabel_post_num;
						tmp_edge->partner = e->partner;
						tmp_edge->dir = emb->edgedir_post;
						tmp_edge->dual_edge = tmp_dual_edge;
						ATTRS_SET( tmp_edge, PE_virtual );
						
						tmp_dual_edge->label = w_strsave( emb->edgelabel_post );
						tmp_dual_edge->label_num = emb->edgelabel_post_num;
						tmp_dual_edge->partner = lauf->pe->trc_iso;
						tmp_dual_edge->dir = PE_inverse_dir( emb->edgedir_post );
						tmp_dual_edge->dual_edge = tmp_edge;
						ATTRS_SET( tmp_dual_edge, PE_virtual );
						
						if( !GPO_find_isomorph_edge( lauf->pe->trc_iso->edges, tmp_edge ) ) {
							PE_insert_edge( lauf->pe->trc_iso, tmp_edge );
							PE_insert_edge( e->partner, tmp_dual_edge );
						} else {
							PE_dispose_both_edges( &tmp_edge );
						}
					} else {
						PE_dispose_edge( &tmp_edge );
						PE_dispose_edge( &tmp_dual_edge );
					}
				}
			} PE_end_for_all_embeddings( lauf->prod_iso, emb );
		} PE_end_for_all_edges( trc_pe, e );
		GPO_unmark_PE_edgelist( lauf->pe->trc_iso->edges );
				
		PE_add_to_PE_set( &( trc_pe->right_side ), lauf->pe->trc_iso );
		lauf = lauf->succ;
	}
	TRC_info.status = TRC_EXPAND;
}

void	TRC_kill_node(Parsing_element trc_pe)
{
	PE_edge	e, tmp;
	
	trc_pe->trc_iso->trc_iso = NULL;
	ATTRS_CLEAR( trc_pe->trc_iso, PE_traced );

	e = trc_pe->edges;
	while( e != NULL ) {
		tmp = e->succ;
		PE_remove_both_edges( e );
		PE_dispose_both_edges( &e );
		e = tmp;
	}
	PE_remove_parsing_element( &(TRC_info.graph), trc_pe );
	PE_dispose_parsing_element( &trc_pe );
}

void	TRC_re_insert_node(void)
{
	Parsing_element	trc_pe;
	PE_set		lauf;
	TRC_list	list;
	PE_edge		e;
	
	list = TRC_info.working_list;
	if( list != NULL ) {
		trc_pe = list->replaced_node;
		trc_pe->trc_iso->trc_iso = trc_pe;
		ATTRS_SET( trc_pe->trc_iso, PE_traced );
		PE_for_all_edges( trc_pe, e ) {
			PE_insert_edge( e->partner, e->dual_edge );
		} PE_end_for_all_edges( trc_pe, e );
		PE_insert_parsing_element( &(TRC_info.graph), trc_pe );
		TRC_info.expand_pe = trc_pe;
		TRC_info.nr_expansion = list->nr_expansion;
		TRC_info.expand_through = list->expand_through;
		TRC_info.next_exp_possible = list->next_exp_possible;
		TRC_info.working_list = list->succ;
		w_free((char *) list );
		lauf = trc_pe->right_side;
		while( lauf != NULL ) {
			TRC_kill_node( lauf->pe );
			lauf = lauf->succ;
		}
		PE_delete_PE_set( &(trc_pe->right_side) );
	} 
}

/* loescht TRC_info.sgraph */

void	TRC_remove_graph(void)
{

	while( TRC_info.working_list != NULL ) {
		TRC_re_insert_node();
	}	
	while( TRC_info.graph != NULL ) {
		TRC_kill_node( TRC_info.graph );
	}
}

/* loescht alle Labels und Attribute von TRC_info.sgraph */

void	TRC_clear_sgraph(void)
{
	Snode	sn;
	Sedge	se;
	if( TRC_info.sgraph==NULL) {
		return;
	}
	
	for_all_nodes( TRC_info.sgraph, sn ) {
		sn->label = NULL;
		attr_data(sn) = NULL;
		for_sourcelist( sn, se ) {
			se->label = NULL;
		} end_for_sourcelist( sn, se );
	} end_for_all_nodes( TRC_info.sgraph, sn );
}

/* loescht alle Sgraph-Knoten aus TRC_info.sgraph */

void	TRC_remove_sgraph(void)
{	
	Slist	list, lauf;
	Snode	sn;
	
	if( TRC_info.sgraph == NULL ) {
		return;
	}
	
	TRC_clear_sgraph();
	
	/* remove all Snodes from TRC_info.sgraph */
	
	list = NULL;
	for_all_nodes( TRC_info.sgraph, sn ) {
		list = add_to_slist( list, make_attr( ATTR_DATA, sn ) );
	} end_for_all_nodes( TRC_info.sgraph, sn );
	
	for_slist( list, lauf ) {
	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
	if( ORGINAL_LAMSHOFT )
	{ 
		remove_node( attr_data_of_type( lauf, Snode ) );
	}
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/
	} end_for_slist( list, lauf );

	free_slist( list );

	/* no remove_graph( TRC_info.sgraph ) allowed, because GraphEd needs this information.
	   So, just forget the graph and let GraphEd free the associated memory */

}

/* macht Sgraph-Kopie von TRC_info.graph in TRC_info.sgraph (letzterer muss bei Aufruf leer sein) */

void	TRC_make_sgraph(void)
{
	Parsing_element	pe;
	PE_edge		e;
	Snode		sn;
	Sedge		se;
	Graphed_node	gred_node;
	Graphed_edge	gred_edge;
	
	int	node_box_num, node_grey_box_num, node_diamond_num;
	int	edge_solid_num,	edge_dotted_num;
	
	node_box_num = add_nodetype( "#box" );
	node_diamond_num = add_nodetype( "#diamond" );
	node_grey_box_num = add_nodetype( "gragra_parser/grey_box.icon" );
	
	edge_solid_num = add_edgetype( "#solid" );
	edge_dotted_num = add_edgetype( "#dotted" );
	
	PE_for_all_parsing_elements( TRC_info.graph, pe ) {
		ATTRS_CLEAR( pe , PE_traced );
	} PE_end_for_all_parsing_elements( TRC_info.graph, pe );

	PE_for_all_parsing_elements( TRC_info.graph, pe ) {
		sn = make_node( TRC_info.sgraph, EMPTY_ATTR );
		TRC_set_snode_attrs( sn, TRC_GRAPHNODE, pe );
		if( sn != NULL ) {
			ATTRS_SET( pe , PE_traced );
			pe->snode = (char *) sn;
			set_nodelabel( sn, pe->label );
			sn->x = pe->x + TRC_info.x_offset;
			sn->y = pe->y + TRC_info.y_offset;
			
			gred_node = create_graphed_node_from_snode( sn );
			if( pe->trc_iso->level > 0 ) {
				if( pe == TRC_info.expand_pe ) {
					node_set( gred_node,	NODE_TYPE,	node_grey_box_num,
								0 );
				} else {
					node_set( gred_node,	NODE_TYPE,	node_box_num,
								0 );
				}
			} else {
				node_set( gred_node, NODE_TYPE, node_diamond_num, 0 );
			}
			
			PE_for_all_edges( pe, e ) {
				if( ATTRS_TEST( e->partner, PE_traced ) ) {
					if( e->dir == IN ) {
						se = make_edge( 	(Snode)e->partner->snode, 
									(Snode)pe->snode,
									make_attr( ATTR_DATA, e )
								);
					} else {
						se = make_edge(		(Snode)pe->snode,
									(Snode)e->partner->snode,
									make_attr( ATTR_DATA, e )
								);
					}
					if( se != NULL ) {
						set_edgelabel( se, e->label );
						gred_edge = create_graphed_edge_from_sedge( se );
						if( ATTRS_TEST( e, PE_virtual ) ) {
							edge_set( gred_edge, EDGE_TYPE, edge_dotted_num, 0 );
						} else {
							edge_set( gred_edge, EDGE_TYPE, edge_solid_num, 0 );
						}
					}
				}
			} PE_end_for_all_edges( pe, e );
		}
	} PE_end_for_all_parsing_elements( TRC_info.graph, pe );
}

void	TRC_make_options_sgraph(void)
{
	Parsing_element	current_pe, pe;
	Snode		sn;
	Graphed_node	gred_node;
	PE_production	prod;
	int		hilf;
	int		node_star_num, node_solid_star_num;
	
	if( (TRC_info.expand_pe == NULL) || (TRC_info.default_expansion) ) {
		return;
	}
	
	if(	(TRC_info.expand_pe->trc_iso != NULL)	&&
		(TRC_info.expand_pe->trc_iso->level < 1)
	   ) {
		TRC_info.message = "Level-0 node has no expand options!";
		return;
	}
	
	node_star_num = add_nodetype( "gragra_parser/star.icon" );
	node_solid_star_num = add_nodetype( "gragra_parser/star_solid.icon" );
	
	
	PE_for_all_productions( PRS_info.grammar, prod ) {
		prod->nr_options = 0;
	} PE_end_for_all_productions( PRS_info.grammar, prod );
	
	current_pe = TRC_info.expand_pe->trc_iso->isomorph_main_pe;
	sn = make_node( TRC_info.sgraph, EMPTY_ATTR );
	TRC_set_snode_attrs( sn, TRC_OPTION, current_pe );
	if( sn != NULL ) {
		prod = current_pe->which_production;
		
		switch( TRC_info.option_node_position ) {
			case	NP_TOP:
			case	NP_BOTTOM:	hilf = prod->width / 20;
						if( hilf < 1 ) {
							hilf = 1;
						}
						break;
			case	NP_LEFT:
			case	NP_RIGHT:	hilf = prod->height / 20;
						if( hilf < 1 ) {
							hilf = 1;
						}
						break;
		}
		
		switch( TRC_info.option_node_position ) {
			case	NP_TOP:		sn->y = prod->y - 15 - 20*((prod->nr_options) / hilf);
						sn->x = prod->x + 11 + 20*(prod->nr_options % hilf);
						break;
			case	NP_LEFT:	sn->x = prod->x - 15 - 20*(prod->nr_options / hilf);
						sn->y = prod->y + 11 + 20*((prod->nr_options) % hilf);
						break;
			case	NP_RIGHT:	sn->x = prod->x + prod->width + 15 + 20*(prod->nr_options / hilf);
						sn->y = prod->y + 11 + 20*((prod->nr_options) % hilf);
						break;
			case	NP_BOTTOM:	sn->y = prod->y + prod->height + 15 + 20*((prod->nr_options) / hilf);
						sn->x = prod->x + 11 + 20*(prod->nr_options % hilf);
						break;
		}
		
		prod->nr_options++;
		
		gred_node = create_graphed_node_from_snode( sn );
		if( current_pe == TRC_info.expand_through ) {
			node_set( gred_node,	NODE_TYPE,	node_solid_star_num,
						NODE_LABEL_VISIBILITY,	FALSE,
						NODE_SIZE,		16, 16,
						0 );
		} else {
			node_set( gred_node,	NODE_TYPE,	node_star_num,
						NODE_LABEL_VISIBILITY,	FALSE,
						NODE_SIZE,		16, 16,
						0 );
		}
	}
			
	pe = current_pe->isomorph_pes;
	while( pe != NULL ) {
		sn = make_node( TRC_info.sgraph, EMPTY_ATTR );
		TRC_set_snode_attrs( sn, TRC_OPTION, pe );
		if( sn != NULL ) {
			prod = pe->which_production;
			
			switch( TRC_info.option_node_position ) {
				case	NP_TOP:
				case	NP_BOTTOM:	hilf = prod->width / 20;
							if( hilf < 1 ) {
								hilf = 1;
							}
							break;
				case	NP_LEFT:
				case	NP_RIGHT:	hilf = prod->height / 20;
							if( hilf < 1 ) {
								hilf = 1;
							}
							break;
			}
			
			switch( TRC_info.option_node_position ) {
				case	NP_TOP:		sn->y = prod->y - 15 - 20*((prod->nr_options) / hilf);
							sn->x = prod->x + 11 + 20*(prod->nr_options % hilf);
							break;
				case	NP_LEFT:	sn->x = prod->x - 15 - 20*(prod->nr_options / hilf);
							sn->y = prod->y + 11 + 20*((prod->nr_options) % hilf);
							break;
				case	NP_RIGHT:	sn->x = prod->x + prod->width + 15 + 20*(prod->nr_options / hilf);
							sn->y = prod->y + 11 + 20*((prod->nr_options) % hilf);
							break;
				case	NP_BOTTOM:	sn->y = prod->y + prod->height + 15 + 20*((prod->nr_options) / hilf);
							sn->x = prod->x + 11 + 20*(prod->nr_options % hilf);
							break;
			}
		
			prod->nr_options++;
			
			gred_node = create_graphed_node_from_snode( sn );
			if( pe == TRC_info.expand_through ) {
				node_set( gred_node,	NODE_TYPE,	node_solid_star_num,
							NODE_LABEL_VISIBILITY,	FALSE,
							NODE_SIZE,		16, 16,
							0 );
			} else {
				node_set( gred_node,	NODE_TYPE,	node_star_num,
							NODE_LABEL_VISIBILITY,	FALSE,
							NODE_SIZE,		16, 16,
							0 );
			}
		}
		pe = pe->succ;
	}
}

void	TRC_make_sgraph_style(Sgraph_proc_info info)
{
	int	node_box_num, node_diamond_num, 
		node_star_num, node_solid_star_num,
		node_grey_box_num, node_black_box_num;
	int	edge_solid_num,	edge_dotted_num;
	TRC_snode_attribute	attr;
	
	PE_edge		e;
	
	Snode	sn;
	Sedge	se;
	
	Graphed_node	gred_node;
	Graphed_edge	gred_edge;
	
	if( info == NULL ) {
		return;
	}
	
	node_box_num		= add_nodetype( "#box" );
	node_diamond_num 	= add_nodetype( "#diamond" );
	node_star_num 		= add_nodetype( "gragra_parser/star.icon" );
	node_solid_star_num 	= add_nodetype( "gragra_parser/star_solid.icon" );
	node_grey_box_num 	= add_nodetype( "gragra_parser/grey_box.icon" );
	node_black_box_num 	= add_nodetype( "gragra_parser/black_box.icon" );
	
	edge_solid_num = add_edgetype( "#solid" );
	edge_dotted_num = add_edgetype( "#dotted" );
	
	for_all_nodes( TRC_info.sgraph, sn ) {
		(void) TRC_get_snode_attrs( sn, &attr );
		gred_node = graphed_node( sn );
		if( (attr != NULL) && (gred_node != NULL) ) {
			if( attr->is_graphnode ) {
				if( attr->pe->trc_iso->level == 0 ) {
					node_set( gred_node, NODE_TYPE, node_diamond_num, 0);
				} else {
					if( attr->pe->trc_iso != NULL /* selected_node */ ) {
						if( attr->pe->trc_iso->isomorph_pes == NULL ) {
							node_set( gred_node, NODE_TYPE, node_black_box_num, 0 );
						} else {
							node_set( gred_node, NODE_TYPE, node_grey_box_num, 0 );
						}
					} else {
						node_set( gred_node, NODE_TYPE, node_box_num, 0);
					}
				}
			} else {
				if( attr->pe != NULL /* current_option */ ) {
					node_set( gred_node, NODE_TYPE, node_star_num, 0 );
				} else {
					node_set( gred_node, NODE_TYPE, node_solid_star_num, 0 );
				}
			}
		}
		for_sourcelist( sn, se ) {
			e = attr_data_of_type( se, PE_edge );
			gred_edge = graphed_edge( se );
			if( (e != NULL) && (gred_edge != NULL) ) {
				if( ATTRS_TEST( e, PE_virtual ) ) {
					edge_set( gred_edge, EDGE_TYPE, edge_dotted_num, 0);
				} else {
					edge_set( gred_edge, EDGE_TYPE, edge_solid_num, 0);
				}
			}
		} end_for_sourcelist( sn, se );
	} end_for_all_nodes( TRC_info.sgraph, sn );
}

/* loescht TRC_info.graph und setzt Status auf TRC_INACTIVE */

void	TRC_deactivate(void)
{
/*
	while( TRC_info.working_list != NULL ) {
		TRC_re_insert_node();
	}
*/
	TRC_remove_graph();
	TRC_info.status = TRC_INACTIVE;
}


/* initialisiert TRC_info und generiert TRC_info.sgraph (zunaechst leer) */

void	TRC_init(void)
{
	TRC_info.status = TRC_INACTIVE;
	TRC_info.working_list = NULL;
	TRC_info.sgraph = NULL;
	TRC_info.selected_snode = NULL;
	TRC_info.graph = NULL;
	TRC_info.nr_start_element = 0;
	TRC_info.next_start_possible = FALSE;
	TRC_info.expand_pe = NULL;
	TRC_info.nr_expansion = 0;
	TRC_info.expand_through = NULL;
	TRC_info.next_exp_possible = FALSE;
	TRC_info.message = "";
	TRC_info.default_expansion = FALSE;
}

void	TRC_init_sgraph(void)
{
	if( TRC_info.sgraph != NULL ) {
		return;
	}
	
	TRC_info.sgraph = make_graph( EMPTY_ATTR );
	if( TRC_info.sgraph != NULL ) {
	
		TRC_info.sgraph->directed = PRS_info.graph_directed;
		TRC_info.sgraph->make_node_proc = TRC_make_snode_attrs;
		TRC_info.sgraph->remove_node_proc = TRC_remove_snode_attrs;
		(void) create_graphed_graph_from_sgraph( TRC_info.sgraph);
		TRC_info.status = TRC_ACTIVE;
	}
}

/* initialisiert TRC_info.graph gemaess PRS_info.start_elements und TRC_info.nr_start_element */

void	TRC_make_start_graph(void)
{
	PE_set	lauf;
	int	i;
	
	lauf = PRS_info.start_elements;
	
	if( lauf == NULL ) {
		return;
	}
	
	i = TRC_info.nr_start_element;
	while( (i>0) && (lauf->succ!=NULL) ) {
		lauf = lauf->succ;
		i--;
	}
	PRS_info.start_elements -= i;
	
	TRC_insert_node( lauf->pe );
	TRC_complete_graph();
	
	if( lauf->succ != NULL ) {
		TRC_info.next_start_possible = TRUE;
	} else {
		TRC_info.next_start_possible = FALSE;
	}
	TRC_info.status = TRC_RESET;
}

/* setzt TRC_info.selected_node auf (letztes) ableitbares PE */

Snode	TRC_get_snode_selection(void)
{
	Snode			sn, tmp_sn;
	Parsing_element		trc_pe;
	TRC_snode_attribute	attr;
	int 			current_max_level = -1, found;
	
	TRC_info.selected_snode = NULL;
	if( TRC_info.status != TRC_SELECT ) {
		for_all_nodes( TRC_info.sgraph, sn ) {
			if( TRC_get_snode_attrs( sn, &attr ) && (attr->is_graphnode) ) {
				trc_pe = attr->pe;
				if( (trc_pe!=NULL) && (trc_pe->trc_iso->level>current_max_level) ) {
					TRC_info.selected_snode = sn;
					current_max_level = trc_pe->trc_iso->level;
				}
			}
		} end_for_all_nodes( TRC_info.sgraph, sn );
/*
		if( current_max_level < 1 ) {
			TRC_info.selected_snode = NULL;
		}
*/
	} else {
		tmp_sn = NULL;	found = FALSE;
		
		for_all_nodes( TRC_info.sgraph, sn ) {
			if( TRC_get_snode_attrs( sn, &attr ) && !(attr->is_graphnode) ) {
				if( tmp_sn == NULL ) {
					tmp_sn = sn;
				}
				if( !found ) {
					if( TRC_info.expand_through == attr->pe ) {
						found = TRUE;
					}
				} else {
					TRC_info.selected_snode = sn;
					break;
				}
			}
		} end_for_all_nodes( TRC_info.sgraph, sn );
		if( TRC_info.selected_snode == NULL ) {
			TRC_info.selected_snode = tmp_sn;
		}
	}
	return TRC_info.selected_snode;
		
}
