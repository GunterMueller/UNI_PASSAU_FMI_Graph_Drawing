#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"
#include "lp_test.h"
#include "lp_edgeline.h"

#include "lpp_1_pass.h"

/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/****** Modul fuer den ersten Berechnungsschritt beim Layout durch Parsing						******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/

/******************************************************************************************************************************
function:	fitting_node_in_prod
Input:	Node node, Graph prod

Output:	Der zu node in prod isomorphe Knoten
******************************************************************************************************************************/

Node	fitting_node_in_prod(Node node, Graph prod)
{
	Node	cur_node;

	for_node_multi_suc( node, cur_node )
	{
		if( cur_node->graph == prod ) 
			return( cur_node );
	}
	end_for_node_multi_suc( node, cur_node );
	return NULL;
}

/******************************************************************************************************************************
function:	number_of_sources_of_node_in_pe
Input:	Graph graph,  Node node, lpp_Parsing_elements pe

	Berechnet, wie viele Kanten in graph von node zu einem Knoten in pe->nodes gehen.

Output:	Berechnete Zahl
******************************************************************************************************************************/

int	number_of_sources_of_node_in_pe(Node node, lpp_Parsing_element pe)
{
	Edge		edge;
	Nodelist	cur_node_list;
	int		result		= 0;
	Node		graph_node	= node->lp_node.pars_iso->nodes->node;

	if( node_is_nonterminal(node) )
	{
		/****** in diesem Fall ist der Node fuer einen nonterminalen Knoten, es gibt genau ein Target		******/
		return( 1 );
	}

	for_edge_targetlist( graph_node, edge)
	{
		for_nodelist( pe->nodes, cur_node_list )
		{
			if( edge->source == cur_node_list->node )
			{
				result += 1;
			}
		}
		end_for_nodelist( pe->nodes, cur_node_list );
	}
	end_for_edge_targetlist( graph_node, edge);

	return( result );
}

/******************************************************************************************************************************
function:	number_of_targets_of_node_in_pe
Input:	Node node, lpp_Parsing_elements pe

	Berechnet, wie viele Kanten im Graph von node zu einem Knoten in pe->nodes gehen.

Output:	Berechnete Zahl
******************************************************************************************************************************/

int	number_of_targets_of_node_in_pe(Node node, lpp_Parsing_element pe)
{
	Edge		edge;
	Nodelist	cur_node_list;
	int		result		= 0;
	Node		graph_node	= node->lp_node.pars_iso->nodes->node;

	if( node_is_nonterminal(node) )
	{
		/****** in diesem Fall ist der Node fuer einen nonterminalen Knoten, es gibt genau ein Target		******/
		return( 1 );
	}

	/****** Jetzt ist node Terminalknoten, man kann entsprechung im Graphen suchen und berechnen			******/
	for_edge_sourcelist( graph_node, edge)
	{
		for_nodelist( pe->nodes, cur_node_list )
		{
			if( edge->target == cur_node_list->node )
			{
				result += 1;
			}
		}
		end_for_nodelist( pe->nodes, cur_node_list );
	}
	end_for_edge_sourcelist( graph_node, edge);

	return( result );
}

/******************************************************************************************************************************
function:	costs_for_connection_relations
Input:	Attributes_ref ref, Attributes_head attr_head

	Berechnet die Anzahl der Knicke der Kanten innerhalb der Knoten der unteren Produktion * "split_nr".

Output:	Berechnete Zahl
******************************************************************************************************************************/

int	costs_for_connection_relations(Attributes_ref ref, Attributes_head attr_head)
{
	Set_of_parsing_elements	cur_set_elem;
	Edge			e;
	Group			cur_group, g;
	Node			LHS_node	= ref->lower_prod->gra.gra.nce1.left_side->node;
	int			result		= 0;
	Set_of_parsing_elements	lower_set	= attr_head->lower_edge->derivation_nodes;

	/****** Pars_iso von Produktion auf set richten, da mir andernfalls nur sehr langsame Algorithmen einfallen	******/
	for_set_of_parsing_elements( lower_set, cur_set_elem )
	{
		fitting_node_in_prod( cur_set_elem->production_iso, ref->lower_prod )->lp_node.pars_iso = cur_set_elem->pe;
	}
	end_for_set_of_parsing_elements( lower_set, cur_set_elem );


	cur_group = ref->lower_prod->gra.gra.nce1.right_side;
	for_group (cur_group, g )
	{
		for_edge_sourcelist( g->node, e)
		{
			if( inside(e->target->x, e->target->y, LHS_node) )
			{
				/****** da nie 2 non-terminale verbunden werden koennen, ist mindestens ein Faktor 1	******/
				result += ( lp_edgeline_length(e->lp_edge.lp_line) - 2 ) 				* 
					  number_of_targets_of_node_in_pe( g->node, e->target->lp_node.pars_iso )	*
					  number_of_sources_of_node_in_pe( e->target, g->node->lp_node.pars_iso );
			}
		}
		end_for_edge_sourcelist( g->node, e);
	}
	end_for_group( cur_group, g )

	return( result );
}


