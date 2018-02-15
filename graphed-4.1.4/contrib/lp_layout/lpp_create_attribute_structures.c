#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpp_attributes.h"
#include "lpp_create_attribute_structures.h"

/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/******															******/
/******															******/
/****** In diesem Modul befinden sich die Funktionen, die die Datenstrukturen zur Attributberechnung beim Parsing	******/
/****** generieren.													******/
/******															******/
/******															******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/

/******************************************************************************************************************************
function:	create_edge_down
Input:	Attributes_ref ref, Attributes_head head

	Legt von ref aus den Zeiger 'same_prod_lower' auf die erste Reihe von attributes_ref_list aus
******************************************************************************************************************************/

void	create_edge_down(Attributes_ref ref, Attributes_head head)
{
	Graph			searched_prod	= ref->lower_prod;
	Attributes_ref_list	attr_list_below;
	Attributes_ref_list	cur_attr_list;

	/****** Wenn das lpp_Parsing_element, das unten abgeleitet wurde, Datenstrukturen zur Attributberechnung besitzt	******/
	/****** NICHT der Fall, wenn darunter nichts mehr abgeleitet wurde						******/
	if( head->lower_edge->attributes_table_down )
	{
		attr_list_below = head->lower_edge->attributes_table_down->upper_productions;

		/****** Schaue unten alle Attributes_ref_list durch, bis das kommt, das gleiche Produktion repraesentiert ****/
		for_attributes_ref_list( attr_list_below, cur_attr_list)
		{
			if( cur_attr_list->refs_between_prods->upper_prod == searched_prod )
			{
				ref->same_prod_lower = cur_attr_list;
			}
		}
		end_for_attributes_ref_list( attr_list_below, cur_attr_list);
	}
}

/*****************************************************************************************************************************/
/******************************************************************************************************************************
function:	find_same_prod
Input:	Attributes_head head, Graph prod

	Sucht unter head diejenige Attributes_ref_list a, die die Produktion prod repraesentiert
	Wird benutzt, um Zeiger 'same_upper_prod_in_next' anzulegen (-->create_same_upper_prod_in_next)

Output:	a
******************************************************************************************************************************/

Attributes_ref_list	find_same_prod(Attributes_head head, Graph prod)
{
	Attributes_ref_list	attr_ref_list;

	for_attributes_ref_list( head->upper_productions, attr_ref_list )
	{
		if( attr_ref_list->refs_between_prods->upper_prod == prod )
		{
			return( attr_ref_list );
		}
	}
	end_for_attributes_ref_list( head->upper_productions, attr_ref_list );
	return NULL;
}

/******************************************************************************************************************************
function:	create_same_upper_prod_in_next
Input:	Derivation derivation

	Erzeugt bei den Datenstrukturen attributes_ref_list den Zeiger same_upper_prod_in_next.
	Gehe dazu durch alle isomorphen Produktionen fuer erste Kante unten und suche die entsprechende Produktion
	in allen nachfolgenden Datenstrukturen fuer die restlichen Kanten unten.
	Kante unten soll heissen: Datenstrukturen werden zwischen je zwei aufeinanderfolgenden Ableitungen oben und unten 
		(in Theorie: Kante e') angelegt
******************************************************************************************************************************/

void	create_same_upper_prod_in_next(Derivation derivation)
{
	Attributes_head		head;
	Attributes_ref_list	attr_ref_list;

	if( derivation->attributes_table_down )
	{
		/****** Gehe in erster Datenstruktur zwischen zwei Kanten durch obige Produktionen			******/
		for_attributes_ref_list( derivation->attributes_table_down->upper_productions, attr_ref_list )
		{
			/****** Gehe die naechsten Datenstrukturen zwischen je zwei Kanten durch ...			******/
			for_attributes_head_from_top( derivation->attributes_table_down->next_for_upper, head )
			{
				/****** ... und suche dort gleiche Produktion						******/
				add_to_same_upper_prod_in_next( attr_ref_list, 
					find_same_prod(head, attr_ref_list->refs_between_prods->upper_prod) );
			}
			end_for_attributes_head_from_top( derivation->attributes_table_down->next_for_upper, head );
		}
		end_for_attributes_ref_list( derivation->attributes_table_down->upper_productions, attr_ref_list );
	}
}

