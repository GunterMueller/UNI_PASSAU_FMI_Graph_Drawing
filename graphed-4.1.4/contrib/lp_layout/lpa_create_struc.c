#include "misc.h"
#include "graph.h"
#include "group.h"
#include "find.h"
#include <ctype.h>

#include "lp_general_functions.h"

#include "lpa_create_struc.h"

/*****************************************************************************************
function:	create_lpa_array_of_productions
Input:	---

	Allokiert Speicherplatz s fuer Datenstruktur lpa_array_of_productions

Output:	Adresse von s
*****************************************************************************************/

LPA_Array_of_productions	create_lpa_array_of_productions(void)
{
	LPA_Array_of_productions	new;

	new = (LPA_Array_of_productions)mymalloc( sizeof(struct lpa_array_of_productions) );

	new->number	= 0;
	new->array_head	= NULL;

	return( new );
}

/*****************************************************************************************
function:	create_lpa_production
Input:	int	number

	Allokiert Speicherplatz s fuer number mal Datenstruktur lpa_production

Output:	Adresse von s
*****************************************************************************************/

LPA_Production	create_lpa_production(int number)
{
	int		i;
	LPA_Production	new;

	new = (LPA_Production)mycalloc( number, sizeof(struct lpa_production) );

	for( i = 0; i < number; i++ )
	{
		new[i].production	= NULL;
		new[i].x_dependency	= NULL;
		new[i].y_dependency	= NULL;
		new[i].width		= 0;
		new[i].height		= 0;
		new[i].skaled_width	= 0;
		new[i].skaled_height	= 0;
		new[i].grid		= 0;
	}

	return( new );
}

/*****************************************************************************************
function:	create_lpa_dependency
Input:	---

	Allokiert Speicherplatz s fuer Datenstruktur lpa_dependency

Output:	Adresse von s
*****************************************************************************************/

LPA_Dependency	create_lpa_dependency(void)
{
	LPA_Dependency	new;

	new = (LPA_Dependency)mymalloc( sizeof(struct lpa_dependency) );

	new->first_border		= NULL;
	new->next			= NULL;
	new->copy_iso			= NULL;
	new->prod_node			= NULL;
	new->graph_node			= NULL;
	new->tree_node			= NULL;
	new->side			= UP;
	new->derivation			= NULL;
	new->der_nr			= 0;

	new->prod_coord			= 0;
	new->new_coord			= 0;
	new->old_coord_plus		= 0;
	new->new_coord_plus		= 0;

	new->skaled_prod_coord		= 0;
	new->skaled_new_coord		= 0;
	new->skaled_old_coord_plus	= 0;
	new->skaled_new_coord_plus	= 0;

	new->grid			= 0;

	return( new );
}

/*****************************************************************************************
function:	arg1_is_befor_arg2
Input:	LPA_Dependency	arg1, arg2

	Berechnet, ob arg1 im Abhaengigkeitsgraphen vor arg2 liegen muss.

	ORDNUNGSGESICHTSPUNKTE( in dieser Reihenfolge, neuer Punkt interessant, wenn
	alle vorher gleich sind ):
		- Koordinate		(kleinere zuerst			)
		- Seite			(rechts und unten vor links und oben	)
		- Knoten terminal?	(li (un): terminal vor nonterminal,
					 re (ob): nonterminal vor oben		)

	ACHTUNG: Es kann sein, dass von arg1 und arg2 der Zeiger prod_node auf NULL gesetzt ist.
		(LHS-Node)

Output:	TRUE, wenn arg1 vor arg2 liegen muss
	FALSE sonst
*****************************************************************************************/

int	arg1_is_befor_arg2(LPA_Dependency arg1, LPA_Dependency arg2)
{

	/****** Koordinaten vergleichen ******/
	if( arg1->prod_coord > arg2->prod_coord )
	{
		return( FALSE );
	}

	if( arg1->prod_coord < arg2->prod_coord )
	{
		return( TRUE );
	}

	/****** Seiten vergleichen ******/
	if( ((arg1->side == RIGHT) || (arg1->side == DOWN)) &&
	    ((arg2->side == LEFT ) || (arg2->side == UP  ))   )
	{
		return( TRUE );
	}

	if( ((arg2->side == RIGHT) || (arg2->side == DOWN)) &&
	    ((arg1->side == LEFT ) || (arg1->side == UP  ))   )
	{
		return( FALSE );
	}

	/****** Label anschauen ******/
	if( !arg1->prod_node )
	{
		return( TRUE );
	}

	if( !arg2->prod_node )
	{
		return( FALSE );
	}

	if( (arg1->side == LEFT) || (arg1->side == UP) )
	{
		if( node_is_terminal(arg1->prod_node) )
		{
			return( TRUE );
		}

		if( node_is_terminal(arg2->prod_node) )		/*** arg1 ist non-terminal ***/
		{
			return( FALSE );
		}

		return( TRUE );					/*** arg1 und arg2 stimmen in allen Punkten ueberein ***/
	}

	if( node_is_nonterminal(arg1->prod_node) )		/*** Sie liegen RECHTS oder UNTEN ***/
	{
		return( TRUE );
	}

	if( node_is_nonterminal(arg2->prod_node) )		/*** arg1 ist non-terminal ***/
	{
		return( FALSE );
	}

	return( TRUE );					/*** arg1 und arg2 stimmen in allen Punkten ueberein ***/
}

