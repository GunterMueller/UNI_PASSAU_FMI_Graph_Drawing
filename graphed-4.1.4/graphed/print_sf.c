/* (C) Universitaet Passau 1986-1994 */
/* GraphEd Source, 1986-1994 by Michael Himsolt, Marv Felsberg		*/
/************************************************************************/
/*									*/
/*				print_sf.c				*/
/*									*/
/************************************************************************/
/*									*/
/*									*/
/************************************************************************/


#include "misc.h"
#include "graph.h"
#include "graphed_subwindows.h"

#include "print.h"
#include "repaint.h"
#include "fileselector/fileselect.h"
#include "type.h"
#include "ps.h"

/************************************************************************/
/*									*/
/*			GLOBALE PROZEDUREN				*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	void	show_print_subframe ()					*/
/*									*/
/************************************************************************/

/************************************************************************/
/*									*/
/*			LOKALE VARIABLE					*/
/*									*/
/************************************************************************/

static	Fs_item		print_fileselector;

static	Panel_item	print_device_cycle;
static	Panel_item	print_area_cycle;
static	Panel_item	print_color_toggle;
static	Panel_item	print_ps_scaling_tofit;	/* PS_FELSBERG */
static	Panel_item	print_ps_frame_visible;	/* PS_FELSBERG */
static	Panel_item	print_ps_portrait;	/* PS_FELSBERG */
static	Panel_item	print_ps_margin_text;	/* PS_FELSBERG */
static	Panel_item	print_ps_margin_left;	/* PS_FELSBERG */
static	Panel_item	print_ps_margin_right;	/* PS_FELSBERG */
static	Panel_item	print_ps_margin_top;	/* PS_FELSBERG */
static	Panel_item	print_ps_margin_bottom;	/* PS_FELSBERG */
static	Panel_item	print_ps_scaling_item;	/* PS_FELSBERG */

static	void	notify_print_cycles (Panel_item item, int value, Event *event);
static	void	create_panel_items (Panel panel);
static	void	print_from_fileselector (char *dir, char *file);
static	void	on_off_ps_items (int yes_no);

#define	toggle_bit_on(value,bit)	(((unsigned int)value) & (1 << (bit)))
#define	toggle_bit_off(value,bit)	(!(toggle_bit_on(value,bit)))

static	char	*device_strings [] = {
			"Postscript",
			"Rasterfile"
		};
		
static	char	*area_strings [] = {
			"visible area",
			"full area",
		};
/*		
static	char	*color_strings [] = {
			"color",
			"no color",
		};
*/
		
/************************************************************************/
/*									*/
/*		FILE_SELECTION_SUBFRAME VERWALTEN			*/
/*									*/
/*									*/
/************************************************************************/
/*									*/
/*	void	show_print_subframe ()					*/
/*									*/
/*----------------------------------------------------------------------*/
/*									*/
/*	static	notify_print_cycles  (item, event)			*/
/*									*/
/************************************************************************/

void	init_print_fileselector (void)
{
	static	int	first = TRUE;
	
	if( first ) {

		first = FALSE;

		print_settings = init_print_settings ();
		print_fileselector = fls_create();

		fls_setup_from_file (
			print_fileselector,
			get_existing_fileselector_startup_filename(),
			"printer" );
		fls_set_user_panel_items_create_proc (
			print_fileselector,
			create_panel_items );
		fls_set_info (
			print_fileselector,
			" << PRINT GRAPH >>" );
	}
}

void	show_print_subframe (void)
{	
	init_print_fileselector ();
	fileselect( print_fileselector, base_frame, print_from_fileselector );
}

void	write_print_fileselector(FILE *file)
{
	init_print_fileselector ();
	fls_write_to_file( print_fileselector, file, "printer" );
	fls_close (print_fileselector);
}


