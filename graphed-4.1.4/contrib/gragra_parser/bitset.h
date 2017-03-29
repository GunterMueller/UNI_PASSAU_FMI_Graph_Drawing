/* FERTIG 130293 */
/********************************************************************************/
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

#ifndef BITSET_HEADER
#define BITSET_HEADER

/*-->@	-Dbitset	*/

typedef struct bitset {
		int		size;
		int		*set;
	} *Bitset;

typedef enum {
		AS_BITVECTOR,
		AS_INTLIST,
		BS_number_of_display_modes
	} BS_display_mode;

/********************************************************************************/
/*										*/
/*	EXPORTIERTE Prozeduren/Funktionen					*/
/*										*/
/********************************************************************************/

extern	int	BS_init_set(Bitset *Set, int size);
extern	int	BS_delete_set(Bitset *Set);
extern	int	BS_clear_set(Bitset set);
extern	int	BS_reverse_set(Bitset set);
extern	void	BS_include(Bitset set, int elem);
extern	void	BS_exclude(Bitset set, int elem);
extern	void	BS_union(Bitset set1, Bitset set2, Bitset tset);
extern	void	BS_intersection(Bitset set1, Bitset set2, Bitset tset);

extern	int	BS_empty_intersection(Bitset set1, Bitset set2);
extern	int	BS_equal_sets(Bitset set1, Bitset set2);
extern	int	BS_is_in_set(Bitset set, int elem);
extern	int	BS_is_subset(Bitset sub, Bitset main);

extern	void	BS_fprintf(FILE *stream, Bitset set, BS_display_mode mode);

#define BS_printf( set, mode )	BS_fprintf(stdout,(set),(mode))

extern	void	BS_store_size(int identifier, int size);
extern	int	BS_get_size(int identifier);

#define BS_MAXIMAL_IDENTIFIER	19
#define BS_STANDARD		BS_MAXIMAL_IDENTIFIER+1

#endif

