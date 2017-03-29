#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_general_functions.h"

#include "lpa_create_struc.h"
#include "lpa_create_sizes.h"

/*****************************************************************************************
Makro steht hier, da es nur in diesem file verwendet wird
*****************************************************************************************/

#define NEG_TO_ZERO( a ) iif( ( (int)(a) == -1 ), 0, (a) )

#define ZERO_TO_ONE( a ) iif( ( (int)(a) == 0 ), 1, (a) )

/*****************************************************************************************
function:	lpa_compute_new_layout
Input:	LPA_Sizes size, int which

	Berechnet in size[which] das neue Layout. 
	Laeuft dazu durch die beiden dependency. Daran haengt dann, falls dies ein unten
	weiter abgeleiteter Knoten ist, wie es nach unten weitergeht.

Output:	---
*****************************************************************************************/

void	lpa_compute_new_layout(LPA_Sizes size, int which)
{
	LPA_Sizes	lower_der;			/*** Wie wurde ein Knoten abgeleitet ***/
	int		nr;				/*** Nummer davon ***/
	LPA_Dependency	x_depen, y_depen;		/*** x- und y-Dependency ***/
	int		space,				/*** Platz, der fuer einen Knoten vorhanden ist ***/
			requiered,			/*** Platz, der fuer einen Knoten notwendig ist ***/
			grid,				/*** Auf welchem grid wurde gezeichnet ***/
			width, height;			/*** Gesammtgroesse ***/

	grid = size[which].grid;

	x_depen = size[which].x_dependency;
	while( x_depen )
	{
		if( x_depen->side == RIGHT )
		{
			/****** Wir sind am rechten Rand eines Knoten . Also nachschauen, ob unten was eingesetzt wurde. Wenn ja damit ausrechnen ******/
			if( x_depen->derivation )
			{
				lower_der	= x_depen->derivation;
				nr		= x_depen->der_nr;
				requiered	= lower_der[nr].width;
			}
			else	/****** Sonst nehmen wir einfach die Knotengroesse aus der Produktion ******/
			{
				requiered	= x_depen->prod_coord - x_depen->first_border->prod_coord;
			}

			space = x_depen->prod_coord + x_depen->old_coord_plus - x_depen->first_border->new_coord;

			if( requiered > space )
			{
				x_depen->new_coord_plus = x_depen->old_coord_plus + requiered - space;
			}
			else
			{
				x_depen->new_coord_plus	= x_depen->old_coord_plus;
			}
		}
		else
		{
			/****** Linke Seite, also nur was von hinten gekommen ist eintragen ******/
			x_depen->new_coord_plus = x_depen->old_coord_plus;
		}

		x_depen->new_coord		= x_depen->prod_coord + x_depen->new_coord_plus;
		x_depen->skaled_new_coord	= x_depen->new_coord / grid;

		if( x_depen->next )	/****** Wenn es einen Nachfolger gibt, dann eintragen, um wieviel verschoben ******/
		{
			x_depen->next->old_coord_plus = x_depen->new_coord_plus;
		}
		else
		{
			width = x_depen->new_coord;
		}
		x_depen = x_depen->next;
	}

	y_depen = size[which].y_dependency;
	while( y_depen )
	{
		if( y_depen->side == DOWN )
		{
			/****** Wir sind am unteren Rand eines Knoten . Also nachschauen, ob unten was eingesetzt wurde. Wenn ja damit ausrechnen ******/
			if( y_depen->derivation )
			{
				lower_der	= y_depen->derivation;
				nr		= y_depen->der_nr;
				requiered	= lower_der[nr].height;
			}
			else	/****** Sonst nehmen wir einfach die Knotengroesse aus der Produktion ******/
			{
				requiered	= y_depen->prod_coord - y_depen->first_border->prod_coord;
			}

			space = y_depen->prod_coord + y_depen->old_coord_plus - y_depen->first_border->new_coord;

			if( requiered > space )
			{
				y_depen->new_coord_plus = y_depen->old_coord_plus + requiered - space;
			}
			else
			{
				y_depen->new_coord_plus	= y_depen->old_coord_plus;
			}
		}
		else
		{
			/****** Obere Seite, also nur was von oben gekommen ist eintragen ******/
			y_depen->new_coord_plus = y_depen->old_coord_plus;
		}

		y_depen->new_coord		= y_depen->prod_coord + y_depen->new_coord_plus;
		y_depen->skaled_new_coord	= y_depen->new_coord / grid;

		if( y_depen->next )	/****** Wenn es einen Nachfolger gibt, dann eintragen, um wieviel verschoben ******/
		{
			y_depen->next->old_coord_plus = y_depen->new_coord_plus;
		}
		else
		{
			height = y_depen->new_coord;
		}
		y_depen = y_depen->next;
	}

	size[which].width		= width;
	size[which].height		= height;
	size[which].skaled_width	= ( width / grid );
	size[which].skaled_height	= ( height / grid );
}

