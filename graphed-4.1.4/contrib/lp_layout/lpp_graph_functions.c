#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lp_general_functions.h"

#include "lp_test.h"
#include "lpp_iso.h"
#include "lpp_functions.h"
#include "lpp_clear_table.h"

#include "lpp_graph_functions.h"

/********************************************************************************/
/*										*/
/*	Funktionen von Graph nach Elementen fuer parsing			*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function:	create_parsing_elements_of_all_nodes
Input:	Graph graph

	Create parsing_elements of all nodes in graph 

Output:	Set_of_parsing_elements with all created elements
*********************************************************************************/

Set_of_parsing_elements	create_parsing_elements_of_all_nodes(Graph graph)
{
	Node			cur_node;
	Edge			cur_edge;
	lpp_Parsing_element	new_pe;
	Nodelist		new_nl;
	Set_of_parsing_elements	result 		= NULL;
	Set_of_parsing_elements	new_set;

	/****** Erzeuge alle parsing_elements										******/
	for_nodes( graph, cur_node )
	{
		new_nl 			= new_nodelist( cur_node, NULL );

		new_pe 			= new_parsing_element();
		new_pe->nodes 		= new_nl;
		new_pe->label 		= cur_node->label.text;
		new_pe->graph_iso	= cur_node;

		cur_node->lp_node.pars_iso	= new_pe;

		new_set		= new_set_of_parsing_elements( new_pe );
		
		result = add_to_set_of_parsing_elements( result, new_set );
	}
	end_for_nodes( graph, cur_node );

	/****** Erzeuge alle Kanten zwischen den lpp_Parsing_elements							******/
	for_nodes( graph, cur_node )
	{
		for_edge_sourcelist( cur_node, cur_edge )
		{
			add_to_source_and_target_edges( cur_node->lp_node.pars_iso, cur_edge->target->lp_node.pars_iso, cur_edge->label.text );
		}
		end_for_edge_sourcelist( cur_node, cur_edge );
	}
	end_for_nodes( graph, cur_node );

	/****** setze alle Zeiger 'pars_iso' zurueck auf NULL								******/
/*	for_nodes( graph, cur_node )
	{
		cur_node->pars_iso = NULL;
	}
	end_for_nodes( graph, cur_node );	*/

	return( result );
}

/*********************************************************************************
function	all_nodes_are_terminals
Input:	Graph	graph

Output:	TRUE: iff. all nodes of graph are terminals
	FALSE: otherwise
*********************************************************************************/

int	all_nodes_are_terminals(Graph graph)
{
	Node	node;

	for_nodes( graph, node )
	{
		if( node_is_nonterminal(node) )
			return( FALSE );
	}
	end_for_nodes( graph, node );

	return( TRUE );
}

/*********************************************************************************
function	add_to_embedding
Input:	Set_of_nodelist	set_of_list, Node node

	set_of_list ist eine Liste aller schon betrachteten Einbettungsregeln einer 
	Produktion. node repraesentiert eine zusaetliche Einbettungsregel, die durch 
	diese Funktion in set_of_list eingefuegt wird (node ist dabei der Knoten
	ausserhalb  der LHS). 
	Wenn in set_of_list schon eine Einbettungsregel mit gleichen Merkmalen
	existiert, dann wird der Knoten in die gleiche nodelist aufgenommen, 
	andernfalls wird eine neue nodelist erzeugt und an set_of_list angehaengt.

Output:	Pointer auf erstes Element von set_of_list
*********************************************************************************/

