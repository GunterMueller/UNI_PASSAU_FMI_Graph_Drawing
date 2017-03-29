#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "graphed_subwindows.h"
#include "dispatch_commands.h"

#include <graphed/menu.h>

#include <X11/Xos.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>
#include "lp_win_help.h"

#include "draw.h"

#include "lp_assign_edge.h"
#include "lp_subframe.h"
#include "lp_general_functions.h"
#include "lp_edgeline.h"
#include "lp_free_tree.h"
#include "lp_test.h"
#include "lp_make_changes_in_productions.h"
#include "lp_top_sort.h"
#include "lp_history.h"
#include "lp_tree_top_sort.h"
#include "lp_draw.h"
#include "lpr_ggraph.h"
#include "lpr_apply_production.h"
#include "lpr_plr_system.h"
#include "lpr_top_down_cost_opt.h"
#include "lpm_create_lgg.h"
#include "lpm_create_lgg_oriented.h"
#include "lpm_iso_test.h"
#include "lpp_parse.h"
#include "lpp_clear_table.h"
#include "lpp_functions.h"
#include "lpa_redraw.h"
#include "lpa_draw_new_tree.h"

/* Algorithmen fuer das USER-Menu */
#include "lp_reduce.h"
#include "lp_open.h"
#include "lp_delete.h"
#include "lp_draw_tree.h"

/****************************************************************************************/
/*											*/
/* Schnittstellenmodul zu GraphEd. Saemtliche Funktionen die in orginal GraphEd-	*/
/* funktionen aufgerufen werden, werden hier definiert					*/
/*											*/
/****************************************************************************************/


/****************************************************************************************/
/*											*/
/* Funktionen zu gragra.c								*/
/*											*/
/****************************************************************************************/

/*****************************************************************************************
function:	lp_production_test_and_change
Input:	Graph prod

	Fuehrt die fuer lgg notwendigen Tests und Veraenderungen der Produktion durch

Output:	---
*****************************************************************************************/

void	lp_production_test_and_change(Graph prod)
{
	int	index;

	for ( index = 0; index < PRECONDITION_COUNTER; index++)	/* Setze zunaechst alle Eigenschaften auf TRUE	*/
	{
		prod->lp_graph.properties_array[index] = 1;
	}

	if( !additional_bnlc_test( prod ) ) 
	{
		prod->lp_graph.properties_array[BNLC_GRAMMAR] = 0;
		grammar_preconditions[BNLC_GRAMMAR] = FALSE;
	}

	if( !(prod->gra.type == ENCE_1 ) ) 
	{
		prod->lp_graph.properties_array[ENCE_1_GRAMMAR] = 0;
		grammar_preconditions[ENCE_1_GRAMMAR] = FALSE;
	}

	if( number_of_in_embedding_edges_is_incorrect( prod ) )
	{
		prod->lp_graph.properties_array[SIMPLE_EMBEDDINGS] = 0;
		grammar_preconditions[SIMPLE_EMBEDDINGS] = FALSE;
	}

	if( !all_embeddings_are_different( prod ) )
	{
		prod->lp_graph.properties_array[DIFF_EMBEDDINGS] = 0;
		grammar_preconditions[DIFF_EMBEDDINGS] = FALSE;
	} 

	Graphcopy_edgeline_to_lp_edgeline( prod );

	compact( prod );

	convert_start_and_end_point_of_edge( prod , 32);
	change_start_point_of_all_embeddings( prod );

	if (!rectangular_edgelines( prod ) )
	{
		prod->lp_graph.properties_array[RECTANGULAR_EDGELINES] = 0;
		grammar_preconditions[RECTANGULAR_EDGELINES] = FALSE;
	}
	if (!grid_layout_production( prod , 16 ) )
	{
		prod->lp_graph.properties_array[GRID_LAYOUT] = 0;
		grammar_preconditions[GRID_LAYOUT] = FALSE;
	}

	
	if(!unit_layout_production( prod , 32 ) )
	{
		prod->lp_graph.properties_array[UNIT_LAYOUT] = 0;
		grammar_preconditions[UNIT_LAYOUT] = FALSE;
	} 	

	if(!schnitt_edge_node( prod ) )
	{
		prod->lp_graph.properties_array[INTERSECTING_LINE] = 0;
		grammar_preconditions[INTERSECTING_LINE] = FALSE;
	}

	if(!edgelines_in_production(prod))
	{
		prod->lp_graph.properties_array[LINE_OUT_OF_PROD] = 0;	
		grammar_preconditions[LINE_OUT_OF_PROD] = FALSE;
	}

	if(degenerated_edges(prod))
	{
		prod->lp_graph.properties_array[DEGENERATED_EDGE] = 0;
		grammar_preconditions[DEGENERATED_EDGE] = FALSE;
	}
	if( !non_term_unit(prod, 32) )
	{
		prod->lp_graph.properties_array[NON_TERM_UNIT] = 0;	
		grammar_preconditions[NON_TERM_UNIT] = FALSE;
	}

	if( !lp_test_on_frame(prod) )
	{
		prod->lp_graph.properties_array[BORDER_GAP_FULLFILLED] = 0;
		grammar_preconditions[BORDER_GAP_FULLFILLED] = FALSE;
	}

	if( !lp_test_nodes_non_touching(prod) )
	{
		prod->lp_graph.properties_array[NODE_DISTANCE] = 0;
		grammar_preconditions[NODE_DISTANCE] = FALSE;
	}

	if( !lp_test_non_overlapping_edges(prod) )
	{
		prod->lp_graph.properties_array[EDGE_OVERLAPPING] = 0;
		grammar_preconditions[EDGE_OVERLAPPING] = FALSE;
	}

	get_ord_side_number_of_imbedding( prod );

	/* create_production_isomorphism(prod); */

	prod->gra.gra.nce1.lp_nce1_gragra.first_x = (top_sort_ref) NULL;
	prod->gra.gra.nce1.lp_nce1_gragra.first_y = (top_sort_ref) NULL;

	topological_sorting( prod );

	/****** Now you have to show the user, what attributes his Grammar has ******/
	lp_reset_all_grammar_panels();
}

