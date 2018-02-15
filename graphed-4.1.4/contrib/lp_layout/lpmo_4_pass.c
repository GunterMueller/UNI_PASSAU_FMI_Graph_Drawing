#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_tree_top_sort.h"
#include "lp_2_pass.h"
#include "lpm_multi_functions.h"
#include "lpmo_4_pass.h"

/********************************************************************
function:	concatination_of_orientation_types
Input:	tree_ref	father, son

Output:	Optimal orientation of son by considering orientation of father
********************************************************************/

int	concatination_of_orientation_types(int orientation_of_father, int orientation_of_son)
{

	int	result;

	/* Beide Orientierungen sind nur Drehungen gegen den Uhrzeigersinn	*/

	if( (orientation_of_son < 5) && (orientation_of_father < 5) )
	{
		result = (orientation_of_son + orientation_of_father - 1) % 4;
		if( result == 0 ) 
			result = 4;
		return( result );
	}

	if( orientation_of_father == 1 )
		return( orientation_of_son );

	if( orientation_of_son == 1 )
		return( orientation_of_father );

	/* remember: this is only executed when reflect */
	if( orientation_of_son == orientation_of_father )
		return( 1 );

	switch( orientation_of_father )
	{
		case 2:
			switch( orientation_of_son )
			{
				case 5:	return( 8 );
				case 6: return( 7 );
				case 7: return( 5 );
				case 8: return( 6 );
			}
		case 3:
			switch( orientation_of_son )
			{
				case 5: return( 6 );
				case 6: return( 5 );
				case 7: return( 8 );
				case 8: return( 7 );
			}
		case 4:
			switch( orientation_of_son )
			{
				case 5: return( 7 );
				case 6: return( 8 );
				case 7: return( 6 );
				case 8: return( 5 );
			}

		case 5:
			switch( orientation_of_son )
			{
				case 2: return( 8 );
				case 3: return( 6 );
				case 4: return( 7 );
				case 6: return( 3 );
				case 7: return( 2 );
				case 8: return( 4 );
			}
		case 6:
			switch( orientation_of_son )
			{
				case 2: return( 8 );
				case 3: return( 5 );
				case 4: return( 7 );
				case 5: return( 3 );
				case 7: return( 4 );
				case 8: return( 2 );
			}
		case 7:
			switch( orientation_of_son )
			{
				case 2: return( 6 );
				case 3: return( 8 );
				case 4: return( 5 );
				case 5: return( 4 );
				case 6: return( 2 );
				case 8: return( 3 );
			}
		case 8:
			switch( orientation_of_son )
			{
				case 2: return( 6 );
				case 3: return( 7 );
				case 4: return( 5 );
				case 5: return( 2 );
				case 6: return( 4 );
				case 7: return( 3 );
			}
	}
	return 0; /* should not be reached */
}

