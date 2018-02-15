#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include "misc.h"
#include "types.h"

#include "lp_datastruc.h"


/****************************************************************************************/
/*											*/
/*	File fuer die Konvertierung der Datenstruktur von Lamshoeft zu der eigenen	*/
/*											*/
/*	Beachte: Parsing_elemente werden folgendermassen verkettet:			*/
/*		Die Parsing_elemente einer Produktion werden mit PE_set verkettet.	*/
/*		(Zeiger right_side)							*/
/*		Falls isomorphe Parsing_elmente existieren:				*/
/*			pe->isomorph_pes zeigt auf Liste von diesen. Diese sind dann 	*/
/*			mit succ weiterverkettet					*/
/*											*/
/****************************************************************************************/


/*****************************************************************************************
function:	create_recursivly_a_copy_of_lower_part_of_table
Input:	Parsing_element father

	Erzeugt rekursiv Kopie von RHS von father und von allen Nachfolgern. Rest Kommentar
	siehe eins weiter unten

Output:	---
*****************************************************************************************/

void	create_recursivly_a_copy_of_lower_part_of_table(Parsing_element father)
{
	PE_set			cur_pe_set;		/* Laufvariable um Menge aller PE`s zu durchlaufen		*/
	Parsing_element		cur_isomorph_pe;	/* Kann auch sein, dass Thomas (immer isomorphe ) Startsymbole..*/
							/* bzgl. isomorph_pes verkettet hat, also damit Durchlauf	*/
	LP_Derivation		new_derivation;		/* Variable fuer die jeweils neu zu erzeugenden Ableitungen	*/

	cur_pe_set = father->right_side;

	/****** Right side wurde bereits kopiert; also muessen nur die Ableit. der RHS-Knoten kopiert werden	   ******/

	while( cur_pe_set )
	{
		/****** Erzeuge Parsing_element mit 1. Ableitung						   ******/
		new_derivation	= create_lp_derivation_of( cur_pe_set->pe->right_side );

		if( new_derivation )	/****** `create_lp_derivation_of` liefert NULL, falls keine RHS existiert  ******/
		{
			lp_derivation_SET_USED_PROD( new_derivation, cur_pe_set->pe->used_prod );
			new_derivation->lams_prod = cur_pe_set->pe->which_production;
			LP_parsing_element_SET_DERIVATIONS( cur_pe_set->pe->copy_iso,
					lp_der_list_add_derivation_to_list(LP_parsing_element_GET_DERIVATIONS(cur_pe_set->pe->copy_iso),
 									   new_derivation) );

			cur_isomorph_pe = cur_pe_set->pe->isomorph_pes;	/****** Menge der isomorphen PE`s	   ******/
			while( cur_isomorph_pe )
			{
				/****** Erzeuge in unserem Parsing_element eine neue Ableitung und haenge an	   ******/

				new_derivation	= create_lp_derivation_of(cur_isomorph_pe->right_side );

				if( new_derivation )
				{
					lp_derivation_SET_USED_PROD( new_derivation, cur_isomorph_pe->used_prod );
					new_derivation->lams_prod = cur_isomorph_pe->which_production;
					LP_parsing_element_SET_DERIVATIONS( cur_pe_set->pe->copy_iso,
							lp_der_list_add_derivation_to_list(LP_parsing_element_GET_DERIVATIONS(cur_pe_set->pe->copy_iso),
											   new_derivation) );

					if( !cur_isomorph_pe->visited )
					{
						cur_isomorph_pe->visited = TRUE;
						create_recursivly_a_copy_of_lower_part_of_table( cur_isomorph_pe );
					}
				}
				cur_isomorph_pe = cur_isomorph_pe->succ;
			}

			/****** rekursive Funktion aufrufen, die restlichen Ableitungsbaum kopiert		   ******/
			if( !cur_pe_set->pe->visited )
			{
				cur_pe_set->pe->visited = TRUE;
				create_recursivly_a_copy_of_lower_part_of_table( cur_pe_set->pe );
			}
		}
		cur_pe_set = cur_pe_set->succ;
	}
}	


