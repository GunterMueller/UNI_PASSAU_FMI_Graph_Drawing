#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>
#include "lpp_functions.h"

#include "lpp_clear_table.h"

/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/******				Modul zum loeschen des Speichers fuer ein derivation_table				******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/

/******************************************************************************************************************************
function:	free_table
Input:	lpp_Parsing_element	head

	Freigeben des Speichers von head und aller anderen Datenstrukturen, die unterhalb liegen (rekursiv)
******************************************************************************************************************************/

void	free_table(lpp_Parsing_element head)
{
	Derivation		cur_derivation;
	Set_of_parsing_elements	cur_set;

	if( head )
	{
		for_derivation( head->derivations, cur_derivation )
		{
			for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set )
			{
				free_table( cur_set->pe );
				free_parsing_element( cur_set->pe );
				free( cur_set );
			}
			end_for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set );

			free( cur_derivation );
		}
		end_for_derivation( head->derivations, cur_derivation );

		free( head );
	}
}

/******************************************************************************************************************************
function:	mark_elements
Input:	lpp_Parsing_element start

	Markiert alle lpp_Parsing_elemente und alle Derivations, die von start aus erreichbar sind
******************************************************************************************************************************/

void	mark_elements(lpp_Parsing_element start)
{
	Derivation		cur_derivation;
	Set_of_parsing_elements	cur_set;

	for_derivation( start->derivations, cur_derivation )
	{
		for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set )
		{
			cur_set->pe->is_in_table = TRUE;
			mark_elements( cur_set->pe );
		}
		end_for_set_of_parsing_elements( cur_derivation->derivation_nodes, cur_set );

		cur_derivation->is_in_table = TRUE;
	}
	end_for_derivation( start->derivations, cur_derivation );
}

/******************************************************************************************************************************
function:	clean_derivation_table
Input:	lpp_Parsing_element	start, Set_of_parsing_elements list

	Es werden diejenigen lpp_Parsing_elemente geloescht, die nicht bis zum Startsymbol zurueckgefuehrt werden konnten
******************************************************************************************************************************/

void	clean_derivation_table(lpp_Parsing_element start, Set_of_parsing_elements list)
{
	Set_of_parsing_elements	cur_set;
	Set_of_parsing_elements	to_delete;

	/****** Markieren aller Elemente, die noch benoetigt werden							******/
	if( start )
	{
		mark_elements( start );
	}
	/****** Loeschen aller unmarkierten Elemente									******/
	cur_set = list;
	while( cur_set )
	{
		if( !cur_set->pe->is_in_table )
		{
			free_parsing_element( cur_set->pe );
		}

		to_delete = cur_set;
		cur_set = cur_set->next;
		free( to_delete );	
	}
}

			
