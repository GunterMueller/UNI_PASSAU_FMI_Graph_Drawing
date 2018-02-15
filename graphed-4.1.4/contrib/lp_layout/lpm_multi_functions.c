#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lpm_multi_functions.h"

/*********************************************************
function	new_opt_lp_of_son
Input:	Lp_of_son elem

	Create a new_opt_lp_of_son n with n->target = elem

Output:	Pointer to n
*********************************************************/

Opt_lp_of_son	new_opt_lp_of_son(Lp_of_son elem)
{
	Opt_lp_of_son	new =
			(Opt_lp_of_son)mymalloc( sizeof(struct opt_lp_of_son));

	new->target	= elem;
	new->next	= NULL;

	return( new );
}

/*********************************************************
function	add_to_opt_lp_of_son
Input:	Opt_lp_of_son list, new

	Put new at the beginning of list

Output:	new
*********************************************************/

Opt_lp_of_son	add_to_opt_lp_of_son(Opt_lp_of_son list, Opt_lp_of_son new)
{
	new->next = list;
	return( new );
}

/*********************************************************
function	new_lp_e_LP

	Create a new lp_of_son n

Output:	Pointer to n
*********************************************************/

Lp_of_son	new_lp_e_lp(void)
{
	int	i;
	Lp_of_son	new = (Lp_of_son)mymalloc( sizeof( struct lp_of_son));

	new->production		= NULL;
	new->costs		= 0;
	new->next		= NULL;
	new->LP_COSTS		= NULL;

	for( i=0; i <9; i++)
	{
		new->orientation_type_costs[i] = 0;
		new->orientation_type_set[i] = 0;
	}
	new->orientation_type_costs[9] = 0;

	return( new );
}

/*****************************************************
function	set_lp_set
Input:	Lp_of_father	head;

	Create head->lp_set
*****************************************************/

void	set_lp_set(Lp_of_father head)
{
	Lp_of_son	cur_lower;
	Opt_lp_of_son	result = NULL;

	for_lower_part( head->LP_set, cur_lower )
	{
		if(cur_lower->costs == head->optimal_costs)
			result = add_to_opt_lp_of_son( result, new_opt_lp_of_son(cur_lower) );
	}
	end_for_lower_part( head->LP_set, cur_lower );

	head->optimal_set = result;
}

/*******************************************************
function	add_to_lp_e_lp
Input:	Lp_of_son head, cur;

	Put cur at the beginning of head

Output: cur
*******************************************************/

Lp_of_son		add_to_lp_e_lp(Lp_of_son head, Lp_of_son cur)
{
	cur->next = head;
	return( cur );
}

/*******************************************************
function	new_multi_lp

	Create a new lp_of_father n

Output:	Pointer to n
*******************************************************/

Lp_of_father	new_multi_lp(void)
{
	Lp_of_father	new = (Lp_of_father)mymalloc( sizeof(struct lp_of_father));

	new->LP_set		= NULL;
	new->optimal_costs 	= 0;
	new->father		= NULL;
	new->son 		= NULL;
	new->next		= NULL;
	new->production 	= NULL;
	new->optimal_set	= NULL;

	return( new );
}

/******************************************************
function	add_to_multi_lp
Input:	Lp_of_father list, element

	Put element at the beginning of list;

Output:	element
******************************************************/

Lp_of_father	add_to_multi_lp(Lp_of_father list, Lp_of_father element)
{
	element->next = list;
	return( element );
}

/****************************************************************************
function:	edges_x_split
Input:	Tree_ref first_brother, Graph production

	let e1...en be the corresponding edges in production to the 
	brothers t1...tn of first_brother which represent edges 

Output:	sum of ei * ti (for i = 1..n)
****************************************************************************/

