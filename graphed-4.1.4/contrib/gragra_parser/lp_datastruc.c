#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include "misc.h"
#include "types.h"

#include "lp_cost_struc.h"
#include "lp_datastruc.h"


/****************************************************************************************/
/*											*/
/*	File fuer grundlegende Funktionen auf Datenstrukturen in lp_datastructures.h	*/
/*											*/
/****************************************************************************************/

/****************************************************************************************/
/*		 LP_Parsing_element / LP_Parsing_element_list				*/
/****************************************************************************************/

/*****************************************************************************************
function:	mem_copy_string_wa
Input:	char* string

	Erzeugt eine Kopie von string

Output:	Kopie

*****************************************************************************************/

char*	mem_copy_string_wa(char *string)
{
	int	string_length;
	char*	result;

	if( !string )
	{
		return( NULL );
	}

	string_length = strlen( string );

	result = (char*)mymalloc( string_length );

	result = strcpy( result, string );

	return( result );
}

/*****************************************************************************************
function:	create_LP_parsing_element
Input:	---

	Erzeugt Speicherplatz fuer struct LP_parsing_element p

Output:	Zeiger auf p
*****************************************************************************************/

LP_Parsing_element	create_LP_parsing_element(void)
{

	LP_Parsing_element	new = (LP_Parsing_element)mymalloc( sizeof(struct LP_parsing_element) );

	new->derivations	= NULL;
	new->label		= NULL;
	new->layout_structures	= NULL;
	new->width		= 0;
	new->height		= 0;
	new->graph_iso		= NULL;
	new->prod_iso		= NULL;
	new->w			= 0;
	new->h			= 0;

	return( new );
}

/*****************************************************************************************
function:	LP_parsing_element_SET_DERIVATIONS
Input:	LP_Parsing_element pe, LP_Derivation_list derivations

	setzt pe->derivations
*****************************************************************************************/
	
void	LP_parsing_element_SET_DERIVATIONS(LP_Parsing_element pe, LP_Derivation_list derivations)
{
	pe->derivations	= derivations;
}

/*****************************************************************************************
function:	LP_parsing_element_GET_DERIVATIONS
Input:	LP_Parsing_element pe

Output:	pe->derivations
*****************************************************************************************/
	
LP_Derivation_list	LP_parsing_element_GET_DERIVATIONS(LP_Parsing_element pe)
{
	return( pe->derivations );
}

/*****************************************************************************************
function:	free_lp_parsing_element_with_lower_part
Input:	LP_Parsing_element pe

	Loescht Speicherplatz von pe und darunterliegende Datenstrukturen
*****************************************************************************************/

void	free_lp_parsing_element_with_lower_part(LP_Parsing_element pe)
{
	if( pe )
	{
		if( pe->layout_structures )
		{
			free_lp_upper_production_with_layouts_and_lower_derivation( pe->layout_structures );
			pe->layout_structures = NULL;
		}
		free_lp_der_list_with_lower_part( LP_parsing_element_GET_DERIVATIONS(pe));
		pe->derivations = NULL;
		free( pe );
	}
}

/************************************************************************************************/
/************************************************************************************************/
/*****************************************************************************************
function:	create_lp_pe_list
Input:	---

	Erzeugt Speicherplatz fuer Datenstruktur LP_Parsing_element_list p

Output:	Zeiger auf p
*****************************************************************************************/

LP_Parsing_element_list	create_lp_pe_list(void)
{
	LP_Parsing_element_list	new = (LP_Parsing_element_list)mymalloc( sizeof(struct LP_parsing_element_list) );

	new->pe		= NULL;
	new->prod_iso	= NULL;
	new->pre	= new;
	new->suc	= new;

	return( new );
}

/*****************************************************************************************
function:	lp_pe_list_SET_PE
Input:	LP_Parsing_element_list pe_list, LP_Parsing_element pe

	Setzt pe_list->pe
*****************************************************************************************/

void	lp_pe_list_SET_PE(LP_Parsing_element_list pe_list, LP_Parsing_element pe)
{
	pe_list->pe = pe;
}

