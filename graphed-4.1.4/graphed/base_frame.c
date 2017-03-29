#include "misc.h"
#include "graph.h"
#include "store.h"

#include "graphed_subwindows.h"

/* Global definitons */

void		create_base_frame                      (int *argc_ptr, char **argv);

extern		int     any_graph_has_changed    (void);
/* Local definitons */

static	Notify_value	my_base_frame_destroyer (Notify_client client, Destroy_status status);

/************************************************************************/
/*									*/
/*			VERWALTUNG DES BASE_FRAME			*/
/*									*/
/************************************************************************/
/*									*/
/*	static	void	create_base_frame (argc, argv)			*/
/*									*/
/*	Erzeugt den base_frame. Aus dem Menue werden "move" und		*/
/*	"resize" entfernt. screenwidth und screenheight werden		*/
/*	entsprechend den lokalen Gegebenheiten ermittelt.		*/
/*	Mit argc und argv koennen SunView-Optionen aus der		*/
/*	Kommandozeile uebergeben werden. Alle so gefundenen Optionen	*/
/*	werden (von xv_set) aus (argc, argv) entfernt. Der Rest		*/
/*	wird von main aus ueber dispatch_command_line_arguments		*/
/*	ausgewertet.							*/
/*									*/
/*======================================================================*/
/*									*/
/*	void	set_base_frame_label ()					*/
/*									*/
/*======================================================================*/
/*									*/
/*	static  Notify_value  my_base_frame_destroyer (client, status)	*/
/*									*/
/*	Wenn GraphEd beendet wird, so wird (ueber den SunView-Notifier)	*/
/*	diese Prozedur aufgerufen. Wenn der Graph noch nicht		*/
/*	abgespeichert wurde, so erfolgt eine Sicherheitsabfrage.	*/
/*									*/
/************************************************************************/

	
#include <images/graphed_icon.xbm>

void	create_base_frame (int *argc_ptr, char **argv)
{
	Rect	*screenrect;

	base_frame = (Frame)xv_create(XV_NULL, FRAME,
		WIN_X,			0,
		WIN_Y,			0,
		XV_SHOW,		FALSE,
		FRAME_NO_CONFIRM,	TRUE,
		FRAME_ARGC_PTR_ARGV,	argc_ptr, argv,
		WIN_ERROR_MSG,		"Could not create base frame.\n Good bye!",
		NULL);

	xv_set (base_frame,
		FRAME_ICON, xv_create (base_frame, ICON,
			ICON_IMAGE, xv_create(XV_NULL, SERVER_IMAGE,
				XV_WIDTH,            graphed_icon_width,
				XV_HEIGHT,           graphed_icon_width,
				SERVER_IMAGE_X_BITS, graphed_icon_bits,
				NULL),
			NULL),
		NULL),

	screenrect   = (Rect *)xv_get(base_frame, WIN_SCREEN_RECT);
	screenwidth  = rect_width (screenrect);
	screenheight = rect_height (screenrect);
	
	/* Wenn "quit" im Menue eingegeben wird, verzweige auch zu der	*/
	/* folgenden Prozedur :						*/

	notify_interpose_destroy_func (base_frame, my_base_frame_destroyer);
}



void	set_base_frame_label (void)
{
	char	buffer [200];

	sprintf (buffer, "GraphEd %s",
		GRAPHED_VERSION);

	xv_set(base_frame, XV_LABEL, buffer, NULL);
}



static	Notify_value	my_base_frame_destroyer (Notify_client client, Destroy_status status)
{
	Prompt	user_choice;
	char	buffer [FILENAMESIZE + 100];
	int	i;
	
	if (status == DESTROY_CHECKING) {
	    if (any_graph_has_changed() &&
		(buffer_get_filename(wac_buffer) == NULL || !strcmp(buffer_get_filename(wac_buffer),""))) {
		user_choice = notice_prompt (base_frame, NULL,	/*fisprompt*/
			NOTICE_FOCUS_XY,	screenwidth/3, screenheight/2,
			NOTICE_MESSAGE_STRINGS,	"Some windows have not been saved.",
						"Really quit ?",
					        NULL,
			NOTICE_BUTTON,		"yes",		PROMPT_ACCEPT,
			NOTICE_BUTTON,		"no",		PROMPT_REFUSE,
			NOTICE_BUTTON,		"cancel",	PROMPT_CANCEL,
			NULL);
		switch (user_choice) {
		    case PROMPT_ACCEPT :
			break;
		    case PROMPT_REFUSE :
		    case PROMPT_CANCEL :
			(void)notify_veto_destroy(client);
			return NOTIFY_DONE;
			break;
		}
	    } else if (any_graph_has_changed() && buffer_get_filename (wac_buffer) != NULL && !strcmp (buffer_get_filename (wac_buffer),"")) {
		sprintf (buffer, "The contents of the window have not been saved - store to %s ?",
		       buffer_get_filename (wac_buffer));
		user_choice = notice_prompt (base_frame, NULL,	/*fisprompt*/
			NOTICE_FOCUS_XY,	screenwidth/3, screenheight/2,
			NOTICE_MESSAGE_STRINGS,	buffer, NULL,
			NOTICE_BUTTON,		"yes",		PROMPT_ACCEPT,
			NOTICE_BUTTON,		"no",		PROMPT_REFUSE,
			NOTICE_BUTTON,		"cancel",	PROMPT_CANCEL,
			NULL);
		switch (user_choice) {
		    case PROMPT_ACCEPT :
			store_graphs (buffer_get_filename (wac_buffer));
			break;
		    case PROMPT_REFUSE :
			break;
		    case PROMPT_CANCEL :
			(void)notify_veto_destroy(client);
			return NOTIFY_DONE;
			break;
		}
	    }
	}
	
	graphed_state.shutdown = TRUE;
	for (i=N_PASTE_BUFFERS; i<N_BUFFERS; i++)
		if (buffers[i].used) {
			unuse_buffer (i);
			xv_set(canvases[i].frame, FRAME_NO_CONFIRM, TRUE, NULL);
			destroy_frame_and_canvas (i);
		}
	
	return(notify_next_destroy_func(client,status));
}