/*****************************************************************************************
function:	insert_in_lpa_dependency
Input:	LPA_Dependency old, new

	Fuegt new in old sortiert nach prod_coord ein.
	Bei gleichen Koordinaten: Datensegmente die mit Left oder Up markiert sind
		werden hinten angefuegt

	Die schoensten Layouts gibts, wenn folgendermassen einsortiert wird:
		Bei gleicher Koordinate: Rechte (untere) Teile vor den linken (oberen).
		Dadurch wird verhindert, dass ein Knoten, der dort aufhoert, wo ein NON-terminaler
		beginnt, mit diesem vergroessert wird.
		Ausserdem: Auf der linken Seite: Terminal vor nonterminal und auf der
		rechten: nonterminal vor terminal. Dadurch werden terminale Zentriert ueber das
		abgeleitete gesetzt.

Output:	Zeiger auf neue Liste
*****************************************************************************************/

LPA_Dependency	insert_in_lpa_dependency(LPA_Dependency old, LPA_Dependency new)
{
	LPA_Dependency	cur_dep;

	if( !old )
	{
		return( new );
	}

	if( arg1_is_befor_arg2(new, old) )
	{
		new->next = old;
		return( new );
	}

	cur_dep = old;
	while( cur_dep->next )
	{
		/****** Wissen: cur_dep liegt VOR new im Abhaengigkeitsgraphen. Also vergleiche den Nachfolger ******/
		if( arg1_is_befor_arg2(new, cur_dep->next) )
		{
			/****** einsortieren ******/
			new->next	= cur_dep->next;
			cur_dep->next	= new;
			return( old );
		}
		cur_dep = cur_dep->next;
	}
	cur_dep->next = new;
	return( old );
}

/*****************************************************************************************
function:	lpa_create_copy_of_dependency
Input:	LPA_Dependency old

	Erzeugt Kopie k von old

Output:	k
*****************************************************************************************/

LPA_Dependency	lpa_create_copy_of_dependency(LPA_Dependency old)
{
	LPA_Dependency	result	= NULL,
			new;

	while( old )
	{
		new	= create_lpa_dependency();

		old->copy_iso	= new;

		if( old->first_border )
		{
			new->first_border		= old->first_border->copy_iso;
		}
		new->prod_node			= old->prod_node;
		new->graph_node			= old->graph_node;
		new->tree_node			= old->tree_node;
		new->side			= old->side;
		new->derivation			= old->derivation;
		new->der_nr			= old->der_nr;

		new->prod_coord			= old->prod_coord;
		new->new_coord			= old->new_coord;
		new->old_coord_plus		= old->old_coord_plus;
		new->new_coord_plus		= old->new_coord_plus;

		new->skaled_prod_coord		= old->skaled_prod_coord;
		new->skaled_new_coord		= old->skaled_new_coord;
		new->skaled_old_coord_plus	= old->skaled_old_coord_plus;
		new->skaled_new_coord_plus	= old->skaled_new_coord_plus;
		new->grid			= old->grid;

		result	= insert_in_lpa_dependency( result, new );

		old = old->next;
	}

	return( result );
}

/*****************************************************************************************
function:	free_lpa_dependency
Input:	LPA_Dependency dep

	Loescht Speicherplatz von dep

Output:	---
*****************************************************************************************/

void	free_lpa_dependency(LPA_Dependency dep)
{
	LPA_Dependency	cur;

	while( dep )
	{
		cur = dep;
		dep = dep->next;
		free( cur );
	}
}	

/*****************************************************************************************
function:	create_lpa_array_of_nodes
Input:	---

	Allokiert Speicherplatz s fuer Datenstruktur lpa_array_of_nodes

Output:	Adresse von s
*****************************************************************************************/

