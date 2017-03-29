#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lp_attribute_init_and_clear.h"
#include "lp_general_functions.h"
#include "lp_draw.h"
#include "lp_1_pass.h"
#include "lp_3_pass.h"
#include "lp_4_pass.h"
#include "lp_5_pass.h"
#include "lp_6_pass.h"
#include "lp_7_pass.h"
#include "lpm_2_pass.h"
#include "lpm_3_pass.h"
#include "lpm_4_pass.h" 
#include "lpm_history.h"
#include "lpm_remove_tree.h"
#include "lpm_iso_test.h"
#include "lpm_create_lgg.h"

/************************************************************************/
/*									*/
/*	modul	lp_create_lgg.c						*/
/*									*/
/************************************************************************/

/*****************************************************************************
function	create_multi_lgg_layout

	create the graph for a multi lgg
*****************************************************************************/

void	create_multi_lgg_layout(Graph graph)
{
	current_size	= graph->lp_graph.current_size;		/*** GLOBALE VARIABLE ***/

	clear_attributes		( graph->lp_graph.derivation_net 	);
	clear_lp_edgelines		( graph 				);
	remove_tree_elements_for_multi	( graph->lp_graph.derivation_net 	);

	create_elements_for_multi_lgg	( graph->lp_graph.derivation_net	);
	pass_1				( graph->lp_graph.derivation_net 	);
	pass_2_multi			( graph->lp_graph.derivation_net 	); 
	pass_2a_multi			( graph->lp_graph.derivation_net 	); 
	pass_2b_multi			( graph->lp_graph.derivation_net	); 
	pass_3				( graph->lp_graph.derivation_net 	);
	pass_4				( graph->lp_graph.derivation_net 	);
	pass_5				( graph->lp_graph.derivation_net 	);

	graph->lp_graph.derivation_net->tree_rec.node->x1 = 0;
	graph->lp_graph.derivation_net->tree_rec.node->x2 = graph->lp_graph.derivation_net->tree_rec.node->w;
	graph->lp_graph.derivation_net->tree_rec.node->y1 = 0;
	graph->lp_graph.derivation_net->tree_rec.node->y2 = graph->lp_graph.derivation_net->tree_rec.node->h;

	pass_6				( graph->lp_graph.derivation_net	);
	pass_7				( graph					);
/*	restore_graph			( graph					);	*/
}

/************************************************************************
function:	create_multi_lgg

	Find the selected graph and create a multi lgg of it
************************************************************************/

void	create_multi_lgg(void)
{
	Graph	graph = empty_graph;

	graph = compute_graph();

	if( graph )
	{
		if( !graph->lp_graph.derivation_net )
		{
			MsgBox( "No hierarchical graph.", CMD_OK );
		}
		else
		{
			create_multi_lgg_layout( graph );
		}
	}
}