/*****************************************************************************************
function:	lpa_make_entry_in_sizes
Input:	LPA_Upper_prod upper; int which, what_derivation, what_entry

	In LPA_Lower_prod wurde alles vorbereitet, jetzt koennen von der dependency aus die
	Eintragungen gemacht werden.
	Dabei:  Which gibt an in Welchem Eintrag von upper zu suchen ist.
		what_derivation gibt an welche Zeile in LPA_Lower_prod.
		what_entry gibt an, welcher LPA_Sizes zu veraendern ist.
	Durchlaufe die dependencies am zu veraendernden Array und schaue nach, ob sie auf
	einen tree_node zeigen.
	Wenn ja dann verglche solange mit richtiger Zeile in LPA_Lower_prod bis dort
	passende gefunden wurde.
	Mache Eintragungen.

Output:	---
*****************************************************************************************/

void	lpa_make_entry_in_sizes(LPA_Upper_prod upper, int which, int what_derivation, int what_entry)
{
	LPA_Dependency	cur_dep;		/*** Laufvariable ***/
	int		i,			/*** Laufvariable ***/
			len;			/*** Hilfsvariable ***/
	LPA_Sizes	sizes;			/*** Hilfsvariable ***/
	LPA_Lower_prod	lower;			/*** Hilfsvariable ***/

	lower	= upper[which].lower_array;
	sizes 	= upper[which].sizes;
	len	= upper[which].node_nr;

	cur_dep	= sizes[what_entry].x_dependency;

	while( cur_dep )
	{
		/****** Zeiger nur von rechter Seite aus notwendig ******/
		if( cur_dep->side == RIGHT && cur_dep->tree_node && cur_dep->tree_node->area_structures )
		{
			/****** Jetzt muessen wir Eintrag in LPA_Lower_array finden der passt ******/
			for( i = 0; i < len; i++ )
			{
				if( lower[ADR( what_derivation, i, len)].same_lower == cur_dep->tree_node->area_structures->array_head )
				{
					/******jetzt haben wir die Entsprechung. Also noch Zeiger setzen ***/
					cur_dep->derivation	= lower[ADR( what_derivation, i, len)].sizes;
					cur_dep->der_nr		= NEG_TO_ZERO( lower[ADR( what_derivation, i, len)].what_sizes );
				}
			}
		}
		cur_dep = cur_dep->next;
	}

	cur_dep = sizes[what_entry].y_dependency;

	while( cur_dep )
	{
		/****** Zeiger nur von unterer Seite aus notwendig ******/
		if( cur_dep->side == DOWN && cur_dep->tree_node && cur_dep->tree_node->area_structures )
		{
			/****** Jetzt muessen wir Eintrag in LPA_Lower_array finden der passt ******/
			for( i = 0; i < len; i++ )
			{
				if( lower[ADR( what_derivation, i, len)].same_lower == cur_dep->tree_node->area_structures->array_head )
				{
					/******jetzt haben wir die Entsprechung. Also noch Zeiger setzen ***/
					cur_dep->derivation	= lower[ADR( what_derivation, i, len)].sizes;
					cur_dep->der_nr		= NEG_TO_ZERO( lower[ADR( what_derivation, i, len)].what_sizes );
				}
			}
		}
		cur_dep = cur_dep->next;
	}
}