/*****************************************************************************************
function:	lp_update_derived_edge
Input:	Edge derived_edge, prod_edge, new_edge

	Fuehrt die fuer lgg notwendigen Updates der Kantengeschichten durch

GLOBAL: create_net_edges
Output:	---
*****************************************************************************************/

void	lp_update_derived_edge(Edge derived_edge, Edge prod_edge, Edge new_edge)
{
	if ( (graph_state.lp_graph_state.LGG_Algorithm != GRAPHED_STANDARD) )
	{
		/* Kopiere die Geschichte von derive_edge und haenge den tree record von in_production */
		update_history_of_the_embedding(derived_edge, prod_edge, new_edge);

		/* Ableitungszeiger im derivation_net erzeugen */
		if ( create_net_edges )
		{
			create_history_of_edge(	new_edge->lp_edge.history->pre->pre->element, 
					new_edge->lp_edge.history->pre->element );

		}
	}
}

/*****************************************************************************************
function:	lp_apply_production
Input:	Graph prod, Node node, Group copy_of_right_side;


	Beim Ableiten eines Knoten muss der komplette Ableitungsbaum aktualisiert werden.
	Das passiert hier.
	ACHTUNG: Es gibt einstellbare Algorithmen, die hierfuer nicht geeignet sind. 
		 ( z.B. alle Bottom Up) Wenn so einer eingestellt ist, dann wird default-
		 maessig GRAPHED_STANDARD ausgefuehrt.

GLOBAL: create_net_edges,
	graph_state.LGG_Algorithm

Output:	---
*****************************************************************************************/