/*****************************************************************************************
function:	lp_pe_list_GET_PE
Input:	LP_Parsing_element_list pe_list

Output:	pe_list->pe
*****************************************************************************************/

LP_Parsing_element	lp_pe_list_GET_PE(LP_Parsing_element_list pe_list)
{
	return( pe_list->pe );
}

/*****************************************************************************************
function:	LP_pe_list_SET_PROD_ISO
Input:	LP_Parsing_element_list pe_list, Snode node

	setzt pe_list->prod_iso
*****************************************************************************************/
	
void	LP_pe_list_SET_PROD_ISO(LP_Parsing_element_list pe_list, Snode node)
{
	pe_list->prod_iso	= node;
}

/*****************************************************************************************
function:	LP_pe_list_GET_PROD_ISO
Input:	LP_Parsing_element_list pe_list

Output:	pe_list->prod_iso
*****************************************************************************************/
	
Snode	LP_pe_list_GET_PROD_ISO(LP_Parsing_element_list pe_list)
{
	return( pe_list->prod_iso );
}

/*****************************************************************************************
function:	create_lp_pe_list_with_pe
Input:	LP_Parsing_element pe

	Erzeugt Speicherplatz fuer LP_parsing_element_list p und setzt Zeiger pe

Output:	Zeiger auf p
*****************************************************************************************/

LP_Parsing_element_list	create_lp_pe_list_with_pe(LP_Parsing_element pe)
{
	LP_Parsing_element_list	new = create_lp_pe_list();

	lp_pe_list_SET_PE( new, pe );

	return( new );
}

/*****************************************************************************************
function:	add_pe_list_to_pe_list
Input:	LP_Parsing_element_list old_list, new

	Haengt new hinten an old_list an (Funktioniert auch fuer zwei Listen)

Output:	Zeiger auf 1. Listenelement
*****************************************************************************************/

LP_Parsing_element_list	add_pe_list_to_pe_list(LP_Parsing_element_list old_list, LP_Parsing_element_list new)
{
	LP_Parsing_element_list	last;

	if( !old_list )
	{
		return( new );
	}


	last = old_list->pre;


	new->pre->suc		= old_list;
	last->suc		= new;
	old_list->pre		= new->pre;
	new->pre		= last;

	return( old_list );
}

/*****************************************************************************************
function:	add_pe_to_pe_list
Input:	LP_Parsing_element_list old_list, LP_Parsing_element new

	Haengt new hinten an old_list an. (Vorher Speicheranforderung fuer
	lpr_Parsing_element_list )

Output:	Zeiger auf 1. Listenelement
*****************************************************************************************/

LP_Parsing_element_list	add_pe_to_pe_list(LP_Parsing_element_list old_list, LP_Parsing_element new)
{
	return( add_pe_list_to_pe_list(old_list, create_lp_pe_list_with_pe(new)) );
}

/*****************************************************************************************
function:	free_lp_pe_list
Input:	LP_Parsing_element_list list

	Loescht den Speicherplatz von list ( und allen Nachfolgern )
*****************************************************************************************/

void	free_lp_pe_list(LP_Parsing_element_list list)
{
	LP_Parsing_element_list	to_delete;

	list->pre->suc = NULL; /*Sichere Terminierung*/
	while( list )
	{
		to_delete = list;
		list = list->suc;

		free( to_delete );
	}
}

/*****************************************************************************************
function:	free_lp_pe_list_with_pe
Input:	LP_Parsing_element_list list

	Loescht den Speicherplatz von list ( und allen Nachfolgern ) und zugehoerige pe
*****************************************************************************************/

void	free_lp_pe_list_with_pe(LP_Parsing_element_list list)
{
	LP_Parsing_element_list	to_delete;

	list->pre->suc = NULL; /*Sichere Terminierung*/
	while( list )
	{
		to_delete = list;
		list = list->suc;

		free( to_delete->pe );
		to_delete->pe = NULL;
		free( to_delete );
	}
}