/*****************************************************************************************
function:	lpa_create_derivation_in_sizes
Input:	LPA_Upper_prod upper, int which

	In upper[which] ist das array der moeglichen Groessen bereits erzeugt. Was noch
	fehlt ist in LPA_Sizes der Verweis wie es unten weitergeht.
	Dazu:
	-Durchlaufe alle moeglichen Ableitungen
		-Durchlaufe sukzessive alle Knoten einer Ableitung
			-Durchlaufe alle moeglichen Groessen einer Ableitung. Aendere die
			 aktuellen Sizes eines Knoten und fuehre Eintragungen durch.
	ACHTUNG: Aufpassen: Es ist i.a. auch so, dass bei what_sizes -1 steht. Dann 0
		 verwenden wenn wirklich zugewiesen wird

Output:	---
*****************************************************************************************/

void	lpa_create_derivation_in_sizes(LPA_Upper_prod upper, int which)
{
	int		der_len		= upper[which].node_nr;		/*** Wieviele Soehne wurden abgeleitet ***/
	int		i, j, k;					/*** Laufvariable ***/
	int		not_ready;					/*** Bool um wirklich alle Moeglichkeiten auszutesten ***/
	LPA_Lower_prod	lower;						/*** Hilfsvariable ***/
	int		what_derivation;				/*** Welche Zeile ist gerade dran ***/
	int		what_entry	= 0;				/*** In welchen LPA_Sizes soll eingetragen werden ***/
	int		ready;						/*** Bool ob Zeile schon fertig ***/

	not_ready = TRUE;

	lower = upper[which].lower_array;

	for( i = 0; i < upper[which].der_nr; i++ )			/*** Fuer alle Ableitungen ***/
	{
		for( k = 0; k < upper[which].node_nr; k++ )
		{
			lower[ADR(i, k, der_len)].finished = FALSE;
		}
	}

	while( not_ready )
	{ 
		for( i = 0; i < upper[which].der_nr; i++ )			/*** Fuer alle Ableitungen ***/
		{
			for( j = 0; j < upper[which].node_nr; j++ )		/*** Fuer alle Knoten einer Ableitung ***/
			{
				if( lower[ADR(i, j, der_len)].finished == TRUE )
				{
					break;
				}
				/****** Der Trick ist jetzt: Ich tu einfach so, wie wenn ich ueberall was aendern wollte. In Wirklichkeit ******/
				/****** aendere ich genau einen Eintrag und mache dann sofort break ******/

				/****** Als erstes pruefe ab, ob der aktuelle Knoten schon voll durchpermutiert ist ... ******/
				/****** Oder vom Anfang -1 gesetzt ist und noch nicht Ende einer ableitung erreicht     ******/
				if( ((lower[ADR(i, j, der_len)].what_sizes + 1) == lower[ADR(i, j, der_len)].sizes_nr) ||
				    ((lower[ADR(i, j, der_len)].what_sizes == -1) && (j + 1 < upper[which].node_nr))     )
				{
					/****** Wenn ja, dann fange bei Ihm wieder von vorne an und lasse naechsten ran ******/
					lower[ADR(i, j, der_len)].what_sizes = 0;
				}
				else
				{
					/****** Sonst aendere dich selbst und breake ******/
					what_derivation = i;
					lower[ADR(i, j, der_len)].what_sizes = lower[ADR(i, j, der_len)].what_sizes + 1;

					/****** Jetzt noch aufpassen: Wenn eine Zeile total fertig ist ******/
					/****** sorge dafuer, dass nie mehr was passiert ******/

					ready = TRUE;
					/*** Ueberpruefen, ob ueberaall ganz oben ***/
					for( k = 0; k < upper[which].node_nr; k++ )
					{
						if( lower[ADR(i, k, der_len)].what_sizes + 1 < lower[ADR(i, k, der_len)].sizes_nr )
						{
							ready = FALSE;
							break;
						}
					}

					if( ready )
					{
						for( k = 0; k < upper[which].node_nr; k++ )
						{
							lower[ADR(i, k, der_len)].finished = TRUE;
						}
					}
					goto exit;
				}
			}
		}
		/****** Wenn wir irgendwann mal hierherkommen sind wir fertig ******/
		not_ready = FALSE;
		break;

		/****** Jetzt mache alle notwendigen Eintragungen ******/
exit:		lpa_make_entry_in_sizes( upper, which, what_derivation, what_entry );
		what_entry++;
	}
}

