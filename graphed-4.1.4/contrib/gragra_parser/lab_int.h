/* FERTIG 130293 */
/********************************************************************************/
/*										*/
/*	MODUL:	lab_int.c							*/
/*										*/
/*	FUNKTION: String-Integer-Bijektionsliste				*/
/*										*/
/*		Um eine (endliche) Menge von Strings als Bitvektor		*/
/*		abspeichern zu koennen, braucht man eine String-Integer-	*/
/*		Bijektion. Genau diese Bijektion ist in diesem Modul		*/
/*		realisiert.							*/
/*										*/
/*	BESONDERES:								*/
/*		Neben den Prozeduren/Funktionen fuer allgemeine Bijektions-	*/
/*		listen enthaelt dieses Modul auch spezielle Makros fuer 	*/
/*		die vom Parser benoetigten Listen				*/
/*				Edgelabel_list					*/
/*			und	Nodelabel_list. 				*/
/*		Diese Makros sind in lab_int.h definiert.			*/
/*										*/
/********************************************************************************/

#ifndef LAB_INT_HEADER
#define LAB_INT_HEADER

/*--------------------- Datenstruktur ----------------------------*/
/*-->@	-Dlab_int						  */

typedef struct nr_label_list {
	int			nummer;
	char			*label;
	struct nr_label_list	*next;
} *Nr_label_list;

extern	Nr_label_list	Nodelabel_list;
extern	Nr_label_list	Edgelabel_list;

#define NOT_IN_LIST	-1	/* possible return value of */
				/* LI_get_number_of_label   */

/********************************************************************************/
/*										*/
/*	Exportierte Prozeduren/Funktionen					*/
/*										*/
/********************************************************************************/

extern void	LI_reset_list(Nr_label_list *list);
extern int	LI_add_label(char *s, Nr_label_list *list);
extern int	LI_add_label_with_index(char *s, int index, Nr_label_list *list);
extern int	LI_get_number_of_label(char *s, Nr_label_list list);
extern char	*LI_get_label_of_number(int nr, Nr_label_list list);
extern void	LI_fshow_list(FILE *file, Nr_label_list list);
extern char	*LI_label_string(Nr_label_list list);

/********************************************************************************/
/*										*/
/*	Definition spezieller Bijektionslisten					*/
/*										*/
/********************************************************************************/

/*---------------- Knoten-Labels ---------------*/

#define LI_reset_nodelabel_list() \
		LI_reset_list( &Nodelabel_list )
#define LI_add_nodelabel( str ) \
		LI_add_label( str, &Nodelabel_list )
#define LI_get_number_of_nodelabel( str ) \
		LI_get_number_of_label( str, Nodelabel_list )
#define LI_get_nodelabel_of_number( nr ) \
		LI_get_label_of_number( nr, Nodelabel_list )
#define LI_fshow_nodelabel_list( file ) \
		LI_fshow_list( file, Nodelabel_list )
#define LI_sizeof_nodelabel_list() \
		((Nodelabel_list == NULL) ? 0 : (Nodelabel_list->nummer))
#define LI_nodelabels_string() \
		LI_label_string( Nodelabel_list )

/*---------------- Kanten-Labels ---------------*/

#define LI_reset_edgelabel_list() \
		LI_reset_list( &Edgelabel_list )
#define LI_add_edgelabel( str ) \
		LI_add_label( str, &Edgelabel_list )
#define LI_get_number_of_edgelabel( str ) \
		LI_get_number_of_label( str, Edgelabel_list )
#define LI_get_edgelabel_of_number( nr ) \
		LI_get_label_of_number( nr, Edgelabel_list )
#define LI_fshow_edgelabel_list( file ) \
		LI_fshow_list( file, Edgelabel_list )
#define LI_sizeof_edgelabel_list() \
		((Edgelabel_list == NULL) ? 0 : (Edgelabel_list->nummer))
#define LI_edgelabels_string() \
		LI_label_string( Edgelabel_list )

#endif

