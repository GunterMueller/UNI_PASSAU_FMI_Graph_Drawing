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
#include "parser.h"
#include "main_sf.h"

#include "lp_datastruc.h"
#include "lp_cost_struc.h"

#include "lp_costs.h"
/****************************************************************************************/
/*											*/
/* In diesem File stehen die Funktionen, um die Kosten einer Produktion auszurechnen	*/
/*											*/
/****************************************************************************************/

 int	UNDEF =	-1;
/*****************************************************************************************
function:	compute_costs_for_current_selection_with_graph_sizes
Input:	LP_array_of_productions	head, LP_lower_derivation lower_der

	Berechnet welche Kosten entstehen, falls head mit der aktuellen Ableitung und den
	darin aktuell ausgewaehlten Groessen abgeleitet wird

Output:	LP_sizes_array mit berechneter Groesse
*****************************************************************************************/

void	compute_costs_for_current_selection_with_graph_sizes(LP_array_of_productions head, LP_lower_derivation lower_der, LP_sizes_array sizes_array, int where_to_set)
{
	LP_array_of_lower_productions	cur_lower_array;
	LP_sizes_ref			sizes_ref;

	LP_dependency_graph					x_depen,			y_depen;
	int			which_sizes_array,		current_x_plus,			current_y_plus,
				which_prod,			new_x_plus,			new_y_plus,
				array_entry_below,		x_size,				y_size,
				current_node_size,
				current_sizes_ref,
				new_coord_plus,
				old_coord_plus;

	sizes_ref = create_lp_sizes_ref( lower_der->number_of_productions );
	current_sizes_ref = 0; 

	old_coord_plus	= 0;

	x_depen = head->x_dependency;
	while( x_depen )
	{
		cur_lower_array = NULL;
		if( x_depen->side == RIGHT )
		{
			if( x_depen->prod_nr != UNDEF )
			{
				which_prod		= x_depen->prod_nr;
				cur_lower_array		= lower_der->productions;
				which_sizes_array	= cur_lower_array[which_prod].entry_for_costs;
			}
			else
			{
				which_sizes_array = UNDEF;
			}
			
			if( which_sizes_array != UNDEF )		/****** In diesem Fall wurde Knoten abgeleitet ->hole Groesse ******/
			{
				array_entry_below	= cur_lower_array[which_prod].array_entry;
				current_x_plus		= 
					cur_lower_array[which_prod].same_prod_lower->production_layouts[array_entry_below].SIZES[which_sizes_array].w;
				current_node_size	= x_depen->original_coord_with_graph_sizes + old_coord_plus - 
							  x_depen->first_border_part->new_coord;
				if( (new_x_plus = current_x_plus - current_node_size) < 0 )
				{
					new_x_plus = 0;
				}
				x_depen->new_coord	= x_depen->original_coord_with_graph_sizes + old_coord_plus + new_x_plus;
				new_coord_plus		= old_coord_plus + new_x_plus;
				old_coord_plus 		=new_coord_plus;

				/****** Trage ein, wie bei jetzigem sizes sich die weitere Ableitung fuer diesen Knoten ergibt ******/
				sizes_ref[current_sizes_ref].which_sizes_array = 
							cur_lower_array[which_prod].same_prod_lower->production_layouts[array_entry_below].SIZES;
				sizes_ref[current_sizes_ref].what_entry_in_array = which_sizes_array;
				current_sizes_ref++;
			}
			else
			{
				if( cur_lower_array )
				{
					current_x_plus		= cur_lower_array[which_prod].w;
					current_node_size	= x_depen->original_coord_with_graph_sizes + old_coord_plus - 
								  x_depen->first_border_part->new_coord;

					if( (new_x_plus = current_x_plus - current_node_size) < 0 )
					{
						new_x_plus = 0;
					}
					x_depen->new_coord	= x_depen->original_coord_with_graph_sizes + old_coord_plus + new_x_plus;
					new_coord_plus		= old_coord_plus + new_x_plus;
				}
				else
				{
					x_depen->new_coord	= x_depen->original_coord_with_graph_sizes + old_coord_plus;
					new_coord_plus		= old_coord_plus;
				}
				old_coord_plus = new_coord_plus;
			}
		}
		else
		{
			x_depen->new_coord	= x_depen->original_coord_with_graph_sizes + old_coord_plus;
			new_coord_plus		= old_coord_plus;
		}
		if( !x_depen->next )
		{
			x_size = x_depen->new_coord;
		}
		x_depen = x_depen->next;
	}

	old_coord_plus	= 0;

	y_depen = head->y_dependency;
	while( y_depen )
	{
		cur_lower_array = NULL;
		if( y_depen->side == DOWN )
		{
			if( y_depen->prod_nr != UNDEF )
			{
				which_prod		= y_depen->prod_nr;
				cur_lower_array		= lower_der->productions;
				which_sizes_array	= cur_lower_array[which_prod].entry_for_costs;
			}
			else
			{
				which_sizes_array = UNDEF;
			}
			
			if( which_sizes_array != UNDEF )		/****** In diesem Fall existiert sizes_array ->hole Groesse ******/
			{
				array_entry_below	= cur_lower_array[which_prod].array_entry;
				current_y_plus		=
					cur_lower_array[which_prod].same_prod_lower->production_layouts[array_entry_below].SIZES[which_sizes_array].h;
				current_node_size	= y_depen->original_coord_with_graph_sizes + old_coord_plus - 
							  y_depen->first_border_part->new_coord;
				if( (new_y_plus = current_y_plus - current_node_size) < 0 )
				{
					new_y_plus = 0;
				}
				y_depen->new_coord	= y_depen->original_coord_with_graph_sizes + old_coord_plus + new_y_plus;
				new_coord_plus		= old_coord_plus + new_y_plus;
				old_coord_plus 		= new_coord_plus;
			}
			else
			{
				/****** Knoten wurde abgeleitet, es existiert aber kein sizes_array, nehme groesse aus prod ******/

				if( cur_lower_array )
				{
					current_y_plus		= cur_lower_array[which_prod].h;
					current_node_size	= y_depen->original_coord_with_graph_sizes + old_coord_plus - 
												y_depen->first_border_part->new_coord;

					if( (new_y_plus = current_y_plus - current_node_size) < 0 )
					{
						new_y_plus = 0;
					}
					y_depen->new_coord	= y_depen->original_coord_with_graph_sizes + old_coord_plus + new_y_plus;
					new_coord_plus		= old_coord_plus + new_y_plus;

				}
				else
				{
					y_depen->new_coord	= y_depen->original_coord_with_graph_sizes + old_coord_plus;
					new_coord_plus		= old_coord_plus;
				}
				old_coord_plus = new_coord_plus;
			}
		}
		else
		{
			y_depen->new_coord	= y_depen->original_coord_with_graph_sizes + old_coord_plus;
			new_coord_plus	= old_coord_plus;
		}
		if( !y_depen->next )
		{
			y_size = y_depen->new_coord;
		}
		y_depen = y_depen->next;
	}

	sizes_array[where_to_set].w 			= x_size;
	sizes_array[where_to_set].h 			= y_size;
	sizes_array[where_to_set].used_productions	= sizes_ref;
	sizes_array[where_to_set].used_derivation	= lower_der;
	sizes_array[where_to_set].nr_of_prods_below	= lower_der->number_of_productions;
	sizes_array[where_to_set].x_dependency		= create_copy_of_lp_dependency_graph( head->x_dependency );
	sizes_array[where_to_set].y_dependency		= create_copy_of_lp_dependency_graph( head->y_dependency );

}