void	lp_apply_production(Graph prod, Node node, Group copy_of_right_side)
{
	Graph   	tmp_graph;
	lpr_Node	head;

/*
	make_node_father_to_production(node, prod );
	create_personal_history_of_edge( copy_of_right_side );
	make_local_embedding (ENCE_1, node, prod);  
*/
	if (graph_state.lp_graph_state.LGG_Algorithm == GRAPHED_STANDARD)
	{
		node->graph->lp_graph.hierarchical_graph = FALSE;
	}
	if( node->graph->lp_graph.dependency_visible &&
	  ((graph_state.lp_graph_state.LGG_Algorithm == GRAPHED_STANDARD) || 
	     (graph_state.lp_graph_state.LGG_Algorithm == TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B)) )
	{
		remove_dependency( node->graph->lp_graph.derivation_net );
		node->graph->lp_graph.dependency_visible = FALSE;
	}

	if ( node->graph->lp_graph.hierarchical_graph )
	{
		create_net_edges = TRUE;
		copy_lp_edgelines_to_tree( prod );
		copy_topological_sorting_to_tree( node->lp_node.tree_iso );
		tmp_graph = node->graph;
 
		if (!( node->graph->lp_graph.dependency_visible &&
	  	  ((graph_state.lp_graph_state.LGG_Algorithm == GRAPHED_STANDARD) || 
	    	 (graph_state.lp_graph_state.LGG_Algorithm == TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B)) ) )
		{
			if( LP_WIN.what_to_do_with_derivated_node == SHOW &&
		  	  !((graph_state.lp_graph_state.LGG_Algorithm == GRAPHED_STANDARD) || 
		     	 (graph_state.lp_graph_state.LGG_Algorithm == TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B)) )
			{
				node->graph->lp_graph.dependency_visible = TRUE;
			}
		}

		if( !node->graph->lp_graph.reduced )	/*** Ich kann nicht garantieren, dass nach reduce und delete alle Ableitungsbaeume korrekt sind ***/
		{
			head = lpr_apply_production( prod, node );
			set_array_of_iso_prod_pointers(head->applied_production);
		}

		switch( graph_state.lp_graph_state.LGG_Algorithm )
		{
			case TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:
				top_down_cost_optimization( node->graph->lp_graph.LRS_graph, head );
				break;

			case TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
				compute_attributes( tmp_graph );
				break;

			case TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI:
				create_multi_lgg_layout( tmp_graph );
				break;

			case TOP_DOWN_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP_MULTI_ORIENTED:
				create_oriented_multi_lgg_layout( tmp_graph );
				break;

			case TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
				lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph( node->graph );
				break;

			case TOP_DOWN_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP:
			case BOTTOM_UP_BEND_OPTIMIZATION_WITH_EGDE_OVERLAP:
			case BOTTOM_UP_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING:
			case BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP:
			case BOTTOM_UP_BEND_OPTIMIZATION_WITHOUT_EGDE_OVERLAP_B:


			/***** Bei zukuenftigen Aenderungen von GRAPHED: ******/
			/****** Anstelle von dem hier den neuen Graphed-Algorithmus einsetzen ******/
			case GRAPHED_STANDARD:
			default:
				break;
		}
		tmp_graph->lp_graph.changed = FALSE;
	}
	
}

/*****************************************************************************************
function:	lp_lgg_derivation_tests
Input:	Graph graph

	graph ist der Graph, auf den eine Produktion angewendet wurde. Es wird ueberprueft,
	ob das ueberhaupt gemacht werden darf.

Output:	TRUE, wenn erlaubt.
	FALSE sonst
*****************************************************************************************/

int	lp_lgg_derivation_tests(Graph graph)
{
	int	result;

	if( !lp_test_if_node_can_be_derivated( graph->firstnode ) )
	{
		return( FALSE );
	}


	if( graph->lp_graph.reduced	&&
	    (graph_state.lp_graph_state.LGG_Algorithm != TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING) )
	{
		MsgBox( "This is a reduced graph. The only \npossible algorithm is top down area optimization.", CMD_OK );
		return( FALSE );
	}


	if( !grammar_fits_algorithm_conditions(graph_state.lp_graph_state.LGG_Algorithm) )
	{
		MsgBox( "Can't execute Algorithm, because Grammar has not \n the attributes for this algorithm.", CMD_OK );
		return( FALSE );
	}

	if( !graph->lp_graph.hierarchical_graph &&
	    (graph_state.lp_graph_state.LGG_Algorithm != GRAPHED_STANDARD) )
	{
		MsgBox( "This is no hierarchical graph. The only possible\nalgorithm is No optimization.", CMD_OK );
		return( FALSE );
	}

	if( ( graph->lp_graph.changed ) && (graph_state.lp_graph_state.LGG_Algorithm != GRAPHED_STANDARD) )
	{
		result =	notice_prompt (base_frame, NULL,		/*fisprompt*/
					NOTICE_MESSAGE_STRINGS,	"WARNING!\nGraph has been changed.\nCompute anyway?", NULL,
					NOTICE_BUTTON_YES,	"Yes",
					NOTICE_BUTTON_NO,	"No",
					NULL);

		if( result == NOTICE_YES )
		{
			return( TRUE );
		}
		return( FALSE );
		
	}
	return( TRUE );
}

	
/****************************************************************************************/
/*											*/
/* Funktionen zu nnode.c								*/
/*											*/
/****************************************************************************************/
/*****************************************************************************************
function:	init_lp_node
Input:	Node node

	Initialisiert die Datenstrukturen in node, die unter lp_node zusammengefasst sind

Output:	---
*****************************************************************************************/

