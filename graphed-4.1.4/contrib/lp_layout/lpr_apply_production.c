/************************************************************************************************/
/*																*/
/*	File fuer die Funktion, die in apply_produktion aufgerufen wird und dann den LRS-Graph	*/
/*	erzeugt														*/
/*																*/
/************************************************************************************************/


#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <sys/syscall.h>
#include <ctype.h>

#include "lpr_nnode.h"
#include "lpr_ggraph.h"
#include "lpr_optimal_layout.h"
#include "lpr_lrp.h"
#include "lpr_apply_production.h"
#include "lpr_glr_system.h"
#include "lpr_compute_pos_ass.h"
#include "lpr_compute_layout.h"
 


/*************************************************************************************************
function:	lpr_apply_production
Input:	Graph applied_prod, Node derivated_node

	Erzeugt lpr_graph von Prod und haengt sie an derivated_node
*************************************************************************************************/

lpr_Node	lpr_apply_production(Graph applied_prod, Node derivated_node)
{
	lpr_Node	new_node;
	lpr_Node	head;

	if( !derivated_node->lp_node.corresponding_lpr_node )
	{
		/****** Es wurde die erste Produktion angewandt, es existiert noch kein lpr_Graph	******/
		new_node = create_lpr_node();
		lpr_node_SET_LABEL		( new_node, derivated_node->label.text	);
		lpr_node_SET_GRAPH_ISO		( new_node, derivated_node		);

		derivated_node->graph->lp_graph.LRS_graph = new_node;

		head = new_node;
	}
	else
	{
		head = derivated_node->lp_node.corresponding_lpr_node;
	}

	head->leaf = FALSE;

	lpr_node_SET_IS_NON_TERMINAL	( head									);
	lpr_node_SET_APPLIED_PRODUCTION	( head, lpr_graph_create_copy_of_graphed_prod(applied_prod, head)	);
	lpr_node_SET_NODETYPE		( head, lpr_LHS_NODE							);
	lpr_node_SET_GRAPH_ISO		( head, NULL								);

	return	head;
}

