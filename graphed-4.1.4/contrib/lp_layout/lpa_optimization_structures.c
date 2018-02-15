#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include <sgraph/sgraph.h>
#include <sgraph/graphed.h>

#include "lp_general_functions.h"

#include "lpa_create_struc.h"

#include "lpa_optimization_structures.h"


/*****************************************************************************************
function:	create_lpa_x_dependency_from_prod
Input:	Graph prod, tree_ref tree_father, int offset

	Erzeugt eine LPA_dependency (x_abhaengigkeit) d von prod. 

	tree_father ist dabei der Knoten im Ableitungsbaum auf den prod angewendet wurde => 
	Es werden Zeiger von der LPA_Dependency auf seine Soehne im Ableitungsbaum gelegt.
	BEACHTE DABEI: Es gibt i.a. mehrere isomorphe Produktionen. Es muessen also vorher
		bei den Soehnen die Array`s von Knoten angelegt worden sein.

	Offset gibt an, wo im array der Knoten gesucht werden muss.
	Aufpassen: Ziehe immer Rand ab !!!
	Idee:
	1. Hole aus prod LHS_Node. Fuege in d ein und merke die Koordinaten.
	2. Durchlaufe Soehne von tree_father und fuege alle Knoten ein, auf die 
	   node_array[offset] vom aktuellen Sohn zeigt.

Output:	d
*****************************************************************************************/

LPA_Dependency	create_lpa_x_dependency_from_prod(Graph prod, tree_node_ref tree_father, int offset)
{
	LPA_Dependency		result 			= NULL,
				new_1, new_2;
	Node			prod_node;
	tree_ref		cur;
	tree_node_ref		tree_node;
	Node			LHS_node 		= prod->gra.gra.nce1.left_side->node;
	int			LHS_center_x, 
				LHS_width,
				LHS_left_border,
				cur_node_x,
				cur_node_width;
	int			grid;

	grid	= 16;		/****** NOCH ERSETZEN DURCH WAHREN WERT ******/

	/******************** ZU 1. ********************/
	LHS_center_x		= (int)node_get( LHS_node, NODE_X );
	LHS_width		= (int)node_get( LHS_node, NODE_WIDTH );
	LHS_left_border		= LHS_center_x - LHS_width / 2;
	/****** linker Rand ******/
	new_1 = create_lpa_dependency();
	new_1->prod_coord		= 0;
	new_1->skaled_prod_coord	= 0;
	new_1->side			= LEFT;
	new_1->grid			= grid;
	/****** rechter Rand ******/
	new_2 = create_lpa_dependency();
	new_2->prod_coord		= LHS_width - ( grid * 2 );
	new_2->skaled_prod_coord	= (int)(LHS_width / grid) -2;
	new_2->side			= RIGHT;
	new_2->grid			= grid;
	new_2->first_border		= new_1;

	result = insert_in_lpa_dependency( result, new_2 );
	result = insert_in_lpa_dependency( result, new_1 );

	/******************** ZU 2. ********************/	
	cur = tree_father->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
			tree_node = cur->tree_rec.node;
			prod_node = tree_node->possible_nodes->array_head[offset].node;

			/*** Wir haben einen Knoten der Aktuellen Produktion. Jetzt einfuegen. ***/
			cur_node_x	= (int)node_get( prod_node, NODE_X );
			cur_node_width	= (int)node_get( prod_node, NODE_WIDTH );
			/****** linker Rand ******/
			new_1				= create_lpa_dependency();
			new_1->side			= LEFT;
			new_1->prod_coord		= (int)(cur_node_x - cur_node_width / 2 - LHS_left_border) - grid;
			new_1->new_coord		= (int)(cur_node_x - cur_node_width / 2 - LHS_left_border) - grid;
			new_1->skaled_prod_coord	= (int)(new_1->prod_coord /grid) -1;
			new_1->skaled_new_coord		= (int)(new_1->prod_coord /grid) -1;
			new_1->grid			= grid;
			new_1->prod_node		= prod_node;
			new_1->tree_node		= tree_node;
			/****** rechter Rand ******/
			new_2				= create_lpa_dependency();
			new_2->side			= RIGHT;
			new_2->prod_coord		= (int)(cur_node_x + cur_node_width / 2 - LHS_left_border) - grid;
			new_2->new_coord		= (int)(cur_node_x + cur_node_width / 2 - LHS_left_border) - grid;
			new_2->skaled_prod_coord	= (int)(new_2->prod_coord /grid) -1;
			new_2->skaled_new_coord		= (int)(new_2->prod_coord /grid) -1;
			new_2->first_border		= new_1;
			new_2->grid			= grid;
			new_2->prod_node		= prod_node;
			new_2->tree_node		= tree_node;

			result 		= insert_in_lpa_dependency( result, new_2 );
			result 		= insert_in_lpa_dependency( result, new_1 );

		}
		cur = cur->next_brother;
	}