int	edges_x_split(tree_ref first_brother, Graph production)
{
	int		result = 0;
	tree_ref	cur;

	for_tree_rec( first_brother, cur )
	{
		if( cur->tree_rec_type == HISTORY_ELEM )
		{
			if( cur->tree_rec.history_elem->type == RHS_EDGE )
			{
				result += (lp_edgeline_length(corresponding_edge(cur, production)->lp_edge.lp_line) -2) 
					  * cur->tree_rec.history_elem->split_nr;
			}
		}
	}
	end_for_tree_rec( first_brother, cur );

	return( result );
}

/****************************************************************************
function:	corresponding_edge
Input:	tree_ref edge, Graph prod

	Find in prod the edge r which corresponds to edge

Output:	r
****************************************************************************/

Edge	corresponding_edge(tree_ref edge, Graph prod)
{
	Edge	cur_edge;

	for_edge_multi_suc( edge->tree_rec.history_elem->prod_iso, cur_edge)
	{
		if( cur_edge->target->graph == prod )
			return( cur_edge );
	}
	end_for_edge_multi_suc( edge->tree_rec.history_elem->prod_iso, cur_edge);
        return( NULL );
}

/********************************************************************
function:	corresponding_node
Input:	tree_ref node, Graph prod

	Find in prod the node n which corresponds to node

Output:	n
********************************************************************/

Node	corresponding_node(tree_ref node, Graph prod)
{
	Node	cur_node;

	for_node_multi_suc( node->tree_rec.node->prod_iso, cur_node )
	{
		if( cur_node->graph == prod ) 
			return cur_node;
	}
	end_for_node_multi_suc( node->tree_rec.node->prod_iso, cur_node );
	return NULL;
}

/*********************************************************
function:	new_lp_set
Input:	List_of_multi_lp target

	Create a new Lp_set n with target target

Output:	Pointer to n
*********************************************************/

Lp_set	new_lp_set(List_of_multi_lp target)
{
	Lp_set	new = (Lp_set) mymalloc( sizeof(struct lp_set) );

	new->target = target;
	new->next = NULL;

	return( new );
}

/*********************************************************
function:	add_to_lp_set
Input:	LP_set	list, elem;

	put elem at the beginning of list

Output:	elem
*********************************************************/

Lp_set	add_to_lp_set(Lp_set list, Lp_set elem)
{
	elem->next = list;
	return( elem );
}

/******************************************************
function	add_to_orientation_type_set
Input:	Lp_of_son	prod, int t

	append t to prod->orientation_type_set;
******************************************************/

void	add_to_orientation_type_set(Lp_of_son prod, int t)
{
	int i = 0;

	while( prod->orientation_type_set[i] != 0)
		i = i + 1;
	prod->orientation_type_set[i] = t;
}
/******************************************************
function	set_orientation_type_set
Input:	lp_of_son	head;

	set the orientation_type_set of head
******************************************************/

void	set_orientation_type_set(Lp_of_son head)
{
	int	t;

	for(t=1; t<9 ; t++)
	{
		if( head->lp_costs == head->orientation_type_costs[t])
			add_to_orientation_type_set(head, t);
	}
}


/*****************************************************
function	set_lp_costs
Input:	Lp_of_son prod

	Compute the lp_costs of prod
*****************************************************/

void	set_lp_costs(Lp_of_son prod)
{
	int m;
	int t;

 	m = prod->orientation_type_costs[1];
	for( t=2; t < 9; t++)
	{
		if(prod->orientation_type_costs[t] < m)
			m = prod->orientation_type_costs[t];
	}

	prod->lp_costs = m;
}

/*****************************************************
function	set_lp_set_costs
Input:	Lp_of_father	head;

	Compute head->lp_set_costs;
*****************************************************/

void	set_lp_set_costs(Lp_of_father head)
{
	int 	m = head->LP_set->lp_costs;
	Lp_of_son cur_prod = head->LP_set;

	while(cur_prod != NULL)
	{
		if (cur_prod->lp_costs < m)
			m = cur_prod->lp_costs;
		cur_prod = cur_prod->next;
	}

	head->lp_set_costs = m;
}

/********************************************************************
function:	set_LP_costs
Input:	tree_ref father

	compute LP_costs
********************************************************************/

