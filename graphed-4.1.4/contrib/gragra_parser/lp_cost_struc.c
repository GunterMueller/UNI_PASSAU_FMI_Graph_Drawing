#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include <X11/Xos.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>

#include "misc.h"
#include "types.h"
#include "main_sf.h"
#include "lp_create_optimal_graph.h"

#include "lp_cost_struc.h"


/****************************************************************************************/
/*											*/
/* File fuer die Grundfunktionen auf den Datenstrukturen die zur Layoutoptimierung	*/
/* dienen.										*/
/*											*/
/****************************************************************************************/






/*****************************************************************************************
function:	create_lp_upper_derivation
Input:	---

	Erzeugt Speicherplatz s

Output:	Zeiger auf s
*****************************************************************************************/

LP_upper_production	create_lp_upper_production(void)
{
	LP_upper_production	new = (LP_upper_production)mymalloc( sizeof(struct lp_upper_production) );

	new->pre			= new;
	new->suc			= new;
	new->number_of_prod_layouts	= 0;
	new->production_layouts		= NULL;
	new->derivation			= NULL;

	return( new );
}

/*****************************************************************************************
function:	append_to_lp_upper_production
Input:	LP_upper_production old_list, new_elem

	Haengt new_elem hinten an old_list an

Output:	Zeiger auf Anfang der neuen Liste
*****************************************************************************************/

LP_upper_production	append_to_lp_upper_production(LP_upper_production old_list, LP_upper_production new_elem)
{
	if( !old_list )
	{
		return( new_elem );
	}

	new_elem->suc		= old_list;
	new_elem->pre		= old_list->pre;
	old_list->pre->suc	= new_elem;
	old_list->pre		= new_elem;

	return( old_list );
}

/*****************************************************************************************
function:	free_lp_upper_production_with_layouts_and_lower_derivation
Input:	LP_upper_production upper

	Loescht ALLES was an upper haengt, also
		- alles mit ->suc verkettete und dort jeweils
		- Production_layouts mit saemtlichen Moeglichkeiten unten weiter abzuleiten
		  (Datenstruktur LP_lower_derivation)

Output:	---
*****************************************************************************************/

void	free_lp_upper_production_with_layouts_and_lower_derivation(LP_upper_production upper)
{
	LP_upper_production	cur_upper,
				del_upper;
	LP_array_of_productions	upper_array;
	int			i;

	upper->pre->suc = NULL;
	cur_upper = upper;

	while( cur_upper )
	{
		upper_array = cur_upper->production_layouts;
		for( i = 0; i < cur_upper->number_of_prod_layouts; i++ )
		{
			free_lp_lower_derivation( upper_array[i].derivations_below );
			upper_array[i].derivations_below = NULL;
			free_lp_dependency_graph( upper_array[i].x_dependency );
			upper_array[i].x_dependency = NULL;
			free_lp_dependency_graph( upper_array[i].y_dependency );
			upper_array[i].y_dependency = NULL;
			free_lp_sizes_array	( upper_array[i].SIZES, upper_array[i].length_of_sizes );
		}
		free( upper_array );

		del_upper = cur_upper;
		cur_upper = cur_upper->suc;
		free( del_upper );
	}
}

/*****************************************************************************************
function:	create_lp_array_of_upper_productions
Input:	int number_of_layouts

	Erzeugt Speicherplatz s fuer number_of_layouts viele Datenstrukturen

Output: Zeiger auf s
*****************************************************************************************/

LP_array_of_productions		create_lp_array_of_upper_productions(int number_of_layouts)
{
	int			i;
	LP_array_of_productions new = (LP_array_of_productions)mycalloc( number_of_layouts, sizeof(struct lp_array_of_productions) );

	/*** initialisierung ***/

	for( i = 0; i < number_of_layouts; i++ )
	{
		new[i].SIZES			= NULL;
		new[i].length_of_sizes		= 0;
		new[i].x_dependency		= NULL;
		new[i].y_dependency		= NULL;
		new[i].prod_iso			= NULL;
		new[i].derivations_below	= NULL;
		new[i].draw_iso			= NULL;

	}

	return( new );
}


