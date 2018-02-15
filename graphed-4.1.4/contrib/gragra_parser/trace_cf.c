/********************************************************************************/
/*-->@	-Etrace_cf								*/
/*										*/
/*	PROGRAMMIERT VON:	LAMSHOEFT THOMAS				*/
/*		   DATUM:	31.03.1994					*/
/*										*/
/********************************************************************************/
/*										*/
/*-->@	-Ftrace_cf								*/
/*										*/
/*	MODUL:	  trace_cf							*/
/*										*/
/*	FUNKTION: Benutzeroberflaeche fuer den Tracer (Modul 'tracer'). 	*/
/*		  -> Prozeduren fuer den Auf- / Abbau des Tracer-Command-Frame	*/
/*		     sowie zugehoerige Notifier-Callback-Routinen.		*/
/*										*/
/********************************************************************************/


#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include <xview/xview.h>
#include <xview/panel.h>

#include "misc.h"
#include "types.h"

#include "parser.h"

#include "tracer.h"

#include "win_defs.h"
#include "trace_cf.h"


/********************************************************************************/
/*										*/
/*	Alle Prozeduren/Funktionen auf einen Blick (fuer 'Find')		*/
/*										*/
/********************************************************************************/
/*-->@	-Ptrace_cf

	void	TRC_nf_quit			()
	void	TRC_create_tracer_subframe	( Frame baseframe )

static	void	TRC_set_selection		()
static	void	TRC_show_message		()
static	void	TRC_react_to_tracer_status	()

static	void	TRC_sg_quit			( Sgraph_proc_info info )

static	void	TRC_sg_reset			( Sgraph_proc_info info )
static	void	TRC_nf_reset			()

static	void	TRC_sg_activate 		( Sgraph_proc_info info )
static	void	TRC_nf_activate 		()

static	void	TRC_sg_reset_previous		( Sgraph_proc_info info )
static	void	TRC_nf_reset_previous		()

static	void	TRC_sg_reset_next		( Sgraph_proc_info info )
static	void	TRC_nf_reset_next		()

static	void	TRC_sg_expand			( Sgraph_proc_info info )
static	void	TRC_nf_expand			()

static	void	TRC_sg_select			( Sgraph_proc_info info )
static	void	TRC_nf_select			()

static	void	TRC_sg_step_back		( Sgraph_proc_info info )
static	void	TRC_nf_step_back		()

static	void	TRC_doubleclick_event_func	( Sgraph_proc_info	 info
						  Sgraph_event_proc_info uev_info
						  Event 		 *event )
						  
static	void	TRC_uev_click_func		( char	*info,	*event )

static	void	TRC_sg_set_default_expansion	( Sgraph_proc_info info )
static	void	TRC_nf_set_default_expansion	()

static	void	TRC_sg_select_and_expand	( Sgraph_proc_info info )
static	void	TRC_nf_select_and_expand	()

static	void	TRC_nf_pushpin_quit		()

**/



/********************************************************************************/
/*										*/
/*	lokale Prozeduren/Funktionen: Vorwaertsdeklaration			*/
/*										*/
/********************************************************************************/

static	void	TRC_set_selection		(Sgraph_proc_info info);
static	void	TRC_show_message		(void);
static	void	TRC_react_to_tracer_status	(void);
static	void	TRC_sg_reset			(Sgraph_proc_info info);
static	void	TRC_nf_reset			(void);
static	void	TRC_sg_activate 		(Sgraph_proc_info info);
static	void	TRC_nf_activate 		(void);
static	void	TRC_sg_reset_previous		(Sgraph_proc_info info);
static	void	TRC_nf_reset_previous		(void);
static	void	TRC_sg_reset_next		(Sgraph_proc_info info);
static	void	TRC_nf_reset_next		(void);
static	void	TRC_sg_expand			(Sgraph_proc_info info);
static	void	TRC_nf_expand			(void);
static	void	TRC_sg_select			(Sgraph_proc_info info);
static	void	TRC_nf_select			(void);
static	void	TRC_sg_step_back		(Sgraph_proc_info info);
static	void	TRC_nf_step_back		(void);
static	void	TRC_doubleclick_event_func	(Sgraph_proc_info info, Sgraph_event_proc_info uev_info, Event *event);
static	void	TRC_uev_click_func		(UEV_info info, Event *event);
static	void	TRC_nf_pushpin_quit		(void);
static	void	TRC_sg_set_default_expansion	(Sgraph_proc_info info);
static	void	TRC_nf_set_default_expansion	(void);
static	void	TRC_sg_select_and_expand	(Sgraph_proc_info info);
static	void	TRC_sg_quit			(Sgraph_proc_info info);
static	void	TRC_nf_select_and_expand	(void);
	

