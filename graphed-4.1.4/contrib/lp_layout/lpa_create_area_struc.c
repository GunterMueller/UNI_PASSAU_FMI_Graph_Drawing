#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_general_functions.h"

#include "lpa_create_struc.h"
#include "lpa_create_area_struc.h"

/**************** als erstes die Datenstruktur Liste von LPA_upper_prod_array, hier ***************/
/**************** definiert, da sie sonst nie gebraucht wird			    ***************/
typedef	struct	lpa_upper_list
{
	struct	lpa_upper_prod_array	*elem;
	struct	lpa_upper_list		*next;
}	*LPA_Upper_list;


/*****************************************************************************************
function:	get_lpa_upper_prod_array_of_sons
Input:	tree_node_ref tree_node

	Voraussetzung ist, dass vorher bei allen Soehnen die Datenstruktur 
	LPA_Upper_prod_array erzeugt wurde ( bottom_up !!! ).

	Hier wird eine Datenstruktur Liste von LPA_Upper_prod_array l erzeugt, die auf 
	jeweils ein array von LPA_Upper_prod zeigt.
	Dieses LPA_Upper_prod_array ist dann diejenige, die an den Soehnen haengt
	(KEINE Kopie). 

Output:	l
*****************************************************************************************/

LPA_Upper_list	get_lpa_upper_prod_array_of_sons(tree_node_ref tree_node)
{
	LPA_Upper_list		result 		= NULL,
				new;
	tree_ref		cur;


	cur = tree_node->first_son;

	/****** Durchlaufe alle Soehne ******/
	while( cur )
	{
		if( cur->tree_rec_type == TREE_NODE )
		{
			if( cur->tree_rec.node->area_structures )
			{
				/****** Wenn Sohn Knoten und selbst abgeleitet nehme in result auf ******/
				new = (LPA_Upper_list)mymalloc( sizeof(struct lpa_upper_list) );

				new->elem = cur->tree_rec.node->area_structures;
				new->next = result;
				result 	  = new;
			}
		}
		cur = cur->next_brother;
	}
	return( result );
}

/*****************************************************************************************
function:	lpa_permute_derivation
Input:	LPA_Lower_prod lower_array; int width, height, *where_to_set;
	LPA_Upper_list liste, cur_result.

	Erzeugt rekursiv alle moeglichen Kombinationen der in Liste gespeicherten Produktionen
	und legt diese in lower_array ab.
	where_to_set gibt dabei an, in welcher Zeile eingetragen werden muss.
	width und height geben die Groesse von lower_array an.
	cur_result gibt die momentane Ableitung an. Dabei wird die Datenstruktur 
	LPA_Upper_prod_array anders als sonst verwendet:
	->number: gibt nicht an wie lang das array ist, sondern welches Layout
		  zur jetzigen Ableitung gehoert.
	Where_to_set wird by_reference uebergeben, da ganz unten geaendert wird. Sonst immer
	Eintrag in erste Zeile.

Output:	---
*****************************************************************************************/

