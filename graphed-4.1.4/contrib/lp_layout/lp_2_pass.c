#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lp_2_pass.h"
#include "lp_graph.h"

/***************************************************************************
function:	first_dir_tree
Input:	tree_ref tree

Output: first direction of the edge in the production which is corresponding 
	to tree
***************************************************************************/

int	first_dir_tree	(tree_ref tree)
{
	return( first_dir(tree->tree_rec.history_elem->prod_iso ) );
}

/***************************************************************************
function:	last_dir_tree
Input:	tree_ref tree

Output:	last direction of the edge in the production which is corresponding 
	to tree
***************************************************************************/

int	last_dir_tree	(tree_ref tree)
{
	return( last_dir(tree->tree_rec.history_elem->prod_iso ) );
}

/***************************************************************************
function:	pass_2
Input:	tree_ref father

	Compute the attributes
		* edge splitting number			Kap. 4.3.1
		* splitting factor			Kap. 4.3.5
		* orientation type costs		Kap. 4.3.7
		* orientation type			Kap. 4.3.8
		* source direction, target direction	Kap. 4.3.3
	in a top-down traversal
***************************************************************************/

void 	pass_2	(tree_ref father)
{
	tree_ref		cur = father->tree_rec.node->first_son;
	history_edge_ref	first_net_edge, cur_net_edge;
	int			sum;
	int			dir;
	int			t;
	int			best_type;
	int			cur_minimum;
	int			actual_costs;
	tree_edge_ref		cur_tree_edge;

	/************************************/
	/* Attribute: edge splitting number */
	/************************************/
	/* Identisch fuer in + out_emb	    */

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			sum = 0;
			first_net_edge = cur->tree_rec.history_elem->in_edges;
			if ( first_net_edge != NULL )
			{
				cur_net_edge = first_net_edge;
				do
				{
					sum+= cur_net_edge->edge_split_nr;
					cur_net_edge = cur_net_edge->in_suc;
				}
				while ( cur_net_edge != first_net_edge );
			}
			else	sum = 1;


			first_net_edge = cur->tree_rec.history_elem->out_edges;
			if ( first_net_edge != NULL )
			{
				cur_net_edge = first_net_edge;
				do
				{
					cur_net_edge->edge_split_nr = sum;
					cur_net_edge = cur_net_edge->out_suc;
				}			
				while ( first_net_edge != cur_net_edge );
			}
		}
		cur = cur->next_brother;
	}

	/*******************************/
	/* Attribute: splitting factor */
	/*******************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
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

	/*************************************/
	/* Attribute: orientation_type_costs */
	/*************************************/

	for( t = 1; t <= 8; t++)
	{
		sum = 0;
		cur = father->tree_rec.node->first_son;
		while ( cur != NULL )
		{
			if ( cur->tree_rec_type == HISTORY_ELEM )
			{
				/* berechnen fuer in_embeddings */
				/* und fuer out_embeddings */
				if(cur->tree_rec.history_elem->type == IN_CONN_REL)
				{
					cur_tree_edge = cur->tree_rec.history_elem;
					for( dir = 0; dir <= 3; dir++ )
					{
						sum+= 
						cur_tree_edge->split[dir] * Xi(dir, T_dir(first_dir_tree(cur), t ));
					}
				}
				if(cur->tree_rec.history_elem->type == OUT_CONN_REL) 
				{
					cur_tree_edge = cur->tree_rec.history_elem;
					for( dir = 0; dir <= 3; dir++ )
					{
						sum+= 
						cur_tree_edge->split[dir] * Xi(dir, T_dir(last_dir_tree(cur), t ));
					}
				}
			}
			cur = cur->next_brother;
		}
		father->tree_rec.node->orientation_type_costs[t] = sum;
	}

	/*******************************/
	/* Attribute: orientation_type */
	/*******************************/


	best_type = 1;

	/*** Beim Nochmal zeichnen mit derivation-Algorithmus darf nicht orientiert werden		***/
	if( !create_by_derivation )
	{
		cur_minimum = father->tree_rec.node->orientation_type_costs[1];
		for( t = 2; t <= 8; t++)
		{
			actual_costs = father->tree_rec.node->orientation_type_costs[t];
			if ( actual_costs < cur_minimum )
			{	
				cur_minimum = actual_costs;
				best_type = t;
			}
		}
	}

	father->tree_rec.node->orientation_type = best_type;

	/****************************************************/
	/* Attributes: source directions, target directions */
	/****************************************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur->tree_rec.history_elem->source_dir =
			T_dir( first_dir_tree(cur), best_type );

			cur->tree_rec.history_elem->target_dir =
			T_dir( last_dir_tree(cur), best_type );
		}
		cur = cur->next_brother;
	}

	cur = father->tree_rec.node->first_son;

	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) ) pass_2( cur );
		cur = cur->next_brother;
	}
}