/******************************************************************************************************************************
function	create_data_structures_for_attributes_rekursiv_part
Input:		lpp_Parsing_element	root

	Es werden die fuer die Attributberechnung notwendigen Datenstrukturen in den derivation_table aufgenommen
	( rekursiv,  bottom_up )

Output: TRUE iff. datenstrukturen wurden erzeugt
	FALSE iff. nichts wurde erzeugt
******************************************************************************************************************************/

int	create_data_structures_for_attributes_rekursiv_part(lpp_Parsing_element derivation_table_root)
{
	Set_of_parsing_elements		cur_set_elem;
	Derivation			cur_upper_derivation, cur_lower_derivation;
	lpp_Parsing_element			cur_pe;
	int				is_same;		/****** ist Marke dafuer, ob Kante unten (e') von	******/
								/****** selbem lpp_Parsing_element stammt			******/

	Graph				cur_upper_prod;
	Graph				cur_lower_prod;

	Attributes_head			head;
	Attributes_ref_list		attributes_list;
	Attributes_ref			attributes;

	int				something_created	= FALSE;


	/****** falls ueberhaupt lpp_Parsing_element uebergeben wurde							******/
	if( derivation_table_root )
	{
		/****** Gehe durch alle Ableitungen oben								******/
		/****** cur_upper_derivation entspricht Kante e in Theorie Seite 4 ff.					******/
		for_derivation( derivation_table_root->derivations, cur_upper_derivation )
		{
			for_set_of_parsing_elements( cur_upper_derivation->derivation_nodes, cur_set_elem )
			{
				cur_pe = cur_set_elem->pe;

				/****** Der REKURSIVE Aufruf, um nicht nur eine Stufe anzulegen				******/
				/****** Erfolgt falls: pe selbst Nonterminales war und damit abgeleitet wurde		******/
				/******		       funktion an diesem pe noch nicht ausgefuehrt wurde		******/
				/****** (Man kann im Table an eine Stelle i.a. auf mehrere Arten kommen)		******/

				if( cur_pe->derivations && !cur_pe->derivations->attributes_table_down )
				{
					something_created = ( create_data_structures_for_attributes_rekursiv_part(cur_pe) ||
							      something_created );
				}

				is_same = FALSE;
				/****** cur_lower_derivation entspricht Kante e' in Theorie Seite 4 ff.			******/
				for_derivation( cur_pe->derivations, cur_lower_derivation )
				{
					/****** Lege Datenstruktur 'attributes_head' an	und haenge sie an table		******/
					head				= new_attributes_head();
					head->is_same			= is_same;
					head->upper_edge		= cur_upper_derivation;
					head->lower_edge		= cur_lower_derivation;
					head->below_derivated_set	= cur_set_elem;

					cur_upper_derivation->attributes_table_down = 
						add_to_attributes_head_from_top( cur_upper_derivation->attributes_table_down, head );
/***					cur_lower_derivation->attributes_table_up = 
						add_to_attributes_head_from_bottom( cur_lower_derivation->attributes_table_up, head );***/

					is_same = TRUE;

					something_created = TRUE;

					/****** Lege Datenstrukturen 'Attributes_ref_list'  an	******/
					for_graph_multi_suc( cur_upper_derivation->used_prod, cur_upper_prod )
					{
						attributes_list 	= new_attributes_ref_list();

						head->upper_productions = 
							add_to_attributes_ref_list( head->upper_productions, attributes_list );
						attributes_list->attr_head = head;

						/****** Lege Datenstrukturen `Attributes_ref' an			******/
						/****** BEACHTE: wird also "ganz unten" NICHT angelegt			******/
						/******		 (da keine "Soehne" mehr vorhanden sind			******/
						for_graph_multi_suc( cur_lower_derivation->used_prod, cur_lower_prod )
						{
							attributes		= new_attributes_ref();
							attributes->upper_prod	= cur_upper_prod;
							attributes->lower_prod	= cur_lower_prod;
							attributes->attr_list	= attributes_list;

							create_edge_down( attributes, head );

							attributes_list->refs_between_prods = 
								add_to_attributes_ref( attributes_list->refs_between_prods, attributes );
						}
						end_for_graph_multi_suc( cur_lower_derivation->used_prod, cur_lower_prod );
					}
					end_for_graph_multi_suc( cur_upper_derivation->used_prod, cur_upper_prod );
				}
				end_for_derivation( cur_pe->derivations, cur_lower_derivation );
			}
			end_for_set_of_parsing_elements( cur_upper_derivation->derivation_nodes, cur_set_elem );
		}
		end_for_derivation( derivation_table_root->derivations, cur_upper_derivation );
	}

	return( something_created );
}