/******************************************************************************************************************************
function:	compute_c_0
Input:	Attributes_ref_list ref, Attributes_head attr_head

	Berechnet die Anzahl der Knicke der Kanten innerhalb der Knoten der unteren Produktion * "split_nr".

Output:	Berechnete Zahl
******************************************************************************************************************************/

int	compute_c_0(Attributes_ref_list ref_list, Attributes_head attr_head)
{
	Set_of_parsing_elements	cur_set_elem;
	Edge			e;
	Group			cur_group, g;
	Node			LHS_node	= ref_list->refs_between_prods->upper_prod->gra.gra.nce1.left_side->node;
	int			result		= 0;
	Set_of_parsing_elements	set		= attr_head->upper_edge->derivation_nodes;
	Graph			production	= ref_list->refs_between_prods->upper_prod;

	/****** Pars_iso von Produktion auf set richten, da mir andernfalls nur sehr langsame Algorithmen einfallen	******/
	for_set_of_parsing_elements( set, cur_set_elem )
	{
		fitting_node_in_prod( cur_set_elem->production_iso, production )->lp_node.pars_iso = cur_set_elem->pe;
	}
	end_for_set_of_parsing_elements( set, cur_set_elem );


	cur_group = production->gra.gra.nce1.right_side;
	for_group (cur_group, g )
	{
		for_edge_sourcelist( g->node, e)
		{
			if( inside(e->target->x, e->target->y, LHS_node) )
			{
				/****** da nie 2 non-terminale verbunden werden koennen, ist mindestens ein Faktor 1	******/
				result += ( lp_edgeline_length(e->lp_edge.lp_line) - 2 ) 				* 
					  number_of_targets_of_node_in_pe( g->node, e->target->lp_node.pars_iso )	*
					  number_of_sources_of_node_in_pe( e->target, g->node->lp_node.pars_iso );
			}
		}
		end_for_edge_sourcelist( g->node, e);
	}
	end_for_group( cur_group, g )

	return( result );
}

/******************************************************************************************************************************
******************************************************************************************************************************/

/******************************************************************************************************************************
function:	get_corresponding_edge_in_prod_for_outgoing
Input:	Set_of_parsing_elements down_set, lpp_Parsing_element pe, Attributes_head head, Graph prod

	Sucht sich in der unten angwandten Produktion das Set_of_pe, das auf pe zeigt und sucht dann in prod diejenige Kante,
	die die beiden Elemente verbindet

Output:	NULL, wenn kein passendes set_.. gefunden wird ( in diesem Fall verarbeitung als Einbettungsregel )
	gefundene Kante sonst
******************************************************************************************************************************/

Edge	get_corresponding_edge_in_prod_for_outgoing(Set_of_parsing_elements down_set, lpp_Parsing_element pe, Attributes_head head, Graph prod)
{
	Set_of_parsing_elements	cur_set;
	Node			source_node;
	Node			target_node;
	Edge			cur_edge;

	for_set_of_parsing_elements( head->upper_edge->derivation_nodes, cur_set )
	{
		if( cur_set->pe == pe )
		{
			/****** Suche Kante zwischen den beiden entsprechenden Knoten in der Produktion			******/
			source_node	= fitting_node_in_prod( down_set->production_iso, prod );
			target_node	= fitting_node_in_prod( cur_set->production_iso, prod );

			for_edge_sourcelist( source_node, cur_edge )
			{
				if( cur_edge->target == target_node )
				{
					return( cur_edge );
				}
			}
			end_for_edge_sourcelist( source_node, cur_edge );
		}
	}
	end_for_set_of_parsing_elements( head->lower_edge->derivation_nodes, cur_set );

	return( NULL );
}