Set_of_nodelist	add_to_embedding(Set_of_nodelist set_of_list, Node node)
{
	Edge		cur_edge;
	Set_of_nodelist	cur_set;
	Set_of_nodelist	new_set;
	Nodelist	new_node;

	/****** Der Knoten kann nur eine Kante besitzen									******/
	/****** also: in diesem Fall handelt es sich um eine in_embedding-regel						******/
	for_edge_sourcelist( node, cur_edge )
	{
		new_node = new_nodelist( cur_edge->target, cur_edge );

		/****** In dieser Schleife suche nach einer nodelist, die Knoten mit gleichen Merkmalen besitzt		******/
		for_set_of_nodelist( set_of_list, cur_set )
		{
			if( cur_set->is_in_embedding 					&& 
			    my_strcmp(cur_set->edgelabel, cur_edge->label.text) 	&&
			    my_strcmp(cur_set->nodelabel, node->label.text) 
			  )
			{
				/****** Haenge an nodelist mit passenden Regeln an					******/
				cur_set->list = add_to_nodelist( cur_set->list, new_node );
				/****** Setze new_node auf NULL, da sonst unten eine neue nodelist erzeugt wird		******/
				new_node = NULL;
			}
		}
		end_for_set_of_nodelist( set_of_list, cur_set );
			
		/****** Es wurde keine passende nodelist gefunden, also erzeuge neue und haenge hinten an		******/
		if( new_node )
		{
			new_set 		= new_set_of_nodelist( new_node );
			new_set->edgelabel	= cur_edge->label.text;
			new_set->nodelabel	= node->label.text;
			set_of_list = add_to_set_of_nodelist( set_of_list, new_set );
		}
	}
	end_for_edge_sourcelist( node, cur_edge );

	/****** ab hier: Es handelt sich um eine out_embedding-regel, alle anderen Kommentare siehe oben		******/
	for_edge_targetlist( node, cur_edge )
	{
		new_node = new_nodelist( cur_edge->source, cur_edge );

		for_set_of_nodelist( set_of_list, cur_set )
		{
			if( !cur_set->is_in_embedding 					&& 
			    my_strcmp(cur_set->edgelabel, cur_edge->label.text) 	&&
			    my_strcmp(cur_set->nodelabel, node->label.text) 
			  )
			{
				cur_set->list = add_to_nodelist( cur_set->list, new_node );
				new_node = NULL;
			}
		}
		end_for_set_of_nodelist( set_of_list, cur_set );
			
		if( new_node )
		{
			new_set 			= new_set_of_nodelist( new_node );
			new_set->is_in_embedding	= FALSE;
			new_set->edgelabel		= cur_edge->label.text;
			new_set->nodelabel		= node->label.text;
			set_of_list = add_to_set_of_nodelist( set_of_list, new_set );
		}
	}
	end_for_edge_targetlist( node, cur_edge );

	return( set_of_list );
}

/*********************************************************************************
function	compute_node_nr_of_productions

	Berechne fuer alle Produktionen in Buffer die Anzahl der Knoten in der
	LHS-side und erzeuge eine Liste aller embedding_rules
	und berechne Anzahl der Kanten innerhalb LHS

Output:	List of numbers of nodes
*********************************************************************************/

Int_list	compute_node_nr_of_productions(void)
{
	int		cur_number;
	Int_list	result = NULL;
	int		buffer;
	int		edge_nr = 0;
	Graph		graph;
	Node		node;
	Node		LHS_node;
	Set_of_nodelist	set_of_list;
	Edge		cur_edge;

	for (buffer=N_PASTE_BUFFERS; buffer<N_BUFFERS; buffer++) 
	{
		for_all_graphs (buffer, graph)
		{
			if( graph->is_production )
			{
				cur_number = 0;
				LHS_node = graph->gra.gra.nce1.left_side->node;
				for_nodes( graph, node )
				{
					if( (node != LHS_node) && (inside( node->x, node->y, LHS_node )) )
						cur_number++;
				}
				end_for_nodes( graph, node );

				graph->lp_graph.node_nr 		= cur_number;
				graph->lp_graph.save_node_nr	= cur_number;
				result = add_to_intlist( result, cur_number );

				/****** Listen aller Knoten mit gleichen Einbettungsregeln aufbauen 			******/
				set_of_list = NULL;
				for_nodes( graph, node )
				{
					if( !inside(node->x, node->y, LHS_node) )
					{
						set_of_list = add_to_embedding( set_of_list, node );
					}
				}
				end_for_nodes( graph, node );
				graph->lp_graph.embedding_rules = set_of_list;

				/****** Anzahl der Kanten innerhalb LHS-node berechnen					******/
				for_nodes( graph, node )
				{
					if( inside(node->x, node->y, LHS_node) )
					{
						for_edge_sourcelist( node, cur_edge )
						{
							if( inside(cur_edge->target->x, cur_edge->target->y, LHS_node) )
							{
								edge_nr++;
							}
						}
						end_for_edge_sourcelist( node, cur_edge );
					}
				}
				end_for_nodes( graph, node );
				graph->lp_graph.edge_nr = edge_nr;
				edge_nr = 0;
			}
		} 
		end_for_all_graphs (buffer, graph);
	}
	return( result );
}

/********************************************************************************/
/*										*/
/*	Funktionen um parsing Baum aufzubauen					*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function:	node_lists_are_disjunkt
Input:	Set_of_parsing_elements	pe_list lpp_Parsing_element pe;

	Ueberprueft, ob die Knotenliste von pe disjunkt ist mit allen Knotenlisten
	der lpp_Parsing_elemente von pe_list

Output:	TRUE	iff. Knotenlisten sind disjunkt
	FALSE	sonst
*********************************************************************************/

