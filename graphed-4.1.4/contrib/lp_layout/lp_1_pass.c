#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lp_1_pass.h"
#include "lp_graph.h"

/************************************************************************
function:	pass_1	
Input:	tree_ref father

	Compute recursively the attribute split_nr in a bottom up traversal
	See:	4.2
************************************************************************/
/***************** kein Unterschied fuer in und out ********************/

void 	pass_1	(tree_ref father)
{
	tree_ref		cur;
	history_edge_ref	first_net_edge, cur_net_edge;
	int			sum;

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
	             ( cur->tree_rec.node->first_son != NULL ) && 
		     ( !cur->tree_rec.node->leaf ) ) pass_1( cur );
		cur = cur->next_brother;
	}

	cur = father->tree_rec.node->first_son;

	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			sum = 0;
			first_net_edge = cur->tree_rec.history_elem->out_edges;
			if ( first_net_edge != NULL )
			{
				cur_net_edge = first_net_edge;
				do
				{
					sum+= cur_net_edge->target->tree_rec.history_elem->split_nr;
					cur_net_edge = cur_net_edge->out_suc;
				}
				while ( cur_net_edge != first_net_edge );
				cur->tree_rec.history_elem->split_nr = sum;
			}
			else
			{
				cur->tree_rec.history_elem->split_nr = 1;
			}
		}
		cur = cur->next_brother;
	}
}
