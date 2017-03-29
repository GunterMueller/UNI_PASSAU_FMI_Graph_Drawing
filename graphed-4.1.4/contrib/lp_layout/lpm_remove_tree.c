#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_test.h"
#include "lpm_remove_tree.h"

/*******************************************************************************
function:	free_lp_set
Input:	Lp_set	cur

	Loescht Speicherplatz von ganzer Liste

Output:	---
*******************************************************************************/

void	free_lp_set(Lp_set cur)
{
	Lp_set	to_delete;

	while( cur )
	{
		to_delete = cur;
		cur = cur->next;
		free( to_delete );
	}
}

/*******************************************************************************
function:	remove_lp_of_son
Input:	Lp_of_son cur

	free_memory space of cur and all suc
*******************************************************************************/

void	remove_lp_of_son(Lp_of_son cur)
{
	Lp_of_son	to_delete;

	while( cur )
	{
		to_delete = cur;
		cur = cur->next;
		free( to_delete );
	}
}

/*******************************************************************************
function:	remove_multi_lp
Input:	tree_node_ref	father

	Free memory space of father->multi_edge
*******************************************************************************/

void	remove_multi_lp(tree_node_ref father)
{
	Multi_edge	cur = father->multi_edges;
	Multi_edge	to_delete;

	while( cur )
	{
		remove_lp_of_son( cur->lps_of_father->LP_set );
		to_delete = cur;
		cur = cur->next;
		free( to_delete->lps_of_father );
		free( to_delete );
	}

	father->multi_edges = NULL;
}

/*******************************************************************************
function:	remove_LP_costs
Input:	List_of_multi_lp

	Free memory space of all elements of LP_costs
*******************************************************************************/

void	remove_LP_costs(List_of_multi_lp head)
{
	Multi_edge		cur_list, to_delete;
	List_of_multi_lp	delete;

	while( head )
	{
		cur_list = head->list;
		while( cur_list )
		{
			to_delete = cur_list;
			cur_list = cur_list->next;
			free( to_delete );
		}
		delete = head;
		head = head->next;
		free( delete );
	}
}

/*******************************************************************************
function:	remove_tree_elements_for_multi
Input:	tree_ref father

	Free memory space of all Elements in the derivation net which belongs 
	to a multi production
*******************************************************************************/

void	remove_tree_elements_for_multi(tree_ref father)
{
	tree_ref	son = father->tree_rec.node->first_son;

	remove_multi_lp	( father->tree_rec.node 		);
	remove_LP_costs	( father->tree_rec.node->LP_costs 	);
	free_lp_set	( father->tree_rec.node->LP_set 	);

	father->tree_rec.node->LP_costs		= NULL;
	father->tree_rec.node->multi_edges	= NULL;
	father->tree_rec.node->LP_set		= NULL;

	while( son )
	{
		son->multi_edge = NULL;

		if( son->tree_rec_type == TREE_NODE )
		{
			remove_tree_elements_for_multi( son );
		}

		son = son->next_brother;
	}
}