LPA_Array_of_nodes	create_lpa_array_of_nodes(void)
{
	LPA_Array_of_nodes	new;

	new = (LPA_Array_of_nodes)mymalloc( sizeof(struct lpa_array_of_nodes) );

	new->number	= 0;
	new->array_head	= NULL;

	return( new );
}

/*****************************************************************************************
function:	create_lpa_node
Input:	int number

	Allokiert Speicherplatz s fuer number mal Datenstruktur lpa_node

Output:	Adresse von s
*****************************************************************************************/

LPA_Node	create_lpa_node(int number)
{
	int		i;
	LPA_Node	new;

	new = (LPA_Node)mycalloc( number, sizeof(struct lpa_node) );

	for( i = 0; i < number; i++ )
	{
		new[i].node	= NULL;
	}

	return( new );
}

/*****************************************************************************************
function:	create_lpa_upper_prod_array
Input:	---

	Allokiert Speicherplatz s fuer Datenstruktur lpa_upper_prod_array

Output:	Adresse von s
*****************************************************************************************/

LPA_Upper_prod_array	create_lpa_upper_prod_array(void)
{
	LPA_Upper_prod_array	new;

	new = (LPA_Upper_prod_array)mymalloc( sizeof(struct lpa_upper_prod_array) );

	new->number	= 0;
	new->array_head	= NULL;

	return( new );
}

/*****************************************************************************************
function:	create_lpa_upper_prod
Input:	int nr

	Allokiert Speicherplatz s fuer nr mal Datenstruktur lpa_upper_prod

Output:	Adresse von s
*****************************************************************************************/

LPA_Upper_prod	create_lpa_upper_prod(int nr)
{
	int		i;
	LPA_Upper_prod	new;

	new = (LPA_Upper_prod)mycalloc( nr, sizeof(struct lpa_upper_prod) );

	for( i = 0; i < nr; i++ )
	{
		new[i].lower_array	= NULL;
		new[i].der_nr		= 0;
		new[i].node_nr		= 0;
		new[i].tree_node	= NULL;
		new[i].prod_layout	= NULL;
		new[i].prod_array_nr	= 0;
		new[i].sizes		= NULL;
		new[i].sizes_nr		= 0;
	}

	return( new );
}

/*****************************************************************************************
function:	create_lpa_lower_prod
Input:	int height, width

	Allokiert Speicherplatz s fuer height* width mal Datenstruktur lpa_lower_prod
	ACHTUNG: Aufpassen beim Zugriff, da ein eigentlich 2-dimensionales Array
		 durch ein ein-dimensionales repraesentiert wird

Output:	Adresse von s
*****************************************************************************************/

LPA_Lower_prod	create_lpa_lower_prod(int width, int height)
{
	LPA_Lower_prod	new;
	int 		i;

	new = (LPA_Lower_prod)mycalloc( width * height, sizeof(struct lpa_lower_prod) );

	for( i = 0; i < width * height; i++ )
	{
		new[i].upper_prod	= NULL;
		new[i].lower_prod	= NULL;
		new[i].same_lower	= NULL;
		new[i].lower_nr		= 0;
		new[i].sizes		= NULL;
		new[i].sizes_nr		= 0;
		new[i].what_sizes	= -1;	/*** Wichtig fuer den Algorithmus der ausrechnet ***/
						/*** welche Ableitungen moeglich sind ***/
		new[i].finished		= FALSE;
	}

	return( new );
}

/*****************************************************************************************
function:	create_copy_of_lpa_lower_array
Input:	LPA_Lower_prod source, int width, height

	Erzeugt eine Kopie k von source

Output:	k
*****************************************************************************************/

LPA_Lower_prod	create_copy_of_lpa_lower_array(LPA_Lower_prod source, int width, int height)
{
	LPA_Lower_prod	new;
	int		i, j;

	new = create_lpa_lower_prod( width, height );

	for( i = 0; i < height; i++ )
	{
		for( j = 0; j < width; j++ )
		{
			new[ADR(i, j, width)].upper_prod	= source[ADR(i, j, width)].upper_prod;
			new[ADR(i, j, width)].lower_prod	= source[ADR(i, j, width)].lower_prod;
			new[ADR(i, j, width)].same_lower	= source[ADR(i, j, width)].same_lower;
			new[ADR(i, j, width)].lower_nr		= source[ADR(i, j, width)].lower_nr;
			new[ADR(i, j, width)].sizes		= source[ADR(i, j, width)].sizes;
			new[ADR(i, j, width)].sizes_nr		= source[ADR(i, j, width)].sizes_nr;
		}
	}

	return( new );
}


