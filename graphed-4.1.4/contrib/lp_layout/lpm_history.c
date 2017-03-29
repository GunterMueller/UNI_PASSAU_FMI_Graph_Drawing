#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lpm_multi_functions.h"
#include "lpm_history.h"
#include "lpm_create_lgg.h"
#include "lp_test.h"

/***************************************************************************
function:	new_list_of_multi_lp

	Create a new List_of_multi_lp n

Output: Pointer to n
***************************************************************************/

List_of_multi_lp	new_list_of_multi_lp(void)
{
	List_of_multi_lp	new = (List_of_multi_lp)mymalloc( sizeof(struct list_of_multi_lp) );

	new->OPTIMAL_COSTS	= 0;
	new->list		= NULL;
	new->next		= NULL;

	return( new );
}

/***************************************************************************
function:	add_to_list_of_multi_lp
Input:	List_of_multi_lp list, new

	Append new at the end of list

Output:	list
***************************************************************************/

List_of_multi_lp	add_to_list_of_multi_lp(List_of_multi_lp list, List_of_multi_lp new)
{
	List_of_multi_lp	cur = list;

	if( cur == NULL ) return( new );
	while( cur->next != NULL )
		cur = cur->next;

	cur->next = new;

	return( list );
}
/***************************************************************************
function	create_LP_COSTS_edge
Input:	List_of_multi_lp lp_costs, tree_ref son

	Create in all lp_of_son, which are equal to lp_costs 
	the pointer LP_COSTS
***************************************************************************/

void	create_LP_COSTS_edge(List_of_multi_lp lp_costs, tree_ref son)
{
	Multi_edge	cur_multi_edge;
	Lp_of_father	cur_upper;
	Lp_of_son	cur_lower;

	for_multi_edge( son->father->tree_rec.node->multi_edges, cur_multi_edge )
	{
		for_upper_part( cur_multi_edge->lps_of_father, cur_upper )
		{
			for_lower_part( cur_upper->LP_set, cur_lower )
			{
				if( cur_lower->production == lp_costs->list->lps_of_father->production )
					cur_lower->LP_COSTS = lp_costs;
			}
			end_for_lower_part( lp_of_son, cur_lower );
		}
		end_for_upper_part( cur_multi_edge->lps_of_father, cur_upper );
	}
	end_for_multi_edge( son->father->tree_rec.node->multi_edges, cur_multi_edge );
}

/***************************************************************************
function:	create_LP_costs
Input:	tree_ref	father

	create Elements to compute LP_costs

1.	Erzeuge fuer jede in father moeglichen Produktionen ein 
	"List_of_multi_lp" mit erstem Listenelement ( Zeiger auf
	entsprechende "Lp_of_father)
2.	Durchlaufe alle restlichen Multi_edge, die zu weiteren 
	abgeleiteten Knoten gehoeren
3.	Durchlaufe alle LP_costs ( unter 1. erzeugt ) und
4.	Haenge jetziges multi_element unter entsprechendem 
	LP_costs Element ein
***************************************************************************/

void	create_LP_costs(tree_ref father)
{
	Multi_edge		cur_list 	= father->tree_rec.node->multi_edges;
	Lp_of_father		cur_upper ;
	List_of_multi_lp	new_LP_costs, cur_lp_costs, LP_costs_result = NULL;
	Multi_edge		new_list;

	if( cur_list != NULL )
	{
		cur_upper = cur_list->lps_of_father;		
		while( cur_upper )											/* 1 */
		{
			new_LP_costs 	= new_list_of_multi_lp();
			new_list	= new_multi_lp_list( cur_upper);
			new_LP_costs->list = new_list;
			if( father->father != NULL )
			{
				create_LP_COSTS_edge( new_LP_costs, father );
			}
			LP_costs_result = add_to_list_of_multi_lp( LP_costs_result, new_LP_costs );
			cur_upper = cur_upper->next;
		}
		father->tree_rec.node->LP_costs = LP_costs_result;

		cur_list = cur_list->next;
		while( cur_list )											/* 2 */
		{
			cur_lp_costs = father->tree_rec.node->LP_costs;
			while( cur_lp_costs )										/* 3 */
			{
				cur_upper = cur_list->lps_of_father;
				while( cur_upper )									/* 4 */
				{
					if( cur_upper->production == cur_lp_costs->list->lps_of_father->production )
						add_to_multi_lp_list( cur_lp_costs->list, new_multi_lp_list(cur_upper) );
					cur_upper = cur_upper->next;
				}

				cur_lp_costs = cur_lp_costs->next;
			}
			cur_list = cur_list->next;
		}
	}
}

/***************************************************************************
function:	new_multi_lp_list
Input:	Multi_lp	target;

	Create a new Multi_lp n with target target;

Output:	Pointer to n
***************************************************************************/

