#include <xview/xview.h>
#include <xview/panel.h>
#include "misc.h"
#include "convert.h"
#include "win_defs.h"
#include "gram2_cf.h"
#include "main_sf.h"

static void nf_scan_from(Panel_item item, int value, Event *event)
{
	CVT_info.grammar_scan_from = value + 1;
}


static void nf_link_isomorph_productions(Panel_item item, int value, Event *event)
{
	if( value == 1 ) {
		CVT_info.grammar_link_isomorph_productions = TRUE;
	} else {
		CVT_info.grammar_link_isomorph_productions = FALSE;
	}
}


static void nf_reduce(Panel_item item, int value, Event *event)
{
	if( (value & 1) ) {
		CVT_info.grammar_reduce_productions = TRUE;
	} else {
		CVT_info.grammar_reduce_productions = FALSE;
	}
	if( (value & 2) ) {
		CVT_info.grammar_reduce_embeddings = TRUE;
	} else {
		CVT_info.grammar_reduce_embeddings = FALSE;
	}
}

void	WIN_create_grammar_scan_options_subframe(void)
{
	static	int	created = FALSE;
		int	error = FALSE;
	Panel_item	tmp;
	
	if( !created ) {
		WIN.scan_options_baseframe = (Frame) xv_create( WIN.baseframe,		FRAME_CMD,
							FRAME_LABEL,			"grammar scan options",
							FRAME_SHOW_LABEL,		TRUE,
							FRAME_SHOW_RESIZE_CORNER,	FALSE,
							FRAME_CMD_PUSHPIN_IN,		TRUE,
							XV_SHOW,			FALSE,
							0);
		if( WIN.scan_options_baseframe == XV_NULL ) {
			error = TRUE;
			goto	SCAN_OPTIONS_error_handling;
		}
		
		WIN.scan_options_panel = (Panel) xv_get(WIN.scan_options_baseframe,	FRAME_CMD_PANEL);
		if( WIN.scan_options_panel == XV_NULL ) {
			error = TRUE;
			goto	SCAN_OPTIONS_error_handling;
		}
		xv_set( WIN.scan_options_panel, 	XV_MARGIN,		0,
							XV_X,			0,
							XV_Y,			0,
							WIN_ROW_GAP,		4,
							0);
		
		tmp = xv_create(			WIN.scan_options_panel, PANEL_CHECK_BOX,
							PANEL_LABEL_STRING,	"reduce:",
							PANEL_LABEL_X,		xv_col(WIN.scan_options_panel, 1),
							PANEL_LABEL_Y,		xv_row(WIN.scan_options_panel, 0),
							PANEL_CHOOSE_ONE,	FALSE,
							PANEL_CHOICE_STRINGS,	"productions", "embeddings", 0,
							PANEL_CHOICE_XS,	xv_col(WIN.scan_options_panel, 10), 0,
							PANEL_CHOICE_YS,	xv_row(WIN.scan_options_panel,0),
										xv_row(WIN.scan_options_panel,1),
										0,
							PANEL_VALUE,		7,
							PANEL_NOTIFY_PROC,	nf_reduce,
							0);
		if( tmp == XV_NULL ) {
			error = TRUE;
			goto	SCAN_OPTIONS_error_handling;
		}
	
		tmp = xv_create(			WIN.scan_options_panel, PANEL_CHOICE_STACK,
							XV_X,			xv_col(WIN.grammar_panel, 1),
							XV_Y,			xv_row(WIN.grammar_panel, 3),
							PANEL_LABEL_STRING,	"link isomorph productions ?",
							PANEL_CHOICE_STRINGS,	"no", "yes", 0,
							PANEL_VALUE,		0,
							PANEL_CLIENT_DATA,	nf_link_isomorph_productions, 
										/* ^ the real notify proc */
	
							PANEL_NOTIFY_PROC,	XV_cycle_panel_choice_stack,
							0);
		if( tmp == XV_NULL ) {
			error = TRUE;
			goto	SCAN_OPTIONS_error_handling;
		}
		XV_cycle_panel_choice_stack( tmp, 1, NULL );
		
		tmp = xv_create(			WIN.scan_options_panel, PANEL_CHOICE_STACK,
							XV_X,			xv_col(WIN.grammar_panel, 1),
							XV_Y,			xv_row(WIN.grammar_panel, 4),
							PANEL_LABEL_STRING,	"scan from ",
							PANEL_CHOICE_STRINGS,	"empty grammar", "current prod window",
										"active window", "all windows", 0,
							PANEL_VALUE,		0,
							PANEL_CLIENT_DATA,	nf_scan_from,
										/* ^ the real notify proc */
	
							PANEL_NOTIFY_PROC,	XV_cycle_panel_choice_stack,
							0);
		if( tmp == XV_NULL ) {
			error = TRUE;
			goto	SCAN_OPTIONS_error_handling;
		}
		XV_cycle_panel_choice_stack( tmp, 2, NULL );
	
		window_fit( WIN.scan_options_panel );
		window_fit( WIN.scan_options_baseframe );
		
		SCAN_OPTIONS_error_handling:

		if( error ) {
			if( WIN.scan_options_baseframe != XV_NULL ) {
				xv_destroy( WIN.scan_options_baseframe );
			}
			return;
		} else {
			/********************************************************************************/
			/*			Layout Graph Grammars: BEGIN				*/
			/********************************************************************************/
			/*
			created = TRUE;
			*/
			/********************************************************************************/
			/*			Layout Graph Grammars: END				*/
			/********************************************************************************/
		}
		
	}
	XV_CMD_position_relative( WIN.scan_options_baseframe, WIN.baseframe, 200, 20 );
	xv_set( WIN.scan_options_baseframe, 	XV_SHOW,		TRUE,
						FRAME_CMD_PUSHPIN_IN,	TRUE,
						0);
}

