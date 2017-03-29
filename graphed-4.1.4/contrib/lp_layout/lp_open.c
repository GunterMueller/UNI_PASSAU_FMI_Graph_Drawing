#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "user_header.h"

#include "lpa_redraw.h"
#include "lp_general_functions.h"
#include "lp_history.h"
#include "lp_edgeline.h"
#include "lp_open.h"

/************************************************************************/
/*									*/
/*		Oeffnen eines Knotens					*/
/*									*/
/************************************************************************/


/**************************************************************************************************
function	open_node
Input:	Node node

	Fuer einen reduced'en Knoten wird der darunterliegende Teilbaum wieder sichtbar gemacht.

Output:	---
**************************************************************************************************/


void	open_node (Node node)
{
	Group		copy_of_right_side;
	tree_ref	cur;
	Graph		prod = node->lp_node.tree_iso->tree_rec.node->used_prod;

	/****** Lass graphed die Arbeit machen. Tu einfach so, wie wenn ein Knoten mit einer Produktion abgeleitet wird ******/

	/* At first, copy the right side into graph		*/
	copy_of_right_side = copy_group_to_graph (prod->gra.gra.nce1.right_side, node->graph);

	cur = node->lp_node.tree_iso->tree_rec.node->first_son;

	while ( cur != NULL )		/****** Setze Zeiger von Baum auf Graph und umgekehrt ******/
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
			cur->tree_rec.node->graph_iso = cur->tree_rec.node->prod_iso->iso;
			cur->tree_rec.node->graph_iso->lp_node.tree_iso = cur;
			cur->tree_rec.node->graph_iso->iso->lp_node.tree_iso = cur;	
		}
		cur = cur->next_brother;
	}
		
	cur = node->lp_node.tree_iso->tree_rec.node->first_son;

	/****** Sorge dafuer, dass bei den Kanten alles stimmt ******/
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == HISTORY_ELEM )
		{
			if ( cur->tree_rec.history_elem->type == RHS_EDGE )
			{
				cur->tree_rec.history_elem->prod_iso->lp_edge.iso->lp_edge.tree_iso = cur;
			}
			cur->tree_rec.history_elem->prod_iso->lp_edge.tree_iso = cur;
		}
		cur = cur->next_brother;
	}

	create_personal_history_of_edge( copy_of_right_side );

	create_net_edges = FALSE;
	make_local_embedding (ENCE_1, node, prod);

	/****** Knoten wurde abgeleitet, also vergesse den Rest ******/
	node->lp_node.tree_iso->tree_rec.node->graph_iso = NULL;
	/* Now, we can delete the old node	*/
	erase_and_delete_node (node);
}

/**************************************************************************************************
function	open_tree_node
Input:	Node node

	Fuer einen reduced'en Knoten wird der darunterliegende Teilbaum wieder sichtbar gemacht.

Output:	---
**************************************************************************************************/

void    open_tree_node	(Node node)
{
	tree_ref	tree = node->lp_node.tree_iso;
	tree_ref	first = tree->tree_rec.node->first_son;

	open_node(node);

	while ( first )
	{
		if ( ( first->tree_rec_type == TREE_NODE ) &&				/****** Sorge dafuer, dass Knoten wieder sichtbar ist ******/
		     ( first->tree_rec.node->first_son != NULL ) &&
		     ( !first->tree_rec.node->leaf ) )
		{
			open_tree_node(first->tree_rec.node->graph_iso);
		}
		if ( ( first->tree_rec_type == TREE_NODE ) && 				/****** Wenn Knoten reduced ist, dann zeige ihn auch so an ******/
		     ( first->tree_rec.node->big ) )
		{
			char *label = (char *)malloc(1);

			label[0] = '\0';

			node_set( first->tree_rec.node->graph_iso, ONLY_SET,
				  NODE_LABEL, label, 0);
		}
		first = first->next_brother;
	}
}

/**************************************************************************************************
function	open_tree
Input:	---

	Sucht sich einen selektierten Knoten. Wenn der reduced ist, dann wird der darunterliegende
	Teilbaum wieder sichtbar gemacht

Output:	---
**************************************************************************************************/

void 	open_tree(void)
{
	Node	node = NULL;
	Graph	graph;
	int		result;
		
	if( something_picked )
	{ 
	   if( picked_object->what == NODE_PICKED ) 
	   {
	      node = picked_object->which.node;
           }
	   else
	   {
	      MsgBox( "No node picked.", CMD_OK );
	   }

	   if (node != NULL)
	   {
	      if ( node->graph->is_production )
	      {
	         MsgBox( "No hierarchies for productions.", CMD_OK );
	      }
	      else
	      {
	         if ( node->graph->lp_graph.derivation_net == NULL )
		 {
		    MsgBox( "No hierarchical graph.", CMD_OK );
		 }
		 else
		 {
	            if ( !node->lp_node.tree_iso->tree_rec.node->big )
		    {
		       MsgBox( "No reduced node.", CMD_OK );
		    }
		    else
		    {	
		       if( graph_state.lp_graph_state.LGG_Algorithm != TOP_DOWN_AREA_OPTIMIZATION_WITHOUT_EGDE_ROUTING )
		       {
			  MsgBox( "Only possible for top down area optimization.", CMD_OK );
			  dispatch_user_action (UNSELECT);
		       }
		       else
		       {
		          if( !node->graph->lp_graph.hierarchical_graph )
			  {
			     MsgBox( "This is no hierarchical graph. Only possible for a hierarchical graph.", CMD_OK );
			  }
			  else
			  {
			     if ( test_grammar_changed( node->graph->lp_graph.creation_time ) )
			     {
			         /* node->graph->lp_graph.hierarchical_graph = FALSE; */
			     }
			     else
			     {
			        if( lp_lgg_derivation_tests(node->graph) ) 
				{
			           if( node->graph->lp_graph.changed )
			           {
			              result =	notice_prompt (base_frame, NULL,		/*fisprompt*/
						NOTICE_MESSAGE_STRINGS,	"WARNING!\nGraph has been changed.\nCompute anyway?", NULL,
						NOTICE_BUTTON_YES,	"Yes",
						NOTICE_BUTTON_NO,	"No",
						NULL);

				      if( result == NOTICE_YES )
				      {
				         node->lp_node.tree_iso->tree_rec.node->leaf = 0;
				         node->lp_node.tree_iso->tree_rec.node->big = 0;
				         graph = node->graph;
				         open_tree_node(node);
	   			         lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph( graph );
				         restore_graph( graph );
				         graph->lp_graph.changed = FALSE;
				      }
			           }
			           else
			           {
				      node->lp_node.tree_iso->tree_rec.node->leaf = 0;
				      node->lp_node.tree_iso->tree_rec.node->big = 0;
				      graph = node->graph;
				      open_tree_node(node);
				      lp_create_optimal_graph_layout_for_derivation_tree_with_given_graph( graph );
				      restore_graph( graph );
				      graph->lp_graph.changed = FALSE;
				   }
				}
			     }
			  }
		       }
		    }
	         }
              }
	   }
           dispatch_user_action (UNSELECT);
	}
	else
	{
		MsgBox( "No Node picked.", CMD_OK );
	}
}
