#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lp_history.h"
#include "lp_tree_top_sort.h"

#include "lpp_tree.h"
#include "lpp_table_to_tree.h"


/*****************************************************************************
function:	get_graph_node
Input:	Node prod_node, Attributes_ref_list attr_ref_list

	prod_node ist ein terminaler Knoten --> es existiert ein entsprechender
	im Graphen. Dieser wird in dieser Funktion berechnet:
	Suche von attr_ref_list aus das passende set_of_partsing_elements s;
	s->pe->nodes->node liefert das gewuenschte Ergebnis (In der Knotenliste
	kann sich nur ein Knoten befinden

Output:	Knoten im Graph
*****************************************************************************/

Node	get_graph_node(Node prod_node, Attributes_ref_list attr_ref_list)
{
	Set_of_parsing_elements	cur_set;
	Node			cur_node;

	/*** Suchen des passenden 'Set_of_parsing_elements'								***/
	for_set_of_parsing_elements( attr_ref_list->attr_head->lower_edge->derivation_nodes, cur_set )
	{
		/*** Da eine Datenstruktur fuer eine Menge von Produktionen steht, kann es sein, dass der Zeiger von	***/
		/*** cur_set auf den Produktionsknoten auf den Gleichen Knoten in einer isomorphen Produktion zeigt	***/
		for_node_multi_suc( prod_node, cur_node )
		{
			if( cur_set->production_iso == cur_node )
			{
				return( cur_set->pe->nodes->node );
			}
		}
		end_for_node_multi_suc( prod_node, cur_node );
	}
	end_for_set_of_parsing_elements( attr_ref_list->attr_head->lower_edge->derivation_nodes, cur_set );

	return( NULL );
}

/*****************************************************************************
function:	get_graph_node_for_first
Input:	Node prod_node, Attributes_ref_list attr_ref_list

	prod_node ist ein terminaler Knoten --> es existiert ein entsprechender
	im Graphen. Dieser wird in dieser Funktion berechnet:
	Suche von attr_ref_list aus das passende set_of_partsing_elements s;
	s->pe->nodes->node liefert das gewuenschte Ergebnis (In der Knotenliste
	kann sich nur ein Knoten befinden.
	GLEICHES WIE OBEN. Funktioniert aber fuer erste Berechnung

Output:	Knoten im Graph
*****************************************************************************/

Node	get_graph_node_for_first(Node prod_node, Attributes_ref_list attr_ref_list)
{
	Set_of_parsing_elements	cur_set;
	Node			cur_node;

	/*** Suchen des passenden 'Set_of_parsing_elements'								***/
	for_set_of_parsing_elements( attr_ref_list->attr_head->upper_edge->derivation_nodes, cur_set )
	{
		/*** Da eine Datenstruktur fuer eine Menge von Produktionen steht, kann es sein, dass der Zeiger von	***/
		/*** cur_set auf den Produktionsknoten auf den Gleichen Knoten in einer isomorphen Produktion zeigt	***/
		for_node_multi_suc( prod_node, cur_node )
		{
			if( cur_set->production_iso == cur_node )
			{
				return( cur_set->pe->nodes->node );
			}
		}
		end_for_node_multi_suc( prod_node, cur_node );
	}
	end_for_set_of_parsing_elements( attr_ref_list->attr_head->upper_edge->derivation_nodes, cur_set );

	return( NULL );
}

/*****************************************************************************
function:	get_corresponding_s_o_pe
Input:	Node prod_node, Set_of_parsing_elements set

	Siehe Funktion drueber, aber als Rueckgabe Set_of_parsing_elements s

Output:	s
*****************************************************************************/

Set_of_parsing_elements	get_corresponding_s_o_pe(Node prod_node, Set_of_parsing_elements set)
{
	Set_of_parsing_elements	cur_set;
	Node			cur_node;

	/*** Suchen des passenden 'Set_of_parsing_elements'								***/
	for_set_of_parsing_elements( set, cur_set )
	{
		/*** Da eine Datenstruktur fuer eine Menge von Produktionen steht, kann es sein, dass der Zeiger von	***/
		/*** cur_set auf den Produktionsknoten auf den Gleichen Knoten in einer isomorphen Produktion zeigt	***/
		for_node_multi_suc( prod_node, cur_node )
		{
			if( cur_set->production_iso == cur_node )
			{
				return( cur_set );
			}
		}
		end_for_node_multi_suc( prod_node, cur_node );
	}
	end_for_set_of_parsing_elements( set, cur_set );
	return NULL;
}