static	void	TRC_nf_trace_options_quit(void);
static	void	TRC_nf_trace_options_opnode_placement(Panel_item item, int value, Event *event);
static	void	TRC_nf_trace_options_graph_placement(Panel_item item, int value, Event *event);
static	void	TRC_create_trace_options_subframe(void);
static	void	TRC_compute_trc_graph_offset(void);


/********************************************************************************/
/*										*/
/*-->	TRC_set_selection							*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info   info 				*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Bestimme gemaess Zustand des Tracers einen Knoten sn	*/
/*			von TRC_info.sgraph und setze info->new_selection auf	*/
/*			diesen. 						*/
/*										*/
/*	BESONDERES:	Falls (gemaess Tracerzustand) kein sinnvoller Knoten	*/
/*			bestimmt werden kann, so wird ganz TRC_info.sgraph zur	*/
/*			neuen Selektion.					*/
/*										*/
/********************************************************************************/

static	void	TRC_set_selection(Sgraph_proc_info info)
{
	TRC_get_snode_selection();
	if( TRC_info.selected_snode == NULL ) {
		int	tmp_status = TRC_info.status;
		TRC_info.status = TRC_ERROR;
		TRC_get_snode_selection();
		TRC_info.status = tmp_status;
	}
	if( TRC_info.selected_snode == NULL ) {
		info->new_selected = SGRAPH_SELECTED_NONE;
	} else {
		info->new_selected = SGRAPH_SELECTED_SNODE;
		info->new_selection.snode = (Snode)(TRC_info.selected_snode);
	}
}

/********************************************************************************/
/*										*/
/*-->	TRC_show_message							*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Verteile TRC_info.message auf die beiden (Info-)	*/
/*			Message-Items des Tracer-Command-Frame. 		*/
/*										*/
/********************************************************************************/

static	void	TRC_show_message(void)
{
	char	tmp, *str, *lauf;
	
	str = TRC_info.message;
	if( (int) strlen( str ) < 31 ) {
		xv_set( TRC.message_text_item1, PANEL_LABEL_STRING, str, 0 );
		xv_set( TRC.message_text_item2, PANEL_LABEL_STRING, "", 0 );
	} else {
		lauf = &(str[31]);
		while( (*lauf!=' ') && (lauf!=str) ) {
			lauf--;
		}
		if( lauf==str ) {
			lauf = &(str[31]);
		}
		tmp = *lauf;
		*lauf = '\0';
		xv_set( TRC.message_text_item1, PANEL_LABEL_STRING, str, 0);
		*lauf = tmp;
		if( tmp == ' ' ) {
			lauf++;
		}
		xv_set( TRC.message_text_item2, PANEL_LABEL_STRING, lauf, 0);
	}
}

/********************************************************************************/
/*										*/
/*-->	TRC_react_to_tracer_status						*/
/*										*/
/*	PARAMETER:	---							*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Update des Tracer-Command-Frame gemaess Zustand des	*/
/*			Tracers.						*/
/*										*/
/*	ERKLAERUNG:	Update heisst (De-)Aktivieren von Buttons, Anzeigen	*/
/*			von Information usw.					*/
/*										*/
/********************************************************************************/

static	void	TRC_react_to_tracer_status(void)
{
	if( TRC_info.status == TRC_RESET ) {
		XV_sleep( TRC.reset_button );
		if( TRC_info.nr_start_element > 0 ) {
			XV_awake( TRC.reset_previous_button );
		} else {
			XV_sleep( TRC.reset_previous_button );
		}
		if( TRC_info.next_start_possible ) {
			XV_awake( TRC.reset_next_button );
		} else {
			XV_sleep( TRC.reset_next_button );
		}
		XV_awake( TRC.expand_button );
		XV_awake( TRC.select_button );
	}
	if( TRC_info.status == TRC_EXPAND ) {
		XV_awake( TRC.reset_button );
		XV_sleep( TRC.reset_previous_button );
		XV_sleep( TRC.reset_next_button );
		
		XV_awake( TRC.expand_button );
	}
	if( TRC_info.working_list != NULL ) {
		XV_awake( TRC.step_back_button );
	} else {
		XV_sleep( TRC.step_back_button );
	}
	if( TRC_info.default_expansion ) {
		XV_hide_item( TRC.select_button );
		XV_hide_item( TRC.expand_button );
		XV_show_item( TRC.select_and_expand_button );
		set_user_event_func( SGRAPH_UEV_DOUBLE_CLICK, NULL );
		xv_set( TRC.expand_default_choice, PANEL_VALUE, 0, 0 );
	} else {
		XV_hide_item( TRC.select_and_expand_button );
		XV_show_item( TRC.select_button );
		XV_show_item( TRC.expand_button );
		set_user_event_func( SGRAPH_UEV_DOUBLE_CLICK, (User_event_function)TRC_uev_click_func );
		xv_set( TRC.expand_default_choice, PANEL_VALUE, 1, 0 );
	}

	TRC_show_message();
	TRC_info.message = "";
}

