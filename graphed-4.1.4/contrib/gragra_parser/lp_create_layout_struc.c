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

#include "lp_datastruc.h"
#include "lp_cost_struc.h"
#include "lp_create_optimal_graph.h"

#include "lp_create_layout_struc.h"


/************************************************************************************************/
/*												*/
/*	File zum Anlegen der Datenstrukturen fuer Layoutberechnung				*/
/*												*/
/************************************************************************************************/


/************************************************************************************************/
/*												*/
/*		Hilfsroutinen									*/
/*												*/
/************************************************************************************************/

/*****************************************************************************************
function:	derivated_son_exists_in
Input:	LP_Derivation derivation

Output:	TRUE, iff. mind. ein PE der derivation wurde abgeleitet
	FALSE, otherwise

Verwendung:	create_layout_datastructures_for_below_part_of_table,
		create_layout_datastructures_for_lower

Verwendet:	---
*****************************************************************************************/

int	derivated_son_exists_in(LP_Derivation derivation)
{
	LP_Parsing_element_list		cur_pe_list;

	FOR_LP_PARSING_ELEMENTS( derivation->parsing_elements, cur_pe_list )
	{
		if( cur_pe_list->pe->derivations )
		{
			return( TRUE );
		}
	}
	END_FOR_LP_PARSING_ELEMENTS( derivation->parsing_elements, cur_pe_list );

	return( FALSE );
}

/*****************************************************************************************
function:	create_list_of_derivated_pes
Input:	LP_Derivation	derivation

	Erzeugt Liste l aller PE's in derivation->parsing_elements,die selbst
	abgeleitet wurden.

Output:	Zeiger auf l

Verwendung:	create_layout_datastructures_for_lower

Verwendet	---
*****************************************************************************************/

LP_Parsing_element_list	create_list_of_derivated_pes(LP_Derivation derivation)
{
	LP_Parsing_element_list	cur_pe_list,
				result,
				new_pe_list;

	result = NULL;


	FOR_LP_PARSING_ELEMENTS( derivation->parsing_elements, cur_pe_list )
	{
		if( cur_pe_list->pe->derivations )
		{
			new_pe_list 			= create_lp_pe_list_with_pe( cur_pe_list->pe );
			new_pe_list->prod_iso		= cur_pe_list->prod_iso;
			new_pe_list->lams_prod_iso	= cur_pe_list->lams_prod_iso;
			result = add_pe_list_to_pe_list( result, new_pe_list );
		}
	}
	END_FOR_LP_PARSING_ELEMENTS( derivation->parsing_elements, cur_pe_list );

	return( result );
}



/*****************************************************************************************
function:	create_copy_of_lower_der
Input:	LP_lower_derivation old
	
	Erzeugt 1:1 Kopie k von old

Output:	Zeiger auf k
*****************************************************************************************/

LP_lower_derivation	create_copy_of_lower_der(LP_lower_derivation old)
{
	LP_lower_derivation		lower_copy,
					result			= NULL,
					current_lower;
	LP_array_of_lower_productions	lower_array_copy,
					old_array;
	int				i;

	FOR_LP_LOWER_DERIVATION( old, current_lower )
	{
		lower_copy		= create_lp_lower_derivation();
		lower_array_copy	= create_lp_array_of_lower_productions( current_lower->number_of_productions );

		lower_copy->number_of_productions 	= current_lower->number_of_productions;
		lower_copy->productions			= lower_array_copy;
		result = append_to_lp_lower_derivation( result, lower_copy );

		old_array = current_lower->productions;

		for( i = 0; i < current_lower->number_of_productions; i++ )
		{
			lower_array_copy[i].father_node		= old_array[i].father_node;
			lower_array_copy[i].derivation		= old_array[i].derivation;
			lower_array_copy[i].lams_prod_iso	= old_array[i].lams_prod_iso;
			lower_array_copy[i].graphed_father	= old_array[i].graphed_father;
			lower_array_copy[i].production		= old_array[i].production;
		}
	}
	END_FOR_LP_LOWER_DERIVATION( old, current_lower );

	return( result );
}
/*****************************************************************************************
function:	get_iso_below
Input:	Sprod prod, LP_Derivation der

	Sucht nach Datenstruktur LP_array_of_productions p, die zu der gehoert und auf
	prod zeigt.

Output:	Zeiger auf p

Verwendung:	create_derivated_node_and_same_prod_lower

Verwendet:	
*****************************************************************************************/