/*	printf( "\n\n Neue x-dependency von prod %s \n", prod->label );
	new_1 = result;
	while( new_1 )
	{
		if( new_1->prod_node )
		{
			printf(" Koordinate: %d Seite: %d Label %d \n", new_1->prod_coord, new_1->side, new_1->prod_node->label.text );
		}
		new_1 = new_1->next;
	}
*/
	return( result );
}

/*****************************************************************************************
function:	create_lpa_y_dependency_from_prod
Input:	Graph prod, tree_node_ref tree_father, int offset

	Entsprechend zu create_lpa_x_dependency_from_prod

Output:	d
*****************************************************************************************/

LPA_Dependency	create_lpa_y_dependency_from_prod(Graph prod, tree_node_ref tree_father, int offset)
{
	LPA_Dependency		result 			= NULL,
				new_1, new_2;
	Node			prod_node;
	tree_ref		cur;
	tree_node_ref		tree_node;
	Node			LHS_node 		= prod->gra.gra.nce1.left_side->node;
	int			LHS_center_y, 
				LHS_height,
				LHS_up_border,
				cur_node_y,
				cur_node_height;
	int			grid;

	grid	= 16;		/****** NOCH ERSETZEN DURCH WAHREN WERT ******/

	/******************** ZU 1. ********************/
	LHS_center_y		= (int)node_get( LHS_node, NODE_Y );
	LHS_height		= (int)node_get( LHS_node, NODE_HEIGHT );
	LHS_up_border		= LHS_center_y - LHS_height / 2;
	/****** linker Rand ******/
	new_1 = create_lpa_dependency();
	new_1->prod_coord		= 0;
	new_1->skaled_prod_coord	= 0;
	new_1->side			= UP;
	new_1->grid			= grid;
	new_1->prod_node		= NULL;
	new_1->graph_node		= NULL;
	new_1->tree_node		= NULL;
	/****** rechter Rand ******/
	new_2 = create_lpa_dependency();
	new_2->prod_coord		= LHS_height - ( grid * 2 );
	new_2->skaled_prod_coord	= (int)(LHS_height / grid) -2;
	new_2->side			= DOWN;
	new_2->grid			= grid;
	new_2->first_border		= new_1;
	new_2->prod_node		= NULL;
	new_2->graph_node		= NULL;
	new_2->tree_node		= NULL;

	result = insert_in_lpa_dependency( result, new_2 );
	result = insert_in_lpa_dependency( result, new_1 );

	/******************** ZU 2. ********************/	
	cur = tree_father->first_son;
	while ( cur != NULL )
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
			tree_node = cur->tree_rec.node;
			prod_node = tree_node->possible_nodes->array_head[offset].node;

			/*** Wir haben einen Knoten der Aktuellen Produktion. Jetzt einfuegen. ***/
			cur_node_y	= (int)node_get( prod_node, NODE_Y );
			cur_node_height	= (int)node_get( prod_node, NODE_HEIGHT );
			/****** linker Rand ******/
			new_1				= create_lpa_dependency();
			new_1->side			= UP;
			new_1->prod_coord		= (int)(cur_node_y - cur_node_height / 2 - LHS_up_border) - grid;
			new_1->new_coord		= (int)(cur_node_y - cur_node_height / 2 - LHS_up_border) - grid;
			new_1->skaled_prod_coord	= (int)(new_1->prod_coord /grid) -1;
			new_1->skaled_new_coord		= (int)(new_1->prod_coord /grid) -1;
			new_1->grid			= grid;
			new_1->prod_node		= prod_node;
			new_1->graph_node		= tree_node->graph_iso;
			new_1->tree_node		= tree_node;
			/****** rechter Rand ******/
			new_2				= create_lpa_dependency();
			new_2->side			= DOWN;
			new_2->prod_coord		= (int)(cur_node_y + cur_node_height / 2 - LHS_up_border) - grid;
			new_2->new_coord		= (int)(cur_node_y + cur_node_height / 2 - LHS_up_border) - grid;
			new_2->skaled_prod_coord	= (int)(new_2->prod_coord /grid) -1;
			new_2->skaled_new_coord		= (int)(new_2->prod_coord /grid) -1;
			new_2->first_border		= new_1;
			new_2->grid			= grid;
			new_2->prod_node		= prod_node;
			new_2->graph_node		= tree_node->graph_iso;
			new_2->tree_node		= tree_node;

			result 		= insert_in_lpa_dependency( result, new_2 );
			result 		= insert_in_lpa_dependency( result, new_1 );

		}
		cur = cur->next_brother;
	}