void	lpa_permute_derivation(LPA_Lower_prod lower_array, int width, int height, int *where_to_set, LPA_Upper_list liste, LPA_Upper_list cur_result)
{
	int			i;		/*** Laufvariable ***/
	int			index;		/*** Hilfsvariable. Macht kurzer und schneller ***/
	LPA_Upper_prod_array	new;		/*** Wird neu erzeugt und in cur_result eingehaengt ***/
	LPA_Upper_list		new_list;	/*** Wird neu erzeugt und in cur_result eingehaengt ***/
	LPA_Upper_prod		cur_upper;	/*** Hilfsvariable. Macht schneller und kuerzer ***/

	/****** Abbruchfall ist, wenn liste = nil. Dann wurde fuer jeden abgeleiteten Sohn was ausgesucht ******/
	if( liste == NULL )
	{
		/****** Trage aktuelle Ableitung in vorgegebener Zeile ein ******/
		/****** DABEI GILT: Array ist genauso breit wie cur_result lang ist ******/
		i = 0;	
		while( cur_result )
		{
			cur_upper	= cur_result->elem->array_head;
			index 		= cur_result->elem->number;
			lower_array[ADR(*where_to_set, i, width)].lower_prod = cur_upper[index].prod_layout[cur_upper[index].prod_array_nr].production;
			lower_array[ADR(*where_to_set, i, width)].same_lower = cur_upper;
			lower_array[ADR(*where_to_set, i, width)].lower_nr   = index;

			i = i + 1;
			cur_result = cur_result->next;
		}
		/****** Jetzt aendere noch Zeile, in die eingetragen werden soll und dann sind wir FERTIG ******/
		*where_to_set = *where_to_set + 1;
	}
	else	/****** Es muss cur_result noch vervollstaendigt werden ******/
	{
		/****** Laufe durch alle moeglichen Layouts fuer aktuell erstes Element in Liste und fuege dies zu cur_result ******/
		for( i = 0; i < liste->elem->number; i++ )
		{
			new = create_lpa_upper_prod_array();
			new->array_head = liste->elem->array_head;
			new->number	= i;

			new_list	= (LPA_Upper_list)mymalloc( sizeof(struct lpa_upper_list) );
			new_list->elem 	= new;
			new_list->next	= cur_result;

			/****** Jetzt rekursiver Aufruf ******/
			lpa_permute_derivation( lower_array, width, height, where_to_set, liste->next, new_list );

			/****** Jetzt stelle alten Zustand wieder her, damit weiterpermutiert werden kann ******/
			free( new );
			free( new_list );
		}
	}
}

/*****************************************************************************************
function:	lpa_create_lower_area
Input:	tree_node_ref	tree_node

	Erzeugt Datenstrukturen zur area-Berechnung fuer untere Produktion. Wird nur
	erzeugt, wenn ein abgeleiteter Sohn existiert. Der uebergebene tree_node ist
	der Vater.
	Es wird also als erstes eine Liste aller abgeleiteten Soehne erzeugt.
	Dann werden ( rekursiv ) alle moeglichen Kombinationen erzeugt und in ein 
	(an rekursive Fkt.) uebergebenes array werden die Moeglichkeiten eingetragen.
 
	Das Ergebnis wird ein zweidimensionales array, das an tree_node->area_structures
	angehaengt wird.

Output: ---
*****************************************************************************************/

void	lpa_create_lower_area(tree_node_ref tree_node)
{
	LPA_Lower_prod		lower_array;		/*** Struktur, die erzeugt wird ***/
	LPA_Upper_prod		upper_array;		/*** Hilfsvariable, macht schneller und kuerzer ***/
	LPA_Upper_list		liste,			/*** Verwaltung der Liste der Ableitungen der Soehne ***/
				cur;			/*** Verwaltung der Liste der Ableitungen der Soehne ***/
	int			permut		= 1,	/*** Wird Hoehe des Arrays  ***/
				len		= 0,	/*** Wird Breite des Arrays ***/
				where_to_set	= 0,	/*** Geht an rekursive Fkt, sagt wo eingesetzt wird ***/
				i, j, k;		/*** Laufvariable ***/

	/****** Hole eine Liste aller LPA_Upper_prod_array von Soehnen ******/
	liste = get_lpa_upper_prod_array_of_sons( tree_node );

	if( liste )	/****** genau dann existiert mindestens ein abgeleiteter Sohn ******/
	{
		/****** Zaehle, wieviele Permutationen moeglich sind ******/
		cur = liste;
		while( cur )	
		{
			permut = permut * cur->elem->number;
			len++;
			cur = cur->next;
		}

		/****** Erzeuge die Datenstruktur, in die alles eingetragen wird und die dann im Baum haengt ******/
		lower_array = create_lpa_lower_prod( len, permut );

		/****** Die rekursive Fkt. wird aufgerufen, sie macht den Rest ausser den Zeiger auf die obere Produktion ******/
		lpa_permute_derivation( lower_array, len, permut, &where_to_set, liste, (LPA_Upper_list)NULL );

		/****** Speicher von liste wieder freimachen ******/
		while( liste )
		{
			cur = liste;
			liste = liste->next;
			free( cur );
		}

		/****** Tree_node kann mehrere isomorphe Produktionen repraesentieren, haenge an alle ******/
		/****** (eine jeweils eigenstaendige Kopie) an. ******/

		/****** Als erstes Eintrag in array[0]. Spart die Abfrage, ob neu erzeugte Kopie kopiert werden muss ******/
		upper_array			= tree_node->area_structures->array_head;
		upper_array[0].lower_array	= lower_array;
		upper_array[0].der_nr		= permut;
		upper_array[0].node_nr		= len;

		for( i = 1; i < tree_node->area_structures->number; i++ )
		{
			upper_array[i].lower_array	= create_copy_of_lpa_lower_array(lower_array, len, permut);
			upper_array[i].der_nr		= permut;
			upper_array[i].node_nr		= len;
		}

		/****** Zeiger auf die obere Produktion fehlt. Das passiert am besten jetzt ******/
		for( k = 0; k < tree_node->area_structures->number; k++ )
		{
			for( i = 0; i < permut; i++ )
			{
				for( j = 0; j < len; j++ )
				{
					lower_array[ADR(i, j, len)].upper_prod = upper_array[k].prod_layout[upper_array[k].prod_array_nr].production;
				}
			}	
		}
	}
}