/********************************************************************************/
/*										*/
/*-->@	TRC_nf/sg_relation							*/
/*										*/
/*	Die .._nf_..- und .._sg_..-Prozeduren bilden funktionelle Paare.	*/
/*	Dabei sind die .._nf_..-Prozeduren Callback-Routinen des Notifiers,	*/
/*	deren wesentliche Aufgabe es ist, call_sgraph_proc( .._sg_.. ) aufzu-	*/
/*	rufen. Ueber solche Paare von Prozeduren ist es moeglich,		*/
/*	(Pseudo-)Notify-Routinen zu entwickeln, die statt 'item', 'event' usw.	*/
/*	eine Sgraph_proc_info als Parameter erwarten.				*/
/*										*/
/********************************************************************************/


		
/********************************************************************************/
/*										*/
/*-->@	TRC_sg_reset								*/
/*-->@	TRC_nf_reset								*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_reset	'reset'-Button						*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ruecksetzen des Tracers, Neustart.			*/
/*										*/
/*	BESONDERES:	Im Gegensatz zu TRC_nf/sg_activate muss bei .._reset	*/
/*			TRC_info.sgraph bereits existieren.			*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_reset(Sgraph_proc_info info)
{
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		TRC_remove_sgraph();
		TRC_remove_graph();
		TRC_info.expand_pe = NULL;
		TRC_make_start_graph();
		TRC_make_sgraph();
		TRC_set_selection( info );
	} else {
		TRC_show_message();
	}
}

static	void	TRC_nf_reset(void)
{
	call_sgraph_proc( TRC_sg_reset, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_activate 							*/
/*-->@	TRC_nf_activate 							*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_activate 							*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Initialisierung des Tracers, Erststart. 		*/
/*										*/
/*	BESONDERES:	Im Gegensatz zu TRC_nf/sg_reset darf bei .._activate	*/
/*			TRC_info.sgraph noch nicht existieren, sonst wird er	*/
/*			nicht geloescht, sondern brutal ueberschrieben. 	*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_activate(Sgraph_proc_info info)
{
	if( info == NULL ) {
		return;
	}
	TRC_init();
	TRC_init_sgraph();
	TRC_activate();
	TRC_make_sgraph();
	info->new_sgraph = (Sgraph) (TRC_info.sgraph);
	TRC_get_snode_selection();
	if( TRC_info.selected_snode == NULL ) {
		info->new_selected = SGRAPH_SELECTED_NOTHING;
	} else {
		info->new_selected = SGRAPH_SELECTED_SNODE;
		info->new_selection.snode = (Snode)(TRC_info.selected_snode);
	}
}

static	void	TRC_nf_activate(void)
{
	call_sgraph_proc( TRC_sg_activate, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_reset_previous							*/
/*-->@	TRC_nf_reset_previous							*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_reset_previous	'prev'-Button					*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ruecksetzen des Tracers, Neustart.			*/
/*										*/
/*	BESONDERES:	Analog zu TRC_nf/sg_reset. Jedoch wird hier der vorher- */
/*			gehende Startgraph (falls existent) generiert.		*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_reset_previous(Sgraph_proc_info info)
{
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		TRC_remove_sgraph();
		TRC_remove_graph();
		TRC_info.nr_start_element--;
		if( TRC_info.nr_start_element < 0 ) {
			TRC_info.nr_start_element = 0;
		}
		TRC_make_start_graph();
		TRC_make_sgraph();
		TRC_set_selection( info );
	} else {
		TRC_show_message();
	}
}

static	void	TRC_nf_reset_previous(void)
{
	call_sgraph_proc( TRC_sg_reset_previous, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_reset_next							*/
/*-->@	TRC_nf_reset_next							*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_reset_next	'next'-Button					*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ruecksetzen des Tracers, Neustart.			*/
/*										*/
/*	BESONDERES:	Analog zu TRC_nf/sg_reset. Jedoch wird hier der nach-	*/
/*			folgende Startgraph (falls existent) generiert. 	*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_reset_next(Sgraph_proc_info info)
{
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		TRC_remove_sgraph();
		TRC_remove_graph();
		TRC_info.nr_start_element++;
		TRC_make_start_graph();
		TRC_make_sgraph();
		TRC_set_selection( info );
	} else {
		TRC_show_message();
	}
}

static	void	TRC_nf_reset_next(void)
{
	call_sgraph_proc( TRC_sg_reset_next, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_expand								*/
