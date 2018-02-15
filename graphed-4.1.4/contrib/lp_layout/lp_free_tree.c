#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpm_remove_tree.h"

#include "lp_attribute_init_and_clear.h"
#include "lp_free_tree.h"

/********************************************************************************/
/*										*/
/*	modul	lp_free_tree.c							*/
/*										*/
/********************************************************************************/

/*********************************************************************************
function:	free_history_edges
Input:	history_edge_ref edge_list

	Free memory space of edge_list
*********************************************************************************/

void			free_history_edges	(history_edge_ref edge_list)
{
	history_edge_ref	cur_edge, del;

	if ( edge_list != NULL )
	{
		cur_edge = edge_list;
		do
		{
			del = cur_edge;
			cur_edge = cur_edge->in_suc;
			free( del );
		}
		while( cur_edge != edge_list );
	}
}

/*********************************************************************************
function:	free_tree_lp_edgeline
Input:	tree_lp_edgeline_ref list

	Free memory space of list
*********************************************************************************/

void			free_tree_lp_edgeline	(tree_lp_edgeline_ref list)
{
	tree_lp_edgeline_ref	cur, del;

	if ( list != NULL )
	{
		cur = list;
		do
		{
			del = cur;
			cur = cur->suc;
			free( del );
		}
		while( cur != list );
	}
}

/*********************************************************************************
function:	free_tree_top_sort
Input:	tree_top_sort_ref list

	Free memory space of list
*********************************************************************************/

void			free_tree_top_sort	(tree_top_sort_ref list)
{
	tree_top_sort_ref	cur = list;
	tree_top_sort_ref	del;

	while( cur != NULL )
	{
		del = cur;
		cur = cur->next_x;
		free( del );
	}
}

/*********************************************************************************
function:	free_tree_node
Input:	tree_ref tree

	Free memory space of tree and the corresponding topological sorting
*********************************************************************************/

void		free_tree_node(tree_ref tree)
{
	int	i;

	/****** Zeiger muessen nicht auf NULL gesetzt werden, da tree geloescht wird ******/
	/***** Jetzt alles was durch area-Optimierung da ist ******/
	if( tree->tree_rec.node->possible_productions )
	{
		free( tree->tree_rec.node->possible_productions->array_head );
		free( tree->tree_rec.node->possible_productions );
	}
	if( tree->tree_rec.node->possible_nodes )
	{
		free( tree->tree_rec.node->possible_nodes->array_head );
		free( tree->tree_rec.node->possible_nodes );
	}
	if( tree->tree_rec.node->area_structures )
	{
		for( i = 0; i < tree->tree_rec.node->area_structures->number; i++ )
		{
			free( tree->tree_rec.node->area_structures->array_head[i].lower_array );
		}
		free( tree->tree_rec.node->area_structures->array_head );
		free( tree->tree_rec.node->area_structures );
	}

	/****** Multi_lp ******/
	remove_multi_lp		( tree->tree_rec.node 		);
	remove_LP_costs		( tree->tree_rec.node->LP_costs );
	free_lp_set		( tree->tree_rec.node->LP_set 	);

	free			( tree->tree_rec.node 		);

	free_tree_top_sort	( tree->tree_rec.node->first_x 	);
	free			( tree 				);

}

/*********************************************************************************
function:	free_history_elem
Input:	tree_ref tree

	Free memory space of tree and the corresponding tree_line and in_edges
*********************************************************************************/

void		free_history_elem	(tree_ref tree)
{
	free_history_edges( tree->tree_rec.history_elem->in_edges );
	free_tree_lp_edgeline( tree->tree_rec.history_elem->tree_line );

	free( tree );
}
/*********************************************************************************
function:	free_derivation_net
Input:	tree_ref tree

	Free memory space of tree and the below part of tree in the derivation net
*********************************************************************************/

void		free_derivation_net	(tree_ref tree)
{
	tree_ref	cur = tree->tree_rec.node->first_son;
	tree_ref	next;

	while( cur != NULL )
	{
		next = cur->next_brother;
		if ( cur->tree_rec_type == TREE_NODE )
		{
			if ( cur->tree_rec.node->first_son != NULL )
			{
				free_derivation_net( cur );
			}
			else
			{
				free_tree_node( cur );
			}
		}
		else
		{
			free_history_elem( cur );
		}
		cur = next;
	}
	free_tree_node( tree );
}

/*********************************************************************************
function:	free_tree
Input:	tree_ref tree

	Free memory space of tree and the below part of tree in the derivation net
	and iff. tree has a father f change the pointers of f belonging to tree
	to NULL
*********************************************************************************/

void		free_tree	(tree_ref tree)
{
	tree_ref	cur;
	tree_ref	father;

	if( tree )
	{
		father = tree->father;

		clear_attributes( tree );
		free_derivation_net( tree );

		if ( father != NULL )
		{
			father->tree_rec.node->first_son = NULL;
			father->tree_rec.node->used_prod = NULL;
			father->tree_rec.node->first_x = NULL;
			father->tree_rec.node->first_y = NULL;

			/****** Hier muss man furchtbar aufpassen: Manche Algorithmen haben da Datenstrukturen, die es aber nicht mehr geben	******/
			/****** darf, da der Knoten nach unten nicht mehr weiter abgeleitet wurde. Ist dies der Fall, muessen die 		******/
			/****** Datenstrukturen geloescht werden. Dann muessen aber auch alle Zeiger, die von weiter oben auf diese Struktur 	******/
			/****** zeigen, auf NULL gesetzt werden (fuer alle Algorithmen, die eine Folge von 2 Ableitungsschritten betrachten)	******/
			/****** IDEE: Schaue die Strukturen vom Vater an. Immer wo Zeiger auf Strutur unten zeigt, loesche diese		******/
			/****** Das war die saubere Loesung: Die andere ist, dass man die Strukturen zwar loescht, aber die Zeiger von oben 	******/
			/****** nicht. Dann muss man aber jedesmal aufschauen, wenn man auf die Zeiger zugreift.				******/

			cur = father->tree_rec.node->first_son;	/****** DAS IST HIER SCHEISSE ******/
			while( cur != NULL )
			{
				if ( cur->tree_rec_type == HISTORY_ELEM )
				{
					cur->tree_rec.history_elem->out_edges = NULL;
				}
				cur = cur->next_brother;
			}
		}
	}
}

/*********************************************************************************
function:	free_tree_without_root
Input:	tree_ref root

	Free memory space of the below part p of root in the derivation net
	and change the pointers of root which belongs to p
*********************************************************************************/

void	free_tree_without_root	(tree_ref root)
{
	tree_ref	cur = root->tree_rec.node->first_son;
	tree_ref	next;
	tree_ref	father = root->father;

	clear_attributes( root );

	while( cur != NULL )
	{
		next = cur->next_brother;
		if ( cur->tree_rec_type == TREE_NODE )
		{
			if ( cur->tree_rec.node->first_son != NULL )
			{
				free_derivation_net( cur );
			}
			else	free_tree_node( cur );
		}
		else
		{
			free_history_elem( cur );
		}
		cur = next;
	}
	
	root->tree_rec.node->first_son = NULL;
	root->tree_rec.node->used_prod = NULL;
	root->tree_rec.node->first_x = NULL;
	root->tree_rec.node->first_y = NULL;

	if ( father != NULL )
	{
		cur = father->tree_rec.node->first_son;
		while( cur != NULL )
		{
			if ( cur->tree_rec_type == HISTORY_ELEM )
			{
				cur->tree_rec.history_elem->out_edges = NULL;
			}
			cur = cur->next_brother;
		}
	}
}