/******************************************************************************************************************************
function:	get_corresponding_edge_in_prod_for_incomming
Input:	Set_of_parsing_elements down_set, lpp_Parsing_element pe, Attributes_head head, Graph prod

	Sucht sich in der unten angwandten Produktion das Set_of_pe, das auf pe zeigt und sucht dann in prod diejenige Kante,
	die die beiden Elemente verbindet

Output:	NULL, wenn kein passendes set_.. gefunden wird ( in diesem Fall verarbeitung als Einbettungsregel )
	gefundene Kante sonst
******************************************************************************************************************************/

Edge	get_corresponding_edge_in_prod_for_incomming(Set_of_parsing_elements down_set, lpp_Parsing_element pe, Attributes_head head, Graph prod)
{
	Set_of_parsing_elements	cur_set;
	Node			source_node;
	Node			target_node;
	Edge			cur_edge;

	for_set_of_parsing_elements( head->upper_edge->derivation_nodes, cur_set )
	{
		if( cur_set->pe == pe )
		{
			/****** Suche Kante zwischen den beiden entsprechenden Knoten in der Produktion			******/
			target_node	= fitting_node_in_prod( down_set->production_iso, prod );
			source_node	= fitting_node_in_prod( cur_set->production_iso, prod );

			for_edge_sourcelist( source_node, cur_edge )
			{
				if( cur_edge->target == target_node )
				{
					return( cur_edge );
				}
			}
			end_for_edge_sourcelist( source_node, cur_edge );
		}
	}
	end_for_set_of_parsing_elements( head->lower_edge->derivation_nodes, cur_set );

	return( NULL );
}

/******************************************************************************************************************************
function:	get_corresponding_out_embedding_rule
Input:	Node source_node, Edge_list edge, Graph prod

	Holt sich diejenige out_embedding_edge, die die benoetigten Eigenschaften erfuellt

Output:	Gefundene Kante
******************************************************************************************************************************/

Edge	get_corresponding_out_embedding_rule(Node source_node, Edge_list edge, Graph prod)
{
	char*	edge_label	= edge->label;
	char*	target_label	= edge->target->label;
	Node	LHS_node	= prod->gra.gra.nce1.left_side->node;
	Node	node_in_prod	= fitting_node_in_prod( source_node, prod );
	Edge	cur_edge;

	for_edge_sourcelist( node_in_prod, cur_edge )
	{
		if( my_strcmp(cur_edge->label.text, edge_label)			&&
		    my_strcmp(cur_edge->target->label.text, target_label)	&&
		    !inside( cur_edge->target->x, cur_edge->target->y, LHS_node)  )
		{
			return( cur_edge );
		}
	}
	end_for_edge_sourcelist( node_in_prod, cur_edge );
	return NULL;
}

/******************************************************************************************************************************
function:	get_corresponding_in_embedding_rule
Input:	Node source_node, Edge_list edge, Graph prod

	Holt sich diejenige in_embedding_edge, die die benoetigten Eigenschaften erfuellt

Output:	Gefundene Kante
******************************************************************************************************************************/

Edge	get_corresponding_in_embedding_rule(Node target_node, Edge_list edge, Graph prod)
{
	char*	edge_label	= edge->label;
	char*	source_label	= edge->source->label;
	Node	LHS_node	= prod->gra.gra.nce1.left_side->node;
	Node	node_in_prod	= fitting_node_in_prod( target_node, prod );
	Edge	cur_edge;

	for_edge_targetlist( node_in_prod, cur_edge )
	{
		if( my_strcmp(cur_edge->label.text, edge_label)			&&
		    my_strcmp(cur_edge->source->label.text, source_label)	&&
		    !inside( cur_edge->source->x, cur_edge->source->y, LHS_node)  )
		{
			return( cur_edge );
		}
	}
	end_for_edge_targetlist( node_in_prod, cur_edge );
	return NULL;
}

/******************************************************************************************************************************
function:	
******************************************************************************************************************************/

Set_of_parsing_elements	get_parsing_element_which_corresponds_to_prod_node(Node node, Attributes_head attr_head)
{
	Set_of_parsing_elements	cur_set;
	Node			cur_node;
	/****** Menge der Set_of_parsing_elements, die die LHS vom unten abgeleiteten Knoten repraesentieren		******/
	Set_of_parsing_elements	lower_prod_nodes = attr_head->lower_edge->derivation_nodes;

	for_set_of_parsing_elements( lower_prod_nodes, cur_set )
	{
		for_node_multi_suc( node, cur_node )
		{
			if( cur_set->production_iso == cur_node )
			{
				return( cur_set );
			}
		}
		end_for_node_multi_suc( node, cur_node );
	}
	end_for_set_of_parsing_elements( lower_prod_nodes, cur_set );
	return NULL;
}