/*-->@	TRC_nf_expand								*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_expand	'expand'-Button 					*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Ersetzen eines Knotens gemaess einer Produktion.	*/
/*										*/
/*	BESONDERES:	Ersetzt wird TRC_info.expand_pe (hervorgehoben, nicht	*/
/*			mehr sichtbar, falls TRC_info.status == TRC_EXPAND ).	*/
/*			Ersetzt wird gemaess dem PE / der Produktion, welches	*/
/*			durch den aktuell selektierten Knoten repraesentiert	*/
/*			wird. Falls kein passender Knoten selektiert ist,	*/
/*			erfolgt eine entsprechende Fehlermeldung.		*/
/*										*/
/********************************************************************************/

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_ 								*/
/*-->@	TRC_nf_ 								*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_ 								*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:								*/
/*										*/
/*	BESONDERES:								*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_expand(Sgraph_proc_info info)
{
	TRC_snode_attribute	attr;
	
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		if( info->selected != SGRAPH_SELECTED_SNODE ) {
			TRC_info.message = "Please select exact one node of the tracing graph!";
			return;
		}
		if( info->selection.snode == NULL ) {
			TRC_info.message = "Fatal error: can't get nodepointer!";
			return;
		}
		if( TRC_get_snode_attrs( info->selection.snode, &attr ) ) {
			if( !(attr->is_graphnode) ) {
				TRC_remove_sgraph();
				if( TRC_info.status == TRC_EXPAND ) {
					TRC_re_insert_node();
				}
				TRC_info.expand_through = attr->pe;
				TRC_info.nr_expansion = 0;
				TRC_replace_node();
				TRC_make_sgraph();
				TRC_make_options_sgraph();
				TRC_info.status = TRC_EXPAND;
				TRC_info.message = "expanded node";
			} else {
				TRC_info.message = "Please select an option node!";
			}
			TRC_set_selection( info );
		}
	} else {
		TRC_show_message();
	}
}

static	void	TRC_nf_expand(void)
{
	call_sgraph_proc( TRC_sg_expand, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_select								*/
/*-->@	TRC_nf_select								*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_select	'select'-Button 					*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Festlegung des naechsten, zu ersetzenden Knotens sowie	*/
/*			Anzeige der verschiedenen Ersetzungsmoeglichkeiten.	*/
/*										*/
/*	BESONDERES:	Der zu ersetzende Knoten wird durch den aktuell selek-	*/
/*			tierten Knoten bestimmt. Falls kein passender Knoten	*/
/*			selektiert ist, wird eine entsprechende Fehlermeldung	*/
/*			ausgegeben.						*/
/*			Der ausgewaehlte Knoten wird besonders hervorgehoben.	*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_select(Sgraph_proc_info info)
{
	TRC_snode_attribute	attr;
	
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		if( info->selected != SGRAPH_SELECTED_SNODE ) {
			TRC_info.message = "Please select exact one node of the tracing graph!";
			return;
		}
		if( info->selection.snode == NULL ) {
			TRC_info.message = "Fatal error: can't get nodepointer!";
			return;
		}
		if( TRC_get_snode_attrs( info->selection.snode, &attr ) ) {
			if( attr->is_graphnode ) {
				TRC_info.message = "selected node";
				TRC_info.expand_pe = attr->pe;
				TRC_info.expand_through = NULL;
				TRC_info.nr_expansion = 0;
				TRC_remove_sgraph();
				TRC_make_sgraph();
				TRC_make_options_sgraph();
				TRC_info.status = TRC_SELECT;
			} else {
				TRC_info.message = "Don't touch THIS node!";
			}
			TRC_set_selection( info );
			if( TRC_info.selected_snode == NULL ) {
				TRC_info.status = TRC_ERROR;
				TRC_set_selection( info );
				TRC_info.status = TRC_SELECT;
			}
		}
	} else {
		TRC_show_message();
	}
}

static	void	TRC_nf_select(void)
{
	call_sgraph_proc( TRC_sg_select, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_step_back							*/
