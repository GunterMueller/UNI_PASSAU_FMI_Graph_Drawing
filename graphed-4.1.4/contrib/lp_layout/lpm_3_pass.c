#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lpm_multi_functions.h"
#include "lp_test.h"

/****************************************************************************
function	OPTIMAL
Input:	Lp_of_son cur

Output: iff. cur->LP_COSTS != NULL: cur->LP_COSTS->LP_COSTS
	otherwise: 0;
****************************************************************************/

int	OPTIMAL(Lp_of_son cur)
{
	if( cur->LP_COSTS != NULL )
	{
		return( cur->LP_COSTS->OPTIMAL_COSTS );
	}
	return( 0 );
}

/****************************************************************************
function:	pass_2a_multi
Input:	tree_ref father

	Compute the attributes
		* costs
		* optimal_costs
		* optimal_set
		* OPTIMAL_COSTS
	in a bottom-up traversal
****************************************************************************/

void 	pass_2a_multi	(tree_ref father)
{
	tree_ref		cur = father->tree_rec.node->first_son;
	tree_ref		cur_son;
	int			sum, cur_minimal_costs;
	Multi_edge		multi_list;
	Lp_of_father		cur_upper;
	Lp_of_son		cur_lower;
	history_edge_ref	cur_net_edge;
	List_of_multi_lp	LP_costs;

	while ( cur )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) )
		{
			pass_2a_multi( cur );
		}
		cur = cur->next_brother;
	}

	/****************************************/
	/*	costs				*/
	/****************************************/

	cur = father;
	for_multi_edge( cur->tree_rec.node->multi_edges, multi_list )
	{
	    for_upper_part( multi_list->lps_of_father, cur_upper )
	    {
		cur_minimal_costs = UNDEFINED;
		for_lower_part( cur_upper->LP_set, cur_lower )
		{
		    sum = 0;
		    for_tree_rec( cur_upper->son->tree_rec.node->first_son, cur_son )
		    {
			if( cur_son->tree_rec_type == HISTORY_ELEM )
			{
				if( cur_son->tree_rec.history_elem->type == RHS_EDGE )
				{
					sum += (lp_edgeline_length(corresponding_edge(cur_son, cur_lower->production)->lp_edge.lp_line)-2 ) * 
					       cur_son->tree_rec.history_elem->split_nr;
				}

				for_net_in_edges( cur_son->tree_rec.history_elem->in_edges, cur_net_edge )
				{	
					if( cur_net_edge->target->tree_rec.history_elem->type == IN_CONN_REL )
					{
						sum +=  cur_net_edge->target->tree_rec.history_elem->split_nr *	
							cur_net_edge->edge_split_nr * 
							Xi(last_dir(corresponding_edge(cur_net_edge->source, cur_upper->production)),
							  first_dir(corresponding_edge(cur_net_edge->target, cur_lower->production)) );
					}
					else
					{
						sum +=  cur_net_edge->target->tree_rec.history_elem->split_nr *	
							cur_net_edge->edge_split_nr * 
							Xi(first_dir(corresponding_edge(cur_net_edge->source, cur_upper->production)),
							  last_dir(corresponding_edge(cur_net_edge->target, cur_lower->production)) );
					 }
					 sum += (lp_edgeline_length(corresponding_edge(
                                                            cur_net_edge->target, cur_lower->production)->lp_edge.lp_line)-2 ) * 
						   cur_net_edge->target->tree_rec.history_elem->split_nr * cur_net_edge->edge_split_nr;
				}
				end_for_net_in_edges( cur_son->tree_rec.history_elem->in_edges, cur_net_edge );
			}
		    }
		    end_for_tree_rec( cur_upper->son->tree_rec.node->first_son, cur_son );
		    sum += OPTIMAL( cur_lower );
		    cur_lower->costs = sum;

		    if( cur_minimal_costs == UNDEFINED ) cur_minimal_costs = sum;
		    else if( cur_minimal_costs > sum ) cur_minimal_costs = sum;
		}
		end_for_lower_part( cur_upper->LP_set, cur_lower );

		/****************************************/
		/*	optimal_costs			*/
		/****************************************/
		cur_upper->optimal_costs = cur_minimal_costs;

		/****************************************/
		/*	optimal_set			*/
		/****************************************/
		set_lp_set( cur_upper );
	    }
	    end_for_upper_part( multi_list->lps_of_father, cur_upper );
	}
	end_for_multi_edge( cur->tree_rec.node->multi_edges, multi_list );

	/****************************************/
	/*	OPTIMAL_COSTS			*/
	/****************************************/

	cur = father;
	for_lp_costs( cur->tree_rec.node->LP_costs, LP_costs )
	{
		sum = 0;

		for_multi_edge( LP_costs->list, multi_list )
		{
			sum += multi_list->lps_of_father->optimal_costs;

			if( cur->father == NULL )
			{
				sum += edges_x_split( cur->tree_rec.node->first_son, multi_list->lps_of_father->production );
			}
		}
		end_for_multi_edge( LP_costs->list, multi_list );

		if( cur->father == NULL )
		{
			sum += edges_x_split( cur->tree_rec.node->first_son, LP_costs->list->lps_of_father->production );
		}

		LP_costs->OPTIMAL_COSTS = sum;
	}
	end_for_lp_costs( cur->tree_rec.node->LP_costs, LP_costs );
}