void	save_print_settings (void)
{
	print_settings.device =
		(Print_device)xv_get(print_device_cycle, PANEL_VALUE);
	print_settings.area =
		(Print_area)xv_get(print_area_cycle, PANEL_VALUE);
	print_settings.color =
		toggle_bit_on(xv_get(print_color_toggle, PANEL_VALUE),0) ?
			PRINT_COLOR :
			PRINT_NOCOLOR;

	print_settings.ps.fit =
		xv_get(print_ps_scaling_tofit, PANEL_VALUE) ? TRUE : FALSE;
	print_settings.ps.scaling =
		(double)xv_get(print_ps_scaling_item, PANEL_VALUE) / 500.0;
	print_settings.ps.frame_visible =
		xv_get(print_ps_frame_visible, PANEL_VALUE) ? TRUE : FALSE;
	print_settings.ps.orientation =
		xv_get(print_ps_portrait, PANEL_VALUE) ? PS_PORTRAIT : PS_LANDSCAPE;

	print_settings.ps.margin_left =
		(double)xv_get(print_ps_margin_left, PANEL_VALUE);
	print_settings.ps.margin_right =
		(double)xv_get(print_ps_margin_right, PANEL_VALUE),
	print_settings.ps.margin_top =
		(double)xv_get(print_ps_margin_top, PANEL_VALUE);
	print_settings.ps.margin_bottom =
		(double)xv_get(print_ps_margin_bottom, PANEL_VALUE);

}

static	void	print_from_fileselector (char *dir, char *file)
{
	char		filename  [FILENAMESIZE];
	
	save_print_settings();
		
	if (strcmp(file,"NOTHING SELECTED")) {

		/* Generate Filename	*/
		strcpy (filename, dir);
		if( strcmp( dir, "" ) && strcmp( dir, "/" ) ) {
			strcat( filename, "/" );
		}
		strcat( filename, file );
		
		if (filename == NULL || !strcmp (filename, "")) {
			error ("No filename provided.\n");
			return;
		} else if (!check_file_is_single (filename)) {
			return;
		} else if (file_exists (filename) && NOTICE_NO == notice_prompt (base_frame, NULL,
			NOTICE_MESSAGE_STRINGS,	"This file does already exist.", NULL,
			NOTICE_BUTTON_YES,	"Overwrite",
			NOTICE_BUTTON_NO,	"Cancel",
			NULL)) {
			return;
		}

		print_buffer (filename, wac_buffer, print_settings);
	}
}


