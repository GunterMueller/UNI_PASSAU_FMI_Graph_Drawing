#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lpp_tree.h"
#include "lpp_2_pass.h"


/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/****** Modul fuer den zweiten Berechnungsschritt beim Layout durch Parsing						******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/

/******************************************************************************************************************************
function:	min_of_little_c
Input:	Attributes_ref_list attr_list

Output:	Min ueber die little_c, die in den zu attr_list gehoerenden attributes_ref eingetragen sind
******************************************************************************************************************************/

int	min_of_little_c_plus_C(Attributes_ref_list attr_list)
{
	Attributes_ref	cur_attr_ref;
	int		sum;
	int		result		= attr_list->refs_between_prods->little_c;


	if( attr_list->refs_between_prods->same_prod_lower )
	{
		result += attr_list->refs_between_prods->same_prod_lower->big_c_star;
	}

	for_attributes_ref( attr_list->refs_between_prods->next, cur_attr_ref )
	{
		sum = cur_attr_ref->little_c;

		if( cur_attr_ref->same_prod_lower )
		{
			sum += cur_attr_ref->same_prod_lower->big_c_star;
		}

		if( result > sum )
		{
			result = sum;
		}
	}
	end_for_attributes_ref( attr_list->refs_between_prods->next, cur_attr_ref );

	return( result );
}

/******************************************************************************************************************************
function:	lpp_2_pass
Input:	lpp_Parsing_element head

	Berechnet, wie in Hickl, Hierarchical Graph Design Seite 11 ff. angegeben, die Attribute
		c*
		set*
	in einem bottom up durchlauf
******************************************************************************************************************************/

void	lpp_2_pass(lpp_Parsing_element head)
{
	Derivation		cur_derivation;
	Set_of_parsing_elements	cur_set;
	Attributes_ref_list	cur_attr_list,
				same_prod,
				last;
	int			end_sum,
				current_sum,
				current_optimal_sum;


	for_derivation( head->derivations, cur_derivation )
	{
		for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set )
		{
			lpp_2_pass( cur_set->pe );
		}
		end_for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set );
	}
	end_for_derivation( head->derivations, cur_derivation );



	for_derivation( head->derivations, cur_derivation )
	{
		if( cur_derivation->attributes_table_down )
		{
			/****** fuer jede der isomorphen Produktionen zu berechnen					******/
			for_attributes_ref_list( cur_derivation->attributes_table_down->upper_productions, cur_attr_list )
			{
				/****** Berechnung des Attributs c*							******/
				end_sum = 0;

				for_same_upper_production( cur_attr_list, same_prod )
				{
					current_optimal_sum = min_of_little_c_plus_C( same_prod );

					do
					{
						current_sum = min_of_little_c_plus_C( same_prod );

						/****** ueberpruefen, ob das als optimal eingetragene auch optimal ist	******/
						if( current_optimal_sum > current_sum )
						{
							current_optimal_sum = current_sum;
						}

						/****** Durch das Makro muss nach innerer Schleife eins zurueck		******/
						last = same_prod;

						same_prod = same_prod->same_upper_prod_in_next;

					}
					while( same_prod && same_prod->attr_head->is_same );

					end_sum += current_optimal_sum;

					same_prod = last;
				}
				end_for_same_upper_production( cur_attr_list, same_prod );

				cur_attr_list->little_c_star	= end_sum;

				/****** Berechnung des Attributs C*							******/
				cur_attr_list->big_c_star	= cur_attr_list->little_c_star + cur_attr_list->c_0;
			}
			end_for_attributes_ref_list( cur_derivation->attributes_table_down->upper_productions, cur_attr_list );
		}
	}
	end_for_derivation( head->derivations, cur_derivation );
}