Multi_edge	new_multi_lp_list(Lp_of_father target)
{
	Multi_edge	new = (Multi_edge)mymalloc( sizeof(struct multi_edge));

	new->lps_of_father	= target;
	new->next		= NULL;

	return( new );
}

/***************************************************************************
function:	add_to_multi_lp_list
Input:	Multi_lp_list	list, new

	Append new at the end of list
***************************************************************************/

Multi_edge	add_to_multi_lp_list(Multi_edge list, Multi_edge new)
{
	Multi_edge	cur = list;

	if( cur == NULL ) return( new );

	while( cur->next != NULL )
		cur = cur->next;

	cur->next = new;

	return( list );
}

/***************************************************************************
function:	create_multi_lp_for_lower
Input:	production_ref productions

	Create Lp_of_son L for productions( and all equivalent productions )

Output:	L
***************************************************************************/

Lp_of_son		create_multi_lp_for_lower(Graph graph)
{
	Graph		cur_graph;
	Lp_of_son	result = NULL;
	Lp_of_son	cur;

	for_graph_multi_suc( graph, cur_graph )
	{
		cur = new_lp_e_lp();
		cur->production = cur_graph;
		result = add_to_lp_e_lp(result, cur);
	}
	end_for_graph_multi_suc( graph, cur_graph );

	return( result );
}

/***************************************************************************
function:	create_multi_lp_for_upper
Input:	production_ref productions

	Create Lp_of_father m for productions ( and all equivalent productions )

Output:	m
***************************************************************************/

Lp_of_father	create_multi_lp_for_upper(Graph graph, tree_ref father, tree_ref son)
{
	Graph		cur_graph;
	Lp_of_father	result = NULL;
	Lp_of_father	cur;


	for_graph_multi_suc( graph, cur_graph )
	{
		cur = new_multi_lp();
		cur->production = cur_graph;
		cur->son = son;
		cur->father = father->tree_rec.node;
		result = add_to_multi_lp(result, cur);
	}
	end_for_graph_multi_suc( graph, cur_graph );

	return( result );
}

/***************************************************************************
function:	ref_has_applied_son
Input:	tree_ref	father;

Output:	TRUE iff. father has at least one applied son
	FALSE otherwise
***************************************************************************/

int	ref_has_applied_son(tree_ref father)
{
	tree_ref	son = father->tree_rec.node->first_son;

	while( son )
	{
		if( (son->tree_rec_type == TREE_NODE) && (son->tree_rec.node->first_son != NULL) )
			return( TRUE );
		son = son->next_brother;
	}
	return( FALSE );
}

/***************************************************************************
function:	create_multi_lp_elements
Input:	tree_ref	father, son

	Create the elements between father and son for multi_lp
***************************************************************************/

void	create_multi_lp_elements(tree_ref father, tree_ref son)
{
	Lp_of_father	upper, cur;

	upper = create_multi_lp_for_upper(father->tree_rec.node->used_prod, father, son);
	cur = upper;
	if( ref_has_applied_son( father ) )
	{
		while(cur)
		{
			cur->LP_set = create_multi_lp_for_lower(son->tree_rec.node->used_prod);
			cur = cur->next;
		}
	}
	father->tree_rec.node->multi_edges = add_to_multi_lp_list( father->tree_rec.node->multi_edges, new_multi_lp_list(upper) );
	son->multi_edge = upper;
}

/***************************************************************************
function:	create_multi_edge_for_root
Input:	tree_ref father

	create father->multi_edge without the lower part of every production
***************************************************************************/

void	create_multi_edge_for_root(tree_ref father, tree_ref son)
{
	Lp_of_father	upper;

	upper = create_multi_lp_for_upper( father->tree_rec.node->prod_iso->graph, father, son);

	upper->father = father->tree_rec.node;
	father->tree_rec.node->multi_edges = add_to_multi_lp_list( father->tree_rec.node->multi_edges, new_multi_lp_list(upper) );
}

/***************************************************************************
function:	create_elements_for_multi_lgg
Input:	Tree_ref father

	Create structures for a multi_lp and recursively for all sons
***************************************************************************/

void	create_elements_for_multi_lgg(tree_ref father)
{
	tree_ref	son ;

	while( father )
	{
		if( father->tree_rec_type == TREE_NODE )
		{
			son = father->tree_rec.node->first_son;
			while( son )
			{
				if(son->tree_rec_type == TREE_NODE)
				{
					if(son->tree_rec.node->first_son != NULL)
					{
						create_multi_lp_elements( father, son );
						create_LP_costs( father );
						create_elements_for_multi_lgg( son );
					}
				}
				son = son->next_brother;
			}
			/* Extra - Behandlung, um fuer Wurzel des der_net sicher multi_edge zu erzeugen */
			if( (father->father == NULL) && (father->tree_rec.node->multi_edges == NULL) )
			{
				create_multi_edge_for_root( father, son );
				create_LP_costs( father );
			}
		}
		father = father->next_brother;
	}
}