int	get_iso_below(Sprod prod, LP_Derivation der)
{
	int			i,
				nr_of_layouts	= der->copy_iso->number_of_prod_layouts;
	LP_array_of_productions array		= der->copy_iso->production_layouts;

	for( i = 0; i < nr_of_layouts; i++ )
	{
		if( array[i].prod_iso == prod )
		{
			return( i );
		}
	}
	return -1;
}


/*****************************************************************************************
ACHTUNG: MUSS ZWEIMAL EXISTIEREN, WENN AUCH MIT FESTER ABLEITUNG GEARBEITET WERDEN SOLL
	 DENN:	DER ZUGRIFF AUF DIE DATENSTRUKTUREN VON KAMSHOFT GESCHIEHT DESHALB, WEIL
 		HIER DIE VERKETTUNG DER ISOMORPHEN PRODUKTIONEN LIEGT. DIESE FUNKTION
		BRAUCHT ABER DIESE VERKETTUNG

function:	get_iso_node_in_prod
Input:	LP_array_of_lower_productions lower_array , Sprod searched

	Sucht in der Liste der zu pe isomorphen Knoten nach dem, der in searched liegt.
	(Genauer gesagt dessen entsprechender Snode)
Output:	Gesuchter Knoten

Verwendung:	create_derivated_node_and_same_prod_lower

Verwendet:	
*****************************************************************************************/

Snode	get_iso_node_in_prod(LP_array_of_lower_productions lower_array, Sprod searched)
{
	Parsing_element	pe;

	pe = lower_array->lams_prod_iso;
	while( pe )
	{
		if( pe->prod_iso->graph == searched->right )
		{
			return( pe->prod_iso );
		}
		pe = pe->next_iso_node;
	}

	/****** falls wir hierher kommen haben wir Ableitung mit Baum. Es existiert also nur immer eine Entsprechung ******/
	return( lower_array->father_node->prod_iso );

}

/*****************************************************************************************
function:	
*****************************************************************************************/
int	get_width(LP_dependency_graph x_depen)
{
	while( x_depen )
	{
		if( !x_depen->next )
		{
			return( x_depen->new_coord );
		}
		x_depen = x_depen->next;
	}
	return 0;
}

/*****************************************************************************************
function:	
*****************************************************************************************/
int	get_height(LP_dependency_graph y_depen)
{
	while( y_depen )
	{
		if( !y_depen->next )
		{
			return( y_depen->new_coord );
		}
		y_depen = y_depen->next;
	}
	return 0;
}

/*****************************************************************************************
function:	create_derivated_node_and_same_prod_lower
Input:	LP_upper_production upper_prod

	Erzeugt von lp_array_of_lower_production aus die zwei Zeiger 'same_prod_lower'
	und 'graphed_father'.
	Muss fuer jede einzelne Derivation aufgerufen werden, da sonst nur fuer einmal
	LP_upper_production ausgefuehrt wird.

Output:	---

Verwendung:	create_layout_datastructures_for_lower

Verwendet:	get_iso_node_in_prod,
*****************************************************************************************/

