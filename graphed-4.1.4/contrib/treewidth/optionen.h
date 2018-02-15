#ifndef optionen_h
#define optionen_h
/*****************************************************************************/
/*                                                                           */
/*                O P T I O N E N  -  F E N S T E R 			     */
/*                                                                           */
/* Modul	: optionen.h						     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/


enum UNTERE_SCHRANKE  {NUR_KANTENZAHL,
                       FINDE_MINBW_DURCH_MAX_CLIQUE,
                       FINDE_MINBW_DURCH_MAXCLIQUENSEPERATOR
                      };

enum UNTERE_SCHRANKE untere_schranke;


extern void optionen_window(void); /* ruft optionen-Fenster auf */

extern jede_baumzerlegung_in_neues_fenster;

#endif
