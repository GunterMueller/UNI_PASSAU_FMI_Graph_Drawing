/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Elab_int								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Flab_int								*/
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

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include "lab_int.h"

/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*	statische Prozeduren/Funktionen

	static	char	*strsave (s)
*/

/*-->@	-Plab_int (exportierte Prozeduren/Funktionen)

	void	LI_reset_list		( Nr_label_list *list )
	int	LI_add_label		( char *s , Nr_label_list *list )
	int	LI_add_label_with_index ( char *s , int index, Nr_label_list *list )
	int	LI_get_number_of_label	( char *s, Nr_label_list list )
	char	*LI_get_label_of_number ( int nr, Nr_label_list list )
	void	LI_fshow_list		( FILE *file, Nr_label_list list )
	char	*LI_label_string	( Nr_label_list list )

#define LI_reset_nodelabel_list 	()
#define LI_add_nodelabel		( char *str )	returns int
#define LI_get_number_of_nodelabel	( char *str )	returns int
#define LI_get_nodelabel_of_number	( int nr )	returns (char *)
#define LI_fshow_nodelabel_list 	( FILE *file )
#define LI_sizeof_nodelabel_list	()		returns int
#define LI_nodelabels_string		()		returns (char *)

#define LI_reset_edgelabel_list 	()
#define LI_add_edgelabel		( char *str )	returns int
#define LI_get_number_of_edgelabel	( char *str )	returns int
#define LI_get_edgelabel_of_number	( int nr )	returns (char *)
#define LI_fshow_edgelabel_list 	( FILE *file )
#define LI_sizeof_edgelabel_list	()		returns int
#define LI_edgelabels_string		()		returns (char *)

**/

/********************************************************************************/
/*										*/
/*	Variablendeklaration:							*/
/*										*/
/********************************************************************************/

static	char	nlb_empty_string[] = "";

	Nr_label_list	Nodelabel_list = (Nr_label_list)NULL;
	Nr_label_list	Edgelabel_list = (Nr_label_list)NULL;

/********************************************************************************/
/*										*/
/*	strsave 								*/
/*										*/
/*	PARAMETER:	char *s 						*/
/*										*/
/*	ZURUECK:	(char *)   Kopie von s					*/
/*										*/
/*	AUFGABE:	- mache (mittels malloc) eine physikalische Kopie	*/
/*			  von s 						*/
/*										*/
/********************************************************************************/

	
static	char	*strsave (char *s)
{
	char	*saved_s;
	
	if (s != NULL) {
		saved_s = (char *)malloc(strlen(s)+1);
		strcpy (saved_s, s);
	} else {
		saved_s = NULL;
	}

	return saved_s;
}

/********************************************************************************/
/*										*/
/*-->	LI_reset_list								*/
/*										*/
/*	PARAMETER:	Nr_label_list  *list		(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	- den von list belegten Speicher freigeben		*/
/*			- list loeschen und damit neu initialisieren		*/
/*										*/
/********************************************************************************/

void	LI_reset_list(Nr_label_list *list)
             	      		/* call BY REFERENCE ! */
{
	Nr_label_list	hilf,hilf2;
	
	hilf = *list;
	while( hilf != (Nr_label_list)NULL) {
		if( hilf->label != (char *)NULL ) {
			free( hilf->label );
		}
		hilf2 = hilf;
		hilf = hilf->next;
		free( hilf2 );
	}
	*list = (Nr_label_list)NULL;
}

/********************************************************************************/
/*										*/
/*-->	LI_add_label								*/
/*										*/
/*	PARAMETER:	1. char 	   *s					*/
/*			2. Nr_label_list   *list	(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	Bijektionsnummer des Labels s				*/
/*										*/
/*	AUFGABE:	Einfuegen eines Labels in eine Bijektionsliste. 	*/
/*										*/
/*	BESONDERES:	Falls 's' bereits in 'list' enthalten ist, wird nur die */
/*			zugehoerige Nummer zurueckgegeben.			*/
/*										*/
/********************************************************************************/

int	LI_add_label(char *s, Nr_label_list *list)
    		   
             	      		/* call BY REFERENCE ! */
{
	Nr_label_list	hilf;
	int		testres;
	
	if( (s==(char *)NULL) || (s[0]=='\0') ) {
		s = nlb_empty_string;
	}
	testres = LI_get_number_of_label( s, *list );
	if( testres == NOT_IN_LIST ) {
		hilf = (Nr_label_list)malloc( sizeof( struct nr_label_list ) );
		if( hilf != (Nr_label_list)NULL ) {
			if( *list != (Nr_label_list)NULL ) {
				hilf->nummer = (*list)->nummer + 1;
			} else {
				hilf->nummer = 1;
			}
			hilf->label = strsave( s );
			hilf->next = *list;
			*list = hilf;
			return hilf->nummer;
		} else {
			return NOT_IN_LIST;
		}
	} else {
		return testres;
	}
}