/*****************************************************************************************
function:	create_lpa_sizes
Input:	int nr

	Allokiert Speicherplatz s fuer nr mal Datenstruktur lpa_sizes

Output:	Adresse von s
*****************************************************************************************/

LPA_Sizes	create_lpa_sizes(int nr)
{
	LPA_Sizes	new;
	int		i;

	new = (LPA_Sizes)mycalloc( nr, sizeof(struct lpa_sizes) );

	for( i = 0; i < nr; i++ )
	{
		new[i].width		= 0;
		new[i].height		= 0;
		new[i].skaled_width	= 0;
		new[i].skaled_height	= 0;
		new[i].grid		= 0;
		new[i].prod_layout	= NULL;
		new[i].prod_array_nr	= 0;
		new[i].x_dependency	= NULL;
		new[i].y_dependency	= NULL;
		new[i].is_in_opt	= TRUE;
	}

	return( new );
}

/*****************************************************************************************
function:	lpa_compact_and_sort_sizes
Input:	LPA_Sizes sizes, int *len

	sizes soll Kompkatifiziert und nach x_Koordinate aufsteigend sortiert werden.
	len gibt die Laenge von sizes an.
	ACHTUNG: len wird veraendert und gibt nach Ausfuehrung der Funktion die Laenge
		 des neuen arrays an.
	Berechnung der neuen Laenge: Fange mi alter an und Ziehe immer eins ab, wenn
		 etwas aus dem Array rausfaellt.

Output:	Veraendertes Array
*****************************************************************************************/

LPA_Sizes	lpa_compact_and_sort_sizes(LPA_Sizes sizes, int *len)
{
	int		i, j,					/*** Laufvariable ***/
			w_of_i, w_of_j,				/*** Hilfsvariable zum abspeichern der Breite der zu vergleichenden Sizes ***/
			h_of_i, h_of_j,				/*** Hilfsvariable zum abspeichern der Hoehe der zu vergleichenden Sizes ***/
			new_len			= *len;		/*** Berechnet die neue Laenge ***/
	LPA_Sizes	new;					/*** Neu erzeugt sizes, die zurueckgeschickt werden ***/

	for( i = 0; i < *len; i++ )
	{
		for( j = 0; j < i; j++ )
		{
			if( sizes[j].is_in_opt )
			{
				w_of_i = sizes[i].width;
				h_of_i = sizes[i].height;
				w_of_j = sizes[j].width;
				h_of_j = sizes[j].height;


				if( (w_of_j <= w_of_i) && (h_of_j <= h_of_i) ) 
				{
					sizes[i].is_in_opt	= FALSE;
					new_len			= new_len -1;
					goto exit;
				}

				if( (w_of_j >= w_of_i) && (h_of_j >= h_of_i) ) 
				{
					sizes[j].is_in_opt	= FALSE;
					new_len			= new_len -1;
				}
			}
		}
exit:	;
	}

	new = create_lpa_sizes( new_len );
	j = 0;					/*** j gibt jetzt immer an, an welcher Stelle im Neuen Array eingetragen wird ***/

	for( i = 0; i < *len; i++ )		/*** Laufvariable fuer das alte array ***/
	{
		if( sizes[i].is_in_opt )
		{
			new[j].width		= sizes[i].width;
			new[j].height		= sizes[i].height;
			new[j].skaled_width	= sizes[i].skaled_width;
			new[j].skaled_height	= sizes[i].skaled_height;
			new[j].grid		= sizes[i].grid;
			new[j].prod_layout	= sizes[i].prod_layout;
			new[j].prod_array_nr	= sizes[i].prod_array_nr;
			new[j].x_dependency	= sizes[i].x_dependency;
			new[j].y_dependency	= sizes[i].y_dependency;
			j = j + 1;
		}
		else /*** Loesche ueberfluessige Datenstrukturen an sizes[i] damit nachher sizes geloescht werden kann ***/
		{
			free_lpa_dependency	( sizes[i].x_dependency );
			free_lpa_dependency	( sizes[i].y_dependency );
		}
	}

	/****** Loesche altes array ******/
	free( sizes );


	/****** Kompakt ist es jetzt. Jetzt muss noch sortiert werden ******/

	*len = new_len;
	return( new );	
}

/*****************************************************************************************
function:	free_area_structures
Input:	LPA_Upper_prod_array head

	Loescht Datenstrukturen fuer oben und unten, die an head haengen

Output:	---
*****************************************************************************************/

void	free_area_structures(LPA_Upper_prod_array head)
{
	int	i;

	for( i = 0; i < head->number; i++ )
	{
		free( head->array_head[i].lower_array );
	}
	free( head->array_head );
	free( head );
}