void	create_derivated_node_and_same_prod_lower(LP_upper_production upper_prod)
{
	LP_lower_derivation		first_lower_der,
					cur_lower_der;
	LP_array_of_lower_productions	prod_array;
	int				i,j;	

	for( i = 0; i < upper_prod->number_of_prod_layouts; i++ )
	{
		first_lower_der = upper_prod->production_layouts[i].derivations_below;

		FOR_LP_LOWER_DERIVATION( first_lower_der, cur_lower_der )
		{
			prod_array = cur_lower_der->productions;

			for( j = 0; j < cur_lower_der->number_of_productions; j++ )
			{
				/****** graphed_father ******/
				prod_array[j].graphed_father = get_iso_node_in_prod( &prod_array[j],
										     upper_prod->production_layouts[i].prod_iso );

				/****** same_prod_lower ******/
				if( prod_array[j].derivation->copy_iso )
				{
					prod_array[j].same_prod_lower	= prod_array[j].derivation->copy_iso;
					prod_array[j].array_entry	= get_iso_below( prod_array[j].production, prod_array[j].derivation );
				}

				if( !prod_array[j].same_prod_lower )
				{
					prod_array[j].x_dependency = create_lp_x_dependency_graph_from_sprod( prod_array[j].production, NULL, 
												      prod_array[j].derivation->parsing_elements );
					prod_array[j].y_dependency = create_lp_y_dependency_graph_from_sprod( prod_array[j].production, NULL, 
												      prod_array[j].derivation->parsing_elements );

					prod_array[j].w = get_width( prod_array[j].x_dependency );
					prod_array[j].h = get_height( prod_array[j].y_dependency ); 
				}
			}
		}
		END_FOR_LP_LOWER_DERIVATION( first_lower_der, cur_lower_der );
	}
}

/************************************************************************************************/
/*												*/
/*		Eigentliches Erzeugen der Datenstrukturen					*/
/*												*/
/************************************************************************************************/

/*****************************************************************************************
function:	create_all_kombinations_of
Input:	LP_Parsing_element_list	head, pe_list, 
	LP_lower_derivation 	result,
	LP-copy_array		current_selection
	
	Erzeugt die Datenstruktur LP_lower_derivation und LP_array_of_lower_productions.
	Und zwar ALLE Moeglichkeiten, die pe_list abzuleiten.

	Vorgehen: durchlaufe alle Moeglichkeiten aktuelles pe (=pe_list) abzuleiten und
		kombiniere mit rekursiv erzeugtem Rest. Erzeuge unten Datenstruktur und
		haenge an result

Output:	Zeiger auf Anfang der erzeugten Datenstrukturen

Verwendung:	selbst,
		create_layout_datastructures_for_lower

Verwendet:	---
*****************************************************************************************/