/*-->@	TRC_nf_step_back							*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_step_back	'step back'-Button				*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Mache letzten Ersetzungsschritt rueckgaengig.		*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_step_back(Sgraph_proc_info info)
{
	Parsing_element tmp_pe;
	
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		TRC_remove_sgraph();
		TRC_re_insert_node();
		if( TRC_info.default_expansion ) {
			tmp_pe = TRC_info.expand_pe;
			TRC_info.expand_pe = NULL;
		}
		TRC_make_sgraph();
		TRC_make_options_sgraph();
		if( !TRC_info.default_expansion ) {
			TRC_info.status = TRC_SELECT;
			TRC_set_selection( info );
		} else {
			info->new_selected = SGRAPH_SELECTED_SNODE;
			info->new_selection.snode = (Snode)tmp_pe->snode;
		}
	} else {
		TRC_show_message();
	}
}

static	void	TRC_nf_step_back(void)
{
	call_sgraph_proc( TRC_sg_step_back, NULL );
	TRC_react_to_tracer_status();
}

static	void	TRC_doubleclick_event_func(Sgraph_proc_info info, Sgraph_event_proc_info uev_info, Event *event)
{
	TRC_snode_attribute	attr;
	
	if(	(info == NULL)		||
		(uev_info == NULL)	||
		(uev_info->type != SGRAPH_UEV_DOUBLE_CLICK)
	   ) {
		return;
	}
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		if(	(info->selected == SGRAPH_SELECTED_SNODE)		&&
			(info->selection.snode != NULL) 		&&
			TRC_get_snode_attrs( info->selection.snode, &attr ) 
		   ) {
			if( attr->is_graphnode ) {
				call_sgraph_proc( TRC_sg_select, NULL );
			} else {
				call_sgraph_proc( TRC_sg_expand, NULL );
			}
		}
	} else {
		TRC_show_message();
	}
	uev_info->do_default_action = FALSE;
	return;
}

static	void	TRC_uev_click_func(UEV_info info, Event *event)
{
	call_sgraph_event_proc( TRC_doubleclick_event_func, info, event, NULL);
	TRC_react_to_tracer_status();
}

static	void	TRC_nf_pushpin_quit(void)
{
	TRC_info.message = "Push (parser)'cont' or 'reset' to quit tracer!";
	xv_set( TRC.baseframe, FRAME_CMD_PUSHPIN_IN,	TRUE, 0);
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_set_default_expansion						*/
/*-->@	TRC_nf_set_default_expansion						*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_set_default_expansion	'default expansion'-Choice		*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Schalte zwischen verschiedene Ableitungsmodi.		*/
/*										*/
/*	ERKLAERUNG:	Mit 'select' und 'expand' hat der Benutzer die		*/
/*			Moeglichkeit, einen Knoten auszuwaehlen (select) und	*/
/*			ALLE Ersetzungsvarianten durchzutesten (expand).	*/
/*			Diese Moeglichkeit erkauft er sich allerdings nur durch */
/*			fleissiges Arbeiten mit der Maus (staendiges Pendeln	*/
/*			zwischen Knoten (selektieren) und Button).		*/
/*			Bequemer geht es mit der zweiten Moeglichkeit		*/
/*			'select & expand'. Dabei kann man nur EINE (Links-) Ab- */
/*			leitung erzeugen, doch man braucht dafuer die Maus	*/
/*			nicht mehr bewegen, sondern nur noch zu druecken.	*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_set_default_expansion(Sgraph_proc_info info)
{
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		TRC_remove_sgraph();
		TRC_info.default_expansion = !TRC_info.default_expansion;
		TRC_info.expand_pe = NULL;
		TRC_make_sgraph();
		TRC_make_options_sgraph();
		TRC_set_selection( info );
	}
}	

static	void	TRC_nf_set_default_expansion(void)
{
	call_sgraph_proc( TRC_sg_set_default_expansion, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_select_and_expand						*/
/*-->@	TRC_nf_select_and_expand						*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_select_and_expand	'select & expand'-Button		*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Waehle Ersetzungsknoten und ersetze durch erste 	*/
/*			Ableitungsoption.					*/
/*										*/
/*	BESONDERES:	Ausgewaehlt und ersetzt wird der aktuell selektierte	*/
/*			Knoten. Falls kein passender Knoten selektiert ist,	*/
/*			wird eine entsprechende Fehlermeldung ausgegeben.	*/
/*			Da Standardersetzung eingestellt ist, werden keine	*/
/*			Ersetzungsauswahlknoten dargestellt. Der ersetzte	*/
/*			Knoten wird auch nicht hervorgehoben.			*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_select_and_expand(Sgraph_proc_info info)
{
	TRC_snode_attribute	attr;
	
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		if( info->selected != SGRAPH_SELECTED_SNODE ) {
			TRC_info.message = "Please select exact one node of the tracing graph!";
			return;
		}
		if( info->selection.snode == NULL ) {
			TRC_info.message = "Fatal error: can't get nodepointer!";
			return;
		}
		if( TRC_get_snode_attrs( info->selection.snode, &attr ) ) {
			if( (attr->is_graphnode) ) {
				TRC_remove_sgraph();
				TRC_info.expand_pe = attr->pe;
				TRC_info.expand_through = attr->pe->trc_iso;
				TRC_info.nr_expansion = 0;
				TRC_replace_node();
				TRC_info.expand_pe = NULL;
				TRC_make_sgraph();
				TRC_make_options_sgraph();
				TRC_info.status = TRC_EXPAND;
			} else {
				TRC_info.message = "Where did you catch THAT node ?";
			}
			TRC_set_selection( info );
		}
	} else {
		TRC_show_message();
	}
}

static	void	TRC_nf_select_and_expand(void)
{
	call_sgraph_proc( TRC_sg_select_and_expand, NULL );
	TRC_react_to_tracer_status();
}

/********************************************************************************/
/*										*/
/*-->@	TRC_sg_quit								*/
/*-->@	TRC_nf_quit								*/
/*m	TRC_nf/sg_relation							*/
/*										*/
/*	TRC_sg_quit								*/
/*										*/
/*	PARAMETER:	Sgraph_proc_info  info					*/
/*										*/
/*	ZURUECK:	---							*/
/*										*/
/*	AUFGABE:	Beenden des Tracers.					*/
/*										*/
/*	ERKLAERUNG:	Abbau des Tracing-Graphen sowie aller Tracer-internen	*/
/*			Datenstrukturen. Schliessen des Tracer-Command-Frame.	*/
/*										*/
/********************************************************************************/