/********************************************************************************/
/*										*/
/*-->	LI_add_label_with_index 						*/
/*										*/
/*	PARAMETER:	1. char 	   *s					*/
/*			2. int		   index				*/
/*			3. Nr_label_list   *list	(VAR-Parameter) 	*/
/*										*/
/*	ZURUECK:	Nummer des Labels s falls s schon in list, ansonsten	*/
/*			index (da neu eingefuegt)				*/
/*										*/
/*	AUFGABE:	Einfuegen eines Labels zusammen mit Nummer in eine	*/
/*			Bijektionsliste.					*/
/*										*/
/*	BESONDERES:	Falls 's' bereits in 'list' enthalten ist, wird zu 's'	*/
/*			der kleinere der beiden Indizes zugeordnet. Dieser	*/
/*			wird schliesslich auch zurueckgegeben.			*/
/*										*/
/*			Da der Programmierer in der Lage ist, fuer verschiedene */
/*			Labels den gleichen Index zu vergeben, verlaesst er mit */
/*			dieser Funktion den Pfad der Bijektion. 		*/
/*			Damit stellt 'list' nach Aufruf dieser Fkt. nur noch	*/
/*			eine Label->Index-Abbildung dar, d.h. ein Aufruf von	*/
/*			LI_get_number_of... macht noch Sinn, ein Aufruf von	*/
/*			LI_get_label_of... dagegen keinen mehr. 		*/
/*										*/
/*		Fazit:	VORSICHT mit dieser Funktion!				*/
/*										*/
/********************************************************************************/

int	LI_add_label_with_index(char *s, int index, Nr_label_list *list)
    		   
   		      
             	      		/* call BY REFERENCE ! */
{
	Nr_label_list	hilf;
	
	if( list==(Nr_label_list *)NULL ) {
		return NOT_IN_LIST;
	}
	
	if( (s==(char *)NULL) || (s[0]=='\0') ) {
		s = nlb_empty_string;
	}
	
	/* search for 's' in '*list' and, if found, update entry */
	
	hilf = *list;
	while (hilf != (Nr_label_list)NULL) {
		if( !strcmp(s, hilf->label) ){
			if( index < hilf->nummer ) {
				hilf->nummer = index;
			}
			return hilf->nummer;
		}
		hilf = hilf->next;
	}

	/* 's' is not in list, so we have to insert */
	
	hilf = (Nr_label_list)malloc( sizeof( struct nr_label_list ) );
	if( hilf != (Nr_label_list)NULL ) {
		hilf->nummer = index;
		hilf->label = strsave( s );
		hilf->next = *list;
		*list = hilf;
		return hilf->nummer;
	} else {
		return NOT_IN_LIST;
	}
}

/********************************************************************************/
/*										*/
/*-->	LI_get_number_of_label							*/
/*										*/
/*	PARAMETER:	1. char 	   *s					*/
/*			2. Nr_label_list   list 				*/
/*										*/
/*	ZURUECK:	Bijektionsnummer des Labels s	  oder			*/
/*			NOT_IN_LIST, falls s nicht in list enthalten.		*/
/*										*/
/*	AUFGABE:	Bijektive Abbildung von s auf eine Nummer (via list).	*/
/*										*/
/*	BESONDERES:	KEIN Einfuegen von s (falls nicht in list) !		*/
/*										*/
/********************************************************************************/

int	LI_get_number_of_label(char *s, Nr_label_list list)
{
	if( s == (char *)NULL || s[0] == '\0' ) {
		s = nlb_empty_string;
	}
	while (list != (Nr_label_list)NULL) {
		if( !strcmp(s, list->label) ){
			return list->nummer;
		}
		list = list->next;
	}
	return NOT_IN_LIST;
}

/********************************************************************************/
/*										*/
/*-->	LI_get_label_of_number							*/
/*										*/
/*	PARAMETER:	1. int		   nr					*/
/*			2. Nr_label_list   list 				*/
/*										*/
/*	ZURUECK:	Bijektionslabel der Nummer nr	  oder			*/
/*			"unknown label-number", falls nr nicht in list		*/
/*			enthalten.						*/
/*										*/
/*	AUFGABE:	Bijektive Abbildung von nr auf zug. Label (via list).	*/
/*										*/
/********************************************************************************/

char	*LI_get_label_of_number(int nr, Nr_label_list list)
{
	while (list != (Nr_label_list)NULL) {
		if( nr == list->nummer ){
			return list->label;
		}
		list = list->next;
	}
	return "unknown label-number";
}

/********************************************************************************/
/*										*/
/*-->	LI_fshow_list								*/
/*										*/
/*	PARAMETER:	1. FILE 	   *file				*/
/*			2. Nr_label_list   list 				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ausgabe der Bijektionsliste 'list' auf das (bereits	*/
/*			geoeffnete) File 'file'.				*/
/*										*/
/********************************************************************************/

void	LI_fshow_list(FILE *file, Nr_label_list list)
{
	while( list != (Nr_label_list)NULL ) {
		fprintf( file, "%s(%d), ", list->label, list->nummer );
		list = list->next;
	}
	fprintf( file, "\n" );
}

/********************************************************************************/
/*										*/
/*-->	LI_label_string 							*/
/*										*/
/*	PARAMETER:	Nr_label_list	list					*/
/*										*/
/*	ZURUECK:	char *							*/
/*										*/
/*	AUFGABE:	Rueckgabe der Bijektionsliste 'list' als String.	*/
/*										*/
/*	BESONDERES:	Der Rueckgabestring wird NICHT mitteles malloc angelegt.*/
/*			Dies muss u.U. selber gemacht werden.			*/
/*										*/
/********************************************************************************/

char	*LI_label_string(Nr_label_list list)
{
	static	char	result[4096], tmp[256];
	int	len_result;
	
	result[0] = '\0';
	len_result = 0;
	while( list != (Nr_label_list)NULL ) {
		sprintf( tmp, "'%s' ", list->label );
		len_result += strlen( tmp );
		if( len_result >= 4096 ) {
			break;
		}
		strcat( result, tmp );
		list = list->next;
	}
	return result;
}
	
/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mlab_int								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Flab_int							*/
/*m		-Elab_int							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dlab_int							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Plab_int							*/
/********************************************************************************/