/*****************************************************************************************
function:	lpa_create_upper_area
Input:	tree_node_ref	tree_node

	Erzeugt Datenstrukturen zur area-Berechnung fuer obere Produktion. Wird IMMER
	erzeugt, auch wenn kein abgeleiteter Sohn existiert (selbst aber schon abgeleitet).

Output: ---
*****************************************************************************************/

void	lpa_create_upper_area(tree_node_ref tree_node)
{
	int			array_len, i;
	LPA_Upper_prod_array	prod_head;
	LPA_Upper_prod		prods;


	array_len	= tree_node->possible_productions->number;	/****** Hilfsvariable ******/

	/****** Erzeugen der neuen Datenstrukturen ******/
	prod_head	= create_lpa_upper_prod_array			();
	prods		= create_lpa_upper_prod				( array_len );

	/****** Zuweisungen an neu erzeugte Datenstrukturen ******/
	prod_head->number	= array_len;

	for( i = 0; i < array_len; i++ )
	{
		prods[i].tree_node	= tree_node;
		prods[i].prod_layout	= tree_node->possible_productions->array_head;
		prods[i].prod_array_nr	= i;						/*** Gleiche Ordnung wie in LPA_Production ***/
	}

	/****** Zuweisen der Datenstrukturen ******/
	tree_node->area_structures	= prod_head;
	prod_head->array_head		= prods;

}

/*****************************************************************************************
function:	lpa_create_area_structures_for_derivation_tree
Input:	tree_ref derivation_tree

	Legt Datenstrukturen an, die zum berechnen des area-optimalen Layouts notwendig sind
	Fkt fuehrt nur bottom-up durchlauf des Ableitungsbaums durch und ruft die Fkt zum
	erzeugen der neuen Datenstrukturen auf

Output:	---
*****************************************************************************************/

void	lpa_create_area_structures_for_derivation_tree(tree_ref derivation_tree)
{
	tree_ref		cur;
	tree_node_ref		tree_node;

	cur = derivation_tree;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
	             ( cur->tree_rec.node->first_son != NULL ) && 
		     ( !cur->tree_rec.node->leaf ) ) 
		{
			lpa_create_area_structures_for_derivation_tree( cur->tree_rec.node->first_son );
		}
		cur = cur->next_brother;
	}


	cur = derivation_tree;
	while ( cur != NULL )
	{
		if (	(cur->tree_rec_type == TREE_NODE)	&&
			(cur->tree_rec.node->first_son) 	&&
			(!cur->tree_rec.node->leaf)		)
		{
			tree_node = cur->tree_rec.node;

			lpa_create_upper_area( tree_node );

			if( tree_node->area_structures )
			{
				lpa_create_lower_area( tree_node );
			}
		}
		cur = cur->next_brother;
	}

}