/******************************************************************************************************************************
function:	costs_for_connection_of_embedding_rules
Input:	Attributes_ref ref, Attributes_head attr_head

	Berechnet die Anzahl der Knicke, die Auftreten, wenn die in den unten abgeleiteten Knoten ein- und auslaufenden Kanten
	mit den in- und out_embedding Regeln der auf den Knoten angewandten Produktion verbunden werden.

Output:	Berechnete Zahl
******************************************************************************************************************************/

int	costs_for_connection_of_embedding_rules(Attributes_ref ref, Attributes_head attr_head)
{
	Edge_list		cur_table_edge;
	Nodelist		cur_list;
	Set_of_nodelist		cur_set;
	Edge			outside_edge;
	int			result		= 0;
	Graph			lower_prod	= ref->lower_prod;
	Set_of_parsing_elements	der_set		= attr_head->below_derivated_set;
	lpp_Parsing_element		derivated_pe	= attr_head->below_derivated_set->pe;
	Set_of_nodelist		embedding_list	= lower_prod->lp_graph.embedding_rules;
	Set_of_parsing_elements	target_pe;


	/****** Out_embedding_rules											******/
	lpp_for_edgelist( derivated_pe->source_edges, cur_table_edge )
	{
		/****** Hole die Kante in der Produktion, die dieser entspricht						******/
		outside_edge = get_corresponding_edge_in_prod_for_outgoing( der_set, cur_table_edge->target, attr_head, ref->upper_prod );

		if( !outside_edge )
		{
			outside_edge = get_corresponding_out_embedding_rule( der_set->production_iso, cur_table_edge, ref->upper_prod );
		}

		/****** Vergleich mit der Prod, um festzustellen, ob dafuer eine out_embedding-Regel existiert		******/

		/******	Durchlaufe Liste der verschiedenen Einbettungsregeln						******/
		for_set_of_nodelist( embedding_list, cur_set )
		{
			/****** Wenn diese embedding_edges ein out_embedding repraesentieren, Edgelabel und		******/
			/****** Label des Knoten ausserhalb uebereinstimmen, dann zaehlen				******/
			if( !cur_set->is_in_embedding 						&&
			    my_strcmp(cur_table_edge->target->label, cur_set->nodelabel)	&&
			    my_strcmp(cur_table_edge->label, cur_set->edgelabel)		  )
			{
				/******	Durchlaufe Liste aller Knoten mit der gleichen Einbettungsregel			******/
				for_nodelist( cur_set->list, cur_list )
				{
					target_pe = get_parsing_element_which_corresponds_to_prod_node( cur_list->edge->source, attr_head );

					result	+= Xi(last_dir(cur_list->edge), first_dir(outside_edge)) *
						   number_of_sources_of_node_in_pe(
							cur_table_edge->target->graph_iso, derivated_pe /* , ref->lower_prod  wieso 3 parameter ? */);
				}
				end_for_nodelist( cur_set->list, cur_list );
			}
		}
		end_for_set_of_nodelist( embedding_list, cur_set );
	}
	end_lpp_for_edgelist( derivated_pe->source_edges, cur_table_edge );

	/****** IN_embedding_rules											******/
	lpp_for_edgelist( derivated_pe->target_edges, cur_table_edge )
	{
		/****** Hole die Kante in der Produktion, die dieser entspricht						******/
		outside_edge = get_corresponding_edge_in_prod_for_incomming( der_set, cur_table_edge->source, attr_head, ref->upper_prod );

		if( !outside_edge )
		{
			outside_edge = get_corresponding_in_embedding_rule( der_set->production_iso, cur_table_edge, ref->upper_prod );
		}
		/****** Vergleich mit der Prod, um festzustellen, ob dafuer eine out_embedding-Regel existiert		******/

		/******	Durchlaufe Liste der verschiedenen Einbettungsregeln						******/
		for_set_of_nodelist( embedding_list, cur_set )
		{
			/****** Wenn diese embedding_edges ein in_embedding repraesentieren, Edgelabel und		******/
			/****** Label des Knoten ausserhalb uebereinstimmen, dann zaehlen				******/
			if( cur_set->is_in_embedding 						&&
			    my_strcmp(cur_table_edge->source->label, cur_set->nodelabel)	&&
			    my_strcmp(cur_table_edge->label, cur_set->edgelabel)		  )
			{
				/******	Durchlaufe Liste aller Knoten mit der gleichen Einbettungsregel			******/
				for_nodelist( cur_set->list, cur_list )
				{
					target_pe = get_parsing_element_which_corresponds_to_prod_node( cur_list->edge->target, attr_head );

					result	+= Xi(first_dir(cur_list->edge), last_dir(outside_edge)) *
						   number_of_targets_of_node_in_pe(
							cur_table_edge->source->graph_iso, target_pe->pe /* , ref->lower_prod  wieso 3 parameter? */);
				}
				end_for_nodelist( cur_set->list, cur_list );
			}
		}
		end_for_set_of_nodelist( embedding_list, cur_set );
	}
	end_lpp_for_edgelist( derivated_pe->target_edges, cur_table_edge );

	return( result );
}