static	void	TRC_sg_quit(Sgraph_proc_info info)
{
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		TRC_remove_sgraph();
		info->new_selected = SGRAPH_SELECTED_NOTHING;
		TRC_deactivate();
		TRC_nf_trace_options_quit();
	}
}

void	TRC_nf_quit(void)
{
	if( TRC_info.status != TRC_INACTIVE ) {
		call_sgraph_proc( TRC_sg_quit, NULL );
		TRC_react_to_tracer_status();
		if( TRC_info.status == TRC_INACTIVE ) {
			xv_set( 	TRC.baseframe,
					FRAME_CMD_PUSHPIN_IN,	FALSE,
					XV_SHOW,		FALSE,
					0 );
			set_user_event_func( SGRAPH_UEV_DOUBLE_CLICK, NULL );
		}
	}
}

void	TRC_create_tracer_subframe(Frame baseframe)
{
	static	int	created = FALSE;
	Panel_item	tmp;
	int		error = FALSE;
	
    if( !created ) {
	
	TRC_info.trc_graph_position = GP_BOTTOM;
	TRC_info.option_node_position = NP_LEFT;
	
	TRC.baseframe = (Frame) xv_create(	baseframe,			FRAME_CMD,
						FRAME_LABEL,			"graph grammar parser - TRACER",
						FRAME_SHOW_LABEL,		TRUE,
						FRAME_CMD_PUSHPIN_IN,		TRUE,
						XV_SHOW,			FALSE,
						FRAME_DONE_PROC,		TRC_nf_pushpin_quit,
						FRAME_NO_CONFIRM,		TRUE,
						FRAME_SHOW_RESIZE_CORNER,	FALSE,
						0);
	if( TRC.baseframe == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	}
	
	TRC.main_panel = (Panel) xv_get(	TRC.baseframe,		FRAME_CMD_PANEL );

	if( TRC.main_panel == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	}
	
	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 1),
						XV_Y,			xv_row(TRC.main_panel, 0)
									+ xv_row( TRC.main_panel, 1 ) / 2,
						PANEL_LABEL_STRING,	"prev",
						PANEL_LABEL_WIDTH,	xv_col(TRC.main_panel, 4),
						PANEL_NOTIFY_PROC,	TRC_nf_reset_previous,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.reset_previous_button = tmp;
	}


	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 9),
						XV_Y,			xv_row(TRC.main_panel, 0)
									+ xv_row( TRC.main_panel, 1 ) / 2,
						PANEL_LABEL_STRING,	"reset",
						PANEL_LABEL_WIDTH,	xv_col(TRC.main_panel, 4),
						PANEL_NOTIFY_PROC,	TRC_nf_reset,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.reset_button = tmp;
	}


	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 17),
						XV_Y,			xv_row(TRC.main_panel, 0)
									+ xv_row( TRC.main_panel, 1 ) / 2,
						PANEL_LABEL_STRING,	"next",
						PANEL_LABEL_WIDTH,	xv_col(TRC.main_panel, 4),
						PANEL_NOTIFY_PROC,	TRC_nf_reset_next,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.reset_next_button = tmp;
	}

	
	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 30),
						XV_Y,			xv_row(TRC.main_panel, 0)
									+ xv_row( TRC.main_panel, 1 ) / 2,
						PANEL_LABEL_STRING,	"options",
						PANEL_NOTIFY_PROC,	TRC_create_trace_options_subframe,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	}
	
	tmp = xv_create(			TRC.main_panel, 	PANEL_CHOICE_STACK,
						XV_X,			xv_col(TRC.main_panel, 1),
						XV_Y,			xv_row(TRC.main_panel, 2),
						PANEL_LABEL_STRING,	"default expansion",
						PANEL_LABEL_BOLD,	FALSE,
						PANEL_CHOICE_STRINGS,	"on", "off", 0,
						PANEL_CLIENT_DATA,	TRC_nf_set_default_expansion,
						PANEL_NOTIFY_PROC,	XV_cycle_panel_choice_stack,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.expand_default_choice = tmp;
	}

		
	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 1),
						XV_Y,			xv_row(TRC.main_panel, 3),
						PANEL_LABEL_STRING,	"      select & expand",
						PANEL_LABEL_WIDTH,	xv_col(TRC.main_panel, 18),
						PANEL_NOTIFY_PROC,	TRC_nf_select_and_expand,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.select_and_expand_button = tmp;
	}

		
	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 1),
						XV_Y,			xv_row(TRC.main_panel, 3),
						PANEL_LABEL_STRING,	"   select",
						PANEL_LABEL_WIDTH,	xv_col(TRC.main_panel, 7),
						PANEL_NOTIFY_PROC,	TRC_nf_select,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.select_button = tmp;
	}
				
						
	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 12),
						XV_Y,			xv_row(TRC.main_panel, 3),
						PANEL_LABEL_STRING,	" expand",
						PANEL_LABEL_WIDTH,	xv_col(TRC.main_panel, 7),
						PANEL_NOTIFY_PROC,	TRC_nf_expand,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.expand_button = tmp;
	}

					
	tmp = xv_create(			TRC.main_panel, 	PANEL_BUTTON,
						XV_X,			xv_col(TRC.main_panel, 23),
						XV_Y,			xv_row(TRC.main_panel, 3), 
						PANEL_LABEL_STRING,	"step back",
						PANEL_LABEL_WIDTH,	xv_col(TRC.main_panel, 7),
						PANEL_NOTIFY_PROC,	TRC_nf_step_back,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.step_back_button = tmp;
	}
						
	tmp = xv_create(			TRC.main_panel, 	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"info:",
						XV_X,			xv_col(TRC.main_panel, 1),
						XV_Y,			xv_row(TRC.main_panel, 4),
						0);
	tmp = xv_create(			TRC.main_panel, 	PANEL_MESSAGE,
						PANEL_LABEL_BOLD,	TRUE,
						PANEL_LABEL_STRING,	"X",
						XV_X,			xv_col(TRC.main_panel, 6),
						XV_Y,			xv_row(TRC.main_panel, 4),
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.message_text_item1 = tmp;
	}
	
	tmp = xv_create(			TRC.main_panel, 	PANEL_MESSAGE,
						PANEL_LABEL_BOLD,	TRUE,
						PANEL_LABEL_STRING,	"X",
						XV_X,			xv_col(TRC.main_panel, 6),
						XV_Y,			xv_row(TRC.main_panel, 4) + 16,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.message_text_item2 = tmp;
	}
	
	window_fit( TRC.main_panel );
	window_fit( TRC.baseframe );