int	node_lists_are_disjunkt(Set_of_parsing_elements pe_list, lpp_Parsing_element pe)
{
	Set_of_parsing_elements	cur_list_elem;
	Nodelist		cur_list_node,
				cur_node;

	for_set_of_parsing_elements( pe_list, cur_list_elem )
	{
		for_nodelist( cur_list_elem->pe->nodes, cur_list_node )
		{
			for_nodelist( pe->nodes, cur_node )
			{
				if( cur_node->node == cur_list_node->node )
				{
					return( FALSE );
				}
			}
			end_for_nodelist( pe->nodes, cur_node );
		}
		end_for_nodelist( cur_list_elem->nodes, cur_list_node );
	}
	end_for_set_of_parsing_elements( pe_list, cur_list_elem );

	return( TRUE );
}

/*********************************************************************************
function	create_permutations_of_nodes
Input:	Set_of_parsing_elements	first_node, group, 
	int 			number, 
	Set_of_arsing_elements 	result,
	int			hierarchy_level

	Erzeugt alle Moeglichen Permutationen (rekursiv), die mit den 
	lpp_Parsing_elementen, beginnend mit first_node, gebildet werden koennen. 
	Diese Permutationen befinden sich in group. Die group wird dann in test_iso 
	mit allen Produktionen, die sich im Speicher befinden verglichen. Ist eine 
	Isomorph zur Gruppe, so liefert test_iso ein Set_of_parsing_elements zurueck, 
	das eine Kopie aller Elemente der group enthaelt.

Output:	TRUE, iff. neue lpp_Parsing_elemente konnten gefunden werden
	FALSE otherwise
*********************************************************************************/

int	create_permutations_of_nodes(Set_of_parsing_elements first_node, Set_of_parsing_elements group, int number, Set_of_parsing_elements *result, int hierarchy_level, Int_list production_length, int group_length)
{
	Set_of_parsing_elements		pe 		= NULL;
	int				changed 	= FALSE;
	Set_of_parsing_elements		cur_node;
	int				change_lower;

	/****** mache iso_test falls Anzahl der Elemente in Group in der Integerliste mit den Knotenanzahlen		******/
	/****** enthalten ist 												******/
	if( is_in_intlist(production_length, group_length) )
	{
		pe = test_iso( group, hierarchy_level );
	}

	/****** Wenn der iso_test erfolgreich war, dann ergaenze result um das neue Element				******/
	if( pe != NULL )
	{
		*result = union_to_tree( *result, pe );
		changed = TRUE;
	}

	/****** wenn number = -1, dann wurden bereits alle Elemente durch solche aus new_set abgedeckt			******/
	if( number != -1 )
	{

		/****** Durchlaufe alle lpp_Parsing_elements und erzeuge rekursiv alle moeglichen Kombinationen		******/
		for_set_of_parsing_elements( first_node, cur_node )
		{
			/****** Befindet sich in der Knotenliste des neuen Elements keiner der Knoten der group		******/
			if( node_lists_are_disjunkt(group, cur_node->pe) )
			{
				/****** dann ergaenze group um neuen Knoten						******/
				group = add_to_set_of_parsing_elements( group, new_set_of_parsing_elements(cur_node->pe) );

				if( number != 0 )
				{
					/****** rekursiver Aufruf, solange nicht die max. Anzahl von Knoten in  	******/
					/****** Produktionen erreicht wurde						******/
					change_lower = create_permutations_of_nodes
							( cur_node->next, group, number-1, result, 
							  hierarchy_level, production_length, group_length + 1 );
					if( change_lower ) changed = TRUE;
				}

				group = delete_from_set_of_parsing_elements( group, cur_node );
			}
		}
		end_for_set_of_parsing_elements( first_node, cur_node );
	}

	return( changed );
}

/*********************************************************************************
function:	pars_tree
Input:	Set_of_parsing_elements	first_node, group, 
	int 			number, 
	Set_of_arsing_elements 	result,
	int			hierarchy_level

	Erzeugt rekursiv alle Kombinationen der lpp_Parsing_elemente (die sich in
	new_set und in old_set befinden), wobei sich immer mindestens ein
	Element aus new_set in der selektierten group befindet

Output:	TRUE, iff. neue lpp_Parsing_elemente konnten gefunden werden
	FALSE otherwise
*********************************************************************************/

