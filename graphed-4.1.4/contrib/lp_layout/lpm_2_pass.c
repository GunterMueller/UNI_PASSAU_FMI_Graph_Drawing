#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_edgeline.h"
#include "lpm_2_pass.h"

/***************************************************************************
function:	pass_2_multi
Input:	tree_ref father

	Compute the attributes
		* edge splitting number			Kap. 4.3.1
	in a top-down traversal
***************************************************************************/

void 	pass_2_multi	(tree_ref father)
{
	tree_ref		cur = father->tree_rec.node->first_son;
	history_edge_ref	first_net_edge, cur_net_edge;
	int			sum;

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

	cur = father->tree_rec.node->first_son;

	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) )
		{
			pass_2_multi( cur );
		}
		cur = cur->next_brother;
	}
}