/*****************************************************************************************
function:	compute_costs_for_current_selection
Input:	LP_array_of_productions	head, LP_lower_derivation lower_der

	Berechnet welche Kosten entstehen, falls head mit der aktuellen Ableitung und den
	darin aktuell ausgewaehlten Groessen abgeleitet wird

Output:	LP_sizes_array mit berechneter Groesse
*****************************************************************************************/

void	compute_costs_for_current_selection(LP_array_of_productions head, LP_lower_derivation lower_der, LP_sizes_array sizes_array, int where_to_set)
{
	LP_array_of_lower_productions	cur_lower_array;
	LP_sizes_ref			sizes_ref;

	LP_dependency_graph					x_depen,			y_depen;
	int			which_sizes_array,		current_x_plus,			current_y_plus,
				which_prod,			new_x_plus,			new_y_plus,
				array_entry_below,		x_size,				y_size,
				current_node_size,
				current_sizes_ref,
				new_coord_plus,
				old_coord_plus;


	sizes_ref = create_lp_sizes_ref( lower_der->number_of_productions );
	current_sizes_ref = 0; 
	old_coord_plus	= 0;

	x_depen = head->x_dependency;
	while( x_depen )
	{
		cur_lower_array = NULL;
		if( x_depen->side == RIGHT )
		{
			if( x_depen->prod_nr != UNDEF )
			{
				which_prod		= x_depen->prod_nr;
				cur_lower_array		= lower_der->productions;
				which_sizes_array	= cur_lower_array[which_prod].entry_for_costs;
			}
			else
			{
				which_sizes_array = UNDEF;
			}
			
			if( which_sizes_array != UNDEF )		/****** In diesem Fall wurde Knoten abgeleitet ->hole Groesse ******/
			{
				array_entry_below	= cur_lower_array[which_prod].array_entry;
				current_x_plus		= 
					cur_lower_array[which_prod].same_prod_lower->production_layouts[array_entry_below].SIZES[which_sizes_array].w;
				current_node_size	= x_depen->original_coord + old_coord_plus - x_depen->first_border_part->new_coord;
				if( (new_x_plus = current_x_plus - current_node_size) < 0 )
				{
					new_x_plus = 0;
				}
				x_depen->new_coord	= x_depen->original_coord + old_coord_plus + new_x_plus;
				new_coord_plus		= old_coord_plus + new_x_plus;
				old_coord_plus		= new_coord_plus;

				/****** Trage ein, wie bei jetzigem sizes sich die weitere Ableitung fuer diesen Knoten ergibt ******/
				sizes_ref[current_sizes_ref].which_sizes_array = 
						cur_lower_array[which_prod].same_prod_lower->production_layouts[array_entry_below].SIZES;
				sizes_ref[current_sizes_ref].what_entry_in_array = which_sizes_array;
				current_sizes_ref++;
			}
			else
			{
				if( cur_lower_array )
				{
					current_x_plus		= cur_lower_array[which_prod].w;
					current_node_size	= x_depen->original_coord + old_coord_plus - x_depen->first_border_part->new_coord;

					if( (new_x_plus = current_x_plus - current_node_size) < 0 )
					{
						new_x_plus = 0;
					}
					x_depen->new_coord	= x_depen->original_coord + old_coord_plus + new_x_plus;
					new_coord_plus		= old_coord_plus + new_x_plus;
				}
				else
				{
					x_depen->new_coord	= x_depen->original_coord + old_coord_plus;
					new_coord_plus		= old_coord_plus;
				}
				old_coord_plus = new_coord_plus;
			}
		}
		else
		{
			x_depen->new_coord	= x_depen->original_coord + old_coord_plus;
			new_coord_plus		= old_coord_plus;
		}
		if( !x_depen->next )
		{
			x_size = x_depen->new_coord;
		}
		x_depen = x_depen->next;
	}

	old_coord_plus	= 0;

	y_depen = head->y_dependency;
	while( y_depen )
	{
		cur_lower_array = NULL;
		if( y_depen->side == DOWN )
		{
			if( y_depen->prod_nr != UNDEF )
			{
				which_prod		= y_depen->prod_nr;
				cur_lower_array		= lower_der->productions;
				which_sizes_array	= cur_lower_array[which_prod].entry_for_costs;
			}
			else
			{
				which_sizes_array = UNDEF;
			}
			
			if( which_sizes_array != UNDEF )		/****** In diesem Fall existiert sizes_array ->hole Groesse ******/
			{
				array_entry_below	= cur_lower_array[which_prod].array_entry;
				current_y_plus		=
					cur_lower_array[which_prod].same_prod_lower->production_layouts[array_entry_below].SIZES[which_sizes_array].h;
				current_node_size	= y_depen->original_coord + old_coord_plus - y_depen->first_border_part->new_coord;
				if( (new_y_plus = current_y_plus - current_node_size) < 0 )
				{
					new_y_plus = 0;
				}
				y_depen->new_coord	= y_depen->original_coord + old_coord_plus + new_y_plus;
				new_coord_plus		= old_coord_plus + new_y_plus;
				old_coord_plus 		= new_coord_plus;
			}
			else
			{
				/****** Knoten wurde abgeleitet, es existiert aber kein sizes_array, nehme groesse aus prod ******/

				if( cur_lower_array )
				{
					current_y_plus		= cur_lower_array[which_prod].h;
					current_node_size	= y_depen->original_coord + old_coord_plus - y_depen->first_border_part->new_coord;

					if( (new_y_plus = current_y_plus - current_node_size) < 0 )
					{
						new_y_plus = 0;
					}
					y_depen->new_coord	= y_depen->original_coord + old_coord_plus + new_y_plus;
					new_coord_plus		= old_coord_plus + new_y_plus;
				}
				else
				{
					y_depen->new_coord	= y_depen->original_coord + old_coord_plus;
					new_coord_plus		= old_coord_plus;
				}
				old_coord_plus = new_coord_plus;
			}
		}
		else
		{
			y_depen->new_coord	= y_depen->original_coord + old_coord_plus;
			new_coord_plus		= old_coord_plus;
		}
		if( !y_depen->next )
		{
			y_size = y_depen->new_coord;
		}
		y_depen = y_depen->next;
	}

	sizes_array[where_to_set].w 			= x_size;
	sizes_array[where_to_set].h 			= y_size;
	sizes_array[where_to_set].used_productions	= sizes_ref;
	sizes_array[where_to_set].used_derivation	= lower_der;
	sizes_array[where_to_set].nr_of_prods_below	= lower_der->number_of_productions;
	sizes_array[where_to_set].x_dependency		= create_copy_of_lp_dependency_graph( head->x_dependency );
	sizes_array[where_to_set].y_dependency		= create_copy_of_lp_dependency_graph( head->y_dependency );

}

