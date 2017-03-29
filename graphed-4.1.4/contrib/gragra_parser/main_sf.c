#include <X11/Xos.h>

#include <xview/xview.h>
#include <xview/panel.h>
#include <xview/icon.h>
#include <xview/notify.h>

extern	Frame	base_frame;	/* GraphEd's baseframe				*/
				/* Got this information from Michael Himsolt,	*/
				/* the executive programmer of GraphEd. 	*/
	
#include <sgraph/std.h>
#include <sgraph/slist.h>
#include <sgraph/sgraph.h>
#include <sgraph/sgragra.h>
#include <sgraph/graphed.h>

#include "misc.h"
#include "lab_int.h"
#include "types.h"
#include "debug.h"
#include "convert.h"
#include "reduce.h"
#include "gram_opt.h"

#include "parser.h"
#include "tracer.h"

#include "win_defs.h"
#include "trace_cf.h"
#include "gram1_cf.h"
#include "gram2_cf.h"

/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/

#include "lp_redraw.h"
#include "lp_datastruc.h"

/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/

#include "main_sf.h"

#define BASEFRAME_SUBWINDOW_WIDTH	400
#define PARSER_PARALLEL

/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/

/************************************************************************************************/
/************************************************************************************************/
static void get_take_what_nodesizes(Panel_item item, int value, Event *event)
{
	switch( value )
	{
		case 0:	WIN.create_with_graph_nodesizes = 1;break;
		case 1:	WIN.create_with_graph_nodesizes = 0;
	}
}

/************************************************************************************************/
/************************************************************************************************/
static void get_what_to_do_with_derivated_node(Panel_item item, int value, Event *event)
{
	switch( value )
	{
		case 0:	WIN.what_to_do_with_derivated_node = 1;break;
		case 1:	WIN.what_to_do_with_derivated_node = 2;
	}
}

/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/

/********************************************************************************/
/* static global variables							*/

static	struct itimerval	timer;

static	unsigned short	parser_icon[] = {
#			include "parser.icon"
		};
		
static	unsigned short	parser_shadow_icon[] = {
#			include "parser_shadow.icon"
		};
		
/********************************************************************************/
/* static procedures: forward declaration 					*/

static	void	WIN_react_to_parser_status(void);

static	void	WIN_hide_all_windows(void);

static	void	WIN_nf_scan_graph(void);	/* (button) 'scan graph' proc 	*/

static	void	WIN_nf_scan_grammar(void);	/* (button) 'scan grammar' proc */

static	void	WIN_nf_prs_reset(void);	/* (button) 'reset' proc 	*/

static	void	WIN_nf_prs_run(void);	/* (button) 'run/cont' proc 	*/

static	void	WIN_nf_step(void);		/* parser 'step' procedure.	*/
					/* ifdef PARSER_PARALLEL, then notify proc of 'timer' */
					
static	void	WIN_nf_stop(void);		/* (button) 'stop' proc 	*/

static	void	WIN_nf_trace(void);		/* (button) 'trace' proc 	*/

static	void	WIN_nf_prs_inactive(void);	/* (menu) 'done' proc 		*/

/********************************************************************************/
/* diverse procedures								*/