/*****************************************************************************
function:	get_graph_edge
Input:	Edge prod_edge, Attributes_ref_list attr_ref_list

	source und target von prod_edge sind terminale ---> es existiert eine
	entsprechende im Graphen.
	Suche die entsprechenden Source und Target-Knoten im Graphen;
	Suche Kante mit passender Markierung

Output:	Oben bestimmte Kante
*****************************************************************************/

Edge	get_graph_edge(Edge prod_edge, Attributes_ref_list attr_ref_list)
{
	Edge	cur_edge;

	Node	graph_source_node	= get_graph_node( prod_edge->source, attr_ref_list );

	for_edge_sourcelist( graph_source_node, cur_edge )
	{
		if( my_strcmp(cur_edge->label.text, prod_edge->label.text) )
		{
			return( cur_edge );
		}
	}
	end_for_edge_sourcelist( graph_source_node, cur_edge );

	return( NULL );
}

/*****************************************************************************
function:	get_graph_edge_for_first
Input:	Edge prod_edge, Attributes_ref_list attr_ref_list

	source und target von prod_edge sind terminale ---> es existiert eine
	entsprechende im Graphen.
	Suche die entsprechenden Source und Target-Knoten im Graphen;
	Suche Kante mit passender Markierung
	Funktioniert wie oben, nur fuer root

Output:	Oben bestimmte Kante
*****************************************************************************/

Edge	get_graph_edge_for_first(Edge prod_edge, Attributes_ref_list attr_ref_list)
{
	Edge	cur_edge;

	Node	graph_source_node	= get_graph_node_for_first( prod_edge->source, attr_ref_list );

	for_edge_sourcelist( graph_source_node, cur_edge )
	{
		if( my_strcmp(cur_edge->label.text, prod_edge->label.text) )
		{
			return( cur_edge );
		}
	}
	end_for_edge_sourcelist( graph_source_node, cur_edge );

	return( NULL );
}

/*****************************************************************************
function:	get_graph_edge_for_in_embedding
Input:	Edge prod_edge; Attributes_ref_list upper_attr_ref_list, lower_attr_ref_list

	wie get_graph_edge, nur Art wie Knoten im Graphen berechnet werden ist anders
	( fuer Knoten ausserhalb ist anderes Attributes_ref_list notwendig )

Output:	berechnete Kante
*****************************************************************************/

Edge	get_graph_edge_for_in_embedding(Edge prod_edge, Attributes_ref_list attr_ref_list)
{
	Edge	cur_edge;

	Node	graph_target_node	= get_graph_node( prod_edge->target, attr_ref_list );

	for_edge_targetlist( graph_target_node, cur_edge )
	{
		if( my_strcmp(cur_edge->label.text, prod_edge->label.text) && 
		    my_strcmp(cur_edge->source->label.text, prod_edge->source->label.text) )
		{
			return( cur_edge );
		}
	}
	end_for_edge_targetlist( graph_target_node, cur_edge );

	return( NULL );
}

/*****************************************************************************
function:	get_graph_edge_for_out_embedding
Input:	Edge prod_edge; Attributes_ref_list attr_ref_list

	wie get_graph_edge, nur Art wie Knoten im Graphen berechnet werden ist anders
	( fuer Knoten ausserhalb ist anderes  notwendig )

Output:	berechnete Kante
*****************************************************************************/

Edge	get_graph_edge_for_out_embedding(Edge prod_edge, Attributes_ref_list attr_ref_list)
{
	Edge	cur_edge;

	Node	graph_source_node	= get_graph_node( prod_edge->source, attr_ref_list );

	for_edge_sourcelist( graph_source_node, cur_edge )
	{
		if( my_strcmp(cur_edge->label.text, prod_edge->label.text) && 
		    my_strcmp(cur_edge->target->label.text, prod_edge->target->label.text) )
		{
			return( cur_edge );
		}
	}
	end_for_edge_sourcelist( graph_source_node, cur_edge );

	return( NULL );
}




/*****************************************************************************
function:	find_optimal_attributes_ref_list
Input:	lpp_Parsing_element	pe

	Sucht Attributes_ref_list a, mit den optimalen Werten

Output:	a
*****************************************************************************/