/********************************************************************
function:	pass_2b_multi
Input:	tree_ref	father

	Compute in a top-down traversal the attributes
		* LP_set
		* splitting factor
		* opt_lp
		* orientation type
		* source- and target-dir
********************************************************************/
void 	pass_2b_multi_oriented	(tree_ref father)
{
	tree_ref		cur;
	tree_ref		cur_tree_son;
	history_edge_ref	first_net_edge, cur_net_edge;
	int			sum;
	int			dir;
	List_of_multi_lp	opt_lp, cur_lp;
	Multi_edge		cur_multi;
	Node			node;
	Edge			edge;

	cur = father;
	if( cur->tree_rec_type == TREE_NODE )
	{
		if( !cur->tree_rec.node->leaf )
		{
			/****************************************************/
			/* opt_lp					    */
			/****************************************************/

			/* Existiert ein multi- Ableitungsnetz ? */
			if( cur->tree_rec.node->LP_costs != NULL )
			{
				/* Bestimme optimal Production fuer Wurzel */
				if( !cur->father )
				{
					opt_lp = cur->tree_rec.node->LP_costs;

					for_lp_costs( cur->tree_rec.node->LP_costs, cur_lp )
					{
						if( cur_lp->OPTIMAL_COSTS < opt_lp->OPTIMAL_COSTS )
							opt_lp = cur_lp;
					}
					end_for_lp_costs( cur->tree_rec.node->LP_costs, cur_lp );

					cur->tree_rec.node->used_prod = opt_lp->list->lps_of_father->production;
				}
				/* Suche optimale Prod fuer inneren Knoten heraus um abgel. Soehnen ihre Prod zuzuweisen */
				else
				{
					for_lp_costs( cur->tree_rec.node->LP_costs, cur_lp )
					{
						if( cur->tree_rec.node->used_prod == cur_lp->list->lps_of_father->production )
							opt_lp = cur_lp;
					}
					end_for_lp_costs( cur->tree_rec.node->LP_costs, cur_lp );
				}

				/* Weise abgeleiteten Soehnen ihre Prod zu */
				for_multi_edge( opt_lp->list, cur_multi )
				{
					if( cur_multi->lps_of_father->optimal_set != NULL )
					{
						/* remember: since now used prod is the optimal production */
						cur_multi->lps_of_father->son->tree_rec.node->used_prod = 
							cur_multi->lps_of_father->optimal_set->target->production;
						cur_multi->lps_of_father->son->tree_rec.node->orientation_type = 
							concatination_of_orientation_types(
								cur_multi->lps_of_father->father->orientation_type,
								cur_multi->lps_of_father->optimal_set->target->orientation_type_set[0] );
					}

				}
				end_for_multi_edge( opt_lp->list, cur_multi );
			}

			/****************************************************************/
			/* eintragen von prod_iso von der_net-Knoten auf Produktion	*/
			/****************************************************************/

			cur_tree_son = father->tree_rec.node->first_son;
			while( cur_tree_son )
			{
				if( cur_tree_son->tree_rec_type == TREE_NODE )
				{
					node = corresponding_node( cur_tree_son, cur->tree_rec.node->used_prod );
					cur_tree_son->tree_rec.node->prod_iso = node;
					node->lp_node.tree_iso = cur_tree_son;
				}
				else 
				{
					edge = corresponding_edge( cur_tree_son, cur->tree_rec.node->used_prod );
					cur_tree_son->tree_rec.history_elem->prod_iso = edge;
					edge->lp_edge.tree_iso = cur_tree_son;
				}

				cur_tree_son = cur_tree_son->next_brother;
			}

			/********************************************************/
			/* Eintragen von tree_lp_edgelines			*/
			/********************************************************/
			copy_lp_edgelines_to_tree( cur->tree_rec.node->used_prod );

			/********************************************************/
			/* Sortierung in x und y Richtung in der_net eintragen	*/
			/********************************************************/
			copy_topological_sorting_to_tree( father );
		}
	}

	/************************************************/
	/* source_dir, target_dir			*/
	/************************************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur->tree_rec.history_elem->source_dir =
			T_dir( first_dir_tree(cur), father->tree_rec.node->orientation_type);

			cur->tree_rec.history_elem->target_dir =
			T_dir(last_dir_tree(cur), father->tree_rec.node->orientation_type);
		}
		cur = cur->next_brother;
	}

	/*******************************/
	/* Attribute: splitting factor */
	/*******************************/

	cur = father->tree_rec.node->first_son;
	while( cur )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			for( dir = 0; dir<=3; dir++)
			{
				sum = 0;
				first_net_edge = cur->tree_rec.history_elem->in_edges;
				if ( first_net_edge != NULL )
				{
					cur_net_edge = first_net_edge;
					do
					{
						if( cur_net_edge->target->tree_rec.history_elem->type == OUT_CONN_REL)
						{
							if ( cur_net_edge->source->tree_rec.history_elem->source_dir == dir )
								sum+= cur->tree_rec.history_elem->split_nr *
						 	 	    cur_net_edge->edge_split_nr;
						}
						else
						{
							if ( cur_net_edge->source->tree_rec.history_elem->target_dir == dir )
								sum+= cur->tree_rec.history_elem->split_nr *
						 			    cur_net_edge->edge_split_nr;
						}
						cur_net_edge = cur_net_edge->in_suc;
					}
					while ( first_net_edge != cur_net_edge );
				}
				cur->tree_rec.history_elem->split[dir] = sum;
			}
		}
		cur = cur->next_brother;
	}


	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) )
		{
			pass_2b_multi_oriented( cur );
		}
		cur = cur->next_brother;
	}
}