/*****************************************************************************************
function:	free_lp_pe_list_with_lower_part
Input:	LP_Parsing_element_list list

	Loescht den Speicherplatz von list ( und allen Nachfolgern ) und darunterliegende
	Datenstrukturen
*****************************************************************************************/

void	free_lp_pe_list_with_lower_part(LP_Parsing_element_list list)
{
	LP_Parsing_element_list	to_delete;

	if( list )
	{
		list->pre->suc = NULL; /*Sichere Terminierung*/
		while( list )
		{
			to_delete = list;
			list = list->suc;

			free_lp_parsing_element_with_lower_part( to_delete->pe );
			to_delete->pe = NULL;
			free( to_delete );
		}
	}
}

/****************************************************************************************/
/*		LP_Derivation / LP_Derivation_list					*/
/****************************************************************************************/


/*****************************************************************************************
function:	create_lp_derivation

	Erzeugt Speicherplatz fuer Datenstruktur LP_derivation p

Output: Zeiger auf p
*****************************************************************************************/

LP_Derivation	create_lp_derivation(void)
{
	LP_Derivation	new = (LP_Derivation)mymalloc( sizeof(struct LP_derivation) );

	new->parsing_elements	= NULL;
	new->used_prod		= NULL;
	new->lams_prod		= NULL;
	new->copy_iso		= NULL;
	new->visited 		= FALSE;

	return( new );
}

/*****************************************************************************************
function:	lp_derivation_SET_PARSING_ELEMENTS
Input:	LP_Derivation der, LP_Parsing_element_list pe_list

	Setzt der->parsing_elements
*****************************************************************************************/

void	lp_derivation_SET_PARSING_ELEMENTS(LP_Derivation der, LP_Parsing_element_list pe_list)
{
	der->parsing_elements = pe_list;
}

/*****************************************************************************************
function:	lp_derivation_GET_PARSING_ELEMENTS
Input:	LP_Derivation der

Output:	der->parsing_elements
*****************************************************************************************/

LP_Parsing_element_list	lp_derivation_GET_PARSING_ELEMENTS(LP_Derivation der)
{
	return( der->parsing_elements );
}

/*****************************************************************************************
function:	lp_derivation_SET_USED_PROD
Input:	LP_Derivation der, Sgraph prod

	Setzt der->used_prod
*****************************************************************************************/

void	lp_derivation_SET_USED_PROD(LP_Derivation der, Sprod prod)
{
	der->used_prod = prod;
}

/*****************************************************************************************
function:	lp_derivation_GET_USED_PROD
Input:	LP_Derivation der

Output:	der->used_prod
*****************************************************************************************/

Sprod	lp_derivation_GET_USED_PROD(LP_Derivation der)
{
	return( der->used_prod );
}

/*****************************************************************************************
function:	create_lp_derivation_with_pe_list
Input:	LP_Parsing_element_list list

	Erzeugt Speocherplatz fuer Datenstruktur LP_derivation p und setzt Zeiger 
	parsing_elements

Output:	Zeiger auf p
*****************************************************************************************/

LP_Derivation	create_lp_derivation_with_pe_list(LP_Parsing_element_list list)
{
	LP_Derivation	new = create_lp_derivation();

	lp_derivation_SET_PARSING_ELEMENTS( new, list );

	return( new );
}

/*****************************************************************************************
function:	free_lp_derivation_with_lower_part
Input:	LP_Derivation der

	Loescht Speicherplatz von der und darunterliegenden Datenstrukturen
*****************************************************************************************/

void	free_lp_derivation_with_lower_part(LP_Derivation der)
{
	if( der )
	{
		free_lp_pe_list_with_lower_part( lp_derivation_GET_PARSING_ELEMENTS(der) );
		der->parsing_elements = NULL;
		free( der );
	}
}

/*****************************************************************************************
function:	create_lp_derivation_of
Input:	PE_set parsing_elem_set

	Erzeugt aus Datenstruktur von Lamshoeft eine Kopie k in unserer Struktur

Output:	Zeiger auf k
	ACHTUNG: Falls Parsing_elem_set = NULL: Ruckgabe = NULL;
*****************************************************************************************/

