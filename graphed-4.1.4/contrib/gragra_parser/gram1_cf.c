#include <xview/xview.h>
#include <xview/panel.h>
#include "misc.h"
#include "lab_int.h"
#include "types.h"
#include "parser.h"
#include "win_defs.h"
#include "gram1_cf.h"
#include "main_sf.h"


void	WIN_gram_show_info(void)
{
	if( WIN.gram_info_baseframe != XV_NULL ) {
	    if( PRS_info.grammar_loaded ) {
		if( PRS_info.grammar_directed ) {
			xv_set( WIN.gram_directed_text_item,
				PANEL_LABEL_STRING,		"yes",
				0);
		} else {
			xv_set( WIN.gram_directed_text_item,
				PANEL_LABEL_STRING,		"no",
				0);
		}
		if( PRS_info.grammar_boundary ) {
			xv_set( WIN.gram_boundary_text_item,
				PANEL_LABEL_STRING,		"yes",
				0);
		} else {
			xv_set( WIN.gram_boundary_text_item,
				PANEL_LABEL_STRING,		"no",
				0);
		}
		xv_set( WIN.gram_start_symbol_text_item,
			PANEL_LABEL_STRING,	MISC_get_grammar_start_symbol(),
			0);
		xv_set( WIN.gram_nodelabels_text_item,
			PANEL_LABEL_STRING,	LI_nodelabels_string(),
			0);
		xv_set( WIN.gram_edgelabels_text_item,
			PANEL_LABEL_STRING,	LI_edgelabels_string(),
			0);
		window_fit( WIN.gram_info_panel );
		window_fit( WIN.gram_info_baseframe );
	    } else {
		xv_set( WIN.gram_info_baseframe, FRAME_CMD_PUSHPIN_IN,	FALSE,
						 XV_SHOW,		FALSE,
						 0);
	    }
	}
}

void	WIN_create_gram_info_subframe(void)
{
	static	int	created = FALSE;
		int	error = FALSE;
	Panel_item	tmp;
	
  if( !created ) {
	WIN.gram_info_baseframe = (Frame) xv_create(	WIN.baseframe,		FRAME_CMD,
						FRAME_LABEL,			"grammar info",
						FRAME_SHOW_LABEL,		TRUE,
						FRAME_SHOW_RESIZE_CORNER,	FALSE,
						FRAME_CMD_PUSHPIN_IN,		TRUE,
						XV_SHOW,			FALSE,
						0);
	if( WIN.gram_info_baseframe == XV_NULL ) {
		error = TRUE;
		goto	GRAM_INFO_error_handling;
	}
	
	WIN.gram_info_panel = (Panel) xv_get(WIN.gram_info_baseframe,	FRAME_CMD_PANEL);
	if( WIN.gram_info_panel == XV_NULL ) {
		error = TRUE;
		goto	GRAM_INFO_error_handling;
	}
	xv_set( WIN.gram_info_panel,		XV_MARGIN,		0,
						XV_X,			0,
						XV_Y,			0,
						WIN_ROW_GAP,		4,
						0);
	
	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"directed :",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 1),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 0),
						0);
	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 13),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 0),
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	GRAM_INFO_error_handling;
	} else {
		WIN.gram_directed_text_item = tmp;
	}

	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"boundary :",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 1),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 1),
						0);
	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 13),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 1),
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	GRAM_INFO_error_handling;
	} else {
		WIN.gram_boundary_text_item = tmp;
	}

	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"start symbol :",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 1),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 2),
						0);
	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 13),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 2),
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	GRAM_INFO_error_handling;
	} else {
		WIN.gram_start_symbol_text_item = tmp;
	}

	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"nodelabels :",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 1),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 3),
						0);
	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 13),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 3),
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	GRAM_INFO_error_handling;
	} else {
		WIN.gram_nodelabels_text_item = tmp;
	}

	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"edgelabels :",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 1),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 4),
						0);
	tmp = xv_create(			WIN.gram_info_panel,	PANEL_MESSAGE,
						PANEL_LABEL_STRING,	"",
						PANEL_LABEL_X,		xv_col(WIN.gram_info_panel, 13),
						PANEL_LABEL_Y,		xv_row(WIN.gram_info_panel, 4),
						0);
	if( tmp == XV_NULL ) {
		error = TRUE;
		goto	GRAM_INFO_error_handling;
	} else {
		WIN.gram_edgelabels_text_item = tmp;
	}

	window_fit( WIN.gram_info_panel );
/*	xv_set( WIN.gram_info_baseframe , XV_HEIGHT, 140, XV_WIDTH, 300 ,0);
*/
	window_fit( WIN.gram_info_baseframe );
	
GRAM_INFO_error_handling:
	if( error ) {
		if( WIN.gram_info_baseframe != XV_NULL ) {
			xv_destroy( WIN.gram_info_baseframe );
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
    WIN_gram_show_info();
    XV_CMD_position_relative( WIN.gram_info_baseframe, WIN.baseframe, 200, 20 );
    xv_set( WIN.gram_info_baseframe,	XV_SHOW,		TRUE,
					FRAME_CMD_PUSHPIN_IN,	TRUE,
					0);

}
