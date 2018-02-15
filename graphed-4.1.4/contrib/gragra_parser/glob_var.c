/* FERTIG 130293 */
/********************************************************************************/
/*-->@	-Eglob_var								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Fglob_var								*/
/*										*/
/*	MODUL:		glob_var						*/
/*										*/
/*	FUNKTION:	Physikalische Realisierung von globalen Variablen.	*/
/*										*/
/*	ERKLAERUNG:	Fuer alle globalen Variablen, die in den Headerfiles	*/
/*			mit EXTERN deklariert wurden, wird in diesem Modul	*/
/*			durch den Compiler der dafuer physikalisch benoetigte	*/
/*			Speicher angelegt.					*/
/*										*/
/********************************************************************************/

/*-->@	-Dglob_var	*/

/*	Keine eigenen Datenstrukturen.						*/
/*	Macht aber jede Menge include's. Im Einzelnen:				*/

#include "misc.h"

#include <xview/xview.h>
#include <xview/panel.h>

#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>


#undef	EXTERN
#define EXTERN

#include "w_memory.h"
#include "lab_int.h"
#include "bitset.h"
#include "types.h"
#include "debug.h"
#include "main_sf.h"
#include "convert.h"
#include "gram_opt.h"
#include "reduce.h"
#include "graph_op.h"
#include "parser.h"
#include "tracer.h"
#include "trace_cf.h"

/**/

/*-->@	-Pglob_var

	keine eigenen Prozeduren/Funktionen.
	
**/


/********************************************************************************/
/*	progman - Modulbeschreibung						*/
/*										*/
/*-->@	-Mglob_var								*/
/*	FUNKTIONSBESCHREIBUNG:							*/
/*	======================							*/
/*m		-Fglob_var							*/
/*m		-Eglob_var							*/
/*	DATENSTRUKTUREN:							*/
/*	================							*/
/*m		-Dglob_var							*/
/*	PROZEDUREN/FUNKTIONEN:							*/
/*	======================							*/
/*m		-Pglob_var							*/
/********************************************************************************/