static	void	WIN_react_to_parser_status(void)
{
	char	nr[20];
	
	if( PRS_info.status_changed == 0 ) {
		return;
	}
	if( PRS_test_changed( CHG_GRAM_LOAD ) ) {
		if( PRS_info.grammar_loaded ) {
			xv_set( WIN.grammar_text_item, PANEL_LABEL_STRING, "scanned", 0 );
			XV_awake( WIN.grammar_info_button );
		} else {
			xv_set( WIN.grammar_text_item, PANEL_LABEL_STRING, "---", 0 );
			XV_sleep( WIN.grammar_info_button );
		}
		PRS_changed( CHG_STATUS );
	}
	if( PRS_test_changed( CHG_GRAPH_LOAD ) ) {
		if( PRS_info.graph_loaded ) {
			xv_set( WIN.graph_text_item, PANEL_LABEL_STRING, "scanned", 0 );
		} else {
			xv_set( WIN.graph_text_item, PANEL_LABEL_STRING, "---", 0 );
		}
		PRS_changed( CHG_STATUS );
	}
	if( PRS_test_changed( CHG_PTAB_SIZE ) ) {
		sprintf( nr, "%d", PRS_info.number_of_pes );
		xv_set( WIN.number_pes_text_item, PANEL_LABEL_STRING, nr, 0 );
	}
	if( PRS_test_changed( CHG_STATUS ) ) {
		switch( PRS_info.status ) {
			case	PRS_RESET:	xv_set(		WIN.parser_run_button,
								PANEL_LABEL_STRING,	"run ",
								0 );
						XV_sleep( WIN.parser_stop_button );
						XV_sleep( WIN.parser_reset_button );
						XV_sleep( WIN.activate_tracer_button );
						XV_awake( WIN.grammar_scan_button );
						XV_awake( WIN.graph_scan_button );
						if( PRS_info.grammar_loaded && PRS_info.graph_loaded ) {
							XV_awake( WIN.parser_run_button );
						} else {
							XV_sleep( WIN.parser_run_button );
						}
						/********************************************************************************/
						/*			Layout Graph Grammars: BEGIN				*/
						/********************************************************************************/
						if( !ORGINAL_LAMSHOFT )
						{
							if( PRS_info.pars_table )
							{
								free_lp_parsing_element_with_lower_part( PRS_info.pars_table );
								PRS_info.pars_table = NULL;
							}

							XV_sleep( WIN.recompute_button );
							XV_awake( WIN.derivated_node_button );
						}
						/********************************************************************************/
						/*			Layout Graph Grammars: END				*/
						/********************************************************************************/
						break;
			case	PRS_RUNNING:	XV_sleep( WIN.parser_run_button );
						XV_awake( WIN.parser_stop_button );
						XV_sleep( WIN.parser_reset_button );
						XV_sleep( WIN.activate_tracer_button );
						XV_sleep( WIN.grammar_scan_button );
						XV_sleep( WIN.graph_scan_button );
						/********************************************************************************/
						/*			Layout Graph Grammars: BEGIN				*/
						/********************************************************************************/
						if( !ORGINAL_LAMSHOFT )
						{
							XV_sleep( WIN.recompute_button );
						}
						/********************************************************************************/
						/*			Layout Graph Grammars: END				*/
						/********************************************************************************/
						break;
			case	PRS_PAUSED:	xv_set(		WIN.parser_run_button,
								PANEL_LABEL_STRING,	"cont",
								0 );
						XV_awake( WIN.parser_run_button );
						XV_sleep( WIN.parser_stop_button );
						XV_awake( WIN.parser_reset_button );
						XV_awake( WIN.activate_tracer_button );
						XV_sleep( WIN.grammar_scan_button );
						XV_sleep( WIN.graph_scan_button );
						/********************************************************************************/
						/*			Layout Graph Grammars: BEGIN				*/
						/********************************************************************************/
						if( !ORGINAL_LAMSHOFT )
						{
							if( PRS_info.graph_in_grammar )
							{
								XV_awake( WIN.recompute_button );
							}
						}
						/********************************************************************************/
						/*			Layout Graph Grammars: END				*/
						/********************************************************************************/

						break;
			case	PRS_FINISHED:	XV_sleep( WIN.parser_run_button );
						XV_sleep( WIN.parser_stop_button );
						XV_awake( WIN.parser_reset_button );
						XV_awake( WIN.activate_tracer_button );
						XV_sleep( WIN.grammar_scan_button );
						XV_sleep( WIN.graph_scan_button );
						/********************************************************************************/
						/*			Layout Graph Grammars: BEGIN				*/
						/********************************************************************************/
						if( !ORGINAL_LAMSHOFT )
						{
							XV_awake( WIN.recompute_button );
						}
						/********************************************************************************/
						/*			Layout Graph Grammars: END				*/
						/********************************************************************************/
						break;
			default:		XV_sleep( WIN.parser_run_button );
						XV_sleep( WIN.parser_stop_button );
						XV_awake( WIN.parser_reset_button );
						XV_sleep( WIN.activate_tracer_button );
						XV_sleep( WIN.grammar_scan_button );
						XV_sleep( WIN.graph_scan_button );
						/********************************************************************************/
						/*			Layout Graph Grammars: BEGIN				*/
						/********************************************************************************/
						if( !ORGINAL_LAMSHOFT )
						{
							XV_sleep( WIN.recompute_button );
							XV_sleep( WIN.derivated_node_button );
						}
						/********************************************************************************/
						/*			Layout Graph Grammars: END				*/
						/********************************************************************************/
						break;
		}
		
		xv_set( WIN.parser_text_item, PANEL_LABEL_STRING, PRS_status_string(), 0 );
	}
	if( PRS_test_changed( CHG_MESSAGE ) ) {
		xv_set( WIN.parser_result_text_item, PANEL_LABEL_STRING, PRS_info.message, 0 );
	}
	PRS_clear_changed();
}

