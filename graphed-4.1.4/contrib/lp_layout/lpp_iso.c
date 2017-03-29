#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lp_test.h"
#include "lpp_functions.h"
#include "lpp_iso.h"

/*********************************************************************************
			STRUKTUR DES ISOMORPHIETESTS


						test_iso 
						    |
						    |
						    |
						    |
						    |
						    |
	 				compare_graph_and_group
						    |
						    |
		  -------------------------------------------------------------------------------------------------
		  |						|			       |		  |
		  |						|			       |		  |
		  |						|			       |		  |
compare_edge_number_of_graph_and_group		all_embedding_edges_are_correct		test_embeddings		compare
								|						  |
								|						  |
								|						  |
								|						  |
								|						  |
								|						  |
					    (found_corresponding_out_embedding_rule,			  (found_source_edge,
					     found_corresponding_in_embedding_rule)			   found_target_edge)




*********************************************************************************/
/********************************************************************************/
/*										*/
/*	Funktionen fuer den iso_test zwischen Produktionen und Parsebaum	*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function:	parsing_element_is_terminal
Input:	lpp_Parsing_element	pe

Output:	TRUE iff. first letter of node's label is lower case (sign for a 
		  terminal )
	FALSE otherwise
*********************************************************************************/

int		parsing_element_is_terminal(lpp_Parsing_element pe)
{
	if( (pe->label != NULL ) && isupper( pe->label[0] ) )
	{
		return FALSE ;
	} 
	else 
	{
		return TRUE ;
	}
}

/*********************************************************************************
function	found_corresponding_out_embedding_rule
Input:	Edge_list edge, Graph production

Output:	TRUE, iff. an edge in production exists, which is an out-embedding rule
		   and has same labels as edge (concerning the edgelabel, 
		   the source- and targetlabel )
	FALSE otherwise
*********************************************************************************/