void	init_lp_node(Node node)
{
	node->lp_node.tree_iso			= NULL;

	node->lp_node.copy_iso			= NULL;
	node->lp_node.corresponding_lpr_node	= NULL;
	node->lp_node.iso_in_area_opt		= NULL;

	node->lp_node.multi_iso			= NULL;
	node->lp_node.multi_pre			= node;
	node->lp_node.multi_suc			= node;

	node->lp_node.pars_iso			= NULL;
	node->lp_node.has_a_mark		= FALSE;
}

void	delete_lp_node(Node node)
{	
	node->graph->lp_graph.changed = TRUE;
}

/****************************************************************************************/
/*											*/
/* Funktionen zu eedge.c								*/
/*											*/
/****************************************************************************************/
/*****************************************************************************************
function:	init_lp_edge
Input:	Edge edge

	Initialisiert die Datenstrukturen in edge, die unter lp_edge zusammengefasst sind

Output:	---
*****************************************************************************************/

void	init_lp_edge(Edge edge)
{
	edge->lp_edge.lp_line 		= (lp_Edgeline) NULL;
	edge->lp_edge.old_lp_line	= (lp_Edgeline) NULL;
	edge->lp_edge.lp_ord 		= 0;
	edge->lp_edge.side		= 0;
	edge->lp_edge.history		= NULL;
	edge->lp_edge.tree_iso		= NULL;
	edge->lp_edge.iso		= NULL;

	edge->lp_edge.multi_iso 	= NULL;
	edge->lp_edge.multi_pre		= edge;
	edge->lp_edge.multi_suc		= edge;
}

/*****************************************************************************************
function:	delete_lp_edge
Input:	Edge edge

	Loescht Speicher von allem was an edge haengt

Output:	---
*****************************************************************************************/

void	delete_lp_edge(Edge edge)
{
	edge->source->graph->lp_graph.changed = TRUE;

	free_history	( edge->lp_edge.history		);
	free_lp_edgeline( edge->lp_edge.lp_line 	);

	edge->lp_edge.lp_line 		= NULL;
	edge->lp_edge.history 		= NULL;
}

/*****************************************************************************************
function:	copy_lp_edge_without_line
Input:	Edge new_edge, old_edge

	Es wird Kopie von old_edge erzeugt. Erzeuge alle notwendigen Zeiger

Output:	---
*****************************************************************************************/

void	copy_lp_edge_without_line(Edge new_edge, Edge old_edge)
{
	new_edge->lp_edge.lp_line 	= (lp_Edgeline) NULL;
	new_edge->lp_edge.lp_ord	= 0;
	new_edge->lp_edge.side		= 0;
	new_edge->lp_edge.history	= NULL;
	new_edge->lp_edge.iso		= old_edge;
	old_edge->lp_edge.iso		= new_edge;
	new_edge->lp_edge.tree_iso	= old_edge->lp_edge.tree_iso;	
}

/****************************************************************************************/
/*											*/
/* Funktionen zu ggraph.c								*/
/*											*/
/****************************************************************************************/
/*****************************************************************************************
function:	init_lp_graph
Input:	Graph graph

	Initialisiert die Datenstrukturen in graph, die unter lp_graph zusammengefasst sind

Output:	---
*****************************************************************************************/

void	init_lp_graph(Graph graph)
{
	graph->lp_graph.derivation_net 			= NULL;
	graph->lp_graph.current_size			= 32;
	graph->lp_graph.multi_pre			= graph;
	graph->lp_graph.multi_suc			= graph;
	graph->lp_graph.node_nr				= 0;
	graph->lp_graph.save_node_nr			= 0;
	graph->lp_graph.edge_nr				= 0;
	graph->lp_graph.embedding_rules			= NULL;
	graph->lp_graph.table				= NULL;
	graph->lp_graph.LRS_graph			= NULL;
	graph->lp_graph.changed				= FALSE;
	graph->lp_graph.reduced				= FALSE;
	graph->lp_graph.creation_time			= FALSE;
	graph->lp_graph.disposed			= mem_copy_string( "nondisposed" );
	graph->lp_graph.hierarchical_graph		= TRUE;
	graph->lp_graph.dependency_visible		= FALSE;

	graph->gra.gra.nce1.lp_nce1_gragra.first_x	= NULL;
	graph->gra.gra.nce1.lp_nce1_gragra.first_y	= NULL;

}