/*****************************************************************************************
function:	create_lp_lower_derivation
Input:	---

	Erzeugt Speicher s

Output:	Zeiger auf s
*****************************************************************************************/

LP_lower_derivation	create_lp_lower_derivation(void)
{
	LP_lower_derivation	new= (LP_lower_derivation)mymalloc( sizeof(struct lp_lower_derivation) );

	new->pre			= new;
	new->suc			= new;
	new->number_of_productions	= 0;
	new->productions		= NULL;

	return( new );
}

/*****************************************************************************************
function:	append_to_lp_lower_derivation
Input:	LP_lower_derivation old_list, new_elem

	Haengt new_elem hinten an old_list an

Output:	Zeiger auf neue Liste
*****************************************************************************************/

LP_lower_derivation	append_to_lp_lower_derivation(LP_lower_derivation old_list, LP_lower_derivation new_elem)
{
	if( !old_list )
	{
		return( new_elem );
	}

	new_elem->suc		= old_list;
	new_elem->pre		= old_list->pre;
	old_list->pre->suc	= new_elem;
	old_list->pre		= new_elem;

	return( old_list );
}

/*****************************************************************************************
function:	free_lp_lower_derivation
Input:	LP_lower_derivation lower

	Loescht Speicherplatz von lower und allem was dranhaengt

Output:	---
*****************************************************************************************/

void	free_lp_lower_derivation(LP_lower_derivation lower)
{
	LP_lower_derivation		cur_lower,
					del_lower;
	int				i;
	LP_array_of_lower_productions	lower_array;

	cur_lower = lower;
	cur_lower->pre->suc = NULL;

	while( cur_lower )
	{
		lower_array = cur_lower->productions;
		for( i = 0; i < cur_lower->number_of_productions; i++ )
		{
			free_lp_dependency_graph( lower_array[i].x_dependency );
			free_lp_dependency_graph( lower_array[i].y_dependency );
		}
		free( lower_array );

		del_lower = cur_lower;
		cur_lower = cur_lower->suc;
		free( del_lower );
	}
}

/*****************************************************************************************
function:	create_lp_array_of_lower_productions
Input:	int number_of_prods

	Erzeugt number_of_prods mal Speicher s

Output:	Zeiger auf s
*****************************************************************************************/

LP_array_of_lower_productions	create_lp_array_of_lower_productions(int number_of_prods)
{
	int	i;
	LP_array_of_lower_productions	new = (LP_array_of_lower_productions)mycalloc( number_of_prods, sizeof(struct lp_array_of_lower_productions) );

	for( i = 0; i < number_of_prods; i++ )
	{
		new[i].father_node	= NULL;
		new[i].lams_prod_iso	= NULL; 
		new[i].graphed_father	= NULL;
		new[i].derivation	= NULL;
		new[i].same_prod_lower	= NULL;
		new[i].array_entry	= 0;
		new[i].w		= 0;
		new[i].h		= 0;
		new[i].entry_for_costs	= 0;
		new[i].production	= NULL;
		new[i].x_dependency	= NULL;
		new[i].y_dependency	= NULL;
	}

	return( new );
}

/*****************************************************************************************
function:	create_lp_sizes_array
Input:	int	number

	Erzeugt number mal Speicher s

Output: Zeiger auf s
*****************************************************************************************/

LP_sizes_array	create_lp_sizes_array(int number)
{
	int	i;
	LP_sizes_array	new = (LP_sizes_array)mycalloc( number, sizeof(struct lp_sizes_array) );

	for( i = 0; i < number; i++ )
	{
		new[i].is_in_optimal_sizes		= TRUE;
		new[i].used_derivation			= NULL;
		new[i].nr_of_prods_below		= 0;
		new[i].used_productions			= NULL;
		new[i].upper_prod_array			= NULL;
		new[i].entry_in_upper_prod_array	= 0;
		new[i].w				= 0;
		new[i].h				= 0;
		new[i].x_dependency			= NULL;
		new[i].y_dependency			= NULL;
	}

	return( new );
}