Attributes_ref_list	find_optimal_attributes_ref_list(lpp_Parsing_element pe)
{
	Derivation		cur_der;
	Attributes_ref_list	cur_attr_ref_list;
	Attributes_ref_list	optimal_attr_list	= pe->derivations->attributes_table_down->upper_productions;

	for_derivation( pe->derivations, cur_der )
	{
		for_attributes_ref_list( cur_der->attributes_table_down->upper_productions, cur_attr_ref_list )
		{
			if( optimal_attr_list->big_c_star > cur_attr_ref_list->big_c_star )
			{
				optimal_attr_list = cur_attr_ref_list;
			}
		}
		end_for_attributes_ref_list( cur_der->attributes_table_down->upper_productions, cur_attr_ref_list );
	}
	end_for_derivation( pe->derivations, cur_der );

	return( optimal_attr_list );
}

/*****************************************************************************
function:	apply_production_on_tree_ref
Input:	Graph prod, tree_ref father

	Wende prod auf father an

Output:	---
*****************************************************************************/

void	apply_production_on_tree_ref(Graph prod, tree_ref father, Attributes_ref attr_ref)
{
	tree_ref	new_place_to_append,
			object_to_append;
	tree_node_ref	node_to_append;
	tree_edge_ref	edge_to_append;

	Group		cur_group, g;
	Edge		cur_edge;
	int		i, number_of_in_embeddings;

	number_of_in_embeddings  = size_of_embedding (prod->gra.gra.nce1.embed_in);


	cur_group = prod->gra.gra.nce1.right_side;

	father->tree_rec.node->used_prod = prod;

	new_place_to_append = father;

	father->tree_rec.node->leaf = 0;

	/*** Haenge alle Knoten an						***/
	for_group(cur_group, g)	 
	{
		object_to_append = new_tree_ref();
		node_to_append = new_tree_node_ref();

		node_to_append->prod_iso = g->node;
		object_to_append->tree_rec.node = node_to_append;
		object_to_append->tree_rec_type = TREE_NODE;
		object_to_append->hierarchy_level = father->hierarchy_level + 1;
		g->node->lp_node.tree_iso = object_to_append;

		/*** Nur fuer terminals existiert ein Knoten im Graphen							***/
		if( node_is_terminal(g->node) )
		{
			node_to_append->graph_iso = get_graph_node( g->node, attr_ref->attr_list );
		}
		/*** Zeiger auf tree_ref eintragen, da fuer ableitungen weiter unten noetig				***/
		if( node_is_nonterminal(g->node) )
		{
			get_corresponding_s_o_pe(g->node, attr_ref->attr_list->attr_head->lower_edge->derivation_nodes)->pe->tree_ref_iso = 
				object_to_append;
		}

		new_place_to_append 
			= append( father, object_to_append, new_place_to_append );
	
	}
	end_for_group(cur_group, g);

	/*** Haenge alle inneren Kanten und alle out_embeddings an		***/
	for_group(cur_group, g)
	{
		for_edge_sourcelist(g->node, cur_edge)
		{
			object_to_append = new_tree_ref();
   			edge_to_append = new_tree_edge_ref();

			edge_to_append->prod_iso 	= cur_edge;
			if(edge_is_out_embedding(cur_edge, prod) )
				edge_to_append->type	= OUT_CONN_REL;
			else	edge_to_append->type 	= RHS_EDGE;
			edge_to_append->target		= cur_edge->target->lp_node.tree_iso;

			object_to_append->tree_rec.history_elem = edge_to_append;
			object_to_append->tree_rec_type = HISTORY_ELEM;
			object_to_append->hierarchy_level = father->hierarchy_level + 1;

			cur_edge->lp_edge.tree_iso = object_to_append;

			new_place_to_append 
				= append( father, object_to_append, new_place_to_append );

		}
		end_for_edge_sourcelist(g->node,cur_edge);
	}
	end_for_group(cur_group, g);


	/* append all in_embedding of the production to the derivation net */
	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = prod->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, cur_edge )
			{
				object_to_append = new_tree_ref();
   				edge_to_append = new_tree_edge_ref();

				edge_to_append->prod_iso 	= cur_edge;
				edge_to_append->type 		= IN_CONN_REL;
				edge_to_append->target 		= cur_edge->target->lp_node.tree_iso;

				object_to_append->tree_rec.history_elem = edge_to_append;
				object_to_append->tree_rec_type = HISTORY_ELEM;
				cur_edge->lp_edge.tree_iso = object_to_append;
				object_to_append->hierarchy_level = father->hierarchy_level + 1;

				new_place_to_append 
					= append( father, object_to_append, new_place_to_append );
			}
			end_for_edge_sourcelist( g->node, cur_edge );
	      	}
		end_for_group (cur_group, g);
	}
}