static	void	WIN_hide_all_windows(void)
{
	if( WIN.baseframe != XV_NULL ) {
		xv_set( WIN.baseframe, WIN_SHOW, FALSE, 0);
	}
	if( WIN.scan_options_baseframe != XV_NULL ) {
		xv_set( 	WIN.scan_options_baseframe,
				FRAME_CMD_PUSHPIN_IN,		FALSE,
				WIN_SHOW,			FALSE,
				0);
	}
	if( WIN.gram_info_baseframe != XV_NULL ) {
		xv_set( 	WIN.gram_info_baseframe,
				FRAME_CMD_PUSHPIN_IN,		FALSE,
				WIN_SHOW,			FALSE, 
				0);
	}
	if( WIN.baseframe != XV_NULL ) {
		xv_set( WIN.baseframe, WIN_SHOW, FALSE, 0);
	}
	if( WIN.baseframe != XV_NULL ) {
		xv_set( WIN.baseframe, WIN_SHOW, FALSE, 0);
	}
}	



/********************************************************************************/
/* notify procedures								*/


/* the following notify procedures are external:

	WIN_create_grammar_scan_options_subframe	in "gram2_cf.c"
	
	WIN_create_gram_info_subframe			in "gram1_cf.c"
	
*/


static	void	WIN_nf_scan_graph(void)	/* (button) 'scan graph' proc */
{
	call_sgraph_proc( PRS_scan_graph, NULL );
	WIN_react_to_parser_status();
}

static	void	WIN_nf_scan_grammar(void)	/* (button) 'scan grammar' proc */
{
	Sgragra_proc_info	info;
	
	info = init_sgragra_from_graphed_gragra( CVT_info.grammar_scan_from, TRUE );

	PRS_scan_grammar( info );

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
	
	if( ORGINAL_LAMSHOFT )
	{
		exit_sgragra_from_graphed_gragra( info );
	}

	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/

	WIN_gram_show_info();
	WIN_react_to_parser_status();
}

static	void	WIN_nf_prs_reset(void)	/* (button) 'reset' proc */
{
	if( TRC_info.status != TRC_INACTIVE ) {
		TRC_nf_quit();	/* this may change TRC_info.status */
	}
	if( TRC_info.status == TRC_INACTIVE ) {
		if( PRS_reset() ) {
			PRS_info.status = PRS_RESET;
			PRS_info.status_changed = -1;
			WIN_react_to_parser_status();
		}
	}
}