LP_lower_derivation	create_all_kombinations_of(LP_Parsing_element_list head, LP_Parsing_element_list pe_list, LP_lower_derivation result, LP_copy_array current_selection)
{
	LP_copy_array			new;
	LP_Derivation_list		cur_der_list;
	PE_production			lams_prod;
	LP_copy_array			cur_array;
	int				i,
					count;
	LP_lower_derivation		new_lp_lower;
	LP_array_of_lower_productions	new_lp_lower_array;

	FOR_LP_DERIVATIONS( pe_list->pe->derivations, cur_der_list )		/****** durchlaufe alle Moeglichkeiten abzuleiten ******/
	{
		lams_prod = cur_der_list->derivation->lams_prod;		/****** fuege auf jetziger Stufe neues Element dazu ******/

		new = create_lp_copy_array();
		if( !lams_prod )
		{
			new->prod	= cur_der_list->derivation->used_prod;
		}
		else
		{
			new->prod	= lams_prod->prod_iso;
		}
		new->derivation		= cur_der_list->derivation;
		new->derivated_node	= pe_list->pe;
		new->pe_list		= pe_list;
		current_selection 	= append_to_lp_copy_array( current_selection, new );

		if( pe_list->suc == head )					/****** Wir sind ganz unten, Datenstrukturen erzeugen ******/
		{
			count = 0;
			cur_array = current_selection;
			while( cur_array )
			{
				count++;
				cur_array = cur_array->next;
			}

			new_lp_lower_array	= create_lp_array_of_lower_productions( count );
			new_lp_lower		= create_lp_lower_derivation();

			new_lp_lower->number_of_productions	= count;
			new_lp_lower->productions		= new_lp_lower_array;

			cur_array = current_selection;

			for( i = 0; i < count; i++ )
			{
				new_lp_lower_array[i].father_node	= cur_array->derivated_node;
				new_lp_lower_array[i].production	= cur_array->prod;
				new_lp_lower_array[i].derivation	= cur_array->derivation;
				new_lp_lower_array[i].lams_prod_iso	= cur_array->pe_list->lams_prod_iso;
				cur_array = cur_array->next;
			}

			result = append_to_lp_lower_derivation( result, new_lp_lower );	/****** altes result vervollstaendigen ******/
		}
		else
		{						/****** Wir muessen aktuelle Moeglichkeit noch vervollstaendigen ******/
			result = create_all_kombinations_of( head, pe_list->suc, result, current_selection );
		}
		/****** jetzt gleiches nochmal, aber isomorphe Produktionen (deshalb so kompakt) ******/
		current_selection = delete_from_copy_array( current_selection, lams_prod->prod_iso, cur_der_list->derivation );

		if( lams_prod )
		{
			lams_prod = lams_prod->isomorph_prods;
			while( lams_prod ){
				new = create_lp_copy_array();
				new->prod		= lams_prod->prod_iso;
				new->derivation		= cur_der_list->derivation;
				new->derivated_node	= pe_list->pe;
				new->pe_list		= pe_list;
				current_selection 	= append_to_lp_copy_array( current_selection, new );
				if( pe_list->suc == head ){
					cur_array = current_selection;
					count = 0;
					while( cur_array ){
						count++;
						cur_array = cur_array->next;}
					new_lp_lower_array	= create_lp_array_of_lower_productions( count );
					new_lp_lower		= create_lp_lower_derivation();
					new_lp_lower->number_of_productions	= count;
					new_lp_lower->productions		= new_lp_lower_array;
					cur_array = current_selection;
					for( i = 0; i < count; i++ ){
						new_lp_lower_array[i].father_node	= cur_array->derivated_node;
						new_lp_lower_array[i].production	= cur_array->prod;
						new_lp_lower_array[i].derivation	= cur_array->derivation;
						new_lp_lower_array[i].lams_prod_iso	= cur_array->pe_list->lams_prod_iso;
						cur_array = cur_array->next; }
					result = append_to_lp_lower_derivation( result, new_lp_lower );}
				else{	result = create_all_kombinations_of( head, pe_list->suc, result, current_selection );}
				current_selection = delete_from_copy_array( current_selection, lams_prod->prod_iso, cur_der_list->derivation );
				lams_prod = lams_prod->succ;}
		}
	}
	END_FOR_LP_DERIVATIONS( pe_list->pe->derivations, cur_der_list );

	return( result );
}

/*****************************************************************************************
function:	create_layout_datastructures_for_upper
Input:	LP_Derivation derivation
	
	Erzeugt entsprechende Datenstrukturen LP_upper_production und je Produktion einmal
	LP_array_of_productions

Output:	Zeiger auf Datenstruktur

Verwendung:	create_layout_datastructures_for_below_part_of_table,
		create_layout_datastructures

Verwendet:	---
*****************************************************************************************/