static	void	create_panel_items(Panel panel)
{
	int 	row_count = 0;
	int	row_count_options_start;
		
	row_count = 0;	
	print_device_cycle  = xv_create(panel, PANEL_CYCLE,
		XV_X,			xv_col(panel,0),
		XV_Y,			xv_row(panel,row_count),
		PANEL_CHOICE_STRINGS,	device_strings [OUTPUT_PS],
					device_strings [OUTPUT_XBITMAP_FILE],
					NULL,
		PANEL_VALUE,		print_settings.device,
		PANEL_NOTIFY_PROC,	notify_print_cycles,
		NULL);

	row_count += 1;
	print_area_cycle  = xv_create(panel, PANEL_CHOICE,
		PANEL_LAYOUT,		PANEL_HORIZONTAL,
		XV_X,			xv_col(panel,0),
		XV_Y,			xv_row(panel,row_count),
		PANEL_CHOICE_STRINGS,	area_strings [AREA_VISIBLE],
					area_strings [AREA_FULL],
					NULL,
		PANEL_VALUE,		print_settings.area,
		NULL);

	row_count += 1;
	print_color_toggle  = xv_create(panel, PANEL_TOGGLE,
		XV_X,			xv_col(panel,0),
		XV_Y,			xv_row(panel,row_count),
		PANEL_CHOICE_STRINGS,	"color", 0,
		PANEL_VALUE,		print_settings.color,
		XV_SHOW,		FALSE,
		NULL);

	row_count += 2;
	row_count_options_start = row_count;

	/* Postscript Options */	/* PS_FELSBERG */
	
	row_count = row_count_options_start;
	print_ps_scaling_tofit = xv_create(panel, PANEL_CHECK_BOX,
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_LABEL_STRING,	"Fit on Page:",
		PANEL_NEXT_ROW,		-1,
		PANEL_VALUE,		print_settings.ps.fit,
		NULL);
	print_ps_frame_visible = xv_create(panel, PANEL_CHECK_BOX,
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_LABEL_STRING,	"Frame visible",
		PANEL_VALUE,		print_settings.ps.frame_visible,
		NULL);
	print_ps_portrait = xv_create(panel, PANEL_CHECK_BOX,
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_LABEL_STRING,	"Portrait",
		PANEL_VALUE,		print_settings.ps.orientation,
		NULL);
	print_ps_margin_text = xv_create(panel, PANEL_MESSAGE,
		PANEL_LABEL_STRING,	"Margins (mm):",
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_NEXT_ROW,		-1,
		NULL);
	print_ps_margin_left = xv_create(panel, PANEL_NUMERIC_TEXT,
		PANEL_LABEL_STRING,	"Left:",
		PANEL_MIN_VALUE,	1,
		PANEL_MAX_VALUE,	100,
		PANEL_NEXT_ROW,		-1,
		PANEL_VALUE,		(int)print_settings.ps.margin_left,
		NULL);
	print_ps_margin_right = xv_create(panel, PANEL_NUMERIC_TEXT,
		PANEL_LABEL_STRING,	"Right:",
		PANEL_MIN_VALUE,	1,
		PANEL_MAX_VALUE,	100,
		PANEL_VALUE,		(int)print_settings.ps.margin_right,
		NULL);
	print_ps_margin_top = xv_create(panel, PANEL_NUMERIC_TEXT,
		PANEL_LABEL_STRING,	"Top:",
		PANEL_MIN_VALUE,	1,
		PANEL_MAX_VALUE,	100,
		PANEL_NEXT_ROW,		-1,
		PANEL_VALUE,		(int)print_settings.ps.margin_top,
		NULL);
	print_ps_margin_bottom = xv_create(panel, PANEL_NUMERIC_TEXT,
		PANEL_LABEL_STRING,	"Bottom:",
		PANEL_MIN_VALUE,	1,
		PANEL_MAX_VALUE,	100,
		PANEL_VALUE,		(int)print_settings.ps.margin_bottom,
		NULL);
	print_ps_scaling_item = xv_create(panel, PANEL_NUMERIC_TEXT,
		PANEL_LABEL_BOLD,	TRUE,
		PANEL_LABEL_STRING,	"Scaling (%):",
		PANEL_MIN_VALUE,	1,
		PANEL_MAX_VALUE,	10000,
		PANEL_NEXT_ROW,		-1,
		PANEL_VALUE,		(int)(print_settings.ps.scaling*500.0),
		NULL);
	
	window_fit_height(panel);
	
	
	switch ((Print_device)	xv_get(print_device_cycle, PANEL_VALUE)) {
	    case OUTPUT_PS :
		on_off_ps_items(TRUE);
	        break;
	    case OUTPUT_XBITMAP_FILE :
		on_off_ps_items(FALSE);
		break;
	}
}


static	void	on_off_ps_items (int yes_no)	/* PS_FELSBERG */
   		       
{
	xv_set(print_ps_scaling_item, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_scaling_tofit, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_portrait, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_frame_visible, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_margin_text, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_margin_left, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_margin_right, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_margin_top, XV_SHOW, yes_no, NULL);
	xv_set(print_ps_margin_bottom, XV_SHOW, yes_no, NULL);
}


static	void	notify_print_cycles (Panel_item item, int value, Event *event)
{
	if (item == print_device_cycle) {
		print_settings.device = (Print_device) xv_get(print_device_cycle, PANEL_VALUE);
		switch (print_settings.device) {
		    case OUTPUT_PS :
			on_off_ps_items(TRUE);
		        break;
		    case OUTPUT_XBITMAP_FILE :
			on_off_ps_items(FALSE);
			break;
		}
	}
}