static	void	WIN_nf_prs_run(void)	/* (button) 'run/cont' proc. ifndef PARSER_PARALLEL, then main procedure
				of parser, else procedure to initialize 'timer'. */
{
	if( PRS_info.graph_loaded && PRS_info.grammar_loaded ) {
		if( PRS_info.status == PRS_RESET ) {
			timer.it_value.tv_usec = 16000;
			timer.it_interval.tv_usec = 0;
			PRS_prepare_parser();
		}
		if( PRS_info.status != PRS_ERROR ) {
			if( TRC_info.status != TRC_INACTIVE ) {
				TRC_nf_quit();
			}
			if( TRC_info.status == TRC_INACTIVE ) {
				PRS_info.status = PRS_RUNNING;
		
#				ifdef PARSER_PARALLEL
					notify_set_itimer_func( WIN.baseframe,	(Notify_func)WIN_nf_step,
										ITIMER_REAL, &timer, NULL );
	
#				else
					while( PRS_info.status == PRS_RUNNING ) {
						(void) WIN_nf_step();
					}
#				endif

			}
		}
		PRS_info.status_changed = -1;
		WIN_react_to_parser_status();
	}
}


static	void	WIN_nf_step(void)	/* parser 'step' procedure. ifdef PARSER_PARALLEL, then notify proc of 'timer' */
{
	
#	ifdef PARSER_PARALLEL
		if( PRS_info.status == PRS_RUNNING ) {
			notify_set_itimer_func( WIN.baseframe,	NOTIFY_FUNC_NULL,
								ITIMER_REAL, NULL, NULL );
		}
#	endif

	PRS_step();
	WIN_react_to_parser_status();
	
#	ifdef PARSER_PARALLEL
		if( PRS_info.status == PRS_RUNNING ) {
			notify_set_itimer_func( WIN.baseframe,	(Notify_func)WIN_nf_step,
								ITIMER_REAL, &timer, NULL );
		}
#	endif
}

static	void	WIN_nf_stop(void)		/* (button) 'stop' proc */
{
	notify_set_itimer_func( WIN.baseframe,	NOTIFY_FUNC_NULL,
						ITIMER_REAL, NULL, NULL );
	
	PRS_pause();
	WIN_react_to_parser_status();
}

static	void	WIN_nf_trace(void)		/* (button) 'trace' proc */
{
	XV_sleep( WIN.activate_tracer_button );
	TRC_create_tracer_subframe( WIN.baseframe );	
}

