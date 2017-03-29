/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Ebitset								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Fbitset								*/
/*										*/
/*	MODUL: bitset.c 							*/
/*										*/
/*	FUNKTION: Prozeduren und Funktionen fuer die Benutzung des Datentyps	*/
/*		  'Bitset'.							*/
/*		  Bitset ist eine Datenstruktur, die eine Implementierung von	*/
/*		  Teilmengen der natuerlichen Zahlen als Bit-Arrays darstellt.	*/
/*		  Dabei gilt: Ist 'set' vom Typ Bitset, so ist set eine 	*/
/*		  Teilmenge von {0,..,n}, wobei n durch die Funktion		*/
/*		  'BS_init_set' festgelegt wird.				*/
/*										*/
/*	WICHTIG: - Bei Prozeduren/Funktionen, die mehr als einen Parameter vom	*/
/*		  Typ Bitset haben, MUESSEN alle diese Parameter auf GLEICHE	*/
/*		  GROESSE initialisiert worden sein!				*/
/*										*/
/*		 - Konvention: fuer (Bitset-)Variable set gilt: 		*/
/*		   aus set!=NULL folgt (bzw. muss folgen) set->set != NULL	*/ 
/*										*/
/********************************************************************************/

#include "misc.h"
#include "bitset.h"

#define NR_OF_INTS( size )	(((size) + 8*sizeof(int)) / (8*sizeof(int)))
#define ERROR(x)		fprintf(stderr,(x))

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*-->@	-Pbitset	  

	int	BS_init_set		(Bitset *set, int size);
	int	BS_delete_set		(Bitset *set);
	int	BS_clear_set		(Bitset set);
	int	BS_reverse_set		(Bitset set);
	void	BS_include		(Bitset set, int elem);
	void	BS_exclude		(Bitset set, int elem);
	void	BS_union		(Bitset set1, set2, tset);
	void	BS_intersection 	(Bitset set1, set2, tset);
	int	BS_empty_intersection	(Bitset set1, set2);
	int	BS_equal_sets		(Bitset set1, set2);
	int	BS_is_in_set		(Bitset set1, int elem); 
	int	BS_is_subset		(Bitset sub, main); 
	void	BS_fprintf	(FILE *stream, Bitset set, BS_display_mode mode);
	void	BS_store_size		(int identifier, size);
	int	BS_get_size		(int identifier);
**/


/********************************************************************************/
/*										*/
/*-->	BS_init_set								*/
/*										*/
/*	PARAMETER:	1. Bitset *Set		(VAR-Parameter!)		*/
/*			2. int	  size						*/
/*										*/
/*	ZURUECK:	TRUE, gdw. Datenstruktur fuer Set erfolgreich		*/
/*			aufgebaut wurde.					*/
/*										*/
/*	AUFGABE:	- Speicheranforderung fuer die Datenstruktur Bitset	*/
/*			- Aufbau der Datenstruktur				*/
/*			- Initialisiere: set := leere Menge			*/
/*										*/
/*	BESONDERES:	aus BS_init_set(..)==FALSE folgt Set==(Bitset)NULL	*/
/*										*/
/********************************************************************************/

int	BS_init_set(Bitset *Set, int size)	
      	     		/* call bitset BY REFERENCE! */
   	     