/*****************************************************************************
function:	create_root_of_tree
Input:	Graph prod, Attributes_ref_list used_attr_ref( wird benoetigt, um den
	entsprechenden Knoten im Graphen zu finden, falls ein terminaler Knoten 
	in der linken Seite der Produktion liegt)

	Erzeugt einen ersten Knoten eines derivation_tree mit Eintrag "S"
	Es werden temporaere Zeiger von/auf die Produktion gesetzt

Output:	tree_ref der erzeugt wird
*****************************************************************************/
tree_ref	create_root_of_tree(Graph prod, Attributes_ref_list used_attr_ref)
{
	tree_ref	new_place_to_append;
	tree_ref	object_to_append;
	tree_node_ref	node_to_append;
	tree_edge_ref	edge_to_append;

	Group		cur_group, g;
	Edge		cur_edge;
	int		i, number_of_in_embeddings;

	tree_ref	node_to_be_father	= new_tree_ref();
	tree_node_ref	new_node_ref		= new_tree_node_ref();

	number_of_in_embeddings  = size_of_embedding (prod->gra.gra.nce1.embed_in);



	cur_group = prod->gra.gra.nce1.right_side;


	/*** alle Zeiger im tree setzen (soweit wie moeglich)									***/
	node_to_be_father->tree_rec_type	= TREE_NODE;
	node_to_be_father->tree_rec.node	= new_node_ref;
	node_to_be_father->tree_rec.node->leaf	= 0;

	new_node_ref->prod_iso	= prod->gra.gra.nce1.left_side->node;
	new_node_ref->used_prod	= prod;

	new_place_to_append = node_to_be_father;

	for_group(cur_group, g)	 
	{
		object_to_append = new_tree_ref();
		node_to_append = new_tree_node_ref();

		node_to_append->prod_iso = g->node;
		object_to_append->tree_rec.node = node_to_append;
		object_to_append->tree_rec_type = TREE_NODE;
		object_to_append->hierarchy_level = 1;
		g->node->lp_node.tree_iso = object_to_append;

		/*** Nur fuer terminals existiert ein Knoten im Graphen							***/
		if( node_is_terminal(g->node) )
		{
			node_to_append->graph_iso = get_graph_node_for_first( g->node, used_attr_ref );
		}

		/*** Zeiger auf tree_ref eintragen, da fuer ableitungen weiter unten noetig				***/
		if( node_is_nonterminal(g->node) )
		{
			get_corresponding_s_o_pe( g->node, used_attr_ref->attr_head->upper_edge->derivation_nodes )->pe->tree_ref_iso = object_to_append;
		}

		new_place_to_append 
			= append(node_to_be_father, object_to_append, new_place_to_append);
	}
	end_for_group(cur_group, g);

	for_group(cur_group, g)
	{
		for_edge_sourcelist(g->node, cur_edge)
		{
			
			object_to_append = new_tree_ref();
   			edge_to_append = new_tree_edge_ref();

			edge_to_append->prod_iso 	= cur_edge;
			if(edge_is_out_embedding(cur_edge, prod) )
				edge_to_append->type	= OUT_CONN_REL;
			else	edge_to_append->type 	= RHS_EDGE;
			edge_to_append->target		= cur_edge->target->lp_node.tree_iso;

			object_to_append->tree_rec.history_elem = edge_to_append;
			object_to_append->tree_rec_type = HISTORY_ELEM;
			object_to_append->hierarchy_level = 1;

			cur_edge->lp_edge.tree_iso = object_to_append;

			new_place_to_append 
				= append(node_to_be_father, object_to_append, new_place_to_append);
		}
		end_for_edge_sourcelist(g->node,cur_edge);
	}
	end_for_group(cur_group, g);

	/* append all _in_embedding of the production to the derivation net */
	for( i=0; i<number_of_in_embeddings; i++) 
	{
		cur_group = prod->gra.gra.nce1.embed_in[i].embed;
	      	for_group (cur_group, g) 
		{
			for_edge_sourcelist( g->node, cur_edge )
			{
				object_to_append = new_tree_ref();
   				edge_to_append = new_tree_edge_ref();

				edge_to_append->prod_iso 	= cur_edge;
				edge_to_append->type 		= IN_CONN_REL;
				edge_to_append->target 		= cur_edge->target->lp_node.tree_iso;

				object_to_append->tree_rec.history_elem = edge_to_append;
				object_to_append->tree_rec_type = HISTORY_ELEM;
				cur_edge->lp_edge.tree_iso = object_to_append;
				object_to_append->hierarchy_level = 1;

				new_place_to_append 
					= append(node_to_be_father, object_to_append, new_place_to_append);
			}
			end_for_edge_sourcelist( g->node, cur_edge );
	      	}
		end_for_group (cur_group, g);
	}

	return( node_to_be_father );
}

