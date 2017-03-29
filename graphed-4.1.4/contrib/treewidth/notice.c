#include "graphed/graph.h"
#include "notice.h"
#include "graphed/graphed_subwindows.h"
#include <xview/xview.h>
#include <xview/frame.h>
#include <xview/panel.h>
#include <xview/notice.h>
#include <stdio.h>

/*****************************************************************************/
/*                                                                           */
/*                N O T I C E  -  F E N S T E R 			     */
/*                                                                           */
/* Modul	: notice.c							     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/


#define true        (0==0)
#define false       (0!=0)

int notice_antwort;
/*********************************************************************/
/*****  notice ******************************************************/
/*********************************************************************/

Xv_opaque notice2_proc(void)
{
 char string[3];
 sprintf(string,"%i",k);
 notice_antwort=notice_prompt(base_frame,NULL,
                 NOTICE_FOCUS_XY,  200,30/*event_x(event),event_y(event)*/,
                 NOTICE_NO_BEEPING, TRUE,
                 NOTICE_MESSAGE_STRINGS, "k=",string, 
                  " nicht erfolgreich !!\nSoll ich fortfahren?\nZeitbedarf *n"
                                     ,NULL,
                 NOTICE_BUTTON_YES,         "YES",
                 NOTICE_BUTTON_NO,          "NO",  NULL);
return XV_NULL;
}

         
/*********************************************************************/
int nextes_k_frage(void)
{notice2_proc();
return notice_antwort;}
/*********************************************************************/
Xv_opaque kein_speicher(void)
{int result;
 char string[3];
 sprintf(string,"%i",k);
 result=notice_prompt(base_frame,NULL,
                 NOTICE_FOCUS_XY,  200,30/*event_x(event),event_y(event)*/,
                 NOTICE_NO_BEEPING, TRUE,
                 NOTICE_MESSAGE_STRINGS, "   ACHTUNG !!!",
                                         "Kein Speicher mehr frei",
                                         "Ich breche ab"  ,NULL,
                 NOTICE_BUTTON_YES,         "OK",
                 NULL);
return XV_NULL;
}

/*********************************************************************/
Xv_opaque free_falsch(void)
{int result;
 char string[3];
 sprintf(string,"%i",k);
 result=notice_prompt(base_frame,NULL,
                 NOTICE_FOCUS_XY,  200,30/*event_x(event),event_y(event)*/,
                 NOTICE_NO_BEEPING, TRUE,
                 NOTICE_MESSAGE_STRINGS, "   ERROR !!!",
                                         "Zeiger nicht vorhanden", NULL,
                 NOTICE_BUTTON_YES,         "OK",
                 NULL);
return XV_NULL;
}

/*********************************************************************/
void zeiger_nicht_vorhanden(void)
{free_falsch();}
/*********************************************************************/
void kein_speicher_mehr_frei(void)
{kein_speicher();}
/*********************************************************************/
/*********************************************************************/
/*********************************************************************/