/*****************************************************************************************
function:	get_nr_of_possible_sizes
Input:	LP_array_of_productions head

	Berechnet auf wieviele Arten head abgeleitet werden kann ( verschiedene Groessen
	und verschiedene Ableitungen )

Output:	Berechnete Anzahl
*****************************************************************************************/

int	get_nr_of_possible_sizes(LP_array_of_productions head)
{
	LP_lower_derivation		cur_lower_der;
	LP_array_of_lower_productions	lower_array;
	int				i,
					result = 0,
					cur_sum;
	

	FOR_LP_LOWER_DERIVATION( head->derivations_below, cur_lower_der )
	{
		lower_array = cur_lower_der->productions;
		cur_sum = 1;
		for( i = 0; i < cur_lower_der->number_of_productions; i++ )
		{
			if( lower_array[i].same_prod_lower )
			{
				cur_sum = cur_sum * lower_array[i].same_prod_lower->production_layouts[lower_array[i].array_entry].length_of_sizes;
			}
		}
		result += cur_sum;
	}
	END_FOR_LP_LOWER_DERIVATION( head->derivations_below, cur_lower_der );
	
	return( result );
}
/*****************************************************************************************
function:	make_entries_for_all_possible_combinations_rek
Input:	Int first_array_entry, LP_lower_derivation lower_der, LP_sizes_array sizes,
	LP_array_of_productions head

	Berechnet saemtliche Eintraege in Sizes, die vin lower_der abhaengen

Output:	Stelle NACH letztem Eintrag
*****************************************************************************************/