/*****************************************************************************************
function:	lpa_create_sizes_for_upper
Input:	LPA_Upper_prod	upper, int len

	Erzeugt und berechnet fuer das array upper die moeglichen Groessen.
	which ist wo bei upper eingetragen werden soll.

Output:	---
*****************************************************************************************/

void	lpa_create_sizes_for_upper(LPA_Upper_prod upper, int which)
{
	int		j,		/*** Laufvariable ***/
			sizes_len;	/*** Wie lang werden neue sizes ***/
	LPA_Sizes	new;		/*** Neu erzeugte sizes ***/

	/****** Erzeuge die notwendigen sizes ******/
	sizes_len	= upper[which].sizes_nr;
	new 		= create_lpa_sizes( sizes_len );

	for( j = 0; j < sizes_len; j ++ )			/*** Durchlaufe alle sizes fuer Aktuelle obere Produktion ***/
	{
		/****** Durchlaufe die neuen sizes und erzeuge eigene Dependency ******/
		new[j].grid		= upper[which].prod_layout[0].grid;
		new[j].x_dependency	= lpa_create_copy_of_dependency( upper[which].prod_layout[upper[which].prod_array_nr].x_dependency );
		new[j].y_dependency	= lpa_create_copy_of_dependency( upper[which].prod_layout[upper[which].prod_array_nr].y_dependency );
	}
	upper[which].sizes = new;
	/*** und jetzt wie es unten weitergeht ***/
	lpa_create_derivation_in_sizes( upper, which );
		
	for( j = 0; j < sizes_len; j ++ )			/*** Durchlaufe alle sizes fuer Aktuelle obere Produktion ***/
	{
		/****** Fkt. berechnet die neuen Groessen und Koordinaten mit den eingetragenen Abhaengigkeiten ******/
		lpa_compute_new_layout( new, j );
	}

	/****** Jetzt haben wir alles ausgerechnet. Aber manches ist ueberfluessig ==> rausschmeissen ******/
	upper[which].sizes	= lpa_compact_and_sort_sizes( upper[which].sizes, &sizes_len );
	upper[which].sizes_nr	= sizes_len;
}

/*****************************************************************************************
function:	lpa_create_sizes_array
Input:	tree_node_ref	tree_node

	Berechnet fuer alle LPA_Upper_prod und LPA_Lower_prod die arrays der moeglichen 
	Groessen.

Output:	---
*****************************************************************************************/

