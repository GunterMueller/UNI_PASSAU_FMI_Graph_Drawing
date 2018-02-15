/*****************************************************************************/
/*                                  M A I N                                  */
/*                                                                           */
/* Modul        : main.c						     */
/* erstellt von : Nikolas Motte                                              */
/* erstellt am  : 10.12.1992                                                 */
/*                                                                           */
/*****************************************************************************/
/*     enthaelt den Garphedaufruf und die User-Menu-Initialisierung          */
/*****************************************************************************/

#include <xview/xview.h>
#include "mystd.h"
#include <stdarg.h>
#include "mainwindow.h"
#include "untere_schranke.h"
#include "my_misc.h"  /*fuer knoten_anpassen*/
#include "control.h" /*fuer graph_reduzieren_aufruf*/

void untere_schranke_aufruf(void)
{call_sgraph_proc(main_control_window, NULL);
call_sgraph_proc(finde_beste_untere_Schranke, NULL);}

void arnborg_aufruf(void)
{algorithmus=ARNBORG;
algo_output_type=BAUMBREITE;
 main_algorithmen_aufruf();
if (main_control_window_frame_active)
  { main_control_window(XV_NULL,XV_NULL);}
}

void heuristik_aufruf(void)
{algorithmus=VALENZHEURISTIK;
algo_output_type=BAUMZERLEGUNG;
 main_algorithmen_aufruf();
if (main_control_window_frame_active)
  { main_control_window(XV_NULL,XV_NULL);}

}