int	make_entries_for_all_possible_combinations_rek(int first_array_entry, LP_lower_derivation lower_der, LP_sizes_array sizes, LP_array_of_productions head, int where_we_are)
{
	int				i,j;
	LP_array_of_lower_productions	lower_array;

	if( where_we_are == lower_der->number_of_productions )	/*** Now we can make our entrys ***/
	{
		if( WIN.create_with_graph_nodesizes )
		{
			compute_costs_for_current_selection_with_graph_sizes( head, lower_der, sizes, first_array_entry );
		}
		else
		{
			compute_costs_for_current_selection( head, lower_der, sizes, first_array_entry );
		}
		return( ++first_array_entry );
	}

	lower_array = lower_der->productions;
	i = where_we_are;
	if( lower_array[i].same_prod_lower )
	{
		for( j = 0; j < lower_array[i].same_prod_lower->production_layouts[lower_array[i].array_entry].length_of_sizes; j++ )
		{
			lower_array[i].entry_for_costs = j;
			first_array_entry = make_entries_for_all_possible_combinations_rek( first_array_entry, lower_der, sizes, head, i+1 );
		}
	}
	else
	{
		lower_array[i].entry_for_costs = UNDEF;
		first_array_entry = make_entries_for_all_possible_combinations_rek( first_array_entry, lower_der, sizes, head, i + 1 );
	}
	return( first_array_entry );
}