LP_upper_production	create_layout_datastructures_for_upper(LP_Derivation derivation)
{
	PE_production		cur_pe_der;	/****** zum durchlaufen der Produktionen von Lamsh. ******/
	int			nr_of_prods;
	LP_upper_production	new_head;
	LP_array_of_productions	new_prod_array;
	int			cur_prod_layout = 0;

	nr_of_prods	= 1;
	cur_pe_der	= derivation->lams_prod;

	/****** Als erstes abfangen, ob ueberhaupt Lamshoft Datenstrukturen existieren ******/
	if( !cur_pe_der )
	{
		/****** Datenstrukturen erzeugen ******/

		new_head	= create_lp_upper_production();
		new_prod_array	= create_lp_array_of_upper_productions( 1 );

		/****** Zuweisungen an Datenstrukturen ******/
		derivation->copy_iso			= new_head;
 		new_head->derivation			= derivation;
		new_head->number_of_prod_layouts	= 1;
		new_head->production_layouts		= new_prod_array;

		new_prod_array[0].prod_iso = derivation->used_prod;

		return( new_head );
	}

	/****** Sonst Menge erzeugen ******/
	/****** Zaehlen, wieviele isomorphe Produktionen ******/

	cur_pe_der = cur_pe_der->isomorph_prods;
	while( cur_pe_der )
	{
		nr_of_prods++;
		cur_pe_der = cur_pe_der->succ;
	}

	/****** Datenstrukturen erzeugen ******/

	new_head	= create_lp_upper_production();
	new_prod_array	= create_lp_array_of_upper_productions( nr_of_prods );

	/****** Zuweisungen an Datenstrukturen ******/

	derivation->copy_iso			= new_head;

 	new_head->derivation			= derivation;
	new_head->number_of_prod_layouts	= nr_of_prods;
	new_head->production_layouts		= new_prod_array;

	cur_pe_der = derivation->lams_prod;

	new_prod_array[cur_prod_layout].prod_iso = cur_pe_der->prod_iso;

	cur_pe_der = cur_pe_der->isomorph_prods;
	cur_prod_layout++;

	while( cur_pe_der )
	{
		new_prod_array[cur_prod_layout].prod_iso = cur_pe_der->prod_iso;
		cur_prod_layout++;
		cur_pe_der = cur_pe_der->succ;
	}

	return( new_head );
}

/*****************************************************************************************
function:	create_layout_datastructures_for_lower
Input:	LP_Derivation derivation

	Erzeugt (falls notwendig) diejenigen Datenstrukturen, die notwendig sind um bei
	der Attributberechnung die untere Funktion betrachten zu koennen.
	Also Datenstrukturen LP_lower_derivation, LP_array_of_lower_productions.
	Gegenstueck zu 'create_layout_datastructures_for_upper'

Output:	---

Verwendung:	create_layout_datastructures_for_below_part_of_table,
		create_layout_datastructures

Verwendet:	derivated_son_exists_in,
		create_list_of_derivated_pes,
		create_all_kombinations_of,
		create_copy_of_lower_der
*****************************************************************************************/

void	create_layout_datastructures_for_lower(LP_Derivation derivation)
{
	LP_Parsing_element_list		pe_list;	/*** Menge der abgeleiteten Soehne ***/
	int				i;
	LP_lower_derivation		lower_der,
					new_lower_der;
	LP_upper_production		upper_struc;	/*** macht schneller und kuerzer ***/

	upper_struc = derivation->copy_iso;

	if( upper_struc &&						/****** koennte ja sein, dass keine abgeleiteten Soehne ******/
	    !upper_struc->production_layouts[0].derivations_below )	/****** koennte auf anderem Pfad erreicht worden sein ******/
	{
		if( derivated_son_exists_in(derivation) )			/****** ist anlegen der Datenstrukturen notwendig ******/
		{
			pe_list = create_list_of_derivated_pes( derivation );	/****** erzeuge Liste aller abgeleiteten Soehne ******/

			lower_der = create_all_kombinations_of( pe_list, pe_list, NULL, NULL );
												/****** erzeuge einmal alle Moeglichkeiten unten ******/
			upper_struc->production_layouts[0].derivations_below = lower_der;	/****** weise an 1. oben zu ******/

			free_lp_pe_list( pe_list );

			for( i = 1; i < upper_struc->number_of_prod_layouts; i++ )
			{
				new_lower_der = create_copy_of_lower_der( lower_der );			/****** erzeuge Kopie und weise an ... ******/
				upper_struc->production_layouts[i].derivations_below = new_lower_der;	/****** andere Layouts zu ******/

			}
			create_derivated_node_and_same_prod_lower( upper_struc ); /****** Erzeuge restliche Zeiger ******/


			for( i = 0; i < upper_struc->number_of_prod_layouts; i++ )
			{
				upper_struc->production_layouts[i].x_dependency	= 
					create_lp_x_dependency_graph_from_sprod( upper_struc->production_layouts[i].prod_iso,
										 upper_struc->production_layouts[i].derivations_below,
										 upper_struc->derivation->parsing_elements );
				upper_struc->production_layouts[i].y_dependency	= 
					create_lp_y_dependency_graph_from_sprod( upper_struc->production_layouts[i].prod_iso,
										 upper_struc->production_layouts[i].derivations_below,
										 upper_struc->derivation->parsing_elements );
			}
		}
	}
}