/*****************************************************************************************
function:	free_lp_sizes_array
Input:	LP_sizes_array sizes, int nr_of_sizes

	Loescht Speicherplatz von sizes und allem was dranhaengt

Output:	---
*****************************************************************************************/

void	free_lp_sizes_array(LP_sizes_array sizes, int nr_of_sizes)
{
	int	i;

	for( i = 0; i < nr_of_sizes; i++ )
	{
		free_lp_dependency_graph( sizes[i].x_dependency );
		free_lp_dependency_graph( sizes[i].y_dependency );
		free( sizes[i].used_productions );
	}
	free( sizes );
}

/*****************************************************************************************
function:	create_lp_dependency_graph
Input:	


Output:
*****************************************************************************************/

LP_dependency_graph	create_lp_dependency_graph(void)
{
	LP_dependency_graph	new = (LP_dependency_graph)mymalloc( sizeof(struct lp_dependency_graph) );

	new->first_border_part			= NULL;
	new->copy_iso				= NULL;
	new->next				= NULL;
	new->node_in_prod			= NULL;
	new->side				= LEFT;
	new->prod_nr				= -1;		/* Entspricht UNDEF */
	new->original_coord			= 0;
	new->original_coord_with_graph_sizes	= 0;
	new->new_coord				= 0;

	return( new );
}

/*****************************************************************************************
function:	create_copy_of_lp_dependency_graph
Input:	LP_dependency_graph old

	Erzeugt Kpie von old

Output:	Zeiger auf Kopie
*****************************************************************************************/

LP_dependency_graph	create_copy_of_lp_dependency_graph(LP_dependency_graph old)
{
	LP_dependency_graph	new,
				new_head,
				last,
				cur_old;

	cur_old		= old;
	new_head	= NULL;
	last		= NULL;

	while( cur_old )
	{
		new = create_lp_dependency_graph();
		cur_old->copy_iso		= new;

		if( cur_old->first_border_part )
		{
			new->first_border_part		= cur_old->first_border_part->copy_iso; 
		}
		new->node_in_prod			= cur_old->node_in_prod;
		new->side				= cur_old->side;
		new->prod_nr				= cur_old->prod_nr;
		new->original_coord			= cur_old->original_coord;
		new->original_coord_with_graph_sizes	= cur_old->original_coord_with_graph_sizes;
		new->new_coord				= cur_old->new_coord;

		if( last )
		{
			last->next = new;
		}
		last = new;

		if( !new_head )
		{
			new_head = new ;
		}
		cur_old = cur_old->next;
	}
	return( new_head );
}

/*****************************************************************************************
function:	insert_in_lp_dependency_graph
Input:	LP_dependency_graph old, new

	Fuegt new in old sortiert nach original_coord ein.
	Bei gleichen Koordinaten: Datensegmente die mit RIGHT oder DOWN markiert sind
		werden hinten angefuegt

Output:	Zeiger auf neue Liste
*****************************************************************************************/

LP_dependency_graph	insert_in_lp_dependency_graph(LP_dependency_graph old, LP_dependency_graph new)
{
	LP_dependency_graph	cur_dep;

	if( !old )
	{
		return( new );
	}

	if( old->original_coord > new->original_coord )
	{
		new->next = old;
		return( new );
	}

	if( old->original_coord == new->original_coord )
	{
		if( (new->side == RIGHT) || (new->side == DOWN) )
		{
			cur_dep = old;
			while( cur_dep->next->original_coord == new->original_coord )
			{
				cur_dep = cur_dep->next;
			}
			new->next = cur_dep->next;
			cur_dep->next = new;
			return( old );
		}
		new->next = old;
		return( new );
	}

	cur_dep = old;
	while( cur_dep->next )
	{
		if( cur_dep->next->original_coord > new->original_coord )
		{
			new->next = cur_dep->next;
			cur_dep->next = new;
			return( old );
		}

		if( cur_dep->next->original_coord == new->original_coord )
		{
			if( (new->side == RIGHT) || (new->side == DOWN) )
			{
				while( cur_dep->next->original_coord == new->original_coord )
				{
					cur_dep = cur_dep->next;
				}
				new->next = cur_dep->next;
				cur_dep->next = new;
				return( old );
			}
			new->next = cur_dep->next;
			cur_dep->next = new;
			return( old );
		}
		cur_dep = cur_dep->next;
	}
	cur_dep->next = new;
	return( old );
}