/*****************************************************************************************
function:	make_entries_for_all_possible_combinations
Input:	Int first_array_entry, LP_lower_derivation lower_der, LP_sizes_array sizes,
	LP_array_of_productions head

	Berechnet saemtliche Eintraege in Sizes, die vin lower_der abhaengen

Output:	Stelle NACH letztem Eintrag
*****************************************************************************************/

int	make_entries_for_all_possible_combinations(int first_array_entry, LP_lower_derivation lower_der, LP_sizes_array sizes, LP_array_of_productions head)
{
	int				j;
	LP_array_of_lower_productions	lower_array;

	lower_array = lower_der->productions;
	if( lower_array[0].same_prod_lower )
	{
		for( j = 0; j < lower_array[0].same_prod_lower->production_layouts[lower_array[0].array_entry].length_of_sizes; j++ )
		{
			lower_array[0].entry_for_costs = j;
			first_array_entry = make_entries_for_all_possible_combinations_rek( first_array_entry, lower_der, sizes, head, 0+1 );
		}
	}
	else
	{
		lower_array[0].entry_for_costs = UNDEF;
		first_array_entry = make_entries_for_all_possible_combinations_rek( first_array_entry, lower_der, sizes, head, 0+1 );
	}
	return( first_array_entry );
}

/*****************************************************************************************
function:	clear_non_optimal_sizes
Input:	LP_sizes_array array, int *array_length

	Erzeugt ein neues array, das jetzt aber nur noch optimale sizes enthaelt

Output:	Neu erzeugtes array
*****************************************************************************************/

