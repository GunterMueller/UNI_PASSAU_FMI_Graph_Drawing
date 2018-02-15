#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_test.h"
#include "lpm_multi_functions.h"
#include "lpmo_3_pass.h"

/****************************************************************************
function	OPT
Input:	Lp_of_son cur

Output: iff. cur->LP_COSTS != NULL: cur->LP_COSTS->LP_COSTS
	otherwise: 0;
****************************************************************************/

int	OPT(Lp_of_son cur)
{
	if( cur->LP_COSTS != NULL )
		return( cur->LP_COSTS->OPTIMAL_COSTS );
	return( 0 );
}

/****************************************************************************
function:	pass_2a_multi
Input:	tree_ref father

	Compute the attributes
		* orientation_type_costs
		* lp_costs
		* orientation_type_set
		* lp_set_costs
		* lp_set
		* min_lp_set_costs
		* LP_costs
		* LP_COSTS
		* LHS_costs
	in a bottom-up traversal
****************************************************************************/

void 	pass_2a_multi_oriented	(tree_ref father)
{
	tree_ref		cur = father->tree_rec.node->first_son;
	tree_ref		cur_son;
	int			i, sum;
	int			m, cur_minimal_costs;
	Multi_edge		multi_list;
	Lp_of_father		cur_upper;
	Lp_of_son		cur_production;
	history_edge_ref	cur_net_edge;
	List_of_multi_lp	LP_costs;

	while ( cur )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) )
		{
			pass_2a_multi_oriented( cur );
		}
		cur = cur->next_brother;
	}
	/****************************************/
	/*	orientation_type_costs		*/
	/****************************************/

	cur = father;
	for_multi_edge( cur->tree_rec.node->multi_edges, multi_list )
	{
		for_upper_part( multi_list->lps_of_father, cur_upper )
		{
		    for_lower_part( cur_upper->LP_set, cur_production )
		    {
			for(i=1; i<=8; i++)
			{
			    sum = 0;
			    for_tree_rec( cur_upper->son->tree_rec.node->first_son, cur_son )
			    {
			        if(cur_son->tree_rec_type == HISTORY_ELEM)
			        {
				    if( cur_son->tree_rec.history_elem->type == RHS_EDGE )
				    {
					sum += (lp_edgeline_length(corresponding_edge(cur_son, cur_production->production)->lp_edge.lp_line)-2 ) * 
						   cur_son->tree_rec.history_elem->split_nr;
				    } 
  
				    for_net_in_edges( cur_son->tree_rec.history_elem->in_edges, cur_net_edge )
				    {	
					    if( cur_net_edge->target->tree_rec.history_elem->type == IN_CONN_REL )
					    {
						sum +=  cur_net_edge->target->tree_rec.history_elem->split_nr *	
							cur_net_edge->edge_split_nr * 
							Xi(last_dir(corresponding_edge(cur_net_edge->source, cur_upper->production)),
							  T_dir(first_dir(corresponding_edge(
									cur_net_edge->target, cur_production->production)), i) );
					    }
					    else
					    {
						sum +=  cur_net_edge->target->tree_rec.history_elem->split_nr *	
							cur_net_edge->edge_split_nr * 
							Xi(first_dir(corresponding_edge(cur_net_edge->source, cur_upper->production)),
							  T_dir(last_dir(corresponding_edge(
									cur_net_edge->target, cur_production->production)), i) );
					    }
					    sum += (lp_edgeline_length(corresponding_edge(
                                                            cur_net_edge->target, cur_production->production)->lp_edge.lp_line)-2 ) * 
						   cur_net_edge->target->tree_rec.history_elem->split_nr * cur_net_edge->edge_split_nr;
				    }
				    end_for_net_in_edges( cur_son->tree_rec.history_elem->in_edges, cur_net_edge );
				}
			    }
			    end_for_tree_rec( cur_upper->son->tree_rec.node->first_son, cur_son );
			    cur_production->orientation_type_costs[i] = sum;
			}
		    }
		    end_for_lower_part( cur_upper->LP_set, cur_production );
		}
		end_for_upper_part( multi_list->lps_of_father, cur_upper );
	}
	end_for_multi_edge( cur->tree_rec.node->multi_edges, multi_list );

	/********************************/
	/*	costs			*/
	/*	orientation_type_set	*/
	/*	optimal_set		*/
	/********************************/
	
	for_multi_edge( cur->tree_rec.node->multi_edges, multi_list )
	{
		for_upper_part( multi_list->lps_of_father, cur_upper )
		{
			cur_minimal_costs = UNDEFINED;
			for_lower_part( cur_upper->LP_set, cur_production )
			{
				m = UNDEFINED;
				for(i=1; i<=8; i++)
				{
					if( (cur_production->orientation_type_costs[i] < m) || (m == UNDEFINED) ) 
						m = cur_production->orientation_type_costs[i];
				}
				cur_production->lp_costs = m;
				set_orientation_type_set( cur_production );
				m += OPT( cur_production );
				cur_production->costs = m;
				if( (cur_minimal_costs == UNDEFINED) || (m < cur_minimal_costs) )
					cur_minimal_costs = m;
			}
			end_for_lower_part( cur_upper->LP_set, cur_production );
			cur_upper->optimal_costs = cur_minimal_costs;
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
