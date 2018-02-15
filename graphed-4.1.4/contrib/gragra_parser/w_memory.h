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
/*	MODUL:	  w_memory							*/
/*										*/
/*	FUNKTION: Einfaches malloc/free/strsave-Toolkit zum Testen der		*/
/*		  Speicherverwaltung.						*/
/*										*/
/********************************************************************************/

#ifndef W_MEMORY_HEADER
#define W_MEMORY_HEADER

extern	void	watch_reset_observation(void);
extern	void	watch_free_remain(void);
extern	char	*watch_malloc(long int n, char *mes);
extern	void	watch_free(char *a);
extern	void	watch_mem_remain(void);
extern	char	*watch_strsave(char *str);
extern	char	*normal_malloc(long int n);
extern	void	normal_free(char *a);
extern	char	*normal_strsave(char *str);
extern	void	w_set_malloc_error_func(void (*proc) ());
extern	int	w_memory_error;

#ifdef MEM_DEBUG
#	define w_malloc( SIZE ) 	watch_malloc((SIZE),"SIZE")
#	define w_free( PTR )		watch_free(PTR)
#	define w_mem_remain()		watch_mem_remain()
#	define w_reset_observation()	watch_reset_observation()
#	define w_free_remain()		watch_free_remain()
#	define w_strsave( str ) 	watch_strsave( str )
#else
#	define w_malloc( SIZE ) 	normal_malloc((SIZE))
#	define w_free( PTR )		normal_free(PTR)
#	define w_mem_remain()
#	define w_reset_observation()
#	define w_free_remain()
#	define w_strsave( str ) 	normal_strsave( str )
#endif

#define W_RESET_ERROR			w_memory_error = (1==2)
#define ON_MALLOC_ERROR_RETURN		if( w_memory_error ) return

#endif