ERROR_HANDLING:
	if( error ) {
		if( TRC.baseframe != XV_NULL ) {
			xv_destroy( TRC.baseframe );
		}
		return;
	} else {
		created = TRUE;
	}
	
    }
    TRC_compute_trc_graph_offset();
    TRC_nf_activate();
    TRC_react_to_tracer_status();
    XV_CMD_position_relative( TRC.baseframe, baseframe, 200, 150 );
    xv_set( TRC.baseframe,	XV_SHOW,		TRUE,
				FRAME_CMD_PUSHPIN_IN,	TRUE,
				0);
}

static	void	TRC_compute_trc_graph_offset(void)
{
	switch( TRC_info.trc_graph_position ) {
		case	GP_BOTTOM_LEFT:
		case	GP_TOP_LEFT:
		case	GP_LEFT:	TRC_info.x_offset = PRS_info.graph_x_left - PRS_info.graph_width - 64;
					break;
					
		case	GP_BOTTOM_RIGHT:
		case	GP_TOP_RIGHT:
		case	GP_RIGHT:	TRC_info.x_offset = PRS_info.graph_x_left + PRS_info.graph_width + 64;
					break;
					
		default:		TRC_info.x_offset = PRS_info.graph_x_left;
					break;
	}
	switch( TRC_info.trc_graph_position ) {
		case	GP_TOP_LEFT:
		case	GP_TOP_RIGHT:
		case	GP_TOP:		TRC_info.y_offset = PRS_info.graph_y_top - PRS_info.graph_height - 64;
					break;
					
		case	GP_BOTTOM_RIGHT:
		case	GP_BOTTOM_LEFT:
		case	GP_BOTTOM:	TRC_info.y_offset = PRS_info.graph_y_top + PRS_info.graph_height + 64;
					break;
					
		default:		TRC_info.y_offset = PRS_info.graph_y_top;
					break;
	}
}