LP_sizes_array	clear_non_optimal_sizes(LP_sizes_array array, int *array_length)
{
	int		i, j, count;
	int		w_of_i, h_of_i, w_of_j, h_of_j;
	LP_sizes_array	new;



	for( i = 0; i < *array_length; i++ )
	{
		for( j = 0; j < i; j++ )
		{
			if( array[j].is_in_optimal_sizes )
			{
				w_of_i = array[i].w;
				h_of_i = array[i].h;
				w_of_j = array[j].w;
				h_of_j = array[j].h;
/*
				if( ((w_of_j < w_of_i) && (h_of_j <= h_of_i)) ||
				    ((w_of_j <= w_of_i) && (h_of_j < h_of_i))   )
				{
					array[i].is_in_optimal_sizes = FALSE;
				}
				if(  ((w_of_j > w_of_i) && (h_of_j >= h_of_i)) ||
				    ((w_of_j >= w_of_i) && (h_of_j >= h_of_i))    )
				{
					array[j].is_in_optimal_sizes = FALSE;
					goto exit;
				}
*/
				if( (w_of_j >= w_of_i) && (h_of_j >= h_of_i) ) 
				{
					array[j].is_in_optimal_sizes = FALSE;
					goto exit;
				}

				if( (w_of_j <= w_of_i) && (h_of_j <= h_of_i) ) 
				{
					array[i].is_in_optimal_sizes = FALSE;
				}
			}
		}
exit:	;
	}


	count = 0;
	for( i = 0; i < *array_length; i++ )
	{
		if( array[i].is_in_optimal_sizes )
		{
			count++;
		}
	}

	new = create_lp_sizes_array( count );

	count = 0;
	for( i = 0; i < *array_length; i++ )
	{
		if( array[i].is_in_optimal_sizes )
		{
			new[count].w				= array[i].w;
			new[count].h				= array[i].h;
			new[count].used_derivation		= array[i].used_derivation;
			new[count].nr_of_prods_below		= array[i].nr_of_prods_below;
			new[count].used_productions		= array[i].used_productions;
			new[count].upper_prod_array		= array[i].upper_prod_array;
			new[count].entry_in_upper_prod_array	= array[i].entry_in_upper_prod_array;
			new[count].x_dependency			= array[i].x_dependency;
			new[count].y_dependency			= array[i].y_dependency;
			count++;
		}
		else
		{
			free			( array[i].used_productions 	);
			free_lp_dependency_graph( array[i].x_dependency		);
			free_lp_dependency_graph( array[i].y_dependency 	);
		}

	}
	free( array );

	*array_length = count;

	return( new );
}

/*****************************************************************************************
function:	create_optimal_sizes
Input:	LP_upper_production upper_production

	Erzeugt optimales Set von Layoutgroessen, die bei der Ableitung von allen
	Production_layouts von upper_production entstehen koennen

Output:	---
*****************************************************************************************/

void	create_optimal_sizes(LP_upper_production upper_production)
{
	LP_upper_production		cur_upper_production;
	LP_array_of_productions		cur_prod_layout;
	LP_sizes_array			cur_sizes;
	LP_lower_derivation		cur_lower_der;
	int				i, j,
					cur_first_entry,
					nr_of_sizes;

	FOR_LP_UPPER_PRODUCTION( upper_production, cur_upper_production )
	{
		cur_prod_layout = cur_upper_production->production_layouts;
		for( i = 0; i < cur_upper_production->number_of_prod_layouts; i++ )
		{
			if( !cur_prod_layout[i].derivations_below )
			{
				nr_of_sizes = 1;
				cur_sizes = create_lp_sizes_array( 1 );
				cur_sizes[0].upper_prod_array = cur_prod_layout;
				cur_sizes[0].entry_in_upper_prod_array = i;
				cur_sizes[0].w = (int)node_get( (Node)cur_prod_layout[i].prod_iso->graphed_left, NODE_WIDTH );
				cur_sizes[0].h = (int)node_get( (Node)cur_prod_layout[i].prod_iso->graphed_left, NODE_HEIGHT );
			}
			else
			{
				nr_of_sizes = get_nr_of_possible_sizes(&cur_prod_layout[i]);
				cur_sizes = create_lp_sizes_array( nr_of_sizes );

				for( j = 0; j < nr_of_sizes; j++ )
				{
					cur_sizes[j].upper_prod_array = cur_prod_layout;
					cur_sizes[j].entry_in_upper_prod_array = i;
				}

				cur_first_entry = 0;		/****** Ab welchem array sollen die jetzt generierten Eintraege gemacht werden ******/

				FOR_LP_LOWER_DERIVATION( cur_prod_layout[i].derivations_below, cur_lower_der )
				{
					cur_first_entry = make_entries_for_all_possible_combinations( cur_first_entry, cur_lower_der,
												      cur_sizes, &cur_prod_layout[i] );
				}
				END_FOR_LP_LOWER_DERIVATION( cur_prod_layout[i].derivations_below, cur_lower_der );
			}
			cur_prod_layout[i].SIZES = clear_non_optimal_sizes( cur_sizes, &nr_of_sizes );	/****** Optimierung durchfuehren ******/
												/****** nr_of_sizes wird veraendert ******/
			cur_prod_layout[i].length_of_sizes = nr_of_sizes;
		}
	}
	END_FOR_LP_UPPER_PRODUCTION( upper_production, cur_upper_production );
}