static	void	WIN_nf_prs_inactive(void)	/* (menu) 'done' proc */
{
	if( TRC_info.status != TRC_INACTIVE ) {
		TRC_nf_quit();	/* this may change TRC_info.status */
	}
	if( TRC_info.status == TRC_INACTIVE ) {
		if( PRS_deactivate() ) {
			WIN_hide_all_windows();
			/********************************************************************************/
			/*			Layout Graph Grammars: BEGIN				*/
			/********************************************************************************/
			SUBFRAME_VISIBLE = FALSE;
			/********************************************************************************/
			/*			Layout Graph Grammars: END				*/
			/********************************************************************************/
			return;
		} else {
			PRS_info.message = "Please stop parser before leaving it!";
			PRS_changed( CHG_MESSAGE );
		}
	} else {
		PRS_info.message = "Select tracing graph that the tracer can be closed.";
		PRS_changed( CHG_MESSAGE );
	}
	WIN_react_to_parser_status();
}



	
void	WIN_create_parser_subframe(Menu menu, Menu_item menu_item)
{
	static	int		created = FALSE;
		Server_image	image1, image2;
		int		error = FALSE;
		Panel_item	tmp;
		Icon		icon;

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
	/*** hier der minimal moegliche Eingriff um mit einem Algorithmus und zwei    ***/
	/*** verschiedenen Oberflaechen zu arbeiten:				      ***/
	/*** Wenn Fenster (frueher) schon anders gezeichnet wurde, dann loesche es    ***/
	/*** setze created auf FALSE und lasse neu zeichnen			      ***/
	/*** Es gilt: Ob zeichnen erlaubt ist, wurde beim Aufruf ueberprueft	      ***/
	/********************************************************************************/

	if( created )
	{
		/****** Ganz am Anfang ist LAST_CREATED_FOR_LAMSHOFT nicht gesetzt ******/
		if( LAST_CREATED_FOR_LAMSHOFT != ORGINAL_LAMSHOFT )
		{
			/****** In diesem Fall gibts einen Konflickt zwischen alter und neuer Oberflaeche ******/
			/*** Unsere Datenstrukturen loeschen***/
			if( PRS_info.pars_table )
			{
				free_lp_parsing_element_with_lower_part( PRS_info.pars_table );
				PRS_info.pars_table = NULL;
				free_copy_iso_in_lams_table( PRS_info.start_elements );
			}
			created = FALSE;
			PRS_reset();
			xv_destroy( WIN.baseframe );
		}
	}
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/


	
    if( !created ) {

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
	LAST_CREATED_FOR_LAMSHOFT	= ORGINAL_LAMSHOFT;	      /**** Nur BOOL! ***/
	SUBFRAME_VISIBLE		= TRUE;

	if( ORGINAL_LAMSHOFT )
	{
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/

		WIN.baseframe = (Frame) xv_create(	base_frame,			FRAME,
						FRAME_LABEL,	"graph grammar parser (1994 by Lamshoeft Thomas)",
						FRAME_SHOW_LABEL,	TRUE,
						XV_SHOW,		FALSE,
						FRAME_NO_CONFIRM,	TRUE,
						FRAME_DONE_PROC,	WIN_nf_prs_inactive,
						FRAME_SHOW_RESIZE_CORNER, FALSE,
						0);

	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/
	}
	else
	{
		WIN.baseframe = (Frame) xv_create(	base_frame,			FRAME,
						FRAME_LABEL,	"optimal size layout      V 0.25b",
						FRAME_SHOW_LABEL,	TRUE,
						XV_SHOW,		FALSE,
						FRAME_NO_CONFIRM,	TRUE,
						FRAME_DONE_PROC,	WIN_nf_prs_inactive,
						FRAME_SHOW_RESIZE_CORNER, FALSE,
						0);
	}
	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/


	if( WIN.baseframe == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	}
	image1 = (Server_image) xv_create(	XV_NULL,		SERVER_IMAGE,
						XV_WIDTH,		64,
						XV_HEIGHT,		64,
						SERVER_IMAGE_BITS,	parser_icon,
						0 );
	image2 = (Server_image) xv_create(	XV_NULL,		SERVER_IMAGE,
						XV_WIDTH,		64,
						XV_HEIGHT,		64,
						XV_DEPTH,		1,
						SERVER_IMAGE_BITS,	parser_shadow_icon,
						0 );
	icon = (Icon) xv_create(		WIN.baseframe,		ICON,
						ICON_IMAGE,		image1,
						ICON_MASK_IMAGE,	image2,
						ICON_TRANSPARENT,	TRUE,
						0 );
	if( icon != XV_NULL ) {
		xv_set( WIN.baseframe, FRAME_ICON, icon, 0 );
	}
	
	
	WIN.grammar_panel = (Panel) xv_create(	WIN.baseframe,		PANEL,
						XV_X,			0,
						XV_WIDTH,		BASEFRAME_SUBWINDOW_WIDTH,
						WIN_ROW_GAP,		4,
						0);
	if( WIN.grammar_panel == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	}
	
	tmp = xv_create(			WIN.grammar_panel,	PANEL_MESSAGE,
						PANEL_LABEL_BOLD,	TRUE,
						PANEL_LABEL_STRING,	"GRAMMAR:",
						XV_X,			xv_col(WIN.grammar_panel, 1),
						XV_Y,			xv_row(WIN.grammar_panel, 0),
						0);
	
	tmp = xv_create(			WIN.grammar_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"---",
						XV_X,			xv_col( WIN.grammar_panel, 11),
						XV_Y,			xv_row( WIN.grammar_panel, 0),
						0);
						
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.grammar_text_item = tmp;
	}
	
	tmp = xv_create(			WIN.grammar_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.grammar_panel, 1),
						XV_Y,			xv_row(WIN.grammar_panel, 1),
						PANEL_LABEL_STRING,	"scan grammar",
						PANEL_NOTIFY_PROC,	WIN_nf_scan_grammar,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.grammar_scan_button = tmp;
	}

	
	tmp = xv_create(			WIN.grammar_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.grammar_panel, 18),
						XV_Y,			xv_row(WIN.grammar_panel, 1),
						PANEL_LABEL_STRING,	"show info",
						PANEL_NOTIFY_PROC,	WIN_create_gram_info_subframe,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.grammar_info_button = tmp;
	}


	tmp = xv_create(			WIN.grammar_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.grammar_panel, 35),
						XV_Y,			xv_row(WIN.grammar_panel, 1),
						PANEL_LABEL_STRING,	"scan options...",
						PANEL_NOTIFY_PROC,	WIN_create_grammar_scan_options_subframe,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.grammar_options_button = tmp;
	}

	window_fit_height( WIN.grammar_panel );
	XV_win_position_down( WIN.grammar_panel, 1 );
	
	WIN.graph_panel = (Panel) xv_create(	WIN.baseframe,		PANEL,
						WIN_BELOW,		WIN.grammar_panel,
						XV_X,			0,
						XV_WIDTH,		BASEFRAME_SUBWINDOW_WIDTH,
						WIN_ROW_GAP,		4,
						0);
	if( WIN.graph_panel == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	}
	
	tmp = xv_create(			WIN.graph_panel,	PANEL_MESSAGE,
						PANEL_LABEL_BOLD,	TRUE,
						XV_X,			xv_col(WIN.graph_panel, 1),
						XV_Y,			xv_row(WIN.graph_panel, 0),
						PANEL_LABEL_STRING,	"GRAPH:",
						0);

	tmp = xv_create(			WIN.graph_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"---",
						XV_X,			xv_col( WIN.graph_panel, 11),
						XV_Y,			xv_row( WIN.graph_panel, 0),
						0);
						
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.graph_text_item = tmp;
	}

						
	tmp = xv_create(			WIN.graph_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.graph_panel, 1),
						XV_Y,			xv_row(WIN.graph_panel, 1),
						PANEL_LABEL_STRING,	"scan graph",
						PANEL_NOTIFY_PROC,	WIN_nf_scan_graph,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.graph_scan_button = tmp;
	}

	
	window_fit_height( WIN.graph_panel );
	XV_win_position_down( WIN.graph_panel, 1 );
						
	WIN.parser_panel = (Panel) xv_create(	WIN.baseframe,		PANEL,
						WIN_BELOW,		WIN.graph_panel,
						XV_X,			0,
						XV_WIDTH,		BASEFRAME_SUBWINDOW_WIDTH,
						WIN_ROW_GAP,		4,
						0);
	if( WIN.parser_panel == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	}

	tmp = xv_create(			WIN.parser_panel,	PANEL_MESSAGE,
						XV_X,			xv_col(WIN.parser_panel, 1),
						XV_Y,			xv_row(WIN.parser_panel, 0),
						PANEL_LABEL_BOLD,	TRUE,
						PANEL_LABEL_STRING,	"PARSER:",
						0);

	tmp = xv_create(			WIN.parser_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"---",
						XV_X,			xv_col( WIN.parser_panel, 11),
						XV_Y,			xv_row( WIN.parser_panel, 0),
						0);
						
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.parser_text_item = tmp;
	}

						
	tmp = xv_create(			WIN.parser_panel,	PANEL_MESSAGE,
						XV_X,			xv_col(WIN.parser_panel, 1),
						XV_Y,			xv_row(WIN.parser_panel, 1),
						PANEL_LABEL_STRING,	"# PE's :",
						0);
						
	tmp = xv_create(			WIN.parser_panel,	PANEL_MESSAGE,
						XV_X,			xv_col(WIN.parser_panel, 8),
						XV_Y,			xv_row(WIN.parser_panel, 1),
						PANEL_LABEL_STRING,	"0",
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.number_pes_text_item = tmp;
	}

						
	tmp = xv_create(			WIN.parser_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.parser_panel, 1),
						XV_Y,			xv_row(WIN.parser_panel, 2),
						PANEL_LABEL_STRING,	"run ",
						PANEL_NOTIFY_PROC,	WIN_nf_prs_run,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.parser_run_button = tmp;
	}

						
	tmp = xv_create(			WIN.parser_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.parser_panel, 8),
						XV_Y,			xv_row(WIN.parser_panel, 2),
						PANEL_LABEL_STRING,	"stop",
						PANEL_NOTIFY_PROC,	WIN_nf_stop,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.parser_stop_button = tmp;
	}

						
	tmp = xv_create(			WIN.parser_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.parser_panel, 15),
						XV_Y,			xv_row(WIN.parser_panel, 2),
						PANEL_LABEL_STRING,	"reset",
						PANEL_NOTIFY_PROC,	WIN_nf_prs_reset,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.parser_reset_button = tmp;
	}

		
	tmp = xv_create(			WIN.parser_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.parser_panel, 35),
						XV_Y,			xv_row(WIN.parser_panel, 2),
						PANEL_LABEL_STRING,	"trace...",
						PANEL_NOTIFY_PROC,	WIN_nf_trace,
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.activate_tracer_button = tmp;
	}
	
	tmp = xv_create(			WIN.parser_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"X",
						PANEL_LABEL_BOLD,	TRUE,
						XV_X,			xv_col( WIN.parser_panel, 1),
						XV_Y,			xv_row( WIN.parser_panel, 3),
						0);
						
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	PARSER_error_handling;
	} else {
		WIN.parser_result_text_item = tmp;
	}
					
	window_fit_height( WIN.parser_panel );
	XV_win_position_down( WIN.parser_panel, 1 );
	window_fit( WIN.baseframe );


	/********************************************************************************/
	/*			Layout Graph Grammars: BEGIN				*/
	/********************************************************************************/

	if( !ORGINAL_LAMSHOFT )
	{
		WIN.redraw_panel = (Panel) xv_create(
						WIN.baseframe,		PANEL,
						WIN_BELOW,		WIN.parser_panel,
						XV_X,			0,
						XV_WIDTH,		BASEFRAME_SUBWINDOW_WIDTH,
						WIN_ROW_GAP,		4,
						0);



		tmp = xv_create(	WIN.redraw_panel,	PANEL_MESSAGE,
						XV_X,			xv_col(WIN.redraw_panel, 1),
						XV_Y,			xv_row(WIN.redraw_panel, 0),
						PANEL_LABEL_BOLD,	TRUE,
						PANEL_LABEL_STRING,	"REDRAW:",
						0);
					
		if( tmp == XV_NULL ) 
		{
				error = TRUE;
				goto	PARSER_error_handling;
		}
		else
		{
				WIN.redraw_text_item = tmp;
		}



		tmp = xv_create(	WIN.redraw_panel,	PANEL_BUTTON,
						XV_X,			xv_col(WIN.redraw_panel, 15),
						XV_Y,			xv_row(WIN.redraw_panel, 0),
						PANEL_LABEL_STRING,	"recompute graph",
						PANEL_NOTIFY_PROC,	lp_create_optimal_graph_layout,
						0);

		if( tmp == XV_NULL )
		{
				error = TRUE;
				goto	PARSER_error_handling;
		}
		else
		{
				WIN.recompute_button = tmp;
		}



		tmp = xv_create(		WIN.redraw_panel,		PANEL_CHOICE,
						XV_X,				xv_col(WIN.redraw_panel, 3),
						XV_Y,				xv_row(WIN.redraw_panel, 1),
						PANEL_LABEL_STRING,		"Show Hierarchy          ",
						PANEL_CHOICE_STRINGS,		"       Yes     ",
										"     No    ", 0,
						PANEL_VALUE,			0,
						PANEL_CLIENT_DATA,		get_what_to_do_with_derivated_node,
										/* ^ the real notify proc */
	
						PANEL_NOTIFY_PROC,		XV_cycle_panel_choice_stack,
						PANEL_LABEL_BOLD,		FALSE,
						0);
		if( tmp == XV_NULL ) 
		{
				error = TRUE;
				goto	PARSER_error_handling;
		}
		XV_cycle_panel_choice_stack( tmp, 5, NULL );



		tmp = xv_create(		WIN.redraw_panel,		PANEL_CHOICE,
						XV_X,				xv_col(WIN.redraw_panel, 3),
						XV_Y,				xv_row(WIN.redraw_panel, 2),
						PANEL_LABEL_STRING,		"Node Sizes                  ",
						PANEL_CHOICE_STRINGS,		"Graph Sizes", "  Large   ", 0,
						PANEL_VALUE,			0,
						PANEL_CLIENT_DATA,		get_take_what_nodesizes,
											/* ^ the real notify proc */
	
						PANEL_NOTIFY_PROC,		XV_cycle_panel_choice_stack,
						PANEL_LABEL_BOLD,		FALSE,
						0);
		if( tmp == XV_NULL ) 
		{
				error = TRUE;
				goto	PARSER_error_handling;
		}
		XV_cycle_panel_choice_stack( tmp, 5, NULL );




		window_fit_height( WIN.redraw_panel );
		XV_win_position_down( WIN.redraw_panel, 1 );
		window_fit( WIN.baseframe );

		/****** Initialisieren ******/
		WIN.create_with_graph_nodesizes		= 0;
		WIN.what_to_do_with_derivated_node	= 0;
	}	/****** ORGINAL_LAMSHOFT ******/

	/********************************************************************************/
	/*			Layout Graph Grammars: END				*/
	/********************************************************************************/