static	void	TRC_sg_redraw(Sgraph_proc_info info)
{
	TRC_check_sgraph( info );
	if( TRC_info.test_result ) {
		TRC_remove_sgraph();
		TRC_compute_trc_graph_offset();
		TRC_make_sgraph();
		TRC_make_options_sgraph();
		TRC_set_selection( info );
		TRC_info.message = "graph redrawn";
	} else {
		TRC_info.message = "Select tracing graph to show modifications.";
	}
}

static	void	TRC_nf_redraw(void)
{
	call_sgraph_proc( TRC_sg_redraw, NULL );
}

static	void	TRC_nf_trace_options_quit(void)
{
	if( TRC.opt_subframe != XV_NULL ) {
		xv_set( TRC.opt_subframe,	FRAME_CMD_PUSHPIN_IN,	FALSE,
						XV_SHOW,		FALSE,
						0 );
	}
}

static	void	TRC_nf_trace_options_opnode_placement(Panel_item item, int value, Event *event)
{
	TRC_info.option_node_position = value;
	TRC_nf_redraw();
}

static	void	TRC_nf_trace_options_graph_placement(Panel_item item, int value, Event *event)
{
	TRC_info.trc_graph_position = value;
	TRC_nf_redraw();
}

static	void	TRC_create_trace_options_subframe(void)
{
	static	int	created = FALSE;
	Panel_item	tmp;
	int		error = FALSE;
	
    if( !created ) {
	
	
	TRC.opt_subframe = (Frame) xv_create(	TRC.baseframe,			FRAME_CMD,
						FRAME_LABEL,			"tracer options",
						FRAME_SHOW_LABEL,		TRUE,
						FRAME_CMD_PUSHPIN_IN,		TRUE,
						XV_SHOW,			FALSE,
						FRAME_DONE_PROC,		TRC_nf_trace_options_quit,
						FRAME_NO_CONFIRM,		TRUE,
						FRAME_SHOW_RESIZE_CORNER,	FALSE,
						0);
	if( TRC.opt_subframe == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	}
	
	TRC.opt_sf_panel = (Panel) xv_get(	TRC.opt_subframe,		FRAME_CMD_PANEL );

	if( TRC.opt_sf_panel == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	}
	
	tmp = xv_create(			TRC.opt_sf_panel, 	PANEL_CHOICE_STACK,
						PANEL_LAYOUT,		PANEL_VERTICAL,
						XV_X,			xv_col(TRC.opt_sf_panel, 1),
						XV_Y,			xv_row(TRC.opt_sf_panel, 0),
						PANEL_LABEL_STRING,	"graph layout",
						PANEL_CHOICE_STRINGS,	
									"left above",
									"above",
									"right above",
									"left",
									"same",
									"right",
									"left below",
									"below",
									"right below",
									0,
						PANEL_VALUE,		TRC_info.trc_graph_position,
						PANEL_CLIENT_DATA,	TRC_nf_trace_options_graph_placement,
						PANEL_NOTIFY_PROC,	XV_cycle_panel_choice_stack,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.graph_placement_choice = tmp;
	}
	

	tmp = xv_create(			TRC.opt_sf_panel, 	PANEL_CHOICE_STACK,
						PANEL_LAYOUT,		PANEL_VERTICAL,
						XV_X,			xv_col(TRC.opt_sf_panel, 20),
						XV_Y,			xv_row(TRC.opt_sf_panel, 0),
						PANEL_LABEL_STRING,	"options layout",
						PANEL_CHOICE_STRINGS,	
									"above",
									"left",
									"right",
									"below",
									0,
						PANEL_VALUE,		TRC_info.option_node_position,
						PANEL_CLIENT_DATA,	TRC_nf_trace_options_opnode_placement,
						PANEL_NOTIFY_PROC,	XV_cycle_panel_choice_stack,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	ERROR_HANDLING;
	} else {
		TRC.opnode_placement_choice = tmp;
	}
	

	window_fit( TRC.opt_sf_panel );
	window_fit( TRC.opt_subframe );

ERROR_HANDLING:
	if( error ) {
		if( TRC.opt_subframe != XV_NULL ) {
			xv_destroy( TRC.opt_subframe );
		}
		return;
	} else {
		created = TRUE;
	}
	
    }
    XV_CMD_position_relative( TRC.opt_subframe, TRC.baseframe, 16, 96 );
    xv_set( TRC.opt_subframe,	XV_SHOW,		TRUE,
				FRAME_CMD_PUSHPIN_IN,	TRUE,
				0);
}
	