/*****************************************************************************************
function:	optimal_sizes_for_tree
Input:	LP_Parsing_element head

	Durchlaeuft pars_table bottom up und ruft jeweils Funktion auf um SIZES auszurechnen
*****************************************************************************************/

void	optimal_sizes_for_tree(LP_Parsing_element head)
{
	LP_Derivation_list	cur_der_list;
	LP_Parsing_element_list	cur_pe_list;

	FOR_LP_DERIVATIONS( head->derivations, cur_der_list )
	{
		FOR_LP_PARSING_ELEMENTS( cur_der_list->derivation->parsing_elements, cur_pe_list )
		{
			optimal_sizes_for_tree( cur_pe_list->pe );
		}
		END_FOR_LP_PARSING_ELEMENTS( cur_der_list->derivation->parsing_elements, cur_pe_list );
	}
	END_FOR_LP_DERIVATIONS( head->derivations, cur_der_list );

	if( head->layout_structures && !head->layout_structures->production_layouts[0].SIZES )
	{
		create_optimal_sizes( head->layout_structures );
	}
}

/*****************************************************************************************
function:	make_entry_at_cur_optimal_sizes_array
Input:	LP_sizes_array opt_sizes int which_entry

	Traegt bei dem Uebergebenem opt_sizes[which_entry] bei used_derivation die dependency
	graphen ein, mit denen optimal gezeichnet werden kann
*****************************************************************************************/

void	make_entry_at_cur_optimal_sizes_array(LP_sizes_array opt_sizes, int which_entry)
{
	int				i, j;
	LP_sizes_array			cur_lower_sizes;
	LP_sizes_ref			cur_sizes_ref;
	LP_lower_derivation		lower_der;
	LP_array_of_lower_productions	cur_lower_array;

	/****** durchlaufe array of lower derivation und trage dependency_graph ein ******/
		lower_der	= opt_sizes[which_entry].used_derivation;
		cur_lower_array	= lower_der->productions;

		for( i = 0; i < lower_der->number_of_productions; i++ )
		{
			if( cur_lower_array[i].same_prod_lower )
			{
				/****** jetzt muss erst der richtige dependency_graph gesucht und dann kopiert werden ******/
				cur_lower_sizes = cur_lower_array[i].same_prod_lower->production_layouts[cur_lower_array[i].array_entry].SIZES;

				cur_sizes_ref = opt_sizes[which_entry].used_productions;
				for( j = 0; j < opt_sizes[which_entry].nr_of_prods_below; j++ )
				{
					if( cur_lower_sizes == cur_sizes_ref[j].which_sizes_array )
					{
						/****** Wir haben es gefunden ******/
						cur_lower_array[i].x_dependency = 
								cur_sizes_ref[j].which_sizes_array[cur_sizes_ref[j].what_entry_in_array].x_dependency;
						cur_lower_array[i].y_dependency = 
								cur_sizes_ref[j].which_sizes_array[cur_sizes_ref[j].what_entry_in_array].y_dependency;
						break;
					}
				}
			}
		}
}