/*	printf( "\n\n Neue y-dependency von prod %s \n", prod->label );
	new_1 = result;
	while( new_1 )
	{
		if( new_1->prod_node )
		{
			printf(" Koordinate: %d Seite: %d Label %d \n", new_1->prod_coord, new_1->side, new_1->prod_node->label.text );
		}
		new_1 = new_1->next;
	}
*/
	return( result );
}

/*****************************************************************************************
function:	get_nr_of_isomorph_productions
Input:	Graph prod

	Berechnet wieviele Produktionen zu prod isomorph sind(EINSCHLIESSLICH prod selbst)

Output:	Berechnete Anzahl
*****************************************************************************************/

int	get_nr_of_isomorph_productions(Graph prod)
{
	Graph	cur_prod;
	int	result		= 0;

	for_graph_multi_suc( prod, cur_prod )
	{
		result++;
	}
	end_for_graph_multi_suc( prod, cur_prod );

	return( result );
}

/*****************************************************************************************
function:	lpa_create_possible_productions
Input:	tree_node_ref	tree_node

	Erzeugt Datenstrukturen 'possible_productions'.
	BEACHTE: Vor Aufruf dieser Funktion muessen in den Soehnen die Zeiger 
		 'possible_nodes' eingetragen werden, da diese Zeiger zum Ausrechnen der 
		 Dependency benoetigt werden.

Output:	---
*****************************************************************************************/

void	lpa_create_possible_productions(tree_node_ref tree_node)
{
	int		nr;
	Graph		cur_prod;
	int		i;
	LPA_Production	cur_array;
	Node		LHS_node;

	nr = get_nr_of_isomorph_productions( tree_node->used_prod );

	/****** Jetzt erzeuge alles neue ******/
	tree_node->possible_productions 		= create_lpa_array_of_productions();
	tree_node->possible_productions->array_head	= create_lpa_production( nr );

	/****** Jetzt trage in neu erzeugtes array alles ein ******/
	tree_node->possible_productions->number		= nr;
	i 		= 0;
	cur_array 	= tree_node->possible_productions->array_head;

	for_graph_multi_suc( tree_node->used_prod, cur_prod )
	{
		LHS_node = cur_prod->gra.gra.nce1.left_side->node;
		cur_array[i].production		= cur_prod;
		cur_array[i].x_dependency	= create_lpa_x_dependency_from_prod( cur_prod, tree_node, i );
		cur_array[i].y_dependency	= create_lpa_y_dependency_from_prod( cur_prod, tree_node, i );
		cur_array[i].grid		= cur_array[i].y_dependency->grid;
		cur_array[i].width		= (int)node_get( LHS_node, NODE_WIDTH ) - cur_array[i].grid * 2;
		cur_array[i].height		= (int)node_get( LHS_node, NODE_HEIGHT ) - cur_array[i].grid * 2;
		cur_array[i].skaled_width	= (int)(cur_array[i].width / cur_array[i].grid) -2;
		cur_array[i].skaled_height	= (int)(cur_array[i].height / cur_array[i].grid) -2;

		i++;
	}
	end_for_graph_multi_suc( tree_node->used_prod, cur_prod );

	
}

/*****************************************************************************************
function:	lpa_create_possible_nodes_in_sons
Input:	tree_node_ref	father

	Erzeugt Datenstrukturen 'possible_nodes' in Soehnen.
	BEACHTE: Damit Zusammenhang mit Zeiger 'possible_productions' bei father gegeben
		 ist, MUSS die Sortierung der Knoten mit der Sortierung der Produktionen
		 in der Verkettung der Isomorphismen uebereinstimmen

Output:	---
*****************************************************************************************/