/************************************************************************************************/
/*												*/
/*		Durchlaufen der alten Datenstrukturen						*/
/*												*/
/************************************************************************************************/

/*****************************************************************************************
function:	create_layout_datastructures_for_below_part_of_table
Input:	LP_Derivation	derivation

	Fuer alle PE's von derivation: Falls abgeleitete Soehne existieren, erzeuge Daten-
		strukturen LP_upper_production und LP_array_of_production.
	(Rekursiv auch fuer alles drunter)
	
Output:	---

Verwendung:	selbst, 
		create_layout_datastructures

Verwendet:	create_layout_datastructures_for_upper,
		create_layout_datastructures_for_lower,
		derivated_son_exists_in
*****************************************************************************************/

void	create_layout_datastructures_for_below_part_of_table(LP_Derivation derivation)
{
	LP_Parsing_element_list		cur_pe_list;
	LP_Derivation_list		cur_der_list;
	LP_Parsing_element		cur_pe;		/*** macht schneller und kuerzer ***/

	if( !derivation->visited)
	{
		derivation->visited  = TRUE;

		FOR_LP_PARSING_ELEMENTS( derivation->parsing_elements, cur_pe_list )
		{
			cur_pe = cur_pe_list->pe;

				FOR_LP_DERIVATIONS( cur_pe->derivations, cur_der_list )
				{
					if(  !cur_der_list->derivation->copy_iso && /****** koennte ja schon auf anderem Pfad erzeugt worden sein ******/
				 	    derivated_son_exists_in(cur_pe->derivations->derivation) )
					{
						cur_pe->layout_structures = append_to_lp_upper_production( cur_pe->layout_structures,
											 create_layout_datastructures_for_upper(cur_der_list->derivation) );

						create_layout_datastructures_for_below_part_of_table( cur_der_list->derivation );

						create_layout_datastructures_for_lower( cur_der_list->derivation );
					}
				}
				END_FOR_LP_DERIVATIONS( cur_pe->derivations, cur_der_list );
		}
		END_FOR_LP_PARSING_ELEMENTS( derivation->parsing_elements, cur_pe_list );
	}
}

/*****************************************************************************************
function:	create_layout_datastructures
Input:	LP_Parsing_element head
	
	Erzeugt Datenstruktur LP_upper_production und LP_array_of_production fuer
	ganzen table.

Output:	---

Verwendung:	EXTERN

Verwendet:	create_layout_datastructures_for_below_part_of_table,
		create_layout_datastructures_for_upper,
		create_layout_datastructures_for_lower
*****************************************************************************************/

void	create_layout_datastructures(LP_Parsing_element head)
{
	LP_Derivation_list	cur_der_list;

		FOR_LP_DERIVATIONS( head->derivations, cur_der_list )
		{
			/****** An Wurzel muss fuer alle erzeugt werdn, auch wenn kein abgeleiteter Sohn existiert ******/
			head->layout_structures = append_to_lp_upper_production( head->layout_structures, 
									 create_layout_datastructures_for_upper(cur_der_list->derivation) );

			/****** rekursive Funktion aufrufen fuer restlichen Table ******/
			create_layout_datastructures_for_below_part_of_table( cur_der_list->derivation );

			/****** Es muessen noch die Datenstrukturen mit den Produktionen unterhalb erzeugt werden ******/
			create_layout_datastructures_for_lower( cur_der_list->derivation );
		}
		END_FOR_LP_DERIVATIONS( head->derivations, cur_der_list );
}