/*****************************************************************************************
function:	copy_dependency_for_lower_part_of_table
Input:	LP_sizes_array opt_sizes int which_enrty

	Beim Zeichnen wird im Normalfall von LP_lower_derivation aus gezeichnet. Um dies
	moeglichst Problemlos durchfuehren zu koennen wird hier der Dependency_graph ein-
	getragen, der zum Layoutoptimalen zeichnen notwendig ist.
	Muss nach Berechnung von COSTS erfolgen.
	Rekursiver Teil.
*****************************************************************************************/

void	copy_dependency_for_lower_part_of_table(LP_sizes_array opt_sizes, int which_entry)
{
	int		i;
	LP_sizes_ref	cur_ref;

	/****** Was passieren kann: es koennen sizes_ref auftreten, die einen Zeiger auf NULL haben (Wenn unterhalb nichts mehr ist, ******/
	/****** aber lower_prod existiert trotzdem ). So kann beim erzeugen der sizes_ref schneller gearbeitet werden ******/
	if( opt_sizes )
	{
		make_entry_at_cur_optimal_sizes_array( opt_sizes, which_entry );

		if( opt_sizes[which_entry].used_productions )
		{
			cur_ref = opt_sizes[which_entry].used_productions;

			for( i = 0; i < opt_sizes[which_entry].nr_of_prods_below; i++ )
			{
				copy_dependency_for_lower_part_of_table( cur_ref[i].which_sizes_array, cur_ref[i].what_entry_in_array );
			}
		}
	}
}

/*****************************************************************************************
function:	copy_dependency_graph_to_lower_array
Input:	LP_Parsing_element	head

	Beim Zeichnen wird im Normalfall von LP_lower_derivation aus gezeichnet. Um dies
	moeglichst Problemlos durchfuehren zu koennen wird hier der Dependency_graph ein-
	getragen, der zum Layoutoptimalen zeichnen notwendig ist.
	Muss nach Berechnung von COSTS erfolgen
*****************************************************************************************/

void	copy_dependency_graph_to_lower_array(LP_Parsing_element head)
{
	int				i, j,
					cur_optimal_size,
					cur_size,
					opt_sizes_array_nr;
	LP_upper_production		cur_upper;
	LP_array_of_productions		cur_upper_array;
	LP_sizes_array			cur_sizes,
					opt_sizes;

	cur_optimal_size = UNDEF;

	/****** Suche nach einer Ableitung, die eine optimale Groesse des Graphen erlaubt ******/

		FOR_LP_UPPER_PRODUCTION( head->layout_structures, cur_upper )
		{
			cur_upper_array = cur_upper->production_layouts;
			for( i = 0; i < cur_upper->number_of_prod_layouts; i++ )
			{
				cur_sizes = cur_upper_array[i].SIZES;
				for( j = 0; j < cur_upper_array[i].length_of_sizes; j++ )
				{
					if( ((cur_size = cur_sizes[j].w * cur_sizes[j].h) < cur_optimal_size) ||
					    (cur_optimal_size == UNDEF) )
					{
						cur_optimal_size	= cur_size;
						opt_sizes_array_nr	= j;
						opt_sizes		= cur_sizes;
					}
				}
			}
		}
		END_FOR_LP_UPPER_PRODUCTION( head->layout_structures, cur_upper );


	make_entry_at_cur_optimal_sizes_array( opt_sizes, opt_sizes_array_nr );

	/****** rekursive Funktion fuer Rest vom Baum + eintragen ******/
	copy_dependency_for_lower_part_of_table( opt_sizes, opt_sizes_array_nr );

}