int	pars_tree(Set_of_parsing_elements new_set, Set_of_parsing_elements old_set, Set_of_parsing_elements group, int number, Set_of_parsing_elements *result, int hierarchy_level, Int_list production_length, int group_length)
{
	int				changed 	= FALSE;
	Set_of_parsing_elements		cur_node;
	int				change_lower;

	/****** Durchlaufe alle lpp_Parsing_elements und erzeuge rekursiv alle moeglichen Kombinationen			******/
	for_set_of_parsing_elements( new_set, cur_node )
	{
		/****** Befindet sich in der Knotenliste des neuen Elements keiner der Knoten der group			******/
		if( node_lists_are_disjunkt(group, cur_node->pe) )
		{ 
			/****** dann ergaenze group um neues pe								******/
			group = add_to_set_of_parsing_elements( group, new_set_of_parsing_elements(cur_node->pe) );

			change_lower = create_permutations_of_nodes
				( old_set, group, number-1, result, hierarchy_level, production_length, group_length + 1 );
			if( change_lower ) changed = TRUE;


			/****** rekursiver Aufruf dieser Funktion, um innerhalb von new_set zu permutieren		******/

			if( number != 0 )
			{
				change_lower = pars_tree( cur_node->next, old_set, group, 
						number-1, result, hierarchy_level, production_length, group_length + 1 );
				if( change_lower ) changed = TRUE;
			}

			group = delete_from_set_of_parsing_elements( group, cur_node );
		}
	}
	end_for_set_of_parsing_elements( first_node, cur_node );

	return( changed );
}

/*********************************************************************************
function:	nodelist_includes_all_nodes
Input:	lpp_Parsing_element Pars_elem

	Testet, ob sich in der Knotenliste von Pars_elem alle Knoten des 
	untersuchten Graphen befinden. Wird aufgerufen, bevor ein Startsymbol 'S'
	als Ergebnis zurueckgegeben wird (Function: create_parsing_tree).

Output:	TRUE, wenn alle Knoten enthalten sind.
	False sonst
*********************************************************************************/

int	nodelist_includes_all_nodes(lpp_Parsing_element pars_elem)
{
	Nodelist	cur_table_node;
	Node		cur_graph_node;
	int		table_nr	= 0;
	int		graph_nr	= 0;

	/****** Berechnung der Anzahl der lpp_Parsing_elements									******/
	for_nodelist( pars_elem->nodes, cur_table_node )
	{
		table_nr++;
	}
	end_for_nodelist( pars_elem->nodes, cur_table_node );

	/****** Berechnung der Anzahl der Graph-Knoten										******/
	for_nodes( pars_elem->nodes->node->graph, cur_graph_node )
	{
		graph_nr++;
	}
	end_for_nodes( pars_elem->nodes->node->graph, cur_graph_node );

	if( table_nr == graph_nr )
		return( TRUE );

	return( FALSE );
}

/*********************************************************************************
function	create_parsing_tree

	create the parsing tree for the selected graph

Output:	lpp_Parsing_element which is the root of tree (iff. there was a selected graph )
*********************************************************************************/

Graph	create_parsing_tree(void)
{
	
 	Graph			graph			= compute_graph();
	Int_list		production_length 	= compute_node_nr_of_productions();
	int			max_nodes		= 0;
	Set_of_parsing_elements	result			= NULL;
	Set_of_parsing_elements	old_tree		= NULL;
	Set_of_parsing_elements	new_tree;

	int			changed			= TRUE;
	int			hierarchy_level		= 2;
	lpp_Parsing_element		back			= NULL;
	Set_of_parsing_elements	cur;
	Int_list		cur_int;

	if( graph )
	{
		if( !all_nodes_are_terminals(graph) )
		{
			MsgBox( "This Algorithm needs a Graph without non_terminals", CMD_OK );
		}
		else
		{
			free_table( graph->lp_graph.table );
			/****** Erzeuge von jedem Knoten des Graphen ein lpp_Parsing_element (Da lpp_Parsing_elemente nicht verkettet sind,	******/
			/****** existiert fuer jedes lpp_Parsing_element ein Set_of_parsing_elements).					******/
			new_tree = create_parsing_elements_of_all_nodes( graph );

			/****** Berechne die maximale Knotenzahl der Produktionen							******/
			for_int_list( production_length, cur_int)
			{
				if( cur_int->integer > max_nodes )
					max_nodes = cur_int->integer;
			}
			end_for_int_list( production_length, cur_int);


			/****** solange neue Moeglichkeiten fuer die Anwendung einer Produktion gefunden werden				******/
			while( changed )
			{
				/****** Suche nach Moeglichkeiten der Anwendung einer Produktion und lege diese, falls welche vorhanden	******/
				/****** sind, in result ab										******/
				changed = pars_tree( new_tree, old_tree, NULL, max_nodes, &result, hierarchy_level, production_length, 0 );

				/****** Fuege die eine Stufe vorher gefundenen Moeglichkeiten bei den alten ein				******/
				old_tree = union_to_tree( old_tree, new_tree );

				/****** Fuege die neu gefundenen Moeglichkeiten als new_tree ein					******/
				new_tree = result;

				/****** setze result auf NULL, damit darin wieder die neuen Moeglichkeiten abgelegt werden koennen	******/
				result = NULL;

				/****** Marke, bei der wievielten Suche das lpp_Parsing_element gefunden wurde				******/
				hierarchy_level++;
			}

			/****** Suche ein Start-symbol, das alle Knoten enthaelt und schicke dieses zurueck				******/
			for_set_of_parsing_elements( old_tree, cur )
			{
				if( my_strcmp(cur->pe->label, "S") )
				{
					if( nodelist_includes_all_nodes(cur->pe) )
					{
						back = cur->pe;
						graph->lp_graph.table = back;
						return( graph );
					}
				}
			}
			end_for_set_of_parsing_elements( old_tree, cur );
			clean_derivation_table( NULL, old_tree );
		}
	}
	return( NULL );
}

