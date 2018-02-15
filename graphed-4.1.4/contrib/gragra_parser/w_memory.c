/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Ew_memory								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Fw_memory								*/
/*										*/
/*	MODUL:	w_memory							*/
/*										*/
/*	FUNKTION: ermoeglicht einem Programmierer, bei der Entwicklung eines	*/
/*		Programms die korrekte Freigabe des angeforderten Speichers	*/
/*		zu ueberpruefen.						*/
/*										*/
/*	ARBEITSWEISE:	anstelle von						*/
/*				#include <malloc.h>				*/
/*			setze							*/
/*				#include "w_memory.h"				*/
/*										*/
/*			anstelle von malloc(..) & free(..) setze		*/
/*			w_malloc(..) & w_free(..).				*/
/*										*/
/*		Danach werden in allen Modulen, in denen MEM_DEBUG definiert	*/
/*		ist (via #define), Speicheranforderungen bzw. -freigaben	*/
/*		mitprotokolliert.						*/
/*		Die aktuelle Speicherbelegung (des Protokolls) kann mittels	*/
/*		w_mem_remain() angezeigt werden (natuerlich muss auch hier	*/
/*		MEM_DEBUG definiert sein).					*/
/*		Naeheres siehe noch w_malloc, w_free, w_...			*/
/*										*/
/********************************************************************************/
/*-->@	-Pw_memory								*/
/*										*/
/*		w_reset_observation	()					*/
/*		w_free_remain		()					*/
/*		w_mem_remain		()					*/
/*		w_free			( char	*address       )		*/
/*	char	*w_malloc		( int	amount	       )		*/
/*	char	*w_strsave		( char	*string        )		*/
/*		w_set_malloc_error_func ( void (*error_func)() )		*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	w_reset_observation							*/
/*-->@	w_free_remain								*/
/*-->@	w_mem_remain								*/
/*-->@	w_free									*/
/*-->@	w_malloc								*/
/*-->@	w_strsave								*/
/*										*/
/*	Abhaengig davon, ob im entsprechenden sourcefile MEM_DEBUG definiert	*/
/*	ist, ist die Arbeitsweise der w_..-Funktionen gemaess nachfolgender	*/
/*	Tabelle:								*/
/*				#ifdef				#ifndef 	*/
/*				MEM_DEBUG			MEM_DEBUG	*/
/*										*/
/*	w_reset_observation <=> watch_reset_observation 	---		*/
/*	w_mem_remain	    <=> watch_mem_remain		---		*/
/*	w_free_remain	    <=> watch_free_remain		---		*/
/*										*/
/*	w_malloc	    <=> watch_malloc			malloc		*/
/*	w_free		    <=> watch_free			free		*/
/*	w_strsave	    <=> watch_strsave			strdup		*/
/*										*/
/*m	watch_reset_observation 						*/
/*										*/
/*m	watch_mem_remain							*/
/*										*/
/*m	watch_free_remain							*/
/*										*/
/*m	watch_malloc								*/
/*										*/
/*m	watch_free								*/
/*										*/
/*m	watch_strsave								*/
/********************************************************************************/


#include <stdio.h>
#include <string.h>
#include <malloc.h>

#ifndef TRUE
#	define	TRUE	(1==1)
#endif

#ifndef FALSE
#	define	FALSE	!TRUE
#endif

/*-->@	watch_malloc_rec	*/
/*-->@	Watch_malloc_rec	*/
/*-->@	-Dw_memory		*/
/*	nur lokal definiert !							*/

typedef struct watch_malloc_rec {
	long	length;
	char	*adress, *msg;
	struct watch_malloc_rec *next;
	} *Watch_malloc_rec;
	
	int			w_memory_error = FALSE;

static	Watch_malloc_rec	Mem_list = NULL;

static	void			(*malloc_error_func)() = NULL;


/********************************************************************************/
/*										*/
/*-->	w_set_malloc_error_func 						*/
/*										*/
/*	PARAMETER:	void (*proc)()						*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Selbsterklaerend.					*/
/*										*/
/*	ERKLAERUNG:	'proc' muss wie folgt definiert sein:			*/
/*										*/
/*			void	proc()						*/
/*			{							*/
/*				...						*/
/*			}							*/
/*										*/
/*			Nach Aufruf dieser Prozedur wird, falls proc != NULL,	*/
/*			im Fall eines fehlerhaften w_malloc's proc()		*/
/*			aufgerufen.						*/
/*			proc() soll dann dafuer sorgen, dass			*/
/*										*/
/*			1. eine entsprechende Meldung ausgegeben wird und	*/
/*										*/
/*			2. der bis dahin belegte Speicher wieder freigegeben	*/
/*			   wird.						*/
/*										*/
/********************************************************************************/