void	set_LP_costs(tree_ref father)
{
	List_of_multi_lp	cur_LP_costs = father->tree_rec.node->LP_costs;
	Multi_edge		cur_list_elem;
	int			LP_costs;

	while( cur_LP_costs )
	{
		LP_costs = 0;
		cur_list_elem = cur_LP_costs->list;
		while( cur_list_elem )
		{
			LP_costs = LP_costs + cur_list_elem->lps_of_father->lp_set_costs;
			cur_list_elem = cur_list_elem->next;
		}
		cur_LP_costs->LP_costs = LP_costs;
		cur_LP_costs = cur_LP_costs->next;
	}
}

/**********************************************************************
function:	set_LHS_costs
Input:	tree_ref father

	Compute father->LHS_costs
**********************************************************************/

void	set_LHS_costs(tree_ref father)
{
	List_of_multi_lp	cur_elem = father->tree_rec.node->LP_costs;
	int			result = cur_elem->LP_COSTS;

	while( cur_elem )
	{
		if( cur_elem->LP_COSTS < result )
			result = cur_elem->LP_COSTS;
		cur_elem = cur_elem->next;
	}
	father->tree_rec.node->LHS_costs = result;
}

/***********************************************************************
function:	is_in_set_of
Input:	LP_set set, List_of_multi_lp to_find

Output:	TRUE iff. in set is an equal element to to_find
***********************************************************************/

int	is_in_set_of(Lp_set set, List_of_multi_lp to_find)
{
	while( set )
	{
		if( to_find->list->lps_of_father->production == set->target->list->lps_of_father->production )
			return( TRUE );
		set = set->next;
	}
	return( FALSE );
}

/***********************************************************************
function:	set_LP_set
Input:	tree_ref father;

	create father->LP_set
***********************************************************************/

void	set_LP_set(tree_ref father)
{
	Lp_set		 	result = NULL;
	List_of_multi_lp	elem = father->tree_rec.node->LP_costs;

	while( elem )
	{
	/*	if( father->father )
		{
			*//* falls ein father existiert, gibt es bei ihm ein LP_set. Dieses Set hat wieder
			   ein set und NUR aus diesem darf ausgewaehlt werden *//*
			if( (elem->LP_COSTS == father->tree_rec.node->LHS_costs) &&
			    is_in_set_of(father->father->tree_rec.node->LP_set, elem) )
			{
				result = add_to_lp_set( result, new_lp_set( elem ) );
			}
		}
		else */
		{
			if( elem->LP_COSTS == father->tree_rec.node->LHS_costs )
				result = add_to_lp_set( result, new_lp_set( elem ) );
		}
		elem = elem->next;
	}
	father->tree_rec.node->LP_set = result;
}

/************************************************************************
function:	set_lp_set_of_lp_of_son_one_level_higher
Input:	tree_ref	son

	create lps_of_son one level above in the derivation net 
	(considering level of son)
************************************************************************/

void	set_lp_set_of_lp_of_son_one_level_higher(tree_ref son)
{
	tree_ref		father = son->father;
	Multi_edge		multi_edges;
	Lp_of_father		lps_of_father;
	Lp_of_son		result = NULL;
	Lp_of_son		cur_lp_of_son;
	Lp_set			lp_set_of_son;

	if( father )
	{
		multi_edges = father->tree_rec.node->multi_edges;
		while( multi_edges )
		{
			lp_set_of_son = multi_edges->lps_of_father->son->tree_rec.node->LP_set;
			lps_of_father = multi_edges->lps_of_father;
			while( lps_of_father )
			{
				while( lp_set_of_son )
				{
					cur_lp_of_son = new_lp_e_lp();
					cur_lp_of_son->production = lp_set_of_son->target->list->lps_of_father->production;
					result = add_to_lp_e_lp( result, cur_lp_of_son );

					lp_set_of_son = lp_set_of_son->next;
				}
				lps_of_father->LP_set = result;
				result = NULL;
				lps_of_father = lps_of_father->next;
			}
			multi_edges = multi_edges->next;
		}
	}
}