/*********************************************************************************
function	create_parsing_tree_graph

	create the parsing tree for the selected graph

Output:	lpp_Parsing_element which is the root of tree (iff. graph could be reduced)
*********************************************************************************/

Graph	create_parsing_tree_graph(Graph graph)
{
	Int_list		production_length 	= compute_node_nr_of_productions();
	int			max_nodes		= 0;
	Set_of_parsing_elements	result			= NULL;
	Set_of_parsing_elements	old_tree		= NULL;
	Set_of_parsing_elements	new_tree;

	int			changed			= TRUE;
	int			hierarchy_level		= 2;
	lpp_Parsing_element		back			= NULL;
	Set_of_parsing_elements	cur;
	Int_list		cur_int;

	if( all_nodes_are_terminals( graph ) )
	{
		free_table( graph->lp_graph.table );
		/****** Erzeuge von jedem Knoten des Graphen ein lpp_Parsing_element (Da lpp_Parsing_elemente nicht verkettet sind,	******/
		/****** existiert fuer jedes lpp_Parsing_element ein Set_of_parsing_elements).					******/
		new_tree = create_parsing_elements_of_all_nodes( graph );

		/****** Berechne die maximale Knotenzahl der Produktionen							******/
		for_int_list( production_length, cur_int)
		{
			if( cur_int->integer > max_nodes )
				max_nodes = cur_int->integer;
		}
		end_for_int_list( production_length, cur_int);



		/****** solange neue Moeglichkeiten fuer die Anwendung einer Produktion gefunden werden				******/
		while( changed )
		{
			/****** Suche nach Moeglichkeiten der Anwendung einer Produktion und lege diese, falls welche vorhanden	******/
			/****** sind, in result ab										******/
			changed = pars_tree( new_tree, old_tree, NULL, max_nodes, &result, hierarchy_level, production_length, 0 );

			/****** Fuege die eine Stufe vorher gefundenen Moeglichkeiten bei den alten ein				******/
			old_tree = union_to_tree( old_tree, new_tree );

			/****** Fuege die neu gefundenen Moeglichkeiten als new_tree ein					******/
			new_tree = result;

			/****** setze result auf NULL, damit darin wieder die neuen Moeglichkeiten abgelegt werden koennen	******/
			result = NULL;

			/****** Marke, bei der wievielten Suche das lpp_Parsing_element gefunden wurde				******/
			hierarchy_level++;
		}

		/****** Suche ein Start-symbol, das alle Knoten enthaelt und schicke dieses zurueck				******/
		for_set_of_parsing_elements( old_tree, cur )
		{
			if( my_strcmp(cur->pe->label, "S") )
			{
				if( nodelist_includes_all_nodes(cur->pe) )
				{
					back = cur->pe;
					graph->lp_graph.table = back;
					return( graph );
				}
			}
		}
		end_for_set_of_parsing_elements( old_tree, cur );
		clean_derivation_table( NULL, old_tree );
	}
	else
	{
		MsgBox( "This Algorithm needs a Graph without non_terminals", CMD_OK );
		return( NULL );
	}

	MsgBox( "Can't generate graph with this grammar", CMD_OK );
	return( NULL );
}