int	found_corresponding_out_embedding_rule(Edge_list edge, Graph production)
{
	Node		cur_node;
	Edge		cur_edge;
	Node		LHS_node = production->gra.gra.nce1.left_side->node;

	/****** Durchlaufe alle Knoten der Produktion									******/
	for_nodes( production, cur_node )
	{
		/****** Durchlaufe die Kantenliste des Knoten								******/
		for_edge_sourcelist( cur_node, cur_edge )
		{
			/****** Ueberpruefe alle Eigenschaften, die fuer TRUE-Meldung erforderlich sind			******/
			/****** (Target ausserhalb, alle Label richtig							******/
			if( !inside(cur_edge->target->x, cur_edge->target->y, LHS_node) 	&&
			    my_strcmp(cur_edge->label.text, edge->label)			&&
			    my_strcmp(cur_edge->source->label.text, edge->source->label)	&&
			    my_strcmp(cur_edge->target->label.text, edge->target->label) )
				return( TRUE );
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( production, cur_node );

	return( FALSE );
}

/*********************************************************************************
function	found_corresponding_in_embedding_rule
Input:	Edge_list edge, Graph production

Output:	TRUE, iff. an edge in production exists, which is an in-embedding rule
		   and has same labels as edge (concerning the edgelabel, 
		   the source- and targetlabel )
	FALSE otherwise
*********************************************************************************/

int	found_corresponding_in_embedding_rule(Edge_list edge, Graph production)
{
	Node		cur_node;
	Edge		cur_edge;
	Node		LHS_node = production->gra.gra.nce1.left_side->node;

	/****** Durchlaufe alle Knoten der Produktion									******/
	for_nodes( production, cur_node )
	{
		/****** Durchlaufe die Kantenliste des Knoten								******/
		for_edge_targetlist( cur_node, cur_edge )
		{
			/****** Ueberpruefe alle Eigenschaften, die fuer TRUE-Meldung erforderlich sind			******/
			/****** (Target ausserhalb, alle Label richtig							******/
			if( !inside(cur_edge->source->x, cur_edge->source->y, LHS_node) 	&&
			    my_strcmp(cur_edge->label.text, edge->label)			&&
			    my_strcmp(cur_edge->source->label.text, edge->source->label)	&&
			    my_strcmp(cur_edge->target->label.text, edge->target->label) )
				return( TRUE );
		}
		end_for_edge_targetlist( cur_node, cur_edge );
	}
	end_for_nodes( production, cur_node );

	return( FALSE );
}

/*********************************************************************************
function	all_embedding_edges_are_correct
Input:	Graph	production,
	Set_of_parsing_elements	group

	Ueberprueft fuer alle Kanten, die aus der group hinauslaufen, ob eine 
	passende Einbetungsregel existiert

Output:	FALSE, wenn eine Kante von der group nach Aussen ohne passende Einbettungs-
		kante existiert
	TRUE sonst
*********************************************************************************/

int	all_embedding_edges_are_correct(Graph production, Set_of_parsing_elements group)
{
	Set_of_parsing_elements	cur_set;
	Edge_list		cur_edge;

	/******	Durchlaufe die group											******/
	for_set_of_parsing_elements( group, cur_set )
	{
		/****** Durchlaufe die Source-Kantenliste des aktuellen Elements der group				******/
		lpp_for_edgelist( cur_set->pe->source_edges, cur_edge )
		{
			/****** Liegt das Target der Kante ausserhalb der group und existiert trotzdem keine passende	******/
			/****** Einbettungsregel, dann return FALSE							******/
			if (parsing_element_is_terminal( cur_edge->target ) )
			{
				if( disjunkt(cur_edge->target, group) && 
			  	  !found_corresponding_out_embedding_rule(cur_edge, production) )
				{
					return( FALSE );
				}
			}
		}
		end_lpp_for_edgelist( cur_set->pe->source_edges, cur_edge );

		/****** Durchlaufe die Target-Kantenliste des aktuellen Elements der group				******/
		lpp_for_edgelist( cur_set->pe->target_edges, cur_edge )
		{
			/****** Liegt das Source der Kante ausserhalb der group und existiert trotzdem keine passende	******/
			/****** Einbettungsregel, dann return FALSE							******/
			if (parsing_element_is_terminal(cur_edge->source) )
			{
				if( disjunkt(cur_edge->source, group) && 
				    !found_corresponding_in_embedding_rule(cur_edge, production) )
				{
					return( FALSE );
				}
			}
		}
		end_lpp_for_edgelist( cur_set->pe->target_edges, cur_edge );
	}
	end_for_set_of_parsing_elements( group, cur_set );

	return( TRUE );
}

/*********************************************************************************
function	test_embeddings
Input:	Graph			production,
	Set_of_parsing_elements	group

	Testet fuer den Fall, dass mehrere Knoten der Produktion die gleiche 
	Einbettungsregel existiert, nach ob dann auch bei den Knoten der group
	alle Einbettungsregeln beruecksichtigt wurden

Output:	TRUE, wenn oben angesprochene Eigenschaften erfuellt sind
	FALSE sonst
*********************************************************************************/

int	test_embeddings(Graph production, Set_of_parsing_elements group)
{
	Set_of_nodelist	cur_set;
	Nodelist	cur_list, to_compare;
	lpp_Parsing_element	pars_elem;
	Edge_list	compare_edge, cur_pars_edge;
	int		result = FALSE;
	Set_of_nodelist	set_of_list;

	/****** Listen aller Knoten mit gleichen aus production holen	 						******/
	set_of_list = production->lp_graph.embedding_rules;

	/****** 					Eigentlicher Test 						******/

	/******	Durchlaufe Liste der verschiedenen Einbettungsregeln							******/
	for_set_of_nodelist( set_of_list, cur_set )
	{
		/******	Durchlaufe Liste aller Knoten mit der gleichen Einbettungsregel					******/
		for_nodelist( cur_set->list, cur_list )
		{
			/****** pars_elem ist das Element, das isomorph zu dem zum Listenelement gehoerenden Knoten	******/
			/****** gesetzt wurde										******/
			pars_elem = cur_list->node->lp_node.pars_iso;

			/****** Wenn das Listenelement eine in_embedding repraesentiert					******/
			if( cur_set->is_in_embedding )
			{
				/****** 			hier die eigentliche Routine				******/
				/****** Durchlaufe die Liste aller Kanten, die pars_elem als target haben und...	******/
				lpp_for_edgelist( pars_elem->target_edges, cur_pars_edge )
				{
					/****** wenn eine Kante von ausserhalb der Gruppe nach innen existiert, dann ...******/
					if( disjunkt( cur_pars_edge->source, group )			&&
					    my_strcmp( cur_pars_edge->label, cur_set->edgelabel )	&& 
					    my_strcmp( cur_pars_edge->source->label, cur_set->nodelabel )
					  )
					{
						/****** suche bei allen anderen group-Elementen, die zu			******/
						/****** Produktionsknoten mitgleicher Einbettungsregel gehoeren, ob 	******/
						/****** auch dort diese Kante existiert					******/
						for_nodelist( cur_set->list, to_compare )
						{
							result = FALSE;
							lpp_for_edgelist( to_compare->node->lp_node.pars_iso->target_edges, compare_edge )
							{
								if( compare_edge->source == cur_pars_edge->source )
								{
									result = TRUE;
								}
							}
							end_lpp_for_edgelist( to_compare->node->pars_iso->target_edges, compare_edge );
							if( result == FALSE ) 
							{
								return( FALSE );
							}
						}
						end_for_nodelist( cur_set->list, to_compare );
					}
				}
				end_lpp_for_edgelist( pars_elem->target_edges, cur_pars_edge );
			}

			/****** wenn das Listenelement ein out_embedding repraesentiert, restliche Kommentare 		******/
			/****** siehe in_embedding									******/
			else
			{
				lpp_for_edgelist( pars_elem->source_edges, cur_pars_edge )
				{
					if( disjunkt( cur_pars_edge->target, group )			&&
					    my_strcmp( cur_pars_edge->label, cur_set->edgelabel )	&& 
					    my_strcmp( cur_pars_edge->target->label, cur_set->nodelabel )
					  )
					{
						for_nodelist( cur_set->list, to_compare )
						{
							result = FALSE;
							lpp_for_edgelist( to_compare->node->lp_node.pars_iso->source_edges, compare_edge )
							{
								if( compare_edge->target == cur_pars_edge->target )
								{
									result = TRUE;
								}
							}
							end_lpp_for_edgelist( to_compare->node->pars_iso->source_edges, compare_edge );
							if( result == FALSE )
							{
								return( FALSE );
							}
						}
						end_for_nodelist( cur_set->list, to_compare );
					}
				}
				end_lpp_for_edgelist( pars_elem->source_edges, cur_pars_edge );
			}
		}
		end_for_nodelist( cur_set->list, cur_list );
	}
	end_for_set_of_nodelist( set_of_list, cur_set );

	return( TRUE );
}

/*********************************************************************************
function	found_source_edge
Input:	Edge edge, lpp_Parsing_element pe

	Die edge ist die Kante einer Produktion. Es wird nach einer Kante gesucht,
	die von pe ausgeht und als target den entsprechenden Knoten des 
	target-Knoten von edge hat.

Output: TRUE iff. entsprechende Kante existiert
	FALSE otherwise
*********************************************************************************/

int	found_source_edge(Edge edge, lpp_Parsing_element pe)
{
	Edge_list	pars_edge;

	lpp_for_edgelist( pe->source_edges, pars_edge )
	{
		if( (edge->target->lp_node.pars_iso == pars_edge->target) && my_strcmp(pars_edge->label, edge->label.text) )
		{
			return( TRUE );
		}
	}
	end_lpp_for_edgelist( pe->source_edges, pars_edge );

	return( FALSE );
}

/*********************************************************************************
function	found_target_edge
Input:	Edge edge, lpp_Parsing_element pe

	Die edge ist die Kante einer Produktion. Es wird nach einer Kante gesucht,
	die nach pe geht und als target den entsprechenden Knoten des 
	source-Knoten von edge hat.

Output: TRUE iff. entsprechende Kante existiert
	FALSE otherwise
*********************************************************************************/

int	found_target_edge(Edge edge, lpp_Parsing_element pe)
{
	Edge_list	pars_edge;

	lpp_for_edgelist( pe->target_edges, pars_edge )
	{
		if( (edge->source->lp_node.pars_iso == pars_edge->source) && my_strcmp(pars_edge->label, edge->label.text) )
		{
			return( TRUE );
		}
	}
	end_lpp_for_edgelist( pe->target_edges, pars_edge );

	return( FALSE );
}

/*********************************************************************************
function:	compare
Input:	Graph	graph

	Ueberprueft bei allen Kanten innerhalb der Produktion, ob in der Gruppe 
	eine entsprechende Kante existiert

Output:	FALSE falls eine Kante im Graphed ohne entsprechung in der group gefunden wird
*********************************************************************************/

int	compare(Graph graph)
{
	Node	cur_node;
	Edge	cur_edge;
	Node	LHS_node	= graph->gra.gra.nce1.left_side->node;

	/****** Durchlaufe alle Knoten der Produktion									******/
	for_nodes( graph, cur_node )
	{
		/****** Stelle fest, ob der Knoten innerhalb der LHS liegt und ob er der LHS_node ist			******/
		if( inside( cur_node->x, cur_node->y, LHS_node) && (cur_node != LHS_node) )
		{
			/****** Durchlaufe alle Source-Kanten des Knoten						******/
			for_edge_sourcelist( cur_node, cur_edge )
			{
				/****** Befindet sich der Target-Knoten in der LHS ?					******/
				if( inside(cur_edge->target->x, cur_edge->target->y, LHS_node) )
				{
					/****** Besitzt der Knoten in der Gruppe der lpp_Parsing_elemente, der zum		******/
					/****** Graph-Knoten als pars_iso gesetzt wurde, keine entsprechende Kante, 	******/
					/****** dann return FALSE							******/
					if( !found_source_edge(cur_edge, cur_node->lp_node.pars_iso) )
					{
						return( FALSE );
					}
				}
			}
			end_for_edge_sourcelist( cur_node, cur_edge );

			/****** Durchlaufe alle Target-Kanten des Knoten						******/
			for_edge_targetlist( cur_node, cur_edge );
			{
				/****** Befindet sich der Source-Knoten in der LHS ?					******/
				if( inside(cur_edge->source->x, cur_edge->source->y, LHS_node) )
				{
					/****** Besitzt der Knoten in der Gruppe der lpp_Parsing_elemente, der zum Graph-Knoten	******/
					/****** als pars_iso gesetzt wurde, keine entsprechende Kante, dann return FALSE	******/
					if( !found_target_edge(cur_edge, cur_node->lp_node.pars_iso) )
					{
						return( FALSE );
					}
				}
			}
			end_for_edge_targetlist( cur_node, cur_edge );
		}
	}
	end_for_nodes( graph, cur_node );

	return( TRUE );
}

/*********************************************************************************
function	compare_edge_number_of_graph_and_group
Input:	Set_of_parsing_elements group, Graph graph

	This funcion tests the number of edges inside of the group and the
	graph. 
Output: TRUE iff. graph and group have same nuber of edges
	FALSE otherwise
*********************************************************************************/

int	compare_edge_number_of_graph_and_group(Set_of_parsing_elements group, Graph graph)
{
	Set_of_parsing_elements	cur_set;
	Edge_list		cur_net_edge;
	int			group_edge_nr		= 0;
	int			graph_edge_nr		= 0;

	/****** Zaehle alle Kanten in der Produktion									******/
	graph_edge_nr = graph->lp_graph.edge_nr;

	/****** Zaehle alle Kanten in der group										******/
	for_set_of_parsing_elements( group, cur_set )
	{
		lpp_for_edgelist( cur_set->pe->source_edges, cur_net_edge )
		{
			if( is_in_set(cur_net_edge->target, group) )
				group_edge_nr ++;
		}
		end_lpp_for_edgelist( cur_set->pe->source_edges, cur_net_edge );
	}
	end_for_set_of_parsing_elements( group, cur_set );

	if( group_edge_nr != graph_edge_nr )
		return( FALSE );

	return( TRUE );
}

/*********************************************************************************
function:	compare_graph_and_group
Input:	Set_of_parsing_elements group,
	Graph graph

	Ruft alle Funktionen auf, die den eigentlichen Isomorphie-Test durchfuehren.
	Fuehrt rekursiv die Permutationen zwischen Produktion und selektierter
	Derivation_table - Group durch.
	Vorgehensweise: 
	Versuche, dem ersten Element der group nacheinander alle Knoten der Produktion
	zuzuweisen. Erfuellen der Produktionsknoten und der groupknoten bestimmte
	Anforderungen, dann Iso-Zeiger zuweisen und rekursiver Aufruf mit gleichem
	Graphen und naechstem Group-element. 
	Rekursionsabbruch: group->next == NULL (Alle Group-elemente sind zugewiesen,
		der eigentliche Isomorphietest kann stattfinden )
	 
Output:	TRUE, wenn Graph und Group Isomorph sind 
	False sonst
*********************************************************************************/
int	compare_graph_and_group(Set_of_parsing_elements group, Graph graph, Set_of_parsing_elements first_group)
{
	int	result;
	Node	graph_node;
	Node	LHS_node	= graph->gra.gra.nce1.left_side->node;

	/*	permutations	*/
	for_nodes( graph, graph_node )
	{
		/****** Ueberpruefe, ob Knoten ueberhaupt zugewiesen werden darf						******/
		if( (graph_node->lp_node.pars_iso == NULL) && inside(graph_node->x, graph_node->y, LHS_node) && (graph_node != LHS_node) )
		{
			/****** Vergleiche den Knotenlabel mit dem Label des lpp_Parsing_element					******/
			if( my_strcmp(group->pe->label, graph_node->label.text) )
			{
				graph_node->lp_node.pars_iso = group->pe;
				group->production_iso	= graph_node;

				if( group->next == NULL )
				{
					/****** Vergleich der Kanten innerhalb der Produktion					******/
					result 	= compare( graph );
					if( result == FALSE ) goto exit;

					/****** Test, ob fuer alle Kanten nach ausserhalb der group passende 			******/
					/****** Einbettungsregeln existieren							******/
					result	= all_embedding_edges_are_correct( graph, first_group ) ;
					if( result == FALSE ) goto exit;

					/****** Testet, ob Knoten mit gleicher Einbettungsregel passend verwendet werden	******/
					result	= test_embeddings( graph, first_group );
					if( result == FALSE ) goto exit;

					/****** Alle Tests waren erfolgreich, TRUE kann zurueckgeschickt werden			******/
					return( TRUE );

exit:					graph_node->lp_node.pars_iso = NULL;
				}

				/****** rekursiver Aufruf									******/
				if( group->next != NULL )
				{
					result = compare_graph_and_group( group->next, graph, first_group );
					if( result == TRUE ) return( TRUE );
					graph_node->lp_node.pars_iso = NULL;
				}
			}
		}
	}
	end_for_nodes( graph, graph_node );

	return( FALSE );
}
/*********************************************************************************
function	test_iso
Input:	Set_of_parsing_elements group

Output:	NULL					iff. no isomorph production exists
	lpp_Parsing_element which represents elements of production		 otherwise
*********************************************************************************/

Set_of_parsing_elements		test_iso(Set_of_parsing_elements group, int hierarchy_level)
{
	int			buffer;
	Graph			graph, cur_graph;
	lpp_Parsing_element		new;
	Set_of_parsing_elements	result = NULL;
	Node			node;
	Set_of_parsing_elements	cur_set;
	int			group_nr = 0;

	/****** Berechne die Anzahl der Knoten in der group, damit diese mit der Knotenanzahl der Produktion verglichen 	******/
	/****** werden kann													******/
	for_set_of_parsing_elements( group, cur_set )
	{
		group_nr++;
	}
	end_for_set_of_parsing_elements( group, cur_set );

	/****** Durchlaufe alle Produktionen im Speicher 									******/
	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) 
	{
		for_all_graphs (buffer, graph)
		{
         		if ( graph->is_production )
			{
			if( graph->lp_graph.node_nr == group_nr )
			{
				/****** eigentlicher Vergleich 	(Vergleich der Kantennummer, Vergleich Rest			******/
				if( compare_edge_number_of_graph_and_group( group, graph ) && compare_graph_and_group(group, graph, group) )
				{
					/****** Erzeuge ein parsing_element, bei dem bei derivations eine Kopie 		******/
					/****** aller Elemente von group angehaengt ist						******/
					new = create_parsing_element_from_group( group, graph->gra.gra.nce1.left_side->node->label.text );

					/****** Erzeuge die restlichen Zeiger 							******/
					new->hierarchy_level 	= hierarchy_level;
					new->derivations->used_prod 	= graph;

					/****** Erzeuge ein Set_of_parsing_elements mit Zeiger auf das neue lpp_Parsing_element 	******/
					/****** damit auch mehr als ein lpp_Parsing_element zurueckgeschickt werden kann	    	******/
					/****** (Eine Gruppe kann zu mehreren, nicht isomorphen Produktionen isomorph sein) 	******/
					cur_set 		= new_set_of_parsing_elements( new );

					/****** Result ist das Ergebnis, das zurueckgeschickt wird			    	******/
					result 			= union_to_tree( result, cur_set );
				}

				/****** ruecksetzen der Pars_isos, die in compare_graph_and_group gesetzt wurden	 	******/
				for_nodes( graph, node )
				{
					node->lp_node.pars_iso = NULL;
				}
				end_for_nodes( graph, node );

				/****** wenn node_nr = 0 ist findet kein Vergleich statt, also setze Knotennummer aller 	******/
				/****** isomorphen Produktionen auf 0, dann findet kein Vergleich mehr statt			******/
				for_graph_multi_suc( graph, cur_graph )
				{
					cur_graph->lp_graph.node_nr = 0;
				}
				end_for_graph_multi_suc( graph, cur_graph );
			}
			}
		} 
		end_for_all_graphs (buffer, graph);
	}

	/****** Setze die Knotennummer aller Produktionen wieder auf den richtigen Wert, sonst findet bei naechster 		******/
	/****** group kein Vergleich mehr statt											******/
	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) 
	{
		for_all_graphs (buffer, graph)
		{
			if( graph->is_production )
				graph->lp_graph.node_nr = graph->lp_graph.save_node_nr;
		} 
		end_for_all_graphs (buffer, graph);
	}
	return( result );
}