LP_Derivation	create_lp_derivation_of(PE_set parsing_elem_set)
{
	PE_set			cur_set;
	LP_Derivation		new_der;
	LP_Parsing_element	new_pe;
	LP_Parsing_element_list	new_pe_list;

	if( parsing_elem_set )
	{
		new_der = create_lp_derivation();

		cur_set = parsing_elem_set;

		while( cur_set != NULL )
		{
			/****** Parsing_element kann in anderem Set bereits umkopiert sein; dann existiert aber copy_iso	   ******/
			/****** Also diesen abfragen und falls vorhanden Kopie in Gruppe aufnehmen				   ******/

			if( cur_set->pe->copy_iso )
			{
				new_pe_list	= create_lp_pe_list_with_pe( cur_set->pe->copy_iso );
				new_pe_list->lams_prod_iso = cur_set->prod_iso;
			}
			else
			{
				new_pe		= create_LP_parsing_element();
				new_pe->label	= cur_set->pe->label;
				new_pe->width	= cur_set->pe->width;
				new_pe->height	= cur_set->pe->height;
				new_pe_list	= create_lp_pe_list_with_pe( new_pe );
				new_pe_list->lams_prod_iso = cur_set->prod_iso;
				cur_set->pe->copy_iso = new_pe;
			}
			LP_pe_list_SET_PROD_ISO( new_pe_list, cur_set->lp_prod_iso );

			lp_derivation_SET_PARSING_ELEMENTS( new_der,
							    add_pe_list_to_pe_list(lp_derivation_GET_PARSING_ELEMENTS(new_der), new_pe_list) );

			cur_set = cur_set->succ;
		}

		return( new_der );
	}
	else
	{
		return( NULL );
	}
}

/************************************************************************************************/

/*****************************************************************************************
function:	free_copy_iso_in_lams_table( cur_pe )

*****************************************************************************************/

void	free_copy_iso_in_lams_table(PE_set pe_set)
{
	Parsing_element	iso_pe;

	if( pe_set )
	{
		while( pe_set )
		{
			pe_set->pe->visited = FALSE;
			pe_set->pe->copy_iso = NULL;

			free_copy_iso_in_lams_table( pe_set->pe->right_side );

			iso_pe = pe_set->pe->isomorph_pes;	/****** Menge der isomorphen PE`s	   ******/
			while( iso_pe )
			{
				iso_pe->visited		= FALSE;
				iso_pe->copy_iso	= NULL;
				free_copy_iso_in_lams_table( iso_pe->right_side );

				iso_pe = iso_pe->succ;
			}
			pe_set = pe_set->succ;
		}
	}
}

/************************************************************************************************/

/*****************************************************************************************
function:	create_lp_derivation_list

	Erzeugt Speicherplatz fuer Datenstruktur LP_derivation_list p

Output:	Zeiger auf p
*****************************************************************************************/

LP_Derivation_list	create_lp_derivation_list(void)
{
	LP_Derivation_list	new = (LP_Derivation_list)mymalloc( sizeof(struct LP_derivation_list) );

	new->derivation	= NULL;
	new->pre	= new;
	new->suc	= new;

	return( new );
}

/*****************************************************************************************
function:	LP_der_list_SET_DERIVATION
Input:	LP_Derivation_list list, LP_Derivation der

	Setzt list->derivation
*****************************************************************************************/

void	LP_der_list_SET_DERIVATION(LP_Derivation_list list, LP_Derivation der)
{
	list->derivation = der;
}

/*****************************************************************************************
function:	LP_der_list_GET_DERIVATION
Input:	LP_Derivation_list list

Output:	list->derivation
*****************************************************************************************/

LP_Derivation	LP_der_list_GET_DERIVATION(LP_Derivation_list list)
{
	return( list->derivation );
}

/*****************************************************************************************
function:	create_lp_derivation_list_with_der
Input:	LP_Derivation der

	Erzeugt Speicherplatz fuer Datenstruktur LP_derivation_list p und setzt derivation

Output:	Zeiger auf p
*****************************************************************************************/