/*****************************************************************************************
function:	create_lp_x_dependency_graph_from_sprod
Input:	Sprod prod,  LP_lower_derivation lower_der, LP_Parsing_elements parsing_elements

	Erzeugt einen LP_dependency_graph d von prod. 
	Parsing_elements entsprechen den Knoten des abgeleiteten Graphen. Hier kann man
	auf die lamshoeft Datenstruktur zugreifen wo steht, wie gross die Knoten im 
	abgeleiteten GRAPHEN waren.

Output:	d
*****************************************************************************************/

LP_dependency_graph	create_lp_x_dependency_graph_from_sprod(Sprod prod, LP_lower_derivation lower_der, LP_Parsing_element_list parsing_elements)
{
	Snode				cur_node;
	LP_array_of_lower_productions	low_array;
	LP_dependency_graph		new_1,
					new_2,
					x_depen,
					x_dependency_result	= NULL;
	LP_Parsing_element		corresponding_pe;
	int				LHS_center_x, 
					LHS_width,
					LHS_left_border,
					cur_node_x,
					cur_node_width,
					i;
	int				current_x_plus,
					current_node_size,
					new_x_plus,
					old_coord_plus,
					new_coord_plus;


	/****** LHS_Node ******/
		LHS_center_x		= (int)node_get( (Node)prod->graphed_left, NODE_X );
		LHS_width		= (int)node_get( (Node)prod->graphed_left, NODE_WIDTH );
		LHS_left_border		= LHS_center_x - LHS_width / 2;
		/****** linker Rand ******/
			new_1 = create_lp_dependency_graph();
			new_1->side	= LEFT;
		/****** rechter Rand ******/
			new_2 = create_lp_dependency_graph();
			new_2->side			= RIGHT;
			new_2->original_coord		= LHS_width;
			new_2->first_border_part	= new_1;

			x_dependency_result = insert_in_lp_dependency_graph( x_dependency_result, new_2 );
			x_dependency_result = insert_in_lp_dependency_graph( x_dependency_result, new_1 );
	/****** restliche Knoten der RHS ******/
		if( lower_der )
		{
			low_array = lower_der->productions;
		}
		for_all_nodes( prod->right, cur_node )
		{
			cur_node_x	= (int)node_get( (Node)cur_node->graphed, NODE_X );
			cur_node_width	= (int)node_get( (Node)cur_node->graphed, NODE_WIDTH );
			/****** x_depenedency ******/
				new_1				= create_lp_dependency_graph();
				new_2				= create_lp_dependency_graph();
				new_1->side			= LEFT;
				new_1->original_coord		= cur_node_x - cur_node_width / 2 - LHS_left_border;
				new_2->side			= RIGHT;
				new_2->original_coord		= cur_node_x + cur_node_width / 2 - LHS_left_border;
				new_2->first_border_part	= new_1;
				new_2->node_in_prod		= cur_node;
				x_dependency_result 		= insert_in_lp_dependency_graph( x_dependency_result, new_2 );
				x_dependency_result 		= insert_in_lp_dependency_graph( x_dependency_result, new_1 );

				if( lower_der )
				{
					for( i = 0; i < lower_der->number_of_productions; i++ )
					{
						if( low_array[i].graphed_father == cur_node )
						{
							new_1->prod_nr	= i;
							new_2->prod_nr	= i;
						}
					}
				}
		}
		end_for_all_nodes( prod->right, cur_node );

	/****** ausrechnen, wie sich alles verschiebt, wenn die Groessen vom Ausgangsgraph verwendet werden ******/
	old_coord_plus = 0;
	x_depen = x_dependency_result;

	while( x_depen )
	{
		if( (x_depen->side == RIGHT) && x_depen->node_in_prod )
		{
			corresponding_pe	= find_corresponding_pe( parsing_elements, x_depen->node_in_prod );

			if(corresponding_pe->width != 0 )
			{
				/****** Knoten hat Entsprechnung im Graphen, also trage dessen Groesse ein ******/
			
				current_x_plus		= corresponding_pe->width;
				current_node_size	= (x_depen->original_coord + old_coord_plus) - 
								  x_depen->first_border_part->original_coord_with_graph_sizes;
				new_x_plus		= current_x_plus - current_node_size;
				if( new_x_plus < 0 )
				{
					new_x_plus = 0;
				}
				x_depen->original_coord_with_graph_sizes	= x_depen->original_coord + old_coord_plus + new_x_plus;
				new_coord_plus					= old_coord_plus + new_x_plus;
			}
			else
			{
				x_depen->original_coord_with_graph_sizes	= x_depen->original_coord + old_coord_plus;
				new_coord_plus					= old_coord_plus;
			}
		}
		else
		{
			x_depen->original_coord_with_graph_sizes	= x_depen->original_coord + old_coord_plus;
			new_coord_plus					= old_coord_plus;
		}

		old_coord_plus = new_coord_plus;

		if( WIN.create_with_graph_nodesizes )
		{
			x_depen->new_coord = x_depen->original_coord_with_graph_sizes;
		}
		else
		{
			x_depen->new_coord = x_depen->original_coord;
		}

		x_depen = x_depen->next;
	}

	return( x_dependency_result );
}