/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/****** Hauptfunktion, die von ausserhalb aufgerufen wird.								******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/

/******************************************************************************************************************************
function	create_data_structures_for_attributes
Input:		lpp_Parsing_element	root

	Es werden die fuer die Attributberechnung notwendigen Datenstrukturen in den derivation_table aufgenommen.
	Zweiteilung stellt sicher, dass fuer Wurzel IMMER Datenstrukturen angelegt werden, auch wenn unten nichts mehr
	abgeleitet wurde.

Output: ------
******************************************************************************************************************************/

void	create_data_structures_for_attributes(lpp_Parsing_element derivation_table_root)
{
	Derivation		cur_derivation;
	Set_of_parsing_elements	cur_set_elem;
	Attributes_head		head;
	Graph			cur_prod;
	Attributes_ref_list	attributes_list;
	Attributes_ref		attributes;


	/****** Es wurde nichts erzeugt, also Extrabehandlung fuer Wurzel						******/
	if( !create_data_structures_for_attributes_rekursiv_part(derivation_table_root) )
	{
		if( derivation_table_root )
		{
			for_derivation( derivation_table_root->derivations, cur_derivation )
			{
				/****** Lege Datenstruktur 'attributes_head' an	und haenge sie an table			******/
				head				= new_attributes_head();
				head->upper_edge		= cur_derivation;

				head->below_derivated_set	= cur_set_elem;

				cur_derivation->attributes_table_down = 
					add_to_attributes_head_from_top( cur_derivation->attributes_table_down, head );

				/****** Lege Datenstrukturen 'Attributes_ref_list'  an	******/
				for_graph_multi_suc( cur_derivation->used_prod, cur_prod )
				{
					attributes_list 	= new_attributes_ref_list();

					head->upper_productions = 
						add_to_attributes_ref_list( head->upper_productions, attributes_list );
					attributes_list->attr_head = head;

					/****** Lege Datenstrukturen `Attributes_ref' an				******/
					attributes		= new_attributes_ref();
					attributes->upper_prod	= cur_prod;
					attributes->attr_list	= attributes_list;

					attributes_list->refs_between_prods = 
						add_to_attributes_ref( attributes_list->refs_between_prods, attributes );

				}
				end_for_graph_multi_suc( cur_derivation->used_prod, cur_prod );
			}
			end_for_derivation( derivation_table_root->derivations, cur_derivation );
		}
	}
}


/******************************************************************************************************************************
function	create_same_upper_prod_in_next_for_tree
Input:	lpp_Parsing_element	head

	Legt rekursiv die Zeiger 'same_upper_prod_in_next' an
	Laeuft dazu den table durch ruft fuer jede Datenstruktur Derivation die Funktion 'create_same_upper_prod_in_next' auf

Output:	---
******************************************************************************************************************************/

void	create_same_upper_prod_in_next_for_tree(lpp_Parsing_element head)
{
	Derivation		cur_der;
	Set_of_parsing_elements	cur_set;

	if( head->derivations )
	{
		for_derivation( head->derivations, cur_der )
		{
			for_set_of_parsing_elements( cur_der->derivation_nodes, cur_set )
			{
				create_same_upper_prod_in_next_for_tree( cur_set->pe );
			}
			end_for_set_of_parsing_elements( cur_der->derivation_nodes, cur_set );

			if( cur_der->attributes_table_down )
			{
				if( !cur_der->attributes_table_down->upper_productions->same_upper_prod_in_next )
				{
					create_same_upper_prod_in_next( cur_der );
				}
			}
		}
		end_for_derivation( head->derivations, cur_der );
	}
}