void	w_set_malloc_error_func(void (*proc)())
{
	malloc_error_func = proc;
}

/********************************************************************************/
/*-->	watch_reset_observation ()						*/
/*										*/
/*	Loesche ev. vorhandenes Protokoll und					*/
/*	lege ein neues an. In diesem wird nun					*/
/*	die aktuelle Speicherbelegung des					*/
/*	Programms abgelegt, wobei nur alle					*/
/*	w_malloc's und w_free's beruecksichtigt 				*/
/*	werden, die (zeitlich) nach diesem					*/
/*	Aufruf vorkommen.							*/
/*										*/
/*	Diese Prozedur wird anstelle von					*/
/*	w_reset_observation(..) aufgerufen,					*/
/*	falls im entsprechenden sourcefile					*/
/*	MEM_DEBUG definiert ist.						*/
/********************************************************************************/


void	watch_reset_observation(void)
{
	register Watch_malloc_rec h1, h2;
	h1 = Mem_list;
	while( h1 != (Watch_malloc_rec)NULL ) {
		h2 = h1;
		h1 = h1->next;
		free( h2 );
	}
	Mem_list = NULL;
}

/********************************************************************************/
/*-->	watch_free_remain ()							*/
/*										*/
/*	Gib den im Protokoll noch vorhandenen					*/
/*	Speicher mittels free(..) frei. 					*/
/*	Gleichzeitig wird auch das Protokoll					*/
/*	geloescht.								*/
/*										*/
/*	ACHTUNG: diese Prozedur sollte von					*/
/*	einem guten Programmierer NIE gebraucht 				*/
/*	werden									*/
/*										*/
/*	Diese Prozedur wird anstelle von					*/
/*	w_free_remain(..) aufgerufen, falls im					*/
/*	entsprechenden sourcefile MEM_DEBUG					*/
/*	definiert ist.								*/
/********************************************************************************/

void	watch_free_remain(void)
{
	register Watch_malloc_rec h1, h2;
	h1 = Mem_list;
	while( h1 != (Watch_malloc_rec)NULL ) {
		free( h1->adress );
		h2 = h1;
		h1 = h1->next;
		free( h2 );
	}
	Mem_list = NULL;
}	

/********************************************************************************/
/*-->	watch_malloc ( long n, char *mes )					*/
/*										*/
/*	gibt (char *) zurueck.							*/
/*	Entspricht malloc( n ), nur dass dabei					*/
/*	mitprotokolliert wird. Die Speicher-					*/
/*	anforderung wird mit 'mes' kommentiert. 				*/
/*										*/
/*	Diese Funktion wird anstelle von					*/
/*	w_malloc(..) aufgerufen, falls im					*/
/*	entsprechenden sourcefile MEM_DEBUG					*/
/*	definiert ist.								*/
/********************************************************************************/

char	*watch_malloc(long int n, char *mes)
{
	Watch_malloc_rec hilf;
	hilf = (Watch_malloc_rec) malloc( sizeof( struct watch_malloc_rec ) );
	if( hilf != NULL ) {
		hilf->adress = (char *) malloc( n );
		if( hilf->adress == NULL ) {
			w_memory_error = TRUE;
		}
		hilf->length = n;
		hilf->msg = mes;
		hilf->next = Mem_list;
		Mem_list = hilf;
#ifdef PRINT
		printf( "malloc -- a: %8x, s: %10d, %s\n", (long)(hilf->adress), n, mes );
#endif
		return hilf->adress;
	} else {
		w_memory_error = TRUE;
		if( malloc_error_func != NULL ) {
			malloc_error_func();
		}
		return NULL;
	}
}

/********************************************************************************/
/*-->	watch_free ( char *adress )						*/
/*										*/
/*	Entspricht free(adress), nur dass dabei 				*/
/*	mitprotokolliert wird.							*/
/*										*/
/*	Diese Funktion wird anstelle von					*/
/*	w_free(..) aufgerufen, falls im 					*/
/*	entsprechenden sourcefile MEM_DEBUG					*/
/*	definiert ist.								*/
/********************************************************************************/

