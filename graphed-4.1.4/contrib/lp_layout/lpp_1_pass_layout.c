#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpp_1_pass_layout.h"

#include "lp_2_pass.h"
#include "lp_tree_top_sort.h"

/********************************************************************
function:	lpp_1_pass_layout
Input:	tree_ref	father

	Compute in a top-down traversal the attributes
		* source- and target-dir
********************************************************************/
void 	lpp_1_pass_layout	(tree_ref father)
{
	tree_ref		cur;

	/************************************************/
	/* source_dir, target_dir			*/
	/************************************************/

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			cur->tree_rec.history_elem->source_dir =
			first_dir_tree(cur);

			cur->tree_rec.history_elem->target_dir =
			last_dir_tree(cur);
		}
		else
		{
			if( cur->tree_rec.node->used_prod )
			{
				copy_lp_edgelines_to_tree( cur->tree_rec.node->used_prod );
			}
		}
		cur = cur->next_brother;
	}

	cur = father->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
		     ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf ) ) lpp_1_pass_layout( cur );
		cur = cur->next_brother;
	}
}