/*****************************************************************************************
function:	convert_parser_data_structures_to_applying_structures
Input:	PE_set pars_table

	Erzeugt von pars_table eine Kopie in der Datenstruktur LP_...
	Dabei werden (im Sinn von gleicher label und gleiche Knotenmenge) isomorphe 
	Parsing_elemente zu einem zusammengefasst.

Output:	Zeiger auf die erzeugte Datenstruktur
*****************************************************************************************/

LP_Parsing_element	convert_parser_data_structures_to_applying_structures(PE_set pars_table)
{
	PE_set			cur_pe_set;		/* Laufvariable um Menge aller Startsymbole zu durchlaufen	*/
	Parsing_element		cur_isomorph_pe;	/* Kann auch sein, dass Thomas (immer isomorphe ) Startsymbole..*/
							/* bzgl. isomorph_pes verkettet hat, also damit Durchlauf	*/
	LP_Derivation		new_derivation;		/* Variable fuer die jeweils neu zu erzeugenden Ableitungen	*/
	LP_Parsing_element	head;			/* Wird Kopie von Menge aller Startsymbole			*/

	cur_pe_set	= pars_table;

	head		= create_LP_parsing_element();

	/****** Mache alle Zuweisungen an das neu erzeugte Parsing_element					   ******/

	head->label	= pars_table->pe->label;
	head->width	= pars_table->pe->width;
	head->height	= pars_table->pe->height;

	while( cur_pe_set )		/****** Hier: Menge aller Startsymbole (rekursiver Teil andere Funktion)   ******/
	{
		/****** Erzeuge Parsing_element mit 1. Ableitung						   ******/
		new_derivation	= create_lp_derivation_of( cur_pe_set->pe->right_side );

		if( new_derivation )	/****** `create_lp_derivation_of` liefert NULL, falls keine RHS existiert  ******/
		{
			lp_derivation_SET_USED_PROD( new_derivation, cur_pe_set->pe->used_prod );
			new_derivation->lams_prod = cur_pe_set->pe->which_production;
			LP_parsing_element_SET_DERIVATIONS( head,
							    lp_der_list_add_derivation_to_list(LP_parsing_element_GET_DERIVATIONS(head), 
											       new_derivation) );

			cur_isomorph_pe = cur_pe_set->pe->isomorph_pes;	/****** Menge der isomorphen PE`s	   ******/
			while( cur_isomorph_pe )
			{
				/****** Erzeuge in unserem Parsing_element eine neue Ableitung und haenge an	   ******/
				new_derivation	= create_lp_derivation_of(cur_isomorph_pe->right_side );

				if( new_derivation )
				{
					lp_derivation_SET_USED_PROD( new_derivation, cur_isomorph_pe->used_prod );
					new_derivation->lams_prod = cur_isomorph_pe->which_production;
					LP_parsing_element_SET_DERIVATIONS( head,
								    lp_der_list_add_derivation_to_list(LP_parsing_element_GET_DERIVATIONS(head),
												       new_derivation) );
					if( !cur_isomorph_pe->visited )
					{
						cur_isomorph_pe->visited = TRUE;
						create_recursivly_a_copy_of_lower_part_of_table( cur_isomorph_pe );
					}
				}
				cur_isomorph_pe = cur_isomorph_pe->succ;
			}

			/****** rekursive Funktion aufrufen, die restlichen Ableitungsbaum kopiert		   ******/
			if( !cur_pe_set->pe->visited )
			{
				cur_pe_set->pe->visited = TRUE;
				create_recursivly_a_copy_of_lower_part_of_table( cur_pe_set->pe );
			}
		}
		cur_pe_set = cur_pe_set->succ;
	}
	return( head );
}