LP_Derivation_list	create_lp_derivation_list_with_der(LP_Derivation der)
{
	LP_Derivation_list	new = create_lp_derivation_list();

	LP_der_list_SET_DERIVATION( new, der );

	return( new );
}

/*****************************************************************************************
function:	free_lp_der_list_with_lower_part
Input:	LP_Derivation_list list

	Loescht Speicherplatz von list und darunterliegende Datenstrukturen
*****************************************************************************************/

void	free_lp_der_list_with_lower_part(LP_Derivation_list list)
{
	LP_Derivation_list	to_delete;

	if( list )
	{
		list->pre->suc = NULL; /*Sichere Terminierung*/
		while( list )
		{
			to_delete = list;
			list = list->suc;

			free_lp_derivation_with_lower_part( to_delete->derivation );
			to_delete->derivation = NULL;
			free( to_delete );
		}
	}
}
/*****************************************************************************************
function:	lp_der_list_add_list_to_list
Input:	LP_Derivation_list list, new_elem

	Haengt new_elem an list an

Output:	Zeiger auf geaenderte Liste
*****************************************************************************************/

LP_Derivation_list	lp_der_list_add_list_to_list(LP_Derivation_list list, LP_Derivation_list new_elem)
{
	LP_Derivation_list	last;

	if( !list )
	{
		return( new_elem );
	}
	
	last = list->pre;

	new_elem->pre->suc	= list;
	last->suc		= new_elem;
	list->pre		= new_elem->pre;
	new_elem->pre		= last;

	return( list );
}

/*****************************************************************************************
function:	lp_der_list_add_derivation_to_list
Input:	LP_Derivation_list list, LP_Derivation new_elem

	Haengt new_elem an list an

Output:	Zeiger auf geaenderte Liste
*****************************************************************************************/

LP_Derivation_list	lp_der_list_add_derivation_to_list(LP_Derivation_list list, LP_Derivation new_elem)
{
	return( lp_der_list_add_list_to_list(list, create_lp_derivation_list_with_der(new_elem)) );
}

/****************************************************************************************/
/*	LP_copy_array									*/
/****************************************************************************************/

LP_copy_array	create_lp_copy_array(void)
{
	LP_copy_array	new = (LP_copy_array)mymalloc( sizeof(struct lp_copy_array) );

	new->prod		= NULL;
	new->derivation		= NULL;
	new->derivated_node	= NULL;
	new->next		= NULL;

	return( new );
}

LP_copy_array	append_to_lp_copy_array(LP_copy_array list, LP_copy_array new)
{
	if( !list )
	{
		return( new );
	}

	new->next = list;
	return( new );
}

void	free_lp_copy_array(LP_copy_array list)
{
	LP_copy_array	to_delete;

	while( list )
	{
		to_delete = list;
		list = list->next;
		free( to_delete );
	}
}

LP_copy_array	delete_from_copy_array(LP_copy_array array, Sprod prod, LP_Derivation der)
{
	LP_copy_array	mem,
			head;

	if( (array->prod == prod) && (array->derivation == der) )
	{
		mem = array->next;
		free( array );
		return( mem );
	}

	head = array;
	while( array )
	{
		if( (array->next->prod == prod) && (array->next->derivation == der) )
		{
			mem = array->next;
			array->next = array->next->next;
			free( mem );
			return( head );
		}
		array = array->next;
	}
	printf("Fehler in delete_from_copy_array\n");
	return NULL;
}

/*****************************************************************************************
function:	append_node_isomorphism
Input:	PE_production	head, iso_prod

	Erzeugt die Zeiger next_iso_node

Output:	---
*****************************************************************************************/

void	append_node_isomorphism(PE_production head, PE_production iso_prod)
{
	Parsing_element	pe_head,
			cur_pe;

	pe_head = head->right_side;

	while( pe_head )
	{
		cur_pe = pe_head;
		while( cur_pe->next_iso_node )
		{
			cur_pe = cur_pe->next_iso_node;
		}
		cur_pe->next_iso_node = pe_head->pe_iso;

		pe_head = pe_head->succ;
	}
}
