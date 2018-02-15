#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lpp_attributes.h"

/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/******															******/
/******															******/
/****** In diesem Modul befinden sich die Grundfunktionen auf die Datenstrukturen zur Attributberechnung beim Parsing	******/
/******															******/
/******															******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/



/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/******															******/
/****** Funktionen auf 'struct attributes_head'										******/
/******															******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/


/******************************************************************************************************************************
function	new_attributes_head
Input:	------

	Anforderung von Speicherplatz, alle Zeiger auf NULL vorinitialisieren

Output: Zeiger auf angeforderten Speicherplatz
******************************************************************************************************************************/

Attributes_head		new_attributes_head(void)
{
	Attributes_head	new = (Attributes_head)mymalloc( sizeof(struct attributes_head) );

	new->is_same			= TRUE;
	new->upper_edge			= NULL;
	new->lower_edge			= NULL;
	new->next_for_upper		= NULL;
	new->next_for_lower		= NULL;
	new->upper_productions		= NULL;
	new->optimal_set		= NULL;
	new->below_derivated_set	= NULL;

	return( new );
}

/******************************************************************************************************************************
function	add_to_attributes_head_from_top
Input:	Atributes_head list, new

	Haenge list an new bzgl. Zeiger der fuer die Verzeigerung von "->attributes_table_down" zustaendig ist 
	BEACHTE: VON AUFRUFENDER FUNKTION HINTEN ANHAENGEN ZWINGEND VORAUSGESETZT

Output: new
******************************************************************************************************************************/

Attributes_head		add_to_attributes_head_from_top(Attributes_head list, Attributes_head new)
{
	Attributes_head	cur = list;

	if( cur )
	{
		while( cur->next_for_upper )
		{
			cur = cur->next_for_upper;
		}
		cur->next_for_upper = new;

		return( list );
	}

	return( new );
}

/******************************************************************************************************************************
function	add_to_attributes_head_from_bottom
Input:	Atributes_head list, new

	Haenge list an new bzgl. Zeiger der fuer die Verzeigerung von "->attributes_table_up" zustaendig ist 

Output: new
******************************************************************************************************************************/

Attributes_head		add_to_attributes_head_from_bottom(Attributes_head list, Attributes_head new)
{
	new->next_for_lower = list;

	return( new );
}


/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/******															******/
/****** Funktionen auf 'struct attributes_ref_list'									******/
/******															******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/


/******************************************************************************************************************************
function	new_attributes_ref_list
Input:	------

	Anforderung von Speicherplatz, alle Zeiger auf NULL vorinitialisieren

Output: Zeiger auf angeforderten Speicherplatz
******************************************************************************************************************************/

Attributes_ref_list	new_attributes_ref_list(void)
{
	Attributes_ref_list	new = (Attributes_ref_list)mymalloc( sizeof(struct attributes_ref_list) );

	new->is_same			= TRUE;
	new->refs_between_prods		= NULL;
	new->optimal_attributes_ref	= NULL;
	new->next			= NULL;
	new->little_c_star		= 0;
	new->c_0			= 0;
	new->big_c_star			= 0;
	new->same_upper_prod_in_next	= NULL;
	new->graphed_iso		= NULL;

	return( new );
}

/******************************************************************************************************************************
function	add_to_attributes_ref_list
Input:	Attributes_ref_list list, new

	Haenge list an new

Output: Zeiger auf new
******************************************************************************************************************************/

Attributes_ref_list	add_to_attributes_ref_list(Attributes_ref_list list, Attributes_ref_list new)
{
	new->next	= list;

	return( new );
}

/******************************************************************************************************************************
function	add_to_same_upper_prod_in_next
Input:	Attributes_ref_list first, new

	haenge new an die Liste hinter first bzgl. des Zeigers same_upper_prod_in_next
******************************************************************************************************************************/

void	add_to_same_upper_prod_in_next(Attributes_ref_list first, Attributes_ref_list new)
{
	Attributes_ref_list	cur_list_elem = first;

	while( cur_list_elem->same_upper_prod_in_next )
	{
		cur_list_elem = cur_list_elem->same_upper_prod_in_next;
	}

	cur_list_elem->same_upper_prod_in_next	= new;
}

/*****************************************************************************************************************************/
/*****************************************************************************************************************************/
/******															******/
/****** Funktionen auf 'struct attributes_ref'										******/
/******															******/
/*****************************************************************************************************************************/
/*****************************************************************************************************************************/


/******************************************************************************************************************************
function	new_attributes_ref
Input:	------

	Anforderung von Speicherplatz, alle Zeiger auf NULL vorinitialisieren

Output: Zeiger auf angeforderten Speicherplatz
******************************************************************************************************************************/

Attributes_ref		new_attributes_ref(void)
{
	Attributes_ref	new = (Attributes_ref)mymalloc( sizeof(struct attributes_ref) );

	new->upper_prod		= NULL;
	new->lower_prod		= NULL;
	new->next		= NULL;
	new->little_c		= UNDEFINED;
	new->same_prod_lower	= NULL;
	new->next_optimal_ref	= NULL;

	return( new );
}

/******************************************************************************************************************************
function	add_to_attributes_ref
Input:	Attributes_ref list, cur

	Haenge list an cur

Output: Zeiger auf cur
******************************************************************************************************************************/

Attributes_ref		add_to_attributes_ref(Attributes_ref list, Attributes_ref cur)
{
	cur->next = list;

	return( cur );
}