/*****************************************************************************************************************************/
/*****************************************************************************************************************************/

/******************************************************************************************************************************
function:	lpp_1_pass_rekursiv
Input:	lpp_Parsing_element head

	Berechnet, wie in Hickl, Hierarchical Graph Design Seite 4 ff. angegeben, die Attribute
		c
	in einem Bottom_up traversal.
******************************************************************************************************************************/

void	lpp_1_pass_rekursiv(lpp_Parsing_element head)
{
	Derivation		cur_derivation;
	Set_of_parsing_elements	cur_set;
	Attributes_head		cur_attr_head;
	Attributes_ref_list	cur_attr_ref_list;
	Attributes_ref		cur_attr_ref;
	int			sum;

	for_derivation( head->derivations, cur_derivation )
	{
		for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set )
		{
			lpp_1_pass_rekursiv( cur_set->pe );
		}
		end_for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set );
	}
	end_for_derivation( head->derivations, cur_derivation );

	if( head->derivations	&&
	    head->derivations->attributes_table_down	&&
	    head->derivations->attributes_table_down->upper_productions->refs_between_prods->little_c == UNDEFINED )
	{   
	
		for_derivation( head->derivations, cur_derivation )
		{
			for_attributes_head_from_top( cur_derivation->attributes_table_down, cur_attr_head )
			{
				for_attributes_ref_list( cur_attr_head->upper_productions, cur_attr_ref_list )
				{
					for_attributes_ref( cur_attr_ref_list->refs_between_prods, cur_attr_ref )
					{
						sum = 0;

						/****** in- and out_embeddings verbinden bei Ableitung unten			******/
						sum += costs_for_connection_of_embedding_rules( cur_attr_ref, cur_attr_head );

						/****** Kanten innerhalb Produktion unten * Anzahl				******/
						sum += costs_for_connection_relations( cur_attr_ref, cur_attr_head );	/* 11.1.     */

						/****** Zuweisung der Kosten							******/
						cur_attr_ref->little_c = sum;
					}
					end_for_attributes_ref( cur_attr_ref_list->refs_between_prods, cur_attr_ref );
				}
				end_for_attributes_ref_list( cur_attr_head->upper_productions, cur_attr_ref_list );
			}
			end_for_attributes_head_from_top( cur_derivation->attributes_table_down, cur_attr_head );
		}
		end_for_derivation( head->derivations, cur_derivation );
	}
}

/******************************************************************************************************************************
function:	lpp_1_pass
Input:	lpp_Parsing_element head

	Berechnet, wie in Hickl, Hierarchical Graph Design Seite 4 ff. angegeben, das Attribute
		c_0
	und ruft die rekursive Funktion zur Berechnung von c auf.
******************************************************************************************************************************/

void	lpp_1_pass(lpp_Parsing_element head)
{
	Derivation		cur_derivation;
	Attributes_head		cur_attr_head;
	Attributes_ref_list	cur_attr_ref_list;

	for_derivation( head->derivations, cur_derivation )
	{
		for_attributes_head_from_top( cur_derivation->attributes_table_down, cur_attr_head )
		{
			for_attributes_ref_list( cur_attr_head->upper_productions, cur_attr_ref_list )
			{
				cur_attr_ref_list->c_0 = compute_c_0( cur_attr_ref_list, cur_attr_head );
			}
			end_for_attributes_ref_list( cur_attr_head->upper_productions, cur_attr_ref_list );
		}
		end_for_attributes_head_from_top( cur_derivation->attributes_table_down, cur_attr_head );
	}
	end_for_derivation( head->derivations, cur_derivation );

	/****** Wenn hier keine Produktion eingetragen wurde, dann besteht ganze Ableitung nur aus einmal Start		******/
	if( head->derivations->attributes_table_down->upper_productions->refs_between_prods->lower_prod )
	lpp_1_pass_rekursiv( head );
}