{
	int	bitset_intarray;

	*Set = (Bitset) w_malloc( sizeof( struct bitset ) );
	if( *Set == (Bitset)NULL ) {
		ERROR(( "WARNING: failed to create bitset in BS_init_set !\n" ));
		return FALSE;
	}
	bitset_intarray = NR_OF_INTS(size) * sizeof(int);
	(*Set)->set = (int *) w_malloc( bitset_intarray );
	if( (*Set)->set != (int *)NULL ) {
		(*Set)->size = size;
		BS_clear_set( *Set );
		return TRUE;
	} else {
		w_free((char *) *Set );
		*Set = (Bitset)NULL;
		ERROR(( "WARNING: failed to create int-array in BS_init_set !\n" ));
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	BS_delete_set								*/
/*										*/
/*	PARAMETER:	Bitset *Set	 (VAR-Parameter)			*/
/*										*/
/*	ZURUECK:	TRUE, gdw. Datenstruktur fuer Set erfolgreich		*/
/*			freigegeben wurde.					*/
/*										*/
/*	AUFGABE:	- Speicherfreigabe der Datenstruktur Bitset		*/
/*			- setze Set auf NULL					*/
/*										*/
/********************************************************************************/

int	BS_delete_set(Bitset *Set)
{
	if( (*Set) != (Bitset)NULL ) {
		if( (*Set)->set != (int *)NULL ) {
			w_free((char *) (*Set)->set );
		} else {
			ERROR(( "NULL-pointer in BS_delete_set !\n" ));
			return FALSE;
		}
		w_free((char *) *Set );
		*Set = (Bitset) NULL;
		return TRUE;
	} else {
		ERROR(( "NULL-pointer in BS_delete_set !\n" ));
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	BS_clear_set								*/
/*										*/
/*	PARAMETER:	Bitset set						*/
/*										*/
/*	ZURUECK:	FALSE, gdw. Datenstruktur von set fehlerhaft		*/
/*										*/
/*	AUFGABE:	- set := leere Menge					*/
/*										*/
/********************************************************************************/

int	BS_clear_set(Bitset set)
{
	int	help, *lauf;
	
	if( (set != (Bitset)NULL) && (set->set != (int *)NULL) ) {
		help = NR_OF_INTS(set->size);
		lauf = set->set;
		while( help > 0 ) {
			*lauf = 0;
			lauf++;
			help--;
		}
		return TRUE;
	} else {
		ERROR(( "NULL-pointer in BS_clear_set !\n" ));
		return FALSE;
	}
}

/********************************************************************************/
/*										*/
/*-->	BS_reverse_set								*/
/*										*/
/*	PARAMETER:	Bitset set						*/
/*										*/
/*	ZURUECK:	FALSE, gdw. Datenstruktur von set fehlerhaft		*/
/*										*/
/*	AUFGABE:	invertiere Menge:					*/
/*			set := { 1, .., SIZE(set) }  \	set			*/
/*										*/
/*	BESONDERES:	0 (NULL) ist anschliessend nicht in set enthalten!	*/
/*										*/
/********************************************************************************/

int	BS_reverse_set(Bitset set)
{
	int	help, *lauf, mask;
	
	if( (set != (Bitset)NULL) && (set->set != (int *)NULL) ) {
		help = NR_OF_INTS(set->size);
		lauf = set->set;
		while( help > 0 ) {
			*lauf ^= -1;
			lauf++;
			help--;
		}
		BS_exclude( set, 0 );
		lauf--;
		mask = ~(-1 << ((set->size % (8*sizeof(int)) +1)));
		*lauf &= mask;
		return TRUE;
	} else {
		ERROR(( "NULL-pointer in BS_reverse_set !\n" ));
		return FALSE;
	}
}
	
/********************************************************************************/
/*										*/
/*-->	BS_include								*/
/*										*/
/*	PARAMETER:	1. Bitset set						*/
/*			2. int	  elem						*/
/*										*/
/*	AUFGABE:	- set := set u {elem}					*/
/*										*/
/*	BESONDERES:	hat keine Wirkung falls elem nicht in {0,..,set->size}	*/
/*										*/
/********************************************************************************/

void	BS_include(Bitset set, int elem)
{
	int	pos, bitnr;
	
	if( (set == (Bitset)NULL) || (set->set == (int *)NULL) ) {
		ERROR(( "NULL-pointer in BS_include !\n" ));
		return;
	}

	if( (elem < 0) || (elem>set->size) ) {
		return;
	}
	pos = elem / (8*sizeof(int));
	bitnr = elem % (8*sizeof(int));
	
	set->set[pos] |= (1<<bitnr);
}

/********************************************************************************/
/*										*/
/*-->	BS_exclude								*/
/*										*/
/*	PARAMETER:	1. Bitset set						*/
/*			2. int	  elem						*/
/*										*/
/*	AUFGABE:	- set := set \ {elem}					*/
/*										*/
/*	BESONDERES:	hat keine Wirkung falls elem nicht in {0,..,set->size}	*/
/*										*/
/********************************************************************************/

void	BS_exclude(Bitset set, int elem)
{
	int	pos, bitnr;
	
	if( (set == (Bitset)NULL) || (set->set == (int *)NULL) ) {
		ERROR(( "NULL-pointer in BS_exclude !\n" ));
		return;
	}

	if( (elem < 0) || (elem>set->size) ) {
		return;
	}
	pos = elem / (8*sizeof(int));
	bitnr = elem % (8*sizeof(int));
	
	set->set[pos] &= ~(1<<bitnr);
}

/********************************************************************************/
/*										*/
/*-->	BS_union								*/
/*										*/
/*	PARAMETER:	1. Bitset set1						*/
/*			2. Bitset set2						*/
/*			3. Bitset tset						*/
/*										*/
/*	AUFGABE:	- tset := set1 u set2					*/
/*										*/
/*	BESONDERES:	- Die Groessen von set1, set2 und tset muessen gleich	*/
/*			  sein. 						*/
/*			- der alte "Inhalt" von tset wird ueberschrieben.	*/
/*			- man darf auch set1 od. set2 als tset uebergeben.	*/
/*										*/
/********************************************************************************/

void	BS_union(Bitset set1, Bitset set2, Bitset tset)
{
	int	count,	*lauf1, *lauf2, *tlauf;
	if( (set1==(Bitset)NULL) || (set2==(Bitset)NULL) || (tset==(Bitset)NULL) ) {
		ERROR(( "NULL-pointer in BS_union !\n" ));
		return;
	}
	if( (set1->size != set2->size) || (set2->size != tset->size) ) {
		ERROR(( "different sizes of set1,set2 and tset in BS_union!\n" ));
		return;
	} else {
		count = NR_OF_INTS( set1->size );
		lauf1 = set1->set;
		lauf2 = set2->set;
		tlauf = tset->set;
		while( count > 0 ) {
			*tlauf = (*lauf1) | (*lauf2);
			tlauf++; lauf1++; lauf2++;
			count--;
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	BS_intersection 							*/
/*										*/
/*	PARAMETER:	1. Bitset set1						*/
/*			2. Bitset set2						*/
/*			3. Bitset tset						*/
/*										*/
/*	AUFGABE:	- tset := set1 n set2					*/
/*										*/
/*	BESONDERES:	- Die Groessen von set1, set2 und tset muessen gleich	*/
/*			  sein. 						*/
/*			- der alte "Inhalt" von tset wird ueberschrieben.	*/
/*			- man darf auch set1 od. set2 als tset uebergeben.	*/
/*										*/
/********************************************************************************/

void	BS_intersection(Bitset set1, Bitset set2, Bitset tset)
{
	int	count,	*lauf1, *lauf2, *tlauf;
	if( (set1==(Bitset)NULL) || (set2==(Bitset)NULL) || (tset==(Bitset)NULL) ) {
		ERROR(( "NULL-pointer in  !\n" ));
		return;
	}
	if( (set1->size != set2->size) || (set2->size != tset->size) ) {
		ERROR(( "different sizes of set1,set2 and tset in BS_intersection!\n" ));
		return;
	} else {
		count = NR_OF_INTS( set1->size );
		lauf1 = set1->set;
		lauf2 = set2->set;
		tlauf = tset->set;
		while( count > 0 ) {
			*tlauf = (*lauf1) & (*lauf2);
			tlauf++; lauf1++; lauf2++;
			count--;
		}
	}
}

/********************************************************************************/
/*										*/
/*-->	BS_empty_intersection							*/
/*										*/
/*	PARAMETER:	1. Bitset set1						*/
/*			2. Bitset set2						*/
/*										*/
/*	ZURUECK:	TRUE  gdw. 1. Datenstrukturen o.k.			*/
/*				   2. Groessen von set1 und set2 gleich und	*/
/*				   3. (set1 n set2) == O (leer) 		*/
/*										*/
/*	AUFGABE:	- teste, ob leerer Durchschnitt 			*/
/*										*/
/*	BESONDERES:	- Die Groessen von set1 und set2 muessen gleich 	*/
/*			  sein. 						*/
/*										*/
/********************************************************************************/

int	BS_empty_intersection(Bitset set1, Bitset set2)
{
	int	count,	*lauf1, *lauf2;
	if( (set1==(Bitset)NULL) || (set2==(Bitset)NULL) ) {
		ERROR(("NULL-pointer in BS_empty_intersection!\n"));
		return TRUE;
	}
	if( (set1->size != set2->size) ) {
		ERROR(( "different sizes of set1 and set2 in BS_empty_intersection!\n" ));
		return TRUE;
	} else {
		count = NR_OF_INTS( set1->size );
		lauf1 = set1->set; lauf2 = set2->set;
		while( count > 0 ) {
			if( (*lauf1 & *lauf2) != 0 ) {
				return FALSE;
			} else {
				count--;
				lauf1++; lauf2++;
			}
		}
		return TRUE;
	}
}

/********************************************************************************/
/*										*/
/*-->	BS_equal_sets								*/
/*										*/
/*	PARAMETER:	1. Bitset set1						*/
/*			2. Bitset set2						*/
/*										*/
/*	ZURUECK:	FALSE gdw. 1. Datenstrukturen o.k.			*/
/*				   2. Groessen von set1 und set2 gleich und	*/
/*				   3. set1 == set2				*/
/*										*/
/*	AUFGABE:	- teste, ob zwei Mengen gleich sind			*/
/*										*/
/*	BESONDERES:	- Die Groessen von set1 und set2 muessen gleich 	*/
/*			  sein. 						*/
/*										*/
/********************************************************************************/

int	BS_equal_sets(Bitset set1, Bitset set2)
{
	int	count,	*lauf1, *lauf2;
	if( (set1==(Bitset)NULL) || (set2==(Bitset)NULL) ) {
		ERROR(("NULL-pointer in BS_empty_intersection!\n"));
		return TRUE;
	}
	if( (set1->size != set2->size) ) {
		ERROR(( "different sizes of set1 and set2 in BS_empty_intersection!\n" ));
		return TRUE;
	} else {
		count = NR_OF_INTS( set1->size );
		lauf1 = set1->set; lauf2 = set2->set;
		while( count > 0 ) {
			if( *lauf1 != *lauf2 ) {
				return FALSE;
			} else {
				count--;
				lauf1++; lauf2++;
			}
		}
		return TRUE;
	}
}
	

/********************************************************************************/
/*										*/
/*-->	BS_is_in_set								*/
/*										*/
/*	PARAMETER:	1. Bitset set1						*/
/*			2. int	  elem						*/
/*										*/
/*	ZURUECK:	TRUE gdw.  1. Datenstrukturen o.k.			*/
/*				   2. elem in set enthalten			*/	      
/*										*/
/*	AUFGABE:	- teste, ob Element in Menge enthalten			*/
/*										*/
/*	BESONDERES:	- Resultat FALSE, falls elem ausserhalb von		*/
/*			  {0,..,set->size} (oder set==NULL)			*/
/*										*/
/********************************************************************************/

int	BS_is_in_set(Bitset set, int elem)
{
	int	pos, bitnr;
	
	if( (set == (Bitset)NULL) || (set->set == (int *)NULL) ) {
		ERROR(( "NULL-pointer in BS_is_in_set !\n" ));
		return FALSE;
	}

	if( (elem<0) || (elem>set->size) ) {
		return FALSE;
	}
		
	pos = elem / (8*sizeof(int));
	bitnr = elem % (8*sizeof(int));
	
	return ((set->set[pos] & (1<<bitnr)) != 0 );
}

/********************************************************************************/
/*										*/
/*-->	BS_is_subset								*/
/*										*/
/*	PARAMETER:	1. Bitset sub						*/
/*			2. Bitset main						*/
/*										*/
/*	ZURUECK:	TRUE  gdw. 1. Datenstrukturen o.k.			*/
/*				   2. Groessen von sub und main gleich und	*/
/*				   3. sub in main enthalten			*/
/*										*/
/*	AUFGABE:	- teste, ob eine Menge in einer anderen enthalten	*/
/*										*/
/*	BESONDERES:	- Die Groessen von sub und main muessen gleich		*/
/*			  sein. 						*/
/*										*/
/********************************************************************************/

int	BS_is_subset(Bitset sub, Bitset main)
{
	int	count, *slauf, *mlauf;
	
	if( (sub == (Bitset)NULL) || (main == (Bitset)NULL) ) {
		ERROR(("NULL-pointer in BS_is_subset !\n" ));
		return FALSE;
	}
	if( (sub->size != main->size) ) {
		ERROR(("different sizes of subset and mainset in BS_is_subset!\n"));
		return FALSE;
	}
	
	count = NR_OF_INTS( sub->size );
	slauf = sub->set; mlauf = main->set;
	while( count > 0 ) {
		if( (*slauf & *mlauf) != *slauf ) {
			return FALSE;
		}
		count--;
		slauf++; mlauf++;
	}
	return TRUE;
}

/********************************************************************************/
/*										*/
/*-->	BS_fprintf								*/
/*										*/
/*	PARAMETER:	1. FILE 		*stream 			*/
/*			2. Bitset		set				*/
/*			3. BS_display_mode	mode				*/
/*										*/
/*	AUFGABE:	- Ausgabe der Bitset 'set' auf Datei 'stream'.		*/
/*										*/
/*	ERKLAERUNG:								*/
/*										*/
/*		BS_fprintf gibt den aktuellen Zustand der Bitset 'set' aus.	*/
/*	Ueber die Variable 'stream' kann man diesen Zustand in eine (bereits	*/
/*	geoeffnete) Datei, oder aber auch auf 'stdout' bzw. 'stderr' schreiben. */
/*	Die Variable 'mode' bestimmt dabei die Art der Ausgabe: 		*/
/*	   AS_BITVECTOR : Ausgabe in der Form '..|.|||.||...|'			*/
/*			  ('|' kennzeichnet dabei ein enthaltenes Element).	*/
/*			  ==> nuetzlich z.B. fuer Bitset-Vergleiche.		*/
/*	   AS_INTLIST	: Ausgabe einer geordneten Liste der enthaltenen	*/
/*			  natuerlichen Zahlen. Am Ende der Liste in Klammern	*/
/*			  die Groesse der Bitset.				*/
/*										*/
/*										*/
/*	BESONDERES:   - Falls die Datenstruktur von 'set' nicht vollstaendig	*/
/*			ist, wird dies ausgedruckt.				*/
/*		      - (das Makro) BS_printf( set, mode) gibt 'set' auf	*/
/*			stdout aus.						*/
/*										*/
/********************************************************************************/

void	BS_fprintf(FILE *stream, Bitset set, BS_display_mode mode)
{
	int	lauf, is_elem;
	if( set == (Bitset)NULL ) {
		fprintf( stream, "Bitset set is NULL!\n" );
		return;
	}
	if( set->set == (int *)NULL ) {
		fprintf( stream, "Bitset set is o.k., but set->set is NULL!\n" );
		return;
	}
	for( lauf=0; lauf<=set->size; lauf++ ) {
		is_elem = BS_is_in_set( set, lauf );
		switch( mode ) {
			case AS_BITVECTOR :
				if( is_elem ) {
					fprintf( stream, "|" );
				} else {
					fprintf( stream, "." );
				}
				break;
			case AS_INTLIST :
				if( is_elem ) {
					fprintf( stream, "%d ", lauf );
				}
				break;
			default:
				break;
		}
	}
	switch( mode ) {
		case AS_BITVECTOR :
			fprintf( stream, "\n" );
			break;
		case AS_INTLIST :
			fprintf( stream, "(%d)\n", set->size );
			break;
		default:
			fprintf( stream, "BS_fprintf: unknown display mode!\n" );
			break;
	}
}

/********************************************************************************/
/*										*/
/*-->	BS_store_size/BS_get_size						*/
/*										*/
/*	ZWECK:	Oftmals kommt es vor, dass bei Verwendung von dynamischen	*/
/*		Bitsets in einem Init-Modul die Groesse der benoetigten 	*/
/*		Sets berechnet wird, die Sets selber aber erst zu einem 	*/
/*		spaeteren Zeitpunkt in einem anderen Modul kreiert werden.	*/
/*										*/
/*		Damit man nun keine globale Variable fuer die Groesse		*/
/*		definieren muss, gibt es BS_store_size und BS_get_size. 	*/
/*		Uber diese Funktionen ist es moeglich, bis zu			*/
/*		BS_MAXIMAL_IDENTIFIER viele Bitset-Groessen zu speichern	*/
/*		und damit ueber das Bitset-Modul anderen Modulen zur Verfuegung */
/*		zu stellen.							*/
/*										*/
/********************************************************************************/

static	int sizebuffer[BS_MAXIMAL_IDENTIFIER+2];

/********************************************************************************/
/*										*/
/*-->@	BS_store_size								*/
/*m	BS_store_size/BS_get_size	(progman submanual)			*/
/*										*/
/*	BS_store_size								*/
/*										*/
/*	PARAMETER:	1. int	identifier					*/
/*			2. int	size						*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	- speichere die Groesse (size) einer Bitset-Art unter	*/
/*			  einem Identifier (pos) ab, damit andere Module ueber	*/
/*			  diesen Ident. die Groesse verwerten koennen.		*/
/*										*/
/*	BESONDERES:	- Der Identifier muss in {0,..,BS_MAXIMAL_IDENTIFIER}	*/
/*			  sein, sonst wird er nicht gespeichert.		*/
/*										*/
/********************************************************************************/


void	BS_store_size(int identifier, int size)
{
	if( (identifier<0) || (identifier>BS_MAXIMAL_IDENTIFIER) ) {
		ERROR(("BS_store_size: invalid size identifier. Set size on BS_STANDARD \n"));
	} else {
		sizebuffer[identifier] = size;
	}
}

/********************************************************************************/
/*										*/
/*-->@	BS_get_size								*/
/*m	BS_store_size/BS_get_size	(progman submanual)			*/
/*										*/
/*	BS_get_size								*/
/*										*/
/*	PARAMETER:	int	identifier					*/
/*										*/
/*	ZURUECK:	(int)	Groesse der mit 'identifier' verbundenen	*/
/*				Bitset-Art					*/
/*										*/
/*	AUFGABE:	- liefert die Groesse einer Bitset-Art			*/
/*										*/
/*	BESONDERES:	- falls 'identifier' nicht in				*/
/*			  {0,..,BS_MAXIMAL_IDENTIFIER} ist, wird die Groesse	*/
/*			  von BS_STANDARD zurueckgegeben.			*/
/*										*/
/********************************************************************************/

int	BS_get_size(int identifier)
{
	if( (identifier<0) || (identifier>BS_MAXIMAL_IDENTIFIER) ) {
		ERROR(("BS_get_size: invalid size identifier. Returned BS_STANDARD \n"));
		return sizebuffer[BS_STANDARD];
	} else {
		return sizebuffer[identifier];
	}
}	


/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mbitset								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fbitset							*/
/*m		-Ebitset							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dbitset							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pbitset							*/
/********************************************************************************/