/*****************************************************************************
function:	create_tree
Input:	Attributes_ref head

	rekursiv mit:
	Abbruch wenn head = NULL
	sonst:	wende lower_prod auf den Knoten an, auf den 
		head->attr_list->attr_head-upper_edge->pe->tree_ref_iso zeigt;
		ausserdem selbes ueber zeiger 'next_optimal_ref (es koennen ja
		mehrere Knoten abgeleitet werden!)

Output:	---
*****************************************************************************/

void	create_tree(Attributes_ref head)
{
	Attributes_ref		cur_attr_ref		= head;
	Attributes_ref_list	optimal_attr_list,
				cur_attr_list;

	if( cur_attr_ref )
	{
		while( cur_attr_ref )
		{
			apply_production_on_tree_ref( cur_attr_ref->lower_prod,
					cur_attr_ref->attr_list->attr_head->below_derivated_set->pe->tree_ref_iso, cur_attr_ref );

			cur_attr_ref = cur_attr_ref->next_optimal_ref;
		}

		cur_attr_ref = head;
		while( cur_attr_ref )
		{
			/*** rekursiver Aufruf stufe weiter unten, zuerst suchen, wo genau weitermachen			***/
			/*** es koennte ja sein, dass man auswaehlen kann, da unten gleiches lpp_Parsing_element auf	***/
			/*** mehrere Arten abgeleitet werden konnte							***/

			/*** gehe Stufe tiefer										***/
			optimal_attr_list = cur_attr_ref->same_prod_lower;

			/*** Schaue durch, ( bis anderes abgeleitetes PE unten )					***/
			for_same_upper_production( cur_attr_ref->same_prod_lower, cur_attr_list )
			{
				/*** Wenn nicht mehr is_same und nicht erste( Weil dann !is_same )			***/
				if( !cur_attr_list->attr_head->is_same && (cur_attr_list != cur_attr_ref->same_prod_lower) )
				{
					goto exit;
				}

				if( optimal_attr_list->big_c_star > cur_attr_list->big_c_star )
				{
					optimal_attr_list = cur_attr_list;
				}
			}
			end_for_same_upper_production( cur_attr_ref->same_prod_lower, cur_attr_list );

exit:			if( optimal_attr_list )
			{
				/*** erzeuge Zeiger "optimal_attributes_ref"						***/
				get_optimal_attributes_ref( optimal_attr_list );

				/*** rekursiver Aufruf fuer unten							***/
				create_tree( optimal_attr_list->optimal_attributes_ref );
			}

			cur_attr_ref = cur_attr_ref->next_optimal_ref;
		}
	}
}

/*****************************************************************************
function:	convert_table_to_tree
Input:	lpp_Parsing_element	table_head

	Erhaelt als Eingabe den Derivation_table; rechnet aus diesem den tree
	mit dem optimalen Layout aus

Output:	tree_node_ref tree
*****************************************************************************/

tree_ref	convert_table_to_tree(lpp_Parsing_element table_head)
{
	Attributes_ref_list	optimal_start;
	tree_ref		root_of_derivation_tree;


	optimal_start		= find_optimal_attributes_ref_list( table_head );
	root_of_derivation_tree	= create_root_of_tree( optimal_start->refs_between_prods->upper_prod, optimal_start );


	/****** Zeiger optimal_attributes_ref erzeugen, ausser Ableitung besteht nur aus start				******/
	if( optimal_start->refs_between_prods->lower_prod )
	{
		get_optimal_attributes_ref( optimal_start );
	}

	/****** Weiter gehts ueber den Zeiger 'optimal_attributes_ref' und zwar in einer rekursiven Funktion		******/
	create_tree( optimal_start->optimal_attributes_ref );

	
	return( root_of_derivation_tree );
}