void	lpa_create_possible_nodes_in_sons(tree_node_ref father)
{
	Node		cur_node;
	tree_ref	cur_son;
	tree_node_ref	tree_node;
	int		i,		/****** Speichert, wieviele isomorphe Knoten ******/
			j;
	LPA_Node	new_lpa_node;

	i = get_nr_of_isomorph_productions( father->used_prod );
	cur_son = father->first_son;

	while( cur_son )
	{
		if( cur_son->tree_rec_type == TREE_NODE )
		{
			/****** Knoten gefunden. Also alles eintragen ******/
			tree_node = cur_son->tree_rec.node;

			/****** Erzeuge neue Datenstrukturen ******/
			tree_node->possible_nodes		= create_lpa_array_of_nodes	();
			new_lpa_node				= create_lpa_node		( i );

			/****** Jetzt trage in neu erzeugtes array alles ein ******/
			tree_node->possible_nodes->number 	= i;
			tree_node->possible_nodes->array_head	= new_lpa_node;

			j = 0;
			for_node_multi_suc( tree_node->prod_iso, cur_node )
			{
				new_lpa_node[j].node	= cur_node;

				j++;
			}
			end_for_node_multi_suc( tree_node->prod_iso, cur_node );
		}
		cur_son = cur_son->next_brother;
	}
}

/*****************************************************************************************
function:	lpa_create_possible_productions_for_derivation_tree
Input:	tree_ref	derivation_tree

	Fuer alle tree_node_ref im derivation_tree:
		Erzeuge die Datenstrukturen hinter dem Zeiger 'possible_productions' und
		'possible_nodes' bei Soehnen
		(Ein Array aller isomorphen Produktionen( bzw Knoten), mit denen dieser 
		 Tree_node abgeleitet werden kann )

Output:	---
*****************************************************************************************/

void	lpa_create_possible_productions_and_nodes_for_derivation_tree(tree_ref derivation_tree)
{
	tree_ref		cur;
	tree_node_ref		tree_node;

	cur = derivation_tree;

	while ( cur != NULL )
	{
		if (	(cur->tree_rec_type == TREE_NODE)	&&
			(cur->tree_rec.node->first_son)		&&
			(!cur->tree_rec.node->leaf)		)
		{
			tree_node = cur->tree_rec.node;

			/****** Es existiert (mind.) ein Sohn => Knoten wurde abgeleitet; Datenstrukturen anlegen ******/
			/****** Voraussetzung: Die Verkettungen von Knoten und Produktionen bzgl. 'multi_suc stimmen in der Reihenfolge ueberein ******/
			/****** Knoten muessen VOR Produktionen erzeugt werden, da Voraussetzung zum dependency ausrechnen ******/
			lpa_create_possible_nodes_in_sons	( tree_node );
			lpa_create_possible_productions		( tree_node );


			/****** Da nach unten geloescht werden kann, kann es sein, dass auch nicht abgeleitete Soehne Areastructures haben ******/
			
		}
		cur = cur->next_brother;
	}

	cur = derivation_tree->tree_rec.node->first_son;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
	             ( cur->tree_rec.node->first_son != NULL ) && 
		     ( !cur->tree_rec.node->leaf ) ) 
		{
			lpa_create_possible_productions_and_nodes_for_derivation_tree( cur );
		}
		cur = cur->next_brother;
	}
}


/*****************************************************************************************
function:	lpa_free_area_struc_pointers
Input:	tree_ref tree

	Loescht van allen tree_node_rec aus die Zeiger auf Area-structures

Output:	---
*****************************************************************************************/

void	lpa_free_area_struc_pointers(tree_ref tree)
{
	tree_ref	cur;
	tree_node_ref	tree_node;

	cur = tree;

	while ( cur )
	{
		if ( cur->tree_rec_type == TREE_NODE )
		{
	        	if( cur->tree_rec.node->first_son != NULL )
			{
				lpa_free_area_struc_pointers( cur->tree_rec.node->first_son );
			}

			tree_node = cur->tree_rec.node;

			if( tree_node->possible_nodes )
			{
				free( tree_node->possible_nodes->array_head );
				free( tree_node->possible_nodes );
				tree_node->possible_nodes = NULL;
			}

			if( tree_node->possible_productions )
			{
				free( tree_node->possible_productions->array_head );
				free( tree_node->possible_productions );
				tree_node->possible_productions = NULL;
			}

			if( tree_node->area_structures )
			{
				free_area_structures( tree_node->area_structures );
				tree_node->area_structures = NULL;
			}
		}
		cur = cur->next_brother;
	}
}
