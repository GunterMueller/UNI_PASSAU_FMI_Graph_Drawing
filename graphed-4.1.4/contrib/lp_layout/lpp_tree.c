#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include <string.h>
#include "user_header.h"

#include "lpp_tree.h"

/*************************************************************************
function	get_optimal_attributes_ref
Input:	Attributes_ref_list head

	Sucht denjenigen Attributes_ref mit den optimalen Werten, und legt
	einen Zeiger von demjenigen Attributes_ref_list auf dieses 
	Datenelement an, der in der 1. 

Output:	---
*************************************************************************/

void		get_optimal_attributes_ref(Attributes_ref_list head)
{
	Attributes_ref_list	cur_attr_list;
	Attributes_ref		cur_attr_ref;
	Attributes_ref		optimal_ref;
	Attributes_ref		last_optimal_ref	= NULL;

	/*** Suchen der optimalen Attributes_ref_list									***/

	optimal_ref = head->refs_between_prods;


		for_same_upper_production( head, cur_attr_list )
		{
			/*** Wenn neuer Abgeleiteter Knoten unten, dann beginnt Zaehlung neu				***/
			if( !cur_attr_list->attr_head->is_same )
			{
				optimal_ref = cur_attr_list->refs_between_prods;
			}

			/*** Suche nach dem optimalen Attributes_ref							***/
			for_attributes_ref( cur_attr_list->refs_between_prods, cur_attr_ref )
			{
				if( cur_attr_ref->same_prod_lower )
				{
					if( (optimal_ref->little_c + optimal_ref->same_prod_lower->big_c_star) > 
				  	    (cur_attr_ref->little_c + cur_attr_ref->same_prod_lower->big_c_star) )
					{
						optimal_ref = cur_attr_ref;
					}
				}
				else
				{
					if( optimal_ref->little_c  > cur_attr_ref->little_c )
					{
						optimal_ref = cur_attr_ref;
					}
				}
			}
			end_for_attributes_ref( cur_attr_list->refs_between_prods, cur_attr_ref );

			/*** Hier muss ein optimal_attributes_ref eingetragen werden					***/
			if( !cur_attr_list->same_upper_prod_in_next ||
			    ( cur_attr_list->same_upper_prod_in_next && !cur_attr_list->same_upper_prod_in_next->attr_head->is_same ) 	
			  )
			{
				/*** Hier wird der erste optimal_ref eingetragen					***/
		  		if( last_optimal_ref != NULL )
				{
					last_optimal_ref->next_optimal_ref = optimal_ref;
					last_optimal_ref = optimal_ref;
				}

				/*** hier wird an bereits vorhandene angehaengt						***/
				if( last_optimal_ref == NULL )
				{
					head->optimal_attributes_ref = optimal_ref;
					last_optimal_ref = optimal_ref;
				}
			}

		}
		end_for_same_upper_production( head, cur_attr_list );


}