/*****************************************************************************************
function:	clear_lp_graph
Input:	Graph graph

	Loescht die Datenstrukturen in graph, die unter lp_graph zusammengefasst sind
	ACHTUNG: Stimmt noch nicht ganz. loescht noch nicht alles

Output:	---
*****************************************************************************************/

void	clear_lp_graph(Graph graph)
{
	if( !graph->is_production )		/*** bei Produktion: topological_sorting loeschen ***/
	{
		free_tree		( graph->lp_graph.derivation_net	);
		free_set_of_nodelist	( graph->lp_graph.embedding_rules	);
		free_table		( graph->lp_graph.table 		);
		if( graph->lp_graph.LRS_graph )
		{
			free_plr_system	( graph->lp_graph.LRS_graph		);
		}
/*		graph->lp_graph.disposed = "___________"; schwachsinn */
		free( graph->lp_graph.disposed );		/*** Kontrolle ob graph geloescht */
		graph->lp_graph.disposed = NULL;
	}
	else
	{
		graph_state.lp_graph_state.production_deleted = TRUE;
	}
}

/****************************************************************************************/
/*											*/
/* Funktionen zu dispatch.c								*/
/*											*/
/****************************************************************************************/
/*****************************************************************************************
function:	lp_test_if_node_can_be_derivated
Input:	Node node

	Ueberprueft, ob Knoten
		-ableitungsbaum hat oder
		-einziger im Graphen ist.
	Dann ist Ableitung erlaubt

Output:	True, wenn Ableitung erlaubt
	False sonst
*****************************************************************************************/

int	lp_test_if_node_can_be_derivated(Node node)
{
	if( !test_for_allowed_first_production(node) )
	{
		node->graph->lp_graph.hierarchical_graph = FALSE;
	}
	return( TRUE );
}


/****************************************************************************************/
/*											*/
/* Funktionen zu main.c									*/
/*											*/
/****************************************************************************************/
/*****************************************************************************************
function:	lp_add_items_to_user_menu()
Input:	---

	Fuegt die Aufrufe fuer unsere Algorithmen in das user_menu ein

Output:	---
*****************************************************************************************/

void	lp_add_items_to_layout_menu(void)
{
	add_to_layout_menu("Hierarchical Graph Design",		lp_create_baseframe );
}

/*****************************************************************************************
function:	lp_init_lgg_settings
Input:	---

	In main.c muss eine Menge initialisiert werden. dies geschieht in dieser Funktion.
	Dadurch, dass alles zusammengefasst wird, steht es allerdings dann nicht mehr da,
	wo es sinngemaess hingehoert.

Output:	---
*****************************************************************************************/

void	lp_init_lgg_settings(void)
{
	create_by_derivation			= FALSE;

	LP_WIN.cur_algorithm 			= GRAPHED_STANDARD;
	LP_WIN.algorithm_label			= "Plain Graphed";
	LP_WIN.what_to_do_with_derivated_node	= 0;
	LP_WIN.min_nodes			= FALSE;
	LP_WIN.frame_is_created			= FALSE;
}


/****************************************************************************************/
/*											*/
/* Funktionen zu state.c								*/
/*											*/
/****************************************************************************************/
/*****************************************************************************************
function:	set_current_LGG_algorithm
Input:	int number

	legt LGG_Algorithm in graph_state fest

Output:	---
*****************************************************************************************/

void		set_current_LGG_algorithm(LGG_Algorithms number)
{
	graph_state.lp_graph_state.LGG_Algorithm = number;
}


void		lp_set_production_deleted(int value)
{
	graph_state.lp_graph_state.production_deleted = value;
}


/*****************************************************************************************
function:	init_lp_graph_state
Input:	---

	Fuehrt die Initialisierungen fuer lgg in state.c durch

Output:	---
*****************************************************************************************/

void	init_lp_graph_state(void)
{
	set_current_LGG_algorithm( GRAPHED_STANDARD  );
	lp_set_production_deleted( FALSE );
}

/*****************************************************************************************
function:	grammar_fits_algorithm_conditions
Input:	int alg

	Ueberprueft, ob die Graph Grammatik die Eigenschaften efuellt, die der Algorithmus
	alg benoetigt.

Output:	TRUE, wenn erfuellt
	FALSE sonst
*****************************************************************************************/

int	grammar_fits_algorithm_conditions(int alg)
{
	int	i;

	for( i = 0; i < PRECONDITION_COUNTER; i++ ) 
	{
		if( Algorithm_preconditions[alg][i] && !grammar_preconditions[i] )
		{
			return( FALSE );
		}
	}
	return( TRUE );
}