void	watch_free(char *a)
{
	Watch_malloc_rec *hilf, suc;
	hilf = &Mem_list;
	while( ((*hilf) != (Watch_malloc_rec) NULL) && ((*hilf)->adress != a) ) {
		hilf = &((*hilf)->next);
	}
	if( ((*hilf) != (Watch_malloc_rec) NULL) && ((*hilf)->adress == a)) {
#ifdef PRINT
		printf( "free	-- a: %8x, s: %10d, %s\n", (long)(a), (*hilf)->length, (*hilf)->msg );
#endif
		free( a );
		suc = (*hilf)->next;
		free( *hilf );
		*hilf = suc;
	} else {
#ifdef PRINT
		printf( "free	-- adress %p never allocated\n", (long)a );
#endif
	}
}

/********************************************************************************/
/*-->	watch_strsave ( char *str )						*/
/*										*/
/*	gibt (char *) zurueck.							*/
/*	Entspricht strdup(str), nur dass dabei					*/
/*	mitprotokolliert wird.							*/
/*										*/
/*	Diese Funktion wird anstelle von					*/
/*	w_strsave(..) aufgerufen, falls im					*/
/*	entsprechenden sourcefile MEM_DEBUG					*/
/*	definiert ist.								*/
/********************************************************************************/

char	*watch_strsave(char *str)
{
	register char *tmp;
	
	if( str == NULL ) {
		str = "";
	}
	tmp = (char *)watch_malloc( strlen(str)+1, " string" );
	if( tmp != (char *)NULL ) {
		strcpy( tmp, str );
	}
	if( tmp == str ) {
		printf( "MALLOC ERROR: equal pointers!\n" );
	}
	return tmp;
}

/********************************************************************************/
/*-->	watch_mem_remain ()							*/
/*										*/
/*	Zeigt derzeitigen Zustand des						*/
/*	Protokolls an.								*/
/*	Die Ausgabe spiegelt die momentane					*/
/*	Speicherbelegung seit Beginn des					*/
/*	Protokolls wider.							*/
/*										*/
/*	Diese Prozedur wird anstelle von					*/
/*	w_mem_remain() aufgerufen, falls im					*/
/*	entsprechenden sourcefile MEM_DEBUG					*/
/*	definiert ist.								*/
/********************************************************************************/

void	watch_mem_remain(void)
{
	Watch_malloc_rec hilf = Mem_list;
	long rem=0;
	while( hilf != NULL ) {
		printf( "adress: %8p, size: %10ld, type: %s\n", hilf->adress, hilf->length, hilf->msg );
		rem += hilf->length;
		hilf = hilf->next;
	}
	printf( "total allocated memory : %ld\n", rem );
}

/********************************************************************************/
/*-->@	normal_malloc								*/
/*-->@	normal_free								*/
/*-->@	normal_strsave								*/
/*	normal_malloc	<=> malloc						*/
/*	normal_free	<=> free						*/
/*	normal_strsave	<=> strdup						*/
/*										*/
/*	Diese Prozeduren/Funktionen werden					*/
/*	anstelle von w_.. aufgerufen, wenn					*/
/*	im entsprechenden sourcefile MEM_DEBUG					*/
/*	NICHT definiert ist.							*/
/********************************************************************************/

char	*normal_malloc(long int n)
{
	register char	*tmp;
	
	tmp = (char *)malloc( n );
	if( tmp == NULL ) {
		w_memory_error = TRUE;
		if( malloc_error_func != NULL ) {
			malloc_error_func();
		}
	}
	return tmp;
}

void	normal_free(char *a)
{
	free( a );
}

char	*normal_strsave(char *str)
{
	register char *tmp;
	
	if( str == NULL ) {
		str = "";
	}
	tmp = (char *)malloc( strlen(str)+1 );
	if( tmp != (char *)NULL ) {
		strcpy( tmp, str );
	}
	if( tmp == str ) {
		printf( "MALLOC ERROR: equal pointers!\n" );
	}
	return tmp;
}


/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mw_memory								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fw_memory							*/
/*m		-Ew_memory							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dw_memory							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pw_memory							*/
/********************************************************************************/