void	lpa_create_sizes_array(tree_node_ref tree_node)
{
	LPA_Upper_prod_array	upper_head;			/*** Hilfsvariable ***/
	LPA_Upper_prod		upper_array	= NULL;		/*** Hilfsvariable ***/
	LPA_Lower_prod		lower_array;			/*** Hilfsvariable ***/
	LPA_Sizes		new_sizes;			/*** Neu erzeugtes Feld von Groessen ***/
	int			i, j, k,			/*** Laufvariable ***/
				new_sizes_len	= 0,		/*** Wie Gross wird das Feld der neuen Sizes ***/
				der_len		= 1,		/*** Wieviele Moegliche Groessen fuer eine feste Ableitung ***/
				der_nodes;			/*** Hilfsvariable ***/

	upper_head = tree_node->area_structures;
	if( upper_head )
	{
		upper_array = upper_head->array_head;
	}
	
	if( upper_array )
	{
		/****** 1. Fall: Es existieren keine Datenstrukturen LPA_Lower_prod => Wir sind ganz unten. Einfach einmal Groesse der Produktion ******/
		if( !upper_array[0].lower_array )
		{
			for( i = 0; i < upper_head->number; i++ )
			{
				new_sizes = create_lpa_sizes( 1 );

				new_sizes[0].prod_layout	= upper_array[i].prod_layout;
				new_sizes[0].prod_array_nr	= upper_array[i].prod_array_nr;
				new_sizes[0].width		= new_sizes[0].prod_layout[new_sizes[0].prod_array_nr].width;
				new_sizes[0].height		= new_sizes[0].prod_layout[new_sizes[0].prod_array_nr].height;
				new_sizes[0].skaled_width	= new_sizes[0].prod_layout[new_sizes[0].prod_array_nr].skaled_width;
				new_sizes[0].skaled_height	= new_sizes[0].prod_layout[new_sizes[0].prod_array_nr].skaled_height;
				new_sizes[0].grid		= new_sizes[0].prod_layout[new_sizes[0].prod_array_nr].grid;
				new_sizes[0].x_dependency	= lpa_create_copy_of_dependency( 
										new_sizes[0].prod_layout[new_sizes[0].prod_array_nr].x_dependency );
				new_sizes[0].y_dependency	= lpa_create_copy_of_dependency( 
										new_sizes[0].prod_layout[new_sizes[0].prod_array_nr].y_dependency );

				upper_array[i].sizes	= new_sizes;
				upper_array[i].sizes_nr	= 1;
			}
		}
		/****** 2. Fall: Es existieren LPA_Lower_prod => Permutieren ueber saemtlche Moeglichkeiten ******/
		else
		{
			/****** Was alles gemacht werden muss: ******/
			/****** Zuerst Zaehle, wieviele Moeglichkeiten es gibt, eine bestimmte obere Produktion abzuleiten, ******/
			/****** also: Summe ueber ( Produkt ueber# Sizes )  jeder moeglichen Ableitung. ******/

			for( i = 0; i < upper_head->number; i++ )				/*** Fuer alle Produktionen oben ***/
			{
				lower_array = upper_array[i].lower_array;

				for( j = 0; j < upper_array[i].der_nr; j++ )			/*** Fuer alle Ableitungen ***/
				{
					der_nodes	= upper_array[i].node_nr;

					for( k = 0; k < der_nodes; k++ )			/*** Fuer alle Knoten einer festen Ableitung ***/
					{
						/****** Es fehlen noch die Zeiger auf LPA_Sizes von LPA_Lower_prod aus. Die jetzt erzeugen ******/
						lower_array[ADR(j, k, der_nodes)].sizes = 
							lower_array[ADR(j, k, der_nodes)].same_lower[lower_array[ADR(j, k, der_nodes)].lower_nr].sizes;
						lower_array[ADR(j, k, der_nodes)].sizes_nr = 
							lower_array[ADR(j, k, der_nodes)].same_lower[lower_array[ADR(j, k, der_nodes)].lower_nr].sizes_nr;

						/****** Und schon koennen wir weiterrechnen ******/
						der_len = der_len * ZERO_TO_ONE( lower_array[ADR(j, k, der_nodes)].sizes_nr );
					}
					new_sizes_len	= new_sizes_len + der_len;
					der_len		= 1;
				}
				upper_array[i].sizes_nr	= new_sizes_len;
				new_sizes_len		= 0;

				/****** Jetzt steht an der aktuellen LPA_Upper_prod wieviele moegliche Groessen sie haben kann, ******/
				/****** Sie hat alle Zeiger, also kann losgerechnet werden ******/

				lpa_create_sizes_for_upper( upper_array, i );
			}

		}
	}
}

/*****************************************************************************************
function:	lpa_create_sizes_for_derivation_tree
Input:	tree_ref derivation_tree

	Berechnet in einem bottom-up Durchlauf welche verschiedenen Groessen ein Knoten in
	der Ableitung hat. D.h. an der Wurzel kann da die optimale Groesse festgestellt
	werden und in einem top-down Durchlauf kann das neue Layout gezeichnet werden.

	Diese Funktion stellt nur den Durchlauf sicher. Die Hauptarbeit wird in der 
	aufgerufenen Fkt. erledigt.

Output:	---
*****************************************************************************************/

void	lpa_create_sizes_for_derivation_tree(tree_ref derivation_tree)
{
	tree_ref		cur;

	cur = derivation_tree;
	while ( cur != NULL )
	{
		if ( ( cur->tree_rec_type == TREE_NODE ) && 
	             ( cur->tree_rec.node->first_son != NULL ) &&
		     ( !cur->tree_rec.node->leaf )  ) 
		{
			lpa_create_sizes_for_derivation_tree( cur->tree_rec.node->first_son );
		}
		cur = cur->next_brother;
	}


	cur = derivation_tree;
	while ( cur != NULL )
	{
		if ( (cur->tree_rec_type == TREE_NODE) && (cur->tree_rec.node->first_son) )
		{
			lpa_create_sizes_array( cur->tree_rec.node );
		}
		cur = cur->next_brother;
	}
}