PARSER_error_handling:
	if( error ) {
		if( WIN.baseframe != XV_NULL ) {
			xv_destroy( WIN.baseframe );
		}
		return;
	} else {
		created = TRUE;
		PRS_init();
		CVT_init();
		xv_set( WIN.baseframe, XV_SHOW, TRUE, 0);
	}
	
    }
    /* init parser */
    WIN_nf_prs_reset();

    /* position window */
   
/********************************************************************************/
/*			Layout Graph Grammars: BEGIN				*/
/********************************************************************************/
/*****Auskommentiert Walter
    {
	Event	*event;
	int	xpos, ypos;
	event = (Event *)xv_get( menu, MENU_LAST_EVENT );
	if( event != NULL ) {
		xpos = event_x( event );
		ypos = event_y( event );
		printf( "last -  x: %d\t y:%d\n", xpos, ypos );
		XV_WIN_position_absolute( WIN.baseframe, xpos, ypos );
	} else {
	
		XV_WIN_position_relative( WIN.baseframe, base_frame, 400, 20 );
	}
	event = (Event *)xv_get( menu, MENU_FIRST_EVENT );
	if( event != NULL ) {
		xpos = event_x( event );
		ypos = event_y( event );
		printf( "first - x: %d\t y:%d\n", xpos, ypos );
	}
   }

*****/
	XV_WIN_position_relative( WIN.baseframe, base_frame, 400, 20 );


/********************************************************************************/
/*			Layout Graph Grammars: END				*/
/********************************************************************************/
   
    /* show window */
    xv_set( WIN.baseframe, XV_SHOW, TRUE, 0);
}