/*****************************************************************************************
function:	create_lp_y_dependency_graph_from_sprod
Input:	Sprod prod

	Erzeugt einen LP_dependency_graph d von prod

Output:	d
*****************************************************************************************/

LP_dependency_graph	create_lp_y_dependency_graph_from_sprod(Sprod prod, LP_lower_derivation lower_der, LP_Parsing_element_list parsing_elements)
{
	Snode				cur_node;
	LP_array_of_lower_productions	low_array;
	LP_dependency_graph		new_1,
					new_2,
					y_depen,
					y_dependency_result	= NULL;
	LP_Parsing_element		corresponding_pe;
	int				LHS_center_y,
					LHS_height,
					LHS_upper_border,
					cur_node_y,
					cur_node_height,
					i;
	int				current_y_plus,
					current_node_size,
					new_y_plus,
					old_coord_plus,
					new_coord_plus;

	/****** LHS_Node ******/
		LHS_center_y		= (int)node_get( (Node)prod->graphed_left, NODE_Y );
		LHS_height		= (int)node_get( (Node)prod->graphed_left, NODE_HEIGHT );
		LHS_upper_border	= LHS_center_y - LHS_height / 2;
		/****** oberer Rand ******/
			new_1				= create_lp_dependency_graph();
			new_1->side			= UP;
		/****** unterer Rand ******/
			new_2				= create_lp_dependency_graph();
			new_2->side			= DOWN;
			new_2->original_coord		= LHS_height;
			new_2->first_border_part	= new_1;

			y_dependency_result		= insert_in_lp_dependency_graph( y_dependency_result, new_2 );
			y_dependency_result		= insert_in_lp_dependency_graph( y_dependency_result, new_1 );
	/****** restliche Knoten der RHS ******/
		if( lower_der )
		{
			low_array = lower_der->productions;
		}
		for_all_nodes( prod->right, cur_node )
		{
			cur_node_y	= (int)node_get( (Node)cur_node->graphed, NODE_Y );
			cur_node_height	= (int)node_get( (Node)cur_node->graphed, NODE_HEIGHT );
			/******y_dependency ******/
				new_1				= create_lp_dependency_graph();
				new_2				= create_lp_dependency_graph();
				new_1->side			= UP;
				new_1->original_coord		= cur_node_y - cur_node_height / 2 - LHS_upper_border;
				new_2->side			= DOWN;
				new_2->original_coord		= cur_node_y + cur_node_height / 2 - LHS_upper_border;
				new_2->first_border_part	= new_1;
				new_2->node_in_prod		= cur_node;
				y_dependency_result 		= insert_in_lp_dependency_graph( y_dependency_result, new_2 );
				y_dependency_result 		= insert_in_lp_dependency_graph( y_dependency_result, new_1 );

				if( lower_der )
				{
					for( i = 0; i < lower_der->number_of_productions; i++ )
					{
						if( low_array[i].graphed_father == cur_node )
						{
							new_1->prod_nr	= i;
							new_2->prod_nr	= i;
						}
					}
				}
		}
		end_for_all_nodes( prod->right, cur_node );

	/****** ausrechnen, wie sich alles verschiebt, wenn die Groessen vom Ausgangsgraph verwendet werden ******/
	old_coord_plus = 0;
	y_depen = y_dependency_result;

	while( y_depen )
	{
		if( (y_depen->side == DOWN) && y_depen->node_in_prod )
		{
			corresponding_pe	= find_corresponding_pe( parsing_elements, y_depen->node_in_prod );

			if(corresponding_pe->height != 0 )
			{
				/****** Knoten hat Entsprechnung im Graphen, also trage dessen Groesse ein ******/
			
				current_y_plus		= corresponding_pe->height;
				current_node_size	= (y_depen->original_coord + old_coord_plus) - 
							  y_depen->first_border_part->original_coord_with_graph_sizes;
				new_y_plus		= current_y_plus - current_node_size;
				if( new_y_plus < 0 )
				{
					new_y_plus = 0;
				}

				y_depen->original_coord_with_graph_sizes	= y_depen->original_coord + old_coord_plus + new_y_plus;
				new_coord_plus					= old_coord_plus + new_y_plus;
			}
			else
			{
				y_depen->original_coord_with_graph_sizes	= y_depen->original_coord + old_coord_plus;
				new_coord_plus					= old_coord_plus;
			}
		}
		else
		{
			y_depen->original_coord_with_graph_sizes	= y_depen->original_coord + old_coord_plus;
			new_coord_plus				= old_coord_plus;
		}
		old_coord_plus = new_coord_plus;

		if( WIN.create_with_graph_nodesizes )
		{
			y_depen->new_coord = y_depen->original_coord_with_graph_sizes;
		}
		else
		{
			y_depen->new_coord = y_depen->original_coord;
		}
		y_depen = y_depen->next;
	}

	return( y_dependency_result );
}

/*****************************************************************************************
function:	free_lp_dependency_graph
Input:	LP_dependency_graph head

	Loescht Speicherplatz vom ganzen dependency_graph

Output:	---
*****************************************************************************************/

void	free_lp_dependency_graph(LP_dependency_graph head)
{
	LP_dependency_graph	cur,
				to_delete;

	cur = head;
	while( cur )
	{
		to_delete = cur;
		cur = cur->next;

		free( to_delete );
	}
}

/*****************************************************************************************
function	create_lp_sizes_ref
Input:	int nr

	Erzeugt array vom Typ LP_sizes_ref mit laenge nr

Output:	Zeiger auf erzeugtes
****************************************************************************************/

LP_sizes_ref	create_lp_sizes_ref(int nr)
{
	int	i;
	LP_sizes_ref	new = (LP_sizes_ref)mycalloc( nr, sizeof(struct lp_sizes_ref) );

	for( i = 0; i < nr; i++ )
	{
		new[i].which_sizes_array	= NULL;
		new[i].what_entry_in_array	= 0;
	}

	return( new );
}
